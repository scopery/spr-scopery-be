-- ============================================================
-- V19: Alter iam_role_assignment to support team assignees
-- Adds assignee_type / assignee_id columns, migrates user_id
-- data, drops user_id, and recreates uniqueness indexes.
-- ============================================================

-- 1. Add new columns (nullable first, fill data, then constrain)
ALTER TABLE iam_role_assignment
    ADD COLUMN assignee_type VARCHAR(50),
    ADD COLUMN assignee_id   UUID;

-- 2. Migrate existing USER rows
UPDATE iam_role_assignment
SET assignee_type = 'USER',
    assignee_id   = user_id;

-- 3. Apply NOT NULL constraints
ALTER TABLE iam_role_assignment
    ALTER COLUMN assignee_type SET NOT NULL,
    ALTER COLUMN assignee_id   SET NOT NULL;

-- 4. Drop old uniqueness indexes that referenced user_id
DROP INDEX IF EXISTS uq_iam_role_assignment_user_role_workspace;
DROP INDEX IF EXISTS uq_iam_role_assignment_user_role_global;
DROP INDEX IF EXISTS idx_iam_role_assignment_user_id;

-- 5. Drop the user_id column
ALTER TABLE iam_role_assignment DROP COLUMN user_id;

-- 6. Recreate unique partial indexes on assignee_type + assignee_id
CREATE UNIQUE INDEX uq_iam_role_assignment_assignee_role_workspace
    ON iam_role_assignment (assignee_type, assignee_id, role_id, workspace_id)
    WHERE workspace_id IS NOT NULL;

CREATE UNIQUE INDEX uq_iam_role_assignment_assignee_role_global
    ON iam_role_assignment (assignee_type, assignee_id, role_id)
    WHERE workspace_id IS NULL;

-- 7. Recreate index for assignee lookup
CREATE INDEX idx_iam_role_assignment_assignee_id ON iam_role_assignment (assignee_id);
