# High Level Architecture Design

> **Status**: Architecture Specification

Enviora follows a **Modular Monolith** pattern. The frontend and backend are decoupled and independently deployable.

```
+-------------------------------------------------------------+
|                      Client Layer                           |
|       Next.js 16 Web Dashboard  /  Future CLI               |
+-------------------------------------------------------------+
                              | HTTPS / REST
                              v
+-------------------------------------------------------------+
|                   Spring Boot API Gateway                   |
|          Spring Security JWT Auth / CORS / Validation       |
+-------------------------------------------------------------+
                              |
       +----------------------+----------------------+
       |                      |                      |
       v                      v                      v
+--------------+      +--------------+      +--------------+
| Auth & User  |      | Org & Project|      | Secret Engine|
|   Module     |      |   Module     |      |   Module     |
+--------------+      +--------------+      +--------------+
                              |                      |
                              v                      v
                      +--------------+      +--------------+
                      | Audit Log    |      | Redis Cache  |
                      |   Module     |      | (Rate Limit) |
                      +--------------+      +--------------+
                              |
                              v
                   +--------------------+
                   | PostgreSQL 16 DB   |
                   | (Authoritative)    |
                   +--------------------+
```
