package com.app.quantitymeasurementapp.messaging;

import com.app.quantitymeasurementapp.config.RabbitMQConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Publishes events to the RabbitMQ topic exchange.
 * Errors are caught and logged — messaging failures must NOT affect the API response.
 */
@Component
public class RabbitMQProducer {

    private static final Logger log = LoggerFactory.getLogger(RabbitMQProducer.class);

    private final RabbitTemplate rabbitTemplate;

    public RabbitMQProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * Publishes a measurement operation event.
     */
    public void publishMeasurementEvent(MeasurementEvent event) {
        try {
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.EXCHANGE,
                    RabbitMQConfig.ROUTING_MEASUREMENT,
                    event);
            log.debug("Published measurement event: op={} type={}", event.getOperation(), event.getMeasurementType());
        } catch (Exception e) {
            log.warn("Failed to publish measurement event: {}", e.getMessage());
        }
    }

    /**
     * Publishes a user-registered event.
     */
    public void publishUserRegistered(String email) {
        try {
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.EXCHANGE,
                    RabbitMQConfig.ROUTING_USER + ".registered",
                    Map.of("event", "USER_REGISTERED", "email", email, "timestamp", LocalDateTime.now().toString()));
            log.info("Published USER_REGISTERED event for {}", email);
        } catch (Exception e) {
            log.warn("Failed to publish USER_REGISTERED event: {}", e.getMessage());
        }
    }

    /**
     * Publishes a user-logged-in event.
     */
    public void publishUserLoggedIn(String email) {
        try {
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.EXCHANGE,
                    RabbitMQConfig.ROUTING_USER + ".login",
                    Map.of("event", "USER_LOGIN", "email", email, "timestamp", LocalDateTime.now().toString()));
            log.info("Published USER_LOGIN event for {}", email);
        } catch (Exception e) {
            log.warn("Failed to publish USER_LOGIN event: {}", e.getMessage());
        }
    }
}
