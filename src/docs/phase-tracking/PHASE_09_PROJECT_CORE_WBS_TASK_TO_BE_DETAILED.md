# PHASE 09 — TO-BE Project Core, Phase, WBS, Task & Dependency Planning Foundation

> Project: Scopery Backend  
> Phase: 09  
> Document type: TO-BE implementation-grade specification  
> Status: Planning / Gap-driven implementation spec  
> Depends on: Phase 00 — Master Backend Roadmap, Phase 01 — API Path & Security Baseline, Phase 02 — IAM Core, Phase 03 — Workspace Core, Phase 04 — Platform Audit / Outbox / Idempotency, Phase 05 — Event Registry, Phase 06 — Notification Engine, Phase 07 — AI Agent Platform, Phase 08 — Knowledge / Document Type Catalog  
> API base: `/api`  
> Primary module: `modules/project`  
> Related modules: `workspace`, `iam`, `eventregistry`, `notification`, future `capacity`, `scheduling`, `gantt`, `finance`, `quote`, `baseline`, `change-request`, `aiagent`, `reporting`  
> Important rule: This file is **not an as-is description**. It defines the TO-BE project foundation, compares it to current Project module capability, and explicitly defers capacity, scheduling, Gantt, finance, quote, baseline, change request, agile, and AI planning features to later phases.

---

# 0. Purpose of this file

Phase 09 defines the core Project Planning foundation for Scopery.

This phase is about the **source-of-truth project structure**, not about all project intelligence.

Phase 09 must establish:

```text
Project
Project lifecycle
Project phase instances
Phase definition reference
WBS tree
WBS hierarchy and ordering
Task
Task estimate hours
Task due date
Task assignee / in-charge
Task status lifecycle
Task dependency
Basic task planning constraints
Events
Activity/audit
Tests
```

Phase 09 must not pretend that the following are implemented:

```text
Capacity scheduling
Estimated finish date
Gantt chart
Drag/drop Gantt editing
Rate card / CCH
Cost roll-up
Margin/profitability
Quote/contract value solver
Baseline/change request
Agile backlog/sprint/epic
Document deliverables
AI-generated project plan
```

Those are later phases.

---

# 1. Source inputs

Before coding Phase 09, the agent must read:

```text
1. Current backend codebase
2. Current BE feature/entity/business-rule inventory
3. Dynamic Work OS future-state feature catalog
4. Phase 00 master roadmap
5. Phase 01 API/security baseline
6. Phase 02 IAM TO-BE spec
7. Phase 03 Workspace TO-BE spec
8. Phase 04 Platform Audit/Outbox/Idempotency spec
9. Phase 05 Event Registry TO-BE spec
10. Phase 06 Notification TO-BE spec
11. Phase 07 AI Agent Platform TO-BE spec
12. Phase 08 Knowledge / Document Type Catalog TO-BE spec
13. Existing project module code, migrations, seeders, tests
14. Existing workspace membership integration services
15. Existing project authorization services if already present
16. Existing Project / Phase / WBS / Task controllers/actions/query services
```

The agent must not implement from current code only.

---

# 2. Current backend snapshot

Current backend inventory shows Project module currently has:

```text
7 action-level function groups
42 business rules
```

Current project-related entities are expected to include:

```text
Project
ProjectPhase
PhaseDefinition
WbsNode
Task
TaskDependency
```

Existing project baseline from current work:

```text
Project tables:
- project_phase_definition
- project_project
- project_project_phase
- project_wbs_node
- project_task
- project_task_dependency
```

Current project behavior likely includes:

```text
Project create/update/activate/archive
Phase definition catalog
Project phase create/update/activate/complete/archive
WBS node create/update/move/archive
Task create/update/start/block/complete/cancel/archive
Task dependency create/remove
Estimate hours validation
Assignee/inCharge active workspace member validation
Workspace-scoped project authorization integration may already exist or is handled by Phase 10
```

This is a strong foundation, but it is not full Work OS project management.

---

# 3. Future-state project-related modules

The full Work OS has several project-related future modules:

```text
Project Setup and Governance
Scope, Deliverable, and Baseline Management
Work Item and Backlog Management
Agile Delivery
Schedule, Gantt, and Milestone Management
Resource and Capacity Management
Time, Attendance, and Expense
Estimation and Planning
Quotation and Commercial Proposal
Rate Card and Cost Policy
Project Budget and Profitability
Contract, Billing, and Revenue
Change Request and Change Order
RAID and Decision Management
Quality and Test Management
Release and Deployment Management
AI-assisted Project Planning
Reporting and Portfolio Intelligence
```

Phase 09 covers only the foundation needed by those modules.

---

# 4. Classification labels

Every requirement uses one label:

| Label | Meaning |
|---|---|
| `CURRENTLY_IMPLEMENTED` | Current backend already implements it. Agent must verify and test. |
| `MUST_IMPLEMENT_IN_PHASE_09` | Must be implemented or hardened now. |
| `SEED_ONLY_IN_PHASE_09` | Create seed definitions/permissions/events now; full feature later. |
| `DEFERRED_TO_PHASE_XX` | Do not implement now; later phase must return. |
| `DEFERRED_TO_POST_23_BACKLOG` | Outside core 00–23 roadmap, part of full Work OS expansion. |
| `NOT_IN_SCOPE_FOR_BACKEND_NOW` | Explicitly not backend scope now. |

---

# 5. Phase 09 target statement

Phase 09 must deliver a future-ready project foundation:

```text
1. Project belongs to exactly one active workspace.
2. Project lifecycle is stable and auditable.
3. Project phase instances can be created from or linked to PhaseDefinition.
4. Task belongs to exactly one project and exactly one project phase.
5. WBS is the project scope source of truth.
6. WBS tree supports parent/child hierarchy, ordering, move, and archive.
7. Task can be linked to WBS node.
8. Task has estimateHours separate from duration/calendar scheduling.
9. Task has dueDate as deadline, not computed schedule.
10. Task can have assignee/inCharge validated as active workspace member.
11. TaskDependency captures logical dependency, but does not compute schedule yet.
12. Events/activity/audit are emitted consistently.
13. API, tests, and completion report are honest about deferred advanced modules.
```

---

# 6. Project source-of-truth decision

This decision is mandatory.

```text
Scope source of truth: WBS
Execution item source of truth: Task
Phase classification: ProjectPhase
Timeline projection: future Gantt/Scheduling
Cost source: future estimate × rate card
Capacity source: future ResourceCalendar
Finance source: future ProjectFinance
```

Therefore:

```text
WBS is not Gantt.
Task is not schedule.
EstimateHours is not duration.
DueDate is not finishDate.
Dependency is not automatically scheduled yet.
```

This distinction prevents bad implementation in later phases.

---

# 7. Phase 09 implementation scope

## 7.1 Must implement / harden now

```text
Project entity/lifecycle
PhaseDefinition catalog read/use
ProjectPhase entity/lifecycle
WBS node tree and move/archive
Task entity/lifecycle
Task estimateHours validation
Task dueDate field
Task inChargeUserId / assignee validation against active workspace member
TaskDependency create/remove
Project event definitions
Project activity/audit
Project seed data if applicable
Tests
Completion gap matrix
```

## 7.2 Do not implement now

```text
Capacity calculation
Resource calendar
Estimated finish date
Automatic scheduling
Critical path
Gantt projection
Gantt drag/drop
Milestone engine
Rate card / CCH
Task cost calculation
Project budget/margin
Quote generation
Baseline snapshot
Change request approval
Agile sprint/backlog
Timesheet
Expense
Risk/issue/decision log
Document deliverable repository
AI project plan generation
Portfolio/program hierarchy
```

---

# 8. TO-BE capability matrix

---

## 8.1 PRJ-001 — Project creation

| Item | Value |
|---|---|
| Future capability | Create governed project under workspace with owner, metadata, lifecycle, permissions |
| Current state | Project create exists |
| Phase 09 target | Verify/harden |
| Classification | `MUST_IMPLEMENT_IN_PHASE_09` |

Required business rules:

```text
1. Workspace must exist.
2. Workspace must be ACTIVE.
3. Actor must have PROJECT_CREATE on workspace.
4. Project code required.
5. Project code normalized uppercase.
6. Project code unique within workspace.
7. Project name required.
8. Project status starts DRAFT or ACTIVE according to current convention.
9. Project owner/inCharge if set must be active workspace member.
10. Project belongs to exactly one workspace.
11. OrganizationId should be denormalized from workspace if current model uses it.
12. Activity log PROJECT_CREATED.
13. Event PROJECT_CREATED.
14. No finance/schedule/baseline automatically created in Phase 09 unless current convention already does so.
```

Recommended status:

```text
DRAFT
ACTIVE
ON_HOLD
COMPLETED
ARCHIVED
```

If current code uses fewer statuses, map and document.

---

## 8.2 PRJ-002 — Project update

| Item | Value |
|---|---|
| Future capability | Update project metadata while preserving identity and integrity |
| Current state | Project update exists |
| Phase 09 target | Verify/harden |
| Classification | `MUST_IMPLEMENT_IN_PHASE_09` |

Updatable in Phase 09:

```text
name
description
plannedStartDate
plannedEndDate
ownerUserId/inChargeUserId if product supports it
priority
tags/metadata if existing
```

Immutable:

```text
workspaceId
organizationId
project code unless product explicitly allows code rename
createdBy
```

Rules:

```text
1. Project must exist.
2. ARCHIVED project cannot be updated.
3. owner/inCharge must be active workspace member.
4. plannedEndDate cannot be before plannedStartDate.
5. Update requires PROJECT_UPDATE.
6. Event PROJECT_UPDATED.
```

---

## 8.3 PRJ-003 — Project lifecycle

| Item | Value |
|---|---|
| Future capability | Activate, hold, complete, archive project safely |
| Current state | Activate/archive exists; complete/hold may not |
| Phase 09 target | Harden core lifecycle; defer advanced closure |
| Classification | `MUST_IMPLEMENT_IN_PHASE_09` for activate/archive; complete/hold optional/current-specific |

Phase 09 minimum statuses:

```text
DRAFT
ACTIVE
ARCHIVED
```

Optional statuses:

```text
ON_HOLD
COMPLETED
CANCELLED
```

Rules:

```text
1. Only DRAFT can activate unless reactivation is supported.
2. ARCHIVED cannot be updated.
3. Archiving project does not delete phases/WBS/tasks.
4. Archiving project blocks new phase/WBS/task/dependency changes.
5. Completing project requires all required tasks/phases complete if current product supports completion.
6. Event PROJECT_ACTIVATED / PROJECT_ARCHIVED.
7. Activity/audit required.
```

Full closeout workflow:

```text
DEFERRED_TO_PHASE_19_BASELINE_CHANGE_REQUEST or Post-23 governance backlog.
```

---

## 8.4 PRJ-004 — PhaseDefinition catalog

| Item | Value |
|---|---|
| Future capability | Define system/workspace phase templates such as Discovery, Design, Build, Test, Deploy |
| Current state | PhaseDefinition exists |
| Phase 09 target | Verify basic catalog; advanced templates in Phase 11 |
| Classification | `MUST_IMPLEMENT_IN_PHASE_09` for read/reference; `DEFERRED_TO_PHASE_11` for template management |

Phase 09 rules:

```text
1. PhaseDefinition can be system-defined or workspace-defined if current schema supports it.
2. ProjectPhase may reference PhaseDefinition.
3. PhaseDefinition status must be ACTIVE to use for new project phase.
4. PhaseDefinition code unique by scope.
5. PhaseDefinition cannot be hard-deleted if referenced by ProjectPhase.
```

Advanced:

```text
Phase template library
Workspace active phase set
Archived/disabled phase catalog
System default ordering
```

Deferred:

```text
DEFERRED_TO_PHASE_11_PROJECT_TEMPLATE_PHASE_CATALOG
```

---

## 8.5 PRJ-005 — ProjectPhase creation

| Item | Value |
|---|---|
| Future capability | Add phase instance to a project |
| Current state | ProjectPhase create exists |
| Phase 09 target | Verify/harden |
| Classification | `MUST_IMPLEMENT_IN_PHASE_09` |

Required rules:

```text
1. Project must exist and not be ARCHIVED.
2. PhaseDefinition must exist and be ACTIVE if provided.
3. ProjectPhase name required.
4. Phase code/name unique within project or order uniqueness according to current design.
5. displayOrder/orderIndex required.
6. Phase status starts PLANNED or ACTIVE according to convention.
7. plannedStartDate/plannedEndDate valid if provided.
8. Phase belongs to exactly one project.
9. Event PROJECT_PHASE_CREATED.
```

Status:

```text
PLANNED
ACTIVE
COMPLETED
ARCHIVED
```

---

## 8.6 PRJ-006 — ProjectPhase update/lifecycle

| Item | Value |
|---|---|
| Future capability | Update, activate, complete, archive phase |
| Current state | ProjectPhase update/activate/complete/archive exists |
| Phase 09 target | Verify/harden |
| Classification | `MUST_IMPLEMENT_IN_PHASE_09` |

Rules:

```text
1. Phase must belong to project in path.
2. Archived project blocks phase changes.
3. Archived phase cannot be updated.
4. Completing phase may require all tasks in phase completed if product rule says so.
5. Archiving phase is blocked if active tasks exist or cascades only if product explicitly supports.
6. Task belongs to one phase; archiving phase must not orphan tasks silently.
7. Event PROJECT_PHASE_UPDATED / PROJECT_PHASE_COMPLETED / PROJECT_PHASE_ARCHIVED.
```

If current product allows archiving phase with tasks:

```text
Document cascade/read-only behavior.
```

---

## 8.7 PRJ-007 — WBS node creation

| Item | Value |
|---|---|
| Future capability | Create hierarchical work breakdown structure node |
| Current state | WbsNode create exists |
| Phase 09 target | Verify/harden |
| Classification | `MUST_IMPLEMENT_IN_PHASE_09` |

Required rules:

```text
1. Project must exist and not be ARCHIVED.
2. Optional parentWbsNodeId must belong to same project.
3. WBS node title/name required.
4. Node type required if current schema supports type.
5. orderIndex required among siblings.
6. Sibling orderIndex uniqueness or stable reorder required.
7. Depth limit enforced if configured.
8. WBS code/path generated or validated.
9. Event WBS_NODE_CREATED.
```

WBS node types:

```text
DELIVERABLE
WORK_PACKAGE
MILESTONE_PLACEHOLDER optional
GROUP
```

Do not implement schedule milestone engine yet.

---

## 8.8 PRJ-008 — WBS node update/move/archive

| Item | Value |
|---|---|
| Future capability | Maintain WBS hierarchy as scope source of truth |
| Current state | WBS update/move/archive exists |
| Phase 09 target | Verify/harden |
| Classification | `MUST_IMPLEMENT_IN_PHASE_09` |

Rules:

```text
1. Node must exist.
2. Node must belong to project in path.
3. Archived project blocks changes.
4. Archived node cannot be updated/moved.
5. Move target parent must belong to same project.
6. Move cannot create cycle.
7. Move cannot place node under itself/descendant.
8. Sibling order updated consistently.
9. Archiving node with children blocked unless cascade explicitly requested.
10. Archiving node with active tasks blocked unless tasks are moved/archived explicitly.
11. Event WBS_NODE_UPDATED / WBS_NODE_MOVED / WBS_NODE_ARCHIVED.
```

Cycle prevention test is mandatory.

---

## 8.9 PRJ-009 — Task creation

| Item | Value |
|---|---|
| Future capability | Create execution task linked to project phase and optionally WBS node |
| Current state | Task create exists |
| Phase 09 target | Verify/harden |
| Classification | `MUST_IMPLEMENT_IN_PHASE_09` |

Required rules:

```text
1. Project must exist and not be ARCHIVED.
2. Task title required.
3. Task belongs to exactly one project.
4. Task belongs to exactly one ProjectPhase.
5. Phase must belong to same project and not be ARCHIVED.
6. Optional WBS node must belong to same project and not be ARCHIVED.
7. estimateHours required and > 0.
8. dueDate optional but if provided must be valid date.
9. inChargeUserId/assigneeUserId optional but if set must be active workspace member.
10. Initial status TODO or PLANNED according to convention.
11. Event TASK_CREATED.
12. Activity log TASK_CREATED.
```

Critical distinction:

```text
estimateHours is effort.
dueDate is deadline.
duration is not computed in Phase 09.
```

---

## 8.10 PRJ-010 — Task update

| Item | Value |
|---|---|
| Future capability | Update task details while preserving planning constraints |
| Current state | Task update exists |
| Phase 09 target | Verify/harden |
| Classification | `MUST_IMPLEMENT_IN_PHASE_09` |

Updatable:

```text
title
description
projectPhaseId
wbsNodeId
estimateHours
dueDate
inChargeUserId / assigneeUserId
priority
status-specific metadata if current convention supports
```

Rules:

```text
1. Task must exist.
2. Task must belong to project in path.
3. Archived project blocks update.
4. Archived/completed/cancelled task update restricted according to status.
5. Changing phase requires target phase same project and not archived.
6. Changing WBS node requires node same project and not archived.
7. Changing assignee requires active workspace member.
8. estimateHours must remain > 0.
9. Event TASK_UPDATED.
```

---

## 8.11 PRJ-011 — Task lifecycle

| Item | Value |
|---|---|
| Future capability | Track task progress lifecycle |
| Current state | Start/block/complete/cancel/archive exists |
| Phase 09 target | Verify/harden |
| Classification | `MUST_IMPLEMENT_IN_PHASE_09` |

Recommended statuses:

```text
TODO
IN_PROGRESS
BLOCKED
COMPLETED
CANCELLED
ARCHIVED
```

Rules:

```text
1. TODO → IN_PROGRESS allowed.
2. IN_PROGRESS → BLOCKED allowed.
3. BLOCKED → IN_PROGRESS allowed.
4. IN_PROGRESS/BLOCKED/TODO → COMPLETED allowed if required fields satisfied.
5. TODO/IN_PROGRESS/BLOCKED → CANCELLED allowed.
6. ARCHIVED is terminal for normal updates.
7. Archived project blocks task lifecycle actions.
8. Completing task sets completedAt/completedBy.
9. Cancelling task sets cancelledAt/cancelledBy.
10. Archiving task sets archivedAt/archivedBy.
11. Event emitted for each lifecycle transition.
```

If current statuses differ, map and document.

---

## 8.12 PRJ-012 — TaskDependency

| Item | Value |
|---|---|
| Future capability | Represent logical task dependency graph |
| Current state | TaskDependency create/remove exists |
| Phase 09 target | Verify/harden dependency graph integrity |
| Classification | `MUST_IMPLEMENT_IN_PHASE_09` |

Required dependency type in Phase 09:

```text
FINISH_TO_START
```

Optional future types:

```text
START_TO_START
FINISH_TO_FINISH
START_TO_FINISH
```

Rules:

```text
1. Predecessor task must exist.
2. Successor task must exist.
3. Both tasks must belong to same project.
4. Predecessor != successor.
5. Duplicate dependency blocked.
6. Creating dependency cannot introduce cycle.
7. Archived/cancelled tasks cannot be used for new dependency unless product allows.
8. Removing dependency must require PROJECT_TASK_UPDATE or TASK_DEPENDENCY_MANAGE.
9. Dependency does not automatically reschedule tasks in Phase 09.
10. Event TASK_DEPENDENCY_CREATED / TASK_DEPENDENCY_REMOVED.
```

Cycle prevention is mandatory.

---

## 8.13 PRJ-013 — Estimate hours

| Item | Value |
|---|---|
| Future capability | Base effort estimate for capacity, cost, and scheduling |
| Current state | estimateHours validation exists |
| Phase 09 target | Harden |
| Classification | `MUST_IMPLEMENT_IN_PHASE_09` |

Rules:

```text
1. estimateHours required for task.
2. estimateHours > 0.
3. Decimal hours allowed if current type is BigDecimal.
4. Minimum increment optional, e.g. 0.25h.
5. estimateHours is effort, not calendar duration.
6. Cost calculation is not in Phase 09.
7. Capacity scheduling is not in Phase 09.
```

Future modules that rely on estimateHours:

```text
Phase 12 Capacity
Phase 13 Scheduling
Phase 15 Rate Card
Phase 16 Estimation Roll-up
Phase 17 Finance
Phase 18 Quote
```

---

## 8.14 PRJ-014 — Due date

| Item | Value |
|---|---|
| Future capability | Record deadline/commitment date |
| Current state | dueDate likely exists |
| Phase 09 target | Harden semantics |
| Classification | `MUST_IMPLEMENT_IN_PHASE_09` |

Rules:

```text
1. dueDate optional unless product requires it.
2. dueDate is deadline, not computed finish date.
3. dueDate before project planned start should warn/reject according to current convention.
4. dueDate after project planned end should warn/reject according to current convention.
5. Due date risk calculation deferred to Phase 13/14/20.
```

If no warning mechanism exists:

```text
Only validate date format; risk warning deferred.
```

---

## 8.15 PRJ-015 — Assignee / In-charge

| Item | Value |
|---|---|
| Future capability | Assign responsible user to task |
| Current state | active workspace member validation was added/hardened |
| Phase 09 target | Verify/harden |
| Classification | `MUST_IMPLEMENT_IN_PHASE_09` |

Rules:

```text
1. inChargeUserId/assigneeUserId optional.
2. If set, user must exist.
3. User must be active workspace member of project workspace.
4. Deactivated workspace member cannot be assigned.
5. Updating assignee validates new user.
6. If assigned user later leaves workspace, later phase must handle reassignment warning.
```

Future:

```text
Team assignment — Phase 12/20 or Work Item phase.
Role-based assignment — Phase 15/16/17.
```

---

# 9. Entity model TO-BE

If current schema differs, agent must map actual fields and document gaps.

---

## 9.1 Project — `project_project`

Required fields:

```text
id UUID PK
workspace_id UUID NOT NULL
organization_id UUID NULL recommended denormalized
code VARCHAR(100) NOT NULL
name VARCHAR(255) NOT NULL
description TEXT NULL
owner_user_id UUID NULL
in_charge_user_id UUID NULL
status VARCHAR(50) NOT NULL
planned_start_date DATE NULL
planned_end_date DATE NULL
priority VARCHAR(50) NULL
metadata_json JSONB NULL
created_at / created_by
updated_at / updated_by
activated_at / activated_by
archived_at / archived_by
version INT
```

Constraints:

```text
unique workspace_id + code
status enum
planned_end_date >= planned_start_date if both provided
```

---

## 9.2 PhaseDefinition — `project_phase_definition`

Required fields:

```text
id UUID PK
code VARCHAR(100) NOT NULL
name VARCHAR(255) NOT NULL
description TEXT NULL
scope VARCHAR(50) NOT NULL
workspace_id UUID NULL
display_order INT NOT NULL DEFAULT 0
status VARCHAR(50) NOT NULL
built_in BOOLEAN NOT NULL DEFAULT false
created_at / created_by
updated_at / updated_by
archived_at / archived_by
version INT
```

Constraints:

```text
unique scope + workspace_id + code
```

Phase 11 will expand this.

---

## 9.3 ProjectPhase — `project_project_phase`

Required fields:

```text
id UUID PK
project_id UUID NOT NULL
phase_definition_id UUID NULL
code VARCHAR(100) NULL
name VARCHAR(255) NOT NULL
description TEXT NULL
display_order INT NOT NULL
status VARCHAR(50) NOT NULL
planned_start_date DATE NULL
planned_end_date DATE NULL
started_at TIMESTAMP NULL
completed_at TIMESTAMP NULL
archived_at TIMESTAMP NULL
created_at / created_by
updated_at / updated_by
version INT
```

Constraints:

```text
unique project_id + display_order
optional unique project_id + code
planned_end_date >= planned_start_date if both provided
```

---

## 9.4 WbsNode — `project_wbs_node`

Required fields:

```text
id UUID PK
project_id UUID NOT NULL
parent_id UUID NULL
code VARCHAR(100) NULL
title VARCHAR(255) NOT NULL
description TEXT NULL
node_type VARCHAR(50) NULL
path TEXT NULL
depth INT NOT NULL DEFAULT 0
order_index INT NOT NULL DEFAULT 0
status VARCHAR(50) NOT NULL
created_at / created_by
updated_at / updated_by
archived_at / archived_by
version INT
```

Constraints:

```text
parent_id references same project
unique project_id + parent_id + order_index if strict order
depth >= 0
```

---

## 9.5 Task — `project_task`

Required fields:

```text
id UUID PK
project_id UUID NOT NULL
project_phase_id UUID NOT NULL
wbs_node_id UUID NULL
code VARCHAR(100) NULL
title VARCHAR(255) NOT NULL
description TEXT NULL
status VARCHAR(50) NOT NULL
priority VARCHAR(50) NULL
estimate_hours DECIMAL(12,2) NOT NULL
due_date DATE NULL
in_charge_user_id UUID NULL
assignee_user_id UUID NULL if separate from inCharge
started_at TIMESTAMP NULL
blocked_at TIMESTAMP NULL
completed_at TIMESTAMP NULL
cancelled_at TIMESTAMP NULL
archived_at TIMESTAMP NULL
created_at / created_by
updated_at / updated_by
version INT
```

Constraints:

```text
estimate_hours > 0
project_phase_id belongs to same project
wbs_node_id belongs to same project
```

---

## 9.6 TaskDependency — `project_task_dependency`

Required fields:

```text
id UUID PK
project_id UUID NOT NULL
predecessor_task_id UUID NOT NULL
successor_task_id UUID NOT NULL
dependency_type VARCHAR(50) NOT NULL
lag_days INT NOT NULL DEFAULT 0
created_at / created_by
```

Constraints:

```text
predecessor_task_id != successor_task_id
unique predecessor_task_id + successor_task_id + dependency_type
both tasks same project
```

Phase 09 recommended dependency type:

```text
FINISH_TO_START
```

Lag scheduling effect:

```text
Stored if current schema supports, but not used for scheduling until Phase 13.
```

---

# 10. API TO-BE list

All APIs use `/api`.

---

## 10.1 Project APIs

```text
POST  /api/projects
GET   /api/projects
GET   /api/projects/{projectId}
PUT   /api/projects/{projectId}
PATCH /api/projects/{projectId}/activate
PATCH /api/projects/{projectId}/archive
```

Optional:

```text
PATCH /api/projects/{projectId}/complete
PATCH /api/projects/{projectId}/hold
PATCH /api/projects/{projectId}/cancel
```

If optional statuses are not implemented, document deferred.

Filters:

```text
workspaceId
status
code
ownerUserId
inChargeUserId
date range
```

---

## 10.2 PhaseDefinition APIs

Phase 09 may provide read/catalog APIs only or basic CRUD if current code has it.

```text
GET /api/phase-definitions
GET /api/phase-definitions/{id}
```

Optional current CRUD:

```text
POST  /api/phase-definitions
PUT   /api/phase-definitions/{id}
PATCH /api/phase-definitions/{id}/activate
PATCH /api/phase-definitions/{id}/archive
```

Advanced phase catalog management:

```text
DEFERRED_TO_PHASE_11
```

---

## 10.3 ProjectPhase APIs

```text
POST  /api/projects/{projectId}/phases
GET   /api/projects/{projectId}/phases
GET   /api/projects/{projectId}/phases/{phaseId}
PUT   /api/projects/{projectId}/phases/{phaseId}
PATCH /api/projects/{projectId}/phases/{phaseId}/activate
PATCH /api/projects/{projectId}/phases/{phaseId}/complete
PATCH /api/projects/{projectId}/phases/{phaseId}/archive
```

---

## 10.4 WBS APIs

```text
POST  /api/projects/{projectId}/wbs-nodes
GET   /api/projects/{projectId}/wbs-nodes
GET   /api/projects/{projectId}/wbs-tree
GET   /api/projects/{projectId}/wbs-nodes/{wbsNodeId}
PUT   /api/projects/{projectId}/wbs-nodes/{wbsNodeId}
POST  /api/projects/{projectId}/wbs-nodes/{wbsNodeId}/move
PATCH /api/projects/{projectId}/wbs-nodes/{wbsNodeId}/archive
```

Move command:

```json
{
  "newParentId": "uuid-or-null",
  "newOrderIndex": 2
}
```

---

## 10.5 Task APIs

```text
POST  /api/projects/{projectId}/tasks
GET   /api/projects/{projectId}/tasks
GET   /api/projects/{projectId}/tasks/{taskId}
PUT   /api/projects/{projectId}/tasks/{taskId}
PATCH /api/projects/{projectId}/tasks/{taskId}/start
PATCH /api/projects/{projectId}/tasks/{taskId}/block
PATCH /api/projects/{projectId}/tasks/{taskId}/complete
PATCH /api/projects/{projectId}/tasks/{taskId}/cancel
PATCH /api/projects/{projectId}/tasks/{taskId}/archive
```

Filters:

```text
projectPhaseId
wbsNodeId
status
assigneeUserId
inChargeUserId
dueDateFrom/dueDateTo
```

---

## 10.6 TaskDependency APIs

```text
POST   /api/projects/{projectId}/task-dependencies
GET    /api/projects/{projectId}/task-dependencies
DELETE /api/projects/{projectId}/task-dependencies/{dependencyId}
```

Request:

```json
{
  "predecessorTaskId": "uuid",
  "successorTaskId": "uuid",
  "dependencyType": "FINISH_TO_START",
  "lagDays": 0
}
```

---

# 11. Authorization requirements

Required IAM authorities:

```text
PROJECT_VIEW
PROJECT_CREATE
PROJECT_UPDATE
PROJECT_ARCHIVE
PROJECT_ACTIVATE
PROJECT_COMPLETE optional

PROJECT_PHASE_VIEW
PROJECT_PHASE_CREATE
PROJECT_PHASE_UPDATE
PROJECT_PHASE_ARCHIVE
PROJECT_PHASE_ACTIVATE
PROJECT_PHASE_COMPLETE

PROJECT_WBS_VIEW
PROJECT_WBS_CREATE
PROJECT_WBS_UPDATE
PROJECT_WBS_MOVE
PROJECT_WBS_ARCHIVE

PROJECT_TASK_VIEW
PROJECT_TASK_CREATE
PROJECT_TASK_UPDATE
PROJECT_TASK_ASSIGN
PROJECT_TASK_STATUS_UPDATE
PROJECT_TASK_ARCHIVE

PROJECT_TASK_DEPENDENCY_VIEW
PROJECT_TASK_DEPENDENCY_CREATE
PROJECT_TASK_DEPENDENCY_REMOVE
```

Current simplification allowed:

```text
Task dependency create/remove may use PROJECT_TASK_UPDATE if no separate authority exists.
WBS move may use PROJECT_WBS_UPDATE if no separate authority exists.
```

Rules:

```text
1. All project endpoints require authenticated user.
2. User must have active workspace access.
3. User must have relevant workspace-scoped project authority.
4. PhaseDefinition system catalog read may be accessible to authenticated users if safe.
5. Future project-private IAM resource is not required in Phase 09.
```

Per-project IAM:

```text
DEFERRED_TO_PHASE_10/11/19 only if private projects or per-project sharing are required.
```

---

# 12. Event Registry integration

Source system:

```text
SCOPERY_PROJECT
```

## 12.1 Required Phase 09 events

```text
PROJECT_CREATED
PROJECT_UPDATED
PROJECT_ACTIVATED
PROJECT_ARCHIVED

PROJECT_PHASE_CREATED
PROJECT_PHASE_UPDATED
PROJECT_PHASE_ACTIVATED
PROJECT_PHASE_COMPLETED
PROJECT_PHASE_ARCHIVED

WBS_NODE_CREATED
WBS_NODE_UPDATED
WBS_NODE_MOVED
WBS_NODE_ARCHIVED

TASK_CREATED
TASK_UPDATED
TASK_ASSIGNED
TASK_STARTED
TASK_BLOCKED
TASK_COMPLETED
TASK_CANCELLED
TASK_ARCHIVED

TASK_DEPENDENCY_CREATED
TASK_DEPENDENCY_REMOVED
```

## 12.2 Standard variables

```text
actor.userId
organization.id
workspace.id
project.id
project.code
project.name
phase.id
phase.name
wbsNode.id
wbsNode.title
task.id
task.title
task.status
task.assigneeUserId
task.estimateHours
task.dueDate
dependency.id
dependency.predecessorTaskId
dependency.successorTaskId
occurredAt
traceId
```

## 12.3 Future project events

Deferred:

```text
PROJECT_TEMPLATE_APPLIED — Phase 11
CAPACITY_RECALCULATED — Phase 12
TASK_SCHEDULE_RECALCULATED — Phase 13
GANTT_RECALCULATED — Phase 14
PROJECT_ESTIMATE_ROLLED_UP — Phase 16
PROJECT_FINANCIAL_SCENARIO_APPROVED — Phase 17
QUOTE_APPROVAL_REQUIRED — Phase 18
PROJECT_BASELINE_APPROVED — Phase 19
CHANGE_REQUEST_APPROVED — Phase 19
AI_PROJECT_PLAN_SUGGESTED — Phase 21
REPORT_EXPORT_COMPLETED — Phase 22
```

---

# 13. Notification integration

Phase 09 should seed event definitions but not necessarily send notifications.

Optional current notification seeds:

```text
TASK_ASSIGNED_NOTIFICATION
TASK_COMPLETED_NOTIFICATION
TASK_BLOCKED_NOTIFICATION
PROJECT_ARCHIVED_NOTIFICATION
```

Recommended classification:

```text
SEED_ONLY_IN_PHASE_09 or DEFERRED_TO_PHASE_20_PROJECT_EVENTS_NOTIFICATIONS
```

Reason:

```text
Full project notification preferences, subscriptions, reminders, and digests belong to Phase 20.
```

Do not implement notification storm logic in Phase 09.

---

# 14. Knowledge / Document integration

Phase 09 may reference DocumentType for deliverables only if Phase 08 supports it.

Optional fields:

```text
WbsNode.deliverableDocumentTypeId
ProjectPhase.deliverableDocumentTypeId
Task.deliverableDocumentTypeId
```

Recommended:

```text
DEFERRED_TO_PHASE_11/27 unless required now.
```

Do not implement document repository in Phase 09.

---

# 15. AI Agent integration

Phase 09 must not implement AI-generated WBS/task planning.

AI planning is deferred:

```text
DEFERRED_TO_PHASE_21_AI_ASSISTED_PROJECT_PLANNING
```

Phase 09 may seed event definitions and leave hooks:

```text
PROJECT_CREATED
TASK_CREATED
WBS_NODE_CREATED
```

AI can listen later, but not now.

Rules:

```text
1. AI cannot create WBS/task automatically in Phase 09.
2. AI suggestions require Phase 21 suggestion review.
3. Any AI-applied project mutation must pass project actions and IAM.
```

---

# 16. Platform audit/outbox/idempotency integration

Phase 09 must use Phase 04 patterns.

## 16.1 Activity log actions

```text
PROJECT_CREATED
PROJECT_UPDATED
PROJECT_ACTIVATED
PROJECT_ARCHIVED
PROJECT_PHASE_CREATED
PROJECT_PHASE_UPDATED
PROJECT_PHASE_COMPLETED
WBS_NODE_CREATED
WBS_NODE_MOVED
WBS_NODE_ARCHIVED
TASK_CREATED
TASK_UPDATED
TASK_ASSIGNED
TASK_STARTED
TASK_BLOCKED
TASK_COMPLETED
TASK_CANCELLED
TASK_ARCHIVED
TASK_DEPENDENCY_CREATED
TASK_DEPENDENCY_REMOVED
```

## 16.2 Audit-sensitive actions

Audit:

```text
PROJECT_ARCHIVED
PROJECT_PHASE_ARCHIVED
WBS_NODE_ARCHIVED
TASK_ARCHIVED
TASK_DEPENDENCY_REMOVED
Task assignee changed
Task estimateHours changed
Task dueDate changed
```

Reason:

```text
These changes affect schedule/capacity/finance future calculations.
```

## 16.3 Idempotency

Recommended for:

```text
POST /api/projects
POST /api/projects/{projectId}/phases
POST /api/projects/{projectId}/wbs-nodes
POST /api/projects/{projectId}/tasks
POST /api/projects/{projectId}/task-dependencies
POST /api/projects/{projectId}/wbs-nodes/{wbsNodeId}/move
```

## 16.4 Outbox

All project events should go through platform outbox if available.

If not available:

```text
Use existing event publisher and document gap.
```

---

# 17. Seed data requirements

## 17.1 PhaseDefinition seeds

Seed standard phase definitions if current module owns them:

```text
DISCOVERY
ANALYSIS
DESIGN
DEVELOPMENT
TESTING
DEPLOYMENT
SUPPORT
CLOSURE
```

Rules:

```text
1. Idempotent.
2. Scope SYSTEM.
3. Built-in true if field exists.
4. Do not overwrite workspace custom definitions.
5. Do not delete old definitions.
```

Advanced project templates:

```text
DEFERRED_TO_PHASE_11
```

## 17.2 Project event seeder

Seed EventDefinitions for all Phase 09 project events.

## 17.3 Permission seeder

Ensure project authorities exist from Phase 02.

If missing:

```text
Add or document Phase 02 follow-up.
```

---

# 18. Business rules master

## 18.1 Project rules

```text
PRJ-001 Workspace must exist.
PRJ-002 Workspace must be ACTIVE.
PRJ-003 Project code required.
PRJ-004 Project code unique within workspace.
PRJ-005 Project code normalized uppercase.
PRJ-006 Project name required.
PRJ-007 owner/inCharge must be active workspace member.
PRJ-008 plannedEndDate >= plannedStartDate.
PRJ-009 Archived project cannot update.
PRJ-010 Archived project blocks child mutations.
PRJ-011 Project archive does not delete children.
```

## 18.2 ProjectPhase rules

```text
PHASE-001 Project must exist and not be archived.
PHASE-002 PhaseDefinition must be ACTIVE if provided.
PHASE-003 Phase name required.
PHASE-004 displayOrder required.
PHASE-005 plannedEndDate >= plannedStartDate.
PHASE-006 Archived phase cannot update.
PHASE-007 Phase must belong to path project.
PHASE-008 Archiving phase cannot orphan active tasks silently.
```

## 18.3 WBS rules

```text
WBS-001 Project must exist and not be archived.
WBS-002 Parent node must belong to same project.
WBS-003 Title required.
WBS-004 Move target parent must be same project.
WBS-005 Move cannot create cycle.
WBS-006 Move cannot place node under itself/descendant.
WBS-007 Archive with children blocked unless cascade explicit.
WBS-008 Archive with active tasks blocked unless tasks moved/archived.
WBS-009 Node must belong to path project.
```

## 18.4 Task rules

```text
TASK-001 Project must exist and not be archived.
TASK-002 Title required.
TASK-003 Task belongs to exactly one phase.
TASK-004 Phase must belong to same project.
TASK-005 WBS node if set must belong to same project.
TASK-006 estimateHours required.
TASK-007 estimateHours > 0.
TASK-008 assignee/inCharge if set must be active workspace member.
TASK-009 Archived task cannot update.
TASK-010 Lifecycle transition must be valid.
TASK-011 Completing task sets completedAt/completedBy.
TASK-012 Cancelling task sets cancelledAt/cancelledBy.
TASK-013 Estimate is effort, not duration.
```

## 18.5 Dependency rules

```text
DEP-001 Predecessor task must exist.
DEP-002 Successor task must exist.
DEP-003 Both tasks same project.
DEP-004 predecessor != successor.
DEP-005 Duplicate dependency blocked.
DEP-006 Dependency cycle blocked.
DEP-007 Dependency does not auto-schedule in Phase 09.
DEP-008 Removing dependency requires permission.
```

---

# 19. Error catalog requirements

Exact names follow current convention, but concepts must exist.

```text
PROJECT_NOT_FOUND
PROJECT_CODE_ALREADY_EXISTS
PROJECT_INVALID_CODE
PROJECT_ARCHIVED
PROJECT_WORKSPACE_NOT_ACTIVE
PROJECT_INVALID_DATE_RANGE
PROJECT_OWNER_NOT_WORKSPACE_MEMBER
PROJECT_IN_CHARGE_NOT_WORKSPACE_MEMBER

PROJECT_PHASE_NOT_FOUND
PROJECT_PHASE_DEFINITION_NOT_FOUND
PROJECT_PHASE_DEFINITION_INACTIVE
PROJECT_PHASE_ARCHIVED
PROJECT_PHASE_PATH_MISMATCH
PROJECT_PHASE_HAS_ACTIVE_TASKS

WBS_NODE_NOT_FOUND
WBS_NODE_PATH_MISMATCH
WBS_NODE_PARENT_NOT_FOUND
WBS_NODE_PARENT_PROJECT_MISMATCH
WBS_NODE_MOVE_CYCLE_DETECTED
WBS_NODE_ARCHIVED
WBS_NODE_HAS_CHILDREN
WBS_NODE_HAS_ACTIVE_TASKS

TASK_NOT_FOUND
TASK_PATH_MISMATCH
TASK_INVALID_ESTIMATE
TASK_PHASE_PROJECT_MISMATCH
TASK_WBS_PROJECT_MISMATCH
TASK_ASSIGNEE_NOT_WORKSPACE_MEMBER
TASK_INVALID_STATUS_TRANSITION
TASK_ARCHIVED

TASK_DEPENDENCY_NOT_FOUND
TASK_DEPENDENCY_DUPLICATE
TASK_DEPENDENCY_SELF_NOT_ALLOWED
TASK_DEPENDENCY_PROJECT_MISMATCH
TASK_DEPENDENCY_CYCLE_DETECTED
```

---

# 20. Required tests

Phase 09 is incomplete without tests.

---

## 20.1 Project tests

```text
createProject_valid_success
createProject_inactiveWorkspace_rejected
createProject_duplicateCodeWithinWorkspace_conflict
createProject_sameCodeDifferentWorkspace_allowed
createProject_codeNormalizedUppercase
createProject_ownerNotWorkspaceMember_rejected
createProject_inChargeNotWorkspaceMember_rejected
createProject_invalidDateRange_rejected
updateProject_archived_rejected
archiveProject_blocksChildMutation
projectEvents_emitted
projectActivity_logged
```

## 20.2 PhaseDefinition tests

```text
phaseDefinitionSeeder_firstRun_createsDefaults
phaseDefinitionSeeder_secondRun_noDuplicates
createProjectPhase_inactivePhaseDefinition_rejected
```

## 20.3 ProjectPhase tests

```text
createProjectPhase_valid_success
createProjectPhase_archivedProject_rejected
createProjectPhase_invalidDateRange_rejected
updateProjectPhase_pathMismatch_rejected
completeProjectPhase_valid_success
archiveProjectPhase_withActiveTasks_rejectedOrDocumented
archiveProjectPhase_valid_success
```

## 20.4 WBS tests

```text
createWbsNode_root_success
createWbsNode_child_success
createWbsNode_parentDifferentProject_rejected
moveWbsNode_valid_success
moveWbsNode_underSelf_rejected
moveWbsNode_underDescendant_rejected
moveWbsNode_crossProjectParent_rejected
archiveWbsNode_withChildren_rejected
archiveWbsNode_withActiveTasks_rejected
archiveWbsNode_valid_success
wbsTree_returnsHierarchy
```

## 20.5 Task tests

```text
createTask_valid_success
createTask_archivedProject_rejected
createTask_missingPhase_rejected
createTask_phaseDifferentProject_rejected
createTask_wbsDifferentProject_rejected
createTask_estimateZero_rejected
createTask_estimateNegative_rejected
createTask_assigneeNotWorkspaceMember_rejected
updateTask_newAssigneeNotWorkspaceMember_rejected
updateTask_estimateInvalid_rejected
startTask_valid_success
blockTask_valid_success
completeTask_valid_success
cancelTask_valid_success
archiveTask_valid_success
invalidStatusTransition_rejected
```

## 20.6 Dependency tests

```text
createDependency_valid_success
createDependency_duplicate_conflict
createDependency_self_rejected
createDependency_crossProject_rejected
createDependency_cycle_rejected
removeDependency_valid_success
dependencyDoesNotScheduleTask
```

## 20.7 Authorization tests

```text
createProject_withoutProjectCreate_forbidden
viewProject_withoutProjectView_forbidden
updateTask_withoutProjectTaskUpdate_forbidden
createDependency_withoutTaskUpdate_forbidden
workspaceMemberWithoutGrant_forbidden
inactiveWorkspaceMember_forbidden
```

If authorization hardening is Phase 10:

```text
Mark missing fine-grained tests as Phase 10 follow-up, but basic access should exist now.
```

## 20.8 Seeder tests

```text
projectEventSeeder_firstRun_createsDefinitions
projectEventSeeder_secondRun_noDuplicates
phaseDefinitionSeeder_firstRun_createsDefinitions
phaseDefinitionSeeder_secondRun_noDuplicates
projectPermissionSeeder_confirmAuthoritiesExist
```

---

# 21. Manual verification checklist

Completion file must include:

```text
1. Create project under active workspace.
2. Verify duplicate project code same workspace rejected.
3. Create project phase.
4. Create root WBS node.
5. Create child WBS node.
6. Move WBS node.
7. Try move under descendant and confirm rejection.
8. Create task under project phase.
9. Create task linked to WBS node.
10. Try task with estimateHours <= 0 and confirm rejection.
11. Try assign inactive/non-member user and confirm rejection.
12. Start/block/complete/cancel/archive task.
13. Create task dependency.
14. Try dependency cycle and confirm rejection.
15. Archive project and confirm child mutations blocked.
16. Confirm events/activity logs exist.
17. Rerun seeders and confirm no duplicates.
```

---

# 22. Acceptance criteria

Phase 09 is accepted only if:

```text
1. Current Project module is classified against TO-BE.
2. Project entity/lifecycle rules implemented/tested.
3. ProjectPhase rules implemented/tested.
4. PhaseDefinition foundation implemented/tested or deferred to Phase 11 where appropriate.
5. WBS tree create/update/move/archive rules implemented/tested.
6. WBS cycle prevention tested.
7. Task create/update/lifecycle rules implemented/tested.
8. Task estimateHours > 0 enforced.
9. Assignee/inCharge active workspace member validation enforced.
10. TaskDependency same-project/duplicate/self/cycle rules implemented/tested.
11. Project events seeded idempotently.
12. Activity/audit/outbox integration follows Phase 04.
13. Authorization uses workspace/IAM model or Phase 10 gap is documented.
14. Scheduling/Gantt/capacity/finance/quote/baseline/AI planning are not falsely claimed.
15. Deferred items mapped to target phases.
16. mvn compile passes.
17. mvn test passes.
18. Completion file exists and includes gap matrix.
```

Agent must not mark complete if:

```text
mvn test fails
project can be created in inactive workspace
task can be created without valid project phase
task estimateHours <= 0 accepted
assignee/inCharge not active workspace member accepted
WBS move can create cycle
task dependency can create cycle
archived project allows child mutations
future scheduling/Gantt/finance/AI features are claimed implemented without real code
```

---

# 23. Required phase completion file

Agent must create:

```text
docs/phase-complete/PHASE_09_PROJECT_CORE_WBS_TASK_TO_BE_COMPLETE.md
```

Required sections:

```text
# Phase 09 — Project Core / Phase / WBS / Task TO-BE Complete

## 1. Summary
## 2. Source Inputs Reviewed
## 3. Current vs TO-BE Classification Matrix
## 4. Implemented in Current BE
## 5. Implemented / Hardened in This Phase
## 6. Seed-only Items Added
## 7. Deferred Items and Target Phase
## 8. Entity Mapping
## 9. API Changes
## 10. Project Rules
## 11. ProjectPhase Rules
## 12. WBS Rules
## 13. Task Rules
## 14. TaskDependency Rules
## 15. IAM Authorization Matrix
## 16. Event Registry Seeder Matrix
## 17. Notification Integration Notes
## 18. Activity / Audit / Outbox Notes
## 19. Seed Data
## 20. Tests Added
## 21. Commands Run
## 22. Test Results
## 23. Manual Verification
## 24. Assumptions
## 25. Deviations From Prompt
## 26. Known Risks
## 27. Future Phases That Must Return to Project Core
```

---

# 24. Future phases that must return to Project Core

## 24.1 Phase 10 — Project Authorization Hardening

Must enforce:

```text
PROJECT_CREATE
PROJECT_VIEW
PROJECT_UPDATE
PROJECT_ARCHIVE
PROJECT_PHASE_*
PROJECT_WBS_*
PROJECT_TASK_*
TASK_DEPENDENCY permissions
```

Must ensure every Project query/action checks workspace access and relevant authority.

## 24.2 Phase 11 — Project Template / Phase Catalog

Must extend:

```text
PhaseDefinition management
Project templates
Template WBS nodes
Template tasks
Clone project from template
Template dependencies
Workspace active phase catalog
```

## 24.3 Phase 12 — Resource Calendar / Capacity

Must use:

```text
Task.estimateHours
Task.assigneeUserId/inChargeUserId
Workspace member
Task dueDate
```

Must not reinterpret estimateHours as duration.

## 24.4 Phase 13 — Task Scheduling Engine

Must add:

```text
estimatedStartDate
estimatedFinishDate
capacity gap
dependency-aware scheduling
schedule run
schedule issue
```

Will consume TaskDependency.

## 24.5 Phase 14 — WBS-driven Gantt

Must project:

```text
ProjectPhase
WBS tree
Task
TaskDependency
Schedule forecast
```

Gantt edits must update tasks/WBS through Phase 09 actions.

## 24.6 Phase 15 — Rate Card

Must use task estimate hours and assignee role/cost role.

## 24.7 Phase 16 — Estimation Roll-up

Must roll up:

```text
WBS estimate hours
Phase estimate hours
Project estimate hours
```

## 24.8 Phase 17 — Finance / Budget / Margin

Must use:

```text
Task estimate hours
Phase
WBS
Cost role / rate card
Custom phase costs
Revenue split
```

## 24.9 Phase 18 — Quote

Must use:

```text
WBS scope
Task estimates
Phase finance summary
Contract value solver
```

## 24.10 Phase 19 — Baseline / Change Request

Must snapshot:

```text
Project
ProjectPhase
WBS tree
Task
TaskDependency
Schedule/finance later
```

Changes after baseline should go through ChangeRequest if configured.

## 24.11 Phase 20 — Project Events / Notifications

Must add:

```text
Task assignment notifications
Due date risk alerts
Blocked task alerts
Phase completion notifications
Subscription/follow preferences
```

## 24.12 Phase 21 — AI-assisted Project Planning

Must add:

```text
AI-generated WBS suggestions
AI-generated task suggestions
Human accept/reject
Apply suggestions through Phase 09 actions
```

## 24.13 Phase 22 — Reporting / Dashboard / Export

Must report:

```text
project count
task status
WBS progress
phase progress
assignment summary
```

## 24.14 Phase 23 — Core Hardening

Must verify:

```text
Project query performance
WBS tree indexes
Task filters
Dependency cycle algorithm performance
Authorization coverage
Audit/event coverage
```

---

# 25. Agent anti-bịa rules

The agent must not:

```text
1. Treat current Project module as full project management future-state.
2. Claim Gantt exists unless Gantt projection/API exists.
3. Claim scheduling exists unless schedule engine and forecast entities exist.
4. Claim capacity exists unless resource calendar/capacity formulas exist.
5. Claim finance exists unless rate card/cost/margin entities exist.
6. Claim quote exists unless quote/version/approval/solver exists.
7. Claim baseline/change request exists unless snapshot and CR workflows exist.
8. Claim AI planning exists unless suggestion entities and approval flow exist.
9. Let WBS move create cycles.
10. Let TaskDependency create cycles.
11. Let task estimateHours <= 0.
12. Allow assignee who is not active workspace member.
13. Allow archived project child mutations.
14. Hide deferred project features.
```

---

# 26. Prompt to give coding agent

```text
You are implementing Phase 09 — TO-BE Project Core, Phase, WBS, Task & Dependency Planning Foundation.

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
- Phase 08 Knowledge/DocumentType TO-BE spec
- Current BE feature/entity/business-rule inventory
- Existing project module code, migrations, seeders, tests

Your task:
1. Compare current Project module against this TO-BE Phase 09 spec.
2. Classify every project-related capability as CURRENTLY_IMPLEMENTED, MUST_IMPLEMENT_IN_PHASE_09, SEED_ONLY_IN_PHASE_09, DEFERRED_TO_PHASE_XX, DEFERRED_TO_POST_23_BACKLOG, or NOT_IN_SCOPE_FOR_BACKEND_NOW.
3. Implement only Phase 09 required items.
4. Harden Project, ProjectPhase, PhaseDefinition reference, WBS, Task, and TaskDependency.
5. Enforce estimateHours > 0.
6. Enforce assignee/inCharge active workspace member.
7. Enforce WBS and dependency cycle prevention.
8. Ensure archived project blocks child mutations.
9. Add/verify project event definitions.
10. Add tests listed in Phase 09 spec.
11. Run mvn compile and mvn test.
12. Create docs/phase-complete/PHASE_09_PROJECT_CORE_WBS_TASK_TO_BE_COMPLETE.md with full gap matrix.

Do not claim scheduling, Gantt, capacity, finance, quote, baseline, change request, agile, or AI planning exists unless implemented in its correct phase.
```

---

# 27. Quick tracking matrix

| Capability | Current backend | Phase 09 action | Later phase |
|---|---|---|---|
| Project CRUD/lifecycle | Present | Harden/test | — |
| ProjectPhase | Present | Harden/test | Phase 11 expands catalog |
| PhaseDefinition | Present | Verify/reference | Phase 11 full management |
| WBS tree | Present | Harden move/cycle/archive | Phase 14 Gantt projection |
| Task | Present | Harden estimate/assignee/status | Phase 12/13 consume |
| TaskDependency | Present | Harden duplicate/cycle | Phase 13 scheduling |
| Estimate hours | Present | Must validate >0 | Phase 16/17 consume |
| Due date | Present/likely | Clarify semantics | Phase 13/20 risk |
| Capacity | Missing | Defer | Phase 12 |
| Scheduling | Missing | Defer | Phase 13 |
| Gantt | Missing | Defer | Phase 14 |
| Rate card / CCH | Missing | Defer | Phase 15 |
| Estimation roll-up | Missing | Defer | Phase 16 |
| Finance / margin | Missing | Defer | Phase 17 |
| Quote | Missing | Defer | Phase 18 |
| Baseline / CR | Missing | Defer | Phase 19 |
| Project notifications | Partial/future | Defer | Phase 20 |
| AI planning | Missing | Defer | Phase 21 |
| Reporting | Missing | Defer | Phase 22 |

---

# 28. Final principle

Phase 09 is not complete when "projects and tasks can be created."

Phase 09 is complete when Scopery has a clean project planning foundation:

```text
Project is workspace-scoped.
Phase is explicit.
WBS is the scope source of truth.
Task is the execution unit.
Estimate hours are effort.
Due date is deadline.
Dependency is logical relationship.
No cycle.
No invalid assignee.
No archived-project mutation.
No fake scheduling, Gantt, finance, quote, or AI planning.
```
