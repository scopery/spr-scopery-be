-- Phase 37: Resource Profile / Effort / Utilization / Cost Input (extends Phase 12 capacity)
CREATE TABLE IF NOT EXISTS resource_profile (
    id UUID NOT NULL, workspace_id UUID NOT NULL, linked_user_id UUID, linked_workspace_member_id UUID,
    linked_team_id UUID, linked_external_contact_id UUID, resource_type VARCHAR(50) NOT NULL,
    display_name VARCHAR(255) NOT NULL, primary_role_id UUID, default_calendar_id UUID, default_rate_card_id UUID,
    timezone VARCHAR(100), status VARCHAR(50) NOT NULL, archived_at TIMESTAMPTZ, archived_by UUID,
    version INT NOT NULL DEFAULT 0, created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255), updated_by VARCHAR(255),
    CONSTRAINT pk_resource_profile PRIMARY KEY (id),
    CONSTRAINT ck_resource_profile_status CHECK (status IN ('ACTIVE','INACTIVE','ARCHIVED'))
);
CREATE UNIQUE INDEX IF NOT EXISTS uq_resource_profile_linked_user ON resource_profile (workspace_id, linked_user_id) WHERE linked_user_id IS NOT NULL;
CREATE INDEX IF NOT EXISTS idx_resource_profile_workspace ON resource_profile (workspace_id);

CREATE TABLE IF NOT EXISTS resource_role (
    id UUID NOT NULL, workspace_id UUID NOT NULL, role_code VARCHAR(150) NOT NULL, name VARCHAR(255) NOT NULL,
    description TEXT, default_rate_card_id UUID, status VARCHAR(50) NOT NULL, archived_at TIMESTAMPTZ, archived_by UUID,
    version INT NOT NULL DEFAULT 0, created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255), updated_by VARCHAR(255),
    CONSTRAINT pk_resource_role PRIMARY KEY (id),
    CONSTRAINT uq_resource_role_code UNIQUE (workspace_id, role_code),
    CONSTRAINT ck_resource_role_status CHECK (status IN ('ACTIVE','INACTIVE','ARCHIVED'))
);

CREATE TABLE IF NOT EXISTS resource_skill (
    id UUID NOT NULL, workspace_id UUID NOT NULL, skill_code VARCHAR(150) NOT NULL, name VARCHAR(255) NOT NULL,
    description TEXT, status VARCHAR(50) NOT NULL, archived_at TIMESTAMPTZ, archived_by UUID,
    version INT NOT NULL DEFAULT 0, created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255), updated_by VARCHAR(255),
    CONSTRAINT pk_resource_skill PRIMARY KEY (id),
    CONSTRAINT uq_resource_skill_code UNIQUE (workspace_id, skill_code)
);

CREATE TABLE IF NOT EXISTS resource_skill_assignment (
    id UUID NOT NULL, workspace_id UUID NOT NULL, resource_profile_id UUID NOT NULL, resource_skill_id UUID NOT NULL,
    proficiency_level VARCHAR(50), version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255), updated_by VARCHAR(255),
    CONSTRAINT pk_resource_skill_assignment PRIMARY KEY (id),
    CONSTRAINT uq_resource_skill_assignment UNIQUE (resource_profile_id, resource_skill_id),
    CONSTRAINT fk_rsa_profile FOREIGN KEY (resource_profile_id) REFERENCES resource_profile(id),
    CONSTRAINT fk_rsa_skill FOREIGN KEY (resource_skill_id) REFERENCES resource_skill(id)
);

CREATE TABLE IF NOT EXISTS resource_task_assignment (
    id UUID NOT NULL, workspace_id UUID NOT NULL, project_id UUID NOT NULL, task_id UUID NOT NULL,
    resource_profile_id UUID NOT NULL, assignment_type VARCHAR(50) NOT NULL, planned_effort_hours NUMERIC(19,4),
    start_date DATE, end_date DATE, status VARCHAR(50) NOT NULL, notes TEXT,
    removed_at TIMESTAMPTZ, removed_by UUID, version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255), updated_by VARCHAR(255),
    CONSTRAINT pk_resource_task_assignment PRIMARY KEY (id),
    CONSTRAINT ck_rta_status CHECK (status IN ('ACTIVE','REMOVED','COMPLETED'))
);
CREATE UNIQUE INDEX IF NOT EXISTS uq_rta_active ON resource_task_assignment (task_id, resource_profile_id, assignment_type) WHERE status = 'ACTIVE';

CREATE TABLE IF NOT EXISTS resource_effort_estimate (
    id UUID NOT NULL, workspace_id UUID NOT NULL, project_id UUID, target_type VARCHAR(100) NOT NULL, target_id UUID NOT NULL,
    resource_role_id UUID, resource_profile_id UUID, estimate_type VARCHAR(50) NOT NULL, effort_hours NUMERIC(19,4) NOT NULL,
    confidence_percent NUMERIC(5,2), reason TEXT, status VARCHAR(50) NOT NULL, archived_at TIMESTAMPTZ, archived_by UUID,
    version INT NOT NULL DEFAULT 0, created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255), updated_by VARCHAR(255),
    CONSTRAINT pk_resource_effort_estimate PRIMARY KEY (id),
    CONSTRAINT ck_ree_hours CHECK (effort_hours >= 0),
    CONSTRAINT ck_ree_status CHECK (status IN ('DRAFT','ACTIVE','SUPERSEDED','ARCHIVED'))
);

CREATE TABLE IF NOT EXISTS resource_effort_forecast (
    id UUID NOT NULL, workspace_id UUID NOT NULL, project_id UUID NOT NULL, resource_profile_id UUID, resource_role_id UUID,
    forecast_type VARCHAR(50) NOT NULL, period_start DATE, period_end DATE,
    forecast_effort_hours NUMERIC(19,4) NOT NULL, remaining_effort_hours NUMERIC(19,4), actual_observed_effort_hours NUMERIC(19,4),
    confidence_percent NUMERIC(5,2), generated_by VARCHAR(50) NOT NULL, status VARCHAR(50) NOT NULL,
    archived_at TIMESTAMPTZ, archived_by UUID, version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255), updated_by VARCHAR(255),
    CONSTRAINT pk_resource_effort_forecast PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS resource_actual_effort_record (
    id UUID NOT NULL, workspace_id UUID NOT NULL, project_id UUID NOT NULL, resource_profile_id UUID,
    target_type VARCHAR(100) NOT NULL, target_id UUID NOT NULL, effort_date DATE NOT NULL,
    effort_hours NUMERIC(19,4) NOT NULL, input_mode VARCHAR(50) NOT NULL, description TEXT, status VARCHAR(50) NOT NULL,
    cancelled_at TIMESTAMPTZ, cancelled_by UUID, cancellation_reason TEXT, version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255), updated_by VARCHAR(255),
    CONSTRAINT pk_resource_actual_effort PRIMARY KEY (id),
    CONSTRAINT ck_rae_hours CHECK (effort_hours >= 0),
    CONSTRAINT ck_rae_status CHECK (status IN ('RECORDED','UPDATED','CANCELLED'))
);

CREATE TABLE IF NOT EXISTS resource_utilization_summary (
    id UUID NOT NULL, workspace_id UUID NOT NULL, resource_profile_id UUID NOT NULL,
    period_start DATE NOT NULL, period_end DATE NOT NULL,
    available_capacity_hours NUMERIC(19,4) NOT NULL DEFAULT 0, allocated_hours NUMERIC(19,4) NOT NULL DEFAULT 0,
    assigned_effort_hours NUMERIC(19,4) NOT NULL DEFAULT 0, actual_observed_hours NUMERIC(19,4) NOT NULL DEFAULT 0,
    remaining_effort_hours NUMERIC(19,4) NOT NULL DEFAULT 0, planned_utilization_percent NUMERIC(9,4),
    actual_utilization_percent NUMERIC(9,4), overload_hours NUMERIC(19,4) NOT NULL DEFAULT 0,
    under_allocation_hours NUMERIC(19,4) NOT NULL DEFAULT 0, utilization_status VARCHAR(50) NOT NULL,
    version INT NOT NULL DEFAULT 0, created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255), updated_by VARCHAR(255),
    CONSTRAINT pk_resource_utilization_summary PRIMARY KEY (id),
    CONSTRAINT uq_resource_utilization UNIQUE (workspace_id, resource_profile_id, period_start, period_end)
);

CREATE TABLE IF NOT EXISTS resource_project_capacity_summary (
    id UUID NOT NULL, workspace_id UUID NOT NULL, project_id UUID NOT NULL, period_start DATE, period_end DATE,
    project_required_effort_hours NUMERIC(19,4) NOT NULL DEFAULT 0, project_allocated_capacity_hours NUMERIC(19,4) NOT NULL DEFAULT 0,
    capacity_gap_hours NUMERIC(19,4) NOT NULL DEFAULT 0, over_allocated_resource_count INT NOT NULL DEFAULT 0,
    under_allocated_role_count INT NOT NULL DEFAULT 0, remaining_effort_hours NUMERIC(19,4) NOT NULL DEFAULT 0,
    forecast_completion_risk VARCHAR(50), cost_forecast_input NUMERIC(19,4), currency VARCHAR(10),
    version INT NOT NULL DEFAULT 0, created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255), updated_by VARCHAR(255),
    CONSTRAINT pk_resource_project_capacity_summary PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS resource_workload_snapshot (
    id UUID NOT NULL, workspace_id UUID NOT NULL, project_id UUID, resource_profile_id UUID,
    period_start DATE, period_end DATE, total_capacity_hours NUMERIC(19,4) NOT NULL DEFAULT 0,
    total_allocated_hours NUMERIC(19,4) NOT NULL DEFAULT 0, total_estimated_effort_hours NUMERIC(19,4) NOT NULL DEFAULT 0,
    total_forecast_effort_hours NUMERIC(19,4) NOT NULL DEFAULT 0, total_actual_observed_effort_hours NUMERIC(19,4) NOT NULL DEFAULT 0,
    capacity_gap_hours NUMERIC(19,4) NOT NULL DEFAULT 0, overload_count INT NOT NULL DEFAULT 0,
    understaffed_role_count INT NOT NULL DEFAULT 0, cost_forecast_input NUMERIC(19,4), currency VARCHAR(10),
    snapshot_source VARCHAR(100) NOT NULL, snapshot_at TIMESTAMPTZ NOT NULL,
    version INT NOT NULL DEFAULT 0, created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255), updated_by VARCHAR(255),
    CONSTRAINT pk_resource_workload_snapshot PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS resource_risk_flag (
    id UUID NOT NULL, workspace_id UUID NOT NULL, project_id UUID, resource_profile_id UUID,
    risk_reason VARCHAR(100) NOT NULL, impact_type VARCHAR(50) NOT NULL, source_type VARCHAR(100), source_id UUID,
    impact_effort_hours NUMERIC(19,4), impact_cost_amount NUMERIC(19,4), currency VARCHAR(10), description TEXT,
    status VARCHAR(50) NOT NULL, mitigated_at TIMESTAMPTZ, mitigated_by UUID, closed_at TIMESTAMPTZ, closed_by UUID,
    version INT NOT NULL DEFAULT 0, created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255), updated_by VARCHAR(255),
    CONSTRAINT pk_resource_risk_flag PRIMARY KEY (id),
    CONSTRAINT ck_rrf_status CHECK (status IN ('OPEN','MITIGATED','CLOSED','ARCHIVED'))
);

CREATE TABLE IF NOT EXISTS resource_assignment_conflict (
    id UUID NOT NULL, workspace_id UUID NOT NULL, project_id UUID, resource_profile_id UUID,
    conflict_type VARCHAR(100) NOT NULL, source_type VARCHAR(100), source_id UUID, severity VARCHAR(50) NOT NULL,
    description TEXT, status VARCHAR(50) NOT NULL, detected_at TIMESTAMPTZ NOT NULL,
    acknowledged_at TIMESTAMPTZ, acknowledged_by UUID, resolved_at TIMESTAMPTZ, resolved_by UUID,
    version INT NOT NULL DEFAULT 0, created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255), updated_by VARCHAR(255),
    CONSTRAINT pk_resource_assignment_conflict PRIMARY KEY (id),
    CONSTRAINT ck_rac_status CHECK (status IN ('OPEN','ACKNOWLEDGED','RESOLVED','DISMISSED','ARCHIVED'))
);

CREATE TABLE IF NOT EXISTS resource_cost_input (
    id UUID NOT NULL, workspace_id UUID NOT NULL, project_id UUID NOT NULL, resource_profile_id UUID, resource_role_id UUID,
    source_type VARCHAR(100) NOT NULL, source_id UUID, effort_hours NUMERIC(19,4) NOT NULL,
    rate_amount NUMERIC(19,4) NOT NULL, currency VARCHAR(10) NOT NULL, cost_amount NUMERIC(19,4) NOT NULL,
    status VARCHAR(50) NOT NULL, version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255), updated_by VARCHAR(255),
    CONSTRAINT pk_resource_cost_input PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS resource_utilization_threshold_policy (
    id UUID NOT NULL, workspace_id UUID NOT NULL, project_id UUID,
    under_allocated_percent NUMERIC(9,4) NOT NULL, healthy_min_percent NUMERIC(9,4) NOT NULL,
    healthy_max_percent NUMERIC(9,4) NOT NULL, watch_max_percent NUMERIC(9,4) NOT NULL,
    overloaded_percent NUMERIC(9,4) NOT NULL, critical_overload_percent NUMERIC(9,4) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE, version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255), updated_by VARCHAR(255),
    CONSTRAINT pk_resource_utilization_threshold PRIMARY KEY (id)
);

-- Extend Phase 12 allocation with Phase 37 lifecycle columns if missing
ALTER TABLE capacity_project_resource_allocation ADD COLUMN IF NOT EXISTS allocation_type_p37 VARCHAR(50);
ALTER TABLE capacity_project_resource_allocation ADD COLUMN IF NOT EXISTS hours_per_week NUMERIC(9,4);
ALTER TABLE capacity_project_resource_allocation ADD COLUMN IF NOT EXISTS total_hours NUMERIC(19,4);
ALTER TABLE capacity_project_resource_allocation ADD COLUMN IF NOT EXISTS resource_profile_id UUID;
ALTER TABLE capacity_project_resource_allocation ADD COLUMN IF NOT EXISTS resource_role_id UUID;
