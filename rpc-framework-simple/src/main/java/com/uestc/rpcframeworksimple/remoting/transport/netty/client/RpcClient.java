package com.uestc.rpcframeworksimple.remoting.transport.netty.client;

import com.uestc.rpcframeworksimple.remoting.dto.RpcRequest;
import com.uestc.rpcframeworksimple.remoting.dto.RpcResponse;
import io.netty.channel.Channel;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * RPC客户端客户端接口
 */
public interface RpcClient {
    /**
     * 根据ip地址创建socket 连接
     * @param inetSocketAddress
     * @return
     */
    public Channel doConnection(InetSocketAddress inetSocketAddress) throws ExecutionException, InterruptedException;

    /**
     * 发送请求
     * @param rpcRequest
     * @return
     */
    public CompletableFuture<RpcResponse<Object>> sendRequest(RpcRequest rpcRequest);

    /**
     * 根据SocketAddress获取channel
     * @param inetSocketAddress
     * @return
     */
    public Channel getChannel(InetSocketAddress inetSocketAddress) throws ExecutionException, InterruptedException;
}
