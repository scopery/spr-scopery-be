-- Phase 08: harden Knowledge DocumentType catalog + DocumentTypeField metadata schema

-- ── knowledge_document_type: new columns ─────────────────────────────────────
ALTER TABLE knowledge_document_type
    ADD COLUMN IF NOT EXISTS organization_id UUID,
    ADD COLUMN IF NOT EXISTS category VARCHAR(100),
    ADD COLUMN IF NOT EXISTS default_classification VARCHAR(50),
    ADD COLUMN IF NOT EXISTS default_review_cycle_days INT,
    ADD COLUMN IF NOT EXISTS default_template_code VARCHAR(150),
    ADD COLUMN IF NOT EXISTS metadata_schema_json JSONB,
    ADD COLUMN IF NOT EXISTS built_in BOOLEAN NOT NULL DEFAULT false,
    ADD COLUMN IF NOT EXISTS archived_at TIMESTAMP,
    ADD COLUMN IF NOT EXISTS archived_by UUID,
    ADD COLUMN IF NOT EXISTS version INT NOT NULL DEFAULT 0;

-- Migrate DELETED → ARCHIVED (copy soft-delete markers to archive columns)
UPDATE knowledge_document_type
SET status = 'ARCHIVED',
    archived_at = COALESCE(archived_at, deleted_at),
    archived_by = COALESCE(archived_by, deleted_by)
WHERE status = 'DELETED';

-- Default classification for existing rows
UPDATE knowledge_document_type
SET default_classification = 'INTERNAL'
WHERE default_classification IS NULL;

-- Drop old unique indexes (will recreate with archive-aware predicates)
DROP INDEX IF EXISTS uq_knowledge_document_type_system_code;
DROP INDEX IF EXISTS uq_knowledge_document_type_workspace_code;

-- Scope / status checks
ALTER TABLE knowledge_document_type
    DROP CONSTRAINT IF EXISTS chk_knowledge_document_type_scope;

ALTER TABLE knowledge_document_type
    ADD CONSTRAINT chk_knowledge_document_type_scope
        CHECK (document_scope IN ('SYSTEM', 'ORGANIZATION', 'WORKSPACE'));

ALTER TABLE knowledge_document_type
    DROP CONSTRAINT IF EXISTS chk_knowledge_document_type_status;

ALTER TABLE knowledge_document_type
    ADD CONSTRAINT chk_knowledge_document_type_status
        CHECK (status IN ('ACTIVE', 'INACTIVE', 'ARCHIVED'));

ALTER TABLE knowledge_document_type
    DROP CONSTRAINT IF EXISTS chk_knowledge_document_type_classification;

ALTER TABLE knowledge_document_type
    ADD CONSTRAINT chk_knowledge_document_type_classification
        CHECK (default_classification IS NULL OR default_classification IN (
            'PUBLIC', 'INTERNAL', 'CONFIDENTIAL', 'RESTRICTED', 'CUSTOM'
        ));

-- Partial unique indexes: scope + org + workspace + code (non-archived)
CREATE UNIQUE INDEX IF NOT EXISTS uq_knowledge_document_type_system_code
    ON knowledge_document_type (code)
    WHERE document_scope = 'SYSTEM'
      AND organization_id IS NULL
      AND workspace_id IS NULL
      AND archived_at IS NULL
      AND deleted_at IS NULL;

CREATE UNIQUE INDEX IF NOT EXISTS uq_knowledge_document_type_organization_code
    ON knowledge_document_type (organization_id, code)
    WHERE document_scope = 'ORGANIZATION'
      AND organization_id IS NOT NULL
      AND workspace_id IS NULL
      AND archived_at IS NULL
      AND deleted_at IS NULL;

CREATE UNIQUE INDEX IF NOT EXISTS uq_knowledge_document_type_workspace_code
    ON knowledge_document_type (workspace_id, code)
    WHERE document_scope = 'WORKSPACE'
      AND workspace_id IS NOT NULL
      AND archived_at IS NULL
      AND deleted_at IS NULL;

CREATE INDEX IF NOT EXISTS idx_knowledge_document_type_organization_id
    ON knowledge_document_type (organization_id);

CREATE INDEX IF NOT EXISTS idx_knowledge_document_type_archived_at
    ON knowledge_document_type (archived_at);

CREATE INDEX IF NOT EXISTS idx_knowledge_document_type_built_in
    ON knowledge_document_type (built_in);

CREATE INDEX IF NOT EXISTS idx_knowledge_document_type_classification
    ON knowledge_document_type (default_classification);

-- ── knowledge_document_type_field ────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS knowledge_document_type_field (
    id                   UUID         NOT NULL,
    document_type_id     UUID         NOT NULL,
    field_key            VARCHAR(100) NOT NULL,
    label                VARCHAR(255) NOT NULL,
    description          TEXT,
    data_type            VARCHAR(50)  NOT NULL,
    required             BOOLEAN      NOT NULL DEFAULT false,
    system_field         BOOLEAN      NOT NULL DEFAULT false,
    options_json         JSONB,
    validation_json      JSONB,
    default_value_json   JSONB,
    display_order        INT          NOT NULL DEFAULT 0,
    status               VARCHAR(50)  NOT NULL DEFAULT 'ACTIVE',
    version              INT          NOT NULL DEFAULT 0,
    created_at           TIMESTAMP    NOT NULL,
    updated_at           TIMESTAMP    NOT NULL,
    created_by           VARCHAR(255) NOT NULL DEFAULT 'SYSTEM',
    updated_by           VARCHAR(255) NOT NULL DEFAULT 'SYSTEM',
    CONSTRAINT pk_knowledge_document_type_field PRIMARY KEY (id),
    CONSTRAINT fk_knowledge_document_type_field_document_type
        FOREIGN KEY (document_type_id) REFERENCES knowledge_document_type (id),
    CONSTRAINT chk_knowledge_document_type_field_status
        CHECK (status IN ('ACTIVE', 'INACTIVE', 'ARCHIVED')),
    CONSTRAINT chk_knowledge_document_type_field_data_type
        CHECK (data_type IN (
            'TEXT', 'LONG_TEXT', 'NUMBER', 'BOOLEAN', 'DATE', 'DATETIME',
            'SELECT', 'MULTI_SELECT', 'USER', 'TEAM', 'PROJECT', 'APPLICATION',
            'URL', 'EMAIL', 'CURRENCY', 'PERCENT'
        )),
    CONSTRAINT chk_knowledge_document_type_field_display_order
        CHECK (display_order >= 0)
);

CREATE UNIQUE INDEX IF NOT EXISTS uq_knowledge_document_type_field_key
    ON knowledge_document_type_field (document_type_id, field_key);

CREATE INDEX IF NOT EXISTS idx_knowledge_document_type_field_document_type_id
    ON knowledge_document_type_field (document_type_id);

CREATE INDEX IF NOT EXISTS idx_knowledge_document_type_field_status
    ON knowledge_document_type_field (status);
