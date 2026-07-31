CREATE TABLE app_use_case (
    id                  UUID         NOT NULL,
    project_id          UUID         NOT NULL,
    primary_function_id UUID         NOT NULL REFERENCES app_functional_item(id) ON DELETE RESTRICT,
    key                 VARCHAR(50)  NOT NULL,
    name                VARCHAR(500) NOT NULL,
    goal                TEXT,
    primary_actor_name  VARCHAR(255),
    trigger_text        TEXT,
    status              VARCHAR(30)  NOT NULL DEFAULT 'DRAFT',
    completeness_status VARCHAR(30)  NOT NULL DEFAULT 'NOT_STARTED',
    display_order       INTEGER      NOT NULL DEFAULT 0,
    version             INTEGER      NOT NULL DEFAULT 0,
    created_at          TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    created_by          VARCHAR(255) NOT NULL DEFAULT 'SYSTEM',
    updated_at          TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_by          VARCHAR(255) NOT NULL DEFAULT 'SYSTEM',
    CONSTRAINT pk_app_use_case PRIMARY KEY (id),
    CONSTRAINT uq_app_use_case_key UNIQUE (project_id, key),
    CONSTRAINT ck_app_use_case_status
        CHECK (status IN ('DRAFT','IN_REVIEW','APPROVED','DEPRECATED','ARCHIVED')),
    CONSTRAINT ck_app_use_case_completeness
        CHECK (completeness_status IN ('NOT_STARTED','INCOMPLETE','READY_FOR_REVIEW','COMPLETE'))
);
CREATE INDEX idx_app_use_case_project  ON app_use_case(project_id);
CREATE INDEX idx_app_use_case_function ON app_use_case(primary_function_id);
CREATE INDEX idx_app_use_case_status   ON app_use_case(project_id, status);
