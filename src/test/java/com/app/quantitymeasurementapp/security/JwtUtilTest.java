package com.app.quantitymeasurementapp.security;

import com.app.quantitymeasurementapp.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("JwtUtil — unit tests")
class JwtUtilTest {

    private JwtUtil jwtUtil;
    private User    sampleUser;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "jwtSecret",
                "TestSecretKey256BitForJwtValidationInTestsOnlyNotForProduction!");
        ReflectionTestUtils.setField(jwtUtil, "expirationSeconds", 3600L);

        sampleUser = User.builder()
                .id(1L)
                .firstName("Alice")
                .lastName("Smith")
                .email("alice@example.com")
                .password("hashed")
                .role(User.Role.USER)
                .build();
    }

    @Test
    @DisplayName("generateToken returns non-blank JWT string")
    void generateToken_returnsNonBlankString() {
        String token = jwtUtil.generateToken(sampleUser);
        assertThat(token).isNotBlank();
        assertThat(token.split("\\.")).hasSize(3);   // header.payload.signature
    }

    @Test
    @DisplayName("validateToken returns true for a freshly generated token")
    void validateToken_trueForFreshToken() {
        String token = jwtUtil.generateToken(sampleUser);
        assertThat(jwtUtil.validateToken(token)).isTrue();
    }

    @Test
    @DisplayName("validateToken returns false for a tampered token")
    void validateToken_falseForTamperedToken() {
        String token = jwtUtil.generateToken(sampleUser) + "tampered";
        assertThat(jwtUtil.validateToken(token)).isFalse();
    }

    @Test
    @DisplayName("extractEmail returns the user email")
    void extractEmail_returnsEmail() {
        String token = jwtUtil.generateToken(sampleUser);
        assertThat(jwtUtil.extractEmail(token)).isEqualTo("alice@example.com");
    }

    @Test
    @DisplayName("extractRole returns USER for a USER-role token")
    void extractRole_returnsRole() {
        String token = jwtUtil.generateToken(sampleUser);
        assertThat(jwtUtil.extractRole(token)).isEqualTo("USER");
    }

    @Test
    @DisplayName("getExpirationSeconds returns configured value")
    void expirationSeconds_matchesConfig() {
        assertThat(jwtUtil.getExpirationSeconds()).isEqualTo(3600L);
    }
}
