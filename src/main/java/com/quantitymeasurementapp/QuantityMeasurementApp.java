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

System.out.printf("1 ft  → in  : %.4f%n", Length.convert(1.0,  Length.LengthUnit.FEET,        Length.LengthUnit.INCHES));      // 12.0
System.out.printf("3 yd  → ft  : %.4f%n", Length.convert(3.0,  Length.LengthUnit.YARDS,       Length.LengthUnit.FEET));        // 9.0
System.out.printf("36 in → yd  : %.4f%n", Length.convert(36.0, Length.LengthUnit.INCHES,      Length.LengthUnit.YARDS));       // 1.0
System.out.printf("1 cm  → in  : %.6f%n", Length.convert(1.0,  Length.LengthUnit.CENTIMETERS, Length.LengthUnit.INCHES));      // ~0.393701
System.out.printf("0 ft  → in  : %.4f%n", Length.convert(0.0,  Length.LengthUnit.FEET,        Length.LengthUnit.INCHES));      // 0.0


System.out.println(new Length(3.0, Length.LengthUnit.FEET));
}
}