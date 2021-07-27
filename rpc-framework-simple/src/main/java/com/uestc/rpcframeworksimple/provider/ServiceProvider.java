package com.uestc.rpcframeworksimple.provider;

import com.uestc.rpcframeworkcommon.entity.RpcServiceProperties;

/**
 * 发布服务、暴露本地服务handler 根据服务名称获取服务处理handler
 */
public interface ServiceProvider {
    /**
     * 添加服务处理handler
     * @param object 服务对象
     * @param tClass 服务对象实现的接口
     * @param rpcServiceProperties Rpc 相关属性
     */
    void addService(Object object, Class<?> tClass, RpcServiceProperties rpcServiceProperties);

    /**
     * 根据PRC服务属性 返回服务handler
     * @param rpcServiceProperties 服务对象相关属性
     * @return 服务对象
     */
    Object getService(RpcServiceProperties rpcServiceProperties);

    /**
     * 服务发布，并添加服务处理handler
     * @param object 服务对象
     * @param properties rpc相关属性
     */
    void publishService(Object object,RpcServiceProperties properties);

    /**
     * 服务发布
     * @param object 服务对象
     */
    void publishService(Object object);
}
