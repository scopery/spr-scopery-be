ALTER TABLE app_functional_item ADD COLUMN IF NOT EXISTS module_id UUID REFERENCES app_registry_module(id) ON DELETE SET NULL;
CREATE INDEX IF NOT EXISTS idx_app_functional_item_module ON app_functional_item(module_id) WHERE module_id IS NOT NULL;
