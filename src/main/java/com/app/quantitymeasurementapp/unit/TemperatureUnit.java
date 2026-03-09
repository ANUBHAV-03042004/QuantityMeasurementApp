package com.app.quantitymeasurementapp.unit;

import java.util.function.Function;

public enum TemperatureUnit implements IMeasurable {

    CELSIUS {
        private final Function<Double, Double> TO_CELSIUS = c -> c;
        @Override public double convertToBaseUnit(double value)  { return TO_CELSIUS.apply(value); }
        @Override public double convertFromBaseUnit(double base) { return TO_CELSIUS.apply(base); }
        @Override public String getUnitName()                    { return "CELSIUS"; }
    },

    FAHRENHEIT {
        private final Function<Double, Double> TO_CELSIUS      = f -> (f - 32.0) * 5.0 / 9.0;
        private final Function<Double, Double> FROM_CELSIUS    = c -> c * 9.0 / 5.0 + 32.0;
        @Override public double convertToBaseUnit(double value)  { return TO_CELSIUS.apply(value); }
        @Override public double convertFromBaseUnit(double base) { return FROM_CELSIUS.apply(base); }
        @Override public String getUnitName()                    { return "FAHRENHEIT"; }
    },

    KELVIN {
        private final Function<Double, Double> TO_CELSIUS      = k -> k - 273.15;
        private final Function<Double, Double> FROM_CELSIUS    = c -> c + 273.15;
        @Override public double convertToBaseUnit(double value)  { return TO_CELSIUS.apply(value); }
        @Override public double convertFromBaseUnit(double base) { return FROM_CELSIUS.apply(base); }
        @Override public String getUnitName()                    { return "KELVIN"; }
    };

    SupportsArithmetic supportsArithmetic = () -> false;

    @Override public double getConversionFactor()             { return 1.0; }
    @Override public String getMeasurementType()              { return "TEMPERATURE"; }
    @Override public boolean supportsArithmetic()             { return supportsArithmetic.isSupported(); }
    @Override public boolean supportsAddition()               { return false; }
    @Override public boolean supportsDivision()               { return false; }

    @Override
    public void validateOperationSupport(String operation) {
        throw new UnsupportedOperationException(
            "Temperature does not support " + operation
            + " because temperature values are absolute points on a scale not additive quantities.");
    }
}
