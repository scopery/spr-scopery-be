# Core Regression Test Matrix — Phase 23

> Equivalent path: `src/docs/testing/CORE_REGRESSION_TEST_MATRIX.md`
> Status: **Gates 3/6/13/14 remediating** — residual HDN categories remain backlog.

## Category matrix (spec §6 / HDN-*)

| Category | Spec | Status | Primary evidence |
|---|---|---|---|
| API security / permitAll | HDN-001 | **PASS** | Security ITs |
| IAM coverage | HDN-002 | PARTIAL | Action-layer authz |
| IDOR / path ownership | HDN-003 | **PASS** | `ReportingIdorAccessTest`, `ProjectIdorSecurityIT`, `ProjectAuthorizationActionTest` |
| Architecture boundaries | HDN-004 | MISSING | No ArchUnit |
| Transaction rollback | HDN-005 | MISSING | — |
| Outbox reliability | HDN-006 | PARTIAL | Unit tests |
| Idempotency | HDN-007 | PARTIAL | `IdempotencyKeyFilterTest` |
| Migrations / Flyway clean | HDN-008 | **PASS (CI)** | `FlywayCleanMigrateIT` + GHA; local skip without Docker |
| OpenAPI contract | HDN-009 | PARTIAL→improved | Typed reporting responses |
| Performance regression | HDN-011 | MISSING | Deferred |
| Concurrency / optimistic lock | HDN-012 | MISSING | — |
| Sensitive masking | HDN-013 | **PASS** | Typed DTOs + finance/quote redaction |
| AI safety | HDN-014 | PARTIAL | Safe-apply tests |
| Baseline / CR guards | HDN-015 | **PASS** | `PostBaselineEditGuardTest` |
| Finance/quote/rate immutability | HDN-016 | **PASS** | Lifecycle + finance immutability tests |
| Notification dedup | HDN-017 | PARTIAL | Reminder/email dedup |
| Reporting/export | HDN-018 | **PASS** | Typed APIs + export lifecycle + masking |
| Observability | HDN-019 | PARTIAL | CI added; runbooks backlog |
| CI quality gate | HDN-020 | **PASS** | `.github/workflows/ci.yml` + Surefire |

## Surefire (latest remediating verification)

```text
Tests run: 996, Failures: 0, Errors: 0, Skipped: 1 — BUILD SUCCESS
```

Skipped locally: `FlywayCleanMigrateIT` (`disabledWithoutDocker=true`).
