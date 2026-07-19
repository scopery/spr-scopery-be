-- Phase NDE-1: Create documenthub_content (canonical AST) and documenthub_revision (immutable snapshots)

CREATE TABLE documenthub_content (
    id                  UUID          NOT NULL,
    document_id         UUID          NOT NULL,
    workspace_id        UUID          NOT NULL,
    project_id          UUID          NULL,
    schema_version      INTEGER       NOT NULL,
    revision_no         BIGINT        NOT NULL DEFAULT 0,
    ast                 JSONB         NOT NULL,
    plain_text          TEXT          NULL,
    word_count          INTEGER       NULL,
    character_count     INTEGER       NULL,
    checksum            VARCHAR(128)  NOT NULL,
    last_saved_at       TIMESTAMPTZ   NULL,
    last_saved_by       UUID          NULL,
    version             INT           NOT NULL DEFAULT 0,
    created_at          TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    created_by          VARCHAR(255)  NULL,
    updated_by          VARCHAR(255)  NULL,
    CONSTRAINT pk_documenthub_content           PRIMARY KEY (id),
    CONSTRAINT uq_documenthub_content_document  UNIQUE (document_id),
    CONSTRAINT ck_documenthub_content_ast       CHECK (jsonb_typeof(ast) = 'object'),
    CONSTRAINT ck_documenthub_content_schema    CHECK (schema_version > 0),
    CONSTRAINT ck_documenthub_content_revision  CHECK (revision_no >= 0)
);

CREATE INDEX idx_documenthub_content_workspace ON documenthub_content(workspace_id);
CREATE INDEX idx_documenthub_content_updated_at ON documenthub_content(updated_at DESC);

-- Immutable revision snapshots — no updated_at, no updated_by
CREATE TABLE documenthub_revision (
    id                  UUID          NOT NULL,
    document_id         UUID          NOT NULL,
    workspace_id        UUID          NOT NULL,
    project_id          UUID          NULL,
    revision_no         BIGINT        NOT NULL,
    revision_type       VARCHAR(32)   NOT NULL DEFAULT 'AUTOSAVE_CHECKPOINT',
    schema_version      INTEGER       NOT NULL,
    ast                 JSONB         NOT NULL,
    plain_text          TEXT          NULL,
    word_count          INTEGER       NULL,
    checksum            VARCHAR(128)  NOT NULL,
    change_summary      VARCHAR(500)  NULL,
    saved_by            UUID          NULL,
    saved_at            TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    source_revision_id  UUID          NULL,
    created_at          TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    created_by          VARCHAR(255)  NULL,
    CONSTRAINT pk_documenthub_revision              PRIMARY KEY (id),
    CONSTRAINT uq_documenthub_revision_doc_no       UNIQUE (document_id, revision_no),
    CONSTRAINT ck_documenthub_revision_ast          CHECK (jsonb_typeof(ast) = 'object'),
    CONSTRAINT ck_documenthub_revision_no           CHECK (revision_no >= 1),
    CONSTRAINT ck_documenthub_revision_type         CHECK (revision_type IN (
        'AUTOSAVE_CHECKPOINT', 'MANUAL', 'APPROVAL', 'FINALIZATION',
        'RESTORE', 'TEMPLATE_CREATE', 'AI_ACCEPT', 'SAFETY_SNAPSHOT'
    ))
);

CREATE INDEX idx_documenthub_revision_document   ON documenthub_revision(document_id, revision_no DESC);
CREATE INDEX idx_documenthub_revision_created_at ON documenthub_revision(document_id, created_at DESC);
