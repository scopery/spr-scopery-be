-- Seed missing LLM-callable tool policies for create_requirement and create_scope_item.
-- These adapters existed but were never registered, causing the LLM to never see them as available tools.

INSERT INTO ai_action_tool_policy (
    id, tool_code, tool_version, invocation_scope, risk_level, execution_mode,
    max_batch_targets, dry_run_required, supports_compensation, supports_pause, status
)
SELECT
    gen_random_uuid(),
    'create_requirement',
    'v1',
    'LLM_CALLABLE',
    'MEDIUM',
    'CONFIRM_BEFORE_EXECUTE',
    20,
    false,
    false,
    false,
    'ACTIVE'
WHERE NOT EXISTS (
    SELECT 1 FROM ai_action_tool_policy WHERE tool_code = 'create_requirement' AND tool_version = 'v1'
);

INSERT INTO ai_action_tool_policy (
    id, tool_code, tool_version, invocation_scope, risk_level, execution_mode,
    max_batch_targets, dry_run_required, supports_compensation, supports_pause, status
)
SELECT
    gen_random_uuid(),
    'create_scope_item',
    'v1',
    'LLM_CALLABLE',
    'MEDIUM',
    'CONFIRM_BEFORE_EXECUTE',
    20,
    false,
    false,
    false,
    'ACTIVE'
WHERE NOT EXISTS (
    SELECT 1 FROM ai_action_tool_policy WHERE tool_code = 'create_scope_item' AND tool_version = 'v1'
);
