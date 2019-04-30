package com.solozyx.rabbitmq.api.returnlistener;

import java.io.IOException;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.ReturnListener;
import com.rabbitmq.client.AMQP.BasicProperties;

public class Producer {
	public static void main(String[] args) throws Exception {
		ConnectionFactory connectionFactory = new ConnectionFactory();
		connectionFactory.setHost("192.168.11.76");
		connectionFactory.setPort(5672);
		connectionFactory.setVirtualHost("/");
		Connection connection = connectionFactory.newConnection();
		Channel channel = connection.createChannel();
		
		String exchange = "test_return_exchange";
		String routingKey = "return.save";
		String routingKeyError = "abc.save";
		
		String msg = "Hello RabbitMQ Return Message";

		channel.addReturnListener(new ReturnListener() {
			@Override
			public void handleReturn(int replyCode,
									 String replyText,
									 String exchange,
									 String routingKey,
									 AMQP.BasicProperties properties,
									 byte[] body) throws IOException {
				System.err.println("---------handle  return----------");
				//响应码
				System.err.println("replyCode: " + replyCode);
				//响应文本
				System.err.println("replyText: " + replyText);
				//交换机
				System.err.println("exchange: " + exchange);
				//路由键
				System.err.println("routingKey: " + routingKey);
				//消息属性
				System.err.println("properties: " + properties);
				//消息体
				System.err.println("body: " + new String(body));
			}
		});
		
		// 交换机 路由键 启用mandatory返回错误消息投递Return机制 消息属性 消息实体
		//channel.basicPublish(exchange, routingKey, true, null, msg.getBytes());
		channel.basicPublish(exchange, routingKeyError, true, null, msg.getBytes());
	}
}