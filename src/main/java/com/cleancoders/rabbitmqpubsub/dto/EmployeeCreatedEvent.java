package com.cleancoders.rabbitmqpubsub.dto;

import java.time.Instant;

/**
 * Domain event published to the fanout exchange whenever a new employee is created.
 *
 * <p>Consumers (NotificationConsumer, AuditConsumer) both receive this event via
 * their own dedicated queues — neither consumer is aware of the other.
 */
public record EmployeeCreatedEvent(

        /** Unique identifier for this event instance. */
        String eventId,

        /** Always {@code "EMPLOYEE_CREATED"}. */
        String eventType,

        /** UTC timestamp when the event occurred. */
        Instant occurredAt,

        /** Business identifier of the newly created employee. */
        String employeeId,

        String name,
        String email,
        String department
) {}
