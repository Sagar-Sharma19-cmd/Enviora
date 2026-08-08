# Production Deployment Guide (Target State)

- **Frontend**: Next.js deployed via Docker or Node runtime behind reverse proxy.
- **Backend**: Spring Boot JAR packaged into minimal container image.
- **Database**: Managed PostgreSQL instance with SSL enabled.
- **Cache**: Managed Redis cluster with password protection.
