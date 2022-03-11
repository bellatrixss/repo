package com.javaweb.dao;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

public class BaseDao {
    private static String url;
    private static String driver;
    private static String username;
    private static String password;

    //静态代码块，一启动就加载
    static {
        InputStream is = BaseDao.class.getClassLoader().getResourceAsStream("db.properties");
        Properties properties = new Properties();
        try {
            properties.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        }

        url =  properties.getProperty("url");
        driver =  properties.getProperty("driver");
        username =  properties.getProperty("username");
        password =  properties.getProperty("password");
    }

    public static Connection getConnection() throws Exception {
        Class.forName(driver);
        Connection conn = DriverManager.getConnection(url, username, password);
        return conn;
    }

    //编写公共查询方法
    public static ResultSet query (Connection conn,PreparedStatement pstmt,ResultSet resultSet,String sql,Object[] params) throws SQLException {
        //提取sql对象，当作参数传入，方便统一打开和关闭
        pstmt = conn.prepareStatement(sql);

        for (int i=0;i<params.length;i++){
            pstmt.setObject(i+1,params[i]);
        }
        resultSet = pstmt.executeQuery();
        return resultSet;
    }

    //增删改公共方法
    public static int update (Connection conn, PreparedStatement pstmt, String sql, Object[] params) throws SQLException {
        //提取sql对象，当作参数传入，方便统一打开和关闭
        pstmt = conn.prepareStatement(sql);

        for (int i=0;i<params.length;i++){
            pstmt.setObject(i+1,params[i]);
        }
        int updateRows = pstmt.executeUpdate();
        return updateRows;
    }

    //释放资源
    public static boolean closeResources(Connection conn,PreparedStatement pstmt,ResultSet resultSet){
        boolean flag = true;

        if (resultSet != null){
            try {
                resultSet.close();
                //gc回收
                resultSet = null;
            } catch (SQLException e) {
                flag = false;
                e.printStackTrace();
            }
        }
        if (pstmt != null){
            try {
                pstmt.close();
                //gc回收
                pstmt = null;
            } catch (SQLException e) {
                flag = false;
                e.printStackTrace();
            }
        }
        if (conn != null){
            try {
                conn.close();
                //gc回收
                conn = null;
            } catch (SQLException e) {
                flag = false;
                e.printStackTrace();
            }
        }
        return flag;
    }
}
