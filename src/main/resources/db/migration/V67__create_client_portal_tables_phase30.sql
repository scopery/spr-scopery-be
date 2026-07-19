-- Phase 30: Customer / external collaboration portal
CREATE TABLE IF NOT EXISTS external_portal_account (
    id UUID NOT NULL, workspace_id UUID NOT NULL, contact_id UUID, email VARCHAR(320) NOT NULL,
    display_name VARCHAR(255), status VARCHAR(50) NOT NULL, password_hash VARCHAR(255),
    last_login_at TIMESTAMPTZ, activated_at TIMESTAMPTZ,
    version INT NOT NULL DEFAULT 0, created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255), updated_by VARCHAR(255),
    CONSTRAINT pk_external_portal_account PRIMARY KEY (id),
    CONSTRAINT uq_external_portal_account_email UNIQUE (workspace_id, email),
    CONSTRAINT ck_external_portal_account_status CHECK (status IN ('INVITED','ACTIVE','SUSPENDED','ARCHIVED'))
);
CREATE TABLE IF NOT EXISTS external_portal_invite (
    id UUID NOT NULL, workspace_id UUID NOT NULL, project_id UUID, email VARCHAR(320) NOT NULL,
    invite_token_hash VARCHAR(255) NOT NULL, status VARCHAR(50) NOT NULL, expires_at TIMESTAMPTZ NOT NULL,
    accepted_at TIMESTAMPTZ, invited_by UUID, portal_account_id UUID,
    version INT NOT NULL DEFAULT 0, created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), created_by VARCHAR(255),
    CONSTRAINT pk_external_portal_invite PRIMARY KEY (id),
    CONSTRAINT ck_external_portal_invite_status CHECK (status IN ('PENDING','ACCEPTED','EXPIRED','REVOKED'))
);
CREATE TABLE IF NOT EXISTS external_project_access_grant (
    id UUID NOT NULL, project_id UUID NOT NULL, workspace_id UUID NOT NULL, portal_account_id UUID NOT NULL,
    status VARCHAR(50) NOT NULL, permission_policy_code VARCHAR(100), expires_at TIMESTAMPTZ,
    version INT NOT NULL DEFAULT 0, created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255), updated_by VARCHAR(255),
    CONSTRAINT pk_external_project_access_grant PRIMARY KEY (id),
    CONSTRAINT uq_external_project_access_grant UNIQUE (project_id, portal_account_id),
    CONSTRAINT ck_external_project_access_grant_status CHECK (status IN ('ACTIVE','REVOKED','EXPIRED'))
);
CREATE TABLE IF NOT EXISTS client_review_request (
    id UUID NOT NULL, project_id UUID NOT NULL, workspace_id UUID NOT NULL, target_type VARCHAR(100) NOT NULL,
    target_id UUID NOT NULL, title VARCHAR(255) NOT NULL, status VARCHAR(50) NOT NULL, due_at TIMESTAMPTZ,
    requested_by UUID, assigned_portal_account_id UUID,
    version INT NOT NULL DEFAULT 0, created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255), updated_by VARCHAR(255),
    CONSTRAINT pk_client_review_request PRIMARY KEY (id),
    CONSTRAINT ck_client_review_request_status CHECK (status IN ('OPEN','IN_REVIEW','APPROVED','REJECTED','CHANGES_REQUESTED','CANCELLED'))
);
CREATE TABLE IF NOT EXISTS client_review_decision (
    id UUID NOT NULL, review_request_id UUID NOT NULL, project_id UUID NOT NULL, decision VARCHAR(50) NOT NULL,
    comment TEXT, decided_by_portal_account_id UUID, decided_at TIMESTAMPTZ NOT NULL,
    version INT NOT NULL DEFAULT 0, created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), created_by VARCHAR(255),
    CONSTRAINT pk_client_review_decision PRIMARY KEY (id),
    CONSTRAINT fk_client_review_decision_request FOREIGN KEY (review_request_id) REFERENCES client_review_request(id)
);
CREATE TABLE IF NOT EXISTS client_comment (
    id UUID NOT NULL, project_id UUID NOT NULL, target_type VARCHAR(100) NOT NULL, target_id UUID NOT NULL,
    body TEXT NOT NULL, author_portal_account_id UUID, status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
    version INT NOT NULL DEFAULT 0, created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), created_by VARCHAR(255),
    CONSTRAINT pk_client_comment PRIMARY KEY (id)
);
CREATE TABLE IF NOT EXISTS client_feedback (
    id UUID NOT NULL, project_id UUID NOT NULL, workspace_id UUID NOT NULL, category VARCHAR(50) NOT NULL,
    title VARCHAR(255) NOT NULL, body TEXT, status VARCHAR(50) NOT NULL, submitted_by_portal_account_id UUID,
    version INT NOT NULL DEFAULT 0, created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255), updated_by VARCHAR(255),
    CONSTRAINT pk_client_feedback PRIMARY KEY (id),
    CONSTRAINT ck_client_feedback_status CHECK (status IN ('SUBMITTED','TRIAGED','ACCEPTED','REJECTED','CLOSED'))
);
CREATE TABLE IF NOT EXISTS client_uat_assignment (
    id UUID NOT NULL, project_id UUID NOT NULL, test_case_id UUID, test_run_id UUID, portal_account_id UUID NOT NULL,
    status VARCHAR(50) NOT NULL, notes TEXT,
    version INT NOT NULL DEFAULT 0, created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255), updated_by VARCHAR(255),
    CONSTRAINT pk_client_uat_assignment PRIMARY KEY (id)
);
CREATE TABLE IF NOT EXISTS external_portal_audit_log (
    id UUID NOT NULL, workspace_id UUID NOT NULL, project_id UUID, portal_account_id UUID,
    action VARCHAR(100) NOT NULL, target_type VARCHAR(100), target_id UUID, details TEXT,
    occurred_at TIMESTAMPTZ NOT NULL, ip_address VARCHAR(100),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_external_portal_audit_log PRIMARY KEY (id)
);
