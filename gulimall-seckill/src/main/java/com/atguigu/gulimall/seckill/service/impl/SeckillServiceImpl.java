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
        //?????????????????????????????????????????????
        R session = couponFeginService.getLates3DaySession();
        if(session.getCode()==0){
//            ????????????
            List<SeckillSessionsWithSkus> data = session.getData(new TypeReference<List<SeckillSessionsWithSkus>>() {
            });
//            ?????????redis
//  1 ??????????????????
           saveSessionInfos(data);
//  2  ?????????????????????????????????
            saveSessionSkuInfos(data);

        }

    }

    /**
     * ????????????????????????????????????????????????
     * @return
     */
    @Override
    public List<SeckillSkuRedisTo> getCurrentSeckillSkus() {
//     1 ??????????????????????????????????????????
        long time = new Date().getTime();
        Set<String> keys = redisTemplate.keys(SESSION_CACHE_PREFIX + "*");

        for(String key:keys){
          //seckill:sessions:1624866300000_1624896000000
            String replace = key.replace(SESSION_CACHE_PREFIX, "");
            String[] s = replace.split("_");
            long start = Long.parseLong(s[0]);
            long end = Long.parseLong(s[1]);

            if(time>=start&&time<=end){
//                ???????????????????????????????????????
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
     * ???????????????skuid???????????????????????????
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
                    // ?????????????????????
                    long time = new Date().getTime();
                    System.out.println(time);

//                    if(time <= to.getStartTime() || time >= to.getEndTime()){
//                        to.setRandomCode(null);
//                        System.out.println("????????????????????????????????????");
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

//        1 ??????????????????????????????????????????
        BoundHashOperations<String, String, String> hashOps = redisTemplate.boundHashOps(SKUKILL_CACHE_PREFIX);
        String json = hashOps.get(killId);
        if(StringUtils.isEmpty(json)){

            return  null;
        }else {

            SeckillSkuRedisTo redis = JSON.parseObject(json, SeckillSkuRedisTo.class);
            //???????????????
//          1  ????????????????????????
            Long startTime = redis.getStartTime();
            Long endTime = redis.getEndTime();
            long ttl = endTime - startTime;
            long time = new Date().getTime();
            if(time>=startTime&&time<=endTime){
//           2 ????????????????????????id
                String randomCode = redis.getRandomCode();
                String skuId = redis.getPromotionSessionId() + "-" + redis.getSkuId();
                if(randomCode.equals(key)&&killId.equals(skuId)){
//               3 ?????????????????????????????????
                   if(num <= redis.getSeckillLimit().intValue()){
//                    4 ????????????????????????????????????,??????????????????????????????????????????????????????????????????????????????????????????
                       String redisKey=respVo.getId()+"-"+skuId;
                       Boolean aBoolean = redisTemplate.opsForValue().setIfAbsent(redisKey, num.toString(), ttl, TimeUnit.MILLISECONDS);
                       if(aBoolean){
//                           ??????????????????????????????????????????
                           RSemaphore semaphore = redissonClient.getSemaphore(SKUSTOCK_SEMAPHONE + randomCode);

                           try {
                               boolean b = semaphore.tryAcquire(num, 100, TimeUnit.MILLISECONDS);
                               if(b){
                                   //                               ?????????????????????????????????????????????MQ
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
//                       ?????????????????????????????????????????????

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
                    // ??????????????????id
                    List<String> collect = session.getRelationSkus().stream().map(item -> item.getPromotionSessionId() + "-" + item.getSkuId()).collect(Collectors.toList());
                    // ??????????????????
                    redisTemplate.opsForList().leftPushAll(key, collect);
                    System.out.println("??????????????????");
                }
            });
        }

    }

    private void saveSessionSkuInfos(List<SeckillSessionsWithSkus> sessions){
        if(sessions != null){
            sessions.stream().forEach(session -> {
                BoundHashOperations<String, Object, Object> ops = redisTemplate.boundHashOps(SKUKILL_CACHE_PREFIX);
                session.getRelationSkus().stream().forEach(seckillSkuVo -> {
                    // 1.??????????????????
                    String randomCode = UUID.randomUUID().toString().replace("-", "");
                    if(!ops.hasKey(seckillSkuVo.getPromotionSessionId() + "-" + seckillSkuVo.getSkuId())){
                        // 2.????????????
                        SeckillSkuRedisTo redisTo = new SeckillSkuRedisTo();
                        BeanUtils.copyProperties(seckillSkuVo, redisTo);
                        // 3.sku??????????????? sku???????????????
                        R info = productFeginService.skuInfo(seckillSkuVo.getSkuId());
                        if(info.getCode() == 0){
                            SkuInfoVo skuInfo = info.getData("skuInfo", new TypeReference<SkuInfoVo>() {});
                            redisTo.setSkuInfoVo(skuInfo);
                        }
                        // 4.?????????????????????????????????
                        redisTo.setStartTime(session.getStartTime().getTime());
                        redisTo.setEndTime(session.getEndTime().getTime());

                        // ?????????????????????????????????????????????????????????
                        redisTo.setRandomCode(randomCode);

                        // 5.????????????????????????????????????  ??????
                        RSemaphore semaphore = redissonClient.getSemaphore(SKUSTOCK_SEMAPHONE + randomCode);
                        semaphore.trySetPermits(seckillSkuVo.getSeckillCount().intValue());

                        ops.put(seckillSkuVo.getPromotionSessionId() + "-" + seckillSkuVo.getSkuId(), JSON.toJSONString(redisTo));
                        // ????????????????????????????????????????????????????????????????????????
                        System.out.println("?????????????????????????????????");
                    }
                });
            });
        }

    }


}
