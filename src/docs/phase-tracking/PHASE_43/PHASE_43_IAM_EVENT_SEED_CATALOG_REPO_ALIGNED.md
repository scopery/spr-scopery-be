# Phase 43 — IAM and Event Registry Seed Catalog — Repository-Aligned

> Status: **Accepted / implementation-blocking**  
> Mechanism: existing idempotent initializer/seeder APIs. Inspect real initializer interfaces and role constants before coding. Do not invent IAM/Event table names.

---

# 1. Initializer classes

```text
AiRecommendationPermissionInitializer
AiRecommendationEventDefinitionInitializer
AiRecommendationRegistryInitializer
```

Use the same ordering, transaction semantics, idempotency, source-system registration, and test pattern as existing initializers.

---

# 2. Authority codes

| Authority | Purpose | Scope |
|---|---|---|
| `AI_RECOMMENDATION_VIEW` | list/read authorized suggestions and evidence | project |
| `AI_RECOMMENDATION_GENERATE` | start manual recommendation runs | project |
| `AI_RECOMMENDATION_EDIT` | edit proposed payloads only | project |
| `AI_RECOMMENDATION_ACCEPT` | mark proposal accepted; no mutation | project |
| `AI_RECOMMENDATION_REJECT` | reject proposal | project |
| `AI_RECOMMENDATION_SUPPRESS` | create actor-scoped suppression | project |
| `AI_RECOMMENDATION_FEEDBACK_CREATE` | submit feedback | project |
| `AI_RECOMMENDATION_POLICY_VIEW` | view active policy/pack metadata | workspace/admin |
| `AI_RECOMMENDATION_POLICY_MANAGE` | manage policies in later admin scope | system/workspace admin |
| `AI_RECOMMENDATION_ANALYTICS_VIEW` | view aggregated acceptance/outcome metrics | workspace/admin |

Rules:

```text
- These authorities never grant Task/Meeting/Document/Finance access.
- Underlying target/evidence permissions are revalidated on every read/review.
- ACCEPT does not grant domain update authority.
- PREPARE_APPLY additionally requires Phase 44 and target-action authorization.
- POLICY_MANAGE is seeded for catalog stability; CRUD may be deferred if not in Phase 43 API.
```

Default grants must use real repository role constants. Semantic minimum:

```text
project participant → VIEW, GENERATE, FEEDBACK_CREATE
project manager/authorized planner → EDIT, ACCEPT, REJECT, SUPPRESS
workspace/system admin → POLICY_VIEW, ANALYTICS_VIEW as allowed
system/workspace admin only → POLICY_MANAGE
```

---

# 3. Source system

```text
SCOPERY_AI_RECOMMENDATION
```

---

# 4. Event definitions

| Code | eventKey | Required variables |
|---|---|---|
| `AI_RECOMMENDATION_RUN_REQUESTED` | `ai_recommendation.run_requested` | actor.userId, workspace.id, project.id, run.id, policy.code, pack.codes, trigger.type, occurredAt, traceId |
| `AI_RECOMMENDATION_RUN_STARTED` | `ai_recommendation.run_started` | workspace.id, project.id, run.id, detector.count, occurredAt, traceId |
| `AI_RECOMMENDATION_RUN_SUCCEEDED` | `ai_recommendation.run_succeeded` | workspace.id, project.id, run.id, persisted.count, deduplicated.count, suppressed.count, latency.ms, occurredAt, traceId |
| `AI_RECOMMENDATION_RUN_PARTIAL` | `ai_recommendation.run_partial` | workspace.id, project.id, run.id, persisted.count, failedDetector.count, error.codes, occurredAt, traceId |
| `AI_RECOMMENDATION_RUN_FAILED` | `ai_recommendation.run_failed` | workspace.id, project.id, run.id, error.code, retryable, occurredAt, traceId |
| `AI_SUGGESTION_GENERATED` | `ai_recommendation.suggestion_generated` | workspace.id, project.id, suggestion.id, suggestion.type, severity, target.type, target.id, confidence.label, occurredAt, traceId |
| `AI_SUGGESTION_DEDUPLICATED` | `ai_recommendation.suggestion_deduplicated` | workspace.id, project.id, suggestion.id, occurrence.count, occurredAt, traceId |
| `AI_SUGGESTION_VIEWED` | `ai_recommendation.suggestion_viewed` | actor.userId, workspace.id, project.id, suggestion.id, occurredAt, traceId |
| `AI_SUGGESTION_EDITED` | `ai_recommendation.suggestion_edited` | actor.userId, workspace.id, project.id, suggestion.id, item.count, occurredAt, traceId |
| `AI_SUGGESTION_ACCEPTED` | `ai_recommendation.suggestion_accepted` | actor.userId, workspace.id, project.id, suggestion.id, occurredAt, traceId |
| `AI_SUGGESTION_REJECTED` | `ai_recommendation.suggestion_rejected` | actor.userId, workspace.id, project.id, suggestion.id, reason.code, occurredAt, traceId |
| `AI_SUGGESTION_SUPPRESSED` | `ai_recommendation.suggestion_suppressed` | actor.userId, workspace.id, project.id, suggestion.id, suppression.id, scope.type, expiresAt, occurredAt, traceId |
| `AI_SUGGESTION_EXPIRED` | `ai_recommendation.suggestion_expired` | workspace.id, project.id, suggestion.id, occurredAt, traceId |
| `AI_SUGGESTION_STALE` | `ai_recommendation.suggestion_stale` | workspace.id, project.id, suggestion.id, reason.code, occurredAt, traceId |
| `AI_SUGGESTION_SUPERSEDED` | `ai_recommendation.suggestion_superseded` | workspace.id, project.id, suggestion.id, successor.id, occurredAt, traceId |
| `AI_SUGGESTION_READY_TO_APPLY` | `ai_recommendation.suggestion_ready_to_apply` | actor.userId, workspace.id, project.id, suggestion.id, actionRequest.id, actionPlan.id, occurredAt, traceId |
| `AI_SUGGESTION_FEEDBACK_SUBMITTED` | `ai_recommendation.suggestion_feedback_submitted` | actor.userId, workspace.id, project.id, suggestion.id, feedback.id, helpful, reason.code, occurredAt, traceId |

Event rules:

```text
- Cross-module delivery uses the existing outbox.
- READY_TO_APPLY emits only after a real Phase 44 plan is persisted.
- Do not emit one event per evidence row or LLM token.
- Suggestion/generated event commits after suggestion/items/evidence/impact are durable.
```

---

# 5. Forbidden event variables

Never include:

```text
raw proposed payload
raw before snapshot
raw source/chunk text
quoted evidence fragment
raw prompt or assistant response
provider request/response
financial/sensitive values
ACL tokens/signatures beyond permitted opaque identifiers
embedding vectors
presigned URLs/secrets
private chain-of-thought
```

Safe IDs, codes, counts, labels, versions, hashes, and redacted errors are allowed.

---

# 6. Work inbox / notification integration

Use the real existing adapter/publisher; do not create a duplicate inbox table.

MVP publication policy:

```text
publishToInbox=false by default
when enabled: HIGH/CRITICAL suggestions only
one inbox item per active suggestion
updates refresh existing item by suggestion ID
rejected/suppressed/expired/stale/superseded closes or resolves the item
```

Notification content must use title/summary already approved for the viewer and must not embed restricted evidence.

---

# 7. Required initializer tests

```text
permissionInitializer_firstRun_createsAuthorities
permissionInitializer_secondRun_noDuplicates
eventInitializer_firstRun_createsDefinitions
eventInitializer_secondRun_noDuplicates
registryInitializer_firstRun_createsLockedMvpCatalog
registryInitializer_secondRun_noDuplicates
readyToApplyEvent_notEmittedWithoutRealPhase44Plan
events_doNotExposePayloadOrEvidenceText
ordinaryRole_notGrantedPolicyManage
recommendationAuthority_doesNotBypassTargetAccess
```
