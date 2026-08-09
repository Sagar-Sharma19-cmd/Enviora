-- V4__create_auth_identities.sql
-- Migration for Google OAuth & Provider Identity Model

ALTER TABLE users ALTER COLUMN password_hash DROP NOT NULL;

CREATE TABLE IF NOT EXISTS auth_identities (
    id UUID NOT NULL PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    provider VARCHAR(50) NOT NULL,
    provider_user_id VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_provider_provider_user_id UNIQUE (provider, provider_user_id)
);

CREATE INDEX IF NOT EXISTS idx_auth_identities_provider_user ON auth_identities(provider, provider_user_id);
CREATE INDEX IF NOT EXISTS idx_auth_identities_user_id ON auth_identities(user_id);
