package com.app.quantitymeasurementapp.auth;

import com.app.quantitymeasurementapp.exception.UserAlreadyExistsException;
import com.app.quantitymeasurementapp.security.JwtUtil;
import com.app.quantitymeasurementapp.service.PasswordResetService;
import com.app.quantitymeasurementapp.user.User;
import com.app.quantitymeasurementapp.user.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Cookie;
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

    private final AuthenticationManager authManager;
    private final UserRepository        userRepository;
    private final PasswordEncoder       passwordEncoder;
    private final JwtUtil               jwtUtil;
    private final PasswordResetService  passwordResetService;

    public AuthController(AuthenticationManager authManager,
                          UserRepository        userRepository,
                          PasswordEncoder       passwordEncoder,
                          JwtUtil               jwtUtil,
                          PasswordResetService  passwordResetService) {
        this.authManager          = authManager;
        this.userRepository       = userRepository;
        this.passwordEncoder      = passwordEncoder;
        this.jwtUtil              = jwtUtil;
        this.passwordResetService = passwordResetService;
    }

    // ── Register ──────────────────────────────────────────────────────────────

    @PostMapping("/api/v1/auth/register")
    @Operation(summary = "Register a new user with email and password")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest req) {
        if (userRepository.existsByEmail(req.getEmail()))
            throw new UserAlreadyExistsException("Email already registered: " + req.getEmail());

        User user = User.builder()
                .firstName(req.getFirstName()).lastName(req.getLastName())
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword()))
                .authProvider(User.AuthProvider.AUTH_LOCAL)
                .role(User.Role.USER)
                .build();
        userRepository.save(user);
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());
        return ResponseEntity.status(HttpStatus.CREATED).body(AuthResponse.builder()
                .token(token).tokenType("Bearer")
                .email(user.getEmail()).role(user.getRole().name())
                .expiresInSeconds(jwtUtil.getExpirationSeconds()).build());
    }

    // ── Login ─────────────────────────────────────────────────────────────────

    @PostMapping("/api/v1/auth/login")
    @Operation(summary = "Login with email and password, receive JWT")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest req) {
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword()));
        UserDetails ud   = (UserDetails) auth.getPrincipal();
        User        user = userRepository.findByEmail(ud.getUsername()).orElseThrow();
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());
        return ResponseEntity.ok(AuthResponse.builder()
                .token(token).tokenType("Bearer")
                .email(user.getEmail()).role(user.getRole().name())
                .expiresInSeconds(jwtUtil.getExpirationSeconds()).build());
    }

    // ── OAuth2 success landing ────────────────────────────────────────────────

    @GetMapping("/oauth2/success")
    @Operation(summary = "OAuth2 redirect landing — returns the JWT issued after Google login")
    public ResponseEntity<AuthResponse> oauth2Success(@RequestParam String token) {
        return ResponseEntity.ok(AuthResponse.builder()
                .token(token).tokenType("Bearer")
                .email(jwtUtil.extractEmail(token)).role(jwtUtil.extractRole(token))
                .expiresInSeconds(jwtUtil.getExpirationSeconds()).build());
    }

    // ── OAuth2 start — stores frontend hint THEN redirects to Google ──────────
    //
    // Why this exists:
    //   CloudFront strips unknown query params by default, so
    //   /oauth2/authorization/google?frontend=angular never reaches the backend
    //   as-is.  Also, OAuth2FrontendHintFilter is registered after Spring's own
    //   OAuth2AuthorizationRequestRedirectFilter, so the hint was saved too late.
    //
    // Fix: Angular calls GET /api/v1/auth/oauth2-start?frontend=angular
    //   → This endpoint saves "angular" in both session + cookie on the SAME
    //     request (no race condition) and then server-side redirects to
    //     /oauth2/authorization/google (no ?frontend param needed anymore).
    //   → OAuth2SuccessHandler reads session/cookie on the way back as before.

    @GetMapping("/api/v1/auth/oauth2-start")
    @Operation(summary = "Store frontend hint in session/cookie, then redirect to Google OAuth2")
    public void oauth2Start(
            @RequestParam(defaultValue = "legacy") String frontend,
            HttpServletRequest  request,
            HttpServletResponse response) throws java.io.IOException {

        // 1. Session — works on single-instance / sticky-session setups
        request.getSession(true)
               .setAttribute(com.app.quantitymeasurementapp.security.OAuth2SuccessHandler.SESSION_ATTR_FRONTEND, frontend);

        // 2. Cookie — SameSite=None; Secure so it survives the Google round-trip
        Cookie c = new Cookie("oauth2_frontend", frontend);
        c.setPath("/");
        c.setMaxAge(300); // 5 minutes
        c.setSecure(true);
        c.setHttpOnly(false);
        response.addCookie(c);
        response.addHeader("Set-Cookie",
            "oauth2_frontend=" + frontend + "; Path=/; Max-Age=300; Secure; SameSite=None");

        // 3. Redirect to Spring Security's OAuth2 endpoint (no ?frontend needed)
        response.sendRedirect("/oauth2/authorization/google");
    }

    // ── Forgot password ───────────────────────────────────────────────────────

    @PostMapping("/api/v1/auth/forgot-password")
    @Operation(summary = "Request a password-reset email")
    public ResponseEntity<Map<String, String>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest req,
            HttpServletRequest httpRequest) {

        String frontendUrl = resolveFrontendBaseUrl(httpRequest);
        passwordResetService.initiatePasswordReset(req.getEmail(), frontendUrl);
        return ResponseEntity.ok(Map.of(
                "message", "If that email is registered, a reset link has been sent."));
    }

    // ── Reset password ────────────────────────────────────────────────────────

    /**
     * POST /api/v1/auth/reset-password
     * Body: { "token": "...", "newPassword": "NewSecret123!" }
     */
    @PostMapping("/api/v1/auth/reset-password")
    @Operation(summary = "Reset password using the token from the reset email")
    public ResponseEntity<Map<String, String>> resetPassword(
            @Valid @RequestBody ResetPasswordRequest req) {
        try {
            passwordResetService.resetPassword(req.getToken(), req.getNewPassword());
            return ResponseEntity.ok(Map.of("message", "Password reset successfully. You can now sign in."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    /** Derives the frontend base URL from the Origin header (preferred) or Referer. */
    private String resolveFrontendBaseUrl(HttpServletRequest req) {
        String origin = req.getHeader("Origin");
        if (origin != null && !origin.isBlank()) return origin;

        String referer = req.getHeader("Referer");
        if (referer != null && !referer.isBlank()) {
            try {
                java.net.URI uri = new java.net.URI(referer);
                return uri.getScheme() + "://" + uri.getHost()
                        + (uri.getPort() != -1 ? ":" + uri.getPort() : "");
            } catch (Exception ignored) {}
        }
        return null; // PasswordResetService uses configured fallback
    }

}