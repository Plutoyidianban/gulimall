package com.atguigu.gulimall.order.web;

import com.atguigu.gulimall.order.entity.OmsOrderEntity;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HelloControl {

    @Autowired
    RabbitTemplate rabbitTemplate;

    @ResponseBody
    @GetMapping("/test/createOrder")
     public  String createOrderTest(){
        OmsOrderEntity orderEntity = new OmsOrderEntity();
        orderEntity.setOrderSn("123");

        rabbitTemplate.convertAndSend("order-event-exchange","order.create.order",orderEntity);
        return  "ok";
    }



    @GetMapping("/{page}.html")
    public String listPage(@PathVariable("page") String page){

        return page;
    }

}
