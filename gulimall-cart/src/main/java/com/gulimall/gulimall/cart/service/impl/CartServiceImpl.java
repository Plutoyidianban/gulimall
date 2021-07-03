package com.gulimall.gulimall.cart.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.atguigu.common.utils.R;
import com.gulimall.gulimall.cart.fegin.ProductFeginService;
import com.gulimall.gulimall.cart.interceptor.CartInterceptor;
import com.gulimall.gulimall.cart.service.CartService;
import com.gulimall.gulimall.cart.vo.CartItem;
import com.gulimall.gulimall.cart.vo.SkuInfoVo;
import com.gulimall.gulimall.cart.vo.UserInfoTo;
import com.gulimall.gulimall.cart.vo.cart;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CartServiceImpl implements CartService {

    @Autowired
  StringRedisTemplate redisTemplate;

    @Autowired
    ProductFeginService productFeginService;

    private final  String CART_PREFIX="gulimall:cart";

    @Autowired
    ThreadPoolExecutor Executor;


    @Override
    public CartItem addToCart(Long skuId, Integer num) throws ExecutionException, InterruptedException {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();



        String res= (String)cartOps.get(skuId.toString());

        if(StringUtils.isEmpty(res)){
            CartItem cartItem = new CartItem();
//       购物车没有商品，修改数量。
            CompletableFuture<Void> getSkuInfoTask = CompletableFuture.runAsync(() -> {
                R skuinfo = productFeginService.getSkuinfo(skuId);
                // 1. 远程查询当前要添加的商品的信息
                SkuInfoVo sku = skuinfo.getData("skuInfo", new TypeReference<SkuInfoVo>() {
                });
                // 2. 添加新商品到购物车

                cartItem.setCount(num);
                cartItem.setCheck(true);
                cartItem.setImage(sku.getSkuDefaultImg());
                cartItem.setPrice(sku.getPrice());
                cartItem.setTitle(sku.getSkuTitle());
                cartItem.setSkuId(skuId);
            }, Executor);

//        3 远程查询sku组合信息,这里为了防止两个远程调用任务查的时候出现堵塞的情况，需要开一个线程池来进行异步编排查询。
            CompletableFuture<Void> getSkuSaleAttrValues = CompletableFuture.runAsync(() -> {
                List<String> values = productFeginService.getSkuSaleAttrValue(skuId);
                cartItem.setSkuAttr(values);

            }, Executor);
//      等待前面的两个现成的任务都完成的时候才可以，进行下面的步骤。
            CompletableFuture.allOf(getSkuInfoTask,getSkuSaleAttrValues).get();

            //      将数据放入redis里面。
//        这里需要先将cartItem里面的信息给序列化，这里的方法是将其转化为字符串了。
            String s = JSON.toJSONString(cartItem);
            cartOps.put(skuId.toString(),s);
            return  cartItem;

         }else {
//       购物车有此商品，修改数量。
            CartItem  cartItem= JSON.parseObject(res, CartItem.class);
            cartItem.setCount(cartItem.getCount()+num);

//            更新redis
            cartOps.put(skuId.toString(),JSON.toJSONString(cartItem));
            return  cartItem;
        }
    }

    @Override
    public CartItem getCartItem(Long skuId) {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        String s = (String) cartOps.get(skuId.toString());

        return  JSON.parseObject(s,CartItem.class);

    }

//    获取购物车
    @Override
    public cart getCart() throws ExecutionException, InterruptedException {

        cart cart = new cart();
        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
        if(userInfoTo.getUserId()!=null){
//          login in
            String cartKey = CART_PREFIX+userInfoTo.getUserId();
//            2  如果临时购物车的数据还没有进行合并
            // 要处理将没有登陆的之前的网页购物车和登录之后的购物车的合并工作。
            String tempcartKey=CART_PREFIX+userInfoTo.getUserKey();
            List<CartItem> tempcartItems = getCartItems( tempcartKey);
           if(tempcartItems!=null){
               //  临时车有数据需要合并
               for(CartItem item:tempcartItems){
                   addToCart(item.getSkuId(),item.getCount());
               }
           }
//           获取登录后的购物车数据，同时合并之前没有登陆的购物车数据
            List<CartItem> cartItems = getCartItems(cartKey);
           cart.setItems(cartItems);

            clearCart(tempcartKey);

        }else {
//            no login in
           String cartKey = CART_PREFIX+userInfoTo.getUserKey();
            List<CartItem> cartItems = getCartItems(cartKey);
            cart.setItems(cartItems);

        }
          return  cart;
        }



    /**
     * 获取到我们要操作的购物车
     * @return
     */
    private  BoundHashOperations<String, Object, Object> getCartOps() {
        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();

        String cartKey="";

        if(userInfoTo.getUserId()!=null){
//            用户登陆了
            cartKey=CART_PREFIX+userInfoTo.getUserId();
        }else {
//            用户没有登陆，采用临时用户登录
            cartKey=CART_PREFIX+userInfoTo.getUserKey();
        }

//        绑定一个Hash操作，使得以后所有对redis的操作都是针对这个key的操作。
        BoundHashOperations<String, Object, Object> operations = redisTemplate.boundHashOps(cartKey);

        return operations;
    }

    /**
     * 通过购物车的key来获取购物车存在redis里面的值。
     * @param cartKey
     * @return
     */
    private List<CartItem> getCartItems(String cartKey) {
        BoundHashOperations<String, Object, Object> hashOps = redisTemplate.boundHashOps(cartKey);
        List<Object> values = hashOps.values();
        if (values != null && values.size() > 0) {
            List<CartItem> collect = values.stream().map((obj) -> {
                String str = (String) obj;
                CartItem cartItem = JSON.parseObject(str, CartItem.class);
                return cartItem;
            }).collect(Collectors.toList());
            return collect;
        }else {
            return  null;
        }
    }

    /**
     * 清空购物车
     * @param cartKey
     */
    @Override
    public void clearCart(String cartKey){
         redisTemplate.delete(cartKey);
    }

    @Override
    public void checkItem(Long skuId, Integer check) {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        CartItem cartItem = getCartItem(skuId);
        cartItem.setCheck(check==1?true:false);
        String s = JSON.toJSONString(cartItem);
        cartOps.put(skuId.toString(),s);
    }

    @Override
    public void changeItemCount(Long skuId, Integer num) {
        CartItem cartItem = getCartItem(skuId);
        cartItem.setCount(num);


        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        cartOps.put(skuId.toString(),JSON.toJSONString(cartItem));

    }

    @Override
    public void deleteItem(Long skuId) {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        cartOps.delete(skuId.toString());
    }

    @Override
    public List<CartItem> getUserCartItems() {
        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
        if(userInfoTo.getUserId() == null){
            return null;
        }else{
            String cartKey = CART_PREFIX + userInfoTo.getUserId();
            List<CartItem> cartItems = getCartItems(cartKey);
            // 获取所有被选中的购物项
            List<CartItem> collect = cartItems.stream().filter(item -> item.getCheck()).map(item -> {
                try {
                    R r = productFeginService.getprice(item.getSkuId());
                    String price = (String) r.get("data");
                    item.setPrice(new BigDecimal(price));
                } catch (Exception e) {
                    log.warn("远程查询商品价格出错 [商品服务未启动]");
                }
                return item;
            }).collect(Collectors.toList());
            return collect;
        }


    }
}
