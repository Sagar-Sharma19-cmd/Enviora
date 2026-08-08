# AGENTS.md — Engineering Contract for AI Coding Agents

This document defines the architectural, security, code quality, and workflow rules for AI coding agents working on the **Enviora** codebase. All automated code generation, refactoring, and feature additions must strictly adhere to this contract.

---

## 1. Architecture Principles

1. **Feature-First Architecture**: Group code by feature domains (`auth`, `organization`, `project`, `secret`, `audit`, etc.) rather than technical roles (`controllers/`, `services/`).
2. **Modular Monolith**: Maintain clear boundaries between backend modules. Do not introduce cross-domain hard dependencies; use clean service interfaces or domain events.
3. **Frontend Separation**: Next.js App Router applications (`apps/web/`) must maintain feature-scoped components (`features/<feature>/components`) and avoid dumping feature logic into global component folders.
4. **Backend Boundary**: Spring Boot application (`apps/api/`) uses `com.enviora` base package with feature-first packages (`com.enviora.<feature>`) containing `controller`, `service`, `repository`, `entity`, `dto`, `mapper`, and `validator`.
5. **Persistence Source of Truth**: PostgreSQL is the sole authoritative store for persistent application state and secrets. Redis is strictly supplementary (caching, rate limiting, temporary session tokens) and **must never store authoritative secret values**.
6. **Future-Proofing**: Keep domain modules decoupled to enable future microservices extraction if scale demands it, but **do not introduce microservices prematurely**.

---

## 2. Non-Negotiable Security Rules

1. **Never Log Secret Values**:
   - `console.log(secret)` is strictly forbidden.
   - `logger.info(secret)`, `System.out.println(secret)`, or stringifying secrets into log contexts is prohibited.
2. **Never Commit Credentials**:
   - Secrets, private keys, database passwords, or JWT secrets must never be hardcoded or committed to git.
   - Always load sensitive values via environment variables.
3. **Never Store Secret Values in Plaintext**:
   - Secret values must be encrypted at rest using envelope encryption / established AES-GCM / KMS abstractions before persisting.
4. **Server-Side Authorization**:
   - Never rely on client-side routing or UI flags for authorization.
   - Every backend endpoint must enforce explicit role-based / resource-based permission checks.
5. **Never Return Plaintext Secrets in List APIs**:
   - List endpoints (`GET /api/v1/projects/{id}/secrets`) must return metadata only (name, version, created_at, updated_at). Secret values must only be decrypted on explicit, audited single-secret retrieval requests.
6. **No Custom Cryptography**:
   - Always use standard, peer-reviewed cryptographic libraries (Spring Security Crypto, Java Cryptography Architecture `javax.crypto`).

---

## 3. Code Quality & Standards

1. **TypeScript**:
   - Strict mode enabled (`"strict": true`).
   - No implicit `any`. Explicit typing required for API inputs, responses, and state hooks.
2. **Java 21**:
   - Constructor injection (prefer Lombok `@RequiredArgsConstructor` or explicit final field constructors).
   - DTOs for API boundaries (records or immutability preferred).
   - Strict validation with `@Valid` and JSR-380 annotations.
   - Centralized exception handling (`@RestControllerAdvice`).
   - Controllers must contain zero business logic or direct repository access.
3. **SOLID Principles**:
   - Keep services small, focused, and single-purpose.
   - Avoid bloated monolithic helper classes.

---

## 4. Feature Development Workflow

Before implementing any business feature, AI agents must follow this sequence:

1. **Read the PRD**: Check [docs/01-product/product-requirements-document.md](file:///Users/sagarsharma/Enviora/docs/01-product/product-requirements-document.md).
2. **Understand the Affected Feature**: Identify all affected backend and frontend components.
3. **Check Existing Code**: Search existing domain packages to avoid duplicate logic.
4. **Design Database Changes**: Write Flyway migration scripts in `apps/api/src/main/resources/db/migration/`.
5. **Design API Contracts**: Define REST endpoints, request/response DTOs, and status codes.
6. **Implement Backend**: Build entity, repository, service, DTO mapper, and controller with full validation and security checks.
7. **Implement Frontend**: Build feature components, custom hooks, TanStack Query integrations, and UI forms.
8. **Add Tests**: Include unit/integration tests for backend and typechecking/linting for frontend.
9. **Update Documentation**: Update relevant documentation files in `docs/`.

---

## 5. Development Constraints

- **Do not modify unrelated features**: Keep commits focused tightly on the requested domain.
- **Do not rewrite working code**: Refactor only when explicitly requested or required for the task.
- **Do not install dependencies without justification**: Avoid adding third-party packages unless standard library/framework capabilities are insufficient.
