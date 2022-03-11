package com.javaweb.service.user;

import com.javaweb.dao.BaseDao;
import com.javaweb.dao.user.UserDao;
import com.javaweb.dao.user.UserDaoImpl;
import com.javaweb.pojo.Role;
import com.javaweb.pojo.User;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class UserServiceImpl implements UserService{
    //业务层都会调用dao层，所以我们要引入dao层
    UserDao userDao = null;

    public UserServiceImpl() {
        userDao = new UserDaoImpl();
    }

    @Override
    public User login(String userCode, String password) {
        Connection conn = null;
        User user = null;

        try {
            conn = BaseDao.getConnection();
            //通过业务层调用具体的数据库操作
            user = userDao.getLoginUser(conn, userCode);
            //密码不正确返回null
            if (!user.getUserPassword().equals(password)){
                return null;
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            BaseDao.closeResources(conn,null,null);
        }
        return user;

    }

    //根据用户id修改密码
    @Override
    public boolean updatePassword(long id, String password) {
        Connection conn = null;
        boolean flag = false;

        try {
            conn = BaseDao.getConnection();
            //通过业务层调用具体的数据库操作
            if (userDao.updatePassword(conn,id,password)>0){
                flag = true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            BaseDao.closeResources(conn,null,null);
        }
        return flag;
    }

    //查询用户数
    @Override
    public int getUserCounts(String userName, int userRole) {
        Connection conn = null;
        int userCounts = 0;

        try {
            conn = BaseDao.getConnection();
            //通过业务层调用具体的数据库操作
            userCounts = userDao.getUserCounts(conn,userName,userRole);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            BaseDao.closeResources(conn,null,null);
        }
        return userCounts;
    }

    //根据条件查询用户列表
    @Override
    public List<User> getUserList(String userName, int userRole, int currentPageNo, int pageSize) {
        Connection conn = null;
        List<User> userList = null;

        try {
            conn = BaseDao.getConnection();
            //通过业务层调用具体的数据库操作
            userList = userDao.getUserList(conn,userName,userRole,currentPageNo,pageSize);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            BaseDao.closeResources(conn,null,null);
        }
        return userList;
    }

    //添加用户
    @Override
    public boolean addUser(User user) {
        Connection conn = null;
        boolean flag = false;

        try {
            conn = BaseDao.getConnection();
            //通过业务层调用具体的数据库操作
            if (userDao.addUser(conn,user)){
                flag = true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            BaseDao.closeResources(conn,null,null);
        }
        return flag;
    }

    //查询userCode是否存在
    @Override
    public boolean checkUserCode(String userCode) {
        Connection conn = null;
        User user = null;
        boolean result = false;

        try {
            conn = BaseDao.getConnection();
            //通过业务层调用具体的数据库操作
            user = userDao.getLoginUser(conn, userCode);
            if (user!=null){
                result = true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            BaseDao.closeResources(conn,null,null);
        }
        return result;
    }

    //删除用户
    @Override
    public int deleteUser(String userId) {
        Connection conn = null;
        int updateRows = 0;

        try {
            conn = BaseDao.getConnection();
            //通过业务层调用具体的数据库操作
            updateRows = userDao.deleteUser(conn,userId);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            BaseDao.closeResources(conn,null,null);
        }
        return updateRows;
    }

    //修改用户
    @Override
    public boolean modifyUser(User user) {
        Connection conn = null;

        try {
            conn = BaseDao.getConnection();
            //通过业务层调用具体的数据库操作
            if (userDao.modifyUser(conn,user)){
                return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            BaseDao.closeResources(conn,null,null);
        }
        return false;
    }

    //根据id获取用户信息
    @Override
    public User getUserId(String userId) {
        Connection conn = null;
        User user = null;

        try {
            conn = BaseDao.getConnection();
            //通过业务层调用具体的数据库操作
            user = userDao.getUserById(conn,userId);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            BaseDao.closeResources(conn,null,null);
        }
        return user;
    }


    @Test
    public void test(){
        User user = new User();
        user.setId(20);
        user.setUserRole(1);
        user.setUserName("cjl2");
        user.setUserPassword("justforme09");
        user.setAddress("广州");
        user.setPhone("13660577120");
        try {
            user.setBirthday(new SimpleDateFormat("yyyy-MM-dd").parse("2000-11-02"));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        user.setUserCode("10000");
        user.setGender(1);
        user.setCreationDate(new Date(System.currentTimeMillis()));
        UserService service = new UserServiceImpl();
        //List<User> userList = service.getUserList("", 2, 1, 5);
        //int count = service.getUserCounts(null,1);
        //for (User user:userList){
        //    System.out.println(user.toString());
        //}
        //System.out.println(count);
        //service.addUser(user);
        //int result = service.deleteUser("123");
        //System.out.println(result);
        //service.modifyUser(user);

        //测试：通过id获取用户信息的方法
        System.out.println(service.getUserId("20").toString());
    }
}
