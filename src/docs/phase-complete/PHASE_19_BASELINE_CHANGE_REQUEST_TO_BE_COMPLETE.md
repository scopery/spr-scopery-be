# Phase 19 — Baseline / Change Request TO-BE Complete

## 1. Summary

Phase 19 delivered `modules/projectbaseline`: ProjectBaseline (DRAFT→READY→APPROVED→ARCHIVED + `currentFlag`), immutable JSON snapshots (scope/schedule/estimate/finance/quote), ChangeRequest lifecycle (DRAFT→SUBMITTED→APPROVED→REJECTED/CANCELLED→APPLIED), ChangeRequestItem, ChangeImpact, ChangeApprovalAction, ChangeOrder foundation, strict post-baseline edit guard via `ProjectMutationGuard` + `BaselineApplyContext`, IAM rights, event seeds (`@Order(25)`), Flyway `V56`, and unit tests. Contract/invoice/billing/actuals/workflow engine were **not** implemented.

## 2. Source Inputs Reviewed

- `PHASE_19_BASELINE_CHANGE_REQUEST_TO_BE_DETAILED.md`
- Phase 18 quote / Phase 17 finance / Phase 16 estimation / Phase 13 schedule / Phase 09–10 project patterns
- Phase 04 outbox-audit / Phase 05 event registry / Phase 02 IAM
- Current BE: no prior baseline/change-request module

## 3. Current vs TO-BE Classification Matrix

| Capability | Classification | Status |
|---|---|---|
| ProjectBaseline | MUST | Implemented |
| Baseline scope/schedule/estimate/finance/quote snapshots | MUST | Implemented |
| Baseline validate/approve/mark-current | MUST | Implemented |
| ChangeRequest + items + impact | MUST | Implemented |
| Approve/reject/apply lifecycle | MUST | Implemented |
| Apply via domain actions (transactional) | MUST | Implemented (TASK CREATE/UPDATE, TASK_DEPENDENCY CREATE) |
| Post-baseline edit guard (strict) | MUST | Implemented |
| ChangeOrder foundation | MUST foundation | Implemented |
| Baseline/change IAM | MUST | Implemented |
| Event seeds | SEED_ONLY | Seeded |
| Detailed compare/diff APIs | OPTIONAL/DEFERRED | Deferred Phase 22 |
| Multi-step workflow / SoD | DEFERRED | Deferred Phase 34 |
| Contract/billing/actuals | OUT OF SCOPE | Not claimed |

## 4. Implemented in Current BE

- Prior phases: Project / WBS / Task / Dependency / Milestone, ScheduleRun, EstimationRun, ProjectFinanceScenario, QuoteVersion, IAM, Event Registry, Outbox/Audit, Idempotency-Key filter, `ProjectMutationGuard`

## 5. Implemented / Hardened in This Phase

- Flyway `V56__create_project_baseline_tables_phase19.sql`
- `Project.currentBaselineId` + JPA/mapper/`withCurrentBaselineId`
- Full `modules/projectbaseline` DDD module
- `BaselineSnapshotService` (`BASELINE_V1`)
- `ApplyChangeRequestService` + `BaselineApplyContext` bypass
- Strict guard in `ProjectMutationGuard` when `currentBaselineId != null`
- IAM: `PROJECT_BASELINE_MANAGEMENT`, `CHANGE_REQUEST_MANAGEMENT`, `CHANGE_ORDER_MANAGEMENT`
- Event seed `@Order(25)` (`SCOPERY_PROJECT_GOVERNANCE`)
- Audit event types for baseline/CR/CO/guard

## 6. Seed-only Items Added

- Event definitions for baseline / change request / impact / change order / post-baseline blocked
- No notification rules (deferred Phase 20)

## 7. Deferred Items and Target Phase

| Item | Target |
|---|---|
| Baseline compare / live diff report | Phase 22 |
| Multi-step approval / SoD / delegation | Phase 34 |
| Contract amendment / billing | Phase 36 |
| Actual cost comparison | Phase 37 |
| AI draft CR / impact summary | Phase 21 |
| Notifications for CR/baseline | Phase 20 |
| Document attachments | Phase 27 |

## 8. Governance Boundary Decision

- **Baseline** = immutable historical snapshot after approval; not the live project.
- **ChangeRequest** = proposal until applied; apply uses existing domain actions inside one transaction (no partial apply).
- **ChangeOrder** = commercial record only; not invoice/contract.
- **Post-baseline mode** = **STRICT** (controlled mutations blocked when `Project.currentBaselineId` is set, unless `BaselineApplyContext` is active during apply).

## 9. Entity Mapping

| Domain | Table |
|---|---|
| ProjectBaseline | `project_baseline` |
| ChangeRequest | `change_request` |
| ChangeRequestItem | `change_request_item` |
| ChangeImpact | `change_impact` |
| ChangeApprovalAction | `change_approval_action` |
| ChangeOrder | `change_order` |
| Project pointer | `project_project.current_baseline_id` |

Partial unique: one `current_flag=true` per project on baseline. Unique `(project_id, baseline_number)`, `(project_id, code)` on CR/CO.

## 10. API Changes

Base paths (`ProjectBaselineApiPaths`):

- Baselines: `POST/GET/PUT /api/projects/{projectId}/baselines`, refresh-snapshot, validate, approve, mark-current, archive; `GET .../baseline/current`
- Change requests: CRUD lifecycle submit/approve/reject/cancel/apply/archive
- Items nested under change requests
- Impact GET/PUT + calculate
- Change orders nested + by-id approve/reject/archive

Compare APIs not implemented.

## 11. Baseline Snapshot Strategy

Single `snapshot_json` + `summary_json` (no section table). Captures project metadata, phases, WBS, tasks, dependencies, milestones, and reference+summary for completed ScheduleRun / EstimationRun, APPROVED-or-current finance, APPROVED/SENT/ACCEPTED quote (or `NOT_APPLICABLE` if no quote). Does not copy ScheduledDailyWork rows.

## 12. Baseline Validation / Approval Strategy

Validate requires ≥1 phase, estimation run present, snapshot present → status `READY`. Approve from READY (or auto-validate DRAFT). Approved baseline immutable except mark-current/archive. Mark-current clears other current flags and sets `Project.currentBaselineId`.

Status model chosen: `DRAFT | READY | APPROVED | ARCHIVED` + boolean `currentFlag`.

## 13. Post-baseline Edit Guard Strategy

**Strict mode.** After current baseline exists, `ProjectMutationGuard.requireMutableProject` blocks phase/WBS/task/dependency/milestone/gantt mutations with `POST_BASELINE_EDIT_BLOCKED`. Apply path sets `BaselineApplyContext` ThreadLocal to bypass. Override permission path not implemented (deferred). Comments/view preferences remain unrestricted (no guard hooks).

## 14. ChangeRequest Lifecycle Strategy

DRAFT editable → SUBMIT (≥1 item) → APPROVE (impact required) / REJECT (reason) → APPLY (APPROVED only) / CANCEL (DRAFT|SUBMITTED) → ARCHIVE. Approval actions recorded in `change_approval_action`.

## 15. ChangeRequestItem Strategy

Stores targetType/operation/summary/before/after/apply payload JSON. Editable only while CR is DRAFT. Apply supports TASK CREATE/UPDATE and TASK_DEPENDENCY CREATE; unsupported ops fail before mutation.

## 16. ChangeImpact Strategy

Manual upsert in DRAFT; optional calculate sums estimateHours deltas from item snapshots. Finance/quote monetary fields masked in response without `PROJECT_FINANCE_MARGIN_VIEW`. Impact required before approve.

## 17. Apply Strategy

All-or-nothing `@Transactional`. Pre-validates supported operations, then applies via `CreateTaskAction` / `UpdateTaskAction` / `CreateTaskDependencyAction` under `BaselineApplyContext`. Records APPLY approval action + outbox/audit/activity. Does not create invoice/contract.

## 18. ChangeOrder Strategy

Create from APPROVED/APPLIED CR only. DRAFT→APPROVE/REJECT/ARCHIVE. Stores commercial impact JSON + optional quote version reference. `futureContractId` reserved; never creates contract/invoice.

## 19. Authorization Matrix

Rights seeded for all Phase 19 authorities listed in spec §11 under three permission codes. Workspace admin defaults include them.

## 20. Event Registry Seeder Matrix

`SCOPERY_PROJECT_GOVERNANCE` / owner `PROJECT_BASELINE` / `@Order(25)` — baseline, CR, item, impact, apply, CO, post-baseline blocked events.

## 21. Activity / Audit / Outbox Notes

`ProjectBaselineActivityLogger` + `ProjectBaselinePlatformPublisher` enqueue outbox and record audit on approve/mark-current/archive/submit/approve/reject/apply/CO approve.

## 22. Idempotency Strategy

Platform `Idempotency-Key` filter on POST when header present; no module-specific store.

## 23. Tests Added

- `ProjectBaselineLifecycleTest`
- `ChangeRequestLifecycleTest`
- `PostBaselineEditGuardTest`
- `ProjectBaselineEventDefinitionSeedInitializerTest`

## 24. Commands Run

```text
mvn -q compile -DskipTests
mvn -q test
```

## 25. Test Results

`mvn test` passed (exit 0) after Phase 19 changes. Compile passed.

## 26. Manual Verification

Checklist from spec §27 remains for runtime QA against a running DB (create artifacts → baseline → guard → CR → apply → CO). Not executed end-to-end in this agent session against a live PostgreSQL instance.

## 27. Assumptions

- Quote section optional (`NOT_APPLICABLE` when no quote version).
- Estimation run required for validate/approve path.
- Finance must be APPROVED or `currentFlag` when referenced.
- Apply support matrix intentionally minimal for Phase 19; other target types fail loudly.

## 28. Deviations From Prompt

- Baseline compare APIs deferred (optional in spec).
- Override-with-reason not implemented; strict block only.
- Apply supports a subset of item operations (documented).
- Post-baseline guard uses `Project.currentBaselineId` rather than querying baseline `currentFlag` alone (kept in sync by mark-current/archive).

## 29. Known Risks

- Large `snapshot_json` size for big WBS trees (Phase 23 performance review).
- Apply coverage limited; unsupported items block whole CR.
- Finance/quote mark-current not hooked into ProjectMutationGuard (scope/WBS/task path is).

## 30. Future Phases That Must Return to Baseline / Change Control

Phase 20 notifications, 21 AI, 22 reporting/compare, 23 hardening, 27 documents, 34 workflow, 36 contract/billing, 37 actuals — as listed in Phase 19 spec §30.
