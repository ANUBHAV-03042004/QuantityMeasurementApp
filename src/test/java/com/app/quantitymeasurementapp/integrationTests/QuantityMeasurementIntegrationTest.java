package com.app.quantitymeasurementapp.integrationTests;

import com.app.quantitymeasurementapp.controller.QuantityMeasurementController;
import com.app.quantitymeasurementapp.entity.QuantityDTO;
import com.app.quantitymeasurementapp.repository.IQuantityMeasurementRepository;
import com.app.quantitymeasurementapp.repository.QuantityMeasurementCacheRepository;
import com.app.quantitymeasurementapp.repository.QuantityMeasurementDatabaseRepository;
import com.app.quantitymeasurementapp.service.IQuantityMeasurementService;
import com.app.quantitymeasurementapp.service.QuantityMeasurementServiceImpl;
import com.app.quantitymeasurementapp.util.ApplicationConfig;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

import com.app.quantitymeasurementapp.entity.QuantityMeasurementEntity;

public class QuantityMeasurementIntegrationTest {

    private QuantityMeasurementDatabaseRepository dbRepository;
    private IQuantityMeasurementService           service;
    private QuantityMeasurementController         controller;

    @Before
    public void setUp() {
        System.setProperty("db.url",           "jdbc:h2:mem:testdb_integration;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE");
        System.setProperty("db.username",      "sa");
        System.setProperty("db.password",      "");
        System.setProperty("db.driver",        "org.h2.Driver");
        System.setProperty("pool.initialSize", "2");
        System.setProperty("pool.maxSize",     "5");
        ApplicationConfig config = ApplicationConfig.getInstance();
        dbRepository = new QuantityMeasurementDatabaseRepository(config);
        dbRepository.deleteAll();
        service    = new QuantityMeasurementServiceImpl(dbRepository);
        controller = new QuantityMeasurementController(service);
    }

    @After
    public void tearDown() {
        dbRepository.deleteAll();
        dbRepository.releaseResources();
    }

    @Test
    public void testServiceWithDatabaseRepository_CompareOperation() {
        QuantityDTO q1 = new QuantityDTO(1.0, QuantityDTO.LengthUnit.FEET);
        QuantityDTO q2 = new QuantityDTO(12.0, QuantityDTO.LengthUnit.INCHES);
        boolean result = service.compare(q1, q2);
        assertTrue(result);
        assertEquals(1, dbRepository.getTotalCount());
    }

    @Test
    public void testServiceWithDatabaseRepository_ConvertOperation() {
        QuantityDTO source = new QuantityDTO(100.0, QuantityDTO.TemperatureUnit.CELSIUS);
        QuantityDTO target = new QuantityDTO(0.0,   QuantityDTO.TemperatureUnit.FAHRENHEIT);
        QuantityDTO result = service.convert(source, target);
        assertEquals(212.0, result.getValue(), 1e-9);
        assertEquals(1, dbRepository.getTotalCount());
    }

    @Test
    public void testServiceWithDatabaseRepository_AddOperation() {
        QuantityDTO q1 = new QuantityDTO(1.0,  QuantityDTO.LengthUnit.FEET);
        QuantityDTO q2 = new QuantityDTO(12.0, QuantityDTO.LengthUnit.INCHES);
        QuantityDTO result = service.add(q1, q2);
        assertEquals(2.0, result.getValue(), 1e-9);
        assertEquals(1, dbRepository.getTotalCount());
    }

    @Test
    public void testControllerEndToEnd_AllOperations() {
        controller.performCompare(new QuantityDTO(1.0, QuantityDTO.LengthUnit.FEET),   new QuantityDTO(12.0, QuantityDTO.LengthUnit.INCHES));
        controller.performConvert(new QuantityDTO(1.0, QuantityDTO.LengthUnit.FEET),   new QuantityDTO(0.0,  QuantityDTO.LengthUnit.INCHES));
        controller.performAdd(    new QuantityDTO(1.0, QuantityDTO.LengthUnit.FEET),   new QuantityDTO(12.0, QuantityDTO.LengthUnit.INCHES));
        controller.performSubtract(new QuantityDTO(10.0, QuantityDTO.LengthUnit.FEET), new QuantityDTO(6.0,  QuantityDTO.LengthUnit.INCHES));
        controller.performDivide(  new QuantityDTO(24.0, QuantityDTO.LengthUnit.INCHES),new QuantityDTO(2.0, QuantityDTO.LengthUnit.FEET));
        assertEquals(5, dbRepository.getTotalCount());
    }

    @Test
    public void testQueryByOperation_AfterMultipleOps() {
        controller.performCompare(new QuantityDTO(1.0, QuantityDTO.LengthUnit.FEET),    new QuantityDTO(12.0, QuantityDTO.LengthUnit.INCHES));
        controller.performCompare(new QuantityDTO(1.0, QuantityDTO.LengthUnit.YARDS),   new QuantityDTO(3.0,  QuantityDTO.LengthUnit.FEET));
        controller.performAdd(    new QuantityDTO(1.0, QuantityDTO.LengthUnit.FEET),    new QuantityDTO(12.0, QuantityDTO.LengthUnit.INCHES));
        List<QuantityMeasurementEntity> compareOps = dbRepository.getMeasurementsByOperation("COMPARE");
        assertEquals(2, compareOps.size());
    }

    @Test
    public void testQueryByMeasurementType_AfterMixedOps() {
        controller.performCompare(new QuantityDTO(1.0,  QuantityDTO.LengthUnit.FEET),    new QuantityDTO(12.0, QuantityDTO.LengthUnit.INCHES));
        controller.performCompare(new QuantityDTO(10.0, QuantityDTO.WeightUnit.KILOGRAM), new QuantityDTO(10.0, QuantityDTO.WeightUnit.KILOGRAM));
        controller.performAdd(    new QuantityDTO(1.0,  QuantityDTO.LengthUnit.FEET),    new QuantityDTO(12.0, QuantityDTO.LengthUnit.INCHES));
        List<QuantityMeasurementEntity> lengthOps = dbRepository.getMeasurementsByType("LENGTH");
        assertEquals(2, lengthOps.size());
    }

    @Test
    public void testRepositoryFactory_CreateDatabaseRepository() {
        assertNotNull(dbRepository);
        assertTrue(dbRepository instanceof QuantityMeasurementDatabaseRepository);
    }

    @Test
    public void testRepositoryFactory_CreateCacheRepository() {
        IQuantityMeasurementRepository cacheRepo = QuantityMeasurementCacheRepository.getInstance();
        assertNotNull(cacheRepo);
        assertTrue(cacheRepo instanceof QuantityMeasurementCacheRepository);
    }

    @Test
    public void testH2TestDatabase_IsolationBetweenTests() {
        assertEquals(0, dbRepository.getTotalCount());
        controller.performCompare(
            new QuantityDTO(1.0, QuantityDTO.LengthUnit.FEET),
            new QuantityDTO(12.0, QuantityDTO.LengthUnit.INCHES));
        assertEquals(1, dbRepository.getTotalCount());
    }

    @Test
    public void testDeleteAllMeasurements_AfterOperations() {
        controller.performCompare(new QuantityDTO(1.0, QuantityDTO.LengthUnit.FEET), new QuantityDTO(12.0, QuantityDTO.LengthUnit.INCHES));
        controller.performAdd(    new QuantityDTO(1.0, QuantityDTO.LengthUnit.FEET), new QuantityDTO(12.0, QuantityDTO.LengthUnit.INCHES));
        assertEquals(2, dbRepository.getTotalCount());
        dbRepository.deleteAll();
        assertEquals(0, dbRepository.getTotalCount());
    }

    @Test
    public void testPoolStatistics_Accessible() {
        String stats = dbRepository.getPoolStatistics();
        assertNotNull(stats);
        assertFalse(stats.isEmpty());
    }

    @Test
    public void testTemperatureConvert_CelsiusToKelvin() {
        QuantityDTO source = new QuantityDTO(0.0,  QuantityDTO.TemperatureUnit.CELSIUS);
        QuantityDTO target = new QuantityDTO(0.0,  QuantityDTO.TemperatureUnit.KELVIN);
        QuantityDTO result = service.convert(source, target);
        assertEquals(273.15, result.getValue(), 1e-9);
    }

    @Test
    public void testTemperatureCompare_KelvinToCelsius() {
        QuantityDTO q1 = new QuantityDTO(273.15, QuantityDTO.TemperatureUnit.KELVIN);
        QuantityDTO q2 = new QuantityDTO(0.0,    QuantityDTO.TemperatureUnit.CELSIUS);
        assertTrue(service.compare(q1, q2));
    }

    @Test
    public void testPropertiesConfiguration_EnvironmentOverride() {
        System.setProperty("repository.type", "cache");
        ApplicationConfig config = ApplicationConfig.getInstance();
        assertEquals("cache", config.getRepositoryType());
        System.setProperty("repository.type", "database");
    }
}
