package com.app.quantitymeasurementapp.service;

import com.app.quantitymeasurementapp.entity.QuantityDTO;
import com.app.quantitymeasurementapp.entity.QuantityMeasurementEntity;
import com.app.quantitymeasurementapp.entity.QuantityModel;
import com.app.quantitymeasurementapp.exception.QuantityMeasurementException;
import com.app.quantitymeasurementapp.repository.IQuantityMeasurementRepository;
import com.app.quantitymeasurementapp.unit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QuantityMeasurementServiceImpl implements IQuantityMeasurementService {

    private static final Logger logger = LoggerFactory.getLogger(QuantityMeasurementServiceImpl.class);

    private final IQuantityMeasurementRepository repository;

    public QuantityMeasurementServiceImpl(IQuantityMeasurementRepository repository) {
        if (repository == null) throw new IllegalArgumentException("Repository must not be null");
        this.repository = repository;
        logger.info("QuantityMeasurementServiceImpl initialized with {}", repository.getClass().getSimpleName());
    }

    @Override
    public boolean compare(QuantityDTO q1, QuantityDTO q2) {
        validateNotNull(q1, q2, "COMPARE");
        try {
            Quantity<?> qty1 = toQuantity(q1);
            Quantity<?> qty2 = toQuantity(q2);
            boolean result = qty1.equals(qty2);
            repository.save(new QuantityMeasurementEntity("COMPARE", q1, q2, result));
            logger.debug("COMPARE {} == {} => {}", q1, q2, result);
            return result;
        } catch (QuantityMeasurementException e) {
            repository.save(new QuantityMeasurementEntity("COMPARE", q1, q2, e.getMessage()));
            throw e;
        } catch (Exception e) {
            String msg = "COMPARE failed: " + e.getMessage();
            repository.save(new QuantityMeasurementEntity("COMPARE", q1, q2, msg));
            throw new QuantityMeasurementException(msg, e);
        }
    }

    @Override
    public QuantityDTO convert(QuantityDTO source, QuantityDTO targetUnitDTO) {
        if (source == null || targetUnitDTO == null)
            throw new QuantityMeasurementException("Null input is not allowed for CONVERT");
        try {
            QuantityDTO result = convertInternal(source, targetUnitDTO);
            repository.save(new QuantityMeasurementEntity("CONVERT", source, result));
            logger.debug("CONVERT {} => {}", source, result);
            return result;
        } catch (QuantityMeasurementException e) {
            repository.save(new QuantityMeasurementEntity("CONVERT", source, null, e.getMessage()));
            throw e;
        } catch (Exception e) {
            String msg = "CONVERT failed: " + e.getMessage();
            repository.save(new QuantityMeasurementEntity("CONVERT", source, null, msg));
            throw new QuantityMeasurementException(msg, e);
        }
    }

    @Override
    public QuantityDTO add(QuantityDTO q1, QuantityDTO q2) {
        validateNotNull(q1, q2, "ADD");
        try {
            QuantityDTO result = addInternal(q1, q2);
            repository.save(new QuantityMeasurementEntity("ADD", q1, q2, result));
            logger.debug("ADD {} + {} => {}", q1, q2, result);
            return result;
        } catch (QuantityMeasurementException e) {
            repository.save(new QuantityMeasurementEntity("ADD", q1, q2, e.getMessage()));
            throw e;
        } catch (UnsupportedOperationException e) {
            String msg = e.getMessage();
            repository.save(new QuantityMeasurementEntity("ADD", q1, q2, msg));
            throw new QuantityMeasurementException(msg, e);
        } catch (Exception e) {
            String msg = "ADD failed: " + e.getMessage();
            repository.save(new QuantityMeasurementEntity("ADD", q1, q2, msg));
            throw new QuantityMeasurementException(msg, e);
        }
    }

    @Override
    public QuantityDTO subtract(QuantityDTO q1, QuantityDTO q2) {
        validateNotNull(q1, q2, "SUBTRACT");
        try {
            QuantityDTO result = subtractInternal(q1, q2);
            repository.save(new QuantityMeasurementEntity("SUBTRACT", q1, q2, result));
            logger.debug("SUBTRACT {} - {} => {}", q1, q2, result);
            return result;
        } catch (QuantityMeasurementException e) {
            repository.save(new QuantityMeasurementEntity("SUBTRACT", q1, q2, e.getMessage()));
            throw e;
        } catch (UnsupportedOperationException e) {
            String msg = e.getMessage();
            repository.save(new QuantityMeasurementEntity("SUBTRACT", q1, q2, msg));
            throw new QuantityMeasurementException(msg, e);
        } catch (Exception e) {
            String msg = "SUBTRACT failed: " + e.getMessage();
            repository.save(new QuantityMeasurementEntity("SUBTRACT", q1, q2, msg));
            throw new QuantityMeasurementException(msg, e);
        }
    }

    @Override
    public QuantityDTO divide(QuantityDTO q1, QuantityDTO q2) {
        validateNotNull(q1, q2, "DIVIDE");
        try {
            QuantityDTO result = divideInternal(q1, q2);
            repository.save(new QuantityMeasurementEntity("DIVIDE", q1, q2, result));
            logger.debug("DIVIDE {} / {} => {}", q1, q2, result);
            return result;
        } catch (QuantityMeasurementException e) {
            repository.save(new QuantityMeasurementEntity("DIVIDE", q1, q2, e.getMessage()));
            throw e;
        } catch (UnsupportedOperationException e) {
            String msg = e.getMessage();
            repository.save(new QuantityMeasurementEntity("DIVIDE", q1, q2, msg));
            throw new QuantityMeasurementException(msg, e);
        } catch (ArithmeticException e) {
            String msg = "DIVIDE failed: " + e.getMessage();
            repository.save(new QuantityMeasurementEntity("DIVIDE", q1, q2, msg));
            throw new QuantityMeasurementException(msg, e);
        } catch (Exception e) {
            String msg = "DIVIDE failed: " + e.getMessage();
            repository.save(new QuantityMeasurementEntity("DIVIDE", q1, q2, msg));
            throw new QuantityMeasurementException(msg, e);
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private QuantityDTO convertInternal(QuantityDTO source, QuantityDTO target) {
        validateSameCategory(source, target, "CONVERT");
        String type = source.getUnit().getMeasurementType();
        switch (type) {
            case "LENGTH": {
                QuantityModel<LengthUnit> m = (QuantityModel<LengthUnit>) toModel(source);
                LengthUnit t = LengthUnit.valueOf(target.getUnit().getUnitName());
                Quantity<LengthUnit> r = m.toQuantity().convertTo(t);
                return new QuantityDTO(r.getValue(), dtoLengthUnit(r.getUnit()));
            }
            case "WEIGHT": {
                QuantityModel<WeightUnit> m = (QuantityModel<WeightUnit>) toModel(source);
                WeightUnit t = WeightUnit.valueOf(target.getUnit().getUnitName());
                Quantity<WeightUnit> r = m.toQuantity().convertTo(t);
                return new QuantityDTO(r.getValue(), dtoWeightUnit(r.getUnit()));
            }
            case "VOLUME": {
                QuantityModel<VolumeUnit> m = (QuantityModel<VolumeUnit>) toModel(source);
                VolumeUnit t = VolumeUnit.valueOf(target.getUnit().getUnitName());
                Quantity<VolumeUnit> r = m.toQuantity().convertTo(t);
                return new QuantityDTO(r.getValue(), dtoVolumeUnit(r.getUnit()));
            }
            case "TEMPERATURE": {
                QuantityModel<TemperatureUnit> m = (QuantityModel<TemperatureUnit>) toModel(source);
                TemperatureUnit t = TemperatureUnit.valueOf(target.getUnit().getUnitName());
                Quantity<TemperatureUnit> r = m.toQuantity().convertTo(t);
                return new QuantityDTO(r.getValue(), dtoTemperatureUnit(r.getUnit()));
            }
            default: throw new QuantityMeasurementException("Unknown measurement type: " + type);
        }
    }

    @SuppressWarnings("unchecked")
    private QuantityDTO addInternal(QuantityDTO q1, QuantityDTO q2) {
        validateSameCategory(q1, q2, "ADD");
        String type = q1.getUnit().getMeasurementType();
        switch (type) {
            case "LENGTH": {
                Quantity<LengthUnit> r = ((QuantityModel<LengthUnit>) toModel(q1)).toQuantity().add(((QuantityModel<LengthUnit>) toModel(q2)).toQuantity());
                return new QuantityDTO(r.getValue(), dtoLengthUnit(r.getUnit()));
            }
            case "WEIGHT": {
                Quantity<WeightUnit> r = ((QuantityModel<WeightUnit>) toModel(q1)).toQuantity().add(((QuantityModel<WeightUnit>) toModel(q2)).toQuantity());
                return new QuantityDTO(r.getValue(), dtoWeightUnit(r.getUnit()));
            }
            case "VOLUME": {
                Quantity<VolumeUnit> r = ((QuantityModel<VolumeUnit>) toModel(q1)).toQuantity().add(((QuantityModel<VolumeUnit>) toModel(q2)).toQuantity());
                return new QuantityDTO(r.getValue(), dtoVolumeUnit(r.getUnit()));
            }
            case "TEMPERATURE":
                throw new QuantityMeasurementException("Temperature does not support ADD because temperature values are absolute points on a scale not additive quantities.");
            default: throw new QuantityMeasurementException("Unknown type: " + type);
        }
    }

    @SuppressWarnings("unchecked")
    private QuantityDTO subtractInternal(QuantityDTO q1, QuantityDTO q2) {
        validateSameCategory(q1, q2, "SUBTRACT");
        String type = q1.getUnit().getMeasurementType();
        switch (type) {
            case "LENGTH": {
                Quantity<LengthUnit> r = ((QuantityModel<LengthUnit>) toModel(q1)).toQuantity().subtract(((QuantityModel<LengthUnit>) toModel(q2)).toQuantity());
                return new QuantityDTO(r.getValue(), dtoLengthUnit(r.getUnit()));
            }
            case "WEIGHT": {
                Quantity<WeightUnit> r = ((QuantityModel<WeightUnit>) toModel(q1)).toQuantity().subtract(((QuantityModel<WeightUnit>) toModel(q2)).toQuantity());
                return new QuantityDTO(r.getValue(), dtoWeightUnit(r.getUnit()));
            }
            case "VOLUME": {
                Quantity<VolumeUnit> r = ((QuantityModel<VolumeUnit>) toModel(q1)).toQuantity().subtract(((QuantityModel<VolumeUnit>) toModel(q2)).toQuantity());
                return new QuantityDTO(r.getValue(), dtoVolumeUnit(r.getUnit()));
            }
            case "TEMPERATURE":
                throw new QuantityMeasurementException("Temperature does not support SUBTRACT.");
            default: throw new QuantityMeasurementException("Unknown type: " + type);
        }
    }

    @SuppressWarnings("unchecked")
    private QuantityDTO divideInternal(QuantityDTO q1, QuantityDTO q2) {
        validateSameCategory(q1, q2, "DIVIDE");
        String type = q1.getUnit().getMeasurementType();
        double ratio;
        QuantityDTO.IMeasurableUnit resultUnit;
        switch (type) {
            case "LENGTH":
                ratio      = ((QuantityModel<LengthUnit>) toModel(q1)).toQuantity().divide(((QuantityModel<LengthUnit>) toModel(q2)).toQuantity());
                resultUnit = dtoLengthUnit(LengthUnit.valueOf(q1.getUnit().getUnitName())); break;
            case "WEIGHT":
                ratio      = ((QuantityModel<WeightUnit>) toModel(q1)).toQuantity().divide(((QuantityModel<WeightUnit>) toModel(q2)).toQuantity());
                resultUnit = dtoWeightUnit(WeightUnit.valueOf(q1.getUnit().getUnitName())); break;
            case "VOLUME":
                ratio      = ((QuantityModel<VolumeUnit>) toModel(q1)).toQuantity().divide(((QuantityModel<VolumeUnit>) toModel(q2)).toQuantity());
                resultUnit = dtoVolumeUnit(VolumeUnit.valueOf(q1.getUnit().getUnitName())); break;
            case "TEMPERATURE":
                throw new QuantityMeasurementException("Temperature does not support DIVIDE.");
            default:
                throw new QuantityMeasurementException("Unknown type: " + type);
        }
        return new QuantityDTO(ratio, resultUnit);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private Quantity<?> toQuantity(QuantityDTO dto) {
        return toModel(dto).toQuantity();
    }

    @SuppressWarnings("rawtypes")
    private QuantityModel toModel(QuantityDTO dto) {
        if (dto == null || dto.getUnit() == null)
            throw new QuantityMeasurementException("QuantityDTO and its unit must not be null");
        String type     = dto.getUnit().getMeasurementType();
        String unitName = dto.getUnit().getUnitName();
        try {
            switch (type) {
                case "LENGTH":      return new QuantityModel<>(dto.getValue(), LengthUnit.valueOf(unitName));
                case "WEIGHT":      return new QuantityModel<>(dto.getValue(), WeightUnit.valueOf(unitName));
                case "VOLUME":      return new QuantityModel<>(dto.getValue(), VolumeUnit.valueOf(unitName));
                case "TEMPERATURE": return new QuantityModel<>(dto.getValue(), TemperatureUnit.valueOf(unitName));
                default: throw new QuantityMeasurementException("Unsupported measurement type: " + type);
            }
        } catch (IllegalArgumentException e) {
            throw new QuantityMeasurementException("Unknown unit '" + unitName + "' for type " + type, e);
        }
    }

    private QuantityDTO.LengthUnit      dtoLengthUnit(LengthUnit u)      { return QuantityDTO.LengthUnit.valueOf(u.name()); }
    private QuantityDTO.WeightUnit      dtoWeightUnit(WeightUnit u)      { return QuantityDTO.WeightUnit.valueOf(u.name()); }
    private QuantityDTO.VolumeUnit      dtoVolumeUnit(VolumeUnit u)      { return QuantityDTO.VolumeUnit.valueOf(u.name()); }
    private QuantityDTO.TemperatureUnit dtoTemperatureUnit(TemperatureUnit u) { return QuantityDTO.TemperatureUnit.valueOf(u.name()); }

    private void validateNotNull(QuantityDTO q1, QuantityDTO q2, String op) {
        if (q1 == null || q2 == null)
            throw new QuantityMeasurementException("Null operand is not allowed for " + op);
    }

    private void validateSameCategory(QuantityDTO q1, QuantityDTO q2, String op) {
        String t1 = q1.getUnit().getMeasurementType();
        String t2 = q2.getUnit().getMeasurementType();
        if (!t1.equals(t2))
            throw new QuantityMeasurementException("Cannot " + op + " different measurement categories: " + t1 + " vs " + t2);
    }
}
