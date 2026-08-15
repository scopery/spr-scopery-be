-- 1. Extend api_endpoint with description, request params, and response schema
ALTER TABLE app_registry_api_endpoint
    ADD COLUMN description          TEXT,
    ADD COLUMN request_params_json  JSONB,
    ADD COLUMN response_schema_json JSONB;

-- 2. Track which component_field generated each screen_field
ALTER TABLE app_registry_screen_field
    ADD COLUMN component_field_id UUID REFERENCES app_registry_component_field(id) ON DELETE SET NULL;

CREATE INDEX idx_app_registry_screen_field_component_field
    ON app_registry_screen_field(component_field_id);

-- 3. Component ↔ API junction
CREATE TABLE app_registry_component_api (
    id            UUID         NOT NULL,
    component_id  UUID         NOT NULL REFERENCES app_registry_component(id) ON DELETE CASCADE,
    api_id        UUID         NOT NULL REFERENCES app_registry_api_endpoint(id) ON DELETE CASCADE,
    workspace_id  UUID         NOT NULL,
    role          VARCHAR(50)  NOT NULL,
    note          TEXT,
    display_order INT          NOT NULL DEFAULT 0,
    status        VARCHAR(50)  NOT NULL DEFAULT 'ACTIVE',
    version       INT          NOT NULL DEFAULT 0,
    created_at    TIMESTAMPTZ  NOT NULL,
    updated_at    TIMESTAMPTZ  NOT NULL,
    created_by    VARCHAR(255),
    updated_by    VARCHAR(255),
    CONSTRAINT pk_app_registry_component_api    PRIMARY KEY (id),
    CONSTRAINT uq_app_registry_component_api    UNIQUE (component_id, api_id, role),
    CONSTRAINT chk_app_registry_component_api_role
        CHECK (role IN ('FETCH_OPTIONS','SUBMIT','VALIDATE','LOAD_DATA','AUTOCOMPLETE'))
);
CREATE INDEX idx_app_registry_component_api_component ON app_registry_component_api(component_id);
CREATE INDEX idx_app_registry_component_api_api       ON app_registry_component_api(api_id);
