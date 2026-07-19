# Phase 01 — API Path & Security Baseline Complete

## 1. Summary

Closed Phase 01 gaps against `PHASE_01_API_PATH_SECURITY_BASELINE_DETAILED.md`:

- Tightened Spring Security public/CSRF whitelist to explicit §8.1 endpoints (no more blanket `/api/iam/auth/**`).
- Extracted `SecurityPathPolicy` as the single source of path/CSRF contract.
- Added unit + MockMvc security regression tests (no Postgres/Redis required).
- Confirmed cookie paths: `access_token` → `/`, `refresh_token` → `/api/iam/auth`.
- Confirmed no active `/api/v1` or `/api/v2` controllers/matchers in `src/main`.

## 2. Files Created

- `src/main/java/com/company/scopery/platform/security/SecurityPathPolicy.java`
- `src/main/java/com/company/scopery/platform/security/CorsProperties.java`
- `src/test/java/com/company/scopery/common/constant/ApiPathsTest.java`
- `src/test/java/com/company/scopery/platform/security/CookieUtilTest.java`
- `src/test/java/com/company/scopery/platform/security/SecurityPathPolicyTest.java`
- `src/test/java/com/company/scopery/platform/security/CorsPropertiesTest.java`
- `src/test/java/com/company/scopery/platform/security/HealthEndpointSecurityIT.java`
- `src/test/java/com/company/scopery/platform/security/AuthPathIT.java`
- `src/test/java/com/company/scopery/platform/security/ProtectedEndpointSecurityIT.java`
- `src/test/java/com/company/scopery/platform/security/CsrfSecurityIT.java`
- `src/test/java/com/company/scopery/platform/security/CorsSecurityIT.java`
- `src/test/java/com/company/scopery/platform/security/OldApiPathRegressionIT.java`
- `src/test/java/com/company/scopery/platform/security/PasswordChangeSecurityIT.java`
- `src/test/java/com/company/scopery/platform/security/support/SecurityWebMvcTestConfig.java`
- `src/test/java/com/company/scopery/platform/security/support/SecurityProbeProjectController.java`
- `src/docs/phase-complete/PHASE_01_API_PATH_SECURITY_BASELINE_COMPLETE.md`

## 3. Files Modified

- `src/main/java/com/company/scopery/platform/config/SecurityConfig.java`
- `src/main/resources/application.yml` (scopery.cors)
- `.env.example` (CORS_ALLOWED_ORIGINS)
- `pom.xml` (added `spring-security-test` test dependency)

## 4. Files Deleted

None.

## 5. API Path Changes

No controller path changes. All module `*ApiPaths` already compose from `ApiPaths.BASE_PATH = "/api"`.

Verified no `/api/v1` or `/api/v2` in `src/main`.

## 6. SecurityConfig Changes

Replaced:

- `PUBLIC_PATHS = IAM_AUTH + "/**"`
- CSRF ignore `IAM_AUTH + "/**"`

With explicit permitAll / CSRF-exempt list from `SecurityPathPolicy`:

- `GET /api/health`
- `POST /api/iam/auth/login|refresh|logout`
- `POST /api/iam/auth/password/reset-request|reset-confirm`
- `POST /api/iam/users`
- swagger / actuator health infra paths

## 7. CSRF Changes

CSRF remains enabled (double-submit cookie).

Now CSRF-required (previously accidentally exempt via `auth/**`):

- `POST /api/iam/auth/password/change`
- `POST /api/iam/auth/sessions/revoke-all`

## 7.1 CORS Changes

Added Phase 01 §15 cookie-auth CORS contract:

- `CorsProperties` (`scopery.cors.*`) with explicit allowed origins (never `*`)
- `allowCredentials=true`
- `SecurityConfig.cors(Customizer.withDefaults())` + `CorsConfigurationSource` bean
- Local defaults: `http://localhost:3000`, `http://localhost:5173`, `http://127.0.0.1:5173`
- Env: `CORS_ALLOWED_ORIGINS`, `CORS_ALLOW_CREDENTIALS`, `CORS_MAX_AGE_SECONDS`
- Startup validation rejects `*` when credentials are enabled
- Tests: `CorsPropertiesTest`, `CorsSecurityIT`

## 8. Cookie Changes

None to contract. `CookieUtil` already correct:

- `access_token`: HttpOnly, path `/`
- `refresh_token`: HttpOnly, path `/api/iam/auth`
- clear methods use matching paths

## 9. Public Endpoint Matrix

| Method | Path | Public | CSRF |
|---|---|---|---|
| GET | `/api/health` | Yes | N/A |
| POST | `/api/iam/auth/login` | Yes | Exempt |
| POST | `/api/iam/auth/refresh` | Yes | Exempt |
| POST | `/api/iam/auth/logout` | Yes | Exempt |
| POST | `/api/iam/auth/password/reset-request` | Yes | Exempt |
| POST | `/api/iam/auth/password/reset-confirm` | Yes | Exempt |
| POST | `/api/iam/users` | Yes | Exempt |
| POST | `/api/iam/auth/password/change` | No | Required |
| POST | `/api/iam/auth/sessions/revoke-all` | No | Required |
| * | all other `/api/**` | No | Required for writes |

## 10. Protected Endpoint Verification

- `GET /api/iam/me` without auth → 401 (`ProtectedEndpointSecurityIT`)
- `GET /api/iam/me` with valid access cookie → 200
- Protected POST without CSRF → 403 (`CsrfSecurityIT`, `PasswordChangeSecurityIT`)

## 11. Old Path Regression Result

- `POST /api/v1|v2/iam/auth/login` does not set auth cookies / not successful login
- `GET /api/v1|v2/health` not successful public health (401)

## 12. Tests Added

Unit:

- `ApiPathsTest` (5)
- `CookieUtilTest` (6)
- `SecurityPathPolicyTest` (7)

MockMvc security ITs:

- `HealthEndpointSecurityIT`
- `AuthPathIT`
- `ProtectedEndpointSecurityIT`
- `CsrfSecurityIT`
- `OldApiPathRegressionIT`
- `PasswordChangeSecurityIT`

- `CorsPropertiesTest`
- `CorsSecurityIT`

Phase 01 focused suite: **40 tests, 0 failures**.

## 13. Commands Run

```bash
mvn test -Dtest=ApiPathsTest,CookieUtilTest,SecurityPathPolicyTest,HealthEndpointSecurityIT,AuthPathIT,ProtectedEndpointSecurityIT,CsrfSecurityIT,OldApiPathRegressionIT,PasswordChangeSecurityIT
mvn test
```

## 14. Test Results

Phase 01 suite:

```text
Tests run: 40, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

Full suite result:

```text
Tests run: 599, Failures: 0, Errors: 0, Skipped: 1
BUILD SUCCESS
```

(`ScoperyApplicationTests` remains `@Disabled` — requires PostgreSQL; pre-existing.)

## 15. Manual Verification

Not browser-verified in this change set. Covered by MockMvc contract tests for:

1. Public health
2. Login cookie paths
3. Refresh cookie rotation path
4. Logout clear paths
5. Protected GET 401
6. CSRF blocked / allowed
7. Old `/api/v1|v2` paths not valid APIs

## 16. Event Registry Changes

None (Phase 01 default: do not emit auth events).

## 17. Email Seeder Changes

None.

## 18. Assumptions

1. Frontend uses cookie auth + CSRF double-submit.
2. MockMvc slice tests are sufficient for Phase 01 path/security contract without Testcontainers.
3. Registration `POST /api/iam/users` remains public (existing product decision).
4. Unauthenticated protected POST may return 403 (CSRF) before 401 (auth) depending on filter order — accepted by Phase 01 §22.3.

## 19. Deviations From Prompt

1. Security ITs use `@WebMvcTest` + mocked IAM actions instead of full `@SpringBootTest`/Postgres (repo has no Testcontainers/H2; `ScoperyApplicationTests` is `@Disabled`).
2. Probe controller `SecurityProbeProjectController` used for CSRF write tests instead of real `ProjectController` (avoids heavy project bean graph).
3. WebMvcTest excludes `RateLimitFilter`, `WebMvcConfig`, `AiAgentSecurityInterceptor`, and production `IdempotencyKeyFilter` (Redis/JPA deps); noop idempotency filter provided in test config.

## 20. Known Risks

1. Frontend that previously relied on CSRF-exempt `password/change` or `sessions/revoke-all` must now send CSRF token.
2. MockMvc tests do not prove Redis rate-limit or idempotency filter runtime behavior.
3. OpenAPI `info.version("v1")` is metadata only; not an API path — left unchanged.

## 21. Follow-up Items

1. Optional: add Testcontainers-based full AuthPath IT against real Postgres.
2. Optional: clarify OpenAPI info version string to avoid confusion with path versioning.
3. Continue Phase 02+ without weakening this path/security contract.
