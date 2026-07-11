# SCOPERY MODULAR ARCHITECTURE PRINCIPLE — Independent Modules, Optional Integrations & Loose Coupling

> Project: Scopery  
> Document type: Architecture addendum / roadmap-wide principle  
> Status: Mandatory principle for all current and future phases  
> Applies to: Phase 00 onward, especially Phase 24+ and all post-core modules  
> API base: `/api`  
> Core idea: Every business module should be usable independently with Core Platform only. Other modules may enrich it, but must not become hidden hard requirements.  
> Important rule: A module dependency list in phase documents must not be interpreted as “this module cannot work unless all previous modules exist.” Dependencies must be split into **hard required core** and **optional integrations**.

---

# 0. Why this addendum exists

Earlier phase documents often listed many previous phases in `Depends on`.

That can accidentally create the wrong interpretation:

```text
To use Support, the system must also have Project, WBS, Task, Resource, Profitability, Release, etc.
```

That is not the intended Scopery architecture.

The correct interpretation is:

```text
A module should run independently with Core Platform.
If related modules exist, the module can connect to them.
If related modules do not exist, the module must still work in standalone mode.
```

Example:

```text
Support / Maintenance can run as a standalone mini service desk.

If Project exists, a support case can link to a project.
If Task exists, a support case can create a task.
If Defect exists, a bug report can create a defect.
If Resource exists, support effort can feed utilization.
If Profitability exists, support cost can feed margin.
If those modules do not exist, support still works normally.
```

This addendum makes that principle explicit for all phases.

---

# 1. Main architecture principle

```text
Scopery is a loosely-coupled modular platform.
```

Meaning:

```text
Each business module is independent in its core workflow.

Modules communicate through optional references, events, permissions, read models, and integration links.

A module should not hard-crash, refuse to start, or become unusable just because another business module is disabled, unimplemented, or not purchased.
```

Short product principle:

```text
Tách bạch để dùng riêng được.
Liên kết để khi cần thì mạnh hơn.
Không ràng buộc để sản phẩm không bị nặng.
```

English version:

```text
Standalone by default.
Integrated when available.
Graceful when missing.
```

---

# 2. Hard core vs optional integrations

Every phase document must separate dependencies into two groups.

## 2.1 Hard required core

These are platform capabilities a module can reasonably require.

Default hard core:

```text
Workspace
User / Principal
IAM / Permission
Authentication / Authorization
Audit basics
Outbox / Idempotency basics
Event registry basics
Notification basics if module sends notifications
File / Attachment basics if module stores attachments
```

A module may require only the core services it truly needs.

## 2.2 Optional integrations

These are business modules that enrich the current module.

Examples:

```text
Project
WBS
Task
Requirement
Scope
Deliverable
Change Request
Quality / Defect
Release
Document Hub
Client Portal
External Party / CRM
Resource / Capacity
Revenue / Profitability
Support / Maintenance
Integration Hub
Trust / Privacy
AI Planning
Search
Custom Fields
```

Optional integration means:

```text
If module exists, current module can link to it.
If module does not exist, current module still works.
```

---

# 3. Required wording for future phase documents

Each new phase should use this dependency format.

```text
Hard required core:
- Workspace
- IAM / Permission
- Audit / Outbox
- Event Registry
- Notification basics if needed

Optional integrations:
- Project, if objects need project context
- Task, if work item linking is enabled
- Document Hub, if attachments/documents are enabled
- Portal, if external client access is enabled
- Resource, if effort/capacity integration is enabled
- Profitability, if cost/revenue impact integration is enabled
```

Avoid this wording:

```text
Depends on Phase 09, Phase 24, Phase 26, Phase 36, Phase 37...
```

Use this wording instead:

```text
Works standalone with Core Platform.
Optionally integrates with Project, Task, Quality, Resource, Profitability, Portal, and Integration modules when enabled.
```

If a phase still includes `Depends on`, clarify:

```text
Depends on means "can integrate with / should inspect if available", not "hard runtime requirement", unless explicitly listed under Hard required core.
```

---

# 4. Module independence rule

Every business module must pass this test:

```text
Can this module be used by a workspace that has only Core Platform plus this module?
```

If the answer is no, the module is too tightly coupled unless there is a strong product reason.

Examples:

```text
Support should work without Project.
Document Hub should work without Task.
Resource Capacity should work without Profitability.
Profitability should work without Resource, using manual cost inputs.
Meetings should work without Project, as standalone meetings.
Custom Fields should work without every object type, by enabling only supported targets.
Integration Hub should work without Support.
```

---

# 5. Graceful degradation rule

When an optional module is unavailable, disabled, or not configured, the current module must degrade gracefully.

Examples:

## 5.1 Support without Task

Available:

```text
Create support case
Assign owner
Add comments
Track SLA
Attach files
Resolve/close case
View support dashboard
```

Unavailable:

```text
Create task from support case
Link task to support case
Show task progress
```

System behavior:

```text
Hide task-related actions or return capability-not-enabled error.
Do not break support case workflow.
```

## 5.2 Support without Project

Available:

```text
Create standalone support case
Link to client/contact
Use support queue
Track SLA
Use portal if enabled
```

Unavailable:

```text
Project-specific support dashboard
Project delivery/handover link
Project task/defect/release link
```

System behavior:

```text
Support case project_id remains null.
Standalone service profile/client service profile is used.
```

## 5.3 Profitability without Resource

Available:

```text
Revenue source
Manual cost source
Manual adjustment
Profitability summary
Margin forecast
```

Unavailable:

```text
Auto cost from effort/capacity
Utilization-based cost forecast
Resource cost input
```

System behavior:

```text
Profitability uses manual/blended/default cost assumptions.
No failure.
```

## 5.4 Resource without Profitability

Available:

```text
Resource profile
Capacity calendar
Allocation
Effort estimate
Utilization
Workload dashboard
```

Unavailable:

```text
Push cost input to profitability
Profit margin impact
```

System behavior:

```text
Cost integration disabled.
Resource module still works.
```

## 5.5 Document Hub without Project

Available:

```text
Upload documents
Categorize documents
Version documents
Search documents
Permission documents
```

Unavailable:

```text
Project-specific document folder
Task-linked document
Deliverable-linked document
```

System behavior:

```text
Documents remain workspace/client/general documents.
```

---

# 6. Module Registry

Scopery should introduce or formalize a module registry.

Purpose:

```text
Track which modules exist, are enabled, and expose which capabilities.
```

Recommended concepts:

```text
ModuleDefinition
WorkspaceModuleSetting
ModuleCapability
ModuleDependencyDeclaration
FeatureFlag
CapabilityCheckService
```

---

## 6.1 ModuleDefinition

Represents a module known by Scopery.

Examples:

```text
CORE_WORKSPACE
CORE_IAM
CORE_AUDIT
PROJECT
TASK
DOCUMENT_HUB
CLIENT_PORTAL
CUSTOM_FIELDS
RESOURCE_CAPACITY
PROFITABILITY
SUPPORT_SERVICE
INTEGRATION_HUB
TRUST_PRIVACY
AI_ASSISTANT
```

Fields:

```text
module_code
name
description
category
enabled_by_default
core_module_flag
status
```

Statuses:

```text
AVAILABLE
BETA
DISABLED
DEPRECATED
FUTURE
```

---

## 6.2 WorkspaceModuleSetting

Represents whether a workspace can use a module.

Fields:

```text
workspace_id
module_code
enabled
enabled_at
enabled_by
disabled_at
disabled_by
configuration_json
```

Rules:

```text
1. Core modules may be always enabled.
2. Business modules can be enabled per workspace.
3. Disabling a module should not delete data.
4. Disabled modules should hide UI/API capabilities but preserve records.
```

---

## 6.3 ModuleCapability

Represents specific features exposed by a module.

Examples:

```text
TASK.CREATE
TASK.LINK
TASK.SEARCH
SUPPORT.CREATE_CASE
SUPPORT.PORTAL_INTAKE
SUPPORT.CREATE_TASK_FROM_CASE
PROFITABILITY.COST_INPUT
RESOURCE.UTILIZATION
DOCUMENT.ATTACHMENT
PORTAL.EXTERNAL_VIEW
```

Rules:

```text
1. APIs should check capability before optional cross-module actions.
2. Capabilities are more precise than module enabled flag.
3. A module can be enabled but some capabilities disabled.
```

---

## 6.4 CapabilityCheckService

Every optional integration should check capabilities through a central service.

Example pseudo-behavior:

```text
if capability TASK.CREATE is enabled:
    allow "create task from support case"
else:
    return MODULE_CAPABILITY_NOT_ENABLED
```

The module should not do:

```text
assume task service exists
call task service directly without checking
fail with null pointer / bean missing / table missing
```

---

# 7. Optional link model

Cross-module links should use optional link tables instead of mandatory foreign keys when the relationship is not core.

Example:

```text
support_work_link:
- id
- workspace_id
- source_object_type = SUPPORT_CASE
- source_object_id
- target_object_type = TASK / DEFECT / RELEASE / DOCUMENT / PROJECT / CHANGE_REQUEST
- target_object_id
- link_type
- created_at
- created_by
```

This allows:

```text
SupportCase can exist without Task.
SupportCase can later link to Task if Task module exists.
SupportCase can link to multiple object types.
```

Bad design:

```text
support_case.task_id NOT NULL
support_case.project_id NOT NULL
support_case.defect_id NOT NULL
```

Good design:

```text
support_case.project_id NULL
support_case.service_profile_id NULL
support_work_link optional
```

---

# 8. Reference strategy

Use three levels of reference.

## 8.1 Hard reference

Use hard FK when the object is part of the module's own core or platform core.

Examples:

```text
support_case.workspace_id NOT NULL
support_case.request_type_id NOT NULL
support_case.queue_id NULL but if present references support_queue
```

## 8.2 Soft optional reference

Use nullable FK when integration is common but not required.

Examples:

```text
support_case.project_id NULL
service_profile.external_organization_id NULL
profitability_profile.project_id NULL if standalone profitability allowed
```

## 8.3 Generic link reference

Use object type + object id for flexible cross-module links.

Examples:

```text
support_work_link.target_object_type
support_work_link.target_object_id

document_link.target_object_type
document_link.target_object_id

governance_object_link.target_object_type
governance_object_link.target_object_id
```

Rules:

```text
1. Generic links must be permission-checked.
2. Generic links must validate module capability if target module exists.
3. Broken links should be detectable by integrity check jobs.
4. Generic links must not bypass domain service rules.
```

---

# 9. API design rule

APIs should be grouped by module core first, optional integration second.

Example Support APIs:

Core support:

```text
POST /api/workspaces/{workspaceId}/support/cases
GET  /api/workspaces/{workspaceId}/support/cases
POST /api/workspaces/{workspaceId}/support/cases/{caseId}/comments
POST /api/workspaces/{workspaceId}/support/cases/{caseId}/resolve
```

Optional project support:

```text
GET /api/projects/{projectId}/support/cases
```

Optional task integration:

```text
POST /api/workspaces/{workspaceId}/support/cases/{caseId}/create-task
POST /api/workspaces/{workspaceId}/support/cases/{caseId}/link-task/{taskId}
```

Optional portal support:

```text
POST /api/portal/support/cases
GET  /api/portal/support/cases
```

Rule:

```text
The core support APIs must work even if optional project/task/portal APIs are disabled.
```

---

# 10. Error design

Use clear errors for disabled optional modules/capabilities.

Recommended error codes:

```text
MODULE_NOT_ENABLED
MODULE_CAPABILITY_NOT_ENABLED
OPTIONAL_INTEGRATION_NOT_AVAILABLE
OPTIONAL_TARGET_MODULE_DISABLED
OPTIONAL_TARGET_OBJECT_NOT_FOUND
OPTIONAL_TARGET_PERMISSION_DENIED
OPTIONAL_LINK_NOT_SUPPORTED
```

Examples:

```text
SUPPORT_TASK_INTEGRATION_NOT_ENABLED
SUPPORT_PROJECT_INTEGRATION_NOT_ENABLED
PROFITABILITY_RESOURCE_COST_INPUT_NOT_ENABLED
DOCUMENT_PORTAL_VISIBILITY_NOT_ENABLED
```

Do not return confusing errors like:

```text
NullPointerException
Table not found
Task service bean missing
500 Internal Server Error
```

---

# 11. Event design

Modules should communicate through events where possible.

Example:

```text
SupportCaseCreated
SupportCaseResolved
SupportEffortRecorded
ResourceCostInputRebuilt
ProfitabilitySummaryUpdated
```

If Resource module exists:

```text
SupportEffortRecorded can be consumed to update ActualEffortRecord.
```

If Resource module does not exist:

```text
Event is still emitted, but no Resource consumer handles it.
```

If Profitability module exists:

```text
ServiceCostInputRebuilt can update CostForecast.
```

If Profitability module does not exist:

```text
Event remains in event history/outbox but no profitability update happens.
```

Rule:

```text
Producer module should not hard-depend on consumer module.
```

---

# 12. Optional integration adapters

For cross-module behavior, prefer adapter interfaces.

Example:

```text
TaskIntegrationPort
DocumentIntegrationPort
ResourceIntegrationPort
ProfitabilityIntegrationPort
PortalIntegrationPort
```

Support module can depend on an interface:

```text
SupportService -> TaskIntegrationPort
```

The implementation can be:

```text
RealTaskIntegrationAdapter if Task module enabled
NoopTaskIntegrationAdapter if Task module disabled
```

Noop adapter returns:

```text
capability disabled
not available
empty result
```

This prevents hard runtime dependency.

---

# 13. Read model strategy

Dashboards should support both standalone and integrated modes.

Example SupportDashboard:

Standalone metrics:

```text
open cases
cases by priority
SLA at risk
SLA breached
incidents
maintenance windows
```

Integrated metrics if Resource exists:

```text
support effort by resource
resource overload from support
```

Integrated metrics if Profitability exists:

```text
support cost impact
maintenance cost impact
profitability impact
```

Integrated metrics if Project exists:

```text
support cases by project
project support health
```

Rule:

```text
A dashboard should show available sections and hide unavailable sections.
```

---

# 14. Search strategy

Search should be modular.

Core search:

```text
Search within current module objects.
```

Optional cross-module search:

```text
Search linked tasks/documents/projects if those modules are enabled and user has permission.
```

Rules:

```text
1. Search index must handle missing modules.
2. Search result type should include module code.
3. Sensitive fields follow Phase 38 masking.
4. Disabled module results should not appear unless policy allows archived/disabled data search.
```

---

# 15. Reporting/export strategy

Reports should be modular.

Example Support report standalone:

```text
cases by status
cases by type
SLA breach rate
incident count
maintenance windows
```

Optional columns:

```text
project name
task count
defect link
resource effort
support cost
profitability impact
```

Rules:

```text
1. Optional columns appear only if module/capability enabled.
2. Report API should allow `include=` flags.
3. Export must follow Phase 38 masking and audit.
4. Missing optional module should not fail report.
```

---

# 16. Portal strategy

Client Portal should be optional.

A module can work internally without portal.

If Portal exists:

```text
Support can expose client support cases.
Document Hub can expose client documents.
Deliverables can expose acceptance.
Maintenance can expose notices.
```

If Portal does not exist:

```text
Internal module still works.
```

Rule:

```text
Portal visibility is always explicit.
No module should assume external users can see internal records.
```

---

# 17. Custom fields strategy

Custom Fields should be optional extension, not core schema dependency.

Module core entities should work without custom fields.

If Custom Fields module exists:

```text
SupportCase can have custom fields.
Project can have custom fields.
ResourceProfile can have custom fields.
Document can have custom fields.
```

If not:

```text
Core fields still work.
```

Rule:

```text
Do not store mandatory business-critical data only in custom fields.
Core workflow should not depend on custom field existence.
```

---

# 18. Trust / Privacy integration strategy

Trust/Privacy can be a platform module that progressively protects other modules.

If Trust module exists:

```text
Sensitive field classification
Sensitive access logging
Export audit
Retention policy
Privacy request handling
```

If Trust module is not fully implemented:

```text
Module still uses baseline audit and permission.
Sensitive fields should still be protected by basic permission.
```

Rule:

```text
Trust integration should harden security, not make business module unusable.
```

For enterprise product, Trust may become hard core. But roadmap documents should clearly say when it becomes hard required.

---

# 19. Integration Hub strategy

Integration Hub is optional.

If Integration Hub exists:

```text
Support can import email/chat tickets.
Document Hub can sync external storage.
Meetings can sync calendar.
Reports can export to BI.
```

If not:

```text
Manual/internal workflow still works.
```

Rule:

```text
No module should claim provider integration unless Integration Hub provider adapter is implemented and tested.
```

---

# 20. AI strategy

AI should be optional.

If AI module exists:

```text
Suggest summaries
Suggest mappings
Suggest risks
Suggest next actions
Explain variance
Draft client response
```

If not:

```text
Human workflow still works.
```

Rules:

```text
1. AI cannot be required for core workflow.
2. AI output is suggestion/draft unless explicitly accepted.
3. AI must respect permissions and sensitive data rules.
4. AI absence must not break module.
```

---

# 21. Module contract template

Every module should define its contract.

Example format:

```text
Module: SUPPORT_SERVICE

Standalone capabilities:
- Create support cases
- Manage queue/assignment
- Add comments/attachments
- Track SLA
- Manage incidents
- Manage maintenance windows
- View support dashboard

Optional integrations:
- PROJECT: link case to project
- TASK: create/link task from case
- QUALITY: create/link defect from case
- RELEASE: link fix release
- DOCUMENT: attach documents and knowledge
- PORTAL: client support portal
- RESOURCE: support effort affects utilization
- PROFITABILITY: support cost affects margin
- INTEGRATION: email/chat/helpdesk import
- AI: case summary and duplicate suggestion

Disabled integration behavior:
- Hide optional actions
- Return MODULE_CAPABILITY_NOT_ENABLED when called directly
- Keep core support workflow working
```

---

# 22. Required module metadata

Each module should have a metadata descriptor.

Suggested fields:

```text
module_code
module_name
category
is_core
standalone_supported
hard_required_modules
optional_integrations
capabilities
api_prefixes
event_source_system
sensitive_object_types
default_permissions
default_roles
default_feature_flags
```

Example:

```text
module_code: SUPPORT_SERVICE
standalone_supported: true
hard_required_modules:
  - CORE_WORKSPACE
  - CORE_IAM
  - CORE_AUDIT
optional_integrations:
  - PROJECT
  - TASK
  - DOCUMENT_HUB
  - CLIENT_PORTAL
  - RESOURCE_CAPACITY
  - PROFITABILITY
  - INTEGRATION_HUB
  - AI_ASSISTANT
```

---

# 23. Database migration rule

Migrations must not assume optional modules exist unless the module is hard-required.

Bad migration:

```text
ALTER TABLE support_case ADD COLUMN task_id UUID NOT NULL REFERENCES task(id);
```

Good migration:

```text
ALTER TABLE support_case ADD COLUMN project_id UUID NULL;
CREATE TABLE support_work_link (... target_object_type, target_object_id ...);
```

If adding optional FK:

```text
project_id UUID NULL
```

And service logic must handle null.

---

# 24. Service layer rule

Domain service should not directly call optional module service unless through a capability-checked integration port.

Bad:

```text
supportService.createCase(...)
taskService.createTask(...)
```

Good:

```text
if capabilityCheck.enabled(workspaceId, "TASK.CREATE"):
    taskIntegrationPort.createTaskFromSupportCase(...)
else:
    skip or return capability disabled
```

---

# 25. Permission rule

Optional links do not grant access.

Example:

```text
A user assigned to SupportCase does not automatically get access to linked Project.
A user assigned to Task does not automatically get access to linked SupportCase.
A portal user with case access does not automatically get document access unless document is client-visible and granted.
```

Every linked object read must check permission for that target object.

---

# 26. Audit rule

When optional integration is used, audit both sides where appropriate.

Example:

```text
SupportCase created Task
```

Audit:

```text
support audit: case linked to task
task audit: task created from support case
activity event: support work linked
```

If optional integration is unavailable:

```text
No audit needed for skipped integration unless user attempted disabled action.
```

---

# 27. Testing rule

Every module must include standalone tests and integration-enabled tests.

## 27.1 Standalone tests

Example Support:

```text
supportCase_create_withoutProjectModule_success
supportCase_create_withoutTaskModule_success
supportDashboard_withoutResourceModule_success
supportCost_withoutProfitabilityModule_hiddenOrSkipped
```

## 27.2 Optional integration tests

```text
supportCase_createTask_whenTaskEnabled_success
supportEffort_feedsResource_whenResourceEnabled_success
supportCost_feedsProfitability_whenProfitabilityEnabled_success
portalCase_visible_whenPortalEnabled_success
```

## 27.3 Disabled capability tests

```text
createTaskFromSupportCase_whenTaskDisabled_returnsCapabilityNotEnabled
portalSupport_whenPortalDisabled_returnsCapabilityNotEnabled
resourceCostPush_whenResourceDisabled_noop
```

---

# 28. Completion file requirement

Every phase completion file must include:

```text
## Module Independence Verification
```

Required questions:

```text
1. Can this module run with Core Platform only?
2. Which APIs are standalone?
3. Which features require optional integrations?
4. What happens when optional modules are disabled?
5. Which optional integrations were tested?
6. Which optional integrations are deferred?
7. Does any DB field accidentally require optional module?
8. Does any service call optional module without capability check?
9. Does any assignment/link accidentally grant access?
10. Does dashboard/report degrade gracefully?
```

---

# 29. Phase document rewrite pattern

When rewriting phase docs, use this structure.

```text
Hard required core:
- Workspace
- IAM / Permission
- Audit / Outbox / Idempotency
- Event Registry

Standalone mode:
- What this module can do by itself

Optional integrations:
- Project: what becomes possible
- Task: what becomes possible
- Document: what becomes possible
- Portal: what becomes possible
- Resource: what becomes possible
- Profitability: what becomes possible
- Integration: what becomes possible
- AI: what becomes possible

Graceful degradation:
- What happens if each optional module is missing
```

---

# 30. Specific rewrite guidance for recent phases

---

## 30.1 Phase 36 — Revenue & Profitability

Standalone mode:

```text
Manual revenue source
Manual cost source
Manual adjustment
Profitability plan
Profitability snapshot
Project/client/workspace summary if Project/Client exists
```

Optional integrations:

```text
Quote: auto revenue from quote
ChangeRequest: revenue/cost impact
Task/WBS: cost from estimated effort
Resource: cost from utilization/effort/rate
Support: support cost impact
Portal: client-safe commercial value
AI: variance explanation
```

Graceful degradation:

```text
Without Resource, use manual/blended cost.
Without Quote, use manual revenue.
Without Project, allow workspace/client profitability profile if product needs.
Without AI, no explanation suggestions.
```

---

## 30.2 Phase 37 — Resource / Capacity

Standalone mode:

```text
Resource profiles
Roles/skills
Capacity calendars
Allocations
Effort estimates
Utilization
Workload dashboard
```

Optional integrations:

```text
Project: project allocation
Task: assignment workload
Quality/Defect: rework effort
Release: release support effort
Profitability: cost input
AI: staffing suggestions
```

Graceful degradation:

```text
Without Project, resource module can still track workspace/team capacity.
Without Task, effort can be manual or allocation-based.
Without Profitability, cost input is disabled but utilization works.
```

---

## 30.3 Phase 38 — Trust / Privacy

Standalone mode:

```text
Classification
Sensitive registry
Sensitive access logs
Export audit
Privacy request
Retention policy
Legal hold
Access review
Compliance evidence
```

Optional integrations:

```text
Document: restricted document access
Portal: portal data subject records
Custom Fields: sensitive custom fields
Profitability: sensitive cost/profit data
Resource: sensitive rate/cost data
Integration: provider export logs
AI: privacy-safe summaries
```

Graceful degradation:

```text
Without Document, document-specific trust controls hidden.
Without Portal, portal data subject scope hidden.
Without Profitability/Resource, cost/rate sensitive fields absent.
```

---

## 30.4 Phase 39 — Integration Hub

Standalone mode:

```text
Provider registry
Connection
Credential reference
Mapping
Import/export framework
Generic webhook
Sync foundation
Dashboard
```

Optional integrations:

```text
Project/Task: import/export projects/tasks
Document: storage sync
Meeting: calendar sync
Support: helpdesk/email/chat intake
Profitability: finance/revenue data import/export
Resource: HR/resource data import
Trust: export audit and masking
AI: mapping suggestions
```

Graceful degradation:

```text
Without Project, project import templates disabled.
Without Support, helpdesk integrations disabled.
Without Trust, baseline export audit still required; advanced masking if available.
```

---

## 30.5 Phase 40 — Service / Support / Maintenance

Standalone mode:

```text
Service profile
Support case
Queue
Assignment
Comments
Attachments if basic file exists
SLA
Incident
Problem
Maintenance window
Support dashboard
```

Optional integrations:

```text
Project: link support to project
Task: create/link task
Quality/Defect: create/link defect
Release: link fix release
Document: handover/knowledge/attachments
Portal: client support portal
Resource: support effort affects utilization
Profitability: support cost affects margin
Integration: email/chat/helpdesk intake
AI: support summary and duplicate detection
```

Graceful degradation:

```text
Without Project, support case is standalone or client-scoped.
Without Task, create-task action hidden.
Without Resource, support effort remains local.
Without Profitability, support cost impact hidden.
Without Portal, internal support still works.
```

---

# 31. Recommended roadmap addendum for Phase 00

Add this to Phase 00 or master roadmap:

```text
Scopery uses loosely-coupled modular architecture.

Each business module must support standalone mode with Core Platform only unless explicitly declared otherwise.

Cross-module dependencies are optional integrations, not hard runtime requirements.

A module may expose additional features when related modules are enabled.

If optional modules are disabled or unavailable, the module must degrade gracefully by hiding optional features or returning MODULE_CAPABILITY_NOT_ENABLED.

No optional integration may silently grant access, bypass permissions, overwrite controlled records, or make the source module unusable.
```

---

# 32. Required coding-agent instruction

Use this instruction in future phase prompts:

```text
Important architecture rule:
Implement this module as standalone-capable.

Hard required core should be limited to platform basics such as workspace, IAM, audit/outbox, event registry, and required storage/notification primitives.

All business-module relationships must be optional integrations.

If Project, Task, Resource, Profitability, Portal, Document, Integration, AI, or other modules are disabled or missing, the current module must still run in standalone mode.

Use capability checks, nullable references, generic link tables, events, and no-op adapters to avoid hard coupling.

Add tests for:
1. standalone mode,
2. optional integration enabled,
3. optional integration disabled,
4. graceful degradation,
5. permission checks across linked objects.
```

---

# 33. Required anti-coupling rules

The coding agent must not:

```text
1. Add NOT NULL foreign keys to optional module tables.
2. Call optional module services without capability checks.
3. Make module startup fail when optional module is absent.
4. Require Project for modules that can be standalone.
5. Require Task/WBS for Support.
6. Require Resource for Profitability.
7. Require Profitability for Resource.
8. Require Portal for internal workflows.
9. Let optional links grant access automatically.
10. Hide optional module assumptions in code.
11. Return 500 when optional capability is disabled.
12. Claim integration exists because a field exists.
```

---

# 34. Error code examples

Generic:

```text
MODULE_NOT_ENABLED
MODULE_CAPABILITY_NOT_ENABLED
OPTIONAL_INTEGRATION_NOT_AVAILABLE
OPTIONAL_TARGET_MODULE_DISABLED
OPTIONAL_TARGET_PERMISSION_DENIED
OPTIONAL_LINK_NOT_SUPPORTED
```

Specific:

```text
SUPPORT_TASK_INTEGRATION_NOT_ENABLED
SUPPORT_PROJECT_INTEGRATION_NOT_ENABLED
SUPPORT_PORTAL_INTEGRATION_NOT_ENABLED
SUPPORT_RESOURCE_INTEGRATION_NOT_ENABLED
SUPPORT_PROFITABILITY_INTEGRATION_NOT_ENABLED

PROFITABILITY_RESOURCE_INTEGRATION_NOT_ENABLED
PROFITABILITY_QUOTE_INTEGRATION_NOT_ENABLED
RESOURCE_PROFITABILITY_INTEGRATION_NOT_ENABLED
DOCUMENT_PORTAL_INTEGRATION_NOT_ENABLED
INTEGRATION_PROVIDER_ADAPTER_NOT_IMPLEMENTED
```

---

# 35. API response pattern for disabled optional feature

Example:

```json
{
  "code": "MODULE_CAPABILITY_NOT_ENABLED",
  "message": "Task integration is not enabled for this workspace.",
  "details": {
    "module": "TASK",
    "capability": "TASK.CREATE",
    "sourceModule": "SUPPORT_SERVICE"
  }
}
```

Rules:

```text
1. Return 400 or 409 depending action semantics.
2. Return 403 if user lacks permission.
3. Return 404 only if target object not found or hidden by permission.
4. Do not return 500 for expected disabled capability.
```

---

# 36. Example: Support module standalone contract

Support module must work with only:

```text
Workspace
User/IAM
Audit
Notification basic
File/Attachment basic if attachments enabled
```

Standalone support capabilities:

```text
create case
list cases
case status lifecycle
queue
assignment
comments
attachments
SLA
incident
problem
maintenance
handover package
dashboard
reports
notifications
```

Optional actions:

```text
create task from case
create defect from case
link release
push support effort to resource
push support cost to profitability
portal case submission
email/chat intake
AI summary
```

Disabled behavior:

```text
optional actions hidden or return MODULE_CAPABILITY_NOT_ENABLED
core support remains usable
```

---

# 37. Example: Profitability module standalone contract

Profitability module must work with only:

```text
Workspace
IAM
Audit
Reporting basics
```

Standalone profitability capabilities:

```text
manual revenue source
manual cost source
profitability adjustment
profitability snapshot
margin dashboard
threshold status
reports
```

Optional actions:

```text
pull revenue from quote
pull cost from task/resource
pull support cost from service module
show project/client dimensions
AI variance explanation
```

Disabled behavior:

```text
manual mode remains usable
auto-source sections hidden
```

---

# 38. Example: Resource module standalone contract

Resource module must work with only:

```text
Workspace
IAM
Audit
User/team basics
```

Standalone resource capabilities:

```text
resource profile
role/skill
capacity calendar
capacity exception
allocation
effort estimate
utilization
workload snapshot
resource dashboard
```

Optional actions:

```text
link allocation to project
link assignment to task
push cost input to profitability
import HR/calendar data
AI staffing recommendation
```

Disabled behavior:

```text
workspace/team capacity still works
project/task/profitability integrations hidden
```

---

# 39. Implementation checklist

For every new module:

```text
[ ] Define standalone capabilities.
[ ] Define hard core requirements.
[ ] Define optional integrations.
[ ] Add module/capability metadata.
[ ] Use nullable references for optional module links.
[ ] Use generic link tables for flexible cross-module links.
[ ] Add capability checks before optional actions.
[ ] Add no-op adapter for disabled optional integration.
[ ] Add standalone tests.
[ ] Add integration-enabled tests.
[ ] Add integration-disabled tests.
[ ] Add permission tests for linked objects.
[ ] Add dashboard/report graceful degradation tests.
[ ] Add completion file module independence section.
```

---

# 40. Final architecture principle

Scopery should feel powerful because modules connect.

But Scopery should feel light because modules do not force each other.

```text
Standalone module
+ optional links
+ events
+ capability checks
+ no-op adapters
+ permission-safe references
+ graceful degradation
= modular Scopery
```

The product principle stays:

```text
Tách bạch để dùng riêng được.
Liên kết để khi cần thì mạnh hơn.
Không ràng buộc để sản phẩm không bị nặng.
```
