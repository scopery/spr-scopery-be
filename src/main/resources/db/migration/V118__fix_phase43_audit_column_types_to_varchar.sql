-- AuditableJpaEntity.createdBy/updatedBy is String (VARCHAR), not UUID.
-- V115 defined created_by/updated_by as UUID — fix all affected columns.

-- V115 tables that had UUID type
ALTER TABLE ai_recommendation_policy
    ALTER COLUMN created_by TYPE VARCHAR(255) USING created_by::text,
    ALTER COLUMN updated_by TYPE VARCHAR(255) USING updated_by::text;

ALTER TABLE ai_recommendation_run
    ALTER COLUMN created_by TYPE VARCHAR(255) USING created_by::text,
    ALTER COLUMN updated_by TYPE VARCHAR(255) USING updated_by::text;

ALTER TABLE ai_recommendation_suggestion
    ALTER COLUMN created_by TYPE VARCHAR(255) USING created_by::text,
    ALTER COLUMN updated_by TYPE VARCHAR(255) USING updated_by::text;

-- V117-added columns (all were UUID NULL — fix to VARCHAR)
ALTER TABLE ai_recommendation_suggestion_item
    ALTER COLUMN created_by TYPE VARCHAR(255) USING created_by::text,
    ALTER COLUMN updated_by TYPE VARCHAR(255) USING updated_by::text;

ALTER TABLE ai_recommendation_evidence
    ALTER COLUMN created_by TYPE VARCHAR(255) USING created_by::text,
    ALTER COLUMN updated_by TYPE VARCHAR(255) USING updated_by::text;

ALTER TABLE ai_recommendation_impact
    ALTER COLUMN created_by TYPE VARCHAR(255) USING created_by::text,
    ALTER COLUMN updated_by TYPE VARCHAR(255) USING updated_by::text;

ALTER TABLE ai_recommendation_review
    ALTER COLUMN created_by TYPE VARCHAR(255) USING created_by::text,
    ALTER COLUMN updated_by TYPE VARCHAR(255) USING updated_by::text;

ALTER TABLE ai_recommendation_feedback
    ALTER COLUMN created_by TYPE VARCHAR(255) USING created_by::text,
    ALTER COLUMN updated_by TYPE VARCHAR(255) USING updated_by::text;

ALTER TABLE ai_recommendation_suppression
    ALTER COLUMN created_by TYPE VARCHAR(255) USING created_by::text,
    ALTER COLUMN updated_by TYPE VARCHAR(255) USING updated_by::text;

ALTER TABLE ai_recommendation_pack_definition
    ALTER COLUMN created_by TYPE VARCHAR(255) USING created_by::text,
    ALTER COLUMN updated_by TYPE VARCHAR(255) USING updated_by::text;

ALTER TABLE ai_recommendation_detector_definition
    ALTER COLUMN created_by TYPE VARCHAR(255) USING created_by::text,
    ALTER COLUMN updated_by TYPE VARCHAR(255) USING updated_by::text;

ALTER TABLE ai_recommendation_next_best_action_definition
    ALTER COLUMN created_by TYPE VARCHAR(255) USING created_by::text,
    ALTER COLUMN updated_by TYPE VARCHAR(255) USING updated_by::text;

ALTER TABLE ai_recommendation_schema_definition
    ALTER COLUMN created_by TYPE VARCHAR(255) USING created_by::text,
    ALTER COLUMN updated_by TYPE VARCHAR(255) USING updated_by::text;
