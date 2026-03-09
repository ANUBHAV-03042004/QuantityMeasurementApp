package com.app.quantitymeasurementapp.unit;

import java.util.function.DoubleBinaryOperator;

public enum ArithmeticOperation {

    ADD((a, b) -> a + b),
    SUBTRACT((a, b) -> a - b),
    DIVIDE((a, b) -> {
        if (b == 0.0) throw new ArithmeticException("Division by zero is not allowed.");
        return a / b;
    }),
    MULTIPLY((a, b) -> a * b);

    private final DoubleBinaryOperator operator;

    ArithmeticOperation(DoubleBinaryOperator operator) {
        this.operator = operator;
    }

    public double compute(double a, double b) {
        return operator.applyAsDouble(a, b);
    }
}
