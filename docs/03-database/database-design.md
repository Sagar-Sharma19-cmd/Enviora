# Database Design Specification

> **Status**: Schema Specification

## Planned Database Tables

- `users`: User account credentials, names, statuses.
- `organizations`: Organization tenant entities.
- `organization_members`: Junction table mapping users to org roles (`OWNER`, `ADMIN`, `MEMBER`, `VIEWER`).
- `invitations`: Pending organization member invites.
- `projects`: Application workspaces per organization.
- `environments`: Environment tiers (`dev`, `staging`, `prod`) per project.
- `secrets`: Secret metadata containers.
- `secret_versions`: Encrypted secret payloads with version sequence numbers.
- `audit_logs`: Immutable security audit events.
- `refresh_tokens`: Active user refresh tokens for session management.

Migrations are managed via **Flyway** in `apps/api/src/main/resources/db/migration/`.
