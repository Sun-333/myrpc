package com.uestc.rpcframeworksimple.proxy;

import com.uestc.rpcframeworkcommon.entity.RpcServiceProperties;
import com.uestc.rpcframeworksimple.remoting.dto.RpcRequest;
import com.uestc.rpcframeworksimple.remoting.dto.RpcResponse;
import com.uestc.rpcframeworksimple.remoting.transport.netty.client.RpcClient;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * 通过动态代理构建client stub
 * 当客户端调用由 @RpcReference 自动注入对象接口时会调用 invoke 方法实现消息的编码、序列化等一系列过程
 */
public class RpcStubProxy implements InvocationHandler {
    private final RpcClient rpcClient;
    private final RpcServiceProperties rpcServiceProperties;
    public RpcStubProxy(RpcClient rpcRequestTransport, RpcServiceProperties rpcServiceProperties) {
        this.rpcClient = rpcRequestTransport;
        if (rpcServiceProperties.getGroup() == null) {
            rpcServiceProperties.setGroup("");
        }
        if (rpcServiceProperties.getVersion() == null) {
            rpcServiceProperties.setVersion("");
        }
        this.rpcServiceProperties = rpcServiceProperties;
    }

    public RpcStubProxy(RpcClient rpcRequestTransport) {
        this.rpcClient = rpcRequestTransport;
        this.rpcServiceProperties = RpcServiceProperties.builder().group("").version("").build();
    }
    public <T> T getProxy(Class<?> clazz) {
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(),new Class[]{clazz},this);
    }


    @Override
    public CompletableFuture<RpcResponse<Object>> invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //构建Rpc请求
        RpcRequest rpcRequest = RpcRequest.builder()
                .methodName(method.getName())
                .interfaceName(method.getDeclaringClass().getName())
                .parameters(args)
                .paramTypes(method.getParameterTypes())
                .requestId(UUID.randomUUID().toString())
                .version(rpcServiceProperties.getVersion())
                .group(rpcServiceProperties.getGroup())
                .build();
        //通过Rpc客户端发送消息
        CompletableFuture<RpcResponse<Object>> completableFuture= rpcClient.sendRequest(rpcRequest);
        return completableFuture;
    }
}
