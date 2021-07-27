package com.uestc.rpcframeworksimple.compress;

public interface Compress {
    byte[] compress(byte[] bytes);
    byte[] deCompress(byte[] bytes);
}
