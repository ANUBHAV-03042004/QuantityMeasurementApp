package com.quantitymeasurementapp;

public class QuantityMeasurementApp {

    public static <U extends IMeasurable> Quantity<U> demonstrateQuantityAddition(
            Quantity<U> length1, Quantity<U> length2, U targetUnit) {

        if (length1 == null || length2 == null)
            throw new IllegalArgumentException("Quantity should not be null");

        if (targetUnit == null)
            throw new IllegalArgumentException("Target Unit should not be null");

        if (!Double.isFinite(length1.getValue()) || !Double.isFinite(length2.getValue()))
            throw new IllegalArgumentException("Quantity should be in finite range");

        if (!length1.getUnit().getClass().equals(length2.getUnit().getClass()))
            throw new IllegalArgumentException("Cannot add different unit types");

        Quantity<U> convertedLength1 = length1.convertTo(targetUnit);
        Quantity<U> convertedLength2 = length2.convertTo(targetUnit);

        double sumValue = convertedLength1.getValue() + convertedLength2.getValue();

        return new Quantity<>(sumValue, targetUnit);
    }

    public static <U extends IMeasurable> Quantity<U> demonstrateQuantityAddition(
            double length1, double length2, U targetUnit) {

        if (length1 < 0 || length2 < 0)
            throw new IllegalArgumentException("Quantity should not be less than zero");

        if (targetUnit == null)
            throw new IllegalArgumentException("Target Unit should not be null");

        if (!Double.isFinite(length1) || !Double.isFinite(length2))
            throw new IllegalArgumentException("Quantity value should be in finite range");

        return new Quantity<>(length1 + length2, targetUnit);
    }

    public static <U extends IMeasurable> Quantity<U> demonstrateQuantitySubtraction(
            Quantity<U> length1, Quantity<U> length2, U targetUnit) {

        if (length1 == null || length2 == null)
            throw new IllegalArgumentException("Quantity should not be null");

        if (targetUnit == null)
            throw new IllegalArgumentException("Target Unit should not be null");

        if (!Double.isFinite(length1.getValue()) || !Double.isFinite(length2.getValue()))
            throw new IllegalArgumentException("Quantity should be in finite range");

        if (!length1.getUnit().getClass().equals(length2.getUnit().getClass()))
            throw new IllegalArgumentException("Cannot subtract different unit types");

        Quantity<U> convertedLength1 = length1.convertTo(targetUnit);
        Quantity<U> convertedLength2 = length2.convertTo(targetUnit);

        double result = convertedLength1.getValue() - convertedLength2.getValue();

        return new Quantity<>(result, targetUnit);
    }

    public static <U extends IMeasurable> Quantity<U> demonstrateQuantitySubtraction(
            double length1, double length2, U targetUnit) {

        if (length1 < 0 || length2 < 0)
            throw new IllegalArgumentException("Quantity should not be less than zero");

        if (targetUnit == null)
            throw new IllegalArgumentException("Target Unit should not be null");

        if (!Double.isFinite(length1) || !Double.isFinite(length2))
            throw new IllegalArgumentException("Quantity value should be in finite range");

        double result = length1 - length2;

        return new Quantity<>(result, targetUnit);
    }

    public static <U extends IMeasurable> Quantity<U> demonstrateQuantityDivision(
            Quantity<U> length1, Quantity<U> length2, U targetUnit) {

        if (length1 == null || length2 == null)
            throw new IllegalArgumentException("Quantity should not be null");

        if (targetUnit == null)
            throw new IllegalArgumentException("Target Unit should not be null");

        if (!Double.isFinite(length1.getValue()) || !Double.isFinite(length2.getValue()))
            throw new IllegalArgumentException("Quantity should be in finite range");

        if (!length1.getUnit().getClass().equals(length2.getUnit().getClass()))
            throw new IllegalArgumentException("Cannot divide different unit types");

        Quantity<U> convertedLength1 = length1.convertTo(targetUnit);
        Quantity<U> convertedLength2 = length2.convertTo(targetUnit);

        if (convertedLength2.getValue() == 0)
            throw new ArithmeticException("Division by zero not allowed");

        double result = convertedLength1.getValue() / convertedLength2.getValue();

        return new Quantity<>(result, targetUnit);
    }

    public static <U extends IMeasurable> Quantity<U> demonstrateQuantityDivision(
            double length1, double length2, U targetUnit) {

        if (length1 < 0 || length2 < 0)
            throw new IllegalArgumentException("Quantity should not be less than zero");

        if (targetUnit == null)
            throw new IllegalArgumentException("Target Unit should not be null");

        if (!Double.isFinite(length1) || !Double.isFinite(length2))
            throw new IllegalArgumentException("Quantity value should be in finite range");

        if (length2 == 0)
            throw new ArithmeticException("Division by zero not allowed");

        return new Quantity<>(length1 / length2, targetUnit);
    }

    public static void main(String[] args) {

        Quantity<LengthUnit> length1 = new Quantity<>(2.0, LengthUnit.FEET);
        Quantity<LengthUnit> length2 = new Quantity<>(200.0, LengthUnit.CENTIMETERS);

        System.out.println("Are equal? " + length1.equals(length2));

        Quantity<LengthUnit> sumLength =
                demonstrateQuantityAddition(length1, length2, LengthUnit.INCHES);

        System.out.println("Sum in inches: " + sumLength);

        Quantity<WeightUnit> weight1 = new Quantity<>(1.0, WeightUnit.KILOGRAM);
        Quantity<WeightUnit> weight2 = new Quantity<>(500.0, WeightUnit.GRAM);

        System.out.println("Are equal? " + weight1.equals(weight2));

        Quantity<WeightUnit> sumWeight =
                demonstrateQuantityAddition(weight1, weight2, WeightUnit.GRAM);

        System.out.println("Sum in grams: " + sumWeight);

//        Quantity<LengthUnit> sub1 =
//                demonstrateQuantitySubtraction(length1, length2, LengthUnit.FEET);
//
//        System.out.println("Length subtraction: " + sub1);

        Quantity<WeightUnit> sub2 =
                demonstrateQuantitySubtraction(weight1, weight2, WeightUnit.KILOGRAM);

        System.out.println("Weight subtraction: " + sub2);

        Quantity<LengthUnit> l1 = new Quantity<>(10.0, LengthUnit.FEET);
        Quantity<LengthUnit> l2 = new Quantity<>(2.0, LengthUnit.FEET);

        Quantity<LengthUnit> div1 =
                demonstrateQuantityDivision(l1, l2, LengthUnit.FEET);

        System.out.println("10 feet / 2 feet = " + div1);

        Quantity<WeightUnit> w1 = new Quantity<>(2000.0, WeightUnit.GRAM);
        Quantity<WeightUnit> w2 = new Quantity<>(1.0, WeightUnit.KILOGRAM);

        Quantity<WeightUnit> div2 =
                demonstrateQuantityDivision(w1, w2, WeightUnit.GRAM);

        System.out.println("2000 g / 1 kg = " + div2);

        Quantity<WeightUnit> addDouble =
                demonstrateQuantityAddition(200, 300, WeightUnit.GRAM);

        System.out.println("200g + 300g = " + addDouble);

        Quantity<WeightUnit> subDouble =
                demonstrateQuantitySubtraction(500, 200, WeightUnit.GRAM);

        System.out.println("500g - 200g = " + subDouble);

        Quantity<WeightUnit> divDouble =
                demonstrateQuantityDivision(400, 200, WeightUnit.GRAM);

        System.out.println("400g / 200g = " + divDouble);
    }
}