package com.app.quantitymeasurementapp.controller;

import com.app.quantitymeasurementapp.exception.QuantityMeasurementException;
import com.app.quantitymeasurementapp.model.QuantityMeasurementDTO;
import com.app.quantitymeasurementapp.security.JwtAuthFilter;
import com.app.quantitymeasurementapp.security.JwtUtil;
import com.app.quantitymeasurementapp.security.OAuth2SuccessHandler;
import com.app.quantitymeasurementapp.service.IQuantityMeasurementService;
import com.app.quantitymeasurementapp.user.UserRepository;
import com.app.quantitymeasurementapp.util.SecurityConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(QuantityMeasurementController.class)
@Import(SecurityConfig.class)
@DisplayName("QuantityMeasurementController MockMvc tests")
class QuantityMeasurementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // ── Service mock (the real target of these tests) ──────────────────────────
    @MockBean
    private IQuantityMeasurementService service;

    // ── Security infrastructure mocks (needed by SecurityConfig) ──────────────

    // @SpyBean — NOT @MockBean — is required here.
    //
    // @MockBean replaces JwtAuthFilter with a pure Mockito mock. Mockito mocks
    // ALL methods including the inherited doFilter() from OncePerRequestFilter.
    // A mocked void method does nothing, so doFilter() never calls
    // chain.doFilter(). Spring Security's FilterChainProxy relies on each
    // security filter propagating the chain; when JwtAuthFilter silently
    // swallows the call the DispatcherServlet is never reached. MockMvc gets
    // back the default MockHttpServletResponse: status 200, empty body.
    // status().isOk() passes by accident, but jsonPath assertions fail with
    // "json can not be null or empty" because there is no response body at all.
    //
    // @SpyBean wraps the real JwtAuthFilter instance. The real doFilterInternal
    // runs, finds no Authorization header, skips the JWT block, and calls
    // chain.doFilter() — the filter chain propagates normally to the controller.
    @SpyBean
    private JwtAuthFilter jwtAuthFilter;

    @MockBean
    private JwtUtil jwtUtil;                       // injected into the JwtAuthFilter spy

    @MockBean
    private OAuth2SuccessHandler oAuth2SuccessHandler; // SecurityConfig.filterChain() uses it

    @MockBean
    private UserDetailsService userDetailsService;  // DaoAuthenticationProvider depends on it

    // ── NEW: needed by QuantityMeasurementController.resolveUserId() ──────────
    // The controller autowires UserRepository to look up the User by email
    // and extract their id. Without this mock the application context fails
    // to start during the @WebMvcTest slice.
    @MockBean
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String BASE_URL = "/api/v1/quantities";

    private static final String COMPARE_BODY = """
            {
              "thisQuantityDTO": { "value": 1.0, "unit": "FEET",   "measurementType": "LengthUnit" },
              "thatQuantityDTO": { "value": 12.0,"unit": "INCHES", "measurementType": "LengthUnit" }
            }
            """;

    private static final String ADD_BODY = """
            {
              "thisQuantityDTO": { "value": 1.0, "unit": "FEET",   "measurementType": "LengthUnit" },
              "thatQuantityDTO": { "value": 12.0,"unit": "INCHES", "measurementType": "LengthUnit" }
            }
            """;

    // ── Helper: build a COMPARE result DTO ────────────────────────────────────

    private QuantityMeasurementDTO compareResult() {
        QuantityMeasurementDTO dto = new QuantityMeasurementDTO();
        dto.setThisValue(1.0);
        dto.setThisUnit("FEET");
        dto.setThisMeasurementType("LengthUnit");
        dto.setThatValue(12.0);
        dto.setThatUnit("INCHES");
        dto.setThatMeasurementType("LengthUnit");
        dto.setOperation("COMPARE");
        dto.setResultString("true");
        dto.setError(false);
        return dto;
    }

    // ── Helper: build an ADD result DTO ───────────────────────────────────────

    private QuantityMeasurementDTO addResult() {
        QuantityMeasurementDTO dto = new QuantityMeasurementDTO();
        dto.setThisValue(1.0);
        dto.setThisUnit("FEET");
        dto.setThisMeasurementType("LengthUnit");
        dto.setThatValue(12.0);
        dto.setThatUnit("INCHES");
        dto.setThatMeasurementType("LengthUnit");
        dto.setOperation("ADD");
        dto.setResultValue(2.0);
        dto.setResultUnit("FEET");
        dto.setResultMeasurementType("LengthUnit");
        dto.setError(false);
        return dto;
    }

    // ── POST /compare ─────────────────────────────────────────────────────────

    @Test
    @WithMockUser
    @DisplayName("POST /compare with valid input returns 200 and result")
    void postCompare_validInput_returns200() throws Exception {
        // Service now takes (QuantityDTO, QuantityDTO, Long userId)
        // Use any() for all three — userId is resolved internally and irrelevant here
        when(service.compare(any(), any(), any())).thenReturn(compareResult());

        mockMvc.perform(post(BASE_URL + "/compare")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(COMPARE_BODY))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.operation", is("COMPARE")))
               .andExpect(jsonPath("$.resultString", is("true")))
               .andExpect(jsonPath("$.error", is(false)));
    }

    @Test
    @WithMockUser
    @DisplayName("POST /compare with missing unit returns 400 validation error")
    void postCompare_missingUnit_returns400() throws Exception {
        String missingUnitBody = """
                {
                  "thisQuantityDTO": { "value": 1.0, "unit": "", "measurementType": "LengthUnit" },
                  "thatQuantityDTO": { "value": 12.0,"unit": "INCHES", "measurementType": "LengthUnit" }
                }
                """;
        mockMvc.perform(post(BASE_URL + "/compare")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(missingUnitBody))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.status", is(400)));
    }

    @Test
    @WithMockUser
    @DisplayName("POST /compare with null value returns 400 validation error")
    void postCompare_nullValue_returns400() throws Exception {
        String nullValueBody = """
                {
                  "thisQuantityDTO": { "unit": "FEET", "measurementType": "LengthUnit" },
                  "thatQuantityDTO": { "value": 12.0,"unit": "INCHES", "measurementType": "LengthUnit" }
                }
                """;
        mockMvc.perform(post(BASE_URL + "/compare")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(nullValueBody))
               .andExpect(status().isBadRequest());
    }

    @Test
    @WithAnonymousUser
    @DisplayName("POST /compare without authentication returns 200 (public endpoint)")
    void postCompare_noAuth_returns200() throws Exception {
        when(service.compare(any(), any(), any())).thenReturn(compareResult());

        mockMvc.perform(post(BASE_URL + "/compare")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(COMPARE_BODY))
               .andExpect(status().isOk());
    }

    // ── POST /add ─────────────────────────────────────────────────────────────

    @Test
    @WithMockUser
    @DisplayName("POST /add with valid input returns 200 and result")
    void postAdd_validInput_returns200() throws Exception {
        when(service.add(any(), any(), any())).thenReturn(addResult());

        mockMvc.perform(post(BASE_URL + "/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ADD_BODY))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.operation", is("ADD")))
               .andExpect(jsonPath("$.resultValue", is(2.0)))
               .andExpect(jsonPath("$.resultUnit", is("FEET")));
    }

    @Test
    @WithMockUser
    @DisplayName("POST /add when service throws exception returns 400")
    void postAdd_serviceThrowsException_returns400() throws Exception {
        when(service.add(any(), any(), any()))
                .thenThrow(new QuantityMeasurementException("Cannot ADD different measurement categories"));

        mockMvc.perform(post(BASE_URL + "/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ADD_BODY))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.error", is("Quantity Measurement Error")))
               .andExpect(jsonPath("$.message", containsString("Cannot ADD")));
    }

    // ── POST /subtract ────────────────────────────────────────────────────────

    @Test
    @WithMockUser
    @DisplayName("POST /subtract with valid input returns 200")
    void postSubtract_validInput_returns200() throws Exception {
        QuantityMeasurementDTO dto = new QuantityMeasurementDTO();
        dto.setOperation("SUBTRACT");
        dto.setResultValue(12.0);
        dto.setResultUnit("INCHES");
        when(service.subtract(any(), any(), any())).thenReturn(dto);

        String body = """
                {
                  "thisQuantityDTO": { "value": 24.0, "unit": "INCHES", "measurementType": "LengthUnit" },
                  "thatQuantityDTO": { "value": 1.0,  "unit": "FEET",   "measurementType": "LengthUnit" }
                }
                """;
        mockMvc.perform(post(BASE_URL + "/subtract")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.resultValue", is(12.0)));
    }

    // ── POST /divide ──────────────────────────────────────────────────────────

    @Test
    @WithMockUser
    @DisplayName("POST /divide divide-by-zero returns 400")
    void postDivide_byZero_returns400() throws Exception {
        when(service.divide(any(), any(), any()))
                .thenThrow(new QuantityMeasurementException("DIVIDE failed: Divide by zero"));

        String body = """
                {
                  "thisQuantityDTO": { "value": 1.0, "unit": "FEET",   "measurementType": "LengthUnit" },
                  "thatQuantityDTO": { "value": 0.0, "unit": "INCHES", "measurementType": "LengthUnit" }
                }
                """;
        mockMvc.perform(post(BASE_URL + "/divide")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
               .andExpect(status().isBadRequest());
    }

    // ── GET /history ──────────────────────────────────────────────────────────

    @Test
    @WithMockUser
    @DisplayName("GET /history/operation/COMPARE returns 200 and list")
    void getHistoryByOperation_returns200() throws Exception {
        // History methods now take (String operation, Long userId)
        when(service.getHistoryByOperation(any(), any())).thenReturn(List.of(compareResult()));

        mockMvc.perform(get(BASE_URL + "/history/operation/COMPARE"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$", hasSize(1)))
               .andExpect(jsonPath("$[0].operation", is("COMPARE")));
    }

    @Test
    @WithMockUser
    @DisplayName("GET /history/type/LengthUnit returns 200 and list")
    void getHistoryByType_returns200() throws Exception {
        when(service.getHistoryByMeasurementType(any(), any())).thenReturn(List.of(compareResult()));

        mockMvc.perform(get(BASE_URL + "/history/type/LengthUnit"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    @WithMockUser
    @DisplayName("GET /count/ADD returns 200 with count")
    void countByOperation_returns200() throws Exception {
        when(service.getOperationCount(any(), any())).thenReturn(5L);

        mockMvc.perform(get(BASE_URL + "/count/ADD"))
               .andExpect(status().isOk())
               .andExpect(content().string("5"));
    }

    @Test
    @WithMockUser
    @DisplayName("GET /history/errored returns 200 and error list")
    void getErrorHistory_returns200() throws Exception {
        QuantityMeasurementDTO errDto = new QuantityMeasurementDTO();
        errDto.setError(true);
        errDto.setErrorMessage("incompatible types");
        when(service.getErrorHistory(any())).thenReturn(List.of(errDto));

        mockMvc.perform(get(BASE_URL + "/history/errored"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$[0].error", is(true)))
               .andExpect(jsonPath("$[0].errorMessage", is("incompatible types")));
    }
}