package com.app.quantitymeasurementapp.service;

import com.app.quantitymeasurementapp.model.QuantityDTO;
import com.app.quantitymeasurementapp.model.QuantityMeasurementDTO;

import java.util.List;

public interface IQuantityMeasurementService {

    QuantityMeasurementDTO compare(QuantityDTO q1, QuantityDTO q2, Long userId);

    QuantityMeasurementDTO convert(QuantityDTO source, QuantityDTO target, Long userId);

    QuantityMeasurementDTO add(QuantityDTO q1, QuantityDTO q2, Long userId);

    QuantityMeasurementDTO subtract(QuantityDTO q1, QuantityDTO q2, Long userId);

    QuantityMeasurementDTO divide(QuantityDTO q1, QuantityDTO q2, Long userId);

    // ── History queries (user-scoped) ────────────────────────────────────────

    List<QuantityMeasurementDTO> getHistoryByOperation(String operation, Long userId);

    List<QuantityMeasurementDTO> getHistoryByMeasurementType(String measurementType, Long userId);

    long getOperationCount(String operation, Long userId);

    List<QuantityMeasurementDTO> getErrorHistory(Long userId);
}
