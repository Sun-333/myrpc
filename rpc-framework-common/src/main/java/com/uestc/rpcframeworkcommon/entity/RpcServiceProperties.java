package com.uestc.rpcframeworkcommon.entity;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class RpcServiceProperties {
    /**
     * server version
     */
    private String version;
    /**
     * when the interface has multiple implementation classes,distinguish by group
     */
    private String group;
    private String serviceName;
    public String toRpcServiceName() {
        return this.getServiceName() + this.getGroup() + this.getVersion();
    }
}
