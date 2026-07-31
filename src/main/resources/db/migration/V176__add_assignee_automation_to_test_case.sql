ALTER TABLE quality_test_case
    ADD COLUMN IF NOT EXISTS assignee_id UUID,
    ADD COLUMN IF NOT EXISTS automation_status VARCHAR(20) NOT NULL DEFAULT 'MANUAL'
        CHECK (automation_status IN ('MANUAL', 'PLANNED', 'AUTOMATED'));
