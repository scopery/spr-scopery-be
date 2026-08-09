-- V230: Create profitability module tables (project_profitability_* / project_profit_rate_card / workspace_profit_rate_card)
-- These are the new-named tables used by the current profitability JPA entities.
-- The old profit_* tables (profit_adjustment, profit_cost_forecast, etc.) already exist from earlier migrations and are NOT touched here.

-- ============================================================
-- project_profitability_profile
-- Entity: ProfitabilityProfileJpaEntity / ProjectProfitabilityProfileJpaEntity
-- ============================================================
CREATE TABLE IF NOT EXISTS project_profitability_profile (
    id                  UUID        NOT NULL,
    workspace_id        UUID        NOT NULL,
    project_id          UUID        NOT NULL,
    currency            VARCHAR(10) NOT NULL,
    status              VARCHAR(50) NOT NULL,
    tracking_mode       VARCHAR(50),
    revenue_mode        VARCHAR(50),
    cost_mode           VARCHAR(50),
    owner_user_id       UUID,
    portal_visibility   VARCHAR(50),
    version             INTEGER,
    created_at          TIMESTAMPTZ NOT NULL,
    updated_at          TIMESTAMPTZ NOT NULL,
    created_by          VARCHAR(255),
    updated_by          VARCHAR(255),
    CONSTRAINT pk_project_profitability_profile PRIMARY KEY (id)
);

CREATE INDEX IF NOT EXISTS idx_project_profitability_profile_project_id
    ON project_profitability_profile (project_id);

CREATE INDEX IF NOT EXISTS idx_project_profitability_profile_workspace_id
    ON project_profitability_profile (workspace_id);

CREATE INDEX IF NOT EXISTS idx_project_profitability_profile_status
    ON project_profitability_profile (status);

-- ============================================================
-- project_profitability_summary
-- Entity: ProfitabilitySummaryJpaEntity / ProjectProfitabilitySummaryJpaEntity
-- ============================================================
CREATE TABLE IF NOT EXISTS project_profitability_summary (
    id                      UUID            NOT NULL,
    workspace_id            UUID,
    project_id              UUID            NOT NULL,
    currency                VARCHAR(10)     NOT NULL,
    total_revenue           NUMERIC(20, 4),
    total_cost              NUMERIC(20, 4),
    gross_margin            NUMERIC(20, 4),
    gross_margin_percent    NUMERIC(20, 4),
    profit_before_tax       NUMERIC(20, 4),
    pbt_percent             NUMERIC(20, 4),
    health_status           VARCHAR(50),
    baseline_revenue        NUMERIC(19, 4),
    forecast_revenue        NUMERIC(19, 4),
    baseline_cost           NUMERIC(19, 4),
    forecast_cost           NUMERIC(19, 4),
    forecast_profit         NUMERIC(19, 4),
    forecast_margin_percent NUMERIC(19, 4),
    profitability_status    VARCHAR(50),
    last_snapshot_at        TIMESTAMPTZ,
    version                 INTEGER,
    created_at              TIMESTAMPTZ     NOT NULL,
    updated_at              TIMESTAMPTZ     NOT NULL,
    created_by              VARCHAR(255),
    updated_by              VARCHAR(255),
    CONSTRAINT pk_project_profitability_summary PRIMARY KEY (id)
);

CREATE INDEX IF NOT EXISTS idx_project_profitability_summary_project_id
    ON project_profitability_summary (project_id);

CREATE INDEX IF NOT EXISTS idx_project_profitability_summary_workspace_id
    ON project_profitability_summary (workspace_id);

-- ============================================================
-- project_profitability_cost_source
-- Entity: ProfitCostSourceJpaEntity
-- ============================================================
CREATE TABLE IF NOT EXISTS project_profitability_cost_source (
    id                    UUID            NOT NULL,
    workspace_id          UUID            NOT NULL,
    project_id            UUID            NOT NULL,
    profile_id            UUID            NOT NULL,
    source_type           VARCHAR(50)     NOT NULL,
    source_id             UUID,
    effort_hours          NUMERIC(19, 4),
    rate_amount           NUMERIC(19, 4),
    amount                NUMERIC(19, 4)  NOT NULL,
    currency              VARCHAR(50)     NOT NULL,
    included_in_forecast  BOOLEAN         NOT NULL,
    status                VARCHAR(50)     NOT NULL,
    version               INTEGER,
    created_at            TIMESTAMPTZ     NOT NULL,
    updated_at            TIMESTAMPTZ     NOT NULL,
    created_by            VARCHAR(255),
    updated_by            VARCHAR(255),
    CONSTRAINT pk_project_profitability_cost_source PRIMARY KEY (id)
);

CREATE INDEX IF NOT EXISTS idx_project_profitability_cost_source_project_id
    ON project_profitability_cost_source (project_id);

CREATE INDEX IF NOT EXISTS idx_project_profitability_cost_source_workspace_id
    ON project_profitability_cost_source (workspace_id);

CREATE INDEX IF NOT EXISTS idx_project_profitability_cost_source_profile_id
    ON project_profitability_cost_source (profile_id);

CREATE INDEX IF NOT EXISTS idx_project_profitability_cost_source_status
    ON project_profitability_cost_source (status);

-- ============================================================
-- project_profitability_revenue_source
-- Entity: ProfitRevenueSourceJpaEntity
-- ============================================================
CREATE TABLE IF NOT EXISTS project_profitability_revenue_source (
    id                    UUID            NOT NULL,
    workspace_id          UUID            NOT NULL,
    project_id            UUID            NOT NULL,
    profile_id            UUID            NOT NULL,
    source_type           VARCHAR(50)     NOT NULL,
    source_id             UUID,
    amount                NUMERIC(19, 4)  NOT NULL,
    currency              VARCHAR(50)     NOT NULL,
    included_in_forecast  BOOLEAN         NOT NULL,
    confidence            VARCHAR(50),
    status                VARCHAR(50)     NOT NULL,
    version               INTEGER,
    created_at            TIMESTAMPTZ     NOT NULL,
    updated_at            TIMESTAMPTZ     NOT NULL,
    created_by            VARCHAR(255),
    updated_by            VARCHAR(255),
    CONSTRAINT pk_project_profitability_revenue_source PRIMARY KEY (id)
);

CREATE INDEX IF NOT EXISTS idx_project_profitability_revenue_source_project_id
    ON project_profitability_revenue_source (project_id);

CREATE INDEX IF NOT EXISTS idx_project_profitability_revenue_source_workspace_id
    ON project_profitability_revenue_source (workspace_id);

CREATE INDEX IF NOT EXISTS idx_project_profitability_revenue_source_profile_id
    ON project_profitability_revenue_source (profile_id);

CREATE INDEX IF NOT EXISTS idx_project_profitability_revenue_source_status
    ON project_profitability_revenue_source (status);

-- ============================================================
-- project_profitability_cost_forecast
-- Entity: ProfitCostForecastJpaEntity
-- ============================================================
CREATE TABLE IF NOT EXISTS project_profitability_cost_forecast (
    id                          UUID            NOT NULL,
    workspace_id                UUID            NOT NULL,
    project_id                  UUID            NOT NULL,
    profitability_profile_id    UUID            NOT NULL,
    forecast_type               VARCHAR(50)     NOT NULL,
    currency                    VARCHAR(10)     NOT NULL,
    forecast_amount             NUMERIC(19, 4)  NOT NULL,
    confidence_percent          NUMERIC(5, 2),
    forecast_date               DATE            NOT NULL,
    assumption_notes            TEXT,
    generated_by                VARCHAR(50)     NOT NULL,
    status                      VARCHAR(50)     NOT NULL,
    version                     INTEGER,
    created_at                  TIMESTAMPTZ     NOT NULL,
    updated_at                  TIMESTAMPTZ     NOT NULL,
    created_by                  VARCHAR(255),
    updated_by                  VARCHAR(255),
    CONSTRAINT pk_project_profitability_cost_forecast PRIMARY KEY (id)
);

CREATE INDEX IF NOT EXISTS idx_project_profitability_cost_forecast_project_id
    ON project_profitability_cost_forecast (project_id);

CREATE INDEX IF NOT EXISTS idx_project_profitability_cost_forecast_workspace_id
    ON project_profitability_cost_forecast (workspace_id);

CREATE INDEX IF NOT EXISTS idx_project_profitability_cost_forecast_profile_id
    ON project_profitability_cost_forecast (profitability_profile_id);

CREATE INDEX IF NOT EXISTS idx_project_profitability_cost_forecast_status
    ON project_profitability_cost_forecast (status);

-- ============================================================
-- project_profitability_revenue_forecast
-- Entity: ProfitRevenueForecastJpaEntity
-- ============================================================
CREATE TABLE IF NOT EXISTS project_profitability_revenue_forecast (
    id                          UUID            NOT NULL,
    workspace_id                UUID            NOT NULL,
    project_id                  UUID            NOT NULL,
    profitability_profile_id    UUID            NOT NULL,
    forecast_type               VARCHAR(50)     NOT NULL,
    currency                    VARCHAR(10)     NOT NULL,
    forecast_amount             NUMERIC(19, 4)  NOT NULL,
    confidence_percent          NUMERIC(5, 2),
    forecast_date               DATE            NOT NULL,
    assumption_notes            TEXT,
    generated_by                VARCHAR(50)     NOT NULL,
    status                      VARCHAR(50)     NOT NULL,
    version                     INTEGER,
    created_at                  TIMESTAMPTZ     NOT NULL,
    updated_at                  TIMESTAMPTZ     NOT NULL,
    created_by                  VARCHAR(255),
    updated_by                  VARCHAR(255),
    CONSTRAINT pk_project_profitability_revenue_forecast PRIMARY KEY (id)
);

CREATE INDEX IF NOT EXISTS idx_project_profitability_revenue_forecast_project_id
    ON project_profitability_revenue_forecast (project_id);

CREATE INDEX IF NOT EXISTS idx_project_profitability_revenue_forecast_workspace_id
    ON project_profitability_revenue_forecast (workspace_id);

CREATE INDEX IF NOT EXISTS idx_project_profitability_revenue_forecast_profile_id
    ON project_profitability_revenue_forecast (profitability_profile_id);

CREATE INDEX IF NOT EXISTS idx_project_profitability_revenue_forecast_status
    ON project_profitability_revenue_forecast (status);

-- ============================================================
-- project_profitability_plan
-- Entity: ProfitabilityPlanJpaEntity
-- ============================================================
CREATE TABLE IF NOT EXISTS project_profitability_plan (
    id                          UUID            NOT NULL,
    workspace_id                UUID            NOT NULL,
    project_id                  UUID            NOT NULL,
    profitability_profile_id    UUID            NOT NULL,
    plan_code                   VARCHAR(150),
    name                        VARCHAR(255)    NOT NULL,
    plan_type                   VARCHAR(50)     NOT NULL,
    status                      VARCHAR(50)     NOT NULL,
    current_version_id          UUID,
    version                     INTEGER,
    created_at                  TIMESTAMPTZ     NOT NULL,
    updated_at                  TIMESTAMPTZ     NOT NULL,
    created_by                  VARCHAR(255),
    updated_by                  VARCHAR(255),
    CONSTRAINT pk_project_profitability_plan PRIMARY KEY (id)
);

CREATE INDEX IF NOT EXISTS idx_project_profitability_plan_project_id
    ON project_profitability_plan (project_id);

CREATE INDEX IF NOT EXISTS idx_project_profitability_plan_workspace_id
    ON project_profitability_plan (workspace_id);

CREATE INDEX IF NOT EXISTS idx_project_profitability_plan_profile_id
    ON project_profitability_plan (profitability_profile_id);

CREATE INDEX IF NOT EXISTS idx_project_profitability_plan_status
    ON project_profitability_plan (status);

-- ============================================================
-- project_profitability_plan_version
-- Entity: ProfitabilityPlanVersionJpaEntity
-- ============================================================
CREATE TABLE IF NOT EXISTS project_profitability_plan_version (
    id                          UUID            NOT NULL,
    workspace_id                UUID            NOT NULL,
    project_id                  UUID            NOT NULL,
    profitability_plan_id       UUID            NOT NULL,
    version_number              INTEGER         NOT NULL,
    version_label               VARCHAR(100),
    currency                    VARCHAR(10)     NOT NULL,
    baseline_revenue            NUMERIC(19, 4)  NOT NULL,
    baseline_cost               NUMERIC(19, 4)  NOT NULL,
    baseline_profit             NUMERIC(19, 4)  NOT NULL,
    baseline_margin_percent     NUMERIC(9, 4),
    planned_revenue             NUMERIC(19, 4)  NOT NULL,
    planned_cost                NUMERIC(19, 4)  NOT NULL,
    planned_profit              NUMERIC(19, 4)  NOT NULL,
    planned_margin_percent      NUMERIC(9, 4),
    assumption_notes            TEXT,
    source_quote_version_id     UUID,
    source_baseline_id          UUID,
    finalized_flag              BOOLEAN         NOT NULL,
    finalized_at                TIMESTAMPTZ,
    finalized_by                UUID,
    status                      VARCHAR(50)     NOT NULL,
    version                     INTEGER,
    created_at                  TIMESTAMPTZ     NOT NULL,
    updated_at                  TIMESTAMPTZ     NOT NULL,
    created_by                  VARCHAR(255),
    updated_by                  VARCHAR(255),
    CONSTRAINT pk_project_profitability_plan_version PRIMARY KEY (id)
);

CREATE INDEX IF NOT EXISTS idx_project_profitability_plan_version_project_id
    ON project_profitability_plan_version (project_id);

CREATE INDEX IF NOT EXISTS idx_project_profitability_plan_version_workspace_id
    ON project_profitability_plan_version (workspace_id);

CREATE INDEX IF NOT EXISTS idx_project_profitability_plan_version_plan_id
    ON project_profitability_plan_version (profitability_plan_id);

CREATE INDEX IF NOT EXISTS idx_project_profitability_plan_version_status
    ON project_profitability_plan_version (status);

-- ============================================================
-- project_profitability_adjustment
-- Entity: ProfitAdjustmentJpaEntity
-- ============================================================
CREATE TABLE IF NOT EXISTS project_profitability_adjustment (
    id                UUID            NOT NULL,
    workspace_id      UUID            NOT NULL,
    project_id        UUID            NOT NULL,
    profile_id        UUID            NOT NULL,
    adjustment_type   VARCHAR(50)     NOT NULL,
    amount            NUMERIC(19, 4)  NOT NULL,
    reason            VARCHAR(255)    NOT NULL,
    status            VARCHAR(50)     NOT NULL,
    source_link_type  VARCHAR(50),
    source_link_id    UUID,
    version           INTEGER,
    created_at        TIMESTAMPTZ     NOT NULL,
    updated_at        TIMESTAMPTZ     NOT NULL,
    created_by        VARCHAR(255),
    updated_by        VARCHAR(255),
    CONSTRAINT pk_project_profitability_adjustment PRIMARY KEY (id)
);

CREATE INDEX IF NOT EXISTS idx_project_profitability_adjustment_project_id
    ON project_profitability_adjustment (project_id);

CREATE INDEX IF NOT EXISTS idx_project_profitability_adjustment_workspace_id
    ON project_profitability_adjustment (workspace_id);

CREATE INDEX IF NOT EXISTS idx_project_profitability_adjustment_profile_id
    ON project_profitability_adjustment (profile_id);

CREATE INDEX IF NOT EXISTS idx_project_profitability_adjustment_status
    ON project_profitability_adjustment (status);

-- ============================================================
-- project_profitability_variance
-- Entity: ProfitVarianceJpaEntity
-- ============================================================
CREATE TABLE IF NOT EXISTS project_profitability_variance (
    id                          UUID            NOT NULL,
    workspace_id                UUID            NOT NULL,
    project_id                  UUID            NOT NULL,
    profitability_profile_id    UUID            NOT NULL,
    variance_type               VARCHAR(100)    NOT NULL,
    from_amount                 NUMERIC(19, 4)  NOT NULL,
    to_amount                   NUMERIC(19, 4)  NOT NULL,
    variance_amount             NUMERIC(19, 4)  NOT NULL,
    variance_percent            NUMERIC(9, 4),
    currency                    VARCHAR(10)     NOT NULL,
    explanation                 TEXT,
    source_snapshot_id          UUID,
    version                     INTEGER,
    created_at                  TIMESTAMPTZ     NOT NULL,
    updated_at                  TIMESTAMPTZ     NOT NULL,
    created_by                  VARCHAR(255),
    updated_by                  VARCHAR(255),
    CONSTRAINT pk_project_profitability_variance PRIMARY KEY (id)
);

CREATE INDEX IF NOT EXISTS idx_project_profitability_variance_project_id
    ON project_profitability_variance (project_id);

CREATE INDEX IF NOT EXISTS idx_project_profitability_variance_workspace_id
    ON project_profitability_variance (workspace_id);

CREATE INDEX IF NOT EXISTS idx_project_profitability_variance_profile_id
    ON project_profitability_variance (profitability_profile_id);

-- ============================================================
-- project_profitability_risk_flag
-- Entity: ProfitRiskFlagJpaEntity
-- ============================================================
CREATE TABLE IF NOT EXISTS project_profitability_risk_flag (
    id              UUID            NOT NULL,
    workspace_id    UUID            NOT NULL,
    project_id      UUID            NOT NULL,
    reason          VARCHAR(500)    NOT NULL,
    impact_type     VARCHAR(50)     NOT NULL,
    amount_at_risk  NUMERIC(19, 4),
    status          VARCHAR(50)     NOT NULL,
    version         INTEGER,
    created_at      TIMESTAMPTZ     NOT NULL,
    updated_at      TIMESTAMPTZ     NOT NULL,
    created_by      VARCHAR(255),
    updated_by      VARCHAR(255),
    CONSTRAINT pk_project_profitability_risk_flag PRIMARY KEY (id)
);

CREATE INDEX IF NOT EXISTS idx_project_profitability_risk_flag_project_id
    ON project_profitability_risk_flag (project_id);

CREATE INDEX IF NOT EXISTS idx_project_profitability_risk_flag_workspace_id
    ON project_profitability_risk_flag (workspace_id);

CREATE INDEX IF NOT EXISTS idx_project_profitability_risk_flag_status
    ON project_profitability_risk_flag (status);

-- ============================================================
-- project_profitability_threshold_policy
-- Entity: thresholdpolicy/ProfitThresholdPolicyJpaEntity (ProfitabilityTableNames.THRESHOLD_POLICY)
-- ============================================================
CREATE TABLE IF NOT EXISTS project_profitability_threshold_policy (
    id                      UUID            NOT NULL,
    project_id              UUID            NOT NULL,
    healthy_margin_percent  NUMERIC(20, 4)  NOT NULL,
    watch_margin_percent    NUMERIC(20, 4)  NOT NULL,
    at_risk_margin_percent  NUMERIC(20, 4)  NOT NULL,
    loss_risk_margin_percent NUMERIC(20, 4) NOT NULL,
    version                 INTEGER,
    created_at              TIMESTAMPTZ     NOT NULL,
    updated_at              TIMESTAMPTZ     NOT NULL,
    created_by              VARCHAR(255),
    updated_by              VARCHAR(255),
    CONSTRAINT pk_project_profitability_threshold_policy PRIMARY KEY (id)
);

CREATE INDEX IF NOT EXISTS idx_project_profitability_threshold_policy_project_id
    ON project_profitability_threshold_policy (project_id);

-- ============================================================
-- project_profitability_threshold
-- Entity: threshold/ProfitThresholdPolicyJpaEntity (ProfitabilityTableNames.THRESHOLD)
-- ============================================================
CREATE TABLE IF NOT EXISTS project_profitability_threshold (
    id                       UUID            NOT NULL,
    workspace_id             UUID            NOT NULL,
    project_id               UUID,
    healthy_margin_percent   NUMERIC(19, 4),
    watch_margin_percent     NUMERIC(19, 4),
    at_risk_margin_percent   NUMERIC(19, 4),
    loss_risk_margin_percent NUMERIC(19, 4),
    version                  INTEGER,
    created_at               TIMESTAMPTZ     NOT NULL,
    updated_at               TIMESTAMPTZ     NOT NULL,
    created_by               VARCHAR(255),
    updated_by               VARCHAR(255),
    CONSTRAINT pk_project_profitability_threshold PRIMARY KEY (id)
);

CREATE INDEX IF NOT EXISTS idx_project_profitability_threshold_workspace_id
    ON project_profitability_threshold (workspace_id);

CREATE INDEX IF NOT EXISTS idx_project_profitability_threshold_project_id
    ON project_profitability_threshold (project_id);

-- ============================================================
-- project_profit_rate_card
-- Entity: ProfitRateCardJpaEntity (ProfitabilityTableNames.RATE_CARD / PROJECT_RATE_CARD)
-- ============================================================
CREATE TABLE IF NOT EXISTS project_profit_rate_card (
    id              UUID            NOT NULL,
    workspace_id    UUID            NOT NULL,
    project_id      UUID,
    rate_code       VARCHAR(150)    NOT NULL,
    name            VARCHAR(255)    NOT NULL,
    rate_type       VARCHAR(50)     NOT NULL,
    role_name       VARCHAR(255),
    team_id         UUID,
    currency        VARCHAR(10)     NOT NULL,
    amount_per_hour NUMERIC(19, 4),
    amount_per_day  NUMERIC(19, 4),
    status          VARCHAR(50)     NOT NULL,
    version         INTEGER,
    created_at      TIMESTAMPTZ     NOT NULL,
    updated_at      TIMESTAMPTZ     NOT NULL,
    created_by      VARCHAR(255),
    updated_by      VARCHAR(255),
    CONSTRAINT pk_project_profit_rate_card PRIMARY KEY (id)
);

CREATE INDEX IF NOT EXISTS idx_project_profit_rate_card_workspace_id
    ON project_profit_rate_card (workspace_id);

CREATE INDEX IF NOT EXISTS idx_project_profit_rate_card_project_id
    ON project_profit_rate_card (project_id);

CREATE INDEX IF NOT EXISTS idx_project_profit_rate_card_status
    ON project_profit_rate_card (status);

-- ============================================================
-- workspace_profit_rate_card
-- Defined in ProfitabilityTableNames.WORKSPACE_RATE_CARD
-- Workspace-scoped rate card (no JPA entity yet; mirrors project_profit_rate_card without project_id)
-- ============================================================
CREATE TABLE IF NOT EXISTS workspace_profit_rate_card (
    id              UUID            NOT NULL,
    workspace_id    UUID            NOT NULL,
    rate_code       VARCHAR(150)    NOT NULL,
    name            VARCHAR(255)    NOT NULL,
    rate_type       VARCHAR(50)     NOT NULL,
    role_name       VARCHAR(255),
    team_id         UUID,
    currency        VARCHAR(10)     NOT NULL,
    amount_per_hour NUMERIC(19, 4),
    amount_per_day  NUMERIC(19, 4),
    status          VARCHAR(50)     NOT NULL,
    version         INTEGER,
    created_at      TIMESTAMPTZ     NOT NULL,
    updated_at      TIMESTAMPTZ     NOT NULL,
    created_by      VARCHAR(255),
    updated_by      VARCHAR(255),
    CONSTRAINT pk_workspace_profit_rate_card PRIMARY KEY (id)
);

CREATE INDEX IF NOT EXISTS idx_workspace_profit_rate_card_workspace_id
    ON workspace_profit_rate_card (workspace_id);

CREATE INDEX IF NOT EXISTS idx_workspace_profit_rate_card_status
    ON workspace_profit_rate_card (status);

-- ============================================================
-- project_profitability_snapshot
-- Entity: ProfitSnapshotJpaEntity
-- ============================================================
CREATE TABLE IF NOT EXISTS project_profitability_snapshot (
    id                      UUID            NOT NULL,
    workspace_id            UUID            NOT NULL,
    project_id              UUID            NOT NULL,
    profile_id              UUID            NOT NULL,
    baseline_revenue        NUMERIC(19, 4),
    forecast_revenue        NUMERIC(19, 4),
    baseline_cost           NUMERIC(19, 4),
    forecast_cost           NUMERIC(19, 4),
    forecast_profit         NUMERIC(19, 4),
    forecast_margin_percent NUMERIC(19, 4),
    profitability_status    VARCHAR(50),
    version                 INTEGER,
    created_at              TIMESTAMPTZ     NOT NULL,
    updated_at              TIMESTAMPTZ     NOT NULL,
    created_by              VARCHAR(255),
    updated_by              VARCHAR(255),
    CONSTRAINT pk_project_profitability_snapshot PRIMARY KEY (id)
);

CREATE INDEX IF NOT EXISTS idx_project_profitability_snapshot_project_id
    ON project_profitability_snapshot (project_id);

CREATE INDEX IF NOT EXISTS idx_project_profitability_snapshot_workspace_id
    ON project_profitability_snapshot (workspace_id);

CREATE INDEX IF NOT EXISTS idx_project_profitability_snapshot_profile_id
    ON project_profitability_snapshot (profile_id);
