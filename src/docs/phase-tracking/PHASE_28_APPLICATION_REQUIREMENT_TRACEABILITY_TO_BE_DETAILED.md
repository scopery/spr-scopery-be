# PHASE 28 — TO-BE Application Registry, Requirements Management, Screen/API Registry & End-to-End Traceability

> Project: Scopery Backend  
> Phase: 28  
> Document type: TO-BE implementation-grade specification  
> Status: Planning / Gap-driven implementation spec  
> Roadmap group: Post-core Dynamic Work OS expansion  
> Depends on: Phase 00 — Master Backend Roadmap, Phase 01 — API Path / Security Baseline, Phase 02 — IAM Core, Phase 03 — Workspace / Organization / Team, Phase 04 — Platform Audit / Outbox / Idempotency, Phase 05 — Event Registry, Phase 06 — Notification Engine, Phase 08 — Knowledge / Document Type Catalog, Phase 09 — Project Core / WBS / Task, Phase 10 — Project Authorization, Phase 18 — Quote / Commercial Proposal, Phase 19 — Baseline / Change Request, Phase 20 — Project Events / Notifications, Phase 21 — AI-assisted Project Planning, Phase 22 — Reporting / Dashboard / Export, Phase 23 — Core Hardening, Phase 24 — Scope / Deliverable / Acceptance, Phase 25 — RAID / Decision Management, Phase 26 — Quality / Test / Release, Phase 27 — Document Hub / Generation  
> API base: `/api`  
> Primary module: `modules/applicationregistry`, `modules/requirements`, or `modules/project/traceability` depending on repository architecture  
> Related modules: `project`, `scope`, `deliverable`, `quality`, `release`, `document`, `raid`, `decision`, `baseline`, `change-request`, `reporting`, `ai-planning`, `iam`, `eventregistry`, future `integration`, `semantic-index`, `client-portal`, `workflow`  
> Important rule: Phase 28 introduces application registry, requirements, screens/API registry, and traceability. It does **not** implement source-code parsing, live CI scanner, automatic code generation, full product roadmap management, external issue tracker sync, semantic knowledge graph, or autonomous AI implementation.

---

# 0. Purpose

Phase 28 creates Scopery's product/application traceability layer.

Earlier phases created:

```text
Project / WBS / Task
Scope / Deliverable / Acceptance
Quality / Test / Defect / Release
Document Hub
RAID / Decisions
Baseline / ChangeRequest
Reporting
AI proposal layer
```

Phase 28 answers:

```text
Which application is this project building or changing?
Which modules/screens/APIs are affected?
Which requirements are approved?
Which scope items map to requirements?
Which requirements map to screens/components/APIs?
Which tasks implement each requirement?
Which test cases verify each requirement?
Which defects affect each requirement?
Which release includes each requirement?
Which documents/evidence support the requirement?
What changed between requirement versions?
What is uncovered or untested?
```

Phase 28 is the **requirements-to-release traceability foundation**.

---

# 1. Source inputs

Before coding Phase 28, the agent must read:

```text
1. Current backend codebase
2. Phase 08 Document Type / Knowledge catalog implementation
3. Phase 09 Project Core implementation
4. Phase 10 Project Authorization implementation
5. Phase 19 Baseline / Change Request implementation
6. Phase 21 AI-assisted Planning implementation
7. Phase 22 Reporting implementation
8. Phase 23 Core Hardening completion file
9. Phase 24 Scope / Deliverable / Acceptance implementation
10. Phase 25 RAID / Decision implementation
11. Phase 26 Quality / Test / Release implementation
12. Phase 27 Document Hub implementation
13. Current IAM seeders
14. Current EventDefinition seeders
15. Existing requirement/application/screen registry code if any
```

The agent must inspect real code and not assume implementation state.

---

# 2. Current expected backend state

After Phase 27, the backend likely has:

```text
Project
ScopeItem
Deliverable
AcceptanceCriteria
Task
TestCase
Defect
ReleasePackage
Document
ChangeRequest
Baseline
ReportExport
AIPlanningSuggestion
```

Likely missing:

```text
Application
ApplicationModule
ApplicationComponent
ApplicationScreen
ScreenSection
ScreenField
ScreenAction
ApiEndpointRegistry
DataEntityRegistry
Requirement
RequirementVersion
RequirementSource
RequirementStatus lifecycle
RequirementArtifactLink
TraceLink
TraceabilityMatrix
RequirementCoverageReport
RequirementImpactAnalysis
RequirementChangeSet
```

Phase 28 implements these.

---

# 3. Target statement

Phase 28 must deliver:

```text
1. Application registry per workspace/project.
2. Application module/component registry.
3. Screen registry with sections, fields, actions, and states.
4. API endpoint registry foundation.
5. Data entity registry foundation.
6. Requirement management with versioning and lifecycle.
7. Requirement source and classification.
8. Requirement links to scope, deliverables, WBS, tasks, screens, APIs, test cases, defects, releases, documents, decisions, RAID items, ChangeRequests, baselines, AI suggestions.
9. End-to-end traceability matrix.
10. Coverage and gap reporting.
11. Requirement impact analysis.
12. Baseline/change integration for approved requirements.
13. Notifications/events for requirement and traceability changes.
14. AI-assisted requirement drafting and trace suggestions as proposal only.
15. IAM, audit/outbox, idempotency, tests, and completion file.
```

---

# 4. Boundary decisions

## 4.1 Requirement is not Task

Requirement defines what is needed.

Task defines work to implement it.

A requirement can map to many tasks.

A task can implement many requirements if policy allows.

## 4.2 Screen registry is not UI builder

Phase 28 records screens, fields, actions, and relationships.

It does not generate frontend code or render dynamic UI.

## 4.3 API registry is not live gateway

Phase 28 records planned/existing endpoints and their relation to requirements.

It does not replace Spring controllers, API Gateway, or OpenAPI generation.

## 4.4 Traceability is not permission

A link between requirement and artifact does not grant access to the linked artifact.

IAM must still apply at query, drill-down, and export time.

## 4.5 Source-code scanning is deferred

Phase 28 can manually register API/screen/component data.

Automatic code parsing, repository scanning, CI sync, and OpenAPI import are deferred to Phase 39 unless simple import exists.

## 4.6 Semantic graph is deferred

Trace links are structured relational links.

Full semantic knowledge graph is deferred to Phase 41.

---

# 5. Classification labels

Use these labels in completion file:

| Label | Meaning |
|---|---|
| `CURRENTLY_IMPLEMENTED` | Existing backend already supports it with tests. |
| `PARTIALLY_IMPLEMENTED` | Some support exists but gaps remain. |
| `MUST_IMPLEMENT_IN_PHASE_28` | Required now. |
| `SEED_ONLY_IN_PHASE_28` | Seed definitions/events/permissions only. |
| `DEFERRED_TO_PHASE_XX` | Later phase returns. |
| `DEFERRED_TO_POST_23_BACKLOG` | Dynamic Work OS expansion backlog. |
| `NOT_IN_SCOPE_FOR_PHASE_28` | Explicitly not in this phase. |

---

# 6. Required capabilities

---

## 6.1 APP-001 — Application registry

Classification: `MUST_IMPLEMENT_IN_PHASE_28`

Purpose:

```text
Represent a software product/application being built, maintained, or analyzed.
```

Examples:

```text
Client Portal
HM Logistics Web App
Admin Backoffice
Mobile App
Order Extension
Integration Service
```

Rules:

```text
1. Application belongs to workspace.
2. Application can link to many projects.
3. Application name/code unique within workspace.
4. Application can be internal or external/client-facing.
5. Application archived state does not delete requirements/screens.
6. Access controlled by workspace/project permissions.
```

Application types:

```text
WEB_APP
MOBILE_APP
BACKEND_SERVICE
BROWSER_EXTENSION
DESKTOP_APP
API_SERVICE
DATA_PIPELINE
INTEGRATION
OTHER
```

Status:

```text
ACTIVE
MAINTENANCE
DEPRECATED
ARCHIVED
```

---

## 6.2 APP-002 — ApplicationProjectMapping

Classification: `MUST_IMPLEMENT_IN_PHASE_28`

Purpose:

```text
Link application registry item to projects that build/change it.
```

Rules:

```text
1. Application and project must belong to same workspace.
2. Project can link to multiple applications.
3. Application can link to multiple projects.
4. Mapping has role/type.
5. Mapping does not grant project access.
```

Mapping types:

```text
PRIMARY
AFFECTED
DEPENDENT
REFERENCE
```

---

## 6.3 APP-003 — ApplicationModule

Classification: `MUST_IMPLEMENT_IN_PHASE_28`

Purpose:

```text
Break application into logical modules.
```

Examples:

```text
Authentication
Order Management
Wallet
Quote
Reporting
Notification
Admin
Customer Portal
```

Rules:

```text
1. Module belongs to Application.
2. Module code/name unique within application.
3. Module can nest if product allows.
4. Module can link to requirements, screens, APIs, tasks, tests.
5. Archived module remains available for history.
```

---

## 6.4 APP-004 — ApplicationComponent

Classification: `MUST_IMPLEMENT_IN_PHASE_28`

Purpose:

```text
Represent technical/functional components inside an application/module.
```

Component types:

```text
FRONTEND_COMPONENT
BACKEND_MODULE
DATABASE_SCHEMA
API_CLIENT
JOB_WORKER
EVENT_CONSUMER
EXTENSION_COMPONENT
INTEGRATION_COMPONENT
LIBRARY
OTHER
```

Rules:

```text
1. Component belongs to Application and optionally Module.
2. Component can link to screens/APIs/data entities.
3. Component can link to tasks/tests/defects.
4. Component does not imply code ownership unless configured.
```

---

## 6.5 SCR-001 — ApplicationScreen

Classification: `MUST_IMPLEMENT_IN_PHASE_28`

Purpose:

```text
Register screens/pages/views in the application.
```

Examples:

```text
Login Page
Project Dashboard
Task Detail
Quote Detail
Admin Exchange Rate Page
Wallet History Page
Order List Page
```

Screen types:

```text
PAGE
MODAL
DRAWER
TAB
WIDGET
ADMIN_PAGE
PUBLIC_PAGE
MOBILE_SCREEN
EXTENSION_POPUP
OTHER
```

Rules:

```text
1. Screen belongs to Application.
2. Screen can belong to Module.
3. Screen route/path optional but recommended.
4. Screen can link to requirements, deliverables, tasks, tests, defects.
5. Screen can have sections, fields, actions, states.
6. Screen does not generate frontend code.
```

Status:

```text
PLANNED
DESIGNED
IN_DEVELOPMENT
IMPLEMENTED
TESTED
RELEASED
DEPRECATED
ARCHIVED
```

---

## 6.6 SCR-002 — ScreenSection

Classification: `MUST_IMPLEMENT_IN_PHASE_28`

Purpose:

```text
Describe major areas of a screen.
```

Examples:

```text
Header
Filter Bar
Order Table
Payment Popup
Finance Summary Card
Approval Action Panel
```

Rules:

```text
1. Section belongs to Screen.
2. Section name required.
3. Sort order required.
4. Section can link to requirements/tests.
```

---

## 6.7 SCR-003 — ScreenField

Classification: `MUST_IMPLEMENT_IN_PHASE_28`

Purpose:

```text
Register important screen fields/inputs/outputs.
```

Field types:

```text
TEXT
NUMBER
CURRENCY
DATE
DATETIME
BOOLEAN
SELECT
MULTI_SELECT
FILE
IMAGE
RICH_TEXT
TABLE_COLUMN
CALCULATED
OTHER
```

Rules:

```text
1. Field belongs to Screen or Section.
2. Field key/name required.
3. Sensitive fields flagged.
4. Validation rules can be stored.
5. Field can link to requirements/tests.
6. Field registry does not implement form engine.
```

---

## 6.8 SCR-004 — ScreenAction

Classification: `MUST_IMPLEMENT_IN_PHASE_28`

Purpose:

```text
Register user actions on a screen.
```

Action types:

```text
BUTTON
LINK
MENU_ITEM
FORM_SUBMIT
BULK_ACTION
NAVIGATION
APPROVAL_ACTION
EXPORT_ACTION
UPLOAD_ACTION
OTHER
```

Rules:

```text
1. Action belongs to Screen.
2. Action can link to API endpoint.
3. Action can link to permission requirement.
4. Action can link to requirement/test.
5. Action registry does not execute action.
```

---

## 6.9 API-001 — ApiEndpointRegistry

Classification: `MUST_IMPLEMENT_IN_PHASE_28`

Purpose:

```text
Register planned/existing API endpoints for traceability.
```

Fields:

```text
method
path
module
operationName
description
status
authRequired
permissionCode
requestSchemaRef
responseSchemaRef
```

Status:

```text
PLANNED
IMPLEMENTED
DEPRECATED
REMOVED
ARCHIVED
```

Rules:

```text
1. Endpoint belongs to Application or Backend Service.
2. Path should use `/api`, not `/api/v1`, unless documenting external legacy.
3. Endpoint can link to requirements, screen actions, tasks, tests.
4. Registry does not replace OpenAPI.
5. Automatic OpenAPI import deferred unless simple importer exists.
```

---

## 6.10 API-002 — DataEntityRegistry

Classification: `MUST_IMPLEMENT_IN_PHASE_28`

Purpose:

```text
Register important data entities/schemas for traceability.
```

Entity types:

```text
DOMAIN_ENTITY
DATABASE_TABLE
READ_MODEL
DTO
EVENT_PAYLOAD
REPORT_MODEL
EXTERNAL_SCHEMA
OTHER
```

Rules:

```text
1. Data entity belongs to Application/Module.
2. Entity can link to requirements/APIs/screens/tests.
3. Sensitive fields can be recorded.
4. Registry does not replace database migration.
```

---

## 6.11 REQ-001 — Requirement

Classification: `MUST_IMPLEMENT_IN_PHASE_28`

Purpose:

```text
Represent a business, user, functional, non-functional, compliance, or technical requirement.
```

Requirement types:

```text
BUSINESS
USER_STORY
FUNCTIONAL
NON_FUNCTIONAL
TECHNICAL
SECURITY
PERFORMANCE
COMPLIANCE
DATA
INTEGRATION
REPORTING
UX
OPERATIONAL
OTHER
```

Priority:

```text
LOW
MEDIUM
HIGH
CRITICAL
MUST
SHOULD
COULD
WONT
```

Status:

```text
DRAFT
PROPOSED
UNDER_REVIEW
APPROVED
IN_IMPLEMENTATION
IMPLEMENTED
VERIFIED
REJECTED
DEFERRED
SUPERSEDED
ARCHIVED
```

Rules:

```text
1. Requirement belongs to workspace and optionally project/application.
2. Requirement code unique within project/application scope.
3. Title required.
4. Approved requirement immutable except new version/change flow.
5. Requirement can link to scope/deliverable/task/test/release.
6. Requirement can have parent/child decomposition.
7. Requirement cannot be marked VERIFIED without test/acceptance evidence if policy requires.
```

---

## 6.12 REQ-002 — RequirementVersion

Classification: `MUST_IMPLEMENT_IN_PHASE_28`

Purpose:

```text
Preserve immutable requirement versions.
```

Rules:

```text
1. RequirementVersion belongs to Requirement.
2. Version number unique.
3. Approved version immutable.
4. Current version marker unique.
5. Version stores title, description, acceptance notes, rationale, priority, type.
6. New version required for material changes after approval/baseline.
```

---

## 6.13 REQ-003 — RequirementSource

Classification: `MUST_IMPLEMENT_IN_PHASE_28`

Purpose:

```text
Record where a requirement came from.
```

Source types:

```text
CLIENT_REQUEST
QUOTE
SCOPE_ITEM
DELIVERABLE
CHANGE_REQUEST
DOCUMENT
MEETING
RAID_ITEM
DECISION
AI_SUGGESTION
INTERNAL
REGULATION
OTHER
```

Rules:

```text
1. Source target must exist and belong to same workspace/project when applicable.
2. Source does not grant access.
3. Multiple sources allowed.
4. Source trace appears in reports.
```

---

## 6.14 REQ-004 — RequirementAcceptanceCriteria

Classification: `MUST_IMPLEMENT_IN_PHASE_28`

Purpose:

```text
Support requirement-level acceptance criteria, optionally synchronized with deliverable acceptance criteria.
```

Rules:

```text
1. Criteria belongs to Requirement.
2. Can link to Phase 24 AcceptanceCriteria.
3. Can link to TestCase.
4. Mandatory criteria must be covered before VERIFIED if policy enabled.
```

If Phase 24 AcceptanceCriteria already exists:

```text
Do not duplicate business meaning blindly.
Use mapping or lightweight requirement criteria if needed.
```

---

## 6.15 TRC-001 — TraceLink

Classification: `MUST_IMPLEMENT_IN_PHASE_28`

Purpose:

```text
Generic traceability link between requirement/application artifacts and delivery artifacts.
```

Source/target types:

```text
REQUIREMENT
REQUIREMENT_VERSION
APPLICATION
APPLICATION_MODULE
APPLICATION_COMPONENT
SCREEN
SCREEN_SECTION
SCREEN_FIELD
SCREEN_ACTION
API_ENDPOINT
DATA_ENTITY
SCOPE_ITEM
DELIVERABLE
ACCEPTANCE_CRITERIA
WBS_NODE
TASK
TEST_CASE
TEST_RUN
DEFECT
RELEASE_PACKAGE
DOCUMENT
RAID_ITEM
DECISION
CHANGE_REQUEST
BASELINE
QUOTE_VERSION
AI_SUGGESTION
```

Link types:

```text
IMPLEMENTS
VERIFIES
VALIDATES
COVERS
DEPENDS_ON
AFFECTS
BLOCKS
DERIVED_FROM
TRACE_TO
TRACE_FROM
RELEASED_IN
TESTED_BY
DEFECT_FOUND_IN
DOCUMENTED_BY
DECIDED_BY
CHANGED_BY
```

Rules:

```text
1. Source/target must belong to compatible workspace/project.
2. Duplicate active trace links prevented.
3. Trace link does not grant access.
4. Sensitive linked artifacts masked by permission.
5. Trace link archived, not hard deleted by default.
```

---

## 6.16 TRC-002 — Traceability matrix

Classification: `MUST_IMPLEMENT_IN_PHASE_28`

Purpose:

```text
Produce end-to-end coverage matrix.
```

Matrix examples:

```text
Scope → Requirement → Screen/API → Task → TestCase → Defect → Release
Requirement → AcceptanceCriteria → TestCase → TestRun result
Requirement → Document → Decision → ChangeRequest
Screen → Action → API Endpoint → Requirement → TestCase
```

Rules:

```text
1. Matrix uses TraceLink and direct module mappings.
2. Missing links flagged.
3. Unauthorized details masked.
4. Export uses Phase 22/27 controls.
5. Matrix is read model/report, not source of truth.
```

---

## 6.17 TRC-003 — Requirement coverage report

Classification: `MUST_IMPLEMENT_IN_PHASE_28`

Coverage dimensions:

```text
hasScope
hasDeliverable
hasTask
hasScreenOrApi
hasTestCase
hasPassingTest
hasRelease
hasDefect
hasDocument
```

Rules:

```text
1. Approved requirements with no implementation link flagged.
2. Implemented requirements with no test link flagged.
3. Verified requirements without passing test/evidence blocked if policy enabled.
4. Defect-linked requirements highlighted.
```

---

## 6.18 TRC-004 — Requirement impact analysis

Classification: `MUST_IMPLEMENT_IN_PHASE_28`

Purpose:

```text
Show what might be affected if requirement changes.
```

Impact includes:

```text
scope items
deliverables
tasks
screens
actions
APIs
data entities
test cases
defects
release packages
documents
decisions
change requests
baseline
```

Rules:

```text
1. Impact analysis is read-only.
2. Changing approved/baselined requirement requires ChangeRequest.
3. Impact results masked by permission.
4. AI may summarize impact as proposal/explanation only.
```

---

## 6.19 TRC-005 — Gap detection

Classification: `MUST_IMPLEMENT_IN_PHASE_28`

Gaps:

```text
approved requirement without task
requirement without test
test without requirement
screen action without API
API without test
deliverable without requirement
defect without requirement/deliverable link
release item without requirement trace
requirement changed after baseline without ChangeRequest
```

Rules:

```text
1. Gap report should be generated from trace links.
2. High-priority gaps can create RAID item/notification.
3. Gap report does not mutate data unless user creates action.
```

---

## 6.20 AI-001 — AI-assisted requirement and trace suggestions

Classification: `SEED_ONLY_IN_PHASE_28` or `MUST_IMPLEMENT_IN_PHASE_28` if Phase 21 tool registry exists.

AI can suggest:

```text
requirements from scope/documents
acceptance criteria
screen list from requirements
API endpoints from screen actions
test coverage gaps
trace links
impact summary
requirement wording improvements
```

Rules:

```text
1. AI suggestions are proposal only.
2. Human must approve/apply trace links or requirements.
3. AI cannot mark requirement approved/verified.
4. AI cannot create tasks/tests without human apply.
5. AI must respect document/project/finance/quote permissions.
```

---

# 7. Entity model TO-BE

---

## 7.1 Application — `app_registry_application`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
code VARCHAR(100) NOT NULL
name VARCHAR(255) NOT NULL
description TEXT NULL
application_type VARCHAR(50) NOT NULL
status VARCHAR(50) NOT NULL
owner_user_id UUID NULL
client_visible BOOLEAN NOT NULL DEFAULT false
created_at / created_by
updated_at / updated_by
archived_at / archived_by
trace_id VARCHAR(100) NULL
version INT
```

Constraint:

```text
unique workspace_id + code
```

---

## 7.2 ApplicationProjectMapping — `app_registry_project_mapping`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
application_id UUID NOT NULL
project_id UUID NOT NULL
mapping_type VARCHAR(50) NOT NULL
created_at / created_by
archived_at / archived_by
version INT
```

Constraint:

```text
unique active application_id + project_id + mapping_type
```

---

## 7.3 ApplicationModule — `app_registry_module`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
application_id UUID NOT NULL
parent_module_id UUID NULL
code VARCHAR(100) NOT NULL
name VARCHAR(255) NOT NULL
description TEXT NULL
status VARCHAR(50) NOT NULL
sort_order INT NULL
created_at / created_by
updated_at / updated_by
archived_at / archived_by
version INT
```

Constraint:

```text
unique application_id + code
```

---

## 7.4 ApplicationComponent — `app_registry_component`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
application_id UUID NOT NULL
module_id UUID NULL
code VARCHAR(100) NULL
name VARCHAR(255) NOT NULL
component_type VARCHAR(50) NOT NULL
description TEXT NULL
status VARCHAR(50) NOT NULL
technical_reference VARCHAR(500) NULL
created_at / created_by
updated_at / updated_by
archived_at / archived_by
version INT
```

---

## 7.5 ApplicationScreen — `app_registry_screen`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
application_id UUID NOT NULL
module_id UUID NULL
code VARCHAR(100) NULL
name VARCHAR(255) NOT NULL
screen_type VARCHAR(50) NOT NULL
route_path VARCHAR(500) NULL
description TEXT NULL
status VARCHAR(50) NOT NULL
owner_user_id UUID NULL
created_at / created_by
updated_at / updated_by
archived_at / archived_by
version INT
```

Constraint:

```text
unique application_id + code where code not null
```

---

## 7.6 ScreenSection — `app_registry_screen_section`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
screen_id UUID NOT NULL
code VARCHAR(100) NULL
name VARCHAR(255) NOT NULL
description TEXT NULL
sort_order INT NOT NULL DEFAULT 0
created_at / created_by
updated_at / updated_by
archived_at / archived_by
version INT
```

---

## 7.7 ScreenField — `app_registry_screen_field`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
screen_id UUID NOT NULL
section_id UUID NULL
field_key VARCHAR(150) NOT NULL
label VARCHAR(255) NOT NULL
field_type VARCHAR(50) NOT NULL
required BOOLEAN NOT NULL DEFAULT false
sensitive BOOLEAN NOT NULL DEFAULT false
validation_json JSONB NULL
description TEXT NULL
sort_order INT NULL
created_at / created_by
updated_at / updated_by
archived_at / archived_by
version INT
```

Constraint:

```text
unique screen_id + field_key
```

---

## 7.8 ScreenAction — `app_registry_screen_action`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
screen_id UUID NOT NULL
code VARCHAR(100) NOT NULL
label VARCHAR(255) NOT NULL
action_type VARCHAR(50) NOT NULL
api_endpoint_id UUID NULL
permission_code VARCHAR(150) NULL
description TEXT NULL
sort_order INT NULL
created_at / created_by
updated_at / updated_by
archived_at / archived_by
version INT
```

Constraint:

```text
unique screen_id + code
```

---

## 7.9 ApiEndpointRegistry — `app_registry_api_endpoint`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
application_id UUID NOT NULL
module_id UUID NULL
code VARCHAR(150) NULL
http_method VARCHAR(20) NOT NULL
path VARCHAR(500) NOT NULL
operation_name VARCHAR(255) NULL
description TEXT NULL
status VARCHAR(50) NOT NULL
auth_required BOOLEAN NOT NULL DEFAULT true
permission_code VARCHAR(150) NULL
request_schema_ref VARCHAR(500) NULL
response_schema_ref VARCHAR(500) NULL
source VARCHAR(50) NULL
created_at / created_by
updated_at / updated_by
archived_at / archived_by
version INT
```

Constraint:

```text
unique application_id + http_method + path
```

---

## 7.10 DataEntityRegistry — `app_registry_data_entity`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
application_id UUID NOT NULL
module_id UUID NULL
code VARCHAR(150) NOT NULL
name VARCHAR(255) NOT NULL
entity_type VARCHAR(50) NOT NULL
description TEXT NULL
sensitive BOOLEAN NOT NULL DEFAULT false
schema_json JSONB NULL
status VARCHAR(50) NOT NULL
created_at / created_by
updated_at / updated_by
archived_at / archived_by
version INT
```

Constraint:

```text
unique application_id + code
```

---

## 7.11 Requirement — `requirements_requirement`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
project_id UUID NULL
application_id UUID NULL
parent_requirement_id UUID NULL
code VARCHAR(100) NOT NULL
title VARCHAR(255) NOT NULL
description TEXT NULL
requirement_type VARCHAR(50) NOT NULL
priority VARCHAR(50) NOT NULL
status VARCHAR(50) NOT NULL
owner_user_id UUID NULL
current_version_id UUID NULL
source_summary TEXT NULL
created_at / created_by
updated_at / updated_by
approved_at TIMESTAMP NULL
approved_by UUID NULL
archived_at / archived_by
trace_id VARCHAR(100) NULL
version INT
```

Constraint:

```text
unique workspace_id + project_id + code where project_id not null
unique workspace_id + application_id + code where application_id not null
```

---

## 7.12 RequirementVersion — `requirements_requirement_version`

Fields:

```text
id UUID PK
requirement_id UUID NOT NULL
workspace_id UUID NOT NULL
project_id UUID NULL
application_id UUID NULL
version_number INT NOT NULL
title VARCHAR(255) NOT NULL
description TEXT NULL
requirement_type VARCHAR(50) NOT NULL
priority VARCHAR(50) NOT NULL
status VARCHAR(50) NOT NULL
rationale TEXT NULL
acceptance_notes TEXT NULL
change_summary TEXT NULL
created_at / created_by
approved_at TIMESTAMP NULL
approved_by UUID NULL
archived_at / archived_by
version INT
```

Constraint:

```text
unique requirement_id + version_number
```

---

## 7.13 RequirementSource — `requirements_requirement_source`

Fields:

```text
id UUID PK
requirement_id UUID NOT NULL
workspace_id UUID NOT NULL
project_id UUID NULL
source_type VARCHAR(50) NOT NULL
source_id UUID NULL
source_text TEXT NULL
source_url VARCHAR(1000) NULL
created_at / created_by
archived_at / archived_by
version INT
```

---

## 7.14 RequirementAcceptanceCriteria — `requirements_acceptance_criteria`

Fields:

```text
id UUID PK
requirement_id UUID NOT NULL
workspace_id UUID NOT NULL
project_id UUID NULL
title VARCHAR(255) NOT NULL
description TEXT NOT NULL
mandatory BOOLEAN NOT NULL DEFAULT true
status VARCHAR(50) NOT NULL
linked_project_acceptance_criteria_id UUID NULL
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

## 7.15 TraceLink — `traceability_link`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
project_id UUID NULL
source_type VARCHAR(100) NOT NULL
source_id UUID NOT NULL
target_type VARCHAR(100) NOT NULL
target_id UUID NOT NULL
link_type VARCHAR(50) NOT NULL
confidence VARCHAR(50) NULL
source_ai_suggestion_id UUID NULL
notes TEXT NULL
created_at / created_by
archived_at / archived_by
trace_id VARCHAR(100) NULL
version INT
```

Constraint:

```text
unique active source_type + source_id + target_type + target_id + link_type
```

---

## 7.16 TraceabilitySnapshot — optional `traceability_snapshot`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
project_id UUID NULL
snapshot_type VARCHAR(100) NOT NULL
filters_json JSONB NULL
matrix_json JSONB NOT NULL
gap_summary_json JSONB NULL
created_at TIMESTAMP NOT NULL
created_by UUID NULL
trace_id VARCHAR(100) NULL
```

Used for reports/export snapshots.

---

# 8. API TO-BE list

All APIs use `/api`.

---

## 8.1 Application APIs

```text
POST  /api/workspaces/{workspaceId}/applications
GET   /api/workspaces/{workspaceId}/applications
GET   /api/workspaces/{workspaceId}/applications/{applicationId}
PUT   /api/workspaces/{workspaceId}/applications/{applicationId}
PATCH /api/workspaces/{workspaceId}/applications/{applicationId}/archive

POST   /api/workspaces/{workspaceId}/applications/{applicationId}/projects
GET    /api/workspaces/{workspaceId}/applications/{applicationId}/projects
DELETE /api/workspaces/{workspaceId}/applications/{applicationId}/projects/{mappingId}
```

---

## 8.2 Application module/component APIs

```text
POST  /api/workspaces/{workspaceId}/applications/{applicationId}/modules
GET   /api/workspaces/{workspaceId}/applications/{applicationId}/modules
GET   /api/workspaces/{workspaceId}/application-modules/{moduleId}
PUT   /api/workspaces/{workspaceId}/application-modules/{moduleId}
PATCH /api/workspaces/{workspaceId}/application-modules/{moduleId}/archive

POST  /api/workspaces/{workspaceId}/applications/{applicationId}/components
GET   /api/workspaces/{workspaceId}/applications/{applicationId}/components
GET   /api/workspaces/{workspaceId}/application-components/{componentId}
PUT   /api/workspaces/{workspaceId}/application-components/{componentId}
PATCH /api/workspaces/{workspaceId}/application-components/{componentId}/archive
```

---

## 8.3 Screen registry APIs

```text
POST  /api/workspaces/{workspaceId}/applications/{applicationId}/screens
GET   /api/workspaces/{workspaceId}/applications/{applicationId}/screens
GET   /api/workspaces/{workspaceId}/screens/{screenId}
PUT   /api/workspaces/{workspaceId}/screens/{screenId}
PATCH /api/workspaces/{workspaceId}/screens/{screenId}/archive

POST  /api/workspaces/{workspaceId}/screens/{screenId}/sections
GET   /api/workspaces/{workspaceId}/screens/{screenId}/sections
PUT   /api/workspaces/{workspaceId}/screens/{screenId}/sections/{sectionId}
PATCH /api/workspaces/{workspaceId}/screens/{screenId}/sections/{sectionId}/archive

POST  /api/workspaces/{workspaceId}/screens/{screenId}/fields
GET   /api/workspaces/{workspaceId}/screens/{screenId}/fields
PUT   /api/workspaces/{workspaceId}/screens/{screenId}/fields/{fieldId}
PATCH /api/workspaces/{workspaceId}/screens/{screenId}/fields/{fieldId}/archive

POST  /api/workspaces/{workspaceId}/screens/{screenId}/actions
GET   /api/workspaces/{workspaceId}/screens/{screenId}/actions
PUT   /api/workspaces/{workspaceId}/screens/{screenId}/actions/{actionId}
PATCH /api/workspaces/{workspaceId}/screens/{screenId}/actions/{actionId}/archive
```

---

## 8.4 API/data entity registry APIs

```text
POST  /api/workspaces/{workspaceId}/applications/{applicationId}/api-endpoints
GET   /api/workspaces/{workspaceId}/applications/{applicationId}/api-endpoints
GET   /api/workspaces/{workspaceId}/api-endpoints/{endpointId}
PUT   /api/workspaces/{workspaceId}/api-endpoints/{endpointId}
PATCH /api/workspaces/{workspaceId}/api-endpoints/{endpointId}/archive

POST  /api/workspaces/{workspaceId}/applications/{applicationId}/data-entities
GET   /api/workspaces/{workspaceId}/applications/{applicationId}/data-entities
GET   /api/workspaces/{workspaceId}/data-entities/{dataEntityId}
PUT   /api/workspaces/{workspaceId}/data-entities/{dataEntityId}
PATCH /api/workspaces/{workspaceId}/data-entities/{dataEntityId}/archive
```

---

## 8.5 Requirement APIs

```text
POST  /api/projects/{projectId}/requirements
GET   /api/projects/{projectId}/requirements
GET   /api/projects/{projectId}/requirements/{requirementId}
PUT   /api/projects/{projectId}/requirements/{requirementId}
POST  /api/projects/{projectId}/requirements/{requirementId}/submit-review
POST  /api/projects/{projectId}/requirements/{requirementId}/approve
POST  /api/projects/{projectId}/requirements/{requirementId}/reject
POST  /api/projects/{projectId}/requirements/{requirementId}/mark-implemented
POST  /api/projects/{projectId}/requirements/{requirementId}/mark-verified
POST  /api/projects/{projectId}/requirements/{requirementId}/defer
POST  /api/projects/{projectId}/requirements/{requirementId}/supersede
PATCH /api/projects/{projectId}/requirements/{requirementId}/archive

POST  /api/projects/{projectId}/requirements/{requirementId}/versions
GET   /api/projects/{projectId}/requirements/{requirementId}/versions
POST  /api/projects/{projectId}/requirements/{requirementId}/versions/{versionId}/approve
```

Workspace/application-level requirements:

```text
POST /api/workspaces/{workspaceId}/applications/{applicationId}/requirements
GET  /api/workspaces/{workspaceId}/applications/{applicationId}/requirements
```

---

## 8.6 Requirement source/criteria APIs

```text
POST   /api/projects/{projectId}/requirements/{requirementId}/sources
GET    /api/projects/{projectId}/requirements/{requirementId}/sources
DELETE /api/projects/{projectId}/requirements/{requirementId}/sources/{sourceId}

POST  /api/projects/{projectId}/requirements/{requirementId}/acceptance-criteria
GET   /api/projects/{projectId}/requirements/{requirementId}/acceptance-criteria
PUT   /api/projects/{projectId}/requirements/{requirementId}/acceptance-criteria/{criteriaId}
PATCH /api/projects/{projectId}/requirements/{requirementId}/acceptance-criteria/{criteriaId}/archive
```

---

## 8.7 Trace link APIs

```text
POST   /api/projects/{projectId}/trace-links
GET    /api/projects/{projectId}/trace-links
GET    /api/projects/{projectId}/trace-links/{traceLinkId}
DELETE /api/projects/{projectId}/trace-links/{traceLinkId}

GET /api/projects/{projectId}/trace-links/by-source?sourceType=REQUIREMENT&sourceId=...
GET /api/projects/{projectId}/trace-links/by-target?targetType=TEST_CASE&targetId=...
```

Create request:

```json
{
  "sourceType": "REQUIREMENT",
  "sourceId": "uuid",
  "targetType": "TEST_CASE",
  "targetId": "uuid",
  "linkType": "VERIFIES",
  "notes": "Test validates acceptance criteria for login flow"
}
```

---

## 8.8 Traceability matrix/report APIs

```text
GET  /api/projects/{projectId}/traceability/matrix
GET  /api/projects/{projectId}/traceability/coverage
GET  /api/projects/{projectId}/traceability/gaps
GET  /api/projects/{projectId}/traceability/impact-analysis
POST /api/projects/{projectId}/traceability/snapshots
GET  /api/projects/{projectId}/traceability/snapshots
GET  /api/projects/{projectId}/traceability/snapshots/{snapshotId}
```

Impact query example:

```text
GET /api/projects/{projectId}/traceability/impact-analysis?sourceType=REQUIREMENT&sourceId=...
```

---

## 8.9 AI suggestion APIs

If Phase 21 tool registry exists:

```text
POST /api/projects/{projectId}/ai-planning/suggest-requirements
POST /api/projects/{projectId}/ai-planning/suggest-trace-links
POST /api/projects/{projectId}/ai-planning/summarize-requirement-impact
```

These should create AIPlanningSuggestion items, not direct mutations.

---

# 9. Authorization requirements

Required authorities:

```text
APPLICATION_VIEW
APPLICATION_CREATE
APPLICATION_UPDATE
APPLICATION_ARCHIVE
APPLICATION_PROJECT_MAPPING_MANAGE

APPLICATION_MODULE_VIEW
APPLICATION_MODULE_CREATE
APPLICATION_MODULE_UPDATE
APPLICATION_MODULE_ARCHIVE

APPLICATION_COMPONENT_VIEW
APPLICATION_COMPONENT_CREATE
APPLICATION_COMPONENT_UPDATE
APPLICATION_COMPONENT_ARCHIVE

SCREEN_VIEW
SCREEN_CREATE
SCREEN_UPDATE
SCREEN_ARCHIVE
SCREEN_SECTION_MANAGE
SCREEN_FIELD_MANAGE
SCREEN_ACTION_MANAGE

API_ENDPOINT_VIEW
API_ENDPOINT_CREATE
API_ENDPOINT_UPDATE
API_ENDPOINT_ARCHIVE
DATA_ENTITY_VIEW
DATA_ENTITY_CREATE
DATA_ENTITY_UPDATE
DATA_ENTITY_ARCHIVE

REQUIREMENT_VIEW
REQUIREMENT_CREATE
REQUIREMENT_UPDATE
REQUIREMENT_SUBMIT_REVIEW
REQUIREMENT_APPROVE
REQUIREMENT_REJECT
REQUIREMENT_MARK_IMPLEMENTED
REQUIREMENT_MARK_VERIFIED
REQUIREMENT_DEFER
REQUIREMENT_SUPERSEDE
REQUIREMENT_ARCHIVE
REQUIREMENT_VERSION_CREATE
REQUIREMENT_VERSION_APPROVE
REQUIREMENT_SOURCE_MANAGE
REQUIREMENT_CRITERIA_MANAGE

TRACE_LINK_VIEW
TRACE_LINK_CREATE
TRACE_LINK_DELETE
TRACEABILITY_MATRIX_VIEW
TRACEABILITY_COVERAGE_VIEW
TRACEABILITY_IMPACT_VIEW
TRACEABILITY_SNAPSHOT_CREATE
TRACEABILITY_SNAPSHOT_VIEW

TRACEABILITY_REPORT_VIEW
TRACEABILITY_EXPORT
```

Rules:

```text
1. Workspace access required for application registry.
2. Project access required for project requirements.
3. Requirement approval requires stronger permission.
4. Verified status requires test/acceptance permission if policy enabled.
5. Trace link creation requires access to both source and target.
6. Trace link does not grant access.
7. Sensitive targets are masked in reports.
```

---

# 10. Lifecycle rules

## 10.1 Requirement lifecycle

```text
DRAFT → PROPOSED → UNDER_REVIEW → APPROVED
APPROVED → IN_IMPLEMENTATION → IMPLEMENTED → VERIFIED
DRAFT/PROPOSED/UNDER_REVIEW → REJECTED
APPROVED/IN_IMPLEMENTATION → DEFERRED
APPROVED/IMPLEMENTED/VERIFIED → SUPERSEDED
Any non-final → ARCHIVED
```

Rules:

```text
1. DRAFT can be edited.
2. APPROVED requirement requires new version for material change.
3. VERIFIED requires coverage and passing evidence if policy enabled.
4. SUPERSEDED requires replacement requirement/version.
5. Requirement changed after baseline requires ChangeRequest.
```

## 10.2 Screen/API lifecycle

```text
PLANNED → DESIGNED → IN_DEVELOPMENT → IMPLEMENTED → TESTED → RELEASED
Any → DEPRECATED / ARCHIVED
```

Rules:

```text
1. Released screens/APIs can be deprecated, not silently deleted.
2. API path changes after baseline should create requirement/change impact.
3. Screen action permission changes are sensitive and audited.
```

## 10.3 TraceLink lifecycle

```text
ACTIVE → ARCHIVED
```

Rules:

```text
1. TraceLink creation/deletion audited.
2. Archived trace links preserved for history.
3. Bulk trace operations require idempotency/audit.
```

---

# 11. Baseline / ChangeRequest integration

Controlled after baseline:

```text
Approved requirement change
Requirement status verified/superseded/deferred
Screen/API changes linked to approved scope
Trace links that affect acceptance/release coverage
Requirement acceptance criteria changes
```

Rules:

```text
1. Material approved requirement changes require ChangeRequest.
2. ChangeRequest impact should include requirement/screen/API/test impact.
3. Approved ChangeRequest apply can create requirement version or trace links.
4. Baseline snapshot should include requirement and traceability sections in future baselines.
```

---

# 12. Quality / Release integration

Phase 26 integration:

```text
Requirement → TestCase coverage
Requirement → TestRun pass/fail status
Requirement → Defect impact
Requirement → ReleasePackage inclusion
```

Rules:

```text
1. Requirement cannot be VERIFIED if mandatory tests fail.
2. Requirement with open blocker defect cannot be VERIFIED unless waived.
3. Release readiness can include requirement coverage checks.
4. Traceability reports support quality gates.
```

---

# 13. Scope / Deliverable integration

Phase 24 integration:

```text
ScopeItem → Requirement
Deliverable → Requirement
AcceptanceCriteria → RequirementAcceptanceCriteria
AcceptanceEvidence → Requirement verification support
```

Rules:

```text
1. ScopeItem without requirements can be flagged.
2. Deliverable without requirement trace can be flagged.
3. Requirement without deliverable/scope trace can be flagged.
```

---

# 14. Document integration

Phase 27 integration:

```text
Document → Requirement source
Document → Requirement evidence
Document → Screen/API spec
Generated proposal/report → source trace
```

Rules:

```text
1. Requirement can be derived from document.
2. Document access still controlled.
3. Full document semantic extraction/RAG deferred to Phase 41.
```

---

# 15. RAID / Decision integration

Phase 25 integration:

```text
DecisionRecord → Requirement rationale
RaidItem → Requirement risk/issue
Assumption → Requirement assumption/source
```

Rules:

```text
1. Decision can approve direction but does not approve requirement unless action performed.
2. Critical RAID item can block requirement verification/release if policy enabled.
```

---

# 16. Reporting integration

Extend Phase 22 with:

```text
APPLICATION_INVENTORY_REPORT
REQUIREMENT_STATUS_REPORT
REQUIREMENT_COVERAGE_REPORT
REQUIREMENT_TRACEABILITY_MATRIX
REQUIREMENT_GAP_REPORT
REQUIREMENT_IMPACT_ANALYSIS_REPORT
SCREEN_REGISTRY_REPORT
API_ENDPOINT_REGISTRY_REPORT
TEST_COVERAGE_BY_REQUIREMENT_REPORT
DEFECTS_BY_REQUIREMENT_REPORT
RELEASE_REQUIREMENT_CONTENT_REPORT
```

Dashboard KPIs:

```text
totalRequirements
approvedRequirements
implementedRequirements
verifiedRequirements
requirementsWithoutTasks
requirementsWithoutTests
requirementsWithOpenDefects
screensWithoutRequirements
apisWithoutTests
traceabilityCoveragePercent
```

---

# 17. Notification integration

Notifications for:

```text
requirement submitted for review
requirement approved/rejected
requirement changed after baseline
requirement missing coverage
critical gap detected
trace link created/removed for critical requirement
screen/API deprecated
requirement verified
```

Recipients:

```text
requirement owner
project manager
project watchers
quality owner
deliverable owner
change watchers
```

No external client notification unless client portal exists.

---

# 18. AI integration

AI can propose:

```text
requirements from scope/document text
requirement decomposition
acceptance criteria
screen registry from requirements
API endpoint suggestions
trace link suggestions
coverage gap explanation
impact summary
test case suggestions
```

Rules:

```text
1. AI output is proposal only.
2. Human must approve/apply.
3. AI cannot approve requirements.
4. AI cannot mark verified.
5. AI cannot create release content automatically.
6. AI respects document/project permissions.
```

---

# 19. Event Registry integration

Recommended source system:

```text
SCOPERY_TRACEABILITY
```

Required events:

```text
APPLICATION_CREATED
APPLICATION_UPDATED
APPLICATION_ARCHIVED
APPLICATION_PROJECT_LINKED
APPLICATION_PROJECT_UNLINKED

APPLICATION_MODULE_CREATED
APPLICATION_MODULE_UPDATED
APPLICATION_MODULE_ARCHIVED
APPLICATION_COMPONENT_CREATED
APPLICATION_COMPONENT_UPDATED
APPLICATION_COMPONENT_ARCHIVED

SCREEN_CREATED
SCREEN_UPDATED
SCREEN_STATUS_CHANGED
SCREEN_ARCHIVED
SCREEN_SECTION_CREATED
SCREEN_FIELD_CREATED
SCREEN_ACTION_CREATED

API_ENDPOINT_REGISTERED
API_ENDPOINT_UPDATED
API_ENDPOINT_DEPRECATED
DATA_ENTITY_REGISTERED
DATA_ENTITY_UPDATED
DATA_ENTITY_ARCHIVED

REQUIREMENT_CREATED
REQUIREMENT_UPDATED
REQUIREMENT_SUBMITTED_REVIEW
REQUIREMENT_APPROVED
REQUIREMENT_REJECTED
REQUIREMENT_IMPLEMENTED
REQUIREMENT_VERIFIED
REQUIREMENT_DEFERRED
REQUIREMENT_SUPERSEDED
REQUIREMENT_ARCHIVED
REQUIREMENT_VERSION_CREATED
REQUIREMENT_VERSION_APPROVED
REQUIREMENT_SOURCE_ADDED
REQUIREMENT_CRITERIA_CREATED

TRACE_LINK_CREATED
TRACE_LINK_ARCHIVED
TRACEABILITY_GAP_DETECTED
TRACEABILITY_SNAPSHOT_CREATED
REQUIREMENT_IMPACT_ANALYSIS_RUN
```

Standard variables:

```text
actor.userId
organization.id
workspace.id
project.id
application.id
module.id
screen.id
apiEndpoint.id
dataEntity.id
requirement.id
requirement.code
requirement.status
traceLink.id
source.type
source.id
target.type
target.id
occurredAt
traceId
```

---

# 20. Audit / activity / outbox

Audit-sensitive actions:

```text
requirement approved/rejected/verified/superseded
requirement version approved
trace link created/deleted for approved requirement
screen action permission changed
API endpoint deprecated/removed
requirement changed after baseline
gap waiver if implemented
```

Activity actions:

```text
REQUIREMENT_APPROVED
REQUIREMENT_VERIFIED
TRACE_LINK_CREATED
TRACEABILITY_GAP_DETECTED
SCREEN_STATUS_CHANGED
API_ENDPOINT_DEPRECATED
```

Outbox required for major traceability events.

Idempotency recommended for:

```text
POST /requirements
POST /requirements/{id}/approve
POST /requirements/{id}/versions
POST /trace-links
POST /traceability/snapshots
bulk import endpoints if implemented
```

---

# 21. Business rules master

## 21.1 Application registry rules

```text
APP-001 Application code unique within workspace.
APP-002 Application must belong to workspace.
APP-003 Project mapping requires same workspace.
APP-004 Module code unique within application.
APP-005 Screen code unique within application if provided.
APP-006 API endpoint method+path unique within application.
APP-007 Archived application remains auditable.
```

## 21.2 Requirement rules

```text
REQ-001 Requirement title required.
REQ-002 Requirement code unique in project/application scope.
REQ-003 Approved requirement immutable except new version/change flow.
REQ-004 Verified requirement requires coverage if policy enabled.
REQ-005 Requirement source target must belong to same project/workspace.
REQ-006 Requirement changed after baseline requires ChangeRequest.
REQ-007 Superseded requirement requires replacement.
```

## 21.3 Traceability rules

```text
TRC-001 Trace source/target must exist.
TRC-002 Source/target must be compatible workspace/project.
TRC-003 Duplicate active trace link prevented.
TRC-004 Trace link does not grant access.
TRC-005 Sensitive target masked in matrix/report.
TRC-006 Archived links retained for history.
TRC-007 Coverage gaps are reported, not auto-mutated.
```

## 21.4 Screen/API rules

```text
SCR-001 Screen belongs to application.
SCR-002 Screen action can reference API endpoint.
SCR-003 Sensitive fields flagged.
SCR-004 Action permission changes audited.
API-001 API path/method required.
API-002 API registry does not replace controller/OpenAPI.
API-003 Deprecated endpoint remains traceable.
```

---

# 22. Error catalog

```text
APPLICATION_NOT_FOUND
APPLICATION_CODE_ALREADY_EXISTS
APPLICATION_ARCHIVED
APPLICATION_PROJECT_MAPPING_NOT_FOUND
APPLICATION_PROJECT_WORKSPACE_MISMATCH
APPLICATION_ACCESS_DENIED

APPLICATION_MODULE_NOT_FOUND
APPLICATION_MODULE_CODE_ALREADY_EXISTS
APPLICATION_MODULE_APPLICATION_MISMATCH
APPLICATION_COMPONENT_NOT_FOUND
APPLICATION_COMPONENT_APPLICATION_MISMATCH

SCREEN_NOT_FOUND
SCREEN_CODE_ALREADY_EXISTS
SCREEN_APPLICATION_MISMATCH
SCREEN_SECTION_NOT_FOUND
SCREEN_FIELD_NOT_FOUND
SCREEN_FIELD_KEY_ALREADY_EXISTS
SCREEN_ACTION_NOT_FOUND
SCREEN_ACTION_CODE_ALREADY_EXISTS
SCREEN_ACTION_ENDPOINT_MISMATCH
SCREEN_ACCESS_DENIED

API_ENDPOINT_NOT_FOUND
API_ENDPOINT_DUPLICATE
API_ENDPOINT_INVALID_PATH
API_ENDPOINT_ACCESS_DENIED
DATA_ENTITY_NOT_FOUND
DATA_ENTITY_CODE_ALREADY_EXISTS
DATA_ENTITY_ACCESS_DENIED

REQUIREMENT_NOT_FOUND
REQUIREMENT_CODE_ALREADY_EXISTS
REQUIREMENT_INVALID_STATUS
REQUIREMENT_APPROVED_IMMUTABLE
REQUIREMENT_VERSION_NOT_FOUND
REQUIREMENT_VERSION_DUPLICATE
REQUIREMENT_SOURCE_NOT_FOUND
REQUIREMENT_SOURCE_TARGET_MISMATCH
REQUIREMENT_CRITERIA_NOT_FOUND
REQUIREMENT_VERIFICATION_BLOCKED
REQUIREMENT_SUPERSEDE_REPLACEMENT_REQUIRED
REQUIREMENT_BASELINE_CHANGE_REQUIRED
REQUIREMENT_ACCESS_DENIED

TRACE_LINK_NOT_FOUND
TRACE_LINK_DUPLICATE
TRACE_LINK_SOURCE_NOT_FOUND
TRACE_LINK_TARGET_NOT_FOUND
TRACE_LINK_TARGET_MISMATCH
TRACE_LINK_ACCESS_DENIED
TRACEABILITY_MATRIX_ACCESS_DENIED
TRACEABILITY_IMPACT_ACCESS_DENIED
```

---

# 23. Required tests

## 23.1 Application registry tests

```text
createApplication_valid_success
createApplication_duplicateCode_rejected
archiveApplication_success
linkApplicationToProject_success
linkApplicationToProject_otherWorkspace_rejected
createApplicationModule_valid_success
createApplicationModule_duplicateCode_rejected
createComponent_valid_success
```

## 23.2 Screen/API registry tests

```text
createScreen_valid_success
createScreen_duplicateCode_rejected
createScreenSection_valid_success
createScreenField_sensitive_success
createScreenAction_withEndpoint_success
createScreenAction_endpointOtherApplication_rejected
registerApiEndpoint_valid_success
registerApiEndpoint_duplicateMethodPath_rejected
registerApiEndpoint_invalidApiV1Path_warningOrRejectedByPolicy
registerDataEntity_valid_success
```

## 23.3 Requirement tests

```text
createRequirement_valid_success
createRequirement_duplicateCode_rejected
approveRequirement_success
updateApprovedRequirement_rejected
createRequirementVersion_success
approveRequirementVersion_success
supersedeRequirement_requiresReplacement
verifyRequirement_withoutCoverage_blockedIfPolicyEnabled
markImplemented_success
deferRequirement_success
postBaselineRequirementChange_requiresChangeRequest
```

## 23.4 Requirement source/criteria tests

```text
addRequirementSource_scopeItem_success
addRequirementSource_document_success
addRequirementSource_otherProject_rejected
createRequirementCriteria_valid_success
linkRequirementCriteriaToProjectAcceptanceCriteria_success
```

## 23.5 Trace link tests

```text
createTraceLink_requirementToTask_success
createTraceLink_requirementToTestCase_success
createTraceLink_requirementToScreen_success
createTraceLink_duplicate_rejected
createTraceLink_targetOtherProject_rejected
deleteTraceLink_archives
traceLinkDoesNotGrantAccess
```

## 23.6 Traceability matrix/report tests

```text
traceabilityMatrix_valid_success
traceabilityMatrix_masksUnauthorizedFinanceQuoteTargets
coverageReport_flagsRequirementWithoutTask
coverageReport_flagsRequirementWithoutTest
gapReport_flagsApiWithoutTest
impactAnalysis_requirementShowsScreensTasksTestsDefectsRelease
snapshotTraceability_success
```

## 23.7 Integration tests

```text
releaseReadinessBlocksUnverifiedRequirement_ifPolicyEnabled
requirementWithOpenBlockerDefect_notVerified
scopeItemWithoutRequirement_flagged
deliverableWithoutRequirement_flagged
testCaseWithoutRequirement_flagged
changeRequestApplyCreatesRequirementVersion_ifConfigured
aiTraceSuggestion_isProposalOnly
```

## 23.8 Authorization tests

```text
viewApplication_withoutPermission_forbidden
createRequirement_withoutPermission_forbidden
approveRequirement_withoutPermission_forbidden
createTraceLink_withoutSourceAccess_forbidden
createTraceLink_withoutTargetAccess_forbidden
viewTraceabilityMatrix_withoutPermission_forbidden
crossWorkspaceApplication_forbidden
```

## 23.9 Event/audit tests

```text
traceabilityEventSeeder_firstRun_createsDefinitions
traceabilityEventSeeder_secondRun_noDuplicates
requirementApproved_eventEmitted
requirementVerified_eventEmitted
traceLinkCreated_eventEmitted
screenActionPermissionChanged_auditCreated
apiEndpointDeprecated_auditCreated
```

---

# 24. Manual verification checklist

Completion file must include:

```text
1. Create Application.
2. Link Application to Project.
3. Create ApplicationModule.
4. Create Screen with sections, fields, and actions.
5. Register API endpoint.
6. Create Requirement.
7. Add Requirement source from ScopeItem or Document.
8. Approve Requirement.
9. Link Requirement to Screen/API/Task/TestCase.
10. Generate traceability matrix.
11. Generate coverage report and confirm gaps.
12. Run impact analysis for a requirement.
13. Try to verify requirement without test coverage and confirm blocked if policy enabled.
14. Create baseline and confirm approved requirement material change requires ChangeRequest.
15. Confirm trace links do not grant access.
16. Confirm AI suggestions are proposal only.
17. Confirm no code generation/source scanner/semantic graph is falsely claimed.
```

---

# 25. Acceptance criteria

Phase 28 is accepted only if:

```text
1. Current application/requirements/traceability capability is classified against TO-BE.
2. Application registry implemented/tested.
3. Application module/component implemented/tested.
4. Screen/section/field/action registry implemented/tested.
5. API endpoint and data entity registry implemented/tested.
6. Requirement and RequirementVersion implemented/tested.
7. RequirementSource and RequirementAcceptanceCriteria implemented/tested.
8. TraceLink implemented/tested.
9. Traceability matrix implemented/tested.
10. Coverage/gap/impact reports implemented/tested.
11. Baseline/ChangeRequest integration implemented/tested.
12. Quality/release/scope/deliverable/document integration implemented/tested.
13. IAM permissions implemented/tested.
14. Event seeders idempotent.
15. Activity/audit/outbox follows Phase 04.
16. No code generation/source scanner/CI sync/semantic graph/client portal/workflow is falsely claimed.
17. `mvn compile` passes.
18. `mvn test` passes.
19. Completion file exists.
```

Do not mark complete if:

```text
approved requirements can be silently edited
verified requirements can skip mandatory coverage when policy says no
trace links allow cross-project unauthorized links
trace links grant access
matrix/export leaks sensitive linked data
API registry claims to replace real controllers/OpenAPI
screen registry claims to generate frontend code
tests fail
```

---

# 26. Required phase completion file

Agent must create:

```text
docs/phase-complete/PHASE_28_APPLICATION_REQUIREMENT_TRACEABILITY_TO_BE_COMPLETE.md
```

Required sections:

```text
# Phase 28 — Application / Requirement / Traceability Complete

## 1. Summary
## 2. Source Inputs Reviewed
## 3. Current vs TO-BE Classification Matrix
## 4. Implemented in Current BE
## 5. Implemented / Hardened in This Phase
## 6. Seed-only Items Added
## 7. Deferred Items and Target Phase
## 8. Traceability Boundary Decision
## 9. Entity Mapping
## 10. API Changes
## 11. Application Registry Strategy
## 12. Screen Registry Strategy
## 13. API / Data Entity Registry Strategy
## 14. Requirement Lifecycle Strategy
## 15. Requirement Versioning Strategy
## 16. Requirement Source / Criteria Strategy
## 17. TraceLink Strategy
## 18. Traceability Matrix Strategy
## 19. Coverage / Gap / Impact Strategy
## 20. Baseline / ChangeRequest Integration
## 21. Quality / Release Integration
## 22. Scope / Deliverable / Document Integration
## 23. AI Suggestion Strategy
## 24. Reporting Strategy
## 25. Notification / Event Strategy
## 26. Authorization Matrix
## 27. Activity / Audit / Outbox Notes
## 28. Idempotency Strategy
## 29. Tests Added
## 30. Commands Run
## 31. Test Results
## 32. Manual Verification
## 33. Assumptions
## 34. Deviations From Prompt
## 35. Known Risks
## 36. Future Phases That Must Return to Traceability
```

---

# 27. Future phases that must return

```text
Phase 30 — Customer / External Collaboration Portal:
- Client-visible requirements, UAT trace, external review.

Phase 31 — Meetings / Collaboration:
- Create requirements/decisions/actions from meetings.

Phase 34 — Workflow / Approval:
- Requirement approval workflow, trace gap waiver workflow.

Phase 35 — Advanced Notifications:
- Requirement review reminders, coverage gap digest.

Phase 36 — Contract / Billing / Revenue:
- Contracted requirements, billing milestones by requirement/deliverable.

Phase 38 — Audit / Compliance / Privacy:
- Compliance requirements, audit evidence, retention.

Phase 39 — Integration / Import / Export / API:
- OpenAPI import, Git/source scanner, Jira/Linear import, test automation sync.

Phase 41 — Data Quality / Knowledge Graph / Semantic Index:
- Semantic traceability, graph intelligence, document-to-requirement extraction.
```

---

# 28. Agent anti-bịa rules

The agent must not:

```text
1. Claim screen registry generates frontend code.
2. Claim API registry replaces real backend controllers.
3. Claim automatic source scanner exists.
4. Claim OpenAPI import exists unless implemented.
5. Claim semantic knowledge graph exists.
6. Claim AI can approve/verify requirements.
7. Allow approved requirements to be silently edited.
8. Allow trace links to grant access.
9. Allow cross-project trace links without validation.
10. Hide deferred integration/semantic/client portal/workflow gaps.
```

---

# 29. Prompt to give coding agent

```text
You are implementing Phase 28 — TO-BE Application Registry, Requirements Management, Screen/API Registry & End-to-End Traceability.

This is not an as-is documentation task.

Before coding, read:
- CLAUDE.md / CLAUDE.ms
- Coding_convention.md
- Phase 00–27 docs and completion files
- Current backend code, migrations, tests

Your task:
1. Compare current application/requirements/traceability capability against this Phase 28 TO-BE spec.
2. Classify each capability with required labels.
3. Implement Application, ApplicationProjectMapping, ApplicationModule, ApplicationComponent.
4. Implement ApplicationScreen, ScreenSection, ScreenField, ScreenAction.
5. Implement ApiEndpointRegistry and DataEntityRegistry.
6. Implement Requirement, RequirementVersion, RequirementSource, RequirementAcceptanceCriteria.
7. Implement TraceLink and traceability reports/matrix.
8. Integrate with Scope, Deliverable, Task, TestCase, Defect, Release, Document, RAID, Decision, Baseline, ChangeRequest, and AI proposal layer where available.
9. Add IAM permissions, events, notifications, reports, audit/outbox, idempotency, and tests.
10. Run mvn compile and mvn test.
11. Create docs/phase-complete/PHASE_28_APPLICATION_REQUIREMENT_TRACEABILITY_TO_BE_COMPLETE.md.

Do not implement or claim frontend code generation, backend controller generation, automatic source-code scanning, OpenAPI import, Jira/Linear sync, client portal review, workflow approval, semantic knowledge graph, or autonomous AI requirement approval unless those systems actually exist and are tested.
```

---

# 30. Quick tracking matrix

| Capability | Current backend | Phase 28 action | Later phase |
|---|---|---|---|
| Application | Missing/unknown | Must implement | — |
| ApplicationProjectMapping | Missing/unknown | Must implement | — |
| ApplicationModule | Missing/unknown | Must implement | — |
| ApplicationComponent | Missing/unknown | Must implement | — |
| ApplicationScreen | Missing/unknown | Must implement | — |
| ScreenSection | Missing/unknown | Must implement | — |
| ScreenField | Missing/unknown | Must implement | — |
| ScreenAction | Missing/unknown | Must implement | — |
| ApiEndpointRegistry | Missing/unknown | Must implement manual registry | OpenAPI import Phase 39 |
| DataEntityRegistry | Missing/unknown | Must implement | Semantic graph Phase 41 |
| Requirement | Missing/unknown | Must implement | Workflow Phase 34 |
| RequirementVersion | Missing/unknown | Must implement | — |
| RequirementSource | Missing/unknown | Must implement | Document extraction Phase 41 |
| RequirementAcceptanceCriteria | Missing/unknown | Must implement/map | Phase 24/26 integration |
| TraceLink | Missing/unknown | Must implement | Semantic graph Phase 41 |
| TraceabilityMatrix | Missing/unknown | Must implement | Advanced analytics Phase 41 |
| Gap/coverage reports | Missing/unknown | Must implement | Phase 35 digest |
| Impact analysis | Missing/unknown | Must implement | AI summary Phase 41 |
| OpenAPI import | Missing | Defer | Phase 39 |
| Source scanner | Missing | Defer | Phase 39 |
| Client review | Missing | Defer | Phase 30 |
| Workflow approval | Missing | Defer | Phase 34 |
| Semantic graph | Missing | Defer | Phase 41 |

---

# 31. Final principle

Phase 28 is not complete when "a requirement row can be stored."

Phase 28 is complete when Scopery can prove end-to-end traceability:

```text
application
+ requirement
+ screen/API/data entity
+ scope/deliverable
+ task
+ test
+ defect
+ release
+ document
+ decision/change history
= controlled product traceability
```

Requirement is not task.

Screen registry is not frontend generator.

API registry is not controller implementation.

Trace link is not permission.

Verified means covered and proven, not just marked.
