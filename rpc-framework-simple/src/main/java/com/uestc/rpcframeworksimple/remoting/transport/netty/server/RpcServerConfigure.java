package com.uestc.rpcframeworksimple.remoting.transport.netty.server;

import lombok.Getter;

@Getter
public class RpcServerConfigure {
    private  byte compress;
    private  byte codec;
}
