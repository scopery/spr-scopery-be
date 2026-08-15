-- 1. Fix cascade: screen_field.data_entity_field_id should SET NULL on delete (not RESTRICT)
ALTER TABLE app_registry_screen_field
    DROP CONSTRAINT IF EXISTS app_registry_screen_field_data_entity_field_id_fkey,
    ADD CONSTRAINT fk_screen_field_data_entity_field
        FOREIGN KEY (data_entity_field_id)
        REFERENCES app_registry_data_entity_field(id)
        ON DELETE SET NULL;

-- 2. Extend data_entity_field with ERD-relevant columns
ALTER TABLE app_registry_data_entity_field
    ADD COLUMN IF NOT EXISTS is_primary_key BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS default_value  TEXT,
    ADD COLUMN IF NOT EXISTS precision      INT,
    ADD COLUMN IF NOT EXISTS scale          INT;

-- 3. Expand data_type enum to cover more DB types
ALTER TABLE app_registry_data_entity_field
    DROP CONSTRAINT chk_data_entity_field_data_type,
    ADD CONSTRAINT chk_data_entity_field_data_type
        CHECK (data_type IN (
            'VARCHAR','TEXT','UUID',
            'INTEGER','BIGINT','SMALLINT',
            'DECIMAL','FLOAT','DOUBLE',
            'BOOLEAN',
            'DATE','TIMESTAMP','TIMESTAMPTZ',
            'JSONB','ARRAY'
        ));

-- 4. Create entity relation table (the ERD edges)
CREATE TABLE app_registry_data_entity_relation (
    id               UUID         NOT NULL,
    source_entity_id UUID         NOT NULL REFERENCES app_registry_data_entity(id) ON DELETE CASCADE,
    target_entity_id UUID         NOT NULL REFERENCES app_registry_data_entity(id) ON DELETE CASCADE,
    workspace_id     UUID         NOT NULL,
    relation_type    VARCHAR(20)  NOT NULL,
    source_column    VARCHAR(100),
    label            VARCHAR(255),
    note             TEXT,
    status           VARCHAR(50)  NOT NULL DEFAULT 'ACTIVE',
    version          INT          NOT NULL DEFAULT 0,
    created_at       TIMESTAMPTZ  NOT NULL,
    updated_at       TIMESTAMPTZ  NOT NULL,
    created_by       VARCHAR(255),
    updated_by       VARCHAR(255),
    CONSTRAINT pk_app_registry_data_entity_relation  PRIMARY KEY (id),
    CONSTRAINT uq_app_registry_data_entity_relation  UNIQUE (source_entity_id, target_entity_id, relation_type),
    CONSTRAINT chk_app_registry_data_entity_relation_type
        CHECK (relation_type IN ('ONE_TO_ONE','ONE_TO_MANY','MANY_TO_MANY'))
);
CREATE INDEX idx_app_registry_data_entity_relation_source ON app_registry_data_entity_relation(source_entity_id);
CREATE INDEX idx_app_registry_data_entity_relation_target ON app_registry_data_entity_relation(target_entity_id);
