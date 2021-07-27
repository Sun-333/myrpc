package com.uestc.myrpcspringbootstarter.autoconfig;


import com.uestc.myrpcspringbootstarter.property.RpcServerProperties;
import com.uestc.rpcframeworksimple.compress.Compress;
import com.uestc.rpcframeworksimple.compress.CompressFactory;
import com.uestc.rpcframeworksimple.compress.gzip.GzipCompress;
import com.uestc.rpcframeworksimple.provider.ServiceProvider;
import com.uestc.rpcframeworksimple.provider.ServiceProviderImp;
import com.uestc.rpcframeworksimple.provider.zk.ZkServiceRegistry;
import com.uestc.rpcframeworksimple.remoting.handler.NettyRpcServerHandler;
import com.uestc.rpcframeworksimple.remoting.handler.PpcRequestHandler;
import com.uestc.rpcframeworksimple.remoting.handler.ServerHandler;
import com.uestc.rpcframeworksimple.remoting.transport.netty.codec.RpcMessageEncoder;
import com.uestc.rpcframeworksimple.remoting.transport.netty.server.NettyRpcServer;
import com.uestc.rpcframeworksimple.remoting.transport.netty.server.RpcServerConfigure;
import com.uestc.rpcframeworksimple.serialize.Serializer;
import com.uestc.rpcframeworksimple.serialize.SerializerFactory;
import com.uestc.rpcframeworksimple.serialize.protostuff.ProtostuffSerializer;
import com.uestc.rpcframeworksimple.spring.HandleRpcServicePostProcessor;
import com.uestc.rpcframeworksimple.util.CuratorUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(RpcServerProperties.class)
@ConditionalOnProperty(
        prefix = "myrpc.server",
        name = "isopen",
        havingValue = "true"
)
@Slf4j
public class ServerAutoConfigure {
    @Autowired
    private RpcServerProperties rpcServerProperties;

    /**
     * 配置CompressFactor 工厂
     * @return
     */
    @Bean
    @ConditionalOnMissingBean(CompressFactory.class)
    public CompressFactory getCompressFactory(){
        log.info("测试");
        return new CompressFactory();
    }

    /**
     * 配置SerializerFactory 序列化与反序列化工厂
     * @return
     */
    @Bean
    @ConditionalOnMissingBean(CompressFactory.class)
    public SerializerFactory serializerFactory(){
        return new SerializerFactory();
    }

    /**
     * 配置 ServiceProvider 当前默认使用zookeeper作为服务注册中心
     * @return
     */
    @Bean
    @ConditionalOnMissingBean(ServiceProvider.class)
    public ServiceProvider getServiceProvider(){
        return new ServiceProviderImp(new ZkServiceRegistry(new CuratorUtils(rpcServerProperties.getZkproperties().getZkAddress())),rpcServerProperties.getPort());
    }

    /**
     * rpcServer配置
     * @return
     */
    @Bean
    @ConditionalOnMissingBean(RpcServerProperties.class)
    public RpcServerConfigure getRpcServerConfigure(){
        return new RpcServerConfigure();
    }

    @Bean(initMethod = "start")
    public NettyRpcServer getNettyRpcServer(NettyRpcServerHandler nettyRpcServerHandler,RpcMessageEncoder rpcMessageEncoder){
        return new NettyRpcServer(rpcServerProperties.getPort(),nettyRpcServerHandler,rpcMessageEncoder);
    }

    @Bean
    public RpcMessageEncoder rpcMessageEncoder(){
        return new RpcMessageEncoder(new ProtostuffSerializer(),new GzipCompress());
    }


    @Bean
    public NettyRpcServerHandler getNettyRpcServerHandler(RpcServerConfigure rpcServerConfigure,ServerHandler serverHandler){
        return new NettyRpcServerHandler(rpcServerConfigure,serverHandler);
    }

    @Bean
    public ServerHandler getServerHandler(ServiceProvider serviceProvider){
        return new PpcRequestHandler(serviceProvider);
    }
    @Bean
    public HandleRpcServicePostProcessor getHandleRpcServicePostProcessor(ServiceProvider serviceProvider){
        return new HandleRpcServicePostProcessor(serviceProvider);
    }
}
