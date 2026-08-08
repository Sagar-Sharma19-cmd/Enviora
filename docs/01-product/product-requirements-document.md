# Enviora — Product Requirements Document (PRD)

> **Document Status**: Active / Source of Truth  
> **Version**: 1.0.0  
> **Domain**: Organization → Projects → Environments → Secrets

---

## 1. Executive Summary

Enviora is a Developer Secrets Platform engineered to manage, audit, and distribute environment variables and sensitive configuration across engineering organizations. It eliminates unencrypted `.env` sharing, credentials checked into repositories, and untracked secret updates.

---

## 2. Core Domain Hierarchy

```
Organization (Tenant Boundary)
  └── Projects (Application / Service)
        └── Environments (Development, Staging, Production)
              └── Secrets (Key-Value pairs with Envelope Encryption & Versioning)
```

### Domain Entities

1. **Organization**: Multi-tenant container representing an enterprise or dev team.
2. **User & Membership**: User accounts with RBAC roles (`OWNER`, `ADMIN`, `MEMBER`, `VIEWER`).
3. **Project**: Application or microservice workspace under an Organization.
4. **Environment**: Execution tier within a Project (e.g. `dev`, `staging`, `prod`).
5. **Secret**: Encrypted key-value pair tied to a specific Environment.
6. **Secret Version**: Historical snapshot of secret values for rollbacks and compliance auditability.
7. **Audit Log**: Immutable append-only event ledger tracking all access, reads, modifications, and deletions.

---

## 3. Product Requirements Overview

### Phase 1: Authentication & Organization Foundation (Milestone 1)
- User Registration & Login (JWT Access + Refresh tokens).
- Organization Creation & Member Invitations.
- Workspace Context Switching.

### Phase 2: Project & Environment Scoping (Milestone 2)
- Project creation and environment configuration per project.
- Role-based Access Control (RBAC) per environment.

### Phase 3: Secrets Core & Envelope Encryption (Milestone 3)
- Key-Value management per environment.
- Envelope encryption at rest using master encryption keys.
- Secret versioning & rollback capabilities.
- Audit logging for all secret reads and writes.

### Phase 4: Developer Workflows & Syncing (Milestone 4)
- Web dashboard interface.
- REST API access with Service Tokens for CI/CD pipelines.

---

## 4. Non-Functional Requirements

- **Security**: Envelope encryption for all secret payloads at rest. Zero plaintext logging.
- **Performance**: Sub-50ms API response time for secret fetching.
- **Reliability**: PostgreSQL as authoritative persistence; Redis strictly for caching non-sensitive metadata and rate-limiting.
- **Compliance**: Complete audit trail for security compliance (SOC2 / ISO 27001 readiness).
