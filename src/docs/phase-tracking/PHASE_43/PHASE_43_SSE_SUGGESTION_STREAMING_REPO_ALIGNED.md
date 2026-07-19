# Phase 43 — Suggestion Streaming Decision — Repository-Aligned

> Status: **Accepted / locked as deferred for MVP**

---

# 1. Decision

Phase 43 does **not** introduce a new mandatory SSE endpoint, replay table, or WebSocket channel.

Canonical MVP interaction:

```text
POST project run → 202 with runId
GET run → poll status
GET project suggestions → fetch persisted results
```

Reasons:

```text
- Phase 42 already owns chat SSE infrastructure.
- Recommendation runs are durable background-style jobs.
- Adding a second replay/state machine would enlarge Phase 43 without improving correctness.
- Suggestions must be persisted before users depend on them.
```

---

# 2. Chat-triggered progress

When Phase 42 calls `recommendation.generate`, Phase 42 may stream generic tool progress using its existing SSE contract. Phase 43 does not emit token deltas or create a separate client connection.

Allowed bounded progress metadata:

```text
runId
status
detector counts
persisted/deduplicated/suppressed counts
```

Forbidden:

```text
candidate payloads before validation
raw evidence text
LLM reasoning
one event per candidate/chunk
```

---

# 3. Future optional endpoint

A future ADR may add:

```text
GET /api/ai-recommendations/runs/{runId}/stream
```

It must reuse Spring MVC `SseEmitter` and Phase 42 sequence/reconnect/error conventions or a shared streaming abstraction. It is not part of Phase 43 acceptance criteria.

---

# 4. Tests

```text
runCreation_returnsPollableRunId
runCompletion_persistsBeforeStatusSucceeded
phase43_hasNoRequiredSseDependency
chatToolProgress_containsOnlyBoundedMetadata
```
