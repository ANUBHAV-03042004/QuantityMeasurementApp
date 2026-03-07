package com.quantitymeasurementapp;

import org.junit.jupiter.api.*;

import com.quantitymeasurementapp.application.QuantityMeasurementApp;
import com.quantitymeasurementapp.controller.QuantityMeasurementController;
import com.quantitymeasurementapp.model.QuantityDTO;
import com.quantitymeasurementapp.model.QuantityMeasurementEntity;
import com.quantitymeasurementapp.repository.IQuantityMeasurementRepository;
import com.quantitymeasurementapp.repository.QuantityMeasurementCacheRepository;
import com.quantitymeasurementapp.service.IQuantityMeasurementService;
import com.quantitymeasurementapp.service.QuantityMeasurementException;
import com.quantitymeasurementapp.service.QuantityMeasurementServiceImpl;

import static org.junit.jupiter.api.Assertions.*;


@TestMethodOrder(MethodOrderer.DisplayName.class)
class QuantityMeasurementAppUC15Test {

    
    private IQuantityMeasurementRepository repository;
    private IQuantityMeasurementService    service;
    private QuantityMeasurementController  controller;

    @BeforeEach
    void setUp() {
        // Each test gets a fresh in-memory cache (cleared singleton cache)
        repository = QuantityMeasurementCacheRepository.getInstance();
        repository.clear();
        service    = new QuantityMeasurementServiceImpl(repository);
        controller = new QuantityMeasurementController(service);
    }

   
    @Test
    @DisplayName("Entity: single-operand (conversion) construction")
    void testQuantityEntity_SingleOperandConstruction() {
        QuantityDTO op = new QuantityDTO(1.0, QuantityDTO.LengthUnit.FEET);
        QuantityDTO res = new QuantityDTO(12.0, QuantityDTO.LengthUnit.INCHES);
        QuantityMeasurementEntity entity = new QuantityMeasurementEntity("CONVERT", op, res);

        assertEquals("CONVERT", entity.getOperationType());
        assertNotNull(entity.getOperationId());
        assertEquals(op,  entity.getOperand1());
        assertNull(entity.getOperand2());
        assertEquals(res, entity.getResult());
        assertFalse(entity.hasError());
        assertNotNull(entity.getTimestamp());
    }

    @Test
    @DisplayName("Entity: binary-operand (addition) construction")
    void testQuantityEntity_BinaryOperandConstruction() {
        QuantityDTO op1 = new QuantityDTO(1.0,  QuantityDTO.LengthUnit.FEET);
        QuantityDTO op2 = new QuantityDTO(12.0, QuantityDTO.LengthUnit.INCHES);
        QuantityDTO res = new QuantityDTO(2.0,  QuantityDTO.LengthUnit.FEET);
        QuantityMeasurementEntity entity =
                new QuantityMeasurementEntity("ADD", op1, op2, res);

        assertEquals("ADD", entity.getOperationType());
        assertEquals(op1, entity.getOperand1());
        assertEquals(op2, entity.getOperand2());
        assertEquals(res, entity.getResult());
        assertFalse(entity.hasError());
    }

    @Test
    @DisplayName("Entity: error construction stores message")
    void testQuantityEntity_ErrorConstruction() {
        QuantityDTO op1 = new QuantityDTO(100.0, QuantityDTO.TemperatureUnit.CELSIUS);
        QuantityDTO op2 = new QuantityDTO(50.0,  QuantityDTO.TemperatureUnit.CELSIUS);
        QuantityMeasurementEntity entity =
                new QuantityMeasurementEntity("ADD", op1, op2, "Temperature does not support ADD");

        assertTrue(entity.hasError());
        assertNotNull(entity.getErrorMessage());
        assertFalse(entity.getErrorMessage().isBlank());
        assertNull(entity.getResult());
    }

    @Test
    @DisplayName("Entity: comparison construction stores boolean result")
    void testQuantityEntity_ComparisonConstruction() {
        QuantityDTO op1 = new QuantityDTO(1.0,  QuantityDTO.LengthUnit.FEET);
        QuantityDTO op2 = new QuantityDTO(12.0, QuantityDTO.LengthUnit.INCHES);
        QuantityMeasurementEntity entity =
                new QuantityMeasurementEntity("COMPARE", op1, op2, true);

        assertFalse(entity.hasError());
        assertTrue(entity.getComparisonResult());
    }

    @Test
    @DisplayName("Entity: toString does not throw, contains operation type")
    void testQuantityEntity_ToString_DoesNotThrow() {
        QuantityDTO op = new QuantityDTO(1.0, QuantityDTO.LengthUnit.FEET);
        QuantityDTO res = new QuantityDTO(12.0, QuantityDTO.LengthUnit.INCHES);
        QuantityMeasurementEntity entity =
                new QuantityMeasurementEntity("CONVERT", op, res);

        String s = assertDoesNotThrow(entity::toString);
        assertTrue(s.contains("CONVERT"));
    }

    @Test
    @DisplayName("QuantityDTO: stores value and unit correctly")
    void testQuantityDTO_StoresValueAndUnit() {
        QuantityDTO dto = new QuantityDTO(42.0, QuantityDTO.WeightUnit.KILOGRAM);
        assertEquals(42.0, dto.getValue());
        assertEquals("KILOGRAM", dto.getUnit().getUnitName());
        assertEquals("WEIGHT", dto.getUnit().getMeasurementType());
    }

    @Test
    @DisplayName("QuantityDTO: all inner enum categories are correct")
    void testQuantityDTO_AllCategories() {
        assertEquals("LENGTH",      QuantityDTO.LengthUnit.FEET.getMeasurementType());
        assertEquals("WEIGHT",      QuantityDTO.WeightUnit.KILOGRAM.getMeasurementType());
        assertEquals("VOLUME",      QuantityDTO.VolumeUnit.LITRE.getMeasurementType());
        assertEquals("TEMPERATURE", QuantityDTO.TemperatureUnit.CELSIUS.getMeasurementType());
    }

    @Test
    @DisplayName("Service compare: same unit, same value → true")
    void testService_CompareEquality_SameUnit_Success() {
        assertTrue(service.compare(
                new QuantityDTO(12.0, QuantityDTO.LengthUnit.INCHES),
                new QuantityDTO(12.0, QuantityDTO.LengthUnit.INCHES)));
    }

    @Test
    @DisplayName("Service compare: 1 FEET == 12 INCHES → true")
    void testService_CompareEquality_DifferentUnit_Success() {
        assertTrue(service.compare(
                new QuantityDTO(1.0,  QuantityDTO.LengthUnit.FEET),
                new QuantityDTO(12.0, QuantityDTO.LengthUnit.INCHES)));
    }

    @Test
    @DisplayName("Service compare: different values → false")
    void testService_CompareEquality_DifferentValues_False() {
        assertFalse(service.compare(
                new QuantityDTO(1.0, QuantityDTO.LengthUnit.FEET),
                new QuantityDTO(2.0, QuantityDTO.LengthUnit.FEET)));
    }

    @Test
    @DisplayName("Service compare: cross-category throws QuantityMeasurementException")
    void testService_CompareEquality_CrossCategory_Error() {
        assertThrows(QuantityMeasurementException.class, () -> service.compare(
                new QuantityDTO(1.0, QuantityDTO.LengthUnit.FEET),
                new QuantityDTO(1.0, QuantityDTO.WeightUnit.KILOGRAM)));
    }

    @Test
    @DisplayName("Service compare: null throws QuantityMeasurementException")
    void testService_CompareEquality_Null_Error() {
        assertThrows(QuantityMeasurementException.class,
                () -> service.compare(null, new QuantityDTO(1.0, QuantityDTO.LengthUnit.FEET)));
    }

    // Temperature equality via service
    @Test
    @DisplayName("Service compare: 0°C == 32°F → true")
    void testService_Compare_Temperature_CelsiusEqualsFahrenheit() {
        assertTrue(service.compare(
                new QuantityDTO(0.0,  QuantityDTO.TemperatureUnit.CELSIUS),
                new QuantityDTO(32.0, QuantityDTO.TemperatureUnit.FAHRENHEIT)));
    }

    @Test
    @DisplayName("Service compare: 100°C == 212°F → true")
    void testService_Compare_Temperature_100CelsiusEquals212Fahrenheit() {
        assertTrue(service.compare(
                new QuantityDTO(100.0, QuantityDTO.TemperatureUnit.CELSIUS),
                new QuantityDTO(212.0, QuantityDTO.TemperatureUnit.FAHRENHEIT)));
    }

  
    @Test
    @DisplayName("Service convert: 1 FEET → 12 INCHES")
    void testService_Convert_FeetToInches() {
        QuantityDTO result = service.convert(
                new QuantityDTO(1.0, QuantityDTO.LengthUnit.FEET),
                new QuantityDTO(0.0, QuantityDTO.LengthUnit.INCHES));
        assertEquals(12.0, result.getValue(), 1e-3);
        assertEquals("INCHES", result.getUnit().getUnitName());
    }

    @Test
    @DisplayName("Service convert: 100°C → 212°F")
    void testService_Convert_CelsiusToFahrenheit() {
        QuantityDTO result = service.convert(
                new QuantityDTO(100.0, QuantityDTO.TemperatureUnit.CELSIUS),
                new QuantityDTO(0.0,   QuantityDTO.TemperatureUnit.FAHRENHEIT));
        assertEquals(212.0, result.getValue(), 1e-3);
    }

    @Test
    @DisplayName("Service convert: 0°C → 273.15 K")
    void testService_Convert_CelsiusToKelvin() {
        QuantityDTO result = service.convert(
                new QuantityDTO(0.0, QuantityDTO.TemperatureUnit.CELSIUS),
                new QuantityDTO(0.0, QuantityDTO.TemperatureUnit.KELVIN));
        assertEquals(273.15, result.getValue(), 1e-3);
    }

    @Test
    @DisplayName("Service convert: cross-category throws")
    void testService_Convert_CrossCategory_Error() {
        assertThrows(QuantityMeasurementException.class, () -> service.convert(
                new QuantityDTO(1.0, QuantityDTO.LengthUnit.FEET),
                new QuantityDTO(0.0, QuantityDTO.WeightUnit.KILOGRAM)));
    }

    @Test
    @DisplayName("Service add: 1 FEET + 12 INCHES = 2 FEET")
    void testService_Add_FeetPlusInches() {
        QuantityDTO result = service.add(
                new QuantityDTO(1.0,  QuantityDTO.LengthUnit.FEET),
                new QuantityDTO(12.0, QuantityDTO.LengthUnit.INCHES));
        assertEquals(2.0, result.getValue(), 1e-3);
        assertEquals("FEET", result.getUnit().getUnitName());
    }

    @Test
    @DisplayName("Service add: 10 KG + 5000 G = 15 KG")
    void testService_Add_KgPlusGrams() {
        QuantityDTO result = service.add(
                new QuantityDTO(10.0,   QuantityDTO.WeightUnit.KILOGRAM),
                new QuantityDTO(5000.0, QuantityDTO.WeightUnit.GRAM));
        assertEquals(15.0, result.getValue(), 1e-2);
    }

    @Test
    @DisplayName("Service add: 1 LITRE + 500 ML = 1.5 LITRE")
    void testService_Add_LitrePlusMillilitre() {
        QuantityDTO result = service.add(
                new QuantityDTO(1.0,   QuantityDTO.VolumeUnit.LITRE),
                new QuantityDTO(500.0, QuantityDTO.VolumeUnit.MILLILITRE));
        assertEquals(1.5, result.getValue(), 1e-3);
    }

    @Test
    @DisplayName("Service add: temperature throws QuantityMeasurementException")
    void testService_Add_UnsupportedOperation_Temperature() {
        assertThrows(QuantityMeasurementException.class, () -> service.add(
                new QuantityDTO(100.0, QuantityDTO.TemperatureUnit.CELSIUS),
                new QuantityDTO(50.0,  QuantityDTO.TemperatureUnit.CELSIUS)));
    }

    @Test
    @DisplayName("Service add: cross-category throws QuantityMeasurementException")
    void testService_Add_CrossCategory_Error() {
        assertThrows(QuantityMeasurementException.class, () -> service.add(
                new QuantityDTO(1.0, QuantityDTO.LengthUnit.FEET),
                new QuantityDTO(1.0, QuantityDTO.WeightUnit.KILOGRAM)));
    }

    @Test
    @DisplayName("Service subtract: 10 FEET - 6 INCHES = 9.5 FEET")
    void testService_Subtract_FeetMinusInches() {
        QuantityDTO result = service.subtract(
                new QuantityDTO(10.0, QuantityDTO.LengthUnit.FEET),
                new QuantityDTO(6.0,  QuantityDTO.LengthUnit.INCHES));
        assertEquals(9.5, result.getValue(), 1e-3);
    }

    @Test
    @DisplayName("Service subtract: temperature throws QuantityMeasurementException")
    void testService_Subtract_UnsupportedOperation_Temperature() {
        assertThrows(QuantityMeasurementException.class, () -> service.subtract(
                new QuantityDTO(100.0, QuantityDTO.TemperatureUnit.CELSIUS),
                new QuantityDTO(50.0,  QuantityDTO.TemperatureUnit.CELSIUS)));
    }

    @Test
    @DisplayName("Service divide: 24 INCHES / 2 FEET = 1.0 (dimensionless)")
    void testService_Divide_InchesOverFeet() {
        QuantityDTO result = service.divide(
                new QuantityDTO(24.0, QuantityDTO.LengthUnit.INCHES),
                new QuantityDTO(2.0,  QuantityDTO.LengthUnit.FEET));
        assertEquals(1.0, result.getValue(), 1e-4);
    }

    @Test
    @DisplayName("Service divide: by zero throws QuantityMeasurementException")
    void testService_Divide_ByZero_Error() {
        assertThrows(QuantityMeasurementException.class, () -> service.divide(
                new QuantityDTO(10.0, QuantityDTO.LengthUnit.FEET),
                new QuantityDTO(0.0,  QuantityDTO.LengthUnit.FEET)));
    }

    @Test
    @DisplayName("Service divide: temperature throws QuantityMeasurementException")
    void testService_Divide_UnsupportedOperation_Temperature() {
        assertThrows(QuantityMeasurementException.class, () -> service.divide(
                new QuantityDTO(100.0, QuantityDTO.TemperatureUnit.CELSIUS),
                new QuantityDTO(50.0,  QuantityDTO.TemperatureUnit.CELSIUS)));
    }

    @Test
    @DisplayName("Repository: save and retrieve entity")
    void testRepository_SaveAndRetrieve() {
        QuantityDTO op  = new QuantityDTO(1.0, QuantityDTO.LengthUnit.FEET);
        QuantityDTO res = new QuantityDTO(12.0, QuantityDTO.LengthUnit.INCHES);
        QuantityMeasurementEntity entity =
                new QuantityMeasurementEntity("CONVERT", op, res);
        repository.save(entity);

        assertEquals(1, repository.getAllMeasurements().size());
        assertEquals(entity, repository.getAllMeasurements().get(0));
    }

    @Test
    @DisplayName("Repository: service saves entity on successful operation")
    void testRepository_ServiceSavesEntityOnSuccess() {
        service.add(
                new QuantityDTO(1.0,  QuantityDTO.LengthUnit.FEET),
                new QuantityDTO(12.0, QuantityDTO.LengthUnit.INCHES));
        assertEquals(1, repository.getAllMeasurements().size());
    }

    @Test
    @DisplayName("Repository: service saves error entity on failed operation")
    void testRepository_ServiceSavesEntityOnError() {
        try {
            service.add(
                    new QuantityDTO(100.0, QuantityDTO.TemperatureUnit.CELSIUS),
                    new QuantityDTO(50.0,  QuantityDTO.TemperatureUnit.CELSIUS));
        } catch (QuantityMeasurementException ignored) {}

        assertEquals(1, repository.getAllMeasurements().size());
        assertTrue(repository.getAllMeasurements().get(0).hasError());
    }

    @Test
    @DisplayName("Repository: clear removes all records")
    void testRepository_Clear() {
        service.compare(
                new QuantityDTO(1.0, QuantityDTO.LengthUnit.FEET),
                new QuantityDTO(1.0, QuantityDTO.LengthUnit.FEET));
        repository.clear();
        assertEquals(0, repository.getAllMeasurements().size());
    }

 
    @Test
    @DisplayName("Controller performCompare: returns correct boolean")
    void testController_PerformCompare_Success() {
        assertTrue(controller.performCompare(
                new QuantityDTO(1.0,  QuantityDTO.LengthUnit.FEET),
                new QuantityDTO(12.0, QuantityDTO.LengthUnit.INCHES)));
        assertFalse(controller.performCompare(
                new QuantityDTO(2.0, QuantityDTO.LengthUnit.FEET),
                new QuantityDTO(2.0, QuantityDTO.LengthUnit.INCHES)));
    }

    @Test
    @DisplayName("Controller performCompare: cross-category returns false (error handled)")
    void testController_PerformCompare_CrossCategory_NoThrow() {
        assertDoesNotThrow(() -> controller.performCompare(
                new QuantityDTO(1.0, QuantityDTO.LengthUnit.FEET),
                new QuantityDTO(1.0, QuantityDTO.WeightUnit.KILOGRAM)));
        assertFalse(controller.performCompare(
                new QuantityDTO(1.0, QuantityDTO.LengthUnit.FEET),
                new QuantityDTO(1.0, QuantityDTO.WeightUnit.KILOGRAM)));
    }

    @Test
    @DisplayName("Controller performConvert: correct value returned")
    void testController_PerformConvert_Success() {
        QuantityDTO result = controller.performConvert(
                new QuantityDTO(1.0, QuantityDTO.LengthUnit.FEET),
                new QuantityDTO(0.0, QuantityDTO.LengthUnit.INCHES));
        assertEquals(12.0, result.getValue(), 1e-3);
    }

    @Test
    @DisplayName("Controller performAdd: correct sum returned")
    void testController_PerformAdd_Success() {
        QuantityDTO result = controller.performAdd(
                new QuantityDTO(1.0,  QuantityDTO.LengthUnit.FEET),
                new QuantityDTO(12.0, QuantityDTO.LengthUnit.INCHES));
        assertEquals(2.0, result.getValue(), 1e-3);
    }

    @Test
    @DisplayName("Controller performAdd: unsupported temperature returns NaN (error handled)")
    void testController_PerformAdd_UnsupportedTemperature_NoThrow() {
        QuantityDTO result = assertDoesNotThrow(() -> controller.performAdd(
                new QuantityDTO(100.0, QuantityDTO.TemperatureUnit.CELSIUS),
                new QuantityDTO(50.0,  QuantityDTO.TemperatureUnit.CELSIUS)));
        assertTrue(Double.isNaN(result.getValue()));
    }

    @Test
    @DisplayName("Controller performSubtract: 10 FEET - 6 INCHES = 9.5 FEET")
    void testController_PerformSubtract_Success() {
        QuantityDTO result = controller.performSubtract(
                new QuantityDTO(10.0, QuantityDTO.LengthUnit.FEET),
                new QuantityDTO(6.0,  QuantityDTO.LengthUnit.INCHES));
        assertEquals(9.5, result.getValue(), 1e-3);
    }

    @Test
    @DisplayName("Controller performDivide: 24 INCHES / 2 FEET = 1.0")
    void testController_PerformDivide_Success() {
        QuantityDTO result = controller.performDivide(
                new QuantityDTO(24.0, QuantityDTO.LengthUnit.INCHES),
                new QuantityDTO(2.0,  QuantityDTO.LengthUnit.FEET));
        assertEquals(1.0, result.getValue(), 1e-4);
    }

    @Test
    @DisplayName("Controller performDivide: by-zero returns NaN (error handled)")
    void testController_PerformDivide_ByZero_NoThrow() {
        QuantityDTO result = assertDoesNotThrow(() -> controller.performDivide(
                new QuantityDTO(10.0, QuantityDTO.LengthUnit.FEET),
                new QuantityDTO(0.0,  QuantityDTO.LengthUnit.FEET)));
        assertTrue(Double.isNaN(result.getValue()));
    }

    @Test
    @DisplayName("Service can be tested independently without controller")
    void testLayerSeparation_ServiceIndependence() {
        IQuantityMeasurementService standaloneService =
                new QuantityMeasurementServiceImpl(repository);
        assertTrue(standaloneService.compare(
                new QuantityDTO(1.0,  QuantityDTO.LengthUnit.FEET),
                new QuantityDTO(12.0, QuantityDTO.LengthUnit.INCHES)));
    }

    @Test
    @DisplayName("Controller null service throws at construction")
    void testController_NullService_ThrowsAtConstruction() {
        assertThrows(IllegalArgumentException.class,
                () -> new QuantityMeasurementController(null));
    }

    @Test
    @DisplayName("Service null repository throws at construction")
    void testService_NullRepository_ThrowsAtConstruction() {
        assertThrows(IllegalArgumentException.class,
                () -> new QuantityMeasurementServiceImpl(null));
    }

    @Test
    @DisplayName("Factory createRepository returns non-null Singleton")
    void testFactory_CreateRepository() {
        IQuantityMeasurementRepository r1 = QuantityMeasurementApp.createRepository();
        IQuantityMeasurementRepository r2 = QuantityMeasurementApp.createRepository();
        assertNotNull(r1);
        assertSame(r1, r2); // Singleton
    }

    @Test
    @DisplayName("Factory createService returns non-null implementation")
    void testFactory_CreateService() {
        IQuantityMeasurementService s =
                QuantityMeasurementApp.createService(repository);
        assertNotNull(s);
        assertInstanceOf(QuantityMeasurementServiceImpl.class, s);
    }

    @Test
    @DisplayName("Factory createController returns non-null controller")
    void testFactory_CreateController() {
        QuantityMeasurementController c =
                QuantityMeasurementApp.createController(service);
        assertNotNull(c);
    }

   
    @Test
    @DisplayName("BC: 1 YARD == 3 FEET")
    void testBackwardCompatibility_YardEqualsFeet() {
        assertTrue(service.compare(
                new QuantityDTO(1.0, QuantityDTO.LengthUnit.YARDS),
                new QuantityDTO(3.0, QuantityDTO.LengthUnit.FEET)));
    }

    @Test
    @DisplayName("BC: 1 KG == 1000 G")
    void testBackwardCompatibility_KgEqualsGrams() {
        assertTrue(service.compare(
                new QuantityDTO(1.0,    QuantityDTO.WeightUnit.KILOGRAM),
                new QuantityDTO(1000.0, QuantityDTO.WeightUnit.GRAM)));
    }

    @Test
    @DisplayName("BC: 1 LITRE == 1000 ML")
    void testBackwardCompatibility_LitreEqualsMillilitre() {
        assertTrue(service.compare(
                new QuantityDTO(1.0,    QuantityDTO.VolumeUnit.LITRE),
                new QuantityDTO(1000.0, QuantityDTO.VolumeUnit.MILLILITRE)));
    }

    @Test
    @DisplayName("BC: -40°C == -40°F (intersection point)")
    void testBackwardCompatibility_Temperature_NegativeFortyEqual() {
        assertTrue(service.compare(
                new QuantityDTO(-40.0, QuantityDTO.TemperatureUnit.CELSIUS),
                new QuantityDTO(-40.0, QuantityDTO.TemperatureUnit.FAHRENHEIT)));
    }

    @Test
    @DisplayName("BC: temperature unsupported ops throw via service")
    void testBackwardCompatibility_TemperatureUnsupportedOps() {
        QuantityDTO c1 = new QuantityDTO(100.0, QuantityDTO.TemperatureUnit.CELSIUS);
        QuantityDTO c2 = new QuantityDTO(50.0,  QuantityDTO.TemperatureUnit.CELSIUS);
        assertThrows(QuantityMeasurementException.class, () -> service.add(c1, c2));
        assertThrows(QuantityMeasurementException.class, () -> service.subtract(c1, c2));
        assertThrows(QuantityMeasurementException.class, () -> service.divide(c1, c2));
    }

    @Test
    @DisplayName("QuantityMeasurementException: message and cause constructors")
    void testQuantityMeasurementException_Constructors() {
        QuantityMeasurementException e1 = new QuantityMeasurementException("test");
        assertEquals("test", e1.getMessage());

        RuntimeException cause = new RuntimeException("root");
        QuantityMeasurementException e2 = new QuantityMeasurementException("wrapped", cause);
        assertEquals("wrapped", e2.getMessage());
        assertEquals(cause, e2.getCause());
    }

    @Test
    @DisplayName("QuantityMeasurementException: is unchecked (RuntimeException)")
    void testQuantityMeasurementException_IsUnchecked() {
        assertInstanceOf(RuntimeException.class,
                new QuantityMeasurementException("msg"));
    }

  
    @Test
    @DisplayName("Integration: full flow — Length addition through all layers")
    void testIntegration_EndToEnd_LengthAddition() {
        // App layer creates stack
        IQuantityMeasurementRepository repo       = QuantityMeasurementApp.createRepository();
        IQuantityMeasurementService    svc        = QuantityMeasurementApp.createService(repo);
        QuantityMeasurementController  ctrl       = QuantityMeasurementApp.createController(svc);

        // Controller call (user interaction)
        QuantityDTO result = ctrl.performAdd(
                new QuantityDTO(1.0,  QuantityDTO.LengthUnit.FEET),
                new QuantityDTO(12.0, QuantityDTO.LengthUnit.INCHES));

        // Verify result
        assertEquals(2.0, result.getValue(), 1e-3);
        assertEquals("FEET", result.getUnit().getUnitName());

        // Verify entity stored in repo
        assertFalse(repo.getAllMeasurements().isEmpty());
        QuantityMeasurementEntity entity = repo.getAllMeasurements().get(0);
        assertEquals("ADD", entity.getOperationType());
        assertFalse(entity.hasError());
    }

    @Test
    @DisplayName("Integration: full flow — Temperature unsupported, error stored in repo")
    void testIntegration_EndToEnd_TemperatureUnsupported() {
        IQuantityMeasurementRepository repo  = QuantityMeasurementApp.createRepository();
        IQuantityMeasurementService    svc   = QuantityMeasurementApp.createService(repo);
        QuantityMeasurementController  ctrl  = QuantityMeasurementApp.createController(svc);

        ctrl.performAdd(
                new QuantityDTO(100.0, QuantityDTO.TemperatureUnit.CELSIUS),
                new QuantityDTO(50.0,  QuantityDTO.TemperatureUnit.CELSIUS));

        // Entity should be stored with error flag
        assertFalse(repo.getAllMeasurements().isEmpty());
        assertTrue(repo.getAllMeasurements().get(0).hasError());
    }
}
