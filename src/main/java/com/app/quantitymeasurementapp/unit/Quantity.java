package com.app.quantitymeasurementapp.unit;

public class Quantity<U extends Enum<U> & IMeasurable> {

    private final double value;
    private final U      unit;

    public Quantity(double value, U unit) {
        if (unit == null) throw new IllegalArgumentException("Unit cannot be null in Quantity");
        this.value = value;
        this.unit  = unit;
    }

    public double getValue() { return value; }
    public U      getUnit()  { return unit; }

    public Quantity<U> convertTo(U targetUnit) {
        if (targetUnit == null) throw new IllegalArgumentException("Target unit cannot be null");
        double base      = unit.convertToBaseUnit(value);
        double converted = targetUnit.convertFromBaseUnit(base);
        return new Quantity<>(converted, targetUnit);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Quantity)) return false;
        @SuppressWarnings("unchecked")
        Quantity<U> other = (Quantity<U>) obj;
        if (!this.unit.getMeasurementType().equals(other.unit.getMeasurementType())) return false;
        double thisBase  = this.unit.convertToBaseUnit(this.value);
        double otherBase = other.unit.convertToBaseUnit(other.value);
        return Math.abs(thisBase - otherBase) < 1e-9;
    }

    @Override
    public int hashCode() {
        double base = unit.convertToBaseUnit(value);
        return Double.hashCode(Math.round(base * 1e9) / 1e9);
    }

    public Quantity<U> add(Quantity<U> other) {
        unit.validateOperationSupport("ADD");
        double sumBase = unit.convertToBaseUnit(value) + other.unit.convertToBaseUnit(other.value);
        return new Quantity<>(unit.convertFromBaseUnit(sumBase), unit);
    }

    public Quantity<U> subtract(Quantity<U> other) {
        unit.validateOperationSupport("SUBTRACT");
        double diffBase = unit.convertToBaseUnit(value) - other.unit.convertToBaseUnit(other.value);
        return new Quantity<>(unit.convertFromBaseUnit(diffBase), unit);
    }

    public double divide(Quantity<U> other) {
        unit.validateOperationSupport("DIVIDE");
        double thisBase  = unit.convertToBaseUnit(value);
        double otherBase = other.unit.convertToBaseUnit(other.value);
        return ArithmeticOperation.DIVIDE.compute(thisBase, otherBase);
    }

    @Override
    public String toString() {
        return String.format("Quantity{value=%.4f, unit=%s}", value, unit.name());
    }
}
