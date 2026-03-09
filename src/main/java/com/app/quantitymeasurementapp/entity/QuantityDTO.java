package com.app.quantitymeasurementapp.entity;

public class QuantityDTO {

    public interface IMeasurableUnit {
        String getUnitName();
        String getMeasurementType();
    }

    public enum LengthUnit implements IMeasurableUnit {
        FEET, INCHES, YARDS, CENTIMETERS;
        @Override public String getUnitName()        { return name(); }
        @Override public String getMeasurementType() { return "LENGTH"; }
    }

    public enum WeightUnit implements IMeasurableUnit {
        MILLIGRAM, GRAM, KILOGRAM, POUND, TONNE;
        @Override public String getUnitName()        { return name(); }
        @Override public String getMeasurementType() { return "WEIGHT"; }
    }

    public enum VolumeUnit implements IMeasurableUnit {
        LITRE, MILLILITRE, GALLON;
        @Override public String getUnitName()        { return name(); }
        @Override public String getMeasurementType() { return "VOLUME"; }
    }

    public enum TemperatureUnit implements IMeasurableUnit {
        CELSIUS, FAHRENHEIT, KELVIN;
        @Override public String getUnitName()        { return name(); }
        @Override public String getMeasurementType() { return "TEMPERATURE"; }
    }

    private double          value;
    private IMeasurableUnit unit;

    public QuantityDTO() {}

    public QuantityDTO(double value, IMeasurableUnit unit) {
        this.value = value;
        this.unit  = unit;
    }

    public double          getValue() { return value; }
    public void            setValue(double value)           { this.value = value; }
    public IMeasurableUnit getUnit()  { return unit; }
    public void            setUnit(IMeasurableUnit unit)    { this.unit = unit; }

    @Override
    public String toString() {
        if (unit == null) return "QuantityDTO{value=" + value + ", unit=null}";
        return String.format("QuantityDTO{value=%s, unit=%s (%s)}", value, unit.getUnitName(), unit.getMeasurementType());
    }
}
