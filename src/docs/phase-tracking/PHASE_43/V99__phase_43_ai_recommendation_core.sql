-- Phase 43 AI recommendation core persistence.
-- Repository baseline: V94; Phase 41 reserves V95/V96; Phase 42 reserves V97/V98.
-- Cross-module identifiers intentionally have no database foreign keys.

CREATE TABLE ai_recommendation_policy (
    id UUID PRIMARY KEY,
    code VARCHAR(120) NOT NULL,
    name VARCHAR(200) NOT NULL,
    description VARCHAR(1000) NULL,
    status VARCHAR(24) NOT NULL DEFAULT 'ACTIVE',
    scope_type VARCHAR(24) NOT NULL DEFAULT 'PROJECT',
    trigger_modes JSONB NOT NULL DEFAULT '["MANUAL"]'::jsonb,
    pack_codes JSONB NOT NULL DEFAULT '[]'::jsonb,
    llm_enrichment_enabled BOOLEAN NOT NULL DEFAULT FALSE,
    min_confidence NUMERIC(5,4) NOT NULL DEFAULT 0.4000,
    default_severity VARCHAR(16) NOT NULL DEFAULT 'WARNING',
    default_cooldown_minutes INTEGER NOT NULL DEFAULT 10080,
    max_suggestions_per_run INTEGER NOT NULL DEFAULT 100,
    publish_to_inbox BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL,
    created_by UUID NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    updated_by UUID NULL,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT uq_ai_rec_policy_code UNIQUE (code),
    CONSTRAINT ck_ai_rec_policy_status CHECK (status IN ('ACTIVE','INACTIVE','ARCHIVED')),
    CONSTRAINT ck_ai_rec_policy_scope CHECK (scope_type IN ('PROJECT')),
    CONSTRAINT ck_ai_rec_policy_trigger_modes CHECK (jsonb_typeof(trigger_modes) = 'array'),
    CONSTRAINT ck_ai_rec_policy_pack_codes CHECK (jsonb_typeof(pack_codes) = 'array'),
    CONSTRAINT ck_ai_rec_policy_conf CHECK (min_confidence >= 0.0000 AND min_confidence <= 1.0000),
    CONSTRAINT ck_ai_rec_policy_severity CHECK (default_severity IN ('INFO','WARNING','HIGH','CRITICAL')),
    CONSTRAINT ck_ai_rec_policy_cooldown CHECK (default_cooldown_minutes >= 0),
    CONSTRAINT ck_ai_rec_policy_limit CHECK (max_suggestions_per_run BETWEEN 1 AND 1000)
);

CREATE TABLE ai_recommendation_run (
    id UUID PRIMARY KEY,
    policy_id UUID NOT NULL REFERENCES ai_recommendation_policy(id),
    workspace_id UUID NOT NULL,
    project_id UUID NOT NULL,
    requested_by UUID NOT NULL,
    trigger_type VARCHAR(24) NOT NULL,
    idempotency_key VARCHAR(200) NOT NULL,
    request_hash CHAR(64) NOT NULL,
    status VARCHAR(24) NOT NULL DEFAULT 'PENDING',
    requested_pack_codes JSONB NOT NULL DEFAULT '[]'::jsonb,
    detector_codes JSONB NOT NULL DEFAULT '[]'::jsonb,
    context_snapshot_id UUID NULL,
    origin_conversation_id UUID NULL,
    origin_message_id UUID NULL,
    origin_turn_id UUID NULL,
    model_provider VARCHAR(64) NULL,
    model_name VARCHAR(160) NULL,
    prompt_profile_code VARCHAR(120) NULL,
    retrieval_trace_ids JSONB NOT NULL DEFAULT '[]'::jsonb,
    detector_count INTEGER NOT NULL DEFAULT 0,
    candidate_count INTEGER NOT NULL DEFAULT 0,
    persisted_count INTEGER NOT NULL DEFAULT 0,
    deduplicated_count INTEGER NOT NULL DEFAULT 0,
    suppressed_count INTEGER NOT NULL DEFAULT 0,
    discarded_count INTEGER NOT NULL DEFAULT 0,
    failed_detector_count INTEGER NOT NULL DEFAULT 0,
    input_token_count INTEGER NOT NULL DEFAULT 0,
    output_token_count INTEGER NOT NULL DEFAULT 0,
    latency_ms INTEGER NULL,
    error_code VARCHAR(100) NULL,
    error_summary_redacted VARCHAR(500) NULL,
    trace_id VARCHAR(128) NULL,
    correlation_id VARCHAR(128) NULL,
    created_at TIMESTAMPTZ NOT NULL,
    created_by UUID NULL,
    started_at TIMESTAMPTZ NULL,
    completed_at TIMESTAMPTZ NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    updated_by UUID NULL,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT ck_ai_rec_run_trigger CHECK (trigger_type IN ('MANUAL','CHAT','EVENT','SCHEDULED')),
    CONSTRAINT ck_ai_rec_run_status CHECK (status IN ('PENDING','RUNNING','PARTIAL','SUCCEEDED','FAILED','CANCELLED')),
    CONSTRAINT ck_ai_rec_run_pack_codes CHECK (jsonb_typeof(requested_pack_codes) = 'array'),
    CONSTRAINT ck_ai_rec_run_detector_codes CHECK (jsonb_typeof(detector_codes) = 'array'),
    CONSTRAINT ck_ai_rec_run_trace_ids CHECK (jsonb_typeof(retrieval_trace_ids) = 'array'),
    CONSTRAINT ck_ai_rec_run_counts CHECK (
        detector_count >= 0 AND candidate_count >= 0 AND persisted_count >= 0
        AND deduplicated_count >= 0 AND suppressed_count >= 0 AND discarded_count >= 0
        AND failed_detector_count >= 0 AND input_token_count >= 0 AND output_token_count >= 0
        AND (latency_ms IS NULL OR latency_ms >= 0)
    ),
    CONSTRAINT uq_ai_rec_run_idempotency UNIQUE (workspace_id, project_id, idempotency_key)
);

CREATE INDEX ix_ai_rec_run_project_created
    ON ai_recommendation_run(workspace_id, project_id, created_at DESC);
CREATE INDEX ix_ai_rec_run_status
    ON ai_recommendation_run(status, created_at);
CREATE INDEX ix_ai_rec_run_trace
    ON ai_recommendation_run(trace_id)
    WHERE trace_id IS NOT NULL;

CREATE TABLE ai_recommendation_suggestion (
    id UUID PRIMARY KEY,
    run_id UUID NULL REFERENCES ai_recommendation_run(id) ON DELETE SET NULL,
    policy_id UUID NULL REFERENCES ai_recommendation_policy(id) ON DELETE SET NULL,
    workspace_id UUID NOT NULL,
    project_id UUID NOT NULL,
    source_system VARCHAR(32) NOT NULL DEFAULT 'PHASE43',
    legacy_phase21_suggestion_id UUID NULL,
    pack_code VARCHAR(120) NOT NULL,
    detector_code VARCHAR(120) NOT NULL,
    suggestion_type VARCHAR(120) NOT NULL,
    schema_code VARCHAR(120) NOT NULL,
    schema_version INTEGER NOT NULL,
    category VARCHAR(48) NOT NULL,
    severity VARCHAR(16) NOT NULL,
    status VARCHAR(24) NOT NULL DEFAULT 'GENERATED',
    title VARCHAR(300) NOT NULL,
    summary VARCHAR(2000) NOT NULL,
    reason VARCHAR(4000) NOT NULL,
    target_entity_type VARCHAR(80) NOT NULL,
    target_entity_id UUID NOT NULL,
    target_version_token VARCHAR(200) NOT NULL,
    confidence_method VARCHAR(24) NOT NULL,
    confidence_value NUMERIC(5,4) NOT NULL,
    confidence_label VARCHAR(16) NOT NULL,
    risk_level VARCHAR(16) NOT NULL DEFAULT 'LOW',
    dedup_key VARCHAR(96) NOT NULL,
    payload_hash CHAR(64) NOT NULL,
    occurrence_count INTEGER NOT NULL DEFAULT 1,
    origin_conversation_id UUID NULL,
    origin_message_id UUID NULL,
    origin_turn_id UUID NULL,
    supersedes_suggestion_id UUID NULL REFERENCES ai_recommendation_suggestion(id) ON DELETE SET NULL,
    superseded_by_suggestion_id UUID NULL REFERENCES ai_recommendation_suggestion(id) ON DELETE SET NULL,
    first_observed_at TIMESTAMPTZ NOT NULL,
    last_observed_at TIMESTAMPTZ NOT NULL,
    viewed_at TIMESTAMPTZ NULL,
    edited_at TIMESTAMPTZ NULL,
    accepted_at TIMESTAMPTZ NULL,
    rejected_at TIMESTAMPTZ NULL,
    suppressed_at TIMESTAMPTZ NULL,
    expires_at TIMESTAMPTZ NOT NULL,
    stale_at TIMESTAMPTZ NULL,
    stale_reason_code VARCHAR(100) NULL,
    created_at TIMESTAMPTZ NOT NULL,
    created_by UUID NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    updated_by UUID NULL,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT ck_ai_rec_sug_source CHECK (source_system IN ('PHASE43','PHASE21')),
    CONSTRAINT ck_ai_rec_sug_legacy CHECK (
        (source_system = 'PHASE43' AND legacy_phase21_suggestion_id IS NULL)
        OR (source_system = 'PHASE21' AND legacy_phase21_suggestion_id IS NOT NULL)
    ),
    CONSTRAINT ck_ai_rec_sug_schema_version CHECK (schema_version > 0),
    CONSTRAINT ck_ai_rec_sug_severity CHECK (severity IN ('INFO','WARNING','HIGH','CRITICAL')),
    CONSTRAINT ck_ai_rec_sug_status CHECK (status IN (
        'GENERATED','VIEWED','EDITED','ACCEPTED','REJECTED','SUPPRESSED','EXPIRED','STALE','SUPERSEDED'
    )),
    CONSTRAINT ck_ai_rec_sug_conf_method CHECK (confidence_method IN ('DETERMINISTIC','HEURISTIC','LLM','LEGACY_MAPPED')),
    CONSTRAINT ck_ai_rec_sug_conf_value CHECK (confidence_value >= 0.0000 AND confidence_value <= 1.0000),
    CONSTRAINT ck_ai_rec_sug_conf_label CHECK (confidence_label IN ('LOW','MEDIUM','HIGH')),
    CONSTRAINT ck_ai_rec_sug_risk CHECK (risk_level IN ('LOW','MEDIUM','HIGH','CRITICAL')),
    CONSTRAINT ck_ai_rec_sug_dedup CHECK (dedup_key ~ '^rec:v1:sha256:[0-9a-f]{64}$'),
    CONSTRAINT ck_ai_rec_sug_payload_hash CHECK (payload_hash ~ '^[0-9a-f]{64}$'),
    CONSTRAINT ck_ai_rec_sug_occurrence CHECK (occurrence_count > 0),
    CONSTRAINT ck_ai_rec_sug_expiry CHECK (expires_at > first_observed_at),
    CONSTRAINT uq_ai_rec_sug_legacy UNIQUE (legacy_phase21_suggestion_id)
);

CREATE UNIQUE INDEX uq_ai_rec_sug_active_dedup
    ON ai_recommendation_suggestion(workspace_id, dedup_key)
    WHERE status IN ('GENERATED','VIEWED','EDITED','ACCEPTED');
CREATE INDEX ix_ai_rec_sug_project_status
    ON ai_recommendation_suggestion(workspace_id, project_id, status, severity, created_at DESC);
CREATE INDEX ix_ai_rec_sug_target
    ON ai_recommendation_suggestion(workspace_id, target_entity_type, target_entity_id, status);
CREATE INDEX ix_ai_rec_sug_expiry
    ON ai_recommendation_suggestion(status, expires_at);
CREATE INDEX ix_ai_rec_sug_origin_message
    ON ai_recommendation_suggestion(origin_message_id)
    WHERE origin_message_id IS NOT NULL;

CREATE TABLE ai_recommendation_suggestion_item (
    id UUID PRIMARY KEY,
    suggestion_id UUID NOT NULL REFERENCES ai_recommendation_suggestion(id) ON DELETE CASCADE,
    ordinal INTEGER NOT NULL,
    operation VARCHAR(24) NOT NULL,
    target_entity_type VARCHAR(80) NOT NULL,
    target_entity_id UUID NOT NULL,
    expected_target_version_token VARCHAR(200) NOT NULL,
    schema_code VARCHAR(120) NOT NULL,
    schema_version INTEGER NOT NULL,
    proposed_payload JSONB NOT NULL DEFAULT '{}'::jsonb,
    masked_before_snapshot JSONB NOT NULL DEFAULT '{}'::jsonb,
    payload_hash CHAR(64) NOT NULL,
    required_target_capability_code VARCHAR(120) NOT NULL,
    confirmation_required BOOLEAN NOT NULL DEFAULT TRUE,
    baseline_impact VARCHAR(24) NOT NULL DEFAULT 'NONE',
    created_at TIMESTAMPTZ NOT NULL,
    CONSTRAINT ck_ai_rec_item_ordinal CHECK (ordinal > 0),
    CONSTRAINT ck_ai_rec_item_operation CHECK (operation IN ('CREATE','UPDATE','LINK','UNLINK','NO_CHANGE_INSIGHT')),
    CONSTRAINT ck_ai_rec_item_schema_version CHECK (schema_version > 0),
    CONSTRAINT ck_ai_rec_item_payload CHECK (jsonb_typeof(proposed_payload) = 'object'),
    CONSTRAINT ck_ai_rec_item_before CHECK (jsonb_typeof(masked_before_snapshot) = 'object'),
    CONSTRAINT ck_ai_rec_item_hash CHECK (payload_hash ~ '^[0-9a-f]{64}$'),
    CONSTRAINT ck_ai_rec_item_baseline CHECK (baseline_impact IN ('NONE','POSSIBLE','REQUIRED','UNKNOWN')),
    CONSTRAINT uq_ai_rec_item_ordinal UNIQUE (suggestion_id, ordinal)
);

CREATE INDEX ix_ai_rec_item_target
    ON ai_recommendation_suggestion_item(target_entity_type, target_entity_id);

CREATE TABLE ai_recommendation_evidence (
    id UUID PRIMARY KEY,
    suggestion_id UUID NOT NULL REFERENCES ai_recommendation_suggestion(id) ON DELETE CASCADE,
    ordinal INTEGER NOT NULL,
    evidence_type VARCHAR(32) NOT NULL,
    support_strength VARCHAR(16) NOT NULL,
    aiassistant_citation_id UUID NULL,
    knowledge_chunk_id UUID NULL,
    retrieval_trace_id UUID NULL,
    source_type VARCHAR(80) NOT NULL,
    source_ref_id UUID NOT NULL,
    source_version_ref_id UUID NULL,
    field_path VARCHAR(500) NULL,
    title VARCHAR(500) NOT NULL,
    quoted_fragment VARCHAR(2000) NULL,
    app_route VARCHAR(1000) NULL,
    permission_signature VARCHAR(96) NOT NULL,
    access_validation_result VARCHAR(24) NOT NULL,
    access_validated_at TIMESTAMPTZ NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    CONSTRAINT ck_ai_rec_evi_ordinal CHECK (ordinal > 0),
    CONSTRAINT ck_ai_rec_evi_type CHECK (evidence_type IN ('CITATION','DOMAIN_FACT','LEGACY_SOURCE_REFERENCE')),
    CONSTRAINT ck_ai_rec_evi_strength CHECK (support_strength IN ('WEAK','MODERATE','STRONG','DIRECT')),
    CONSTRAINT ck_ai_rec_evi_perm CHECK (permission_signature ~ '^acl:v1:sha256:[0-9a-f]{64}$'),
    CONSTRAINT ck_ai_rec_evi_access CHECK (access_validation_result IN ('ALLOWED','REDACTED')),
    CONSTRAINT uq_ai_rec_evi_ordinal UNIQUE (suggestion_id, ordinal)
);

CREATE UNIQUE INDEX uq_ai_rec_evi_source
    ON ai_recommendation_evidence(
        suggestion_id, source_type, source_ref_id,
        COALESCE(source_version_ref_id, '00000000-0000-0000-0000-000000000000'::uuid),
        COALESCE(knowledge_chunk_id, '00000000-0000-0000-0000-000000000000'::uuid)
    );
CREATE INDEX ix_ai_rec_evi_source_ref
    ON ai_recommendation_evidence(source_type, source_ref_id, source_version_ref_id);

CREATE TABLE ai_recommendation_impact (
    id UUID PRIMARY KEY,
    suggestion_id UUID NOT NULL REFERENCES ai_recommendation_suggestion(id) ON DELETE CASCADE,
    dimension VARCHAR(32) NOT NULL,
    direction VARCHAR(16) NOT NULL,
    assessment_type VARCHAR(16) NOT NULL,
    numeric_value NUMERIC(20,6) NULL,
    unit_code VARCHAR(40) NULL,
    qualitative_magnitude VARCHAR(16) NOT NULL DEFAULT 'UNKNOWN',
    source_method VARCHAR(24) NOT NULL,
    calculation_method_code VARCHAR(120) NULL,
    assumptions JSONB NOT NULL DEFAULT '[]'::jsonb,
    source_type VARCHAR(80) NULL,
    source_ref_id UUID NULL,
    created_at TIMESTAMPTZ NOT NULL,
    CONSTRAINT ck_ai_rec_impact_dimension CHECK (dimension IN (
        'SCHEDULE','COST','REVENUE','MARGIN','QUALITY','RESOURCE','RISK','COMPLIANCE','CLIENT_VISIBILITY'
    )),
    CONSTRAINT ck_ai_rec_impact_direction CHECK (direction IN ('INCREASE','DECREASE','NEUTRAL','UNKNOWN')),
    CONSTRAINT ck_ai_rec_impact_type CHECK (assessment_type IN ('NUMERIC','QUALITATIVE','UNKNOWN')),
    CONSTRAINT ck_ai_rec_impact_magnitude CHECK (qualitative_magnitude IN ('LOW','MEDIUM','HIGH','UNKNOWN')),
    CONSTRAINT ck_ai_rec_impact_method CHECK (source_method IN ('DETERMINISTIC','DOMAIN_CALC','HEURISTIC','LLM','LEGACY_MAPPED')),
    CONSTRAINT ck_ai_rec_impact_assumptions CHECK (jsonb_typeof(assumptions) = 'array'),
    CONSTRAINT ck_ai_rec_impact_numeric CHECK (
        (assessment_type = 'NUMERIC' AND numeric_value IS NOT NULL AND unit_code IS NOT NULL
            AND source_method IN ('DETERMINISTIC','DOMAIN_CALC') AND source_ref_id IS NOT NULL)
        OR (assessment_type <> 'NUMERIC' AND numeric_value IS NULL)
    ),
    CONSTRAINT uq_ai_rec_impact_dimension UNIQUE (suggestion_id, dimension)
);

CREATE TABLE ai_recommendation_review (
    id UUID PRIMARY KEY,
    suggestion_id UUID NOT NULL REFERENCES ai_recommendation_suggestion(id) ON DELETE CASCADE,
    actor_id UUID NOT NULL,
    decision VARCHAR(24) NOT NULL,
    from_status VARCHAR(24) NOT NULL,
    to_status VARCHAR(24) NOT NULL,
    expected_suggestion_version BIGINT NOT NULL,
    reason_code VARCHAR(100) NULL,
    comment VARCHAR(2000) NULL,
    edited_items JSONB NOT NULL DEFAULT '[]'::jsonb,
    created_at TIMESTAMPTZ NOT NULL,
    trace_id VARCHAR(128) NULL,
    CONSTRAINT ck_ai_rec_review_decision CHECK (decision IN ('VIEW','EDIT','ACCEPT','REJECT','SUPPRESS')),
    CONSTRAINT ck_ai_rec_review_items CHECK (jsonb_typeof(edited_items) = 'array'),
    CONSTRAINT ck_ai_rec_review_version CHECK (expected_suggestion_version >= 0)
);

CREATE INDEX ix_ai_rec_review_suggestion
    ON ai_recommendation_review(suggestion_id, created_at DESC);

CREATE TABLE ai_recommendation_feedback (
    id UUID PRIMARY KEY,
    suggestion_id UUID NOT NULL REFERENCES ai_recommendation_suggestion(id) ON DELETE CASCADE,
    actor_id UUID NOT NULL,
    helpful BOOLEAN NULL,
    reason_code VARCHAR(100) NULL,
    comment VARCHAR(2000) NULL,
    observed_outcome VARCHAR(32) NULL,
    created_at TIMESTAMPTZ NOT NULL,
    CONSTRAINT ck_ai_rec_feedback_outcome CHECK (
        observed_outcome IS NULL OR observed_outcome IN ('UNKNOWN','HELPED','NO_EFFECT','HARMFUL','NOT_APPLICABLE')
    ),
    CONSTRAINT uq_ai_rec_feedback_actor UNIQUE (suggestion_id, actor_id)
);

CREATE TABLE ai_recommendation_suppression (
    id UUID PRIMARY KEY,
    workspace_id UUID NOT NULL,
    project_id UUID NOT NULL,
    actor_id UUID NOT NULL,
    scope_type VARCHAR(16) NOT NULL,
    scope_key VARCHAR(500) NOT NULL,
    suppression_key VARCHAR(99) NOT NULL,
    target_entity_type VARCHAR(80) NULL,
    target_entity_id UUID NULL,
    suggestion_type VARCHAR(120) NULL,
    pack_code VARCHAR(120) NULL,
    reason_code VARCHAR(100) NOT NULL,
    comment VARCHAR(1000) NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    starts_at TIMESTAMPTZ NOT NULL,
    expires_at TIMESTAMPTZ NOT NULL,
    revoked_at TIMESTAMPTZ NULL,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT ck_ai_rec_sup_scope CHECK (scope_type IN ('TARGET','TYPE','PACK')),
    CONSTRAINT ck_ai_rec_sup_key CHECK (suppression_key ~ '^recsup:v1:sha256:[0-9a-f]{64}$'),
    CONSTRAINT ck_ai_rec_sup_expiry CHECK (expires_at > starts_at AND expires_at <= starts_at + INTERVAL '90 days'),
    CONSTRAINT ck_ai_rec_sup_shape CHECK (
        (scope_type = 'TARGET' AND target_entity_type IS NOT NULL AND target_entity_id IS NOT NULL)
        OR (scope_type = 'TYPE' AND suggestion_type IS NOT NULL)
        OR (scope_type = 'PACK' AND pack_code IS NOT NULL)
    )
);

CREATE UNIQUE INDEX uq_ai_rec_sup_active
    ON ai_recommendation_suppression(workspace_id, project_id, actor_id, suppression_key)
    WHERE active = TRUE;
CREATE INDEX ix_ai_rec_sup_expiry
    ON ai_recommendation_suppression(active, expires_at);
