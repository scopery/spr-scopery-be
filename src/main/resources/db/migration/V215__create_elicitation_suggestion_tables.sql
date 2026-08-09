CREATE TABLE elicitation_suggestion
(
    id               UUID         NOT NULL,
    round_id         UUID         NOT NULL,
    overall_summary  TEXT,
    status           VARCHAR(50)  NOT NULL,
    ai_raw_response  TEXT,
    created_by       VARCHAR(255),
    updated_by       VARCHAR(255),
    created_at       TIMESTAMPTZ  NOT NULL,
    updated_at       TIMESTAMPTZ  NOT NULL,

    CONSTRAINT pk_elicitation_suggestion PRIMARY KEY (id),
    CONSTRAINT fk_elicitation_suggestion_round
        FOREIGN KEY (round_id) REFERENCES elicitation_round (id),
    CONSTRAINT uq_elicitation_suggestion_round UNIQUE (round_id),
    CONSTRAINT ck_elicitation_suggestion_status
        CHECK (status IN ('PENDING', 'PARTIALLY_APPROVED', 'COMPLETED'))
);

CREATE TABLE elicitation_suggestion_item
(
    id                        UUID         NOT NULL,
    suggestion_id             UUID         NOT NULL,
    sequence                  INTEGER      NOT NULL,
    action                    VARCHAR(100) NOT NULL,
    target_entity_type        VARCHAR(50),
    target_entity_id          UUID,
    target_entity_name        TEXT,
    rationale                 TEXT         NOT NULL,
    changes_json              TEXT,
    precondition_actions_json TEXT,
    estimated_impact          VARCHAR(50)  NOT NULL,
    status                    VARCHAR(50)  NOT NULL,
    executed_at               TIMESTAMPTZ,
    error_message             TEXT,
    rollback_data_json        TEXT,
    created_by                VARCHAR(255),
    updated_by                VARCHAR(255),
    created_at                TIMESTAMPTZ  NOT NULL,
    updated_at                TIMESTAMPTZ  NOT NULL,

    CONSTRAINT pk_elicitation_suggestion_item PRIMARY KEY (id),
    CONSTRAINT fk_elicitation_suggestion_item_suggestion
        FOREIGN KEY (suggestion_id) REFERENCES elicitation_suggestion (id),
    CONSTRAINT ck_elicitation_suggestion_item_impact
        CHECK (estimated_impact IN ('LOW', 'MEDIUM', 'HIGH')),
    CONSTRAINT ck_elicitation_suggestion_item_status
        CHECK (status IN ('PENDING', 'APPROVED', 'EXECUTING', 'SUCCEEDED', 'FAILED', 'SKIPPED', 'REJECTED'))
);

CREATE INDEX idx_elicitation_suggestion_round ON elicitation_suggestion (round_id);
CREATE INDEX idx_elicitation_suggestion_item_suggestion ON elicitation_suggestion_item (suggestion_id);
CREATE INDEX idx_elicitation_suggestion_item_status ON elicitation_suggestion_item (suggestion_id, status);
