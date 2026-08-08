# Indexing Strategy Specification

## Planned Database Indexes

- `users(email)`: Unique index for fast authentication lookup.
- `organization_members(organization_id, user_id)`: Unique compound index.
- `projects(organization_id, name)`: Unique index ensuring unique project names per org.
- `environments(project_id, slug)`: Unique index per environment tier.
- `secrets(environment_id, key)`: Unique index preventing duplicate secret keys in an environment.
- `audit_logs(organization_id, created_at)`: B-tree index for efficient timeline queries.
