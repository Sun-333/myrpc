package com.uestc.rpcframeworksimple.remoting.transport.netty.codec;

import com.uestc.rpcframeworksimple.compress.Compress;
import com.uestc.rpcframeworksimple.remoting.constance.RpcConstance;
import com.uestc.rpcframeworksimple.remoting.dto.RpcMessage;
import com.uestc.rpcframeworksimple.remoting.dto.RpcRequest;
import com.uestc.rpcframeworksimple.serialize.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

import java.nio.channels.SocketChannel;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
@Slf4j
@ChannelHandler.Sharable
public class RpcMessageEncoder extends MessageToByteEncoder<RpcMessage> {
    private Serializer serializer;
    private Compress compress;
    private static final AtomicInteger ATOMIC_INTEGER = new AtomicInteger(0);
    public RpcMessageEncoder(Serializer serializer,Compress compress){
        this.serializer = serializer;
        this.compress = compress;
    }
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, RpcMessage rpcMessage, ByteBuf byteBuf) throws Exception {
        log.info(rpcMessage.toString());
        //序列化
        byte[] bytes = serializer.serialize(rpcMessage.getData());
        //压缩
        bytes = compress.compress(bytes);
        //添加魔术位（myrpc)
        byteBuf.writeBytes(RpcConstance.MAGIC_NUMBER);
        //添加版本
        byteBuf.writeByte(RpcConstance.VERSION);
        //写消息长度
        byteBuf.writeInt(bytes.length+RpcConstance.HeadLength);
        //预留整体长度
        byteBuf.writerIndex(byteBuf.writerIndex()+4);
        //消息类型
        byteBuf.writeByte(rpcMessage.getMessageType());
        //序列化方式
        byteBuf.writeByte(rpcMessage.getCodec());
        //压缩方式
        byteBuf.writeByte(rpcMessage.getCodec());
        //消息编号
        int count = ATOMIC_INTEGER.getAndIncrement();
        byteBuf.writeByte(count);
        //写数据
        if (bytes!=null&&bytes.length!=0)
            byteBuf.writeBytes(bytes);

    }
}
