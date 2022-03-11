package com.javaweb.service.user;

import com.javaweb.pojo.Role;
import com.javaweb.pojo.User;

import java.sql.Connection;
import java.util.List;

public interface UserService {
    //用户登录
    public User login(String userCode,String password);

    //根据用户id修改密码
    public boolean updatePassword(long id,String password);

    //查询用户数
    public int getUserCounts(String userName,int userRole);

    //根据条件查询用户列表
    public List<User> getUserList(String userName,int userRole,int currentPageNo,int pageSize);

    //增加用户
    public boolean addUser(User user);

    //查询userCode是否存在
    public boolean checkUserCode(String userCode);

    //删除用户
    public int deleteUser(String userId);

    //修改用户
    public boolean modifyUser(User user);

    //根据id获取用户信息
    public User getUserId(String userId);



}
