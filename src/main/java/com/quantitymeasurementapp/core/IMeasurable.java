package com.quantitymeasurementapp.core;


public interface IMeasurable {

    @FunctionalInterface
    interface SupportsArithmetic {
        boolean isSupported();
    }

    SupportsArithmetic supportsArithmetic = () -> true;

  
    double getConversionFactor();
    double convertToBaseUnit(double value);
    double convertFromBaseUnit(double baseValue);
    String getUnitName();

   
    String getMeasurementType();

    default boolean supportsArithmetic() {
        return supportsArithmetic.isSupported();
    }

    default boolean supportsAddition() {
        return supportsArithmetic();
    }

    default boolean supportsDivision() {
        return supportsArithmetic();
    }

    default void validateOperationSupport(String operation) {
       
    }
}
