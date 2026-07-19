# Phase 42 — SSE Streaming Contract — Repository-Aligned

> Status: **Accepted / implementation-blocking**  
> Mechanism: Spring MVC `SseEmitter`  
> WebSocket: explicitly deferred to Phase 44  
> Durable source of truth: PostgreSQL

---

# 1. Endpoint

```text
GET /api/ai-assistant/messages/{assistantMessageId}/stream
Accept: text/event-stream
Last-Event-ID: <last received sequence>
```

Fallback cursor:

```text
GET .../stream?afterSequence=<sequence>
```

`Last-Event-ID` wins when both are supplied.

The stream endpoint only accepts an `ASSISTANT` message belonging to a visible conversation.

---

# 2. Transport headers

Server response:

```text
Content-Type: text/event-stream
Cache-Control: no-cache, no-transform
Connection: keep-alive
X-Accel-Buffering: no
```

Proxy/load-balancer configuration must disable response buffering for this path.

---

# 3. Event envelope

Every non-comment SSE frame uses:

```text
id: <sequence>
event: <eventType>
data: <single-line JSON object>
```

Common fields in every event payload:

```json
{
  "messageId": "uuid",
  "turnId": "uuid",
  "sequence": 17,
  "occurredAt": "2026-07-17T08:30:02.123Z"
}
```

Rules:

```text
- sequence starts at 1 per assistant message;
- sequence is strictly monotonic;
- one event payload <= 8 KiB serialized;
- answer.delta text <= 4 KiB;
- maximum 4,096 events per assistant message;
- payload hash is persisted with the event;
- duplicate sequences are ignored by the client;
- gaps trigger reconnect/replay from the last contiguous sequence.
```

---

# 4. Event definitions

## 4.1 `message.started`

Emitted once when execution begins.

```json
{
  "messageId": "uuid",
  "turnId": "uuid",
  "sequence": 1,
  "occurredAt": "2026-07-17T08:30:00Z",
  "status": "CONTEXTUALIZING",
  "responseModeHint": null
}
```

## 4.2 `context.completed`

```json
{
  "messageId": "uuid",
  "turnId": "uuid",
  "sequence": 2,
  "occurredAt": "2026-07-17T08:30:00.100Z",
  "contextHash": "ctx:v1:sha256:0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef",
  "pageCode": "PROJECT_TASK_DETAIL",
  "entityType": "TASK",
  "entityId": "uuid",
  "availableActionCount": 3,
  "redacted": false
}
```

Do not expose permission signatures, raw rights, or hidden fields.

## 4.3 `retrieval.started`

```json
{
  "messageId": "uuid",
  "turnId": "uuid",
  "sequence": 3,
  "occurredAt": "2026-07-17T08:30:00.150Z",
  "toolCode": "knowledge.search",
  "querySummary": "Why is API Integration blocked?",
  "sourceTypes": ["TASK", "DOCUMENT_VERSION", "MEETING_MINUTE"]
}
```

`querySummary` is bounded and redacted; it is not the provider prompt.

## 4.4 `retrieval.completed`

```json
{
  "messageId": "uuid",
  "turnId": "uuid",
  "sequence": 4,
  "occurredAt": "2026-07-17T08:30:00.450Z",
  "toolCode": "knowledge.search",
  "retrievalTraceId": "uuid",
  "resultCount": 8,
  "truncated": false,
  "durationMs": 287
}
```

## 4.5 `answer.delta`

```json
{
  "messageId": "uuid",
  "turnId": "uuid",
  "sequence": 5,
  "occurredAt": "2026-07-17T08:30:00.600Z",
  "text": "Task **API Integration**",
  "contentFormat": "MARKDOWN"
}
```

The server appends deltas into a durable accumulated assistant message at bounded checkpoints and always writes the complete final answer before the final event.

## 4.6 `citation.added`

```json
{
  "messageId": "uuid",
  "turnId": "uuid",
  "sequence": 12,
  "occurredAt": "2026-07-17T08:30:01.200Z",
  "citation": {
    "id": "uuid",
    "ordinal": 1,
    "sourceType": "TASK",
    "sourceId": "uuid",
    "sourceVersionId": "uuid",
    "knowledgeChunkId": "uuid",
    "title": "API Integration",
    "headingPath": ["Blocker"],
    "fragment": "Task is waiting for Authentication API.",
    "appRoute": "/projects/{projectId}/tasks/{taskId}"
  }
}
```

The citation row must already be persisted and access-validated before emitting this event.

## 4.7 `answer.completed`

Final event for `COMPLETED`.

```json
{
  "messageId": "uuid",
  "turnId": "uuid",
  "sequence": 18,
  "occurredAt": "2026-07-17T08:30:02Z",
  "status": "COMPLETED",
  "responseMode": "GROUNDED_ANSWER",
  "finishReason": "STOP",
  "citationCount": 2,
  "inputTokenCount": 6230,
  "outputTokenCount": 410,
  "messageUrl": "/api/ai-assistant/messages/{messageId}"
}
```

## 4.8 `answer.cancelled`

Final event for `CANCELLED`.

```json
{
  "messageId": "uuid",
  "turnId": "uuid",
  "sequence": 9,
  "occurredAt": "2026-07-17T08:30:01Z",
  "status": "CANCELLED",
  "reasonCode": "USER_REQUESTED",
  "partialContentAvailable": true,
  "messageUrl": "/api/ai-assistant/messages/{messageId}"
}
```

## 4.9 `answer.failed`

Final event for `FAILED`.

```json
{
  "messageId": "uuid",
  "turnId": "uuid",
  "sequence": 9,
  "occurredAt": "2026-07-17T08:30:01Z",
  "status": "FAILED",
  "errorCode": "AI_ASSISTANT_MODEL_UNAVAILABLE",
  "retryable": true,
  "message": "The assistant is temporarily unavailable.",
  "messageUrl": "/api/ai-assistant/messages/{messageId}"
}
```

No provider stack trace or raw response is exposed.

## 4.10 `answer.blocked`

Final event for `BLOCKED`.

```json
{
  "messageId": "uuid",
  "turnId": "uuid",
  "sequence": 7,
  "occurredAt": "2026-07-17T08:30:01Z",
  "status": "BLOCKED",
  "errorCode": "AI_MESSAGE_BLOCKED_BY_POLICY",
  "message": "This request cannot be answered under the current policy.",
  "messageUrl": "/api/ai-assistant/messages/{messageId}"
}
```

## 4.11 `heartbeat`

Emitted at most every 15 seconds of inactivity.

```json
{
  "messageId": "uuid",
  "turnId": "uuid",
  "sequence": 16,
  "occurredAt": "2026-07-17T08:30:15Z",
  "status": "GENERATING"
}
```

Heartbeats are persisted so sequence replay remains deterministic. They may be pruned with the same 24-hour stream-event retention.

---

# 5. Message state machine

```text
RECEIVED
  → QUEUED
  → CONTEXTUALIZING
  → [RETRIEVING]
  → GENERATING
  → STREAMING
  → COMPLETED
```

Alternative final paths:

```text
Any non-final state → CANCEL_REQUESTED → CANCELLED
Any non-final state → FAILED
Any pre-generation/policy state → BLOCKED
```

Allowed skipped states:

```text
GENERAL_GUIDE may skip RETRIEVING.
A fully buffered provider response may transition GENERATING → COMPLETED while still emitting deltas from the durable buffer.
```

Forbidden:

```text
final → non-final
COMPLETED → CANCELLED
CANCELLED → COMPLETED
FAILED → retry in the same message
BLOCKED → provider execution
```

A retry creates a new turn/message.

---

# 6. Reconnect and replay algorithm

```text
1. Authenticate and authorize the message/conversation.
2. Resolve cursor from Last-Event-ID, else afterSequence, else 0.
3. Load durable stream events where sequence > cursor ordered ascending.
4. Emit replay rows exactly once per sequence.
5. If message is final, close after replaying final event.
6. If message is non-final, register the emitter with AiAssistantSseStreamService.
7. Race protection: after registration, query once more for events created after the last replayed sequence.
8. Live events are persisted before broadcast.
9. On disconnect, remove emitter; do not cancel the model automatically.
10. Client recovers final state through GET message even when replay TTL expired.
```

Expired replay behavior:

```text
- If requested cursor is older than retained events and message is final: emit the final event reconstructed from the message row, then close.
- If requested cursor is older than retained events and message is non-final: emit `message.started` with `replayTruncated=true`, then continue live; client must refresh GET message for accumulated content.
```

---

# 7. Cancellation behavior

Cancellation endpoint sets `CANCEL_REQUESTED` atomically when the message is non-final.

Orchestrator checks cancellation:

```text
- before context resolution;
- before tool execution;
- after tool execution;
- before provider call;
- on each provider delta callback;
- before final persistence.
```

Provider cancellation is best effort. Race resolution:

```text
- If completion is durably committed before cancel request: COMPLETED wins.
- If cancel request is committed before final completion: orchestrator must stop emission and finalize CANCELLED unless an irreversible provider callback has already been committed as final.
```

---

# 8. Multi-instance rule

Phase 42 does not require WebSocket or Redis Pub/Sub.

Minimum supported deployment:

```text
- durable event rows in PostgreSQL;
- emitter registry local to one application instance;
- sticky session recommended for live efficiency;
- reconnect works on any instance through replay polling.
```

When multi-instance non-sticky live fan-out is required, Phase 44 may introduce Redis Pub/Sub/Streams without changing this SSE payload contract.

---

# 9. Failure handling

```text
Client disconnect → execution continues; durable final state remains recoverable.
Provider timeout → FAILED + answer.failed.
Tool failure → FAILED unless policy converts to INSUFFICIENT_EVIDENCE.
Invalid citation → do not emit; block/fail answer before finalization.
Database failure while persisting event → do not broadcast unpersisted event.
Event limit reached → stop deltas, persist FAILED with AI_STREAM_EVENT_LIMIT_EXCEEDED.
Emitter timeout → close connection only; execution may continue.
```

---

# 10. Required tests

```text
sseUsesTextEventStreamHeaders
sseSequenceStartsAtOneAndIsMonotonic
ssePersistsBeforeBroadcast
sseReconnectReplaysAfterLastEventId
sseLastEventIdWinsOverQueryCursor
sseReplayThenLiveHasNoGapOrDuplicate
sseFinalMessageReplaysAndCloses
sseExpiredReplayFallsBackToMessageState
sseHeartbeatEveryConfiguredInterval
sseDeltaPayloadRespectsSizeLimit
sseEventCountLimitFailsSafely
sseCitationIsPersistedBeforeEmission
sseDisconnectDoesNotCancelExecution
sseCancelTransitionsToCancelled
sseCompletionCancelRaceHasDeterministicWinner
sseFailurePersistsRedactedError
sseToolOrProviderSecretNeverAppears
