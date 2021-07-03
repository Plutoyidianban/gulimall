package com.atguigu.gulimall.product.web;

import com.atguigu.gulimall.product.entity.CategoryEntity;
import com.atguigu.gulimall.product.service.CategoryService;
import com.atguigu.gulimall.product.vo.Catelog2Vo;
import org.redisson.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
public class IndexController {

    @Autowired
    CategoryService categoryService;

    @Autowired
    RedissonClient redissonClient;

    @Autowired
   StringRedisTemplate stringRedisTemplate;

    @GetMapping({"/","/index.html"})
   public String indexPage(Model model){
     List<CategoryEntity>  categoryEntities= categoryService.getLevel1Category();

     model.addAttribute("categorys",categoryEntities);
       return "index";
   }

    @ResponseBody
    @RequestMapping("index/catalog.json")
    public Map<String, List<Catelog2Vo>> getCatlogJson() {

        Map<String, List<Catelog2Vo>> map = categoryService.getCatelogJson();
        return map;
    }

    @ResponseBody
    @GetMapping("/hello")
    public String hello(){
        RLock lock = redissonClient.getLock("mylock");
//        加锁
        lock.lock();
        try{
            System.out.println("加锁成功，执行业务。。。"+Thread.currentThread().getId());
            Thread.sleep(10000);
        }catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
//            解锁
            System.out.println("解锁成功...."+Thread.currentThread().getId());
            lock.unlock();
        }
        return  "hello";
    }

    @ResponseBody
    @GetMapping("/write")
    public String writeValue(){

        RReadWriteLock lock = redissonClient.getReadWriteLock("rw-lock");

        String s = "";
        RLock rLock = lock.writeLock();
        try {

            rLock.lock();
            s=UUID.randomUUID().toString();
            Thread.sleep(30000);
            stringRedisTemplate.opsForValue().set("writevalue",s);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            rLock.unlock();
        }
        return  s;
    }

    @ResponseBody
    @GetMapping("/read")
    public String readValue(){

        RReadWriteLock lock = redissonClient.getReadWriteLock("rw-lock");
        String s="";
        RLock rLock = lock.readLock();
        rLock.lock();


        try {
             s = stringRedisTemplate.opsForValue().get("writevalue");
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            rLock.unlock();
        }
        return  s;
    }

    /**
     * 信号量可以用来限流
     * @return
     */
    @ResponseBody
    @GetMapping("/park")
    public String park(){
        RSemaphore park = redissonClient.getSemaphore("park");
        try {
            park.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return  "ok";
    }

    @ResponseBody
    @GetMapping("/go")
    public String go(){
        RSemaphore park = redissonClient.getSemaphore("park");
        park.release();

        return  "ok";
    }

    @ResponseBody
    @GetMapping("/lockdoor")
    public String lockdoor(){
        RCountDownLatch door = redissonClient.getCountDownLatch("door");
        door.trySetCount(5);
        try {
            door.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return  "放假了";
    }

    @ResponseBody
    @GetMapping("/gogogo/{id}")
    public String gogogo(@PathVariable("id") Long id){
        RCountDownLatch door = redissonClient.getCountDownLatch("door");
        door.countDown();
        return  id+"班的人都走了。。。";
    }



}
