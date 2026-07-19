-- Phase 36 — Profitability: missing tables (rate_card, plan, plan_version, revenue_forecast, cost_forecast, variance)

CREATE TABLE IF NOT EXISTS profit_rate_card (
    id          UUID          NOT NULL,
    workspace_id UUID         NOT NULL,
    project_id  UUID          NULL,
    rate_code   VARCHAR(150)  NOT NULL,
    name        VARCHAR(255)  NOT NULL,
    rate_type   VARCHAR(50)   NOT NULL,
    role_name   VARCHAR(150)  NULL,
    team_id     UUID          NULL,
    currency    VARCHAR(10)   NOT NULL,
    amount_per_hour NUMERIC(19,4) NULL,
    amount_per_day  NUMERIC(19,4) NULL,
    status      VARCHAR(50)   NOT NULL DEFAULT 'ACTIVE',
    version     INT           NOT NULL DEFAULT 0,
    created_at  TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    created_by  VARCHAR(255),
    updated_by  VARCHAR(255),
    archived_at TIMESTAMPTZ,
    archived_by VARCHAR(255),
    CONSTRAINT pk_profit_rate_card PRIMARY KEY (id),
    CONSTRAINT uq_profit_rate_card_code UNIQUE (workspace_id, project_id, rate_code),
    CONSTRAINT ck_profit_rate_card_status CHECK (status IN ('ACTIVE','ARCHIVED'))
);
CREATE INDEX IF NOT EXISTS idx_profit_rate_card_workspace ON profit_rate_card (workspace_id);
CREATE INDEX IF NOT EXISTS idx_profit_rate_card_project  ON profit_rate_card (project_id);

CREATE TABLE IF NOT EXISTS profit_plan (
    id                      UUID          NOT NULL,
    workspace_id            UUID          NOT NULL,
    project_id              UUID          NOT NULL,
    profitability_profile_id UUID         NOT NULL,
    plan_code               VARCHAR(150)  NULL,
    name                    VARCHAR(255)  NOT NULL,
    plan_type               VARCHAR(50)   NOT NULL,
    status                  VARCHAR(50)   NOT NULL DEFAULT 'DRAFT',
    current_version_id      UUID          NULL,
    version                 INT           NOT NULL DEFAULT 0,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by  VARCHAR(255),
    updated_by  VARCHAR(255),
    archived_at TIMESTAMPTZ,
    archived_by VARCHAR(255),
    CONSTRAINT pk_profit_plan PRIMARY KEY (id),
    CONSTRAINT fk_profit_plan_profile FOREIGN KEY (profitability_profile_id) REFERENCES profit_project_profile(id),
    CONSTRAINT ck_profit_plan_status CHECK (status IN ('DRAFT','ACTIVE','SUPERSEDED','CANCELLED','ARCHIVED'))
);
CREATE INDEX IF NOT EXISTS idx_profit_plan_project ON profit_plan (workspace_id, project_id);

CREATE TABLE IF NOT EXISTS profit_plan_version (
    id                      UUID          NOT NULL,
    workspace_id            UUID          NOT NULL,
    project_id              UUID          NOT NULL,
    profitability_plan_id   UUID          NOT NULL,
    version_number          INT           NOT NULL,
    version_label           VARCHAR(100)  NULL,
    currency                VARCHAR(10)   NOT NULL,
    baseline_revenue        NUMERIC(19,4) NOT NULL DEFAULT 0,
    baseline_cost           NUMERIC(19,4) NOT NULL DEFAULT 0,
    baseline_profit         NUMERIC(19,4) NOT NULL DEFAULT 0,
    baseline_margin_percent NUMERIC(9,4)  NULL,
    planned_revenue         NUMERIC(19,4) NOT NULL DEFAULT 0,
    planned_cost            NUMERIC(19,4) NOT NULL DEFAULT 0,
    planned_profit          NUMERIC(19,4) NOT NULL DEFAULT 0,
    planned_margin_percent  NUMERIC(9,4)  NULL,
    assumption_notes        TEXT          NULL,
    source_quote_version_id UUID          NULL,
    source_baseline_id      UUID          NULL,
    finalized_flag          BOOLEAN       NOT NULL DEFAULT false,
    finalized_at            TIMESTAMPTZ   NULL,
    finalized_by            UUID          NULL,
    status                  VARCHAR(50)   NOT NULL DEFAULT 'DRAFT',
    version                 INT           NOT NULL DEFAULT 0,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by  VARCHAR(255),
    updated_by  VARCHAR(255),
    archived_at TIMESTAMPTZ,
    archived_by VARCHAR(255),
    CONSTRAINT pk_profit_plan_version PRIMARY KEY (id),
    CONSTRAINT uq_profit_plan_version_number UNIQUE (profitability_plan_id, version_number),
    CONSTRAINT fk_profit_plan_version_plan FOREIGN KEY (profitability_plan_id) REFERENCES profit_plan(id),
    CONSTRAINT ck_profit_plan_version_status CHECK (status IN ('DRAFT','FINALIZED','SUPERSEDED','CANCELLED','ARCHIVED'))
);
CREATE INDEX IF NOT EXISTS idx_profit_plan_version_plan ON profit_plan_version (profitability_plan_id);

CREATE TABLE IF NOT EXISTS profit_revenue_forecast (
    id                       UUID          NOT NULL,
    workspace_id             UUID          NOT NULL,
    project_id               UUID          NOT NULL,
    profitability_profile_id UUID          NOT NULL,
    forecast_type            VARCHAR(50)   NOT NULL,
    currency                 VARCHAR(10)   NOT NULL,
    forecast_amount          NUMERIC(19,4) NOT NULL,
    confidence_percent       NUMERIC(5,2)  NULL,
    forecast_date            DATE          NOT NULL,
    assumption_notes         TEXT          NULL,
    generated_by             VARCHAR(50)   NOT NULL DEFAULT 'MANUAL',
    status                   VARCHAR(50)   NOT NULL DEFAULT 'ACTIVE',
    version                  INT           NOT NULL DEFAULT 0,
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    created_by  VARCHAR(255),
    archived_at TIMESTAMPTZ,
    archived_by VARCHAR(255),
    CONSTRAINT pk_profit_revenue_forecast PRIMARY KEY (id),
    CONSTRAINT fk_profit_rev_forecast_profile FOREIGN KEY (profitability_profile_id) REFERENCES profit_project_profile(id),
    CONSTRAINT ck_profit_rev_forecast_status CHECK (status IN ('ACTIVE','SUPERSEDED','ARCHIVED'))
);
CREATE INDEX IF NOT EXISTS idx_profit_rev_forecast_project ON profit_revenue_forecast (workspace_id, project_id);

CREATE TABLE IF NOT EXISTS profit_cost_forecast (
    id                       UUID          NOT NULL,
    workspace_id             UUID          NOT NULL,
    project_id               UUID          NOT NULL,
    profitability_profile_id UUID          NOT NULL,
    forecast_type            VARCHAR(50)   NOT NULL,
    currency                 VARCHAR(10)   NOT NULL,
    forecast_amount          NUMERIC(19,4) NOT NULL,
    confidence_percent       NUMERIC(5,2)  NULL,
    forecast_date            DATE          NOT NULL,
    assumption_notes         TEXT          NULL,
    generated_by             VARCHAR(50)   NOT NULL DEFAULT 'MANUAL',
    status                   VARCHAR(50)   NOT NULL DEFAULT 'ACTIVE',
    version                  INT           NOT NULL DEFAULT 0,
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    created_by  VARCHAR(255),
    archived_at TIMESTAMPTZ,
    archived_by VARCHAR(255),
    CONSTRAINT pk_profit_cost_forecast PRIMARY KEY (id),
    CONSTRAINT fk_profit_cost_forecast_profile FOREIGN KEY (profitability_profile_id) REFERENCES profit_project_profile(id),
    CONSTRAINT ck_profit_cost_forecast_status CHECK (status IN ('ACTIVE','SUPERSEDED','ARCHIVED'))
);
CREATE INDEX IF NOT EXISTS idx_profit_cost_forecast_project ON profit_cost_forecast (workspace_id, project_id);

CREATE TABLE IF NOT EXISTS profit_variance (
    id                       UUID          NOT NULL,
    workspace_id             UUID          NOT NULL,
    project_id               UUID          NOT NULL,
    profitability_profile_id UUID          NOT NULL,
    variance_type            VARCHAR(100)  NOT NULL,
    from_amount              NUMERIC(19,4) NOT NULL,
    to_amount                NUMERIC(19,4) NOT NULL,
    variance_amount          NUMERIC(19,4) NOT NULL,
    variance_percent         NUMERIC(9,4)  NULL,
    currency                 VARCHAR(10)   NOT NULL,
    explanation              TEXT          NULL,
    source_snapshot_id       UUID          NULL,
    version                  INT           NOT NULL DEFAULT 0,
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    created_by  UUID          NULL,
    CONSTRAINT pk_profit_variance PRIMARY KEY (id),
    CONSTRAINT fk_profit_variance_profile FOREIGN KEY (profitability_profile_id) REFERENCES profit_project_profile(id)
);
CREATE INDEX IF NOT EXISTS idx_profit_variance_project ON profit_variance (workspace_id, project_id);
