UPDATE ai_action_tool_policy
SET max_batch_targets = 20
WHERE invocation_scope = 'LLM_CALLABLE'
  AND max_batch_targets < 20;
