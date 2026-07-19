# Phase 44 — Phase 21 / Phase 43 Apply Compatibility — Repository-Aligned

> Status: **Accepted / implementation-blocking**

---

# 1. Ownership

```text
Phase 21 modules/aiplanning:
- retains ai_planning_* records and legacy APIs;
- retains legacy apply behavior for existing clients;
- is not silently migrated or deleted.

Phase 43 modules/airecommendation:
- owns suggestion review/accept/reject/edit/suppress;
- ACCEPT does not mutate domain state;
- delegates PREPARE_APPLY through RecommendationApplyPreparationPort.

Phase 44 modules/aiaction:
- owns request/plan/preview/confirmation/execution;
- is the canonical apply path for p43 recommendations;
- never calls the legacy Phase 21 apply endpoint.
```

---

# 2. `p43:<uuid>` handoff

Requirements:

```text
suggestion exists and is visible
status is ACCEPTED or EDITED
not EXPIRED/STALE/SUPPRESSED/REJECTED/SUPERSEDED
items conform to active Phase 43 schema
required user-provided fields are non-null
all evidence/target access revalidates
mapping to an ACTIVE Phase 44 tool exists
```

Success:

```json
{
  "suggestionRef": "p43:uuid",
  "actionRequestId": "uuid",
  "actionPlanId": "uuid",
  "planStatus": "WAITING_CONFIRMATION",
  "planHash": "aplan:v1:sha256:...",
  "requiresConfirmation": true
}
```

Only after durable persistence may Phase 43 emit `AI_SUGGESTION_READY_TO_APPLY`.

---

# 3. `p21:<uuid>` compatibility

A Phase 21 compatibility adapter may produce a Phase 44 plan only when:

```text
- legacy type has an explicit allowlisted mapper;
- masked legacy payload maps fully to an active Phase 44 input schema;
- expected target versions can be resolved;
- required permissions and baseline rules pass;
- no unsupported freeform operation remains.
```

Otherwise return:

```text
AI_ACTION_LEGACY_PAYLOAD_UNSUPPORTED
```

No generic JSON-to-command reflection is allowed.

---

# 4. Legacy endpoint behavior

```text
- Existing Phase 21 apply endpoint remains unchanged in Phase 44.
- Phase 43 and Phase 44 UIs must prefer the Phase 44 prepare/confirm/execute flow for new generalized suggestions.
- No database migration copies ai_planning_* into ai_action_*.
- Historical Phase 21 result/audit links remain readable through Phase 21 APIs.
```

---

# 5. Status synchronization

Phase 44 final outcome may update Phase 43 through a port/event:

```text
plan created → READY_TO_APPLY metadata/link
execution SUCCEEDED → APPLIED outcome metadata
execution PARTIAL/FAILED → APPLY_PARTIAL/APPLY_FAILED outcome metadata
plan cancelled/expired/stale → no suggestion review-state rewrite; expose action outcome separately
```

Do not overload Phase 43 review status with execution state unless a future migration explicitly adds fields. Prefer an outcome/read-model link.
