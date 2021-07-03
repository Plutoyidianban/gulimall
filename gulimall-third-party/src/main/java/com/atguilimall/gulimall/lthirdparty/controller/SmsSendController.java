package com.atguilimall.gulimall.lthirdparty.controller;

import com.atguilimall.gulimall.lthirdparty.component.SmsComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.atguigu.common.utils.R;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>Title: SmsSendController</p>
 * Description：
 * date：2020/6/25 14:53
 */
@RestController
@RequestMapping("/sms")
public class SmsSendController {

	@Autowired
	private SmsComponent smsComponent;

	/**
	 * 提供给别的服务进行调用的
	 */
	@GetMapping("/sendcode")
	public R sendCode(@RequestParam("phone") String phone, @RequestParam("code") String code){
     smsComponent.SendSmsCode(phone,code);
     return R.ok();
	}
}
