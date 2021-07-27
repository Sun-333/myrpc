package com.uestc.rpcframeworksimple.compress;

import com.uestc.rpcframeworksimple.compress.gzip.GzipCompress;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

@Component
public class CompressFactory {
    private static Map<String, Supplier<Compress>> map = new HashMap<>();
    static {
        map.put("gzip", GzipCompress::new);
    }
    public Compress getCompress(String name){
        return map.get(name).get();
    }
}
