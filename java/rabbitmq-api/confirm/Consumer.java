package com.solozyx.rabbitmq.api.confirm;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.QueueingConsumer.Delivery;

public class Consumer {
	public static void main(String[] args) throws Exception {
		//1 创建ConnectionFactory
		ConnectionFactory connectionFactory = new ConnectionFactory();
		connectionFactory.setHost("192.168.11.76");
		connectionFactory.setPort(5672);
		connectionFactory.setVirtualHost("/");
		//2 获取Connection
		Connection connection = connectionFactory.newConnection();
		//3 通过Connection创建一个新的Channel 所有操作建立在channel上
		Channel channel = connection.createChannel();

		// 消费端不需要指定消息的投递模式
		String exchangeName = "test_confirm_exchange";
		// 生产端投递消息的路由键 "confirm.save"
		// # 匹配1个到多个单词 * 只匹配1个单词
		String routingKey = "confirm.#";
		String queueName = "test_confirm_queue";
		//4 声明交换机和队列 然后进行绑定设置, 最后制定路由Key
		// topic 进行持久化
		channel.exchangeDeclare(exchangeName, "topic", true);
		// 队列名 进行持久化 不是独占模式 不自动删除 没有扩展参数
		channel.queueDeclare(queueName, true, false, false, null);
		channel.queueBind(queueName, exchangeName, routingKey);
		
		//5 创建消费者 
		QueueingConsumer queueingConsumer = new QueueingConsumer(channel);
		// 消费模式 消费的队列 自动签收ACK 回调者
		channel.basicConsume(queueName, true, queueingConsumer);
		while(true){
			Delivery delivery = queueingConsumer.nextDelivery();
			String msg = new String(delivery.getBody());
			System.err.println("消费端: " + msg);
		}
	}
}