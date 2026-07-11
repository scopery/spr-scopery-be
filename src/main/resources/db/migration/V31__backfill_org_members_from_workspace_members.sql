-- ============================================================
-- V31: Backfill org_member from workspace_member
-- For each distinct (organization_id, user_id) pair found
-- across workspace_member rows, insert one org_member record.
-- Owner of the organization gets membership_type = OWNER;
-- everyone else gets MEMBER.
-- ============================================================

INSERT INTO org_member (id, organization_id, user_id, membership_type, status,
                        joined_at, created_at, updated_at, created_by)
SELECT
    gen_random_uuid()                              AS id,
    ww.organization_id,
    wm.user_id,
    CASE
        WHEN wm.user_id = wo.owner_user_id THEN 'OWNER'
        ELSE 'MEMBER'
    END                                            AS membership_type,
    wm.status,
    MIN(wm.joined_at)                              AS joined_at,
    NOW()                                          AS created_at,
    NOW()                                          AS updated_at,
    'MIGRATION_V31'                                AS created_by
FROM workspace_member wm
         JOIN workspace_workspace ww ON ww.id = wm.workspace_id
         JOIN workspace_organization wo ON wo.id = ww.organization_id
GROUP BY ww.organization_id, wm.user_id, wo.owner_user_id, wm.status
ON CONFLICT (organization_id, user_id) DO NOTHING;
