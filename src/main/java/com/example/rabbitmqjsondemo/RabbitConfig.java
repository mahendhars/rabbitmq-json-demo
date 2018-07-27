package com.example.rabbitmqjsondemo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.RabbitListenerConfigurer;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistrar;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.handler.annotation.support.DefaultMessageHandlerMethodFactory;
import org.springframework.messaging.handler.annotation.support.MessageHandlerMethodFactory;


/*
 * When recieving a JSON message, to treat the message payload as JSON we should customize the RabbitMQ configuration 
 * by implementing RabbitListenerConfigurer.
 */
@Configuration
public class RabbitConfig implements RabbitListenerConfigurer {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(RabbitConfig.class);
	
	public static final String QUEUE_JSON_ORDERS = "json-orders-queue";
	public static final String QUEUE_DEAD_ORDERS = "dead_orders_queue"; 
	public static final String EXCHANGE_ORDERS = "orders-exchange";
	
	// declare Queue with name "json-orders-queue"
	/*
	 * We may want to send invalid messages to a separate queue so that we can inspect and reprocess them later. 
	 * We can use DLQ (Dead Letter Queue) concept to automatically do it instead of we manually write the code to handle such scenarios. 
	 * We can declare the dead-letter-exchange, dead-letter-routing-key for a Queue while defining the Queue bean as follows
	 */
	@Bean
	Queue simpleOrdersQueue() {
		LOGGER.info("Creating orders_queue");
		return QueueBuilder.durable(QUEUE_JSON_ORDERS)
                .withArgument("x-dead-letter-exchange", "") // setting up dead letter queue
                .withArgument("x-dead-letter-routing-key", QUEUE_DEAD_ORDERS)
                .withArgument("x-message-ttl", 15000) //if message is not consumed in 15 seconds send to DLQ
                .build();

	}
	
	// When an invalid JSON message us sent to json-orders-queue, it will be sent to dead-orders-queue.
	@Bean
    Queue deadLetterQueue() {
        return QueueBuilder.durable(QUEUE_DEAD_ORDERS).build();
    }
	
	// declare Exchange with name "orders-exchange"
	@Bean
	Exchange ordersExchange() {
		LOGGER.info("Creating orders_exchange");
		return ExchangeBuilder.topicExchange(EXCHANGE_ORDERS).build();
	}
	
	// define binding between orders-queue and orders-exchange so that any message sent 
	// to orders-exchange with routing-key as “orders-queue” will be sent to orders-queue.
	@Bean
	Binding binding(Queue simpleOrdersQueue, TopicExchange ordersExchange) {
		LOGGER.info("Binding orders_queue to orders_exchange");
		return BindingBuilder.bind(simpleOrdersQueue).to(ordersExchange).with(QUEUE_JSON_ORDERS);
	}
	
	// Below configurations are required to setup the Senders to produce JSON payload
	@Bean
    public RabbitTemplate rabbitTemplate(final ConnectionFactory connectionFactory) {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(producerJackson2MessageConverter());
        return rabbitTemplate;
    }

    @Bean
    public Jackson2JsonMessageConverter producerJackson2MessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
	
	// Below configurations are required to setup the Listeners to consume JSON payload
	@Override
	public void configureRabbitListeners(RabbitListenerEndpointRegistrar registrar) {
		registrar.setMessageHandlerMethodFactory(messageHandlerMethodFactory());
	}
	
	@Bean
	public MappingJackson2MessageConverter consumerJackson2MessageConverter() {
		return new MappingJackson2MessageConverter();
	}
	
	@Bean
	public MessageHandlerMethodFactory messageHandlerMethodFactory() {
		DefaultMessageHandlerMethodFactory messageHandlerMethodFactory = new DefaultMessageHandlerMethodFactory();
        messageHandlerMethodFactory.setMessageConverter(consumerJackson2MessageConverter());
        return messageHandlerMethodFactory;
	}
}
