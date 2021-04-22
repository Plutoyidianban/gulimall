package com.atguigu.gulimall.product;

import com.atguigu.gulimall.product.entity.BrandEntity;
import com.atguigu.gulimall.product.service.BrandService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;


import java.util.List;

import org.junit.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class GulimallProductApplicationTests {

    @Autowired
    BrandService brandService;

   @Test
    public void contextLoads() {
//        BrandEntity brandEntity = new BrandEntity();
//        brandEntity.setBrandId(1L);
//        brandEntity.setDescript("不合格");
//        brandEntity.setName("韦辉的小11");
//        brandService.save(brandEntity);
//        System.out.println("保存成功。。");
//       brandService.updateById(brandEntity);
//        System.out.println("保存成功。。");
       List<BrandEntity> list = brandService.list(new QueryWrapper<BrandEntity>().eq("brand_id", 1L));
        list.forEach((item)->{
            System.out.println(item);
        });


    }

}
