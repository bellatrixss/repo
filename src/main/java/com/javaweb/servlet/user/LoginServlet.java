package com.javaweb.servlet.user;

import com.javaweb.pojo.User;
import com.javaweb.service.user.UserService;
import com.javaweb.service.user.UserServiceImpl;
import com.javaweb.util.Constants;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class LoginServlet extends HttpServlet {
    //控制层：调用业务层
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //获取用户名和密码
        String userCode = (String) req.getParameter("userCode");
        String userPassword = (String) req.getParameter("userPassword");


        //和数据库的用户进行对比，调用业务层
        UserService service = new UserServiceImpl();
        User user = service.login(userCode, userPassword);

        if (user!=null){
            //保存用户信息到session
            req.getSession().setAttribute(Constants.USER_SESSION,user);
            //跳转到主页
            resp.sendRedirect("jsp/frame.jsp");
        }else {
            //转发到登陆页面，顺便提示用户或者密码错误
            req.setAttribute("error","用户名或者密码不正确");
            req.getRequestDispatcher("login.jsp").forward(req,resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req,resp);
    }
}
