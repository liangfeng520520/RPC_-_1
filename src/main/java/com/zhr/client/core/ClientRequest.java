package com.zhr.client.core;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 客户端请求对象封装类
 * 用于构造网络请求的元数据和内容载体
 */
public class ClientRequest {
   
   // 请求的唯一ID（final修饰保证线程安全）
   private final long id;
   
   // 请求携带的业务数据内容
   private Object content;
   
   // 原子计数器，用于生成全局唯一的请求ID（静态保证全局唯一性）
   private final static AtomicLong aid = new AtomicLong(1); // 修改为static
   
   private String command;
   /**
    * 构造函数
    * 自动生成递增的请求ID
    */
   public ClientRequest() {
       // 原子操作获取并递增ID（线程安全）
       this.id = aid.incrementAndGet(); 
   }
   
   // -------------------- Getter & Setter --------------------
   
   /**
    * 获取请求内容
    * @return 请求负载对象
    */
   public Object getContent() {
       return content;
   }
   
   public String getCommand() {
	return command;
}

public void setCommand(String command) {
	this.command = command;
}

/**
    * 设置请求内容
    * @param content 需要传输的业务数据
    */
   public void setContent(Object content) {
       this.content = content;
   }
   
   /**
    * 获取请求唯一标识
    * @return 请求ID（long类型）
    */
   public long getId() {
       return id;
   }
}