package com.cleancoders.rabbitmqpubsub.consumer;

import com.cleancoders.rabbitmqpubsub.dto.EmployeeCreatedEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThatNoException;

/**
 * Unit tests for {@link NotificationConsumer}.
 *
 * <p>Verifies that the consumer handles an {@link EmployeeCreatedEvent} without
 * throwing, regardless of the underlying email stub.
 */
@ExtendWith(MockitoExtension.class)
class NotificationConsumerTest {

    @Spy
    private NotificationConsumer consumer;

    @Test
    void handleEmployeeCreated_processesEventWithoutException() {
        EmployeeCreatedEvent event = new EmployeeCreatedEvent(
                "evt-001", "EMPLOYEE_CREATED", Instant.now(),
                "emp-001", "Alice Smith", "alice@example.com", "Engineering");

        assertThatNoException().isThrownBy(() -> consumer.handleEmployeeCreated(event));
    }

    @Test
    void handleEmployeeCreated_handlesMultipleEventsSequentially() {
        EmployeeCreatedEvent first  = new EmployeeCreatedEvent("evt-A", "EMPLOYEE_CREATED", Instant.now(), "emp-A", "Bob",   "bob@example.com",   "HR");
        EmployeeCreatedEvent second = new EmployeeCreatedEvent("evt-B", "EMPLOYEE_CREATED", Instant.now(), "emp-B", "Carol", "carol@example.com", "Finance");

        assertThatNoException().isThrownBy(() -> {
            consumer.handleEmployeeCreated(first);
            consumer.handleEmployeeCreated(second);
        });
    }
}
