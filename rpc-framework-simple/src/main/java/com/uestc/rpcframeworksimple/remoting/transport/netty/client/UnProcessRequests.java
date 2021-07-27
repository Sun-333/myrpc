package com.uestc.rpcframeworksimple.remoting.transport.netty.client;

import com.uestc.rpcframeworksimple.remoting.dto.RpcResponse;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 管理还未收到服务器相应的请求
 * netty发送请求与接收相应在不同线程中通过
 * 通过request id建立关联
 */
public class UnProcessRequests {
    private Map<String, CompletableFuture<RpcResponse<Object>>> map;
    public UnProcessRequests(){
        map = new ConcurrentHashMap<>();
    }
    public void put(String id,CompletableFuture<RpcResponse<Object>> completableFuture){
        map.put(id,completableFuture);
    }
    public void complete(RpcResponse rpcResponse){
        if(!map.containsKey(rpcResponse.getRequestId())) return;
        CompletableFuture<RpcResponse<Object>> completableFuture = map.remove(rpcResponse.getRequestId());
        completableFuture.complete(rpcResponse);
    }
}
