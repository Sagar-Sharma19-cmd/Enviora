# CI/CD Workflow Specification

- `.github/workflows/ci.yml` runs on push and PR to `main`.
- Jobs:
  - `frontend-ci`: Node setup, dependency install, linting, typechecking, building `apps/web`.
  - `backend-ci`: JDK 21 setup, Maven test, Maven compilation of `apps/api`.
