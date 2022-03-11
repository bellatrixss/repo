package com.javaweb.service.role;

import com.javaweb.dao.BaseDao;
import com.javaweb.dao.role.RoleDao;
import com.javaweb.dao.role.RoleDaoImpl;
import com.javaweb.pojo.Role;
import org.junit.Test;

import java.sql.Connection;
import java.util.List;

public class RoleServiceImpl implements RoleService{
    //业务层都会调用dao层，所以我们要引入dao层
    RoleDao roleDao = null;

    public RoleServiceImpl() {
        roleDao = new RoleDaoImpl();
    }

    //获取角色列表
    @Override
    public List<Role> getRoleList() {
        Connection conn = null;
        List<Role> roleList = null;

        try {
            conn = BaseDao.getConnection();
            //通过业务层调用具体的数据库操作
            roleList = roleDao.getRoleList(conn);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            BaseDao.closeResources(conn,null,null);
        }
        return roleList;
    }

    @Test
    public void test(){
        RoleServiceImpl service = new RoleServiceImpl();
        List<Role> roleList = service.getRoleList();
        for(Role role:roleList){
            System.out.println(role.toString());
        }
    }
}
