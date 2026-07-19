-- Fix missing audit columns and version_lock for phase 43 ai_recommendation tables.
-- All JPA entities extend AuditableJpaEntity which requires created_at, updated_at, created_by, updated_by.
-- Some tables also need version_lock for optimistic locking (@Version).

-- Tables from V115 missing updated_at (immutable-insert entities)
ALTER TABLE ai_recommendation_suggestion_item
    ADD COLUMN updated_at   TIMESTAMPTZ NULL,
    ADD COLUMN created_by   UUID NULL,
    ADD COLUMN updated_by   UUID NULL;

ALTER TABLE ai_recommendation_evidence
    ADD COLUMN updated_at   TIMESTAMPTZ NULL,
    ADD COLUMN created_by   UUID NULL,
    ADD COLUMN updated_by   UUID NULL;

ALTER TABLE ai_recommendation_impact
    ADD COLUMN updated_at   TIMESTAMPTZ NULL,
    ADD COLUMN created_by   UUID NULL,
    ADD COLUMN updated_by   UUID NULL;

ALTER TABLE ai_recommendation_review
    ADD COLUMN updated_at   TIMESTAMPTZ NULL,
    ADD COLUMN created_by   UUID NULL,
    ADD COLUMN updated_by   UUID NULL;

ALTER TABLE ai_recommendation_feedback
    ADD COLUMN updated_at   TIMESTAMPTZ NULL,
    ADD COLUMN created_by   UUID NULL,
    ADD COLUMN updated_by   UUID NULL;

-- Tables from V115 that have updated_at but missing created_by / updated_by
ALTER TABLE ai_recommendation_suppression
    ADD COLUMN created_by   UUID NULL,
    ADD COLUMN updated_by   UUID NULL;

-- Tables from V116 missing created_by / updated_by
ALTER TABLE ai_recommendation_pack_definition
    ADD COLUMN created_by   UUID NULL,
    ADD COLUMN updated_by   UUID NULL;

ALTER TABLE ai_recommendation_detector_definition
    ADD COLUMN created_by   UUID NULL,
    ADD COLUMN updated_by   UUID NULL;

ALTER TABLE ai_recommendation_next_best_action_definition
    ADD COLUMN created_by   UUID NULL,
    ADD COLUMN updated_by   UUID NULL;

-- ai_recommendation_schema_definition: missing created_by/updated_by + version named wrong
ALTER TABLE ai_recommendation_schema_definition
    ADD COLUMN created_by   UUID NULL,
    ADD COLUMN updated_by   UUID NULL;

ALTER TABLE ai_recommendation_schema_definition
    RENAME COLUMN version TO version_lock;
