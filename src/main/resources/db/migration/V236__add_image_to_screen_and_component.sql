ALTER TABLE app_registry_screen
    ADD COLUMN mockup_object_key VARCHAR(500);

ALTER TABLE app_registry_component
    ADD COLUMN screenshot_object_key VARCHAR(500);
