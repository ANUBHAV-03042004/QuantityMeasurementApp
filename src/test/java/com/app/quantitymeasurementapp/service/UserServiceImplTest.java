package com.app.quantitymeasurementapp.service;

import com.app.quantitymeasurementapp.auth.LoginRequest;
import com.app.quantitymeasurementapp.auth.RegisterRequest;
import com.app.quantitymeasurementapp.auth.AuthResponse;
import com.app.quantitymeasurementapp.exception.UserAlreadyExistsException;
import com.app.quantitymeasurementapp.messaging.RabbitMQProducer;
import com.app.quantitymeasurementapp.security.JwtUtil;
import com.app.quantitymeasurementapp.user.User;
import com.app.quantitymeasurementapp.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserServiceImpl — unit tests")
class UserServiceImplTest {

    @Mock UserRepository        userRepository;
    @Mock PasswordEncoder       passwordEncoder;
    @Mock JwtUtil               jwtUtil;
    @Mock AuthenticationManager authenticationManager;
    @Mock EmailService          emailService;
    @Mock RabbitMQProducer      rabbitMQProducer;

    @InjectMocks UserServiceImpl userService;

    private RegisterRequest registerRequest;
    private User            savedUser;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setFirstName("Alice");
        registerRequest.setLastName("Smith");
        registerRequest.setEmail("alice@example.com");
        registerRequest.setPassword("Passw0rd@1");

        savedUser = User.builder()
                .id(1L).firstName("Alice").lastName("Smith")
                .email("alice@example.com").password("hashed")
                .role(User.Role.USER).build();
    }

    // ── register ──────────────────────────────────────────────────────────────

    @Test
    @DisplayName("register: returns AuthResponse with token for new user")
    void register_returnsAuthResponse() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("hashed");
        when(userRepository.save(any())).thenReturn(savedUser);
        when(jwtUtil.generateToken(any())).thenReturn("jwt.token.here");
        when(jwtUtil.getExpirationSeconds()).thenReturn(86400L);

        AuthResponse response = userService.register(registerRequest);

        assertThat(response.getToken()).isEqualTo("jwt.token.here");
        assertThat(response.getTokenType()).isEqualTo("Bearer");
        assertThat(response.getRole()).isEqualTo("USER");
    }

    @Test
    @DisplayName("register: throws UserAlreadyExistsException when email taken")
    void register_throwsWhenEmailExists() {
        when(userRepository.existsByEmail("alice@example.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.register(registerRequest))
                .isInstanceOf(UserAlreadyExistsException.class)
                .hasMessageContaining("already exists");
    }

    @Test
    @DisplayName("register: BCrypt-encodes password before saving")
    void register_encodesPassword() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode("Passw0rd@1")).thenReturn("bcrypt_hash");
        when(userRepository.save(any())).thenReturn(savedUser);
        when(jwtUtil.generateToken(any())).thenReturn("token");
        when(jwtUtil.getExpirationSeconds()).thenReturn(86400L);

        userService.register(registerRequest);

        verify(passwordEncoder).encode("Passw0rd@1");
        verify(userRepository).save(argThat(u -> "bcrypt_hash".equals(u.getPassword())));
    }

    @Test
    @DisplayName("register: sends welcome email after save")
    void register_sendsWelcomeEmail() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn("hashed");
        when(userRepository.save(any())).thenReturn(savedUser);
        when(jwtUtil.generateToken(any())).thenReturn("token");
        when(jwtUtil.getExpirationSeconds()).thenReturn(86400L);

        userService.register(registerRequest);

        verify(emailService).sendWelcomeEmail(eq("alice@example.com"), anyString());
    }

    @Test
    @DisplayName("register: publishes USER_REGISTERED event to RabbitMQ")
    void register_publishesRabbitMQEvent() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn("hashed");
        when(userRepository.save(any())).thenReturn(savedUser);
        when(jwtUtil.generateToken(any())).thenReturn("token");
        when(jwtUtil.getExpirationSeconds()).thenReturn(86400L);

        userService.register(registerRequest);

        verify(rabbitMQProducer).publishUserRegistered("alice@example.com");
    }

    // ── login ─────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("login: returns AuthResponse for valid credentials")
    void login_returnsAuthResponse() {
        LoginRequest loginReq = new LoginRequest();
        loginReq.setEmail("alice@example.com");
        loginReq.setPassword("Passw0rd@1");

        when(authenticationManager.authenticate(any())).thenReturn(
                new UsernamePasswordAuthenticationToken("alice@example.com", null));
        when(userRepository.findByEmail("alice@example.com")).thenReturn(Optional.of(savedUser));
        when(jwtUtil.generateToken(savedUser)).thenReturn("login.token");
        when(jwtUtil.getExpirationSeconds()).thenReturn(86400L);

        AuthResponse response = userService.login(loginReq);

        assertThat(response.getToken()).isEqualTo("login.token");
        assertThat(response.getEmail()).isEqualTo("alice@example.com");
    }

    @Test
    @DisplayName("login: throws BadCredentialsException for wrong password")
    void login_throwsForWrongPassword() {
        LoginRequest loginReq = new LoginRequest();
        loginReq.setEmail("alice@example.com");
        loginReq.setPassword("WrongPassword1!");

        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThatThrownBy(() -> userService.login(loginReq))
                .isInstanceOf(BadCredentialsException.class);
    }
}
