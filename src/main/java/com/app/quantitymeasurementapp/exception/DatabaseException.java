package com.app.quantitymeasurementapp.exception;

public class DatabaseException extends RuntimeException {

    private final String operation;

    public DatabaseException(String message) {
        super(message);
        this.operation = "UNKNOWN";
    }

    public DatabaseException(String message, String operation) {
        super(message);
        this.operation = operation;
    }

    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
        this.operation = "UNKNOWN";
    }

    public DatabaseException(String message, String operation, Throwable cause) {
        super(message, cause);
        this.operation = operation;
    }

    public String getOperation() {
        return operation;
    }

    @Override
    public String toString() {
        return String.format("DatabaseException{operation=%s, message=%s}", operation, getMessage());
    }
}
