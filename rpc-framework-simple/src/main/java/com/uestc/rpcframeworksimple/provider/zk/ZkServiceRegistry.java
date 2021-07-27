package com.uestc.rpcframeworksimple.provider.zk;

import com.uestc.rpcframeworksimple.provider.ServiceRegistry;
import com.uestc.rpcframeworksimple.util.CuratorUtils;
import org.apache.curator.framework.CuratorFramework;

import java.net.InetSocketAddress;

public class ZkServiceRegistry implements ServiceRegistry {
    private CuratorUtils curatorUtils;

    public ZkServiceRegistry(CuratorUtils curatorUtils) {
        this.curatorUtils = curatorUtils;
    }

    @Override
    public void registerService(String serviceName, InetSocketAddress inetSocketAddress) {
        String path = CuratorUtils.ZK_REGISTER_ROOT_PATH + "/" +serviceName+inetSocketAddress.toString();
        CuratorFramework curatorFramework = curatorUtils.getZkClient();
        curatorUtils.createPersistentNode(curatorFramework,path);
    }
}
