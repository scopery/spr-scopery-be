# Phase 43 — Suggestion Schema Registry — Repository-Aligned

> Status: **Accepted / implementation-blocking**  
> Registry table: `ai_recommendation_schema_definition`  
> Validation: JSON Schema Draft 2020-12 compatible validator already approved by repository dependency policy, or an equivalent compile-safe validator behind `SuggestionPayloadSchemaValidator`.

---

# 1. Registry rules

```text
- A persisted item must reference an ACTIVE schema code/version.
- ACTIVE schema versions are immutable.
- Changes require a new schema version.
- JSON objects are canonicalized before payload hashing.
- Unknown properties are rejected unless the schema explicitly allows them.
- Phase 43 stores proposals only; validation does not execute domain mutations.
- Sensitive values are masked before persistence and response.
```

Stable operations:

```text
CREATE, UPDATE, LINK, UNLINK, NO_CHANGE_INSIGHT
```

Stable target capability codes:

```text
TARGET_TASK_UPDATE
TARGET_MEETING_ACTION_UPDATE
TARGET_PROJECT_PLAN_UPDATE
```

---

# 2. `TASK_MISSING_OWNER` v1

```json
{
  "$schema": "https://json-schema.org/draft/2020-12/schema",
  "$id": "scopery://ai-recommendation/TASK_MISSING_OWNER/1",
  "type": "object",
  "additionalProperties": false,
  "properties": {
    "candidateUserId": {"type": ["string", "null"], "format": "uuid"},
    "candidateDisplayName": {"type": ["string", "null"], "maxLength": 200},
    "assignmentReasonCode": {"type": "string", "enum": ["UNASSIGNED", "CAPACITY_MATCH_UNKNOWN", "MANUAL_SELECTION_REQUIRED"]}
  },
  "required": ["candidateUserId", "assignmentReasonCode"]
}
```

Registry metadata:

```text
operation: UPDATE
targetEntityType: TASK
requiredTargetCapabilityCode: TARGET_TASK_UPDATE
confirmationRequired: true
baselineImpact: POSSIBLE
sensitiveFieldPaths: []
```

MVP detector sets `candidateUserId=null` unless an authorized deterministic assignee resolver exists. It must not invent a person.

---

# 3. `TASK_MISSING_ESTIMATE` v1

```json
{
  "$schema": "https://json-schema.org/draft/2020-12/schema",
  "$id": "scopery://ai-recommendation/TASK_MISSING_ESTIMATE/1",
  "type": "object",
  "additionalProperties": false,
  "properties": {
    "estimateValue": {"type": ["number", "null"], "minimum": 0},
    "estimateUnit": {"type": "string", "enum": ["HOUR", "DAY", "POINT", "UNKNOWN"]},
    "estimationMethod": {"type": "string", "enum": ["MISSING_FIELD_ONLY", "DOMAIN_CALC", "MANUAL_INPUT_REQUIRED"]}
  },
  "required": ["estimateValue", "estimateUnit", "estimationMethod"]
}
```

```text
operation: UPDATE
targetEntityType: TASK
requiredTargetCapabilityCode: TARGET_TASK_UPDATE
confirmationRequired: true
baselineImpact: POSSIBLE
```

MVP detector uses `estimateValue=null`, `estimateUnit=UNKNOWN`, `estimationMethod=MISSING_FIELD_ONLY`; it flags missing data but does not fabricate an estimate.

---

# 4. `TASK_BLOCKED_WITHOUT_MITIGATION` v1

```json
{
  "$schema": "https://json-schema.org/draft/2020-12/schema",
  "$id": "scopery://ai-recommendation/TASK_BLOCKED_WITHOUT_MITIGATION/1",
  "type": "object",
  "additionalProperties": false,
  "properties": {
    "mitigationText": {"type": ["string", "null"], "maxLength": 2000},
    "reviewDate": {"type": ["string", "null"], "format": "date"},
    "ownerUserId": {"type": ["string", "null"], "format": "uuid"},
    "proposalMode": {"type": "string", "enum": ["MANUAL_INPUT_REQUIRED", "AUTHORIZED_TEMPLATE"]}
  },
  "required": ["mitigationText", "reviewDate", "proposalMode"]
}
```

```text
operation: UPDATE
targetEntityType: TASK
requiredTargetCapabilityCode: TARGET_TASK_UPDATE
confirmationRequired: true
baselineImpact: NONE
```

MVP sets manual placeholders unless an authorized template exists.

---

# 5. `MEETING_ACTION_MISSING_OWNER` v1

```json
{
  "$schema": "https://json-schema.org/draft/2020-12/schema",
  "$id": "scopery://ai-recommendation/MEETING_ACTION_MISSING_OWNER/1",
  "type": "object",
  "additionalProperties": false,
  "properties": {
    "actionItemId": {"type": "string", "format": "uuid"},
    "candidateUserId": {"type": ["string", "null"], "format": "uuid"},
    "proposalMode": {"type": "string", "enum": ["MANUAL_SELECTION_REQUIRED", "AUTHORIZED_PARTICIPANT_MATCH"]}
  },
  "required": ["actionItemId", "candidateUserId", "proposalMode"]
}
```

```text
operation: UPDATE
targetEntityType: MEETING_ACTION
requiredTargetCapabilityCode: TARGET_MEETING_ACTION_UPDATE
confirmationRequired: true
baselineImpact: NONE
```

---

# 6. `MEETING_ACTION_MISSING_DUE_DATE` v1

```json
{
  "$schema": "https://json-schema.org/draft/2020-12/schema",
  "$id": "scopery://ai-recommendation/MEETING_ACTION_MISSING_DUE_DATE/1",
  "type": "object",
  "additionalProperties": false,
  "properties": {
    "actionItemId": {"type": "string", "format": "uuid"},
    "proposedDueDate": {"type": ["string", "null"], "format": "date"},
    "proposalMode": {"type": "string", "enum": ["MANUAL_INPUT_REQUIRED", "MEETING_DATE_POLICY"]}
  },
  "required": ["actionItemId", "proposedDueDate", "proposalMode"]
}
```

```text
operation: UPDATE
targetEntityType: MEETING_ACTION
requiredTargetCapabilityCode: TARGET_MEETING_ACTION_UPDATE
confirmationRequired: true
baselineImpact: NONE
```

---

# 7. `PHASE21_PLANNING_PROPOSAL` v1

```json
{
  "$schema": "https://json-schema.org/draft/2020-12/schema",
  "$id": "scopery://ai-recommendation/PHASE21_PLANNING_PROPOSAL/1",
  "type": "object",
  "additionalProperties": false,
  "properties": {
    "legacySuggestionId": {"type": "string", "format": "uuid"},
    "legacyType": {"type": "string", "maxLength": 120},
    "legacyPayload": {"type": "object"},
    "legacyApplyState": {"type": ["string", "null"], "maxLength": 80}
  },
  "required": ["legacySuggestionId", "legacyType", "legacyPayload"]
}
```

```text
operation: NO_CHANGE_INSIGHT
targetEntityType: PROJECT_PLAN
requiredTargetCapabilityCode: TARGET_PROJECT_PLAN_UPDATE
confirmationRequired: true
baselineImpact: UNKNOWN
sensitiveFieldPaths: determined by Phase 21 masking adapter
```

`legacyPayload` is bounded to 64 KB after masking. Unknown or unsafe fields are removed, not blindly copied.

---

# 8. Masking and field safety

Always remove or mask:

```text
credentials/secrets/tokens
raw provider request/response
presigned URLs
private notes not authorized to viewer
salary/rate/margin/cost fields unless current actor has the real finance authority
personal contact fields not needed for the recommendation
raw chain-of-thought
```

Masking occurs before:

```text
payload persistence
before-snapshot persistence
API response
event emission
logs/telemetry
```

---

# 9. Next-best-action registry seeds

Seed exactly these version-1 definitions:

| Code | Kind | Required authority | Target capability | Phase 44 tool | Risk | Status |
|---|---|---|---|---|---|---|
| `OPEN_TARGET` | `NAVIGATE` | `AI_RECOMMENDATION_VIEW` | null | null | LOW | ACTIVE |
| `VIEW_EVIDENCE` | `REVIEW` | `AI_RECOMMENDATION_VIEW` | null | null | LOW | ACTIVE |
| `EDIT_PROPOSAL` | `EDIT` | `AI_RECOMMENDATION_EDIT` | resolved from selected item | null | LOW | ACTIVE |
| `ACCEPT_SUGGESTION` | `ACCEPT` | `AI_RECOMMENDATION_ACCEPT` | resolved from selected item for readiness display only | null | LOW | ACTIVE |
| `REJECT_SUGGESTION` | `REJECT` | `AI_RECOMMENDATION_REJECT` | null | null | LOW | ACTIVE |
| `SUPPRESS_SUGGESTION` | `SUPPRESS` | `AI_RECOMMENDATION_SUPPRESS` | null | null | LOW | ACTIVE |
| `PREPARE_APPLY` | `PREPARE_APPLY` | `AI_RECOMMENDATION_ACCEPT` | resolved from every selected item | `agent.action.prepare` v1 | MEDIUM | RESERVED_PHASE44 |

Rules:

```text
- applicableSuggestionTypes is the six-type MVP whitelist unless a definition is target-specific.
- OPEN_TARGET route is resolved server-side; no arbitrary client URL is stored.
- PREPARE_APPLY remains disabled while the reserved tool is absent/inactive.
- The registry definition does not grant the listed authority or target capability.
- Any new action code/version requires an immutable new registry row and tests.
```

---

# 10. Registry initializer

```text
AiRecommendationRegistryInitializer
```

It idempotently creates the six schema definitions, three packs, six detectors, and next-best-action definitions. It must compare immutable schema content hashes and fail startup in non-production-safe mode when an ACTIVE version has been changed in place.

Required tests:

```text
registryInitializer_firstRun_createsDefinitions
registryInitializer_secondRun_noDuplicates
activeSchema_modifiedInPlace_failsValidation
unknownProperty_payloadRejected
sensitiveFields_maskedBeforePersistence
payloadCanonicalization_producesStableHash
```
