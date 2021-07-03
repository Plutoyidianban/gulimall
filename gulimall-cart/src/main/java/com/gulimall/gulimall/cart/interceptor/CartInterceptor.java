package com.gulimall.gulimall.cart.interceptor;

import com.atguigu.common.constant.AuthServerConstant;
import com.atguigu.common.constant.CartConstant;
import com.atguigu.common.utils.Constant;
import com.atguigu.common.vo.MemberRsepVo;
import com.gulimall.gulimall.cart.vo.UserInfoTo;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.UUID;

import static com.atguigu.common.constant.CartConstant.TEMP_USER_COOKIE_TIME_OUT;


public class CartInterceptor implements HandlerInterceptor {



    public static ThreadLocal<UserInfoTo> threadLocal=new ThreadLocal<>();


    /**
     * 会在目标方法执行之前进行拦截,拦截器主要是为了获取目标方法的key和name
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        UserInfoTo userInfoTo = new UserInfoTo();
        HttpSession session = request.getSession();
        MemberRsepVo member = (MemberRsepVo) session.getAttribute(AuthServerConstant.LOGIN_USER);
        if(member!=null){
            //用户登录了
           userInfoTo.setUserId(member.getId());
            userInfoTo.setUsername(member.getUsername());
        }
        //如果用户登陆了，要看携带的cookie里面是否包含有user-key
        Cookie[] cookies = request.getCookies();
            if(cookies!=null&&cookies.length>0){
                for(Cookie cookie:cookies){
                    String name=cookie.getName();
                    if(name.equals(CartConstant.TEMP_USER_COOKIE_NAME)){
                        userInfoTo.setUserKey(cookie.getValue());
                        userInfoTo.setTempUser(true);
                    }
                }
            }

//        如果没有临时用户，一定要创建临时用户
        if(StringUtils.isEmpty(userInfoTo.getUserKey())){
            String uuid = UUID.randomUUID().toString();
            userInfoTo.setUserKey(uuid);
        }
        threadLocal.set(userInfoTo);
        return  true;
    }

    /**
     * 执行完毕之后分配临时用户让浏览器保存
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        UserInfoTo userInfoTo = threadLocal.get();
//      如果浏览器是临时用户，那么就没有必要放cookie了，但是如果是第一次登录的页面的时候，就需要把cookie放进去了。
       if(!userInfoTo.isTempUser()){
           Cookie cookie = new Cookie(CartConstant.TEMP_USER_COOKIE_NAME,userInfoTo.getUserKey());

           cookie.setDomain("gulimall.com");
           cookie.setMaxAge(TEMP_USER_COOKIE_TIME_OUT);

           response.addCookie(cookie);
       }
    }
}
