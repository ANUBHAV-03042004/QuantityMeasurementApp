package com.quantitymeasurementapp.service;

import com.quantitymeasurementapp.model.QuantityDTO;

public interface IQuantityMeasurementService {

    boolean compare(QuantityDTO q1, QuantityDTO q2);

   
    QuantityDTO convert(QuantityDTO source, QuantityDTO targetUnitDTO);

   
    QuantityDTO add(QuantityDTO q1, QuantityDTO q2);

    
    QuantityDTO subtract(QuantityDTO q1, QuantityDTO q2);

    QuantityDTO divide(QuantityDTO q1, QuantityDTO q2);
}
