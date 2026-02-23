package com.quantitymeasurementapp;


public class QuantityMeasurementApp {

public static void main(String[] args) {
Length length1 = new Length(1.0, Length.LengthUnit.FEET);
Length length2 = new Length(12.0, Length.LengthUnit.INCHES);
System.out.println("Are lengths equal ? " +  length1.equals(length2));

Length length3 = new Length(1.0, Length.LengthUnit.YARDS);
Length length4 = new Length(36.0, Length.LengthUnit.INCHES);
System.out.println("Are lengths equal ? " +  length3.equals(length4));

Length length5 = new Length(100.0, Length.LengthUnit.CENTIMETERS);
Length length6 = new Length(39.3701, Length.LengthUnit.INCHES);
System.out.println("Are lengths equal ? " +  length5.equals(length6));


System.out.println(Length.demonstrateLengthComparison(1.0,Length.LengthUnit.FEET,12.0,Length.LengthUnit.INCHES));
System.out.println(Length.demonstrateLengthComparison(1.0,Length.LengthUnit.YARDS,36.0,Length.LengthUnit.INCHES));
System.out.println(Length.demonstrateLengthComparison(100.0,Length.LengthUnit.CENTIMETERS,39.3701,Length.LengthUnit.INCHES));
System.out.println(Length.demonstrateLengthComparison(3.0,Length.LengthUnit.FEET,1.0,Length.LengthUnit.YARDS));
System.out.println(Length.demonstrateLengthComparison(1.0,Length.LengthUnit.FEET,30.48,Length.LengthUnit.CENTIMETERS));

}
}