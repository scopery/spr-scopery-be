# Phase 20 — Project Events / Notifications TO-BE Complete

## 1. Summary

Phase 20 delivered `modules/projectnotification`: project/task watchers, notification preferences, recipient resolution strategies, due-date reminder job, PlatformOutbox→Phase 06 notification bridge, project email template/rule seeders, IAM rights, Flyway `V57`, and unit tests. Mobile push, chat, digest, workflow escalation, and external client portal notifications were **not** implemented.

## 2. Source Inputs Reviewed

- `PHASE_20_PROJECT_EVENTS_NOTIFICATIONS_TO_BE_DETAILED.md`
- Phase 05 Event Registry / Phase 06 Notification Engine implementation
- Phase 09–19 project/scheduling/finance/quote/baseline publishers and seeders
- Phase 04 outbox (`PlatformOutboxPublishedEvent`)
- Existing `EmailDispatchService`, `EmailRecipientResolver`, `SystemEmailTemplateInitializer`

## 3. Current vs TO-BE Classification Matrix

| Capability | Classification | Status |
|---|---|---|
| Project EventDefinitions | MUST / verify-seed | Verified prior seeds + reminder events seeded |
| Project templates/rules | MUST | Seeded |
| Project watcher | MUST | Implemented |
| Task watcher | MUST | Implemented |
| Recipient resolver | MUST | Implemented (extended strategies) |
| Preferences minimum | MUST | Implemented (PROJECT + EVENT_TYPE) |
| Due soon / overdue reminder | MUST | Implemented |
| Schedule risk rules | MUST | Seeded templates/rules + bridge |
| Baseline/change/finance/quote rules | MUST | Seeded + bridge |
| Payload masking | MUST | Phase 06 masker + project masker |
| Dedup | MUST | Phase 06 outbox dedup + reminder emission unique key |
| Activity feed | DEFERRED | Deferred Phase 22 |
| External client notifications | DEFERRED | Deferred post-23 |
| Push/chat/digest | OUT OF SCOPE | Not claimed |

## 4. Implemented in Current BE

- Phase 06 Notification Engine (templates, rules, outbox, in-app items, dedup, sensitive masker)
- Phase 05 EventDefinitions for project/quote/finance/baseline/scheduling
- Project/task/quote/baseline/finance outbox publishers
- IAM workspace rights model

## 5. Implemented / Hardened in This Phase

- Flyway `V57__create_project_notification_tables_phase20.sql`
- Module `modules/projectnotification` (subscription, tasksubscription, preference, reminder, recipient, bridge, masking, shared)
- Extended `EmailRecipientStrategy` + `ExtendedRecipientStrategyHandler` + multi-recipient `EmailDispatchService`
- `ProjectNotificationOutboxBridgeListener` for whitelisted outbox event codes
- Seeders `@Order(26)` events / `@Order(27)` templates+rules
- `ProjectDueDateReminderJob` + admin reminder APIs
- IAM `PROJECT_NOTIFICATION_MANAGEMENT` rights/permission

## 6. Seed-only Items Added

- Reminder events: `PROJECT_TASK_DUE_SOON`, `PROJECT_TASK_OVERDUE`, `PROJECT_TASK_AT_RISK`
- Email/in-app templates and rules for assigned/due/overdue/risk/schedule-fail/baseline/CR/quote/finance/margin

## 7. Deferred Items and Target Phase

| Item | Target |
|---|---|
| Activity feed integration | Phase 22 |
| Advanced subscription center / digest / quiet hours | Phase 35 |
| Push / chat | Phase 35 / post-23 |
| External client notifications | External portal backlog |
| AI notification summaries | Phase 21 |
| Workflow escalation | Phase 34 |

## 8. Notification Boundary Decision

Phase 20 is integration only. Delivery remains Phase 06 (`EmailOutbox` / `NotificationItem`). Notification does not grant access; deep links still require normal authorization. Approver alerts are not workflow approval.

## 9. Event Definition Seeder Matrix

| Source | Codes |
|---|---|
| Prior phases | `TASK_ASSIGNED`, schedule/risk, baseline/CR, quote, finance margin |
| Phase 20 `@Order(26)` | `PROJECT_TASK_DUE_SOON`, `PROJECT_TASK_OVERDUE`, `PROJECT_TASK_AT_RISK` |

## 10. Template Seeder Matrix

Seeded codes include: `PROJECT_TASK_ASSIGNED_EMAIL`, due/overdue/at-risk, schedule failed, baseline approved, CR lifecycle, quote submitted/approved, finance approved, margin warning, plus matching `*_INAPP` templates.

## 11. Notification Rule Seeder Matrix

Rules bind ACTIVE EventDefinitions to templates with strategies: `TASK_ASSIGNEE`, `PROJECT_WATCHERS`, `PROJECT_OWNER`, `CHANGE_WATCHERS`, `QUOTE_WATCHERS`, `FINANCE_WATCHERS`. Finance/quote amount templates use `allowSensitiveVariables=false` and generic subjects.

## 12. Project Subscription Entity Mapping

Table `project_notification_subscription` — types PROJECT_WATCHER/OWNER/MANAGER/FINANCE/QUOTE/CHANGE/BASELINE_WATCHER; statuses ACTIVE/MUTED/ARCHIVED; partial unique active (project, user, type).

## 13. Task Subscription Entity Mapping

Table `project_task_notification_subscription` — TASK_WATCHER / TASK_ASSIGNEE_AUTO / TASK_IN_CHARGE_AUTO; partial unique active (task, user, type).

## 14. Preference Strategy

Table `project_notification_preference` — PROJECT (+ optional TASK) + EVENT_TYPE + channel EMAIL/IN_APP. Mute/disabled skips optional notices; mandatory rules bypass mute in recipient handler.

## 15. Recipient Resolver Strategy

`ProjectExtendedRecipientStrategyHandler` resolves project owner (`Project.ownerUserId`), task in-charge, watchers by subscription type, requester from payload. Deduplicates, skips inactive users/members, applies preferences, supports `excludeActor` config JSON.

Approver fan-out uses watcher subscription types (CHANGE/QUOTE/FINANCE/BASELINE_WATCHER) rather than full IAM grant enumeration (`WORKSPACE_USERS_WITH_RIGHT` remains deferred).

## 16. Due Date Reminder Strategy

Daily cron (`scopery.project-notification.reminder-cron`, default 06:00). Due soon = dueDate tomorrow; overdue = dueDate &lt; today; excludes DONE/CANCELLED/ARCHIVED; records `project_reminder_run` + unique `project_reminder_emission.dedup_key`; publishes via Phase 06 trigger.

## 17. Schedule Risk Alert Strategy

Bridge listens for `TASK_SCHEDULE_AT_RISK`, `TASK_DUE_DATE_CAPACITY_GAP_DETECTED`, `RESOURCE_SCHEDULE_OVER_CAPACITY_DETECTED`, `SCHEDULE_RUN_FAILED` and dispatches seeded rules. Dedup via Phase 06 occurrence + rule + recipient email.

## 18. Baseline/Change Notification Strategy

Seeded rules for baseline approved and CR submitted/approved/rejected/applied; recipients via PROJECT_WATCHERS / CHANGE_WATCHERS.

## 19. Finance/Quote Masking Strategy

Templates avoid amounts in subjects. Rule `allowSensitiveVariables=false`. `ProjectNotificationPayloadMasker` redacts amount/margin keys when unauthorized. Phase 06 `SensitivePayloadMasker` still applies for EventVariable.sensitive paths.

## 20. Dedup / Idempotency Strategy

- Email/in-app: Phase 06 `dedupKey = eventDefinitionId:occurrenceKey:ruleId:recipientEmail`
- Reminders: unique `taskId:reminderType:date:recipientUserId`
- Outbox bridge uses outboxId as occurrenceId when present

## 21. API Changes

- `POST/GET /api/projects/{projectId}/notification-subscriptions` (+ `/me`, mute/unmute, DELETE)
- Task equivalents under `/tasks/{taskId}/notification-subscriptions`
- `GET/PUT .../notification-preferences/me` (project and task)
- `POST /api/projects/notifications/reminders/run`, `GET .../runs`, `GET .../runs/{runId}`

## 22. Authorization Matrix

Rights under `PROJECT_NOTIFICATION_MANAGEMENT` (seeded; included in workspace admin defaults): VIEW, SUBSCRIBE_SELF, MANAGE_SUBSCRIBERS, PREFERENCE_*, TASK_* variants, RULE_*, REMINDER_RUN, DELIVERY_VIEW.

## 23. Activity / Audit / Outbox Notes

`ProjectNotificationActivityLogger` for subscribe/mute/delete/preference/reminder. Delivery still uses Phase 06 activity/audit. Outbox bridge consumes `PlatformOutboxPublishedEvent` only for whitelisted codes.

## 24. Tests Added

- `ProjectNotificationSubscriptionDomainTest`
- `ProjectNotificationPreferenceDomainTest`
- `ProjectReminderEmissionDedupTest`
- `ProjectNotificationPayloadMaskerTest`
- `ProjectExtendedRecipientStrategyHandlerTest`

## 25. Commands Run

- `mvn -q -DskipTests compile`
- `mvn -q -Dtest='com.company.scopery.modules.projectnotification.**' test`

## 26. Test Results

- `mvn -DskipTests compile` — PASSED
- `mvn -Dtest='com.company.scopery.modules.projectnotification.**,com.company.scopery.modules.notification.emailrule.application.EmailRecipientResolverTest' test` — PASSED (16 tests)

## 27. Manual Verification

Checklist from TO-BE §24 remains for ops: seed idempotency, subscribe watchers, assign task, run reminder twice (no duplicate), CR/quote/finance masking, mute optional vs mandatory, no push/chat/digest artifacts.

## 28. Assumptions

- Project manager role = subscription type `PROJECT_MANAGER` (no dedicated project manager column)
- Approvers resolved via watcher subscriptions, not IAM grant fan-out
- Reminder job workspace filter optional (`workspaceId` null = all projects)

## 29. Deviations From Prompt

- Optional admin project notification-rule enable/disable APIs deferred to Phase 06 rule admin APIs
- Milestone due/missed reminder types seeded in schema/enums but job currently emits task due-soon/overdue only
- Auto-subscribe assignee on TASK_ASSIGNED not implemented as write-side hook (can subscribe via API / future listener)

## 30. Known Risks

- Outbox payload shape must nest `project`/`task`/`workspace`/`actor` for resolver; flat publishers may resolve fewer recipients
- Concurrent reminder runs blocked by RUNNING status only (no distributed lock)
- Large workspaces: reminder batch limited to 500 tasks per mode

## 31. Future Phases That Must Return to Notifications

Phases 21 (AI), 22 (reporting/digest), 23 (hardening), 27 (documents), 31 (collaboration), 34 (workflow), 35 (advanced subscription), 36–37 (billing/time), external portal backlog.
