package com.quantitymeasurementapp.core;

import java.util.function.Function;

public enum TemperatureUnit implements IMeasurable {

    CELSIUS {
        private final Function<Double, Double> CELSIUS_TO_CELSIUS = (celsius) -> celsius;

        @Override public double convertToBaseUnit(double value)      { return CELSIUS_TO_CELSIUS.apply(value); }
        @Override public double convertFromBaseUnit(double base)     { return CELSIUS_TO_CELSIUS.apply(base); }
        @Override public String getUnitName()                        { return "Celsius"; }
    },

    FAHRENHEIT {
        private final Function<Double, Double> FAHRENHEIT_TO_CELSIUS = (f) -> (f - 32.0) * 5.0 / 9.0;
        private final Function<Double, Double> CELSIUS_TO_FAHRENHEIT = (c) -> c * 9.0 / 5.0 + 32.0;

        @Override public double convertToBaseUnit(double value)      { return FAHRENHEIT_TO_CELSIUS.apply(value); }
        @Override public double convertFromBaseUnit(double base)     { return CELSIUS_TO_FAHRENHEIT.apply(base); }
        @Override public String getUnitName()                        { return "Fahrenheit"; }
    },

    KELVIN {
        private final Function<Double, Double> KELVIN_TO_CELSIUS = (k) -> k - 273.15;
        private final Function<Double, Double> CELSIUS_TO_KELVIN  = (c) -> c + 273.15;

        @Override public double convertToBaseUnit(double value)      { return KELVIN_TO_CELSIUS.apply(value); }
        @Override public double convertFromBaseUnit(double base)     { return CELSIUS_TO_KELVIN.apply(base); }
        @Override public String getUnitName()                        { return "Kelvin"; }
    };

    
    SupportsArithmetic supportsArithmetic = () -> false;

    @Override public double getConversionFactor() { return 1.0; }

    @Override public String getMeasurementType() { return "TEMPERATURE"; }

    @Override public boolean supportsArithmetic()  { return supportsArithmetic.isSupported(); }
    @Override public boolean supportsAddition()    { return false; }
    @Override public boolean supportsDivision()    { return false; }

    @Override
    public void validateOperationSupport(String operation) {
        throw new UnsupportedOperationException(
                "Temperature does not support " + operation
                + " — temperature values are absolute points on a scale, "
                + "not additive quantities. "
                + "Subtract two temperatures to compute a difference.");
    }
}
