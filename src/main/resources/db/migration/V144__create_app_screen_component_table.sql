CREATE TABLE app_screen_component (
    screen_id     UUID        NOT NULL REFERENCES app_registry_screen(id) ON DELETE CASCADE,
    component_id  UUID        NOT NULL REFERENCES app_registry_component(id) ON DELETE CASCADE,
    section_id    UUID,
    display_order INTEGER     NOT NULL DEFAULT 0,
    note          TEXT,
    created_at    TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by    VARCHAR(255) NOT NULL DEFAULT 'SYSTEM',
    CONSTRAINT pk_app_screen_component PRIMARY KEY (screen_id, component_id)
);
CREATE INDEX IF NOT EXISTS idx_app_screen_component_screen ON app_screen_component(screen_id);
CREATE INDEX IF NOT EXISTS idx_app_screen_component_comp ON app_screen_component(component_id);
