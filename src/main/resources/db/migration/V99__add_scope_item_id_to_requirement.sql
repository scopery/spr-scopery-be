ALTER TABLE requirements_requirement
    ADD COLUMN IF NOT EXISTS scope_item_id UUID;
