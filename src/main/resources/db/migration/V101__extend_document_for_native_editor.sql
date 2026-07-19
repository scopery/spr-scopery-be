-- Phase NDE-1: Extend documenthub_document to support native content mode

ALTER TABLE documenthub_document
    ADD COLUMN content_mode        VARCHAR(16)   NOT NULL DEFAULT 'FILE',
    ADD COLUMN parent_document_id  UUID          NULL,
    ADD COLUMN current_content_revision_id UUID  NULL,
    ADD COLUMN current_content_revision_no BIGINT NOT NULL DEFAULT 0,
    ADD COLUMN editor_schema_version INTEGER      NULL,
    ADD COLUMN content_checksum    VARCHAR(128)  NULL,
    ADD COLUMN content_updated_at  TIMESTAMPTZ   NULL,
    ADD COLUMN content_updated_by  UUID          NULL,
    ADD COLUMN template_version_id UUID          NULL,
    ADD COLUMN page_icon           VARCHAR(255)  NULL,
    ADD COLUMN page_cover_object_key VARCHAR(500) NULL,
    ADD COLUMN content_width       VARCHAR(16)   NOT NULL DEFAULT 'CENTERED',
    ADD COLUMN client_visible      BOOLEAN       NOT NULL DEFAULT FALSE;

ALTER TABLE documenthub_document
    ADD CONSTRAINT ck_documenthub_document_content_mode
        CHECK (content_mode IN ('NATIVE', 'FILE', 'HYBRID')),
    ADD CONSTRAINT ck_documenthub_document_content_width
        CHECK (content_width IN ('CENTERED', 'WIDE', 'FULL')),
    ADD CONSTRAINT ck_documenthub_document_parent_not_self
        CHECK (parent_document_id IS NULL OR parent_document_id <> id);

CREATE INDEX idx_documenthub_document_parent
    ON documenthub_document(parent_document_id)
    WHERE parent_document_id IS NOT NULL;

CREATE INDEX idx_documenthub_document_content_mode
    ON documenthub_document(content_mode, status);

CREATE INDEX idx_documenthub_document_client_visible
    ON documenthub_document(client_visible)
    WHERE client_visible = TRUE;
