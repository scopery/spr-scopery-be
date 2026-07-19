# Phase 37 — Resource / Capacity / Effort / Utilization Complete

## 1. Summary

Phase 37 extends `modules/resourcecapacity` (Phase 12 calendars/allocations) with resource planning entities, effort/forecast rebuild, utilization/cost/risk, task assignment, actual effort, assignment conflicts, workload snapshots, and threshold/utilization HTTP. Migrations: **V82**. AI-001 tools are **seeded** into AiTool registry (V89) — execution remains stub/NO_OP.

## 2. Source Inputs Reviewed

- `PHASE_37_*_TO_BE_DETAILED.md`, Phase 12 capacity module, Phase 36 patterns, deferred AI-tool note.

## 3. Classification Matrix

| Capability | Status |
|---|---|
| CapacityCalendar / Exception / DayRule / ProjectAllocation | CURRENTLY_IMPLEMENTED (Phase 12) |
| ResourceProfile / Role / Skill | MUST — implemented |
| EffortEstimate + Forecast rebuild | MUST — implemented |
| Utilization / Capacity / CostInput rebuild | MUST — implemented |
| ResourceRiskFlag | MUST — implemented |
| TaskResourceAssignment HTTP+persist | MUST — implemented |
| ActualEffortRecord HTTP+persist | MUST — implemented (not payroll/timesheet) |
| AssignmentConflict detect/list/ack/recalculate | MUST — implemented |
| WorkloadSnapshot create/list | MUST — implemented (immutable create) |
| UtilizationThresholdPolicy GET/PUT | MUST — implemented |
| Utilization rebuild HTTP | MUST — implemented |
| AI-001 resource planning tools | **SEEDED** in AiTool registry (V89); execute = stub/NO_OP |

## 4. Already Done (prior pass)

- V82 tables, shared constants/errors/activity, profile/role/skill/effort/planning/risk packages
- Event seed `SCOPERY_CAPACITY` (31 events)
- Domain calculator + rebuild services

## 5. Newly Added / Completed in This Pass

- Task assignment + actual effort stacks (finished wiring)
- AssignmentConflict persistence + `AssignmentConflictController` (detect/list/ack/recalculate)
- WorkloadSnapshot persistence + controller
- Threshold policy GET/PUT + utilization rebuild controller
- Domain tests: assignment, actual effort, conflict, snapshot

## 6. Migrations

- `V82__create_resource_planning_tables_phase37.sql` (no new Flyway in this pass)

## 7. Seed-only / Deferred

| Item | Notes |
|---|---|
| AI-001 AiTool seeds | **Done** — seeded via `AiToolSeedInitializer` (RESOURCE_CAPACITY category) |
| Payroll / HR leave / timesheet | NOT_IN_SCOPE |
| Full profitability auto-wire from capacity cost input | Light cost rebuild only (Phase 36 path) |
| Notification fan-out on overload | **Done** — `ResourceOverloadEvaluationJob` → transactional outbox `RESOURCE_OVERLOAD_DETECTED` + Phase 35 alert match + in-app `NotificationItem` fan-out + email trigger → `EmailDispatch` → `EmailOutbox` (SMTP only when configured; default `LOG_ONLY`) |

## 5. Newly Added / Completed in This Pass (gap closure)

- `ResourceOverloadEvaluationService` + `ResourceOverloadEvaluationJob` (registered in `ScheduledJobRegistry`)
- Event seeds: `WORKLOAD_SNAPSHOT_CREATED`, `RESOURCE_OVERLOAD_DETECTED`
- Email template + static rule seed for overload (`SystemEmailTemplateInitializer`)
- Workload snapshot repo query `findWithOverload()`
- Tests: `ResourceOverloadEvaluationServiceTest`

## 8. Product Boundaries

ResourceProfile ≠ user; ResourceRole ≠ IAM; assignment ≠ access grant; capacity exception ≠ HR leave; actual effort ≠ payroll; missing rate → risk, no crash. Overload email delivery does **not** claim SMTP success without provider config.

## 9. Commands / Tests

```text
mvn -DskipTests compile
mvn -Dtest='com.company.scopery.modules.resourcecapacity.**,com.company.scopery.modules.trust.**,com.company.scopery.modules.integrationhub.**,com.company.scopery.modules.servicesupport.**,com.company.scopery.modules.documenthub.**,com.company.scopery.modules.profitability.**' test
```

## 10. Release Decision

**Phase 37 MUST core COMPLETE** at Phase-36 pragmatism depth. AI-001 registry seeds shipped; live tool handlers remain future work. Overload fan-out path is complete and honest about email provider limits.
