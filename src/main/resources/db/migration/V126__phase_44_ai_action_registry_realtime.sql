-- Phase 44: AI Action Planning & Agentic Operations — Registry & Realtime Tables

-- ── ai_action_schema_definition ───────────────────────────────────────────────
CREATE TABLE ai_action_schema_definition (
    id             UUID        NOT NULL,
    schema_code    VARCHAR(100) NOT NULL,
    schema_version INT         NOT NULL DEFAULT 1,
    schema_json    TEXT        NOT NULL,
    status         VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at     TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT pk_ai_action_schema_definition PRIMARY KEY (id)
);

CREATE UNIQUE INDEX uq_ai_action_schema_definition_code_version
    ON ai_action_schema_definition (schema_code, schema_version);

-- ── ai_action_tool_policy ─────────────────────────────────────────────────────
CREATE TABLE ai_action_tool_policy (
    id                   UUID        NOT NULL,
    tool_code            VARCHAR(100) NOT NULL,
    tool_version         VARCHAR(20) NOT NULL,
    invocation_scope     VARCHAR(40) NOT NULL,
    risk_level           VARCHAR(20) NOT NULL,
    execution_mode       VARCHAR(40) NOT NULL,
    max_batch_targets    INT         NOT NULL DEFAULT 25,
    dry_run_required     BOOLEAN     NOT NULL DEFAULT TRUE,
    supports_compensation BOOLEAN    NOT NULL DEFAULT FALSE,
    supports_pause       BOOLEAN     NOT NULL DEFAULT FALSE,
    status               VARCHAR(20) NOT NULL DEFAULT 'INACTIVE',
    created_at           TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT pk_ai_action_tool_policy PRIMARY KEY (id)
);

CREATE UNIQUE INDEX uq_ai_action_tool_policy_code_version
    ON ai_action_tool_policy (tool_code, tool_version);

CREATE INDEX idx_ai_action_tool_policy_status
    ON ai_action_tool_policy (status);

-- ── ai_action_policy_definition ───────────────────────────────────────────────
CREATE TABLE ai_action_policy_definition (
    id             UUID        NOT NULL,
    policy_code    VARCHAR(100) NOT NULL,
    policy_version INT         NOT NULL DEFAULT 1,
    config_json    TEXT        NOT NULL,
    status         VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at     TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT pk_ai_action_policy_definition PRIMARY KEY (id)
);

CREATE UNIQUE INDEX uq_ai_action_policy_definition_code_version
    ON ai_action_policy_definition (policy_code, policy_version);

-- ── ai_action_execution_event ─────────────────────────────────────────────────
CREATE TABLE ai_action_execution_event (
    id                UUID        NOT NULL,
    execution_id      UUID        NOT NULL,
    sequence          BIGINT      NOT NULL,
    execution_version INT         NOT NULL,
    event_type        VARCHAR(60) NOT NULL,
    occurred_at       TIMESTAMPTZ NOT NULL DEFAULT now(),
    trace_id          VARCHAR(100),
    payload_json      TEXT,
    redis_published_at TIMESTAMPTZ,
    created_at        TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT pk_ai_action_execution_event PRIMARY KEY (id),
    CONSTRAINT fk_ai_action_execution_event_exec FOREIGN KEY (execution_id)
        REFERENCES ai_action_execution (id),
    CONSTRAINT uq_ai_action_execution_event_seq UNIQUE (execution_id, sequence)
);

CREATE INDEX idx_ai_action_execution_event_unpublished
    ON ai_action_execution_event (occurred_at)
    WHERE redis_published_at IS NULL;

CREATE INDEX idx_ai_action_execution_event_exec_seq
    ON ai_action_execution_event (execution_id, sequence);

-- ── ai_action_control_command ─────────────────────────────────────────────────
CREATE TABLE ai_action_control_command (
    id                       UUID        NOT NULL,
    execution_id             UUID        NOT NULL,
    command_type             VARCHAR(30) NOT NULL,
    issued_by_user_id        UUID        NOT NULL,
    expected_execution_version INT       NOT NULL,
    idempotency_key          VARCHAR(200) NOT NULL,
    status                   VARCHAR(30) NOT NULL DEFAULT 'ACCEPTED',
    created_at               TIMESTAMPTZ NOT NULL DEFAULT now(),
    processed_at             TIMESTAMPTZ,

    CONSTRAINT pk_ai_action_control_command PRIMARY KEY (id),
    CONSTRAINT fk_ai_action_control_command_exec FOREIGN KEY (execution_id)
        REFERENCES ai_action_execution (id)
);

CREATE UNIQUE INDEX uq_ai_action_control_command_idempotency
    ON ai_action_control_command (idempotency_key);

CREATE INDEX idx_ai_action_control_command_execution_status
    ON ai_action_control_command (execution_id, status);
