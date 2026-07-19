CREATE TABLE documenthub_block_index (
    id              UUID            NOT NULL,
    document_id     UUID            NOT NULL,
    block_id        VARCHAR(128)    NOT NULL,
    block_type      VARCHAR(64)     NOT NULL,
    heading_level   SMALLINT        NULL,
    heading_text    VARCHAR(1000)   NULL,
    plain_text      TEXT            NULL,
    ordinal         INTEGER         NOT NULL DEFAULT 0,
    created_at      TIMESTAMPTZ     NOT NULL,
    updated_at      TIMESTAMPTZ     NOT NULL,
    created_by      VARCHAR(255)    NULL,
    updated_by      VARCHAR(255)    NULL,
    CONSTRAINT pk_documenthub_block_index PRIMARY KEY (id),
    CONSTRAINT uq_documenthub_block_index_doc_block UNIQUE (document_id, block_id)
);

CREATE INDEX idx_documenthub_block_index_document_id ON documenthub_block_index (document_id);
CREATE INDEX idx_documenthub_block_index_block_type ON documenthub_block_index (block_type);
