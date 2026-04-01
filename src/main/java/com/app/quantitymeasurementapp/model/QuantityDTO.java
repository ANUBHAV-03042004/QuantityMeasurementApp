package com.app.quantitymeasurementapp.model;

import com.app.quantitymeasurementapp.unit.IMeasurable;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class QuantityDTO {

    @NotNull(message = "value must not be null")
    private Double value;

    @NotEmpty(message = "unit must not be empty")
    private String unit;

    @NotEmpty(message = "measurementType must not be empty")
    private String measurementType;

    public QuantityDTO() {}

    public QuantityDTO(Double value, String unit, String measurementType) {
        this.value           = value;
        this.unit            = unit;
        this.measurementType = measurementType;
    }

    public Double getValue()           { return value; }
    public String getUnit()            { return unit; }
    public String getMeasurementType() { return measurementType; }

    public void setValue(Double value)                   { this.value = value; }
    public void setUnit(String unit)                     { this.unit = unit; }
    public void setMeasurementType(String measurementType) { this.measurementType = measurementType; }

    // ── LengthUnit — base: INCHES ────────────────────────────────────────────

    public enum LengthUnit implements IMeasurable {
        FEET(12.0),
        INCHES(1.0),
        YARDS(36.0),
        CENTIMETERS(1.0 / 2.54);

        private final double inchesPerUnit;

        LengthUnit(double inchesPerUnit) { this.inchesPerUnit = inchesPerUnit; }

        @Override public double toBaseValue(double value)   { return value * inchesPerUnit; }
        @Override public double fromBaseValue(double base)  { return base / inchesPerUnit; }
    }

    // ── WeightUnit — base: GRAM ──────────────────────────────────────────────

    public enum WeightUnit implements IMeasurable {
        MILLIGRAM(0.001),
        GRAM(1.0),
        KILOGRAM(1_000.0),
        POUND(453.592),
        TONNE(1_000_000.0);

        private final double gramsPerUnit;

        WeightUnit(double gramsPerUnit) { this.gramsPerUnit = gramsPerUnit; }

        @Override public double toBaseValue(double value)  { return value * gramsPerUnit; }
        @Override public double fromBaseValue(double base) { return base / gramsPerUnit; }
    }

    // ── VolumeUnit — base: MILLILITRE ────────────────────────────────────────

    public enum VolumeUnit implements IMeasurable {
        LITRE(1_000.0),
        MILLILITRE(1.0),
        GALLON(3_785.41);

        private final double mlPerUnit;

        VolumeUnit(double mlPerUnit) { this.mlPerUnit = mlPerUnit; }

        @Override public double toBaseValue(double value)  { return value * mlPerUnit; }
        @Override public double fromBaseValue(double base) { return base / mlPerUnit; }
    }

    // ── TemperatureUnit — base: CELSIUS (non-linear) ─────────────────────────

    public enum TemperatureUnit implements IMeasurable {
        CELSIUS {
            @Override public double toBaseValue(double v)   { return v; }
            @Override public double fromBaseValue(double b) { return b; }
        },
        FAHRENHEIT {
            @Override public double toBaseValue(double v)   { return (v - 32.0) * 5.0 / 9.0; }
            @Override public double fromBaseValue(double b) { return b * 9.0 / 5.0 + 32.0; }
        },
        KELVIN {
            @Override public double toBaseValue(double v)   { return v - 273.15; }
            @Override public double fromBaseValue(double b) { return b + 273.15; }
        }
    }
}
