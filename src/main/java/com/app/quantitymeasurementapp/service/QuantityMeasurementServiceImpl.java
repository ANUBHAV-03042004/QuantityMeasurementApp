package com.app.quantitymeasurementapp.service;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import com.app.quantitymeasurementapp.exception.QuantityMeasurementException;
import com.app.quantitymeasurementapp.messaging.MeasurementEvent;
import com.app.quantitymeasurementapp.messaging.RabbitMQProducer;
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
    private final RabbitMQProducer              rabbitMQProducer;

    @Autowired
    public QuantityMeasurementServiceImpl(QuantityMeasurementRepository repository,
                                          RabbitMQProducer rabbitMQProducer) {
        this.repository      = repository;
        this.rabbitMQProducer = rabbitMQProducer;
    }

    // ── COMPARE ────────────────────────────────────────────────────────────────

    @Override
    @Caching(evict = {
        @CacheEvict(value = "historyByOperation", key = "'COMPARE'"),
        @CacheEvict(value = "errorHistory",       allEntries = true)
    })
    public QuantityMeasurementDTO compare(QuantityDTO q1, QuantityDTO q2) {
        validateNotNull(q1, q2, "COMPARE");
        try {
            validateSameCategory(q1, q2, "COMPARE");
            boolean result = toQuantity(q1).equals(toQuantity(q2));
            log.debug("COMPARE {} {} => {}", q1.getValue(), q2.getValue(), result);

            QuantityMeasurementEntity entity = new QuantityMeasurementEntity(
                    q1.getValue(), q1.getUnit(), q1.getMeasurementType(),
                    q2.getValue(), q2.getUnit(), q2.getMeasurementType(),
                    result);
            repository.save(entity);

            publishMeasurementEvent("COMPARE", q1, q2, String.valueOf(result), false);
            return QuantityMeasurementDTO.fromEntity(entity);

        } catch (QuantityMeasurementException e) {
            saveError(q1, q2, "COMPARE", e.getMessage());
            publishMeasurementEvent("COMPARE", q1, q2, e.getMessage(), true);
            throw e;
        } catch (Exception e) {
            String msg = "COMPARE failed: " + e.getMessage();
            saveError(q1, q2, "COMPARE", msg);
            publishMeasurementEvent("COMPARE", q1, q2, msg, true);
            throw new QuantityMeasurementException(msg, e);
        }
    }

    // ── CONVERT ────────────────────────────────────────────────────────────────

    @Override
    @Caching(evict = {
        @CacheEvict(value = "historyByOperation", key = "'CONVERT'"),
        @CacheEvict(value = "errorHistory",       allEntries = true)
    })
    public QuantityMeasurementDTO convert(QuantityDTO source, QuantityDTO target) {
        if (source == null || target == null)
            throw new QuantityMeasurementException("Null input is not allowed for CONVERT");
        try {
            validateSameCategory(source, target, "CONVERT");
            double resultValue = convertValue(source.getValue(), source.getUnit(),
                                              source.getMeasurementType(), target.getUnit());
            log.debug("CONVERT {} {} => {} {}", source.getValue(), source.getUnit(),
                      resultValue, target.getUnit());

            QuantityMeasurementEntity entity = new QuantityMeasurementEntity(
                    source.getValue(), source.getUnit(), source.getMeasurementType(),
                    "CONVERT",
                    resultValue, target.getUnit(), target.getMeasurementType());
            repository.save(entity);

            publishMeasurementEvent("CONVERT", source, target, resultValue + " " + target.getUnit(), false);
            return QuantityMeasurementDTO.fromEntity(entity);

        } catch (QuantityMeasurementException e) {
            saveError(source, target, "CONVERT", e.getMessage());
            throw e;
        } catch (Exception e) {
            String msg = "CONVERT failed: " + e.getMessage();
            saveError(source, target, "CONVERT", msg);
            throw new QuantityMeasurementException(msg, e);
        }
    }

    // ── ADD ────────────────────────────────────────────────────────────────────

    @Override
    @Caching(evict = {
        @CacheEvict(value = "historyByOperation", key = "'ADD'"),
        @CacheEvict(value = "errorHistory",       allEntries = true)
    })
    public QuantityMeasurementDTO add(QuantityDTO q1, QuantityDTO q2) {
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
            repository.save(entity);

            publishMeasurementEvent("ADD", q1, q2, resultValue + " " + q1.getUnit(), false);
            return QuantityMeasurementDTO.fromEntity(entity);

        } catch (QuantityMeasurementException e) {
            saveError(q1, q2, "ADD", e.getMessage());
            throw e;
        } catch (Exception e) {
            String msg = "ADD failed: " + e.getMessage();
            saveError(q1, q2, "ADD", msg);
            throw new QuantityMeasurementException(msg, e);
        }
    }

    // ── SUBTRACT ───────────────────────────────────────────────────────────────

    @Override
    @Caching(evict = {
        @CacheEvict(value = "historyByOperation", key = "'SUBTRACT'"),
        @CacheEvict(value = "errorHistory",       allEntries = true)
    })
    public QuantityMeasurementDTO subtract(QuantityDTO q1, QuantityDTO q2) {
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
            repository.save(entity);

            publishMeasurementEvent("SUBTRACT", q1, q2, resultValue + " " + q1.getUnit(), false);
            return QuantityMeasurementDTO.fromEntity(entity);

        } catch (QuantityMeasurementException e) {
            saveError(q1, q2, "SUBTRACT", e.getMessage());
            throw e;
        } catch (Exception e) {
            String msg = "SUBTRACT failed: " + e.getMessage();
            saveError(q1, q2, "SUBTRACT", msg);
            throw new QuantityMeasurementException(msg, e);
        }
    }

    // ── DIVIDE ─────────────────────────────────────────────────────────────────

    @Override
    @Caching(evict = {
        @CacheEvict(value = "historyByOperation", key = "'DIVIDE'"),
        @CacheEvict(value = "errorHistory",       allEntries = true)
    })
    public QuantityMeasurementDTO divide(QuantityDTO q1, QuantityDTO q2) {
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
            repository.save(entity);

            publishMeasurementEvent("DIVIDE", q1, q2, String.valueOf(resultValue), false);
            return QuantityMeasurementDTO.fromEntity(entity);

        } catch (QuantityMeasurementException e) {
            saveError(q1, q2, "DIVIDE", e.getMessage());
            throw e;
        } catch (Exception e) {
            String msg = "DIVIDE failed: " + e.getMessage();
            saveError(q1, q2, "DIVIDE", msg);
            throw new QuantityMeasurementException(msg, e);
        }
    }

    // ── History (CACHED) ───────────────────────────────────────────────────────

    @Override
    @Cacheable(value = "historyByOperation", key = "#operation.toUpperCase()")
    public List<QuantityMeasurementDTO> getHistoryByOperation(String operation) {
        log.debug("Cache MISS for historyByOperation: {}", operation);
        return QuantityMeasurementDTO.fromEntityList(
                repository.findByOperation(operation.toUpperCase()));
    }

    @Override
    @Cacheable(value = "historyByType", key = "#measurementType")
    public List<QuantityMeasurementDTO> getHistoryByMeasurementType(String measurementType) {
        log.debug("Cache MISS for historyByType: {}", measurementType);
        return QuantityMeasurementDTO.fromEntityList(
                repository.findByThisMeasurementType(measurementType));
    }

    @Override
    public long getOperationCount(String operation) {
        return repository.countByOperationAndIsErrorFalse(operation.toUpperCase());
    }

    @Override
    @Cacheable(value = "errorHistory")
    public List<QuantityMeasurementDTO> getErrorHistory() {
        log.debug("Cache MISS for errorHistory");
        return QuantityMeasurementDTO.fromEntityList(repository.findByIsErrorTrue());
    }

    // ── Private helpers ────────────────────────────────────────────────────────

    @SuppressWarnings({"unchecked", "rawtypes"})
    private Quantity<?> toQuantity(QuantityDTO dto) {
        try {
            return switch (dto.getMeasurementType()) {
                case "LengthUnit"      -> new Quantity<>(dto.getValue(), LengthUnit.valueOf(dto.getUnit()));
                case "WeightUnit"      -> new Quantity<>(dto.getValue(), WeightUnit.valueOf(dto.getUnit()));
                case "VolumeUnit"      -> new Quantity<>(dto.getValue(), VolumeUnit.valueOf(dto.getUnit()));
                case "TemperatureUnit" -> new Quantity<>(dto.getValue(), TemperatureUnit.valueOf(dto.getUnit()));
                default -> throw new QuantityMeasurementException("Unknown measurementType: " + dto.getMeasurementType());
            };
        } catch (IllegalArgumentException e) {
            throw new QuantityMeasurementException("Unit must be valid for the specified measurement type", e);
        }
    }

    private double convertValue(double value, String fromUnit, String type, String toUnit) {
        try {
            return switch (type) {
                case "LengthUnit" -> new Quantity<>(value, LengthUnit.valueOf(fromUnit))
                                         .convertTo(LengthUnit.valueOf(toUnit)).getValue();
                case "WeightUnit" -> new Quantity<>(value, WeightUnit.valueOf(fromUnit))
                                         .convertTo(WeightUnit.valueOf(toUnit)).getValue();
                case "VolumeUnit" -> new Quantity<>(value, VolumeUnit.valueOf(fromUnit))
                                         .convertTo(VolumeUnit.valueOf(toUnit)).getValue();
                case "TemperatureUnit" -> new Quantity<>(value, TemperatureUnit.valueOf(fromUnit))
                                              .convertTo(TemperatureUnit.valueOf(toUnit)).getValue();
                default -> throw new QuantityMeasurementException("Unknown type: " + type);
            };
        } catch (IllegalArgumentException e) {
            throw new QuantityMeasurementException("Unit must be valid for the specified measurement type", e);
        }
    }

    @SuppressWarnings("unchecked")
    private double arithmetic(QuantityDTO q1, QuantityDTO q2, String op) {
        try {
            return switch (q1.getMeasurementType()) {
                case "LengthUnit" -> applyOp(
                        new Quantity<>(q1.getValue(), LengthUnit.valueOf(q1.getUnit())),
                        new Quantity<>(q2.getValue(), LengthUnit.valueOf(q2.getUnit())), op);
                case "WeightUnit" -> applyOp(
                        new Quantity<>(q1.getValue(), WeightUnit.valueOf(q1.getUnit())),
                        new Quantity<>(q2.getValue(), WeightUnit.valueOf(q2.getUnit())), op);
                case "VolumeUnit" -> applyOp(
                        new Quantity<>(q1.getValue(), VolumeUnit.valueOf(q1.getUnit())),
                        new Quantity<>(q2.getValue(), VolumeUnit.valueOf(q2.getUnit())), op);
                default -> throw new QuantityMeasurementException("Unsupported type for " + op + ": " + q1.getMeasurementType());
            };
        } catch (IllegalArgumentException e) {
            throw new QuantityMeasurementException("Unknown unit: " + e.getMessage(), e);
        }
    }

    private <U extends Enum<U> & IMeasurable> double applyOp(Quantity<U> a, Quantity<U> b, String op) {
        return switch (op) {
            case "ADD"      -> a.add(b).getValue();
            case "SUBTRACT" -> a.subtract(b).getValue();
            case "DIVIDE"   -> a.divide(b);
            default -> throw new QuantityMeasurementException("Unknown operation: " + op);
        };
    }

    private void validateNotNull(QuantityDTO q1, QuantityDTO q2, String op) {
        if (q1 == null || q2 == null)
            throw new QuantityMeasurementException("Null operand is not allowed for " + op);
    }

    private void validateSameCategory(QuantityDTO q1, QuantityDTO q2, String op) {
        String t1 = q1.getMeasurementType();
        String t2 = q2.getMeasurementType();
        if (!t1.equals(t2))
            throw new QuantityMeasurementException(
                    op + " Error: Cannot perform arithmetic between different measurement categories: "
                    + t1 + " and " + t2);
    }

    private void validateArithmeticSupported(QuantityDTO q, String op) {
        if ("TemperatureUnit".equals(q.getMeasurementType()))
            throw new QuantityMeasurementException(
                    "Temperature does not support " + op
                    + " because temperature values are absolute points on a scale, not additive quantities.");
    }

    private void publishMeasurementEvent(String op, QuantityDTO q1, QuantityDTO q2,
                                          String result, boolean isError) {
        MeasurementEvent event = MeasurementEvent.builder()
                .operation(op)
                .measurementType(q1 != null ? q1.getMeasurementType() : "UNKNOWN")
                .thisValue(q1 != null ? q1.getValue() : 0)
                .thisUnit(q1 != null ? q1.getUnit() : "UNKNOWN")
                .thatValue(q2 != null ? q2.getValue() : 0)
                .thatUnit(q2 != null ? q2.getUnit() : "UNKNOWN")
                .result(result)
                .isError(isError)
                .timestamp(LocalDateTime.now())
                .build();
        rabbitMQProducer.publishMeasurementEvent(event);
    }

    private void saveError(QuantityDTO q1, QuantityDTO q2, String op, String errorMessage) {
        try {
            QuantityMeasurementEntity err = new QuantityMeasurementEntity(
                    q1 != null ? q1.getValue()           : 0.0,
                    q1 != null ? q1.getUnit()            : "UNKNOWN",
                    q1 != null ? q1.getMeasurementType() : "UNKNOWN",
                    q2 != null ? q2.getValue()           : null,
                    q2 != null ? q2.getUnit()            : null,
                    q2 != null ? q2.getMeasurementType() : null,
                    op, errorMessage);
            repository.save(err);
        } catch (Exception ignored) {
            log.warn("Could not persist error record for operation {}", op);
        }
    }
}
