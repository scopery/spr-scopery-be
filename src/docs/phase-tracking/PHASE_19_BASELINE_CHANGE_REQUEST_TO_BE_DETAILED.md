# PHASE 19 — TO-BE Project Baseline, Change Request, Change Order & Controlled Scope/Schedule/Finance Governance

> Project: Scopery Backend  
> Phase: 19  
> Document type: TO-BE implementation-grade specification  
> Status: Planning / Gap-driven implementation spec  
> Depends on: Phase 00 — Master Backend Roadmap, Phase 01 — API Path / Security Baseline, Phase 02 — IAM Core, Phase 03 — Workspace Core, Phase 04 — Platform Audit / Outbox / Idempotency, Phase 05 — Event Registry, Phase 06 — Notification Engine, Phase 07 — AI Agent Platform, Phase 08 — Knowledge / Document Type Catalog, Phase 09 — Project Core / Phase / WBS / Task, Phase 10 — Project Authorization, Phase 11 — Project Template / Phase Catalog, Phase 12 — Resource Calendar / Capacity, Phase 13 — Task Scheduling Engine, Phase 14 — WBS-driven Gantt, Phase 15 — Rate Card / Cost Policy, Phase 16 — Estimation Roll-up, Phase 17 — Project Budget / Margin, Phase 18 — Quote / Commercial Proposal  
> API base: `/api`  
> Primary module: `modules/projectbaseline` or `modules/project/baseline` depending on repository architecture  
> Related modules: `project`, `scheduling`, `gantt`, `estimation`, `projectfinance`, `quote`, `workspace`, `iam`, `eventregistry`, `notification`, future `contract`, `billing`, `reporting`, `aiagent`, `workflow`  
> Important rule: This file is **not an as-is document**. It defines the TO-BE baseline/change governance foundation. Phase 19 does not implement contract execution, invoice/billing, revenue recognition, actual cost, timesheet, or full workflow engine.

---

# 0. Purpose of this file

Phase 19 creates Scopery's project control layer.

Previous phases created:

```text
Phase 09:
- Project / Phase / WBS / Task / Dependency

Phase 13:
- ScheduleRun / TaskSchedule / ScheduledDailyWork

Phase 14:
- Gantt projection / milestone foundation

Phase 16:
- EstimationRun / labor estimate snapshots

Phase 17:
- ProjectFinanceScenario / margin / PBT scenario

Phase 18:
- Quote / QuoteVersion / target margin solver
```

Phase 19 answers:

```text
What was the approved project scope?
What was the approved schedule?
What was the approved estimate?
What was the approved finance scenario?
What was the approved quote?
What changed after approval?
Who requested the change?
What is the impact on scope, schedule, cost, revenue, margin, and quote?
Who approved or rejected it?
When approved, how is the change applied safely?
```

Phase 19 must prevent silent changes to approved project commitments.

---

# 1. Source inputs

Before coding Phase 19, the agent must read:

```text
1. Current backend codebase
2. Phase 09 Project Core TO-BE spec and implementation
3. Phase 10 Project Authorization TO-BE spec and implementation
4. Phase 13 Task Scheduling Engine TO-BE spec and implementation
5. Phase 14 WBS-driven Gantt TO-BE spec and implementation
6. Phase 16 Estimation Roll-up TO-BE spec and implementation
7. Phase 17 Project Budget / Margin TO-BE spec and implementation
8. Phase 18 Quote / Commercial Proposal TO-BE spec and implementation
9. Phase 04 Platform Audit / Outbox / Idempotency spec
10. Phase 05 Event Registry spec
11. Phase 06 Notification spec
12. Phase 02 IAM TO-BE spec
13. Current BE feature/entity/business-rule inventory
14. Dynamic Work OS feature catalog
15. Existing baseline/change request/scope management code if any
```

The agent must not implement Phase 19 from assumptions only.

---

# 2. Current expected backend state

After Phase 18, the backend should have:

```text
Project
ProjectPhase
WbsNode
Task
TaskDependency
ProjectMilestone if implemented
ScheduleRun
TaskSchedule
EstimationRun
ProjectFinanceScenario
QuoteVersion
Activity/Audit/Event infrastructure
IAM permissions
```

Likely missing:

```text
ProjectBaseline
BaselineScopeSnapshot
BaselineScheduleSnapshot
BaselineEstimateSnapshot
BaselineFinanceSnapshot
BaselineQuoteSnapshot
ChangeRequest
ChangeImpact
ChangeRequestItem
ChangeApprovalAction
ChangeOrder
Baseline comparison
Controlled apply of approved changes
Post-baseline edit guard
```

Phase 19 implements baseline/change control, not contract/billing.

---

# 3. Phase 19 target statement

Phase 19 must deliver a future-ready project governance foundation:

```text
1. Create ProjectBaseline from approved/current project artifacts.
2. Snapshot scope, WBS, tasks, dependencies, milestones, schedule, estimate, finance, and quote references.
3. Preserve baseline immutable after approval.
4. Detect or require ChangeRequest for controlled changes after baseline.
5. Create ChangeRequest with proposed change items.
6. Capture change impact on scope, schedule, estimate, finance, margin, and quote.
7. Support simple submit/approve/reject/cancel/apply lifecycle.
8. Apply approved changes through existing project actions, not direct table mutation.
9. Create ChangeOrder record for approved client/commercial changes if needed.
10. Protect baseline and change APIs with IAM.
11. Emit baseline/change events and audit sensitive actions.
12. Clearly defer full workflow engine, contract execution, billing, actuals, and legal change order lifecycle.
```

---

# 4. Governance boundary decisions

## 4.1 Baseline is immutable

Once approved:

```text
baseline snapshot must not change
```

If project changes:

```text
create new baseline version or approved change request
```

## 4.2 Baseline is not live project

Baseline is snapshot.

Live project remains:

```text
Project
WBS
Task
ScheduleRun
EstimationRun
FinanceScenario
QuoteVersion
```

## 4.3 Change Request is proposed change, not applied change

A change request can propose:

```text
add/remove/update WBS
add/remove/update task
change estimate
change due date
change dependency
change schedule
change milestone
change custom cost
change finance scenario
change quote version
```

But proposal does not mutate live project until approved and applied.

## 4.4 Change Order is commercial/legal follow-up

A change order can be created after approved ChangeRequest if commercial impact exists.

Phase 19 can create simple ChangeOrder record, but full legal contract/billing is deferred.

---

# 5. Classification labels

Every requirement uses one label:

| Label | Meaning |
|---|---|
| `CURRENTLY_IMPLEMENTED` | Current backend already implements it. Agent must verify and test. |
| `PARTIALLY_IMPLEMENTED` | Current backend implements part of it. |
| `MUST_IMPLEMENT_IN_PHASE_19` | Must be implemented or hardened now. |
| `SEED_ONLY_IN_PHASE_19` | Seed events/permissions/templates/config now; full consumer later. |
| `DEFERRED_TO_PHASE_XX` | Do not implement now; later phase must return. |
| `DEFERRED_TO_POST_23_BACKLOG` | Outside core 00–23 roadmap, part of full Work OS expansion. |
| `NOT_IN_SCOPE_FOR_BACKEND_NOW` | Explicitly not backend scope now. |

---

# 6. Phase 19 scope decision

## 6.1 Must implement now

```text
ProjectBaseline
Baseline version/status lifecycle
Baseline snapshots:
- scope/WBS/task/dependency
- schedule reference/snapshot
- estimation reference/snapshot
- finance reference/snapshot
- quote reference/snapshot
Baseline approval/current marker
ChangeRequest
ChangeRequestItem
ChangeImpact
ChangeApprovalAction
ChangeRequest lifecycle
Approved change apply mechanism
Post-baseline edit guard or warning mode
Simple ChangeOrder foundation
Baseline/change permissions
Baseline/change events
Activity/audit/outbox
Tests
Completion report
```

## 6.2 Optional now

```text
Detailed diff engine
Baseline compare API
Change request from detected live changes
Multi-approver workflow
Client approval marker
Change request document package
Change impact simulator
```

Implement only if product requires.

## 6.3 Must not implement now

```text
Full workflow engine
Legal contract amendment
Invoice/billing
Revenue recognition
Payment collection
Actual cost/timesheet
Procurement/vendor PO
Tax adjustment
AI auto-approval
Client portal approval
```

---

# 7. Baseline snapshot scope

A baseline can snapshot these artifacts:

```text
Project metadata
ProjectPhase list
WBS tree
Task list
TaskDependency list
Milestones
ScheduleRun / TaskSchedule summary
EstimationRun / estimate summary
ProjectFinanceScenario / finance summary
QuoteVersion / quote summary
```

Recommended Phase 19 minimum:

```text
Project
Phases
WBS
Tasks
Dependencies
Latest/current ScheduleRun reference + summary
Current EstimationRun reference + summary
Current ProjectFinanceScenario reference + summary
Approved/current QuoteVersion reference + summary
```

Do not copy every ScheduledDailyWork row unless needed.

Instead:

```text
snapshot summary + reference to immutable completed run
```

If referenced artifact is not immutable, copy enough data to preserve history.

---

# 8. TO-BE capability matrix

---

## 8.1 BSL-001 — ProjectBaseline

| Item | Value |
|---|---|
| Future capability | Immutable approved project baseline snapshot |
| Current state | Missing/unknown |
| Phase 19 target | Implement |
| Classification | `MUST_IMPLEMENT_IN_PHASE_19` |

Rules:

```text
1. Baseline belongs to one project.
2. Project must not be ARCHIVED.
3. Baseline version unique within project.
4. Baseline starts DRAFT.
5. DRAFT can be recalculated/refreshed.
6. APPROVED baseline immutable.
7. Only one current baseline per project.
8. Baseline approval audited.
9. Baseline is snapshot, not live project.
10. Baseline does not create contract/invoice.
```

Status:

```text
DRAFT
READY
APPROVED
CURRENT
ARCHIVED
```

or:

```text
DRAFT
APPROVED
ARCHIVED + currentFlag
```

Completion file must document chosen model.

---

## 8.2 BSL-002 — Baseline scope snapshot

| Item | Value |
|---|---|
| Future capability | Preserve approved scope/WBS/task state |
| Current state | Missing/unknown |
| Phase 19 target | Implement |
| Classification | `MUST_IMPLEMENT_IN_PHASE_19` |

Snapshot must include:

```text
ProjectPhase snapshot
WBS node snapshot
Task snapshot
TaskDependency snapshot
Milestone snapshot if implemented
```

Rules:

```text
1. Snapshot captures IDs and human-readable names/codes.
2. Snapshot captures task estimateHours, dueDate, status, phase, WBS, assignee/inCharge.
3. Snapshot captures dependency edges.
4. Snapshot captures WBS parent/ordering.
5. Snapshot is immutable after baseline approval.
```

---

## 8.3 BSL-003 — Baseline schedule snapshot

| Item | Value |
|---|---|
| Future capability | Preserve approved schedule forecast |
| Current state | ScheduleRun exists from Phase 13 |
| Phase 19 target | Reference + summary snapshot |
| Classification | `MUST_IMPLEMENT_IN_PHASE_19` |

Rules:

```text
1. Baseline may reference current completed ScheduleRun.
2. ScheduleRun must belong to project.
3. ScheduleRun must be COMPLETED.
4. Baseline stores schedule summary snapshot:
   - project start/end forecast
   - scheduled tasks count
   - at-risk tasks count
   - major issue count
5. Baseline does not recalculate schedule.
```

If no schedule run exists:

```text
Baseline can be created with schedule section marked MISSING only if policy allows.
```

Recommended:

```text
Require completed ScheduleRun for schedule baseline.
```

---

## 8.4 BSL-004 — Baseline estimate snapshot

| Item | Value |
|---|---|
| Future capability | Preserve approved labor estimate snapshot |
| Current state | EstimationRun exists from Phase 16 |
| Phase 19 target | Reference + summary snapshot |
| Classification | `MUST_IMPLEMENT_IN_PHASE_19` |

Rules:

```text
1. EstimationRun must belong to project.
2. EstimationRun must be COMPLETED.
3. Baseline stores estimate summary:
   - total hours
   - total labor cost
   - unresolved role/rate count
   - currency
4. Baseline does not recalculate estimate.
```

Recommended:

```text
Block approval if estimation has unresolved rate/role unless override permission exists.
```

---

## 8.5 BSL-005 — Baseline finance snapshot

| Item | Value |
|---|---|
| Future capability | Preserve approved project finance scenario |
| Current state | ProjectFinanceScenario exists from Phase 17 |
| Phase 19 target | Reference + summary snapshot |
| Classification | `MUST_IMPLEMENT_IN_PHASE_19` |

Rules:

```text
1. FinanceScenario must belong to project.
2. FinanceScenario should be APPROVED/CURRENT.
3. Baseline stores finance summary:
   - planned revenue
   - direct cost
   - overhead
   - budget of costs
   - gross margin
   - PBT
   - currency
4. Baseline does not modify finance scenario.
```

---

## 8.6 BSL-006 — Baseline quote snapshot

| Item | Value |
|---|---|
| Future capability | Preserve approved/sent quote version used for project commitment |
| Current state | QuoteVersion exists from Phase 18 |
| Phase 19 target | Reference + summary snapshot |
| Classification | `MUST_IMPLEMENT_IN_PHASE_19` if quote exists; otherwise optional |

Rules:

```text
1. QuoteVersion must belong to project.
2. QuoteVersion should be APPROVED/SENT/ACCEPTED according to product.
3. Baseline stores quote summary:
   - total quoted amount
   - target margin
   - quote status
   - valid until
   - currency
4. Baseline does not create contract.
```

If internal projects have no quote:

```text
Baseline can mark quote section NOT_APPLICABLE.
```

---

## 8.7 BSL-007 — Baseline validation

| Item | Value |
|---|---|
| Future capability | Validate baseline readiness |
| Current state | Missing/unknown |
| Phase 19 target | Implement |
| Classification | `MUST_IMPLEMENT_IN_PHASE_19` |

Before approval:

```text
1. Project has at least one phase.
2. Project has WBS/tasks if required by product.
3. ScheduleRun completed if schedule baseline required.
4. EstimationRun completed.
5. FinanceScenario approved/current if finance baseline required.
6. QuoteVersion approved/sent if quote baseline required.
7. No blocking unresolved estimation issues.
8. No blocking finance validation errors.
9. Baseline snapshot generated.
```

---

## 8.8 CR-001 — ChangeRequest

| Item | Value |
|---|---|
| Future capability | Controlled proposed change after baseline |
| Current state | Missing/unknown |
| Phase 19 target | Implement |
| Classification | `MUST_IMPLEMENT_IN_PHASE_19` |

Rules:

```text
1. ChangeRequest belongs to project.
2. ChangeRequest may reference baseline.
3. Project must not be ARCHIVED.
4. ChangeRequest starts DRAFT.
5. DRAFT can be edited.
6. SUBMITTED immutable except withdraw/reject.
7. APPROVED can be applied.
8. REJECTED cannot be applied.
9. APPLIED cannot be edited.
10. ChangeRequest is proposal until applied.
```

Status:

```text
DRAFT
SUBMITTED
APPROVED
REJECTED
CANCELLED
APPLIED
ARCHIVED
```

Types:

```text
SCOPE_CHANGE
SCHEDULE_CHANGE
COST_CHANGE
REVENUE_CHANGE
QUOTE_CHANGE
RESOURCE_CHANGE
RISK_RESPONSE
OTHER
```

---

## 8.9 CR-002 — ChangeRequestItem

| Item | Value |
|---|---|
| Future capability | Represent proposed atomic changes |
| Current state | Missing/unknown |
| Phase 19 target | Implement |
| Classification | `MUST_IMPLEMENT_IN_PHASE_19` |

Item target types:

```text
PROJECT
PROJECT_PHASE
WBS_NODE
TASK
TASK_DEPENDENCY
MILESTONE
SCHEDULE
ESTIMATE
FINANCE_SCENARIO
QUOTE_VERSION
CUSTOM_COST
VENDOR_COST
OTHER
```

Operation types:

```text
CREATE
UPDATE
DELETE
ARCHIVE
MOVE
RECALCULATE
REPLACE_REFERENCE
```

Rules:

```text
1. Item belongs to ChangeRequest.
2. Item stores targetType and targetId if applicable.
3. Item stores beforeSnapshotJson and afterSnapshotJson.
4. Item stores human-readable summary.
5. DRAFT request required to edit items.
6. Approved items are applied through domain actions.
```

---

## 8.10 CR-003 — ChangeImpact

| Item | Value |
|---|---|
| Future capability | Capture expected impact of change |
| Current state | Missing/unknown |
| Phase 19 target | Implement |
| Classification | `MUST_IMPLEMENT_IN_PHASE_19` |

Impact dimensions:

```text
scopeImpact
scheduleImpactDays
estimateHoursImpact
laborCostImpact
directCostImpact
overheadImpact
revenueImpact
grossMarginImpact
pbtImpact
quoteAmountImpact
riskImpact
```

Rules:

```text
1. Impact can be manually entered or calculated from snapshots.
2. Impact must be present before approval if required by policy.
3. Monetary impact currency required.
4. Impact is forecast, not actual.
5. Impact does not mutate finance/quote until applied.
```

---

## 8.11 CR-004 — Change approval

| Item | Value |
|---|---|
| Future capability | Approve/reject controlled changes |
| Current state | Missing/unknown |
| Phase 19 target | Implement simple approval |
| Classification | `MUST_IMPLEMENT_IN_PHASE_19` simple; full workflow deferred |

Rules:

```text
1. Submit requires DRAFT.
2. Approve requires CHANGE_REQUEST_APPROVE.
3. Reject requires reason.
4. Approval audited.
5. Full multi-step workflow deferred to Phase 34.
6. Segregation of duties optional/deferred unless already supported.
```

---

## 8.12 CR-005 — Apply approved change

| Item | Value |
|---|---|
| Future capability | Safely apply approved change to live project |
| Current state | Missing/unknown |
| Phase 19 target | Implement controlled apply |
| Classification | `MUST_IMPLEMENT_IN_PHASE_19` |

Rules:

```text
1. ChangeRequest must be APPROVED.
2. Apply requires CHANGE_REQUEST_APPLY.
3. Apply must be transactional.
4. Apply must call existing domain actions/services.
5. Apply must not bypass validation/IAM.
6. Apply records appliedAt/appliedBy.
7. Apply creates audit/activity/events.
8. Apply may create new ScheduleRun/EstimationRun/FinanceScenario/QuoteVersion if requested.
9. Apply does not create invoice/contract.
```

If item operation unsupported:

```text
ChangeRequest cannot be applied and must show clear error.
```

---

## 8.13 CO-001 — ChangeOrder foundation

| Item | Value |
|---|---|
| Future capability | Commercial change order created from approved ChangeRequest |
| Current state | Missing/unknown |
| Phase 19 target | Implement simple record if commercial impact exists |
| Classification | `MUST_IMPLEMENT_IN_PHASE_19` foundation; legal/billing deferred |

Rules:

```text
1. ChangeOrder belongs to ChangeRequest.
2. ChangeOrder stores commercial impact summary.
3. ChangeOrder can reference QuoteVersion or future Contract.
4. ChangeOrder status starts DRAFT.
5. ChangeOrder is not invoice.
6. ChangeOrder is not legal contract unless contract module exists.
```

Status:

```text
DRAFT
SUBMITTED
APPROVED
REJECTED
ARCHIVED
```

Full contract amendment:

```text
DEFERRED_TO_PHASE_36_CONTRACT_BILLING_REVENUE
```

---

## 8.14 BSL-008 — Post-baseline edit guard

| Item | Value |
|---|---|
| Future capability | Prevent silent changes after baseline |
| Current state | Missing/unknown |
| Phase 19 target | Implement guard or warning mode |
| Classification | `MUST_IMPLEMENT_IN_PHASE_19` |

Two acceptable modes:

### Strict mode

```text
After current baseline exists:
- direct edits to controlled fields are blocked
- user must create ChangeRequest
```

### Warning mode

```text
Direct edits allowed only with warning/audit and optional auto ChangeRequest
```

Recommended Phase 19:

```text
Strict mode for controlled fields.
```

Controlled fields:

```text
WBS create/update/archive/move
Task create/update/archive
Task estimateHours
Task dueDate
Task phase/WBS assignment
Task dependency create/remove
Milestone date
Finance scenario current/approved change
Quote current/approved change
```

Completion file must document mode.

---

# 9. Entity model TO-BE

If current schema differs, agent must map actual fields and document gaps.

---

## 9.1 ProjectBaseline — `project_baseline`

Required fields:

```text
id UUID PK
project_id UUID NOT NULL
workspace_id UUID NOT NULL
baseline_number INT NOT NULL
name VARCHAR(255) NOT NULL
description TEXT NULL
status VARCHAR(50) NOT NULL
current_flag BOOLEAN NOT NULL DEFAULT false

source_schedule_run_id UUID NULL
source_estimation_run_id UUID NULL
source_finance_scenario_id UUID NULL
source_quote_version_id UUID NULL

snapshot_json JSONB NOT NULL
summary_json JSONB NOT NULL
validation_json JSONB NULL
formula_version VARCHAR(50) NOT NULL

approved_at TIMESTAMP NULL
approved_by UUID NULL
created_at / created_by
updated_at / updated_by
archived_at / archived_by
trace_id VARCHAR(100) NULL
version INT
```

Constraint:

```text
unique project_id + baseline_number
only one current baseline per project
```

Status:

```text
DRAFT
READY
APPROVED
ARCHIVED
```

---

## 9.2 BaselineSnapshotSection — optional table

If storing sections separately:

```text
project_baseline_section
```

Fields:

```text
id UUID PK
baseline_id UUID NOT NULL
section_type VARCHAR(50) NOT NULL
source_entity_id UUID NULL
snapshot_json JSONB NOT NULL
summary_json JSONB NULL
created_at TIMESTAMP NOT NULL
```

Section types:

```text
PROJECT
PHASES
WBS
TASKS
DEPENDENCIES
MILESTONES
SCHEDULE
ESTIMATION
FINANCE
QUOTE
```

Recommended:

```text
Use sections table if snapshot_json becomes too large.
Otherwise store single snapshot_json in Phase 19.
```

---

## 9.3 ChangeRequest — `change_request`

Required fields:

```text
id UUID PK
project_id UUID NOT NULL
workspace_id UUID NOT NULL
baseline_id UUID NULL
code VARCHAR(100) NOT NULL
title VARCHAR(255) NOT NULL
description TEXT NULL
change_type VARCHAR(50) NOT NULL
priority VARCHAR(50) NULL
status VARCHAR(50) NOT NULL
reason TEXT NULL
requested_by UUID NULL
requested_at TIMESTAMP NULL
submitted_at TIMESTAMP NULL
submitted_by UUID NULL
approved_at TIMESTAMP NULL
approved_by UUID NULL
rejected_at TIMESTAMP NULL
rejected_by UUID NULL
rejection_reason TEXT NULL
cancelled_at TIMESTAMP NULL
cancelled_by UUID NULL
applied_at TIMESTAMP NULL
applied_by UUID NULL
created_at / created_by
updated_at / updated_by
archived_at / archived_by
trace_id VARCHAR(100) NULL
version INT
```

Constraint:

```text
unique project_id + code
```

---

## 9.4 ChangeRequestItem — `change_request_item`

Required fields:

```text
id UUID PK
change_request_id UUID NOT NULL
project_id UUID NOT NULL
target_type VARCHAR(50) NOT NULL
target_id UUID NULL
operation VARCHAR(50) NOT NULL
summary VARCHAR(500) NOT NULL
before_snapshot_json JSONB NULL
after_snapshot_json JSONB NULL
apply_payload_json JSONB NULL
status VARCHAR(50) NOT NULL
created_at / created_by
updated_at / updated_by
version INT
```

Status:

```text
DRAFT
READY
APPLIED
FAILED
SKIPPED
```

---

## 9.5 ChangeImpact — `change_impact`

Required fields:

```text
id UUID PK
change_request_id UUID NOT NULL UNIQUE
project_id UUID NOT NULL
currency_code VARCHAR(10) NULL

scope_impact TEXT NULL
schedule_impact_days INT NULL
estimate_hours_impact DECIMAL(12,2) NULL
labor_cost_impact DECIMAL(18,4) NULL
direct_cost_impact DECIMAL(18,4) NULL
overhead_impact DECIMAL(18,4) NULL
revenue_impact DECIMAL(18,4) NULL
gross_margin_impact DECIMAL(18,4) NULL
pbt_impact DECIMAL(18,4) NULL
quote_amount_impact DECIMAL(18,4) NULL

risk_impact VARCHAR(50) NULL
impact_summary_json JSONB NULL
created_at / created_by
updated_at / updated_by
version INT
```

---

## 9.6 ChangeApprovalAction — `change_approval_action`

Required fields:

```text
id UUID PK
change_request_id UUID NOT NULL
action VARCHAR(50) NOT NULL
actor_user_id UUID NOT NULL
reason TEXT NULL
created_at TIMESTAMP NOT NULL
trace_id VARCHAR(100) NULL
```

Actions:

```text
SUBMIT
APPROVE
REJECT
CANCEL
APPLY
REOPEN
```

---

## 9.7 ChangeOrder — `change_order`

Required fields:

```text
id UUID PK
change_request_id UUID NOT NULL
project_id UUID NOT NULL
workspace_id UUID NOT NULL
code VARCHAR(100) NOT NULL
title VARCHAR(255) NOT NULL
description TEXT NULL
status VARCHAR(50) NOT NULL
commercial_impact_json JSONB NULL
source_quote_version_id UUID NULL
future_contract_id UUID NULL
created_at / created_by
updated_at / updated_by
approved_at / approved_by
archived_at / archived_by
version INT
```

Status:

```text
DRAFT
SUBMITTED
APPROVED
REJECTED
ARCHIVED
```

Rule:

```text
ChangeOrder is commercial record only in Phase 19, not invoice/contract.
```

---

# 10. API TO-BE list

All APIs use `/api`.

---

## 10.1 Baseline APIs

```text
POST  /api/projects/{projectId}/baselines
GET   /api/projects/{projectId}/baselines
GET   /api/projects/{projectId}/baselines/{baselineId}
PUT   /api/projects/{projectId}/baselines/{baselineId}
POST  /api/projects/{projectId}/baselines/{baselineId}/refresh-snapshot
POST  /api/projects/{projectId}/baselines/{baselineId}/validate
POST  /api/projects/{projectId}/baselines/{baselineId}/approve
POST  /api/projects/{projectId}/baselines/{baselineId}/mark-current
PATCH /api/projects/{projectId}/baselines/{baselineId}/archive
GET   /api/projects/{projectId}/baseline/current
```

Create request:

```json
{
  "name": "Initial Approved Baseline",
  "description": "Baseline after approved quote",
  "sourceScheduleRunId": "uuid",
  "sourceEstimationRunId": "uuid",
  "sourceFinanceScenarioId": "uuid",
  "sourceQuoteVersionId": "uuid",
  "sections": ["PROJECT", "PHASES", "WBS", "TASKS", "DEPENDENCIES", "SCHEDULE", "ESTIMATION", "FINANCE", "QUOTE"]
}
```

---

## 10.2 Baseline compare APIs

Optional/deferred:

```text
GET /api/projects/{projectId}/baselines/{baselineId}/compare-live
GET /api/projects/{projectId}/baselines/compare?left=...&right=...
```

Recommended:

```text
Implement simple compare-live if useful.
Detailed diff deferred to Phase 22.
```

---

## 10.3 ChangeRequest APIs

```text
POST  /api/projects/{projectId}/change-requests
GET   /api/projects/{projectId}/change-requests
GET   /api/projects/{projectId}/change-requests/{changeRequestId}
PUT   /api/projects/{projectId}/change-requests/{changeRequestId}
POST  /api/projects/{projectId}/change-requests/{changeRequestId}/submit
POST  /api/projects/{projectId}/change-requests/{changeRequestId}/approve
POST  /api/projects/{projectId}/change-requests/{changeRequestId}/reject
POST  /api/projects/{projectId}/change-requests/{changeRequestId}/cancel
POST  /api/projects/{projectId}/change-requests/{changeRequestId}/apply
PATCH /api/projects/{projectId}/change-requests/{changeRequestId}/archive
```

Create request:

```json
{
  "code": "CR-001",
  "title": "Add payment gateway integration",
  "description": "Client requested additional payment provider",
  "changeType": "SCOPE_CHANGE",
  "baselineId": "uuid",
  "reason": "New client requirement"
}
```

---

## 10.4 ChangeRequestItem APIs

```text
POST   /api/projects/{projectId}/change-requests/{changeRequestId}/items
GET    /api/projects/{projectId}/change-requests/{changeRequestId}/items
GET    /api/projects/{projectId}/change-requests/{changeRequestId}/items/{itemId}
PUT    /api/projects/{projectId}/change-requests/{changeRequestId}/items/{itemId}
DELETE /api/projects/{projectId}/change-requests/{changeRequestId}/items/{itemId}
```

Item request:

```json
{
  "targetType": "TASK",
  "targetId": "uuid-or-null",
  "operation": "CREATE",
  "summary": "Add task: Integrate Stripe payment gateway",
  "afterSnapshotJson": {
    "title": "Integrate Stripe payment gateway",
    "estimateHours": 24,
    "phaseId": "uuid",
    "wbsNodeId": "uuid"
  },
  "applyPayloadJson": {
    "action": "CREATE_TASK",
    "payload": {
      "title": "Integrate Stripe payment gateway",
      "estimateHours": 24
    }
  }
}
```

---

## 10.5 ChangeImpact APIs

```text
GET /api/projects/{projectId}/change-requests/{changeRequestId}/impact
PUT /api/projects/{projectId}/change-requests/{changeRequestId}/impact
POST /api/projects/{projectId}/change-requests/{changeRequestId}/impact/calculate
```

Calculate impact can be optional.

Manual impact update allowed in DRAFT only.

---

## 10.6 ChangeOrder APIs

```text
POST  /api/projects/{projectId}/change-requests/{changeRequestId}/change-orders
GET   /api/projects/{projectId}/change-requests/{changeRequestId}/change-orders
GET   /api/projects/{projectId}/change-orders/{changeOrderId}
PUT   /api/projects/{projectId}/change-orders/{changeOrderId}
POST  /api/projects/{projectId}/change-orders/{changeOrderId}/approve
POST  /api/projects/{projectId}/change-orders/{changeOrderId}/reject
PATCH /api/projects/{projectId}/change-orders/{changeOrderId}/archive
```

Phase 19 ChangeOrder is basic record only.

---

# 11. Authorization requirements

Required IAM authorities:

```text
PROJECT_BASELINE_VIEW
PROJECT_BASELINE_CREATE
PROJECT_BASELINE_UPDATE
PROJECT_BASELINE_VALIDATE
PROJECT_BASELINE_APPROVE
PROJECT_BASELINE_MARK_CURRENT
PROJECT_BASELINE_ARCHIVE
PROJECT_BASELINE_COMPARE

CHANGE_REQUEST_VIEW
CHANGE_REQUEST_CREATE
CHANGE_REQUEST_UPDATE
CHANGE_REQUEST_SUBMIT
CHANGE_REQUEST_APPROVE
CHANGE_REQUEST_REJECT
CHANGE_REQUEST_CANCEL
CHANGE_REQUEST_APPLY
CHANGE_REQUEST_ARCHIVE

CHANGE_REQUEST_ITEM_VIEW
CHANGE_REQUEST_ITEM_CREATE
CHANGE_REQUEST_ITEM_UPDATE
CHANGE_REQUEST_ITEM_DELETE

CHANGE_IMPACT_VIEW
CHANGE_IMPACT_UPDATE
CHANGE_IMPACT_CALCULATE

CHANGE_ORDER_VIEW
CHANGE_ORDER_CREATE
CHANGE_ORDER_UPDATE
CHANGE_ORDER_APPROVE
CHANGE_ORDER_REJECT
CHANGE_ORDER_ARCHIVE
```

Rules:

```text
1. Normal PROJECT_UPDATE does not automatically approve baseline.
2. Normal PROJECT_UPDATE does not automatically apply ChangeRequest.
3. Baseline approval requires stronger permission.
4. Change approval requires stronger permission.
5. Finance/quote impact fields require finance/quote permission or are masked.
6. Baseline snapshot can include finance/quote sections only if user has permissions.
7. AI/reporting consumers must enforce these permissions.
```

Simplification allowed:

```text
CHANGE_REQUEST_MANAGE covers item/impact update in early implementation.
```

Completion file must document.

---

# 12. Baseline creation algorithm

## 12.1 Create draft baseline

```text
1. Validate project and permission.
2. Validate source artifact IDs belong to project.
3. Create ProjectBaseline DRAFT.
4. Generate snapshot sections.
5. Generate summary.
6. Store validation warnings/errors.
7. Emit PROJECT_BASELINE_CREATED.
```

## 12.2 Snapshot generation

Recommended:

```text
1. Snapshot project metadata.
2. Snapshot phases.
3. Snapshot WBS tree.
4. Snapshot tasks.
5. Snapshot dependencies.
6. Snapshot milestones if implemented.
7. Snapshot selected schedule summary.
8. Snapshot selected estimation summary.
9. Snapshot selected finance summary.
10. Snapshot selected quote summary.
```

## 12.3 Validate baseline

```text
1. Check required sections exist.
2. Check source artifacts completed/approved.
3. Check no blocking unresolved estimate/finance errors.
4. Check quote status if quote section required.
5. Return validation result.
```

## 12.4 Approve baseline

```text
1. Baseline must be DRAFT/READY.
2. Validation must pass.
3. User must have PROJECT_BASELINE_APPROVE.
4. Baseline becomes APPROVED.
5. If markCurrent, unset previous current baseline.
6. Baseline immutable.
7. Emit PROJECT_BASELINE_APPROVED.
8. Audit.
```

---

# 13. ChangeRequest lifecycle algorithm

## 13.1 Draft

```text
1. Create ChangeRequest DRAFT.
2. Add items.
3. Add impact.
4. Edit allowed.
```

## 13.2 Submit

Before submit:

```text
1. At least one item.
2. Impact present if policy requires.
3. Required reason present.
4. Baseline reference valid if current baseline exists.
```

Then:

```text
status = SUBMITTED
audit/event
```

## 13.3 Approve/reject

Approve:

```text
1. Status SUBMITTED.
2. User has CHANGE_REQUEST_APPROVE.
3. Impact accepted.
4. status = APPROVED.
5. Audit/event.
```

Reject:

```text
1. Status SUBMITTED.
2. Reason required.
3. status = REJECTED.
4. Audit/event.
```

## 13.4 Apply

```text
1. Status APPROVED.
2. User has CHANGE_REQUEST_APPLY.
3. Start transaction.
4. For each item:
   - validate target still exists/current
   - call appropriate domain action
   - record item APPLIED or FAILED
5. If any required item fails, rollback unless partial apply explicitly supported.
6. Mark ChangeRequest APPLIED.
7. Emit events.
8. Recalculate schedule/estimation/finance/quote only if requested by items.
```

Recommended:

```text
No partial apply in Phase 19.
```

---

# 14. Post-baseline edit guard

Phase 19 must decide how to guard direct edits.

## 14.1 Strict mode recommendation

If current baseline exists:

```text
controlled changes must go through ChangeRequest
```

Controlled direct actions block:

```text
Create/update/archive WBS
Move WBS
Create/update/archive task
Change task estimateHours
Change task dueDate
Change task phase/WBS
Create/remove dependency
Move/resize Gantt task override
Change milestone date
Approve/mark current finance scenario
Approve/mark current quote version
```

## 14.2 Allowed direct changes

Allowed without ChangeRequest:

```text
comments
internal notes
activity logs
notification preferences
view preferences
minor non-controlled metadata if product allows
```

Completion file must list exact controlled fields.

## 14.3 Emergency override

Optional:

```text
BASELINE_OVERRIDE_EDIT
```

Rules:

```text
1. Requires strong permission.
2. Requires reason.
3. Audit.
4. Optionally auto-create ChangeRequest record.
```

Recommended:

```text
Defer emergency override unless required.
```

---

# 15. Impact calculation strategy

Phase 19 can support manual or calculated impact.

## 15.1 Manual impact

User enters:

```text
schedule impact days
estimate hours impact
cost impact
revenue impact
margin impact
risk impact
```

Recommended minimum:

```text
Manual impact + optional simple calculation.
```

## 15.2 Simple calculated impact

For task estimate change:

```text
estimateHoursImpact =
newEstimateHours - oldEstimateHours
```

Cost impact:

```text
laborCostImpact =
estimateHoursImpact × resolvedRateSnapshot or current rate
```

But careful:

```text
Using current rate may differ from baseline rate.
```

Recommended:

```text
Use baseline/finance snapshot rate if available, and document.
```

## 15.3 Schedule impact

If schedule recalculation is triggered:

```text
scheduleImpactDays =
newEstimatedFinishDate - baselineEstimatedFinishDate
```

Phase 19 can defer exact schedule impact calculation to Phase 22/AI if too complex.

---

# 16. Integration with Project Core

Change apply must call existing actions:

```text
CreateTaskAction
UpdateTaskAction
ArchiveTaskAction
CreateWbsNodeAction
MoveWbsNodeAction
CreateTaskDependencyAction
RemoveTaskDependencyAction
CreateProjectPhaseAction
UpdateProjectPhaseAction
```

Rules:

```text
1. Do not directly mutate tables.
2. Existing validation must run.
3. Existing IAM must run or apply service must perform equivalent authorization.
4. Existing events/activity should still emit.
```

---

# 17. Integration with Schedule / Gantt

ChangeRequest may request:

```text
new ScheduleRun
new Gantt recalculation
manual schedule override
milestone date change
```

Rules:

```text
1. Baseline stores previous schedule reference.
2. Approved change may trigger new schedule run.
3. New schedule run does not alter baseline.
4. Gantt displays baseline variance later in Phase 22/19 optional.
```

Baseline comparison overlay in Gantt:

```text
DEFERRED_TO_PHASE_22_REPORTING_EXPORT or Phase 19 optional.
```

---

# 18. Integration with Estimation / Finance / Quote

ChangeRequest may request:

```text
new EstimationRun
new FinanceScenario
new QuoteVersion
```

Recommended:

```text
Applying scope/task estimate change should not silently update approved finance/quote.
```

Instead:

```text
1. Apply project scope/task change.
2. Create new estimation run if requested.
3. Create new finance scenario if requested.
4. Create new quote version if commercial impact exists.
```

This prevents silent commercial changes.

---

# 19. Integration with Contract / Billing

Phase 19 does not implement contract/billing.

If quote has been accepted and contract exists in future:

```text
ChangeOrder may amend contract.
```

Deferred to:

```text
Phase 36 — Contract / Billing / Revenue
```

Phase 19 ChangeOrder is only a governance record.

---

# 20. Integration with Notifications

Phase 19 seeds events.

Potential notifications:

```text
CHANGE_REQUEST_SUBMITTED_APPROVER_NOTIFICATION
CHANGE_REQUEST_APPROVED_NOTIFICATION
CHANGE_REQUEST_REJECTED_NOTIFICATION
CHANGE_REQUEST_APPLIED_NOTIFICATION
BASELINE_APPROVED_NOTIFICATION
CHANGE_ORDER_APPROVED_NOTIFICATION
```

Notification rule configuration can be Phase 20.

---

# 21. Integration with AI Agent

Phase 21 may assist:

```text
summarize change impact
compare baseline vs proposed
suggest risk mitigation
draft change request
draft change order text
```

Rules:

```text
1. AI cannot approve.
2. AI cannot apply changes.
3. AI must respect baseline/change/finance/quote permissions.
4. AI output is proposal.
```

---

# 22. Event Registry integration

Recommended source system:

```text
SCOPERY_PROJECT_GOVERNANCE
```

Required events:

```text
PROJECT_BASELINE_CREATED
PROJECT_BASELINE_REFRESHED
PROJECT_BASELINE_VALIDATED
PROJECT_BASELINE_APPROVED
PROJECT_BASELINE_MARKED_CURRENT
PROJECT_BASELINE_ARCHIVED

CHANGE_REQUEST_CREATED
CHANGE_REQUEST_UPDATED
CHANGE_REQUEST_ITEM_CREATED
CHANGE_REQUEST_ITEM_UPDATED
CHANGE_REQUEST_ITEM_DELETED
CHANGE_IMPACT_UPDATED
CHANGE_IMPACT_CALCULATED
CHANGE_REQUEST_SUBMITTED
CHANGE_REQUEST_APPROVED
CHANGE_REQUEST_REJECTED
CHANGE_REQUEST_CANCELLED
CHANGE_REQUEST_APPLIED
CHANGE_REQUEST_APPLY_FAILED
CHANGE_REQUEST_ARCHIVED

CHANGE_ORDER_CREATED
CHANGE_ORDER_UPDATED
CHANGE_ORDER_SUBMITTED
CHANGE_ORDER_APPROVED
CHANGE_ORDER_REJECTED
CHANGE_ORDER_ARCHIVED

POST_BASELINE_EDIT_BLOCKED
BASELINE_OVERRIDE_EDIT_USED
```

Standard variables:

```text
actor.userId
organization.id
workspace.id
project.id
baseline.id
baseline.number
changeRequest.id
changeRequest.code
changeRequest.status
changeOrder.id
changeOrder.code
sourceScheduleRun.id
sourceEstimationRun.id
sourceFinanceScenario.id
sourceQuoteVersion.id
impact.scheduleDays
impact.cost
impact.revenue
impact.margin
occurredAt
traceId
```

---

# 23. Platform audit/outbox/idempotency integration

## 23.1 Activity log actions

```text
PROJECT_BASELINE_CREATED
PROJECT_BASELINE_APPROVED
PROJECT_BASELINE_MARKED_CURRENT
CHANGE_REQUEST_CREATED
CHANGE_REQUEST_SUBMITTED
CHANGE_REQUEST_APPROVED
CHANGE_REQUEST_REJECTED
CHANGE_REQUEST_APPLIED
CHANGE_ORDER_CREATED
CHANGE_ORDER_APPROVED
POST_BASELINE_EDIT_BLOCKED
```

## 23.2 Audit-sensitive actions

Audit:

```text
Baseline approved
Baseline marked current
Baseline archived
ChangeRequest submitted
ChangeRequest approved/rejected
ChangeRequest applied
Post-baseline direct edit blocked
Override used
ChangeOrder approved
```

Reason:

```text
These actions affect approved project commitments.
```

## 23.3 Idempotency

Recommended for:

```text
POST /api/projects/{projectId}/baselines
POST /api/projects/{projectId}/baselines/{baselineId}/approve
POST /api/projects/{projectId}/baselines/{baselineId}/mark-current
POST /api/projects/{projectId}/change-requests
POST /api/projects/{projectId}/change-requests/{id}/submit
POST /api/projects/{projectId}/change-requests/{id}/approve
POST /api/projects/{projectId}/change-requests/{id}/apply
POST /api/projects/{projectId}/change-requests/{id}/change-orders
```

## 23.4 Outbox

Baseline/change events should use platform outbox if available.

---

# 24. Business rules master

## 24.1 Baseline rules

```text
BSL-001 Project must exist.
BSL-002 Project must not be ARCHIVED.
BSL-003 Baseline number unique within project.
BSL-004 DRAFT baseline can be refreshed.
BSL-005 APPROVED baseline immutable.
BSL-006 Only one current baseline per project.
BSL-007 Baseline stores snapshot and summary.
BSL-008 Baseline approval requires validation.
BSL-009 Baseline is not live project.
BSL-010 Baseline does not create contract/invoice.
```

## 24.2 Snapshot rules

```text
BSL-SNP-001 Snapshot sections must belong to same project.
BSL-SNP-002 ScheduleRun must be COMPLETED if included.
BSL-SNP-003 EstimationRun must be COMPLETED if included.
BSL-SNP-004 FinanceScenario must be APPROVED/CURRENT if included.
BSL-SNP-005 QuoteVersion must be APPROVED/SENT/ACCEPTED if included for commercial baseline.
BSL-SNP-006 Snapshot immutable after approval.
```

## 24.3 ChangeRequest rules

```text
CR-001 Project must exist.
CR-002 Project must not be ARCHIVED.
CR-003 ChangeRequest code unique within project.
CR-004 DRAFT can be edited.
CR-005 SUBMITTED cannot be edited except lifecycle.
CR-006 APPROVED can be applied.
CR-007 REJECTED cannot be applied.
CR-008 APPLIED cannot be edited.
CR-009 At least one item required before submit.
CR-010 Impact required before approval if policy enabled.
```

## 24.4 ChangeRequestItem rules

```text
CRI-001 Item belongs to ChangeRequest.
CRI-002 Target if provided must belong to project.
CRI-003 Operation required.
CRI-004 Summary required.
CRI-005 DRAFT request required to edit items.
CRI-006 Apply uses domain actions.
CRI-007 Apply does not bypass validation.
```

## 24.5 Approval/apply rules

```text
CRA-001 Submit requires DRAFT.
CRA-002 Approve requires SUBMITTED.
CRA-003 Reject requires SUBMITTED and reason.
CRA-004 Apply requires APPROVED.
CRA-005 Apply requires transaction.
CRA-006 Partial apply disabled by default.
CRA-007 Apply creates audit/events.
```

## 24.6 Post-baseline guard rules

```text
PBG-001 Current baseline enables controlled edit guard.
PBG-002 Controlled fields cannot be directly changed in strict mode.
PBG-003 Blocked edit returns clear error.
PBG-004 Override requires permission and reason if implemented.
PBG-005 Guard does not block comments/view preferences.
```

## 24.7 ChangeOrder rules

```text
CO-001 ChangeOrder belongs to ChangeRequest.
CO-002 ChangeRequest should be APPROVED or APPLIED.
CO-003 ChangeOrder stores commercial impact.
CO-004 ChangeOrder is not invoice.
CO-005 ChangeOrder is not contract unless contract module exists.
```

---

# 25. Error catalog requirements

Exact names follow project convention, but these concepts must exist.

```text
PROJECT_BASELINE_NOT_FOUND
PROJECT_BASELINE_PROJECT_ARCHIVED
PROJECT_BASELINE_NUMBER_ALREADY_EXISTS
PROJECT_BASELINE_NOT_DRAFT
PROJECT_BASELINE_ALREADY_APPROVED
PROJECT_BASELINE_IMMUTABLE
PROJECT_BASELINE_VALIDATION_FAILED
PROJECT_BASELINE_CURRENT_CONFLICT
PROJECT_BASELINE_SNAPSHOT_FAILED
PROJECT_BASELINE_SOURCE_MISMATCH
PROJECT_BASELINE_SCHEDULE_NOT_COMPLETED
PROJECT_BASELINE_ESTIMATION_NOT_COMPLETED
PROJECT_BASELINE_FINANCE_NOT_APPROVED
PROJECT_BASELINE_QUOTE_NOT_APPROVED

CHANGE_REQUEST_NOT_FOUND
CHANGE_REQUEST_CODE_ALREADY_EXISTS
CHANGE_REQUEST_PROJECT_ARCHIVED
CHANGE_REQUEST_NOT_DRAFT
CHANGE_REQUEST_NOT_SUBMITTED
CHANGE_REQUEST_NOT_APPROVED
CHANGE_REQUEST_ALREADY_APPLIED
CHANGE_REQUEST_ITEM_REQUIRED
CHANGE_REQUEST_IMPACT_REQUIRED
CHANGE_REQUEST_REJECTION_REASON_REQUIRED
CHANGE_REQUEST_APPLY_FAILED
CHANGE_REQUEST_PARTIAL_APPLY_NOT_SUPPORTED

CHANGE_REQUEST_ITEM_NOT_FOUND
CHANGE_REQUEST_ITEM_TARGET_MISMATCH
CHANGE_REQUEST_ITEM_UNSUPPORTED_OPERATION
CHANGE_REQUEST_ITEM_INVALID_PAYLOAD

CHANGE_IMPACT_NOT_FOUND
CHANGE_IMPACT_INVALID_CURRENCY
CHANGE_IMPACT_INVALID_AMOUNT

CHANGE_ORDER_NOT_FOUND
CHANGE_ORDER_CHANGE_REQUEST_MISMATCH
CHANGE_ORDER_NOT_DRAFT
CHANGE_ORDER_NOT_APPROVABLE

POST_BASELINE_EDIT_BLOCKED
BASELINE_OVERRIDE_PERMISSION_REQUIRED
BASELINE_OVERRIDE_REASON_REQUIRED

PROJECT_BASELINE_ACCESS_DENIED
CHANGE_REQUEST_ACCESS_DENIED
CHANGE_ORDER_ACCESS_DENIED
```

---

# 26. Required tests

Phase 19 is incomplete without tests.

---

## 26.1 Baseline tests

```text
createBaseline_valid_success
createBaseline_archivedProject_rejected
createBaseline_sourceScheduleRunOtherProject_rejected
createBaseline_sourceEstimationOtherProject_rejected
createBaseline_sourceFinanceOtherProject_rejected
createBaseline_sourceQuoteOtherProject_rejected
createBaseline_generatesScopeSnapshot
createBaseline_generatesScheduleSummary
createBaseline_generatesEstimateSummary
createBaseline_generatesFinanceSummary
createBaseline_generatesQuoteSummary
approveBaseline_valid_success
approveBaseline_validationFailed_rejected
approvedBaseline_immutable
markCurrent_onlyOneCurrentBaseline
archiveBaseline_valid_success
```

## 26.2 Snapshot/history tests

```text
baselineSnapshot_unchangedAfterTaskChange
baselineSnapshot_unchangedAfterScheduleRunNew
baselineSnapshot_unchangedAfterFinanceScenarioChange
baselineSnapshot_unchangedAfterQuoteVersionChange
baselineSnapshot_containsTaskEstimateDueDateAssignee
baselineSnapshot_containsDependencies
```

## 26.3 ChangeRequest tests

```text
createChangeRequest_valid_success
createChangeRequest_duplicateCode_conflict
updateDraftChangeRequest_success
updateSubmittedChangeRequest_rejected
submitWithoutItems_rejected
submitWithItems_success
approveSubmitted_success
approveWithoutPermission_forbidden
rejectRequiresReason
cancelDraft_success
approvedCanApply
rejectedCannotApply
appliedCannotEdit
```

## 26.4 ChangeRequestItem tests

```text
createItem_validTaskCreate_success
createItem_targetOtherProject_rejected
createItem_unsupportedOperation_rejected
updateItem_onDraft_success
deleteItem_onSubmitted_rejected
itemStoresBeforeAfterSnapshot
```

## 26.5 ChangeImpact tests

```text
updateImpact_valid_success
calculateImpact_taskEstimateChange_success
impactCurrencyMismatch_rejected
impactRequiredBeforeApproval_rejectedWhenMissing
```

## 26.6 Apply tests

```text
applyApprovedChange_createTask_success
applyApprovedChange_updateTaskEstimate_success
applyApprovedChange_createDependency_success
applyApprovedChange_unsupportedOperation_rejected
applyApprovedChange_domainValidationFailure_rollsBack
applyApprovedChange_partialApplyDisabled
applyCreatesAuditAndEvents
```

## 26.7 Post-baseline guard tests

```text
currentBaseline_blocksDirectTaskEstimateUpdate
currentBaseline_blocksDirectWbsMove
currentBaseline_blocksDirectDependencyCreate
currentBaseline_blocksDirectMilestoneDateChange
currentBaseline_allowsCommentOrViewPreference
overrideWithoutPermission_forbidden
overrideWithPermissionAndReason_audited_ifImplemented
```

## 26.8 ChangeOrder tests

```text
createChangeOrder_fromApprovedChange_success
createChangeOrder_fromDraftChange_rejectedOrDocumented
approveChangeOrder_valid_success
changeOrderDoesNotCreateInvoice
changeOrderDoesNotCreateContract
```

## 26.9 Authorization tests

```text
viewBaseline_withoutPermission_forbidden
approveBaseline_withoutPermission_forbidden
createChangeRequest_withoutPermission_forbidden
approveChangeRequest_withoutPermission_forbidden
applyChangeRequest_withoutPermission_forbidden
viewFinanceImpact_withoutFinancePermission_maskedOrForbidden
crossWorkspaceBaselineAccess_forbidden
```

## 26.10 Seeder/event tests

```text
baselineChangeEventSeeder_firstRun_createsDefinitions
baselineChangeEventSeeder_secondRun_noDuplicates
baselineChangePermissionSeeder_authoritiesExist
baselineApproved_eventEmitted
changeRequestApproved_eventEmitted
changeRequestApplied_eventEmitted
postBaselineEditBlocked_eventEmitted
```

---

# 27. Manual verification checklist

Completion file must include:

```text
1. Create project with WBS/tasks/dependencies.
2. Create completed schedule run.
3. Create completed estimation run.
4. Create approved finance scenario.
5. Create approved/sent quote version.
6. Create baseline from these artifacts.
7. Approve and mark baseline current.
8. Confirm baseline snapshot remains unchanged after live task edit attempt.
9. Try direct controlled edit and confirm blocked.
10. Create ChangeRequest.
11. Add task change item.
12. Add impact.
13. Submit ChangeRequest.
14. Approve ChangeRequest.
15. Apply ChangeRequest.
16. Confirm live project updated through domain actions.
17. Confirm new audit/events.
18. Create ChangeOrder if commercial impact exists.
19. Confirm no contract/invoice/billing/actual cost created.
```

---

# 28. Acceptance criteria

Phase 19 is accepted only if:

```text
1. Current baseline/change capability is classified against TO-BE.
2. ProjectBaseline implemented/tested.
3. Baseline snapshot sections implemented/tested.
4. Baseline approval/current immutability implemented/tested.
5. ChangeRequest implemented/tested.
6. ChangeRequestItem implemented/tested.
7. ChangeImpact implemented/tested.
8. Change approval/reject/apply lifecycle implemented/tested.
9. Approved apply uses domain actions and is transactional.
10. Post-baseline edit guard implemented or documented with strict/warning mode.
11. ChangeOrder foundation implemented/tested if in scope.
12. Baseline/change permissions implemented/tested.
13. Events seeded idempotently.
14. Activity/audit/outbox follows Phase 04.
15. No contract/invoice/billing/tax/actual cost is falsely claimed.
16. mvn compile passes.
17. mvn test passes.
18. Completion file exists and includes gap matrix.
```

Agent must not mark complete if:

```text
mvn test fails
approved baseline can be edited
baseline snapshot changes after live project changes
direct controlled edits bypass ChangeRequest after current baseline
approved change apply directly mutates tables without domain validation
partial apply leaves inconsistent state
change order creates fake invoice/contract
finance/quote impact exposed without permission
deferred gaps missing
```

---

# 29. Required phase completion file

Agent must create:

```text
docs/phase-complete/PHASE_19_BASELINE_CHANGE_REQUEST_TO_BE_COMPLETE.md
```

Required sections:

```text
# Phase 19 — Baseline / Change Request TO-BE Complete

## 1. Summary
## 2. Source Inputs Reviewed
## 3. Current vs TO-BE Classification Matrix
## 4. Implemented in Current BE
## 5. Implemented / Hardened in This Phase
## 6. Seed-only Items Added
## 7. Deferred Items and Target Phase
## 8. Governance Boundary Decision
## 9. Entity Mapping
## 10. API Changes
## 11. Baseline Snapshot Strategy
## 12. Baseline Validation / Approval Strategy
## 13. Post-baseline Edit Guard Strategy
## 14. ChangeRequest Lifecycle Strategy
## 15. ChangeRequestItem Strategy
## 16. ChangeImpact Strategy
## 17. Apply Strategy
## 18. ChangeOrder Strategy
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
## 30. Future Phases That Must Return to Baseline / Change Control
```

---

# 30. Future phases that must return to Baseline / Change Control

## 30.1 Phase 20 — Project Events / Notifications

Must notify:

```text
baseline approved
change request submitted
change request approved/rejected
change request applied
post-baseline edit blocked
change order approved
```

## 30.2 Phase 21 — AI-assisted Project Planning

AI may draft CR and summarize impact.

AI cannot approve/apply.

## 30.3 Phase 22 — Reporting / Export

Must add:

```text
baseline vs current diff
change log report
approved change impact report
baseline export
change order export
```

## 30.4 Phase 23 — Core Hardening

Review:

```text
snapshot size/performance
JSON indexing
apply transaction safety
guard coverage
authorization coverage
audit completeness
concurrency
```

## 30.5 Phase 27 — Document Hub

May attach:

```text
baseline documents
change request documents
client approval documents
```

## 30.6 Phase 34 — Workflow / Approval

Full workflow:

```text
multi-step approval
approval conditions
SoD
delegation
escalation
```

## 30.7 Phase 36 — Contract / Billing / Revenue

ChangeOrder may become:

```text
contract amendment
billing schedule update
invoice adjustment
revenue recognition update
```

## 30.8 Phase 37 — Time / Attendance / Expense

Baseline actual comparison:

```text
planned vs actual hours/cost
approved CR vs actual variance
```

---

# 31. Agent anti-bịa rules

The agent must not:

```text
1. Claim baseline is live project.
2. Allow approved baseline mutation.
3. Let live project changes silently alter baseline.
4. Let controlled post-baseline edits bypass ChangeRequest in strict mode.
5. Apply ChangeRequest by direct table mutation.
6. Apply unsupported change item silently.
7. Partially apply without explicit partial apply policy.
8. Claim ChangeOrder is invoice.
9. Claim ChangeOrder is legal contract.
10. Claim actual cost exists.
11. Claim contract/billing/revenue recognition exists.
12. Expose finance/quote impact without permission.
13. Hide deferred workflow/contract/billing/actual gaps.
```

---

# 32. Prompt to give coding agent

```text
You are implementing Phase 19 — TO-BE Project Baseline, Change Request, Change Order & Controlled Scope/Schedule/Finance Governance.

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
- Phase 06 Notification TO-BE spec
- Phase 07 AI Agent TO-BE spec
- Phase 08 Knowledge TO-BE spec
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
- Current backend code, migrations, tests

Your task:
1. Compare current baseline/change capability against this Phase 19 TO-BE spec.
2. Classify each capability as CURRENTLY_IMPLEMENTED, PARTIALLY_IMPLEMENTED, MUST_IMPLEMENT_IN_PHASE_19, SEED_ONLY_IN_PHASE_19, DEFERRED_TO_PHASE_XX, or NOT_IN_SCOPE_FOR_BACKEND_NOW.
3. Implement only Phase 19 required items.
4. Implement ProjectBaseline and immutable baseline snapshot.
5. Implement baseline validation/approval/current marker.
6. Implement ChangeRequest, ChangeRequestItem, ChangeImpact, ChangeApprovalAction.
7. Implement approved ChangeRequest apply through domain actions, transactionally.
8. Implement strict or documented post-baseline edit guard.
9. Implement simple ChangeOrder foundation if commercial impact exists.
10. Add baseline/change permissions and event definitions.
11. Add tests listed in this spec.
12. Run mvn compile and mvn test.
13. Create docs/phase-complete/PHASE_19_BASELINE_CHANGE_REQUEST_TO_BE_COMPLETE.md with full gap matrix.

Do not implement or claim contract execution, invoice/billing, revenue recognition, tax accounting, actual cost, timesheet actuals, legal workflow, client portal approval, or AI approval in this phase.
```

---

# 33. Quick tracking matrix

| Capability | Current backend | Phase 19 action | Later phase |
|---|---|---|---|
| ProjectBaseline | Missing/unknown | Must implement | — |
| Baseline scope snapshot | Missing/unknown | Must implement | — |
| Baseline schedule snapshot | Missing/unknown | Must implement | Phase 22 comparison |
| Baseline estimate snapshot | Missing/unknown | Must implement | — |
| Baseline finance snapshot | Missing/unknown | Must implement | — |
| Baseline quote snapshot | Missing/unknown | Implement if quote exists | — |
| Baseline approval/current | Missing/unknown | Must implement | Workflow Phase 34 |
| ChangeRequest | Missing/unknown | Must implement | — |
| ChangeRequestItem | Missing/unknown | Must implement | — |
| ChangeImpact | Missing/unknown | Must implement | AI/Reporting later |
| Apply approved change | Missing/unknown | Must implement | — |
| Post-baseline edit guard | Missing/unknown | Must implement | Phase 23 audit |
| ChangeOrder foundation | Missing/unknown | Must implement foundation | Contract Phase 36 |
| Detailed diff report | Missing | Optional/defer | Phase 22 |
| Multi-step approval | Missing | Defer | Phase 34 |
| Contract amendment | Missing | Defer | Phase 36 |
| Billing update | Missing | Defer | Phase 36 |
| Actual cost comparison | Missing | Defer | Phase 37 |
| AI impact summary | Missing | Defer | Phase 21 |

---

# 34. Final principle

Phase 19 is not complete when "a baseline row can be stored."

Phase 19 is complete when Scopery can govern project commitments:

```text
approved baseline snapshot
+ protected live project
+ controlled change request
+ impact record
+ approval
+ transactional apply
= change-controlled project delivery
```

Baseline is history.

Project is live.

ChangeRequest is proposal.

Applied ChangeRequest is controlled mutation.

ChangeOrder is not invoice.

No approved commitment should change silently.
