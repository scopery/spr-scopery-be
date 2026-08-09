CREATE TABLE elicitation_question
(
    id                 UUID         NOT NULL,
    session_id         UUID         NOT NULL,
    sequence           INTEGER      NOT NULL,
    question_text      TEXT         NOT NULL,
    answer_text        TEXT,
    clarity_level      VARCHAR(50),
    ai_feedback        TEXT,
    status             VARCHAR(50)  NOT NULL,
    source             VARCHAR(50)  NOT NULL,
    parent_question_id UUID,
    answered_at        TIMESTAMPTZ,
    created_by         VARCHAR(255),
    updated_by         VARCHAR(255),
    created_at         TIMESTAMPTZ  NOT NULL,
    updated_at         TIMESTAMPTZ  NOT NULL,

    CONSTRAINT pk_elicitation_question PRIMARY KEY (id),
    CONSTRAINT fk_elicitation_question_session
        FOREIGN KEY (session_id) REFERENCES elicitation_session (id),
    CONSTRAINT ck_elicitation_question_clarity
        CHECK (clarity_level IS NULL OR clarity_level IN ('BLOCKED', 'CRITICAL', 'IMPORTANT', 'MINOR', 'CLEARED')),
    CONSTRAINT ck_elicitation_question_status
        CHECK (status IN ('PENDING', 'ANSWERED', 'SKIPPED')),
    CONSTRAINT ck_elicitation_question_source
        CHECK (source IN ('AI_GENERATED', 'FEEDBACK'))
);

CREATE INDEX idx_elicitation_question_session ON elicitation_question (session_id);
CREATE INDEX idx_elicitation_question_status ON elicitation_question (session_id, status);
CREATE INDEX idx_elicitation_question_parent ON elicitation_question (parent_question_id)
    WHERE parent_question_id IS NOT NULL;
