-- Synced Block: reusable content blocks embeddable in multiple documents
CREATE TABLE documenthub_synced_block (
    id                  UUID        NOT NULL,
    workspace_id        UUID        NOT NULL,
    project_id          UUID        NOT NULL,
    title               VARCHAR(500),
    status              VARCHAR(32) NOT NULL DEFAULT 'ACTIVE',
    current_revision_no BIGINT      NOT NULL DEFAULT 0,
    schema_version      INTEGER,
    created_at          TIMESTAMPTZ NOT NULL,
    updated_at          TIMESTAMPTZ NOT NULL,
    created_by          VARCHAR(255),
    updated_by          VARCHAR(255),
    CONSTRAINT pk_documenthub_synced_block PRIMARY KEY (id),
    CONSTRAINT chk_documenthub_synced_block_status CHECK (status IN ('ACTIVE', 'ARCHIVED'))
);

CREATE INDEX idx_documenthub_synced_block_workspace ON documenthub_synced_block (workspace_id);
CREATE INDEX idx_documenthub_synced_block_project ON documenthub_synced_block (project_id);

-- Immutable revision snapshots for synced blocks
CREATE TABLE documenthub_synced_block_revision (
    id              UUID        NOT NULL,
    synced_block_id UUID        NOT NULL,
    revision_no     BIGINT      NOT NULL,
    ast             JSONB       NOT NULL,
    created_at      TIMESTAMPTZ NOT NULL,
    created_by      VARCHAR(255),
    CONSTRAINT pk_documenthub_synced_block_revision PRIMARY KEY (id),
    CONSTRAINT uq_documenthub_synced_block_revision UNIQUE (synced_block_id, revision_no),
    CONSTRAINT fk_documenthub_synced_block_revision_block FOREIGN KEY (synced_block_id) REFERENCES documenthub_synced_block (id)
);

CREATE INDEX idx_documenthub_synced_block_revision_block ON documenthub_synced_block_revision (synced_block_id);

-- Tracks which documents embed a given synced block
CREATE TABLE documenthub_synced_block_reference (
    id              UUID        NOT NULL,
    synced_block_id UUID        NOT NULL,
    document_id     UUID        NOT NULL,
    created_at      TIMESTAMPTZ NOT NULL,
    updated_at      TIMESTAMPTZ NOT NULL,
    created_by      VARCHAR(255),
    updated_by      VARCHAR(255),
    CONSTRAINT pk_documenthub_synced_block_reference PRIMARY KEY (id),
    CONSTRAINT uq_documenthub_synced_block_ref UNIQUE (synced_block_id, document_id),
    CONSTRAINT fk_documenthub_synced_block_ref_block FOREIGN KEY (synced_block_id) REFERENCES documenthub_synced_block (id)
);

CREATE INDEX idx_documenthub_synced_block_ref_block ON documenthub_synced_block_reference (synced_block_id);
CREATE INDEX idx_documenthub_synced_block_ref_doc ON documenthub_synced_block_reference (document_id);
