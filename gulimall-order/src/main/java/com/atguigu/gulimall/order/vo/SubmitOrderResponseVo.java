package com.atguigu.gulimall.order.vo;

import com.atguigu.gulimall.order.entity.OmsOrderEntity;
import lombok.Data;

/**
 * <p>Title: SubmitOrderResponseVo</p>
 * Description：
 * date：2020/7/1 22:50
 */
@Data
public class SubmitOrderResponseVo {

	private OmsOrderEntity orderEntity;

	/**
	 * 错误状态码： 0----成功
	 */
	private Integer code;
}
