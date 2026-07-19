# Phase 44 — REST API Contracts — Repository-Aligned

> Status: **Accepted / implementation-blocking**  
> Base path: `/api/ai-actions`  
> Error handling: existing repository `ErrorResponse` / `AppException` only  
> Pagination: use the real shared page envelope.

---

# 1. Common error

```json
{
  "success": false,
  "errorCode": "AI_ACTION_PLAN_STALE",
  "message": "The action plan is stale and must be regenerated.",
  "traceId": "trace-id",
  "details": {}
}
```

---

# 2. List active tools

```http
GET /api/ai-actions/tools?invocationScope=PLAN_EXECUTION_ONLY&status=ACTIVE
```

Authority: `AI_TOOL_VIEW`.

Response:

```json
{
  "items": [
    {
      "toolCode": "task.assign",
      "toolVersion": "1",
      "invocationScope": "PLAN_EXECUTION_ONLY",
      "riskLevel": "MEDIUM",
      "executionMode": "CONFIRM_BEFORE_EXECUTE",
      "maxBatchTargets": 25,
      "dryRunRequired": true,
      "supportsCompensation": true,
      "status": "ACTIVE"
    }
  ]
}
```

```http
GET /api/ai-actions/tools/{toolCode}?version=1
```

Never returns adapter class internals, secrets, unrestricted schemas, or sensitive policy expressions to ordinary users.

---

# 3. Create action request

```http
POST /api/ai-actions/requests
```

Authority: `AI_ACTION_REQUEST` plus origin/target visibility.

Request:

```json
{
  "origin": {
    "type": "SUGGESTION",
    "conversationId": null,
    "messageId": null,
    "turnId": null,
    "suggestionRef": "p43:uuid"
  },
  "intentSummary": "Prepare the accepted recommendation for application.",
  "requestedActions": [],
  "clientContextHash": "ctx:v1:sha256:...",
  "idempotencyKey": "action-request-001"
}
```

For direct/chat requests, `requestedActions` contains 1..25 proposed calls:

```json
{
  "toolCode": "task.assign",
  "toolVersion": "1",
  "target": {"entityType": "TASK", "entityId": "uuid"},
  "arguments": {"assigneeUserId": "uuid"}
}
```

Validation:

```text
intentSummary 1..2000
requestedActions max 25
origin IDs all-null/all-present according to origin type
idempotencyKey 1..200
workspace/project/actor supplied by client are rejected or ignored per DTO design
```

Response `202`:

```json
{
  "requestId": "uuid",
  "status": "RECEIVED",
  "originType": "SUGGESTION",
  "links": {
    "self": "/api/ai-actions/requests/uuid",
    "plan": "/api/ai-actions/requests/uuid/plan"
  }
}
```

---

# 4. Get request

```http
GET /api/ai-actions/requests/{requestId}
```

Response:

```json
{
  "requestId": "uuid",
  "workspaceId": "uuid",
  "projectId": "uuid",
  "originType": "SUGGESTION",
  "originSuggestionRef": "p43:uuid",
  "status": "PLANNED",
  "intentSummary": "Prepare...",
  "createdAt": "2026-07-19T08:00:00Z",
  "latestPlanId": "uuid"
}
```

---

# 5. Build plan

```http
POST /api/ai-actions/requests/{requestId}/plan
```

Authority: `AI_ACTION_PLAN` plus target permissions.

Request:

```json
{
  "policyCode": "AI_ACTION_MVP_V1",
  "idempotencyKey": "plan-001"
}
```

Response `201`:

```json
{
  "planId": "uuid",
  "requestId": "uuid",
  "planNumber": 1,
  "status": "PREVIEW_READY",
  "planHash": "aplan:v1:sha256:...",
  "riskLevel": "MEDIUM",
  "executionMode": "CONFIRM_BEFORE_EXECUTE",
  "requiresConfirmation": true,
  "stepCount": 1,
  "targetCount": 1,
  "expiresAt": "2026-07-19T08:30:00Z"
}
```

Missing manual input returns `422 AI_ACTION_REQUIRED_INPUT_MISSING` with safe field names.

---

# 6. Get plan

```http
GET /api/ai-actions/plans/{planId}
```

Response:

```json
{
  "planId": "uuid",
  "requestId": "uuid",
  "status": "WAITING_CONFIRMATION",
  "planHash": "aplan:v1:sha256:...",
  "version": 4,
  "summary": "Assign one task.",
  "riskLevel": "MEDIUM",
  "executionMode": "CONFIRM_BEFORE_EXECUTE",
  "requiresConfirmation": true,
  "steps": [
    {
      "stepId": "uuid",
      "ordinal": 1,
      "toolCode": "task.assign",
      "toolVersion": "1",
      "target": {"entityType": "TASK", "entityId": "uuid"},
      "riskLevel": "MEDIUM",
      "status": "READY"
    }
  ],
  "preview": {
    "previewHash": "apreview:v1:sha256:...",
    "maskedDiff": [{"field":"assignee","before":"Unassigned","after":"User A"}],
    "warnings": [],
    "validUntil": "2026-07-19T08:15:00Z"
  }
}
```

---

# 7. Validate/regenerate preview

```http
POST /api/ai-actions/plans/{planId}/validate
POST /api/ai-actions/plans/{planId}/preview
```

Validate request:

```json
{"expectedPlanVersion":3,"planHash":"aplan:v1:sha256:..."}
```

Preview response:

```json
{
  "planId": "uuid",
  "status": "WAITING_CONFIRMATION",
  "previewHash": "apreview:v1:sha256:...",
  "sourceStateHash": "64hex",
  "maskedDiff": [],
  "warnings": [],
  "impact": {"baseline":"NONE","externalSideEffect":false},
  "validUntil": "2026-07-19T08:15:00Z"
}
```

---

# 8. Confirm or reject plan

```http
POST /api/ai-actions/plans/{planId}/confirm
```

Authority: `AI_ACTION_CONFIRM` plus current target authorization.

Request:

```json
{
  "planHash": "aplan:v1:sha256:...",
  "expectedPlanVersion": 4,
  "decision": "CONFIRM",
  "channel": "UI",
  "comment": "Proceed.",
  "idempotencyKey": "confirm-001"
}
```

Response:

```json
{
  "confirmationId": "uuid",
  "status": "CONFIRMED",
  "planId": "uuid",
  "planStatus": "CONFIRMED",
  "expiresAt": "2026-07-19T08:20:00Z"
}
```

`REJECT` moves the plan to `CANCELLED`.

---

# 9. Cancel plan

```http
POST /api/ai-actions/plans/{planId}/cancel
```

Request:

```json
{"expectedPlanVersion":4,"reasonCode":"USER_CANCELLED","idempotencyKey":"cancel-plan-001"}
```

Allowed only before execution starts.

---

# 10. Execute plan

```http
POST /api/ai-actions/plans/{planId}/execute
```

Authority: `AI_ACTION_EXECUTE`; `AI_ACTION_AUTO_EXECUTE` additionally required for auto mode.

Request:

```json
{
  "planHash": "aplan:v1:sha256:...",
  "expectedPlanVersion": 5,
  "confirmationId": "uuid",
  "idempotencyKey": "execute-001"
}
```

Response `202`:

```json
{
  "executionId": "uuid",
  "status": "QUEUED",
  "executionVersion": 0,
  "eventSequence": 1,
  "links": {
    "self": "/api/ai-actions/executions/uuid",
    "steps": "/api/ai-actions/executions/uuid/steps",
    "events": "/api/ai-actions/executions/uuid/events"
  }
}
```

---

# 11. Execution reads

```http
GET /api/ai-actions/executions/{executionId}
GET /api/ai-actions/executions/{executionId}/steps
```

Execution response:

```json
{
  "executionId": "uuid",
  "planId": "uuid",
  "status": "PARTIAL",
  "executionVersion": 12,
  "currentStepOrdinal": null,
  "counts": {"succeeded":3,"failed":1,"skipped":1,"compensated":0},
  "startedAt": "2026-07-19T08:01:00Z",
  "completedAt": "2026-07-19T08:01:03Z",
  "errorCode": "AI_ACTION_EXECUTION_PARTIAL"
}
```

Step list excludes raw input and unrestricted snapshots for ordinary viewers.

---

# 12. Durable events/replay

```http
GET /api/ai-actions/executions/{executionId}/events?afterSequence=10&limit=100
```

Response:

```json
{
  "executionId": "uuid",
  "events": [
    {
      "sequence": 11,
      "executionVersion": 8,
      "eventType": "step.completed",
      "occurredAt": "2026-07-19T08:01:02Z",
      "payload": {"stepId":"uuid","status":"SUCCEEDED"}
    }
  ],
  "latestSequence": 11,
  "hasMore": false
}
```

Limit 1..500.

---

# 13. Pause/resume/cancel execution

```http
POST /api/ai-actions/executions/{executionId}/pause
POST /api/ai-actions/executions/{executionId}/resume
POST /api/ai-actions/executions/{executionId}/cancel
```

Request:

```json
{
  "expectedExecutionVersion": 7,
  "idempotencyKey": "pause-001",
  "reasonCode": "USER_REQUEST"
}
```

Response:

```json
{
  "commandRecordId": "uuid",
  "commandType": "PAUSE",
  "status": "ACCEPTED",
  "executionId": "uuid"
}
```

---

# 14. Compensation

```http
POST /api/ai-actions/executions/{executionId}/compensate
```

Authority: `AI_ACTION_COMPENSATE` plus current domain update permissions.

Request:

```json
{
  "expectedExecutionVersion": 12,
  "mode": "ALL_SUPPORTED_SUCCEEDED_STEPS",
  "idempotencyKey": "compensate-001",
  "comment": "Revert supported changes."
}
```

Unsupported or unsafe steps are reported, never silently claimed as reverted.

---

# 15. History

```http
GET /api/ai-actions/history?projectId={uuid}&status=SUCCEEDED,PARTIAL&page=0&size=20
```

Authority: `AI_ACTION_HISTORY_VIEW`; results are filtered by current resource access.
