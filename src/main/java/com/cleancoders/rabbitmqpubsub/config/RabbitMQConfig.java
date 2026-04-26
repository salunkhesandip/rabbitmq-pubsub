package com.cleancoders.rabbitmqpubsub.config;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ topology declaration.
 *
 * <p>Pattern: Fanout Exchange — all messages are broadcast to every bound queue,
 * routing key is ignored.
 *
 * <pre>
 *                        ┌────────────────────────────┐
 *  Publisher ──►  [employee.events.fanout]  ──►  employee.notification.queue ──► NotificationConsumer
 *                        │                  ──►  employee.audit.queue        ──► AuditConsumer
 *                        └────────────────────────────┘
 * </pre>
 */
@Configuration
public class RabbitMQConfig {

    // ── Exchange ──────────────────────────────────────────────────────────────
    public static final String EXCHANGE_NAME = "employee.events.fanout";

    // ── Queues ────────────────────────────────────────────────────────────────
    public static final String NOTIFICATION_QUEUE = "employee.notification.queue";
    public static final String AUDIT_QUEUE        = "employee.audit.queue";

    // ── Exchange bean ─────────────────────────────────────────────────────────
    @Bean
    public FanoutExchange employeeEventsFanoutExchange() {
        return new FanoutExchange(EXCHANGE_NAME);
    }

    // ── Queue beans ───────────────────────────────────────────────────────────

    /** Durable notification queue — survives broker restart. */
    @Bean
    public Queue notificationQueue() {
        return new Queue(NOTIFICATION_QUEUE, true);
    }

    /** Durable audit queue — survives broker restart. */
    @Bean
    public Queue auditQueue() {
        return new Queue(AUDIT_QUEUE, true);
    }

    // ── Bindings: both queues receive every event from the fanout exchange ────

    @Bean
    public Binding notificationBinding(Queue notificationQueue,
                                       FanoutExchange employeeEventsFanoutExchange) {
        return BindingBuilder.bind(notificationQueue).to(employeeEventsFanoutExchange);
    }

    @Bean
    public Binding auditBinding(Queue auditQueue,
                                FanoutExchange employeeEventsFanoutExchange) {
        return BindingBuilder.bind(auditQueue).to(employeeEventsFanoutExchange);
    }

    // ── Message serialization — JSON via Jackson ──────────────────────────────

    /**
     * Jackson2JsonMessageConverter serializes/deserializes AMQP messages to/from
     * JSON format.
     *
     * <p>Note: Jackson2JsonMessageConverter is marked for removal in a future 
     * Spring AMQP version, but remains the standard way to handle JSON conversion.
     */
    @Bean
    public MessageConverter jacksonMessageConverter() {
        @SuppressWarnings({"deprecation", "removal"})
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();
        return converter;
    }

    @Bean
    public AmqpTemplate amqpTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jacksonMessageConverter());
        return rabbitTemplate;
    }
}
