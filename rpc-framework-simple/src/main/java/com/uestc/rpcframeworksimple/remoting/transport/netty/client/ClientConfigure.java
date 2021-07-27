package com.uestc.rpcframeworksimple.remoting.transport.netty.client;

import com.uestc.rpcframeworkcommon.enums.CompressTypeEnum;
import com.uestc.rpcframeworkcommon.enums.SerializationTypeEnum;

/**
 * 客户端插件配置类
 */
public class ClientConfigure {
    private CompressTypeEnum compressType = CompressTypeEnum.GZIP;
    private SerializationTypeEnum serializationType = SerializationTypeEnum.Protuff;
    public byte getCode(){
        return serializationType.code;
    }
    public byte getSerializationType(){
        return serializationType.code;
    }
}
