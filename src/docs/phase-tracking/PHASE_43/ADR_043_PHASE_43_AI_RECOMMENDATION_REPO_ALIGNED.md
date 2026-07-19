# ADR-043 — Phase 43 AI Recommendation & Suggestion Engine — Repository-Aligned Lock

> Status: **Accepted / implementation-blocking**  
> Date: 2026-07-17  
> Applies to: Phase 43 implementation and Phase 44 consumers  
> Repository baseline: Phase 41 reserves `V95/V96`; Phase 42 reserves `V97/V98`; existing `modules/aiplanning`, `modules/aiassistant`, `modules/aiagent`, `ErrorResponse`, `AppException`, audit/outbox/idempotency conventions  
> Conflict rule: **this ADR wins over Phase 43 planning text. Compile-safe repository code, `CLAUDE.md`, and `Coding_convention.md` win over package examples.**

---

# 1. Decision summary

```text
Module owner: modules/airecommendation
Table prefix: ai_recommendation_
REST API base: /api/ai-recommendations
Registered Phase 43 tool code: recommendation.generate
Required retrieval tool code: knowledge.search
Retrieval invocation: AiTool registry only; no direct search/repository bypass
Phase 21 owner retained: modules/aiplanning
Phase 21 tables retained: ai_planning_*
Phase 21 API retained for backward compatibility
New Phase 43 suggestion reference: p43:<uuid>
Legacy Phase 21 suggestion reference: p21:<uuid>
Flyway versions: V99 and V100
MVP generation: deterministic detectors and compatibility adapter
LLM enrichment: optional, disabled by default
MVP packs: TASK_PLANNING_HYGIENE_V1, MEETING_FOLLOW_UP_HYGIENE_V1, PHASE21_PLANNING_COMPAT_V1
Confidence: numeric 0..1 + derived LOW/MEDIUM/HIGH label
Impact: dimension + direction + NUMERIC/QUALITATIVE/UNKNOWN assessment
Dedup key: rec:v1:sha256:<64 lowercase hex>
Suppression key: recsup:v1:sha256:<64 lowercase hex>
Default dedup RRF/search evidence: Phase 41 knowledge.search result/citation model
Accept does not mutate domain state
Prepare-apply handoff: reserved Phase 44 tool code agent.action.prepare
Phase 43 SSE: not an MVP acceptance gate; polling is canonical
```

No coding agent may change the module, table prefix, API base, tool codes, migration versions, Phase 21 coexistence rule, no-mutation boundary, dedup format, confidence scale, MVP pack whitelist, or Phase 44 handoff behavior without a replacement ADR.

---

# 2. Repository precedence

```text
1. Current compile-safe repository code and real table/class names
2. CLAUDE.md / CLAUDE.ms
3. Coding_convention.md
4. ADR-043 repository-aligned lock
5. Phase 43 TO-BE planning document
```

Rules:

```text
- Do not rename, drop, or repurpose ai_planning_* tables in Phase 43.
- Do not move Phase 21 code into modules/airecommendation.
- Do not add recommendation entities to modules/aiassistant.
- Do not create modules/copilot or modules/assistant.
- Do not create a second AiTool registry.
- Do not call Elasticsearch, Knowledge repositories, or domain repositories from recommendation orchestrators.
- Do not create a parallel error envelope.
- Do not store raw chain-of-thought, prompts, unrestricted source text, vectors, secrets, or presigned URLs.
- If V99/V100 are occupied at merge time, renumber both atomically to the next consecutive free versions and update every artifact reference in the same change.
```

---

# 3. Module/package layout lock

Logical owner:

```text
modules/airecommendation
```

Repository-aligned layout:

```text
modules/airecommendation/
├── policy/
│   └── http/controller|request|response
├── run/
│   └── http/controller|request|response
├── suggestion/
│   └── http/controller|request|response
├── feedback/
│   └── http/controller|request|response
├── nextbestaction/
│   └── http/controller|response
├── application/
│   ├── command/
│   ├── action/
│   ├── service/
│   ├── orchestrator/
│   ├── detector/
│   ├── compatibility/
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
    ├── compatibility/
    ├── eventconsumer/
    ├── inbox/
    └── retention/
```

Required flow:

```text
HTTP Controller
→ Request DTO
→ Command/query parameters
→ *Action.execute(Command) for writes
→ *QueryService for reads
→ Response DTO
```

Required application ports:

```text
RecommendationDetector
RecommendationSourceVersionResolver
RecommendationTargetAuthorizationPort
RecommendationEvidenceAccessValidator
RecommendationApplyPreparationPort
Phase21SuggestionCompatibilityPort
RecommendationInboxPublisherPort
```

Provider/module-specific imports remain infrastructure-only.

---

# 4. Database lock

Migration files:

```text
V99__phase_43_ai_recommendation_core.sql
V100__phase_43_ai_recommendation_registry.sql
```

Phase 43 owns:

```text
ai_recommendation_policy
ai_recommendation_run
ai_recommendation_suggestion
ai_recommendation_suggestion_item
ai_recommendation_evidence
ai_recommendation_impact
ai_recommendation_review
ai_recommendation_feedback
ai_recommendation_suppression
ai_recommendation_schema_definition
ai_recommendation_pack_definition
ai_recommendation_detector_definition
ai_recommendation_next_best_action_definition
```

Cross-module identifiers are stored without database foreign keys:

```text
workspace_id, project_id, actor_id
origin_conversation_id, origin_message_id, origin_turn_id
aiassistant_citation_id
knowledge_chunk_id, retrieval_trace_id
legacy_phase21_suggestion_id
source/target entity IDs
```

Foreign keys inside `ai_recommendation_*` are mandatory.

---

# 5. Phase 21 coexistence lock

```text
- Existing Phase 21 records remain in ai_planning_*.
- Existing Phase 21 read APIs remain supported.
- Existing Phase 21 apply endpoint remains legacy-only and is never invoked by Phase 43.
- No bulk data migration from ai_planning_* is required in Phase 43.
- New generalized recommendations are written only to ai_recommendation_*.
- Historical Phase 21 records are exposed through a read compatibility adapter using p21:<uuid> references.
- New Phase 43 records use p43:<uuid> references.
- Phase 43 accept means review acceptance only; it never calls Phase 21 apply.
- Phase 43 prepare-apply requires Phase 44. Before Phase 44 is available it returns a controlled unavailable error.
```

Detailed mapping is locked in `PHASE_43_PHASE21_COMPATIBILITY_REPO_ALIGNED.md`.

---

# 6. MVP recommendation packs

Exactly:

```text
TASK_PLANNING_HYGIENE_V1
MEETING_FOLLOW_UP_HYGIENE_V1
PHASE21_PLANNING_COMPAT_V1
```

Active deterministic detectors:

```text
TASK_MISSING_OWNER
TASK_MISSING_ESTIMATE
TASK_BLOCKED_WITHOUT_MITIGATION
MEETING_ACTION_MISSING_OWNER
MEETING_ACTION_MISSING_DUE_DATE
```

Compatibility adapter:

```text
PHASE21_PLANNING_SUGGESTION_ADAPTER
```

Deferred:

```text
resource/capacity optimization
critical path forecasting
finance/margin recommendations
requirements coverage
support conversion
governance/baseline recommendations
LLM-generated proposed mutations
scheduled and event-driven recommendation runs
```

Phase 43 MVP supports manual project runs. Event and scheduled trigger values remain schema-compatible but disabled until their source event contracts are explicitly mapped and tested.

---

# 7. Retrieval and AiTool lock

All evidence retrieval uses:

```text
AiTool registry → knowledge.search → KnowledgeSearchAiToolHandler
```

Phase 43 must not call Phase 41 services directly. `recommendation.generate` is a read-only registered tool that invokes the Phase 43 orchestrator. The orchestrator may invoke `knowledge.search` only through the registry.

Tool boundaries:

```text
recommendation.generate
- READ_ONLY
- project-scoped
- returns run/suggestion references
- never applies changes

knowledge.search
- READ_ONLY
- source ACL enforced by Phase 41
- returns bounded evidence and citations
```

When a run originates from Phase 42 chat, the run stores conversation/message/turn identifiers. It does not duplicate conversation text.

---

# 8. Suggestion schema and operation lock

MVP suggestion types:

```text
TASK_MISSING_OWNER
TASK_MISSING_ESTIMATE
TASK_BLOCKED_WITHOUT_MITIGATION
MEETING_ACTION_MISSING_OWNER
MEETING_ACTION_MISSING_DUE_DATE
PHASE21_PLANNING_PROPOSAL
```

Operations:

```text
CREATE
UPDATE
LINK
UNLINK
NO_CHANGE_INSIGHT
```

MVP types use `UPDATE` or `NO_CHANGE_INSIGHT`; no Phase 43 operation executes.

Stable target capability codes:

```text
TARGET_TASK_UPDATE
TARGET_MEETING_ACTION_UPDATE
TARGET_PROJECT_PLAN_UPDATE
```

These are recommendation capability-policy codes, not new IAM grants. `RecommendationTargetAuthorizationPort` resolves them to existing domain authorization checks.

Full JSON Schemas and masking rules are in `PHASE_43_SUGGESTION_SCHEMA_REGISTRY_REPO_ALIGNED.md`.

---

# 9. Confidence lock

Persist:

```text
confidence_method: DETERMINISTIC | HEURISTIC | LLM | LEGACY_MAPPED
confidence_value: decimal 0.0000..1.0000
confidence_label: LOW | MEDIUM | HIGH
```

Label derivation:

```text
HIGH   >= 0.8500
MEDIUM >= 0.6500 and < 0.8500
LOW    >= 0.4000 and < 0.6500
Below 0.4000: candidate is discarded and not persisted
```

MVP defaults:

```text
TASK_MISSING_OWNER: 1.0000 DETERMINISTIC
TASK_MISSING_ESTIMATE: 1.0000 DETERMINISTIC
TASK_BLOCKED_WITHOUT_MITIGATION: 0.9500 DETERMINISTIC
MEETING_ACTION_MISSING_OWNER: 1.0000 DETERMINISTIC
MEETING_ACTION_MISSING_DUE_DATE: 1.0000 DETERMINISTIC
Phase 21 HIGH/MEDIUM/LOW: 0.9000/0.7000/0.5000 LEGACY_MAPPED
```

LLM confidence cannot exceed `0.8500` unless a deterministic validator confirms every material field.

---

# 10. Impact lock

Dimensions:

```text
SCHEDULE, COST, REVENUE, MARGIN, QUALITY, RESOURCE, RISK, COMPLIANCE, CLIENT_VISIBILITY
```

Assessment:

```text
assessment_type: NUMERIC | QUALITATIVE | UNKNOWN
direction: INCREASE | DECREASE | NEUTRAL | UNKNOWN
qualitative_magnitude: LOW | MEDIUM | HIGH | UNKNOWN
numeric_value + unit: allowed only for deterministic/domain-calculated evidence
source_method: DETERMINISTIC | DOMAIN_CALC | HEURISTIC | LLM | LEGACY_MAPPED
```

A numeric impact requires a source reference, calculation method code, and assumptions record. Otherwise it must be qualitative or unknown.

---

# 11. Dedup, cooldown, suppression, expiry, and staleness lock

Dedup key:

```text
rec:v1:sha256:<sha256(canonical-json)>
```

Canonical input:

```json
{
  "workspaceId": "uuid",
  "projectId": "uuid",
  "suggestionType": "TASK_MISSING_OWNER",
  "targetEntityType": "TASK",
  "targetEntityId": "uuid",
  "schemaCode": "TASK_MISSING_OWNER",
  "schemaVersion": 1,
  "normalizedProposedPayload": {}
}
```

Active duplicate statuses:

```text
GENERATED, VIEWED, EDITED, ACCEPTED
```

Duplicate behavior:

```text
- Do not insert a second active suggestion.
- Increment occurrence_count.
- Update last_observed_at.
- Merge newly authorized evidence without duplicating citation/source keys.
- Keep the original suggestion ID and review history.
```

Default cooldown:

```text
INFO: 14 days
WARNING: 7 days
HIGH: 3 days
CRITICAL: 24 hours
```

Default expiry:

```text
TASK_MISSING_OWNER: 30 days
TASK_MISSING_ESTIMATE: 30 days
TASK_BLOCKED_WITHOUT_MITIGATION: 7 days
MEETING_ACTION_MISSING_OWNER: 14 days
MEETING_ACTION_MISSING_DUE_DATE: 14 days
PHASE21_PLANNING_PROPOSAL: 30 days
```

Staleness:

```text
- Persist target_version_token at generation.
- Before detail/view/accept/edit/prepare-apply, resolve the current token.
- If material fields differ, mark STALE and reject state-changing review operations.
- Source permission loss hides affected evidence and may mark the suggestion STALE when no valid evidence remains.
```

Suppression:

```text
scope: TARGET | TYPE | PACK
maximum duration: 90 days
suppression key: recsup:v1:sha256:<64 lowercase hex>
non-suppressible: severity CRITICAL and registry nonSuppressible=true
MVP: no detector is seeded as CRITICAL/non-suppressible
```

Algorithm details are in `PHASE_43_DEDUP_SUPPRESSION_COOLDOWN_REPO_ALIGNED.md`.

---

# 12. Lifecycle lock

Suggestion statuses:

```text
GENERATED
VIEWED
EDITED
ACCEPTED
REJECTED
SUPPRESSED
EXPIRED
STALE
SUPERSEDED
```

Allowed transitions:

```text
GENERATED → VIEWED | EDITED | ACCEPTED | REJECTED | SUPPRESSED | EXPIRED | STALE | SUPERSEDED
VIEWED    → EDITED | ACCEPTED | REJECTED | SUPPRESSED | EXPIRED | STALE | SUPERSEDED
EDITED    → ACCEPTED | REJECTED | SUPPRESSED | EXPIRED | STALE | SUPERSEDED
ACCEPTED  → EXPIRED | STALE | SUPERSEDED
```

Terminal for Phase 43 review:

```text
REJECTED, SUPPRESSED, EXPIRED, STALE, SUPERSEDED
```

`ACCEPTED` is not applied. Applied/executed state belongs to Phase 44 execution records.

---

# 13. Prepare-apply handoff lock

Reserved Phase 44 tool code:

```text
agent.action.prepare
```

Phase 43 calls only `RecommendationApplyPreparationPort`.

Before Phase 44 is installed/active:

```text
HTTP 409
errorCode: AI_SUGGESTION_PREPARE_APPLY_UNAVAILABLE
suggestion status remains ACCEPTED
no domain mutation
no fake plan/token is created
```

When Phase 44 becomes active, the port returns a real action-request/plan reference. The handoff request includes suggestion reference, selected item IDs, expected suggestion version, target version tokens, actor/workspace/project scope, and idempotency key.

---

# 14. API and error lock

API contracts are defined in `PHASE_43_API_CONTRACTS_REPO_ALIGNED.md`.

Repository error handling:

```text
throw module AppException/error catalog
use existing ErrorResponse serializer
never return a Phase 43-specific alternative envelope
```

Canonical error example:

```json
{
  "success": false,
  "errorCode": "AI_SUGGESTION_STALE",
  "message": "The suggestion is stale and must be regenerated.",
  "traceId": "trace-id",
  "details": {
    "suggestionRef": "p43:uuid"
  }
}
```

If the real repository `ErrorResponse` has different optional fields, keep its exact class and map the same error code/details semantically.

---

# 15. Dependency gate

Phase 43 code may start only after these compile-safe capabilities exist:

```text
Phase 41:
- knowledge.search registered and backed by a live handler
- evidence source/chunk authorization contract available

Phase 42:
- aiassistant conversation/message/citation IDs available when chat linkage is enabled
- AiTool registry invocation contract available
```

The Phase 21 compatibility adapter can be scaffolded earlier, but Phase 43 completion cannot be claimed before the gates pass.

---

# 16. Acceptance boundary

Phase 43 is implementation-ready only when:

```text
- V99/V100 match the real migration history and compile-safe entities.
- Every REST endpoint has a locked request/response contract.
- Every MVP suggestion type has a versioned JSON Schema.
- Every MVP detector and pack is whitelisted.
- Phase 21 compatibility behavior is tested.
- Dedup/cooldown/suppression/staleness algorithms are deterministic.
- IAM/Event initializers are idempotent.
- Accept/edit/reject/suppress never mutate target domain state.
- prepare-apply returns controlled unavailable behavior until Phase 44 exists.
```
