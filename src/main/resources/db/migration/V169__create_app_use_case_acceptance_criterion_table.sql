CREATE TABLE app_use_case_acceptance_criterion (
    id            UUID         NOT NULL,
    use_case_id   UUID         NOT NULL REFERENCES app_use_case(id) ON DELETE CASCADE,
    title         VARCHAR(500) NOT NULL,
    given_text    TEXT,
    when_text     TEXT,
    then_text     TEXT,
    display_order INTEGER      NOT NULL DEFAULT 0,
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    created_by    VARCHAR(255) NOT NULL DEFAULT 'SYSTEM',
    updated_at    TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_by    VARCHAR(255) NOT NULL DEFAULT 'SYSTEM',
    CONSTRAINT pk_app_uc_acceptance_criterion PRIMARY KEY (id)
);
CREATE INDEX idx_app_uc_ac_uc ON app_use_case_acceptance_criterion(use_case_id);
