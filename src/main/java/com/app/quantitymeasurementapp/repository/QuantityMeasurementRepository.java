package com.app.quantitymeasurementapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.app.quantitymeasurementapp.model.QuantityMeasurementEntity;

import java.util.List;

@Repository
public interface QuantityMeasurementRepository
        extends JpaRepository<QuantityMeasurementEntity, Long> {

    // ── User-scoped queries (all history endpoints use these) ────────────────

    List<QuantityMeasurementEntity> findByUserIdAndOperation(Long userId, String operation);

    List<QuantityMeasurementEntity> findByUserIdAndThisMeasurementType(Long userId, String measurementType);

    List<QuantityMeasurementEntity> findByUserIdAndIsErrorTrue(Long userId);

    long countByUserIdAndOperationAndIsErrorFalse(Long userId, String operation);

    // ── Legacy / admin queries (kept but not used by user-facing endpoints) ──

    List<QuantityMeasurementEntity> findByOperation(String operation);

    List<QuantityMeasurementEntity> findByThisMeasurementType(String measurementType);

    List<QuantityMeasurementEntity> findByIsErrorTrue();

    long countByOperationAndIsErrorFalse(String operation);

    @Query("SELECT COUNT(q) FROM QuantityMeasurementEntity q")
    long countAllMeasurements();

    // ── User-scoped queries ──────────────────────────────────────────────────
//    List<QuantityMeasurementEntity> findByUserIdAndOperation(Long userId, String operation);
//    List<QuantityMeasurementEntity> findByUserIdAndThisMeasurementType(Long userId, String measurementType);
//    List<QuantityMeasurementEntity> findByUserIdAndIsErrorTrue(Long userId);
//    long countByUserIdAndOperationAndIsErrorFalse(Long userId, String operation);
}
