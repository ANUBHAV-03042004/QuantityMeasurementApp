package com.app.quantitymeasurementapp.unit;

public enum WeightUnit implements IMeasurable {
    MILLIGRAM(0.000001),
    GRAM(0.001),
    KILOGRAM(1.0),
    POUND(0.453592),
    TONNE(1000.0);

    private final double conversionFactor;

    WeightUnit(double conversionFactor) {
        this.conversionFactor = conversionFactor;
    }

    @Override public double getConversionFactor()              { return conversionFactor; }
    @Override public double convertToBaseUnit(double value)    { return value * conversionFactor; }
    @Override public double convertFromBaseUnit(double base)   { return Math.round(base / conversionFactor * 100.0) / 100.0; }
    @Override public String getUnitName()                      { return name(); }
    @Override public String getMeasurementType()               { return "WEIGHT"; }

    SupportsArithmetic supportsArithmetic = () -> true;
}
