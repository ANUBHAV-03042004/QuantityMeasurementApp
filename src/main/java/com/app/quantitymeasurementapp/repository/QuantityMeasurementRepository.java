package com.app.quantitymeasurementapp.repository;

import com.app.quantitymeasurementapp.model.QuantityMeasurementEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface QuantityMeasurementRepository
        extends JpaRepository<QuantityMeasurementEntity, Long> {

    // ── Global queries ───────────────────────────────────────────────────────
    List<QuantityMeasurementEntity> findByOperation(String operation);
    List<QuantityMeasurementEntity> findByThisMeasurementType(String measurementType);
    List<QuantityMeasurementEntity> findByCreatedAtAfter(LocalDateTime date);
    List<QuantityMeasurementEntity> findByIsErrorTrue();
    long countByOperationAndIsErrorFalse(String operation);

    @Query("SELECT COUNT(q) FROM QuantityMeasurementEntity q")
    long countAllMeasurements();

    // ── User-scoped queries ──────────────────────────────────────────────────
    List<QuantityMeasurementEntity> findByUserIdAndOperation(Long userId, String operation);
    List<QuantityMeasurementEntity> findByUserIdAndThisMeasurementType(Long userId, String measurementType);
    List<QuantityMeasurementEntity> findByUserIdAndIsErrorTrue(Long userId);
    long countByUserIdAndOperationAndIsErrorFalse(Long userId, String operation);
}
