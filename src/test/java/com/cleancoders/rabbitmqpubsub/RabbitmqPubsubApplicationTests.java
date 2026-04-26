package com.cleancoders.rabbitmqpubsub;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
class RabbitmqPubsubApplicationTests {

	// Prevent the application context from trying to connect to a real broker
	@MockitoBean
	ConnectionFactory connectionFactory;

	@Test
	void contextLoads() {
	}
}
