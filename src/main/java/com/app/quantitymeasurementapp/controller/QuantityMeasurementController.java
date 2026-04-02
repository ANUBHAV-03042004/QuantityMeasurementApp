package com.app.quantitymeasurementapp.controller;

import com.app.quantitymeasurementapp.model.QuantityMeasurementDTO;
import com.app.quantitymeasurementapp.model.QuantityInputDTO;
import com.app.quantitymeasurementapp.service.IQuantityMeasurementService;
import com.app.quantitymeasurementapp.user.User;
import com.app.quantitymeasurementapp.user.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/quantities")
@Tag(name = "Quantity Measurement", description = "Compare, convert and perform arithmetic on quantities")
public class QuantityMeasurementController {

    private static final Logger log = LoggerFactory.getLogger(QuantityMeasurementController.class);

    private final IQuantityMeasurementService service;
    private final UserRepository userRepository;

    @Autowired
    public QuantityMeasurementController(IQuantityMeasurementService service,
                                          UserRepository userRepository) {
        this.service = service;
        this.userRepository = userRepository;
    }

    // ── Resolve userId from JWT principal (null for guests) ──────────────────

    private Long resolveUserId(UserDetails principal) {
        if (principal == null) return null;
        return userRepository.findByEmail(principal.getUsername())
                .map(User::getId)
                .orElse(null);
    }

    // ── POST /compare ─────────────────────────────────────────────────────────

    @PostMapping("/compare")
    @Operation(summary = "Compare two quantities for equality")
    public ResponseEntity<QuantityMeasurementDTO> compare(
            @Valid @RequestBody QuantityInputDTO input,
            @AuthenticationPrincipal UserDetails principal) {
        Long userId = resolveUserId(principal);
        return ResponseEntity.ok(
                service.compare(input.getThisQuantityDTO(), input.getThatQuantityDTO(), userId));
    }

    // ── POST /convert ─────────────────────────────────────────────────────────

    @PostMapping("/convert")
    @Operation(summary = "Convert a quantity to a different unit")
    public ResponseEntity<QuantityMeasurementDTO> convert(
            @Valid @RequestBody QuantityInputDTO input,
            @AuthenticationPrincipal UserDetails principal) {
        Long userId = resolveUserId(principal);
        return ResponseEntity.ok(
                service.convert(input.getThisQuantityDTO(), input.getThatQuantityDTO(), userId));
    }

    // ── POST /add ─────────────────────────────────────────────────────────────

    @PostMapping("/add")
    @Operation(summary = "Add two quantities")
    public ResponseEntity<QuantityMeasurementDTO> add(
            @Valid @RequestBody QuantityInputDTO input,
            @AuthenticationPrincipal UserDetails principal) {
        Long userId = resolveUserId(principal);
        return ResponseEntity.ok(
                service.add(input.getThisQuantityDTO(), input.getThatQuantityDTO(), userId));
    }

    // ── POST /subtract ────────────────────────────────────────────────────────

    @PostMapping("/subtract")
    @Operation(summary = "Subtract one quantity from another")
    public ResponseEntity<QuantityMeasurementDTO> subtract(
            @Valid @RequestBody QuantityInputDTO input,
            @AuthenticationPrincipal UserDetails principal) {
        Long userId = resolveUserId(principal);
        return ResponseEntity.ok(
                service.subtract(input.getThisQuantityDTO(), input.getThatQuantityDTO(), userId));
    }

    // ── POST /divide ──────────────────────────────────────────────────────────

    @PostMapping("/divide")
    @Operation(summary = "Divide one quantity by another (returns dimensionless ratio)")
    public ResponseEntity<QuantityMeasurementDTO> divide(
            @Valid @RequestBody QuantityInputDTO input,
            @AuthenticationPrincipal UserDetails principal) {
        Long userId = resolveUserId(principal);
        return ResponseEntity.ok(
                service.divide(input.getThisQuantityDTO(), input.getThatQuantityDTO(), userId));
    }

    // ── GET history (user-scoped) ─────────────────────────────────────────────

    @GetMapping("/history/operation/{operation}")
    @Operation(summary = "Get your history for a specific operation")
    public ResponseEntity<List<QuantityMeasurementDTO>> historyByOperation(
            @Parameter(description = "Operation name, e.g. COMPARE") @PathVariable String operation,
            @AuthenticationPrincipal UserDetails principal) {
        Long userId = resolveUserId(principal);
        if (userId == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(service.getHistoryByOperation(operation, userId));
    }

    @GetMapping("/history/type/{measurementType}")
    @Operation(summary = "Get your history for a measurement type")
    public ResponseEntity<List<QuantityMeasurementDTO>> historyByType(
            @Parameter(description = "Measurement type, e.g. LengthUnit") @PathVariable String measurementType,
            @AuthenticationPrincipal UserDetails principal) {
        Long userId = resolveUserId(principal);
        if (userId == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(service.getHistoryByMeasurementType(measurementType, userId));
    }

    @GetMapping("/count/{operation}")
    @Operation(summary = "Count your successful records for a given operation")
    public ResponseEntity<Long> countByOperation(
            @Parameter(description = "Operation name, e.g. COMPARE") @PathVariable String operation,
            @AuthenticationPrincipal UserDetails principal) {
        Long userId = resolveUserId(principal);
        if (userId == null) return ResponseEntity.ok(0L);
        return ResponseEntity.ok(service.getOperationCount(operation, userId));
    }

    @GetMapping("/history/errored")
    @Operation(summary = "Get your error records")
    public ResponseEntity<List<QuantityMeasurementDTO>> errorHistory(
            @AuthenticationPrincipal UserDetails principal) {
        Long userId = resolveUserId(principal);
        if (userId == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(service.getErrorHistory(userId));
    }
}
