package com.quantitymeasurementapp;
import com.quantitymeasurementapp.Length;
import com.quantitymeasurementapp.Length.LengthUnit;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class QuantityMeasurementAppTest {
	@Test
	public void addFeetandInches() {
		Length length1 = new Length(2.0,Length.LengthUnit.FEET);
		Length length2 = new Length(12.0,Length.LengthUnit.INCHES);
		Length sumLength = QuantityMeasurementApp.demonstrateLengthAddition(length1, length2);
		Length checkLength = new Length(1.0,Length.LengthUnit.YARDS);
		assertTrue(Length.demonstrateLengthEquality(sumLength, checkLength));
	}
	@Test
	public void checkLengthValue() {
		Length length1 = null;
		Length length2 = null;
		assertThrows(IllegalArgumentException.class,()->QuantityMeasurementApp.demonstrateLengthAddition(length1,length2));
	}
}