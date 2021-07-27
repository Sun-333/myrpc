package com.uestc.rpcframeworksimple.remoting.transport.netty.client;

import io.netty.channel.Channel;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 管理socket连接
 */
public class ChannelProvider {
    private Map<String, Channel> map;

    public ChannelProvider() {
        this.map = new ConcurrentHashMap<>();
    }

    /**
     * 获取socket连接
     * InetSocketAddress 对应socket如果存在并且处于活跃状态则返回，反之返回null
     * @param socketAddress socket连接地址
     * @return 对应channel
     */
    public Channel getChannel(InetSocketAddress socketAddress){
        String key  = socketAddress.toString();
        if(map.containsKey(key)){
            Channel channel = map.get(key);
            if(channel!=null&&channel.isActive())
                return channel;
            else
                map.remove(key);
        }
        return null;
    }

    /**
     * 添加channel
     * @param inetSocketAddress socketAddress
     * @param channel channel
     */
    public void set(InetSocketAddress inetSocketAddress,Channel channel){
        String key = inetSocketAddress.toString();
        map.put(key,channel);
    }

    /**
     * 删除socketAddress对应 channel
     * @param inetSocketAddress socketAddress
     * @return
     */
    public Channel removeChannel(InetSocketAddress inetSocketAddress){
        String key = inetSocketAddress.toString();
        return map.remove(key);
    }
}
