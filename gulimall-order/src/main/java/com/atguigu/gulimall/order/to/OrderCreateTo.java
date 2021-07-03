package com.atguigu.gulimall.order.to;

import com.atguigu.gulimall.order.entity.OmsOrderEntity;
import com.atguigu.gulimall.order.entity.OmsOrderItemEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * <p>Title: OrderCreateTo</p>
 * Description：
 * date：2020/7/1 23:51
 */
@Data
public class OrderCreateTo {

	private OmsOrderEntity order;

	private List<OmsOrderItemEntity> orderItems;

	/**
	 * 订单计算的应付价格
	 */
	private BigDecimal payPrice;

	/**
	 * 运费
	 */
	private BigDecimal fare;
}
