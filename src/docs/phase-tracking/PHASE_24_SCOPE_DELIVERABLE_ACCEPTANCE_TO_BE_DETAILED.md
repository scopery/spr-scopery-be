# PHASE 24 — TO-BE Scope Package, Deliverable Management, Acceptance Criteria, Evidence & Formal Acceptance

> Project: Scopery Backend  
> Phase: 24  
> Document type: TO-BE implementation-grade specification  
> Status: Planning / Gap-driven implementation spec  
> Roadmap group: Post-core Dynamic Work OS expansion  
> Depends on: Phase 00 — Master Backend Roadmap, Phase 01 — API Path / Security Baseline, Phase 02 — IAM Core, Phase 03 — Workspace / Organization / Team, Phase 04 — Platform Audit / Outbox / Idempotency, Phase 05 — Event Registry, Phase 06 — Notification Engine, Phase 08 — Knowledge / Document Type Catalog, Phase 09 — Project Core / Phase / WBS / Task, Phase 10 — Project Authorization, Phase 14 — WBS-driven Gantt, Phase 18 — Quote / Commercial Proposal, Phase 19 — Baseline / Change Request, Phase 20 — Project Events / Notifications, Phase 22 — Reporting / Dashboard / Export, Phase 23 — Core Hardening  
> API base: `/api`  
> Primary module: `modules/scope` or `modules/project/scope` depending on repository architecture  
> Related modules: `project`, `baseline`, `change-request`, `quote`, `document`, `notification`, `reporting`, `iam`, `eventregistry`, future `contract`, `billing`, `client-portal`, `quality`, `workflow`  
> Important rule: Phase 24 introduces formal scope and deliverable acceptance. It does **not** implement invoice/billing, revenue recognition, legal contract execution, full external client portal, or full document generation.

---

# 0. Purpose

Phase 24 turns project scope from only WBS/task planning into formal deliverable governance.

Earlier phases already created:

```text
Project
ProjectPhase
WBS
Task
Schedule
Quote
Baseline
ChangeRequest
Reporting
Notification
```

Phase 24 answers:

```text
What exactly is in project scope?
Which deliverables are promised?
Which WBS nodes/tasks support each deliverable?
What acceptance criteria must be satisfied?
What evidence proves the deliverable is ready?
Who reviewed it?
Who accepted it?
What is rejected and why?
How is a scope/deliverable change controlled after baseline?
```

Phase 24 is about **scope commitment and formal acceptance**, not invoice/payment.

---

# 1. Source inputs

Before coding Phase 24, the agent must read:

```text
1. Current backend codebase
2. Phase 09 Project Core implementation
3. Phase 10 Project Authorization implementation
4. Phase 18 Quote implementation
5. Phase 19 Baseline / Change Request implementation
6. Phase 20 Notification implementation
7. Phase 22 Reporting implementation
8. Phase 23 hardening completion file
9. Current Document Type / Knowledge catalog from Phase 08
10. Current EventDefinition and Notification rule seeders
11. Current IAM permissions
12. Existing project/task/WBS relationships
13. Existing baseline/change guard behavior
```

The agent must inspect actual code and not assume capabilities exist.

---

# 2. Current expected backend state

After Phase 23, Scopery should have a hardened core with:

```text
Project / Phase / WBS / Task
Baseline / ChangeRequest
Quote / Finance references
Notifications
Reporting/export
IAM and event registry
```

Likely missing:

```text
ScopePackage
ScopeItem
Deliverable
DeliverableAcceptanceCriteria
DeliverableEvidence
DeliverableReview
DeliverableAcceptance
Scope-to-WBS mapping
Deliverable-to-task mapping
Formal acceptance lifecycle
Acceptance rejection reason/history
Acceptance report
Scope change integration
```

Phase 24 implements those.

---

# 3. Target statement

Phase 24 must deliver:

```text
1. ScopePackage for project-level scope grouping.
2. ScopeItem for agreed scope units.
3. Deliverable entity linked to phase/WBS/tasks/scope items.
4. AcceptanceCriteria per deliverable.
5. AcceptanceEvidence per deliverable/criteria.
6. Formal review and acceptance lifecycle.
7. Rejection/rework loop.
8. Acceptance status reporting.
9. ChangeRequest integration for post-baseline scope/deliverable changes.
10. Event/notification integration.
11. IAM permissions for scope and acceptance.
12. Tests and completion report.
```

---

# 4. Boundary decisions

## 4.1 Scope is not WBS

WBS is planning/decomposition.

Scope is commitment.

Relationship:

```text
ScopeItem → can map to WBS nodes and deliverables
WBS → can produce tasks
Deliverable → can be supported by WBS/tasks and accepted formally
```

A WBS node can exist for internal planning without being client-facing scope.

A ScopeItem can be client-facing without exposing every task.

## 4.2 Acceptance is not billing

Acceptance can later trigger billing milestones, but Phase 24 does not create invoices.

Rule:

```text
Accepted deliverable does not automatically create invoice, revenue recognition, or payment request.
```

Those belong to Phase 36.

## 4.3 Acceptance is not legal contract execution

Phase 24 records formal acceptance within project operations.

Legal contract lifecycle is deferred to Phase 36.

## 4.4 External client portal is not implemented

Phase 24 can model client acceptance data internally.

External client login/portal approval is deferred.

---

# 5. Classification labels

Use these labels in the completion file:

| Label | Meaning |
|---|---|
| `CURRENTLY_IMPLEMENTED` | Existing backend already supports it with tests. |
| `PARTIALLY_IMPLEMENTED` | Some support exists but gaps remain. |
| `MUST_IMPLEMENT_IN_PHASE_24` | Required now. |
| `SEED_ONLY_IN_PHASE_24` | Seed definitions/events/permissions only. |
| `DEFERRED_TO_PHASE_XX` | Later phase returns. |
| `DEFERRED_TO_POST_23_BACKLOG` | Dynamic Work OS expansion backlog. |
| `NOT_IN_SCOPE_FOR_PHASE_24` | Explicitly not in this phase. |

---

# 6. Required capabilities

---

## 6.1 SCP-001 — ScopePackage

Classification: `MUST_IMPLEMENT_IN_PHASE_24`

Purpose:

```text
Group project scope into a versioned/controlled package.
```

Examples:

```text
Initial MVP Scope
Phase 1 Scope
Client Portal Scope
Internal Implementation Scope
```

Rules:

```text
1. ScopePackage belongs to one project.
2. Project must not be archived.
3. Code unique within project.
4. DRAFT package can be edited.
5. APPROVED package immutable except archive.
6. Current baseline/change guard applies after baseline.
7. ScopePackage is not a quote or contract.
```

Status:

```text
DRAFT
READY
APPROVED
CURRENT
ARCHIVED
```

or:

```text
DRAFT
APPROVED
ARCHIVED + currentFlag
```

Completion file must document final model.

---

## 6.2 SCP-002 — ScopeItem

Classification: `MUST_IMPLEMENT_IN_PHASE_24`

Purpose:

```text
Represent one committed scope unit.
```

Scope item types:

```text
FEATURE
MODULE
DELIVERABLE_GROUP
SERVICE
INTEGRATION
REPORT
TRAINING
DOCUMENTATION
SUPPORT
OTHER
```

Fields should support:

```text
title
description
type
inScope flag
outOfScope flag
priority
acceptanceRequired flag
sourceQuoteLineId optional
sourceChangeRequestId optional
```

Rules:

```text
1. ScopeItem belongs to ScopePackage and Project.
2. ScopeItem title required.
3. Cannot be both in-scope and out-of-scope.
4. Out-of-scope item can be documented but not accepted.
5. Approved package scope items immutable.
6. Post-baseline changes require ChangeRequest.
```

---

## 6.3 SCP-003 — Scope-to-WBS mapping

Classification: `MUST_IMPLEMENT_IN_PHASE_24`

Purpose:

```text
Connect committed scope to execution structure.
```

Rules:

```text
1. ScopeItem can map to zero or many WBS nodes.
2. WBS node must belong to same project.
3. Mapping does not grant access to WBS.
4. Mapping can be used for reporting coverage.
5. Removing mapping after baseline requires ChangeRequest if controlled.
```

---

## 6.4 DLV-001 — Deliverable

Classification: `MUST_IMPLEMENT_IN_PHASE_24`

Purpose:

```text
Represent a promised output that can be reviewed and accepted.
```

Deliverable types:

```text
SOFTWARE_FEATURE
SCREEN
API
REPORT
DOCUMENT
TRAINING_SESSION
DEPLOYMENT
CONFIGURATION
INTEGRATION
DATA_MIGRATION
DESIGN_ASSET
OTHER
```

Rules:

```text
1. Deliverable belongs to project.
2. Deliverable can optionally belong to phase.
3. Deliverable can link to ScopeItem.
4. Deliverable can link to WBS nodes/tasks.
5. Deliverable title required.
6. AcceptanceRequired controls formal acceptance.
7. Deliverable status changes are audited.
8. Accepted deliverable immutable except reopen with permission/change flow.
```

Status:

```text
DRAFT
PLANNED
IN_PROGRESS
READY_FOR_REVIEW
IN_REVIEW
ACCEPTED
REJECTED
REWORK_REQUIRED
CANCELLED
ARCHIVED
```

---

## 6.5 DLV-002 — Deliverable-to-task mapping

Classification: `MUST_IMPLEMENT_IN_PHASE_24`

Purpose:

```text
Show which tasks produce or support a deliverable.
```

Rules:

```text
1. Task must belong to same project.
2. One deliverable can map to many tasks.
3. One task can support many deliverables if product allows.
4. Deliverable readiness can be calculated from mapped tasks.
5. Mapping does not automatically complete tasks.
```

Recommended readiness rule:

```text
A deliverable cannot be marked READY_FOR_REVIEW if required mapped tasks are not completed, unless override permission exists.
```

---

## 6.6 DLV-003 — AcceptanceCriteria

Classification: `MUST_IMPLEMENT_IN_PHASE_24`

Purpose:

```text
Define measurable conditions for deliverable acceptance.
```

Criteria types:

```text
FUNCTIONAL
NON_FUNCTIONAL
DESIGN
PERFORMANCE
SECURITY
DOCUMENTATION
TRAINING
DEPLOYMENT
DATA
COMPLIANCE
OTHER
```

Rules:

```text
1. Criteria belongs to deliverable.
2. Title/description required.
3. Criteria can be mandatory or optional.
4. Mandatory criteria must be satisfied before acceptance.
5. Criteria can reference test case later in Phase 26.
6. Criteria status audited.
```

Status:

```text
OPEN
SATISFIED
FAILED
WAIVED
ARCHIVED
```

Waive requires reason and permission.

---

## 6.7 DLV-004 — AcceptanceEvidence

Classification: `MUST_IMPLEMENT_IN_PHASE_24`

Purpose:

```text
Attach evidence that acceptance criteria are satisfied.
```

Evidence types:

```text
TEXT_NOTE
LINK
FILE_REFERENCE
SCREENSHOT_REFERENCE
TEST_RESULT_REFERENCE
DEPLOYMENT_REFERENCE
MEETING_NOTE
CLIENT_CONFIRMATION
OTHER
```

Rules:

```text
1. Evidence belongs to deliverable and optionally criteria.
2. Evidence can store text/link/reference id.
3. File upload/storage integration can be deferred to Document Hub if not present.
4. Evidence cannot contain secrets.
5. Evidence changes audited.
```

If Document Hub is not implemented:

```text
Use external link/reference fields only.
Do not claim file versioning/storage.
```

---

## 6.8 DLV-005 — Deliverable review

Classification: `MUST_IMPLEMENT_IN_PHASE_24`

Purpose:

```text
Submit deliverable for internal/client review.
```

Rules:

```text
1. Deliverable must be READY_FOR_REVIEW to start review.
2. Review can assign reviewers.
3. Review decision can be APPROVE, REJECT, REQUEST_REWORK.
4. Rejection/rework requires reason.
5. Review does not equal client acceptance unless reviewer role indicates final acceptance.
```

Review status:

```text
OPEN
APPROVED
REJECTED
REWORK_REQUESTED
CANCELLED
```

---

## 6.9 DLV-006 — Formal acceptance

Classification: `MUST_IMPLEMENT_IN_PHASE_24`

Purpose:

```text
Record final acceptance of deliverable.
```

Rules:

```text
1. Deliverable must have all mandatory criteria satisfied or waived.
2. Actor must have DELIVERABLE_ACCEPT.
3. Acceptance records acceptedAt/acceptedBy.
4. External client acceptance can be recorded internally if allowed.
5. Acceptance does not create invoice.
6. Accepted deliverable cannot be edited without reopen/change flow.
```

Acceptance outcomes:

```text
ACCEPTED
REJECTED
ACCEPTED_WITH_NOTES
REWORK_REQUIRED
```

---

## 6.10 DLV-007 — Reopen accepted deliverable

Classification: `MUST_IMPLEMENT_IN_PHASE_24`

Rules:

```text
1. Reopen requires DELIVERABLE_REOPEN permission.
2. Reopen requires reason.
3. If current baseline exists, reopen may require ChangeRequest.
4. Reopen is audited.
5. Reopen does not reverse billing because billing is not Phase 24.
```

---

## 6.11 SCP-004 — Quote to scope import

Classification: `MUST_IMPLEMENT_IN_PHASE_24` if Quote exists; otherwise defer.

Purpose:

```text
Create initial scope package from approved/sent QuoteVersion and QuoteLines.
```

Rules:

```text
1. QuoteVersion must belong to project.
2. QuoteVersion should be APPROVED/SENT/ACCEPTED.
3. Import creates ScopePackage DRAFT.
4. QuoteLine can become ScopeItem or Deliverable.
5. Import preserves sourceQuoteVersionId/sourceQuoteLineId.
6. Import does not change quote.
```

---

## 6.12 SCP-005 — ChangeRequest integration

Classification: `MUST_IMPLEMENT_IN_PHASE_24`

Rules after current baseline:

```text
1. Adding/removing/changing ScopeItem requires ChangeRequest.
2. Adding/removing/changing accepted deliverable requires ChangeRequest or reopen permission.
3. Changing acceptance criteria after baseline requires ChangeRequest.
4. Approved ChangeRequest apply must use scope/deliverable domain actions.
5. Change impact report should include scope/deliverable impact.
```

---

## 6.13 RPT-001 — Scope/deliverable reporting

Classification: `MUST_IMPLEMENT_IN_PHASE_24`

Reports:

```text
scope coverage report
deliverable status report
acceptance criteria status report
acceptance evidence report
rework/rejection report
accepted deliverable report
```

May integrate into Phase 22 reporting.

---

# 7. Entity model TO-BE

If current schema differs, agent must map actual fields and document gaps.

---

## 7.1 ScopePackage — `project_scope_package`

Fields:

```text
id UUID PK
project_id UUID NOT NULL
workspace_id UUID NOT NULL
source_quote_version_id UUID NULL
source_baseline_id UUID NULL
code VARCHAR(100) NOT NULL
name VARCHAR(255) NOT NULL
description TEXT NULL
status VARCHAR(50) NOT NULL
current_flag BOOLEAN NOT NULL DEFAULT false
approved_at TIMESTAMP NULL
approved_by UUID NULL
created_at / created_by
updated_at / updated_by
archived_at / archived_by
trace_id VARCHAR(100) NULL
version INT
```

Constraints:

```text
unique project_id + code
only one current package per project if current_flag used
```

---

## 7.2 ScopeItem — `project_scope_item`

Fields:

```text
id UUID PK
scope_package_id UUID NOT NULL
project_id UUID NOT NULL
workspace_id UUID NOT NULL
source_quote_line_id UUID NULL
source_change_request_id UUID NULL
parent_scope_item_id UUID NULL
type VARCHAR(50) NOT NULL
code VARCHAR(100) NULL
title VARCHAR(255) NOT NULL
description TEXT NULL
in_scope BOOLEAN NOT NULL DEFAULT true
out_of_scope BOOLEAN NOT NULL DEFAULT false
priority VARCHAR(50) NULL
acceptance_required BOOLEAN NOT NULL DEFAULT true
status VARCHAR(50) NOT NULL
sort_order INT NULL
created_at / created_by
updated_at / updated_by
archived_at / archived_by
version INT
```

Status:

```text
DRAFT
ACTIVE
APPROVED
CHANGED
ARCHIVED
```

---

## 7.3 ScopeItemWbsMapping — `project_scope_item_wbs_mapping`

Fields:

```text
id UUID PK
scope_item_id UUID NOT NULL
project_id UUID NOT NULL
wbs_node_id UUID NOT NULL
mapping_type VARCHAR(50) NOT NULL
created_at / created_by
archived_at / archived_by
version INT
```

Mapping type:

```text
PRIMARY
SUPPORTING
REFERENCE
```

Constraint:

```text
unique active scope_item_id + wbs_node_id + mapping_type
```

---

## 7.4 Deliverable — `project_deliverable`

Fields:

```text
id UUID PK
project_id UUID NOT NULL
workspace_id UUID NOT NULL
scope_item_id UUID NULL
project_phase_id UUID NULL
source_quote_line_id UUID NULL
source_change_request_id UUID NULL
code VARCHAR(100) NULL
title VARCHAR(255) NOT NULL
description TEXT NULL
type VARCHAR(50) NOT NULL
acceptance_required BOOLEAN NOT NULL DEFAULT true
status VARCHAR(50) NOT NULL
planned_due_date DATE NULL
ready_for_review_at TIMESTAMP NULL
accepted_at TIMESTAMP NULL
accepted_by UUID NULL
rejected_at TIMESTAMP NULL
rejected_by UUID NULL
rejection_reason TEXT NULL
created_at / created_by
updated_at / updated_by
archived_at / archived_by
trace_id VARCHAR(100) NULL
version INT
```

Constraint:

```text
unique project_id + code where code not null
```

---

## 7.5 DeliverableTaskMapping — `project_deliverable_task_mapping`

Fields:

```text
id UUID PK
deliverable_id UUID NOT NULL
project_id UUID NOT NULL
task_id UUID NOT NULL
mapping_type VARCHAR(50) NOT NULL
required_for_acceptance BOOLEAN NOT NULL DEFAULT true
created_at / created_by
archived_at / archived_by
version INT
```

Mapping type:

```text
PRODUCES
SUPPORTS
VALIDATES
REFERENCE
```

Constraint:

```text
unique active deliverable_id + task_id + mapping_type
```

---

## 7.6 AcceptanceCriteria — `project_acceptance_criteria`

Fields:

```text
id UUID PK
deliverable_id UUID NOT NULL
project_id UUID NOT NULL
type VARCHAR(50) NOT NULL
title VARCHAR(255) NOT NULL
description TEXT NOT NULL
mandatory BOOLEAN NOT NULL DEFAULT true
status VARCHAR(50) NOT NULL
waived_at TIMESTAMP NULL
waived_by UUID NULL
waiver_reason TEXT NULL
created_at / created_by
updated_at / updated_by
archived_at / archived_by
version INT
```

Status:

```text
OPEN
SATISFIED
FAILED
WAIVED
ARCHIVED
```

---

## 7.7 AcceptanceEvidence — `project_acceptance_evidence`

Fields:

```text
id UUID PK
deliverable_id UUID NOT NULL
acceptance_criteria_id UUID NULL
project_id UUID NOT NULL
type VARCHAR(50) NOT NULL
title VARCHAR(255) NOT NULL
description TEXT NULL
evidence_text TEXT NULL
evidence_url VARCHAR(1000) NULL
document_reference_id UUID NULL
external_reference VARCHAR(500) NULL
submitted_at TIMESTAMP NULL
submitted_by UUID NULL
created_at / created_by
updated_at / updated_by
archived_at / archived_by
version INT
```

---

## 7.8 DeliverableReview — `project_deliverable_review`

Fields:

```text
id UUID PK
deliverable_id UUID NOT NULL
project_id UUID NOT NULL
review_round INT NOT NULL
status VARCHAR(50) NOT NULL
submitted_at TIMESTAMP NOT NULL
submitted_by UUID NOT NULL
reviewed_at TIMESTAMP NULL
reviewed_by UUID NULL
decision VARCHAR(50) NULL
decision_reason TEXT NULL
created_at / created_by
updated_at / updated_by
version INT
```

Status:

```text
OPEN
APPROVED
REJECTED
REWORK_REQUESTED
CANCELLED
```

Decision:

```text
APPROVE
REJECT
REQUEST_REWORK
```

---

## 7.9 DeliverableAcceptance — `project_deliverable_acceptance`

Fields:

```text
id UUID PK
deliverable_id UUID NOT NULL
project_id UUID NOT NULL
acceptance_round INT NOT NULL
outcome VARCHAR(50) NOT NULL
accepted_at TIMESTAMP NULL
accepted_by UUID NULL
accepted_by_name_snapshot VARCHAR(255) NULL
accepted_by_email_snapshot VARCHAR(255) NULL
notes TEXT NULL
rejected_at TIMESTAMP NULL
rejected_by UUID NULL
rejection_reason TEXT NULL
created_at / created_by
trace_id VARCHAR(100) NULL
version INT
```

Outcome:

```text
ACCEPTED
ACCEPTED_WITH_NOTES
REJECTED
REWORK_REQUIRED
```

---

# 8. API TO-BE list

All APIs use `/api`.

---

## 8.1 Scope package APIs

```text
POST  /api/projects/{projectId}/scope-packages
GET   /api/projects/{projectId}/scope-packages
GET   /api/projects/{projectId}/scope-packages/{scopePackageId}
PUT   /api/projects/{projectId}/scope-packages/{scopePackageId}
POST  /api/projects/{projectId}/scope-packages/{scopePackageId}/approve
POST  /api/projects/{projectId}/scope-packages/{scopePackageId}/mark-current
PATCH /api/projects/{projectId}/scope-packages/{scopePackageId}/archive
POST  /api/projects/{projectId}/scope-packages/import-from-quote
```

Import request:

```json
{
  "quoteVersionId": "uuid",
  "name": "Scope from approved quote",
  "importMode": "QUOTE_LINES_TO_SCOPE_ITEMS"
}
```

---

## 8.2 Scope item APIs

```text
POST   /api/projects/{projectId}/scope-packages/{scopePackageId}/items
GET    /api/projects/{projectId}/scope-packages/{scopePackageId}/items
GET    /api/projects/{projectId}/scope-items/{scopeItemId}
PUT    /api/projects/{projectId}/scope-items/{scopeItemId}
PATCH  /api/projects/{projectId}/scope-items/{scopeItemId}/archive
POST   /api/projects/{projectId}/scope-items/{scopeItemId}/wbs-mappings
GET    /api/projects/{projectId}/scope-items/{scopeItemId}/wbs-mappings
DELETE /api/projects/{projectId}/scope-items/{scopeItemId}/wbs-mappings/{mappingId}
```

---

## 8.3 Deliverable APIs

```text
POST  /api/projects/{projectId}/deliverables
GET   /api/projects/{projectId}/deliverables
GET   /api/projects/{projectId}/deliverables/{deliverableId}
PUT   /api/projects/{projectId}/deliverables/{deliverableId}
PATCH /api/projects/{projectId}/deliverables/{deliverableId}/status
PATCH /api/projects/{projectId}/deliverables/{deliverableId}/archive
POST  /api/projects/{projectId}/deliverables/{deliverableId}/reopen
```

Filters:

```text
phaseId
scopeItemId
status
type
acceptanceRequired
dueBefore
```

---

## 8.4 Deliverable task mapping APIs

```text
POST   /api/projects/{projectId}/deliverables/{deliverableId}/task-mappings
GET    /api/projects/{projectId}/deliverables/{deliverableId}/task-mappings
DELETE /api/projects/{projectId}/deliverables/{deliverableId}/task-mappings/{mappingId}
```

---

## 8.5 Acceptance criteria APIs

```text
POST  /api/projects/{projectId}/deliverables/{deliverableId}/acceptance-criteria
GET   /api/projects/{projectId}/deliverables/{deliverableId}/acceptance-criteria
GET   /api/projects/{projectId}/acceptance-criteria/{criteriaId}
PUT   /api/projects/{projectId}/acceptance-criteria/{criteriaId}
PATCH /api/projects/{projectId}/acceptance-criteria/{criteriaId}/satisfy
PATCH /api/projects/{projectId}/acceptance-criteria/{criteriaId}/fail
PATCH /api/projects/{projectId}/acceptance-criteria/{criteriaId}/waive
PATCH /api/projects/{projectId}/acceptance-criteria/{criteriaId}/archive
```

Waive request:

```json
{
  "reason": "Client approved alternative acceptance condition"
}
```

---

## 8.6 Acceptance evidence APIs

```text
POST  /api/projects/{projectId}/deliverables/{deliverableId}/evidence
GET   /api/projects/{projectId}/deliverables/{deliverableId}/evidence
GET   /api/projects/{projectId}/evidence/{evidenceId}
PUT   /api/projects/{projectId}/evidence/{evidenceId}
PATCH /api/projects/{projectId}/evidence/{evidenceId}/archive
```

---

## 8.7 Review / acceptance APIs

```text
POST /api/projects/{projectId}/deliverables/{deliverableId}/submit-review
POST /api/projects/{projectId}/deliverables/{deliverableId}/reviews/{reviewId}/approve
POST /api/projects/{projectId}/deliverables/{deliverableId}/reviews/{reviewId}/reject
POST /api/projects/{projectId}/deliverables/{deliverableId}/reviews/{reviewId}/request-rework

POST /api/projects/{projectId}/deliverables/{deliverableId}/accept
POST /api/projects/{projectId}/deliverables/{deliverableId}/reject-acceptance
```

Accept request:

```json
{
  "outcome": "ACCEPTED_WITH_NOTES",
  "notes": "Accepted with minor UI text correction to be done in support window",
  "acceptedByNameSnapshot": "Client Representative",
  "acceptedByEmailSnapshot": "client@example.com"
}
```

---

## 8.8 Scope/deliverable reports APIs

```text
GET /api/projects/{projectId}/reports/scope-coverage
GET /api/projects/{projectId}/reports/deliverable-status
GET /api/projects/{projectId}/reports/acceptance-criteria
GET /api/projects/{projectId}/reports/acceptance-evidence
```

---

# 9. Authorization requirements

Required authorities:

```text
PROJECT_SCOPE_VIEW
PROJECT_SCOPE_CREATE
PROJECT_SCOPE_UPDATE
PROJECT_SCOPE_APPROVE
PROJECT_SCOPE_MARK_CURRENT
PROJECT_SCOPE_ARCHIVE
PROJECT_SCOPE_IMPORT_FROM_QUOTE

PROJECT_SCOPE_ITEM_VIEW
PROJECT_SCOPE_ITEM_CREATE
PROJECT_SCOPE_ITEM_UPDATE
PROJECT_SCOPE_ITEM_ARCHIVE
PROJECT_SCOPE_WBS_MAPPING_MANAGE

PROJECT_DELIVERABLE_VIEW
PROJECT_DELIVERABLE_CREATE
PROJECT_DELIVERABLE_UPDATE
PROJECT_DELIVERABLE_ARCHIVE
PROJECT_DELIVERABLE_REOPEN
PROJECT_DELIVERABLE_TASK_MAPPING_MANAGE

PROJECT_ACCEPTANCE_CRITERIA_VIEW
PROJECT_ACCEPTANCE_CRITERIA_CREATE
PROJECT_ACCEPTANCE_CRITERIA_UPDATE
PROJECT_ACCEPTANCE_CRITERIA_SATISFY
PROJECT_ACCEPTANCE_CRITERIA_WAIVE
PROJECT_ACCEPTANCE_CRITERIA_ARCHIVE

PROJECT_ACCEPTANCE_EVIDENCE_VIEW
PROJECT_ACCEPTANCE_EVIDENCE_CREATE
PROJECT_ACCEPTANCE_EVIDENCE_UPDATE
PROJECT_ACCEPTANCE_EVIDENCE_ARCHIVE

PROJECT_DELIVERABLE_REVIEW_SUBMIT
PROJECT_DELIVERABLE_REVIEW_DECIDE
PROJECT_DELIVERABLE_ACCEPT
PROJECT_DELIVERABLE_REJECT_ACCEPTANCE

PROJECT_SCOPE_REPORT_VIEW
PROJECT_DELIVERABLE_REPORT_VIEW
```

Rules:

```text
1. Project access required for all APIs.
2. Mutations require project/workspace membership and permission.
3. Acceptance requires stronger permission.
4. Waiving mandatory criteria requires stronger permission.
5. Importing from quote requires quote view permission.
6. Finance/quote values are not exposed by scope APIs unless explicitly needed and permitted.
7. External client users are not in scope unless client portal exists.
```

---

# 10. Lifecycle rules

## 10.1 ScopePackage lifecycle

```text
DRAFT → READY → APPROVED → CURRENT
DRAFT → ARCHIVED
APPROVED → ARCHIVED
```

Rules:

```text
1. DRAFT can be edited.
2. APPROVED/CURRENT immutable.
3. Only one current package per project.
4. Mark current audited.
5. After baseline, changes require ChangeRequest.
```

## 10.2 Deliverable lifecycle

```text
DRAFT
→ PLANNED
→ IN_PROGRESS
→ READY_FOR_REVIEW
→ IN_REVIEW
→ ACCEPTED

IN_REVIEW → REJECTED
IN_REVIEW → REWORK_REQUIRED
REWORK_REQUIRED → IN_PROGRESS
ACCEPTED → reopened only with permission/change flow
```

Rules:

```text
1. READY_FOR_REVIEW requires required mapped tasks completed unless override.
2. ACCEPTED requires mandatory criteria satisfied/waived.
3. REJECTED/REWORK_REQUIRED requires reason.
4. ACCEPTED deliverable immutable except reopen.
```

## 10.3 Criteria lifecycle

```text
OPEN → SATISFIED
OPEN → FAILED
OPEN → WAIVED
SATISFIED → OPEN only with permission/reason
```

Rules:

```text
1. Mandatory criteria cannot be ignored.
2. Waiver requires reason.
3. Evidence should exist before satisfy unless policy allows manual satisfy.
```

---

# 11. Baseline / ChangeRequest integration

Phase 24 must respect Phase 19 guard.

Controlled fields after baseline:

```text
ScopePackage approval/current
ScopeItem create/update/archive
ScopeItem WBS mapping changes
Deliverable create/update/archive
Deliverable task mapping changes
AcceptanceCriteria create/update/archive/waive
Accepted deliverable reopen
Deliverable acceptance reversal
```

Rules:

```text
1. If current baseline exists, controlled change requires ChangeRequest or override policy.
2. Approved ChangeRequest apply must call scope/deliverable domain actions.
3. Scope/deliverable impact should be captured in ChangeImpact.
4. Baseline snapshots should include scope/deliverable sections in future baselines.
```

---

# 12. Reporting integration

Phase 24 should extend Phase 22 with:

```text
scope coverage report
deliverable status report
criteria status report
acceptance evidence report
rework report
accepted deliverable report
```

Dashboard KPIs:

```text
totalDeliverables
acceptedDeliverables
inReviewDeliverables
reworkRequiredDeliverables
rejectedDeliverables
acceptanceCompletionPercent
criteriaSatisfiedPercent
deliverablesPastDue
```

---

# 13. Notification integration

Seed/use events for:

```text
SCOPE_PACKAGE_APPROVED
SCOPE_PACKAGE_MARKED_CURRENT
SCOPE_ITEM_CREATED
SCOPE_ITEM_UPDATED
DELIVERABLE_CREATED
DELIVERABLE_READY_FOR_REVIEW
DELIVERABLE_REVIEW_APPROVED
DELIVERABLE_REVIEW_REJECTED
DELIVERABLE_REWORK_REQUESTED
DELIVERABLE_ACCEPTED
DELIVERABLE_ACCEPTANCE_REJECTED
ACCEPTANCE_CRITERIA_WAIVED
ACCEPTANCE_EVIDENCE_ADDED
```

Possible recipients:

```text
project watchers
deliverable owner
task assignee/inCharge
reviewers
acceptance approvers
change watchers
```

Do not send external client emails unless external notification model exists.

---

# 14. Event Registry integration

Recommended source system:

```text
SCOPERY_SCOPE_ACCEPTANCE
```

Required events:

```text
SCOPE_PACKAGE_CREATED
SCOPE_PACKAGE_UPDATED
SCOPE_PACKAGE_APPROVED
SCOPE_PACKAGE_MARKED_CURRENT
SCOPE_PACKAGE_ARCHIVED

SCOPE_ITEM_CREATED
SCOPE_ITEM_UPDATED
SCOPE_ITEM_ARCHIVED
SCOPE_ITEM_WBS_MAPPING_CREATED
SCOPE_ITEM_WBS_MAPPING_REMOVED

DELIVERABLE_CREATED
DELIVERABLE_UPDATED
DELIVERABLE_STATUS_CHANGED
DELIVERABLE_READY_FOR_REVIEW
DELIVERABLE_REVIEW_SUBMITTED
DELIVERABLE_REVIEW_APPROVED
DELIVERABLE_REVIEW_REJECTED
DELIVERABLE_REWORK_REQUESTED
DELIVERABLE_ACCEPTED
DELIVERABLE_ACCEPTED_WITH_NOTES
DELIVERABLE_ACCEPTANCE_REJECTED
DELIVERABLE_REOPENED
DELIVERABLE_ARCHIVED

ACCEPTANCE_CRITERIA_CREATED
ACCEPTANCE_CRITERIA_UPDATED
ACCEPTANCE_CRITERIA_SATISFIED
ACCEPTANCE_CRITERIA_FAILED
ACCEPTANCE_CRITERIA_WAIVED
ACCEPTANCE_CRITERIA_ARCHIVED

ACCEPTANCE_EVIDENCE_ADDED
ACCEPTANCE_EVIDENCE_UPDATED
ACCEPTANCE_EVIDENCE_ARCHIVED
```

Standard variables:

```text
actor.userId
organization.id
workspace.id
project.id
scopePackage.id
scopeItem.id
deliverable.id
deliverable.title
acceptanceCriteria.id
acceptanceEvidence.id
review.id
acceptance.id
outcome
occurredAt
traceId
```

---

# 15. Audit / activity / outbox

Audit-sensitive actions:

```text
ScopePackage approved/current
ScopeItem changed after baseline
Deliverable submitted for review
Deliverable accepted/rejected/reopened
Acceptance criteria waived
Acceptance evidence added/archived
Acceptance rejection reason recorded
```

Activity actions:

```text
SCOPE_PACKAGE_APPROVED
DELIVERABLE_READY_FOR_REVIEW
DELIVERABLE_ACCEPTED
DELIVERABLE_REWORK_REQUESTED
ACCEPTANCE_CRITERIA_WAIVED
```

Outbox:

```text
All major scope/deliverable/acceptance events should use platform outbox.
```

Idempotency recommended for:

```text
POST /scope-packages
POST /scope-packages/import-from-quote
POST /deliverables
POST /deliverables/{id}/submit-review
POST /deliverables/{id}/accept
POST /acceptance-criteria/{id}/waive
```

---

# 16. Business rules master

## 16.1 Scope rules

```text
SCP-001 Project must exist.
SCP-002 Project must not be archived.
SCP-003 ScopePackage code unique within project.
SCP-004 DRAFT package editable.
SCP-005 APPROVED/CURRENT package immutable.
SCP-006 Only one current package per project.
SCP-007 ScopeItem cannot be both in-scope and out-of-scope.
SCP-008 Post-baseline scope changes require ChangeRequest.
SCP-009 Import from quote does not mutate quote.
```

## 16.2 Deliverable rules

```text
DLV-001 Deliverable belongs to project.
DLV-002 Deliverable title required.
DLV-003 Phase/scope/task references must belong to same project.
DLV-004 READY_FOR_REVIEW requires required mapped tasks complete unless override.
DLV-005 ACCEPTED requires mandatory criteria satisfied or waived.
DLV-006 ACCEPTED deliverable immutable unless reopened.
DLV-007 Reopen requires reason and permission.
DLV-008 Deliverable acceptance does not create invoice.
```

## 16.3 Criteria/evidence rules

```text
ACC-001 Criteria belongs to deliverable.
ACC-002 Mandatory criteria required for acceptance.
ACC-003 Waiver requires reason and permission.
ACC-004 Evidence belongs to deliverable and optionally criteria.
ACC-005 Evidence cannot store secrets.
ACC-006 File/versioned document evidence deferred to Document Hub unless implemented.
```

## 16.4 Review/acceptance rules

```text
REV-001 Submit review requires READY_FOR_REVIEW.
REV-002 Review decision requires reviewer permission.
REV-003 Reject/rework requires reason.
ACP-001 Accept requires DELIVERABLE_ACCEPT.
ACP-002 Acceptance records actor/time/outcome.
ACP-003 Accepted with notes allowed.
ACP-004 Acceptance does not create billing/revenue.
```

---

# 17. Error catalog

Exact names follow project convention, but these concepts must exist.

```text
SCOPE_PACKAGE_NOT_FOUND
SCOPE_PACKAGE_CODE_ALREADY_EXISTS
SCOPE_PACKAGE_NOT_DRAFT
SCOPE_PACKAGE_IMMUTABLE
SCOPE_PACKAGE_CURRENT_CONFLICT
SCOPE_PACKAGE_PROJECT_ARCHIVED
SCOPE_PACKAGE_QUOTE_NOT_FOUND
SCOPE_PACKAGE_QUOTE_NOT_APPROVED
SCOPE_ACCESS_DENIED

SCOPE_ITEM_NOT_FOUND
SCOPE_ITEM_INVALID_SCOPE_FLAGS
SCOPE_ITEM_PACKAGE_MISMATCH
SCOPE_ITEM_IMMUTABLE
SCOPE_ITEM_BASELINE_CHANGE_REQUIRED
SCOPE_ITEM_WBS_MISMATCH
SCOPE_ITEM_MAPPING_DUPLICATE

DELIVERABLE_NOT_FOUND
DELIVERABLE_CODE_ALREADY_EXISTS
DELIVERABLE_PHASE_MISMATCH
DELIVERABLE_SCOPE_ITEM_MISMATCH
DELIVERABLE_TASK_MISMATCH
DELIVERABLE_INVALID_STATUS
DELIVERABLE_NOT_READY_FOR_REVIEW
DELIVERABLE_TASKS_NOT_COMPLETED
DELIVERABLE_ACCEPTANCE_BLOCKED
DELIVERABLE_ALREADY_ACCEPTED
DELIVERABLE_REOPEN_REASON_REQUIRED
DELIVERABLE_BASELINE_CHANGE_REQUIRED
DELIVERABLE_ACCESS_DENIED

ACCEPTANCE_CRITERIA_NOT_FOUND
ACCEPTANCE_CRITERIA_MANDATORY_NOT_SATISFIED
ACCEPTANCE_CRITERIA_WAIVER_REASON_REQUIRED
ACCEPTANCE_CRITERIA_ACCESS_DENIED

ACCEPTANCE_EVIDENCE_NOT_FOUND
ACCEPTANCE_EVIDENCE_INVALID_REFERENCE
ACCEPTANCE_EVIDENCE_SECRET_DETECTED
ACCEPTANCE_EVIDENCE_ACCESS_DENIED

DELIVERABLE_REVIEW_NOT_FOUND
DELIVERABLE_REVIEW_REASON_REQUIRED
DELIVERABLE_ACCEPTANCE_NOT_FOUND
DELIVERABLE_ACCEPTANCE_REASON_REQUIRED
```

---

# 18. Required tests

## 18.1 ScopePackage tests

```text
createScopePackage_valid_success
createScopePackage_archivedProject_rejected
createScopePackage_duplicateCode_conflict
updateDraftScopePackage_success
updateApprovedScopePackage_rejected
approveScopePackage_valid_success
markCurrent_onlyOneCurrentPackage
importFromApprovedQuote_success
importFromUnapprovedQuote_rejected
```

## 18.2 ScopeItem tests

```text
createScopeItem_valid_success
createScopeItem_inScopeAndOutOfScope_rejected
updateScopeItem_inApprovedPackage_rejected
mapScopeItemToWbs_valid_success
mapScopeItemToWbs_otherProject_rejected
duplicateWbsMapping_rejected
postBaselineScopeItemChange_requiresChangeRequest
```

## 18.3 Deliverable tests

```text
createDeliverable_valid_success
createDeliverable_phaseOtherProject_rejected
createDeliverable_scopeItemOtherProject_rejected
mapDeliverableToTask_valid_success
mapDeliverableToTask_otherProject_rejected
readyForReview_requiresRequiredTasksComplete
readyForReview_overrideRequiresPermission
acceptedDeliverable_immutable
reopenAcceptedDeliverable_requiresReason
postBaselineDeliverableChange_requiresChangeRequest
```

## 18.4 Acceptance criteria tests

```text
createCriteria_valid_success
satisfyCriteria_valid_success
failCriteria_valid_success
waiveCriteria_requiresReason
waiveMandatoryCriteria_requiresPermission
acceptDeliverable_requiresMandatoryCriteriaSatisfiedOrWaived
```

## 18.5 Evidence tests

```text
addEvidence_validText_success
addEvidence_validUrl_success
addEvidence_criteriaOtherDeliverable_rejected
addEvidence_secretDetected_rejectedIfSecretScannerExists
archiveEvidence_valid_success
```

## 18.6 Review / acceptance tests

```text
submitReview_valid_success
submitReview_notReady_rejected
approveReview_valid_success
rejectReview_requiresReason
requestRework_requiresReason
acceptDeliverable_valid_success
acceptDeliverable_withoutPermission_forbidden
rejectAcceptance_requiresReason
acceptedDeliverable_doesNotCreateInvoice
acceptedDeliverable_doesNotCreateRevenue
```

## 18.7 Authorization tests

```text
viewScope_withoutPermission_forbidden
createScope_withoutPermission_forbidden
approveScope_withoutPermission_forbidden
viewDeliverable_withoutPermission_forbidden
acceptDeliverable_withoutPermission_forbidden
waiveCriteria_withoutPermission_forbidden
crossWorkspaceDeliverable_forbidden
```

## 18.8 Event/audit tests

```text
scopeEventSeeder_firstRun_createsDefinitions
scopeEventSeeder_secondRun_noDuplicates
deliverableAccepted_eventEmitted
criteriaWaived_auditCreated
deliverableReopened_auditCreated
scopePackageApproved_activityCreated
```

---

# 19. Manual verification checklist

Completion file must include:

```text
1. Create ScopePackage.
2. Create ScopeItems.
3. Map ScopeItem to WBS.
4. Create Deliverable from ScopeItem.
5. Map Deliverable to tasks.
6. Create mandatory acceptance criteria.
7. Add evidence.
8. Try to submit review before tasks complete and confirm blocked.
9. Complete required tasks or use authorized override.
10. Submit review.
11. Approve review.
12. Accept deliverable.
13. Confirm accepted deliverable immutable.
14. Reopen with reason and permission.
15. Create baseline and confirm controlled changes require ChangeRequest.
16. Confirm acceptance does not create invoice/revenue.
17. Confirm events/notifications/audit created.
```

---

# 20. Acceptance criteria

Phase 24 is accepted only if:

```text
1. Current scope/deliverable capability is classified against TO-BE.
2. ScopePackage implemented/tested.
3. ScopeItem implemented/tested.
4. Scope-to-WBS mapping implemented/tested.
5. Deliverable implemented/tested.
6. Deliverable-to-task mapping implemented/tested.
7. AcceptanceCriteria implemented/tested.
8. AcceptanceEvidence implemented/tested.
9. Review/acceptance lifecycle implemented/tested.
10. Post-baseline ChangeRequest integration implemented/tested.
11. Events seeded idempotently.
12. IAM permissions implemented/tested.
13. Reporting extension implemented/tested or explicitly deferred.
14. Activity/audit/outbox follows Phase 04.
15. No invoice/billing/revenue/contract/client portal is falsely claimed.
16. `mvn compile` passes.
17. `mvn test` passes.
18. Completion file exists.
```

Do not mark complete if:

```text
approved scope package can be edited
accepted deliverable can be edited without reopen/change flow
mandatory criteria can be ignored
acceptance creates fake invoice/revenue
post-baseline scope change bypasses ChangeRequest
cross-project mappings are allowed
events/permissions are missing
tests fail
```

---

# 21. Required phase completion file

Agent must create:

```text
docs/phase-complete/PHASE_24_SCOPE_DELIVERABLE_ACCEPTANCE_TO_BE_COMPLETE.md
```

Required sections:

```text
# Phase 24 — Scope / Deliverable / Formal Acceptance Complete

## 1. Summary
## 2. Source Inputs Reviewed
## 3. Current vs TO-BE Classification Matrix
## 4. Implemented in Current BE
## 5. Implemented / Hardened in This Phase
## 6. Seed-only Items Added
## 7. Deferred Items and Target Phase
## 8. Scope Boundary Decision
## 9. Entity Mapping
## 10. API Changes
## 11. ScopePackage Lifecycle Strategy
## 12. ScopeItem and WBS Mapping Strategy
## 13. Deliverable Lifecycle Strategy
## 14. Acceptance Criteria Strategy
## 15. Evidence Strategy
## 16. Review / Acceptance Strategy
## 17. Baseline / ChangeRequest Integration
## 18. Quote Import Strategy
## 19. Reporting Strategy
## 20. Notification / Event Strategy
## 21. Authorization Matrix
## 22. Activity / Audit / Outbox Notes
## 23. Idempotency Strategy
## 24. Tests Added
## 25. Commands Run
## 26. Test Results
## 27. Manual Verification
## 28. Assumptions
## 29. Deviations From Prompt
## 30. Known Risks
## 31. Future Phases That Must Return to Scope / Acceptance
```

---

# 22. Future phases that must return

```text
Phase 25 — RAID / Decision Management:
- Link risks/issues/decisions to deliverables and acceptance blockers.

Phase 26 — Quality / Test / Release Management:
- Link test cases/test runs to acceptance criteria and evidence.

Phase 27 — Full Document Hub:
- Store versioned acceptance evidence files, signed documents, generated acceptance reports.

Phase 30 — Customer / External Collaboration Portal:
- External client review/acceptance portal.

Phase 34 — Workflow / Approval / Automation:
- Multi-step acceptance approval, escalation, SoD.

Phase 36 — Contract / Billing / Revenue:
- Accepted deliverable can trigger billing milestone/invoice/revenue recognition.

Phase 38 — Audit / Compliance / Privacy / Retention:
- Retention and legal hold for acceptance evidence.

Phase 41 — Semantic Index:
- AI-assisted acceptance evidence retrieval and cross-deliverable traceability.
```

---

# 23. Agent anti-bịa rules

The agent must not:

```text
1. Claim scope is the same as WBS.
2. Claim deliverable acceptance creates invoice.
3. Claim deliverable acceptance creates revenue recognition.
4. Claim acceptance is legal contract execution.
5. Claim external client portal exists.
6. Claim file evidence versioning exists unless Document Hub/storage exists.
7. Allow accepted deliverable mutation without reopen/change flow.
8. Allow mandatory criteria to be ignored.
9. Allow post-baseline scope changes to bypass ChangeRequest.
10. Hide deferred billing/contract/client portal/document hub gaps.
```

---

# 24. Prompt to give coding agent

```text
You are implementing Phase 24 — TO-BE Scope Package, Deliverable Management, Acceptance Criteria, Evidence & Formal Acceptance.

This is not an as-is documentation task.

Before coding, read:
- CLAUDE.md / CLAUDE.ms
- Coding_convention.md
- Phase 00–23 docs and completion files
- Current backend code, migrations, tests

Your task:
1. Compare current scope/deliverable capability against this Phase 24 TO-BE spec.
2. Classify each capability with required labels.
3. Implement ScopePackage, ScopeItem, ScopeItemWbsMapping.
4. Implement Deliverable, DeliverableTaskMapping.
5. Implement AcceptanceCriteria and AcceptanceEvidence.
6. Implement DeliverableReview and DeliverableAcceptance lifecycle.
7. Integrate post-baseline changes with ChangeRequest guard.
8. Add quote import foundation if Quote exists.
9. Add events, notifications, permissions, reports, and tests.
10. Run mvn compile and mvn test.
11. Create docs/phase-complete/PHASE_24_SCOPE_DELIVERABLE_ACCEPTANCE_TO_BE_COMPLETE.md.

Do not implement or claim invoice/billing, revenue recognition, legal contract execution, full external client portal, full Document Hub file versioning, or workflow automation in this phase.
```

---

# 25. Quick tracking matrix

| Capability | Current backend | Phase 24 action | Later phase |
|---|---|---|---|
| ScopePackage | Missing/unknown | Must implement | — |
| ScopeItem | Missing/unknown | Must implement | — |
| Scope-to-WBS mapping | Missing/unknown | Must implement | — |
| Deliverable | Missing/unknown | Must implement | — |
| Deliverable-to-task mapping | Missing/unknown | Must implement | — |
| AcceptanceCriteria | Missing/unknown | Must implement | Phase 26 test linkage |
| AcceptanceEvidence | Missing/unknown | Must implement basic | Phase 27 file/versioning |
| DeliverableReview | Missing/unknown | Must implement | Phase 34 workflow |
| DeliverableAcceptance | Missing/unknown | Must implement | Phase 36 billing trigger |
| Quote-to-scope import | Missing/unknown | Must implement if quote exists | — |
| Client portal acceptance | Missing | Defer | Phase 30 |
| Billing milestone trigger | Missing | Defer | Phase 36 |
| Legal contract execution | Missing | Defer | Phase 36 |
| Document file evidence | Missing/partial | Defer if no storage | Phase 27 |
| Test case evidence | Missing | Defer | Phase 26 |
| AI acceptance support | Missing | Defer | Phase 41/AI follow-up |

---

# 26. Final principle

Phase 24 is not complete when "a deliverable row can be stored."

Phase 24 is complete when Scopery can govern formal scope acceptance:

```text
scope commitment
+ deliverable
+ acceptance criteria
+ evidence
+ review
+ acceptance decision
+ audit
+ change control
= trustworthy delivery acceptance
```

Scope is commitment.

WBS is planning.

Acceptance is proof.

Acceptance is not billing.

Accepted deliverables must not change silently.
