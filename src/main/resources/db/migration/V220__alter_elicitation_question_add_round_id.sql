-- Add round_id FK to elicitation_question so each question belongs to a specific round.
-- Add suggested_answers_json to persist AI-generated answer options alongside each question.

ALTER TABLE elicitation_question
    ADD COLUMN round_id               UUID REFERENCES elicitation_round(id),
    ADD COLUMN suggested_answers_json TEXT;

CREATE INDEX idx_elicitation_question_round_id ON elicitation_question(round_id);
