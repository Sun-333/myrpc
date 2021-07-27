package com.uestc.rpcframeworksimple.remoting.transport.netty.codec;

import com.uestc.rpcframeworkcommon.enums.CompressTypeEnum;
import com.uestc.rpcframeworkcommon.enums.MessageTypeEnum;
import com.uestc.rpcframeworkcommon.enums.SerializationTypeEnum;
import com.uestc.rpcframeworksimple.compress.Compress;
import com.uestc.rpcframeworksimple.remoting.constance.RpcConstance;
import com.uestc.rpcframeworksimple.remoting.dto.RpcMessage;
import com.uestc.rpcframeworksimple.remoting.dto.RpcRequest;
import com.uestc.rpcframeworksimple.remoting.dto.RpcResponse;
import com.uestc.rpcframeworksimple.serialize.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Arrays;


/**
 * RPC消息解码器
 * 通过Application上下文与序列化、压缩方式选择处理器
 * <pre>
 *   0     1     2     3     4        5     6     7     8         9          10      11     12  13  14   15 16
 *   +-----+-----+-----+-----+--------+----+----+----+------+-----------+-------+----- --+-----+-----+-------+
 *   |   magic   code        |version | full length         | messageType| codec|compress|    RequestId       |
 *   +-----------------------+--------+---------------------+-----------+-----------+-----------+------------+
 *   |                                                                                                       |
 *   |                                         body                                                          |
 *   |                                                                                                       |
 *   |                                        ... ...                                                        |
 *   +-------------------------------------------------------------------------------------------------------+
 * 4B  magic code（魔法数）   1B version（版本）   4B full length（消息长度）    1B messageType（消息类型）
 * 1B compress（压缩类型） 1B codec（序列化类型）    4B  requestId（请求的Id）
 * body（object类型数据）
 * </pre>
 */

@Slf4j
@ChannelHandler.Sharable
public class RpcMessageDecoder extends LengthFieldBasedFrameDecoder implements ApplicationContextAware {
    ApplicationContext applicationContext;

    public RpcMessageDecoder(){
        this(RpcConstance.MAX_FRAME_LENGTH,3,4,-9,0);
    }
    public RpcMessageDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength, int lengthAdjustment, int initialBytesToStrip) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        //通过父类方法拆分出完整数据包
        Object byteBuf = super.decode(ctx,in);
        if(byteBuf instanceof ByteBuf){
            ByteBuf frame = (ByteBuf) byteBuf;
            if(frame.readableBytes()>=RpcConstance.HeadLength){
                try {
                    return decodeFrame(in);
                }catch (Exception e){
                    log.info("解析RPC数据出错[{}]",e);
                }finally {
                    frame.release();
                }
            }
        }
        return byteBuf;
    }
    @SneakyThrows
    private RpcMessage decodeFrame(ByteBuf in)  {
        //读取4位判定魔术位是否是RPC协议
        int len = RpcConstance.MAGIC_NUMBER.length;
        byte[] tmp = new byte[len];
        in.readBytes(tmp);
        for (int i = 0; i < len; i++) {
            if (tmp[i] != RpcConstance.MAGIC_NUMBER[i]) {
                throw new IllegalArgumentException("Unknown magic code: " + Arrays.toString(tmp));
            }
        }
        //读取Rpc协议版本与数据压缩方式
        byte version = in.readByte();
        if (version != RpcConstance.VERSION) {
            throw new RuntimeException("version isn't compatible" + version);
        }
        int fullLength = in.readInt();
        // 构建RPC消息
        byte messageType = in.readByte();
        byte codecType = in.readByte();
        byte compressType = in.readByte();
        int requestId = in.readInt();
        RpcMessage rpcMessage = RpcMessage.builder()
                .codec(codecType)
                .requestId(requestId)
                .messageType(messageType).build();
        if (messageType == RpcConstance.HEARTBEAT_REQUEST_TYPE) {
            rpcMessage.setData(RpcConstance.PING);
        } else if (messageType == RpcConstance.HEARTBEAT_RESPONSE_TYPE) {
            rpcMessage.setData(RpcConstance.PONG);
        } else {
            int bodyLength = fullLength - RpcConstance.HEAD_LENGTH;
            if (bodyLength > 0) {
                byte[] bs = new byte[bodyLength];
                in.readBytes(bs);
                //数据解压
                String compressName = CompressTypeEnum.getByName(compressType);
                Compress compress = (Compress) applicationContext.getBean(compressName);
                bs = compress.deCompress(bs);
                String codecName = SerializationTypeEnum.getByCode(rpcMessage.getCodec());
                log.info("codec name: [{}] ", codecName);
                //反序列化
                Serializer serializer = (Serializer) applicationContext.getBean(codecName);
                if (messageType == MessageTypeEnum.Request.code) {
                    RpcRequest tmpValue = serializer.deSerialize(bs, RpcRequest.class);
                    rpcMessage.setData(tmpValue);
                } else {
                    RpcResponse tmpValue = serializer.deSerialize(bs, RpcResponse.class);
                    rpcMessage.setData(tmpValue);
                }
            }
        }
        return rpcMessage;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext){
        this.applicationContext = applicationContext;
    }
}
