package com.zhr.netty.handler;

import com.alibaba.fastjson.JSONObject;
import com.zhr.netty.handler.param.ServerRequest;
import com.zhr.netty.medium.Media;
import com.zhr.netty.util.Response;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
 
public class ServerHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
         //将从客户端收到的JSON字符串请求反序列化转换为ServerRequest类
    	 ServerRequest request = JSONObject.parseObject(msg.toString(),ServerRequest.class);
    	 
    	 Media media = Media.newInstance();
    	 Response result = media.process(request);
         ctx.channel().writeAndFlush(JSONObject.toJSONString(result));//序列化为JSON字符串格式，并且发送到缓冲区并且传入网络
         ctx.channel().writeAndFlush("\r\n");
    }
    
    @Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if(evt instanceof IdleStateEvent) {
	    	IdleStateEvent event = (IdleStateEvent)evt;
	    	if(event.state().equals(IdleState.READER_IDLE)) {
				System.out.println("读空闲");
				ctx.channel().close();
			}else if(event.state().equals(IdleState.WRITER_IDLE)){
				System.out.println("写空闲");
			}else if(event.state().equals(IdleState.ALL_IDLE)){
				System.out.println("读写空闲");
				ctx.writeAndFlush("ping\r\n");
		}
	  }
    }

}