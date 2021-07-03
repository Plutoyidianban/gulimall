package com.atguigu.gulimall.ware.listener;

import com.atguigu.common.to.mq.OrderTo;
import com.atguigu.common.to.mq.StockLockedTo;
import com.atguigu.gulimall.ware.service.WareSkuService;
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
@RabbitListener(queues = "stock.release.stock.queue")
public class StockReleaseListenser {

  @Autowired
  WareSkuService wareSkuService;

    /**
     * 查询数据库关于这个订单的锁定信息，
     * 有：证明库存锁定成功了
     *     需要解锁（查看订单情况）：
     *         1  没有这个订单（说明订单回滚了），必须解锁。
     *         2  有这个订单（需要进一步查看订单状态）
     *              订单状态： 一、已取消（用户点了东西之后，把购物车的订单取消了），解锁库存
     *                        二、没取消（用户一直还在纠结买不买），不能解锁
     * 没有： 库存锁定失败，库存回滚了，这种情况无需解锁。
     * @param to
     * @param message
     */
    @RabbitHandler
    public void handleStockLockedRelease(StockLockedTo to, Message message, Channel channel) throws IOException {


        try {
            System.out.println("收到解锁库存之前....");
            wareSkuService.unlocked(to);
            System.out.println("没有解锁库存之后....");
            //解锁消息
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        } catch (Exception e) {
            //不解锁消息，重新放回消息队列里面
           channel.basicReject(message.getMessageProperties().getDeliveryTag(),true);
        }
    }

    /**
     * 防止因为网络的原因，订单的演示队列的时间大于库存的延时队列的时间，这里所以每次订单取消的时候应该提醒库存该解锁了。
     * @param orderTo
     * @param message
     * @param channel
     */
    @RabbitHandler
    public void handlerOrderCloseRelease(OrderTo orderTo,Message message,Channel channel) throws IOException {



        try {
            wareSkuService.unlockStock(orderTo);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        } catch (Exception e) {

            channel.basicReject(message.getMessageProperties().getDeliveryTag(),true);
        }


    }







}
