package com.app.quantitymeasurementapp.auth;

import com.app.quantitymeasurementapp.exception.UserAlreadyExistsException;
import com.app.quantitymeasurementapp.security.JwtUtil;
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


@RestController
@Tag(name = "Authentication", description = "Register, login and Google OAuth2")
public class AuthController {

    private final AuthenticationManager authManager;
    private final UserRepository        userRepository;
    private final PasswordEncoder       passwordEncoder;
    private final JwtUtil               jwtUtil;

    public AuthController(AuthenticationManager authManager,
                          UserRepository        userRepository,
                          PasswordEncoder       passwordEncoder,
                          JwtUtil               jwtUtil) {
        this.authManager     = authManager;
        this.userRepository  = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil         = jwtUtil;
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
                AuthResponse.builder()
                        .token(token)
                        .tokenType("Bearer")
                        .email(user.getEmail())
                        .role(user.getRole().name())
                        .expiresInSeconds(jwtUtil.getExpirationSeconds())
                        .build()
        );
    }

    // ── Login ─────────────────────────────────────────────────────────────────

    @PostMapping("/api/v1/auth/login")
    @Operation(summary = "Login with email and password, receive JWT")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest req) {

        Authentication authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword()));

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow();

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());

        return ResponseEntity.ok(
                AuthResponse.builder()
                        .token(token)
                        .tokenType("Bearer")
                        .email(user.getEmail())
                        .role(user.getRole().name())
                        .expiresInSeconds(jwtUtil.getExpirationSeconds())
                        .build()
        );
    }

    // ── OAuth2 success landing ────────────────────────────────────────────────

    @GetMapping("/oauth2/success")
    @Operation(summary = "OAuth2 redirect landing — returns the JWT issued after Google login")
    public ResponseEntity<AuthResponse> oauth2Success(@RequestParam String token) {

        String email = jwtUtil.extractEmail(token);
        String role  = jwtUtil.extractRole(token);

        return ResponseEntity.ok(
                AuthResponse.builder()
                        .token(token)
                        .tokenType("Bearer")
                        .email(email)
                        .role(role)
                        .expiresInSeconds(jwtUtil.getExpirationSeconds())
                        .build()
        );
    }
}
