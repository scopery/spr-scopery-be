-- Phase 42 contextual AI assistant core persistence.
-- Repository baseline: V94; Phase 41 reserves V95/V96.
-- Cross-module identifiers intentionally have no database foreign keys.

CREATE TABLE aiassistant_conversation (
    id UUID PRIMARY KEY,
    workspace_id UUID NOT NULL,
    project_id UUID NULL,
    owner_user_id UUID NOT NULL,
    conversation_type VARCHAR(32) NOT NULL,
    capability_level VARCHAR(32) NOT NULL,
    assistant_agent_id UUID NULL,
    status VARCHAR(24) NOT NULL DEFAULT 'ACTIVE',
    title VARCHAR(200) NOT NULL,
    title_source VARCHAR(16) NOT NULL DEFAULT 'AUTO',
    retention_policy_code VARCHAR(80) NOT NULL DEFAULT 'AI_ASSISTANT_DEFAULT_180D',
    last_message_at TIMESTAMPTZ NULL,
    last_memory_summary_version INTEGER NULL,
    archived_at TIMESTAMPTZ NULL,
    deleted_at TIMESTAMPTZ NULL,
    created_at TIMESTAMPTZ NOT NULL,
    created_by UUID NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    updated_by UUID NULL,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT ck_aiasst_conv_type CHECK (conversation_type IN ('GENERAL_GUIDE','PROJECT_ASSISTANT')),
    CONSTRAINT ck_aiasst_conv_level CHECK (capability_level IN ('GUIDE','CONTEXTUAL_ANSWER')),
    CONSTRAINT ck_aiasst_conv_status CHECK (status IN ('ACTIVE','ARCHIVED','DELETED')),
    CONSTRAINT ck_aiasst_conv_title_source CHECK (title_source IN ('AUTO','USER')),
    CONSTRAINT ck_aiasst_conv_project_scope CHECK (
        (conversation_type = 'GENERAL_GUIDE')
        OR (conversation_type = 'PROJECT_ASSISTANT' AND project_id IS NOT NULL)
    )
);

CREATE INDEX ix_aiasst_conv_owner_status
    ON aiassistant_conversation(workspace_id, owner_user_id, status, last_message_at DESC);
CREATE INDEX ix_aiasst_conv_project_status
    ON aiassistant_conversation(workspace_id, project_id, status, last_message_at DESC)
    WHERE project_id IS NOT NULL;
CREATE INDEX ix_aiasst_conv_retention
    ON aiassistant_conversation(status, last_message_at, deleted_at);

CREATE TABLE aiassistant_message (
    id UUID PRIMARY KEY,
    conversation_id UUID NOT NULL REFERENCES aiassistant_conversation(id) ON DELETE CASCADE,
    turn_id UUID NOT NULL,
    parent_message_id UUID NULL REFERENCES aiassistant_message(id) ON DELETE SET NULL,
    idempotency_key VARCHAR(200) NULL,
    sequence_in_conversation INTEGER NOT NULL,
    role VARCHAR(24) NOT NULL,
    status VARCHAR(32) NOT NULL,
    content_format VARCHAR(24) NOT NULL DEFAULT 'MARKDOWN',
    content TEXT NULL,
    response_mode VARCHAR(48) NULL,
    model_provider VARCHAR(64) NULL,
    model_name VARCHAR(160) NULL,
    model_deployment VARCHAR(160) NULL,
    prompt_profile_code VARCHAR(120) NULL,
    input_token_count INTEGER NOT NULL DEFAULT 0,
    output_token_count INTEGER NOT NULL DEFAULT 0,
    latency_ms INTEGER NULL,
    finish_reason VARCHAR(64) NULL,
    error_code VARCHAR(100) NULL,
    error_summary_redacted VARCHAR(500) NULL,
    trace_id VARCHAR(128) NULL,
    correlation_id VARCHAR(128) NULL,
    cancel_requested_at TIMESTAMPTZ NULL,
    cancel_requested_by UUID NULL,
    created_at TIMESTAMPTZ NOT NULL,
    created_by UUID NULL,
    started_at TIMESTAMPTZ NULL,
    completed_at TIMESTAMPTZ NULL,
    cancelled_at TIMESTAMPTZ NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    updated_by UUID NULL,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT ck_aiasst_msg_sequence CHECK (sequence_in_conversation > 0),
    CONSTRAINT ck_aiasst_msg_role CHECK (role IN ('SYSTEM','USER','ASSISTANT','TOOL_REQUEST','TOOL_RESULT')),
    CONSTRAINT ck_aiasst_msg_status CHECK (status IN (
        'RECEIVED','QUEUED','CONTEXTUALIZING','RETRIEVING','GENERATING','STREAMING',
        'CANCEL_REQUESTED','COMPLETED','FAILED','CANCELLED','BLOCKED'
    )),
    CONSTRAINT ck_aiasst_msg_format CHECK (content_format IN ('PLAIN_TEXT','MARKDOWN','JSON_SUMMARY')),
    CONSTRAINT ck_aiasst_msg_response_mode CHECK (response_mode IS NULL OR response_mode IN (
        'GENERAL_GUIDE','GROUNDED_ANSWER','CURRENT_PAGE_EXPLANATION','FIELD_EXPLANATION',
        'DISABLED_ACTION_EXPLANATION','TRACEABILITY_ANSWER','COMPARISON_SUMMARY',
        'INSUFFICIENT_EVIDENCE','ACCESS_RESTRICTED','OUT_OF_SCOPE'
    )),
    CONSTRAINT ck_aiasst_msg_tokens CHECK (input_token_count >= 0 AND output_token_count >= 0),
    CONSTRAINT ck_aiasst_msg_latency CHECK (latency_ms IS NULL OR latency_ms >= 0),
    CONSTRAINT uq_aiasst_msg_sequence UNIQUE (conversation_id, sequence_in_conversation)
);

CREATE INDEX ix_aiasst_msg_conv_created
    ON aiassistant_message(conversation_id, sequence_in_conversation);
CREATE UNIQUE INDEX uq_aiasst_msg_idempotency
    ON aiassistant_message(conversation_id, idempotency_key)
    WHERE idempotency_key IS NOT NULL;
CREATE INDEX ix_aiasst_msg_turn
    ON aiassistant_message(turn_id, role, created_at);
CREATE UNIQUE INDEX uq_aiasst_msg_turn_primary_roles
    ON aiassistant_message(conversation_id, turn_id, role)
    WHERE role IN ('USER','ASSISTANT');
CREATE INDEX ix_aiasst_msg_status
    ON aiassistant_message(status, created_at);
CREATE INDEX ix_aiasst_msg_trace
    ON aiassistant_message(trace_id)
    WHERE trace_id IS NOT NULL;

CREATE TABLE aiassistant_context_snapshot (
    id UUID PRIMARY KEY,
    conversation_id UUID NOT NULL REFERENCES aiassistant_conversation(id) ON DELETE CASCADE,
    assistant_message_id UUID NOT NULL REFERENCES aiassistant_message(id) ON DELETE CASCADE,
    turn_id UUID NOT NULL,
    actor_id UUID NOT NULL,
    workspace_id UUID NOT NULL,
    project_id UUID NULL,
    route VARCHAR(1000) NULL,
    page_code VARCHAR(160) NULL,
    page_metadata_version INTEGER NULL,
    entity_type VARCHAR(80) NULL,
    entity_id UUID NULL,
    entity_version BIGINT NULL,
    selected_action_code VARCHAR(160) NULL,
    tab_code VARCHAR(160) NULL,
    locale VARCHAR(20) NOT NULL DEFAULT 'en-US',
    timezone VARCHAR(80) NOT NULL DEFAULT 'UTC',
    client_context_version INTEGER NOT NULL DEFAULT 1,
    client_visible_field_codes JSONB NOT NULL DEFAULT '[]'::jsonb,
    client_reported_action_codes JSONB NOT NULL DEFAULT '[]'::jsonb,
    server_visible_field_codes JSONB NOT NULL DEFAULT '[]'::jsonb,
    available_action_codes JSONB NOT NULL DEFAULT '[]'::jsonb,
    disabled_action_reasons JSONB NOT NULL DEFAULT '{}'::jsonb,
    permission_signature VARCHAR(96) NOT NULL,
    client_context_hash VARCHAR(96) NULL,
    context_hash VARCHAR(96) NOT NULL,
    server_context JSONB NOT NULL DEFAULT '{}'::jsonb,
    context_status VARCHAR(24) NOT NULL DEFAULT 'VALID',
    invalidation_reason_code VARCHAR(100) NULL,
    created_at TIMESTAMPTZ NOT NULL,
    expires_at TIMESTAMPTZ NOT NULL,
    invalidated_at TIMESTAMPTZ NULL,
    CONSTRAINT ck_aiasst_ctx_arrays CHECK (
        jsonb_typeof(client_visible_field_codes) = 'array'
        AND jsonb_typeof(client_reported_action_codes) = 'array'
        AND jsonb_typeof(server_visible_field_codes) = 'array'
        AND jsonb_typeof(available_action_codes) = 'array'
    ),
    CONSTRAINT ck_aiasst_ctx_disabled CHECK (jsonb_typeof(disabled_action_reasons) = 'object'),
    CONSTRAINT ck_aiasst_ctx_server CHECK (jsonb_typeof(server_context) = 'object'),
    CONSTRAINT ck_aiasst_ctx_perm_sig CHECK (permission_signature ~ '^acl:v1:sha256:[0-9a-f]{64}$'),
    CONSTRAINT ck_aiasst_ctx_hash CHECK (context_hash ~ '^ctx:v1:sha256:[0-9a-f]{64}$'),
    CONSTRAINT ck_aiasst_ctx_client_hash CHECK (
        client_context_hash IS NULL OR client_context_hash ~ '^ctx:v1:sha256:[0-9a-f]{64}$'
    ),
    CONSTRAINT ck_aiasst_ctx_status CHECK (context_status IN ('VALID','REDACTED','INVALIDATED','EXPIRED')),
    CONSTRAINT uq_aiasst_ctx_assistant_message UNIQUE (assistant_message_id),
    CONSTRAINT uq_aiasst_ctx_turn UNIQUE (conversation_id, turn_id)
);

CREATE INDEX ix_aiasst_ctx_scope
    ON aiassistant_context_snapshot(workspace_id, project_id, actor_id, created_at DESC);
CREATE INDEX ix_aiasst_ctx_expiry
    ON aiassistant_context_snapshot(expires_at, context_status);

CREATE TABLE aiassistant_message_citation (
    id UUID PRIMARY KEY,
    message_id UUID NOT NULL REFERENCES aiassistant_message(id) ON DELETE CASCADE,
    ordinal INTEGER NOT NULL,
    retrieval_trace_id UUID NULL,
    knowledge_chunk_id UUID NULL,
    source_type VARCHAR(40) NOT NULL,
    source_ref_id UUID NOT NULL,
    source_version_ref_id UUID NULL,
    title VARCHAR(500) NOT NULL,
    heading_path JSONB NOT NULL DEFAULT '[]'::jsonb,
    quoted_fragment VARCHAR(2000) NULL,
    app_route VARCHAR(1000) NULL,
    permission_signature VARCHAR(96) NOT NULL,
    access_validation_result VARCHAR(24) NOT NULL,
    access_validated_at TIMESTAMPTZ NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    CONSTRAINT ck_aiasst_cit_ordinal CHECK (ordinal > 0),
    CONSTRAINT ck_aiasst_cit_heading CHECK (jsonb_typeof(heading_path) = 'array'),
    CONSTRAINT ck_aiasst_cit_source_type CHECK (source_type IN ('TASK','DOCUMENT_VERSION','MEETING_MINUTE')),
    CONSTRAINT ck_aiasst_cit_perm_sig CHECK (permission_signature ~ '^acl:v1:sha256:[0-9a-f]{64}$'),
    CONSTRAINT ck_aiasst_cit_access CHECK (access_validation_result IN ('ALLOWED','REDACTED')),
    CONSTRAINT uq_aiasst_cit_ordinal UNIQUE (message_id, ordinal)
);

CREATE UNIQUE INDEX uq_aiasst_cit_chunk
    ON aiassistant_message_citation(message_id, knowledge_chunk_id)
    WHERE knowledge_chunk_id IS NOT NULL;
CREATE INDEX ix_aiasst_cit_source
    ON aiassistant_message_citation(source_type, source_ref_id, source_version_ref_id);

CREATE TABLE aiassistant_tool_call (
    id UUID PRIMARY KEY,
    conversation_id UUID NOT NULL REFERENCES aiassistant_conversation(id) ON DELETE CASCADE,
    turn_id UUID NOT NULL,
    request_message_id UUID NOT NULL REFERENCES aiassistant_message(id) ON DELETE CASCADE,
    result_message_id UUID NULL REFERENCES aiassistant_message(id) ON DELETE SET NULL,
    tool_code VARCHAR(160) NOT NULL,
    tool_version VARCHAR(40) NOT NULL,
    handler_code VARCHAR(200) NOT NULL,
    status VARCHAR(24) NOT NULL,
    request_hash CHAR(64) NOT NULL,
    masked_arguments JSONB NOT NULL DEFAULT '{}'::jsonb,
    server_resolved_scope JSONB NOT NULL DEFAULT '{}'::jsonb,
    result_summary JSONB NOT NULL DEFAULT '{}'::jsonb,
    retrieval_trace_id UUID NULL,
    result_count INTEGER NOT NULL DEFAULT 0,
    truncated BOOLEAN NOT NULL DEFAULT FALSE,
    latency_ms INTEGER NULL,
    error_code VARCHAR(100) NULL,
    error_summary_redacted VARCHAR(500) NULL,
    created_at TIMESTAMPTZ NOT NULL,
    started_at TIMESTAMPTZ NULL,
    completed_at TIMESTAMPTZ NULL,
    CONSTRAINT ck_aiasst_tool_status CHECK (status IN ('REQUESTED','RUNNING','SUCCEEDED','FAILED','BLOCKED','CANCELLED')),
    CONSTRAINT ck_aiasst_tool_req_hash CHECK (request_hash ~ '^[0-9a-f]{64}$'),
    CONSTRAINT ck_aiasst_tool_json CHECK (
        jsonb_typeof(masked_arguments) = 'object'
        AND jsonb_typeof(server_resolved_scope) = 'object'
        AND jsonb_typeof(result_summary) = 'object'
    ),
    CONSTRAINT ck_aiasst_tool_count CHECK (result_count >= 0),
    CONSTRAINT ck_aiasst_tool_latency CHECK (latency_ms IS NULL OR latency_ms >= 0),
    CONSTRAINT uq_aiasst_tool_request_message UNIQUE (request_message_id)
);

CREATE INDEX ix_aiasst_tool_turn
    ON aiassistant_tool_call(conversation_id, turn_id, created_at);
CREATE INDEX ix_aiasst_tool_trace
    ON aiassistant_tool_call(retrieval_trace_id)
    WHERE retrieval_trace_id IS NOT NULL;

CREATE TABLE aiassistant_memory_summary (
    id UUID PRIMARY KEY,
    conversation_id UUID NOT NULL REFERENCES aiassistant_conversation(id) ON DELETE CASCADE,
    summary_version INTEGER NOT NULL,
    strategy_code VARCHAR(40) NOT NULL DEFAULT 'summary-v1',
    status VARCHAR(24) NOT NULL,
    covered_through_message_sequence INTEGER NOT NULL,
    source_message_count INTEGER NOT NULL,
    estimated_token_count INTEGER NOT NULL,
    summary_text TEXT NOT NULL,
    permission_signature VARCHAR(96) NOT NULL,
    summary_hash CHAR(64) NOT NULL,
    model_provider VARCHAR(64) NULL,
    model_name VARCHAR(160) NULL,
    prompt_profile_code VARCHAR(120) NULL,
    created_at TIMESTAMPTZ NOT NULL,
    created_by UUID NULL,
    invalidated_at TIMESTAMPTZ NULL,
    invalidation_reason_code VARCHAR(100) NULL,
    CONSTRAINT ck_aiasst_mem_version CHECK (summary_version > 0),
    CONSTRAINT ck_aiasst_mem_status CHECK (status IN ('ACTIVE','STALE','SUPERSEDED','REDACTED')),
    CONSTRAINT ck_aiasst_mem_counts CHECK (
        covered_through_message_sequence > 0
        AND source_message_count > 0
        AND estimated_token_count > 0
        AND estimated_token_count <= 2000
    ),
    CONSTRAINT ck_aiasst_mem_perm_sig CHECK (permission_signature ~ '^acl:v1:sha256:[0-9a-f]{64}$'),
    CONSTRAINT ck_aiasst_mem_hash CHECK (summary_hash ~ '^[0-9a-f]{64}$'),
    CONSTRAINT uq_aiasst_mem_version UNIQUE (conversation_id, summary_version)
);

CREATE UNIQUE INDEX uq_aiasst_mem_active
    ON aiassistant_memory_summary(conversation_id)
    WHERE status = 'ACTIVE';
CREATE INDEX ix_aiasst_mem_status
    ON aiassistant_memory_summary(status, created_at);

CREATE TABLE aiassistant_guide_definition (
    id UUID PRIMARY KEY,
    code VARCHAR(160) NOT NULL UNIQUE,
    page_code VARCHAR(160) NOT NULL,
    field_code VARCHAR(160) NULL,
    action_code VARCHAR(160) NULL,
    locale VARCHAR(20) NOT NULL,
    title VARCHAR(300) NOT NULL,
    body_markdown TEXT NOT NULL,
    metadata_version INTEGER NOT NULL,
    source_kind VARCHAR(32) NOT NULL DEFAULT 'REGISTERED_METADATA',
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMPTZ NOT NULL,
    created_by UUID NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    updated_by UUID NULL,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT ck_aiasst_guide_version CHECK (metadata_version > 0),
    CONSTRAINT ck_aiasst_guide_source CHECK (source_kind IN ('REGISTERED_METADATA','ADMIN_CURATED')),
    CONSTRAINT ck_aiasst_guide_status CHECK (status IN ('ACTIVE','INACTIVE','RETIRED'))
);

CREATE UNIQUE INDEX uq_aiasst_guide_scope
    ON aiassistant_guide_definition(
        page_code,
        COALESCE(field_code, ''),
        COALESCE(action_code, ''),
        locale,
        metadata_version
    );
CREATE INDEX ix_aiasst_guide_lookup
    ON aiassistant_guide_definition(page_code, locale, status);

CREATE TABLE aiassistant_suggested_question (
    id UUID PRIMARY KEY,
    code VARCHAR(160) NOT NULL UNIQUE,
    page_code VARCHAR(160) NOT NULL,
    entity_type VARCHAR(80) NULL,
    action_code VARCHAR(160) NULL,
    locale VARCHAR(20) NOT NULL,
    question_text VARCHAR(500) NOT NULL,
    display_order INTEGER NOT NULL DEFAULT 0,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMPTZ NOT NULL,
    created_by UUID NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    updated_by UUID NULL,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT ck_aiasst_suggest_order CHECK (display_order >= 0),
    CONSTRAINT ck_aiasst_suggest_status CHECK (status IN ('ACTIVE','INACTIVE','RETIRED'))
);

CREATE INDEX ix_aiasst_suggest_lookup
    ON aiassistant_suggested_question(page_code, entity_type, locale, status, display_order);

CREATE TABLE aiassistant_answer_feedback (
    id UUID PRIMARY KEY,
    conversation_id UUID NOT NULL REFERENCES aiassistant_conversation(id) ON DELETE CASCADE,
    message_id UUID NOT NULL REFERENCES aiassistant_message(id) ON DELETE CASCADE,
    actor_id UUID NOT NULL,
    rating VARCHAR(16) NOT NULL,
    reason_code VARCHAR(80) NULL,
    comment VARCHAR(2000) NULL,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT ck_aiasst_feedback_rating CHECK (rating IN ('UP','DOWN')),
    CONSTRAINT uq_aiasst_feedback_actor UNIQUE (message_id, actor_id)
);

CREATE INDEX ix_aiasst_feedback_conv
    ON aiassistant_answer_feedback(conversation_id, created_at DESC);
