package com.gulimall.gulimall.cart.controll;

import com.gulimall.gulimall.cart.interceptor.CartInterceptor;
import com.gulimall.gulimall.cart.service.CartService;
import com.gulimall.gulimall.cart.vo.CartItem;
import com.gulimall.gulimall.cart.vo.UserInfoTo;
import com.gulimall.gulimall.cart.vo.cart;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.concurrent.ExecutionException;
import java.util.List;

@Slf4j
@Controller
public class CartController {

    @Autowired
    CartService cartService;

    @ResponseBody
    @GetMapping("/currentUserCartItem")
    public List<CartItem> getCurrentUserCartItem(){

        return cartService.getUserCartItems();
    }


    /**
     * 浏览器有一个cookie：user-key 标识用户身份 一个月后过期
     * 每次访问都会带上这个 user-key
     * 如果没有临时用户 还要帮忙创建一个,
     * 浏览器以后保存，每次访问都会带上这个cookie。
     *
     * 登陆了,自动携带seesion
     * 没有登陆，则看cookie里面有没有携带user-key
     *
     * 通过ThreadLocal来共享同一个线程的数据。然后通过get方法，把它取出来。
     */
    @GetMapping("/cart.html")
    public  String cartListPage(Model model) throws ExecutionException, InterruptedException {

//获取购物车
        cart cart=cartService.getCart();

        model.addAttribute("cart",cart);
        return "cartList";
    }

    /**
     * 勾选购物项的时候更改redis中check项的值，来动态刷新。
     * @param skuId
     * @param check
     * @return
     */
    @GetMapping("/checkItem.html")
    public String checkItem(@RequestParam("skuId") Long skuId,
                            @RequestParam("check") Integer check){
        cartService.checkItem(skuId,check);

        return "redirect:http://cart.gulimall.com/cart.html";
    }


    @GetMapping("/deleteItem")
    public String deleteItem(@RequestParam("skuId") Long skuId){
        cartService.deleteItem(skuId);
        return "redirect:http://cart.gulimall.com/cart.html";
    }

    /**\
     * 修改购物项数量
     * @param skuId
     * @param num
     * @return
     */
    @GetMapping("/countItem")
    public String countItem(@RequestParam("skuId") Long skuId, @RequestParam("num") Integer num){
        cartService.changeItemCount(skuId, num);
        return "redirect:http://cart.gulimall.com/cart.html";
    }

    /**
     * 添加商品到购物车,
     * @return
     */
    @GetMapping("addToCart")
    public  String addToCart(@RequestParam("skuId") Long skuId,
                             @RequestParam("num") Integer num,
                              RedirectAttributes redirectAttributes) throws ExecutionException, InterruptedException {
        cartService.addToCart(skuId, num);
//        重定向携带数据。
        redirectAttributes.addAttribute("skuId", skuId);
        // 重定向到成功页面
        return "redirect:http://cart.gulimall.com/addToCartSuccess.html";
    }

    /**
     * 这个函数的主要作用是addToCart()函数的重定向函数，目的是为了防止重复刷新购物车页面时，出现重复提交的误操作。
     * @param skuId
     * @param model
     * @return
     */
    @GetMapping("/addToCartSuccess.html")
    public String addToCartSuccessPage(@RequestParam(value = "skuId",required = false) Object skuId, Model model){
        CartItem cartItem = null;
        // 然后在查一遍 购物车
        if(skuId == null){
            model.addAttribute("item", null);
        }else{
            try {
                cartItem = cartService.getCartItem(Long.parseLong((String)skuId));
            } catch (NumberFormatException e) {
                log.warn("恶意操作! 页面传来非法字符.");
            }
            model.addAttribute("item", cartItem);
        }
        return "success";
    }

}
