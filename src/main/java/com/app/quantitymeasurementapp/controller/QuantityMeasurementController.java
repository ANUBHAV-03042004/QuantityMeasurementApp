package com.app.quantitymeasurementapp.controller;

import com.app.quantitymeasurementapp.model.QuantityMeasurementDTO;
import com.app.quantitymeasurementapp.model.QuantityInputDTO;
import com.app.quantitymeasurementapp.service.IQuantityMeasurementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/quantities")
@Tag(name = "Quantity Measurement", description = "Compare, convert and perform arithmetic on quantities")
public class QuantityMeasurementController {

    private static final Logger log = LoggerFactory.getLogger(QuantityMeasurementController.class);

    private final IQuantityMeasurementService service;

    @Autowired
    public QuantityMeasurementController(IQuantityMeasurementService service) {
        this.service = service;
    }

    // ── POST /compare ─────────────────────────────────────────────────────────

    @PostMapping("/compare")
    @Operation(summary = "Compare two quantities for equality")
    public ResponseEntity<QuantityMeasurementDTO> compare(
            @Valid @RequestBody QuantityInputDTO input) {
        log.info("POST /compare  thisUnit={} thatUnit={}",
                 input.getThisQuantityDTO().getUnit(), input.getThatQuantityDTO().getUnit());
        return ResponseEntity.ok(
                service.compare(input.getThisQuantityDTO(), input.getThatQuantityDTO()));
    }

    // ── POST /convert ─────────────────────────────────────────────────────────

    @PostMapping("/convert")
    @Operation(summary = "Convert a quantity to a different unit")
    public ResponseEntity<QuantityMeasurementDTO> convert(
            @Valid @RequestBody QuantityInputDTO input) {
        log.info("POST /convert  from={} to={}",
                 input.getThisQuantityDTO().getUnit(), input.getThatQuantityDTO().getUnit());
        return ResponseEntity.ok(
                service.convert(input.getThisQuantityDTO(), input.getThatQuantityDTO()));
    }

    // ── POST /add ─────────────────────────────────────────────────────────────

    @PostMapping("/add")
    @Operation(summary = "Add two quantities")
    public ResponseEntity<QuantityMeasurementDTO> add(
            @Valid @RequestBody QuantityInputDTO input) {
        log.info("POST /add  {} {} + {} {}",
                 input.getThisQuantityDTO().getValue(), input.getThisQuantityDTO().getUnit(),
                 input.getThatQuantityDTO().getValue(), input.getThatQuantityDTO().getUnit());
        return ResponseEntity.ok(
                service.add(input.getThisQuantityDTO(), input.getThatQuantityDTO()));
    }

    // ── POST /subtract ────────────────────────────────────────────────────────

    @PostMapping("/subtract")
    @Operation(summary = "Subtract one quantity from another")
    public ResponseEntity<QuantityMeasurementDTO> subtract(
            @Valid @RequestBody QuantityInputDTO input) {
        log.info("POST /subtract  {} {} - {} {}",
                 input.getThisQuantityDTO().getValue(), input.getThisQuantityDTO().getUnit(),
                 input.getThatQuantityDTO().getValue(), input.getThatQuantityDTO().getUnit());
        return ResponseEntity.ok(
                service.subtract(input.getThisQuantityDTO(), input.getThatQuantityDTO()));
    }

    // ── POST /divide ──────────────────────────────────────────────────────────

    @PostMapping("/divide")
    @Operation(summary = "Divide one quantity by another (returns dimensionless ratio)")
    public ResponseEntity<QuantityMeasurementDTO> divide(
            @Valid @RequestBody QuantityInputDTO input) {
        log.info("POST /divide  {} {} / {} {}",
                 input.getThisQuantityDTO().getValue(), input.getThisQuantityDTO().getUnit(),
                 input.getThatQuantityDTO().getValue(), input.getThatQuantityDTO().getUnit());
        return ResponseEntity.ok(
                service.divide(input.getThisQuantityDTO(), input.getThatQuantityDTO()));
    }

    // ── GET history ───────────────────────────────────────────────────────────

    @GetMapping("/history/operation/{operation}")
    @Operation(summary = "Get all records for a specific operation (COMPARE, ADD, etc.)")
    public ResponseEntity<List<QuantityMeasurementDTO>> historyByOperation(
            @Parameter(description = "Operation name, e.g. COMPARE") @PathVariable String operation) {
        return ResponseEntity.ok(service.getHistoryByOperation(operation));
    }

    @GetMapping("/history/type/{measurementType}")
    @Operation(summary = "Get all records for a measurement type (LengthUnit, WeightUnit, etc.)")
    public ResponseEntity<List<QuantityMeasurementDTO>> historyByType(
            @Parameter(description = "Measurement type, e.g. LengthUnit") @PathVariable String measurementType) {
        return ResponseEntity.ok(service.getHistoryByMeasurementType(measurementType));
    }

    @GetMapping("/count/{operation}")
    @Operation(summary = "Count successful records for a given operation")
    public ResponseEntity<Long> countByOperation(
            @Parameter(description = "Operation name, e.g. COMPARE") @PathVariable String operation) {
        return ResponseEntity.ok(service.getOperationCount(operation));
    }

    @GetMapping("/history/errored")
    @Operation(summary = "Get all error records")
    public ResponseEntity<List<QuantityMeasurementDTO>> errorHistory() {
        return ResponseEntity.ok(service.getErrorHistory());
    }
}
