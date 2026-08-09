CREATE TABLE elicitation_session
(
    id               UUID         NOT NULL,
    project_id       UUID         NOT NULL,
    scope_package_id UUID         NOT NULL,
    title            TEXT,
    status           VARCHAR(50)  NOT NULL,
    created_by       VARCHAR(255),
    updated_by       VARCHAR(255),
    created_at       TIMESTAMPTZ  NOT NULL,
    updated_at       TIMESTAMPTZ  NOT NULL,

    CONSTRAINT pk_elicitation_session PRIMARY KEY (id),
    CONSTRAINT ck_elicitation_session_status
        CHECK (status IN ('ACTIVE', 'CLOSED', 'CANCELLED'))
);

CREATE INDEX idx_elicitation_session_project ON elicitation_session (project_id);
CREATE INDEX idx_elicitation_session_scope ON elicitation_session (scope_package_id);
CREATE INDEX idx_elicitation_session_status ON elicitation_session (status);
