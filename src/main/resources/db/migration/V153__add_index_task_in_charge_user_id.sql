-- Supports My Work workspace-level query: filter by assignee across all projects
CREATE INDEX IF NOT EXISTS idx_project_task_in_charge_user_id
    ON project_task (in_charge_user_id);

-- Composite index for the primary My Work filter pattern
CREATE INDEX IF NOT EXISTS idx_project_task_user_project_status
    ON project_task (in_charge_user_id, project_id, status);
