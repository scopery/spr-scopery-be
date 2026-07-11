# PHASE 11 — TO-BE Project Template, Phase Catalog, Template WBS & Template Task Foundation

> Project: Scopery Backend  
> Phase: 11  
> Document type: TO-BE implementation-grade specification  
> Status: Planning / Gap-driven implementation spec  
> Depends on: Phase 00 — Master Backend Roadmap, Phase 01 — API Path / Security Baseline, Phase 02 — IAM Core, Phase 03 — Workspace Core, Phase 04 — Platform Audit / Outbox / Idempotency, Phase 05 — Event Registry, Phase 06 — Notification Engine, Phase 07 — AI Agent Platform, Phase 08 — Knowledge / Document Type Catalog, Phase 09 — Project Core / Phase / WBS / Task, Phase 10 — Project Authorization Hardening  
> API base: `/api`  
> Primary module: `modules/project`  
> Related modules: `workspace`, `iam`, `eventregistry`, `notification`, future `capacity`, `scheduling`, `gantt`, `finance`, `quote`, `baseline`, `aiagent`, `document`, `reporting`  
> Important rule: This file is **not an as-is description**. It defines the TO-BE project template and phase catalog layer, compares it to the current Project module, and explicitly defers scheduling, Gantt, finance, quote, baseline, and AI planning features.

---

# 0. Purpose of this file

Phase 09 created the Project Core foundation:

```text
Project
ProjectPhase
PhaseDefinition
WBS
Task
TaskDependency
```

Phase 10 secured that foundation.

Phase 11 adds reusable project structure:

```text
ProjectTemplate
ProjectTemplateVersion
Template phases
Template WBS nodes
Template tasks
Template dependencies
PhaseDefinition catalog hardening
Workspace phase catalog
Template publish/archive lifecycle
Apply template to project
```

The goal is to allow Scopery to create projects consistently from approved templates.

Examples:

```text
Software implementation project
E-commerce website project
Mobile app project
ERP rollout project
Internal process improvement project
AI automation project
Maintenance/support project
```

Phase 11 is **structure reuse**, not scheduling/finance automation.

---

# 1. Source inputs

Before coding Phase 11, the agent must read:

```text
1. Current backend codebase
2. Phase 09 Project Core TO-BE spec and implementation
3. Phase 10 Project Authorization TO-BE spec and implementation
4. Phase 02 IAM TO-BE spec
5. Phase 03 Workspace TO-BE spec
6. Phase 04 Platform Audit/Outbox/Idempotency spec
7. Phase 05 Event Registry spec
8. Existing project module code, migrations, seeders, tests
9. Existing PhaseDefinition entity/service
10. Existing WBS/Task/Dependency actions
11. Existing workspace and IAM integration services
12. Existing DocumentType catalog if template deliverable type references are considered
```

The agent must not implement Phase 11 from current code only.

---

# 2. Current backend snapshot

Current Project module already has:

```text
Project
ProjectPhase
PhaseDefinition
WbsNode
Task
TaskDependency
```

Current PhaseDefinition exists as a basic catalog/reference.

Current project creation likely creates an empty project, and phases/WBS/tasks are added manually.

Phase 11 must not assume ProjectTemplate exists unless code confirms it.

Expected current state:

```text
PhaseDefinition: partially implemented
ProjectTemplate: missing or incomplete
ProjectTemplateVersion: missing
TemplatePhase: missing
TemplateWbsNode: missing
TemplateTask: missing
TemplateTaskDependency: missing
ApplyTemplate workflow: missing
```

The completion report must verify actual code and classify accurately.

---

# 3. Future-state capabilities related to Phase 11

Phase 11 overlaps several Work OS capability areas:

```text
Project Setup and Governance
Work Item and Backlog Management
Schedule/Gantt future
Resource/Capacity future
Estimation future
Finance future
Document/Knowledge future
AI-assisted planning future
```

Phase 11 delivers only the reusable structural foundation.

It must not deliver:

```text
capacity scheduling
date generation
Gantt projection
critical path
cost/margin roll-up
quote generation
baseline approval
change request workflow
AI-generated templates
document template generation
```

---

# 4. Classification labels

Every requirement uses one label:

| Label | Meaning |
|---|---|
| `CURRENTLY_IMPLEMENTED` | Current backend already implements it. Agent must verify and test. |
| `PARTIALLY_IMPLEMENTED` | Current backend implements part of it. |
| `MUST_IMPLEMENT_IN_PHASE_11` | Must be implemented or hardened now. |
| `SEED_ONLY_IN_PHASE_11` | Seed template/phase/event/permission definitions now; full use later. |
| `DEFERRED_TO_PHASE_XX` | Do not implement now; later phase must return. |
| `DEFERRED_TO_POST_23_BACKLOG` | Outside core 00–23 roadmap, part of full Work OS expansion. |
| `NOT_IN_SCOPE_FOR_BACKEND_NOW` | Explicitly not backend scope now. |

---

# 5. Phase 11 target statement

Phase 11 must deliver a future-ready project template system:

```text
1. PhaseDefinition catalog is hardened.
2. ProjectTemplate can be created under system/organization/workspace scope.
3. ProjectTemplate has versioning.
4. Published/active template versions are immutable.
5. Template phases define default project phase structure.
6. Template WBS nodes define default scope hierarchy.
7. Template tasks define default execution items.
8. Template task dependencies define default logical relationships.
9. Applying a template creates ProjectPhase/WBS/Task/TaskDependency records through Phase 09 rules.
10. Template application is idempotent or duplication-safe.
11. Template lifecycle is authorized, audited, and evented.
12. Future scheduling/finance/document/AI fields are clearly deferred.
```

---

# 6. Phase 11 scope decision

## 6.1 Must implement now

```text
PhaseDefinition hardening
Workspace/system phase catalog seed
ProjectTemplate entity
ProjectTemplateVersion entity
ProjectTemplatePhase entity
ProjectTemplateWbsNode entity
ProjectTemplateTask entity
ProjectTemplateTaskDependency entity
Template lifecycle: draft, published/active, archived
Template version immutability
Apply template to new or existing project
Template event definitions
Template permission seed/check
Tests
Completion report
```

## 6.2 Must not implement now

```text
Automatic scheduling from template
Task start/end date generation
Gantt generation
Capacity assignment
Rate card/cost estimate
Budget/margin
Quote/proposal generation
Baseline snapshot
Change request workflow
AI-generated template
Document content generation
Workflow approval for template publishing unless already simple
```

---

# 7. Core design decision

## 7.1 Template is not a project

A template is reusable structure.

A project is operational data.

Rules:

```text
ProjectTemplate does not contain live project state.
Template tasks are not real tasks.
Template WBS nodes are not real WBS nodes.
Template dependencies are not real task dependencies.
Applying template creates real project records.
```

## 7.2 Published template version immutability

Once a template version is published/active:

```text
It must not be modified directly.
Create a new version instead.
```

This protects historical project creation traceability.

## 7.3 Apply template through Project Core actions

Template application should use the same business rules as manual creation:

```text
Create ProjectPhase
Create WbsNode
Create Task
Create TaskDependency
```

Do not bypass Phase 09 validation.

---

# 8. TO-BE capability matrix

---

## 8.1 PTC-001 — PhaseDefinition catalog hardening

| Item | Value |
|---|---|
| Future capability | Maintain reusable phase definitions for projects and templates |
| Current state | PhaseDefinition exists |
| Phase 11 target | Harden full phase catalog |
| Classification | `MUST_IMPLEMENT_IN_PHASE_11` |

Required rules:

```text
1. PhaseDefinition code required.
2. Code normalized uppercase.
3. Code unique within scope.
4. Name required.
5. Scope required: SYSTEM / ORGANIZATION / WORKSPACE.
6. SYSTEM phase cannot have org/workspace id.
7. ORGANIZATION phase requires organizationId.
8. WORKSPACE phase requires workspaceId and organizationId.
9. Built-in phase definitions cannot be hard-deleted.
10. PhaseDefinition referenced by ProjectPhase or ProjectTemplatePhase cannot be hard-deleted.
11. Inactive/archived PhaseDefinition cannot be used for new ProjectPhase/TemplatePhase.
12. displayOrder must be stable.
13. Lifecycle changes audited.
```

Default seed examples:

```text
DISCOVERY
ANALYSIS
DESIGN
DEVELOPMENT
TESTING
DEPLOYMENT
SUPPORT
CLOSURE
```

---

## 8.2 PTC-002 — Workspace active phase catalog

| Item | Value |
|---|---|
| Future capability | Workspace can choose active phase definitions and default order |
| Current state | Not confirmed |
| Phase 11 target | Implement simple workspace phase activation if feasible; otherwise defer |
| Classification | `MUST_IMPLEMENT_IN_PHASE_11` if needed by templates; otherwise `DEFERRED_TO_PHASE_11_FOLLOWUP` |

Recommended model:

```text
WorkspacePhaseDefinitionSetting
```

Purpose:

```text
Control which system/workspace phase definitions are available in a workspace.
```

If too much for current scope, keep:

```text
PhaseDefinition.status + scope
```

and defer workspace phase set.

Rules if implemented:

```text
1. Workspace setting links workspace to PhaseDefinition.
2. PhaseDefinition must be SYSTEM or same workspace/org scope.
3. displayOrder can override default.
4. Disabled setting hides from new templates/projects.
5. Does not affect existing project phases.
```

---

## 8.3 PTC-003 — ProjectTemplate creation

| Item | Value |
|---|---|
| Future capability | Create reusable project template shell |
| Current state | Missing/unknown |
| Phase 11 target | Implement |
| Classification | `MUST_IMPLEMENT_IN_PHASE_11` |

Required rules:

```text
1. Template code required.
2. Template code normalized uppercase.
3. Template code unique within scope.
4. Name required.
5. Scope required: SYSTEM / ORGANIZATION / WORKSPACE.
6. Workspace-scoped template requires active workspace.
7. Organization-scoped template requires active organization.
8. Template owner/creator must have PROJECT_TEMPLATE_CREATE.
9. Initial status DRAFT.
10. Template shell may exist without published version.
11. Event PROJECT_TEMPLATE_CREATED.
```

Template categories:

```text
SOFTWARE_IMPLEMENTATION
WEB_APP
MOBILE_APP
ERP_ROLLOUT
MAINTENANCE
SUPPORT
CUSTOM
```

---

## 8.4 PTC-004 — ProjectTemplate update/lifecycle

| Item | Value |
|---|---|
| Future capability | Update template shell metadata and lifecycle |
| Current state | Missing/unknown |
| Phase 11 target | Implement |
| Classification | `MUST_IMPLEMENT_IN_PHASE_11` |

Updatable:

```text
name
description
category
tags
defaultPriority
visibility
```

Immutable:

```text
scope
organizationId
workspaceId
code unless explicit rename supported
```

Status:

```text
DRAFT
ACTIVE
INACTIVE
ARCHIVED
```

Rules:

```text
1. Archived template cannot be updated.
2. Active template must have at least one published/active version.
3. Archiving template blocks new application.
4. Archiving template does not affect projects already created from it.
5. Event PROJECT_TEMPLATE_UPDATED / PROJECT_TEMPLATE_ARCHIVED.
```

---

## 8.5 PTC-005 — ProjectTemplateVersion

| Item | Value |
|---|---|
| Future capability | Version project templates and preserve published structure |
| Current state | Missing/unknown |
| Phase 11 target | Implement |
| Classification | `MUST_IMPLEMENT_IN_PHASE_11` |

Required status:

```text
DRAFT
PUBLISHED
ARCHIVED
```

Rules:

```text
1. versionNumber unique within template.
2. New version starts DRAFT.
3. DRAFT can be edited.
4. PUBLISHED version immutable.
5. Publishing validates internal structure:
   - at least one phase
   - WBS tree has no cycle
   - template tasks reference valid template phase
   - template tasks reference valid template WBS node if provided
   - template dependencies reference valid template tasks
   - template dependencies have no cycle
6. Publishing can mark version as current/default.
7. Only one current published version per template unless product supports multiple active versions.
8. Archiving version blocks new application but preserves historical reference.
9. Event PROJECT_TEMPLATE_VERSION_CREATED / PROJECT_TEMPLATE_VERSION_PUBLISHED.
```

---

## 8.6 PTC-006 — TemplatePhase

| Item | Value |
|---|---|
| Future capability | Define default phases inside a template version |
| Current state | Missing/unknown |
| Phase 11 target | Implement |
| Classification | `MUST_IMPLEMENT_IN_PHASE_11` |

Required rules:

```text
1. TemplateVersion must be DRAFT to add/update/remove phases.
2. PhaseDefinition must be ACTIVE.
3. TemplatePhase name required.
4. displayOrder required and unique within template version.
5. Phase code/name should be unique within template version.
6. Optional planned offset/duration fields are stored only if current scope supports them, but scheduling use is deferred.
7. Removing a TemplatePhase with TemplateTasks is blocked unless cascade explicitly requested.
```

Optional fields for future:

```text
defaultDurationDays
startOffsetDays
deliverableDocumentTypeId
```

Phase 11 rule:

```text
These fields do not produce schedule/finance/document records yet.
```

---

## 8.7 PTC-007 — TemplateWbsNode

| Item | Value |
|---|---|
| Future capability | Define default WBS hierarchy inside template version |
| Current state | Missing/unknown |
| Phase 11 target | Implement |
| Classification | `MUST_IMPLEMENT_IN_PHASE_11` |

Required rules:

```text
1. TemplateVersion must be DRAFT to add/update/remove WBS nodes.
2. Parent TemplateWbsNode must belong to same template version.
3. Title required.
4. orderIndex required among siblings.
5. Move cannot create cycle.
6. Archive/remove with children blocked unless cascade explicitly requested.
7. TemplateWbsNode may optionally reference TemplatePhase or deliverableDocumentTypeId if product wants phase linkage.
```

Do not create real WBS nodes until template is applied.

---

## 8.8 PTC-008 — TemplateTask

| Item | Value |
|---|---|
| Future capability | Define reusable task blueprint inside template version |
| Current state | Missing/unknown |
| Phase 11 target | Implement |
| Classification | `MUST_IMPLEMENT_IN_PHASE_11` |

Required rules:

```text
1. TemplateVersion must be DRAFT to add/update/remove tasks.
2. Title required.
3. TemplateTask must reference a TemplatePhase.
4. TemplatePhase must belong to same template version.
5. Optional TemplateWbsNode must belong to same template version.
6. estimateHours optional or required according to product decision; if present, must be > 0.
7. defaultAssigneeRoleCode optional; no user assignment in template unless product supports it.
8. dueOffsetDays optional; does not create actual dueDate until apply logic decides.
9. Task status in template is not live task status.
```

Recommended:

```text
TemplateTask.estimateHours should be optional but if provided > 0.
```

Why optional:

```text
Some templates define structure only; estimation can happen later in Phase 16.
```

---

## 8.9 PTC-009 — TemplateTaskDependency

| Item | Value |
|---|---|
| Future capability | Define default logical dependencies between template tasks |
| Current state | Missing/unknown |
| Phase 11 target | Implement |
| Classification | `MUST_IMPLEMENT_IN_PHASE_11` |

Required rules:

```text
1. TemplateVersion must be DRAFT to add/remove dependencies.
2. Predecessor TemplateTask must belong to same template version.
3. Successor TemplateTask must belong to same template version.
4. predecessor != successor.
5. Duplicate dependency blocked.
6. Dependency cycle blocked.
7. Dependency type defaults FINISH_TO_START.
8. Lag may be stored but not scheduled until Phase 13.
```

---

## 8.10 PTC-010 — Apply template to project

| Item | Value |
|---|---|
| Future capability | Create project structure from published template version |
| Current state | Missing/unknown |
| Phase 11 target | Implement |
| Classification | `MUST_IMPLEMENT_IN_PHASE_11` |

Use cases:

```text
1. Create new project from template.
2. Apply template structure to existing empty project.
3. Apply additional template phase/WBS/tasks to existing project if product allows.
```

Recommended Phase 11 minimum:

```text
Create new project from template.
```

Optional:

```text
Apply to existing empty project.
```

Rules:

```text
1. Template must exist.
2. Template must be ACTIVE.
3. TemplateVersion must be PUBLISHED.
4. Workspace must be ACTIVE.
5. Actor must have PROJECT_CREATE and PROJECT_TEMPLATE_APPLY.
6. Applying template creates Project.
7. TemplatePhase → ProjectPhase.
8. TemplateWbsNode → WbsNode.
9. TemplateTask → Task.
10. TemplateTaskDependency → TaskDependency.
11. Mapping must preserve hierarchy.
12. TemplateDependency references must be mapped to created Task IDs.
13. If any child creation fails, transaction rolls back.
14. Apply operation records templateId/templateVersionId on Project if fields exist.
15. Event PROJECT_TEMPLATE_APPLIED.
```

Important:

```text
Template application must call or reuse Project Core validation rules.
```

Do not bypass:

```text
task estimate validation
phase same project validation
WBS cycle validation
task dependency cycle validation
assignee membership validation
```

If template has role placeholders, no actual user assignment happens until later unless mapping provided.

---

## 8.11 PTC-011 — Template clone / new version from existing project

| Item | Value |
|---|---|
| Future capability | Create template from an existing successful project |
| Current state | Missing |
| Phase 11 target | Defer unless product requires |
| Classification | `DEFERRED_TO_PHASE_16/21` or `POST_23_TEMPLATE_OPTIMIZATION_BACKLOG` |

Future rules:

```text
1. Source project must be accessible.
2. Sensitive data stripped.
3. Actual users removed or converted to role placeholders.
4. Dates converted to offsets.
5. Costs/finance excluded unless finance template supports it.
```

Do not implement in Phase 11 unless required.

---

## 8.12 PTC-012 — Template marketplace/library

| Item | Value |
|---|---|
| Future capability | Browse, categorize, rate, duplicate, and install templates |
| Current state | Missing |
| Phase 11 target | Basic list/search only |
| Classification | `DEFERRED_TO_POST_23_BACKLOG` for marketplace features |

Phase 11 list/search filters:

```text
scope
category
status
workspaceId
organizationId
code/name
```

Marketplace features deferred:

```text
ratings
install/uninstall
publisher metadata
template marketplace permissions
```

---

# 9. Entity model TO-BE

If current schema differs, agent must map actual fields and document gaps.

---

## 9.1 PhaseDefinition — `project_phase_definition`

Required fields:

```text
id UUID PK
code VARCHAR(100) NOT NULL
name VARCHAR(255) NOT NULL
description TEXT NULL
scope VARCHAR(50) NOT NULL
organization_id UUID NULL
workspace_id UUID NULL
display_order INT NOT NULL DEFAULT 0
built_in BOOLEAN NOT NULL DEFAULT false
status VARCHAR(50) NOT NULL
created_at / created_by
updated_at / updated_by
archived_at / archived_by
version INT
```

Constraints:

```text
unique scope + organization_id + workspace_id + code
display_order >= 0
```

Status:

```text
ACTIVE
INACTIVE
ARCHIVED
```

---

## 9.2 ProjectTemplate — `project_template`

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
visibility VARCHAR(50) NULL
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
```

Status:

```text
DRAFT
ACTIVE
INACTIVE
ARCHIVED
```

Constraints:

```text
unique scope + organization_id + workspace_id + code
```

---

## 9.3 ProjectTemplateVersion — `project_template_version`

Required fields:

```text
id UUID PK
project_template_id UUID NOT NULL
version_number INT NOT NULL
name VARCHAR(255) NULL
description TEXT NULL
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
unique project_template_id + version_number
```

---

## 9.4 ProjectTemplatePhase — `project_template_phase`

Required fields:

```text
id UUID PK
template_version_id UUID NOT NULL
phase_definition_id UUID NULL
code VARCHAR(100) NULL
name VARCHAR(255) NOT NULL
description TEXT NULL
display_order INT NOT NULL
default_duration_days INT NULL
start_offset_days INT NULL
deliverable_document_type_id UUID NULL
created_at / created_by
updated_at / updated_by
version INT
```

Constraints:

```text
unique template_version_id + display_order
optional unique template_version_id + code
```

---

## 9.5 ProjectTemplateWbsNode — `project_template_wbs_node`

Required fields:

```text
id UUID PK
template_version_id UUID NOT NULL
parent_id UUID NULL
template_phase_id UUID NULL
code VARCHAR(100) NULL
title VARCHAR(255) NOT NULL
description TEXT NULL
node_type VARCHAR(50) NULL
depth INT NOT NULL DEFAULT 0
order_index INT NOT NULL DEFAULT 0
deliverable_document_type_id UUID NULL
created_at / created_by
updated_at / updated_by
version INT
```

Constraints:

```text
parent_id belongs to same template_version_id
depth >= 0
```

---

## 9.6 ProjectTemplateTask — `project_template_task`

Required fields:

```text
id UUID PK
template_version_id UUID NOT NULL
template_phase_id UUID NOT NULL
template_wbs_node_id UUID NULL
code VARCHAR(100) NULL
title VARCHAR(255) NOT NULL
description TEXT NULL
default_priority VARCHAR(50) NULL
estimate_hours DECIMAL(12,2) NULL
due_offset_days INT NULL
start_offset_days INT NULL
default_assignee_role_code VARCHAR(100) NULL
deliverable_document_type_id UUID NULL
created_at / created_by
updated_at / updated_by
version INT
```

Rules:

```text
estimate_hours null or > 0
template_phase_id same template version
template_wbs_node_id same template version
```

---

## 9.7 ProjectTemplateTaskDependency — `project_template_task_dependency`

Required fields:

```text
id UUID PK
template_version_id UUID NOT NULL
predecessor_template_task_id UUID NOT NULL
successor_template_task_id UUID NOT NULL
dependency_type VARCHAR(50) NOT NULL
lag_days INT NOT NULL DEFAULT 0
created_at / created_by
```

Constraint:

```text
unique predecessor_template_task_id + successor_template_task_id + dependency_type
predecessor != successor
both tasks same template version
```

---

## 9.8 Project template reference on Project

Recommended fields on `project_project`:

```text
source_template_id UUID NULL
source_template_version_id UUID NULL
source_template_applied_at TIMESTAMP NULL
```

If not added now:

```text
Document as gap.
```

Recommended classification:

```text
MUST_IMPLEMENT_IN_PHASE_11
```

Reason:

```text
Traceability matters when projects are created from templates.
```

---

# 10. API TO-BE list

All APIs use `/api`.

---

## 10.1 PhaseDefinition APIs

```text
POST  /api/project/phase-definitions
GET   /api/project/phase-definitions
GET   /api/project/phase-definitions/{id}
PUT   /api/project/phase-definitions/{id}
PATCH /api/project/phase-definitions/{id}/activate
PATCH /api/project/phase-definitions/{id}/deactivate
PATCH /api/project/phase-definitions/{id}/archive
```

Filters:

```text
scope
organizationId
workspaceId
status
builtIn
code
```

If project module already has different path, keep consistent and document.

---

## 10.2 ProjectTemplate APIs

```text
POST  /api/project/templates
GET   /api/project/templates
GET   /api/project/templates/{templateId}
PUT   /api/project/templates/{templateId}
PATCH /api/project/templates/{templateId}/activate
PATCH /api/project/templates/{templateId}/deactivate
PATCH /api/project/templates/{templateId}/archive
```

Filters:

```text
scope
workspaceId
organizationId
status
category
code
name
```

---

## 10.3 ProjectTemplateVersion APIs

```text
POST  /api/project/templates/{templateId}/versions
GET   /api/project/templates/{templateId}/versions
GET   /api/project/templates/{templateId}/versions/{versionId}
PUT   /api/project/templates/{templateId}/versions/{versionId}
POST  /api/project/templates/{templateId}/versions/{versionId}/publish
PATCH /api/project/templates/{templateId}/versions/{versionId}/archive
POST  /api/project/templates/{templateId}/versions/{versionId}/duplicate
```

Rules:

```text
PUT only DRAFT.
PUBLISHED immutable.
Duplicate creates new DRAFT version.
```

---

## 10.4 TemplatePhase APIs

```text
POST   /api/project/templates/{templateId}/versions/{versionId}/phases
GET    /api/project/templates/{templateId}/versions/{versionId}/phases
GET    /api/project/templates/{templateId}/versions/{versionId}/phases/{templatePhaseId}
PUT    /api/project/templates/{templateId}/versions/{versionId}/phases/{templatePhaseId}
DELETE /api/project/templates/{templateId}/versions/{versionId}/phases/{templatePhaseId}
PUT    /api/project/templates/{templateId}/versions/{versionId}/phases/reorder
```

DELETE should be soft/remove from draft only.

---

## 10.5 TemplateWbsNode APIs

```text
POST   /api/project/templates/{templateId}/versions/{versionId}/wbs-nodes
GET    /api/project/templates/{templateId}/versions/{versionId}/wbs-nodes
GET    /api/project/templates/{templateId}/versions/{versionId}/wbs-tree
GET    /api/project/templates/{templateId}/versions/{versionId}/wbs-nodes/{templateWbsNodeId}
PUT    /api/project/templates/{templateId}/versions/{versionId}/wbs-nodes/{templateWbsNodeId}
POST   /api/project/templates/{templateId}/versions/{versionId}/wbs-nodes/{templateWbsNodeId}/move
DELETE /api/project/templates/{templateId}/versions/{versionId}/wbs-nodes/{templateWbsNodeId}
```

---

## 10.6 TemplateTask APIs

```text
POST   /api/project/templates/{templateId}/versions/{versionId}/tasks
GET    /api/project/templates/{templateId}/versions/{versionId}/tasks
GET    /api/project/templates/{templateId}/versions/{versionId}/tasks/{templateTaskId}
PUT    /api/project/templates/{templateId}/versions/{versionId}/tasks/{templateTaskId}
DELETE /api/project/templates/{templateId}/versions/{versionId}/tasks/{templateTaskId}
```

---

## 10.7 TemplateTaskDependency APIs

```text
POST   /api/project/templates/{templateId}/versions/{versionId}/task-dependencies
GET    /api/project/templates/{templateId}/versions/{versionId}/task-dependencies
DELETE /api/project/templates/{templateId}/versions/{versionId}/task-dependencies/{dependencyId}
```

---

## 10.8 Apply template APIs

Recommended:

```text
POST /api/project/templates/{templateId}/versions/{versionId}/apply
```

Request:

```json
{
  "workspaceId": "uuid",
  "projectCode": "CLIENT_PORTAL_IMPL",
  "projectName": "Client Portal Implementation",
  "projectDescription": "Implementation project created from template",
  "plannedStartDate": "2026-08-01",
  "ownerUserId": "uuid",
  "options": {
    "includeTemplateTasks": true,
    "includeTemplateDependencies": true,
    "copyEstimateHours": true,
    "copyDueOffsets": false
  }
}
```

Alternative:

```text
POST /api/projects/from-template
```

Either is acceptable if consistent.

Rules:

```text
Create real Project/ProjectPhase/WBS/Task/Dependency.
```

---

# 11. Authorization requirements

Phase 11 must use IAM.

## 11.1 Required permissions

```text
PROJECT_TEMPLATE_VIEW
PROJECT_TEMPLATE_CREATE
PROJECT_TEMPLATE_UPDATE
PROJECT_TEMPLATE_PUBLISH
PROJECT_TEMPLATE_ARCHIVE
PROJECT_TEMPLATE_APPLY
PROJECT_TEMPLATE_MANAGE

PROJECT_TEMPLATE_VERSION_VIEW
PROJECT_TEMPLATE_VERSION_CREATE
PROJECT_TEMPLATE_VERSION_UPDATE
PROJECT_TEMPLATE_VERSION_PUBLISH
PROJECT_TEMPLATE_VERSION_ARCHIVE

PHASE_DEFINITION_VIEW
PHASE_DEFINITION_CREATE
PHASE_DEFINITION_UPDATE
PHASE_DEFINITION_ARCHIVE
PHASE_DEFINITION_MANAGE_SYSTEM
PHASE_DEFINITION_MANAGE_WORKSPACE
```

## 11.2 Apply template permissions

Applying template requires:

```text
PROJECT_TEMPLATE_VIEW on template scope
PROJECT_TEMPLATE_APPLY on template scope or workspace
PROJECT_CREATE on target workspace
PROJECT_PHASE_CREATE on target workspace
PROJECT_WBS_CREATE on target workspace
PROJECT_TASK_CREATE on target workspace if including tasks
PROJECT_TASK_UPDATE or PROJECT_TASK_DEPENDENCY_CREATE if including dependencies
```

Simplification allowed:

```text
PROJECT_CREATE + PROJECT_TEMPLATE_APPLY can internally perform child creation if product decides apply-template is a privileged operation.
```

If simplified, document clearly.

## 11.3 Scope access

Rules:

```text
1. SYSTEM template visible to users with template view and workspace access where applied.
2. ORGANIZATION template visible only within organization.
3. WORKSPACE template visible only within workspace.
4. Workspace-scoped template requires active workspace membership.
5. Organization-scoped template requires active org membership.
```

---

# 12. Event Registry integration

Source system:

```text
SCOPERY_PROJECT
```

## 12.1 Required Phase 11 events

```text
PHASE_DEFINITION_CREATED
PHASE_DEFINITION_UPDATED
PHASE_DEFINITION_ACTIVATED
PHASE_DEFINITION_DEACTIVATED
PHASE_DEFINITION_ARCHIVED

PROJECT_TEMPLATE_CREATED
PROJECT_TEMPLATE_UPDATED
PROJECT_TEMPLATE_ACTIVATED
PROJECT_TEMPLATE_DEACTIVATED
PROJECT_TEMPLATE_ARCHIVED

PROJECT_TEMPLATE_VERSION_CREATED
PROJECT_TEMPLATE_VERSION_UPDATED
PROJECT_TEMPLATE_VERSION_PUBLISHED
PROJECT_TEMPLATE_VERSION_ARCHIVED
PROJECT_TEMPLATE_VERSION_DUPLICATED

PROJECT_TEMPLATE_PHASE_CREATED
PROJECT_TEMPLATE_PHASE_UPDATED
PROJECT_TEMPLATE_PHASE_DELETED
PROJECT_TEMPLATE_PHASES_REORDERED

PROJECT_TEMPLATE_WBS_NODE_CREATED
PROJECT_TEMPLATE_WBS_NODE_UPDATED
PROJECT_TEMPLATE_WBS_NODE_MOVED
PROJECT_TEMPLATE_WBS_NODE_DELETED

PROJECT_TEMPLATE_TASK_CREATED
PROJECT_TEMPLATE_TASK_UPDATED
PROJECT_TEMPLATE_TASK_DELETED

PROJECT_TEMPLATE_TASK_DEPENDENCY_CREATED
PROJECT_TEMPLATE_TASK_DEPENDENCY_REMOVED

PROJECT_TEMPLATE_APPLIED
```

## 12.2 Standard variables

```text
actor.userId
organization.id
workspace.id
template.id
template.code
template.name
template.versionId
template.versionNumber
phaseDefinition.id
templatePhase.id
templateWbsNode.id
templateTask.id
project.id
project.code
project.name
occurredAt
traceId
```

---

# 13. Notification integration

Phase 11 does not need broad user notifications.

Optional admin notifications:

```text
PROJECT_TEMPLATE_VERSION_PUBLISHED_ADMIN_EMAIL
PROJECT_TEMPLATE_ARCHIVED_ADMIN_EMAIL
```

Recommended classification:

```text
DEFERRED_TO_PHASE_20 or not implemented unless product needs template admin alerts.
```

Do not notify end-users when every template changes.

---

# 14. Knowledge / Document integration

Phase 08 provides DocumentType foundation.

Phase 11 may store references:

```text
ProjectTemplatePhase.deliverableDocumentTypeId
ProjectTemplateWbsNode.deliverableDocumentTypeId
ProjectTemplateTask.deliverableDocumentTypeId
```

Rules:

```text
1. Referenced DocumentType must exist and be ACTIVE.
2. Reference does not create a Document.
3. Applying template does not create document content in Phase 11.
4. Full document generation deferred to Phase 21/27.
```

If document type reference is not needed now:

```text
DEFERRED_TO_PHASE_27_FULL_DOCUMENT_HUB
```

---

# 15. AI Agent integration

Phase 11 must not implement AI-generated templates.

Future:

```text
AI suggests WBS/tasks from project description.
AI suggests a template based on industry.
AI improves existing template.
AI creates draft template version.
```

Deferred to:

```text
Phase 21 — AI-assisted Project Planning
Post-23 template optimization backlog
```

Rule:

```text
No AI mutation of ProjectTemplate in Phase 11.
```

---

# 16. Platform audit/outbox/idempotency integration

Phase 11 must follow Phase 04.

## 16.1 Activity log actions

```text
PHASE_DEFINITION_CREATED
PHASE_DEFINITION_UPDATED
PHASE_DEFINITION_ARCHIVED
PROJECT_TEMPLATE_CREATED
PROJECT_TEMPLATE_UPDATED
PROJECT_TEMPLATE_ARCHIVED
PROJECT_TEMPLATE_VERSION_CREATED
PROJECT_TEMPLATE_VERSION_PUBLISHED
PROJECT_TEMPLATE_PHASE_CREATED
PROJECT_TEMPLATE_WBS_NODE_CREATED
PROJECT_TEMPLATE_TASK_CREATED
PROJECT_TEMPLATE_TASK_DEPENDENCY_CREATED
PROJECT_TEMPLATE_APPLIED
```

## 16.2 Audit-sensitive actions

Audit:

```text
PHASE_DEFINITION_ARCHIVED
PROJECT_TEMPLATE_VERSION_PUBLISHED
PROJECT_TEMPLATE_ARCHIVED
PROJECT_TEMPLATE_APPLIED
Template structure changed
Template task estimate changed
Template dependency changed
```

Reason:

```text
Templates can affect many future projects.
```

## 16.3 Idempotency

Recommended for:

```text
POST /api/project/templates
POST /api/project/templates/{id}/versions
POST /api/project/templates/{id}/versions/{versionId}/publish
POST /api/project/templates/{id}/versions/{versionId}/apply
POST template phase/WBS/task/dependency creation endpoints
```

Apply template idempotency is especially important.

Rules:

```text
Same Idempotency-Key for apply template must not create duplicate project.
```

## 16.4 Outbox

All template events should use platform outbox if available.

If not available:

```text
Use existing event publisher and document gap.
```

---

# 17. Apply template algorithm

## 17.1 Inputs

```text
templateId
templateVersionId
workspaceId
projectCode
projectName
projectDescription
plannedStartDate optional
ownerUserId optional
options
```

## 17.2 Validation order

```text
1. Authenticate actor.
2. Check template visibility/access.
3. Check template status ACTIVE.
4. Check version status PUBLISHED.
5. Check target workspace ACTIVE.
6. Check actor PROJECT_CREATE on workspace.
7. Check actor PROJECT_TEMPLATE_APPLY.
8. Validate project code/name.
9. Validate ownerUserId active workspace member if provided.
10. Validate template version structure again or ensure published validation exists.
```

## 17.3 Creation order

```text
1. Create Project.
2. Create ProjectPhases from TemplatePhases.
3. Create WbsNodes from TemplateWbsNodes:
   - root nodes first
   - then children by depth
   - store mapping templateWbsNodeId → wbsNodeId
4. Create Tasks from TemplateTasks:
   - map templatePhaseId → projectPhaseId
   - map templateWbsNodeId → wbsNodeId if present
   - copy estimateHours if option true
   - do not assign user unless mapping exists
   - dueDate not created from offset unless product chooses simple offset support
5. Create TaskDependencies:
   - map predecessor/successor template tasks to real task IDs
6. Store Project.sourceTemplateId/sourceTemplateVersionId.
7. Emit PROJECT_TEMPLATE_APPLIED.
```

## 17.4 Transaction rule

```text
All apply-template writes must be in one transaction.
If any child creation fails, rollback everything.
```

## 17.5 Mapping table

Mapping may be in memory only.

Optional persistence:

```text
ProjectTemplateApplicationLog
```

Recommended entity:

```text
ProjectTemplateApplication
```

Fields:

```text
id
templateId
templateVersionId
projectId
workspaceId
appliedBy
appliedAt
status
summaryJson
traceId
```

Classification:

```text
MUST_IMPLEMENT_IN_PHASE_11 if project sourceTemplate fields are insufficient.
Otherwise optional.
```

---

# 18. Business rules master

## 18.1 PhaseDefinition rules

```text
PTC-PHD-001 Code required.
PTC-PHD-002 Code normalized uppercase.
PTC-PHD-003 Code unique within scope.
PTC-PHD-004 Name required.
PTC-PHD-005 Scope required.
PTC-PHD-006 SYSTEM scope has no org/workspace.
PTC-PHD-007 WORKSPACE scope requires active workspace.
PTC-PHD-008 Built-in cannot be hard-deleted.
PTC-PHD-009 Referenced definition cannot be hard-deleted.
PTC-PHD-010 Inactive/archived definition cannot be used in new template/project phase.
```

## 18.2 ProjectTemplate rules

```text
PTC-TPL-001 Template code required.
PTC-TPL-002 Template code normalized uppercase.
PTC-TPL-003 Template code unique within scope.
PTC-TPL-004 Name required.
PTC-TPL-005 Scope required.
PTC-TPL-006 Workspace-scoped template requires active workspace.
PTC-TPL-007 Organization-scoped template requires active organization.
PTC-TPL-008 Archived template cannot be updated/applied.
PTC-TPL-009 Active template requires current published version.
PTC-TPL-010 Built-in template cannot be hard-deleted.
```

## 18.3 TemplateVersion rules

```text
PTC-VER-001 Version number unique within template.
PTC-VER-002 New version starts DRAFT.
PTC-VER-003 DRAFT can be edited.
PTC-VER-004 PUBLISHED immutable.
PTC-VER-005 Publish validates structure.
PTC-VER-006 Published version can be applied.
PTC-VER-007 Archived version cannot be applied.
PTC-VER-008 Only one current published version unless multiple-active is supported.
```

## 18.4 TemplatePhase rules

```text
PTC-TPH-001 Template version must be DRAFT to mutate.
PTC-TPH-002 Name required.
PTC-TPH-003 displayOrder required.
PTC-TPH-004 PhaseDefinition must be active if provided.
PTC-TPH-005 Removing phase with template tasks blocked unless cascade explicit.
```

## 18.5 TemplateWbsNode rules

```text
PTC-TWBS-001 Template version must be DRAFT to mutate.
PTC-TWBS-002 Title required.
PTC-TWBS-003 Parent node same template version.
PTC-TWBS-004 Move cannot create cycle.
PTC-TWBS-005 Move cannot place under itself/descendant.
PTC-TWBS-006 Delete with children blocked unless cascade explicit.
```

## 18.6 TemplateTask rules

```text
PTC-TASK-001 Template version must be DRAFT to mutate.
PTC-TASK-002 Title required.
PTC-TASK-003 TemplatePhase required.
PTC-TASK-004 TemplatePhase same template version.
PTC-TASK-005 TemplateWbsNode if set same template version.
PTC-TASK-006 estimateHours null or > 0.
PTC-TASK-007 defaultAssigneeRoleCode does not assign a real user.
```

## 18.7 TemplateDependency rules

```text
PTC-DEP-001 Template version must be DRAFT to mutate.
PTC-DEP-002 Predecessor task same template version.
PTC-DEP-003 Successor task same template version.
PTC-DEP-004 predecessor != successor.
PTC-DEP-005 Duplicate dependency blocked.
PTC-DEP-006 Dependency cycle blocked.
PTC-DEP-007 Dependency type defaults FINISH_TO_START.
```

## 18.8 Apply rules

```text
PTC-APPLY-001 Template must be ACTIVE.
PTC-APPLY-002 Version must be PUBLISHED.
PTC-APPLY-003 Workspace must be ACTIVE.
PTC-APPLY-004 Actor must have PROJECT_TEMPLATE_APPLY and PROJECT_CREATE.
PTC-APPLY-005 Project code unique in target workspace.
PTC-APPLY-006 Apply creates all records in one transaction.
PTC-APPLY-007 Template hierarchy mapping must be preserved.
PTC-APPLY-008 Template dependencies mapped to real task dependencies.
PTC-APPLY-009 Apply must not create schedule/Gantt/finance/quote/baseline.
PTC-APPLY-010 Same idempotency key must not create duplicate project.
```

---

# 19. Error catalog requirements

Exact names follow project convention, but these concepts must exist.

```text
PHASE_DEFINITION_NOT_FOUND
PHASE_DEFINITION_CODE_ALREADY_EXISTS
PHASE_DEFINITION_INVALID_SCOPE
PHASE_DEFINITION_ARCHIVED
PHASE_DEFINITION_INACTIVE
PHASE_DEFINITION_IN_USE
PHASE_DEFINITION_BUILT_IN_CANNOT_DELETE

PROJECT_TEMPLATE_NOT_FOUND
PROJECT_TEMPLATE_CODE_ALREADY_EXISTS
PROJECT_TEMPLATE_INVALID_SCOPE
PROJECT_TEMPLATE_ARCHIVED
PROJECT_TEMPLATE_INACTIVE
PROJECT_TEMPLATE_NO_PUBLISHED_VERSION
PROJECT_TEMPLATE_ACCESS_DENIED

PROJECT_TEMPLATE_VERSION_NOT_FOUND
PROJECT_TEMPLATE_VERSION_NOT_DRAFT
PROJECT_TEMPLATE_VERSION_NOT_PUBLISHED
PROJECT_TEMPLATE_VERSION_ALREADY_PUBLISHED
PROJECT_TEMPLATE_VERSION_STRUCTURE_INVALID

PROJECT_TEMPLATE_PHASE_NOT_FOUND
PROJECT_TEMPLATE_PHASE_PATH_MISMATCH
PROJECT_TEMPLATE_PHASE_HAS_TASKS

PROJECT_TEMPLATE_WBS_NODE_NOT_FOUND
PROJECT_TEMPLATE_WBS_NODE_PATH_MISMATCH
PROJECT_TEMPLATE_WBS_NODE_PARENT_INVALID
PROJECT_TEMPLATE_WBS_NODE_CYCLE_DETECTED
PROJECT_TEMPLATE_WBS_NODE_HAS_CHILDREN

PROJECT_TEMPLATE_TASK_NOT_FOUND
PROJECT_TEMPLATE_TASK_PATH_MISMATCH
PROJECT_TEMPLATE_TASK_INVALID_ESTIMATE
PROJECT_TEMPLATE_TASK_PHASE_MISMATCH
PROJECT_TEMPLATE_TASK_WBS_MISMATCH

PROJECT_TEMPLATE_DEPENDENCY_NOT_FOUND
PROJECT_TEMPLATE_DEPENDENCY_DUPLICATE
PROJECT_TEMPLATE_DEPENDENCY_SELF_NOT_ALLOWED
PROJECT_TEMPLATE_DEPENDENCY_CYCLE_DETECTED
PROJECT_TEMPLATE_DEPENDENCY_TASK_MISMATCH

PROJECT_TEMPLATE_APPLY_FAILED
PROJECT_TEMPLATE_APPLY_WORKSPACE_INACTIVE
PROJECT_TEMPLATE_APPLY_PERMISSION_DENIED
PROJECT_TEMPLATE_APPLY_DUPLICATE_PROJECT_CODE
```

---

# 20. Required tests

Phase 11 is incomplete without tests.

---

## 20.1 PhaseDefinition tests

```text
createPhaseDefinition_validSystem_success
createPhaseDefinition_duplicateCodeSameScope_conflict
createPhaseDefinition_sameCodeDifferentWorkspace_allowed
createPhaseDefinition_invalidScope_rejected
createPhaseDefinition_workspaceScopeInactiveWorkspace_rejected
archiveReferencedPhaseDefinition_rejected
useInactivePhaseDefinitionInTemplatePhase_rejected
phaseDefinitionSeeder_firstRun_createsDefaults
phaseDefinitionSeeder_secondRun_noDuplicates
```

## 20.2 ProjectTemplate tests

```text
createProjectTemplate_validSystem_success
createProjectTemplate_validWorkspace_success
createProjectTemplate_duplicateCodeSameScope_conflict
createProjectTemplate_sameCodeDifferentWorkspace_allowed
createProjectTemplate_invalidScope_rejected
updateArchivedTemplate_rejected
archiveTemplate_blocksApply
activateTemplate_withoutPublishedVersion_rejected
```

## 20.3 ProjectTemplateVersion tests

```text
createTemplateVersion_valid_success
updateDraftVersion_success
updatePublishedVersion_rejected
publishVersion_withoutPhases_rejected
publishVersion_validStructure_success
publishVersion_withInvalidTaskPhase_rejected
publishVersion_withDependencyCycle_rejected
duplicateVersion_createsDraftCopy
archivePublishedVersion_blocksApply
```

## 20.4 TemplatePhase tests

```text
createTemplatePhase_valid_success
createTemplatePhase_onPublishedVersion_rejected
createTemplatePhase_inactivePhaseDefinition_rejected
updateTemplatePhase_valid_success
deleteTemplatePhase_withTasks_rejected
reorderTemplatePhases_valid_success
```

## 20.5 TemplateWbsNode tests

```text
createTemplateWbsNode_root_success
createTemplateWbsNode_child_success
createTemplateWbsNode_parentDifferentVersion_rejected
moveTemplateWbsNode_valid_success
moveTemplateWbsNode_underSelf_rejected
moveTemplateWbsNode_underDescendant_rejected
deleteTemplateWbsNode_withChildren_rejected
```

## 20.6 TemplateTask tests

```text
createTemplateTask_valid_success
createTemplateTask_onPublishedVersion_rejected
createTemplateTask_missingPhase_rejected
createTemplateTask_phaseDifferentVersion_rejected
createTemplateTask_wbsDifferentVersion_rejected
createTemplateTask_estimateZero_rejected
updateTemplateTask_valid_success
deleteTemplateTask_withDependencies_rejected
```

## 20.7 TemplateDependency tests

```text
createTemplateDependency_valid_success
createTemplateDependency_duplicate_conflict
createTemplateDependency_self_rejected
createTemplateDependency_crossVersion_rejected
createTemplateDependency_cycle_rejected
removeTemplateDependency_valid_success
```

## 20.8 Apply template tests

```text
applyTemplate_valid_createsProjectStructure
applyTemplate_inactiveTemplate_rejected
applyTemplate_unpublishedVersion_rejected
applyTemplate_archivedVersion_rejected
applyTemplate_inactiveWorkspace_rejected
applyTemplate_withoutPermission_forbidden
applyTemplate_duplicateProjectCode_conflict
applyTemplate_preservesPhaseMapping
applyTemplate_preservesWbsHierarchy
applyTemplate_preservesTaskPhaseAndWbsMapping
applyTemplate_preservesTaskDependencies
applyTemplate_transactionRollbackOnChildFailure
applyTemplate_sameIdempotencyKey_noDuplicateProject
applyTemplate_doesNotCreateGanttFinanceQuoteBaseline
```

## 20.9 Authorization tests

```text
createTemplate_withoutPermission_forbidden
publishTemplateVersion_withoutPermission_forbidden
applyTemplate_withoutProjectCreate_forbidden
applyTemplate_withoutTemplateApply_forbidden
workspaceTemplate_crossWorkspaceAccess_forbidden
systemTemplate_viewAllowedWithPermission
```

## 20.10 Seeder/event tests

```text
projectTemplateEventSeeder_firstRun_createsDefinitions
projectTemplateEventSeeder_secondRun_noDuplicates
templatePermissionSeeder_authoritiesExist
templateActivity_logged
templateAudit_forPublish_created
```

---

# 21. Manual verification checklist

Completion file must include:

```text
1. Create phase definition.
2. Create project template.
3. Create template version.
4. Add template phases.
5. Add template WBS root/child nodes.
6. Add template tasks.
7. Add template task dependency.
8. Try dependency cycle and confirm rejection.
9. Publish template version.
10. Confirm published version is immutable.
11. Apply template to create project.
12. Confirm project phases created.
13. Confirm WBS hierarchy created.
14. Confirm tasks created and mapped to phases/WBS.
15. Confirm dependencies created.
16. Confirm project stores source template/version.
17. Confirm no Gantt/finance/quote/baseline records are created.
18. Rerun seeders and confirm no duplicates.
```

---

# 22. Acceptance criteria

Phase 11 is accepted only if:

```text
1. Current Project/PhaseDefinition implementation is classified against TO-BE.
2. PhaseDefinition catalog is hardened.
3. ProjectTemplate implemented/tested.
4. ProjectTemplateVersion implemented/tested.
5. TemplatePhase implemented/tested.
6. TemplateWbsNode implemented/tested with cycle prevention.
7. TemplateTask implemented/tested.
8. TemplateTaskDependency implemented/tested with cycle prevention.
9. Published template version is immutable.
10. Applying template creates Project/Phase/WBS/Task/Dependency correctly.
11. Apply template is transactional.
12. Apply template is idempotency-safe.
13. Template permissions are enforced.
14. Template events are seeded idempotently.
15. Activity/audit follows Phase 04.
16. Scheduling/Gantt/finance/quote/baseline/AI planning are not falsely claimed.
17. mvn compile passes.
18. mvn test passes.
19. Completion file exists and includes gap matrix.
```

Agent must not mark complete if:

```text
mvn test fails
published template version can be edited
template WBS can create cycle
template dependency can create cycle
apply template partially creates records after failure
apply template duplicates project with same idempotency key
apply template creates fake schedule/Gantt/finance/quote/baseline
template permissions are missing
deferred gaps are not documented
```

---

# 23. Required phase completion file

Agent must create:

```text
docs/phase-complete/PHASE_11_PROJECT_TEMPLATE_PHASE_CATALOG_TO_BE_COMPLETE.md
```

Required sections:

```text
# Phase 11 — Project Template / Phase Catalog TO-BE Complete

## 1. Summary
## 2. Source Inputs Reviewed
## 3. Current vs TO-BE Classification Matrix
## 4. Implemented in Current BE
## 5. Implemented / Hardened in This Phase
## 6. Seed-only Items Added
## 7. Deferred Items and Target Phase
## 8. PhaseDefinition Catalog Decision
## 9. ProjectTemplate Entity Mapping
## 10. ProjectTemplateVersion Entity Mapping
## 11. TemplatePhase/WBS/Task/Dependency Mapping
## 12. Apply Template Algorithm
## 13. API Changes
## 14. Authorization Matrix
## 15. Event Registry Seeder Matrix
## 16. Activity / Audit / Outbox Notes
## 17. Idempotency Strategy
## 18. Tests Added
## 19. Commands Run
## 20. Test Results
## 21. Manual Verification
## 22. Assumptions
## 23. Deviations From Prompt
## 24. Known Risks
## 25. Future Phases That Must Return to Project Template
```

---

# 24. Future phases that must return to Project Template

## 24.1 Phase 12 — Resource Calendar / Capacity

Template tasks may include:

```text
defaultRoleCode
defaultSkill
estimatedEffortByRole
```

Capacity cannot be calculated in Phase 11.

## 24.2 Phase 13 — Task Scheduling Engine

Template may add:

```text
startOffsetDays
dueOffsetDays
dependency lag scheduling
default task duration
```

Scheduling interpretation happens in Phase 13.

## 24.3 Phase 14 — Gantt

Template can support:

```text
Gantt view presets
phase timeline presets
milestone templates
```

But Gantt projection is Phase 14.

## 24.4 Phase 15 — Rate Card / CCH

Template task role placeholders may map to cost roles.

## 24.5 Phase 16 — Estimation Roll-up

Template can define:

```text
default estimate hours
estimation assumptions
complexity bands
```

Roll-up engine is Phase 16.

## 24.6 Phase 17 — Finance

Template may include:

```text
default custom phase costs
overhead model
revenue split defaults
margin target
```

Do not include in Phase 11 unless fields are explicitly marked future.

## 24.7 Phase 18 — Quote

Template may become basis for quote/proposal.

Quote generation deferred.

## 24.8 Phase 19 — Baseline / Change Request

Template application may become first baseline seed.

Baseline snapshot deferred.

## 24.9 Phase 20 — Project Events / Notifications

Template events may notify admins or project creators.

Full notification subscriptions deferred.

## 24.10 Phase 21 — AI-assisted Project Planning

AI can suggest:

```text
best template
WBS additions
task estimates
template improvement suggestions
```

Human approval required.

## 24.11 Phase 22 — Reporting

Reports can show:

```text
projects by template
template usage
template success metrics
average variance by template
```

## 24.12 Phase 23 — Core Hardening

Must review:

```text
template apply performance
large template transaction handling
deep WBS validation
dependency cycle algorithm
template authorization coverage
```

## 24.13 Phase 27 — Full Document Hub

Template deliverables may create document placeholders or document generation rules.

## 24.14 Post-23 Template Marketplace Backlog

May add:

```text
template marketplace
template import/export
template install
template rating
template publisher lifecycle
```

---

# 25. Agent anti-bịa rules

The agent must not:

```text
1. Treat PhaseDefinition as full ProjectTemplate.
2. Claim project templates exist unless ProjectTemplate entities/APIs/tests exist.
3. Claim applying template creates schedule/Gantt unless schedule/Gantt exists.
4. Claim applying template creates finance/quote/baseline unless those modules exist.
5. Edit published template version directly.
6. Let template WBS move create cycles.
7. Let template task dependency create cycles.
8. Apply template by bypassing Project Core validation.
9. Partially create project records on failed template apply.
10. Ignore idempotency for template apply.
11. Hide deferred template-finance/schedule/AI/document features.
```

---

# 26. Prompt to give coding agent

```text
You are implementing Phase 11 — TO-BE Project Template, Phase Catalog, Template WBS & Template Task Foundation.

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
- Existing project module code, migrations, tests

Your task:
1. Compare current Project/PhaseDefinition implementation against this Phase 11 TO-BE spec.
2. Classify every Phase 11 capability as CURRENTLY_IMPLEMENTED, PARTIALLY_IMPLEMENTED, MUST_IMPLEMENT_IN_PHASE_11, SEED_ONLY_IN_PHASE_11, DEFERRED_TO_PHASE_XX, DEFERRED_TO_POST_23_BACKLOG, or NOT_IN_SCOPE_FOR_BACKEND_NOW.
3. Implement only Phase 11 required items.
4. Harden PhaseDefinition catalog.
5. Implement ProjectTemplate and ProjectTemplateVersion.
6. Implement TemplatePhase, TemplateWbsNode, TemplateTask, TemplateTaskDependency.
7. Ensure published versions are immutable.
8. Implement apply-template transaction that creates Project/Phase/WBS/Task/Dependency through Project Core rules.
9. Add idempotency protection for apply-template.
10. Add/verify template permissions and event definitions.
11. Add tests listed in this spec.
12. Run mvn compile and mvn test.
13. Create docs/phase-complete/PHASE_11_PROJECT_TEMPLATE_PHASE_CATALOG_TO_BE_COMPLETE.md with full gap matrix.

Do not implement or claim scheduling, Gantt, capacity, finance, quote, baseline, document generation, or AI planning in this phase.
```

---

# 27. Quick tracking matrix

| Capability | Current backend | Phase 11 action | Later phase |
|---|---|---|---|
| PhaseDefinition | Present | Harden/full catalog | — |
| Workspace phase setting | Missing/unknown | Implement simple or defer | Phase 23/39 if needed |
| ProjectTemplate | Missing/unknown | Must implement | — |
| ProjectTemplateVersion | Missing/unknown | Must implement | — |
| TemplatePhase | Missing | Must implement | — |
| TemplateWbsNode | Missing | Must implement | — |
| TemplateTask | Missing | Must implement | — |
| TemplateTaskDependency | Missing | Must implement | — |
| Apply template | Missing | Must implement | — |
| Template from existing project | Missing | Defer | Phase 16/21/Post-23 |
| Template marketplace | Missing | Defer | Post-23 |
| Schedule offsets execution | Missing | Defer | Phase 13 |
| Gantt presets | Missing | Defer | Phase 14 |
| Role/cost mapping | Missing | Defer | Phase 15/17 |
| Finance defaults | Missing | Defer | Phase 17 |
| Quote generation | Missing | Defer | Phase 18 |
| Baseline from template | Missing | Defer | Phase 19 |
| AI template generation | Missing | Defer | Phase 21 |
| Document generation | Missing | Defer | Phase 21/27 |

---

# 28. Final principle

Phase 11 is not complete when "a phase catalog exists."

Phase 11 is complete when Scopery can safely reuse project structure:

```text
approved template
published immutable version
template phases
template WBS hierarchy
template tasks
template dependencies
transactional apply
source template traceability
no fake scheduling
no fake finance
no fake AI
```

A template is a blueprint.

A project is live work.

Applying a template creates live project records, but it does not create capacity schedules, Gantt charts, budgets, quotes, baselines, or AI plans.
