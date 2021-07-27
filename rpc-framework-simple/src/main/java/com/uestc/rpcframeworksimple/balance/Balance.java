package com.uestc.rpcframeworksimple.balance;

import java.util.List;

/**
 * 负载均衡接口
 */
public interface Balance {
    /**
     * 通过服务名进行负载均衡选举
     * @param serviceName 服务名称
     * @return
     */
    String selectServiceAddress(List<String> service, String serviceName);
}
