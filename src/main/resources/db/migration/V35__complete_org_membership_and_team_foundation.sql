-- Complete the additive organization-membership and organization-team model.
-- V32 was intentionally not introduced after V33/V34; its permission seed is included here.

ALTER TABLE org_member
    ADD COLUMN source VARCHAR(100),
    ADD COLUMN suspended_at TIMESTAMP,
    ADD COLUMN removed_at TIMESTAMP,
    ADD COLUMN version INT NOT NULL DEFAULT 0;

UPDATE org_member
SET source = CASE
        WHEN created_by = 'MIGRATION_V31' THEN 'WORKSPACE_BACKFILL'
        ELSE 'MANUAL'
    END,
    status = CASE WHEN status = 'INACTIVE' THEN 'REMOVED' ELSE status END,
    removed_at = CASE WHEN status = 'INACTIVE' THEN COALESCE(updated_at, CURRENT_TIMESTAMP) ELSE removed_at END;

ALTER TABLE org_member
    ALTER COLUMN source SET NOT NULL,
    ADD CONSTRAINT fk_org_member_user FOREIGN KEY (user_id) REFERENCES iam_user (id),
    ADD CONSTRAINT ck_org_member_status CHECK (status IN ('ACTIVE', 'SUSPENDED', 'REMOVED')),
    ADD CONSTRAINT ck_org_member_source CHECK (source IN (
        'SYSTEM_BOOTSTRAP', 'MANUAL', 'ORGANIZATION_INVITATION',
        'WORKSPACE_INVITATION', 'WORKSPACE_BACKFILL'));

ALTER TABLE org_team
    ADD COLUMN version INT NOT NULL DEFAULT 0;

ALTER TABLE org_team_member
    ADD CONSTRAINT fk_org_team_member_user FOREIGN KEY (user_id) REFERENCES iam_user (id);

INSERT INTO iam_permission (
    id, code, module_code, name, description, resource_scope_level,
    data_access_policy, status, permission_category, assignable_subject_types,
    risk_level, created_at, updated_at, created_by, updated_by)
VALUES
    (gen_random_uuid(), 'ORGANIZATION_MEMBER_MANAGEMENT', 'WORKSPACE',
     'Organization Member Management', 'Manage canonical organization memberships',
     'ORGANIZATION', 'SCOPE_WIDE', 'ACTIVE', 'MEMBER_ADMIN', 'ROLE,USER',
     'HIGH', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'MIGRATION_V35', 'MIGRATION_V35'),
    (gen_random_uuid(), 'ORGANIZATION_INVITATION_MANAGEMENT', 'WORKSPACE',
     'Organization Invitation Management', 'Manage organization invitations',
     'ORGANIZATION', 'SCOPE_WIDE', 'ACTIVE', 'MEMBER_ADMIN', 'ROLE,USER',
     'HIGH', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'MIGRATION_V35', 'MIGRATION_V35')
ON CONFLICT (code) DO NOTHING;

INSERT INTO iam_permission_action (
    id, permission_id, action_code, name, description, status,
    created_at, updated_at, created_by, updated_by)
SELECT gen_random_uuid(), p.id, a.action_code, a.name, a.description, 'ACTIVE',
       CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'MIGRATION_V35', 'MIGRATION_V35'
FROM iam_permission p
CROSS JOIN (VALUES
    ('VIEW', 'View', 'View organization members or invitations'),
    ('CREATE', 'Create', 'Create organization memberships or invitations'),
    ('UPDATE', 'Update', 'Activate or suspend organization memberships'),
    ('DELETE', 'Delete', 'Remove memberships or revoke invitations')
) AS a(action_code, name, description)
WHERE p.code IN ('ORGANIZATION_MEMBER_MANAGEMENT', 'ORGANIZATION_INVITATION_MANAGEMENT')
ON CONFLICT (permission_id, action_code) DO NOTHING;
