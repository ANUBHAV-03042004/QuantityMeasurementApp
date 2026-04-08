package com.app.quantitymeasurementapp.service;

import com.app.quantitymeasurementapp.exception.QuantityMeasurementException;
import com.app.quantitymeasurementapp.model.QuantityDTO;
import com.app.quantitymeasurementapp.model.QuantityMeasurementDTO;
import com.app.quantitymeasurementapp.model.QuantityMeasurementEntity;
import com.app.quantitymeasurementapp.repository.QuantityMeasurementRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("QuantityMeasurementServiceImpl unit tests")
class QuantityMeasurementServiceTest {

    @Mock
    private QuantityMeasurementRepository repository;

    @InjectMocks
    private QuantityMeasurementServiceImpl service;

    // Stub repository.save() to return the entity it receives unchanged
    @BeforeEach
    void stubSave() {
        lenient().when(repository.save(any(QuantityMeasurementEntity.class)))
                .thenAnswer(inv -> inv.getArgument(0));
    }

    // ── Shared userId used across all tests ───────────────────────────────────
    private static final Long USER_ID = 42L;

    // ── QuantityDTO factory helpers ───────────────────────────────────────────
    private QuantityDTO length(double value, String unit) {
        return new QuantityDTO(value, unit, "LengthUnit");
    }
    private QuantityDTO weight(double value, String unit) {
        return new QuantityDTO(value, unit, "WeightUnit");
    }
    private QuantityDTO volume(double value, String unit) {
        return new QuantityDTO(value, unit, "VolumeUnit");
    }
    private QuantityDTO temperature(double value, String unit) {
        return new QuantityDTO(value, unit, "TemperatureUnit");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // COMPARE
    // ─────────────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("compare()")
    class CompareTests {

        @Test
        @DisplayName("1 FEET == 12 INCHES → resultString = 'true'")
        void compare_feetAndInches_equal() {
            QuantityMeasurementDTO result =
                    service.compare(length(1, "FEET"), length(12, "INCHES"), USER_ID);

            assertThat(result.getResultString()).isEqualTo("true");
            assertThat(result.isError()).isFalse();
            assertThat(result.getOperation()).isEqualTo("COMPARE");
        }

        @Test
        @DisplayName("1 FEET != 10 INCHES → resultString = 'false'")
        void compare_feetAndInches_notEqual() {
            QuantityMeasurementDTO result =
                    service.compare(length(1, "FEET"), length(10, "INCHES"), USER_ID);

            assertThat(result.getResultString()).isEqualTo("false");
            assertThat(result.isError()).isFalse();
        }

        @Test
        @DisplayName("Saves entity with correct userId")
        void compare_savesEntityWithUserId() {
            service.compare(length(1, "FEET"), length(12, "INCHES"), USER_ID);

            ArgumentCaptor<QuantityMeasurementEntity> captor =
                    ArgumentCaptor.forClass(QuantityMeasurementEntity.class);
            verify(repository).save(captor.capture());
            assertThat(captor.getValue().getUserId()).isEqualTo(USER_ID);
        }

        @Test
        @DisplayName("Saves entity with null userId when user is not authenticated")
        void compare_savesEntityWithNullUserId_whenGuestUser() {
            service.compare(length(1, "FEET"), length(12, "INCHES"), null);

            ArgumentCaptor<QuantityMeasurementEntity> captor =
                    ArgumentCaptor.forClass(QuantityMeasurementEntity.class);
            verify(repository).save(captor.capture());
            assertThat(captor.getValue().getUserId()).isNull();
        }

        @Test
        @DisplayName("Mixed measurement types throws QuantityMeasurementException")
        void compare_differentTypes_throwsException() {
            assertThatThrownBy(() ->
                    service.compare(length(1, "FEET"), weight(1, "KILOGRAM"), USER_ID))
                    .isInstanceOf(QuantityMeasurementException.class)
                    .hasMessageContaining("COMPARE");
        }

        @Test
        @DisplayName("Temperature units can be compared")
        void compare_temperatureUnits() {
            // 0 CELSIUS == 32 FAHRENHEIT
            QuantityMeasurementDTO result =
                    service.compare(temperature(0, "CELSIUS"), temperature(32, "FAHRENHEIT"), USER_ID);

            assertThat(result.getResultString()).isEqualTo("true");
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // CONVERT
    // ─────────────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("convert()")
    class ConvertTests {

        @Test
        @DisplayName("1 FEET → INCHES = 12.0")
        void convert_feetToInches() {
            QuantityMeasurementDTO result =
                    service.convert(length(1, "FEET"), length(0, "INCHES"), USER_ID);

            assertThat(result.getResultValue()).isEqualTo(12.0);
            assertThat(result.getResultUnit()).isEqualTo("INCHES");
            assertThat(result.getOperation()).isEqualTo("CONVERT");
        }

        @Test
        @DisplayName("100 CELSIUS → FAHRENHEIT = 212.0")
        void convert_celsiusToFahrenheit() {
            QuantityMeasurementDTO result =
                    service.convert(temperature(100, "CELSIUS"), temperature(0, "FAHRENHEIT"), USER_ID);

            assertThat(result.getResultValue()).isCloseTo(212.0, within(0.001));
            assertThat(result.getResultUnit()).isEqualTo("FAHRENHEIT");
        }

        @Test
        @DisplayName("1 KILOGRAM → GRAM = 1000.0")
        void convert_kilogramToGram() {
            QuantityMeasurementDTO result =
                    service.convert(weight(1, "KILOGRAM"), weight(0, "GRAM"), USER_ID);

            assertThat(result.getResultValue()).isEqualTo(1000.0);
        }

        @Test
        @DisplayName("Saves entity with correct userId")
        void convert_savesEntityWithUserId() {
            service.convert(length(1, "FEET"), length(0, "INCHES"), USER_ID);

            ArgumentCaptor<QuantityMeasurementEntity> captor =
                    ArgumentCaptor.forClass(QuantityMeasurementEntity.class);
            verify(repository).save(captor.capture());
            assertThat(captor.getValue().getUserId()).isEqualTo(USER_ID);
            assertThat(captor.getValue().getOperation()).isEqualTo("CONVERT");
        }

        @Test
        @DisplayName("Mixed types throws QuantityMeasurementException")
        void convert_differentTypes_throwsException() {
            assertThatThrownBy(() ->
                    service.convert(length(1, "FEET"), weight(0, "KILOGRAM"), USER_ID))
                    .isInstanceOf(QuantityMeasurementException.class);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // ADD
    // ─────────────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("add()")
    class AddTests {

        @Test
        @DisplayName("1 FEET + 12 INCHES = 2.0 FEET")
        void add_feetAndInches_returnsTwoFeet() {
            QuantityMeasurementDTO result =
                    service.add(length(1, "FEET"), length(12, "INCHES"), USER_ID);

            assertThat(result.getResultValue()).isEqualTo(2.0);
            assertThat(result.getResultUnit()).isEqualTo("FEET");
            assertThat(result.getOperation()).isEqualTo("ADD");
        }

        @Test
        @DisplayName("Saves entity with correct userId")
        void add_savesEntityWithUserId() {
            service.add(length(1, "FEET"), length(12, "INCHES"), USER_ID);

            ArgumentCaptor<QuantityMeasurementEntity> captor =
                    ArgumentCaptor.forClass(QuantityMeasurementEntity.class);
            verify(repository).save(captor.capture());
            assertThat(captor.getValue().getUserId()).isEqualTo(USER_ID);
        }

        @Test
        @DisplayName("Adding temperature units throws QuantityMeasurementException")
        void add_temperatureUnits_throwsException() {
            assertThatThrownBy(() ->
                    service.add(temperature(10, "CELSIUS"), temperature(20, "CELSIUS"), USER_ID))
                    .isInstanceOf(QuantityMeasurementException.class)
                    .hasMessageContaining("Temperature");
        }

        @Test
        @DisplayName("Mixed measurement types throws QuantityMeasurementException")
        void add_differentTypes_throwsException() {
            assertThatThrownBy(() ->
                    service.add(length(1, "FEET"), weight(1, "KILOGRAM"), USER_ID))
                    .isInstanceOf(QuantityMeasurementException.class);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SUBTRACT
    // ─────────────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("subtract()")
    class SubtractTests {

        @Test
        @DisplayName("24 INCHES - 1 FEET = 12.0 INCHES")
        void subtract_inchesMinusFeet() {
            QuantityMeasurementDTO result =
                    service.subtract(length(24, "INCHES"), length(1, "FEET"), USER_ID);

            assertThat(result.getResultValue()).isEqualTo(12.0);
            assertThat(result.getResultUnit()).isEqualTo("INCHES");
            assertThat(result.getOperation()).isEqualTo("SUBTRACT");
        }

        @Test
        @DisplayName("Saves entity with correct userId")
        void subtract_savesEntityWithUserId() {
            service.subtract(length(24, "INCHES"), length(1, "FEET"), USER_ID);

            ArgumentCaptor<QuantityMeasurementEntity> captor =
                    ArgumentCaptor.forClass(QuantityMeasurementEntity.class);
            verify(repository).save(captor.capture());
            assertThat(captor.getValue().getUserId()).isEqualTo(USER_ID);
        }

        @Test
        @DisplayName("Subtracting temperature units throws QuantityMeasurementException")
        void subtract_temperatureUnits_throwsException() {
            assertThatThrownBy(() ->
                    service.subtract(temperature(100, "CELSIUS"), temperature(50, "CELSIUS"), USER_ID))
                    .isInstanceOf(QuantityMeasurementException.class)
                    .hasMessageContaining("Temperature");
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // DIVIDE
    // ─────────────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("divide()")
    class DivideTests {

        @Test
        @DisplayName("2 FEET / 1 FEET = 2.0 (dimensionless)")
        void divide_sameFeet_returnsRatio() {
            QuantityMeasurementDTO result =
                    service.divide(length(2, "FEET"), length(1, "FEET"), USER_ID);

            assertThat(result.getResultValue()).isEqualTo(2.0);
            assertThat(result.getOperation()).isEqualTo("DIVIDE");
        }

        @Test
        @DisplayName("24 INCHES / 1 FEET = 2.0")
        void divide_inchesAndFeet_returnsRatio() {
            QuantityMeasurementDTO result =
                    service.divide(length(24, "INCHES"), length(1, "FEET"), USER_ID);

            assertThat(result.getResultValue()).isEqualTo(2.0);
        }

        @Test
        @DisplayName("Saves entity with correct userId")
        void divide_savesEntityWithUserId() {
            service.divide(length(2, "FEET"), length(1, "FEET"), USER_ID);

            ArgumentCaptor<QuantityMeasurementEntity> captor =
                    ArgumentCaptor.forClass(QuantityMeasurementEntity.class);
            verify(repository).save(captor.capture());
            assertThat(captor.getValue().getUserId()).isEqualTo(USER_ID);
        }

        @Test
        @DisplayName("Divide by zero throws QuantityMeasurementException")
        void divide_byZero_throwsException() {
            assertThatThrownBy(() ->
                    service.divide(length(1, "FEET"), length(0, "FEET"), USER_ID))
                    .isInstanceOf(QuantityMeasurementException.class)
                    .hasMessageContaining("DIVIDE");
        }

        @Test
        @DisplayName("Dividing temperature units throws QuantityMeasurementException")
        void divide_temperatureUnits_throwsException() {
            assertThatThrownBy(() ->
                    service.divide(temperature(100, "CELSIUS"), temperature(50, "CELSIUS"), USER_ID))
                    .isInstanceOf(QuantityMeasurementException.class)
                    .hasMessageContaining("Temperature");
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // HISTORY — user-scoped queries
    // ─────────────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("History queries (user-scoped)")
    class HistoryTests {

        @Test
        @DisplayName("getHistoryByOperation delegates to user-scoped repository method")
        void getHistoryByOperation_delegatesToUserScopedRepo() {
            when(repository.findByUserIdAndOperation(USER_ID, "COMPARE"))
                    .thenReturn(List.of());

            service.getHistoryByOperation("COMPARE", USER_ID);

            verify(repository).findByUserIdAndOperation(USER_ID, "COMPARE");
            verify(repository, never()).findByOperation(any());
        }

        @Test
        @DisplayName("getHistoryByOperation uppercases the operation string")
        void getHistoryByOperation_uppercasesOperation() {
            when(repository.findByUserIdAndOperation(USER_ID, "COMPARE"))
                    .thenReturn(List.of());

            service.getHistoryByOperation("compare", USER_ID);   // lowercase input

            verify(repository).findByUserIdAndOperation(USER_ID, "COMPARE");
        }

        @Test
        @DisplayName("getHistoryByMeasurementType delegates to user-scoped repository method")
        void getHistoryByMeasurementType_delegatesToUserScopedRepo() {
            when(repository.findByUserIdAndThisMeasurementType(USER_ID, "LengthUnit"))
                    .thenReturn(List.of());

            service.getHistoryByMeasurementType("LengthUnit", USER_ID);

            verify(repository).findByUserIdAndThisMeasurementType(USER_ID, "LengthUnit");
            verify(repository, never()).findByThisMeasurementType(any());
        }

        @Test
        @DisplayName("getOperationCount delegates to user-scoped repository method")
        void getOperationCount_delegatesToUserScopedRepo() {
            when(repository.countByUserIdAndOperationAndIsErrorFalse(USER_ID, "ADD"))
                    .thenReturn(7L);

            long count = service.getOperationCount("ADD", USER_ID);

            assertThat(count).isEqualTo(7L);
            verify(repository).countByUserIdAndOperationAndIsErrorFalse(USER_ID, "ADD");
            verify(repository, never()).countByOperationAndIsErrorFalse(any());
        }

        @Test
        @DisplayName("getErrorHistory delegates to user-scoped repository method")
        void getErrorHistory_delegatesToUserScopedRepo() {
            when(repository.findByUserIdAndIsErrorTrue(USER_ID))
                    .thenReturn(List.of());

            service.getErrorHistory(USER_ID);

            verify(repository).findByUserIdAndIsErrorTrue(USER_ID);
            verify(repository, never()).findByIsErrorTrue();
        }

        @Test
        @DisplayName("Two users get separate history — different userIds hit different repo calls")
        void historyIsolation_differentUsersGetDifferentData() {
            Long userA = 1L;
            Long userB = 2L;

            QuantityMeasurementEntity recordA = new QuantityMeasurementEntity(
                    1.0, "FEET", "LengthUnit", 12.0, "INCHES", "LengthUnit", true);
            recordA.setUserId(userA);

            when(repository.findByUserIdAndOperation(userA, "COMPARE"))
                    .thenReturn(List.of(recordA));
            when(repository.findByUserIdAndOperation(userB, "COMPARE"))
                    .thenReturn(List.of());

            List<QuantityMeasurementDTO> resultA = service.getHistoryByOperation("COMPARE", userA);
            List<QuantityMeasurementDTO> resultB = service.getHistoryByOperation("COMPARE", userB);

            assertThat(resultA).hasSize(1);
            assertThat(resultB).isEmpty();
        }
    }
}