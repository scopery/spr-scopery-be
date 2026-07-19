# Phase 44 — MVP Action Tool Catalog — Repository-Aligned

> Status: **Accepted / implementation-blocking**  
> Registry ownership: existing `modules/aiagent` AiTool registry  
> Execution policy ownership: `modules/aiaction` / `ai_action_tool_policy`

---

# 1. Catalog rules

```text
- Tool code and version are immutable after activation.
- No ACTIVE tool without exactly one live AiActionToolAdapter.
- Mutation tools are PLAN_EXECUTION_ONLY.
- Generic AiTool execution must reject direct mutation calls.
- Every adapter invokes an existing Application Action/Command; repositories are forbidden.
- Input is validated against active JSON Schema before preview and again before execution.
- Output is a bounded safe result summary, never a raw aggregate/provider response.
```

---

# 2. LLM-callable tools

## `agent.action.prepare` v1

```text
Invocation scope: LLM_CALLABLE
Risk: LOW
Mode: PREVIEW_ONLY
Authority: AI_ACTION_REQUEST
Adapter: AgentActionPrepareAiToolHandler
Mutation: creates ai_action_request/plan/preview only; no project-domain mutation
Max suggestions/items: 25
```

Input:

```json
{
  "origin": {
    "type": "CHAT",
    "conversationId": "uuid",
    "messageId": "uuid",
    "turnId": "uuid",
    "suggestionRef": null
  },
  "intentSummary": "Assign the selected task to the user I chose.",
  "requestedActions": [
    {
      "toolCode": "task.assign",
      "toolVersion": "1",
      "target": {"entityType": "TASK", "entityId": "uuid"},
      "arguments": {"assigneeUserId": "uuid"}
    }
  ]
}
```

The backend overrides workspace, project, actor, permissions, expected version, and available tool metadata.

## `agent.action.status` v1

```text
Invocation scope: LLM_CALLABLE_READ_ONLY
Risk: LOW
Mode: READ_ONLY
Authority: AI_ACTION_HISTORY_VIEW
Adapter: AgentActionStatusAiToolHandler
```

Input:

```json
{"executionId":"uuid"}
```

Output excludes inaccessible steps, raw payloads, secrets, and sensitive diffs.

---

# 3. PLAN_EXECUTION_ONLY mutation tools

| Tool | Adapter key | Authority | Default risk | Confirmation | Max targets | Compensation |
|---|---|---|---|---|---:|---|
| `task.create` v1 | `TASK_CREATE_V1` | target task-create right | MEDIUM | required | 25 | optional delete-draft only when real action exists |
| `task.assign` v1 | `TASK_ASSIGN_V1` | target task-update/assign right | MEDIUM | required | 25 | restore prior assignee when version-safe |
| `task.estimate.update` v1 | `TASK_ESTIMATE_UPDATE_V1` | target task-update right | MEDIUM | required | 25 | restore prior estimate when version-safe |
| `task.mitigation.update` v1 | `TASK_MITIGATION_UPDATE_V1` | target task-update right | MEDIUM | required | 25 | restore prior mitigation when version-safe |
| `meeting.action.assign` v1 | `MEETING_ACTION_ASSIGN_V1` | target meeting-action update right | MEDIUM | required | 25 | restore prior owner when version-safe |
| `meeting.action.due-date.update` v1 | `MEETING_ACTION_DUE_DATE_V1` | target meeting-action update right | MEDIUM | required | 25 | restore prior due date when version-safe |

The exact repository authority constant and Application Action class are resolved during implementation from compile-safe code. The semantic tool code, adapter key, payload, risk, and boundary are locked.

---

# 4. Phase 43 suggestion mapping

| Phase 43 suggestion type | Phase 44 tool |
|---|---|
| `TASK_MISSING_OWNER` | `task.assign` v1 |
| `TASK_MISSING_ESTIMATE` | `task.estimate.update` v1 |
| `TASK_BLOCKED_WITHOUT_MITIGATION` | `task.mitigation.update` v1 |
| `MEETING_ACTION_MISSING_OWNER` | `meeting.action.assign` v1 |
| `MEETING_ACTION_MISSING_DUE_DATE` | `meeting.action.due-date.update` v1 |
| `PHASE21_PLANNING_PROPOSAL` | compatibility adapter; no direct generic mapping |

Null/manual placeholders from Phase 43 are not executable. `agent.action.prepare` returns `AI_ACTION_REQUIRED_INPUT_MISSING` and a safe list of required fields.

---

# 5. Forbidden generic tools

Never register or activate:

```text
database.query
database.execute
shell.execute
code.execute
http.request.any
permission.grant
permission.revoke
workspace.delete
project.delete
secret.read
secret.write
billing.charge
external.send.any
```
