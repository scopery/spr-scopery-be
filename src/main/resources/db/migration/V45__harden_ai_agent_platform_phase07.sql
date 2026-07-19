-- Phase 07: harden AI Agent platform (model capabilities, agent scope/autonomy,
-- prompt version metadata, execution log enrichment, usage policy limits, event config mapping)

-- ── aiagent_model: capability flags + token limits ───────────────────────────
ALTER TABLE aiagent_model
    ADD COLUMN IF NOT EXISTS supports_chat BOOLEAN NOT NULL DEFAULT false,
    ADD COLUMN IF NOT EXISTS supports_embedding BOOLEAN NOT NULL DEFAULT false,
    ADD COLUMN IF NOT EXISTS supports_tool_calling BOOLEAN NOT NULL DEFAULT false,
    ADD COLUMN IF NOT EXISTS supports_json_mode BOOLEAN NOT NULL DEFAULT false,
    ADD COLUMN IF NOT EXISTS context_window_tokens INT,
    ADD COLUMN IF NOT EXISTS max_output_tokens INT,
    ADD COLUMN IF NOT EXISTS model_family VARCHAR(100),
    ADD COLUMN IF NOT EXISTS capabilities_json JSONB;

-- ── aiagent_agent: autonomy + scope ──────────────────────────────────────────
ALTER TABLE aiagent_agent
    ADD COLUMN IF NOT EXISTS autonomy_level VARCHAR(100) NOT NULL DEFAULT 'SUGGEST_ONLY',
    ADD COLUMN IF NOT EXISTS scope VARCHAR(50) NOT NULL DEFAULT 'SYSTEM',
    ADD COLUMN IF NOT EXISTS organization_id UUID,
    ADD COLUMN IF NOT EXISTS workspace_id UUID;

ALTER TABLE aiagent_agent
    DROP CONSTRAINT IF EXISTS chk_aiagent_agent_autonomy_level;

ALTER TABLE aiagent_agent
    ADD CONSTRAINT chk_aiagent_agent_autonomy_level
        CHECK (autonomy_level IN (
            'SUGGEST_ONLY',
            'DRAFT_ONLY',
            'REQUIRES_APPROVAL',
            'AUTO_EXECUTE_READ_ONLY',
            'AUTO_EXECUTE_RESTRICTED'
        ));

ALTER TABLE aiagent_agent
    DROP CONSTRAINT IF EXISTS chk_aiagent_agent_scope;

ALTER TABLE aiagent_agent
    ADD CONSTRAINT chk_aiagent_agent_scope
        CHECK (scope IN ('SYSTEM', 'ORGANIZATION', 'WORKSPACE'));

CREATE INDEX IF NOT EXISTS idx_aiagent_agent_organization_id
    ON aiagent_agent (organization_id);

CREATE INDEX IF NOT EXISTS idx_aiagent_agent_workspace_id
    ON aiagent_agent (workspace_id);

-- ── aiagent_prompt_version: split prompts + activation metadata ──────────────
ALTER TABLE aiagent_prompt_version
    ADD COLUMN IF NOT EXISTS system_prompt TEXT,
    ADD COLUMN IF NOT EXISTS user_prompt_template TEXT,
    ADD COLUMN IF NOT EXISTS response_format VARCHAR(100),
    ADD COLUMN IF NOT EXISTS response_schema_json TEXT,
    ADD COLUMN IF NOT EXISTS temperature NUMERIC(5, 2),
    ADD COLUMN IF NOT EXISTS top_p NUMERIC(5, 2),
    ADD COLUMN IF NOT EXISTS max_tokens INT,
    ADD COLUMN IF NOT EXISTS activated_at TIMESTAMPTZ,
    ADD COLUMN IF NOT EXISTS activated_by VARCHAR(100);

UPDATE aiagent_prompt_version
SET user_prompt_template = content
WHERE user_prompt_template IS NULL
  AND content IS NOT NULL;

-- ── aiagent_execution_log: enrichment + BLOCKED status ───────────────────────
ALTER TABLE aiagent_execution_log
    ADD COLUMN IF NOT EXISTS prompt_template_id UUID,
    ADD COLUMN IF NOT EXISTS provider_id UUID,
    ADD COLUMN IF NOT EXISTS model_id UUID,
    ADD COLUMN IF NOT EXISTS environment VARCHAR(50),
    ADD COLUMN IF NOT EXISTS triggered_by_user_id UUID,
    ADD COLUMN IF NOT EXISTS input_hash VARCHAR(255),
    ADD COLUMN IF NOT EXISTS input_preview_json TEXT,
    ADD COLUMN IF NOT EXISTS output_preview_json TEXT,
    ADD COLUMN IF NOT EXISTS currency VARCHAR(10),
    ADD COLUMN IF NOT EXISTS trace_id VARCHAR(100),
    ADD COLUMN IF NOT EXISTS block_reason_code VARCHAR(150);

-- No prior status CHECK on execution_log; app enum includes BLOCKED.
-- Documented here for operators: status values =
-- PENDING | RUNNING | SUCCEEDED | FAILED | CANCELLED | BLOCKED

CREATE INDEX IF NOT EXISTS idx_aiagent_execution_log_trace_id
    ON aiagent_execution_log (trace_id);

CREATE INDEX IF NOT EXISTS idx_aiagent_execution_log_provider_id
    ON aiagent_execution_log (provider_id);

CREATE INDEX IF NOT EXISTS idx_aiagent_execution_log_model_id
    ON aiagent_execution_log (model_id);

-- ── aiagent_usage_policy: finer limits + event allow/block lists ─────────────
ALTER TABLE aiagent_usage_policy
    ADD COLUMN IF NOT EXISTS environment VARCHAR(50),
    ADD COLUMN IF NOT EXISTS max_requests_per_minute INT,
    ADD COLUMN IF NOT EXISTS max_requests_per_day INT,
    ADD COLUMN IF NOT EXISTS max_tokens_per_request INT,
    ADD COLUMN IF NOT EXISTS max_tokens_per_day INT,
    ADD COLUMN IF NOT EXISTS max_estimated_cost_per_day NUMERIC(18, 6),
    ADD COLUMN IF NOT EXISTS allowed_event_definition_ids TEXT,
    ADD COLUMN IF NOT EXISTS blocked_event_definition_ids TEXT;

ALTER TABLE aiagent_usage_policy
    DROP CONSTRAINT IF EXISTS chk_aiagent_usage_policy_has_limit;

ALTER TABLE aiagent_usage_policy
    ADD CONSTRAINT chk_aiagent_usage_policy_has_limit
        CHECK (
            max_requests_per_period IS NOT NULL OR
            max_tokens_per_period IS NOT NULL OR
            max_cost_per_period IS NOT NULL OR
            max_concurrent_requests IS NOT NULL OR
            daily_budget IS NOT NULL OR
            max_requests_per_minute IS NOT NULL OR
            max_requests_per_day IS NOT NULL OR
            max_tokens_per_request IS NOT NULL OR
            max_tokens_per_day IS NOT NULL OR
            max_estimated_cost_per_day IS NOT NULL
        );

-- ── aiagent_event_config: I/O mapping + activation audit ─────────────────────
ALTER TABLE aiagent_event_config
    ADD COLUMN IF NOT EXISTS input_mapping_json TEXT,
    ADD COLUMN IF NOT EXISTS output_mapping_json TEXT,
    ADD COLUMN IF NOT EXISTS activated_at TIMESTAMPTZ,
    ADD COLUMN IF NOT EXISTS activated_by VARCHAR(100),
    ADD COLUMN IF NOT EXISTS deactivated_at TIMESTAMPTZ,
    ADD COLUMN IF NOT EXISTS deactivated_by VARCHAR(100);
