-- Phase 41 semantic retrieval foundation. Repository baseline latest migration: V94.
-- External module reference columns intentionally have no cross-module foreign keys.

CREATE TABLE knowledge_source (
    id UUID PRIMARY KEY,
    workspace_id UUID NOT NULL,
    project_id UUID NULL,
    source_type VARCHAR(40) NOT NULL,
    source_ref_id UUID NOT NULL,
    source_version_ref_id UUID NOT NULL,
    title VARCHAR(500) NOT NULL,
    language VARCHAR(16) NOT NULL DEFAULT 'und',
    classification VARCHAR(32) NOT NULL DEFAULT 'INTERNAL',
    content_hash CHAR(64) NOT NULL,
    permission_signature VARCHAR(96) NOT NULL,
    acl_tokens JSONB NOT NULL DEFAULT '[]'::jsonb,
    source_status VARCHAR(32) NOT NULL,
    last_observed_at TIMESTAMPTZ NOT NULL,
    last_indexed_at TIMESTAMPTZ NULL,
    created_at TIMESTAMPTZ NOT NULL,
    created_by UUID NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    updated_by UUID NULL,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT ck_knowledge_source_type CHECK (source_type IN ('TASK','DOCUMENT_VERSION','MEETING_MINUTE')),
    CONSTRAINT ck_knowledge_source_classification CHECK (classification IN ('PUBLIC','INTERNAL','CONFIDENTIAL','RESTRICTED')),
    CONSTRAINT ck_knowledge_source_status CHECK (source_status IN ('DISCOVERED','PROJECTED','INDEXING','INDEXED','FAILED','INVALIDATED','DELETED')),
    CONSTRAINT ck_knowledge_source_content_hash CHECK (content_hash ~ '^[0-9a-f]{64}$'),
    CONSTRAINT ck_knowledge_source_permission_signature CHECK (permission_signature ~ '^acl:v1:sha256:[0-9a-f]{64}$'),
    CONSTRAINT ck_knowledge_source_acl_tokens_array CHECK (jsonb_typeof(acl_tokens) = 'array'),
    CONSTRAINT uq_knowledge_source_version UNIQUE (workspace_id, source_type, source_ref_id, source_version_ref_id)
);

CREATE INDEX ix_knowledge_source_workspace_project ON knowledge_source(workspace_id, project_id);
CREATE INDEX ix_knowledge_source_ref ON knowledge_source(source_type, source_ref_id);
CREATE INDEX ix_knowledge_source_status ON knowledge_source(source_status, last_observed_at);

CREATE TABLE knowledge_projection (
    id UUID PRIMARY KEY,
    source_id UUID NOT NULL REFERENCES knowledge_source(id) ON DELETE CASCADE,
    projection_version INTEGER NOT NULL,
    extractor_code VARCHAR(80) NOT NULL,
    extractor_version VARCHAR(40) NOT NULL,
    normalization_version VARCHAR(40) NOT NULL DEFAULT 'projection-v1',
    plain_text TEXT NOT NULL,
    structured_metadata JSONB NOT NULL DEFAULT '{}'::jsonb,
    heading_index JSONB NOT NULL DEFAULT '[]'::jsonb,
    content_hash CHAR(64) NOT NULL,
    projection_status VARCHAR(24) NOT NULL,
    failure_code VARCHAR(80) NULL,
    failure_message_redacted VARCHAR(500) NULL,
    created_at TIMESTAMPTZ NOT NULL,
    created_by UUID NULL,
    CONSTRAINT ck_knowledge_projection_version CHECK (projection_version > 0),
    CONSTRAINT ck_knowledge_projection_status CHECK (projection_status IN ('READY','FAILED','SUPERSEDED')),
    CONSTRAINT ck_knowledge_projection_hash CHECK (content_hash ~ '^[0-9a-f]{64}$'),
    CONSTRAINT uq_knowledge_projection_version UNIQUE (source_id, projection_version)
);

CREATE INDEX ix_knowledge_projection_source_status ON knowledge_projection(source_id, projection_status);

CREATE TABLE knowledge_chunk (
    id UUID PRIMARY KEY,
    source_id UUID NOT NULL REFERENCES knowledge_source(id) ON DELETE CASCADE,
    projection_id UUID NOT NULL REFERENCES knowledge_projection(id) ON DELETE CASCADE,
    chunk_ordinal INTEGER NOT NULL,
    strategy_version VARCHAR(40) NOT NULL DEFAULT 'chunk-v1',
    chunk_type VARCHAR(40) NOT NULL,
    heading_path JSONB NOT NULL DEFAULT '[]'::jsonb,
    plain_text TEXT NOT NULL,
    token_count INTEGER NOT NULL,
    start_code_point INTEGER NOT NULL,
    end_code_point INTEGER NOT NULL,
    content_hash CHAR(64) NOT NULL,
    metadata JSONB NOT NULL DEFAULT '{}'::jsonb,
    is_current BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL,
    CONSTRAINT ck_knowledge_chunk_ordinal CHECK (chunk_ordinal >= 0),
    CONSTRAINT ck_knowledge_chunk_token_count CHECK (token_count > 0 AND token_count <= 1000),
    CONSTRAINT ck_knowledge_chunk_offsets CHECK (start_code_point >= 0 AND end_code_point > start_code_point),
    CONSTRAINT ck_knowledge_chunk_hash CHECK (content_hash ~ '^[0-9a-f]{64}$'),
    CONSTRAINT uq_knowledge_chunk_ordinal UNIQUE (projection_id, chunk_ordinal)
);

CREATE INDEX ix_knowledge_chunk_source_current ON knowledge_chunk(source_id, is_current);
CREATE INDEX ix_knowledge_chunk_projection ON knowledge_chunk(projection_id, chunk_ordinal);

CREATE TABLE knowledge_embedding_profile (
    id UUID PRIMARY KEY,
    code VARCHAR(120) NOT NULL UNIQUE,
    provider VARCHAR(40) NOT NULL,
    model VARCHAR(120) NOT NULL,
    dimensions INTEGER NOT NULL,
    max_input_tokens INTEGER NOT NULL,
    distance_metric VARCHAR(20) NOT NULL,
    normalization VARCHAR(40) NOT NULL,
    profile_version INTEGER NOT NULL,
    status VARCHAR(20) NOT NULL,
    non_secret_config JSONB NOT NULL DEFAULT '{}'::jsonb,
    created_at TIMESTAMPTZ NOT NULL,
    created_by UUID NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    updated_by UUID NULL,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT ck_knowledge_embedding_dimensions CHECK (dimensions > 0 AND dimensions <= 4096),
    CONSTRAINT ck_knowledge_embedding_tokens CHECK (max_input_tokens > 0),
    CONSTRAINT ck_knowledge_embedding_metric CHECK (distance_metric = 'COSINE'),
    CONSTRAINT ck_knowledge_embedding_status CHECK (status IN ('ACTIVE','INACTIVE','RETIRED')),
    CONSTRAINT uq_knowledge_embedding_provider_model_version UNIQUE (provider, model, profile_version)
);


CREATE TABLE knowledge_index_definition (
    id UUID PRIMARY KEY,
    code VARCHAR(120) NOT NULL UNIQUE,
    environment VARCHAR(40) NOT NULL,
    index_family VARCHAR(120) NOT NULL,
    schema_version VARCHAR(20) NOT NULL,
    embedding_profile_id UUID NOT NULL REFERENCES knowledge_embedding_profile(id) ON DELETE RESTRICT,
    chunk_strategy_version VARCHAR(40) NOT NULL,
    read_alias VARCHAR(255) NOT NULL,
    write_alias VARCHAR(255) NOT NULL,
    active_generation VARCHAR(20) NULL,
    active_concrete_index VARCHAR(255) NULL,
    mapping_hash CHAR(64) NOT NULL,
    definition_status VARCHAR(24) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    created_by UUID NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    updated_by UUID NULL,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT ck_knowledge_index_definition_status CHECK (definition_status IN ('PLANNED','BUILDING','ACTIVE','FAILED','RETIRED')),
    CONSTRAINT ck_knowledge_index_definition_mapping_hash CHECK (mapping_hash ~ '^[0-9a-f]{64}$'),
    CONSTRAINT uq_knowledge_index_definition_aliases UNIQUE (environment, read_alias, write_alias)
);

CREATE INDEX ix_knowledge_index_definition_active ON knowledge_index_definition(environment, index_family, definition_status);

CREATE TABLE knowledge_index_job (
    id UUID PRIMARY KEY,
    workspace_id UUID NOT NULL,
    project_id UUID NULL,
    source_id UUID NULL REFERENCES knowledge_source(id) ON DELETE SET NULL,
    embedding_profile_id UUID NULL REFERENCES knowledge_embedding_profile(id) ON DELETE RESTRICT,
    job_type VARCHAR(40) NOT NULL,
    job_status VARCHAR(24) NOT NULL,
    idempotency_key VARCHAR(200) NOT NULL UNIQUE,
    target_index_name VARCHAR(255) NULL,
    attempt_count INTEGER NOT NULL DEFAULT 0,
    processed_count INTEGER NOT NULL DEFAULT 0,
    success_count INTEGER NOT NULL DEFAULT 0,
    failure_count INTEGER NOT NULL DEFAULT 0,
    error_code VARCHAR(80) NULL,
    error_message_redacted VARCHAR(500) NULL,
    queued_at TIMESTAMPTZ NOT NULL,
    started_at TIMESTAMPTZ NULL,
    completed_at TIMESTAMPTZ NULL,
    created_by UUID NULL,
    CONSTRAINT ck_knowledge_index_job_type CHECK (job_type IN ('SOURCE_INDEX','SOURCE_DELETE','PROJECT_REINDEX','INDEX_GENERATION_REBUILD','ACL_REFRESH')),
    CONSTRAINT ck_knowledge_index_job_status CHECK (job_status IN ('QUEUED','RUNNING','SUCCEEDED','PARTIALLY_SUCCEEDED','FAILED','CANCELLED')),
    CONSTRAINT ck_knowledge_index_job_counts CHECK (attempt_count >= 0 AND processed_count >= 0 AND success_count >= 0 AND failure_count >= 0)
);

CREATE INDEX ix_knowledge_index_job_status ON knowledge_index_job(job_status, queued_at);
CREATE INDEX ix_knowledge_index_job_scope ON knowledge_index_job(workspace_id, project_id, job_type);

CREATE TABLE knowledge_graph_node (
    id UUID PRIMARY KEY,
    workspace_id UUID NOT NULL,
    project_id UUID NULL,
    node_type VARCHAR(40) NOT NULL,
    source_ref_id UUID NOT NULL,
    source_version_ref_id UUID NULL,
    title VARCHAR(500) NOT NULL,
    permission_signature VARCHAR(96) NOT NULL,
    acl_tokens JSONB NOT NULL DEFAULT '[]'::jsonb,
    node_status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT ck_knowledge_graph_node_type CHECK (node_type IN ('PROJECT','TASK','DOCUMENT_VERSION','MEETING_MINUTE')),
    CONSTRAINT ck_knowledge_graph_node_status CHECK (node_status IN ('ACTIVE','INVALIDATED','DELETED')),
    CONSTRAINT ck_knowledge_graph_node_permission_signature CHECK (permission_signature ~ '^acl:v1:sha256:[0-9a-f]{64}$'),
    CONSTRAINT uq_knowledge_graph_node_ref UNIQUE (workspace_id, node_type, source_ref_id, source_version_ref_id)
);

CREATE INDEX ix_knowledge_graph_node_scope ON knowledge_graph_node(workspace_id, project_id, node_type);

CREATE TABLE knowledge_graph_edge (
    id UUID PRIMARY KEY,
    workspace_id UUID NOT NULL,
    project_id UUID NULL,
    from_node_id UUID NOT NULL REFERENCES knowledge_graph_node(id) ON DELETE CASCADE,
    to_node_id UUID NOT NULL REFERENCES knowledge_graph_node(id) ON DELETE CASCADE,
    edge_type VARCHAR(60) NOT NULL,
    source_ref_id UUID NULL,
    edge_status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT ck_knowledge_graph_edge_type CHECK (edge_type IN (
        'PROJECT_HAS_TASK','PROJECT_HAS_DOCUMENT_VERSION','PROJECT_HAS_MEETING_MINUTE',
        'TASK_DEPENDS_ON_TASK','MEETING_MINUTE_REFERENCES_TASK','DOCUMENT_VERSION_REFERENCES_TASK'
    )),
    CONSTRAINT ck_knowledge_graph_edge_status CHECK (edge_status IN ('ACTIVE','INVALIDATED','DELETED')),
    CONSTRAINT ck_knowledge_graph_edge_not_self CHECK (from_node_id <> to_node_id)
);

CREATE UNIQUE INDEX uq_knowledge_graph_edge
    ON knowledge_graph_edge (workspace_id, edge_type, from_node_id, to_node_id, COALESCE(source_ref_id, '00000000-0000-0000-0000-000000000000'::uuid));

CREATE INDEX ix_knowledge_graph_edge_from ON knowledge_graph_edge(from_node_id, edge_status);
CREATE INDEX ix_knowledge_graph_edge_to ON knowledge_graph_edge(to_node_id, edge_status);

CREATE TABLE knowledge_retrieval_trace (
    id UUID PRIMARY KEY,
    workspace_id UUID NOT NULL,
    project_id UUID NULL,
    actor_id UUID NOT NULL,
    query_hash CHAR(64) NOT NULL,
    retrieval_mode VARCHAR(32) NOT NULL,
    source_types JSONB NOT NULL DEFAULT '[]'::jsonb,
    filter_summary JSONB NOT NULL DEFAULT '{}'::jsonb,
    lexical_candidate_count INTEGER NOT NULL DEFAULT 0,
    vector_candidate_count INTEGER NOT NULL DEFAULT 0,
    graph_candidate_count INTEGER NOT NULL DEFAULT 0,
    returned_count INTEGER NOT NULL DEFAULT 0,
    duration_ms INTEGER NOT NULL,
    result_status VARCHAR(24) NOT NULL,
    error_code VARCHAR(80) NULL,
    created_at TIMESTAMPTZ NOT NULL,
    expires_at TIMESTAMPTZ NOT NULL,
    CONSTRAINT ck_knowledge_retrieval_query_hash CHECK (query_hash ~ '^[0-9a-f]{64}$'),
    CONSTRAINT ck_knowledge_retrieval_mode CHECK (retrieval_mode IN ('LEXICAL','VECTOR','HYBRID_RRF')),
    CONSTRAINT ck_knowledge_retrieval_counts CHECK (
        lexical_candidate_count >= 0 AND vector_candidate_count >= 0 AND graph_candidate_count >= 0 AND returned_count >= 0 AND duration_ms >= 0
    ),
    CONSTRAINT ck_knowledge_retrieval_status CHECK (result_status IN ('SUCCEEDED','BLOCKED','FAILED','INSUFFICIENT_RESULTS'))
);

CREATE INDEX ix_knowledge_retrieval_trace_scope_time ON knowledge_retrieval_trace(workspace_id, project_id, created_at DESC);
CREATE INDEX ix_knowledge_retrieval_trace_expiry ON knowledge_retrieval_trace(expires_at);

-- The application must calculate and persist the real SHA-256 mapping hash when creating knowledge_index_definition.

INSERT INTO knowledge_embedding_profile (
    id, code, provider, model, dimensions, max_input_tokens, distance_metric,
    normalization, profile_version, status, non_secret_config,
    created_at, created_by, updated_at, updated_by, version
) VALUES (
    '41000000-0000-0000-0000-000000000001',
    'OPENAI_TEXT_EMBEDDING_3_SMALL_1536_V1',
    'OPENAI',
    'text-embedding-3-small',
    1536,
    8191,
    'COSINE',
    'PROVIDER_NORMALIZED',
    1,
    'ACTIVE',
    '{"encoding":"cl100k_base"}'::jsonb,
    CURRENT_TIMESTAMP, NULL, CURRENT_TIMESTAMP, NULL, 0
) ON CONFLICT (code) DO NOTHING;
