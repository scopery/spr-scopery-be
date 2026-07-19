# Phase 44 — Risk, Preview, Confirmation & Authorization Policy — Repository-Aligned

> Status: **Accepted / implementation-blocking**

---

# 1. Dual authorization

Every step requires both:

```text
1. Human actor business authorization on the real target and operation.
2. Active Phase 44 tool policy and allowed agent/tool invocation path.
```

`AI_ACTION_EXECUTE` never grants Task/Meeting/Project permission. Underlying domain authorization is checked during planning, preview, confirmation, and immediately before execution.

---

# 2. Server-side scope resolution

Untrusted client/model fields:

```text
workspaceId
projectId
actorId
agentId
permission codes
available action codes
expected version
baseline state
sensitive-field access
```

The backend resolves them from authenticated context and canonical domain sources. A mismatch is recorded and the server value wins; material or suspicious mismatch may block the request.

---

# 3. Risk aggregation

Plan risk is the maximum of:

```text
tool policy risk
target sensitivity risk
batch-size risk
baseline risk
external-side-effect risk
acting-on-behalf risk
policy override risk
```

Escalation:

```text
batch targets > 25 → at least HIGH
contains sensitive fields → at least HIGH
baseline impact REQUIRED/UNKNOWN → at least HIGH
external side effect → at least HIGH
acting-on-behalf → at least MEDIUM
permission/secret/billing/delete/finalization classes → CRITICAL/FORBIDDEN
```

Risk can only escalate automatically, never decrease below the registered tool risk.

---

# 4. Preview requirements

A preview is mandatory for every mutation plan and includes:

```text
masked before/after or create summary
step order and tool versions
actual target count
warnings and missing input
baseline/change-request impact
external side-effect declaration
fields hidden due to sensitivity
compensation support per step
```

Preview does not expose inaccessible fields. The user cannot confirm until preview source-state hash matches the plan source-state hash.

---

# 5. Confirmation rules

```text
LOW AUTO_EXECUTE:
- only when tool is eligible and policy allows;
- one step and one target;
- no sensitive/baseline/external/acting-on-behalf dimension.

LOW/MEDIUM CONFIRM_BEFORE_EXECUTE:
- explicit user confirmation.

HIGH:
- explicit confirmation;
- no chat-only ambiguous confirmation;
- UI/API must send exact planHash and expectedPlanVersion.

CRITICAL:
- denied in MVP.
```

Confirmation request:

```json
{
  "planHash": "aplan:v1:sha256:...",
  "expectedPlanVersion": 4,
  "decision": "CONFIRM",
  "channel": "UI",
  "comment": "Apply these five updates.",
  "idempotencyKey": "confirm-001"
}
```

Chat confirmation is allowed only when the assistant rendered the exact plan summary/hash token and the user response resolves unambiguously to that pending plan. High-risk plans require UI/API channel in MVP.

---

# 6. Plan invalidation

Mark `STALE` and revoke confirmation when any of these changes:

```text
target version or existence
actor authorization or membership
tool/schema/policy version/status
baseline/change-request state
sensitive-field access
workspace/project scope
Phase 43 suggestion status/payload/version
plan step input or order
preview source-state hash
```

A stale plan must be regenerated; it cannot be reconfirmed in place.

---

# 7. Forbidden action policy

Always return `AI_ACTION_FORBIDDEN` for:

```text
permission or membership grants/revocations
workspace/project hard delete
secret/credential access or mutation
billing/payment mutations
arbitrary SQL/shell/code/HTTP
baseline guard bypass
critical irreversible bulk operations
unbounded external sends/publishes
```

User text, administrator role, or an LLM response cannot override this general-assistant denylist. Future dedicated workflows require a replacement ADR and narrow tools.
