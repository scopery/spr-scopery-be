ALTER TABLE knowledge_source
    ALTER COLUMN created_by TYPE VARCHAR(100) USING created_by::text,
    ALTER COLUMN updated_by TYPE VARCHAR(100) USING updated_by::text;
