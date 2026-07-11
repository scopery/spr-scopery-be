# PHASE 13 — TO-BE Task Scheduling Engine, Capacity-based Forecasting, Due-date Risk & Dependency-aware Planning

> Project: Scopery Backend  
> Phase: 13  
> Document type: TO-BE implementation-grade specification  
> Status: Planning / Gap-driven implementation spec  
> Depends on: Phase 00 — Master Backend Roadmap, Phase 01 — API Path / Security Baseline, Phase 02 — IAM Core, Phase 03 — Workspace Core, Phase 04 — Platform Audit / Outbox / Idempotency, Phase 05 — Event Registry, Phase 06 — Notification Engine, Phase 07 — AI Agent Platform, Phase 08 — Knowledge / Document Type Catalog, Phase 09 — Project Core / Phase / WBS / Task, Phase 10 — Project Authorization, Phase 11 — Project Template / Phase Catalog, Phase 12 — Resource Calendar / Capacity  
> API base: `/api`  
> Primary module: `modules/project/scheduling` or `modules/scheduling` depending on repo architecture  
> Related modules: `project`, `resourcecapacity`, `workspace`, `iam`, `eventregistry`, `notification`, future `gantt`, `finance`, `quote`, `baseline`, `reporting`, `aiagent`  
> Important rule: This file is **not an as-is document**. It defines the TO-BE scheduling engine. Phase 13 does not implement Gantt visualization, cost/margin, quote, baseline/change request, or actual timesheet tracking.

---

# 0. Purpose of this file

Phase 13 creates the first real planning intelligence layer.

Previous phases established:

```text
Phase 09:
- Project
- ProjectPhase
- WBS
- Task
- Task.estimateHours
- Task.dueDate
- Task.assignee/inCharge
- TaskDependency

Phase 12:
- Working calendar
- Calendar exceptions
- User capacity profile
- Focus factor
- Project allocation %
- Capacity calculation
```

Phase 13 answers:

```text
Given a task estimate and assignee capacity, when can this task start?
Given working calendar and allocation, when can this task finish?
Given task dependencies, what is the earliest feasible start?
Given due date, is there enough capacity before deadline?
Which tasks are at risk?
Which assignees are overloaded by scheduled task hours?
How should task forecast be persisted for Gantt and reporting later?
```

Phase 13 is **scheduling computation**.

It is not:

```text
Gantt UI
Gantt drag/drop
Visual timeline
Cost calculation
Finance/margin
Quote generation
Baseline approval
Change request workflow
Timesheet actuals
AI autonomous planning
```

---

# 1. Source inputs

Before coding Phase 13, the agent must read:

```text
1. Current backend codebase
2. Phase 09 Project Core TO-BE spec and implementation
3. Phase 10 Project Authorization TO-BE spec and implementation
4. Phase 11 Project Template TO-BE spec and implementation
5. Phase 12 Resource Calendar / Capacity TO-BE spec and implementation
6. Phase 04 Platform Audit / Outbox / Idempotency spec
7. Phase 05 Event Registry spec
8. Phase 06 Notification spec
9. Existing project task/dependency actions
10. Existing capacity calculation service
11. Existing IAM/project authorization service
12. Current BE feature/entity/business-rule inventory
13. Dynamic Work OS feature catalog
```

The agent must not implement Phase 13 from assumptions only.

---

# 2. Current expected backend state

After Phase 12, the backend should have:

```text
Task.estimateHours
Task.dueDate
Task assignee/inCharge user
TaskDependency
WorkingCalendar
CalendarDayRule
CalendarException
UserCapacityProfile
ProjectResourceAllocation
Capacity calculation service
```

Likely missing:

```text
ScheduleRun
TaskSchedule
ScheduledDailyWork
Dependency-aware task ordering
Estimated start date
Estimated finish date
Due date capacity gap
Task schedule risk status
Scheduling issue
Schedule recalculation API
Schedule locking/versioning
```

Phase 13 implements these.

---

# 3. Phase 13 target statement

Phase 13 must deliver a future-ready scheduling engine:

```text
1. Scheduling run can be created for a project.
2. Scheduling run reads project tasks, dependencies, assignees, calendars, focus factors, and project allocations.
3. Engine calculates estimatedStartDate and estimatedFinishDate for schedulable tasks.
4. Engine creates scheduled daily work allocation per task/user/date.
5. Engine respects working days, holidays, exceptions, focus factor, and allocation %.
6. Engine respects task dependencies for earliest start.
7. Engine calculates due date capacity gap.
8. Engine flags schedule risk and over-capacity issues.
9. Engine persists schedule result for Gantt/reporting.
10. Scheduling changes are authorized, evented, audited, and testable.
11. Engine does not calculate cost, margin, quote, baseline, or Gantt visualization.
```

---

# 4. Core scheduling definitions

## 4.1 Estimate hours

From Phase 09:

```text
Task.estimateHours
```

Meaning:

```text
Effort required to complete task.
```

Not duration.

## 4.2 Duration

Computed by scheduling engine:

```text
Duration = number of working calendar days needed to fit estimate hours into available daily capacity.
```

## 4.3 Due date

From Phase 09:

```text
Task.dueDate
```

Meaning:

```text
Deadline / commitment date.
```

Not guaranteed finish date.

## 4.4 Estimated finish date

Computed by Phase 13:

```text
Date when scheduled effort is expected to finish under current capacity/dependency conditions.
```

## 4.5 Daily scheduled work

Persisted plan:

```text
User X works N planned hours on Task Y on Date D.
```

This is still planned/forecast, not actual timesheet.

---

# 5. Formula foundation

## 5.1 Effective daily capacity

From Phase 12:

```text
FocusedCapacity(date) =
CalendarWorkingHours(date) × FocusFactor
```

For a project:

```text
ProjectAllocatedCapacity(date) =
FocusedCapacity(date) × ProjectAllocationPercent(date)
```

Phase 13 extends with already scheduled hours:

```text
RemainingProjectCapacity(date, user, project) =
ProjectAllocatedCapacity(date, user, project) − AlreadyScheduledTaskHours(date, user, project)
```

## 5.2 Planned duration

```text
PlannedDurationDays =
number of working days required to allocate Task.estimateHours into RemainingProjectCapacity
```

This is computed by iterative calendar allocation, not simply:

```text
estimateHours / dailyCapacity
```

because holidays/exceptions and changing allocation matter.

## 5.3 Due date capacity gap

```text
AvailableCapacityBeforeDueDate =
sum(RemainingProjectCapacity(date) from earliestStartDate to dueDate)
```

```text
DueDateCapacityGap =
max(0, Task.estimateHours − AvailableCapacityBeforeDueDate)
```

If gap > 0:

```text
Task is at risk.
```

## 5.4 Dependency earliest start

For FINISH_TO_START:

```text
earliestStartDate(successor) =
max(successor manual earliest date, predecessor estimatedFinishDate + lagDays)
```

For Phase 13 minimum:

```text
FINISH_TO_START required.
Other dependency types can be stored but not scheduled unless implemented.
```

---

# 6. Phase 13 scope decision

## 6.1 Must implement now

```text
ScheduleRun
TaskSchedule
ScheduledDailyWork
SchedulingIssue
Schedule recalculation service
Dependency-aware scheduling for FINISH_TO_START
Capacity-aware effort placement
Estimated start/finish calculation
Due date capacity gap
Task risk status
Over-capacity detection from scheduled work
Schedule APIs
Scheduling permissions
Scheduling events
Tests
Completion report
```

## 6.2 Optional now

```text
Manual task schedule override
Schedule run comparison
Partial schedule recalculation
Schedule dry-run
Schedule versioning
```

Only implement if current architecture supports it.

## 6.3 Must not implement now

```text
Gantt UI/projection
Drag/drop Gantt editing
Critical path visualization
Resource leveling optimization
AI recommendations
Cost/rate/margin
Quote timeline promises
Baseline approval
Actual timesheet progress
```

---

# 7. TO-BE capability matrix

---

## 7.1 SCH-001 — ScheduleRun

| Item | Value |
|---|---|
| Future capability | Create a reproducible scheduling calculation run |
| Current state | Missing/unknown |
| Phase 13 target | Implement |
| Classification | `MUST_IMPLEMENT_IN_PHASE_13` |

Rules:

```text
1. ScheduleRun belongs to one project.
2. Project must exist and not be ARCHIVED.
3. Actor must have TASK_SCHEDULE_RECALCULATE or equivalent.
4. Run status starts PENDING/RUNNING.
5. Run stores input summary and algorithm version.
6. Successful run becomes COMPLETED.
7. Failed run becomes FAILED with reason.
8. A project can have many runs.
9. Current/active schedule result can point to latest completed run.
10. ScheduleRun does not modify Task actual status.
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

## 7.2 SCH-002 — TaskSchedule

| Item | Value |
|---|---|
| Future capability | Persist forecast schedule per task |
| Current state | Missing/unknown |
| Phase 13 target | Implement |
| Classification | `MUST_IMPLEMENT_IN_PHASE_13` |

Rules:

```text
1. TaskSchedule belongs to one ScheduleRun.
2. TaskSchedule belongs to one Task.
3. Task must belong to same project as ScheduleRun.
4. estimatedStartDate nullable if unschedulable.
5. estimatedFinishDate nullable if unschedulable.
6. scheduledHours must equal allocated daily work total.
7. unscheduledHours records remaining effort if not schedulable.
8. riskStatus derived from dueDate and capacity.
9. TaskSchedule does not overwrite Task.dueDate.
10. TaskSchedule does not overwrite Task.status.
```

Risk status:

```text
ON_TRACK
AT_RISK
OVERDUE
BLOCKED_BY_DEPENDENCY
UNSCHEDULED
NO_ASSIGNEE
NO_CAPACITY
```

---

## 7.3 SCH-003 — ScheduledDailyWork

| Item | Value |
|---|---|
| Future capability | Store daily planned work hours by user/task/date |
| Current state | Missing/unknown |
| Phase 13 target | Implement |
| Classification | `MUST_IMPLEMENT_IN_PHASE_13` |

Rules:

```text
1. ScheduledDailyWork belongs to one ScheduleRun.
2. It references TaskSchedule and Task.
3. It references user/workspaceMember.
4. workDate required.
5. plannedHours > 0.
6. plannedHours cannot exceed remaining project capacity for user/date unless override allowed.
7. Non-working days cannot have planned hours.
8. Sum plannedHours for task equals scheduledHours.
9. ScheduledDailyWork is forecast, not actual timesheet.
```

---

## 7.4 SCH-004 — SchedulingIssue

| Item | Value |
|---|---|
| Future capability | Persist scheduling problems and warnings |
| Current state | Missing/unknown |
| Phase 13 target | Implement |
| Classification | `MUST_IMPLEMENT_IN_PHASE_13` |

Issue types:

```text
TASK_NO_ASSIGNEE
TASK_NO_ESTIMATE
TASK_INVALID_ESTIMATE
TASK_NO_CAPACITY
TASK_DUE_DATE_CAPACITY_GAP
TASK_DEPENDENCY_CYCLE
TASK_DEPENDENCY_UNSCHEDULED
RESOURCE_OVER_ALLOCATED
CALENDAR_MISSING
ALLOCATION_MISSING
UNSUPPORTED_DEPENDENCY_TYPE
```

Severity:

```text
INFO
WARNING
ERROR
BLOCKER
```

Rules:

```text
1. ScheduleRun can complete with warnings.
2. BLOCKER issues may fail run if algorithm cannot continue.
3. Issues must reference task/user/date when applicable.
4. Issues are safe for user-facing display.
```

---

## 7.5 SCH-005 — Dependency-aware scheduling

| Item | Value |
|---|---|
| Future capability | Respect task dependency graph when forecasting |
| Current state | TaskDependency exists; scheduling missing |
| Phase 13 target | Implement FINISH_TO_START |
| Classification | `MUST_IMPLEMENT_IN_PHASE_13` |

Rules:

```text
1. Dependency graph must be acyclic before scheduling.
2. Use topological order.
3. FINISH_TO_START successor cannot start before predecessor finishes + lag.
4. Unsupported dependency types create warning/error unless implemented.
5. Dependency on unscheduled predecessor can block successor.
6. Dependency does not change TaskDependency records.
```

Future dependency types:

```text
START_TO_START
FINISH_TO_FINISH
START_TO_FINISH
```

Classification:

```text
DEFERRED_TO_PHASE_14_OR_23 unless needed now.
```

---

## 7.6 SCH-006 — Capacity-aware effort placement

| Item | Value |
|---|---|
| Future capability | Place task effort into user/project calendar capacity |
| Current state | Capacity calculation exists from Phase 12 |
| Phase 13 target | Implement |
| Classification | `MUST_IMPLEMENT_IN_PHASE_13` |

Rules:

```text
1. Only tasks with active assignee/inCharge can be scheduled.
2. User capacity profile must exist or default must resolve.
3. Project allocation must exist or default allocation policy must be defined.
4. Non-working dates skipped.
5. Calendar exceptions applied.
6. Existing scheduled work in same run reduces remaining capacity.
7. Task effort allocated date by date until estimateHours consumed.
8. If capacity exhausted before planning horizon, task becomes partially/fully unscheduled.
```

Default allocation policy if no project allocation exists:

```text
Option A: treat as 0 capacity and create ALLOCATION_MISSING issue.
Option B: use 100% temporary planning allocation.
```

Recommended Phase 13:

```text
Use 0 capacity unless explicit project allocation exists.
```

Reason:

```text
Avoid false optimistic schedule.
```

---

## 7.7 SCH-007 — Due-date risk detection

| Item | Value |
|---|---|
| Future capability | Detect tasks that cannot finish by dueDate |
| Current state | dueDate exists; risk calculation missing |
| Phase 13 target | Implement |
| Classification | `MUST_IMPLEMENT_IN_PHASE_13` |

Rules:

```text
1. If dueDate is null, risk can be ON_TRACK or UNKNOWN.
2. If estimatedFinishDate > dueDate, risk AT_RISK or OVERDUE.
3. If DueDateCapacityGap > 0, risk AT_RISK.
4. If current date > dueDate and task not completed, risk OVERDUE.
5. Issue TASK_DUE_DATE_CAPACITY_GAP created when gap > 0.
```

---

## 7.8 SCH-008 — Schedule recalculation

| Item | Value |
|---|---|
| Future capability | Recompute project schedule when inputs change |
| Current state | Missing/unknown |
| Phase 13 target | Implement basic recalculation API |
| Classification | `MUST_IMPLEMENT_IN_PHASE_13` |

Triggers:

```text
manual API call
task estimate changed
task assignee changed
task dueDate changed
task dependency changed
project allocation changed
calendar exception changed
focus factor changed
```

Phase 13 required:

```text
Manual recalculation API.
```

Automatic recalculation:

```text
Optional or deferred to Phase 20/32 automation.
```

Rules:

```text
1. Recalculation creates new ScheduleRun.
2. Previous completed runs remain historical.
3. Latest completed run can be marked current.
4. Recalculation does not mutate task status.
```

---

## 7.9 SCH-009 — Manual schedule override

| Item | Value |
|---|---|
| Future capability | Planner can manually override schedule start/date/hours |
| Current state | Missing |
| Phase 13 target | Defer unless product requires |
| Classification | `DEFERRED_TO_PHASE_14_GANTT` |

Reason:

```text
Manual override is usually driven by Gantt drag/drop.
```

Future rules:

```text
1. Override requires TASK_SCHEDULE_OVERRIDE.
2. Override records reason.
3. Override can violate capacity only with permission.
4. Override audited.
```

---

## 7.10 SCH-010 — Resource leveling / optimization

| Item | Value |
|---|---|
| Future capability | Automatically adjust assignment/order to resolve overload |
| Current state | Missing |
| Phase 13 target | Defer |
| Classification | `DEFERRED_TO_PHASE_21_AI_ASSISTED_PLANNING` or `POST_23_OPTIMIZATION_BACKLOG` |

Phase 13 scheduling is deterministic forecast, not optimizer.

---

# 8. Entity model TO-BE

If current schema differs, the agent must map actual fields and document gaps.

---

## 8.1 ScheduleRun — `schedule_run`

Required fields:

```text
id UUID PK
project_id UUID NOT NULL
workspace_id UUID NOT NULL
status VARCHAR(50) NOT NULL
algorithm_version VARCHAR(50) NOT NULL
planning_start_date DATE NOT NULL
planning_end_date DATE NOT NULL
input_summary_json JSONB NULL
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

Result summary should include:

```text
totalTasks
scheduledTasks
unscheduledTasks
atRiskTasks
overdueTasks
totalScheduledHours
issueCount
```

---

## 8.2 TaskSchedule — `task_schedule`

Required fields:

```text
id UUID PK
schedule_run_id UUID NOT NULL
project_id UUID NOT NULL
task_id UUID NOT NULL
assignee_user_id UUID NULL
workspace_member_id UUID NULL
estimated_start_date DATE NULL
estimated_finish_date DATE NULL
scheduled_hours DECIMAL(12,2) NOT NULL DEFAULT 0
unscheduled_hours DECIMAL(12,2) NOT NULL DEFAULT 0
due_date DATE NULL
due_date_capacity_gap_hours DECIMAL(12,2) NOT NULL DEFAULT 0
risk_status VARCHAR(50) NOT NULL
schedule_status VARCHAR(50) NOT NULL
created_at TIMESTAMP NOT NULL
updated_at TIMESTAMP NOT NULL
```

Schedule status:

```text
SCHEDULED
PARTIALLY_SCHEDULED
UNSCHEDULED
BLOCKED
SKIPPED
```

Constraint:

```text
unique schedule_run_id + task_id
scheduled_hours >= 0
unscheduled_hours >= 0
```

---

## 8.3 ScheduledDailyWork — `scheduled_daily_work`

Required fields:

```text
id UUID PK
schedule_run_id UUID NOT NULL
task_schedule_id UUID NOT NULL
project_id UUID NOT NULL
task_id UUID NOT NULL
workspace_member_id UUID NOT NULL
user_id UUID NOT NULL
work_date DATE NOT NULL
planned_hours DECIMAL(12,2) NOT NULL
capacity_hours DECIMAL(12,2) NOT NULL
remaining_capacity_after DECIMAL(12,2) NOT NULL
created_at TIMESTAMP NOT NULL
```

Constraint:

```text
planned_hours > 0
unique schedule_run_id + task_id + user_id + work_date optional depending on split rules
```

---

## 8.4 SchedulingIssue — `scheduling_issue`

Required fields:

```text
id UUID PK
schedule_run_id UUID NOT NULL
project_id UUID NOT NULL
task_id UUID NULL
user_id UUID NULL
workspace_member_id UUID NULL
issue_type VARCHAR(100) NOT NULL
severity VARCHAR(50) NOT NULL
message TEXT NOT NULL
issue_date DATE NULL
details_json JSONB NULL
created_at TIMESTAMP NOT NULL
```

Severity:

```text
INFO
WARNING
ERROR
BLOCKER
```

---

## 8.5 Project current schedule pointer

Recommended field on Project:

```text
current_schedule_run_id UUID NULL
```

If not added:

```text
Provide query that resolves latest completed schedule run.
```

Recommended:

```text
MUST_IMPLEMENT_IN_PHASE_13 if simple.
```

---

# 9. API TO-BE list

All APIs use `/api`.

---

## 9.1 Schedule run APIs

```text
POST /api/projects/{projectId}/schedule-runs
GET  /api/projects/{projectId}/schedule-runs
GET  /api/projects/{projectId}/schedule-runs/{scheduleRunId}
POST /api/projects/{projectId}/schedule-runs/{scheduleRunId}/cancel
```

Create request:

```json
{
  "planningStartDate": "2026-08-01",
  "planningEndDate": "2026-09-30",
  "options": {
    "includeCompletedTasks": false,
    "useProjectAllocationsOnly": true,
    "markAsCurrent": true
  }
}
```

Rules:

```text
1. planningStartDate required.
2. planningEndDate required.
3. planningEndDate >= planningStartDate.
4. Date range length limited to prevent expensive runs.
```

---

## 9.2 Current schedule APIs

```text
GET /api/projects/{projectId}/schedule/current
GET /api/projects/{projectId}/schedule/current/tasks
GET /api/projects/{projectId}/schedule/current/daily-work
GET /api/projects/{projectId}/schedule/current/issues
```

Filters:

```text
taskId
assigneeUserId
riskStatus
scheduleStatus
dateFrom
dateTo
issueType
severity
```

---

## 9.3 Schedule calculation preview / dry-run

Optional:

```text
POST /api/projects/{projectId}/schedule/preview
```

Classification:

```text
DEFERRED_TO_PHASE_14_OR_23 unless needed.
```

---

## 9.4 Task-level schedule APIs

```text
GET /api/projects/{projectId}/tasks/{taskId}/schedule
GET /api/projects/{projectId}/tasks/{taskId}/schedule/history
```

Optional:

```text
POST /api/projects/{projectId}/tasks/{taskId}/schedule/recalculate
```

Partial recalculation deferred unless needed.

---

# 10. Authorization requirements

Required IAM authorities:

```text
TASK_SCHEDULE_VIEW
TASK_SCHEDULE_RECALCULATE
TASK_SCHEDULE_CANCEL_RUN
TASK_SCHEDULE_VIEW_ISSUES
TASK_SCHEDULE_OVERRIDE future
```

Simplified mapping allowed:

```text
TASK_SCHEDULE_VIEW → PROJECT_TASK_VIEW
TASK_SCHEDULE_RECALCULATE → PROJECT_TASK_UPDATE or PROJECT_SCHEDULE_RECALCULATE
```

But completion file must document mapping.

Rules:

```text
1. All schedule APIs require authenticated user.
2. User must have active workspace access.
3. User must have PROJECT_VIEW and task schedule permission.
4. Recalculation requires TASK_SCHEDULE_RECALCULATE.
5. Schedule results inherit project visibility.
6. Schedule issue details must not leak hidden user/private leave details.
7. Future finance/cost fields not returned.
```

---

# 11. Scheduling algorithm TO-BE

## 11.1 High-level algorithm

```text
1. Validate project and authorization.
2. Load project tasks.
3. Exclude archived/cancelled/completed tasks according to options.
4. Load task dependencies.
5. Validate dependency graph acyclic.
6. Load assignees/workspace members.
7. Load capacity profiles/calendars/exceptions.
8. Load project resource allocations.
9. Topologically sort tasks.
10. For each task:
    a. Determine earliestStartDate.
    b. Validate assignee.
    c. Determine available capacity by date.
    d. Allocate estimateHours across dates.
    e. Create ScheduledDailyWork records.
    f. Create TaskSchedule.
    g. Calculate dueDateCapacityGap.
    h. Create SchedulingIssue if needed.
11. Create ScheduleRun summary.
12. Mark run COMPLETED or FAILED.
13. Optionally set project.currentScheduleRunId.
14. Emit events.
```

---

## 11.2 Task selection

By default, schedule:

```text
TODO
IN_PROGRESS
BLOCKED
```

Do not schedule:

```text
COMPLETED
CANCELLED
ARCHIVED
```

unless options include completed tasks for historical comparison.

---

## 11.3 Assignee selection

Phase 13 uses:

```text
Task.assigneeUserId
or Task.inChargeUserId
```

Decision must match current model.

Rules:

```text
1. If assignee/inCharge is null, task cannot be scheduled.
2. Create TASK_NO_ASSIGNEE issue.
3. TaskSchedule status UNSCHEDULED or SKIPPED.
```

Future multi-assignee scheduling:

```text
DEFERRED_TO_POST_23_BACKLOG unless Work Item model supports it.
```

---

## 11.4 Capacity allocation loop

Pseudo:

```text
remaining = task.estimateHours
date = earliestStartDate

while remaining > 0 and date <= planningEndDate:
    if date is working date for user:
        capacity = remainingProjectCapacity(user, project, date)
        if capacity > 0:
            planned = min(remaining, capacity)
            create ScheduledDailyWork(date, planned)
            remaining -= planned
    date = nextDate(date)

if remaining == 0:
    task scheduled
else:
    partially/unscheduled
```

Rules:

```text
1. Never schedule on non-working day.
2. Never schedule negative capacity.
3. Do not exceed daily remaining project capacity unless override implemented.
4. Calendar exceptions override weekly rules.
5. Already scheduled work in same run reduces capacity.
```

---

## 11.5 Dependency ordering

Use topological sort.

Rules:

```text
1. If dependency cycle exists, run fails or affected tasks blocked.
2. Phase 09 should prevent cycles, but Phase 13 must re-check.
3. FINISH_TO_START successor earliestStartDate is predecessor estimatedFinishDate + lagDays.
4. If predecessor unscheduled, successor blocked by dependency.
```

Recommended:

```text
Cycle = BLOCKER issue and run FAILED.
```

---

## 11.6 Planning horizon

Scheduling can be expensive.

Rules:

```text
1. planningStartDate required.
2. planningEndDate required.
3. Max range configurable, e.g. 365 days.
4. If task cannot be scheduled within horizon, unscheduledHours > 0.
5. Issue TASK_NO_CAPACITY or PLANNING_HORIZON_EXCEEDED.
```

---

## 11.7 Over-capacity detection

Phase 12 detects allocation % over-allocation.

Phase 13 detects scheduled work over daily capacity.

Rules:

```text
1. ScheduledDailyWork must not exceed remaining capacity.
2. If manual override not implemented, over-capacity scheduled work should not happen.
3. If existing data causes over-capacity, create RESOURCE_OVER_ALLOCATED issue.
```

Future override:

```text
Phase 14 Gantt manual override.
```

---

# 12. Integration with Gantt Phase 14

Phase 14 will use Phase 13 outputs.

Gantt consumes:

```text
TaskSchedule.estimatedStartDate
TaskSchedule.estimatedFinishDate
ScheduledDailyWork
TaskDependency
ProjectPhase
WBS hierarchy
SchedulingIssue
```

Phase 13 must not implement visual Gantt.

Phase 13 must provide clean APIs/data for Gantt.

---

# 13. Integration with Finance / Rate Card

Phase 13 does not calculate money.

Future Phase 15/17 will combine:

```text
Task.estimateHours
Role/CCH
Assignee role
Schedule dates if needed for rate escalation
```

Phase 13 can expose:

```text
scheduledHours
scheduled date range
```

But must not expose:

```text
cost
margin
profit
contract value
```

---

# 14. Integration with Baseline / Change Request

Phase 13 schedule is forecast.

Phase 19 will snapshot approved schedule into baseline.

Phase 13 must not create baseline.

It may store historical schedule runs.

---

# 15. Event Registry integration

Source system:

```text
SCOPERY_SCHEDULING
```

or if module source list is kept small:

```text
SCOPERY_PROJECT
```

Recommended:

```text
SCOPERY_SCHEDULING
```

Required events:

```text
SCHEDULE_RUN_CREATED
SCHEDULE_RUN_STARTED
SCHEDULE_RUN_COMPLETED
SCHEDULE_RUN_FAILED
SCHEDULE_RUN_CANCELLED

TASK_SCHEDULE_CREATED
TASK_SCHEDULE_UPDATED
TASK_SCHEDULE_AT_RISK
TASK_SCHEDULE_UNSCHEDULED

SCHEDULE_DAILY_WORK_CREATED
SCHEDULING_ISSUE_CREATED
SCHEDULING_ISSUE_RESOLVED optional

TASK_DUE_DATE_CAPACITY_GAP_DETECTED
RESOURCE_SCHEDULE_OVER_CAPACITY_DETECTED
```

Standard variables:

```text
actor.userId
organization.id
workspace.id
project.id
scheduleRun.id
task.id
task.title
assignee.userId
estimatedStartDate
estimatedFinishDate
dueDate
dueDateCapacityGapHours
riskStatus
issue.type
issue.severity
occurredAt
traceId
```

---

# 16. Notification integration

Phase 13 should not send all notifications directly.

Seed-only or defer:

```text
TASK_DUE_DATE_AT_RISK_NOTIFICATION
TASK_UNSCHEDULED_NOTIFICATION
RESOURCE_SCHEDULE_OVER_CAPACITY_NOTIFICATION
SCHEDULE_RUN_FAILED_ADMIN_EMAIL
```

Recommended:

```text
Seed events now.
Full notification rules in Phase 20.
```

---

# 17. AI Agent integration

Phase 13 does not implement AI recommendations.

Future Phase 21 can use schedule data to suggest:

```text
alternate assignee
estimate adjustment
dependency reorder
scope reduction
allocation increase
```

Rules:

```text
AI suggestions cannot auto-change schedule in Phase 13.
```

---

# 18. Platform audit/outbox/idempotency integration

## 18.1 Activity log actions

```text
SCHEDULE_RUN_CREATED
SCHEDULE_RUN_COMPLETED
SCHEDULE_RUN_FAILED
TASK_SCHEDULE_AT_RISK
TASK_SCHEDULE_UNSCHEDULED
```

## 18.2 Audit-sensitive actions

Audit:

```text
Schedule run marked current
Manual override future
Schedule recalculation by admin
Schedule cancellation
```

Phase 13 no manual override unless implemented.

## 18.3 Idempotency

Recommended for:

```text
POST /api/projects/{projectId}/schedule-runs
POST /api/projects/{projectId}/schedule/preview if implemented
```

Same idempotency key should not create duplicate schedule run.

## 18.4 Outbox

Schedule events should use platform outbox if available.

---

# 19. Business rules master

## 19.1 ScheduleRun rules

```text
SCH-RUN-001 Project must exist.
SCH-RUN-002 Project must not be ARCHIVED.
SCH-RUN-003 planningStartDate required.
SCH-RUN-004 planningEndDate required.
SCH-RUN-005 planningEndDate >= planningStartDate.
SCH-RUN-006 Planning horizon must not exceed configured max.
SCH-RUN-007 User must have schedule recalculation permission.
SCH-RUN-008 ScheduleRun status transitions valid.
SCH-RUN-009 Completed run can be marked current.
SCH-RUN-010 ScheduleRun does not mutate task status.
```

## 19.2 TaskSchedule rules

```text
SCH-TASK-001 Task must belong to schedule project.
SCH-TASK-002 Task estimateHours must be > 0.
SCH-TASK-003 Task without assignee cannot be scheduled.
SCH-TASK-004 Task with inactive assignee cannot be scheduled.
SCH-TASK-005 estimatedFinishDate >= estimatedStartDate if both exist.
SCH-TASK-006 scheduledHours + unscheduledHours = task.estimateHours.
SCH-TASK-007 Due date is copied from task, not overwritten.
SCH-TASK-008 Risk status derived from schedule result.
```

## 19.3 ScheduledDailyWork rules

```text
SCH-DAY-001 workDate required.
SCH-DAY-002 plannedHours > 0.
SCH-DAY-003 Cannot schedule non-working date.
SCH-DAY-004 Cannot exceed remaining project capacity unless override.
SCH-DAY-005 Sum daily work equals task scheduledHours.
SCH-DAY-006 ScheduledDailyWork is planned, not actual.
```

## 19.4 Dependency scheduling rules

```text
SCH-DEP-001 Dependency graph must be acyclic.
SCH-DEP-002 FINISH_TO_START respected.
SCH-DEP-003 Unsupported dependency type creates issue.
SCH-DEP-004 Unscheduled predecessor blocks successor.
SCH-DEP-005 lagDays applied if present.
```

## 19.5 Due date risk rules

```text
SCH-RISK-001 dueDate null means no deadline risk unless unscheduled.
SCH-RISK-002 estimatedFinishDate > dueDate means AT_RISK.
SCH-RISK-003 today > dueDate and task incomplete means OVERDUE.
SCH-RISK-004 dueDateCapacityGap > 0 creates issue.
```

---

# 20. Error catalog requirements

Exact names follow project convention, but these concepts must exist.

```text
SCHEDULE_RUN_NOT_FOUND
SCHEDULE_RUN_PROJECT_ARCHIVED
SCHEDULE_RUN_INVALID_DATE_RANGE
SCHEDULE_RUN_RANGE_TOO_LARGE
SCHEDULE_RUN_ALREADY_RUNNING
SCHEDULE_RUN_NOT_CANCELLABLE
SCHEDULE_RUN_FAILED

TASK_SCHEDULE_NOT_FOUND
TASK_SCHEDULE_TASK_NOT_FOUND
TASK_SCHEDULE_TASK_PROJECT_MISMATCH
TASK_SCHEDULE_TASK_NO_ASSIGNEE
TASK_SCHEDULE_ASSIGNEE_INACTIVE
TASK_SCHEDULE_NO_CAPACITY
TASK_SCHEDULE_UNSUPPORTED_DEPENDENCY_TYPE

SCHEDULE_DEPENDENCY_CYCLE_DETECTED
SCHEDULE_DEPENDENCY_PREDECESSOR_UNSCHEDULED

SCHEDULE_CAPACITY_PROFILE_MISSING
SCHEDULE_PROJECT_ALLOCATION_MISSING
SCHEDULE_CALENDAR_MISSING
SCHEDULE_NON_WORKING_DAY

SCHEDULE_ACCESS_DENIED
```

---

# 21. Required tests

Phase 13 is incomplete without tests.

---

## 21.1 ScheduleRun tests

```text
createScheduleRun_valid_success
createScheduleRun_archivedProject_rejected
createScheduleRun_invalidDateRange_rejected
createScheduleRun_rangeTooLarge_rejected
createScheduleRun_withoutPermission_forbidden
scheduleRun_success_marksCompleted
scheduleRun_failure_marksFailed
scheduleRun_markAsCurrent_setsProjectCurrentScheduleRun
scheduleRun_idempotency_sameKey_noDuplicateRun
```

## 21.2 Capacity allocation tests

```text
scheduleTask_singleDayCapacity_success
scheduleTask_multiDayCapacity_success
scheduleTask_skipsWeekend
scheduleTask_skipsHolidayException
scheduleTask_usesSpecialWorkingDay
scheduleTask_appliesFocusFactor
scheduleTask_appliesProjectAllocationPercent
scheduleTask_subtractsAlreadyScheduledWorkInSameRun
scheduleTask_noAllocation_unscheduledOrIssueAccordingToPolicy
scheduleTask_noCapacity_unscheduled
```

## 21.3 TaskSchedule tests

```text
taskWithoutAssignee_unscheduledIssue
taskWithInactiveAssignee_unscheduledIssue
taskEstimateHoursEqualsScheduledPlusUnscheduled
taskDueDateCopiedNotMutated
taskEstimatedFinishAfterDueDate_atRisk
taskDueDateCapacityGap_created
completedTaskExcludedByDefault
cancelledTaskExcludedByDefault
archivedTaskExcludedByDefault
```

## 21.4 Dependency tests

```text
dependencyFinishToStart_successorStartsAfterPredecessorFinish
dependencyLagDays_applied
dependencyCycleDetected_runFailedOrBlockerIssue
unsupportedDependencyType_issueCreated
unscheduledPredecessor_blocksSuccessor
```

## 21.5 ScheduledDailyWork tests

```text
dailyWork_plannedHoursPositive
dailyWork_notCreatedForNonWorkingDay
dailyWork_notExceedRemainingCapacity
dailyWork_sumEqualsScheduledHours
dailyWork_isForecastNotActual
```

## 21.6 SchedulingIssue tests

```text
issueCreated_taskNoAssignee
issueCreated_taskNoCapacity
issueCreated_dueDateCapacityGap
issueCreated_dependencyCycle
issueCreated_allocationMissing
issueSeverity_correct
```

## 21.7 Authorization tests

```text
viewSchedule_withoutPermission_forbidden
createScheduleRun_withoutPermission_forbidden
viewSchedule_crossWorkspace_forbidden
viewScheduleIssues_withoutPermission_forbidden
```

## 21.8 Seeder/event tests

```text
schedulingEventSeeder_firstRun_createsDefinitions
schedulingEventSeeder_secondRun_noDuplicates
schedulingPermissionSeeder_authoritiesExist
scheduleRunCompleted_eventEmitted
taskAtRisk_eventEmitted
```

---

# 22. Manual verification checklist

Completion file must include:

```text
1. Create project with tasks and estimates.
2. Create working calendar and allocation.
3. Assign tasks to active workspace member.
4. Run schedule calculation.
5. Confirm TaskSchedule records created.
6. Confirm ScheduledDailyWork records created.
7. Confirm weekend/holiday skipped.
8. Confirm focus factor and allocation applied.
9. Confirm dependency successor starts after predecessor finish.
10. Confirm due date risk when finish exceeds due date.
11. Confirm missing allocation creates issue.
12. Confirm no Gantt/finance/baseline records created.
13. Confirm schedule run can be queried.
14. Confirm latest completed run marked current.
15. Confirm events/activity logs created.
```

---

# 23. Acceptance criteria

Phase 13 is accepted only if:

```text
1. Current scheduling capability is classified against TO-BE.
2. ScheduleRun implemented/tested.
3. TaskSchedule implemented/tested.
4. ScheduledDailyWork implemented/tested.
5. SchedulingIssue implemented/tested.
6. Scheduling engine uses Phase 12 capacity data.
7. Scheduling engine respects working days/exceptions/focus factor/allocation.
8. Scheduling engine respects FINISH_TO_START dependencies.
9. Due date capacity gap calculated.
10. Risk status calculated.
11. Authorization implemented/tested.
12. Scheduling events seeded idempotently.
13. Activity/audit/outbox integration follows Phase 04.
14. Phase 13 does not falsely claim Gantt/cost/quote/baseline/timesheet/AI optimization.
15. mvn compile passes.
16. mvn test passes.
17. Completion file exists and includes gap matrix.
```

Agent must not mark complete if:

```text
mvn test fails
schedule ignores calendar exceptions
schedule ignores focus factor
schedule ignores allocation %
schedule creates work on non-working days
schedule exceeds remaining capacity without override
dependency successor starts before predecessor finish
due date risk not calculated
Task.dueDate is overwritten
Task.status is mutated by schedule run
Gantt/finance/baseline is claimed implemented
```

---

# 24. Required phase completion file

Agent must create:

```text
docs/phase-complete/PHASE_13_TASK_SCHEDULING_ENGINE_TO_BE_COMPLETE.md
```

Required sections:

```text
# Phase 13 — Task Scheduling Engine TO-BE Complete

## 1. Summary
## 2. Source Inputs Reviewed
## 3. Current vs TO-BE Classification Matrix
## 4. Implemented in Current BE
## 5. Implemented / Hardened in This Phase
## 6. Seed-only Items Added
## 7. Deferred Items and Target Phase
## 8. Entity Mapping
## 9. API Changes
## 10. Scheduling Algorithm
## 11. Capacity Formula Implementation
## 12. Dependency Scheduling Rules
## 13. Due Date Risk Rules
## 14. Over-capacity / Issue Rules
## 15. Authorization Matrix
## 16. Event Registry Seeder Matrix
## 17. Activity / Audit / Outbox Notes
## 18. Idempotency Strategy
## 19. Tests Added
## 20. Commands Run
## 21. Test Results
## 22. Manual Verification
## 23. Assumptions
## 24. Deviations From Prompt
## 25. Known Risks
## 26. Future Phases That Must Return to Scheduling
```

---

# 25. Future phases that must return to Scheduling

## 25.1 Phase 14 — WBS-driven Gantt

Must consume:

```text
TaskSchedule
ScheduledDailyWork
TaskDependency
ProjectPhase
WBS tree
SchedulingIssue
```

Must add:

```text
Gantt projection
Gantt task bars
Gantt dependency lines
Manual drag/drop updates
Milestones
```

## 25.2 Phase 15 — Rate Card / CCH

May use scheduled date range for future rate escalation if needed.

## 25.3 Phase 16 — Estimation Roll-up

Must compare estimated effort vs scheduled effort.

## 25.4 Phase 17 — Finance

May use scheduled dates for forecast cost phasing.

Cost calculation is not Phase 13.

## 25.5 Phase 18 — Quote

May use schedule forecast as timeline basis for quote.

Quote commitments require approval.

## 25.6 Phase 19 — Baseline / Change Request

Must snapshot schedule run into baseline.

Schedule changes after baseline may require change request.

## 25.7 Phase 20 — Project Notifications

Must notify:

```text
due date risk
unscheduled task
over capacity
schedule run failed
```

## 25.8 Phase 21 — AI-assisted Project Planning

May recommend:

```text
reassignment
scope reduction
dependency changes
allocation changes
```

AI must not auto-apply without approval.

## 25.9 Phase 22 — Reporting

Must expose:

```text
schedule risk dashboard
capacity gap report
tasks finishing after due date
schedule run history
```

## 25.10 Phase 23 — Core Hardening

Must review:

```text
scheduling performance
large dependency graph
large date range
timezone correctness
concurrency
idempotency
authorization
```

---

# 26. Agent anti-bịa rules

The agent must not:

```text
1. Claim Gantt exists in Phase 13.
2. Claim drag/drop scheduling exists unless manual override APIs exist.
3. Claim cost/margin exists.
4. Claim quote timeline is approved.
5. Claim baseline exists.
6. Claim timesheet actuals exist.
7. Mutate Task.status during schedule calculation.
8. Overwrite Task.dueDate.
9. Treat estimateHours as duration without capacity.
10. Ignore project allocations.
11. Ignore focus factor.
12. Ignore calendar exceptions.
13. Schedule work on non-working days.
14. Hide unsupported dependency type behavior.
15. Hide unscheduled tasks.
```

---

# 27. Prompt to give coding agent

```text
You are implementing Phase 13 — TO-BE Task Scheduling Engine, Capacity-based Forecasting, Due-date Risk & Dependency-aware Planning.

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
- Phase 12 Resource Calendar / Capacity TO-BE spec
- Current backend code, migrations, tests

Your task:
1. Compare current scheduling capability against this Phase 13 TO-BE spec.
2. Classify each capability as CURRENTLY_IMPLEMENTED, PARTIALLY_IMPLEMENTED, MUST_IMPLEMENT_IN_PHASE_13, SEED_ONLY_IN_PHASE_13, DEFERRED_TO_PHASE_XX, or NOT_IN_SCOPE_FOR_BACKEND_NOW.
3. Implement only Phase 13 required items.
4. Implement ScheduleRun, TaskSchedule, ScheduledDailyWork, SchedulingIssue.
5. Implement capacity-aware task scheduling using Phase 12 calendars, exceptions, focus factor, and project allocations.
6. Implement FINISH_TO_START dependency-aware scheduling.
7. Implement dueDate capacity gap and risk status.
8. Add schedule APIs and permissions.
9. Add scheduling event definitions.
10. Add tests listed in this spec.
11. Run mvn compile and mvn test.
12. Create docs/phase-complete/PHASE_13_TASK_SCHEDULING_ENGINE_TO_BE_COMPLETE.md with full gap matrix.

Do not implement or claim Gantt, finance/cost, quote, baseline/change request, timesheet actuals, or AI optimization in this phase.
```

---

# 28. Quick tracking matrix

| Capability | Current backend | Phase 13 action | Later phase |
|---|---|---|---|
| ScheduleRun | Missing/unknown | Must implement | — |
| TaskSchedule | Missing/unknown | Must implement | — |
| ScheduledDailyWork | Missing/unknown | Must implement | — |
| SchedulingIssue | Missing/unknown | Must implement | — |
| Capacity-aware scheduling | Missing | Must implement | — |
| FINISH_TO_START scheduling | Missing | Must implement | Later types Phase 14/23 |
| Due date capacity gap | Missing | Must implement | — |
| Risk status | Missing | Must implement | Phase 20 notify |
| Manual override | Missing | Defer | Phase 14 |
| Gantt visualization | Missing | Defer | Phase 14 |
| Resource leveling | Missing | Defer | Phase 21/Post-23 |
| Cost phasing | Missing | Defer | Phase 17 |
| Baseline snapshot | Missing | Defer | Phase 19 |
| Schedule reports | Missing | Defer | Phase 22 |
| AI schedule recommendation | Missing | Defer | Phase 21 |

---

# 29. Final principle

Phase 13 is not complete when "task dates are stored."

Phase 13 is complete when Scopery can calculate a realistic forecast:

```text
Task estimate hours
+ assignee capacity
+ focus factor
+ project allocation
+ working calendar
+ holidays
+ dependencies
= estimated start / finish / daily planned work / risk
```

Scheduling is not Gantt.

Scheduling is not finance.

Scheduling is not timesheet.

Scheduling is the planning engine that makes Gantt and reporting possible later.
