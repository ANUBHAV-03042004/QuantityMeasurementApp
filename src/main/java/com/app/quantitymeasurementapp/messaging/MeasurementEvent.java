package com.app.quantitymeasurementapp.messaging;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Message payload published to RabbitMQ for every measurement operation.
 * Must be Serializable for Jackson JSON conversion.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MeasurementEvent implements Serializable {
    private String        operation;
    private String        measurementType;
    private double        thisValue;
    private String        thisUnit;
    private double        thatValue;
    private String        thatUnit;
    private String        result;
    private boolean       isError;
    private LocalDateTime timestamp;
}
