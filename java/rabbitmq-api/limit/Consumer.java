package com.solozyx.rabbitmq.api.limit;

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

		String exchangeName = "test_qos_exchange";
		String queueName = "test_qos_queue";
		String routingKey = "qos.#";
		
		channel.exchangeDeclare(exchangeName, "topic", true, false, null);
		channel.queueDeclare(queueName, true, false, false, null);
		channel.queueBind(queueName, exchangeName, routingKey);

		// 0 对单条消息的大小不做限制
		// 1 消费端接收到1条消息 手动签收Ack后 MQ才能把消息继续发给消费端
		// false 限流应用在Consumer级别 而不是Channel级别
		channel.basicQos(0, 1, false);
		// 限流方式 autoAck设置为 false 必须手动签收Ack才能实现消费端限流
		channel.basicConsume(queueName, false, new MyConsumer(channel));
	}
}