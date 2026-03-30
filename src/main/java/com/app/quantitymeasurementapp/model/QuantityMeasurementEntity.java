package com.app.quantitymeasurementapp.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "quantity_measurement",
    indexes = {
        @Index(name = "idx_operation",             columnList = "operation"),
        @Index(name = "idx_this_measurement_type", columnList = "thisMeasurementType"),
        @Index(name = "idx_created_at",            columnList = "createdAt"),
        @Index(name = "idx_is_error",              columnList = "isError")
    }
)
public class QuantityMeasurementEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double thisValue;
    private String thisUnit;
    private String thisMeasurementType;

    private Double thatValue;
    private String thatUnit;
    private String thatMeasurementType;

    private String operation;
    private String resultString;
    private double resultValue;
    private String resultUnit;
    private String resultMeasurementType;

    private String  errorMessage;
    private boolean isError;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public QuantityMeasurementEntity() {}


    public QuantityMeasurementEntity(
            double thisValue, String thisUnit, String thisMeasurementType,
            double thatValue, String thatUnit, String thatMeasurementType,
            boolean comparisonResult) {
        this.thisValue           = thisValue;
        this.thisUnit            = thisUnit;
        this.thisMeasurementType = thisMeasurementType;
        this.thatValue           = thatValue;
        this.thatUnit            = thatUnit;
        this.thatMeasurementType = thatMeasurementType;
        this.operation           = "COMPARE";
        this.resultString        = String.valueOf(comparisonResult);
        this.isError             = false;
    }


    public QuantityMeasurementEntity(
            double thisValue, String thisUnit, String thisMeasurementType,
            String operation,
            double resultValue, String resultUnit, String resultMeasurementType) {
        this.thisValue             = thisValue;
        this.thisUnit              = thisUnit;
        this.thisMeasurementType   = thisMeasurementType;
        this.operation             = operation;
        this.resultValue           = resultValue;
        this.resultUnit            = resultUnit;
        this.resultMeasurementType = resultMeasurementType;
        this.resultString          = resultValue + " " + resultUnit;
        this.isError               = false;
    }

    public QuantityMeasurementEntity(
            double thisValue, String thisUnit, String thisMeasurementType,
            double thatValue, String thatUnit, String thatMeasurementType,
            String operation,
            double resultValue, String resultUnit, String resultMeasurementType) {
        this.thisValue             = thisValue;
        this.thisUnit              = thisUnit;
        this.thisMeasurementType   = thisMeasurementType;
        this.thatValue             = thatValue;
        this.thatUnit              = thatUnit;
        this.thatMeasurementType   = thatMeasurementType;
        this.operation             = operation;
        this.resultValue           = resultValue;
        this.resultUnit            = resultUnit;
        this.resultMeasurementType = resultMeasurementType;
        this.resultString          = resultValue + " " + resultUnit;
        this.isError               = false;
    }


    public QuantityMeasurementEntity(
            double thisValue, String thisUnit, String thisMeasurementType,
            Double thatValue, String thatUnit, String thatMeasurementType,
            String operation, String errorMessage) {
        this.thisValue           = thisValue;
        this.thisUnit            = thisUnit;
        this.thisMeasurementType = thisMeasurementType;
        this.thatValue           = thatValue;
        this.thatUnit            = thatUnit;
        this.thatMeasurementType = thatMeasurementType;
        this.operation           = operation;
        this.errorMessage        = errorMessage;
        this.isError             = true;
    }

    public Long    getId()                       { return id; }
    public double  getThisValue()                { return thisValue; }
    public String  getThisUnit()                 { return thisUnit; }
    public String  getThisMeasurementType()      { return thisMeasurementType; }
    public Double  getThatValue()                { return thatValue; }
    public String  getThatUnit()                 { return thatUnit; }
    public String  getThatMeasurementType()      { return thatMeasurementType; }
    public String  getOperation()                { return operation; }
    public String  getResultString()             { return resultString; }
    public double  getResultValue()              { return resultValue; }
    public String  getResultUnit()               { return resultUnit; }
    public String  getResultMeasurementType()    { return resultMeasurementType; }
    public String  getErrorMessage()             { return errorMessage; }
    public boolean isError()                     { return isError; }
    public LocalDateTime getCreatedAt()          { return createdAt; }
    public LocalDateTime getUpdatedAt()          { return updatedAt; }

    public void setId(Long id)                              { this.id = id; }
    public void setThisValue(double v)                      { this.thisValue = v; }
    public void setThisUnit(String v)                       { this.thisUnit = v; }
    public void setThisMeasurementType(String v)            { this.thisMeasurementType = v; }
    public void setThatValue(Double v)                      { this.thatValue = v; }
    public void setThatUnit(String v)                       { this.thatUnit = v; }
    public void setThatMeasurementType(String v)            { this.thatMeasurementType = v; }
    public void setOperation(String v)                      { this.operation = v; }
    public void setResultString(String v)                   { this.resultString = v; }
    public void setResultValue(double v)                    { this.resultValue = v; }
    public void setResultUnit(String v)                     { this.resultUnit = v; }
    public void setResultMeasurementType(String v)          { this.resultMeasurementType = v; }
    public void setErrorMessage(String v)                   { this.errorMessage = v; }
    public void setError(boolean v)                         { this.isError = v; }
    public void setCreatedAt(LocalDateTime v)               { this.createdAt = v; }
    public void setUpdatedAt(LocalDateTime v)               { this.updatedAt = v; }

    @Override
    public String toString() {
        return "QuantityMeasurementEntity{id=" + id
               + ", operation='" + operation + "'"
               + ", thisValue=" + thisValue
               + ", thisUnit='" + thisUnit + "'"
               + ", resultValue=" + resultValue
               + ", isError=" + isError + "}";
    }
}
