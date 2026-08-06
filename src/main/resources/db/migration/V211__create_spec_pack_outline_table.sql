CREATE TABLE spec_pack_outline (
    id              UUID        NOT NULL,
    session_id      UUID        NOT NULL,
    version_number  INTEGER     NOT NULL DEFAULT 1,
    outline_json    JSONB       NOT NULL DEFAULT '{}',
    status          VARCHAR(50) NOT NULL DEFAULT 'DRAFT',
    approved_at     TIMESTAMPTZ,
    created_by      VARCHAR(255),
    created_at      TIMESTAMPTZ NOT NULL,

    CONSTRAINT pk_spec_pack_outline PRIMARY KEY (id),
    CONSTRAINT uq_spec_pack_outline_session_version UNIQUE (session_id, version_number),
    CONSTRAINT fk_spec_pack_outline_session FOREIGN KEY (session_id) REFERENCES spec_pack_agent_session (id)
);

CREATE INDEX idx_spec_pack_outline_session_id ON spec_pack_outline (session_id);
