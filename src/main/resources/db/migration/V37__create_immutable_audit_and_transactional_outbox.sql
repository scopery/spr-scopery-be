CREATE TABLE app_audit_event
(
    id              UUID         NOT NULL,
    event_type      VARCHAR(200) NOT NULL,
    actor_id        UUID,
    actor_type      VARCHAR(50)  NOT NULL,
    resource_type   VARCHAR(100),
    resource_ref_id UUID,
    organization_id UUID,
    workspace_id    UUID,
    before_state    JSONB,
    after_state     JSONB,
    reason          TEXT,
    trace_id        VARCHAR(100),
    occurred_at     TIMESTAMP    NOT NULL,
    CONSTRAINT pk_app_audit_event PRIMARY KEY (id),
    CONSTRAINT ck_app_audit_event_actor_type CHECK (actor_type IN ('USER', 'SERVICE', 'SYSTEM'))
);

CREATE INDEX idx_app_audit_event_resource ON app_audit_event (resource_type, resource_ref_id);
CREATE INDEX idx_app_audit_event_actor ON app_audit_event (actor_id);
CREATE INDEX idx_app_audit_event_occurred_at ON app_audit_event (occurred_at);
CREATE INDEX idx_app_audit_event_trace_id ON app_audit_event (trace_id);

CREATE TABLE app_transactional_outbox
(
    id              UUID         NOT NULL,
    aggregate_type  VARCHAR(100) NOT NULL,
    aggregate_id    UUID         NOT NULL,
    event_type      VARCHAR(200) NOT NULL,
    payload         JSONB        NOT NULL,
    trace_id        VARCHAR(100),
    status          VARCHAR(50)  NOT NULL,
    occurred_at     TIMESTAMP    NOT NULL,
    published_at    TIMESTAMP,
    retry_count     INT          NOT NULL DEFAULT 0,
    CONSTRAINT pk_app_transactional_outbox PRIMARY KEY (id),
    CONSTRAINT ck_app_transactional_outbox_status CHECK (status IN ('PENDING', 'PUBLISHED', 'FAILED'))
);

CREATE INDEX idx_app_transactional_outbox_pending
    ON app_transactional_outbox (status, occurred_at);
