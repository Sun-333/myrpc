package com.uestc.rpcframeworkcommon.enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum MessageTypeEnum {
    Request((byte)0x01),Response((byte) 0x02);
    public byte code;
}
