-- ============================================================
-- V16: IAM Role Assignment
-- Table: iam_role_assignment
-- Assigns a role to a user, optionally scoped to a workspace.
-- A user+role pair can appear once per workspace (NULL workspace = global).
-- ============================================================

CREATE TABLE iam_role_assignment
(
    id           UUID         NOT NULL,
    user_id      UUID         NOT NULL,
    role_id      UUID         NOT NULL,
    workspace_id UUID,
    assigned_by  UUID,
    assigned_at  TIMESTAMP    NOT NULL,
    status       VARCHAR(50)  NOT NULL,
    created_at   TIMESTAMP    NOT NULL,
    updated_at   TIMESTAMP    NOT NULL,
    created_by   VARCHAR(100),
    updated_by   VARCHAR(100),

    CONSTRAINT pk_iam_role_assignment PRIMARY KEY (id),
    CONSTRAINT fk_iam_role_assignment_role
        FOREIGN KEY (role_id) REFERENCES iam_role (id)
);

-- Unique: one role assignment per user+role+workspace (NULL-safe via partial index).
CREATE UNIQUE INDEX uq_iam_role_assignment_user_role_workspace
    ON iam_role_assignment (user_id, role_id, workspace_id)
    WHERE workspace_id IS NOT NULL;

CREATE UNIQUE INDEX uq_iam_role_assignment_user_role_global
    ON iam_role_assignment (user_id, role_id)
    WHERE workspace_id IS NULL;

CREATE INDEX idx_iam_role_assignment_user_id      ON iam_role_assignment (user_id);
CREATE INDEX idx_iam_role_assignment_role_id      ON iam_role_assignment (role_id);
CREATE INDEX idx_iam_role_assignment_workspace_id ON iam_role_assignment (workspace_id);
CREATE INDEX idx_iam_role_assignment_status       ON iam_role_assignment (status);
