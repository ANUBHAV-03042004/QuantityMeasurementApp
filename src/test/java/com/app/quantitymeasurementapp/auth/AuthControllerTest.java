package com.app.quantitymeasurementapp.auth;

import com.app.quantitymeasurementapp.service.IUserService;
import com.app.quantitymeasurementapp.util.SecurityConfig;
import com.app.quantitymeasurementapp.security.JwtAuthFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@Import({ SecurityConfig.class })
@DisplayName("AuthController — MockMvc tests")
class AuthControllerTest {

    @Autowired MockMvc      mockMvc;
    @Autowired ObjectMapper objectMapper;

    @MockBean IUserService   userService;
    @MockBean JwtAuthFilter  jwtAuthFilter;

    private static final AuthResponse SAMPLE_RESPONSE = AuthResponse.builder()
            .token("eyJhbGci.sample.token")
            .tokenType("Bearer")
            .email("user@example.com")
            .role("USER")
            .expiresInSeconds(86400)
            .build();

    @Test
    @DisplayName("POST /register: 201 with valid payload")
    void register_201_validPayload() throws Exception {
        when(userService.register(any())).thenReturn(SAMPLE_RESPONSE);

        RegisterRequest req = new RegisterRequest();
        req.setFirstName("Alice");
        req.setLastName("Smith");
        req.setEmail("alice@example.com");
        req.setPassword("Passw0rd@1");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
               .andExpect(status().isCreated())
               .andExpect(jsonPath("$.tokenType").value("Bearer"))
               .andExpect(jsonPath("$.email").value("user@example.com"));
    }

    @Test
    @DisplayName("POST /register: 400 when email format is invalid")
    void register_400_invalidEmail() throws Exception {
        RegisterRequest req = new RegisterRequest();
        req.setFirstName("Alice");
        req.setLastName("Smith");
        req.setEmail("not-an-email");
        req.setPassword("Passw0rd@1");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
               .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /register: 400 when password is too weak")
    void register_400_weakPassword() throws Exception {
        RegisterRequest req = new RegisterRequest();
        req.setFirstName("Alice");
        req.setLastName("Smith");
        req.setEmail("alice@example.com");
        req.setPassword("weakpass");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
               .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /login: 200 with correct credentials")
    void login_200_validCredentials() throws Exception {
        when(userService.login(any())).thenReturn(SAMPLE_RESPONSE);

        LoginRequest req = new LoginRequest();
        req.setEmail("user@example.com");
        req.setPassword("Passw0rd@1");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.token").exists())
               .andExpect(jsonPath("$.tokenType").value("Bearer"));
    }

    @Test
    @DisplayName("POST /login: 401 with wrong credentials")
    void login_401_badCredentials() throws Exception {
        when(userService.login(any())).thenThrow(new BadCredentialsException("Bad credentials"));

        LoginRequest req = new LoginRequest();
        req.setEmail("user@example.com");
        req.setPassword("WrongPassword1!");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
               .andExpect(status().isUnauthorized());
    }
}
