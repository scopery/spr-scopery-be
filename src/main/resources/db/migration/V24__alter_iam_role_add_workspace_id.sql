ALTER TABLE iam_role
    ADD COLUMN IF NOT EXISTS workspace_id UUID;
