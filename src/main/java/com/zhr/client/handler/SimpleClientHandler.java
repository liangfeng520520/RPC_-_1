package com.zhr.client.handler;

import com.alibaba.fastjson.JSONObject;
import com.zhr.client.core.DefaultFuture;
import com.zhr.client.param.Response;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.util.AttributeKey;

// 定义一个自定义的客户端处理器，继承自 ChannelInboundHandlerAdapter
public class SimpleClientHandler extends ChannelInboundHandlerAdapter {

    // 重写 channelRead 方法，用于处理从服务器接收到的消息
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    	if("ping\r\n".equals(msg.toString())) {
    	    ctx.channel().writeAndFlush("pong\r\n");
    	    return;
    	}
    	//String data = (String) msg;
    	//System.out.println("Received data: " + data);
        //将接收到的 JSON 字符串反序列化为 Response 对象
        Response response = JSONObject.parseObject(msg.toString(),Response.class);
        System.out.println("接收服务器返回数据："+ JSONObject.toJSONString(response));
        //IO线程调用receive方法通知挂住的主线程。
        DefaultFuture.receive(response);
        
        // 关闭当前通道
        //ctx.channel().close();
    }

    // 重写 exceptionCaught 方法，用于处理在处理过程中发生的异常
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // 调用父类的 exceptionCaught 方法以处理异常
        cause.printStackTrace();
        ctx.close();
    }
}