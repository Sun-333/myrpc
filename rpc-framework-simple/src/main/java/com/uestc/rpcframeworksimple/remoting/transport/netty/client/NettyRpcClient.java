package com.uestc.rpcframeworksimple.remoting.transport.netty.client;

import com.uestc.rpcframeworkcommon.enums.MessageTypeEnum;
import com.uestc.rpcframeworksimple.discovery.ServiceDiscovery;
import com.uestc.rpcframeworksimple.remoting.dto.RpcMessage;
import com.uestc.rpcframeworksimple.remoting.dto.RpcRequest;
import com.uestc.rpcframeworksimple.remoting.dto.RpcResponse;
import com.uestc.rpcframeworksimple.remoting.handler.HettyRpcClientHandler;
import com.uestc.rpcframeworksimple.remoting.transport.netty.codec.RpcMessageEncoder;
import com.uestc.rpcframeworksimple.remoting.transport.netty.codec.RpcMessageDecoder;
import com.uestc.rpcframeworksimple.serialize.Serializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
public class NettyRpcClient implements RpcClient {
    private ChannelProvider channelProvider;
    private UnProcessRequests unProcessRequests;
    private Bootstrap bootstrap;
    private ServiceDiscovery serviceDiscovery;
    private ClientConfigure clientConfigure;
    private final EventLoopGroup eventLoopGroup;
    private RpcMessageEncoder rpcMessageEncoder;
    public NettyRpcClient(RpcMessageEncoder rpcMessageEncoder,ServiceDiscovery serviceDiscovery,ClientConfigure clientConfigure,ChannelProvider channelProvider,UnProcessRequests unProcessRequests){
        Bootstrap bootstrap  = new Bootstrap();
        eventLoopGroup = new NioEventLoopGroup();
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline channelPipeline = socketChannel.pipeline();
                        channelPipeline.addLast(new IdleStateHandler(0, 5, 0, TimeUnit.SECONDS));
                        channelPipeline.addLast(rpcMessageEncoder);
                        channelPipeline.addLast(new RpcMessageDecoder());
                        channelPipeline.addLast(new HettyRpcClientHandler(unProcessRequests));
                    }
                });
        this.bootstrap = bootstrap;
        this.serviceDiscovery = serviceDiscovery;
        this.clientConfigure = clientConfigure;
        this.channelProvider = channelProvider;
        this.unProcessRequests = unProcessRequests;
        this.rpcMessageEncoder = rpcMessageEncoder;
    }

    @SneakyThrows
    @Override
    public Channel doConnection(InetSocketAddress inetSocketAddress) {
        CompletableFuture<Channel> completableFuture = new CompletableFuture<>();
        bootstrap.connect(inetSocketAddress).addListener((ChannelFutureListener) future -> {
            if(future.isSuccess()){
                log.info("连接成功[{}]",inetSocketAddress.toString());
                completableFuture.complete(future.channel());
            }else{
                throw new  IllegalStateException();
            }
        });
        return completableFuture.get();
    }

    @Override
    public CompletableFuture<RpcResponse<Object>> sendRequest(RpcRequest rpcRequest) {
        System.out.println(rpcRequest.toString());
        CompletableFuture<RpcResponse<Object>> completableFuture = new CompletableFuture<>();
        //服务发现
        InetSocketAddress address = serviceDiscovery.lookupService(rpcRequest.toRpcProperties().getServiceName());
        log.info(address.toString());
        Channel channel  = getChannel(address);
        if(channel.isActive()){
            unProcessRequests.put(rpcRequest.getRequestId(),completableFuture);
            RpcMessage rpcMessage = new RpcMessage();
            rpcMessage.setMessageType(MessageTypeEnum.Request.code);
            rpcMessage.setCodec(clientConfigure.getSerializationType());
            rpcMessage.setCompress(clientConfigure.getCode());
            rpcMessage.setData(rpcRequest);
            channel.writeAndFlush(rpcMessage).addListener((ChannelFutureListener)future ->{
                if(future.isSuccess()){
                    log.info("客户端发送数据[{}]",rpcMessage);
                }else{
                    future.channel().close();
                    completableFuture.completeExceptionally(future.cause());
                    log.info("客户端发送数据失败");
                }
            });
        }else{
            throw new IllegalStateException();
        }
        return completableFuture;
    }

    @Override
    public Channel getChannel(InetSocketAddress inetSocketAddress) {
        Channel channel = channelProvider.getChannel(inetSocketAddress);
        if(channel==null){
            channel= doConnection(inetSocketAddress);
            channelProvider.set(inetSocketAddress,channel);
        }
        return channel;
    }
}
