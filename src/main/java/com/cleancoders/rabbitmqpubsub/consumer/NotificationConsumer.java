package com.cleancoders.rabbitmqpubsub.consumer;

import com.cleancoders.rabbitmqpubsub.config.RabbitMQConfig;
import com.cleancoders.rabbitmqpubsub.dto.EmployeeCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Notification Consumer (Subscriber).
 *
 * <p>Listens on {@code employee.notification.queue}, which is bound to the
 * {@code employee.events.fanout} exchange. Every time an employee is created,
 * this consumer receives the event independently of any other consumer.
 *
 * <p>Responsibility: simulate sending a welcome e-mail to the new employee.
 * In production this would delegate to an email service (e.g., SendGrid, SES).
 */
@Component
public class NotificationConsumer {

    private static final Logger log = LoggerFactory.getLogger(NotificationConsumer.class);

    @RabbitListener(queues = RabbitMQConfig.NOTIFICATION_QUEUE)
    public void handleEmployeeCreated(EmployeeCreatedEvent event) {
        log.info("[NOTIFICATION] Employee created — sending welcome e-mail. " +
                 "name='{}', email='{}', department='{}'",
                event.name(), event.email(), event.department());

        // TODO: delegate to e-mail service (e.g., JavaMailSender / SendGrid SDK)
        sendWelcomeEmail(event);

        log.info("[NOTIFICATION] Welcome e-mail dispatched for employeeId='{}'", event.employeeId());
    }

    private void sendWelcomeEmail(EmployeeCreatedEvent event) {
        // Simulated — replace with real e-mail logic
        log.debug("[NOTIFICATION] (stub) welcome email sent to {}", event.email());
    }
}
