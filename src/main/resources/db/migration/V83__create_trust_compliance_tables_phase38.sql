-- Phase 38: Trust / Privacy / Retention / Compliance readiness
CREATE TABLE IF NOT EXISTS trust_data_classification_policy (
    id UUID NOT NULL, workspace_id UUID NOT NULL, policy_code VARCHAR(150) NOT NULL, name VARCHAR(255) NOT NULL,
    description TEXT, default_classification VARCHAR(50) NOT NULL, classification_rules_json JSONB,
    enabled BOOLEAN NOT NULL DEFAULT TRUE, version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255), updated_by VARCHAR(255),
    CONSTRAINT pk_trust_data_classification_policy PRIMARY KEY (id),
    CONSTRAINT uq_trust_dcp_code UNIQUE (workspace_id, policy_code)
);
CREATE TABLE IF NOT EXISTS trust_sensitive_object_registry (
    id UUID NOT NULL, workspace_id UUID, object_type_code VARCHAR(100) NOT NULL, classification VARCHAR(50) NOT NULL,
    access_logging_required BOOLEAN NOT NULL DEFAULT TRUE, export_reason_required BOOLEAN NOT NULL DEFAULT FALSE,
    search_index_allowed BOOLEAN NOT NULL DEFAULT FALSE, enabled BOOLEAN NOT NULL DEFAULT TRUE, version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), created_by VARCHAR(255), updated_by VARCHAR(255),
    CONSTRAINT pk_trust_sensitive_object PRIMARY KEY (id)
);
CREATE UNIQUE INDEX IF NOT EXISTS uq_trust_sensitive_object ON trust_sensitive_object_registry (COALESCE(workspace_id, '00000000-0000-0000-0000-000000000000'::uuid), object_type_code);
CREATE TABLE IF NOT EXISTS trust_sensitive_field_registry (
    id UUID NOT NULL, workspace_id UUID, object_type_code VARCHAR(100) NOT NULL, field_path VARCHAR(500) NOT NULL,
    classification VARCHAR(50) NOT NULL, masking_strategy VARCHAR(100) NOT NULL,
    access_logging_required BOOLEAN NOT NULL DEFAULT TRUE, search_index_allowed BOOLEAN NOT NULL DEFAULT FALSE,
    export_allowed BOOLEAN NOT NULL DEFAULT FALSE, enabled BOOLEAN NOT NULL DEFAULT TRUE, version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), created_by VARCHAR(255), updated_by VARCHAR(255),
    CONSTRAINT pk_trust_sensitive_field PRIMARY KEY (id)
);
CREATE TABLE IF NOT EXISTS trust_sensitive_access_log (
    id UUID NOT NULL, workspace_id UUID NOT NULL, project_id UUID, actor_principal_type VARCHAR(50) NOT NULL,
    actor_user_id UUID, actor_external_portal_account_id UUID, target_object_type VARCHAR(100) NOT NULL,
    target_object_id UUID, field_path VARCHAR(500), classification VARCHAR(50) NOT NULL, access_action VARCHAR(50) NOT NULL,
    access_channel VARCHAR(50), reason TEXT, request_path VARCHAR(500), ip_address_hash VARCHAR(255), user_agent_hash VARCHAR(255),
    occurred_at TIMESTAMPTZ NOT NULL, trace_id VARCHAR(100), version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), created_by VARCHAR(255), updated_by VARCHAR(255),
    CONSTRAINT pk_trust_sensitive_access_log PRIMARY KEY (id)
);
CREATE TABLE IF NOT EXISTS trust_export_audit_log (
    id UUID NOT NULL, workspace_id UUID NOT NULL, project_id UUID, actor_user_id UUID, actor_external_portal_account_id UUID,
    export_type VARCHAR(100) NOT NULL, target_object_type VARCHAR(100), classification VARCHAR(50) NOT NULL,
    filter_summary_json JSONB, row_count BIGINT, file_reference VARCHAR(500), reason TEXT, status VARCHAR(50) NOT NULL,
    completed_at TIMESTAMPTZ, failure_code VARCHAR(150), failure_message TEXT, trace_id VARCHAR(100), version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), created_by VARCHAR(255), updated_by VARCHAR(255),
    CONSTRAINT pk_trust_export_audit_log PRIMARY KEY (id)
);
CREATE TABLE IF NOT EXISTS trust_data_subject_index (
    id UUID NOT NULL, workspace_id UUID NOT NULL, subject_type VARCHAR(50) NOT NULL, subject_id UUID, subject_key_hash VARCHAR(255),
    display_name_snapshot VARCHAR(255), linked_user_id UUID, linked_external_contact_id UUID, linked_portal_account_id UUID,
    record_count BIGINT NOT NULL DEFAULT 0, last_rebuilt_at TIMESTAMPTZ, status VARCHAR(50) NOT NULL, version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), created_by VARCHAR(255), updated_by VARCHAR(255),
    CONSTRAINT pk_trust_data_subject_index PRIMARY KEY (id)
);
CREATE TABLE IF NOT EXISTS trust_privacy_request (
    id UUID NOT NULL, workspace_id UUID NOT NULL, request_code VARCHAR(150), request_type VARCHAR(50) NOT NULL,
    data_subject_index_id UUID, subject_type VARCHAR(50), subject_reference VARCHAR(255),
    submitted_by_user_id UUID, submitted_channel VARCHAR(50) NOT NULL, status VARCHAR(50) NOT NULL,
    request_description TEXT, assigned_owner_user_id UUID, due_date DATE, resolution_summary TEXT, rejection_reason TEXT,
    completed_at TIMESTAMPTZ, completed_by UUID, version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), created_by VARCHAR(255), updated_by VARCHAR(255),
    CONSTRAINT pk_trust_privacy_request PRIMARY KEY (id),
    CONSTRAINT ck_trust_privacy_status CHECK (status IN ('SUBMITTED','TRIAGED','IN_REVIEW','ACTION_PLANNED','COMPLETED','REJECTED','CANCELLED'))
);
CREATE TABLE IF NOT EXISTS trust_privacy_export_package (
    id UUID NOT NULL, workspace_id UUID NOT NULL, privacy_request_id UUID NOT NULL, data_subject_index_id UUID NOT NULL,
    status VARCHAR(50) NOT NULL, package_manifest_json JSONB, file_reference VARCHAR(500), expires_at TIMESTAMPTZ,
    completed_at TIMESTAMPTZ, completed_by UUID, failure_code VARCHAR(150), failure_message TEXT, version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), created_by VARCHAR(255), updated_by VARCHAR(255),
    CONSTRAINT pk_trust_privacy_export PRIMARY KEY (id)
);
CREATE TABLE IF NOT EXISTS trust_anonymization_plan (
    id UUID NOT NULL, workspace_id UUID NOT NULL, privacy_request_id UUID, data_subject_index_id UUID NOT NULL,
    status VARCHAR(50) NOT NULL, plan_json JSONB NOT NULL, dry_run_result_json JSONB, execution_result_json JSONB,
    legal_hold_blocked BOOLEAN NOT NULL DEFAULT FALSE, reason TEXT NOT NULL,
    executed_at TIMESTAMPTZ, executed_by UUID, cancelled_at TIMESTAMPTZ, cancelled_by UUID, version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), created_by VARCHAR(255), updated_by VARCHAR(255),
    CONSTRAINT pk_trust_anonymization_plan PRIMARY KEY (id)
);
CREATE TABLE IF NOT EXISTS trust_retention_policy (
    id UUID NOT NULL, workspace_id UUID NOT NULL, policy_code VARCHAR(150) NOT NULL, name VARCHAR(255) NOT NULL,
    object_type_code VARCHAR(100) NOT NULL, classification VARCHAR(50), retention_period_days INT NOT NULL,
    retention_action VARCHAR(50) NOT NULL, review_required BOOLEAN NOT NULL DEFAULT TRUE, enabled BOOLEAN NOT NULL DEFAULT TRUE,
    archived_at TIMESTAMPTZ, archived_by UUID, version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), created_by VARCHAR(255), updated_by VARCHAR(255),
    CONSTRAINT pk_trust_retention_policy PRIMARY KEY (id),
    CONSTRAINT uq_trust_retention_policy UNIQUE (workspace_id, policy_code)
);
CREATE TABLE IF NOT EXISTS trust_retention_job (
    id UUID NOT NULL, workspace_id UUID NOT NULL, retention_policy_id UUID, job_mode VARCHAR(50) NOT NULL, status VARCHAR(50) NOT NULL,
    candidate_count BIGINT NOT NULL DEFAULT 0, actioned_count BIGINT NOT NULL DEFAULT 0, skipped_legal_hold_count BIGINT NOT NULL DEFAULT 0,
    failed_count BIGINT NOT NULL DEFAULT 0, result_summary_json JSONB, started_at TIMESTAMPTZ, completed_at TIMESTAMPTZ,
    failure_code VARCHAR(150), failure_message TEXT, version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), created_by VARCHAR(255), updated_by VARCHAR(255),
    CONSTRAINT pk_trust_retention_job PRIMARY KEY (id)
);
CREATE TABLE IF NOT EXISTS trust_retention_candidate (
    id UUID NOT NULL, workspace_id UUID NOT NULL, retention_job_id UUID NOT NULL, object_type_code VARCHAR(100) NOT NULL,
    object_id UUID NOT NULL, classification VARCHAR(50), candidate_reason TEXT, proposed_action VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL, legal_hold_blocked BOOLEAN NOT NULL DEFAULT FALSE, result_message TEXT, version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), created_by VARCHAR(255), updated_by VARCHAR(255),
    CONSTRAINT pk_trust_retention_candidate PRIMARY KEY (id)
);
CREATE TABLE IF NOT EXISTS trust_legal_hold (
    id UUID NOT NULL, workspace_id UUID NOT NULL, hold_code VARCHAR(150), hold_type VARCHAR(50) NOT NULL,
    scope_type VARCHAR(50) NOT NULL, scope_id UUID, reason TEXT NOT NULL, status VARCHAR(50) NOT NULL,
    released_at TIMESTAMPTZ, released_by UUID, release_reason TEXT, version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), created_by VARCHAR(255), updated_by VARCHAR(255),
    CONSTRAINT pk_trust_legal_hold PRIMARY KEY (id),
    CONSTRAINT ck_trust_legal_hold_status CHECK (status IN ('ACTIVE','RELEASED','ARCHIVED'))
);
CREATE TABLE IF NOT EXISTS trust_consent_record (
    id UUID NOT NULL, workspace_id UUID NOT NULL, data_subject_index_id UUID, external_contact_id UUID, portal_account_id UUID,
    consent_type VARCHAR(100) NOT NULL, status VARCHAR(50) NOT NULL, source VARCHAR(100), source_reference VARCHAR(255),
    given_at TIMESTAMPTZ, withdrawn_at TIMESTAMPTZ, version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), created_by VARCHAR(255), updated_by VARCHAR(255),
    CONSTRAINT pk_trust_consent_record PRIMARY KEY (id)
);
CREATE TABLE IF NOT EXISTS trust_contact_suppression (
    id UUID NOT NULL, workspace_id UUID NOT NULL, data_subject_index_id UUID, external_contact_id UUID, portal_account_id UUID,
    suppression_type VARCHAR(100) NOT NULL, reason TEXT, status VARCHAR(50) NOT NULL,
    released_at TIMESTAMPTZ, released_by UUID, release_reason TEXT, version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), created_by VARCHAR(255), updated_by VARCHAR(255),
    CONSTRAINT pk_trust_contact_suppression PRIMARY KEY (id)
);
CREATE TABLE IF NOT EXISTS trust_access_review_campaign (
    id UUID NOT NULL, workspace_id UUID NOT NULL, campaign_code VARCHAR(150), name VARCHAR(255) NOT NULL,
    scope_json JSONB NOT NULL, status VARCHAR(50) NOT NULL, owner_user_id UUID, due_date DATE,
    started_at TIMESTAMPTZ, completed_at TIMESTAMPTZ, version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), created_by VARCHAR(255), updated_by VARCHAR(255),
    CONSTRAINT pk_trust_access_review_campaign PRIMARY KEY (id)
);
CREATE TABLE IF NOT EXISTS trust_permission_review_finding (
    id UUID NOT NULL, workspace_id UUID NOT NULL, campaign_id UUID, finding_type VARCHAR(100) NOT NULL, severity VARCHAR(50) NOT NULL,
    target_type VARCHAR(100), target_id UUID, principal_type VARCHAR(50), principal_id UUID,
    description TEXT, recommendation TEXT, status VARCHAR(50) NOT NULL,
    acknowledged_at TIMESTAMPTZ, acknowledged_by UUID, resolved_at TIMESTAMPTZ, resolved_by UUID,
    dismissed_at TIMESTAMPTZ, dismissed_by UUID, version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), created_by VARCHAR(255), updated_by VARCHAR(255),
    CONSTRAINT pk_trust_permission_review_finding PRIMARY KEY (id)
);
CREATE TABLE IF NOT EXISTS trust_compliance_evidence_record (
    id UUID NOT NULL, workspace_id UUID NOT NULL, evidence_code VARCHAR(150), evidence_type VARCHAR(100) NOT NULL,
    title VARCHAR(255) NOT NULL, description TEXT, source_type VARCHAR(100), source_id UUID, document_id UUID,
    status VARCHAR(50) NOT NULL, owner_user_id UUID, evidence_date DATE NOT NULL,
    finalized_at TIMESTAMPTZ, finalized_by UUID, version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), created_by VARCHAR(255), updated_by VARCHAR(255),
    CONSTRAINT pk_trust_compliance_evidence PRIMARY KEY (id),
    CONSTRAINT ck_trust_evidence_status CHECK (status IN ('DRAFT','FINALIZED','SUPERSEDED','ARCHIVED'))
);
