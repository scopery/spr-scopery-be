CREATE TABLE config_field_visibility_policy (
    id UUID NOT NULL,
    workspace_id UUID NOT NULL,
    custom_field_definition_id UUID NOT NULL,
    audience_type VARCHAR(50) NOT NULL,
    visible BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    CONSTRAINT pk_config_field_visibility_policy PRIMARY KEY (id),
    CONSTRAINT uq_field_visibility_field_audience UNIQUE (custom_field_definition_id, audience_type),
    CONSTRAINT fk_field_visibility_field FOREIGN KEY (custom_field_definition_id) REFERENCES config_custom_field_definition(id)
);

CREATE INDEX idx_field_visibility_workspace ON config_field_visibility_policy (workspace_id);
CREATE INDEX idx_field_visibility_field ON config_field_visibility_policy (custom_field_definition_id);
