CREATE TABLE knowledge_query_rewriter_config (
    id                  uuid         NOT NULL,
    workspace_id        uuid         NOT NULL,
    enabled             boolean      NOT NULL DEFAULT false,
    provider            varchar(100),
    model               varchar(200),
    max_tokens          int          NOT NULL DEFAULT 80,
    prompt_template     text,
    version             bigint       NOT NULL DEFAULT 0,
    created_at          timestamptz  NOT NULL,
    created_by          varchar(100),
    updated_at          timestamptz  NOT NULL,
    updated_by          varchar(100),
    CONSTRAINT pk_knowledge_query_rewriter_config PRIMARY KEY (id),
    CONSTRAINT uq_knowledge_query_rewriter_config_workspace UNIQUE (workspace_id)
);
