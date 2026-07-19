# Phase 13 — Task Scheduling Engine TO-BE Complete

## 1. Summary

Phase 13 delivers the first planning intelligence layer: a deterministic, capacity-aware,
dependency-aware scheduling engine under `modules/project/scheduling`. It creates immutable
`ScheduleRun` snapshots with `TaskSchedule`, `ScheduledDailyWork`, and `SchedulingIssue`
results for later Gantt/reporting consumption.

Phase 13 does **not** implement Gantt UI, cost/margin, quote, baseline, timesheet actuals,
manual schedule override, AI leveling, or preview/dry-run.

## 2. Source Inputs Reviewed

- `PHASE_13_TASK_SCHEDULING_ENGINE_TO_BE_DETAILED.md`
- Phase 09–12 completion docs + `modules/project/**` and `modules/resourcecapacity/**`
- Capacity calculation formulas (working hours × focus × allocation %)
- Task / TaskDependency / Project authorization patterns
- Phase 04 outbox/audit + Phase 05 event seed pattern

## 3. Current vs TO-BE Classification Matrix

| Capability | Pre-Phase 13 | Phase 13 result |
|---|---|---|
| Task estimate / dueDate / inCharge | Implemented | Reused (`inChargeUserId` = assignee) |
| TaskDependency FINISH_TO_START + lag | Implemented | Reused; engine re-checks cycles |
| Working calendar / exceptions / focus / allocation | Implemented | Consumed via `SchedulingCapacityResolver` |
| ScheduleRun | Missing | MUST → Implemented |
| TaskSchedule | Missing | MUST → Implemented |
| ScheduledDailyWork | Missing | MUST → Implemented |
| SchedulingIssue | Missing | MUST → Implemented |
| Capacity-aware effort placement | Missing | MUST → Implemented |
| Due-date capacity gap + risk | Missing | MUST → Implemented |
| Schedule APIs | Missing | MUST → Implemented |
| Schedule auth mapping | Missing | MUST → Implemented (mapped to PROJECT_TASK_*) |
| Scheduling events | Missing | SEED + emit on run lifecycle |
| Manual override / preview | Missing | DEFERRED_TO_PHASE_14 |
| Gantt / cost / baseline / timesheet / AI | Out of scope | NOT_IN_SCOPE |

## 4. Implemented in This Phase

- Flyway `V50__create_schedule_tables_phase13.sql`
- `project_project.current_schedule_run_id`
- Module `modules/project/scheduling` with:
  - `schedulerun`, `taskschedule`, `scheduleddailywork`, `schedulingissue`
  - `engine` (`ScheduleEngineService`, `SchedulingCapacityResolver`)
- APIs under `/api/projects/{projectId}/schedule-runs` and `/schedule/current/*`
- Task schedule current + history endpoints
- Event seed `SCOPERY_SCHEDULING` / owner `SCHEDULING`
- Outbox + activity + audit for recalculation / mark-current / cancel / at-risk / unscheduled
- Error catalog entries for schedule failures

## 5. Entity Mapping

| Entity | Table |
|---|---|
| ScheduleRun | `project_schedule_run` |
| TaskSchedule | `project_task_schedule` |
| ScheduledDailyWork | `project_scheduled_daily_work` |
| SchedulingIssue | `project_scheduling_issue` |
| Project pointer | `project_project.current_schedule_run_id` |

## 6. API Changes

| Surface | Path |
|---|---|
| Create/list/get/cancel runs | `/api/projects/{projectId}/schedule-runs` |
| Current run | `GET /api/projects/{projectId}/schedule/current` |
| Current tasks | `GET .../schedule/current/tasks` (filters: taskId, assigneeUserId, riskStatus, scheduleStatus) |
| Current daily work | `GET .../schedule/current/daily-work` (filters: taskId, assigneeUserId, dateFrom, dateTo) |
| Current issues | `GET .../schedule/current/issues` (filters: taskId, issueType, severity) |
| Task schedule | `GET /api/projects/{projectId}/tasks/{taskId}/schedule` |
| Task schedule history | `GET .../tasks/{taskId}/schedule/history` |

Create body:

```json
{
  "planningStartDate": "2026-08-01",
  "planningEndDate": "2026-09-30",
  "options": {
    "includeCompletedTasks": false,
    "markAsCurrent": true
  }
}
```

`Idempotency-Key` is supported via the platform filter for any POST under `/api` when the header is present.

## 7. Algorithm Rules

- Algorithm version: `1.0.0`
- Max inclusive planning horizon: **365 days**
- Assignee: `Task.inChargeUserId` only
- Default schedule statuses: `TODO`, `IN_PROGRESS`, `BLOCKED` (exclude `DONE`/`CANCELLED`/`ARCHIVED` unless `includeCompletedTasks`)
- Missing project allocation → **0 capacity** + `ALLOCATION_MISSING` (no optimistic 100%)
- Capacity per date:

```text
FocusedCapacity = WorkingHours × FocusFactor
ProjectAllocatedCapacity = FocusedCapacity × ActiveProjectAllocationPercent / 100
RemainingCapacity = ProjectAllocatedCapacity − same-run scheduled hours
DueDateCapacityGap = max(0, Estimate − RemainingCapacity from earliestStart..dueDate)
```

- Non-working days / exceptions: never receive planned hours
- FINISH_TO_START: successor earliest = max(planningStart, plannedStart, predecessorFinish + lagDays)
- Unsupported dependency types → `UNSUPPORTED_DEPENDENCY_TYPE` warning
- Cycle → `TASK_DEPENDENCY_CYCLE` BLOCKER + run `FAILED`
- Does **not** mutate `Task.dueDate` or `Task.status`

## 8. Risk Status

| Status | Meaning |
|---|---|
| ON_TRACK | Schedulable and within due date capacity |
| AT_RISK | Finish after due date or capacity gap > 0 |
| OVERDUE | Today after due date |
| BLOCKED_BY_DEPENDENCY | Predecessor unscheduled |
| NO_ASSIGNEE | Missing/inactive in-charge |
| NO_CAPACITY | Partial/fully unscheduled for capacity/horizon |
| UNSCHEDULED | Could not place schedule |

## 9. Authorization Mapping

Phase 13 reuses existing project task authorities (documented simplification):

| Spec authority | Mapped to |
|---|---|
| TASK_SCHEDULE_VIEW / VIEW_ISSUES | `IamAuthorities.PROJECT_TASK_VIEW` |
| TASK_SCHEDULE_RECALCULATE / CANCEL_RUN | `IamAuthorities.PROJECT_TASK_UPDATE` |

Helpers: `requireScheduleView`, `requireScheduleRecalculate`, `requireScheduleCancel`.

## 10. Events / Activity / Audit

**Events (`SCOPERY_SCHEDULING`):**
SCHEDULE_RUN_CREATED/STARTED/COMPLETED/FAILED/CANCELLED,
TASK_SCHEDULE_CREATED/UPDATED/AT_RISK/UNSCHEDULED,
SCHEDULE_DAILY_WORK_CREATED, SCHEDULING_ISSUE_CREATED,
TASK_DUE_DATE_CAPACITY_GAP_DETECTED, RESOURCE_SCHEDULE_OVER_CAPACITY_DETECTED

**Activity:** SCHEDULE_RUN_CREATED/COMPLETED/FAILED, TASK_SCHEDULE_AT_RISK, TASK_SCHEDULE_UNSCHEDULED

**Audit:** SCHEDULE_RECALCULATION, SCHEDULE_RUN_MARKED_CURRENT, SCHEDULE_RUN_CANCELLED

## 11. Deferred Items

| Item | Target |
|---|---|
| Manual schedule override / Gantt drag-drop | Phase 14 |
| Schedule preview/dry-run | Phase 14/23 |
| Partial task recalculation | Later |
| Resource leveling / AI recommendations | Phase 21 |
| Cost / quote / baseline / timesheet | Phases 15/17/19/37 |
| Dedicated schedule IAM permission codes | Optional later |
| Full notification rules | Phase 20 |

## 12. Verification

- `mvn -q -DskipTests compile` — passed
- `mvn -q -Dtest=ScheduleEngineServiceTest,SchedulingEventDefinitionSeedInitializerTest,CreateScheduleRunActionTest test` — passed

Covered scenarios:
- FS + capacity placement
- Non-working day skip
- No assignee / no estimate
- Missing allocation (zero capacity)
- Dependency cycle fails run
- Unsupported dependency warning
- Due-date gap → AT_RISK
- Archived project / invalid range / range too large / forbidden

## 13. Acceptance Checklist

- [x] ScheduleRun / TaskSchedule / ScheduledDailyWork / SchedulingIssue implemented
- [x] Engine uses Phase 12 capacity inputs
- [x] Working days / exceptions / focus / allocation respected
- [x] FINISH_TO_START + lag + cycle re-check
- [x] Due-date capacity gap + risk status
- [x] Authorization mapped and tested
- [x] Events seeded idempotently
- [x] Activity/audit/outbox wired
- [x] Does not claim Gantt/cost/quote/baseline/timesheet/AI
- [x] Compile + focused tests pass
- [x] Completion file written
