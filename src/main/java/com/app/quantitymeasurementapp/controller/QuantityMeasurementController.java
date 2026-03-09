package com.app.quantitymeasurementapp.controller;

import com.app.quantitymeasurementapp.entity.QuantityDTO;
import com.app.quantitymeasurementapp.exception.QuantityMeasurementException;
import com.app.quantitymeasurementapp.service.IQuantityMeasurementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QuantityMeasurementController {

    private static final Logger logger = LoggerFactory.getLogger(QuantityMeasurementController.class);

    private final IQuantityMeasurementService service;

    public QuantityMeasurementController(IQuantityMeasurementService service) {
        if (service == null) throw new IllegalArgumentException("Service must not be null");
        this.service = service;
        logger.info("QuantityMeasurementController initialized");
    }

    public boolean performCompare(QuantityDTO q1, QuantityDTO q2) {
        try {
            boolean result = service.compare(q1, q2);
            logger.info("COMPARE    | {} == {} => {}", format(q1), format(q2), result);
            return result;
        } catch (QuantityMeasurementException e) {
            logger.error("COMPARE    | ERROR {}", e.getMessage());
            return false;
        }
    }

    public QuantityDTO performConvert(QuantityDTO source, QuantityDTO targetUnitDTO) {
        try {
            QuantityDTO result = service.convert(source, targetUnitDTO);
            logger.info("CONVERT    | {} => {}", format(source), format(result));
            return result;
        } catch (QuantityMeasurementException e) {
            logger.error("CONVERT    | ERROR {}", e.getMessage());
            return errorDTO(source);
        }
    }

    public QuantityDTO performAdd(QuantityDTO q1, QuantityDTO q2) {
        try {
            QuantityDTO result = service.add(q1, q2);
            logger.info("ADD        | {} + {} => {}", format(q1), format(q2), format(result));
            return result;
        } catch (QuantityMeasurementException e) {
            logger.error("ADD        | ERROR {}", e.getMessage());
            return errorDTO(q1);
        }
    }

    public QuantityDTO performSubtract(QuantityDTO q1, QuantityDTO q2) {
        try {
            QuantityDTO result = service.subtract(q1, q2);
            logger.info("SUBTRACT   | {} - {} => {}", format(q1), format(q2), format(result));
            return result;
        } catch (QuantityMeasurementException e) {
            logger.error("SUBTRACT   | ERROR {}", e.getMessage());
            return errorDTO(q1);
        }
    }

    public QuantityDTO performDivide(QuantityDTO q1, QuantityDTO q2) {
        try {
            QuantityDTO result = service.divide(q1, q2);
            logger.info("DIVIDE     | {} / {} => {} (dimensionless ratio)", format(q1), format(q2), String.format("%.4f", result.getValue()));
            return result;
        } catch (QuantityMeasurementException e) {
            logger.error("DIVIDE     | ERROR {}", e.getMessage());
            return errorDTO(q1);
        }
    }

    private String format(QuantityDTO dto) {
        if (dto == null || dto.getUnit() == null) return "null";
        return dto.getValue() + " " + dto.getUnit().getUnitName();
    }

    private QuantityDTO errorDTO(QuantityDTO source) {
        return new QuantityDTO(Double.NaN, source != null ? source.getUnit() : null);
    }
}
