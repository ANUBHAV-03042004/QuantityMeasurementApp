package com.app.quantitymeasurementapp.auth;

import com.app.quantitymeasurementapp.exception.UserAlreadyExistsException;
import com.app.quantitymeasurementapp.security.JwtUtil;
import com.app.quantitymeasurementapp.service.PasswordResetService;
import com.app.quantitymeasurementapp.user.User;
import com.app.quantitymeasurementapp.user.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@Tag(name = "Authentication", description = "Register, login, Google OAuth2, forgot/reset password")
public class AuthController {

    private final AuthenticationManager  authManager;
    private final UserRepository         userRepository;
    private final PasswordEncoder        passwordEncoder;
    private final JwtUtil                jwtUtil;
    private final PasswordResetService   passwordResetService;

    public AuthController(AuthenticationManager  authManager,
                          UserRepository         userRepository,
                          PasswordEncoder        passwordEncoder,
                          JwtUtil                jwtUtil,
                          PasswordResetService   passwordResetService) {
        this.authManager          = authManager;
        this.userRepository       = userRepository;
        this.passwordEncoder      = passwordEncoder;
        this.jwtUtil              = jwtUtil;
        this.passwordResetService = passwordResetService;
    }

    // ── Register ─────────────────────────────────────────────────────────────

    @PostMapping("/api/v1/auth/register")
    @Operation(summary = "Register a new user with email and password")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest req) {
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new UserAlreadyExistsException("Email already registered: " + req.getEmail());
        }
        User user = User.builder()
                .firstName(req.getFirstName())
                .lastName(req.getLastName())
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword()))
                .authProvider(User.AuthProvider.AUTH_LOCAL)
                .role(User.Role.USER)
                .build();
        userRepository.save(user);
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());
        return ResponseEntity.status(HttpStatus.CREATED).body(
                AuthResponse.builder().token(token).tokenType("Bearer")
                        .email(user.getEmail()).role(user.getRole().name())
                        .expiresInSeconds(jwtUtil.getExpirationSeconds()).build());
    }

    // ── Login ─────────────────────────────────────────────────────────────────

    @PostMapping("/api/v1/auth/login")
    @Operation(summary = "Login with email and password, receive JWT")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest req) {
        Authentication authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword()));
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());
        return ResponseEntity.ok(
                AuthResponse.builder().token(token).tokenType("Bearer")
                        .email(user.getEmail()).role(user.getRole().name())
                        .expiresInSeconds(jwtUtil.getExpirationSeconds()).build());
    }

    // ── OAuth2 success landing ────────────────────────────────────────────────

    @GetMapping("/oauth2/success")
    @Operation(summary = "OAuth2 redirect landing — returns the JWT issued after Google login")
    public ResponseEntity<AuthResponse> oauth2Success(@RequestParam String token) {
        String email = jwtUtil.extractEmail(token);
        String role  = jwtUtil.extractRole(token);
        return ResponseEntity.ok(
                AuthResponse.builder().token(token).tokenType("Bearer")
                        .email(email).role(role)
                        .expiresInSeconds(jwtUtil.getExpirationSeconds()).build());
    }

    // ── Forgot password ───────────────────────────────────────────────────────

    @PostMapping("/api/v1/auth/forgot-password")
    @Operation(summary = "Send a password-reset email to the given address")
    public ResponseEntity<Map<String, String>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest req) {
        passwordResetService.initiateReset(req.getEmail());
        // Always return success to prevent user enumeration
        return ResponseEntity.ok(Map.of(
            "message", "If that email is registered you will receive a reset link shortly."));
    }

    // ── Validate reset token (HEAD/GET — frontend polls before showing form) ──

    @GetMapping("/api/v1/auth/reset-password/validate")
    @Operation(summary = "Check whether a reset token is still valid")
    public ResponseEntity<Map<String, Boolean>> validateToken(@RequestParam String token) {
        boolean valid = passwordResetService.isTokenValid(token);
        return ResponseEntity.ok(Map.of("valid", valid));
    }

    // ── Reset password ────────────────────────────────────────────────────────

    @PostMapping("/api/v1/auth/reset-password")
    @Operation(summary = "Set a new password using the reset token")
    public ResponseEntity<Map<String, String>> resetPassword(
            @Valid @RequestBody ResetPasswordRequest req) {
        try {
            passwordResetService.resetPassword(req.getToken(), req.getNewPassword());
            return ResponseEntity.ok(Map.of("message", "Password updated successfully. You can now sign in."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
