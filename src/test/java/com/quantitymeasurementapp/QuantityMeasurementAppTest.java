package com.quantitymeasurementapp;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


public class QuantityMeasurementAppTest {

    @Test
    @DisplayName("Given 1.0 ft and 1.0 ft When compared Then should be Equal")
    public void givenSameFeetValue_WhenCompared_ThenShouldBeEqual() {
        QuantityMeasurementApp.Feet feet1 = new QuantityMeasurementApp.Feet(1.0);
        QuantityMeasurementApp.Feet feet2 = new QuantityMeasurementApp.Feet(1.0);
        Assertions.assertEquals(feet1, feet2,
                "Two Feet objects with the same value should be equal");
    }

  
    @Test
    @DisplayName("Given 1.0 ft and 2.0 ft When compared Then should NOT be Equal")
    public void givenDifferentFeetValues_WhenCompared_ThenShouldNotBeEqual() {
        QuantityMeasurementApp.Feet feet1 = new QuantityMeasurementApp.Feet(1.0);
        QuantityMeasurementApp.Feet feet2 = new QuantityMeasurementApp.Feet(2.0);
        Assertions.assertNotEquals(feet1, feet2,
                "Two Feet objects with different values should NOT be equal");
    }

    @Test
    @DisplayName("Given 1.0 ft and null When compared Then should NOT be Equal")
    public void givenFeetValueAndNull_WhenCompared_ThenShouldNotBeEqual() {
        QuantityMeasurementApp.Feet feet1 = new QuantityMeasurementApp.Feet(1.0);
        Assertions.assertNotEquals(feet1, null,
                "A Feet object should NOT be equal to null");
    }

  
    @Test
    @DisplayName("Given 1.0 ft and a String When compared Then should NOT be Equal")
    public void givenFeetValueAndNonNumericInput_WhenCompared_ThenShouldNotBeEqual() {
        QuantityMeasurementApp.Feet feet1 = new QuantityMeasurementApp.Feet(1.0);
        String nonNumeric = "abc";
        Assertions.assertNotEquals(feet1, nonNumeric,
                "A Feet object should NOT be equal to a non-numeric type");
    }

    @Test
    @DisplayName("Given 1.0 ft and same reference When compared Then should be Equal")
    public void givenSameReference_WhenCompared_ThenShouldBeEqual() {
        QuantityMeasurementApp.Feet feet1 = new QuantityMeasurementApp.Feet(1.0);
        Assertions.assertEquals(feet1, feet1,
                "A Feet object should be equal to itself (reflexive property)");
    }
}