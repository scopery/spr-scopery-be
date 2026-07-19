-- Phase 17: Project planned finance / budget / margin foundation
ALTER TABLE project_project ADD COLUMN IF NOT EXISTS current_finance_scenario_id UUID;

CREATE TABLE IF NOT EXISTS project_finance_scenario (
    id UUID NOT NULL,
    project_id UUID NOT NULL,
    workspace_id UUID NOT NULL,
    estimation_run_id UUID NOT NULL,
    code VARCHAR(100),
    name VARCHAR(255) NOT NULL,
    description TEXT,
    scenario_version INT NOT NULL DEFAULT 1,
    status VARCHAR(50) NOT NULL,
    currency_code VARCHAR(10) NOT NULL,
    planned_revenue DECIMAL(18,4) NOT NULL DEFAULT 0,
    revenue_split_method VARCHAR(50) NOT NULL,
    contingency_method VARCHAR(50),
    contingency_percent DECIMAL(8,4),
    contingency_fixed_amount DECIMAL(18,4),
    overhead_method VARCHAR(50),
    overhead_percent DECIMAL(8,4),
    overhead_fixed_amount DECIMAL(18,4),
    target_margin_percent DECIMAL(8,4),
    assumptions_json JSONB,
    formula_version VARCHAR(50) NOT NULL,
    current_flag BOOLEAN NOT NULL DEFAULT FALSE,
    approved_at TIMESTAMPTZ,
    approved_by UUID,
    archived_at TIMESTAMPTZ,
    archived_by UUID,
    actor_user_id UUID,
    trace_id VARCHAR(100),
    version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    CONSTRAINT pk_project_finance_scenario PRIMARY KEY (id),
    CONSTRAINT fk_project_finance_scenario_project FOREIGN KEY (project_id)
        REFERENCES project_project(id),
    CONSTRAINT fk_project_finance_scenario_estimation FOREIGN KEY (estimation_run_id)
        REFERENCES estimation_run(id),
    CONSTRAINT ck_project_finance_scenario_status CHECK (status IN ('DRAFT','APPROVED','ARCHIVED')),
    CONSTRAINT ck_project_finance_scenario_revenue_split CHECK (revenue_split_method IN (
        'MANUAL_AMOUNT','MANUAL_PERCENT','COST_PROPORTION')),
    CONSTRAINT ck_project_finance_scenario_contingency CHECK (contingency_method IS NULL OR contingency_method IN (
        'FIXED_AMOUNT','PERCENT_OF_LABOR_COST','PERCENT_OF_DIRECT_COST')),
    CONSTRAINT ck_project_finance_scenario_overhead CHECK (overhead_method IS NULL OR overhead_method IN (
        'FIXED_AMOUNT','PERCENT_OF_LABOR_COST','PERCENT_OF_DIRECT_COST','PERCENT_OF_REVENUE',
        'FTE_MONTH','PER_PHASE_FIXED'))
);
CREATE INDEX IF NOT EXISTS idx_project_finance_scenario_project ON project_finance_scenario(project_id);
CREATE INDEX IF NOT EXISTS idx_project_finance_scenario_workspace ON project_finance_scenario(workspace_id);
CREATE INDEX IF NOT EXISTS idx_project_finance_scenario_status ON project_finance_scenario(status);
CREATE INDEX IF NOT EXISTS idx_project_finance_scenario_estimation ON project_finance_scenario(estimation_run_id);
CREATE UNIQUE INDEX IF NOT EXISTS uq_project_finance_scenario_current
    ON project_finance_scenario(project_id) WHERE current_flag = TRUE;

CREATE TABLE IF NOT EXISTS project_phase_finance (
    id UUID NOT NULL,
    finance_scenario_id UUID NOT NULL,
    project_id UUID NOT NULL,
    project_phase_id UUID NOT NULL,
    phase_name_snapshot VARCHAR(255) NOT NULL,
    phase_order INT,
    currency_code VARCHAR(10) NOT NULL,
    estimate_hours DECIMAL(12,2) NOT NULL DEFAULT 0,
    labor_cost DECIMAL(18,4) NOT NULL DEFAULT 0,
    custom_cost DECIMAL(18,4) NOT NULL DEFAULT 0,
    vendor_cost DECIMAL(18,4) NOT NULL DEFAULT 0,
    contingency_amount DECIMAL(18,4) NOT NULL DEFAULT 0,
    direct_cost DECIMAL(18,4) NOT NULL DEFAULT 0,
    overhead_amount DECIMAL(18,4) NOT NULL DEFAULT 0,
    budget_of_costs DECIMAL(18,4) NOT NULL DEFAULT 0,
    planned_revenue DECIMAL(18,4) NOT NULL DEFAULT 0,
    revenue_percent DECIMAL(8,4),
    gross_margin DECIMAL(18,4),
    gross_margin_percent DECIMAL(8,4),
    profit_before_tax DECIMAL(18,4),
    pbt_percent DECIMAL(8,4),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    CONSTRAINT pk_project_phase_finance PRIMARY KEY (id),
    CONSTRAINT fk_project_phase_finance_scenario FOREIGN KEY (finance_scenario_id)
        REFERENCES project_finance_scenario(id) ON DELETE CASCADE,
    CONSTRAINT uq_project_phase_finance_scenario_phase UNIQUE (finance_scenario_id, project_phase_id)
);
CREATE INDEX IF NOT EXISTS idx_project_phase_finance_scenario ON project_phase_finance(finance_scenario_id);
CREATE INDEX IF NOT EXISTS idx_project_phase_finance_project ON project_phase_finance(project_id);

CREATE TABLE IF NOT EXISTS project_custom_cost (
    id UUID NOT NULL,
    finance_scenario_id UUID NOT NULL,
    project_id UUID NOT NULL,
    project_phase_id UUID,
    category VARCHAR(100) NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    amount DECIMAL(18,4) NOT NULL,
    currency_code VARCHAR(10) NOT NULL,
    cost_date DATE,
    status VARCHAR(50) NOT NULL,
    archived_at TIMESTAMPTZ,
    archived_by UUID,
    version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    CONSTRAINT pk_project_custom_cost PRIMARY KEY (id),
    CONSTRAINT fk_project_custom_cost_scenario FOREIGN KEY (finance_scenario_id)
        REFERENCES project_finance_scenario(id) ON DELETE CASCADE,
    CONSTRAINT ck_project_custom_cost_status CHECK (status IN ('ACTIVE','INACTIVE','ARCHIVED')),
    CONSTRAINT ck_project_custom_cost_category CHECK (category IN (
        'TRAVEL','MEAL','ACCOMMODATION','SOFTWARE_LICENSE','HARDWARE','CLOUD',
        'TRAINING','WORKSHOP','TRANSLATION','SUBCONTRACTOR','OTHER')),
    CONSTRAINT ck_project_custom_cost_amount CHECK (amount >= 0)
);
CREATE INDEX IF NOT EXISTS idx_project_custom_cost_scenario ON project_custom_cost(finance_scenario_id);
CREATE INDEX IF NOT EXISTS idx_project_custom_cost_project ON project_custom_cost(project_id);
CREATE INDEX IF NOT EXISTS idx_project_custom_cost_status ON project_custom_cost(status);

CREATE TABLE IF NOT EXISTS project_vendor_cost (
    id UUID NOT NULL,
    finance_scenario_id UUID NOT NULL,
    project_id UUID NOT NULL,
    project_phase_id UUID,
    vendor_name VARCHAR(255),
    external_party_id UUID,
    description TEXT NOT NULL,
    amount DECIMAL(18,4) NOT NULL,
    currency_code VARCHAR(10) NOT NULL,
    status VARCHAR(50) NOT NULL,
    archived_at TIMESTAMPTZ,
    archived_by UUID,
    version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    CONSTRAINT pk_project_vendor_cost PRIMARY KEY (id),
    CONSTRAINT fk_project_vendor_cost_scenario FOREIGN KEY (finance_scenario_id)
        REFERENCES project_finance_scenario(id) ON DELETE CASCADE,
    CONSTRAINT ck_project_vendor_cost_status CHECK (status IN ('ACTIVE','INACTIVE','ARCHIVED')),
    CONSTRAINT ck_project_vendor_cost_amount CHECK (amount >= 0)
);
CREATE INDEX IF NOT EXISTS idx_project_vendor_cost_scenario ON project_vendor_cost(finance_scenario_id);
CREATE INDEX IF NOT EXISTS idx_project_vendor_cost_project ON project_vendor_cost(project_id);
CREATE INDEX IF NOT EXISTS idx_project_vendor_cost_status ON project_vendor_cost(status);

CREATE TABLE IF NOT EXISTS project_finance_summary (
    id UUID NOT NULL,
    finance_scenario_id UUID NOT NULL,
    project_id UUID NOT NULL,
    currency_code VARCHAR(10) NOT NULL,
    total_estimate_hours DECIMAL(12,2) NOT NULL DEFAULT 0,
    total_labor_cost DECIMAL(18,4) NOT NULL DEFAULT 0,
    total_custom_cost DECIMAL(18,4) NOT NULL DEFAULT 0,
    total_vendor_cost DECIMAL(18,4) NOT NULL DEFAULT 0,
    total_contingency DECIMAL(18,4) NOT NULL DEFAULT 0,
    total_direct_cost DECIMAL(18,4) NOT NULL DEFAULT 0,
    total_overhead DECIMAL(18,4) NOT NULL DEFAULT 0,
    budget_of_costs DECIMAL(18,4) NOT NULL DEFAULT 0,
    planned_revenue DECIMAL(18,4) NOT NULL DEFAULT 0,
    gross_margin DECIMAL(18,4),
    gross_margin_percent DECIMAL(8,4),
    profit_before_tax DECIMAL(18,4),
    pbt_percent DECIMAL(8,4),
    average_cost_rate DECIMAL(18,4),
    formula_version VARCHAR(50) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    CONSTRAINT pk_project_finance_summary PRIMARY KEY (id),
    CONSTRAINT fk_project_finance_summary_scenario FOREIGN KEY (finance_scenario_id)
        REFERENCES project_finance_scenario(id) ON DELETE CASCADE,
    CONSTRAINT uq_project_finance_summary_scenario UNIQUE (finance_scenario_id)
);
CREATE INDEX IF NOT EXISTS idx_project_finance_summary_project ON project_finance_summary(project_id);
