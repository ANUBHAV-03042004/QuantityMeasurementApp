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

//    public static <U extends Enum<U> & IMeasurable> Quantity<U> demonstrateQuantityDivision(
//            Quantity<U> q1, Quantity<U> q2, U targetUnit) {
//
//        validateQuantityOperands(q1, q2, targetUnit, "divide");
//
//  
//        double ratio = q1.divide(q2);          // dimensionless; divide() throws on zero
//        return new Quantity<>(ratio, targetUnit);
//    }
    
    
    public static <U extends Enum<U> & IMeasurable> double demonstrateQuantityDivision(Quantity<U> q1, Quantity<U> q2) {

        validateQuantityOperands(q1, q2, q1.getUnit(), "divide");

        return q1.divide(q2);
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

        System.out.println("100°C == 100 ft     : " +
                new Quantity<>(100.0, TemperatureUnit.CELSIUS)
                        .equals(new Quantity<>(100.0, LengthUnit.FEET)));          // false

        System.out.println("50°C == 50 kg       : " +
                new Quantity<>(50.0, TemperatureUnit.CELSIUS)
                        .equals(new Quantity<>(50.0, WeightUnit.KILOGRAM)));       // false


        System.out.println("CELSIUS.supportsArithmetic()  = " +
                TemperatureUnit.CELSIUS.supportsArithmetic());                     // false
        System.out.println("CELSIUS.supportsAddition()    = " +
                TemperatureUnit.CELSIUS.supportsAddition());                       // false
        System.out.println("CELSIUS.supportsDivision()    = " +
                TemperatureUnit.CELSIUS.supportsDivision());                       // false
        System.out.println("FEET.supportsArithmetic()     = " +
                LengthUnit.FEET.supportsArithmetic());                             // true
        System.out.println("KILOGRAM.supportsAddition()   = " +
                WeightUnit.KILOGRAM.supportsAddition());       

    }
}