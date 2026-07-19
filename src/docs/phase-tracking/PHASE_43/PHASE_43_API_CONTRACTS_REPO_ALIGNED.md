# Phase 43 — REST API Contracts — Repository-Aligned

> Status: **Accepted / implementation-blocking**  
> Base path: `/api/ai-recommendations`  
> Error handling: existing repository `ErrorResponse` / `AppException` only  
> Pagination: use the repository's existing page envelope and parameter names. Examples below use `items`, `page`, `size`, `totalElements`, `totalPages`; adapt field names only to the real shared DTO without changing semantics.

---

# 1. Common types

Suggestion reference:

```text
p43:<uuid>
p21:<uuid>
```

Common error:

```json
{
  "success": false,
  "errorCode": "AI_SUGGESTION_NOT_FOUND",
  "message": "Suggestion was not found.",
  "traceId": "trace-id",
  "details": {}
}
```

Suggestion summary:

```json
{
  "suggestionRef": "p43:uuid",
  "sourceSystem": "PHASE43",
  "projectId": "uuid",
  "type": "TASK_MISSING_OWNER",
  "category": "PLANNING",
  "severity": "WARNING",
  "status": "GENERATED",
  "title": "Assign an owner to Task A",
  "summary": "The active task has no owner.",
  "target": {
    "entityType": "TASK",
    "entityId": "uuid",
    "versionToken": "TASK:v1:42",
    "appRoute": "/projects/uuid/tasks/uuid"
  },
  "confidence": {
    "method": "DETERMINISTIC",
    "value": 1.0,
    "label": "HIGH"
  },
  "riskLevel": "LOW",
  "occurrenceCount": 1,
  "createdAt": "2026-07-17T12:00:00Z",
  "expiresAt": "2026-08-16T12:00:00Z",
  "version": 0
}
```

---

# 2. Create recommendation run

```http
POST /api/ai-recommendations/projects/{projectId}/runs
```

Authority: `AI_RECOMMENDATION_GENERATE` plus project/source access.

Request:

```json
{
  "policyCode": "PROJECT_RECOMMENDATION_MVP_V1",
  "packCodes": ["TASK_PLANNING_HYGIENE_V1", "MEETING_FOLLOW_UP_HYGIENE_V1"],
  "triggerType": "MANUAL",
  "idempotencyKey": "rec-run-20260717-001",
  "origin": {
    "conversationId": null,
    "messageId": null,
    "turnId": null
  }
}
```

Validation:

```text
projectId required
policyCode max 120
packCodes 1..10, unique, ACTIVE whitelist only
triggerType must be MANUAL in MVP
idempotencyKey 1..200
origin IDs all null or all present and authorized
```

Response `202 Accepted`:

```json
{
  "runId": "uuid",
  "status": "PENDING",
  "projectId": "uuid",
  "policyCode": "PROJECT_RECOMMENDATION_MVP_V1",
  "packCodes": ["TASK_PLANNING_HYGIENE_V1"],
  "links": {
    "self": "/api/ai-recommendations/runs/uuid"
  }
}
```

Same idempotency key + same request returns the existing response. Different request returns repository idempotency conflict.

---

# 3. Get run

```http
GET /api/ai-recommendations/runs/{runId}
```

Response:

```json
{
  "runId": "uuid",
  "workspaceId": "uuid",
  "projectId": "uuid",
  "status": "SUCCEEDED",
  "triggerType": "MANUAL",
  "policyCode": "PROJECT_RECOMMENDATION_MVP_V1",
  "packCodes": ["TASK_PLANNING_HYGIENE_V1"],
  "counts": {
    "detectors": 3,
    "candidates": 5,
    "persisted": 4,
    "deduplicated": 1,
    "suppressed": 0,
    "discarded": 0,
    "failedDetectors": 0
  },
  "startedAt": "2026-07-17T12:00:00Z",
  "completedAt": "2026-07-17T12:00:01Z",
  "latencyMs": 950,
  "error": null,
  "traceId": "trace-id"
}
```

---

# 4. List project suggestions

```http
GET /api/ai-recommendations/projects/{projectId}/suggestions
```

Query:

```text
status=GENERATED,VIEWED
severity=WARNING,HIGH
packCode=TASK_PLANNING_HYGIENE_V1
type=TASK_MISSING_OWNER
targetEntityType=TASK
includeLegacyPhase21=true
includeExpired=false
page=0
size=20
sort=createdAt,desc
```

Response:

```json
{
  "items": [{"suggestionRef": "p43:uuid", "type": "TASK_MISSING_OWNER", "status": "GENERATED"}],
  "page": 0,
  "size": 20,
  "totalElements": 4,
  "totalPages": 1
}
```

Maximum page size: 100.

---

# 5. List entity suggestions

```http
GET /api/ai-recommendations/entities/{entityType}/{entityId}/suggestions?projectId={projectId}
```

`projectId` is required in MVP to prevent ambiguous cross-project entity references.

Response uses the same page envelope.

---

# 6. Get suggestion detail

```http
GET /api/ai-recommendations/suggestions/{suggestionRef}
```

Response:

```json
{
  "suggestion": {"suggestionRef": "p43:uuid", "type": "TASK_MISSING_OWNER", "status": "GENERATED"},
  "reason": "The task is active and the canonical assignee field is empty.",
  "items": [
    {
      "itemId": "uuid",
      "ordinal": 1,
      "operation": "UPDATE",
      "targetEntityType": "TASK",
      "targetEntityId": "uuid",
      "schemaCode": "TASK_MISSING_OWNER",
      "schemaVersion": 1,
      "proposedPayload": {
        "candidateUserId": null,
        "assignmentReasonCode": "MANUAL_SELECTION_REQUIRED"
      },
      "requiredTargetCapabilityCode": "TARGET_TASK_UPDATE",
      "confirmationRequired": true,
      "baselineImpact": "POSSIBLE"
    }
  ],
  "evidence": [
    {
      "evidenceId": "uuid",
      "ordinal": 1,
      "evidenceType": "DOMAIN_FACT",
      "supportStrength": "DIRECT",
      "sourceType": "TASK",
      "sourceRefId": "uuid",
      "title": "Task A",
      "fragment": "Owner: not assigned",
      "appRoute": "/projects/uuid/tasks/uuid",
      "accessValidationResult": "ALLOWED"
    }
  ],
  "impacts": [
    {
      "dimension": "RESOURCE",
      "direction": "UNKNOWN",
      "assessmentType": "QUALITATIVE",
      "qualitativeMagnitude": "MEDIUM",
      "sourceMethod": "DETERMINISTIC"
    }
  ],
  "nextBestActions": [
    {"code": "OPEN_TARGET", "enabled": true},
    {"code": "ACCEPT_SUGGESTION", "enabled": true},
    {"code": "PREPARE_APPLY", "enabled": false, "disabledReasonCode": "PHASE_44_UNAVAILABLE"}
  ],
  "reviews": [],
  "legacyMetadata": null
}
```

Read revalidates target/evidence access. Restricted evidence is omitted/redacted; suggestion access does not grant source access.

---

# 7. Mark viewed

```http
POST /api/ai-recommendations/suggestions/{suggestionRef}/view
```

Request:

```json
{
  "expectedVersion": 0,
  "idempotencyKey": "view-key"
}
```

Response:

```json
{
  "suggestionRef": "p43:uuid",
  "status": "VIEWED",
  "viewedAt": "2026-07-17T12:10:00Z",
  "version": 1
}
```

Repeated view is idempotent.

---

# 8. Edit proposal

```http
PATCH /api/ai-recommendations/suggestions/{suggestionRef}/edit
```

Authority: `AI_RECOMMENDATION_EDIT`.

Request:

```json
{
  "expectedVersion": 1,
  "idempotencyKey": "edit-key",
  "items": [
    {
      "itemId": "uuid",
      "proposedPayload": {
        "candidateUserId": "uuid",
        "candidateDisplayName": "User A",
        "assignmentReasonCode": "MANUAL_SELECTION_REQUIRED"
      }
    }
  ],
  "comment": "Selected after reviewing team ownership."
}
```

Rules:

```text
all item IDs belong to suggestion
payload validates against existing schema version
mask before persist
original item/history retained through review record/audit
no target mutation
```

Response returns updated detail with status `EDITED`.

---

# 9. Accept

```http
POST /api/ai-recommendations/suggestions/{suggestionRef}/accept
```

Request:

```json
{
  "expectedVersion": 2,
  "idempotencyKey": "accept-key",
  "comment": "Proposal reviewed and accepted."
}
```

Response:

```json
{
  "suggestionRef": "p43:uuid",
  "status": "ACCEPTED",
  "acceptedAt": "2026-07-17T12:15:00Z",
  "domainMutationPerformed": false,
  "version": 3
}
```

---

# 10. Reject

```http
POST /api/ai-recommendations/suggestions/{suggestionRef}/reject
```

Request:

```json
{
  "expectedVersion": 1,
  "idempotencyKey": "reject-key",
  "reasonCode": "NOT_RELEVANT",
  "comment": "Owner is intentionally assigned later."
}
```

Response status `REJECTED`; no domain mutation.

---

# 11. Suppress

```http
POST /api/ai-recommendations/suggestions/{suggestionRef}/suppress
```

Request:

```json
{
  "expectedVersion": 1,
  "idempotencyKey": "suppress-key",
  "scopeType": "TARGET",
  "durationDays": 30,
  "reasonCode": "KNOWN_EXCEPTION",
  "comment": "Do not suggest this for the current task during the release window."
}
```

Validation:

```text
durationDays 1..90
scopeType TARGET|TYPE|PACK
critical nonSuppressible definitions rejected
legacy p21 unsupported in MVP
```

Response:

```json
{
  "suggestionRef": "p43:uuid",
  "status": "SUPPRESSED",
  "suppressionId": "uuid",
  "scopeType": "TARGET",
  "expiresAt": "2026-08-16T12:20:00Z",
  "version": 2
}
```

---

# 12. Feedback

```http
POST /api/ai-recommendations/suggestions/{suggestionRef}/feedback
```

Request:

```json
{
  "helpful": true,
  "reasonCode": "ACTIONABLE",
  "comment": "The missing owner warning was useful.",
  "observedOutcome": "HELPED"
}
```

Response:

```json
{
  "feedbackId": "uuid",
  "suggestionRef": "p43:uuid",
  "createdAt": "2026-07-17T12:30:00Z"
}
```

One current feedback row per actor/suggestion; repeat updates according to repository upsert convention or returns existing idempotently.

---

# 13. Prepare apply

```http
POST /api/ai-recommendations/suggestions/{suggestionRef}/prepare-apply
```

Request:

```json
{
  "expectedVersion": 3,
  "selectedItemIds": ["uuid"],
  "idempotencyKey": "prepare-key"
}
```

Before Phase 44: HTTP 409 with `AI_SUGGESTION_PREPARE_APPLY_UNAVAILABLE` as defined in the handoff artifact.

When Phase 44 is active: returns a real action request/plan reference; never returns a fabricated token.

---

# 14. Next-best actions

```http
GET /api/ai-recommendations/projects/{projectId}/next-best-actions
```

Query:

```text
suggestionRef=p43:uuid (optional)
entityType=TASK (optional)
entityId=uuid (optional)
limit=20
```

Response:

```json
{
  "items": [
    {
      "code": "OPEN_TARGET",
      "label": "Open task",
      "actionKind": "NAVIGATE",
      "enabled": true,
      "riskLevel": "LOW",
      "requiredAuthorityCode": "AI_RECOMMENDATION_VIEW",
      "phase44ToolCode": null,
      "metadata": {"route": "/projects/uuid/tasks/uuid"}
    }
  ]
}
```

---

# 15. Error catalog

```text
AI_RECOMMENDATION_POLICY_NOT_FOUND
AI_RECOMMENDATION_POLICY_INACTIVE
AI_RECOMMENDATION_PACK_NOT_FOUND
AI_RECOMMENDATION_PACK_INACTIVE
AI_RECOMMENDATION_RUN_NOT_FOUND
AI_RECOMMENDATION_RUN_FAILED
AI_RECOMMENDATION_CONTEXT_ACCESS_DENIED
AI_SUGGESTION_REFERENCE_INVALID
AI_SUGGESTION_NOT_FOUND
AI_SUGGESTION_ACCESS_DENIED
AI_SUGGESTION_INVALID_STATUS
AI_SUGGESTION_VERSION_CONFLICT
AI_SUGGESTION_STALE
AI_SUGGESTION_SCHEMA_INVALID
AI_SUGGESTION_TARGET_NOT_FOUND
AI_SUGGESTION_EVIDENCE_MISSING
AI_SUGGESTION_EVIDENCE_ACCESS_DENIED
AI_SUGGESTION_DUPLICATE
AI_SUGGESTION_SUPPRESSION_FORBIDDEN
AI_SUGGESTION_PREPARE_APPLY_UNAVAILABLE
AI_SUGGESTION_IMPACT_UNVERIFIED
AI_LEGACY_SUGGESTION_EDIT_UNAVAILABLE
AI_LEGACY_SUGGESTION_SUPPRESSION_UNAVAILABLE
```
