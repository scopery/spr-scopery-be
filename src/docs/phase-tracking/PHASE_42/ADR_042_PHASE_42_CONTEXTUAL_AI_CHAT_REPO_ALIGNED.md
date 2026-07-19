# ADR-042 — Phase 42 Contextual AI Chat & Guidance — Repository-Aligned Lock

> Status: **Accepted / implementation-blocking**  
> Date: 2026-07-17  
> Applies to: Phase 42 implementation and Phase 43–45 consumers  
> Repository baseline used by this revision: latest known migration `V94`; Phase 41 reserves `V95/V96`; existing `ErrorResponse` / `AppException`; existing `modules/aiagent` tool registry; repository package convention `{feature}/http/controller|request|response` plus `application/action|service`  
> Conflict rule: **this ADR wins over Phase 42 planning text. Compile-safe existing code, `CLAUDE.md`, and `Coding_convention.md` win over package examples.**

---

# 1. Decision summary

```text
Module owner: modules/aiassistant
Table prefix: aiassistant_
REST API base: /api/ai-assistant
Conversation capability: READ_ONLY
Retrieval tool code: knowledge.search
Tool invocation path: AiTool registry only; no direct retrieval bypass
Live knowledge tool handler owner: modules/knowledge
SSE implementation: Spring MVC SseEmitter
WebSocket: not in Phase 42
Flyway versions: V97 and V98
Prompt profile code: SCOPERY_CONTEXTUAL_ASSISTANT_V1
Context hash format: ctx:v1:sha256:<64 lowercase hex>
Memory summary strategy: summary-v1
Conversation default retention: 180 days after last activity
Soft-delete purge delay: 30 days unless legal hold
SSE replay retention: 24 hours
Tool/retrieval trace retention: 30 days
Default context budget: 24,000 input tokens
Default output budget: 2,000 tokens
Default retrieved chunks offered to LLM: max 8
Daily turn quota: 200 turns per actor/workspace
Daily token quota: 500,000 input+output tokens per actor/workspace
Concurrent active streams: 2 per actor/workspace
```

No coding agent may rename the module, table prefix, API base, tool code, SSE mechanism, migration versions, context hash format, or read-only boundary without a replacement ADR.

---

# 2. Repository precedence

```text
1. Current compile-safe repository code and real table/class names
2. CLAUDE.md / CLAUDE.ms
3. Coding_convention.md
4. ADR-042 repository-aligned lock
5. Phase 42 TO-BE planning document
```

Rules:

```text
- Do not create modules/aichat, modules/assistant, or modules/copilot.
- Do not use interfaces/rest package layout.
- Do not create a second AI tool registry.
- Do not call Phase 41 retrieval directly from the orchestrator.
- Do not create a parallel error envelope.
- Do not store or expose chain-of-thought.
- Do not add WebFlux solely for Phase 42.
- Do not claim WebSocket support in Phase 42.
- If V97/V98 are occupied at merge time, renumber both atomically and update every artifact reference in the same change.
```

---

# 3. Module and package layout lock

Logical owner:

```text
modules/aiassistant
```

Repository-aligned layout:

```text
modules/aiassistant/
├── conversation/
│   └── http/
│       ├── controller/
│       ├── request/
│       └── response/
├── message/
│   └── http/
│       ├── controller/
│       ├── request/
│       └── response/
├── guide/
│   └── http/
│       ├── controller/
│       ├── request/
│       └── response/
├── feedback/
│   └── http/
│       ├── controller/
│       ├── request/
│       └── response/
├── application/
│   ├── command/
│   ├── action/
│   ├── service/
│   ├── orchestrator/
│   ├── response/
│   └── port/
├── domain/
│   ├── model/
│   ├── repository/
│   ├── policy/
│   └── value/
└── infrastructure/
    ├── persistence/
    ├── sse/
    ├── llm/
    ├── quota/
    ├── retention/
    └── eventconsumer/
```

Required interaction pattern:

```text
HTTP Controller
→ Request DTO
→ Command or query parameters
→ *Action.execute(Command) for writes
→ *QueryService for reads
→ Response DTO
```

Exact primary classes to implement, unless an equivalent compile-safe class already exists:

```text
AiAssistantTurnOrchestrator
AiAssistantContextBuilder
AiAssistantConversationQueryService
AiAssistantMessageQueryService
AiAssistantSseStreamService
AiAssistantQuotaService
AiAssistantMemorySummaryService
AiAssistantCitationValidationService
AiAssistantRetentionService
```

---

# 4. Database and migration lock

## 4.1 Migration files

```text
V97__phase_42_aiassistant_conversation_core.sql
V98__phase_42_aiassistant_streaming_quota.sql
```

## 4.2 Phase 42 tables

V97:

```text
aiassistant_conversation
aiassistant_message
aiassistant_context_snapshot
aiassistant_message_citation
aiassistant_tool_call
aiassistant_memory_summary
aiassistant_guide_definition
aiassistant_suggested_question
aiassistant_answer_feedback
```

V98:

```text
aiassistant_stream_event
aiassistant_active_stream
aiassistant_quota_usage
```

Cross-module identifiers such as workspace, project, actor, AI agent, prompt profile, knowledge chunk, and retrieval trace must not use database foreign keys unless the current repository already uses cross-module foreign keys for the same boundary.

Internal `aiassistant_*` relationships must use foreign keys.

---

# 5. Conversation and message lock

## 5.1 Conversation types

```text
GENERAL_GUIDE
PROJECT_ASSISTANT
```

## 5.2 Capability levels

```text
GUIDE
CONTEXTUAL_ANSWER
```

## 5.3 Message roles

```text
SYSTEM
USER
ASSISTANT
TOOL_REQUEST
TOOL_RESULT
```

## 5.4 Message states

```text
RECEIVED
QUEUED
CONTEXTUALIZING
RETRIEVING
GENERATING
STREAMING
CANCEL_REQUESTED
COMPLETED
FAILED
CANCELLED
BLOCKED
```

Final states:

```text
COMPLETED
FAILED
CANCELLED
BLOCKED
```

A final message cannot transition to another state.

---

# 6. AiTool registry and knowledge.search lock

All grounded project retrieval must execute through the existing AiTool registry.

Required tool code:

```text
knowledge.search
```

Invocation path:

```text
AiAssistantTurnOrchestrator
→ existing AiToolExecutionService / registry facade
→ handler dispatch by exact tool code
→ KnowledgeSearchAiToolHandler
→ Phase 41 permission-aware retrieval application service
```

Live handler owner:

```text
modules/knowledge
```

Locked handler name:

```text
KnowledgeSearchAiToolHandler
```

The concrete handler must be placed under the existing Knowledge package convention. A representative package is:

```text
modules/knowledge/retrieval/application/aitool/KnowledgeSearchAiToolHandler
```

If the repository already defines a different live-handler interface/package, implement the same semantics using that interface rather than creating a competing registry.

Phase 42 includes the minimum registry hardening required to dispatch a real read-only handler because the current execution path is known to be largely stub/NO_OP.

Allowed tools in Phase 42:

```text
knowledge.search
```

No mutation tool may be callable from the Phase 42 orchestrator.

Server-injected values that the LLM/client cannot override:

```text
actorId
workspaceId
projectId authorization result
ACL tokens
classification clearance
traceId
correlationId
```

---

# 7. Context Builder lock

Client context is untrusted navigation metadata, never authorization.

Client may send:

```text
route
pageCode
entityType/entityId
selectedActionCode
tabCode
locale
timezone
client-visible field codes
client-reported available action codes
client context version
```

Server must resolve and override:

```text
actorId
workspaceId
projectId
actual entity visibility
actual field visibility
actual available action codes
disabled action reason codes
page metadata version
permission signature
context hash
```

Context hash format:

```text
ctx:v1:sha256:<64 lowercase hex>
```

Hash input is canonical JSON of server-resolved, non-secret context fields. Client-provided context hash is only a cache hint and cannot be trusted.

---

# 8. LLM and prompt profile lock

Prompt profile code:

```text
SCOPERY_CONTEXTUAL_ASSISTANT_V1
```

Phase 42 reuses the existing Phase 07 provider abstraction and configured chat deployment. It does not directly import a provider SDK into domain/application code.

Structured output fields:

```text
responseMode
answerMarkdown
citationRefs
suggestedQuestions
insufficientEvidence
```

The model is never asked to return hidden reasoning.

Project factual answers require at least one validated citation. General product guide responses may reference a registered `AiGuideDefinition` without a Phase 41 chunk citation.

---

# 9. SSE implementation lock

Mechanism:

```text
Spring MVC SseEmitter
```

Do not introduce Spring WebFlux solely for streaming.

Defaults:

```text
Emitter timeout: 180 seconds
Heartbeat interval: 15 seconds
Maximum one event payload: 8 KiB
Maximum answer.delta text: 4 KiB per event
Maximum events per assistant message: 4,096
Replay event retention: 24 hours
```

Reconnect:

```text
- Accept Last-Event-ID header.
- Accept afterSequence query as fallback.
- Replay aiassistant_stream_event rows with sequence > cursor.
- Then attach to the live emitter if the message is non-final.
- If the message is final, replay through the final event and close.
```

SSE is delivery only. PostgreSQL message, citation, tool-call, and final status records remain the source of truth.

---

# 10. Quota and context budget lock

Defaults are configuration-backed and seeded/documented, not hard-coded throughout business code:

```text
Max user message length: 8,000 characters
Max conversation title: 200 characters
Max messages per conversation: 500
Max retrieved candidates requested from Phase 41: 20
Max chunks passed to LLM: 8
Max input context: 24,000 tokens
Max output: 2,000 tokens
Max active streams per actor/workspace: 2
Max turns per actor/workspace/day: 200
Max input+output tokens per actor/workspace/day: 500,000
Max safe tool request snapshot: 32 KiB
Max safe tool result snapshot: 64 KiB
```

Turn quota is reserved before provider execution and finalized after completion. Active-stream concurrency is coordinated through `aiassistant_active_stream`, not process memory. Failed/blocked turns count toward turn quota but only actual measured tokens count toward token quota.

---

# 11. Memory summarization lock

Strategy code:

```text
summary-v1
```

Create or refresh memory when any condition is true:

```text
- 20 completed USER→ASSISTANT turns exist after the last active summary; or
- unsummarized estimated tokens reach 12,000; or
- the next turn would exceed the 24,000-token input budget.
```

Summary rules:

```text
- Keep the latest 8 USER/ASSISTANT messages verbatim outside the summary.
- Summary target maximum is 2,000 tokens.
- Summary contains only user-visible content.
- TOOL_REQUEST/TOOL_RESULT raw payloads are excluded.
- Summary is not project truth.
```

Invalidate active summary when:

```text
- actor permission signature changes;
- workspace/project access is revoked;
- a cited source becomes inaccessible;
- retention/redaction removes covered content;
- the conversation is deleted;
- summary hash or covered sequence becomes inconsistent.
```

A stale/invalidated summary must not be injected into a new prompt until rebuilt.

---

# 12. Retention lock

Defaults, subject to stricter Phase 38 policy or legal hold:

```text
Conversation/message/context/citation retention: 180 days after last activity
Soft-deleted conversation purge: within 30 days
Stream event retention: 24 hours
Tool-call safe snapshots: 30 days
Retrieval trace reference retention: 30 days
Answer feedback retention: 365 days
```

Delete behavior:

```text
- DELETE endpoint performs repository-standard soft delete immediately.
- User-visible content becomes unavailable immediately.
- Physical purge is asynchronous and legal-hold aware.
- Audit/event records retain IDs and redacted metadata only.
```

---

# 13. Citation persistence lock

Citation model:

```text
Normalized child table: aiassistant_message_citation
```

Do not store citations only as an unvalidated JSON column on `aiassistant_message`.

Every citation must persist:

```text
messageId
ordinal
retrievalTraceId
knowledgeChunkId
sourceType
sourceRefId
sourceVersionRefId
title
headingPath
bounded quoted fragment
appRoute
permissionSignature at generation time
accessValidatedAt/accessValidationResult
```

Before persistence and before returning an old citation route, the server revalidates source access. A past `ALLOWED` result is evidence of generation-time validation, not a permanent access grant.

---

# 14. Tool transcript lock

`TOOL_REQUEST` and `TOOL_RESULT` are persisted as bounded safe message records and normalized in `aiassistant_tool_call`.

Never persist:

```text
provider secret
presigned URL
raw ACL token list
raw embedding
full source documents
private chain-of-thought
unredacted stack trace
```

The tool result snapshot stores identifiers, ranks, safe snippets, counts, truncation, hashes, and retrieval trace ID only.

---

# 15. Read-only boundary

Allowed:

```text
Guide/explain
Grounded search and answer
Read-only comparison/summarization
Navigation link generation
Disabled-action explanation
Suggested questions
Feedback creation
Conversation lifecycle management
```

Forbidden:

```text
Create/update/delete project business entities
Change status/date/assignee
Send notifications/messages
Approve/finalize/lock
Invoke any mutation tool
Claim a mutation occurred
```

---

# 16. Error and response lock

Use the existing repository `ErrorResponse` and `AppException` pipeline.

Phase 42 adds `AiAssistantErrorCatalog`; it does not create a new error wrapper.

Success responses follow the repository's current success-wrapper convention if one exists.

---

# 17. IAM/Event initializer lock

Initializer classes:

```text
AiAssistantPermissionInitializer
AiAssistantEventDefinitionInitializer
```

Tool definition/handler registration:

```text
- `knowledge.search` remains Knowledge-owned.
- Phase 42 verifies it is executable through the existing registry.
- Do not duplicate the tool definition under aiassistant.
```

Exact codes and event variables are defined in `PHASE_42_IAM_EVENT_SEED_CATALOG_REPO_ALIGNED.md`.

---

# 18. Definition of Ready

Phase 42 coding may start only after:

```text
[ ] Phase 41 `knowledge.search` contract is available.
[ ] Existing AiTool registry execution interfaces are inspected.
[ ] Stub/NO_OP behavior is identified and replacement location is confirmed.
[ ] Existing ErrorResponse/AppException fields are inspected.
[ ] Existing IAM/Event initializer patterns are inspected.
[ ] V97/V98 are free or atomically renumbered.
[ ] Spring MVC is confirmed as the active web stack.
[ ] Existing executor/scheduling conventions are inspected.
[ ] Phase 38 retention/legal-hold integration points are identified.
```

---

# 19. Acceptance lock

Phase 42 is not complete unless:

```text
- all V97/V98 migrations apply cleanly;
- all REST contracts are implemented;
- SseEmitter replay/reconnect/cancel works;
- `knowledge.search` executes through the AiTool registry live handler;
- every project factual answer has validated citations;
- tool traces are bounded and safe;
- memory trigger/invalidation rules are tested;
- quota and retention rules are enforced;
- no mutation tool is callable;
- mvn compile and mvn test pass;
- completion file documents current-vs-TO-BE evidence.
```
