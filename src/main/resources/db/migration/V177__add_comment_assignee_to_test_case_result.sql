ALTER TABLE quality_test_case_result
    ADD COLUMN IF NOT EXISTS comment TEXT,
    ADD COLUMN IF NOT EXISTS assignee_id UUID;
