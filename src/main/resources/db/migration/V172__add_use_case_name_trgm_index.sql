-- pg_trgm already enabled in V159
CREATE INDEX IF NOT EXISTS idx_app_use_case_name_trgm
    ON app_use_case USING GIN (name gin_trgm_ops);
