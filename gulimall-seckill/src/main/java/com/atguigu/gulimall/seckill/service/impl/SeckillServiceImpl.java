package com.atguigu.gulimall.seckill.service.impl;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.atguigu.common.constant.Productconstant;
import com.atguigu.common.to.mq.SecKillOrderTo;
import com.atguigu.common.utils.R;
import com.atguigu.common.vo.MemberRsepVo;
import com.atguigu.gulimall.seckill.fegin.CouponFeginService;
import com.atguigu.gulimall.seckill.fegin.ProductFeginService;
import com.atguigu.gulimall.seckill.interceptor.LoginUserInterceptor;
import com.atguigu.gulimall.seckill.service.SeckillService;
import com.atguigu.gulimall.seckill.to.SeckillSkuRedisTo;
import com.atguigu.gulimall.seckill.vo.SeckillSessionsWithSkus;
import com.atguigu.gulimall.seckill.vo.SeckillSkuVo;
import com.atguigu.gulimall.seckill.vo.SkuInfoVo;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import org.apache.commons.lang.StringUtils;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class SeckillServiceImpl implements SeckillService {

    @Autowired
   CouponFeginService couponFeginService;

    @Autowired
    ProductFeginService productFeginService;

   @Autowired
   StringRedisTemplate redisTemplate;

   @Autowired
   RedissonClient redissonClient;

   @Autowired
   RabbitTemplate rabbitTemplate;

    private final String SESSION_CACHE_PREFIX = "seckill:sessions:";
    private final String SKUKILL_CACHE_PREFIX = "seckill:skus:";

    private final String SKUSTOCK_SEMAPHONE = "seckill:stock:";

    /**
     *
     */
    @Override
    public void uploadSeckillSkuLastest3Days() {
        //扫描最近三天需要参与的秒杀活动
        R session = couponFeginService.getLates3DaySession();
        if(session.getCode()==0){
//            上架商品
            List<SeckillSessionsWithSkus> data = session.getData(new TypeReference<List<SeckillSessionsWithSkus>>() {
            });
//            缓存到redis
//  1 缓存活动信息
           saveSessionInfos(data);
//  2  缓存活动相关的商品信息
            saveSessionSkuInfos(data);

        }

    }

    /**
     * 返回当前可以参加秒杀的商品的信息
     * @return
     */
    @Override
    public List<SeckillSkuRedisTo> getCurrentSeckillSkus() {
//     1 确定当前时间属于哪个秒杀场次
        long time = new Date().getTime();
        Set<String> keys = redisTemplate.keys(SESSION_CACHE_PREFIX + "*");

        for(String key:keys){
          //seckill:sessions:1624866300000_1624896000000
            String replace = key.replace(SESSION_CACHE_PREFIX, "");
            String[] s = replace.split("_");
            long start = Long.parseLong(s[0]);
            long end = Long.parseLong(s[1]);

            if(time>=start&&time<=end){
//                获取当前场次的所有商品信息
                List<String> range = redisTemplate.opsForList().range(key, -100, 100);

                BoundHashOperations<String, String, Object> hashOps = redisTemplate.boundHashOps(SKUKILL_CACHE_PREFIX);

                List<Object> list = hashOps.multiGet(range);

                if(list!=null){
                    List<SeckillSkuRedisTo> collect = list.stream().map(item -> {
                        SeckillSkuRedisTo redisTo = new SeckillSkuRedisTo();
                        SeckillSkuRedisTo redis = JSON.parseObject((String) item, SeckillSkuRedisTo.class);
//                        redis.setRandomCode(null);
                        return redis;

                    }).collect(Collectors.toList());

                    return collect;
                }

                break;

            }


        }
        return null;


    }

    /**
     * 根据商品的skuid来查找商品的信息。
     * @param skuId
     * @return
     */
    @Override
    public SeckillSkuRedisTo getSkuSeckillInfo(Long skuId) {

        BoundHashOperations<String, String, String> hashOps = redisTemplate.boundHashOps(SKUKILL_CACHE_PREFIX);
        Set<String> keys = hashOps.keys();
        if(keys != null && keys.size() > 0){
            String regx = "\\d-" + skuId;
            for (String key : keys) {
                if(Pattern.matches(regx, key)){
                    String json = hashOps.get(key);
                    SeckillSkuRedisTo to = JSON.parseObject(json, SeckillSkuRedisTo.class);
                    // 处理一下随机码
                    long time = new Date().getTime();
                    System.out.println(time);

//                    if(time <= to.getStartTime() || time >= to.getEndTime()){
//                        to.setRandomCode(null);
//                        System.out.println("不在秒杀的时间里面。。。");
//                    }
                    return to;
                }
            }
        }
        return null;
    }

    @Override
    public String kill(String killId, String key, Integer num) {

        MemberRsepVo respVo = LoginUserInterceptor.loginUser.get();

//        1 获取当前商品的秒杀的详细信息
        BoundHashOperations<String, String, String> hashOps = redisTemplate.boundHashOps(SKUKILL_CACHE_PREFIX);
        String json = hashOps.get(killId);
        if(StringUtils.isEmpty(json)){

            return  null;
        }else {

            SeckillSkuRedisTo redis = JSON.parseObject(json, SeckillSkuRedisTo.class);
            //校验合法性
//          1  校验时间的合法性
            Long startTime = redis.getStartTime();
            Long endTime = redis.getEndTime();
            long ttl = endTime - startTime;
            long time = new Date().getTime();
            if(time>=startTime&&time<=endTime){
//           2 校验随机码和商品id
                String randomCode = redis.getRandomCode();
                String skuId = redis.getPromotionSessionId() + "-" + redis.getSkuId();
                if(randomCode.equals(key)&&killId.equals(skuId)){
//               3 验证购物的数量书否合理
                   if(num <= redis.getSeckillLimit().intValue()){
//                    4 验证这个人是否已经秒杀过,幂等性，如果已经秒杀过，就不需要再进行秒杀。秒杀过赳去占位置
                       String redisKey=respVo.getId()+"-"+skuId;
                       Boolean aBoolean = redisTemplate.opsForValue().setIfAbsent(redisKey, num.toString(), ttl, TimeUnit.MILLISECONDS);
                       if(aBoolean){
//                           如果占位成功，说明还没有买过
                           RSemaphore semaphore = redissonClient.getSemaphore(SKUSTOCK_SEMAPHONE + randomCode);

                           try {
                               boolean b = semaphore.tryAcquire(num, 100, TimeUnit.MILLISECONDS);
                               if(b){
                                   //                               秒杀成功，快速下单，发送消息给MQ
                                   String timeId = IdWorker.getTimeId();
                                   SecKillOrderTo orderTo = new SecKillOrderTo();
                                   orderTo.setOrderSn(timeId);
                                   orderTo.setMemberId(respVo.getId());
                                   orderTo.setPromotionSessionId(redis.getPromotionSessionId());
                                   orderTo.setSkuId(redis.getSkuId());
                                   orderTo.setSeckillPrice(redis.getSeckillPrice());
                                   rabbitTemplate.convertAndSend("order-event-exchange","order.seckill.order",orderTo);
                                   return  timeId;
                               }
                               return  null;
                           } catch (InterruptedException e) {
                              return null;
                           }


                       }else {
//                       如果占位失败，说明都已经买过了

                       }

                   }
                }else {

                    return  null;
                }


            }else {


            }

        }

         return  null;
    }


    private void saveSessionInfos(List<SeckillSessionsWithSkus> sessions){

        if(sessions != null){
            sessions.stream().forEach(session -> {
                long startTime = session.getStartTime().getTime();

                long endTime = session.getEndTime().getTime();
                String key = SESSION_CACHE_PREFIX + startTime + "_" + endTime;
                Boolean hasKey = redisTemplate.hasKey(key);
                if(!hasKey){
                    // 获取所有商品id
                    List<String> collect = session.getRelationSkus().stream().map(item -> item.getPromotionSessionId() + "-" + item.getSkuId()).collect(Collectors.toList());
                    // 缓存活动信息
                    redisTemplate.opsForList().leftPushAll(key, collect);
                    System.out.println("缓存活动对象");
                }
            });
        }

    }

    private void saveSessionSkuInfos(List<SeckillSessionsWithSkus> sessions){
        if(sessions != null){
            sessions.stream().forEach(session -> {
                BoundHashOperations<String, Object, Object> ops = redisTemplate.boundHashOps(SKUKILL_CACHE_PREFIX);
                session.getRelationSkus().stream().forEach(seckillSkuVo -> {
                    // 1.商品的随机码
                    String randomCode = UUID.randomUUID().toString().replace("-", "");
                    if(!ops.hasKey(seckillSkuVo.getPromotionSessionId() + "-" + seckillSkuVo.getSkuId())){
                        // 2.缓存商品
                        SeckillSkuRedisTo redisTo = new SeckillSkuRedisTo();
                        BeanUtils.copyProperties(seckillSkuVo, redisTo);
                        // 3.sku的基本数据 sku的秒杀信息
                        R info = productFeginService.skuInfo(seckillSkuVo.getSkuId());
                        if(info.getCode() == 0){
                            SkuInfoVo skuInfo = info.getData("skuInfo", new TypeReference<SkuInfoVo>() {});
                            redisTo.setSkuInfoVo(skuInfo);
                        }
                        // 4.设置当前商品的秒杀信息
                        redisTo.setStartTime(session.getStartTime().getTime());
                        redisTo.setEndTime(session.getEndTime().getTime());

                        // 随机码只有在商品开始的那一刻才暴露出来
                        redisTo.setRandomCode(randomCode);

                        // 5.使用库存作为分布式信号量  限流
                        RSemaphore semaphore = redissonClient.getSemaphore(SKUSTOCK_SEMAPHONE + randomCode);
                        semaphore.trySetPermits(seckillSkuVo.getSeckillCount().intValue());

                        ops.put(seckillSkuVo.getPromotionSessionId() + "-" + seckillSkuVo.getSkuId(), JSON.toJSONString(redisTo));
                        // 如果当前这个场次的商品库存已经上架就不需要上架了
                        System.out.println("缓存活动相关的商品信息");
                    }
                });
            });
        }

    }


}
