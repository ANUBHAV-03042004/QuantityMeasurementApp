package com.quantitymeasurementapp;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.*;


class QuantityMeasurementAppTest {

    private static final double EPSILON = 1e-3;


    @Test
    @DisplayName("ADD.compute(10, 5) = 15.0")
    void testArithmeticOperation_Add_EnumComputation() {
        assertEquals(15.0, ArithmeticOperation.ADD.compute(10, 5), EPSILON);
    }

    @Test
    @DisplayName("SUBTRACT.compute(10, 5) = 5.0")
    void testArithmeticOperation_Subtract_EnumComputation() {
        assertEquals(5.0, ArithmeticOperation.SUBTRACT.compute(10, 5), EPSILON);
    }

    @Test
    @DisplayName("DIVIDE.compute(10, 2) = 5.0")
    void testArithmeticOperation_Divide_EnumComputation() {
        assertEquals(5.0, ArithmeticOperation.DIVIDE.compute(10, 2), EPSILON);
    }

    @Test
    @DisplayName("DIVIDE.compute(10, 0) throws ArithmeticException")
    void testArithmeticOperation_DivideByZero_EnumThrows() {
        assertThrows(ArithmeticException.class,
                () -> ArithmeticOperation.DIVIDE.compute(10, 0));
    }

    @Test
    @DisplayName("MULTIPLY.compute(4, 3) = 12.0")
    void testArithmeticOperation_Multiply_EnumComputation() {
        assertEquals(12.0, ArithmeticOperation.MULTIPLY.compute(4, 3), EPSILON);
    }

    @Test
    @DisplayName("ADD.compute(7, 3) = 10.0")
    void testEnumConstant_ADD_CorrectlyAdds() {
        assertEquals(10.0, ArithmeticOperation.ADD.compute(7, 3), EPSILON);
    }

    @Test
    @DisplayName("SUBTRACT.compute(7, 3) = 4.0")
    void testEnumConstant_SUBTRACT_CorrectlySubtracts() {
        assertEquals(4.0, ArithmeticOperation.SUBTRACT.compute(7, 3), EPSILON);
    }

    @Test
    @DisplayName("DIVIDE.compute(7, 2) = 3.5")
    void testEnumConstant_DIVIDE_CorrectlyDivides() {
        assertEquals(3.5, ArithmeticOperation.DIVIDE.compute(7, 2), EPSILON);
    }

    
    @Test
    @DisplayName("add(null), subtract(null), divide(null) all throw IllegalArgumentException")
    void testValidation_NullOperand_ConsistentAcrossOperations() {
        Quantity<LengthUnit> q = new Quantity<>(10.0, LengthUnit.FEET);
        assertThrows(IllegalArgumentException.class, () -> q.add(null));
        assertThrows(IllegalArgumentException.class, () -> q.subtract(null));
        assertThrows(IllegalArgumentException.class, () -> q.divide(null));
    }

    @Test
    @DisplayName("Cross-category add / subtract / divide all throw consistently")
    @SuppressWarnings("unchecked")
    void testValidation_CrossCategory_ConsistentAcrossOperations() {
        Quantity<LengthUnit> len = new Quantity<>(10.0, LengthUnit.FEET);
        Quantity<WeightUnit> wgt = new Quantity<>(5.0,  WeightUnit.KILOGRAM);
        assertThrows(IllegalArgumentException.class, () -> len.add((Quantity) wgt));
        assertThrows(IllegalArgumentException.class, () -> len.subtract((Quantity) wgt));
        assertThrows(IllegalArgumentException.class, () -> len.divide((Quantity) wgt));
    }

    @Test
    @DisplayName("NaN value in constructor throws IllegalArgumentException")
    void testValidation_NaN_ThrowsOnConstruction() {
        assertThrows(IllegalArgumentException.class,
                () -> new Quantity<>(Double.NaN, LengthUnit.FEET));
    }

    @Test
    @DisplayName("Infinite value in constructor throws IllegalArgumentException")
    void testValidation_PositiveInfinity_ThrowsOnConstruction() {
        assertThrows(IllegalArgumentException.class,
                () -> new Quantity<>(Double.POSITIVE_INFINITY, LengthUnit.FEET));
    }

    @Test
    @DisplayName("Null targetUnit in add(other, null) throws IllegalArgumentException")
    void testValidation_NullTargetUnit_AddRejects() {
        Quantity<LengthUnit> q1 = new Quantity<>(1.0, LengthUnit.FEET);
        Quantity<LengthUnit> q2 = new Quantity<>(12.0, LengthUnit.INCHES);
        assertThrows(IllegalArgumentException.class, () -> q1.add(q2, null));
    }

    @Test
    @DisplayName("Null targetUnit in subtract(other, null) throws IllegalArgumentException")
    void testValidation_NullTargetUnit_SubtractRejects() {
        Quantity<LengthUnit> q1 = new Quantity<>(10.0, LengthUnit.FEET);
        Quantity<LengthUnit> q2 = new Quantity<>(6.0, LengthUnit.INCHES);
        assertThrows(IllegalArgumentException.class, () -> q1.subtract(q2, null));
    }

    @Test
    @DisplayName("Null unit in constructor throws IllegalArgumentException")
    void testConstructor_NullUnit_Throws() {
        assertThrows(IllegalArgumentException.class,
                () -> new Quantity<>(1.0, null));
    }

    @Test
    @DisplayName("1 ft + 12 in = 2.0 ft (implicit target unit)")
    void testAdd_FeetPlusInches_ImplicitTarget() {
        Quantity<LengthUnit> result =
                new Quantity<>(1.0, LengthUnit.FEET)
                        .add(new Quantity<>(12.0, LengthUnit.INCHES));
        assertEquals(2.0, result.getValue(), EPSILON);
        assertEquals(LengthUnit.FEET, result.getUnit());
    }

    @Test
    @DisplayName("10 kg + 5000 g = 15000 g (explicit GRAM target)")
    void testAdd_KgPlusGram_ExplicitGramTarget() {
        Quantity<WeightUnit> result =
                new Quantity<>(10.0, WeightUnit.KILOGRAM)
                        .add(new Quantity<>(5000.0, WeightUnit.GRAM), WeightUnit.GRAM);
        assertEquals(15000.0, result.getValue(), EPSILON);
        assertEquals(WeightUnit.GRAM, result.getUnit());
    }

    @Test
    @DisplayName("1 L + 500 mL = 1.5 L (implicit LITRE target)")
    void testAdd_LitrePlusMillilitre_ImplicitTarget() {
        Quantity<VolumeUnit> result =
                new Quantity<>(1.0, VolumeUnit.LITRE)
                        .add(new Quantity<>(500.0, VolumeUnit.MILLILITRE));
        assertEquals(1.5, result.getValue(), EPSILON);
    }

    @Test
    @DisplayName("1 yd + 3 ft = 2.0 yd (cross-unit same category)")
    void testAdd_YardPlusFeet_InYards() {
        Quantity<LengthUnit> result =
                new Quantity<>(1.0, LengthUnit.YARDS)
                        .add(new Quantity<>(3.0, LengthUnit.FEET));
        assertEquals(2.0, result.getValue(), EPSILON);
    }

    @Test
    @DisplayName("10 ft - 6 in = 9.5 ft (implicit target)")
    void testSubtract_FeetMinusInches_ImplicitTarget() {
        Quantity<LengthUnit> result =
                new Quantity<>(10.0, LengthUnit.FEET)
                        .subtract(new Quantity<>(6.0, LengthUnit.INCHES));
        assertEquals(9.5, result.getValue(), EPSILON);
        assertEquals(LengthUnit.FEET, result.getUnit());
    }

    @Test
    @DisplayName("5 L - 2 L = 3000.0 mL (explicit MILLILITRE target)")
    void testSubtract_LitreMinusLitre_ExplicitMLTarget() {
        Quantity<VolumeUnit> result =
                new Quantity<>(5.0, VolumeUnit.LITRE)
                        .subtract(new Quantity<>(2.0, VolumeUnit.LITRE),
                                VolumeUnit.MILLILITRE);
        assertEquals(3000.0, result.getValue(), EPSILON);
        assertEquals(VolumeUnit.MILLILITRE, result.getUnit());
    }

    @Test
    @DisplayName("10 kg - 5000 g = 5.0 kg (implicit target)")
    void testSubtract_KgMinusGram_ImplicitKgTarget() {
        Quantity<WeightUnit> result =
                new Quantity<>(10.0, WeightUnit.KILOGRAM)
                        .subtract(new Quantity<>(5000.0, WeightUnit.GRAM));
        assertEquals(5.0, result.getValue(), EPSILON);
    }

    @Test
    @DisplayName("Subtraction is non-commutative: a-b ≠ b-a")
    void testSubtract_NonCommutativity() {
        Quantity<LengthUnit> q1 = new Quantity<>(10.0, LengthUnit.FEET);
        Quantity<LengthUnit> q2 = new Quantity<>(4.0,  LengthUnit.FEET);
        double ab = q1.subtract(q2).getValue();
        double ba = q2.subtract(q1).getValue();
        assertNotEquals(ab, ba, EPSILON);
    }

    @Test
    @DisplayName("10 ft / 2 ft = 5.0 (dimensionless scalar)")
    void testDivide_SameUnit_ReturnsDimensionlessScalar() {
        double result = new Quantity<>(10.0, LengthUnit.FEET)
                .divide(new Quantity<>(2.0, LengthUnit.FEET));
        assertEquals(5.0, result, EPSILON);
    }

    @Test
    @DisplayName("24 in / 2 ft = 1.0 (cross-unit same category)")
    void testDivide_InchesOverFeet_CrossUnit() {
        double result = new Quantity<>(24.0, LengthUnit.INCHES)
                .divide(new Quantity<>(2.0, LengthUnit.FEET));
        assertEquals(1.0, result, EPSILON);
    }

    @Test
    @DisplayName("10 ft / 0 ft throws ArithmeticException")
    void testDivide_ByZero_Throws() {
        assertThrows(ArithmeticException.class, () ->
                new Quantity<>(10.0, LengthUnit.FEET)
                        .divide(new Quantity<>(0.0, LengthUnit.FEET)));
    }

    @Test
    @DisplayName("Division is non-commutative: a/b ≠ b/a (unless equal)")
    void testDivide_NonCommutativity() {
        double ab = new Quantity<>(10.0, LengthUnit.FEET)
                .divide(new Quantity<>(2.0, LengthUnit.FEET));
        double ba = new Quantity<>(2.0, LengthUnit.FEET)
                .divide(new Quantity<>(10.0, LengthUnit.FEET));
        assertNotEquals(ab, ba, EPSILON);
    }

    @Test
    @DisplayName("10 kg / 2 kg = 5.0 (weight category)")
    void testDivide_WeightCategory() {
        double result = new Quantity<>(10.0, WeightUnit.KILOGRAM)
                .divide(new Quantity<>(2.0, WeightUnit.KILOGRAM));
        assertEquals(5.0, result, EPSILON);
    }

   
    @Test
    @DisplayName("Implicit target: result unit equals first operand's unit")
    void testImplicitTargetUnit_AddUsesFirstOperandUnit() {
        Quantity<LengthUnit> q = new Quantity<>(1.0, LengthUnit.YARDS)
                .add(new Quantity<>(36.0, LengthUnit.INCHES));
        assertEquals(LengthUnit.YARDS, q.getUnit());
    }

    @Test
    @DisplayName("Explicit target overrides first operand's unit")
    void testExplicitTargetUnit_OverridesImplicit() {
        Quantity<LengthUnit> q = new Quantity<>(1.0, LengthUnit.FEET)
                .add(new Quantity<>(12.0, LengthUnit.INCHES), LengthUnit.INCHES);
        assertEquals(LengthUnit.INCHES, q.getUnit());
        assertEquals(24.0, q.getValue(), EPSILON);
    }

    @Test
    @DisplayName("Subtract: explicit CENTIMETERS target works correctly")
    void testExplicitTargetUnit_Subtract_Centimeters() {
        // 1 ft = 30.48 cm; 6 in = 15.24 cm; difference = 15.24 cm
        Quantity<LengthUnit> result =
                new Quantity<>(1.0, LengthUnit.FEET)
                        .subtract(new Quantity<>(6.0, LengthUnit.INCHES),
                                LengthUnit.CENTIMETERS);
        assertEquals(15.24, result.getValue(), 0.01);
        assertEquals(LengthUnit.CENTIMETERS, result.getUnit());
    }

    @Test
    @DisplayName("Add/subtract results are rounded to two decimal places")
    void testRounding_AddSubtract_TwoDecimalPlaces() {
        // 1 cm = 0.393701 in; 2 cm = 0.787402 in → rounded 0.79
        Quantity<LengthUnit> result =
                new Quantity<>(1.0, LengthUnit.CENTIMETERS)
                        .add(new Quantity<>(1.0, LengthUnit.CENTIMETERS),
                                LengthUnit.INCHES);
        String str = String.valueOf(result.getValue());
        // Verify at most 2 decimal places
        int dotIndex = str.indexOf('.');
        if (dotIndex >= 0) {
            assertTrue(str.length() - dotIndex - 1 <= 2,
                    "Expected at most 2 decimal places, got: " + str);
        }
    }

    @Test
    @DisplayName("Division returns raw double without forced rounding")
    void testRounding_Divide_NoForcedRounding() {
        // 7 / 3 = 2.333... — should NOT be rounded to 2 d.p.
        double result = new Quantity<>(7.0, LengthUnit.FEET)
                .divide(new Quantity<>(3.0, LengthUnit.FEET));
        assertTrue(result > 2.33 && result < 2.34,
                "Expected ~2.333, got: " + result);
    }

    @Test
    @DisplayName("Original quantities unchanged after add()")
    void testImmutability_AfterAdd() {
        Quantity<LengthUnit> q1 = new Quantity<>(1.0, LengthUnit.FEET);
        Quantity<LengthUnit> q2 = new Quantity<>(12.0, LengthUnit.INCHES);
        q1.add(q2);
        assertEquals(1.0, q1.getValue(), EPSILON);
        assertEquals(12.0, q2.getValue(), EPSILON);
    }

    @Test
    @DisplayName("Original quantities unchanged after subtract()")
    void testImmutability_AfterSubtract() {
        Quantity<LengthUnit> q1 = new Quantity<>(10.0, LengthUnit.FEET);
        Quantity<LengthUnit> q2 = new Quantity<>(6.0,  LengthUnit.INCHES);
        q1.subtract(q2);
        assertEquals(10.0, q1.getValue(), EPSILON);
        assertEquals(6.0,  q2.getValue(), EPSILON);
    }

    @Test
    @DisplayName("Original quantities unchanged after divide()")
    void testImmutability_AfterDivide() {
        Quantity<LengthUnit> q1 = new Quantity<>(10.0, LengthUnit.FEET);
        Quantity<LengthUnit> q2 = new Quantity<>(2.0,  LengthUnit.FEET);
        q1.divide(q2);
        assertEquals(10.0, q1.getValue(), EPSILON);
        assertEquals(2.0,  q2.getValue(), EPSILON);
    }

    @Test
    @DisplayName("Add works for Length, Weight, and Volume categories")
    void testAllOperations_Add_AcrossAllCategories() {
        assertDoesNotThrow(() -> {
            new Quantity<>(1.0, LengthUnit.FEET).add(new Quantity<>(12.0, LengthUnit.INCHES));
            new Quantity<>(1.0, WeightUnit.KILOGRAM).add(new Quantity<>(1000.0, WeightUnit.GRAM));
            new Quantity<>(1.0, VolumeUnit.LITRE).add(new Quantity<>(1000.0, VolumeUnit.MILLILITRE));
        });
    }

    @Test
    @DisplayName("Subtract works for Length, Weight, and Volume categories")
    void testAllOperations_Subtract_AcrossAllCategories() {
        assertDoesNotThrow(() -> {
            new Quantity<>(2.0, LengthUnit.FEET).subtract(new Quantity<>(12.0, LengthUnit.INCHES));
            new Quantity<>(2.0, WeightUnit.KILOGRAM).subtract(new Quantity<>(500.0, WeightUnit.GRAM));
            new Quantity<>(2.0, VolumeUnit.LITRE).subtract(new Quantity<>(500.0, VolumeUnit.MILLILITRE));
        });
    }

    @Test
    @DisplayName("Divide works for Length, Weight, and Volume categories")
    void testAllOperations_Divide_AcrossAllCategories() {
        assertDoesNotThrow(() -> {
            new Quantity<>(10.0, LengthUnit.FEET).divide(new Quantity<>(2.0, LengthUnit.FEET));
            new Quantity<>(10.0, WeightUnit.KILOGRAM).divide(new Quantity<>(2.0, WeightUnit.KILOGRAM));
            new Quantity<>(10.0, VolumeUnit.LITRE).divide(new Quantity<>(2.0, VolumeUnit.LITRE));
        });
    }

  
    @Test
    @DisplayName("Add zero returns original value")
    void testAdd_ZeroOperand_ReturnsOriginal() {
        Quantity<LengthUnit> result =
                new Quantity<>(5.0, LengthUnit.FEET)
                        .add(new Quantity<>(0.0, LengthUnit.FEET));
        assertEquals(5.0, result.getValue(), EPSILON);
    }

    @Test
    @DisplayName("Subtract zero returns original value")
    void testSubtract_ZeroOperand_ReturnsOriginal() {
        Quantity<LengthUnit> result =
                new Quantity<>(5.0, LengthUnit.FEET)
                        .subtract(new Quantity<>(0.0, LengthUnit.FEET));
        assertEquals(5.0, result.getValue(), EPSILON);
    }

   
    @Test
    @DisplayName("Chain: add then subtract returns correct result")
    void testArithmetic_ChainAddSubtract() {
        // 1 ft + 12 in = 2 ft; 2 ft - 12 in = 1 ft
        Quantity<LengthUnit> result =
                new Quantity<>(1.0, LengthUnit.FEET)
                        .add(new Quantity<>(12.0, LengthUnit.INCHES))
                        .subtract(new Quantity<>(12.0, LengthUnit.INCHES));
        assertEquals(1.0, result.getValue(), EPSILON);
    }

    @Test
    @DisplayName("Chain: add twice accumulates correctly")
    void testArithmetic_ChainDoubleAdd() {
        Quantity<WeightUnit> result =
                new Quantity<>(1.0, WeightUnit.KILOGRAM)
                        .add(new Quantity<>(1000.0, WeightUnit.GRAM))
                        .add(new Quantity<>(1.0,    WeightUnit.KILOGRAM));
        assertEquals(3.0, result.getValue(), EPSILON);
    }

    @Test
    @DisplayName("Null-operand messages are non-empty and consistent")
    void testErrorMessage_Consistency_NullOperand() {
        Quantity<LengthUnit> q = new Quantity<>(5.0, LengthUnit.FEET);
        String msgAdd = assertThrows(IllegalArgumentException.class,
                () -> q.add(null)).getMessage();
        String msgSub = assertThrows(IllegalArgumentException.class,
                () -> q.subtract(null)).getMessage();
        String msgDiv = assertThrows(IllegalArgumentException.class,
                () -> q.divide(null)).getMessage();

        assertNotNull(msgAdd); assertFalse(msgAdd.isBlank());
        assertNotNull(msgSub); assertFalse(msgSub.isBlank());
        assertNotNull(msgDiv); assertFalse(msgDiv.isBlank());
        // All three must carry the same message (single source in validateArithmeticOperands)
        assertEquals(msgAdd, msgSub);
        assertEquals(msgSub, msgDiv);
    }

    @Test
    @DisplayName("UC12 compat: 1 ft + 12 in = 2.0 ft")
    void testAdd_UC12_BehaviorPreserved_FeetInches() {
        Quantity<LengthUnit> r = new Quantity<>(1.0, LengthUnit.FEET)
                .add(new Quantity<>(12.0, LengthUnit.INCHES));
        assertEquals(2.0, r.getValue(), EPSILON);
    }

    @Test
    @DisplayName("UC12 compat: 10 ft - 6 in = 9.5 ft")
    void testSubtract_UC12_BehaviorPreserved_FeetInches() {
        Quantity<LengthUnit> r = new Quantity<>(10.0, LengthUnit.FEET)
                .subtract(new Quantity<>(6.0, LengthUnit.INCHES));
        assertEquals(9.5, r.getValue(), EPSILON);
    }

    @Test
    @DisplayName("UC12 compat: 10 ft / 2 ft = 5.0")
    void testDivide_UC12_BehaviorPreserved_Feet() {
        double r = new Quantity<>(10.0, LengthUnit.FEET)
                .divide(new Quantity<>(2.0, LengthUnit.FEET));
        assertEquals(5.0, r, EPSILON);
    }

    @Test
    @DisplayName("toString includes unit name and value")
    void testToString_ContainsUnitAndValue() {
        String s = new Quantity<>(3.0, LengthUnit.FEET).toString();
        assertTrue(s.contains("FEET"));
        assertTrue(s.contains("3.0") || s.contains("3,0"));
    }
}