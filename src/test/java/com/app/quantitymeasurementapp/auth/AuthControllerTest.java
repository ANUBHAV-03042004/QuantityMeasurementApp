package com.app.quantitymeasurementapp.auth;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.http.MediaType;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Auth integration tests — register + login")
public class AuthControllerTest {   // <-- public: Eclipse runner requires it to load the class

    @Autowired
    private TestRestTemplate restTemplate;

    private static final String REGISTER = "/api/v1/auth/register";
    private static final String LOGIN    = "/api/v1/auth/login";

    private static String jwtToken;

    @BeforeEach
    void useApacheHttpClient() {
        HttpClient httpClient = HttpClientBuilder.create()
                .disableRedirectHandling()
                .build();
        restTemplate.getRestTemplate()
                    .setRequestFactory(new HttpComponentsClientHttpRequestFactory(httpClient));
    }

    private HttpEntity<String> json(String body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(body, headers);
    }

    // ── Register ──────────────────────────────────────────────────────────────

    @Test
    @Order(1)
    @DisplayName("POST /register: valid payload returns 201 + JWT")
    void register_validUser_returns201AndToken() {
        String body = """
                {
                  "firstName": "Jane",
                  "lastName":  "Doe",
                  "email":     "jane.doe@example.com",
                  "password":  "Test@1234"
                }
                """;
        ResponseEntity<AuthResponse> response =
                restTemplate.postForEntity(REGISTER, json(body), AuthResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getToken()).isNotBlank();
        assertThat(response.getBody().getTokenType()).isEqualTo("Bearer");
        assertThat(response.getBody().getEmail()).isEqualTo("jane.doe@example.com");
        assertThat(response.getBody().getRole()).isEqualTo("USER");
        assertThat(response.getBody().getExpiresInSeconds()).isGreaterThan(0);

        jwtToken = response.getBody().getToken();
    }

    @Test
    @Order(2)
    @DisplayName("POST /register: duplicate email returns 409")
    void register_duplicateEmail_returns409() {
        String body = """
                {
                  "firstName": "Jane",
                  "lastName":  "Doe",
                  "email":     "jane.doe@example.com",
                  "password":  "Test@1234"
                }
                """;
        ResponseEntity<Map> response =
                restTemplate.postForEntity(REGISTER, json(body), Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody().get("error")).isEqualTo("Conflict");
    }

    @Test
    @Order(3)
    @DisplayName("POST /register: weak password returns 400")
    void register_weakPassword_returns400() {
        String body = """
                {
                  "firstName": "John",
                  "lastName":  "Smith",
                  "email":     "john.smith@example.com",
                  "password":  "weak"
                }
                """;
        ResponseEntity<Map> response =
                restTemplate.postForEntity(REGISTER, json(body), Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @Order(4)
    @DisplayName("POST /register: invalid email format returns 400")
    void register_invalidEmail_returns400() {
        String body = """
                {
                  "firstName": "John",
                  "lastName":  "Smith",
                  "email":     "not-an-email",
                  "password":  "Test@1234"
                }
                """;
        ResponseEntity<Map> response =
                restTemplate.postForEntity(REGISTER, json(body), Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    // ── Login ─────────────────────────────────────────────────────────────────

    @Test
    @Order(5)
    @DisplayName("POST /login: correct credentials return 200 + JWT")
    void login_correctCredentials_returns200AndToken() {
        String body = """
                {
                  "email":    "jane.doe@example.com",
                  "password": "Test@1234"
                }
                """;
        ResponseEntity<AuthResponse> response =
                restTemplate.postForEntity(LOGIN, json(body), AuthResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getToken()).isNotBlank();
        assertThat(response.getBody().getEmail()).isEqualTo("jane.doe@example.com");
    }

    @Test
    @Order(6)
    @DisplayName("POST /login: wrong password returns 401")
    void login_wrongPassword_returns401() {
        String body = """
                {
                  "email":    "jane.doe@example.com",
                  "password": "WrongPass@99"
                }
                """;
        ResponseEntity<Map> response =
                restTemplate.postForEntity(LOGIN, json(body), Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @Order(7)
    @DisplayName("POST /login: unknown email returns 401")
    void login_unknownEmail_returns401() {
        String body = """
                {
                  "email":    "ghost@example.com",
                  "password": "Test@1234"
                }
                """;
        ResponseEntity<Map> response =
                restTemplate.postForEntity(LOGIN, json(body), Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    // ── Protected endpoint with JWT ───────────────────────────────────────────

    @Test
    @Order(8)
    @DisplayName("GET /users/me: valid JWT returns user profile")
    void getProfile_withValidJwt_returnsProfile() {
        assertThat(jwtToken).isNotNull();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtToken);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<Map> response =
                restTemplate.exchange("/api/v1/users/me", HttpMethod.GET, request, Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().get("email")).isEqualTo("jane.doe@example.com");
        assertThat(response.getBody().get("role")).isEqualTo("USER");
    }

    @Test
    @Order(9)
    @DisplayName("GET /users/me: no token returns 401 or 403")
    void getProfile_withoutJwt_returnsUnauthorized() {
        ResponseEntity<String> response =
                restTemplate.getForEntity("/api/v1/users/me", String.class);

        assertThat(response.getStatusCode().value()).isIn(401, 403);
    }

    @Test
    @Order(10)
    @DisplayName("POST /api/v1/quantities/compare: valid JWT returns 200")
    void quantityEndpoint_withValidJwt_returns200() {
        assertThat(jwtToken).isNotNull();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        String body = """
                {
                  "thisQuantityDTO": { "value": 1.0,  "unit": "FEET",   "measurementType": "LengthUnit" },
                  "thatQuantityDTO": { "value": 12.0, "unit": "INCHES", "measurementType": "LengthUnit" }
                }
                """;

        ResponseEntity<Map> response = restTemplate.exchange(
                "/api/v1/quantities/compare",
                HttpMethod.POST,
                new HttpEntity<>(body, headers),
                Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @Order(11)
    @DisplayName("POST /api/v1/quantities/compare: no JWT still returns 200 (public endpoint)")
    void quantityEndpoint_withoutJwt_returns200() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String body = """
                {
                  "thisQuantityDTO": { "value": 1.0, "unit": "FEET",   "measurementType": "LengthUnit" },
                  "thatQuantityDTO": { "value": 1.0, "unit": "INCHES", "measurementType": "LengthUnit" }
                }
                """;

        ResponseEntity<Map> response = restTemplate.exchange(
                "/api/v1/quantities/compare",
                HttpMethod.POST,
                new HttpEntity<>(body, headers),
                Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @Order(12)
    @DisplayName("GET /api/v1/quantities/history/operation/COMPARE: no JWT returns 401")
    void historyEndpoint_withoutJwt_returnsUnauthorized() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/api/v1/quantities/history/operation/COMPARE", String.class);

        assertThat(response.getStatusCode().value()).isIn(401, 403);
    }
}