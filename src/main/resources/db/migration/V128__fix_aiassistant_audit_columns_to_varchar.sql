-- Fix: aiassistant tables have created_by/updated_by as UUID but AuditableJpaEntity stores String (e.g. "SYSTEM")
ALTER TABLE aiassistant_conversation
    ALTER COLUMN created_by TYPE VARCHAR(255) USING created_by::text,
    ALTER COLUMN updated_by TYPE VARCHAR(255) USING updated_by::text;

ALTER TABLE aiassistant_message
    ALTER COLUMN created_by TYPE VARCHAR(255) USING created_by::text,
    ALTER COLUMN updated_by TYPE VARCHAR(255) USING updated_by::text;

ALTER TABLE aiassistant_memory_summary
    ALTER COLUMN created_by TYPE VARCHAR(255) USING created_by::text;

ALTER TABLE aiassistant_guide_definition
    ALTER COLUMN created_by TYPE VARCHAR(255) USING created_by::text,
    ALTER COLUMN updated_by TYPE VARCHAR(255) USING updated_by::text;

ALTER TABLE aiassistant_suggested_question
    ALTER COLUMN created_by TYPE VARCHAR(255) USING created_by::text,
    ALTER COLUMN updated_by TYPE VARCHAR(255) USING updated_by::text;
