package com.uestc.rpcframeworksimple.remoting.handler;

import com.uestc.rpcframeworkcommon.enums.MessageTypeEnum;
import com.uestc.rpcframeworksimple.remoting.constance.RpcConstance;
import com.uestc.rpcframeworksimple.remoting.dto.RpcMessage;
import com.uestc.rpcframeworksimple.remoting.dto.RpcResponse;
import com.uestc.rpcframeworksimple.remoting.transport.netty.client.UnProcessRequests;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ChannelHandler.Sharable
public class HettyRpcClientHandler extends ChannelInboundHandlerAdapter {
    public HettyRpcClientHandler(UnProcessRequests unProcessRequests) {
        this.unProcessRequests = unProcessRequests;
    }

    private UnProcessRequests unProcessRequests;
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        log.info("收到消息[{}]",msg);
        try {
            if(msg instanceof RpcMessage){
                RpcMessage rpcMessage = (RpcMessage) msg;
                if(rpcMessage.getMessageType()== RpcConstance.HEARTBEAT_RESPONSE_TYPE){
                    log.info("收到心跳回应包[{}]",rpcMessage);
                }
                if(rpcMessage.getMessageType()==MessageTypeEnum.Response.code){
                    RpcResponse rpcResponse = (RpcResponse) rpcMessage.getData();
                    unProcessRequests.complete(rpcResponse);
                }
            }
        }finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {

        super.userEventTriggered(ctx, evt);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("client catch exception：", cause);
        cause.printStackTrace();
        ctx.close();
    }
}
