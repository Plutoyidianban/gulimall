package com.atguigu.gulimall.order.listenser;


import com.atguigu.gulimall.order.entity.OmsOrderEntity;
import com.atguigu.gulimall.order.service.OmsOrderService;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RabbitListener(queues = "order.release.order.queue")
public class OrderCloseListener {

    @Autowired
    OmsOrderService orderService;

    @RabbitHandler
    public void listener(OmsOrderEntity entity, Channel channel, Message message) throws IOException {
        System.out.println("收到过期订单，准备关闭订单"+entity.getOrderSn());

        try {
            orderService.closeOrder(entity);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        } catch (Exception e) {
            channel.basicReject(message.getMessageProperties().getDeliveryTag(),true);
        }


    }

}
