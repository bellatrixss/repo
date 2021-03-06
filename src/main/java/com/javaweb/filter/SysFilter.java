package com.javaweb.filter;

import com.javaweb.pojo.User;
import com.javaweb.util.Constants;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class SysFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        //从session中获取用户
        User user = (User) request.getSession().getAttribute(Constants.USER_SESSION);

        if (user==null){//已经注销或者未登录
            response.sendRedirect(request.getContextPath()+"/error.jsp");
        }else {
            filterChain.doFilter(servletRequest,servletResponse);
        }


    }

    @Override
    public void destroy() {
    }
}
