# CLAUDE.md - AI Assistant Guide for Trade Validation System

**Last Updated:** 2025-11-13
**Project:** Trade Validation REST API (KafkaCreditSuisse)
**Framework:** Spring Boot 1.5.4 | Java 8 | Maven

---

## Table of Contents

1. [Project Overview](#project-overview)
2. [Repository Structure](#repository-structure)
3. [Architecture & Design Patterns](#architecture--design-patterns)
4. [Key Files & Their Purposes](#key-files--their-purposes)
5. [Code Conventions](#code-conventions)
6. [Development Workflows](#development-workflows)
7. [Testing Guidelines](#testing-guidelines)
8. [Making Changes Safely](#making-changes-safely)
9. [Common Tasks](#common-tasks)
10. [API Reference](#api-reference)
11. [Validation System Deep Dive](#validation-system-deep-dive)
12. [Troubleshooting](#troubleshooting)

---

## Project Overview

### Purpose
A Spring Boot REST API that validates financial trade transactions against comprehensive business rules. The system processes JSON arrays of trade objects and applies validation rules specific to trade types (Spot, Forward, VanillaOption).

### Core Functionality
- **Validates** trade data against date constraints, currency codes, customer whitelists, and option-specific rules
- **Returns** detailed error reports or success confirmations
- **Supports** three trade types: Spot, Forward, VanillaOption
- **Runs** on port 9090 with a single REST endpoint

### Technology Stack
- **Spring Boot 1.5.4** - Web framework
- **Java 8** - Programming language
- **Maven 3.5.0** - Build tool (via wrapper)
- **JUnit 4** - Testing framework
- **org.json** - JSON processing

---

## Repository Structure

```
/home/user/tradevalidation/
├── pom.xml                              # Maven configuration (dependencies, build)
├── mvnw & mvnw.cmd                      # Maven wrapper (use these, not system Maven)
├── .mvn/wrapper/                        # Maven wrapper resources
├── README.md                            # User-facing documentation
├── .gitignore                           # Git exclusions
└── src/
    ├── main/
    │   ├── java/com/touraj/creditsuisse/kafkaproject/
    │   │   ├── KafkaCreditSuisseApplication.java       # Spring Boot entry point
    │   │   ├── controller/
    │   │   │   └── CreditSuisseRestController.java     # REST API endpoint
    │   │   ├── Validator/                              # Validation logic (7 validators)
    │   │   │   ├── IValidator.java                     # Validator interface
    │   │   │   ├── Validator.java                      # Facade for validation
    │   │   │   ├── ChainofValidators.java              # Orchestrates validator chain
    │   │   │   ├── BeforeDateValidator.java            # Date order validation
    │   │   │   ├── WeekendValidator.java               # Weekend checking
    │   │   │   ├── CustomerValidator.java              # Customer whitelist
    │   │   │   ├── ISO4217Validator.java               # Currency code validation
    │   │   │   ├── StyleValidator.java                 # Option style validation
    │   │   │   ├── ExcerciseStartDateValidator.java    # Exercise date validation
    │   │   │   └── ExpiryAndPrimiumDateValidator.java  # Expiry/premium validation
    │   │   └── util/
    │   │       └── Utility.java                        # Date/currency utilities
    │   └── resources/
    │       └── application.properties                   # Server config (port 9090)
    └── test/
        └── java/com/touraj/creditsuisse/kafkaproject/
            └── KafkaCreditSuisseApplicationTests.java  # Unit tests (8 tests)
```

### Key Directories

- **`src/main/java/.../controller/`** - REST API layer (single controller)
- **`src/main/java/.../Validator/`** - Business logic (all validation rules)
- **`src/main/java/.../util/`** - Utility functions (date/currency helpers)
- **`src/main/resources/`** - Configuration files
- **`src/test/`** - Test files (mirrors main structure)

---

## Architecture & Design Patterns

### Primary Pattern: Chain of Responsibility

The validation system implements a **Chain of Responsibility** pattern:

```
Request → Controller → Validator (Facade) → ChainofValidators → [7 Validators]
```

**Flow:**
1. `CreditSuisseRestController` receives JSON array of trades
2. `Validator` facade provides simple interface to validation
3. `ChainofValidators` orchestrates sequential execution of validators
4. Each validator implements `IValidator` interface
5. All validators contribute to shared `JSONArray` of errors
6. Errors returned to client as JSON response

### Supporting Patterns

1. **Facade Pattern**: `Validator.java` simplifies access to complex validation chain
2. **Strategy Pattern**: Different validators implement `IValidator` interface with specific strategies
3. **Template Method**: All validators follow same processing template via interface

### Architectural Principles

- **Stateless Design**: No session state; suitable for horizontal scaling
- **Error Accumulation**: All errors collected before response (fail-fast avoided)
- **Type Awareness**: Validators check trade type before applying rules
- **Single Responsibility**: Each validator handles one concern
- **Open/Closed**: Easy to add new validators without modifying existing code

---

## Key Files & Their Purposes

### Entry Point
- **`KafkaCreditSuisseApplication.java`** (`src/main/java/.../`)
  - Spring Boot main class with `@SpringBootApplication`
  - Contains `public static void main()` - application entry point
  - **Never modify** unless changing Spring Boot configuration

### REST API
- **`CreditSuisseRestController.java`** (`src/main/java/.../controller/`)
  - Defines single endpoint: `POST /validatetrades`
  - Receives JSON array as `text/plain`
  - Creates `Validator` and calls `startValidation()`
  - Returns validation errors or "No errors found."
  - **Modify when**: Adding endpoints, changing request/response format

### Validation Core
- **`IValidator.java`** (`src/main/java/.../Validator/`)
  - Interface defining validator contract
  - Single method: `void processValidation(JSONArray errors, JSONObject trade, int tradeIndex)`
  - **Modify when**: Adding parameters all validators need

- **`Validator.java`** (`src/main/java/.../Validator/`)
  - Facade class for validation system
  - Method: `startValidation(String tradesJSON)` - main entry point
  - Creates `ChainofValidators` and initiates processing
  - **Modify when**: Changing validation initialization logic

- **`ChainofValidators.java`** (`src/main/java/.../Validator/`)
  - Orchestrates validator execution
  - `initValidators()` - Creates all 7 validators
  - `executeChain()` - Runs validators for each trade
  - **Modify when**: Adding/removing validators from chain

### Individual Validators
Each validator checks specific rules. **Modify when** adding/changing validation logic:

1. **`BeforeDateValidator.java`** - Ensures valueDate > tradeDate
2. **`WeekendValidator.java`** - Checks valueDate not on Sat/Sun
3. **`CustomerValidator.java`** - Validates customer in whitelist (PLUTO1, PLUTO2)
4. **`ISO4217Validator.java`** - Validates ISO 4217 currency codes
5. **`StyleValidator.java`** - Validates option style (AMERICAN/EUROPEAN)
6. **`ExcerciseStartDateValidator.java`** - Validates exercise date range
7. **`ExpiryAndPrimiumDateValidator.java`** - Validates expiry/premium dates

### Utilities
- **`Utility.java`** (`src/main/java/.../util/`)
  - `checkBeforeDate(String firstDate, String secondDate)` - Date comparison
  - `isDateFallinWeekend(String date)` - Weekend detection
  - `isValidCurrencyISO4217(String currency)` - Currency validation
  - **Modify when**: Adding common utility functions

### Configuration
- **`application.properties`** (`src/main/resources/`)
  - Currently only sets `server.port=9090`
  - **Modify when**: Changing server config, adding properties

- **`pom.xml`** (root)
  - Maven configuration (dependencies, plugins, versions)
  - **Modify when**: Adding dependencies, updating versions, changing build

### Tests
- **`KafkaCreditSuisseApplicationTests.java`** (`src/test/`)
  - Contains 8 JUnit tests for validators and utilities
  - Uses Spring Test Runner (`@RunWith(SpringRunner.class)`)
  - **Modify when**: Adding new tests, updating test data

---

## Code Conventions

### Naming Conventions
- **Classes**: PascalCase (e.g., `BeforeDateValidator`)
- **Methods**: camelCase (e.g., `processValidation()`)
- **Variables**: camelCase (e.g., `tradeIndex`, `errors`)
- **Constants**: UPPER_SNAKE_CASE (though few used currently)
- **Packages**: lowercase (e.g., `com.touraj.creditsuisse.kafkaproject`)

### Package Naming
Follow existing structure:
```
com.touraj.creditsuisse.kafkaproject
    ├── controller    # REST endpoints
    ├── Validator     # Note: Capital 'V' (follow existing convention)
    └── util          # Utilities
```

### Date Format Standard
**Always use `yyyy-MM-dd` format** (e.g., "2016-08-11")
- Parsing: `LocalDate.parse(dateString)`
- Formatting: `localDate.toString()`

### Error Object Format
Errors are JSONObjects with this structure:
```java
JSONObject error = new JSONObject();
error.put("ErrorType", "Description of error");
error.put("TradeNumber", tradeIndex);
errors.put(error); // Add to errors array
```

### Validation Method Signature
All validators implement:
```java
@Override
public void processValidation(JSONArray errors, JSONObject trade, int tradeIndex) {
    // Validation logic
    // Add errors to 'errors' array if validation fails
}
```

### Import Organization
Follow Spring Boot conventions:
1. Java standard library imports
2. Third-party library imports (org.json, etc.)
3. Spring framework imports
4. Project imports

### Comments
- Use JavaDoc for public methods
- Use inline comments for complex logic
- Current codebase has minimal comments - maintain consistency

---

## Development Workflows

### Starting the Application

```bash
# Navigate to project root
cd /home/user/tradevalidation

# Start with Maven wrapper (recommended)
./mvnw spring-boot:run

# Or build and run JAR
./mvnw clean package
java -jar target/kafkacreditsuisse-0.0.1-SNAPSHOT.jar
```

Application starts on **http://localhost:9090**

### Building the Project

```bash
# Clean and compile
./mvnw clean compile

# Clean and package (creates JAR)
./mvnw clean package

# Skip tests during build
./mvnw clean package -DskipTests

# Clean, compile, test, and install to local Maven repo
./mvnw clean install
```

### Running Tests

```bash
# Run all tests
./mvnw test

# Run specific test class
./mvnw test -Dtest=KafkaCreditSuisseApplicationTests

# Run specific test method
./mvnw test -Dtest=KafkaCreditSuisseApplicationTests#testBeforeDate

# Run tests with verbose output
./mvnw test -X
```

### Testing the API

```bash
# Example curl request
curl -X POST http://localhost:9090/validatetrades \
  -H "Content-Type: text/plain" \
  -d '[
    {
      "customer": "PLUTO1",
      "ccyPair": "EURUSD",
      "type": "Spot",
      "direction": "BUY",
      "tradeDate": "2016-08-11",
      "amount1": 1000000.00,
      "amount2": 1120000.00,
      "rate": 1.12,
      "valueDate": "2016-08-15",
      "legalEntity": "CS Zurich",
      "trader": "Johann Baumfiddler"
    }
  ]'
```

### Git Workflow

Current branch: `claude/claude-md-mhy1u5vdgui2jubz-01Kk7WeHhg38x6PY5Ca5o88g`

**For AI assistants:**
1. **Always develop on the specified branch**
2. **Commit with clear, descriptive messages**
3. **Push to the designated branch** using: `git push -u origin <branch-name>`
4. **Never push to main/master** without explicit permission

```bash
# Check current branch
git branch

# Commit changes
git add .
git commit -m "Descriptive message about changes"

# Push to feature branch
git push -u origin claude/claude-md-mhy1u5vdgui2jubz-01Kk7WeHhg38x6PY5Ca5o88g
```

---

## Testing Guidelines

### Test Structure
Tests are in: `src/test/java/com/touraj/creditsuisse/kafkaproject/KafkaCreditSuisseApplicationTests.java`

### Existing Tests (8 total)

1. **`testBeforeDate()`**
   - Tests `Utility.checkBeforeDate()` method
   - Validates date comparison logic

2. **`testIfDateFallInWeekend()`**
   - Tests `Utility.isDateFallinWeekend()` method
   - Uses known weekend date (June 11, 2017 = Sunday)

3. **`testIfCurrencyIsValidISO4217()`**
   - Tests `Utility.isValidCurrencyISO4217()` method
   - Validates currency code checking

4. **`testBeforeDateValidator()`**
   - Integration test for BeforeDateValidator
   - Uses valid trade object

5. **`testWeekendValidator()`**
   - Integration test for WeekendValidator
   - Uses valid trade object

6. **`testCustomerValidator()`**
   - Integration test for CustomerValidator
   - Tests valid customer (PLUTO1)

7. **`testCustomerValidator2()`**
   - Integration test for CustomerValidator
   - Tests invalid customer (expects failure)

8. **Context Load Test** (implicit via `@SpringBootTest`)

### Writing New Tests

**Template for Utility Tests:**
```java
@Test
public void testYourUtilityMethod() {
    boolean result = Utility.yourMethod(params);
    Assert.isTrue(result, "Error message if fails");
}
```

**Template for Validator Tests:**
```java
@Test
public void testYourValidator() {
    JSONArray errors = new JSONArray();
    JSONObject trade = new JSONObject();
    trade.put("fieldName", "value");
    // ... populate trade object

    YourValidator validator = new YourValidator();
    validator.processValidation(errors, trade, 0);

    assertEquals(0, errors.length()); // Expect no errors
}
```

### Test Best Practices
1. **Test one thing per test method**
2. **Use descriptive test names** (e.g., `testWeekendValidator_WithSaturday_ReturnsError`)
3. **Test both success and failure cases**
4. **Use known valid/invalid data**
5. **Add tests when adding new validators**
6. **Run tests before committing**: `./mvnw test`

---

## Making Changes Safely

### Before Making Changes

1. **Understand the flow**: Request → Controller → Validator → ChainofValidators → [Validators]
2. **Check existing tests**: Ensure you understand current behavior
3. **Identify impact**: Will change affect multiple validators?
4. **Read related code**: Understand dependencies

### Adding a New Validator

**Step-by-step process:**

1. **Create new validator class** in `src/main/java/.../Validator/`
   ```java
   package com.touraj.creditsuisse.kafkaproject.Validator;

   import org.json.JSONArray;
   import org.json.JSONObject;

   public class YourNewValidator implements IValidator {
       @Override
       public void processValidation(JSONArray errors, JSONObject trade, int tradeIndex) {
           // Check if rule applies to this trade type
           String type = trade.getString("type");
           if (!type.equals("YourTargetType")) {
               return; // Skip if not applicable
           }

           // Validation logic
           if (/* validation fails */) {
               JSONObject error = new JSONObject();
               error.put("ErrorType", "Description of the error");
               error.put("TradeNumber", tradeIndex);
               errors.put(error);
           }
       }
   }
   ```

2. **Add to validator chain** in `ChainofValidators.java:initValidators()`
   ```java
   validatorsList.add(new YourNewValidator());
   ```

3. **Add tests** in `KafkaCreditSuisseApplicationTests.java`
   ```java
   @Test
   public void testYourNewValidator() {
       JSONArray errors = new JSONArray();
       JSONObject trade = createTestTrade(); // Helper method

       YourNewValidator validator = new YourNewValidator();
       validator.processValidation(errors, trade, 0);

       assertEquals(expectedErrorCount, errors.length());
   }
   ```

4. **Run tests**: `./mvnw test`
5. **Test manually**: Start app and send curl request
6. **Commit changes**: Clear commit message describing the new validator

### Modifying Existing Validator

1. **Read the validator code** thoroughly
2. **Check which tests use it**
3. **Make the change**
4. **Update affected tests**
5. **Run all tests**: `./mvnw test`
6. **Test manually** with various trade types

### Adding New Dependencies

1. **Edit `pom.xml`**
   ```xml
   <dependency>
       <groupId>group.id</groupId>
       <artifactId>artifact-id</artifactId>
       <version>1.0.0</version>
   </dependency>
   ```

2. **Reload Maven**: `./mvnw clean compile`
3. **Verify dependency downloaded**: Check `.m2` repository or build output
4. **Test the build**: `./mvnw clean package`

### Changing API Contract

**Warning:** Changing the API contract affects clients!

If you need to change `/validatetrades` endpoint:
1. **Document the change** in README.md
2. **Consider versioning**: Create `/v2/validatetrades` instead
3. **Update controller** in `CreditSuisseRestController.java`
4. **Update tests**
5. **Test extensively** with curl

---

## Common Tasks

### Task: Add a New Trade Type

**Example: Adding "Swap" trade type**

1. **Identify validation rules** for Swap trades
2. **Create validators** if new rules needed
3. **Update existing validators** to handle "Swap" type:
   ```java
   String type = trade.getString("type");
   if (type.equals("Spot") || type.equals("Forward") || type.equals("Swap")) {
       // Apply validation
   }
   ```
4. **Add tests** with Swap trade objects
5. **Update documentation** (README.md, this file)

### Task: Change Customer Whitelist

**Current whitelist:** PLUTO1, PLUTO2

**To add PLUTO3:**

1. **Edit `CustomerValidator.java`** (line ~20-30)
   ```java
   if (customer.equals("PLUTO1") || customer.equals("PLUTO2") || customer.equals("PLUTO3")) {
       // Valid customer
   } else {
       // Error
   }
   ```

2. **Add test** in `KafkaCreditSuisseApplicationTests.java`
   ```java
   @Test
   public void testCustomerValidator_PLUTO3() {
       JSONArray errors = new JSONArray();
       JSONObject trade = new JSONObject();
       trade.put("customer", "PLUTO3");
       trade.put("type", "Spot");
       // ... other fields

       CustomerValidator validator = new CustomerValidator();
       validator.processValidation(errors, trade, 0);

       assertEquals(0, errors.length());
   }
   ```

3. **Run tests**: `./mvnw test`

### Task: Add New Utility Function

**Example: Add `isValidEmail()` method**

1. **Add to `Utility.java`**
   ```java
   public static boolean isValidEmail(String email) {
       // Validation logic
       return email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
   }
   ```

2. **Add test** in `KafkaCreditSuisseApplicationTests.java`
   ```java
   @Test
   public void testIsValidEmail() {
       Assert.isTrue(Utility.isValidEmail("test@example.com"), "Valid email failed");
       Assert.isTrue(!Utility.isValidEmail("invalid"), "Invalid email passed");
   }
   ```

3. **Use in validators** as needed

### Task: Change Server Port

1. **Edit `application.properties`**
   ```properties
   server.port=8080
   ```

2. **Restart application**
3. **Update documentation** mentioning the port

### Task: Add Logging

**Spring Boot uses SLF4J by default**

1. **Add logger to class**
   ```java
   import org.slf4j.Logger;
   import org.slf4j.LoggerFactory;

   public class YourValidator implements IValidator {
       private static final Logger logger = LoggerFactory.getLogger(YourValidator.class);

       @Override
       public void processValidation(JSONArray errors, JSONObject trade, int tradeIndex) {
           logger.info("Validating trade {}", tradeIndex);
           // ... validation logic
           logger.debug("Trade details: {}", trade.toString());
       }
   }
   ```

2. **Configure logging** in `application.properties`
   ```properties
   logging.level.com.touraj.creditsuisse=DEBUG
   logging.level.org.springframework=INFO
   ```

---

## API Reference

### Endpoint: POST /validatetrades

**URL:** `http://localhost:9090/validatetrades`

**Method:** POST

**Content-Type:** `text/plain` (not `application/json`)

**Request Body:** JSON array of trade objects

**Response:**
- **Success**: `"No errors found."`
- **Errors**: JSON array of error objects

### Trade Object Schema

**Common Fields (All Types):**
```json
{
  "customer": "string",        // Required: PLUTO1 or PLUTO2
  "ccyPair": "string",         // Currency pair (e.g., "EURUSD")
  "type": "string",            // Required: "Spot", "Forward", or "VanillaOption"
  "direction": "string",       // BUY or SELL
  "tradeDate": "YYYY-MM-DD",   // Required: Trade execution date
  "amount1": number,           // First currency amount
  "amount2": number,           // Second currency amount
  "rate": number,              // Exchange rate
  "valueDate": "YYYY-MM-DD",   // Required: Value/settlement date
  "legalEntity": "string",     // Legal entity (e.g., "CS Zurich")
  "trader": "string"           // Trader name
}
```

**Additional Fields for VanillaOption:**
```json
{
  "style": "string",                // Required: "AMERICAN" or "EUROPEAN"
  "strategy": "string",             // Option strategy
  "deliveryDate": "YYYY-MM-DD",     // Delivery date
  "expiryDate": "YYYY-MM-DD",       // Option expiry date
  "excerciseStartDate": "YYYY-MM-DD", // Exercise start (American only)
  "payCcy": "string",               // Payment currency (ISO 4217)
  "premium": "string",              // Premium amount
  "premiumCcy": "string",           // Premium currency (ISO 4217)
  "premiumType": "string",          // Premium type
  "premiumDate": "YYYY-MM-DD"       // Premium payment date
}
```

### Error Object Schema

```json
{
  "ErrorType": "string",      // Description of validation failure
  "TradeNumber": number       // Zero-based index of failing trade
}
```

### Example Responses

**Success:**
```
No errors found.
```

**Validation Errors:**
```json
[
  {
    "ErrorType": "Value date cannot be before trade date",
    "TradeNumber": 0
  },
  {
    "ErrorType": "Value date cannot fall on weekend",
    "TradeNumber": 0
  }
]
```

---

## Validation System Deep Dive

### Validator Execution Order

Validators execute in this sequence (see `ChainofValidators.java:initValidators()`):

1. **BeforeDateValidator** - Date ordering
2. **WeekendValidator** - Weekend checking
3. **ISO4217Validator** - Currency validation
4. **CustomerValidator** - Customer whitelist
5. **StyleValidator** - Option style
6. **ExcerciseStartDateValidator** - Exercise dates
7. **ExpiryAndPrimiumDateValidator** - Expiry/premium dates

**Note:** Order matters! Earlier validators may detect errors that later validators assume are correct.

### Validation Rules by Trade Type

#### Spot & Forward Trades

| Validator | Rule | Error Message |
|-----------|------|---------------|
| BeforeDateValidator | valueDate > tradeDate | "Value date cannot be before trade date" |
| WeekendValidator | valueDate ≠ Sat/Sun | "Value date cannot fall on weekend" |
| CustomerValidator | customer ∈ {PLUTO1, PLUTO2} | "Customer must be PLUTO1 or PLUTO2" |

#### VanillaOption Trades

All Spot/Forward rules **plus:**

| Validator | Rule | Error Message |
|-----------|------|---------------|
| ISO4217Validator | payCcy & premiumCcy valid ISO 4217 | "payCcy or primiumCcy are not valid ISO codes" |
| StyleValidator | style ∈ {AMERICAN, EUROPEAN} | "Style not support just American and European Option" |
| ExcerciseStartDateValidator (American only) | tradeDate < excerciseStartDate < expiryDate | "excerciseStartDate cannot be before tradeDate or after expiryDate" |
| ExpiryAndPrimiumDateValidator | expiryDate < deliveryDate AND premiumDate < deliveryDate | "expiryDate or primiumDate cannot be after deliveryDate" |

### Validation Logic Patterns

**Type Checking Pattern:**
```java
String type = trade.getString("type");
if (type.equals("Spot") || type.equals("Forward")) {
    // Apply rule
}
```

**American-Only Pattern:**
```java
String style = trade.optString("style", "");
if (style.equals("AMERICAN")) {
    // Apply American-specific rule
}
```

**Date Validation Pattern:**
```java
String date1 = trade.getString("date1");
String date2 = trade.getString("date2");
if (!Utility.checkBeforeDate(date1, date2)) {
    JSONObject error = new JSONObject();
    error.put("ErrorType", "date1 must be before date2");
    error.put("TradeNumber", tradeIndex);
    errors.put(error);
}
```

### Extending Validation

**To add validation complexity:**

1. **Simple addition**: Add logic to existing validator
2. **New concern**: Create new validator class
3. **Cross-field validation**: Use `Validator` class or create coordinator validator
4. **External service**: Call from validator (but maintain stateless design)

**Example: Add database lookup for customer:**
```java
// In CustomerValidator.java
public void processValidation(JSONArray errors, JSONObject trade, int tradeIndex) {
    String customer = trade.getString("customer");

    // Current: Hardcoded whitelist
    // Enhanced: Database lookup
    if (!customerService.isValidCustomer(customer)) {
        JSONObject error = new JSONObject();
        error.put("ErrorType", "Invalid customer");
        error.put("TradeNumber", tradeIndex);
        errors.put(error);
    }
}
```

---

## Troubleshooting

### Common Issues

#### Issue: Application won't start

**Symptoms:**
- Error: "Port 9090 already in use"
- Application starts then immediately stops

**Solutions:**
1. **Kill process on port 9090:**
   ```bash
   lsof -ti:9090 | xargs kill -9
   ```

2. **Change port in `application.properties`:**
   ```properties
   server.port=8080
   ```

3. **Check for exceptions in logs** - look for stack traces

#### Issue: Tests failing after changes

**Symptoms:**
- `./mvnw test` shows failures
- Assertions fail

**Solutions:**
1. **Read the error message** carefully
2. **Check if test data needs updating**
3. **Verify validator logic** matches test expectations
4. **Run single test** to isolate: `./mvnw test -Dtest=ClassName#methodName`
5. **Add debug logging** to validator

#### Issue: Validation not working as expected

**Symptoms:**
- Valid trades show errors
- Invalid trades pass validation

**Solutions:**
1. **Check trade type matching** - validators may skip wrong types
2. **Verify field names** - JSON field names are case-sensitive
3. **Check date format** - must be `yyyy-MM-dd`
4. **Add logging** to see which validators execute
5. **Test validator in isolation** with unit test

#### Issue: Maven build fails

**Symptoms:**
- `./mvnw clean install` fails
- Compilation errors

**Solutions:**
1. **Check Java version**: `java -version` (needs Java 8+)
2. **Clean and retry**: `./mvnw clean compile`
3. **Check for syntax errors** in recent changes
4. **Verify imports** are correct
5. **Check `pom.xml`** for malformed XML

#### Issue: JSON parsing errors

**Symptoms:**
- `JSONException` in logs
- "Missing field" errors

**Solutions:**
1. **Use `trade.optString("field", "default")`** for optional fields
2. **Use `trade.getString("field")`** for required fields
3. **Check field existence** before accessing: `trade.has("field")`
4. **Validate JSON format** with online validator

### Debugging Tips

1. **Add logging** throughout validator chain:
   ```java
   logger.info("Processing trade {}: {}", tradeIndex, trade.toString());
   ```

2. **Use debugger** in IDE:
   - Set breakpoint in `ChainofValidators.executeChain()`
   - Step through each validator

3. **Test with minimal trade object**:
   ```json
   [{
     "customer": "PLUTO1",
     "type": "Spot",
     "tradeDate": "2016-08-11",
     "valueDate": "2016-08-15"
   }]
   ```

4. **Check validator order** - earlier validators may affect later ones

5. **Isolate validators** in unit tests to verify individual behavior

---

## AI Assistant Best Practices

### When Analyzing Code

1. **Start with entry point**: Trace from `main()` → Controller → Validator
2. **Understand the chain**: Know validator execution order
3. **Read tests first**: Tests reveal expected behavior
4. **Check utility functions**: Shared logic is in `Utility.java`

### When Making Changes

1. **Use TodoWrite tool** for multi-step tasks
2. **Read before editing**: Always read file before using Edit tool
3. **Run tests after changes**: `./mvnw test`
4. **Test manually**: Use curl to verify API behavior
5. **Commit incrementally**: Don't batch unrelated changes
6. **Update documentation**: Keep README.md and this file current

### When Adding Features

1. **Follow existing patterns**: Match current code style
2. **Add tests first** (TDD approach recommended)
3. **Update validator chain** in `ChainofValidators.java`
4. **Document validation rules** in comments and this file
5. **Test with all trade types**: Spot, Forward, VanillaOption

### When Fixing Bugs

1. **Reproduce the bug**: Create failing test first
2. **Isolate the validator**: Which one has the issue?
3. **Check utility functions**: Bug might be in shared code
4. **Fix and verify**: Ensure test passes
5. **Add regression test**: Prevent future reoccurrence

### Communication

- **Be concise**: This is a CLI environment
- **Avoid emojis**: Unless explicitly requested
- **Use file paths with line numbers**: e.g., `Validator.java:25`
- **Provide context**: Explain "why" not just "what"

---

## Quick Reference

### File Locations

| What | Where |
|------|-------|
| REST Controller | `src/main/java/.../controller/CreditSuisseRestController.java` |
| Validator Facade | `src/main/java/.../Validator/Validator.java` |
| Validator Chain | `src/main/java/.../Validator/ChainofValidators.java` |
| Validator Interface | `src/main/java/.../Validator/IValidator.java` |
| All Validators | `src/main/java/.../Validator/*Validator.java` (7 files) |
| Utilities | `src/main/java/.../util/Utility.java` |
| Tests | `src/test/java/.../KafkaCreditSuisseApplicationTests.java` |
| Config | `src/main/resources/application.properties` |
| Dependencies | `pom.xml` |

### Command Reference

| Task | Command |
|------|---------|
| Start app | `./mvnw spring-boot:run` |
| Run tests | `./mvnw test` |
| Build JAR | `./mvnw clean package` |
| Compile only | `./mvnw compile` |
| Clean build | `./mvnw clean install` |
| Test API | `curl -X POST http://localhost:9090/validatetrades -H "Content-Type: text/plain" -d '[{...}]'` |

### Key Classes

| Class | Purpose | When to Modify |
|-------|---------|----------------|
| `KafkaCreditSuisseApplication` | Entry point | Rarely (Spring config changes) |
| `CreditSuisseRestController` | REST API | Adding endpoints, changing I/O |
| `Validator` | Validation facade | Changing validation initialization |
| `ChainofValidators` | Orchestrator | Adding/removing validators |
| `IValidator` | Interface | Adding validator parameters |
| `*Validator` classes | Business rules | Changing validation logic |
| `Utility` | Helper functions | Adding shared utilities |

### Validation Rules Summary

**All Trades:**
- Customer must be PLUTO1 or PLUTO2

**Spot/Forward:**
- valueDate > tradeDate
- valueDate not on weekend

**VanillaOption:**
- All above, plus:
- payCcy & premiumCcy valid ISO 4217
- style is AMERICAN or EUROPEAN
- expiryDate < deliveryDate
- premiumDate < deliveryDate
- (American only) tradeDate < excerciseStartDate < expiryDate

---

## Revision History

| Date | Changes | Updated By |
|------|---------|------------|
| 2025-11-13 | Initial comprehensive CLAUDE.md creation | Claude (AI Assistant) |

---

**End of CLAUDE.md**

For questions about this guide, refer to the code directly or test hypotheses with unit tests.
