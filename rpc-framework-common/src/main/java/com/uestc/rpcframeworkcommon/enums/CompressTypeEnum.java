package com.uestc.rpcframeworkcommon.enums;

import lombok.AllArgsConstructor;

/**
 * 数据表示方式
 */
@AllArgsConstructor
public enum CompressTypeEnum {
    GZIP((byte) 0x01, "gzip");
    private final byte code;
    private final String name;

    public static String getByName(byte name) {
        for(CompressTypeEnum compressTypeEnum:CompressTypeEnum.values()){
            if(compressTypeEnum.code==name) return compressTypeEnum.name;
        }
        return null;
    }
}
