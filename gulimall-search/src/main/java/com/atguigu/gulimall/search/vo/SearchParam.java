package com.atguigu.gulimall.search.vo;


import java.util.List;
import lombok.Data;

@Data
public class SearchParam {

    private String keyword;

    private Long catalog3Id;

    private String sort;

    private Integer hasStock;

    private  String skuPrice;

    private List<Long> brandId;

    private  List<String> attrs;

    private Integer pageNum=1;

    private String _queryString;//原生的查询条件


}
