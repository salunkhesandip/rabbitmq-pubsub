---
name: rabbitmq-spring
description: "RabbitMQ + Spring Boot pub/sub skill. Use when configuring exchanges, queues, or bindings, writing publishers with AmqpTemplate, writing consumers with @RabbitListener, or adding message DTOs in this project."
argument-hint: "Describe the task (e.g., 'add new consumer queue', 'publish new event type', 'configure dead-letter queue')"
---

# RabbitMQ Spring Boot Pub/Sub

## When to Use
- Adding or editing `@Bean` exchange/queue/binding configuration in `RabbitMQConfig`
- Writing a new publisher method using `AmqpTemplate`
- Adding a new `@RabbitListener` consumer class
- Defining a new event record DTO
- Writing integration tests with embedded or mocked AMQP

## Procedure

1. Define the event record DTO first
2. Add exchange/queue/binding beans to `RabbitMQConfig` only if new infrastructure is required
3. Implement publisher (`AmqpTemplate.convertAndSend`) then consumer (`@RabbitListener`)
4. Use constants from `RabbitMQConfig` for exchange and queue names — never hardcode strings

## References

| Domain | Reference |
|--------|-----------|
| Exchange, queue & binding config | [config.md](./references/config.md) |
| Publisher & consumer patterns | [pubsub.md](./references/pubsub.md) |
| Event DTOs | [events.md](./references/events.md) |
