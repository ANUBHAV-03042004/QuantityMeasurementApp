package com.quantitymeasurementapp;

public class Length{
private double value;
private LengthUnit unit;

// used enum for feet , inches , yards , centimeters
public enum LengthUnit{
	FEET(12.0),
	INCHES(1.0),
	YARDS(36.0),
	CENTIMETERS(0.393701);
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

public static boolean demonstrateLengthEquality(Length length1,Length length2)
{
	return length1.equals(length2);
}
public static boolean demonstrateLengthComparison(double value1, LengthUnit unit1, double value2, LengthUnit unit2){
	Length l1 = new Length(value1,unit1);
	Length l2 = new Length(value2,unit2);
	
	return l1.equals(l2);
}
}