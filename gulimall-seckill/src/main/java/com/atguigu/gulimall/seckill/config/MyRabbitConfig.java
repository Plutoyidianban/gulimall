package com.atguigu.gulimall.seckill.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.annotation.PostConstruct;

/**
 * <p>Title: MyRabbitConfig</p>
 * Description：配置序列化方式
 * date：2020/6/29 14:29
 */

@Slf4j
@Configuration
public class MyRabbitConfig {

	@Autowired
	private RabbitTemplate rabbitTemplate;

	@Primary
	@Bean
	public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory){
		RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
		this.rabbitTemplate = rabbitTemplate;
		rabbitTemplate.setMessageConverter(messageConverter());
		initRabbitTemplate();
		return rabbitTemplate;
	}

	@PostConstruct//MyRabbitConfig对象生成以后调用这个方法
	public void initRabbitTemplate(){

//		rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
//			@Override
//			public void confirm(CorrelationData correlationData, boolean ack, String cause) {
//				System.out.println("confirm...correlationData"+correlationData+"  ack="+ack+"=====cause="+cause);
//			}
//		});

		/**
		 * 		设置确认回调
		 *  correlationData: 消息的唯一id
		 *  ack： 消息是否成功收到
		 * 	cause：失败的原因
		 */
		rabbitTemplate.setConfirmCallback((correlationData, ack , cause) -> log.info("\n收到消息: " + correlationData + "\tack: " + ack + "\tcause： " + cause));

		/**
		 * 设置消息抵达队列回调：可以很明确的知道那些消息失败了，消息如果正确抵达，这个方法什么都不会调用，如果错误抵达了，那么该方法就会被调用。
		 *
		 * message: 投递失败的消息详细信息
		 * replyCode: 回复的状态码
		 * replyText: 回复的文本内容
		 * exchange: 当时这个发送给那个交换机
		 * routerKey: 当时这个消息用那个路由键
		 */
		rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routerKey) -> log.error("Fail Message [" + message + "]" + "\treplyCode: " + replyCode + "\treplyText:" + replyText + "\texchange:" + exchange + "\trouterKey:" + routerKey));
	}



	/**
	 * 这样配置就可以直接将消息序列化为JSON串了
	 * @return
	 */
	@Bean
	public MessageConverter messageConverter(){
		return new Jackson2JsonMessageConverter();
	}


}
