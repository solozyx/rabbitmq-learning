package com.solozyx.rabbitmq.api.dlx;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class Producer {
	public static void main(String[] args) throws Exception {
		ConnectionFactory connectionFactory = new ConnectionFactory();
		connectionFactory.setHost("192.168.11.76");
		connectionFactory.setPort(5672);
		connectionFactory.setVirtualHost("/");
		Connection connection = connectionFactory.newConnection();
		Channel channel = connection.createChannel();

		// 自定义交换机,不是死信队列,是普通正常交换机
		String exchange = "test_dlx_exchange";
		// 普通正常路由键
		String routingKey = "dlx.save";
		
		String msg = "Hello RabbitMQ DLX Message";
		for(int i = 0; i < 1; i++){
			AMQP.BasicProperties properties = new AMQP.BasicProperties.Builder()
					.deliveryMode(2)
					.contentEncoding("UTF-8")
					.expiration("10000")
					.build();
			channel.basicPublish(exchange, routingKey, true, properties, msg.getBytes());
		}
	}
}