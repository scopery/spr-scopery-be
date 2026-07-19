CREATE TABLE documenthub_suggestion (
    id                  UUID            NOT NULL,
    document_id         UUID            NOT NULL,
    workspace_id        UUID            NOT NULL,
    project_id          UUID            NOT NULL,
    target_revision_no  BIGINT          NOT NULL,
    description         TEXT            NULL,
    status              VARCHAR(32)     NOT NULL DEFAULT 'PENDING',
    accepted_at         TIMESTAMPTZ     NULL,
    accepted_by         UUID            NULL,
    rejected_at         TIMESTAMPTZ     NULL,
    rejected_by         UUID            NULL,
    accepted_revision_no BIGINT         NULL,
    created_at          TIMESTAMPTZ     NOT NULL,
    updated_at          TIMESTAMPTZ     NOT NULL,
    created_by          VARCHAR(255)    NULL,
    updated_by          VARCHAR(255)    NULL,
    CONSTRAINT pk_documenthub_suggestion PRIMARY KEY (id),
    CONSTRAINT chk_documenthub_suggestion_status
        CHECK (status IN ('PENDING', 'ACCEPTED', 'REJECTED', 'EXPIRED'))
);

CREATE INDEX idx_documenthub_suggestion_document_id ON documenthub_suggestion (document_id);
CREATE INDEX idx_documenthub_suggestion_status ON documenthub_suggestion (status);

CREATE TABLE documenthub_suggestion_operation (
    id              UUID            NOT NULL,
    suggestion_id   UUID            NOT NULL,
    op_type         VARCHAR(32)     NOT NULL,
    block_id        VARCHAR(128)    NULL,
    path            VARCHAR(500)    NULL,
    value           JSONB           NULL,
    ordinal         INTEGER         NOT NULL DEFAULT 0,
    created_at      TIMESTAMPTZ     NOT NULL,
    updated_at      TIMESTAMPTZ     NOT NULL,
    created_by      VARCHAR(255)    NULL,
    updated_by      VARCHAR(255)    NULL,
    CONSTRAINT pk_documenthub_suggestion_operation PRIMARY KEY (id),
    CONSTRAINT fk_documenthub_suggestion_operation_suggestion
        FOREIGN KEY (suggestion_id) REFERENCES documenthub_suggestion (id)
);

CREATE INDEX idx_documenthub_suggestion_operation_suggestion_id ON documenthub_suggestion_operation (suggestion_id);
