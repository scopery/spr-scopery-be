# PHASE 01 — API Path, Security Baseline, CSRF & Cookie Auth Contract

> Project: Scopery Backend  
> Phase: 01  
> Document type: Implementation-grade phase specification  
> Status: Baseline / regression contract  
> Primary owner: Backend agent  
> Depends on: Phase 00 — Architecture Baseline  
> Current product decision: All backend APIs use `/api` as base path. `/api/v1` and `/api/v2` are intentionally removed unless a later product decision reintroduces versioning.

---

## 0. Why this phase exists

This phase exists to lock the most dangerous cross-cutting backend contract:

```text
API path convention
Spring Security boundary
CSRF behavior
Cookie path behavior
JWT access/refresh behavior
Public endpoint whitelist
Protected endpoint default-deny behavior
Frontend/backend path compatibility
OpenAPI/documentation consistency
Regression tests for authentication and security
```

If this phase is weak, every later module can appear to work during local testing but fail in real browser flows because of:

```text
- Wrong cookie path
- CSRF mismatch
- Public endpoint accidentally protected
- Protected endpoint accidentally public
- Frontend still calling /api/v1 or /api/v2
- Refresh token not sent by browser
- Logout unable to clear cookie
- Security filter matching old paths
- Interceptors still listening to old API paths
```

This phase is therefore a **platform contract**. Later phases must not change it casually.

---

## 1. Phase goal

The goal of Phase 01 is to enforce a single stable API and security baseline:

```text
All API endpoints use /api as base path.
No active endpoint should require /api/v1 or /api/v2.
Security uses explicit public paths only.
All non-public endpoints are authenticated by default.
CSRF behavior is browser-safe and consistent.
Cookie paths allow login, refresh, logout, and protected API access to work.
Tests prove the contract.
```

---

## 2. Current decision

### 2.1 API base path

Use:

```text
/api
```

Do not use:

```text
/api/v1
/api/v2
```

### 2.2 Backward compatibility

Current decision:

```text
No backward compatibility aliases for /api/v1 or /api/v2.
Clients must migrate to /api.
```

If this decision changes in the future, aliases must be implemented intentionally with:

```text
- Deprecation headers
- Documentation
- Tests
- Removal deadline
```

Do not silently restore old paths.

---

## 3. In scope

Phase 01 covers:

```text
1. Global API path constants
2. Module API path constants
3. Spring Security public path whitelist
4. CSRF ignore paths
5. JWT access token cookie contract
6. JWT refresh token cookie contract
7. Auth endpoints
8. Health endpoint
9. Security-related filters/interceptors
10. OpenAPI path consistency
11. Tests for security and auth path behavior
12. Documentation consistency
13. Regression checks against /api/v1 and /api/v2
```

---

## 4. Out of scope

Phase 01 must not implement or redesign:

```text
- IAM role model
- IAM permission/action matrix
- Workspace membership rules
- Project authorization logic
- Per-project IAM resource
- Business module endpoints
- Rate card
- Gantt
- Capacity
- Finance
- Notification engine
- AI agent execution
```

These belong to later phases.

Phase 01 only ensures the platform security path is stable.

---

## 5. Required existing files to inspect before coding

Before modifying anything, the agent must inspect these files or their current equivalents.

```text
CLAUDE.md
CLAUDE.ms
AGENTS.md
Coding_convention.md
README.md
API_SPECIFICATION.md
TECHNICAL.md
common/constant/ApiPaths.java
modules/iam/shared/constant/IamApiPaths.java
modules/workspace/shared/constant/WorkspaceApiPaths.java
modules/project/shared/constant/ProjectApiPaths.java
platform/config/SecurityConfig.java
platform/security/CookieUtil.java
platform/config/WebMvcConfig.java
platform/web/HealthController.java
platform/web/idempotency/IdempotencyKeyFilter.java
modules/iam/user/http/controller/IamAuthController.java
```

If any file name differs, the agent must locate the equivalent file by searching references to:

```text
/api
/api/v1
/api/v2
SecurityFilterChain
csrf
Cookie
refresh_token
access_token
permitAll
requestMatchers
```

The agent must not assume a file does not exist without searching.

---

## 6. Architecture rules for this phase

The agent must follow the project architecture:

```text
http → application → domain
infrastructure → domain
domain must not import JPA
domain must not import Spring Web
controllers return response DTOs, not JPA/domain objects
write use cases use *Action.execute(Command)
read use cases use *QueryService
all errors use ErrorCatalog/AppException convention
all endpoints return ApiResponse<T> unless existing convention says otherwise
```

Security config and platform filters may live in platform/common packages according to existing codebase convention.

---

## 7. API path constant contract

### 7.1 Global API path constant

The global path constant must be the single source of truth.

Expected concept:

```java
public final class ApiPaths {
    public static final String BASE_PATH = "/api";

    public static final String HEALTH = BASE_PATH + "/health";
    public static final String IAM = BASE_PATH + "/iam";
    public static final String IAM_AUTH = IAM + "/auth";
    public static final String IAM_USERS = IAM + "/users";

    private ApiPaths() {}
}
```

The exact file/class names may follow existing convention, but the concept must be preserved.

### 7.2 Forbidden global constants

Do not keep active constants like:

```java
public static final String V1 = "/v1";
public static final String V2 = "/v2";
public static final String BASE_V1 = "/api/v1";
public static final String BASE_V2 = "/api/v2";
```

Exception: old constants may exist only in a deprecated compatibility layer if the product later decides to support aliases. Current decision is no aliases.

### 7.3 Module path constants

Every module must compose paths from `ApiPaths.BASE_PATH`.

Examples:

```java
public final class IamApiPaths {
    public static final String BASE = ApiPaths.BASE_PATH + "/iam";
    public static final String AUTH = BASE + "/auth";
}
```

```java
public final class WorkspaceApiPaths {
    public static final String BASE_WORKSPACES = ApiPaths.BASE_PATH + "/workspaces";
    public static final String BASE_ORGANIZATIONS = ApiPaths.BASE_PATH + "/organizations";
}
```

```java
public final class ProjectApiPaths {
    public static final String PROJECTS = ApiPaths.BASE_PATH + "/projects";
    public static final String PHASE_DEFINITIONS = ApiPaths.BASE_PATH + "/phase-definitions";
}
```

### 7.4 Controller mapping rule

Controllers should use module path constants, not string literals.

Preferred:

```java
@RequestMapping(ProjectApiPaths.PROJECTS)
```

Avoid:

```java
@RequestMapping("/api/projects")
@RequestMapping("/api/v1/projects")
```

### 7.5 Search rule

Before completing this phase, the agent must grep/search for old paths:

```bash
grep -R '"/api/v1' src/main src/test docs || true
grep -R '"/api/v2' src/main src/test docs || true
grep -R "/api/v1" src/main src/test docs || true
grep -R "/api/v2" src/main src/test docs || true
```

Remaining matches are allowed only if they appear in:

```text
- migration notes
- historical documentation
- explicit compatibility tests proving old paths are removed
- comments explaining intentional removal
```

They must not appear in active controllers, filters, security matchers, cookie paths, OpenAPI config, frontend examples, or integration tests.

---

## 8. Public endpoint contract

Only explicitly listed endpoints may be public.

### 8.1 Required public endpoints

| Method | Path | Purpose | CSRF |
|---|---|---|---|
| GET | `/api/health` | Health check | Not relevant |
| POST | `/api/iam/auth/login` | Login | Exempt |
| POST | `/api/iam/auth/refresh` | Refresh token rotation | Exempt |
| POST | `/api/iam/auth/logout` | Logout | Exempt or accepted by existing auth convention |
| POST | `/api/iam/users` | User registration | Depends on existing product decision; usually exempt/public |
| POST | `/api/iam/auth/password/reset-request` | Request password reset | Exempt/public |
| POST | `/api/iam/auth/password/reset-confirm` | Confirm password reset | Exempt/public |

If registration is not public in the product, document that and adjust tests.

### 8.2 Forbidden public matcher

Never use:

```java
.requestMatchers("/api/**").permitAll()
```

This is a critical security bug.

### 8.3 Acceptable public matcher pattern

Use explicit constants:

```java
.requestMatchers(HttpMethod.GET, ApiPaths.HEALTH).permitAll()
.requestMatchers(ApiPaths.IAM_AUTH + "/**").permitAll()
.requestMatchers(HttpMethod.POST, ApiPaths.IAM_USERS).permitAll()
.anyRequest().authenticated()
```

Actual syntax may differ depending on existing security configuration, but the behavior must match.

---

## 9. Protected endpoint contract

All non-public API endpoints must require authentication by default.

Examples that must not be public:

```text
GET /api/iam/me
GET /api/workspaces
POST /api/workspaces
GET /api/projects
POST /api/projects
POST /api/projects/{projectId}/tasks
GET /api/ai-agent/providers
POST /api/ai-agent/providers
GET /api/notification/email-templates
POST /api/notification/email-templates
```

Expected unauthenticated behavior:

```text
401 Unauthorized
```

Expected authenticated-but-unauthorized behavior:

```text
403 Forbidden
```

The distinction depends on endpoint and authorization layer.

---

## 10. CSRF contract

### 10.1 Why CSRF matters

The backend uses browser cookies. If cookies are used for authentication, CSRF protection must be consistent.

The risk:

```text
If CSRF is disabled globally, malicious sites may trigger state-changing requests using the user's cookies.
If CSRF is enabled but auth endpoints are not exempt, login/refresh/logout may fail.
```

### 10.2 Required behavior

Auth endpoints that cannot have a CSRF token before login must be CSRF-exempt:

```text
/api/iam/auth/**
```

Password reset endpoints are also usually CSRF-exempt if public.

Protected write endpoints must require CSRF token when using cookie auth.

Example:

```text
POST /api/projects without CSRF → 401/403/CSRF blocked
POST /api/projects with valid cookies + valid CSRF → reaches controller/business validation
```

The exact error status may be 401 or 403 depending on Spring Security order. The important rule is:

```text
Protected POST without valid auth/CSRF must not execute business action.
```

### 10.3 Forbidden CSRF shortcut

Do not do:

```java
csrf.disable()
```

unless the entire product explicitly switches to non-cookie bearer token auth. Current contract uses cookies, so do not disable CSRF globally.

### 10.4 CSRF ignored matcher

Expected concept:

```java
.csrf(csrf -> csrf
    .ignoringRequestMatchers(ApiPaths.IAM_AUTH + "/**")
)
```

Do not leave:

```java
.ignoringRequestMatchers("/api/v1/iam/auth/**")
```

---

## 11. Cookie auth contract

### 11.1 Cookie names

| Cookie | Purpose |
|---|---|
| `access_token` | Short-lived access JWT |
| `refresh_token` | Long-lived refresh JWT/token |

If the existing code uses different names, document the actual names and keep them consistent.

### 11.2 Required cookie settings

| Cookie | Path | HttpOnly | SameSite | Secure |
|---|---|---:|---|---|
| `access_token` | `/` | true | Strict or existing convention | from config |
| `refresh_token` | `/api/iam/auth` | true | Strict or existing convention | from config |

### 11.3 Why refresh token path matters

Browser sends a cookie only when the request path matches the cookie path.

If `refresh_token` path is:

```text
/api/v1/iam/auth
```

but refresh endpoint is:

```text
/api/iam/auth/refresh
```

then browser will not send the refresh token.

Result:

```text
login works
access token expires
refresh fails
user is logged out unexpectedly
```

### 11.4 Logout must clear cookies correctly

Logout must clear cookies using the same path used when setting them.

If refresh cookie is set with:

```text
Path=/api/iam/auth
```

then logout must clear it with:

```text
Path=/api/iam/auth
Max-Age=0
```

If paths differ, browser may keep the old cookie.

### 11.5 Cookie utility requirement

Cookie path must use constants, not literals.

Preferred:

```java
CookieUtil.createRefreshCookie(..., ApiPaths.IAM_AUTH)
```

Avoid:

```java
"/api/v1/iam/auth"
"/api/iam/auth"
```

The second is not logically wrong, but constants prevent drift.

---

## 12. JWT token behavior contract

### 12.1 Login

Endpoint:

```text
POST /api/iam/auth/login
```

Input:

```json
{
  "usernameOrEmail": "user@example.com",
  "password": "secret"
}
```

Expected success:

```text
200 OK
Set-Cookie: access_token=...
Set-Cookie: refresh_token=...
body: ApiResponse with user/session info according to existing convention
```

Business/security rules:

```text
- Unknown user must not leak whether username/email exists.
- Wrong password must return generic invalid credentials.
- Suspended/inactive user cannot login.
- Successful login issues access and refresh token.
```

### 12.2 Refresh

Endpoint:

```text
POST /api/iam/auth/refresh
```

Expected behavior:

```text
- Browser sends refresh_token cookie because path matches /api/iam/auth.
- Refresh token validates.
- Old refresh token is revoked.
- New access token and refresh token are issued.
- Cookie paths remain correct.
```

### 12.3 Logout

Endpoint:

```text
POST /api/iam/auth/logout
```

Expected behavior:

```text
- Provided refresh token is revoked.
- access_token cookie cleared.
- refresh_token cookie cleared using correct path.
```

### 12.4 Revoke all sessions

Endpoint should follow existing IAM convention. If present:

```text
POST /api/iam/auth/sessions/revoke-all
```

Expected behavior:

```text
- Current user required.
- All refresh tokens for current user revoked.
```

---

## 13. Security filters and interceptors

Phase 01 must review path-sensitive filters/interceptors.

### 13.1 Required review targets

```text
JwtAuthenticationFilter
IdempotencyKeyFilter
DeprecationHeaderFilter
WebMvcConfig interceptors
AI Agent interceptors
CORS config
CSRF config
Exception handling entry points
```

### 13.2 Idempotency filter

Expected behavior:

```text
Apply to /api/** POST requests only when Idempotency-Key header is present.
Do not break auth endpoints unless existing product requires idempotency there.
Do not apply to non-API static resources.
```

### 13.3 AI Agent interceptor

If AI Agent has interceptors, path must be:

```text
/api/ai-agent/**
```

not:

```text
/api/v1/ai-agent/**
/api/v2/ai-agent/**
```

### 13.4 Deprecated path filter

If `/api/v1` and `/api/v2` are intentionally removed, old deprecation header filters may be removed.

If old paths are kept as aliases, deprecation header filter must be explicit and tested.

---

## 14. OpenAPI / Swagger contract

OpenAPI must show `/api` paths only.

### 14.1 Must not show

```text
/api/v1/...
/api/v2/...
```

### 14.2 Must show examples

```text
/api/health
/api/iam/auth/login
/api/iam/auth/refresh
/api/iam/users
/api/projects
/api/workspaces
/api/ai-agent/providers
```

### 14.3 Documentation files to update

```text
README.md
API_SPECIFICATION.md
TECHNICAL.md
api-samples.md
AGENTS.md
CLAUDE.md
Postman collections
frontend environment examples
```

Do not leave docs telling frontend to call `/api/v1`.

---

## 15. CORS contract

If frontend is served from a different origin, CORS must allow the correct origin and credentials.

### 15.1 Required behavior for cookie auth

```text
Access-Control-Allow-Credentials: true
Allowed origins must be explicit, not "*" when credentials are used.
```

### 15.2 Forbidden

```text
allowedOrigins("*")
allowCredentials(true)
```

This is invalid/insecure for credentialed requests.

### 15.3 Local dev examples

Allowed local origins may include:

```text
http://localhost:3000
http://localhost:5173
http://127.0.0.1:5173
```

Use config properties, not hard-coded production assumptions.

---

## 16. Frontend compatibility contract

Frontend must call `/api`, not `/api/v1` or `/api/v2`.

### 16.1 Required frontend base

```text
VITE_API_BASE_URL=http://localhost:<port>/api
```

or if using proxy:

```text
/api
```

### 16.2 Avoid double `/api`

If frontend base URL is already `/api`, frontend service paths should be:

```text
/iam/auth/login
/projects
```

not:

```text
/api/iam/auth/login
```

Otherwise it may call:

```text
/api/api/iam/auth/login
```

This phase is backend-focused, but the backend docs must warn about this.

---

## 17. Entities affected

Phase 01 does not introduce business entities.

It affects platform/auth entities indirectly:

| Entity | Module | Impact |
|---|---|---|
| `IamUser` | iam | Login, refresh, logout, password reset |
| Refresh token entity/table if present | iam/platform | Cookie/session lifecycle |
| Audit/activity log if auth logs exist | platform/iam | Login/logout events if implemented |
| `IamAuthResource` | iam | Protected resource access after authentication |

No new database table is required unless current code lacks refresh token persistence and the product requires it.

---

## 18. Database migration requirement

Phase 01 normally should not need a migration.

If the codebase currently stores API path strings in DB, then a migration may be required for:

```text
IamAuthResource codes/paths
EventDefinition source path metadata
Notification template links
Webhook callback path config
Stored API samples
```

Before adding a migration, the agent must prove the DB actually stores old paths.

Do not create speculative migrations.

---

## 19. Business rules

### BR-01 — Single API base

All active API endpoints must use `/api`.

### BR-02 — No silent old version usage

No active controller, filter, or security matcher may depend on `/api/v1` or `/api/v2`.

### BR-03 — Explicit public endpoints only

Only explicitly whitelisted endpoints may be public.

### BR-04 — Default authentication

All non-public endpoints require authentication.

### BR-05 — Auth endpoints CSRF exempt

`/api/iam/auth/**` must be CSRF-exempt.

### BR-06 — Protected writes require CSRF

State-changing protected requests using cookie auth must require CSRF.

### BR-07 — Refresh cookie path must match refresh endpoint family

`refresh_token` cookie path must be `/api/iam/auth`.

### BR-08 — Logout clears cookies with correct path

Logout must clear cookies using the same path and relevant attributes.

### BR-09 — No global CSRF disable

Do not disable CSRF globally while using cookie auth.

### BR-10 — No global permitAll

Do not permit all `/api/**`.

### BR-11 — Documentation must match runtime

API docs and examples must use `/api`.

### BR-12 — Tests prove behavior

The phase is incomplete without passing tests proving auth path and security behavior.

---

## 20. Constraints

### 20.1 Technical constraints

```text
- Java 21
- Spring Boot 3.4.x
- Spring Security
- Cookie-based auth
- CSRF enabled for protected browser writes
- Modular monolith
- API path constants instead of string duplication
```

### 20.2 Product constraints

```text
- No /api/v1 or /api/v2 active support
- Browser clients are first-class clients
- Future module endpoints must inherit this path contract
```

### 20.3 Safety constraints

```text
- Never open all APIs to public
- Never disable CSRF globally
- Never store raw tokens in logs
- Never expose refresh token in response body
- Never leak user existence on login/password reset
```

---

## 21. Assumptions

If not contradicted by existing code, assume:

```text
1. Frontend uses cookies for auth.
2. access_token is HttpOnly cookie.
3. refresh_token is HttpOnly cookie.
4. CSRF protection is required for browser write requests.
5. Login/refresh/logout are intentionally CSRF-exempt.
6. `/api/v1` and `/api/v2` are intentionally removed.
7. Existing IAM login/refresh business rules are already implemented in IAM module.
8. This phase only aligns path/security behavior, not IAM domain logic.
```

If any assumption is false, the agent must document it in the phase completion file.

---

## 22. Error behavior expectations

### 22.1 Unauthenticated protected request

```text
GET /api/iam/me without cookie
→ 401 Unauthorized
```

### 22.2 Authenticated but unauthorized request

```text
POST /api/projects with login but missing PROJECT_CREATE
→ 403 Forbidden
```

Actual project authorization belongs to Project phase, but Phase 01 must ensure 403 flow works globally.

### 22.3 Missing CSRF on protected POST

```text
POST /api/projects with cookies but without CSRF
→ 401 or 403 depending on security order
```

The action must not execute.

### 22.4 Invalid login

```text
POST /api/iam/auth/login with wrong password
→ generic invalid credentials
```

Do not reveal whether username/email exists.

### 22.5 Old path

```text
POST /api/v1/iam/auth/login
→ 404 or 401/403, but must not behave as valid login endpoint
```

Preferred:

```text
404 Not Found
```

---

## 23. Required tests

This section is mandatory. Phase 01 is not complete without tests.

### 23.1 Unit tests

#### ApiPathsTest

Expected tests:

```text
basePath_isApi
iamAuthPath_isApiIamAuth
healthPath_isApiHealth
noPublicConstantContainsApiV1
noPublicConstantContainsApiV2
```

#### CookieUtilTest

Expected tests:

```text
refreshCookie_pathIsApiIamAuth
refreshCookie_isHttpOnly
refreshCookie_usesConfiguredSecureFlag
clearRefreshCookie_usesSamePath
accessCookie_pathIsRoot
clearAccessCookie_usesRootPath
```

#### SecurityPublicPathTest

Expected tests:

```text
publicPaths_doNotContainApiWildcard
publicPaths_includeHealth
publicPaths_includeIamAuth
csrfIgnoredPaths_includeIamAuth
csrfIgnoredPaths_doNotContainOldV1Auth
```

If SecurityConfig is hard to unit test, use integration tests.

### 23.2 Integration tests

#### HealthEndpointSecurityIT

```text
GET /api/health without auth → 200
```

#### AuthPathIT

```text
POST /api/iam/auth/login valid credentials → 200 + sets cookies
POST /api/iam/auth/login invalid credentials → generic failure
POST /api/iam/auth/refresh with valid refresh cookie → 200 + rotates cookies
POST /api/iam/auth/logout with refresh cookie → 200 + clears cookies
```

#### ProtectedEndpointSecurityIT

```text
GET /api/iam/me without auth → 401
GET /api/iam/me after login → 200
GET /api/projects without auth → 401
```

#### CsrfSecurityIT

```text
POST /api/projects without auth → blocked
POST /api/projects with auth but no CSRF → blocked before action
POST /api/projects with auth + CSRF → reaches controller; may return validation error if payload invalid
```

#### OldApiPathRegressionIT

```text
POST /api/v1/iam/auth/login → not successful login
POST /api/v2/iam/auth/login → not successful login
GET /api/v1/health → not successful health
GET /api/v2/health → not successful health
```

### 23.3 Static regression tests

A test or script must ensure active source has no old path usage.

Example command:

```bash
grep -R "/api/v1" src/main src/test docs || true
grep -R "/api/v2" src/main src/test docs || true
```

If matches remain, list them and justify.

---

## 24. Manual verification checklist

The phase completion file must include manual verification for:

```text
1. GET /api/health returns 200 public
2. POST /api/iam/auth/login returns 200 and sets cookies
3. refresh_token cookie path is /api/iam/auth
4. access_token cookie path is /
5. POST /api/iam/auth/refresh succeeds using browser cookies
6. POST /api/iam/auth/logout clears cookies
7. GET /api/iam/me without auth returns 401
8. GET /api/iam/me after login returns 200
9. POST protected endpoint without CSRF does not execute action
10. POST protected endpoint with CSRF reaches controller
11. /api/v1 path no longer acts as valid API
12. Swagger shows /api paths
```

---

## 25. Event registry requirements

Phase 01 does not need to create new business events unless auth events are part of the product.

If auth event registry is implemented or required, seed these event definitions:

| Event Code | Source System | Event Key | When emitted | Payload |
|---|---|---|---|---|
| `IAM_USER_LOGGED_IN` | `SCOPERY_IAM` | `user.logged_in` | Successful login | userId, username, occurredAt, ipHash |
| `IAM_USER_LOGGED_OUT` | `SCOPERY_IAM` | `user.logged_out` | Logout | userId, occurredAt |
| `IAM_REFRESH_TOKEN_ROTATED` | `SCOPERY_IAM` | `refresh_token.rotated` | Refresh success | userId, sessionId, occurredAt |
| `IAM_LOGIN_FAILED` | `SCOPERY_IAM` | `login.failed` | Failed login | usernameHash, reasonCategory, occurredAt |

Default decision for Phase 01:

```text
Do not emit auth events unless current event registry pattern already supports security events safely.
Do not include raw username, password, token, IP address, or user agent unless privacy policy allows it.
```

If emitted, payload must avoid secrets and PII leakage.

---

## 26. Notification/email seeder requirements

Phase 01 usually does not create user-facing emails except password reset if not already implemented.

If password reset templates are missing and this phase touches password reset routes, seed:

### Template: Password Reset Request

| Field | Value |
|---|---|
| Code | `IAM_PASSWORD_RESET_REQUEST` |
| Scope | SYSTEM |
| Event | `IAM_PASSWORD_RESET_REQUESTED` |
| Subject | `Reset your Scopery password` |

Required variables:

```text
user.fullName
reset.url
reset.expiresAt
```

Rules:

```text
- Email must not reveal whether request came from valid account in public response.
- Email only enqueued if user exists.
- Reset link must not be logged.
```

However, if password reset email already exists, do not duplicate templates.

---

## 27. Seeder requirements

### 27.1 Required seeders

Phase 01 should ensure or preserve seeders for:

```text
- IAM system permissions/actions required by existing modules
- Owner policies required for workspace/org bootstrap
- System auth resources if existing architecture requires them
```

### 27.2 Not allowed

Do not create random demo users, demo passwords, or hard-coded admin accounts in Phase 01 unless existing local-dev seed convention already allows it.

### 27.3 Idempotency

All seeders must be idempotent:

```text
Running app twice must not fail.
Running migration + seed on clean DB must pass.
Existing records must not duplicate.
```

---

## 28. API endpoint list to verify

The agent must verify at least these endpoint families after path migration.

### 28.1 IAM

```text
POST /api/iam/users
POST /api/iam/auth/login
POST /api/iam/auth/refresh
POST /api/iam/auth/logout
POST /api/iam/auth/password/change
POST /api/iam/auth/password/reset-request
POST /api/iam/auth/password/reset-confirm
GET  /api/iam/me
```

### 28.2 Workspace

```text
POST /api/organizations
GET  /api/organizations
POST /api/workspaces
GET  /api/workspaces
POST /api/workspaces/{id}/members
```

### 28.3 Project

```text
POST /api/projects
GET  /api/projects
GET  /api/projects/{id}
```

### 28.4 AI Agent

```text
GET  /api/ai-agent/providers
POST /api/ai-agent/providers
```

### 28.5 Event Registry

```text
GET  /api/event-definitions
POST /api/event-definitions
```

### 28.6 Notification

```text
GET  /api/notification/email-templates
POST /api/notification/email-templates
```

---

## 29. Acceptance criteria

Phase 01 is accepted only if all are true:

```text
1. All active API paths use /api.
2. No active controller uses /api/v1 or /api/v2.
3. No active security matcher depends on /api/v1 or /api/v2.
4. No active cookie path uses /api/v1 or /api/v2.
5. No active interceptor/filter depends on old paths.
6. Public endpoints are explicitly listed.
7. No /api/** permitAll wildcard exists.
8. CSRF is not disabled globally.
9. /api/iam/auth/** is CSRF-exempt.
10. Protected POST endpoints require CSRF.
11. refresh_token path is /api/iam/auth.
12. logout clears refresh_token with /api/iam/auth path.
13. Swagger/OpenAPI shows /api paths.
14. Documentation examples use /api.
15. mvn compile passes.
16. mvn test passes.
17. Manual verification checklist is complete.
18. Phase complete file is created.
```

---

## 30. Required phase completion file

After implementation, the agent must create:

```text
docs/phase-complete/PHASE_01_API_PATH_SECURITY_BASELINE_COMPLETE.md
```

The file must include:

```text
# Phase 01 — API Path & Security Baseline Complete

## 1. Summary
## 2. Files Created
## 3. Files Modified
## 4. Files Deleted
## 5. API Path Changes
## 6. SecurityConfig Changes
## 7. CSRF Changes
## 8. Cookie Changes
## 9. Public Endpoint Matrix
## 10. Protected Endpoint Verification
## 11. Old Path Regression Result
## 12. Tests Added
## 13. Commands Run
## 14. Test Results
## 15. Manual Verification
## 16. Event Registry Changes
## 17. Email Seeder Changes
## 18. Assumptions
## 19. Deviations From Prompt
## 20. Known Risks
## 21. Follow-up Items
```

The agent must not write "complete" if:

```text
- mvn test fails
- any old active /api/v1 or /api/v2 remains
- refresh token cookie path is wrong
- /api/** is public
- CSRF is globally disabled
```

---

## 31. Anti-bịa rules

The agent must obey:

```text
1. Do not invent files that do not exist.
2. Do not claim tests pass unless commands were run.
3. Do not claim manual browser verification unless actually verified.
4. Do not silently change product decision.
5. Do not add /api/v1 aliases unless explicitly requested.
6. Do not modify unrelated business modules except path constants and references.
7. Do not rewrite IAM business logic in this phase.
8. Do not disable security to make tests pass.
9. Do not change cookie SameSite/Secure convention without documenting impact.
10. Do not expose tokens in logs or response bodies.
```

---

## 32. Suggested agent prompt for this phase

Use this prompt when assigning Phase 01 to a coding agent:

```text
You are implementing Phase 01 — API Path, Security Baseline, CSRF & Cookie Auth Contract.

Before coding, read:
- CLAUDE.md / CLAUDE.ms
- AGENTS.md
- Coding_convention.md
- common/constant/ApiPaths.java
- platform/config/SecurityConfig.java
- platform/security/CookieUtil.java
- modules/iam/shared/constant/IamApiPaths.java
- all controller path constants
- all security filters/interceptors

Product decision:
- All API endpoints must use /api.
- /api/v1 and /api/v2 are intentionally removed.
- No backward compatibility aliases unless explicitly requested.

Implement/fix:
1. Centralize all API base paths through ApiPaths.BASE_PATH = "/api".
2. Ensure module ApiPaths classes compose from ApiPaths.BASE_PATH.
3. Ensure SecurityConfig uses explicit public endpoint list only.
4. Do not use /api/** permitAll.
5. Do not disable CSRF globally.
6. Ensure /api/iam/auth/** is CSRF-exempt.
7. Ensure refresh_token cookie path is /api/iam/auth.
8. Ensure logout clears cookies with correct path.
9. Update filters/interceptors from old paths to /api.
10. Update docs/tests/examples from /api/v1 or /api/v2 to /api.
11. Add tests listed in PHASE_01 spec.
12. Run mvn compile and mvn test.
13. Create docs/phase-complete/PHASE_01_API_PATH_SECURITY_BASELINE_COMPLETE.md.

Do not implement business features in this phase.
Do not modify unrelated domain logic.
Document every deviation and assumption.
```

---

## 33. Phase 01 quick checklist

```text
[ ] ApiPaths.BASE_PATH = /api
[ ] No active /api/v1
[ ] No active /api/v2
[ ] /api/health public
[ ] /api/iam/auth/** public
[ ] /api/iam/auth/** CSRF-exempt
[ ] /api/iam/users public if registration is public
[ ] no /api/** permitAll
[ ] CSRF not disabled globally
[ ] access_token path /
[ ] refresh_token path /api/iam/auth
[ ] logout clears cookies correctly
[ ] protected GET without auth → 401
[ ] protected POST without CSRF blocked
[ ] login works
[ ] refresh works
[ ] logout works
[ ] OpenAPI shows /api
[ ] mvn compile PASS
[ ] mvn test PASS
[ ] phase complete file created
```

---

## 34. Final note

This phase is not about adding features. It is about preventing platform instability.

A project can survive missing business features. It cannot survive an unstable auth/security path contract.

Every future phase must inherit this rule:

```text
Use /api.
Use constants.
Explicit public paths only.
Default protected.
Do not weaken CSRF.
Do not touch auth path behavior unless the phase explicitly requires it.
```
