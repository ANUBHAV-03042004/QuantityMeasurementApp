package com.quantitymeasurementapp;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
// test cases
class QuantityMeasurementAppTest{

	@Test
	void testEquality_FeetToFeet_SameValue() {

		assertEquals(true, new Length(1.0, Length.LengthUnit.FEET).equals(new Length(1.0, Length.LengthUnit.FEET)));
		
	}
	
	@Test
	void testEquality_InchToInch_SameValue() {
		
		assertEquals(true, new Length(1.0, Length.LengthUnit.INCHES).equals(new Length(1.0, Length.LengthUnit.INCHES)));
		
	}
	
	@Test
	void testEquality_FeetToInch_SameValue() {
		
		assertEquals(true, new Length(1.0, Length.LengthUnit.FEET).equals(new Length(12.0, Length.LengthUnit.INCHES)));
		
	}
	
	@Test
	void testEquality_InchToFeet_SameValue() {
		
		assertEquals(true, new Length(12.0, Length.LengthUnit.INCHES).equals(new Length(1.0, Length.LengthUnit.FEET)));
		
	}
	
	@Test
	void testEquality_FeetToFeet_DifferentValue() {
		
		assertEquals(false, new Length(1.0, Length.LengthUnit.FEET).equals(new Length(2.0, Length.LengthUnit.FEET)));
		
	}
	
	@Test
	void testEquality_InchToInch_DifferentValue() {
		
		assertEquals(true, new Length(1.0, Length.LengthUnit.FEET).equals(new Length(1.0, Length.LengthUnit.FEET)));
		
	}
	
	@Test
	void testEquality_InvalidUnit() {

		assertThrows(IllegalArgumentException.class, ()->new Length(1.0, null));
		
	}
	
	@Test
    void testEquality_NullUnit() {

        assertThrows(IllegalArgumentException.class, ()->new Length(5.0, null));
        
    }
	
	@Test
    void testEquality_SameReference() {
		
		assertTrue(new Length(10, Length.LengthUnit.INCHES).equals(new Length(10, Length.LengthUnit.INCHES)));
		
	}
	
	@Test
	void testEquality_NullComparision() {
		
		assertFalse(new Length(10, Length.LengthUnit.INCHES).equals(null));
		
	}
}
