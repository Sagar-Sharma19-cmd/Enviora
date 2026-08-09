# REST API Specification Outline

Base Path: `/api/v1`

## Authentication Endpoints

### 1. Register User
`POST /api/v1/auth/register`

Registers a new user account on the Enviora platform with `PENDING_VERIFICATION` status and triggers an email verification link.

#### Request Body
```json
{
  "name": "Sagar Sharma",
  "email": "sagar@example.com",
  "password": "StrongPassword123"
}
```

#### Validation Rules
- `name`: Required, non-blank string.
- `email`: Required, valid email format, normalized to lowercase. Must be unique.
- `password`: Required, minimum 8 characters.

#### Response Body (201 Created)
```json
{
  "id": "c56a4180-65aa-42ec-a945-5fd21dec0538",
  "name": "Sagar Sharma",
  "email": "sagar@example.com",
  "status": "PENDING_VERIFICATION",
  "createdAt": "2026-08-08T14:46:35.319Z"
}
```

#### Error Responses
- `400 Bad Request`: Validation failure.
- `409 Conflict`: Account with this email address already exists.

---

### 2. Verify Email
`GET /api/v1/auth/verify-email?token={token}`

Verifies an account using a high-entropy single-use verification token.

#### Query Parameters
- `token`: Required. 64-character hex verification token string.

#### Response Body (200 OK)
```json
{
  "message": "Email address verified successfully. You may now sign in."
}
```

#### Error Responses
- `400 Bad Request`: Token missing, invalid, expired, or already consumed.

---

### 3. Resend Verification Link
`POST /api/v1/auth/resend-verification`

Resends a new verification email to the user if an unverified account exists. Rate-limited to 3 requests per 15 minutes.

#### Request Body
```json
{
  "email": "sagar@example.com"
}
```

#### Response Body (200 OK)
```json
{
  "message": "If an unverified account exists for this email address, a new verification link has been sent."
}
```

#### Error Responses
- `429 Too Many Requests`: Rate limit exceeded.

---

### 4. User Login
`POST /api/v1/auth/login`

Authenticates an active user account and returns a signed JWT access token.

#### Request Body
```json
{
  "email": "sagar@example.com",
  "password": "StrongPassword123"
}
```

#### Response Body (200 OK)
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "user": {
    "id": "c56a4180-65aa-42ec-a945-5fd21dec0538",
    "name": "Sagar Sharma",
    "email": "sagar@example.com",
    "status": "ACTIVE"
  }
}
```

#### Error Responses
- `401 Unauthorized`: Invalid email or password, OR email verification required (`PENDING_VERIFICATION`).

---

### 5. Google OAuth 2.0 Authorization Endpoint
`GET /api/v1/auth/oauth2/google`

Initiates the Google OAuth 2.0 / OpenID Connect authorization code flow by redirecting the browser to Google's OAuth consent screen.

#### Redirection Flow
```
GET /api/v1/auth/oauth2/google
  ↓ (302 Redirect to accounts.google.com)
Google User Consent
  ↓ (Redirect to /api/v1/auth/oauth2/code/google?code=...&state=...)
Backend Token Exchange & User Matching
  ↓ (302 Redirect to frontend /login/oauth/callback?token={jwt})
Next.js Application
```

#### Callback Parameters (Frontend Redirect)
- **Success**: `GET /login/oauth/callback?token={enviora_jwt}`
- **Account Conflict**: `GET /login/oauth/callback?error=account_exists` (Password account exists with same email; auto-linking blocked)
- **User Cancelled / Error**: `GET /login/oauth/callback?error=oauth_denied`
