package com.quantitymeasurementapp;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class QuantityMeasurementAppTest {

    @Nested
    class FeetEqualityTests {

    
        @Test
        @DisplayName("Given 1.0 ft and 1.0 ft When compared Then should be Equal")
        public void givenSameFeetValue_WhenCompared_ThenShouldBeEqual() {
            // GIVEN – two Feet objects with the same value
            QuantityMeasurementApp.Feet feet1 = new QuantityMeasurementApp.Feet(1.0);
            QuantityMeasurementApp.Feet feet2 = new QuantityMeasurementApp.Feet(1.0);

            // WHEN & THEN – equality check r̥must return true
            Assertions.assertEquals(feet1, feet2,
                    "Two Feet objects with the same value (1.0) should be equal");
        }

     
        @Test
        @DisplayName("Given 1.0 ft and 2.0 ft When compared Then should NOT be Equal")
        public void givenDifferentFeetValues_WhenCompared_ThenShouldNotBeEqual() {
            // GIVEN – two Feet objects with different values
            QuantityMeasurementApp.Feet feet1 = new QuantityMeasurementApp.Feet(1.0);
            QuantityMeasurementApp.Feet feet2 = new QuantityMeasurementApp.Feet(2.0);

            // WHEN & THEN – equality check must return false
            Assertions.assertNotEquals(feet1, feet2,
                    "Two Feet objects with different values (1.0 vs 2.0) should NOT be equal");
        }

   
        @Test
        @DisplayName("Given 1.0 ft and null When compared Then should NOT be Equal")
        public void givenFeetValueAndNull_WhenCompared_ThenShouldNotBeEqual() {
            // GIVEN – a Feet object and null
            QuantityMeasurementApp.Feet feet1 = new QuantityMeasurementApp.Feet(1.0);

            // WHEN & THEN – equals(null) must return false (Equality Contract)
            Assertions.assertNotEquals(feet1, null,
                    "A Feet object should NOT be equal to null");
        }

     
        @Test
        @DisplayName("Given 1.0 ft and a String When compared Then should NOT be Equal")
        public void givenFeetValueAndNonNumericInput_WhenCompared_ThenShouldNotBeEqual() {
            // GIVEN – a Feet object and a completely different type (String)
            QuantityMeasurementApp.Feet feet1 = new QuantityMeasurementApp.Feet(1.0);
            String nonNumeric = "abc";

            // WHEN & THEN – type check in equals() must return false
            Assertions.assertNotEquals(feet1, nonNumeric,
                    "A Feet object should NOT be equal to a non-numeric / different type");
        }

     
        @Test
        @DisplayName("Given 1.0 ft and same reference When compared Then should be Equal")
        public void givenSameFeetReference_WhenCompared_ThenShouldBeEqual() {
            // GIVEN – one Feet object
            QuantityMeasurementApp.Feet feet1 = new QuantityMeasurementApp.Feet(1.0);

            // WHEN & THEN – reflexive property: a.equals(a) must be true
            Assertions.assertEquals(feet1, feet1,
                    "A Feet object must be equal to itself (reflexive property)");
        }
    }

 
    @Nested
    @DisplayName("UC2 – Inches Equality Tests")
    class InchesEqualityTests {

     
        @Test
        @DisplayName("Given 1.0 in and 1.0 in When compared Then should be Equal")
        public void givenSameInchesValue_WhenCompared_ThenShouldBeEqual() {
            // GIVEN – two Inches objects with the same value
            QuantityMeasurementApp.Inches inches1 = new QuantityMeasurementApp.Inches(1.0);
            QuantityMeasurementApp.Inches inches2 = new QuantityMeasurementApp.Inches(1.0);

            // WHEN & THEN – equality check must return true
            Assertions.assertEquals(inches1, inches2,
                    "Two Inches objects with the same value (1.0) should be equal");
        }
   
        @Test
        @DisplayName("Given 1.0 in and 2.0 in When compared Then should NOT be Equal")
        public void givenDifferentInchesValues_WhenCompared_ThenShouldNotBeEqual() {
            // GIVEN – two Inches objects with different values
            QuantityMeasurementApp.Inches inches1 = new QuantityMeasurementApp.Inches(1.0);
            QuantityMeasurementApp.Inches inches2 = new QuantityMeasurementApp.Inches(2.0);

            // WHEN & THEN – equality check must return false
            Assertions.assertNotEquals(inches1, inches2,
                    "Two Inches objects with different values (1.0 vs 2.0) should NOT be equal");
        }

    
        @Test
        @DisplayName("Given 1.0 in and null When compared Then should NOT be Equal")
        public void givenInchesValueAndNull_WhenCompared_ThenShouldNotBeEqual() {
            // GIVEN – an Inches object and null
            QuantityMeasurementApp.Inches inches1 = new QuantityMeasurementApp.Inches(1.0);

            // WHEN & THEN – equals(null) must return false
            Assertions.assertNotEquals(inches1, null,
                    "An Inches object should NOT be equal to null");
        }

   
        @Test
        @DisplayName("Given 1.0 in and a String When compared Then should NOT be Equal")
        public void givenInchesValueAndNonNumericInput_WhenCompared_ThenShouldNotBeEqual() {
            // GIVEN – an Inches object and a different type (String)
            QuantityMeasurementApp.Inches inches1 = new QuantityMeasurementApp.Inches(1.0);
            String nonNumeric = "abc";

            // WHEN & THEN – type check in equals() must block this
            Assertions.assertNotEquals(inches1, nonNumeric,
                    "An Inches object should NOT be equal to a non-numeric / different type");
        }

     
        @Test
        @DisplayName("Given 1.0 in and same reference When compared Then should be Equal")
        public void givenSameInchesReference_WhenCompared_ThenShouldBeEqual() {
            // GIVEN – one Inches object
            QuantityMeasurementApp.Inches inches1 = new QuantityMeasurementApp.Inches(1.0);

            // WHEN & THEN – reflexive property: a.equals(a) must be true
            Assertions.assertEquals(inches1, inches1,
                    "An Inches object must be equal to itself (reflexive property)");
        }
    }

  
    @Nested
    @DisplayName("UC2 – Cross-type (Feet vs Inches) Tests")
    class CrossTypeTests {

        @Test
        @DisplayName("Given 1.0 ft and 1.0 in When compared Then should NOT be Equal (different types)")
        public void givenFeetAndInchesWithSameNumericValue_WhenCompared_ThenShouldNotBeEqual() {
            // GIVEN – Feet and Inches both holding value 1.0
            QuantityMeasurementApp.Feet   feet   = new QuantityMeasurementApp.Feet(1.0);
            QuantityMeasurementApp.Inches inches = new QuantityMeasurementApp.Inches(1.0);

            // WHEN & THEN – different classes, so equals() must return false
            Assertions.assertNotEquals(feet, inches,
                    "A Feet object and an Inches object should NEVER be equal "
                  + "even if they hold the same numerical value, as they are different types");
        }
    }

    @Nested
    @DisplayName("UC2 – Static Helper Method Tests")
    class StaticHelperMethodTests {

        @Test
        @DisplayName("compareFeet(1.0, 1.0) should return true")
        public void givenSameFeetValues_WhenUsingStaticHelper_ThenShouldReturnTrue() {
            Assertions.assertTrue(
                    QuantityMeasurementApp.compareFeet(1.0, 1.0),
                    "compareFeet(1.0, 1.0) should return true");
        }

        @Test
        @DisplayName("compareFeet(1.0, 2.0) should return false")
        public void givenDifferentFeetValues_WhenUsingStaticHelper_ThenShouldReturnFalse() {
            Assertions.assertFalse(
                    QuantityMeasurementApp.compareFeet(1.0, 2.0),
                    "compareFeet(1.0, 2.0) should return false");
        }

        @Test
        @DisplayName("compareInches(1.0, 1.0) should return true")
        public void givenSameInchesValues_WhenUsingStaticHelper_ThenShouldReturnTrue() {
            Assertions.assertTrue(
                    QuantityMeasurementApp.compareInches(1.0, 1.0),
                    "compareInches(1.0, 1.0) should return true");
        }

        @Test
        @DisplayName("compareInches(1.0, 2.0) should return false")
        public void givenDifferentInchesValues_WhenUsingStaticHelper_ThenShouldReturnFalse() {
            Assertions.assertFalse(
                    QuantityMeasurementApp.compareInches(1.0, 2.0),
                    "compareInches(1.0, 2.0) should return false");
        }
    }
}