# Phase 06 — Notification Engine TO-BE Complete

## 1. Summary

Phase 06 hardens the Notification module into a reliable notification engine core: email rule mandatory/sensitive flags with audit, template publish sensitive-variable gating, email outbox `RETRY_SCHEDULED`/`DEAD_LETTER` + `dedup_key`/`provider_message_id`, dispatch-time sensitive payload masking + deduplication, in-app `NotificationItem` inbox APIs under `/api/notifications`, cancel outbox API, and expanded notification event seeds. Push/chat/preferences/follow/mention/reminder/escalation/digest/provider webhooks remain deferred.

## 2. Source Inputs Reviewed

- `PHASE_00_MASTER_ROADMAP_MODULAR_REVISED.md`
- `PHASE_01`–`PHASE_05` specs + completion files (esp. Phase 04 audit/outbox, Phase 05 event registry)
- `PHASE_06_NOTIFICATION_ENGINE_TO_BE_DETAILED.md`
- Existing `modules/notification` (emailtemplate / emailrule / emailoutbox / emaildelivery / emailtrigger)
- `CLAUDE.md` Action+QueryService / DDD / NotificationExceptions conventions

## 3. Current vs TO-BE Classification Matrix

| Capability | Classification | Notes |
|---|---|---|
| Email template CRUD + versioning | `CURRENTLY_IMPLEMENTED` + hardened | Publish now validates sensitive vars |
| Email rule engine | Hardened | `mandatory`, `allowSensitiveVariables` |
| Email outbox / delivery | Hardened | Retry schedule + dead letter + dedup |
| EventRegistry variable validation | Hardened | Sensitive subject always rejected |
| Sensitive masking at dispatch | `MUST_IMPLEMENT_IN_PHASE_06` → done | `SensitivePayloadMasker` |
| Deduplication | Done | Unique `dedup_key` on outbox |
| Mandatory notice foundation | Done | Rule flag + SECURITY in-app severity |
| In-app NotificationItem | Done (minimal) | Own-inbox APIs |
| System email template seeders | `CURRENTLY_IMPLEMENTED` | Idempotent initializer unchanged |
| Notification event seeds | Expanded | Dead letter / item / dedup / mask |
| Push / chat / preferences / quiet hours | `DEFERRED_TO_PHASE_20/37` | Explicitly not implemented |
| Follow / subscribe / mention / reminder / escalation / digest | Deferred | |
| Provider webhooks (DELIVERED/BOUNCED/OPENED) | Deferred | |
| WORKSPACE_USERS_WITH_RIGHT recipients | Deferred skip | Unchanged skip path |
| Platform outbox → email consumer | Known gap | Still Spring `EmailNotificationTrigger` |

## 4. Implemented in Current BE (pre–Phase 06)

- EmailTemplate / Version / Rule / Delivery / Outbox
- Event-bound publish variable path validation
- EmailDispatchService via Spring event trigger
- Outbox processor with PENDING retry + FAILED terminal
- SystemEmailTemplateInitializer for IAM/Workspace emails
- NotificationEventDefinitionSeedInitializer (basic email lifecycle events)

## 5. Implemented / Hardened in This Phase

- **V44** migration: rule flags, outbox dedup/provider id, `notification_item` table
- Outbox statuses `RETRY_SCHEDULED`, `DEAD_LETTER`; claim/batch select both PENDING + RETRY_SCHEDULED
- `EmailOutbox.dedupKey` + `providerMessageId` mapped end-to-end
- EmailRule `mandatory` / `allowSensitiveVariables` + activity/audit when set true
- `EmailTemplateVariableValidator` sensitive subject/body rules; publish flag `allowSensitiveVariables`
- Dispatch: mask sensitive payload when rule disallows; dedup skip; create in-app item when userId resolvable
- `NotificationItem` submodule + APIs under `/api/notifications`
- `CancelEmailOutboxAction` + POST `/{id}/cancel`
- Seed events: `EMAIL_DELIVERY_DEAD_LETTERED`, `NOTIFICATION_ITEM_CREATED`, `NOTIFICATION_DEDUPLICATED`, `NOTIFICATION_SENSITIVE_VARIABLE_MASKED`
- Error catalog: sensitive variable, notification item, outbox cancel

## 6. Seed-only Items Added

| Code | Purpose | Emitter |
|---|---|---|
| EMAIL_DELIVERY_DEAD_LETTERED | Dead-letter catalog | Processor audit/activity (not full domain event emit) |
| NOTIFICATION_ITEM_CREATED | In-app create catalog | Activity on create |
| NOTIFICATION_DEDUPLICATED | Dedup catalog | Activity/audit on skip |
| NOTIFICATION_SENSITIVE_VARIABLE_MASKED | Mask catalog | Activity on mask |

Full Spring domain-event emitters for these remain seed/catalog oriented; dispatch/processor use activity + immutable audit where applicable.

## 7. Deferred Items and Target Phase

| Item | Target |
|---|---|
| Push / mobile | Phase 20 / 37 |
| Chat integrations | Phase 20 / 37 |
| Preferences + quiet hours | Phase 20 |
| Follow / subscribe | Phase 20 |
| Mention engine | Phase 20 |
| Reminder / escalation | Phase 20 |
| Digest | Phase 20 / 22 |
| Provider webhooks DELIVERED/BOUNCED/OPENED | Phase 37 |
| WORKSPACE_USERS_WITH_RIGHT resolution | Phase 20 (IAM grant fan-out) |
| Platform transactional outbox consumer for email | Phase 04 follow-up / 20 |
| Full subscription preference respect for mandatory | Phase 20 (mandatory flag is foundation only) |

## 8. Entity Mapping

### EmailRule

| TO-BE field | Actual |
|---|---|
| mandatory | `mandatory` BOOLEAN |
| allowSensitiveVariables | `allow_sensitive_variables` BOOLEAN |

### EmailOutbox

| TO-BE field | Actual |
|---|---|
| dedupKey | `dedup_key` UNIQUE NOT NULL |
| providerMessageId | `provider_message_id` |
| RETRY_SCHEDULED / DEAD_LETTER | status enum + CHECK |
| scheduledAt | `next_retry_at` (existing column) |

### NotificationItem

| TO-BE field | Actual |
|---|---|
| recipientUserId / title / bodyPreview / severity / priority / status | columns as specified |
| dedupKey | UNIQUE with recipient_user_id |
| mandatory / readAt / dismissedAt / traceId | columns |
| Auditable fields | `created_at` / `updated_at` / `created_by` / `updated_by` |

## 9. API Changes

| Method | Path | Change |
|---|---|---|
| POST/PUT | `/api/notification/email-rules` | + `mandatory`, `allowSensitiveVariables` |
| PATCH | `/api/notification/email-templates/{id}/versions/{versionId}/publish` | + `allowSensitiveVariables` query param |
| POST | `/api/notification/email-outbox/{id}/cancel` | **new** |
| GET | `/api/notifications` | **new** inbox list |
| GET | `/api/notifications/unread-count` | **new** |
| PATCH | `/api/notifications/{id}/read` | **new** |
| PATCH | `/api/notifications/read-all` | **new** |
| PATCH | `/api/notifications/{id}/dismiss` | **new** |

## 10. Template Versioning Rules

- DRAFT editable; PUBLISHED via `publish(publisherUserId)`
- Publish validates all `{{paths}}` against Event Registry
- Sensitive vars **always** rejected in subject
- Sensitive vars in body rejected unless `allowSensitiveVariables=true` (requires MANAGE_TEMPLATE auth already on publish)
- `published_at` / `published_by` mapped on `EmailTemplateVersion` (columns from V22; domain/JPA wired in Phase 06)

## 11. EventRegistry Integration

- Rules still require ACTIVE EventDefinition (existing)
- Publish loads EventVariables including `sensitive`
- Dispatch loads sensitive paths for masking when rule disallows sensitive body content
- Recipient strategies still resolve from event payload paths

## 12. Recipient Strategy Matrix

| Strategy | Phase 06 behavior |
|---|---|
| EVENT_ACTOR | Resolve `actor.email` + optional `actor.userId` |
| EVENT_TARGET_USER | `targetUser.email` + `targetUser.userId` |
| INVITEE_EMAIL | `invitee.email` + `invitee.userId` |
| REQUESTER_EMAIL | `requester.email` + `requester.userId` |
| STATIC_EMAIL | From `recipientConfigJson.email` |
| WORKSPACE_OWNER | `workspace.ownerEmail` + `workspace.ownerUserId` |
| WORKSPACE_USERS_WITH_RIGHT | **Deferred skip** (delivery SKIPPED + reason) |

## 13. Sensitive Variable Masking

- Publish-time: reject sensitive in subject always; body only if not allowed
- Dispatch-time: if `!rule.allowSensitiveVariables()`, mask sensitive paths to `***` via `SensitivePayloadMasker` before render
- In-app `bodyPreview` uses rendered (already masked) text truncated to 200 chars
- Activity: `NOTIFICATION_SENSITIVE_VARIABLE_MASKED`

## 14. Mandatory Notice Rules

- `EmailRule.mandatory` default false
- Setting true logs `MARK_EMAIL_RULE_MANDATORY` + audit `NOTIFICATION_RULE_MANDATORY_SET`
- In-app item severity = `SECURITY` when mandatory
- Preference/quiet-hours bypass for mandatory is deferred (flag foundation only)

## 15. Email Outbox / Delivery State Machine

```text
PENDING → PROCESSING → SENT
PENDING|RETRY_SCHEDULED → PROCESSING → RETRY_SCHEDULED (retryCount < max)
PENDING|RETRY_SCHEDULED → PROCESSING → DEAD_LETTER (retryCount >= max)
FAILED|DEAD_LETTER → PENDING (manual retry reset)
PENDING|RETRY_SCHEDULED|FAILED|DEAD_LETTER → CANCELLED
SKIPPED retained for delivery skip cases
```

Processor claims PENDING **or** RETRY_SCHEDULED with `scheduledAt <= now`.

## 16. Deduplication Strategy

```text
dedupKey = eventDefinitionId + ":" + (occurrenceId|eventOccurrenceId|aggregateId|actorUserId|"na")
           + ":" + ruleId + ":" + recipientEmail
```

- Unique DB constraint on outbox `dedup_key`
- Before create: `existsByDedupKey` → skip + activity/audit `NOTIFICATION_DEDUPLICATED`
- In-app uses same key with unique `(recipient_user_id, dedup_key)`

## 17. In-app Notification Decision

**Implemented** (must-have for Phase 06). Minimal inbox:

- Create on email dispatch when recipient `userId` (or trigger `actorUserId`) present
- Own-user only (IDOR → not found)
- List excludes DISMISSED by default; unread count; mark read / mark all / dismiss
- No preferences, digests, or multi-channel fan-out

## 18. Event Seeder Matrix

| Seeder | Order | Owner |
|---|---|---|
| NotificationEventDefinitionSeedInitializer | 13 | SCOPERY_NOTIFICATION |

Added Phase 06 codes listed in §6.

## 19. Email Template Seeder Matrix

| Seeder | Status |
|---|---|
| SystemEmailTemplateInitializer | Verified idempotent; createSystem still works (defaults mandatory/sensitive=false) |
| IAM password reset / org & workspace invitation / join request templates | Unchanged |

## 20. Authorization Matrix

| Surface | Auth |
|---|---|
| Email template/rule admin | Existing SYSTEM_NOTIFICATION_MANAGE_* / workspace NOTIFICATION_MANAGE_* |
| Outbox retry/cancel | `SYSTEM_NOTIFICATION_RETRY_DELIVERY` |
| `/api/notifications/*` | Authenticated user; own records only (no special right) |

## 21. Activity / Audit Notes

**Activity actions added:** `MARK_EMAIL_RULE_MANDATORY`, `ALLOW_EMAIL_RULE_SENSITIVE_VARIABLES`, `CANCEL_EMAIL`, `EMAIL_DELIVERY_DEAD_LETTERED`, `NOTIFICATION_DEDUPLICATED`, `NOTIFICATION_SENSITIVE_VARIABLE_MASKED`, `NOTIFICATION_ITEM_CREATED`, `MARK_NOTIFICATION_READ`, `MARK_ALL_NOTIFICATIONS_READ`, `DISMISS_NOTIFICATION`

**Immutable audit types:** `NOTIFICATION_RULE_MANDATORY_SET`, `NOTIFICATION_RULE_SENSITIVE_ALLOWED`, `EMAIL_DELIVERY_DEAD_LETTERED`, `NOTIFICATION_DEDUPLICATED`

## 22. Tests Added

- `EmailOutboxProcessorTest` — RETRY_SCHEDULED / DEAD_LETTER
- `EmailTemplateVariableValidatorTest` — sensitive subject/body
- `EmailDispatchServiceTest` — dedup skip + masking
- `EmailRecipientResolverTest` — userId resolve + deferred skip text
- `NotificationItemDomainTest` / `NotificationItemActionTest` — mark read / dismiss / own-user only

## 23. Commands Run

```bash
mvn -q -Dtest='com.company.scopery.modules.notification.**' test
mvn -q compile
mvn -q test
```

## 24. Test Results

- Notification module tests → **BUILD SUCCESS** (exit 0)
- `mvn -q compile` → **BUILD SUCCESS**
- Full suite `mvn -q test` → **BUILD SUCCESS** (685 tests, 0 failures, 0 errors)

## 25. Manual Verification

| Check | Status |
|---|---|
| Create/publish template + sensitive rejection | Unit |
| Mandatory/sensitive rule flags + audit | Unit/code path |
| Dispatch creates outbox + dedup | Unit |
| Processor retry / dead-letter | Unit |
| In-app list/read/dismiss | Unit (actions/domain) |
| Seeder second run | Existing initializer pattern; new events add-missing via seed support |
| Live HTTP against running app | **Not yet verified — requires running the application** |

## 26. Assumptions

- Outbox schedule column remains `next_retry_at` mapped as `scheduledAt`
- In-app creation piggybacks email dispatch (no separate non-email notification producers yet)
- Manual retry from FAILED/DEAD_LETTER resets to PENDING without requiring remaining retry budget
- Update rule: null `mandatory` / `allowSensitiveVariables` preserves existing values

## 27. Deviations From Prompt

- Event codes are seeded; not all emit as first-class EventRegistry payloads from processor (activity/audit used)
- Platform transactional outbox consumer for notification not wired (Spring `EmailNotificationTrigger` retained — documented gap)

## 28. Known Risks

- Event → email still uses Spring `EmailNotificationTrigger` rather than Phase 04 platform outbox consumer (known gap → Phase 04/20 follow-up)
- Dedup key without `occurrenceId`/`aggregateId` falls back to actor or `na` — may over-dedupe some events
- In-app item only created when userId resolvable; email-only recipients get email without inbox row
- WORKSPACE_USERS_WITH_RIGHT remains skipped — multi-recipient admin alerts incomplete
- Sensitive masking is path-based; nested arrays / alternate path shapes may miss

## 29. Future Phases That Must Return to Notification

- Phase 04/20 — Platform outbox consumer for event→notification
- Phase 07 — AI Agent notification templates/events
- Phase 08 — Document Hub notifications
- Phase 09/10 — Project/task notifications
- Phase 12–22 — Finance / quote / reporting / capacity alerts
- Phase 20/37 — Preferences, push, chat, digests, webhooks, WORKSPACE_USERS_WITH_RIGHT
