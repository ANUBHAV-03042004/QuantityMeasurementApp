package com.quantitymeasurementapp;

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
	
	public static Weight demonstrateWeightAddition(Weight weight1,Weight weight2,WeightUnit targetUnit) {
		if(weight1 == null || weight2 == null) throw new IllegalArgumentException("Weight should not be null");
		if(targetUnit == null ) throw new IllegalArgumentException("Target Unit Should not be null");
		if(!Double.isFinite(weight1.getValue()) || !Double.isFinite(weight2.getValue())) throw new IllegalArgumentException("Weights should be in finite range");
		
		Weight convertedWeight1 = weight1.convertTo(targetUnit);
		Weight convertedWeight2 = weight2.convertTo(targetUnit);
		
		 double sumValue = convertedWeight1.getValue() + convertedWeight2.getValue();
		    return new Weight(sumValue, targetUnit);
	}


public static void main(String[] args) {
	double kilograms = 10.0;
	double grams = WeightUnit.GRAM.convertFromBaseUnit(
	                    WeightUnit.KILOGRAM.convertToBaseUnit(kilograms));

	System.out.println(kilograms + " kilograms in grams = " + grams);

double milligrams=WeightUnit.MILLIGRAM.convertFromBaseUnit(grams);
System.out.println(grams+" grams in milligram "+ milligrams);
}
}