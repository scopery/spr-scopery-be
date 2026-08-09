ALTER TABLE external_portal_audit_log
    ADD COLUMN IF NOT EXISTS updated_at  TIMESTAMPTZ,
    ADD COLUMN IF NOT EXISTS created_by  VARCHAR(100),
    ADD COLUMN IF NOT EXISTS updated_by  VARCHAR(100);
