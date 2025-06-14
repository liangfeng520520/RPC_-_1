package com.zhr.netty.init;


import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.springframework.context.*;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import com.zhr.netty.Factory.ZookeeperFactory;
import com.zhr.netty.constant.Constants;
import com.zhr.netty.handler.ServerHandler;
import com.zhr.netty.handler.SimpleServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;

@Component
public class NettyInitial implements ApplicationListener<ContextRefreshedEvent> {
	public void start() throws UnknownHostException{
        // 创建两个 EventLoopGroup
        EventLoopGroup parentGroup = new NioEventLoopGroup(); // 用于接受客户端连接
        EventLoopGroup childGroup = new NioEventLoopGroup();  // 用于处理客户端连接
        
        try {
            // 创建 ServerBootstrap 实例
            ServerBootstrap bootstrap = new ServerBootstrap();
            
            // 配置 ServerBootstrap
            bootstrap.group(parentGroup, childGroup)
                     .option(ChannelOption.SO_BACKLOG, 128) // 设置 TCP 参数
                     .childOption(ChannelOption.SO_KEEPALIVE, false)
                     .channel(NioServerSocketChannel.class) // 指定使用 NIO 传输
                     .childHandler(new ChannelInitializer<SocketChannel>() {
                         @Override
                         public void initChannel(SocketChannel ch) throws Exception {
                     
                        	 ch.pipeline().addLast(new DelimiterBasedFrameDecoder(Integer.MAX_VALUE, Delimiters.lineDelimiter()[0]));
                        	 ch.pipeline().addLast(new StringDecoder());
                        	 ch.pipeline().addLast(new StringEncoder());
                        	 ch.pipeline().addLast(new IdleStateHandler(60, 45, 20, TimeUnit.SECONDS));
                        	 ch.pipeline().addLast(new ServerHandler());
                         }
                     });
            
            // 绑定端口并启动服务器
            ChannelFuture future = bootstrap.bind(8080).sync();
            System.out.println("Server started on port 8080");
            CuratorFramework client = ZookeeperFactory.create();
            InetAddress netAddress = InetAddress.getLocalHost();
           
            // 在ZooKeeper指定路径创建EPHEMERAL临时节点（节点路径包含本机IP)
            int port =8080;
            int weight = 2;
			try {
				client.create().withMode(CreateMode.EPHEMERAL_SEQUENTIAL).forPath(Constants.SERVER_PATH+"/"+netAddress.getHostAddress()+"#"+ port+"#"+weight+"#");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	
            // 等待服务器 socket 关闭
            future.channel().closeFuture().sync();
            
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            // 优雅关闭 EventLoopGroup
            childGroup.shutdownGracefully();
            parentGroup.shutdownGracefully();
        }
    }

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		try {
			this.start();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
