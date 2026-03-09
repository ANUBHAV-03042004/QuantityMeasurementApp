package com.app.quantitymeasurementapp.controller;

import com.app.quantitymeasurementapp.entity.QuantityDTO;
import com.app.quantitymeasurementapp.exception.QuantityMeasurementException;
import com.app.quantitymeasurementapp.service.IQuantityMeasurementService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class QuantityMeasurementControllerTest {

    private IQuantityMeasurementService mockService;
    private QuantityMeasurementController controller;

    @Before
    public void setUp() {
        mockService  = Mockito.mock(IQuantityMeasurementService.class);
        controller   = new QuantityMeasurementController(mockService);
    }

    @Test
    public void testPerformCompare_DelegatesToService() {
        QuantityDTO q1 = new QuantityDTO(1.0, QuantityDTO.LengthUnit.FEET);
        QuantityDTO q2 = new QuantityDTO(12.0, QuantityDTO.LengthUnit.INCHES);
        when(mockService.compare(q1, q2)).thenReturn(true);
        boolean result = controller.performCompare(q1, q2);
        assertTrue(result);
        verify(mockService, times(1)).compare(q1, q2);
    }

    @Test
    public void testPerformCompare_ServiceThrowsException_ReturnsFalse() {
        QuantityDTO q1 = new QuantityDTO(1.0, QuantityDTO.LengthUnit.FEET);
        QuantityDTO q2 = new QuantityDTO(1.0, QuantityDTO.WeightUnit.KILOGRAM);
        when(mockService.compare(q1, q2)).thenThrow(new QuantityMeasurementException("Category mismatch"));
        boolean result = controller.performCompare(q1, q2);
        assertFalse(result);
    }

    @Test
    public void testPerformConvert_DelegatesToService() {
        QuantityDTO source = new QuantityDTO(1.0, QuantityDTO.LengthUnit.FEET);
        QuantityDTO target = new QuantityDTO(0.0, QuantityDTO.LengthUnit.INCHES);
        QuantityDTO expected = new QuantityDTO(12.0, QuantityDTO.LengthUnit.INCHES);
        when(mockService.convert(source, target)).thenReturn(expected);
        QuantityDTO result = controller.performConvert(source, target);
        assertEquals(12.0, result.getValue(), 1e-9);
        verify(mockService, times(1)).convert(source, target);
    }

    @Test
    public void testPerformConvert_ServiceThrowsException_ReturnsNaN() {
        QuantityDTO source = new QuantityDTO(1.0, QuantityDTO.LengthUnit.FEET);
        QuantityDTO target = new QuantityDTO(0.0, QuantityDTO.WeightUnit.KILOGRAM);
        when(mockService.convert(any(), any())).thenThrow(new QuantityMeasurementException("Category mismatch"));
        QuantityDTO result = controller.performConvert(source, target);
        assertTrue(Double.isNaN(result.getValue()));
    }

    @Test
    public void testPerformAdd_DelegatesToService() {
        QuantityDTO q1 = new QuantityDTO(1.0, QuantityDTO.LengthUnit.FEET);
        QuantityDTO q2 = new QuantityDTO(12.0, QuantityDTO.LengthUnit.INCHES);
        QuantityDTO expected = new QuantityDTO(2.0, QuantityDTO.LengthUnit.FEET);
        when(mockService.add(q1, q2)).thenReturn(expected);
        QuantityDTO result = controller.performAdd(q1, q2);
        assertEquals(2.0, result.getValue(), 1e-9);
        verify(mockService, times(1)).add(q1, q2);
    }

    @Test
    public void testPerformSubtract_DelegatesToService() {
        QuantityDTO q1 = new QuantityDTO(10.0, QuantityDTO.LengthUnit.FEET);
        QuantityDTO q2 = new QuantityDTO(6.0, QuantityDTO.LengthUnit.INCHES);
        QuantityDTO expected = new QuantityDTO(9.5, QuantityDTO.LengthUnit.FEET);
        when(mockService.subtract(q1, q2)).thenReturn(expected);
        QuantityDTO result = controller.performSubtract(q1, q2);
        assertEquals(9.5, result.getValue(), 1e-9);
        verify(mockService, times(1)).subtract(q1, q2);
    }

    @Test
    public void testPerformDivide_DelegatesToService() {
        QuantityDTO q1 = new QuantityDTO(24.0, QuantityDTO.LengthUnit.INCHES);
        QuantityDTO q2 = new QuantityDTO(2.0, QuantityDTO.LengthUnit.FEET);
        QuantityDTO expected = new QuantityDTO(1.0, QuantityDTO.LengthUnit.INCHES);
        when(mockService.divide(q1, q2)).thenReturn(expected);
        QuantityDTO result = controller.performDivide(q1, q2);
        assertEquals(1.0, result.getValue(), 1e-9);
        verify(mockService, times(1)).divide(q1, q2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructor_NullService_ThrowsException() {
        new QuantityMeasurementController(null);
    }

    @Test
    public void testPerformAdd_TemperatureException_ReturnsNaN() {
        QuantityDTO q1 = new QuantityDTO(100.0, QuantityDTO.TemperatureUnit.CELSIUS);
        QuantityDTO q2 = new QuantityDTO(50.0,  QuantityDTO.TemperatureUnit.CELSIUS);
        when(mockService.add(q1, q2)).thenThrow(new QuantityMeasurementException("Temperature does not support ADD"));
        QuantityDTO result = controller.performAdd(q1, q2);
        assertTrue(Double.isNaN(result.getValue()));
    }
}
