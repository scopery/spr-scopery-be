-- Phase 25 fix: rename project_* RAID/decision tables to raid_* module prefix (CLAUDE.md §12)
-- Note: V63 is already used by Phase 26; this is V69 (next free Flyway version).

ALTER TABLE IF EXISTS project_raid_item RENAME TO raid_item;
ALTER TABLE IF EXISTS project_raid_action RENAME TO raid_action;
ALTER TABLE IF EXISTS project_raid_link RENAME TO raid_link;
ALTER TABLE IF EXISTS project_decision_record RENAME TO raid_decision_record;
ALTER TABLE IF EXISTS project_decision_option RENAME TO raid_decision_option;
ALTER TABLE IF EXISTS project_decision_impact RENAME TO raid_decision_impact;

-- Primary keys
ALTER TABLE raid_item RENAME CONSTRAINT pk_project_raid_item TO pk_raid_item;
ALTER TABLE raid_action RENAME CONSTRAINT pk_project_raid_action TO pk_raid_action;
ALTER TABLE raid_link RENAME CONSTRAINT pk_project_raid_link TO pk_raid_link;
ALTER TABLE raid_decision_record RENAME CONSTRAINT pk_project_decision_record TO pk_raid_decision_record;
ALTER TABLE raid_decision_option RENAME CONSTRAINT pk_project_decision_option TO pk_raid_decision_option;
ALTER TABLE raid_decision_impact RENAME CONSTRAINT pk_project_decision_impact TO pk_raid_decision_impact;

-- Foreign keys
ALTER TABLE raid_item RENAME CONSTRAINT fk_project_raid_item_project TO fk_raid_item_project_project;
ALTER TABLE raid_action RENAME CONSTRAINT fk_project_raid_action_item TO fk_raid_action_raid_item;
ALTER TABLE raid_link RENAME CONSTRAINT fk_project_raid_link_item TO fk_raid_link_raid_item;
ALTER TABLE raid_decision_record RENAME CONSTRAINT fk_project_decision_record_project TO fk_raid_decision_record_project_project;
ALTER TABLE raid_decision_option RENAME CONSTRAINT fk_project_decision_option_decision TO fk_raid_decision_option_raid_decision_record;
ALTER TABLE raid_decision_impact RENAME CONSTRAINT fk_project_decision_impact_decision TO fk_raid_decision_impact_raid_decision_record;

-- Check constraints
ALTER TABLE raid_item RENAME CONSTRAINT ck_project_raid_item_type TO ck_raid_item_type;
ALTER TABLE raid_action RENAME CONSTRAINT ck_project_raid_action_status TO ck_raid_action_status;
ALTER TABLE raid_decision_record RENAME CONSTRAINT ck_project_decision_record_status TO ck_raid_decision_record_status;

-- Indexes
ALTER INDEX IF EXISTS idx_project_raid_item_project RENAME TO idx_raid_item_project;
ALTER INDEX IF EXISTS idx_project_raid_item_type RENAME TO idx_raid_item_type;
