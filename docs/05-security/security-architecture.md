# Security Architecture Overview

- **Authentication**: Password authentication with BCrypt hashing + Google OAuth 2.0 / OpenID Connect.
- **Authorization**: Server-side role checks at controller & service layers.
- **Secret Encryption**: AES-256-GCM Envelope encryption.
- **Zero Plaintext Logs**: Explicit log filtering and zero secret/token logging policy.

---

## 1. Password Hashing Architecture

Enviora strictly forbids plaintext password storage. User passwords are processed through Spring Security's `BCryptPasswordEncoder` before database persistence.

```
Plaintext Password (Request)
        ↓
BCrypt PasswordEncoder (Spring Security)
        ↓
60-character BCrypt Hash ($2a$10$...)
        ↓
PostgreSQL Persistence (users.password_hash)
```

- **Salt**: Automatically generated per-password salt embedded within the BCrypt string.
- **Cost Factor**: Default strength 10 (adaptive work factor).
- **Leak Protection**: Password hashes are stripped from all API response DTOs (`UserResponse`).

---

## 2. Email Verification Architecture

Email verification proves control of the registered email address. **Email verification is NOT Multi-Factor Authentication (MFA)**.

```
User Registration
        ↓
User Status: PENDING_VERIFICATION
        v
Generate Raw Token (SecureRandom 256-bit) ──> Sent in Email Link
        ↓
Compute SHA-256 Hash
        ↓
Store Token Hash in PostgreSQL (email_verification_tokens)
        ↓
User clicks link ──> SHA-256(incoming token) == DB token_hash?
        ↓
Status: ACTIVE (Atomically marked used_at = NOW)
```

### Security Principles:

1. **Cryptographic Entropy**: Generated using `java.security.SecureRandom` (32 bytes / 64-char hex, ~256-bit entropy).
2. **At-Rest Hashing**: Only the **SHA-256 hash** (`token_hash`) is stored in the database. Raw tokens exist only temporarily during email generation.
3. **Zero-Token Logging Policy**: Raw verification tokens and full verification URLs containing tokens are strictly forbidden from application logs, stack traces, database columns, and audit logs.
4. **Single-Use & Invalidation**: Tokens are single-use (`used_at`). Requesting a new verification email automatically invalidates previous unused tokens for that user.
5. **Configurable Short Expiration**: Default 15 minutes (`AUTH_EMAIL_VERIFICATION_EXPIRATION_MINUTES=15`).
6. **Rate Limiting**: Resend verification requests are rate-limited via `RateLimiterService` (3 requests per 15 min per email) and return generic safe responses to prevent email enumeration.
7. **Login Gatekeeper**: Unverified accounts (`PENDING_VERIFICATION`) are blocked from obtaining JWT access tokens (`401 Unauthorized`).

---

## 3. Google OAuth & Provider Identity Model

```
Password Authentication
        +
Google OAuth 2.0 / OIDC
        ↓
Unified Enviora Identity (auth_identities table)
        ↓
Enviora Authorization
        ↓
Enviora Access Token (JWT)
```

### Security & Architecture Rules:

1. **Google Identity ≠ Enviora Authorization**: Google authenticates external identity. Enviora remains the authoritative source of identity, roles, permissions, and JWT access tokens.
2. **Immutable Subject Identifier (`sub`)**: User matching relies strictly on Google's permanent subject ID (`sub`), NOT mutable email strings.
3. **Strict Anti-Auto-Linking Policy**:
   - If a Google identity does NOT exist, but an Enviora password account ALREADY exists with the same email, **accounts are NEVER silently merged**.
   - The user is prompted to sign in with their password first to prevent pre-hijacking attacks.
4. **OAuth-Only Accounts**: OAuth users have `password_hash = NULL`. Fake or dummy password hashes are strictly forbidden.
5. **Unified Token System**: Enviora issues its standard signed JWT access token upon successful OAuth completion. Google access tokens are **never stored** and **never returned** to the frontend.
6. **Backend Credential Isolation**: Google `CLIENT_SECRET` is kept strictly within backend environment variables.
