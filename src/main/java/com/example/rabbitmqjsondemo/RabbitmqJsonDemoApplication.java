package com.example.rabbitmqjsondemo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class RabbitmqJsonDemoApplication implements CommandLineRunner {

	private static final Logger LOGGER = LoggerFactory.getLogger(RabbitmqJsonDemoApplication.class);

	@Autowired
	private OrderMessageSender orderMessageSender;
	
	@Autowired
	private ConfigurableApplicationContext context;
	
	public static void main(String[] args) {
		SpringApplication.run(RabbitmqJsonDemoApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

		LOGGER.info("Creating order");
		
		Order order = new Order();
		order.setProductId("Hello World");
		order.setOrderNumber("1");
		order.setAmount(1000);
		
		orderMessageSender.sendOrder(order);		
		
		context.close();
	}
}
