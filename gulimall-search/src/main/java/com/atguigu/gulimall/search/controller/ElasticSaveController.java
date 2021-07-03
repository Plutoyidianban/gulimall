package com.atguigu.gulimall.search.controller;

import com.atguigu.common.exception.BizCodeEnume;
import com.atguigu.common.to.es.SkuEsModel;
import com.atguigu.common.utils.R;
import com.atguigu.gulimall.search.service.ProductSaveService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

/**
 * <p>Title: ElasticSaveController</p>
 * Description：商品上架 Controller
 * date：2020/6/8 21:13
 */
@Slf4j
@RequestMapping("/search/save")
@RestController
public class ElasticSaveController {

	@Autowired
	ProductSaveService productSaveService;
	/**
	 * 上架商品
	 */
	@PostMapping("/product")
	public R productStatusUp(@RequestBody List<SkuEsModel> skuEsModels) {

		boolean status;
		try {
			status = productSaveService.productStatusUp(skuEsModels);
		} catch (IOException e) {
			log.error("ElasticSaveController商品上架错误: {}", e);
			return R.error(BizCodeEnume.PRODUCT_UP_EXCEPTION.getCode(), BizCodeEnume.PRODUCT_UP_EXCEPTION.getMsg());
		}
		if(!status){
			return R.ok();
		}
		return R.error(BizCodeEnume.PRODUCT_UP_EXCEPTION.getCode(), BizCodeEnume.PRODUCT_UP_EXCEPTION.getMsg());
	}
}
