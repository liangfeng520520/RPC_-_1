package com.zhr.user.remote;

import java.util.List;
import com.zhr.netty.util.Response;
import com.zhr.user.bean.User;

public interface UserRemote {
    public Response saveUser(User user);
    public Response saveUsers(List<User> users);
}