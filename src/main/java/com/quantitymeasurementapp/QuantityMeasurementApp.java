package com.quantitymeasurementapp;


public class QuantityMeasurementApp {
public static void main(String[] args) {
	Length length1 = new Length(1.0, Length.LengthUnit.FEET);
	Length length2 = new Length(12.0, Length.LengthUnit.INCHES);
	System.out.println("Are lengths equal ? " +  length1.equals(length2));
}
}