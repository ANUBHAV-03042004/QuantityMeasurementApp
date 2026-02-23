package com.quantitymeasurementapp;

import com.quantitymeasurementapp.Length.LengthUnit;

public class QuantityMeasurementApp {
	public static Length demonstrateLengthAddition(Length length1,Length length2,LengthUnit targetUnit) {
		if(length1 == null || length2 == null) throw new IllegalArgumentException("Length should not be null");
		if(targetUnit == null ) throw new IllegalArgumentException("Target Unit Should not be null");
		if(!Double.isFinite(length1.getValue()) || !Double.isFinite(length2.getValue())) throw new IllegalArgumentException("Lengths should be in finite range");
		
		Length convertedLength1 = length1.convertTo(targetUnit);
		Length convertedLength2 = length2.convertTo(targetUnit);
		
		 double sumValue = convertedLength1.getValue() + convertedLength2.getValue();
		    return new Length(sumValue, targetUnit);
	}

	public static Length demonstrateLengthAddition(double length1,double length2,LengthUnit targetUnit) {
	if(length1 <0 || length2<0) throw new IllegalArgumentException("Length should not be less than zero");
	if(targetUnit == null ) throw new IllegalArgumentException("Target Unit Should not be null");
	if(!Double.isFinite(length1) || !Double.isFinite(length2)) throw new IllegalArgumentException("Length value should be in finite range");

	Length sum = new Length(length1 + length2,targetUnit);
	return sum;
	}

public static void main(String[] args) {
Length length1 = new Length(2.0, Length.LengthUnit.FEET);
Length length2 = new Length(12.0, Length.LengthUnit.INCHES);
System.out.println("Are lengths equal ? " +  length1.equals(length2));
System.out.println(demonstrateLengthAddition(12.0, 13.0,LengthUnit.FEET));
System.out.println(demonstrateLengthAddition(length1, length2,LengthUnit.YARDS));
System.out.println(length1.addLength(2.0,LengthUnit.CENTIMETERS));
System.out.println(length1.addLength(length2,LengthUnit.INCHES));
}
}