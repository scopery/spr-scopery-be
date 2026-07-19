# Phase 43 — Repository Gap Closure Matrix

> Status: **Locked based on current known repository facts supplied during review. The coding agent must re-run repository searches before implementation.**

| # | Capability | Current repository | Phase 43 action | Artifact | Status |
|---:|---|---|---|---|---|
| 1 | General recommendation module | missing | create `modules/airecommendation` | ADR-043 | LOCKED |
| 2 | Phase 21 planning suggestions | `modules/aiplanning`, `ai_planning_*` present | preserve and adapt reads | Phase 21 compatibility | LOCKED |
| 3 | Common suggestion persistence | missing | V99 tables | V99 | LOCKED |
| 4 | Registry persistence | missing | V100 tables + initializer | V100/schema registry | LOCKED |
| 5 | Flyway sequence | V95–V98 reserved by 41/42 | use V99/V100; renumber atomically if occupied | ADR/checklist | LOCKED |
| 6 | REST contracts | planning paths only | full JSON contracts/error alignment | API contract | LOCKED |
| 7 | Suggestion schemas | Phase 21 freeform JSON | six versioned schemas | schema registry | LOCKED |
| 8 | MVP pack scope | broad planning list | three packs/five detectors + compatibility | packs | LOCKED |
| 9 | Retrieval | Phase 41 `knowledge.search` | registry invocation only | orchestrator | LOCKED |
| 10 | Chat linkage | Phase 42 conversation/citation model | optional ID linkage, independent revalidation | orchestrator | LOCKED |
| 11 | Live LLM generation | Phase 21 deferred/unknown | disabled by default | ADR/packs | LOCKED |
| 12 | Confidence model | Phase 21 label string | numeric + label + method | ADR/DDL | LOCKED |
| 13 | Impact model | missing/common unclear | normalized impact rows | ADR/V99 | LOCKED |
| 14 | Dedup | missing/general unclear | hash + partial unique index + algorithm | dedup/V99 | LOCKED |
| 15 | Suppression/cooldown | missing | actor-scoped suppression and defaults | dedup/V99 | LOCKED |
| 16 | Staleness | missing | target token/evidence revalidation | dedup | LOCKED |
| 17 | Accept vs apply | Phase 21 apply mutates | Phase 43 accept is review-only | compatibility/handoff | LOCKED |
| 18 | Phase 44 handoff | missing | controlled unavailable port + reserved tool | handoff | LOCKED |
| 19 | IAM catalog | rights absent | idempotent initializer | IAM/Event catalog | LOCKED |
| 20 | Event catalog | definitions absent | idempotent initializer/outbox | IAM/Event catalog | LOCKED |
| 21 | Work inbox | existing capability assumed | use adapter, no new table | IAM/Event catalog | LOCKED |
| 22 | SSE | Phase 42 only | polling canonical; no new Phase 43 SSE | SSE decision | LOCKED |
| 23 | Artifact index/checklist | missing | create package | index/checklist | CLOSED |
| 24 | Exact target domain authority names | repository-specific | resolve through target capability port; inspect actual actions | schema/checklist | IMPLEMENTATION CHECK |

Result:

```text
23 design/document gaps closed.
1 repository-specific mapping intentionally delegated to a typed resolver and pre-code verification; no agent may bypass the real domain authorization action.
```
