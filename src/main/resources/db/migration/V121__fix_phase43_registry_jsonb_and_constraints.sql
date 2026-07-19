-- Fix multiple issues in phase 43 registry tables:
-- 1. ai_recommendation_schema_definition: target_entity_type NOT NULL but domain allows null
-- 2. ai_recommendation_pack_definition: detector_codes/allowed_trigger_modes are JSONB but JPA maps as TEXT
--    + expiry_minutes CHECK constraint rejects 0 but PHASE21 compat pack uses 0
-- 3. ai_recommendation_next_best_action_definition: applicable_suggestion_types JSONB→TEXT
-- 4. ai_recommendation_detector_definition: expiry CHECK rejects 0

-- schema_definition: allow null target_entity_type
ALTER TABLE ai_recommendation_schema_definition
    ALTER COLUMN target_entity_type DROP NOT NULL;

-- pack_definition: convert JSONB list columns to TEXT, relax constraints
ALTER TABLE ai_recommendation_pack_definition
    DROP CONSTRAINT ck_ai_rec_pack_detectors,
    DROP CONSTRAINT ck_ai_rec_pack_triggers,
    DROP CONSTRAINT ck_ai_rec_pack_expiry;

ALTER TABLE ai_recommendation_pack_definition
    ALTER COLUMN detector_codes        TYPE TEXT USING detector_codes::text,
    ALTER COLUMN allowed_trigger_modes TYPE TEXT USING allowed_trigger_modes::text,
    ALTER COLUMN detector_codes        DROP NOT NULL;

ALTER TABLE ai_recommendation_pack_definition
    ADD CONSTRAINT ck_ai_rec_pack_expiry CHECK (default_expiry_minutes >= 0);

-- nba_definition: convert applicable_suggestion_types JSONB→TEXT
ALTER TABLE ai_recommendation_next_best_action_definition
    DROP CONSTRAINT ck_ai_rec_nba_types;

ALTER TABLE ai_recommendation_next_best_action_definition
    ALTER COLUMN applicable_suggestion_types TYPE TEXT USING applicable_suggestion_types::text,
    ALTER COLUMN applicable_suggestion_types DROP NOT NULL;

-- detector_definition: relax expiry constraint to allow 0
ALTER TABLE ai_recommendation_detector_definition
    DROP CONSTRAINT ck_ai_rec_detector_expiry;

ALTER TABLE ai_recommendation_detector_definition
    ADD CONSTRAINT ck_ai_rec_detector_expiry CHECK (default_expiry_minutes >= 0);
