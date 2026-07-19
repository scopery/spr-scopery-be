-- Phase 13: dependency-aware, capacity-based project scheduling
ALTER TABLE project_project ADD COLUMN IF NOT EXISTS current_schedule_run_id UUID;

CREATE TABLE IF NOT EXISTS project_schedule_run (
    id UUID NOT NULL, project_id UUID NOT NULL, workspace_id UUID NOT NULL,
    status VARCHAR(50) NOT NULL, algorithm_version VARCHAR(50) NOT NULL,
    planning_start_date DATE NOT NULL, planning_end_date DATE NOT NULL,
    input_summary_json JSONB, result_summary_json JSONB,
    error_code VARCHAR(150), error_message TEXT,
    started_at TIMESTAMPTZ, completed_at TIMESTAMPTZ,
    actor_user_id UUID, trace_id VARCHAR(100), version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255), updated_by VARCHAR(255),
    CONSTRAINT pk_project_schedule_run PRIMARY KEY (id),
    CONSTRAINT ck_project_schedule_run_dates CHECK (planning_end_date >= planning_start_date),
    CONSTRAINT ck_project_schedule_run_status CHECK (status IN ('PENDING','RUNNING','COMPLETED','FAILED','CANCELLED'))
);
CREATE INDEX IF NOT EXISTS idx_project_schedule_run_project ON project_schedule_run(project_id);
CREATE INDEX IF NOT EXISTS idx_project_schedule_run_workspace ON project_schedule_run(workspace_id);
CREATE INDEX IF NOT EXISTS idx_project_schedule_run_status ON project_schedule_run(status);

CREATE TABLE IF NOT EXISTS project_task_schedule (
    id UUID NOT NULL, schedule_run_id UUID NOT NULL, project_id UUID NOT NULL, task_id UUID NOT NULL,
    assignee_user_id UUID, workspace_member_id UUID,
    estimated_start_date DATE, estimated_finish_date DATE,
    scheduled_hours DECIMAL(12,2) NOT NULL DEFAULT 0,
    unscheduled_hours DECIMAL(12,2) NOT NULL DEFAULT 0,
    due_date DATE, due_date_capacity_gap_hours DECIMAL(12,2) NOT NULL DEFAULT 0,
    risk_status VARCHAR(50) NOT NULL, schedule_status VARCHAR(50) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255), updated_by VARCHAR(255),
    CONSTRAINT pk_project_task_schedule PRIMARY KEY (id),
    CONSTRAINT fk_project_task_schedule_run FOREIGN KEY (schedule_run_id) REFERENCES project_schedule_run(id) ON DELETE CASCADE,
    CONSTRAINT uq_project_task_schedule_run_task UNIQUE(schedule_run_id, task_id),
    CONSTRAINT ck_project_task_schedule_hours CHECK (scheduled_hours >= 0 AND unscheduled_hours >= 0 AND due_date_capacity_gap_hours >= 0)
);
CREATE INDEX IF NOT EXISTS idx_project_task_schedule_project ON project_task_schedule(project_id);
CREATE INDEX IF NOT EXISTS idx_project_task_schedule_task ON project_task_schedule(task_id);
CREATE INDEX IF NOT EXISTS idx_project_task_schedule_risk ON project_task_schedule(risk_status);

CREATE TABLE IF NOT EXISTS project_scheduled_daily_work (
    id UUID NOT NULL, schedule_run_id UUID NOT NULL, task_schedule_id UUID NOT NULL,
    project_id UUID NOT NULL, task_id UUID NOT NULL, workspace_member_id UUID NOT NULL, user_id UUID NOT NULL,
    work_date DATE NOT NULL, planned_hours DECIMAL(12,2) NOT NULL,
    capacity_hours DECIMAL(12,2) NOT NULL, remaining_capacity_after DECIMAL(12,2) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255), updated_by VARCHAR(255),
    CONSTRAINT pk_project_scheduled_daily_work PRIMARY KEY(id),
    CONSTRAINT fk_project_daily_work_run FOREIGN KEY(schedule_run_id) REFERENCES project_schedule_run(id) ON DELETE CASCADE,
    CONSTRAINT fk_project_daily_work_task_schedule FOREIGN KEY(task_schedule_id) REFERENCES project_task_schedule(id) ON DELETE CASCADE,
    CONSTRAINT uq_project_daily_work_run_task_user_date UNIQUE(schedule_run_id, task_id, user_id, work_date),
    CONSTRAINT ck_project_daily_work_hours CHECK(planned_hours > 0 AND capacity_hours >= 0 AND remaining_capacity_after >= 0)
);
CREATE INDEX IF NOT EXISTS idx_project_daily_work_run_date ON project_scheduled_daily_work(schedule_run_id, work_date);
CREATE INDEX IF NOT EXISTS idx_project_daily_work_user_date ON project_scheduled_daily_work(user_id, work_date);

CREATE TABLE IF NOT EXISTS project_scheduling_issue (
    id UUID NOT NULL, schedule_run_id UUID NOT NULL, project_id UUID NOT NULL,
    task_id UUID, user_id UUID, workspace_member_id UUID,
    issue_type VARCHAR(100) NOT NULL, severity VARCHAR(50) NOT NULL,
    message TEXT NOT NULL, issue_date DATE, details_json JSONB,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255), updated_by VARCHAR(255),
    CONSTRAINT pk_project_scheduling_issue PRIMARY KEY(id),
    CONSTRAINT fk_project_scheduling_issue_run FOREIGN KEY(schedule_run_id) REFERENCES project_schedule_run(id) ON DELETE CASCADE,
    CONSTRAINT ck_project_scheduling_issue_severity CHECK(severity IN ('INFO','WARNING','ERROR','BLOCKER'))
);
CREATE INDEX IF NOT EXISTS idx_project_scheduling_issue_run ON project_scheduling_issue(schedule_run_id);
CREATE INDEX IF NOT EXISTS idx_project_scheduling_issue_task ON project_scheduling_issue(task_id);
CREATE INDEX IF NOT EXISTS idx_project_scheduling_issue_type ON project_scheduling_issue(issue_type);
