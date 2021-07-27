package com.uestc.rpcframeworksimple.remoting.dto;

import com.uestc.rpcframeworkcommon.entity.RpcServiceProperties;
import lombok.*;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
@Builder
public class RpcRequest implements Serializable {
    private static final long serialVersionUID = 1905122041950251207L;
    //请求消息ID
    private String requestId;
    //请求接口名称
    private String interfaceName;
    //请求方法
    private String methodName;
    //请求参数
    private Object[] parameters;
    //参数类型
    private Class<?>[] paramTypes;
    //接口版本
    private String version;
    //接口群组
    private String group;

    public RpcServiceProperties toRpcProperties() {
        return RpcServiceProperties.builder().
                serviceName(this.getInterfaceName()).
                version(this.version).
                group(this.getGroup()).build();
    }
}
