# Google OAuth 2.0 Integration & Setup Guide

This document outlines configuring Google OAuth 2.0 authentication for the **Enviora** Developer Secrets Platform.

---

## Architecture Overview

Enviora delegates identity verification to Google OAuth 2.0 / OpenID Connect (OIDC).
The Spring Boot backend acts as the OAuth Client and remains the sole authoritative source for user identity, role permissions, and JWT issuance.

```
Browser (Next.js)
  ↓ GET /api/v1/auth/oauth2/google
Spring Security OAuth2 Client
  ↓ Redirects to Google Authorization Endpoint
Google Cloud OIDC
  ↓ User Authenticates & Grants Consent
Google Callback (/api/v1/auth/oauth2/code/google)
  ↓ Spring Boot validates token & reads Google 'sub' claim
AuthIdentity Domain (auth_identities table)
  ↓ Find/Create Enviora User & issue JWT
Frontend Callback (/login/oauth/callback?token=...)
```

---

## Google Cloud Console Setup

### 1. Create a Google Cloud Project
1. Go to the [Google Cloud Console](https://console.cloud.google.com/).
2. Click **Select a Project** → **New Project**.
3. Name the project `Enviora Developer Secrets` and click **Create**.

### 2. Configure OAuth Consent Screen
1. Navigate to **APIs & Services** → **OAuth consent screen**.
2. Select User Type:
   - **Internal** (for organization workspace users) OR **External** (for general public testing).
3. Fill in mandatory app details:
   - **App name**: `Enviora`
   - **User support email**: `support@yourdomain.com`
   - **Developer contact information**: `admin@yourdomain.com`
4. Define Scopes:
   - `openid`
   - `https://www.googleapis.com/auth/userinfo.profile`
   - `https://www.googleapis.com/auth/userinfo.email`
5. Save and continue.

### 3. Create OAuth 2.0 Client Credentials
1. Navigate to **APIs & Services** → **Credentials**.
2. Click **+ Create Credentials** → **OAuth client ID**.
3. Application type: **Web application**.
4. Name: `Enviora Web & API Client`.
5. **Authorized JavaScript origins**:
   - Local: `http://localhost:3000`
   - Production: `https://app.enviora.com`
6. **Authorized redirect URIs**:
   - Local: `http://localhost:8080/api/v1/auth/oauth2/code/google`
   - Production: `https://api.enviora.com/api/v1/auth/oauth2/code/google`
7. Click **Create** and copy your **Client ID** and **Client Secret**.

---

## Environment Configuration

Set the environment variables in your environment or `.env` file:

```bash
# Google OAuth 2.0 Credentials (Backend Only)
GOOGLE_CLIENT_ID=your-google-client-id.apps.googleusercontent.com
GOOGLE_CLIENT_SECRET=your-google-client-secret
GOOGLE_REDIRECT_URI=http://localhost:8080/api/v1/auth/oauth2/code/google
```

> [!CAUTION]
> **Security Requirement**: Never expose `GOOGLE_CLIENT_SECRET` in frontend environment variables (`NEXT_PUBLIC_*`), Git repositories, or client-side JavaScript code!

---

## Local Testing Instructions

1. Start Docker containers (PostgreSQL, Mailpit, Redis):
   ```bash
   docker compose up -d
   ```
2. Run backend Spring Boot with your Google Client ID/Secret:
   ```bash
   export GOOGLE_CLIENT_ID="your-client-id.apps.googleusercontent.com"
   export GOOGLE_CLIENT_SECRET="your-client-secret"
   cd apps/api
   mvn spring-boot:run -Dspring-boot.run.profiles=local
   ```
3. Run frontend Next.js app:
   ```bash
   npm run dev:web
   ```
4. Open `http://localhost:3000/login` and click **Continue with Google**.
