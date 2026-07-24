# RabbitMQ Exchange, Queue & Binding Config

## Location
`com.cleancoders.rabbitmqpubsub.config.RabbitMQConfig`

## Config Template

```java
@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE_NAME        = "employee.events.fanout";
    public static final String NOTIFICATION_QUEUE   = "employee.notification.queue";
    public static final String AUDIT_QUEUE          = "employee.audit.queue";

    @Bean
    public FanoutExchange employeeEventsFanoutExchange() {
        return new FanoutExchange(EXCHANGE_NAME, true, false);
    }

    @Bean
    public Queue notificationQueue() {
        return QueueBuilder.durable(NOTIFICATION_QUEUE).build();
    }

    @Bean
    public Queue auditQueue() {
        return QueueBuilder.durable(AUDIT_QUEUE).build();
    }

    @Bean
    public Binding notificationBinding(Queue notificationQueue, FanoutExchange exchange) {
        return BindingBuilder.bind(notificationQueue).to(exchange);
    }

    @Bean
    public Binding auditBinding(Queue auditQueue, FanoutExchange exchange) {
        return BindingBuilder.bind(auditQueue).to(exchange);
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public AmqpTemplate amqpTemplate(ConnectionFactory connectionFactory) {
        var template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }
}
```

## application.yml

```yaml
spring:
  rabbitmq:
    host: ${RABBITMQ_HOST:localhost}
    port: ${RABBITMQ_PORT:5672}
    listener:
      simple:
        acknowledge-mode: auto
        retry:
          enabled: true
          max-attempts: 3
```

## Rules

- Use `public static final` constants for exchange and queue names — never hardcode strings elsewhere.
- Queues must be **durable** (`QueueBuilder.durable(...)`) to survive broker restarts.
- The fanout exchange ignores routing keys — pass `""` as the routing key when publishing.
- Add `Jackson2JsonMessageConverter` as the default converter for JSON serialization.
