# Phase 44 — Tool Gateway & Orchestrator Integration — Repository-Aligned

> Status: **Accepted / implementation-blocking**

---

# 1. Components

```text
AiActionRequestOrchestrator
AiActionPlanningOrchestrator
AiActionPreviewOrchestrator
AiActionConfirmationAction
AiActionExecutionOrchestrator
AiActionCompensationOrchestrator
AiActionToolGateway
AiActionToolAdapterRegistry
AiActionRealtimePublisher
AiActionExecutionWorker
```

---

# 2. Planning flow

```text
Create request
→ load origin context (chat/suggestion/direct/legacy)
→ server-resolve workspace/project/actor/scope
→ classify requested semantic operations
→ resolve AiTool definitions through existing registry
→ resolve ai_action_tool_policy
→ validate JSON schemas
→ resolve target versions and required inputs
→ call adapter dryRun/preview only
→ aggregate risk/policy/batch/baseline effects
→ persist plan/steps/preview
→ compute hashes
→ return confirmation requirement
```

The planner may use Phase 41 `knowledge.search` through the existing AiTool registry for grounding, but retrieval evidence cannot grant mutation permission or replace canonical domain reads.

Locked repository path:

```text
AiActionPlanningOrchestrator
→ AiTool registry execute knowledge.search
→ Phase 41 knowledge.search live handler
→ HybridRetrievalService
→ NamedParameterJdbcTemplate
   ├── PostgreSQL tsvector/tsquery candidate query
   └── pgvector cosine candidate query
→ Java RRF
→ bounded citations/evidence
```

Forbidden Phase 44 dependencies:

```text
KnowledgeChunkJpaRepository
KnowledgeChunkJpaEntity persistence access
PostgresKnowledgeIndexService
KnowledgeChunkIndexRecord
HybridRetrievalService direct injection
JdbcTemplate/NamedParameterJdbcTemplate retrieval SQL
vector/tsvector mutation
```

The class names above are listed to define boundaries, not to authorize direct coupling. `knowledge.search` is the only supported Phase 44 retrieval integration point.

---

# 3. Gateway contract

```java
public interface AiActionToolAdapter {
    String toolCode();
    String toolVersion();
    AiActionDryRunResult dryRun(AiActionToolContext context, JsonNode input);
    AiActionExecutionResult execute(AiActionToolContext context, JsonNode input, String idempotencyKey);
    default AiActionCompensationResult compensate(...) { return unsupported; }
}
```

Semantic contract; adapt names to repository conventions.

Context includes only server-resolved values:

```text
actor user ID
assistant agent ID
workspace/project scope
required authority decision
acting-on-behalf trace
plan/execution/step IDs
trace/correlation ID
expected target version
baseline/change-request decision
safe idempotency key
```

Adapters must not accept client-provided authorities or unrestricted service locators.

---

# 4. Domain action mapping

Each adapter:

```text
- converts validated JSON into a typed existing Command;
- calls the real *Action.execute(Command);
- lets domain validation/IAM/error catalog run normally;
- maps result into bounded AiActionExecutionResult;
- records real audit/outbox references when exposed by repository infrastructure;
- never catches and converts all errors into success/no-op.
```

If a required domain action is absent, the tool remains INACTIVE and planning returns `AI_TOOL_ADAPTER_NOT_FOUND` or `AI_ACTION_DOMAIN_CAPABILITY_UNAVAILABLE`.

---

# 5. Transaction boundaries

```text
- plan/preview creation transaction: Phase 44 tables only;
- confirmation transaction: confirmation + plan transition;
- execution creation transaction: execution + plan queued + durable event;
- each domain step: one existing domain action transaction;
- step result transaction: Phase 44 step/execution/event update;
- no transaction spans multiple external/domain steps.
```

This prevents long global transactions and makes partial completion explicit.

---

# 6. Chat integration

Links:

```text
AiConversation/AiMessage USER
→ ai_action_request.origin_*
→ ai_action_plan
→ ai_action_execution
→ assistant TOOL_RESULT/final summary
```

Phase 42 stores bounded tool transcript references. Phase 44 does not duplicate the full conversation body.

Final assistant summary must be generated from persisted execution/step states, not model memory:

```text
Succeeded: ...
Failed: ...
Skipped: ...
Compensated: ...
Next safe action: ...
```

---

# 7. Phase 43 integration

`RecommendationApplyPreparationPort` implementation:

```text
AiActionRecommendationApplyPreparationAdapter
```

It calls the same application action used by `POST /api/ai-actions/requests`, with origin `SUGGESTION`; it does not bypass request/plan persistence.
