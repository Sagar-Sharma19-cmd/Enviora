# Security Architecture Overview

- **Authentication**: Stateless JWT access tokens + HTTP-only secure refresh tokens.
- **Authorization**: Server-side role checks at controller & service layers.
- **Secret Encryption**: AES-256-GCM Envelope encryption.
- **Zero Plaintext Logs**: Explicit log filtering and zero secret logging policy.
