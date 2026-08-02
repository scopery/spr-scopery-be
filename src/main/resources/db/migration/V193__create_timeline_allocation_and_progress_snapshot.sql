-- Timeline cell buckets: daily planned allocation + progress snapshots

CREATE TABLE IF NOT EXISTS project_task_daily_allocation (
    id UUID NOT NULL,
    project_id UUID NOT NULL,
    task_id UUID NOT NULL,
    work_date DATE NOT NULL,
    planned_minutes INT NOT NULL,
    source VARCHAR(20) NOT NULL,
    version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    CONSTRAINT pk_project_task_daily_allocation PRIMARY KEY (id),
    CONSTRAINT ck_project_task_daily_allocation_minutes CHECK (planned_minutes >= 0),
    CONSTRAINT ck_project_task_daily_allocation_source CHECK (source IN ('AUTO', 'MANUAL')),
    CONSTRAINT uq_project_task_daily_allocation_task_date UNIQUE (task_id, work_date)
);

CREATE INDEX IF NOT EXISTS idx_project_task_daily_allocation_project
    ON project_task_daily_allocation (project_id);
CREATE INDEX IF NOT EXISTS idx_project_task_daily_allocation_task
    ON project_task_daily_allocation (task_id);

CREATE TABLE IF NOT EXISTS project_task_progress_snapshot (
    id UUID NOT NULL,
    project_id UUID NOT NULL,
    task_id UUID NOT NULL,
    snapshot_date DATE NOT NULL,
    progress_percent NUMERIC(5, 2) NOT NULL,
    time_spent_minutes INT,
    note TEXT,
    recorded_by UUID,
    recorded_at TIMESTAMPTZ NOT NULL,
    version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    CONSTRAINT pk_project_task_progress_snapshot PRIMARY KEY (id),
    CONSTRAINT ck_project_task_progress_snapshot_percent CHECK (
        progress_percent >= 0 AND progress_percent <= 100
    ),
    CONSTRAINT uq_project_task_progress_snapshot_task_date UNIQUE (task_id, snapshot_date)
);

CREATE INDEX IF NOT EXISTS idx_project_task_progress_snapshot_project
    ON project_task_progress_snapshot (project_id);
CREATE INDEX IF NOT EXISTS idx_project_task_progress_snapshot_task
    ON project_task_progress_snapshot (task_id);
CREATE INDEX IF NOT EXISTS idx_project_task_progress_snapshot_date
    ON project_task_progress_snapshot (task_id, snapshot_date);
