package com.app.quantitymeasurementapp;

import com.app.quantitymeasurementapp.auth.AuthResponse;
import com.app.quantitymeasurementapp.auth.LoginRequest;
import com.app.quantitymeasurementapp.auth.RegisterRequest;
import com.app.quantitymeasurementapp.model.QuantityMeasurementDTO;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Full-stack integration tests.
 *
 * Order:
 *  1  Context loads
 *  2  Register a new user → receive JWT
 *  3  Login  with same user → receive JWT
 *  4+ Use JWT on all /api/v1/quantities/** endpoints
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("QuantityMeasurement integration tests (full Spring Boot stack)")
class QuantityMeasurementApplicationTests {

    @Autowired
    private TestRestTemplate restTemplate;

    private static final String BASE      = "/api/v1/quantities";
    private static final String AUTH_BASE = "/api/auth";

    /** Shared across ordered tests via static field */
    private static String jwtToken;

    // ── Helper: build auth header ─────────────────────────────────────────────

    private HttpHeaders authHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (jwtToken != null) {
            headers.setBearerAuth(jwtToken);
        }
        return headers;
    }

    private HttpEntity<String> authBody(String json) {
        return new HttpEntity<>(json, authHeaders());
    }

    private HttpEntity<String> jsonBody(String json) {
        HttpHeaders h = new HttpHeaders();
        h.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(json, h);
    }

    // ── Order 1: Context ──────────────────────────────────────────────────────

    @Test @Order(1)
    @DisplayName("Spring application context loads without errors")
    void contextLoads() {}

    // ── Order 2: Register ─────────────────────────────────────────────────────

    @Test @Order(2)
    @DisplayName("POST /auth/register: creates user and returns JWT")
    void register_returns201_and_token() {
        String body = """
                {
                  "firstName": "Test",
                  "lastName":  "User",
                  "email":     "testuser@qmapp.com",
                  "password":  "Passw0rd@Test1"
                }
                """;
        ResponseEntity<AuthResponse> resp =
                restTemplate.postForEntity(AUTH_BASE + "/register", jsonBody(body), AuthResponse.class);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(resp.getBody()).isNotNull();
        assertThat(resp.getBody().getToken()).isNotBlank();
        assertThat(resp.getBody().getTokenType()).isEqualTo("Bearer");

        jwtToken = resp.getBody().getToken();  // store for subsequent tests
    }

    // ── Order 3: Login ────────────────────────────────────────────────────────

    @Test @Order(3)
    @DisplayName("POST /auth/login: existing user receives new JWT")
    void login_returns200_and_token() {
        String body = """
                {
                  "email":    "testuser@qmapp.com",
                  "password": "Passw0rd@Test1"
                }
                """;
        ResponseEntity<AuthResponse> resp =
                restTemplate.postForEntity(AUTH_BASE + "/login", jsonBody(body), AuthResponse.class);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resp.getBody().getToken()).isNotBlank();

        jwtToken = resp.getBody().getToken();  // refresh token
    }

    @Test @Order(4)
    @DisplayName("POST /auth/login: wrong password returns 401")
    void login_wrongPassword_returns401() {
        String body = """
                {
                  "email":    "testuser@qmapp.com",
                  "password": "WrongPassword1!"
                }
                """;
        ResponseEntity<Map> resp =
                restTemplate.postForEntity(AUTH_BASE + "/login", jsonBody(body), Map.class);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    // ── Order 5: unauthenticated call returns 401 ─────────────────────────────

    @Test @Order(5)
    @DisplayName("POST /compare: without token returns 401")
    void compare_withoutToken_returns401() {
        String body = """
                {
                  "thisQuantityDTO": { "value": 1.0, "unit": "FEET",   "measurementType": "LengthUnit" },
                  "thatQuantityDTO": { "value": 12.0,"unit": "INCHES", "measurementType": "LengthUnit" }
                }
                """;
        ResponseEntity<Map> resp =
                restTemplate.postForEntity(BASE + "/compare", jsonBody(body), Map.class);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    // ── Order 6-14: authenticated operations ──────────────────────────────────

    @Test @Order(6)
    @DisplayName("POST /compare: 1 FEET equals 12 INCHES returns true")
    void compare_1Feet_12Inches_returnsTrue() {
        String body = """
                {
                  "thisQuantityDTO": { "value": 1.0,  "unit": "FEET",   "measurementType": "LengthUnit" },
                  "thatQuantityDTO": { "value": 12.0, "unit": "INCHES", "measurementType": "LengthUnit" }
                }
                """;
        ResponseEntity<QuantityMeasurementDTO> resp =
                restTemplate.exchange(BASE + "/compare", HttpMethod.POST,
                        authBody(body), QuantityMeasurementDTO.class);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resp.getBody().getResultString()).isEqualTo("true");
        assertThat(resp.getBody().isError()).isFalse();
    }

    @Test @Order(7)
    @DisplayName("POST /convert: 1 FEET converts to 12 INCHES")
    void convert_1Feet_toInches_returns12() {
        String body = """
                {
                  "thisQuantityDTO": { "value": 1.0, "unit": "FEET",   "measurementType": "LengthUnit" },
                  "thatQuantityDTO": { "value": 0.0, "unit": "INCHES", "measurementType": "LengthUnit" }
                }
                """;
        ResponseEntity<QuantityMeasurementDTO> resp =
                restTemplate.exchange(BASE + "/convert", HttpMethod.POST,
                        authBody(body), QuantityMeasurementDTO.class);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resp.getBody().getResultValue()).isEqualTo(12.0);
        assertThat(resp.getBody().getResultUnit()).isEqualTo("INCHES");
    }

    @Test @Order(8)
    @DisplayName("POST /convert: 0 CELSIUS to FAHRENHEIT equals 32")
    void convert_0Celsius_to_32Fahrenheit() {
        String body = """
                {
                  "thisQuantityDTO": { "value": 0.0, "unit": "CELSIUS",    "measurementType": "TemperatureUnit" },
                  "thatQuantityDTO": { "value": 0.0, "unit": "FAHRENHEIT", "measurementType": "TemperatureUnit" }
                }
                """;
        ResponseEntity<QuantityMeasurementDTO> resp =
                restTemplate.exchange(BASE + "/convert", HttpMethod.POST,
                        authBody(body), QuantityMeasurementDTO.class);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resp.getBody().getResultValue()).isEqualTo(32.0);
    }

    @Test @Order(9)
    @DisplayName("POST /add: 1 FEET + 12 INCHES = 2 FEET")
    void add_1Feet_12Inches_returns2Feet() {
        String body = """
                {
                  "thisQuantityDTO": { "value": 1.0,  "unit": "FEET",   "measurementType": "LengthUnit" },
                  "thatQuantityDTO": { "value": 12.0, "unit": "INCHES", "measurementType": "LengthUnit" }
                }
                """;
        ResponseEntity<QuantityMeasurementDTO> resp =
                restTemplate.exchange(BASE + "/add", HttpMethod.POST,
                        authBody(body), QuantityMeasurementDTO.class);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resp.getBody().getResultValue()).isEqualTo(2.0);
        assertThat(resp.getBody().getResultUnit()).isEqualTo("FEET");
    }

    @Test @Order(10)
    @DisplayName("POST /subtract: 24 INCHES - 1 FEET = 12 INCHES")
    void subtract_24Inches_1Foot_returns12Inches() {
        String body = """
                {
                  "thisQuantityDTO": { "value": 24.0, "unit": "INCHES", "measurementType": "LengthUnit" },
                  "thatQuantityDTO": { "value": 1.0,  "unit": "FEET",   "measurementType": "LengthUnit" }
                }
                """;
        ResponseEntity<QuantityMeasurementDTO> resp =
                restTemplate.exchange(BASE + "/subtract", HttpMethod.POST,
                        authBody(body), QuantityMeasurementDTO.class);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resp.getBody().getResultValue()).isEqualTo(12.0);
    }

    @Test @Order(11)
    @DisplayName("POST /divide: 24 INCHES / 12 INCHES = 2.0")
    void divide_24Inches_by_12Inches_returns2() {
        String body = """
                {
                  "thisQuantityDTO": { "value": 24.0, "unit": "INCHES", "measurementType": "LengthUnit" },
                  "thatQuantityDTO": { "value": 12.0, "unit": "INCHES", "measurementType": "LengthUnit" }
                }
                """;
        ResponseEntity<QuantityMeasurementDTO> resp =
                restTemplate.exchange(BASE + "/divide", HttpMethod.POST,
                        authBody(body), QuantityMeasurementDTO.class);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resp.getBody().getResultValue()).isEqualTo(2.0);
    }

    @Test @Order(12)
    @DisplayName("POST /add: incompatible types returns 400")
    void add_incompatibleTypes_returns400() {
        String body = """
                {
                  "thisQuantityDTO": { "value": 1.0, "unit": "FEET",     "measurementType": "LengthUnit" },
                  "thatQuantityDTO": { "value": 1.0, "unit": "KILOGRAM", "measurementType": "WeightUnit" }
                }
                """;
        ResponseEntity<Map> resp =
                restTemplate.exchange(BASE + "/add", HttpMethod.POST, authBody(body), Map.class);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(resp.getBody().get("error")).isEqualTo("Quantity Measurement Error");
    }

    @Test @Order(13)
    @DisplayName("POST /add: temperature arithmetic returns 400")
    void add_temperature_returns400() {
        String body = """
                {
                  "thisQuantityDTO": { "value": 100.0, "unit": "CELSIUS", "measurementType": "TemperatureUnit" },
                  "thatQuantityDTO": { "value": 0.0,   "unit": "CELSIUS", "measurementType": "TemperatureUnit" }
                }
                """;
        ResponseEntity<Map> resp =
                restTemplate.exchange(BASE + "/add", HttpMethod.POST, authBody(body), Map.class);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(resp.getBody().get("message").toString()).contains("Temperature does not support ADD");
    }

    @Test @Order(14)
    @DisplayName("GET /history/operation/COMPARE returns previously saved records")
    void historyByOperation_returnsRecord() {
        ResponseEntity<QuantityMeasurementDTO[]> resp =
                restTemplate.exchange(BASE + "/history/operation/COMPARE",
                        HttpMethod.GET, new HttpEntity<>(authHeaders()),
                        QuantityMeasurementDTO[].class);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resp.getBody()).isNotEmpty();
        assertThat(resp.getBody()[0].getOperation()).isEqualTo("COMPARE");
    }

    @Test @Order(15)
    @DisplayName("GET /history/errored returns error records after failed operations")
    void historyErrored_returnsErrorRecords() {
        ResponseEntity<QuantityMeasurementDTO[]> resp =
                restTemplate.exchange(BASE + "/history/errored",
                        HttpMethod.GET, new HttpEntity<>(authHeaders()),
                        QuantityMeasurementDTO[].class);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resp.getBody()).isNotEmpty();
        for (QuantityMeasurementDTO dto : resp.getBody()) {
            assertThat(dto.isError()).isTrue();
        }
    }

    @Test @Order(16)
    @DisplayName("GET /count/ADD returns positive count after add operations")
    void countByOperation_returnsPositive() {
        ResponseEntity<Long> resp =
                restTemplate.exchange(BASE + "/count/ADD",
                        HttpMethod.GET, new HttpEntity<>(authHeaders()), Long.class);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resp.getBody()).isGreaterThan(0L);
    }
}
