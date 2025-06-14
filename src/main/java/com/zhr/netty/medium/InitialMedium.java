package com.zhr.netty.medium;


import java.lang.reflect.Method;
import java.util.Map;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import com.zhr.netty.annotation.Remote;
import com.zhr.netty.annotation.RemoteInvoke;

//@Component注解表示这是一个Spring管理的Bean，
//Spring容器会自动扫描并创建其实例。
@Component
public class InitialMedium implements BeanPostProcessor {

 /**
  * 在Bean初始化之前执行的方法。
  * 
  * @param bean     要处理的Bean实例。
  * @param beanName Bean的名称。
  * @return 返回处理后的Bean实例。
  */
 @Override
 public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
     // 这里没有对Bean进行任何处理，直接返回原始Bean实例。
     return bean;
 }

 /**
  * 在Bean初始化之后执行的方法。
  * 
  * @param bean     要处理的Bean实例。
  * @param beanName Bean的名称。
  * @return 返回处理后的Bean实例。
  */
 @Override
 public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
     
     // 检查Bean的类是否被@Remote注解标记
     if (bean.getClass().isAnnotationPresent(Remote.class)) {
         // 获取Bean的所有声明方法
         Method[] methods = bean.getClass().getDeclaredMethods();
         for (Method m : methods) {
             // 生成一个唯一的键，用于标识Bean的方法
             String key = bean.getClass().getInterfaces()[0].getName() + "." + m.getName();
             // 获取存储Bean方法的Ma
             Map<String, BeanMethod> beanMap = Media.beanMap;
             // 创建一个新的BeanMethod对象
             BeanMethod beanMethod = new BeanMethod();
             // 设置BeanMethod的Bean实例
             beanMethod.setBean(bean);
             // 设置BeanMethod的方法
             beanMethod.setMethod(m);
             // 将BeanMethod存入Map中
             beanMap.put(key, beanMethod);
         }
     }
     // 返回处理后的Bean实例
     return bean;
 }
}
