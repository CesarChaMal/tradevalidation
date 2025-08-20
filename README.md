# Trade Validation System

A Spring Boot REST API for validating financial trade data with comprehensive validation rules.

## Developer
**Touraj Ebrahimi** - Senior Java Developer  
Twitter: [@toraj58](https://twitter.com/toraj58)

## Overview
Validates trade transactions against business rules including date validation, currency codes, customer verification, and option-specific constraints.

## Quick Start
```bash
mvn spring-boot:run
```
API runs on port 9090

## API Endpoint
**POST** `/validatetrades`
- Content-Type: `text/plain`
- Body: JSON array of trade objects

### Example Request
```json
[
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
]
```

## Validation Rules
- **Value Date**: Must be after trade date and not on weekends
- **Customer**: Must be PLUTO1 or PLUTO2
- **Currency**: ISO 4217 compliant (VanillaOption only)
- **Style**: AMERICAN or EUROPEAN (VanillaOption only)
- **Exercise Date**: Must be after trade date and before expiry (VanillaOption only)
- **Premium Date**: Must be after trade date (VanillaOption only)

## Tech Stack
- Spring Boot 1.5.4
- Java 8
- Maven
- JSON processing

## Build
```bash
mvn clean install
```
