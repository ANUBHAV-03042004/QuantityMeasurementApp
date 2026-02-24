package com.quantitymeasurementapp;

public enum WeightUnit {
	MILLIGRAM(0.001),
	KILOGRAM(1000.0),
	GRAM(1.0),
	POUND(453.592),
	TONNE(1_000_000.0);
	private final double conversionFactor;
	WeightUnit(double conversionFactor){
		this.conversionFactor = conversionFactor;
	}
	public double getConversionFactor() {
		return conversionFactor;
	}
	public double convertToBaseUnit(double value) {
		return value * this.getConversionFactor();
	}
	public double convertFromBaseUnit(double baseValue) {
		return Math.round(baseValue/this.getConversionFactor()*100.0)/100.0;
	}
	
}
