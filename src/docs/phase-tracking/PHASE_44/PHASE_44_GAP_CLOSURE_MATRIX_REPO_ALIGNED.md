# Phase 44 — Gap Closure Matrix — Repository-Aligned

> Status: **Accepted / pre-code gate**  
> Current-state statements must be reverified against the mounted repository immediately before implementation.

| Capability | Current repository expectation | Phase 44 action | Classification |
|---|---|---|---|
| AiTool registry | existing in `modules/aiagent`; execution largely stub/NO_OP for mutations | reuse, add live prepare/status handlers and Phase 44 adapter policy | MUST_HARDEN |
| General action module | missing | create `modules/aiaction` | MUST_IMPLEMENT |
| Phase 41 retrieval storage | PostgreSQL `knowledge_chunk` with pgvector/tsvector via V123 | reuse only through knowledge.search | REUSE_HARD_BOUNDARY |
| Phase 41 retrieval implementation | HybridRetrievalService + NamedParameterJdbcTemplate + Java RRF | do not duplicate/directly inject | REUSE_HARD_BOUNDARY |
| Phase 41 indexing implementation | PostgresKnowledgeIndexService + KnowledgeSourceIndexingService | no Phase 44 writes to embedding/search_vector | REUSE_HARD_BOUNDARY |
| Elasticsearch | removed from repository | must not be reintroduced | FORBIDDEN |
| Phase 43 prepare-apply | reserved port/tool | implement real request/plan handoff | MUST_IMPLEMENT |
| Phase 21 apply | existing legacy behavior | preserve; never invoke from Phase 44 | COMPATIBILITY |
| Domain Application Actions | present per feature, exact names vary | adapters call typed Actions only | MUST_INTEGRATE |
| Request/plan/preview/confirmation persistence | missing | V124 | MUST_IMPLEMENT |
| Durable execution/step/compensation | missing | V124 | MUST_IMPLEMENT |
| Tool/action schema and risk policy | missing | V102 + initializer | MUST_IMPLEMENT |
| WebSocket/STOMP | no implementation expected | add Spring WebSocket/STOMP | MUST_IMPLEMENT |
| Redis | already present | live fan-out only | REUSE |
| Durable realtime replay | missing | ai_action_execution_event | MUST_IMPLEMENT |
| Pause/resume/cancel commands | missing | durable command + REST/STOMP | MUST_IMPLEMENT |
| Audit/outbox/idempotency | platform foundation exists | reuse adapters/conventions | MUST_INTEGRATE |
| Direct DB/repository execution | must remain absent | deny by architecture/tests | HARD_BOUNDARY |
| Compensation | no generic support | tool-specific, best effort | MUST_IMPLEMENT_MINIMAL |
| Critical/destructive tools | not approved | forbidden/deferred | NOT_IN_SCOPE |
| External side effects | provider-dependent | no MVP active external tool | DEFERRED |
| Multi-instance correctness | Redis + PostgreSQL available | DB authoritative, Redis notification | MUST_IMPLEMENT |
| Phase 45 governance/evaluation | future | expose metrics/audit hooks only | DEFERRED_PHASE45 |

---

# Closure rule

A row is closed only when the implementation commit links:

```text
code path
migration/initializer
unit/integration/security test
completion evidence
```

Planning text alone does not close a gap.
