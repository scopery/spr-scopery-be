ALTER TABLE app_registry_data_entity ADD COLUMN IF NOT EXISTS module_id UUID REFERENCES app_registry_module(id) ON DELETE SET NULL;
ALTER TABLE app_registry_data_entity ALTER COLUMN table_name DROP NOT NULL;
CREATE INDEX IF NOT EXISTS idx_app_data_entity_module ON app_registry_data_entity(module_id) WHERE module_id IS NOT NULL;
