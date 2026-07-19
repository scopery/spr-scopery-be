CREATE TABLE documenthub_comment_thread (
    id              UUID            NOT NULL,
    document_id     UUID            NOT NULL,
    workspace_id    UUID            NOT NULL,
    project_id      UUID            NOT NULL,
    block_id        VARCHAR(128)    NULL,
    anchor_text     TEXT            NULL,
    status          VARCHAR(32)     NOT NULL DEFAULT 'OPEN',
    resolved_at     TIMESTAMPTZ     NULL,
    resolved_by     UUID            NULL,
    created_at      TIMESTAMPTZ     NOT NULL,
    updated_at      TIMESTAMPTZ     NOT NULL,
    created_by      VARCHAR(255)    NULL,
    updated_by      VARCHAR(255)    NULL,
    CONSTRAINT pk_documenthub_comment_thread PRIMARY KEY (id),
    CONSTRAINT chk_documenthub_comment_thread_status
        CHECK (status IN ('OPEN', 'RESOLVED', 'ARCHIVED'))
);

CREATE INDEX idx_documenthub_comment_thread_document_id ON documenthub_comment_thread (document_id);
CREATE INDEX idx_documenthub_comment_thread_status ON documenthub_comment_thread (status);

CREATE TABLE documenthub_comment (
    id              UUID            NOT NULL,
    thread_id       UUID            NOT NULL,
    document_id     UUID            NOT NULL,
    body            TEXT            NOT NULL,
    deleted_at      TIMESTAMPTZ     NULL,
    created_at      TIMESTAMPTZ     NOT NULL,
    updated_at      TIMESTAMPTZ     NOT NULL,
    created_by      VARCHAR(255)    NULL,
    updated_by      VARCHAR(255)    NULL,
    CONSTRAINT pk_documenthub_comment PRIMARY KEY (id),
    CONSTRAINT fk_documenthub_comment_thread
        FOREIGN KEY (thread_id) REFERENCES documenthub_comment_thread (id)
);

CREATE INDEX idx_documenthub_comment_thread_id ON documenthub_comment (thread_id);

CREATE TABLE documenthub_comment_reply (
    id              UUID            NOT NULL,
    comment_id      UUID            NOT NULL,
    thread_id       UUID            NOT NULL,
    body            TEXT            NOT NULL,
    deleted_at      TIMESTAMPTZ     NULL,
    created_at      TIMESTAMPTZ     NOT NULL,
    updated_at      TIMESTAMPTZ     NOT NULL,
    created_by      VARCHAR(255)    NULL,
    updated_by      VARCHAR(255)    NULL,
    CONSTRAINT pk_documenthub_comment_reply PRIMARY KEY (id),
    CONSTRAINT fk_documenthub_comment_reply_comment
        FOREIGN KEY (comment_id) REFERENCES documenthub_comment (id)
);

CREATE INDEX idx_documenthub_comment_reply_comment_id ON documenthub_comment_reply (comment_id);
