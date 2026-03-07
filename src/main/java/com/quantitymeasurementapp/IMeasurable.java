package com.quantitymeasurementapp;

public interface IMeasurable {
	  @FunctionalInterface
	    interface SupportsArithmetic {
	        boolean isSupported();
	    }
	  SupportsArithmetic supportsArithmetic = () -> true;
	public double getConversionFactor();
	public double convertToBaseUnit(double value);
	public double convertFromBaseUnit(double baseValue);
	public String getUnitName();
	 default boolean supportsArithmetic() {
	        return supportsArithmetic.isSupported();
	    }
	 default boolean supportsAddition() {
	        return supportsArithmetic();
	    }
	 default boolean supportsDivision() {
	        return supportsArithmetic();
	    }
//	 default boolean supportsSubtraction() {
//	        return supportsArithmetic();
//	    }
	 default void validateOperationSupport(String operation) {
	        // Default: all operations are supported — no-op
	    }
}
