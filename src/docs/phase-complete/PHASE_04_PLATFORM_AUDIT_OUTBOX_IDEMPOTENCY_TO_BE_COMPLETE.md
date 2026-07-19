# Phase 04 — Platform Audit / Outbox / Idempotency TO-BE Complete

## 1. Summary

Phase 04 hardens the platform reliability core: transactional outbox now has a claim/retry/dead-letter processor, idempotency persists request hashes with conflict/`IN_PROGRESS` semantics, immutable audit is append-only (DB trigger) with severity + redaction, sensitive payloads are scrubbed via a shared redactor, and platform event seeds (`SCOPERY_PLATFORM`) plus seeder contracts are documented. Email handoff continues via the existing AFTER_COMMIT notification path (explicitly deferred to Phase 20 for generic outbox→email fan-out).

## 2. Source Inputs Reviewed

- `PHASE_00_MASTER_ROADMAP_MODULAR_REVISED.md`
- `PHASE_01` / `PHASE_02` / `PHASE_03` specs + completion files
- `PHASE_04_PLATFORM_AUDIT_OUTBOX_IDEMPOTENCY_TO_BE_DETAILED.md`
- Existing `common/audit`, `common/outbox`, `platform/web/idempotency`, notification email outbox, event registry seeders

## 3. Current vs TO-BE Classification Matrix

| Capability | Classification | Notes |
|---|---|---|
| Activity log (`app_activity_log`) | `CURRENTLY_IMPLEMENTED` + verified | Module action constants + `ActivityLogService`; tests added |
| Immutable audit (`app_audit_event`) | Hardened in Phase 04 | Severity, redaction, DB append-only trigger, expanded event types + callers |
| Transactional outbox enqueue | `CURRENTLY_IMPLEMENTED` | Envelope + redaction + optional event-code validator |
| Transactional outbox processor | `MUST_IMPLEMENT_IN_PHASE_04` → done | Claim lock, retry backoff, `DEAD_LETTER`, Spring publish event |
| Email outbox (notification) | `CURRENTLY_IMPLEMENTED` | Separate from platform outbox; retry/claim already existed |
| Email handoff via platform outbox | `DEFERRED_TO_PHASE_20` | Keep AFTER_COMMIT email trigger for MVP |
| Idempotency persistent store | Hardened | Request hash, conflict, `IN_PROGRESS`, redacted cache, cleanup job |
| TraceId | `CURRENTLY_IMPLEMENTED` | Request filter → MDC → activity/audit/outbox/error |
| correlationId | `DEFERRED_TO_PHASE_23` | Document only; use `traceId` as correlation today |
| Error response shape | `CURRENTLY_IMPLEMENTED` | `ErrorResponse` + `PlatformErrorCatalog` |
| Job locking (outbox rows) | Implemented | `FOR UPDATE SKIP LOCKED` + atomic claim |
| Generic distributed JobLock table | `DEFERRED_TO_PHASE_23` | Row claim sufficient for outbox/email |
| Event registry seeder standard | Defined | IAM/Workspace patterns + platform seeder |
| Email template seeder standard | Defined (existing) | `SystemEmailTemplateInitializer` contract documented |
| Event dataClassification | `DEFERRED_TO_PHASE_38` | |
| Audit/outbox read admin APIs | `DEFERRED_TO_PHASE_38` / `DEFERRED_TO_PHASE_23` | Write-side only now |
| Required Idempotency-Key on critical POSTs | `DEFERRED_TO_PHASE_23` | Optional header still supported |

## 4. Implemented in Current BE (pre–Phase 04)

- Activity log service + module loggers/constants
- Immutable audit write service (partial callers)
- Outbox enqueue (PENDING only, no processor)
- Idempotency filter + `app_idempotency_key` (replay only)
- Email outbox processor with claim/retry
- TraceId via `RequestLoggingFilter` / `RequestContext`
- Standard `ErrorResponse` with traceId
- IAM/Workspace event + email template seeders

## 5. Implemented / Hardened in This Phase

- **V42** migration: outbox lock/retry/DLQ fields; idempotency status/request_hash; audit severity + append-only trigger
- `TransactionalOutboxProcessor` + envelope enqueue + redaction
- `IdempotencyKeyFilter` request-hash conflict / `IN_PROGRESS` / redacted response cache
- `IdempotencyCleanupJob`
- `SensitiveDataRedactor`
- `PlatformErrorCatalog`
- `PlatformEventDefinitionSeedInitializer` (`SCOPERY_PLATFORM`) + `OutboxEventCodeValidator`
- Audit coverage: login failed, password change/reset, user suspend, org/workspace archive, workspace member deactivate
- `ScheduledJobRegistry` entries for outbox + idempotency cleanup
- Config under `scopery.platform.*`

## 6. Deferred Items and Target Phase

| Item | Target |
|---|---|
| Platform outbox → EmailRule → EmailOutbox handoff | Phase 20 / Notification |
| AFTER_COMMIT email drop risk compensation | Phase 20 |
| correlationId distinct from traceId | Phase 23 |
| Generic `JobLock` entity | Phase 23 |
| Mandatory Idempotency-Key on critical POSTs | Phase 23 |
| Audit / outbox admin read APIs | Phase 38 / Ops |
| EventDefinition.dataClassification | Phase 38 |
| Full activity action rename to TO-BE names (`IAM_LOGIN_SUCCESS` etc.) | Incremental; current constants kept |
| Project/AI/Knowledge event seeders | Phases 07–10 / module owners |
| Export / finance / AI blocked audit types | Future module phases |

## 7. Platform Entity Mapping

| TO-BE concept | Actual table / package |
|---|---|
| ActivityLog | `app_activity_log` → `common.audit` |
| AuditEvent | `app_audit_event` → `common.audit` |
| PlatformEventOutbox | `app_transactional_outbox` → `common.outbox` |
| IdempotencyRecord | `app_idempotency_key` → `platform.web.idempotency` |
| Email delivery outbox | `notification_email_outbox` (module) |
| Job lock | Outbox/email row claim (no separate table) |

## 8. Activity Log Contract

- Write via `ActivityLogService` / module `*ActivityLogger`
- Action names are module string constants (not free-form in call sites)
- Includes `traceId` from MDC when present
- Failures degrade gracefully (`REQUIRES_NEW` + swallow)
- Secrets must not be placed in `message`/`metadata` (callers + future redaction of metadata deferred)

## 9. Immutable Audit Contract

- Append-only: JPA `updatable=false` + DB trigger blocks UPDATE/DELETE
- Severity: `INFO` / `SECURITY` (auto for security-sensitive types)
- Payloads redacted via `SensitiveDataRedactor`
- No public update/delete API
- Write failure logs WARN (does not fail business TX) — `PLATFORM_AUDIT_WRITE_FAILED` event seeded for future alerting

## 10. Event / Outbox Strategy Decision

**Chosen strategy (Phase 04):**

1. Business actions enqueue **transactional outbox** in the same DB transaction.
2. `TransactionalOutboxProcessor` claims rows, publishes `PlatformOutboxPublishedEvent`, marks `PUBLISHED`.
3. **Email** continues via Spring `@TransactionalEventListener(AFTER_COMMIT)` → `EmailDispatchService` → `notification_email_outbox` (existing).
4. Generic outbox→email fan-out deferred to Phase 20; drop-after-commit risk documented.

Payload envelope fields: `eventCode`, `eventVersion`, `sourceSystem`, `occurredAt`, `traceId`, `aggregateType`, `aggregateId`, `data` (redacted).

Unknown event codes: allowed with WARN when `OutboxEventCodeValidator` is present (monitoring path; `common` cannot hard-depend on EventRegistry).

## 11. Idempotency Strategy

- Header `Idempotency-Key` on `POST /api/**` (optional)
- Scope: actor + method + path + raw key → `key_hash`
- Body SHA-256 → `request_hash`
- Same key + same hash + `COMPLETED` → replay
- Same key + different hash → `409 PLATFORM_IDEMPOTENCY_CONFLICT`
- Concurrent `IN_PROGRESS` → `409 PLATFORM_IDEMPOTENCY_IN_PROGRESS`
- Cached response bodies redacted
- TTL 24h; hourly cleanup job

## 12. Trace / Correlation Strategy

- Every request gets `traceId` (`X-Trace-Id` / generated)
- Propagated to MDC, activity, audit, outbox, error responses
- `correlationId`: deferred; treat `traceId` as the correlation key until Phase 23

## 13. Error Response Strategy

- Standard `ErrorResponse` with `traceId`
- Platform codes in `PlatformErrorCatalog` (idempotency, outbox, audit, job lock)

## 14. Scheduler / Job Locking Strategy

| Job | Locking |
|---|---|
| `TransactionalOutboxProcessor` | `FOR UPDATE SKIP LOCKED` + atomic `claim` UPDATE |
| `EmailOutboxProcessor` | Existing row claim |
| `IdempotencyCleanupJob` | Idempotent DELETE by expiry |

Catalog: `ScheduledJobRegistry`.

## 15. Event Registry Seeder Standard

1. `@Component` + `ApplicationListener<ApplicationReadyEvent>`
2. Source system constant (e.g. `SCOPERY_IAM`, `SCOPERY_WORKSPACE`, `SCOPERY_PLATFORM`)
3. Idempotent `findByCode` → create if missing
4. Variables: delete+upsert or equivalent idempotent upsert
5. Unit test: first run creates; second run no duplicates

Platform seeds: `PLATFORM_OUTBOX_MESSAGE_DEAD_LETTERED`, `PLATFORM_OUTBOX_MESSAGE_RETRIED`, `PLATFORM_IDEMPOTENCY_CONFLICT_DETECTED`, `PLATFORM_JOB_FAILED`, `PLATFORM_AUDIT_WRITE_FAILED`.

## 16. Email Template Seeder Standard

1. Seed in `SystemEmailTemplateInitializer` (or module-owned listener)
2. Template code stable; link to registered event code
3. Required variables must exist on EventDefinition
4. Rules: enable only when recipient strategy is safe
5. Never embed raw secrets in template samples

## 17. Sensitive Data Redaction Rules

`SensitiveDataRedactor` redacts keys matching password/secret/token/apiKey/authorization (and nested objects). Applied to:

- Audit before/after JSON
- Outbox envelope `data`
- Idempotency cached response JSON

Never store: raw passwords, tokens, invitation codes, API secrets, Authorization headers.

## 18. Tests Added

- `SensitiveDataRedactorTest`
- `TransactionalOutboxServiceTest`
- `TransactionalOutboxProcessorTest`
- `ImmutableAuditEventServiceTest`
- `ActivityLogServiceTest`
- `IdempotencyKeyFilterTest`
- `PlatformEventDefinitionSeedInitializerTest`
- Updated constructors in Login/Phase02/IamUser/ArchiveWorkspace/Organization tests

## 19. Commands Run

```bash
./mvnw test
```

## 20. Test Results

`./mvnw test` → **BUILD SUCCESS** — Tests run: **651**, Failures: 0, Errors: 0, Skipped: 1.

## 21. Manual Verification

| Check | Status |
|---|---|
| Write action → activity log | Covered by unit tests + existing module usage |
| Sensitive action → audit | Unit + wired actions |
| Business event → outbox row | Unit (`TransactionalOutboxServiceTest`) |
| Rollback ⇒ no outbox | Same TX as business write (pattern); live rollback **Not yet verified — requires running the application** |
| Processor → PUBLISHED | Unit (`TransactionalOutboxProcessorTest`) |
| Failure → retry / DEAD_LETTER | Unit |
| Idempotency replay / conflict | Unit |
| Error response includes traceId | Existing GlobalExceptionHandler + filter JSON errors |
| No raw secrets in audit/outbox | Redactor unit + outbox/audit tests |
| Seeder idempotent | Platform seeder unit test |

Live multi-instance outbox double-send: **Not yet verified — requires running two app instances against PostgreSQL**.

## 22. Assumptions

- Table names remain `app_*` (existing) rather than renaming to `platform_*`
- Soft WARN for unknown event codes is acceptable vs hard-fail during rollout
- Email AFTER_COMMIT path remains MVP-acceptable

## 23. Deviations From Prompt

- Package mapping uses `common.*` / `platform.web.idempotency` instead of new `platform/audit|outbox` packages (documented in §7)
- Activity action string names retain existing module constants rather than mass-renaming to TO-BE catalog names
- Email not migrated onto platform outbox consumer in this phase (explicit deferral)

## 24. Known Risks

- AFTER_COMMIT email listener can still drop events on handler failure (Phase 20)
- Outbox claim uses separate TX from publish listener side effects of consumers (consumers must be idempotent)
- Concurrent idempotency under extreme load relies on unique PK + conflict handling

## 25. Future Phases That Must Return to Platform

- Phase 06/20 — Notification / email rule fan-out from platform outbox
- Phase 07–10 — Project / AI event seeders + outbox emit consistency
- Phase 22 — Export audit + activity timelines
- Phase 23 — Hardening (required idempotency keys, correlationId, JobLock)
- Phase 38 — Compliance classification, audit read APIs, retention
