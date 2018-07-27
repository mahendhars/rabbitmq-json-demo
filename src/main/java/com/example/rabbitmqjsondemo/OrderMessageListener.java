package com.example.rabbitmqjsondemo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class OrderMessageListener {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(OrderMessageListener.class);
	
	/*
	 * By simply adding @RabbitListener and defining which queue to listen 
	 * to we can create a Listener.
	 * Now if you send a message to orders-queue that should be consumed by 
	 * OrderMessageListener.processOrder() method and you should see the 
	 * log statement “Order Received: “.
	 * 
	 * Here default serialization mechanism converts the message object 
	 * into byte[] using SimpleMessageConverter and on the receiving end, it will deserialize 
	 * byte[] into the Object type (in our case Order) using GenericMessageConverter.
	 */
	@RabbitListener(queues = RabbitConfig.QUEUE_JSON_ORDERS)
	public void processOrder(Order order) {
		LOGGER.info("Order Recieved : " + order);
	}
}
