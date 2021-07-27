package com.uestc.myrpcspringbootstarter.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "myrpc.client")
public class RpcClientProperties {
    private String codec;
    private String compress;
    private ZkProperties zkProperties;
    @Data
    public static class ZkProperties{
        private String zkAddress;
    }
}
