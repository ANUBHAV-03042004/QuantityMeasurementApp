package com.app.quantitymeasurementapp;

import com.app.quantitymeasurementapp.auth.AuthResponse;
import com.app.quantitymeasurementapp.model.QuantityMeasurementDTO;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Full-stack integration tests.
 * Order(1) registers a user and stores the JWT.
 * All subsequent quantity requests carry that JWT.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")   // loads application-test.properties (supplies dummy OAuth2 creds)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("QuantityMeasurement integration tests (full Spring Boot stack)")
class QuantityMeasurementApplicationTests {

    @Autowired
    private TestRestTemplate restTemplate;

    private static final String BASE     = "/api/v1/quantities";
    private static final String REGISTER = "/api/v1/auth/register";

    private static String jwtToken;

    // ── Context ───────────────────────────────────────────────────────────────

    @Test
    @Order(1)
    @DisplayName("Bootstrap: register test user and obtain JWT")
    void bootstrapJwt() {
        String body = """
                {
                  "firstName": "Test",
                  "lastName":  "User",
                  "email":     "testuser@quantity.com",
                  "password":  "Test@1234"
                }
                """;
        ResponseEntity<AuthResponse> resp =
                restTemplate.postForEntity(REGISTER, json(body), AuthResponse.class);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        jwtToken = resp.getBody().getToken();
        assertThat(jwtToken).isNotBlank();
    }

    @Test
    @Order(2)
    @DisplayName("Spring application context loads without errors")
    void contextLoads() {}

    // ── COMPARE ───────────────────────────────────────────────────────────────

    @Test
    @Order(3)
    @DisplayName("POST /compare: 1 FEET equals 12 INCHES returns true")
    void compare_1Feet_12Inches_returnsTrue() {
        String body = """
                {
                  "thisQuantityDTO": { "value": 1.0,  "unit": "FEET",   "measurementType": "LengthUnit" },
                  "thatQuantityDTO": { "value": 12.0, "unit": "INCHES", "measurementType": "LengthUnit" }
                }
                """;
        ResponseEntity<QuantityMeasurementDTO> response =
                restTemplate.exchange(BASE + "/compare", HttpMethod.POST, authJson(body), QuantityMeasurementDTO.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getResultString()).isEqualTo("true");
        assertThat(response.getBody().isError()).isFalse();
    }

    @Test
    @Order(4)
    @DisplayName("POST /compare: 1 FEET does not equal 1 INCHES returns false")
    void compare_1Feet_1Inch_returnsFalse() {
        String body = """
                {
                  "thisQuantityDTO": { "value": 1.0, "unit": "FEET",   "measurementType": "LengthUnit" },
                  "thatQuantityDTO": { "value": 1.0, "unit": "INCHES", "measurementType": "LengthUnit" }
                }
                """;
        ResponseEntity<QuantityMeasurementDTO> response =
                restTemplate.exchange(BASE + "/compare", HttpMethod.POST, authJson(body), QuantityMeasurementDTO.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getResultString()).isEqualTo("false");
    }

    // ── CONVERT ───────────────────────────────────────────────────────────────

    @Test
    @Order(5)
    @DisplayName("POST /convert: 1 FEET converts to 12 INCHES")
    void convert_1Feet_toInches_returns12() {
        String body = """
                {
                  "thisQuantityDTO": { "value": 1.0, "unit": "FEET",   "measurementType": "LengthUnit" },
                  "thatQuantityDTO": { "value": 0.0, "unit": "INCHES", "measurementType": "LengthUnit" }
                }
                """;
        ResponseEntity<QuantityMeasurementDTO> response =
                restTemplate.exchange(BASE + "/convert", HttpMethod.POST, authJson(body), QuantityMeasurementDTO.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getResultValue()).isEqualTo(12.0);
        assertThat(response.getBody().getResultUnit()).isEqualTo("INCHES");
        assertThat(response.getBody().getOperation()).isEqualTo("CONVERT");
    }

    @Test
    @Order(6)
    @DisplayName("POST /convert: 0 CELSIUS to FAHRENHEIT equals 32")
    void convert_0Celsius_to_32Fahrenheit() {
        String body = """
                {
                  "thisQuantityDTO": { "value": 0.0, "unit": "CELSIUS",    "measurementType": "TemperatureUnit" },
                  "thatQuantityDTO": { "value": 0.0, "unit": "FAHRENHEIT", "measurementType": "TemperatureUnit" }
                }
                """;
        ResponseEntity<QuantityMeasurementDTO> response =
                restTemplate.exchange(BASE + "/convert", HttpMethod.POST, authJson(body), QuantityMeasurementDTO.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getResultValue()).isEqualTo(32.0);
    }

    // ── ADD ───────────────────────────────────────────────────────────────────

    @Test
    @Order(7)
    @DisplayName("POST /add: 1 FEET + 12 INCHES = 2 FEET")
    void add_1Feet_12Inches_returns2Feet() {
        String body = """
                {
                  "thisQuantityDTO": { "value": 1.0,  "unit": "FEET",   "measurementType": "LengthUnit" },
                  "thatQuantityDTO": { "value": 12.0, "unit": "INCHES", "measurementType": "LengthUnit" }
                }
                """;
        ResponseEntity<QuantityMeasurementDTO> response =
                restTemplate.exchange(BASE + "/add", HttpMethod.POST, authJson(body), QuantityMeasurementDTO.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getResultValue()).isEqualTo(2.0);
        assertThat(response.getBody().getResultUnit()).isEqualTo("FEET");
        assertThat(response.getBody().getOperation()).isEqualTo("ADD");
    }

    // ── SUBTRACT ──────────────────────────────────────────────────────────────

    @Test
    @Order(8)
    @DisplayName("POST /subtract: 24 INCHES - 1 FEET = 12 INCHES")
    void subtract_24Inches_1Foot_returns12Inches() {
        String body = """
                {
                  "thisQuantityDTO": { "value": 24.0, "unit": "INCHES", "measurementType": "LengthUnit" },
                  "thatQuantityDTO": { "value": 1.0,  "unit": "FEET",   "measurementType": "LengthUnit" }
                }
                """;
        ResponseEntity<QuantityMeasurementDTO> response =
                restTemplate.exchange(BASE + "/subtract", HttpMethod.POST, authJson(body), QuantityMeasurementDTO.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getResultValue()).isEqualTo(12.0);
        assertThat(response.getBody().getResultUnit()).isEqualTo("INCHES");
    }

    // ── DIVIDE ────────────────────────────────────────────────────────────────

    @Test
    @Order(9)
    @DisplayName("POST /divide: 24 INCHES / 12 INCHES = 2.0")
    void divide_24Inches_by_12Inches_returns2() {
        String body = """
                {
                  "thisQuantityDTO": { "value": 24.0, "unit": "INCHES", "measurementType": "LengthUnit" },
                  "thatQuantityDTO": { "value": 12.0, "unit": "INCHES", "measurementType": "LengthUnit" }
                }
                """;
        ResponseEntity<QuantityMeasurementDTO> response =
                restTemplate.exchange(BASE + "/divide", HttpMethod.POST, authJson(body), QuantityMeasurementDTO.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getResultValue()).isEqualTo(2.0);
    }

    // ── Error scenarios ───────────────────────────────────────────────────────

    @Test
    @Order(10)
    @DisplayName("POST /add: incompatible types returns 400")
    void add_incompatibleTypes_returns400() {
        String body = """
                {
                  "thisQuantityDTO": { "value": 1.0, "unit": "FEET",     "measurementType": "LengthUnit" },
                  "thatQuantityDTO": { "value": 1.0, "unit": "KILOGRAM", "measurementType": "WeightUnit" }
                }
                """;
        ResponseEntity<Map> response =
                restTemplate.exchange(BASE + "/add", HttpMethod.POST, authJson(body), Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().get("error")).isEqualTo("Quantity Measurement Error");
    }

    @Test
    @Order(11)
    @DisplayName("POST /compare: invalid unit returns 400")
    void compare_invalidUnit_returns400() {
        String body = """
                {
                  "thisQuantityDTO": { "value": 1.0, "unit": "FOOT",   "measurementType": "LengthUnit" },
                  "thatQuantityDTO": { "value": 1.0, "unit": "INCHES", "measurementType": "LengthUnit" }
                }
                """;
        ResponseEntity<Map> response =
                restTemplate.exchange(BASE + "/compare", HttpMethod.POST, authJson(body), Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @Order(12)
    @DisplayName("POST /add: temperature arithmetic returns 400")
    void add_temperature_returns400() {
        String body = """
                {
                  "thisQuantityDTO": { "value": 100.0, "unit": "CELSIUS", "measurementType": "TemperatureUnit" },
                  "thatQuantityDTO": { "value": 0.0,   "unit": "CELSIUS", "measurementType": "TemperatureUnit" }
                }
                """;
        ResponseEntity<Map> response =
                restTemplate.exchange(BASE + "/add", HttpMethod.POST, authJson(body), Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().get("message").toString()).contains("Temperature does not support ADD");
    }

    // ── History endpoints ─────────────────────────────────────────────────────

    @Test
    @Order(13)
    @DisplayName("GET /history/operation/COMPARE returns previously saved records")
    void historyByOperation_afterCompare_returnsRecord() {
        ResponseEntity<QuantityMeasurementDTO[]> response =
                restTemplate.exchange(BASE + "/history/operation/COMPARE",
                        HttpMethod.GET, authEmpty(), QuantityMeasurementDTO[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotEmpty();
        assertThat(response.getBody()[0].getOperation()).isEqualTo("COMPARE");
    }

    @Test
    @Order(14)
    @DisplayName("GET /history/errored returns error records after failed operations")
    void historyErrored_returnsErrorRecords() {
        ResponseEntity<QuantityMeasurementDTO[]> response =
                restTemplate.exchange(BASE + "/history/errored",
                        HttpMethod.GET, authEmpty(), QuantityMeasurementDTO[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotEmpty();
        for (QuantityMeasurementDTO dto : response.getBody()) {
            assertThat(dto.isError()).isTrue();
        }
    }

    @Test
    @Order(15)
    @DisplayName("GET /count/ADD returns positive count after add operations")
    void countByOperation_afterAdd_returnsPositive() {
        ResponseEntity<Long> response =
                restTemplate.exchange(BASE + "/count/ADD",
                        HttpMethod.GET, authEmpty(), Long.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isGreaterThan(0L);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private HttpEntity<String> json(String body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(body, headers);
    }

    private HttpEntity<String> authJson(String body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(jwtToken);
        return new HttpEntity<>(body, headers);
    }

    private HttpEntity<Void> authEmpty() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtToken);
        return new HttpEntity<>(headers);
    }
}