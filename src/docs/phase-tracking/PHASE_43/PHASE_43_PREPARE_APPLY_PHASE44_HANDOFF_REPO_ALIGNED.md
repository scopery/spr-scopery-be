# Phase 43 — Prepare-Apply / Phase 44 Handoff Contract — Repository-Aligned

> Status: **Accepted / implementation-blocking**  
> Boundary: Phase 43 reviews proposals; Phase 44 prepares and executes domain actions.

---

# 1. Port and reserved tool

```text
Application port: RecommendationApplyPreparationPort
Reserved Phase 44 AiTool code: agent.action.prepare
Minimum tool version: 1
```

Phase 43 has no direct dependency on a Phase 44 implementation package. The port has two adapters:

```text
UnavailableRecommendationApplyPreparationAdapter (Phase 43 default)
AiToolRecommendationApplyPreparationAdapter (activated when Phase 44 tool exists)
```

---

# 2. Eligibility

Prepare-apply requires:

```text
suggestion sourceSystem = PHASE43
status = ACCEPTED
not expired/stale/superseded
all selected items schema-valid
current actor has AI_RECOMMENDATION_ACCEPT plus target review access
current target version tokens match
all evidence/citations currently accessible or safely redacted without invalidating rationale
idempotency key present
```

Legacy `p21:` suggestions return unavailable until a dedicated Phase 44 compatibility adapter exists.

---

# 3. Request to Phase 44 port

```json
{
  "suggestionRef": "p43:uuid",
  "suggestionVersion": 4,
  "workspaceId": "uuid",
  "projectId": "uuid",
  "actorId": "uuid",
  "selectedItemIds": ["uuid"],
  "targetVersionTokens": {
    "TASK:uuid": "TASK:v1:42"
  },
  "idempotencyKey": "client-generated-key",
  "originConversationId": "uuid-or-null",
  "originMessageId": "uuid-or-null",
  "traceId": "trace-id"
}
```

The port loads canonical items server-side. Client-supplied proposed payloads are ignored.

---

# 4. Response when Phase 44 is available

```json
{
  "available": true,
  "suggestionRef": "p43:uuid",
  "actionRequestId": "uuid",
  "actionPlanId": "uuid",
  "planStatus": "PREVIEW_READY",
  "confirmationRequired": true,
  "expiresAt": "2026-07-17T13:30:00Z",
  "links": {
    "plan": "/api/ai-actions/plans/uuid"
  }
}
```

No Phase 43 status is changed to APPLIED. Phase 44 owns plan/execution status.

---

# 5. Response before Phase 44 exists

```text
HTTP 409
errorCode: AI_SUGGESTION_PREPARE_APPLY_UNAVAILABLE
```

```json
{
  "success": false,
  "errorCode": "AI_SUGGESTION_PREPARE_APPLY_UNAVAILABLE",
  "message": "Action-plan preparation is not available until Phase 44 is enabled.",
  "traceId": "trace-id",
  "details": {
    "suggestionRef": "p43:uuid",
    "requiredCapability": "PHASE_44_AGENTIC_ACTIONS",
    "reservedToolCode": "agent.action.prepare",
    "suggestionStatus": "ACCEPTED"
  }
}
```

Rules:

```text
- no fake token/plan is generated;
- no target mutation occurs;
- suggestion stays ACCEPTED;
- an operational metric may be incremented;
- do not emit AI_SUGGESTION_READY_TO_APPLY until a real plan exists.
```

---

# 6. Idempotency

```text
scope: workspace + actor + suggestion + idempotencyKey
same key/same canonical request → same actionRequest/actionPlan response
same key/different canonical request → IDEMPOTENCY_CONFLICT
```

Phase 44 owns durable idempotency storage once active. The unavailable adapter does not reserve the key.

---

# 7. Next-best-action mapping

| Action code | Phase 43 behavior | Phase 44 tool |
|---|---|---|
| `OPEN_TARGET` | navigate only | none |
| `VIEW_EVIDENCE` | read only | none |
| `EDIT_PROPOSAL` | edit suggestion items | none |
| `ACCEPT_SUGGESTION` | review state only | none |
| `REJECT_SUGGESTION` | review state only | none |
| `SUPPRESS_SUGGESTION` | create suppression | none |
| `PREPARE_APPLY` | invoke port when eligible | `agent.action.prepare` v1 |

---

# 8. Tests

```text
prepareApply_unavailableBeforePhase44_returnsControlled409
prepareApply_unavailable_doesNotChangeSuggestionStatus
prepareApply_rejectsNonAcceptedSuggestion
prepareApply_revalidatesTargetVersion
prepareApply_usesServerStoredPayload
prepareApply_sameIdempotencyReturnsSamePlan_whenPhase44Active
legacyPhase21PrepareApply_unavailableWithoutAdapter
readyToApplyEvent_emittedOnlyForRealPlan
```
