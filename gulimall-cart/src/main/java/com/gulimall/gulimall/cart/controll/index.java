package com.gulimall.gulimall.cart.controll;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller("index")
public class index {



    @GetMapping("/index")
    public String index(){

        return "success";
    }
}
