# Low Level Architecture Design

> **Status**: Architecture Specification

## Backend Modular Monolith Structure

All backend code resides in `apps/api/src/main/java/com/enviora/`.

Each feature module is completely self-contained:

```
com.enviora.<feature>/
├── controller/   # REST Endpoints
├── service/      # Business logic & transaction boundaries
├── repository/   # Spring Data JPA repositories
├── entity/       # JPA Entities mapped to PostgreSQL tables
├── dto/          # Input/Output DTOs
├── mapper/       # Entity <-> DTO conversion logic
└── validator/    # Domain specific validation rules
```

## Module Isolation Rules

- Direct SQL joins across module entities outside defined JPA relations are discouraged.
- Cross-module operations must go through public Service interfaces.
- DTOs are the sole data carriers across API controller boundaries.
