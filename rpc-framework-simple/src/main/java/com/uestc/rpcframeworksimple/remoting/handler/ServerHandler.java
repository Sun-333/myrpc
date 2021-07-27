package com.uestc.rpcframeworksimple.remoting.handler;

import com.uestc.rpcframeworksimple.remoting.dto.RpcRequest;

public interface ServerHandler {
    Object handle(RpcRequest rpcRequest);
}
