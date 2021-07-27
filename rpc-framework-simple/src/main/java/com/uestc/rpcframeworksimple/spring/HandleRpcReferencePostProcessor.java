package com.uestc.rpcframeworksimple.spring;

import com.uestc.rpcframeworkcommon.entity.RpcServiceProperties;
import com.uestc.rpcframeworksimple.annotation.RpcReference;
import com.uestc.rpcframeworksimple.annotation.RpcService;
import com.uestc.rpcframeworksimple.provider.ServiceProvider;
import com.uestc.rpcframeworksimple.proxy.RpcStubProxy;
import com.uestc.rpcframeworksimple.remoting.transport.netty.client.RpcClient;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

/**
 * 通过BeanPostProcessor 在bean对象的创建过程中将 @RpcService注解标记的接口发布 将@RpcReference标记的接口通过动态代理进行包装
 */
@Slf4j
public class HandleRpcReferencePostProcessor implements BeanPostProcessor {
    private  RpcClient rpcClient;
    public HandleRpcReferencePostProcessor(RpcClient rpcClient) {
        this.rpcClient = rpcClient;
    }
    /**
     * 将 @RpcReference 标记的属性进行代理 实现rpc client stub
     * @param bean bean对象
     * @param beanName bean 名称
     * @return
     */
    @SneakyThrows
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        log.info(beanName);
        for(Field field : bean.getClass().getDeclaredFields()){
            if(field.isAnnotationPresent(RpcReference.class)){
                RpcReference rpcReference = field.getAnnotation(RpcReference.class);
                RpcServiceProperties rpcServiceProperties = RpcServiceProperties.builder()
                        .group(rpcReference.group())
                        .version(rpcReference.version())
                        .build();
                RpcStubProxy proxy = new RpcStubProxy(rpcClient,rpcServiceProperties);
                Object object = proxy.getProxy(field.getType());
                field.setAccessible(true);
                field.set(bean,object);
            }
        }
        return bean;
    }
}
