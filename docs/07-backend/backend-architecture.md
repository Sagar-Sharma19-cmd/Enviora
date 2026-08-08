# Backend Architecture Overview

- **Framework**: Spring Boot 3.4 / Java 21.
- **Security**: Spring Security filter chain with JWT Bearer Token validation.
- **Persistence**: Spring Data JPA + Hibernate + PostgreSQL.
- **Caching**: Spring Data Redis (supplementary).
- **Structure**: Modular Monolith organized by domain under `com.enviora.<feature>`.
