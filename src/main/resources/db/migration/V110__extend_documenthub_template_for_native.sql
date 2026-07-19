-- Add native template mode support to existing template tables
ALTER TABLE documenthub_template
    ADD COLUMN template_mode VARCHAR(16) NOT NULL DEFAULT 'FILE';

ALTER TABLE documenthub_template
    ADD CONSTRAINT chk_documenthub_template_mode CHECK (template_mode IN ('FILE', 'NATIVE'));

ALTER TABLE documenthub_template_version
    ADD COLUMN ast JSONB NULL;

-- Native template variables (declared placeholders in AST)
CREATE TABLE documenthub_native_template_variable (
    id                  UUID        NOT NULL,
    template_version_id UUID        NOT NULL,
    variable_key        VARCHAR(255) NOT NULL,
    label               VARCHAR(500),
    variable_type       VARCHAR(64) NOT NULL DEFAULT 'TEXT',
    required            BOOLEAN     NOT NULL DEFAULT FALSE,
    default_value       TEXT,
    sensitive           BOOLEAN     NOT NULL DEFAULT FALSE,
    ordinal             INTEGER     NOT NULL DEFAULT 0,
    created_at          TIMESTAMPTZ NOT NULL,
    updated_at          TIMESTAMPTZ NOT NULL,
    created_by          VARCHAR(255),
    updated_by          VARCHAR(255),
    CONSTRAINT pk_documenthub_native_template_variable PRIMARY KEY (id),
    CONSTRAINT uq_documenthub_native_template_variable_key UNIQUE (template_version_id, variable_key),
    CONSTRAINT fk_documenthub_native_template_variable_version
        FOREIGN KEY (template_version_id) REFERENCES documenthub_template_version (id)
);

CREATE INDEX idx_documenthub_native_template_variable_version ON documenthub_native_template_variable (template_version_id);
