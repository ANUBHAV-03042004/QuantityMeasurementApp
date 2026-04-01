package com.app.quantitymeasurementapp.service;

import com.app.quantitymeasurementapp.exception.QuantityMeasurementException;
import com.app.quantitymeasurementapp.model.QuantityDTO;
import com.app.quantitymeasurementapp.model.QuantityMeasurementDTO;
import com.app.quantitymeasurementapp.model.QuantityMeasurementEntity;
import com.app.quantitymeasurementapp.repository.QuantityMeasurementRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("QuantityMeasurementServiceImpl unit tests")
class QuantityMeasurementServiceTest {

    @Mock
    private QuantityMeasurementRepository repository;

    @InjectMocks
    private QuantityMeasurementServiceImpl service;

    @BeforeEach
    void setUpRepositoryStub() {
        lenient().when(repository.save(any(QuantityMeasurementEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private QuantityDTO length(double value, String unit) {
        return new QuantityDTO(value, unit, "LengthUnit");
    }

    private QuantityDTO weight(double value, String unit) {
        return new QuantityDTO(value, unit, "WeightUnit");
    }

    private QuantityDTO temperature(double value, String unit) {
        return new QuantityDTO(value, unit, "TemperatureUnit");
    }

    // ── COMPARE ──────────────────────────────────────────────────────────────

    @Test
    @DisplayName("COMPARE: 1 FEET equals 12 INCHES returns true")
    void compare_1Feet_12Inches_returnsTrue() {
        QuantityMeasurementDTO result = service.compare(length(1.0, "FEET"), length(12.0, "INCHES"));
        assertThat(result.getResultString()).isEqualTo("true");
        assertThat(result.isError()).isFalse();
        assertThat(result.getOperation()).isEqualTo("COMPARE");
        verify(repository).save(any());
    }

    @Test
    @DisplayName("COMPARE: 1 FEET does not equal 1 INCHES returns false")
    void compare_1Feet_1Inch_returnsFalse() {
        QuantityMeasurementDTO result = service.compare(length(1.0, "FEET"), length(1.0, "INCHES"));
        assertThat(result.getResultString()).isEqualTo("false");
        assertThat(result.isError()).isFalse();
    }

    @Test
    @DisplayName("COMPARE: 100 CELSIUS equals 212 FAHRENHEIT returns true")
    void compare_100Celsius_212Fahrenheit_returnsTrue() {
        QuantityMeasurementDTO result = service.compare(temperature(100.0, "CELSIUS"), temperature(212.0, "FAHRENHEIT"));
        assertThat(result.getResultString()).isEqualTo("true");
    }

    @Test
    @DisplayName("COMPARE: different measurement types throws exception")
    void compare_differentTypes_throwsException() {
        assertThatThrownBy(() -> service.compare(length(1.0, "FEET"), weight(1.0, "KILOGRAM")))
                .isInstanceOf(QuantityMeasurementException.class)
                .hasMessageContaining("Cannot perform arithmetic between different measurement categories");
    }

    @Test
    @DisplayName("COMPARE: null operand throws exception")
    void compare_nullInput_throwsException() {
        assertThatThrownBy(() -> service.compare(null, length(1.0, "FEET")))
                .isInstanceOf(QuantityMeasurementException.class);
    }

    @Test
    @DisplayName("COMPARE: invalid unit name throws exception")
    void compare_invalidUnit_throwsException() {
        assertThatThrownBy(() -> service.compare(length(1.0, "FOOT"), length(12.0, "INCHES")))
                .isInstanceOf(QuantityMeasurementException.class)
                .hasMessageContaining("Unit must be valid");
    }

    // ── CONVERT ──────────────────────────────────────────────────────────────

    @Test
    @DisplayName("CONVERT: 1 FEET converts to 12 INCHES")
    void convert_1Feet_toInches_returns12() {
        QuantityMeasurementDTO result = service.convert(length(1.0, "FEET"), length(0.0, "INCHES"));
        assertThat(result.getResultValue()).isEqualTo(12.0);
        assertThat(result.getResultUnit()).isEqualTo("INCHES");
        assertThat(result.getOperation()).isEqualTo("CONVERT");
    }

    @Test
    @DisplayName("CONVERT: 0 CELSIUS converts to 32 FAHRENHEIT")
    void convert_0Celsius_to_32Fahrenheit() {
        QuantityMeasurementDTO result = service.convert(temperature(0.0, "CELSIUS"), temperature(0.0, "FAHRENHEIT"));
        assertThat(result.getResultValue()).isCloseTo(32.0, within(1e-6));
    }

    @Test
    @DisplayName("CONVERT: 1 KILOGRAM converts to 1000 GRAM")
    void convert_1Kilogram_to_1000Gram() {
        QuantityMeasurementDTO result = service.convert(weight(1.0, "KILOGRAM"), weight(0.0, "GRAM"));
        assertThat(result.getResultValue()).isEqualTo(1000.0);
    }

    // ── ADD ──────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("ADD: 1 FEET + 12 INCHES = 2.0 FEET")
    void add_1Feet_12Inches_returns2Feet() {
        QuantityMeasurementDTO result = service.add(length(1.0, "FEET"), length(12.0, "INCHES"));
        assertThat(result.getResultValue()).isEqualTo(2.0);
        assertThat(result.getResultUnit()).isEqualTo("FEET");
        assertThat(result.getOperation()).isEqualTo("ADD");
        assertThat(result.isError()).isFalse();
    }

    @Test
    @DisplayName("ADD: 1 LITRE + 1000 MILLILITRE = 2.0 LITRE")
    void add_1Litre_1000Millilitre_returns2Litre() {
        QuantityDTO q1 = new QuantityDTO(1.0, "LITRE", "VolumeUnit");
        QuantityDTO q2 = new QuantityDTO(1000.0, "MILLILITRE", "VolumeUnit");
        QuantityMeasurementDTO result = service.add(q1, q2);
        assertThat(result.getResultValue()).isEqualTo(2.0);
    }

    @Test
    @DisplayName("ADD: temperature throws exception")
    void add_temperature_throwsException() {
        assertThatThrownBy(() -> service.add(temperature(100.0, "CELSIUS"), temperature(0.0, "CELSIUS")))
                .isInstanceOf(QuantityMeasurementException.class)
                .hasMessageContaining("Temperature does not support ADD");
    }

    // ── SUBTRACT ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("SUBTRACT: 24 INCHES - 1 FEET = 12.0 INCHES")
    void subtract_24Inches_1Foot_returns12Inches() {
        QuantityMeasurementDTO result = service.subtract(length(24.0, "INCHES"), length(1.0, "FEET"));
        assertThat(result.getResultValue()).isEqualTo(12.0);
        assertThat(result.getResultUnit()).isEqualTo("INCHES");
        assertThat(result.getOperation()).isEqualTo("SUBTRACT");
    }

    // ── DIVIDE ───────────────────────────────────────────────────────────────

    @Test
    @DisplayName("DIVIDE: 2 FEET / 1 FOOT = 2.0")
    void divide_2Feet_by_1Foot_returns2() {
        QuantityMeasurementDTO result = service.divide(length(2.0, "FEET"), length(1.0, "FEET"));
        assertThat(result.getResultValue()).isEqualTo(2.0);
        assertThat(result.getOperation()).isEqualTo("DIVIDE");
    }

    @Test
    @DisplayName("DIVIDE: divisor zero throws exception")
    void divide_byZero_throwsException() {
        assertThatThrownBy(() -> service.divide(length(1.0, "FEET"), length(0.0, "INCHES")))
                .isInstanceOf(QuantityMeasurementException.class)
                .hasMessageContaining("Divide by zero");
    }

    // ── History queries ───────────────────────────────────────────────────────

    @Test
    @DisplayName("getHistoryByOperation delegates to repository")
    void getHistoryByOperation_delegatesToRepository() {
        when(repository.findByOperation("COMPARE")).thenReturn(List.of());
        List<QuantityMeasurementDTO> result = service.getHistoryByOperation("compare");
        assertThat(result).isEmpty();
        verify(repository).findByOperation("COMPARE");
    }

    @Test
    @DisplayName("getHistoryByMeasurementType delegates to repository")
    void getHistoryByMeasurementType_delegatesToRepository() {
        when(repository.findByThisMeasurementType("LengthUnit")).thenReturn(List.of());
        List<QuantityMeasurementDTO> result = service.getHistoryByMeasurementType("LengthUnit");
        assertThat(result).isEmpty();
        verify(repository).findByThisMeasurementType("LengthUnit");
    }

    @Test
    @DisplayName("getOperationCount delegates to repository")
    void getOperationCount_delegatesToRepository() {
        when(repository.countByOperationAndIsErrorFalse("ADD")).thenReturn(3L);
        long count = service.getOperationCount("add");
        assertThat(count).isEqualTo(3L);
        verify(repository).countByOperationAndIsErrorFalse("ADD");
    }

    @Test
    @DisplayName("getErrorHistory delegates to repository")
    void getErrorHistory_delegatesToRepository() {
        when(repository.findByIsErrorTrue()).thenReturn(List.of());
        List<QuantityMeasurementDTO> result = service.getErrorHistory();
        assertThat(result).isEmpty();
        verify(repository).findByIsErrorTrue();
    }
}