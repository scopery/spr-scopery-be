-- ============================================================
-- V27: IAM Permission Classification Metadata
-- Adds UI/admin-facing metadata used to group permissions,
-- show risk level, and restrict which subject types can receive them.
-- ============================================================

ALTER TABLE iam_permission
    ADD COLUMN permission_category VARCHAR(100),
    ADD COLUMN assignable_subject_types VARCHAR(255),
    ADD COLUMN risk_level VARCHAR(50);

UPDATE iam_permission
SET permission_category = CASE code
        WHEN 'SYSTEM_IAM_MANAGEMENT' THEN 'SECURITY'
        WHEN 'SYSTEM_RESOURCE_MANAGEMENT' THEN 'RESOURCE_ADMIN'
        WHEN 'SYSTEM_GOVERNANCE_MANAGEMENT' THEN 'GOVERNANCE'
        WHEN 'SYSTEM_NOTIFICATION_MANAGEMENT' THEN 'NOTIFICATION_ADMIN'
        WHEN 'ORGANIZATION_MANAGEMENT' THEN 'ORGANIZATION_ADMIN'
        WHEN 'WORKSPACE_MANAGEMENT' THEN 'WORKSPACE_ADMIN'
        WHEN 'WORKSPACE_ACCESS_MANAGEMENT' THEN 'ACCESS_CONTROL'
        WHEN 'WORKSPACE_ROLE_MANAGEMENT' THEN 'ACCESS_CONTROL'
        WHEN 'TEAM_MANAGEMENT' THEN 'TEAM_ADMIN'
        WHEN 'WORKSPACE_MEMBER_MANAGEMENT' THEN 'MEMBER_ADMIN'
        WHEN 'TEAM_MEMBER_MANAGEMENT' THEN 'MEMBER_ADMIN'
        WHEN 'DOCUMENT_TYPE_MANAGEMENT' THEN 'CONTENT_ADMIN'
        ELSE CASE resource_scope_level
            WHEN 'SYSTEM' THEN 'SECURITY'
            WHEN 'ORGANIZATION' THEN 'ORGANIZATION_ADMIN'
            ELSE 'WORKSPACE_ADMIN'
        END
    END,
    assignable_subject_types = CASE resource_scope_level
        WHEN 'WORKSPACE' THEN 'ROLE,TEAM,USER'
        ELSE 'ROLE,USER'
    END,
    risk_level = CASE code
        WHEN 'SYSTEM_IAM_MANAGEMENT' THEN 'CRITICAL'
        WHEN 'SYSTEM_RESOURCE_MANAGEMENT' THEN 'CRITICAL'
        WHEN 'SYSTEM_GOVERNANCE_MANAGEMENT' THEN 'CRITICAL'
        WHEN 'SYSTEM_NOTIFICATION_MANAGEMENT' THEN 'HIGH'
        WHEN 'ORGANIZATION_MANAGEMENT' THEN 'HIGH'
        WHEN 'WORKSPACE_MANAGEMENT' THEN 'HIGH'
        WHEN 'WORKSPACE_ACCESS_MANAGEMENT' THEN 'HIGH'
        WHEN 'WORKSPACE_ROLE_MANAGEMENT' THEN 'HIGH'
        ELSE CASE resource_scope_level
            WHEN 'SYSTEM' THEN 'CRITICAL'
            WHEN 'ORGANIZATION' THEN 'HIGH'
            ELSE 'MEDIUM'
        END
    END
WHERE permission_category IS NULL
   OR assignable_subject_types IS NULL
   OR risk_level IS NULL;

ALTER TABLE iam_permission
    ALTER COLUMN permission_category SET NOT NULL,
    ALTER COLUMN assignable_subject_types SET NOT NULL,
    ALTER COLUMN risk_level SET NOT NULL;

ALTER TABLE iam_permission
    ADD CONSTRAINT ck_iam_permission_category
        CHECK (permission_category IN (
            'SECURITY',
            'RESOURCE_ADMIN',
            'GOVERNANCE',
            'NOTIFICATION_ADMIN',
            'ORGANIZATION_ADMIN',
            'WORKSPACE_ADMIN',
            'ACCESS_CONTROL',
            'TEAM_ADMIN',
            'MEMBER_ADMIN',
            'CONTENT_ADMIN'
        )),
    ADD CONSTRAINT ck_iam_permission_risk_level
        CHECK (risk_level IN ('LOW', 'MEDIUM', 'HIGH', 'CRITICAL')),
    ADD CONSTRAINT ck_iam_permission_assignable_subject_types_not_blank
        CHECK (btrim(assignable_subject_types) <> '');

CREATE INDEX idx_iam_permission_category
    ON iam_permission (permission_category);

CREATE INDEX idx_iam_permission_risk_level
    ON iam_permission (risk_level);
