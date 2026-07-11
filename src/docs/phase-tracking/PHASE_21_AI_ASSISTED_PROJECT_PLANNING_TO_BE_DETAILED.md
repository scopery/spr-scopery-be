# PHASE 21 — TO-BE AI-assisted Project Planning, Suggestion Governance, Human Approval & Safe Apply

> Project: Scopery Backend  
> Phase: 21  
> Document type: TO-BE implementation-grade specification  
> Status: Planning / Gap-driven implementation spec  
> Depends on: Phase 00–20, especially Phase 07 AI Agent Platform, Phase 09 Project Core, Phase 13 Scheduling, Phase 16 Estimation, Phase 17 Finance, Phase 18 Quote, Phase 19 Baseline/Change Request, Phase 20 Notifications  
> API base: `/api`  
> Primary module: `modules/aiplanning` or `modules/project/ai-planning` depending on repository architecture  
> Important rule: This file is **not an as-is document**. It defines the TO-BE AI-assisted project planning layer. Phase 21 does not allow AI to silently mutate business data, grant permissions, bypass IAM, expose restricted finance/quote data, or act as an approval authority.

---

# 0. Purpose of this file

Phase 21 adds AI assistance on top of the project planning system.

Previous phases created:

```text
Phase 07:
- AI Agent Platform
- Provider / model / deployment / agent / prompt / event config / execution log
- AI usage policy
- No autonomous business mutation

Phase 09–14:
- Project / Phase / WBS / Task / Schedule / Gantt

Phase 15–18:
- Rate / Estimation / Finance / Quote

Phase 19:
- Baseline / Change Request

Phase 20:
- Project notifications
```

Phase 21 answers:

```text
How can AI help create a project plan?
How can AI suggest WBS and tasks?
How can AI suggest estimates, cost roles, assignees, and due-date risk mitigations?
How can AI explain schedule, capacity, finance, and quote problems?
How can AI draft a ChangeRequest or quote/proposal text?
How can AI output be reviewed, accepted, rejected, and applied safely?
How do we guarantee AI respects IAM and cannot mutate data without human approval?
```

Phase 21 is an **AI proposal and assisted planning layer**.

It is not autonomous project management.

---

# 1. Source inputs

Before coding Phase 21, the agent must read:

```text
1. Current backend codebase
2. Phase 07 AI Agent Platform TO-BE spec and implementation
3. Phase 09 Project Core TO-BE spec and implementation
4. Phase 10 Project Authorization TO-BE spec and implementation
5. Phase 11 Project Template TO-BE spec and implementation
6. Phase 12 Resource Calendar / Capacity TO-BE spec and implementation
7. Phase 13 Task Scheduling Engine TO-BE spec and implementation
8. Phase 14 WBS-driven Gantt TO-BE spec and implementation
9. Phase 15 Rate Card / Cost Policy TO-BE spec and implementation
10. Phase 16 Estimation Roll-up TO-BE spec and implementation
11. Phase 17 Project Budget / Margin TO-BE spec and implementation
12. Phase 18 Quote / Commercial Proposal TO-BE spec and implementation
13. Phase 19 Baseline / Change Request TO-BE spec and implementation
14. Phase 20 Project Events / Notifications TO-BE spec and implementation
15. Phase 02 IAM TO-BE spec
16. Phase 04 Platform Audit / Outbox / Idempotency spec
17. Phase 05 Event Registry spec
18. Current BE feature/entity/business-rule inventory
19. Dynamic Work OS feature catalog
20. Existing AI agent executions, prompt templates, event config, usage policy
```

The agent must not implement Phase 21 from assumptions only.

---

# 2. Current expected backend state

After Phase 20, the backend should have:

```text
AI Agent Platform foundation
Project data
WBS/tasks/dependencies
ScheduleRun and scheduling issues
Capacity profiles/allocation
EstimationRun and estimate snapshots
FinanceScenario and margin summary
QuoteVersion and quote summary
Baseline and ChangeRequest
Notification rules/events
IAM permissions and resource checks
```

Likely missing:

```text
AI project planning run
AI project planning context snapshot
AI planning suggestion
AI suggestion item
AI suggestion review/approval
AI safe apply service
AI prompt catalog for project planning
AI project tool/action permission mapping
AI suggestion audit trail
AI suggestion event notifications
AI grounding/source references
AI access-aware context builder
```

Phase 21 implements the safe proposal layer.

---

# 3. Phase 21 target statement

Phase 21 must deliver a future-ready AI-assisted project planning system:

```text
1. Create AIPlanningRun for a project and use an approved AI Agent configuration.
2. Build an access-aware ProjectPlanningContextSnapshot from project data the actor is allowed to see.
3. Run AI prompts through Phase 07 AI Agent Platform.
4. Store AI suggestions as structured, reviewable proposals.
5. Support suggestion types for WBS, tasks, estimates, assignee/capacity, schedule risk, finance insight, quote/proposal text, and change request draft.
6. Require human review before any suggestion is applied.
7. Apply accepted suggestions only through existing domain actions and authorization rules.
8. Preserve full traceability: prompt version, model/deployment, context snapshot, output, reviewer, applied actions.
9. Enforce IAM, AI tool permissions, usage policy, and sensitive data masking.
10. Emit events/notifications for AI suggestion lifecycle.
11. Clearly defer autonomous AI mutation, RAG over full documents, semantic knowledge graph, workflow approval, and AI external communication.
```

---

# 4. Non-negotiable AI safety rules

## 4.1 AI output is proposal only

AI may suggest:

```text
create WBS node
create task
update estimate
assign role
flag schedule risk
draft ChangeRequest
draft quote/proposal wording
explain finance issue
```

AI may not silently:

```text
create task
change estimate
move due date
approve baseline
approve quote
send notification externally
grant permission
change rate card
change finance scenario
apply ChangeRequest
```

Human-approved apply is required.

## 4.2 AI must respect effective access

The context builder must only include data the actor can access.

Rules:

```text
1. AI cannot see finance data unless actor has finance permission.
2. AI cannot see quote/margin data unless actor has quote/margin permission.
3. AI cannot see restricted baseline/change data unless actor has permission.
4. AI cannot see private capacity/leave data.
5. AI cannot see salary/payroll because not stored.
6. AI cannot use hidden data to produce visible suggestions.
```

## 4.3 AI cannot grant itself permission

Rule:

```text
AI must never grant itself, its agent identity, the actor, or another user additional permission.
```

## 4.4 Acting-on-behalf-of requires both sides

If AI apply action acts on behalf of user:

```text
1. User must be authorized for the business action.
2. Agent/tool must be authorized for the action.
3. Apply still goes through domain action and audit.
```

---

# 5. Classification labels

Every requirement uses one label:

| Label | Meaning |
|---|---|
| `CURRENTLY_IMPLEMENTED` | Current backend already implements it. Agent must verify and test. |
| `PARTIALLY_IMPLEMENTED` | Current backend implements part of it. |
| `MUST_IMPLEMENT_IN_PHASE_21` | Must be implemented or hardened now. |
| `SEED_ONLY_IN_PHASE_21` | Seed agents/prompts/events/permissions now; full consumer later. |
| `DEFERRED_TO_PHASE_XX` | Do not implement now; later phase must return. |
| `DEFERRED_TO_POST_23_BACKLOG` | Outside core 00–23 roadmap, part of full Work OS expansion. |
| `NOT_IN_SCOPE_FOR_BACKEND_NOW` | Explicitly not backend scope now. |

---

# 6. Phase 21 scope decision

## 6.1 Must implement now

```text
AIPlanningRun
AIPlanningContextSnapshot
AIPlanningSuggestion
AIPlanningSuggestionItem
AIPlanningSuggestionReview
AIPlanningApplyResult
Access-aware context builder
Project planning prompt catalog
AI project agent seed/config
Suggestion lifecycle: GENERATED / UNDER_REVIEW / ACCEPTED / REJECTED / APPLIED
Safe apply service through domain actions
Human approval/review requirement
AI project permissions
AI event definitions
Audit/activity/outbox integration
Tests
Completion report
```

## 6.2 Optional now

```text
AI suggestion comparison
AI suggestion scoring
AI confidence labels
AI prompt A/B testing
AI plan regeneration from rejected suggestions
AI natural-language chat endpoint
AI proposal document draft
AI notification summary
```

Implement only if product requires.

## 6.3 Must not implement now

```text
Autonomous mutation without human approval
AI permission grants
AI rate card changes
AI quote approval/send
AI baseline approval
AI ChangeRequest apply without human permission
Full RAG over project documents
Knowledge graph reasoning
Vector semantic index
External client AI communication
Workflow automation/escalation
AI model fine-tuning
```

---

# 7. AI use case matrix

## 7.1 AIP-001 — AI project plan draft

| Item | Value |
|---|---|
| Future capability | AI drafts WBS/tasks from project description/template |
| Current state | Missing/unknown |
| Phase 21 target | Implement proposal generation |
| Classification | `MUST_IMPLEMENT_IN_PHASE_21` |

Possible outputs:

```text
WBS nodes
Tasks
Task descriptions
Phase mapping
Initial estimateHours
Dependencies
Risks/assumptions
```

Rules:

```text
1. Output stored as suggestion items.
2. No WBS/task records created until human applies.
3. Apply creates WBS/tasks through domain actions.
4. If project has current baseline, apply must create/use ChangeRequest.
```

## 7.2 AIP-002 — AI template recommendation

| Item | Value |
|---|---|
| Future capability | AI suggests best ProjectTemplate |
| Current state | ProjectTemplate exists from Phase 11 |
| Phase 21 target | Implement optional suggestion |
| Classification | `MUST_IMPLEMENT_IN_PHASE_21` if template catalog exists; otherwise defer |

Rules:

```text
1. AI can recommend templateId/templateVersionId.
2. Actor must have access to template.
3. Applying template still uses Phase 11 apply-template action.
4. AI cannot apply template automatically.
```

## 7.3 AIP-003 — AI task estimate suggestion

| Item | Value |
|---|---|
| Future capability | AI suggests estimateHours for tasks |
| Current state | Task estimate exists; AI suggestion missing |
| Phase 21 target | Implement proposal |
| Classification | `MUST_IMPLEMENT_IN_PHASE_21` |

Rules:

```text
1. AI may propose estimateHours.
2. Estimate must be > 0.
3. Human accepts before Task.estimateHours changes.
4. If baseline exists, change must go through ChangeRequest.
5. Suggestions must include reason/assumptions.
```

## 7.4 AIP-004 — AI cost role suggestion

| Item | Value |
|---|---|
| Future capability | AI suggests CostRole for task/member |
| Current state | CostRole exists from Phase 15 |
| Phase 21 target | Implement proposal |
| Classification | `MUST_IMPLEMENT_IN_PHASE_21` |

Rules:

```text
1. AI may suggest costRoleCode.
2. CostRole must exist and be active.
3. AI cannot infer salary.
4. Applying role mapping uses Rate/Estimation domain action.
5. Finance/rate details masked unless actor authorized.
```

## 7.5 AIP-005 — AI capacity/schedule risk explanation

| Item | Value |
|---|---|
| Future capability | AI explains scheduling issues and recommends mitigation |
| Current state | Scheduling issues exist from Phase 13 |
| Phase 21 target | Implement explanation/suggestion |
| Classification | `MUST_IMPLEMENT_IN_PHASE_21` |

Possible suggestions:

```text
increase allocation
change assignee
split task
move due date
remove dependency
add predecessor
create risk/ChangeRequest
```

Rules:

```text
1. AI can explain existing SchedulingIssue.
2. AI cannot directly reassign/move task.
3. Suggested mitigation stored as proposal items.
4. Private capacity/leave details not exposed.
```

## 7.6 AIP-006 — AI finance insight

| Item | Value |
|---|---|
| Future capability | AI explains margin/cost drivers |
| Current state | FinanceScenario exists from Phase 17 |
| Phase 21 target | Implement only with finance permission |
| Classification | `MUST_IMPLEMENT_IN_PHASE_21` with strict permissions |

Possible outputs:

```text
High-cost phase explanation
Margin risk explanation
Cost reduction suggestions
Revenue split observations
Overhead/contingency notes
```

Rules:

```text
1. Actor must have PROJECT_FINANCE_VIEW.
2. Margin values require PROJECT_FINANCE_MARGIN_VIEW if separated.
3. AI cannot change finance scenario.
4. AI cannot suggest salary/payroll.
5. AI cannot expose hidden finance in output.
```

## 7.7 AIP-007 — AI quote/proposal drafting

| Item | Value |
|---|---|
| Future capability | AI drafts proposal wording and commercial assumptions |
| Current state | Quote exists from Phase 18 |
| Phase 21 target | Implement draft text only |
| Classification | `MUST_IMPLEMENT_IN_PHASE_21` if quote module exists |

AI can draft:

```text
executive summary
scope summary
assumptions
exclusions
delivery terms
support terms
client-friendly phase description
```

Rules:

```text
1. AI cannot set final quote price.
2. AI cannot approve quote.
3. AI cannot send quote.
4. Human must review terms before saving to QuoteTerm.
5. Actor must have quote access.
```

## 7.8 AIP-008 — AI ChangeRequest draft

| Item | Value |
|---|---|
| Future capability | AI drafts ChangeRequest from requested change or detected variance |
| Current state | ChangeRequest exists from Phase 19 |
| Phase 21 target | Implement proposal |
| Classification | `MUST_IMPLEMENT_IN_PHASE_21` |

AI can draft:

```text
title
description
reason
change items
impact summary
risk notes
change order draft text
```

Rules:

```text
1. AI cannot submit/approve/apply CR.
2. Human creates or applies draft through ChangeRequest actions.
3. Finance/quote impacts require permissions.
4. AI should cite source baseline/live differences when possible.
```

## 7.9 AIP-009 — AI notification/risk summary

| Item | Value |
|---|---|
| Future capability | AI summarizes recent project alerts |
| Current state | Notifications exist from Phase 20 |
| Phase 21 target | Optional/defer |
| Classification | `DEFERRED_TO_PHASE_22_REPORTING_OR_PHASE_35_ADVANCED_NOTIFICATIONS` |

Reason:

```text
Needs digest/reporting context and notification analytics.
```

## 7.10 AIP-010 — Full RAG over project documents

| Item | Value |
|---|---|
| Future capability | AI reads documents/knowledge base |
| Current state | DocumentType catalog only; full Document Hub deferred |
| Phase 21 target | Defer |
| Classification | `DEFERRED_TO_PHASE_27_DOCUMENT_HUB` and `PHASE_41_SEMANTIC_INDEX` |

Phase 21 can use structured project data.

Do not claim full document RAG.

---

# 8. Entity model TO-BE

If current schema differs, agent must map actual fields and document gaps.

## 8.1 AIPlanningRun — `ai_planning_run`

Required fields:

```text
id UUID PK
project_id UUID NOT NULL
workspace_id UUID NOT NULL
actor_user_id UUID NOT NULL
agent_id UUID NOT NULL
agent_version_id UUID NULL
prompt_template_id UUID NULL
prompt_template_version_id UUID NULL
model_deployment_id UUID NULL
ai_execution_log_id UUID NULL
run_type VARCHAR(100) NOT NULL
status VARCHAR(50) NOT NULL
input_summary_json JSONB NULL
context_snapshot_id UUID NULL
output_summary_json JSONB NULL
error_code VARCHAR(150) NULL
error_message TEXT NULL
started_at TIMESTAMP NULL
completed_at TIMESTAMP NULL
created_at / created_by
trace_id VARCHAR(100) NULL
version INT
```

Run types:

```text
PROJECT_PLAN_DRAFT
TEMPLATE_RECOMMENDATION
TASK_ESTIMATE_SUGGESTION
COST_ROLE_SUGGESTION
SCHEDULE_RISK_EXPLANATION
FINANCE_INSIGHT
QUOTE_PROPOSAL_DRAFT
CHANGE_REQUEST_DRAFT
GENERAL_PROJECT_ASSISTANT
```

Status:

```text
PENDING
RUNNING
COMPLETED
FAILED
CANCELLED
```

## 8.2 AIPlanningContextSnapshot — `ai_planning_context_snapshot`

Required fields:

```text
id UUID PK
project_id UUID NOT NULL
workspace_id UUID NOT NULL
actor_user_id UUID NOT NULL
context_type VARCHAR(100) NOT NULL
access_scope_json JSONB NOT NULL
included_sections_json JSONB NOT NULL
redaction_summary_json JSONB NULL
context_payload_json JSONB NOT NULL
token_estimate INT NULL
created_at TIMESTAMP NOT NULL
trace_id VARCHAR(100) NULL
```

Included sections:

```text
PROJECT
PHASES
WBS
TASKS
DEPENDENCIES
SCHEDULE
CAPACITY_SUMMARY
ESTIMATION_SUMMARY
FINANCE_SUMMARY
QUOTE_SUMMARY
BASELINE_SUMMARY
CHANGE_REQUESTS
NOTIFICATION_SUMMARY
```

Rules:

```text
1. Context snapshot includes only authorized data.
2. Sensitive data redaction summary stored.
3. Snapshot immutable.
4. Snapshot does not include hidden finance/quote fields unless authorized.
```

## 8.3 AIPlanningSuggestion — `ai_planning_suggestion`

Required fields:

```text
id UUID PK
planning_run_id UUID NOT NULL
project_id UUID NOT NULL
workspace_id UUID NOT NULL
suggestion_type VARCHAR(100) NOT NULL
title VARCHAR(255) NOT NULL
summary TEXT NULL
rationale TEXT NULL
confidence_label VARCHAR(50) NULL
status VARCHAR(50) NOT NULL
source_references_json JSONB NULL
created_at / created_by
reviewed_at TIMESTAMP NULL
reviewed_by UUID NULL
applied_at TIMESTAMP NULL
applied_by UUID NULL
rejected_at TIMESTAMP NULL
rejected_by UUID NULL
rejection_reason TEXT NULL
version INT
```

Suggestion types:

```text
CREATE_WBS
CREATE_TASKS
UPDATE_TASK_ESTIMATES
UPDATE_COST_ROLES
SCHEDULE_RISK_MITIGATION
FINANCE_INSIGHT
QUOTE_TEXT_DRAFT
CHANGE_REQUEST_DRAFT
TEMPLATE_RECOMMENDATION
MIXED_PLAN
```

Status:

```text
GENERATED
UNDER_REVIEW
ACCEPTED
PARTIALLY_ACCEPTED
REJECTED
APPLIED
PARTIALLY_APPLIED
FAILED_TO_APPLY
ARCHIVED
```

## 8.4 AIPlanningSuggestionItem — `ai_planning_suggestion_item`

Required fields:

```text
id UUID PK
suggestion_id UUID NOT NULL
project_id UUID NOT NULL
item_type VARCHAR(100) NOT NULL
target_type VARCHAR(100) NULL
target_id UUID NULL
operation VARCHAR(50) NOT NULL
title VARCHAR(255) NOT NULL
description TEXT NULL
proposed_payload_json JSONB NOT NULL
rationale TEXT NULL
confidence_label VARCHAR(50) NULL
status VARCHAR(50) NOT NULL
apply_action VARCHAR(150) NULL
apply_result_json JSONB NULL
created_at / created_by
reviewed_at TIMESTAMP NULL
reviewed_by UUID NULL
applied_at TIMESTAMP NULL
applied_by UUID NULL
version INT
```

Item types:

```text
WBS_NODE
TASK
TASK_ESTIMATE
TASK_DEPENDENCY
TASK_ASSIGNEE
COST_ROLE
SCHEDULE_ADJUSTMENT
FINANCE_NOTE
QUOTE_TERM
CHANGE_REQUEST
RISK
```

Operation:

```text
CREATE
UPDATE
DELETE
ARCHIVE
MOVE
RECOMMEND
DRAFT_TEXT
```

Status:

```text
PROPOSED
ACCEPTED
REJECTED
APPLIED
FAILED
SKIPPED
```

## 8.5 AIPlanningReviewAction — `ai_planning_review_action`

Required fields:

```text
id UUID PK
suggestion_id UUID NOT NULL
suggestion_item_id UUID NULL
action VARCHAR(50) NOT NULL
actor_user_id UUID NOT NULL
reason TEXT NULL
created_at TIMESTAMP NOT NULL
trace_id VARCHAR(100) NULL
```

Actions:

```text
START_REVIEW
ACCEPT
REJECT
ACCEPT_ITEM
REJECT_ITEM
APPLY
APPLY_ITEM
ARCHIVE
```

## 8.6 AIPlanningApplyResult — `ai_planning_apply_result`

Required fields:

```text
id UUID PK
suggestion_id UUID NOT NULL
suggestion_item_id UUID NULL
project_id UUID NOT NULL
status VARCHAR(50) NOT NULL
domain_action VARCHAR(150) NULL
target_type VARCHAR(100) NULL
target_id UUID NULL
result_payload_json JSONB NULL
error_code VARCHAR(150) NULL
error_message TEXT NULL
created_at TIMESTAMP NOT NULL
created_by UUID NULL
trace_id VARCHAR(100) NULL
```

Status:

```text
SUCCESS
FAILED
SKIPPED
ROLLED_BACK
```

---

# 9. API TO-BE list

All APIs use `/api`.

## 9.1 AI planning run APIs

```text
POST /api/projects/{projectId}/ai-planning/runs
GET  /api/projects/{projectId}/ai-planning/runs
GET  /api/projects/{projectId}/ai-planning/runs/{runId}
POST /api/projects/{projectId}/ai-planning/runs/{runId}/cancel
```

Create request:

```json
{
  "runType": "PROJECT_PLAN_DRAFT",
  "agentId": "uuid",
  "promptTemplateCode": "PROJECT_PLAN_DRAFT",
  "input": {
    "projectGoal": "Build client portal for order tracking",
    "constraints": ["MVP in 8 weeks", "Use existing design system"],
    "requestedOutputs": ["WBS", "tasks", "estimates", "dependencies"]
  },
  "includeSections": ["PROJECT", "PHASES", "WBS", "TASKS", "SCHEDULE", "ESTIMATION_SUMMARY"],
  "options": {
    "createSuggestions": true,
    "maxSuggestions": 50
  }
}
```

Rules:

```text
1. Actor must have project AI planning permission.
2. Agent must be active/allowed.
3. Prompt template must be active.
4. Usage policy must allow execution.
5. Context builder must apply access filtering.
```

## 9.2 Suggestion APIs

```text
GET  /api/projects/{projectId}/ai-planning/suggestions
GET  /api/projects/{projectId}/ai-planning/suggestions/{suggestionId}
POST /api/projects/{projectId}/ai-planning/suggestions/{suggestionId}/start-review
POST /api/projects/{projectId}/ai-planning/suggestions/{suggestionId}/accept
POST /api/projects/{projectId}/ai-planning/suggestions/{suggestionId}/reject
POST /api/projects/{projectId}/ai-planning/suggestions/{suggestionId}/archive
```

Reject request:

```json
{
  "reason": "Estimate assumptions are not suitable"
}
```

## 9.3 Suggestion item APIs

```text
GET  /api/projects/{projectId}/ai-planning/suggestions/{suggestionId}/items
GET  /api/projects/{projectId}/ai-planning/suggestions/{suggestionId}/items/{itemId}
POST /api/projects/{projectId}/ai-planning/suggestions/{suggestionId}/items/{itemId}/accept
POST /api/projects/{projectId}/ai-planning/suggestions/{suggestionId}/items/{itemId}/reject
```

## 9.4 Safe apply APIs

```text
POST /api/projects/{projectId}/ai-planning/suggestions/{suggestionId}/apply
POST /api/projects/{projectId}/ai-planning/suggestions/{suggestionId}/items/{itemId}/apply
```

Apply request:

```json
{
  "applyMode": "ALL_ACCEPTED_ITEMS",
  "requireChangeRequestIfBaselined": true,
  "idempotencyKey": "client-key"
}
```

Rules:

```text
1. Suggestion/item must be accepted before apply.
2. Actor must have underlying business permission.
3. AI agent/tool must be authorized for action.
4. Apply through domain actions only.
5. If current baseline exists, route controlled changes through ChangeRequest.
```

## 9.5 AI explanation APIs

Optional:

```text
POST /api/projects/{projectId}/ai-planning/explain-schedule-risk
POST /api/projects/{projectId}/ai-planning/explain-finance
POST /api/projects/{projectId}/ai-planning/draft-change-request
POST /api/projects/{projectId}/ai-planning/draft-quote-terms
```

These can internally create AIPlanningRun.

Recommended:

```text
Use runType endpoint first; add convenience endpoints only if product needs.
```

---

# 10. Authorization requirements

Required IAM authorities:

```text
AI_PROJECT_PLANNING_RUN
AI_PROJECT_PLANNING_VIEW
AI_PROJECT_PLANNING_REVIEW
AI_PROJECT_PLANNING_ACCEPT
AI_PROJECT_PLANNING_REJECT
AI_PROJECT_PLANNING_APPLY
AI_PROJECT_PLANNING_ARCHIVE

AI_PROJECT_PLAN_DRAFT
AI_TASK_ESTIMATE_SUGGEST
AI_COST_ROLE_SUGGEST
AI_SCHEDULE_RISK_EXPLAIN
AI_FINANCE_INSIGHT
AI_QUOTE_DRAFT
AI_CHANGE_REQUEST_DRAFT

AI_SUGGESTION_APPLY_WBS
AI_SUGGESTION_APPLY_TASK
AI_SUGGESTION_APPLY_ESTIMATE
AI_SUGGESTION_APPLY_DEPENDENCY
AI_SUGGESTION_APPLY_QUOTE_TERM
AI_SUGGESTION_APPLY_CHANGE_REQUEST
```

Rules:

```text
1. Running AI requires AI_PROJECT_PLANNING_RUN.
2. Viewing AI output requires project view and AI planning view.
3. Finance AI output requires finance permission.
4. Quote AI output requires quote permission.
5. Applying suggestion requires AI apply permission and underlying business permission.
6. Agent/tool must also be allowed to propose/apply that action.
7. AI cannot bypass baseline/change request guard.
```

---

# 11. AI context builder rules

## 11.1 Context sections

Supported sections:

```text
PROJECT
PHASES
WBS
TASKS
DEPENDENCIES
SCHEDULE
CAPACITY_SUMMARY
ESTIMATION_SUMMARY
FINANCE_SUMMARY
QUOTE_SUMMARY
BASELINE_SUMMARY
CHANGE_REQUESTS
NOTIFICATION_SUMMARY
```

## 11.2 Access filtering

Rules:

```text
1. Check actor's effective access for each section.
2. Exclude unauthorized sections.
3. Redact unauthorized fields.
4. Store redaction summary.
5. Do not include raw secrets, tokens, provider keys, or hidden prompt data.
6. Do not include salary/payroll.
```

## 11.3 Finance/quote masking

If actor lacks permission:

```text
FINANCE_SUMMARY omitted or masked.
QUOTE_SUMMARY omitted or masked.
```

Masked example:

```json
{
  "financeAvailable": true,
  "detailsRedacted": true,
  "reason": "PROJECT_FINANCE_VIEW_REQUIRED"
}
```

AI output must not infer hidden values.

## 11.4 Context size

Rules:

```text
1. Context builder must enforce max token/size limit.
2. Large task lists should be summarized.
3. Full document content not included in Phase 21.
4. Semantic retrieval deferred to Phase 41.
```

---

# 12. Prompt catalog

Phase 21 must seed prompt templates or AI agent configs.

Recommended prompt codes:

```text
PROJECT_PLAN_DRAFT_PROMPT
PROJECT_TEMPLATE_RECOMMENDATION_PROMPT
TASK_ESTIMATE_SUGGESTION_PROMPT
COST_ROLE_SUGGESTION_PROMPT
SCHEDULE_RISK_EXPLANATION_PROMPT
FINANCE_INSIGHT_PROMPT
QUOTE_PROPOSAL_DRAFT_PROMPT
CHANGE_REQUEST_DRAFT_PROMPT
```

Prompt rules:

```text
1. Prompt version is immutable once published.
2. Prompt instructs AI to output JSON matching schema.
3. Prompt says output is proposal only.
4. Prompt says not to invent inaccessible data.
5. Prompt says not to approve/apply changes.
6. Prompt says not to include salary/payroll.
7. Prompt includes allowed suggestion item schema.
```

---

# 13. Suggestion output schema

AI output should be parsed into structured items.

Minimum JSON shape:

```json
{
  "title": "Suggested plan for Client Portal",
  "summary": "Create WBS and tasks for MVP delivery.",
  "rationale": "Based on project goal and existing template structure.",
  "items": [
    {
      "itemType": "WBS_NODE",
      "operation": "CREATE",
      "title": "Authentication and User Access",
      "description": "Scope area for login, roles, and user management.",
      "proposedPayload": {
        "parentWbsNodeId": null,
        "phaseId": "uuid",
        "title": "Authentication and User Access"
      },
      "rationale": "Common module required by client portal.",
      "confidenceLabel": "MEDIUM"
    }
  ],
  "risks": [
    {
      "title": "Payment gateway dependency",
      "severity": "MEDIUM",
      "mitigation": "Confirm provider credentials before development."
    }
  ]
}
```

Rules:

```text
1. Invalid JSON output stored as failed run with raw output reference if allowed.
2. Unknown itemType rejected.
3. Invalid payload rejected or stored as invalid suggestion item.
4. No apply occurs during parsing.
```

---

# 14. Safe apply strategy

## 14.1 Apply through domain actions

Mapping examples:

```text
WBS_NODE CREATE → CreateWbsNodeAction
WBS_NODE MOVE → MoveWbsNodeAction
TASK CREATE → CreateTaskAction
TASK UPDATE estimate → UpdateTaskAction
TASK_DEPENDENCY CREATE → CreateTaskDependencyAction
QUOTE_TERM CREATE → QuoteTermCreateAction
CHANGE_REQUEST CREATE → CreateChangeRequestAction
```

Rules:

```text
1. No direct repository mutation.
2. Domain validations run.
3. IAM validations run.
4. Baseline/change guard runs.
5. Events/audit from domain action preserved.
```

## 14.2 Baselined project rule

If current baseline exists and suggestion changes controlled fields:

```text
Apply must create a ChangeRequest draft or append to existing ChangeRequest.
```

It must not directly mutate:

```text
WBS
Task
Dependency
Milestone
Finance
Quote
Schedule overrides
```

unless override policy explicitly permits and audits.

Recommended:

```text
Create ChangeRequest draft with AI-suggested items.
```

## 14.3 Transaction rule

Apply modes:

```text
ALL_OR_NOTHING
BEST_EFFORT
CREATE_CHANGE_REQUEST_ONLY
```

Recommended Phase 21 default:

```text
ALL_OR_NOTHING for direct apply.
CREATE_CHANGE_REQUEST_ONLY if baseline exists.
```

Completion file must document.

---

# 15. Review lifecycle

## 15.1 Generated

AI output parsed and saved.

```text
Suggestion status = GENERATED
Items status = PROPOSED
```

## 15.2 Review

Reviewer can:

```text
accept all
reject all
accept item
reject item
edit proposal before apply if supported
```

Editing AI suggestion:

```text
Optional. If supported, store editedBy and editedPayload.
```

Recommended Phase 21:

```text
No edit in-place; user can accept/reject or apply via domain form.
```

## 15.3 Apply

Only accepted items apply.

Rejected items never apply.

Applied results stored.

---

# 16. Integration with Event Registry

Recommended source system:

```text
SCOPERY_AI_PLANNING
```

Required events:

```text
AI_PLANNING_RUN_CREATED
AI_PLANNING_RUN_STARTED
AI_PLANNING_RUN_COMPLETED
AI_PLANNING_RUN_FAILED
AI_PLANNING_RUN_CANCELLED

AI_PLANNING_CONTEXT_SNAPSHOT_CREATED
AI_PLANNING_SUGGESTION_GENERATED
AI_PLANNING_SUGGESTION_REVIEW_STARTED
AI_PLANNING_SUGGESTION_ACCEPTED
AI_PLANNING_SUGGESTION_REJECTED
AI_PLANNING_SUGGESTION_APPLIED
AI_PLANNING_SUGGESTION_APPLY_FAILED

AI_PLANNING_SUGGESTION_ITEM_ACCEPTED
AI_PLANNING_SUGGESTION_ITEM_REJECTED
AI_PLANNING_SUGGESTION_ITEM_APPLIED
AI_PLANNING_SUGGESTION_ITEM_APPLY_FAILED

AI_PLANNING_CHANGE_REQUEST_DRAFTED
AI_PLANNING_QUOTE_TERMS_DRAFTED
AI_PLANNING_FINANCE_INSIGHT_GENERATED
AI_PLANNING_SCHEDULE_RISK_EXPLAINED
```

Standard variables:

```text
actor.userId
organization.id
workspace.id
project.id
aiPlanningRun.id
aiAgent.id
modelDeployment.id
promptTemplate.id
suggestion.id
suggestion.type
suggestionItem.id
suggestionItem.type
target.type
target.id
apply.status
occurredAt
traceId
```

---

# 17. Notification integration

Phase 21 may trigger internal notifications:

```text
AI_PLANNING_SUGGESTION_GENERATED → requester
AI_PLANNING_SUGGESTION_APPLY_FAILED → requester/admin
AI_PLANNING_CHANGE_REQUEST_DRAFTED → requester
```

Recommended:

```text
Seed events only or use Phase 20 rules if already configured.
```

Do not notify external clients.

---

# 18. Audit/activity requirements

Audit-sensitive:

```text
AI context snapshot created
AI output generated
AI suggestion accepted/rejected
AI suggestion applied
AI apply failed
AI suggested finance/quote content
AI created ChangeRequest draft
```

Activity log:

```text
AI_PLANNING_RUN_COMPLETED
AI_PLANNING_SUGGESTION_GENERATED
AI_PLANNING_SUGGESTION_APPLIED
AI_PLANNING_CHANGE_REQUEST_DRAFTED
```

Reason:

```text
AI affects planning decisions and may propose commercial/scope changes.
```

---

# 19. Usage policy and limits

Phase 21 must enforce Phase 07 usage policy.

Rules:

```text
1. Agent/model must be active.
2. Workspace/org must be allowed to use AI.
3. User must have permission.
4. Run type must be allowed.
5. Token/cost limit checked before execution if implemented.
6. Rate limit applied.
7. Sensitive data policy applied.
8. Execution logged.
```

If usage quota exists:

```text
Run should fail cleanly when quota exceeded.
```

---

# 20. Error catalog requirements

Exact names follow project convention, but these concepts must exist.

```text
AI_PLANNING_RUN_NOT_FOUND
AI_PLANNING_RUN_PROJECT_ARCHIVED
AI_PLANNING_RUN_INVALID_TYPE
AI_PLANNING_RUN_AGENT_NOT_FOUND
AI_PLANNING_RUN_AGENT_INACTIVE
AI_PLANNING_RUN_PROMPT_NOT_FOUND
AI_PLANNING_RUN_PROMPT_INACTIVE
AI_PLANNING_RUN_USAGE_NOT_ALLOWED
AI_PLANNING_RUN_QUOTA_EXCEEDED
AI_PLANNING_RUN_FAILED

AI_PLANNING_CONTEXT_BUILD_FAILED
AI_PLANNING_CONTEXT_SECTION_ACCESS_DENIED
AI_PLANNING_CONTEXT_TOO_LARGE

AI_PLANNING_SUGGESTION_NOT_FOUND
AI_PLANNING_SUGGESTION_INVALID_STATUS
AI_PLANNING_SUGGESTION_PARSE_FAILED
AI_PLANNING_SUGGESTION_SCHEMA_INVALID
AI_PLANNING_SUGGESTION_ITEM_NOT_FOUND
AI_PLANNING_SUGGESTION_ITEM_INVALID_PAYLOAD
AI_PLANNING_SUGGESTION_ITEM_UNSUPPORTED_TYPE

AI_PLANNING_APPLY_NOT_ACCEPTED
AI_PLANNING_APPLY_PERMISSION_DENIED
AI_PLANNING_APPLY_AGENT_NOT_ALLOWED
AI_PLANNING_APPLY_BASELINE_REQUIRES_CHANGE_REQUEST
AI_PLANNING_APPLY_DOMAIN_VALIDATION_FAILED
AI_PLANNING_APPLY_FAILED

AI_PLANNING_FINANCE_ACCESS_DENIED
AI_PLANNING_QUOTE_ACCESS_DENIED
AI_PLANNING_RATE_ACCESS_DENIED
AI_PLANNING_ACCESS_DENIED
```

---

# 21. Business rules master

## 21.1 Run rules

```text
AIP-RUN-001 Project must exist.
AIP-RUN-002 Project must not be ARCHIVED.
AIP-RUN-003 Actor must have AI planning permission.
AIP-RUN-004 Agent must be active and allowed.
AIP-RUN-005 Prompt template must be active.
AIP-RUN-006 Usage policy must allow run.
AIP-RUN-007 AI execution logged.
AIP-RUN-008 Run does not mutate business data.
```

## 21.2 Context rules

```text
AIP-CTX-001 Context includes only authorized data.
AIP-CTX-002 Sensitive fields redacted if unauthorized.
AIP-CTX-003 Finance/quote data require permission.
AIP-CTX-004 Context snapshot immutable.
AIP-CTX-005 Full document RAG not in Phase 21.
```

## 21.3 Suggestion rules

```text
AIP-SUG-001 AI output stored as suggestion.
AIP-SUG-002 Suggestion item schema validated.
AIP-SUG-003 Suggestion is proposal only.
AIP-SUG-004 Human review required before apply.
AIP-SUG-005 Rejected suggestion cannot be applied.
AIP-SUG-006 Suggestion preserves prompt/model/context references.
```

## 21.4 Apply rules

```text
AIP-APL-001 Only accepted items can apply.
AIP-APL-002 User must have underlying business permission.
AIP-APL-003 Agent/tool must be authorized.
AIP-APL-004 Apply uses domain actions.
AIP-APL-005 Apply does not bypass baseline guard.
AIP-APL-006 Baselined controlled changes create ChangeRequest.
AIP-APL-007 Apply transactional by mode.
AIP-APL-008 Apply audited.
```

## 21.5 Finance/quote rules

```text
AIP-FIN-001 Finance insight requires finance permission.
AIP-FIN-002 Margin details require margin permission if separated.
AIP-FIN-003 AI cannot change finance scenario.
AIP-QTE-001 Quote drafting requires quote permission.
AIP-QTE-002 AI cannot approve/send quote.
AIP-QTE-003 AI quote text must be human reviewed.
```

---

# 22. Required tests

Phase 21 is incomplete without tests.

## 22.1 AI planning run tests

```text
createPlanningRun_valid_success
createPlanningRun_archivedProject_rejected
createPlanningRun_withoutPermission_forbidden
createPlanningRun_inactiveAgent_rejected
createPlanningRun_inactivePrompt_rejected
createPlanningRun_usagePolicyDenied_rejected
planningRun_logsAiExecution
planningRun_doesNotMutateProjectData
planningRun_idempotency_sameKey_noDuplicateRun_ifImplemented
```

## 22.2 Context builder tests

```text
contextBuilder_projectView_includesProjectBasics
contextBuilder_withoutFinancePermission_masksFinance
contextBuilder_withFinancePermission_includesFinanceSummary
contextBuilder_withoutQuotePermission_masksQuote
contextBuilder_excludesPrivateCapacityDetails
contextBuilder_excludesSalaryPayroll
contextBuilder_recordsRedactionSummary
contextBuilder_largeProject_summarizesOrLimits
```

## 22.3 Suggestion parsing tests

```text
parseProjectPlanSuggestion_valid_success
parseInvalidJson_marksRunFailed
parseUnknownItemType_rejected
parseInvalidTaskEstimate_rejected
parseSuggestionStoresPromptModelContextRefs
suggestionGenerated_noDomainMutation
```

## 22.4 Review tests

```text
acceptSuggestion_valid_success
rejectSuggestion_requiresReason_optionalPolicy
acceptItem_valid_success
rejectItem_valid_success
rejectedSuggestion_cannotApply
acceptedSuggestion_canApply
reviewActionsAudited
```

## 22.5 Apply tests

```text
applyAcceptedCreateTask_usesCreateTaskAction
applyAcceptedCreateWbs_usesCreateWbsAction
applyAcceptedEstimateUpdate_usesUpdateTaskAction
applyDependencySuggestion_usesDependencyAction
applyWithoutUnderlyingPermission_forbidden
applyWithoutAgentToolPermission_forbidden
applyDomainValidationFailure_recordsFailedResult
applyAllOrNothing_rollsBackOnFailure
applyBaselinedProject_createsChangeRequestInsteadOfDirectMutation
applyDoesNotBypassIam
```

## 22.6 Finance/quote AI tests

```text
financeInsight_withoutFinancePermission_forbiddenOrMasked
financeInsight_withPermission_success
financeInsight_doesNotChangeFinanceScenario
quoteDraft_withoutQuotePermission_forbidden
quoteDraft_generatesQuoteTermSuggestion
quoteDraft_doesNotApproveOrSendQuote
```

## 22.7 ChangeRequest AI tests

```text
draftChangeRequest_validSuggestion_success
draftChangeRequest_applyCreatesChangeRequestDraft
draftChangeRequest_doesNotSubmitApproveApply
changeRequestDraft_financeImpactMaskedWithoutPermission
```

## 22.8 Event/audit tests

```text
aiPlanningEventSeeder_firstRun_createsDefinitions
aiPlanningEventSeeder_secondRun_noDuplicates
aiPlanningPermissionSeeder_authoritiesExist
aiSuggestionGenerated_eventEmitted
aiSuggestionApplied_eventEmitted
aiApplyFailed_eventEmitted
aiContextSnapshot_audited
```

---

# 23. Manual verification checklist

Completion file must include:

```text
1. Seed AI planning prompts/events/permissions.
2. Create project with WBS/tasks.
3. Run PROJECT_PLAN_DRAFT AI planning run.
4. Confirm context snapshot includes only authorized sections.
5. Confirm AI output saved as suggestion, not applied.
6. Accept selected suggestion items.
7. Apply accepted items on non-baselined project and confirm domain actions used.
8. Create baseline.
9. Run AI estimate suggestion.
10. Apply accepted estimate suggestion and confirm ChangeRequest draft is created, not direct task update.
11. Run finance insight without finance permission and confirm blocked/masked.
12. Run quote draft with quote permission and confirm QuoteTerm suggestion only.
13. Confirm AI cannot approve quote/baseline/change request.
14. Confirm events/activity/audit created.
```

---

# 24. Acceptance criteria

Phase 21 is accepted only if:

```text
1. Current AI planning capability is classified against TO-BE.
2. AIPlanningRun implemented/tested.
3. AIPlanningContextSnapshot implemented/tested.
4. AIPlanningSuggestion and SuggestionItem implemented/tested.
5. Suggestion review lifecycle implemented/tested.
6. Safe apply service implemented/tested.
7. Context builder enforces IAM and masking.
8. AI output never mutates business data without human apply.
9. Apply uses domain actions and IAM.
10. Baselined controlled changes route through ChangeRequest.
11. Finance/quote AI outputs are permission-protected.
12. Prompt/event/permission seeders idempotent.
13. Activity/audit/outbox follows Phase 04.
14. Phase 21 does not falsely claim autonomous AI, full RAG, semantic index, workflow approval, or external communication.
15. mvn compile passes.
16. mvn test passes.
17. Completion file exists and includes gap matrix.
```

Agent must not mark complete if:

```text
mvn test fails
AI suggestion directly mutates project data
context includes unauthorized finance/quote data
AI can apply without underlying business permission
AI can bypass baseline/change guard
AI can approve/send quote
AI can approve/apply ChangeRequest
AI can grant permissions
salary/payroll appears in context/output
full document RAG is claimed without document/semantic index modules
```

---

# 25. Required phase completion file

Agent must create:

```text
docs/phase-complete/PHASE_21_AI_ASSISTED_PROJECT_PLANNING_TO_BE_COMPLETE.md
```

Required sections:

```text
# Phase 21 — AI-assisted Project Planning TO-BE Complete

## 1. Summary
## 2. Source Inputs Reviewed
## 3. Current vs TO-BE Classification Matrix
## 4. Implemented in Current BE
## 5. Implemented / Hardened in This Phase
## 6. Seed-only Items Added
## 7. Deferred Items and Target Phase
## 8. AI Safety Boundary Decision
## 9. Entity Mapping
## 10. API Changes
## 11. Prompt Catalog / Agent Config
## 12. Context Builder Strategy
## 13. Suggestion Schema Strategy
## 14. Review Lifecycle Strategy
## 15. Safe Apply Strategy
## 16. Baseline / ChangeRequest Integration
## 17. Finance / Quote Permission Strategy
## 18. Usage Policy / Quota Strategy
## 19. Authorization Matrix
## 20. Event Registry Seeder Matrix
## 21. Activity / Audit / Outbox Notes
## 22. Idempotency Strategy
## 23. Tests Added
## 24. Commands Run
## 25. Test Results
## 26. Manual Verification
## 27. Assumptions
## 28. Deviations From Prompt
## 29. Known Risks
## 30. Future Phases That Must Return to AI Planning
```

---

# 26. Future phases that must return to AI planning

## 26.1 Phase 22 — Reporting / Dashboard / Export

AI can summarize:

```text
project dashboard
schedule risk report
finance scenario report
quote status report
change request report
```

But reporting permissions must apply.

## 26.2 Phase 23 — Core Hardening

Review:

```text
prompt injection safety
context size/performance
audit completeness
permission coverage
apply transaction safety
model failure handling
cost/usage limits
```

## 26.3 Phase 27 — Full Document Hub

AI can draft/summarize:

```text
documents
project specs
proposal document
change request documents
```

Only after Document Hub exists.

## 26.4 Phase 34 — Workflow / Approval

AI can draft approval summaries but cannot approve.

Workflow can require human approval before AI apply.

## 26.5 Phase 35 — Advanced Notifications

AI can summarize notification digests and prioritize alerts.

## 26.6 Phase 37 — Time / Attendance / Expense

AI can compare estimated vs actual and suggest corrective actions.

## 26.7 Phase 41 — Knowledge Graph / Semantic Index

Full RAG/semantic project intelligence:

```text
document retrieval
semantic relationship graph
cross-project recommendations
knowledge-grounded AI answers
```

---

# 27. Agent anti-bịa rules

The agent must not:

```text
1. Claim AI autonomously manages projects.
2. Let AI mutate business data without human apply.
3. Let AI bypass domain actions.
4. Let AI bypass IAM.
5. Let AI bypass baseline/change request guard.
6. Let AI approve baseline/change request/quote/finance.
7. Let AI send external messages.
8. Let AI grant permission.
9. Include unauthorized finance/quote/rate data in context.
10. Include salary/payroll data.
11. Claim full RAG over documents.
12. Claim semantic knowledge graph exists.
13. Hide prompt/model/context traceability.
14. Hide deferred AI/document/knowledge graph gaps.
```

---

# 28. Prompt to give coding agent

```text
You are implementing Phase 21 — TO-BE AI-assisted Project Planning, Suggestion Governance, Human Approval & Safe Apply.

This is not an as-is documentation task.

Before coding, read:
- CLAUDE.md / CLAUDE.ms
- Coding_convention.md
- Phase 00 master roadmap
- Phase 01 API/security baseline
- Phase 02 IAM TO-BE spec
- Phase 03 Workspace TO-BE spec
- Phase 04 Platform Audit/Outbox/Idempotency spec
- Phase 05 Event Registry TO-BE spec
- Phase 06 Notification Engine TO-BE spec
- Phase 07 AI Agent Platform TO-BE spec
- Phase 08 Knowledge/Document Type TO-BE spec
- Phase 09 Project Core TO-BE spec
- Phase 10 Project Authorization TO-BE spec
- Phase 11 Project Template TO-BE spec
- Phase 12 Resource Calendar/Capacity TO-BE spec
- Phase 13 Task Scheduling TO-BE spec
- Phase 14 WBS Gantt TO-BE spec
- Phase 15 Rate Card TO-BE spec
- Phase 16 Estimation Roll-up TO-BE spec
- Phase 17 Project Budget/Margin TO-BE spec
- Phase 18 Quote TO-BE spec
- Phase 19 Baseline/Change Request TO-BE spec
- Phase 20 Project Events/Notifications TO-BE spec
- Current backend code, migrations, tests

Your task:
1. Compare current AI project planning capability against this Phase 21 TO-BE spec.
2. Classify each capability as CURRENTLY_IMPLEMENTED, PARTIALLY_IMPLEMENTED, MUST_IMPLEMENT_IN_PHASE_21, SEED_ONLY_IN_PHASE_21, DEFERRED_TO_PHASE_XX, or NOT_IN_SCOPE_FOR_BACKEND_NOW.
3. Implement only Phase 21 required items.
4. Implement AIPlanningRun, AIPlanningContextSnapshot, AIPlanningSuggestion, AIPlanningSuggestionItem, AIPlanningReviewAction, AIPlanningApplyResult.
5. Implement access-aware context builder.
6. Seed AI planning prompts, agent config, permissions, and events.
7. Implement suggestion review lifecycle.
8. Implement safe apply through domain actions with human approval.
9. Enforce baseline/change request routing for controlled changes.
10. Enforce finance/quote/rate permission masking.
11. Add tests listed in this spec.
12. Run mvn compile and mvn test.
13. Create docs/phase-complete/PHASE_21_AI_ASSISTED_PROJECT_PLANNING_TO_BE_COMPLETE.md with full gap matrix.

Do not implement or claim autonomous AI mutation, AI permission grants, quote/baseline/change approval by AI, full document RAG, semantic knowledge graph, workflow automation, external client communication, model fine-tuning, payroll/salary usage, or AI sending external notifications in this phase.
```

---

# 29. Quick tracking matrix

| Capability | Current backend | Phase 21 action | Later phase |
|---|---|---|---|
| AI Agent Platform | Exists from Phase 07 | Reuse | — |
| AIPlanningRun | Missing/unknown | Must implement | — |
| AIPlanningContextSnapshot | Missing/unknown | Must implement | Phase 23 hardening |
| AIPlanningSuggestion | Missing/unknown | Must implement | — |
| SuggestionItem | Missing/unknown | Must implement | — |
| Suggestion review | Missing/unknown | Must implement | Workflow Phase 34 later |
| Safe apply | Missing/unknown | Must implement | — |
| Project plan draft | Missing/unknown | Must implement | — |
| Template recommendation | Missing/unknown | Implement/defer based on Phase 11 | — |
| Task estimate suggestion | Missing | Must implement | — |
| Cost role suggestion | Missing | Must implement | — |
| Schedule risk explanation | Missing | Must implement | — |
| Finance insight | Missing | Must implement with permissions | — |
| Quote text draft | Missing | Must implement if quote exists | Phase 27 document generation |
| ChangeRequest draft | Missing | Must implement | — |
| AI notification summary | Missing | Defer | Phase 22/35 |
| Full document RAG | Missing | Defer | Phase 27/41 |
| Semantic knowledge graph | Missing | Defer | Phase 41 |
| Autonomous mutation | Not allowed | Not implement | Maybe never / strict workflow |
| AI approval | Not allowed | Not implement | Human workflow only |

---

# 30. Final principle

Phase 21 is not complete when "AI returns text."

Phase 21 is complete when Scopery can safely turn AI into governed project proposals:

```text
authorized context
+ approved AI agent
+ versioned prompt
+ execution log
+ structured suggestion
+ human review
+ domain-action apply
+ audit trail
= safe AI-assisted planning
```

AI suggests.

Human decides.

Domain actions apply.

IAM controls access.

Baseline controls commitments.

Audit preserves trust.
