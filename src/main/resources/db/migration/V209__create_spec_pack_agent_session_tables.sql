CREATE TABLE spec_pack_agent_session (
    id                  UUID        NOT NULL,
    project_id          UUID        NOT NULL,
    spec_pack_id        UUID        NOT NULL,
    scope_package_id    UUID        NOT NULL,
    status              VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
    current_stage_code  VARCHAR(50) NOT NULL DEFAULT 'PREPARE',
    readiness_status    VARCHAR(50) NOT NULL DEFAULT 'NOT_READY',
    created_by          VARCHAR(255),
    created_at          TIMESTAMPTZ NOT NULL,
    updated_at          TIMESTAMPTZ NOT NULL,

    CONSTRAINT pk_spec_pack_agent_session PRIMARY KEY (id),
    CONSTRAINT fk_spec_pack_agent_session_spec_pack FOREIGN KEY (spec_pack_id) REFERENCES spec_pack (id)
);

CREATE INDEX idx_spec_pack_agent_session_project_id  ON spec_pack_agent_session (project_id);
CREATE INDEX idx_spec_pack_agent_session_spec_pack_id ON spec_pack_agent_session (spec_pack_id);
CREATE INDEX idx_spec_pack_agent_session_status       ON spec_pack_agent_session (status);

CREATE TABLE spec_pack_agent_stage (
    id              UUID        NOT NULL,
    session_id      UUID        NOT NULL,
    stage_code      VARCHAR(50) NOT NULL,
    stage_status    VARCHAR(50) NOT NULL DEFAULT 'NOT_STARTED',
    result_json     JSONB,
    completed_at    TIMESTAMPTZ,
    created_at      TIMESTAMPTZ NOT NULL,

    CONSTRAINT pk_spec_pack_agent_stage PRIMARY KEY (id),
    CONSTRAINT uq_spec_pack_agent_stage_session_code UNIQUE (session_id, stage_code),
    CONSTRAINT fk_spec_pack_agent_stage_session FOREIGN KEY (session_id) REFERENCES spec_pack_agent_session (id)
);

CREATE INDEX idx_spec_pack_agent_stage_session_id ON spec_pack_agent_stage (session_id);
