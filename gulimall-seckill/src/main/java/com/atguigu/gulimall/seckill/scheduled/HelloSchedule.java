package com.atguigu.gulimall.seckill.scheduled;


import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 定时任务
 * 1  @EnableScheduling 开启spring的定时任务
 * 2 @Scheduled 开启一个定时任务
 *
 * 异步任务：
 *   1 @EnableAsync 开启异步任务的功能
 *   2  @Async      在想要执行的异步方法上面开启异步功能
 *
 */
@Slf4j
@Component
//@EnableScheduling
//@EnableAsync
public class HelloSchedule {

    /**
     * 1 spring中6位组成，不允许有第7位
     * 2 在周几的位置，1-7就代表周几
     * 3 定时任务不应该阻塞。默认是阻塞的。
     *    解决办法：
     *      异步任务：定时器任务异步执行
     *     使用异步加+定时器的任务来完成定时任务不阻塞的功能
     */
//    @Async
//    @Scheduled(cron = "* * * * * ?")
//      public void hello() throws InterruptedException {
//       log.info("hello");
//       Thread.sleep(3000);
//      }

}
