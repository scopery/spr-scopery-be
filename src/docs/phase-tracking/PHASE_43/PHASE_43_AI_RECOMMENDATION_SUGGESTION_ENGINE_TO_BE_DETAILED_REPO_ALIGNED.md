# PHASE 43 — AI Recommendation, Suggestion Engine & Next-Best-Action — Repository-Aligned Implementation Specification

> Project: Scopery Backend  
> Phase: 43  
> Status: **LOCKED / implementation-ready documentation**  
> Module: `modules/airecommendation`  
> Tables: `ai_recommendation_*`  
> API: `/api/ai-recommendations`  
> Flyway: `V99/V100` subject to atomic renumbering if occupied  
> Conflict rule: **ADR-043 wins over this consolidated document. Existing compile-safe repository conventions win over package examples.**

---

# 0. Purpose

Phase 43 implements Level 3 — Suggest:

```text
Detect a concrete issue/opportunity
→ gather authorized evidence
→ produce a structured proposal
→ explain confidence and impact
→ allow view/edit/accept/reject/suppress
→ never mutate the target domain
→ hand accepted proposals to Phase 44 only
```

Phase 21 planning history remains intact and readable through compatibility adapters.

---

# 1. Locked architecture

```text
HTTP / recommendation.generate tool
→ RecommendationRunOrchestrator
→ active policy + pack + detector registry
→ deterministic source adapters
→ AiTool registry → knowledge.search where evidence retrieval is needed
→ schema validation
→ target/source authorization
→ confidence/impact
→ dedup/suppression/staleness
→ ai_recommendation_* persistence + outbox
→ project/entity list and review APIs
```

Dependencies:

```text
Phase 41: live knowledge.search + evidence authorization
Phase 42: AiTool invocation and optional conversation/message/citation linkage
Phase 21: compatibility read adapter only; no migration/drop
Phase 44: optional prepare-apply port, unavailable in Phase 43 standalone mode
```

---

# 2. Hard boundaries

Allowed:

```text
insight/warning/recommendation
structured proposed payload
reason/evidence/confidence/impact
review lifecycle
next-best-action metadata
```

Forbidden:

```text
create/update/delete target domain object
call Phase 21 apply from Phase 43
send external messages
change permission
approve/finalize baseline
direct repository/JPA/SQL/Elasticsearch access from orchestrator
store chain-of-thought or unrestricted source content
```

---

# 3. Repository artifacts

Read in this order:

```text
1. ADR_043_PHASE_43_AI_RECOMMENDATION_REPO_ALIGNED.md
2. PHASE_43_PHASE21_COMPATIBILITY_REPO_ALIGNED.md
3. V99/V100 SQL
4. PHASE_43_SUGGESTION_SCHEMA_REGISTRY_REPO_ALIGNED.md
5. PHASE_43_RECOMMENDATION_PACKS_MVP_REPO_ALIGNED.md
6. PHASE_43_API_CONTRACTS_REPO_ALIGNED.md
7. PHASE_43_DEDUP_SUPPRESSION_COOLDOWN_REPO_ALIGNED.md
8. PHASE_43_ORCHESTRATOR_INTEGRATION_REPO_ALIGNED.md
9. PHASE_43_PREPARE_APPLY_PHASE44_HANDOFF_REPO_ALIGNED.md
10. PHASE_43_IAM_EVENT_SEED_CATALOG_REPO_ALIGNED.md
11. PHASE_43_PRE_CODE_CHECKLIST_REPO_ALIGNED.md
```

---

# 4. Persistence model

Core:

```text
AiRecommendationPolicy
AiRecommendationRun
AiSuggestion
AiSuggestionItem
AiSuggestionEvidence
AiSuggestionImpact
AiSuggestionReview
AiSuggestionFeedback
AiSuggestionSuppression
```

Registry:

```text
SuggestionSchemaDefinition
RecommendationPackDefinition
RecommendationDetectorDefinition
NextBestActionDefinition
```

Important normalized relationships:

```text
Suggestion 1..* Items
Suggestion 1..* Evidence
Suggestion 0..* Impacts
Suggestion 0..* Reviews
Suggestion 0..* Feedback
```

Cross-module references intentionally have no database FK and are revalidated by application ports.

---

# 5. MVP scope

Packs:

```text
TASK_PLANNING_HYGIENE_V1
MEETING_FOLLOW_UP_HYGIENE_V1
PHASE21_PLANNING_COMPAT_V1
```

Detectors:

```text
TASK_MISSING_OWNER
TASK_MISSING_ESTIMATE
TASK_BLOCKED_WITHOUT_MITIGATION
MEETING_ACTION_MISSING_OWNER
MEETING_ACTION_MISSING_DUE_DATE
PHASE21_PLANNING_SUGGESTION_ADAPTER
```

Manual trigger only. LLM enrichment is disabled by default. Detectors must not invent candidate people, estimates, dates, or mitigation text.

---

# 6. Review lifecycle

```text
GENERATED → VIEWED/EDITED/ACCEPTED/REJECTED/SUPPRESSED/EXPIRED/STALE/SUPERSEDED
VIEWED → EDITED/ACCEPTED/REJECTED/SUPPRESSED/EXPIRED/STALE/SUPERSEDED
EDITED → ACCEPTED/REJECTED/SUPPRESSED/EXPIRED/STALE/SUPERSEDED
ACCEPTED → EXPIRED/STALE/SUPERSEDED
```

Accept response always reports:

```text
domainMutationPerformed=false
```

---

# 7. Confidence and impact

Confidence:

```text
0.0000..1.0000
HIGH >= .85
MEDIUM >= .65
LOW >= .40
below .40 discarded
```

Impact dimensions and numeric restrictions are locked in ADR-043. Numeric values require deterministic/domain calculation evidence.

---

# 8. Dedup and staleness

```text
dedupKey: rec:v1:sha256:<64hex>
suppressionKey: recsup:v1:sha256:<64hex>
active duplicate unique index
actor-scoped suppression, max 90 days
target version token revalidation before review/prepare
hourly expiry job
```

Detailed canonicalization and race behavior are mandatory from the dedup artifact.

---

# 9. API

Canonical endpoints:

```text
POST /api/ai-recommendations/projects/{projectId}/runs
GET  /api/ai-recommendations/runs/{runId}
GET  /api/ai-recommendations/projects/{projectId}/suggestions
GET  /api/ai-recommendations/entities/{entityType}/{entityId}/suggestions
GET  /api/ai-recommendations/suggestions/{suggestionRef}
POST /api/ai-recommendations/suggestions/{suggestionRef}/view
PATCH /api/ai-recommendations/suggestions/{suggestionRef}/edit
POST /api/ai-recommendations/suggestions/{suggestionRef}/accept
POST /api/ai-recommendations/suggestions/{suggestionRef}/reject
POST /api/ai-recommendations/suggestions/{suggestionRef}/suppress
POST /api/ai-recommendations/suggestions/{suggestionRef}/feedback
POST /api/ai-recommendations/suggestions/{suggestionRef}/prepare-apply
GET  /api/ai-recommendations/projects/{projectId}/next-best-actions
```

Polling is canonical. Phase 43 adds no required SSE endpoint.

---

# 10. IAM/events/audit

Use exact rights and events from the seed catalog. Recommendation authorities never grant access to underlying entities/evidence. Cross-module events use outbox. No raw payload/evidence/prompt appears in event variables.

---

# 11. Required tests

```text
generateTaskMissingOwner_success
generateTaskMissingEstimate_doesNotInventValue
generateBlockedWithoutMitigation_withDirectEvidence
meetingDetectorUnavailable_marksRunPartial
suggestionPayload_schemaValid
suggestionWithoutEvidence_rejectedUnlessCompatibilityLabeled
viewerCannotSeeRestrictedEvidence
acceptSuggestion_doesNotMutateDomain
editSuggestion_preservesAuditAndSchema
materialTargetChange_marksStale
duplicateConcurrentRun_onlyOneActiveSuggestion
suppressedSuggestion_notRegeneratedDuringCooldown
phase21Suggestion_mapsToCommonContract
phase43Accept_neverCallsPhase21Apply
prepareApply_beforePhase44_controlledUnavailable
run_idempotent
registryInitializer_idempotent
permissionIsolation_crossWorkspaceDenied
```

Build gates:

```bash
mvn compile
mvn test
```

---

# 12. Acceptance criteria

Phase 43 is accepted only if:

```text
1. Module/tables/API follow ADR-043.
2. V99/V100 run on clean and upgraded DB.
3. Six schemas and three packs are seeded idempotently.
4. All five deterministic detectors are implemented or unavailable detector behavior is explicit/tested.
5. Phase 21 history remains readable; no data migration/drop.
6. Every evidence row is current-access validated.
7. Dedup/cooldown/suppression/staleness behavior is deterministic.
8. Accept/edit/reject/suppress do not mutate target domain state.
9. Phase 44 absence produces controlled prepare-apply error.
10. IAM/Event catalogs are initialized and tested.
11. All API contracts and ErrorResponse behavior match repository conventions.
12. Compile/test/migration gates pass and completion evidence is recorded.
```
