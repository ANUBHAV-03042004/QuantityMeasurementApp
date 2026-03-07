package com.quantitymeasurementapp;


public class Quantity<U extends Enum<U> & IMeasurable> {

    private final double value;
    private final U unit;

    private static final double EPSILON = 1e-3;

   
    public Quantity(double value, U unit) {
       
        if (!Double.isFinite(value))
            throw new IllegalArgumentException(
                    "Value must be a finite number, got: " + value);
        if (unit == null)
            throw new IllegalArgumentException("Unit cannot be null");
        this.value = value;
        this.unit  = unit;
    }

    public double getValue() { return value; }
    public U      getUnit()  { return unit;  }

   
    public boolean compare(Quantity<U> that) {
        if (that == null) return false;

        double thisBase  = roundToTwoDecimals(this.unit.convertToBaseUnit(this.value));
        double otherBase = roundToTwoDecimals(that.unit.convertToBaseUnit(that.value));

        return Math.abs(thisBase - otherBase) <= EPSILON;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Quantity<?> other)) return false;

       
        Class<?> thisCategory  = ((Enum<?>) this.unit).getDeclaringClass();
        Class<?> otherCategory = ((Enum<?>) other.unit).getDeclaringClass();
        if (!thisCategory.equals(otherCategory)) return false;

        return this.compare((Quantity<U>) other);
    }

    @Override
    public int hashCode() {
        long rounded = Math.round(unit.convertToBaseUnit(value) / EPSILON);
        return Long.hashCode(rounded);
    }

    public static boolean demonstrateEquality(Quantity<?> q1, Quantity<?> q2) {
        return q1.equals(q2);
    }

   
//    public Quantity<U> convertTo(U targetUnit) {
//        if (targetUnit == null)
//            throw new IllegalArgumentException("Target unit cannot be null");
//        double baseValue      = this.unit.convertToBaseUnit(this.value);
//        double convertedValue = targetUnit.convertFromBaseUnit(baseValue);
//        return new Quantity<>(convertedValue, targetUnit);
//    }
    
    
    public Quantity<U> convertTo(U targetUnit) {
        if (targetUnit == null)
            throw new IllegalArgumentException("Target unit cannot be null");

        double baseValue      = this.unit.convertToBaseUnit(this.value);
        double convertedValue = targetUnit.convertFromBaseUnit(baseValue);
        return new Quantity<>(convertedValue, targetUnit);
    }

  
    private void validateArithmeticOperands(Quantity<U> other,
                                             U targetUnit,
                                             boolean targetUnitRequired) {
        if (other == null)
            throw new IllegalArgumentException("Operand cannot be null.");

        if (!this.unit.getClass().equals(other.unit.getClass()))
            throw new IllegalArgumentException(
                    "Cannot perform arithmetic on different measurement categories: "
                    + this.unit.getClass().getSimpleName()
                    + " vs "
                    + other.unit.getClass().getSimpleName());

        if (!Double.isFinite(this.value))
            throw new IllegalArgumentException(
                    "This quantity's value must be finite, got: " + this.value);

        if (!Double.isFinite(other.value))
            throw new IllegalArgumentException(
                    "Operand value must be finite, got: " + other.value);

        if (targetUnitRequired && targetUnit == null)
            throw new IllegalArgumentException(
                    "Target unit cannot be null for add/subtract operations.");
    }



    private double performBaseArithmetic(Quantity<U> other,
            ArithmeticOperation operation) {

double baseThis  = this.unit.convertToBaseUnit(this.value);
double baseOther = other.unit.convertToBaseUnit(other.value);

return operation.compute(baseThis, baseOther);
}

    private static double roundToTwoDecimals(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

   
    public Quantity<U> add(Quantity<U> other) {
        return add(other, this.unit);
    }


    public Quantity<U> add(Quantity<U> other, U targetUnit) {

        validateArithmeticOperands(other, targetUnit, true);

        unit.validateOperationSupport("ADD");

        double baseResult = performBaseArithmetic(other, ArithmeticOperation.ADD);
        double convertedResult = targetUnit.convertFromBaseUnit(baseResult);

        return new Quantity<>(convertedResult, targetUnit);
    }
   
    public Quantity<U> subtract(Quantity<U> other) {
        return subtract(other, this.unit);
    }

   
    public Quantity<U> subtract(Quantity<U> other, U targetUnit) {

        validateArithmeticOperands(other, targetUnit, true);

        unit.validateOperationSupport("SUBTRACT");

        double baseResult = performBaseArithmetic(other, ArithmeticOperation.SUBTRACT);
        double convertedResult = targetUnit.convertFromBaseUnit(baseResult);

        return new Quantity<>(convertedResult, targetUnit);
    }

    public double divide(Quantity<U> other) {

        validateArithmeticOperands(other, null, false);

        unit.validateOperationSupport("DIVIDE");

        return performBaseArithmetic(other, ArithmeticOperation.DIVIDE);
    }
   
    @Override
    public String toString() {
        return String.format("%s %.4f (%s base: %.4f)",
                unit, value, unit.getUnitName(), unit.convertToBaseUnit(value));
    }
}