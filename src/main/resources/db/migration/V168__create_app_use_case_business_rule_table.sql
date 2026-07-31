CREATE TABLE app_use_case_business_rule (
    id            UUID         NOT NULL,
    use_case_id   UUID         NOT NULL REFERENCES app_use_case(id) ON DELETE CASCADE,
    rule_code     VARCHAR(50)  NOT NULL,
    description   TEXT         NOT NULL,
    display_order INTEGER      NOT NULL DEFAULT 0,
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    created_by    VARCHAR(255) NOT NULL DEFAULT 'SYSTEM',
    updated_at    TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_by    VARCHAR(255) NOT NULL DEFAULT 'SYSTEM',
    CONSTRAINT pk_app_uc_business_rule PRIMARY KEY (id)
);
CREATE INDEX idx_app_uc_br_uc ON app_use_case_business_rule(use_case_id);
