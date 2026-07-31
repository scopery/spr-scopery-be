CREATE TABLE app_use_case_flow_step (
    id                UUID         NOT NULL,
    flow_id           UUID         NOT NULL REFERENCES app_use_case_flow(id) ON DELETE CASCADE,
    step_type         VARCHAR(50)  NOT NULL,
    screen_context_id UUID,
    content_json      JSONB,
    next_screen_id    UUID,
    display_order     INTEGER      NOT NULL DEFAULT 0,
    created_at        TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    created_by        VARCHAR(255) NOT NULL DEFAULT 'SYSTEM',
    updated_at        TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_by        VARCHAR(255) NOT NULL DEFAULT 'SYSTEM',
    CONSTRAINT pk_app_uc_flow_step PRIMARY KEY (id),
    CONSTRAINT ck_app_uc_flow_step_type CHECK (
        step_type IN ('USER_ACTION','SYSTEM_ACTION','CONDITION','NAVIGATION','RESULT','ERROR'))
);
CREATE INDEX idx_app_uc_flow_step_flow  ON app_use_case_flow_step(flow_id);
CREATE INDEX idx_app_uc_flow_step_order ON app_use_case_flow_step(flow_id, display_order);
