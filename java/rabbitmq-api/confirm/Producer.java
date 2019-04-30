package com.solozyx.rabbitmq.api.confirm;

import java.io.IOException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmListener;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class Producer {
	public static void main(String[] args) throws Exception {
		//1 创建ConnectionFactory
		ConnectionFactory connectionFactory = new ConnectionFactory();
		connectionFactory.setHost("192.168.11.76");
		connectionFactory.setPort(5672);
		connectionFactory.setVirtualHost("/");
		//2 获取Connection
		Connection connection = connectionFactory.newConnection();
		//3 通过Connection创建一个新的Channel
		Channel channel = connection.createChannel();

		//4 指定消息投递模式: 消息的确认模式
		channel.confirmSelect();
		// 交换机
		String exchangeName = "test_confirm_exchange";
		// 路由键
		String routingKey = "confirm.save";
		//5 发送消息
		String msg = "Hello RabbitMQ Send confirm message!";
		channel.basicPublish(exchangeName, routingKey, null, msg.getBytes());
		
		//6 添加一个确认监听
		// 生产者把消息送到MQ服务器,MQ服务器主动会送是否接收到生产者消息的应答
		channel.addConfirmListener(new ConfirmListener() {
			@Override
			public void handleNack(long deliveryTag, boolean multiple) throws IOException {
				// deliveryTag 关键的唯一的消息的标签 投递消息可以采用该参数 返回应答根据它确认到底
				// 有无成功投递
				// multiple 是否批量
				// 失败进入
				System.err.println("-------no ack!-----------");
			}
			@Override
			public void handleAck(long deliveryTag, boolean multiple) throws IOException {
				// 成功进入
				System.err.println("-------ack!-----------");
			}
		});
	}
}