CREATE INDEX IF NOT EXISTS idx_app_registry_component_name_trgm
    ON app_registry_component USING GIN (name gin_trgm_ops);
