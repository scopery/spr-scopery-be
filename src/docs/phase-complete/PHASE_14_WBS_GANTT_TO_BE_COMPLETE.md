# Phase 14 — WBS-driven Gantt TO-BE Complete

## 1. Summary

Phase 14 delivers the WBS-driven Gantt projection and controlled timeline adjustment layer.

Gantt is a **read model + controlled edit surface**. Source of truth remains:

```text
ProjectPhase → WbsNode → Task → TaskDependency → TaskSchedule / ScheduleRun
```

Phase 14 adds:

- Gantt projection APIs from phases, WBS, tasks, dependencies, schedule runs, issues, and milestones
- Dedicated `ProjectMilestone` entity (Model B)
- `TaskScheduleOverride` for drag/move/resize with Phase 13 scheduler respect
- Gantt recalculation that delegates to Phase 13 `CreateScheduleRunAction`
- Dependency create/remove wrappers over TaskDependency actions
- Events, activity, audit, error catalog, and unit tests

Phase 14 does **not** implement finance/cost, quote, baseline comparison, change request,
timesheet actuals, critical path CPM, Gantt export, view preferences, or AI optimization.

## 2. Source Inputs Reviewed

- `src/docs/phase-tracking/PHASE_14_WBS_GANTT_TO_BE_DETAILED.md`
- Phase 09–13 completion docs and live code under `modules/project/**`
- Phase 12 capacity + Phase 13 `ScheduleEngineService` / `TaskSchedule` / `ScheduleRun`
- Phase 04 outbox/audit/idempotency filter
- Phase 05 event registry seed pattern
- Existing TaskDependency create/remove + ProjectWorkspaceAuthorizationService
- IAM right catalog (Phase 13-style mapping reused)

## 3. Current vs TO-BE Classification Matrix

| Capability | Pre-Phase 14 | Classification | Phase 14 result |
|---|---|---|---|
| Gantt projection / read model | Missing | MUST_IMPLEMENT_IN_PHASE_14 | Implemented |
| Gantt item DTOs (PROJECT/PHASE/WBS/TASK/MILESTONE) | Missing | MUST_IMPLEMENT_IN_PHASE_14 | Implemented |
| Gantt dependency projection | TaskDependency existed | MUST_IMPLEMENT_IN_PHASE_14 | Implemented |
| Gantt issue markers | SchedulingIssue existed | MUST_IMPLEMENT_IN_PHASE_14 | Projected into Gantt |
| ProjectMilestone | Missing | MUST_IMPLEMENT_IN_PHASE_14 | Implemented (Model B) |
| Gantt recalculation | Schedule run existed | MUST_IMPLEMENT_IN_PHASE_14 | Wrapper → Phase 13 |
| Task move (override) | Deferred from Phase 13 | MUST_IMPLEMENT_IN_PHASE_14 | Implemented |
| Task resize (override) | Deferred | MUST (with override) | Implemented |
| Clear override | Missing | MUST (with override) | Implemented |
| Dependency edit via Gantt | TaskDependency APIs | MUST_IMPLEMENT_IN_PHASE_14 | Wrappers |
| WBS row move | MoveWbsNodeAction | PARTIALLY_IMPLEMENTED | Reused (no Gantt duplicate) |
| Scheduler respects override | Missing | MUST if override | Implemented |
| Critical path | Missing | DEFERRED_TO_PHASE_23 | Deferred |
| Gantt view preferences | Missing | DEFERRED_TO_POST_23 | Deferred |
| Gantt export | Missing | DEFERRED_TO_PHASE_22 | Deferred |
| Baseline comparison | Missing | DEFERRED_TO_PHASE_19 | Not in scope |
| Cost / quote / AI | Missing | NOT_IN_SCOPE_FOR_BACKEND_NOW | Not claimed |

## 4. Implemented in Current BE (pre-Phase 14)

- Project / ProjectPhase / WbsNode / Task / TaskDependency (Phase 09)
- Project authorization helpers (Phase 10)
- ScheduleRun / TaskSchedule / ScheduledDailyWork / SchedulingIssue (Phase 13)
- Platform outbox, immutable audit, Idempotency-Key filter (Phase 04)
- Event definition seeding pattern (Phase 05)

## 5. Implemented / Hardened in This Phase

- Flyway `V51__create_gantt_tables_phase14.sql`
- Module `modules/project/gantt` — projection + Gantt-facing write APIs
- Module `modules/project/milestone` — CRUD + achieve + archive
- Module `modules/project/scheduleoverride` — domain + persistence
- `ScheduleEngineService` reads ACTIVE overrides (PIN_START / PIN_FINISH / PIN_RANGE / DUE_DATE_OVERRIDE)
- `SchedulingIssueType.MANUAL_SCHEDULE_OVERRIDE_APPLIED`
- Shared constants / errors / auth helpers / publisher methods
- Gantt event seeder `SCOPERY_GANTT` / owner `GANTT` (`@Order(18)`)

## 6. Seed-only Items Added

| Seed | Codes |
|---|---|
| `GanttEventDefinitionSeedInitializer` | `GANTT_VIEW_GENERATED`, `GANTT_RECALCULATION_REQUESTED`, `GANTT_RECALCULATED`, `GANTT_TASK_MOVED`, `GANTT_TASK_RESIZED`, `GANTT_TASK_OVERRIDE_CLEARED`, `GANTT_DEPENDENCY_CREATED`, `GANTT_DEPENDENCY_REMOVED`, `PROJECT_MILESTONE_CREATED/UPDATED/ACHIEVED/ARCHIVED`, `GANTT_ISSUE_MARKER_CREATED` |

No dedicated new IAM rights were seeded (Phase 13-style mapping to `VIEW_TASK` / `UPDATE_TASK`).

## 7. Deferred Items and Target Phase

| Item | Target |
|---|---|
| Critical path (full CPM) | Phase 23 / post-23 |
| Gantt view preferences (zoom, collapsed rows) | Post-23 productivity backlog |
| Gantt export PDF/Excel/image | Phase 22 |
| Baseline comparison / change request gate | Phase 19 |
| Cost / margin timeline overlay | Phase 17 |
| Quote timeline commitment | Phase 18 |
| Notifications for move/milestone/recalc | Phase 20 |
| AI schedule suggestions | Phase 21 |
| Dedicated GANTT_* IAM right codes | Optional later |

## 8. Gantt Source-of-truth Decision

```text
WBS / Task / TaskDependency / TaskSchedule / ScheduleRun = source of truth
GanttItem = projection DTO only (no persistent GanttItem table)
ProjectMilestone = first-class timeline marker (not a Task)
TaskScheduleOverride = manual pin input consumed by Phase 13 engine
```

Gantt edits never create disconnected plan rows that drift from tasks.

## 9. Entity Mapping

| Entity | Table | Notes |
|---|---|---|
| ProjectMilestone | `project_milestone` | Status: PLANNED / ACHIEVED / MISSED / ARCHIVED |
| TaskScheduleOverride | `project_task_schedule_override` | One ACTIVE override per task (partial unique index) |
| GanttItem | — | DTO only |
| GanttViewPreference | — | Deferred |

## 10. API Changes

| Method | Path | Behavior |
|---|---|---|
| GET | `/api/projects/{projectId}/gantt` | Full projection |
| GET | `.../gantt/items` | Items slice |
| GET | `.../gantt/dependencies` | Dependency slice |
| GET | `.../gantt/issues` | Issue slice |
| POST | `.../gantt/recalculate` | Delegates to Phase 13 schedule run |
| POST | `.../gantt/tasks/{taskId}/move` | Creates PIN_RANGE override |
| POST | `.../gantt/tasks/{taskId}/resize` | Updates finish pin; does **not** change estimateHours |
| POST | `.../gantt/tasks/{taskId}/clear-override` | Cancels ACTIVE override |
| POST | `.../gantt/dependencies` | Wraps TaskDependency create (+ optional recalc) |
| DELETE | `.../gantt/dependencies/{dependencyId}` | Wraps TaskDependency remove (+ optional recalc) |
| POST/GET/PUT/PATCH | `/api/projects/{projectId}/milestones[...]` | Milestone CRUD + achieve + archive |

Query params on Gantt view: `scheduleRunId`, `dateFrom`, `dateTo`, `includeUnscheduled`, `includeArchived`, `groupBy=PHASE|WBS|ASSIGNEE`.

`Idempotency-Key` supported via existing platform filter for POST/DELETE under `/api`.

## 11. Gantt Projection Rules

1. Auth: `requireGanttView` → `PROJECT_TASK_VIEW`
2. Schedule run resolution: explicit `scheduleRunId` (must belong to project) → else `project.currentScheduleRunId` → else latest COMPLETED; missing run still returns structure with unscheduled tasks
3. Item types: PROJECT, PHASE, WBS_NODE, TASK, MILESTONE
4. Task dates from `TaskSchedule.estimatedStartDate` / `estimatedFinishDate`
5. Milestone = zero-duration marker on `milestoneDate`
6. Spans use min/max of planned dates + child schedules + milestones
7. Dependencies from TaskDependency; non-FS types flagged unsupported for scheduling semantics
8. Issues from `SchedulingIssue` for selected run
9. Response contains **no** finance / quote / baseline / cost fields

## 12. Milestone Decision

**Implemented Model B — dedicated `ProjectMilestone`.**

Reasons:

- Phase 09 Task requires `estimateHours > 0` (zero-hour milestone task would violate rules)
- Milestones must not consume capacity
- Milestone is a date marker, not an execution unit

APIs: create / list / get / update / achieve / archive.

## 13. Manual Move/Resize Decision

**Implemented with `TaskScheduleOverride`.**

| Action | Override type | estimateHours |
|---|---|---|
| Move | PIN_RANGE (start+finish) | Unchanged |
| Resize | PIN_FINISH / PIN_RANGE finish update | Unchanged |
| Clear | Cancel ACTIVE | N/A |

Phase 13 `ScheduleEngineService` applies ACTIVE overrides:

- PIN_START / PIN_RANGE → pin earliest start
- PIN_FINISH / PIN_RANGE → clamp planning end for placement
- DUE_DATE_OVERRIDE → effective due date for risk/gap
- Emits `MANUAL_SCHEDULE_OVERRIDE_APPLIED` INFO issue

## 14. Dependency Edit Strategy

Gantt dependency endpoints **reuse** `CreateTaskDependencyAction` / `RemoveTaskDependencyAction`.

All existing guards remain: self-ref, same project, duplicate, cycle BFS, task eligibility.

Optional `recalculate=true` triggers Phase 13 schedule run after edit.

## 15. Recalculation Strategy

`RecalculateGanttAction` → `CreateScheduleRunAction` (Phase 13).

Gantt does **not** implement a separate scheduling algorithm.

Events: `GANTT_RECALCULATION_REQUESTED`, `GANTT_RECALCULATED` + existing schedule run events.

## 16. Authorization Matrix

Phase 14 simplification (documented, same pattern as Phase 13):

| Spec authority | Mapped to |
|---|---|
| GANTT_VIEW | `PROJECT_TASK_VIEW` (`VIEW_TASK`) |
| GANTT_RECALCULATE | `PROJECT_TASK_UPDATE` |
| GANTT_MOVE_TASK / RESIZE / CLEAR_OVERRIDE | `PROJECT_TASK_UPDATE` |
| GANTT_EDIT_DEPENDENCY | `PROJECT_TASK_UPDATE` |
| GANTT_*_MILESTONE | `PROJECT_TASK_UPDATE` |

Helpers on `ProjectWorkspaceAuthorizationService`: `requireGanttView`, `requireGanttRecalculate`, `requireGanttMoveTask`, `requireGanttResizeTask`, `requireGanttClearOverride`, `requireGanttEditDependency`, `requireGanttMilestoneCreate/Update/Archive`.

## 17. Event Registry Seeder Matrix

| Source system | Owner | Count | Idempotent |
|---|---|---|---|
| `SCOPERY_GANTT` | `GANTT` | 13 definitions | Yes (`findOrCreate`) |

## 18. Activity / Audit / Outbox Notes

**Activity actions:** `GANTT_RECALCULATED`, `GANTT_TASK_MOVED`, `GANTT_TASK_RESIZED`, `GANTT_TASK_OVERRIDE_CLEARED`, `GANTT_DEPENDENCY_CREATED`, `GANTT_DEPENDENCY_REMOVED`, `PROJECT_MILESTONE_CREATED/UPDATED/ACHIEVED/ARCHIVED`.

**Audit (immutable):** gantt task move/resize/clear-override, milestone achieve/archive, recalculation mark-current (via schedule path).

**Outbox:** Gantt/milestone/override events enqueued through `ProjectPlatformPublisher` with `SCOPERY_GANTT` source system where applicable.

## 19. Idempotency Strategy

Relies on platform `Idempotency-Key` filter (`app_idempotency_key`). No Gantt-specific idempotency table.

Recommended clients send `Idempotency-Key` on:

- POST recalculate / move / resize / dependencies / milestones

## 20. Tests Added

| Test class | Focus |
|---|---|
| `GanttQueryServiceTest` | Projection spans / schedule run mismatch / no finance fields |
| `RecalculateGanttActionTest` | Delegates to Phase 13 |
| `MoveGanttTaskActionTest` | Override create, invalid range, reason required |
| `ResizeGanttTaskActionTest` | Finish pin; estimateHours unchanged |
| `ClearGanttTaskOverrideActionTest` | Cancel ACTIVE |
| `CreateGanttDependencyActionTest` | Wrapper + exception reuse |
| `CreateProjectMilestoneActionTest` | Create + archived project rejection |
| `GanttEventDefinitionSeedInitializerTest` | First run + idempotent second run |
| `ScheduleEngineServiceTest` (updated) | Active override pin respected |

Focused Phase 14 suite: **23 tests, 0 failures**.

Full `mvn test`: **BUILD SUCCESS**.

## 21. Commands Run

```bash
mvn -q -DskipTests compile
mvn -q test -Dtest='**/gantt/**/*,**/milestone/**/*,**/scheduling/engine/ScheduleEngineServiceTest,...'
mvn -q test
```

## 22. Test Results

- `mvn -q -DskipTests compile` — passed
- Focused Phase 14 + schedule engine tests — **23 passed**
- Full suite `mvn test` — **BUILD SUCCESS**

## 23. Manual Verification

Checklist for local runtime (DB + auth):

1. Run schedule calculation for a project (`POST .../schedule-runs`).
2. `GET .../gantt` — confirm phases / WBS / tasks appear.
3. Confirm task dates come from `TaskSchedule`.
4. Confirm dependencies appear from `TaskDependency`.
5. Create milestone — confirm appears as zero-duration item.
6. Confirm unscheduled tasks appear with UNSCHEDULED status when requested.
7. `POST .../gantt/recalculate` — new ScheduleRun created / marked current.
8. Move task — override + audit + optional recalculation.
9. Resize task — estimateHours unchanged.
10. Create dependency from Gantt — TaskDependency created; cycle rejected.
11. Confirm response has no finance / quote / baseline fields.

Runtime smoke against a live DB was **not yet verified in this agent session** beyond compile/unit tests.

## 24. Assumptions

- Gantt view permission can reuse `VIEW_TASK` without new IAM right codes.
- Default `groupBy=PHASE` is acceptable for V1 UI.
- Soft FKs (no DB FK to project/task) match Phase 13 table style.
- When no completed schedule run exists, Gantt still returns structural items rather than hard-failing (except invalid explicit `scheduleRunId`).

## 25. Deviations From Prompt

| Spec suggestion | Actual |
|---|---|
| Optional dedicated GANTT_* IAM rights | Mapped to PROJECT_TASK_* (Phase 13 pattern) |
| Optional GanttViewPreference | Deferred |
| Critical path | Deferred |
| `GANTT_VIEW_GENERATED` on every GET | Seeded; not emitted on every read (avoid noise) |
| Completion path `docs/phase-complete/...` | Written under `src/docs/phase-complete/` (repo convention) |

## 26. Known Risks

- Large projects: full projection loads all phases/WBS/tasks in memory — Phase 23 performance review needed.
- Override + capacity interaction can leave unscheduled hours; surfaced as scheduling issues.
- Concurrent move/resize: partial unique ACTIVE index protects one override per task; last writer cancels prior ACTIVE in app flow.
- Approximate critical path not provided — UI must not invent CPM highlighting from this API.

## 27. Future Phases That Must Return to Gantt

| Phase | Return reason |
|---|---|
| 15 Rate Card | Optional cost-role overlays later |
| 16 Estimation roll-up | Effort roll-up in Gantt summary |
| 17 Finance | Cost phasing / burn overlays (permission-gated) |
| 18 Quote | Forecast as quote schedule basis |
| 19 Baseline / CR | Baseline comparison + edit gates |
| 20 Notifications | Move / milestone missed / recalc subscriptions |
| 21 AI | Suggest moves/deps; human approval required |
| 22 Reporting | Export / timeline dashboards |
| 23 Hardening | Performance, timezone, concurrency, auth depth |

---

## Acceptance Checklist

- [x] Current Gantt capability classified against TO-BE
- [x] Gantt projection API implemented/tested
- [x] Projection uses ProjectPhase / WBS / Task / TaskSchedule / TaskDependency
- [x] No independent GanttItem source-of-truth table
- [x] Milestone decision implemented (Model B) and documented
- [x] Recalculation delegates to Phase 13
- [x] Dependency editing uses TaskDependency actions
- [x] Manual move/resize implemented with override + scheduler respect
- [x] Authorization helpers implemented
- [x] Events seeded idempotently
- [x] Activity / audit / outbox wired
- [x] Does not claim finance / quote / baseline / timesheet / AI
- [x] `mvn compile` passes
- [x] `mvn test` passes
- [x] Completion file includes gap matrix
