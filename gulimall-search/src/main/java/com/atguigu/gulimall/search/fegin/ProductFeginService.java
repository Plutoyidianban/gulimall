package com.atguigu.gulimall.search.fegin;

import com.atguigu.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient("gulimall-product")
public interface ProductFeginService {

    @GetMapping("/product/attr/info/{attrId}")
    public R attrinfo(@PathVariable("attrId") Long attrId);

    @GetMapping("/product/brand/infos")
    public R brandsinfo(@RequestParam("brandIds") List<Long> brandIds);

}
