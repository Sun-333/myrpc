package com.example.exampleserver.service;

import com.example.commoninterface.Hello;
import com.example.commoninterface.HelloService;
import com.uestc.rpcframeworksimple.annotation.RpcService;
@RpcService
public class HelloServiceImp implements HelloService {
    @Override
    public Object hello(Hello hello) {
        return "hello";
    }
}
