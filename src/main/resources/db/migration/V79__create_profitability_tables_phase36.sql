-- Phase 36: Revenue & Profitability Visibility
CREATE TABLE IF NOT EXISTS profit_project_profile (
    id UUID NOT NULL, workspace_id UUID NOT NULL, project_id UUID NOT NULL, currency VARCHAR(10) NOT NULL,
    tracking_mode VARCHAR(50), revenue_mode VARCHAR(50), cost_mode VARCHAR(50), owner_user_id UUID,
    portal_visibility VARCHAR(50), status VARCHAR(50) NOT NULL, version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), created_by VARCHAR(255), updated_by VARCHAR(255),
    CONSTRAINT pk_profit_project_profile PRIMARY KEY (id), CONSTRAINT uq_profit_project_profile UNIQUE (workspace_id, project_id)
);
CREATE TABLE IF NOT EXISTS profit_revenue_source (
    id UUID NOT NULL, workspace_id UUID NOT NULL, project_id UUID NOT NULL, profile_id UUID NOT NULL,
    source_type VARCHAR(50) NOT NULL, source_id UUID, amount NUMERIC(19,4) NOT NULL, currency VARCHAR(10) NOT NULL,
    included_in_forecast BOOLEAN NOT NULL DEFAULT TRUE, confidence VARCHAR(50), status VARCHAR(50) NOT NULL,
    version INT NOT NULL DEFAULT 0, created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255), updated_by VARCHAR(255),
    CONSTRAINT pk_profit_revenue_source PRIMARY KEY (id),
    CONSTRAINT fk_profit_revenue_profile FOREIGN KEY (profile_id) REFERENCES profit_project_profile(id)
);
CREATE TABLE IF NOT EXISTS profit_cost_source (
    id UUID NOT NULL, workspace_id UUID NOT NULL, project_id UUID NOT NULL, profile_id UUID NOT NULL,
    source_type VARCHAR(50) NOT NULL, source_id UUID, effort_hours NUMERIC(19,4), rate_amount NUMERIC(19,4),
    amount NUMERIC(19,4) NOT NULL, currency VARCHAR(10) NOT NULL, included_in_forecast BOOLEAN NOT NULL DEFAULT TRUE,
    status VARCHAR(50) NOT NULL, version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), created_by VARCHAR(255), updated_by VARCHAR(255),
    CONSTRAINT pk_profit_cost_source PRIMARY KEY (id),
    CONSTRAINT fk_profit_cost_profile FOREIGN KEY (profile_id) REFERENCES profit_project_profile(id)
);
CREATE TABLE IF NOT EXISTS profit_project_summary (
    id UUID NOT NULL, workspace_id UUID NOT NULL, project_id UUID NOT NULL, currency VARCHAR(10) NOT NULL,
    baseline_revenue NUMERIC(19,4), forecast_revenue NUMERIC(19,4), baseline_cost NUMERIC(19,4), forecast_cost NUMERIC(19,4),
    forecast_profit NUMERIC(19,4), forecast_margin_percent NUMERIC(9,4), profitability_status VARCHAR(50),
    last_snapshot_at TIMESTAMPTZ, version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), created_by VARCHAR(255), updated_by VARCHAR(255),
    CONSTRAINT pk_profit_project_summary PRIMARY KEY (id),
    CONSTRAINT uq_profit_project_summary UNIQUE (workspace_id, project_id, currency)
);
CREATE TABLE IF NOT EXISTS profit_threshold_policy (
    id UUID NOT NULL, workspace_id UUID NOT NULL, project_id UUID,
    healthy_margin_percent NUMERIC(9,4), watch_margin_percent NUMERIC(9,4), at_risk_margin_percent NUMERIC(9,4),
    loss_risk_margin_percent NUMERIC(9,4), version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), created_by VARCHAR(255), updated_by VARCHAR(255),
    CONSTRAINT pk_profit_threshold_policy PRIMARY KEY (id)
);
CREATE TABLE IF NOT EXISTS profit_adjustment (
    id UUID NOT NULL, workspace_id UUID NOT NULL, project_id UUID NOT NULL, profile_id UUID NOT NULL,
    adjustment_type VARCHAR(50) NOT NULL, amount NUMERIC(19,4) NOT NULL, reason TEXT NOT NULL, status VARCHAR(50) NOT NULL,
    source_link_type VARCHAR(50), source_link_id UUID, version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), created_by VARCHAR(255), updated_by VARCHAR(255),
    CONSTRAINT pk_profit_adjustment PRIMARY KEY (id),
    CONSTRAINT ck_profit_adjustment_status CHECK (status IN ('DRAFT','APPLIED','CANCELLED'))
);
CREATE TABLE IF NOT EXISTS profit_risk_flag (
    id UUID NOT NULL, workspace_id UUID NOT NULL, project_id UUID NOT NULL, reason VARCHAR(500) NOT NULL,
    impact_type VARCHAR(50), amount_at_risk NUMERIC(19,4), status VARCHAR(50) NOT NULL, version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), created_by VARCHAR(255), updated_by VARCHAR(255),
    CONSTRAINT pk_profit_risk_flag PRIMARY KEY (id),
    CONSTRAINT ck_profit_risk_status CHECK (status IN ('OPEN','MITIGATED','CLOSED'))
);
CREATE TABLE IF NOT EXISTS profit_snapshot (
    id UUID NOT NULL, workspace_id UUID NOT NULL, project_id UUID NOT NULL, profile_id UUID NOT NULL,
    baseline_revenue NUMERIC(19,4), forecast_revenue NUMERIC(19,4), baseline_cost NUMERIC(19,4), forecast_cost NUMERIC(19,4),
    forecast_profit NUMERIC(19,4), forecast_margin_percent NUMERIC(9,4), profitability_status VARCHAR(50),
    version INT NOT NULL DEFAULT 0, created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255), updated_by VARCHAR(255),
    CONSTRAINT pk_profit_snapshot PRIMARY KEY (id)
);
