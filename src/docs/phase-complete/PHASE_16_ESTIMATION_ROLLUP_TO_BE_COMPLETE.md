# Phase 16 — Estimation Roll-up TO-BE Complete

## 1. Summary

Phase 16 delivered the `modules/estimation` foundation: EstimationRun, TaskEstimateSnapshot, WbsEstimateRollup, PhaseEstimateRollup, ProjectEstimateSummary, EstimationEngineService, CostRoleResolutionService, project `currentEstimationRunId`, IAM rights, event seeds (`@Order(22)`), Flyway `V53`, and unit tests. No budget, margin, PBT, overhead, quote, baseline approval, timesheet, salary, estimate comparison, AI suggestions, or preview-task/preview-rate-impact APIs were implemented.

## 2. Source Inputs Reviewed

- `PHASE_16_ESTIMATION_ROLLUP_TO_BE_DETAILED.md`
- Phase 13 schedule run / engine patterns (`CreateScheduleRunAction`, `ScheduleEngineService`, `V50`)
- Phase 15 rate card (`RateResolutionService`, `RateSnapshot`, `CostRole`, member cost role)
- Project / Task / WBS / IAM / Event Registry / Audit / Outbox patterns
- Phase 15 completion doc structure

## 3. Current vs TO-BE Classification Matrix

| Capability | Classification | Status |
|---|---|---|
| EstimationRun | MUST | Implemented |
| TaskEstimateSnapshot | MUST | Implemented |
| Cost role resolution | MUST | Implemented |
| Rate snapshot persistence | MUST | Implemented |
| WBS roll-up | MUST | Implemented |
| Phase roll-up | MUST | Implemented |
| Project estimate summary | MUST | Implemented |
| Estimation APIs | MUST | Implemented |
| IAM rights | MUST | Implemented |
| Event seeds | SEED_ONLY | Seeded |
| Preview-task / preview-rate-impact | DEFERRED | Deferred |
| Budget / margin / quote / timesheet / salary | OUT OF SCOPE | Not claimed |

## 4. Implemented in Current BE

- Prior phases: Project/Task (`estimateHours`, `plannedRoleCode`, `inChargeUserId`, `dueDate`), WBS tree, RateResolutionService + RateSnapshot, CostRole + member assignment, IAM, Event Registry, Outbox/Audit.

## 5. Implemented / Hardened in This Phase

- Flyway `V53__create_estimation_tables_phase16.sql`
- Full `modules/estimation` DDD module (shared + estimationrun, tasksnapshot, wbsrollup, phaserollup, projectsummary, calculation, costrole)
- `Project.currentEstimationRunId` + JPA/mapper/`withCurrentEstimationRunId`
- IAM rights/permissions/authorities for ESTIMATION
- Event seed `@Order(22)` (`SCOPERY_ESTIMATION` / owner `ESTIMATION`)
- Audit event types: `ESTIMATION_RUN_MARKED_CURRENT`, `ESTIMATION_RUN_COMPLETED`, `ESTIMATION_RUN_FAILED`, `ESTIMATION_RATE_SNAPSHOT_USED`

## 6. Seed-only Items Added

- Event definitions for estimation lifecycle / warnings / roll-ups
- No notification rules (deferred Phase 20/22)
- No commercial estimate scenario seeds

## 7. Deferred Items and Target Phase

| Item | Target |
|---|---|
| Budget / margin / PBT / overhead | Phase 17 |
| Quote / contract value | Phase 18 |
| Baseline approval | Later governance |
| Timesheet / actual cost / salary | Later |
| Estimate comparison / AI suggestions | Later / AI |
| preview-task / preview-rate-impact APIs | Phase 22 reporting unless needed |
| Notification rules for estimation failures | Phase 20/22 |
| GROUP_BY_CURRENCY multi-currency totals UI | Later (enum accepted; engine fails under SINGLE_CURRENCY_REQUIRED) |

## 8. Estimation Boundary Decision

Phase 16 calculates **labor estimate preview** only: hours × adjusted cost rate, optional billing preview. Responses and docs avoid calling results “project budget”, “gross margin”, or “finance scenario”. Snapshots never store salary/payroll fields.

## 9. Entity Mapping

| Domain | Table |
|---|---|
| EstimationRun | `estimation_run` |
| TaskEstimateSnapshot | `estimation_task_snapshot` |
| WbsEstimateRollup | `estimation_wbs_rollup` |
| PhaseEstimateRollup | `estimation_phase_rollup` |
| ProjectEstimateSummary | `estimation_project_summary` |
| Project pointer | `project_project.current_estimation_run_id` |

Children CASCADE delete from `estimation_run`. Auditable columns on tables mirroring schedule pattern.

## 10. API Changes

Base paths in `EstimationApiPaths`:

- `POST/GET /api/projects/{projectId}/estimation-runs`
- `GET /api/projects/{projectId}/estimation-runs/{id}`
- `POST .../cancel`, `POST .../mark-current`
- Run-scoped: `.../tasks|wbs-rollups|phase-rollups|summary`
- Current: `GET /api/projects/{projectId}/estimation/current` (+ `/tasks|wbs-rollups|phase-rollups|summary`)

Rate detail fields on task snapshots are nulled unless caller has `ESTIMATION_RATE_DETAIL_VIEW` (hide detail; summary totals still visible with `ESTIMATION_SUMMARY_VIEW` / `ESTIMATION_TASK_VIEW`).

## 11. Estimation Algorithm

1. Mark RUNNING  
2. Load tasks / WBS  
3. Per task: exclude by status options → unestimated → resolve cost role → resolve rate (unless TASK_ESTIMATE_ONLY) → labor = hours × adjustedCostRate (HALF_UP scale 4) → optional billing preview  
4. SINGLE_CURRENCY_REQUIRED fails on mixed CALCULATED currencies  
5. WBS bottom-up (descendants, no double count; archived WBS skipped)  
6. Phase roll-up by `projectPhaseId`  
7. Project summary + averages (null when hours=0)  
8. COMPLETED + optional mark current  

## 12. Cost Role Resolution Strategy

Deterministic: `Task.plannedRoleCode` → ACTIVE CostRole WORKSPACE → ORGANIZATION → SYSTEM; else default `WorkspaceMemberCostRoleAssignment` for `inChargeUserId` effective on target date. No IAM roles, no invented defaults.

## 13. Rate Resolution / Snapshot Strategy

Uses Phase 15 `RateResolutionService.resolve(ResolveRateQuery)`. On `AppException`, snapshot status `RATE_UNRESOLVED` (run continues with warnings). Snapshot copies rate card/version/line, base/adjusted cost & billing, currency, inflation fields, resolved dates. Historical immutability by persistence (future rate changes do not rewrite snapshots).

## 14. WBS Roll-up Strategy

For each non-archived WBS node, sum task snapshots whose `wbsNodeId` is the node or a descendant. Tasks without WBS excluded from WBS roll-ups but included in project summary.

## 15. Phase Roll-up Strategy

Group non-null `projectPhaseId` snapshots; sum hours/labor/billing and counts.

## 16. Project Summary Strategy

Counts total/included/excluded/unestimated/unresolved role/rate; totals; average cost/billing when hours > 0; `warningCount` = unresolved + unestimated.

## 17. Currency Policy

Default `SINGLE_CURRENCY_REQUIRED`: multiple currencies among CALCULATED snapshots → run FAILED `ESTIMATION_MIXED_CURRENCY_NOT_ALLOWED`. `GROUP_BY_CURRENCY` accepted but Phase 16 does not split summary totals by currency (single currency field retained).

## 18. Authorization Matrix

| Authority | Use |
|---|---|
| ESTIMATION_VIEW | List/get runs, current run |
| ESTIMATION_RUN_CREATE | Create/execute run |
| ESTIMATION_RUN_CANCEL | Cancel |
| ESTIMATION_MARK_CURRENT | Mark current |
| ESTIMATION_TASK_VIEW | Task snapshots |
| ESTIMATION_WBS_VIEW | WBS roll-ups |
| ESTIMATION_PHASE_VIEW | Phase roll-ups |
| ESTIMATION_SUMMARY_VIEW | Project summary |
| ESTIMATION_RATE_DETAIL_VIEW | Rate snapshot fields on task responses |

Enforced via `EstimationAuthorizationService` → `ProjectWorkspaceAuthorizationService.requireProjectPermission`. Granted to same admin role lists as RATE_CARD entries.

## 19. Event Registry Seeder Matrix

`SOURCE_SYSTEM=SCOPERY_ESTIMATION`, `OWNER=ESTIMATION`, `@Order(22)`:

ESTIMATION_RUN_CREATED/STARTED/COMPLETED/FAILED/CANCELLED/MARKED_CURRENT, TASK_ESTIMATE_SNAPSHOT_CREATED, TASK_ESTIMATE_RATE_UNRESOLVED, TASK_ESTIMATE_ROLE_UNRESOLVED, WBS_ESTIMATE_ROLLED_UP, PHASE_ESTIMATE_ROLLED_UP, PROJECT_ESTIMATE_SUMMARY_CREATED, ESTIMATION_RATE_SNAPSHOT_USED, ESTIMATION_WARNING_CREATED.

## 20. Activity / Audit / Outbox Notes

Activity: run created/completed/failed/cancelled/marked-current. Outbox enqueue on lifecycle events. Audit: completed/failed/marked-current/rate-snapshot-used.

## 21. Idempotency Strategy

Event seed find-or-create by code. Estimation runs are always new UUIDs (no create-idempotency key in Phase 16). Cancel only for PENDING/RUNNING. Mark-current requires COMPLETED.

## 22. Tests Added

- `EstimationEngineServiceTest` — labor/billing, role/rate unresolved, WBS descendants, no-WBS project-only, phase/summary averages, mixed currency fail, snapshot independence, rate date strategies  
- `CostRoleResolutionServiceTest` — planned wins, member fallback, missing  
- `CreateEstimationRunActionTest` — archived, invalid mode, success completed  
- `EstimationEventDefinitionSeedInitializerTest` — seed + no duplicate  

## 23. Commands Run

```text
mvn -q -DskipTests compile
mvn -q test -Dtest='com.company.scopery.modules.estimation.**'
mvn -q test
```

## 24. Test Results

- `mvn -q -DskipTests compile` — SUCCESS  
- `mvn -q test -Dtest='com.company.scopery.modules.estimation.**'` — 17 tests, 0 failures (Engine 9, CostRole 3, CreateAction 3, EventSeed 2)  
- `mvn test` — Tests run: 856, Failures: 0, Errors: 0, Skipped: 1 — BUILD SUCCESS  
- IAM right catalog seed now includes ESTIMATION rights (observed seed count ~170–171 depending on prior DB state in tests)  

## 25. Manual Verification

Not yet verified — requires running the application against PostgreSQL + Flyway V53 and calling estimation APIs with JWT.

## 26. Assumptions

- Default include statuses: TODO, IN_PROGRESS, BLOCKED; DONE/CANCELLED/ARCHIVED opt-in via options  
- `TASK_SCHEDULED_START` / `TASK_SCHEDULED_FINISH` fall back to task plannedStart/due/today (no TaskSchedule join required for Phase 16 default path)  
- SCHEDULED_WORK_WITH_RATE uses estimate hours (scheduled-work hours integration deferred)  
- Missing project on create path relies on auth then optional find  

## 27. Deviations From Prompt

- Estimation API paths live in `EstimationApiPaths` (not duplicated into `ProjectApiPaths`)  
- Rate detail choice: **hide** rate fields without `ESTIMATION_RATE_DETAIL_VIEW` rather than hard-failing the task list  
- `GROUP_BY_CURRENCY` does not yet emit multi-currency summary buckets  

## 28. Known Risks

- Synchronous engine in request transaction may be slow for very large projects  
- Multi-instance concurrent “mark current” last-write-wins without locking  
- Cost role search by code depends on repository search filters matching scope correctly  

## 29. Future Phases That Must Return to Estimation

- Phase 17 Finance/Budget consume snapshots for margin/overhead  
- Phase 18 Quote consume billing preview / summaries  
- Phase 20/22 notifications for failed runs / unresolved rate-role  
- Reporting comparison between runs  
