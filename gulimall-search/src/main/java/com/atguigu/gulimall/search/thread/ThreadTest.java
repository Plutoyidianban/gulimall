package com.atguigu.gulimall.search.thread;

import org.springframework.cache.annotation.Cacheable;

import java.util.concurrent.*;

public class ThreadTest {
    public static ExecutorService executor = Executors.newFixedThreadPool(10);

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        System.out.println("main....start..");
//        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
//            System.out.println("当前线程:" + Thread.currentThread().getId());
//            int i = 10 / 2;
//            System.out.println("线程处理的结果：" + i);
//        }, executor);
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            int i = 10/1;
            return i;
//            这里因为是future，相当于调用的是future的whenComplete方法；
        }, executor).thenApplyAsync((res) -> {
                    System.out.println("2222" + res);
                    return "hello" + res; }, executor);

        System.out.println("main....end.."+future.get());
    }



    public static void thread(String[] args) {
        executor.execute(new Runable01());

    }


   public static class Callable01 implements Callable<Integer>{

       @Override
       public Integer call() throws Exception {
           System.out.println("当前线程:"+Thread.currentThread().getId());
           int i=10/2;
           System.out.println("线程处理的结果："+i);
           return  i;
       }
   }

    public static  class Runable01 implements Runnable{
        @Override
        public void run() {
            System.out.println("当前线程:"+Thread.currentThread().getId());
            int i=10/2;
            System.out.println("线程处理的结果："+i);
        }
    }
}
