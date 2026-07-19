# Phase 43 — Recommendation Packs MVP — Repository-Aligned

> Status: **Accepted / implementation-blocking**  
> MVP policy: deterministic/manual first; no unbounded LLM-generated recommendations.

---

# 1. Pack whitelist

## `TASK_PLANNING_HYGIENE_V1`

```text
Trigger: MANUAL
Scope: one authorized project
Required Phase 43 authority: AI_RECOMMENDATION_GENERATE
Target authorization: read access during generation; target capability checked on review/prepare-apply
LLM enrichment: false
Maximum suggestions per run: 100
```

Detectors:

| Detector | Finding | Confidence | Severity | Expiry | Cooldown |
|---|---|---:|---|---:|---:|
| `TASK_MISSING_OWNER` | active task has no owner/assignee | 1.0000 | WARNING | 30d | 7d |
| `TASK_MISSING_ESTIMATE` | schedulable task lacks required estimate | 1.0000 | WARNING | 30d | 7d |
| `TASK_BLOCKED_WITHOUT_MITIGATION` | blocked task has no authorized mitigation/next-step field | 0.9500 | HIGH | 7d | 3d |

Detector rules:

```text
- Inspect actual Task aggregate/query DTO; do not infer fields by name.
- Ignore cancelled/completed/archived tasks.
- A zero estimate is missing only when the domain considers zero invalid/unset.
- A blocked task is detected only from the canonical status/blocker model.
- No candidate owner, estimate, or mitigation text is invented.
```

## `MEETING_FOLLOW_UP_HYGIENE_V1`

```text
Trigger: MANUAL
Scope: one authorized project
LLM enrichment: false
Maximum suggestions per run: 100
```

Detectors:

| Detector | Finding | Confidence | Severity | Expiry | Cooldown |
|---|---|---:|---|---:|---:|
| `MEETING_ACTION_MISSING_OWNER` | open action item has no owner | 1.0000 | WARNING | 14d | 7d |
| `MEETING_ACTION_MISSING_DUE_DATE` | open action item has no due date | 1.0000 | WARNING | 14d | 7d |

Rules:

```text
- Use the real Meeting/Action Item query boundary.
- Ignore completed/cancelled action items.
- If action items are not represented as a stable entity in the repo, mark this detector unavailable and the run PARTIAL; do not parse free text as a substitute in MVP.
```

## `PHASE21_PLANNING_COMPAT_V1`

```text
Execution method: COMPATIBILITY_ADAPTER
Trigger: read/list compatibility, not a new generation job
LLM enrichment: false
Writes Phase 43 rows: false by default
```

Maps authorized Phase 21 historical records into the common response contract.

---

# 2. Deferred packs

```text
REQUIREMENT_TRACEABILITY_V1
QUALITY_RELEASE_READINESS_V1
RESOURCE_CAPACITY_RISK_V1
SCHEDULE_CRITICAL_PATH_V1
FINANCE_MARGIN_V1
GOVERNANCE_BASELINE_V1
DOCUMENT_FRESHNESS_V1
SUPPORT_TRIAGE_V1
```

They require later source adapters, domain calculations, sensitive-field controls, or Phase 44 action mappings. They must not appear ACTIVE in registry seeds.

---

# 3. Trigger lock

MVP:

```text
MANUAL only
```

Accepted schema values but disabled:

```text
CHAT, EVENT, SCHEDULED
```

CHAT may be enabled after Phase 42-to-43 tool integration tests pass. EVENT/SCHEDULED require a replacement or extension ADR naming exact event codes, replay/idempotency behavior, and scheduler ownership.

---

# 4. Run behavior

```text
1. Resolve policy and requested pack codes.
2. Authorize project and source reads.
3. Resolve active detector definitions.
4. Execute detectors independently.
5. Retrieve evidence through knowledge.search where evidence enrichment is needed.
6. Normalize candidates against schema registry.
7. Revalidate target/source access.
8. Compute confidence/impact.
9. Apply dedup/suppression/staleness rules.
10. Persist suggestions and outbox events transactionally.
11. Mark run SUCCEEDED, PARTIAL, FAILED, or CANCELLED.
```

A detector failure does not fail the run when at least one detector completes; status becomes `PARTIAL` and `failedDetectorCount > 0`.

---

# 5. Registry seed payload

```text
AiRecommendationRegistryInitializer
```

Seed exactly:

```text
3 pack definitions
6 detector definitions (5 deterministic + 1 compatibility)
6 schema definitions
7 next-best-action definitions
1 default recommendation policy: PROJECT_RECOMMENDATION_MVP_V1
```

Default policy:

```text
code: PROJECT_RECOMMENDATION_MVP_V1
triggerModes: [MANUAL]
packCodes: [TASK_PLANNING_HYGIENE_V1, MEETING_FOLLOW_UP_HYGIENE_V1]
llmEnrichmentEnabled: false
minConfidence: 0.4000
maxSuggestionsPerRun: 100
publishToInbox: false
```

---

# 6. Tests

```text
taskPack_completedTasksIgnored
taskMissingOwner_detectedDeterministically
taskMissingEstimate_doesNotInventEstimate
blockedTaskWithoutMitigation_detected
meetingPack_unavailableActionEntity_marksRunPartial
phase21Compatibility_doesNotWriteDuplicateRows
manualRun_unknownPackRejected
deferredPack_notActive
llmEnrichment_disabledByDefault
```
