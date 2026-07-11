# PHASE 16 — TO-BE Estimation Roll-up, Task Labor Estimate, WBS/Phase/Project Effort Summary & Rate Snapshot Foundation

> Project: Scopery Backend  
> Phase: 16  
> Document type: TO-BE implementation-grade specification  
> Status: Planning / Gap-driven implementation spec  
> Depends on: Phase 00 — Master Backend Roadmap, Phase 01 — API Path / Security Baseline, Phase 02 — IAM Core, Phase 03 — Workspace Core, Phase 04 — Platform Audit / Outbox / Idempotency, Phase 05 — Event Registry, Phase 06 — Notification Engine, Phase 07 — AI Agent Platform, Phase 08 — Knowledge / Document Type Catalog, Phase 09 — Project Core / Phase / WBS / Task, Phase 10 — Project Authorization, Phase 11 — Project Template / Phase Catalog, Phase 12 — Resource Calendar / Capacity, Phase 13 — Task Scheduling Engine, Phase 14 — WBS-driven Gantt, Phase 15 — Rate Card / Cost Policy  
> API base: `/api`  
> Primary module: `modules/estimation` or `modules/project/estimation` depending on repository architecture  
> Related modules: `project`, `ratecard`, `capacity`, `scheduling`, `workspace`, `iam`, `eventregistry`, `notification`, future `finance`, `quote`, `baseline`, `reporting`, `aiagent`  
> Important rule: This file is **not an as-is document**. It defines the TO-BE estimation roll-up foundation. Phase 16 does not implement full project budget, gross margin, profit before tax, overhead, revenue split, quote, contract value solver, billing, actual cost, or timesheet.

---

# 0. Purpose of this file

Phase 16 creates Scopery's estimation roll-up foundation.

Previous phases created:

```text
Phase 09:
- Project / Phase / WBS / Task
- Task.estimateHours
- Task.assignee / inCharge
- Task.dueDate

Phase 13:
- TaskSchedule
- ScheduledDailyWork
- estimated start / finish forecast

Phase 15:
- CostRole
- RateCard
- RateCardVersion
- RateCardLine
- CCH / costRatePerHour
- billingRatePerHour
- inflation policy
- RateResolutionService
- RateSnapshot contract
```

Phase 16 answers:

```text
How many estimated hours are in a task, WBS node, phase, and project?
Which cost role/rate applies to a task estimate?
What is the estimated labor cost preview by task/WBS/phase/project?
What is the estimated billing amount preview by task/WBS/phase/project?
Which rate snapshot was used?
What assumptions were used during estimate calculation?
How can later finance/quote/baseline modules reuse a stable estimate snapshot?
```

Phase 16 does **not** answer:

```text
What is the final project budget?
What is project gross margin?
What is project profit before tax?
What is overhead?
What is revenue split?
What is contract value?
What is quote price?
What is invoice amount?
What is actual cost from timesheets?
```

Those are later phases.

---

# 1. Source inputs

Before coding Phase 16, the agent must read:

```text
1. Current backend codebase
2. Phase 09 Project Core TO-BE spec and implementation
3. Phase 10 Project Authorization TO-BE spec and implementation
4. Phase 12 Resource Calendar / Capacity TO-BE spec and implementation
5. Phase 13 Task Scheduling Engine TO-BE spec and implementation
6. Phase 14 WBS-driven Gantt TO-BE spec and implementation
7. Phase 15 Rate Card / Cost Policy TO-BE spec and implementation
8. Phase 04 Platform Audit / Outbox / Idempotency spec
9. Phase 05 Event Registry spec
10. Current BE feature/entity/business-rule inventory
11. Dynamic Work OS feature catalog
12. Existing task/rate/cost role mapping code if any
```

The agent must not implement Phase 16 from assumptions only.

---

# 2. Current expected backend state

After Phase 15, the backend should have:

```text
Task.estimateHours
ProjectPhase
WbsNode tree
TaskDependency
TaskSchedule optional
WorkspaceMemberCostRoleAssignment optional
CostRole
RateCard
RateCardVersion
RateCardLine
RateResolutionService
RateSnapshot contract
```

Likely missing:

```text
EstimationRun
TaskEstimateSnapshot
WbsEstimateRollup
PhaseEstimateRollup
ProjectEstimateSummary
Estimate assumption model
Estimate calculation service
Estimate version/history
Estimate comparison
Estimate approval
```

Phase 16 implements roll-up and snapshot, but not approval/budget.

---

# 3. Phase 16 target statement

Phase 16 must deliver a future-ready estimation foundation:

```text
1. Create an estimation run for a project.
2. Read tasks, phases, WBS hierarchy, assignees, cost roles, and rate cards.
3. Resolve rate snapshots for each task estimate.
4. Calculate task estimated labor cost preview.
5. Calculate optional task billing amount preview.
6. Roll up effort and estimated labor amount by WBS subtree.
7. Roll up effort and estimated labor amount by phase.
8. Produce project-level estimate summary.
9. Store all calculation snapshots so future rate changes do not change historical estimate runs.
10. Expose estimation APIs and read models.
11. Emit estimation events and audit sensitive recalculations.
12. Protect estimation APIs with IAM.
13. Clearly defer full finance, budget, margin, quote, and approval.
```

---

# 4. Core financial boundary

Phase 16 can calculate:

```text
estimated labor hours
estimated labor cost preview
estimated billing amount preview
rate snapshot used
```

Phase 16 cannot calculate/claim:

```text
approved budget
gross margin
profit before tax
overhead
custom phase cost
vendor cost
contingency
revenue split
contract value
quote amount
invoice amount
actual cost
```

Important language:

```text
Use "estimate preview" or "labor estimate".
Do not call it "project budget" or "finance scenario".
```

---

# 5. Classification labels

Every requirement uses one label:

| Label | Meaning |
|---|---|
| `CURRENTLY_IMPLEMENTED` | Current backend already implements it. Agent must verify and test. |
| `PARTIALLY_IMPLEMENTED` | Current backend implements part of it. |
| `MUST_IMPLEMENT_IN_PHASE_16` | Must be implemented or hardened now. |
| `SEED_ONLY_IN_PHASE_16` | Seed events/permissions/config now; full consumer later. |
| `DEFERRED_TO_PHASE_XX` | Do not implement now; later phase must return. |
| `DEFERRED_TO_POST_23_BACKLOG` | Outside core 00–23 roadmap, part of full Work OS expansion. |
| `NOT_IN_SCOPE_FOR_BACKEND_NOW` | Explicitly not backend scope now. |

---

# 6. Phase 16 scope decision

## 6.1 Must implement now

```text
EstimationRun
TaskEstimateSnapshot
WbsEstimateRollup
PhaseEstimateRollup
ProjectEstimateSummary
Estimate calculation service
Rate snapshot persistence
Task cost role resolution strategy
WBS subtree roll-up
Phase roll-up
Project estimate summary
Estimation APIs
Estimation permissions
Estimation events
Tests
Completion report
```

## 6.2 Optional now

```text
Estimate scenario name/description
Manual task estimate adjustment inside run
Estimate comparison between runs
Use schedule dates for rate target date
Use task dueDate for rate target date
Billing amount preview
Confidence/complexity fields
```

Implement only if product requires.

## 6.3 Must not implement now

```text
Project budget approval
Gross margin
Profit before tax
Overhead
Custom phase costs
Vendor cost
Contingency
Revenue split
Contract value solver
Quote approval
Billing/invoice
Timesheet actual cost
Payroll/salary
Formal baseline approval
Change request
AI estimate recommendation
```

---

# 7. Core estimation formulas

## 7.1 Task estimated labor cost

```text
TaskEstimatedLaborCost =
TaskEstimateHours × ResolvedAdjustedCostRatePerHour
```

Where:

```text
TaskEstimateHours = Task.estimateHours
ResolvedAdjustedCostRatePerHour = RateResolutionService adjustedCostRate
```

## 7.2 Task estimated billing amount preview

Optional:

```text
TaskEstimatedBillingPreview =
TaskEstimateHours × ResolvedAdjustedBillingRatePerHour
```

Important:

```text
Billing preview is not quote price.
Billing preview is not contract value.
Billing preview is not invoice amount.
```

## 7.3 WBS roll-up

For a WBS node:

```text
WbsEstimatedHours =
sum(TaskEstimateHours for tasks directly or indirectly under WBS subtree)
```

```text
WbsEstimatedLaborCost =
sum(TaskEstimatedLaborCost for tasks directly or indirectly under WBS subtree)
```

```text
WbsEstimatedBillingPreview =
sum(TaskEstimatedBillingPreview for tasks directly or indirectly under WBS subtree)
```

## 7.4 Phase roll-up

For a phase:

```text
PhaseEstimatedHours =
sum(TaskEstimateHours for tasks in phase)
```

```text
PhaseEstimatedLaborCost =
sum(TaskEstimatedLaborCost for tasks in phase)
```

## 7.5 Project roll-up

```text
ProjectEstimatedHours =
sum(TaskEstimateHours for included tasks)
```

```text
ProjectEstimatedLaborCost =
sum(TaskEstimatedLaborCost for included tasks)
```

```text
ProjectEstimatedBillingPreview =
sum(TaskEstimatedBillingPreview for included tasks)
```

## 7.6 Average blended rate

```text
AverageCostRate =
ProjectEstimatedLaborCost / ProjectEstimatedHours
```

If hours = 0:

```text
AverageCostRate = null
```

```text
AverageBillingRate =
ProjectEstimatedBillingPreview / ProjectEstimatedHours
```

---

# 8. TO-BE capability matrix

---

## 8.1 EST-001 — EstimationRun

| Item | Value |
|---|---|
| Future capability | Create reproducible project estimation calculation |
| Current state | Missing/unknown |
| Phase 16 target | Implement |
| Classification | `MUST_IMPLEMENT_IN_PHASE_16` |

Rules:

```text
1. EstimationRun belongs to one project.
2. Project must exist and not be ARCHIVED.
3. Actor must have ESTIMATION_RUN_CREATE or equivalent.
4. Run stores calculation mode and assumptions.
5. Run status starts PENDING/RUNNING.
6. Completed run stores summary totals.
7. Failed run stores error code/message.
8. Project can have many estimation runs.
9. Latest completed run can be marked current estimate.
10. EstimationRun does not mutate Task.estimateHours unless explicit adjustment feature exists.
```

Status:

```text
PENDING
RUNNING
COMPLETED
FAILED
CANCELLED
```

---

## 8.2 EST-002 — TaskEstimateSnapshot

| Item | Value |
|---|---|
| Future capability | Store task estimate calculation with rate snapshot |
| Current state | Missing/unknown |
| Phase 16 target | Implement |
| Classification | `MUST_IMPLEMENT_IN_PHASE_16` |

Rules:

```text
1. Snapshot belongs to one EstimationRun.
2. Snapshot belongs to one Task.
3. Task must belong to same project.
4. Task estimateHours copied into snapshot.
5. CostRole resolved and copied.
6. Rate snapshot copied from RateResolutionService.
7. estimatedLaborCost calculated and stored.
8. estimatedBillingPreview optional.
9. Snapshot does not change when task/rate changes later.
10. Snapshot does not contain salary.
```

---

## 8.3 EST-003 — Cost role resolution

| Item | Value |
|---|---|
| Future capability | Determine which CostRole applies to a task |
| Current state | Missing/partial |
| Phase 16 target | Implement deterministic strategy |
| Classification | `MUST_IMPLEMENT_IN_PHASE_16` |

Recommended resolution order:

```text
1. Task explicit costRoleId / costRoleCode if added.
2. WorkspaceMemberCostRoleAssignment for task assignee/inCharge on target date.
3. Project default cost role mapping if implemented.
4. Fallback role configured for workspace/project.
5. Error ESTIMATE_COST_ROLE_NOT_RESOLVED.
```

Important:

```text
Do not infer role from personal salary.
Do not infer role from IAM role.
```

If Task has no explicit costRole field and member mapping was deferred in Phase 15:

```text
Phase 16 must either add a task costRole field or implement member role mapping now.
```

---

## 8.4 EST-004 — Rate snapshot persistence

| Item | Value |
|---|---|
| Future capability | Preserve historical estimate values |
| Current state | Rate snapshot contract from Phase 15 |
| Phase 16 target | Persist it in TaskEstimateSnapshot |
| Classification | `MUST_IMPLEMENT_IN_PHASE_16` |

Required snapshot fields:

```text
rateCardId
rateCardVersionId
rateCardLineId
costRoleId
costRoleCode
baseCostRate
adjustedCostRate
baseBillingRate
adjustedBillingRate
currencyCode
inflationPolicyId
inflationPercent
yearsForward
resolvedForDate
resolvedAt
```

Rules:

```text
1. Future rate card changes do not update snapshot.
2. Future inflation policy changes do not update snapshot.
3. Snapshot can reference archived rate card version.
4. Snapshot never includes salary.
```

---

## 8.5 EST-005 — WBS roll-up

| Item | Value |
|---|---|
| Future capability | Roll up estimates through WBS tree |
| Current state | WBS tree exists; roll-up missing |
| Phase 16 target | Implement |
| Classification | `MUST_IMPLEMENT_IN_PHASE_16` |

Rules:

```text
1. WBS node must belong to project.
2. Roll-up includes tasks directly linked to node.
3. Roll-up includes tasks under all descendant WBS nodes.
4. Tasks without WBS are excluded from WBS node roll-up but included in project unassigned bucket.
5. Archived WBS nodes excluded by default unless options include archived.
6. Cancelled/archived tasks excluded by default.
7. Roll-up stores child totals.
```

---

## 8.6 EST-006 — Phase roll-up

| Item | Value |
|---|---|
| Future capability | Roll up estimates by project phase |
| Current state | ProjectPhase exists; roll-up missing |
| Phase 16 target | Implement |
| Classification | `MUST_IMPLEMENT_IN_PHASE_16` |

Rules:

```text
1. Phase must belong to project.
2. Roll-up includes tasks in phase.
3. Archived phase excluded by default unless options include archived.
4. Cancelled/archived tasks excluded by default.
5. Phase roll-up does not create finance phase budget.
```

---

## 8.7 EST-007 — Project estimate summary

| Item | Value |
|---|---|
| Future capability | Summary estimate at project level |
| Current state | Missing/unknown |
| Phase 16 target | Implement |
| Classification | `MUST_IMPLEMENT_IN_PHASE_16` |

Summary fields:

```text
totalTaskCount
includedTaskCount
excludedTaskCount
unestimatedTaskCount
unresolvedRateTaskCount
totalEstimateHours
totalLaborCost
totalBillingPreview
averageCostRate
averageBillingRate
currencyCode
rateResolutionFailureCount
warningCount
```

Rules:

```text
1. Summary comes from snapshots and roll-ups.
2. Summary is not project budget.
3. Summary is not gross margin.
4. Summary currency handling must be clear.
```

If multiple currencies exist:

```text
Either reject mixed currency estimate or group totals by currency.
```

Recommended Phase 16:

```text
Group by currency or require single currency per run.
```

---

## 8.8 EST-008 — Estimate assumptions

| Item | Value |
|---|---|
| Future capability | Store calculation assumptions for reproducibility |
| Current state | Missing/unknown |
| Phase 16 target | Implement as JSON fields in EstimationRun |
| Classification | `MUST_IMPLEMENT_IN_PHASE_16` |

Assumptions:

```text
includedTaskStatuses
includeArchivedTasks
includeCancelledTasks
rateTargetDateStrategy
currencyPolicy
rateCardSelectionStrategy
costRoleResolutionStrategy
useBillingPreview
roundingMode
```

Rules:

```text
1. Assumptions stored with run.
2. Assumptions do not change after run completed.
3. Re-run with different assumptions creates a new run.
```

---

## 8.9 EST-009 — Estimate comparison

| Item | Value |
|---|---|
| Future capability | Compare current run to previous run |
| Current state | Missing |
| Phase 16 target | Optional/defer |
| Classification | `DEFERRED_TO_PHASE_22_REPORTING` unless needed |

Future comparison:

```text
run A vs run B
hours delta
cost delta
role mix delta
phase delta
WBS delta
```

Do not block Phase 16 on comparison.

---

## 8.10 EST-010 — Estimate approval

| Item | Value |
|---|---|
| Future capability | Approve an estimate as basis for finance/quote/baseline |
| Current state | Missing |
| Phase 16 target | Defer |
| Classification | `DEFERRED_TO_PHASE_17_FINANCE` and `PHASE_19_BASELINE_CHANGE_REQUEST` |

Reason:

```text
Approval changes estimate into finance/baseline governance.
```

Phase 16 run can be marked current, but not approved budget.

---

## 8.11 EST-011 — AI estimate suggestion

| Item | Value |
|---|---|
| Future capability | AI suggests task estimates or role mix |
| Current state | Missing |
| Phase 16 target | Defer |
| Classification | `DEFERRED_TO_PHASE_21_AI_ASSISTED_PLANNING` |

Rules future:

```text
1. AI suggestion is draft.
2. Human accepts before Task.estimateHours changes.
3. AI output stores prompt/model/execution log.
4. Rate/finance permissions respected.
```

---

# 9. Entity model TO-BE

If current schema differs, agent must map actual fields and document gaps.

---

## 9.1 EstimationRun — `estimation_run`

Required fields:

```text
id UUID PK
project_id UUID NOT NULL
workspace_id UUID NOT NULL
schedule_run_id UUID NULL
name VARCHAR(255) NULL
description TEXT NULL
status VARCHAR(50) NOT NULL
calculation_mode VARCHAR(50) NOT NULL
rate_target_date_strategy VARCHAR(100) NOT NULL
currency_policy VARCHAR(100) NOT NULL
assumptions_json JSONB NULL
result_summary_json JSONB NULL
error_code VARCHAR(150) NULL
error_message TEXT NULL
started_at TIMESTAMP NULL
completed_at TIMESTAMP NULL
created_at TIMESTAMP NOT NULL
created_by UUID NULL
trace_id VARCHAR(100) NULL
version INT
```

Status:

```text
PENDING
RUNNING
COMPLETED
FAILED
CANCELLED
```

Calculation mode:

```text
TASK_ESTIMATE_ONLY
TASK_ESTIMATE_WITH_RATE
SCHEDULED_WORK_WITH_RATE
```

Phase 16 recommended default:

```text
TASK_ESTIMATE_WITH_RATE
```

---

## 9.2 TaskEstimateSnapshot — `estimation_task_snapshot`

Required fields:

```text
id UUID PK
estimation_run_id UUID NOT NULL
project_id UUID NOT NULL
project_phase_id UUID NULL
wbs_node_id UUID NULL
task_id UUID NOT NULL
task_code VARCHAR(100) NULL
task_title VARCHAR(255) NOT NULL
assignee_user_id UUID NULL
workspace_member_id UUID NULL
cost_role_id UUID NULL
cost_role_code VARCHAR(100) NULL
estimate_hours DECIMAL(12,2) NOT NULL
rate_target_date DATE NOT NULL

rate_card_id UUID NULL
rate_card_version_id UUID NULL
rate_card_line_id UUID NULL
base_cost_rate DECIMAL(18,4) NULL
adjusted_cost_rate DECIMAL(18,4) NULL
base_billing_rate DECIMAL(18,4) NULL
adjusted_billing_rate DECIMAL(18,4) NULL
currency_code VARCHAR(10) NULL
inflation_policy_id UUID NULL
inflation_percent DECIMAL(8,4) NULL
years_forward DECIMAL(10,4) NULL
resolved_at TIMESTAMP NULL

estimated_labor_cost DECIMAL(18,4) NULL
estimated_billing_preview DECIMAL(18,4) NULL
status VARCHAR(50) NOT NULL
issue_code VARCHAR(150) NULL
issue_message TEXT NULL
created_at TIMESTAMP NOT NULL
```

Status:

```text
CALCULATED
RATE_UNRESOLVED
ROLE_UNRESOLVED
TASK_UNESTIMATED
EXCLUDED
```

---

## 9.3 WbsEstimateRollup — `estimation_wbs_rollup`

Required fields:

```text
id UUID PK
estimation_run_id UUID NOT NULL
project_id UUID NOT NULL
wbs_node_id UUID NOT NULL
parent_wbs_node_id UUID NULL
depth INT NOT NULL
task_count INT NOT NULL DEFAULT 0
included_task_count INT NOT NULL DEFAULT 0
unresolved_task_count INT NOT NULL DEFAULT 0
total_estimate_hours DECIMAL(12,2) NOT NULL DEFAULT 0
total_labor_cost DECIMAL(18,4) NULL
total_billing_preview DECIMAL(18,4) NULL
currency_code VARCHAR(10) NULL
created_at TIMESTAMP NOT NULL
```

---

## 9.4 PhaseEstimateRollup — `estimation_phase_rollup`

Required fields:

```text
id UUID PK
estimation_run_id UUID NOT NULL
project_id UUID NOT NULL
project_phase_id UUID NOT NULL
task_count INT NOT NULL DEFAULT 0
included_task_count INT NOT NULL DEFAULT 0
unresolved_task_count INT NOT NULL DEFAULT 0
total_estimate_hours DECIMAL(12,2) NOT NULL DEFAULT 0
total_labor_cost DECIMAL(18,4) NULL
total_billing_preview DECIMAL(18,4) NULL
currency_code VARCHAR(10) NULL
created_at TIMESTAMP NOT NULL
```

---

## 9.5 ProjectEstimateSummary — `estimation_project_summary`

Required fields:

```text
id UUID PK
estimation_run_id UUID NOT NULL UNIQUE
project_id UUID NOT NULL
total_task_count INT NOT NULL DEFAULT 0
included_task_count INT NOT NULL DEFAULT 0
excluded_task_count INT NOT NULL DEFAULT 0
unestimated_task_count INT NOT NULL DEFAULT 0
unresolved_role_task_count INT NOT NULL DEFAULT 0
unresolved_rate_task_count INT NOT NULL DEFAULT 0
total_estimate_hours DECIMAL(12,2) NOT NULL DEFAULT 0
total_labor_cost DECIMAL(18,4) NULL
total_billing_preview DECIMAL(18,4) NULL
average_cost_rate DECIMAL(18,4) NULL
average_billing_rate DECIMAL(18,4) NULL
currency_code VARCHAR(10) NULL
warning_count INT NOT NULL DEFAULT 0
created_at TIMESTAMP NOT NULL
```

---

## 9.6 Project current estimate pointer

Recommended field on Project:

```text
current_estimation_run_id UUID NULL
```

If not added:

```text
Resolve latest completed estimation run by query.
```

Recommended:

```text
MUST_IMPLEMENT_IN_PHASE_16 if simple.
```

---

# 10. API TO-BE list

All APIs use `/api`.

---

## 10.1 Estimation run APIs

```text
POST /api/projects/{projectId}/estimation-runs
GET  /api/projects/{projectId}/estimation-runs
GET  /api/projects/{projectId}/estimation-runs/{estimationRunId}
POST /api/projects/{projectId}/estimation-runs/{estimationRunId}/cancel
POST /api/projects/{projectId}/estimation-runs/{estimationRunId}/mark-current
```

Create request:

```json
{
  "name": "Initial labor estimate",
  "scheduleRunId": "uuid-or-null",
  "calculationMode": "TASK_ESTIMATE_WITH_RATE",
  "rateTargetDateStrategy": "TASK_DUE_DATE_OR_PROJECT_START",
  "currencyPolicy": "SINGLE_CURRENCY_REQUIRED",
  "options": {
    "includeCompletedTasks": false,
    "includeCancelledTasks": false,
    "includeArchivedTasks": false,
    "useBillingPreview": true,
    "markAsCurrent": true
  }
}
```

Rules:

```text
1. Project must not be archived.
2. Date/rate strategy required.
3. Same idempotency key must not create duplicate run.
```

---

## 10.2 Current estimate APIs

```text
GET /api/projects/{projectId}/estimation/current
GET /api/projects/{projectId}/estimation/current/tasks
GET /api/projects/{projectId}/estimation/current/wbs-rollups
GET /api/projects/{projectId}/estimation/current/phase-rollups
GET /api/projects/{projectId}/estimation/current/summary
```

Filters:

```text
phaseId
wbsNodeId
assigneeUserId
costRoleId
status
currencyCode
```

---

## 10.3 Specific run detail APIs

```text
GET /api/projects/{projectId}/estimation-runs/{estimationRunId}/tasks
GET /api/projects/{projectId}/estimation-runs/{estimationRunId}/wbs-rollups
GET /api/projects/{projectId}/estimation-runs/{estimationRunId}/phase-rollups
GET /api/projects/{projectId}/estimation-runs/{estimationRunId}/summary
```

---

## 10.4 Estimate preview APIs

Optional:

```text
POST /api/projects/{projectId}/estimation/preview-task
POST /api/projects/{projectId}/estimation/preview-rate-impact
```

Classification:

```text
DEFERRED_TO_PHASE_22_REPORTING unless needed.
```

---

# 11. Authorization requirements

Required IAM authorities:

```text
ESTIMATION_VIEW
ESTIMATION_RUN_CREATE
ESTIMATION_RUN_CANCEL
ESTIMATION_MARK_CURRENT
ESTIMATION_TASK_VIEW
ESTIMATION_WBS_VIEW
ESTIMATION_PHASE_VIEW
ESTIMATION_SUMMARY_VIEW
ESTIMATION_RATE_DETAIL_VIEW
```

Simplified mapping allowed:

```text
ESTIMATION_VIEW → PROJECT_VIEW
ESTIMATION_RUN_CREATE → PROJECT_UPDATE or PROJECT_ESTIMATION_CREATE
ESTIMATION_RATE_DETAIL_VIEW → RATE_RESOLUTION_VIEW
```

But completion file must document mapping.

Rules:

```text
1. All estimation APIs require authenticated user.
2. User must have active workspace access.
3. User must have PROJECT_VIEW and estimation permission.
4. Creating estimation run requires ESTIMATION_RUN_CREATE.
5. Viewing rate detail requires rate/estimation detail permission.
6. Estimation response must not expose salary/payroll.
7. Future finance/margin fields require finance permissions, not estimation permission.
```

---

# 12. Estimation algorithm TO-BE

## 12.1 High-level algorithm

```text
1. Validate project and authorization.
2. Load project tasks according to options.
3. Load phases and WBS tree.
4. For each task:
   a. Copy task estimateHours.
   b. Resolve cost role.
   c. Determine rate target date.
   d. Resolve rate snapshot using Phase 15 RateResolutionService.
   e. Calculate estimated labor cost.
   f. Calculate optional billing preview.
   g. Create TaskEstimateSnapshot.
5. Build WBS subtree roll-ups.
6. Build phase roll-ups.
7. Build project estimate summary.
8. Store result summary on EstimationRun.
9. Mark run COMPLETED or FAILED.
10. Optionally mark as current estimate.
11. Emit events.
```

---

## 12.2 Task inclusion rules

Default include:

```text
TODO
IN_PROGRESS
BLOCKED
```

Optional include:

```text
COMPLETED
```

Default exclude:

```text
CANCELLED
ARCHIVED
```

Rules:

```text
1. Excluded tasks counted in summary.
2. Completed task inclusion is configurable.
3. Archived tasks excluded unless explicitly included.
```

---

## 12.3 Rate target date strategy

Supported strategies:

```text
PROJECT_PLANNED_START
TASK_DUE_DATE
TASK_SCHEDULED_START
TASK_SCHEDULED_FINISH
RUN_DATE
CUSTOM_DATE
TASK_DUE_DATE_OR_PROJECT_START
```

Phase 16 recommended:

```text
TASK_DUE_DATE_OR_PROJECT_START
```

Rules:

```text
1. If strategy uses schedule date, scheduleRunId must be supplied or current schedule exists.
2. If date cannot be resolved, fallback rule must be explicit.
3. Resolved date stored in snapshot.
```

---

## 12.4 Cost role resolution strategy

Recommended:

```text
Task explicit costRole → Member default costRole → Workspace fallback costRole → error
```

Rules:

```text
1. No role means snapshot status ROLE_UNRESOLVED.
2. Role unresolved task still counted as unresolved.
3. EstimationRun may complete with warnings if unresolved roles exist.
4. Product may choose fail-fast if any role unresolved.
```

Recommended Phase 16:

```text
Complete with warnings.
```

Reason:

```text
Users can see which tasks need role mapping.
```

---

## 12.5 Rate resolution strategy

Rules:

```text
1. Use RateResolutionService from Phase 15.
2. Store all snapshot fields.
3. If no rate found, snapshot status RATE_UNRESOLVED.
4. Run can complete with warnings or fail according to option.
5. Do not invent default rate.
6. Do not use draft rate card version.
```

Recommended:

```text
Complete with warnings.
```

---

## 12.6 Currency policy

Supported policies:

```text
SINGLE_CURRENCY_REQUIRED
GROUP_BY_CURRENCY
CONVERT_TO_PROJECT_CURRENCY future
```

Phase 16 recommended:

```text
SINGLE_CURRENCY_REQUIRED or GROUP_BY_CURRENCY.
```

Do not implement currency conversion unless Phase 17 exchange rate exists.

Rules:

```text
1. If SINGLE_CURRENCY_REQUIRED and multiple currencies appear, run fails or warnings based on policy.
2. If GROUP_BY_CURRENCY, summaries may be per currency.
3. Do not convert currency without exchange rate snapshot.
```

---

# 13. WBS roll-up algorithm

Algorithm:

```text
1. Build WBS tree.
2. Attach task snapshots to their wbsNodeId.
3. For each WBS node bottom-up:
   total = direct task snapshots + descendant WBS totals
4. Store WbsEstimateRollup per node.
5. Tasks without WBS go to project unassigned bucket.
```

Rules:

```text
1. WBS cycle should not exist, but validate defensively.
2. Archived WBS excluded by default.
3. Roll-up must not double count tasks.
4. Task linked to one WBS counts once.
```

---

# 14. Phase roll-up algorithm

Algorithm:

```text
1. Group task snapshots by projectPhaseId.
2. Sum hours/cost/billing preview.
3. Store PhaseEstimateRollup per phase.
4. Tasks without valid phase should be error because Phase 09 requires phase.
```

Rules:

```text
1. Phase roll-up independent from WBS roll-up.
2. Same task contributes to one phase only.
3. Phase roll-up is not phase budget.
```

---

# 15. Project summary algorithm

Algorithm:

```text
1. Sum included TaskEstimateSnapshots.
2. Count excluded/unresolved/unestimated tasks.
3. Calculate average blended rates.
4. Store ProjectEstimateSummary.
```

Rules:

```text
1. Summary totalLaborCost from task snapshots only.
2. Summary excludes overhead/custom/vendor costs.
3. Summary is labor estimate, not project budget.
4. Summary must state currency policy.
```

---

# 16. Integration with Phase 17 Finance

Phase 17 will consume Phase 16 outputs.

Phase 17 will add:

```text
custom phase costs
vendor costs
contingency
overhead
revenue split
gross margin
profit before tax
target margin
finance scenario
```

Phase 16 must provide:

```text
stable labor estimate snapshot
task/WBS/phase/project roll-up
rate snapshot
unresolved role/rate issues
```

Do not add finance formulas here.

---

# 17. Integration with Phase 18 Quote

Phase 18 may use:

```text
estimatedBillingPreview
rate snapshots
task/phase roll-ups
target margin
contract value solver
```

But Phase 16 must not call this a quote.

---

# 18. Integration with Phase 19 Baseline / Change Request

Phase 19 may snapshot:

```text
EstimationRun
TaskEstimateSnapshot
WbsEstimateRollup
PhaseEstimateRollup
ProjectEstimateSummary
```

Phase 16 itself does not approve or baseline.

---

# 19. Integration with AI Agent

Phase 16 does not implement AI estimate recommendation.

Future Phase 21 can:

```text
suggest missing estimates
suggest cost roles
explain variance
recommend estimate changes
```

Rules future:

```text
1. AI suggestions must be reviewed by human.
2. Applying AI suggestion calls Task update / role mapping actions.
3. AI cannot expose rates without permission.
```

---

# 20. Event Registry integration

Recommended source system:

```text
SCOPERY_ESTIMATION
```

Required events:

```text
ESTIMATION_RUN_CREATED
ESTIMATION_RUN_STARTED
ESTIMATION_RUN_COMPLETED
ESTIMATION_RUN_FAILED
ESTIMATION_RUN_CANCELLED
ESTIMATION_RUN_MARKED_CURRENT

TASK_ESTIMATE_SNAPSHOT_CREATED
TASK_ESTIMATE_RATE_UNRESOLVED
TASK_ESTIMATE_ROLE_UNRESOLVED

WBS_ESTIMATE_ROLLED_UP
PHASE_ESTIMATE_ROLLED_UP
PROJECT_ESTIMATE_SUMMARY_CREATED

ESTIMATION_RATE_SNAPSHOT_USED
ESTIMATION_WARNING_CREATED
```

Standard variables:

```text
actor.userId
organization.id
workspace.id
project.id
estimationRun.id
task.id
wbsNode.id
phase.id
costRole.id
costRole.code
rateCard.id
rateCardVersion.id
currency.code
estimateHours
estimatedLaborCost
estimatedBillingPreview
occurredAt
traceId
```

---

# 21. Notification integration

Phase 16 should not spam notifications.

Optional notifications later:

```text
ESTIMATION_RUN_FAILED_ADMIN_EMAIL
ESTIMATION_MISSING_RATE_WARNING_NOTIFICATION
ESTIMATION_MISSING_ROLE_WARNING_NOTIFICATION
```

Recommended:

```text
Seed events only.
Notification rules deferred to Phase 20/22.
```

---

# 22. Platform audit/outbox/idempotency integration

## 22.1 Activity log actions

```text
ESTIMATION_RUN_CREATED
ESTIMATION_RUN_COMPLETED
ESTIMATION_RUN_FAILED
ESTIMATION_RUN_MARKED_CURRENT
TASK_ESTIMATE_RATE_UNRESOLVED
TASK_ESTIMATE_ROLE_UNRESOLVED
```

## 22.2 Audit-sensitive actions

Audit:

```text
EstimationRun marked current
Estimation run generated with rate details
Rate snapshot used
Unresolved rate/role warnings
```

Reason:

```text
Estimates influence future finance, quote, and baseline.
```

## 22.3 Idempotency

Recommended for:

```text
POST /api/projects/{projectId}/estimation-runs
POST /api/projects/{projectId}/estimation-runs/{runId}/mark-current
```

Same idempotency key should not create duplicate run.

## 22.4 Outbox

Estimation events should use platform outbox if available.

---

# 23. Business rules master

## 23.1 EstimationRun rules

```text
EST-RUN-001 Project must exist.
EST-RUN-002 Project must not be ARCHIVED.
EST-RUN-003 User must have estimation run permission.
EST-RUN-004 Calculation mode required.
EST-RUN-005 Rate target date strategy required.
EST-RUN-006 Currency policy required.
EST-RUN-007 Estimation run does not mutate Task.estimateHours.
EST-RUN-008 Completed run immutable.
EST-RUN-009 Mark current requires permission.
```

## 23.2 Task snapshot rules

```text
EST-TASK-001 Task must belong to project.
EST-TASK-002 Task estimateHours must be > 0.
EST-TASK-003 Snapshot copies task estimateHours.
EST-TASK-004 Snapshot copies rate snapshot.
EST-TASK-005 Snapshot does not change after run.
EST-TASK-006 Snapshot never stores salary.
EST-TASK-007 Unresolved role/rate produces warning status.
```

## 23.3 Cost role rules

```text
EST-ROLE-001 Cost role resolution deterministic.
EST-ROLE-002 IAM role is not cost role.
EST-ROLE-003 Missing role does not invent default unless configured.
EST-ROLE-004 Role mapping changes do not modify past snapshots.
```

## 23.4 Rate rules

```text
EST-RATE-001 Use RateResolutionService.
EST-RATE-002 Use only published rate versions.
EST-RATE-003 Do not use archived rate card for new run.
EST-RATE-004 Do not use draft rate version.
EST-RATE-005 Rate snapshot stored.
EST-RATE-006 No salary exposed.
```

## 23.5 Roll-up rules

```text
EST-WBS-001 WBS roll-up includes descendant tasks.
EST-WBS-002 WBS roll-up does not double count.
EST-WBS-003 Tasks without WBS are included in project unassigned bucket.
EST-PHASE-001 Phase roll-up includes tasks in phase.
EST-PHASE-002 Phase roll-up is not phase budget.
EST-PROJ-001 Project summary is labor estimate only.
EST-PROJ-002 No overhead/custom/vendor/revenue/margin in Phase 16.
```

---

# 24. Error catalog requirements

Exact names follow project convention, but these concepts must exist.

```text
ESTIMATION_RUN_NOT_FOUND
ESTIMATION_RUN_PROJECT_ARCHIVED
ESTIMATION_RUN_INVALID_MODE
ESTIMATION_RUN_INVALID_RATE_DATE_STRATEGY
ESTIMATION_RUN_INVALID_CURRENCY_POLICY
ESTIMATION_RUN_ALREADY_COMPLETED
ESTIMATION_RUN_NOT_CANCELLABLE
ESTIMATION_RUN_FAILED

ESTIMATION_TASK_NOT_FOUND
ESTIMATION_TASK_INVALID_ESTIMATE
ESTIMATION_TASK_EXCLUDED
ESTIMATION_TASK_COST_ROLE_NOT_RESOLVED
ESTIMATION_TASK_RATE_NOT_RESOLVED

ESTIMATION_WBS_CYCLE_DETECTED
ESTIMATION_WBS_ROLLUP_FAILED
ESTIMATION_PHASE_ROLLUP_FAILED
ESTIMATION_PROJECT_SUMMARY_FAILED

ESTIMATION_RATE_CARD_NOT_FOUND
ESTIMATION_RATE_VERSION_NOT_FOUND
ESTIMATION_RATE_LINE_NOT_FOUND
ESTIMATION_RATE_UNRESOLVED
ESTIMATION_MIXED_CURRENCY_NOT_ALLOWED

ESTIMATION_ACCESS_DENIED
ESTIMATION_RATE_DETAIL_ACCESS_DENIED
```

---

# 25. Required tests

Phase 16 is incomplete without tests.

---

## 25.1 EstimationRun tests

```text
createEstimationRun_valid_success
createEstimationRun_archivedProject_rejected
createEstimationRun_invalidMode_rejected
createEstimationRun_invalidCurrencyPolicy_rejected
createEstimationRun_withoutPermission_forbidden
createEstimationRun_idempotency_sameKey_noDuplicateRun
estimationRun_success_marksCompleted
estimationRun_failure_marksFailed
markCurrent_valid_success
markCurrent_withoutPermission_forbidden
```

## 25.2 Task snapshot tests

```text
taskSnapshot_valid_calculatesLaborCost
taskSnapshot_billingPreview_calculatesBillingPreview
taskSnapshot_copiesRateSnapshotFields
taskSnapshot_doesNotExposeSalary
taskSnapshot_taskEstimateCopied
taskSnapshot_pastRunUnaffectedByTaskEstimateChange
taskSnapshot_pastRunUnaffectedByRateChange
taskWithoutCostRole_roleUnresolvedWarning
taskWithoutRate_rateUnresolvedWarning
```

## 25.3 Cost role/rate resolution tests

```text
resolveCostRole_taskExplicitRole_wins
resolveCostRole_memberDefaultRole_used
resolveCostRole_missingRole_warning
resolveRate_usesPublishedVersion
resolveRate_draftVersionIgnored
resolveRate_archivedCardIgnoredForNewRun
resolveRate_inflationApplied
resolveRate_targetDateStrategy_taskDueDate
resolveRate_targetDateStrategy_projectStartFallback
```

## 25.4 WBS roll-up tests

```text
wbsRollup_leafNode_success
wbsRollup_parentIncludesDescendants
wbsRollup_noDoubleCount
wbsRollup_taskWithoutWbs_excludedFromWbsButInProjectUnassigned
wbsRollup_archivedWbsExcludedByDefault
wbsRollup_cancelledTaskExcludedByDefault
```

## 25.5 Phase roll-up tests

```text
phaseRollup_valid_success
phaseRollup_includesOnlyPhaseTasks
phaseRollup_cancelledTaskExcludedByDefault
phaseRollup_isNotBudget
```

## 25.6 Project summary tests

```text
projectSummary_validTotals
projectSummary_countsIncludedExcludedTasks
projectSummary_countsUnresolvedRole
projectSummary_countsUnresolvedRate
projectSummary_averageCostRate
projectSummary_mixedCurrencySinglePolicy_rejectedOrWarning
projectSummary_noMarginNoOverheadNoRevenue
```

## 25.7 Authorization tests

```text
viewEstimation_withoutPermission_forbidden
createEstimationRun_withoutPermission_forbidden
viewRateDetails_withoutRatePermission_hiddenOrForbidden
crossWorkspaceProject_forbidden
```

## 25.8 Seeder/event tests

```text
estimationEventSeeder_firstRun_createsDefinitions
estimationEventSeeder_secondRun_noDuplicates
estimationPermissionSeeder_authoritiesExist
estimationRunCompleted_eventEmitted
estimationRateUnresolved_eventEmitted
```

---

# 26. Manual verification checklist

Completion file must include:

```text
1. Create project with phases, WBS, and tasks.
2. Add estimateHours to tasks.
3. Assign cost roles or member default cost role.
4. Create/publish rate card version.
5. Run estimation.
6. Confirm task snapshots created.
7. Confirm labor cost = estimateHours × adjustedCostRate.
8. Confirm WBS roll-up includes descendant tasks.
9. Confirm phase roll-up totals match tasks in phase.
10. Confirm project summary totals match task snapshots.
11. Change rate card after run and confirm previous run unchanged.
12. Change task estimate after run and confirm previous run unchanged.
13. Confirm no salary fields returned.
14. Confirm no finance/margin/quote records created.
15. Confirm events/activity/audit created.
```

---

# 27. Acceptance criteria

Phase 16 is accepted only if:

```text
1. Current estimation capability is classified against TO-BE.
2. EstimationRun implemented/tested.
3. TaskEstimateSnapshot implemented/tested.
4. Rate snapshot persisted/tested.
5. Cost role resolution deterministic/tested.
6. Rate resolution uses Phase 15 service/tested.
7. WBS roll-up implemented/tested.
8. Phase roll-up implemented/tested.
9. Project summary implemented/tested.
10. Historical estimate runs do not change after task/rate changes.
11. Authorization implemented/tested.
12. Events seeded idempotently.
13. Activity/audit/outbox follows Phase 04.
14. No salary/payroll exposed.
15. Phase 16 does not falsely claim budget/margin/quote/baseline/timesheet.
16. mvn compile passes.
17. mvn test passes.
18. Completion file exists and includes gap matrix.
```

Agent must not mark complete if:

```text
mvn test fails
estimation uses draft rate version
estimation uses archived rate card for new run
rate snapshot not persisted
past runs change after rate/task update
WBS roll-up double counts
salary/payroll fields exposed
project budget/margin/quote is claimed implemented
```

---

# 28. Required phase completion file

Agent must create:

```text
docs/phase-complete/PHASE_16_ESTIMATION_ROLLUP_TO_BE_COMPLETE.md
```

Required sections:

```text
# Phase 16 — Estimation Roll-up TO-BE Complete

## 1. Summary
## 2. Source Inputs Reviewed
## 3. Current vs TO-BE Classification Matrix
## 4. Implemented in Current BE
## 5. Implemented / Hardened in This Phase
## 6. Seed-only Items Added
## 7. Deferred Items and Target Phase
## 8. Estimation Boundary Decision
## 9. Entity Mapping
## 10. API Changes
## 11. Estimation Algorithm
## 12. Cost Role Resolution Strategy
## 13. Rate Resolution / Snapshot Strategy
## 14. WBS Roll-up Strategy
## 15. Phase Roll-up Strategy
## 16. Project Summary Strategy
## 17. Currency Policy
## 18. Authorization Matrix
## 19. Event Registry Seeder Matrix
## 20. Activity / Audit / Outbox Notes
## 21. Idempotency Strategy
## 22. Tests Added
## 23. Commands Run
## 24. Test Results
## 25. Manual Verification
## 26. Assumptions
## 27. Deviations From Prompt
## 28. Known Risks
## 29. Future Phases That Must Return to Estimation
```

---

# 29. Future phases that must return to Estimation

## 29.1 Phase 17 — Project Budget / Margin

Must consume:

```text
EstimationRun
TaskEstimateSnapshot
WbsEstimateRollup
PhaseEstimateRollup
ProjectEstimateSummary
```

Must add:

```text
custom phase costs
vendor costs
overhead
contingency
revenue split
gross margin
profit before tax
finance scenario
```

## 29.2 Phase 18 — Quote

Must consume:

```text
estimated billing preview
rate snapshots
target margin
contract value solver
quote line generation
```

Quote must store commercial snapshots.

## 29.3 Phase 19 — Baseline / Change Request

Must snapshot approved estimation run.

After baseline, estimate changes may require ChangeRequest.

## 29.4 Phase 20 — Project Notifications

May notify:

```text
estimation run failed
missing role/rate
estimate changed after baseline
```

## 29.5 Phase 21 — AI-assisted Planning

AI may suggest:

```text
estimate hours
cost role mapping
role mix
scope decomposition
```

Human approval required.

## 29.6 Phase 22 — Reporting

Reports:

```text
estimate by WBS
estimate by phase
estimate by role
estimate variance between runs
unresolved rate/role report
```

## 29.7 Phase 23 — Core Hardening

Review:

```text
roll-up performance
large WBS tree
BigDecimal precision
currency policy
rate snapshot integrity
authorization
```

## 29.8 Phase 37 — Time / Attendance / Expense

Compare:

```text
estimated hours vs actual hours
estimated cost vs actual cost
```

Actuals are not Phase 16.

---

# 30. Agent anti-bịa rules

The agent must not:

```text
1. Claim Phase 16 implements project budget.
2. Claim Phase 16 implements gross margin.
3. Claim Phase 16 implements profit before tax.
4. Claim Phase 16 implements quote price.
5. Claim Phase 16 implements baseline approval.
6. Claim Phase 16 implements actual cost/timesheet.
7. Store or expose salary.
8. Use draft rate card version.
9. Let historical estimate run change after rate update.
10. Double count WBS tasks.
11. Convert currency without exchange-rate snapshot.
12. Hide unresolved cost role/rate warnings.
```

---

# 31. Prompt to give coding agent

```text
You are implementing Phase 16 — TO-BE Estimation Roll-up, Task Labor Estimate, WBS/Phase/Project Effort Summary & Rate Snapshot Foundation.

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
- Current backend code, migrations, tests

Your task:
1. Compare current estimation capability against this Phase 16 TO-BE spec.
2. Classify each capability as CURRENTLY_IMPLEMENTED, PARTIALLY_IMPLEMENTED, MUST_IMPLEMENT_IN_PHASE_16, SEED_ONLY_IN_PHASE_16, DEFERRED_TO_PHASE_XX, or NOT_IN_SCOPE_FOR_BACKEND_NOW.
3. Implement only Phase 16 required items.
4. Implement EstimationRun, TaskEstimateSnapshot, WbsEstimateRollup, PhaseEstimateRollup, ProjectEstimateSummary.
5. Implement deterministic cost role resolution.
6. Use Phase 15 RateResolutionService and persist rate snapshots.
7. Implement WBS/phase/project roll-up.
8. Add estimation permissions and event definitions.
9. Add tests listed in this spec.
10. Run mvn compile and mvn test.
11. Create docs/phase-complete/PHASE_16_ESTIMATION_ROLLUP_TO_BE_COMPLETE.md with full gap matrix.

Do not implement or claim project budget, gross margin, profit before tax, overhead, revenue split, quote, baseline approval, timesheet actuals, payroll, or salary in this phase.
```

---

# 32. Quick tracking matrix

| Capability | Current backend | Phase 16 action | Later phase |
|---|---|---|---|
| EstimationRun | Missing/unknown | Must implement | — |
| TaskEstimateSnapshot | Missing/unknown | Must implement | — |
| Rate snapshot persistence | Missing/unknown | Must implement | Phase 17/18 consume |
| Cost role resolution | Missing/partial | Must implement | Phase 21 can suggest |
| Task labor cost preview | Missing | Must implement | Phase 17 consumes |
| Billing preview | Missing | Optional | Phase 18 consumes |
| WBS roll-up | Missing | Must implement | — |
| Phase roll-up | Missing | Must implement | Phase 17 consumes |
| Project estimate summary | Missing | Must implement | Phase 17 consumes |
| Estimate comparison | Missing | Defer | Phase 22 |
| Estimate approval | Missing | Defer | Phase 17/19 |
| Finance scenario | Missing | Defer | Phase 17 |
| Gross margin/PBT | Missing | Defer | Phase 17 |
| Quote price | Missing | Defer | Phase 18 |
| Baseline snapshot | Missing | Defer | Phase 19 |
| Actual cost | Missing | Defer | Phase 37 |
| AI estimate suggestion | Missing | Defer | Phase 21 |

---

# 33. Final principle

Phase 16 is not complete when "task hours can be summed."

Phase 16 is complete when Scopery can produce a stable labor estimate:

```text
Task estimate hours
+ cost role
+ published rate snapshot
= task labor estimate

Task snapshots
+ WBS tree
+ phase grouping
= WBS / phase / project roll-up
```

Estimation is not finance.

Labor estimate is not project budget.

Billing preview is not quote.

Snapshot protects history.
