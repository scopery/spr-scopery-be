-- Phase 39: Integration hub foundation
CREATE TABLE IF NOT EXISTS integration_provider (
    id UUID NOT NULL, provider_code VARCHAR(150) NOT NULL, name VARCHAR(255) NOT NULL, category VARCHAR(100) NOT NULL,
    description TEXT, adapter_key VARCHAR(150), enabled BOOLEAN NOT NULL DEFAULT TRUE, seed_only BOOLEAN NOT NULL DEFAULT FALSE,
    capabilities_json JSONB, version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_integration_provider PRIMARY KEY (id), CONSTRAINT uq_integration_provider_code UNIQUE (provider_code)
);
CREATE TABLE IF NOT EXISTS integration_connection (
    id UUID NOT NULL, workspace_id UUID NOT NULL, project_id UUID, provider_code VARCHAR(150) NOT NULL,
    connection_scope VARCHAR(50) NOT NULL, name VARCHAR(255) NOT NULL, description TEXT, owner_user_id UUID,
    credential_reference_id UUID, config_json JSONB, status VARCHAR(50) NOT NULL, last_health_status VARCHAR(50),
    last_health_checked_at TIMESTAMPTZ, last_error_code VARCHAR(150), last_error_message TEXT,
    disabled_at TIMESTAMPTZ, disabled_by UUID, archived_at TIMESTAMPTZ, archived_by UUID, version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), created_by VARCHAR(255), updated_by VARCHAR(255),
    CONSTRAINT pk_integration_connection PRIMARY KEY (id)
);
CREATE TABLE IF NOT EXISTS integration_credential_reference (
    id UUID NOT NULL, workspace_id UUID NOT NULL, provider_code VARCHAR(150) NOT NULL, credential_type VARCHAR(50) NOT NULL,
    secret_reference VARCHAR(500) NOT NULL, status VARCHAR(50) NOT NULL, expires_at TIMESTAMPTZ, last_rotated_at TIMESTAMPTZ,
    revoked_at TIMESTAMPTZ, revoked_by UUID, version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), created_by VARCHAR(255), updated_by VARCHAR(255),
    CONSTRAINT pk_integration_credential PRIMARY KEY (id)
);
CREATE TABLE IF NOT EXISTS integration_connector_capability (
    id UUID NOT NULL, provider_code VARCHAR(150) NOT NULL, capability_code VARCHAR(150) NOT NULL, direction VARCHAR(50) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE, description TEXT, version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_integration_capability PRIMARY KEY (id),
    CONSTRAINT uq_integration_capability UNIQUE (provider_code, capability_code)
);
CREATE TABLE IF NOT EXISTS integration_connection_health_check (
    id UUID NOT NULL, workspace_id UUID NOT NULL, connection_id UUID NOT NULL, health_status VARCHAR(50) NOT NULL,
    checked_at TIMESTAMPTZ NOT NULL, duration_ms BIGINT, message TEXT, error_code VARCHAR(150), details_json JSONB, version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), created_by VARCHAR(255), updated_by VARCHAR(255),
    CONSTRAINT pk_integration_health_check PRIMARY KEY (id)
);
CREATE TABLE IF NOT EXISTS integration_data_mapping_profile (
    id UUID NOT NULL, workspace_id UUID NOT NULL, connection_id UUID, mapping_code VARCHAR(150) NOT NULL, name VARCHAR(255) NOT NULL,
    target_object_type VARCHAR(100) NOT NULL, source_format VARCHAR(100) NOT NULL, mapping_json JSONB NOT NULL,
    validation_rules_json JSONB, status VARCHAR(50) NOT NULL, archived_at TIMESTAMPTZ, archived_by UUID, version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), created_by VARCHAR(255), updated_by VARCHAR(255),
    CONSTRAINT pk_integration_mapping PRIMARY KEY (id), CONSTRAINT uq_integration_mapping UNIQUE (workspace_id, mapping_code)
);
CREATE TABLE IF NOT EXISTS integration_external_id_mapping (
    id UUID NOT NULL, workspace_id UUID NOT NULL, connection_id UUID NOT NULL, provider_code VARCHAR(150) NOT NULL,
    external_object_type VARCHAR(150) NOT NULL, external_id VARCHAR(500) NOT NULL, scopery_object_type VARCHAR(100) NOT NULL,
    scopery_object_id UUID NOT NULL, last_synced_at TIMESTAMPTZ, sync_state VARCHAR(50), metadata_json JSONB, version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_integration_external_id PRIMARY KEY (id),
    CONSTRAINT uq_integration_external_id UNIQUE (connection_id, external_object_type, external_id)
);
CREATE TABLE IF NOT EXISTS integration_import_template (
    id UUID NOT NULL, workspace_id UUID, template_code VARCHAR(150) NOT NULL, name VARCHAR(255) NOT NULL,
    target_object_type VARCHAR(100) NOT NULL, source_format VARCHAR(100) NOT NULL, schema_json JSONB NOT NULL,
    sample_file_reference VARCHAR(500), enabled BOOLEAN NOT NULL DEFAULT TRUE, version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), created_by VARCHAR(255), updated_by VARCHAR(255),
    CONSTRAINT pk_integration_import_template PRIMARY KEY (id)
);
CREATE TABLE IF NOT EXISTS integration_import_job (
    id UUID NOT NULL, workspace_id UUID NOT NULL, project_id UUID, connection_id UUID, import_template_id UUID, mapping_profile_id UUID,
    job_mode VARCHAR(50) NOT NULL, source_format VARCHAR(100) NOT NULL, source_file_reference VARCHAR(500),
    target_object_type VARCHAR(100) NOT NULL, status VARCHAR(50) NOT NULL,
    total_rows BIGINT NOT NULL DEFAULT 0, valid_rows BIGINT NOT NULL DEFAULT 0, invalid_rows BIGINT NOT NULL DEFAULT 0,
    created_rows BIGINT NOT NULL DEFAULT 0, updated_rows BIGINT NOT NULL DEFAULT 0, skipped_rows BIGINT NOT NULL DEFAULT 0,
    conflict_rows BIGINT NOT NULL DEFAULT 0, failed_rows BIGINT NOT NULL DEFAULT 0,
    started_at TIMESTAMPTZ, completed_at TIMESTAMPTZ, cancelled_at TIMESTAMPTZ, cancelled_by UUID,
    failure_code VARCHAR(150), failure_message TEXT, version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), created_by VARCHAR(255), updated_by VARCHAR(255),
    CONSTRAINT pk_integration_import_job PRIMARY KEY (id)
);
CREATE TABLE IF NOT EXISTS integration_import_row_result (
    id UUID NOT NULL, workspace_id UUID NOT NULL, import_job_id UUID NOT NULL, row_number BIGINT NOT NULL, row_reference VARCHAR(255),
    status VARCHAR(50) NOT NULL, message TEXT, validation_errors_json JSONB, target_object_type VARCHAR(100), target_object_id UUID,
    external_id VARCHAR(500), version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), created_by VARCHAR(255), updated_by VARCHAR(255),
    CONSTRAINT pk_integration_import_row PRIMARY KEY (id)
);
CREATE TABLE IF NOT EXISTS integration_export_profile (
    id UUID NOT NULL, workspace_id UUID NOT NULL, connection_id UUID, profile_code VARCHAR(150) NOT NULL, name VARCHAR(255) NOT NULL,
    export_format VARCHAR(50) NOT NULL, target_destination VARCHAR(100) NOT NULL, object_scope VARCHAR(100) NOT NULL,
    columns_json JSONB, filters_json JSONB, masking_policy VARCHAR(100), status VARCHAR(50) NOT NULL,
    archived_at TIMESTAMPTZ, archived_by UUID, version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), created_by VARCHAR(255), updated_by VARCHAR(255),
    CONSTRAINT pk_integration_export_profile PRIMARY KEY (id), CONSTRAINT uq_integration_export_profile UNIQUE (workspace_id, profile_code)
);
CREATE TABLE IF NOT EXISTS integration_export_job (
    id UUID NOT NULL, workspace_id UUID NOT NULL, project_id UUID, connection_id UUID, export_profile_id UUID,
    export_format VARCHAR(50) NOT NULL, object_scope VARCHAR(100) NOT NULL, status VARCHAR(50) NOT NULL,
    row_count BIGINT, file_reference VARCHAR(500), expires_at TIMESTAMPTZ, started_at TIMESTAMPTZ, completed_at TIMESTAMPTZ,
    cancelled_at TIMESTAMPTZ, cancelled_by UUID, failure_code VARCHAR(150), failure_message TEXT, export_audit_log_id UUID, version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), created_by VARCHAR(255), updated_by VARCHAR(255),
    CONSTRAINT pk_integration_export_job PRIMARY KEY (id)
);
CREATE TABLE IF NOT EXISTS integration_webhook_subscription (
    id UUID NOT NULL, workspace_id UUID NOT NULL, connection_id UUID, name VARCHAR(255) NOT NULL, endpoint_url VARCHAR(1000) NOT NULL,
    event_types_json JSONB NOT NULL, payload_version VARCHAR(50) NOT NULL, signing_secret_reference_id UUID,
    status VARCHAR(50) NOT NULL, max_attempts INT NOT NULL DEFAULT 5, timeout_seconds INT NOT NULL DEFAULT 10,
    disabled_at TIMESTAMPTZ, disabled_by UUID, archived_at TIMESTAMPTZ, archived_by UUID, version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), created_by VARCHAR(255), updated_by VARCHAR(255),
    CONSTRAINT pk_integration_webhook_sub PRIMARY KEY (id)
);
CREATE TABLE IF NOT EXISTS integration_webhook_delivery_attempt (
    id UUID NOT NULL, workspace_id UUID NOT NULL, webhook_subscription_id UUID NOT NULL, event_type VARCHAR(150) NOT NULL,
    event_id UUID, target_object_type VARCHAR(100), target_object_id UUID, status VARCHAR(50) NOT NULL, attempt_number INT NOT NULL,
    request_id VARCHAR(150), response_status_code INT, response_body_redacted TEXT, duration_ms BIGINT, next_retry_at TIMESTAMPTZ,
    sent_at TIMESTAMPTZ, failure_code VARCHAR(150), failure_message TEXT, version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), created_by VARCHAR(255), updated_by VARCHAR(255),
    CONSTRAINT pk_integration_webhook_delivery PRIMARY KEY (id)
);
CREATE TABLE IF NOT EXISTS integration_inbound_webhook_endpoint (
    id UUID NOT NULL, workspace_id UUID NOT NULL, connection_id UUID NOT NULL, endpoint_code VARCHAR(150) NOT NULL,
    provider_code VARCHAR(150) NOT NULL, signing_secret_reference_id UUID, status VARCHAR(50) NOT NULL,
    disabled_at TIMESTAMPTZ, disabled_by UUID, version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), created_by VARCHAR(255), updated_by VARCHAR(255),
    CONSTRAINT pk_integration_inbound_endpoint PRIMARY KEY (id)
);
CREATE TABLE IF NOT EXISTS integration_inbound_webhook_event (
    id UUID NOT NULL, workspace_id UUID NOT NULL, inbound_endpoint_id UUID NOT NULL, provider_code VARCHAR(150) NOT NULL,
    external_event_id VARCHAR(500), event_type VARCHAR(150) NOT NULL, status VARCHAR(50) NOT NULL,
    payload_reference VARCHAR(500), payload_redacted_json JSONB, received_at TIMESTAMPTZ NOT NULL, processed_at TIMESTAMPTZ,
    failure_code VARCHAR(150), failure_message TEXT, version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), created_by VARCHAR(255), updated_by VARCHAR(255),
    CONSTRAINT pk_integration_inbound_event PRIMARY KEY (id)
);
CREATE TABLE IF NOT EXISTS integration_sync_job (
    id UUID NOT NULL, workspace_id UUID NOT NULL, project_id UUID, connection_id UUID NOT NULL, mapping_profile_id UUID,
    sync_code VARCHAR(150), name VARCHAR(255) NOT NULL, sync_direction VARCHAR(50) NOT NULL, sync_mode VARCHAR(50) NOT NULL,
    object_scope VARCHAR(100) NOT NULL, conflict_strategy VARCHAR(50) NOT NULL, schedule_json JSONB, status VARCHAR(50) NOT NULL,
    disabled_at TIMESTAMPTZ, disabled_by UUID, archived_at TIMESTAMPTZ, archived_by UUID, version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), created_by VARCHAR(255), updated_by VARCHAR(255),
    CONSTRAINT pk_integration_sync_job PRIMARY KEY (id)
);
CREATE TABLE IF NOT EXISTS integration_sync_run (
    id UUID NOT NULL, workspace_id UUID NOT NULL, sync_job_id UUID NOT NULL, status VARCHAR(50) NOT NULL,
    started_at TIMESTAMPTZ, completed_at TIMESTAMPTZ, processed_count BIGINT NOT NULL DEFAULT 0, created_count BIGINT NOT NULL DEFAULT 0,
    updated_count BIGINT NOT NULL DEFAULT 0, skipped_count BIGINT NOT NULL DEFAULT 0, conflict_count BIGINT NOT NULL DEFAULT 0,
    failed_count BIGINT NOT NULL DEFAULT 0, failure_code VARCHAR(150), failure_message TEXT, version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), created_by VARCHAR(255), updated_by VARCHAR(255),
    CONSTRAINT pk_integration_sync_run PRIMARY KEY (id)
);
CREATE TABLE IF NOT EXISTS integration_sync_cursor (
    id UUID NOT NULL, workspace_id UUID NOT NULL, sync_job_id UUID NOT NULL, cursor_key VARCHAR(150) NOT NULL, cursor_value TEXT,
    last_successful_sync_at TIMESTAMPTZ, version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_integration_sync_cursor PRIMARY KEY (id), CONSTRAINT uq_integration_sync_cursor UNIQUE (sync_job_id, cursor_key)
);
CREATE TABLE IF NOT EXISTS integration_sync_conflict (
    id UUID NOT NULL, workspace_id UUID NOT NULL, sync_job_id UUID NOT NULL, sync_run_id UUID, connection_id UUID NOT NULL,
    conflict_type VARCHAR(100) NOT NULL, scopery_object_type VARCHAR(100), scopery_object_id UUID,
    external_object_type VARCHAR(100), external_id VARCHAR(500), severity VARCHAR(50) NOT NULL, status VARCHAR(50) NOT NULL,
    description TEXT, resolution_strategy VARCHAR(50), resolved_at TIMESTAMPTZ, resolved_by UUID, resolution_notes TEXT, version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), created_by VARCHAR(255), updated_by VARCHAR(255),
    CONSTRAINT pk_integration_sync_conflict PRIMARY KEY (id)
);
CREATE TABLE IF NOT EXISTS integration_provider_rate_limit_state (
    id UUID NOT NULL, workspace_id UUID NOT NULL, connection_id UUID NOT NULL, provider_code VARCHAR(150) NOT NULL,
    status VARCHAR(50) NOT NULL, limit_name VARCHAR(150), remaining_count BIGINT, reset_at TIMESTAMPTZ, backoff_until TIMESTAMPTZ,
    last_updated_at TIMESTAMPTZ NOT NULL, version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_integration_rate_limit PRIMARY KEY (id)
);
CREATE TABLE IF NOT EXISTS integration_dead_letter_event (
    id UUID NOT NULL, workspace_id UUID NOT NULL, source_type VARCHAR(100) NOT NULL, source_id UUID, event_type VARCHAR(150),
    payload_reference VARCHAR(500), failure_code VARCHAR(150) NOT NULL, failure_message TEXT, attempt_count INT NOT NULL DEFAULT 0,
    status VARCHAR(50) NOT NULL, last_attempt_at TIMESTAMPTZ, resolved_at TIMESTAMPTZ, resolved_by UUID, version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_integration_dead_letter PRIMARY KEY (id)
);
