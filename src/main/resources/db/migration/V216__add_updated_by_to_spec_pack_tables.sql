ALTER TABLE spec_pack_agent_session  ADD COLUMN IF NOT EXISTS updated_by VARCHAR(255);
ALTER TABLE spec_pack_agent_stage    ADD COLUMN IF NOT EXISTS updated_by VARCHAR(255);
ALTER TABLE spec_pack_block_revision ADD COLUMN IF NOT EXISTS updated_by VARCHAR(255);
ALTER TABLE spec_pack_clarification  ADD COLUMN IF NOT EXISTS updated_by VARCHAR(255);
ALTER TABLE spec_pack_outline        ADD COLUMN IF NOT EXISTS updated_by VARCHAR(255);
ALTER TABLE spec_pack_version        ADD COLUMN IF NOT EXISTS updated_by VARCHAR(255);
