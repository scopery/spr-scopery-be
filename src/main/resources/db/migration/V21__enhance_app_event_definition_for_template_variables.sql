-- Enhance app_event_definition with version and sample payload support
ALTER TABLE app_event_definition
    ADD COLUMN IF NOT EXISTS event_version    INTEGER NOT NULL DEFAULT 1,
    ADD COLUMN IF NOT EXISTS sample_payload_json JSONB;

-- Separate table for structured event variables (queryable, typed, indexed)
CREATE TABLE app_event_variable
(
    id                  UUID         NOT NULL,
    event_definition_id UUID         NOT NULL,
    variable_path       VARCHAR(255) NOT NULL,
    variable_label      VARCHAR(255) NOT NULL,
    variable_type       VARCHAR(50)  NOT NULL,
    required            BOOLEAN      NOT NULL DEFAULT false,
    description         TEXT,
    example_value       TEXT,
    created_at          TIMESTAMP    NOT NULL,
    updated_at          TIMESTAMP    NOT NULL,
    created_by          VARCHAR(100),
    updated_by          VARCHAR(100),

    CONSTRAINT pk_app_event_variable PRIMARY KEY (id),
    CONSTRAINT fk_app_event_variable_definition
        FOREIGN KEY (event_definition_id) REFERENCES app_event_definition (id),
    CONSTRAINT uq_app_event_variable_path
        UNIQUE (event_definition_id, variable_path)
);

CREATE INDEX idx_app_event_variable_definition_id ON app_event_variable (event_definition_id);
CREATE INDEX idx_app_event_variable_path          ON app_event_variable (variable_path);
