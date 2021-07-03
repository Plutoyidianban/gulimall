package com.atguigu.gulimall.order.interceptor;

import com.atguigu.common.constant.AuthServerConstant;
import com.atguigu.common.vo.MemberRsepVo;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
////SpringMVC自带的拦截器功能，需要在config包下面配置实现WebMvcConfigurer接口的addInterceptor方法
@Component
public class LoginUserInterceptor implements HandlerInterceptor {

    public static ThreadLocal<MemberRsepVo> loginUser=new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        //放行查询订单的order/status方法
        String uri = request.getRequestURI();
        AntPathMatcher antPathMatcher = new AntPathMatcher();
        boolean match = antPathMatcher.match("/order/omsorder/status/**", uri);
        boolean match1 = antPathMatcher.match("/payed/notify", uri);
        if(match||match1){
            return  true;
        }


        MemberRsepVo attribute = (MemberRsepVo) request.getSession().getAttribute(AuthServerConstant.LOGIN_USER);
        if(attribute!=null){
//登陆了,将loginUser存入到ThreadLocal里面去。
            loginUser.set(attribute);
            return true;
        }else {
//            没登陆就去登录。
             request.getSession().setAttribute("msg","请先进行登录");
             response.sendRedirect("http://auth.gulimall.com/login.html");

            return  false;
        }

    }
}
