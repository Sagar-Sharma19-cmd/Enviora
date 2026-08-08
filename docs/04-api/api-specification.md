# REST API Specification Outline

Base Path: `/api/v1`

## Resource Paths

- `POST /api/v1/auth/register` — Register user
- `POST /api/v1/auth/login` — Authenticate and obtain JWT
- `POST /api/v1/auth/refresh` — Refresh access token
- `GET  /api/v1/organizations` — List user organizations
- `GET  /api/v1/projects/{id}` — Get project details
- `GET  /api/v1/environments/{id}/secrets` — List secret keys (metadata only)
- `GET  /api/v1/secrets/{id}` — Read decrypted secret value (Audited)
