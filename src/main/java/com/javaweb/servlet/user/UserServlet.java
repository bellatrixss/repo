package com.javaweb.servlet.user;

import com.alibaba.fastjson.JSONArray;
import com.javaweb.pojo.Role;
import com.javaweb.pojo.User;
import com.javaweb.service.role.RoleService;
import com.javaweb.service.role.RoleServiceImpl;
import com.javaweb.service.user.UserService;
import com.javaweb.service.user.UserServiceImpl;
import com.javaweb.util.Constants;
import com.javaweb.util.PageSupport;
import com.mysql.cj.util.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String method = req.getParameter("method");
        if (method.equals("savepwd")){
            this.updatePassword(req,resp);
        }else if (method.equals("pwdmodify")){
            this.passwordModify(req,resp);
        }else if (method.equals("query")){
            this.userQuery(req,resp);
        }else if (method.equals("add")){
            this.userAdd(req,resp);
        }else if(method.equals("getrolelist")){
            this.getrolelist(req,resp);
        }else if (method.equals("ucexist")){
            this.checkUserCode(req,resp);
        }else if (method.equals("deluser")){
            this.deleteUser(req,resp);
        }else if (method.equals("modify")){
            this.modifyRedirect(req,resp);
        } else if (method.equals("modifyexe")){
            this.modifyUser(req,resp);
        }else if (method.equals("view")){
            this.viewUser(req,resp);
        }

    }




    //获取角色列表
    private void getrolelist(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        List<Role> roleList = null;
        RoleService roleService = new RoleServiceImpl();
        roleList = roleService.getRoleList();
        //把roleList转换成json对象输出
        resp.setContentType("application/json");
        PrintWriter outPrintWriter = resp.getWriter();
        outPrintWriter.write(JSONArray.toJSONString(roleList));
        outPrintWriter.flush();
        outPrintWriter.close();
    }


    //修改密码
    private void updatePassword(HttpServletRequest req, HttpServletResponse resp) {
        //从session里面拿id
        Object o = req.getSession().getAttribute(Constants.USER_SESSION);
        String newPassword = req.getParameter("newpassword");
        boolean flag = false;

        if(o!=null && !StringUtils.isNullOrEmpty(newPassword)){
            UserService service = new UserServiceImpl();
            flag = service.updatePassword(((User)o).getId(),newPassword);
            if (flag){
                req.setAttribute("message","密码修改成功，请用新密码登录！");
                //密码修改成功，移除session
                req.getSession().removeAttribute(Constants.USER_SESSION);

            }else {
                req.setAttribute("message","密码修改错误！");
            }
        }else {
            req.setAttribute("message","新密码有问题");
        }
        try {
            req.getRequestDispatcher("pwdmodify.jsp").forward(req,resp);
        } catch (ServletException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //验证旧密码
    private void passwordModify(HttpServletRequest req, HttpServletResponse resp) {
        //从session里面拿id
        Object o = req.getSession().getAttribute(Constants.USER_SESSION);
        String inputOldPassword = req.getParameter("oldpassword");
        //万能的map:结果集
        Map<String,String> resultMap = new HashMap<>();

        if (o==null){//session过期
            resultMap.put("result","sessionerror");
        }else if(StringUtils.isNullOrEmpty(inputOldPassword)){//输入密码为空
            resultMap.put("result","error");
        }else {
            //从session获取旧密码
            String oldPassword = ((User) o).getUserPassword();
            if (inputOldPassword.equals(oldPassword)){
                resultMap.put("result","true");
            }else {
                resultMap.put("result","false");
            }
        }


        try {
            resp.setContentType("application/json");
            PrintWriter writer = resp.getWriter();
            //JSONArray，阿里巴巴的工具类，转换格式
            /*resultMap = ["result","sessionerror","result","error"...]
            * JSON = {key:value}*/
            writer.write(JSONArray.toJSONString(resultMap));
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //验证usercode是否存在
    private void checkUserCode(HttpServletRequest req, HttpServletResponse resp) {

        //获取输入的userCode
        String userCode = req.getParameter("userCode");
        //万能的map:结果集
        Map<String,String> resultMap = new HashMap<>();

        //查询userCode对应的用户
        UserServiceImpl userService = new UserServiceImpl();
        boolean result = userService.checkUserCode(userCode);

        //userCode已经存在
        if (result){
            resultMap.put("userCode","exist");
        }else {
            resultMap.put("userCode","none");
        }

        try {
            resp.setContentType("application/json");
            PrintWriter writer = resp.getWriter();
            //JSONArray，阿里巴巴的工具类，转换格式
            /*resultMap = ["result","sessionerror","result","error"...]
             * JSON = {key:value}*/
            writer.write(JSONArray.toJSONString(resultMap));
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //增
    private void userAdd(HttpServletRequest req, HttpServletResponse resp) {
        //获取请求参数
        String userCode = req.getParameter("userCode");
        String userName = req.getParameter("userName");
        String userPassword = req.getParameter("userPassword");
        int gender = Integer.parseInt(req.getParameter("gender"));
        String birthday = req.getParameter("birthday");
        String phone = req.getParameter("phone");
        String address = req.getParameter("address");
        int userRole = Integer.parseInt(req.getParameter("userRole"));
        long createdBy = 1;
        Date creationDate = new Date(System.currentTimeMillis());

        //包装
        User user = new User();
        user.setUserCode(userCode);
        user.setUserName(userName);
        user.setUserPassword(userPassword);
        user.setGender(gender);
        try {
            user.setBirthday(new SimpleDateFormat("yyyy-MM-dd").parse(birthday));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        user.setPhone(phone);
        user.setAddress(address);
        user.setUserRole(userRole);
        user.setCreatedBy(createdBy);
        user.setCreationDate(creationDate);

        //调用service
        UserServiceImpl userService = new UserServiceImpl();
        boolean result = userService.addUser(user);


        if(result){
            try {
                resp.sendRedirect(req.getContextPath()+"/jsp/user.do?method=query");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            try {
                req.getRequestDispatcher("useradd.jsp").forward(req, resp);
            } catch (ServletException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

    //删
    private void deleteUser(HttpServletRequest req, HttpServletResponse resp) {
        //获取请求参数
        String userId = req.getParameter("uid");
        //万能的map:结果集
        Map<String,String> resultMap = new HashMap<>();


        //调用service
        UserServiceImpl userService = new UserServiceImpl();
        //先查询id存不存在
        int result = userService.deleteUser(userId);

        if(result<0){
            resultMap.put("delResult","notexist");
        }else if (result==0){
            resultMap.put("delResult","false");
        }else {
            resultMap.put("delResult","true");
        }

        try {
            resp.setContentType("application/json");
            PrintWriter writer = resp.getWriter();
            writer.write(JSONArray.toJSONString(resultMap));
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //查
    private void userQuery(HttpServletRequest req, HttpServletResponse resp) {
        //查询用户列表
        //从前端获取数据：查询的用户名、用户角色、跳转的页面数
        String queryUserName = req.getParameter("queryname");
        String tempUserRole = req.getParameter("queryUserRole");
        String pageIndex = req.getParameter("pageIndex");
        int queryUserRole = 0;

        //第一次走这个请求，一定是第一页，页面大小固定
        //把这些写到配置文件，方便修改
        int pageSize = 5;
        int currentPageNo = 1;

        if (queryUserName==null){
            queryUserName="";
        }
        if (tempUserRole!=null&&!tempUserRole.equals("")){
            //给查询赋值，0，1，2，3
            queryUserRole = Integer.parseInt(tempUserRole);
        }
        if(pageIndex!=null){
            currentPageNo=Integer.parseInt(pageIndex);
        }

        //获取用户列表
        UserServiceImpl userService = new UserServiceImpl();
        List<User> userList = null;

        //获取用户总数(用于分页)
        int userCounts = userService.getUserCounts(queryUserName, queryUserRole);
        //设置当前页和页面容量
        PageSupport pageSupport = new PageSupport();
        pageSupport.setCurrentPageNo(currentPageNo);
        pageSupport.setPageSize(pageSize);
        //设置总页数(传入用户总数并计算)
        pageSupport.setTotalCount(userCounts);
        //获取总页数
        int totalPageCount = pageSupport.getTotalPageCount();

        //控制首页和尾页
        if (currentPageNo<1){
            currentPageNo=1;
        }else if (currentPageNo>totalPageCount){
            currentPageNo=totalPageCount;
        }

        //获取用户列表展示
        userList = userService.getUserList(queryUserName,queryUserRole,currentPageNo,pageSize);
        req.setAttribute("userList",userList);

        RoleServiceImpl roleService = new RoleServiceImpl();
        List<Role> roleList = roleService.getRoleList();
        req.setAttribute("roleList",roleList);
        req.setAttribute("totalCount",userCounts);
        req.setAttribute("currentPageNo",currentPageNo);
        req.setAttribute("totalPageCount",totalPageCount);
        req.setAttribute("queryUserName",queryUserName);
        req.setAttribute("queryUserRole",queryUserRole);

        //返回前端
        try {
            req.getRequestDispatcher("userlist.jsp").forward(req,resp);
        } catch (ServletException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //改
    private void modifyUser(HttpServletRequest req, HttpServletResponse resp) {
        //获取请求参数
        int id = Integer.valueOf(req.getParameter("uid"));
        String userName = req.getParameter("userName");
        int gender = Integer.parseInt(req.getParameter("gender"));
        String birthday = req.getParameter("birthday");
        String phone = req.getParameter("phone");
        String address = req.getParameter("address");
        int userRole = Integer.parseInt(req.getParameter("userRole"));

        //包装
        User user = new User();
        user.setId(id);
        user.setUserName(userName);
        user.setGender(gender);
        try {
            user.setBirthday(new SimpleDateFormat("yyyy-MM-dd").parse(birthday));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        user.setPhone(phone);
        user.setAddress(address);
        user.setUserRole(userRole);

        //调用service
        UserServiceImpl userService = new UserServiceImpl();
        boolean result = userService.modifyUser(user);


        if(result){
            try {
                resp.sendRedirect(req.getContextPath()+"/jsp/user.do?method=query");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            try {
                req.getRequestDispatcher("usermodify.jsp").forward(req, resp);
            } catch (ServletException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    //获取用户信息并且重定向
    private void modifyRedirect(HttpServletRequest req, HttpServletResponse resp) {
        UserServiceImpl userService = new UserServiceImpl();
        User user = new User();
        String userId = req.getParameter("uid");
        user = userService.getUserId(userId);

        req.setAttribute("user",user);
        try {
            req.getRequestDispatcher("usermodify.jsp").forward(req,resp);
        } catch (ServletException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //角色查看页面
    private void viewUser(HttpServletRequest req, HttpServletResponse resp) {
        UserServiceImpl userService = new UserServiceImpl();
        User user = null;
        String userId = req.getParameter("uid");
        user = userService.getUserId(userId);
        System.out.println(userId);

        req.setAttribute("user",user);
        try {
            req.getRequestDispatcher("userview.jsp").forward(req,resp);
        } catch (ServletException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req,resp);
    }
}
