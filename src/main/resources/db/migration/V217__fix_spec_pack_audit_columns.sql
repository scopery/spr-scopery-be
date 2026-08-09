-- spec_pack_agent_stage: missing updated_at and created_by
ALTER TABLE spec_pack_agent_stage ADD COLUMN IF NOT EXISTS updated_at TIMESTAMPTZ;
ALTER TABLE spec_pack_agent_stage ADD COLUMN IF NOT EXISTS created_by VARCHAR(255);

-- spec_pack_block_revision: missing updated_at
ALTER TABLE spec_pack_block_revision ADD COLUMN IF NOT EXISTS updated_at TIMESTAMPTZ;

-- spec_pack_clarification: missing created_by
ALTER TABLE spec_pack_clarification ADD COLUMN IF NOT EXISTS created_by VARCHAR(255);

-- spec_pack_outline: missing updated_at
ALTER TABLE spec_pack_outline ADD COLUMN IF NOT EXISTS updated_at TIMESTAMPTZ;

-- spec_pack_version: missing updated_at
ALTER TABLE spec_pack_version ADD COLUMN IF NOT EXISTS updated_at TIMESTAMPTZ;

-- Backfill updated_at = created_at for all existing rows
UPDATE spec_pack_agent_stage   SET updated_at = created_at WHERE updated_at IS NULL;
UPDATE spec_pack_block_revision SET updated_at = created_at WHERE updated_at IS NULL;
UPDATE spec_pack_outline        SET updated_at = created_at WHERE updated_at IS NULL;
UPDATE spec_pack_version        SET updated_at = created_at WHERE updated_at IS NULL;

-- Make updated_at NOT NULL after backfill
ALTER TABLE spec_pack_agent_stage    ALTER COLUMN updated_at SET NOT NULL;
ALTER TABLE spec_pack_block_revision ALTER COLUMN updated_at SET NOT NULL;
ALTER TABLE spec_pack_outline        ALTER COLUMN updated_at SET NOT NULL;
ALTER TABLE spec_pack_version        ALTER COLUMN updated_at SET NOT NULL;
