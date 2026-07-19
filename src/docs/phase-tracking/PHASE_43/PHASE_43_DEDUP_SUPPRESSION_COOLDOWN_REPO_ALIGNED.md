# Phase 43 — Deduplication, Suppression, Cooldown, Expiry & Staleness — Repository-Aligned

> Status: **Accepted / implementation-blocking**

---

# 1. Canonicalization

Before hashing:

```text
- UTF-8
- object keys sorted lexicographically
- arrays retain semantic order
- UUIDs lowercase canonical form
- timestamps UTC ISO-8601 with millisecond precision
- decimal numbers normalized without insignificant trailing zeros
- absent optional fields omitted; explicit null retained only when schema permits
- sensitive fields masked before canonicalization
```

---

# 2. Payload hash

```text
payloadHash = lowercase hex SHA-256(canonical proposed payload JSON bytes)
```

Stored as 64 hexadecimal characters.

---

# 3. Dedup key

Canonical object:

```json
{
  "workspaceId": "uuid",
  "projectId": "uuid",
  "suggestionType": "type",
  "targetEntityType": "type",
  "targetEntityId": "uuid",
  "schemaCode": "code",
  "schemaVersion": 1,
  "normalizedProposedPayload": {}
}
```

```text
dedupKey = "rec:v1:sha256:" + sha256(canonical object)
```

The pack/detector is deliberately excluded so equivalent findings from different detectors converge.

---

# 4. Atomic duplicate handling

Use the partial unique index on active statuses. Required algorithm:

```text
1. Build candidate and dedup key.
2. Query active row by workspace/dedup key.
3. If none, insert.
4. If concurrent insert wins elsewhere, catch the unique violation and reload.
5. For duplicate: increment occurrence_count, set last_observed_at, merge new evidence, preserve original reviews.
6. Do not reset VIEWED/EDITED/ACCEPTED status to GENERATED.
7. Do not extend expiry beyond the detector default unless target/source version changed materially.
```

---

# 5. Cooldown

Cooldown prevents regeneration after terminal review.

| Severity | Default |
|---|---:|
| INFO | 14 days |
| WARNING | 7 days |
| HIGH | 3 days |
| CRITICAL | 24 hours |

After `REJECTED`, `SUPPRESSED`, or `EXPIRED`, a matching candidate is discarded during cooldown unless:

```text
- target version token changed materially; or
- severity increased; or
- detector schema version increased; or
- an administrator revoked the suppression.
```

---

# 6. Suppression

Suppression key canonical input:

```json
{
  "workspaceId": "uuid",
  "projectId": "uuid",
  "actorId": "uuid",
  "scopeType": "TARGET|TYPE|PACK",
  "scopeKey": "canonical-scope-key"
}
```

```text
suppressionKey = "recsup:v1:sha256:" + sha256(canonical input)
```

Scopes:

```text
TARGET: targetEntityType + targetEntityId + optional suggestionType
TYPE: suggestionType
PACK: packCode
```

Rules:

```text
- maximum duration 90 days;
- suppression is actor-specific in MVP;
- workspace-wide administrative suppression is deferred;
- critical + registry nonSuppressible=true cannot be suppressed;
- MVP registry seeds no critical/non-suppressible detector;
- suppression never hides an existing suggestion from auditors with proper authority; it prevents normal regeneration/display according to scope.
```

---

# 7. Expiry

Expiry is set from detector definition at creation. A scheduled cleanup marks active rows `EXPIRED` after `expires_at`; it does not delete them.

```text
cleanup owner: RecommendationExpiryJob
minimum cadence: hourly
idempotent batch size: 500
```

Event emitted once: `AI_SUGGESTION_EXPIRED`.

---

# 8. Target version tokens

`RecommendationSourceVersionResolver` returns a stable opaque token:

```text
<entity-type>:v1:<domain-version-or-hash>
```

Examples:

```text
TASK:v1:42
MEETING_ACTION:v1:sha256:<hash>
PROJECT_PLAN:v1:<version>
```

The token contains no secret data. It must change when fields material to the detector or proposed item change.

---

# 9. Staleness algorithm

Run before:

```text
GET detail (lazy revalidation when token is cheap)
view
edit
accept
prepare-apply
```

Algorithm:

```text
1. Reauthorize target read.
2. Resolve current target token.
3. Compare to persisted target_version_token and item expected tokens.
4. Revalidate evidence access.
5. If material token differs, mark STALE with reason TARGET_VERSION_CHANGED.
6. If no valid evidence remains, mark STALE with reason EVIDENCE_NO_LONGER_ACCESSIBLE.
7. If target is deleted, mark STALE with reason TARGET_NOT_FOUND.
8. Return AI_SUGGESTION_STALE for review/prepare operations.
```

Historical content is retained subject to retention/privacy policy; unauthorized evidence is redacted at read time.

---

# 10. Supersession

A new candidate may supersede a terminal/active row when:

```text
- schema version is newer; or
- target version materially changed; or
- proposed payload materially changed; or
- severity increased.
```

Persist bidirectional IDs and mark old row `SUPERSEDED`. Never create loops; a suggestion may have one direct successor.

---

# 11. Required tests

```text
canonicalJson_stableAcrossMapOrdering
dedupKey_equivalentDetectorsConverge
concurrentDuplicate_onlyOneActiveSuggestion
acceptedDuplicate_preservesAcceptedStatus
rejectedSuggestion_notRegeneratedDuringCooldown
materialTargetChange_bypassesCooldownAndSupersedes
suppression_actorScoped
suppression_longerThan90DaysRejected
criticalNonSuppressible_rejected
expiredSuggestion_markedOnce
permissionLoss_stalesSuggestionWhenNoEvidenceRemains
```
