# ⚖️ Quantity Measurement Application

A Java-based multi-unit measurement system built incrementally across **18 Use Cases**, following **Test-Driven Development (TDD)**, **SOLID principles**, and an **N-Tier Architecture**.

> **Trainee:** Anubhav Kumar Srivastava  
> **Repository:** [ANUBHAV-03042004/QuantityMeasurementApp](https://github.com/ANUBHAV-03042004/QuantityMeasurementApp)  
> **Program:** BridgeLabz Java Training — M1 Module

---

## 📋 Use Case Progress

| # | Use Case | Branch | Date Completed |
|---|----------|--------|---------------|
| UC1 | [Feet Measurement Equality](https://github.com/ANUBHAV-03042004/QuantityMeasurementApp/tree/feature/UC1-FeetMeasurementEquality) | `feature/UC1-FeetMeasurementEquality` | 17 Feb 2026 |
| UC2 | [Feet and Inches Measurement Equality](https://github.com/ANUBHAV-03042004/QuantityMeasurementApp/tree/feature/UC2-FeetAndInchesMeasurementEquality) | `feature/UC2-FeetAndInchesMeasurementEquality` | 18 Feb 2026 |
| UC3 | [Generic Quantity Class for DRY Principle](https://github.com/ANUBHAV-03042004/QuantityMeasurementApp/tree/feature/UC3-GenericQuantityClassForDRYPrinciple) | `feature/UC3-GenericQuantityClassForDRYPrinciple` | 18 Feb 2026 |
| UC4 | [Extended Unit Support](https://github.com/ANUBHAV-03042004/QuantityMeasurementApp/tree/feature/UC4-ExtendedUnitSupport) | `feature/UC4-Extended Unit Support` | 19 Feb 2026 |
| UC5 | [Unit-to-Unit Conversion](https://github.com/ANUBHAV-03042004/QuantityMeasurementApp/tree/feature/UC5-Unit-to-Unit-Conversion) | `feature/UC5-Unit-to-Unit Conversion` | 19 Feb 2026 |
| UC6 | [Addition of Two Length Units](https://github.com/ANUBHAV-03042004/QuantityMeasurementApp/tree/feature/UC6-Addition-Of-Two-Length-Units) | `feature/UC6-Addition-Of-Two-Length-Units` | 19 Feb 2026 |
| UC7 | [Addition with Target Unit Specification](https://github.com/ANUBHAV-03042004/QuantityMeasurementApp/tree/feature/UC7-Addition-With-Target-Unit-Specification) | `feature/UC7-Addition-With-Target-Unit-Specification` | 19 Feb 2026 |
| UC8 | [Refactoring Unit Enum to Standalone](https://github.com/ANUBHAV-03042004/QuantityMeasurementApp/tree/feature/UC8-Refactoring-Unit-Enum-To-Standalone) | `feature/UC8-Refactoring-Unit-Enum-To-Standalone` | 19 Feb 2026 |
| UC9 | [Weight Measurement](https://github.com/ANUBHAV-03042004/QuantityMeasurementApp/tree/feature/UC9-Weight-Measurement) | `feature/UC9-Weight-Measurement` | 19 Feb 2026 |
| UC10 | [Generic Quantity Class with Unit Interface for Multi-Category Support](https://github.com/ANUBHAV-03042004/QuantityMeasurementApp/tree/feature/UC10-Generic-Quantity-Class-with-Unit-Interface-For-Multi-Category-Support) | `feature/UC10-Generic-Quantity-Class-with-Unit-Interface-For-Multi-Category-Support` | 20 Feb 2026 |
| UC11 | [Volume Measurement — Equality, Conversion, and Addition (Litre, Millilitre, Gallon)](https://github.com/ANUBHAV-03042004/QuantityMeasurementApp/tree/feature/UC11-Volume-Measurement-Equality-Conversion-and-Addition) | `feature/UC11-Volume-Measurement-Equality, Conversion, and Addition` | 20 Feb 2026 |
| UC12 | [Subtraction and Division Operations on Quantity Measurements](https://github.com/ANUBHAV-03042004/QuantityMeasurementApp/tree/feature/UC12-Subtraction-and-Division-Operations-on-Quantity-Measurements) | `feature/UC12-Subtraction-and-Division-Operations-on-Quantity-Measurements` | 20 Feb 2026 |
| UC13 | [Centralized Arithmetic Logic to Enforce DRY in Quantity Operations](https://github.com/ANUBHAV-03042004/QuantityMeasurementApp/tree/feature/UC13-Centralized-Arithmetic-Logic-to-Enforce-DRY-in-Quantity-Operations) | `feature/UC13-Centralized-Arithmetic-Logic-to-Enforce-DRY-in-Quantity-Operations` | 21 Feb 2026 |
| UC14 | [Temperature Measurement with Selective Arithmetic Support and Measurable Refactoring](https://github.com/ANUBHAV-03042004/QuantityMeasurementApp/tree/feature/UC14-TemperaturE-Measurement-with-Selective-Arithmetic-Support-and-Measurable-Refactoring) | `feature/UC14-TemperaturE-Measurement-with-Selective-Arithmetic-Support-and-Measurable-Refactoring` | 21 Feb 2026 |
| UC15 | [N-Tier Architecture Refactoring for Quantity Measurement Application](https://github.com/ANUBHAV-03042004/QuantityMeasurementApp/tree/feature/UC15-N-Tier) | `feature/UC15-N-Tier` | 23 Feb 2026 |
| UC16 | [Database Integration with JDBC for Quantity Measurement Persistence](https://github.com/ANUBHAV-03042004/QuantityMeasurementApp/tree/feature/UC16) | `feature/UC16` | 13 Mar 2026 |
| UC17 | [Spring Framework Integration — REST Services and JPA for Quantity Measurement](https://github.com/ANUBHAV-03042004/QuantityMeasurementApp/tree/feature/UC17-spring-backend-for-quantity-measurement) | `feature/UC17-spring-backend-for-quantity-measurement` | 17 Mar 2026 |
| UC18 | [Google OAuth2 Authentication and JWT-based User Management](https://github.com/ANUBHAV-03042004/QuantityMeasurementApp/tree/feature/UC-18-google-authentication-and-user-management-for-quantity-measurement) | `feature/UC-18-google-authentication-and-user-management-for-quantity-measurement` | 02 Apr 2026 |

---

## 🏗️ Architecture (UC15 — N-Tier, extended in UC17)

```
com.app.quantitymeasurementapp/
├── auth/                 ← Authentication layer (AuthController, JWT login/register)
├── controller/           ← Presentation layer (QuantityMeasurementController)
├── service/              ← Business logic (IUserService, UserServiceImpl, IQuantityMeasurementService)
├── repository/           ← Data access layer (UserRepository, QuantityMeasurementRepository)
├── model/                ← DTOs & entities (QuantityDTO, QuantityMeasurementEntity, QuantityMeasurementDTO)
├── security/             ← Security config (JwtUtil, JwtAuthFilter, OAuth2SuccessHandler, SecurityConfig)
├── exception/            ← Exception handling (GlobalExceptionHandler, custom exceptions)
├── user/                 ← User domain (User entity, UserController, UserRepository)
└── util/                 ← OpenAPI/Swagger config
```

---

## 📐 Supported Units

| Category | Units |
|----------|-------|
| **Length** | Feet, Inches, Yards, Centimeters |
| **Weight** | Milligram, Gram, Kilogram, Pound, Tonne |
| **Volume** | Litre, Millilitre, Gallon |
| **Temperature** | Celsius, Fahrenheit, Kelvin |

---

## ⚙️ Supported Operations

| Operation | Length | Weight | Volume | Temperature |
|-----------|:------:|:------:|:------:|:-----------:|
| Compare (equality) | ✅ | ✅ | ✅ | ✅ |
| Convert | ✅ | ✅ | ✅ | ✅ |
| Add | ✅ | ✅ | ✅ | ❌ |
| Subtract | ✅ | ✅ | ✅ | ❌ |
| Divide | ✅ | ✅ | ✅ | ❌ |

> ℹ️ Temperature does not support arithmetic operations — temperature values are absolute points on a scale, not additive quantities.

---

## 🔐 Authentication & Security (UC18)

The application supports two authentication flows:

**JWT Authentication** — register and login via REST endpoints to receive a signed JWT token. Include the token in the `Authorization: Bearer <token>` header on all protected requests.

**Google OAuth2** — sign in with Google via the OAuth2 authorization code flow. On successful login, `OAuth2SuccessHandler` issues a JWT token for subsequent API calls, unifying both flows under the same token-based security model.

| Endpoint | Method | Access |
|----------|--------|--------|
| `/api/auth/register` | POST | Public |
| `/api/auth/login` | POST | Public |
| `/oauth2/authorization/google` | GET | Public |
| `/api/measurements/**` | POST/GET | JWT required |
| `/api/users/**` | GET | JWT required |

---

## 🚀 Running the Project (UC17+)

**Prerequisites:** Java 17+, Maven 3.6+, MySQL (or Aiven MySQL for production)

```bash
# Clone
git clone https://github.com/ANUBHAV-03042004/QuantityMeasurementApp.git
cd QuantityMeasurementApp

# Run with embedded H2 (development)
mvn spring-boot:run

# Run with production MySQL profile
mvn spring-boot:run -Dspring.profiles.active=prod
```

**Access points after startup:**
- REST API: `http://localhost:8080/api/v1/quantities/`
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- H2 Console (dev): `http://localhost:8080/h2-console`
- Actuator health: `http://localhost:8080/actuator/health`

---

## 🧪 Running Tests

```bash
# All tests
mvn test

# Specific test class
mvn test -Dtest=QuantityMeasurementControllerTest
mvn test -Dtest=AuthControllerTest

# Generate rich HTML test report
mvn surefire-report:report
open target/site/surefire-report.html
```

Tests are written using **JUnit 5** and **Mockito**, following TDD. REST layer tests use Spring's `MockMvc` with `@WebMvcTest`. Integration tests use `@SpringBootTest` with `TestRestTemplate`.

---

## 📁 Branch Strategy

Each use case is developed on a dedicated feature branch and merged into `main` upon completion:

```
main
 └── feature/UC1-FeetMeasurementEquality
 └── feature/UC2-FeetAndInchesMeasurementEquality
 └── feature/UC3-GenericQuantityClassForDRYPrinciple
 └── ...
 └── feature/UC15-N-Tier
 └── feature/UC16
 └── feature/UC17-spring-backend-for-quantity-measurement
 └── feature/UC-18-google-authentication-and-user-management-for-quantity-measurement
```

---

## 🔑 Key Design Decisions

**`IMeasurable` interface** — all unit enums implement a common contract: `convertToBaseUnit()`, `convertFromBaseUnit()`, `getMeasurementType()`, and `validateOperationSupport()`. This enables the generic `Quantity<U>` class to work across all measurement categories without duplication (DRY).

**`ArithmeticOperation` enum** — centralises all arithmetic (`ADD`, `SUBTRACT`, `DIVIDE`, `MULTIPLY`) as `DoubleBinaryOperator` lambdas (UC13), eliminating repeated operator logic.

**`QuantityDTO` as API boundary** — the controller and app entry point use only `QuantityDTO` with its own inner enums. The service layer maps these to core enums, keeping the layers fully decoupled.

**Spring Data JPA repository (UC17)** — replaces the JDBC `QuantityMeasurementDatabaseRepository` introduced in UC16. Declarative query methods (`findByOperation`, `findByCreatedAtAfter`) eliminate hand-written SQL while retaining full query flexibility via `@Query`.

**Dual authentication (UC18)** — `JwtUtil` issues and validates tokens for both the local register/login flow and the Google OAuth2 flow. `OAuth2SuccessHandler` bridges the OAuth2 callback into the same JWT response, so the client never needs to handle two different auth schemes.

---

## 📌 Notes

- All temperature conversion uses Celsius as the base unit internally
- Weight rounding is applied to 2 decimal places via `Math.round` in `WeightUnit.convertFromBaseUnit()`
- The `QuantityMeasurementEntity` is annotated with `@Entity` and uses `@PrePersist`/`@PreUpdate` for automatic timestamps (UC17)
- Cross-category operations (e.g., adding LENGTH to WEIGHT) throw a `QuantityMeasurementException` with a clear message, handled globally by `GlobalExceptionHandler` via `@ControllerAdvice`
- Production database is Aiven MySQL; development uses embedded H2 with the H2 console enabled
- `spring.jpa.hibernate.ddl-auto=validate` is recommended in production to prevent accidental schema changes
