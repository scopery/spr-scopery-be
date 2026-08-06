-- NOTE: vector extension skipped for local dev (requires pgvector).
-- In production/Docker this file is replaced with the full version.

-- Add search + ACL columns to knowledge_chunk (IF NOT EXISTS for idempotency)
ALTER TABLE knowledge_chunk
    ADD COLUMN IF NOT EXISTS embedding      TEXT         NULL,
    ADD COLUMN IF NOT EXISTS search_vector  tsvector     NULL,
    ADD COLUMN IF NOT EXISTS title          TEXT         NULL,
    ADD COLUMN IF NOT EXISTS language       VARCHAR(20)  NULL,
    ADD COLUMN IF NOT EXISTS workspace_id   UUID         NULL,
    ADD COLUMN IF NOT EXISTS project_id     UUID         NULL,
    ADD COLUMN IF NOT EXISTS acl_tokens     TEXT[]       NULL,
    ADD COLUMN IF NOT EXISTS classification VARCHAR(50)  NULL,
    ADD COLUMN IF NOT EXISTS source_type    VARCHAR(50)  NULL,
    ADD COLUMN IF NOT EXISTS source_status  VARCHAR(50)  NULL,
    ADD COLUMN IF NOT EXISTS app_route      TEXT         NULL,
    ADD COLUMN IF NOT EXISTS indexed_at     TIMESTAMPTZ  NULL;

-- GIN index for full-text search
CREATE INDEX IF NOT EXISTS idx_knowledge_chunk_search_vector
    ON knowledge_chunk USING gin (search_vector)
    WHERE search_vector IS NOT NULL;

-- Composite filter index for workspace-scoped queries
CREATE INDEX IF NOT EXISTS idx_knowledge_chunk_workspace_current
    ON knowledge_chunk (workspace_id, is_current)
    WHERE is_current = true;

-- Filter index for project-scoped queries
CREATE INDEX IF NOT EXISTS idx_knowledge_chunk_project
    ON knowledge_chunk (project_id)
    WHERE project_id IS NOT NULL;
