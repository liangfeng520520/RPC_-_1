package com.zhr.user.remote;
import java.lang.annotation.Annotation;
import java.util.List;
import javax.annotation.Resource;
import com.zhr.netty.annotation.Remote;
import com.zhr.netty.util.Response;
import com.zhr.netty.util.ResponseUtil;
import com.zhr.user.bean.User;
import com.zhr.user.service.UserService;

@Remote
public class UserRemoteImpl implements UserRemote{
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
