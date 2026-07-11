# PHASE 14 — TO-BE WBS-driven Gantt, Timeline Projection, Milestone & Manual Schedule Adjustment

> Project: Scopery Backend  
> Phase: 14  
> Document type: TO-BE implementation-grade specification  
> Status: Planning / Gap-driven implementation spec  
> Depends on: Phase 00 — Master Backend Roadmap, Phase 01 — API Path / Security Baseline, Phase 02 — IAM Core, Phase 03 — Workspace Core, Phase 04 — Platform Audit / Outbox / Idempotency, Phase 05 — Event Registry, Phase 06 — Notification Engine, Phase 07 — AI Agent Platform, Phase 08 — Knowledge / Document Type Catalog, Phase 09 — Project Core / Phase / WBS / Task, Phase 10 — Project Authorization, Phase 11 — Project Template / Phase Catalog, Phase 12 — Resource Calendar / Capacity, Phase 13 — Task Scheduling Engine  
> API base: `/api`  
> Primary module: `modules/project/gantt` or `modules/gantt` depending on repo architecture  
> Related modules: `project`, `scheduling`, `capacity`, `workspace`, `iam`, `eventregistry`, `notification`, future `baseline`, `change-request`, `finance`, `quote`, `reporting`, `aiagent`  
> Important rule: This file is **not an as-is document**. It defines the TO-BE Gantt projection and timeline adjustment layer. Phase 14 does not implement finance, quote, baseline approval, change request workflow, or AI autonomous planning.

---

# 0. Purpose of this file

Phase 14 creates the WBS-driven Gantt layer.

Previous phases created:

```text
Phase 09:
- Project
- ProjectPhase
- WBS tree
- Task
- TaskDependency

Phase 12:
- Working calendar
- Focus factor
- Project allocation
- Capacity foundation

Phase 13:
- ScheduleRun
- TaskSchedule
- ScheduledDailyWork
- SchedulingIssue
- Estimated start/finish dates
- Due date risk
```

Phase 14 answers:

```text
How should a project be visualized as a Gantt timeline?
How do WBS, phases, tasks, dependencies, and milestones appear in a timeline?
What happens when a user drags a task bar?
What happens when a user changes a dependency line?
What backend records are updated by Gantt edits?
How does Gantt stay a projection of WBS/tasks instead of becoming a separate source of truth?
```

Phase 14 must preserve a key decision:

```text
WBS / Task / Schedule are source of truth.
Gantt is projection + controlled adjustment interface.
```

---

# 1. Source inputs

Before coding Phase 14, the agent must read:

```text
1. Current backend codebase
2. Phase 09 Project Core TO-BE spec and implementation
3. Phase 10 Project Authorization TO-BE spec and implementation
4. Phase 11 Project Template TO-BE spec and implementation
5. Phase 12 Resource Calendar / Capacity TO-BE spec and implementation
6. Phase 13 Task Scheduling Engine TO-BE spec and implementation
7. Phase 04 Platform Audit / Outbox / Idempotency spec
8. Phase 05 Event Registry spec
9. Phase 06 Notification spec
10. Existing project task/dependency APIs
11. Existing ScheduleRun/TaskSchedule APIs
12. Existing IAM/project authorization service
13. Current BE feature/entity/business-rule inventory
14. Dynamic Work OS feature catalog
```

The agent must not implement Phase 14 from assumptions only.

---

# 2. Current expected backend state

After Phase 13, the backend should have:

```text
Project
ProjectPhase
WbsNode
Task
TaskDependency
ScheduleRun
TaskSchedule
ScheduledDailyWork
SchedulingIssue
```

Likely missing:

```text
Gantt view API
Gantt item projection
Gantt dependency projection
Gantt milestone model
Manual Gantt task move
Manual Gantt task resize
Gantt dependency create/remove/update
Gantt recalculation endpoint
Gantt conflict detection
Gantt view configuration
Gantt export
Gantt baseline comparison
```

Phase 14 implements Gantt projection and controlled timeline adjustment.

---

# 3. Phase 14 target statement

Phase 14 must deliver a future-ready WBS-driven Gantt layer:

```text
1. Generate Gantt view from ProjectPhase, WBS, Task, TaskDependency, and latest ScheduleRun.
2. Represent phases, WBS nodes, tasks, dependencies, milestones, and schedule issues in a timeline-friendly response.
3. Allow controlled task move/resize if product requires manual adjustment.
4. Gantt edits must update underlying Task/Schedule inputs, not create disconnected Gantt-only data.
5. Gantt dependency edits must update TaskDependency records.
6. Recalculation after Gantt edits must use Phase 13 scheduling engine.
7. Milestones can be modeled safely without pretending to be full baseline/change management.
8. Gantt actions are authorized, audited, evented, and tested.
9. Gantt does not calculate cost, quote, margin, baseline approval, or actual timesheet.
```

---

# 4. Core principle

## 4.1 Gantt is not source of truth

Gantt view is a projection.

Source of truth:

```text
ProjectPhase → phase grouping
WbsNode → scope hierarchy
Task → execution unit
TaskDependency → dependency graph
TaskSchedule → forecast dates/daily plan
ScheduleRun → scheduling calculation
```

Gantt must not create an independent copy of the project plan that drifts from tasks.

## 4.2 Gantt edits update source records

Examples:

```text
Drag task bar start/end:
- update schedule override or task date fields depending on chosen model
- trigger schedule recalculation

Draw dependency line:
- create TaskDependency

Delete dependency line:
- remove TaskDependency

Move WBS row:
- call WBS move action

Rename task:
- call Task update action
```

Do not write directly to Gantt-only fields unless they are view preferences.

---

# 5. Phase 14 scope decision

## 5.1 Must implement now

```text
Gantt view/read model
Gantt projection API
Gantt item DTOs for phases/WBS/tasks/milestones
Gantt dependency DTOs
Gantt issue markers
Gantt recalculation endpoint using Phase 13
Controlled Gantt task move/resize model or explicit deferral
Gantt dependency create/remove through TaskDependency
Milestone foundation
Gantt permissions
Gantt events
Gantt tests
Completion report
```

## 5.2 Optional now

```text
Gantt view preferences
Gantt collapsed/expanded state
Gantt zoom level
Critical path indicator
Manual schedule override persistence
Gantt export
```

Implement only if current product requires.

## 5.3 Must not implement now

```text
Finance/cost timeline
Quote timeline approval
Baseline comparison
Change request workflow
Timesheet actual progress
AI schedule optimization
Resource leveling
Portfolio Gantt
Client portal Gantt
```

---

# 6. TO-BE capability matrix

---

## 6.1 GNT-001 — Gantt projection/read model

| Item | Value |
|---|---|
| Future capability | Generate timeline response from project structure and schedule |
| Current state | Missing/unknown |
| Phase 14 target | Implement |
| Classification | `MUST_IMPLEMENT_IN_PHASE_14` |

Rules:

```text
1. Gantt projection belongs to one project.
2. User must have GANTT_VIEW or mapped PROJECT_TASK_VIEW/PROJECT_WBS_VIEW.
3. Projection uses latest completed ScheduleRun by default.
4. Projection can accept scheduleRunId for historical view.
5. Projection includes phases, WBS nodes, tasks, dependencies, and issues.
6. Projection must not return hidden finance/quote data.
7. Projection response is DTO/read model, not JPA/domain.
8. Projection must handle unscheduled tasks safely.
```

---

## 6.2 GNT-002 — Gantt item types

| Item | Value |
|---|---|
| Future capability | Represent phases, WBS nodes, tasks, and milestones in timeline |
| Current state | Missing/unknown |
| Phase 14 target | Implement |
| Classification | `MUST_IMPLEMENT_IN_PHASE_14` |

Item types:

```text
PROJECT
PHASE
WBS_NODE
TASK
MILESTONE
```

Rules:

```text
1. Project item spans min/max of scheduled children or project planned dates.
2. Phase item spans tasks in phase or phase planned dates.
3. WBS node item spans tasks under WBS subtree.
4. Task item uses TaskSchedule estimatedStartDate/estimatedFinishDate.
5. Milestone item is zero-duration date marker.
6. Unscheduled tasks appear with no dates or special status.
7. Each item references source entity type/id.
```

---

## 6.3 GNT-003 — Gantt dependency projection

| Item | Value |
|---|---|
| Future capability | Visualize dependency lines between tasks |
| Current state | TaskDependency exists; projection missing |
| Phase 14 target | Implement |
| Classification | `MUST_IMPLEMENT_IN_PHASE_14` |

Rules:

```text
1. Dependency projection derives from TaskDependency.
2. Only dependencies where both tasks belong to project are returned.
3. FINISH_TO_START must be supported.
4. Unsupported dependency types displayed if stored but marked unsupported if not scheduled.
5. Dependency projection includes lagDays if supported.
```

---

## 6.4 GNT-004 — Milestone foundation

| Item | Value |
|---|---|
| Future capability | Add milestone markers to project timeline |
| Current state | Missing/unknown |
| Phase 14 target | Implement simple milestone entity or map milestone tasks |
| Classification | `MUST_IMPLEMENT_IN_PHASE_14` if milestones are required by Gantt; otherwise `DEFERRED_TO_PHASE_19` |

Two acceptable models:

### Model A — Milestone as Task

```text
Task.type = MILESTONE
Task.estimateHours = minimal/zero policy
TaskSchedule date = milestone date
```

Problem:

```text
Phase 09 requires estimateHours > 0, so zero-hour milestone task may violate rules.
```

### Model B — Dedicated ProjectMilestone entity

Recommended:

```text
ProjectMilestone
```

Rules:

```text
1. Milestone belongs to project.
2. Milestone can optionally belong to phase/WBS node.
3. Milestone date required.
4. Milestone does not consume capacity.
5. Milestone can have status.
6. Milestone can appear in Gantt.
7. Milestone does not replace baseline milestone approval.
```

Phase 14 recommended:

```text
Implement ProjectMilestone as dedicated entity.
```

---

## 6.5 GNT-005 — Gantt recalculation

| Item | Value |
|---|---|
| Future capability | Recalculate schedule and refresh Gantt view |
| Current state | Phase 13 schedule recalculation exists |
| Phase 14 target | Expose Gantt-friendly recalculation endpoint |
| Classification | `MUST_IMPLEMENT_IN_PHASE_14` |

Rules:

```text
1. Recalculation delegates to Phase 13 ScheduleRun.
2. Gantt does not implement separate scheduling algorithm.
3. User must have GANTT_RECALCULATE or TASK_SCHEDULE_RECALCULATE.
4. New completed schedule run becomes current if requested.
5. Event GANTT_RECALCULATED emitted after successful run.
```

---

## 6.6 GNT-006 — Gantt task move

| Item | Value |
|---|---|
| Future capability | Drag task bar to new timeline position |
| Current state | Missing |
| Phase 14 target | Implement controlled move or explicitly defer |
| Classification | `MUST_IMPLEMENT_IN_PHASE_14` if UI requires drag/drop; otherwise `DEFERRED_TO_PHASE_14_FOLLOWUP` |

There are two models.

### Model A — Move updates Task.dueDate only

Simple but limited.

```text
Move finish date → update Task.dueDate
Then recalculate schedule.
```

Problem:

```text
Does not force start date; schedule engine may calculate different dates.
```

### Model B — Manual Schedule Override

Recommended if drag/drop is required.

Introduce:

```text
TaskScheduleOverride
```

Rules:

```text
1. Override stores manualStartDate/manualFinishDate or pinned date.
2. Override requires reason.
3. Override can be soft/hard.
4. Phase 13 scheduler must respect override if active.
5. Override can create capacity/dependency issues.
6. Override is audited.
```

Recommended Phase 14:

```text
Implement TaskScheduleOverride if drag/drop is in scope.
If not, provide read-only Gantt and defer drag/drop.
```

Completion file must document decision.

---

## 6.7 GNT-007 — Gantt task resize

| Item | Value |
|---|---|
| Future capability | Resize task bar to change planned duration/finish |
| Current state | Missing |
| Phase 14 target | Implement only with schedule override model |
| Classification | `DEFERRED_TO_PHASE_14_FOLLOWUP` unless override implemented |

Rules if implemented:

```text
1. Resize does not change estimateHours automatically unless user explicitly chooses.
2. Resize can update manual finish date.
3. Changing estimateHours belongs to Task update, not pure Gantt resize.
4. Resize requires GANTT_RESIZE_TASK permission.
5. Resize is audited.
```

---

## 6.8 GNT-008 — Gantt dependency edit

| Item | Value |
|---|---|
| Future capability | Add/remove dependency from Gantt line UI |
| Current state | TaskDependency exists |
| Phase 14 target | Implement through TaskDependency actions |
| Classification | `MUST_IMPLEMENT_IN_PHASE_14` |

Rules:

```text
1. Creating Gantt dependency calls TaskDependency create.
2. Removing Gantt dependency calls TaskDependency remove.
3. Dependency cycle prevention remains Phase 09/13 responsibility.
4. After dependency edit, schedule recalculation recommended.
5. User requires GANTT_EDIT_DEPENDENCY or PROJECT_TASK_DEPENDENCY_CREATE/REMOVE.
```

---

## 6.9 GNT-009 — Gantt WBS/row move

| Item | Value |
|---|---|
| Future capability | Reorder WBS/task rows in Gantt |
| Current state | WBS move exists |
| Phase 14 target | Reuse WBS move; task row order optional |
| Classification | `PARTIALLY_IMPLEMENTED` if WBS move exists; task row order deferred |

Rules:

```text
1. WBS row move calls WBS move action.
2. Task row move within WBS may update task orderIndex if task supports it.
3. Task ordering is view-only unless Task has explicit order field.
4. Must prevent WBS cycle.
```

Task order field:

```text
DEFERRED unless existing.
```

---

## 6.10 GNT-010 — Critical path

| Item | Value |
|---|---|
| Future capability | Highlight tasks that determine project finish |
| Current state | Missing |
| Phase 14 target | Optional/defer |
| Classification | `DEFERRED_TO_PHASE_23_HARDENING_OR_POST_23_OPTIMIZATION` |

Reason:

```text
Critical path requires stable scheduling semantics and dependency types.
```

Optional simple indicator:

```text
tasks with no slack based on due/finish can be marked approximate.
```

If implemented, must label as approximate unless full CPM exists.

---

## 6.11 GNT-011 — Gantt view preferences

| Item | Value |
|---|---|
| Future capability | Persist collapsed rows, zoom, date window |
| Current state | Missing |
| Phase 14 target | Optional |
| Classification | `DEFERRED_TO_POST_23_USER_PRODUCTIVITY_BACKLOG` unless UI requires |

Possible entity:

```text
GanttViewPreference
```

Fields:

```text
userId
projectId
zoomLevel
dateFrom
dateTo
collapsedNodeIds
showCriticalPath
showUnscheduled
```

Do not block core Gantt on preferences.

---

## 6.12 GNT-012 — Gantt export

| Item | Value |
|---|---|
| Future capability | Export Gantt timeline to PDF/Excel/image |
| Current state | Missing |
| Phase 14 target | Defer |
| Classification | `DEFERRED_TO_PHASE_22_REPORTING_EXPORT` |

---

# 7. Entity model TO-BE

If current schema differs, the agent must map actual fields and document gaps.

---

## 7.1 ProjectMilestone — `project_milestone`

Recommended Phase 14 entity.

Required fields:

```text
id UUID PK
project_id UUID NOT NULL
project_phase_id UUID NULL
wbs_node_id UUID NULL
code VARCHAR(100) NULL
name VARCHAR(255) NOT NULL
description TEXT NULL
milestone_date DATE NOT NULL
status VARCHAR(50) NOT NULL
sort_order INT NULL
created_at / created_by
updated_at / updated_by
archived_at / archived_by
version INT
```

Status:

```text
PLANNED
ACHIEVED
MISSED
ARCHIVED
```

Rules:

```text
1. Project must exist and not be ARCHIVED.
2. Milestone date required.
3. Phase/WBS if set must belong to same project.
4. Milestone does not consume capacity.
5. Achieving milestone records achievedAt/achievedBy if fields exist.
```

---

## 7.2 TaskScheduleOverride — `task_schedule_override`

Optional but recommended if drag/drop is implemented.

Required fields:

```text
id UUID PK
project_id UUID NOT NULL
task_id UUID NOT NULL
override_type VARCHAR(50) NOT NULL
manual_start_date DATE NULL
manual_finish_date DATE NULL
manual_due_date DATE NULL
reason TEXT NOT NULL
status VARCHAR(50) NOT NULL
created_at / created_by
updated_at / updated_by
cancelled_at / cancelled_by
version INT
```

Override type:

```text
PIN_START
PIN_FINISH
PIN_RANGE
DUE_DATE_OVERRIDE
```

Status:

```text
ACTIVE
INACTIVE
CANCELLED
ARCHIVED
```

Rules:

```text
1. Task must belong to project.
2. Project must not be ARCHIVED.
3. Date range valid.
4. Only one active override per task unless multiple override model exists.
5. Override reason required.
6. Override is audited.
```

If not implemented, Phase 14 can be read-only Gantt + dependency edit.

---

## 7.3 GanttViewPreference — optional/deferred

```text
gantt_view_preference
```

Fields:

```text
id UUID PK
project_id UUID NOT NULL
user_id UUID NOT NULL
zoom_level VARCHAR(50) NULL
date_from DATE NULL
date_to DATE NULL
collapsed_node_ids JSONB NULL
visible_columns_json JSONB NULL
show_unscheduled BOOLEAN NOT NULL DEFAULT true
show_critical_path BOOLEAN NOT NULL DEFAULT false
created_at / updated_at
```

Classification:

```text
DEFERRED unless UI requires.
```

---

## 7.4 No GanttItem table by default

Recommended:

```text
Do not persist GanttItem as source table.
Generate GanttItem DTO from project/schedule records.
```

Reason:

```text
Avoid data drift.
```

Persist only:

```text
milestones
manual overrides
view preferences
```

---

# 8. API TO-BE list

All APIs use `/api`.

---

## 8.1 Gantt view APIs

```text
GET /api/projects/{projectId}/gantt
GET /api/projects/{projectId}/gantt/items
GET /api/projects/{projectId}/gantt/dependencies
GET /api/projects/{projectId}/gantt/issues
```

Query params:

```text
scheduleRunId
dateFrom
dateTo
includeUnscheduled
includeArchived
groupBy=PHASE|WBS|ASSIGNEE
```

Response should include:

```text
project
scheduleRun
items
dependencies
issues
summary
```

---

## 8.2 Gantt recalculation API

```text
POST /api/projects/{projectId}/gantt/recalculate
```

Request:

```json
{
  "planningStartDate": "2026-08-01",
  "planningEndDate": "2026-10-31",
  "markAsCurrent": true
}
```

Behavior:

```text
Delegates to Phase 13 schedule run.
Returns new scheduleRunId and updated Gantt summary.
```

---

## 8.3 Milestone APIs

```text
POST  /api/projects/{projectId}/milestones
GET   /api/projects/{projectId}/milestones
GET   /api/projects/{projectId}/milestones/{milestoneId}
PUT   /api/projects/{projectId}/milestones/{milestoneId}
PATCH /api/projects/{projectId}/milestones/{milestoneId}/achieve
PATCH /api/projects/{projectId}/milestones/{milestoneId}/archive
```

If milestones deferred:

```text
Document and omit APIs.
```

---

## 8.4 Gantt task move/resize APIs

If manual override implemented:

```text
POST /api/projects/{projectId}/gantt/tasks/{taskId}/move
POST /api/projects/{projectId}/gantt/tasks/{taskId}/resize
POST /api/projects/{projectId}/gantt/tasks/{taskId}/clear-override
```

Move request:

```json
{
  "manualStartDate": "2026-08-05",
  "manualFinishDate": "2026-08-09",
  "reason": "Client dependency requires this timing",
  "recalculate": true
}
```

Resize request:

```json
{
  "manualFinishDate": "2026-08-12",
  "reason": "Extended timeline after planning review",
  "recalculate": true
}
```

If no manual override:

```text
DEFERRED_TO_PHASE_14_FOLLOWUP
```

---

## 8.5 Gantt dependency APIs

Recommended wrapper over TaskDependency:

```text
POST   /api/projects/{projectId}/gantt/dependencies
DELETE /api/projects/{projectId}/gantt/dependencies/{dependencyId}
```

Request:

```json
{
  "predecessorTaskId": "uuid",
  "successorTaskId": "uuid",
  "dependencyType": "FINISH_TO_START",
  "lagDays": 0,
  "recalculate": true
}
```

Rules:

```text
Must reuse TaskDependency create/remove.
```

---

## 8.6 View preference APIs

Optional/deferred:

```text
GET /api/projects/{projectId}/gantt/preferences
PUT /api/projects/{projectId}/gantt/preferences
```

---

# 9. Authorization requirements

Required IAM authorities:

```text
GANTT_VIEW
GANTT_RECALCULATE
GANTT_CREATE_MILESTONE
GANTT_UPDATE_MILESTONE
GANTT_ARCHIVE_MILESTONE
GANTT_MOVE_TASK
GANTT_RESIZE_TASK
GANTT_CLEAR_OVERRIDE
GANTT_EDIT_DEPENDENCY
GANTT_MANAGE_VIEW_PREFERENCE optional
```

Simplified mapping allowed:

```text
GANTT_VIEW → PROJECT_VIEW + PROJECT_TASK_VIEW + PROJECT_WBS_VIEW
GANTT_RECALCULATE → TASK_SCHEDULE_RECALCULATE
GANTT_EDIT_DEPENDENCY → PROJECT_TASK_DEPENDENCY_CREATE/REMOVE
GANTT_MOVE_TASK / RESIZE → TASK_SCHEDULE_OVERRIDE if implemented
```

Rules:

```text
1. All Gantt APIs require authenticated user.
2. User must have active workspace access.
3. User must have project view permission.
4. Editing dependency requires task dependency permission.
5. Moving/resizing requires stronger schedule override permission.
6. Milestone mutation requires milestone/Gantt update permission.
7. Gantt response must not include finance/quote fields.
```

---

# 10. Gantt projection rules

## 10.1 Item hierarchy

Recommended hierarchy:

```text
Project
  Phase
    WBS node tree
      Task
      Milestone
```

Alternative grouping:

```text
Project
  WBS tree
    Task
```

API may support `groupBy`.

## 10.2 Project item span

```text
projectStart =
min(project.plannedStartDate, min(taskSchedule.estimatedStartDate), min(milestoneDate))
```

```text
projectEnd =
max(project.plannedEndDate, max(taskSchedule.estimatedFinishDate), max(milestoneDate))
```

If no schedule:

```text
Use project planned dates if available.
```

## 10.3 Phase item span

```text
phaseStart =
min(phase.plannedStartDate, min(task schedules in phase), min(milestones in phase))
```

```text
phaseEnd =
max(phase.plannedEndDate, max(task schedules in phase), max(milestones in phase))
```

## 10.4 WBS node span

```text
wbsStart =
min(task schedule start for tasks under subtree)
```

```text
wbsEnd =
max(task schedule finish for tasks under subtree)
```

If no scheduled child:

```text
WBS node appears as unscheduled/group row.
```

## 10.5 Task item date

Use:

```text
TaskSchedule.estimatedStartDate
TaskSchedule.estimatedFinishDate
```

If absent:

```text
Item status = UNSCHEDULED
```

## 10.6 Milestone date

Use:

```text
ProjectMilestone.milestoneDate
```

Milestone duration:

```text
zero duration marker
```

---

# 11. Manual adjustment rules

Only apply if TaskScheduleOverride is implemented.

## 11.1 Move rule

```text
Move changes manual start/finish override, not Task.estimateHours.
```

Rules:

```text
1. Task must exist and belong to project.
2. Project must not be ARCHIVED.
3. manualFinishDate >= manualStartDate.
4. Reason required.
5. User must have GANTT_MOVE_TASK.
6. Override audited.
7. Recalculate schedule if requested.
8. Scheduler must respect active override.
```

## 11.2 Resize rule

```text
Resize changes manual finish date or override range.
```

Rules:

```text
1. Resize does not automatically change estimateHours.
2. If user wants estimate change, call Task update estimate endpoint.
3. Resize can create schedule/capacity issue.
4. User must have GANTT_RESIZE_TASK.
```

## 11.3 Clear override rule

```text
Clear override cancels active TaskScheduleOverride.
```

Rules:

```text
1. User must have GANTT_CLEAR_OVERRIDE.
2. Recalculate schedule after clearing if requested.
3. Audit event created.
```

---

# 12. Dependency edit rules

Gantt dependency edits must reuse TaskDependency rules.

Rules:

```text
1. Predecessor and successor must belong to project.
2. predecessor != successor.
3. Duplicate dependency blocked.
4. Dependency cycle blocked.
5. Dependency type supported or stored with warning.
6. After edit, schedule recalculation recommended.
7. Event GANTT_DEPENDENCY_CREATED / REMOVED or TASK_DEPENDENCY_CREATED / REMOVED emitted.
```

---

# 13. Milestone rules

If ProjectMilestone implemented:

```text
1. Project must exist and not be ARCHIVED.
2. Name required.
3. milestoneDate required.
4. Phase if set must belong to same project.
5. WBS node if set must belong to same project.
6. Milestone does not consume capacity.
7. Achieving milestone requires permission.
8. Archived milestone hidden by default.
9. Milestone appears on Gantt.
10. Milestone is not baseline approval.
```

---

# 14. Integration with Phase 13 Scheduling

Phase 14 must delegate scheduling to Phase 13.

Rules:

```text
1. Gantt recalculation creates ScheduleRun through Phase 13.
2. Gantt projection reads TaskSchedule from selected ScheduleRun.
3. If Gantt manual override exists, Phase 13 scheduler must read it.
4. Gantt must not implement duplicate scheduling logic.
5. SchedulingIssue appears as Gantt issue marker.
```

If scheduler does not yet support override:

```text
Manual move/resize must be deferred.
```

---

# 15. Integration with Baseline / Change Request

Phase 14 is forecast.

Phase 19 baseline/change request will handle:

```text
approved schedule baseline
baseline comparison
change approval before schedule mutation after baseline
restore baseline
```

Phase 14 must not claim baseline exists.

If a project has approved baseline in future:

```text
Gantt edits may require ChangeRequest.
```

Deferred to Phase 19.

---

# 16. Integration with Finance / Quote

Phase 14 must not calculate:

```text
cost
margin
profit
contract value
quote timeline commitment
billing milestone
```

Future:

```text
Phase 17 may show cost phasing on timeline.
Phase 18 may use schedule forecast for quote.
```

Gantt response in Phase 14 should not include finance fields.

---

# 17. Integration with Notifications

Phase 14 can emit events.

Notification rules deferred to Phase 20.

Possible future notifications:

```text
GANTT_TASK_MOVED_NOTIFICATION
GANTT_DEPENDENCY_CHANGED_NOTIFICATION
MILESTONE_MISSED_NOTIFICATION
SCHEDULE_RECALCULATED_NOTIFICATION
```

Do not spam notifications in Phase 14.

---

# 18. Integration with AI Agent

Phase 14 does not implement AI optimization.

Future Phase 21 can suggest:

```text
move task
resize task
change dependency
change allocation
add milestone
```

AI suggestions must not auto-apply without human approval.

---

# 19. Event Registry integration

Recommended source system:

```text
SCOPERY_GANTT
```

If source systems are limited:

```text
SCOPERY_PROJECT
```

Required events:

```text
GANTT_VIEW_GENERATED
GANTT_RECALCULATION_REQUESTED
GANTT_RECALCULATED

GANTT_TASK_MOVED
GANTT_TASK_RESIZED
GANTT_TASK_OVERRIDE_CLEARED

GANTT_DEPENDENCY_CREATED
GANTT_DEPENDENCY_REMOVED

PROJECT_MILESTONE_CREATED
PROJECT_MILESTONE_UPDATED
PROJECT_MILESTONE_ACHIEVED
PROJECT_MILESTONE_ARCHIVED

GANTT_ISSUE_MARKER_CREATED
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
milestone.id
milestone.name
dependency.id
manualStartDate
manualFinishDate
milestoneDate
occurredAt
traceId
```

---

# 20. Platform audit/outbox/idempotency integration

## 20.1 Activity log actions

```text
GANTT_RECALCULATED
GANTT_TASK_MOVED
GANTT_TASK_RESIZED
GANTT_TASK_OVERRIDE_CLEARED
GANTT_DEPENDENCY_CREATED
GANTT_DEPENDENCY_REMOVED
PROJECT_MILESTONE_CREATED
PROJECT_MILESTONE_UPDATED
PROJECT_MILESTONE_ACHIEVED
PROJECT_MILESTONE_ARCHIVED
```

## 20.2 Audit-sensitive actions

Audit:

```text
Manual task move
Manual task resize
Task override cleared
Dependency created/removed
Milestone achieved/archived
Gantt recalculation marked current schedule
```

Reason:

```text
These actions can affect project delivery commitments.
```

## 20.3 Idempotency

Recommended for:

```text
POST /api/projects/{projectId}/gantt/recalculate
POST /api/projects/{projectId}/gantt/tasks/{taskId}/move
POST /api/projects/{projectId}/gantt/tasks/{taskId}/resize
POST /api/projects/{projectId}/gantt/dependencies
POST /api/projects/{projectId}/milestones
```

Same idempotency key should not create duplicate milestone/dependency/override/run.

## 20.4 Outbox

Gantt events should use platform outbox if available.

---

# 21. Business rules master

## 21.1 Gantt projection rules

```text
GNT-VIEW-001 Project must exist.
GNT-VIEW-002 User must have Gantt view permission.
GNT-VIEW-003 Projection uses latest completed schedule run by default.
GNT-VIEW-004 scheduleRunId if supplied must belong to project.
GNT-VIEW-005 Projection does not expose finance/quote fields.
GNT-VIEW-006 Unscheduled tasks are represented safely.
GNT-VIEW-007 Projection does not persist GanttItem as source of truth.
```

## 21.2 Gantt recalculation rules

```text
GNT-REC-001 Project must not be ARCHIVED.
GNT-REC-002 User must have GANTT_RECALCULATE.
GNT-REC-003 Recalculation delegates to Phase 13 scheduling.
GNT-REC-004 Successful recalculation emits event.
GNT-REC-005 Recalculation does not mutate task status.
```

## 21.3 Milestone rules

```text
GNT-MIL-001 Milestone name required.
GNT-MIL-002 Milestone date required.
GNT-MIL-003 Milestone project must not be archived.
GNT-MIL-004 Phase/WBS reference must belong to same project.
GNT-MIL-005 Milestone consumes no capacity.
GNT-MIL-006 Archived milestone hidden by default.
GNT-MIL-007 Achieve milestone audited.
```

## 21.4 Manual override rules

```text
GNT-OVR-001 Task must belong to project.
GNT-OVR-002 Project must not be ARCHIVED.
GNT-OVR-003 Manual date range must be valid.
GNT-OVR-004 Override reason required.
GNT-OVR-005 Only one active override per task unless model supports more.
GNT-OVR-006 Override does not change estimateHours.
GNT-OVR-007 Override audited.
GNT-OVR-008 Scheduler must respect active override if implemented.
```

## 21.5 Dependency edit rules

```text
GNT-DEP-001 Predecessor task belongs to project.
GNT-DEP-002 Successor task belongs to project.
GNT-DEP-003 predecessor != successor.
GNT-DEP-004 Duplicate blocked.
GNT-DEP-005 Cycle blocked.
GNT-DEP-006 Recalculation recommended after edit.
```

---

# 22. Error catalog requirements

Exact names follow project convention, but these concepts must exist.

```text
GANTT_PROJECT_NOT_FOUND
GANTT_PROJECT_ARCHIVED
GANTT_ACCESS_DENIED
GANTT_SCHEDULE_RUN_NOT_FOUND
GANTT_SCHEDULE_RUN_PROJECT_MISMATCH
GANTT_NO_COMPLETED_SCHEDULE_RUN
GANTT_PROJECTION_FAILED

PROJECT_MILESTONE_NOT_FOUND
PROJECT_MILESTONE_PATH_MISMATCH
PROJECT_MILESTONE_INVALID_DATE
PROJECT_MILESTONE_PHASE_MISMATCH
PROJECT_MILESTONE_WBS_MISMATCH
PROJECT_MILESTONE_ARCHIVED

GANTT_TASK_NOT_FOUND
GANTT_TASK_PATH_MISMATCH
GANTT_TASK_MOVE_NOT_SUPPORTED
GANTT_TASK_RESIZE_NOT_SUPPORTED
GANTT_TASK_OVERRIDE_NOT_FOUND
GANTT_TASK_OVERRIDE_INVALID_DATE_RANGE
GANTT_TASK_OVERRIDE_REASON_REQUIRED
GANTT_TASK_OVERRIDE_CONFLICT

GANTT_DEPENDENCY_DUPLICATE
GANTT_DEPENDENCY_SELF_NOT_ALLOWED
GANTT_DEPENDENCY_CYCLE_DETECTED
GANTT_DEPENDENCY_PROJECT_MISMATCH

GANTT_RECALCULATION_FAILED
```

---

# 23. Required tests

Phase 14 is incomplete without tests.

---

## 23.1 Gantt projection tests

```text
getGantt_validProject_returnsItemsAndDependencies
getGantt_withoutPermission_forbidden
getGantt_crossWorkspace_forbidden
getGantt_latestCompletedScheduleRun_usedByDefault
getGantt_specificScheduleRun_success
getGantt_scheduleRunFromOtherProject_rejected
getGantt_unscheduledTasks_includedWhenRequested
getGantt_noFinanceFields_returned
ganttProjectSpan_usesProjectAndTaskDates
ganttPhaseSpan_usesPhaseAndTaskDates
ganttWbsSpan_usesSubtreeTaskDates
ganttTaskItem_usesTaskScheduleDates
```

## 23.2 Milestone tests

```text
createMilestone_valid_success
createMilestone_archivedProject_rejected
createMilestone_phaseDifferentProject_rejected
createMilestone_wbsDifferentProject_rejected
updateMilestone_valid_success
achieveMilestone_valid_success
archiveMilestone_valid_success
milestoneAppearsInGantt
milestoneDoesNotConsumeCapacity
```

If milestones deferred:

```text
Mark these tests deferred.
```

## 23.3 Recalculation tests

```text
ganttRecalculate_valid_createsScheduleRun
ganttRecalculate_withoutPermission_forbidden
ganttRecalculate_archivedProject_rejected
ganttRecalculate_delegatesToPhase13
ganttRecalculate_markAsCurrent_success
ganttRecalculate_eventEmitted
ganttRecalculate_idempotency_noDuplicateRun
```

## 23.4 Manual override tests

If override implemented:

```text
moveTask_valid_createsOverride
moveTask_withoutPermission_forbidden
moveTask_invalidDateRange_rejected
moveTask_reasonMissing_rejected
moveTask_archivedProject_rejected
moveTask_doesNotChangeEstimateHours
moveTask_recalculate_success
resizeTask_valid_updatesOverride
clearOverride_valid_success
schedulerRespectsActiveOverride
overrideAudited
```

If deferred:

```text
moveTask_returnsNotSupportedOrEndpointOmitted
```

## 23.5 Dependency edit tests

```text
ganttCreateDependency_valid_success
ganttCreateDependency_withoutPermission_forbidden
ganttCreateDependency_duplicate_conflict
ganttCreateDependency_self_rejected
ganttCreateDependency_crossProject_rejected
ganttCreateDependency_cycle_rejected
ganttRemoveDependency_valid_success
ganttDependencyEdit_recalculateIfRequested
```

## 23.6 Authorization tests

```text
viewGantt_withoutGanttView_forbidden
moveTask_withoutGanttMove_forbidden
resizeTask_withoutGanttResize_forbidden
createMilestone_withoutPermission_forbidden
editDependency_withoutPermission_forbidden
```

## 23.7 Event/seeder tests

```text
ganttEventSeeder_firstRun_createsDefinitions
ganttEventSeeder_secondRun_noDuplicates
ganttPermissionSeeder_authoritiesExist
ganttTaskMoved_eventEmitted
milestoneCreated_eventEmitted
```

---

# 24. Manual verification checklist

Completion file must include:

```text
1. Run schedule calculation for project.
2. Open Gantt API and confirm phases/WBS/tasks appear.
3. Confirm task dates come from TaskSchedule.
4. Confirm dependencies appear from TaskDependency.
5. Create milestone and confirm appears in Gantt.
6. Confirm unscheduled task appears with UNSCHEDULED status.
7. Recalculate Gantt and confirm new ScheduleRun.
8. If drag/drop implemented: move task and confirm override/audit/recalculation.
9. If resize implemented: resize task and confirm estimateHours unchanged.
10. Create dependency from Gantt and confirm TaskDependency created.
11. Try dependency cycle and confirm rejection.
12. Confirm Gantt response has no finance/quote/baseline data.
```

---

# 25. Acceptance criteria

Phase 14 is accepted only if:

```text
1. Current Gantt capability is classified against TO-BE.
2. Gantt projection API implemented/tested.
3. Gantt view uses ProjectPhase/WBS/Task/TaskSchedule/TaskDependency.
4. Gantt does not persist independent source-of-truth items.
5. Milestone decision implemented or deferred with reason.
6. Recalculation delegates to Phase 13 scheduling.
7. Dependency editing uses TaskDependency actions.
8. Manual move/resize implemented with override model or explicitly deferred.
9. Authorization implemented/tested.
10. Events seeded idempotently.
11. Activity/audit/outbox integration follows Phase 04.
12. Gantt does not falsely claim finance/quote/baseline/timesheet/AI optimization.
13. mvn compile passes.
14. mvn test passes.
15. Completion file exists and includes gap matrix.
```

Agent must not mark complete if:

```text
mvn test fails
Gantt has separate source-of-truth records that drift from tasks
Gantt projection ignores ScheduleRun/TaskSchedule
Gantt edit bypasses Project/Task/Dependency validations
Manual move changes estimateHours silently
Dependency cycle is accepted
Gantt response leaks finance fields
Gantt claims baseline/change request exists
```

---

# 26. Required phase completion file

Agent must create:

```text
docs/phase-complete/PHASE_14_WBS_GANTT_TO_BE_COMPLETE.md
```

Required sections:

```text
# Phase 14 — WBS-driven Gantt TO-BE Complete

## 1. Summary
## 2. Source Inputs Reviewed
## 3. Current vs TO-BE Classification Matrix
## 4. Implemented in Current BE
## 5. Implemented / Hardened in This Phase
## 6. Seed-only Items Added
## 7. Deferred Items and Target Phase
## 8. Gantt Source-of-truth Decision
## 9. Entity Mapping
## 10. API Changes
## 11. Gantt Projection Rules
## 12. Milestone Decision
## 13. Manual Move/Resize Decision
## 14. Dependency Edit Strategy
## 15. Recalculation Strategy
## 16. Authorization Matrix
## 17. Event Registry Seeder Matrix
## 18. Activity / Audit / Outbox Notes
## 19. Idempotency Strategy
## 20. Tests Added
## 21. Commands Run
## 22. Test Results
## 23. Manual Verification
## 24. Assumptions
## 25. Deviations From Prompt
## 26. Known Risks
## 27. Future Phases That Must Return to Gantt
```

---

# 27. Future phases that must return to Gantt

## 27.1 Phase 15 — Rate Card / CCH

May overlay role/cost information later, but not in Phase 14.

## 27.2 Phase 16 — Estimation Roll-up

May add WBS/phase effort roll-up to Gantt summary.

## 27.3 Phase 17 — Finance

May add cost phasing, budget burn forecast, or margin timeline overlays.

Must require finance permission.

## 27.4 Phase 18 — Quote

May use Gantt forecast as quote schedule basis.

Quote approval required before commercial commitment.

## 27.5 Phase 19 — Baseline / Change Request

Must add:

```text
baseline Gantt comparison
approved baseline dates
variance markers
change request required after baseline
```

## 27.6 Phase 20 — Project Notifications

Must notify:

```text
task moved
milestone missed
schedule recalculated
dependency changed
```

according to subscription/preferences.

## 27.7 Phase 21 — AI-assisted Planning

May suggest:

```text
schedule adjustment
dependency optimization
allocation change
risk mitigation
```

AI must not auto-apply.

## 27.8 Phase 22 — Reporting / Export

Must add:

```text
Gantt export
schedule risk report
timeline dashboard
PDF/Excel export
```

## 27.9 Phase 23 — Core Hardening

Must review:

```text
large Gantt performance
deep WBS projection
timezone correctness
drag/drop concurrency
authorization coverage
baseline conflict readiness
```

---

# 28. Agent anti-bịa rules

The agent must not:

```text
1. Claim Gantt is source of truth.
2. Create disconnected Gantt tasks that drift from Project Task.
3. Bypass TaskDependency validation when editing Gantt dependency.
4. Bypass Phase 13 scheduling during recalculation.
5. Change estimateHours silently from task resize.
6. Claim baseline exists.
7. Claim change request exists.
8. Claim cost/margin/quote exists.
9. Claim AI schedule optimization exists.
10. Leak finance fields in Gantt response.
11. Hide manual move/resize decision.
12. Hide milestone decision.
```

---

# 29. Prompt to give coding agent

```text
You are implementing Phase 14 — TO-BE WBS-driven Gantt, Timeline Projection, Milestone & Manual Schedule Adjustment.

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
- Phase 13 Task Scheduling Engine TO-BE spec
- Current backend code, migrations, tests

Your task:
1. Compare current Gantt/timeline capability against this Phase 14 TO-BE spec.
2. Classify each capability as CURRENTLY_IMPLEMENTED, PARTIALLY_IMPLEMENTED, MUST_IMPLEMENT_IN_PHASE_14, SEED_ONLY_IN_PHASE_14, DEFERRED_TO_PHASE_XX, or NOT_IN_SCOPE_FOR_BACKEND_NOW.
3. Implement only Phase 14 required items.
4. Implement Gantt projection from ProjectPhase, WBS, Task, TaskDependency, and TaskSchedule.
5. Implement or explicitly defer ProjectMilestone.
6. Implement Gantt recalculation by delegating to Phase 13.
7. Implement dependency edits through TaskDependency actions.
8. Implement manual task move/resize only if TaskScheduleOverride is added and scheduler supports it; otherwise explicitly defer.
9. Add Gantt permissions and event definitions.
10. Add tests listed in this spec.
11. Run mvn compile and mvn test.
12. Create docs/phase-complete/PHASE_14_WBS_GANTT_TO_BE_COMPLETE.md with full gap matrix.

Do not implement or claim finance/cost, quote, baseline/change request, timesheet actuals, or AI optimization in this phase.
```

---

# 30. Quick tracking matrix

| Capability | Current backend | Phase 14 action | Later phase |
|---|---|---|---|
| Gantt projection | Missing/unknown | Must implement | — |
| Gantt items DTO | Missing/unknown | Must implement | — |
| Gantt dependency DTO | Missing/unknown | Must implement | — |
| Milestone | Missing/unknown | Implement or defer | Phase 19 if deferred |
| Gantt recalculation | Missing | Must delegate Phase 13 | — |
| Task move | Missing | Implement override or defer | Phase 14 follow-up |
| Task resize | Missing | Implement override or defer | Phase 14 follow-up |
| Dependency edit | TaskDependency exists | Must wrap/reuse | — |
| WBS row move | WBS move exists | Reuse | — |
| Critical path | Missing | Defer | Phase 23/Post-23 |
| Gantt preferences | Missing | Defer | Post-23 |
| Gantt export | Missing | Defer | Phase 22 |
| Baseline comparison | Missing | Defer | Phase 19 |
| Cost overlay | Missing | Defer | Phase 17 |
| Quote timeline | Missing | Defer | Phase 18 |
| AI schedule optimization | Missing | Defer | Phase 21 |

---

# 31. Final principle

Phase 14 is not complete when "a timeline endpoint returns tasks."

Phase 14 is complete when Scopery can show a trustworthy Gantt projection:

```text
Project phases
+ WBS tree
+ Tasks
+ Dependencies
+ Schedule forecast
+ Milestones
+ Issues
= Gantt view
```

Gantt is a view and control surface.

Gantt is not the project source of truth.

Gantt edits must flow back through task, dependency, schedule, and authorization rules.
