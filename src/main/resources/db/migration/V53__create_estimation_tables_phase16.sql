-- Phase 16: Estimation roll-up foundation
ALTER TABLE project_project ADD COLUMN IF NOT EXISTS current_estimation_run_id UUID;

CREATE TABLE IF NOT EXISTS estimation_run (
    id UUID NOT NULL,
    project_id UUID NOT NULL,
    workspace_id UUID NOT NULL,
    schedule_run_id UUID NULL,
    name VARCHAR(255),
    description TEXT,
    status VARCHAR(50) NOT NULL,
    calculation_mode VARCHAR(50) NOT NULL,
    rate_target_date_strategy VARCHAR(100) NOT NULL,
    currency_policy VARCHAR(100) NOT NULL,
    assumptions_json JSONB,
    result_summary_json JSONB,
    error_code VARCHAR(150),
    error_message TEXT,
    started_at TIMESTAMPTZ,
    completed_at TIMESTAMPTZ,
    actor_user_id UUID,
    trace_id VARCHAR(100),
    version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    CONSTRAINT pk_estimation_run PRIMARY KEY (id),
    CONSTRAINT ck_estimation_run_status CHECK (status IN ('PENDING','RUNNING','COMPLETED','FAILED','CANCELLED')),
    CONSTRAINT ck_estimation_run_calc_mode CHECK (calculation_mode IN (
        'TASK_ESTIMATE_ONLY','TASK_ESTIMATE_WITH_RATE','SCHEDULED_WORK_WITH_RATE')),
    CONSTRAINT ck_estimation_run_rate_strategy CHECK (rate_target_date_strategy IN (
        'PROJECT_PLANNED_START','TASK_DUE_DATE','TASK_SCHEDULED_START','TASK_SCHEDULED_FINISH',
        'RUN_DATE','CUSTOM_DATE','TASK_DUE_DATE_OR_PROJECT_START')),
    CONSTRAINT ck_estimation_run_currency_policy CHECK (currency_policy IN (
        'SINGLE_CURRENCY_REQUIRED','GROUP_BY_CURRENCY'))
);
CREATE INDEX IF NOT EXISTS idx_estimation_run_project ON estimation_run(project_id);
CREATE INDEX IF NOT EXISTS idx_estimation_run_workspace ON estimation_run(workspace_id);
CREATE INDEX IF NOT EXISTS idx_estimation_run_status ON estimation_run(status);

CREATE TABLE IF NOT EXISTS estimation_task_snapshot (
    id UUID NOT NULL,
    estimation_run_id UUID NOT NULL,
    project_id UUID NOT NULL,
    project_phase_id UUID,
    wbs_node_id UUID,
    task_id UUID NOT NULL,
    task_code VARCHAR(100),
    task_title VARCHAR(255) NOT NULL,
    assignee_user_id UUID,
    workspace_member_id UUID,
    cost_role_id UUID,
    cost_role_code VARCHAR(100),
    estimate_hours DECIMAL(12,2) NOT NULL DEFAULT 0,
    rate_target_date DATE NOT NULL,
    rate_card_id UUID,
    rate_card_version_id UUID,
    rate_card_line_id UUID,
    base_cost_rate DECIMAL(18,4),
    adjusted_cost_rate DECIMAL(18,4),
    base_billing_rate DECIMAL(18,4),
    adjusted_billing_rate DECIMAL(18,4),
    currency_code VARCHAR(10),
    inflation_policy_id UUID,
    inflation_percent DECIMAL(8,4),
    years_forward DECIMAL(10,4),
    resolved_at TIMESTAMPTZ,
    estimated_labor_cost DECIMAL(18,4),
    estimated_billing_preview DECIMAL(18,4),
    status VARCHAR(50) NOT NULL,
    issue_code VARCHAR(150),
    issue_message TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    CONSTRAINT pk_estimation_task_snapshot PRIMARY KEY (id),
    CONSTRAINT fk_estimation_task_snapshot_run FOREIGN KEY (estimation_run_id)
        REFERENCES estimation_run(id) ON DELETE CASCADE,
    CONSTRAINT uq_estimation_task_snapshot_run_task UNIQUE (estimation_run_id, task_id),
    CONSTRAINT ck_estimation_task_snapshot_status CHECK (status IN (
        'CALCULATED','RATE_UNRESOLVED','ROLE_UNRESOLVED','TASK_UNESTIMATED','EXCLUDED'))
);
CREATE INDEX IF NOT EXISTS idx_estimation_task_snapshot_run ON estimation_task_snapshot(estimation_run_id);
CREATE INDEX IF NOT EXISTS idx_estimation_task_snapshot_project ON estimation_task_snapshot(project_id);
CREATE INDEX IF NOT EXISTS idx_estimation_task_snapshot_task ON estimation_task_snapshot(task_id);
CREATE INDEX IF NOT EXISTS idx_estimation_task_snapshot_wbs ON estimation_task_snapshot(wbs_node_id);
CREATE INDEX IF NOT EXISTS idx_estimation_task_snapshot_phase ON estimation_task_snapshot(project_phase_id);
CREATE INDEX IF NOT EXISTS idx_estimation_task_snapshot_status ON estimation_task_snapshot(status);

CREATE TABLE IF NOT EXISTS estimation_wbs_rollup (
    id UUID NOT NULL,
    estimation_run_id UUID NOT NULL,
    project_id UUID NOT NULL,
    wbs_node_id UUID NOT NULL,
    parent_wbs_node_id UUID,
    depth INT NOT NULL DEFAULT 0,
    task_count INT NOT NULL DEFAULT 0,
    included_task_count INT NOT NULL DEFAULT 0,
    unresolved_task_count INT NOT NULL DEFAULT 0,
    total_estimate_hours DECIMAL(12,2) NOT NULL DEFAULT 0,
    total_labor_cost DECIMAL(18,4),
    total_billing_preview DECIMAL(18,4),
    currency_code VARCHAR(10),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    CONSTRAINT pk_estimation_wbs_rollup PRIMARY KEY (id),
    CONSTRAINT fk_estimation_wbs_rollup_run FOREIGN KEY (estimation_run_id)
        REFERENCES estimation_run(id) ON DELETE CASCADE,
    CONSTRAINT uq_estimation_wbs_rollup_run_node UNIQUE (estimation_run_id, wbs_node_id)
);
CREATE INDEX IF NOT EXISTS idx_estimation_wbs_rollup_run ON estimation_wbs_rollup(estimation_run_id);
CREATE INDEX IF NOT EXISTS idx_estimation_wbs_rollup_project ON estimation_wbs_rollup(project_id);

CREATE TABLE IF NOT EXISTS estimation_phase_rollup (
    id UUID NOT NULL,
    estimation_run_id UUID NOT NULL,
    project_id UUID NOT NULL,
    project_phase_id UUID NOT NULL,
    task_count INT NOT NULL DEFAULT 0,
    included_task_count INT NOT NULL DEFAULT 0,
    unresolved_task_count INT NOT NULL DEFAULT 0,
    total_estimate_hours DECIMAL(12,2) NOT NULL DEFAULT 0,
    total_labor_cost DECIMAL(18,4),
    total_billing_preview DECIMAL(18,4),
    currency_code VARCHAR(10),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    CONSTRAINT pk_estimation_phase_rollup PRIMARY KEY (id),
    CONSTRAINT fk_estimation_phase_rollup_run FOREIGN KEY (estimation_run_id)
        REFERENCES estimation_run(id) ON DELETE CASCADE,
    CONSTRAINT uq_estimation_phase_rollup_run_phase UNIQUE (estimation_run_id, project_phase_id)
);
CREATE INDEX IF NOT EXISTS idx_estimation_phase_rollup_run ON estimation_phase_rollup(estimation_run_id);
CREATE INDEX IF NOT EXISTS idx_estimation_phase_rollup_project ON estimation_phase_rollup(project_id);

CREATE TABLE IF NOT EXISTS estimation_project_summary (
    id UUID NOT NULL,
    estimation_run_id UUID NOT NULL,
    project_id UUID NOT NULL,
    total_task_count INT NOT NULL DEFAULT 0,
    included_task_count INT NOT NULL DEFAULT 0,
    excluded_task_count INT NOT NULL DEFAULT 0,
    unestimated_task_count INT NOT NULL DEFAULT 0,
    unresolved_role_task_count INT NOT NULL DEFAULT 0,
    unresolved_rate_task_count INT NOT NULL DEFAULT 0,
    total_estimate_hours DECIMAL(12,2) NOT NULL DEFAULT 0,
    total_labor_cost DECIMAL(18,4),
    total_billing_preview DECIMAL(18,4),
    average_cost_rate DECIMAL(18,4),
    average_billing_rate DECIMAL(18,4),
    currency_code VARCHAR(10),
    warning_count INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    CONSTRAINT pk_estimation_project_summary PRIMARY KEY (id),
    CONSTRAINT fk_estimation_project_summary_run FOREIGN KEY (estimation_run_id)
        REFERENCES estimation_run(id) ON DELETE CASCADE,
    CONSTRAINT uq_estimation_project_summary_run UNIQUE (estimation_run_id)
);
CREATE INDEX IF NOT EXISTS idx_estimation_project_summary_project ON estimation_project_summary(project_id);
