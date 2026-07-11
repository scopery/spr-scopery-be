# PHASE 05 — TO-BE Event Registry, Event Schema, Variables & Module Event Contract

> Project: Scopery Backend  
> Phase: 05  
> Document type: TO-BE implementation-grade specification  
> Status: Planning / Gap-driven implementation spec  
> Depends on: Phase 00 — Master Backend Roadmap, Phase 01 — API Path & Security Baseline, Phase 02 — IAM Core, Phase 03 — Workspace Core, Phase 04 — Platform Audit / Outbox / Idempotency  
> API base: `/api`  
> Primary module: `modules/eventregistry`  
> Important rule: This file is **not an as-is description**. It defines the TO-BE event registry contract required by Notification, AI Agent, Audit, Reporting, Workflow, and future modules.

---

# 0. Purpose of this file

Phase 05 defines the event catalog backbone of Scopery.

Scopery needs a reliable way to describe, version, validate, and consume business events across modules.

Examples:

```text
IAM_USER_CREATED
WORKSPACE_CREATED
PROJECT_CREATED
TASK_ASSIGNED
PROJECT_MARGIN_BELOW_TARGET
QUOTE_APPROVAL_REQUIRED
CHANGE_REQUEST_APPROVED
AI_PROJECT_PLAN_SUGGESTED
REPORT_EXPORT_COMPLETED
DOCUMENT_PUBLISHED
```

These events are used by:

```text
Notification engine
Email templates
AI event configurations
Audit and compliance
Reporting and analytics
Workflow automation
Webhook/integration future
Search/indexing future
```

Without Event Registry, modules will emit random string events with inconsistent payloads, breaking templates, automations, analytics, and AI workflows.

Phase 05 establishes the **event contract hub**.

---

# 1. Current backend snapshot

Current backend inventory shows Event Registry currently has:

```text
2 action-level functions
9 business rules
```

Current Event Registry functions:

```text
Event Definition Create / Update / Activate / Deactivate
Event Variables Upsert
```

Current entities:

```text
EventDefinition
EventVariable
```

Current rules include:

```text
1. EventDefinition code unique.
2. (sourceSystem, eventKey) unique.
3. Name required.
4. input/output schema valid JSON if provided.
5. Initial ACTIVE.
6. eventVersion starts at 1.
7. Code/sourceSystem/eventKey immutable.
8. DEPRECATED cannot be activated.
9. Event variables upsert deletes all existing variables then recreates from payload.
```

This is a good catalog foundation, but not yet a complete future-state event governance system.

---

# 2. Why current Event Registry is not enough yet

Current Event Registry likely supports basic event definition and variables, but future Scopery requires more:

```text
1. Every emitted event must be registered.
2. Every event must have a schema contract.
3. Event variables must support email templates and AI prompt rendering.
4. Event versioning must be explicit.
5. Event deprecation must not break consumers.
6. Event payloads must have data classification.
7. Sensitive variables must be marked.
8. Events must define allowed consumers.
9. Module seeders must be idempotent.
10. Notification templates must validate against variables.
11. AI EventConfig must only bind to active events.
12. Reporting/export must understand event taxonomy.
13. Event changes must be auditable.
14. Event registry itself must have authorization rules.
```

Phase 05 must move Event Registry from basic CRUD into a reliable event contract system.

---

# 3. Classification labels

Every requirement uses one label:

| Label | Meaning |
|---|---|
| `CURRENTLY_IMPLEMENTED` | Current backend already implements it. Agent must verify and test. |
| `MUST_IMPLEMENT_IN_PHASE_05` | Must be implemented or hardened now. |
| `SEED_ONLY_IN_PHASE_05` | Create seed definitions/contracts now; full emitter/consumer comes later. |
| `DEFERRED_TO_PHASE_XX` | Do not implement now; later phase must return to Event Registry. |
| `NOT_IN_SCOPE_FOR_BACKEND_NOW` | Explicitly not in backend scope for this roadmap. |

---

# 4. Phase 05 target statement

Phase 05 must deliver a future-ready Event Registry.

Target:

```text
1. EventDefinition is the canonical event catalog.
2. EventVariable is the canonical variable catalog for templates, AI prompts, rules, and docs.
3. Event code, sourceSystem, eventKey, and version are stable.
4. Event schema JSON is valid and versioned.
5. Event variables are validated against schema where possible.
6. Event definitions have lifecycle: ACTIVE, INACTIVE, DEPRECATED.
7. Deprecated events cannot be used for new configs/templates.
8. Existing active consumers are protected from breaking changes.
9. Event definitions can be seeded idempotently by modules.
10. Event changes are activity-logged and audited.
11. Event Registry has clear IAM permissions.
12. Future modules know which events to seed and when to return to Event Registry.
```

---

# 5. In scope

Phase 05 covers:

```text
EventDefinition model
EventVariable model
EventDefinition lifecycle
Event schema validation
Event variable validation
Event version semantics
Event deprecation semantics
Event seeders and idempotency
Event registry IAM permissions
Event registry events/audit
Event consumer safety rules
Integration contract with Notification
Integration contract with AI Agent
Integration contract with Platform Outbox
Integration contract with Reporting/Workflow future
Tests
Acceptance criteria
Deferred future gaps
```

---

# 6. Out of scope

Phase 05 must not implement:

```text
Full Notification engine
Full Email rendering
Full AI execution
Full Workflow automation
Webhook delivery
Kafka/RabbitMQ/event bus migration
Analytics warehouse
Event replay UI
Event sourcing
Full audit viewer
Event subscription UI
End-user notification preferences
```

Phase 05 defines event contracts and catalog behavior only.

---

# 7. Future-state event architecture

## 7.1 Event Registry role

Event Registry is the source of truth for:

```text
event code
source system
event key
event name
event description
event version
input schema
output schema if relevant
variable paths
variable types
variable required flags
data classification
lifecycle status
consumer compatibility
```

## 7.2 Platform Outbox role

Platform Outbox emits actual event instances.

Event Registry defines what an event means.

Platform Outbox stores that an event happened.

Notification/AI/Workflow consume event instances.

```text
EventDefinition = contract
PlatformEventOutbox = event occurrence
EmailRule/EventConfig = consumers
```

## 7.3 Module event seeder role

Each module must own its event definitions.

Examples:

```text
IamEventDefinitionInitializer
WorkspaceEventDefinitionInitializer
ProjectEventDefinitionInitializer
FinanceEventDefinitionInitializer
QuoteEventDefinitionInitializer
```

Seeders create/update event definitions idempotently.

---

# 8. EventDefinition TO-BE entity

If current schema differs, agent must map and document.

Suggested table:

```text
app_event_definition
```

Required fields:

```text
id UUID PK
code VARCHAR(150) NOT NULL
source_system VARCHAR(100) NOT NULL
event_key VARCHAR(150) NOT NULL
name VARCHAR(255) NOT NULL
description TEXT NULL
event_version INT NOT NULL DEFAULT 1
input_schema_json JSONB NULL
output_schema_json JSONB NULL
status VARCHAR(50) NOT NULL
data_classification VARCHAR(50) NULL
owner_module VARCHAR(100) NULL
is_system_event BOOLEAN NOT NULL DEFAULT true
deprecated_at TIMESTAMP NULL
deprecated_by UUID NULL
replacement_event_definition_id UUID NULL
created_at TIMESTAMP NOT NULL
created_by UUID NULL
updated_at TIMESTAMP NOT NULL
updated_by UUID NULL
version INT NOT NULL DEFAULT 0
```

Required constraints:

```text
unique code
unique source_system + event_key
event_version >= 1
status in ACTIVE, INACTIVE, DEPRECATED
data_classification enum if implemented
```

---

# 9. EventDefinition fields explained

## 9.1 `code`

Human-readable stable code.

Examples:

```text
WORKSPACE_CREATED
TASK_ASSIGNED
QUOTE_APPROVAL_REQUIRED
```

Rules:

```text
1. Required.
2. Uppercase snake case.
3. Globally unique.
4. Immutable after creation.
5. Must not contain version number unless product decides otherwise.
```

Regex:

```text
^[A-Z][A-Z0-9_]{2,149}$
```

## 9.2 `sourceSystem`

Module/system that owns the event.

Examples:

```text
SCOPERY_IAM
SCOPERY_WORKSPACE
SCOPERY_PROJECT
SCOPERY_FINANCE
SCOPERY_QUOTE
SCOPERY_AI_AGENT
SCOPERY_NOTIFICATION
SCOPERY_KNOWLEDGE
SCOPERY_REPORTING
SCOPERY_PLATFORM
```

Rules:

```text
1. Required.
2. Immutable.
3. Uppercase snake case.
4. Must match module ownership.
```

## 9.3 `eventKey`

Machine-friendly scoped event key.

Examples:

```text
user.created
workspace.created
task.assigned
quote.approval_required
```

Rules:

```text
1. Required.
2. Unique with sourceSystem.
3. Immutable.
4. Lowercase dot/underscore style preferred.
```

## 9.4 `eventVersion`

Rules:

```text
1. Starts at 1.
2. Increment only for breaking contract changes.
3. Non-breaking variable additions do not require new event code.
4. Removing or renaming required variables is breaking.
5. Changing variable data type is breaking.
```

If current implementation has only one EventDefinition row per event and eventVersion field, keep it simple.

Do not implement full multi-version event registry unless needed.

Deferred:

```text
EventDefinitionVersion entity — DEFERRED_TO_PHASE_23/38 unless product needs strict versioning now.
```

## 9.5 `inputSchemaJson`

Defines expected event payload.

Rules:

```text
1. Must be valid JSON if provided.
2. Should be JSON Schema-like.
3. Must include required variables where possible.
4. Must not contain secrets in examples.
```

## 9.6 `outputSchemaJson`

Usually optional.

Can be used for events that trigger AI or workflow outputs.

Rules:

```text
1. Valid JSON if provided.
2. Optional in Phase 05.
3. Do not invent output schema for normal business events.
```

## 9.7 `dataClassification`

Recommended values:

```text
PUBLIC_INTERNAL
INTERNAL
SENSITIVE
SECURITY
FINANCIAL
PERSONAL_DATA
CONFIDENTIAL_CLIENT
```

If current schema lacks this field:

```text
MUST_IMPLEMENT_IN_PHASE_05 if low risk.
Otherwise DEFERRED_TO_PHASE_38 with explicit note.
```

Data classification is important for:

```text
email payload safety
AI context access
audit/compliance
report export
document events
finance events
```

---

# 10. EventVariable TO-BE entity

Suggested table:

```text
app_event_variable
```

Required fields:

```text
id UUID PK
event_definition_id UUID NOT NULL
path VARCHAR(255) NOT NULL
name VARCHAR(255) NOT NULL
description TEXT NULL
data_type VARCHAR(50) NOT NULL
required BOOLEAN NOT NULL DEFAULT false
sensitive BOOLEAN NOT NULL DEFAULT false
example_value TEXT NULL
created_at TIMESTAMP NOT NULL
updated_at TIMESTAMP NOT NULL
```

Required constraints:

```text
unique event_definition_id + path
data_type enum
```

Suggested data types:

```text
STRING
NUMBER
BOOLEAN
DATE
DATETIME
UUID
OBJECT
ARRAY
EMAIL
URL
CURRENCY
PERCENT
```

---

# 11. EventVariable rules

## 11.1 Path rule

Variable path examples:

```text
actor.userId
organization.id
workspace.id
project.id
task.title
quote.totalAmount
margin.grossMarginPercent
reset.url
invitation.acceptUrl
```

Rules:

```text
1. Path required.
2. Dot notation.
3. Unique per event.
4. Must not start or end with dot.
5. Should map to event payload path.
```

## 11.2 Required rule

If variable is required:

```text
1. Event payload should include it.
2. Email template can assume it exists.
3. AI prompt templates can require it.
```

If optional:

```text
Consumers must handle missing value.
```

## 11.3 Sensitive rule

If `sensitive=true`:

```text
1. Variable must not be sent to all consumers by default.
2. Email templates must explicitly be allowed to use it.
3. AI event config must require permission/safety check before using it.
4. Logs should redact it.
```

Examples of sensitive variables:

```text
reset.url
invitation.acceptUrl
financial.marginPercent
quote.totalAmount
client.email
document.downloadUrl
```

---

# 12. Event lifecycle

## 12.1 Status

Required statuses:

```text
ACTIVE
INACTIVE
DEPRECATED
```

Optional future:

```text
DRAFT
PUBLISHED
ARCHIVED
```

Current inventory says initial status ACTIVE and DEPRECATED cannot be activated. Keep this unless changing lifecycle is necessary.

## 12.2 ACTIVE

Rules:

```text
1. Can be used by EmailRule.
2. Can be used by AI EventConfig.
3. Can be emitted by PlatformOutbox.
4. Can be shown in event selector.
```

## 12.3 INACTIVE

Rules:

```text
1. Existing historical outbox/event logs remain valid.
2. New consumers should not bind to INACTIVE events.
3. Emission should be blocked unless explicitly allowed for backward compatibility.
```

## 12.4 DEPRECATED

Rules:

```text
1. Cannot be activated.
2. New EmailRule/EventConfig cannot bind.
3. Existing consumers should show warning.
4. replacementEventDefinitionId recommended.
5. Deprecation is audited.
```

---

# 13. EventDefinition API TO-BE

All APIs use `/api`, no `/api/v1`.

## 13.1 Create event definition

```text
POST /api/event-definitions
```

Request:

```json
{
  "code": "TASK_ASSIGNED",
  "sourceSystem": "SCOPERY_PROJECT",
  "eventKey": "task.assigned",
  "name": "Task assigned",
  "description": "Emitted when a task is assigned to a user",
  "inputSchemaJson": {},
  "outputSchemaJson": null,
  "dataClassification": "INTERNAL"
}
```

Rules:

```text
1. code required and unique.
2. sourceSystem required.
3. eventKey required.
4. sourceSystem + eventKey unique.
5. name required.
6. schema JSON valid if provided.
7. initial status ACTIVE.
8. eventVersion = 1.
9. activity log EVENT_DEFINITION_CREATED.
10. audit if sensitive/system event.
```

## 13.2 Update event definition

```text
PUT /api/event-definitions/{id}
```

Updatable:

```text
name
description
inputSchemaJson
outputSchemaJson
dataClassification
```

Immutable:

```text
code
sourceSystem
eventKey
```

Rules:

```text
1. Event must exist.
2. DEPRECATED cannot be updated except maybe description/replacement.
3. Schema JSON valid.
4. Breaking changes must be blocked or require version increment.
5. If active consumers exist, dangerous changes must be rejected unless force flag is allowed.
```

Phase 05 minimum:

```text
Block code/sourceSystem/eventKey changes.
Validate JSON.
```

Advanced breaking-change detection:

```text
DEFERRED_TO_PHASE_23 if not implemented.
```

## 13.3 Activate / deactivate / deprecate

```text
PATCH /api/event-definitions/{id}/activate
PATCH /api/event-definitions/{id}/deactivate
PATCH /api/event-definitions/{id}/deprecate
```

Rules:

```text
1. Event must exist.
2. DEPRECATED cannot activate.
3. Deactivate blocks new bindings.
4. Deprecate should require replacementEventDefinitionId or reason where possible.
5. Existing historical logs remain.
```

If current code only supports activate/deactivate and DEPRECATED status, add deprecate endpoint if needed or document deferred.

## 13.4 Get/search event definitions

```text
GET /api/event-definitions/{id}
GET /api/event-definitions
```

Filters:

```text
sourceSystem
status
code
eventKey
dataClassification
ownerModule
```

Pagination required.

## 13.5 Event variables upsert

```text
PUT /api/event-definitions/{id}/variables
```

Rules:

```text
1. Event definition must exist.
2. Payload variable paths unique.
3. Each variable type must parse.
4. If replacing all variables, protect active templates/configs from breaking.
5. Required variable removal should be blocked if active consumers depend on it.
6. Activity log EVENT_VARIABLES_UPSERTED.
```

Current rule says delete all existing variables then recreate. That is acceptable for simple implementation, but Phase 05 must add consumer safety or document as risk.

## 13.6 Event variables read

```text
GET /api/event-definitions/{id}/variables
```

Should return:

```text
path
name
description
dataType
required
sensitive
exampleValue if safe
```

---

# 14. Authorization requirements

Event Registry is a configuration module. Not every user should manage events.

Required IAM authorities:

```text
EVENT_REGISTRY_VIEW
EVENT_REGISTRY_CREATE
EVENT_REGISTRY_UPDATE
EVENT_REGISTRY_ACTIVATE
EVENT_REGISTRY_DEACTIVATE
EVENT_REGISTRY_DEPRECATE
EVENT_REGISTRY_MANAGE_VARIABLES
EVENT_REGISTRY_MANAGE
```

Rules:

```text
1. Read/search requires EVENT_REGISTRY_VIEW or admin/system config permission.
2. Create requires EVENT_REGISTRY_CREATE.
3. Update requires EVENT_REGISTRY_UPDATE.
4. Activate/deactivate/deprecate requires EVENT_REGISTRY_MANAGE.
5. Upsert variables requires EVENT_REGISTRY_MANAGE_VARIABLES.
6. System seeders can bypass user auth through internal service.
7. Unauthorized access returns 403.
```

If current Event Registry endpoints are only protected by authentication but not permission, Phase 05 must add authorization or explicitly defer to IAM hardening.

Recommended classification:

```text
MUST_IMPLEMENT_IN_PHASE_05
```

---

# 15. Event seeder TO-BE

## 15.1 Seeder purpose

Each module must seed its event definitions and variables.

Seeder must be:

```text
idempotent
safe to rerun
non-destructive
consumer-safe
```

## 15.2 Seeder behavior

For each event definition:

```text
1. Find by code or sourceSystem + eventKey.
2. If missing, create.
3. If exists, update safe metadata only:
   - name
   - description
   - schemas if safe
   - dataClassification if missing
4. Do not change immutable code/sourceSystem/eventKey.
5. Do not delete event definition.
6. Add missing variables.
7. Do not remove variables if active consumers exist.
8. Log drift if expected variable differs.
```

## 15.3 Seeder class pattern

Suggested:

```text
IamEventDefinitionInitializer
WorkspaceEventDefinitionInitializer
ProjectEventDefinitionInitializer
NotificationEventDefinitionInitializer
AiAgentEventDefinitionInitializer
KnowledgeEventDefinitionInitializer
```

Future:

```text
FinanceEventDefinitionInitializer
QuoteEventDefinitionInitializer
BaselineChangeRequestEventDefinitionInitializer
ReportingEventDefinitionInitializer
DocumentEventDefinitionInitializer
WorkflowEventDefinitionInitializer
```

## 15.4 Seeder test requirement

Every module seeder must have:

```text
firstRun_createsEvents
secondRun_noDuplicates
missingVariable_added
existingConsumer_variableNotDeleted
```

---

# 16. Consumer safety rules

Event Registry is used by consumers:

```text
EmailRule / EmailTemplate
AI EventConfig
Workflow automation future
Webhook future
Reports future
```

## 16.1 Do not break active consumers

Before update/deactivate/deprecate/variable replacement, check:

```text
active EmailRule references event
active EmailTemplateVersion uses event variables
active AI EventConfig references event
active WorkflowRule future references event
active WebhookSubscription future references event
```

If active consumers exist:

```text
1. Safe metadata update allowed.
2. Removing required variable blocked.
3. Changing variable type blocked.
4. Deactivating event requires force/admin or no active consumers.
5. Deprecating event allowed with warning/replacement if consumers can migrate.
```

Phase 05 current requirement:

```text
At minimum, EventRegistry must expose repository/service method for consumers to check.
Notification and AI modules must be checked if already exist.
```

If active consumer checks are not implemented now, mark:

```text
DEFERRED_TO_PHASE_06_NOTIFICATION_ENGINE and PHASE_07_AI_AGENT_PLATFORM with risk.
```

---

# 17. Integration with Notification Engine

Current Notification module uses:

```text
EmailTemplate
EmailTemplateVersion
EmailRule
EmailOutbox
EmailDelivery
```

Event Registry TO-BE contract:

```text
1. EmailTemplate references EventDefinition.
2. EmailRule references EventDefinition.
3. EmailTemplateVersion variable validation uses EventVariable paths.
4. Publishing template version fails if template uses unknown variable.
5. Active EmailRule requires EventDefinition ACTIVE.
6. Deactivating EventDefinition with active EmailRule should be blocked or warn.
```

Required Phase 05 action:

```text
Verify existing integration.
Add missing repository/query methods if needed.
Document gaps for Phase 06.
```

---

# 18. Integration with AI Agent

Current AI Agent module includes:

```text
EventConfig
Agent
PromptVersion
PromptTemplate
ModelDeployment
ExecutionLog
UsagePolicy
```

Event Registry TO-BE contract:

```text
1. EventConfig must reference ACTIVE EventDefinition.
2. EventConfig environment uniqueness uses eventDefinitionId + environment.
3. Execute event can resolve EventDefinition by id OR sourceSystem+eventKey OR eventCode.
4. Input schema validation should use EventDefinition schema.
5. Prompt variable rendering should align with EventVariable where applicable.
6. Deprecating event should block new EventConfig activation.
```

Required Phase 05 action:

```text
Verify EventConfig references EventDefinition correctly.
Document gaps for Phase 07 AI Agent if missing.
```

---

# 19. Integration with Platform Outbox

Phase 04 defines event outbox. Phase 05 defines event registry.

TO-BE rule:

```text
PlatformOutbox should only emit eventCode that exists and is ACTIVE unless compatibility override exists.
```

Event emission validation:

```text
1. Look up EventDefinition by code.
2. Ensure ACTIVE.
3. Optionally validate payload against inputSchemaJson.
4. Optionally validate required EventVariable paths.
5. Store eventVersion.
```

If PlatformOutbox not implemented yet:

```text
DEFERRED_TO_PHASE_20 or Phase 04 follow-up, but EventRegistry must document expected integration.
```

---

# 20. Integration with Reporting and Analytics future

Future reporting needs event taxonomy.

Phase 05 must reserve fields:

```text
sourceSystem
ownerModule
dataClassification
eventVersion
status
```

Future reporting features:

```text
event volume by module
failed automation by event
notification delivery by event
AI execution by event
business timeline by event
```

Deferred:

```text
Event analytics dashboard — DEFERRED_TO_PHASE_22_REPORTING
```

---

# 21. Integration with Workflow future

Future workflow engine will use Event Registry as trigger catalog.

Requirements reserved:

```text
1. EventDefinition has stable code.
2. EventVariable paths are discoverable.
3. Variable data types are known.
4. Sensitive variables are marked.
5. Deprecated events not used for new workflow triggers.
```

Workflow automation itself:

```text
DEFERRED_TO_PHASE_32_WORKFLOW_AUTOMATION
```

---

# 22. Event taxonomy TO-BE

Events should follow naming convention.

## 22.1 Code convention

```text
<ENTITY>_<ACTION>
```

Examples:

```text
PROJECT_CREATED
TASK_ASSIGNED
QUOTE_APPROVAL_REQUIRED
CHANGE_REQUEST_APPROVED
```

For risk/alert:

```text
PROJECT_MARGIN_BELOW_TARGET
TASK_DUE_DATE_AT_RISK
RESOURCE_OVER_ALLOCATED
```

## 22.2 sourceSystem convention

```text
SCOPERY_<MODULE>
```

Examples:

```text
SCOPERY_IAM
SCOPERY_WORKSPACE
SCOPERY_PROJECT
SCOPERY_FINANCE
```

## 22.3 eventKey convention

```text
entity.action
```

Examples:

```text
project.created
task.assigned
quote.approval_required
```

---

# 23. Required event definitions by module

Phase 05 does not need to implement all emitters, but must define/seeds current and known future event contracts.

## 23.1 IAM events

Status:

```text
Phase 02 owns IAM events.
Phase 05 verifies consistency.
```

Required:

```text
IAM_USER_CREATED
IAM_USER_UPDATED
IAM_USER_ACTIVATED
IAM_USER_DEACTIVATED
IAM_USER_SUSPENDED
IAM_USER_LOGGED_IN
IAM_LOGIN_FAILED
IAM_USER_LOGGED_OUT
IAM_REFRESH_TOKEN_ROTATED
IAM_SESSIONS_REVOKED
IAM_PASSWORD_CHANGED
IAM_PASSWORD_RESET_REQUESTED
IAM_PASSWORD_RESET_COMPLETED
IAM_ROLE_CREATED
IAM_ROLE_ASSIGNED
IAM_ACCESS_GRANTED
IAM_ACCESS_REVOKED
IAM_DELEGATION_CREATED
IAM_DELEGATION_REJECTED
IAM_AUTHORIZATION_DENIED
```

## 23.2 Workspace events

Status:

```text
Phase 03 owns Workspace events.
Phase 05 verifies consistency.
```

Required:

```text
ORGANIZATION_CREATED
ORGANIZATION_UPDATED
ORGANIZATION_ARCHIVED
ORG_MEMBER_ADDED
ORG_MEMBER_SUSPENDED
ORG_MEMBER_REMOVED
ORG_INVITATION_CREATED
ORG_INVITATION_ACCEPTED
WORKSPACE_CREATED
WORKSPACE_UPDATED
WORKSPACE_ARCHIVED
WORKSPACE_MEMBER_ADDED
WORKSPACE_MEMBER_DEACTIVATED
WORKSPACE_INVITATION_CREATED
WORKSPACE_INVITATION_ACCEPTED
WORKSPACE_INVITATION_REVOKED
WORKSPACE_JOIN_REQUEST_CREATED
WORKSPACE_JOIN_REQUEST_APPROVED
WORKSPACE_JOIN_REQUEST_REJECTED
WORKSPACE_CONTEXT_SWITCHED
ORG_TEAM_CREATED
ORG_TEAM_ASSIGNED_TO_WORKSPACE
```

## 23.3 Project events

Status:

```text
Phase 09/10 owns Project events.
Phase 05 may seed known contracts if project module exists.
```

Required current/foundation:

```text
PROJECT_CREATED
PROJECT_UPDATED
PROJECT_ACTIVATED
PROJECT_ARCHIVED
PROJECT_PHASE_CREATED
PROJECT_PHASE_ACTIVATED
PROJECT_PHASE_COMPLETED
PROJECT_PHASE_ARCHIVED
WBS_NODE_CREATED
WBS_NODE_UPDATED
WBS_NODE_MOVED
WBS_NODE_ARCHIVED
TASK_CREATED
TASK_UPDATED
TASK_ASSIGNED
TASK_STARTED
TASK_BLOCKED
TASK_COMPLETED
TASK_CANCELLED
TASK_ARCHIVED
TASK_DEPENDENCY_CREATED
TASK_DEPENDENCY_REMOVED
```

Future project alerts:

```text
TASK_DUE_DATE_AT_RISK — Phase 13/14
TASK_OVER_CAPACITY — Phase 12/13
PROJECT_SCHEDULE_RECALCULATED — Phase 13/14
```

## 23.4 AI Agent events

Status:

```text
Phase 07 owns AI Agent event contracts.
```

Required:

```text
AI_PROVIDER_CREATED
AI_MODEL_CREATED
AI_DEPLOYMENT_CREATED
AI_AGENT_CREATED
AI_PROMPT_VERSION_ACTIVATED
AI_EVENT_CONFIG_ACTIVATED
AI_EXECUTION_STARTED
AI_EXECUTION_SUCCEEDED
AI_EXECUTION_FAILED
AI_USAGE_POLICY_BLOCKED
```

Future:

```text
AI_PROJECT_PLAN_SUGGESTED — Phase 21
AI_PROJECT_PLAN_APPLIED — Phase 21
AI_ACTION_BLOCKED_BY_IAM — Phase 21
```

## 23.5 Notification events

Status:

```text
Phase 06 owns notification events.
```

Required:

```text
EMAIL_TEMPLATE_CREATED
EMAIL_TEMPLATE_VERSION_PUBLISHED
EMAIL_RULE_CREATED
EMAIL_RULE_ENABLED
EMAIL_RULE_DISABLED
EMAIL_OUTBOX_ENQUEUED
EMAIL_DELIVERY_SENT
EMAIL_DELIVERY_FAILED
EMAIL_DELIVERY_RETRIED
```

## 23.6 Knowledge / Document events

Current knowledge only has DocumentType.

Current:

```text
DOCUMENT_TYPE_CREATED
DOCUMENT_TYPE_UPDATED
DOCUMENT_TYPE_DELETED
```

Future Document Hub:

```text
DOCUMENT_CREATED
DOCUMENT_VERSION_UPLOADED
DOCUMENT_PUBLISHED
DOCUMENT_ARCHIVED
DOCUMENT_RESTORED
DOCUMENT_DOWNLOADED
DOCUMENT_EXPORTED
DOCUMENT_INDEXED_FOR_AI
```

Deferred:

```text
DEFERRED_TO_PHASE_08_DOCUMENT_KNOWLEDGE_HUB
```

## 23.7 Capacity / Scheduling / Gantt future events

Phase 12–14:

```text
RESOURCE_CALENDAR_CREATED
RESOURCE_TIME_OFF_CREATED
RESOURCE_TIME_OFF_APPROVED
PROJECT_ALLOCATION_CREATED
CAPACITY_RECALCULATED
RESOURCE_OVER_ALLOCATED
TASK_SCHEDULE_FORECAST_CREATED
TASK_SCHEDULE_RECALCULATED
GANTT_RECALCULATED
GANTT_TASK_MOVED
GANTT_BASELINE_CREATED
```

## 23.8 Rate / Finance / Quote future events

Phase 15–18:

```text
RATE_CARD_CREATED
RATE_CARD_PUBLISHED
RATE_CARD_ARCHIVED
COST_ROLE_CREATED
CURRENCY_RATE_UPDATED
ESTIMATE_ROLLUP_RECALCULATED
FINANCIAL_SCENARIO_CREATED
FINANCIAL_SCENARIO_RECALCULATED
FINANCIAL_SCENARIO_APPROVED
PROJECT_MARGIN_BELOW_TARGET
QUOTE_CREATED
QUOTE_SUBMITTED
QUOTE_APPROVAL_REQUIRED
QUOTE_APPROVED
QUOTE_REJECTED
QUOTE_PUBLISHED
QUOTE_EXPORTED
```

## 23.9 Baseline / Change Request future events

Phase 19:

```text
PROJECT_BASELINE_CREATED
PROJECT_BASELINE_APPROVED
PROJECT_BASELINE_RESTORED
CHANGE_REQUEST_CREATED
CHANGE_REQUEST_SUBMITTED
CHANGE_REQUEST_APPROVED
CHANGE_REQUEST_REJECTED
CHANGE_REQUEST_APPLIED
```

## 23.10 Reporting future events

Phase 22:

```text
REPORT_EXPORT_REQUESTED
REPORT_EXPORT_STARTED
REPORT_EXPORT_COMPLETED
REPORT_EXPORT_FAILED
DASHBOARD_VIEWED optional
```

---

# 24. Required EventVariable examples

## 24.1 Standard context variables

Most business events should include:

```text
actor.userId UUID optional
actor.type STRING optional
organization.id UUID optional
organization.code STRING optional
workspace.id UUID optional
workspace.code STRING optional
project.id UUID optional
project.code STRING optional
occurredAt DATETIME required
traceId STRING required
```

## 24.2 Task event variables

```text
task.id UUID required
task.code STRING required
task.title STRING required
task.status STRING required
task.assigneeUserId UUID optional
task.dueDate DATE optional
```

## 24.3 Finance event variables

```text
financialScenario.id UUID required
margin.grossMarginPercent PERCENT optional sensitive
margin.profitBeforeTax NUMBER optional sensitive
currency.code STRING required
```

Sensitive:

```text
margin.grossMarginPercent
margin.profitBeforeTax
quote.totalAmount
rateCard.cch
```

## 24.4 Invitation variables

```text
invitation.id UUID required
invitation.acceptUrl URL sensitive
invitation.expiresAt DATETIME required
recipient.email EMAIL sensitive
```

---

# 25. Business rules master

## 25.1 EventDefinition rules

```text
EVDEF-001 code required.
EVDEF-002 code unique.
EVDEF-003 code uppercase snake case.
EVDEF-004 sourceSystem required.
EVDEF-005 eventKey required.
EVDEF-006 sourceSystem + eventKey unique.
EVDEF-007 name required.
EVDEF-008 inputSchemaJson valid JSON if provided.
EVDEF-009 outputSchemaJson valid JSON if provided.
EVDEF-010 initial status ACTIVE.
EVDEF-011 eventVersion starts at 1.
EVDEF-012 code immutable.
EVDEF-013 sourceSystem immutable.
EVDEF-014 eventKey immutable.
EVDEF-015 DEPRECATED cannot be activated.
EVDEF-016 deprecate should record deprecatedAt/deprecatedBy.
EVDEF-017 new consumers cannot bind to INACTIVE/DEPRECATED events.
EVDEF-018 active consumers protect against breaking changes.
```

## 25.2 EventVariable rules

```text
EVVAR-001 event definition must exist.
EVVAR-002 variable path required.
EVVAR-003 variable path unique per event.
EVVAR-004 data type required and valid.
EVVAR-005 required flag defaults false.
EVVAR-006 sensitive flag defaults false.
EVVAR-007 removing required variable used by active consumer is blocked.
EVVAR-008 changing type of used variable is blocked.
EVVAR-009 upsert operation is atomic.
```

## 25.3 Seeder rules

```text
EVSEED-001 seeders idempotent.
EVSEED-002 second run creates no duplicates.
EVSEED-003 seeders do not delete existing definitions.
EVSEED-004 seeders do not remove variables used by active consumers.
EVSEED-005 seeders log drift.
EVSEED-006 seeders are ordered after module prerequisites.
```

## 25.4 Consumer rules

```text
EVCONS-001 EmailRule requires ACTIVE EventDefinition.
EVCONS-002 EventConfig requires ACTIVE EventDefinition.
EVCONS-003 Template publishing validates variables.
EVCONS-004 Deprecated event blocks new consumer binding.
EVCONS-005 EventDefinition deactivate blocked if active consumers unless force/admin allowed.
```

---

# 26. Error catalog requirements

Exact names should follow existing convention, but concepts must exist.

```text
EVENT_DEFINITION_NOT_FOUND
EVENT_DEFINITION_CODE_ALREADY_EXISTS
EVENT_DEFINITION_SOURCE_KEY_ALREADY_EXISTS
EVENT_DEFINITION_INVALID_CODE
EVENT_DEFINITION_INVALID_SCHEMA
EVENT_DEFINITION_ALREADY_DEPRECATED
EVENT_DEFINITION_DEPRECATED_CANNOT_ACTIVATE
EVENT_DEFINITION_HAS_ACTIVE_CONSUMERS
EVENT_DEFINITION_IMMUTABLE_FIELD
EVENT_VARIABLE_INVALID_PATH
EVENT_VARIABLE_DUPLICATE_PATH
EVENT_VARIABLE_INVALID_TYPE
EVENT_VARIABLE_USED_BY_ACTIVE_CONSUMER
EVENT_VARIABLE_REQUIRED_REMOVAL_BLOCKED
EVENT_REGISTRY_ACCESS_DENIED
```

---

# 27. Activity log and audit requirements

## 27.1 Activity actions

```text
EVENT_DEFINITION_CREATED
EVENT_DEFINITION_UPDATED
EVENT_DEFINITION_ACTIVATED
EVENT_DEFINITION_DEACTIVATED
EVENT_DEFINITION_DEPRECATED
EVENT_VARIABLES_UPSERTED
EVENT_SEEDER_RAN
EVENT_SEEDER_DRIFT_DETECTED
```

## 27.2 Audit-sensitive actions

Audit:

```text
EVENT_DEFINITION_DEPRECATED
EVENT_DEFINITION_FORCE_DEACTIVATED
EVENT_VARIABLE_REQUIRED_REMOVED
EVENT_SCHEMA_CHANGED
EVENT_SEEDER_DRIFT_DETECTED
```

Reason:

```text
Changing events can break notifications, AI automations, workflows, and reports.
```

---

# 28. Required tests

Phase 05 is incomplete without tests.

## 28.1 EventDefinition action tests

```text
createEventDefinition_valid_success
createEventDefinition_duplicateCode_conflict
createEventDefinition_duplicateSourceSystemEventKey_conflict
createEventDefinition_invalidCode_rejected
createEventDefinition_invalidInputSchema_rejected
createEventDefinition_invalidOutputSchema_rejected
createEventDefinition_initialStatusActive
createEventDefinition_eventVersionStartsAtOne
updateEventDefinition_valid_success
updateEventDefinition_codeImmutable_rejected
updateEventDefinition_sourceSystemImmutable_rejected
updateEventDefinition_eventKeyImmutable_rejected
updateEventDefinition_invalidSchema_rejected
activateDeprecatedEvent_rejected
deprecateActiveEvent_success
deactivateEventWithActiveConsumers_blockedOrDocumented
```

## 28.2 EventVariable tests

```text
upsertVariables_valid_success
upsertVariables_eventNotFound_rejected
upsertVariables_duplicatePath_rejected
upsertVariables_invalidType_rejected
upsertVariables_invalidPath_rejected
upsertVariables_atomicOnFailure
upsertVariables_removingUsedRequiredVariable_rejected
upsertVariables_changingUsedVariableType_rejected
getVariables_returnsAllVariables
```

## 28.3 Authorization tests

```text
searchEventDefinitions_withoutPermission_forbidden
createEventDefinition_withoutPermission_forbidden
updateEventDefinition_withoutPermission_forbidden
upsertVariables_withoutPermission_forbidden
systemSeeder_bypassesUserAuthSafely
```

## 28.4 Seeder tests

```text
iamEventSeeder_firstRun_createsDefinitions
iamEventSeeder_secondRun_noDuplicates
workspaceEventSeeder_secondRun_noDuplicates
projectEventSeeder_createsKnownProjectEvents
eventSeeder_addsMissingVariable
eventSeeder_doesNotRemoveConsumerUsedVariable
eventSeeder_logsDrift
```

## 28.5 Integration tests with Notification

```text
emailTemplatePublish_unknownVariable_rejected
emailRuleCreate_inactiveEvent_rejected
eventDeactivate_withActiveEmailRule_blocked
```

If Notification integration is deferred to Phase 06:

```text
Mark tests deferred to Phase 06.
```

## 28.6 Integration tests with AI Agent

```text
eventConfigCreate_inactiveEvent_rejected
eventConfigActivate_deprecatedEvent_rejected
executeEvent_unknownEvent_rejected
```

If AI integration is deferred to Phase 07:

```text
Mark tests deferred to Phase 07.
```

## 28.7 Event emission validation tests

If PlatformOutbox validates event code:

```text
emitRegisteredActiveEvent_success
emitUnknownEvent_rejected
emitDeprecatedEvent_rejected
emitEvent_missingRequiredVariable_rejectedOrWarnsAccordingToPolicy
```

If PlatformOutbox is deferred:

```text
Mark deferred to Phase 04/20.
```

---

# 29. Manual verification checklist

Completion file must include:

```text
1. Create EventDefinition.
2. Attempt duplicate code and confirm conflict.
3. Attempt duplicate sourceSystem+eventKey and confirm conflict.
4. Attempt invalid schema and confirm rejection.
5. Upsert variables.
6. Attempt duplicate variable path and confirm rejection.
7. Search event definitions by sourceSystem/status.
8. Deactivate event.
9. Confirm new consumer cannot bind inactive event.
10. Deprecate event.
11. Confirm deprecated event cannot activate.
12. Rerun event seeders and confirm no duplicates.
13. Confirm event variables exist for seeded events.
14. Confirm activity log created for create/update/deprecate.
15. Confirm audit created for deprecate/schema change if implemented.
```

---

# 30. Acceptance criteria

Phase 05 is accepted only if:

```text
1. Current EventRegistry is classified against TO-BE.
2. EventDefinition entity supports required fields or documented gaps.
3. EventVariable entity supports required fields or documented gaps.
4. EventDefinition create/update/lifecycle rules are implemented/tested.
5. EventVariable upsert rules are implemented/tested.
6. Code/sourceSystem/eventKey immutability is enforced.
7. Schema JSON validation is enforced.
8. Deprecated event cannot be activated.
9. New consumers cannot bind inactive/deprecated events or gaps are assigned to Phase 06/07.
10. Event seeders are idempotent.
11. Module event ownership is documented.
12. Event registry IAM permissions are enforced or explicitly deferred.
13. Event changes are activity logged.
14. Audit for sensitive event changes implemented or deferred with reason.
15. Future modules and events are listed with target phases.
16. mvn compile passes.
17. mvn test passes.
18. Completion file exists and includes gap matrix.
```

Agent must not mark complete if:

```text
mvn test fails
duplicate event codes are allowed
invalid JSON schema is accepted
deprecated event can be activated
event variables can duplicate paths
event seeders duplicate rows
active consumers are broken silently
future events are claimed implemented without seed/emitter proof
```

---

# 31. Required phase completion file

Agent must create:

```text
docs/phase-complete/PHASE_05_EVENT_REGISTRY_TO_BE_COMPLETE.md
```

Required sections:

```text
# Phase 05 — Event Registry TO-BE Complete

## 1. Summary
## 2. Source Inputs Reviewed
## 3. Current vs TO-BE Classification Matrix
## 4. Implemented in Current BE
## 5. Implemented / Hardened in This Phase
## 6. Seed-only Items Added
## 7. Deferred Items and Target Phase
## 8. EventDefinition Entity Mapping
## 9. EventVariable Entity Mapping
## 10. API Changes
## 11. Business Rules Implemented
## 12. Business Rules Deferred
## 13. Event Lifecycle Rules
## 14. Consumer Safety Rules
## 15. Event Seeder Matrix by Module
## 16. Event Variable Matrix
## 17. IAM Authorization Matrix
## 18. Activity / Audit Notes
## 19. Tests Added
## 20. Commands Run
## 21. Test Results
## 22. Manual Verification
## 23. Assumptions
## 24. Deviations From Prompt
## 25. Known Risks
## 26. Future Phases That Must Return to Event Registry
```

---

# 32. Future phases that must return to Event Registry

## 32.1 Phase 06 — Notification Engine

Must validate:

```text
EmailTemplate variables against EventVariable.
EmailRule requires ACTIVE EventDefinition.
Deactivation/deprecation safety.
```

## 32.2 Phase 07 — AI Agent Platform

Must validate:

```text
EventConfig requires ACTIVE EventDefinition.
Execute event resolves via EventDefinition.
Prompt variables align with EventVariable.
```

## 32.3 Phase 08 — Knowledge / Document Hub

Must add document events:

```text
DOCUMENT_CREATED
DOCUMENT_PUBLISHED
DOCUMENT_ARCHIVED
DOCUMENT_DOWNLOADED
DOCUMENT_INDEXED_FOR_AI
```

## 32.4 Phase 09/10 — Project

Must add/verify project/WBS/task events.

## 32.5 Phase 12–14 — Capacity/Scheduling/Gantt

Must add capacity and schedule risk events.

## 32.6 Phase 15–18 — Rate/Finance/Quote

Must add financial and commercial events with dataClassification FINANCIAL.

## 32.7 Phase 19 — Baseline/Change Request

Must add baseline/change lifecycle events.

## 32.8 Phase 20 — Project Notifications

Must use EventRegistry as source of truth for notification triggers.

## 32.9 Phase 21 — AI-assisted Planning

Must add AI suggestion/application events.

## 32.10 Phase 22 — Reporting/Export

Must add report export events.

## 32.11 Phase 23 / Compliance

Must harden event versioning, classification, deprecation, and retention.

---

# 33. Agent anti-bịa rules

The agent must not:

```text
1. Treat Event Registry current 2 functions as complete future-state.
2. Claim event emission works if only EventDefinition exists.
3. Claim notification integration works without EmailRule/template validation.
4. Claim AI EventConfig integration works without active event checks.
5. Remove variables used by active templates/configs.
6. Accept invalid JSON schema.
7. Allow duplicate event codes.
8. Allow duplicate sourceSystem+eventKey.
9. Reactivate deprecated event.
10. Seed future events and claim corresponding business modules are implemented.
11. Create email templates in Phase 05 unless requested; only define event variables.
12. Hide deferred consumer-safety gaps.
```

---

# 34. Prompt to give coding agent

```text
You are implementing Phase 05 — TO-BE Event Registry, Event Schema, Variables & Module Event Contract.

This is not an as-is documentation task.

Before coding, read:
- CLAUDE.md / CLAUDE.ms
- Coding_convention.md
- Phase 00 master roadmap
- Phase 01 API/security baseline
- Phase 02 IAM TO-BE spec
- Phase 03 Workspace TO-BE spec
- Phase 04 Platform Audit/Outbox/Idempotency spec
- Current BE feature/entity/business-rule inventory
- Existing eventregistry code, migrations, seeders, tests
- Existing notification EmailTemplate/EmailRule code
- Existing AI Agent EventConfig code
- Existing platform outbox/event publisher code if present

Your task:
1. Compare current EventRegistry against this TO-BE Phase 05 spec.
2. Classify every Event Registry capability as:
   CURRENTLY_IMPLEMENTED,
   MUST_IMPLEMENT_IN_PHASE_05,
   SEED_ONLY_IN_PHASE_05,
   DEFERRED_TO_PHASE_XX,
   or NOT_IN_SCOPE_FOR_BACKEND_NOW.
3. Implement only Phase 05 required items.
4. Do not implement Notification, AI, Workflow, Webhook, Reporting, Finance, or Quote business modules in this phase.
5. Harden EventDefinition and EventVariable rules.
6. Add/verify event lifecycle rules.
7. Add/verify event registry authorization.
8. Define module event seeder standard.
9. Seed or verify known current module events where appropriate.
10. Document future event ownership by phase.
11. Add required tests.
12. Run mvn compile and mvn test.
13. Create docs/phase-complete/PHASE_05_EVENT_REGISTRY_TO_BE_COMPLETE.md with gap matrix.

Do not claim event emission, email notifications, or AI automations work unless code/tests prove them.
Do not hide deferred integration gaps.
```

---

# 35. Quick tracking matrix

| Capability | Current backend | Phase 05 action | Later phase |
|---|---|---|---|
| EventDefinition CRUD | Present | Harden/test | — |
| EventVariable upsert | Present | Harden/test | — |
| Event schema validation | Present/basic | Harden/test | Phase 23 for advanced diff |
| Event versioning | Basic eventVersion | Define contract | Phase 23 for full version entity |
| Data classification | Unknown | Add or defer | Phase 38 |
| Event lifecycle | Active/deprecated present | Harden | — |
| Consumer safety | Likely partial | Add checks or defer | Phase 06/07 |
| Module event seeders | Partial/unknown | Standardize | all modules |
| Notification variable validation | Notification phase | Define contract | Phase 06 |
| AI EventConfig validation | AI phase | Define contract | Phase 07 |
| PlatformOutbox validation | Platform phase | Define contract | Phase 04/20 |
| Workflow triggers | Missing | Defer | Phase 32 |
| Webhooks | Missing | Defer | Integration phase |
| Event analytics | Missing | Defer | Phase 22 |
| Strict event versioning | Missing | Defer | Phase 23/38 |

---

# 36. Final principle

Phase 05 is not complete when "events can be created."

Phase 05 is complete when Scopery has a stable event contract system:

```text
Every event has an owner.
Every event has a stable code.
Every event has variables.
Consumers validate variables.
Deprecated events cannot be reused accidentally.
Seeders are idempotent.
Future modules know exactly which event contracts they must add.
```
