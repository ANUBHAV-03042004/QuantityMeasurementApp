package com.quantitymeasurementapp;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import com.quantitymeasurementapp.Length;
import com.quantitymeasurementapp.Length.LengthUnit;

import org.junit.jupiter.api.Test;


class QuantityMeasurementAppTest{

@Test
public void testFeetEquality() {
	Length length1 = new Length(1.0, LengthUnit.FEET);
	Length length2 = new Length(1.0,LengthUnit.FEET);
	assertTrue(Length.demonstrateLengthEquality(length1,length2));
}
@Test
public void testInchesEquality() {
	Length length1 = new Length(1.0, LengthUnit.INCHES);
	Length length2 = new Length(1.0,LengthUnit.INCHES);
	assertTrue(Length.demonstrateLengthEquality(length1,length2));
}
@Test
public void testFeetInchesComparison() {

	assertTrue(Length.demonstrateLengthComparison(1.0,LengthUnit.FEET,12.0,LengthUnit.INCHES));	
}
@Test
public void testFeetInequality() {
	Length length1 = new Length(1.0, LengthUnit.FEET);
	Length length2 = new Length(2.0,LengthUnit.FEET);
	assertFalse(Length.demonstrateLengthEquality(length1,length2));
}
@Test
public void testInchesInequality() {
	Length length1 = new Length(1.0, LengthUnit.INCHES);
	Length length2 = new Length(2.0,LengthUnit.INCHES);
	assertFalse(Length.demonstrateLengthEquality(length1,length2));
}
@Test
public void testCrossUnitInequality() {
	Length length1 = new Length(1.0, LengthUnit.INCHES);
	Length length2 = new Length(2.0,LengthUnit.FEET);
	assertFalse(Length.demonstrateLengthEquality(length1,length2));
}
@Test
public void testMultipleFeetComparison() {
	Length length1 = new Length(1.0,LengthUnit.FEET);
	Length length2 = new Length(12.0, LengthUnit.INCHES);
	Length length3 = new Length(2.0,LengthUnit.YARDS);
	assertAll(()->
		assertTrue(Length.demonstrateLengthEquality(length1, length2)),
		()->
		assertFalse(Length.demonstrateLengthEquality(length1, length3))
	);
}
@Test
public void yardEquals36Inches() {
	Length length1 = new Length(1.0,LengthUnit.YARDS);
	Length length2 = new Length(36.0, LengthUnit.INCHES);
	assertTrue(Length.demonstrateLengthEquality(length1, length2));
}
@Test
public void centimeterEquals39Point3701Inches() {
	Length length1 = new Length(1.0,LengthUnit.CENTIMETERS);
	Length length2 = new Length(39.3701, LengthUnit.INCHES);
	assertFalse(Length.demonstrateLengthEquality(length1, length2));
}
@Test
public void threeFeetEqualsOneYard() {
	Length length1 = new Length(3.0,LengthUnit.FEET);
	Length length2 = new Length(1.0, LengthUnit.YARDS);
	assertTrue(Length.demonstrateLengthEquality(length1, length2));
}
@Test
public void thirtyPoint48CmEqualsOneFoot() {
	Length length1 = new Length(30.48,LengthUnit.CENTIMETERS);
	Length length2 = new Length(1.0, LengthUnit.FEET);
	assertFalse(Length.demonstrateLengthEquality(length1, length2));
}
@Test
public void yardNotEqualToInches() {
	Length length1 = new Length(1.0,LengthUnit.YARDS);
	Length length2 = new Length(1.0, LengthUnit.INCHES);
	assertFalse(Length.demonstrateLengthEquality(length1, length2));
}
@Test
public void referenceEqualitySameObject() {
	Length length1 = new Length(1.0,LengthUnit.YARDS);
assertTrue(Length.demonstrateLengthEquality(length1, length1));
}
@Test
public void equalsReturnsFalseForNull() {
	Length length1 = new Length(1.0,LengthUnit.YARDS);
	assertFalse(Length.demonstrateLengthEquality(length1,null));
}
@Test
public void reflexiveSymmetricAndTransitiveProperty() {
	Length length1 = new Length(3.0,LengthUnit.FEET);
	Length length2 = new Length(36.0,LengthUnit.INCHES);
    Length length3 = new Length(1.0, Length.LengthUnit.YARDS);

	    assertAll(
//	    		reflexive
	        () -> assertTrue(Length.demonstrateLengthEquality(length1, length1)),

//	   symmetric
	        () -> assertTrue(length1.equals(length2)),
	        () -> assertTrue(length2.equals(length1)),
// transitive
	        () -> assertTrue(length1.equals(length2)),
	        () -> assertTrue(length2.equals(length3)),
	        () -> assertTrue(length1.equals(length3))
	    );

}
@Test
public void differentValuesSameUnitNotEqual() {
	Length length1 = new Length(3.0,LengthUnit.FEET);
	Length length2 = new Length(36.0,LengthUnit.FEET);
	
	assertFalse(Length.demonstrateLengthEquality(length1, length2));
}
@Test
public void crossUnitEqualityDemonstrateMethod() {
	assertFalse(Length.demonstrateLengthComparison(1.0, LengthUnit.FEET, 1.0, LengthUnit.YARDS));
	assertTrue(Length.demonstrateLengthComparison(1.0, LengthUnit.FEET, 12.0, LengthUnit.INCHES));
	assertTrue(Length.demonstrateLengthComparison(3.0, LengthUnit.FEET, 1.0, LengthUnit.YARDS));
	assertTrue(Length.demonstrateLengthComparison(100.0, LengthUnit.CENTIMETERS, 39.3701, LengthUnit.INCHES));
}
}
