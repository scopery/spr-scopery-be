# Phase 44 — IAM and Event Registry Seed Catalog — Repository-Aligned

> Status: **Accepted / implementation-blocking**  
> Mechanism: existing idempotent initializer/seeder APIs. Inspect real initializer interfaces and role constants before coding; do not invent IAM/Event table names.

---

# 1. Initializer classes

```text
AiActionPermissionInitializer
AiActionEventDefinitionInitializer
AiActionRegistryInitializer
```

Use existing ordering, transaction, idempotency, source-system registration, and test conventions.

---

# 2. Authority codes

| Authority | Purpose | Scope |
|---|---|---|
| `AI_TOOL_VIEW` | view safe active tool metadata | project/workspace |
| `AI_TOOL_MANAGE` | manage action tool policy/catalog in future admin APIs | system/workspace admin |
| `AI_ACTION_REQUEST` | create action requests | project |
| `AI_ACTION_PLAN` | build/read plans | project |
| `AI_ACTION_PREVIEW` | generate/read previews | project |
| `AI_ACTION_CONFIRM` | confirm/reject plans | project |
| `AI_ACTION_EXECUTE` | queue confirmed plans | project |
| `AI_ACTION_AUTO_EXECUTE` | allow eligible low-risk auto execution | tightly controlled |
| `AI_ACTION_HISTORY_VIEW` | read authorized executions/events | project/workspace |
| `AI_ACTION_CONTROL` | pause/resume/cancel execution | project |
| `AI_ACTION_COMPENSATE` | request supported compensation | project/admin |
| `AI_ACT_ON_BEHALF` | permit agent acting-on-behalf trace | tightly controlled |
| `AI_ACTION_POLICY_VIEW` | view action policy metadata | workspace/admin |
| `AI_ACTION_POLICY_MANAGE` | manage policy in future admin APIs | system/workspace admin |

Rules:

```text
- Phase 44 authorities never grant underlying task/meeting/project permissions.
- CONFIRM and EXECUTE both revalidate target domain permissions.
- AUTO_EXECUTE is not granted to ordinary roles by default.
- TOOL_MANAGE/POLICY_MANAGE are seeded for catalog stability; CRUD may remain deferred.
- AI_ACT_ON_BEHALF never changes the human actor recorded for authorization/audit.
```

Default grants must use real repository role constants. Semantic minimum:

```text
project participant → TOOL_VIEW, REQUEST, PLAN, PREVIEW, HISTORY_VIEW when underlying access exists
project manager/authorized operator → CONFIRM, EXECUTE, CONTROL
workspace/system admin → POLICY_VIEW
restricted admin only → COMPENSATE, AUTO_EXECUTE, TOOL_MANAGE, POLICY_MANAGE, ACT_ON_BEHALF
```

---

# 3. Source system

```text
SCOPERY_AI_ACTION
```

---

# 4. Event definitions

| Code | eventKey | Safe required variables |
|---|---|---|
| `AI_ACTION_REQUEST_CREATED` | `ai_action.request_created` | actor.userId, workspace.id, project.id?, request.id, origin.type, occurredAt, traceId |
| `AI_ACTION_PLAN_CREATED` | `ai_action.plan_created` | actor.userId, request.id, plan.id, risk.level, execution.mode, step.count, target.count, occurredAt, traceId |
| `AI_ACTION_PLAN_VALIDATED` | `ai_action.plan_validated` | plan.id, plan.hash, warning.count, occurredAt, traceId |
| `AI_ACTION_PLAN_INVALIDATED` | `ai_action.plan_invalidated` | plan.id, reason.code, occurredAt, traceId |
| `AI_ACTION_PREVIEW_GENERATED` | `ai_action.preview_generated` | plan.id, preview.id, warning.count, occurredAt, traceId |
| `AI_ACTION_CONFIRMATION_REQUESTED` | `ai_action.confirmation_requested` | plan.id, risk.level, expiresAt, occurredAt, traceId |
| `AI_ACTION_PLAN_CONFIRMED` | `ai_action.plan_confirmed` | actor.userId, plan.id, confirmation.id, channel, occurredAt, traceId |
| `AI_ACTION_PLAN_CANCELLED` | `ai_action.plan_cancelled` | actor.userId, plan.id, reason.code, occurredAt, traceId |
| `AI_ACTION_EXECUTION_QUEUED` | `ai_action.execution_queued` | actor.userId, execution.id, plan.id, occurredAt, traceId |
| `AI_ACTION_EXECUTION_STARTED` | `ai_action.execution_started` | execution.id, step.count, occurredAt, traceId |
| `AI_ACTION_STEP_STARTED` | `ai_action.step_started` | execution.id, step.id, step.ordinal, tool.code, target.type?, target.id?, occurredAt, traceId |
| `AI_ACTION_STEP_SUCCEEDED` | `ai_action.step_succeeded` | execution.id, step.id, tool.code, target.type?, target.id?, occurredAt, traceId |
| `AI_ACTION_STEP_FAILED` | `ai_action.step_failed` | execution.id, step.id, tool.code, error.code, retryable, occurredAt, traceId |
| `AI_ACTION_EXECUTION_SUCCEEDED` | `ai_action.execution_succeeded` | execution.id, succeeded.count, latency.ms, occurredAt, traceId |
| `AI_ACTION_EXECUTION_PARTIAL` | `ai_action.execution_partial` | execution.id, succeeded.count, failed.count, skipped.count, occurredAt, traceId |
| `AI_ACTION_EXECUTION_FAILED` | `ai_action.execution_failed` | execution.id, error.code, occurredAt, traceId |
| `AI_ACTION_EXECUTION_PAUSED` | `ai_action.execution_paused` | actor.userId, execution.id, occurredAt, traceId |
| `AI_ACTION_EXECUTION_CANCELLED` | `ai_action.execution_cancelled` | actor.userId, execution.id, succeeded.count, occurredAt, traceId |
| `AI_ACTION_COMPENSATION_STARTED` | `ai_action.compensation_started` | actor.userId, execution.id, supported.count, occurredAt, traceId |
| `AI_ACTION_COMPENSATION_COMPLETED` | `ai_action.compensation_completed` | execution.id, compensated.count, failed.count, occurredAt, traceId |
| `AI_ACTION_POLICY_DENIED` | `ai_action.policy_denied` | actor.userId, workspace.id, project.id?, tool.code?, reason.code, occurredAt, traceId |

Cross-module events use existing outbox. Domain action events remain emitted by their owning modules; Phase 44 does not replace them.

---

# 5. Forbidden variables

Never include:

```text
raw tool input/output
raw before/after values
prompt/model response/chain-of-thought
secrets/tokens/presigned URLs
sensitive financial/personal fields
stack traces/provider payloads
ACL tokens or permission lists
```

Use IDs, codes, counts, hashes, labels, versions, and redacted error codes only.

---

# 6. Required initializer tests

```text
permissionInitializer_firstRun_createsAuthorities
permissionInitializer_secondRun_noDuplicates
eventInitializer_firstRun_createsDefinitions
eventInitializer_secondRun_noDuplicates
registryInitializer_seedsLockedMvpToolsSchemasPolicy
registryInitializer_secondRun_noDuplicates
ordinaryRole_notGrantedAutoExecuteOrPolicyManage
actionAuthority_doesNotBypassTargetPermission
events_doNotExposeToolPayloadOrDiff
readyToApplyEvent_requiresDurablePlan
```
