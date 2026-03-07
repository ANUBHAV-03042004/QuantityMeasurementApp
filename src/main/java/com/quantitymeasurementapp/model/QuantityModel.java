package com.quantitymeasurementapp.model;

import com.quantitymeasurementapp.core.IMeasurable;
import com.quantitymeasurementapp.core.Quantity;


public class QuantityModel<U extends Enum<U> & IMeasurable> {

    private final double value;
    private final U unit;

    public QuantityModel(double value, U unit) {
        if (unit == null)
            throw new IllegalArgumentException("Unit cannot be null in QuantityModel");
        this.value = value;
        this.unit  = unit;
    }

    public double getValue() { return value; }
    public U      getUnit()  { return unit;  }

    public Quantity<U> toQuantity() {
        return new Quantity<>(value, unit);
    }

    @Override
    public String toString() {
        return String.format("QuantityModel{value=%.4f, unit=%s (%s)}",
                value, unit.name(), unit.getMeasurementType());
    }
}
