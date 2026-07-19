-- Phase 34: Governance by permission/versioning/ownership/locking (NO approval workflow)
CREATE TABLE IF NOT EXISTS governance_object_type (
    id UUID NOT NULL, object_type_code VARCHAR(50) NOT NULL, ownership_supported BOOLEAN NOT NULL DEFAULT TRUE,
    versioning_supported BOOLEAN NOT NULL DEFAULT TRUE, locking_supported BOOLEAN NOT NULL DEFAULT TRUE,
    restore_supported BOOLEAN NOT NULL DEFAULT TRUE, enabled BOOLEAN NOT NULL DEFAULT TRUE,
    version INT NOT NULL DEFAULT 0, created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255), updated_by VARCHAR(255),
    CONSTRAINT pk_governance_object_type PRIMARY KEY (id), CONSTRAINT uq_governance_object_type UNIQUE (object_type_code)
);
CREATE TABLE IF NOT EXISTS governance_policy (
    id UUID NOT NULL, workspace_id UUID NOT NULL, object_type_code VARCHAR(50) NOT NULL,
    versioning_mode VARCHAR(50), version_on_update BOOLEAN NOT NULL DEFAULT TRUE, lock_on_finalize BOOLEAN NOT NULL DEFAULT TRUE,
    allow_restore BOOLEAN NOT NULL DEFAULT TRUE, allow_owner_grant BOOLEAN NOT NULL DEFAULT TRUE,
    baseline_guard_mode VARCHAR(50), audit_level VARCHAR(50), status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
    version INT NOT NULL DEFAULT 0, created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255), updated_by VARCHAR(255),
    CONSTRAINT pk_governance_policy PRIMARY KEY (id), CONSTRAINT uq_governance_policy UNIQUE (workspace_id, object_type_code)
);
CREATE TABLE IF NOT EXISTS governance_object_ownership (
    id UUID NOT NULL, workspace_id UUID NOT NULL, project_id UUID, object_type_code VARCHAR(50) NOT NULL, target_id UUID NOT NULL,
    owner_target_type VARCHAR(50) NOT NULL, owner_target_id UUID NOT NULL, status VARCHAR(50) NOT NULL,
    assigned_at TIMESTAMPTZ, assigned_by UUID, revoked_at TIMESTAMPTZ, revoked_by UUID,
    version INT NOT NULL DEFAULT 0, created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255), updated_by VARCHAR(255),
    CONSTRAINT pk_governance_object_ownership PRIMARY KEY (id)
);
CREATE UNIQUE INDEX IF NOT EXISTS uq_governance_ownership_active ON governance_object_ownership(object_type_code, target_id) WHERE status = 'ACTIVE';
CREATE TABLE IF NOT EXISTS governance_object_access_grant (
    id UUID NOT NULL, workspace_id UUID NOT NULL, project_id UUID, object_type_code VARCHAR(50) NOT NULL, target_id UUID NOT NULL,
    grantee_type VARCHAR(50) NOT NULL, grantee_id UUID NOT NULL, grant_role VARCHAR(50) NOT NULL, status VARCHAR(50) NOT NULL,
    expires_at TIMESTAMPTZ, version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), created_by VARCHAR(255), updated_by VARCHAR(255),
    CONSTRAINT pk_governance_object_access_grant PRIMARY KEY (id),
    CONSTRAINT ck_governance_grant_role CHECK (grant_role IN ('VIEW','COMMENT','EDIT','MANAGE','OWNER'))
);
CREATE TABLE IF NOT EXISTS governance_version_record (
    id UUID NOT NULL, workspace_id UUID NOT NULL, project_id UUID, object_type_code VARCHAR(50) NOT NULL, target_id UUID NOT NULL,
    domain_version_type VARCHAR(50), domain_version_id UUID, snapshot_id UUID, change_type VARCHAR(50),
    change_reason TEXT, current_flag BOOLEAN NOT NULL DEFAULT FALSE, finalized_flag BOOLEAN NOT NULL DEFAULT FALSE,
    restore_eligible BOOLEAN NOT NULL DEFAULT TRUE, version_number INT NOT NULL DEFAULT 1,
    version INT NOT NULL DEFAULT 0, created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255), updated_by VARCHAR(255),
    CONSTRAINT pk_governance_version_record PRIMARY KEY (id)
);
CREATE TABLE IF NOT EXISTS governance_snapshot (
    id UUID NOT NULL, workspace_id UUID NOT NULL, object_type_code VARCHAR(50) NOT NULL, target_id UUID NOT NULL,
    snapshot_mode VARCHAR(50), schema_version VARCHAR(50), snapshot_json TEXT NOT NULL,
    masked_fields_json TEXT, sensitive_fields_present BOOLEAN NOT NULL DEFAULT FALSE,
    version INT NOT NULL DEFAULT 0, created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255), updated_by VARCHAR(255),
    CONSTRAINT pk_governance_snapshot PRIMARY KEY (id)
);
CREATE TABLE IF NOT EXISTS governance_object_lock (
    id UUID NOT NULL, workspace_id UUID NOT NULL, project_id UUID, object_type_code VARCHAR(50) NOT NULL, target_id UUID NOT NULL,
    lock_type VARCHAR(50) NOT NULL, status VARCHAR(50) NOT NULL, locked_at TIMESTAMPTZ, locked_by UUID,
    released_at TIMESTAMPTZ, released_by UUID, reason TEXT,
    version INT NOT NULL DEFAULT 0, created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255), updated_by VARCHAR(255),
    CONSTRAINT pk_governance_object_lock PRIMARY KEY (id)
);
CREATE UNIQUE INDEX IF NOT EXISTS uq_governance_lock_active ON governance_object_lock(object_type_code, target_id, lock_type) WHERE status = 'ACTIVE';
CREATE TABLE IF NOT EXISTS governance_restore_request (
    id UUID NOT NULL, workspace_id UUID NOT NULL, project_id UUID, object_type_code VARCHAR(50) NOT NULL, target_id UUID NOT NULL,
    restore_from_version_record_id UUID NOT NULL, status VARCHAR(50) NOT NULL, reason TEXT,
    completed_at TIMESTAMPTZ, version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(), created_by VARCHAR(255), updated_by VARCHAR(255),
    CONSTRAINT pk_governance_restore_request PRIMARY KEY (id),
    CONSTRAINT ck_governance_restore_status CHECK (status IN ('REQUESTED','COMPLETED','FAILED','CANCELLED'))
);
