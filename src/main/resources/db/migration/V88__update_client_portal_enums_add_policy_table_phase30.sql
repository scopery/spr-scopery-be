-- Phase 30 gap fixes: extend enum check constraints and add missing tables

-- external_portal_account: add LOCKED, DEACTIVATED
ALTER TABLE external_portal_account DROP CONSTRAINT IF EXISTS ck_external_portal_account_status;
ALTER TABLE external_portal_account ADD CONSTRAINT ck_external_portal_account_status
    CHECK (status IN ('INVITED','ACTIVE','SUSPENDED','LOCKED','DEACTIVATED','ARCHIVED'));

-- external_project_access_grant: add SUSPENDED, ARCHIVED
ALTER TABLE external_project_access_grant DROP CONSTRAINT IF EXISTS ck_external_project_access_grant_status;
ALTER TABLE external_project_access_grant ADD CONSTRAINT ck_external_project_access_grant_status
    CHECK (status IN ('ACTIVE','SUSPENDED','REVOKED','EXPIRED','ARCHIVED'));

-- client_review_request: replace old statuses with new lifecycle
ALTER TABLE client_review_request DROP CONSTRAINT IF EXISTS ck_client_review_request_status;
ALTER TABLE client_review_request ADD CONSTRAINT ck_client_review_request_status
    CHECK (status IN ('DRAFT','SENT','VIEWED','RESPONDED','OVERDUE','CANCELLED','EXPIRED'));

-- client_feedback: add conversion and archive statuses
ALTER TABLE client_feedback DROP CONSTRAINT IF EXISTS ck_client_feedback_status;
ALTER TABLE client_feedback ADD CONSTRAINT ck_client_feedback_status
    CHECK (status IN ('SUBMITTED','TRIAGED','ACCEPTED','REJECTED','CLOSED',
                      'CONVERTED_TO_REQUIREMENT','CONVERTED_TO_DEFECT','CONVERTED_TO_CHANGE_REQUEST','ARCHIVED'));

-- external_portal_permission_policy: new table
CREATE TABLE IF NOT EXISTS external_portal_permission_policy (
    id UUID NOT NULL,
    workspace_id UUID NOT NULL,
    code VARCHAR(100) NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    permissions_json TEXT,
    version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    CONSTRAINT pk_external_portal_permission_policy PRIMARY KEY (id),
    CONSTRAINT uq_external_portal_permission_policy_code UNIQUE (workspace_id, code)
);
CREATE INDEX IF NOT EXISTS idx_external_portal_permission_policy_workspace
    ON external_portal_permission_policy(workspace_id);
