package com.uestc.rpcframeworkcommon.enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum SerializationTypeEnum {
    JSON((byte)0x01,"json"),Protuff((byte)0x02,"Protostuff"),Kryo((byte) 0x03,"Kryo");
    public byte code;
    public String name;

    public static String getByCode(byte codec) {
        for(SerializationTypeEnum serializationTypeEnum:SerializationTypeEnum.values()){
            if(serializationTypeEnum.code==codec) return serializationTypeEnum.name;
        }
        return null;
    }
}
