CREATE TABLE project_task_role_contribution (
    id                      UUID        NOT NULL,
    project_id              UUID        NOT NULL,
    task_id                 UUID        NOT NULL,
    user_id                 UUID,
    cost_role_code          VARCHAR(100),
    cost_role_name          VARCHAR(255),
    planned_hours           NUMERIC(10,2),
    actual_hours            NUMERIC(10,2) NOT NULL DEFAULT 0,
    rate_snapshot_per_hour  NUMERIC(14,4),
    currency_code           VARCHAR(10),
    period_start            DATE,
    period_end              DATE,
    notes                   TEXT,
    created_at              TIMESTAMP WITH TIME ZONE,
    updated_at              TIMESTAMP WITH TIME ZONE,
    created_by              VARCHAR(255),
    updated_by              VARCHAR(255),
    CONSTRAINT pk_project_task_role_contribution PRIMARY KEY (id),
    CONSTRAINT fk_task_role_contribution_task FOREIGN KEY (task_id) REFERENCES project_task(id) ON DELETE CASCADE
);

CREATE INDEX idx_task_role_contribution_task    ON project_task_role_contribution(task_id);
CREATE INDEX idx_task_role_contribution_project ON project_task_role_contribution(project_id);
