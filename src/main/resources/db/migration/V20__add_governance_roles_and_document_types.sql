-- Phase 5: Platform Governance — IAM role governance columns + Knowledge document type table

-- ── 1. Alter iam_role ─────────────────────────────────────────────────────────────────────────

ALTER TABLE iam_role
    ADD COLUMN role_scope  VARCHAR(50),
    ADD COLUMN role_source VARCHAR(50),
    ADD COLUMN parent_role_id UUID,
    ADD COLUMN deleted_at  TIMESTAMP,
    ADD COLUMN deleted_by  UUID;

ALTER TABLE iam_role
    ADD CONSTRAINT fk_iam_role_parent_role
        FOREIGN KEY (parent_role_id) REFERENCES iam_role (id);

CREATE INDEX idx_iam_role_role_scope     ON iam_role (role_scope);
CREATE INDEX idx_iam_role_role_source    ON iam_role (role_source);
CREATE INDEX idx_iam_role_parent_role_id ON iam_role (parent_role_id);
CREATE INDEX idx_iam_role_deleted_at     ON iam_role (deleted_at);

-- ── 2. Create knowledge_document_type ────────────────────────────────────────────────────────

CREATE TABLE knowledge_document_type (
    id              UUID         NOT NULL,
    code            VARCHAR(100) NOT NULL,
    name            VARCHAR(255) NOT NULL,
    description     TEXT,
    document_scope  VARCHAR(50)  NOT NULL,
    status          VARCHAR(50)  NOT NULL DEFAULT 'ACTIVE',
    workspace_id    UUID,
    deleted_at      TIMESTAMP,
    deleted_by      UUID,
    created_at      TIMESTAMP    NOT NULL,
    updated_at      TIMESTAMP    NOT NULL,
    created_by      VARCHAR(255) NOT NULL DEFAULT 'SYSTEM',
    updated_by      VARCHAR(255) NOT NULL DEFAULT 'SYSTEM',

    CONSTRAINT pk_knowledge_document_type PRIMARY KEY (id)
);

-- Unique code per scope: system-scoped codes must be globally unique (workspace_id IS NULL),
-- workspace-scoped codes must be unique within the workspace.
CREATE UNIQUE INDEX uq_knowledge_document_type_system_code
    ON knowledge_document_type (code)
    WHERE workspace_id IS NULL AND deleted_at IS NULL;

CREATE UNIQUE INDEX uq_knowledge_document_type_workspace_code
    ON knowledge_document_type (workspace_id, code)
    WHERE workspace_id IS NOT NULL AND deleted_at IS NULL;

CREATE INDEX idx_knowledge_document_type_status       ON knowledge_document_type (status);
CREATE INDEX idx_knowledge_document_type_scope        ON knowledge_document_type (document_scope);
CREATE INDEX idx_knowledge_document_type_workspace_id ON knowledge_document_type (workspace_id);
CREATE INDEX idx_knowledge_document_type_deleted_at   ON knowledge_document_type (deleted_at);
