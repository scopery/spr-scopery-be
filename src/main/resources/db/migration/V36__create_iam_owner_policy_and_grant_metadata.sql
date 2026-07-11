CREATE TABLE iam_owner_policy
(
    id               UUID         NOT NULL,
    resource_type    VARCHAR(100) NOT NULL,
    policy_version   INT          NOT NULL,
    status           VARCHAR(50)  NOT NULL,
    action_bundle    JSONB        NOT NULL,
    inheritance_scope VARCHAR(50) NOT NULL,
    can_delegate     BOOLEAN      NOT NULL DEFAULT FALSE,
    delegation_depth INT          NOT NULL DEFAULT 0,
    effective_from   TIMESTAMP    NOT NULL,
    effective_to     TIMESTAMP,
    version          INT          NOT NULL DEFAULT 0,
    created_at       TIMESTAMP    NOT NULL,
    updated_at       TIMESTAMP    NOT NULL,
    created_by       VARCHAR(100),
    updated_by       VARCHAR(100),
    CONSTRAINT pk_iam_owner_policy PRIMARY KEY (id),
    CONSTRAINT uq_iam_owner_policy_resource_version UNIQUE (resource_type, policy_version),
    CONSTRAINT ck_iam_owner_policy_status CHECK (status IN ('ACTIVE', 'SUPERSEDED')),
    CONSTRAINT ck_iam_owner_policy_scope CHECK (
        inheritance_scope IN ('SELF_ONLY', 'DESCENDANTS', 'SPECIFIC_CHILDREN')),
    CONSTRAINT ck_iam_owner_policy_delegation_depth CHECK (delegation_depth >= 0)
);

CREATE UNIQUE INDEX uq_iam_owner_policy_active_resource
    ON iam_owner_policy (resource_type) WHERE status = 'ACTIVE';

INSERT INTO iam_owner_policy (
    id, resource_type, policy_version, status, action_bundle, inheritance_scope,
    can_delegate, delegation_depth, effective_from, version,
    created_at, updated_at, created_by, updated_by)
VALUES
    (gen_random_uuid(), 'ORGANIZATION', 1, 'ACTIVE',
     '[{"permissionCode":"ORGANIZATION_MANAGEMENT","actionCode":"VIEW"},
       {"permissionCode":"ORGANIZATION_MANAGEMENT","actionCode":"MANAGE"},
       {"permissionCode":"ORGANIZATION_MANAGEMENT","actionCode":"CREATE_WORKSPACE"},
       {"permissionCode":"TEAM_MANAGEMENT","actionCode":"VIEW"},
       {"permissionCode":"TEAM_MANAGEMENT","actionCode":"CREATE"},
       {"permissionCode":"TEAM_MANAGEMENT","actionCode":"UPDATE"},
       {"permissionCode":"TEAM_MANAGEMENT","actionCode":"ARCHIVE"},
       {"permissionCode":"TEAM_MANAGEMENT","actionCode":"MANAGE"}]'::jsonb,
     'DESCENDANTS', TRUE, 2, CURRENT_TIMESTAMP, 0,
     CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'MIGRATION_V36', 'MIGRATION_V36'),
    (gen_random_uuid(), 'WORKSPACE', 1, 'ACTIVE',
     '[{"permissionCode":"WORKSPACE_MANAGEMENT","actionCode":"VIEW"},
       {"permissionCode":"WORKSPACE_MANAGEMENT","actionCode":"MANAGE"},
       {"permissionCode":"WORKSPACE_MANAGEMENT","actionCode":"MANAGE_SETTING"},
       {"permissionCode":"WORKSPACE_ACCESS_MANAGEMENT","actionCode":"MANAGE_MEMBER"},
       {"permissionCode":"WORKSPACE_ACCESS_MANAGEMENT","actionCode":"MANAGE_ACCESS"},
       {"permissionCode":"WORKSPACE_ACCESS_MANAGEMENT","actionCode":"MANAGE_PERMISSION"},
       {"permissionCode":"TEAM_MANAGEMENT","actionCode":"VIEW"},
       {"permissionCode":"TEAM_MANAGEMENT","actionCode":"CREATE"},
       {"permissionCode":"TEAM_MANAGEMENT","actionCode":"UPDATE"},
       {"permissionCode":"TEAM_MANAGEMENT","actionCode":"ARCHIVE"},
       {"permissionCode":"TEAM_MANAGEMENT","actionCode":"MANAGE"}]'::jsonb,
     'DESCENDANTS', TRUE, 1, CURRENT_TIMESTAMP, 0,
     CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'MIGRATION_V36', 'MIGRATION_V36'),
    (gen_random_uuid(), 'TEAM', 1, 'ACTIVE',
     '[{"permissionCode":"TEAM_MANAGEMENT","actionCode":"VIEW"},
       {"permissionCode":"TEAM_MANAGEMENT","actionCode":"MANAGE"},
       {"permissionCode":"TEAM_MEMBER_MANAGEMENT","actionCode":"VIEW"},
       {"permissionCode":"TEAM_MEMBER_MANAGEMENT","actionCode":"ADD"},
       {"permissionCode":"TEAM_MEMBER_MANAGEMENT","actionCode":"REMOVE"}]'::jsonb,
     'SELF_ONLY', TRUE, 1, CURRENT_TIMESTAMP, 0,
     CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'MIGRATION_V36', 'MIGRATION_V36');

ALTER TABLE iam_access_grant
    ADD COLUMN kind VARCHAR(50) NOT NULL DEFAULT 'DIRECT',
    ADD COLUMN source_policy_id UUID,
    ADD COLUMN can_delegate BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN delegation_depth INT NOT NULL DEFAULT 0,
    ADD COLUMN expires_at TIMESTAMP,
    ADD COLUMN condition_json JSONB,
    ADD COLUMN reason TEXT,
    ADD COLUMN version INT NOT NULL DEFAULT 0,
    ADD CONSTRAINT fk_iam_access_grant_owner_policy
        FOREIGN KEY (source_policy_id) REFERENCES iam_owner_policy (id),
    ADD CONSTRAINT ck_iam_access_grant_kind
        CHECK (kind IN ('OWNER', 'DIRECT', 'TEAM', 'ROLE', 'DELEGATED')),
    ADD CONSTRAINT ck_iam_access_grant_delegation_depth CHECK (delegation_depth >= 0);

UPDATE iam_access_grant grant_row
SET kind = CASE
        WHEN grant_row.subject_type = 'TEAM' THEN 'TEAM'
        WHEN grant_row.subject_type = 'ROLE' THEN 'ROLE'
        WHEN grant_row.subject_id = resource.owner_user_id THEN 'OWNER'
        ELSE 'DIRECT'
    END,
    source_policy_id = CASE WHEN grant_row.subject_id = resource.owner_user_id THEN policy.id ELSE NULL END,
    can_delegate = CASE WHEN grant_row.subject_id = resource.owner_user_id THEN policy.can_delegate ELSE FALSE END,
    delegation_depth = CASE WHEN grant_row.subject_id = resource.owner_user_id THEN policy.delegation_depth ELSE 0 END
FROM iam_auth_resource resource
LEFT JOIN iam_owner_policy policy
       ON policy.resource_type = resource.resource_type AND policy.status = 'ACTIVE'
WHERE grant_row.resource_id = resource.id;

CREATE INDEX idx_iam_access_grant_expires_at ON iam_access_grant (expires_at);
CREATE INDEX idx_iam_access_grant_source_policy ON iam_access_grant (source_policy_id);

ALTER TABLE iam_auth_resource
    ADD COLUMN organization_id UUID,
    ADD COLUMN version INT NOT NULL DEFAULT 0;

UPDATE iam_auth_resource SET organization_id = ref_id WHERE resource_type = 'ORGANIZATION';

UPDATE iam_auth_resource resource
SET organization_id = workspace.organization_id
FROM workspace_workspace workspace
WHERE resource.resource_type = 'WORKSPACE' AND resource.ref_id = workspace.id;

UPDATE iam_auth_resource resource
SET organization_id = workspace.organization_id
FROM workspace_workspace workspace
WHERE resource.workspace_id = workspace.id AND resource.organization_id IS NULL;

UPDATE iam_auth_resource workspace_resource
SET parent_resource_id = organization_resource.id
FROM iam_auth_resource organization_resource
WHERE workspace_resource.resource_type = 'WORKSPACE'
  AND organization_resource.resource_type = 'ORGANIZATION'
  AND organization_resource.ref_id = workspace_resource.organization_id
  AND workspace_resource.parent_resource_id IS NULL;

CREATE INDEX idx_iam_auth_resource_organization_id ON iam_auth_resource (organization_id);

ALTER TABLE workspace_organization ADD COLUMN version INT NOT NULL DEFAULT 0;
ALTER TABLE workspace_workspace ADD COLUMN version INT NOT NULL DEFAULT 0;
