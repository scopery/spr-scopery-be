# Phase 44 — Durable Execution State Machine — Repository-Aligned

> Status: **Accepted / implementation-blocking**

---

# 1. Request state machine

```text
RECEIVED → PLANNING → PLANNED
RECEIVED/PLANNING → BLOCKED|FAILED|CANCELLED
```

Only a `PLANNED` request has at least one durable plan.

---

# 2. Plan state machine

```text
DRAFT
→ VALIDATING
→ INVALID | PREVIEW_READY
PREVIEW_READY
→ WAITING_CONFIRMATION | CONFIRMED   (eligible auto-execute only)
WAITING_CONFIRMATION
→ CONFIRMED | CANCELLED | EXPIRED | STALE
CONFIRMED
→ EXECUTION_QUEUED | STALE | CANCELLED | EXPIRED
EXECUTION_QUEUED
→ EXECUTING
EXECUTING
→ COMPLETED | PARTIAL | FAILED | CANCELLED
```

`INVALID`, `COMPLETED`, `PARTIAL`, `FAILED`, `CANCELLED`, `EXPIRED`, and `STALE` are terminal for that plan version.

---

# 3. Execution state machine

```text
QUEUED → RUNNING
RUNNING → PAUSING → PAUSED → RESUMING → RUNNING
RUNNING/PAUSED → CANCEL_REQUESTED → CANCELLED
RUNNING → SUCCEEDED | PARTIAL | FAILED
PARTIAL/FAILED/SUCCEEDED → COMPENSATING
COMPENSATING → COMPENSATED | COMPENSATION_PARTIAL | COMPENSATION_FAILED
```

Rules:

```text
- PAUSE is accepted only between steps or inside a tool that declares supportsPause.
- CANCEL prevents new steps; an in-flight non-cancellable domain transaction completes normally.
- A cancelled execution truthfully reports already committed steps.
- SUCCEEDED means every required step succeeded or was an explicitly allowed no-op.
- PARTIAL means at least one committed success and at least one failed/skipped/cancelled required step.
- FAILED means no required mutation completed successfully or a pre-execution failure blocked the run.
```

---

# 4. Step execution

```text
QUEUED → RUNNING → SUCCEEDED
QUEUED/RUNNING → FAILED | CANCELLED
QUEUED → SKIPPED
SUCCEEDED → COMPENSATED
```

Before each step:

```text
1. verify execution/plan status;
2. verify worker lease;
3. reload tool policy and adapter;
4. revalidate input schema;
5. revalidate actor and target authorization;
6. revalidate target version and baseline state;
7. check dependencies;
8. claim idempotency key;
9. call adapter → existing Application Action.execute(Command).
```

After each step, persist step result and execution counters in a transaction before writing/publishing the realtime event.

---

# 5. Worker and lease model

MVP worker implementation:

```text
- no Kafka requirement;
- execution created as QUEUED;
- existing outbox or scheduled dispatcher wakes workers;
- worker claims with database compare-and-set/row lock;
- lease duration: 60 seconds;
- renew every 20 seconds while active;
- expired lease may be reclaimed;
- step idempotency prevents duplicate domain effects.
```

Preferred query uses repository-supported pessimistic locking or `FOR UPDATE SKIP LOCKED` through an infrastructure repository, never domain-layer raw SQL.

---

# 6. Concurrent execution limit

Default:

```text
2 active executions per initiated user/workspace
```

Active statuses:

```text
QUEUED, RUNNING, PAUSING, PAUSED, RESUMING, CANCEL_REQUESTED, COMPENSATING
```

Limit is enforced transactionally from PostgreSQL. Redis counters are not authoritative.

---

# 7. Recovery

On restart:

```text
- reclaim expired RUNNING leases;
- inspect latest step execution/idempotency record;
- resume at the first non-terminal step;
- never repeat a committed action merely because a realtime event was not published;
- republish durable events whose redis_published_at is null.
```
