# Publisher & Consumer Patterns

## Publisher

```java
@Component
@RequiredArgsConstructor
@Slf4j
public class EmployeeEventPublisher {

    private final AmqpTemplate amqpTemplate;

    public void publishEmployeeCreated(EmployeeCreatedEvent event) {
        log.info("[PUBLISHER] Sending: type={}, eventId={}, employeeId={}",
            event.eventType(), event.eventId(), event.employeeId());
        // Fanout exchange — routing key is always ""
        amqpTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, "", event);
    }
}
```

## Consumer

```java
@Component
@Slf4j
public class AuditConsumer {

    @RabbitListener(queues = RabbitMQConfig.AUDIT_QUEUE)
    public void handleEmployeeCreated(EmployeeCreatedEvent event) {
        log.info("[AUDIT] type='{}', eventId='{}', employeeId='{}'",
            event.eventType(), event.eventId(), event.employeeId());
        // business logic here
    }
}
```

## Rules

- Always reference queue/exchange names via `RabbitMQConfig` constants, not string literals.
- Use `AmqpTemplate.convertAndSend` in publishers — it applies the configured `MessageConverter`.
- Use `@RabbitListener(queues = ...)` on a method in a `@Component` class.
- Log events at entry/exit of every listener for observability.
- Do not throw unchecked exceptions from listeners unless retry is acceptable (configured in `application.yml`).
