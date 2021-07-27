package com.uestc.rpcframeworksimple.balance.loadbalanceImp;

import com.uestc.rpcframeworksimple.balance.AbstractLoadBalance;

import java.util.List;
import java.util.Random;

public class RandomLoadBalance extends AbstractLoadBalance {
    @Override
    protected String doSelect(List<String> service, String serviceName) {
        Random random = new Random();
        return service.get(random.nextInt(service.size()));
    }
}
