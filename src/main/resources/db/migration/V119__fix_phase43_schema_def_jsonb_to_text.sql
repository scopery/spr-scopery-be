-- SuggestionSchemaDefinitionJpaEntity maps json_schema and sensitive_field_paths as String (TEXT).
-- The V116 migration created them as JSONB — alter to TEXT for compatibility.
ALTER TABLE ai_recommendation_schema_definition
    DROP CONSTRAINT ck_ai_rec_schema_sensitive,
    DROP CONSTRAINT ck_ai_rec_schema_json;

ALTER TABLE ai_recommendation_schema_definition
    ALTER COLUMN json_schema         TYPE TEXT USING json_schema::text,
    ALTER COLUMN sensitive_field_paths TYPE TEXT USING sensitive_field_paths::text;
