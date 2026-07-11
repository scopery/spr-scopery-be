# PHASE 25 — TO-BE RAID, Decision Management, Project Governance Log, Risk Response & Dependency Control

> Project: Scopery Backend  
> Phase: 25  
> Document type: TO-BE implementation-grade specification  
> Status: Planning / Gap-driven implementation spec  
> Roadmap group: Post-core Dynamic Work OS expansion  
> Depends on: Phase 00 — Master Backend Roadmap, Phase 01 — API Path / Security Baseline, Phase 02 — IAM Core, Phase 03 — Workspace / Organization / Team, Phase 04 — Platform Audit / Outbox / Idempotency, Phase 05 — Event Registry, Phase 06 — Notification Engine, Phase 09 — Project Core / Phase / WBS / Task, Phase 10 — Project Authorization, Phase 12 — Resource Calendar / Capacity, Phase 13 — Task Scheduling Engine, Phase 14 — WBS-driven Gantt, Phase 16 — Estimation Roll-up, Phase 17 — Project Budget / Margin, Phase 19 — Baseline / Change Request, Phase 20 — Project Events / Notifications, Phase 21 — AI-assisted Project Planning, Phase 22 — Reporting / Dashboard / Export, Phase 23 — Core Hardening, Phase 24 — Scope / Deliverable / Acceptance  
> API base: `/api`  
> Primary module: `modules/raid` or `modules/project/raid` depending on repository architecture  
> Related modules: `project`, `task`, `schedule`, `capacity`, `scope`, `deliverable`, `baseline`, `change-request`, `notification`, `reporting`, `ai-planning`, `iam`, `eventregistry`, future `meeting`, `workflow`, `quality`, `contract`, `client-portal`  
> Important rule: Phase 25 introduces RAID and Decision governance. It does **not** implement full meeting management, workflow automation, legal contract governance, external client portal approval, or actual financial accounting.

---

# 0. Purpose

Phase 25 adds project governance logs and control mechanisms around:

```text
R = Risks
A = Assumptions
I = Issues
D = Dependencies
+
Decisions
Actions
Lessons / notes foundation
```

Earlier phases created structured project planning and control:

```text
Project / WBS / Task
Schedule / Gantt
Capacity
Estimation / Finance / Quote
Baseline / ChangeRequest
Notifications
Reporting
Scope / Deliverable / Acceptance
```

Phase 25 answers:

```text
What risks threaten the project?
What assumptions are driving the plan?
What issues are already happening?
What external/internal dependencies can block delivery?
What decisions have been made?
Who owns each item?
What is the impact on scope, schedule, finance, quote, baseline, deliverable, and acceptance?
Which items need escalation or ChangeRequest?
Which actions are needed to resolve them?
How are RAID items reported, notified, audited, and closed?
```

Phase 25 is the governance memory of project delivery.

---

# 1. Source inputs

Before coding Phase 25, the agent must read:

```text
1. Current backend codebase
2. Phase 09 Project Core implementation
3. Phase 10 Project Authorization implementation
4. Phase 12 Capacity implementation
5. Phase 13 Schedule implementation
6. Phase 14 Gantt implementation
7. Phase 17 Finance implementation
8. Phase 19 Baseline / Change Request implementation
9. Phase 20 Notification implementation
10. Phase 21 AI-assisted Planning implementation
11. Phase 22 Reporting implementation
12. Phase 24 Scope / Deliverable / Acceptance implementation
13. Phase 23 hardening completion file
14. Current IAM permission seeders
15. Current EventDefinition seeders
16. Current notification rule seeders
17. Existing risk/issue/decision code if any
```

The coding agent must inspect real code, not assume implementation.

---

# 2. Current expected backend state

After Phase 24, the backend should have:

```text
Project
ProjectPhase
WBS
Task
TaskDependency
ScheduleRun
SchedulingIssue
Capacity
ScopePackage
ScopeItem
Deliverable
AcceptanceCriteria
Baseline
ChangeRequest
Notification
Reporting
AIPlanningSuggestion
```

Likely missing:

```text
RaidItem
Risk
Assumption
Issue
DependencyLog
DecisionRecord
DecisionOption
DecisionImpact
RaidAction
RaidLink
RiskResponsePlan
IssueResolution
Escalation
RAID report
Decision log report
RAID notification rules
RAID AI suggestion foundation
```

Phase 25 implements these.

---

# 3. Target statement

Phase 25 must deliver:

```text
1. Unified RAID item model or separate risk/assumption/issue/dependency models.
2. Decision record model.
3. RAID severity/probability/impact scoring.
4. Ownership, due dates, status, escalation, and actions.
5. Link RAID/decision records to project, phase, WBS, task, deliverable, baseline, change request, quote/finance where allowed.
6. Risk response planning.
7. Issue resolution lifecycle.
8. Dependency tracking lifecycle.
9. Decision options and decision outcomes.
10. Integration with ChangeRequest when impact requires controlled change.
11. Notifications for high-risk, overdue actions, escalations, and decisions.
12. Reporting/dashboard extensions.
13. AI-assisted suggestions as proposal only.
14. IAM permissions, events, audit, idempotency, and tests.
```

---

# 4. Boundary decisions

## 4.1 RAID is governance, not task management replacement

A RAID item can create or link tasks/actions, but does not replace normal tasks.

```text
RaidAction can optionally create/link Project Task.
```

## 4.2 Risk is potential; issue is actual

```text
Risk = uncertain future event.
Issue = current problem already happening.
```

Converting risk to issue must be explicit and audited.

## 4.3 DependencyLog is not TaskDependency

`TaskDependency` is scheduling logic.

`DependencyLog` is governance tracking for external/internal dependency.

Example:

```text
TaskDependency:
Task B cannot start until Task A finishes.

DependencyLog:
Waiting for client to provide API credentials.
```

A DependencyLog can link to TaskDependency but is not the same entity.

## 4.4 Decision is not approval workflow

DecisionRecord captures a decision and rationale.

It does not implement full multi-step approval workflow.

Workflow automation is deferred to Phase 34.

## 4.5 RAID impact is forecast/governance impact

RAID impact can reference schedule/cost/scope/margin impact.

It does not create actual cost, invoice, or revenue.

---

# 5. Classification labels

Use these labels in the completion file:

| Label | Meaning |
|---|---|
| `CURRENTLY_IMPLEMENTED` | Existing backend already supports it with tests. |
| `PARTIALLY_IMPLEMENTED` | Some support exists but gaps remain. |
| `MUST_IMPLEMENT_IN_PHASE_25` | Required now. |
| `SEED_ONLY_IN_PHASE_25` | Seed definitions/events/permissions only. |
| `DEFERRED_TO_PHASE_XX` | Later phase returns. |
| `DEFERRED_TO_POST_23_BACKLOG` | Dynamic Work OS expansion backlog. |
| `NOT_IN_SCOPE_FOR_PHASE_25` | Explicitly not in this phase. |

---

# 6. Required capabilities

---

## 6.1 RAID-001 — Unified RaidItem

Classification: `MUST_IMPLEMENT_IN_PHASE_25`

Purpose:

```text
Represent risk, assumption, issue, dependency, or governance action item in one model.
```

Types:

```text
RISK
ASSUMPTION
ISSUE
DEPENDENCY
ACTION
```

Rules:

```text
1. RaidItem belongs to one project.
2. Project must not be archived.
3. Type required.
4. Title required.
5. Owner optional but recommended.
6. Severity/probability/impact fields depend on type.
7. Status lifecycle depends on type.
8. Item can link to project artifacts.
9. Item must not expose finance/quote values without permission.
10. Post-baseline item impact may require ChangeRequest.
```

Alternative:

```text
Separate Risk, Assumption, Issue, Dependency entities.
```

Recommended Phase 25:

```text
Unified RaidItem with type-specific fields.
```

Reason:

```text
Simpler API/reporting; less duplication; still extensible.
```

---

## 6.2 RAID-002 — Risk scoring

Classification: `MUST_IMPLEMENT_IN_PHASE_25`

Risk fields:

```text
probability
impact
severity
riskScore
riskResponseStrategy
riskTrigger
riskOwner
targetResolutionDate
```

Probability:

```text
LOW
MEDIUM
HIGH
VERY_HIGH
```

Impact:

```text
LOW
MEDIUM
HIGH
CRITICAL
```

Risk response strategy:

```text
AVOID
MITIGATE
TRANSFER
ACCEPT
EXPLOIT
ENHANCE
SHARE
```

Recommended scoring:

```text
riskScore = probabilityWeight × impactWeight
```

Weights:

```text
LOW = 1
MEDIUM = 2
HIGH = 3
VERY_HIGH / CRITICAL = 4
```

Rules:

```text
1. Risk score formula version stored.
2. High/critical risks can trigger notifications.
3. Accepted risk requires reason.
4. Closed risk requires outcome note.
5. Risk can be converted to Issue.
```

---

## 6.3 RAID-003 — Assumption tracking

Classification: `MUST_IMPLEMENT_IN_PHASE_25`

Purpose:

```text
Record assumptions behind estimates, schedules, scope, quote, finance, and acceptance.
```

Fields:

```text
assumptionStatement
validationStatus
validationOwner
validationDueDate
impactIfFalse
```

Validation status:

```text
UNVALIDATED
VALIDATING
VALIDATED
INVALIDATED
SUPERSEDED
```

Rules:

```text
1. Invalidated assumption can trigger Risk, Issue, or ChangeRequest.
2. Important assumptions can link to Baseline/Quote/Finance/Deliverable.
3. Assumption changes after baseline may require ChangeRequest if controlled.
```

---

## 6.4 RAID-004 — Issue management

Classification: `MUST_IMPLEMENT_IN_PHASE_25`

Purpose:

```text
Track current project problems and their resolution.
```

Issue fields:

```text
issueCategory
severity
impactSummary
rootCause
resolutionPlan
resolutionDueDate
resolvedAt
resolvedBy
```

Issue categories:

```text
SCOPE
SCHEDULE
RESOURCE
TECHNICAL
QUALITY
CLIENT
VENDOR
FINANCE
SECURITY
COMPLIANCE
OTHER
```

Rules:

```text
1. Issue is actual, not potential.
2. Issue severity required.
3. Open critical issue can trigger notification.
4. Issue can create/link ChangeRequest.
5. Resolution requires note.
6. Resolved issue can be reopened with reason.
```

---

## 6.5 RAID-005 — Dependency log

Classification: `MUST_IMPLEMENT_IN_PHASE_25`

Purpose:

```text
Track project governance dependencies.
```

Dependency types:

```text
CLIENT_INPUT
VENDOR_DELIVERY
INTERNAL_TEAM
TECHNICAL_ENVIRONMENT
APPROVAL
LEGAL
FINANCE
SECURITY
DATA
OTHER
```

Status:

```text
OPEN
WAITING
RECEIVED
BLOCKED
RESOLVED
CANCELLED
```

Rules:

```text
1. Dependency owner and due date recommended.
2. Blocked dependency can create issue.
3. Dependency can link to task dependency but does not replace it.
4. Overdue dependency triggers notification.
```

---

## 6.6 RAID-006 — RAID action

Classification: `MUST_IMPLEMENT_IN_PHASE_25`

Purpose:

```text
Track action items needed to mitigate risks, resolve issues, validate assumptions, or unblock dependencies.
```

Fields:

```text
title
description
ownerUserId
dueDate
status
linkedTaskId optional
completionNote
```

Status:

```text
OPEN
IN_PROGRESS
DONE
CANCELLED
OVERDUE
```

Rules:

```text
1. RaidAction belongs to RaidItem.
2. Action can optionally create/link a Project Task.
3. Completing action does not automatically close RaidItem unless policy says all actions done.
4. Overdue actions trigger notification.
```

---

## 6.7 RAID-007 — Escalation

Classification: `MUST_IMPLEMENT_IN_PHASE_25`

Purpose:

```text
Escalate high-risk, critical issue, overdue dependency, or overdue action to accountable stakeholders.
```

Escalation levels:

```text
PROJECT_MANAGER
WORKSPACE_ADMIN
EXECUTIVE
CLIENT
```

Client escalation is internal record only unless client portal exists.

Rules:

```text
1. Escalation requires reason.
2. Escalation creates event/notification.
3. Escalated item remains visible in reports.
4. External client notification deferred.
```

---

## 6.8 DEC-001 — DecisionRecord

Classification: `MUST_IMPLEMENT_IN_PHASE_25`

Purpose:

```text
Record project decisions, rationale, options considered, and impact.
```

Decision categories:

```text
SCOPE
TECHNICAL
ARCHITECTURE
SCHEDULE
RESOURCE
FINANCE
QUOTE
ACCEPTANCE
RISK_RESPONSE
CHANGE_REQUEST
OTHER
```

Status:

```text
PROPOSED
UNDER_REVIEW
DECIDED
REJECTED
SUPERSEDED
ARCHIVED
```

Rules:

```text
1. Decision belongs to project.
2. Decision title required.
3. Decision outcome required when status DECIDED.
4. Decision must store rationale.
5. Decision can link to RAID items, tasks, deliverables, ChangeRequests, baseline, quote, finance scenario.
6. Superseded decision links to replacement decision.
7. Decision does not implement approval workflow.
```

---

## 6.9 DEC-002 — Decision options

Classification: `MUST_IMPLEMENT_IN_PHASE_25`

Purpose:

```text
Record options considered before decision.
```

Fields:

```text
optionTitle
optionDescription
pros
cons
estimatedImpact
selectedFlag
```

Rules:

```text
1. Decision can have many options.
2. At most one selected option unless decision type allows multiple.
3. Selected option should match decision outcome.
4. Options immutable after decision decided unless reopen/supersede.
```

---

## 6.10 DEC-003 — Decision impact

Classification: `MUST_IMPLEMENT_IN_PHASE_25`

Purpose:

```text
Capture impact of a decision.
```

Impact dimensions:

```text
scopeImpact
scheduleImpactDays
estimateHoursImpact
costImpact
revenueImpact
marginImpact
riskImpact
deliverableImpact
acceptanceImpact
```

Rules:

```text
1. Finance impacts require finance permission to view.
2. Quote impacts require quote permission to view.
3. Impact is forecast/governance impact, not actual accounting.
4. Significant impact can require ChangeRequest.
```

---

## 6.11 RAID-008 — Links to project artifacts

Classification: `MUST_IMPLEMENT_IN_PHASE_25`

RAID/decision items can link to:

```text
ProjectPhase
WBS node
Task
TaskDependency
ScheduleRun
SchedulingIssue
Resource allocation
EstimationRun
FinanceScenario
QuoteVersion
Baseline
ChangeRequest
ScopeItem
Deliverable
AcceptanceCriteria
Notification
AIPlanningSuggestion
```

Rules:

```text
1. Linked resource must belong to same project.
2. Link type required.
3. Link does not grant access.
4. Sensitive linked data masked by permission.
```

---

## 6.12 RAID-009 — ChangeRequest integration

Classification: `MUST_IMPLEMENT_IN_PHASE_25`

Rules:

```text
1. High-impact RAID item can create ChangeRequest draft.
2. Decision can require ChangeRequest.
3. Converting Risk/Issue impact into ChangeRequest is explicit.
4. Applying ChangeRequest can update RAID/Decision status.
5. ChangeRequest report includes linked RAID/Decision.
```

---

## 6.13 RAID-010 — AI-assisted RAID suggestions

Classification: `SEED_ONLY_IN_PHASE_25` or `MUST_IMPLEMENT_IN_PHASE_25` if Phase 21 tool registry exists.

AI can suggest:

```text
risk from schedule/capacity issue
issue from overdue blocked tasks
assumption from quote/finance context
dependency from task blockers
decision draft from change request discussion
mitigation actions
```

Rules:

```text
1. AI output is proposal only.
2. Human approval required to create RAID item.
3. AI cannot escalate automatically.
4. AI cannot create ChangeRequest without human approve/apply.
5. AI respects permissions.
```

---

## 6.14 RPT-001 — RAID / Decision reporting

Classification: `MUST_IMPLEMENT_IN_PHASE_25`

Reports:

```text
RAID summary
risk register
issue log
assumption log
dependency log
overdue RAID actions
decision log
decision impact report
RAID items by deliverable/task/phase
```

Dashboard KPIs:

```text
openRisks
criticalRisks
openIssues
criticalIssues
openDependencies
overdueDependencies
openRaidActions
overdueRaidActions
pendingDecisions
decidedThisPeriod
```

---

# 7. Entity model TO-BE

---

## 7.1 RaidItem — `project_raid_item`

Fields:

```text
id UUID PK
project_id UUID NOT NULL
workspace_id UUID NOT NULL
type VARCHAR(50) NOT NULL
code VARCHAR(100) NULL
title VARCHAR(255) NOT NULL
description TEXT NULL
status VARCHAR(50) NOT NULL
category VARCHAR(100) NULL
owner_user_id UUID NULL
priority VARCHAR(50) NULL
severity VARCHAR(50) NULL
probability VARCHAR(50) NULL
impact VARCHAR(50) NULL
risk_score DECIMAL(8,4) NULL
risk_score_formula_version VARCHAR(50) NULL
response_strategy VARCHAR(50) NULL
trigger_description TEXT NULL
root_cause TEXT NULL
resolution_plan TEXT NULL
impact_summary TEXT NULL
due_date DATE NULL
resolved_at TIMESTAMP NULL
resolved_by UUID NULL
resolution_note TEXT NULL
escalated_at TIMESTAMP NULL
escalated_by UUID NULL
escalation_level VARCHAR(50) NULL
escalation_reason TEXT NULL
source_ai_suggestion_id UUID NULL
source_change_request_id UUID NULL
created_at / created_by
updated_at / updated_by
archived_at / archived_by
trace_id VARCHAR(100) NULL
version INT
```

Status values by type can be normalized but minimum statuses:

```text
OPEN
MONITORING
IN_PROGRESS
BLOCKED
ESCALATED
RESOLVED
CLOSED
CANCELLED
ARCHIVED
```

---

## 7.2 RaidAction — `project_raid_action`

Fields:

```text
id UUID PK
raid_item_id UUID NOT NULL
project_id UUID NOT NULL
title VARCHAR(255) NOT NULL
description TEXT NULL
owner_user_id UUID NULL
linked_task_id UUID NULL
status VARCHAR(50) NOT NULL
due_date DATE NULL
completed_at TIMESTAMP NULL
completed_by UUID NULL
completion_note TEXT NULL
created_at / created_by
updated_at / updated_by
archived_at / archived_by
version INT
```

Status:

```text
OPEN
IN_PROGRESS
DONE
CANCELLED
OVERDUE
ARCHIVED
```

---

## 7.3 RaidLink — `project_raid_link`

Fields:

```text
id UUID PK
raid_item_id UUID NOT NULL
project_id UUID NOT NULL
target_type VARCHAR(100) NOT NULL
target_id UUID NOT NULL
link_type VARCHAR(50) NOT NULL
created_at / created_by
archived_at / archived_by
version INT
```

Target types:

```text
PROJECT_PHASE
WBS_NODE
TASK
TASK_DEPENDENCY
SCHEDULE_RUN
SCHEDULING_ISSUE
ESTIMATION_RUN
FINANCE_SCENARIO
QUOTE_VERSION
BASELINE
CHANGE_REQUEST
SCOPE_ITEM
DELIVERABLE
ACCEPTANCE_CRITERIA
AI_SUGGESTION
```

Link types:

```text
AFFECTS
CAUSED_BY
MITIGATES
BLOCKS
RELATED_TO
RESULTED_IN
REQUIRES_DECISION
```

---

## 7.4 DecisionRecord — `project_decision_record`

Fields:

```text
id UUID PK
project_id UUID NOT NULL
workspace_id UUID NOT NULL
code VARCHAR(100) NULL
title VARCHAR(255) NOT NULL
description TEXT NULL
category VARCHAR(100) NOT NULL
status VARCHAR(50) NOT NULL
decision_outcome TEXT NULL
rationale TEXT NULL
decision_date DATE NULL
decided_at TIMESTAMP NULL
decided_by UUID NULL
owner_user_id UUID NULL
supersedes_decision_id UUID NULL
superseded_by_decision_id UUID NULL
source_raid_item_id UUID NULL
source_change_request_id UUID NULL
source_ai_suggestion_id UUID NULL
created_at / created_by
updated_at / updated_by
archived_at / archived_by
trace_id VARCHAR(100) NULL
version INT
```

Status:

```text
PROPOSED
UNDER_REVIEW
DECIDED
REJECTED
SUPERSEDED
ARCHIVED
```

---

## 7.5 DecisionOption — `project_decision_option`

Fields:

```text
id UUID PK
decision_id UUID NOT NULL
project_id UUID NOT NULL
title VARCHAR(255) NOT NULL
description TEXT NULL
pros TEXT NULL
cons TEXT NULL
impact_summary TEXT NULL
selected BOOLEAN NOT NULL DEFAULT false
sort_order INT NULL
created_at / created_by
updated_at / updated_by
archived_at / archived_by
version INT
```

---

## 7.6 DecisionImpact — `project_decision_impact`

Fields:

```text
id UUID PK
decision_id UUID NOT NULL UNIQUE
project_id UUID NOT NULL
currency_code VARCHAR(10) NULL
scope_impact TEXT NULL
schedule_impact_days INT NULL
estimate_hours_impact DECIMAL(12,2) NULL
cost_impact DECIMAL(18,4) NULL
revenue_impact DECIMAL(18,4) NULL
margin_impact DECIMAL(18,4) NULL
risk_impact VARCHAR(50) NULL
deliverable_impact TEXT NULL
acceptance_impact TEXT NULL
impact_summary_json JSONB NULL
created_at / created_by
updated_at / updated_by
version INT
```

---

## 7.7 DecisionLink — `project_decision_link`

Fields:

```text
id UUID PK
decision_id UUID NOT NULL
project_id UUID NOT NULL
target_type VARCHAR(100) NOT NULL
target_id UUID NOT NULL
link_type VARCHAR(50) NOT NULL
created_at / created_by
archived_at / archived_by
version INT
```

Link types:

```text
AFFECTS
DECIDES_FOR
CAUSED_BY
RELATED_TO
SUPERSEDES
REQUIRES_CHANGE
```

---

# 8. API TO-BE list

All APIs use `/api`.

---

## 8.1 RAID item APIs

```text
POST  /api/projects/{projectId}/raid-items
GET   /api/projects/{projectId}/raid-items
GET   /api/projects/{projectId}/raid-items/{raidItemId}
PUT   /api/projects/{projectId}/raid-items/{raidItemId}
PATCH /api/projects/{projectId}/raid-items/{raidItemId}/status
POST  /api/projects/{projectId}/raid-items/{raidItemId}/resolve
POST  /api/projects/{projectId}/raid-items/{raidItemId}/close
POST  /api/projects/{projectId}/raid-items/{raidItemId}/reopen
POST  /api/projects/{projectId}/raid-items/{raidItemId}/escalate
PATCH /api/projects/{projectId}/raid-items/{raidItemId}/archive
```

Filters:

```text
type
status
severity
ownerUserId
dueBefore
overdue
linkedTargetType
linkedTargetId
```

---

## 8.2 Risk conversion APIs

```text
POST /api/projects/{projectId}/raid-items/{raidItemId}/convert-risk-to-issue
POST /api/projects/{projectId}/raid-items/{raidItemId}/create-change-request-draft
```

Rules:

```text
Risk must be type RISK.
ChangeRequest draft is not submitted/approved automatically.
```

---

## 8.3 RAID action APIs

```text
POST  /api/projects/{projectId}/raid-items/{raidItemId}/actions
GET   /api/projects/{projectId}/raid-items/{raidItemId}/actions
GET   /api/projects/{projectId}/raid-actions/{raidActionId}
PUT   /api/projects/{projectId}/raid-actions/{raidActionId}
POST  /api/projects/{projectId}/raid-actions/{raidActionId}/complete
PATCH /api/projects/{projectId}/raid-actions/{raidActionId}/archive
POST  /api/projects/{projectId}/raid-actions/{raidActionId}/create-linked-task
```

---

## 8.4 RAID link APIs

```text
POST   /api/projects/{projectId}/raid-items/{raidItemId}/links
GET    /api/projects/{projectId}/raid-items/{raidItemId}/links
DELETE /api/projects/{projectId}/raid-items/{raidItemId}/links/{linkId}
```

---

## 8.5 Decision APIs

```text
POST  /api/projects/{projectId}/decisions
GET   /api/projects/{projectId}/decisions
GET   /api/projects/{projectId}/decisions/{decisionId}
PUT   /api/projects/{projectId}/decisions/{decisionId}
POST  /api/projects/{projectId}/decisions/{decisionId}/decide
POST  /api/projects/{projectId}/decisions/{decisionId}/reject
POST  /api/projects/{projectId}/decisions/{decisionId}/supersede
PATCH /api/projects/{projectId}/decisions/{decisionId}/archive
```

Decide request:

```json
{
  "decisionOutcome": "Use Stripe for card payments in MVP",
  "rationale": "Fastest integration and client already has account",
  "selectedOptionId": "uuid"
}
```

---

## 8.6 Decision option APIs

```text
POST   /api/projects/{projectId}/decisions/{decisionId}/options
GET    /api/projects/{projectId}/decisions/{decisionId}/options
PUT    /api/projects/{projectId}/decisions/{decisionId}/options/{optionId}
DELETE /api/projects/{projectId}/decisions/{decisionId}/options/{optionId}
```

---

## 8.7 Decision impact/link APIs

```text
GET /api/projects/{projectId}/decisions/{decisionId}/impact
PUT /api/projects/{projectId}/decisions/{decisionId}/impact

POST   /api/projects/{projectId}/decisions/{decisionId}/links
GET    /api/projects/{projectId}/decisions/{decisionId}/links
DELETE /api/projects/{projectId}/decisions/{decisionId}/links/{linkId}
```

---

## 8.8 RAID / Decision report APIs

```text
GET /api/projects/{projectId}/reports/raid-summary
GET /api/projects/{projectId}/reports/risk-register
GET /api/projects/{projectId}/reports/issue-log
GET /api/projects/{projectId}/reports/assumption-log
GET /api/projects/{projectId}/reports/dependency-log
GET /api/projects/{projectId}/reports/raid-actions
GET /api/projects/{projectId}/reports/decision-log
```

---

# 9. Authorization requirements

Required authorities:

```text
PROJECT_RAID_VIEW
PROJECT_RAID_CREATE
PROJECT_RAID_UPDATE
PROJECT_RAID_RESOLVE
PROJECT_RAID_CLOSE
PROJECT_RAID_REOPEN
PROJECT_RAID_ESCALATE
PROJECT_RAID_ARCHIVE
PROJECT_RAID_LINK_MANAGE
PROJECT_RAID_ACTION_VIEW
PROJECT_RAID_ACTION_CREATE
PROJECT_RAID_ACTION_UPDATE
PROJECT_RAID_ACTION_COMPLETE
PROJECT_RAID_ACTION_ARCHIVE
PROJECT_RAID_CREATE_CHANGE_REQUEST

PROJECT_DECISION_VIEW
PROJECT_DECISION_CREATE
PROJECT_DECISION_UPDATE
PROJECT_DECISION_DECIDE
PROJECT_DECISION_REJECT
PROJECT_DECISION_SUPERSEDE
PROJECT_DECISION_ARCHIVE
PROJECT_DECISION_OPTION_MANAGE
PROJECT_DECISION_IMPACT_VIEW
PROJECT_DECISION_IMPACT_UPDATE
PROJECT_DECISION_LINK_MANAGE

PROJECT_RAID_REPORT_VIEW
PROJECT_DECISION_REPORT_VIEW
PROJECT_RAID_FINANCE_IMPACT_VIEW
PROJECT_RAID_QUOTE_IMPACT_VIEW
```

Rules:

```text
1. Project access required.
2. Finance impact fields require finance permission.
3. Quote impact fields require quote permission.
4. Creating linked ChangeRequest requires ChangeRequest permission.
5. Creating linked task requires task create permission.
6. External client escalation is internal record only unless client portal exists.
```

---

# 10. Lifecycle rules

## 10.1 Risk lifecycle

```text
OPEN → MONITORING → MITIGATING → RESOLVED → CLOSED
OPEN → ACCEPTED
OPEN/MONITORING/MITIGATING → ESCALATED
OPEN/MONITORING/MITIGATING → CONVERTED_TO_ISSUE
```

Rules:

```text
1. Accepted risk requires reason.
2. Converted risk creates linked issue.
3. Closed risk requires outcome.
4. High/critical risk can trigger notification.
```

## 10.2 Issue lifecycle

```text
OPEN → IN_PROGRESS → RESOLVED → CLOSED
OPEN/IN_PROGRESS → ESCALATED
RESOLVED/CLOSED → REOPENED
```

Rules:

```text
1. Resolution requires note.
2. Critical issue can trigger ChangeRequest.
3. Reopen requires reason.
```

## 10.3 Assumption lifecycle

```text
UNVALIDATED → VALIDATING → VALIDATED
UNVALIDATED/VALIDATING → INVALIDATED
VALIDATED → SUPERSEDED
```

Rules:

```text
1. Invalidated assumption can create risk/issue/change request.
2. Validation owner/due date recommended.
```

## 10.4 Dependency lifecycle

```text
OPEN → WAITING → RECEIVED → RESOLVED
OPEN/WAITING → BLOCKED
BLOCKED → RESOLVED or CONVERTED_TO_ISSUE
```

Rules:

```text
1. Overdue dependency triggers notification.
2. Blocked dependency can create issue.
```

## 10.5 Decision lifecycle

```text
PROPOSED → UNDER_REVIEW → DECIDED
PROPOSED/UNDER_REVIEW → REJECTED
DECIDED → SUPERSEDED
```

Rules:

```text
1. Decided decision requires outcome and rationale.
2. Supersede requires replacement decision.
3. Decisions immutable after decided except supersede/archive.
```

---

# 11. Integration with schedule/capacity

Phase 25 should be able to create RAID items from:

```text
schedule run failed
task at risk
due-date capacity gap
over-allocation
blocked task
dependency cycle
```

Rules:

```text
1. Auto-created RAID from system issue must be idempotent.
2. Same schedule issue should not create duplicates.
3. User can dismiss/link/resolve.
4. Do not expose private capacity details.
```

---

# 12. Integration with deliverables/acceptance

RAID items can block:

```text
deliverable review
acceptance criteria satisfaction
formal acceptance
```

Rules:

```text
1. Deliverable can link to open risks/issues/dependencies.
2. Critical open issue may block acceptance if policy enabled.
3. Acceptance report includes open blocking RAID items.
```

---

# 13. Integration with baseline/change control

After baseline:

```text
1. RAID item can be created without ChangeRequest if it only records governance.
2. RAID item that changes scope/schedule/finance/quote must create ChangeRequest.
3. Decision requiring scope/schedule/finance change must link/create ChangeRequest.
4. ChangeRequest approval/apply can resolve or update linked RAID items.
```

---

# 14. Notification integration

Events should drive notifications for:

```text
critical risk created
risk escalated
issue created
critical issue escalated
dependency overdue
raid action overdue
decision required
decision decided
risk converted to issue
change request created from RAID item
```

Recipient examples:

```text
project manager
item owner
project watchers
change watchers
deliverable owner
finance watcher if finance impact
```

No external client notifications unless external portal exists.

---

# 15. Reporting integration

Extend Phase 22 reports with:

```text
RAID summary
risk register
issue log
assumption log
dependency log
decision log
overdue actions
risk heatmap
RAID by deliverable
RAID by phase/WBS
RAID linked to ChangeRequests
```

Health score impact:

```text
critical open issue → RED
high unmitigated risk → YELLOW/RED
overdue dependency → YELLOW/RED
pending critical decision → YELLOW
```

---

# 16. AI integration

If Phase 21 tool registry exists, seed AI tools/prompts:

```text
suggestProjectRisks
suggestIssueRootCause
suggestRiskMitigations
draftDecisionRecord
draftChangeRequestFromRisk
summarizeRaidStatus
```

Rules:

```text
1. AI suggestions are proposal only.
2. Human must approve create/update/apply.
3. Finance/quote impact masked by permission.
4. AI cannot escalate or close item automatically.
```

---

# 17. Event Registry integration

Recommended source system:

```text
SCOPERY_PROJECT_RAID
```

Required events:

```text
RAID_ITEM_CREATED
RAID_ITEM_UPDATED
RAID_ITEM_STATUS_CHANGED
RAID_ITEM_RESOLVED
RAID_ITEM_CLOSED
RAID_ITEM_REOPENED
RAID_ITEM_ESCALATED
RAID_RISK_CONVERTED_TO_ISSUE
RAID_ITEM_ARCHIVED

RAID_ACTION_CREATED
RAID_ACTION_UPDATED
RAID_ACTION_COMPLETED
RAID_ACTION_OVERDUE
RAID_ACTION_ARCHIVED

RAID_LINK_CREATED
RAID_LINK_REMOVED

DECISION_CREATED
DECISION_UPDATED
DECISION_UNDER_REVIEW
DECISION_DECIDED
DECISION_REJECTED
DECISION_SUPERSEDED
DECISION_ARCHIVED
DECISION_OPTION_CREATED
DECISION_OPTION_UPDATED
DECISION_OPTION_REMOVED
DECISION_IMPACT_UPDATED

RAID_CHANGE_REQUEST_DRAFT_CREATED
```

Standard variables:

```text
actor.userId
organization.id
workspace.id
project.id
raidItem.id
raidItem.type
raidItem.severity
raidItem.status
raidAction.id
decision.id
decision.status
target.type
target.id
occurredAt
traceId
```

---

# 18. Audit / activity / outbox

Audit-sensitive actions:

```text
critical risk created
risk accepted
risk converted to issue
critical issue resolved/closed
RAID item escalated
decision decided
decision superseded
finance/quote impact changed
ChangeRequest draft created from RAID
```

Activity actions:

```text
RAID_ITEM_CREATED
RAID_ITEM_ESCALATED
RAID_ITEM_RESOLVED
RAID_RISK_CONVERTED_TO_ISSUE
DECISION_DECIDED
DECISION_SUPERSEDED
```

Outbox required for major events.

Idempotency recommended for:

```text
POST /raid-items
POST /raid-items/{id}/convert-risk-to-issue
POST /raid-items/{id}/create-change-request-draft
POST /decisions
POST /decisions/{id}/decide
POST /raid-actions/{id}/create-linked-task
```

---

# 19. Business rules master

## 19.1 RAID item rules

```text
RAID-001 Project must exist.
RAID-002 Project must not be archived.
RAID-003 Type required.
RAID-004 Title required.
RAID-005 Linked targets must belong to same project.
RAID-006 Finance/quote impact requires permission.
RAID-007 Closing requires outcome/resolution note.
RAID-008 Reopening requires reason.
RAID-009 Escalation requires reason.
RAID-010 Governance-only RAID creation does not require ChangeRequest after baseline.
RAID-011 RAID-driven project mutation requires ChangeRequest after baseline.
```

## 19.2 Risk rules

```text
RISK-001 Probability and impact required for risk scoring.
RISK-002 Risk score formula version stored.
RISK-003 Accepted risk requires reason.
RISK-004 Converted risk creates linked issue.
RISK-005 Critical risk triggers event.
```

## 19.3 Issue rules

```text
ISSUE-001 Issue severity required.
ISSUE-002 Resolution requires note.
ISSUE-003 Reopen requires reason.
ISSUE-004 Critical issue can block acceptance if policy enabled.
```

## 19.4 Dependency rules

```text
DEP-001 Dependency owner/due date recommended.
DEP-002 Overdue dependency triggers event.
DEP-003 Blocked dependency can convert to issue.
DEP-004 DependencyLog does not replace TaskDependency.
```

## 19.5 Decision rules

```text
DEC-001 Decision title required.
DEC-002 Decided status requires outcome and rationale.
DEC-003 Selected option must belong to decision.
DEC-004 Decided decision immutable except supersede/archive.
DEC-005 Supersede requires replacement decision.
DEC-006 Decision does not implement workflow approval.
```

---

# 20. Error catalog

```text
RAID_ITEM_NOT_FOUND
RAID_ITEM_INVALID_TYPE
RAID_ITEM_INVALID_STATUS
RAID_ITEM_PROJECT_ARCHIVED
RAID_ITEM_TARGET_MISMATCH
RAID_ITEM_CLOSE_NOTE_REQUIRED
RAID_ITEM_REOPEN_REASON_REQUIRED
RAID_ITEM_ESCALATION_REASON_REQUIRED
RAID_RISK_PROBABILITY_REQUIRED
RAID_RISK_IMPACT_REQUIRED
RAID_RISK_ACCEPT_REASON_REQUIRED
RAID_RISK_ALREADY_CONVERTED
RAID_ISSUE_RESOLUTION_NOTE_REQUIRED
RAID_DEPENDENCY_NOT_FOUND
RAID_LINK_NOT_FOUND
RAID_LINK_TARGET_MISMATCH
RAID_LINK_DUPLICATE
RAID_ACTION_NOT_FOUND
RAID_ACTION_INVALID_STATUS
RAID_ACTION_TASK_MISMATCH
RAID_ACTION_COMPLETE_NOTE_REQUIRED
RAID_CHANGE_REQUEST_PERMISSION_REQUIRED
RAID_ACCESS_DENIED

DECISION_NOT_FOUND
DECISION_INVALID_STATUS
DECISION_OUTCOME_REQUIRED
DECISION_RATIONALE_REQUIRED
DECISION_OPTION_NOT_FOUND
DECISION_OPTION_MISMATCH
DECISION_SELECTED_OPTION_INVALID
DECISION_ALREADY_DECIDED
DECISION_SUPERSEDE_REPLACEMENT_REQUIRED
DECISION_IMPACT_NOT_FOUND
DECISION_LINK_NOT_FOUND
DECISION_ACCESS_DENIED
```

---

# 21. Required tests

## 21.1 RAID item tests

```text
createRisk_valid_success
createIssue_valid_success
createAssumption_valid_success
createDependency_valid_success
createRaidItem_archivedProject_rejected
updateRaidItem_valid_success
closeRaidItem_requiresResolutionNote
reopenRaidItem_requiresReason
escalateRaidItem_requiresReason
linkRaidItemToTask_valid_success
linkRaidItemToOtherProjectTask_rejected
financeImpactWithoutPermission_maskedOrForbidden
```

## 21.2 Risk tests

```text
riskScoring_lowHigh_success
riskScoring_formulaVersionStored
acceptRisk_requiresReason
convertRiskToIssue_success
convertRiskToIssue_createsLink
convertRiskToIssue_idempotency_noDuplicateIssue
criticalRisk_emitsEvent
```

## 21.3 Issue tests

```text
resolveIssue_requiresNote
resolveIssue_success
reopenIssue_requiresReason
criticalIssue_canBlockAcceptance_ifPolicyEnabled
issueCreateChangeRequestDraft_success
```

## 21.4 Dependency tests

```text
createDependencyLog_valid_success
overdueDependency_emitsEvent
blockedDependency_convertToIssue_success
dependencyLogDoesNotCreateTaskDependency
```

## 21.5 RAID action tests

```text
createRaidAction_valid_success
completeRaidAction_valid_success
createLinkedTask_fromRaidAction_success
createLinkedTask_withoutTaskPermission_forbidden
overdueRaidAction_emitsEvent
```

## 21.6 Decision tests

```text
createDecision_valid_success
addDecisionOption_valid_success
decideDecision_requiresOutcome
decideDecision_requiresRationale
decideDecision_selectedOptionMustBelong
decidedDecision_immutable
supersedeDecision_requiresReplacement
decisionImpact_financeMaskedWithoutPermission
```

## 21.7 Integration tests

```text
scheduleIssue_canCreateRaidRisk_idempotent
deliverableAcceptanceBlocksOnCriticalIssue_ifPolicyEnabled
raidChangeRequestDraft_created_success
changeRequestApply_updatesLinkedRaidItem_ifConfigured
aiRaidSuggestion_isProposalOnly
```

## 21.8 Authorization tests

```text
viewRaid_withoutPermission_forbidden
createRaid_withoutPermission_forbidden
escalateRaid_withoutPermission_forbidden
decideDecision_withoutPermission_forbidden
viewDecisionImpactFinance_withoutPermission_maskedOrForbidden
crossWorkspaceRaid_forbidden
```

## 21.9 Event/audit tests

```text
raidEventSeeder_firstRun_createsDefinitions
raidEventSeeder_secondRun_noDuplicates
raidItemEscalated_eventEmitted
decisionDecided_eventEmitted
riskAccepted_auditCreated
criticalIssueResolved_auditCreated
```

---

# 22. Manual verification checklist

Completion file must include:

```text
1. Create project RAID risk.
2. Verify risk score calculation.
3. Add mitigation action.
4. Link risk to task/deliverable.
5. Escalate risk and confirm event/notification.
6. Convert risk to issue.
7. Resolve issue with note.
8. Create dependency log and mark overdue/blocked.
9. Create decision with options.
10. Decide with rationale and selected option.
11. Supersede decision.
12. Create ChangeRequest draft from high-impact RAID.
13. Confirm finance/quote impacts masked without permission.
14. Confirm reports show RAID/Decision KPIs.
15. Confirm no workflow/contract/billing/client portal is falsely created.
```

---

# 23. Acceptance criteria

Phase 25 is accepted only if:

```text
1. Current RAID/decision capability is classified against TO-BE.
2. RaidItem implemented/tested.
3. Risk scoring implemented/tested.
4. Assumption tracking implemented/tested.
5. Issue lifecycle implemented/tested.
6. Dependency log implemented/tested.
7. RaidAction implemented/tested.
8. RaidLink implemented/tested.
9. DecisionRecord implemented/tested.
10. DecisionOption and DecisionImpact implemented/tested.
11. ChangeRequest integration implemented/tested.
12. Schedule/deliverable/report/notification integrations implemented/tested or explicitly deferred.
13. IAM permissions implemented/tested.
14. Event seeders idempotent.
15. Activity/audit/outbox follows Phase 04.
16. No workflow/contract/billing/client portal is falsely claimed.
17. `mvn compile` passes.
18. `mvn test` passes.
19. Completion file exists.
```

Do not mark complete if:

```text
critical RAID actions lack audit
risk score formula missing
risk can convert to duplicate issues repeatedly
decision can be decided without rationale
decided decision can be silently edited
cross-project links are allowed
finance/quote impacts leak without permission
tests fail
```

---

# 24. Required phase completion file

Agent must create:

```text
docs/phase-complete/PHASE_25_RAID_DECISION_MANAGEMENT_TO_BE_COMPLETE.md
```

Required sections:

```text
# Phase 25 — RAID / Decision Management Complete

## 1. Summary
## 2. Source Inputs Reviewed
## 3. Current vs TO-BE Classification Matrix
## 4. Implemented in Current BE
## 5. Implemented / Hardened in This Phase
## 6. Seed-only Items Added
## 7. Deferred Items and Target Phase
## 8. RAID Boundary Decision
## 9. Entity Mapping
## 10. API Changes
## 11. Risk Scoring Strategy
## 12. Assumption Strategy
## 13. Issue Lifecycle Strategy
## 14. Dependency Log Strategy
## 15. RAID Action Strategy
## 16. Decision Lifecycle Strategy
## 17. Decision Option / Impact Strategy
## 18. Project Artifact Link Strategy
## 19. ChangeRequest Integration
## 20. Deliverable / Schedule Integration
## 21. Reporting Strategy
## 22. Notification / Event Strategy
## 23. Authorization Matrix
## 24. Activity / Audit / Outbox Notes
## 25. Idempotency Strategy
## 26. Tests Added
## 27. Commands Run
## 28. Test Results
## 29. Manual Verification
## 30. Assumptions
## 31. Deviations From Prompt
## 32. Known Risks
## 33. Future Phases That Must Return to RAID / Decisions
```

---

# 25. Future phases that must return

```text
Phase 26 — Quality / Test / Release:
- Link issues/risks to defects, test runs, release blockers.

Phase 27 — Document Hub:
- Attach formal decision documents, risk evidence, meeting minutes.

Phase 31 — Meetings / Collaboration:
- Create decisions/actions from meetings.
- Link meeting agenda/minutes to RAID and decisions.

Phase 34 — Workflow / Approval:
- Multi-step decision approval/escalation.
- Risk acceptance approval workflow.

Phase 35 — Advanced Notifications:
- Escalation reminders, risk review digest, overdue action digest.

Phase 36 — Contract / Billing / Revenue:
- Contractual risks, commercial decisions, change order billing impact.

Phase 37 — Time / Expense:
- Actual effort/cost variance linked to RAID.

Phase 38 — Audit / Compliance / Privacy:
- Compliance risk register, decision retention, legal hold.

Phase 41 — Knowledge Graph / Semantic Index:
- Semantic linkage between decisions, risks, tasks, deliverables, documents.
```

---

# 26. Agent anti-bịa rules

The agent must not:

```text
1. Treat DependencyLog as TaskDependency.
2. Treat DecisionRecord as workflow approval.
3. Claim external client escalation sends messages.
4. Claim ChangeRequest is applied automatically from RAID.
5. Claim risk impact is actual cost.
6. Claim issue resolution changes billing/revenue.
7. Expose finance/quote impact without permission.
8. Allow decided decisions to be silently edited.
9. Allow cross-project RAID/decision links.
10. Hide deferred meeting/workflow/contract/client portal gaps.
```

---

# 27. Prompt to give coding agent

```text
You are implementing Phase 25 — TO-BE RAID, Decision Management, Project Governance Log, Risk Response & Dependency Control.

This is not an as-is documentation task.

Before coding, read:
- CLAUDE.md / CLAUDE.ms
- Coding_convention.md
- Phase 00–24 docs and completion files
- Current backend code, migrations, tests

Your task:
1. Compare current RAID/decision capability against this Phase 25 TO-BE spec.
2. Classify each capability with required labels.
3. Implement RaidItem, RaidAction, RaidLink.
4. Implement risk scoring, assumption validation, issue lifecycle, dependency log.
5. Implement DecisionRecord, DecisionOption, DecisionImpact, DecisionLink.
6. Integrate with ChangeRequest, Schedule, Deliverable, Notification, Reporting, and AI proposal layer where available.
7. Add IAM permissions, events, audit/outbox, idempotency, and tests.
8. Run mvn compile and mvn test.
9. Create docs/phase-complete/PHASE_25_RAID_DECISION_MANAGEMENT_TO_BE_COMPLETE.md.

Do not implement or claim full meeting management, workflow automation, contract governance, billing/revenue recognition, external client portal, or autonomous AI actions in this phase.
```

---

# 28. Quick tracking matrix

| Capability | Current backend | Phase 25 action | Later phase |
|---|---|---|---|
| RaidItem | Missing/unknown | Must implement | — |
| Risk scoring | Missing/unknown | Must implement | — |
| Assumption tracking | Missing/unknown | Must implement | — |
| Issue lifecycle | Missing/unknown | Must implement | Quality linkage Phase 26 |
| Dependency log | Missing/unknown | Must implement | — |
| RaidAction | Missing/unknown | Must implement | Meeting actions Phase 31 |
| RaidLink | Missing/unknown | Must implement | Semantic graph Phase 41 |
| DecisionRecord | Missing/unknown | Must implement | Meeting linkage Phase 31 |
| DecisionOption | Missing/unknown | Must implement | — |
| DecisionImpact | Missing/unknown | Must implement | — |
| ChangeRequest integration | Missing/partial | Must implement | — |
| Notification integration | Missing/partial | Must implement | Phase 35 advanced |
| Reporting | Missing/partial | Must implement | Phase 22/41 advanced |
| AI suggestions | Missing/partial | Seed/must implement proposal | Phase 21/41 advanced |
| Workflow approval | Missing | Defer | Phase 34 |
| Meeting management | Missing | Defer | Phase 31 |
| Contract/billing impact | Missing | Defer | Phase 36 |
| Client portal escalation | Missing | Defer | Phase 30 |

---

# 29. Final principle

Phase 25 is not complete when "a risk row can be stored."

Phase 25 is complete when Scopery can govern uncertainty and decisions:

```text
risk / assumption / issue / dependency
+ owner
+ impact
+ action
+ escalation
+ decision
+ links to project artifacts
+ reporting
+ audit
= controlled project governance
```

Risk is potential.

Issue is actual.

DependencyLog is governance.

TaskDependency is scheduling.

Decision is recorded rationale, not workflow approval.

RAID must create visibility before problems become silent project failure.
