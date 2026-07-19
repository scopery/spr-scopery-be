CREATE TABLE documenthub_mention (
    id                      UUID            NOT NULL,
    document_id             UUID            NOT NULL,
    workspace_id            UUID            NOT NULL,
    project_id              UUID            NOT NULL,
    block_id                VARCHAR(128)    NULL,
    mention_type            VARCHAR(32)     NOT NULL,
    mentioned_resource_type VARCHAR(128)    NOT NULL,
    mentioned_resource_id   UUID            NOT NULL,
    created_at              TIMESTAMPTZ     NOT NULL,
    updated_at              TIMESTAMPTZ     NOT NULL,
    created_by              VARCHAR(255)    NULL,
    updated_by              VARCHAR(255)    NULL,
    CONSTRAINT pk_documenthub_mention PRIMARY KEY (id),
    CONSTRAINT uq_documenthub_mention_doc_block_res
        UNIQUE (document_id, block_id, mentioned_resource_type, mentioned_resource_id)
);

CREATE INDEX idx_documenthub_mention_document_id ON documenthub_mention (document_id);
CREATE INDEX idx_documenthub_mention_resource ON documenthub_mention (mentioned_resource_type, mentioned_resource_id);

CREATE TABLE documenthub_relation (
    id                      UUID            NOT NULL,
    source_document_id      UUID            NOT NULL,
    target_document_id      UUID            NOT NULL,
    relation_type           VARCHAR(64)     NOT NULL DEFAULT 'LINK',
    block_id                VARCHAR(128)    NULL,
    created_at              TIMESTAMPTZ     NOT NULL,
    updated_at              TIMESTAMPTZ     NOT NULL,
    created_by              VARCHAR(255)    NULL,
    updated_by              VARCHAR(255)    NULL,
    CONSTRAINT pk_documenthub_relation PRIMARY KEY (id),
    CONSTRAINT uq_documenthub_relation_src_tgt_type
        UNIQUE (source_document_id, target_document_id, relation_type)
);

CREATE INDEX idx_documenthub_relation_source ON documenthub_relation (source_document_id);
CREATE INDEX idx_documenthub_relation_target ON documenthub_relation (target_document_id);
