package com.zhr.client.core;

import io.netty.handler.codec.DelimiterBasedFrameDecoder;

import io.netty.handler.codec.Delimiters;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.curator.framework.CuratorFramework;
import com.alibaba.fastjson.JSONObject;
import com.zhr.client.constant.Constants;
import com.zhr.client.handler.SimpleClientHandler;
import com.zhr.client.param.Response;
import com.zhr.client.zk.ZookeeperFactory;
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
import org.apache.curator.framework.api.CuratorWatcher;

/**TcpClient 是客户端的核心类，负责与服务器建立连接并发送请求。
realServerPath 用于存储从 ZooKeeper 获取的服务器地址列表，供 send 方法使用。
在初始化时（静态代码块中），TcpClient 会从 ZooKeeper 获取初始的服务器地址列表，并填充到 realServerPath 中。
  **/
 
public class TcpClient {
		static ChannelFuture f = null;
		static Bootstrap b = new Bootstrap();
		static {
			
			EventLoopGroup workerGroup = new NioEventLoopGroup();
	        
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
	        //单例模式创建一个服务端节点，用于服务暴露
	        CuratorFramework client = ZookeeperFactory.create(); 
	        // 服务器的主机名或IP地址
	        String host = "localhost";
	        // 服务器监听的端口号
	        int port = 8080;
	        //获取client节点的子节点，用于客户端服务发现。
	        try {
				List<String> serverPaths = client.getChildren().forPath(Constants.SERVER_PATH);
				CuratorWatcher watcher = new ServerWatcher();
				//加上zk监听服务器变化
				client.getChildren().usingWatcher(watcher).forPath(Constants.SERVER_PATH);
				//遍历服务器地址列表并建立连接
				for(String serverPath:serverPaths) {
					String[] str = serverPath.split("#");
					int weight = Integer.valueOf(str[2]);
					if(weight>0) {
						for(int w=0;w<=weight;w++) {
							ChannelManager.realServerPath.add(str[0] + "#" + str[1]);
							ChannelFuture channelFuture = TcpClient.b.connect(str[0],Integer.valueOf(str[1]));
						    ChannelManager.add(channelFuture);
						}
					}
					
				    
				}
				//选择默认服务器地址
				if(ChannelManager.realServerPath.size()>0) {
					String[] hostAndPort = ChannelManager.realServerPath.toArray()[0].toString().split("#");
					host = hostAndPort[0];
					port = Integer.valueOf(hostAndPort[1]);
					}
			} catch (Exception e) {
				
				e.printStackTrace();
			}
	        
	    }
	   
	
		public static Response send(ClientRequest request) {
			f = ChannelManager.get(ChannelManager.position);
		    // 1. 通过Netty通道异步发送请求（非阻塞）
		    f.channel().writeAndFlush(JSONObject.toJSONString(request));
		    f.channel().writeAndFlush("\r\n");
		    // 2. 创建与请求关联的Future对象（用于异步转同步）
		    DefaultFuture df = new DefaultFuture(request);
		    // 3. 阻塞等待响应结果
		    return df.get(5000);
		}

}
		

			
	
	
	
