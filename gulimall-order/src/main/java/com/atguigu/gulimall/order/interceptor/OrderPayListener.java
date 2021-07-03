package com.atguigu.gulimall.order.interceptor;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
public class OrderPayListener {


    @PostMapping("/payed/notify")
    public String handleAlipayed(HttpServletRequest request){

        Map<String, String[]> map = request.getParameterMap();

        for(String key:map.keySet()){
            String value = request.getParameter(key);
            System.out.println("参数名： "+key+"==>参数值 "+value);
        }

        System.out.println("支付宝通知的消息==="+map);

        return "success";

    }

}
