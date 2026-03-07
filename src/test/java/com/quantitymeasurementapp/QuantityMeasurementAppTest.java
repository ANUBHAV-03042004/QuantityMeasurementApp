package com.quantitymeasurementapp;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.*;


class QuantityMeasurementAppTest {

    private static final double EPSILON = 1e-3;

    
    @Test
    @DisplayName("0°C equals 0°C (same unit, same value)")
    void testTemperatureEquality_CelsiusToCelsius_SameValue() {
        assertTrue(new Quantity<>(0.0, TemperatureUnit.CELSIUS)
                .equals(new Quantity<>(0.0, TemperatureUnit.CELSIUS)));
    }

    @Test
    @DisplayName("32°F equals 32°F (same unit, same value)")
    void testTemperatureEquality_FahrenheitToFahrenheit_SameValue() {
        assertTrue(new Quantity<>(32.0, TemperatureUnit.FAHRENHEIT)
                .equals(new Quantity<>(32.0, TemperatureUnit.FAHRENHEIT)));
    }

    @Test
    @DisplayName("273.15 K equals 273.15 K (same unit)")
    void testTemperatureEquality_KelvinToKelvin_SameValue() {
        assertTrue(new Quantity<>(273.15, TemperatureUnit.KELVIN)
                .equals(new Quantity<>(273.15, TemperatureUnit.KELVIN)));
    }

 
    @Test
    @DisplayName("0°C equals 32°F")
    void testTemperatureEquality_CelsiusToFahrenheit_0Celsius32Fahrenheit() {
        assertTrue(new Quantity<>(0.0, TemperatureUnit.CELSIUS)
                .equals(new Quantity<>(32.0, TemperatureUnit.FAHRENHEIT)));
    }

    @Test
    @DisplayName("100°C equals 212°F")
    void testTemperatureEquality_CelsiusToFahrenheit_100Celsius212Fahrenheit() {
        assertTrue(new Quantity<>(100.0, TemperatureUnit.CELSIUS)
                .equals(new Quantity<>(212.0, TemperatureUnit.FAHRENHEIT)));
    }

    @Test
    @DisplayName("-40°C equals -40°F (intersection point)")
    void testTemperatureEquality_CelsiusToFahrenheit_Negative40Equal() {
        assertTrue(new Quantity<>(-40.0, TemperatureUnit.CELSIUS)
                .equals(new Quantity<>(-40.0, TemperatureUnit.FAHRENHEIT)));
    }

    @Test
    @DisplayName("0°C equals 273.15 K")
    void testTemperatureEquality_CelsiusToKelvin_0Celsius273Kelvin() {
        assertTrue(new Quantity<>(0.0, TemperatureUnit.CELSIUS)
                .equals(new Quantity<>(273.15, TemperatureUnit.KELVIN)));
    }

    @Test
    @DisplayName("100°C equals 373.15 K")
    void testTemperatureEquality_CelsiusToKelvin_100Celsius373Kelvin() {
        Quantity<TemperatureUnit> celsius  = new Quantity<>(100.0, TemperatureUnit.CELSIUS);
        Quantity<TemperatureUnit> kelvin   = new Quantity<>(373.15, TemperatureUnit.KELVIN);
        assertTrue(celsius.equals(kelvin));
    }

    @Test
    @DisplayName("273.15 K equals 32°F (multi-step cross-unit)")
    void testTemperatureEquality_KelvinToFahrenheit() {
        Quantity<TemperatureUnit> kelvin      = new Quantity<>(273.15, TemperatureUnit.KELVIN);
        Quantity<TemperatureUnit> fahrenheit  = new Quantity<>(32.0,   TemperatureUnit.FAHRENHEIT);
        assertTrue(kelvin.equals(fahrenheit));
    }

    @Test
    @DisplayName("Reflexive: temperature equals itself")
    void testTemperatureEquality_ReflexiveProperty() {
        Quantity<TemperatureUnit> t = new Quantity<>(50.0, TemperatureUnit.CELSIUS);
        assertTrue(t.equals(t));
    }

    @Test
    @DisplayName("Symmetric: if A == B then B == A")
    void testTemperatureEquality_SymmetricProperty() {
        Quantity<TemperatureUnit> celsius    = new Quantity<>(0.0,  TemperatureUnit.CELSIUS);
        Quantity<TemperatureUnit> fahrenheit = new Quantity<>(32.0, TemperatureUnit.FAHRENHEIT);
        assertTrue(celsius.equals(fahrenheit));
        assertTrue(fahrenheit.equals(celsius));
    }

    @Test
    @DisplayName("Transitive: A==B and B==C implies A==C")
    void testTemperatureEquality_TransitiveProperty() {
        Quantity<TemperatureUnit> celsius    = new Quantity<>(0.0,    TemperatureUnit.CELSIUS);
        Quantity<TemperatureUnit> fahrenheit = new Quantity<>(32.0,   TemperatureUnit.FAHRENHEIT);
        Quantity<TemperatureUnit> kelvin     = new Quantity<>(273.15, TemperatureUnit.KELVIN);
        assertTrue(celsius.equals(fahrenheit));
        assertTrue(fahrenheit.equals(kelvin));
        assertTrue(celsius.equals(kelvin));
    }

    @Test
    @DisplayName("50°C does NOT equal 100°C")
    void testTemperatureDifferentValuesInequality() {
        assertFalse(new Quantity<>(50.0, TemperatureUnit.CELSIUS)
                .equals(new Quantity<>(100.0, TemperatureUnit.CELSIUS)));
    }

    @Test
    @DisplayName("equals(null) returns false")
    void testTemperatureEquality_NullReturnsFalse() {
        assertFalse(new Quantity<>(100.0, TemperatureUnit.CELSIUS).equals(null));
    }

    @Test
    @DisplayName("100°C → 212°F")
    void testTemperatureConversion_CelsiusToFahrenheit_100() {
        Quantity<TemperatureUnit> result = new Quantity<>(100.0, TemperatureUnit.CELSIUS)
                .convertTo(TemperatureUnit.FAHRENHEIT);
        assertEquals(212.0, result.getValue(), EPSILON);
        assertEquals(TemperatureUnit.FAHRENHEIT, result.getUnit());
    }

    @Test
    @DisplayName("32°F → 0°C")
    void testTemperatureConversion_FahrenheitToCelsius_32() {
        Quantity<TemperatureUnit> result = new Quantity<>(32.0, TemperatureUnit.FAHRENHEIT)
                .convertTo(TemperatureUnit.CELSIUS);
        assertEquals(0.0, result.getValue(), EPSILON);
    }

    @Test
    @DisplayName("212°F → 100°C")
    void testTemperatureConversion_FahrenheitToCelsius_212() {
        Quantity<TemperatureUnit> result = new Quantity<>(212.0, TemperatureUnit.FAHRENHEIT)
                .convertTo(TemperatureUnit.CELSIUS);
        assertEquals(100.0, result.getValue(), EPSILON);
    }

    @Test
    @DisplayName("0°C → 273.15 K")
    void testTemperatureConversion_CelsiusToKelvin_0() {
        Quantity<TemperatureUnit> result = new Quantity<>(0.0, TemperatureUnit.CELSIUS)
                .convertTo(TemperatureUnit.KELVIN);
        assertEquals(273.15, result.getValue(), EPSILON);
    }

    @Test
    @DisplayName("273.15 K → 0°C")
    void testTemperatureConversion_KelvinToCelsius() {
        Quantity<TemperatureUnit> result = new Quantity<>(273.15, TemperatureUnit.KELVIN)
                .convertTo(TemperatureUnit.CELSIUS);
        assertEquals(0.0, result.getValue(), EPSILON);
    }

    @Test
    @DisplayName("-40°C → -40°F (equal point)")
    void testTemperatureConversion_NegativeForty_EqualPoint() {
        Quantity<TemperatureUnit> result = new Quantity<>(-40.0, TemperatureUnit.CELSIUS)
                .convertTo(TemperatureUnit.FAHRENHEIT);
        assertEquals(-40.0, result.getValue(), EPSILON);
    }

    @Test
    @DisplayName("Same unit conversion returns unchanged value (0°C → 0°C)")
    void testTemperatureConversion_SameUnit_ReturnsUnchanged() {
        Quantity<TemperatureUnit> result = new Quantity<>(0.0, TemperatureUnit.CELSIUS)
                .convertTo(TemperatureUnit.CELSIUS);
        assertEquals(0.0, result.getValue(), EPSILON);
    }

    @Test
    @DisplayName("Round-trip: °C → °F → °C preserves original value")
    void testTemperatureConversion_RoundTrip_CelsiusFahrenheitCelsius() {
        double original = 50.0;
        double roundTrip = new Quantity<>(original, TemperatureUnit.CELSIUS)
                .convertTo(TemperatureUnit.FAHRENHEIT)
                .convertTo(TemperatureUnit.CELSIUS)
                .getValue();
        assertEquals(original, roundTrip, EPSILON);
    }

    @Test
    @DisplayName("Round-trip: K → °C → K preserves original value")
    void testTemperatureConversion_RoundTrip_KelvinCelsiusKelvin() {
        double original = 373.15;
        double roundTrip = new Quantity<>(original, TemperatureUnit.KELVIN)
                .convertTo(TemperatureUnit.CELSIUS)
                .convertTo(TemperatureUnit.KELVIN)
                .getValue();
        assertEquals(original, roundTrip, EPSILON);
    }

    @Test
    @DisplayName("Large value: 1000°C → 1832°F")
    void testTemperatureConversion_LargeValue() {
        Quantity<TemperatureUnit> result = new Quantity<>(1000.0, TemperatureUnit.CELSIUS)
                .convertTo(TemperatureUnit.FAHRENHEIT);
        assertEquals(1832.0, result.getValue(), EPSILON);
    }

   
    @Test
    @DisplayName("add() on temperature throws UnsupportedOperationException")
    void testTemperatureUnsupportedOperation_Add() {
        Quantity<TemperatureUnit> t = new Quantity<>(100.0, TemperatureUnit.CELSIUS);
        assertThrows(UnsupportedOperationException.class,
                () -> t.add(new Quantity<>(50.0, TemperatureUnit.CELSIUS)));
    }

    @Test
    @DisplayName("subtract() on temperature throws UnsupportedOperationException")
    void testTemperatureUnsupportedOperation_Subtract() {
        Quantity<TemperatureUnit> t = new Quantity<>(100.0, TemperatureUnit.CELSIUS);
        assertThrows(UnsupportedOperationException.class,
                () -> t.subtract(new Quantity<>(50.0, TemperatureUnit.CELSIUS)));
    }

    @Test
    @DisplayName("divide() on temperature throws UnsupportedOperationException")
    void testTemperatureUnsupportedOperation_Divide() {
        Quantity<TemperatureUnit> t = new Quantity<>(100.0, TemperatureUnit.CELSIUS);
        assertThrows(UnsupportedOperationException.class,
                () -> t.divide(new Quantity<>(50.0, TemperatureUnit.CELSIUS)));
    }

    @Test
    @DisplayName("validateOperationSupport() throws with informative message")
    void testTemperatureUnsupportedOperation_ErrorMessageClear() {
        UnsupportedOperationException ex = assertThrows(
                UnsupportedOperationException.class,
                () -> TemperatureUnit.CELSIUS.validateOperationSupport("ADD"));
        assertNotNull(ex.getMessage());
        assertFalse(ex.getMessage().isBlank());
        assertTrue(ex.getMessage().toLowerCase().contains("temperature"));
    }

    @Test
    @DisplayName("add(null) still throws IllegalArgumentException (null check before op-support check)")
    void testTemperatureNullOperand_StillThrowsIAE() {
        assertThrows(IllegalArgumentException.class,
                () -> new Quantity<>(100.0, TemperatureUnit.CELSIUS).add(null));
    }

    @Test
    @DisplayName("100°C does not equal 100 FEET (different categories)")
    void testTemperatureVsLengthIncompatibility() {
        assertFalse(new Quantity<>(100.0, TemperatureUnit.CELSIUS)
                .equals(new Quantity<>(100.0, LengthUnit.FEET)));
    }

    @Test
    @DisplayName("50°C does not equal 50 KILOGRAM")
    void testTemperatureVsWeightIncompatibility() {
        assertFalse(new Quantity<>(50.0, TemperatureUnit.CELSIUS)
                .equals(new Quantity<>(50.0, WeightUnit.KILOGRAM)));
    }

    @Test
    @DisplayName("25°C does not equal 25 LITRE")
    void testTemperatureVsVolumeIncompatibility() {
        assertFalse(new Quantity<>(25.0, TemperatureUnit.CELSIUS)
                .equals(new Quantity<>(25.0, VolumeUnit.LITRE)));
    }

    @Test
    @DisplayName("CELSIUS.supportsArithmetic() == false")
    void testOperationSupport_TemperatureUnit_ArithmeticFalse() {
        assertFalse(TemperatureUnit.CELSIUS.supportsArithmetic());
    }

    @Test
    @DisplayName("CELSIUS.supportsAddition() == false")
    void testOperationSupportMethods_TemperatureUnitAddition() {
        assertFalse(TemperatureUnit.CELSIUS.supportsAddition());
    }

    @Test
    @DisplayName("FAHRENHEIT.supportsDivision() == false")
    void testOperationSupportMethods_TemperatureUnitDivision() {
        assertFalse(TemperatureUnit.FAHRENHEIT.supportsDivision());
    }

    @Test
    @DisplayName("KELVIN.supportsAddition() == false")
    void testOperationSupport_KelvinAdditionFalse() {
        assertFalse(TemperatureUnit.KELVIN.supportsAddition());
    }

    @Test
    @DisplayName("LengthUnit.FEET.supportsAddition() == true (inherits default)")
    void testOperationSupportMethods_LengthUnitAddition() {
        assertTrue(LengthUnit.FEET.supportsAddition());
    }

    @Test
    @DisplayName("WeightUnit.KILOGRAM.supportsDivision() == true (inherits default)")
    void testOperationSupportMethods_WeightUnitDivision() {
        assertTrue(WeightUnit.KILOGRAM.supportsDivision());
    }

    @Test
    @DisplayName("VolumeUnit.LITRE.supportsArithmetic() == true (inherits default)")
    void testOperationSupport_VolumeUnit_ArithmeticTrue() {
        assertTrue(VolumeUnit.LITRE.supportsArithmetic());
    }

    
    @Test
    @DisplayName("Null unit throws IllegalArgumentException")
    void testTemperatureNullUnitValidation() {
        assertThrows(IllegalArgumentException.class,
                () -> new Quantity<>(100.0, (TemperatureUnit) null));
    }

    @Test
    @DisplayName("NaN value throws IllegalArgumentException")
    void testTemperatureNaN_ThrowsOnConstruction() {
        assertThrows(IllegalArgumentException.class,
                () -> new Quantity<>(Double.NaN, TemperatureUnit.CELSIUS));
    }

    @Test
    @DisplayName("Infinite value throws IllegalArgumentException")
    void testTemperatureInfinity_ThrowsOnConstruction() {
        assertThrows(IllegalArgumentException.class,
                () -> new Quantity<>(Double.POSITIVE_INFINITY, TemperatureUnit.CELSIUS));
    }

    @Test
    @DisplayName("Negative temperature -40°C is valid (constructor accepts negatives)")
    void testTemperatureNegativeValue_AcceptedByConstructor() {
        assertDoesNotThrow(() -> new Quantity<>(-40.0, TemperatureUnit.CELSIUS));
    }

    @Test
    @DisplayName("Absolute zero -273.15°C is accepted by constructor")
    void testTemperatureAbsoluteZero_CelsiusAccepted() {
        assertDoesNotThrow(() -> new Quantity<>(-273.15, TemperatureUnit.CELSIUS));
    }

    @Test
    @DisplayName("TemperatureUnit implements IMeasurable")
    void testTemperatureEnumImplementsIMeasurable() {
        assertTrue(TemperatureUnit.CELSIUS instanceof IMeasurable);
    }

    @Test
    @DisplayName("Non-temperature units unaffected — LengthUnit arithmetic still works")
    void testIMeasurableInterface_BackwardCompatibility_Length() {
        assertDoesNotThrow(() -> {
            new Quantity<>(1.0, LengthUnit.FEET).add(new Quantity<>(12.0, LengthUnit.INCHES));
            new Quantity<>(10.0, LengthUnit.FEET).subtract(new Quantity<>(6.0, LengthUnit.INCHES));
            new Quantity<>(10.0, LengthUnit.FEET).divide(new Quantity<>(2.0, LengthUnit.FEET));
        });
    }

    @Test
    @DisplayName("Non-temperature units unaffected — WeightUnit arithmetic still works")
    void testIMeasurableInterface_BackwardCompatibility_Weight() {
        assertDoesNotThrow(() -> {
            new Quantity<>(10.0, WeightUnit.KILOGRAM).add(new Quantity<>(1000.0, WeightUnit.GRAM));
            new Quantity<>(10.0, WeightUnit.KILOGRAM).divide(new Quantity<>(2.0, WeightUnit.KILOGRAM));
        });
    }

    @Test
    @DisplayName("validateOperationSupport() on LengthUnit is no-op (does not throw)")
    void testIMeasurableInterface_LengthValidateNoOp() {
        assertDoesNotThrow(() -> LengthUnit.FEET.validateOperationSupport("ADD"));
        assertDoesNotThrow(() -> LengthUnit.INCHES.validateOperationSupport("DIVIDE"));
    }

    @Test
    @DisplayName("getUnitName() returns correct names for all TemperatureUnit constants")
    void testTemperatureUnit_NameMethod() {
        assertEquals("Celsius",    TemperatureUnit.CELSIUS.getUnitName());
        assertEquals("Fahrenheit", TemperatureUnit.FAHRENHEIT.getUnitName());
        assertEquals("Kelvin",     TemperatureUnit.KELVIN.getUnitName());
    }

    @Test
    @DisplayName("getConversionFactor() returns 1.0 (interface compliance placeholder)")
    void testTemperatureUnit_ConversionFactor() {
        assertEquals(1.0, TemperatureUnit.CELSIUS.getConversionFactor(),    EPSILON);
        assertEquals(1.0, TemperatureUnit.FAHRENHEIT.getConversionFactor(), EPSILON);
        assertEquals(1.0, TemperatureUnit.KELVIN.getConversionFactor(),     EPSILON);
    }

   
    @Test
    @DisplayName("Quantity<TemperatureUnit> integrates seamlessly with generic demonstrateEquality()")
    void testTemperatureIntegrationWithGenericSystem() {
        Quantity<TemperatureUnit> a = new Quantity<>(100.0, TemperatureUnit.CELSIUS);
        Quantity<TemperatureUnit> b = new Quantity<>(212.0, TemperatureUnit.FAHRENHEIT);
        assertTrue(Quantity.demonstrateEquality(a, b));
    }

    @Test
    @DisplayName("toString() contains unit name and value for temperature")
    void testTemperatureToString_ContainsUnitAndValue() {
        String s = new Quantity<>(100.0, TemperatureUnit.CELSIUS).toString();
        assertTrue(s.contains("CELSIUS"));
        assertTrue(s.contains("100") || s.contains("100,"));
    }
}