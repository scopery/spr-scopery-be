-- Fix column name mismatches between JPA entities and migration V125
-- ai_action_confirmation: confirmed_by_user_id → confirmed_by
ALTER TABLE ai_action_confirmation
    RENAME COLUMN confirmed_by_user_id TO confirmed_by;

-- ai_action_execution: initiated_by_user_id → initiated_by
ALTER TABLE ai_action_execution
    RENAME COLUMN initiated_by_user_id TO initiated_by;
