package com.cleancoders.rabbitmqpubsub.consumer;

import com.cleancoders.rabbitmqpubsub.config.RabbitMQConfig;
import com.cleancoders.rabbitmqpubsub.dto.EmployeeCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Audit Consumer (Subscriber).
 *
 * <p>Listens on {@code employee.audit.queue}, which is also bound to the same
 * {@code employee.events.fanout} exchange. Because it is a fanout exchange, both
 * {@link NotificationConsumer} and this consumer receive every event — fully
 * decoupled from each other and from the publisher.
 *
 * <p>Responsibility: write a structured audit record for every employee lifecycle
 * event. In production this would persist to an audit database or SIEM.
 */
@Component
public class AuditConsumer {

    private static final Logger log = LoggerFactory.getLogger(AuditConsumer.class);

    @RabbitListener(queues = RabbitMQConfig.AUDIT_QUEUE)
    public void handleEmployeeCreated(EmployeeCreatedEvent event) {
        log.info("[AUDIT] Event received — type='{}', eventId='{}', employeeId='{}', occurredAt='{}'",
                event.eventType(), event.eventId(), event.employeeId(), event.occurredAt());

        // TODO: persist to audit store (e.g., database table, Elasticsearch index)
        writeAuditRecord(event);

        log.info("[AUDIT] Audit record written for eventId='{}'", event.eventId());
    }

    private void writeAuditRecord(EmployeeCreatedEvent event) {
        // Simulated — replace with real persistence logic
        log.debug("[AUDIT] (stub) audit record written: {}", event);
    }
}
