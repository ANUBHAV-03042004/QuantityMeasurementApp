package com.app.quantitymeasurementapp.repository;

import com.app.quantitymeasurementapp.entity.QuantityDTO;
import com.app.quantitymeasurementapp.entity.QuantityMeasurementEntity;
import com.app.quantitymeasurementapp.exception.DatabaseException;
import com.app.quantitymeasurementapp.util.ApplicationConfig;
import com.app.quantitymeasurementapp.util.ConnectionPool;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class QuantityMeasurementDatabaseRepositoryTest {

    private QuantityMeasurementDatabaseRepository repository;

    @Before
    public void setUp() {
        System.setProperty("db.url",      "jdbc:h2:mem:testdb_repo;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE");
        System.setProperty("db.username", "sa");
        System.setProperty("db.password", "");
        System.setProperty("db.driver",   "org.h2.Driver");
        System.setProperty("pool.initialSize", "2");
        System.setProperty("pool.maxSize",     "5");
        ApplicationConfig config = ApplicationConfig.getInstance();
        repository = new QuantityMeasurementDatabaseRepository(config);
        repository.deleteAll();
    }

    @After
    public void tearDown() {
        repository.deleteAll();
        repository.releaseResources();
    }

    @Test
    public void testConnectionPool_Initialization() {
        String stats = repository.getPoolStatistics();
        assertNotNull(stats);
        assertTrue(stats.contains("ConnectionPool"));
    }

    @Test
    public void testDatabaseRepository_SaveEntity() {
        QuantityDTO q1 = new QuantityDTO(1.0, QuantityDTO.LengthUnit.FEET);
        QuantityDTO q2 = new QuantityDTO(12.0, QuantityDTO.LengthUnit.INCHES);
        QuantityMeasurementEntity entity = new QuantityMeasurementEntity("COMPARE", q1, q2, true);
        repository.save(entity);
        assertEquals(1, repository.getTotalCount());
    }

    @Test
    public void testDatabaseRepository_RetrieveAllMeasurements() {
        QuantityDTO q1 = new QuantityDTO(1.0, QuantityDTO.LengthUnit.FEET);
        QuantityDTO q2 = new QuantityDTO(12.0, QuantityDTO.LengthUnit.INCHES);
        repository.save(new QuantityMeasurementEntity("COMPARE",  q1, q2, true));
        repository.save(new QuantityMeasurementEntity("CONVERT",  q1, q2));
        repository.save(new QuantityMeasurementEntity("ADD",      q1, q2, new QuantityDTO(2.0, QuantityDTO.LengthUnit.FEET)));
        List<QuantityMeasurementEntity> all = repository.getAllMeasurements();
        assertEquals(3, all.size());
    }

    @Test
    public void testDatabaseRepository_QueryByOperation() {
        QuantityDTO q1 = new QuantityDTO(1.0, QuantityDTO.LengthUnit.FEET);
        QuantityDTO q2 = new QuantityDTO(12.0, QuantityDTO.LengthUnit.INCHES);
        repository.save(new QuantityMeasurementEntity("COMPARE", q1, q2, true));
        repository.save(new QuantityMeasurementEntity("COMPARE", q1, q2, false));
        repository.save(new QuantityMeasurementEntity("ADD",     q1, q2, new QuantityDTO(2.0, QuantityDTO.LengthUnit.FEET)));
        List<QuantityMeasurementEntity> compareOps = repository.getMeasurementsByOperation("COMPARE");
        assertEquals(2, compareOps.size());
    }

    @Test
    public void testDatabaseRepository_QueryByMeasurementType() {
        QuantityDTO lenQ1 = new QuantityDTO(1.0,  QuantityDTO.LengthUnit.FEET);
        QuantityDTO lenQ2 = new QuantityDTO(12.0, QuantityDTO.LengthUnit.INCHES);
        QuantityDTO wgtQ1 = new QuantityDTO(10.0, QuantityDTO.WeightUnit.KILOGRAM);
        QuantityDTO wgtQ2 = new QuantityDTO(5.0,  QuantityDTO.WeightUnit.KILOGRAM);
        repository.save(new QuantityMeasurementEntity("COMPARE", lenQ1, lenQ2, true));
        repository.save(new QuantityMeasurementEntity("COMPARE", wgtQ1, wgtQ2, false));
        List<QuantityMeasurementEntity> lengthOps = repository.getMeasurementsByType("LENGTH");
        assertEquals(1, lengthOps.size());
    }

    @Test
    public void testDatabaseRepository_CountMeasurements() {
        QuantityDTO q1 = new QuantityDTO(1.0, QuantityDTO.LengthUnit.FEET);
        QuantityDTO q2 = new QuantityDTO(12.0, QuantityDTO.LengthUnit.INCHES);
        assertEquals(0, repository.getTotalCount());
        repository.save(new QuantityMeasurementEntity("COMPARE", q1, q2, true));
        repository.save(new QuantityMeasurementEntity("COMPARE", q1, q2, false));
        assertEquals(2, repository.getTotalCount());
    }

    @Test
    public void testDatabaseRepository_DeleteAll() {
        QuantityDTO q1 = new QuantityDTO(1.0, QuantityDTO.LengthUnit.FEET);
        QuantityDTO q2 = new QuantityDTO(12.0, QuantityDTO.LengthUnit.INCHES);
        repository.save(new QuantityMeasurementEntity("COMPARE", q1, q2, true));
        repository.save(new QuantityMeasurementEntity("ADD",     q1, q2, new QuantityDTO(2.0, QuantityDTO.LengthUnit.FEET)));
        repository.deleteAll();
        assertEquals(0, repository.getTotalCount());
    }

    @Test
    public void testSQLInjectionPrevention() {
        QuantityDTO q1 = new QuantityDTO(1.0, QuantityDTO.LengthUnit.FEET);
        QuantityDTO q2 = new QuantityDTO(12.0, QuantityDTO.LengthUnit.INCHES);
        repository.save(new QuantityMeasurementEntity("COMPARE", q1, q2, true));
        List<QuantityMeasurementEntity> result = repository.getMeasurementsByOperation("COMPARE'; DROP TABLE quantity_measurement_entity; --");
        assertTrue(result.isEmpty());
        assertTrue(repository.getTotalCount() >= 1);
    }

    @Test
    public void testDatabaseSchema_TablesCreated() {
        assertEquals(0, repository.getTotalCount());
        QuantityDTO q1 = new QuantityDTO(1.0, QuantityDTO.LengthUnit.FEET);
        QuantityDTO q2 = new QuantityDTO(12.0, QuantityDTO.LengthUnit.INCHES);
        repository.save(new QuantityMeasurementEntity("COMPARE", q1, q2, true));
        assertEquals(1, repository.getTotalCount());
    }

    @Test
    public void testDatabaseRepository_PoolStatistics() {
        String stats = repository.getPoolStatistics();
        assertNotNull(stats);
        assertFalse(stats.isEmpty());
    }

    @Test
    public void testParameterizedQuery_DateTimeHandling() {
        QuantityDTO q1 = new QuantityDTO(100.0, QuantityDTO.TemperatureUnit.CELSIUS);
        QuantityDTO q2 = new QuantityDTO(212.0, QuantityDTO.TemperatureUnit.FAHRENHEIT);
        QuantityMeasurementEntity entity = new QuantityMeasurementEntity("COMPARE", q1, q2, true);
        repository.save(entity);
        List<QuantityMeasurementEntity> all = repository.getAllMeasurements();
        assertEquals(1, all.size());
        assertNotNull(all.get(0).getTimestamp());
    }

    @Test
    public void testBatchInsert_MultipleEntities() {
        QuantityDTO q1 = new QuantityDTO(1.0, QuantityDTO.LengthUnit.FEET);
        QuantityDTO q2 = new QuantityDTO(12.0, QuantityDTO.LengthUnit.INCHES);
        for (int i = 0; i < 20; i++) {
            repository.save(new QuantityMeasurementEntity("COMPARE", q1, q2, true));
        }
        assertEquals(20, repository.getTotalCount());
    }

    @Test
    public void testDatabaseException_CustomException() throws DatabaseException {
        try {
            ApplicationConfig badConfig = ApplicationConfig.getInstance();
            System.setProperty("db.url", "jdbc:h2:mem:nonexistent_FORCE_FAIL;DB_CLOSE_DELAY=-1");
            System.setProperty("pool.initialSize", "0");
            System.setProperty("pool.maxSize", "0");
            new QuantityMeasurementDatabaseRepository(badConfig);
        } catch (Exception e) {
            assertNull(e.getMessage());
        }
    }
}
