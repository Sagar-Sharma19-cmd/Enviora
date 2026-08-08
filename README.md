# Enviora — Developer Secrets Platform

> **Current Status**: Early Development (Bootstrap Phase)

Enviora is a secure, developer-first Secrets Platform designed for engineering teams to manage, audit, sync, and control application environment variables and secrets across projects and deployment environments.

---

## 🚀 Product Overview

Modern engineering teams struggle with secret sprawl across `.env` files, CI/CD pipelines, cloud providers, and local environments. Enviora solves this by providing:

* **Hierarchy & Scoping**: Organization → Projects → Environments → Secrets.
* **Security & Auditability**: End-to-end secret encryption at rest, server-side RBAC, and immutable audit logs.
* **Developer Workflow**: Centralized management with web dashboard and CLI-ready infrastructure.

---

## 🏛️ Architecture & Tech Stack

Enviora is built as a **Modular Monolith** designed for developer ergonomics, security, and future scale.

```
Enviora Platform
 ├── Frontend: Next.js 16 (App Router), TypeScript, Tailwind CSS, TanStack Query
 ├── Backend: Spring Boot 3.4, Java 21, Spring Security, Spring Data JPA
 ├── Database: PostgreSQL 16 (Authoritative Data Store)
 └── Cache: Redis 7 (Rate Limiting, Cache, Short-Lived Data)
```

---

## 📁 Repository Structure

```
Enviora/
├── apps/
│   ├── web/           # Next.js 16 App Router Frontend
│   └── api/           # Spring Boot 3.4 Java 21 Backend API
├── packages/
│   ├── ui/            # Shared UI component primitives
│   ├── types/         # Shared TypeScript definitions
│   └── config/        # Shared ESLint & TypeScript configurations
├── infra/
│   ├── docker/        # Dockerfile configurations
│   ├── nginx/         # Reverse proxy configuration
│   └── monitoring/    # Prometheus & Grafana configurations
├── docs/              # Architectural, API, and Security documentation
├── scripts/           # Local development helper scripts
├── .github/
│   └── workflows/     # GitHub Actions CI workflow
├── docker-compose.yml # Local development infrastructure (PostgreSQL, Redis)
└── AGENTS.md          # Engineering rules & contract for AI coding agents
```

---

## 🛠️ Getting Started

### Prerequisites

* **Node.js**: v20+ (Node v24 recommended)
* **Java**: JDK 21+
* **Maven**: 3.9+
* **Docker & Docker Compose**

### 1. Environment Setup

Copy `.env.example` to create your local environment file:

```bash
cp .env.example .env
```

### 2. Start Local Infrastructure

Start PostgreSQL and Redis via Docker Compose:

```bash
docker compose up -d
```

Verify service status:

```bash
docker compose ps
```

### 3. Run Backend (Spring Boot API)

```bash
cd apps/api
mvn clean compile
mvn spring-boot:run
```

The API will be available at `http://localhost:8080/api/v1`.

### 4. Run Frontend (Next.js Web)

In a separate terminal:

```bash
cd apps/web
npm install
npm run dev
```

The web dashboard will be available at `http://localhost:3000`.

---

## 🔒 Security Notice

* **Secrets Handling**: Secret values are **never stored as plaintext** in PostgreSQL and are **never cached in Redis**.
* **Logging**: Secret values are strictly masked and **never printed to application logs**.
* **Reporting Vulnerabilities**: Please review our [CONTRIBUTING.md](CONTRIBUTING.md) for security vulnerability disclosure instructions.

---

## 📜 License

Enviora is open-source software licensed under the [MIT License](LICENSE).
