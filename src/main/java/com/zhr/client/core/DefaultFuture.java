package com.zhr.client.core;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import com.zhr.client.param.Response;



/**
 * 异步调用结果容器，用于实现请求-响应模型的异步通信
 */
public class DefaultFuture {
  
    // 存储响应结果对象
    private Response response;
    
    // 可重入锁，用于线程同步
    final Lock lock = new ReentrantLock();
    
    // 条件变量，用于线程间通信（等待/通知机制）
    public Condition condition = lock.newCondition();
    
    // 全局映射表：存储所有未完成的Future对象（key=请求ID）
    public final static ConcurrentHashMap<Long, DefaultFuture> allDefaultFuture = 
        new ConcurrentHashMap<Long, DefaultFuture>();
    
    private long timeout = 5*60*1000l;
    private long startTime = System.currentTimeMillis();
    
    /**
     * 构造函数
     * @param request 客户端请求对象，将其ID与当前Future关联
     */
    public DefaultFuture(ClientRequest request) {
        // 将当前Future存入全局映射表
        allDefaultFuture.put(request.getId(), this);
    }
    
    /**
     * 阻塞获取响应结果（主线程调用）
     * @return 响应对象
     */
    
    public Response get(long time) {
        lock.lock();  // 加锁
        try {
            // 循环检查响应是否就绪（防止虚假唤醒）
            while (!done()) {
            	
                condition.await(time,TimeUnit.MILLISECONDS); //释放锁并等待，被唤醒时会重新获取锁
                if((System.currentTimeMillis()-startTime)>time){
                	System.out.println("请求超时");
                	break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();  // 确保锁被释放
        }
        return this.response;  // 返回响应结果
    }
    
    

	//由IO线程调用，用来唤醒挂住的主线程。
    public static void receive(Response response) {
  
    	DefaultFuture df = allDefaultFuture.get(response.getId());
    	if(df!=null) {
    		Lock lock = df.lock;
        	lock.lock();
			    	try {
			    		df.setResponse(response);
			    		df.condition.signal();
			    		allDefaultFuture.remove(df);
			    	}catch(Exception e) {
			    		e.printStackTrace();
			    	}finally {
			    		lock.unlock();
			       }
			    	}
			    }
    /**
     * 检查响应是否已就绪
     * @return true=响应已到达，false=响应未到达
     */
    private boolean done() {
        return this.response != null;  // 简单判空检查
    }

    
    static class FutureThread extends Thread{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			Set<Long> ids = allDefaultFuture.keySet();
			for(Long id:ids) {
				DefaultFuture df = allDefaultFuture.get(id);
				if(df==null) {
					allDefaultFuture.remove(id);
					
				}else {
					if(df.getTimeout()<(System.currentTimeMillis()-df.getStartTime())) {
						Response resp = new Response();
						resp.setId(id);
						resp.setCode("3333");
						resp.setMsg("超时了");
						receive(resp);
				}
			}
		}
    	
    }
    }	
   static {
	   FutureThread futureThread = new FutureThread();
	   futureThread.setDaemon(true);
	   futureThread.start();
   }
    // -------------------- Getter/Setter --------------------

    public Response getResponse() {
        return response;
    }

	public void setResponse(Response response) {
		this.response = response;
	}
    
    public void setTimeout(long timeout) {
		this.timeout = timeout;
	}

	public long getStartTime() {
		return startTime;
	}

	
	public long getTimeout() {
		return timeout;
	}
}
