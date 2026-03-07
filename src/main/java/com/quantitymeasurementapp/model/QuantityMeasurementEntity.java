package com.quantitymeasurementapp.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;


public class QuantityMeasurementEntity implements Serializable {

    private static final long serialVersionUID = 1L;

  
    private String        operationId;
    private String        operationType;
    private QuantityDTO   operand1;
    private QuantityDTO   operand2;           // null for single-operand ops
    private QuantityDTO   result;             // null for comparison or error
    private boolean       comparisonResult;   // meaningful only for COMPARE ops
    private boolean       hasError;
    private String        errorMessage;
    private LocalDateTime timestamp;

   
    public QuantityMeasurementEntity(String operationType,
                                      QuantityDTO operand1,
                                      QuantityDTO result) {
        this.operationId   = UUID.randomUUID().toString();
        this.operationType = operationType;
        this.operand1      = operand1;
        this.operand2      = null;
        this.result        = result;
        this.hasError      = false;
        this.timestamp     = LocalDateTime.now();
    }

   
    public QuantityMeasurementEntity(String operationType,
                                      QuantityDTO operand1,
                                      QuantityDTO operand2,
                                      QuantityDTO result) {
        this.operationId   = UUID.randomUUID().toString();
        this.operationType = operationType;
        this.operand1      = operand1;
        this.operand2      = operand2;
        this.result        = result;
        this.hasError      = false;
        this.timestamp     = LocalDateTime.now();
    }

   
    public QuantityMeasurementEntity(String operationType,
                                      QuantityDTO operand1,
                                      QuantityDTO operand2,
                                      boolean comparisonResult) {
        this.operationId      = UUID.randomUUID().toString();
        this.operationType    = operationType;
        this.operand1         = operand1;
        this.operand2         = operand2;
        this.comparisonResult = comparisonResult;
        this.hasError         = false;
        this.timestamp        = LocalDateTime.now();
    }

  
    public QuantityMeasurementEntity(String operationType,
                                      QuantityDTO operand1,
                                      QuantityDTO operand2,
                                      String errorMessage) {
        this.operationId   = UUID.randomUUID().toString();
        this.operationType = operationType;
        this.operand1      = operand1;
        this.operand2      = operand2;
        this.hasError      = true;
        this.errorMessage  = errorMessage;
        this.timestamp     = LocalDateTime.now();
    }

    public String        getOperationId()      { return operationId; }
    public String        getOperationType()    { return operationType; }
    public QuantityDTO   getOperand1()         { return operand1; }
    public QuantityDTO   getOperand2()         { return operand2; }
    public QuantityDTO   getResult()           { return result; }
    public boolean       getComparisonResult() { return comparisonResult; }
    public boolean       hasError()            { return hasError; }
    public String        getErrorMessage()     { return errorMessage; }
    public LocalDateTime getTimestamp()        { return timestamp; }

    @Override
    public String toString() {
        if (hasError) {
            return String.format("[%s] %-12s | ERROR: %s", timestamp, operationType, errorMessage);
        }
        if ("COMPARE".equals(operationType)) {
            return String.format("[%s] %-12s | %s == %s → %b",
                    timestamp, operationType, operand1, operand2, comparisonResult);
        }
        if (operand2 == null) {
            return String.format("[%s] %-12s | %s → %s", timestamp, operationType, operand1, result);
        }
        return String.format("[%s] %-12s | %s , %s → %s",
                timestamp, operationType, operand1, operand2, result);
    }
}
