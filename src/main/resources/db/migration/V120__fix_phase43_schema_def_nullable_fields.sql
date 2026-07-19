-- SuggestionSchemaDefinition domain allows null for requiredTargetCapabilityCode,
-- jsonSchema, and sensitiveFieldPaths (empty list). Drop NOT NULL constraints
-- so the seed initializer can insert definitions without these optional fields.
ALTER TABLE ai_recommendation_schema_definition
    ALTER COLUMN required_target_capability_code DROP NOT NULL,
    ALTER COLUMN json_schema                     DROP NOT NULL,
    ALTER COLUMN sensitive_field_paths           DROP NOT NULL;
