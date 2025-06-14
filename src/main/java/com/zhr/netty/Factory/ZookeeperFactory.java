package com.zhr.netty.Factory;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.RetryPolicy;
import org.apache.curator.retry.ExponentialBackoffRetry;

//在zookeeper创建服务器节点以便于客户端的服务发现
public class ZookeeperFactory {
	
public static CuratorFramework client;
public static CuratorFramework create() {
	if(client==null) {
	     RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
	     client = CuratorFrameworkFactory.newClient("localhost:2181", retryPolicy);
		 client.start();
	}
    return client;
  }
}

 