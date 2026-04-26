package com.cleancoders.rabbitmqpubsub.publisher;

import com.cleancoders.rabbitmqpubsub.config.RabbitMQConfig;
import com.cleancoders.rabbitmqpubsub.dto.EmployeeCreatedEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.AmqpTemplate;

import java.time.Instant;

import static org.mockito.Mockito.verify;

/**
 * Unit tests for {@link EmployeeEventPublisher}.
 *
 * <p>Verifies that the publisher delegates correctly to {@link AmqpTemplate} using
 * the configured fanout exchange name and an empty routing key.
 */
@ExtendWith(MockitoExtension.class)
class EmployeeEventPublisherTest {

    @Mock
    private AmqpTemplate amqpTemplate;

    @InjectMocks
    private EmployeeEventPublisher publisher;

    @Test
    void publishEmployeeCreated_sendsEventToFanoutExchange() {
        EmployeeCreatedEvent event = new EmployeeCreatedEvent(
                "evt-001",
                "EMPLOYEE_CREATED",
                Instant.parse("2026-04-26T10:00:00Z"),
                "emp-001",
                "Alice Smith",
                "alice@example.com",
                "Engineering"
        );

        publisher.publishEmployeeCreated(event);

        // Must send to the correct exchange with an empty routing key (fanout ignores it)
        verify(amqpTemplate).convertAndSend(RabbitMQConfig.EXCHANGE_NAME, "", event);
    }

    @Test
    void publishEmployeeCreated_propagatesDistinctEventsIndependently() {
        EmployeeCreatedEvent first  = new EmployeeCreatedEvent("evt-A", "EMPLOYEE_CREATED", Instant.now(), "emp-A", "Bob",   "bob@example.com",   "HR");
        EmployeeCreatedEvent second = new EmployeeCreatedEvent("evt-B", "EMPLOYEE_CREATED", Instant.now(), "emp-B", "Carol", "carol@example.com", "Finance");

        publisher.publishEmployeeCreated(first);
        publisher.publishEmployeeCreated(second);

        verify(amqpTemplate).convertAndSend(RabbitMQConfig.EXCHANGE_NAME, "", first);
        verify(amqpTemplate).convertAndSend(RabbitMQConfig.EXCHANGE_NAME, "", second);
    }
}
