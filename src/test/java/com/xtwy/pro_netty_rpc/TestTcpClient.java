package com.xtwy.pro_netty_rpc;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.zhr.netty.client.ClientRequest;
import com.zhr.netty.client.TcpClient;
import com.zhr.netty.util.Response;
import com.zhr.user.bean.User;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes=RemoteInvokingTest.class)
@ComponentScan("com.zhr")
public class TestTcpClient {
    @Test
	public void testResponse() {
	    ClientRequest request = new ClientRequest();
	    request.setContent("测试");
	    Response resp = TcpClient.send(request);
	    System.out.println("响应为：" + resp.getResult());
   }
    
    @Test
   	public void testSaveUser() {
    	ClientRequest request = new ClientRequest();
        User u = new User();
        u.setId((long) 1);
        u.setName("zhr");
        request.setCommand("com.zhr.user.controller.UserController.saveUser");
	    request.setContent(u);
	    Response resp = TcpClient.send(request);
	    System.out.println("响应为：" + resp.getResult());
    }
    
    @Test
   	public void testSaveUsers() {
    	ClientRequest request = new ClientRequest();
    	List<User> users = new ArrayList<User>();
        User u = new User();
        u.setId((long) 1);
        u.setName("zhr");
        users.add(u);
        request.setCommand("com.zhr.user.controller.UserController.saveUsers");
	    request.setContent(users);
	    Response resp = TcpClient.send(request);
	    System.out.println("响应为：" + resp.getResult());
    }
}
