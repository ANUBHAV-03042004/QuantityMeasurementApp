package com.quantitymeasurementapp;
import com.quantitymeasurementapp.Length;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.*;

class QuantityMeasurementAppTest {
	public static final double EPSILON = 1e-4;
	@Test
	public void addFeetandInches() {
		Length length1 = new Length(2.0,LengthUnit.FEET);
		Length length2 = new Length(12.0,LengthUnit.INCHES);
		Length sumLength = QuantityMeasurementApp.demonstrateLengthAddition(length1, length2, LengthUnit.YARDS);
		Length checkLength = new Length(1.0,LengthUnit.YARDS);
		assertTrue(Length.demonstrateLengthEquality(sumLength, checkLength));
	}
	@Test
	public void checkLengthValue() {
		Length length1 = null;
		Length length2 = null;
		LengthUnit unit=null;
		assertThrows(IllegalArgumentException.class,()->QuantityMeasurementApp.demonstrateLengthAddition(length1,length2,unit));
	}
	@Test
	public void checkInstanceAddLength() {
		Length length1 = new Length(2.0,LengthUnit.FEET);
		Length length2 = new Length(12.0,LengthUnit.INCHES);
		Length sumLength=length1.addLength(length2, LengthUnit.INCHES);
		assertEquals(36.0,sumLength.getValue(),EPSILON);
	    assertEquals(LengthUnit.INCHES, sumLength.getUnit());

	}
}