package com.zhr.user.remote;

import java.util.List;

import com.zhr.client.param.Response;
import com.zhr.user.bean.User;

public interface UserRemote {
    public Response saveUser(User user);
    public Response saveUsers(List<User> users);
}