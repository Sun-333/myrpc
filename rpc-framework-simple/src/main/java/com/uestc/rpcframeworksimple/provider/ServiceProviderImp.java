package com.uestc.rpcframeworksimple.provider;

import com.uestc.rpcframeworkcommon.entity.RpcServiceProperties;
import com.uestc.rpcframeworksimple.remoting.transport.netty.server.NettyRpcServer;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class ServiceProviderImp implements ServiceProvider{
    private final Map<String, Object> serviceMap;
    //通过依赖注入 方便实现插件式服务注册中心
    private final Set<String> registeredService;
    private final ServiceRegistry serviceRegistry;
    private int port;
    public ServiceProviderImp(ServiceRegistry serviceRegistry,int port) {
        this.serviceMap = new ConcurrentHashMap();
        this.registeredService = ConcurrentHashMap.newKeySet();
        this.serviceRegistry = serviceRegistry;
        this.port = port;
    }

    @Override
    public void addService(Object object, Class<?> tClass, RpcServiceProperties rpcServiceProperties) {
        String rpcServiceName = rpcServiceProperties.toRpcServiceName();
        if (registeredService.contains(rpcServiceName)) {
            return;
        }
        registeredService.add(rpcServiceName);
        serviceMap.put(rpcServiceName, object);
        log.info("Add service: {} and interfaces:{}", rpcServiceName, object.getClass().getInterfaces());
    }

    @Override
    public Object getService(RpcServiceProperties rpcServiceProperties) {
        Object service = serviceMap.get(rpcServiceProperties.toRpcServiceName());
        if (null == service) {
            throw new RuntimeException("本地未有此服务");
        }
        return service;
    }

    @Override
    public void publishService(Object object, RpcServiceProperties properties) {
        try {
            String host = InetAddress.getLocalHost().getHostAddress();
            Class<?> serviceRelatedInterface = object.getClass().getInterfaces()[0];
            String serviceName = serviceRelatedInterface.getCanonicalName();
            properties.setServiceName(serviceName);
            this.addService(object, serviceRelatedInterface, properties);
            serviceRegistry.registerService(properties.toRpcServiceName(), new InetSocketAddress(host, port));
        } catch (UnknownHostException e) {
            log.error("occur exception when getHostAddress", e);
        }
    }

    @Override
    public void publishService(Object object) {
        this.publishService(object, RpcServiceProperties.builder().group("").version("").build());
    }
}
