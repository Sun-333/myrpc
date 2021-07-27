package com.uestc.rpcframeworksimple.discovery;

import java.net.InetSocketAddress;

public interface ServiceDiscovery {
    /**
     * 根据rpc服务的名称找到对应地址
     * @param requestType
     * @return
     */
    public InetSocketAddress lookupService(String requestType);
}
