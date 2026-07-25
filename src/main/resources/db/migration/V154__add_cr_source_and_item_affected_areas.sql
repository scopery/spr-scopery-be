-- §3: Source provenance on change_request
ALTER TABLE change_request
    ADD COLUMN IF NOT EXISTS source_type    VARCHAR(50),
    ADD COLUMN IF NOT EXISTS source_id      UUID,
    ADD COLUMN IF NOT EXISTS source_subtype VARCHAR(50),
    ADD COLUMN IF NOT EXISTS source_code    VARCHAR(200),
    ADD COLUMN IF NOT EXISTS source_title   VARCHAR(500);

-- §1.3B: Affected areas on change_request_item
ALTER TABLE change_request_item
    ADD COLUMN IF NOT EXISTS affected_areas JSONB;

-- §1.3A: Index for functional item search (code + title)
CREATE INDEX IF NOT EXISTS idx_functional_item_project_code  ON app_functional_item(project_id, code);
CREATE INDEX IF NOT EXISTS idx_functional_item_project_title ON app_functional_item(project_id, title);
