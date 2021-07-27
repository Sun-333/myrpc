package com.uestc.rpcframeworksimple.serialize;

import com.uestc.rpcframeworksimple.serialize.kyro.KryoSerializer;
import com.uestc.rpcframeworksimple.serialize.protostuff.ProtostuffSerializer;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * 创建序列工具工厂
 */
@Component
public class SerializerFactory {
    private static final Map<String, Supplier<Serializer>> map = new HashMap<>();
    static {
        map.put("Protostuff", ProtostuffSerializer::new);
        map.put("Kryo", KryoSerializer::new);
    }
    public  Serializer getSerializerByName(String name){
        return map.get(name).get();
    }
}
