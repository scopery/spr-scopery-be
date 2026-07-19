# Core Performance Smoke Baseline

Minimal manual and automated smoke checks for production readiness. **No synthetic benchmark numbers** — this document describes procedure only.

## Prerequisites

- Application running locally or in target environment (`SPRING_PROFILES_ACTIVE` as needed).
- Valid IAM user with at least `PROJECT_VIEW` on a known workspace/project (for authenticated GET).

## 1. Health (unauthenticated)

```http
GET /api/health
```

**Expect:** HTTP 200, JSON body with `"success": true` and `"status": "UP"` (or equivalent health payload).

**Automated:** `HealthEndpointSmokeTest` (MockMvc, no auth).

## 2. Authenticated read (baseline latency observation)

Pick one stable read endpoint that exercises auth + DB:

```http
GET /api/projects/{projectId}
Authorization: Bearer <access_token>
```

**Expect:** HTTP 200, `success: true`, project payload.

Record wall-clock time manually once per environment deploy (no fixed SLA in this doc). Compare against prior deploys informally.

## 3. Optional extended smoke (manual)

| Step | Endpoint | Notes |
|------|----------|-------|
| Login | `POST /api/iam/auth/login` | Obtain JWT |
| List projects | `GET /api/projects?workspaceId=...` | Pagination works |
| Dashboard | `GET /api/projects/{id}/dashboard` | Reporting read path |

## 4. What this does NOT cover

- Load/stress testing (see `src/docs/performance/CORE_PERFORMANCE_REVIEW.md`)
- Concurrency / transaction rollback suites
- Full regression matrix (`src/docs/testing/CORE_REGRESSION_TEST_MATRIX.md`)

## 5. Deferred

- Full DB-backed refresh-token rotation model (Phase 12.1) — remains deferred.
