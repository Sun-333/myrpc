package com.uestc.rpcframeworksimple.balance;

import java.util.List;

public abstract class AbstractLoadBalance implements Balance{
    @Override
    public String selectServiceAddress(List<String> service, String serviceName) {
        if(service==null||service.size()==0)
            return null;
        else if(service.size()==1)
            return service.get(0);
        else
            return doSelect(service,serviceName);
    }
    protected abstract String doSelect(List<String> service,String serviceName);
}
