# Event DTO Conventions

## Location
`com.cleancoders.rabbitmqpubsub.dto`

## Event Record Template

```java
public record EmployeeCreatedEvent(
    String   eventId,
    String   eventType,
    Instant  occurredAt,
    String   employeeId,
    String   name,
    String   email,
    String   department
) {
    public static EmployeeCreatedEvent of(String employeeId, String name,
                                          String email, String department) {
        return new EmployeeCreatedEvent(
            UUID.randomUUID().toString(),
            "EMPLOYEE_CREATED",
            Instant.now(),
            employeeId,
            name, email, department
        );
    }
}
```

## Rules

- Events are **immutable records** — no setters.
- Always include `eventId` (UUID), `eventType` (string constant), and `occurredAt` (Instant).
- Use a static factory method `of(...)` to construct events with generated metadata.
- Jackson serializes records automatically with `Jackson2JsonMessageConverter`.
