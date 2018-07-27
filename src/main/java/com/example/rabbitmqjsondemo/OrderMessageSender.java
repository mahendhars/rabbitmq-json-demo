package com.example.rabbitmqjsondemo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class OrderMessageSender {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(OrderMessageSender.class);
	/*
	 * Spring Boot auto-configures the infrastructure beans required to send/receive 
	 * messages to/from RabbitMQ broker. We can simply autowire RabbitTemplate and send a 
	 * message by invoking rabbitTemplate.convertAndSend(“routingKey”, Object) method.
	 */
	
	/*
	 * Default serialization mechanism converts the message object 
	 * into byte[] using SimpleMessageConverter and on the receiving end, it will deserialize 
	 * byte[] into the Object type (in our case Order) using GenericMessageConverter.
	 * 
	 * In order to change this behavior, we need to customize the Spring Boot RabbitMQ auto-configured beans.
	 */
	
	@Autowired
	private RabbitTemplate rabbitTemplate;
	
	/*
	 * One quick way to send a message as JSON payload is using ObjectMapper we can convert the Order object into JSON and send it.
	 * But converting objects into JSON like this is a kind of boilerplate.
	 * 
	 * Instead, we can configure org.springframework.amqp.support.converter.Jackson2JsonMessageConverter bean to be used by 
	 * RabbitTemplate so that the message will be serialized as JSON instead of byte[].
	 */
	@Autowired
	private ObjectMapper objectMapper;
	
	public void sendOrder(Order order) throws JsonProcessingException {
		LOGGER.info("Sending message : " + order);
		
		/*
		String orderJson = objectMapper.writeValueAsString(order);
		LOGGER.info(orderJson);
		Message message = MessageBuilder.withBody(orderJson.getBytes())
				.setContentType(MessageProperties.CONTENT_TYPE_JSON)
				.build();
		
		this.rabbitTemplate.convertAndSend(RabbitConfig.QUEUE_JSON_ORDERS, message);
		*/
		
		this.rabbitTemplate.convertAndSend(RabbitConfig.QUEUE_JSON_ORDERS, order);
	}
	
}
