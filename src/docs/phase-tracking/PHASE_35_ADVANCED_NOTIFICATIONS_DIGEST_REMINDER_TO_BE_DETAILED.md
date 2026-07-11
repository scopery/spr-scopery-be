# PHASE 35 — TO-BE Advanced Notifications, Digest, Reminder, Subscription, Alert Rules & Preference Center

> Project: Scopery Backend  
> Phase: 35  
> Document type: TO-BE implementation-grade specification  
> Status: Planning / Gap-driven implementation spec  
> Roadmap group: Post-core Dynamic Work OS expansion  
> Depends on: Phase 00 — Master Backend Roadmap, Phase 01 — API Path / Security Baseline, Phase 02 — IAM Core, Phase 03 — Workspace / Organization / Team, Phase 04 — Platform Audit / Outbox / Idempotency, Phase 05 — Event Registry, Phase 06 — Notification Engine, Phase 09 — Project Core / WBS / Task, Phase 10 — Project Authorization, Phase 20 — Project Events / Notifications, Phase 22 — Reporting / Dashboard / Export, Phase 23 — Core Hardening, Phase 24 — Scope / Deliverable / Acceptance, Phase 25 — RAID / Decision Management, Phase 26 — Quality / Test / Release, Phase 27 — Document Hub / Generation, Phase 28 — Application / Requirement / Traceability, Phase 29 — External Party / Stakeholder, Phase 30 — External Portal, Phase 31 — Meetings / Collaboration, Phase 32 — Search / Productivity, Phase 33 — Custom Fields / Forms / Configuration, Phase 34 — Governance / Versioning / Permission / Audit  
> API base: `/api`  
> Primary module: `modules/notification`, `modules/alerting`, or `modules/platform/notification` depending on repository architecture  
> Related modules: `iam`, `workspace`, `project`, `task`, `scope`, `deliverable`, `requirement`, `quality`, `release`, `document`, `raid`, `decision`, `externalparty`, `clientportal`, `collaboration`, `productivity`, `configuration`, `governance`, `reporting`, `audit`, `eventregistry`, future `integration`, `mobile`, `service-support`, `semantic-index`  
> Important rule: Phase 35 improves notifications, reminders, digests, subscriptions, alert rules, and user preferences. It does **not** implement approval workflow, workflow engine, billing/payment reminders, public marketing email automation, real-time chat, or guaranteed external email/SMS/push delivery unless providers are already integrated and tested.

---

# 0. Purpose

Phase 35 upgrades Scopery from basic event notifications to a usable notification system.

Phase 06 likely introduced the basic notification engine:

```text
event → template → notification record → delivery attempt
```

But after Phase 34, Scopery has many important signals:

```text
task due soon
meeting action overdue
baseline guard blocked
client-visible content changed
sensitive field accessed
document downloaded by external user
release readiness failed
defect severity high
requirement missing test coverage
client feedback submitted
project risk became critical
ownership changed
object grant revoked
```

Phase 35 answers:

```text
Who should be notified?
When should they be notified?
Should this be instant or digest?
Should quiet hours suppress it?
Should the user be subscribed to this object/project?
Should owners/watchers/project managers receive alerts?
Should client portal users receive anything?
How do we avoid notification spam?
How do we deduplicate similar alerts?
How do reminders work without a workflow engine?
How do governance/security events surface safely?
How do users manage notification preferences?
```

Phase 35 is the **notification intelligence layer**.

It does not create approval workflow.

It does not replace permissions.

It does not guarantee external provider delivery unless provider exists.

---

# 1. Source inputs

Before coding Phase 35, the agent must read:

```text
1. Current backend codebase
2. Phase 01 API/Security baseline implementation
3. Phase 02 IAM implementation
4. Phase 03 Workspace/Organization/Team implementation
5. Phase 04 Audit/Outbox/Idempotency implementation
6. Phase 05 Event Registry implementation
7. Phase 06 Notification Engine implementation
8. Phase 09 Project/Task implementation
9. Phase 10 Project Authorization implementation
10. Phase 20 Project Events/Notifications implementation
11. Phase 22 Reporting implementation
12. Phase 23 Core Hardening completion file
13. Phase 24 Scope/Deliverable/Acceptance implementation
14. Phase 25 RAID/Decision implementation
15. Phase 26 Quality/Test/Release implementation
16. Phase 27 Document Hub implementation
17. Phase 28 Requirement/Traceability implementation
18. Phase 29 External Party implementation
19. Phase 30 External Portal implementation
20. Phase 31 Collaboration implementation
21. Phase 32 Work Inbox/Productivity implementation
22. Phase 33 Custom Configuration implementation
23. Phase 34 Governance/Versioning/Audit implementation
24. Existing Notification, NotificationTemplate, NotificationPreference, DeliveryAttempt code if any
25. Current scheduled job infrastructure if any
26. Current email/websocket/push provider integration if any
27. Current IAM seeders and EventDefinition seeders
```

The agent must inspect actual code and not assume provider support.

---

# 2. Current expected backend state

After Phase 34, Scopery likely has:

```text
Notification basics
EventDefinitions
Outbox
User/project notifications
WorkInbox
Comment mentions
Project events
Document access events
Governance audit events
Client portal events
```

Likely missing or partial:

```text
NotificationPreferenceCenter
NotificationChannelPreference
QuietHours
DigestRule
DigestRun
ReminderRule
ReminderInstance
Subscription / Watcher
AlertRule
AlertEvaluation
DeduplicationKey
NotificationSuppression
NotificationPriority
NotificationBatch
NotificationReadState
ExternalNotificationConsent
PortalNotificationPreference
NotificationAnalytics
```

Phase 35 adds these capabilities.

---

# 3. Target statement

Phase 35 must deliver:

```text
1. Unified notification preference center.
2. Channel preferences by event/category/priority.
3. Quiet hours and timezone-aware delivery policy.
4. Object/project subscriptions and watchers.
5. Digest rules and scheduled digest generation.
6. Reminder rules for due dates, overdue actions, stale items, and governance events.
7. Alert rules for important conditions.
8. Notification deduplication and suppression.
9. Notification priority/severity classification.
10. Work inbox integration.
11. External portal notification preferences.
12. Governance/security alert integration.
13. Reporting and analytics for notification health.
14. Tests, seeders, and completion report.
```

---

# 4. Boundary decisions

## 4.1 Notification is not authorization

A notification can point to an object.

Opening the object must re-check permission.

Notification text/snippet must not leak hidden data.

## 4.2 Reminder is not workflow

A reminder tells a user to act.

It does not enforce approval, state transition, escalation workflow, or SLA execution.

## 4.3 Alert is not automation

Alert rules notify/record important conditions.

They do not mutate business objects unless a separate safe domain action exists and is explicitly triggered.

## 4.4 Digest is not report export

Digest is a summarized notification bundle.

Report/export remains Phase 22.

## 4.5 External delivery depends on provider

Phase 35 can create delivery records for:

```text
IN_APP
EMAIL
WEB_PUSH
MOBILE_PUSH
SLACK
TEAMS
SMS
PORTAL
```

But only channels with real providers should be marked as delivered.

Do not claim email/SMS/push/Slack/Teams is working unless provider exists and tests pass.

---

# 5. Classification labels

Use these labels in completion file:

| Label | Meaning |
|---|---|
| `CURRENTLY_IMPLEMENTED` | Existing backend already supports it with tests. |
| `PARTIALLY_IMPLEMENTED` | Some support exists but gaps remain. |
| `MUST_IMPLEMENT_IN_PHASE_35` | Required now. |
| `MUST_HARDEN_IN_PHASE_35` | Existing notification behavior must be hardened now. |
| `SEED_ONLY_IN_PHASE_35` | Seed rules/templates/categories only. |
| `DEFERRED_TO_PHASE_XX` | Later phase returns. |
| `DEFERRED_TO_POST_23_BACKLOG` | Later backlog item. |
| `NOT_IN_SCOPE_FOR_PHASE_35` | Explicitly not in this phase. |

---

# 6. Required capabilities

---

## 6.1 PREF-001 — NotificationPreferenceProfile

Classification: `MUST_IMPLEMENT_IN_PHASE_35`

Purpose:

```text
Store user-level notification preferences per workspace.
```

Rules:

```text
1. Preference profile belongs to a user and workspace.
2. Profile includes timezone, default channels, digest preference, quiet hours.
3. Defaults generated when missing.
4. Admin can set workspace default but user can override where allowed.
5. Preference does not grant access to events/objects.
```

Default modes:

```text
ALL
IMPORTANT_ONLY
DIGEST_ONLY
MUTED
CUSTOM
```

---

## 6.2 PREF-002 — NotificationCategoryPreference

Classification: `MUST_IMPLEMENT_IN_PHASE_35`

Purpose:

```text
Allow users to configure notification behavior by category.
```

Categories:

```text
TASK
PROJECT
MENTION
COMMENT
DOCUMENT
REQUIREMENT
DELIVERABLE
RAID
DECISION
QUALITY
RELEASE
MEETING
CLIENT_PORTAL
GOVERNANCE
SECURITY
SYSTEM
REPORT
CUSTOM_FIELD
```

Rules:

```text
1. Category preference can override profile default.
2. Channel, priority threshold, instant/digest mode configurable.
3. Critical security/governance notifications may be non-mutable by policy.
4. Preferences apply only to future notifications.
```

---

## 6.3 PREF-003 — ChannelPreference

Classification: `MUST_IMPLEMENT_IN_PHASE_35`

Channels:

```text
IN_APP
EMAIL
WEB_PUSH
MOBILE_PUSH
PORTAL
SLACK
TEAMS
SMS
WEBHOOK future
```

Rules:

```text
1. Channel enabled only if provider supported.
2. Unsupported provider channel can be configured but delivery disabled/queued-disabled by policy.
3. User can disable optional channels.
4. Security-critical in-app notifications remain enabled unless admin policy allows mute.
5. External portal channel only for external portal users.
```

---

## 6.4 PREF-004 — QuietHours

Classification: `MUST_IMPLEMENT_IN_PHASE_35`

Purpose:

```text
Avoid interrupting users outside preferred hours.
```

Rules:

```text
1. Quiet hours use user's timezone.
2. Critical notifications can bypass quiet hours by policy.
3. Non-critical notifications are delayed or included in digest.
4. Quiet hours must not delay in-app notification record creation if policy says create now.
5. Quiet hour evaluation deterministic and tested.
```

---

## 6.5 SUB-001 — Subscription / Watcher

Classification: `MUST_IMPLEMENT_IN_PHASE_35`

Purpose:

```text
Let users watch projects/objects for updates.
```

Watchable targets:

```text
PROJECT
TASK
DELIVERABLE
REQUIREMENT
DOCUMENT
RAID_ITEM
DECISION_RECORD
DEFECT
RELEASE_PACKAGE
MEETING
COMMENT_THREAD
CLIENT_FEEDBACK
EXTERNAL_ORGANIZATION
```

Subscription levels:

```text
ALL_ACTIVITY
IMPORTANT_ACTIVITY
COMMENTS_ONLY
STATUS_CHANGES
MENTIONS_ONLY
MUTED
```

Rules:

```text
1. Subscription belongs to user/principal.
2. User must have access to target when subscribing.
3. Subscription does not grant access.
4. If access is lost, notifications stop or are masked.
5. Owner/assignee can be auto-subscribed by policy.
6. Duplicate active subscription prevented.
```

---

## 6.6 SUB-002 — Auto-subscription policy

Classification: `MUST_IMPLEMENT_IN_PHASE_35`

Auto-subscribe when:

```text
user creates object
user is assigned owner
user is assigned task
user is mentioned
user comments on thread
user becomes project manager
user is document owner
user is requirement owner
user is external client reviewer
```

Rules:

```text
1. Auto-subscription follows workspace policy.
2. User can mute optional auto subscriptions.
3. Mandatory system/security subscriptions cannot be muted unless policy.
4. Auto-subscription audited if sensitive.
```

---

## 6.7 DIG-001 — DigestRule

Classification: `MUST_IMPLEMENT_IN_PHASE_35`

Purpose:

```text
Define how notifications are grouped into digests.
```

Digest frequencies:

```text
DAILY
WEEKLY
CUSTOM
IMMEDIATE_DIGEST_WINDOW
```

Digest grouping:

```text
BY_PROJECT
BY_CATEGORY
BY_PRIORITY
BY_DUE_DATE
BY_OBJECT_TYPE
```

Rules:

```text
1. Digest rule belongs to user/workspace or workspace default.
2. Digest respects permissions at generation time.
3. Digest content must not leak hidden object details.
4. Digest should exclude notifications already read/dismissed if policy.
5. Critical notifications can bypass digest.
```

---

## 6.8 DIG-002 — DigestRun

Classification: `MUST_IMPLEMENT_IN_PHASE_35`

Purpose:

```text
Record digest generation and delivery.
```

Rules:

```text
1. DigestRun references recipient and included notification IDs.
2. Digest content generated from visible/masked data.
3. Failed delivery can be retried if provider supports.
4. DigestRun audit retained.
5. DigestRun is not report export.
```

---

## 6.9 REM-001 — ReminderRule

Classification: `MUST_IMPLEMENT_IN_PHASE_35`

Purpose:

```text
Create reminders for due dates, overdue items, stale objects, and important follow-ups.
```

Reminder sources:

```text
TASK_DUE_DATE
TASK_OVERDUE
MEETING_ACTION_DUE_DATE
MEETING_ACTION_OVERDUE
DOCUMENT_REVIEW_DUE
REQUIREMENT_REVIEW_STALE
RELEASE_READINESS_DUE
TEST_RUN_DUE
DEFECT_STALE
RAID_REVIEW_DUE
CLIENT_FEEDBACK_STALE
CLIENT_REVIEW_REQUEST_DUE
OBJECT_OWNER_MISSING
GOVERNANCE_LOCK_STALE
```

Rules:

```text
1. ReminderRule is declarative.
2. ReminderRule creates ReminderInstance or notification when condition matches.
3. Reminder does not mutate source object.
4. Duplicate reminders deduped by source/action/time window.
5. Reminder respects quiet hours unless critical.
```

---

## 6.10 REM-002 — ReminderInstance

Classification: `MUST_IMPLEMENT_IN_PHASE_35`

Purpose:

```text
Track generated reminder instances.
```

Status:

```text
PENDING
SENT
SNOOZED
DISMISSED
CANCELLED
EXPIRED
```

Rules:

```text
1. ReminderInstance belongs to source object and recipient.
2. Snooze applies only to recipient.
3. Dismiss does not mutate source object.
4. Source completion cancels open reminders.
5. Reminder can appear in WorkInbox.
```

---

## 6.11 ALERT-001 — AlertRule

Classification: `MUST_IMPLEMENT_IN_PHASE_35`

Purpose:

```text
Notify important conditions that may need attention.
```

Alert categories:

```text
PROJECT_HEALTH
DEADLINE_RISK
BUDGET_RISK
SCOPE_CHANGE
QUALITY_RISK
RELEASE_RISK
SECURITY
GOVERNANCE
CLIENT_ACTIVITY
DOCUMENT_ACCESS
DATA_QUALITY
SYSTEM
```

Example alert rules:

```text
critical risk created
high severity defect opened
release readiness failed
baseline guard blocked
sensitive document downloaded externally
client-visible content changed
task overdue by N days
requirement without test coverage
deliverable overdue
client feedback untriaged for N days
object has no owner
access grant created for sensitive object
```

Rules:

```text
1. AlertRule is declarative and whitelisted.
2. AlertRule does not perform business mutation.
3. Alert priority calculated.
4. Alert notifications deduplicated.
5. Security/governance alerts may bypass quiet hours.
```

---

## 6.12 ALERT-002 — AlertEvent

Classification: `MUST_IMPLEMENT_IN_PHASE_35`

Purpose:

```text
Record alert evaluation result.
```

Rules:

```text
1. AlertEvent stores rule, source target, severity, recipients.
2. AlertEvent can create notification(s).
3. AlertEvent can be acknowledged/dismissed by recipient.
4. Acknowledgement does not mutate source object.
5. AlertEvent audit retained.
```

---

## 6.13 DEDUP-001 — Notification deduplication

Classification: `MUST_IMPLEMENT_IN_PHASE_35`

Purpose:

```text
Prevent notification spam.
```

Dedup dimensions:

```text
recipient
event type
target type/id
category
dedup window
priority
```

Rules:

```text
1. Duplicate notifications within window merged or suppressed.
2. Suppression recorded.
3. Critical events may bypass dedup by policy.
4. Digest grouping uses deduped notifications.
```

---

## 6.14 SUP-001 — Notification suppression

Classification: `MUST_IMPLEMENT_IN_PHASE_35`

Purpose:

```text
Avoid sending notifications that should not be delivered.
```

Suppression reasons:

```text
USER_MUTED_CATEGORY
USER_MUTED_TARGET
QUIET_HOURS_DELAYED
DUPLICATE
NO_ACCESS_TO_TARGET
CHANNEL_DISABLED
PROVIDER_UNAVAILABLE
LOW_PRIORITY_DIGESTED
OBJECT_ARCHIVED
POLICY_SUPPRESSED
```

Rules:

```text
1. Suppression does not delete original event.
2. Suppression is auditable.
3. Suppressed notification may appear in digest if policy says.
4. No-access suppression must not leak target details.
```

---

## 6.15 PRIO-001 — Notification priority/severity

Classification: `MUST_IMPLEMENT_IN_PHASE_35`

Priorities:

```text
LOW
NORMAL
HIGH
URGENT
CRITICAL
```

Rules:

```text
1. Priority derived from event type, target severity, policy, and user context.
2. Critical notifications may bypass quiet hours/digest.
3. Priority visible in notification payload.
4. Priority does not grant access.
```

---

## 6.16 READ-001 — Read state, archive, dismiss

Classification: `MUST_IMPLEMENT_IN_PHASE_35`

Rules:

```text
1. Notification read state is per recipient.
2. Dismiss/archive is per recipient.
3. Mark read does not mutate source object.
4. Bulk read/dismiss supported.
5. Read state integrates with WorkInbox counts.
```

---

## 6.17 EXT-001 — External portal notification preferences

Classification: `MUST_IMPLEMENT_IN_PHASE_35` if Phase 30 exists.

Purpose:

```text
Let external portal users manage allowed portal notifications.
```

Portal categories:

```text
CLIENT_REVIEW_REQUEST
CLIENT_REVIEW_DECISION
CLIENT_FEEDBACK_RESPONSE
DOCUMENT_SHARED
DELIVERABLE_UPDATE
REQUIREMENT_UPDATE
UAT_ASSIGNMENT
PORTAL_SECURITY
```

Rules:

```text
1. Portal user can manage optional preferences.
2. Portal notifications only for active project grants.
3. External notification content must be client-safe.
4. Do-not-contact and communication preferences respected.
5. External notification does not reveal internal notes/cost/margin.
```

---

## 6.18 GOV-001 — Governance/security alert integration

Classification: `MUST_IMPLEMENT_IN_PHASE_35` if Phase 34 exists.

Governance events to notify:

```text
object owner assigned/transferred
object grant created/revoked
sensitive object accessed/exported
client-visible content changed
baseline guard blocked
object finalized/unfinalized
restore completed
governance policy changed
```

Rules:

```text
1. Sensitive notifications masked.
2. Recipients determined by owner/project manager/admin policy.
3. Security-critical alerts can bypass quiet hours.
4. Notification does not include raw sensitive values.
```

---

## 6.19 INB-001 — WorkInbox integration

Classification: `MUST_IMPLEMENT_IN_PHASE_35` if Phase 32 exists.

Rules:

```text
1. Important notifications create or update WorkInboxItem.
2. ReminderInstance can create WorkInboxItem.
3. AlertEvent can create WorkInboxItem if action required.
4. Inbox status and notification status remain separate but linked.
5. Dismiss notification does not always dismiss inbox item unless policy.
```

---

## 6.20 AI-001 — AI-assisted notification summaries

Classification: `SEED_ONLY_IN_PHASE_35` or `MUST_IMPLEMENT_IN_PHASE_35` if Phase 21 exists.

AI can help with:

```text
daily digest summary
alert explanation
notification noise analysis
suggest preference changes
summarize overdue reminders
summarize governance/security activity
```

Rules:

```text
1. AI sees only notifications/targets actor can access.
2. AI summary cannot reveal hidden fields.
3. AI cannot change preferences automatically.
4. AI cannot send external messages automatically.
5. AI output is proposal/explanation only.
```

---

# 7. Entity model TO-BE

---

## 7.1 NotificationPreferenceProfile — `notification_preference_profile`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
principal_type VARCHAR(50) NOT NULL
user_id UUID NULL
external_portal_account_id UUID NULL
timezone VARCHAR(100) NULL
default_mode VARCHAR(50) NOT NULL
default_channels_json JSONB NULL
digest_enabled BOOLEAN NOT NULL DEFAULT false
digest_frequency VARCHAR(50) NULL
quiet_hours_enabled BOOLEAN NOT NULL DEFAULT false
quiet_hours_config_json JSONB NULL
created_at / created_by
updated_at / updated_by
version INT
```

Constraint:

```text
unique workspace_id + principal_type + principal_id
```

---

## 7.2 NotificationCategoryPreference — `notification_category_preference`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
preference_profile_id UUID NOT NULL
category VARCHAR(100) NOT NULL
mode VARCHAR(50) NOT NULL
priority_threshold VARCHAR(50) NULL
channels_json JSONB NULL
digest_allowed BOOLEAN NOT NULL DEFAULT true
muted BOOLEAN NOT NULL DEFAULT false
created_at / updated_at
version INT
```

Constraint:

```text
unique preference_profile_id + category
```

---

## 7.3 NotificationChannelPreference — `notification_channel_preference`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
preference_profile_id UUID NOT NULL
channel VARCHAR(50) NOT NULL
enabled BOOLEAN NOT NULL DEFAULT true
verified BOOLEAN NOT NULL DEFAULT false
destination_ref VARCHAR(500) NULL
provider_key VARCHAR(100) NULL
created_at / updated_at
version INT
```

---

## 7.4 NotificationSubscription — `notification_subscription`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
project_id UUID NULL
principal_type VARCHAR(50) NOT NULL
user_id UUID NULL
external_portal_account_id UUID NULL
target_type VARCHAR(100) NOT NULL
target_id UUID NOT NULL
subscription_level VARCHAR(50) NOT NULL
status VARCHAR(50) NOT NULL
auto_subscribed BOOLEAN NOT NULL DEFAULT false
created_at / created_by
updated_at / updated_by
archived_at / archived_by
version INT
```

Status:

```text
ACTIVE
MUTED
ARCHIVED
```

Constraint:

```text
unique active principal_type + principal_id + target_type + target_id
```

---

## 7.5 DigestRule — `notification_digest_rule`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
preference_profile_id UUID NULL
scope VARCHAR(50) NOT NULL
frequency VARCHAR(50) NOT NULL
schedule_config_json JSONB NOT NULL
grouping_json JSONB NULL
category_filter_json JSONB NULL
priority_threshold VARCHAR(50) NULL
enabled BOOLEAN NOT NULL DEFAULT true
created_at / created_by
updated_at / updated_by
version INT
```

Scope:

```text
USER
WORKSPACE_DEFAULT
PORTAL_USER
```

---

## 7.6 DigestRun — `notification_digest_run`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
principal_type VARCHAR(50) NOT NULL
user_id UUID NULL
external_portal_account_id UUID NULL
digest_rule_id UUID NULL
status VARCHAR(50) NOT NULL
period_start TIMESTAMP NOT NULL
period_end TIMESTAMP NOT NULL
notification_ids_json JSONB NULL
summary_json JSONB NULL
delivery_channel VARCHAR(50) NULL
delivery_status VARCHAR(50) NULL
created_at TIMESTAMP NOT NULL
sent_at TIMESTAMP NULL
failure_code VARCHAR(150) NULL
failure_message TEXT NULL
trace_id VARCHAR(100) NULL
version INT
```

Status:

```text
CREATED
SENT
FAILED
SKIPPED
```

---

## 7.7 ReminderRule — `notification_reminder_rule`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
rule_code VARCHAR(150) NOT NULL
name VARCHAR(255) NOT NULL
description TEXT NULL
source_type VARCHAR(100) NOT NULL
condition_json JSONB NOT NULL
recipient_rule_json JSONB NOT NULL
schedule_json JSONB NULL
priority VARCHAR(50) NOT NULL
enabled BOOLEAN NOT NULL DEFAULT true
created_at / created_by
updated_at / updated_by
archived_at / archived_by
version INT
```

Constraint:

```text
unique workspace_id + rule_code
```

---

## 7.8 ReminderInstance — `notification_reminder_instance`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
project_id UUID NULL
reminder_rule_id UUID NOT NULL
source_type VARCHAR(100) NOT NULL
source_id UUID NOT NULL
principal_type VARCHAR(50) NOT NULL
user_id UUID NULL
external_portal_account_id UUID NULL
status VARCHAR(50) NOT NULL
due_at TIMESTAMP NULL
remind_at TIMESTAMP NOT NULL
sent_at TIMESTAMP NULL
snoozed_until TIMESTAMP NULL
dismissed_at TIMESTAMP NULL
notification_id UUID NULL
dedup_key VARCHAR(300) NULL
created_at TIMESTAMP NOT NULL
updated_at TIMESTAMP NULL
trace_id VARCHAR(100) NULL
version INT
```

Constraint suggestion:

```text
unique active dedup_key
```

---

## 7.9 AlertRule — `notification_alert_rule`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
rule_code VARCHAR(150) NOT NULL
name VARCHAR(255) NOT NULL
description TEXT NULL
category VARCHAR(100) NOT NULL
condition_json JSONB NOT NULL
recipient_rule_json JSONB NOT NULL
severity VARCHAR(50) NOT NULL
dedup_window_minutes INT NULL
bypass_quiet_hours BOOLEAN NOT NULL DEFAULT false
enabled BOOLEAN NOT NULL DEFAULT true
created_at / created_by
updated_at / updated_by
archived_at / archived_by
version INT
```

Constraint:

```text
unique workspace_id + rule_code
```

---

## 7.10 AlertEvent — `notification_alert_event`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
project_id UUID NULL
alert_rule_id UUID NOT NULL
source_type VARCHAR(100) NOT NULL
source_id UUID NOT NULL
severity VARCHAR(50) NOT NULL
status VARCHAR(50) NOT NULL
message_snapshot TEXT NULL
recipient_summary_json JSONB NULL
notification_ids_json JSONB NULL
dedup_key VARCHAR(300) NULL
created_at TIMESTAMP NOT NULL
acknowledged_at TIMESTAMP NULL
acknowledged_by UUID NULL
dismissed_at TIMESTAMP NULL
dismissed_by UUID NULL
trace_id VARCHAR(100) NULL
version INT
```

Status:

```text
OPEN
ACKNOWLEDGED
DISMISSED
CLOSED
SUPPRESSED
```

---

## 7.11 NotificationSuppression — `notification_suppression`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
principal_type VARCHAR(50) NOT NULL
user_id UUID NULL
external_portal_account_id UUID NULL
event_type VARCHAR(150) NULL
target_type VARCHAR(100) NULL
target_id UUID NULL
category VARCHAR(100) NULL
suppression_reason VARCHAR(100) NOT NULL
dedup_key VARCHAR(300) NULL
suppressed_at TIMESTAMP NOT NULL
details_json JSONB NULL
trace_id VARCHAR(100) NULL
version INT
```

---

## 7.12 NotificationDeliveryPlan — optional `notification_delivery_plan`

If existing Notification entity already covers this, do not duplicate.

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
notification_id UUID NOT NULL
recipient_principal_type VARCHAR(50) NOT NULL
recipient_user_id UUID NULL
recipient_external_portal_account_id UUID NULL
channel VARCHAR(50) NOT NULL
delivery_timing VARCHAR(50) NOT NULL
scheduled_at TIMESTAMP NULL
status VARCHAR(50) NOT NULL
suppression_id UUID NULL
created_at TIMESTAMP NOT NULL
updated_at TIMESTAMP NULL
version INT
```

Delivery timing:

```text
IMMEDIATE
DELAYED_QUIET_HOURS
DIGEST
SUPPRESSED
```

---

# 8. API TO-BE list

All APIs use `/api`.

---

## 8.1 Preference profile APIs

```text
GET /api/workspaces/{workspaceId}/notifications/preferences/me
PUT /api/workspaces/{workspaceId}/notifications/preferences/me

GET /api/workspaces/{workspaceId}/notifications/preferences/me/categories
PUT /api/workspaces/{workspaceId}/notifications/preferences/me/categories/{category}

GET /api/workspaces/{workspaceId}/notifications/preferences/me/channels
PUT /api/workspaces/{workspaceId}/notifications/preferences/me/channels/{channel}

GET /api/workspaces/{workspaceId}/notifications/preferences/me/quiet-hours
PUT /api/workspaces/{workspaceId}/notifications/preferences/me/quiet-hours
```

Admin/default:

```text
GET /api/workspaces/{workspaceId}/notifications/preferences/defaults
PUT /api/workspaces/{workspaceId}/notifications/preferences/defaults
```

---

## 8.2 Subscription APIs

```text
POST   /api/workspaces/{workspaceId}/notifications/subscriptions
GET    /api/workspaces/{workspaceId}/notifications/subscriptions
GET    /api/workspaces/{workspaceId}/notifications/subscriptions/by-target?targetType=PROJECT&targetId=...
PATCH  /api/workspaces/{workspaceId}/notifications/subscriptions/{subscriptionId}
DELETE /api/workspaces/{workspaceId}/notifications/subscriptions/{subscriptionId}
```

Convenience:

```text
POST /api/projects/{projectId}/watch
POST /api/projects/{projectId}/unwatch
POST /api/projects/{projectId}/tasks/{taskId}/watch
POST /api/projects/{projectId}/tasks/{taskId}/unwatch
```

---

## 8.3 Notification center APIs

If not already complete in Phase 06:

```text
GET  /api/workspaces/{workspaceId}/notifications
GET  /api/workspaces/{workspaceId}/notifications/counts
POST /api/workspaces/{workspaceId}/notifications/{notificationId}/mark-read
POST /api/workspaces/{workspaceId}/notifications/{notificationId}/mark-unread
POST /api/workspaces/{workspaceId}/notifications/{notificationId}/dismiss
POST /api/workspaces/{workspaceId}/notifications/bulk/mark-read
POST /api/workspaces/{workspaceId}/notifications/bulk/dismiss
```

---

## 8.4 Digest APIs

```text
POST  /api/workspaces/{workspaceId}/notifications/digest-rules
GET   /api/workspaces/{workspaceId}/notifications/digest-rules
GET   /api/workspaces/{workspaceId}/notifications/digest-rules/{digestRuleId}
PUT   /api/workspaces/{workspaceId}/notifications/digest-rules/{digestRuleId}
PATCH /api/workspaces/{workspaceId}/notifications/digest-rules/{digestRuleId}/disable

POST /api/workspaces/{workspaceId}/notifications/digests/run-now
GET  /api/workspaces/{workspaceId}/notifications/digests/runs
GET  /api/workspaces/{workspaceId}/notifications/digests/runs/{digestRunId}
```

---

## 8.5 Reminder APIs

```text
POST  /api/workspaces/{workspaceId}/notifications/reminder-rules
GET   /api/workspaces/{workspaceId}/notifications/reminder-rules
GET   /api/workspaces/{workspaceId}/notifications/reminder-rules/{reminderRuleId}
PUT   /api/workspaces/{workspaceId}/notifications/reminder-rules/{reminderRuleId}
PATCH /api/workspaces/{workspaceId}/notifications/reminder-rules/{reminderRuleId}/disable

GET  /api/workspaces/{workspaceId}/notifications/reminders
POST /api/workspaces/{workspaceId}/notifications/reminders/{reminderInstanceId}/snooze
POST /api/workspaces/{workspaceId}/notifications/reminders/{reminderInstanceId}/dismiss
POST /api/workspaces/{workspaceId}/notifications/reminders/evaluate-now
```

---

## 8.6 Alert APIs

```text
POST  /api/workspaces/{workspaceId}/notifications/alert-rules
GET   /api/workspaces/{workspaceId}/notifications/alert-rules
GET   /api/workspaces/{workspaceId}/notifications/alert-rules/{alertRuleId}
PUT   /api/workspaces/{workspaceId}/notifications/alert-rules/{alertRuleId}
PATCH /api/workspaces/{workspaceId}/notifications/alert-rules/{alertRuleId}/disable

GET  /api/workspaces/{workspaceId}/notifications/alerts
GET  /api/workspaces/{workspaceId}/notifications/alerts/{alertEventId}
POST /api/workspaces/{workspaceId}/notifications/alerts/{alertEventId}/acknowledge
POST /api/workspaces/{workspaceId}/notifications/alerts/{alertEventId}/dismiss
POST /api/workspaces/{workspaceId}/notifications/alerts/evaluate-now
```

---

## 8.7 Suppression/delivery diagnostics APIs

```text
GET /api/workspaces/{workspaceId}/notifications/suppressions
GET /api/workspaces/{workspaceId}/notifications/delivery-plans
GET /api/workspaces/{workspaceId}/notifications/delivery-attempts
POST /api/workspaces/{workspaceId}/notifications/delivery-attempts/{attemptId}/retry
```

Only implement retry if delivery provider exists.

---

## 8.8 Portal notification APIs

If Phase 30 exists:

```text
GET /api/portal/notifications
GET /api/portal/notifications/counts
POST /api/portal/notifications/{notificationId}/mark-read
POST /api/portal/notifications/{notificationId}/dismiss

GET /api/portal/notifications/preferences
PUT /api/portal/notifications/preferences

GET /api/portal/notifications/reminders
POST /api/portal/notifications/reminders/{reminderInstanceId}/snooze
POST /api/portal/notifications/reminders/{reminderInstanceId}/dismiss
```

---

## 8.9 Reports APIs

```text
GET /api/workspaces/{workspaceId}/reports/notifications/summary
GET /api/workspaces/{workspaceId}/reports/notifications/delivery-health
GET /api/workspaces/{workspaceId}/reports/notifications/digests
GET /api/workspaces/{workspaceId}/reports/notifications/reminders
GET /api/workspaces/{workspaceId}/reports/notifications/alerts
GET /api/workspaces/{workspaceId}/reports/notifications/noise
GET /api/workspaces/{workspaceId}/reports/notifications/suppressions
```

---

# 9. Authorization requirements

Required authorities:

```text
NOTIFICATION_VIEW
NOTIFICATION_UPDATE
NOTIFICATION_BULK_UPDATE

NOTIFICATION_PREFERENCE_VIEW
NOTIFICATION_PREFERENCE_UPDATE
NOTIFICATION_DEFAULT_PREFERENCE_VIEW
NOTIFICATION_DEFAULT_PREFERENCE_UPDATE

NOTIFICATION_SUBSCRIPTION_VIEW
NOTIFICATION_SUBSCRIPTION_CREATE
NOTIFICATION_SUBSCRIPTION_UPDATE
NOTIFICATION_SUBSCRIPTION_DELETE

DIGEST_RULE_VIEW
DIGEST_RULE_CREATE
DIGEST_RULE_UPDATE
DIGEST_RULE_DISABLE
DIGEST_RUN_VIEW
DIGEST_RUN_EXECUTE

REMINDER_RULE_VIEW
REMINDER_RULE_CREATE
REMINDER_RULE_UPDATE
REMINDER_RULE_DISABLE
REMINDER_INSTANCE_VIEW
REMINDER_INSTANCE_UPDATE
REMINDER_EVALUATE

ALERT_RULE_VIEW
ALERT_RULE_CREATE
ALERT_RULE_UPDATE
ALERT_RULE_DISABLE
ALERT_EVENT_VIEW
ALERT_EVENT_ACKNOWLEDGE
ALERT_EVENT_DISMISS
ALERT_EVALUATE

NOTIFICATION_SUPPRESSION_VIEW
NOTIFICATION_DELIVERY_DIAGNOSTIC_VIEW
NOTIFICATION_DELIVERY_RETRY

NOTIFICATION_REPORT_VIEW
```

Portal capabilities:

```text
PORTAL_NOTIFICATION_VIEW
PORTAL_NOTIFICATION_UPDATE
PORTAL_NOTIFICATION_PREFERENCE_UPDATE
PORTAL_REMINDER_VIEW
PORTAL_REMINDER_UPDATE
```

Rules:

```text
1. Notification access is recipient-based.
2. Admin reports require admin/report permission.
3. Opening notification target re-checks target permission.
4. Alert rule management requires workspace admin/config permission.
5. Reminder/digest rule management requires notification admin/config permission.
6. External portal APIs use portal principal and active grant.
```

---

# 10. Lifecycle rules

## 10.1 Notification lifecycle

```text
CREATED → DELIVERED
CREATED → SUPPRESSED
DELIVERED → READ
DELIVERED/READ → DISMISSED
DELIVERED/READ/DISMISSED → ARCHIVED
```

Rules:

```text
1. Read/dismiss is per recipient.
2. Suppressed notification can still be recorded for diagnostics if policy.
3. Notification target access re-checked on open.
```

## 10.2 Subscription lifecycle

```text
ACTIVE → MUTED
MUTED → ACTIVE
ACTIVE/MUTED → ARCHIVED
```

## 10.3 Reminder lifecycle

```text
PENDING → SENT
PENDING/SENT → SNOOZED
PENDING/SENT/SNOOZED → DISMISSED
PENDING/SENT/SNOOZED → CANCELLED
PENDING/SENT/SNOOZED → EXPIRED
```

## 10.4 Alert lifecycle

```text
OPEN → ACKNOWLEDGED
OPEN/ACKNOWLEDGED → DISMISSED
OPEN/ACKNOWLEDGED → CLOSED
OPEN → SUPPRESSED
```

## 10.5 Digest lifecycle

```text
CREATED → SENT
CREATED → FAILED
CREATED → SKIPPED
```

---

# 11. Recipient resolution strategy

Notification recipients can be resolved from:

```text
target owner
object watcher/subscriber
assignee
project manager
project team role
workspace admin
document owner
requirement owner
deliverable owner
release owner
RAID owner
meeting participant
comment mentioned user
external portal reviewer
approval authority display record without approval engine
governance policy recipients
```

Rules:

```text
1. Recipient must have target access unless notification is access/security admin alert.
2. Recipient list deduplicated.
3. External recipients require portal grant and client-safe target.
4. Do-not-contact and external communication preferences respected.
5. Recipient resolution errors logged but should not break source transaction unless policy.
```

---

# 12. Deduplication strategy

Dedup key format recommendation:

```text
workspaceId + recipientPrincipal + eventType + targetType + targetId + category + windowBucket
```

Examples:

```text
same task overdue reminder within 24h
same user mentioned multiple times in same comment thread
same baseline guard blocked repeatedly within 1h
same sensitive document downloaded multiple times by same user within 15m
```

Rules:

```text
1. Dedup should preserve highest priority.
2. Dedup count stored.
3. Merged notification text should avoid leaking sensitive details.
4. Critical security events may bypass dedup.
```

---

# 13. Quiet hours strategy

Quiet hour evaluation:

```text
1. Resolve user timezone.
2. Check local time against quiet window.
3. If quiet:
   - CRITICAL with bypass -> deliver now
   - non-critical -> delay or digest
4. Store suppression/delay reason.
```

Rules:

```text
1. Overnight windows supported, e.g. 22:00–08:00.
2. Weekends optional.
3. Missing timezone defaults to workspace timezone or UTC.
4. Tests must cover timezone boundaries.
```

---

# 14. Digest strategy

Digest generation pipeline:

```text
1. Select eligible notifications/reminders.
2. Re-check recipient access.
3. Apply preferences.
4. Group by rule.
5. Mask sensitive fields.
6. Generate summary payload.
7. Create DigestRun.
8. Deliver through selected channel if provider exists.
```

Rules:

```text
1. Digest does not include hidden target content.
2. Digest can include counts for inaccessible items only if safe.
3. Digest can link to notification center.
4. Digest delivery failure does not delete notifications.
```

---

# 15. Reminder strategy

Reminder evaluation sources:

```text
due date
overdue status
stale unchanged date
missing owner
missing coverage
failed readiness check
pending client feedback triage
upcoming release date
governance lock age
```

Rules:

```text
1. Evaluation job idempotent.
2. Source object completion cancels reminders.
3. Snooze/dismiss per recipient.
4. Reminder does not mutate source object.
5. Reminder can create WorkInboxItem.
```

---

# 16. Alert strategy

Alert evaluation sources:

```text
event-driven from outbox
scheduled condition scan
manual evaluate-now
```

Rules:

```text
1. Event-driven preferred for near-real-time.
2. Scheduled scans handle stale/overdue/missing owner conditions.
3. Manual evaluation admin-only.
4. Alert rule conditions must be declarative.
5. Alert rules never execute arbitrary code.
```

---

# 17. Module integration rules

## 17.1 Task / project integration

Notifications:

```text
task assigned
task due soon
task overdue
task status changed
project owner changed
project health changed
```

Rules:

```text
Task reminders do not change task status.
```

## 17.2 Document integration

Notifications:

```text
document shared
document version created
document downloaded
restricted document accessed
document current version changed
```

Rules:

```text
Restricted document notifications must not expose document content.
```

## 17.3 Requirement / traceability integration

Notifications:

```text
requirement assigned
requirement version changed
requirement missing test coverage
traceability gap detected
requirement client-visible changed
```

## 17.4 Quality / release integration

Notifications:

```text
defect assigned
critical defect created
test run failed
release readiness failed
release package finalized/released
deployment record created
```

## 17.5 RAID / decision integration

Notifications:

```text
critical risk created
issue escalated
dependency blocked
decision finalized
decision changed
```

No approval workflow implied.

## 17.6 Collaboration integration

Notifications:

```text
meeting scheduled/changed
meeting action assigned
meeting action overdue
comment created
mention created
minutes finalized
```

## 17.7 Client portal integration

Notifications:

```text
client review request sent
client feedback submitted
external document downloaded
UAT result submitted
portal access grant changed
```

External notifications are portal-safe only.

## 17.8 Governance integration

Notifications:

```text
owner transferred
access grant created/revoked
object locked/finalized
restore completed
baseline guard blocked
sensitive access event
client-visible change
```

---

# 18. Event Registry integration

Recommended source system:

```text
SCOPERY_ADVANCED_NOTIFICATIONS
```

Required events:

```text
NOTIFICATION_PREFERENCE_PROFILE_CREATED
NOTIFICATION_PREFERENCE_PROFILE_UPDATED
NOTIFICATION_CATEGORY_PREFERENCE_UPDATED
NOTIFICATION_CHANNEL_PREFERENCE_UPDATED
QUIET_HOURS_UPDATED

NOTIFICATION_SUBSCRIPTION_CREATED
NOTIFICATION_SUBSCRIPTION_UPDATED
NOTIFICATION_SUBSCRIPTION_MUTED
NOTIFICATION_SUBSCRIPTION_REMOVED
AUTO_SUBSCRIPTION_CREATED

DIGEST_RULE_CREATED
DIGEST_RULE_UPDATED
DIGEST_RULE_DISABLED
DIGEST_RUN_CREATED
DIGEST_RUN_SENT
DIGEST_RUN_FAILED
DIGEST_RUN_SKIPPED

REMINDER_RULE_CREATED
REMINDER_RULE_UPDATED
REMINDER_RULE_DISABLED
REMINDER_INSTANCE_CREATED
REMINDER_INSTANCE_SENT
REMINDER_INSTANCE_SNOOZED
REMINDER_INSTANCE_DISMISSED
REMINDER_INSTANCE_CANCELLED

ALERT_RULE_CREATED
ALERT_RULE_UPDATED
ALERT_RULE_DISABLED
ALERT_EVENT_CREATED
ALERT_EVENT_ACKNOWLEDGED
ALERT_EVENT_DISMISSED
ALERT_EVENT_SUPPRESSED

NOTIFICATION_DEDUPED
NOTIFICATION_SUPPRESSED
NOTIFICATION_DELAYED_QUIET_HOURS
NOTIFICATION_DELIVERY_PLANNED
NOTIFICATION_DELIVERY_RETRIED
```

Standard variables:

```text
actor.userId
externalPortalAccount.id
workspace.id
project.id
notification.id
recipient.principalType
recipient.id
category
channel
target.type
target.id
subscription.id
digestRule.id
digestRun.id
reminderRule.id
reminderInstance.id
alertRule.id
alertEvent.id
suppression.reason
occurredAt
traceId
```

---

# 19. Audit / activity / outbox

Audit-sensitive actions:

```text
security alert created
sensitive access notification created
external notification preference changed
notification default policy changed
alert rule created/changed
reminder rule created/changed
digest rule created/changed
delivery retry
notification report exported
```

Activity actions:

```text
NOTIFICATION_SUBSCRIPTION_CREATED
REMINDER_INSTANCE_CREATED
ALERT_EVENT_CREATED
DIGEST_RUN_SENT
```

Outbox required for major notification events.

Idempotency recommended for:

```text
preference update
subscription create
digest run
reminder evaluation
alert evaluation
bulk mark read/dismiss
delivery retry
```

---

# 20. Business rules master

## 20.1 Preference rules

```text
PREF-001 Preference profile unique per workspace/principal.
PREF-002 Critical notifications may be non-mutable.
PREF-003 Unsupported channels cannot be marked delivered.
PREF-004 Quiet hours use recipient timezone.
PREF-005 Preference does not grant access.
```

## 20.2 Subscription rules

```text
SUB-001 Subscribe requires target access.
SUB-002 Subscription does not grant access.
SUB-003 Access loss stops or masks future notifications.
SUB-004 Duplicate active subscription prevented.
SUB-005 Auto-subscription follows workspace policy.
```

## 20.3 Digest rules

```text
DIG-001 Digest re-checks access at generation time.
DIG-002 Hidden target details omitted.
DIG-003 Critical notifications can bypass digest.
DIG-004 Failed digest delivery does not delete notifications.
```

## 20.4 Reminder rules

```text
REM-001 Reminder does not mutate source.
REM-002 Duplicate reminders deduped.
REM-003 Source completion cancels reminder.
REM-004 Snooze/dismiss per recipient.
```

## 20.5 Alert rules

```text
ALERT-001 Alert rule declarative only.
ALERT-002 Alert does not mutate business object.
ALERT-003 Alert priority/severity required.
ALERT-004 Alert recipients must be safe/authorized.
```

## 20.6 Delivery rules

```text
DELIV-001 Delivery uses enabled provider only.
DELIV-002 Provider failure recorded.
DELIV-003 No provider means no delivery claim.
DELIV-004 In-app notification can exist without external provider.
```

---

# 21. Error catalog

```text
NOTIFICATION_PREFERENCE_PROFILE_NOT_FOUND
NOTIFICATION_PREFERENCE_ACCESS_DENIED
NOTIFICATION_CATEGORY_INVALID
NOTIFICATION_CHANNEL_NOT_SUPPORTED
NOTIFICATION_CHANNEL_PROVIDER_UNAVAILABLE
QUIET_HOURS_INVALID
TIMEZONE_INVALID

NOTIFICATION_SUBSCRIPTION_NOT_FOUND
NOTIFICATION_SUBSCRIPTION_DUPLICATE
NOTIFICATION_SUBSCRIPTION_TARGET_NOT_FOUND
NOTIFICATION_SUBSCRIPTION_TARGET_ACCESS_DENIED

DIGEST_RULE_NOT_FOUND
DIGEST_RULE_INVALID
DIGEST_RUN_NOT_FOUND
DIGEST_RUN_FAILED
DIGEST_PROVIDER_UNAVAILABLE

REMINDER_RULE_NOT_FOUND
REMINDER_RULE_INVALID
REMINDER_INSTANCE_NOT_FOUND
REMINDER_INSTANCE_INVALID_STATUS
REMINDER_SOURCE_NOT_FOUND
REMINDER_EVALUATION_FAILED

ALERT_RULE_NOT_FOUND
ALERT_RULE_INVALID
ALERT_EVENT_NOT_FOUND
ALERT_EVENT_INVALID_STATUS
ALERT_EVALUATION_FAILED

NOTIFICATION_SUPPRESSION_NOT_FOUND
NOTIFICATION_DEDUP_KEY_INVALID
NOTIFICATION_ACCESS_DENIED
NOTIFICATION_TARGET_ACCESS_DENIED
NOTIFICATION_BULK_OPERATION_TOO_LARGE
DELIVERY_ATTEMPT_NOT_FOUND
DELIVERY_RETRY_NOT_ALLOWED
```

---

# 22. Required tests

## 22.1 Preference tests

```text
createDefaultPreferenceProfile_success
updatePreferenceProfile_success
updateCategoryPreference_success
unsupportedChannelDeliveryDisabled
quietHoursDelaysNormalNotification
criticalNotificationBypassesQuietHours
timezoneBoundaryQuietHours_success
```

## 22.2 Subscription tests

```text
subscribeToProject_success
subscribeWithoutTargetAccess_forbidden
subscriptionDoesNotGrantAccess
muteSubscription_suppressesOptionalNotifications
autoSubscribeOnAssignment_success
accessLossStopsSubscriptionNotifications
duplicateSubscription_rejected
```

## 22.3 Digest tests

```text
createDigestRule_success
digestRunGroupsNotifications_success
digestRechecksAccess
digestMasksSensitiveFields
digestExcludesReadNotifications_ifPolicy
digestProviderUnavailable_marksFailed
criticalNotificationBypassesDigest
```

## 22.4 Reminder tests

```text
createReminderRule_success
taskDueReminderCreated_success
overdueMeetingActionReminder_success
reminderDedupPreventsSpam
snoozeReminder_success
dismissReminder_doesNotMutateSource
sourceCompletedCancelsReminder
reminderCreatesWorkInboxItem
```

## 22.5 Alert tests

```text
createAlertRule_success
criticalRiskAlertCreated
baselineGuardBlockedAlertCreated
sensitiveDocumentDownloadAlertCreated
clientVisibleChangeAlertCreated
alertDedupWorks
acknowledgeAlert_success
dismissAlert_success
alertDoesNotMutateSource
```

## 22.6 Notification center tests

```text
listNotifications_recipientOnly
openNotificationTarget_rechecksAccess
notificationSnippetDoesNotLeakSensitiveData
bulkMarkRead_success
bulkDismiss_success
dedupSuppressionRecorded
quietHoursSuppressionRecorded
```

## 22.7 Portal tests

```text
portalNotificationList_clientSafeOnly
portalPreferences_update_success
portalNotificationRequiresActiveGrant
portalNotificationDoesNotExposeInternalFields
doNotContactSuppressesExternalOptionalDelivery
```

## 22.8 Governance/security integration tests

```text
sensitiveAccessCreatesSecurityAlert
objectGrantCreatedNotifiesOwner
restoreCompletedNotifiesOwner
baselineGuardBlockedNotifiesProjectManager
governanceNotificationMasksSensitiveDetails
```

## 22.9 Authorization tests

```text
viewOtherUserNotifications_forbidden
manageAlertRule_withoutPermission_forbidden
manageReminderRule_withoutPermission_forbidden
viewSuppression_withoutPermission_forbidden
retryDelivery_withoutPermission_forbidden
crossWorkspaceNotification_forbidden
```

## 22.10 Event/audit tests

```text
advancedNotificationEventSeeder_firstRun_createsDefinitions
advancedNotificationEventSeeder_secondRun_noDuplicates
preferenceUpdated_eventEmitted
alertCreated_eventEmitted
digestRunSent_eventEmitted
deliveryRetry_auditCreated
```

---

# 23. Manual verification checklist

Completion file must include:

```text
1. Create default preference profile.
2. Update quiet hours.
3. Subscribe to project.
4. Trigger task assignment notification.
5. Confirm notification appears in notification center.
6. Confirm opening target re-checks permission.
7. Trigger same event repeatedly and confirm dedup.
8. Create daily digest rule.
9. Run digest and confirm grouped safe content.
10. Create reminder rule for task due soon.
11. Confirm reminder creates notification/inbox item.
12. Snooze reminder and confirm source unchanged.
13. Create alert rule for baseline guard blocked.
14. Trigger baseline guard blocked and confirm alert notification.
15. Trigger sensitive access and confirm masked security alert.
16. Test external portal notification with client-safe content.
17. Confirm unsupported channels are not falsely marked delivered.
18. Confirm no approval workflow or automation is falsely claimed.
```

---

# 24. Acceptance criteria

Phase 35 is accepted only if:

```text
1. Current notification capability is classified against TO-BE.
2. NotificationPreferenceProfile implemented/tested.
3. Category/channel preferences implemented/tested.
4. Quiet hours implemented/tested.
5. Subscription/watcher implemented/tested.
6. Auto-subscription implemented/tested.
7. DigestRule and DigestRun implemented/tested.
8. ReminderRule and ReminderInstance implemented/tested.
9. AlertRule and AlertEvent implemented/tested.
10. Deduplication and suppression implemented/tested.
11. Notification read/dismiss/archive behavior implemented/tested.
12. WorkInbox integration implemented/tested.
13. External portal notification subset implemented/tested or explicitly deferred.
14. Governance/security alert integration implemented/tested.
15. IAM permissions implemented/tested.
16. Event seeders idempotent.
17. Activity/audit/outbox follows Phase 04.
18. Unsupported delivery providers are not falsely claimed as working.
19. No approval workflow, workflow engine, marketing automation, billing/payment reminder, realtime chat is falsely claimed.
20. `mvn compile` passes.
21. `mvn test` passes.
22. Completion file exists.
```

Do not mark complete if:

```text
notification reveals hidden target data
subscription grants access
digest includes forbidden object details
quiet hours timezone handling is broken
reminder mutates source object
alert mutates source object
unsupported provider delivery is marked successful
portal notification exposes internal content
tests fail
```

---

# 25. Required phase completion file

Agent must create:

```text
docs/phase-complete/PHASE_35_ADVANCED_NOTIFICATIONS_DIGEST_REMINDER_TO_BE_COMPLETE.md
```

Required sections:

```text
# Phase 35 — Advanced Notifications / Digest / Reminder Complete

## 1. Summary
## 2. Source Inputs Reviewed
## 3. Current vs TO-BE Classification Matrix
## 4. Implemented in Current BE
## 5. Implemented / Hardened in This Phase
## 6. Seed-only Items Added
## 7. Deferred Items and Target Phase
## 8. Notification Boundary Decision
## 9. Entity Mapping
## 10. API Changes
## 11. Preference Center Strategy
## 12. Channel / Provider Strategy
## 13. Quiet Hours Strategy
## 14. Subscription / Watcher Strategy
## 15. Digest Strategy
## 16. Reminder Strategy
## 17. Alert Strategy
## 18. Deduplication / Suppression Strategy
## 19. Work Inbox Integration
## 20. Portal Notification Strategy
## 21. Governance / Security Alert Strategy
## 22. Reporting Strategy
## 23. AI Summary Strategy
## 24. Authorization Matrix
## 25. Activity / Audit / Outbox Notes
## 26. Idempotency Strategy
## 27. Tests Added
## 28. Commands Run
## 29. Test Results
## 30. Manual Verification
## 31. Assumptions
## 32. Deviations From Prompt
## 33. Known Risks
## 34. Future Phases That Must Return to Notifications
```

---

# 26. Future phases that must return

```text
Phase 36 — Contract / Billing / Revenue:
- Invoice/payment/contract notifications if commercial module exists.

Phase 38 — Audit / Compliance / Privacy:
- Notification retention, privacy export/delete, sensitive alert review.

Phase 39 — Integration / Import / Export:
- Slack/Teams/email/SMS/push providers, webhook delivery, calendar reminders.

Phase 40 — Service / Support / Maintenance:
- Incident/SLA/support ticket alerts, uptime notifications.

Phase 41 — Knowledge Graph / Semantic Index:
- AI notification summarization, priority prediction, smart noise reduction.

Mobile app backlog:
- Native push tokens, device management, push notification delivery.
```

---

# 27. Agent anti-bịa rules

The agent must not:

```text
1. Claim email/SMS/push/Slack/Teams delivery works without provider integration and tests.
2. Claim reminder is workflow.
3. Claim alert mutates business objects.
4. Claim subscription grants access.
5. Let digest leak forbidden target data.
6. Let notification snippets expose sensitive fields.
7. Treat notification as authorization.
8. Claim marketing automation exists.
9. Claim billing/payment reminders exist before Phase 36.
10. Claim realtime chat exists.
11. Hide provider/deferred integration gaps.
```

---

# 28. Prompt to give coding agent

```text
You are implementing Phase 35 — TO-BE Advanced Notifications, Digest, Reminder, Subscription, Alert Rules & Preference Center.

This is not an as-is documentation task.

Before coding, read:
- CLAUDE.md / CLAUDE.ms
- Coding_convention.md
- Phase 00–34 docs and completion files
- Current backend code, migrations, tests

Your task:
1. Compare current notification capability against this Phase 35 TO-BE spec.
2. Classify each capability with required labels.
3. Implement NotificationPreferenceProfile, category preferences, channel preferences, and quiet hours.
4. Implement NotificationSubscription and auto-subscription policy.
5. Implement DigestRule and DigestRun.
6. Implement ReminderRule and ReminderInstance.
7. Implement AlertRule and AlertEvent.
8. Implement deduplication, suppression, priority, read/dismiss/archive behavior.
9. Integrate with Work Inbox, External Portal, Governance/Security, Document, Task, Requirement, Quality, RAID, Meeting, Reporting where available.
10. Add IAM permissions, events, audit/outbox, idempotency, and tests.
11. Run mvn compile and mvn test.
12. Create docs/phase-complete/PHASE_35_ADVANCED_NOTIFICATIONS_DIGEST_REMINDER_TO_BE_COMPLETE.md.

Do not implement or claim approval workflow, workflow engine, marketing automation, billing/payment reminders, realtime chat, or external channel delivery without real provider integration and tests.
```

---

# 29. Quick tracking matrix

| Capability | Current backend | Phase 35 action | Later phase |
|---|---|---|---|
| Basic notification engine | Existing/unknown | Harden | Phase 06 base |
| Preference profile | Missing/unknown | Must implement | — |
| Category preference | Missing/unknown | Must implement | — |
| Channel preference | Missing/unknown | Must implement | Provider integration Phase 39 |
| Quiet hours | Missing/unknown | Must implement | — |
| Subscription/watcher | Missing/unknown | Must implement | — |
| Auto-subscription | Missing/unknown | Must implement | — |
| DigestRule | Missing/unknown | Must implement | AI summary Phase 41 |
| DigestRun | Missing/unknown | Must implement | — |
| ReminderRule | Missing/unknown | Must implement | — |
| ReminderInstance | Missing/unknown | Must implement | — |
| AlertRule | Missing/unknown | Must implement | — |
| AlertEvent | Missing/unknown | Must implement | — |
| Deduplication | Missing/unknown | Must implement | Smart noise Phase 41 |
| Suppression | Missing/unknown | Must implement | — |
| WorkInbox integration | Missing/partial | Must implement if Phase 32 | — |
| Portal notifications | Missing/partial | Must implement if Phase 30 | — |
| Governance/security alerts | Missing/partial | Must implement if Phase 34 | Compliance Phase 38 |
| Email provider | Missing/unknown | Do not claim unless real | Phase 39 |
| SMS provider | Missing/unknown | Do not claim unless real | Phase 39 |
| Slack/Teams provider | Missing/unknown | Defer | Phase 39 |
| Native mobile push | Missing | Defer | Mobile backlog |
| Marketing automation | Missing | Not in scope | CRM backlog |
| Billing reminders | Missing | Defer | Phase 36 |
| Workflow reminders | Removed | Not in scope | No approval workflow |

---

# 30. Final principle

Phase 35 is not complete when "a notification row is inserted."

Phase 35 is complete when Scopery can notify the right person at the right time without leaking data or creating noise:

```text
event
+ recipient resolution
+ permission check
+ preference
+ quiet hours
+ dedup
+ digest/reminder/alert logic
+ safe delivery
+ audit
= useful notification system
```

Notification is not permission.

Reminder is not workflow.

Alert is not automation.

Digest is not report export.

Subscription is not access.

External delivery must not be claimed without provider integration.
