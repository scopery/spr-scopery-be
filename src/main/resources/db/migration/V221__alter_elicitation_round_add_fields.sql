-- Add fields to elicitation_round for the round-based evaluation architecture.
-- scope_snapshot_json: entity graph at the moment of round generation (for AI context + before/after tree).
-- should_continue: AI decision after evaluation — false means scope is sufficiently clear.
-- evaluated_at: timestamp when SubmitRoundAction completed AI evaluation.

ALTER TABLE elicitation_round
    ADD COLUMN IF NOT EXISTS scope_snapshot_json TEXT,
    ADD COLUMN IF NOT EXISTS should_continue     BOOLEAN,
    ADD COLUMN IF NOT EXISTS evaluated_at        TIMESTAMPTZ;

-- Update status constraint: new lifecycle is ACTIVE → EVALUATED only.
-- Drop old constraints by both possible names (V214 used ck_elicitation_round_status).
DO $$ BEGIN
    ALTER TABLE elicitation_round DROP CONSTRAINT IF EXISTS ck_elicitation_round_status;
EXCEPTION WHEN OTHERS THEN NULL; END $$;

DO $$ BEGIN
    ALTER TABLE elicitation_round DROP CONSTRAINT IF EXISTS elicitation_round_status_check;
EXCEPTION WHEN OTHERS THEN NULL; END $$;

-- Migrate existing rows with old status values to the new lifecycle statuses.
UPDATE elicitation_round
SET status = CASE
    WHEN status IN ('SUBMITTED', 'PROCESSING', 'COMPLETED') THEN 'EVALUATED'
    WHEN status = 'DRAFT' THEN 'ACTIVE'
    ELSE status
END
WHERE status NOT IN ('ACTIVE', 'EVALUATED');

DO $$ BEGIN
    ALTER TABLE elicitation_round
        ADD CONSTRAINT chk_elicitation_round_status
        CHECK (status IN ('ACTIVE', 'EVALUATED'));
EXCEPTION WHEN duplicate_object THEN NULL; END $$;
