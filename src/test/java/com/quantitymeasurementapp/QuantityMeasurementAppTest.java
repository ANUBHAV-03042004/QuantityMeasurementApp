package com.quantitymeasurementapp;
import com.quantitymeasurementapp.Length;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.*;

class QuantityMeasurementAppTest {
	@Test
	void testKilogramToGramEquality() {
	    Weight w1 = new Weight(1.0, WeightUnit.KILOGRAM);
	    Weight w2 = new Weight(1000.0, WeightUnit.GRAM);

	    assertEquals(w1, w2);
	}

	@Test
	void testKilogramToPoundEquality() {
	    Weight w1 = new Weight(1.0, WeightUnit.KILOGRAM);
	    Weight w2 = new Weight(2.20462, WeightUnit.POUND);

	    assertTrue(w1.equals(w2));
	}
	@Test
	void testConvertKgToGram() {
	    Weight w = new Weight(1.0, WeightUnit.KILOGRAM);
	    Weight result = w.convertTo(WeightUnit.GRAM);

	    assertEquals(1000.0, result.getValue(), 1e-6);
	}

	@Test
	void testConvertPoundToKg() {
	    Weight w = new Weight(2.20462, WeightUnit.POUND);
	    Weight result = w.convertTo(WeightUnit.KILOGRAM);

	    assertEquals(1.0, result.getValue(), 1e-4);
	}
	
	@Test
	void testAdditionSameUnit() {
	    Weight w1 = new Weight(1.0, WeightUnit.KILOGRAM);
	    Weight w2 = new Weight(2.0, WeightUnit.KILOGRAM);

	    Weight result = w1.add(w2);

	    assertEquals(3.0, result.getValue(), 1e-6);
	}

	@Test
	void testAdditionCrossUnit() {
	    Weight w1 = new Weight(1.0, WeightUnit.KILOGRAM);
	    Weight w2 = new Weight(1000.0, WeightUnit.GRAM);

	    Weight result = w1.add(w2);

	    assertEquals(2.0, result.getValue(), 1e-6);
	}
	
	@Test
	void testWeightVsLengthIncompatible() {
	    Weight weight = new Weight(1.0, WeightUnit.KILOGRAM);
	    Length length = new Length(1.0, LengthUnit.FEET);

	    assertFalse(weight.equals(length));
	}
}