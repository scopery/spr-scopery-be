-- Phase 05: Event Registry governance fields, deprecation metadata, variable sensitivity

ALTER TABLE app_event_definition
    ALTER COLUMN code TYPE VARCHAR(150);

ALTER TABLE app_event_definition
    ADD COLUMN IF NOT EXISTS data_classification VARCHAR(50),
    ADD COLUMN IF NOT EXISTS owner_module VARCHAR(100),
    ADD COLUMN IF NOT EXISTS is_system_event BOOLEAN NOT NULL DEFAULT TRUE,
    ADD COLUMN IF NOT EXISTS deprecated_at TIMESTAMPTZ,
    ADD COLUMN IF NOT EXISTS deprecated_by UUID,
    ADD COLUMN IF NOT EXISTS replacement_event_definition_id UUID;

ALTER TABLE app_event_definition
    DROP CONSTRAINT IF EXISTS fk_app_event_definition_replacement;

ALTER TABLE app_event_definition
    ADD CONSTRAINT fk_app_event_definition_replacement
        FOREIGN KEY (replacement_event_definition_id) REFERENCES app_event_definition (id);

CREATE INDEX IF NOT EXISTS idx_app_event_definition_data_classification
    ON app_event_definition (data_classification);

CREATE INDEX IF NOT EXISTS idx_app_event_definition_owner_module
    ON app_event_definition (owner_module);

ALTER TABLE app_event_variable
    ADD COLUMN IF NOT EXISTS sensitive BOOLEAN NOT NULL DEFAULT FALSE;

ALTER TABLE app_event_definition
    DROP CONSTRAINT IF EXISTS chk_app_event_definition_status;

ALTER TABLE app_event_definition
    ADD CONSTRAINT chk_app_event_definition_status
        CHECK (status IN ('ACTIVE', 'INACTIVE', 'DEPRECATED'));

ALTER TABLE app_event_definition
    DROP CONSTRAINT IF EXISTS chk_app_event_definition_event_version;

ALTER TABLE app_event_definition
    ADD CONSTRAINT chk_app_event_definition_event_version
        CHECK (event_version >= 1);

ALTER TABLE app_event_definition
    DROP CONSTRAINT IF EXISTS chk_app_event_definition_data_classification;

ALTER TABLE app_event_definition
    ADD CONSTRAINT chk_app_event_definition_data_classification
        CHECK (
            data_classification IS NULL
            OR data_classification IN (
                'PUBLIC_INTERNAL',
                'INTERNAL',
                'SENSITIVE',
                'SECURITY',
                'FINANCIAL',
                'PERSONAL_DATA',
                'CONFIDENTIAL_CLIENT'
            )
        );
