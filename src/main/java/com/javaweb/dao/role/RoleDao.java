package com.javaweb.dao.role;

import com.javaweb.pojo.Role;

import java.sql.Connection;
import java.util.List;

public interface RoleDao {
    //获取角色列表
    public List<Role> getRoleList(Connection conn) throws Exception;
}
