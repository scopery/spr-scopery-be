CREATE TABLE app_function_api (
    function_id     UUID        NOT NULL REFERENCES app_functional_item(id) ON DELETE CASCADE,
    api_endpoint_id UUID        NOT NULL REFERENCES app_registry_api_endpoint(id) ON DELETE CASCADE,
    note            TEXT,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by      VARCHAR(255) NOT NULL DEFAULT 'SYSTEM',
    CONSTRAINT pk_app_function_api PRIMARY KEY (function_id, api_endpoint_id)
);
CREATE INDEX IF NOT EXISTS idx_app_function_api_fi ON app_function_api(function_id);
CREATE INDEX IF NOT EXISTS idx_app_function_api_endpoint ON app_function_api(api_endpoint_id);
