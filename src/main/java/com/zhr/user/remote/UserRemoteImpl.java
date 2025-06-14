package com.zhr.user.remote;
import java.lang.annotation.Annotation;
import java.util.List;
import javax.annotation.Resource;

import com.zhr.client.annotation.Remote;
import com.zhr.client.param.Response;
import com.zhr.client.param.ResponseUtil;
import com.zhr.user.bean.User;
import com.zhr.user.service.UserService;

@Remote
public class UserRemoteImpl implements UserRemote{
	 //Spring 的依赖注入注解，自动注入 UserService 的实例到当前类。
	 @Resource
	 private UserService userService;

	 /**
	  * 保存用户信息的方法。
	  * @param user 要保存的用户对象。
	  */
	 public Response saveUser(User user) {
	     // 调用userService的save方法，将用户对象保存到数据库或其他存储中。
	     // 具体的保存逻辑由UserService的实现类决定。
	     userService.save(user);
	     return ResponseUtil.createSuccessResult(user);
	 }
	 
	 public Response saveUsers(List<User> users) {
	     userService.saveList(users);
	     return ResponseUtil.createSuccessResult(users);
	 }

	
}
