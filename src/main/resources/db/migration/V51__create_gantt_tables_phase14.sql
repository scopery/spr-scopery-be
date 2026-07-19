-- Phase 14: WBS-driven Gantt — milestones + manual schedule overrides
-- GanttItem is a projection only (no persistent table).

CREATE TABLE IF NOT EXISTS project_milestone (
    id UUID NOT NULL,
    project_id UUID NOT NULL,
    project_phase_id UUID,
    wbs_node_id UUID,
    code VARCHAR(100),
    name VARCHAR(255) NOT NULL,
    description TEXT,
    milestone_date DATE NOT NULL,
    status VARCHAR(50) NOT NULL,
    sort_order INT,
    achieved_at TIMESTAMPTZ,
    achieved_by UUID,
    archived_at TIMESTAMPTZ,
    archived_by UUID,
    version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    CONSTRAINT pk_project_milestone PRIMARY KEY (id),
    CONSTRAINT ck_project_milestone_status CHECK (status IN ('PLANNED', 'ACHIEVED', 'MISSED', 'ARCHIVED'))
);

CREATE UNIQUE INDEX IF NOT EXISTS uq_project_milestone_project_code
    ON project_milestone (project_id, code)
    WHERE code IS NOT NULL;

CREATE INDEX IF NOT EXISTS idx_project_milestone_project_id ON project_milestone (project_id);
CREATE INDEX IF NOT EXISTS idx_project_milestone_milestone_date ON project_milestone (milestone_date);
CREATE INDEX IF NOT EXISTS idx_project_milestone_status ON project_milestone (status);

CREATE TABLE IF NOT EXISTS project_task_schedule_override (
    id UUID NOT NULL,
    project_id UUID NOT NULL,
    task_id UUID NOT NULL,
    override_type VARCHAR(50) NOT NULL,
    manual_start_date DATE,
    manual_finish_date DATE,
    manual_due_date DATE,
    reason TEXT NOT NULL,
    status VARCHAR(50) NOT NULL,
    cancelled_at TIMESTAMPTZ,
    cancelled_by UUID,
    version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    CONSTRAINT pk_project_task_schedule_override PRIMARY KEY (id),
    CONSTRAINT ck_project_task_schedule_override_type CHECK (
        override_type IN ('PIN_START', 'PIN_FINISH', 'PIN_RANGE', 'DUE_DATE_OVERRIDE')
    ),
    CONSTRAINT ck_project_task_schedule_override_status CHECK (
        status IN ('ACTIVE', 'INACTIVE', 'CANCELLED', 'ARCHIVED')
    ),
    CONSTRAINT ck_project_task_schedule_override_dates CHECK (
        manual_finish_date IS NULL OR manual_start_date IS NULL OR manual_finish_date >= manual_start_date
    )
);

-- One ACTIVE override per task
CREATE UNIQUE INDEX IF NOT EXISTS uq_project_task_schedule_override_active_task
    ON project_task_schedule_override (task_id)
    WHERE status = 'ACTIVE';

CREATE INDEX IF NOT EXISTS idx_project_task_schedule_override_project ON project_task_schedule_override (project_id);
CREATE INDEX IF NOT EXISTS idx_project_task_schedule_override_task ON project_task_schedule_override (task_id);
CREATE INDEX IF NOT EXISTS idx_project_task_schedule_override_status ON project_task_schedule_override (status);
