CREATE TABLE spec_pack_clarification (
    id          UUID        NOT NULL,
    session_id  UUID        NOT NULL,
    code        VARCHAR(100),
    question    TEXT        NOT NULL,
    answer      TEXT,
    priority    VARCHAR(50) NOT NULL DEFAULT 'IMPORTANT',
    status      VARCHAR(50) NOT NULL DEFAULT 'OPEN',
    source      VARCHAR(50) NOT NULL DEFAULT 'MANUAL',
    created_at  TIMESTAMPTZ NOT NULL,
    updated_at  TIMESTAMPTZ NOT NULL,

    CONSTRAINT pk_spec_pack_clarification PRIMARY KEY (id),
    CONSTRAINT fk_spec_pack_clarification_session FOREIGN KEY (session_id) REFERENCES spec_pack_agent_session (id)
);

CREATE INDEX idx_spec_pack_clarification_session_id ON spec_pack_clarification (session_id);
CREATE INDEX idx_spec_pack_clarification_status     ON spec_pack_clarification (status);
CREATE INDEX idx_spec_pack_clarification_priority   ON spec_pack_clarification (priority);
