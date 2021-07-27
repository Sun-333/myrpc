package com.uestc.myrpcspringbootstarter.autoconfig;


import com.uestc.myrpcspringbootstarter.property.RpcClientProperties;
import com.uestc.rpcframeworksimple.balance.Balance;
import com.uestc.rpcframeworksimple.balance.loadbalanceImp.RandomLoadBalance;
import com.uestc.rpcframeworksimple.compress.Compress;
import com.uestc.rpcframeworksimple.compress.CompressFactory;
import com.uestc.rpcframeworksimple.discovery.ServiceDiscovery;
import com.uestc.rpcframeworksimple.discovery.zk.ZkServiceDiscovery;
import com.uestc.rpcframeworksimple.remoting.transport.netty.client.*;
import com.uestc.rpcframeworksimple.remoting.transport.netty.codec.RpcMessageEncoder;
import com.uestc.rpcframeworksimple.serialize.Serializer;
import com.uestc.rpcframeworksimple.serialize.SerializerFactory;
import com.uestc.rpcframeworksimple.spring.HandleRpcReferencePostProcessor;
import com.uestc.rpcframeworksimple.util.CuratorUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(RpcClientProperties.class)
@ConditionalOnProperty(
        prefix = "myrpc.client",
        name = "isopen",
        havingValue = "true"
)
public class ClientAutoConfigure {
    @Autowired
    private RpcClientProperties rpcClientProperties;
    public ClientAutoConfigure(){

    }

    @Bean
    public CompressFactory getCompressFactory(){
        return new CompressFactory();
    }
    @Bean
    public SerializerFactory serializerFactory(){
        return new SerializerFactory();
    }

    @Bean
    @ConditionalOnProperty(prefix = "myrpc.client",name = "compress")
    public Compress getCompress(CompressFactory compressFactory){
        Compress compress=null;
        if(rpcClientProperties.getCompress()!=null)
            compress =  compressFactory.getCompress(rpcClientProperties.getCompress());
        if(compress==null){
            throw new IllegalStateException("myrpc.client.compress 配置错误");
        }
        return compress;
    }
    @Bean
    @ConditionalOnProperty(prefix = "myrpc.client",name ="codec" )
    public Serializer getSerializer(SerializerFactory serializerFactory){
        Serializer serializer = null;
        if(rpcClientProperties.getCodec()!=null){
             serializer = serializerFactory.getSerializerByName(rpcClientProperties.getCodec());
        }
        if(serializer==null){
            throw new IllegalStateException("myrpc.client.serializer 配置错误");
        }
        return serializer;
    }
    @Bean
    public ChannelProvider getChannelProvider(){
        return new ChannelProvider();
    }
    @Bean
    public UnProcessRequests getUnProcessRequests(){
        return new UnProcessRequests();
    }
    @Bean
    public ClientConfigure getClientConfigure(){
        return new ClientConfigure();
    }

    @Bean
    public ServiceDiscovery getServiceDiscovery(Balance balance,CuratorUtils curatorUtils){
        return new ZkServiceDiscovery(balance,curatorUtils);
    }
    @Bean
    public NettyRpcClient getClientServer(ServiceDiscovery serviceDiscovery,
                                          ClientConfigure clientConfigure,
                                          ChannelProvider channelProvider,
                                          UnProcessRequests unProcessRequests,
                                          RpcMessageEncoder rpcMessageEncoder){
        return new NettyRpcClient(rpcMessageEncoder,serviceDiscovery,clientConfigure,channelProvider,unProcessRequests);
    }
    @Bean
    public RpcMessageEncoder rpcMessageEncoder(Serializer serializer,Compress compress){
        return new RpcMessageEncoder(serializer,compress);
    }
    @Bean
    public HandleRpcReferencePostProcessor getSpringBeanPostProcessor(RpcClient rpcClient){
        return new HandleRpcReferencePostProcessor(rpcClient);
    }
    @Bean
    public CuratorUtils getCuratorUtils(){
        return new CuratorUtils(rpcClientProperties.getZkProperties().getZkAddress());
    }
    @Bean
    public Balance getBalance(){
        return new RandomLoadBalance();
    }
}
