# PHASE 15 — TO-BE Rate Card, Cost Role, CCH, Billing Rate, Currency & Inflation Policy Foundation

> Project: Scopery Backend  
> Phase: 15  
> Document type: TO-BE implementation-grade specification  
> Status: Planning / Gap-driven implementation spec  
> Depends on: Phase 00 — Master Backend Roadmap, Phase 01 — API Path / Security Baseline, Phase 02 — IAM Core, Phase 03 — Workspace Core, Phase 04 — Platform Audit / Outbox / Idempotency, Phase 05 — Event Registry, Phase 06 — Notification Engine, Phase 07 — AI Agent Platform, Phase 08 — Knowledge / Document Type Catalog, Phase 09 — Project Core / Phase / WBS / Task, Phase 10 — Project Authorization, Phase 11 — Project Template / Phase Catalog, Phase 12 — Resource Calendar / Capacity, Phase 13 — Task Scheduling Engine, Phase 14 — WBS-driven Gantt  
> API base: `/api`  
> Primary module: `modules/ratecard` or `modules/project/ratecard` depending on repository architecture  
> Related modules: `workspace`, `iam`, `project`, `capacity`, `scheduling`, `eventregistry`, `notification`, future `estimation`, `finance`, `quote`, `billing`, `reporting`, `aiagent`  
> Important rule: This file is **not an as-is document**. It defines the TO-BE rate/cost policy foundation. Phase 15 does not implement full project budget, margin, quote, contract value solver, billing, revenue, actual cost, timesheet, or payroll.

---

# 0. Purpose of this file

Phase 15 creates the rate foundation required for later estimation, finance, quote, and profitability modules.

Previous phases created:

```text
Task.estimateHours
Task.assignee/inCharge
Project phase
WBS
Capacity and schedule forecast
```

Phase 15 answers:

```text
What role/cost role is used for planning?
What is the cost per hour for that role?
What is the billing rate per hour for that role?
Which currency is used?
Which rate card version applies on a date?
How should inflation/escalation adjust future rates?
How can later modules resolve a stable rate snapshot?
```

Phase 15 does **not** answer:

```text
What is total project budget?
What is project gross margin?
What is contract value?
What is quote price?
What is actual cost?
What is invoice amount?
What is salary?
```

Those are later phases.

---

# 1. Source inputs

Before coding Phase 15, the agent must read:

```text
1. Current backend codebase
2. Phase 09 Project Core TO-BE spec and implementation
3. Phase 12 Resource Calendar / Capacity TO-BE spec and implementation
4. Phase 13 Task Scheduling Engine TO-BE spec and implementation
5. Phase 14 WBS-driven Gantt TO-BE spec and implementation
6. Phase 02 IAM TO-BE spec
7. Phase 03 Workspace TO-BE spec
8. Phase 04 Platform Audit / Outbox / Idempotency spec
9. Phase 05 Event Registry spec
10. Current BE feature/entity/business-rule inventory
11. Dynamic Work OS feature catalog
12. Existing finance/rate/currency code if any
13. Existing workspace member/team/user role code if any
```

The agent must not implement Phase 15 based on assumptions only.

---

# 2. Current expected backend state

After Phase 14, the backend likely has:

```text
Project
Task
Task.estimateHours
Task.assignee/inCharge
ScheduleRun
TaskSchedule
WorkspaceMember
WorkspaceTeam
User
IAM authorities
```

Likely missing:

```text
CostRole
RateCard
RateCardVersion
RateCardLine
CostPolicy
BillingRate
InflationPolicy
CurrencyRate
RateResolutionService
Rate snapshot contract
```

Phase 15 must verify actual code and classify accurately.

---

# 3. Phase 15 target statement

Phase 15 must deliver a future-ready rate and cost policy foundation:

```text
1. Define planning cost roles independent of personal salary.
2. Define rate cards by workspace/organization/client/project scope.
3. Version rate cards with effective date ranges.
4. Define cost-per-hour / CCH for roles.
5. Define billing rate per hour for roles if needed.
6. Define currency per rate card/version/line.
7. Define inflation/escalation policy for future years.
8. Provide rate resolution service for later modules.
9. Provide immutable published rate card versions.
10. Provide rate snapshot contract so historical calculations do not change when rates change later.
11. Protect rate management with IAM.
12. Emit rate events and audit sensitive changes.
13. Do not expose salary or payroll data.
14. Do not calculate project budget/margin in Phase 15.
```

---

# 4. Core financial safety decision

## 4.1 CCH is not salary

CCH means:

```text
Cost per chargeable hour
or planning cost per hour
```

It is a planning rate.

It may include:

```text
salary burden
benefits
tools
office cost
management overhead component
internal cost policy assumptions
```

But Phase 15 must not store or expose personal salary.

Rule:

```text
Salary must not be copied into project/quote/worklog/rate card entities.
```

## 4.2 Rate card is not project finance

Rate card provides unit rates.

Project finance later calculates totals.

```text
Task Planned Labor Cost = Estimate Hours × Resolved CCH
```

This formula may be implemented as preview/resolution in Phase 15, but **project roll-up** belongs to Phase 16/17.

## 4.3 Historical calculations need snapshots

Future finance/quote modules must store:

```text
rateCardVersionId
rateCardLineId
roleCode
baseRate
adjustedRate
currency
effectiveDate
inflationApplied
resolvedAt
```

Rule:

```text
Historical transactions must not change when rate card/currency/inflation later changes.
```

Phase 15 must define this contract even if no transactions exist yet.

---

# 5. Classification labels

Every requirement uses one label:

| Label | Meaning |
|---|---|
| `CURRENTLY_IMPLEMENTED` | Current backend already implements it. Agent must verify and test. |
| `PARTIALLY_IMPLEMENTED` | Current backend implements part of it. |
| `MUST_IMPLEMENT_IN_PHASE_15` | Must be implemented or hardened now. |
| `SEED_ONLY_IN_PHASE_15` | Seed roles/events/permissions/policies now; full consumer later. |
| `DEFERRED_TO_PHASE_XX` | Do not implement now; later phase must return. |
| `DEFERRED_TO_POST_23_BACKLOG` | Outside core 00–23 roadmap, part of full Work OS expansion. |
| `NOT_IN_SCOPE_FOR_BACKEND_NOW` | Explicitly not backend scope now. |

---

# 6. Phase 15 scope decision

## 6.1 Must implement now

```text
CostRole catalog
RateCard
RateCardVersion
RateCardLine
RateCardVersion publish/immutability
Currency definition/validation foundation
InflationPolicy / escalation policy foundation
RateResolutionService
Rate snapshot DTO/contract
Rate card permissions
Rate card event definitions
Rate card seed data
Tests
Completion report
```

## 6.2 Optional now

```text
Workspace default rate card
Project rate card assignment
Client-specific rate card scope
Simple exchange rate table
Rate preview endpoint for task/role
```

Implement only if needed by Phase 16/17.

## 6.3 Must not implement now

```text
Project budget roll-up
Phase finance
Gross margin
Profit before tax
Overhead model
Custom phase costs
Revenue split
Quote price
Contract value solver
Billing/invoice
Actual cost
Payroll/salary
Timesheet cost
Tax
```

---

# 7. TO-BE capability matrix

---

## 7.1 RTE-001 — CostRole catalog

| Item | Value |
|---|---|
| Future capability | Define planning roles for cost/rate estimation |
| Current state | Missing/unknown |
| Phase 15 target | Implement |
| Classification | `MUST_IMPLEMENT_IN_PHASE_15` |

Examples:

```text
PROJECT_MANAGER
BUSINESS_ANALYST
UX_UI_DESIGNER
FRONTEND_DEVELOPER
BACKEND_DEVELOPER
FULLSTACK_DEVELOPER
MOBILE_DEVELOPER
QA_ENGINEER
DEVOPS_ENGINEER
SOLUTION_ARCHITECT
DATA_ENGINEER
AI_ENGINEER
TECH_LEAD
```

Rules:

```text
1. Role code required.
2. Role code normalized uppercase.
3. Role name required.
4. Role status required.
5. Scope required: SYSTEM / ORGANIZATION / WORKSPACE.
6. Code unique within scope.
7. Built-in roles cannot be hard-deleted.
8. Archived/inactive roles cannot be used in new rate card lines.
9. CostRole is not an IAM role.
10. CostRole is not a job title unless mapped explicitly.
```

---

## 7.2 RTE-002 — Workspace member cost role mapping

| Item | Value |
|---|---|
| Future capability | Map a workspace member to default cost role for planning |
| Current state | Missing/unknown |
| Phase 15 target | Optional but recommended |
| Classification | `MUST_IMPLEMENT_IN_PHASE_15` if Phase 16 needs task cost by assignee; otherwise `DEFERRED_TO_PHASE_16` |

Entity:

```text
WorkspaceMemberCostRoleAssignment
```

Rules:

```text
1. WorkspaceMember must be ACTIVE.
2. CostRole must be ACTIVE and available in workspace scope.
3. User can have one default cost role per workspace at a time.
4. Assignment effective date range valid.
5. Assignment does not expose salary.
6. Changing assignment is audited.
```

Alternative:

```text
Task can later store costRoleCode directly.
```

Recommended Phase 15:

```text
Implement mapping if simple because Phase 16/17 need rate resolution.
```

---

## 7.3 RTE-003 — RateCard

| Item | Value |
|---|---|
| Future capability | Group rates under a named card and scope |
| Current state | Missing/unknown |
| Phase 15 target | Implement |
| Classification | `MUST_IMPLEMENT_IN_PHASE_15` |

Scope options:

```text
SYSTEM
ORGANIZATION
WORKSPACE
CLIENT
PROJECT
```

Recommended Phase 15:

```text
SYSTEM / ORGANIZATION / WORKSPACE
```

Defer:

```text
CLIENT-specific rate card → Phase 18/29
PROJECT-specific override → Phase 17/18
```

Rules:

```text
1. Rate card code required.
2. Code normalized uppercase.
3. Code unique within scope.
4. Name required.
5. Scope required.
6. Workspace-scoped rate card requires active workspace.
7. Organization-scoped rate card requires active organization.
8. Rate card shell can exist without published version.
9. Archived rate card cannot be used for new calculations.
10. Rate card changes audited.
```

---

## 7.4 RTE-004 — RateCardVersion

| Item | Value |
|---|---|
| Future capability | Version rates and preserve historical calculations |
| Current state | Missing/unknown |
| Phase 15 target | Implement |
| Classification | `MUST_IMPLEMENT_IN_PHASE_15` |

Status:

```text
DRAFT
PUBLISHED
ARCHIVED
```

Rules:

```text
1. Version number unique within rate card.
2. EffectiveFrom required.
3. EffectiveTo optional.
4. EffectiveTo >= EffectiveFrom.
5. Published version immutable.
6. DRAFT can be edited.
7. Publishing validates:
   - at least one rate line
   - no duplicate role/currency/effective combination
   - all roles active
   - all rates positive
   - currency valid
   - date range does not overlap another published version unless policy allows
8. Only PUBLISHED version can be used for official rate resolution.
9. Archived version cannot be used for new calculations.
10. Historical snapshots can still reference archived versions.
```

---

## 7.5 RTE-005 — RateCardLine

| Item | Value |
|---|---|
| Future capability | Define CCH and billing rate by role |
| Current state | Missing/unknown |
| Phase 15 target | Implement |
| Classification | `MUST_IMPLEMENT_IN_PHASE_15` |

Fields:

```text
costRoleId
currencyCode
costRatePerHour
billingRatePerHour
effectiveFrom optional if version covers effective date
effectiveTo optional
seniorityLevel optional
locationCode optional
notes
```

Rules:

```text
1. RateCardVersion must be DRAFT to add/update/delete lines.
2. CostRole must be ACTIVE.
3. Currency required.
4. costRatePerHour > 0.
5. billingRatePerHour optional but if present > 0.
6. billingRatePerHour should be >= costRatePerHour unless product allows loss-leading pricing.
7. Duplicate role/seniority/location/currency blocked within version.
8. Published line immutable through published version.
9. Rate line does not store salary.
```

---

## 7.6 RTE-006 — Currency foundation

| Item | Value |
|---|---|
| Future capability | Support rate cards in different currencies |
| Current state | Missing/unknown |
| Phase 15 target | Implement currency validation foundation |
| Classification | `MUST_IMPLEMENT_IN_PHASE_15` for currency codes; exchange rates optional/deferred |

Required currency format:

```text
ISO 4217 code, e.g. VND, USD, EUR, SGD, JPY
```

Rules:

```text
1. Currency code required for rate lines.
2. Currency code normalized uppercase.
3. Currency code must be supported.
4. Currency rate conversion is not required unless finance/quote needs it now.
```

Exchange rate table:

```text
DEFERRED_TO_PHASE_17_FINANCE or PHASE_18_QUOTE unless needed.
```

Important future rule:

```text
Currency conversion used in finance/quote must be snapshotted.
```

---

## 7.7 RTE-007 — Inflation / escalation policy

| Item | Value |
|---|---|
| Future capability | Adjust future rates by yearly inflation/escalation |
| Current state | Missing/unknown |
| Phase 15 target | Implement foundation |
| Classification | `MUST_IMPLEMENT_IN_PHASE_15` |

Formula:

```text
Adjusted CCH = Base CCH × (1 + Inflation%) ^ YearsForward
```

Where:

```text
Base CCH = costRatePerHour from rate card line
Inflation% = escalation percent from policy
YearsForward = difference between base effective date and target date in years
```

Rules:

```text
1. Policy code required.
2. Policy scope required.
3. inflationPercent >= 0.
4. effectiveFrom required.
5. effectiveTo optional.
6. Published/active policy used by rate resolution.
7. Policy changes audited.
8. Inflation adjustment result must be snapshotted by future consumers.
```

If inflation is not implemented now:

```text
Must at least define field/contract and defer calculation to Phase 17.
```

Recommended Phase 15:

```text
Implement simple annual compound inflation policy.
```

---

## 7.8 RTE-008 — Rate resolution service

| Item | Value |
|---|---|
| Future capability | Resolve applicable rate for role/date/workspace/project |
| Current state | Missing/unknown |
| Phase 15 target | Implement |
| Classification | `MUST_IMPLEMENT_IN_PHASE_15` |

Inputs:

```text
workspaceId
organizationId
projectId optional
costRoleId or costRoleCode
targetDate
currencyCode optional
rateType COST / BILLING / BOTH
```

Output:

```text
rateCardId
rateCardVersionId
rateCardLineId
costRoleId
costRoleCode
baseCostRate
adjustedCostRate
baseBillingRate
adjustedBillingRate
currencyCode
inflationPolicyId
inflationPercent
yearsForward
resolvedAt
```

Rules:

```text
1. Prefer most specific applicable rate card:
   PROJECT future > CLIENT future > WORKSPACE > ORGANIZATION > SYSTEM.
2. Only PUBLISHED rate card versions used.
3. targetDate must fall within version effective range.
4. CostRole must be active or historically referenceable.
5. If no rate found, return clear error.
6. Resolution must be deterministic.
7. Resolution must not expose salary.
```

---

## 7.9 RTE-009 — Rate snapshot contract

| Item | Value |
|---|---|
| Future capability | Preserve historical calculations |
| Current state | Missing/unknown |
| Phase 15 target | Define DTO/embeddable/contract |
| Classification | `MUST_IMPLEMENT_IN_PHASE_15` as contract; persisted consumers in Phase 16/17/18 |

Snapshot fields:

```text
rateCardId
rateCardVersionId
rateCardLineId
costRoleId
costRoleCode
baseCostRate
adjustedCostRate
baseBillingRate
adjustedBillingRate
currencyCode
inflationPolicyId
inflationPercent
yearsForward
resolvedForDate
resolvedAt
```

Rules:

```text
1. Future project finance/quote records store snapshot, not dynamic lookup only.
2. Updating rate card later does not change historical snapshot.
3. Snapshot can reference archived version.
4. Snapshot cannot include salary.
```

---

## 7.10 RTE-010 — Project default rate card assignment

| Item | Value |
|---|---|
| Future capability | Project can choose which rate card applies |
| Current state | Missing/unknown |
| Phase 15 target | Optional |
| Classification | `DEFERRED_TO_PHASE_17_FINANCE` unless required by Phase 16 |

Recommended Phase 15 minimum:

```text
Workspace default rate card.
```

Project-level assignment can be:

```text
ProjectRateCardAssignment
```

Fields:

```text
projectId
rateCardId
effectiveFrom
effectiveTo
status
```

Reason to defer:

```text
Project finance/quote module owns commercial selection.
```

---

## 7.11 RTE-011 — Billing rate

| Item | Value |
|---|---|
| Future capability | Define commercial billing rate for quote/pricing |
| Current state | Missing/unknown |
| Phase 15 target | Store optional billingRatePerHour |
| Classification | `MUST_IMPLEMENT_IN_PHASE_15` as field; quote usage deferred |

Rules:

```text
1. billingRatePerHour optional.
2. If present, > 0.
3. Billing rate is not invoice.
4. Billing rate does not create revenue.
5. Quote module decides pricing application later.
```

---

## 7.12 RTE-012 — Approval workflow for rate card

| Item | Value |
|---|---|
| Future capability | Draft/submit/approve/publish rate cards |
| Current state | Missing |
| Phase 15 target | Simple publish permission only |
| Classification | `DEFERRED_TO_PHASE_34_WORKFLOW_APPROVAL` for full workflow |

Phase 15 minimum:

```text
User with RATE_CARD_PUBLISH can publish.
```

Future:

```text
Submit for approval
Approve/reject
SoD policy
Approval audit trail
```

---

# 8. Entity model TO-BE

If current schema differs, agent must map actual fields and document gaps.

---

## 8.1 CostRole — `rate_cost_role`

Required fields:

```text
id UUID PK
code VARCHAR(100) NOT NULL
name VARCHAR(255) NOT NULL
description TEXT NULL
scope VARCHAR(50) NOT NULL
organization_id UUID NULL
workspace_id UUID NULL
category VARCHAR(100) NULL
built_in BOOLEAN NOT NULL DEFAULT false
status VARCHAR(50) NOT NULL
created_at / created_by
updated_at / updated_by
archived_at / archived_by
version INT
```

Scope:

```text
SYSTEM
ORGANIZATION
WORKSPACE
```

Status:

```text
ACTIVE
INACTIVE
ARCHIVED
```

Constraint:

```text
unique scope + organization_id + workspace_id + code
```

---

## 8.2 WorkspaceMemberCostRoleAssignment — optional/recommended

Table:

```text
rate_workspace_member_cost_role
```

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
workspace_member_id UUID NOT NULL
user_id UUID NOT NULL
cost_role_id UUID NOT NULL
is_default BOOLEAN NOT NULL DEFAULT true
effective_from DATE NOT NULL
effective_to DATE NULL
status VARCHAR(50) NOT NULL
created_at / created_by
updated_at / updated_by
archived_at / archived_by
version INT
```

Rules:

```text
unique active default role per workspace_member over date range
```

---

## 8.3 RateCard — `rate_card`

Required fields:

```text
id UUID PK
code VARCHAR(100) NOT NULL
name VARCHAR(255) NOT NULL
description TEXT NULL
scope VARCHAR(50) NOT NULL
organization_id UUID NULL
workspace_id UUID NULL
client_id UUID NULL future
project_id UUID NULL future
default_currency_code VARCHAR(10) NOT NULL
status VARCHAR(50) NOT NULL
current_version_id UUID NULL
built_in BOOLEAN NOT NULL DEFAULT false
created_at / created_by
updated_at / updated_by
archived_at / archived_by
version INT
```

Scope:

```text
SYSTEM
ORGANIZATION
WORKSPACE
CLIENT future
PROJECT future
```

Status:

```text
DRAFT
ACTIVE
INACTIVE
ARCHIVED
```

Constraint:

```text
unique scope + organization_id + workspace_id + client_id + project_id + code
```

---

## 8.4 RateCardVersion — `rate_card_version`

Required fields:

```text
id UUID PK
rate_card_id UUID NOT NULL
version_number INT NOT NULL
name VARCHAR(255) NULL
description TEXT NULL
effective_from DATE NOT NULL
effective_to DATE NULL
status VARCHAR(50) NOT NULL
published_at TIMESTAMP NULL
published_by UUID NULL
archived_at TIMESTAMP NULL
archived_by UUID NULL
created_at / created_by
updated_at / updated_by
version INT
```

Status:

```text
DRAFT
PUBLISHED
ARCHIVED
```

Constraint:

```text
unique rate_card_id + version_number
```

---

## 8.5 RateCardLine — `rate_card_line`

Required fields:

```text
id UUID PK
rate_card_version_id UUID NOT NULL
cost_role_id UUID NOT NULL
seniority_level VARCHAR(100) NULL
location_code VARCHAR(100) NULL
currency_code VARCHAR(10) NOT NULL
cost_rate_per_hour DECIMAL(18,4) NOT NULL
billing_rate_per_hour DECIMAL(18,4) NULL
notes TEXT NULL
created_at / created_by
updated_at / updated_by
version INT
```

Constraint:

```text
unique rate_card_version_id + cost_role_id + seniority_level + location_code + currency_code
```

Rules:

```text
cost_rate_per_hour > 0
billing_rate_per_hour null or > 0
```

---

## 8.6 InflationPolicy — `rate_inflation_policy`

Required fields:

```text
id UUID PK
code VARCHAR(100) NOT NULL
name VARCHAR(255) NOT NULL
description TEXT NULL
scope VARCHAR(50) NOT NULL
organization_id UUID NULL
workspace_id UUID NULL
inflation_percent DECIMAL(8,4) NOT NULL
compound_frequency VARCHAR(50) NOT NULL
effective_from DATE NOT NULL
effective_to DATE NULL
status VARCHAR(50) NOT NULL
created_at / created_by
updated_at / updated_by
archived_at / archived_by
version INT
```

Frequency:

```text
ANNUAL
MONTHLY optional
NONE
```

Status:

```text
ACTIVE
INACTIVE
ARCHIVED
```

Rules:

```text
inflation_percent >= 0
```

---

## 8.7 Currency — optional static table

If implemented:

```text
rate_currency
```

Fields:

```text
code VARCHAR(10) PK
name VARCHAR(255)
symbol VARCHAR(20)
decimal_places INT
status VARCHAR(50)
```

If not implemented:

```text
Use ISO code enum/config.
```

Recommended Phase 15:

```text
Use enum/config unless finance module needs currency table.
```

---

## 8.8 CurrencyRate — deferred

```text
rate_currency_exchange
```

Deferred to:

```text
Phase 17 Finance / Phase 18 Quote
```

Reason:

```text
Currency conversion affects finance/quote snapshots and should be implemented with those modules.
```

---

# 9. API TO-BE list

All APIs use `/api`.

---

## 9.1 CostRole APIs

```text
POST  /api/rate-card/cost-roles
GET   /api/rate-card/cost-roles
GET   /api/rate-card/cost-roles/{roleId}
PUT   /api/rate-card/cost-roles/{roleId}
PATCH /api/rate-card/cost-roles/{roleId}/activate
PATCH /api/rate-card/cost-roles/{roleId}/deactivate
PATCH /api/rate-card/cost-roles/{roleId}/archive
```

Filters:

```text
scope
workspaceId
organizationId
status
category
code
```

---

## 9.2 Workspace member cost role assignment APIs

If implemented:

```text
POST  /api/rate-card/member-cost-roles
GET   /api/rate-card/member-cost-roles
GET   /api/rate-card/member-cost-roles/{assignmentId}
PUT   /api/rate-card/member-cost-roles/{assignmentId}
PATCH /api/rate-card/member-cost-roles/{assignmentId}/archive
```

Filters:

```text
workspaceId
workspaceMemberId
userId
costRoleId
effectiveDate
status
```

---

## 9.3 RateCard APIs

```text
POST  /api/rate-card/cards
GET   /api/rate-card/cards
GET   /api/rate-card/cards/{rateCardId}
PUT   /api/rate-card/cards/{rateCardId}
PATCH /api/rate-card/cards/{rateCardId}/activate
PATCH /api/rate-card/cards/{rateCardId}/deactivate
PATCH /api/rate-card/cards/{rateCardId}/archive
```

Filters:

```text
scope
workspaceId
organizationId
status
currency
code
```

---

## 9.4 RateCardVersion APIs

```text
POST  /api/rate-card/cards/{rateCardId}/versions
GET   /api/rate-card/cards/{rateCardId}/versions
GET   /api/rate-card/cards/{rateCardId}/versions/{versionId}
PUT   /api/rate-card/cards/{rateCardId}/versions/{versionId}
POST  /api/rate-card/cards/{rateCardId}/versions/{versionId}/publish
PATCH /api/rate-card/cards/{rateCardId}/versions/{versionId}/archive
POST  /api/rate-card/cards/{rateCardId}/versions/{versionId}/duplicate
```

Rules:

```text
PUT only DRAFT.
Publish validates lines.
Published immutable.
```

---

## 9.5 RateCardLine APIs

```text
POST   /api/rate-card/cards/{rateCardId}/versions/{versionId}/lines
GET    /api/rate-card/cards/{rateCardId}/versions/{versionId}/lines
GET    /api/rate-card/cards/{rateCardId}/versions/{versionId}/lines/{lineId}
PUT    /api/rate-card/cards/{rateCardId}/versions/{versionId}/lines/{lineId}
DELETE /api/rate-card/cards/{rateCardId}/versions/{versionId}/lines/{lineId}
```

Rules:

```text
Only DRAFT version can mutate lines.
```

---

## 9.6 InflationPolicy APIs

```text
POST  /api/rate-card/inflation-policies
GET   /api/rate-card/inflation-policies
GET   /api/rate-card/inflation-policies/{policyId}
PUT   /api/rate-card/inflation-policies/{policyId}
PATCH /api/rate-card/inflation-policies/{policyId}/activate
PATCH /api/rate-card/inflation-policies/{policyId}/deactivate
PATCH /api/rate-card/inflation-policies/{policyId}/archive
```

---

## 9.7 Rate resolution APIs

```text
POST /api/rate-card/resolve
POST /api/rate-card/preview-task-rate
```

Resolve request:

```json
{
  "workspaceId": "uuid",
  "projectId": "uuid",
  "costRoleCode": "BACKEND_DEVELOPER",
  "targetDate": "2027-01-15",
  "currencyCode": "VND",
  "rateType": "BOTH"
}
```

Resolve response:

```json
{
  "rateCardId": "uuid",
  "rateCardVersionId": "uuid",
  "rateCardLineId": "uuid",
  "costRoleCode": "BACKEND_DEVELOPER",
  "baseCostRate": 500000,
  "adjustedCostRate": 525000,
  "baseBillingRate": 850000,
  "adjustedBillingRate": 892500,
  "currencyCode": "VND",
  "inflationPolicyId": "uuid",
  "inflationPercent": 5.0,
  "yearsForward": 1,
  "resolvedAt": "2026-07-11T..."
}
```

Preview task rate request:

```json
{
  "taskId": "uuid",
  "targetDate": "2027-01-15",
  "costRoleCode": "BACKEND_DEVELOPER"
}
```

Phase 15 preview can return:

```text
rate × estimateHours
```

But must label as:

```text
rate preview / estimated labor cost preview
```

Not official project finance.

---

# 10. Authorization requirements

Required IAM authorities:

```text
COST_ROLE_VIEW
COST_ROLE_CREATE
COST_ROLE_UPDATE
COST_ROLE_ARCHIVE
COST_ROLE_MANAGE

RATE_CARD_VIEW
RATE_CARD_CREATE
RATE_CARD_UPDATE
RATE_CARD_PUBLISH
RATE_CARD_ARCHIVE
RATE_CARD_MANAGE

RATE_CARD_LINE_VIEW
RATE_CARD_LINE_CREATE
RATE_CARD_LINE_UPDATE
RATE_CARD_LINE_DELETE

INFLATION_POLICY_VIEW
INFLATION_POLICY_CREATE
INFLATION_POLICY_UPDATE
INFLATION_POLICY_ARCHIVE
INFLATION_POLICY_MANAGE

RATE_RESOLUTION_VIEW
RATE_RESOLUTION_PREVIEW

MEMBER_COST_ROLE_VIEW
MEMBER_COST_ROLE_ASSIGN
MEMBER_COST_ROLE_MANAGE
```

Rules:

```text
1. All rate APIs require authentication.
2. Workspace-scoped rate card requires active workspace access.
3. Organization-scoped rate card requires active org access.
4. Publishing rate card requires RATE_CARD_PUBLISH.
5. Rate card line mutation requires version DRAFT and permission.
6. Member cost role assignment requires workspace member management or rate management permission.
7. Rate resolution requires access to target workspace/project.
8. Rate response must not include salary or personal payroll.
```

Future finance-specific permissions:

```text
PROJECT_FINANCE_VIEW
QUOTE_RATE_OVERRIDE
QUOTE_MARGIN_VIEW
```

Deferred to later phases.

---

# 11. Rate resolution precedence

Recommended precedence:

```text
1. Project-specific rate card — future Phase 17/18
2. Client-specific rate card — future Phase 18/29
3. Workspace rate card
4. Organization rate card
5. System default rate card
```

Phase 15 minimum:

```text
Workspace > Organization > System
```

Rules:

```text
1. More specific scope wins.
2. If multiple cards match same specificity, use default active card or require explicit selection.
3. If no card matches, error.
4. targetDate must fall in published version effective range.
5. If no version matches, error.
```

---

# 12. Inflation calculation

## 12.1 Formula

```text
AdjustedRate = BaseRate × (1 + InflationPercent / 100) ^ YearsForward
```

## 12.2 YearsForward

Recommended:

```text
YearsForward = whole or decimal years between rateCardVersion.effectiveFrom and targetDate
```

Simpler Phase 15 option:

```text
YearsForward = calendar year difference
```

Completion file must document chosen method.

## 12.3 Rounding

Recommended:

```text
Money/rate values use BigDecimal.
Rounding mode: HALF_UP.
Scale: 4 for rate calculation, currency-specific display later.
```

Do not use floating point.

## 12.4 Example

```text
Base CCH = 500,000 VND/hour
Inflation = 5%
YearsForward = 2

Adjusted CCH = 500,000 × (1.05)^2 = 551,250 VND/hour
```

---

# 13. Integration with Project / Task

Phase 15 can resolve rate for:

```text
Task
Task assignee's default cost role
Explicit costRoleCode
Target date
Workspace/project
```

But must not persist official project cost.

Allowed:

```text
Rate preview
Task rate resolution
Return snapshot DTO
```

Not allowed:

```text
Project budget table
Phase cost roll-up
Gross margin
Profit before tax
Quote price
```

If task has no cost role:

```text
Return RATE_ROLE_NOT_RESOLVED.
```

Future Phase 16/17 decides role mapping.

---

# 14. Integration with Capacity / Scheduling

Capacity/scheduling tells:

```text
when work may happen
how many hours are scheduled
```

Rate card tells:

```text
what cost/billing rate applies
```

Future cost phasing may use:

```text
ScheduledDailyWork.workDate
Resolved rate for that date
```

But Phase 15 does not calculate scheduled cost.

---

# 15. Integration with Estimation / Finance / Quote

## 15.1 Phase 16 — Estimation roll-up

Will consume:

```text
Task estimateHours
CostRole
RateResolutionService
```

To calculate:

```text
estimated labor amount by task/WBS/phase/project
```

## 15.2 Phase 17 — Finance

Will consume:

```text
rate snapshots
custom phase costs
overhead
revenue split
margin formulas
```

## 15.3 Phase 18 — Quote

Will consume:

```text
billing rate
target margin
contract value solver
quote line pricing
```

Phase 15 only stores rates.

---

# 16. Event Registry integration

Recommended source system:

```text
SCOPERY_RATE_CARD
```

Required events:

```text
COST_ROLE_CREATED
COST_ROLE_UPDATED
COST_ROLE_ARCHIVED
COST_ROLE_ACTIVATED
COST_ROLE_DEACTIVATED

MEMBER_COST_ROLE_ASSIGNED
MEMBER_COST_ROLE_UPDATED
MEMBER_COST_ROLE_ARCHIVED

RATE_CARD_CREATED
RATE_CARD_UPDATED
RATE_CARD_ACTIVATED
RATE_CARD_DEACTIVATED
RATE_CARD_ARCHIVED

RATE_CARD_VERSION_CREATED
RATE_CARD_VERSION_UPDATED
RATE_CARD_VERSION_PUBLISHED
RATE_CARD_VERSION_ARCHIVED
RATE_CARD_VERSION_DUPLICATED

RATE_CARD_LINE_CREATED
RATE_CARD_LINE_UPDATED
RATE_CARD_LINE_DELETED

INFLATION_POLICY_CREATED
INFLATION_POLICY_UPDATED
INFLATION_POLICY_ACTIVATED
INFLATION_POLICY_DEACTIVATED
INFLATION_POLICY_ARCHIVED

RATE_RESOLVED
RATE_RESOLUTION_FAILED
```

Standard variables:

```text
actor.userId
organization.id
workspace.id
rateCard.id
rateCard.code
rateCardVersion.id
rateCardVersion.versionNumber
rateCardLine.id
costRole.id
costRole.code
currency.code
inflationPolicy.id
targetDate
resolvedRate
occurredAt
traceId
```

---

# 17. Notification integration

Phase 15 should not notify normal users.

Optional admin notifications:

```text
RATE_CARD_VERSION_PUBLISHED_ADMIN_EMAIL
RATE_CARD_ARCHIVED_ADMIN_EMAIL
```

Recommended:

```text
Seed events only.
Notification rules deferred to Phase 20/22 if needed.
```

Rate changes are sensitive; do not send broad notifications without policy.

---

# 18. AI Agent integration

Phase 15 does not implement AI pricing.

Future Phase 21/22 may use rates to:

```text
explain cost drivers
suggest role mix
identify expensive phases
recommend estimate adjustments
```

Rules:

```text
AI must not expose sensitive rates to users without permission.
AI must not infer salary.
AI must not auto-change rate card.
```

---

# 19. Platform audit/outbox/idempotency integration

## 19.1 Activity log actions

```text
COST_ROLE_CREATED
COST_ROLE_UPDATED
RATE_CARD_CREATED
RATE_CARD_UPDATED
RATE_CARD_VERSION_CREATED
RATE_CARD_VERSION_PUBLISHED
RATE_CARD_LINE_CREATED
RATE_CARD_LINE_UPDATED
INFLATION_POLICY_CREATED
INFLATION_POLICY_UPDATED
MEMBER_COST_ROLE_ASSIGNED
```

## 19.2 Audit-sensitive actions

Audit:

```text
RateCardVersion published
RateCardLine costRate changed
RateCardLine billingRate changed
InflationPolicy changed
Member cost role assignment changed
RateCard archived
CostRole archived
```

Reason:

```text
These changes affect future estimates, project finance, and quote pricing.
```

## 19.3 Idempotency

Recommended for:

```text
POST /api/rate-card/cost-roles
POST /api/rate-card/cards
POST /api/rate-card/cards/{id}/versions
POST /api/rate-card/cards/{id}/versions/{versionId}/publish
POST /api/rate-card/cards/{id}/versions/{versionId}/lines
POST /api/rate-card/inflation-policies
POST /api/rate-card/member-cost-roles
```

## 19.4 Outbox

Rate events should use platform outbox if available.

---

# 20. Seed data requirements

## 20.1 CostRole seed

Seed system roles:

```text
PROJECT_MANAGER
BUSINESS_ANALYST
SOLUTION_ARCHITECT
TECH_LEAD
FRONTEND_DEVELOPER
BACKEND_DEVELOPER
FULLSTACK_DEVELOPER
MOBILE_DEVELOPER
QA_ENGINEER
DEVOPS_ENGINEER
UX_UI_DESIGNER
DATA_ENGINEER
AI_ENGINEER
```

Rules:

```text
1. Idempotent.
2. Built-in true.
3. Scope SYSTEM.
4. Status ACTIVE.
5. Do not overwrite custom roles.
```

## 20.2 Default rate card seed

Optional:

```text
SYSTEM_DEFAULT_RATE_CARD
```

Recommended:

```text
Create structure but do not seed real commercial rates unless product owner provides values.
```

If seeded with placeholder rates:

```text
Must be marked SAMPLE / DEV_ONLY.
```

Do not put fake production rates into production seeds.

## 20.3 Currency seed

Supported currencies:

```text
VND
USD
EUR
SGD
JPY
KRW
CNY
```

Adjust to product market.

## 20.4 Inflation policy seed

Optional:

```text
DEFAULT_ANNUAL_INFLATION_0_PERCENT
```

Safe default:

```text
0%
```

Do not invent real inflation assumptions.

---

# 21. Business rules master

## 21.1 CostRole rules

```text
RTE-ROLE-001 Code required.
RTE-ROLE-002 Code normalized uppercase.
RTE-ROLE-003 Code unique within scope.
RTE-ROLE-004 Name required.
RTE-ROLE-005 Scope required.
RTE-ROLE-006 Built-in role cannot be hard-deleted.
RTE-ROLE-007 Archived role cannot be used in new rate lines.
RTE-ROLE-008 CostRole is not IAM role.
RTE-ROLE-009 CostRole does not expose salary.
```

## 21.2 RateCard rules

```text
RTE-CARD-001 Code required.
RTE-CARD-002 Code normalized uppercase.
RTE-CARD-003 Code unique within scope.
RTE-CARD-004 Name required.
RTE-CARD-005 Scope required.
RTE-CARD-006 Workspace-scoped card requires active workspace.
RTE-CARD-007 Archived card cannot be used for new resolution.
RTE-CARD-008 Active card should have current published version.
```

## 21.3 RateCardVersion rules

```text
RTE-VER-001 Version number unique within rate card.
RTE-VER-002 EffectiveFrom required.
RTE-VER-003 EffectiveTo >= EffectiveFrom if provided.
RTE-VER-004 DRAFT can be edited.
RTE-VER-005 PUBLISHED immutable.
RTE-VER-006 Publishing requires at least one line.
RTE-VER-007 Publishing validates no duplicate lines.
RTE-VER-008 Publishing validates active cost roles.
RTE-VER-009 Publishing validates positive rates.
RTE-VER-010 Published version cannot overlap another published version unless policy allows.
RTE-VER-011 Archived version not used for new resolution.
```

## 21.4 RateCardLine rules

```text
RTE-LINE-001 Version must be DRAFT to mutate.
RTE-LINE-002 CostRole required and active.
RTE-LINE-003 Currency required and valid.
RTE-LINE-004 costRatePerHour > 0.
RTE-LINE-005 billingRatePerHour null or > 0.
RTE-LINE-006 Duplicate role/seniority/location/currency blocked.
RTE-LINE-007 Line does not store salary.
```

## 21.5 Inflation rules

```text
RTE-INF-001 Policy code required.
RTE-INF-002 inflationPercent >= 0.
RTE-INF-003 effective date range valid.
RTE-INF-004 Active policy used by rate resolution.
RTE-INF-005 Adjusted rate uses BigDecimal.
RTE-INF-006 Result must be snapshotted by future consumers.
```

## 21.6 Rate resolution rules

```text
RTE-RES-001 targetDate required.
RTE-RES-002 CostRole required.
RTE-RES-003 Only PUBLISHED version used.
RTE-RES-004 Most specific applicable card wins.
RTE-RES-005 targetDate must be within version date range.
RTE-RES-006 If no rate found, return clear error.
RTE-RES-007 Resolution deterministic.
RTE-RES-008 Resolution response excludes salary.
RTE-RES-009 Snapshot fields returned.
```

---

# 22. Error catalog requirements

Exact names follow project convention, but these concepts must exist.

```text
COST_ROLE_NOT_FOUND
COST_ROLE_CODE_ALREADY_EXISTS
COST_ROLE_INVALID_SCOPE
COST_ROLE_ARCHIVED
COST_ROLE_IN_USE
COST_ROLE_BUILT_IN_CANNOT_DELETE

MEMBER_COST_ROLE_ASSIGNMENT_NOT_FOUND
MEMBER_COST_ROLE_MEMBER_INACTIVE
MEMBER_COST_ROLE_ROLE_INACTIVE
MEMBER_COST_ROLE_DATE_RANGE_INVALID
MEMBER_COST_ROLE_OVERLAP

RATE_CARD_NOT_FOUND
RATE_CARD_CODE_ALREADY_EXISTS
RATE_CARD_INVALID_SCOPE
RATE_CARD_ARCHIVED
RATE_CARD_NO_PUBLISHED_VERSION

RATE_CARD_VERSION_NOT_FOUND
RATE_CARD_VERSION_NOT_DRAFT
RATE_CARD_VERSION_NOT_PUBLISHED
RATE_CARD_VERSION_ALREADY_PUBLISHED
RATE_CARD_VERSION_DATE_RANGE_INVALID
RATE_CARD_VERSION_OVERLAP
RATE_CARD_VERSION_NO_LINES
RATE_CARD_VERSION_STRUCTURE_INVALID

RATE_CARD_LINE_NOT_FOUND
RATE_CARD_LINE_DUPLICATE
RATE_CARD_LINE_INVALID_COST_RATE
RATE_CARD_LINE_INVALID_BILLING_RATE
RATE_CARD_LINE_INVALID_CURRENCY
RATE_CARD_LINE_ROLE_INACTIVE

INFLATION_POLICY_NOT_FOUND
INFLATION_POLICY_CODE_ALREADY_EXISTS
INFLATION_POLICY_INVALID_PERCENT
INFLATION_POLICY_DATE_RANGE_INVALID

RATE_RESOLUTION_NOT_FOUND
RATE_RESOLUTION_ROLE_NOT_FOUND
RATE_RESOLUTION_NO_APPLICABLE_CARD
RATE_RESOLUTION_NO_APPLICABLE_VERSION
RATE_RESOLUTION_NO_APPLICABLE_LINE
RATE_RESOLUTION_ACCESS_DENIED

RATE_CARD_ACCESS_DENIED
```

---

# 23. Required tests

Phase 15 is incomplete without tests.

---

## 23.1 CostRole tests

```text
createCostRole_validSystem_success
createCostRole_duplicateCodeSameScope_conflict
createCostRole_sameCodeDifferentWorkspace_allowed
createCostRole_invalidScope_rejected
createCostRole_codeNormalizedUppercase
archiveBuiltInRole_rejectedOrSoftArchiveAccordingToPolicy
useArchivedRoleInRateLine_rejected
costRoleSeeder_firstRun_createsRoles
costRoleSeeder_secondRun_noDuplicates
```

## 23.2 Member cost role tests

If implemented:

```text
assignCostRole_valid_success
assignCostRole_inactiveWorkspaceMember_rejected
assignCostRole_inactiveCostRole_rejected
assignCostRole_overlapDefault_rejected
assignCostRole_dateRangeInvalid_rejected
updateAssignment_audited
```

If deferred:

```text
Mark tests deferred to Phase 16.
```

## 23.3 RateCard tests

```text
createRateCard_validWorkspace_success
createRateCard_duplicateCodeSameScope_conflict
createRateCard_sameCodeDifferentWorkspace_allowed
createRateCard_invalidScope_rejected
updateArchivedRateCard_rejected
archiveRateCard_blocksNewResolution
```

## 23.4 RateCardVersion tests

```text
createVersion_valid_success
updateDraftVersion_success
updatePublishedVersion_rejected
publishVersion_withoutLines_rejected
publishVersion_withInvalidLine_rejected
publishVersion_valid_success
publishVersion_overlapPublishedVersion_rejectedOrDocumented
archivePublishedVersion_blocksNewResolution
duplicateVersion_createsDraftCopy
```

## 23.5 RateCardLine tests

```text
createLine_valid_success
createLine_onPublishedVersion_rejected
createLine_inactiveRole_rejected
createLine_invalidCurrency_rejected
createLine_costRateZero_rejected
createLine_costRateNegative_rejected
createLine_billingRateNegative_rejected
createLine_duplicateRoleCurrency_conflict
updateLine_onDraft_success
deleteLine_onDraft_success
```

## 23.6 InflationPolicy tests

```text
createInflationPolicy_valid_success
createInflationPolicy_negativePercent_rejected
createInflationPolicy_invalidDateRange_rejected
activateInflationPolicy_success
adjustedRate_zeroInflation_equalsBase
adjustedRate_positiveInflation_compounds
adjustedRate_usesBigDecimalNoFloat
```

## 23.7 Rate resolution tests

```text
resolveRate_workspaceRate_success
resolveRate_orgFallback_success
resolveRate_systemFallback_success
resolveRate_noApplicableCard_rejected
resolveRate_noPublishedVersion_rejected
resolveRate_targetDateOutsideVersion_rejected
resolveRate_noLineForRole_rejected
resolveRate_appliesInflation
resolveRate_returnsSnapshotFields
resolveRate_doesNotExposeSalary
resolveRate_deterministic
```

## 23.8 Authorization tests

```text
createRateCard_withoutPermission_forbidden
publishRateCardVersion_withoutPermission_forbidden
updateRateLine_withoutPermission_forbidden
resolveRate_withoutAccess_forbidden
crossWorkspaceRateAccess_forbidden
```

## 23.9 Seeder/event tests

```text
rateEventSeeder_firstRun_createsDefinitions
rateEventSeeder_secondRun_noDuplicates
ratePermissionSeeder_authoritiesExist
rateCardPublished_eventEmitted
rateLineChanged_auditCreated
inflationPolicyChanged_auditCreated
```

---

# 24. Manual verification checklist

Completion file must include:

```text
1. Create cost role.
2. Create rate card.
3. Create rate card version.
4. Add rate line with CCH and billing rate.
5. Try invalid zero/negative rate and confirm rejection.
6. Publish version.
7. Confirm published version immutable.
8. Resolve rate for role/date.
9. Confirm inflation calculation if policy active.
10. Confirm snapshot fields returned.
11. Confirm no salary/payroll fields returned.
12. Archive rate card/version and confirm new resolution blocked.
13. Rerun seeders and confirm no duplicates.
14. Confirm events/activity/audit created.
15. Confirm no project budget/margin/quote records created.
```

---

# 25. Acceptance criteria

Phase 15 is accepted only if:

```text
1. Current rate/cost capability is classified against TO-BE.
2. CostRole implemented/tested.
3. RateCard implemented/tested.
4. RateCardVersion implemented/tested.
5. RateCardLine implemented/tested.
6. Published versions are immutable.
7. Currency validation implemented/tested.
8. Inflation policy implemented/tested or explicitly deferred with contract.
9. RateResolutionService implemented/tested.
10. Rate snapshot contract defined.
11. Authorization matrix implemented/tested.
12. Events seeded idempotently.
13. Activity/audit/outbox integration follows Phase 04.
14. No salary/payroll data is stored/exposed.
15. Phase 15 does not falsely claim project finance/margin/quote/billing/timesheet.
16. mvn compile passes.
17. mvn test passes.
18. Completion file exists and includes gap matrix.
```

Agent must not mark complete if:

```text
mvn test fails
published rate version can be edited
zero/negative rates accepted
archived rate card used for new resolution
rate resolution nondeterministic
salary or payroll fields exposed
historical snapshot contract missing
project budget/margin/quote is claimed implemented
```

---

# 26. Required phase completion file

Agent must create:

```text
docs/phase-complete/PHASE_15_RATE_CARD_COST_POLICY_TO_BE_COMPLETE.md
```

Required sections:

```text
# Phase 15 — Rate Card / Cost Policy TO-BE Complete

## 1. Summary
## 2. Source Inputs Reviewed
## 3. Current vs TO-BE Classification Matrix
## 4. Implemented in Current BE
## 5. Implemented / Hardened in This Phase
## 6. Seed-only Items Added
## 7. Deferred Items and Target Phase
## 8. CostRole Decision
## 9. Member Cost Role Mapping Decision
## 10. RateCard Entity Mapping
## 11. RateCardVersion Entity Mapping
## 12. RateCardLine Entity Mapping
## 13. InflationPolicy Decision
## 14. Currency Decision
## 15. Rate Resolution Strategy
## 16. Rate Snapshot Contract
## 17. API Changes
## 18. Authorization Matrix
## 19. Event Registry Seeder Matrix
## 20. Activity / Audit / Outbox Notes
## 21. Idempotency Strategy
## 22. Tests Added
## 23. Commands Run
## 24. Test Results
## 25. Manual Verification
## 26. Assumptions
## 27. Deviations From Prompt
## 28. Known Risks
## 29. Future Phases That Must Return to Rate Card
```

---

# 27. Future phases that must return to Rate Card

## 27.1 Phase 16 — Estimation Roll-up

Must consume:

```text
Task estimateHours
Task/assignee cost role
RateResolutionService
RateSnapshot
```

Must calculate:

```text
task estimated labor amount
WBS estimated labor amount
phase estimated labor amount
project estimated labor amount
```

## 27.2 Phase 17 — Project Budget / Margin

Must consume:

```text
rate snapshots
labor cost roll-up
custom phase costs
overhead
revenue split
target margin
```

Must ensure historical calculations do not change after rate update.

## 27.3 Phase 18 — Quote

Must consume:

```text
billingRatePerHour
target margin
contract value solver
commercial override policy
```

Quote should store rate snapshots.

## 27.4 Phase 19 — Baseline / Change Request

Baseline must snapshot:

```text
rates used
cost assumptions
estimates
finance scenario
```

Rate changes after baseline may require change request.

## 27.5 Phase 20 — Notifications

May notify rate admins:

```text
rate card published
rate card archived
rate card version expiring
```

## 27.6 Phase 21 — AI-assisted Planning

AI may explain cost drivers but must respect rate permissions.

## 27.7 Phase 22 — Reporting

Reports:

```text
rate card coverage
cost by role
billing vs cost rate
rate version usage
```

Must enforce rate/finance permissions.

## 27.8 Phase 23 — Core Hardening

Must verify:

```text
rate resolution performance
date range overlap edge cases
currency precision
BigDecimal rounding
authorization coverage
snapshot integrity
```

## 27.9 Phase 37 — Time / Attendance / Expense

Actual work cost may use:

```text
actual hours × historical rate snapshot
```

Do not recalc actual historical costs from changed rates.

---

# 28. Agent anti-bịa rules

The agent must not:

```text
1. Claim Phase 15 implements project budget.
2. Claim Phase 15 implements gross margin.
3. Claim Phase 15 implements quote price.
4. Claim Phase 15 implements invoice/billing.
5. Store or expose personal salary.
6. Treat IAM role as cost role.
7. Let published rate version be edited.
8. Let rate resolution use draft version.
9. Let archived card/version be used for new resolution.
10. Use floating point for money/rate calculation.
11. Ignore historical snapshot requirement.
12. Hide currency/exchange-rate deferral.
```

---

# 29. Prompt to give coding agent

```text
You are implementing Phase 15 — TO-BE Rate Card, Cost Role, CCH, Billing Rate, Currency & Inflation Policy Foundation.

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
- Current backend code, migrations, tests

Your task:
1. Compare current rate/cost capability against this Phase 15 TO-BE spec.
2. Classify each capability as CURRENTLY_IMPLEMENTED, PARTIALLY_IMPLEMENTED, MUST_IMPLEMENT_IN_PHASE_15, SEED_ONLY_IN_PHASE_15, DEFERRED_TO_PHASE_XX, or NOT_IN_SCOPE_FOR_BACKEND_NOW.
3. Implement only Phase 15 required items.
4. Implement CostRole, RateCard, RateCardVersion, RateCardLine, InflationPolicy, and RateResolutionService.
5. Implement currency validation.
6. Enforce published version immutability.
7. Define and return rate snapshot contract.
8. Add permissions and event definitions.
9. Add tests listed in this spec.
10. Run mvn compile and mvn test.
11. Create docs/phase-complete/PHASE_15_RATE_CARD_COST_POLICY_TO_BE_COMPLETE.md with full gap matrix.

Do not implement or claim project budget, margin, quote, contract value solver, invoice/billing, timesheet actuals, payroll, or salary in this phase.
```

---

# 30. Quick tracking matrix

| Capability | Current backend | Phase 15 action | Later phase |
|---|---|---|---|
| CostRole | Missing/unknown | Must implement | — |
| Member cost role mapping | Missing/unknown | Implement or defer | Phase 16 |
| RateCard | Missing/unknown | Must implement | — |
| RateCardVersion | Missing/unknown | Must implement | — |
| RateCardLine | Missing/unknown | Must implement | — |
| CCH / cost rate | Missing/unknown | Must implement | Phase 16/17 consume |
| Billing rate | Missing/unknown | Store optional | Phase 18 consume |
| Currency validation | Missing/unknown | Must implement | Phase 17 conversion |
| Currency exchange | Missing | Defer | Phase 17/18 |
| Inflation policy | Missing/unknown | Must implement foundation | Phase 17/18 consume |
| Rate resolution | Missing/unknown | Must implement | Phase 16/17/18 consume |
| Rate snapshot contract | Missing | Must define | Phase 16/17/18 persist |
| Project budget | Missing | Defer | Phase 17 |
| Estimation roll-up | Missing | Defer | Phase 16 |
| Quote pricing | Missing | Defer | Phase 18 |
| Billing/invoice | Missing | Defer | Phase 36 |
| Timesheet actual cost | Missing | Defer | Phase 37 |
| Salary/payroll | Not scope | Not implement | HR/payroll not in scope |

---

# 31. Final principle

Phase 15 is not complete when "a rate value can be stored."

Phase 15 is complete when Scopery has a safe rate foundation:

```text
Cost roles are governed.
Rate cards are versioned.
Published rates are immutable.
CCH and billing rates are validated.
Inflation can be applied predictably.
Rate resolution is deterministic.
Snapshots protect historical calculations.
No salary is exposed.
No fake project budget, margin, or quote is claimed.
```

Rate card is unit price/cost policy.

Finance is calculation.

Quote is commercial commitment.

Timesheet is actual work.

Salary is not stored here.
