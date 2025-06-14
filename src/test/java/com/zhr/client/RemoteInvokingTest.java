package com.zhr.client;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import com.alibaba.fastjson.JSONObject;
import com.zhr.client.annotation.RemoteInvoke;
import com.zhr.client.param.Response;
import com.zhr.user.bean.User;
import com.zhr.user.remote.UserRemote;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes=RemoteInvokingTest.class)
@ComponentScan("com.zhr")
public class RemoteInvokingTest {
    
	 @RemoteInvoke
     private UserRemote userRemote;
     
	 @Test
	 public void testSaveUser() {
		 User u = new User();
		 u.setId(1);
		 u.setName("zhr");
		 Response response = userRemote.saveUser(u);
		 System.out.println(JSONObject.toJSONString(response));
	 }
	 @Test
	 public void testUsers() {
		 List<User> users = new ArrayList<User>();
		 User u = new User();
		 u.setId(1);
		 u.setName("zhr");
		 users.add(u);
		 userRemote.saveUsers(users);
	 }
}
