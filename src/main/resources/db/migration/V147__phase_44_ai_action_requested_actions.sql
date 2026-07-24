-- Phase 44: store requested actions JSON on the action request so planning orchestrator
-- can generate real steps without needing to re-resolve from the conversation turn.
ALTER TABLE ai_action_request
    ADD COLUMN IF NOT EXISTS requested_actions_json TEXT;
