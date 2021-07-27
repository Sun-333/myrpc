package com.uestc.rpcframeworksimple.discovery.zk;

import com.uestc.rpcframeworksimple.balance.Balance;
import com.uestc.rpcframeworksimple.balance.loadbalanceImp.RandomLoadBalance;
import com.uestc.rpcframeworksimple.discovery.ServiceDiscovery;
import com.uestc.rpcframeworksimple.util.CuratorUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;

import java.net.InetSocketAddress;
import java.util.List;
@Slf4j
public class ZkServiceDiscovery implements ServiceDiscovery {
    //默认使用随机算法
    private Balance balance= new RandomLoadBalance();
    private CuratorUtils curatorUtils;
    public ZkServiceDiscovery(Balance balance,CuratorUtils curatorUtils){
        this.balance = balance;
        this.curatorUtils = curatorUtils;
    }
    @Override
    public InetSocketAddress lookupService(String serviceName) {
        System.out.println(serviceName);
        CuratorFramework curatorFramework = curatorUtils.getZkClient();
        System.out.println(curatorUtils.getChildrenNodes(curatorFramework, ""));
        List<String> serviceAddress = curatorUtils.getChildrenNodes(curatorFramework,serviceName);
        if(serviceAddress==null||serviceAddress.size()==0)
            throw new RuntimeException("服务不存在");
        String targetAddress = balance.selectServiceAddress(serviceAddress,serviceName);
        log.info("成功找到服务[{}]",targetAddress);
        String[] str = targetAddress.split(":");
        return new InetSocketAddress(str[0],Integer.valueOf(str[1]));
    }
}
