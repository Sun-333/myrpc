package com.uestc.rpcframeworksimple.remoting.handler;

import com.uestc.rpcframeworksimple.provider.ServiceProvider;
import com.uestc.rpcframeworksimple.remoting.dto.RpcRequest;
import io.netty.channel.ChannelHandler;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Slf4j
public class PpcRequestHandler implements ServerHandler{
    private ServiceProvider serviceProvider;

    public PpcRequestHandler(ServiceProvider serviceProvider) {
        this.serviceProvider = serviceProvider;
    }

    @Override
    public Object handle(RpcRequest rpcRequest) {
        Object handler = serviceProvider.getService(rpcRequest.toRpcProperties());
        return invokeTargetMethod(rpcRequest,handler);
    }

    /**
     * 通过反射调用Rpc请求对应Method
     * @param rpcRequest rpc请求
     * @param service 服务
     * @return
     */
    private Object invokeTargetMethod(RpcRequest rpcRequest, Object service) {
        Object result;
        try {
            Method method = service.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getParamTypes());
            result = method.invoke(service, rpcRequest.getParameters());
            log.info("服务端成功调用接口:[{}] 成功调用方法:[{}]", rpcRequest.getInterfaceName(), rpcRequest.getMethodName());
        } catch (NoSuchMethodException | IllegalArgumentException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        return result;
    }
}
