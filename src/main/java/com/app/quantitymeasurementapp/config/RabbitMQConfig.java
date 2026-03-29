package com.app.quantitymeasurementapp.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ configuration.
 *
 * Topology:
 *   Exchange  : quantity.exchange  (topic)
 *   Queue     : quantity.measurement.queue
 *   Routing   : quantity.measurement.#
 *   Dead Letter: quantity.dlq (messages that fail processing go here)
 */
@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE          = "quantity.exchange";
    public static final String MEASUREMENT_QUEUE = "quantity.measurement.queue";
    public static final String USER_QUEUE        = "quantity.user.queue";
    public static final String DLQ               = "quantity.dlq";
    public static final String ROUTING_MEASUREMENT = "quantity.measurement";
    public static final String ROUTING_USER        = "quantity.user";

    // ── Exchange ──────────────────────────────────────────────────────────────

    @Bean
    public TopicExchange quantityExchange() {
        return ExchangeBuilder.topicExchange(EXCHANGE).durable(true).build();
    }

    // ── Queues ────────────────────────────────────────────────────────────────

    @Bean
    public Queue measurementQueue() {
        return QueueBuilder.durable(MEASUREMENT_QUEUE)
                .withArgument("x-dead-letter-exchange", "")
                .withArgument("x-dead-letter-routing-key", DLQ)
                .build();
    }

    @Bean
    public Queue userQueue() {
        return QueueBuilder.durable(USER_QUEUE)
                .withArgument("x-dead-letter-exchange", "")
                .withArgument("x-dead-letter-routing-key", DLQ)
                .build();
    }

    @Bean
    public Queue deadLetterQueue() {
        return QueueBuilder.durable(DLQ).build();
    }

    // ── Bindings ──────────────────────────────────────────────────────────────

    @Bean
    public Binding measurementBinding() {
        return BindingBuilder.bind(measurementQueue())
                .to(quantityExchange())
                .with(ROUTING_MEASUREMENT + ".#");
    }

    @Bean
    public Binding userBinding() {
        return BindingBuilder.bind(userQueue())
                .to(quantityExchange())
                .with(ROUTING_USER + ".#");
    }

    // ── JSON message converter ────────────────────────────────────────────────

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }
}
