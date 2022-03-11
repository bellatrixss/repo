package com.javaweb.dao.provider;

import com.javaweb.pojo.Provider;
import com.javaweb.pojo.User;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface ProviderDao {
    //查询供应商总数
    public int getProCounts(Connection conn, String proCode) throws SQLException;

    //条件查询获取供应商列表
    public List<Provider> getProList(Connection conn, int ProCode, String ProName, int currentPageNo, int pageSize) throws Exception;

    //添加供应商
    public boolean addPro(Connection conn,Provider provider) throws SQLException;

    //删除供应商
    public int deletePro(Connection conn,String proId) throws SQLException;

    //修改供应商
    public boolean modifyPro(Connection conn,Provider provider) throws SQLException;

    //通过id获取供应商信息
    public Provider getProById(Connection conn,String proId) throws SQLException;

}
