-- Phase 33: Custom Fields / Forms / Configuration
CREATE TABLE IF NOT EXISTS config_object_type (
    id UUID NOT NULL, code VARCHAR(50) NOT NULL, name VARCHAR(255) NOT NULL,
    custom_fields_enabled BOOLEAN NOT NULL DEFAULT TRUE, forms_enabled BOOLEAN NOT NULL DEFAULT TRUE,
    tags_enabled BOOLEAN NOT NULL DEFAULT TRUE, custom_status_enabled BOOLEAN NOT NULL DEFAULT FALSE,
    client_visible_fields_enabled BOOLEAN NOT NULL DEFAULT FALSE, reportable_fields_enabled BOOLEAN NOT NULL DEFAULT TRUE,
    searchable_fields_enabled BOOLEAN NOT NULL DEFAULT TRUE, enabled BOOLEAN NOT NULL DEFAULT TRUE,
    version INT NOT NULL DEFAULT 0, created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255), updated_by VARCHAR(255),
    CONSTRAINT pk_config_object_type PRIMARY KEY (id), CONSTRAINT uq_config_object_type_code UNIQUE (code)
);
CREATE TABLE IF NOT EXISTS config_custom_field_definition (
    id UUID NOT NULL, workspace_id UUID NOT NULL, object_type_code VARCHAR(50) NOT NULL, field_key VARCHAR(100) NOT NULL,
    label VARCHAR(255) NOT NULL, field_type VARCHAR(50) NOT NULL, required_flag BOOLEAN NOT NULL DEFAULT FALSE,
    sensitive_flag BOOLEAN NOT NULL DEFAULT FALSE, client_visible BOOLEAN NOT NULL DEFAULT FALSE,
    searchable BOOLEAN NOT NULL DEFAULT FALSE, reportable BOOLEAN NOT NULL DEFAULT FALSE, exportable BOOLEAN NOT NULL DEFAULT FALSE,
    default_value_json TEXT, status VARCHAR(50) NOT NULL, version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), created_by VARCHAR(255), updated_by VARCHAR(255),
    CONSTRAINT pk_config_custom_field_definition PRIMARY KEY (id),
    CONSTRAINT uq_config_custom_field_key UNIQUE (workspace_id, object_type_code, field_key),
    CONSTRAINT ck_config_field_status CHECK (status IN ('DRAFT','ACTIVE','ARCHIVED')),
    CONSTRAINT ck_config_field_type CHECK (field_type IN ('TEXT','LONG_TEXT','NUMBER','DECIMAL','CURRENCY','DATE','DATETIME','BOOLEAN','SELECT','MULTI_SELECT','USER','TEAM','EXTERNAL_CONTACT','EXTERNAL_ORGANIZATION','PROJECT','TASK','DOCUMENT','URL','EMAIL','PHONE','PERCENTAGE'))
);
CREATE TABLE IF NOT EXISTS config_custom_field_option (
    id UUID NOT NULL, custom_field_definition_id UUID NOT NULL, option_code VARCHAR(100) NOT NULL, label VARCHAR(255) NOT NULL,
    sort_order INT NOT NULL DEFAULT 0, status VARCHAR(50) NOT NULL, version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), created_by VARCHAR(255), updated_by VARCHAR(255),
    CONSTRAINT pk_config_custom_field_option PRIMARY KEY (id),
    CONSTRAINT fk_config_field_option_field FOREIGN KEY (custom_field_definition_id) REFERENCES config_custom_field_definition(id),
    CONSTRAINT uq_config_field_option UNIQUE (custom_field_definition_id, option_code)
);
CREATE TABLE IF NOT EXISTS config_custom_field_value (
    id UUID NOT NULL, workspace_id UUID NOT NULL, object_type_code VARCHAR(50) NOT NULL, target_id UUID NOT NULL,
    custom_field_definition_id UUID NOT NULL, value_text TEXT, value_long_text TEXT, value_number DOUBLE PRECISION,
    value_decimal NUMERIC(19,4), value_boolean BOOLEAN, value_date DATE, value_datetime TIMESTAMPTZ,
    value_json TEXT, value_option_ids TEXT, version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), created_by VARCHAR(255), updated_by VARCHAR(255),
    CONSTRAINT pk_config_custom_field_value PRIMARY KEY (id),
    CONSTRAINT fk_config_field_value_field FOREIGN KEY (custom_field_definition_id) REFERENCES config_custom_field_definition(id),
    CONSTRAINT uq_config_field_value UNIQUE (custom_field_definition_id, target_id)
);
CREATE TABLE IF NOT EXISTS config_custom_form_definition (
    id UUID NOT NULL, workspace_id UUID NOT NULL, project_id UUID, object_type_code VARCHAR(50) NOT NULL,
    form_code VARCHAR(100) NOT NULL, name VARCHAR(255) NOT NULL, form_type VARCHAR(50), status VARCHAR(50) NOT NULL,
    current_version_id UUID, version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), created_by VARCHAR(255), updated_by VARCHAR(255),
    CONSTRAINT pk_config_custom_form_definition PRIMARY KEY (id),
    CONSTRAINT uq_config_form_code UNIQUE (workspace_id, form_code)
);
CREATE TABLE IF NOT EXISTS config_tag_definition (
    id UUID NOT NULL, workspace_id UUID NOT NULL, tag_code VARCHAR(100) NOT NULL, label VARCHAR(255) NOT NULL,
    color VARCHAR(50), allowed_object_types_json TEXT, status VARCHAR(50) NOT NULL, version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), created_by VARCHAR(255), updated_by VARCHAR(255),
    CONSTRAINT pk_config_tag_definition PRIMARY KEY (id), CONSTRAINT uq_config_tag_code UNIQUE (workspace_id, tag_code)
);
CREATE TABLE IF NOT EXISTS config_tag_assignment (
    id UUID NOT NULL, workspace_id UUID NOT NULL, tag_definition_id UUID NOT NULL, object_type_code VARCHAR(50) NOT NULL, target_id UUID NOT NULL,
    archived_at TIMESTAMPTZ, version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), created_by VARCHAR(255), updated_by VARCHAR(255),
    CONSTRAINT pk_config_tag_assignment PRIMARY KEY (id),
    CONSTRAINT fk_config_tag_assignment_tag FOREIGN KEY (tag_definition_id) REFERENCES config_tag_definition(id)
);
CREATE UNIQUE INDEX IF NOT EXISTS uq_config_tag_assignment_active ON config_tag_assignment(tag_definition_id, object_type_code, target_id) WHERE archived_at IS NULL;
CREATE TABLE IF NOT EXISTS config_taxonomy (
    id UUID NOT NULL, workspace_id UUID NOT NULL, taxonomy_code VARCHAR(100) NOT NULL, name VARCHAR(255) NOT NULL, status VARCHAR(50) NOT NULL,
    version INT NOT NULL DEFAULT 0, created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), created_by VARCHAR(255), updated_by VARCHAR(255),
    CONSTRAINT pk_config_taxonomy PRIMARY KEY (id), CONSTRAINT uq_config_taxonomy UNIQUE (workspace_id, taxonomy_code)
);
CREATE TABLE IF NOT EXISTS config_taxonomy_term (
    id UUID NOT NULL, taxonomy_id UUID NOT NULL, parent_term_id UUID, term_code VARCHAR(100) NOT NULL, label VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL, version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), created_by VARCHAR(255), updated_by VARCHAR(255),
    CONSTRAINT pk_config_taxonomy_term PRIMARY KEY (id), CONSTRAINT fk_config_taxonomy_term FOREIGN KEY (taxonomy_id) REFERENCES config_taxonomy(id)
);
