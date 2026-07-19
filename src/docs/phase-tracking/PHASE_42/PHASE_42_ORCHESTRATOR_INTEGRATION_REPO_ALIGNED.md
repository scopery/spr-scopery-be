# Phase 42 — Orchestrator, AiTool Registry, Retrieval and LLM Integration — Repository-Aligned

> Status: **Accepted / implementation-blocking**  
> Orchestrator owner: `modules/aiassistant`  
> Retrieval execution path: existing AiTool registry only  
> Allowed tool code: `knowledge.search`

---

# 1. Required runtime components

```text
AiAssistantTurnOrchestrator
AiAssistantContextBuilder
AiAssistantQuotaService
AiAssistantMemorySummaryService
AiAssistantCitationValidationService
AiAssistantSseStreamService
existing AiToolExecutionService / AiTool registry facade
KnowledgeSearchAiToolHandler
existing Phase 07 LLM provider abstraction
existing ErrorResponse/AppException
existing Outbox/Event Registry
```

Representative application port names may adapt to exact existing interfaces, but no second registry/provider framework may be created.

---

# 2. Start-turn transaction

`POST /conversations/{id}/messages` must atomically:

```text
1. authorize conversation and current workspace/project;
2. validate conversation ACTIVE;
3. enforce message/conversation quota preconditions;
4. enforce idempotency key;
5. allocate turnId;
6. persist USER message as COMPLETED;
7. persist ASSISTANT placeholder as QUEUED;
8. update conversation.lastMessageAt;
9. reserve one daily turn;
10. enqueue/dispatch asynchronous turn execution;
11. return 202 with IDs and stream URL.
```

If asynchronous dispatch fails before commit, the transaction rolls back. If dispatch fails after commit, the assistant placeholder must transition to FAILED through recovery logic.

Idempotency is persisted in `aiassistant_message.idempotency_key` with a unique partial index per conversation. It must not rely on process memory.

---

# 3. Turn execution algorithm

```text
A. Mark ASSISTANT message CONTEXTUALIZING.
B. Persist and emit message.started.
C. Build/persist validated context snapshot.
D. Emit context.completed.
E. Classify request into one response mode.
F. For grounded/project factual modes, call knowledge.search via AiTool registry.
G. Persist TOOL_REQUEST message + aiassistant_tool_call REQUESTED/RUNNING.
H. Execute live read-only handler.
I. Persist TOOL_RESULT message + safe result summary + retrievalTraceId.
J. Select at most 8 accessible chunks under token budget.
K. Build structured LLM request with prompt profile SCOPERY_CONTEXTUAL_ASSISTANT_V1.
L. Stream provider output through bounded delta callback.
M. Validate structured response, policy, and citation references.
N. Persist final answer and normalized citations in one transaction.
O. Persist/emit final SSE event.
P. Finalize quota counts and emit completion/failure event.
Q. Schedule memory summarization when trigger conditions are met.
```

Cancellation is checked between every major step and on every provider delta callback.

---

# 4. Response-mode classification

Locked modes:

```text
GENERAL_GUIDE
GROUNDED_ANSWER
CURRENT_PAGE_EXPLANATION
FIELD_EXPLANATION
DISABLED_ACTION_EXPLANATION
TRACEABILITY_ANSWER
COMPARISON_SUMMARY
INSUFFICIENT_EVIDENCE
ACCESS_RESTRICTED
OUT_OF_SCOPE
```

Deterministic routing should be used before LLM intent classification when endpoint semantics are explicit:

```text
/explain-page → CURRENT_PAGE_EXPLANATION
/explain-field → FIELD_EXPLANATION
/explain-disabled-action → DISABLED_ACTION_EXPLANATION
```

Conversation message endpoint may use the configured LLM classifier, but tool allowlisting remains server-side.

---

# 5. Tool request contract

The orchestrator calls the existing registry with exact code:

```text
knowledge.search
```

Safe logical arguments:

```json
{
  "query": "Why is API Integration blocked?",
  "projectId": "uuid",
  "sourceTypes": ["TASK", "DOCUMENT_VERSION", "MEETING_MINUTE"],
  "topK": 20,
  "includeGraphExpansion": false,
  "filters": {
    "language": null,
    "updatedFrom": null,
    "updatedTo": null
  }
}
```

The registry execution context injects:

```text
actorId
workspaceId
current project authorization
ACL tokens
classification clearance
traceId
correlationId
```

The LLM cannot supply or override these values.

---

# 6. Live AiTool handler lock

Current registry behavior is known to include stub/NO_OP execution. Phase 42 must make one real handler executable.

Exact handler:

```text
KnowledgeSearchAiToolHandler
```

Ownership:

```text
modules/knowledge
```

Required semantics:

```text
- supports exact tool code knowledge.search;
- read-only;
- validates schema/version;
- obtains security scope from registry execution context;
- calls Phase 41 retrieval Action/QueryService;
- returns safe identifiers/snippets/citation package;
- never returns embedding vectors, raw ACL tokens, secrets, or unrestricted content;
- returns retrievalTraceId;
- maps errors into existing AppException/error catalog.
```

Required registry behavior:

```text
- one active handler per tool code/version;
- duplicate handler registration fails startup;
- unknown tool code is blocked;
- mutation tools are not included in Phase 42 allowlist;
- execution is logged/audited through bounded metadata;
- handler timeout is configurable and enforced.
```

---

# 7. Tool transcript persistence

Before execution:

```text
TOOL_REQUEST message content = bounded JSON summary
aiassistant_tool_call.status = REQUESTED → RUNNING
requestHash = SHA-256 canonical safe arguments
maskedArguments excludes server ACL/secret fields
```

After execution:

```text
TOOL_RESULT message content = bounded JSON summary
aiassistant_tool_call.status = SUCCEEDED/FAILED/BLOCKED/CANCELLED
resultSummary includes source IDs/version IDs/chunk IDs/ranks/counts/truncated
retrievalTraceId references Phase 41 trace
```

Limits:

```text
masked request <= 32 KiB
safe result summary <= 64 KiB
max result references stored = 20
```

Normal user message-history API excludes tool messages.

---

# 8. Prompt assembly

Prompt input order:

```text
1. system safety/read-only instructions;
2. prompt profile SCOPERY_CONTEXTUAL_ASSISTANT_V1;
3. validated page/entity/action context;
4. valid memory summary, when available;
5. latest 8 user-visible USER/ASSISTANT messages;
6. current user question;
7. bounded tool evidence with citation reference IDs;
8. structured output schema.
```

Never include:

```text
private chain-of-thought
provider secret
raw ACL tokens
unbounded prior conversation
raw full documents
inaccessible fields
unvalidated client action availability
```

Context budget:

```text
24,000 max input tokens
2,000 max output tokens
8 max evidence chunks
```

Budget reduction order:

```text
1. remove low-ranked evidence;
2. truncate safe snippets at semantic boundaries;
3. use valid memory summary instead of older messages;
4. keep current question/context and highest-ranked evidence;
5. fail with AI_MESSAGE_CONTEXT_TOO_LARGE rather than silently omit required authorization/context.
```

---

# 9. Structured model response

Logical schema:

```json
{
  "responseMode": "GROUNDED_ANSWER",
  "answerMarkdown": "Task is blocked because...",
  "citationRefs": ["evidence-1", "evidence-2"],
  "suggestedQuestions": ["Which dependency should finish first?"],
  "insufficientEvidence": false
}
```

Validation:

```text
responseMode must be allowed
answerMarkdown <= configured final answer limit
citationRefs must reference supplied evidence only
project factual modes require >= 1 validated citation
no citation may reference inaccessible/unknown chunk
no mutation claim/action plan is allowed
```

If validation fails once, a bounded repair pass may run with the same evidence and no new tools. A second failure finalizes FAILED/BLOCKED according to error type.

---

# 10. Grounding outcomes

## Sufficient evidence

```text
Generate grounded answer.
Persist citations before emitting citation.added.
```

## No accessible results

```text
responseMode = INSUFFICIENT_EVIDENCE
No fabricated answer.
Explain that no accessible evidence was found.
```

## Some source is known to exist but actor lacks access

```text
responseMode = ACCESS_RESTRICTED
Do not reveal source title/count/details that would leak existence unless repository policy permits it.
```

## General product guide

```text
May answer from registered page/field/action metadata and AiGuideDefinition.
Project-specific claims still require retrieval/citations.
```

---

# 11. Citation validation transaction

Finalization transaction must:

```text
1. revalidate conversation/project access;
2. verify each citation belongs to the current tool result;
3. verify source/chunk still accessible;
4. insert aiassistant_message_citation rows;
5. write final assistant content/status/token counts;
6. write final durable SSE event;
7. update conversation.lastMessageAt;
8. commit;
9. broadcast final/citation events after persistence.
```

If any required citation fails, do not persist a grounded final answer as successful.

---

# 12. Quota behavior

Before provider/tool execution:

```text
- lock/create current daily aiassistant_quota_usage row;
- verify turn_count < 200;
- verify estimated daily tokens do not exceed 500,000;
- expire stale `aiassistant_active_stream` rows;
- verify active stream count < 2 for actor/workspace;
- insert one ACTIVE stream reservation for the assistant message;
- increment turn_count once.
```

After final state:

```text
- add actual input/output tokens;
- increment failed/blocked counters where applicable;
- mark the `aiassistant_active_stream` reservation RELEASED;
```

A cancelled/failed/blocked turn counts as one turn. Token counts use actual provider values when available.

---

# 13. Memory summary integration

After a completed assistant message, schedule summary refresh when ADR-042 triggers are met.

Summary generation:

```text
- uses a separate summary prompt profile or bounded deterministic summarizer;
- only user-visible USER/ASSISTANT content;
- no TOOL messages/raw retrieved chunks;
- produces <= 2,000 tokens;
- persists new ACTIVE summary and supersedes previous one atomically;
- emits AI_MEMORY_SUMMARY_CREATED.
```

Permission/source invalidation marks summary STALE before any subsequent turn can use it.

---

# 14. Failure and retry policy

```text
Context/IAM failure → BLOCKED or access error before provider call.
knowledge.search timeout/failure → FAILED or INSUFFICIENT_EVIDENCE only when safe policy explicitly allows fallback.
Provider transient error → one retry through existing provider/resilience policy if configured.
Structured response invalid → one repair attempt.
Database finalization failure → no final broadcast; recover message as FAILED through reconciliation.
Cancellation → no automatic retry.
```

No turn is automatically retried in a way that could duplicate quota/tool records without the same turn/idempotency identity.

---

# 15. Observability

Metrics:

```text
aiassistant_turn_total{status,responseMode}
aiassistant_turn_latency_ms
aiassistant_context_build_latency_ms
aiassistant_tool_call_total{toolCode,status}
aiassistant_tool_latency_ms{toolCode}
aiassistant_llm_input_tokens_total
aiassistant_llm_output_tokens_total
aiassistant_citation_count
aiassistant_insufficient_evidence_total
aiassistant_sse_active_connections
aiassistant_sse_replay_events_total
aiassistant_quota_rejected_total
```

Logs/traces use IDs and codes only; no raw prompt/answer/source text at info level.

---

# 16. Required tests

```text
orchestrator_persistsMessagesBeforeAsyncDispatch
orchestrator_callsKnowledgeSearchThroughRegistry
orchestrator_neverCallsKnowledgeServiceDirectly
registry_dispatchesLiveKnowledgeSearchHandler
registry_duplicateToolHandlerFailsStartup
registry_unknownOrMutationToolIsBlocked
knowledgeSearchHandler_usesServerSecurityScope
knowledgeSearchHandler_returnsNoAclTokensOrVectors
orchestrator_persistsBoundedToolRequestAndResult
orchestrator_projectFactRequiresCitation
orchestrator_invalidCitationFailsFinalization
orchestrator_noEvidenceReturnsInsufficientEvidence
orchestrator_cancelChecksBetweenStages
orchestrator_quotaReservedAndFinalizedExactlyOnce
orchestrator_memorySummaryUsesOnlyVisibleMessages
orchestrator_noChainOfThoughtStoredOrReturned
orchestrator_noMutationClaimOrToolCall
