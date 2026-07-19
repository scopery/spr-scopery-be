-- AI Context resolution policy: defines what context to pull for an AI operation
CREATE TABLE aicontext_resolution_policy (
    id              UUID        NOT NULL,
    workspace_id    UUID        NOT NULL,
    policy_code     VARCHAR(128) NOT NULL,
    label           VARCHAR(500),
    max_tokens      INTEGER     NOT NULL DEFAULT 8000,
    include_related BOOLEAN     NOT NULL DEFAULT FALSE,
    enabled         BOOLEAN     NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMPTZ NOT NULL,
    updated_at      TIMESTAMPTZ NOT NULL,
    created_by      VARCHAR(255),
    updated_by      VARCHAR(255),
    CONSTRAINT pk_aicontext_resolution_policy PRIMARY KEY (id),
    CONSTRAINT uq_aicontext_resolution_policy_code UNIQUE (workspace_id, policy_code)
);

CREATE INDEX idx_aicontext_resolution_policy_workspace ON aicontext_resolution_policy (workspace_id);

-- Audit trail of every AI context resolution call
CREATE TABLE aicontext_resolution_audit (
    id              UUID        NOT NULL,
    policy_id       UUID,
    document_id     UUID,
    actor_id        UUID,
    token_count     INTEGER,
    block_count     INTEGER,
    status          VARCHAR(32) NOT NULL DEFAULT 'SUCCESS',
    error_message   TEXT,
    resolved_at     TIMESTAMPTZ NOT NULL,
    CONSTRAINT pk_aicontext_resolution_audit PRIMARY KEY (id),
    CONSTRAINT fk_aicontext_resolution_audit_policy FOREIGN KEY (policy_id) REFERENCES aicontext_resolution_policy (id) ON DELETE SET NULL
);

CREATE INDEX idx_aicontext_resolution_audit_document ON aicontext_resolution_audit (document_id);
CREATE INDEX idx_aicontext_resolution_audit_actor ON aicontext_resolution_audit (actor_id);
CREATE INDEX idx_aicontext_resolution_audit_resolved_at ON aicontext_resolution_audit (resolved_at DESC);
