-- Phase 19: Project Baseline / Change Request / Change Order foundation
ALTER TABLE project_project ADD COLUMN IF NOT EXISTS current_baseline_id UUID;

CREATE TABLE IF NOT EXISTS project_baseline (
    id UUID NOT NULL,
    project_id UUID NOT NULL,
    workspace_id UUID NOT NULL,
    baseline_number INT NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    status VARCHAR(50) NOT NULL,
    current_flag BOOLEAN NOT NULL DEFAULT FALSE,
    source_schedule_run_id UUID,
    source_estimation_run_id UUID,
    source_finance_scenario_id UUID,
    source_quote_version_id UUID,
    snapshot_json JSONB NOT NULL,
    summary_json JSONB NOT NULL,
    validation_json JSONB,
    formula_version VARCHAR(50) NOT NULL,
    approved_at TIMESTAMPTZ,
    approved_by UUID,
    archived_at TIMESTAMPTZ,
    archived_by UUID,
    trace_id VARCHAR(100),
    version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    CONSTRAINT pk_project_baseline PRIMARY KEY (id),
    CONSTRAINT fk_project_baseline_project FOREIGN KEY (project_id)
        REFERENCES project_project(id),
    CONSTRAINT uq_project_baseline_project_number UNIQUE (project_id, baseline_number),
    CONSTRAINT ck_project_baseline_status CHECK (status IN ('DRAFT','READY','APPROVED','ARCHIVED'))
);
CREATE INDEX IF NOT EXISTS idx_project_baseline_project ON project_baseline(project_id);
CREATE INDEX IF NOT EXISTS idx_project_baseline_workspace ON project_baseline(workspace_id);
CREATE INDEX IF NOT EXISTS idx_project_baseline_status ON project_baseline(status);
CREATE UNIQUE INDEX IF NOT EXISTS uq_project_baseline_current
    ON project_baseline(project_id) WHERE current_flag = TRUE;

CREATE TABLE IF NOT EXISTS change_request (
    id UUID NOT NULL,
    project_id UUID NOT NULL,
    workspace_id UUID NOT NULL,
    baseline_id UUID,
    code VARCHAR(100) NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    change_type VARCHAR(50) NOT NULL,
    priority VARCHAR(50),
    status VARCHAR(50) NOT NULL,
    reason TEXT,
    requested_by UUID,
    requested_at TIMESTAMPTZ,
    submitted_at TIMESTAMPTZ,
    submitted_by UUID,
    approved_at TIMESTAMPTZ,
    approved_by UUID,
    rejected_at TIMESTAMPTZ,
    rejected_by UUID,
    rejection_reason TEXT,
    cancelled_at TIMESTAMPTZ,
    cancelled_by UUID,
    applied_at TIMESTAMPTZ,
    applied_by UUID,
    archived_at TIMESTAMPTZ,
    archived_by UUID,
    trace_id VARCHAR(100),
    version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    CONSTRAINT pk_change_request PRIMARY KEY (id),
    CONSTRAINT fk_change_request_project FOREIGN KEY (project_id)
        REFERENCES project_project(id),
    CONSTRAINT fk_change_request_baseline FOREIGN KEY (baseline_id)
        REFERENCES project_baseline(id),
    CONSTRAINT uq_change_request_project_code UNIQUE (project_id, code),
    CONSTRAINT ck_change_request_status CHECK (status IN (
        'DRAFT','SUBMITTED','APPROVED','REJECTED','CANCELLED','APPLIED','ARCHIVED')),
    CONSTRAINT ck_change_request_type CHECK (change_type IN (
        'SCOPE_CHANGE','SCHEDULE_CHANGE','COST_CHANGE','REVENUE_CHANGE',
        'QUOTE_CHANGE','RESOURCE_CHANGE','RISK_RESPONSE','OTHER'))
);
CREATE INDEX IF NOT EXISTS idx_change_request_project ON change_request(project_id);
CREATE INDEX IF NOT EXISTS idx_change_request_workspace ON change_request(workspace_id);
CREATE INDEX IF NOT EXISTS idx_change_request_status ON change_request(status);
CREATE INDEX IF NOT EXISTS idx_change_request_baseline ON change_request(baseline_id);

CREATE TABLE IF NOT EXISTS change_request_item (
    id UUID NOT NULL,
    change_request_id UUID NOT NULL,
    project_id UUID NOT NULL,
    target_type VARCHAR(50) NOT NULL,
    target_id UUID,
    operation VARCHAR(50) NOT NULL,
    summary VARCHAR(500) NOT NULL,
    before_snapshot_json JSONB,
    after_snapshot_json JSONB,
    apply_payload_json JSONB,
    status VARCHAR(50) NOT NULL,
    version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    CONSTRAINT pk_change_request_item PRIMARY KEY (id),
    CONSTRAINT fk_change_request_item_cr FOREIGN KEY (change_request_id)
        REFERENCES change_request(id) ON DELETE CASCADE,
    CONSTRAINT ck_change_request_item_status CHECK (status IN (
        'DRAFT','READY','APPLIED','FAILED','SKIPPED')),
    CONSTRAINT ck_change_request_item_operation CHECK (operation IN (
        'CREATE','UPDATE','DELETE','ARCHIVE','MOVE','RECALCULATE','REPLACE_REFERENCE')),
    CONSTRAINT ck_change_request_item_target CHECK (target_type IN (
        'PROJECT','PROJECT_PHASE','WBS_NODE','TASK','TASK_DEPENDENCY','MILESTONE',
        'SCHEDULE','ESTIMATE','FINANCE_SCENARIO','QUOTE_VERSION','CUSTOM_COST','VENDOR_COST','OTHER'))
);
CREATE INDEX IF NOT EXISTS idx_change_request_item_cr ON change_request_item(change_request_id);
CREATE INDEX IF NOT EXISTS idx_change_request_item_project ON change_request_item(project_id);

CREATE TABLE IF NOT EXISTS change_impact (
    id UUID NOT NULL,
    change_request_id UUID NOT NULL,
    project_id UUID NOT NULL,
    currency_code VARCHAR(10),
    scope_impact TEXT,
    schedule_impact_days INT,
    estimate_hours_impact DECIMAL(12,2),
    labor_cost_impact DECIMAL(18,4),
    direct_cost_impact DECIMAL(18,4),
    overhead_impact DECIMAL(18,4),
    revenue_impact DECIMAL(18,4),
    gross_margin_impact DECIMAL(18,4),
    pbt_impact DECIMAL(18,4),
    quote_amount_impact DECIMAL(18,4),
    risk_impact VARCHAR(50),
    impact_summary_json JSONB,
    version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    CONSTRAINT pk_change_impact PRIMARY KEY (id),
    CONSTRAINT fk_change_impact_cr FOREIGN KEY (change_request_id)
        REFERENCES change_request(id) ON DELETE CASCADE,
    CONSTRAINT uq_change_impact_cr UNIQUE (change_request_id)
);
CREATE INDEX IF NOT EXISTS idx_change_impact_project ON change_impact(project_id);

CREATE TABLE IF NOT EXISTS change_approval_action (
    id UUID NOT NULL,
    change_request_id UUID NOT NULL,
    action VARCHAR(50) NOT NULL,
    actor_user_id UUID NOT NULL,
    reason TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    trace_id VARCHAR(100),
    CONSTRAINT pk_change_approval_action PRIMARY KEY (id),
    CONSTRAINT fk_change_approval_action_cr FOREIGN KEY (change_request_id)
        REFERENCES change_request(id) ON DELETE CASCADE,
    CONSTRAINT ck_change_approval_action CHECK (action IN (
        'SUBMIT','APPROVE','REJECT','CANCEL','APPLY','REOPEN'))
);
CREATE INDEX IF NOT EXISTS idx_change_approval_action_cr ON change_approval_action(change_request_id);

CREATE TABLE IF NOT EXISTS change_order (
    id UUID NOT NULL,
    change_request_id UUID NOT NULL,
    project_id UUID NOT NULL,
    workspace_id UUID NOT NULL,
    code VARCHAR(100) NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    status VARCHAR(50) NOT NULL,
    commercial_impact_json JSONB,
    source_quote_version_id UUID,
    future_contract_id UUID,
    approved_at TIMESTAMPTZ,
    approved_by UUID,
    rejected_at TIMESTAMPTZ,
    rejected_by UUID,
    rejection_reason TEXT,
    archived_at TIMESTAMPTZ,
    archived_by UUID,
    version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    CONSTRAINT pk_change_order PRIMARY KEY (id),
    CONSTRAINT fk_change_order_cr FOREIGN KEY (change_request_id)
        REFERENCES change_request(id),
    CONSTRAINT fk_change_order_project FOREIGN KEY (project_id)
        REFERENCES project_project(id),
    CONSTRAINT uq_change_order_project_code UNIQUE (project_id, code),
    CONSTRAINT ck_change_order_status CHECK (status IN (
        'DRAFT','SUBMITTED','APPROVED','REJECTED','ARCHIVED'))
);
CREATE INDEX IF NOT EXISTS idx_change_order_cr ON change_order(change_request_id);
CREATE INDEX IF NOT EXISTS idx_change_order_project ON change_order(project_id);
CREATE INDEX IF NOT EXISTS idx_change_order_workspace ON change_order(workspace_id);
