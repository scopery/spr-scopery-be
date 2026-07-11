-- ============================================================
-- V34: Backfill org_team, org_team_member, org_team_workspace_assignment
-- from existing workspace_team + workspace_team_member data.
-- ============================================================

-- Step 1: Insert org_team rows derived from workspace_team.
-- organization_id is derived via workspace_workspace.organization_id.
INSERT INTO org_team (id, organization_id, code, name, description, status,
                      created_at, updated_at, created_by)
SELECT
    wt.id,
    ww.organization_id,
    wt.code,
    wt.name,
    wt.description,
    wt.status,
    wt.created_at,
    wt.updated_at,
    'MIGRATION_V34'
FROM workspace_team wt
         JOIN workspace_workspace ww ON ww.id = wt.workspace_id
ON CONFLICT (organization_id, code) DO NOTHING;

-- Step 2: Insert org_team_member rows from workspace_team_member.
INSERT INTO org_team_member (team_id, user_id, joined_at, created_at, created_by)
SELECT
    wtm.team_id,
    wtm.user_id,
    wtm.created_at,
    wtm.created_at,
    'MIGRATION_V34'
FROM workspace_team_member wtm
ON CONFLICT (team_id, user_id) DO NOTHING;

-- Step 3: Insert org_team_workspace_assignment rows.
-- Each workspace_team maps to one workspace assignment with status ACTIVE.
INSERT INTO org_team_workspace_assignment (id, team_id, workspace_id, assigned_by,
                                           assigned_at, status, created_at, updated_at, created_by)
SELECT
    gen_random_uuid(),
    wt.id,
    wt.workspace_id,
    NULL,
    wt.created_at,
    'ACTIVE',
    NOW(),
    NOW(),
    'MIGRATION_V34'
FROM workspace_team wt
ON CONFLICT (team_id, workspace_id) DO NOTHING;
