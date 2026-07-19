# Phase 44 — Action Plan & Tool JSON Schema Registry — Repository-Aligned

> Status: **Accepted / implementation-blocking**  
> JSON Schema draft: 2020-12  
> Persistence: `ai_action_schema_definition`

---

# 1. Common target schema

```json
{
  "$schema": "https://json-schema.org/draft/2020-12/schema",
  "$id": "scopery://ai-action/common-target/1",
  "type": "object",
  "additionalProperties": false,
  "properties": {
    "entityType": {"type": "string", "maxLength": 80},
    "entityId": {"type": "string", "format": "uuid"},
    "expectedVersionToken": {"type": "string", "minLength": 1, "maxLength": 200}
  },
  "required": ["entityType", "entityId", "expectedVersionToken"]
}
```

`expectedVersionToken` is always server-resolved or server-verified. A client/model value never becomes authoritative.

---

# 2. `TASK_CREATE_V1_INPUT`

```json
{
  "$schema": "https://json-schema.org/draft/2020-12/schema",
  "$id": "scopery://ai-action/task.create/input/1",
  "type": "object",
  "additionalProperties": false,
  "properties": {
    "projectId": {"type": "string", "format": "uuid"},
    "wbsNodeId": {"type": ["string", "null"], "format": "uuid"},
    "title": {"type": "string", "minLength": 1, "maxLength": 300},
    "description": {"type": ["string", "null"], "maxLength": 10000},
    "assigneeUserId": {"type": ["string", "null"], "format": "uuid"},
    "dueDate": {"type": ["string", "null"], "format": "date"},
    "estimateValue": {"type": ["number", "null"], "minimum": 0},
    "estimateUnit": {"type": ["string", "null"], "enum": ["HOUR", "DAY", "POINT", null]}
  },
  "required": ["projectId", "title"]
}
```

---

# 3. `TASK_ASSIGN_V1_INPUT`

```json
{
  "$schema": "https://json-schema.org/draft/2020-12/schema",
  "$id": "scopery://ai-action/task.assign/input/1",
  "type": "object",
  "additionalProperties": false,
  "properties": {
    "taskId": {"type": "string", "format": "uuid"},
    "assigneeUserId": {"type": "string", "format": "uuid"},
    "expectedVersionToken": {"type": "string", "maxLength": 200}
  },
  "required": ["taskId", "assigneeUserId", "expectedVersionToken"]
}
```

---

# 4. `TASK_ESTIMATE_UPDATE_V1_INPUT`

```json
{
  "$schema": "https://json-schema.org/draft/2020-12/schema",
  "$id": "scopery://ai-action/task.estimate.update/input/1",
  "type": "object",
  "additionalProperties": false,
  "properties": {
    "taskId": {"type": "string", "format": "uuid"},
    "estimateValue": {"type": "number", "minimum": 0},
    "estimateUnit": {"type": "string", "enum": ["HOUR", "DAY", "POINT"]},
    "expectedVersionToken": {"type": "string", "maxLength": 200}
  },
  "required": ["taskId", "estimateValue", "estimateUnit", "expectedVersionToken"]
}
```

---

# 5. `TASK_MITIGATION_UPDATE_V1_INPUT`

```json
{
  "$schema": "https://json-schema.org/draft/2020-12/schema",
  "$id": "scopery://ai-action/task.mitigation.update/input/1",
  "type": "object",
  "additionalProperties": false,
  "properties": {
    "taskId": {"type": "string", "format": "uuid"},
    "mitigationText": {"type": "string", "minLength": 1, "maxLength": 2000},
    "reviewDate": {"type": ["string", "null"], "format": "date"},
    "ownerUserId": {"type": ["string", "null"], "format": "uuid"},
    "expectedVersionToken": {"type": "string", "maxLength": 200}
  },
  "required": ["taskId", "mitigationText", "expectedVersionToken"]
}
```

---

# 6. `MEETING_ACTION_ASSIGN_V1_INPUT`

```json
{
  "$schema": "https://json-schema.org/draft/2020-12/schema",
  "$id": "scopery://ai-action/meeting.action.assign/input/1",
  "type": "object",
  "additionalProperties": false,
  "properties": {
    "actionItemId": {"type": "string", "format": "uuid"},
    "assigneeUserId": {"type": "string", "format": "uuid"},
    "expectedVersionToken": {"type": "string", "maxLength": 200}
  },
  "required": ["actionItemId", "assigneeUserId", "expectedVersionToken"]
}
```

---

# 7. `MEETING_ACTION_DUE_DATE_V1_INPUT`

```json
{
  "$schema": "https://json-schema.org/draft/2020-12/schema",
  "$id": "scopery://ai-action/meeting.action.due-date.update/input/1",
  "type": "object",
  "additionalProperties": false,
  "properties": {
    "actionItemId": {"type": "string", "format": "uuid"},
    "dueDate": {"type": "string", "format": "date"},
    "expectedVersionToken": {"type": "string", "maxLength": 200}
  },
  "required": ["actionItemId", "dueDate", "expectedVersionToken"]
}
```

---

# 8. Safe tool output schema

All mutation adapters return:

```json
{
  "$schema": "https://json-schema.org/draft/2020-12/schema",
  "$id": "scopery://ai-action/common-safe-result/1",
  "type": "object",
  "additionalProperties": false,
  "properties": {
    "status": {"type": "string", "enum": ["SUCCEEDED", "FAILED", "SKIPPED"]},
    "targetEntityType": {"type": ["string", "null"], "maxLength": 80},
    "targetEntityId": {"type": ["string", "null"], "format": "uuid"},
    "resultVersionToken": {"type": ["string", "null"], "maxLength": 200},
    "domainResultRef": {"type": ["string", "null"], "maxLength": 300},
    "warnings": {"type": "array", "maxItems": 20, "items": {"type": "string", "maxLength": 500}}
  },
  "required": ["status", "warnings"]
}
```

---

# 9. Plan canonicalization

Canonical plan-hash input:

```json
{
  "requestId": "uuid",
  "planNumber": 1,
  "policy": {"code": "AI_ACTION_MVP_V1", "version": 1},
  "contextHash": "ctx:v1:sha256:...",
  "sourceStateHash": "64hex",
  "steps": [
    {
      "ordinal": 1,
      "toolCode": "task.assign",
      "toolVersion": "1",
      "inputSchemaCode": "TASK_ASSIGN_V1_INPUT",
      "inputSchemaVersion": 1,
      "inputHash": "64hex",
      "expectedTargetVersionToken": "TASK:v1:42",
      "riskLevel": "MEDIUM",
      "executionMode": "CONFIRM_BEFORE_EXECUTE"
    }
  ]
}
```

Canonical JSON uses UTF-8, sorted object keys, preserved array order, normalized numbers, and no insignificant whitespace. Hash:

```text
aplan:v1:sha256:<sha256(canonical-json)>
```

Unknown fields, server-only fields, secrets, timestamps, and presentation labels are excluded.
