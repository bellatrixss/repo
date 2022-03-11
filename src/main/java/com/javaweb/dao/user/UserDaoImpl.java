package com.javaweb.dao.user;

import com.javaweb.dao.BaseDao;
import com.javaweb.pojo.User;
import com.javaweb.service.user.UserService;
import com.javaweb.service.user.UserServiceImpl;
import com.mysql.cj.util.StringUtils;
import org.junit.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UserDaoImpl implements UserDao{

    //获取登录的用户
    @Override
    public User getLoginUser(Connection conn, String userCode) throws SQLException {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        User user = null;
        if (conn != null) {
            String sql = "select * from smbms_user where userCode=?";
            Object[] params = {userCode};
            rs = BaseDao.query(conn, pstmt, rs, sql, params);
            if (rs.next()){
                user = new User();
                user.setId(rs.getInt("id"));
                user.setUserCode(rs.getString("userCode"));
                user.setUserName(rs.getString("userName"));
                user.setUserPassword(rs.getString("userPassword"));
                user.setGender(rs.getInt("gender"));
                user.setBirthday(rs.getDate("birthday"));
                user.setPhone(rs.getString("phone"));
                user.setAddress(rs.getString("address"));
                user.setUserRole(rs.getInt("userRole"));
                user.setCreatedBy(rs.getInt("createdBy"));
                user.setCreationDate(rs.getTimestamp("creationDate"));
                user.setModifyBy(rs.getInt("modifyBy"));
                user.setModifyDate(rs.getTimestamp("modifyDate"));

            }
            BaseDao.closeResources(null,pstmt,rs);

        }
        return user;
    }

    //修改当前用户密码
    @Override
    public int updatePassword(Connection conn, long id, String password) throws SQLException {
        PreparedStatement pstmt = null;
        int updateRows = 0;

        if (conn!=null){
            String sql = "update smbms_user set userPassword=? where id=?";
            Object[] params = {password,id};
            updateRows = BaseDao.update(conn,pstmt,sql,params);
            BaseDao.closeResources(null,pstmt,null);
        }
        return updateRows;
    }

    //查询用户总数
    @Override
    public int getUserCounts(Connection conn, String userName, int userRole) throws SQLException {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int userCounts = 0;

        if (conn!=null){
            StringBuffer sql = new StringBuffer();
            sql.append("select count(1) from smbms_user as u,smbms_role as r where u.userRole=r.id ");
            ArrayList<Object> list = new ArrayList<>();

            //动态sql
            //如果userName不为空，需要拼接sql
            if (!StringUtils.isNullOrEmpty(userName)){
                sql.append("and u.userName like ?");
                list.add("%"+userName+"%");
            }
            if (userRole>0){
                sql.append("and u.userRole=?");
                list.add(userRole);
            }

            //list转换为array
            Object[] params = list.toArray();
            rs = BaseDao.query(conn,pstmt,rs,sql.toString(),params);
            if (rs.next()){
               userCounts = rs.getInt("count(1)");
            }
            BaseDao.closeResources(null,pstmt,rs);
        }
        return userCounts;
    }

    //获取用户列表
    @Override
    public List<User> getUserList(Connection conn, String userName, int userRole, int currentPageNo, int pageSize) throws Exception {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<User> userList = new ArrayList<>();

        if (conn!=null){
            StringBuffer sql = new StringBuffer();
            //sql最后的空格不能删，因为需要拼接！
            sql.append("select * from smbms_user as u,smbms_role as r where u.userRole=r.id ");
            ArrayList<Object> list = new ArrayList<>();

            if (!StringUtils.isNullOrEmpty(userName)){
                sql.append("and u.userName like ? ");
                list.add("%"+userName+"%");
            }
            if (userRole>0){
                sql.append("and u.userRole=? ");
                list.add(userRole);
            }
            //实现分页，需要两个参数，第一个是首部tuple的序号，第二个是页面容量
            sql.append("order by u.creationDate DESC limit ?,?");
            list.add((currentPageNo-1)*pageSize);
            list.add(pageSize);

            //list转换为array
            Object[] params = list.toArray();
            rs = BaseDao.query(conn,pstmt,rs,sql.toString(),params);
            while (rs.next()){
                User _user = new User();
                _user.setBirthday((Date) rs.getObject("birthday"));
                _user.setGender((Integer) rs.getObject("gender"));
                _user.setId((Long) rs.getObject("id"));
                _user.setPhone((String) rs.getObject("phone"));
                _user.setUserCode((String) rs.getObject("userCode"));
                _user.setUserName((String) rs.getObject("userName"));
                _user.setUserRole((Integer) rs.getObject("userRole"));
                _user.setUserRoleName((String) rs.getObject("roleName"));
                userList.add(_user);

            }
            BaseDao.closeResources(null,pstmt,rs);
        }
        return userList;
    }

    //增加用户
    @Override
    public boolean addUser(Connection conn, User user) throws SQLException {
        PreparedStatement pstmt = null;
        int updateRows = 0;

        if (conn!=null){
            //开启事务
            conn.setAutoCommit(false);
            String sql = "insert into smbms_user values(null,?,?,?,?,?,?,?,?,?,?,null,null)";
            Object[] params = {user.getUserCode(),user.getUserName(), user.getUserPassword(),
                               user.getGender(),user.getBirthday(), user.getPhone(),
                               user.getAddress(),user.getUserRole(), user.getCreatedBy(),
                               user.getCreationDate()};

            try {
                updateRows = BaseDao.update(conn,pstmt,sql,params);
                conn.commit();
            }catch (Exception e){
                conn.rollback();
                e.printStackTrace();
            }finally {
                BaseDao.closeResources(null,pstmt,null);
            }

            if (updateRows>0){
                return true;
            }else {
                return false;
            }

        }
        return false;
    }

    //删除用户
    @Override
    public int deleteUser(Connection conn, String userId) throws SQLException {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int updateRows = 0;

        if (conn!=null){
            //查询id是否存在
            String sql = "select * from smbms_user where id=?";
            Object[] params = {userId};
            rs = BaseDao.query(conn,pstmt,rs,sql,params);
            if (!rs.next()){//不存在
                updateRows = -1;
                return updateRows;
            }

            //开启事务
            conn.setAutoCommit(false);
            sql = "delete from smbms_user where id=?";

            try {
                updateRows = BaseDao.update(conn,pstmt,sql,params);
                conn.commit();
            }catch (Exception e){
                conn.rollback();
                e.printStackTrace();
            }finally {
                BaseDao.closeResources(null,pstmt,rs);
            }

        }
        return updateRows;
    }

    //修改用户信息
    @Override
    public boolean modifyUser(Connection conn, User user) throws SQLException {
        PreparedStatement pstmt = null;
        int updateRows = 0;

        if (conn!=null){
            //开启事务
            conn.setAutoCommit(false);
            String sql = "update smbms_user " +
                         "set userName=?,gender=?,birthday=?,phone=?,address=?,userRole=? " +
                         "where id = ?;";

            Object[] params = {user.getUserName(), user.getGender(),user.getBirthday(),
                               user.getPhone(), user.getAddress(),user.getUserRole(),
                               user.getId()};

            try {
                updateRows = BaseDao.update(conn,pstmt,sql,params);
                conn.commit();
            }catch (Exception e){
                conn.rollback();
                e.printStackTrace();
            }finally {
                BaseDao.closeResources(null,pstmt,null);
            }

            if (updateRows>0){
                return true;
            }else {
                return false;
            }

        }
        return false;
    }

    //通过id获取用户信息
    @Override
    public User getUserById(Connection conn, String userId) throws SQLException {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        User user = new User();

        if (conn!=null){
            String sql = "select * from smbms_user as u,smbms_role as r where u.userRole=r.id and u.id=?";

            Object[] params = {userId};
            rs = BaseDao.query(conn,pstmt,rs,sql,params);
            if (rs.next()){
                user.setUserCode((String) rs.getObject("userCode"));
                user.setBirthday((Date) rs.getObject("birthday"));
                user.setGender((Integer) rs.getObject("gender"));
                user.setId((Long) rs.getObject("id"));
                user.setPhone((String) rs.getObject("phone"));
                user.setAddress((String) rs.getObject("address"));
                user.setUserName((String) rs.getObject("userName"));
                user.setUserRole((Integer) rs.getObject("userRole"));
                user.setUserRoleName((String) rs.getObject("roleName"));

            }
            BaseDao.closeResources(null,pstmt,rs);
        }
        return user;
    }



    @Test
    public void test(){
        UserServiceImpl service = new UserServiceImpl();
        User admin = service.login("wen", "123456");
        System.out.println(admin.getCreationDate());

    }
}
