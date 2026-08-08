-- Flyway Initial Database Migration Placeholder
-- Core domain tables will be defined in subsequent feature milestone migrations.

CREATE TABLE IF NOT EXISTS schema_initialization_marker (
    id VARCHAR(36) PRIMARY KEY,
    initialized_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);
