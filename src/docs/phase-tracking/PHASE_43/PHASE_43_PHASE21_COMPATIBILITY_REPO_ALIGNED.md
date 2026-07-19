# Phase 43 — Phase 21 Compatibility Contract — Repository-Aligned

> Status: **Accepted / implementation-blocking**  
> Purpose: preserve Phase 21 planning history and APIs while generalizing future recommendations without data loss or double mutation.

---

# 1. Ownership

```text
Phase 21 owner: modules/aiplanning
Phase 21 storage: ai_planning_*
Phase 21 API: /api/projects/{projectId}/ai-planning/...
Phase 43 owner: modules/airecommendation
Phase 43 storage: ai_recommendation_*
Phase 43 API: /api/ai-recommendations/...
```

Phase 43 does not rename, move, migrate, or drop Phase 21 tables.

---

# 2. Suggestion reference format

```text
p43:<uuid> → ai_recommendation_suggestion.id
p21:<uuid> → ai_planning_suggestion.id
```

REST controllers parse the prefix before lookup. Missing/unknown prefixes return `AI_SUGGESTION_REFERENCE_INVALID`.

---

# 3. Read compatibility

Implement:

```text
Phase21SuggestionCompatibilityQueryService
Phase21SuggestionCompatibilityMapper
```

The new general recommendation list/detail API may include Phase 21 historical suggestions when:

```text
includeLegacyPhase21=true
```

Default:

```text
project suggestion list: true
entity suggestion list: true when target mapping exists
analytics: false unless explicitly requested
```

No duplicate Phase 43 row is created for read-only compatibility.

---

# 4. Field mapping

| Phase 21 field | Phase 43 common response |
|---|---|
| `id` | `suggestionRef = p21:<id>` |
| project/workspace scope | unchanged after authorization |
| planning suggestion type | mapped to `PHASE21_PLANNING_PROPOSAL` plus `legacyType` |
| `confidence_label` | HIGH=0.9000, MEDIUM=0.7000, LOW=0.5000; method `LEGACY_MAPPED` |
| `proposed_payload_json` | one compatibility item; schema `PHASE21_PLANNING_PROPOSAL` v1 |
| `source_references_json` | bounded legacy evidence entries; no claim that they are Phase 41 citations |
| review status | mapped to common status where semantics match |
| apply status | exposed as `legacyApplyState`; never treated as Phase 43 acceptance/application state |
| review/apply history | read from Phase 21 service; no copy required |

Unknown Phase 21 values must be surfaced as `legacyMetadata` and must not be silently reinterpreted.

---

# 5. State/action compatibility

## New Phase 43 endpoints for `p21:` references

| Operation | Behavior |
|---|---|
| GET detail/list | supported through adapter |
| view | call Phase 21 review/view action if available; otherwise emit Phase 43 view audit without mutating planning data |
| accept | map to Phase 21 review ACCEPT only if Phase 21 has a non-applying review action |
| reject | map to Phase 21 review REJECT if available |
| edit | map only when Phase 21 supports version-safe edit; otherwise `AI_LEGACY_SUGGESTION_EDIT_UNAVAILABLE` |
| suppress | unsupported in MVP; return `AI_LEGACY_SUGGESTION_SUPPRESSION_UNAVAILABLE` |
| feedback | stored in Phase 43 feedback only if a Phase 43 compatibility suggestion row exists; otherwise use legacy feedback capability if present |
| prepare-apply | unavailable until Phase 44 compatibility adapter exists |

## Legacy Phase 21 apply endpoint

```text
- remains available for backward compatibility;
- is marked deprecated in API documentation;
- is never invoked by Phase 43;
- must retain its existing IAM, validation, baseline/change-request, and idempotency rules;
- must not be exposed as “accept” in the new UI.
```

New suggestions generated after Phase 43 activation are persisted in Phase 43 tables and cannot be applied through the Phase 21 endpoint.

---

# 6. Generation coexistence

```text
- Phase 21 historical generation may remain enabled during migration rollout.
- Once Phase 43 planning-hygiene pack is enabled, duplicate planning generation must be controlled by feature flag.
- Do not dual-write one recommendation to ai_planning_* and ai_recommendation_*.
- Preferred target: Phase 21 planning-specific rich proposal generation remains Phase 21; Phase 43 consumes it through compatibility until a later ADR generalizes its producer.
```

Feature flags:

```text
scopery.ai.recommendation.phase21-read-compat-enabled=true
scopery.ai.recommendation.phase21-write-bridge-enabled=false
scopery.ai.recommendation.phase21-legacy-apply-visible=true
```

---

# 7. Evidence compatibility

Phase 21 `source_references_json` is not automatically a trusted citation.

Mapping:

```text
- If a source reference can be revalidated through knowledge.search/current source authorization,
  expose it as authorized common evidence.
- Otherwise expose only a redacted legacy reference label.
- Never copy inaccessible source text into Phase 43.
- Permission is rechecked on every read.
```

---

# 8. Required tests

```text
phase21HistoricalSuggestion_readableThroughLegacyApi
phase21HistoricalSuggestion_readableThroughCommonApi
phase21Mapping_preservesLegacyTypeAndPayload
phase21ConfidenceLabel_mapsDeterministically
phase21SourceReference_revalidatedBeforeExposure
phase43Accept_neverInvokesPhase21Apply
phase43GeneratedSuggestion_notVisibleToPhase21Apply
legacyApplyEndpoint_remainsBackwardCompatible
legacyUnknownStatus_notSilentlyReinterpreted
suggestionReferencePrefix_preventsCrossStoreCollision
```
