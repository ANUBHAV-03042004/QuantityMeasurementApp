package com.app.quantitymeasurementapp.auth;

import com.app.quantitymeasurementapp.exception.UserAlreadyExistsException;
import com.app.quantitymeasurementapp.security.JwtUtil;
import com.app.quantitymeasurementapp.security.OAuth2SuccessHandler;
import com.app.quantitymeasurementapp.service.PasswordResetService;
import com.app.quantitymeasurementapp.user.User;
import com.app.quantitymeasurementapp.user.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
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

    /**
     * Public HTTPS base URL of this backend as the browser sees it (via CloudFront).
     * Used for absolute redirects — relative sendRedirect() resolves to http:// behind CF.
     *
     * Set in application-prod.properties:
     *   app.backend.base-url=https://dpvh78pj77mvc.cloudfront.net
     * Or as AWS Elastic Beanstalk env var:
     *   APP_BACKEND_BASE_URL=https://dpvh78pj77mvc.cloudfront.net
     */
    @Value("${app.backend.base-url:https://dpvh78pj77mvc.cloudfront.net}")
    private String backendBaseUrl;

    @Value("${app.frontend.base-url:https://anubhav-03042004.github.io/QuantityMeasurementApp-Frontend}")
    private String frontendBaseUrl;

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
    //   1. CloudFront strips unknown query params by default, so
    //      /oauth2/authorization/google?frontend=angular never reaches the backend.
    //   2. OAuth2FrontendHintFilter runs AFTER Spring's OAuth2AuthorizationRequest-
    //      RedirectFilter, so the hint was saved too late.
    //
    // Fix:
    //   Angular calls GET /api/v1/auth/oauth2-start?frontend=angular
    //   -> Saves "angular" in session + SameSite=None cookie on this very request
    //   -> Then issues an ABSOLUTE redirect to CloudFront/oauth2/authorization/google
    //      (relative sendRedirect resolves to http:// behind CF -> 400)
    //   -> OAuth2SuccessHandler reads session/cookie on callback as before.

    @GetMapping("/api/v1/auth/oauth2-start")
    @Operation(summary = "Store frontend hint in session/cookie, then redirect to Google OAuth2")
    public void oauth2Start(
            @RequestParam(defaultValue = "legacy") String frontend,
            HttpServletRequest  request,
            HttpServletResponse response) throws java.io.IOException {

        // 1. Session
        request.getSession(true)
               .setAttribute(OAuth2SuccessHandler.SESSION_ATTR_FRONTEND, frontend);

        // 2. Cookie — SameSite=None; Secure survives the Google OAuth2 round-trip
        Cookie c = new Cookie("oauth2_frontend", frontend);
        c.setPath("/");
        c.setMaxAge(300);
        c.setSecure(true);
        c.setHttpOnly(false);
        response.addCookie(c);
        response.addHeader("Set-Cookie",
            "oauth2_frontend=" + frontend + "; Path=/; Max-Age=300; Secure; SameSite=None");

        // 3. ABSOLUTE redirect — relative URLs break behind CloudFront because
        //    Tomcat resolves them to http:// which CF rejects -> 400
        response.sendRedirect(backendBaseUrl + "/oauth2/authorization/google");
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

    // ── Validate reset token (called by frontend before showing the form) ─────

    @GetMapping("/api/v1/auth/reset-password/validate")
    @Operation(summary = "Check whether a password-reset token is still valid")
    public ResponseEntity<Map<String, Object>> validateResetToken(@RequestParam String token) {
        boolean valid = passwordResetService.isTokenValid(token);
        return ResponseEntity.ok(Map.of("valid", valid));
    }

    // ── Reset password ────────────────────────────────────────────────────────

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

    private String resolveFrontendBaseUrl(HttpServletRequest req) {
        String xOrigin = req.getHeader("X-Frontend-Origin");
        if (xOrigin != null && !xOrigin.isBlank()) {
            // Vercel preview/production deployments — all *.vercel.app URLs
            if (xOrigin.contains("vercel.app")) {
                // The Vercel app uses /reset-password route (no .html)
                return xOrigin;
            }
            // GitHub Pages — origin is just https://anubhav-03042004.github.io
            // but the app lives under the repo sub-path
            if (xOrigin.contains("github.io")) {
                return "https://anubhav-03042004.github.io/QuantityMeasurementApp-Frontend";
            }
            // localhost dev
            if (xOrigin.contains("localhost") || xOrigin.contains("127.0.0.1")) {
                return xOrigin;
            }
        }
        // Fallback: use the configured env var (covers Swagger/Postman calls)
        return frontendBaseUrl;
    }
}