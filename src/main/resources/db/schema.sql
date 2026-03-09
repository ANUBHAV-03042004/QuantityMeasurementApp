CREATE TABLE IF NOT EXISTS quantity_measurement_entity (
    id                VARCHAR(36)  NOT NULL PRIMARY KEY,
    operation_type    VARCHAR(20)  NOT NULL,
    operand1_value    DOUBLE       NOT NULL,
    operand1_unit     VARCHAR(50)  NOT NULL,
    operand1_type     VARCHAR(20)  NOT NULL,
    operand2_value    DOUBLE,
    operand2_unit     VARCHAR(50),
    operand2_type     VARCHAR(20),
    result_value      DOUBLE,
    result_unit       VARCHAR(50),
    result_type       VARCHAR(20),
    comparison_result BOOLEAN,
    has_error         BOOLEAN      NOT NULL DEFAULT FALSE,
    error_message     VARCHAR(500),
    created_at        TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_operation_type ON quantity_measurement_entity(operation_type);
CREATE INDEX IF NOT EXISTS idx_operand1_type  ON quantity_measurement_entity(operand1_type);
CREATE INDEX IF NOT EXISTS idx_created_at     ON quantity_measurement_entity(created_at);

CREATE TABLE IF NOT EXISTS quantity_measurement_history (
    history_id        BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    entity_id         VARCHAR(36)  NOT NULL,
    operation_type    VARCHAR(20)  NOT NULL,
    measurement_type  VARCHAR(20)  NOT NULL,
    summary           VARCHAR(500),
    recorded_at       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (entity_id) REFERENCES quantity_measurement_entity(id)
);

CREATE INDEX IF NOT EXISTS idx_history_entity    ON quantity_measurement_history(entity_id);
CREATE INDEX IF NOT EXISTS idx_history_op_type   ON quantity_measurement_history(operation_type);
CREATE INDEX IF NOT EXISTS idx_history_meas_type ON quantity_measurement_history(measurement_type);
