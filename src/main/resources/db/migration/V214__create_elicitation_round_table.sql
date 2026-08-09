CREATE TABLE elicitation_round
(
    id              UUID         NOT NULL,
    session_id      UUID         NOT NULL,
    round_number    INTEGER      NOT NULL,
    questions_json  TEXT         NOT NULL,
    overall_clarity VARCHAR(50),
    status          VARCHAR(50)  NOT NULL,
    submitted_at    TIMESTAMPTZ,
    created_by      VARCHAR(255),
    updated_by      VARCHAR(255),
    created_at      TIMESTAMPTZ  NOT NULL,
    updated_at      TIMESTAMPTZ  NOT NULL,

    CONSTRAINT pk_elicitation_round PRIMARY KEY (id),
    CONSTRAINT fk_elicitation_round_session
        FOREIGN KEY (session_id) REFERENCES elicitation_session (id),
    CONSTRAINT uq_elicitation_round_session_number UNIQUE (session_id, round_number),
    CONSTRAINT ck_elicitation_round_clarity
        CHECK (overall_clarity IS NULL OR overall_clarity IN ('BLOCKED', 'CRITICAL', 'IMPORTANT', 'MINOR', 'CLEARED')),
    CONSTRAINT ck_elicitation_round_status
        CHECK (status IN ('DRAFT', 'SUBMITTED', 'PROCESSING', 'COMPLETED'))
);

CREATE INDEX idx_elicitation_round_session ON elicitation_round (session_id);
CREATE INDEX idx_elicitation_round_status ON elicitation_round (status);
