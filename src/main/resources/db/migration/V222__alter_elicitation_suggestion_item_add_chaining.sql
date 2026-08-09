-- Add chaining support to elicitation_suggestion_item.
-- requirement_id: which requirement in the scope triggered this suggestion item
--   (used to group items by requirement in the UI and for per-requirement AI chaining).
-- chaining_context_json: accumulated summary of suggestions for requirements already processed
--   before this one; stored so GenerateSuggestionsAction output is traceable.

ALTER TABLE elicitation_suggestion_item
    ADD COLUMN IF NOT EXISTS requirement_id        UUID,
    ADD COLUMN IF NOT EXISTS chaining_context_json TEXT;

CREATE INDEX IF NOT EXISTS idx_elicitation_suggestion_item_req
    ON elicitation_suggestion_item(suggestion_id, requirement_id);
