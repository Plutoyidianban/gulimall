package com.atguigu.gulimall.ware.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>Title: MyRabbitConfig</p>
 * Description：配置序列化方式
 * date：2020/6/29 14:29
 */
@Slf4j
@Configuration
public class MyRabbitConfig {



	@Bean
	public MessageConverter messageConverter(){
		return new Jackson2JsonMessageConverter();
	}

	//String name, boolean durable, boolean autoDelete, Map<String, Object> arguments

	@Bean
	public Exchange stockEventExchange(){
      return  new TopicExchange("stock-event-exchange",true,false);
	}



	/**
	 * String name, boolean durable, boolean exclusive, boolean autoDelete, @Nullable Map<String, Object> arguments
	 */
	@Bean
	public Queue stockReleaseQueue(){
		return new Queue("stock.release.stock.queue", true, false, false);
	}

	@Bean
	public Queue stockDelayQueue(){

		Map<String, Object> args = new HashMap<>();
		// 信死了 交给 [stock-event-exchange] 交换机
		args.put("x-dead-letter-exchange","stock-event-exchange");
		// 死信路由
		args.put("x-dead-letter-routing-key","stock.release");
		args.put("x-message-ttl", 120000);

		return new Queue("stock.delay.queue", true, false, false, args);
	}

	/**
	 * 普通队列的绑定关系
	 * String destination, DestinationType destinationType, String exchange, String routingKey, @Nullable Map<String, Object> arguments
	 */
	@Bean
	public Binding stockLockedReleaseBinding(){

		return new Binding("stock.release.stock.queue",Binding.DestinationType.QUEUE,"stock-event-exchange","stock.release" + ".#", null);
	}

	/**
	 * 延时队列的绑定关系
	 * String destination, DestinationType destinationType, String exchange, String routingKey, @Nullable Map<String, Object> arguments
	 */
	@Bean
	public Binding stockLockedBinding(){

		return new Binding("stock.delay.queue", Binding.DestinationType.QUEUE, "stock-event-exchange", "stock.locked", null);
	}





//	/**
//	 * rabbitMQ服务器的地址
//	 */
//	@Value("${spring.rabbitmq.addresses:192.168.1.128:5672}")
//	private String addresses;
//	/**
//	 * rabbitMQ用户名
//	 */
//	@Value("${spring.rabbitmq.username:guest}")
//	private String username;
//	/**
//	 * rabbitMQ密码
//	 */
//	@Value("${spring.rabbitmq.password:guest}")
//	private String password;
//	/**
//	 * rabbitMQ虚拟机 这里默认 /
//	 */
//	@Value("${spring.rabbitmq.virtual-host:/}")
//	private String virtualHost;
//
//	/**
//	 * 注册rabbitMQ的Connection
//	 *
//	 * @return
//	 */
//	@Bean
//	public ConnectionFactory connectionFactory() {
//		CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory();
//		cachingConnectionFactory.setAddresses(this.addresses);
//		cachingConnectionFactory.setUsername(this.username);
//		cachingConnectionFactory.setPassword(this.password);
//		cachingConnectionFactory.setVirtualHost(this.virtualHost);
//		return cachingConnectionFactory;
//	}
//
//	/**
//	 * 注册rabbitAdmin 方便管理
//	 *
//	 * @param connectionFactory
//	 * @return
//	 */
//	@Bean
//	public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
//		return new RabbitAdmin(connectionFactory);
//	}
//


//	@Autowired
//	private RabbitTemplate rabbitTemplate;

//	@Primary
//	@Bean
//	public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory){
//		RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
//		this.rabbitTemplate = rabbitTemplate;
//		rabbitTemplate.setMessageConverter(messageConverter());
//		initRabbitTemplate();
//		return rabbitTemplate;
//	}

	/**
	 * 这样配置就可以直接将消息序列化为JSON串了
	 * @return
	 */


	/**
	 * 1.设置确认回调： ConfirmCallback
	 * 先在配置文件中开启 publisher-confirms: true
	 * @PostConstruct: MyRabbitConfig对象创建完成以后 执行这个方法
	 *
	 *  2.消息抵达队列的确认回调
	 * 　	开启发送端消息抵达队列确认
	 *     publisher-returns: true
	 *     	只要抵达队列，以异步优先回调我们这个 returnconfirm
	 *     template:
	 *       mandatory: true
	 *	3.消费端确认(保证每一个消息被正确消费才可以broker删除消息)
	 *		1.默认是自动确认的 只要消息接收到 服务端就会移除这个消息
	 *
	 *		如何签收:
	 *			签收: channel.basicAck(deliveryTag, false);
	 *			拒签: channel.basicNack(deliveryTag, false,true);
	 *	配置文件中一定要加上这个配置
	 *		listener:
	 *       simple:
	 *         acknowledge-mode: manual
	 */
//	@PostConstruct//MyRabbitConfig对象生成以后调用这个方法
//	public void initRabbitTemplate(){

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
//		rabbitTemplate.setConfirmCallback((correlationData, ack , cause) -> log.info("\n收到消息: " + correlationData + "\tack: " + ack + "\tcause： " + cause));

		/**
		 * 设置消息抵达队列回调：可以很明确的知道那些消息失败了，消息如果正确抵达，这个方法什么都不会调用，如果错误抵达了，那么该方法就会被调用。
		 *
		 * message: 投递失败的消息详细信息
		 * replyCode: 回复的状态码
		 * replyText: 回复的文本内容
		 * exchange: 当时这个发送给那个交换机
		 * routerKey: 当时这个消息用那个路由键
		 */
//		rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routerKey) -> log.error("Fail Message [" + message + "]" + "\treplyCode: " + replyCode + "\treplyText:" + replyText + "\texchange:" + exchange + "\trouterKey:" + routerKey));
//	}
}
