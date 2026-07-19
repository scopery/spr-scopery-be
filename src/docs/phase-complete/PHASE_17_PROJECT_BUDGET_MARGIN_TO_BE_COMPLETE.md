# Phase 17 — Project Budget / Margin TO-BE Complete

## 1. Summary

Phase 17 delivered `modules/projectfinance`: ProjectFinanceScenario (DRAFT/APPROVED/ARCHIVED + `currentFlag`), ProjectPhaseFinance, ProjectCustomCost, ProjectVendorCost, ProjectFinanceSummary, FinanceCalculationService (`FINANCE_V1`), project `currentFinanceScenarioId`, IAM rights, event seeds (`@Order(23)`), Flyway `V54`, and unit tests. Quote/contract solver, tax, billing, actual cost, timesheet, salary, baseline/change request, scenario comparison, and preview APIs were **not** implemented.

## 2. Source Inputs Reviewed

- `PHASE_17_PROJECT_BUDGET_MARGIN_TO_BE_DETAILED.md`
- Phase 16 estimation patterns (`CreateEstimationRunAction`, `EstimationEngineService`, `V53`, completion doc)
- Phase 15 rate card / Phase 09 project / Phase 10 authorization / Phase 04 outbox-audit / Phase 05 event registry
- Current BE: no prior finance module

## 3. Current vs TO-BE Classification Matrix

| Capability | Classification | Status |
|---|---|---|
| ProjectFinanceScenario | MUST | Implemented |
| EstimationRun import / snapshot | MUST | Implemented |
| ProjectPhaseFinance | MUST | Implemented |
| Custom phase cost | MUST | Implemented |
| Planned vendor cost | MUST | Implemented |
| Contingency | MUST | Implemented |
| Overhead | MUST | Implemented |
| Revenue split | MUST | Implemented |
| Margin / PBT summary | MUST | Implemented |
| Approve / mark current | MUST | Implemented |
| Finance IAM | MUST | Implemented |
| Event seeds | SEED_ONLY | Seeded |
| Scenario duplicate | MUST (API 10.1) | Implemented |
| Scenario comparison / preview | DEFERRED | Deferred Phase 22 |
| Quote / tax / actuals / baseline | OUT OF SCOPE | Not claimed |

## 4. Implemented in Current BE

- Prior phases: Project/Phase, EstimationRun + phase roll-up + project summary, IAM, Event Registry, Outbox/Audit, Idempotency-Key filter

## 5. Implemented / Hardened in This Phase

- Flyway `V54__create_project_finance_tables_phase17.sql`
- Full `modules/projectfinance` DDD module
- `Project.currentFinanceScenarioId` + JPA/mapper/`withCurrentFinanceScenarioId`
- IAM rights/permissions/authorities for PROJECT_FINANCE
- Event seed `@Order(23)` (`SCOPERY_PROJECT_FINANCE` / owner `PROJECT_FINANCE`)
- Audit event types: approve, mark-current, recalculate, revenue/cost/overhead/contingency changed

## 6. Seed-only Items Added

- Event definitions for finance lifecycle / costs / margin warning
- No notification rules (deferred Phase 20/22)

## 7. Deferred Items and Target Phase

| Item | Target |
|---|---|
| Scenario comparison / preview | Phase 22 |
| Quote / contract value solver | Phase 18 |
| Baseline finance snapshot | Phase 19 |
| Invoice / billing / tax | Phase 36 / tax module |
| Actual cost / timesheet | Phase 37 |
| Formal workflow approval | Phase 34 |
| AI finance recommendation | Phase 21 |
| Multi-currency conversion | Later |

## 8. Finance Boundary Decision

Phase 17 calculates **planned** finance only from EstimationRun labor snapshots + custom/vendor/contingency/overhead + planned revenue. Responses and docs do not claim quote, contract value, invoice, tax, or actual cost. Salary/payroll is never stored. Gross margin excludes overhead; PBT includes overhead. Tax excluded.

## 9. Entity Mapping

| Domain | Table |
|---|---|
| ProjectFinanceScenario | `project_finance_scenario` |
| ProjectPhaseFinance | `project_phase_finance` |
| ProjectCustomCost | `project_custom_cost` |
| ProjectVendorCost | `project_vendor_cost` |
| ProjectFinanceSummary | `project_finance_summary` |
| Project pointer | `project_project.current_finance_scenario_id` |

Children CASCADE delete from scenario. Partial unique index: one `current_flag=true` per project.

## 10. API Changes

Base paths in `ProjectFinanceApiPaths`:

- `POST/GET/PUT /api/projects/{projectId}/finance-scenarios`
- `POST .../recalculate|approve|mark-current|duplicate`
- `PATCH .../archive`
- `GET/PUT .../phases`, `PUT .../phases/{id}/revenue`
- Custom/vendor cost CRUD + archive
- `GET .../summary`
- Current: `GET /api/projects/{projectId}/finance/current` (+ `/summary|/phases`)

## 11. Estimation Import Strategy

Create scenario requires EstimationRun COMPLETED for same project. Currency must match estimate summary. Phase labor/hours copied from PhaseEstimateRollup into ProjectPhaseFinance. Recalculate does **not** re-import estimation (preserves snapshot). Later estimation/rate changes do not mutate existing scenarios.

## 12. Custom Cost Strategy

Categories: TRAVEL, MEAL, ACCOMMODATION, SOFTWARE_LICENSE, HARDWARE, CLOUD, TRAINING, WORKSHOP, TRANSLATION, SUBCONTRACTOR, OTHER. OTHER requires description. Amount ≥ 0; currency must match scenario. Project-level costs (null phase) allocated by labor proportion. ACTIVE only included in calc; archive sets ARCHIVED.

## 13. Vendor Cost Strategy

Planned vendor cost only (free-text vendor name). Same draft/currency/amount rules. Not PO/invoice.

## 14. Contingency Strategy

Methods: FIXED_AMOUNT, PERCENT_OF_LABOR_COST, PERCENT_OF_DIRECT_COST (direct before contingency). Stored on scenario; allocated to phases by labor proportion; included in direct cost.

## 15. Overhead Strategy

Methods: FIXED_AMOUNT, PERCENT_OF_LABOR_COST, PERCENT_OF_DIRECT_COST, PERCENT_OF_REVENUE, FTE_MONTH/PER_PHASE_FIXED (use fixed amount in Phase 17). Affects PBT only; not gross margin. Embedded on scenario.

## 16. Revenue Split Strategy

MANUAL_AMOUNT, MANUAL_PERCENT, COST_PROPORTION. Stored on ProjectPhaseFinance (`plannedRevenue` / `revenuePercent`). COST_PROPORTION uses phase direct cost share. Validation on calculate + approve.

## 17. Margin / PBT Formula Version

`FINANCE_V1`:

```text
DirectCost = Labor + Custom + Vendor + Contingency
BudgetOfCosts = DirectCost + Overhead
GrossMargin = PlannedRevenue - DirectCost
PBT = PlannedRevenue - DirectCost - Overhead
```

Percents null when PlannedRevenue = 0. BigDecimal HALF_UP scale 4.

## 18. Currency Policy

SINGLE_CURRENCY_REQUIRED: scenario currency required; estimation summary and costs must match. No FX conversion.

## 19. Scenario Status / Immutability Rules

Status: **DRAFT / APPROVED / ARCHIVED** + boolean `currentFlag` (not a CURRENT status value). Only DRAFT editable. APPROVED/current immutable except archive. Approve/mark-current block unresolved estimation rates. One current scenario per project (partial unique index + clear prior flag + project pointer).

## 20. Authorization Matrix

| Authority | Use |
|---|---|
| PROJECT_FINANCE_VIEW | List/get scenarios, phases, current |
| PROJECT_FINANCE_CREATE | Create / duplicate |
| PROJECT_FINANCE_UPDATE | Update draft |
| PROJECT_FINANCE_RECALCULATE | Recalculate |
| PROJECT_FINANCE_APPROVE | Approve |
| PROJECT_FINANCE_MARK_CURRENT | Mark current |
| PROJECT_FINANCE_ARCHIVE | Archive scenario |
| PROJECT_FINANCE_MANAGE | Full manage |
| PROJECT_FINANCE_COST_* | Custom/vendor cost |
| PROJECT_FINANCE_REVENUE_* | Revenue split |
| PROJECT_FINANCE_MARGIN_VIEW | Summary margin fields |

Enforced via `ProjectFinanceAuthorizationService` → `ProjectWorkspaceAuthorizationService`. Not exposed on normal project/Gantt APIs.

## 21. Event Registry Seeder Matrix

`SOURCE_SYSTEM=SCOPERY_PROJECT_FINANCE`, `OWNER=PROJECT_FINANCE`, `@Order(23)`:

PROJECT_FINANCE_SCENARIO_CREATED/UPDATED/RECALCULATED/APPROVED/MARKED_CURRENT/ARCHIVED/DUPLICATED, PROJECT_PHASE_FINANCE_CALCULATED, PROJECT_FINANCE_SUMMARY_CALCULATED, CUSTOM/VENDOR cost CRUD events, REVENUE_SPLIT/OVERHEAD/CONTINGENCY updated, PROJECT_MARGIN_THRESHOLD_WARNING.

## 22. Activity / Audit / Outbox Notes

Activity logger for create/update/recalculate/approve/mark-current/costs/revenue/overhead/contingency. Outbox enqueue on scenario events. Audit: approve, mark-current, recalculate, revenue/cost/overhead/contingency changes.

## 23. Idempotency Strategy

Platform `Idempotency-Key` filter for POSTs. Event seed find-or-create. Mark-current clears prior current. Create always new UUID.

## 24. Tests Added

- `FinanceCalculationServiceTest` — labor import, custom/vendor, contingency/overhead methods, revenue splits, zero revenue percents, no-reimport labor
- `CreateFinanceScenarioActionTest` — archived, estimation not completed, project mismatch, success + formula version
- `ApproveAndMarkCurrentFinanceScenarioActionTest` — approve, already approved, mark current clears previous
- `ProjectFinanceEventDefinitionSeedInitializerTest` — seed + no duplicate

## 25. Commands Run

```text
mvn -q compile -DskipTests
mvn -q test -Dtest=FinanceCalculationServiceTest,CreateFinanceScenarioActionTest,ApproveAndMarkCurrentFinanceScenarioActionTest,ProjectFinanceEventDefinitionSeedInitializerTest
mvn test
```

## 26. Test Results

- `mvn -q compile -DskipTests` — SUCCESS
- Phase 17 targeted tests — SUCCESS
- `mvn test` — Tests run: 876, Failures: 0, Errors: 0, Skipped: 1 — BUILD SUCCESS

## 27. Manual Verification

Not yet verified — requires running the application against PostgreSQL + Flyway V54 and calling finance APIs with JWT.

## 28. Assumptions

- Project-level custom/vendor costs allocated to phases by labor proportion (equal share if all labor zero)
- Contingency/overhead allocated the same way for phase display
- `markAsCurrent` on create approves after validation
- FTE_MONTH / PER_PHASE_FIXED use `overheadFixedAmount` in Phase 17
- Summary GET requires both VIEW and MARGIN_VIEW

## 29. Deviations From Prompt

- Module package `modules/projectfinance` (not nested under `project/finance`)
- Status model uses DRAFT/APPROVED/ARCHIVED + `currentFlag` (not CURRENT status enum value)
- Preview/compare APIs deferred (optional §10.6)
- Completion path: `src/docs/phase-complete/` (matches prior phases)

## 30. Known Risks

- Synchronous recalculate in request transaction for large phase/cost sets
- Concurrent mark-current last-write-wins without pessimistic lock (DB partial unique helps)
- MANUAL_PERCENT/AMOUNT require phase revenue fields set before approve

## 31. Future Phases That Must Return to Finance

- Phase 18 Quote / contract value solver
- Phase 19 Baseline / change request snapshot
- Phase 20/22 Notifications and reporting comparison
- Phase 21 AI suggestions (permission-gated)
- Phase 36 Billing / revenue recognition
- Phase 37 Actual cost from timesheet/expense
