package com.zhr.netty.client;

import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;

import com.alibaba.fastjson.JSONObject;
import com.zhr.netty.handler.SimpleClientHandler;
import com.zhr.netty.util.Response;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.AttributeKey;

public class TcpClient {
    
		static ChannelFuture f = null;
		static {
			
			 // 服务器的主机名或IP地址
	        String host = "localhost";
	        // 服务器监听的端口号
	        int port = 8080;
			EventLoopGroup workerGroup = new NioEventLoopGroup();
	        try {
	        Bootstrap b = new Bootstrap();
	        // 设置EventLoopGroup
	        b.group(workerGroup);
	        
	        // 指定使用的Channel类型，这里是NIO Socket Channel
	        b.channel(NioSocketChannel.class);
	        
	        // 设置Channel选项，这里启用了SO_KEEPALIVE选项以保持连接活跃
	        b.option(ChannelOption.SO_KEEPALIVE, true);
	        
	        // 设置Channel处理器
	        b.handler(new ChannelInitializer<SocketChannel>() {
	            @Override
	            public void initChannel(SocketChannel ch) throws Exception {
	                // 添加一个基于分隔符的帧解码器，用于处理以换行符分隔的消息
	                ch.pipeline().addLast(new DelimiterBasedFrameDecoder(Integer.MAX_VALUE, Delimiters.lineDelimiter()[0]));
	                
	                // 添加一个字符串解码器，用于将字节流解码为字符串
	                ch.pipeline().addLast(new StringDecoder());
	                
	                // 添加自定义的处理器，用于处理接收到的消息
	                ch.pipeline().addLast(new SimpleClientHandler());
	                
	                // 添加一个字符串编码器，用于将字符串编码为字节流
	                ch.pipeline().addLast(new StringEncoder());
	            }
	        });
	        
	         // 连接到服务器并等待连接完成
	        f = b.connect(host, port).sync();
			
		}catch(InterruptedException e) {
			e.printStackTrace();
			
		  }
	    }
	    
		public static Response send(ClientRequest request) {
		    // 1. 通过Netty通道异步发送请求（非阻塞）
		    f.channel().writeAndFlush(JSONObject.toJSONString(request));
		    f.channel().writeAndFlush("\r\n");
		    // 2. 创建与请求关联的Future对象（用于异步转同步）
		    DefaultFuture df = new DefaultFuture(request);
		    // 3. 阻塞等待响应结果
		    return df.get();
		}

}
		

			
	
	
	
