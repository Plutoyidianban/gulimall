package com.atguigu.gulimall.order.listenser;


import com.atguigu.common.to.mq.SecKillOrderTo;
import com.atguigu.gulimall.order.entity.OmsOrderEntity;
import com.atguigu.gulimall.order.service.OmsOrderService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@Service
@RabbitListener(queues = "order.seckill.order.queue")
public class OrderSeckillListener {

    @Autowired
    OmsOrderService orderService;

    @RabbitHandler
    public void listener(SecKillOrderTo seckilOrder, Channel channel, Message message) throws IOException {

        try {
            log.info("开始创建秒杀单的详细信息。。。。。。。。");
            orderService.createSeckillOrder(seckilOrder);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        } catch (Exception e) {
            channel.basicReject(message.getMessageProperties().getDeliveryTag(),true);
        }


    }
}
