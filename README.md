# ⚖️ Quantity Measurement Application

A Java-based multi-unit measurement system built incrementally across **15 Use Cases**, following **Test-Driven Development (TDD)**, **SOLID principles**, and an **N-Tier Architecture**.

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

---

## 🏗️ Architecture (UC15 — N-Tier)

```
com.quantitymeasurementapp/
├── application/          ← Entry point (QuantityMeasurementApp.java)
├── controller/           ← Presentation layer (QuantityMeasurementController)
├── service/              ← Business logic (IQuantityMeasurementService, ServiceImpl)
├── repository/           ← Data access layer (IQuantityMeasurementRepository, CacheRepository)
├── model/                ← DTOs & entities (QuantityDTO, QuantityMeasurementEntity, QuantityModel)
└── core/                 ← Domain (Quantity, IMeasurable, ArithmeticOperation, *Unit enums)
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

## 🔑 Key Design Decisions

**`IMeasurable` interface** — all unit enums implement a common contract: `convertToBaseUnit()`, `convertFromBaseUnit()`, `getMeasurementType()`, and `validateOperationSupport()`. This enables the generic `Quantity<U>` class to work across all measurement categories without duplication (DRY).

**`ArithmeticOperation` enum** — centralises all arithmetic (`ADD`, `SUBTRACT`, `DIVIDE`, `MULTIPLY`) as `DoubleBinaryOperator` lambdas (UC13), eliminating repeated operator logic.

**`QuantityDTO` as API boundary** — the controller and app entry point use only `QuantityDTO` with its own inner enums. The service layer maps these to core enums, keeping the layers fully decoupled.

**`QuantityMeasurementCacheRepository`** — singleton in-memory store with disk persistence via Java object serialization. Appends to file using `AppendableObjectOutputStream` to avoid re-writing the stream header on each save.

---

## 🚀 Running the Project

**Prerequisites:** Java 17+ and Maven (or any IDE — IntelliJ / Eclipse / VS Code with Java Extension Pack)

```bash
# Clone
git clone https://github.com/ANUBHAV-03042004/QuantityMeasurementApp.git
cd QuantityMeasurementApp

# Run (Maven)
mvn compile exec:java -Dexec.mainClass="com.quantitymeasurementapp.application.QuantityMeasurementApp"
```

**Expected output includes:**
- Length equality comparisons (feet ↔ inches ↔ yards)
- Unit conversions (feet → inches, Celsius → Fahrenheit, Celsius → Kelvin)
- Arithmetic results (add, subtract, divide across mixed units)
- Temperature arithmetic rejection with clear error messages
- Cross-category prevention (e.g. LENGTH + WEIGHT → error)
- Repository audit of last 3 stored operations

---

## 🧪 Running Tests

```bash
mvn test
```

Tests are written using **JUnit 5** following TDD — each use case has a corresponding test class covering positive, negative, and edge cases.

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
```

---

## 📌 Notes

- All temperature conversion uses Celsius as the base unit internally
- Weight rounding is applied to 2 decimal places via `Math.round` in `WeightUnit.convertFromBaseUnit()`
- The `QuantityMeasurementEntity` is immutable by convention (no setters) and `Serializable` for disk persistence
- Cross-category operations (e.g., adding LENGTH to WEIGHT) throw a `QuantityMeasurementException` with a clear message
