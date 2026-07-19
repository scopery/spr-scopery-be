-- Phase 09: Harden Project Core / WBS / Task foundation
-- - Wire organization_id on project (column already existed in V39)
-- - Lifecycle timestamps
-- - estimate_hours required and > 0
-- - Optional WBS link on task
-- - Root sibling sort_order uniqueness
-- - Backfill estimate hours for legacy null rows

-- ─────────────────────────────────────────────────────────────────────────────
-- project_project lifecycle timestamps
-- ─────────────────────────────────────────────────────────────────────────────
ALTER TABLE project_project
    ADD COLUMN IF NOT EXISTS activated_at TIMESTAMPTZ,
    ADD COLUMN IF NOT EXISTS activated_by UUID,
    ADD COLUMN IF NOT EXISTS archived_at TIMESTAMPTZ,
    ADD COLUMN IF NOT EXISTS archived_by UUID;

CREATE INDEX IF NOT EXISTS idx_project_project_organization_id
    ON project_project (organization_id);

-- ─────────────────────────────────────────────────────────────────────────────
-- project_project_phase lifecycle timestamps
-- ─────────────────────────────────────────────────────────────────────────────
ALTER TABLE project_project_phase
    ADD COLUMN IF NOT EXISTS started_at TIMESTAMPTZ,
    ADD COLUMN IF NOT EXISTS completed_at TIMESTAMPTZ,
    ADD COLUMN IF NOT EXISTS archived_at TIMESTAMPTZ;

-- ─────────────────────────────────────────────────────────────────────────────
-- project_wbs_node: unique sort among root siblings
-- ─────────────────────────────────────────────────────────────────────────────
CREATE UNIQUE INDEX IF NOT EXISTS uq_project_wbs_node_root_sort
    ON project_wbs_node (project_id, sort_order)
    WHERE parent_id IS NULL AND status = 'ACTIVE';

-- ─────────────────────────────────────────────────────────────────────────────
-- project_task: required estimateHours, optional WBS, lifecycle timestamps
-- ─────────────────────────────────────────────────────────────────────────────
UPDATE project_task
SET estimate_hours = 1
WHERE estimate_hours IS NULL OR estimate_hours <= 0;

ALTER TABLE project_task
    ALTER COLUMN estimate_hours SET NOT NULL;

ALTER TABLE project_task
    DROP CONSTRAINT IF EXISTS chk_project_task_estimate_hours_positive;

ALTER TABLE project_task
    ADD CONSTRAINT chk_project_task_estimate_hours_positive
        CHECK (estimate_hours > 0);

ALTER TABLE project_task
    ALTER COLUMN wbs_node_id DROP NOT NULL;

ALTER TABLE project_task
    ADD COLUMN IF NOT EXISTS started_at TIMESTAMPTZ,
    ADD COLUMN IF NOT EXISTS started_by UUID,
    ADD COLUMN IF NOT EXISTS blocked_at TIMESTAMPTZ,
    ADD COLUMN IF NOT EXISTS completed_at TIMESTAMPTZ,
    ADD COLUMN IF NOT EXISTS completed_by UUID,
    ADD COLUMN IF NOT EXISTS cancelled_at TIMESTAMPTZ,
    ADD COLUMN IF NOT EXISTS cancelled_by UUID,
    ADD COLUMN IF NOT EXISTS archived_at TIMESTAMPTZ,
    ADD COLUMN IF NOT EXISTS archived_by UUID;
