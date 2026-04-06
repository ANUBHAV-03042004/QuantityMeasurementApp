package com.app.quantitymeasurementapp.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.quantitymeasurementapp.exception.QuantityMeasurementException;
import com.app.quantitymeasurementapp.model.QuantityDTO;
import com.app.quantitymeasurementapp.model.QuantityDTO.LengthUnit;
import com.app.quantitymeasurementapp.model.QuantityDTO.TemperatureUnit;
import com.app.quantitymeasurementapp.model.QuantityDTO.VolumeUnit;
import com.app.quantitymeasurementapp.model.QuantityDTO.WeightUnit;
import com.app.quantitymeasurementapp.model.QuantityMeasurementDTO;
import com.app.quantitymeasurementapp.model.QuantityMeasurementEntity;
import com.app.quantitymeasurementapp.repository.QuantityMeasurementRepository;
import com.app.quantitymeasurementapp.unit.IMeasurable;
import com.app.quantitymeasurementapp.unit.Quantity;

@Service
public class QuantityMeasurementServiceImpl implements IQuantityMeasurementService {

    private static final Logger log = LoggerFactory.getLogger(QuantityMeasurementServiceImpl.class);

    private final QuantityMeasurementRepository repository;

    @Autowired
    public QuantityMeasurementServiceImpl(QuantityMeasurementRepository repository) {
        this.repository = repository;
    }

    // ── COMPARE ──────────────────────────────────────────────────────────────

    @Override
    public QuantityMeasurementDTO compare(QuantityDTO q1, QuantityDTO q2, Long userId) {
        validateNotNull(q1, q2, "COMPARE");
        try {
            validateSameCategory(q1, q2, "COMPARE");
            boolean result = toQuantity(q1).equals(toQuantity(q2));
            QuantityMeasurementEntity entity = new QuantityMeasurementEntity(
                    q1.getValue(), q1.getUnit(), q1.getMeasurementType(),
                    q2.getValue(), q2.getUnit(), q2.getMeasurementType(),
                    result);
            entity.setUserId(userId);
            repository.save(entity);
            return QuantityMeasurementDTO.fromEntity(entity);
        } catch (QuantityMeasurementException e) {
            saveError(q1, q2, "COMPARE", e.getMessage(), userId);
            throw e;
        } catch (Exception e) {
            String msg = "COMPARE failed: " + e.getMessage();
            saveError(q1, q2, "COMPARE", msg, userId);
            throw new QuantityMeasurementException(msg, e);
        }
    }

    // ── CONVERT ──────────────────────────────────────────────────────────────

    @Override
    public QuantityMeasurementDTO convert(QuantityDTO source, QuantityDTO target, Long userId) {
        if (source == null || target == null)
            throw new QuantityMeasurementException("Null input is not allowed for CONVERT");
        try {
            validateSameCategory(source, target, "CONVERT");
            double resultValue = convertValue(source.getValue(), source.getUnit(),
                                              source.getMeasurementType(), target.getUnit());
            QuantityMeasurementEntity entity = new QuantityMeasurementEntity(
                    source.getValue(), source.getUnit(), source.getMeasurementType(),
                    "CONVERT",
                    resultValue, target.getUnit(), target.getMeasurementType());
            entity.setUserId(userId);
            repository.save(entity);
            return QuantityMeasurementDTO.fromEntity(entity);
        } catch (QuantityMeasurementException e) {
            saveError(source, target, "CONVERT", e.getMessage(), userId);
            throw e;
        } catch (Exception e) {
            String msg = "CONVERT failed: " + e.getMessage();
            saveError(source, target, "CONVERT", msg, userId);
            throw new QuantityMeasurementException(msg, e);
        }
    }

    // ── ADD ──────────────────────────────────────────────────────────────────

    @Override
    public QuantityMeasurementDTO add(QuantityDTO q1, QuantityDTO q2, Long userId) {
        validateNotNull(q1, q2, "ADD");
        try {
            validateSameCategory(q1, q2, "ADD");
            validateArithmeticSupported(q1, "ADD");
            double resultValue = arithmetic(q1, q2, "ADD");
            QuantityMeasurementEntity entity = new QuantityMeasurementEntity(
                    q1.getValue(), q1.getUnit(), q1.getMeasurementType(),
                    q2.getValue(), q2.getUnit(), q2.getMeasurementType(),
                    "ADD",
                    resultValue, q1.getUnit(), q1.getMeasurementType());
            entity.setUserId(userId);
            repository.save(entity);
            return QuantityMeasurementDTO.fromEntity(entity);
        } catch (QuantityMeasurementException e) {
            saveError(q1, q2, "ADD", e.getMessage(), userId);
            throw e;
        } catch (Exception e) {
            String msg = "ADD failed: " + e.getMessage();
            saveError(q1, q2, "ADD", msg, userId);
            throw new QuantityMeasurementException(msg, e);
        }
    }

    // ── SUBTRACT ─────────────────────────────────────────────────────────────

    @Override
    public QuantityMeasurementDTO subtract(QuantityDTO q1, QuantityDTO q2, Long userId) {
        validateNotNull(q1, q2, "SUBTRACT");
        try {
            validateSameCategory(q1, q2, "SUBTRACT");
            validateArithmeticSupported(q1, "SUBTRACT");
            double resultValue = arithmetic(q1, q2, "SUBTRACT");
            QuantityMeasurementEntity entity = new QuantityMeasurementEntity(
                    q1.getValue(), q1.getUnit(), q1.getMeasurementType(),
                    q2.getValue(), q2.getUnit(), q2.getMeasurementType(),
                    "SUBTRACT",
                    resultValue, q1.getUnit(), q1.getMeasurementType());
            entity.setUserId(userId);
            repository.save(entity);
            return QuantityMeasurementDTO.fromEntity(entity);
        } catch (QuantityMeasurementException e) {
            saveError(q1, q2, "SUBTRACT", e.getMessage(), userId);
            throw e;
        } catch (Exception e) {
            String msg = "SUBTRACT failed: " + e.getMessage();
            saveError(q1, q2, "SUBTRACT", msg, userId);
            throw new QuantityMeasurementException(msg, e);
        }
    }

    // ── DIVIDE ───────────────────────────────────────────────────────────────

    @Override
    public QuantityMeasurementDTO divide(QuantityDTO q1, QuantityDTO q2, Long userId) {
        validateNotNull(q1, q2, "DIVIDE");
        try {
            validateSameCategory(q1, q2, "DIVIDE");
            validateArithmeticSupported(q1, "DIVIDE");
            double resultValue = arithmetic(q1, q2, "DIVIDE");
            QuantityMeasurementEntity entity = new QuantityMeasurementEntity(
                    q1.getValue(), q1.getUnit(), q1.getMeasurementType(),
                    q2.getValue(), q2.getUnit(), q2.getMeasurementType(),
                    "DIVIDE",
                    resultValue, q1.getUnit(), q1.getMeasurementType());
            entity.setUserId(userId);
            repository.save(entity);
            return QuantityMeasurementDTO.fromEntity(entity);
        } catch (QuantityMeasurementException e) {
            saveError(q1, q2, "DIVIDE", e.getMessage(), userId);
            throw e;
        } catch (ArithmeticException e) {
            String msg = "DIVIDE failed: " + e.getMessage();
            saveError(q1, q2, "DIVIDE", msg, userId);
            throw new QuantityMeasurementException(msg, e);
        } catch (Exception e) {
            String msg = "DIVIDE failed: " + e.getMessage();
            saveError(q1, q2, "DIVIDE", msg, userId);
            throw new QuantityMeasurementException(msg, e);
        }
    }

    // ── History (user-scoped) ─────────────────────────────────────────────────

    @Override
    public List<QuantityMeasurementDTO> getHistoryByOperation(String operation, Long userId) {
        return QuantityMeasurementDTO.fromEntityList(
                repository.findByUserIdAndOperation(userId, operation.toUpperCase()));
    }

    @Override
    public List<QuantityMeasurementDTO> getHistoryByMeasurementType(String measurementType, Long userId) {
        return QuantityMeasurementDTO.fromEntityList(
                repository.findByUserIdAndThisMeasurementType(userId, measurementType));
    }

    @Override
    public long getOperationCount(String operation, Long userId) {
        return repository.countByUserIdAndOperationAndIsErrorFalse(userId, operation.toUpperCase());
    }

    @Override
    public List<QuantityMeasurementDTO> getErrorHistory(Long userId) {
        return QuantityMeasurementDTO.fromEntityList(repository.findByUserIdAndIsErrorTrue(userId));
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    @SuppressWarnings({"unchecked", "rawtypes"})
    private Quantity<?> toQuantity(QuantityDTO dto) {
        String type = dto.getMeasurementType();
        String unit = dto.getUnit();
        try {
            switch (type) {
                case "LengthUnit":      return new Quantity<>(dto.getValue(), LengthUnit.valueOf(unit));
                case "WeightUnit":      return new Quantity<>(dto.getValue(), WeightUnit.valueOf(unit));
                case "VolumeUnit":      return new Quantity<>(dto.getValue(), VolumeUnit.valueOf(unit));
                case "TemperatureUnit": return new Quantity<>(dto.getValue(), TemperatureUnit.valueOf(unit));
                default: throw new QuantityMeasurementException("Unknown measurementType: " + type);
            }
        } catch (IllegalArgumentException e) {
            throw new QuantityMeasurementException("Unit must be valid for the specified measurement type", e);
        }
    }

    private double convertValue(double value, String fromUnit, String type, String toUnit) {
        try {
            switch (type) {
                case "LengthUnit": {
                    Quantity<LengthUnit> q = new Quantity<>(value, LengthUnit.valueOf(fromUnit));
                    return q.convertTo(LengthUnit.valueOf(toUnit)).getValue();
                }
                case "WeightUnit": {
                    Quantity<WeightUnit> q = new Quantity<>(value, WeightUnit.valueOf(fromUnit));
                    return q.convertTo(WeightUnit.valueOf(toUnit)).getValue();
                }
                case "VolumeUnit": {
                    Quantity<VolumeUnit> q = new Quantity<>(value, VolumeUnit.valueOf(fromUnit));
                    return q.convertTo(VolumeUnit.valueOf(toUnit)).getValue();
                }
                case "TemperatureUnit": {
                    Quantity<TemperatureUnit> q = new Quantity<>(value, TemperatureUnit.valueOf(fromUnit));
                    return q.convertTo(TemperatureUnit.valueOf(toUnit)).getValue();
                }
                default: throw new QuantityMeasurementException("Unknown type: " + type);
            }
        } catch (IllegalArgumentException e) {
            throw new QuantityMeasurementException("Unit must be valid for the specified measurement type", e);
        }
    }

    @SuppressWarnings("unchecked")
    private double arithmetic(QuantityDTO q1, QuantityDTO q2, String op) {
        String type = q1.getMeasurementType();
        try {
            switch (type) {
                case "LengthUnit": {
                    Quantity<LengthUnit> a = new Quantity<>(q1.getValue(), LengthUnit.valueOf(q1.getUnit()));
                    Quantity<LengthUnit> b = new Quantity<>(q2.getValue(), LengthUnit.valueOf(q2.getUnit()));
                    return applyOp(a, b, op);
                }
                case "WeightUnit": {
                    Quantity<WeightUnit> a = new Quantity<>(q1.getValue(), WeightUnit.valueOf(q1.getUnit()));
                    Quantity<WeightUnit> b = new Quantity<>(q2.getValue(), WeightUnit.valueOf(q2.getUnit()));
                    return applyOp(a, b, op);
                }
                case "VolumeUnit": {
                    Quantity<VolumeUnit> a = new Quantity<>(q1.getValue(), VolumeUnit.valueOf(q1.getUnit()));
                    Quantity<VolumeUnit> b = new Quantity<>(q2.getValue(), VolumeUnit.valueOf(q2.getUnit()));
                    return applyOp(a, b, op);
                }
                default: throw new QuantityMeasurementException("Unsupported type for " + op + ": " + type);
            }
        } catch (IllegalArgumentException e) {
            throw new QuantityMeasurementException("Unknown unit: " + e.getMessage(), e);
        }
    }

    private <U extends Enum<U> & IMeasurable> double applyOp(Quantity<U> a, Quantity<U> b, String op) {
        switch (op) {
            case "ADD":      return a.add(b).getValue();
            case "SUBTRACT": return a.subtract(b).getValue();
            case "DIVIDE":   return a.divide(b);
            default: throw new QuantityMeasurementException("Unknown operation: " + op);
        }
    }

    private void validateNotNull(QuantityDTO q1, QuantityDTO q2, String op) {
        if (q1 == null || q2 == null)
            throw new QuantityMeasurementException("Null operand is not allowed for " + op);
    }

    private void validateSameCategory(QuantityDTO q1, QuantityDTO q2, String op) {
        if (!q1.getMeasurementType().equals(q2.getMeasurementType()))
            throw new QuantityMeasurementException(
                    op + " Error: Cannot perform arithmetic between different measurement categories: "
                    + q1.getMeasurementType() + " and " + q2.getMeasurementType());
    }

    private void validateArithmeticSupported(QuantityDTO q, String op) {
        if ("TemperatureUnit".equals(q.getMeasurementType()))
            throw new QuantityMeasurementException(
                    "Temperature does not support " + op
                    + " because temperature values are absolute points on a scale, not additive quantities.");
    }

    private void saveError(QuantityDTO q1, QuantityDTO q2, String op, String errorMessage, Long userId) {
        try {
            QuantityMeasurementEntity err = new QuantityMeasurementEntity(
                    q1 != null ? q1.getValue()           : 0.0,
                    q1 != null ? q1.getUnit()            : "UNKNOWN",
                    q1 != null ? q1.getMeasurementType() : "UNKNOWN",
                    q2 != null ? q2.getValue()           : null,
                    q2 != null ? q2.getUnit()            : null,
                    q2 != null ? q2.getMeasurementType() : null,
                    op, errorMessage);
            err.setUserId(userId);
            repository.save(err);
        } catch (Exception ignored) {
            log.warn("Could not persist error record for operation {}", op);
        }
    }
}
