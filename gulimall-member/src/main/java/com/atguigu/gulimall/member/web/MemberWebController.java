package com.atguigu.gulimall.member.web;

import com.alibaba.fastjson.JSON;
import com.atguigu.gulimall.member.feign.OrderFeginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.atguigu.common.utils.R;
import java.util.HashMap;

@Controller
public class MemberWebController {


    @Autowired
    OrderFeginService orderFeginService;


    @GetMapping("/memberOrder.html")
    public String memberOrderPage(@RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer pageNum, Model model){
        // 这里可以获取到支付宝给我们传来的所有数据
        // 查出当前登录用户的所有订单
        HashMap<String, Object> page = new HashMap<>();
        page.put("page", pageNum.toString());
        R r = orderFeginService.listWithItem(page);
        System.out.println(JSON.toJSONString(r));
        model.addAttribute("orders", r);
//		支付宝返回的页面数据111
//		System.out.println(r.get("page"));
        return "orderList";
    }
}
