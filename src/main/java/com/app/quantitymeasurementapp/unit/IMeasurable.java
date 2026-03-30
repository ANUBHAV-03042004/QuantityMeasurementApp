package com.app.quantitymeasurementapp.unit;

public interface IMeasurable {

    double toBaseValue(double value);

    double fromBaseValue(double baseValue);
}
