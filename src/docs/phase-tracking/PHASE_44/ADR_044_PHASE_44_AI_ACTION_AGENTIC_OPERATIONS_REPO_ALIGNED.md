# ADR-044 — Phase 44 AI Action Planning & Agentic Operations — Repository-Aligned Lock

> Status: **Accepted / implementation-blocking**  
> Date: 2026-07-19  
> Applies to: Phase 44 implementation, Phase 43 prepare-apply, Phase 42 chat action preparation, and Phase 45 governance  
> Repository baseline: Phase 41 semantic retrieval is implemented through `V123__migrate_knowledge_chunk_to_pgvector.sql`, PostgreSQL full-text search + pgvector, `PostgresKnowledgeIndexService`, `KnowledgeChunkIndexRecord`, `HybridRetrievalService`, and `KnowledgeSourceIndexingService`; existing `modules/aiagent` AiTool registry, `modules/aiassistant`, `modules/airecommendation`, audit/outbox/idempotency/IAM conventions  
> Conflict rule: **this ADR wins over Phase 44 planning text. Compile-safe repository code, `CLAUDE.md`, and `Coding_convention.md` win over package examples.**

---

# 1. Decision summary

```text
Module owner: modules/aiaction
Table prefix: ai_action_
REST API base: /api/ai-actions
WebSocket handshake endpoint: /api/ws/ai-actions
WebSocket protocol: STOMP over native WebSocket
Live delivery: Redis Pub/Sub → local SimpMessagingTemplate
Durable realtime replay: PostgreSQL ai_action_execution_event
Flyway versions: V124 and V125
Existing AiTool registry: reused; no second tool registry
LLM-callable preparation tool: agent.action.prepare v1
LLM-callable read tool: agent.action.status v1
Mutation tool invocation mode: PLAN_EXECUTION_ONLY
Direct LLM mutation invocation: forbidden
Plan hash: aplan:v1:sha256:<64 lowercase hex>
Preview hash: apreview:v1:sha256:<64 lowercase hex>
Confirmation hash: aconfirm:v1:sha256:<64 lowercase hex>
Execution key: aexec:v1:sha256:<64 lowercase hex>
Step idempotency key: astep:v1:sha256:<64 lowercase hex>
Default plan TTL: 30 minutes
Default preview TTL: 15 minutes
Default confirmation TTL: 10 minutes
Max steps per MVP plan: 25
Max distinct targets per MVP plan: 100
Max concurrent executions per user/workspace: 2
Critical-risk execution: forbidden in Phase 44 MVP
Phase 43 ACCEPT: review state only
Phase 43 PREPARE_APPLY: creates a real Phase 44 request and plan
Phase 21 legacy apply: preserved but never invoked by Phase 44
WebSocket is notification/control transport, not source of truth
PostgreSQL is authoritative for every action state transition
```

No coding agent may change the module, prefix, API path, migration versions, tool codes, no-direct-mutation rule, hash formats, TTLs, batch limits, Phase 21/43 compatibility boundary, or WebSocket durability model without a replacement ADR.

---

# 2. Repository precedence

```text
1. Current compile-safe repository code and real table/class/action names
2. CLAUDE.md / CLAUDE.ms
3. Coding_convention.md
4. ADR-044 repository-aligned lock
5. Phase 44 TO-BE planning document
```

Rules:

```text
- Do not create modules/agentic, modules/copilot, modules/aitool, or a second AiTool registry.
- Do not move existing modules/aiagent registry ownership into modules/aiaction.
- Do not call repositories, EntityManager, raw SQL, shell, arbitrary code, or unrestricted HTTP from an AI action adapter.
- Elasticsearch is not part of the current Scopery architecture and must not be reintroduced by Phase 44.
- Do not query `knowledge_chunk`, call `HybridRetrievalService`, or execute pgvector/full-text SQL directly from `modules/aiaction`; all grounding must go through the registered read-only tool `knowledge.search`.
- Do not create a parallel ErrorResponse envelope.
- Do not store hidden chain-of-thought, raw provider secrets, unrestricted prompts, presigned URLs, or unmasked sensitive snapshots.
- If V124/V125 are occupied at merge time, renumber both atomically to the next consecutive free versions and update every artifact reference in the same change.
```

---

# 2A. Repository retrieval truth inherited from Phase 41

The following implementation is authoritative for any grounding performed while preparing an action plan:

```text
Migration: V123__migrate_knowledge_chunk_to_pgvector.sql
Vector column: knowledge_chunk.embedding vector(1536)
Keyword column: knowledge_chunk.search_vector tsvector
Vector index: PostgreSQL HNSW with cosine operators
Keyword index: PostgreSQL GIN
JPA entity: KnowledgeChunkJpaEntity
Index writer: PostgresKnowledgeIndexService using JdbcTemplate batch updates
Index record: KnowledgeChunkIndexRecord
Retrieval: HybridRetrievalService using NamedParameterJdbcTemplate
Fusion: Reciprocal Rank Fusion in Java
Index orchestration: KnowledgeSourceIndexingService
Public AI retrieval entry point: AiTool code knowledge.search
```

Phase 44 rules:

```text
- AiActionPlanningOrchestrator MAY request grounding through the existing AiTool registry using knowledge.search.
- modules/aiaction MUST NOT inject KnowledgeChunkJpaRepository, JdbcTemplate, NamedParameterJdbcTemplate, PostgresKnowledgeIndexService, or HybridRetrievalService for retrieval.
- Retrieval output is evidence only. It never grants authority, confirms current entity state, or replaces canonical domain reads.
- Before preview and again before execution, every target is loaded from its authoritative domain service/action and its current version, baseline, permission, and business rules are revalidated.
- Citation/source references may be persisted as bounded identifiers and hashes; raw embeddings, raw search vectors, hidden reasoning, and unrestricted retrieved text are not copied into ai_action_* tables.
- Any remaining Elasticsearch reference in Phase 44 material is obsolete and must be removed rather than implemented.
```

Precedence for retrieval conflicts:

```text
1. Current compile-safe repository implementation
2. V123__migrate_knowledge_chunk_to_pgvector.sql
3. knowledge.search live handler contract
4. ADR-044
5. older Phase 44 planning text
```

---

# 3. Module/package layout lock

Logical owner:

```text
modules/aiaction
```

Repository-aligned layout:

```text
modules/aiaction/
├── request/http/controller|request|response
├── plan/http/controller|request|response
├── execution/http/controller|request|response
├── tool/http/controller|response
├── policy/http/controller|response
├── realtime/
│   ├── websocket/
│   ├── messaging/
│   └── security/
├── application/
│   ├── command/
│   ├── action/
│   ├── service/
│   ├── orchestrator/
│   ├── gateway/
│   ├── registry/
│   ├── worker/
│   ├── response/
│   └── port/
├── domain/
│   ├── model/
│   ├── repository/
│   ├── policy/
│   └── value/
└── infrastructure/
    ├── persistence/
    ├── aitool/
    ├── domainadapter/
    ├── redis/
    ├── eventconsumer/
    ├── outbox/
    └── retention/
```

Required flow:

```text
HTTP/STOMP Controller
→ validated request DTO
→ Command/query parameters
→ *Action.execute(Command) for writes
→ *QueryService for reads
→ Response DTO
```

Required application ports:

```text
AiActionToolRegistryPort
AiActionToolAdapter
AiActionAuthorizationPort
AiActionTargetVersionResolver
AiActionBaselineGuardPort
AiActionSensitiveFieldMaskingPort
AiActionAuditReferencePort
AiActionOutboxReferencePort
AiActionRealtimePublisherPort
AiActionWorkerLeasePort
AiActionPhase43SuggestionPort
AiActionPhase21CompatibilityPort
```

---

# 4. Existing AiTool registry coexistence

The existing `modules/aiagent` AiTool registry remains authoritative for tool identity, version, schema metadata, handler registration, and bounded execution logging.

Phase 44 adds action policy and durable execution state without duplicating the registry:

```text
AiTool registry definition
+ ai_action_tool_policy
+ live AiActionToolAdapter binding
= executable Phase 44 tool
```

Tool behavior:

```text
agent.action.prepare v1
- LLM_CALLABLE
- creates/validates a request and plan only
- does not mutate project domain state

agent.action.status v1
- LLM_CALLABLE_READ_ONLY
- returns safe persisted execution status

task.create/task.assign/... mutation tools
- PLAN_EXECUTION_ONLY
- direct AiTool execute from LLM or generic API is rejected
- callable only by AiActionExecutionOrchestrator after policy and confirmation checks
```

Required live handlers:

```text
AgentActionPrepareAiToolHandler
AgentActionStatusAiToolHandler
```

Required adapter registry:

```text
AiActionToolAdapterRegistry
```

Startup fails when an ACTIVE Phase 44 tool policy has zero or multiple live adapters.

---

# 5. Flyway/database lock

Migration files:

```text
V124__phase_44_ai_action_core.sql
V125__phase_44_ai_action_registry_realtime.sql
```

Phase 44 owns:

```text
ai_action_request
ai_action_plan
ai_action_step
ai_action_preview
ai_action_confirmation
ai_action_execution
ai_action_step_execution
ai_action_compensation
ai_action_schema_definition
ai_action_tool_policy
ai_action_policy_definition
ai_action_execution_event
ai_action_control_command
```

Cross-module identifiers have no database foreign keys:

```text
workspace_id, project_id, user_id, assistant_agent_id
origin_conversation_id, origin_message_id, origin_turn_id
origin_suggestion_ref, legacy_phase21_suggestion_id
business entity IDs, audit IDs, outbox IDs, trace IDs
```

Foreign keys inside `ai_action_*` are mandatory.

---

# 6. Plan, preview, confirmation, and execution lock

Canonical flow:

```text
intent/suggestion
→ create AiActionRequest
→ server resolves context and scope
→ build schema-valid AiActionPlan
→ resolve tool policies/adapters
→ dry-run each step
→ generate masked preview
→ compute plan hash
→ request confirmation when required
→ confirm exact plan hash
→ revalidate authorization/version/baseline immediately before execution
→ execute steps through existing Application Actions
→ persist each result before realtime publication
```

Plan and confirmation rules:

```text
- Plan hash includes canonical step order, tool versions, safe inputs, expected target versions, risk, and policy version.
- Client-supplied workspace/project/user/permission fields are ignored and overridden server-side.
- Any material target version, policy, permission, schema, tool version, or baseline change marks the plan STALE.
- Confirmation is bound to plan ID, plan version, plan hash, confirming user, and expiry.
- A confirmation never authorizes a different or regenerated plan.
- Execute endpoint is idempotent and creates at most one durable execution per plan.
```

---

# 7. Risk and execution modes

Risk levels:

```text
LOW
MEDIUM
HIGH
CRITICAL
```

Execution modes:

```text
READ_ONLY
PREVIEW_ONLY
CONFIRM_BEFORE_EXECUTE
AUTO_EXECUTE
FORBIDDEN
```

MVP policy:

```text
LOW:
- may auto-execute only when global/workspace policy allows;
- one step, one target, no sensitive field, no baseline impact, no external side effect.

MEDIUM:
- explicit confirmation required.

HIGH:
- explicit confirmation required;
- max 10 steps and max 25 targets;
- never auto-execute.

CRITICAL:
- forbidden in Phase 44 MVP.
```

Always forbidden for the general assistant:

```text
workspace/project deletion
permission/role/grant mutation
secret/credential mutation or retrieval
billing/payment mutation
irreversible bulk delete
baseline finalization/lock bypass
unrestricted external message/send/publish
arbitrary connector/URL invocation
shell/code/SQL execution
```

---

# 8. MVP action tool catalog

Required preparation/read tools:

```text
agent.action.prepare v1
agent.action.status v1
```

Required mutation tool policies for Phase 43 compatibility:

```text
task.assign v1
task.estimate.update v1
task.mitigation.update v1
meeting.action.assign v1
meeting.action.due-date.update v1
```

Additional direct-chat MVP tool:

```text
task.create v1
```

No tool becomes ACTIVE until:

```text
- AiTool registry definition exists;
- input/output schemas are active;
- ai_action_tool_policy is active;
- exactly one live adapter is registered;
- required domain Application Action exists;
- authorization, preview, idempotency, and contract tests pass.
```

Exact schemas and suggestion mappings are locked in the catalog and schema artifacts.

---

# 9. Realtime lock

```text
Transport: STOMP over native WebSocket
Handshake: /api/ws/ai-actions
Subscribe: /user/queue/ai-actions/{executionId}
Client command destination: /app/ai-actions/{executionId}/commands
REST fallback: pause/resume/cancel/events endpoints
Live fan-out: Redis Pub/Sub
Durable replay: ai_action_execution_event
```

Rules:

```text
- WebSocket disconnect does not cancel or roll back an execution.
- Every published event has executionId, sequence, executionVersion, eventType, occurredAt, and safe payload.
- Database event row is committed before Redis/WebSocket delivery.
- Reconnect uses REST current-state query plus GET events?afterSequence=N.
- Redis loss may delay live updates but cannot lose authoritative execution state.
- Authentication uses the existing authenticated session/token during handshake; credentials are never accepted in query parameters.
```

---

# 10. Phase 21 and Phase 43 compatibility

```text
- Phase 43 ACCEPT remains review-only.
- Phase 43 PREPARE_APPLY calls RecommendationApplyPreparationPort backed by Phase 44.
- A successful handoff persists a real ai_action_request and ai_action_plan before returning IDs.
- Phase 44 emits/causes AI_SUGGESTION_READY_TO_APPLY only after the plan is durable.
- Phase 44 never invokes the legacy Phase 21 apply endpoint.
- p21:<uuid> may be converted only through an explicit compatibility adapter and active Phase 44 schema/tool mapping.
- Unsupported legacy payloads return AI_ACTION_LEGACY_PAYLOAD_UNSUPPORTED.
- Historical Phase 21 apply behavior remains unchanged for its existing API until separately deprecated.
```

---

# 11. Dependency gates

Hard prerequisites before Phase 44 implementation completion:

```text
Phase 41 knowledge/search available for grounded planning when needed
Phase 42 aiassistant conversation/message linkage available for chat origin
Phase 43 accepted suggestion and prepare-apply port available
Existing audit/outbox/idempotency/IAM infrastructure available
At least one real domain Application Action adapter passes integration tests
Existing Redis available for live fan-out
```

Phase 44 may scaffold before every optional domain adapter exists, but inactive/missing adapters must fail closed.
