CREATE TABLE app_registry_component_field (
    id            UUID         NOT NULL,
    component_id  UUID         NOT NULL REFERENCES app_registry_component(id) ON DELETE CASCADE,
    workspace_id  UUID         NOT NULL,
    field_key     VARCHAR(100) NOT NULL,
    label         VARCHAR(255) NOT NULL,
    field_type    VARCHAR(50)  NOT NULL,
    required      BOOLEAN      NOT NULL DEFAULT FALSE,
    max_length    INT,
    remark        TEXT,
    display_order INT          NOT NULL DEFAULT 0,
    status        VARCHAR(50)  NOT NULL DEFAULT 'ACTIVE',
    version       INT          NOT NULL DEFAULT 0,
    created_at    TIMESTAMPTZ  NOT NULL,
    updated_at    TIMESTAMPTZ  NOT NULL,
    created_by    VARCHAR(255),
    updated_by    VARCHAR(255),
    CONSTRAINT pk_app_registry_component_field PRIMARY KEY (id),
    CONSTRAINT uq_app_registry_component_field UNIQUE (component_id, field_key)
);
CREATE INDEX idx_app_registry_component_field_component ON app_registry_component_field(component_id);
