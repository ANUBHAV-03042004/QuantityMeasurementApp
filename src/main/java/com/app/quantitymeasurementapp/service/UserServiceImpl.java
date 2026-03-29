package com.app.quantitymeasurementapp.service;

import com.app.quantitymeasurementapp.auth.AuthResponse;
import com.app.quantitymeasurementapp.auth.LoginRequest;
import com.app.quantitymeasurementapp.auth.RegisterRequest;
import com.app.quantitymeasurementapp.exception.UserAlreadyExistsException;
import com.app.quantitymeasurementapp.messaging.RabbitMQProducer;
import com.app.quantitymeasurementapp.security.JwtUtil;
import com.app.quantitymeasurementapp.user.User;
import com.app.quantitymeasurementapp.user.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Handles user registration and login.
 * On successful registration:
 *   1. Persists the user with a BCrypt-hashed password
 *   2. Sends a welcome e-mail via {@link EmailService}
 *   3. Publishes a registration event to RabbitMQ
 *   4. Returns a signed JWT token
 */
@Service
public class UserServiceImpl implements IUserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository       userRepository;
    private final PasswordEncoder      passwordEncoder;
    private final JwtUtil              jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final EmailService         emailService;
    private final RabbitMQProducer     rabbitMQProducer;

    public UserServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           JwtUtil jwtUtil,
                           AuthenticationManager authenticationManager,
                           EmailService emailService,
                           RabbitMQProducer rabbitMQProducer) {
        this.userRepository        = userRepository;
        this.passwordEncoder       = passwordEncoder;
        this.jwtUtil               = jwtUtil;
        this.authenticationManager = authenticationManager;
        this.emailService          = emailService;
        this.rabbitMQProducer      = rabbitMQProducer;
    }

    @Override
    public AuthResponse register(RegisterRequest request) {
        log.info("Registering new user: {}", request.getEmail());

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException(
                    "An account with email " + request.getEmail() + " already exists");
        }

        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(User.Role.USER)
                .build();

        userRepository.save(user);
        log.info("User registered successfully: id={} email={}", user.getId(), user.getEmail());

        // Send welcome email (non-blocking; errors are logged but do not fail registration)
        emailService.sendWelcomeEmail(user.getEmail(), user.getFirstName());

        // Publish registration event to RabbitMQ
        rabbitMQProducer.publishUserRegistered(user.getEmail());

        String token = jwtUtil.generateToken(user);
        return buildResponse(token, user);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        log.info("Login attempt for: {}", request.getEmail());

        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        // Load the full User entity to build the token
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow();

        String token = jwtUtil.generateToken(user);
        log.info("Login successful for: {}", request.getEmail());

        // Publish login event
        rabbitMQProducer.publishUserLoggedIn(user.getEmail());

        return buildResponse(token, user);
    }

    // ── private helpers ───────────────────────────────────────────────────────

    private AuthResponse buildResponse(String token, User user) {
        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .email(user.getEmail())
                .role(user.getRole().name())
                .expiresInSeconds(jwtUtil.getExpirationSeconds())
                .build();
    }
}
