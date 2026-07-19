# Phase 09 — Project Core / Phase / WBS / Task TO-BE Complete

## 1. Summary

Phase 09 hardens the Project Planning foundation: workspace-scoped Project lifecycle, PhaseDefinition reference, ProjectPhase, WBS tree (move/cycle/archive), Task (estimateHours, dueDate, assignee), TaskDependency graph integrity, outbox/activity/immutable audit wiring, and seeds. Scheduling, Gantt, capacity, finance, quote, baseline, change request, agile, and AI planning are **explicitly not implemented**.

## 2. Source Inputs Reviewed

- `CLAUDE.md` / project coding conventions
- `PHASE_00_MASTER_ROADMAP_MODULAR_REVISED.md`
- `PHASE_09_PROJECT_CORE_WBS_TASK_TO_BE_DETAILED.md`
- Existing `modules/project/**` (V39 tables + actions)
- Phase 04 outbox/audit patterns (`CreateWorkspaceAction`, `DocumentTypePlatformPublisher`)
- Phase 03 workspace membership (`WorkspaceRepository`, `WorkspaceMemberRepository`)
- IAM project authorities (`IamAuthorities.PROJECT_*`)
- Phase 08 completion file structure

## 3. Current vs TO-BE Classification Matrix

| Area | Pre-Phase 09 | Phase 09 |
|---|---|---|
| Project CRUD/lifecycle | CURRENTLY_IMPLEMENTED | Hardened (ACTIVE workspace, owner member, DRAFT-only activate, orgId) |
| ProjectPhase | CURRENTLY_IMPLEMENTED | Hardened + outbox + lifecycle timestamps |
| PhaseDefinition catalog | CURRENTLY_IMPLEMENTED | Seeds expanded; advanced templates deferred Phase 11 |
| WBS tree | CURRENTLY_IMPLEMENTED | Hardened archive-project guard, path mismatch, events |
| Task | CURRENTLY_IMPLEMENTED | Required estimateHours, optional WBS, lifecycle timestamps, broader complete |
| TaskDependency | CURRENTLY_IMPLEMENTED | **Cycle detection fixed**; archived project/task eligibility |
| estimateHours | Partial (nullable) | MUST → NOT NULL + CHECK > 0 |
| Assignee validation | CURRENTLY_IMPLEMENTED | Kept + project owner membership |
| Outbox/immutable audit | Missing | Wired via `ProjectPlatformPublisher` |
| Events | Partial seeds | Expanded + emitted |
| Capacity/Scheduling/Gantt/Finance/Quote/Baseline/AI | Missing | Deferred |

## 4. Implemented in Current BE (pre-hardening)

- Six tables (`project_phase_definition`, `project_project`, `project_project_phase`, `project_wbs_node`, `project_task`, `project_task_dependency`)
- Full Action + QueryService + Controller surface for Project / Phase / WBS / Task / Dependency
- Activity logging via `ProjectActivityLogger`
- Basic IAM PROJECT_* / PHASE_* / WBS_* / TASK_* authorities
- Partial unit tests and event seeder

## 5. Implemented / Hardened in This Phase

- Flyway `V47__harden_project_module_phase09.sql`
- `ProjectMutationGuard` — archived / non-DRAFT-ACTIVE projects block child mutations
- `ProjectPlatformPublisher` — outbox enqueue + immutable audit helpers
- Create project: workspace must exist and be ACTIVE; owner active member; `organizationId` denormalized
- Activate project: DRAFT only
- Task: `estimateHours` required and > 0; `wbsNodeId` optional; complete from TODO|IN_PROGRESS|BLOCKED → DONE
- TaskDependency: outgoing BFS cycle detection; reject ARCHIVED/CANCELLED/DONE endpoints
- Domain lifecycle timestamps on Project, ProjectPhase, Task
- Event seed: `PROJECT_PHASE_UPDATED`; phase seeds ANALYSIS/SUPPORT/CLOSURE (keep MAINTENANCE)
- Error catalog expansions
- Expanded unit tests

## 6. Seed-only Items Added

- Project event definitions (SCOPERY_PROJECT), including `PROJECT_PHASE_UPDATED`
- System PhaseDefinitions: DISCOVERY, ANALYSIS, DESIGN, DEVELOPMENT, TESTING, DEPLOYMENT, SUPPORT, CLOSURE (+ legacy MAINTENANCE)
- IAM PROJECT_* authorities already seeded (no new fine-grained ACTIVATE/MOVE/ASSIGN authorities — Phase 10)

## 7. Deferred Items and Target Phase

| Item | Target |
|---|---|
| Fine-grained PROJECT_ACTIVATE / WBS_MOVE / TASK_ASSIGN / DEPENDENCY_* rights | Phase 10 |
| PhaseDefinition template library / clone project | Phase 11 |
| Capacity / resource calendar | Phase 12 |
| Scheduling / estimated finish / critical path | Phase 13 |
| Gantt projection / drag-drop | Phase 14 |
| Rate card / CCH | Phase 15 |
| Estimation roll-up | Phase 16 |
| Finance / budget / margin | Phase 17 |
| Quote | Phase 18 |
| Baseline / change request / full closeout | Phase 19 |
| Project notification digests / assignment alerts | Phase 20 |
| AI project plan generation | Phase 21 |
| Reporting / portfolio | Phase 22 |
| HTTP idempotency keys on POSTs | Recommended later (Phase 04 pattern available) |

## 8. Entity Mapping

| Entity | Table | Notes |
|---|---|---|
| Project | `project_project` | + lifecycle timestamps; organizationId wired |
| PhaseDefinition | `project_phase_definition` | Catalog; Phase 11 expands management |
| ProjectPhase | `project_project_phase` | + started/completed/archived timestamps |
| WbsNode | `project_wbs_node` | Uses `sort_order` / `level` / `title` (TO-BE order_index/depth) |
| Task | `project_task` | estimate required; WBS optional; DONE ≡ TO-BE COMPLETED |
| TaskDependency | `project_task_dependency` | Logical FS deps only; no scheduling |

## 9. API Changes

| Method | Path | Notes |
|---|---|---|
| * | `/api/projects/**` | Existing surface kept; behavior hardened |
| POST | `/api/projects/{id}/tasks` | `estimateHours` required; `wbsNodeId` optional |
| * | No Gantt/schedule/capacity/finance endpoints | Not added |

No new public path prefixes beyond existing project module routes.

## 10. Project Rules

- Workspace must exist and be ACTIVE
- Code unique within workspace (uppercase via `ProjectCode`)
- Owner (if set) must be active workspace member
- `organizationId` denormalized from workspace
- ARCHIVED cannot be updated; blocks all child mutations
- Only DRAFT can activate
- Statuses: DRAFT, ACTIVE, ON_HOLD, COMPLETED, ARCHIVED
- Events: PROJECT_CREATED / UPDATED / ACTIVATED / ARCHIVED via outbox

## 11. ProjectPhase Rules

- Project must be mutable (DRAFT/ACTIVE)
- PhaseDefinition must be ACTIVE when referenced
- Code + displayOrder unique within project
- Archive blocked if active WBS/tasks
- Events including PROJECT_PHASE_UPDATED

## 12. WBS Rules

- Parent must same project; move cannot create cycle / under descendant
- Archive blocked with active children or linked tasks
- Root sibling `sort_order` uniqueness (partial unique index)
- Archived project blocks create/update/move/archive
- WBS remains scope source of truth — **not** a Gantt

## 13. Task Rules

- Belongs to one project + one phase; WBS optional
- `estimateHours` required and > 0 (effort, not duration)
- `dueDate` optional deadline (not computed finish)
- `inChargeUserId` optional; must be active workspace member
- Status: TODO, IN_PROGRESS, BLOCKED, DONE (maps COMPLETED), CANCELLED, ARCHIVED
- Complete allowed from TODO / IN_PROGRESS / BLOCKED
- Archived project blocks all task mutations

## 14. TaskDependency Rules

- Same project; no self; no duplicate; no cycle (outgoing BFS)
- FINISH_TO_START (and other types stored) — **no auto-reschedule**
- ARCHIVED / CANCELLED / DONE tasks not eligible for new deps
- Archived project blocks create/remove

## 15. IAM Authorization Matrix

| Authority | Status |
|---|---|
| PROJECT_VIEW/CREATE/UPDATE/ARCHIVE | Present |
| PROJECT_PHASE_* VIEW/CREATE/UPDATE/ARCHIVE/MANAGE | Present |
| PROJECT_WBS_* VIEW/CREATE/UPDATE/ARCHIVE/MANAGE | Present (move uses UPDATE) |
| PROJECT_TASK_* VIEW/CREATE/UPDATE/ARCHIVE/MANAGE | Present (dependency uses TASK_UPDATE) |
| PROJECT_ACTIVATE / TASK_ASSIGN / TASK_DEPENDENCY_* fine grain | Deferred Phase 10 |

Workspace-scoped checks via `ProjectWorkspaceAuthorizationService`.

## 16. Event Registry Seeder Matrix

| Event | Seeded | Emitted |
|---|---|---|
| PROJECT_CREATED/UPDATED/ACTIVATED/ARCHIVED | Yes | Yes |
| PROJECT_PHASE_CREATED/UPDATED/ACTIVATED/COMPLETED/ARCHIVED | Yes | Yes |
| WBS_NODE_CREATED/UPDATED/MOVED/ARCHIVED | Yes | Yes |
| TASK_CREATED/UPDATED/ASSIGNED/STARTED/BLOCKED/COMPLETED/CANCELLED/ARCHIVED | Yes | Yes |
| TASK_DEPENDENCY_CREATED/REMOVED | Yes | Yes |
| PROJECT_ON_HOLD / PROJECT_COMPLETED | No dedicated codes | Hold/Complete: activity only |

Source system: `SCOPERY_PROJECT`. Owner module: `PROJECT`.

## 17. Notification Integration Notes

- No Phase 09 email templates or assignment digests
- Events are outbox-ready for Phase 20 notification consumers
- No claim of delivery/subscription UX

## 18. Activity / Audit / Outbox Notes

- Activity via `ProjectActivityLogger` on all write actions
- Outbox via `TransactionalOutboxService` (`ProjectPlatformPublisher`)
- Immutable audit: project/phase/WBS/task archive, dependency remove, task planning changes (assignee/estimate/dueDate)
- HTTP idempotency keys: **not** wired (documented gap)

## 19. Seed Data

- System PhaseDefinitions (idempotent): DISCOVERY, ANALYSIS, DESIGN, DEVELOPMENT, TESTING, DEPLOYMENT, SUPPORT, CLOSURE, MAINTENANCE
- Project event definitions (23 codes including PROJECT_PHASE_UPDATED)
- TASK_ASSIGNED variable specs seeded

## 20. Tests Added

`ProjectBusinessRulesActionTest` expanded (~27 cases) including:

- inactive workspace / owner not member
- activate from ACTIVE rejected
- estimate null/zero rejected; optional WBS success
- archived project blocks task/WBS/dependency
- dependency cycle / duplicate / self
- WBS move under descendant
- complete from TODO

`ProjectAuthorizationActionTest` constructors updated.  
`ProjectEventDefinitionSeedInitializerTest` still green.

## 21. Commands Run

```text
mvn -q -DskipTests compile
mvn -q -Dtest=ProjectBusinessRulesActionTest,ProjectAuthorizationActionTest,ProjectEventDefinitionSeedInitializerTest test
mvn -q test
```

## 22. Test Results

```text
mvn -q -DskipTests compile → SUCCESS
ProjectBusinessRulesActionTest + ProjectAuthorizationActionTest + ProjectEventDefinitionSeedInitializerTest → SUCCESS
mvn test (full suite) → BUILD SUCCESS
Tests run: 691, Failures: 0, Errors: 0, Skipped: 1
```

## 23. Manual Verification

- [ ] Flyway V47 applies on DB with V39 project tables
- [ ] Create project under ACTIVE workspace; reject inactive workspace
- [ ] Duplicate project code same workspace rejected
- [ ] Create phase / root WBS / child WBS / move; reject move under descendant
- [ ] Create task with estimateHours > 0; reject ≤ 0 / null
- [ ] Optional WBS task create succeeds
- [ ] Assignee non-member rejected
- [ ] Start/block/complete/cancel/archive task
- [ ] Dependency cycle rejected; no schedule side effects
- [ ] Archive project blocks child mutations
- [ ] Outbox + activity rows present for create/archive
- [ ] Rerun seeders — no duplicate phase/event codes

## 24. Assumptions

- `TaskStatus.DONE` is the product mapping of TO-BE `COMPLETED`
- WBS field names `sort_order` / `level` map to TO-BE `order_index` / `depth`
- WBS remains phase-bound (current model stricter than optional phase-free WBS)
- Hold/Complete project remain available without dedicated outbox event codes
- Fine-grained IAM gaps are acceptable until Phase 10

## 25. Deviations From Prompt

- Completion path is `src/docs/phase-complete/` (repo convention) not `docs/phase-complete/`
- Kept `DONE` instead of renaming enum to `COMPLETED`
- Kept MAINTENANCE phase seed alongside SUPPORT/CLOSURE
- Idempotency keys not implemented on POSTs
- Phase complete-all-tasks gate on CompleteProjectPhase not enforced (existing product rule: archive-with-active blocked instead)

## 26. Known Risks

- Pre-V47 null/zero estimate rows backfilled to `1` in migration — review production data before apply
- Making `wbs_node_id` nullable may affect clients that assumed required WBS
- Dependency cycle check is BFS in-memory; large graphs may need Phase 23 performance review
- Update task omitted JSON null may not clear WBS (null keeps existing)

## 27. Future Phases That Must Return to Project Core

- Phase 10 — Project authorization hardening
- Phase 11 — Templates / PhaseDefinition catalog expansion
- Phase 12 — Capacity (consumes estimateHours + assignee)
- Phase 13 — Scheduling (consumes TaskDependency; must not reinterpret estimate as duration)
- Phase 14 — WBS-driven Gantt projection
- Phase 15–18 — Rate card, roll-up, finance, quote
- Phase 19 — Baseline / change request snapshots
- Phase 20 — Project notifications
- Phase 21 — AI planning suggestions applied through Phase 09 actions
- Phase 22–23 — Reporting and core hardening

---

## PRJ Capability Gap Matrix (honest)

| Code | Classification | Result |
|---|---|---|
| PRJ-001 Project create | MUST_IMPLEMENT_IN_PHASE_09 | Implemented/hardened |
| PRJ-002 Project update | MUST_IMPLEMENT_IN_PHASE_09 | Implemented/hardened |
| PRJ-003 Project lifecycle | MUST (activate/archive) | Implemented/hardened; hold/complete present |
| PRJ-004 PhaseDefinition | MUST read; templates Phase 11 | Seeds + read/use hardened |
| PRJ-005 ProjectPhase create | MUST | Hardened |
| PRJ-006 ProjectPhase lifecycle | MUST | Hardened |
| PRJ-007 WBS create | MUST | Hardened |
| PRJ-008 WBS move/archive | MUST | Hardened + cycle tests |
| PRJ-009 Task create | MUST | Hardened |
| PRJ-010 Task update | MUST | Hardened |
| PRJ-011 Task lifecycle | MUST | Hardened |
| PRJ-012 TaskDependency | MUST | Hardened + cycle fix |
| PRJ-013 Estimate hours | MUST | Hardened (required > 0) |
| PRJ-014 Due date | MUST | Present; risk warnings deferred |
| PRJ-015 Assignee | MUST | Hardened |

**Not claimed:** capacity, scheduling, Gantt, rate card, finance, quote, baseline, AI planning.
