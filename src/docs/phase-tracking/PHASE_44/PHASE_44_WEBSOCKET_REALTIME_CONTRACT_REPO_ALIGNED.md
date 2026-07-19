# Phase 44 — WebSocket/STOMP Realtime Contract — Repository-Aligned

> Status: **Accepted / implementation-blocking**  
> Web stack: Spring MVC + `spring-boot-starter-websocket`; **no WebFlux migration**  
> Live fan-out: existing Redis  
> Durable replay: PostgreSQL `ai_action_execution_event`

---

# 1. Connection and destinations

```text
Handshake: GET /api/ws/ai-actions
Protocol: STOMP 1.2 over native WebSocket
Server heartbeat: 15 seconds
Client heartbeat expectation: 15 seconds
Max frame payload: 64 KiB
```

Destinations:

```text
Subscribe: /user/queue/ai-actions/{executionId}
Send command: /app/ai-actions/{executionId}/commands
Personal error/ack: /user/queue/ai-actions/commands
```

No credentials, confirmation tokens, or secrets in URL query parameters. Authenticate using the repository's existing session/token mechanism during handshake. Validate allowed origins using configured frontend origins.

---

# 2. Common server event envelope

```json
{
  "executionId": "uuid",
  "sequence": 12,
  "executionVersion": 7,
  "eventType": "step.completed",
  "occurredAt": "2026-07-19T08:00:00Z",
  "traceId": "trace-id",
  "payload": {}
}
```

Required event types:

```text
execution.queued
execution.started
step.started
step.progress
step.completed
step.failed
execution.waiting_for_confirmation
execution.pausing
execution.paused
execution.resuming
execution.resumed
execution.cancel_requested
execution.cancelled
execution.partially_completed
execution.completed
execution.failed
compensation.started
compensation.step_completed
compensation.completed
heartbeat
```

`step.progress` is optional per adapter and may not expose provider/internal details.

---

# 3. Event payloads

## `step.started`

```json
{
  "stepId": "uuid",
  "ordinal": 2,
  "toolCode": "task.assign",
  "attempt": 1,
  "target": {"entityType": "TASK", "entityId": "uuid"}
}
```

## `step.completed`

```json
{
  "stepId": "uuid",
  "ordinal": 2,
  "status": "SUCCEEDED",
  "target": {"entityType": "TASK", "entityId": "uuid"},
  "resultVersionToken": "TASK:v1:43",
  "warnings": []
}
```

## `step.failed`

```json
{
  "stepId": "uuid",
  "ordinal": 2,
  "status": "FAILED",
  "errorCode": "AI_ACTION_TARGET_VERSION_CONFLICT",
  "retryable": false
}
```

## final event

```json
{
  "status": "PARTIAL",
  "counts": {"succeeded": 3, "failed": 1, "skipped": 1, "compensated": 0},
  "summary": "Three steps succeeded; one failed; one dependent step was skipped."
}
```

---

# 4. Client command envelope

```json
{
  "commandId": "client-uuid",
  "commandType": "PAUSE",
  "expectedExecutionVersion": 7,
  "idempotencyKey": "pause-001",
  "payload": {}
}
```

Allowed:

```text
PAUSE
RESUME
CANCEL
COMPENSATE
```

The WebSocket handler delegates to the same application command actions as REST. It never mutates execution rows directly.

Command acknowledgement:

```json
{
  "commandId": "client-uuid",
  "executionId": "uuid",
  "status": "ACCEPTED",
  "commandRecordId": "uuid",
  "executionVersion": 8,
  "errorCode": null
}
```

---

# 5. Persistence and publication ordering

```text
1. persist execution transition;
2. allocate/increment sequence transactionally;
3. insert ai_action_execution_event;
4. commit;
5. publish event ID/envelope through Redis Pub/Sub;
6. each instance delivers through SimpMessagingTemplate;
7. update delivery timestamps best-effort.
```

Redis/WebSocket publication failure never rolls back a completed domain action. A publisher job retries unpublished durable events.

---

# 6. Reconnect and replay

On reconnect:

```text
1. GET /api/ai-actions/executions/{executionId};
2. GET /api/ai-actions/executions/{executionId}/events?afterSequence={lastSeen};
3. subscribe to /user/queue/ai-actions/{executionId};
4. de-duplicate by sequence.
```

Replay retention: 7 days after execution completion in Phase 44 MVP. Phase 45 may replace this with policy-driven retention.

`409 AI_ACTION_EVENT_REPLAY_GAP` is returned when requested sequence is older than retained events; client reloads full current state and step list.

---

# 7. Subscription authorization

Before subscription or command:

```text
- authenticated user required;
- AI_ACTION_HISTORY_VIEW for status subscription;
- actor/project target visibility required;
- AI_ACTION_CONTROL for pause/resume/cancel;
- AI_ACTION_COMPENSATE for compensation;
- execution workspace/project scope revalidated.
```

User destinations do not replace resource authorization.
