# PHASE 44 — AI Tool Gateway, Action Planning, Safe Execution & Agentic Operations — Repository-Aligned

> Project: Scopery Backend  
> Phase: 44  
> Status: **Implementation-ready planning / repository-aligned lock**  
> Module: `modules/aiaction`  
> Tables: `ai_action_*`  
> REST base: `/api/ai-actions`  
> WebSocket: `/api/ws/ai-actions`  
> Flyway: `V124/V125`  
> Depends on: Phase 41 `knowledge.search` backed by PostgreSQL full-text search + pgvector (`V123`), Phase 42 chat/tool transcript, Phase 43 recommendation handoff, existing IAM/audit/outbox/idempotency/domain Actions  
> Conflict rule: **ADR-044 and focused repo-aligned artifacts win over this consolidated document.**

---

# 0. Purpose

Phase 44 delivers the controlled transition from:

```text
AI explains/suggests
→ AI prepares a structured action plan
→ user reviews a masked preview
→ user confirms the exact plan
→ Scopery executes allowlisted domain Actions safely
```

It implements Level 4 and bounded Level 5:

```text
Level 4: PREPARE_ACTION
Level 5: EXECUTE_ALLOWED_CONFIRMED_ACTION
```

---

# 1. Non-negotiable principles

```text
AI never writes the database directly.
AI never calls repositories/EntityManager/raw SQL/shell/arbitrary HTTP.
Every mutation maps to an existing typed Application Action/Command.
Human domain permission and AI tool policy are both required.
Mutation tools cannot be called directly by the LLM.
Every plan is versioned, hashed, previewed, and revalidated.
High-risk actions never auto-execute.
Critical/general destructive actions are forbidden.
Every committed step is auditable, idempotent, and honestly reported.
Compensation is explicit and tool-specific; no fake global rollback.
PostgreSQL is authoritative; WebSocket/Redis are delivery mechanisms.
```

---

# 2. Locked architecture

```text
User/chat/accepted suggestion
→ AiActionRequest
→ AiActionPlanningOrchestrator
→ existing AiTool registry definitions
→ ai_action_tool_policy
→ AiActionToolAdapter dry-run
→ AiActionPlan + AiActionStep + AiActionPreview
→ confirmation bound to plan hash
→ AiActionExecutionWorker
→ AiActionToolGateway
→ typed existing Action.execute(Command)
→ domain audit/outbox
→ persisted step/execution result
→ durable execution event
→ Redis Pub/Sub
→ STOMP user destination
```

Module/package, tables, paths, tools, and limits are locked in ADR-044.

---

# 2A. Phase 41 PostgreSQL/pgvector dependency

Phase 44 does not own or duplicate retrieval infrastructure.

```text
knowledge.search
→ existing AiTool registry
→ Phase 41 live handler
→ HybridRetrievalService
→ PostgreSQL full-text candidates
→ pgvector cosine candidates
→ Java RRF
→ permission-safe chunks/citations
```

Authoritative repository components:

```text
V123__migrate_knowledge_chunk_to_pgvector.sql
KnowledgeChunkJpaEntity
PostgresKnowledgeIndexService
KnowledgeChunkIndexRecord
HybridRetrievalService
KnowledgeSourceIndexingService
```

Hard boundaries:

```text
modules/aiaction never queries knowledge_chunk directly;
modules/aiaction never generates vector/full-text SQL;
modules/aiaction never updates embeddings or search_vector;
retrieval evidence never grants mutation permission;
canonical domain reads win over retrieved projections;
Phase 44 introduces no Elasticsearch client, service, container, mapping, alias, or configuration.
```

---

# 3. Scope

In scope:

```text
request/plan/preview/confirmation/execution persistence
versioned action/tool schema and risk policy
existing AiTool registry integration
Phase 43 prepare-apply
bounded direct-chat action preparation
safe domain Action adapters
idempotent durable worker
partial failure and supported compensation
STOMP WebSocket live progress + PostgreSQL replay
IAM/events/audit/outbox/metrics/tests
```

Out of scope:

```text
permission/billing/secret mutation
workspace/project delete
arbitrary external sends
arbitrary code/shell/SQL/HTTP
fully autonomous critical actions
workflow engine replacement
new Kafka/Temporal/Camunda requirement
Phase 45 evaluation/governance dashboards
```

---

# 4. MVP tools

LLM-callable:

```text
agent.action.prepare v1
agent.action.status v1
```

Plan-execution-only:

```text
task.create v1
task.assign v1
task.estimate.update v1
task.mitigation.update v1
meeting.action.assign v1
meeting.action.due-date.update v1
```

Exact schema/tool/adapter activation rules are in the MVP catalog and schema registry artifacts.

---

# 5. Persistence

V101 creates:

```text
ai_action_request
ai_action_plan
ai_action_step
ai_action_preview
ai_action_confirmation
ai_action_execution
ai_action_step_execution
ai_action_compensation
```

V102 creates:

```text
ai_action_schema_definition
ai_action_tool_policy
ai_action_policy_definition
ai_action_execution_event
ai_action_control_command
```

Cross-module IDs remain opaque identifiers without cross-module database FKs. Internal Phase 44 FKs are mandatory.

---

# 6. Hashes and TTLs

```text
plan: aplan:v1:sha256:<64hex>
preview: apreview:v1:sha256:<64hex>
confirmation: aconfirm:v1:sha256:<64hex>
execution: aexec:v1:sha256:<64hex>
step: astep:v1:sha256:<64hex>

plan TTL: 30 minutes
preview TTL: 15 minutes
confirmation TTL: 10 minutes
realtime replay retention: 7 days after completion
```

Any material state/version/permission/policy/schema change invalidates the plan/confirmation.

---

# 7. Limits

```text
max plan steps: 25
max distinct targets: 100
max high-risk steps: 10
max high-risk targets: 25
max concurrent active executions per user/workspace: 2
max attempts per step: 3 by policy, database hard cap 5
max WebSocket frame: 64 KiB
```

---

# 8. Risk/confirmation

```text
LOW: auto only when explicitly eligible and policy-enabled, one step/target, no sensitive/baseline/external/acting-on-behalf.
MEDIUM: explicit confirmation.
HIGH: explicit UI/API confirmation; never auto.
CRITICAL: forbidden in MVP.
```

Preview and confirmation policy is defined in `PHASE_44_CONFIRMATION_RISK_POLICY_REPO_ALIGNED.md`.

---

# 9. Execution

Each step executes through one real domain transaction. No global transaction spans the entire plan.

```text
STOP_ON_FAILURE is MVP default.
Dependent steps are skipped after failure.
Committed successful steps remain committed unless explicitly compensated.
Final state reports success/failure/skipped/cancelled/compensated counts.
```

Worker lease, recovery, idempotency, and state machines are locked in focused artifacts.

---

# 10. Realtime

```text
STOMP over native WebSocket
/api/ws/ai-actions
/user/queue/ai-actions/{executionId}
/app/ai-actions/{executionId}/commands
Redis Pub/Sub live fan-out
PostgreSQL durable events/replay
```

WebSocket disconnect never changes execution correctness.

---

# 11. Phase 21/43 boundary

```text
Phase 43 ACCEPT = review state only.
Phase 43 PREPARE_APPLY = real Phase 44 request/plan.
Phase 44 never invokes Phase 21 legacy apply.
Legacy p21 payload requires explicit mapper; unsupported payload fails closed.
```

---

# 12. API

Required API groups:

```text
tools
requests
plans
preview/validate/confirm/cancel/execute
executions/steps/events/history
pause/resume/cancel/compensate
WebSocket connection/subscription/commands
```

Every request/response is locked in `PHASE_44_API_CONTRACTS_REPO_ALIGNED.md` and uses the repository `ErrorResponse/AppException`.

---

# 13. Error catalog

```text
AI_TOOL_NOT_FOUND
AI_TOOL_INACTIVE
AI_TOOL_SCHEMA_INVALID
AI_TOOL_ADAPTER_NOT_FOUND
AI_TOOL_DIRECT_MUTATION_FORBIDDEN
AI_TOOL_PERMISSION_DENIED
AI_TOOL_POLICY_DENIED
AI_ACTION_REQUEST_NOT_FOUND
AI_ACTION_DOMAIN_CAPABILITY_UNAVAILABLE
AI_ACTION_REQUIRED_INPUT_MISSING
AI_ACTION_LEGACY_PAYLOAD_UNSUPPORTED
AI_ACTION_PLAN_NOT_FOUND
AI_ACTION_PLAN_INVALID_STATUS
AI_ACTION_PLAN_VALIDATION_FAILED
AI_ACTION_PLAN_STALE
AI_ACTION_PLAN_EXPIRED
AI_ACTION_PREVIEW_REQUIRED
AI_ACTION_PREVIEW_EXPIRED
AI_ACTION_CONFIRMATION_REQUIRED
AI_ACTION_CONFIRMATION_INVALID
AI_ACTION_CONFIRMATION_EXPIRED
AI_ACTION_EXECUTION_ALREADY_EXISTS
AI_ACTION_EXECUTION_NOT_FOUND
AI_ACTION_EXECUTION_INVALID_STATUS
AI_ACTION_EXECUTION_PARTIAL
AI_ACTION_STEP_FAILED
AI_ACTION_IDEMPOTENCY_CONFLICT
AI_ACTION_TARGET_VERSION_CONFLICT
AI_ACTION_BASELINE_GUARD_BLOCKED
AI_ACTION_BATCH_LIMIT_EXCEEDED
AI_ACTION_CONCURRENT_LIMIT_EXCEEDED
AI_ACTION_COMPENSATION_UNSUPPORTED
AI_ACTION_EVENT_REPLAY_GAP
AI_ACTION_FORBIDDEN
```

---

# 14. Required tests

```text
activeTool_withoutAdapter_failsStartup
directMutationToolCall_fromLlm_isBlocked
prepareTool_createsPlanButNoDomainMutation
planHash_isDeterministic
materialTargetChange_marksPlanStale
confirmation_wrongHash_isRejected
confirmation_expired_isRejected
execute_withoutConfirmation_isRejected
duplicateExecute_returnsSameExecution
step_callsTypedDomainAction_notRepository
permissionChangeBeforeExecution_blocksStep
partialFailure_reportsExactCommittedSteps
compensation_reversesOnlySupportedVersionSafeSteps
workerCrash_resumesWithoutDuplicateMutation
redisOutage_executionStillCompletesAndReplays
websocketUnauthorizedSubscriptionDenied
stompCommand_usesSameApplicationActionAsRest
p43AcceptedSuggestion_preparesRealPlan
p21UnsupportedPayload_failsClosed
forbiddenCriticalAction_neverPlansOrExecutes
```

---

# 15. Completion gate

Phase 44 is complete only when:

```text
V124/V125 migrate cleanly
initializers are idempotent
at least the Phase 43 compatibility tool set has real active adapters
all mutation paths use existing Application Actions
request→plan→preview→confirm→execute works end-to-end
WebSocket/Redis and REST replay tests pass
cross-tenant and underlying permission tests pass
idempotency/recovery/partial/compensation tests pass
Phase 21 legacy regression passes
completion evidence file is filled with real outputs
```
