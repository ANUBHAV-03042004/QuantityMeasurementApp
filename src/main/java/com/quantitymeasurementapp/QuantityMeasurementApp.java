package com.quantitymeasurementapp;


public class QuantityMeasurementApp {

    public static <U extends Enum<U> & IMeasurable> Quantity<U> demonstrateQuantityAddition(
            Quantity<U> q1, Quantity<U> q2, U targetUnit) {

        validateQuantityOperands(q1, q2, targetUnit, "add");

        return q1.add(q2, targetUnit);
    }


    public static <U extends Enum<U> & IMeasurable> Quantity<U> demonstrateQuantityAddition(
            double v1, double v2, U targetUnit) {

        validateRawOperands(v1, v2, targetUnit);
        return new Quantity<>(v1 + v2, targetUnit);
    }

    public static <U extends Enum<U> & IMeasurable> Quantity<U> demonstrateQuantitySubtraction(
            Quantity<U> q1, Quantity<U> q2, U targetUnit) {

        validateQuantityOperands(q1, q2, targetUnit, "subtract");

        return q1.subtract(q2, targetUnit);
    }

    public static <U extends Enum<U> & IMeasurable> Quantity<U> demonstrateQuantitySubtraction(
            double v1, double v2, U targetUnit) {

        validateRawOperands(v1, v2, targetUnit);

        double result = v1 - v2;

        return new Quantity<>(result, targetUnit);
    }

    public static <U extends Enum<U> & IMeasurable> Quantity<U> demonstrateQuantityDivision(
            Quantity<U> q1, Quantity<U> q2, U targetUnit) {

        validateQuantityOperands(q1, q2, targetUnit, "divide");

  
        double ratio = q1.divide(q2);          // dimensionless; divide() throws on zero
        return new Quantity<>(ratio, targetUnit);
    }

   
    public static <U extends Enum<U> & IMeasurable> Quantity<U> demonstrateQuantityDivision(
            double v1, double v2, U targetUnit) {

        validateRawOperands(v1, v2, targetUnit);

        if (v2 == 0)
            throw new ArithmeticException("Division by zero not allowed");

        return new Quantity<>(v1 / v2, targetUnit);
    }

 
    private static <U extends Enum<U> & IMeasurable> void validateQuantityOperands(
            Quantity<U> q1, Quantity<U> q2, U targetUnit, String operation) {

        if (q1 == null || q2 == null)
            throw new IllegalArgumentException(
                    "Quantity operands must not be null for " + operation);

        if (targetUnit == null)
            throw new IllegalArgumentException(
                    "Target unit must not be null for " + operation);

        if (!Double.isFinite(q1.getValue()) || !Double.isFinite(q2.getValue()))
            throw new IllegalArgumentException(
                    "Quantity values must be finite for " + operation);

        if (!q1.getUnit().getClass().equals(q2.getUnit().getClass()))
            throw new IllegalArgumentException(
                    "Cannot " + operation + " different measurement categories: "
                    + q1.getUnit().getClass().getSimpleName()
                    + " vs "
                    + q2.getUnit().getClass().getSimpleName());
    }

  
    private static <U extends Enum<U> & IMeasurable> void validateRawOperands(
            double v1, double v2, U targetUnit) {

        if (targetUnit == null)
            throw new IllegalArgumentException("Target unit must not be null");

        if (!Double.isFinite(v1) || !Double.isFinite(v2))
            throw new IllegalArgumentException(
                    "Raw values must be finite; got: " + v1 + ", " + v2);

        if (v1 < 0 || v2 < 0)
            throw new IllegalArgumentException(
                    "Raw input values must be ≥ 0; got: " + v1 + ", " + v2);
    }


    public static void main(String[] args) {



        Quantity<LengthUnit> l1 = new Quantity<>(1.0,  LengthUnit.FEET);
        Quantity<LengthUnit> l2 = new Quantity<>(12.0, LengthUnit.INCHES);
        System.out.println("1 ft + 12 in          = " + l1.add(l2));             // 2.0 FEET

        Quantity<WeightUnit> w1 = new Quantity<>(10.0,   WeightUnit.KILOGRAM);
        Quantity<WeightUnit> w2 = new Quantity<>(5000.0, WeightUnit.GRAM);
        System.out.println("10 kg + 5000 g (GRAM) = " + w1.add(w2, WeightUnit.GRAM)); // 15000 GRAM

        Quantity<VolumeUnit> v1 = new Quantity<>(1.0, VolumeUnit.LITRE);
        Quantity<VolumeUnit> v2 = new Quantity<>(500.0, VolumeUnit.MILLILITRE);
        System.out.println("1 L + 500 mL          = " + v1.add(v2));             // 1.5 LITRE

        Quantity<LengthUnit> l3 = new Quantity<>(10.0, LengthUnit.FEET);
        Quantity<LengthUnit> l4 = new Quantity<>(6.0,  LengthUnit.INCHES);
        System.out.println("10 ft - 6 in           = " + l3.subtract(l4));       // 9.5 FEET

        Quantity<VolumeUnit> v3 = new Quantity<>(5.0, VolumeUnit.LITRE);
        Quantity<VolumeUnit> v4 = new Quantity<>(2.0, VolumeUnit.LITRE);
        System.out.println("5 L - 2 L (MILLILITRE) = " +
                v3.subtract(v4, VolumeUnit.MILLILITRE));                           // 3000.0 ML

 
        Quantity<LengthUnit> l5 = new Quantity<>(10.0, LengthUnit.FEET);
        Quantity<LengthUnit> l6 = new Quantity<>(2.0,  LengthUnit.FEET);
        System.out.printf("10 ft / 2 ft  = %.2f%n", l5.divide(l6));              // 5.00

        Quantity<LengthUnit> l7 = new Quantity<>(24.0, LengthUnit.INCHES);
        Quantity<LengthUnit> l8 = new Quantity<>(2.0,  LengthUnit.FEET);
        System.out.printf("24 in / 2 ft  = %.2f%n", l7.divide(l8));              // 1.00


        System.out.printf("ADD.compute(10, 5)      = %.1f%n", ArithmeticOperation.ADD.compute(10, 5));
        System.out.printf("SUBTRACT.compute(10, 5) = %.1f%n", ArithmeticOperation.SUBTRACT.compute(10, 5));
        System.out.printf("DIVIDE.compute(10, 2)   = %.1f%n", ArithmeticOperation.DIVIDE.compute(10, 2));
        System.out.printf("MULTIPLY.compute(4, 3)  = %.1f%n", ArithmeticOperation.MULTIPLY.compute(4, 3));

        try {
            new Quantity<>(10.0, LengthUnit.FEET).add(null);
        } catch (IllegalArgumentException e) {
            System.out.println("null operand    → " + e.getMessage());
        }

        try {
            Quantity<LengthUnit> len = new Quantity<>(10.0, LengthUnit.FEET);
            @SuppressWarnings("unchecked")
            Quantity<WeightUnit> wgt = new Quantity<>(5.0, WeightUnit.KILOGRAM);
            len.add((Quantity) wgt);   // cross-category: throws IAE
        } catch (IllegalArgumentException e) {
            System.out.println("cross-category  → " + e.getMessage());
        }

        try {
            new Quantity<>(10.0, LengthUnit.FEET)
                    .divide(new Quantity<>(0.0, LengthUnit.FEET));
        } catch (ArithmeticException e) {
            System.out.println("divide-by-zero  → " + e.getMessage());
        }
    }
}