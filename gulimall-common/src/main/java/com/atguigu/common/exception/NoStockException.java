package com.atguigu.common.exception;


public class NoStockException extends RuntimeException {

    private Long skuId;

    public NoStockException(String msg){
        super(msg + "号商品没有足够的库存了");
    }

    private Long getSkuId(){
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }
}
