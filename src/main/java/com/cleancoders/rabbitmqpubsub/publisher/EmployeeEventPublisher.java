package com.cleancoders.rabbitmqpubsub.publisher;

import com.cleancoders.rabbitmqpubsub.config.RabbitMQConfig;
import com.cleancoders.rabbitmqpubsub.dto.EmployeeCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Component;

/**
 * Publisher (Producer) that sends {@link EmployeeCreatedEvent} messages to the
 * fanout exchange.
 *
 * <p>The fanout exchange automatically broadcasts the message to every queue that
 * is bound to it — {@code employee.notification.queue} and {@code employee.audit.queue}
 * — without the publisher knowing anything about its consumers.
 *
 * <p>Routing key is an empty string because fanout exchanges ignore it entirely.
 */
@Component
public class EmployeeEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(EmployeeEventPublisher.class);

    private final AmqpTemplate amqpTemplate;

    public EmployeeEventPublisher(AmqpTemplate amqpTemplate) {
        this.amqpTemplate = amqpTemplate;
    }

    /**
     * Publishes an {@link EmployeeCreatedEvent} to the fanout exchange.
     *
     * @param event the event to broadcast
     */
    public void publishEmployeeCreated(EmployeeCreatedEvent event) {
        log.info("[PUBLISHER] Sending event: type={}, eventId={}, employeeId={}",
                event.eventType(), event.eventId(), event.employeeId());
        // routingKey "" — fanout exchange ignores it; message goes to ALL bound queues
        amqpTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, "", event);
        log.info("[PUBLISHER] Event dispatched to exchange '{}'", RabbitMQConfig.EXCHANGE_NAME);
    }
}
