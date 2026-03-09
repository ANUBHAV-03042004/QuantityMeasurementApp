package com.app.quantitymeasurementapp.repository;

import com.app.quantitymeasurementapp.entity.QuantityMeasurementEntity;

import java.util.List;

public interface IQuantityMeasurementRepository {

    void save(QuantityMeasurementEntity entity);

    List<QuantityMeasurementEntity> getAllMeasurements();

    List<QuantityMeasurementEntity> getMeasurementsByOperation(String operationType);

    List<QuantityMeasurementEntity> getMeasurementsByType(String measurementType);

    int getTotalCount();

    void deleteAll();

    void clear();

    default String getPoolStatistics() {
        return "Pool statistics not available for this repository implementation";
    }

    default void releaseResources() {
    }
}
