package com.quantitymeasurementapp;

public class Length{
private double value;
private LengthUnit unit;
private static final double epsilon = 1e-4;
// used enum
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
	this.setValue(value);
	this.unit=unit;
}
public double convertToBaseUnit() {
	return this.getValue() * this.unit.conversionFactor;
}
public boolean compare(Length thatLength) {
	return Math.abs(this.convertToBaseUnit()-thatLength.convertToBaseUnit())<epsilon;
//return Double.compare(this.convertToBaseUnit() ,thatLength.convertToBaseUnit()) == 0;
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
public static Length demonstrateLengthConversion(double value,LengthUnit fromUnit,LengthUnit toUnit) {
Length source = new Length(value, fromUnit);
return source.convertTo(toUnit);
}

public static Length demonstrateLengthConversion(Length length, LengthUnit toUnit) {
    return length.convertTo(toUnit);
}
public Length convertTo(LengthUnit targetUnit) {
    if (targetUnit == null)
        throw new IllegalArgumentException("Target unit cannot be null");
    double baseValue = this.convertToBaseUnit();           
    double convertedValue = baseValue / targetUnit.getConversionFactor(); 
    return new Length(convertedValue, targetUnit);
}
public static double convert(double value, LengthUnit sourceUnit, LengthUnit targetUnit) {
    if (!Double.isFinite(value))
        throw new IllegalArgumentException("Value must be finite, got: " + value);
    if (sourceUnit == null || targetUnit == null)
        throw new IllegalArgumentException("Source and target units must not be null");
    Length temp = new Length(value, sourceUnit);
    return temp.convertTo(targetUnit).getValue();
}

@Override
public String toString() {
    return String.format("Length{value=%.4f, unit=%s, inInches=%.4f}",
            getValue(), unit.name(), convertToBaseUnit());
}
double getValue() {
	return value;
}
void setValue(double value) {
	this.value = value;
}
LengthUnit getUnit() {
	return unit;
}
public Length addLength(Length thatLength) {
    return QuantityMeasurementApp.demonstrateLengthAddition(this, thatLength);
}
}