package com.atguigu.gulimall.order.controller;

        import com.atguigu.gulimall.order.entity.OmsOrderEntity;
        import com.atguigu.gulimall.order.entity.OmsOrderItemEntity;
		import com.atguigu.gulimall.order.entity.OmsOrderReturnReasonEntity;
		import org.springframework.amqp.rabbit.connection.CorrelationData;
        import org.springframework.amqp.rabbit.core.RabbitTemplate;
        import org.springframework.beans.factory.annotation.Autowired;
        import org.springframework.web.bind.annotation.GetMapping;
        import org.springframework.web.bind.annotation.RequestParam;
        import org.springframework.web.bind.annotation.RestController;

        import java.util.Date;
        import java.util.UUID;

/**
 * <p>Title: RabbitController</p>
 * Description：
 * date：2020/6/29 15:50
 */
@RestController
//@RequestMapping("rabbit")
public class RabbitController {

    @Autowired
    private RabbitTemplate rabbitTemplate;

//	@Value("${myRabbitmq.exchange}")
//	private String exchange;
//
//	@Value("${myRabbitmq.routeKey}")
//	private String routeKey;


	@GetMapping("/sendMQ")
	public String sendMQ(@RequestParam(value = "num", required = false, defaultValue = "10") Integer num){
		for (int i = 0; i < num; i++) {
			if(i % 2 == 0){
				OmsOrderEntity entity = new OmsOrderEntity();
				entity.setId(1L);
				entity.setCommentTime(new Date());
				entity.setCreateTime(new Date());
				entity.setConfirmStatus(0);
				entity.setAutoConfirmDay(1);
				entity.setGrowth(1);
				entity.setMemberId(12L);
				entity.setReceiverName("FIRE-" + i);
				rabbitTemplate.convertAndSend("hello-java-exchange", "hello.java", entity, new CorrelationData(UUID.randomUUID().toString().replace("-","")));
			}else {
				OmsOrderReturnReasonEntity reasonEntity = new OmsOrderReturnReasonEntity();
				reasonEntity.setId(1L);
				reasonEntity.setCreateTime(new Date());
				reasonEntity.setName("哈哈哈"+i);
				rabbitTemplate.convertAndSend("hello-java-exchange", "hello22.java", reasonEntity, new CorrelationData(UUID.randomUUID().toString().replace("-","")));
				// 测试消息发送失败
//				rabbitTemplate.convertAndSend(this.exchange, this.routeKey + "test", orderEntity);
			}
		}
		return "ok";
	}
}
