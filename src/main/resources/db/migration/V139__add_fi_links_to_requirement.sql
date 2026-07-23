ALTER TABLE requirements_requirement
    ADD COLUMN IF NOT EXISTS functional_item_id     UUID REFERENCES app_functional_item(id) ON DELETE SET NULL,
    ADD COLUMN IF NOT EXISTS non_functional_item_id UUID REFERENCES app_non_functional_item(id) ON DELETE SET NULL;

CREATE INDEX IF NOT EXISTS idx_req_functional_item ON requirements_requirement(functional_item_id) WHERE functional_item_id IS NOT NULL;
CREATE INDEX IF NOT EXISTS idx_req_non_functional_item ON requirements_requirement(non_functional_item_id) WHERE non_functional_item_id IS NOT NULL;
