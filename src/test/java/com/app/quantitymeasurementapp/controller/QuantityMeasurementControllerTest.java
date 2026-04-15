package com.app.quantitymeasurementapp.controller;

import com.app.quantitymeasurementapp.exception.QuantityMeasurementException;
import com.app.quantitymeasurementapp.model.QuantityMeasurementDTO;
import com.app.quantitymeasurementapp.security.JwtAuthFilter;
import com.app.quantitymeasurementapp.security.JwtUtil;
import com.app.quantitymeasurementapp.security.OAuth2SuccessHandler;
import com.app.quantitymeasurementapp.service.IQuantityMeasurementService;
import com.app.quantitymeasurementapp.user.User;
import com.app.quantitymeasurementapp.user.UserRepository;
import com.app.quantitymeasurementapp.util.SecurityConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
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
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * MockMvc slice test for QuantityMeasurementController.
 *
 * Architecture:
 *  - Operations (compare/add/subtract/divide/convert) → PUBLIC, service takes (q1, q2, userId)
 *  - History endpoints → AUTHENTICATED, service takes (operation/type, userId)
 *  - Controller resolves userId via userRepository.findByEmail(principal.getUsername())
 *  - If userId == null → controller returns 401 with empty body
 *
 * Critical stub rules:
 *  - Use anyString()/anyLong() for history stubs, not exact values, so stubs
 *    fire regardless of minor arg differences (avoids NPE → empty body → json error)
 *  - UserRepository must return a non-empty Optional so resolveUserId() != null
 *  - Service history methods must return a non-null List (Mockito default is null
 *    which causes NPE in fromEntityList().stream())
 */
@WebMvcTest(QuantityMeasurementController.class)
@Import(SecurityConfig.class)
@DisplayName("QuantityMeasurementController MockMvc tests")
class QuantityMeasurementControllerTest {

    @Autowired private MockMvc      mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockBean private IQuantityMeasurementService service;

    // Controller uses UserRepository to resolve userId from principal email
    @MockBean private UserRepository userRepository;

    // SecurityConfig constructor dependencies
    // @SpyBean — NOT @MockBean — is required here.

    @SpyBean
    private JwtAuthFilter jwtAuthFilter;

    @MockBean
    private JwtUtil jwtUtil;                       // injected into the JwtAuthFilter spy

    @MockBean
    private OAuth2SuccessHandler oAuth2SuccessHandler; // SecurityConfig.filterChain() uses it

    @MockBean
    private UserDetailsService userDetailsService;  // DaoAuthenticationProvider depends on it

    // FIX: removed duplicate @MockBean UserRepository and @Autowired ObjectMapper declarations
    // that caused "variable already defined" compilation errors (errors 1 & 2)

    private static final String BASE_URL = "/api/v1/quantities";

    // FIX: added missing MOCK_USER_ID constant (was referenced in @BeforeEach but never declared)
    private static final Long MOCK_USER_ID = 42L;

    private static final String COMPARE_BODY = """
            {
              "thisQuantityDTO": { "value": 1.0,  "unit": "FEET",   "measurementType": "LengthUnit" },
              "thatQuantityDTO": { "value": 12.0, "unit": "INCHES", "measurementType": "LengthUnit" }
            }
            """;

    private static final String ADD_BODY = """
            {
              "thisQuantityDTO": { "value": 1.0,  "unit": "FEET",   "measurementType": "LengthUnit" },
              "thatQuantityDTO": { "value": 12.0, "unit": "INCHES", "measurementType": "LengthUnit" }
            }
            """;

    // ── Global setup ──────────────────────────────────────────────────────────

    @BeforeEach
    void globalSetup() {
        /*
         * @WithMockUser injects a principal with getUsername() == "user".
         * The controller calls userRepository.findByEmail(principal.getUsername()).
         * We stub it for ANY email so every @WithMockUser test resolves a non-null userId
         * without needing per-test setup.
         *
         * lenient() suppresses "unnecessary stubbing" warnings for tests that
         * don't hit history endpoints (operations, guest tests).
         */
        // FIX: User class is now imported; MOCK_USER_ID constant is now declared above
        User mockUser = User.builder()
                .firstName("Test").lastName("User")
                .email("user")                          // matches @WithMockUser default
                .role(User.Role.USER)
                .authProvider(User.AuthProvider.AUTH_LOCAL)
                .build();
        mockUser.setId(MOCK_USER_ID);

        lenient().when(userRepository.findByEmail(anyString()))
                 .thenReturn(Optional.of(mockUser));

        /*
         * Stub ALL history service methods to return an empty list by default.
         * This prevents NPE (null.stream()) when a test hits a history endpoint
         * but only cares about the status code, not the body.
         * Individual tests override these with specific return values as needed.
         */
        lenient().when(service.getHistoryByOperation(anyString(), anyLong()))
                 .thenReturn(List.of());
        lenient().when(service.getHistoryByMeasurementType(anyString(), anyLong()))
                 .thenReturn(List.of());
        lenient().when(service.getErrorHistory(anyLong()))
                 .thenReturn(List.of());
        lenient().when(service.getOperationCount(anyString(), anyLong()))
                 .thenReturn(0L);
    }

    // ── Helper DTOs ───────────────────────────────────────────────────────────

    private QuantityMeasurementDTO compareResult() {
        QuantityMeasurementDTO dto = new QuantityMeasurementDTO();
        dto.setThisValue(1.0);  dto.setThisUnit("FEET");  dto.setThisMeasurementType("LengthUnit");
        dto.setThatValue(12.0); dto.setThatUnit("INCHES"); dto.setThatMeasurementType("LengthUnit");
        dto.setOperation("COMPARE"); dto.setResultString("true"); dto.setError(false);
        return dto;
    }

    private QuantityMeasurementDTO addResult() {
        QuantityMeasurementDTO dto = new QuantityMeasurementDTO();
        dto.setThisValue(1.0);  dto.setThisUnit("FEET");  dto.setThisMeasurementType("LengthUnit");
        dto.setThatValue(12.0); dto.setThatUnit("INCHES"); dto.setThatMeasurementType("LengthUnit");
        dto.setOperation("ADD"); dto.setResultValue(2.0); dto.setResultUnit("FEET");
        dto.setResultMeasurementType("LengthUnit"); dto.setError(false);
        return dto;
    }

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
               .andExpect(jsonPath("$.operation",    is("COMPARE")))
               .andExpect(jsonPath("$.resultString", is("true")))
               .andExpect(jsonPath("$.error",        is(false)));
    }

    @Test
    @WithAnonymousUser
    @DisplayName("POST /compare: guest (no token) — 200, endpoint is public")
    void postCompare_guest_returns200() throws Exception {
        // Guest → principal is null → controller passes null userId → isNull()
        when(service.compare(any(), any(), isNull())).thenReturn(compareResult());

        mockMvc.perform(post(BASE_URL + "/compare")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(COMPARE_BODY))
               .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    @DisplayName("POST /compare: missing unit — 400 validation error")
    void postCompare_missingUnit_returns400() throws Exception {
        String body = """
                {
                  "thisQuantityDTO": { "value": 1.0, "unit": "", "measurementType": "LengthUnit" },
                  "thatQuantityDTO": { "value": 12.0,"unit": "INCHES", "measurementType": "LengthUnit" }
                }
                """;
        mockMvc.perform(post(BASE_URL + "/compare")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.status", is(400)));
    }

    @Test
    @WithMockUser
    @DisplayName("POST /compare: null value — 400 validation error")
    void postCompare_nullValue_returns400() throws Exception {
        String body = """
                {
                  "thisQuantityDTO": { "unit": "FEET", "measurementType": "LengthUnit" },
                  "thatQuantityDTO": { "value": 12.0, "unit": "INCHES", "measurementType": "LengthUnit" }
                }
                """;
        mockMvc.perform(post(BASE_URL + "/compare")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
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

   
    @Test
    @WithMockUser
    @DisplayName("POST /add: valid input — 200")
    void postAdd_validInput_returns200() throws Exception {
        when(service.add(any(), any(), any())).thenReturn(addResult());

        mockMvc.perform(post(BASE_URL + "/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ADD_BODY))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.operation",   is("ADD")))
               .andExpect(jsonPath("$.resultValue", is(2.0)))
               .andExpect(jsonPath("$.resultUnit",  is("FEET")));
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
               .andExpect(jsonPath("$.error",   is("Quantity Measurement Error")))
               .andExpect(jsonPath("$.message", containsString("Cannot ADD")));
    }

    @Test
    @WithMockUser
    @DisplayName("POST /subtract: valid input — 200")
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

    @Test
    @WithMockUser
    @DisplayName("POST /divide: divide-by-zero — 400")
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

 
    @Test
    @WithMockUser
    @DisplayName("GET /history/operation/COMPARE: authenticated user — 200 with their records")
    void getHistoryByOperation_returns200() throws Exception {
        // History methods now take (String operation, Long userId)
        when(service.getHistoryByOperation(any(), any())).thenReturn(List.of(compareResult()));

        mockMvc.perform(get(BASE_URL + "/history/operation/COMPARE"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$",              hasSize(1)))
               .andExpect(jsonPath("$[0].operation", is("COMPARE")));
    }

    @Test
    @WithAnonymousUser
    @DisplayName("GET /history/operation/COMPARE: unauthenticated — 401")
    void getHistoryByOperation_unauthenticated_returns401() throws Exception {
        mockMvc.perform(get(BASE_URL + "/history/operation/COMPARE"))
               .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    @DisplayName("GET /history/type/LengthUnit: authenticated user — 200 with their records")
    void getHistoryByType_returns200() throws Exception {
        when(service.getHistoryByMeasurementType(any(), any())).thenReturn(List.of(compareResult()));

        mockMvc.perform(get(BASE_URL + "/history/type/LengthUnit"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    @WithMockUser
    @DisplayName("GET /count/ADD: authenticated user — 200 with their count")
    void countByOperation_returns200() throws Exception {
        when(service.getOperationCount(any(), any())).thenReturn(5L);

        mockMvc.perform(get(BASE_URL + "/count/ADD"))
               .andExpect(status().isOk())
               .andExpect(content().string("5"));
    }

    @Test
    @WithMockUser
    @DisplayName("GET /history/errored: authenticated user — 200 with their error records")
    void getErrorHistory_returns200() throws Exception {
        QuantityMeasurementDTO errDto = new QuantityMeasurementDTO();
        errDto.setError(true);
        errDto.setErrorMessage("incompatible types");
        when(service.getErrorHistory(any())).thenReturn(List.of(errDto));

        mockMvc.perform(get(BASE_URL + "/history/errored"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$[0].error",        is(true)))
               .andExpect(jsonPath("$[0].errorMessage", is("incompatible types")));
    }

    @Test
    @WithAnonymousUser
    @DisplayName("GET /history/errored: unauthenticated — 401")
    void getErrorHistory_unauthenticated_returns401() throws Exception {
        mockMvc.perform(get(BASE_URL + "/history/errored"))
               .andExpect(status().isUnauthorized());
    }
}
