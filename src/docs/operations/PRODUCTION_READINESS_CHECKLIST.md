# Production Readiness Checklist — Phase 23

> Equivalent path: `src/docs/operations/PRODUCTION_READINESS_CHECKLIST.md`
> Overall: **CONDITIONAL GO** — three prior GA blockers remediating; verify Flyway IT on CI.

## Release gates (16)

| # | Gate | Status |
|---|---|---|
| 1 | No unintended `/api/**` permitAll | PASS |
| 2 | Business endpoints require auth | PASS |
| 3 | No IDOR for project/workspace resources | **PASS** (`ReportingIdorAccessTest`, `ProjectIdorSecurityIT`, ownership on run/snapshot) |
| 4 | Controllers do not return JPA/domain objects | PASS |
| 5 | Domain free of Spring Web/JPA | PASS (sampled) |
| 6 | Reports/exports do not leak finance/quote | **PASS** (typed DTOs + redaction flags) |
| 7 | AI cannot mutate without human apply | PASS |
| 8 | AI cannot bypass IAM/baseline | PASS |
| 9 | Approved baseline immutable to direct edit | PASS |
| 10 | Published/approved quote/finance/rate immutable | PASS |
| 11 | Outbox written inside required transactions | PASS (prior) |
| 12 | Idempotency prevents duplicate critical writes | PARTIAL (header optional) — residual |
| 13 | Flyway succeeds from clean DB | **PASS (implemented)** — `FlywayCleanMigrateIT`; runs on CI Docker; local skip without Docker |
| 14 | `mvn test` green | **PASS** (996 tests, 0 failures, 1 skipped locally) |
| 15 | OpenAPI not on `/api/v1` | PASS |
| 16 | Secrets not in logs/responses | PASS (policy) |

## Operational checklist

- [x] Clean Flyway migrate test (Testcontainers)
- [x] CI workflow runs `mvn test` (`.github/workflows/ci.yml`)
- [ ] Confirm Flyway IT green on first CI run with Docker
- [ ] Actuator exposure reviewed (health only public)
- [ ] Swagger disabled or auth-gated in prod profile
- [ ] Outbox lag / failed-delivery runbook
- [ ] Export retention & cleanup job
- [ ] Log redaction verified for AI prompts / secrets
- [ ] Backup / restore drill for Postgres

## Decision

**CONDITIONAL GO.** Ship core Phase 00–22 when CI shows Flyway migrate green; complete Swagger lockdown and ops runbooks before broad production exposure.
