# IAM Permission Coverage — Phase 23

> Equivalent path: `src/docs/security/IAM_PERMISSION_COVERAGE.md`
> Status: **PARTIALLY_IMPLEMENTED**

## Pattern in use

Module-scoped `*AuthorizationService` wrappers call `ProjectAuthorizationService.requireProjectPermission` / workspace IAM integration with `IamAuthorities` constants. Examples:

- `ReportingAuthorizationService`
- `ProjectFinanceAuthorizationService`
- `QuoteAuthorizationService`
- `AiPlanningAuthorizationService`
- `ProjectBaselineAuthorizationService`
- `ProjectNotificationAuthorizationService`

System rights for AI Agent use `IamSystemAuthorizationService` / `AiAgentSecurityInterceptor`.

## Coverage assessment

| Area | Classification | Notes |
|---|---|---|
| Project-scoped modules (finance, quote, reporting, baseline, AI planning, notifications) | CURRENTLY_IMPLEMENTED (pattern) | Action-layer require* before mutate/read |
| Workspace/org IAM | CURRENTLY_IMPLEMENTED | Phase 02–03 + rate card workspace checks |
| Exhaustive endpoint × right matrix | PARTIALLY_IMPLEMENTED | No generated matrix from all controllers |
| Seed completeness vs new Phase 22–25 rights | PARTIALLY_IMPLEMENTED | Continuous review required |
| MFA / SSO | DEFERRED | Phase 12.1 / not core BE blocker |

## Gaps

1. No machine-readable matrix of every HTTP method → `IamAuthorities` constant.
2. Reporting dashboard Map payloads still rely on runtime masking rather than field-level DTO contracts.
3. IDOR (cross-tenant object access) is separate — see `IDOR_PATH_OWNERSHIP_AUDIT.md`.

## Required follow-up

- Generate IAM coverage matrix from controllers + authorization call sites.
- Add integration tests that assert 403 when right is missing (not only 401 unauthenticated).
