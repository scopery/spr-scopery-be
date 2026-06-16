-- ============================================================
-- V17: Add effect and scope columns to iam_access_grant.
-- effect: ALLOW | DENY (default ALLOW for backward compatibility)
-- scope_type: optional scope qualifier (RESOURCE, WORKSPACE, ...)
-- scope_ref_id: optional UUID reference for the scope
-- workspace_id: optional workspace boundary for the grant
-- ============================================================

ALTER TABLE iam_access_grant
    ADD COLUMN effect       VARCHAR(50)  NOT NULL DEFAULT 'ALLOW',
    ADD COLUMN scope_type   VARCHAR(100),
    ADD COLUMN scope_ref_id UUID,
    ADD COLUMN workspace_id UUID;

CREATE INDEX idx_iam_access_grant_effect       ON iam_access_grant (effect);
CREATE INDEX idx_iam_access_grant_workspace_id ON iam_access_grant (workspace_id);
