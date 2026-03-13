package com.app.quantitymeasurementapp.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class QuantityMeasurementDTO {

    private Long   id;

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
    private boolean error;

    private LocalDateTime createdAt;

    public QuantityMeasurementDTO() {}

    public QuantityMeasurementDTO(
            Long id,
            double thisValue, String thisUnit, String thisMeasurementType,
            Double thatValue, String thatUnit, String thatMeasurementType,
            String operation,
            String resultString, double resultValue, String resultUnit, String resultMeasurementType,
            String errorMessage, boolean error,
            LocalDateTime createdAt) {
        this.id                    = id;
        this.thisValue             = thisValue;
        this.thisUnit              = thisUnit;
        this.thisMeasurementType   = thisMeasurementType;
        this.thatValue             = thatValue;
        this.thatUnit              = thatUnit;
        this.thatMeasurementType   = thatMeasurementType;
        this.operation             = operation;
        this.resultString          = resultString;
        this.resultValue           = resultValue;
        this.resultUnit            = resultUnit;
        this.resultMeasurementType = resultMeasurementType;
        this.errorMessage          = errorMessage;
        this.error                 = error;
        this.createdAt             = createdAt;
    }

    /** Convert a single QuantityMeasurementEntity to DTO. */
    public static QuantityMeasurementDTO fromEntity(QuantityMeasurementEntity e) {
        if (e == null) return null;
        QuantityMeasurementDTO dto = new QuantityMeasurementDTO();
        dto.setId(e.getId());
        dto.setThisValue(e.getThisValue());
        dto.setThisUnit(e.getThisUnit());
        dto.setThisMeasurementType(e.getThisMeasurementType());
        dto.setThatValue(e.getThatValue());
        dto.setThatUnit(e.getThatUnit());
        dto.setThatMeasurementType(e.getThatMeasurementType());
        dto.setOperation(e.getOperation());
        dto.setResultString(e.getResultString());
        dto.setResultValue(e.getResultValue());
        dto.setResultUnit(e.getResultUnit());
        dto.setResultMeasurementType(e.getResultMeasurementType());
        dto.setErrorMessage(e.getErrorMessage());
        dto.setError(e.isError());
        dto.setCreatedAt(e.getCreatedAt());
        return dto;
    }

    /** Convert this DTO to QuantityMeasurementEntity. */
    public QuantityMeasurementEntity toEntity() {
        QuantityMeasurementEntity e = new QuantityMeasurementEntity();
        e.setThisValue(this.thisValue);
        e.setThisUnit(this.thisUnit);
        e.setThisMeasurementType(this.thisMeasurementType);
        e.setThatValue(this.thatValue);
        e.setThatUnit(this.thatUnit);
        e.setThatMeasurementType(this.thatMeasurementType);
        e.setOperation(this.operation);
        e.setResultString(this.resultString);
        e.setResultValue(this.resultValue);
        e.setResultUnit(this.resultUnit);
        e.setResultMeasurementType(this.resultMeasurementType);
        e.setErrorMessage(this.errorMessage);
        e.setError(this.error);
        return e;
    }

    /** Convert a list of entities to a list of DTOs (Stream API). */
    public static List<QuantityMeasurementDTO> fromEntityList(List<QuantityMeasurementEntity> entities) {
        return entities.stream()
                       .map(QuantityMeasurementDTO::fromEntity)
                       .collect(Collectors.toList());
    }

    /** Convert a list of DTOs to a list of entities (Stream API). */
    public static List<QuantityMeasurementEntity> toEntityList(List<QuantityMeasurementDTO> dtos) {
        return dtos.stream()
                   .map(QuantityMeasurementDTO::toEntity)
                   .collect(Collectors.toList());
    }

    public Long   getId()                       { return id; }
    public double getThisValue()                { return thisValue; }
    public String getThisUnit()                 { return thisUnit; }
    public String getThisMeasurementType()      { return thisMeasurementType; }
    public Double getThatValue()                { return thatValue; }
    public String getThatUnit()                 { return thatUnit; }
    public String getThatMeasurementType()      { return thatMeasurementType; }
    public String getOperation()                { return operation; }
    public String getResultString()             { return resultString; }
    public double getResultValue()              { return resultValue; }
    public String getResultUnit()               { return resultUnit; }
    public String getResultMeasurementType()    { return resultMeasurementType; }
    public String getErrorMessage()             { return errorMessage; }
    public boolean isError()                    { return error; }
    public LocalDateTime getCreatedAt()         { return createdAt; }

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
    public void setError(boolean v)                         { this.error = v; }
    public void setCreatedAt(LocalDateTime v)               { this.createdAt = v; }

    @Override
    public String toString() {
        return "QuantityMeasurementDTO{id=" + id
               + ", operation='" + operation + "'"
               + ", resultValue=" + resultValue
               + ", error=" + error + "}";
    }
}
