package com.solozyx.rabbitmq.api.dlx;

import java.util.HashMap;
import java.util.Map;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.QueueingConsumer.Delivery;

public class Consumer {
	public static void main(String[] args) throws Exception {
		ConnectionFactory connectionFactory = new ConnectionFactory();
		connectionFactory.setHost("192.168.11.76");
		connectionFactory.setPort(5672);
		connectionFactory.setVirtualHost("/");
		Connection connection = connectionFactory.newConnection();
		Channel channel = connection.createChannel();
		
		// 普通的交换机 和 队列 以及路由 不是死信队列的配置
		String exchangeName = "test_dlx_exchange";
		String routingKey = "dlx.#";
		String queueName = "test_dlx_queue";
		channel.exchangeDeclare(exchangeName, "topic", true, false, null);

		Map<String, Object> agruments = new HashMap<String, Object>();
		// 声明 dlx.exchange 是死信队列
		agruments.put("x-dead-letter-exchange", "dlx.exchange");
		// 这个agruments属性,要设置到声明队列上
		channel.queueDeclare(queueName, true, false, false, agruments);
		channel.queueBind(queueName, exchangeName, routingKey);
		
		// 死信队列的声明:
		// 交换机名称 交换机类型 持久化 非自动删除 扩展参数空
		channel.exchangeDeclare("dlx.exchange", "topic", true, false, null);
		// 队列名称 持久化 非独占exclusive 非自动删除 扩展参数空
		channel.queueDeclare("dlx.queue", true, false, false, null);
		// 队列 交换机 路由键#表示匹配所有消息都能路由到死信队列
		channel.queueBind("dlx.queue", "dlx.exchange", "#");

		// 是否自动签收 无所谓
		channel.basicConsume(queueName, true, new MyConsumer(channel));
	}
}