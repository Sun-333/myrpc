package com.uestc.rpcframeworksimple.remoting.transport.netty.server;

import com.uestc.rpcframeworkcommon.threadpool.ThreadPoolFactoryUtils;
import com.uestc.rpcframeworksimple.remoting.handler.NettyRpcServerHandler;
import com.uestc.rpcframeworksimple.remoting.transport.netty.codec.RpcMessageDecoder;
import com.uestc.rpcframeworksimple.remoting.transport.netty.codec.RpcMessageEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.util.concurrent.TimeUnit;
@Slf4j
public class NettyRpcServer {
    private int port =100;
    private NettyRpcServerHandler nettyRpcServerHandler;
    private RpcMessageEncoder rpcMessageEncoder;
    public NettyRpcServer(int port,NettyRpcServerHandler serverHandler,RpcMessageEncoder encoder){
        this.port = port;
        this.nettyRpcServerHandler = serverHandler;
        this.rpcMessageEncoder = encoder;
    }
    @SneakyThrows
    public void start(){
        log.info("服务端启动监听");
        String host = InetAddress.getLocalHost().getHostAddress();
        //连接时间处理为单线程
        NioEventLoopGroup boss = new NioEventLoopGroup(1);
        //读写、异常处理时间为多线程
        NioEventLoopGroup worker = new NioEventLoopGroup();
        //请求执行线程池，将IO的读写与处理分开，防止阻塞
        DefaultEventLoopGroup serviceHandlerGroup = new DefaultEventLoopGroup(
                Runtime.getRuntime().availableProcessors() * 2,
                ThreadPoolFactoryUtils.createThreadFactory("rpcServiceHandlerThread",false));
        ServerBootstrap bootstrap = new ServerBootstrap();
        try {
            bootstrap.group(boss,worker)
                    .channel(NioServerSocketChannel.class)
                    // TCP默认开启了 Nagle 算法，该算法的作用是尽可能的发送大数据快，减少网络传输。TCP_NODELAY 参数的作用就是控制是否启用 Nagle 算法。
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    // 是否开启 TCP 底层心跳机制
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    //表示系统用于临时存放已完成三次握手的请求的队列的最大长度,如果连接建立频繁，服务器处理创建新连接较慢，可以适当调大这个参数
                    .option(ChannelOption.SO_BACKLOG, 128)
                    // 当客户端第一次进行请求的时候才会进行初始化
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            // 30 秒之内没有收到客户端请求的话就关闭连接
                            ChannelPipeline p = ch.pipeline();
                            p.addLast(rpcMessageEncoder);
                            p.addLast(new RpcMessageDecoder());
                            p.addLast(serviceHandlerGroup, nettyRpcServerHandler);
                        }
                    });
            ChannelFuture f = bootstrap.bind(host, port).sync();
            // 等待服务端监听端口关闭
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("启动netty服务时抛出异常:", e);
        } finally {
            log.error("关闭线程组");
            boss.shutdownGracefully();
            worker.shutdownGracefully();
            serviceHandlerGroup.shutdownGracefully();
        }
    }
}
