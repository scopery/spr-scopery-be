-- V160: Link requirements to scope packages (many requirements → one package)
ALTER TABLE requirements_requirement
    ADD COLUMN IF NOT EXISTS scope_package_id UUID;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint WHERE conname = 'fk_req_scope_package'
    ) THEN
        ALTER TABLE requirements_requirement
            ADD CONSTRAINT fk_req_scope_package
            FOREIGN KEY (scope_package_id) REFERENCES scope_package(id) ON DELETE SET NULL;
    END IF;
END $$;

CREATE INDEX IF NOT EXISTS idx_req_scope_package
    ON requirements_requirement(scope_package_id)
    WHERE scope_package_id IS NOT NULL;
