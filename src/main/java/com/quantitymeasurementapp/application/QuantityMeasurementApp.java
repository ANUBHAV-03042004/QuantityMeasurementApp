package com.quantitymeasurementapp.application;

import com.quantitymeasurementapp.controller.QuantityMeasurementController;
import com.quantitymeasurementapp.model.QuantityDTO;
import com.quantitymeasurementapp.model.QuantityMeasurementEntity;
import com.quantitymeasurementapp.repository.IQuantityMeasurementRepository;
import com.quantitymeasurementapp.repository.QuantityMeasurementCacheRepository;
import com.quantitymeasurementapp.service.IQuantityMeasurementService;
import com.quantitymeasurementapp.service.QuantityMeasurementServiceImpl;

import java.util.List;


public class QuantityMeasurementApp {

    public static IQuantityMeasurementRepository createRepository() {
        return QuantityMeasurementCacheRepository.getInstance();
    }

    public static IQuantityMeasurementService createService(
            IQuantityMeasurementRepository repository) {
        return new QuantityMeasurementServiceImpl(repository);
    }

    public static QuantityMeasurementController createController(
            IQuantityMeasurementService service) {
        return new QuantityMeasurementController(service);
    }

    public static void main(String[] args) {

        IQuantityMeasurementRepository repository = createRepository();
        IQuantityMeasurementService    service    = createService(repository);
        QuantityMeasurementController  controller = createController(service);


      
        controller.performCompare(
                new QuantityDTO(1.0,  QuantityDTO.LengthUnit.FEET),
                new QuantityDTO(12.0, QuantityDTO.LengthUnit.INCHES));   // true

        controller.performCompare(
                new QuantityDTO(1.0, QuantityDTO.LengthUnit.YARDS),
                new QuantityDTO(3.0, QuantityDTO.LengthUnit.FEET));      // true

        controller.performCompare(
                new QuantityDTO(2.0, QuantityDTO.LengthUnit.FEET),
                new QuantityDTO(2.0, QuantityDTO.LengthUnit.INCHES));    // false

      
        controller.performConvert(
                new QuantityDTO(1.0,   QuantityDTO.LengthUnit.FEET),
                new QuantityDTO(0.0,   QuantityDTO.LengthUnit.INCHES));  // 12.0 in

        controller.performConvert(
                new QuantityDTO(100.0, QuantityDTO.TemperatureUnit.CELSIUS),
                new QuantityDTO(0.0,   QuantityDTO.TemperatureUnit.FAHRENHEIT)); // 212.0 °F

        controller.performConvert(
                new QuantityDTO(0.0,   QuantityDTO.TemperatureUnit.CELSIUS),
                new QuantityDTO(0.0,   QuantityDTO.TemperatureUnit.KELVIN));     // 273.15 K

   
        controller.performAdd(
                new QuantityDTO(1.0,    QuantityDTO.LengthUnit.FEET),
                new QuantityDTO(12.0,   QuantityDTO.LengthUnit.INCHES));  // 2.0 ft

        controller.performAdd(
                new QuantityDTO(10.0,   QuantityDTO.WeightUnit.KILOGRAM),
                new QuantityDTO(5000.0, QuantityDTO.WeightUnit.GRAM));    // 15 kg

        controller.performAdd(
                new QuantityDTO(1.0,    QuantityDTO.VolumeUnit.LITRE),
                new QuantityDTO(500.0,  QuantityDTO.VolumeUnit.MILLILITRE)); // 1.5 L

        
        controller.performSubtract(
                new QuantityDTO(10.0, QuantityDTO.LengthUnit.FEET),
                new QuantityDTO(6.0,  QuantityDTO.LengthUnit.INCHES));   // 9.5 ft

    
        controller.performDivide(
                new QuantityDTO(24.0, QuantityDTO.LengthUnit.INCHES),
                new QuantityDTO(2.0,  QuantityDTO.LengthUnit.FEET));     // 1.0

  
        controller.performCompare(
                new QuantityDTO(0.0,   QuantityDTO.TemperatureUnit.CELSIUS),
                new QuantityDTO(32.0,  QuantityDTO.TemperatureUnit.FAHRENHEIT)); // true

        controller.performCompare(
                new QuantityDTO(100.0, QuantityDTO.TemperatureUnit.CELSIUS),
                new QuantityDTO(212.0, QuantityDTO.TemperatureUnit.FAHRENHEIT)); // true

        controller.performCompare(
                new QuantityDTO(273.15, QuantityDTO.TemperatureUnit.KELVIN),
                new QuantityDTO(0.0,    QuantityDTO.TemperatureUnit.CELSIUS));   // true


        controller.performAdd(
                new QuantityDTO(100.0, QuantityDTO.TemperatureUnit.CELSIUS),
                new QuantityDTO(50.0,  QuantityDTO.TemperatureUnit.CELSIUS));


        controller.performAdd(
                new QuantityDTO(1.0, QuantityDTO.LengthUnit.FEET),
                new QuantityDTO(1.0, QuantityDTO.WeightUnit.KILOGRAM));

        List<QuantityMeasurementEntity> history = repository.getAllMeasurements();
        int start = Math.max(0, history.size() - 3);
        history.subList(start, history.size()).forEach(System.out::println);

        System.out.println("\nTotal operations stored: " + history.size());
    }
}
