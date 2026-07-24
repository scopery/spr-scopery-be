-- Phase 44.5: Seed create_task tool policy and input schema

INSERT INTO ai_action_schema_definition (id, schema_code, schema_version, schema_json, status)
VALUES (
    gen_random_uuid(),
    'create_task_input',
    1,
    '{
      "type": "object",
      "properties": {
        "projectId": {
          "type": "string",
          "format": "uuid",
          "description": "The UUID of the project to create the task in."
        },
        "title": {
          "type": "string",
          "description": "The title of the task."
        },
        "description": {
          "type": "string",
          "description": "Optional description of the task."
        },
        "priority": {
          "type": "string",
          "enum": ["LOW", "MEDIUM", "HIGH", "CRITICAL"],
          "description": "Task priority. Defaults to MEDIUM if not provided."
        },
        "dueDate": {
          "type": "string",
          "format": "date",
          "description": "Due date in YYYY-MM-DD format. Optional."
        },
        "estimateHours": {
          "type": "number",
          "description": "Estimated effort in hours. Defaults to 1 if not provided."
        },
        "projectPhaseId": {
          "type": "string",
          "format": "uuid",
          "description": "UUID of the project phase. If omitted, the first active phase is used."
        }
      },
      "required": ["projectId", "title"]
    }',
    'ACTIVE'
);

INSERT INTO ai_action_tool_policy (
    id, tool_code, tool_version, invocation_scope, risk_level, execution_mode,
    max_batch_targets, dry_run_required, supports_compensation, supports_pause, status
)
VALUES (
    gen_random_uuid(),
    'create_task',
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
