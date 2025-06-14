package com.zhr.client.core;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import io.netty.channel.ChannelFuture;

public class ChannelManager {
	static AtomicInteger position = new AtomicInteger(0);
	static CopyOnWriteArrayList<String> realServerPath = new CopyOnWriteArrayList<String>();
    public static CopyOnWriteArrayList<ChannelFuture> channelFutures = new  CopyOnWriteArrayList<ChannelFuture>();
    
    public static void removeChannel(ChannelFuture channel) {
    	channelFutures.remove(channel);
    }
    
    public static void add(ChannelFuture channel) {
    	channelFutures.add(channel);
    }
    
    public static void clear() {
    	channelFutures.clear();
    }

    /**
	  计数器超过连接数：
		如果 position 的值超过了连接列表的大小，意味着已经遍历了所有连接。
		此时，position 被重置为 1，并返回第一个连接，实现循环轮询。
	  正常轮询：
		否则，使用 getAndIncrement() 方法获取当前计数器值，并返回对应的连接。
		计数器自动递增，以便下次调用时选择下一个连接。
     * 
     * @return
     */
	public static ChannelFuture get(AtomicInteger i) {
		int size = channelFutures.size();
		ChannelFuture channel = null;
		if(i.get()>size) {
			channel = channelFutures.get(0);
			ChannelManager.position= new AtomicInteger(1);
		}else {
			channel = channelFutures.get(i.getAndIncrement());
			
		}
		return channel;
	}
}
