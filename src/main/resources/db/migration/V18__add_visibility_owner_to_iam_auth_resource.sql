-- ============================================================
-- V18: Add ownership and visibility columns to iam_auth_resource.
-- ref_id: UUID of the actual domain object this resource represents
-- owner_user_id: IAM user who owns this resource
-- workspace_id: workspace boundary for this resource
-- visibility: PRIVATE | WORKSPACE | SPACE | PUBLIC_LINK | RESTRICTED
-- parent_resource_id: optional parent for hierarchical resources
-- ============================================================

ALTER TABLE iam_auth_resource
    ADD COLUMN ref_id            UUID,
    ADD COLUMN owner_user_id     UUID,
    ADD COLUMN workspace_id      UUID,
    ADD COLUMN visibility        VARCHAR(100),
    ADD COLUMN parent_resource_id UUID;

CREATE INDEX idx_iam_auth_resource_ref_id        ON iam_auth_resource (ref_id);
CREATE INDEX idx_iam_auth_resource_owner         ON iam_auth_resource (owner_user_id);
CREATE INDEX idx_iam_auth_resource_workspace_id  ON iam_auth_resource (workspace_id);
CREATE INDEX idx_iam_auth_resource_parent        ON iam_auth_resource (parent_resource_id);
