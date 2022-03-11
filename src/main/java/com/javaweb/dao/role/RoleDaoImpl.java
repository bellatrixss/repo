package com.javaweb.dao.role;

import com.javaweb.dao.BaseDao;
import com.javaweb.pojo.Role;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class RoleDaoImpl implements RoleDao{
    //获取角色列表
    @Override
    public List<Role> getRoleList(Connection conn) throws Exception {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Role> roleList = new ArrayList<>();
        if (conn != null) {
            String sql = "select * from smbms_role";
            Object[] params = {};
            rs = BaseDao.query(conn, pstmt, rs, sql, params);
            while (rs.next()){
                Role role = new Role();
                role.setId(rs.getInt("id"));
                role.setRoleCode(rs.getString("roleCode"));
                role.setRoleName(rs.getString("roleName"));
                roleList.add(role);

            }
            BaseDao.closeResources(null,pstmt,rs);

        }
        return roleList;
    }

}
