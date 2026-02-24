package com.quantitymeasurementapp;

import java.util.Objects;

public class Weight {
private static final double epsilon = 1e-4;
private double value;
private WeightUnit unit;

public Weight(double value,WeightUnit unit) {
	this.value=value;
	this.unit=unit;
}
public double getValue() {
	return value;
}
public WeightUnit getUnit() {
	return unit;
}
public boolean compare(Weight thatWeight) {
	return Math.abs(this.unit.convertToBaseUnit(this.value)-thatWeight.unit.convertToBaseUnit(thatWeight.value))<epsilon;
//return Double.compare(this.convertToBaseUnit() ,thatLength.convertToBaseUnit()) == 0;
}
@Override
public boolean equals(Object o) {
	if(this == o) return true;
	if(o==null) return false;
	if(this.getClass()!=o.getClass()) return false;
	Weight obj = (Weight) o;
	return this.compare(obj);
}
@Override
public int hashCode() {
	return Objects.hash(this.unit.convertToBaseUnit(value));
}
public Weight convertTo(WeightUnit targetUnit) {
	 if (targetUnit == null)
	        throw new IllegalArgumentException("Target unit cannot be null");
	    double baseValue = this.unit.convertToBaseUnit(this.getValue());           
	    double convertedValue = baseValue / targetUnit.getConversionFactor(); 
	    return new Weight(convertedValue, targetUnit);
}
public Weight add(Weight thatWeight) {
    return QuantityMeasurementApp.demonstrateWeightAddition(this, thatWeight, this.getUnit());
}
public Weight add(Weight weight , WeightUnit targetUnit) {
	return QuantityMeasurementApp.demonstrateWeightAddition(this, weight, targetUnit);
}
private Weight addAndConvert(Weight weight,WeightUnit targetUnit) {
	Weight w1 = QuantityMeasurementApp.demonstrateWeightAddition(this, weight, targetUnit);
	return w1.convertTo(targetUnit);
}
private double convertFromBaseUnitToTargetUnit(double weightInGrams,WeightUnit targetunit) {
	Weight w1 = new Weight(weightInGrams,WeightUnit.GRAM);
	Weight convertedWeight = w1.convertTo(targetunit);
	return convertedWeight.getValue();
}
@Override
public String toString() {
    return value + " " + unit;
}
}
