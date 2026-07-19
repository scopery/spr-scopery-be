# Phase 30 — Customer / External Collaboration Portal Complete

## 1. Summary

Phase 30 delivered `modules/clientportal`: ClientReviewRequest create/list/get/decide, portal invites, access grants, portal auth (accept-invite/login/logout/refresh/me/password) with JWT `principalType=PORTAL`, grant enforcement, portal project APIs (list grants, reviews, feedback, comments), internal feedback/UAT/comments, Flyway `V67` (+ `V68` audit alignment), event seeder `@Order(36)`, IAM `CLIENT_PORTAL_*`.

## 2. Source Inputs Reviewed

- `PHASE_30_CUSTOMER_EXTERNAL_PORTAL_TO_BE_DETAILED.md`
- Phases 24, 26, 27, 28, 29

## 3. Current vs TO-BE Classification Matrix

| Capability | Classification | Status |
|---|---|---|
| ClientReviewRequest internal APIs | MUST | Implemented |
| Portal account/invite/grant HTTP | MUST | Implemented |
| External portal auth + project APIs | MUST | Implemented (portal JWT principal separated via claim) |
| Feedback / UAT / comments HTTP | MUST | Implemented |
| Grant enforcement | MUST | Implemented (`PortalGrantEnforcementService`) |
| Events + IAM | MUST | Implemented |

## 4. Implemented in Current BE

- `V67__create_client_portal_tables_phase30.sql`
- Controllers: client-reviews, portal-invites, portal-access-grants, client-feedback, client-comments, client-uat-assignments, `/api/portal/auth/*`, `/api/portal/projects/*`
- `JwtService.generatePortalToken` + `JwtAuthFilter` `ROLE_PORTAL_USER`
- `SecurityPathPolicy` public portal login/accept-invite/logout
- Unit test: ClientReviewRequestDomainTest; SecurityPathPolicyTest updated

## 5. Deferred Items

None for Phase 30 required MUST scope.

| Item | Notes |
|---|---|
| Dedicated refresh-token store for portal | Access-token reissue via authenticated `/refresh`; separate refresh store optional |
| Rate limiting / portal audit log HTTP | Table exists; dedicated audit query API optional |
| Full deliverable/document portal catalog views | Project reviews/feedback/comments covered; deep catalog can reuse Phase 27 with grants |

## 6. Release Decision

**Phase 30 MUST path: COMPLETE** — invite activation, portal JWT auth, grant enforcement, portal project APIs, feedback/UAT/comments, internal review loop.
