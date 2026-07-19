# Phase 42 — Repository-Aligned Gap Closure Matrix

> Baseline: user-supplied repository inspection plus Phase 41 repo-aligned artifacts.  
> Conflict rule: ADR-042 wins over Phase 42 planning text.

| # | Gap / repository finding | Locked resolution | Artifact |
|---:|---|---|---|
| 1 | Phase 42 only has one planning document | Create implementation-ready Phase 42 package | this folder |
| 2 | Module name ambiguous: aichat/assistant/copilot | `modules/aiassistant` | ADR-042 §1–3 |
| 3 | Table prefix may conflict with `aiagent_*` | `aiassistant_*` | ADR-042 §1,4 |
| 4 | API base not locked beyond examples | `/api/ai-assistant` | ADR-042 §1 |
| 5 | No Flyway DDL | V97/V98 exact DDL | SQL artifacts |
| 6 | Flyway version unspecified after V94 | Phase 41 reserves V95/V96; Phase 42 uses V97/V98 | ADR-042 §4 |
| 7 | Package layout could ignore repo convention | entity submodule HTTP + application action/service; no interfaces/rest | ADR-042 §3 |
| 8 | No AiConversation/AiMessage persistence in code | normalized conversation/message tables | V97 |
| 9 | Tool transcript persistence unclear | message roles + `aiassistant_tool_call` bounded snapshots | ADR-042 §14 + V97 |
| 10 | Citation model unclear | normalized child table with generation-time access validation | ADR-042 §13 + V97 |
| 11 | Memory summary trigger/invalidation unclear | 20 turns / 12k tokens / context overflow; permission/source/retention invalidation | ADR-042 §11 |
| 12 | Retention values unclear | 180d conversation, 30d purge, 24h stream, 30d tool trace, 365d feedback | ADR-042 §12 |
| 13 | Quota values unclear | 200 turns/day, 500k tokens/day, 2 active streams, 24k/2k budget | ADR-042 §10 |
| 14 | No request/response schema for endpoints | exact JSON contracts for all §8 endpoints | API contract |
| 15 | Error shape could diverge | reuse existing ErrorResponse/AppException | ADR-042 §16 + API §1 |
| 16 | SSE mechanism not chosen | Spring MVC `SseEmitter`; no WebFlux | ADR-042 §9 + SSE contract |
| 17 | SSE event payloads missing | exact event JSON and state machine | SSE contract |
| 18 | Reconnect/resume unspecified | Last-Event-ID + durable stream-event replay | SSE contract §§5–6 + V98 |
| 19 | Cancellation race unspecified | `CANCEL_REQUESTED` state and deterministic winner | SSE contract §7 |
| 20 | WebSocket unclear | explicitly deferred to Phase 44 | ADR-042 §9 |
| 21 | Client page/action context could grant access | server-side override and deterministic context hash | Context Builder contract |
| 22 | Context payload fields not locked | route/page/entity/action/tab/fields/actions/hash exact contract | Context Builder contract |
| 23 | AiTool call path unclear | registry-only execution | ADR-042 §6 + Orchestrator contract |
| 24 | `knowledge.search` tool code could drift | exact tool code locked | ADR-042 §6 |
| 25 | Tool registry execution is mostly stub/NO_OP | implement one live read-only handler | Orchestrator contract §6 |
| 26 | Live handler owner/location unclear | Knowledge-owned `KnowledgeSearchAiToolHandler` | ADR-042 §6 |
| 27 | Direct Phase 41 service call risk | orchestrator direct bypass forbidden | Orchestrator contract |
| 28 | LLM prompt/profile unspecified | `SCOPERY_CONTEXTUAL_ASSISTANT_V1`, existing Phase 07 provider port | ADR-042 §8 |
| 29 | Grounding/citation finalization unclear | structured output + citation revalidation transaction | Orchestrator §§8–11 |
| 30 | IAM rights only listed in planning | exact authority codes + initializer class | IAM/Event catalog |
| 31 | Event codes only listed informally | exact source system, codes, variables, safety | IAM/Event catalog |
| 32 | Tool/stream content could leak secrets | explicit forbidden fields and bounded snapshots | ADR-042 §§13–14 + contracts |
| 33 | Current normal history may expose tool messages | normal history excludes TOOL roles | API §10 |
| 34 | No current-vs-TO-BE classification | classified here and in detailed spec | this matrix |
| 35 | Completion can be claimed without runtime evidence | pre-code checklist + acceptance lock + completion template | checklist + detailed spec |

## Readiness result

```text
Architecture/MVP decisions: LOCKED
DDL: LOCKED, subject to actual V97/V98 availability
REST contract: LOCKED
SSE contract: LOCKED
Context Builder: LOCKED
AiTool registry integration: LOCKED
IAM/Event catalog: LOCKED
Repo implementation: NOT YET IMPLEMENTED
```

Phase 42 is implementation-ready after the Pre-Code Checklist confirms actual repository interfaces/class names and migration numbers.
