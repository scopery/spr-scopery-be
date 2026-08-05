-- Backfill COVERS trace links for all existing requirement→function junction entries
-- that do not already have an active COVERS link in traceability_link.
-- Needed because ApplyMappingDraftAction previously only wrote to app_requirement_function
-- without creating the corresponding trace link, causing the sidebar to show 0 functions.

INSERT INTO traceability_link (
    id,
    project_id,
    source_type,
    source_id,
    target_type,
    target_id,
    link_type,
    status,
    version,
    created_at,
    created_by,
    source_code,
    source_title,
    target_code,
    target_title
)
SELECT
    gen_random_uuid(),
    r.project_id,
    'REQUIREMENT',
    arf.requirement_id,
    'FUNCTIONAL_ITEM',
    arf.function_id,
    'COVERS',
    'ACTIVE',
    0,
    NOW(),
    'SYSTEM',
    r.code,
    r.title,
    fi.code,
    fi.title
FROM app_requirement_function arf
INNER JOIN requirements_requirement r  ON r.id  = arf.requirement_id
INNER JOIN app_functional_item     fi ON fi.id = arf.function_id
WHERE NOT EXISTS (
    SELECT 1
    FROM traceability_link tl
    WHERE tl.source_type = 'REQUIREMENT'
      AND tl.source_id   = arf.requirement_id
      AND tl.target_type = 'FUNCTIONAL_ITEM'
      AND tl.target_id   = arf.function_id
      AND tl.link_type   = 'COVERS'
      AND tl.status      = 'ACTIVE'
);
