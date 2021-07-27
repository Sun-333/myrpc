package com.uestc.rpcframeworksimple.spring;

import com.uestc.rpcframeworkcommon.entity.RpcServiceProperties;
import com.uestc.rpcframeworksimple.annotation.RpcService;
import com.uestc.rpcframeworksimple.provider.ServiceProvider;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * 处理抽象接口
 */
@Slf4j
public class HandleRpcServicePostProcessor implements BeanPostProcessor {
    private ServiceProvider serviceProvider;

    public HandleRpcServicePostProcessor(ServiceProvider serviceProvider) {
        this.serviceProvider = serviceProvider;
    }

    /**
     * 将RpcService 注解标记的接口发布
     * @param bean bean对象
     * @param beanName 名称
     * @return 被增强或处理后的方法
     */
    @SneakyThrows
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        if(bean.getClass().isAnnotationPresent(RpcService.class)){
            log.info(beanName);
            RpcService rpcService = bean.getClass().getAnnotation(RpcService.class);
            RpcServiceProperties rpcServiceProperties = RpcServiceProperties.builder()
                    .group(rpcService.group())
                    .version(rpcService.version())
                    .build();
            serviceProvider.publishService(bean,rpcServiceProperties);
        }
        return bean;
    }
}
