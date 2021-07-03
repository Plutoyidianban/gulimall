package com.atguigu.gulimall.order.web;

import com.alipay.api.AlipayApiException;
import com.atguigu.gulimall.order.config.AlipayTemplate;
import com.atguigu.gulimall.order.service.OmsOrderService;
import com.atguigu.gulimall.order.vo.PayVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@Controller
public class PayOrderController {

    @Autowired
    AlipayTemplate alipayTemplate;

    @Autowired
    OmsOrderService orderService;

    @ResponseBody
    @GetMapping(value = "payOrder",produces = "text/html")
     public String payOrder(@RequestParam("orderSn") String orderSn) throws AlipayApiException {

         PayVo payVo= orderService.getOrderPay(orderSn);
        String pay = alipayTemplate.pay(payVo);
        log.info("\n"+payVo);
        return pay;

     }

}
