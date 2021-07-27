package com.uestc.rpcframeworksimple.remoting.constance;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class RpcConstance {
    /**
     * 魔术位验证 rpc消息
     */
    public static final byte[] MAGIC_NUMBER = {(byte) 'm',(byte)'y', (byte) 'r', (byte) 'p', (byte) 'c'};
    public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    //version information
    public static final byte VERSION = 1;
    public static final int HeadLength=16;
    public static final byte HEARTBEAT_REQUEST_TYPE = 3;
    //pong
    public static final byte HEARTBEAT_RESPONSE_TYPE = 4;
    public static final int HEAD_LENGTH = 16;
    public static final String PING = "ping";
    public static final String PONG = "pong";
    public static final int MAX_FRAME_LENGTH = 2* 1024 *1024;
}
