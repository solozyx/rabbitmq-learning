package com.solozyx.rabbitmq.api.ack;

import java.io.IOException;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

public class MyConsumer extends DefaultConsumer {
	private Channel channel ;
	
	public MyConsumer(Channel channel) {
		super(channel);
		this.channel = channel;
	}

	@Override
	public void handleDelivery(String consumerTag,
							   Envelope envelope,
							   AMQP.BasicProperties properties,
							   byte[] body) throws IOException {
		System.err.println("-----------consume message----------");
		System.err.println("body: " + new String(body));

		try {
			Thread.sleep(2000); // 休眠2秒
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		// 生产端投递消息 num = 0 1 2 3 4
		if((Integer)properties.getHeaders().get("num") == 0) {
			// deliveryTag
			// multiple 是否批量 false非批量
			// requeue 是否重回队列 NAck表示消费端消费MQ服务器推送的消息失败
			//  -  消费端是否把自己消费失败的消息扔回MQ服务器队列
			//  -  false 不重新投递回MQ服务器队列
			//  -  true 重新投递回MQ服务器队列
			//  -  num==0 的消息NAck 重回队列把num==0消费失败的消息重新添加到队列尾部
			channel.basicNack(envelope.getDeliveryTag(), false, true);
		} else {
			// deliveryTag
			// multiple
			channel.basicAck(envelope.getDeliveryTag(), false);
		}
	}
}