-- AI Mapping Summary: compact summary cache with embedding for each traceable entity
-- NOTE: embedding stored as TEXT locally (vector type requires pgvector extension)
CREATE TABLE ai_mapping_summary
(
    id              UUID         NOT NULL,
    entity_type     VARCHAR(50)  NOT NULL,
    entity_id       UUID         NOT NULL,
    entity_version  INTEGER      NOT NULL DEFAULT 0,
    compact_text    TEXT         NOT NULL,
    structured_json TEXT         NOT NULL,
    embedding       TEXT,
    summary_hash    VARCHAR(64)  NOT NULL,
    generated_at    TIMESTAMPTZ  NOT NULL DEFAULT NOW(),

    CONSTRAINT pk_ai_mapping_summary PRIMARY KEY (id),
    CONSTRAINT uq_ai_mapping_summary_entity UNIQUE (entity_type, entity_id),
    CONSTRAINT ck_ai_mapping_summary_entity_type
        CHECK (entity_type IN ('REQUIREMENT', 'FUNCTION', 'USE_CASE', 'TEST_CASE'))
);

CREATE INDEX idx_ai_mapping_summary_entity ON ai_mapping_summary (entity_type, entity_id);
