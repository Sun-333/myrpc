package com.uestc.exmaple.client;


import com.example.commoninterface.Hello;
import com.example.commoninterface.HelloService;
import com.uestc.rpcframeworksimple.annotation.RpcReference;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Component
public class HelloController {
    @RpcReference
    HelloService demoService;
    public void test() throws ExecutionException, InterruptedException {
        CompletableFuture<String> completableFuture  = (CompletableFuture<String>) demoService.hello(new Hello());
        System.out.println(completableFuture.get());
    }
}
