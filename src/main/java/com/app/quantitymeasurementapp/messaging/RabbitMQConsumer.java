package com.app.quantitymeasurementapp.messaging;

import com.app.quantitymeasurementapp.config.RabbitMQConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Consumes messages from the RabbitMQ queues.
 *
 * In a production system these consumers could:
 *   - Trigger analytics pipelines
 *   - Notify downstream microservices
 *   - Store audit logs in a separate data store
 */
@Component
public class RabbitMQConsumer {

    private static final Logger log = LoggerFactory.getLogger(RabbitMQConsumer.class);

    @RabbitListener(queues = RabbitMQConfig.MEASUREMENT_QUEUE)
    public void consumeMeasurementEvent(MeasurementEvent event) {
        log.info("[MQ] Measurement event received — op={} type={} result={} error={}",
                 event.getOperation(),
                 event.getMeasurementType(),
                 event.getResult(),
                 event.isError());
    }

    @RabbitListener(queues = RabbitMQConfig.USER_QUEUE)
    public void consumeUserEvent(Map<String, Object> event) {
        log.info("[MQ] User event received — event={} email={}",
                 event.get("event"), event.get("email"));
    }
}
