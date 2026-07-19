-- All JPA entities in phase 43 core map list/JSON fields as String (TEXT).
-- V115 created them as JSONB — convert to TEXT and drop JSONB-specific constraints.

-- ai_recommendation_policy
ALTER TABLE ai_recommendation_policy
    DROP CONSTRAINT ck_ai_rec_policy_trigger_modes,
    DROP CONSTRAINT ck_ai_rec_policy_pack_codes;

ALTER TABLE ai_recommendation_policy
    ALTER COLUMN trigger_modes TYPE TEXT USING trigger_modes::text,
    ALTER COLUMN pack_codes    TYPE TEXT USING pack_codes::text;

-- ai_recommendation_run
ALTER TABLE ai_recommendation_run
    DROP CONSTRAINT ck_ai_rec_run_pack_codes,
    DROP CONSTRAINT ck_ai_rec_run_detector_codes,
    DROP CONSTRAINT ck_ai_rec_run_trace_ids;

ALTER TABLE ai_recommendation_run
    ALTER COLUMN requested_pack_codes TYPE TEXT USING requested_pack_codes::text,
    ALTER COLUMN detector_codes       TYPE TEXT USING detector_codes::text;

-- ai_recommendation_suggestion_item
ALTER TABLE ai_recommendation_suggestion_item
    DROP CONSTRAINT ck_ai_rec_item_payload,
    DROP CONSTRAINT ck_ai_rec_item_before;

ALTER TABLE ai_recommendation_suggestion_item
    ALTER COLUMN proposed_payload       TYPE TEXT USING proposed_payload::text,
    ALTER COLUMN masked_before_snapshot TYPE TEXT USING masked_before_snapshot::text;

-- ai_recommendation_impact
ALTER TABLE ai_recommendation_impact
    DROP CONSTRAINT ck_ai_rec_impact_assumptions;

ALTER TABLE ai_recommendation_impact
    ALTER COLUMN assumptions TYPE TEXT USING assumptions::text;

-- ai_recommendation_review: column also needs rename (JPA expects edited_items_json)
ALTER TABLE ai_recommendation_review
    DROP CONSTRAINT ck_ai_rec_review_items;

ALTER TABLE ai_recommendation_review
    RENAME COLUMN edited_items TO edited_items_json;

ALTER TABLE ai_recommendation_review
    ALTER COLUMN edited_items_json TYPE TEXT USING edited_items_json::text;
