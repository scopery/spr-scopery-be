-- Phase 40: Service / Support / Maintenance foundation
CREATE TABLE IF NOT EXISTS support_service_profile (
    id UUID NOT NULL, workspace_id UUID NOT NULL, scope_type VARCHAR(50) NOT NULL, project_id UUID, client_organization_id UUID,
    support_owner_user_id UUID, default_sla_policy_id UUID, default_queue_id UUID, portal_intake_enabled BOOLEAN NOT NULL DEFAULT FALSE,
    service_start_date DATE, service_end_date DATE, warranty_start_date DATE, warranty_end_date DATE, status VARCHAR(50) NOT NULL,
    version INT NOT NULL DEFAULT 0, created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255), updated_by VARCHAR(255),
    CONSTRAINT pk_support_service_profile PRIMARY KEY (id)
);
CREATE TABLE IF NOT EXISTS support_request_type (
    id UUID NOT NULL, workspace_id UUID NOT NULL, type_code VARCHAR(100) NOT NULL, name VARCHAR(255) NOT NULL,
    default_priority VARCHAR(50), portal_visible BOOLEAN NOT NULL DEFAULT TRUE, status VARCHAR(50) NOT NULL,
    version INT NOT NULL DEFAULT 0, created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255), updated_by VARCHAR(255),
    CONSTRAINT pk_support_request_type PRIMARY KEY (id), CONSTRAINT uq_support_request_type UNIQUE (workspace_id, type_code)
);
CREATE TABLE IF NOT EXISTS support_queue (
    id UUID NOT NULL, workspace_id UUID NOT NULL, queue_code VARCHAR(100) NOT NULL, name VARCHAR(255) NOT NULL,
    owner_user_id UUID, default_sla_policy_id UUID, status VARCHAR(50) NOT NULL, version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), created_by VARCHAR(255), updated_by VARCHAR(255),
    CONSTRAINT pk_support_queue PRIMARY KEY (id), CONSTRAINT uq_support_queue UNIQUE (workspace_id, queue_code)
);
CREATE TABLE IF NOT EXISTS support_case (
    id UUID NOT NULL, workspace_id UUID NOT NULL, service_profile_id UUID, project_id UUID, case_number VARCHAR(50) NOT NULL,
    request_type_code VARCHAR(100) NOT NULL, source VARCHAR(50) NOT NULL, priority VARCHAR(50) NOT NULL, status VARCHAR(50) NOT NULL,
    title VARCHAR(500) NOT NULL, description TEXT, reporter_user_id UUID, reporter_external_contact_id UUID,
    owner_user_id UUID, queue_id UUID, portal_visible BOOLEAN NOT NULL DEFAULT TRUE, sla_policy_id UUID,
    version INT NOT NULL DEFAULT 0, created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255), updated_by VARCHAR(255),
    CONSTRAINT pk_support_case PRIMARY KEY (id), CONSTRAINT uq_support_case_number UNIQUE (workspace_id, case_number),
    CONSTRAINT ck_support_case_status CHECK (status IN ('NEW','TRIAGED','IN_PROGRESS','WAITING_FOR_CLIENT','WAITING_INTERNAL','WAITING_VENDOR','RESOLVED','CLOSED','CANCELLED','ARCHIVED'))
);
CREATE TABLE IF NOT EXISTS support_status_history (
    id UUID NOT NULL, workspace_id UUID NOT NULL, support_case_id UUID NOT NULL, from_status VARCHAR(50), to_status VARCHAR(50) NOT NULL,
    reason TEXT, actor_user_id UUID, occurred_at TIMESTAMPTZ NOT NULL, version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), created_by VARCHAR(255), updated_by VARCHAR(255),
    CONSTRAINT pk_support_status_history PRIMARY KEY (id)
);
CREATE TABLE IF NOT EXISTS support_assignment (
    id UUID NOT NULL, workspace_id UUID NOT NULL, support_case_id UUID NOT NULL, assignment_type VARCHAR(50) NOT NULL,
    assignee_user_id UUID, resource_profile_id UUID, status VARCHAR(50) NOT NULL, version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), created_by VARCHAR(255), updated_by VARCHAR(255),
    CONSTRAINT pk_support_assignment PRIMARY KEY (id)
);
CREATE TABLE IF NOT EXISTS support_comment (
    id UUID NOT NULL, workspace_id UUID NOT NULL, support_case_id UUID NOT NULL, visibility VARCHAR(50) NOT NULL,
    body TEXT NOT NULL, author_user_id UUID, author_portal_account_id UUID, version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), created_by VARCHAR(255), updated_by VARCHAR(255),
    CONSTRAINT pk_support_comment PRIMARY KEY (id)
);
CREATE TABLE IF NOT EXISTS support_sla_policy (
    id UUID NOT NULL, workspace_id UUID NOT NULL, policy_code VARCHAR(150) NOT NULL, name VARCHAR(255) NOT NULL,
    first_response_minutes INT, resolve_minutes INT, business_hours_only BOOLEAN NOT NULL DEFAULT TRUE, status VARCHAR(50) NOT NULL,
    version INT NOT NULL DEFAULT 0, created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255), updated_by VARCHAR(255),
    CONSTRAINT pk_support_sla_policy PRIMARY KEY (id), CONSTRAINT uq_support_sla_policy UNIQUE (workspace_id, policy_code)
);
CREATE TABLE IF NOT EXISTS support_sla_clock (
    id UUID NOT NULL, workspace_id UUID NOT NULL, support_case_id UUID NOT NULL, sla_policy_id UUID NOT NULL,
    clock_type VARCHAR(50) NOT NULL, started_at TIMESTAMPTZ NOT NULL, due_at TIMESTAMPTZ, paused_at TIMESTAMPTZ,
    completed_at TIMESTAMPTZ, status VARCHAR(50) NOT NULL, version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), created_by VARCHAR(255), updated_by VARCHAR(255),
    CONSTRAINT pk_support_sla_clock PRIMARY KEY (id)
);
CREATE TABLE IF NOT EXISTS support_sla_breach (
    id UUID NOT NULL, workspace_id UUID NOT NULL, support_case_id UUID NOT NULL, sla_clock_id UUID NOT NULL,
    breach_type VARCHAR(50) NOT NULL, breached_at TIMESTAMPTZ NOT NULL, status VARCHAR(50) NOT NULL, version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), created_by VARCHAR(255), updated_by VARCHAR(255),
    CONSTRAINT pk_support_sla_breach PRIMARY KEY (id)
);
CREATE TABLE IF NOT EXISTS support_incident_record (
    id UUID NOT NULL, workspace_id UUID NOT NULL, support_case_id UUID, project_id UUID, severity VARCHAR(50) NOT NULL,
    title VARCHAR(500) NOT NULL, status VARCHAR(50) NOT NULL, version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), created_by VARCHAR(255), updated_by VARCHAR(255),
    CONSTRAINT pk_support_incident PRIMARY KEY (id)
);
CREATE TABLE IF NOT EXISTS support_maintenance_plan (
    id UUID NOT NULL, workspace_id UUID NOT NULL, project_id UUID, name VARCHAR(255) NOT NULL, status VARCHAR(50) NOT NULL,
    planned_start TIMESTAMPTZ, planned_end TIMESTAMPTZ, version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), created_by VARCHAR(255), updated_by VARCHAR(255),
    CONSTRAINT pk_support_maintenance_plan PRIMARY KEY (id)
);
CREATE TABLE IF NOT EXISTS support_maintenance_window (
    id UUID NOT NULL, workspace_id UUID NOT NULL, maintenance_plan_id UUID NOT NULL, window_start TIMESTAMPTZ NOT NULL,
    window_end TIMESTAMPTZ NOT NULL, status VARCHAR(50) NOT NULL, version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), created_by VARCHAR(255), updated_by VARCHAR(255),
    CONSTRAINT pk_support_maintenance_window PRIMARY KEY (id)
);
CREATE TABLE IF NOT EXISTS support_maintenance_activity (
    id UUID NOT NULL, workspace_id UUID NOT NULL, maintenance_plan_id UUID NOT NULL, title VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL, owner_user_id UUID, version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), created_by VARCHAR(255), updated_by VARCHAR(255),
    CONSTRAINT pk_support_maintenance_activity PRIMARY KEY (id)
);
CREATE TABLE IF NOT EXISTS support_warranty_coverage (
    id UUID NOT NULL, workspace_id UUID NOT NULL, project_id UUID, service_profile_id UUID, start_date DATE NOT NULL, end_date DATE,
    status VARCHAR(50) NOT NULL, version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), created_by VARCHAR(255), updated_by VARCHAR(255),
    CONSTRAINT pk_support_warranty PRIMARY KEY (id)
);
CREATE TABLE IF NOT EXISTS support_effort_record (
    id UUID NOT NULL, workspace_id UUID NOT NULL, support_case_id UUID NOT NULL, resource_profile_id UUID, effort_hours NUMERIC(19,4) NOT NULL,
    effort_date DATE NOT NULL, status VARCHAR(50) NOT NULL, version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), created_by VARCHAR(255), updated_by VARCHAR(255),
    CONSTRAINT pk_support_effort PRIMARY KEY (id)
);
