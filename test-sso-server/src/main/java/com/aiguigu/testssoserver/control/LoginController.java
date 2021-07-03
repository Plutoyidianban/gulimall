package com.aiguigu.testssoserver.control;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Controller
public class LoginController {

    @Autowired
    StringRedisTemplate stringRedisTemplate;


    @ResponseBody
    @GetMapping("/userInfo")
    public  String userinfo(@RequestParam("token") String token){
        String s = stringRedisTemplate.opsForValue().get(token);
        return s;
    }


    @GetMapping("/login.html")
    public  String loginPage(@RequestParam("redirect_url") String url, Model model,
                             @CookieValue(value = "sso_token",required = false) String sso_token){
        if(!StringUtils.isEmpty(sso_token)){
            return "redirect:"+url+"?token="+sso_token;
    }
        model.addAttribute("url",url);
        return "login";
    }

    @PostMapping("/doLogin")
    public  String doLogin(String username, String password, String url, HttpServletResponse response){

        if(!StringUtils.isEmpty(username)&&!StringUtils.isEmpty(password)&&!StringUtils.isEmpty(url)){

            String uuid = UUID.randomUUID().toString().replace("-","");
            stringRedisTemplate.opsForValue().set(uuid,username);
            Cookie sso_token = new Cookie("sso_token", uuid);
            response.addCookie(sso_token);


            return "redirect:"+url+"?token="+uuid;
        }else {
            return "login";
        }


    }
}
