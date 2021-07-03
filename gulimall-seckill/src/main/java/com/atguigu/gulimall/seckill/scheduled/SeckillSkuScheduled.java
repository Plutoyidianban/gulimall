package com.atguigu.gulimall.seckill.scheduled;

import com.atguigu.gulimall.seckill.service.SeckillService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;


/**
 * 秒杀商品的定时上架
 */
@Slf4j
@Service
public class SeckillSkuScheduled {

    @Autowired
    SeckillService seckillService;

    @Autowired
    RedissonClient redissonClient;

    private final String upload_lock="seckill:upload:lock";

//    todo 上架过程应该做成幂等性的
//    使用分布式锁先锁住服务
    @Scheduled(cron = "*/3 * * * * ?")
   public void  uploadSeckillSkuLastest3Days(){
        // 1 重复是上架就无需处理



        RLock lock = redissonClient.getLock(upload_lock);

        lock.lock(10, TimeUnit.SECONDS);
        try {

            seckillService.uploadSeckillSkuLastest3Days();
            log.info("上架秒杀商品的信息。。。。");
        } finally {
            lock.unlock();
        }


    }


}
