-- Step 1: Enable pgvector extension
CREATE EXTENSION IF NOT EXISTS vector;

-- Step 2: Add search + ACL columns to knowledge_chunk
ALTER TABLE knowledge_chunk
    ADD COLUMN embedding      vector(1536) NULL,
    ADD COLUMN search_vector  tsvector     NULL,
    ADD COLUMN title          TEXT         NULL,
    ADD COLUMN language       VARCHAR(20)  NULL,
    ADD COLUMN workspace_id   UUID         NULL,
    ADD COLUMN project_id     UUID         NULL,
    ADD COLUMN acl_tokens     TEXT[]       NULL,
    ADD COLUMN classification VARCHAR(50)  NULL,
    ADD COLUMN source_type    VARCHAR(50)  NULL,
    ADD COLUMN source_status  VARCHAR(50)  NULL,
    ADD COLUMN app_route      TEXT         NULL,
    ADD COLUMN indexed_at     TIMESTAMPTZ  NULL;

-- Step 3: HNSW index for ANN vector search (cosine distance)
CREATE INDEX idx_knowledge_chunk_embedding
    ON knowledge_chunk USING hnsw (embedding vector_cosine_ops)
    WHERE embedding IS NOT NULL;

-- Step 4: GIN index for full-text search
CREATE INDEX idx_knowledge_chunk_search_vector
    ON knowledge_chunk USING gin (search_vector)
    WHERE search_vector IS NOT NULL;

-- Step 5: Composite filter index for workspace-scoped queries
CREATE INDEX idx_knowledge_chunk_workspace_current
    ON knowledge_chunk (workspace_id, is_current)
    WHERE is_current = true;

-- Step 6: Filter index for project-scoped queries
CREATE INDEX idx_knowledge_chunk_project
    ON knowledge_chunk (project_id)
    WHERE project_id IS NOT NULL;
