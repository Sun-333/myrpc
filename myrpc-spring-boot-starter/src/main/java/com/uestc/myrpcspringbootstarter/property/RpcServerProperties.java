package com.uestc.myrpcspringbootstarter.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;

@Data
@ConfigurationProperties(prefix = "myrpc.server")
public class RpcServerProperties {
    private int port;
    private ZkProperties zkproperties;
    private String balance;
    @Data
    public static class ZkProperties{
        private String zkAddress;
    }
}
