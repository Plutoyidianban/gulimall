package com.atguigu.gulimall.order.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * 针对远程调用服务的时候会出现请求头丢失的情况（因为feign客户端收到请求之后，会重新生成一个请求），解决办法是把老的请求的请求头设置给新的请求。
 */
@Configuration
public class GuliFeignConfig {

    @Bean("requestInterceptor")
    public RequestInterceptor requestInterceptor(){
        // Feign在远程调用之前都会先经过这个方法
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate template) {
                // RequestContextHolder拿到刚进来这个请求
                ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                if(attributes != null){
                    HttpServletRequest request = attributes.getRequest();
                    if(request != null){
                        // 同步请求头数据
                        String cookie = request.getHeader("Cookie");
                        // 给新请求同步Cookie
                        template.header("Cookie", cookie);
                    }
                }
            }
        };
    }
}
