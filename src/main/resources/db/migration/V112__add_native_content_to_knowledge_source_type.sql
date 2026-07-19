-- Add NATIVE_DOCUMENT_CONTENT to knowledge source type check constraint
-- Find and drop existing check constraint on knowledge_source.source_type, then recreate with new value
DO $$
DECLARE
    constraint_name TEXT;
BEGIN
    SELECT con.conname INTO constraint_name
    FROM pg_constraint con
    JOIN pg_class rel ON rel.oid = con.conrelid
    WHERE rel.relname = 'knowledge_source'
      AND con.contype = 'c'
      AND con.conname ILIKE '%source_type%'
    LIMIT 1;

    IF constraint_name IS NOT NULL THEN
        EXECUTE format('ALTER TABLE knowledge_source DROP CONSTRAINT %I', constraint_name);
    END IF;
END $$;

ALTER TABLE knowledge_source
    ADD CONSTRAINT chk_knowledge_source_type
        CHECK (source_type IN ('TASK', 'DOCUMENT_VERSION', 'MEETING_MINUTE', 'NATIVE_DOCUMENT_CONTENT'));
