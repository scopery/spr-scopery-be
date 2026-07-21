-- Phase 44: AI Action Planning & Agentic Operations — Core Tables

-- ── ai_action_request ─────────────────────────────────────────────────────────
CREATE TABLE ai_action_request (
    id                          UUID        NOT NULL,
    workspace_id                UUID        NOT NULL,
    project_id                  UUID,
    initiated_by_user_id        UUID        NOT NULL,
    origin_type                 VARCHAR(30) NOT NULL,
    origin_conversation_id      VARCHAR(200),
    origin_message_id           VARCHAR(200),
    origin_turn_id              VARCHAR(200),
    origin_suggestion_ref       VARCHAR(200),
    legacy_phase21_suggestion_id VARCHAR(200),
    intent_summary              VARCHAR(2000) NOT NULL,
    status                      VARCHAR(30) NOT NULL,
    idempotency_key             VARCHAR(200) NOT NULL,
    request_hash                VARCHAR(200),
    latest_plan_id              UUID,
    created_at                  TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at                  TIMESTAMPTZ NOT NULL DEFAULT now(),
    created_by                  VARCHAR(100) NOT NULL DEFAULT 'SYSTEM',
    updated_by                  VARCHAR(100) NOT NULL DEFAULT 'SYSTEM',

    CONSTRAINT pk_ai_action_request PRIMARY KEY (id)
);

CREATE UNIQUE INDEX uq_ai_action_request_idempotency
    ON ai_action_request (workspace_id, initiated_by_user_id, idempotency_key);

CREATE INDEX idx_ai_action_request_workspace_status
    ON ai_action_request (workspace_id, status);

-- ── ai_action_plan ────────────────────────────────────────────────────────────
CREATE TABLE ai_action_plan (
    id                      UUID        NOT NULL,
    request_id              UUID        NOT NULL,
    plan_number             INT         NOT NULL,
    status                  VARCHAR(30) NOT NULL,
    policy_code             VARCHAR(100),
    policy_version          INT         NOT NULL DEFAULT 1,
    plan_hash               VARCHAR(200),
    context_hash            VARCHAR(200),
    source_state_hash       VARCHAR(200),
    risk_level              VARCHAR(20),
    execution_mode          VARCHAR(40),
    requires_confirmation   BOOLEAN     NOT NULL DEFAULT TRUE,
    step_count              INT         NOT NULL DEFAULT 0,
    target_count            INT         NOT NULL DEFAULT 0,
    summary                 TEXT,
    version                 INT         NOT NULL DEFAULT 1,
    expires_at              TIMESTAMPTZ,
    created_at              TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at              TIMESTAMPTZ NOT NULL DEFAULT now(),
    created_by              VARCHAR(100) NOT NULL DEFAULT 'SYSTEM',
    updated_by              VARCHAR(100) NOT NULL DEFAULT 'SYSTEM',

    CONSTRAINT pk_ai_action_plan PRIMARY KEY (id),
    CONSTRAINT fk_ai_action_plan_request FOREIGN KEY (request_id)
        REFERENCES ai_action_request (id)
);

CREATE INDEX idx_ai_action_plan_request_id ON ai_action_plan (request_id);
CREATE INDEX idx_ai_action_plan_status      ON ai_action_plan (status);

-- ── ai_action_step ────────────────────────────────────────────────────────────
CREATE TABLE ai_action_step (
    id                            UUID        NOT NULL,
    plan_id                       UUID        NOT NULL,
    ordinal                       INT         NOT NULL,
    tool_code                     VARCHAR(100) NOT NULL,
    tool_version                  VARCHAR(20) NOT NULL,
    input_schema_code             VARCHAR(100),
    input_schema_version          INT,
    input_hash                    VARCHAR(200),
    target_entity_type            VARCHAR(80),
    target_entity_id              UUID,
    expected_target_version_token VARCHAR(200),
    risk_level                    VARCHAR(20),
    execution_mode                VARCHAR(40),
    depends_on_step_ids           TEXT,

    CONSTRAINT pk_ai_action_step PRIMARY KEY (id),
    CONSTRAINT fk_ai_action_step_plan FOREIGN KEY (plan_id)
        REFERENCES ai_action_plan (id)
);

CREATE INDEX idx_ai_action_step_plan_ordinal ON ai_action_step (plan_id, ordinal);

-- ── ai_action_preview ─────────────────────────────────────────────────────────
CREATE TABLE ai_action_preview (
    id                   UUID        NOT NULL,
    plan_id              UUID        NOT NULL,
    preview_hash         VARCHAR(200),
    masked_diff_json     TEXT,
    warnings_json        TEXT,
    baseline_impact      VARCHAR(50),
    external_side_effect BOOLEAN     NOT NULL DEFAULT FALSE,
    valid_until          TIMESTAMPTZ,
    created_at           TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT pk_ai_action_preview PRIMARY KEY (id),
    CONSTRAINT fk_ai_action_preview_plan FOREIGN KEY (plan_id)
        REFERENCES ai_action_plan (id),
    CONSTRAINT uq_ai_action_preview_plan UNIQUE (plan_id)
);

-- ── ai_action_confirmation ────────────────────────────────────────────────────
CREATE TABLE ai_action_confirmation (
    id                   UUID        NOT NULL,
    plan_id              UUID        NOT NULL,
    plan_version         INT         NOT NULL,
    plan_hash            VARCHAR(200) NOT NULL,
    confirmed_by_user_id UUID        NOT NULL,
    decision             VARCHAR(20) NOT NULL,
    channel              VARCHAR(50),
    comment              TEXT,
    confirmation_hash    VARCHAR(200),
    status               VARCHAR(30) NOT NULL,
    expires_at           TIMESTAMPTZ,
    created_at           TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT pk_ai_action_confirmation PRIMARY KEY (id),
    CONSTRAINT fk_ai_action_confirmation_plan FOREIGN KEY (plan_id)
        REFERENCES ai_action_plan (id)
);

CREATE INDEX idx_ai_action_confirmation_plan_id ON ai_action_confirmation (plan_id);

-- ── ai_action_execution ───────────────────────────────────────────────────────
CREATE TABLE ai_action_execution (
    id                   UUID        NOT NULL,
    plan_id              UUID        NOT NULL,
    initiated_by_user_id UUID        NOT NULL,
    execution_key        VARCHAR(200) NOT NULL,
    status               VARCHAR(40) NOT NULL,
    execution_version    INT         NOT NULL DEFAULT 0,
    worker_instance_id   VARCHAR(200),
    lease_expires_at     TIMESTAMPTZ,
    current_step_ordinal INT,
    succeeded_count      INT         NOT NULL DEFAULT 0,
    failed_count         INT         NOT NULL DEFAULT 0,
    skipped_count        INT         NOT NULL DEFAULT 0,
    compensated_count    INT         NOT NULL DEFAULT 0,
    cancelled_count      INT         NOT NULL DEFAULT 0,
    started_at           TIMESTAMPTZ,
    completed_at         TIMESTAMPTZ,
    created_at           TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at           TIMESTAMPTZ NOT NULL DEFAULT now(),
    created_by           VARCHAR(100) NOT NULL DEFAULT 'SYSTEM',
    updated_by           VARCHAR(100) NOT NULL DEFAULT 'SYSTEM',

    CONSTRAINT pk_ai_action_execution PRIMARY KEY (id),
    CONSTRAINT fk_ai_action_execution_plan FOREIGN KEY (plan_id)
        REFERENCES ai_action_plan (id),
    CONSTRAINT uq_ai_action_execution_per_plan UNIQUE (plan_id),
    CONSTRAINT uq_ai_action_execution_key     UNIQUE (execution_key)
);

CREATE INDEX idx_ai_action_execution_status_lease
    ON ai_action_execution (status, lease_expires_at);

-- ── ai_action_step_execution ──────────────────────────────────────────────────
CREATE TABLE ai_action_step_execution (
    id                      UUID        NOT NULL,
    execution_id            UUID        NOT NULL,
    step_id                 UUID        NOT NULL,
    ordinal                 INT         NOT NULL,
    tool_code               VARCHAR(100) NOT NULL,
    attempt                 INT         NOT NULL DEFAULT 1,
    idempotency_key         VARCHAR(200) NOT NULL,
    status                  VARCHAR(30) NOT NULL,
    safe_result_summary     TEXT,
    domain_result_ref       VARCHAR(300),
    result_version_token    VARCHAR(200),
    error_code              VARCHAR(200),
    retryable               BOOLEAN,
    audit_ref               VARCHAR(300),
    outbox_ref              VARCHAR(300),
    started_at              TIMESTAMPTZ NOT NULL DEFAULT now(),
    completed_at            TIMESTAMPTZ,

    CONSTRAINT pk_ai_action_step_execution PRIMARY KEY (id),
    CONSTRAINT fk_ai_action_step_execution_exec FOREIGN KEY (execution_id)
        REFERENCES ai_action_execution (id),
    CONSTRAINT fk_ai_action_step_execution_step FOREIGN KEY (step_id)
        REFERENCES ai_action_step (id)
);

CREATE UNIQUE INDEX uq_ai_action_step_execution_idempotency
    ON ai_action_step_execution (idempotency_key);

CREATE INDEX idx_ai_action_step_exec_execution_ordinal
    ON ai_action_step_execution (execution_id, ordinal);

-- ── ai_action_compensation ────────────────────────────────────────────────────
CREATE TABLE ai_action_compensation (
    id                   UUID        NOT NULL,
    execution_id         UUID        NOT NULL,
    step_execution_id    UUID        NOT NULL,
    requested_by_user_id UUID        NOT NULL,
    tool_code            VARCHAR(100) NOT NULL,
    status               VARCHAR(30) NOT NULL,
    comment              TEXT,
    created_at           TIMESTAMPTZ NOT NULL DEFAULT now(),
    completed_at         TIMESTAMPTZ,

    CONSTRAINT pk_ai_action_compensation PRIMARY KEY (id),
    CONSTRAINT fk_ai_action_compensation_exec FOREIGN KEY (execution_id)
        REFERENCES ai_action_execution (id),
    CONSTRAINT fk_ai_action_compensation_step_exec FOREIGN KEY (step_execution_id)
        REFERENCES ai_action_step_execution (id)
);

CREATE INDEX idx_ai_action_compensation_execution_id
    ON ai_action_compensation (execution_id);
