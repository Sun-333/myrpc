package com.uestc.rpcframeworksimple.serialize.protostuff;

import com.uestc.rpcframeworksimple.serialize.Serializer;
import io.protostuff.LinkBuffer;
import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;

public class ProtostuffSerializer implements Serializer {
    //重复利用buffer
    private LinkedBuffer BUFFER = LinkedBuffer.allocate(LinkBuffer.DEFAULT_BUFFER_SIZE);
    @Override
    public byte[] serialize(Object object) {
        Class<?> clazz = object.getClass();
        Schema schema = RuntimeSchema.getSchema(clazz);
        byte[] bytes;
        try {
            bytes = ProtostuffIOUtil.toByteArray(object, schema, BUFFER);
        } finally {
            BUFFER.clear();
        }
        return bytes;
    }

    @Override
    public <T> T deSerialize(byte[] bytes, Class<T> tClass) {
        Schema<T> schema = RuntimeSchema.getSchema(tClass);
        T obj = schema.newMessage();
        ProtostuffIOUtil.mergeFrom(bytes, obj, schema);
        return obj;
    }
}
