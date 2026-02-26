package com.quantitymeasurementapp;

import java.util.Objects;

public class Quantity<U extends IMeasurable>{
	private final double value;
	private final U unit;
	
	public Quantity(double value,U unit) {
		if(value<0) throw new IllegalArgumentException("value cannot be less than zero");
		if(unit == null)  throw new IllegalArgumentException("unit cannot be null");
		this.value=value;
		this.unit=unit;
	}
	private static final double EPSILON = 1e-4;
	public double getValue() {
		return value;
	}
	public U getUnit() {
		return unit;
	}
	public boolean compare(Quantity<U> that) {
		  if (that == null) return false;
		return Math.abs(( this.unit).convertToBaseUnit(this.value)-(that.getUnit()).convertToBaseUnit(that.getValue()))<EPSILON;
	//return Double.compare(this.convertToBaseUnit() ,thatLength.convertToBaseUnit()) == 0;
	}
	@Override
	public boolean equals(Object o) {
		if(this == o) return true;
//		if(o == null) return false;
//		if(this.getClass()!=o.getClass()) return false;
	    if (!(o instanceof Quantity<?>other)) return false;
		if (!this.unit.getClass().equals(other.unit.getClass()))
		    return false;
		return this.compare((Quantity<U>) other);
	}

	@Override
	public int hashCode() {
	    double base = unit.convertToBaseUnit(value);
	    long rounded = Math.round(base / EPSILON);
	    return Long.hashCode(rounded);
	}
	public static boolean demonstrateEquality(Quantity<?> quantity1,Quantity<?> quantity2)
	{
		return quantity1.equals(quantity2);
	}
	
	public static <U extends IMeasurable> boolean demonstrateComparison(double value1, U unit1, double value2,U unit2){
		Quantity<U> l1 = new Quantity<>(value1,unit1);
		Quantity<U> l2 = new Quantity<>(value2,unit2);
		
		return l1.equals(l2);
	}
	public static <U extends IMeasurable> Quantity<U> demonstrateConversion(double value,U fromUnit,U toUnit) {
	Quantity<U> source = new Quantity<>(value, fromUnit);
	return source.convertTo(toUnit);
	}
	
	public static <U extends IMeasurable> Quantity<U> demonstrateConversion(Quantity<U> quantity, U toUnit) {
	    return quantity.convertTo(toUnit);
	}
	public Quantity<U> convertTo(U targetUnit) {
	    if (targetUnit == null)
	        throw new IllegalArgumentException("Target unit cannot be null");
	    double baseValue = this.unit.convertToBaseUnit(this.getValue());           
	    double convertedValue = baseValue / targetUnit.getConversionFactor(); 
	    return new Quantity<>(convertedValue, targetUnit);
	}
	
	public static  <U extends IMeasurable>  double convert(double value, U sourceUnit, U targetUnit) {
	    if (!Double.isFinite(value))
	        throw new IllegalArgumentException("Value must be finite, got: " + value);
	    if (sourceUnit == null || targetUnit == null)
	        throw new IllegalArgumentException("Source and target units must not be null");
	    Quantity<U> temp = new Quantity<>(value, sourceUnit);
	    return temp.convertTo(targetUnit).getValue();
	}
	public Quantity<U> addQuantity(Quantity<U> thatLength,U targetUnit) {
	    return QuantityMeasurementApp.demonstrateQuantityAddition(this, thatLength,targetUnit);
	}
	public Quantity<U> addQuantity(double value ,U targetUnit) {
	    return QuantityMeasurementApp.demonstrateQuantityAddition(this.value, value,targetUnit);
	}
	public Quantity<U> addQuantity(Quantity<U> other) {
	    if (other == null)
	        throw new IllegalArgumentException("Quantity cannot be null");

	    return addQuantity(other, this.unit);
	}
	public Quantity<U> addQuantity(double value) {
	    return addQuantity(value, this.unit);
	}
	@Override
	public String toString() {
	    return String.format(
	            "%s %.4f (%s base: %.4f)",
	            unit,
	            value,
	            unit.getUnitName(),
	            unit.convertToBaseUnit(value)
	    );
	}
}
