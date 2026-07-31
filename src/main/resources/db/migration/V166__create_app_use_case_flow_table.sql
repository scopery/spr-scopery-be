CREATE TABLE app_use_case_flow (
    id             UUID         NOT NULL,
    use_case_id    UUID         NOT NULL REFERENCES app_use_case(id) ON DELETE CASCADE,
    flow_type      VARCHAR(30)  NOT NULL,
    name           VARCHAR(255),
    source_step_id UUID,
    condition_text TEXT,
    display_order  INTEGER      NOT NULL DEFAULT 0,
    created_at     TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    created_by     VARCHAR(255) NOT NULL DEFAULT 'SYSTEM',
    updated_at     TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_by     VARCHAR(255) NOT NULL DEFAULT 'SYSTEM',
    CONSTRAINT pk_app_use_case_flow PRIMARY KEY (id),
    CONSTRAINT ck_app_uc_flow_type CHECK (flow_type IN ('MAIN','ALTERNATIVE','EXCEPTION'))
);
CREATE INDEX idx_app_uc_flow_uc   ON app_use_case_flow(use_case_id);
CREATE INDEX idx_app_uc_flow_type ON app_use_case_flow(use_case_id, flow_type);
