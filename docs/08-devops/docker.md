# Docker & Local Infrastructure Setup

- `docker-compose.yml` runs PostgreSQL 16 on port 5432 and Redis 7 on port 6379.
- Volumes `postgres_data` and `redis_data` ensure local data persistence across container restarts.
- Health checks ensure database readiness before API connections are established.
