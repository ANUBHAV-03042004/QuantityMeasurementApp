package com.app.quantitymeasurementapp.unit;

public class Quantity<U extends Enum<U> & IMeasurable> {

    private final double value;
    private final U unit;

    public Quantity(double value, U unit) {
        this.value = value;
        this.unit  = unit;
    }

    public double getValue() { return value; }
    public U      getUnit()  { return unit;  }

    public Quantity<U> convertTo(U targetUnit) {
        double base      = unit.toBaseValue(value);
        double converted = targetUnit.fromBaseValue(base);
        return new Quantity<>(converted, targetUnit);
    }

    public Quantity<U> add(Quantity<U> other) {
        double base   = unit.toBaseValue(value) + other.unit.toBaseValue(other.value);
        return new Quantity<>(unit.fromBaseValue(base), unit);
    }

    public Quantity<U> subtract(Quantity<U> other) {
        double base = unit.toBaseValue(value) - other.unit.toBaseValue(other.value);
        return new Quantity<>(unit.fromBaseValue(base), unit);
    }

    public double divide(Quantity<U> other) {
        double denominator = other.unit.toBaseValue(other.value);
        if (denominator == 0.0) {
            throw new ArithmeticException("Divide by zero");
        }
        return unit.toBaseValue(value) / denominator;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Quantity)) return false;
        @SuppressWarnings("unchecked")
        Quantity<U> other = (Quantity<U>) obj;
        double thisBase  = this.unit.toBaseValue(this.value);
        double otherBase = other.unit.toBaseValue(other.value);
        return Math.abs(thisBase - otherBase) < 1e-9;
    }

    @Override
    public int hashCode() {
        return Double.hashCode(unit.toBaseValue(value));
    }

    @Override
    public String toString() {
        return value + " " + unit.name();
    }
}
