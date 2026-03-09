package com.app.quantitymeasurementapp.service;

import com.app.quantitymeasurementapp.entity.QuantityDTO;
import com.app.quantitymeasurementapp.entity.QuantityMeasurementEntity;
import com.app.quantitymeasurementapp.exception.QuantityMeasurementException;
import com.app.quantitymeasurementapp.repository.IQuantityMeasurementRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class QuantityMeasurementServiceTest {

    private IQuantityMeasurementRepository mockRepository;
    private QuantityMeasurementServiceImpl service;

    @Before
    public void setUp() {
        mockRepository = Mockito.mock(IQuantityMeasurementRepository.class);
        service = new QuantityMeasurementServiceImpl(mockRepository);
    }

    @Test
    public void testCompare_EqualLengths_ReturnsTrue() {
        QuantityDTO q1 = new QuantityDTO(1.0, QuantityDTO.LengthUnit.FEET);
        QuantityDTO q2 = new QuantityDTO(12.0, QuantityDTO.LengthUnit.INCHES);
        boolean result = service.compare(q1, q2);
        assertTrue(result);
        verify(mockRepository, times(1)).save(any(QuantityMeasurementEntity.class));
    }

    @Test
    public void testCompare_UnequalLengths_ReturnsFalse() {
        QuantityDTO q1 = new QuantityDTO(2.0, QuantityDTO.LengthUnit.FEET);
        QuantityDTO q2 = new QuantityDTO(2.0, QuantityDTO.LengthUnit.INCHES);
        boolean result = service.compare(q1, q2);
        assertFalse(result);
        verify(mockRepository, times(1)).save(any(QuantityMeasurementEntity.class));
    }

    @Test(expected = QuantityMeasurementException.class)
    public void testCompare_NullInput_ThrowsException() {
        service.compare(null, new QuantityDTO(1.0, QuantityDTO.LengthUnit.FEET));
    }

    @Test
    public void testConvert_FeetToInches() {
        QuantityDTO source = new QuantityDTO(1.0, QuantityDTO.LengthUnit.FEET);
        QuantityDTO target = new QuantityDTO(0.0, QuantityDTO.LengthUnit.INCHES);
        QuantityDTO result = service.convert(source, target);
        assertEquals(12.0, result.getValue(), 1e-9);
        verify(mockRepository, times(1)).save(any(QuantityMeasurementEntity.class));
    }

    @Test
    public void testConvert_CelsiusToFahrenheit() {
        QuantityDTO source = new QuantityDTO(100.0, QuantityDTO.TemperatureUnit.CELSIUS);
        QuantityDTO target = new QuantityDTO(0.0, QuantityDTO.TemperatureUnit.FAHRENHEIT);
        QuantityDTO result = service.convert(source, target);
        assertEquals(212.0, result.getValue(), 1e-9);
    }

    @Test
    public void testAdd_LengthValues() {
        QuantityDTO q1 = new QuantityDTO(1.0, QuantityDTO.LengthUnit.FEET);
        QuantityDTO q2 = new QuantityDTO(12.0, QuantityDTO.LengthUnit.INCHES);
        QuantityDTO result = service.add(q1, q2);
        assertEquals(2.0, result.getValue(), 1e-9);
        verify(mockRepository, times(1)).save(any(QuantityMeasurementEntity.class));
    }

    @Test
    public void testAdd_WeightValues() {
        QuantityDTO q1 = new QuantityDTO(10.0, QuantityDTO.WeightUnit.KILOGRAM);
        QuantityDTO q2 = new QuantityDTO(5000.0, QuantityDTO.WeightUnit.GRAM);
        QuantityDTO result = service.add(q1, q2);
        assertEquals(15.0, result.getValue(), 0.01);
    }

    @Test(expected = QuantityMeasurementException.class)
    public void testAdd_TemperatureNotSupported_ThrowsException() {
        QuantityDTO q1 = new QuantityDTO(100.0, QuantityDTO.TemperatureUnit.CELSIUS);
        QuantityDTO q2 = new QuantityDTO(50.0, QuantityDTO.TemperatureUnit.CELSIUS);
        service.add(q1, q2);
    }

    @Test(expected = QuantityMeasurementException.class)
    public void testAdd_DifferentCategories_ThrowsException() {
        QuantityDTO q1 = new QuantityDTO(1.0, QuantityDTO.LengthUnit.FEET);
        QuantityDTO q2 = new QuantityDTO(1.0, QuantityDTO.WeightUnit.KILOGRAM);
        service.add(q1, q2);
    }

    @Test
    public void testSubtract_LengthValues() {
        QuantityDTO q1 = new QuantityDTO(10.0, QuantityDTO.LengthUnit.FEET);
        QuantityDTO q2 = new QuantityDTO(6.0, QuantityDTO.LengthUnit.INCHES);
        QuantityDTO result = service.subtract(q1, q2);
        assertEquals(9.5, result.getValue(), 1e-9);
        verify(mockRepository, times(1)).save(any(QuantityMeasurementEntity.class));
    }

    @Test
    public void testDivide_LengthValues() {
        QuantityDTO q1 = new QuantityDTO(24.0, QuantityDTO.LengthUnit.INCHES);
        QuantityDTO q2 = new QuantityDTO(2.0, QuantityDTO.LengthUnit.FEET);
        QuantityDTO result = service.divide(q1, q2);
        assertEquals(1.0, result.getValue(), 1e-9);
        verify(mockRepository, times(1)).save(any(QuantityMeasurementEntity.class));
    }

    @Test(expected = QuantityMeasurementException.class)
    public void testDivide_TemperatureNotSupported_ThrowsException() {
        QuantityDTO q1 = new QuantityDTO(100.0, QuantityDTO.TemperatureUnit.CELSIUS);
        QuantityDTO q2 = new QuantityDTO(50.0, QuantityDTO.TemperatureUnit.CELSIUS);
        service.divide(q1, q2);
    }

    @Test
    public void testCompare_TemperatureEquality() {
        QuantityDTO q1 = new QuantityDTO(0.0, QuantityDTO.TemperatureUnit.CELSIUS);
        QuantityDTO q2 = new QuantityDTO(32.0, QuantityDTO.TemperatureUnit.FAHRENHEIT);
        assertTrue(service.compare(q1, q2));
    }

    @Test
    public void testRepositoryCalledOnException() {
        QuantityDTO q1 = new QuantityDTO(1.0, QuantityDTO.LengthUnit.FEET);
        QuantityDTO q2 = new QuantityDTO(1.0, QuantityDTO.WeightUnit.KILOGRAM);
        try {
            service.compare(q1, q2);
        } catch (QuantityMeasurementException e) {
            verify(mockRepository, times(1)).save(any(QuantityMeasurementEntity.class));
        }
    }
}
