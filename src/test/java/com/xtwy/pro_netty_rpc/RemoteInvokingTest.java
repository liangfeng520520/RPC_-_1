package com.xtwy.pro_netty_rpc;

import com.zhr.user.bean.User;
import com.zhr.user.remote.UserRemote;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import com.zhr.netty.annotation.RemoteInvoke;
public class RemoteInvokingTest {
    
	 @RemoteInvoke
     private UserRemote userRemote;
     
	 @Test
	 public void testSaveUser() {
		 User u = new User();
		 u.setId(1);
		 u.setName("zhr");
		 userRemote.saveUser(u);
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
