package com.quantitymeasurementapp;

public class Length{
	private double value;
	private LengthUnit unit;
	
	
	public enum LengthUnit{
		FEET(12.0),
		INCHES(1.0);
		
		private final double conversionFactor;
		LengthUnit(double conversionFactor){
			this.conversionFactor = conversionFactor;
		}
		public double getConversionFactor() {
			return conversionFactor;
		}
	}
	
	public Length(double value,LengthUnit unit) {
		if(value<0) throw new IllegalArgumentException("value cannot be less than zero");
		if(unit == null)  throw new IllegalArgumentException("unit cannot be null");
		this.value = value;
		this.unit=unit;
	}
	public double convertToBaseUnit() {
		return this.value * this.unit.conversionFactor;
	}
	public boolean compare(Length thatLength) {
	return Double.compare(this.convertToBaseUnit() ,thatLength.convertToBaseUnit()) == 0;
	}
	@Override
	public boolean equals(Object o) {
		if(this == o) return true;
		if(o == null) return false;
		if(this.getClass()!=o.getClass()) return false;
		
		Length other = (Length) o;
	return this.compare(other);
	}
}
