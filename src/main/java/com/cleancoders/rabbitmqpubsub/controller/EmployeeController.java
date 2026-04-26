package com.cleancoders.rabbitmqpubsub.controller;

import com.cleancoders.rabbitmqpubsub.dto.Employee;
import com.cleancoders.rabbitmqpubsub.dto.EmployeeCreatedEvent;
import com.cleancoders.rabbitmqpubsub.publisher.EmployeeEventPublisher;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.UUID;

/**
 * REST entry point for employee operations.
 *
 * <p>On creation, an {@code EmployeeCreatedEvent} is published to RabbitMQ so that
 * downstream consumers can react independently (send a welcome email, write an audit
 * log, update a read model, etc.).
 */
@RestController
@RequestMapping("/api/employees")
@Tag(name = "Employees", description = "Employee lifecycle operations")
public class EmployeeController {

    private final EmployeeEventPublisher publisher;

    public EmployeeController(EmployeeEventPublisher publisher) {
        this.publisher = publisher;
    }

    /**
     * Creates a new employee and emits an {@code EMPLOYEE_CREATED} event to the
     * fanout exchange. All bound consumers receive the event simultaneously.
     */
    @PostMapping
    @Operation(summary = "Create employee", description = "Persists an employee and publishes an EMPLOYEE_CREATED event to the fanout exchange.")
    public ResponseEntity<Employee> createEmployee(@RequestBody @Valid Employee employee) {

        EmployeeCreatedEvent event = new EmployeeCreatedEvent(
                UUID.randomUUID().toString(),
                "EMPLOYEE_CREATED",
                Instant.now(),
                employee.id(),
                employee.name(),
                employee.email(),
                employee.department()
        );

        publisher.publishEmployeeCreated(event);

        return ResponseEntity.status(HttpStatus.CREATED).body(employee);
    }
}
