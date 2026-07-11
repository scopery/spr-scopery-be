# PHASE 18 — TO-BE Quotation, Commercial Proposal, Quote Versioning, Target Margin Solver & Client-facing Price Foundation

> Project: Scopery Backend  
> Phase: 18  
> Document type: TO-BE implementation-grade specification  
> Status: Planning / Gap-driven implementation spec  
> Depends on: Phase 00 — Master Backend Roadmap, Phase 01 — API Path / Security Baseline, Phase 02 — IAM Core, Phase 03 — Workspace Core, Phase 04 — Platform Audit / Outbox / Idempotency, Phase 05 — Event Registry, Phase 06 — Notification Engine, Phase 07 — AI Agent Platform, Phase 08 — Knowledge / Document Type Catalog, Phase 09 — Project Core / Phase / WBS / Task, Phase 10 — Project Authorization, Phase 11 — Project Template / Phase Catalog, Phase 12 — Resource Calendar / Capacity, Phase 13 — Task Scheduling Engine, Phase 14 — WBS-driven Gantt, Phase 15 — Rate Card / Cost Policy, Phase 16 — Estimation Roll-up, Phase 17 — Project Budget / Margin  
> API base: `/api`  
> Primary module: `modules/quote` or `modules/project/quote` depending on repository architecture  
> Related modules: `project`, `projectfinance`, `estimation`, `ratecard`, `workspace`, `iam`, `eventregistry`, `notification`, future `contract`, `billing`, `external-party`, `document`, `baseline`, `reporting`, `aiagent`  
> Important rule: This file is **not an as-is document**. It defines the TO-BE quote and commercial proposal foundation. Phase 18 does not implement signed contract, invoice/billing, revenue recognition, payment collection, tax accounting, actual cost, or legal contract lifecycle.

---

# 0. Purpose of this file

Phase 18 converts internal project finance planning into a commercial quote/proposal foundation.

Previous phases created:

```text
Phase 15:
- Rate card
- CCH / cost rate
- Billing rate
- Rate snapshot

Phase 16:
- EstimationRun
- Task labor estimate
- WBS/Phase/Project estimate roll-up

Phase 17:
- ProjectFinanceScenario
- Direct cost
- Custom cost
- Vendor planned cost
- Contingency
- Overhead
- Planned revenue
- Gross margin
- Profit before tax
```

Phase 18 answers:

```text
What commercial price should be quoted to a client?
How can required contract value be calculated from target margin?
How are quote versions created and preserved?
How are quote lines generated from finance phases/cost groups?
How can a quote be submitted, approved, rejected, revised, and marked current?
What client-facing assumptions/terms are included?
What quote snapshot must be preserved so later finance/rate changes do not silently change sent quotes?
```

Phase 18 does **not** answer:

```text
Has the client signed the contract?
Has an invoice been issued?
Has revenue been recognized?
Has payment been collected?
What tax is due?
What actual work cost occurred?
```

Those belong to later phases.

---

# 1. Source inputs

Before coding Phase 18, the agent must read:

```text
1. Current backend codebase
2. Phase 15 Rate Card / Cost Policy TO-BE spec and implementation
3. Phase 16 Estimation Roll-up TO-BE spec and implementation
4. Phase 17 Project Budget / Margin TO-BE spec and implementation
5. Phase 09 Project Core TO-BE spec and implementation
6. Phase 10 Project Authorization TO-BE spec and implementation
7. Phase 08 Knowledge / Document Type Catalog if proposal document type references are considered
8. Phase 04 Platform Audit / Outbox / Idempotency spec
9. Phase 05 Event Registry spec
10. Phase 06 Notification spec
11. Current BE feature/entity/business-rule inventory
12. Dynamic Work OS feature catalog
13. Existing quote/proposal/client/commercial code if any
```

The agent must not implement Phase 18 from assumptions only.

---

# 2. Current expected backend state

After Phase 17, the backend should have:

```text
Project
ProjectFinanceScenario
ProjectFinanceSummary
ProjectPhaseFinance
ProjectCustomCost
ProjectVendorCost
EstimationRun
Rate snapshots
```

Likely missing:

```text
Quote
QuoteVersion
QuoteLine
QuotePhaseLine
QuoteSummary
QuoteApproval
QuoteTerm
Commercial proposal metadata
Target margin solver
Quote revision history
Quote submit/approve/reject workflow
Quote export/proposal document package
Client CRM integration
Contract conversion
Billing/invoice
```

Phase 18 implements quote/proposal foundation, not contract/billing.

---

# 3. Phase 18 target statement

Phase 18 must deliver a future-ready quotation foundation:

```text
1. Create Quote under a Project from an approved/current ProjectFinanceScenario.
2. Create QuoteVersion snapshots from finance scenario totals and phase finance.
3. Generate quote lines from finance phases and/or cost groups.
4. Support target margin solver:
   RequiredContractValue = CostBase / (1 - TargetMargin%)
5. Support manual quote price adjustments with audit.
6. Calculate quote subtotal, discount, tax placeholder/exclusion, total quoted amount, margin, and PBT preview.
7. Preserve quote version snapshots so later finance/rate changes do not change sent quotes.
8. Support quote lifecycle: DRAFT, SUBMITTED, APPROVED, REJECTED, SENT, ACCEPTED optional, ARCHIVED.
9. Implement simple approval action; full workflow engine deferred.
10. Protect quote data with dedicated IAM permissions.
11. Emit quote events and audit sensitive actions.
12. Clearly defer contract, billing, invoice, revenue recognition, tax accounting, signed acceptance, and legal workflow.
```

---

# 4. Commercial boundary decisions

## 4.1 Quote is commercial proposal, not contract

A quote is an offer/proposal.

A contract is a signed/accepted binding agreement.

Phase 18 does not create legal contract records unless the product explicitly treats accepted quote as draft contract.

Recommended:

```text
Quote accepted → future Phase 36 Contract / Billing.
```

## 4.2 Quote is not invoice

Quote amount is not invoice amount.

Invoice/billing schedule is deferred.

## 4.3 Tax is separate

Tax rules vary by jurisdiction and client arrangement.

Phase 18 can include:

```text
taxExcluded
taxNote
taxDisplayMode
```

But should not implement tax accounting unless a tax module exists.

Default:

```text
Quote subtotal and total are tax-exclusive.
```

## 4.4 Quote must snapshot inputs

A quote version must not change when:

```text
finance scenario changes
rate card changes
estimation run changes
project tasks change
currency exchange changes
```

A new quote version must be created for changes.

---

# 5. Classification labels

Every requirement uses one label:

| Label | Meaning |
|---|---|
| `CURRENTLY_IMPLEMENTED` | Current backend already implements it. Agent must verify and test. |
| `PARTIALLY_IMPLEMENTED` | Current backend implements part of it. |
| `MUST_IMPLEMENT_IN_PHASE_18` | Must be implemented or hardened now. |
| `SEED_ONLY_IN_PHASE_18` | Seed events/permissions/templates/config now; full consumer later. |
| `DEFERRED_TO_PHASE_XX` | Do not implement now; later phase must return. |
| `DEFERRED_TO_POST_23_BACKLOG` | Outside core 00–23 roadmap, part of full Work OS expansion. |
| `NOT_IN_SCOPE_FOR_BACKEND_NOW` | Explicitly not backend scope now. |

---

# 6. Phase 18 scope decision

## 6.1 Must implement now

```text
Quote
QuoteVersion
QuoteLine
QuoteSummary
QuoteTerm / assumption foundation
Quote lifecycle
Quote version immutability
Generate quote from ProjectFinanceScenario
Target margin solver
Manual adjustment / discount rules
Simple submit/approve/reject actions
Mark current quote/version
Quote permissions
Quote events
Audit/activity/outbox
Tests
Completion report
```

## 6.2 Optional now

```text
Client snapshot fields
Proposal document metadata
Quote export job placeholder
Quote comparison
Multi-currency quote grouping
Quote expiration date
Client acceptance marker
```

Implement only if product requires.

## 6.3 Must not implement now

```text
Signed contract
Contract lifecycle
Invoice/billing
Revenue recognition
Payment collection
Tax accounting
Legal approval workflow
Client portal acceptance
External CRM full model
Proposal PDF generation with document repository
Actual cost
Timesheet actuals
AI-generated commercial offer
```

---

# 7. Core formulas

## 7.1 Cost base

Recommended Phase 18 cost base:

```text
CostBase =
ProjectFinanceSummary.budgetOfCosts
```

Where Phase 17 default:

```text
budgetOfCosts = DirectCost + Overhead
```

Alternative:

```text
CostBase = DirectCost
```

Completion file must document chosen cost base.

Recommended:

```text
Use budgetOfCosts so target margin covers overhead.
```

## 7.2 Required contract value from target margin

```text
RequiredContractValue =
CostBase / (1 - TargetMarginPercent)
```

Where:

```text
TargetMarginPercent = decimal, e.g. 30% = 0.30
```

Example:

```text
CostBase = 700,000,000
TargetMargin = 30%

RequiredContractValue = 700,000,000 / (1 - 0.30)
                      = 1,000,000,000
```

Rules:

```text
1. TargetMarginPercent must be < 100%.
2. TargetMarginPercent can be 0.
3. Negative target margin allowed only if product explicitly supports loss-leading quotes.
4. Use BigDecimal.
```

## 7.3 Markup from cost

If markup model used:

```text
QuotedAmount =
CostBase × (1 + MarkupPercent)
```

Different from margin.

Important distinction:

```text
Margin% = (Revenue - Cost) / Revenue
Markup% = (Revenue - Cost) / Cost
```

## 7.4 Quote margin

```text
QuoteGrossMargin =
QuoteSubtotal - DirectCost
```

```text
QuoteGrossMarginPercent =
QuoteGrossMargin / QuoteSubtotal
```

## 7.5 Quote PBT preview

```text
QuoteProfitBeforeTax =
QuoteSubtotal - DirectCost - Overhead
```

```text
QuotePbtPercent =
QuoteProfitBeforeTax / QuoteSubtotal
```

Tax excluded.

## 7.6 Discount

```text
DiscountAmount =
fixed amount
or SubtotalBeforeDiscount × DiscountPercent
```

```text
QuoteSubtotal =
SubtotalBeforeDiscount - DiscountAmount
```

Rules:

```text
DiscountAmount >= 0.
QuoteSubtotal >= 0.
Discount requires permission if above threshold.
```

## 7.7 Tax placeholder

Default Phase 18:

```text
TaxAmount = null
TaxMode = TAX_EXCLUDED
```

Do not calculate tax unless tax module exists.

---

# 8. TO-BE capability matrix

---

## 8.1 QTE-001 — Quote

| Item | Value |
|---|---|
| Future capability | Commercial proposal shell under project |
| Current state | Missing/unknown |
| Phase 18 target | Implement |
| Classification | `MUST_IMPLEMENT_IN_PHASE_18` |

Rules:

```text
1. Quote belongs to one project.
2. Project must not be ARCHIVED.
3. Quote code unique within project or workspace.
4. Quote name/title required.
5. Quote references source ProjectFinanceScenario.
6. FinanceScenario must belong to same project.
7. FinanceScenario should be APPROVED/CURRENT before official quote.
8. Quote starts DRAFT.
9. Quote can have multiple versions.
10. Quote is not contract.
```

---

## 8.2 QTE-002 — QuoteVersion

| Item | Value |
|---|---|
| Future capability | Immutable versioned commercial quote snapshot |
| Current state | Missing/unknown |
| Phase 18 target | Implement |
| Classification | `MUST_IMPLEMENT_IN_PHASE_18` |

Status:

```text
DRAFT
SUBMITTED
APPROVED
REJECTED
SENT
ACCEPTED
ARCHIVED
```

Rules:

```text
1. Version number unique within Quote.
2. New version starts DRAFT.
3. DRAFT can be edited.
4. SUBMITTED/APPROVED/SENT/ACCEPTED versions immutable except lifecycle.
5. Revisions create new DRAFT version.
6. Version snapshots finance inputs.
7. Only one current version per quote.
8. Approved/sent version cannot be silently changed.
9. QuoteVersion does not create invoice/contract.
```

---

## 8.3 QTE-003 — Generate quote from finance scenario

| Item | Value |
|---|---|
| Future capability | Convert planned finance scenario into quote version |
| Current state | Missing/unknown |
| Phase 18 target | Implement |
| Classification | `MUST_IMPLEMENT_IN_PHASE_18` |

Rules:

```text
1. FinanceScenario must belong to project.
2. FinanceScenario should be APPROVED or CURRENT.
3. Generate copies direct cost, overhead, planned revenue, margin, PBT.
4. Generate creates quote lines from phase finance or summary.
5. Generate stores sourceFinanceScenarioId.
6. Generate stores formula version.
7. Later finance changes do not affect quote version.
```

If allowing DRAFT finance scenario:

```text
Quote must be marked INTERNAL_DRAFT and cannot be sent/approved until finance scenario approved.
```

Recommended:

```text
Require APPROVED/CURRENT finance scenario for official quote generation.
```

---

## 8.4 QTE-004 — QuoteLine

| Item | Value |
|---|---|
| Future capability | Client-facing or internal quote line item |
| Current state | Missing/unknown |
| Phase 18 target | Implement |
| Classification | `MUST_IMPLEMENT_IN_PHASE_18` |

Line types:

```text
PHASE
SERVICE
DELIVERABLE
CUSTOM
DISCOUNT
OPTIONAL
```

Rules:

```text
1. QuoteVersion must be DRAFT to edit lines.
2. Line name required.
3. Quantity > 0 unless discount line.
4. Unit price >= 0.
5. Amount = quantity × unit price.
6. Discount line amount <= 0 or stored separately according to model.
7. Line can reference ProjectPhaseFinance.
8. Client-facing visibility flag supported.
9. Internal cost fields require finance/quote margin permission.
```

Recommended:

```text
Use positive line amounts and separate discount model to avoid confusion.
```

---

## 8.5 QTE-005 — Quote summary

| Item | Value |
|---|---|
| Future capability | Store quote-level commercial totals and margin preview |
| Current state | Missing/unknown |
| Phase 18 target | Implement |
| Classification | `MUST_IMPLEMENT_IN_PHASE_18` |

Summary fields:

```text
subtotalBeforeDiscount
discountAmount
subtotalAfterDiscount
taxMode
taxAmount nullable
totalQuotedAmount
costBase
directCost
overhead
grossMargin
grossMarginPercent
profitBeforeTax
pbtPercent
targetMarginPercent
requiredContractValue
currencyCode
```

Rules:

```text
1. Summary recalculated from lines and finance snapshot.
2. Use BigDecimal.
3. No floating point.
4. Tax excluded unless explicit tax module.
5. Summary is quote preview, not invoice.
```

---

## 8.6 QTE-006 — Target margin solver

| Item | Value |
|---|---|
| Future capability | Calculate quote amount required to hit target margin |
| Current state | Missing/unknown |
| Phase 18 target | Implement |
| Classification | `MUST_IMPLEMENT_IN_PHASE_18` |

Inputs:

```text
costBase
targetMarginPercent
currencyCode
roundingPolicy
```

Output:

```text
requiredContractValue
grossMargin
grossMarginPercent
pbtPreview
```

Rules:

```text
1. costBase >= 0.
2. targetMarginPercent < 100.
3. Use BigDecimal.
4. Result can populate QuoteVersion planned price if user chooses.
5. Solver does not approve quote automatically.
```

---

## 8.7 QTE-007 — Discount / commercial adjustment

| Item | Value |
|---|---|
| Future capability | Apply commercial discount/adjustment |
| Current state | Missing/unknown |
| Phase 18 target | Implement basic discount |
| Classification | `MUST_IMPLEMENT_IN_PHASE_18` |

Discount methods:

```text
NONE
FIXED_AMOUNT
PERCENT_OF_SUBTOTAL
```

Rules:

```text
1. Discount >= 0.
2. Discount cannot make subtotal negative.
3. Discount above threshold requires QUOTE_DISCOUNT_APPROVE or approval.
4. Discount reason required if discount > 0.
5. Discount audited.
```

Threshold example:

```text
discountPercent > 10% requires approval
```

If no approval threshold needed:

```text
Document as not configured.
```

---

## 8.8 QTE-008 — Quote lifecycle

| Item | Value |
|---|---|
| Future capability | Manage quote draft, review, approval, send, accept, archive |
| Current state | Missing/unknown |
| Phase 18 target | Implement simple lifecycle |
| Classification | `MUST_IMPLEMENT_IN_PHASE_18` |

Recommended lifecycle:

```text
DRAFT → SUBMITTED → APPROVED → SENT
DRAFT → ARCHIVED
SUBMITTED → REJECTED
REJECTED → DRAFT revision or new version
SENT → ACCEPTED optional
SENT → REJECTED/EXPIRED optional
```

Rules:

```text
1. DRAFT can be edited.
2. SUBMITTED immutable until rejected/approved.
3. APPROVED immutable.
4. SENT immutable.
5. ACCEPTED optional marker only, not contract.
6. Full workflow engine deferred.
7. Lifecycle actions audited.
```

---

## 8.9 QTE-009 — Quote approval

| Item | Value |
|---|---|
| Future capability | Internal approval before sending commercial quote |
| Current state | Missing/unknown |
| Phase 18 target | Implement simple approve/reject action |
| Classification | `MUST_IMPLEMENT_IN_PHASE_18` simple; full workflow deferred |

Rules:

```text
1. Submit requires valid quote summary.
2. Approve requires QUOTE_APPROVE.
3. Reject requires reason.
4. Approved quote version immutable.
5. Approval is not contract signing.
6. Segregation of duties can be deferred unless Phase 02 SoD already supports it.
```

Full workflow:

```text
DEFERRED_TO_PHASE_34_WORKFLOW_APPROVAL
```

---

## 8.10 QTE-010 — Client snapshot

| Item | Value |
|---|---|
| Future capability | Link quote to client/external party |
| Current state | External Party / Client CRM likely deferred |
| Phase 18 target | Minimal client snapshot optional |
| Classification | `MUST_IMPLEMENT_IN_PHASE_18` minimal snapshot if no CRM; full CRM deferred |

Minimal fields:

```text
clientName
clientEmail
clientCompany
clientContactName
clientReference
```

Rules:

```text
1. Client snapshot can be free text in Phase 18.
2. No full CRM/contact lifecycle in Phase 18.
3. ExternalParty reference added later when CRM exists.
4. Snapshot preserved in quote version.
```

Full Client CRM:

```text
DEFERRED_TO_POST_23_EXTERNAL_PARTY_CLIENT_CRM_BACKLOG
```

---

## 8.11 QTE-011 — Commercial terms

| Item | Value |
|---|---|
| Future capability | Store quote assumptions, validity, payment terms, exclusions |
| Current state | Missing/unknown |
| Phase 18 target | Implement foundation |
| Classification | `MUST_IMPLEMENT_IN_PHASE_18` |

Term types:

```text
PAYMENT_TERM
VALIDITY
ASSUMPTION
EXCLUSION
DELIVERY_TERM
WARRANTY
SUPPORT_TERM
LEGAL_NOTE
TAX_NOTE
```

Rules:

```text
1. QuoteVersion must be DRAFT to edit terms.
2. Term type required.
3. Content required.
4. Terms snapshot with version.
5. Legal contract clauses deferred to contract module.
```

---

## 8.12 QTE-012 — Proposal document/export

| Item | Value |
|---|---|
| Future capability | Generate client-facing proposal package |
| Current state | Document Hub full generation deferred |
| Phase 18 target | Metadata/placeholder only or defer |
| Classification | `DEFERRED_TO_PHASE_27_DOCUMENT_HUB` and `PHASE_22_REPORTING_EXPORT` |

Phase 18 can store:

```text
proposalTitle
proposalTemplateCode
proposalNotes
```

Do not claim PDF/DOCX export unless implemented.

---

## 8.13 QTE-013 — Convert quote to contract

| Item | Value |
|---|---|
| Future capability | Accepted quote becomes contract |
| Current state | Missing |
| Phase 18 target | Defer |
| Classification | `DEFERRED_TO_PHASE_36_CONTRACT_BILLING_REVENUE` |

Rule:

```text
Quote accepted marker does not create contract/invoice.
```

---

## 8.14 QTE-014 — Quote tax

| Item | Value |
|---|---|
| Future capability | Apply tax/VAT/GST rules |
| Current state | Missing |
| Phase 18 target | Defer; tax note only |
| Classification | `DEFERRED_TO_TAX_FINANCE_MODULE` |

Rules:

```text
1. Tax excluded by default.
2. Tax amount null unless tax module exists.
3. Tax note can be stored.
4. Do not include tax in margin unless formula explicitly supports tax module.
```

---

# 9. Entity model TO-BE

If current schema differs, agent must map actual fields and document gaps.

---

## 9.1 Quote — `quote_quote`

Required fields:

```text
id UUID PK
project_id UUID NOT NULL
workspace_id UUID NOT NULL
source_finance_scenario_id UUID NOT NULL
code VARCHAR(100) NOT NULL
title VARCHAR(255) NOT NULL
description TEXT NULL

client_name VARCHAR(255) NULL
client_company VARCHAR(255) NULL
client_email VARCHAR(255) NULL
client_contact_name VARCHAR(255) NULL
client_reference VARCHAR(255) NULL
external_party_id UUID NULL future

status VARCHAR(50) NOT NULL
current_version_id UUID NULL
created_at / created_by
updated_at / updated_by
archived_at / archived_by
version INT
```

Status:

```text
DRAFT
ACTIVE
ARCHIVED
```

Constraint:

```text
unique project_id + code
```

---

## 9.2 QuoteVersion — `quote_version`

Required fields:

```text
id UUID PK
quote_id UUID NOT NULL
project_id UUID NOT NULL
finance_scenario_id UUID NOT NULL
version_number INT NOT NULL
status VARCHAR(50) NOT NULL

title_snapshot VARCHAR(255) NOT NULL
client_snapshot_json JSONB NULL
finance_snapshot_json JSONB NOT NULL
formula_version VARCHAR(50) NOT NULL
currency_code VARCHAR(10) NOT NULL

target_margin_percent DECIMAL(8,4) NULL
pricing_method VARCHAR(50) NOT NULL
cost_base_method VARCHAR(50) NOT NULL

submitted_at TIMESTAMP NULL
submitted_by UUID NULL
approved_at TIMESTAMP NULL
approved_by UUID NULL
rejected_at TIMESTAMP NULL
rejected_by UUID NULL
rejection_reason TEXT NULL
sent_at TIMESTAMP NULL
sent_by UUID NULL
accepted_at TIMESTAMP NULL
accepted_by UUID NULL
archived_at TIMESTAMP NULL
archived_by UUID NULL

valid_until DATE NULL
created_at / created_by
updated_at / updated_by
version INT
```

Status:

```text
DRAFT
SUBMITTED
APPROVED
REJECTED
SENT
ACCEPTED
ARCHIVED
```

Pricing method:

```text
FROM_FINANCE_PLANNED_REVENUE
TARGET_MARGIN_SOLVER
MANUAL_TOTAL
PHASE_LINE_SUM
```

Cost base method:

```text
BUDGET_OF_COSTS
DIRECT_COST
CUSTOM
```

Constraint:

```text
unique quote_id + version_number
```

---

## 9.3 QuoteLine — `quote_line`

Required fields:

```text
id UUID PK
quote_version_id UUID NOT NULL
project_id UUID NOT NULL
source_phase_finance_id UUID NULL
source_project_phase_id UUID NULL
line_type VARCHAR(50) NOT NULL
name VARCHAR(255) NOT NULL
description TEXT NULL
quantity DECIMAL(18,4) NOT NULL DEFAULT 1
unit_price DECIMAL(18,4) NOT NULL DEFAULT 0
amount DECIMAL(18,4) NOT NULL DEFAULT 0
currency_code VARCHAR(10) NOT NULL
display_order INT NOT NULL DEFAULT 0
client_visible BOOLEAN NOT NULL DEFAULT true
internal_note TEXT NULL
created_at / created_by
updated_at / updated_by
version INT
```

Line types:

```text
PHASE
SERVICE
DELIVERABLE
CUSTOM
OPTIONAL
```

Discount should be handled at version/summary level unless product wants discount line.

---

## 9.4 QuoteSummary — `quote_summary`

Required fields:

```text
id UUID PK
quote_version_id UUID NOT NULL UNIQUE
project_id UUID NOT NULL
currency_code VARCHAR(10) NOT NULL

cost_base DECIMAL(18,4) NOT NULL DEFAULT 0
direct_cost DECIMAL(18,4) NOT NULL DEFAULT 0
overhead DECIMAL(18,4) NOT NULL DEFAULT 0

subtotal_before_discount DECIMAL(18,4) NOT NULL DEFAULT 0
discount_method VARCHAR(50) NOT NULL DEFAULT 'NONE'
discount_percent DECIMAL(8,4) NULL
discount_amount DECIMAL(18,4) NOT NULL DEFAULT 0
subtotal_after_discount DECIMAL(18,4) NOT NULL DEFAULT 0

tax_mode VARCHAR(50) NOT NULL DEFAULT 'TAX_EXCLUDED'
tax_amount DECIMAL(18,4) NULL
total_quoted_amount DECIMAL(18,4) NOT NULL DEFAULT 0

target_margin_percent DECIMAL(8,4) NULL
required_contract_value DECIMAL(18,4) NULL

gross_margin DECIMAL(18,4) NULL
gross_margin_percent DECIMAL(8,4) NULL
profit_before_tax DECIMAL(18,4) NULL
pbt_percent DECIMAL(8,4) NULL

created_at TIMESTAMP NOT NULL
updated_at TIMESTAMP NOT NULL
```

Tax modes:

```text
TAX_EXCLUDED
TAX_INCLUDED_DISPLAY_ONLY
TAX_NOT_APPLICABLE
```

Recommended Phase 18:

```text
TAX_EXCLUDED only.
```

---

## 9.5 QuoteTerm — `quote_term`

Required fields:

```text
id UUID PK
quote_version_id UUID NOT NULL
term_type VARCHAR(50) NOT NULL
title VARCHAR(255) NULL
content TEXT NOT NULL
display_order INT NOT NULL DEFAULT 0
client_visible BOOLEAN NOT NULL DEFAULT true
created_at / created_by
updated_at / updated_by
version INT
```

Term types:

```text
PAYMENT_TERM
VALIDITY
ASSUMPTION
EXCLUSION
DELIVERY_TERM
WARRANTY
SUPPORT_TERM
LEGAL_NOTE
TAX_NOTE
```

---

## 9.6 QuoteApprovalAction — optional table

If simple lifecycle fields on QuoteVersion are not enough:

```text
quote_approval_action
```

Fields:

```text
id UUID PK
quote_version_id UUID NOT NULL
action VARCHAR(50) NOT NULL
actor_user_id UUID NOT NULL
reason TEXT NULL
created_at TIMESTAMP NOT NULL
trace_id VARCHAR(100) NULL
```

Actions:

```text
SUBMIT
APPROVE
REJECT
SEND
MARK_ACCEPTED
ARCHIVE
```

Recommended:

```text
Implement if audit trail needs detailed approval history.
```

---

# 10. API TO-BE list

All APIs use `/api`.

---

## 10.1 Quote APIs

```text
POST  /api/projects/{projectId}/quotes
GET   /api/projects/{projectId}/quotes
GET   /api/projects/{projectId}/quotes/{quoteId}
PUT   /api/projects/{projectId}/quotes/{quoteId}
PATCH /api/projects/{projectId}/quotes/{quoteId}/archive
```

Create request:

```json
{
  "code": "Q-CLIENT-PORTAL-001",
  "title": "Client Portal Implementation Proposal",
  "sourceFinanceScenarioId": "uuid",
  "clientName": "ABC Company",
  "clientEmail": "client@example.com"
}
```

---

## 10.2 Quote version APIs

```text
POST  /api/projects/{projectId}/quotes/{quoteId}/versions
GET   /api/projects/{projectId}/quotes/{quoteId}/versions
GET   /api/projects/{projectId}/quotes/{quoteId}/versions/{versionId}
PUT   /api/projects/{projectId}/quotes/{quoteId}/versions/{versionId}
POST  /api/projects/{projectId}/quotes/{quoteId}/versions/{versionId}/duplicate
PATCH /api/projects/{projectId}/quotes/{quoteId}/versions/{versionId}/archive
```

Create version request:

```json
{
  "financeScenarioId": "uuid",
  "pricingMethod": "TARGET_MARGIN_SOLVER",
  "costBaseMethod": "BUDGET_OF_COSTS",
  "targetMarginPercent": 30.0,
  "generateLinesFrom": "PHASE_FINANCE",
  "validUntil": "2026-09-30"
}
```

---

## 10.3 Quote line APIs

```text
POST   /api/projects/{projectId}/quotes/{quoteId}/versions/{versionId}/lines
GET    /api/projects/{projectId}/quotes/{quoteId}/versions/{versionId}/lines
GET    /api/projects/{projectId}/quotes/{quoteId}/versions/{versionId}/lines/{lineId}
PUT    /api/projects/{projectId}/quotes/{quoteId}/versions/{versionId}/lines/{lineId}
DELETE /api/projects/{projectId}/quotes/{quoteId}/versions/{versionId}/lines/{lineId}
PUT    /api/projects/{projectId}/quotes/{quoteId}/versions/{versionId}/lines/reorder
```

Rules:

```text
Only DRAFT version can mutate lines.
```

---

## 10.4 Quote terms APIs

```text
POST   /api/projects/{projectId}/quotes/{quoteId}/versions/{versionId}/terms
GET    /api/projects/{projectId}/quotes/{quoteId}/versions/{versionId}/terms
PUT    /api/projects/{projectId}/quotes/{quoteId}/versions/{versionId}/terms/{termId}
DELETE /api/projects/{projectId}/quotes/{quoteId}/versions/{versionId}/terms/{termId}
PUT    /api/projects/{projectId}/quotes/{quoteId}/versions/{versionId}/terms/reorder
```

---

## 10.5 Quote summary / recalculation APIs

```text
GET  /api/projects/{projectId}/quotes/{quoteId}/versions/{versionId}/summary
POST /api/projects/{projectId}/quotes/{quoteId}/versions/{versionId}/recalculate
POST /api/projects/{projectId}/quotes/{quoteId}/versions/{versionId}/solve-target-margin
```

Solve request:

```json
{
  "costBase": 700000000,
  "targetMarginPercent": 30.0,
  "currencyCode": "VND"
}
```

---

## 10.6 Quote lifecycle APIs

```text
POST /api/projects/{projectId}/quotes/{quoteId}/versions/{versionId}/submit
POST /api/projects/{projectId}/quotes/{quoteId}/versions/{versionId}/approve
POST /api/projects/{projectId}/quotes/{quoteId}/versions/{versionId}/reject
POST /api/projects/{projectId}/quotes/{quoteId}/versions/{versionId}/send
POST /api/projects/{projectId}/quotes/{quoteId}/versions/{versionId}/mark-accepted
POST /api/projects/{projectId}/quotes/{quoteId}/versions/{versionId}/mark-current
```

Reject request:

```json
{
  "reason": "Margin too low"
}
```

Send/accept in Phase 18:

```text
State markers only.
No email/PDF/contract/invoice unless explicitly implemented.
```

---

## 10.7 Quote export/proposal APIs

Deferred:

```text
POST /api/projects/{projectId}/quotes/{quoteId}/versions/{versionId}/export-pdf
POST /api/projects/{projectId}/quotes/{quoteId}/versions/{versionId}/generate-proposal-document
```

Target:

```text
Phase 22 Reporting Export
Phase 27 Document Hub
```

Do not implement fake export.

---

# 11. Authorization requirements

Quote is commercial and sensitive.

Required IAM authorities:

```text
QUOTE_VIEW
QUOTE_CREATE
QUOTE_UPDATE
QUOTE_ARCHIVE

QUOTE_VERSION_VIEW
QUOTE_VERSION_CREATE
QUOTE_VERSION_UPDATE
QUOTE_VERSION_ARCHIVE
QUOTE_VERSION_DUPLICATE

QUOTE_LINE_VIEW
QUOTE_LINE_CREATE
QUOTE_LINE_UPDATE
QUOTE_LINE_DELETE

QUOTE_TERM_VIEW
QUOTE_TERM_CREATE
QUOTE_TERM_UPDATE
QUOTE_TERM_DELETE

QUOTE_SOLVER_USE
QUOTE_RECALCULATE

QUOTE_SUBMIT
QUOTE_APPROVE
QUOTE_REJECT
QUOTE_SEND
QUOTE_MARK_ACCEPTED
QUOTE_MARK_CURRENT

QUOTE_MARGIN_VIEW
QUOTE_DISCOUNT_UPDATE
QUOTE_DISCOUNT_APPROVE

QUOTE_EXPORT future
```

Rules:

```text
1. Normal PROJECT_VIEW does not automatically grant quote view.
2. Quote summary with margin requires QUOTE_MARGIN_VIEW or finance permission.
3. Create quote requires access to source ProjectFinanceScenario.
4. Approve quote requires QUOTE_APPROVE.
5. Discount above threshold requires stronger permission.
6. Send/mark accepted can be internal state only.
7. Quote APIs must not expose finance internals to unauthorized users.
```

Simplified mapping allowed:

```text
QUOTE_VIEW includes line/summary view.
QUOTE_UPDATE includes draft line/term update.
```

Completion file must document simplifications.

---

# 12. Quote generation algorithm

## 12.1 Create quote

```text
1. Validate project and authorization.
2. Validate source finance scenario belongs to project.
3. Validate source finance scenario status APPROVED/CURRENT for official quote.
4. Create Quote shell.
5. Optionally create initial QuoteVersion.
6. Emit QUOTE_CREATED.
```

## 12.2 Create quote version from finance scenario

```text
1. Validate quote/project/path.
2. Validate finance scenario.
3. Copy finance summary into finance_snapshot_json.
4. Determine cost base.
5. Determine pricing method.
6. If TARGET_MARGIN_SOLVER:
   calculate required contract value.
7. Generate quote lines:
   a. From phase finance, or
   b. From project summary, or
   c. Manual blank version.
8. Calculate quote summary.
9. Store QuoteVersion DRAFT.
10. Emit QUOTE_VERSION_CREATED.
```

## 12.3 Generate lines from phase finance

For each ProjectPhaseFinance:

```text
QuoteLine:
- type = PHASE
- name = phase name snapshot
- quantity = 1
- unitPrice = phase planned revenue or allocated quote amount
- amount = unitPrice
- clientVisible = true
```

If using target margin solver:

```text
Distribute required contract value across phases by:
- finance revenue split,
- direct cost proportion,
- manual setting,
- or equal split.
```

Recommended:

```text
Use finance revenue split if present.
Fallback to direct cost proportion.
```

## 12.4 Recalculate quote version

Only DRAFT can recalculate.

```text
1. Recalculate line totals.
2. Apply discount.
3. Recalculate quote summary.
4. Recalculate margin preview against frozen finance snapshot.
5. Do not re-read current finance scenario unless explicit refresh action exists.
```

If finance scenario changed:

```text
Create new quote version from new finance snapshot.
```

---

# 13. Lifecycle validation rules

## 13.1 Submit

Before submit:

```text
1. QuoteVersion status DRAFT.
2. At least one line.
3. Summary totalQuotedAmount > 0.
4. Required terms present if configured.
5. Valid until date not in past if provided.
6. Discount reason exists if discount > 0.
7. Margin threshold validation passes or warnings acknowledged.
```

## 13.2 Approve

Before approve:

```text
1. Status SUBMITTED.
2. User has QUOTE_APPROVE.
3. If discount threshold exceeded, user has discount approval or approval policy satisfied.
4. No blocking validation errors.
5. Approval audited.
```

## 13.3 Send

Before send:

```text
1. Status APPROVED.
2. User has QUOTE_SEND.
3. Send marker does not send email unless Notification integration implemented.
4. Sent version immutable.
```

## 13.4 Mark accepted

Before accepted:

```text
1. Status SENT or APPROVED depending on product.
2. User has QUOTE_MARK_ACCEPTED.
3. Accepted marker does not create contract.
4. Contract conversion deferred.
```

---

# 14. Margin threshold policy

Optional but recommended.

Fields/config:

```text
minimumGrossMarginPercent
minimumPbtPercent
discountApprovalThresholdPercent
```

Rules:

```text
1. If margin below threshold, create warning.
2. Approval can be blocked or require stronger permission.
3. Threshold policy stored in quote assumptions.
4. Do not silently approve low-margin quote.
```

If not implemented:

```text
Document as deferred to Phase 22/34.
```

---

# 15. Currency policy

Recommended Phase 18:

```text
SINGLE_CURRENCY_REQUIRED
```

Rules:

```text
1. Quote currency required.
2. Source finance scenario currency must match quote currency.
3. Quote lines must match quote currency.
4. Do not convert currency without exchange-rate snapshot.
5. Multi-currency quote deferred.
```

Currency conversion:

```text
DEFERRED_TO_PHASE_36_BILLING_REVENUE or dedicated finance currency module.
```

---

# 16. Tax policy

Phase 18 default:

```text
TAX_EXCLUDED
```

Rules:

```text
1. Tax amount null by default.
2. Tax note may be stored as QuoteTerm.
3. Quote total is tax-exclusive unless product explicitly configures display-only tax.
4. Tax does not affect margin in Phase 18.
```

Do not calculate VAT/GST unless tax module exists.

---

# 17. Integration with Project Finance

Phase 18 consumes:

```text
ProjectFinanceScenario
ProjectFinanceSummary
ProjectPhaseFinance
```

Rules:

```text
1. Quote stores finance snapshot.
2. Quote does not mutate finance scenario.
3. Quote version remains stable if finance scenario changes later.
4. New finance scenario should create new quote version.
```

---

# 18. Integration with Baseline / Change Request

Phase 19 may snapshot:

```text
approved quote version
finance scenario
estimate run
project scope
schedule
```

After baseline:

```text
quote changes may require change request.
```

Phase 18 does not implement baseline/change request.

---

# 19. Integration with Contract / Billing

Future Phase 36 will convert accepted quote to:

```text
contract
billing schedule
invoice
revenue recognition
payment tracking
```

Phase 18 only stores:

```text
quote accepted marker optional
```

No contract/invoice created.

---

# 20. Integration with Document Hub / Export

Future proposal document generation may use:

```text
QuoteVersion
QuoteLines
QuoteTerms
Project scope
Gantt/schedule summary
Finance summary
```

Deferred to:

```text
Phase 22 Reporting Export
Phase 27 Document Hub
```

Phase 18 may store:

```text
proposalTemplateCode
proposalNotes
```

But must not claim PDF/DOCX export.

---

# 21. Integration with External Party / Client CRM

Full client CRM is deferred.

Phase 18 may store client snapshot:

```text
clientName
clientCompany
clientEmail
clientContactName
```

Future CRM integration:

```text
ExternalParty
ClientAccount
ClientContact
ClientPortalShare
```

Deferred to:

```text
POST_23_EXTERNAL_PARTY_CLIENT_CRM_BACKLOG
```

---

# 22. Integration with AI Agent

Phase 18 does not implement AI-generated quote.

Future AI may:

```text
draft proposal wording
explain margin
suggest discount risk
suggest commercial terms
summarize scope
```

Rules future:

```text
1. AI must enforce quote/finance permissions.
2. AI output is draft/proposal.
3. Human must approve quote text and pricing.
4. AI cannot auto-send quote.
```

Deferred to Phase 21/27.

---

# 23. Event Registry integration

Recommended source system:

```text
SCOPERY_QUOTE
```

Required events:

```text
QUOTE_CREATED
QUOTE_UPDATED
QUOTE_ARCHIVED

QUOTE_VERSION_CREATED
QUOTE_VERSION_UPDATED
QUOTE_VERSION_DUPLICATED
QUOTE_VERSION_ARCHIVED
QUOTE_VERSION_MARKED_CURRENT

QUOTE_LINE_CREATED
QUOTE_LINE_UPDATED
QUOTE_LINE_DELETED
QUOTE_LINES_REORDERED

QUOTE_TERM_CREATED
QUOTE_TERM_UPDATED
QUOTE_TERM_DELETED
QUOTE_TERMS_REORDERED

QUOTE_TARGET_MARGIN_SOLVED
QUOTE_RECALCULATED
QUOTE_DISCOUNT_UPDATED
QUOTE_MARGIN_THRESHOLD_WARNING

QUOTE_SUBMITTED
QUOTE_APPROVED
QUOTE_REJECTED
QUOTE_SENT
QUOTE_ACCEPTED
```

Standard variables:

```text
actor.userId
organization.id
workspace.id
project.id
quote.id
quote.code
quoteVersion.id
quoteVersion.versionNumber
financeScenario.id
currency.code
costBase
targetMarginPercent
requiredContractValue
totalQuotedAmount
grossMargin
grossMarginPercent
profitBeforeTax
pbtPercent
occurredAt
traceId
```

---

# 24. Notification integration

Phase 18 can seed events but should not hardcode broad notifications.

Potential notifications:

```text
QUOTE_SUBMITTED_APPROVER_NOTIFICATION
QUOTE_APPROVED_NOTIFICATION
QUOTE_REJECTED_NOTIFICATION
QUOTE_SENT_NOTIFICATION
QUOTE_ACCEPTED_NOTIFICATION
QUOTE_MARGIN_THRESHOLD_WARNING_NOTIFICATION
```

Recommended:

```text
Seed events now.
Notification rules can be configured later in Phase 20 or workflow phase.
```

Do not send client email unless email template/rule is explicitly implemented and approved.

---

# 25. Platform audit/outbox/idempotency integration

## 25.1 Activity log actions

```text
QUOTE_CREATED
QUOTE_VERSION_CREATED
QUOTE_RECALCULATED
QUOTE_SUBMITTED
QUOTE_APPROVED
QUOTE_REJECTED
QUOTE_SENT
QUOTE_ACCEPTED
QUOTE_DISCOUNT_UPDATED
QUOTE_TARGET_MARGIN_SOLVED
```

## 25.2 Audit-sensitive actions

Audit:

```text
Quote version created from finance scenario
Target margin solved
Manual quote price changed
Discount changed
Quote submitted
Quote approved
Quote rejected
Quote sent
Quote accepted
Quote marked current
Quote archived
```

Reason:

```text
Quote actions affect commercial commitment.
```

## 25.3 Idempotency

Recommended for:

```text
POST /api/projects/{projectId}/quotes
POST /api/projects/{projectId}/quotes/{quoteId}/versions
POST /api/projects/{projectId}/quotes/{quoteId}/versions/{versionId}/duplicate
POST /api/projects/{projectId}/quotes/{quoteId}/versions/{versionId}/recalculate
POST /api/projects/{projectId}/quotes/{quoteId}/versions/{versionId}/submit
POST /api/projects/{projectId}/quotes/{quoteId}/versions/{versionId}/approve
POST /api/projects/{projectId}/quotes/{quoteId}/versions/{versionId}/send
```

## 25.4 Outbox

Quote events should use platform outbox if available.

---

# 26. Business rules master

## 26.1 Quote rules

```text
QTE-QUOTE-001 Project must exist.
QTE-QUOTE-002 Project must not be ARCHIVED.
QTE-QUOTE-003 Source finance scenario must belong to project.
QTE-QUOTE-004 Quote code unique within project.
QTE-QUOTE-005 Quote title required.
QTE-QUOTE-006 Quote is not contract.
QTE-QUOTE-007 Quote is not invoice.
```

## 26.2 QuoteVersion rules

```text
QTE-VER-001 Version number unique within quote.
QTE-VER-002 New version starts DRAFT.
QTE-VER-003 DRAFT can be edited.
QTE-VER-004 SUBMITTED/APPROVED/SENT/ACCEPTED immutable.
QTE-VER-005 New revision creates new DRAFT version.
QTE-VER-006 Version snapshots finance inputs.
QTE-VER-007 Only one current version per quote.
QTE-VER-008 Approved/sent quote does not change after finance/rate changes.
```

## 26.3 QuoteLine rules

```text
QTE-LINE-001 QuoteVersion must be DRAFT to edit lines.
QTE-LINE-002 Line name required.
QTE-LINE-003 Quantity > 0.
QTE-LINE-004 Unit price >= 0.
QTE-LINE-005 Amount = quantity × unit price.
QTE-LINE-006 Currency matches quote version.
QTE-LINE-007 Internal cost fields require margin permission.
```

## 26.4 Solver rules

```text
QTE-SOLVER-001 CostBase >= 0.
QTE-SOLVER-002 TargetMarginPercent < 100.
QTE-SOLVER-003 Use BigDecimal.
QTE-SOLVER-004 RequiredContractValue = CostBase / (1 - TargetMargin).
QTE-SOLVER-005 Solver does not approve quote.
```

## 26.5 Discount rules

```text
QTE-DISC-001 Discount >= 0.
QTE-DISC-002 Discount cannot make subtotal negative.
QTE-DISC-003 Discount reason required when discount > 0.
QTE-DISC-004 Discount above threshold requires stronger permission or approval.
QTE-DISC-005 Discount audited.
```

## 26.6 Lifecycle rules

```text
QTE-LIFE-001 Submit requires valid quote summary.
QTE-LIFE-002 Submit requires at least one quote line.
QTE-LIFE-003 Approve requires QUOTE_APPROVE.
QTE-LIFE-004 Reject requires reason.
QTE-LIFE-005 Send requires approved quote.
QTE-LIFE-006 Accepted marker does not create contract.
QTE-LIFE-007 Lifecycle actions audited.
```

## 26.7 Tax/currency rules

```text
QTE-CUR-001 Quote currency required.
QTE-CUR-002 Quote line currency must match quote version.
QTE-CUR-003 Finance scenario currency must match quote currency unless conversion supported.
QTE-TAX-001 Tax excluded by default.
QTE-TAX-002 Tax amount not calculated without tax module.
QTE-TAX-003 Tax does not affect margin in Phase 18.
```

---

# 27. Error catalog requirements

Exact names follow project convention, but these concepts must exist.

```text
QUOTE_NOT_FOUND
QUOTE_CODE_ALREADY_EXISTS
QUOTE_PROJECT_ARCHIVED
QUOTE_SOURCE_FINANCE_NOT_FOUND
QUOTE_SOURCE_FINANCE_PROJECT_MISMATCH
QUOTE_SOURCE_FINANCE_NOT_APPROVED
QUOTE_ACCESS_DENIED

QUOTE_VERSION_NOT_FOUND
QUOTE_VERSION_NOT_DRAFT
QUOTE_VERSION_IMMUTABLE
QUOTE_VERSION_INVALID_STATUS
QUOTE_VERSION_NO_LINES
QUOTE_VERSION_SUMMARY_INVALID
QUOTE_VERSION_CURRENT_CONFLICT
QUOTE_VERSION_FINANCE_SNAPSHOT_INVALID

QUOTE_LINE_NOT_FOUND
QUOTE_LINE_INVALID_QUANTITY
QUOTE_LINE_INVALID_UNIT_PRICE
QUOTE_LINE_CURRENCY_MISMATCH
QUOTE_LINE_SOURCE_PHASE_MISMATCH

QUOTE_TERM_NOT_FOUND
QUOTE_TERM_CONTENT_REQUIRED

QUOTE_SOLVER_INVALID_COST_BASE
QUOTE_SOLVER_INVALID_TARGET_MARGIN
QUOTE_SOLVER_FAILED

QUOTE_DISCOUNT_INVALID
QUOTE_DISCOUNT_REASON_REQUIRED
QUOTE_DISCOUNT_APPROVAL_REQUIRED

QUOTE_SUBMIT_VALIDATION_FAILED
QUOTE_APPROVAL_PERMISSION_DENIED
QUOTE_REJECTION_REASON_REQUIRED
QUOTE_SEND_NOT_APPROVED
QUOTE_ACCEPTED_NO_CONTRACT_CREATED

QUOTE_CURRENCY_MISMATCH
QUOTE_TAX_NOT_SUPPORTED
QUOTE_MARGIN_ACCESS_DENIED
```

---

# 28. Required tests

Phase 18 is incomplete without tests.

---

## 28.1 Quote tests

```text
createQuote_valid_success
createQuote_archivedProject_rejected
createQuote_financeScenarioNotFound_rejected
createQuote_financeScenarioProjectMismatch_rejected
createQuote_financeScenarioNotApproved_rejectedOrDraftOnlyAccordingToPolicy
createQuote_duplicateCodeInProject_conflict
updateQuote_valid_success
archiveQuote_valid_success
```

## 28.2 QuoteVersion tests

```text
createQuoteVersion_fromFinance_success
createQuoteVersion_copiesFinanceSnapshot
createQuoteVersion_generatesPhaseLines
createQuoteVersion_targetMarginSolver_success
updateDraftVersion_success
updateSubmittedVersion_rejected
updateApprovedVersion_rejected
duplicateApprovedVersion_createsDraftRevision
markCurrent_onlyOneCurrentVersion
quoteVersionUnaffectedByFinanceScenarioChange
quoteVersionUnaffectedByRateChange
```

## 28.3 QuoteLine tests

```text
createLine_valid_success
createLine_onNonDraftVersion_rejected
createLine_invalidQuantity_rejected
createLine_negativeUnitPrice_rejected
createLine_currencyMismatch_rejected
updateLine_recalculatesSummary
deleteLine_recalculatesSummary
reorderLines_valid_success
```

## 28.4 Solver tests

```text
solver_costBaseAndTargetMargin_calculatesRequiredContractValue
solver_targetMarginZero_returnsCostBase
solver_targetMargin100_rejected
solver_targetMarginAbove100_rejected
solver_usesBigDecimal
solver_doesNotApproveQuote
```

## 28.5 Discount tests

```text
discount_fixedAmount_success
discount_percent_success
discount_negative_rejected
discount_exceedsSubtotal_rejected
discount_reasonRequired
discount_aboveThreshold_requiresPermissionOrApproval
discount_recalculatesMargin
```

## 28.6 Lifecycle tests

```text
submitDraft_valid_success
submitWithoutLines_rejected
submitInvalidSummary_rejected
approveSubmitted_valid_success
approveWithoutPermission_forbidden
rejectSubmitted_requiresReason
sendApproved_valid_success
sendDraft_rejected
markAccepted_sent_success
markAccepted_doesNotCreateContract
lifecycleActionsAudited
```

## 28.7 Summary/margin tests

```text
summary_subtotalFromLines
summary_discountApplied
summary_totalQuotedAmountTaxExcluded
summary_grossMarginCalculated
summary_pbtCalculated
summary_zeroSubtotalMarginNull
summary_taxNotCalculated
summary_noInvoiceCreated
```

## 28.8 Authorization tests

```text
viewQuote_withoutPermission_forbidden
createQuote_withoutPermission_forbidden
updateQuote_withoutPermission_forbidden
viewMargin_withoutMarginPermission_hiddenOrForbidden
approveQuote_withoutPermission_forbidden
discountApprove_withoutPermission_forbidden
crossWorkspaceQuoteAccess_forbidden
normalProjectView_doesNotReturnQuoteCommercials
```

## 28.9 Event/seeder tests

```text
quoteEventSeeder_firstRun_createsDefinitions
quoteEventSeeder_secondRun_noDuplicates
quotePermissionSeeder_authoritiesExist
quoteSubmitted_eventEmitted
quoteApproved_eventEmitted
targetMarginSolved_eventEmitted
discountChanged_auditCreated
```

---

# 29. Manual verification checklist

Completion file must include:

```text
1. Create approved/current finance scenario.
2. Create quote from finance scenario.
3. Create quote version using target margin solver.
4. Confirm quote lines generated from phase finance.
5. Confirm required contract value formula.
6. Apply discount and confirm totals/margins recalculate.
7. Add quote terms.
8. Submit quote.
9. Approve quote.
10. Send quote marker.
11. Mark accepted and confirm no contract/invoice created.
12. Change finance scenario/rate later and confirm quote version unchanged.
13. Confirm normal project/Gantt APIs do not expose quote commercial data.
14. Confirm no tax calculation/billing/revenue recognition records created.
15. Confirm events/activity/audit created.
```

---

# 30. Acceptance criteria

Phase 18 is accepted only if:

```text
1. Current quote capability is classified against TO-BE.
2. Quote implemented/tested.
3. QuoteVersion implemented/tested.
4. QuoteLine implemented/tested.
5. Quote summary implemented/tested.
6. Target margin solver implemented/tested.
7. Quote version snapshots finance data and remains immutable after submit/approve/send.
8. Discount/commercial adjustment implemented/tested.
9. Simple lifecycle submit/approve/reject/send/accepted marker implemented/tested or accepted marker explicitly deferred.
10. Quote permissions implemented/tested.
11. Events seeded idempotently.
12. Activity/audit/outbox follows Phase 04.
13. No contract/invoice/billing/tax/accounting/actual cost is falsely claimed.
14. No salary/payroll exposed.
15. mvn compile passes.
16. mvn test passes.
17. Completion file exists and includes gap matrix.
```

Agent must not mark complete if:

```text
mvn test fails
approved/sent quote can be edited
quote version changes when finance scenario changes later
target margin formula wrong
target margin >= 100 accepted
discount can make quote negative
quote accepted creates fake contract/invoice
tax calculated without tax module
normal project view exposes quote/margin
deferred gaps missing
```

---

# 31. Required phase completion file

Agent must create:

```text
docs/phase-complete/PHASE_18_QUOTE_COMMERCIAL_PROPOSAL_TO_BE_COMPLETE.md
```

Required sections:

```text
# Phase 18 — Quote / Commercial Proposal TO-BE Complete

## 1. Summary
## 2. Source Inputs Reviewed
## 3. Current vs TO-BE Classification Matrix
## 4. Implemented in Current BE
## 5. Implemented / Hardened in This Phase
## 6. Seed-only Items Added
## 7. Deferred Items and Target Phase
## 8. Commercial Boundary Decision
## 9. Entity Mapping
## 10. API Changes
## 11. Quote Generation Strategy
## 12. Finance Snapshot Strategy
## 13. Target Margin Solver Formula
## 14. Quote Line Strategy
## 15. Discount / Adjustment Strategy
## 16. Quote Lifecycle Strategy
## 17. Approval Strategy
## 18. Client Snapshot Strategy
## 19. Tax / Currency Policy
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
## 31. Future Phases That Must Return to Quote
```

---

# 32. Future phases that must return to Quote

## 32.1 Phase 19 — Baseline / Change Request

Must snapshot:

```text
approved quote version
finance scenario
scope/WBS
schedule
estimate
```

After baseline, quote changes may require ChangeRequest.

## 32.2 Phase 20 — Project Notifications

May notify:

```text
quote submitted
quote approved
quote rejected
quote sent
quote accepted
margin warning
```

## 32.3 Phase 21 — AI-assisted Planning

AI may draft proposal text or explain pricing.

AI cannot approve/send quote.

## 32.4 Phase 22 — Reporting / Export

Must add:

```text
quote PDF export
proposal package export
quote pipeline report
quote margin report
quote conversion report
```

## 32.5 Phase 23 — Core Hardening

Review:

```text
BigDecimal precision
quote immutability
authorization masking
audit completeness
target margin edge cases
concurrency
```

## 32.6 Phase 27 — Full Document Hub

May generate proposal document from QuoteVersion and terms.

## 32.7 Phase 34 — Workflow Approval

Full approval routing:

```text
submit
multi-level approve
SoD
approval conditions by discount/margin
```

## 32.8 Phase 36 — Contract / Billing / Revenue

Must convert accepted quote to:

```text
contract
billing schedule
invoice
revenue recognition
payment tracking
```

## 32.9 Post-23 External Client CRM / Portal

Must add:

```text
ClientAccount
ClientContact
external quote sharing
client acceptance portal
```

---

# 33. Agent anti-bịa rules

The agent must not:

```text
1. Claim quote is contract.
2. Claim accepted quote creates legal contract unless contract module exists.
3. Claim quote creates invoice.
4. Claim quote creates revenue recognition.
5. Calculate tax without tax module.
6. Let approved/sent quote be edited.
7. Let quote version change after finance/rate changes.
8. Use floating point for money.
9. Accept target margin >= 100%.
10. Hide discount approval rules.
11. Expose margin/finance fields without permission.
12. Send quote email/PDF unless notification/export implemented.
13. Hide deferred contract/billing/tax/client portal/document export gaps.
```

---

# 34. Prompt to give coding agent

```text
You are implementing Phase 18 — TO-BE Quotation, Commercial Proposal, Quote Versioning, Target Margin Solver & Client-facing Price Foundation.

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
- Phase 17 Project Budget / Margin TO-BE spec
- Current backend code, migrations, tests

Your task:
1. Compare current quote/commercial proposal capability against this Phase 18 TO-BE spec.
2. Classify each capability as CURRENTLY_IMPLEMENTED, PARTIALLY_IMPLEMENTED, MUST_IMPLEMENT_IN_PHASE_18, SEED_ONLY_IN_PHASE_18, DEFERRED_TO_PHASE_XX, or NOT_IN_SCOPE_FOR_BACKEND_NOW.
3. Implement only Phase 18 required items.
4. Implement Quote, QuoteVersion, QuoteLine, QuoteSummary, QuoteTerm.
5. Generate quote version from ProjectFinanceScenario and preserve finance snapshot.
6. Implement target margin solver.
7. Implement discount/commercial adjustment rules.
8. Implement simple quote lifecycle submit/approve/reject/send/accepted marker.
9. Add quote permissions and event definitions.
10. Add tests listed in this spec.
11. Run mvn compile and mvn test.
12. Create docs/phase-complete/PHASE_18_QUOTE_COMMERCIAL_PROPOSAL_TO_BE_COMPLETE.md with full gap matrix.

Do not implement or claim contract, invoice/billing, revenue recognition, payment collection, tax accounting, actual cost, timesheet actuals, payroll/salary, client portal, or legal workflow in this phase.
```

---

# 35. Quick tracking matrix

| Capability | Current backend | Phase 18 action | Later phase |
|---|---|---|---|
| Quote | Missing/unknown | Must implement | — |
| QuoteVersion | Missing/unknown | Must implement | — |
| QuoteLine | Missing/unknown | Must implement | — |
| QuoteSummary | Missing/unknown | Must implement | — |
| QuoteTerm | Missing/unknown | Must implement | — |
| Finance snapshot | Missing/unknown | Must implement | — |
| Target margin solver | Missing/unknown | Must implement | — |
| Discount / adjustment | Missing/unknown | Must implement | Approval workflow later |
| Quote lifecycle | Missing/unknown | Must implement simple | Workflow Phase 34 |
| Client snapshot | Missing/unknown | Minimal optional | Client CRM post-23 |
| Proposal export | Missing | Defer | Phase 22/27 |
| Contract conversion | Missing | Defer | Phase 36 |
| Invoice/billing | Missing | Defer | Phase 36 |
| Revenue recognition | Missing | Defer | Phase 36 |
| Tax accounting | Missing | Defer | Tax finance module |
| Client portal acceptance | Missing | Defer | Post-23 external portal |
| AI quote draft | Missing | Defer | Phase 21/27 |

---

# 36. Final principle

Phase 18 is not complete when "a price field can be stored."

Phase 18 is complete when Scopery can create a controlled commercial quote:

```text
Finance scenario snapshot
+ target margin solver
+ quote lines
+ terms
+ discount rules
+ approval lifecycle
= immutable quote version
```

Quote is not contract.

Quote is not invoice.

Quote total is not tax accounting.

Accepted quote is not revenue recognition.

Commercial history must be snapshotted.
