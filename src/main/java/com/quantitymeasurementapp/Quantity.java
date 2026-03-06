package com.quantitymeasurementapp;


public class Quantity<U extends Enum<U> & IMeasurable> {

    private final double value;
    private final U unit;

    private static final double EPSILON = 1e-4;

   
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
        return Math.abs(this.unit.convertToBaseUnit(this.value)
                      - that.getUnit().convertToBaseUnit(that.getValue())) < EPSILON;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Quantity)) return false;
        Quantity<?> other = (Quantity<?>) o;
        if (!this.unit.getClass().equals(other.unit.getClass())) return false;
        double thisBase  = this.unit.convertToBaseUnit(this.value);
        @SuppressWarnings("unchecked")
        double otherBase = ((U) other.unit).convertToBaseUnit(other.value);
        return Double.compare(thisBase, otherBase) == 0;
    }

    @Override
    public int hashCode() {
        return Double.hashCode(unit.convertToBaseUnit(value));
    }

    public static boolean demonstrateEquality(Quantity<?> q1, Quantity<?> q2) {
        return q1.equals(q2);
    }

   
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
        double baseResult     = performBaseArithmetic(other, ArithmeticOperation.ADD);
        double convertedResult = targetUnit.convertFromBaseUnit(baseResult);
        return new Quantity<>(roundToTwoDecimals(convertedResult), targetUnit);
    }

   
    public Quantity<U> subtract(Quantity<U> other) {
        return subtract(other, this.unit);
    }

   
    public Quantity<U> subtract(Quantity<U> other, U targetUnit) {
        validateArithmeticOperands(other, targetUnit, true);
        double baseResult      = performBaseArithmetic(other, ArithmeticOperation.SUBTRACT);
        double convertedResult = targetUnit.convertFromBaseUnit(baseResult);
        return new Quantity<>(roundToTwoDecimals(convertedResult), targetUnit);
    }

    public double divide(Quantity<U> other) {
        validateArithmeticOperands(other, null, false);
        return performBaseArithmetic(other, ArithmeticOperation.DIVIDE);
    }

   
    @Override
    public String toString() {
        return String.format("%s %.4f (%s base: %.4f)",
                unit, value, unit.getUnitName(), unit.convertToBaseUnit(value));
    }
}