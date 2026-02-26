package com.quantitymeasurementapp;
import com.quantitymeasurementapp.Quantity;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
public class QuantityMeasurementAppTest {

    @Test
    public void lengthFeetEqualsInches() {
        Quantity<LengthUnit> feet = new Quantity<>(1, LengthUnit.FEET);
        Quantity<LengthUnit> inches = new Quantity<>(12, LengthUnit.INCHES);
        assertEquals(feet, inches);
    }

    @Test
    public void lengthYardsEqualsFeet() {
        Quantity<LengthUnit> yards = new Quantity<>(1, LengthUnit.YARDS);
        Quantity<LengthUnit> feet = new Quantity<>(3, LengthUnit.FEET);
        assertEquals(yards, feet);
    }

    @Test
    public void weightKilogramEqualsGrams() {
        Quantity<WeightUnit> kg = new Quantity<>(1, WeightUnit.KILOGRAM);
        Quantity<WeightUnit> grams = new Quantity<>(1000, WeightUnit.GRAM);
        assertEquals(kg, grams);
    }

    @Test
    public void weightPoundEqualsGrams() {
        Quantity<WeightUnit> pound = new Quantity<>(1, WeightUnit.POUND);
        Quantity<WeightUnit> grams = new Quantity<>(453.592, WeightUnit.GRAM);
        assertEquals(pound, grams);
    }

    @Test
    public void convertLengthFeetToInches() {
        Quantity<LengthUnit> feet = new Quantity<>(1, LengthUnit.FEET);
        Quantity<LengthUnit> result = feet.convertTo(LengthUnit.INCHES);
        assertEquals(12, result.getValue());
    }

    @Test
    public void convertLengthYardsToInches() {
        Quantity<LengthUnit> yards = new Quantity<>(1, LengthUnit.YARDS);
        Quantity<LengthUnit> result = yards.convertTo(LengthUnit.INCHES);
        assertEquals(36, result.getValue());
    }

    @Test
    public void convertWeightKilogramsToGrams() {
        Quantity<WeightUnit> kg = new Quantity<>(1, WeightUnit.KILOGRAM);
        Quantity<WeightUnit> result = kg.convertTo(WeightUnit.GRAM);
        assertEquals(1000, result.getValue());
    }


    @Test
    public void addLengthFeetAndInches() {
        Quantity<LengthUnit> feet = new Quantity<>(1, LengthUnit.FEET);
        Quantity<LengthUnit> inches = new Quantity<>(12, LengthUnit.INCHES);

        Quantity<LengthUnit> result =
                feet.addQuantity(inches, LengthUnit.FEET);

        assertEquals(2, result.getValue());
    }

    @Test
    public void addLengthYardsAndFeet() {
        Quantity<LengthUnit> yards = new Quantity<>(1, LengthUnit.YARDS);
        Quantity<LengthUnit> feet = new Quantity<>(3, LengthUnit.FEET);

        Quantity<LengthUnit> result =
                yards.addQuantity(feet, LengthUnit.YARDS);

        assertEquals(2, result.getValue());
    }

    @Test
    public void addWeightKilogramsAndGrams() {
        Quantity<WeightUnit> kg = new Quantity<>(1, WeightUnit.KILOGRAM);
        Quantity<WeightUnit> grams = new Quantity<>(1000, WeightUnit.GRAM);

        Quantity<WeightUnit> result =
                kg.addQuantity(grams, WeightUnit.KILOGRAM);

        assertEquals(2, result.getValue());
    }

    @Test
    public void addWeightTonnesAndKilograms() {
        Quantity<WeightUnit> tonne = new Quantity<>(1, WeightUnit.TONNE);
        Quantity<WeightUnit> kg = new Quantity<>(1000, WeightUnit.KILOGRAM);

        Quantity<WeightUnit> result =
                tonne.addQuantity(kg, WeightUnit.TONNE);

        assertEquals(2, result.getValue());
    }

 
    @Test
    public void testGenericTypeSafetyWithWeight() {
        Quantity<WeightUnit> kg = new Quantity<>(1, WeightUnit.KILOGRAM);
        Quantity<WeightUnit> grams = new Quantity<>(1000, WeightUnit.GRAM);
        assertEquals(kg, grams);
    }


    @Test
    public void preventCrossTypeComparisonLengthVsWeight() {
        Quantity<LengthUnit> length = new Quantity<>(1, LengthUnit.FEET);
        Quantity<WeightUnit> weight = new Quantity<>(1, WeightUnit.KILOGRAM);

        assertNotEquals(length, weight);
    }

    @Test
    public void preventCrossTypeAdditionLengthVsWeight() {
        Quantity<LengthUnit> length = new Quantity<>(1, LengthUnit.FEET);
        Quantity<WeightUnit> weight = new Quantity<>(1, WeightUnit.KILOGRAM);

        assertThrows(IllegalArgumentException.class, () -> {
            // unsafe cast only for test simulation
            Quantity rawLength = (Quantity) length;
            rawLength.addQuantity((Quantity) weight, LengthUnit.FEET);
        });
    }

   

    @Test
    public void backwardCompatibilityLengthFeetEqualsInches() {
        lengthFeetEqualsInches();
    }

    @Test
    public void backwardCompatibilityWeightKilogramEqualsGrams() {
        weightKilogramEqualsGrams();
    }

    @Test
    public void backwardCompatibilityConvertLengthFeetToInches() {
        convertLengthFeetToInches();
    }

    @Test
    public void backwardCompatibilityConvertWeightKilogramsToGrams() {
        convertWeightKilogramsToGrams();
    }

    @Test
    public void backwardCompatibilityAddLengthInSameUnit() {
        Quantity<LengthUnit> f1 = new Quantity<>(1, LengthUnit.FEET);
        Quantity<LengthUnit> f2 = new Quantity<>(1, LengthUnit.FEET);
        Quantity<LengthUnit> result = f1.addQuantity(f2);
        assertEquals(2, result.getValue());
    }

    @Test
    public void backwardCompatibilityAddWeightInSameUnit() {
        Quantity<WeightUnit> k1 = new Quantity<>(1, WeightUnit.KILOGRAM);
        Quantity<WeightUnit> k2 = new Quantity<>(1, WeightUnit.KILOGRAM);
        Quantity<WeightUnit> result = k1.addQuantity(k2);
        assertEquals(2, result.getValue());
    }

    @Test
    public void backwardCompatibilityLengthYardsEqualsFeet() {
        lengthYardsEqualsFeet();
    }

    @Test
    public void backwardCompatibilityWeightPoundEqualsGrams() {
        weightPoundEqualsGrams();
    }

    @Test
    public void backwardCompatibilityChainedAdditionsLength() {
        Quantity<LengthUnit> f1 = new Quantity<>(1, LengthUnit.FEET);
        Quantity<LengthUnit> i1 = new Quantity<>(12, LengthUnit.INCHES);

        Quantity<LengthUnit> result =
                f1.addQuantity(i1)
                  .addQuantity(new Quantity<>(12, LengthUnit.INCHES));

        assertEquals(3, result.getValue());
    }
}