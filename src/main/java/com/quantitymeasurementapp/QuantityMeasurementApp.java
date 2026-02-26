package com.quantitymeasurementapp;

public class QuantityMeasurementApp {
	
	public static <U extends IMeasurable> Quantity<U> demonstrateQuantityAddition(Quantity<U> length1,Quantity<U> length2,U targetUnit) {
		if(length1 == null || length2 == null) throw new IllegalArgumentException("Quantity should not be null");
		if(targetUnit == null ) throw new IllegalArgumentException("Target Unit Should not be null");
		if(!Double.isFinite(length1.getValue()) || !Double.isFinite(length2.getValue())) throw new IllegalArgumentException("Quantity should be in finite range");
		if (!length1.getUnit().getClass().equals(length2.getUnit().getClass()))
		    throw new IllegalArgumentException("Cannot add different unit types");
		Quantity<U> convertedLength1 = length1.convertTo(targetUnit);
		Quantity<U> convertedLength2 = length2.convertTo(targetUnit);
		
		 double sumValue = convertedLength1.getValue() + convertedLength2.getValue();
		    return new Quantity<>(sumValue, targetUnit);
	}

	public static <U extends IMeasurable> Quantity<U> demonstrateQuantityAddition(double length1,double length2,U targetUnit) {
	if(length1 <0 || length2<0) throw new IllegalArgumentException("Quantity should not be less than zero");
	if(targetUnit == null ) throw new IllegalArgumentException("Target Unit Should not be null");
	if(!Double.isFinite(length1) || !Double.isFinite(length2)) throw new IllegalArgumentException("Quantity value should be in finite range");

	Quantity<U> sum = new Quantity<>(length1 + length2,targetUnit);
	   return sum;
	}
	public static void main(String[] args) {
	    Quantity<LengthUnit> length1 =
	            new Quantity<>(2.0, LengthUnit.FEET);

	    Quantity<LengthUnit> length2 =
	            new Quantity<>(200.0, LengthUnit.CENTIMETERS);

	    System.out.println("Are equal? " + length1.equals(length2));

	    Quantity<LengthUnit> sumLength =
	            demonstrateQuantityAddition(length1, length2, LengthUnit.INCHES);

	    System.out.println("Sum in meters: " + sumLength);


	    Quantity<WeightUnit> weight1 =
	            new Quantity<>(1.0, WeightUnit.KILOGRAM);

	    Quantity<WeightUnit> weight2 =
	            new Quantity<>(500.0, WeightUnit.GRAM);

	    System.out.println("Are equal? " + weight1.equals(weight2));

	    Quantity<WeightUnit> sumWeight =
	            demonstrateQuantityAddition(weight1, weight2, WeightUnit.GRAM);

	    System.out.println("Sum in grams: " + sumWeight);


	    Quantity<WeightUnit> doubleAdd =
	            demonstrateQuantityAddition(200, 300, WeightUnit.GRAM);

	    System.out.println("200g + 300g = " + doubleAdd);
	}
}