# PHASE 36 — TO-BE Revenue & Profitability Visibility, Forecast, Cost Snapshot, Margin Dashboard & PM Adjustment

> Project: Scopery Backend  
> Phase: 36  
> Document type: TO-BE implementation-grade specification  
> Status: Planning / Revenue & profitability management view  
> Roadmap group: Post-core Project Intelligence expansion  
> Depends on: Phase 00 — Master Backend Roadmap, Phase 01 — API Path / Security Baseline, Phase 02 — IAM Core, Phase 03 — Workspace / Organization / Team, Phase 04 — Platform Audit / Outbox / Idempotency, Phase 05 — Event Registry, Phase 06 — Notification Engine, Phase 09 — Project Core / WBS / Task, Phase 10 — Project Authorization, Phase 18 — Quote / Commercial Proposal, Phase 19 — Baseline / Change Request, Phase 20 — Project Events / Notifications, Phase 22 — Reporting / Dashboard / Export, Phase 23 — Core Hardening, Phase 24 — Scope / Deliverable / Acceptance, Phase 25 — RAID / Decision Management, Phase 26 — Quality / Test / Release, Phase 28 — Application / Requirement / Traceability, Phase 29 — External Party / Client CRM / Stakeholder, Phase 32 — Search / Productivity, Phase 34 — Governance / Versioning / Permission / Audit, Phase 35 — Advanced Notifications  
> API base: `/api`  
> Primary module: `modules/profitability`, `modules/revenue`, or `modules/project-insight` depending on repository architecture  
> Related modules: `iam`, `workspace`, `project`, `quote`, `change-request`, `scope`, `deliverable`, `task`, `resource`, `cost`, `quality`, `raid`, `externalparty`, `notification`, `reporting`, `governance`, `audit`, `eventregistry`, future `resource-capacity`, `vendor-cost`, `ai-insight`  
> Important rule: Phase 36 gives PM/project owner visibility into revenue, cost, profit, margin, forecast, and profitability risk. The system should auto-derive as much as possible from quote, change request, scope, deliverable, task, resource, and project data. PM should mostly view, review, and make small manual adjustments when needed.

---

# 0. Purpose

Phase 36 creates the project profitability view.

The main question is:

```text
Is this project still profitable?
```

The system should help PM, Project Owner, Delivery Manager, Account Manager, and Founder see:

```text
project revenue
project forecast revenue
project estimated cost
project forecast cost
project profit
project margin
revenue risk
cost overrun risk
profitability status
variance against baseline
variance against quote
variance against current plan
which source caused the change
```

This phase is not designed for PM to manually enter lots of finance data.

The intended experience:

```text
System auto-calculates most values.
PM opens dashboard.
PM sees whether project is healthy, at risk, or loss risk.
PM can adjust small assumptions if needed.
PM can explain why forecast changed.
```

---

# 1. Product intention

Scopery is a project management platform with commercial awareness.

Phase 36 should make Scopery useful for service businesses such as:

```text
software agency
outsourcing team
consulting company
implementation partner
project-based service company
design/dev studio
client delivery team
```

For these teams, PM does not need to act like accountant.

But PM does need to know:

```text
Are we earning enough from this project?
Is scope creep hurting margin?
Did a change request increase revenue?
Are delays increasing cost?
Are defects/rework making project unprofitable?
Which project/client is most profitable?
```

Phase 36 gives this visibility.

---

# 2. Core principle

```text
Revenue + Cost + Risk = Profitability visibility.
```

Simple formulas:

```text
Forecast Profit = Forecast Revenue - Forecast Cost

Forecast Margin % = Forecast Profit / Forecast Revenue * 100

Revenue Variance = Forecast Revenue - Baseline Revenue

Cost Variance = Forecast Cost - Baseline Cost

Profit Variance = Forecast Profit - Baseline Profit
```

Important:

```text
These values are management forecasts.
They help PM make decisions.
They are not accounting statements.
```

---

# 3. Source data strategy

Phase 36 should auto-derive data from existing modules.

## 3.1 Revenue sources

Revenue can come from:

```text
QuoteVersion
QuoteLine
ChangeRequest
ScopePackage
Deliverable value
Project baseline value
Manual revenue adjustment
```

Examples:

```text
Quote accepted: +10,000 USD
Change Request approved/applied: +2,000 USD
Scope reduction: -1,000 USD
Manual correction: +500 USD
```

## 3.2 Cost sources

Cost can come from:

```text
estimated task effort
actual task effort if exists
resource cost rate if exists
team blended rate
manual cost estimate
manual cost adjustment
defect/rework estimate
vendor/external cost future
```

Examples:

```text
Estimated effort: 300 hours
Blended cost rate: 20 USD/hour
Estimated cost: 6,000 USD

Rework risk from critical defects: +800 USD
Manual adjustment: +300 USD
Forecast cost: 7,100 USD
```

## 3.3 Risk sources

Profitability risk can come from:

```text
deliverable delay
scope creep
open high risk
client feedback blocker
high severity defects
release delay
task effort overrun
change request not yet accepted
unowned revenue/cost assumptions
```

---

# 4. Target statement

Phase 36 must deliver:

```text
1. Project profitability profile.
2. Revenue sources and revenue forecast.
3. Cost sources and cost forecast.
4. Profitability plan/version.
5. Profitability snapshot.
6. Project profitability summary dashboard.
7. Workspace/client/project profitability aggregation.
8. Revenue/cost/profit variance.
9. Profitability risk flags.
10. Manual PM adjustments with reason.
11. Automatic recalculation from quote/change/task/deliverable/risk events.
12. Governance, versioning, audit, notifications, reports, and tests.
```

---

# 5. Current expected backend state

Before Phase 36, backend likely has:

```text
projects
tasks
task estimates maybe
quote versions
quote lines
change requests
scope/deliverables
acceptance records
risks/issues
defects
release status
governance snapshots
reporting/dashboard foundation
notification foundation
```

Likely missing:

```text
ProjectProfitabilityProfile
RevenueSource
CostSource
ProfitabilityPlan
ProfitabilityPlanVersion
RevenueForecast
CostForecast
ProfitabilitySnapshot
ProjectProfitabilitySummary
WorkspaceProfitabilitySummary
ProfitabilityAdjustment
ProfitabilityRiskFlag
ProfitabilityVariance
RateCard / BlendedCostRate foundation
Profitability recalculation jobs
```

---

# 6. Classification labels

Use these labels in completion file:

| Label | Meaning |
|---|---|
| `CURRENTLY_IMPLEMENTED` | Existing backend already supports it with tests. |
| `PARTIALLY_IMPLEMENTED` | Some support exists but gaps remain. |
| `MUST_IMPLEMENT_IN_PHASE_36` | Required now. |
| `MUST_HARDEN_IN_PHASE_36` | Existing behavior must be hardened now. |
| `SEED_ONLY_IN_PHASE_36` | Seed definitions/events/permissions/default configs only. |
| `DEFERRED_TO_PHASE_XX` | Later phase returns. |
| `DEFERRED_TO_POST_23_BACKLOG` | Later backlog item. |
| `NOT_IN_SCOPE_FOR_PHASE_36` | Explicitly not in this phase. |

---

# 7. Required capabilities

---

## 7.1 PROF-001 — ProjectProfitabilityProfile

Classification: `MUST_IMPLEMENT_IN_PHASE_36`

Purpose:

```text
Enable profitability tracking for a project.
```

Profile defines:

```text
default currency
tracking mode
revenue calculation mode
cost calculation mode
profitability owner
baseline source
portal visibility optional
status
```

Tracking modes:

```text
DISABLED
MANUAL
QUOTE_BASED
TASK_COST_BASED
HYBRID
```

Rules:

```text
1. One active profile per project.
2. Profile can be created from quote/project setup.
3. Profile stores configuration, not final calculated result.
4. Profile update triggers summary recalculation.
5. Profile access is permission-controlled.
```

---

## 7.2 REV-001 — RevenueSource

Classification: `MUST_IMPLEMENT_IN_PHASE_36`

Purpose:

```text
Track what contributes to forecast revenue.
```

Revenue source types:

```text
QUOTE_VERSION
QUOTE_LINE
CHANGE_REQUEST
SCOPE_PACKAGE
DELIVERABLE
PROJECT_BASELINE
MANUAL_REVENUE
MANUAL_ADJUSTMENT
```

Fields:

```text
source type
source target
amount
currency
included flag
confidence
status
reason
```

Rules:

```text
1. RevenueSource belongs to project profitability profile.
2. Source target must exist.
3. Source target must belong to same project if applicable.
4. Source can be included/excluded from forecast.
5. Exclusion requires reason.
6. Source amount changes audited.
```

---

## 7.3 REV-002 — RevenueForecast

Classification: `MUST_IMPLEMENT_IN_PHASE_36`

Purpose:

```text
Capture revenue forecast at a point in time.
```

Forecast types:

```text
BASELINE
CURRENT
BEST_CASE
WORST_CASE
COMMIT
MANUAL
SYSTEM_REBUILD
AI_ASSISTED_DRAFT
```

Rules:

```text
1. Forecast belongs to project profitability profile.
2. Forecast can be generated from active revenue sources.
3. Forecast can include confidence percentage.
4. AI-assisted forecast remains draft until user accepts.
5. Forecast update creates event if material change.
```

---

## 7.4 COST-001 — CostSource

Classification: `MUST_IMPLEMENT_IN_PHASE_36`

Purpose:

```text
Track what contributes to forecast cost.
```

Cost source types:

```text
TASK_ESTIMATE
TASK_ACTUAL
WBS_ESTIMATE
RESOURCE_ASSIGNMENT
TEAM_BLENDED_RATE
DEFECT_REWORK
RELEASE_DELAY
MANUAL_COST
MANUAL_ADJUSTMENT
VENDOR_COST_FUTURE
```

Rules:

```text
1. CostSource belongs to project profitability profile.
2. Source can reference task, WBS, defect, release, resource, or manual entry.
3. Cost amount/currency required.
4. System-generated cost sources should be rebuildable.
5. Manual adjustment requires reason.
6. Source does not represent payroll.
```

---

## 7.5 COST-002 — CostForecast

Classification: `MUST_IMPLEMENT_IN_PHASE_36`

Purpose:

```text
Capture estimated project cost forecast.
```

Cost forecast inputs:

```text
estimated task effort
actual effort if available
resource cost rate
blended team cost rate
manual cost assumptions
rework estimate
delay estimate
```

Rules:

```text
1. Forecast belongs to profile.
2. Forecast stores amount, currency, confidence, generated_by.
3. Forecast can be rebuilt from CostSources.
4. Cost forecast changes can trigger profitability risk.
5. Cost values are sensitive by default.
```

---

## 7.6 RATE-001 — RateCard / BlendedCostRate foundation

Classification: `MUST_IMPLEMENT_IN_PHASE_36`

Purpose:

```text
Provide simple cost rate assumptions for project profitability.
```

Rate types:

```text
WORKSPACE_DEFAULT
TEAM_DEFAULT
ROLE_DEFAULT
PROJECT_DEFAULT
RESOURCE_SPECIFIC future
```

Rules:

```text
1. Rate belongs to workspace or project.
2. Rate has currency and amount per hour/day.
3. Rate can be used to convert effort into cost.
4. Sensitive access controlled.
5. Rate change does not rewrite finalized snapshots.
```

Example:

```text
Developer blended cost: 20 USD/hour
QA blended cost: 15 USD/hour
PM blended cost: 25 USD/hour
```

---

## 7.7 PLAN-001 — ProfitabilityPlan

Classification: `MUST_IMPLEMENT_IN_PHASE_36`

Purpose:

```text
Represent planned revenue/cost/profit baseline for project.
```

Plan types:

```text
QUOTE_BASED
BASELINE_BASED
MANUAL
HYBRID
```

Rules:

```text
1. Plan belongs to profile.
2. Plan has versions.
3. Plan can be finalized/locked.
4. Plan stores assumptions.
5. Plan is the reference point for variance.
```

---

## 7.8 PLAN-002 — ProfitabilityPlanVersion

Classification: `MUST_IMPLEMENT_IN_PHASE_36`

Purpose:

```text
Immutable version of profitability assumptions.
```

Version contains:

```text
baseline revenue
baseline cost
baseline profit
baseline margin
planned revenue
planned cost
planned profit
planned margin
assumption notes
source quote version
source baseline id
```

Rules:

```text
1. Draft editable.
2. Finalized version immutable.
3. New version supersedes old current version.
4. GovernanceVersionRecord should reference finalized version.
```

---

## 7.9 ADJ-001 — ProfitabilityAdjustment

Classification: `MUST_IMPLEMENT_IN_PHASE_36`

Purpose:

```text
Allow PM/owner to adjust revenue or cost forecast when system estimate needs correction.
```

Adjustment types:

```text
REVENUE_INCREASE
REVENUE_DECREASE
COST_INCREASE
COST_DECREASE
SCOPE_CHANGE_IMPACT
REWORK_COST
DELAY_COST
DISCOUNT
MANUAL_CORRECTION
```

Rules:

```text
1. Adjustment belongs to project/profile.
2. Adjustment requires reason.
3. Adjustment affects forecast after applied.
4. Adjustment can link to ChangeRequest, Risk, Issue, Deliverable, Defect, or Task.
5. Adjustment is audited.
6. Cancelled adjustment no longer affects forecast.
```

---

## 7.10 SNAP-001 — ProfitabilitySnapshot

Classification: `MUST_IMPLEMENT_IN_PHASE_36`

Purpose:

```text
Immutable timestamped state of project profitability.
```

Snapshot metrics:

```text
baselineRevenue
baselineCost
baselineProfit
baselineMarginPercent

forecastRevenue
forecastCost
forecastProfit
forecastMarginPercent

revenueVariance
costVariance
profitVariance
marginVariancePercent

revenueAtRisk
costOverrunRisk
profitAtRisk
```

Rules:

```text
1. Snapshot belongs to project/profile.
2. Snapshot is immutable.
3. Snapshot source recorded.
4. Snapshot can feed reports/dashboard.
5. Sensitive values masked without permission.
```

Snapshot sources:

```text
MANUAL
SCHEDULED_REBUILD
QUOTE_ACCEPTED
CHANGE_REQUEST_APPLIED
TASK_COST_RECALCULATED
DELIVERABLE_ACCEPTED
RISK_CREATED
DEFECT_REWORK_UPDATED
PLAN_VERSION_FINALIZED
```

---

## 7.11 SUM-001 — ProjectProfitabilitySummary

Classification: `MUST_IMPLEMENT_IN_PHASE_36`

Purpose:

```text
Fast read model for PM dashboard.
```

Fields:

```text
projectId
clientId
currency
forecastRevenue
forecastCost
forecastProfit
forecastMarginPercent
baselineRevenue
baselineCost
baselineProfit
baselineMarginPercent
revenueAtRisk
costOverrunRisk
profitAtRisk
profitabilityStatus
lastSnapshotAt
updatedAt
```

Profitability statuses:

```text
NO_DATA
HEALTHY
WATCH
AT_RISK
LOSS_RISK
LOSS
SUPERSEDED
DISABLED
```

Rules:

```text
1. Summary derived from source records.
2. Summary can be rebuilt.
3. Summary is permission-protected.
4. Summary should be cheap to query for dashboards.
```

---

## 7.12 SUM-002 — WorkspaceProfitabilitySummary

Classification: `MUST_IMPLEMENT_IN_PHASE_36`

Purpose:

```text
Aggregate profitability across projects and clients.
```

Dimensions:

```text
workspace
client
project
project manager
team
currency
month
quarter
status
```

Metrics:

```text
totalForecastRevenue
totalForecastCost
totalForecastProfit
averageMarginPercent
revenueAtRisk
profitAtRisk
lossRiskProjectCount
healthyProjectCount
```

Rules:

```text
1. Aggregates respect permissions.
2. Multi-currency not summed without conversion policy.
3. Export audited.
```

---

## 7.13 RISK-001 — ProfitabilityRiskFlag

Classification: `MUST_IMPLEMENT_IN_PHASE_36`

Purpose:

```text
Flag risk that can hurt revenue, cost, profit, or margin.
```

Risk reasons:

```text
SCOPE_CREEP
DELIVERABLE_DELAY
ACCEPTANCE_DELAY
CHANGE_REQUEST_UNCERTAIN
TASK_EFFORT_OVERRUN
DEFECT_REWORK
RELEASE_DELAY
RESOURCE_COST_INCREASE
CLIENT_FEEDBACK_BLOCKER
MANUAL_RISK
```

Risk impact types:

```text
REVENUE_RISK
COST_RISK
PROFIT_RISK
MARGIN_RISK
```

Rules:

```text
1. Risk flag belongs to profile.
2. Can be system-generated or manual.
3. Can link to project risk/issue/defect/task/deliverable/change request.
4. Open risk flags can affect summary status.
5. Risk flag does not mutate source object.
```

Status:

```text
OPEN
MITIGATED
CLOSED
ARCHIVED
```

---

## 7.14 VAR-001 — ProfitabilityVariance

Classification: `MUST_IMPLEMENT_IN_PHASE_36`

Purpose:

```text
Explain difference between baseline, planned, and forecast values.
```

Variance types:

```text
REVENUE_BASELINE_TO_FORECAST
COST_BASELINE_TO_FORECAST
PROFIT_BASELINE_TO_FORECAST
MARGIN_BASELINE_TO_FORECAST
MONTH_OVER_MONTH
CHANGE_REQUEST_IMPACT
REWORK_IMPACT
DELAY_IMPACT
MANUAL_ADJUSTMENT_IMPACT
```

Rules:

```text
1. Variance derived from snapshots and forecast records.
2. Variance can be positive or negative.
3. Explanation can be manual or generated.
4. Sensitive values masked.
```

---

## 7.15 AUTO-001 — Automatic recalculation

Classification: `MUST_IMPLEMENT_IN_PHASE_36`

Purpose:

```text
Keep profitability dashboard updated without PM manual work.
```

Trigger recalculation on:

```text
QuoteVersion accepted/finalized
ChangeRequest applied
RevenueSource changed
CostSource changed
Task estimate changed
Task actual effort changed if available
Deliverable accepted
Defect/rework estimate changed
ProfitabilityAdjustment applied/cancelled
Risk flag created/closed
Rate card changed
```

Rules:

```text
1. Recalculation job is idempotent.
2. Recalculation creates snapshot if material change or policy says.
3. Summary updated after recalculation.
4. Recalculation errors logged and do not corrupt summary.
```

---

## 7.16 THRESH-001 — Profitability threshold policy

Classification: `MUST_IMPLEMENT_IN_PHASE_36`

Purpose:

```text
Define health thresholds for status calculation.
```

Example thresholds:

```text
HEALTHY margin >= 30%
WATCH margin >= 20%
AT_RISK margin >= 10%
LOSS_RISK margin >= 0%
LOSS margin < 0%
```

Rules:

```text
1. Workspace can configure default thresholds.
2. Project can override if permission allows.
3. Threshold changes audited.
4. Threshold changes update summary status.
```

---

## 7.17 PORTAL-001 — Client-safe commercial value summary

Classification: `SEED_ONLY_IN_PHASE_36` or optional if Phase 30 exists.

Purpose:

```text
Optionally expose selected commercial value to client portal if the product wants it.
```

Default:

```text
portal visibility disabled
```

Rules:

```text
1. Internal profitability is hidden.
2. Cost/profit/margin never shown to client portal by default.
3. Only explicitly client-visible revenue/commercial value can be shown.
4. Portal access requires active grant.
```

---

## 7.18 GOV-001 — Profitability governance

Classification: `MUST_IMPLEMENT_IN_PHASE_36` if Phase 34 exists.

Rules:

```text
1. Finalized ProfitabilityPlanVersion locked.
2. ProfitabilitySnapshot immutable.
3. Manual adjustment requires reason.
4. Sensitive profitability access audited.
5. Restore/revert creates new version/snapshot.
6. Finalized plan cannot be silently edited.
```

---

## 7.19 NOTIF-001 — Profitability notifications

Classification: `MUST_IMPLEMENT_IN_PHASE_36` if Phase 35 exists.

Notifications:

```text
profitability plan finalized
forecast revenue changed materially
forecast cost changed materially
margin dropped below threshold
project entered loss risk
profitability risk created
manual adjustment applied
snapshot created
```

Rules:

```text
1. Notification content must be permission-safe.
2. Recipients usually profitability owner, PM, project owner, workspace admin.
3. Sensitive values masked if recipient lacks permission.
```

---

## 7.20 AI-001 — AI-assisted profitability explanation

Classification: `SEED_ONLY_IN_PHASE_36` or `MUST_IMPLEMENT_IN_PHASE_36` if Phase 21 exists.

AI can help with:

```text
explain why profit changed
summarize margin risk
suggest which source caused cost overrun
summarize revenue/cost variance
suggest PM actions to improve margin
```

Rules:

```text
1. AI output is explanation/proposal only.
2. AI cannot change profitability values automatically.
3. AI cannot apply adjustment.
4. AI cannot expose hidden cost/profit values.
```

---

# 8. Entity model TO-BE

---

## 8.1 ProjectProfitabilityProfile — `profit_project_profile`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
project_id UUID NOT NULL
external_organization_id UUID NULL
default_currency VARCHAR(10) NOT NULL
tracking_mode VARCHAR(50) NOT NULL
revenue_calculation_mode VARCHAR(50) NOT NULL
cost_calculation_mode VARCHAR(50) NOT NULL
profitability_owner_user_id UUID NULL
portal_commercial_value_visible BOOLEAN NOT NULL DEFAULT false
status VARCHAR(50) NOT NULL
notes TEXT NULL
created_at / created_by
updated_at / updated_by
archived_at / archived_by
trace_id VARCHAR(100) NULL
version INT
```

Status:

```text
ACTIVE
DISABLED
ARCHIVED
```

Constraint:

```text
unique workspace_id + project_id
```

---

## 8.2 RevenueSource — `profit_revenue_source`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
project_id UUID NOT NULL
profitability_profile_id UUID NOT NULL
source_type VARCHAR(100) NOT NULL
source_id UUID NULL
title VARCHAR(255) NOT NULL
description TEXT NULL
amount DECIMAL(19,4) NOT NULL
currency VARCHAR(10) NOT NULL
confidence_percent DECIMAL(5,2) NULL
included_in_forecast BOOLEAN NOT NULL DEFAULT true
status VARCHAR(50) NOT NULL
reason TEXT NULL
created_at / created_by
updated_at / updated_by
archived_at / archived_by
trace_id VARCHAR(100) NULL
version INT
```

Status:

```text
DRAFT
ACTIVE
SUPERSEDED
EXCLUDED
ARCHIVED
```

---

## 8.3 RevenueForecast — `profit_revenue_forecast`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
project_id UUID NOT NULL
profitability_profile_id UUID NOT NULL
forecast_type VARCHAR(50) NOT NULL
currency VARCHAR(10) NOT NULL
forecast_amount DECIMAL(19,4) NOT NULL
confidence_percent DECIMAL(5,2) NULL
forecast_date DATE NOT NULL
assumption_notes TEXT NULL
generated_by VARCHAR(50) NOT NULL
status VARCHAR(50) NOT NULL
created_at / created_by
archived_at / archived_by
trace_id VARCHAR(100) NULL
version INT
```

Generated by:

```text
MANUAL
SYSTEM
AI_ASSISTED_DRAFT
```

---

## 8.4 CostSource — `profit_cost_source`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
project_id UUID NOT NULL
profitability_profile_id UUID NOT NULL
source_type VARCHAR(100) NOT NULL
source_id UUID NULL
title VARCHAR(255) NOT NULL
description TEXT NULL
estimated_effort_hours DECIMAL(19,4) NULL
rate_amount DECIMAL(19,4) NULL
amount DECIMAL(19,4) NOT NULL
currency VARCHAR(10) NOT NULL
confidence_percent DECIMAL(5,2) NULL
included_in_forecast BOOLEAN NOT NULL DEFAULT true
status VARCHAR(50) NOT NULL
reason TEXT NULL
created_at / created_by
updated_at / updated_by
archived_at / archived_by
trace_id VARCHAR(100) NULL
version INT
```

Status:

```text
DRAFT
ACTIVE
SUPERSEDED
EXCLUDED
ARCHIVED
```

---

## 8.5 CostForecast — `profit_cost_forecast`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
project_id UUID NOT NULL
profitability_profile_id UUID NOT NULL
forecast_type VARCHAR(50) NOT NULL
currency VARCHAR(10) NOT NULL
forecast_amount DECIMAL(19,4) NOT NULL
confidence_percent DECIMAL(5,2) NULL
forecast_date DATE NOT NULL
assumption_notes TEXT NULL
generated_by VARCHAR(50) NOT NULL
status VARCHAR(50) NOT NULL
created_at / created_by
archived_at / archived_by
trace_id VARCHAR(100) NULL
version INT
```

---

## 8.6 RateCard — `profit_rate_card`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
project_id UUID NULL
rate_code VARCHAR(150) NOT NULL
name VARCHAR(255) NOT NULL
rate_type VARCHAR(50) NOT NULL
role_name VARCHAR(150) NULL
team_id UUID NULL
currency VARCHAR(10) NOT NULL
amount_per_hour DECIMAL(19,4) NULL
amount_per_day DECIMAL(19,4) NULL
status VARCHAR(50) NOT NULL
created_at / created_by
updated_at / updated_by
archived_at / archived_by
trace_id VARCHAR(100) NULL
version INT
```

Constraint:

```text
unique workspace_id + project_id + rate_code
```

---

## 8.7 ProfitabilityPlan — `profit_plan`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
project_id UUID NOT NULL
profitability_profile_id UUID NOT NULL
plan_code VARCHAR(150) NULL
name VARCHAR(255) NOT NULL
plan_type VARCHAR(50) NOT NULL
status VARCHAR(50) NOT NULL
current_version_id UUID NULL
created_at / created_by
updated_at / updated_by
archived_at / archived_by
trace_id VARCHAR(100) NULL
version INT
```

---

## 8.8 ProfitabilityPlanVersion — `profit_plan_version`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
project_id UUID NOT NULL
profitability_plan_id UUID NOT NULL
version_number INT NOT NULL
version_label VARCHAR(100) NULL
currency VARCHAR(10) NOT NULL

baseline_revenue DECIMAL(19,4) NOT NULL DEFAULT 0
baseline_cost DECIMAL(19,4) NOT NULL DEFAULT 0
baseline_profit DECIMAL(19,4) NOT NULL DEFAULT 0
baseline_margin_percent DECIMAL(9,4) NULL

planned_revenue DECIMAL(19,4) NOT NULL DEFAULT 0
planned_cost DECIMAL(19,4) NOT NULL DEFAULT 0
planned_profit DECIMAL(19,4) NOT NULL DEFAULT 0
planned_margin_percent DECIMAL(9,4) NULL

assumption_notes TEXT NULL
source_quote_version_id UUID NULL
source_baseline_id UUID NULL
finalized_flag BOOLEAN NOT NULL DEFAULT false
finalized_at TIMESTAMP NULL
finalized_by UUID NULL
status VARCHAR(50) NOT NULL
created_at / created_by
updated_at / updated_by
archived_at / archived_by
trace_id VARCHAR(100) NULL
version INT
```

Constraint:

```text
unique profitability_plan_id + version_number
```

---

## 8.9 ProfitabilityAdjustment — `profit_adjustment`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
project_id UUID NOT NULL
profitability_profile_id UUID NOT NULL
adjustment_type VARCHAR(50) NOT NULL
title VARCHAR(255) NOT NULL
description TEXT NULL
amount DECIMAL(19,4) NOT NULL
currency VARCHAR(10) NOT NULL
source_type VARCHAR(100) NULL
source_id UUID NULL
reason TEXT NOT NULL
status VARCHAR(50) NOT NULL
created_at / created_by
updated_at / updated_by
applied_at / applied_by
cancelled_at / cancelled_by
cancellation_reason TEXT NULL
trace_id VARCHAR(100) NULL
version INT
```

Status:

```text
DRAFT
ACTIVE
APPLIED
CANCELLED
ARCHIVED
```

---

## 8.10 ProfitabilitySnapshot — `profit_snapshot`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
project_id UUID NOT NULL
profitability_profile_id UUID NOT NULL
currency VARCHAR(10) NOT NULL

baseline_revenue DECIMAL(19,4) NOT NULL DEFAULT 0
baseline_cost DECIMAL(19,4) NOT NULL DEFAULT 0
baseline_profit DECIMAL(19,4) NOT NULL DEFAULT 0
baseline_margin_percent DECIMAL(9,4) NULL

forecast_revenue DECIMAL(19,4) NOT NULL DEFAULT 0
forecast_cost DECIMAL(19,4) NOT NULL DEFAULT 0
forecast_profit DECIMAL(19,4) NOT NULL DEFAULT 0
forecast_margin_percent DECIMAL(9,4) NULL

revenue_variance DECIMAL(19,4) NOT NULL DEFAULT 0
cost_variance DECIMAL(19,4) NOT NULL DEFAULT 0
profit_variance DECIMAL(19,4) NOT NULL DEFAULT 0
margin_variance_percent DECIMAL(9,4) NULL

revenue_at_risk DECIMAL(19,4) NOT NULL DEFAULT 0
cost_overrun_risk DECIMAL(19,4) NOT NULL DEFAULT 0
profit_at_risk DECIMAL(19,4) NOT NULL DEFAULT 0

snapshot_source VARCHAR(100) NOT NULL
snapshot_at TIMESTAMP NOT NULL
created_at TIMESTAMP NOT NULL
trace_id VARCHAR(100) NULL
version INT
```

---

## 8.11 ProjectProfitabilitySummary — `profit_project_summary`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
project_id UUID NOT NULL
external_organization_id UUID NULL
currency VARCHAR(10) NOT NULL

forecast_revenue DECIMAL(19,4) NOT NULL DEFAULT 0
forecast_cost DECIMAL(19,4) NOT NULL DEFAULT 0
forecast_profit DECIMAL(19,4) NOT NULL DEFAULT 0
forecast_margin_percent DECIMAL(9,4) NULL

baseline_revenue DECIMAL(19,4) NOT NULL DEFAULT 0
baseline_cost DECIMAL(19,4) NOT NULL DEFAULT 0
baseline_profit DECIMAL(19,4) NOT NULL DEFAULT 0
baseline_margin_percent DECIMAL(9,4) NULL

revenue_at_risk DECIMAL(19,4) NOT NULL DEFAULT 0
cost_overrun_risk DECIMAL(19,4) NOT NULL DEFAULT 0
profit_at_risk DECIMAL(19,4) NOT NULL DEFAULT 0

profitability_status VARCHAR(50) NOT NULL
last_snapshot_at TIMESTAMP NULL
updated_at TIMESTAMP NOT NULL
version INT
```

Constraint:

```text
unique workspace_id + project_id + currency
```

---

## 8.12 WorkspaceProfitabilitySummary — optional read model

Implementation can be materialized or computed.

Fields:

```text
workspace_id
currency
period_start
period_end
total_forecast_revenue
total_forecast_cost
total_forecast_profit
average_margin_percent
revenue_at_risk
cost_overrun_risk
profit_at_risk
healthy_project_count
watch_project_count
at_risk_project_count
loss_risk_project_count
loss_project_count
updated_at
```

---

## 8.13 ProfitabilityRiskFlag — `profit_risk_flag`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
project_id UUID NOT NULL
profitability_profile_id UUID NOT NULL
risk_reason VARCHAR(100) NOT NULL
impact_type VARCHAR(50) NOT NULL
source_type VARCHAR(100) NULL
source_id UUID NULL
amount_at_risk DECIMAL(19,4) NULL
currency VARCHAR(10) NULL
description TEXT NULL
status VARCHAR(50) NOT NULL
created_at / created_by
mitigated_at / mitigated_by
closed_at / closed_by
trace_id VARCHAR(100) NULL
version INT
```

---

## 8.14 ProfitabilityVariance — `profit_variance`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
project_id UUID NOT NULL
profitability_profile_id UUID NOT NULL
variance_type VARCHAR(100) NOT NULL
from_amount DECIMAL(19,4) NOT NULL
to_amount DECIMAL(19,4) NOT NULL
variance_amount DECIMAL(19,4) NOT NULL
variance_percent DECIMAL(9,4) NULL
currency VARCHAR(10) NOT NULL
explanation TEXT NULL
source_snapshot_id UUID NULL
created_at TIMESTAMP NOT NULL
created_by UUID NULL
version INT
```

---

## 8.15 ProfitabilityThresholdPolicy — `profit_threshold_policy`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
project_id UUID NULL
currency VARCHAR(10) NULL
healthy_margin_percent DECIMAL(9,4) NOT NULL
watch_margin_percent DECIMAL(9,4) NOT NULL
at_risk_margin_percent DECIMAL(9,4) NOT NULL
loss_risk_margin_percent DECIMAL(9,4) NOT NULL
enabled BOOLEAN NOT NULL DEFAULT true
created_at / created_by
updated_at / updated_by
version INT
```

---

# 9. API TO-BE list

All APIs use `/api`.

---

## 9.1 Profitability profile APIs

```text
POST  /api/projects/{projectId}/profitability/profile
GET   /api/projects/{projectId}/profitability/profile
PUT   /api/projects/{projectId}/profitability/profile
PATCH /api/projects/{projectId}/profitability/profile/disable
PATCH /api/projects/{projectId}/profitability/profile/archive
POST  /api/projects/{projectId}/profitability/profile/from-quote/{quoteVersionId}
```

---

## 9.2 Revenue source / forecast APIs

```text
POST  /api/projects/{projectId}/profitability/revenue-sources
GET   /api/projects/{projectId}/profitability/revenue-sources
GET   /api/projects/{projectId}/profitability/revenue-sources/{sourceId}
PUT   /api/projects/{projectId}/profitability/revenue-sources/{sourceId}
POST  /api/projects/{projectId}/profitability/revenue-sources/{sourceId}/include
POST  /api/projects/{projectId}/profitability/revenue-sources/{sourceId}/exclude
PATCH /api/projects/{projectId}/profitability/revenue-sources/{sourceId}/archive

POST /api/projects/{projectId}/profitability/revenue-forecasts
GET  /api/projects/{projectId}/profitability/revenue-forecasts
POST /api/projects/{projectId}/profitability/revenue-forecasts/rebuild-current
```

---

## 9.3 Cost source / forecast APIs

```text
POST  /api/projects/{projectId}/profitability/cost-sources
GET   /api/projects/{projectId}/profitability/cost-sources
GET   /api/projects/{projectId}/profitability/cost-sources/{sourceId}
PUT   /api/projects/{projectId}/profitability/cost-sources/{sourceId}
POST  /api/projects/{projectId}/profitability/cost-sources/{sourceId}/include
POST  /api/projects/{projectId}/profitability/cost-sources/{sourceId}/exclude
PATCH /api/projects/{projectId}/profitability/cost-sources/{sourceId}/archive

POST /api/projects/{projectId}/profitability/cost-forecasts
GET  /api/projects/{projectId}/profitability/cost-forecasts
POST /api/projects/{projectId}/profitability/cost-forecasts/rebuild-current
```

---

## 9.4 Rate card APIs

```text
POST  /api/workspaces/{workspaceId}/profitability/rate-cards
GET   /api/workspaces/{workspaceId}/profitability/rate-cards
GET   /api/workspaces/{workspaceId}/profitability/rate-cards/{rateCardId}
PUT   /api/workspaces/{workspaceId}/profitability/rate-cards/{rateCardId}
PATCH /api/workspaces/{workspaceId}/profitability/rate-cards/{rateCardId}/archive

POST  /api/projects/{projectId}/profitability/rate-cards
GET   /api/projects/{projectId}/profitability/rate-cards
```

---

## 9.5 Profitability plan/version APIs

```text
POST  /api/projects/{projectId}/profitability/plans
GET   /api/projects/{projectId}/profitability/plans
GET   /api/projects/{projectId}/profitability/plans/{planId}
PUT   /api/projects/{projectId}/profitability/plans/{planId}
PATCH /api/projects/{projectId}/profitability/plans/{planId}/archive

POST /api/projects/{projectId}/profitability/plans/{planId}/versions
GET  /api/projects/{projectId}/profitability/plans/{planId}/versions
GET  /api/projects/{projectId}/profitability/plan-versions/{versionId}
PUT  /api/projects/{projectId}/profitability/plan-versions/{versionId}
POST /api/projects/{projectId}/profitability/plan-versions/{versionId}/finalize
POST /api/projects/{projectId}/profitability/plan-versions/{versionId}/cancel
POST /api/projects/{projectId}/profitability/plan-versions/{versionId}/make-current
```

---

## 9.6 Adjustment APIs

```text
POST /api/projects/{projectId}/profitability/adjustments
GET  /api/projects/{projectId}/profitability/adjustments
GET  /api/projects/{projectId}/profitability/adjustments/{adjustmentId}
PUT  /api/projects/{projectId}/profitability/adjustments/{adjustmentId}
POST /api/projects/{projectId}/profitability/adjustments/{adjustmentId}/apply
POST /api/projects/{projectId}/profitability/adjustments/{adjustmentId}/cancel
```

---

## 9.7 Snapshot / summary APIs

```text
POST /api/projects/{projectId}/profitability/snapshots
GET  /api/projects/{projectId}/profitability/snapshots
GET  /api/projects/{projectId}/profitability/snapshots/{snapshotId}

GET  /api/projects/{projectId}/profitability/summary
POST /api/projects/{projectId}/profitability/summary/rebuild

GET /api/workspaces/{workspaceId}/profitability/summary
GET /api/workspaces/{workspaceId}/profitability/by-client
GET /api/workspaces/{workspaceId}/profitability/by-project
GET /api/workspaces/{workspaceId}/profitability/by-month
```

---

## 9.8 Risk / variance APIs

```text
POST /api/projects/{projectId}/profitability/risk-flags
GET  /api/projects/{projectId}/profitability/risk-flags
GET  /api/projects/{projectId}/profitability/risk-flags/{riskFlagId}
POST /api/projects/{projectId}/profitability/risk-flags/{riskFlagId}/mitigate
POST /api/projects/{projectId}/profitability/risk-flags/{riskFlagId}/close

GET  /api/projects/{projectId}/profitability/variance
POST /api/projects/{projectId}/profitability/variance/recalculate
```

---

## 9.9 Threshold policy APIs

```text
GET /api/workspaces/{workspaceId}/profitability/threshold-policy
PUT /api/workspaces/{workspaceId}/profitability/threshold-policy

GET /api/projects/{projectId}/profitability/threshold-policy
PUT /api/projects/{projectId}/profitability/threshold-policy
```

---

## 9.10 Reports APIs

```text
GET /api/workspaces/{workspaceId}/reports/profitability/summary
GET /api/workspaces/{workspaceId}/reports/profitability/by-client
GET /api/workspaces/{workspaceId}/reports/profitability/by-project
GET /api/workspaces/{workspaceId}/reports/profitability/by-month
GET /api/workspaces/{workspaceId}/reports/profitability/at-risk
GET /api/workspaces/{workspaceId}/reports/profitability/loss-risk
GET /api/projects/{projectId}/reports/profitability/project-dashboard
GET /api/projects/{projectId}/reports/profitability/history
GET /api/projects/{projectId}/reports/profitability/variance
```

---

# 10. Authorization requirements

Required authorities:

```text
PROFITABILITY_PROFILE_VIEW
PROFITABILITY_PROFILE_CREATE
PROFITABILITY_PROFILE_UPDATE
PROFITABILITY_PROFILE_DISABLE
PROFITABILITY_PROFILE_ARCHIVE

REVENUE_SOURCE_VIEW
REVENUE_SOURCE_CREATE
REVENUE_SOURCE_UPDATE
REVENUE_SOURCE_INCLUDE_EXCLUDE
REVENUE_SOURCE_ARCHIVE
REVENUE_FORECAST_VIEW
REVENUE_FORECAST_CREATE
REVENUE_FORECAST_REBUILD

COST_SOURCE_VIEW
COST_SOURCE_CREATE
COST_SOURCE_UPDATE
COST_SOURCE_INCLUDE_EXCLUDE
COST_SOURCE_ARCHIVE
COST_FORECAST_VIEW
COST_FORECAST_CREATE
COST_FORECAST_REBUILD

RATE_CARD_VIEW
RATE_CARD_MANAGE

PROFITABILITY_PLAN_VIEW
PROFITABILITY_PLAN_CREATE
PROFITABILITY_PLAN_UPDATE
PROFITABILITY_PLAN_ARCHIVE
PROFITABILITY_PLAN_VERSION_CREATE
PROFITABILITY_PLAN_VERSION_UPDATE
PROFITABILITY_PLAN_VERSION_FINALIZE
PROFITABILITY_PLAN_VERSION_CANCEL
PROFITABILITY_PLAN_VERSION_MAKE_CURRENT

PROFITABILITY_ADJUSTMENT_VIEW
PROFITABILITY_ADJUSTMENT_CREATE
PROFITABILITY_ADJUSTMENT_UPDATE
PROFITABILITY_ADJUSTMENT_APPLY
PROFITABILITY_ADJUSTMENT_CANCEL

PROFITABILITY_SNAPSHOT_VIEW
PROFITABILITY_SNAPSHOT_CREATE
PROFITABILITY_SUMMARY_VIEW
PROFITABILITY_SUMMARY_REBUILD

PROFITABILITY_RISK_VIEW
PROFITABILITY_RISK_CREATE
PROFITABILITY_RISK_UPDATE
PROFITABILITY_VARIANCE_VIEW
PROFITABILITY_VARIANCE_RECALCULATE

PROFITABILITY_THRESHOLD_VIEW
PROFITABILITY_THRESHOLD_MANAGE

PROFITABILITY_REPORT_VIEW
PROFITABILITY_SENSITIVE_VIEW
PROFITABILITY_AUDIT_VIEW
```

Rules:

```text
1. Project access required.
2. Cost/profit/margin fields require sensitive permission.
3. Revenue-only view can be less restricted if workspace policy allows.
4. Rate card access is sensitive.
5. Workspace-level profitability reports require report permission.
6. Export of profitability reports is audited.
```

---

# 11. Lifecycle rules

## 11.1 Profile lifecycle

```text
ACTIVE → DISABLED
ACTIVE/DISABLED → ARCHIVED
```

## 11.2 Source lifecycle

```text
DRAFT → ACTIVE
ACTIVE → SUPERSEDED
ACTIVE → EXCLUDED
Any → ARCHIVED
```

## 11.3 Plan lifecycle

```text
DRAFT → ACTIVE
ACTIVE → SUPERSEDED
DRAFT/ACTIVE → CANCELLED
Any → ARCHIVED
```

Plan version:

```text
DRAFT → FINALIZED
FINALIZED → SUPERSEDED
DRAFT/FINALIZED → CANCELLED
Any → ARCHIVED
```

## 11.4 Adjustment lifecycle

```text
DRAFT → ACTIVE → APPLIED
DRAFT/ACTIVE → CANCELLED
Any → ARCHIVED
```

## 11.5 Risk lifecycle

```text
OPEN → MITIGATED → CLOSED
OPEN → CLOSED
Any → ARCHIVED
```

---

# 12. Calculation rules

## 12.1 Forecast revenue

```text
forecast_revenue =
  sum(active included revenue sources)
+ sum(applied revenue adjustments)
```

## 12.2 Forecast cost

```text
forecast_cost =
  sum(active included cost sources)
+ sum(applied cost adjustments)
```

## 12.3 Forecast profit

```text
forecast_profit = forecast_revenue - forecast_cost
```

## 12.4 Forecast margin

```text
forecast_margin_percent =
  forecast_profit / forecast_revenue * 100
```

Rules:

```text
1. If forecast_revenue is 0, margin is null or 0 by policy.
2. Use BigDecimal/decimal precision.
3. Never use floating point.
4. Currency required.
5. Do not sum different currencies without conversion policy.
```

## 12.5 Variance

```text
revenue_variance = forecast_revenue - baseline_revenue
cost_variance = forecast_cost - baseline_cost
profit_variance = forecast_profit - baseline_profit
margin_variance_percent = forecast_margin_percent - baseline_margin_percent
```

## 12.6 Profitability status

Default logic:

```text
LOSS         if forecast_profit < 0
LOSS_RISK    if margin < lossRisk threshold
AT_RISK      if margin < atRisk threshold or profitAtRisk high
WATCH        if margin < watch threshold
HEALTHY      otherwise
NO_DATA      if no forecast data
```

Workspace/project threshold policy can override.

---

# 13. Integration rules

## 13.1 Quote integration

Rules:

```text
1. QuoteVersion can create ProjectProfitabilityProfile.
2. QuoteVersion/QuoteLine can create RevenueSource.
3. Quote amount becomes forecast revenue input.
4. Internal quote cost/margin stays permission-protected.
```

## 13.2 ChangeRequest integration

Rules:

```text
1. ChangeRequest commercial impact can create RevenueSource or Adjustment.
2. ChangeRequest effort impact can create CostSource or Adjustment.
3. Applied ChangeRequest triggers recalculation.
4. Change impact appears in variance explanation.
```

## 13.3 Task / WBS integration

Rules:

```text
1. Task estimated effort can create/update CostSource.
2. Task actual effort can update CostForecast if actual tracking exists.
3. WBS estimates can aggregate into cost forecast.
4. Cost recalculation should be idempotent.
```

## 13.4 Resource/rate integration

Rules:

```text
1. If resource cost rate exists, use it.
2. Otherwise use team/project/workspace blended rate.
3. Missing rate should create warning/risk flag, not crash.
4. Rate changes do not rewrite finalized snapshots.
```

## 13.5 Deliverable / acceptance integration

Rules:

```text
1. Deliverable can link to RevenueSource.
2. Acceptance can update forecast confidence or realized-management flag.
3. Delayed deliverable can create profitability risk.
```

## 13.6 Quality / defect integration

Rules:

```text
1. High-severity defects can create rework cost source/risk.
2. Rework estimate can affect forecast cost.
3. Quality risk can lower profitability status.
```

## 13.7 RAID integration

Rules:

```text
1. Risk/Issue can link to ProfitabilityRiskFlag.
2. Critical risk can affect revenue at risk or cost overrun risk.
3. Closing risk can trigger summary recalculation.
```

## 13.8 Governance integration

Rules:

```text
1. Finalized ProfitabilityPlanVersion locked.
2. ProfitabilitySnapshot immutable.
3. Manual adjustments require reason.
4. Sensitive cost/profit/margin access audited.
5. Revert/restore creates new version/snapshot.
```

## 13.9 Notification integration

Rules:

```text
1. Margin drop below threshold triggers notification.
2. Project enters loss risk triggers alert.
3. Material forecast change notifies owner/PM.
4. Sensitive values masked by recipient permission.
```

## 13.10 Reporting integration

Rules:

```text
1. Profitability reports require permission.
2. Cost/profit/margin reports require sensitive permission.
3. Export audited.
4. Aggregated reports avoid leaking restricted projects.
```

---

# 14. Event Registry integration

Recommended source system:

```text
SCOPERY_PROFITABILITY
```

Required events:

```text
PROFITABILITY_PROFILE_CREATED
PROFITABILITY_PROFILE_UPDATED
PROFITABILITY_PROFILE_DISABLED
PROFITABILITY_PROFILE_ARCHIVED

REVENUE_SOURCE_CREATED
REVENUE_SOURCE_UPDATED
REVENUE_SOURCE_INCLUDED
REVENUE_SOURCE_EXCLUDED
REVENUE_SOURCE_ARCHIVED
REVENUE_FORECAST_CREATED
REVENUE_FORECAST_REBUILT

COST_SOURCE_CREATED
COST_SOURCE_UPDATED
COST_SOURCE_INCLUDED
COST_SOURCE_EXCLUDED
COST_SOURCE_ARCHIVED
COST_FORECAST_CREATED
COST_FORECAST_REBUILT

RATE_CARD_CREATED
RATE_CARD_UPDATED
RATE_CARD_ARCHIVED

PROFITABILITY_PLAN_CREATED
PROFITABILITY_PLAN_UPDATED
PROFITABILITY_PLAN_ARCHIVED
PROFITABILITY_PLAN_VERSION_CREATED
PROFITABILITY_PLAN_VERSION_UPDATED
PROFITABILITY_PLAN_VERSION_FINALIZED
PROFITABILITY_PLAN_VERSION_CANCELLED
PROFITABILITY_PLAN_VERSION_CURRENT_CHANGED

PROFITABILITY_ADJUSTMENT_CREATED
PROFITABILITY_ADJUSTMENT_APPLIED
PROFITABILITY_ADJUSTMENT_CANCELLED

PROFITABILITY_SNAPSHOT_CREATED
PROJECT_PROFITABILITY_SUMMARY_UPDATED
WORKSPACE_PROFITABILITY_SUMMARY_REBUILT

PROFITABILITY_RISK_FLAG_CREATED
PROFITABILITY_RISK_FLAG_MITIGATED
PROFITABILITY_RISK_FLAG_CLOSED

PROFITABILITY_VARIANCE_CALCULATED
PROFITABILITY_STATUS_CHANGED
PROFITABILITY_MARGIN_THRESHOLD_BREACHED
PROFITABILITY_REPORT_EXPORTED
PROFITABILITY_SENSITIVE_FIELD_VIEWED
```

Standard variables:

```text
actor.userId
workspace.id
project.id
client.id
profitabilityProfile.id
revenueSource.id
costSource.id
revenueForecast.id
costForecast.id
rateCard.id
profitabilityPlan.id
profitabilityPlanVersion.id
adjustment.id
snapshot.id
riskFlag.id
amount
currency
status
occurredAt
traceId
```

---

# 15. Audit / activity / outbox

Audit-sensitive actions:

```text
cost source amount changed
rate card changed
profit/margin viewed
manual adjustment applied
plan version finalized
snapshot created
threshold policy changed
profitability report exported
portal commercial visibility changed
```

Activity actions:

```text
PROFITABILITY_PLAN_VERSION_FINALIZED
PROFITABILITY_ADJUSTMENT_APPLIED
PROFITABILITY_SNAPSHOT_CREATED
PROFITABILITY_RISK_FLAG_CREATED
PROFITABILITY_STATUS_CHANGED
```

Outbox required for:

```text
forecast materially changed
margin threshold breached
loss risk detected
risk flag created
snapshot created
plan finalized
```

Idempotency recommended for:

```text
create profile from quote
rebuild revenue forecast
rebuild cost forecast
apply adjustment
create snapshot
rebuild summary
finalize plan version
```

---

# 16. Business rules master

## 16.1 Profile/source rules

```text
PROF-001 Profile unique per project.
PROF-002 Profile controls tracking mode.
REV-001 Revenue source target must exist.
COST-001 Cost source target must exist where source_id provided.
SRC-001 Sources can be included/excluded with reason.
SRC-002 Source changes trigger recalculation.
```

## 16.2 Rate/cost rules

```text
RATE-001 Rate currency required.
RATE-002 Cost calculation uses decimal precision.
RATE-003 Missing rate creates warning/risk, not broken dashboard.
RATE-004 Rate changes do not rewrite finalized snapshots.
```

## 16.3 Plan/version rules

```text
PLAN-001 Plan has versions.
PLAN-002 Finalized plan version immutable.
PLAN-003 New current version supersedes old current.
PLAN-004 Finalized plan version creates governance lock.
```

## 16.4 Adjustment rules

```text
ADJ-001 Adjustment requires reason.
ADJ-002 Applied adjustment affects forecast.
ADJ-003 Cancelled adjustment no longer affects forecast.
ADJ-004 Adjustment audited.
```

## 16.5 Snapshot/summary rules

```text
SNAP-001 Snapshot immutable.
SNAP-002 Summary derived from records.
SNAP-003 Summary rebuild traceable.
SUM-001 Summary permissions enforced.
```

## 16.6 Risk/variance rules

```text
RISK-001 Risk flag can be manual or system-generated.
RISK-002 Risk flag does not mutate source object.
VAR-001 Variance calculated from baseline/forecast/snapshot.
VAR-002 Sensitive variance values masked if needed.
```

---

# 17. Error catalog

```text
PROFITABILITY_PROFILE_NOT_FOUND
PROFITABILITY_PROFILE_ALREADY_EXISTS
PROFITABILITY_PROFILE_DISABLED
PROFITABILITY_PROFILE_ACCESS_DENIED
PROFITABILITY_SENSITIVE_ACCESS_DENIED

REVENUE_SOURCE_NOT_FOUND
REVENUE_SOURCE_TARGET_NOT_FOUND
REVENUE_SOURCE_TARGET_MISMATCH
REVENUE_SOURCE_INVALID_AMOUNT
REVENUE_SOURCE_INVALID_STATUS

REVENUE_FORECAST_NOT_FOUND
REVENUE_FORECAST_REBUILD_FAILED

COST_SOURCE_NOT_FOUND
COST_SOURCE_TARGET_NOT_FOUND
COST_SOURCE_TARGET_MISMATCH
COST_SOURCE_INVALID_AMOUNT
COST_SOURCE_INVALID_STATUS
COST_FORECAST_NOT_FOUND
COST_FORECAST_REBUILD_FAILED

RATE_CARD_NOT_FOUND
RATE_CARD_INVALID
RATE_CARD_ACCESS_DENIED

PROFITABILITY_PLAN_NOT_FOUND
PROFITABILITY_PLAN_INVALID_STATUS
PROFITABILITY_PLAN_VERSION_NOT_FOUND
PROFITABILITY_PLAN_VERSION_IMMUTABLE
PROFITABILITY_PLAN_VERSION_FINALIZE_NOT_ALLOWED
PROFITABILITY_PLAN_VERSION_CURRENT_CONFLICT

PROFITABILITY_ADJUSTMENT_NOT_FOUND
PROFITABILITY_ADJUSTMENT_REASON_REQUIRED
PROFITABILITY_ADJUSTMENT_INVALID_STATUS
PROFITABILITY_ADJUSTMENT_INVALID_AMOUNT

PROFITABILITY_SNAPSHOT_NOT_FOUND
PROFITABILITY_SNAPSHOT_CREATE_FAILED
PROFITABILITY_SUMMARY_NOT_FOUND
PROFITABILITY_SUMMARY_REBUILD_FAILED

PROFITABILITY_RISK_FLAG_NOT_FOUND
PROFITABILITY_RISK_FLAG_INVALID_STATUS
PROFITABILITY_VARIANCE_CALCULATION_FAILED
PROFITABILITY_THRESHOLD_POLICY_INVALID
PROFITABILITY_CURRENCY_MISMATCH
PROFITABILITY_REPORT_ACCESS_DENIED
```

---

# 18. Required tests

## 18.1 Profile tests

```text
createProfitabilityProfile_success
createProfile_duplicate_rejected
createProfileFromQuote_success
updateProfile_success
disableProfile_success
profileAccess_withoutPermission_forbidden
```

## 18.2 Revenue tests

```text
createRevenueSourceFromQuote_success
createRevenueSourceFromChangeRequest_success
createManualRevenueSource_success
revenueSourceOtherProject_rejected
excludeRevenueSource_requiresReason
rebuildRevenueForecast_success
revenueForecastMaterialChange_eventEmitted
```

## 18.3 Cost tests

```text
createCostSourceFromTaskEstimate_success
createManualCostSource_success
costSourceOtherProject_rejected
excludeCostSource_requiresReason
rateCardUsedForEffortCost_success
missingRateCreatesRiskFlag
rebuildCostForecast_success
```

## 18.4 Plan/version tests

```text
createProfitabilityPlan_success
createProfitabilityPlanVersion_success
finalizeProfitabilityPlanVersion_success
finalizedPlanVersion_immutable
makeCurrentVersion_supersedesOld
planVersionCalculatesProfitMargin_success
```

## 18.5 Adjustment tests

```text
createRevenueIncreaseAdjustment_success
createCostIncreaseAdjustment_success
adjustmentRequiresReason
applyAdjustment_recalculatesSummary
cancelAdjustment_recalculatesSummary
adjustmentAudited
```

## 18.6 Snapshot/summary tests

```text
createProfitabilitySnapshot_success
profitabilitySnapshotImmutable
rebuildProjectProfitabilitySummary_success
summaryCalculatesProfitAndMargin_success
workspaceProfitabilitySummary_success
multiCurrencyAggregationRequiresPolicy
```

## 18.7 Risk/variance tests

```text
createProfitabilityRiskFlag_success
systemRiskFlagFromMarginDrop_success
mitigateRiskFlag_success
closeRiskFlag_success
varianceCalculatedCorrectly
marginThresholdStatusHealthyWatchAtRiskLoss_success
```

## 18.8 Integration tests

```text
quoteAcceptedCreatesRevenueSource
changeRequestAppliedUpdatesRevenueAndCost
taskEstimateChangeUpdatesCostForecast
deliverableDelayCreatesProfitabilityRisk
defectReworkCreatesCostRisk
riskClosedRebuildsSummary
```

## 18.9 Governance/security tests

```text
finalizedPlanVersionCreatesGovernanceLock
manualAdjustmentAuditCreated
sensitiveProfitabilityAccessAudited
profitabilityReportExportAudited
restoreCreatesNewVersionNotOverwrite
```

## 18.10 Authorization tests

```text
viewProfitability_withoutPermission_forbidden
viewCostWithoutSensitivePermission_maskedOrForbidden
viewMarginWithoutSensitivePermission_maskedOrForbidden
manageRateCard_withoutPermission_forbidden
applyAdjustment_withoutPermission_forbidden
crossWorkspaceProfitability_forbidden
```

## 18.11 Event/audit tests

```text
profitabilityEventSeeder_firstRun_createsDefinitions
profitabilityEventSeeder_secondRun_noDuplicates
planFinalized_eventEmitted
summaryUpdated_eventEmitted
marginThresholdBreached_eventEmitted
reportExport_auditCreated
```

---

# 19. Manual verification checklist

Completion file must include:

```text
1. Create ProjectProfitabilityProfile from QuoteVersion.
2. Confirm RevenueSource created from quote.
3. Create CostSource from task estimate or manual cost.
4. Configure RateCard / blended cost rate.
5. Rebuild revenue forecast.
6. Rebuild cost forecast.
7. Create ProfitabilityPlan.
8. Create and finalize ProfitabilityPlanVersion.
9. Confirm finalized version locked.
10. Apply manual revenue adjustment with reason.
11. Apply manual cost adjustment with reason.
12. Create ProfitabilitySnapshot.
13. Rebuild ProjectProfitabilitySummary.
14. Confirm dashboard shows revenue, cost, profit, margin.
15. Trigger margin drop and confirm risk/status changes.
16. Generate workspace profitability summary.
17. Confirm cost/profit/margin hidden without sensitive permission.
18. Confirm PM can mostly view and only adjust when needed.
```

---

# 20. Acceptance criteria

Phase 36 is accepted only if:

```text
1. Current revenue/cost/profitability capability is classified against TO-BE.
2. ProjectProfitabilityProfile implemented/tested.
3. RevenueSource and RevenueForecast implemented/tested.
4. CostSource and CostForecast implemented/tested.
5. RateCard/blended cost foundation implemented/tested.
6. ProfitabilityPlan and ProfitabilityPlanVersion implemented/tested.
7. ProfitabilityAdjustment implemented/tested.
8. ProfitabilitySnapshot implemented/tested.
9. ProjectProfitabilitySummary and WorkspaceProfitabilitySummary implemented/tested.
10. ProfitabilityRiskFlag and ProfitabilityVariance implemented/tested.
11. Automatic recalculation implemented/tested.
12. Threshold policy implemented/tested.
13. Governance/lock/audit integration implemented/tested.
14. Notification/report/export integration implemented/tested.
15. IAM permissions implemented/tested.
16. Event seeders idempotent.
17. Activity/audit/outbox follows Phase 04.
18. Cost/profit/margin sensitive access is enforced.
19. `mvn compile` passes.
20. `mvn test` passes.
21. Completion file exists.
```

Do not mark complete if:

```text
PM must manually enter all values for dashboard to work
forecast cost is not permission-protected
finalized profitability plan can be silently edited
summary does not recalculate from source changes
profit/margin calculations use floating point
risk/status thresholds are not tested
reports bypass permission
tests fail
```

---

# 21. Required phase completion file

Agent must create:

```text
docs/phase-complete/PHASE_36_REVENUE_PROFITABILITY_VISIBILITY_TO_BE_COMPLETE.md
```

Required sections:

```text
# Phase 36 — Revenue & Profitability Visibility Complete

## 1. Summary
## 2. Source Inputs Reviewed
## 3. Current vs TO-BE Classification Matrix
## 4. Implemented in Current BE
## 5. Implemented / Hardened in This Phase
## 6. Seed-only Items Added
## 7. Deferred Items and Target Phase
## 8. Product Boundary Decision
## 9. Entity Mapping
## 10. API Changes
## 11. Profitability Profile Strategy
## 12. Revenue Source / Forecast Strategy
## 13. Cost Source / Forecast Strategy
## 14. Rate Card Strategy
## 15. Profitability Plan / Version Strategy
## 16. Adjustment Strategy
## 17. Snapshot / Summary Strategy
## 18. Risk / Variance Strategy
## 19. Automatic Recalculation Strategy
## 20. Threshold Policy Strategy
## 21. Governance / Lock / Versioning Strategy
## 22. Notification Strategy
## 23. Reporting / Export Strategy
## 24. Authorization Matrix
## 25. Activity / Audit / Outbox Notes
## 26. Idempotency Strategy
## 27. Tests Added
## 28. Commands Run
## 29. Test Results
## 30. Manual Verification
## 31. Assumptions
## 32. Deviations From Prompt
## 33. Known Risks
## 34. Future Phases That Must Return to Profitability
```

---

# 22. Future phases that may return

```text
Phase 37 — Resource / Capacity:
- Better resource cost, planned effort, actual effort, utilization.

Phase 38 — Audit / Compliance / Privacy:
- Sensitive profitability access review and export audit.

Phase 39 — Integration / Import / Export:
- Import cost/revenue actuals from external systems if needed.
- BI export.

Phase 41 — Knowledge Graph / Semantic Index:
- AI profitability insights, variance explanation, margin risk prediction.
```

---

# 23. Agent anti-bịa rules

The agent must not:

```text
1. Treat PM as finance/accounting user.
2. Force manual input for all profitability data.
3. Expose cost/profit/margin without permission.
4. Let finalized profitability plan be silently edited.
5. Let rate changes rewrite historical snapshots.
6. Use floating point for money.
7. Claim forecast is exact truth.
8. Hide missing data quality warnings.
9. Skip audit for manual adjustments.
10. Skip recalculation after source changes.
```

---

# 24. Prompt to give coding agent

```text
You are implementing Phase 36 — TO-BE Revenue & Profitability Visibility, Forecast, Cost Snapshot, Margin Dashboard & PM Adjustment.

Product direction:
PM should not manually manage finance.
The system should auto-derive revenue, cost, profit, margin, variance, and risk from quote, change request, scope, deliverable, task, resource/rate, quality, and project data.
PM mostly views the dashboard and only makes small manual adjustments when needed.

Before coding, read:
- CLAUDE.md / CLAUDE.ms
- Coding_convention.md
- Phase 00–35 docs and completion files
- Current backend code, migrations, tests

Your task:
1. Compare current revenue/cost/profitability capability against this Phase 36 TO-BE spec.
2. Classify each capability with required labels.
3. Implement ProjectProfitabilityProfile.
4. Implement RevenueSource and RevenueForecast.
5. Implement CostSource and CostForecast.
6. Implement RateCard / blended cost rate foundation.
7. Implement ProfitabilityPlan and ProfitabilityPlanVersion.
8. Implement ProfitabilityAdjustment.
9. Implement ProfitabilitySnapshot, ProjectProfitabilitySummary, WorkspaceProfitabilitySummary.
10. Implement ProfitabilityRiskFlag and ProfitabilityVariance.
11. Implement automatic recalculation from quote/change/task/deliverable/quality/risk/rate/adjustment changes.
12. Implement threshold policy for HEALTHY/WATCH/AT_RISK/LOSS_RISK/LOSS.
13. Integrate with Governance, Notifications, Reporting, Audit, Outbox, IAM.
14. Run mvn compile and mvn test.
15. Create docs/phase-complete/PHASE_36_REVENUE_PROFITABILITY_VISIBILITY_TO_BE_COMPLETE.md.
```

---

# 25. Quick tracking matrix

| Capability | Current backend | Phase 36 action | Later phase |
|---|---|---|---|
| ProjectProfitabilityProfile | Missing/unknown | Must implement | — |
| RevenueSource | Missing/unknown | Must implement | — |
| RevenueForecast | Missing/unknown | Must implement | AI insights Phase 41 |
| CostSource | Missing/unknown | Must implement | Resource Phase 37 |
| CostForecast | Missing/unknown | Must implement | Resource Phase 37 |
| RateCard | Missing/unknown | Must implement foundation | Resource Phase 37 |
| ProfitabilityPlan | Missing/unknown | Must implement | — |
| ProfitabilityPlanVersion | Missing/unknown | Must implement | — |
| ProfitabilityAdjustment | Missing/unknown | Must implement | — |
| ProfitabilitySnapshot | Missing/unknown | Must implement | — |
| ProjectProfitabilitySummary | Missing/unknown | Must implement | — |
| WorkspaceProfitabilitySummary | Missing/unknown | Must implement/report | BI export Phase 39 |
| ProfitabilityRiskFlag | Missing/unknown | Must implement | AI risk Phase 41 |
| ProfitabilityVariance | Missing/unknown | Must implement | AI explanation Phase 41 |
| Automatic recalculation | Missing/unknown | Must implement | — |
| Threshold policy | Missing/unknown | Must implement | — |
| Governance lock/audit | Missing/partial | Must integrate | Phase 34 |
| Profitability notifications | Missing/partial | Must integrate | Phase 35 |
| Sensitive cost/profit view | Missing/unknown | Must enforce | Phase 38 |

---

# 26. Final principle

Phase 36 is not complete when "revenue fields exist."

Phase 36 is complete when PM can open a project and instantly understand:

```text
How much revenue do we expect?
How much will it cost?
Are we still profitable?
What is the forecast margin?
What changed from baseline?
What is at risk?
What should we adjust?
```

The system should calculate most of it automatically.

PM should only review, explain, and adjust lightly.

Revenue is useful.

Cost is necessary.

Profitability is the real management insight.
