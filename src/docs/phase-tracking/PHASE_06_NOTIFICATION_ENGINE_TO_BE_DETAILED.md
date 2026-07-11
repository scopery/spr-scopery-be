# PHASE 06 — TO-BE Notification Engine, Email Template, In-app Notification, Delivery Log & Subscription Foundation

> Project: Scopery Backend  
> Phase: 06  
> Document type: TO-BE implementation-grade specification  
> Status: Planning / Gap-driven implementation spec  
> Depends on: Phase 00 — Master Backend Roadmap, Phase 01 — API Path & Security Baseline, Phase 02 — IAM Core, Phase 03 — Workspace Core, Phase 04 — Platform Audit / Outbox / Idempotency, Phase 05 — Event Registry  
> API base: `/api`  
> Primary module: `modules/notification`  
> Related modules: `eventregistry`, `platform/outbox`, `iam`, `workspace`, future `project`, `finance`, `quote`, `aiagent`, `reporting`  
> Important rule: This file is **not an as-is document**. It defines the TO-BE notification engine, compares it to the current email-focused implementation, and marks what must be implemented now vs deferred.

---

# 0. Purpose of this file

Phase 06 defines how Scopery delivers relevant, secure, auditable, and configurable notifications.

Notification is not just sending emails.

Future Scopery needs notification support for:

```text
In-app alerts
Email notifications
Approval messages
Security notices
Digest summaries
Mentions
Reminders
Escalations
Follow/subscription changes
AI-generated proposal alerts
Project risk alerts
Finance/quote approvals
Report export completion
Document publication
External portal messages
```

But current backend likely only has an email-focused notification foundation.

Phase 06 must turn the current Notification module into a reliable **notification engine core** without pretending all future channels are already implemented.

---

# 1. Source inputs

Before coding Phase 06, the agent must read:

```text
1. Current backend codebase
2. Current BE feature/entity/business-rule inventory
3. Dynamic Work OS future-state feature catalog
4. Phase 00 master roadmap
5. Phase 01 API/security contract
6. Phase 02 IAM TO-BE spec
7. Phase 03 Workspace TO-BE spec
8. Phase 04 Platform Audit/Outbox/Idempotency spec
9. Phase 05 Event Registry TO-BE spec
10. Existing notification module code/migrations/tests
11. Existing EmailTemplate / EmailTemplateVersion / EmailRule / EmailOutbox / EmailDelivery code
12. Existing EventDefinition/EventVariable integration
13. Existing platform event/outbox implementation if present
```

The agent must not implement from current code only.

---

# 2. Current backend snapshot

Current backend inventory says the Notification module currently has:

```text
4 action-level functions
20 business rules
```

Current entities are expected to include:

```text
EmailTemplate
EmailTemplateVersion
EmailRule
EmailOutbox
EmailDelivery
```

Current notification functions likely cover:

```text
Email template management
Email template version publishing
Email rule management
Email outbox / delivery processing
```

Current known constraints from current design:

```text
Email templates are versioned.
Email rules connect events to templates.
Email outbox stores pending email work.
Email delivery stores send results.
Event Registry provides event definitions and variables.
```

This is useful, but not enough for full future-state Notification and Subscription.

---

# 3. Future-state Work OS notification capabilities

The uploaded/future Work OS feature catalog defines the Notification and Subscription module as a Wave 2 module with these capabilities:

```text
NTF-001 In-app notification
NTF-002 Email notification
NTF-003 Mobile/push notification
NTF-004 Chat integration
NTF-005 Notification preference
NTF-006 Follow/subscribe
NTF-007 Mention
NTF-008 Reminder
NTF-009 Escalation notification
NTF-010 Digest
NTF-011 Template management
NTF-012 Delivery log
NTF-013 Notification deduplication
NTF-014 Sensitive content masking
NTF-015 Mandatory notice
```

Phase 06 cannot implement every future capability fully.

Phase 06 must implement the core foundation and explicitly defer advanced features.

---

# 4. Classification labels

Every requirement uses one label:

| Label | Meaning |
|---|---|
| `CURRENTLY_IMPLEMENTED` | Current backend already implements it. Agent must verify and test. |
| `MUST_IMPLEMENT_IN_PHASE_06` | Must be implemented or hardened now. |
| `SEED_ONLY_IN_PHASE_06` | Create seeds/templates/rules now; full business producer comes later. |
| `DEFERRED_TO_PHASE_XX` | Do not implement now; later phase must return to Notification. |
| `NOT_IN_SCOPE_FOR_BACKEND_NOW` | Explicitly not backend scope for current roadmap. |

---

# 5. Phase 06 target statement

Phase 06 must deliver a future-ready notification core:

```text
1. Event-driven notification pipeline.
2. In-app notification item foundation.
3. Email template management with versioning.
4. Email template variable validation against Event Registry.
5. Email rule engine connected to EventDefinition.
6. Email outbox and delivery state machine.
7. Retry, deduplication, and dead-letter behavior.
8. Sensitive content masking rules.
9. Mandatory notice support for security/legal/approval events.
10. Basic recipient resolution contract.
11. Delivery log and audit/activity behavior.
12. Idempotent notification/email seeders.
13. Clear deferrals for push, chat, digest, reminders, mentions, subscriptions, preferences, escalation.
14. Future phase return matrix.
```

---

# 6. Phase 06 implementation decision

Phase 06 must not explode into a full omnichannel notification platform.

Recommended Phase 06 scope:

```text
MUST_IMPLEMENT:
- Email template/version/rule/outbox/delivery hardening.
- EventRegistry validation.
- In-app notification item minimal backend.
- Deduplication key support.
- Mandatory notice flag.
- Sensitive variable masking validation.
- Activity/audit/outbox tests.
- Seeders for current system emails.

DEFER:
- Mobile push.
- Chat integration.
- Full notification preferences with quiet hours.
- Follow/subscribe.
- Mention engine.
- Recurring reminders.
- Escalation engine.
- Digest scheduler.
- External portal notifications.
- Provider webhook callbacks/open tracking if not present.
```

If product chooses email-only MVP, the agent may defer in-app notifications but must mark that clearly. However, TO-BE recommendation is to implement minimal in-app notification now because it is the simplest common delivery channel and future UI can consume it later.

---

# 7. TO-BE capability matrix

---

## 7.1 NTF-001 — In-app notification

| Item | Value |
|---|---|
| Future capability | Deliver actionable alerts linked to source resources |
| Current state | Not confirmed in current inventory |
| Phase 06 target | Implement minimal backend notification item, or explicitly defer |
| Recommended classification | `MUST_IMPLEMENT_IN_PHASE_06` |

### Required behavior if implemented

In-app notification should be a durable user-facing record.

It answers:

```text
What happened?
Why should the user care?
Where can the user click?
Has the user read/dismissed it?
```

Required entity:

```text
NotificationItem
```

Required APIs:

```text
GET   /api/notifications
GET   /api/notifications/unread-count
PATCH /api/notifications/{id}/read
PATCH /api/notifications/read-all
PATCH /api/notifications/{id}/dismiss
```

Required rules:

```text
1. NotificationItem belongs to exactly one recipient user.
2. Recipient can only read/update own notifications.
3. Admin cannot casually read user's notification content unless compliance/admin API exists.
4. Notification has title, bodyPreview, severity, source reference, actionUrl/actionType.
5. Read changes readAt.
6. Dismiss changes dismissedAt.
7. Notification must not leak hidden resource details.
8. If source resource becomes inaccessible, actionUrl may still exist but UI/API must recheck access.
9. Deduplication prevents storm for same event/rule/recipient.
10. Mandatory notices may be read but not suppressed by preferences.
```

If deferred:

```text
DEFERRED_TO_PHASE_20_PROJECT_EVENTS_NOTIFICATIONS or dedicated Notification UI phase.
```

---

## 7.2 NTF-002 — Email notification

| Item | Value |
|---|---|
| Future capability | Send transactional, digest, and approval emails using controlled templates |
| Current state | Email-focused notification module exists |
| Phase 06 target | Harden and complete email engine core |
| Classification | `MUST_IMPLEMENT_IN_PHASE_06` |

Required behavior:

```text
1. Email templates are versioned.
2. Only published/active template versions can send.
3. Email rules bind EventDefinition to template version.
4. Email rule cannot bind inactive/deprecated event.
5. Email variable validation uses EventVariable.
6. Email outbox is created from matched event/rule.
7. Email delivery state machine is retry-safe.
8. Duplicate event/rule/recipient does not create duplicate logical email.
9. Sensitive variables are masked or require explicit permission.
10. Delivery logs are retained and auditable.
```

---

## 7.3 NTF-003 — Mobile / push notification

| Item | Value |
|---|---|
| Future capability | Push approved high-priority events where supported |
| Current state | Not implemented |
| Phase 06 target | Defer |
| Classification | `DEFERRED_TO_PHASE_37_INTEGRATION` or mobile app phase |

Future entities:

```text
PushDevice
PushSubscription
PushDelivery
```

Future rules:

```text
1. User must register device.
2. Device token encrypted or provider-safe.
3. User can revoke device.
4. Push payload must be masked.
5. Mandatory notice may bypass channel preference but not OS-level block.
```

Do not implement fake push in Phase 06.

---

## 7.4 NTF-004 — Chat integration

| Item | Value |
|---|---|
| Future capability | Send to Slack/Teams/Zalo/etc. under organization policy |
| Current state | Not implemented |
| Phase 06 target | Defer |
| Classification | `DEFERRED_TO_PHASE_37_INTEGRATION` |

Future entities:

```text
ChatIntegrationConnection
ChatChannelBinding
ChatDelivery
```

Rules:

```text
1. Organization admin enables channel.
2. Workspace binds channel.
3. Sensitive content masking applies.
4. Delivery audited.
```

---

## 7.5 NTF-005 — Notification preference

| Item | Value |
|---|---|
| Future capability | Configure channel/event/priority/quiet-hours/digest |
| Current state | Not confirmed |
| Phase 06 target | Implement minimal preference only if low-risk; otherwise defer |
| Classification | `DEFERRED_TO_PHASE_20` for project-level preferences; `DEFERRED_TO_PHASE_23` for quiet-hour hardening |

Recommended Phase 06 minimum:

```text
NotificationPreference entity optional:
- userId
- channel
- eventCode or eventCategory
- enabled
```

But if not immediately needed, defer to avoid overbuilding.

Important mandatory notice rule:

```text
Mandatory/security/legal/approval notices cannot be fully opted out.
```

Future preferences:

```text
quiet hours
timezone
daily digest
weekly digest
priority threshold
channel fallback
resource-specific preferences
```

---

## 7.6 NTF-006 — Follow / subscribe

| Item | Value |
|---|---|
| Future capability | Subscribe to resource/filter/project/report changes |
| Current state | Not implemented |
| Phase 06 target | Defer |
| Classification | `DEFERRED_TO_PHASE_20_PROJECT_EVENTS_NOTIFICATIONS` and `PHASE_22_REPORTING` |

Future entities:

```text
NotificationSubscription
NotificationSubscriptionFilter
```

Future resources:

```text
project
task
wbs node
document
report
quote
change request
```

Do not implement in Phase 06 except reserve table/permission if product asks.

---

## 7.7 NTF-007 — Mention

| Item | Value |
|---|---|
| Future capability | Notify mentioned users subject to access |
| Current state | Not implemented |
| Phase 06 target | Defer |
| Classification | `DEFERRED_TO_PHASE_30_MEETINGS_COLLABORATION` or Project comments phase |

Future rules:

```text
1. Mentioned user must have access to source resource.
2. Mention does not grant access.
3. Hidden source details must be masked.
4. Notification can include safe excerpt only.
```

---

## 7.8 NTF-008 — Reminder

| Item | Value |
|---|---|
| Future capability | Personal or policy-driven reminder with snooze/recurrence |
| Current state | Not implemented |
| Phase 06 target | Defer |
| Classification | `DEFERRED_TO_PHASE_20` or `PHASE_32_WORKFLOW_AUTOMATION` |

Future entities:

```text
Reminder
ReminderOccurrence
ReminderSnooze
```

Future uses:

```text
task due reminder
approval pending reminder
quote follow-up
change request review reminder
```

---

## 7.9 NTF-009 — Escalation notification

| Item | Value |
|---|---|
| Future capability | Notify higher authority after unresolved threshold |
| Current state | Not implemented |
| Phase 06 target | Defer |
| Classification | `DEFERRED_TO_PHASE_32_WORKFLOW_AUTOMATION` |

Future rules:

```text
1. Escalation policy defines threshold.
2. Escalation recipient resolved by role/manager/permission.
3. Escalation audited.
4. Duplicate escalation deduped.
```

---

## 7.10 NTF-010 — Digest

| Item | Value |
|---|---|
| Future capability | Summarize relevant changes daily/weekly |
| Current state | Not implemented |
| Phase 06 target | Defer |
| Classification | `DEFERRED_TO_PHASE_22_REPORTING` or Phase 20 notification expansion |

Future entities:

```text
NotificationDigestPreference
NotificationDigestJob
NotificationDigestItem
```

Do not implement digest before subscription/preference model exists.

---

## 7.11 NTF-011 — Template management

| Item | Value |
|---|---|
| Future capability | Localize and version subject/body/variables/action links |
| Current state | EmailTemplate and EmailTemplateVersion exist |
| Phase 06 target | Harden current template system |
| Classification | `MUST_IMPLEMENT_IN_PHASE_06` |

Required current behavior:

```text
1. Template code unique.
2. Template belongs to scope SYSTEM / ORGANIZATION / WORKSPACE if supported.
3. Template has versions.
4. Draft version can be edited.
5. Published version immutable.
6. Only one active/published version used by EmailRule unless rule pins version.
7. Variables validate against EventDefinition variables.
8. Template preview renders with sample data.
9. Template content cannot reference unknown variables.
10. Template localization deferred unless already present.
```

Localization:

```text
DEFERRED_TO_PHASE_23 or Phase 39 unless current code supports locale.
```

---

## 7.12 NTF-012 — Delivery log

| Item | Value |
|---|---|
| Future capability | Record sent, delivered, bounced, failed, opened where permitted, and retried |
| Current state | EmailDelivery exists |
| Phase 06 target | Harden delivery log and provider status model |
| Classification | `MUST_IMPLEMENT_IN_PHASE_06` for sent/failed/retry; `DEFERRED_TO_PHASE_37` for provider webhook delivered/bounced/opened |

Required now:

```text
PENDING
PROCESSING
SENT
FAILED
RETRY_SCHEDULED
DEAD_LETTER
CANCELLED
```

Optional future provider statuses:

```text
DELIVERED
BOUNCED
COMPLAINED
OPENED
CLICKED
```

Privacy rule:

```text
Opened/clicked tracking may require consent/privacy review.
Defer to compliance/integration phase.
```

---

## 7.13 NTF-013 — Notification deduplication

| Item | Value |
|---|---|
| Future capability | Combine repeated events and prevent storms |
| Current state | Not confirmed |
| Phase 06 target | Implement basic deduplication key for email/in-app |
| Classification | `MUST_IMPLEMENT_IN_PHASE_06` |

Required basic rule:

```text
dedupKey = eventOutboxId + ruleId + recipientUserId/channel
```

For storm compression:

```text
DEFERRED_TO_PHASE_20/22
```

Phase 06 minimum:

```text
1. Processor retry does not create duplicate email/in-app item.
2. Same event/rule/recipient is unique.
3. Dedup result is logged.
```

---

## 7.14 NTF-014 — Sensitive content masking

| Item | Value |
|---|---|
| Future capability | Avoid leaking classified details through email/push/chat |
| Current state | Not confirmed |
| Phase 06 target | Implement rule using EventVariable.sensitive and template validation |
| Classification | `MUST_IMPLEMENT_IN_PHASE_06` |

Required rules:

```text
1. EventVariable.sensitive=true cannot be used in a template unless template/rule explicitly allows it.
2. Allowing sensitive variables requires permission and audit.
3. Email subjects should not contain sensitive variables by default.
4. In-app bodyPreview should not contain sensitive variables.
5. Action link may point to secure resource instead of embedding detail.
6. Notification engine must never bypass resource access checks for detailed content.
```

Future finance/document masking:

```text
Phase 17 finance
Phase 18 quote
Phase 08 document
Phase 22 reporting
```

---

## 7.15 NTF-015 — Mandatory notice

| Item | Value |
|---|---|
| Future capability | Prevent opt-out for security, legal, required approval events |
| Current state | Not confirmed |
| Phase 06 target | Add mandatory flag on rule/template/notification category |
| Classification | `MUST_IMPLEMENT_IN_PHASE_06` as foundation |

Required rules:

```text
1. Mandatory notice ignores user opt-out preferences.
2. Mandatory does not bypass access checks.
3. Mandatory does not bypass masking.
4. Mandatory classification must be explicit.
5. Marking rule mandatory requires notification admin permission.
6. Mandatory use is audited.
```

Examples:

```text
password reset
security alert
quote approval required
change request approval
financial scenario approval
legal/compliance notice
```

---

# 8. TO-BE architecture

## 8.1 Event-driven notification flow

Preferred flow:

```text
Business Action
  → PlatformEventOutbox record
  → Notification Event Consumer / Processor
  → find active Notification/Email rules by EventDefinition
  → resolve recipients
  → check access / preferences / mandatory / masking
  → create InAppNotificationItem and/or EmailOutbox
  → delivery processors send email
  → EmailDelivery records result
```

If generic PlatformEventOutbox is not implemented yet:

```text
Current application event listener may be used temporarily.
Completion file must mark this as gap and target Phase 04/20 follow-up.
```

## 8.2 Do not send directly from business actions

Business actions should not call SMTP/provider directly.

Allowed:

```text
Business action emits event/outbox.
Notification engine creates email outbox.
Email processor sends later.
```

Reason:

```text
Avoid slow transactions, duplicate sends, rollback mismatch, and untestable side effects.
```

---

# 9. Entity model TO-BE

If current schema differs, agent must map current fields and document gaps.

---

## 9.1 EmailTemplate — `notification_email_template`

Required fields:

```text
id UUID PK
code VARCHAR(150) NOT NULL UNIQUE
name VARCHAR(255) NOT NULL
description TEXT NULL
scope VARCHAR(50) NOT NULL
organization_id UUID NULL
workspace_id UUID NULL
event_definition_id UUID NULL
status VARCHAR(50) NOT NULL
created_at / created_by
updated_at / updated_by
version INT
```

Status:

```text
ACTIVE
INACTIVE
ARCHIVED
```

Scope:

```text
SYSTEM
ORGANIZATION
WORKSPACE
```

Phase 06 decision:

```text
If current module only supports SYSTEM templates, keep SYSTEM and defer org/workspace override.
```

Deferred:

```text
Organization/workspace template override — DEFERRED_TO_PHASE_20/39 unless already implemented.
```

---

## 9.2 EmailTemplateVersion — `notification_email_template_version`

Required fields:

```text
id UUID PK
template_id UUID NOT NULL
version_number INT NOT NULL
subject_template TEXT NOT NULL
html_body_template TEXT NOT NULL
text_body_template TEXT NULL
status VARCHAR(50) NOT NULL
published_at TIMESTAMP NULL
published_by UUID NULL
created_at / created_by
updated_at / updated_by
```

Status:

```text
DRAFT
PUBLISHED
ARCHIVED
```

Rules:

```text
1. version_number unique per template.
2. DRAFT can be edited.
3. PUBLISHED immutable.
4. Publishing validates variables.
5. Published version cannot be deleted.
6. Only one current published version unless rules pin versions.
```

---

## 9.3 EmailRule — `notification_email_rule`

Required fields:

```text
id UUID PK
code VARCHAR(150) NOT NULL UNIQUE
name VARCHAR(255) NOT NULL
event_definition_id UUID NOT NULL
template_id UUID NOT NULL
template_version_id UUID NULL
status VARCHAR(50) NOT NULL
recipient_strategy VARCHAR(100) NOT NULL
recipient_config_json JSONB NULL
condition_json JSONB NULL
priority VARCHAR(50) NOT NULL
mandatory BOOLEAN NOT NULL DEFAULT false
allow_sensitive_variables BOOLEAN NOT NULL DEFAULT false
dedup_window_seconds INT NULL
created_at / created_by
updated_at / updated_by
```

Status:

```text
ACTIVE
INACTIVE
ARCHIVED
```

Recipient strategy examples:

```text
ACTOR
TARGET_USER
WORKSPACE_ADMINS
PROJECT_MEMBERS
PROJECT_OWNER
TASK_ASSIGNEE
QUOTE_APPROVERS
CHANGE_REQUEST_APPROVERS
STATIC_EMAILS
EVENT_VARIABLE_EMAIL
```

Phase 06 must implement only strategies used by current seed/templates.

Do not implement fake project/quote strategies before their modules exist.

---

## 9.4 EmailOutbox — `notification_email_outbox`

Required fields:

```text
id UUID PK
event_outbox_id UUID NULL
event_definition_id UUID NOT NULL
email_rule_id UUID NOT NULL
template_version_id UUID NOT NULL
recipient_user_id UUID NULL
recipient_email VARCHAR(255) NOT NULL
subject TEXT NOT NULL
html_body TEXT NOT NULL
text_body TEXT NULL
status VARCHAR(50) NOT NULL
priority VARCHAR(50) NOT NULL
dedup_key VARCHAR(255) NOT NULL
available_at TIMESTAMP NOT NULL
attempt_count INT NOT NULL DEFAULT 0
max_attempts INT NOT NULL DEFAULT 5
last_attempt_at TIMESTAMP NULL
next_retry_at TIMESTAMP NULL
locked_by VARCHAR(100) NULL
locked_until TIMESTAMP NULL
error_code VARCHAR(150) NULL
error_message TEXT NULL
provider_message_id VARCHAR(255) NULL
trace_id VARCHAR(100) NULL
created_at / updated_at
```

Status:

```text
PENDING
PROCESSING
SENT
FAILED
RETRY_SCHEDULED
DEAD_LETTER
CANCELLED
```

Unique:

```text
dedup_key
```

---

## 9.5 EmailDelivery — `notification_email_delivery`

Required fields:

```text
id UUID PK
email_outbox_id UUID NOT NULL
provider VARCHAR(100) NULL
provider_message_id VARCHAR(255) NULL
status VARCHAR(50) NOT NULL
attempt_number INT NOT NULL
sent_at TIMESTAMP NULL
failed_at TIMESTAMP NULL
failure_code VARCHAR(150) NULL
failure_message TEXT NULL
response_json JSONB NULL
created_at TIMESTAMP NOT NULL
```

Status:

```text
SENT
FAILED
BOUNCED future
DELIVERED future
OPENED future
CLICKED future
```

Phase 06 required:

```text
SENT
FAILED
```

Provider webhook statuses:

```text
DEFERRED_TO_PHASE_37_INTEGRATION
```

---

## 9.6 NotificationItem — `notification_item`

Recommended Phase 06 entity.

Required fields:

```text
id UUID PK
recipient_user_id UUID NOT NULL
event_outbox_id UUID NULL
event_definition_id UUID NULL
source_system VARCHAR(100) NULL
source_resource_type VARCHAR(100) NULL
source_resource_id UUID NULL
organization_id UUID NULL
workspace_id UUID NULL
project_id UUID NULL
title VARCHAR(255) NOT NULL
body_preview TEXT NULL
severity VARCHAR(50) NOT NULL
priority VARCHAR(50) NOT NULL
action_type VARCHAR(100) NULL
action_url TEXT NULL
dedup_key VARCHAR(255) NOT NULL
mandatory BOOLEAN NOT NULL DEFAULT false
status VARCHAR(50) NOT NULL
read_at TIMESTAMP NULL
dismissed_at TIMESTAMP NULL
created_at TIMESTAMP NOT NULL
updated_at TIMESTAMP NOT NULL
trace_id VARCHAR(100) NULL
```

Status:

```text
UNREAD
READ
DISMISSED
ARCHIVED
```

Severity:

```text
INFO
SUCCESS
WARNING
ERROR
SECURITY
APPROVAL
```

Unique:

```text
recipient_user_id + dedup_key
```

If not implemented in Phase 06:

```text
DEFERRED_TO_PHASE_20_PROJECT_EVENTS_NOTIFICATIONS
```

---

## 9.7 NotificationPreference — future/minimal

Optional Phase 06 entity.

If implemented minimally:

```text
id UUID PK
user_id UUID NOT NULL
event_code VARCHAR(150) NULL
channel VARCHAR(50) NOT NULL
enabled BOOLEAN NOT NULL DEFAULT true
created_at / updated_at
```

Channels:

```text
IN_APP
EMAIL
PUSH future
CHAT future
```

But full preferences are deferred:

```text
quiet hours
digest frequency
priority threshold
resource-specific preferences
org/workspace policy overrides
```

Recommended classification:

```text
DEFERRED_TO_PHASE_20
```

---

## 9.8 NotificationSubscription — future

Deferred entity:

```text
NotificationSubscription
NotificationSubscriptionFilter
```

Deferred to:

```text
PHASE_20_PROJECT_EVENTS_NOTIFICATIONS
PHASE_22_REPORTING
PHASE_30_MEETINGS_COLLABORATION
```

---

# 10. API TO-BE list

All APIs use `/api`.

---

## 10.1 Email template APIs

```text
POST  /api/notification/email-templates
GET   /api/notification/email-templates
GET   /api/notification/email-templates/{id}
PUT   /api/notification/email-templates/{id}
PATCH /api/notification/email-templates/{id}/activate
PATCH /api/notification/email-templates/{id}/deactivate
PATCH /api/notification/email-templates/{id}/archive
```

Rules:

```text
Create/update requires NOTIFICATION_TEMPLATE_CREATE/UPDATE.
Archive requires NOTIFICATION_TEMPLATE_MANAGE.
```

---

## 10.2 Email template version APIs

```text
POST  /api/notification/email-templates/{templateId}/versions
GET   /api/notification/email-templates/{templateId}/versions
GET   /api/notification/email-templates/{templateId}/versions/{versionId}
PUT   /api/notification/email-templates/{templateId}/versions/{versionId}
POST  /api/notification/email-templates/{templateId}/versions/{versionId}/publish
POST  /api/notification/email-templates/{templateId}/versions/{versionId}/preview
```

Rules:

```text
1. PUT only DRAFT.
2. Publish validates variables.
3. Preview can use sample payload.
4. Published version immutable.
```

---

## 10.3 Email rule APIs

```text
POST  /api/notification/email-rules
GET   /api/notification/email-rules
GET   /api/notification/email-rules/{id}
PUT   /api/notification/email-rules/{id}
PATCH /api/notification/email-rules/{id}/enable
PATCH /api/notification/email-rules/{id}/disable
PATCH /api/notification/email-rules/{id}/archive
```

Rules:

```text
1. EventDefinition must be ACTIVE.
2. Template must be ACTIVE.
3. Template version must be PUBLISHED if pinned.
4. Recipient strategy must be supported.
5. allowSensitiveVariables requires permission and audit.
6. mandatory=true requires permission and audit.
```

---

## 10.4 Email outbox / delivery APIs

Admin/support only:

```text
GET  /api/notification/email-outbox
GET  /api/notification/email-outbox/{id}
POST /api/notification/email-outbox/{id}/retry
POST /api/notification/email-outbox/{id}/cancel

GET  /api/notification/email-deliveries
GET  /api/notification/email-deliveries/{id}
```

If not exposed:

```text
DEFERRED_TO_PHASE_23_SUPPORT_ADMIN
```

Write-side processor must still exist/test.

---

## 10.5 In-app notification APIs

If NotificationItem implemented:

```text
GET   /api/notifications
GET   /api/notifications/unread-count
PATCH /api/notifications/{id}/read
PATCH /api/notifications/read-all
PATCH /api/notifications/{id}/dismiss
```

Rules:

```text
1. User can only see own notifications.
2. read/dismiss only own notification.
3. dismissed notifications hidden by default.
4. unread-count excludes dismissed.
```

If deferred, document.

---

## 10.6 Preference APIs

Defer unless implemented:

```text
GET /api/notification/preferences
PUT /api/notification/preferences
```

Deferred:

```text
PHASE_20 or PHASE_23
```

---

# 11. Authorization requirements

Required IAM authorities:

```text
NOTIFICATION_TEMPLATE_VIEW
NOTIFICATION_TEMPLATE_CREATE
NOTIFICATION_TEMPLATE_UPDATE
NOTIFICATION_TEMPLATE_PUBLISH
NOTIFICATION_TEMPLATE_ARCHIVE
NOTIFICATION_TEMPLATE_MANAGE

NOTIFICATION_EMAIL_RULE_VIEW
NOTIFICATION_EMAIL_RULE_CREATE
NOTIFICATION_EMAIL_RULE_UPDATE
NOTIFICATION_EMAIL_RULE_ENABLE
NOTIFICATION_EMAIL_RULE_DISABLE
NOTIFICATION_EMAIL_RULE_MANAGE

NOTIFICATION_DELIVERY_VIEW
NOTIFICATION_DELIVERY_RETRY
NOTIFICATION_DELIVERY_CANCEL

NOTIFICATION_ITEM_VIEW_OWN
NOTIFICATION_ITEM_UPDATE_OWN
NOTIFICATION_ADMIN_VIEW_ALL optional
```

Rules:

```text
1. Template/rule admin endpoints require notification admin authorities.
2. Delivery/outbox admin endpoints require delivery support/admin authorities.
3. User notification inbox endpoints require authentication and own-record check.
4. System processors use internal service, not user auth.
5. Sensitive variable usage requires NOTIFICATION_TEMPLATE_MANAGE or equivalent.
6. mandatory notice configuration requires NOTIFICATION_EMAIL_RULE_MANAGE.
```

---

# 12. Event Registry integration rules

Phase 06 must consume Phase 05 Event Registry.

Rules:

```text
1. EmailRule.eventDefinitionId must point to ACTIVE EventDefinition.
2. Template variables must be a subset of EventVariable paths.
3. Unknown variables reject publish.
4. Sensitive variables reject publish unless template/rule allows and actor has permission.
5. Inactive/deprecated events cannot be used by new rules.
6. Deactivating/deprecating EventDefinition with active EmailRule must be blocked or flagged by Event Registry consumer safety.
7. Email rule stores eventDefinitionId, not raw event string only.
```

Template syntax examples:

```text
{{user.fullName}}
{{workspace.name}}
{{invitation.acceptUrl}}
```

Agent must use existing template syntax if already defined.

---

# 13. Recipient resolution TO-BE

Recipient resolution is a major source of risk.

## 13.1 Recipient strategy contract

EmailRule must declare `recipientStrategy`.

Minimum Phase 06 strategies:

```text
EVENT_VARIABLE_EMAIL
EVENT_VARIABLE_USER
ACTOR
STATIC_EMAILS
```

Optional if current code supports:

```text
WORKSPACE_ADMINS
ORG_ADMINS
```

Future strategies:

```text
PROJECT_OWNER
PROJECT_MEMBERS
TASK_ASSIGNEE
QUOTE_APPROVERS
CHANGE_REQUEST_APPROVERS
REPORT_REQUESTER
DOCUMENT_WATCHERS
SUBSCRIBERS
MENTIONED_USERS
```

Future strategies must be added in their owning phase.

## 13.2 Access-safe recipient rule

Notification engine must not send resource details to users who cannot access the resource.

Rule:

```text
If recipient is resolved from event but does not have access to source resource,
send only generic/limited notice or skip according to rule config.
```

For Phase 06 current flows:

```text
password reset email — recipient identity flow, not resource access.
workspace/org invitation — recipient may not yet be member; email content limited to invitation details.
join request admin email — recipients must be workspace managers/admins.
```

Project/finance/quote access checks are deferred to those phases.

---

# 14. Template rendering and variable rules

## 14.1 Template validation

At publish time:

```text
1. Parse subject/html/text templates.
2. Extract variable references.
3. Ensure every variable exists in EventVariable for template event.
4. Ensure required variables have fallback or are present in schema.
5. Reject unknown variables.
6. Reject sensitive variables unless allowed.
7. Reject dangerous template expressions if template engine supports logic.
```

## 14.2 Rendering rules

At send time:

```text
1. Load event payload.
2. Resolve variables.
3. Apply masking rules.
4. Render subject/html/text.
5. Validate recipient email.
6. Create EmailOutbox.
```

If required variable missing:

```text
rule-configurable:
- FAIL_AND_LOG
- SKIP_RECIPIENT
- SEND_WITH_FALLBACK
```

Phase 06 default:

```text
FAIL_AND_LOG
```

## 14.3 Action links

Action links should point to secure frontend route.

Rules:

```text
1. Do not embed access token.
2. Do not embed raw invitation code except invite/reset flows where link itself is the token.
3. Link target must re-check authorization.
4. Link must include workspace/project context where helpful.
```

---

# 15. Email outbox processing rules

## 15.1 State transition

```text
PENDING → PROCESSING → SENT
PENDING → PROCESSING → FAILED → RETRY_SCHEDULED → PENDING
FAILED/RETRY_SCHEDULED → DEAD_LETTER after max attempts
PENDING → CANCELLED
```

## 15.2 Locking

Processor must avoid double-send.

Options:

```text
SELECT FOR UPDATE SKIP LOCKED
locked_by + locked_until atomic update
ShedLock for job-level lock
```

Rules:

```text
1. Processor picks limited batch.
2. Processor locks row before sending.
3. Expired lock can be retried.
4. SENT cannot be sent again.
5. CANCELLED cannot be sent.
```

## 15.3 Retry

Rules:

```text
1. attempt_count increments on every failed attempt.
2. next_retry_at calculated by backoff.
3. error_code/error_message recorded safely.
4. max attempts leads to DEAD_LETTER.
5. Manual retry allowed for DEAD_LETTER only if admin/support API exists.
```

## 15.4 Provider abstraction

Use interface:

```text
EmailSender
```

Do not couple business logic to SMTP/provider.

Provider response must map to:

```text
providerMessageId
status
failureCode
failureMessage
```

---

# 16. Deduplication rules

Minimum dedupe keys:

## 16.1 Email

```text
dedupKey = eventOutboxId + ":" + emailRuleId + ":" + recipientEmail
```

If eventOutboxId unavailable:

```text
dedupKey = eventCode + ":" + aggregateId + ":" + emailRuleId + ":" + recipientEmail + ":" + stableEventOccurrenceId
```

## 16.2 In-app

```text
dedupKey = eventOutboxId + ":" + ruleId + ":" + recipientUserId
```

## 16.3 Business rules

```text
1. Same dedupKey cannot create duplicate active outbox/item.
2. Processor retry must not create duplicate logical email.
3. Dedup result should be logged.
4. Storm-compression across many events is deferred.
```

---

# 17. Sensitive content masking

## 17.1 Sensitive variable source

Sensitive variables come from EventVariable:

```text
sensitive=true
```

Examples:

```text
reset.url
invitation.acceptUrl
financial.marginPercent
quote.totalAmount
document.downloadUrl
client.email
```

## 17.2 Email template subject rule

Default:

```text
Sensitive variables are not allowed in email subject.
```

Exception:

```text
Invitation/reset URLs should never be in subject anyway.
```

## 17.3 Body rule

Sensitive variables in body require:

```text
allowSensitiveVariables=true
actor has NOTIFICATION_TEMPLATE_MANAGE
audit event created on publish/rule change
```

## 17.4 In-app body preview

```text
bodyPreview must be safe.
Do not include sensitive variables.
Use generic text and secure action link.
```

---

# 18. Mandatory notice rules

Mandatory notices are for:

```text
security
legal
approval required
account recovery
workspace invitation if product says required
critical project risk future
```

Rules:

```text
1. mandatory=true cannot be opted out.
2. mandatory still respects access and masking.
3. Only authorized notification admin can mark rule mandatory.
4. Mandatory rule creation/update audited.
5. Mandatory notices should avoid high-frequency events.
```

---

# 19. Activity log and audit requirements

## 19.1 Activity actions

```text
EMAIL_TEMPLATE_CREATED
EMAIL_TEMPLATE_UPDATED
EMAIL_TEMPLATE_ARCHIVED
EMAIL_TEMPLATE_VERSION_CREATED
EMAIL_TEMPLATE_VERSION_UPDATED
EMAIL_TEMPLATE_VERSION_PUBLISHED
EMAIL_RULE_CREATED
EMAIL_RULE_UPDATED
EMAIL_RULE_ENABLED
EMAIL_RULE_DISABLED
EMAIL_RULE_ARCHIVED
EMAIL_OUTBOX_ENQUEUED
EMAIL_OUTBOX_RETRIED
EMAIL_OUTBOX_CANCELLED
EMAIL_DELIVERY_SENT
EMAIL_DELIVERY_FAILED
NOTIFICATION_ITEM_CREATED
NOTIFICATION_ITEM_READ
NOTIFICATION_ITEM_DISMISSED
```

## 19.2 Audit-sensitive actions

Audit:

```text
EMAIL_TEMPLATE_VERSION_PUBLISHED
EMAIL_RULE_ENABLED
EMAIL_RULE_UPDATED_WITH_SENSITIVE_VARIABLES
EMAIL_RULE_MARKED_MANDATORY
EMAIL_OUTBOX_MANUAL_RETRY
EMAIL_OUTBOX_CANCELLED
EMAIL_DELIVERY_DEAD_LETTERED
NOTIFICATION_MASKING_BYPASSED
```

Do not audit raw rendered body if it contains sensitive variables.

---

# 20. Event definitions owned by Notification module

Source system:

```text
SCOPERY_NOTIFICATION
```

Required Phase 06 event definitions:

```text
EMAIL_TEMPLATE_CREATED
EMAIL_TEMPLATE_VERSION_PUBLISHED
EMAIL_RULE_CREATED
EMAIL_RULE_ENABLED
EMAIL_RULE_DISABLED
EMAIL_OUTBOX_ENQUEUED
EMAIL_DELIVERY_SENT
EMAIL_DELIVERY_FAILED
EMAIL_DELIVERY_DEAD_LETTERED
NOTIFICATION_ITEM_CREATED
NOTIFICATION_ITEM_READ
NOTIFICATION_ITEM_DISMISSED
NOTIFICATION_DEDUPLICATED
NOTIFICATION_MASKED
```

Future/deferred:

```text
PUSH_DELIVERY_SENT
PUSH_DELIVERY_FAILED
CHAT_MESSAGE_SENT
CHAT_MESSAGE_FAILED
DIGEST_CREATED
DIGEST_SENT
REMINDER_TRIGGERED
ESCALATION_TRIGGERED
SUBSCRIPTION_CREATED
MENTION_CREATED
```

---

# 21. Email template seeders

Phase 06 must seed or verify current required system email templates.

## 21.1 Required current templates

From Phase 02 and Phase 03:

```text
IAM_PASSWORD_RESET_REQUEST_EMAIL
IAM_EMAIL_VERIFICATION_EMAIL optional if email verification implemented
ORG_INVITATION_CREATED_EMAIL
WORKSPACE_INVITATION_CREATED_EMAIL
WORKSPACE_JOIN_REQUEST_CREATED_ADMIN_EMAIL
WORKSPACE_JOIN_REQUEST_APPROVED_EMAIL
WORKSPACE_JOIN_REQUEST_REJECTED_EMAIL
```

## 21.2 Future seed ownership

Do not seed future module templates as active until the module exists unless marked seed-only.

Future:

```text
PROJECT_TASK_ASSIGNED_EMAIL — Phase 20
PROJECT_DUE_DATE_AT_RISK_EMAIL — Phase 13/20
PROJECT_MARGIN_BELOW_TARGET_EMAIL — Phase 17/20
QUOTE_APPROVAL_REQUIRED_EMAIL — Phase 18/20
CHANGE_REQUEST_APPROVAL_REQUIRED_EMAIL — Phase 19/20
REPORT_EXPORT_COMPLETED_EMAIL — Phase 22
AI_PROJECT_PLAN_READY_EMAIL — Phase 21
DOCUMENT_PUBLISHED_EMAIL — Phase 08/20
```

## 21.3 Seeder rules

```text
1. Idempotent.
2. Creates EmailTemplate if missing.
3. Creates draft/published EmailTemplateVersion if missing.
4. Validates variables against EventDefinition.
5. Creates EmailRule if required.
6. Does not duplicate rule.
7. Does not overwrite customer-modified template unless system version strategy allows.
8. If EventDefinition missing, seeder fails or logs clear dependency error.
```

---

# 22. Business rules master

## 22.1 Template rules

```text
NTF-TPL-001 Template code unique.
NTF-TPL-002 Template name required.
NTF-TPL-003 Template status defaults ACTIVE.
NTF-TPL-004 Archived template cannot create new versions.
NTF-TPL-005 Template eventDefinitionId must exist if template is event-bound.
NTF-TPL-006 SYSTEM template cannot be edited by workspace admin unless override model exists.
NTF-TPL-007 Template archive blocked if active EmailRule depends on it unless force/admin.
```

## 22.2 Template version rules

```text
NTF-TPLV-001 Version number unique per template.
NTF-TPLV-002 DRAFT can be edited.
NTF-TPLV-003 PUBLISHED immutable.
NTF-TPLV-004 Publish validates variables.
NTF-TPLV-005 Publish rejects unknown variables.
NTF-TPLV-006 Publish rejects sensitive variable unless allowed.
NTF-TPLV-007 Published version cannot be deleted.
NTF-TPLV-008 Preview does not persist delivery.
```

## 22.3 Email rule rules

```text
NTF-RULE-001 Rule code unique.
NTF-RULE-002 EventDefinition must be ACTIVE.
NTF-RULE-003 Template must be ACTIVE.
NTF-RULE-004 Pinned template version must be PUBLISHED.
NTF-RULE-005 Recipient strategy must be supported.
NTF-RULE-006 Mandatory flag requires permission and audit.
NTF-RULE-007 Sensitive variable allowance requires permission and audit.
NTF-RULE-008 Disabled rule does not create outbox.
NTF-RULE-009 Archived rule cannot be enabled.
```

## 22.4 Email outbox rules

```text
NTF-OUT-001 Outbox status starts PENDING.
NTF-OUT-002 Dedup key unique.
NTF-OUT-003 Processor must lock before send.
NTF-OUT-004 SENT cannot be resent automatically.
NTF-OUT-005 CANCELLED cannot be processed.
NTF-OUT-006 Failed attempt increments attemptCount.
NTF-OUT-007 Max attempts leads DEAD_LETTER.
NTF-OUT-008 Retry allowed only for retryable status.
NTF-OUT-009 Outbox does not store secrets beyond rendered email content required to send.
NTF-OUT-010 Rendered email content must be access-safe and masked.
```

## 22.5 Delivery rules

```text
NTF-DEL-001 Every send attempt creates delivery record.
NTF-DEL-002 SENT records providerMessageId if available.
NTF-DEL-003 FAILED records safe failure reason.
NTF-DEL-004 Provider webhook statuses deferred unless implemented.
```

## 22.6 In-app notification rules

```text
NTF-INAPP-001 Notification belongs to one user.
NTF-INAPP-002 User can only view own notifications.
NTF-INAPP-003 Read sets readAt.
NTF-INAPP-004 Dismiss sets dismissedAt.
NTF-INAPP-005 Unread count excludes dismissed.
NTF-INAPP-006 Body preview must be safe.
NTF-INAPP-007 Action link must recheck access.
NTF-INAPP-008 Mandatory notices can be read but not hidden by preference.
```

---

# 23. Error catalog requirements

Exact naming follows project convention, but concepts must exist.

```text
NOTIFICATION_TEMPLATE_NOT_FOUND
NOTIFICATION_TEMPLATE_CODE_ALREADY_EXISTS
NOTIFICATION_TEMPLATE_ARCHIVED
NOTIFICATION_TEMPLATE_HAS_ACTIVE_RULES
NOTIFICATION_TEMPLATE_VERSION_NOT_FOUND
NOTIFICATION_TEMPLATE_VERSION_NOT_DRAFT
NOTIFICATION_TEMPLATE_VERSION_ALREADY_PUBLISHED
NOTIFICATION_TEMPLATE_VARIABLE_UNKNOWN
NOTIFICATION_TEMPLATE_VARIABLE_SENSITIVE_NOT_ALLOWED
NOTIFICATION_TEMPLATE_RENDER_FAILED

NOTIFICATION_EMAIL_RULE_NOT_FOUND
NOTIFICATION_EMAIL_RULE_CODE_ALREADY_EXISTS
NOTIFICATION_EMAIL_RULE_EVENT_INACTIVE
NOTIFICATION_EMAIL_RULE_TEMPLATE_INACTIVE
NOTIFICATION_EMAIL_RULE_VERSION_NOT_PUBLISHED
NOTIFICATION_EMAIL_RULE_RECIPIENT_STRATEGY_UNSUPPORTED
NOTIFICATION_EMAIL_RULE_MANDATORY_PERMISSION_REQUIRED
NOTIFICATION_EMAIL_RULE_SENSITIVE_PERMISSION_REQUIRED

NOTIFICATION_EMAIL_OUTBOX_NOT_FOUND
NOTIFICATION_EMAIL_OUTBOX_NOT_RETRYABLE
NOTIFICATION_EMAIL_OUTBOX_ALREADY_SENT
NOTIFICATION_EMAIL_OUTBOX_CANCELLED
NOTIFICATION_EMAIL_OUTBOX_DEDUPLICATE

NOTIFICATION_ITEM_NOT_FOUND
NOTIFICATION_ITEM_ACCESS_DENIED

NOTIFICATION_DELIVERY_PROVIDER_FAILED
```

---

# 24. Required tests

Phase 06 is incomplete without tests.

---

## 24.1 Email template tests

```text
createEmailTemplate_valid_success
createEmailTemplate_duplicateCode_conflict
createEmailTemplate_missingName_validation
updateEmailTemplate_archived_rejected
archiveEmailTemplate_withActiveRule_blockedOrDocumented
```

## 24.2 Template version tests

```text
createTemplateVersion_valid_success
updateDraftVersion_success
updatePublishedVersion_rejected
publishVersion_valid_success
publishVersion_unknownVariable_rejected
publishVersion_sensitiveVariableInSubject_rejected
publishVersion_sensitiveVariableWithoutPermission_rejected
publishVersion_sensitiveVariableWithPermission_audited
previewVersion_valid_rendersWithoutPersistingOutbox
```

## 24.3 Email rule tests

```text
createEmailRule_valid_success
createEmailRule_inactiveEvent_rejected
createEmailRule_deprecatedEvent_rejected
createEmailRule_inactiveTemplate_rejected
createEmailRule_unpublishedPinnedVersion_rejected
createEmailRule_unsupportedRecipientStrategy_rejected
createEmailRule_mandatoryWithoutPermission_rejected
createEmailRule_mandatoryWithPermission_audited
enableArchivedRule_rejected
disabledRule_doesNotCreateOutbox
```

## 24.4 Event-to-email tests

```text
businessEvent_matchingActiveRule_createsEmailOutbox
businessEvent_disabledRule_noOutbox
businessEvent_missingRequiredVariable_failsAndLogs
businessEvent_sensitiveVariable_maskedOrRejected
businessEvent_duplicate_dedupesEmailOutbox
businessEvent_multipleRecipients_createsOneOutboxPerRecipient
```

## 24.5 Email outbox processor tests

```text
emailOutboxProcessor_pending_sendsEmail
emailOutboxProcessor_success_marksSent
emailOutboxProcessor_failure_recordsDeliveryFailed
emailOutboxProcessor_failure_schedulesRetry
emailOutboxProcessor_maxAttempts_deadLetters
emailOutboxProcessor_lockPreventsDoubleSend
emailOutboxProcessor_sentCannotRetryAutomatically
manualRetry_deadLetter_success
cancelPendingOutbox_success
```

## 24.6 Email delivery tests

```text
emailDelivery_sent_hasProviderMessageId
emailDelivery_failed_hasSafeFailureMessage
emailDelivery_doesNotStoreProviderSecret
```

## 24.7 In-app notification tests

If implemented:

```text
createNotificationItem_valid_success
listNotifications_onlyOwn
unreadCount_excludesReadAndDismissed
markRead_ownNotification_success
markRead_otherUser_forbidden
dismiss_ownNotification_success
notificationDedup_sameEventRuleRecipient_noDuplicate
notificationBodyPreview_masksSensitiveData
```

If deferred:

```text
Mark all in-app tests deferred to Phase 20.
```

## 24.8 Seeder tests

```text
notificationEventSeeder_firstRun_createsEvents
notificationEventSeeder_secondRun_noDuplicates
emailTemplateSeeder_firstRun_createsTemplates
emailTemplateSeeder_secondRun_noDuplicates
emailTemplateSeeder_variablesExistInEventRegistry
emailRuleSeeder_firstRun_createsRules
emailRuleSeeder_secondRun_noDuplicates
```

## 24.9 Security/access tests

```text
templateAdminEndpoint_withoutPermission_forbidden
emailRuleAdminEndpoint_withoutPermission_forbidden
deliveryAdminEndpoint_withoutPermission_forbidden
notificationList_withoutAuth_unauthorized
notificationList_doesNotLeakOtherUsers
```

---

# 25. Manual verification checklist

Completion file must include:

```text
1. Create email template.
2. Create draft template version.
3. Preview draft version.
4. Publish version.
5. Try publishing unknown variable and confirm rejection.
6. Create email rule for ACTIVE event.
7. Try creating email rule for INACTIVE/DEPRECATED event and confirm rejection.
8. Trigger matching event and see EmailOutbox row.
9. Trigger same event again and confirm dedup behavior.
10. Run email processor and confirm EmailDelivery SENT/FAILED.
11. Force email failure and confirm retry/dead-letter.
12. Confirm sensitive variable masking rule.
13. Confirm mandatory rule requires permission.
14. If in-app implemented: list, unread count, mark read, dismiss.
15. Rerun seeders and confirm no duplicates.
```

---

# 26. Acceptance criteria

Phase 06 is accepted only if:

```text
1. Current Notification module is classified against TO-BE.
2. Email template/version/rule behavior is implemented or verified.
3. Template publishing validates variables against EventRegistry.
4. Sensitive variables are blocked/masked according to rules.
5. EmailRule requires ACTIVE EventDefinition.
6. Email outbox state machine is tested.
7. Email delivery log is tested.
8. Retry/dead-letter behavior is implemented or explicitly deferred with risk.
9. Deduplication prevents duplicate logical notification/email.
10. Mandatory notice foundation is implemented.
11. In-app notification item is implemented or explicitly deferred to Phase 20.
12. Current system email templates are seeded or verified.
13. Notification events are seeded in EventRegistry.
14. Authorization rules protect admin endpoints.
15. No raw provider secrets/tokens are logged.
16. Future notification capabilities are assigned to phases.
17. mvn compile passes.
18. mvn test passes.
19. Completion file exists and includes gap matrix.
```

Agent must not mark complete if:

```text
mvn test fails
templates can publish unknown variables
email rules can bind inactive/deprecated events
processor can double-send under retry/concurrency
sensitive variables leak into subject/bodyPreview
dedup does not work
email seeders duplicate templates/rules
future push/chat/digest is claimed implemented without real code
```

---

# 27. Required phase completion file

Agent must create:

```text
docs/phase-complete/PHASE_06_NOTIFICATION_ENGINE_TO_BE_COMPLETE.md
```

Required sections:

```text
# Phase 06 — Notification Engine TO-BE Complete

## 1. Summary
## 2. Source Inputs Reviewed
## 3. Current vs TO-BE Classification Matrix
## 4. Implemented in Current BE
## 5. Implemented / Hardened in This Phase
## 6. Seed-only Items Added
## 7. Deferred Items and Target Phase
## 8. Entity Mapping
## 9. API Changes
## 10. Template Versioning Rules
## 11. EventRegistry Integration
## 12. Recipient Strategy Matrix
## 13. Sensitive Variable Masking
## 14. Mandatory Notice Rules
## 15. Email Outbox / Delivery State Machine
## 16. Deduplication Strategy
## 17. In-app Notification Decision
## 18. Event Seeder Matrix
## 19. Email Template Seeder Matrix
## 20. Authorization Matrix
## 21. Activity / Audit Notes
## 22. Tests Added
## 23. Commands Run
## 24. Test Results
## 25. Manual Verification
## 26. Assumptions
## 27. Deviations From Prompt
## 28. Known Risks
## 29. Future Phases That Must Return to Notification
```

---

# 28. Future phases that must return to Notification

## 28.1 Phase 07 — AI Agent Platform

AI execution events may trigger notification.

Must add:

```text
AI_EXECUTION_FAILED_EMAIL optional
AI_USAGE_POLICY_BLOCKED_NOTIFICATION
AI_EVENT_CONFIG_ACTIVATED_NOTIFICATION optional
```

## 28.2 Phase 08 — Knowledge / Document Hub

Must add:

```text
DOCUMENT_PUBLISHED_NOTIFICATION
DOCUMENT_REVIEW_REQUIRED_EMAIL
DOCUMENT_EXPORTED_AUDIT_NOTIFICATION
```

## 28.3 Phase 09/10 — Project Core

Must add basic project/task notification contracts if needed:

```text
TASK_ASSIGNED_NOTIFICATION
TASK_COMPLETED_NOTIFICATION
TASK_BLOCKED_NOTIFICATION
```

Full project notifications deferred to Phase 20.

## 28.4 Phase 12–14 — Capacity/Scheduling/Gantt

Must add:

```text
RESOURCE_OVER_ALLOCATED_NOTIFICATION
TASK_DUE_DATE_AT_RISK_NOTIFICATION
SCHEDULE_RECALCULATED_NOTIFICATION
```

## 28.5 Phase 17 — Finance

Must add sensitive finance notification rules:

```text
PROJECT_MARGIN_BELOW_TARGET_EMAIL
FINANCIAL_SCENARIO_APPROVED_NOTIFICATION
```

Must respect finance permissions and masking.

## 28.6 Phase 18 — Quote

Must add:

```text
QUOTE_APPROVAL_REQUIRED_EMAIL
QUOTE_APPROVED_EMAIL
QUOTE_REJECTED_EMAIL
QUOTE_PUBLISHED_EMAIL
```

Must respect quote approval permissions and SoD.

## 28.7 Phase 19 — Baseline / Change Request

Must add:

```text
CHANGE_REQUEST_APPROVAL_REQUIRED_EMAIL
CHANGE_REQUEST_APPROVED_NOTIFICATION
BASELINE_APPROVED_NOTIFICATION
```

## 28.8 Phase 20 — Project Events + Notifications

This is the main expansion phase for project notification subscriptions.

Must implement or extend:

```text
Project notification rules
Resource follow/subscribe
Project notification preferences
Task mentions if comments exist
Escalation reminders
Project digest
In-app notification UI support if deferred
```

## 28.9 Phase 21 — AI-assisted Planning

Must add:

```text
AI_PROJECT_PLAN_SUGGESTED_NOTIFICATION
AI_PROJECT_PLAN_APPLIED_NOTIFICATION
AI_ACTION_BLOCKED_BY_IAM_NOTIFICATION
```

## 28.10 Phase 22 — Reporting / Export

Must add:

```text
REPORT_EXPORT_COMPLETED_EMAIL
REPORT_EXPORT_FAILED_EMAIL
REPORT_DIGEST_EMAIL
```

## 28.11 Phase 23 — Final Hardening

Must verify:

```text
notification storm prevention
quiet hours
mandatory notice policy
retention/deletion
provider webhook security
delivery audit
access-safe notification payloads
```

## 28.12 Phase 29 — External Collaboration Portal

Must add external-safe notifications:

```text
EXTERNAL_PORTAL_INVITATION_EMAIL
CLIENT_DECISION_REQUIRED_EMAIL
VENDOR_RESPONSE_REQUIRED_EMAIL
```

## 28.13 Phase 32 — Workflow / Automation

Must add:

```text
workflow-driven notification steps
escalation notification
approval reminder
```

## 28.14 Phase 37 — Integration / API

Must add:

```text
push provider
chat provider
email provider webhooks
webhook notification channel
```

---

# 29. Agent anti-bịa rules

The agent must not:

```text
1. Treat current email module as full Notification and Subscription future-state.
2. Claim in-app notification exists unless NotificationItem/API/tests exist.
3. Claim push/chat/digest/reminder/subscription exists without real code.
4. Send email directly inside business action transaction.
5. Publish template with unknown variables.
6. Allow email rule to bind inactive/deprecated EventDefinition.
7. Leak sensitive EventVariable in subject/bodyPreview.
8. Mark rule mandatory without audit/permission.
9. Double-send email on retry/concurrent processor.
10. Store provider API secrets in delivery logs.
11. Claim event-to-email pipeline is reliable without retry/dedup tests.
12. Seed future module templates as active before module exists unless marked seed-only.
13. Forget to list future phases that must return to Notification.
```

---

# 30. Prompt to give coding agent

```text
You are implementing Phase 06 — TO-BE Notification Engine, Email Template, In-app Notification, Delivery Log & Subscription Foundation.

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
- Current BE feature/entity/business-rule inventory
- Dynamic Work OS feature catalog
- Existing notification module code, migrations, tests
- Existing EventRegistry code
- Existing platform outbox/event publisher code
- Existing email provider/sender abstraction

Your task:
1. Compare current Notification module against this TO-BE Phase 06 spec.
2. Classify every Notification capability as CURRENTLY_IMPLEMENTED, MUST_IMPLEMENT_IN_PHASE_06, SEED_ONLY_IN_PHASE_06, DEFERRED_TO_PHASE_XX, or NOT_IN_SCOPE_FOR_BACKEND_NOW.
3. Implement only Phase 06 required items.
4. Harden EmailTemplate, EmailTemplateVersion, EmailRule, EmailOutbox, and EmailDelivery.
5. Implement or explicitly defer minimal in-app NotificationItem.
6. Validate template variables against EventRegistry.
7. Prevent inactive/deprecated events from being used by active rules.
8. Implement sensitive variable masking and mandatory notice foundation.
9. Implement or verify deduplication and retry/dead-letter behavior.
10. Add/verify current system email template seeders.
11. Add/verify Notification event definitions.
12. Add tests listed in the Phase 06 spec.
13. Run mvn compile and mvn test.
14. Create docs/phase-complete/PHASE_06_NOTIFICATION_ENGINE_TO_BE_COMPLETE.md with full gap matrix.

Do not claim push/chat/digest/subscription/reminder/escalation exists unless implemented and tested.
Do not hide deferred gaps.
```

---

# 31. Quick tracking matrix

| Capability | Current backend | Phase 06 action | Later phase |
|---|---|---|---|
| EmailTemplate | Present | Harden/test | — |
| EmailTemplateVersion | Present | Harden publish/immutability/preview | — |
| EmailRule | Present | Harden EventDefinition binding | — |
| EmailOutbox | Present | Harden retry/dedup/dead-letter | Phase 23 if partial |
| EmailDelivery | Present | Harden delivery log | Phase 37 for provider webhook |
| In-app notification | Missing/unknown | Implement minimal or defer | Phase 20 |
| Mobile push | Missing | Defer | Phase 37/mobile |
| Chat integration | Missing | Defer | Phase 37 |
| Preferences | Missing/partial | Defer/minimal | Phase 20/23 |
| Follow/subscribe | Missing | Defer | Phase 20/22 |
| Mention | Missing | Defer | Phase 30/project comments |
| Reminder | Missing | Defer | Phase 20/32 |
| Escalation | Missing | Defer | Phase 32 |
| Digest | Missing | Defer | Phase 22/20 |
| Template localization | Missing | Defer | Phase 23/39 |
| Delivery log | Present | Harden | — |
| Deduplication | Unknown | Must implement basic | Phase 20 for storm compression |
| Sensitive masking | Unknown | Must implement foundation | Finance/document phases revisit |
| Mandatory notice | Unknown | Must implement foundation | Phase 23 policy hardening |

---

# 32. Final principle

Phase 06 is not complete when "emails can be sent."

Phase 06 is complete when Scopery has a notification engine foundation that is:

```text
event-driven
template-versioned
variable-validated
recipient-aware
access-safe
deduplicated
retry-safe
auditable
extensible to future channels
honest about deferred capabilities
```

No notification should leak information the recipient cannot access.

No future module should invent its own notification pipeline.
