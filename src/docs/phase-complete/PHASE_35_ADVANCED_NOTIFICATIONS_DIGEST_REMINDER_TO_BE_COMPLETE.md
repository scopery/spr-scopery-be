# Phase 35 — Advanced Notifications Complete

## 1. Summary

Phase 35 delivered preference profiles, subscriptions, digest/alert/reminder rules, and evaluation jobs (`V78`).

**Follow-up (this pass):** category/channel preferences, suppression ledger, richer equals/and condition evaluation wired into alert job (`V81`). Error catalog expanded to full spec coverage.

## 2. Implemented

- Preference profile GET/PUT (`notification_preference_profile`)
- Subscriptions, digest/alert/reminder CRUD + scheduled jobs (no email claim)
- Channel prefs: `GET/PUT /api/v1/workspaces/{workspaceId}/notifications/channel-preferences/me` (`notification_channel_preference`)
- Suppression ledger table + writes when alert conditions fail (`notification_suppression_ledger`)
- `SimpleConditionEvaluator` (`equals` / `and`) used by `AlertEvaluationJob`
- `DigestRun` table and domain (`V92`, `notification_digest_run`)
- Scheduled jobs: `AdvancedReminderEvaluationJob`, `DigestEvaluationJob`, `AlertEvaluationJob` — all registered in `ScheduledJobRegistry`
- Full error catalog: `AdvancedNotificationErrorCatalog` (35 entries covering all spec section 21 codes) + `AdvancedNotificationExceptions` factory methods for each

## 3. Deviations From Spec

### DEV-001 — NotificationCategoryPreference merged into NotificationChannelPreference

**Spec defines** (section 7.2): a separate `notification_category_preference` table with its own entity (`NotificationCategoryPreference`) keyed on `preference_profile_id + category`.

**What was implemented instead:** category-level configuration is stored as a `category_code` column on `notification_channel_preference` (`notification_channel_preference.category_code`). A single row in that table combines channel + category, meaning `channel_preference_id` effectively captures the (channel, category) pair per user.

**Rationale:** Avoids a separate join table for what is essentially a cross-product of (channel × category). The spec's separation would require two queries for a single preference lookup; the merged model needs one.

**Impact:** The API surface for category preferences is exposed via the channel-preference endpoint (`/channel-preferences/me`) rather than a dedicated `/categories/{category}` endpoint. Clients filter by `categoryCode` query param. If the spec's distinct PREF-002 category endpoint is required in future, it can be introduced as a read-only projection over `notification_channel_preference` without schema changes.

**Migration note:** No `notification_category_preference` table exists. `V81` creates only `notification_channel_preference` and `notification_suppression_ledger`.

### DEV-002 — API path prefix is `/api/v1` not `/api`

**Spec section 8** shows paths starting with `/api`. The project uses `/api/v1` as the global base path (`ApiPaths.BASE_PATH`). All advanced notification paths follow this project-wide convention via `AdvancedNotificationApiPaths`, which composes from `ApiPaths.BASE_PATH`.

## 4. Deferred

| Item | Notes |
|---|---|
| Full domain-event condition payloads | Jobs still use scan-context stubs; richer event contracts deferred |
| Actual email/SMS/push delivery | Explicitly not claimed — no provider integrated |
| Portal notification APIs (`/api/portal/notifications`) | Deferred; Phase 30 client portal exists but portal notification subset not wired |
| Admin default preference APIs (`/preferences/defaults`) | Deferred — no workspace-level default policy enforcement yet |
| Convenience watch/unwatch endpoints (`/projects/{id}/watch`) | Deferred — covered by generic subscription API |
| Notification center read/dismiss/bulk APIs | Covered by existing `notificationitem` sub-module (Phase 06); not duplicated here |
| Governance/security alert auto-trigger integration | Alert rules exist; event-driven triggers from governance module deferred |
| WorkInbox auto-creation from ReminderInstance | `WorkInboxItem` domain exists; wiring deferred to a hardening pass |
| AI-assisted notification summaries | Deferred to Phase 41 |
| Report/analytics APIs (`/reports/notifications/*`) | Deferred to Phase 22 reporting module |

## 5. Release Decision

**Phase 35 MUST path + channel prefs/suppression/basic conditions: COMPLETE** for non-delivery evaluation. Delivery remains out of scope per spec boundary decision (section 4.5). Error catalog is now complete.
