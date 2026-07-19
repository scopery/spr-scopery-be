-- Phase 33 gap close: forms versions/sections/fields/submissions, validation, status sets, layouts
CREATE TABLE IF NOT EXISTS config_custom_field_validation_rule (
    id UUID NOT NULL, workspace_id UUID NOT NULL, custom_field_definition_id UUID NOT NULL,
    rule_type VARCHAR(50) NOT NULL, rule_config_json TEXT, status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
    version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255), updated_by VARCHAR(255),
    CONSTRAINT pk_config_field_validation PRIMARY KEY (id),
    CONSTRAINT fk_config_field_validation_field FOREIGN KEY (custom_field_definition_id) REFERENCES config_custom_field_definition(id)
);
CREATE TABLE IF NOT EXISTS config_custom_form_version (
    id UUID NOT NULL, form_definition_id UUID NOT NULL, workspace_id UUID NOT NULL, version_number INT NOT NULL,
    status VARCHAR(50) NOT NULL, schema_json TEXT, published_at TIMESTAMPTZ, current_flag BOOLEAN NOT NULL DEFAULT FALSE,
    version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255), updated_by VARCHAR(255),
    CONSTRAINT pk_config_form_version PRIMARY KEY (id),
    CONSTRAINT fk_config_form_version_form FOREIGN KEY (form_definition_id) REFERENCES config_custom_form_definition(id),
    CONSTRAINT uq_config_form_version UNIQUE (form_definition_id, version_number),
    CONSTRAINT ck_config_form_version_status CHECK (status IN ('DRAFT','PUBLISHED','ARCHIVED'))
);
CREATE TABLE IF NOT EXISTS config_custom_form_section (
    id UUID NOT NULL, form_version_id UUID NOT NULL, title VARCHAR(255) NOT NULL, sort_order INT NOT NULL DEFAULT 0,
    visibility_json TEXT, version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255), updated_by VARCHAR(255),
    CONSTRAINT pk_config_form_section PRIMARY KEY (id),
    CONSTRAINT fk_config_form_section_version FOREIGN KEY (form_version_id) REFERENCES config_custom_form_version(id)
);
CREATE TABLE IF NOT EXISTS config_custom_form_field (
    id UUID NOT NULL, form_version_id UUID NOT NULL, section_id UUID, field_source VARCHAR(50) NOT NULL,
    custom_field_definition_id UUID, core_field_key VARCHAR(100), label_override VARCHAR(255),
    required_on_form BOOLEAN NOT NULL DEFAULT FALSE, readonly_flag BOOLEAN NOT NULL DEFAULT FALSE,
    sort_order INT NOT NULL DEFAULT 0, metadata_json TEXT, version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255), updated_by VARCHAR(255),
    CONSTRAINT pk_config_form_field PRIMARY KEY (id),
    CONSTRAINT fk_config_form_field_version FOREIGN KEY (form_version_id) REFERENCES config_custom_form_version(id)
);
CREATE TABLE IF NOT EXISTS config_form_submission (
    id UUID NOT NULL, workspace_id UUID NOT NULL, project_id UUID, form_definition_id UUID NOT NULL, form_version_id UUID NOT NULL,
    object_type_code VARCHAR(50), target_id UUID, principal_type VARCHAR(50) NOT NULL, submitted_by UUID,
    payload_json TEXT NOT NULL, validation_status VARCHAR(50) NOT NULL, validation_errors_json TEXT,
    status VARCHAR(50) NOT NULL, version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255), updated_by VARCHAR(255),
    CONSTRAINT pk_config_form_submission PRIMARY KEY (id),
    CONSTRAINT fk_config_form_submission_version FOREIGN KEY (form_version_id) REFERENCES config_custom_form_version(id),
    CONSTRAINT ck_config_form_submission_status CHECK (status IN ('ACCEPTED','REJECTED','PENDING'))
);
CREATE TABLE IF NOT EXISTS config_status_set (
    id UUID NOT NULL, workspace_id UUID NOT NULL, object_type_code VARCHAR(50) NOT NULL, set_code VARCHAR(100) NOT NULL,
    name VARCHAR(255) NOT NULL, status VARCHAR(50) NOT NULL, version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255), updated_by VARCHAR(255),
    CONSTRAINT pk_config_status_set PRIMARY KEY (id),
    CONSTRAINT uq_config_status_set UNIQUE (workspace_id, object_type_code, set_code)
);
CREATE TABLE IF NOT EXISTS config_status_value (
    id UUID NOT NULL, status_set_id UUID NOT NULL, value_code VARCHAR(100) NOT NULL, label VARCHAR(255) NOT NULL,
    domain_category VARCHAR(100) NOT NULL, sort_order INT NOT NULL DEFAULT 0, status VARCHAR(50) NOT NULL,
    version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255), updated_by VARCHAR(255),
    CONSTRAINT pk_config_status_value PRIMARY KEY (id),
    CONSTRAINT fk_config_status_value_set FOREIGN KEY (status_set_id) REFERENCES config_status_set(id),
    CONSTRAINT uq_config_status_value UNIQUE (status_set_id, value_code)
);
CREATE TABLE IF NOT EXISTS config_layout_definition (
    id UUID NOT NULL, workspace_id UUID NOT NULL, object_type_code VARCHAR(50) NOT NULL, layout_type VARCHAR(50) NOT NULL,
    name VARCHAR(255) NOT NULL, layout_json TEXT NOT NULL, status VARCHAR(50) NOT NULL, current_flag BOOLEAN NOT NULL DEFAULT FALSE,
    version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255), updated_by VARCHAR(255),
    CONSTRAINT pk_config_layout PRIMARY KEY (id),
    CONSTRAINT ck_config_layout_status CHECK (status IN ('DRAFT','PUBLISHED','ARCHIVED'))
);
