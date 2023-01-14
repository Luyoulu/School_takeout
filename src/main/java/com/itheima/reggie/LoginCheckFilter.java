package com.itheima.reggie;

import com.alibaba.fastjson.JSON;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(filterName = "loginCheckFilter",urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter {
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String requestURI = request.getRequestURI();
        log.info("拦截到"+requestURI);
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**",
                "/user/sendMsg",
                "/user/login"
        };
        boolean check = check(urls, requestURI);
        if (check){
            log.info("本次请求不需要处理");
            filterChain.doFilter(request,response);
            return;
        }
        Object employee = request.getSession().getAttribute("employee");
        Object user = request.getSession().getAttribute("user");
        if (employee!=null){
            BaseContext.setCurrentId((Long) employee);
            log.info("用户已登录,id为{}",employee);
            filterChain.doFilter(request,response);
            return;
        }
        if (user!=null){
            BaseContext.setCurrentId((Long) user);
            log.info("用户已登录，id为{}",user);
            filterChain.doFilter(request,response);
            return;
        }
        log.info("用户未登录");
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;



    }
    public boolean check(String[] urls,String url){
        for (String s : urls) {
            boolean match = PATH_MATCHER.match(s, url);
            if (match){return true;}
        }return false;
    }
}
