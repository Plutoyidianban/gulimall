package com.atguigu.gulimall.order;


import com.atguigu.gulimall.order.entity.OmsOrderEntity;
import com.atguigu.gulimall.order.entity.OmsOrderReturnReasonEntity;
import com.rabbitmq.client.AMQP;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.UUID;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class test {

    @Autowired
    AmqpAdmin amqpAdmin;

    @Autowired
    RabbitTemplate rabbitTemplate;



    @Test
    public void sendMessage(){
        for (int i = 0; i <10; i++) {
            if(i%2==0){
                OmsOrderReturnReasonEntity reasonEntity = new OmsOrderReturnReasonEntity();
                reasonEntity.setId(1L);
                reasonEntity.setCreateTime(new Date());
                reasonEntity.setName("哈哈哈"+i);

                rabbitTemplate.convertAndSend("hello-java-exchange","hello.java",reasonEntity);
                log.info("发送消息成功",reasonEntity);
            }else {
                OmsOrderEntity omsOrderEntity = new OmsOrderEntity();
                omsOrderEntity.setOrderSn(UUID.randomUUID().toString());
                rabbitTemplate.convertAndSend("hello-java-exchange","hello.java",omsOrderEntity);

            }

        }
    }

    /**
     * 创建交换机
     */
    @Test
    public void creatExchange(){
        DirectExchange directExchange = new DirectExchange("hello-java-exchange",true,false);
        amqpAdmin.declareExchange(directExchange);
        log.info("创建成功：hello-java-exchange");
    }

    @Test
    public void creatqueue(){
        Queue queue = new Queue("hello-queue", true, false, false);
        amqpAdmin.declareQueue(queue);
        log.info("Queue创建成功");
    }

    @Test
    public void creatBinding(){
        Binding binding = new Binding("hello-queue", Binding.DestinationType.QUEUE, "hello-java-exchange", "hello.java",null);
        amqpAdmin.declareBinding(binding);
        log.info("binding创建成功");
    }

}
