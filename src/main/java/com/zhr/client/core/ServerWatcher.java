package com.zhr.client.core;

import java.util.HashSet;
import java.util.List;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.zookeeper.WatchedEvent;
import com.zhr.client.zk.ZookeeperFactory;

import io.netty.channel.ChannelFuture;

public class ServerWatcher implements CuratorWatcher {
    
	/**ServerWatcher 是一个 ZooKeeper 监听器，用于监听 ZooKeeper 节点变化（如服务器地址增删）。
	当节点变化时，process 方法会被触发，重新获取最新的服务器地址列表，并更新 realServerPath。
	这样，TcpClient 可以动态感知服务器地址的变化，并更新连接。
	**/
	@Override
	public void process(WatchedEvent event) throws Exception {
		CuratorFramework client = ZookeeperFactory.create();
		String path = event.getPath();
		client.getChildren().usingWatcher(this).forPath(path);//重新添加监听器。
		List<String> serverPaths = client.getChildren().forPath(path);//获取最新服务器路径列表
		ChannelManager.realServerPath.clear();//清理旧的realServerPath
		for(String serverPath:serverPaths) {
			String[] str = serverPath.split("#");
			int weight = Integer.valueOf(str[2]);
			if(weight>0) {//按照权重大小添加多个服务器地址到realServerPath。
				for(int w=0;w<=weight;w++) {
					ChannelManager.realServerPath.add(str[0] + "#" + str[1]);
					
				}
			}
			ChannelManager.realServerPath.add(str[0] + "#" + str[1]);//就算是权重为0的serverPath也应该加入realServerPath。
			
		}
		ChannelManager.clear();//清理旧的channelFuture连接
		for(String realServer:ChannelManager.realServerPath) {
			String[] str = realServer.split("#");
			try {//根据权重连接多次到这个服务器地址
				int weight = Integer.valueOf(str[2]);
				if(weight>0) {
					for(int w=0;w<=weight;w++) {
						ChannelFuture channelFuture = TcpClient.b.connect(str[0],Integer.valueOf(str[1]));
						ChannelManager.add(channelFuture);
					}
				}
				
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}  
		}
	}


