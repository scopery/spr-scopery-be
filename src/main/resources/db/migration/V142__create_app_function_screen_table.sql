CREATE TABLE app_function_screen (
    function_id UUID        NOT NULL REFERENCES app_functional_item(id) ON DELETE CASCADE,
    screen_id   UUID        NOT NULL REFERENCES app_registry_screen(id) ON DELETE CASCADE,
    note        TEXT,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by  VARCHAR(255) NOT NULL DEFAULT 'SYSTEM',
    CONSTRAINT pk_app_function_screen PRIMARY KEY (function_id, screen_id)
);
CREATE INDEX IF NOT EXISTS idx_app_function_screen_fi ON app_function_screen(function_id);
CREATE INDEX IF NOT EXISTS idx_app_function_screen_screen ON app_function_screen(screen_id);
