package com.atguigu.gulimall.product.vo;

import com.atguigu.gulimall.product.entity.SkuImagesEntity;
import com.atguigu.gulimall.product.entity.SkuInfoEntity;
import com.atguigu.gulimall.product.entity.SpuInfoDescEntity;
import lombok.Data;

import java.util.List;

@Data
public class SkuItemVo {

    /**
     * 基本信息
     */
    SkuInfoEntity info;

    boolean hasStock = true;

    /**
     * 图片信息
     */
    List<SkuImagesEntity> images;

    /**
     * 销售属性组合
     */
    List<SkuItemSaleAttrVo> saleAttr;

    /**
     * 介绍
     */
    SpuInfoDescEntity desc;

    /**
     * 参数规格信息
     */
    List<SpuItemAttrGroup> groupAttrs;

    /**
     * 秒杀信息
     */
    SeckillInfoVo seckillInfoVo;




//    @Data
//    public static  class  SpuItemAttrGroupVo{
//        private String groupName;
//        private List<SpuBaseAttrVo> attrs;
//    }
//
//
//    @Data
//    public static class SpuBaseAttrVo{
//        private String attrName;
//
//        private String attrValue;
//    }



}
