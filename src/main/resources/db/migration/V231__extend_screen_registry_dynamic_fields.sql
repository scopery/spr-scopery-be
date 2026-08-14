-- V231: Screen Design Spec — Dynamic Schema Extension
-- Covers: screen modes, data entity fields, validation rule catalog,
--         component option source, field mode configs, field validations,
--         spec documents, change history, process/event items, function-screen role upgrade

-- 1. Screen modes
CREATE TABLE app_registry_screen_mode (
    id            UUID         NOT NULL,
    screen_id     UUID         NOT NULL REFERENCES app_registry_screen(id) ON DELETE CASCADE,
    workspace_id  UUID         NOT NULL,
    mode_code     VARCHAR(50)  NOT NULL,
    name          VARCHAR(255) NOT NULL,
    display_order INT          NOT NULL DEFAULT 0,
    status        VARCHAR(50)  NOT NULL DEFAULT 'ACTIVE',
    version       INT          NOT NULL DEFAULT 0,
    created_at    TIMESTAMPTZ  NOT NULL,
    updated_at    TIMESTAMPTZ  NOT NULL,
    created_by    VARCHAR(255),
    updated_by    VARCHAR(255),
    CONSTRAINT pk_app_registry_screen_mode PRIMARY KEY (id),
    CONSTRAINT uq_app_registry_screen_mode UNIQUE (screen_id, mode_code)
);
CREATE INDEX idx_app_registry_screen_mode_screen ON app_registry_screen_mode(screen_id);

-- 2. Data entity fields
CREATE TABLE app_registry_data_entity_field (
    id            UUID         NOT NULL,
    entity_id     UUID         NOT NULL REFERENCES app_registry_data_entity(id) ON DELETE CASCADE,
    workspace_id  UUID         NOT NULL,
    column_name   VARCHAR(100) NOT NULL,
    data_type     VARCHAR(50)  NOT NULL,
    max_length    INT,
    is_nullable   BOOLEAN      NOT NULL DEFAULT TRUE,
    is_unique     BOOLEAN      NOT NULL DEFAULT FALSE,
    remark        TEXT,
    display_order INT          NOT NULL DEFAULT 0,
    status        VARCHAR(50)  NOT NULL DEFAULT 'ACTIVE',
    version       INT          NOT NULL DEFAULT 0,
    created_at    TIMESTAMPTZ  NOT NULL,
    updated_at    TIMESTAMPTZ  NOT NULL,
    created_by    VARCHAR(255),
    updated_by    VARCHAR(255),
    CONSTRAINT pk_app_registry_data_entity_field PRIMARY KEY (id),
    CONSTRAINT uq_app_registry_data_entity_field UNIQUE (entity_id, column_name),
    CONSTRAINT chk_data_entity_field_data_type
        CHECK (data_type IN ('VARCHAR','INTEGER','BOOLEAN','DATE','TIMESTAMP','TEXT','UUID','DECIMAL'))
);
CREATE INDEX idx_app_registry_data_entity_field_entity ON app_registry_data_entity_field(entity_id);

-- 3. Validation rule type catalog
CREATE TABLE app_registry_validation_rule_type (
    id                UUID         NOT NULL,
    workspace_id      UUID,
    code              VARCHAR(100) NOT NULL,
    name              VARCHAR(255) NOT NULL,
    category          VARCHAR(50)  NOT NULL DEFAULT 'GENERAL',
    param_schema_json JSONB,
    default_message   TEXT,
    description       TEXT,
    is_system         BOOLEAN      NOT NULL DEFAULT FALSE,
    status            VARCHAR(50)  NOT NULL DEFAULT 'ACTIVE',
    display_order     INT          NOT NULL DEFAULT 0,
    version           INT          NOT NULL DEFAULT 0,
    created_at        TIMESTAMPTZ  NOT NULL,
    updated_at        TIMESTAMPTZ  NOT NULL,
    created_by        VARCHAR(255),
    updated_by        VARCHAR(255),
    CONSTRAINT pk_app_registry_validation_rule_type PRIMARY KEY (id)
);
CREATE UNIQUE INDEX uq_vrt_system_code ON app_registry_validation_rule_type(code)
    WHERE workspace_id IS NULL;
CREATE UNIQUE INDEX uq_vrt_workspace_code ON app_registry_validation_rule_type(workspace_id, code)
    WHERE workspace_id IS NOT NULL;
CREATE INDEX idx_app_registry_validation_rule_type_ws ON app_registry_validation_rule_type(workspace_id);

-- 4. Component option source columns
ALTER TABLE app_registry_component
    ADD COLUMN option_source_type  VARCHAR(20)  NOT NULL DEFAULT 'NONE',
    ADD COLUMN source_entity_id    UUID         REFERENCES app_registry_data_entity(id),
    ADD COLUMN source_value_column VARCHAR(100),
    ADD COLUMN source_label_column VARCHAR(100),
    ADD COLUMN source_filter_json  JSONB;

ALTER TABLE app_registry_component
    ADD CONSTRAINT chk_component_option_source_type
        CHECK (option_source_type IN ('STATIC','DYNAMIC','NONE')),
    ADD CONSTRAINT chk_component_source_config CHECK (
        (option_source_type = 'NONE'
            AND source_entity_id IS NULL AND source_value_column IS NULL AND source_label_column IS NULL)
        OR (option_source_type = 'STATIC' AND source_entity_id IS NULL)
        OR (option_source_type = 'DYNAMIC'
            AND source_entity_id IS NOT NULL
            AND source_value_column IS NOT NULL
            AND source_label_column IS NOT NULL)
    );

-- 5. Component options (static)
CREATE TABLE app_registry_component_option (
    id            UUID         NOT NULL,
    component_id  UUID         NOT NULL REFERENCES app_registry_component(id) ON DELETE CASCADE,
    workspace_id  UUID         NOT NULL,
    option_value  VARCHAR(100) NOT NULL,
    option_label  VARCHAR(255) NOT NULL,
    display_order INT          NOT NULL DEFAULT 0,
    status        VARCHAR(50)  NOT NULL DEFAULT 'ACTIVE',
    version       INT          NOT NULL DEFAULT 0,
    created_at    TIMESTAMPTZ  NOT NULL,
    updated_at    TIMESTAMPTZ  NOT NULL,
    created_by    VARCHAR(255),
    updated_by    VARCHAR(255),
    CONSTRAINT pk_app_registry_component_option PRIMARY KEY (id),
    CONSTRAINT uq_app_registry_component_option UNIQUE (component_id, option_value)
);
CREATE INDEX idx_app_registry_component_option_component ON app_registry_component_option(component_id);

-- 6. Screen field extra columns
ALTER TABLE app_registry_screen_field
    ADD COLUMN component_id         UUID REFERENCES app_registry_component(id),
    ADD COLUMN data_entity_field_id UUID REFERENCES app_registry_data_entity_field(id),
    ADD COLUMN max_length           INT,
    ADD COLUMN remark               TEXT;

-- 7. Screen field mode config
CREATE TABLE app_registry_screen_field_mode_config (
    id            UUID        NOT NULL,
    field_id      UUID        NOT NULL REFERENCES app_registry_screen_field(id) ON DELETE CASCADE,
    mode_id       UUID        NOT NULL REFERENCES app_registry_screen_mode(id) ON DELETE CASCADE,
    workspace_id  UUID        NOT NULL,
    is_visible    BOOLEAN     NOT NULL DEFAULT TRUE,
    is_required   BOOLEAN     NOT NULL DEFAULT FALSE,
    is_readonly   BOOLEAN     NOT NULL DEFAULT FALSE,
    default_value TEXT,
    display_order INTEGER,
    version       INT         NOT NULL DEFAULT 0,
    created_at    TIMESTAMPTZ NOT NULL,
    updated_at    TIMESTAMPTZ NOT NULL,
    created_by    VARCHAR(255),
    updated_by    VARCHAR(255),
    CONSTRAINT pk_app_registry_screen_field_mode_config PRIMARY KEY (id),
    CONSTRAINT uq_app_registry_screen_field_mode_config UNIQUE (field_id, mode_id)
);
CREATE INDEX idx_app_registry_sfmc_field ON app_registry_screen_field_mode_config(field_id);
CREATE INDEX idx_app_registry_sfmc_mode ON app_registry_screen_field_mode_config(mode_id);

-- 8. Screen field validation (catalog-driven)
CREATE TABLE app_registry_screen_field_validation (
    id              UUID        NOT NULL,
    field_id        UUID        NOT NULL REFERENCES app_registry_screen_field(id) ON DELETE CASCADE,
    mode_id         UUID        REFERENCES app_registry_screen_mode(id) ON DELETE CASCADE,
    rule_type_id    UUID        NOT NULL REFERENCES app_registry_validation_rule_type(id),
    workspace_id    UUID        NOT NULL,
    rule_param_json JSONB,
    condition_json  JSONB,
    error_message   TEXT,
    remark          TEXT,
    display_order   INT         NOT NULL DEFAULT 0,
    status          VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
    version         INT         NOT NULL DEFAULT 0,
    created_at      TIMESTAMPTZ NOT NULL,
    updated_at      TIMESTAMPTZ NOT NULL,
    created_by      VARCHAR(255),
    updated_by      VARCHAR(255),
    CONSTRAINT pk_app_registry_screen_field_validation PRIMARY KEY (id)
);
CREATE INDEX idx_app_registry_sfv_field ON app_registry_screen_field_validation(field_id);
CREATE INDEX idx_app_registry_sfv_rule_type ON app_registry_screen_field_validation(rule_type_id);

-- 9. Screen spec document
CREATE TABLE app_registry_screen_spec_document (
    id             UUID         NOT NULL,
    project_id     UUID         NOT NULL,
    workspace_id   UUID         NOT NULL,
    document_code  VARCHAR(100) NOT NULL,
    document_name  VARCHAR(500) NOT NULL,
    project_name   VARCHAR(255),
    system_name    VARCHAR(255),
    phase_name     VARCHAR(255),
    language       VARCHAR(10)  NOT NULL DEFAULT 'EN',
    overview       TEXT,
    figma_url      VARCHAR(1000),
    status         VARCHAR(50)  NOT NULL DEFAULT 'ACTIVE',
    version        INT          NOT NULL DEFAULT 0,
    created_at     TIMESTAMPTZ  NOT NULL,
    updated_at     TIMESTAMPTZ  NOT NULL,
    created_by     VARCHAR(255),
    updated_by     VARCHAR(255),
    CONSTRAINT pk_app_registry_screen_spec_document PRIMARY KEY (id),
    CONSTRAINT uq_app_registry_screen_spec_document_code UNIQUE (project_id, document_code)
);
CREATE INDEX idx_app_registry_screen_spec_document_project ON app_registry_screen_spec_document(project_id);
CREATE INDEX idx_app_registry_screen_spec_document_workspace ON app_registry_screen_spec_document(workspace_id);

-- 10. Spec document screen junction
CREATE TABLE app_registry_spec_doc_screen (
    document_id   UUID NOT NULL REFERENCES app_registry_screen_spec_document(id) ON DELETE CASCADE,
    screen_id     UUID NOT NULL REFERENCES app_registry_screen(id) ON DELETE CASCADE,
    display_order INT  NOT NULL DEFAULT 0,
    note          TEXT,
    CONSTRAINT pk_app_registry_spec_doc_screen PRIMARY KEY (document_id, screen_id)
);
CREATE INDEX idx_app_registry_spec_doc_screen_doc ON app_registry_spec_doc_screen(document_id);
CREATE INDEX idx_app_registry_spec_doc_screen_screen ON app_registry_spec_doc_screen(screen_id);

-- 11. Spec document revision (Change History)
CREATE TABLE app_registry_spec_doc_revision (
    id                UUID         NOT NULL,
    document_id       UUID         NOT NULL REFERENCES app_registry_screen_spec_document(id) ON DELETE CASCADE,
    workspace_id      UUID         NOT NULL,
    revision_no       VARCHAR(50)  NOT NULL,
    target_sheet_name VARCHAR(255),
    details           TEXT         NOT NULL,
    person_in_charge  VARCHAR(255),
    color             VARCHAR(50),
    changed_at        DATE,
    display_order     INT          NOT NULL DEFAULT 0,
    status            VARCHAR(50)  NOT NULL DEFAULT 'ACTIVE',
    version           INT          NOT NULL DEFAULT 0,
    created_at        TIMESTAMPTZ  NOT NULL,
    updated_at        TIMESTAMPTZ  NOT NULL,
    created_by        VARCHAR(255),
    updated_by        VARCHAR(255),
    CONSTRAINT pk_app_registry_spec_doc_revision PRIMARY KEY (id)
);
CREATE INDEX idx_app_registry_spec_doc_revision_doc ON app_registry_spec_doc_revision(document_id);

-- 12. Screen process items
CREATE TABLE app_registry_screen_process_item (
    id                  UUID         NOT NULL,
    screen_id           UUID         NOT NULL REFERENCES app_registry_screen(id) ON DELETE CASCADE,
    workspace_id        UUID         NOT NULL,
    mode_id             UUID         REFERENCES app_registry_screen_mode(id) ON DELETE SET NULL,
    target_field_id     UUID         REFERENCES app_registry_screen_field(id) ON DELETE SET NULL,
    title               VARCHAR(500),
    content             TEXT         NOT NULL,
    source_table        VARCHAR(255),
    condition_note      TEXT,
    display_order       INT          NOT NULL DEFAULT 0,
    status              VARCHAR(50)  NOT NULL DEFAULT 'ACTIVE',
    version             INT          NOT NULL DEFAULT 0,
    created_at          TIMESTAMPTZ  NOT NULL,
    updated_at          TIMESTAMPTZ  NOT NULL,
    created_by          VARCHAR(255),
    updated_by          VARCHAR(255),
    CONSTRAINT pk_app_registry_screen_process_item PRIMARY KEY (id)
);
CREATE INDEX idx_app_registry_screen_process_item_screen ON app_registry_screen_process_item(screen_id);

-- 13. Screen event items
CREATE TABLE app_registry_screen_event_item (
    id                  UUID         NOT NULL,
    screen_id           UUID         NOT NULL REFERENCES app_registry_screen(id) ON DELETE CASCADE,
    workspace_id        UUID         NOT NULL,
    mode_id             UUID         REFERENCES app_registry_screen_mode(id) ON DELETE SET NULL,
    trigger_field_id    UUID         REFERENCES app_registry_screen_field(id) ON DELETE SET NULL,
    trigger_action_code VARCHAR(100),
    title               VARCHAR(500),
    content             TEXT         NOT NULL,
    condition_note      TEXT,
    target_screen_id    UUID         REFERENCES app_registry_screen(id) ON DELETE SET NULL,
    target_mode_code    VARCHAR(50),
    display_order       INT          NOT NULL DEFAULT 0,
    status              VARCHAR(50)  NOT NULL DEFAULT 'ACTIVE',
    version             INT          NOT NULL DEFAULT 0,
    created_at          TIMESTAMPTZ  NOT NULL,
    updated_at          TIMESTAMPTZ  NOT NULL,
    created_by          VARCHAR(255),
    updated_by          VARCHAR(255),
    CONSTRAINT pk_app_registry_screen_event_item PRIMARY KEY (id)
);
CREATE INDEX idx_app_registry_screen_event_item_screen ON app_registry_screen_event_item(screen_id);

-- 14. Function-screen link upgrade (role + mode_code + display_order)
ALTER TABLE app_function_screen
    ADD COLUMN role          VARCHAR(50),
    ADD COLUMN mode_code     VARCHAR(50),
    ADD COLUMN display_order INT NOT NULL DEFAULT 0,
    ADD CONSTRAINT chk_app_function_screen_role
        CHECK (role IN ('ENTRY','MAIN','SUB','RESULT','DIALOG','ERROR','RELATED'));
