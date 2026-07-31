ALTER TABLE requirements_requirement
    ADD COLUMN IF NOT EXISTS requires_use_case VARCHAR(10) NOT NULL DEFAULT 'AUTO';
