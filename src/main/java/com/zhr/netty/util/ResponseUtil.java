package com.zhr.netty.util;

public class ResponseUtil {
  public static Response createSuccessfully() {
	  return new Response();
  }
  
  public static Response createFailResult(String code,String msg) {
	  Response response = new Response();
	  response.setCode(code);//设置失败序号
	  response.setMsg(msg);//设置失败原因
	  return response;
  }
  
  public static Response createSuccessResult(Object content) {
	  Response response = new Response();
	  response.setResult(content);
	  return response;
  }
}
