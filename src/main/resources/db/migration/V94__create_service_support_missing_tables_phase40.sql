-- Phase 40: Service / Support / Maintenance — missing tables
CREATE TABLE IF NOT EXISTS support_request_type (
    id UUID NOT NULL, workspace_id UUID NOT NULL, type_code VARCHAR(150) NOT NULL, name VARCHAR(255) NOT NULL,
    description TEXT, default_priority VARCHAR(50), portal_visible BOOLEAN NOT NULL DEFAULT false,
    enabled BOOLEAN NOT NULL DEFAULT true, status VARCHAR(50) NOT NULL, version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255), updated_by VARCHAR(255),
    CONSTRAINT pk_support_request_type PRIMARY KEY (id),
    CONSTRAINT uq_support_request_type UNIQUE (workspace_id, type_code)
);
CREATE TABLE IF NOT EXISTS support_status_history (
    id UUID NOT NULL, workspace_id UUID NOT NULL, support_case_id UUID NOT NULL,
    from_status VARCHAR(50), to_status VARCHAR(50) NOT NULL, reason TEXT,
    changed_at TIMESTAMPTZ NOT NULL, changed_by UUID, visibility VARCHAR(50) NOT NULL,
    version INT NOT NULL DEFAULT 0, created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_support_status_history PRIMARY KEY (id)
);
CREATE TABLE IF NOT EXISTS support_sla_target (
    id UUID NOT NULL, workspace_id UUID NOT NULL, sla_policy_id UUID NOT NULL,
    request_type_id UUID, priority VARCHAR(50), target_type VARCHAR(50) NOT NULL,
    duration_minutes INT NOT NULL, enabled BOOLEAN NOT NULL DEFAULT true, version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255), updated_by VARCHAR(255),
    CONSTRAINT pk_support_sla_target PRIMARY KEY (id)
);
CREATE TABLE IF NOT EXISTS support_incident_record (
    id UUID NOT NULL, workspace_id UUID NOT NULL, service_profile_id UUID, project_id UUID,
    incident_number VARCHAR(150), title VARCHAR(500) NOT NULL, description TEXT,
    severity VARCHAR(50) NOT NULL, status VARCHAR(50) NOT NULL, impact_summary TEXT,
    client_visible_summary TEXT, owner_user_id UUID, detected_at TIMESTAMPTZ,
    acknowledged_at TIMESTAMPTZ, resolved_at TIMESTAMPTZ, closed_at TIMESTAMPTZ, version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255), updated_by VARCHAR(255),
    CONSTRAINT pk_support_incident_record PRIMARY KEY (id)
);
CREATE TABLE IF NOT EXISTS support_incident_timeline_entry (
    id UUID NOT NULL, workspace_id UUID NOT NULL, incident_id UUID NOT NULL,
    entry_type VARCHAR(100) NOT NULL, visibility VARCHAR(50) NOT NULL,
    message TEXT NOT NULL, occurred_at TIMESTAMPTZ NOT NULL, version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255),
    CONSTRAINT pk_support_incident_timeline PRIMARY KEY (id)
);
CREATE TABLE IF NOT EXISTS support_problem_record (
    id UUID NOT NULL, workspace_id UUID NOT NULL, project_id UUID, service_profile_id UUID,
    problem_number VARCHAR(150), title VARCHAR(500) NOT NULL, description TEXT,
    status VARCHAR(50) NOT NULL, root_cause_summary TEXT, workaround TEXT,
    owner_user_id UUID, resolved_at TIMESTAMPTZ, resolved_by UUID, closed_at TIMESTAMPTZ, closed_by UUID,
    version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255), updated_by VARCHAR(255),
    CONSTRAINT pk_support_problem_record PRIMARY KEY (id)
);
CREATE TABLE IF NOT EXISTS support_maintenance_activity (
    id UUID NOT NULL, workspace_id UUID NOT NULL, maintenance_window_id UUID,
    maintenance_plan_id UUID, service_profile_id UUID, project_id UUID,
    activity_type VARCHAR(100) NOT NULL, title VARCHAR(255) NOT NULL, description TEXT,
    outcome_summary TEXT, effort_hours NUMERIC(19,4), client_visible BOOLEAN NOT NULL DEFAULT false,
    performed_at TIMESTAMPTZ, performed_by UUID, version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255), updated_by VARCHAR(255),
    CONSTRAINT pk_support_maintenance_activity PRIMARY KEY (id)
);
CREATE TABLE IF NOT EXISTS service_handover_package (
    id UUID NOT NULL, workspace_id UUID NOT NULL, project_id UUID NOT NULL,
    service_profile_id UUID, package_code VARCHAR(150), title VARCHAR(255) NOT NULL,
    summary TEXT, status VARCHAR(50) NOT NULL, client_visible BOOLEAN NOT NULL DEFAULT false,
    finalized_at TIMESTAMPTZ, finalized_by UUID, version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255), updated_by VARCHAR(255),
    CONSTRAINT pk_service_handover_package PRIMARY KEY (id)
);
CREATE TABLE IF NOT EXISTS service_handover_package_item (
    id UUID NOT NULL, workspace_id UUID NOT NULL, handover_package_id UUID NOT NULL,
    item_type VARCHAR(100) NOT NULL, target_object_type VARCHAR(100), target_object_id UUID,
    document_id UUID, title VARCHAR(255) NOT NULL, description TEXT,
    client_visible BOOLEAN NOT NULL DEFAULT false, sort_order INT NOT NULL DEFAULT 0,
    version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255),
    CONSTRAINT pk_service_handover_item PRIMARY KEY (id)
);
CREATE TABLE IF NOT EXISTS support_work_link (
    id UUID NOT NULL, workspace_id UUID NOT NULL, support_case_id UUID NOT NULL,
    target_object_type VARCHAR(100) NOT NULL, target_object_id UUID NOT NULL,
    link_type VARCHAR(100) NOT NULL, version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255),
    CONSTRAINT pk_support_work_link PRIMARY KEY (id)
);
CREATE TABLE IF NOT EXISTS support_service_cost_input (
    id UUID NOT NULL, workspace_id UUID NOT NULL, project_id UUID, service_profile_id UUID,
    support_case_id UUID, incident_id UUID, maintenance_activity_id UUID,
    resource_profile_id UUID, source_type VARCHAR(100) NOT NULL, source_id UUID,
    effort_hours NUMERIC(19,4), rate_amount NUMERIC(19,4), currency VARCHAR(10),
    cost_amount NUMERIC(19,4) NOT NULL, status VARCHAR(50) NOT NULL, version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255), updated_by VARCHAR(255),
    CONSTRAINT pk_support_service_cost_input PRIMARY KEY (id)
);
CREATE TABLE IF NOT EXISTS support_metric_snapshot (
    id UUID NOT NULL, workspace_id UUID NOT NULL, project_id UUID,
    service_profile_id UUID, period_start DATE, period_end DATE,
    open_cases INT NOT NULL DEFAULT 0, new_cases INT NOT NULL DEFAULT 0,
    resolved_cases INT NOT NULL DEFAULT 0, closed_cases INT NOT NULL DEFAULT 0,
    sla_at_risk INT NOT NULL DEFAULT 0, sla_breached INT NOT NULL DEFAULT 0,
    critical_incidents INT NOT NULL DEFAULT 0, avg_first_response_minutes NUMERIC(19,4),
    avg_resolution_minutes NUMERIC(19,4), maintenance_windows_planned INT NOT NULL DEFAULT 0,
    support_effort_hours NUMERIC(19,4) NOT NULL DEFAULT 0,
    support_cost_input NUMERIC(19,4), currency VARCHAR(10),
    snapshot_source VARCHAR(100) NOT NULL, snapshot_at TIMESTAMPTZ NOT NULL,
    version INT NOT NULL DEFAULT 0, created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_support_metric_snapshot PRIMARY KEY (id)
);
CREATE TABLE IF NOT EXISTS support_escalation_rule (
    id UUID NOT NULL, workspace_id UUID NOT NULL, service_profile_id UUID, queue_id UUID,
    rule_code VARCHAR(150) NOT NULL, name VARCHAR(255) NOT NULL, trigger_type VARCHAR(100) NOT NULL,
    condition_json JSONB, target_user_ids_json JSONB, target_team_ids_json JSONB,
    notification_template_code VARCHAR(150), enabled BOOLEAN NOT NULL DEFAULT true,
    status VARCHAR(50) NOT NULL, version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255), updated_by VARCHAR(255),
    CONSTRAINT pk_support_escalation_rule PRIMARY KEY (id),
    CONSTRAINT uq_support_escalation_rule UNIQUE (workspace_id, rule_code)
);
CREATE TABLE IF NOT EXISTS support_knowledge_link (
    id UUID NOT NULL, workspace_id UUID NOT NULL, support_case_id UUID,
    problem_id UUID, incident_id UUID, document_id UUID, document_version_id UUID,
    link_type VARCHAR(100) NOT NULL, client_visible BOOLEAN NOT NULL DEFAULT false,
    version INT NOT NULL DEFAULT 0, created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255),
    CONSTRAINT pk_support_knowledge_link PRIMARY KEY (id)
);
