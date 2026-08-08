# Threat Model & Mitigation Matrix

| Threat | Risk Level | Mitigation Strategy |
| --- | --- | --- |
| Database Leak | Critical | Payload encrypted with envelope encryption using external KEK. |
| Man-In-The-Middle | High | Enforce TLS in transit, secure cookies for refresh tokens. |
| Log Exfiltration | High | Zero secret logging rule enforced by code review & automated linting. |
| Unauthorized Access | High | Server-side RBAC and mandatory token validation on every request. |
