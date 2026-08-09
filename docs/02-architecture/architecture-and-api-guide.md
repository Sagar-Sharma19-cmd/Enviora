# Enviora — Architecture, API & Database Guide

Welcome to the comprehensive architecture guide for **Enviora**. This document explains how the repository is structured, how the backend API layers work together, how data flows from the UI to the database, and how to inspect your live database records.

---

## 1. How to View Your Database Tables

Your local development database uses H2 configured with file persistence (`./target/enviora_local_db`). All registered users stay saved even if you restart the application.

### Step-by-Step Instructions

1. Start the API server:
   ```bash
   cd apps/api
   mvn spring-boot:run -Dspring-boot.run.profiles=local
   ```
2. Open your browser and go to:
   👉 **`http://localhost:8080/api/v1/h2-console`**

3. Enter these connection settings on the login screen:

   | Field | Setting |
   | --- | --- |
   | **Driver Class** | `org.h2.Driver` |
   | **JDBC URL** | `jdbc:h2:file:./target/enviora_local_db` |
   | **User Name** | `sa` |
   | **Password** | *(leave completely blank)* |

4. Click **Connect**.

5. In the SQL Editor on the left or top panel, run this SQL query:
   ```sql
   SELECT id, name, email, status, password_hash, created_at FROM USERS;
   ```

6. Click **Run**. You will see a table displaying all your registered users, their UUIDs, active statuses, and encrypted BCrypt password hashes!

---

## 2. Monorepo Repository Structure

Enviora uses an **npm workspace monorepo** containing both the Next.js frontend and the Spring Boot backend.

```
Enviora/
│
├── apps/
│   ├── web/                     # Next.js 16 App Router Frontend
│   └── api/                     # Spring Boot 3.4 Java 21 Backend API
│
├── packages/                    # Monorepo Shared Workspace Packages
│   ├── ui/                      # Shared UI primitives
│   ├── types/                   # Shared TypeScript contracts
│   └── config/                  # Shared tsconfig configurations
│
├── docs/                        # Project & Architecture Documentation
│   ├── 01-product/              # Product PRD
│   ├── 02-architecture/         # High & Low level architecture guides
│   ├── 04-api/                  # REST API Specification
│   └── 05-security/             # Security Architecture & Hashing
│
├── docker-compose.yml           # PostgreSQL 16 & Redis 7 container config
├── AGENTS.md                    # Engineering contract for AI coding agents
└── README.md                    # Root overview & setup commands
```

---

## 3. Frontend Architecture (`apps/web/`)

The web dashboard is built using **Next.js 16 (App Router)**, **TypeScript (Strict Mode)**, **Tailwind CSS**, **React Hook Form**, and **Zod**.

### Key Directories

```
apps/web/
├── app/                         # Next.js App Router Pages
│   ├── layout.tsx               # Root HTML layout with providers
│   ├── page.tsx                 # Landing page
│   ├── (auth)/                  # Auth route group (hidden from URL path)
│   │   ├── login/page.tsx       # Sign-In UI Form
│   │   └── register/page.tsx    # Registration UI Form
│   └── (dashboard)/             # Protected Dashboard route group
│       ├── dashboard/page.tsx   # Overview dashboard
│       └── organizations/       # Organizations view
│
├── features/                    # Feature-First Business Modules
│   └── auth/
│       └── schemas/             # Zod validation schemas
│           ├── register-schema.ts
│           └── login-schema.ts
│
├── lib/
│   ├── api/api-client.ts        # Centralized fetch wrapper with base URL & CORS error handling
│   └── utils/cn.ts              # Tailwind class merging utility
```

---

## 4. Backend Feature-First Architecture (`apps/api/`)

The backend is built with **Spring Boot 3.4** and **Java 21**. It follows a strict **Feature-First Modular Monolith** pattern.

Instead of dumping all controllers into one folder and all services into another, code is grouped into domain feature packages under `com.enviora`.

### Base Package: `com.enviora`

```
apps/api/src/main/java/com/enviora/
│
├── auth/                        # Authentication Feature Domain
│   ├── controller/
│   │   └── AuthController.java  # REST API Endpoints (/auth/register, /auth/login)
│   ├── service/
│   │   └── AuthService.java     # Registration & Login Business Logic
│   └── dto/
│       ├── RegisterRequest.java # Registration input validation DTO
│       ├── LoginRequest.java    # Login input validation DTO
│       ├── UserResponse.java    # Safe user output DTO (no passwords)
│       └── LoginResponse.java   # Token + User output DTO
│
├── user/                        # User Feature Domain
│   ├── entity/
│   │   ├── User.java            # JPA Database Entity mapped to 'users' table
│   │   └── UserStatus.java      # ACTIVE / INACTIVE / SUSPENDED enum
│   └── repository/
│       └── UserRepository.java  # Spring Data JPA database access interface
│
└── shared/                      # Global Cross-Cutting Infrastructure
    ├── security/
    │   ├── SecurityConfig.java  # Spring Security, CORS, BCrypt bean
    │   ├── JwtService.java      # JWT Token generation & parsing
    │   └── JwtAuthenticationFilter.java # Bearer token filter
    └── exception/
        ├── GlobalExceptionHandler.java # Centralized REST exception handler
        ├── ApiException.java    # Custom application exception
        └── ErrorResponse.java   # Standard JSON error payload format
```

---

## 5. End-to-End Execution Flow

Here is how data flows through the application when a user signs up or signs in:

```
[Browser / Web UI]
       │
       │  1. HTTP POST Request (JSON)
       v
[AuthController] (@RestController)
       │
       │  2. Triggers Bean Validation (@Valid)
       │  3. Delegates to Service
       v
[AuthService] (@Service)
       │
       │  4. Normalizes Email (lowercase & trim)
       │  5. Encrypts Password (BCryptPasswordEncoder)
       │  6. Generates JWT Token (JwtService)
       v
[UserRepository] (Spring Data JPA)
       │
       │  7. Executes SQL queries
       v
[H2 / PostgreSQL Database]
```

### 1. User Registration Flow (`POST /api/v1/auth/register`)

1. **User inputs form** on `http://localhost:3000/register`.
2. **Client Zod Validation**: `registerSchema` verifies email format and minimum 8-character password before sending request.
3. **HTTP Request**: `apiClient` sends `POST http://localhost:8080/api/v1/auth/register`.
4. **Controller**: `AuthController.register()` receives `@Valid @RequestBody RegisterRequest`.
5. **Server Validation**: If fields are missing/invalid, `GlobalExceptionHandler` intercepts and returns `400 Bad Request`.
6. **Service Logic**: `AuthService.register()`:
   * Normalizes email: `sagar@example.com`.
   * Checks duplicate: `userRepository.existsByEmail("sagar@example.com")`. If true, throws `ApiException` (`409 Conflict`).
   * Hashes password: `BCryptPasswordEncoder.encode("Password123!")` → `$2a$10$e8wF...`.
   * Creates `User` entity and saves via `userRepository.save(user)`.
7. **Response**: Returns `201 Created` with `UserResponse` (`id`, `name`, `email`, `status`). **Password is never returned**.

### 2. User Sign-In Flow (`POST /api/v1/auth/login`)

1. **User inputs credentials** on `http://localhost:3000/login`.
2. **HTTP Request**: `apiClient` sends `POST http://localhost:8080/api/v1/auth/login`.
3. **Controller**: `AuthController.login()` receives `@Valid @RequestBody LoginRequest`.
4. **Service Logic**: `AuthService.login()`:
   * Finds user: `userRepository.findByEmail(email)`. If missing, throws `401 Unauthorized`.
   * Checks status: Verifies status is `ACTIVE`.
   * Verifies password: `passwordEncoder.matches(plaintextPassword, user.getPasswordHash())`. If mismatch, throws `401 Unauthorized`.
   * Generates JWT: `jwtService.generateToken(user.getEmail())`.
5. **Response**: Returns `200 OK` with `LoginResponse` containing `token` and `user`.
6. **Client Handling**: Next.js stores `token` in `localStorage` and redirects user to `/dashboard`.

---

## 6. Non-Negotiable Security Principles Applied

1. **Zero Plaintext Passwords**: Passwords are hashed immediately with BCrypt. Plaintext passwords are never stored in the database.
2. **Zero Password Exposure**: Response DTOs (`UserResponse`, `LoginResponse`) never contain `password` or `passwordHash` fields.
3. **Zero Secret Logging**: Plaintext passwords and JWT secrets are never printed in application logs or stack traces.
4. **Uniform Authorization Errors**: Invalid email and invalid password both return `"Invalid email or password"` (`401 Unauthorized`) to prevent account enumeration attacks.
