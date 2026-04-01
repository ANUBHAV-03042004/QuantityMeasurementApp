package com.app.quantitymeasurementapp.model;


public enum OperationType {

    COMPARE,
    CONVERT,
    ADD,
    SUBTRACT,
    MULTIPLY,
    DIVIDE;


    public static OperationType fromString(String value) {
        for (OperationType t : values()) {
            if (t.name().equalsIgnoreCase(value)) return t;
        }
        throw new IllegalArgumentException("Unknown OperationType: " + value);
    }
}
