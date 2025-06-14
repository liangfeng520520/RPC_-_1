package com.zhr.client.proxy;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.stereotype.Component;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.cglib.proxy.Callback;
import com.zhr.client.annotation.RemoteInvoke;
import com.zhr.client.core.ClientRequest;
import com.zhr.client.core.TcpClient;
import com.zhr.client.param.Response;
import com.zhr.user.bean.User;
@Component
public class InvokeProxy implements BeanPostProcessor {
 
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        // 获取 bean 类中声明的所有字段
        Field[] fields = bean.getClass().getDeclaredFields();
 
        for (Field field : fields) {
            // 检查字段是否带有 @RemoteInvoke 注解
            if (field.isAnnotationPresent(RemoteInvoke.class)) {
                // 设置字段为可访问，即使它是私有的
                field.setAccessible(true);
 
                // 创建一个 Map 来存储方法与类之间的映射
                final Map<Method, Class> methodClassMap = new HashMap<Method, Class>();
                // 填充 methodClassMap，将字段类型中的所有方法映射到该字段类型
                putMethodClass(methodClassMap, field);
 
                // 创建一个 CGLIB 的 Enhancer 实例
                Enhancer enhancer = new Enhancer();
                // 设置 Enhancer 的接口为字段的类型
                enhancer.setInterfaces(new Class[]{field.getType()});
 
                // 设置 Enhancer 的回调为 MethodInterceptor
                enhancer.setCallback(new MethodInterceptor() {
                    @Override
                    public Object intercept(Object instance, Method method, Object[] args, MethodProxy proxy) throws Throwable {
                        // 创建一个 ClientRequest 对象
                        ClientRequest request = new ClientRequest();
                        // 创建一个 User 对象并设置其名称
                       // User u = new User();
                       // u.setName("ZHR");
                        // 设置请求命令为方法所属类名和方法名
                        request.setCommand(methodClassMap.get(method).getName() + "." + method.getName());
                        // 设置请求内容为方法调用的第一个参数
                        request.setContent(args[0]);
                        // 发送请求并获取响应
                        Response resp = TcpClient.send(request);
                        // 返回响应
                        return resp;
                    }
                });
 
                try {
                    // 使用 Enhancer 创建代理对象，并将其设置到 bean 的字段中
                    field.set(bean, enhancer.create());
                } catch (Exception e) {
                    // 如果创建代理失败，抛出运行时异常
                    throw new RuntimeException("Failed to create proxy for field: " + field.getName(), e);
                }
            }
        }
 
        // 返回修改后的 bean
        return bean;
    }
 
    // 将字段类型中的所有方法映射到该字段类型，并存储在 methodClassMap 中
    private void putMethodClass(Map<Method, Class> methodClassMap, Field field) {
        // 获取字段类型中声明的所有方法
        Method[] methods = field.getType().getDeclaredMethods();
        // 遍历所有方法，并将它们映射到字段类型
        for (Method m : methods) {
            methodClassMap.put(m, field.getType());
        }
    }
 
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        // 当前实现中没有进行任何操作，直接返回 bean
        return bean;
    }
}