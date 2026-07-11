# PHASE 10 — TO-BE Project Authorization Hardening, Workspace Access Enforcement & Project Security Boundary

> Project: Scopery Backend  
> Phase: 10  
> Document type: TO-BE implementation-grade specification  
> Status: Planning / Gap-driven implementation spec  
> Depends on: Phase 00 — Master Backend Roadmap, Phase 01 — API Path / Security Baseline, Phase 02 — IAM Core, Phase 03 — Workspace Core, Phase 04 — Platform Audit / Outbox / Idempotency, Phase 05 — Event Registry, Phase 06 — Notification Engine, Phase 07 — AI Agent Platform, Phase 08 — Knowledge / Document Type Catalog, Phase 09 — Project Core / Phase / WBS / Task  
> API base: `/api`  
> Primary module: `modules/project`  
> Related modules: `iam`, `workspace`, `platform/security`, future `capacity`, `finance`, `quote`, `baseline`, `AI-assisted planning`, `reporting`  
> Important rule: This file is **not an as-is document**. It defines the TO-BE authorization hardening layer for Project Core. It must compare current implementation against target authorization behavior and explicitly mark gaps.

---

# 0. Purpose of this file

Phase 09 creates or verifies the project planning foundation.

Phase 10 secures it.

Phase 10 exists because project data is sensitive:

```text
project scope
WBS structure
tasks
assignees
deadlines
dependency graph
future cost estimates
future margin/profitability
future quotes
future baselines
future AI suggestions
future client-facing deliverables
```

A user who can see one workspace must not automatically see another workspace.

A user who can view a project must not automatically update tasks.

A user who can update a task must not automatically archive a project.

A user who receives a notification must not automatically gain access to hidden project detail.

Phase 10 makes the project security boundary explicit.

---

# 1. Phase 10 target statement

Phase 10 must deliver a secure, testable authorization layer for all Project Core APIs.

Target:

```text
1. Every Project read/write endpoint is protected by IAM.
2. Every Project query is filtered by workspace access.
3. Every Project write validates workspace membership and permission.
4. Every child resource path checks parent ownership.
5. Cross-workspace / cross-project IDOR is blocked.
6. Task assignee/in-charge validation remains enforced.
7. Project authorization decisions are centralized, not copied manually in every action.
8. Authorization tests cover positive, negative, cross-workspace, and inactive membership cases.
9. Per-project IAM is explicitly deferred unless required by private project sharing.
10. Future modules know which additional permissions must be added later.
```

---

# 2. Source inputs

Before coding Phase 10, the agent must read:

```text
1. Current backend codebase
2. Phase 09 Project Core spec and implementation
3. Phase 02 IAM TO-BE spec
4. Phase 03 Workspace TO-BE spec
5. Existing Project module controllers/actions/query services
6. Existing WorkspaceIamIntegrationService or equivalent
7. Existing ProjectWorkspaceAuthorizationService if present
8. Current security configuration
9. Current tests around Project actions and queries
10. Existing phase-complete docs if any
```

The agent must not add project features in this phase. The task is authorization hardening.

---

# 3. Current backend snapshot

Current project module likely already has some authorization hardening.

Current known or expected implementation may include:

```text
ProjectWorkspaceAuthorizationService
WorkspaceIamIntegrationService.requireWorkspaceAccess(...)
Project action permission checks
Project query permission checks
Task assignee validation against active workspace member
Endpoint security under /api
No /api/v1 project routes
```

But Phase 10 must not assume this is complete.

The agent must classify current state:

```text
CURRENTLY_IMPLEMENTED
PARTIALLY_IMPLEMENTED
MUST_IMPLEMENT_IN_PHASE_10
DEFERRED_TO_PHASE_XX
```

For every endpoint/action/query listed in this file.

---

# 4. Classification labels

Every requirement uses one label:

| Label | Meaning |
|---|---|
| `CURRENTLY_IMPLEMENTED` | Current backend already implements it. Agent must verify and test. |
| `PARTIALLY_IMPLEMENTED` | Current backend implements some but not all rules. |
| `MUST_IMPLEMENT_IN_PHASE_10` | Must be implemented or hardened now. |
| `SEED_ONLY_IN_PHASE_10` | Seed permission/action/event definitions now; full consumer later. |
| `DEFERRED_TO_PHASE_XX` | Do not implement now; later phase must return. |
| `DEFERRED_TO_POST_23_BACKLOG` | Outside core 00–23 roadmap, part of full Work OS expansion. |
| `NOT_IN_SCOPE_FOR_BACKEND_NOW` | Explicitly not backend scope now. |

---

# 5. In scope

Phase 10 covers authorization/security only for current Project Core objects:

```text
Project
ProjectPhase
PhaseDefinition read/use
WbsNode
Task
TaskDependency
Project query services
Project read models / DTO mappers
Project controller endpoints
Project action services
Project event/audit authorization metadata
```

Phase 10 also covers:

```text
Workspace access checks
IAM authority checks
Path ownership checks
Query filtering
IDOR prevention
Inactive workspace/member behavior
Archived project behavior from security perspective
Permission seed verification
Authorization test matrix
```

---

# 6. Out of scope

Phase 10 must not implement:

```text
Project templates
Capacity scheduling
Gantt
Rate card
Finance/margin
Quote
Baseline/change request
Agile sprint/backlog
Timesheet
Document repository
AI project planning
Reporting dashboards
External client portal project access
Per-project IAM resource unless explicitly required now
```

Per-project IAM is deferred unless product requires private projects now.

---

# 7. Security model decision

## 7.1 Current recommended model

For core 00–23 roadmap, recommended authorization model:

```text
Workspace-scoped project permissions
```

Meaning:

```text
A project belongs to a workspace.
Access to project operations is checked against the workspace IAM resource.
Authorities are project-related actions scoped to workspace.
```

Example:

```text
PROJECT_CREATE on WORKSPACE resource
PROJECT_VIEW on WORKSPACE resource
PROJECT_TASK_UPDATE on WORKSPACE resource
```

This is sufficient while all workspace project members share the same project visibility model.

## 7.2 Per-project IAM resource

Per-project IAM is not required in Phase 10 unless product needs:

```text
private projects inside workspace
client-specific project access
vendor-limited project access
project-level confidential finance
project-level external portal sharing
project-level document restrictions
```

If not needed now:

```text
DEFERRED_TO_PHASE_19 or PHASE_29/30 depending on baseline/external collaboration requirements.
```

Completion file must state:

```text
Per-project IAM resource: implemented / not implemented / deferred.
Reason:
```

---

# 8. Central authorization service

Phase 10 should use a centralized service.

Suggested service:

```text
ProjectWorkspaceAuthorizationService
```

or current equivalent.

Required methods:

```text
requireProjectCreate(workspaceId)
requireProjectView(workspaceId)
requireProjectUpdate(workspaceId)
requireProjectArchive(workspaceId)

requireProjectPhaseView(workspaceId)
requireProjectPhaseCreate(workspaceId)
requireProjectPhaseUpdate(workspaceId)
requireProjectPhaseArchive(workspaceId)
requireProjectPhaseComplete(workspaceId)

requireWbsView(workspaceId)
requireWbsCreate(workspaceId)
requireWbsUpdate(workspaceId)
requireWbsMove(workspaceId)
requireWbsArchive(workspaceId)

requireTaskView(workspaceId)
requireTaskCreate(workspaceId)
requireTaskUpdate(workspaceId)
requireTaskAssign(workspaceId)
requireTaskStatusUpdate(workspaceId)
requireTaskArchive(workspaceId)

requireTaskDependencyView(workspaceId)
requireTaskDependencyCreate(workspaceId)
requireTaskDependencyRemove(workspaceId)
```

If current code uses fewer permission methods, map them.

Minimum acceptable simplification:

```text
ProjectPhase create/update/archive → PROJECT_PHASE_CREATE / UPDATE / ARCHIVE
WBS move → PROJECT_WBS_UPDATE
Task dependency create/remove → PROJECT_TASK_UPDATE
Task status update → PROJECT_TASK_UPDATE
```

But the completion file must document simplification.

---

# 9. IAM permission matrix

## 9.1 Project permissions

| Operation | Required authority |
|---|---|
| Create project | `PROJECT_CREATE` |
| View project | `PROJECT_VIEW` |
| List projects | `PROJECT_VIEW` |
| Update project | `PROJECT_UPDATE` |
| Activate project | `PROJECT_UPDATE` or `PROJECT_ACTIVATE` |
| Archive project | `PROJECT_ARCHIVE` |

## 9.2 Project phase permissions

| Operation | Required authority |
|---|---|
| List project phases | `PROJECT_PHASE_VIEW` |
| Get project phase | `PROJECT_PHASE_VIEW` |
| Create project phase | `PROJECT_PHASE_CREATE` |
| Update project phase | `PROJECT_PHASE_UPDATE` |
| Activate project phase | `PROJECT_PHASE_UPDATE` or `PROJECT_PHASE_ACTIVATE` |
| Complete project phase | `PROJECT_PHASE_UPDATE` or `PROJECT_PHASE_COMPLETE` |
| Archive project phase | `PROJECT_PHASE_ARCHIVE` |

## 9.3 WBS permissions

| Operation | Required authority |
|---|---|
| List WBS nodes | `PROJECT_WBS_VIEW` |
| Get WBS node | `PROJECT_WBS_VIEW` |
| Get WBS tree | `PROJECT_WBS_VIEW` |
| Create WBS node | `PROJECT_WBS_CREATE` |
| Update WBS node | `PROJECT_WBS_UPDATE` |
| Move WBS node | `PROJECT_WBS_MOVE` or `PROJECT_WBS_UPDATE` |
| Archive WBS node | `PROJECT_WBS_ARCHIVE` |

## 9.4 Task permissions

| Operation | Required authority |
|---|---|
| List tasks | `PROJECT_TASK_VIEW` |
| Get task | `PROJECT_TASK_VIEW` |
| Create task | `PROJECT_TASK_CREATE` |
| Update task | `PROJECT_TASK_UPDATE` |
| Assign/change assignee | `PROJECT_TASK_ASSIGN` or `PROJECT_TASK_UPDATE` |
| Start/block/complete/cancel task | `PROJECT_TASK_STATUS_UPDATE` or `PROJECT_TASK_UPDATE` |
| Archive task | `PROJECT_TASK_ARCHIVE` |

## 9.5 Task dependency permissions

| Operation | Required authority |
|---|---|
| List dependencies | `PROJECT_TASK_VIEW` or `PROJECT_TASK_DEPENDENCY_VIEW` |
| Create dependency | `PROJECT_TASK_UPDATE` or `PROJECT_TASK_DEPENDENCY_CREATE` |
| Remove dependency | `PROJECT_TASK_UPDATE` or `PROJECT_TASK_DEPENDENCY_REMOVE` |

## 9.6 PhaseDefinition permissions

PhaseDefinition has two modes.

### Mode A — basic catalog read/use in Phase 09/10

```text
Read system phase definitions:
- authenticated user
or
- PROJECT_PHASE_VIEW

Use active phase definition in ProjectPhase:
- PROJECT_PHASE_CREATE
```

### Mode B — full phase catalog management

```text
PHASE_DEFINITION_VIEW
PHASE_DEFINITION_CREATE
PHASE_DEFINITION_UPDATE
PHASE_DEFINITION_ARCHIVE
PHASE_DEFINITION_MANAGE_SYSTEM
```

Mode B is deferred to Phase 11.

---

# 10. Workspace access rules

Every Project Core operation must enforce workspace access.

Rules:

```text
1. User must be authenticated.
2. Project workspace must exist.
3. Workspace must be ACTIVE unless reading historical/archive data is explicitly allowed.
4. User must be active WorkspaceMember of workspace.
5. User must have relevant IAM authority on workspace resource.
6. Inactive workspace member is denied.
7. Suspended/removed org member is denied through workspace/member cascade.
8. Archived workspace blocks project mutations.
```

Query behavior:

```text
List projects must only return projects in workspaces the user can access.
Get project must deny if user lacks access.
Search must not leak project existence across workspace boundary.
```

---

# 11. IDOR / path ownership prevention

Phase 10 must eliminate IDOR.

Every nested endpoint must verify parent-child ownership.

## 11.1 Project path

```text
/api/projects/{projectId}/...
```

Rules:

```text
1. Load project by projectId.
2. Check workspace access.
3. Then load child resource and verify child.projectId == projectId.
```

## 11.2 Phase path

```text
/api/projects/{projectId}/phases/{phaseId}
```

Rules:

```text
phase.projectId must equal projectId
```

## 11.3 WBS path

```text
/api/projects/{projectId}/wbs-nodes/{wbsNodeId}
```

Rules:

```text
wbsNode.projectId must equal projectId
```

## 11.4 Task path

```text
/api/projects/{projectId}/tasks/{taskId}
```

Rules:

```text
task.projectId must equal projectId
```

## 11.5 Dependency path

```text
/api/projects/{projectId}/task-dependencies/{dependencyId}
```

Rules:

```text
dependency.projectId must equal projectId
```

## 11.6 Cross-project command references

Commands must verify referenced IDs belong to the same project:

```text
projectPhaseId belongs to projectId
wbsNodeId belongs to projectId
predecessorTaskId belongs to projectId
successorTaskId belongs to projectId
newParentWbsNodeId belongs to projectId
```

---

# 12. Query service hardening

Project query services must not load unauthorized records.

Required behavior:

```text
1. List projects requires workspaceId filter or internally filters accessible workspaces.
2. If workspaceId filter provided, require PROJECT_VIEW on that workspace.
3. If no workspaceId filter allowed, query only accessible workspace IDs.
4. Get by ID requires access to the owning workspace.
5. Child list endpoints require project view and child-specific view authority.
6. DTOs must not include hidden future-sensitive fields without permission.
```

Future-sensitive fields:

```text
financial summary
margin
cost
quote total
internal notes
AI prompt/output
document classification
client confidential fields
```

Phase 10 current project DTO should not contain finance/quote fields.

---

# 13. Controller hardening

Controllers must not rely only on frontend hiding buttons.

Rules:

```text
1. Every endpoint calls action/query service that enforces authorization.
2. Do not put all auth only in controller annotations if service can be reused elsewhere.
3. Internal service methods either:
   - enforce auth
   - or are explicitly named internal and only called by already-authorized actions.
4. Controllers never return JPA/domain objects directly.
5. Controllers never bypass action services for writes.
```

---

# 14. Write action hardening

Every write action must include:

```text
1. Load target / parent resource.
2. Resolve workspaceId.
3. Require active workspace/member access.
4. Require relevant IAM authority.
5. Validate business rules.
6. Persist changes.
7. Emit activity/event/audit as needed.
```

Ordering recommendation:

```text
For target-specific reads:
- avoid leaking existence across workspace boundary.
- load by id, then deny generically if not accessible.
```

Error behavior should avoid revealing hidden project existence.

---

# 15. Read action hardening

Reads must include:

```text
1. Workspace access check.
2. Project view permission.
3. Child-specific view permission if applicable.
4. Query filtering.
5. DTO filtering for future-sensitive fields.
```

If a user lacks permission:

```text
403 FORBIDDEN
```

If resource does not exist or inaccessible:

```text
Use existing convention:
- 404 for not found
- 403 for known but forbidden
But avoid cross-workspace enumeration.
```

Recommended:

```text
Return 404 for resource outside accessible scope if current security convention uses not-found masking.
```

Completion file must document chosen behavior.

---

# 16. Assignment authorization

Changing task assignee/inCharge has two dimensions:

```text
1. Actor permission to assign.
2. Target user eligibility.
```

Rules:

```text
1. Actor needs PROJECT_TASK_ASSIGN or PROJECT_TASK_UPDATE.
2. Target user must be active workspace member.
3. Target user must not be deactivated/suspended.
4. Future capacity/role fit is not checked in Phase 10.
5. Assignment event emitted only after successful authorization.
```

Future:

```text
Role/capacity validation — Phase 12/15/16.
```

---

# 17. Archived project security behavior

Archived project behavior:

```text
View:
- allowed if user has PROJECT_VIEW and workspace access.

Mutate:
- blocked for project child mutations.
```

Blocked:

```text
create phase
update phase
create WBS
move WBS
create task
update task
status change task
create dependency
remove dependency
```

Allowed:

```text
read project
read phases
read WBS
read tasks
read dependencies
```

unless product chooses stricter archived visibility.

---

# 18. Inactive workspace / member behavior

## 18.1 Inactive workspace

```text
No project mutations.
Read may be blocked or allowed for historical admin depending on product.
Default Phase 10: block normal project operations if workspace inactive/archived.
```

## 18.2 Inactive workspace member

```text
No project reads.
No project writes.
No task assignment target.
```

## 18.3 Suspended org member

```text
Denied through workspace membership cascade or direct check.
```

Phase 10 must test inactive membership.

---

# 19. Event/audit security

Phase 10 must ensure event and audit payloads do not leak hidden data.

Rules:

```text
1. Events emitted only after successful authorization and mutation.
2. Authorization denied event may be emitted by IAM/platform but must not include hidden project details.
3. Activity log visible to users must be filtered by project/workspace access.
4. Notification payloads must not include task/project details to unauthorized recipients.
```

Future finance/quote events need stricter masking.

---

# 20. Permission seed requirements

Phase 10 must verify or seed these authorities.

## 20.1 Required current authorities

```text
PROJECT_VIEW
PROJECT_CREATE
PROJECT_UPDATE
PROJECT_ARCHIVE
PROJECT_ACTIVATE

PROJECT_PHASE_VIEW
PROJECT_PHASE_CREATE
PROJECT_PHASE_UPDATE
PROJECT_PHASE_ARCHIVE
PROJECT_PHASE_ACTIVATE
PROJECT_PHASE_COMPLETE

PROJECT_WBS_VIEW
PROJECT_WBS_CREATE
PROJECT_WBS_UPDATE
PROJECT_WBS_MOVE
PROJECT_WBS_ARCHIVE

PROJECT_TASK_VIEW
PROJECT_TASK_CREATE
PROJECT_TASK_UPDATE
PROJECT_TASK_ASSIGN
PROJECT_TASK_STATUS_UPDATE
PROJECT_TASK_ARCHIVE

PROJECT_TASK_DEPENDENCY_VIEW
PROJECT_TASK_DEPENDENCY_CREATE
PROJECT_TASK_DEPENDENCY_REMOVE
```

If current IAM uses fewer authorities, the agent must document mapping.

Example simplification:

```text
PROJECT_TASK_UPDATE covers:
- task update
- task status update
- task assignment
- dependency create/remove
```

This is acceptable only if intentional.

## 20.2 Future authorities not required now

```text
PROJECT_FINANCE_VIEW
PROJECT_FINANCE_UPDATE
PROJECT_FINANCE_APPROVE
QUOTE_VIEW
QUOTE_APPROVE
BASELINE_APPROVE
CHANGE_REQUEST_APPROVE
GANTT_MOVE_TASK
CAPACITY_RECALCULATE
AI_PROJECT_PLAN_APPLY
REPORT_EXPORT
```

These are deferred to later phases.

---

# 21. Per-project IAM decision matrix

Per-project IAM should be introduced only if any of these become true:

| Requirement | Phase |
|---|---|
| Private projects inside workspace | Phase 11/19 |
| Client/external user can access only selected projects | Phase 29/30 |
| Project finance restricted to subset of workspace | Phase 17 |
| Project documents have project-level sharing | Phase 27/30 |
| Project-specific AI context restrictions | Phase 21/27 |
| Project-specific report export restrictions | Phase 22 |

Until then:

```text
Workspace-scoped project authorities are sufficient.
```

Phase 10 completion must include:

```text
Per-project IAM: not implemented.
Reason: workspace-scoped permission model is current product decision.
Future trigger: ...
```

---

# 22. Event Registry integration

Source system:

```text
SCOPERY_PROJECT
```

Phase 10 does not introduce new business events unless authorization events are added.

Optional security events:

```text
PROJECT_AUTHORIZATION_DENIED
PROJECT_CROSS_WORKSPACE_ACCESS_BLOCKED
PROJECT_IDOR_ATTEMPT_BLOCKED
```

Recommended:

```text
Use IAM/platform authorization-denied audit event rather than many module-specific security events, unless current architecture supports module-specific denied events.
```

If implemented, payload must not leak hidden data:

```text
actor.userId
attemptedAction
resourceType
resourceIdHash optional
workspaceId if accessible
reasonCode
traceId
occurredAt
```

---

# 23. Notification integration

Phase 10 should not send notifications.

Authorization failures may be audited, not emailed.

Future security notification:

```text
repeated suspicious access attempts → Phase 23 hardening
```

Do not notify normal users about every forbidden request.

---

# 24. Business rules master

## 24.1 Authorization rules

```text
AUTH-PRJ-001 Every project endpoint requires authenticated user.
AUTH-PRJ-002 Every project operation resolves workspaceId.
AUTH-PRJ-003 User must be active workspace member.
AUTH-PRJ-004 User must have required IAM authority on workspace.
AUTH-PRJ-005 Inactive workspace member denied.
AUTH-PRJ-006 Archived/inactive workspace blocks mutations.
AUTH-PRJ-007 Authorization service is centralized.
AUTH-PRJ-008 Internal services cannot bypass authorization accidentally.
```

## 24.2 IDOR rules

```text
IDOR-001 phaseId must belong to projectId.
IDOR-002 wbsNodeId must belong to projectId.
IDOR-003 taskId must belong to projectId.
IDOR-004 dependencyId must belong to projectId.
IDOR-005 command projectPhaseId must belong to projectId.
IDOR-006 command wbsNodeId must belong to projectId.
IDOR-007 dependency predecessor/successor must belong to projectId.
IDOR-008 WBS move parent must belong to projectId.
IDOR-009 Cross-workspace project IDs are not enumerable.
```

## 24.3 Query rules

```text
QUERY-001 List projects filters by accessible workspace.
QUERY-002 Get project requires PROJECT_VIEW.
QUERY-003 Child lists require project view + child view.
QUERY-004 Query DTOs hide future-sensitive fields unless permission.
QUERY-005 Query services enforce authorization, not only controllers.
```

## 24.4 Assignment security rules

```text
ASSIGN-001 Actor requires assign/update permission.
ASSIGN-002 Target user must be active workspace member.
ASSIGN-003 Target user cannot be inactive/deactivated.
ASSIGN-004 Assignment change is audited/activity logged.
```

## 24.5 Archived behavior rules

```text
ARCH-001 Archived project can be read with permission.
ARCH-002 Archived project blocks child mutations.
ARCH-003 Archived workspace blocks project mutations.
ARCH-004 Archived child resources cannot be mutated unless restore flow exists.
```

---

# 25. Error catalog requirements

Exact names follow current convention, but these concepts must exist.

```text
PROJECT_ACCESS_DENIED
PROJECT_WORKSPACE_ACCESS_DENIED
PROJECT_PERMISSION_DENIED
PROJECT_VIEW_PERMISSION_REQUIRED
PROJECT_UPDATE_PERMISSION_REQUIRED
PROJECT_ARCHIVE_PERMISSION_REQUIRED

PROJECT_PHASE_ACCESS_DENIED
PROJECT_WBS_ACCESS_DENIED
PROJECT_TASK_ACCESS_DENIED
PROJECT_TASK_DEPENDENCY_ACCESS_DENIED

PROJECT_PATH_MISMATCH
PROJECT_PHASE_PATH_MISMATCH
PROJECT_WBS_PATH_MISMATCH
PROJECT_TASK_PATH_MISMATCH
PROJECT_DEPENDENCY_PATH_MISMATCH

PROJECT_CROSS_WORKSPACE_ACCESS_DENIED
PROJECT_ASSIGNEE_NOT_ACTIVE_WORKSPACE_MEMBER
PROJECT_WORKSPACE_MEMBER_INACTIVE
PROJECT_WORKSPACE_ARCHIVED

PROJECT_RESOURCE_NOT_ACCESSIBLE
```

Avoid leaking existence:

```text
PROJECT_NOT_FOUND_OR_ACCESS_DENIED optional if project uses masked not-found behavior.
```

Completion file must document 403 vs 404 masking policy.

---

# 26. Required tests

Phase 10 is incomplete without tests.

---

## 26.1 Project authorization tests

```text
createProject_withProjectCreate_allowed
createProject_withoutProjectCreate_forbidden
viewProject_withProjectView_allowed
viewProject_withoutProjectView_forbidden
updateProject_withoutProjectUpdate_forbidden
archiveProject_withoutProjectArchive_forbidden
projectList_onlyAccessibleWorkspaceProjects
projectGet_crossWorkspace_forbiddenOrMaskedNotFound
inactiveWorkspaceMember_cannotViewProject
inactiveWorkspaceMember_cannotCreateProject
```

## 26.2 ProjectPhase authorization tests

```text
listPhases_withProjectPhaseView_allowed
listPhases_withoutProjectPhaseView_forbidden
createPhase_withoutProjectPhaseCreate_forbidden
updatePhase_withoutProjectPhaseUpdate_forbidden
archivePhase_withoutProjectPhaseArchive_forbidden
phasePathMismatch_rejected
phaseFromOtherProject_rejected
```

## 26.3 WBS authorization / IDOR tests

```text
listWbs_withProjectWbsView_allowed
listWbs_withoutProjectWbsView_forbidden
createWbs_withoutProjectWbsCreate_forbidden
updateWbs_withoutProjectWbsUpdate_forbidden
moveWbs_withoutProjectWbsMoveOrUpdate_forbidden
archiveWbs_withoutProjectWbsArchive_forbidden
wbsPathMismatch_rejected
wbsMoveParentFromOtherProject_rejected
wbsFromOtherWorkspace_forbiddenOrMasked
```

## 26.4 Task authorization / IDOR tests

```text
listTasks_withProjectTaskView_allowed
listTasks_withoutProjectTaskView_forbidden
createTask_withoutProjectTaskCreate_forbidden
updateTask_withoutProjectTaskUpdate_forbidden
assignTask_withoutAssignOrUpdate_forbidden
statusChangeTask_withoutStatusOrUpdate_forbidden
archiveTask_withoutArchive_forbidden
taskPathMismatch_rejected
taskPhaseFromOtherProject_rejected
taskWbsFromOtherProject_rejected
taskAssigneeNotWorkspaceMember_rejected
taskAssigneeInactiveWorkspaceMember_rejected
```

## 26.5 Dependency authorization / IDOR tests

```text
listDependencies_withTaskViewOrDependencyView_allowed
listDependencies_withoutPermission_forbidden
createDependency_withoutPermission_forbidden
removeDependency_withoutPermission_forbidden
dependencyPathMismatch_rejected
dependencyPredecessorFromOtherProject_rejected
dependencySuccessorFromOtherProject_rejected
dependencyCrossWorkspace_rejected
```

## 26.6 Archived/inactive boundary tests

```text
archivedProject_viewAllowedWithPermission
archivedProject_createTask_rejected
archivedProject_updateTask_rejected
archivedProject_createPhase_rejected
archivedProject_createDependency_rejected
archivedWorkspace_createProject_rejected
archivedWorkspace_listProject_behaviorDocumented
```

## 26.7 Query service tests

```text
projectQueryService_filtersByAccessibleWorkspaces
projectQueryService_requiresWorkspaceAccessWhenWorkspaceFilterProvided
taskQueryService_requiresProjectViewAndTaskView
wbsTreeQuery_requiresProjectViewAndWbsView
phaseQuery_requiresProjectViewAndPhaseView
queryDtos_doNotExposeFutureFinanceFields
```

## 26.8 Security regression tests

```text
allProjectControllers_underApiRequireAuth
noProjectEndpointPermitAll
noProjectV1Routes
controllersDoNotReturnJpaEntities
writeActionsGoThroughActionServices
```

---

# 27. Manual verification checklist

Completion file must include manual verification for:

```text
1. User with PROJECT_CREATE can create project.
2. User without PROJECT_CREATE cannot create project.
3. User with PROJECT_VIEW can view project.
4. User without PROJECT_VIEW cannot view project.
5. User cannot access project in another workspace.
6. User cannot list projects from inaccessible workspace.
7. User cannot use phaseId from another project in task create.
8. User cannot use wbsNodeId from another project in task create.
9. User cannot use predecessor/successor task from another project in dependency create.
10. Inactive workspace member cannot view/update/create.
11. Archived project blocks child mutations.
12. Assignee must be active workspace member.
13. Query endpoints return only authorized records.
14. Authorization denied does not leak sensitive details.
```

---

# 28. Acceptance criteria

Phase 10 is accepted only if:

```text
1. Current project authorization implementation is classified against TO-BE.
2. Every Project endpoint has authorization coverage.
3. Every Project query has authorization filtering.
4. Every child path validates parent ownership.
5. Cross-workspace access is blocked.
6. Inactive workspace member is denied.
7. Archived workspace/project mutation behavior is enforced.
8. Task assignee/inCharge active workspace member validation is enforced.
9. Permission matrix is implemented or simplified mapping is documented.
10. Per-project IAM decision is documented.
11. Authorization tests pass.
12. No /api/v1 project routes exist.
13. No project endpoint is accidentally permitAll.
14. mvn compile passes.
15. mvn test passes.
16. Completion file exists and includes gap matrix.
```

Agent must not mark complete if:

```text
mvn test fails
any project write endpoint lacks authorization
query service returns inaccessible projects
phase/task/wbs/dependency path mismatch is accepted
cross-workspace IDs can be used in commands
inactive workspace member can access project
archived project accepts child mutation
per-project IAM decision is not documented
```

---

# 29. Required phase completion file

Agent must create:

```text
docs/phase-complete/PHASE_10_PROJECT_AUTHORIZATION_TO_BE_COMPLETE.md
```

Required sections:

```text
# Phase 10 — Project Authorization Hardening TO-BE Complete

## 1. Summary
## 2. Source Inputs Reviewed
## 3. Current vs TO-BE Classification Matrix
## 4. Implemented in Current BE
## 5. Implemented / Hardened in This Phase
## 6. Deferred Items and Target Phase
## 7. Project Authorization Model Decision
## 8. Per-project IAM Decision
## 9. Permission Matrix Implemented
## 10. Simplified Permission Mappings
## 11. Query Authorization Strategy
## 12. IDOR / Path Ownership Strategy
## 13. Archived Project / Workspace Behavior
## 14. Inactive Membership Behavior
## 15. Assignment Authorization Rules
## 16. APIs Checked
## 17. Tests Added
## 18. Commands Run
## 19. Test Results
## 20. Manual Verification
## 21. Assumptions
## 22. Deviations From Prompt
## 23. Known Risks
## 24. Future Phases That Must Return to Project Authorization
```

---

# 30. Future phases that must return to Project Authorization

## 30.1 Phase 11 — Project Template / Phase Catalog

Must add authorization for:

```text
PROJECT_TEMPLATE_VIEW
PROJECT_TEMPLATE_CREATE
PROJECT_TEMPLATE_UPDATE
PROJECT_TEMPLATE_PUBLISH
PROJECT_TEMPLATE_ARCHIVE
PHASE_DEFINITION_MANAGE_SYSTEM
PHASE_DEFINITION_MANAGE_WORKSPACE
```

## 30.2 Phase 12 — Resource Calendar / Capacity

Must add:

```text
CAPACITY_VIEW
CAPACITY_RECALCULATE
PROJECT_ALLOCATION_MANAGE
RESOURCE_CALENDAR_VIEW
RESOURCE_CALENDAR_MANAGE
```

Capacity views must not leak private time-off detail.

## 30.3 Phase 13 — Scheduling

Must add:

```text
TASK_SCHEDULE_VIEW
TASK_SCHEDULE_RECALCULATE
TASK_SCHEDULE_OVERRIDE
```

Manual override must require stronger permission.

## 30.4 Phase 14 — Gantt

Must add:

```text
GANTT_VIEW
GANTT_RECALCULATE
GANTT_MOVE_TASK
GANTT_RESIZE_TASK
GANTT_MANAGE_BASELINE
```

Drag/drop must pass task update and schedule permissions.

## 30.5 Phase 15–17 — Rate/Finance

Must add finance-specific permissions:

```text
RATE_CARD_VIEW
RATE_CARD_MANAGE
PROJECT_FINANCE_VIEW
PROJECT_FINANCE_UPDATE
PROJECT_FINANCE_APPROVE
PROJECT_FINANCE_EXPORT
```

Not every project viewer can view margins/costs.

## 30.6 Phase 18 — Quote

Must add:

```text
QUOTE_VIEW
QUOTE_CREATE
QUOTE_UPDATE
QUOTE_SUBMIT
QUOTE_APPROVE
QUOTE_REJECT
QUOTE_EXPORT
```

Quote approval may require SoD.

## 30.7 Phase 19 — Baseline / Change Request

Must add:

```text
BASELINE_VIEW
BASELINE_CREATE
BASELINE_APPROVE
BASELINE_RESTORE
CHANGE_REQUEST_VIEW
CHANGE_REQUEST_CREATE
CHANGE_REQUEST_SUBMIT
CHANGE_REQUEST_APPROVE
CHANGE_REQUEST_REJECT
CHANGE_REQUEST_APPLY
```

## 30.8 Phase 20 — Project Notifications

Must ensure notification recipients still have access before detailed payload.

## 30.9 Phase 21 — AI-assisted Planning

Must implement dual authorization:

```text
user has project permission
agent has tool/action permission
human approval exists where required
```

## 30.10 Phase 22 — Reporting

Report queries/exports must enforce project/workspace/finance permissions.

## 30.11 Phase 23 — Core Hardening

Must perform full authorization audit:

```text
endpoint coverage
query coverage
DTO field masking
performance
regression security tests
```

## 30.12 Phase 29/30 — External Collaboration

May require per-project IAM resource and external user model.

---

# 31. Agent anti-bịa rules

The agent must not:

```text
1. Add new project business features in Phase 10.
2. Claim per-project IAM exists unless implemented with resources/grants/tests.
3. Rely only on frontend UI hiding buttons.
4. Rely only on controller annotations if service/query can bypass auth.
5. Return unauthorized project rows in list/search.
6. Allow child resource from another project in path command.
7. Allow cross-workspace task dependencies.
8. Allow inactive workspace member to view/update project data.
9. Let notifications or events leak hidden project data.
10. Hide simplified permission mappings.
11. Hide deferred authorization needs for finance/quote/baseline/AI/reporting.
```

---

# 32. Prompt to give coding agent

```text
You are implementing Phase 10 — TO-BE Project Authorization Hardening, Workspace Access Enforcement & Project Security Boundary.

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
- Current project module code, migrations, tests
- Existing IAM/workspace authorization services

Your task:
1. Compare current Project authorization against this Phase 10 TO-BE spec.
2. Classify every endpoint/action/query as CURRENTLY_IMPLEMENTED, PARTIALLY_IMPLEMENTED, MUST_IMPLEMENT_IN_PHASE_10, or DEFERRED.
3. Implement only authorization hardening.
4. Do not add scheduling, Gantt, finance, quote, baseline, AI planning, or reporting features.
5. Centralize project authorization through a service.
6. Enforce workspace access and IAM authorities for every Project Core action/query.
7. Enforce path ownership for ProjectPhase, WbsNode, Task, TaskDependency.
8. Block cross-workspace/cross-project IDOR.
9. Enforce inactive membership denial.
10. Enforce archived project mutation restrictions.
11. Add tests listed in this spec.
12. Run mvn compile and mvn test.
13. Create docs/phase-complete/PHASE_10_PROJECT_AUTHORIZATION_TO_BE_COMPLETE.md with full gap matrix.

Do not claim per-project IAM unless implemented and tested.
Do not hide simplified permission mappings.
```

---

# 33. Quick tracking matrix

| Capability | Current backend | Phase 10 action | Later phase |
|---|---|---|---|
| Workspace-scoped project auth | Present/partial | Harden/test | — |
| Project create/view/update/archive auth | Present/partial | Harden/test | — |
| ProjectPhase auth | Present/partial | Harden/test | — |
| WBS auth | Present/partial | Harden/test | — |
| Task auth | Present/partial | Harden/test | — |
| Dependency auth | Present/partial | Harden/test | — |
| Query filtering | Unknown/partial | Must verify/harden | Phase 23 audit |
| IDOR prevention | Partial | Must verify/harden | — |
| Assignee validation | Present | Verify/test | Phase 12 role/capacity |
| Per-project IAM | Missing/not needed | Defer | Phase 19/29/30 if needed |
| Finance field masking | Missing | Defer | Phase 17/22 |
| Quote authorization | Missing | Defer | Phase 18 |
| Baseline/CR auth | Missing | Defer | Phase 19 |
| AI dual auth | Missing | Defer | Phase 21 |
| Report export auth | Missing | Defer | Phase 22 |

---

# 34. Final principle

Phase 10 is not complete when "endpoints require login."

Phase 10 is complete when:

```text
Every project operation checks workspace access.
Every project operation checks the correct permission.
Every child resource belongs to the path parent.
Every query filters unauthorized data.
Inactive members are denied.
Archived project mutations are blocked.
Per-project IAM decision is documented.
Future sensitive modules know where to plug in.
```

Authentication is not authorization.

Workspace membership is not permission.

Project view is not task update.

Task update is not project archive.
