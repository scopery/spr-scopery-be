CREATE TABLE documenthub_attachment (
    id                  UUID            NOT NULL,
    document_id         UUID            NOT NULL,
    workspace_id        UUID            NOT NULL,
    project_id          UUID            NOT NULL,
    block_id            VARCHAR(128)    NULL,
    file_name           VARCHAR(500)    NOT NULL,
    media_type          VARCHAR(255)    NULL,
    file_size_bytes     BIGINT          NULL,
    object_key          VARCHAR(1000)   NULL,
    storage_status      VARCHAR(32)     NOT NULL DEFAULT 'PENDING_UPLOAD',
    checksum            VARCHAR(128)    NULL,
    presigned_url       VARCHAR(2000)   NULL,
    presigned_expires_at TIMESTAMPTZ   NULL,
    created_at          TIMESTAMPTZ     NOT NULL,
    updated_at          TIMESTAMPTZ     NOT NULL,
    created_by          VARCHAR(255)    NULL,
    updated_by          VARCHAR(255)    NULL,
    CONSTRAINT pk_documenthub_attachment PRIMARY KEY (id),
    CONSTRAINT chk_documenthub_attachment_status
        CHECK (storage_status IN ('PENDING_UPLOAD', 'AVAILABLE', 'FAILED', 'PURGED'))
);

CREATE INDEX idx_documenthub_attachment_document_id ON documenthub_attachment (document_id);
CREATE INDEX idx_documenthub_attachment_status ON documenthub_attachment (storage_status);
