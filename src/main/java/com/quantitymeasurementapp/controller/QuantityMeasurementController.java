package com.quantitymeasurementapp.controller;

import com.quantitymeasurementapp.model.QuantityDTO;
import com.quantitymeasurementapp.service.IQuantityMeasurementService;
import com.quantitymeasurementapp.service.QuantityMeasurementException;


public class QuantityMeasurementController {

    private final IQuantityMeasurementService service;

   
    public QuantityMeasurementController(IQuantityMeasurementService service) {
        if (service == null)
            throw new IllegalArgumentException("Service must not be null");
        this.service = service;
    }

  
    public boolean performCompare(QuantityDTO q1, QuantityDTO q2) {
        try {
            boolean result = service.compare(q1, q2);
            displayResult("COMPARE",
                    format(q1) + " == " + format(q2),
                    String.valueOf(result));
            return result;
        } catch (QuantityMeasurementException e) {
            displayError("COMPARE", e.getMessage());
            return false;
        }
    }

    public QuantityDTO performConvert(QuantityDTO source, QuantityDTO targetUnitDTO) {
        try {
            QuantityDTO result = service.convert(source, targetUnitDTO);
            displayResult("CONVERT",
                    format(source) + " → " + result.getUnit().getUnitName(),
                    format(result));
            return result;
        } catch (QuantityMeasurementException e) {
            displayError("CONVERT", e.getMessage());
            return errorDTO(source);
        }
    }

    public QuantityDTO performAdd(QuantityDTO q1, QuantityDTO q2) {
        try {
            QuantityDTO result = service.add(q1, q2);
            displayResult("ADD",
                    format(q1) + " + " + format(q2),
                    format(result));
            return result;
        } catch (QuantityMeasurementException e) {
            displayError("ADD", e.getMessage());
            return errorDTO(q1);
        }
    }

    public QuantityDTO performSubtract(QuantityDTO q1, QuantityDTO q2) {
        try {
            QuantityDTO result = service.subtract(q1, q2);
            displayResult("SUBTRACT",
                    format(q1) + " - " + format(q2),
                    format(result));
            return result;
        } catch (QuantityMeasurementException e) {
            displayError("SUBTRACT", e.getMessage());
            return errorDTO(q1);
        }
    }

    public QuantityDTO performDivide(QuantityDTO q1, QuantityDTO q2) {
        try {
            QuantityDTO result = service.divide(q1, q2);
            displayResult("DIVIDE",
                    format(q1) + " / " + format(q2),
                    String.format("%.4f (dimensionless ratio)", result.getValue()));
            return result;
        } catch (QuantityMeasurementException e) {
            displayError("DIVIDE", e.getMessage());
            return errorDTO(q1);
        }
    }


    private void displayResult(String operation, String expression, String result) {
        System.out.printf("%-10s | %-40s => %s%n", operation, expression, result);
    }

    private void displayError(String operation, String message) {
        System.out.printf("%-10s | ERROR: %s%n", operation, message);
    }

    private String format(QuantityDTO dto) {
        if (dto == null || dto.getUnit() == null) return "null";
        return dto.getValue() + " " + dto.getUnit().getUnitName();
    }

    private QuantityDTO errorDTO(QuantityDTO source) {
        return new QuantityDTO(Double.NaN, source != null ? source.getUnit() : null);
    }
}
