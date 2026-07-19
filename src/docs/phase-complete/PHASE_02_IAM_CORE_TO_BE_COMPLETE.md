# Phase 02 — IAM Core TO-BE Complete

## 1. Summary

Phase 02 hardens and verifies IAM & Identity Core against `PHASE_02_IAM_CORE_TO_BE_DETAILED.md` without rebuilding working modules. Auth flows, grants, owner policies, event/email seeders, and key §18 tests are in place. Refresh tokens remain Redis-backed as an **intentional deviation** from the DB-table TO-BE (session viewer / per-token audit deferred to Phase 23).

## 2. Source Inputs Reviewed

- `PHASE_00_MASTER_ROADMAP_MODULAR_REVISED.md`
- `PHASE_01_API_PATH_SECURITY_BASELINE_DETAILED.md` + Phase 01 completion
- `PHASE_02_IAM_CORE_TO_BE_DETAILED.md` (§12–§20)
- `CLAUDE.md` / `AGENTS.md` coding conventions
- Existing IAM, workspace, notification, event-registry code and migrations (through V40)

## 3. Current vs TO-BE Classification Matrix

| Capability | Classification | Notes |
|---|---|---|
| User CRUD + ACTIVE/INACTIVE/SUSPENDED | `CURRENTLY_IMPLEMENTED` | Verified + security fields |
| Login / Logout / Refresh / RevokeAll / ChangePassword / ResetPassword | `CURRENTLY_IMPLEMENTED` + hardened | Tests in §19 |
| Password reset token hashing | `MUST_IMPLEMENT_IN_PHASE_02` → done | SHA-256 in Redis |
| Inactive login no account leak | `MUST_IMPLEMENT_IN_PHASE_02` → done | Generic `INVALID_CREDENTIALS` |
| User security fields | `MUST_IMPLEMENT_IN_PHASE_02` → done | V40 |
| Refresh token storage | `INTENTIONAL_DEVIATION` | Redis Set vs DB table §12.2 |
| Session viewer / per-token audit / replaced-by chain | `DEFERRED_TO_PHASE_23` | Requires DB refresh-token model |
| AuthResource + lifecycle sync helper | `CURRENTLY_IMPLEMENTED` + hardened | `IamAuthResourceLifecycleService` |
| Permission/action/right catalog | `CURRENTLY_IMPLEMENTED` | Right catalog seeder ≥24 |
| Grants + expiresAt + cross-org reject | `MUST_IMPLEMENT_IN_PHASE_02` → done | Create + delegate tests |
| OwnerPolicy ORGANIZATION/WORKSPACE/TEAM | `CURRENTLY_IMPLEMENTED` + idempotent ensure | V36 + ApplicationReady seeder |
| Delegation rules | `CURRENTLY_IMPLEMENTED` | §18 delegate tests |
| Authorization DENY > ALLOW | `CURRENTLY_IMPLEMENTED` | Incl. team DENY vs user ALLOW |
| IAM events §15.1/15.2 | `MUST_IMPLEMENT_IN_PHASE_02` → done | `SCOPERY_IAM` seeder |
| Password-reset email template | `MUST_IMPLEMENT_IN_PHASE_02` → done | `IAM_PASSWORD_RESET_REQUEST_EMAIL` |
| Email verification flow | `DEFERRED_TO_PHASE_03` / 23 | Column exists; flow not built |
| MFA / SSO / social | `DEFERRED_TO_PHASE_23` | — |
| Service accounts | `DEFERRED_TO_PHASE_21` / 37 | — |
| API tokens | `DEFERRED_TO_PHASE_37` | — |
| Conditional access | `DEFERRED_TO_PHASE_23` / 38 | — |
| Access request / review / SoD | `DEFERRED_TO_PHASE_32` / 38 / 18 | — |
| AI act-on-behalf | `DEFERRED_TO_PHASE_21` | — |
| Finance / quote / report IAM | Seed-only or deferred | Phases 17 / 18 / 22 |

## 4. Implemented in Current BE (pre–Phase 02)

- IAM user, role, right, permission, grant, role-assignment, owner-policy, authorization engine
- JWT access + Redis refresh rotate/revoke/revoke-all
- Cookie auth paths from Phase 01
- Workspace/org IAM bootstrap
- Immutable audit on deny / grant create / delegation reject

## 5. Implemented / Hardened in This Phase

- Hashed password-reset tokens; generic inactive login
- `IamUser` security fields (`registrationSource`, `passwordChangedAt`, `passwordResetRequired`, `failedLoginCount`, `lastLoginAt`, `lastLoginFailedAt`, `emailVerified`)
- Create-grant `expiresAt` + tenant subject validation
- Outbox emit: `IAM_USER_LOGGED_IN`, `IAM_LOGIN_FAILED`, `IAM_PASSWORD_RESET_REQUESTED` (safe payload), `IAM_PASSWORD_RESET_COMPLETED`
- Email trigger for password reset (nested payload; raw token only in `reset.url`)
- `IamEventDefinitionSeedInitializer`, password-reset email template/rule
- `IamOwnerPolicyCatalogInitializer`, `IamAuthResourceLifecycleService`
- Gap-close §18 tests (auth + delegation + team DENY + createGrant cross-org)

## 6. Seed-only Items Added

- Full IAM identity + policy event codes under source system `SCOPERY_IAM` (definitions + password-reset variables)
- `IAM_PASSWORD_RESET_REQUEST_EMAIL` system template + rule (`EVENT_TARGET_USER`)
- Owner policy ensure if V36 rows missing (no duplicate when active exists)

Future module permissions (finance/quote/RAID/etc.) remain seed-when-module-arrives; not falsely claimed as runtime features.

## 7. Deferred Items and Target Phase

| Item | Target |
|---|---|
| DB refresh_token table, token_hash, replaced_by, user_agent_hash, ip_hash, revoked_at | Phase 23 |
| Session viewer UI APIs / per-token audit trail | Phase 23 |
| MFA / SSO / social login | Phase 23 |
| Email verification end-to-end | Phase 03 / 23 |
| API tokens | Phase 37 |
| Service accounts | Phase 21 / 37 |
| Access request workflow | Phase 32 |
| Access review / SoD | Phase 38 / 18 |
| Conditional access policies | Phase 23 / 38 |
| AI acting-on-behalf IAM | Phase 21 |
| Finance / quote / report permission productization | Phases 17 / 18 / 22 |
| Full outbox fan-out for every IAM event → notification | Notification engine follow-up |

### Deferred tests (do not add failing tests now)

```text
MFA tests deferred to Phase 23
SSO tests deferred to Phase 23
API token tests deferred to Phase 37
Service account tests deferred to Phase 21/37
Finance permission tests deferred to Phase 17
Quote SoD tests deferred to Phase 18
AI acting-on-behalf tests deferred to Phase 21
Report access tests deferred to Phase 22
```

## 8. Entities Created / Modified

- **Created:** `IamRegistrationSource`; `IamEventDefinitionSeedInitializer`; `IamOwnerPolicyCatalogInitializer`; `IamAuthResourceLifecycleService`
- **Modified:** `IamUser` (+ JPA/mapper); `PasswordResetTokenService`; `LoginAction`; `RequestPasswordResetAction`; `ConfirmPasswordResetAction`; `CreateIamAccessGrantAction` (+ request/command); `SystemEmailTemplateInitializer`
- **Migration:** `V40__alter_iam_user_add_security_fields.sql`

## 9. APIs Created / Modified

No new public path prefixes. Existing `/api/iam/auth/**` and grant create accept optional `expiresAt`. Behavior hardened only (login leak, reset hashing/email, grant tenant checks).

## 10. Business Rules Implemented

- Username/email unique; password hashed; hash never returned
- Status gates login without status leak
- Reset request always outward-success; token hashed at rest
- Refresh rotate; revoked/expired refresh rejected
- Password change/reset revokes all sessions
- Expired/revoked grants ineffective; DENY overrides ALLOW (incl. team DENY vs user ALLOW)
- Cross-org grant/delegate subjects rejected by default
- Delegation requires `canDelegate`, depth, and covered action

## 11. Business Rules Deferred

- Refresh token replaced-by chain and device/IP binding
- MFA step-up, SSO assertion mapping
- Federation / cross-org grants
- SoD conflict detection on approve paths
- Conditional/ABAC policies beyond grant conditions JSON

## 12. IAM Resource Types Supported Now

`ORGANIZATION`, `WORKSPACE`, `TEAM` (+ existing project/document-type integrations where already bootstrapped)

## 13. IAM Resource Types Deferred

Project deep types, document hub, finance, quote, RAID, meeting, portal, connector resources — return with their module phases.

## 14. Permission/Action Seeder Matrix

| Area | Status |
|---|---|
| Core org/workspace/team/IAM permissions | Present (migrations + right catalog) |
| AI Agent system rights | Present (Phase 12 IAM) |
| Future finance/quote/report/RAID actions | Deferred / seed-with-module |

## 15. Owner Policy Seeder Matrix

| Resource type | Policy v1 | Inheritance | Delegation depth | Seeder |
|---|---|---|---|---|
| ORGANIZATION | ACTIVE | DESCENDANTS | 2 | V36 + ApplicationReady ensure |
| WORKSPACE | ACTIVE | DESCENDANTS | 1 | V36 + ApplicationReady ensure |
| TEAM | ACTIVE | SELF_ONLY | 1 | V36 + ApplicationReady ensure |

## 16. Event Registry Seeder Matrix

| Group | Codes | Source system | Status |
|---|---|---|---|
| Identity §15.1 | `IAM_USER_*`, login/logout/refresh/sessions/password* | `SCOPERY_IAM` | Seeded |
| Policy §15.2 | role/resource/grant/owner/delegation/authz denied | `SCOPERY_IAM` | Seeded |
| MFA/SSO/API token/service account | — | — | Deferred |

Password-reset variables: `user.*`, `targetUser.email`, `reset.url`, `reset.expiresAt`, `support.email` (no secrets in sample/outbox).

## 17. Email Template Seeder Matrix

| Template code | Event | Recipient | Status |
|---|---|---|---|
| `IAM_PASSWORD_RESET_REQUEST_EMAIL` | `IAM_PASSWORD_RESET_REQUESTED` | `EVENT_TARGET_USER` | Seeded |
| `IAM_EMAIL_VERIFICATION_EMAIL` | — | — | Deferred |
| Security notice emails | — | — | Optional / deferred |

## 18. Security Notes

- Raw reset token only in email `reset.url`; Redis stores SHA-256; outbox reset payload omits token/URL
- Passwords/tokens/Authorization headers not logged
- Refresh cookies HttpOnly; paths `/` (access) and `/api/iam/auth` (refresh)
- **INTENTIONAL DEVIATION:** refresh tokens in Redis (performance/simplicity for rotation/revoke-all) instead of DB table with `token_hash`, `replaced_by_token_id`, `user_agent_hash`, `ip_hash`, `revoked_at`. Functionality for rotate/revoke/revoke-all is complete; audit trail / session viewer → Phase 23

## 19. Tests Added

**Auth (§18.1 gap-close):**

- `refresh_valid_rotatesToken`
- `refresh_revoked_rejected`
- `refresh_expired_rejected`
- `logout_valid_revokesTokenAndClearsCookie`
- `changePassword_wrongCurrentPassword_rejected`
- `changePassword_valid_revokesSessions`
- `resetRequest_unknownEmail_outwardSuccess`
- `resetConfirm_valid_updatesPasswordAndRevokesSessions`
- (+ prior) `LoginActionTest`, `PasswordResetTokenServiceTest`

**Authorization / grant:**

- `authorization_teamDenyOverridesUserAllow_denied`
- `delegateAccess_withoutCanDelegate_forbidden`
- `delegateAccess_depthExceeded_forbidden`
- `delegateAccess_actionNotCovered_forbidden`
- `delegateAccess_crossOrgRejected`
- `createGrant_crossOrgRejected`
- `IamAccessGrantEffectivenessTest` (expired/revoked)

**Seeders:**

- `IamOwnerPolicyCatalogInitializerTest`
- `IamEventDefinitionSeedInitializerTest`
- `IamAuthResourceLifecycleServiceTest`

## 20. Commands Run

```bash
./mvnw test -Dtest=Phase02AuthFlowActionTest,DelegateIamAccessActionTest,AuthorizationDecisionServiceTest,IamAccessGrantActionTest
./mvnw test
```

## 21. Test Results

`./mvnw test` → **BUILD SUCCESS** — Tests run: **629**, Failures: 0, Errors: 0, Skipped: 1.

## 22. Manual Verification

Not yet verified end-to-end against live SMTP / Redis TTL expiry clock — unit/MockMvc coverage only. Password-reset email delivery requires `notification.email.enabled=true`.

## 23. Assumptions

- Frontend reset URL path: `{frontendBaseUrl}/reset-password?token=...`
- Support email in template uses `notification.email.from-address`
- Cross-org federation remains out of scope

## 24. Deviations From Prompt

1. **Refresh token store = Redis, not DB table (§12.2)** — intentional; documented in §7/§18; Phase 23 for DB model + session viewer.
2. Delegation depth failure currently surfaces as `IAM_DELEGATION_NOT_PERMITTED` (filter), not always `IAM_DELEGATION_DEPTH_EXCEEDED` catalog code.
3. Completion path: `src/docs/phase-complete/...` (repo convention) rather than bare `docs/phase-complete/...`.

## 25. Risks

- Redis restart loses refresh sessions (acceptable for current model; mitigated when Phase 23 persists tokens)
- Password-reset email silent-skip if event definition missing at runtime before seeder order
- Broader IAM outbox events not yet consumed by notification for all codes

## 26. Future Phases That Must Return to IAM

| Phase | IAM return reason |
|---|---|
| 03 | Workspace/org membership + email verification touchpoints |
| 08 / Document Hub | Document resource types & permissions |
| 17 | Finance permissions / SoD seeds |
| 18 | Quote approval SoD |
| 21 | AI agent act-on-behalf / service identity |
| 22 | Report access permissions |
| 23 | MFA/SSO, session viewer, DB refresh tokens, hardening |
| 32 | Access request / policy studio |
| 37 | API tokens / connectors identity |
| 38 | Access review, retention, privacy |
