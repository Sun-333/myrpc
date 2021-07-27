package com.uestc.rpcframeworksimple.remoting.handler;

import com.uestc.rpcframeworkcommon.enums.MessageTypeEnum;
import com.uestc.rpcframeworkcommon.enums.RpcResponseCodeEnum;
import com.uestc.rpcframeworksimple.remoting.constance.RpcConstance;
import com.uestc.rpcframeworksimple.remoting.dto.RpcMessage;
import com.uestc.rpcframeworksimple.remoting.dto.RpcRequest;
import com.uestc.rpcframeworksimple.remoting.dto.RpcResponse;
import com.uestc.rpcframeworksimple.remoting.handler.ServerHandler;
import com.uestc.rpcframeworksimple.remoting.transport.netty.server.RpcServerConfigure;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ChannelHandler.Sharable
public class NettyRpcServerHandler extends ChannelInboundHandlerAdapter {
    private RpcServerConfigure rpcServerConfigure;
    private ServerHandler rpcServiceHandler;

    public NettyRpcServerHandler(RpcServerConfigure rpcServerConfigure, ServerHandler rpcServiceHandler) {
        this.rpcServerConfigure = rpcServerConfigure;
        this.rpcServiceHandler = rpcServiceHandler;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            if(msg instanceof RpcMessage){
                log.info("服务器接收到Rpc请求: [{}] ", msg);
                byte messageType = ((RpcMessage) msg).getMessageType();
                RpcMessage rpcMessage = new RpcMessage();
                rpcMessage.setCodec(rpcServerConfigure.getCodec());
                rpcMessage.setCompress(rpcServerConfigure.getCompress());
                if (messageType == RpcConstance.HEARTBEAT_REQUEST_TYPE) {
                    rpcMessage.setMessageType(RpcConstance.HEARTBEAT_RESPONSE_TYPE);
                    rpcMessage.setData(RpcConstance.PONG);
                } else {
                    RpcRequest rpcRequest = (RpcRequest) ((RpcMessage) msg).getData();
                    //执行目标方法（客户端请求接口处理方法）并返回处理结果
                    Object result = rpcServiceHandler.handle(rpcRequest);
                    log.info(String.format("服务器处理结果: %s", result.toString()));
                    rpcMessage.setMessageType(MessageTypeEnum.Response.code);
                    if (ctx.channel().isActive() && ctx.channel().isWritable()) {
                        RpcResponse<Object> rpcResponse = RpcResponse.success(result, rpcRequest.getRequestId());
                        rpcMessage.setData(rpcResponse);
                    } else {
                        RpcResponse<Object> rpcResponse = RpcResponse.fail(RpcResponseCodeEnum.FAIL);
                        rpcMessage.setData(rpcResponse);
                        log.error("not writable now, message dropped");
                    }
                }
                ctx.writeAndFlush(rpcMessage).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
            }
        }finally {
            ReferenceCountUtil.release(msg);
        }
    }
}
