# Contributing to Enviora

Thank you for contributing to **Enviora**! This document provides guidelines for contributing to the repository.

---

## 🌿 Git Branching Model

Create descriptive branch names prefixed with the relevant type:

- `feature/` — New feature implementations
- `fix/` — Bug fixes
- `refactor/` — Code restructuring without behavior changes
- `chore/` — Build, dependency, or configuration updates
- `docs/` — Documentation updates
- `security/` — Security enhancements or vulnerability fixes

### Examples

- `feature/authentication-jwt`
- `feature/secret-envelope-encryption`
- `fix/login-validation-schema`
- `chore/docker-compose-healthcheck`
- `docs/api-specification`

---

## 📝 Commit Message Format

Enviora enforces **Conventional Commits**:

```
<type>(<scope>): <short description>
```

### Allowed Types

- `feat`: A new feature
- `fix`: A bug fix
- `docs`: Documentation changes
- `refactor`: Code change that neither fixes a bug nor adds a feature
- `test`: Adding missing tests or correcting existing tests
- `chore`: Changes to build process, tooling, or helper scripts
- `ci`: Changes to CI configuration workflows
- `security`: Security patches or cryptographic updates

### Example Commit Messages

```
feat(auth): implement JWT token generation and refresh flow
fix(secret): sanitize error message on invalid encryption key
docs(api): add OpenAPI spec for environment secrets endpoint
chore(deps): update Spring Boot to 3.4.2
```

---

## 🔍 Pull Request Process

1. **Branch**: Create your branch from `main`.
2. **Code Quality**: Ensure strict TypeScript checks pass and Maven tests pass cleanly.
3. **Commit**: Write clean, conventional commit messages.
4. **Submit PR**: Open a Pull Request against `main` detailing changes, tested scenarios, and affected features.
5. **Review**: Address review comments promptly.

---

## 🔒 Security Vulnerability Disclosure

If you discover a security vulnerability within Enviora, please **do not** open a public issue. Email security report details directly to `security@enviora.dev`. All security reports will be investigated promptly.
