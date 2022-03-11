package com.javaweb.dao.user;

import com.javaweb.pojo.User;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface UserDao {
    //获取要登录的用户
    public User getLoginUser(Connection conn,String userCode) throws SQLException;

    //修改用户密码
    public int updatePassword(Connection conn,long id,String password) throws  SQLException;

    //查询用户总数
    public int getUserCounts(Connection conn,String userName,int userRole) throws SQLException;

    //条件查询获取用户列表
    public List<User> getUserList(Connection conn, String userName, int userRole, int currentPageNo, int pageSize) throws Exception;

    //添加用户
    public boolean addUser(Connection conn,User user) throws SQLException;

    //删除用户
    public int deleteUser(Connection conn,String userId) throws SQLException;

    //修改用户
    public boolean modifyUser(Connection conn,User user) throws SQLException;

    //通过id获取用户信息
    public User getUserById(Connection conn,String userId) throws SQLException;





}
