# Architecture Decision Records (ADR)

## ADR 001: Modular Monolith Architecture
* **Decision**: Adopt a Modular Monolith over Microservices for MVP.
* **Rationale**: Speeds up development, simplifies transactions and deployments, while maintaining clean feature boundaries to allow microservice extraction if needed later.

## ADR 002: PostgreSQL as Persistence Source of Truth
* **Decision**: PostgreSQL is the single source of truth for persistent data and secret envelopes.
* **Rationale**: ACID compliance, rich query capabilities, and proven reliability.

## ADR 003: Redis strictly for Non-Authoritative Data
* **Decision**: Redis must never store authoritative secrets.
* **Rationale**: Cache eviction or Redis cluster restart must not lead to secret data loss.
