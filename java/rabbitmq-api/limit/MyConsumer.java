package com.solozyx.rabbitmq.api.limit;

import java.io.IOException;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

public class MyConsumer extends DefaultConsumer {

	// 定义成员变量
	private Channel channel ;
	
	public MyConsumer(Channel channel) {
		super(channel);
		// 接收channel
		this.channel = channel;
	}

	@Override
	public void handleDelivery(String consumerTag,
							   Envelope envelope,
							   AMQP.BasicProperties properties,
							   byte[] body) throws IOException {
		System.err.println("-----------consume message----------");
		System.err.println("consumerTag: " + consumerTag);
		System.err.println("envelope: " + envelope);
		System.err.println("properties: " + properties);
		System.err.println("body: " + new String(body));
		// deliveryTag 消息标签
		// false 非批量签收
		// 该方法会回送个MQ Broker服务器 Ack应答
		// 表示本个消息消费端处理完成 MQ服务器可以发送下1条数据了
		channel.basicAck(envelope.getDeliveryTag(), false);
	}
}
