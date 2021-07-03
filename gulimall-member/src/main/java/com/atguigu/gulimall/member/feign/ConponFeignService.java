package com.atguigu.gulimall.member.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import com.atguigu.common.utils.R;
@FeignClient("gulimall-coupon")
public interface ConponFeignService {

    @RequestMapping("/coupon/coupon/member/list")
    public R menbercoupons();
}
