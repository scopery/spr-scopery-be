# Phase 44 — Idempotency, Retry, Partial Failure & Compensation — Repository-Aligned

> Status: **Accepted / implementation-blocking**

---

# 1. Request idempotency

Scope:

```text
(workspace_id, initiated_by_user_id, idempotency_key)
```

Behavior:

```text
same key + same requestHash → return existing request/plan/execution
same key + different requestHash → AI_ACTION_IDEMPOTENCY_CONFLICT
```

---

# 2. Execution idempotency

Execution key canonical input:

```text
planId + planHash + confirmedBy + confirmationHash/null
```

Format:

```text
aexec:v1:sha256:<64hex>
```

There is at most one `ai_action_execution` per plan.

---

# 3. Step idempotency

Canonical input:

```text
executionId + stepId + toolCode + toolVersion + inputHash + expectedTargetVersionToken
```

Format:

```text
astep:v1:sha256:<64hex>
```

Retries reuse the same logical idempotency key when the domain Application Action supports idempotency. When the underlying action has its own idempotency key, the adapter maps the Phase 44 key to it.

---

# 4. Retry policy

Default max attempts: 3.

Retryable examples:

```text
transient database serialization/deadlock
retryable connector timeout for an explicitly allowed tool
worker crash before a committed result is recorded
realtime publication failure (does not repeat domain action)
```

Non-retryable examples:

```text
permission denied
schema validation failure
expected version conflict
baseline guard blocked
forbidden tool
missing required input
business validation failure
unknown adapter
```

Suggested backoff:

```text
1s, 5s, 30s with bounded jitter
```

Use repository-standard retry infrastructure if present; do not add nested retries around domain transactions that already retry.

---

# 5. Partial execution

Execution strategy defaults to `STOP_ON_FAILURE`.

```text
- failed step blocks dependent steps;
- independent later steps are skipped in MVP unless policy explicitly allows CONTINUE_INDEPENDENT;
- final status reports succeeded, failed, skipped, cancelled, and compensated counts;
- user-visible summary names exactly what changed.
```

No execution may report a global rollback unless every committed step was successfully compensated.

---

# 6. Compensation

Compensation is explicit, tool-specific, best-effort, and version-safe.

Required checks:

```text
- original step succeeded;
- tool policy declares supportsCompensation;
- compensation adapter exists;
- current target state still matches the expected post-action version/state;
- actor has current compensation/update authority;
- baseline/policy still allows reversal.
```

Compensation runs in reverse successful-step order.

MVP examples:

```text
task.assign → restore previous assignee if unchanged since execution
task.estimate.update → restore previous estimate if unchanged
meeting.action.assign → restore previous owner if unchanged
meeting.action.due-date.update → restore previous due date if unchanged
```

`task.create` compensation is disabled unless a real safe draft-delete/archive Application Action exists. Hard delete must not be invented.

---

# 7. Error/result safety

Persist only:

```text
safe result summary
domain result reference
version token
audit/outbox identifiers
redacted error code/summary
```

Do not persist raw aggregate dumps, stack traces, secrets, provider payloads, unrestricted before/after snapshots, or chain-of-thought.
