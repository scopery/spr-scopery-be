-- Phase 44.5: Seed update_task_status tool policy and input schema

INSERT INTO ai_action_schema_definition (id, schema_code, schema_version, schema_json, status)
VALUES (
    gen_random_uuid(),
    'update_task_status_input',
    1,
    '{
      "type": "object",
      "properties": {
        "taskId": {
          "type": "string",
          "format": "uuid",
          "description": "The UUID of the task to update."
        },
        "newStatus": {
          "type": "string",
          "enum": ["TODO", "IN_PROGRESS", "DONE", "CANCELLED", "BLOCKED"],
          "description": "The new status to assign to the task."
        }
      },
      "required": ["taskId", "newStatus"]
    }',
    'ACTIVE'
);

INSERT INTO ai_action_tool_policy (
    id, tool_code, tool_version, invocation_scope, risk_level, execution_mode,
    max_batch_targets, dry_run_required, supports_compensation, supports_pause, status
)
VALUES (
    gen_random_uuid(),
    'update_task_status',
    'v1',
    'LLM_CALLABLE',
    'LOW',
    'CONFIRM_BEFORE_EXECUTE',
    1,
    false,
    false,
    false,
    'ACTIVE'
);
