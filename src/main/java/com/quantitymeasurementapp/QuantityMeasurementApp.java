package com.quantitymeasurementapp;

import com.quantitymeasurementapp.Length.LengthUnit;

public class QuantityMeasurementApp {
	public static Length demonstrateLengthAddition(Length length1,Length length2) {
		if(length1 == null || length2 == null) throw new IllegalArgumentException("Length should not be null");
//		if(targetUnit == null ) throw new IllegalArgumentException("Target Unit Should not be null");
		if(!Double.isFinite(length1.getValue()) || !Double.isFinite(length2.getValue())) throw new IllegalArgumentException("Lengths should be in finite range");
		
//		Length convertedLength1 = length1.convertTo(targetUnit);
		Length convertedLength2 = length2.convertTo(length1.getUnit());
		
		 double sumValue = length1.getValue() + convertedLength2.getValue();
		    return new Length(sumValue,length1.getUnit());
	}
public static void main(String[] args) {
Length length1 = new Length(1.0, Length.LengthUnit.FEET);
Length length2 = new Length(12.0, Length.LengthUnit.INCHES);
System.out.println("Are lengths equal ? " +  length1.equals(length2));

System.out.println(demonstrateLengthAddition(length1, length2));
}
}