# PHASE 17 — TO-BE Project Budget, Phase Finance, Custom Cost, Overhead, Revenue Split, Gross Margin & PBT Scenario

> Project: Scopery Backend  
> Phase: 17  
> Document type: TO-BE implementation-grade specification  
> Status: Planning / Gap-driven implementation spec  
> Depends on: Phase 00 — Master Backend Roadmap, Phase 01 — API Path / Security Baseline, Phase 02 — IAM Core, Phase 03 — Workspace Core, Phase 04 — Platform Audit / Outbox / Idempotency, Phase 05 — Event Registry, Phase 06 — Notification Engine, Phase 07 — AI Agent Platform, Phase 08 — Knowledge / Document Type Catalog, Phase 09 — Project Core / Phase / WBS / Task, Phase 10 — Project Authorization, Phase 11 — Project Template / Phase Catalog, Phase 12 — Resource Calendar / Capacity, Phase 13 — Task Scheduling Engine, Phase 14 — WBS-driven Gantt, Phase 15 — Rate Card / Cost Policy, Phase 16 — Estimation Roll-up  
> API base: `/api`  
> Primary module: `modules/projectfinance` or `modules/project/finance` depending on repository architecture  
> Related modules: `project`, `estimation`, `ratecard`, `workspace`, `iam`, `eventregistry`, `notification`, future `quote`, `baseline`, `change-request`, `billing`, `reporting`, `aiagent`, `timesheet`  
> Important rule: This file is **not an as-is document**. It defines the TO-BE planned finance scenario foundation. Phase 17 does not implement quote/contract value solver, invoice/billing, tax calculation, actual cost from timesheets, payroll/salary, or baseline/change request governance.

---

# 0. Purpose of this file

Phase 17 creates Scopery's planned project finance and profitability foundation.

Previous phases created:

```text
Phase 09:
- Project / Phase / WBS / Task

Phase 15:
- RateCard / CostRole / CCH / BillingRate / Inflation
- Rate snapshot contract

Phase 16:
- EstimationRun
- TaskEstimateSnapshot
- WBS/Phase/Project labor estimate roll-up
- Rate snapshots persisted
```

Phase 17 answers:

```text
What is the planned labor cost of the project?
What extra/custom costs exist by phase?
What vendor/direct costs exist?
What contingency is included?
What overhead policy applies?
What planned revenue is assigned to each phase?
What is gross margin?
What is profit before tax?
What is the budget of costs?
What scenario is current/approved for planning?
```

Phase 17 does **not** answer:

```text
What is the quote price sent to client?
What contract value should be solved from target margin?
What invoice amount is issued?
What tax is due?
What actual timesheet cost happened?
What baseline/change request is approved?
```

Those are later phases.

---

# 1. Source inputs

Before coding Phase 17, the agent must read:

```text
1. Current backend codebase
2. Phase 15 Rate Card / Cost Policy TO-BE spec and implementation
3. Phase 16 Estimation Roll-up TO-BE spec and implementation
4. Phase 09 Project Core TO-BE spec and implementation
5. Phase 10 Project Authorization TO-BE spec and implementation
6. Phase 13 Task Scheduling Engine TO-BE spec and implementation if schedule phasing is used
7. Phase 14 Gantt TO-BE spec and implementation if timeline phasing is used
8. Phase 04 Platform Audit / Outbox / Idempotency spec
9. Phase 05 Event Registry spec
10. Current BE feature/entity/business-rule inventory
11. Dynamic Work OS feature catalog
12. Existing finance/budget/profitability code if any
```

The agent must not implement Phase 17 from assumptions only.

---

# 2. Current expected backend state

After Phase 16, the backend should have:

```text
Project
ProjectPhase
Task
EstimationRun
TaskEstimateSnapshot
PhaseEstimateRollup
WbsEstimateRollup
ProjectEstimateSummary
Rate snapshot fields
```

Likely missing:

```text
ProjectFinanceScenario
ProjectPhaseFinance
ProjectCustomCost
ProjectVendorCost
ProjectContingency
ProjectOverheadPolicy
ProjectRevenueSplit
ProjectMarginSummary
Scenario approval/current marker
Finance version history
Finance permission model
Finance events
```

Phase 17 implements planned finance scenario, not actual accounting.

---

# 3. Phase 17 target statement

Phase 17 must deliver a future-ready planned finance foundation:

```text
1. Create ProjectFinanceScenario based on an EstimationRun.
2. Copy labor estimate totals into finance scenario snapshots.
3. Add custom phase costs such as travel, meals, tools, training, hardware, license, vendor.
4. Add contingency cost.
5. Add overhead policy.
6. Define planned revenue split by phase.
7. Calculate direct cost, overhead, total cost, planned revenue, gross margin, gross margin %, PBT, PBT %.
8. Store scenario summary so later rate/estimate changes do not silently change past scenarios.
9. Allow draft scenario edit and publish/approve/current scenario state.
10. Protect finance data with stronger permissions than normal project view.
11. Emit finance events and audit sensitive changes.
12. Clearly defer quote, contract value solver, tax, billing, actuals, and baseline/change request.
```

---

# 4. Financial boundary decisions

## 4.1 Planned finance, not accounting

Phase 17 calculates planned finance based on estimates and assumptions.

It is not general ledger accounting.

It is not invoice accounting.

It is not tax accounting.

## 4.2 Labor estimate comes from Phase 16

Phase 17 must not recalculate task rate snapshots directly unless explicitly needed.

Recommended:

```text
Finance scenario references EstimationRun.
Finance scenario copies/snapshots EstimationRun totals.
```

Why:

```text
EstimationRun already stores rate snapshots.
Finance scenario must preserve the estimate assumptions used.
```

## 4.3 Tax is separate

Rule:

```text
Tax must be separate from project net revenue/gross margin calculations.
```

Phase 17 can store tax notes or taxExcluded flag, but must not calculate tax unless a tax module exists.

## 4.4 Actual cost is separate

Rule:

```text
Actual cost requires approved source such as worklog, expense, vendor commitment, or invoice.
```

Phase 17 uses planned cost only.

Actual cost is deferred to:

```text
Phase 37 — Time / Attendance / Expense
Phase 36 — Contract / Billing / Revenue
```

## 4.5 Salary is not used

Phase 17 uses rate snapshots/CCH.

It must not expose or store salary.

---

# 5. Classification labels

Every requirement uses one label:

| Label | Meaning |
|---|---|
| `CURRENTLY_IMPLEMENTED` | Current backend already implements it. Agent must verify and test. |
| `PARTIALLY_IMPLEMENTED` | Current backend implements part of it. |
| `MUST_IMPLEMENT_IN_PHASE_17` | Must be implemented or hardened now. |
| `SEED_ONLY_IN_PHASE_17` | Seed events/permissions/config now; full consumer later. |
| `DEFERRED_TO_PHASE_XX` | Do not implement now; later phase must return. |
| `DEFERRED_TO_POST_23_BACKLOG` | Outside core 00–23 roadmap, part of full Work OS expansion. |
| `NOT_IN_SCOPE_FOR_BACKEND_NOW` | Explicitly not backend scope now. |

---

# 6. Phase 17 scope decision

## 6.1 Must implement now

```text
ProjectFinanceScenario
Finance scenario version/status lifecycle
Link to EstimationRun
Labor estimate snapshot import
ProjectPhaseFinance
Custom phase/direct costs
Vendor/direct costs foundation
Contingency cost
Overhead policy
Revenue split by phase
Finance summary calculation
Gross margin / margin %
Profit before tax / PBT %
Scenario current/approved marker
Finance permissions
Finance event definitions
Audit/activity/outbox
Tests
Completion report
```

## 6.2 Optional now

```text
Scenario comparison
Scenario duplicate
Finance preview API
Schedule-based cost phasing
Multi-currency grouping
Manual revenue amount per phase
Overhead by FTE-month
```

Implement only if product requires.

## 6.3 Must not implement now

```text
Quote price
Contract value solver
Formal quote approval
Invoice/billing
Revenue recognition
Tax calculation
Payment collection
Actual timesheet cost
Expense approval
Vendor purchase order
Payroll/salary
Baseline/change request approval
AI finance recommendation
```

---

# 7. Core formulas

## 7.1 Labor cost

From Phase 16:

```text
LaborCost =
ProjectEstimateSummary.totalLaborCost
```

By phase:

```text
PhaseLaborCost =
PhaseEstimateRollup.totalLaborCost
```

## 7.2 Custom phase cost

```text
PhaseCustomCost =
sum(ProjectCustomCost.amount where phaseId = phase)
```

Examples:

```text
Flight
Hotel
Meal
Software license
Hardware
Training
Workshop
Translation
Cloud environment
Third-party service
```

## 7.3 Vendor/direct cost

```text
PhaseVendorCost =
sum(ProjectVendorCost.amount where phaseId = phase)
```

Vendor cost is planned only in Phase 17.

Actual vendor commitment/invoice is deferred.

## 7.4 Contingency

Three supported models:

```text
FIXED_AMOUNT
PERCENT_OF_DIRECT_COST
PERCENT_OF_LABOR_COST
```

Formula:

```text
ContingencyAmount =
fixed amount
or DirectCostBeforeContingency × contingencyPercent
or LaborCost × contingencyPercent
```

## 7.5 Direct cost

```text
PhaseDirectCost =
PhaseLaborCost
+ PhaseCustomCost
+ PhaseVendorCost
+ PhaseContingency
```

```text
ProjectDirectCost =
sum(PhaseDirectCost)
```

## 7.6 Overhead

Overhead models:

```text
FIXED_AMOUNT
PERCENT_OF_LABOR_COST
PERCENT_OF_DIRECT_COST
PERCENT_OF_REVENUE
FTE_MONTH
PER_PHASE_FIXED
```

Default formula examples:

```text
OverheadAmount = LaborCost × overheadPercent
```

or

```text
OverheadAmount = DirectCost × overheadPercent
```

Important rule:

```text
Overhead affects PBT, not gross margin by default.
```

## 7.7 Budget of costs

```text
BudgetOfCosts =
ProjectDirectCost + ProjectOverhead
```

Alternative product definition may exclude overhead.

Completion file must document chosen definition.

Recommended:

```text
BudgetOfCosts includes direct cost + overhead.
```

## 7.8 Planned revenue

Revenue split must sum to 100% before scenario approval if percentage method is used.

Methods:

```text
MANUAL_AMOUNT
MANUAL_PERCENT
COST_PROPORTION
MILESTONE_BILLING future
```

For percentage split:

```text
PhaseRevenue =
ProjectPlannedRevenue × PhaseRevenuePercent
```

For cost proportion:

```text
PhaseRevenue =
ProjectPlannedRevenue × (PhaseDirectCost / ProjectDirectCost)
```

## 7.9 Gross margin

Default:

```text
GrossMargin =
PlannedRevenue - DirectCost
```

```text
GrossMarginPercent =
GrossMargin / PlannedRevenue
```

If PlannedRevenue = 0:

```text
GrossMarginPercent = null
```

## 7.10 Profit before tax

Default:

```text
ProfitBeforeTax =
PlannedRevenue - DirectCost - Overhead
```

```text
PbtPercent =
ProfitBeforeTax / PlannedRevenue
```

If PlannedRevenue = 0:

```text
PbtPercent = null
```

## 7.11 Target contract value solver

This formula belongs to Phase 18, not Phase 17:

```text
RequiredContractValue = CostBase / (1 - TargetMargin%)
```

Phase 17 may store targetMarginPercent as input for analysis, but must not claim commercial quote solver.

---

# 8. TO-BE capability matrix

---

## 8.1 FIN-001 — ProjectFinanceScenario

| Item | Value |
|---|---|
| Future capability | Versioned planned finance scenario for project |
| Current state | Missing/unknown |
| Phase 17 target | Implement |
| Classification | `MUST_IMPLEMENT_IN_PHASE_17` |

Rules:

```text
1. Scenario belongs to one project.
2. Scenario references one EstimationRun.
3. Project must not be ARCHIVED.
4. EstimationRun must belong to same project and be COMPLETED.
5. Scenario starts DRAFT.
6. DRAFT scenario can be edited.
7. APPROVED/PUBLISHED scenario immutable except archive.
8. One current scenario per project.
9. Scenario copies estimate summary/labor totals.
10. Scenario stores formulas/assumptions.
11. Scenario is planned finance, not quote.
```

Status:

```text
DRAFT
READY
APPROVED
CURRENT
ARCHIVED
```

Alternative:

```text
DRAFT / PUBLISHED / ARCHIVED
```

Completion file must document chosen status model.

---

## 8.2 FIN-002 — Import estimation summary

| Item | Value |
|---|---|
| Future capability | Use Phase 16 estimation as labor cost source |
| Current state | EstimationRun exists from Phase 16 |
| Phase 17 target | Implement |
| Classification | `MUST_IMPLEMENT_IN_PHASE_17` |

Rules:

```text
1. EstimationRun must be COMPLETED.
2. Finance scenario stores estimationRunId.
3. Finance scenario copies project total estimate hours.
4. Finance scenario copies total labor cost.
5. Finance scenario copies phase labor costs.
6. If EstimationRun changes later or new run created, scenario does not auto-change.
7. Re-import requires explicit action and creates scenario revision/new scenario.
```

---

## 8.3 FIN-003 — ProjectPhaseFinance

| Item | Value |
|---|---|
| Future capability | Store planned finance totals per project phase |
| Current state | Missing/unknown |
| Phase 17 target | Implement |
| Classification | `MUST_IMPLEMENT_IN_PHASE_17` |

Fields/totals:

```text
phaseLaborCost
customCost
vendorCost
contingencyAmount
directCost
overheadAmount
plannedRevenue
grossMargin
grossMarginPercent
profitBeforeTax
pbtPercent
```

Rules:

```text
1. One ProjectPhaseFinance per scenario + phase.
2. Phase must belong to project.
3. Labor cost imported from PhaseEstimateRollup.
4. Custom/vendor/contingency added in scenario.
5. Revenue split assigned to phase.
6. Phase finance is not invoice milestone.
```

---

## 8.4 FIN-004 — Custom phase cost

| Item | Value |
|---|---|
| Future capability | Add planned custom costs such as flight/meals/license/hardware |
| Current state | Missing/unknown |
| Phase 17 target | Implement |
| Classification | `MUST_IMPLEMENT_IN_PHASE_17` |

Cost categories:

```text
TRAVEL
MEAL
ACCOMMODATION
SOFTWARE_LICENSE
HARDWARE
CLOUD
TRAINING
WORKSHOP
TRANSLATION
SUBCONTRACTOR
OTHER
```

Rules:

```text
1. Scenario must be DRAFT to edit custom costs.
2. Cost amount >= 0.
3. Currency required and must match scenario currency unless multi-currency policy exists.
4. Phase required unless cost is project-level.
5. Description required for OTHER category.
6. Custom cost is planned only.
7. Custom cost is not actual expense.
```

---

## 8.5 FIN-005 — Vendor/direct cost foundation

| Item | Value |
|---|---|
| Future capability | Add planned vendor/subcontractor/direct costs |
| Current state | Missing/unknown |
| Phase 17 target | Implement planned vendor cost only |
| Classification | `MUST_IMPLEMENT_IN_PHASE_17` for planned vendor cost; actual vendor commitment deferred |

Rules:

```text
1. Scenario must be DRAFT.
2. Vendor name optional/free text in Phase 17 or external party id if Phase 29 exists later.
3. Amount >= 0.
4. Phase optional/project-level allowed.
5. Planned vendor cost is not purchase order.
6. Actual commitment/invoice deferred.
```

Future:

```text
Vendor commitment / PO / invoice → Phase 36 or Post-23 procurement backlog.
```

---

## 8.6 FIN-006 — Contingency

| Item | Value |
|---|---|
| Future capability | Add risk buffer to planned cost |
| Current state | Missing/unknown |
| Phase 17 target | Implement |
| Classification | `MUST_IMPLEMENT_IN_PHASE_17` |

Methods:

```text
FIXED_AMOUNT
PERCENT_OF_LABOR_COST
PERCENT_OF_DIRECT_COST
```

Rules:

```text
1. Scenario must be DRAFT to edit.
2. Percent >= 0.
3. Fixed amount >= 0.
4. Calculation method stored.
5. Contingency is planned cost.
6. Contingency does not create risk item automatically.
```

---

## 8.7 FIN-007 — Overhead policy

| Item | Value |
|---|---|
| Future capability | Model indirect project costs |
| Current state | Missing/unknown |
| Phase 17 target | Implement |
| Classification | `MUST_IMPLEMENT_IN_PHASE_17` |

Overhead examples:

```text
PMO
admin
office
software/tools
finance/legal
management
shared infrastructure
```

Methods:

```text
FIXED_AMOUNT
PERCENT_OF_LABOR_COST
PERCENT_OF_DIRECT_COST
PERCENT_OF_REVENUE
FTE_MONTH optional
PER_PHASE_FIXED optional
```

Rules:

```text
1. Scenario must be DRAFT to edit.
2. Overhead amount/percent >= 0.
3. Overhead affects PBT by default.
4. Overhead is not included in gross margin by default.
5. Formula assumptions stored.
```

---

## 8.8 FIN-008 — Revenue split

| Item | Value |
|---|---|
| Future capability | Allocate planned project revenue to phases |
| Current state | Missing/unknown |
| Phase 17 target | Implement |
| Classification | `MUST_IMPLEMENT_IN_PHASE_17` |

Methods:

```text
MANUAL_AMOUNT
MANUAL_PERCENT
COST_PROPORTION
MILESTONE_BILLING future
```

Rules:

```text
1. Project planned revenue required to calculate margin.
2. For MANUAL_PERCENT, phase percentages must sum to 100%.
3. For MANUAL_AMOUNT, phase amounts must sum to project planned revenue.
4. For COST_PROPORTION, calculate based on phase direct cost.
5. Revenue split is planned only.
6. Revenue split is not invoice/billing schedule.
```

---

## 8.9 FIN-009 — Margin summary

| Item | Value |
|---|---|
| Future capability | Calculate profitability metrics |
| Current state | Missing/unknown |
| Phase 17 target | Implement |
| Classification | `MUST_IMPLEMENT_IN_PHASE_17` |

Metrics:

```text
plannedRevenue
directCost
laborCost
customCost
vendorCost
contingency
overhead
budgetOfCosts
grossMargin
grossMarginPercent
profitBeforeTax
pbtPercent
averageCostRate
```

Rules:

```text
1. Use BigDecimal.
2. No floating point.
3. Divide-by-zero handled.
4. Currency policy explicit.
5. Tax excluded.
6. Actual cost excluded.
```

---

## 8.10 FIN-010 — Scenario approval/current marker

| Item | Value |
|---|---|
| Future capability | Identify working scenario vs accepted/current scenario |
| Current state | Missing/unknown |
| Phase 17 target | Implement simple status/current marker |
| Classification | `MUST_IMPLEMENT_IN_PHASE_17` |

Rules:

```text
1. Scenario can be DRAFT while being edited.
2. Scenario can be marked READY/APPROVED by authorized user.
3. Approved/current scenario immutable.
4. Only one current scenario per project.
5. Mark current audited.
6. Approval is not baseline approval.
7. Approval is not quote approval.
```

Formal workflow approval:

```text
DEFERRED_TO_PHASE_34_WORKFLOW_APPROVAL
```

---

## 8.11 FIN-011 — Scenario comparison

| Item | Value |
|---|---|
| Future capability | Compare finance scenarios |
| Current state | Missing |
| Phase 17 target | Optional/defer |
| Classification | `DEFERRED_TO_PHASE_22_REPORTING` unless simple comparison needed |

Future comparison:

```text
scenario A vs B
cost delta
revenue delta
margin delta
phase delta
custom cost delta
```

---

## 8.12 FIN-012 — Actual cost

| Item | Value |
|---|---|
| Future capability | Compare planned vs actual cost |
| Current state | Missing |
| Phase 17 target | Defer |
| Classification | `DEFERRED_TO_PHASE_37_TIME_ATTENDANCE_EXPENSE` and `PHASE_36_BILLING_REVENUE` |

Rule:

```text
Actual cost requires approved source such as worklog, expense, vendor commitment, or invoice.
```

Do not fake actuals in Phase 17.

---

## 8.13 FIN-013 — Contract value solver

| Item | Value |
|---|---|
| Future capability | Calculate required contract value from target margin |
| Current state | Missing |
| Phase 17 target | Defer |
| Classification | `DEFERRED_TO_PHASE_18_QUOTE_CONTRACT_VALUE_SOLVER` |

Formula later:

```text
RequiredContractValue = CostBase / (1 - TargetMargin%)
```

Phase 17 may store target margin for analysis but not quote/solver.

---

# 9. Entity model TO-BE

If current schema differs, agent must map actual fields and document gaps.

---

## 9.1 ProjectFinanceScenario — `project_finance_scenario`

Required fields:

```text
id UUID PK
project_id UUID NOT NULL
workspace_id UUID NOT NULL
estimation_run_id UUID NOT NULL
code VARCHAR(100) NULL
name VARCHAR(255) NOT NULL
description TEXT NULL
scenario_version INT NOT NULL
status VARCHAR(50) NOT NULL
currency_code VARCHAR(10) NOT NULL
planned_revenue DECIMAL(18,4) NOT NULL DEFAULT 0
revenue_split_method VARCHAR(50) NOT NULL
contingency_method VARCHAR(50) NULL
overhead_method VARCHAR(50) NULL
assumptions_json JSONB NULL
formula_version VARCHAR(50) NOT NULL
current_flag BOOLEAN NOT NULL DEFAULT false
approved_at TIMESTAMP NULL
approved_by UUID NULL
created_at / created_by
updated_at / updated_by
archived_at / archived_by
trace_id VARCHAR(100) NULL
version INT
```

Status:

```text
DRAFT
READY
APPROVED
CURRENT
ARCHIVED
```

If `CURRENT` is represented by `current_flag`, status can be:

```text
DRAFT
APPROVED
ARCHIVED
```

Completion file must document.

---

## 9.2 ProjectPhaseFinance — `project_phase_finance`

Required fields:

```text
id UUID PK
finance_scenario_id UUID NOT NULL
project_id UUID NOT NULL
project_phase_id UUID NOT NULL
phase_name_snapshot VARCHAR(255) NOT NULL
phase_order INT NULL
currency_code VARCHAR(10) NOT NULL

estimate_hours DECIMAL(12,2) NOT NULL DEFAULT 0
labor_cost DECIMAL(18,4) NOT NULL DEFAULT 0
custom_cost DECIMAL(18,4) NOT NULL DEFAULT 0
vendor_cost DECIMAL(18,4) NOT NULL DEFAULT 0
contingency_amount DECIMAL(18,4) NOT NULL DEFAULT 0
direct_cost DECIMAL(18,4) NOT NULL DEFAULT 0
overhead_amount DECIMAL(18,4) NOT NULL DEFAULT 0
budget_of_costs DECIMAL(18,4) NOT NULL DEFAULT 0
planned_revenue DECIMAL(18,4) NOT NULL DEFAULT 0
revenue_percent DECIMAL(8,4) NULL
gross_margin DECIMAL(18,4) NULL
gross_margin_percent DECIMAL(8,4) NULL
profit_before_tax DECIMAL(18,4) NULL
pbt_percent DECIMAL(8,4) NULL

created_at TIMESTAMP NOT NULL
updated_at TIMESTAMP NOT NULL
```

Constraint:

```text
unique finance_scenario_id + project_phase_id
```

---

## 9.3 ProjectCustomCost — `project_custom_cost`

Required fields:

```text
id UUID PK
finance_scenario_id UUID NOT NULL
project_id UUID NOT NULL
project_phase_id UUID NULL
category VARCHAR(100) NOT NULL
name VARCHAR(255) NOT NULL
description TEXT NULL
amount DECIMAL(18,4) NOT NULL
currency_code VARCHAR(10) NOT NULL
cost_date DATE NULL
status VARCHAR(50) NOT NULL
created_at / created_by
updated_at / updated_by
archived_at / archived_by
version INT
```

Status:

```text
ACTIVE
INACTIVE
ARCHIVED
```

Rules:

```text
amount >= 0
```

---

## 9.4 ProjectVendorCost — `project_vendor_cost`

Required fields:

```text
id UUID PK
finance_scenario_id UUID NOT NULL
project_id UUID NOT NULL
project_phase_id UUID NULL
vendor_name VARCHAR(255) NULL
external_party_id UUID NULL future
description TEXT NOT NULL
amount DECIMAL(18,4) NOT NULL
currency_code VARCHAR(10) NOT NULL
status VARCHAR(50) NOT NULL
created_at / created_by
updated_at / updated_by
archived_at / archived_by
version INT
```

Rules:

```text
amount >= 0
```

---

## 9.5 ProjectFinanceSummary — `project_finance_summary`

Required fields:

```text
id UUID PK
finance_scenario_id UUID NOT NULL UNIQUE
project_id UUID NOT NULL
currency_code VARCHAR(10) NOT NULL

total_estimate_hours DECIMAL(12,2) NOT NULL DEFAULT 0
total_labor_cost DECIMAL(18,4) NOT NULL DEFAULT 0
total_custom_cost DECIMAL(18,4) NOT NULL DEFAULT 0
total_vendor_cost DECIMAL(18,4) NOT NULL DEFAULT 0
total_contingency DECIMAL(18,4) NOT NULL DEFAULT 0
total_direct_cost DECIMAL(18,4) NOT NULL DEFAULT 0
total_overhead DECIMAL(18,4) NOT NULL DEFAULT 0
budget_of_costs DECIMAL(18,4) NOT NULL DEFAULT 0
planned_revenue DECIMAL(18,4) NOT NULL DEFAULT 0
gross_margin DECIMAL(18,4) NULL
gross_margin_percent DECIMAL(8,4) NULL
profit_before_tax DECIMAL(18,4) NULL
pbt_percent DECIMAL(8,4) NULL
average_cost_rate DECIMAL(18,4) NULL

formula_version VARCHAR(50) NOT NULL
created_at TIMESTAMP NOT NULL
updated_at TIMESTAMP NOT NULL
```

---

## 9.6 ProjectOverheadPolicySnapshot — optional embedded/table

Can be embedded in `ProjectFinanceScenario.assumptions_json` or separate table.

If separate:

```text
project_overhead_policy_snapshot
```

Fields:

```text
id UUID PK
finance_scenario_id UUID NOT NULL
method VARCHAR(50) NOT NULL
percent DECIMAL(8,4) NULL
fixed_amount DECIMAL(18,4) NULL
currency_code VARCHAR(10) NULL
notes TEXT NULL
created_at TIMESTAMP NOT NULL
```

Recommended:

```text
Embed in scenario for Phase 17 unless multiple overhead lines are required.
```

---

## 9.7 ProjectRevenueSplit — optional table

Can be represented by `ProjectPhaseFinance.plannedRevenue/revenuePercent`.

If detailed split needed:

```text
project_revenue_split
```

Fields:

```text
id UUID PK
finance_scenario_id UUID NOT NULL
project_phase_id UUID NOT NULL
method VARCHAR(50) NOT NULL
revenue_percent DECIMAL(8,4) NULL
revenue_amount DECIMAL(18,4) NULL
currency_code VARCHAR(10) NOT NULL
created_at / updated_at
```

Recommended:

```text
ProjectPhaseFinance fields are enough for Phase 17.
```

---

# 10. API TO-BE list

All APIs use `/api`.

---

## 10.1 Finance scenario APIs

```text
POST  /api/projects/{projectId}/finance-scenarios
GET   /api/projects/{projectId}/finance-scenarios
GET   /api/projects/{projectId}/finance-scenarios/{scenarioId}
PUT   /api/projects/{projectId}/finance-scenarios/{scenarioId}
POST  /api/projects/{projectId}/finance-scenarios/{scenarioId}/recalculate
POST  /api/projects/{projectId}/finance-scenarios/{scenarioId}/approve
POST  /api/projects/{projectId}/finance-scenarios/{scenarioId}/mark-current
PATCH /api/projects/{projectId}/finance-scenarios/{scenarioId}/archive
POST  /api/projects/{projectId}/finance-scenarios/{scenarioId}/duplicate
```

Create request:

```json
{
  "name": "Initial planned finance",
  "estimationRunId": "uuid",
  "currencyCode": "VND",
  "plannedRevenue": 1000000000,
  "revenueSplitMethod": "COST_PROPORTION",
  "contingency": {
    "method": "PERCENT_OF_DIRECT_COST",
    "percent": 10
  },
  "overhead": {
    "method": "PERCENT_OF_LABOR_COST",
    "percent": 15
  },
  "markAsCurrent": false
}
```

---

## 10.2 Phase finance APIs

```text
GET /api/projects/{projectId}/finance-scenarios/{scenarioId}/phases
GET /api/projects/{projectId}/finance-scenarios/{scenarioId}/phases/{phaseFinanceId}
PUT /api/projects/{projectId}/finance-scenarios/{scenarioId}/phases/{phaseFinanceId}/revenue
```

Revenue update request:

```json
{
  "plannedRevenue": 250000000,
  "revenuePercent": 25.0
}
```

Only DRAFT scenario can update revenue.

---

## 10.3 Custom cost APIs

```text
POST   /api/projects/{projectId}/finance-scenarios/{scenarioId}/custom-costs
GET    /api/projects/{projectId}/finance-scenarios/{scenarioId}/custom-costs
GET    /api/projects/{projectId}/finance-scenarios/{scenarioId}/custom-costs/{costId}
PUT    /api/projects/{projectId}/finance-scenarios/{scenarioId}/custom-costs/{costId}
PATCH  /api/projects/{projectId}/finance-scenarios/{scenarioId}/custom-costs/{costId}/archive
```

---

## 10.4 Vendor cost APIs

```text
POST   /api/projects/{projectId}/finance-scenarios/{scenarioId}/vendor-costs
GET    /api/projects/{projectId}/finance-scenarios/{scenarioId}/vendor-costs
GET    /api/projects/{projectId}/finance-scenarios/{scenarioId}/vendor-costs/{costId}
PUT    /api/projects/{projectId}/finance-scenarios/{scenarioId}/vendor-costs/{costId}
PATCH  /api/projects/{projectId}/finance-scenarios/{scenarioId}/vendor-costs/{costId}/archive
```

---

## 10.5 Finance summary APIs

```text
GET /api/projects/{projectId}/finance-scenarios/{scenarioId}/summary
GET /api/projects/{projectId}/finance/current
GET /api/projects/{projectId}/finance/current/summary
GET /api/projects/{projectId}/finance/current/phases
```

---

## 10.6 Finance preview/comparison APIs

Optional/deferred:

```text
POST /api/projects/{projectId}/finance-scenarios/{scenarioId}/preview
GET  /api/projects/{projectId}/finance-scenarios/compare?left=...&right=...
```

Comparison deferred to Phase 22 unless needed.

---

# 11. Authorization requirements

Finance is sensitive.

Required IAM authorities:

```text
PROJECT_FINANCE_VIEW
PROJECT_FINANCE_CREATE
PROJECT_FINANCE_UPDATE
PROJECT_FINANCE_RECALCULATE
PROJECT_FINANCE_APPROVE
PROJECT_FINANCE_MARK_CURRENT
PROJECT_FINANCE_ARCHIVE
PROJECT_FINANCE_MANAGE

PROJECT_FINANCE_COST_VIEW
PROJECT_FINANCE_COST_CREATE
PROJECT_FINANCE_COST_UPDATE
PROJECT_FINANCE_COST_ARCHIVE

PROJECT_FINANCE_REVENUE_VIEW
PROJECT_FINANCE_REVENUE_UPDATE

PROJECT_FINANCE_MARGIN_VIEW
PROJECT_FINANCE_EXPORT future
```

Rules:

```text
1. Project viewer is not automatically finance viewer.
2. Finance summary requires PROJECT_FINANCE_VIEW.
3. Margin fields require PROJECT_FINANCE_MARGIN_VIEW or PROJECT_FINANCE_VIEW depending on product.
4. Custom/vendor cost edits require finance cost permission.
5. Revenue split edit requires finance revenue update permission.
6. Approve/mark current requires stronger permission.
7. Finance data must not be returned in normal project/Gantt APIs.
8. AI/reporting consumers must enforce finance permissions.
```

Simplified mapping allowed:

```text
PROJECT_FINANCE_VIEW covers summary/margin for internal admin products.
```

But completion file must document.

---

# 12. Finance calculation algorithm

## 12.1 Create scenario

```text
1. Validate project and authorization.
2. Validate EstimationRun belongs to project and is COMPLETED.
3. Validate currency policy.
4. Create ProjectFinanceScenario DRAFT.
5. Import estimation phase roll-ups.
6. Create ProjectPhaseFinance rows.
7. Apply contingency policy.
8. Apply overhead policy.
9. Apply revenue split method.
10. Calculate phase totals.
11. Calculate project summary.
12. Store assumptions/formula version.
13. Emit events.
```

## 12.2 Recalculate scenario

Allowed only for DRAFT.

```text
1. Re-read current scenario custom costs/vendor costs/revenue settings.
2. Do not re-import EstimationRun unless explicitly requested.
3. Recompute phase finance.
4. Recompute summary.
5. Store formula version.
6. Audit.
```

If user wants latest estimation:

```text
Create new scenario from new EstimationRun or explicit re-import action.
```

## 12.3 Approve/mark current

```text
1. Scenario must be calculated successfully.
2. Revenue split valid.
3. No blocking validation errors.
4. User has PROJECT_FINANCE_APPROVE or MARK_CURRENT.
5. Scenario becomes immutable.
6. Only one current scenario per project.
7. Event emitted and audit created.
```

---

# 13. Validation rules before approval

Before scenario approval/current:

```text
1. Planned revenue >= 0.
2. All active phases have phase finance rows.
3. Revenue split sum equals planned revenue or 100% depending on method.
4. Currency is consistent unless multi-currency policy supported.
5. Direct cost calculated.
6. Overhead calculated.
7. Gross margin and PBT calculated.
8. No unresolved estimation rates if policy says block.
9. Scenario has formula version.
```

Recommended:

```text
Unresolved estimate rates should block approval.
```

---

# 14. Currency policy

Phase 17 should avoid uncontrolled currency conversion.

Supported policies:

```text
SINGLE_CURRENCY_REQUIRED
GROUP_BY_CURRENCY
CONVERT_WITH_SNAPSHOT future
```

Recommended Phase 17:

```text
SINGLE_CURRENCY_REQUIRED
```

Rules:

```text
1. Scenario currency required.
2. EstimationRun currency must match scenario currency.
3. Custom/vendor costs must match scenario currency.
4. If multiple currencies, reject or group by currency.
5. Do not convert currency without exchange-rate snapshot.
```

Currency exchange:

```text
DEFERRED_TO_PHASE_18_QUOTE or Phase 17 follow-up if product requires multi-currency.
```

---

# 15. Integration with Phase 18 Quote

Phase 18 will use finance scenario to:

```text
create quote draft
solve contract value from target margin
build commercial proposal
approval flow
client-facing price
```

Phase 17 must not do those.

Phase 17 outputs:

```text
planned cost base
planned revenue scenario
margin metrics
```

Phase 18 turns it into:

```text
commercial quote/contract value
```

---

# 16. Integration with Phase 19 Baseline / Change Request

Phase 19 may snapshot approved finance scenario into baseline.

After baseline:

```text
finance scenario changes may require change request.
```

Phase 17 itself does not baseline.

---

# 17. Integration with Reporting

Phase 22 will expose:

```text
finance dashboards
margin by project
margin by phase
cost breakdown
scenario comparison
portfolio profitability
```

Phase 17 only provides data and APIs.

---

# 18. Integration with AI Agent

Phase 21 may use finance data to:

```text
explain margin drivers
suggest cost reduction
suggest revenue split
identify risky phase costs
```

Rules:

```text
1. AI must enforce finance permissions.
2. AI suggestion cannot auto-change finance scenario.
3. AI output is proposal until human approval.
```

---

# 19. Event Registry integration

Recommended source system:

```text
SCOPERY_PROJECT_FINANCE
```

Required events:

```text
PROJECT_FINANCE_SCENARIO_CREATED
PROJECT_FINANCE_SCENARIO_UPDATED
PROJECT_FINANCE_SCENARIO_RECALCULATED
PROJECT_FINANCE_SCENARIO_APPROVED
PROJECT_FINANCE_SCENARIO_MARKED_CURRENT
PROJECT_FINANCE_SCENARIO_ARCHIVED
PROJECT_FINANCE_SCENARIO_DUPLICATED

PROJECT_PHASE_FINANCE_CALCULATED
PROJECT_FINANCE_SUMMARY_CALCULATED

PROJECT_CUSTOM_COST_CREATED
PROJECT_CUSTOM_COST_UPDATED
PROJECT_CUSTOM_COST_ARCHIVED

PROJECT_VENDOR_COST_CREATED
PROJECT_VENDOR_COST_UPDATED
PROJECT_VENDOR_COST_ARCHIVED

PROJECT_REVENUE_SPLIT_UPDATED
PROJECT_OVERHEAD_POLICY_UPDATED
PROJECT_CONTINGENCY_UPDATED

PROJECT_MARGIN_THRESHOLD_WARNING
```

Standard variables:

```text
actor.userId
organization.id
workspace.id
project.id
financeScenario.id
estimationRun.id
projectPhase.id
currency.code
plannedRevenue
directCost
overhead
grossMargin
grossMarginPercent
profitBeforeTax
pbtPercent
occurredAt
traceId
```

---

# 20. Notification integration

Phase 17 should not send broad notifications by default.

Optional/admin future:

```text
PROJECT_FINANCE_SCENARIO_APPROVED_EMAIL
PROJECT_MARGIN_THRESHOLD_WARNING_NOTIFICATION
PROJECT_FINANCE_SCENARIO_MARKED_CURRENT_NOTIFICATION
```

Recommended:

```text
Seed events only.
Notification rules deferred to Phase 20/22.
```

---

# 21. Platform audit/outbox/idempotency integration

## 21.1 Activity log actions

```text
PROJECT_FINANCE_SCENARIO_CREATED
PROJECT_FINANCE_SCENARIO_RECALCULATED
PROJECT_FINANCE_SCENARIO_APPROVED
PROJECT_FINANCE_SCENARIO_MARKED_CURRENT
PROJECT_CUSTOM_COST_CREATED
PROJECT_CUSTOM_COST_UPDATED
PROJECT_VENDOR_COST_CREATED
PROJECT_VENDOR_COST_UPDATED
PROJECT_REVENUE_SPLIT_UPDATED
PROJECT_OVERHEAD_POLICY_UPDATED
PROJECT_CONTINGENCY_UPDATED
```

## 21.2 Audit-sensitive actions

Audit:

```text
Finance scenario approved
Finance scenario marked current
Planned revenue changed
Revenue split changed
Custom cost added/updated/archived
Vendor cost added/updated/archived
Overhead policy changed
Contingency changed
Margin summary recalculated
```

Reason:

```text
Finance scenario affects profitability and future quote/baseline.
```

## 21.3 Idempotency

Recommended for:

```text
POST /api/projects/{projectId}/finance-scenarios
POST /api/projects/{projectId}/finance-scenarios/{scenarioId}/recalculate
POST /api/projects/{projectId}/finance-scenarios/{scenarioId}/approve
POST /api/projects/{projectId}/finance-scenarios/{scenarioId}/mark-current
POST /api/projects/{projectId}/finance-scenarios/{scenarioId}/custom-costs
POST /api/projects/{projectId}/finance-scenarios/{scenarioId}/vendor-costs
```

## 21.4 Outbox

Finance events should use platform outbox if available.

---

# 22. Business rules master

## 22.1 Scenario rules

```text
FIN-SCN-001 Project must exist.
FIN-SCN-002 Project must not be ARCHIVED.
FIN-SCN-003 EstimationRun must belong to project.
FIN-SCN-004 EstimationRun must be COMPLETED.
FIN-SCN-005 Scenario starts DRAFT.
FIN-SCN-006 DRAFT can be edited.
FIN-SCN-007 APPROVED/CURRENT scenario immutable.
FIN-SCN-008 Only one current scenario per project.
FIN-SCN-009 Scenario stores formula version and assumptions.
FIN-SCN-010 Scenario is planned finance, not quote.
```

## 22.2 Custom/vendor cost rules

```text
FIN-COST-001 Scenario must be DRAFT to edit costs.
FIN-COST-002 Amount >= 0.
FIN-COST-003 Currency must match scenario unless multi-currency policy.
FIN-COST-004 Phase if provided must belong to project.
FIN-COST-005 OTHER category requires description.
FIN-COST-006 Planned vendor cost is not actual vendor invoice.
```

## 22.3 Contingency rules

```text
FIN-CONT-001 Scenario must be DRAFT to edit contingency.
FIN-CONT-002 Percent >= 0.
FIN-CONT-003 Fixed amount >= 0.
FIN-CONT-004 Method stored.
FIN-CONT-005 Contingency included in direct cost by default.
```

## 22.4 Overhead rules

```text
FIN-OVH-001 Scenario must be DRAFT to edit overhead.
FIN-OVH-002 Percent/amount >= 0.
FIN-OVH-003 Overhead affects PBT by default.
FIN-OVH-004 Overhead not included in gross margin by default.
FIN-OVH-005 Formula assumptions stored.
```

## 22.5 Revenue split rules

```text
FIN-REV-001 Planned revenue >= 0.
FIN-REV-002 MANUAL_PERCENT split must sum to 100%.
FIN-REV-003 MANUAL_AMOUNT split must sum to planned revenue.
FIN-REV-004 COST_PROPORTION uses phase direct cost.
FIN-REV-005 Revenue split is not invoice schedule.
```

## 22.6 Margin rules

```text
FIN-MAR-001 GrossMargin = PlannedRevenue - DirectCost.
FIN-MAR-002 PBT = PlannedRevenue - DirectCost - Overhead.
FIN-MAR-003 Tax excluded.
FIN-MAR-004 Actual cost excluded.
FIN-MAR-005 Divide-by-zero handled.
FIN-MAR-006 BigDecimal required.
```

---

# 23. Error catalog requirements

Exact names follow project convention, but these concepts must exist.

```text
PROJECT_FINANCE_SCENARIO_NOT_FOUND
PROJECT_FINANCE_SCENARIO_PROJECT_ARCHIVED
PROJECT_FINANCE_SCENARIO_NOT_DRAFT
PROJECT_FINANCE_SCENARIO_ALREADY_APPROVED
PROJECT_FINANCE_SCENARIO_INVALID_STATUS
PROJECT_FINANCE_SCENARIO_CURRENT_CONFLICT
PROJECT_FINANCE_SCENARIO_ESTIMATION_NOT_FOUND
PROJECT_FINANCE_SCENARIO_ESTIMATION_NOT_COMPLETED
PROJECT_FINANCE_SCENARIO_ESTIMATION_PROJECT_MISMATCH
PROJECT_FINANCE_SCENARIO_CURRENCY_MISMATCH
PROJECT_FINANCE_SCENARIO_VALIDATION_FAILED

PROJECT_PHASE_FINANCE_NOT_FOUND
PROJECT_PHASE_FINANCE_PHASE_MISMATCH

PROJECT_CUSTOM_COST_NOT_FOUND
PROJECT_CUSTOM_COST_INVALID_AMOUNT
PROJECT_CUSTOM_COST_INVALID_CATEGORY
PROJECT_CUSTOM_COST_PHASE_MISMATCH
PROJECT_CUSTOM_COST_CURRENCY_MISMATCH

PROJECT_VENDOR_COST_NOT_FOUND
PROJECT_VENDOR_COST_INVALID_AMOUNT
PROJECT_VENDOR_COST_PHASE_MISMATCH
PROJECT_VENDOR_COST_CURRENCY_MISMATCH

PROJECT_REVENUE_SPLIT_INVALID_PERCENT_TOTAL
PROJECT_REVENUE_SPLIT_INVALID_AMOUNT_TOTAL
PROJECT_REVENUE_SPLIT_DIRECT_COST_ZERO

PROJECT_OVERHEAD_POLICY_INVALID
PROJECT_CONTINGENCY_INVALID

PROJECT_FINANCE_ACCESS_DENIED
PROJECT_FINANCE_MARGIN_ACCESS_DENIED
```

---

# 24. Required tests

Phase 17 is incomplete without tests.

---

## 24.1 Finance scenario tests

```text
createFinanceScenario_valid_success
createFinanceScenario_archivedProject_rejected
createFinanceScenario_estimationNotFound_rejected
createFinanceScenario_estimationNotCompleted_rejected
createFinanceScenario_estimationProjectMismatch_rejected
createFinanceScenario_importsLaborTotals
createFinanceScenario_storesFormulaVersion
updateDraftScenario_success
updateApprovedScenario_rejected
approveScenario_valid_success
approveScenario_missingRevenueSplit_rejected
markCurrent_onlyOneCurrentScenario
archiveScenario_blocksFurtherEdit
duplicateScenario_createsDraftCopy
```

## 24.2 Phase finance calculation tests

```text
phaseFinance_importsPhaseLaborCost
phaseFinance_addsCustomCost
phaseFinance_addsVendorCost
phaseFinance_appliesContingencyFixed
phaseFinance_appliesContingencyPercentDirectCost
phaseFinance_calculatesDirectCost
phaseFinance_appliesRevenueSplit
phaseFinance_calculatesGrossMargin
phaseFinance_calculatesPbt
```

## 24.3 Custom/vendor cost tests

```text
createCustomCost_valid_success
createCustomCost_negativeAmount_rejected
createCustomCost_otherWithoutDescription_rejected
createCustomCost_phaseDifferentProject_rejected
createCustomCost_currencyMismatch_rejected
updateCustomCost_approvedScenario_rejected
archiveCustomCost_valid_success

createVendorCost_valid_success
createVendorCost_negativeAmount_rejected
createVendorCost_phaseDifferentProject_rejected
createVendorCost_currencyMismatch_rejected
```

## 24.4 Overhead/contingency tests

```text
overhead_fixedAmount_success
overhead_percentLabor_success
overhead_percentDirectCost_success
overhead_percentRevenue_success
overhead_negativePercent_rejected
contingency_fixedAmount_success
contingency_percentLabor_success
contingency_negativeRejected
```

## 24.5 Revenue split tests

```text
revenueSplit_manualPercent_sum100_success
revenueSplit_manualPercent_not100_rejected
revenueSplit_manualAmount_matchesPlannedRevenue_success
revenueSplit_manualAmount_mismatch_rejected
revenueSplit_costProportion_success
revenueSplit_costProportion_zeroDirectCost_rejected
```

## 24.6 Margin tests

```text
grossMargin_revenueMinusDirectCost
grossMarginPercent_handlesZeroRevenue
pbt_revenueMinusDirectCostMinusOverhead
pbtPercent_handlesZeroRevenue
taxExcludedFromMargin
actualCostExcluded
bigDecimalNoFloatingPoint
```

## 24.7 Snapshot/history tests

```text
financeScenario_unchangedAfterEstimationRunChanges
financeScenario_unchangedAfterRateCardChanges
approvedScenario_immutable
currentScenario_switch_audited
```

## 24.8 Authorization tests

```text
viewFinance_withoutPermission_forbidden
viewMargin_withoutMarginPermission_forbiddenOrHidden
createScenario_withoutPermission_forbidden
updateRevenue_withoutPermission_forbidden
approveScenario_withoutPermission_forbidden
crossWorkspaceFinanceAccess_forbidden
normalProjectView_doesNotReturnFinance
```

## 24.9 Seeder/event tests

```text
financeEventSeeder_firstRun_createsDefinitions
financeEventSeeder_secondRun_noDuplicates
financePermissionSeeder_authoritiesExist
financeScenarioApproved_eventEmitted
marginSummaryCalculated_eventEmitted
customCostChanged_auditCreated
```

---

# 25. Manual verification checklist

Completion file must include:

```text
1. Create completed EstimationRun.
2. Create finance scenario from EstimationRun.
3. Confirm labor cost imported.
4. Add custom phase cost.
5. Add vendor cost.
6. Add contingency.
7. Add overhead.
8. Set planned revenue.
9. Set revenue split by phase.
10. Recalculate scenario.
11. Confirm direct cost, budget of costs, gross margin, PBT.
12. Approve scenario.
13. Confirm approved scenario immutable.
14. Mark scenario current.
15. Confirm only one current scenario.
16. Change estimation/rate after scenario and confirm scenario snapshot unchanged.
17. Confirm normal project/Gantt APIs do not expose finance.
18. Confirm no quote/contract/invoice/tax/actual cost records created.
```

---

# 26. Acceptance criteria

Phase 17 is accepted only if:

```text
1. Current finance capability is classified against TO-BE.
2. ProjectFinanceScenario implemented/tested.
3. Scenario imports EstimationRun totals and snapshots/tested.
4. ProjectPhaseFinance implemented/tested.
5. Custom costs implemented/tested.
6. Vendor planned costs implemented/tested.
7. Contingency implemented/tested.
8. Overhead implemented/tested.
9. Revenue split implemented/tested.
10. Finance summary/margins/PBT implemented/tested.
11. Approved/current scenario immutability implemented/tested.
12. Finance permissions implemented/tested.
13. Events seeded idempotently.
14. Activity/audit/outbox follows Phase 04.
15. No salary/payroll exposed.
16. Phase 17 does not falsely claim quote/contract solver/billing/tax/actuals/baseline.
17. mvn compile passes.
18. mvn test passes.
19. Completion file exists and includes gap matrix.
```

Agent must not mark complete if:

```text
mvn test fails
approved scenario can be edited
multiple current scenarios allowed
gross margin formula includes overhead when default says it should not
tax included in margin without tax module
actual cost faked from estimates
finance data exposed via normal project view
quote/contract solver claimed implemented
```

---

# 27. Required phase completion file

Agent must create:

```text
docs/phase-complete/PHASE_17_PROJECT_BUDGET_MARGIN_TO_BE_COMPLETE.md
```

Required sections:

```text
# Phase 17 — Project Budget / Margin TO-BE Complete

## 1. Summary
## 2. Source Inputs Reviewed
## 3. Current vs TO-BE Classification Matrix
## 4. Implemented in Current BE
## 5. Implemented / Hardened in This Phase
## 6. Seed-only Items Added
## 7. Deferred Items and Target Phase
## 8. Finance Boundary Decision
## 9. Entity Mapping
## 10. API Changes
## 11. Estimation Import Strategy
## 12. Custom Cost Strategy
## 13. Vendor Cost Strategy
## 14. Contingency Strategy
## 15. Overhead Strategy
## 16. Revenue Split Strategy
## 17. Margin / PBT Formula Version
## 18. Currency Policy
## 19. Scenario Status / Immutability Rules
## 20. Authorization Matrix
## 21. Event Registry Seeder Matrix
## 22. Activity / Audit / Outbox Notes
## 23. Idempotency Strategy
## 24. Tests Added
## 25. Commands Run
## 26. Test Results
## 27. Manual Verification
## 28. Assumptions
## 29. Deviations From Prompt
## 30. Known Risks
## 31. Future Phases That Must Return to Finance
```

---

# 28. Future phases that must return to Finance

## 28.1 Phase 18 — Quote / Contract Value Solver

Must consume:

```text
ProjectFinanceScenario
ProjectFinanceSummary
PhaseFinance
plannedRevenue
cost base
target margin
billing preview
```

Must add:

```text
quote version
commercial proposal
required contract value solver
client-facing price
quote approval
```

## 28.2 Phase 19 — Baseline / Change Request

Must snapshot:

```text
approved finance scenario
cost assumptions
revenue assumptions
margin summary
```

After baseline, finance scenario changes may require change request.

## 28.3 Phase 20 — Project Notifications

May notify:

```text
finance scenario approved
margin below threshold
finance scenario changed after baseline
```

## 28.4 Phase 21 — AI-assisted Planning

AI may explain margin/cost drivers and suggest improvements.

AI must require finance permission and human approval.

## 28.5 Phase 22 — Reporting

Reports:

```text
margin dashboard
phase cost breakdown
scenario comparison
portfolio profitability
finance export
```

## 28.6 Phase 23 — Core Hardening

Review:

```text
BigDecimal precision
formula versioning
currency behavior
authorization field masking
performance
audit coverage
```

## 28.7 Phase 36 — Contract / Billing / Revenue

Must add:

```text
contract
billing schedule
invoice
revenue recognition
payment status
```

## 28.8 Phase 37 — Time / Attendance / Expense

Must add:

```text
actual labor cost
actual expense
planned vs actual variance
```

---

# 29. Agent anti-bịa rules

The agent must not:

```text
1. Claim Phase 17 creates quote.
2. Claim Phase 17 solves contract value.
3. Claim Phase 17 creates invoice/billing.
4. Claim Phase 17 calculates tax.
5. Claim Phase 17 tracks actual cost.
6. Claim Phase 17 uses salary/payroll.
7. Expose finance fields through normal project/Gantt APIs.
8. Let approved/current scenario be edited.
9. Allow multiple current scenarios.
10. Change scenario silently when EstimationRun/rate changes later.
11. Include overhead in gross margin unless product explicitly changes formula.
12. Hide formula assumptions/version.
13. Hide deferred quote/billing/actual/baseline gaps.
```

---

# 30. Prompt to give coding agent

```text
You are implementing Phase 17 — TO-BE Project Budget, Phase Finance, Custom Cost, Overhead, Revenue Split, Gross Margin & PBT Scenario.

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
- Phase 13 Task Scheduling TO-BE spec
- Phase 14 WBS Gantt TO-BE spec
- Phase 15 Rate Card TO-BE spec
- Phase 16 Estimation Roll-up TO-BE spec
- Current backend code, migrations, tests

Your task:
1. Compare current finance capability against this Phase 17 TO-BE spec.
2. Classify each capability as CURRENTLY_IMPLEMENTED, PARTIALLY_IMPLEMENTED, MUST_IMPLEMENT_IN_PHASE_17, SEED_ONLY_IN_PHASE_17, DEFERRED_TO_PHASE_XX, or NOT_IN_SCOPE_FOR_BACKEND_NOW.
3. Implement only Phase 17 required items.
4. Implement ProjectFinanceScenario, ProjectPhaseFinance, ProjectCustomCost, ProjectVendorCost, ProjectFinanceSummary.
5. Import EstimationRun labor totals and preserve snapshots.
6. Implement custom costs, vendor planned costs, contingency, overhead, revenue split.
7. Implement direct cost, budget of costs, gross margin, gross margin %, profit before tax, PBT %.
8. Enforce approved/current scenario immutability.
9. Add finance permissions and event definitions.
10. Add tests listed in this spec.
11. Run mvn compile and mvn test.
12. Create docs/phase-complete/PHASE_17_PROJECT_BUDGET_MARGIN_TO_BE_COMPLETE.md with full gap matrix.

Do not implement or claim quote, contract value solver, invoice/billing, tax, actual cost, timesheet actuals, payroll/salary, baseline approval, or change request in this phase.
```

---

# 31. Quick tracking matrix

| Capability | Current backend | Phase 17 action | Later phase |
|---|---|---|---|
| ProjectFinanceScenario | Missing/unknown | Must implement | — |
| EstimationRun import | Missing/unknown | Must implement | — |
| ProjectPhaseFinance | Missing/unknown | Must implement | — |
| Custom phase cost | Missing/unknown | Must implement | — |
| Planned vendor cost | Missing/unknown | Must implement | Actuals later |
| Contingency | Missing/unknown | Must implement | — |
| Overhead | Missing/unknown | Must implement | — |
| Revenue split | Missing/unknown | Must implement | Billing later |
| Gross margin | Missing/unknown | Must implement | — |
| Profit before tax | Missing/unknown | Must implement | Tax excluded |
| Scenario approval/current | Missing/unknown | Must implement simple | Workflow Phase 34 |
| Scenario comparison | Missing | Defer | Phase 22 |
| Quote / contract solver | Missing | Defer | Phase 18 |
| Baseline finance snapshot | Missing | Defer | Phase 19 |
| Invoice/billing | Missing | Defer | Phase 36 |
| Actual cost | Missing | Defer | Phase 37 |
| Tax | Missing | Defer | Finance/tax module |
| AI finance recommendation | Missing | Defer | Phase 21 |

---

# 32. Final principle

Phase 17 is not complete when "cost fields can be stored."

Phase 17 is complete when Scopery can produce a planned profitability scenario:

```text
Estimation labor cost
+ custom cost
+ vendor cost
+ contingency
= direct cost

Planned revenue
- direct cost
= gross margin

Planned revenue
- direct cost
- overhead
= profit before tax
```

Finance scenario is not quote.

Planned cost is not actual cost.

Gross margin excludes tax.

Salary is not stored here.
