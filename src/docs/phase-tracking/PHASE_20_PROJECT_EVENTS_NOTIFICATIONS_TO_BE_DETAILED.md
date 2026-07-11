# PHASE 20 — TO-BE Project Events, Project Notification Rules, Watchers, Reminders, Alerts & Delivery Integration

> Project: Scopery Backend  
> Phase: 20  
> Document type: TO-BE implementation-grade specification  
> Status: Planning / Gap-driven implementation spec  
> Depends on: Phase 00 — Master Backend Roadmap, Phase 01 — API Path / Security Baseline, Phase 02 — IAM Core, Phase 03 — Workspace Core, Phase 04 — Platform Audit / Outbox / Idempotency, Phase 05 — Event Registry, Phase 06 — Notification Engine, Phase 07 — AI Agent Platform, Phase 08 — Knowledge / Document Type Catalog, Phase 09 — Project Core / Phase / WBS / Task, Phase 10 — Project Authorization, Phase 11 — Project Template / Phase Catalog, Phase 12 — Resource Calendar / Capacity, Phase 13 — Task Scheduling Engine, Phase 14 — WBS-driven Gantt, Phase 15 — Rate Card / Cost Policy, Phase 16 — Estimation Roll-up, Phase 17 — Project Budget / Margin, Phase 18 — Quote / Commercial Proposal, Phase 19 — Baseline / Change Request  
> API base: `/api`  
> Primary module: `modules/projectnotification` or `modules/project/notification` depending on repository architecture  
> Related modules: `project`, `notification`, `eventregistry`, `platform/outbox`, `iam`, `workspace`, `scheduling`, `finance`, `quote`, `baseline`, future `workflow`, `reporting`, `aiagent`, `external-portal`  
> Important rule: This file is **not an as-is document**. It defines the TO-BE project-specific event and notification integration layer. Phase 20 does not replace the Notification Engine from Phase 06 and does not implement full global subscription center, mobile push, chat integration, digest engine, workflow automation, or external client portal.

---

# 0. Purpose of this file

Phase 20 connects Scopery's project modules to a controlled notification experience.

Previous phases created:

```text
Phase 05:
- EventDefinition
- EventVariable
- EventRegistry contract

Phase 06:
- Notification Engine
- EmailTemplate
- EmailRule
- EmailOutbox
- EmailDelivery
- In-app notification foundation
- Sensitive masking / dedup foundation

Phase 09–19:
- Project / Phase / WBS / Task
- Scheduling / Gantt
- Rate / Estimation / Finance
- Quote
- Baseline / Change Request
```

Phase 20 answers:

```text
Which project events should notify people?
Who should receive project/task/baseline/change/finance/quote alerts?
What rules control notifications?
What templates are needed?
How are reminders generated?
How are due-date risks surfaced?
How are watchers/subscribers managed?
How do we avoid duplicate/spam notifications?
How do we prevent notification payloads from leaking data?
```

Phase 20 is **project notification integration**, not a new notification engine.

---

# 1. Source inputs

Before coding Phase 20, the agent must read:

```text
1. Current backend codebase
2. Phase 05 Event Registry TO-BE spec and implementation
3. Phase 06 Notification Engine TO-BE spec and implementation
4. Phase 09 Project Core TO-BE spec and implementation
5. Phase 10 Project Authorization TO-BE spec and implementation
6. Phase 13 Task Scheduling Engine TO-BE spec and implementation
7. Phase 14 WBS-driven Gantt TO-BE spec and implementation
8. Phase 17 Project Finance TO-BE spec and implementation
9. Phase 18 Quote TO-BE spec and implementation
10. Phase 19 Baseline / Change Request TO-BE spec and implementation
11. Phase 04 Platform Audit / Outbox / Idempotency spec
12. Current EventDefinition seeders
13. Current EmailTemplate / EmailRule seeders
14. Current in-app notification implementation
15. Current BE feature/entity/business-rule inventory
16. Dynamic Work OS feature catalog
```

The agent must not implement Phase 20 from assumptions only.

---

# 2. Current expected backend state

After Phase 19, the backend should have many business events emitted or seedable:

```text
PROJECT_CREATED
PROJECT_UPDATED
TASK_CREATED
TASK_UPDATED
TASK_ASSIGNED
TASK_COMPLETED
TASK_DUE_DATE_CAPACITY_GAP_DETECTED
SCHEDULE_RUN_COMPLETED
GANTT_TASK_MOVED
PROJECT_FINANCE_SCENARIO_APPROVED
QUOTE_SUBMITTED
QUOTE_APPROVED
PROJECT_BASELINE_APPROVED
CHANGE_REQUEST_SUBMITTED
CHANGE_REQUEST_APPROVED
CHANGE_REQUEST_APPLIED
```

Phase 06 likely already has generic notification capability.

Likely missing:

```text
Project-specific notification rule seeders
Project watcher/subscriber model
Task watcher/subscriber model
Project notification recipient resolver
Due date reminder policy
Schedule risk alert policy
Over-allocation alert rule
Baseline/change request alert rule
Finance/quote alert templates
Notification access masking for project data
Project notification preference layer
Project notification dedup policy
Notification delivery integration tests
```

Phase 20 implements the project-specific layer.

---

# 3. Phase 20 target statement

Phase 20 must deliver project event notification integration:

```text
1. Seed and validate project-related EventDefinitions.
2. Seed project notification templates/rules for key project events.
3. Implement project/task watcher or subscriber foundation.
4. Implement recipient resolution for project roles and event participants.
5. Implement project notification preference/policy minimum.
6. Implement due-date reminder and schedule-risk alert generation.
7. Implement baseline/change request/finance/quote notification rules.
8. Ensure notification payloads respect IAM and masking.
9. Ensure notification deduplication/idempotency.
10. Ensure notification events flow through Phase 06 Notification Engine.
11. Add tests for event-to-notification flow.
12. Clearly defer global advanced subscription center, push, chat, digest, workflow automation, and client portal notifications.
```

---

# 4. Key boundary decisions

## 4.1 Phase 20 does not replace Phase 06

Phase 06 owns:

```text
EmailTemplate
EmailTemplateVersion
EmailRule
EmailOutbox
EmailDelivery
NotificationItem / in-app notification foundation
Dedup
Sensitive masking
Template rendering
```

Phase 20 owns:

```text
Which project events use those capabilities.
Which project recipients should receive them.
Which project-specific reminders/alerts are created.
Which project-level preferences apply.
```

## 4.2 Notifications must not grant access

Receiving a notification does not grant permission to open the project.

Rules:

```text
Notification payload must be minimal.
Deep links must require normal authorization when opened.
Details must be masked if recipient lacks permission.
```

## 4.3 Notification is not workflow approval

A notification can alert an approver.

It does not approve the action.

Workflow/multi-step approval is deferred.

## 4.4 Reminders are scheduled checks, not background promises

Backend can implement scheduled jobs.

Spec must define jobs, but coding agent must implement them now if in scope.

No fake "will be implemented later" claims in completion.

---

# 5. Classification labels

Every requirement uses one label:

| Label | Meaning |
|---|---|
| `CURRENTLY_IMPLEMENTED` | Current backend already implements it. Agent must verify and test. |
| `PARTIALLY_IMPLEMENTED` | Current backend implements part of it. |
| `MUST_IMPLEMENT_IN_PHASE_20` | Must be implemented or hardened now. |
| `SEED_ONLY_IN_PHASE_20` | Seed events/templates/rules now; full consumer later. |
| `DEFERRED_TO_PHASE_XX` | Do not implement now; later phase must return. |
| `DEFERRED_TO_POST_23_BACKLOG` | Outside core 00–23 roadmap, part of full Work OS expansion. |
| `NOT_IN_SCOPE_FOR_BACKEND_NOW` | Explicitly not backend scope now. |

---

# 6. Phase 20 scope decision

## 6.1 Must implement now

```text
Project event taxonomy validation/seeding
Project notification rule seeder
Project email/in-app template seeder
ProjectNotificationSubscription / watcher foundation
Task watcher foundation
ProjectNotificationPreference minimum
Recipient resolver for project roles
Recipient resolver for task assignee/inCharge/watchers
Recipient resolver for baseline/change/quote/finance approvers
Due date reminder policy
Schedule risk alert policy
Over-allocation alert rule if Phase 12/13 emits data
Baseline/change request notification rules
Finance/quote notification rules
Access-aware payload masking
Dedup/idempotency integration
Tests
Completion report
```

## 6.2 Optional now

```text
Project notification settings UI APIs
Mute project/task
Reminder snooze
Daily summary email
Escalation rules
Notification digest
Mention notifications
Comment notifications
External client notification
```

Implement only if required.

## 6.3 Must not implement now

```text
Mobile push
Chat/Slack/Teams integration
Global advanced subscription center
Digest engine
Escalation workflow
Full approval workflow
Client portal notifications
External client access model
Comment/mention collaboration suite
AI notification summary
SMS/WhatsApp
Calendar invites
```

---

# 7. Project event taxonomy

Phase 20 must verify or seed project events across modules.

Recommended source systems:

```text
SCOPERY_PROJECT
SCOPERY_SCHEDULING
SCOPERY_GANTT
SCOPERY_ESTIMATION
SCOPERY_PROJECT_FINANCE
SCOPERY_QUOTE
SCOPERY_PROJECT_GOVERNANCE
```

If the backend uses a smaller set of source systems, document mapping.

---

## 7.1 Project core events

```text
PROJECT_CREATED
PROJECT_UPDATED
PROJECT_ARCHIVED
PROJECT_ACTIVATED

PROJECT_PHASE_CREATED
PROJECT_PHASE_UPDATED
PROJECT_PHASE_COMPLETED
PROJECT_PHASE_ARCHIVED

PROJECT_WBS_NODE_CREATED
PROJECT_WBS_NODE_UPDATED
PROJECT_WBS_NODE_MOVED
PROJECT_WBS_NODE_ARCHIVED

PROJECT_TASK_CREATED
PROJECT_TASK_UPDATED
PROJECT_TASK_ASSIGNED
PROJECT_TASK_STATUS_CHANGED
PROJECT_TASK_COMPLETED
PROJECT_TASK_BLOCKED
PROJECT_TASK_CANCELLED
PROJECT_TASK_ARCHIVED

PROJECT_TASK_DEPENDENCY_CREATED
PROJECT_TASK_DEPENDENCY_REMOVED
```

## 7.2 Scheduling/Gantt events

```text
SCHEDULE_RUN_COMPLETED
SCHEDULE_RUN_FAILED
TASK_SCHEDULE_AT_RISK
TASK_SCHEDULE_UNSCHEDULED
TASK_DUE_DATE_CAPACITY_GAP_DETECTED
RESOURCE_SCHEDULE_OVER_CAPACITY_DETECTED

GANTT_RECALCULATED
GANTT_TASK_MOVED
GANTT_TASK_RESIZED
PROJECT_MILESTONE_CREATED
PROJECT_MILESTONE_UPDATED
PROJECT_MILESTONE_ACHIEVED
PROJECT_MILESTONE_ARCHIVED
```

## 7.3 Estimation/finance events

```text
ESTIMATION_RUN_COMPLETED
ESTIMATION_RUN_FAILED
TASK_ESTIMATE_RATE_UNRESOLVED
TASK_ESTIMATE_ROLE_UNRESOLVED

PROJECT_FINANCE_SCENARIO_CREATED
PROJECT_FINANCE_SCENARIO_RECALCULATED
PROJECT_FINANCE_SCENARIO_APPROVED
PROJECT_FINANCE_SCENARIO_MARKED_CURRENT
PROJECT_MARGIN_THRESHOLD_WARNING
```

## 7.4 Quote events

```text
QUOTE_CREATED
QUOTE_VERSION_CREATED
QUOTE_SUBMITTED
QUOTE_APPROVED
QUOTE_REJECTED
QUOTE_SENT
QUOTE_ACCEPTED
QUOTE_MARGIN_THRESHOLD_WARNING
```

## 7.5 Baseline/change events

```text
PROJECT_BASELINE_CREATED
PROJECT_BASELINE_APPROVED
PROJECT_BASELINE_MARKED_CURRENT

CHANGE_REQUEST_CREATED
CHANGE_REQUEST_SUBMITTED
CHANGE_REQUEST_APPROVED
CHANGE_REQUEST_REJECTED
CHANGE_REQUEST_APPLIED
CHANGE_REQUEST_APPLY_FAILED

CHANGE_ORDER_CREATED
CHANGE_ORDER_APPROVED
POST_BASELINE_EDIT_BLOCKED
```

---

# 8. TO-BE capability matrix

---

## 8.1 PNT-001 — Project event definition seeder

| Item | Value |
|---|---|
| Future capability | Ensure project events are available for notifications |
| Current state | Partial via prior phases |
| Phase 20 target | Verify/seed notification-critical events |
| Classification | `MUST_IMPLEMENT_IN_PHASE_20` |

Rules:

```text
1. Every notification rule must reference ACTIVE EventDefinition.
2. Seeder must be idempotent.
3. Seeder must not break existing active consumers.
4. Deprecated events cannot be used by active notification rules.
5. Event variables must include recipient-resolver inputs.
```

Minimum variables:

```text
actor.userId
organization.id
workspace.id
project.id
project.name
task.id
task.title
assignee.userId
dueDate
riskStatus
scheduleRun.id
financeScenario.id
quote.id
quoteVersion.id
baseline.id
changeRequest.id
changeRequest.code
occurredAt
traceId
```

---

## 8.2 PNT-002 — Project notification template seeder

| Item | Value |
|---|---|
| Future capability | Provide templates for project email/in-app notifications |
| Current state | Generic notification engine exists |
| Phase 20 target | Seed project templates |
| Classification | `MUST_IMPLEMENT_IN_PHASE_20` |

Required templates:

```text
PROJECT_TASK_ASSIGNED_EMAIL
PROJECT_TASK_DUE_SOON_EMAIL
PROJECT_TASK_OVERDUE_EMAIL
PROJECT_TASK_AT_RISK_EMAIL
PROJECT_SCHEDULE_RUN_FAILED_EMAIL
PROJECT_BASELINE_APPROVED_EMAIL
CHANGE_REQUEST_SUBMITTED_EMAIL
CHANGE_REQUEST_APPROVED_EMAIL
CHANGE_REQUEST_REJECTED_EMAIL
CHANGE_REQUEST_APPLIED_EMAIL
QUOTE_SUBMITTED_EMAIL
QUOTE_APPROVED_EMAIL
PROJECT_FINANCE_SCENARIO_APPROVED_EMAIL
PROJECT_MARGIN_WARNING_EMAIL
```

In-app templates:

```text
PROJECT_TASK_ASSIGNED_INAPP
PROJECT_TASK_DUE_SOON_INAPP
PROJECT_TASK_OVERDUE_INAPP
PROJECT_TASK_AT_RISK_INAPP
CHANGE_REQUEST_SUBMITTED_INAPP
QUOTE_APPROVED_INAPP
PROJECT_BASELINE_APPROVED_INAPP
```

Rules:

```text
1. Templates use EventVariables only.
2. Templates must avoid leaking finance/quote details unless recipient authorized.
3. Templates must include safe deep link, not token.
4. Templates must be idempotent.
5. Published template versions immutable.
```

---

## 8.3 PNT-003 — Project notification rule seeder

| Item | Value |
|---|---|
| Future capability | Bind project events to notification templates and recipient strategies |
| Current state | Generic EmailRule exists |
| Phase 20 target | Seed project rules |
| Classification | `MUST_IMPLEMENT_IN_PHASE_20` |

Rules:

```text
1. Rule references ACTIVE EventDefinition.
2. Rule references published template version.
3. Rule has recipient strategy.
4. Rule can be disabled by admin.
5. Rule has dedup key strategy.
6. Rule respects mandatory/optional classification.
```

Required rule examples:

```text
PROJECT_TASK_ASSIGNED → task assignee
TASK_DUE_DATE_CAPACITY_GAP_DETECTED → assignee + project manager/watchers
SCHEDULE_RUN_FAILED → project admins / project manager
PROJECT_BASELINE_APPROVED → project watchers / owner
CHANGE_REQUEST_SUBMITTED → approvers / project manager
CHANGE_REQUEST_APPROVED → requester + watchers
QUOTE_SUBMITTED → quote approvers
QUOTE_APPROVED → quote requester / project manager
PROJECT_MARGIN_THRESHOLD_WARNING → finance managers
```

---

## 8.4 PNT-004 — ProjectNotificationSubscription / watcher

| Item | Value |
|---|---|
| Future capability | Users can follow project-level events |
| Current state | Missing/unknown |
| Phase 20 target | Implement foundation |
| Classification | `MUST_IMPLEMENT_IN_PHASE_20` |

Rules:

```text
1. Subscription belongs to project.
2. Subscriber must be active workspace member.
3. Subscriber must have project view access at subscription time.
4. Subscription does not grant future access.
5. Notification delivery re-checks access before exposing details.
6. User can unsubscribe unless subscription is mandatory by role.
7. Duplicate active subscription blocked.
```

Subscription types:

```text
PROJECT_WATCHER
PROJECT_MANAGER
PROJECT_OWNER
FINANCE_WATCHER
QUOTE_WATCHER
CHANGE_WATCHER
BASELINE_WATCHER
```

---

## 8.5 PNT-005 — TaskNotificationSubscription / task watcher

| Item | Value |
|---|---|
| Future capability | Users can follow specific task updates |
| Current state | Missing/unknown |
| Phase 20 target | Implement foundation |
| Classification | `MUST_IMPLEMENT_IN_PHASE_20` |

Rules:

```text
1. Subscription belongs to task and project.
2. Task must belong to project.
3. Subscriber must be active workspace member.
4. Duplicate active task watcher blocked.
5. Assignee/inCharge can be auto-subscribed depending on policy.
6. Completing/archiving task can deactivate optional watchers or keep for history.
```

---

## 8.6 PNT-006 — Recipient resolver

| Item | Value |
|---|---|
| Future capability | Convert event + rule into recipients |
| Current state | Generic recipient strategies may exist |
| Phase 20 target | Implement project-specific resolver |
| Classification | `MUST_IMPLEMENT_IN_PHASE_20` |

Supported strategies:

```text
ACTOR
TASK_ASSIGNEE
TASK_IN_CHARGE
TASK_WATCHERS
PROJECT_WATCHERS
PROJECT_OWNER
PROJECT_MANAGER
PROJECT_ADMINS
WORKSPACE_ADMINS
CHANGE_REQUEST_REQUESTER
CHANGE_REQUEST_APPROVERS
QUOTE_REQUESTER
QUOTE_APPROVERS
FINANCE_MANAGERS
BASELINE_APPROVERS
STATIC_EMAILS optional
EVENT_VARIABLE_USER
EVENT_VARIABLE_EMAIL
```

Rules:

```text
1. Resolver must deduplicate recipients.
2. Resolver must exclude actor if rule says excludeActor.
3. Resolver must exclude inactive users.
4. Resolver must exclude users without workspace access.
5. Resolver must mask payload if recipient lacks detailed permission.
6. Resolver must not notify external clients unless external notification rule explicitly configured.
```

---

## 8.7 PNT-007 — Project notification preference

| Item | Value |
|---|---|
| Future capability | Control which project notifications a user receives |
| Current state | Missing/partial |
| Phase 20 target | Implement minimum |
| Classification | `MUST_IMPLEMENT_IN_PHASE_20` |

Preference levels:

```text
GLOBAL_DEFAULT
WORKSPACE_DEFAULT
PROJECT
TASK
EVENT_TYPE
```

Phase 20 recommended minimum:

```text
PROJECT + EVENT_TYPE
```

Preferences:

```text
emailEnabled
inAppEnabled
muted
mandatoryOverrideAllowed
```

Rules:

```text
1. Mandatory notices ignore mute for critical events.
2. User can mute optional project/task notifications.
3. Admin can set workspace defaults if implemented.
4. Preferences do not bypass IAM.
```

Advanced preference center:

```text
DEFERRED_TO_PHASE_35_ADVANCED_NOTIFICATION_SUBSCRIPTION
```

---

## 8.8 PNT-008 — Due date reminder policy

| Item | Value |
|---|---|
| Future capability | Send reminders for due-soon/overdue tasks |
| Current state | Missing/unknown |
| Phase 20 target | Implement basic scheduled job |
| Classification | `MUST_IMPLEMENT_IN_PHASE_20` |

Reminder types:

```text
TASK_DUE_SOON
TASK_OVERDUE
MILESTONE_DUE_SOON
MILESTONE_MISSED
```

Default policy:

```text
Due soon: 1 day before dueDate
Overdue: first day after dueDate if task not completed/cancelled/archived
```

Rules:

```text
1. Reminder job runs on configured schedule.
2. Job is idempotent.
3. Reminder not sent twice for same task/reminder/date/recipient.
4. Completed/cancelled/archived tasks excluded.
5. Task must belong to active project/workspace.
6. Recipient access re-checked.
7. Reminder emits event or directly invokes Notification Engine according to architecture.
```

Recommended implementation:

```text
Generate PROJECT_TASK_DUE_SOON / PROJECT_TASK_OVERDUE events and let Notification Engine process rules.
```

---

## 8.9 PNT-009 — Schedule risk alert policy

| Item | Value |
|---|---|
| Future capability | Alert users when schedule engine finds risk |
| Current state | Scheduling issues exist from Phase 13 |
| Phase 20 target | Implement rules for key risk events |
| Classification | `MUST_IMPLEMENT_IN_PHASE_20` |

Risk alerts:

```text
TASK_SCHEDULE_AT_RISK
TASK_SCHEDULE_UNSCHEDULED
TASK_DUE_DATE_CAPACITY_GAP_DETECTED
SCHEDULE_RUN_FAILED
RESOURCE_SCHEDULE_OVER_CAPACITY_DETECTED
```

Rules:

```text
1. Alert on new issue, not every recalculation if unchanged.
2. Deduplicate by scheduleRunId + taskId + issueType + recipient.
3. Critical alerts can be mandatory.
4. Payload must avoid exposing private capacity/leave details.
```

---

## 8.10 PNT-010 — Baseline/change notifications

| Item | Value |
|---|---|
| Future capability | Notify stakeholders about baseline/change governance |
| Current state | Baseline/change events exist from Phase 19 |
| Phase 20 target | Implement rules/templates |
| Classification | `MUST_IMPLEMENT_IN_PHASE_20` |

Events:

```text
PROJECT_BASELINE_APPROVED
CHANGE_REQUEST_SUBMITTED
CHANGE_REQUEST_APPROVED
CHANGE_REQUEST_REJECTED
CHANGE_REQUEST_APPLIED
CHANGE_REQUEST_APPLY_FAILED
POST_BASELINE_EDIT_BLOCKED
CHANGE_ORDER_APPROVED
```

Rules:

```text
1. Submitted CR notifies approvers.
2. Approved/rejected CR notifies requester and watchers.
3. Applied CR notifies project watchers.
4. Apply failed notifies requester and admins.
5. Post-baseline edit blocked may notify actor only or project admin depending severity.
```

---

## 8.11 PNT-011 — Finance/quote notifications

| Item | Value |
|---|---|
| Future capability | Notify finance/quote stakeholders |
| Current state | Finance/quote events exist from Phase 17/18 |
| Phase 20 target | Implement rules/templates with permission masking |
| Classification | `MUST_IMPLEMENT_IN_PHASE_20` |

Events:

```text
PROJECT_FINANCE_SCENARIO_APPROVED
PROJECT_MARGIN_THRESHOLD_WARNING
QUOTE_SUBMITTED
QUOTE_APPROVED
QUOTE_REJECTED
QUOTE_SENT
QUOTE_ACCEPTED
QUOTE_MARGIN_THRESHOLD_WARNING
```

Rules:

```text
1. Finance/margin values only included for recipients with finance/margin permission.
2. Quote approvers notified on submitted quote.
3. Quote requester/project manager notified on approval/rejection.
4. Client notification is not sent unless external client rules exist.
5. Quote sent marker does not automatically email client unless explicitly configured.
```

---

## 8.12 PNT-012 — Notification payload masking

| Item | Value |
|---|---|
| Future capability | Prevent sensitive data leak through notifications |
| Current state | Phase 06 sensitive masking foundation |
| Phase 20 target | Implement project-specific masking rules |
| Classification | `MUST_IMPLEMENT_IN_PHASE_20` |

Sensitive fields:

```text
finance planned revenue
direct cost
overhead
gross margin
PBT
quote amount
discount
rate details
client email
private capacity/leave detail
AI prompt/output
```

Rules:

```text
1. Recipient access checked before rendering detailed payload.
2. If recipient lacks permission, use generic text.
3. Deep link can exist but endpoint enforces authorization.
4. Email subject should not include sensitive finance/quote values unless recipient allowed.
5. Delivery logs must not store unmasked sensitive values if not needed.
```

---

## 8.13 PNT-013 — Deduplication / anti-spam

| Item | Value |
|---|---|
| Future capability | Avoid duplicate repeated notifications |
| Current state | Phase 06 dedup foundation |
| Phase 20 target | Define project dedup keys |
| Classification | `MUST_IMPLEMENT_IN_PHASE_20` |

Dedup examples:

```text
TASK_ASSIGNED: eventOutboxId + ruleId + recipientUserId
TASK_DUE_SOON: taskId + dueDate + reminderType + recipientUserId
TASK_OVERDUE: taskId + overdueDate + recipientUserId
SCHEDULE_RISK: scheduleRunId + taskId + issueType + recipientUserId
CHANGE_REQUEST_SUBMITTED: changeRequestId + recipientUserId
QUOTE_SUBMITTED: quoteVersionId + recipientUserId
BASELINE_APPROVED: baselineId + recipientUserId
```

Rules:

```text
1. Dedup keys stable.
2. Retries do not duplicate deliveries.
3. Recalculation with same unchanged risk should not spam.
4. New risk state can notify again.
```

---

## 8.14 PNT-014 — Project activity feed integration

| Item | Value |
|---|---|
| Future capability | Show project activity and notifications together |
| Current state | Phase 04 activity log exists |
| Phase 20 target | Optional/defer |
| Classification | `DEFERRED_TO_PHASE_22_REPORTING_OR_POST_23_COLLABORATION` |

Phase 20 can link NotificationItem to activity log if simple.

Do not block core notifications on activity feed UI.

---

## 8.15 PNT-015 — External/client notifications

| Item | Value |
|---|---|
| Future capability | Notify clients/external collaborators |
| Current state | External portal/client CRM deferred |
| Phase 20 target | Defer |
| Classification | `DEFERRED_TO_POST_23_EXTERNAL_COLLABORATION_PORTAL` |

Rules future:

```text
1. External user must have explicit share/access.
2. External notification payload must be stricter.
3. Client quote sent email must use approved template and audit.
4. Unsubscribe/compliance required.
```

Phase 20 internal notifications only.

---

# 9. Entity model TO-BE

If current schema differs, agent must map actual fields and document gaps.

---

## 9.1 ProjectNotificationSubscription — `project_notification_subscription`

Required fields:

```text
id UUID PK
project_id UUID NOT NULL
workspace_id UUID NOT NULL
subscriber_user_id UUID NOT NULL
workspace_member_id UUID NOT NULL
subscription_type VARCHAR(50) NOT NULL
status VARCHAR(50) NOT NULL
mandatory BOOLEAN NOT NULL DEFAULT false
created_at / created_by
updated_at / updated_by
archived_at / archived_by
version INT
```

Subscription types:

```text
PROJECT_WATCHER
PROJECT_OWNER
PROJECT_MANAGER
FINANCE_WATCHER
QUOTE_WATCHER
CHANGE_WATCHER
BASELINE_WATCHER
```

Status:

```text
ACTIVE
MUTED
ARCHIVED
```

Constraints:

```text
unique project_id + subscriber_user_id + subscription_type where active
```

---

## 9.2 TaskNotificationSubscription — `project_task_notification_subscription`

Required fields:

```text
id UUID PK
project_id UUID NOT NULL
task_id UUID NOT NULL
workspace_id UUID NOT NULL
subscriber_user_id UUID NOT NULL
workspace_member_id UUID NOT NULL
subscription_type VARCHAR(50) NOT NULL
status VARCHAR(50) NOT NULL
mandatory BOOLEAN NOT NULL DEFAULT false
created_at / created_by
updated_at / updated_by
archived_at / archived_by
version INT
```

Subscription types:

```text
TASK_WATCHER
TASK_ASSIGNEE_AUTO
TASK_IN_CHARGE_AUTO
```

Constraint:

```text
unique task_id + subscriber_user_id + subscription_type where active
```

---

## 9.3 ProjectNotificationPreference — `project_notification_preference`

Required fields:

```text
id UUID PK
project_id UUID NULL
task_id UUID NULL
workspace_id UUID NOT NULL
user_id UUID NOT NULL
event_code VARCHAR(150) NULL
channel VARCHAR(50) NOT NULL
enabled BOOLEAN NOT NULL DEFAULT true
muted BOOLEAN NOT NULL DEFAULT false
mandatory_override BOOLEAN NOT NULL DEFAULT false
created_at / created_by
updated_at / updated_by
version INT
```

Channel:

```text
IN_APP
EMAIL
```

Future:

```text
PUSH
CHAT
DIGEST
SMS
```

Rules:

```text
project_id or task_id optional depending scope
event_code null means all project events for channel
```

---

## 9.4 ProjectNotificationRuleBinding — optional

If Phase 06 EmailRule/NotificationRule already stores enough metadata, no new table is needed.

Optional table:

```text
project_notification_rule_binding
```

Fields:

```text
id UUID PK
event_definition_id UUID NOT NULL
rule_code VARCHAR(150) NOT NULL
recipient_strategy VARCHAR(100) NOT NULL
channel VARCHAR(50) NOT NULL
template_code VARCHAR(150) NOT NULL
mandatory BOOLEAN NOT NULL DEFAULT false
enabled BOOLEAN NOT NULL DEFAULT true
dedup_strategy VARCHAR(150) NOT NULL
created_at / updated_at
```

Recommended:

```text
Use Phase 06 EmailRule/NotificationRule if available.
Do not duplicate rule engine unless needed.
```

---

## 9.5 ProjectReminderRun — `project_reminder_run`

Required if due date reminder job implemented.

Fields:

```text
id UUID PK
workspace_id UUID NULL
run_type VARCHAR(50) NOT NULL
status VARCHAR(50) NOT NULL
started_at TIMESTAMP NOT NULL
completed_at TIMESTAMP NULL
result_summary_json JSONB NULL
error_code VARCHAR(150) NULL
error_message TEXT NULL
created_at TIMESTAMP NOT NULL
trace_id VARCHAR(100) NULL
```

Run types:

```text
TASK_DUE_SOON
TASK_OVERDUE
MILESTONE_DUE_SOON
MILESTONE_MISSED
```

Status:

```text
RUNNING
COMPLETED
FAILED
```

---

## 9.6 ProjectReminderEmission — `project_reminder_emission`

Used to deduplicate scheduled reminders.

Fields:

```text
id UUID PK
reminder_run_id UUID NULL
project_id UUID NOT NULL
task_id UUID NULL
milestone_id UUID NULL
recipient_user_id UUID NOT NULL
reminder_type VARCHAR(50) NOT NULL
reminder_date DATE NOT NULL
dedup_key VARCHAR(255) NOT NULL
status VARCHAR(50) NOT NULL
created_at TIMESTAMP NOT NULL
```

Constraint:

```text
unique dedup_key
```

Status:

```text
EMITTED
SKIPPED
FAILED
```

---

# 10. API TO-BE list

All APIs use `/api`.

---

## 10.1 Project watcher APIs

```text
POST  /api/projects/{projectId}/notification-subscriptions
GET   /api/projects/{projectId}/notification-subscriptions
GET   /api/projects/{projectId}/notification-subscriptions/me
PATCH /api/projects/{projectId}/notification-subscriptions/{subscriptionId}/mute
PATCH /api/projects/{projectId}/notification-subscriptions/{subscriptionId}/unmute
DELETE /api/projects/{projectId}/notification-subscriptions/{subscriptionId}
```

Create request:

```json
{
  "subscriberUserId": "uuid-or-null-for-self",
  "subscriptionType": "PROJECT_WATCHER"
}
```

Rules:

```text
1. User can subscribe self if has project view.
2. Adding another user requires manage subscription permission.
3. Mandatory subscription cannot be deleted by normal user.
```

---

## 10.2 Task watcher APIs

```text
POST  /api/projects/{projectId}/tasks/{taskId}/notification-subscriptions
GET   /api/projects/{projectId}/tasks/{taskId}/notification-subscriptions
GET   /api/projects/{projectId}/tasks/{taskId}/notification-subscriptions/me
PATCH /api/projects/{projectId}/tasks/{taskId}/notification-subscriptions/{subscriptionId}/mute
PATCH /api/projects/{projectId}/tasks/{taskId}/notification-subscriptions/{subscriptionId}/unmute
DELETE /api/projects/{projectId}/tasks/{taskId}/notification-subscriptions/{subscriptionId}
```

---

## 10.3 Notification preference APIs

```text
GET /api/projects/{projectId}/notification-preferences/me
PUT /api/projects/{projectId}/notification-preferences/me

GET /api/projects/{projectId}/tasks/{taskId}/notification-preferences/me
PUT /api/projects/{projectId}/tasks/{taskId}/notification-preferences/me
```

Preference request:

```json
{
  "preferences": [
    {
      "eventCode": "PROJECT_TASK_ASSIGNED",
      "channel": "EMAIL",
      "enabled": true,
      "muted": false
    },
    {
      "eventCode": "TASK_DUE_DATE_CAPACITY_GAP_DETECTED",
      "channel": "IN_APP",
      "enabled": true,
      "muted": false
    }
  ]
}
```

---

## 10.4 Project notification admin APIs

Optional:

```text
GET /api/projects/{projectId}/notification-rules
PATCH /api/projects/{projectId}/notification-rules/{ruleCode}/enable
PATCH /api/projects/{projectId}/notification-rules/{ruleCode}/disable
```

Recommended:

```text
Use Phase 06 notification rule admin APIs if already present.
```

---

## 10.5 Reminder job/admin APIs

Internal/admin:

```text
POST /api/projects/notifications/reminders/run
GET  /api/projects/notifications/reminders/runs
GET  /api/projects/notifications/reminders/runs/{runId}
```

Rules:

```text
Manual run requires admin/ops permission.
Scheduled job can call internal service.
```

---

# 11. Authorization requirements

Required IAM authorities:

```text
PROJECT_NOTIFICATION_VIEW
PROJECT_NOTIFICATION_SUBSCRIBE_SELF
PROJECT_NOTIFICATION_MANAGE_SUBSCRIBERS
PROJECT_NOTIFICATION_PREFERENCE_VIEW
PROJECT_NOTIFICATION_PREFERENCE_UPDATE

TASK_NOTIFICATION_VIEW
TASK_NOTIFICATION_SUBSCRIBE_SELF
TASK_NOTIFICATION_MANAGE_SUBSCRIBERS
TASK_NOTIFICATION_PREFERENCE_VIEW
TASK_NOTIFICATION_PREFERENCE_UPDATE

PROJECT_NOTIFICATION_RULE_VIEW
PROJECT_NOTIFICATION_RULE_MANAGE

PROJECT_REMINDER_RUN
PROJECT_NOTIFICATION_DELIVERY_VIEW
```

Rules:

```text
1. User must have project view to subscribe/watch project.
2. User must have task view to watch task.
3. Managing other subscribers requires stronger permission.
4. Viewing subscriber list requires permission.
5. Notification delivery must re-check resource access.
6. Finance/quote details require finance/quote permission.
7. External users are not in scope unless external portal exists.
```

Simplified mapping allowed:

```text
PROJECT_NOTIFICATION_SUBSCRIBE_SELF → PROJECT_VIEW
TASK_NOTIFICATION_SUBSCRIBE_SELF → PROJECT_TASK_VIEW
```

Completion file must document.

---

# 12. Recipient resolution rules

## 12.1 General resolution

```text
1. Receive EventOutbox or EventDefinition + payload.
2. Find active notification rules for event.
3. For each rule, resolve recipient candidates.
4. Remove duplicate users/emails.
5. Remove inactive users/members.
6. Re-check access and permissions.
7. Apply preferences/mute.
8. Apply mandatory override if critical.
9. Render masked/unmasked template.
10. Enqueue email/in-app through Phase 06.
```

## 12.2 Exclude actor rule

Many notifications should exclude the actor.

Examples:

```text
If user updates own task, do not notify them unless mandatory/risk.
```

Rule must be configurable.

## 12.3 Role-based recipients

Project owner/manager requires source.

Possible sources:

```text
Project.ownerUserId if exists
Project manager field if exists
ProjectNotificationSubscription type PROJECT_MANAGER
Workspace admin fallback
```

If project owner field does not exist:

```text
Use subscription type or workspace admin as documented fallback.
```

## 12.4 Approver recipients

For quote/baseline/change approval:

```text
Use users with relevant approval IAM permission in workspace/project.
```

If listing users by permission is expensive/missing:

```text
Use configured subscription type:
- QUOTE_WATCHER
- CHANGE_WATCHER
- BASELINE_WATCHER
- FINANCE_WATCHER
```

Completion file must document strategy.

---

# 13. Due date reminder job

## 13.1 Due soon job

Default:

```text
Run daily.
Find tasks:
- dueDate = today + N days
- status not COMPLETED/CANCELLED/ARCHIVED
- project active
- workspace active
```

N default:

```text
1
```

Recipients:

```text
task assignee
task inCharge
task watchers
project watchers optional
```

Event emitted:

```text
PROJECT_TASK_DUE_SOON
```

## 13.2 Overdue job

Default:

```text
Run daily.
Find tasks:
- dueDate < today
- status not COMPLETED/CANCELLED/ARCHIVED
- not already emitted for this overdue date/recipient
```

Event emitted:

```text
PROJECT_TASK_OVERDUE
```

## 13.3 Dedup

Dedup key:

```text
taskId + reminderType + reminderDate + recipientUserId
```

## 13.4 Job safety

Rules:

```text
1. Job must be idempotent.
2. Job must have reasonable batch limit.
3. Job should use lock to avoid concurrent duplicate runs.
4. Job records ProjectReminderRun.
5. Failed run recorded.
```

---

# 14. Schedule risk alert flow

From Phase 13:

```text
SchedulingIssue
TaskSchedule.riskStatus
ScheduleRun completed/failed
```

Flow:

```text
1. ScheduleRun completes.
2. For new critical issues, emit project risk event.
3. Notification rule resolves recipients.
4. Dedup by scheduleRunId/taskId/issueType/recipient.
5. Render template with masking.
```

Risk severity mapping:

```text
TASK_NO_CAPACITY → WARNING/ERROR
TASK_DUE_DATE_CAPACITY_GAP → WARNING
TASK_DEPENDENCY_CYCLE → ERROR/BLOCKER
SCHEDULE_RUN_FAILED → ERROR
```

---

# 15. Finance/quote masking strategy

## 15.1 Finance event masking

If recipient lacks finance permission:

Unmasked example:

```text
Project margin dropped below threshold: 18.5%.
```

Masked example:

```text
A finance warning was detected for project ABC. Open the project finance page if you have access.
```

## 15.2 Quote event masking

If recipient lacks quote/margin permission:

Unmasked:

```text
Quote Q-001 was approved. Total: 1,000,000,000 VND.
```

Masked:

```text
A quote was approved for project ABC.
```

Rules:

```text
1. Do not include quote amount in email subject unless allowed.
2. Do not include margin/discount in subject unless allowed.
3. Deep link authorization remains enforced.
```

---

# 16. Integration with Phase 06 Notification Engine

Phase 20 should reuse:

```text
EmailTemplate
EmailTemplateVersion
EmailRule
EmailOutbox
EmailDelivery
NotificationItem
Dedup
Sensitive masking
```

Do not implement duplicate email outbox.

If Phase 06 lacks in-app notification:

```text
Phase 20 can seed email only and document in-app gap.
```

But if Phase 06 promised in-app foundation, verify it.

---

# 17. Integration with Phase 05 Event Registry

Every rule must validate:

```text
EventDefinition ACTIVE
Variables exist
Sensitive variables marked
Deprecated events not used
```

If event missing:

```text
Seed it idempotently or block rule activation.
```

---

# 18. Integration with Baseline / Change Request

Phase 20 must add notification rules for:

```text
Baseline approved
Change request submitted
Change request approved/rejected
Change request applied
Change request apply failed
Change order approved
```

Recipient examples:

```text
Submitted → change approvers
Approved/rejected → requester + project watchers
Applied → project watchers + requester
Apply failed → requester + workspace/project admins
```

---

# 19. Integration with AI Agent

Phase 20 does not implement AI summaries.

Future Phase 21 can:

```text
summarize notification stream
explain schedule risk
draft message to stakeholders
classify high-priority notifications
```

Rules:

```text
AI cannot create notification rules unless authorized.
AI cannot read notifications beyond effective access.
AI cannot send external notifications without human approval.
```

---

# 20. Integration with Reporting / Digest

Digest and reporting are deferred.

Future Phase 22/35 can add:

```text
daily project digest
weekly risk digest
notification analytics
unread notification dashboard
delivery failure dashboard
```

Phase 20 focuses on transactional alerts.

---

# 21. Business rules master

## 21.1 Event/rule rules

```text
PNT-EVT-001 Notification rule must reference ACTIVE EventDefinition.
PNT-EVT-002 Deprecated event cannot be used by active rule.
PNT-EVT-003 Template variables must exist in EventRegistry.
PNT-EVT-004 Rule seeder idempotent.
PNT-EVT-005 Missing event definition blocks rule activation.
```

## 21.2 Subscription rules

```text
PNT-SUB-001 Subscriber must be active workspace member.
PNT-SUB-002 Subscriber must have project/task access at subscription time.
PNT-SUB-003 Subscription does not grant access.
PNT-SUB-004 Duplicate active subscription blocked.
PNT-SUB-005 Mandatory subscription cannot be deleted by normal user.
PNT-SUB-006 User can mute optional subscription.
```

## 21.3 Recipient rules

```text
PNT-REC-001 Resolver deduplicates recipients.
PNT-REC-002 Inactive users excluded.
PNT-REC-003 Removed workspace members excluded.
PNT-REC-004 Actor excluded if rule configured.
PNT-REC-005 Access re-checked before delivery.
PNT-REC-006 Finance/quote details masked if recipient lacks permission.
```

## 21.4 Preference rules

```text
PNT-PREF-001 User preferences apply to optional notifications.
PNT-PREF-002 Mandatory notices can bypass mute.
PNT-PREF-003 Preferences do not bypass IAM.
PNT-PREF-004 Email and in-app can be controlled separately.
```

## 21.5 Reminder rules

```text
PNT-REM-001 Reminder job idempotent.
PNT-REM-002 Completed/cancelled/archived tasks excluded.
PNT-REM-003 Inactive project/workspace excluded.
PNT-REM-004 Reminder deduped by task/reminder/date/recipient.
PNT-REM-005 Job records run status.
```

## 21.6 Dedup/masking rules

```text
PNT-DEDUP-001 Dedup key required for project notification rule.
PNT-DEDUP-002 Retry must not duplicate delivery.
PNT-MASK-001 Sensitive fields masked for unauthorized recipients.
PNT-MASK-002 Email subject must not leak sensitive values.
PNT-MASK-003 Deep links require normal authorization.
```

---

# 22. Error catalog requirements

Exact names follow project convention, but these concepts must exist.

```text
PROJECT_NOTIFICATION_SUBSCRIPTION_NOT_FOUND
PROJECT_NOTIFICATION_SUBSCRIPTION_DUPLICATE
PROJECT_NOTIFICATION_SUBSCRIBER_NOT_MEMBER
PROJECT_NOTIFICATION_SUBSCRIBER_INACTIVE
PROJECT_NOTIFICATION_SUBSCRIBER_NO_ACCESS
PROJECT_NOTIFICATION_SUBSCRIPTION_MANDATORY
PROJECT_NOTIFICATION_SUBSCRIPTION_ACCESS_DENIED

TASK_NOTIFICATION_SUBSCRIPTION_NOT_FOUND
TASK_NOTIFICATION_SUBSCRIPTION_DUPLICATE
TASK_NOTIFICATION_TASK_MISMATCH
TASK_NOTIFICATION_SUBSCRIBER_NO_ACCESS

PROJECT_NOTIFICATION_PREFERENCE_NOT_FOUND
PROJECT_NOTIFICATION_PREFERENCE_INVALID_CHANNEL
PROJECT_NOTIFICATION_PREFERENCE_INVALID_EVENT
PROJECT_NOTIFICATION_PREFERENCE_ACCESS_DENIED

PROJECT_NOTIFICATION_RULE_EVENT_NOT_ACTIVE
PROJECT_NOTIFICATION_RULE_TEMPLATE_NOT_PUBLISHED
PROJECT_NOTIFICATION_RULE_RECIPIENT_STRATEGY_INVALID
PROJECT_NOTIFICATION_RULE_DEDUP_STRATEGY_MISSING

PROJECT_NOTIFICATION_RECIPIENT_RESOLUTION_FAILED
PROJECT_NOTIFICATION_PAYLOAD_MASKING_FAILED
PROJECT_NOTIFICATION_DEDUPLICATED

PROJECT_REMINDER_RUN_NOT_FOUND
PROJECT_REMINDER_RUN_ALREADY_RUNNING
PROJECT_REMINDER_RUN_FAILED
PROJECT_REMINDER_INVALID_POLICY

PROJECT_NOTIFICATION_ACCESS_DENIED
```

---

# 23. Required tests

Phase 20 is incomplete without tests.

---

## 23.1 Event/template/rule seeder tests

```text
projectEventSeeder_firstRun_createsDefinitions
projectEventSeeder_secondRun_noDuplicates
projectTemplateSeeder_firstRun_createsTemplates
projectTemplateSeeder_secondRun_noDuplicates
projectRuleSeeder_firstRun_createsRules
projectRuleSeeder_secondRun_noDuplicates
projectRuleReferencesOnlyActiveEvents
projectRuleRejectsDeprecatedEvent
projectTemplateRejectsUnknownVariables
```

## 23.2 Project subscription tests

```text
subscribeProject_selfWithAccess_success
subscribeProject_selfWithoutAccess_forbidden
subscribeProject_inactiveMember_rejected
subscribeProject_duplicate_rejected
muteProjectSubscription_success
unmuteProjectSubscription_success
deleteMandatorySubscription_rejected
manageOtherSubscriber_withoutPermission_forbidden
```

## 23.3 Task subscription tests

```text
subscribeTask_selfWithTaskView_success
subscribeTask_taskOtherProject_rejected
subscribeTask_withoutTaskAccess_forbidden
subscribeTask_duplicate_rejected
autoSubscribeAssignee_onTaskAssigned_success_ifImplemented
archiveTaskSubscription_success
```

## 23.4 Recipient resolver tests

```text
resolveTaskAssignee_success
resolveTaskWatchers_success
resolveProjectWatchers_success
resolveChangeRequestApprovers_success
resolveQuoteApprovers_success
resolverDeduplicatesRecipients
resolverExcludesInactiveMembers
resolverExcludesActorWhenConfigured
resolverRechecksAccess
resolverMasksFinanceForUnauthorizedRecipient
resolverMasksQuoteAmountForUnauthorizedRecipient
```

## 23.5 Notification flow tests

```text
taskAssignedEvent_enqueuesNotificationToAssignee
taskDueSoonReminder_enqueuesNotification
taskOverdueReminder_enqueuesNotification
scheduleRiskEvent_enqueuesNotification
changeRequestSubmitted_enqueuesApproverNotification
quoteSubmitted_enqueuesApproverNotification
baselineApproved_enqueuesWatcherNotification
financeMarginWarning_masksPayloadWithoutPermission
```

## 23.6 Reminder job tests

```text
dueSoonJob_findsDueTomorrowTask
dueSoonJob_skipsCompletedTask
dueSoonJob_skipsArchivedProject
dueSoonJob_idempotentNoDuplicateEmission
overdueJob_findsOverdueTask
overdueJob_skipsCancelledTask
reminderRun_recordsCompleted
reminderRun_recordsFailed
```

## 23.7 Dedup/preference tests

```text
dedup_sameEventSameRecipient_noDuplicate
dedup_taskDueSoonSameDate_noDuplicate
dedup_newRiskState_allowsNewNotification
preferenceMuted_skipsOptionalNotification
mandatoryNotification_ignoresMute
emailDisabled_inAppEnabled_onlyInApp
inAppDisabled_emailEnabled_onlyEmail
```

## 23.8 Authorization tests

```text
viewProjectSubscriptions_withoutPermission_forbidden
manageProjectSubscriptions_withoutPermission_forbidden
updatePreferences_withoutProjectAccess_forbidden
runReminderJob_withoutAdmin_forbidden
crossWorkspaceNotificationAccess_forbidden
notificationDeepLink_doesNotBypassAuthorization
```

## 23.9 Delivery integration tests

```text
notificationUsesPhase06EmailOutbox
notificationUsesPhase06InAppIfAvailable
emailOutboxDedupKeySet
notificationItemDedupKeySet
deliveryPayloadDoesNotStoreUnmaskedSensitiveFieldsWhenUnauthorized
```

---

# 24. Manual verification checklist

Completion file must include:

```text
1. Seed project events/templates/rules.
2. Rerun seeders and confirm no duplicates.
3. Subscribe user as project watcher.
4. Subscribe user as task watcher.
5. Assign task and confirm assignee notification queued.
6. Run due soon reminder job and confirm notification queued once.
7. Rerun due soon job and confirm no duplicate.
8. Create schedule risk and confirm watcher notification.
9. Submit ChangeRequest and confirm approver notification.
10. Approve Quote and confirm requester/project manager notification.
11. Trigger finance margin warning and confirm masking for unauthorized recipient.
12. Mute optional notification and confirm skipped.
13. Confirm mandatory notification still delivered.
14. Confirm notification deep link still requires authorization.
15. Confirm no push/chat/digest/client portal notification is falsely created.
```

---

# 25. Acceptance criteria

Phase 20 is accepted only if:

```text
1. Current project notification capability is classified against TO-BE.
2. Project event/template/rule seeders implemented/tested.
3. Project watcher/subscription implemented/tested.
4. Task watcher/subscription implemented/tested.
5. Recipient resolver implemented/tested.
6. Project notification preference minimum implemented/tested.
7. Due date reminder job implemented/tested.
8. Schedule risk alert rules implemented/tested.
9. Baseline/change/finance/quote notification rules implemented/tested.
10. Payload masking and permission checks implemented/tested.
11. Dedup/idempotency implemented/tested.
12. Integration with Phase 06 Notification Engine verified/tested.
13. Activity/audit/outbox follows Phase 04.
14. No push/chat/digest/workflow/client portal notifications are falsely claimed.
15. mvn compile passes.
16. mvn test passes.
17. Completion file exists and includes gap matrix.
```

Agent must not mark complete if:

```text
mvn test fails
notification rule uses inactive/deprecated event
notification template uses unknown variables
recipient resolver sends to inactive/removed user
notification payload leaks finance/quote data
muted optional notifications still deliver
mandatory notices can be muted when they should not
reminder job duplicates notifications
notification bypasses authorization
Phase 06 engine is bypassed with duplicate email sender
advanced push/chat/digest/client portal is claimed implemented
```

---

# 26. Required phase completion file

Agent must create:

```text
docs/phase-complete/PHASE_20_PROJECT_EVENTS_NOTIFICATIONS_TO_BE_COMPLETE.md
```

Required sections:

```text
# Phase 20 — Project Events / Notifications TO-BE Complete

## 1. Summary
## 2. Source Inputs Reviewed
## 3. Current vs TO-BE Classification Matrix
## 4. Implemented in Current BE
## 5. Implemented / Hardened in This Phase
## 6. Seed-only Items Added
## 7. Deferred Items and Target Phase
## 8. Notification Boundary Decision
## 9. Event Definition Seeder Matrix
## 10. Template Seeder Matrix
## 11. Notification Rule Seeder Matrix
## 12. Project Subscription Entity Mapping
## 13. Task Subscription Entity Mapping
## 14. Preference Strategy
## 15. Recipient Resolver Strategy
## 16. Due Date Reminder Strategy
## 17. Schedule Risk Alert Strategy
## 18. Baseline/Change Notification Strategy
## 19. Finance/Quote Masking Strategy
## 20. Dedup / Idempotency Strategy
## 21. API Changes
## 22. Authorization Matrix
## 23. Activity / Audit / Outbox Notes
## 24. Tests Added
## 25. Commands Run
## 26. Test Results
## 27. Manual Verification
## 28. Assumptions
## 29. Deviations From Prompt
## 30. Known Risks
## 31. Future Phases That Must Return to Notifications
```

---

# 27. Future phases that must return to Notifications

## 27.1 Phase 21 — AI-assisted Project Planning

AI can:

```text
summarize project alerts
explain schedule risk
draft stakeholder messages
prioritize notifications
```

AI must respect notification/resource permissions.

## 27.2 Phase 22 — Reporting / Dashboard / Export

Add:

```text
notification dashboard
delivery failure report
project risk notification report
daily/weekly project digest export
```

## 27.3 Phase 23 — Core Hardening

Review:

```text
notification spam controls
batch performance
access masking
template variables
delivery retry/dead-letter
scheduler lock
timezone correctness
```

## 27.4 Phase 27 — Full Document Hub

Notifications for:

```text
document shared
document approval needed
document version published
```

## 27.5 Phase 31 — Meetings / Collaboration

Notifications for:

```text
comments
mentions
meeting action items
decision updates
```

## 27.6 Phase 34 — Workflow / Approval

Workflow controls:

```text
approval routing
escalation
delegation
SLA reminders
approval reminders
```

## 27.7 Phase 35 — Advanced Notification / Subscription / Reminder / Digest

Full expansion:

```text
global subscription center
digest
escalation
quiet hours
mobile push
chat integration
advanced reminders
```

## 27.8 Phase 36 — Contract / Billing / Revenue

Notifications for:

```text
contract signed
invoice issued
payment overdue
billing milestone reached
```

## 27.9 Phase 37 — Time / Attendance / Expense

Notifications for:

```text
timesheet due
expense approval
actual cost variance
```

## 27.10 External Client Portal backlog

Notifications for external users require:

```text
external identity
explicit share
client notification templates
unsubscribe/compliance
client portal authorization
```

---

# 28. Agent anti-bịa rules

The agent must not:

```text
1. Implement duplicate notification engine instead of using Phase 06.
2. Claim mobile push exists unless push provider/device tokens exist.
3. Claim chat/Slack/Teams exists unless integration exists.
4. Claim digest exists unless digest scheduler/aggregation exists.
5. Claim workflow escalation exists unless workflow module exists.
6. Send finance/quote values to unauthorized users.
7. Treat notification as permission grant.
8. Send to inactive/removed workspace members.
9. Ignore user mute/preferences for optional notices.
10. Allow reminder job to duplicate notifications.
11. Notify external clients without external portal/access model.
12. Hide deferred advanced notification features.
```

---

# 29. Prompt to give coding agent

```text
You are implementing Phase 20 — TO-BE Project Events, Project Notification Rules, Watchers, Reminders, Alerts & Delivery Integration.

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
- Phase 06 Notification Engine TO-BE spec
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
- Phase 17 Project Budget/Margin TO-BE spec
- Phase 18 Quote TO-BE spec
- Phase 19 Baseline/Change Request TO-BE spec
- Current backend code, migrations, tests

Your task:
1. Compare current project notification capability against this Phase 20 TO-BE spec.
2. Classify each capability as CURRENTLY_IMPLEMENTED, PARTIALLY_IMPLEMENTED, MUST_IMPLEMENT_IN_PHASE_20, SEED_ONLY_IN_PHASE_20, DEFERRED_TO_PHASE_XX, or NOT_IN_SCOPE_FOR_BACKEND_NOW.
3. Implement only Phase 20 required items.
4. Verify/seed project EventDefinitions.
5. Seed project notification templates and rules using Phase 06 Notification Engine.
6. Implement ProjectNotificationSubscription and TaskNotificationSubscription.
7. Implement project notification preferences minimum.
8. Implement project recipient resolver with IAM/access masking.
9. Implement due-date reminder job and schedule-risk notification rules.
10. Implement baseline/change/finance/quote notification rules with masking.
11. Add notification permissions and tests listed in this spec.
12. Run mvn compile and mvn test.
13. Create docs/phase-complete/PHASE_20_PROJECT_EVENTS_NOTIFICATIONS_TO_BE_COMPLETE.md with full gap matrix.

Do not implement or claim mobile push, chat integration, digest engine, workflow escalation, external client notifications, client portal, SMS/WhatsApp, or AI notification summaries in this phase.
```

---

# 30. Quick tracking matrix

| Capability | Current backend | Phase 20 action | Later phase |
|---|---|---|---|
| Project EventDefinitions | Partial | Verify/seed | — |
| Project templates | Missing/partial | Must seed | — |
| Project notification rules | Missing/partial | Must seed | — |
| Project watcher | Missing/unknown | Must implement | Advanced subscription Phase 35 |
| Task watcher | Missing/unknown | Must implement | Advanced subscription Phase 35 |
| Recipient resolver | Missing/partial | Must implement | — |
| Notification preferences minimum | Missing/partial | Must implement | Phase 35 advanced |
| Due soon reminder | Missing | Must implement | Phase 35 advanced reminder |
| Overdue reminder | Missing | Must implement | Phase 35 advanced reminder |
| Schedule risk alert | Missing/partial | Must implement | Phase 22 report |
| Baseline/change alert | Missing | Must implement | Workflow Phase 34 |
| Finance/quote alert | Missing | Must implement with masking | — |
| Payload masking | Partial | Must implement project-specific | Phase 23 hardening |
| Dedup/idempotency | Partial | Must implement project keys | — |
| Mobile push | Missing | Defer | Phase 35/Post-23 |
| Chat integration | Missing | Defer | Phase 35/Post-23 |
| Digest | Missing | Defer | Phase 35 |
| External client notifications | Missing | Defer | External portal backlog |
| AI notification summary | Missing | Defer | Phase 21 |

---

# 31. Final principle

Phase 20 is not complete when "an email can be sent."

Phase 20 is complete when Scopery can deliver safe, relevant project notifications:

```text
project event
+ active notification rule
+ recipient resolver
+ access check
+ preference/mute
+ masking
+ dedup
+ Phase 06 delivery engine
= safe project notification
```

Notification is not permission.

Notification is not workflow approval.

Notification is not external portal access.

A reminder must be idempotent.

Sensitive project finance and quote data must be masked unless explicitly authorized.
