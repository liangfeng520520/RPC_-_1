package com.zhr.netty.medium;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.zhr.netty.handler.param.ServerRequest;
import com.zhr.netty.util.Response;

public class Media {
    public static Map<String,BeanMethod> beanMap;
    static {
    	beanMap = new HashMap<String,BeanMethod>();
    }
    
    private static Media m = null;
   
	
    public static Media newInstance() {
		
       if(m == null) {
    		m = new Media();
    	}
       return m;
    }
    
    //反射处理业务代码
	public Response process(ServerRequest request){
	Response result = null;
	try {
		//获取请求的方法名
		String command = request.getCommand();
		BeanMethod beanMethod = beanMap.get(command);
		
		if(beanMethod == null) {
			return null;
		}
		Object bean = beanMethod.getBean();
		Method m = beanMethod.getMethod();
		Class paramType = m.getParameterTypes()[0];
		Object content = request.getContent();
		Object args = JSONObject.parseObject(JSONObject.toJSONString(content),paramType);
		result = (Response) m.invoke(bean, args);//传入调用该方法的实例和参数
		result.setId(request.getId());
	}catch(Exception e) {
		e.printStackTrace();
	}
	
	return result;
	}
	}

