CREATE TABLE app_function_workflow (
    id          UUID         NOT NULL,
    project_id  UUID         NOT NULL,
    function_id UUID         NOT NULL REFERENCES app_functional_item(id) ON DELETE CASCADE,
    status      VARCHAR(50)  NOT NULL DEFAULT 'DRAFT',
    version     INTEGER      NOT NULL DEFAULT 0,
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    created_by  VARCHAR(255) NOT NULL DEFAULT 'SYSTEM',
    updated_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_by  VARCHAR(255) NOT NULL DEFAULT 'SYSTEM',
    CONSTRAINT pk_app_function_workflow PRIMARY KEY (id),
    CONSTRAINT uq_app_function_workflow_function UNIQUE (function_id),
    CONSTRAINT ck_app_function_workflow_status
        CHECK (status IN ('DRAFT', 'PUBLISHED', 'ARCHIVED'))
);

CREATE INDEX idx_app_function_workflow_project  ON app_function_workflow (project_id);
CREATE INDEX idx_app_function_workflow_function ON app_function_workflow (function_id);

CREATE TABLE app_function_workflow_step (
    id                UUID         NOT NULL,
    workflow_id       UUID         NOT NULL REFERENCES app_function_workflow(id) ON DELETE CASCADE,
    step_type         VARCHAR(50)  NOT NULL,
    screen_context_id UUID,
    content_json      JSONB,
    condition_text    TEXT,
    next_screen_id    UUID,
    display_order     INTEGER      NOT NULL DEFAULT 0,
    created_at        TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    created_by        VARCHAR(255) NOT NULL DEFAULT 'SYSTEM',
    updated_at        TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_by        VARCHAR(255) NOT NULL DEFAULT 'SYSTEM',
    CONSTRAINT pk_app_function_workflow_step PRIMARY KEY (id),
    CONSTRAINT ck_app_function_workflow_step_type CHECK (
        step_type IN ('USER_ACTION', 'SYSTEM_ACTION', 'CONDITION', 'NAVIGATION', 'RESULT', 'ERROR')
    )
);

CREATE INDEX idx_app_fws_workflow ON app_function_workflow_step (workflow_id);
CREATE INDEX idx_app_fws_order    ON app_function_workflow_step (workflow_id, display_order);
