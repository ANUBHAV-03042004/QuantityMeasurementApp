package com.quantitymeasurementapp.repository;


import com.quantitymeasurementapp.model.QuantityMeasurementEntity;

import java.util.List;

public interface IQuantityMeasurementRepository {

    void save(QuantityMeasurementEntity entity);

    List<QuantityMeasurementEntity> getAllMeasurements();

    void clear();
}
