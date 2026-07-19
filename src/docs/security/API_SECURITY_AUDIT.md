# API Security Audit — Phase 23

> Equivalent path: `src/docs/security/API_SECURITY_AUDIT.md` (repo docs live under `src/docs/`).
> Status: **PARTIAL PASS** — auth baseline verified; object-level IDOR suite incomplete.

## Scope

Review of Spring Security path policy, CSRF, CORS, public endpoints, and protected `/api/**` business routes against Phase 23 HDN-001 / release gates 1–2, 15–16.

## Findings

| Check | Result | Evidence |
|---|---|---|
| No `/api/**` permitAll wildcard | **PASS** | `SecurityPathPolicy` + `SecurityPathPolicyTest`; public paths limited to health, swagger, auth bootstrap |
| Business endpoints require auth | **PASS (IT)** | `ProtectedEndpointSecurityIT`, `AuthPathIT` |
| CSRF ignore only auth bootstrap | **PASS** | `SecurityPathPolicy.csrfIgnoredMatchers()`, `CsrfSecurityIT` |
| Refresh cookie path `/api/iam/auth` | **PASS (static)** | Auth cookie configuration (Phase 01/12) |
| CORS configured origins only | **PASS (IT)** | `CorsSecurityIT` |
| OpenAPI path not `/api/v1` | **PASS** | `ApiPaths.BASE_PATH = "/api"`; `OldApiPathRegressionIT` / `containsApiVersionPrefix` |
| Secrets not in error responses | **PASS (policy)** | `GlobalExceptionHandler` + `SensitiveDataRedactor`; ongoing vigilance |
| Swagger public in all envs | **RISK** | Swagger paths are infra-public; restrict in production profile before GA |

## Public path inventory

- `/api/health`
- `/actuator/health`
- `/swagger-ui/**`, `/swagger-ui.html`, `/v3/api-docs/**`
- Auth POST: login, refresh, logout, password-reset request/confirm
- User registration POST `/api/iam/users` (CSRF-exempt)

Explicitly **not** public: `/api/iam/auth/password/change`, `/api/iam/auth/sessions/revoke-all`.

## Gaps (must close before GA)

1. Production profile should deny or protect Swagger/OpenAPI.
2. Full controller→auth matrix still sampled, not exhaustive (see `IAM_PERMISSION_COVERAGE.md`).
3. No automated scan that every `@RestController` under `/api` is covered by a 401 IT.

## Decision

**Gate 1–2:** PASS. **Gate 15:** PASS. **Gate 16:** PASS (policy). Remaining ops hardening deferred but documented.
