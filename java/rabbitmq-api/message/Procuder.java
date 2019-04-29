package com.solozyx.rabbitmq.api.message;

import java.util.HashMap;
import java.util.Map;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class Procuder {
	public static void main(String[] args) throws Exception {
		//1 创建一个ConnectionFactory, 并进行配置
		ConnectionFactory connectionFactory = new ConnectionFactory();
		connectionFactory.setHost("192.168.11.76");
		connectionFactory.setPort(5672);
		connectionFactory.setVirtualHost("/");
		//2 通过连接工厂创建连接
		Connection connection = connectionFactory.newConnection();
		//3 通过connection创建一个Channel
		Channel channel = connection.createChannel();

		// 发送带有附加属性的消息
		// 自定义消息属性
		Map<String, Object> headers = new HashMap<>();
		headers.put("my1", "111");
		headers.put("my2", "222");
		
		// 链式编程
		AMQP.BasicProperties properties = new AMQP.BasicProperties.Builder()
				// 1 非持久化投递 消息发到服务器,假设服务器重启,消息没被消费者消费,消息会丢失
				// 2 持久化投递 消息发到服务器,假设服务器重启,消息不会丢失
				.deliveryMode(2)
				// 字符集
				.contentEncoding("UTF-8")
				// 过期时间 10秒 消息没被消费者消费会被自动删除
				.expiration("10000")
				.headers(headers)
				.build();
		
		//4 通过Channel发送数据
		for(int i=0; i < 5; i++){
			String msg = "Hello RabbitMQ!";
			//1 exchange   2 routingKey
			channel.basicPublish("", "test001", properties, msg.getBytes());
		}

		//5 记得要关闭相关的连接
		channel.close();
		connection.close();
	}
}