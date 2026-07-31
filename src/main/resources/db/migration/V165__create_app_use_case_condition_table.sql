CREATE TABLE app_use_case_condition (
    id             UUID         NOT NULL,
    use_case_id    UUID         NOT NULL REFERENCES app_use_case(id) ON DELETE CASCADE,
    condition_type VARCHAR(30)  NOT NULL,
    content        TEXT         NOT NULL,
    display_order  INTEGER      NOT NULL DEFAULT 0,
    created_at     TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    created_by     VARCHAR(255) NOT NULL DEFAULT 'SYSTEM',
    updated_at     TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_by     VARCHAR(255) NOT NULL DEFAULT 'SYSTEM',
    CONSTRAINT pk_app_use_case_condition PRIMARY KEY (id),
    CONSTRAINT ck_app_uc_condition_type CHECK (
        condition_type IN ('PRECONDITION','ASSUMPTION','SUCCESS_POSTCONDITION','FAILURE_POSTCONDITION'))
);
CREATE INDEX idx_app_uc_condition_uc ON app_use_case_condition(use_case_id);
