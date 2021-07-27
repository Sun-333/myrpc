package com.uestc.rpcframeworksimple.serialize.kyro;

import com.esotericsoftware.kryo.Kryo;
import com.uestc.rpcframeworksimple.serialize.Serializer;

public class KryoSerializer implements Serializer {
    @Override
    public byte[] serialize(Object object) {
        return new byte[0];
    }

    @Override
    public <T> T deSerialize(byte[] bytes, Class<T> tClass) {
        return null;
    }
}
