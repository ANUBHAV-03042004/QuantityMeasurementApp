package com.app.quantitymeasurementapp.auth;

import com.app.quantitymeasurementapp.service.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Handles user registration and login.
 * These endpoints are publicly accessible (no JWT required).
 *
 * POST /api/auth/register  → creates a new user, returns JWT
 * POST /api/auth/login     → validates credentials, returns JWT
 */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Register and login to obtain a JWT token")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final IUserService userService;

    public AuthController(IUserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    @Operation(summary = "Register a new user",
               description = "Password: 8-64 chars, must include uppercase, lowercase, digit and special char")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        log.info("POST /api/auth/register for email={}", request.getEmail());
        AuthResponse response = userService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    @Operation(summary = "Login with email and password to receive a JWT token")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("POST /api/auth/login for email={}", request.getEmail());
        return ResponseEntity.ok(userService.login(request));
    }
}
