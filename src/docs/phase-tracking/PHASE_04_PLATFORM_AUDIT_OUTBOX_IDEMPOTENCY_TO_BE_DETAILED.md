# PHASE 04 — TO-BE Platform Audit, Activity Log, Event Outbox, Idempotency & Reliability Core

> Project: Scopery Backend  
> Phase: 04  
> Document type: TO-BE implementation-grade specification  
> Status: Planning / Gap-driven implementation spec  
> Depends on: Phase 00 — Master Backend Roadmap, Phase 01 — API Path & Security Baseline, Phase 02 — IAM Core, Phase 03 — Organization / Workspace Core  
> API base: `/api`  
> Primary areas: `platform`, `common`, cross-module integration, audit/activity/outbox/idempotency/error handling  
> Important rule: This file is **not an as-is description**. It defines the TO-BE platform reliability and audit contract that every current and future module must follow.

---

# 0. Purpose of this file

Phase 04 defines the platform backbone used by all business modules.

Scopery is not just a CRUD backend. It is a Work OS where many actions have side effects:

```text
Create organization → create owner member → bootstrap IAM resource → owner grant → event → email
Create workspace → create member → bootstrap IAM → event → email
Create project → activity log → notification → reporting
Approve quote → baseline/finance event → notification → audit
AI generates WBS → execution log → suggestion event → human approval
Change request approved → baseline update → finance impact → notification
```

Without a clear platform contract, coding agents will implement side effects inconsistently.

This phase defines how every module must handle:

```text
1. Activity log
2. Immutable audit
3. Domain/business event recording
4. Transactional outbox
5. Email/outbox pipeline handoff
6. Idempotency keys
7. Trace IDs and correlation IDs
8. Error response consistency
9. Retry behavior
10. Scheduler/job locking
11. Event registry integration
12. Privacy-safe audit payloads
13. Test strategy for side effects
14. Future module return points
```

---

# 1. Difference between current state and TO-BE

The current backend already has signs of platform reliability patterns:

```text
- Activity log is used by modules.
- Notification module has EmailOutbox and EmailDelivery.
- EventRegistry module has EventDefinition and EventVariable.
- Some modules emit outbox/application events.
- IdempotencyKeyFilter exists or is referenced.
- Security/trace/error patterns exist.
```

But current existence does not mean TO-BE completeness.

Phase 04 must verify and harden:

```text
- Are all write actions consistently logged?
- Are important business events captured transactionally?
- Are events defined in EventRegistry before use?
- Are outbox records idempotent and retry-safe?
- Are email templates seeded against event variables?
- Are sensitive fields excluded from audit/outbox?
- Are duplicate POSTs protected by idempotency?
- Are job processors safe under multiple app instances?
- Are error responses consistent?
- Are traceId/correlationId propagated?
```

---

# 2. Classification labels

Every requirement in this file uses one label.

| Label | Meaning |
|---|---|
| `CURRENTLY_IMPLEMENTED` | Current backend already implements it. Agent must verify and test. |
| `MUST_IMPLEMENT_IN_PHASE_04` | Must be implemented or hardened now. |
| `SEED_ONLY_IN_PHASE_04` | Create event/template/metadata seeds now; full feature later. |
| `DEFERRED_TO_PHASE_XX` | Do not implement now; later phase must return to platform. |
| `NOT_IN_SCOPE_FOR_BACKEND_NOW` | Explicitly not in backend scope for this roadmap. |

---

# 3. Phase 04 target statement

Phase 04 must deliver a future-ready platform reliability core.

Phase 04 target:

```text
1. Every state-changing action can produce activity log.
2. Every sensitive/security/commercial action can produce immutable audit.
3. Business events are registered, typed, versioned, and safe.
4. Outbox records are created transactionally with business changes.
5. Outbox processors are retry-safe and idempotent.
6. Email delivery pipeline can consume events safely.
7. POST idempotency prevents duplicate creation/side effects.
8. Trace/correlation IDs are consistent across API, logs, outbox, events.
9. Error responses follow a standard shape.
10. Side effects are testable.
11. Every future module knows how to add events, templates, audit logs, and idempotency rules.
```

---

# 4. In scope

Phase 04 covers:

```text
ActivityLogService / activity logger pattern
Immutable audit event pattern
Business event model
Transactional outbox
Outbox processing state machine
Email outbox handoff contract
Idempotency key support
TraceId / correlationId / requestId
Standard API error response shape
Event registry seed/export standard
Email template seeder standard
Job/scheduler reliability contract
Retry/backoff/dead-letter contract
Privacy/sensitive data redaction
Module integration checklist
Tests for all platform side effects
```

---

# 5. Out of scope

Phase 04 must not implement full business modules:

```text
Event Registry UI
Notification UI
Advanced workflow engine
AI tool execution
Project finance
Quote approval workflow
Document repository
Data retention engine
Analytics warehouse
Distributed message broker migration
Kafka/RabbitMQ unless product explicitly requires
Observability platform deployment
```

Phase 04 may define contracts for these but must not implement them fully.

---

# 6. Current modules affected by Phase 04

Current backend modules that must integrate with Phase 04:

```text
iam
workspace
project
aiagent
eventregistry
notification
knowledge
```

Later modules that must return to Phase 04 contracts:

```text
project template
resource calendar / capacity
task scheduling
gantt
rate card
estimation
finance
quote
baseline
change request
project notifications
AI-assisted planning
reporting/export
document hub
external collaboration
workflow automation
compliance/retention
```

---

# 7. Platform concepts and definitions

## 7.1 Activity log

Activity log answers:

```text
Who did what business action, to which entity, when?
```

Activity log is for product visibility and operational timeline.

Examples:

```text
User created project
User archived workspace
User completed task
User approved quote
AI suggested WBS
```

Activity log may be visible to users depending on permissions.

## 7.2 Immutable audit

Immutable audit answers:

```text
What security/compliance-sensitive change happened, with enough metadata for investigation?
```

Audit is stricter than activity log.

Examples:

```text
Access grant created
Access grant revoked
Login failed
Permission denied
Financial scenario approved
Quote exported
Baseline restored
Document downloaded
AI action blocked by IAM
```

Audit may be admin/compliance-only and should not be editable.

## 7.3 Business event

Business event is a typed fact that something happened.

Examples:

```text
WORKSPACE_CREATED
TASK_ASSIGNED
PROJECT_MARGIN_BELOW_TARGET
QUOTE_APPROVAL_REQUIRED
```

Business events can trigger:

```text
Email
Notification
AI agent execution
Webhook
Report refresh
Audit enrichment
```

## 7.4 Transactional outbox

Transactional outbox stores events/messages in the same database transaction as business changes.

Purpose:

```text
If business change commits, event is not lost.
If transaction rolls back, event is not sent.
Processor can retry safely.
```

## 7.5 Idempotency

Idempotency prevents duplicate side effects for repeated requests.

Example:

```text
Client retries POST /api/projects with same Idempotency-Key.
Only one project is created.
Same response or same resource reference returned.
```

---

# 8. TO-BE platform architecture

## 8.1 Required flow for write action with side effects

For a state-changing business action:

```text
Controller
  → Action.execute(Command)
    → validate business rules
    → persist entity changes
    → write activity log if business-visible
    → write immutable audit if sensitive
    → write domain event / outbox if other systems need to react
  → transaction commits
Outbox processor
  → reads pending messages
  → resolves event definition/templates/rules
  → sends email/notification/webhook/AI trigger
  → records delivery status
```

Do not send emails or call external services directly inside the same DB transaction unless existing platform explicitly supports safe compensation.

## 8.2 Required layering

```text
Application action can call:
- repositories
- activity logger
- audit service
- outbox/event publisher abstraction

Domain model should not call:
- email sender
- HTTP clients
- scheduler
- JPA
- Spring Web
```

## 8.3 Platform package expectation

Actual package names may follow current repo, but TO-BE responsibilities should exist:

```text
platform/activity
platform/audit
platform/event
platform/outbox
platform/idempotency
platform/error
platform/tracing
platform/scheduler
```

If current package differs, document mapping in completion file.

---

# 9. Activity log TO-BE

## 9.1 Required activity log entity

If current entity exists, map to it. If missing, implement.

Suggested table:

```text
platform_activity_log
```

Required fields:

```text
id UUID PK
module_code VARCHAR(100) NOT NULL
entity_type VARCHAR(100) NOT NULL
entity_id UUID NULL
action VARCHAR(150) NOT NULL
actor_user_id UUID NULL
workspace_id UUID NULL
organization_id UUID NULL
project_id UUID NULL
summary VARCHAR(500) NULL
metadata_json JSONB NULL
trace_id VARCHAR(100) NULL
created_at TIMESTAMP NOT NULL
```

Optional:

```text
source_type VARCHAR(50) -- USER, SYSTEM, AI_AGENT, JOB
source_id UUID NULL
```

## 9.2 Activity log business rules

```text
1. Every important user-triggered state change should log an activity.
2. Activity log must not contain raw secrets.
3. Activity log should contain enough context for UI timeline.
4. Failed validation normally does not create activity log.
5. Security-sensitive failures may create audit, not activity.
6. Activity log write failure should follow existing product decision:
   - fail transaction for critical activity
   - or degrade gracefully for non-critical activity
7. Activity log action names must be constants.
8. Activity log metadata schema should be stable.
```

## 9.3 Required current module activity actions

Phase 04 must verify these action names exist or map to existing constants.

### IAM

```text
IAM_USER_CREATED
IAM_USER_UPDATED
IAM_USER_ACTIVATED
IAM_USER_DEACTIVATED
IAM_USER_SUSPENDED
IAM_LOGIN_SUCCESS
IAM_LOGIN_FAILED
IAM_LOGOUT
IAM_PASSWORD_CHANGED
IAM_PASSWORD_RESET_COMPLETED
IAM_ROLE_CREATED
IAM_ROLE_ASSIGNED
IAM_ACCESS_GRANTED
IAM_ACCESS_REVOKED
IAM_DELEGATION_CREATED
IAM_AUTHORIZATION_DENIED
```

### Workspace

```text
ORGANIZATION_CREATED
ORGANIZATION_UPDATED
ORGANIZATION_ARCHIVED
ORG_MEMBER_ADDED
ORG_MEMBER_SUSPENDED
ORG_INVITATION_CREATED
ORG_INVITATION_ACCEPTED
WORKSPACE_CREATED
WORKSPACE_UPDATED
WORKSPACE_ARCHIVED
WORKSPACE_MEMBER_ADDED
WORKSPACE_MEMBER_DEACTIVATED
WORKSPACE_INVITATION_CREATED
WORKSPACE_INVITATION_ACCEPTED
WORKSPACE_JOIN_REQUEST_CREATED
WORKSPACE_JOIN_REQUEST_APPROVED
WORKSPACE_CONTEXT_SWITCHED
ORG_TEAM_CREATED
ORG_TEAM_ASSIGNED_TO_WORKSPACE
```

### Project

```text
PROJECT_CREATED
PROJECT_UPDATED
PROJECT_ACTIVATED
PROJECT_ARCHIVED
PROJECT_PHASE_CREATED
PROJECT_PHASE_ARCHIVED
WBS_NODE_CREATED
WBS_NODE_MOVED
WBS_NODE_ARCHIVED
TASK_CREATED
TASK_UPDATED
TASK_STARTED
TASK_BLOCKED
TASK_COMPLETED
TASK_CANCELLED
TASK_ARCHIVED
TASK_DEPENDENCY_CREATED
TASK_DEPENDENCY_REMOVED
```

### AI Agent

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

### Notification

```text
EMAIL_TEMPLATE_CREATED
EMAIL_TEMPLATE_VERSION_PUBLISHED
EMAIL_RULE_CREATED
EMAIL_RULE_ENABLED
EMAIL_RULE_DISABLED
EMAIL_OUTBOX_RETRIED
EMAIL_DELIVERY_SENT
EMAIL_DELIVERY_FAILED
```

### Event Registry

```text
EVENT_DEFINITION_CREATED
EVENT_DEFINITION_UPDATED
EVENT_VARIABLES_UPSERTED
```

### Knowledge

```text
DOCUMENT_TYPE_CREATED
DOCUMENT_TYPE_UPDATED
DOCUMENT_TYPE_DELETED
```

If current implementation lacks any action, Phase 04 must either add it or document deferred status.

---

# 10. Immutable audit TO-BE

## 10.1 Required audit entity

If current audit table exists, map to it. If missing, implement minimum.

Suggested table:

```text
platform_audit_event
```

Required fields:

```text
id UUID PK
event_type VARCHAR(150) NOT NULL
severity VARCHAR(50) NOT NULL
actor_user_id UUID NULL
actor_type VARCHAR(50) NULL
target_type VARCHAR(100) NULL
target_id UUID NULL
organization_id UUID NULL
workspace_id UUID NULL
project_id UUID NULL
ip_hash VARCHAR(255) NULL
user_agent_hash VARCHAR(255) NULL
decision VARCHAR(50) NULL
reason_code VARCHAR(150) NULL
metadata_json JSONB NULL
trace_id VARCHAR(100) NULL
created_at TIMESTAMP NOT NULL
```

Immutability rule:

```text
No update/delete API for audit event.
Only append.
```

## 10.2 Audit severity

Suggested enum:

```text
INFO
WARNING
SECURITY
COMPLIANCE
CRITICAL
```

## 10.3 Audit-sensitive events

Must audit:

```text
login failed
user suspended
password changed
password reset completed
grant created
grant revoked
delegation rejected
authorization denied
organization archived
workspace archived
org member suspended/removed
workspace member deactivated
financial scenario approved future
quote approved/rejected/exported future
baseline restored future
change request approved future
AI action blocked future
document exported/downloaded future
report exported future
```

## 10.4 Sensitive data redaction

Audit must never store:

```text
raw password
password hash
raw refresh token
raw access token
raw reset token
raw invitation code
authorization header
provider API secret
full payment/financial confidential details unless encrypted/access-controlled
```

Hashing allowed for investigation:

```text
ip_hash
user_agent_hash
email_hash
username_hash
```

---

# 11. Business event TO-BE

## 11.1 EventDefinition dependency

Every emitted business event must have an EventDefinition registered in EventRegistry.

Rule:

```text
Do not emit unknown event codes.
```

If the platform currently allows unknown events, Phase 04 must either:

```text
- add validation before emission
or
- document why unknown-event emission is allowed and add monitoring
```

## 11.2 Required event payload envelope

Every business event/outbox payload should have a standard envelope.

Suggested structure:

```json
{
  "eventCode": "WORKSPACE_CREATED",
  "eventVersion": 1,
  "sourceSystem": "SCOPERY_WORKSPACE",
  "occurredAt": "2026-07-11T00:00:00Z",
  "traceId": "trace-id",
  "actor": {
    "userId": "uuid",
    "type": "USER"
  },
  "context": {
    "organizationId": "uuid",
    "workspaceId": "uuid",
    "projectId": "uuid"
  },
  "data": {}
}
```

## 11.3 Event versioning

Rules:

```text
1. Event code is stable.
2. eventVersion starts at 1.
3. Breaking payload change increments version.
4. EventDefinition stores input schema if supported.
5. Consumers should handle unknown optional fields.
```

## 11.4 Event privacy

Events must be classified:

```text
PUBLIC_INTERNAL
SENSITIVE_INTERNAL
SECURITY
FINANCIAL
PERSONAL_DATA
```

If classification is not implemented, Phase 04 must add it to EventDefinition or document as deferred to compliance phase.

Recommended:

```text
eventDefinition.dataClassification
```

---

# 12. Transactional outbox TO-BE

## 12.1 Required outbox entity

If current outbox exists only in notification module, Phase 04 must decide whether to:

```text
A. Keep notification_email_outbox for email-specific delivery
B. Add generic platform_event_outbox for all business events
C. Use existing outbox as generic with type column
```

Preferred TO-BE:

```text
platform_event_outbox for generic events
notification_email_outbox for rendered/scheduled email delivery
```

## 12.2 platform_event_outbox fields

Suggested table:

```text
platform_event_outbox
```

Fields:

```text
id UUID PK
event_code VARCHAR(150) NOT NULL
event_version INT NOT NULL DEFAULT 1
source_system VARCHAR(100) NOT NULL
aggregate_type VARCHAR(100) NULL
aggregate_id UUID NULL
organization_id UUID NULL
workspace_id UUID NULL
project_id UUID NULL
payload_json JSONB NOT NULL
status VARCHAR(50) NOT NULL
available_at TIMESTAMP NOT NULL
attempt_count INT NOT NULL DEFAULT 0
max_attempts INT NOT NULL DEFAULT 10
last_attempt_at TIMESTAMP NULL
next_retry_at TIMESTAMP NULL
locked_by VARCHAR(100) NULL
locked_until TIMESTAMP NULL
error_code VARCHAR(150) NULL
error_message TEXT NULL
trace_id VARCHAR(100) NULL
created_at TIMESTAMP NOT NULL
updated_at TIMESTAMP NOT NULL
```

Status:

```text
PENDING
PROCESSING
PUBLISHED
FAILED
DEAD_LETTER
CANCELLED
```

## 12.3 Outbox business rules

```text
1. Outbox record created in same DB transaction as business change.
2. Processor only picks PENDING where availableAt <= now.
3. Processor uses lock or atomic update to avoid double processing.
4. PROCESSING messages stuck past lockedUntil become retryable.
5. attemptCount increments on failure.
6. Exponential backoff or fixed retry policy.
7. After maxAttempts → DEAD_LETTER.
8. PUBLISHED messages immutable.
9. Payload must not contain raw secrets.
10. Outbox idempotency key prevents duplicate outbox for same event if business action retried.
```

## 12.4 Outbox consumer types

Potential consumers:

```text
notification email rule engine
in-app notification engine future
webhook engine future
AI event config executor
report refresh job future
audit enrichment future
search indexing future
```

Phase 04 must not implement all consumers, but must design outbox to support them.

---

# 13. Email outbox / notification handoff TO-BE

The current Notification module has EmailTemplate, EmailTemplateVersion, EmailRule, EmailOutbox, EmailDelivery.

Phase 04 must define handoff between business event and email pipeline.

## 13.1 Required flow

```text
Business action
  → platform_event_outbox emits event
Event processor / notification listener
  → match active EmailRule by eventDefinition
  → select active EmailTemplate current version
  → render variables
  → create notification_email_outbox
EmailOutboxProcessor
  → sends email
  → creates/updates EmailDelivery
```

If current notification module bypasses generic platform outbox and listens to application events directly:

```text
Document current behavior.
Decide whether to keep current for now or migrate to generic outbox later.
```

Classification:

```text
MUST_IMPLEMENT_IN_PHASE_04 if current email pipeline can drop events.
DEFERRED_TO_PHASE_20 if current pipeline is acceptable for MVP and project notifications not yet built.
```

## 13.2 Email delivery state machine

Status:

```text
PENDING
PROCESSING
SENT
FAILED
CANCELLED
```

Rules:

```text
1. Only PENDING can become PROCESSING.
2. PROCESSING can become SENT or FAILED.
3. FAILED can retry if retryCount < maxRetry.
4. Retry sets status PENDING and schedule time.
5. SENT cannot be retried.
6. CANCELLED cannot be processed.
7. Provider message ID stored on success.
8. Failure reason truncated safely.
```

## 13.3 Email idempotency

Rules:

```text
1. Same event + rule + recipient should not send duplicate email if processor retries.
2. Unique key recommended:
   eventOutboxId + emailRuleId + recipientEmail
3. Retry should resend only failed delivery, not create duplicate logical delivery unless provider requires new attempt row.
```

---

# 14. Idempotency TO-BE

## 14.1 Purpose

Protect clients and backend from duplicate POSTs.

Examples:

```text
Create organization
Create workspace
Create invitation
Create project
Create task
Create quote
Submit change request
Approve quote
Run AI action
Export report
```

## 14.2 Required idempotency entity

Suggested table:

```text
platform_idempotency_record
```

Fields:

```text
id UUID PK
idempotency_key VARCHAR(255) NOT NULL
request_method VARCHAR(20) NOT NULL
request_path VARCHAR(500) NOT NULL
request_hash VARCHAR(255) NOT NULL
actor_user_id UUID NULL
organization_id UUID NULL
workspace_id UUID NULL
status VARCHAR(50) NOT NULL
response_status INT NULL
response_body_json JSONB NULL
resource_type VARCHAR(100) NULL
resource_id UUID NULL
locked_until TIMESTAMP NULL
created_at TIMESTAMP NOT NULL
updated_at TIMESTAMP NOT NULL
expires_at TIMESTAMP NOT NULL
```

Unique:

```text
actor_user_id + idempotency_key + request_method + request_path
```

If anonymous endpoints use idempotency, actor may be null and another scope key is needed.

Status:

```text
IN_PROGRESS
COMPLETED
FAILED
EXPIRED
```

## 14.3 Idempotency rules

```text
1. Only apply to unsafe methods: POST, PATCH maybe, PUT optional.
2. Apply only when Idempotency-Key header is present unless endpoint requires it.
3. Same key + same request hash returns stored response.
4. Same key + different request hash returns 409 conflict.
5. IN_PROGRESS duplicate returns 409 or waits according to product decision.
6. Completed response can be replayed.
7. Records expire after configured TTL.
8. Do not store sensitive response fields unless redacted/encrypted.
9. Idempotency must cover side effects, not only entity insert.
```

## 14.4 Endpoints where idempotency should be required or strongly recommended

Require or recommend:

```text
POST /api/organizations
POST /api/workspaces
POST /api/workspaces/{id}/invitations
POST /api/projects
POST /api/projects/{id}/tasks
POST /api/iam/grants
POST /api/ai-agent/executions/*
POST /api/notification/email-rules
POST /api/quotes future
POST /api/change-requests future
POST /api/reports/export future
```

Current decision:

```text
Phase 04 should implement optional idempotency by header for all /api/** POST.
Later phases may require it for high-risk endpoints.
```

---

# 15. Trace ID / correlation ID TO-BE

## 15.1 Required behavior

Every request should have:

```text
traceId
```

Sources:

```text
X-Trace-Id header if present and valid
generated UUID if missing
```

Trace ID must appear in:

```text
API error response
logs
activity log
audit event
outbox payload
email delivery metadata
AI execution log
report export job future
```

## 15.2 Correlation ID vs trace ID

Suggested:

```text
traceId = single request path
correlationId = multi-step workflow/event chain
requestId = idempotency/client-supplied request if applicable
```

If only traceId exists now, Phase 04 must document and optionally defer full correlationId.

---

# 16. Error response TO-BE

## 16.1 Standard error response

Expected structure:

```json
{
  "success": false,
  "error": {
    "errorCode": "PROJECT_NOT_FOUND",
    "message": "Project not found",
    "details": {},
    "traceId": "abc",
    "timestamp": "2026-07-11T00:00:00Z"
  }
}
```

## 16.2 Rules

```text
1. Every AppException maps to errorCode.
2. Every error response includes traceId.
3. Validation errors include field errors.
4. Security errors do not leak hidden resource existence.
5. Internal errors do not expose stack trace.
6. Error catalog entries are module-scoped and stable.
7. Error code renames require migration/compatibility note.
```

## 16.3 Error categories

```text
VALIDATION
NOT_FOUND
CONFLICT
UNPROCESSABLE
UNAUTHORIZED
FORBIDDEN
RATE_LIMITED
INTERNAL
EXTERNAL_SERVICE_FAILURE
```

---

# 17. Scheduler / job processing TO-BE

## 17.1 Required scheduler jobs

Current/future jobs:

```text
EmailOutboxProcessor
PlatformEventOutboxProcessor
InvitationExpirationJob
JoinRequestReminderJob future
GrantExpirationJob future
ReportExportProcessor future
AIExecutionRecoveryJob future
```

Phase 04 must implement/gate:

```text
EmailOutboxProcessor if already current
PlatformEventOutboxProcessor if generic outbox is created
```

## 17.2 Job locking

If app can run multiple instances, jobs need locking.

Options:

```text
DB row lock / SKIP LOCKED
locked_by + locked_until
ShedLock
PostgreSQL advisory lock
```

Phase 04 must choose current-compatible method and document.

Rules:

```text
1. Two instances must not process same outbox row concurrently.
2. Crashed lock must expire.
3. Job retry must be safe.
4. Job must be pageable/batched.
5. Job must not load unlimited rows.
```

---

# 18. Event Registry integration TO-BE

Phase 04 must define master event seeding standard.

## 18.1 EventDefinition required fields

If not already present, TO-BE should include:

```text
id
code
sourceSystem
eventKey
name
description
eventVersion
inputSchemaJson
outputSchemaJson optional
status ACTIVE/INACTIVE/DEPRECATED
dataClassification
createdAt
updatedAt
```

If dataClassification is missing:

```text
DEFERRED_TO_PHASE_38_COMPLIANCE unless easy to add now.
```

## 18.2 EventVariable required fields

```text
id
eventDefinitionId
path
name
dataType
required
description
createdAt
```

## 18.3 Seeder standard

Each module should provide event seed definitions:

```text
ModuleEventDefinitionInitializer
```

Rules:

```text
1. Idempotent.
2. Stable event code.
3. Stable sourceSystem.
4. Does not delete existing event definitions.
5. Updates descriptions carefully.
6. Adds missing variables.
7. Does not break published templates.
```

## 18.4 Event source systems

Suggested:

```text
SCOPERY_IAM
SCOPERY_WORKSPACE
SCOPERY_PROJECT
SCOPERY_AI_AGENT
SCOPERY_EVENT_REGISTRY
SCOPERY_NOTIFICATION
SCOPERY_KNOWLEDGE
SCOPERY_FINANCE future
SCOPERY_QUOTE future
SCOPERY_REPORTING future
```

---

# 19. Notification/email seeder standard TO-BE

Every email seeder must:

```text
1. Reference existing EventDefinition.
2. Ensure required EventVariables exist.
3. Create EmailTemplate if missing.
4. Create EmailTemplateVersion if missing or if versioning strategy says new version.
5. Publish version only when variables are valid.
6. Create EmailRule if missing.
7. Be idempotent.
8. Avoid duplicate active rules.
```

Completion file must list:

```text
email templates created
email rules created
event definitions used
variables used
```

---

# 20. Platform security and privacy rules

## 20.1 Never store in activity/audit/outbox

```text
raw password
password hash
raw access token
raw refresh token
raw reset token
raw invitation code
provider API key
authorization header
credit card data
raw personal identity documents
```

## 20.2 Financial privacy future

When Phase 17/18 arrive, Phase 04 platform must support:

```text
financial event classification
restricted audit metadata
export audit
approval audit
```

Do not log:

```text
raw salary
individual compensation
sensitive client pricing beyond authorized finance context
```

## 20.3 AI privacy future

When Phase 21 arrives, platform must support:

```text
AI execution trace
prompt version id
model deployment id
context source ids
redacted prompt payload if sensitive
```

Do not log:

```text
secret provider keys
full private document text in audit metadata
```

---

# 21. Module integration checklist

Every module write action must answer:

```text
1. Does this action need activity log?
2. Does this action need immutable audit?
3. Does this action emit business event?
4. Is the event registered in EventRegistry?
5. Does this action need email/in-app notification?
6. Does this endpoint need idempotency?
7. What is the traceId?
8. What sensitive fields must be redacted?
9. What tests prove side effects?
```

This checklist must be included in future phase docs.

---

# 22. Future phases that must return to Phase 04 platform

## 22.1 Phase 05 — Event Registry

Must align EventDefinition/EventVariable schema with Phase 04 event envelope.

## 22.2 Phase 06 — Notification Engine

Must consume platform events or document why direct application events are used.

## 22.3 Phase 07 — AI Agent Platform

Must log AI executions and usage policy decisions using traceId/correlationId.

## 22.4 Phase 08 — Knowledge / Document Hub

Must audit document publish, archive, download/export, AI indexing.

## 22.5 Phase 09/10 — Project Core / Authorization

Must emit and log:

```text
PROJECT_CREATED
TASK_CREATED
TASK_ASSIGNED
TASK_COMPLETED
WBS_NODE_MOVED
```

## 22.6 Phase 11 — Project Template

Must audit template publish/archive and emit template events.

## 22.7 Phase 12 — Capacity

Must emit capacity recalculation events and audit time-off approval if implemented.

## 22.8 Phase 13 — Scheduling

Must emit schedule forecast/recalculation events.

## 22.9 Phase 14 — Gantt

Must audit manual Gantt move/resize and emit schedule changed events.

## 22.10 Phase 15 — Rate Card

Must audit rate changes and publish events. Rate changes are sensitive.

## 22.11 Phase 17 — Finance

Must audit margin/cost changes, financial scenario approval, export.

## 22.12 Phase 18 — Quote

Must audit quote approval/rejection/export/publish. Requires idempotency on approval/export.

## 22.13 Phase 19 — Baseline / Change Request

Must audit baseline approval/restore and change request approval/application.

## 22.14 Phase 20 — Project Notifications

Must use event registry + notification seeder standard.

## 22.15 Phase 21 — AI-assisted Planning

Must audit AI suggestions and AI-applied actions, including human approver.

## 22.16 Phase 22 — Reporting/Export

Must audit every export request and completion/failure.

## 22.17 Phase 23 — Final Hardening

Must verify platform audit/outbox/idempotency coverage across all modules.

---

# 23. Required platform entities summary

Phase 04 must implement or verify these.

## 23.1 ActivityLog

Status:

```text
CURRENTLY_IMPLEMENTED if exists; otherwise MUST_IMPLEMENT_IN_PHASE_04.
```

Required:

```text
moduleCode
entityType
entityId
action
actorUserId
context ids
metadata
traceId
createdAt
```

## 23.2 AuditEvent

Status:

```text
MUST_IMPLEMENT_IN_PHASE_04 if no immutable audit exists.
```

Required:

```text
eventType
severity
actor
target
context ids
decision/reason
safe metadata
traceId
createdAt
```

## 23.3 PlatformEventOutbox

Status:

```text
MUST_IMPLEMENT_IN_PHASE_04 if current event delivery lacks transactional reliability.
DEFERRED_TO_PHASE_20 only if current application-event notification is intentionally accepted for MVP.
```

## 23.4 IdempotencyRecord

Status:

```text
MUST_IMPLEMENT_IN_PHASE_04 if IdempotencyKeyFilter exists but no persistent record.
CURRENTLY_IMPLEMENTED if persistent idempotency exists and is tested.
```

## 23.5 JobLock / outbox lock fields

Status:

```text
MUST_IMPLEMENT_IN_PHASE_04 if outbox processor can run concurrently.
```

---

# 24. Required APIs for Phase 04

Phase 04 may not need many public APIs. Platform mostly supports internal services.

## 24.1 Activity log read API

Optional if current product has activity timeline:

```text
GET /api/activity-logs
GET /api/activity-logs/entity/{entityType}/{entityId}
```

Authorization:

```text
Caller must have access to target entity.
Admin/compliance can view broader logs.
```

If not implemented:

```text
DEFERRED_TO_PHASE_22_REPORTING or module timeline phase.
```

## 24.2 Audit read API

Usually admin/compliance only:

```text
GET /api/audit-events
GET /api/audit-events/{id}
```

Classification:

```text
DEFERRED_TO_PHASE_38_COMPLIANCE unless needed now.
```

Phase 04 may only implement write-side audit.

## 24.3 Outbox admin API

Optional, admin-only:

```text
GET  /api/platform/outbox
POST /api/platform/outbox/{id}/retry
POST /api/platform/outbox/{id}/cancel
```

Classification:

```text
DEFERRED_TO_PHASE_23 or ops/admin phase unless needed for support.
```

## 24.4 Idempotency API

No public API required.

Cleanup job may exist:

```text
DELETE expired idempotency records
```

---

# 25. Required business rules

## 25.1 Activity rules

```text
ACT-001 State-changing actions log activity where business-visible.
ACT-002 Activity metadata excludes secrets.
ACT-003 Activity action names are constants.
ACT-004 Activity log includes actor and traceId when available.
ACT-005 Activity log can be queried by entity if read API exists.
```

## 25.2 Audit rules

```text
AUD-001 Sensitive actions append immutable audit event.
AUD-002 Audit event cannot be updated/deleted through API.
AUD-003 Audit excludes raw secrets.
AUD-004 Denied authorization checks audited.
AUD-005 Financial/export/security actions future must audit.
```

## 25.3 Event rules

```text
EVT-001 Emitted event code must be registered.
EVT-002 Event payload follows envelope.
EVT-003 Event has version.
EVT-004 Event has traceId.
EVT-005 Event payload excludes secrets.
EVT-006 Event seeders are idempotent.
```

## 25.4 Outbox rules

```text
OUT-001 Outbox created in same transaction as business change.
OUT-002 Processor locks rows safely.
OUT-003 Failed messages retry.
OUT-004 Max attempts leads to DEAD_LETTER.
OUT-005 Published messages are not reprocessed.
OUT-006 Duplicate processor instances do not double-send.
OUT-007 Payload is redacted.
```

## 25.5 Idempotency rules

```text
IDEMP-001 Idempotency-Key applies to /api/** POST when present.
IDEMP-002 Same key + same request returns same response/resource.
IDEMP-003 Same key + different request hash returns conflict.
IDEMP-004 Concurrent same key cannot create duplicate resource.
IDEMP-005 Completed record can replay response.
IDEMP-006 Records expire by TTL.
IDEMP-007 Sensitive response payload redacted.
```

## 25.6 Trace rules

```text
TRACE-001 Every request has traceId.
TRACE-002 Error response includes traceId.
TRACE-003 Activity/audit/outbox include traceId.
TRACE-004 Outbox consumer preserves or extends correlationId.
```

---

# 26. Error catalog requirements

Platform error concepts:

```text
PLATFORM_EVENT_DEFINITION_NOT_FOUND
PLATFORM_OUTBOX_MESSAGE_NOT_FOUND
PLATFORM_OUTBOX_MESSAGE_NOT_RETRYABLE
PLATFORM_OUTBOX_MESSAGE_ALREADY_PUBLISHED
PLATFORM_OUTBOX_MAX_ATTEMPTS_EXCEEDED
PLATFORM_IDEMPOTENCY_CONFLICT
PLATFORM_IDEMPOTENCY_IN_PROGRESS
PLATFORM_IDEMPOTENCY_RECORD_NOT_FOUND
PLATFORM_AUDIT_WRITE_FAILED
PLATFORM_ACTIVITY_WRITE_FAILED
PLATFORM_TRACE_ID_INVALID
PLATFORM_JOB_LOCK_FAILED
```

Use existing error catalog convention.

---

# 27. Required tests

Phase 04 is incomplete without tests.

## 27.1 Activity log tests

```text
activityLog_writeAction_createsActivity
activityLog_containsActorEntityActionTrace
activityLog_doesNotStoreSecrets
activityLog_actionConstants_areStable
activityLog_failureBehavior_matchesPolicy
```

## 27.2 Audit tests

```text
audit_sensitiveAction_createsAuditEvent
audit_deniedAuthorization_createsAuditEvent
audit_doesNotStoreRawTokenOrPassword
audit_eventsAreAppendOnly
audit_containsTraceId
```

## 27.3 Event registry / event seed tests

```text
eventSeeder_firstRun_createsDefinitions
eventSeeder_secondRun_noDuplicates
eventDefinition_requiredVariablesExist
eventDefinition_codeStable
emitUnknownEvent_rejectedOrDocumented
eventPayload_doesNotContainSecrets
```

## 27.4 Platform outbox tests

```text
outbox_createdInSameTransactionAsBusinessChange
outbox_notCreatedWhenTransactionRollsBack
outboxProcessor_publishesPendingMessage
outboxProcessor_failureRetriesWithBackoff
outboxProcessor_maxAttemptsMovesToDeadLetter
outboxProcessor_lockPreventsDoubleProcessing
outboxPayload_containsTraceId
outboxPayload_redactsSecrets
```

## 27.5 Email outbox handoff tests

```text
businessEvent_matchingEmailRule_createsEmailOutbox
emailOutbox_duplicateEventDoesNotDuplicateRecipientDelivery
emailOutbox_retryFailedDelivery_success
emailOutbox_sentDeliveryCannotRetry
emailTemplateVariables_validateAgainstEventDefinition
```

If Phase 04 does not implement email handoff, mark tests deferred to Phase 06/20.

## 27.6 Idempotency tests

```text
idempotency_sameKeySameRequest_returnsSameResult
idempotency_sameKeyDifferentBody_conflict
idempotency_concurrentSameKey_singleResourceCreated
idempotency_withoutKey_requestStillWorks
idempotency_recordExpiresAfterTtl
idempotency_responseDoesNotStoreSecrets
```

## 27.7 Trace/error tests

```text
requestWithoutTraceId_generatesTraceId
requestWithTraceId_usesProvidedTraceId
errorResponse_includesTraceId
activityAuditOutbox_shareTraceId
invalidTraceId_rejectedOrSanitized
```

## 27.8 Scheduler/job tests

```text
jobProcessor_batchesResults
jobProcessor_doesNotLoadUnlimitedRows
jobLock_preventsTwoWorkersSameRow
expiredLock_recovered
```

---

# 28. Seeder requirements

Phase 04 must create/verify seeders for platform-level event definitions.

## 28.1 Platform source system

```text
SCOPERY_PLATFORM
```

## 28.2 Platform events

```text
PLATFORM_OUTBOX_MESSAGE_DEAD_LETTERED
PLATFORM_OUTBOX_MESSAGE_RETRIED
PLATFORM_IDEMPOTENCY_CONFLICT_DETECTED
PLATFORM_JOB_FAILED
PLATFORM_AUDIT_WRITE_FAILED
```

These may be internal-only and not user-facing.

## 28.3 Current module event seeders

Phase 04 must not duplicate every module event manually if module-specific seeders exist. Instead, define seeder contract and verify current modules:

```text
IAM event seeder — Phase 02
Workspace event seeder — Phase 03
Project event seeder — Phase 09/10
AI Agent event seeder — Phase 07
Notification event seeder — Phase 06
Knowledge event seeder — Phase 08
```

If current code lacks module event seeders, Phase 04 must create a master tracking list and mark future phase ownership.

---

# 29. Manual verification checklist

Completion file must include manual verification for:

```text
1. Trigger a write action and see activity log.
2. Trigger sensitive action and see audit event.
3. Trigger business event and see outbox row.
4. Roll back transaction and confirm no outbox row.
5. Run outbox processor and confirm status moves to PUBLISHED/SENT.
6. Force outbox failure and confirm retry count increments.
7. Force max attempts and confirm DEAD_LETTER.
8. Send duplicate POST with same Idempotency-Key and same body; confirm no duplicate resource.
9. Send duplicate POST with same key and different body; confirm conflict.
10. Confirm error response includes traceId.
11. Confirm logs/activity/audit/outbox share traceId.
12. Confirm no raw secrets appear in audit/outbox/activity.
13. Rerun seeders and confirm no duplicates.
```

---

# 30. Acceptance criteria

Phase 04 is accepted only if:

```text
1. Current platform side-effect mechanisms are classified against TO-BE.
2. Activity log pattern is implemented or verified.
3. Immutable audit pattern is implemented or explicitly deferred with reason.
4. Event/outbox strategy is chosen and documented.
5. Transactional outbox exists or clear deferral to Phase 20/06 is documented.
6. Outbox retry/dead-letter behavior implemented or documented.
7. Idempotency persistent behavior implemented or verified.
8. TraceId appears in error responses and side-effect records.
9. Error response standard is verified.
10. Sensitive data redaction is tested.
11. Event registry seeder standard is defined.
12. Email template seeder standard is defined.
13. Tests listed for implemented areas pass.
14. Deferred areas are mapped to target phases.
15. mvn compile passes.
16. mvn test passes.
17. Completion file exists and includes gap matrix.
```

Agent must not mark complete if:

```text
mvn test fails
duplicate POST creates duplicate critical resources despite same Idempotency-Key
outbox can double-send under concurrent processors
audit/activity logs raw secrets
error responses lack traceId
unknown event codes are emitted silently without documentation
seeders duplicate records
```

---

# 31. Required phase completion file

Agent must create:

```text
docs/phase-complete/PHASE_04_PLATFORM_AUDIT_OUTBOX_IDEMPOTENCY_TO_BE_COMPLETE.md
```

Required sections:

```text
# Phase 04 — Platform Audit / Outbox / Idempotency TO-BE Complete

## 1. Summary
## 2. Source Inputs Reviewed
## 3. Current vs TO-BE Classification Matrix
## 4. Implemented in Current BE
## 5. Implemented / Hardened in This Phase
## 6. Deferred Items and Target Phase
## 7. Platform Entity Mapping
## 8. Activity Log Contract
## 9. Immutable Audit Contract
## 10. Event / Outbox Strategy Decision
## 11. Idempotency Strategy
## 12. Trace / Correlation Strategy
## 13. Error Response Strategy
## 14. Scheduler / Job Locking Strategy
## 15. Event Registry Seeder Standard
## 16. Email Template Seeder Standard
## 17. Sensitive Data Redaction Rules
## 18. Tests Added
## 19. Commands Run
## 20. Test Results
## 21. Manual Verification
## 22. Assumptions
## 23. Deviations From Prompt
## 24. Known Risks
## 25. Future Phases That Must Return to Platform
```

---

# 32. Agent anti-bịa rules

The agent must not:

```text
1. Claim transactional outbox exists if only synchronous application events exist.
2. Claim email delivery is reliable without retry/dead-letter behavior.
3. Claim idempotency works if only a filter checks header but no persistent record exists.
4. Store raw passwords/tokens/invitation codes/API keys in activity/audit/outbox.
5. Send external email/API call inside DB transaction without documenting risk.
6. Disable idempotency to make tests pass.
7. Ignore concurrent processor double-send risk.
8. Emit event codes not registered in EventRegistry unless documented.
9. Create email templates without matching event variables.
10. Claim manual verification without doing it.
11. Hide deferred platform gaps.
```

---

# 33. Prompt to give coding agent

```text
You are implementing Phase 04 — TO-BE Platform Audit, Activity Log, Event Outbox, Idempotency & Reliability Core.

This is not an as-is documentation task.

Before coding, read:
- CLAUDE.md / CLAUDE.ms
- Coding_convention.md
- Phase 00 master roadmap
- Phase 01 API/security baseline
- Phase 02 IAM TO-BE spec
- Phase 03 Workspace TO-BE spec
- Current BE feature/entity/business-rule inventory
- Existing platform/common code
- Existing activity log code
- Existing audit code if present
- Existing event registry module
- Existing notification/email outbox module
- Existing idempotency filter/record code
- Existing error handling and traceId code
- Existing scheduler/job processors

Your task:
1. Compare current platform reliability implementation against this Phase 04 TO-BE spec.
2. Classify each capability as CURRENTLY_IMPLEMENTED, MUST_IMPLEMENT_IN_PHASE_04, SEED_ONLY_IN_PHASE_04, DEFERRED_TO_PHASE_XX, or NOT_IN_SCOPE_FOR_BACKEND_NOW.
3. Implement only Phase 04 required items.
4. Do not implement future business modules.
5. Ensure activity log, audit, outbox, idempotency, traceId, and error response patterns are consistent.
6. Define or verify event registry seeder standard.
7. Define or verify email template seeder standard.
8. Add tests for implemented side-effect patterns.
9. Run mvn compile and mvn test.
10. Create docs/phase-complete/PHASE_04_PLATFORM_AUDIT_OUTBOX_IDEMPOTENCY_TO_BE_COMPLETE.md.

Do not claim outbox/idempotency/audit reliability without tests.
Do not store secrets.
Do not hide deferred platform gaps.
```

---

# 34. Quick tracking matrix

| Capability | Current backend | Phase 04 action | Later phase |
|---|---|---|---|
| Activity log | Present in modules | Harden/standardize/test | All future modules |
| Immutable audit | Present/partial unknown | Implement or mark gap | Phase 23/38 |
| EventRegistry | Present | Define seeder/emission contract | Phase 05 |
| Notification email outbox | Present | Align with platform event strategy | Phase 06/20 |
| Generic platform outbox | Unknown/partial | Implement or defer explicitly | Phase 20 if deferred |
| Idempotency filter | Present/mentioned | Add persistent behavior/test | Phase 23 hardening |
| TraceId | Present/unknown | Standardize | All phases |
| Error response | Present | Verify standard | All phases |
| Job locking | Unknown | Implement or document | Phase 23 |
| Dead-letter | Unknown | Implement or document | Phase 23 |
| Event classification | Likely missing | Defer if needed | Phase 38 |
| Audit read API | Missing/unknown | Defer | Phase 38/22 |
| Outbox admin API | Missing | Defer | Phase 23/Ops |
| Export audit | Missing | Defer | Phase 22/38 |
| AI execution trace | AI log exists | Align later | Phase 21 |

---

# 35. Final principle

Phase 04 is not complete just because events or logs exist.

Phase 04 is complete only when Scopery has a reliable platform contract:

```text
Business changes are logged.
Sensitive changes are audited.
Events are registered.
Outbox is transactional or explicitly deferred.
Emails are retry-safe.
Duplicate POSTs do not duplicate side effects.
TraceId follows the request and event chain.
Secrets are never leaked.
Future modules know exactly how to plug into this system.
```
