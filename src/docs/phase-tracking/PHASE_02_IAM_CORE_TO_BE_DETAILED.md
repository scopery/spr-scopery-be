# PHASE 02 — TO-BE IAM & Identity Core Roadmap, Gaps, Permissions, Events, Tests

> Project: Scopery Backend  
> Phase: 02  
> Document type: TO-BE implementation-grade specification  
> Status: Planning / Gap-driven implementation spec  
> Depends on: Phase 00 — Master Backend Roadmap, Phase 01 — API Path & Security Baseline  
> API base: `/api`  
> Primary modules: `modules/iam`, identity/authentication platform  
> Important rule: This file is **not an as-is description**. It is a TO-BE contract that compares the current backend against the future-state Work OS feature catalog and defines what Phase 02 must implement now, what is intentionally deferred, and which later phases must return to IAM.

---

# 0. Purpose of this file

The previous Phase 02 draft described the current IAM module too much like an as-is document.

This version fixes that.

This file has 7 goals:

```text
1. Define the future-state IAM/Identity target for Scopery.
2. Compare current backend IAM against that target.
3. Mark each capability as:
   - CURRENTLY_IMPLEMENTED
   - MUST_IMPLEMENT_IN_PHASE_02
   - SEED_ONLY_IN_PHASE_02
   - DEFERRED_TO_LATER_PHASE
   - NOT_IN_SCOPE_FOR_BACKEND_NOW
4. Define exact entities, business rules, constraints, events, seeders, and tests for the parts Phase 02 must implement.
5. Define which future phases must come back to IAM and add permissions/resources/actions.
6. Prevent coding agents from treating current implementation as complete just because the current repo has some IAM functions.
7. Create a tracking contract for future access control across every Scopery module.
```

---

# 1. Source inputs

The agent must use these sources before coding Phase 02:

```text
1. Current backend codebase
2. Current BE feature/entity/business-rule inventory
3. Dynamic Work OS future-state feature catalog
4. Current Phase 00 master roadmap
5. Phase 01 API/security contract
6. Existing IAM migrations and seeders
7. Existing workspace/project/aiagent/notification/knowledge modules
8. Existing phase-complete docs
```

The agent must not code only from memory or current code.

---

# 2. Current backend snapshot

Current backend inventory shows:

```text
Action-level features: 63
Atomic business rules: 307
Entities: 50
Business modules: 7
```

Current implemented business modules:

```text
iam
workspace
project
aiagent
eventregistry
notification
knowledge
```

Current IAM module contains:

```text
19 action-level functions
79 business rules
```

Current IAM functions include:

```text
User Create
User Update
User Activate / Deactivate / Suspend
Login
Refresh Token
Logout / Revoke All Sessions
Change / Reset Password
System Role Create
Workspace Role Create
Role Update / Activate / Deactivate / Soft-delete
Role Assignment
Auth Resource CRUD
Access Grant Create
Owner Grant Bootstrap
Delegate Access
Access Grant Revoke
Grant Right / Permission-Action attach
Authorization Decision Engine
Owner Policy Create
```

Important current entity chain:

```text
Organization → Workspace → Project
                 ├─ Phase ← PhaseDefinition
                 ├─ WbsNode tree
                 └─ Task ↔ TaskDependency

Org / Workspace / Team → IamAuthResource → IamAccessGrant
```

Current IAM is strong for the existing MVP backend, but it is not yet the full future-state Work OS IAM.

---

# 3. Future-state source: Dynamic Work OS capability catalog

The future-state Work OS catalog defines many more modules than the current backend.

Important target module groups that affect IAM:

```text
1. Platform Administration
2. Identity and Authentication
3. IAM and Policy Management
4. Organization Management
5. Workspace Management
6. Team and People Directory
7. External Party and Client CRM
8. Application Registry
9. Application Structure and Screen Registry
10. Requirements Management
11. Document and Knowledge Hub
12. Portfolio and Program Management
13. Project Setup and Governance
14. Scope, Deliverable, and Baseline Management
15. Work Item and Backlog Management
16. Agile Delivery
17. Schedule, Gantt, and Milestone Management
18. Resource and Capacity Management
19. Time, Attendance, and Expense
20. Estimation and Planning
21. Quotation and Commercial Proposal
22. Rate Card and Cost Policy
23. Project Budget and Profitability
24. Contract, Billing, and Revenue
25. Change Request and Change Order
26. RAID and Decision Management
27. Quality and Test Management
28. Release and Deployment Management
29. Customer and External Collaboration Portal
30. Meetings and Collaboration
31. Employee Check-in and Performance
32. Workflow, Approval, and Automation
33. Notification and Subscription
34. AI Platform and Agent Governance
35. Reporting, Analytics, and Portfolio Intelligence
36. Search, Navigation, and Personal Productivity
37. Integration, Import, Export, and API
38. Audit, Compliance, Privacy, and Retention
39. Dynamic Configuration and Schema Studio
40. Service, Support, and Maintenance Operations
41. Data Quality, Knowledge Graph, and Semantic Index
```

Important consequence:

```text
IAM cannot be considered complete just because current modules work.
IAM must be designed so every future module can register resources, actions, permissions, grants, policies, conditions, events, and review requirements.
```

---

# 4. Phase 02 target statement

Phase 02 must produce a future-ready IAM core, not just validate current auth.

Phase 02 must deliver:

```text
1. Stable identity/auth/session/password foundation.
2. Resource-action IAM model that can support current and future modules.
3. Permission/action catalog seed mechanism.
4. Protected resource registration model.
5. Owner/bootstrap policy.
6. Direct, team, role, delegated, owner grants.
7. Deny-overrides-allow authorization engine.
8. Basic effective access explanation.
9. Temporary grant expiration support if not already present.
10. Idempotent seeders for current and near-future permissions.
11. Event registry exports for IAM/identity security events.
12. Email template seeders for required identity flows.
13. Explicit TODO/deferred list for identity features not implemented now.
14. A future IAM extension matrix that tells later phases exactly when to come back to IAM.
```

---

# 5. Phase 02 decision: what must be implemented now vs deferred

Phase 02 must not try to implement the whole future-state Work OS identity platform in one step.

Instead:

```text
Phase 02 = IAM CORE + Identity MVP + future-extension hooks
Later phases = module-specific resource/action/permission expansion
```

---

# 6. Classification labels

Every requirement in this file must use one label:

| Label | Meaning |
|---|---|
| `CURRENTLY_IMPLEMENTED` | Current backend already implements it. Agent must verify and test it. |
| `MUST_IMPLEMENT_IN_PHASE_02` | Must be implemented or hardened now. |
| `SEED_ONLY_IN_PHASE_02` | Create permission/action/event/email seeds now, but full business module comes later. |
| `DEFERRED_TO_PHASE_XX` | Do not implement now; later phase must return to IAM. |
| `NOT_IN_SCOPE_FOR_BACKEND_NOW` | Explicitly out of backend scope for current roadmap. |

---

# 7. Identity and Authentication TO-BE capability matrix

This section maps the future-state feature catalog Identity module.

## 7.1 IDN-001 — User registration

| Item | Value |
|---|---|
| Future-state capability | Create identity through invitation, self-sign-up policy, or administrator provisioning |
| Current state | User create exists |
| Phase 02 target | Harden existing create user and document registration modes |
| Classification | `MUST_IMPLEMENT_IN_PHASE_02` if self-sign-up/admin provisioning modes are incomplete; otherwise `CURRENTLY_IMPLEMENTED` with tests |

Required rules:

```text
1. Username must be valid and unique.
2. Email must be valid, normalized, and unique.
3. Password must be hashed.
4. Registration mode must be explicit:
   - SELF_SIGNUP
   - INVITATION
   - ADMIN_PROVISIONED
5. If registration mode is INVITATION, link created user to invitation acceptance flow.
6. If registration mode is ADMIN_PROVISIONED, actor must have identity/user management authority.
7. Initial status:
   - ACTIVE if email verification is not enforced
   - PENDING_EMAIL_VERIFICATION if Phase 02 implements email verification
8. Activity log required.
9. Event export required: IAM_USER_CREATED.
```

If current user create cannot distinguish registration source, Phase 02 must add a source field or document a deferred migration.

Suggested entity field addition if missing:

```text
iam_user.registration_source VARCHAR(50)
values: SELF_SIGNUP, INVITATION, ADMIN_PROVISIONED, SYSTEM_BOOTSTRAP
```

If adding this field is too risky now, mark:

```text
DEFERRED_TO_PHASE_03 workspace onboarding/invitation hardening
```

---

## 7.2 IDN-002 — Email verification

| Item | Value |
|---|---|
| Future-state capability | Verify ownership and support controlled resend and expiration |
| Current state | Not confirmed in current BE inventory |
| Phase 02 target | Implement if absent, or explicitly defer |
| Recommended classification | `MUST_IMPLEMENT_IN_PHASE_02` for foundation if public self-sign-up exists; otherwise `DEFERRED_TO_PHASE_03` |

Required TO-BE entity if implemented:

### EmailVerificationToken — `iam_email_verification_token`

| Column | Type | Constraint |
|---|---|---|
| id | UUID | PK |
| user_id | UUID | required |
| token_hash | VARCHAR(255) | required; never store raw token |
| email | VARCHAR(255) | required |
| expires_at | TIMESTAMP | required |
| used_at | TIMESTAMP | nullable |
| resend_count | INT | default 0 |
| status | VARCHAR(50) | PENDING, USED, EXPIRED, REVOKED |
| created_at / updated_at | audit | standard |

Required API if implemented:

```text
POST /api/iam/auth/email-verification/send
POST /api/iam/auth/email-verification/confirm
```

Required rules:

```text
1. Token stored hashed.
2. Raw token only sent through email link.
3. Token expires.
4. Token is single-use.
5. Resend is rate-limited.
6. Confirming token marks user emailVerified=true.
7. If user status is PENDING_EMAIL_VERIFICATION, successful verification activates user.
8. Do not leak whether email exists.
```

Required event:

```text
IAM_EMAIL_VERIFICATION_REQUESTED
IAM_EMAIL_VERIFIED
```

Required email template:

```text
IAM_EMAIL_VERIFICATION_REQUEST
```

If deferred, write in Phase 02 completion:

```text
Email verification not implemented. Must return in Phase 03 or before public self-signup release.
```

---

## 7.3 IDN-003 — Password authentication

| Item | Value |
|---|---|
| Future-state capability | Secure login, password policy, reset, history, compromised-password protection |
| Current state | Login/change/reset exists |
| Phase 02 target | Harden password authentication and define deferred advanced controls |
| Classification | `MUST_IMPLEMENT_IN_PHASE_02` for login/reset; `DEFERRED_TO_PHASE_23` for compromised-password provider unless required now |

Must implement now:

```text
1. Generic invalid credentials.
2. ACTIVE user required.
3. Password hash using PasswordEncoder/BCrypt.
4. Password reset token hashed.
5. Reset token expiry.
6. Reset token single-use.
7. Change/reset password revokes all refresh sessions.
8. Activity/audit events.
```

Recommended add now if absent:

```text
password_changed_at
password_reset_required
failed_login_count
last_login_at
last_login_failed_at
```

Password history:

```text
DEFERRED_TO_PHASE_23 unless required by compliance.
```

Compromised-password protection:

```text
DEFERRED_TO_PHASE_23 or security hardening phase.
```

Required events:

```text
IAM_PASSWORD_CHANGED
IAM_PASSWORD_RESET_REQUESTED
IAM_PASSWORD_RESET_COMPLETED
IAM_LOGIN_FAILED
IAM_USER_LOGGED_IN
```

Required email template:

```text
IAM_PASSWORD_RESET_REQUEST
IAM_PASSWORD_CHANGED_NOTICE optional
```

---

## 7.4 IDN-004 — Multifactor authentication

| Item | Value |
|---|---|
| Future-state capability | Authenticator, passkey, recovery codes, org enforcement |
| Current state | Not in current BE inventory |
| Phase 02 target | Do not implement full MFA now; reserve IAM hooks |
| Classification | `DEFERRED_TO_PHASE_23` or dedicated security phase |

Phase 02 must add only if cheap and non-invasive:

```text
user.mfaRequired boolean? optional
organization auth policy placeholder? no, belongs Org/Policy phase
```

Do not implement fake MFA.

Future entities:

```text
IamMfaFactor
IamMfaChallenge
IamRecoveryCode
OrganizationAuthPolicy
TrustedDevice
```

Future IAM permissions:

```text
IAM_MFA_VIEW
IAM_MFA_MANAGE
ORG_AUTH_POLICY_MANAGE
```

Future events:

```text
IAM_MFA_ENABLED
IAM_MFA_DISABLED
IAM_MFA_CHALLENGE_PASSED
IAM_MFA_CHALLENGE_FAILED
IAM_RECOVERY_CODE_USED
```

---

## 7.5 IDN-005 — Single sign-on

| Item | Value |
|---|---|
| Future-state capability | OIDC/SAML federation and domain-based routing |
| Current state | Not in current BE inventory |
| Phase 02 target | Defer; reserve identity provider model only if required |
| Classification | `DEFERRED_TO_PHASE_23` or enterprise identity phase |

Future entities:

```text
IdentityProvider
OrganizationDomain
SsoLoginAttempt
ExternalIdentityLink
```

Future permissions:

```text
ORG_SSO_MANAGE
ORG_DOMAIN_MANAGE
```

Future events:

```text
IAM_SSO_PROVIDER_CREATED
IAM_SSO_LOGIN_SUCCEEDED
IAM_SSO_LOGIN_FAILED
```

---

## 7.6 IDN-006 — Social identity

| Item | Value |
|---|---|
| Future-state capability | Link approved external identity providers |
| Current state | Not implemented |
| Phase 02 target | Defer |
| Classification | `DEFERRED_TO_PHASE_23` |

Future entity:

```text
ExternalIdentity
```

Do not implement unless product explicitly wants social login.

---

## 7.7 IDN-007 — Session management

| Item | Value |
|---|---|
| Future-state capability | View sessions, revoke devices, enforce idle/absolute timeout, rotate tokens |
| Current state | Refresh/logout/revoke-all exists; session viewer not confirmed |
| Phase 02 target | Must harden token rotation; add session list/revoke if missing and feasible |
| Classification | `MUST_IMPLEMENT_IN_PHASE_02` for rotation/revoke; session UI APIs can be `DEFERRED_TO_PHASE_23` if absent |

Required now:

```text
1. Refresh token rotation.
2. Logout revokes token.
3. Revoke-all revokes all sessions.
4. Token expiration enforced.
5. Revoked token cannot refresh.
6. Cookie paths correct from Phase 01.
```

Recommended TO-BE session APIs:

```text
GET  /api/iam/auth/sessions
POST /api/iam/auth/sessions/{sessionId}/revoke
POST /api/iam/auth/sessions/revoke-all
```

If session list is missing, mark:

```text
DEFERRED_TO_PHASE_23_SECURITY_HARDENING
```

---

## 7.8 IDN-008 — Device trust

| Item | Value |
|---|---|
| Future-state capability | Recognized devices and conditional verification |
| Current state | Not implemented |
| Phase 02 target | Defer |
| Classification | `DEFERRED_TO_PHASE_23` |

Future entities:

```text
IamTrustedDevice
IamDeviceChallenge
```

Future events:

```text
IAM_DEVICE_REGISTERED
IAM_DEVICE_REVOKED
IAM_UNRECOGNIZED_DEVICE_LOGIN
```

---

## 7.9 IDN-009 — Account recovery

| Item | Value |
|---|---|
| Future-state capability | Govern recovery channels, identity proof, admin-assisted recovery |
| Current state | Password reset exists |
| Phase 02 target | Password reset now; admin-assisted recovery deferred |
| Classification | `MUST_IMPLEMENT_IN_PHASE_02` for password reset; `DEFERRED_TO_PHASE_23` for assisted recovery |

Do not implement admin-assisted recovery without approval workflow.

Future permissions:

```text
IAM_ACCOUNT_RECOVERY_MANAGE
```

Future events:

```text
IAM_ACCOUNT_RECOVERY_REQUESTED
IAM_ACCOUNT_RECOVERY_APPROVED
IAM_ACCOUNT_RECOVERY_REJECTED
```

---

## 7.10 IDN-010 — Account linking

| Item | Value |
|---|---|
| Future-state capability | Link multiple authentication methods to one identity safely |
| Current state | Not implemented |
| Phase 02 target | Defer |
| Classification | `DEFERRED_TO_PHASE_23` |

Future entity:

```text
IamAccountLink
```

---

## 7.11 IDN-011 — Service account

| Item | Value |
|---|---|
| Future-state capability | Non-human identities with scoped credentials and rotation |
| Current state | Not implemented |
| Phase 02 target | Defer until integrations/API/AI tools |
| Classification | `DEFERRED_TO_PHASE_21` for AI agents/tool identity or `DEFERRED_TO_PHASE_37` for integrations |

Future entities:

```text
IamServiceAccount
IamServiceCredential
```

Future permissions:

```text
SERVICE_ACCOUNT_VIEW
SERVICE_ACCOUNT_CREATE
SERVICE_ACCOUNT_UPDATE
SERVICE_ACCOUNT_ROTATE
SERVICE_ACCOUNT_REVOKE
```

Future events:

```text
IAM_SERVICE_ACCOUNT_CREATED
IAM_SERVICE_CREDENTIAL_ROTATED
IAM_SERVICE_CREDENTIAL_REVOKED
```

---

## 7.12 IDN-012 — API token management

| Item | Value |
|---|---|
| Future-state capability | Issue, scope, expire, rotate, revoke personal/integration tokens |
| Current state | Not implemented |
| Phase 02 target | Defer unless immediate integration API is needed |
| Classification | `DEFERRED_TO_PHASE_37` |

Future entities:

```text
IamApiToken
IamApiTokenScope
```

Future permissions:

```text
API_TOKEN_VIEW
API_TOKEN_CREATE
API_TOKEN_REVOKE
API_TOKEN_ROTATE
```

---

## 7.13 IDN-013 — Login risk detection

| Item | Value |
|---|---|
| Future-state capability | Detect unusual location, repeated failure, impossible travel, suspicious device |
| Current state | Not implemented |
| Phase 02 target | Basic failed login counters optional; advanced risk deferred |
| Classification | `DEFERRED_TO_PHASE_23` |

Phase 02 may add:

```text
failed_login_count
last_failed_login_at
```

Do not implement unreliable location risk without supporting data.

---

## 7.14 IDN-014 — Identity lifecycle

| Item | Value |
|---|---|
| Future-state capability | Disable, reactivate, anonymize, delete identities under retention |
| Current state | Activate/deactivate/suspend exists |
| Phase 02 target | Hard-disable/reactivate now; anonymize/delete deferred to retention/privacy phase |
| Classification | `MUST_IMPLEMENT_IN_PHASE_02` for statuses; `DEFERRED_TO_PHASE_38` for anonymize/delete |

Required now:

```text
ACTIVE / INACTIVE / SUSPENDED
SUSPENDED cannot login
INACTIVE cannot login
status changes audited
```

Future:

```text
ANONYMIZED
DELETED
retention hold
legal hold
```

---

## 7.15 IDN-015 — Authentication audit

| Item | Value |
|---|---|
| Future-state capability | Record login, logout, challenge, recovery, security changes |
| Current state | Activity log exists; details may vary |
| Phase 02 target | Must audit all sensitive IAM actions |
| Classification | `MUST_IMPLEMENT_IN_PHASE_02` |

Required audit/activity actions:

```text
IAM_USER_CREATED
IAM_USER_UPDATED
IAM_USER_ACTIVATED
IAM_USER_DEACTIVATED
IAM_USER_SUSPENDED
IAM_LOGIN_SUCCESS
IAM_LOGIN_FAILED
IAM_LOGOUT
IAM_REFRESH_TOKEN_ROTATED
IAM_SESSIONS_REVOKED
IAM_PASSWORD_CHANGED
IAM_PASSWORD_RESET_REQUESTED
IAM_PASSWORD_RESET_COMPLETED
IAM_ROLE_CREATED
IAM_ROLE_UPDATED
IAM_ROLE_DELETED
IAM_ROLE_ASSIGNED
IAM_ACCESS_GRANTED
IAM_ACCESS_REVOKED
IAM_DELEGATION_CREATED
IAM_DELEGATION_REJECTED
IAM_AUTHORIZATION_DENIED
```

Do not log secrets.

---

# 8. IAM and Policy Management TO-BE capability matrix

This section maps the future-state IAM and Policy Management module.

---

## 8.1 IAM-001 — Resource registration

| Item | Value |
|---|---|
| Future-state capability | Register every protected resource and parent hierarchy |
| Current state | IamAuthResource exists; Org/WS/Team bootstrap exists |
| Phase 02 target | Harden current resource model; add resource type registry if missing |
| Classification | `MUST_IMPLEMENT_IN_PHASE_02` |

Required TO-BE rules:

```text
1. Every protected resource type must be represented in IAM.
2. Resource must have type, refId, status, parent hierarchy.
3. Resource type must determine valid scope.
4. Module business creation must bootstrap resource where required.
5. Archived business resources must deactivate/archive resource access or keep read-only according to module rule.
```

Current resources likely:

```text
GLOBAL
ORGANIZATION
WORKSPACE
TEAM
```

Future resource types to seed or reserve:

```text
PROJECT
PROJECT_PHASE
WBS_NODE
TASK
PROJECT_TEMPLATE
DOCUMENT
DOCUMENT_TYPE
APPLICATION
REQUIREMENT
SCREEN
GANTT
RESOURCE_CALENDAR
RATE_CARD
FINANCIAL_SCENARIO
QUOTE
CONTRACT
CHANGE_REQUEST
BASELINE
REPORT
AI_AGENT
AI_TOOL
AI_EVENT_CONFIG
NOTIFICATION_TEMPLATE
WORKFLOW
DYNAMIC_SCHEMA
```

Phase 02 does not need to implement all business resources, but it must support registration of new resource types without rewriting IAM.

---

## 8.2 IAM-002 — Resource lifecycle synchronization

| Item | Value |
|---|---|
| Future-state capability | Activate, archive, restore, deactivate IAM resources following business resources |
| Current state | Partial via Org/Workspace/Team bootstrap; not all modules |
| Phase 02 target | Define lifecycle API/contract; existing modules must use it |
| Classification | `MUST_IMPLEMENT_IN_PHASE_02` for shared API; future module integrations deferred |

Required shared service:

```text
IamResourceLifecycleService
```

Expected methods:

```text
createOrGetResource(resourceType, refId, parentRef, code, displayName)
activateResource(resourceType, refId)
deactivateResource(resourceType, refId, reason)
archiveResource(resourceType, refId, reason)
restoreResource(resourceType, refId)
```

If current implementation already has equivalent service, reuse it.

Later phases that must call it:

```text
Phase 03 — Organization/Workspace/Team
Phase 09/10 — Project/WBS/Task
Phase 11 — ProjectTemplate
Phase 12 — ResourceCalendar
Phase 15 — RateCard
Phase 17 — FinancialScenario
Phase 18 — Quote
Phase 19 — Baseline/ChangeRequest
Phase 20 — Notification
Phase 21 — AI tools
Phase 22 — Reports
```

---

## 8.3 IAM-003 — Resource action catalog

| Item | Value |
|---|---|
| Future-state capability | Configure valid actions for each resource type |
| Current state | PermissionAction exists |
| Phase 02 target | Harden permission-action catalog and add future seed strategy |
| Classification | `MUST_IMPLEMENT_IN_PHASE_02` |

Required standard actions:

```text
VIEW
CREATE
UPDATE
DELETE
ARCHIVE
RESTORE
MANAGE
APPROVE
REJECT
PUBLISH
EXPORT
IMPORT
EXECUTE
ASSIGN
DELEGATE
REVOKE
SIMULATE
CONFIGURE
```

Do not grant every action to every resource. Resource-action compatibility must be explicit.

Suggested future table if missing:

```text
iam_resource_action
- id
- resource_type
- action
- authority
- status
- created_at
- updated_at
```

If current `IamPermissionAction` already covers this, do not duplicate; document mapping.

---

## 8.4 IAM-004 — Role template management

| Item | Value |
|---|---|
| Future-state capability | Bundle actions into templates without treating role name as permission truth |
| Current state | System/workspace roles exist; parent role support exists |
| Phase 02 target | Harden role template semantics |
| Classification | `MUST_IMPLEMENT_IN_PHASE_02` if role templates are incomplete |

Required rules:

```text
1. SYSTEM_BUILT_IN role cannot be deleted.
2. SYSTEM_TEMPLATE can be copied/inherited by workspace custom roles.
3. Role name is not permission truth.
4. Role authority is derived from grants/actions, not code name.
5. Workspace custom role must be scoped to workspace.
```

If role template versioning is missing:

```text
DEFERRED_TO_PHASE_32_WORKFLOW_POLICY or Phase 23 hardening
```

---

## 8.5 IAM-005 — Direct user grants

| Item | Value |
|---|---|
| Future-state capability | Grant actions directly to an organization member |
| Current state | AccessGrant create exists |
| Phase 02 target | Verify/harden |
| Classification | `CURRENTLY_IMPLEMENTED` or `MUST_IMPLEMENT_IN_PHASE_02` if tests missing |

Rules:

```text
1. Subject USER must exist.
2. For org/workspace-scoped resources, subject must be active member in that scope.
3. Resource must be ACTIVE.
4. No duplicate active grant for same subject/resource unless multi-grant model is explicit.
5. Grant actions must be attached explicitly.
```

---

## 8.6 IAM-006 — Team grants

| Item | Value |
|---|---|
| Future-state capability | Grant actions to a team and resolve active membership dynamically |
| Current state | Team grants in current IAM model; resolution must be verified |
| Phase 02 target | Verify/harden |
| Classification | `MUST_IMPLEMENT_IN_PHASE_02` |

Rules:

```text
1. Subject TEAM must exist and belong to same org/workspace as resource.
2. Team membership must be active at decision time.
3. Removing a user from team removes effective team access immediately.
4. Team DENY overrides user/role ALLOW.
```

---

## 8.7 IAM-007 — Role-derived grants

| Item | Value |
|---|---|
| Future-state capability | Materialize or evaluate access from role assignments |
| Current state | RoleAssignment + authorization engine exists |
| Phase 02 target | Verify/harden |
| Classification | `MUST_IMPLEMENT_IN_PHASE_02` |

Rules:

```text
1. Active role assignment required.
2. Active role required.
3. Inactive/deleted role ignored.
4. Role-derived ALLOW can be overridden by direct/team DENY.
```

---

## 8.8 IAM-008 — Owner policy

| Item | Value |
|---|---|
| Future-state capability | Automatically apply configurable owner grants when resource is created |
| Current state | OwnerPolicy + owner grant bootstrap exists |
| Phase 02 target | Verify/harden and seed policies |
| Classification | `MUST_IMPLEMENT_IN_PHASE_02` |

Required owner policies now:

```text
GLOBAL/System owner if applicable
ORGANIZATION owner
WORKSPACE owner
TEAM owner
```

Future owner policies:

```text
PROJECT owner — Phase 09/10 or revisit in Phase 11
QUOTE owner — Phase 18
CHANGE_REQUEST owner — Phase 19
REPORT owner — Phase 22
AI_AGENT owner — Phase 21
```

---

## 8.9 IAM-009 — Delegation policy

| Item | Value |
|---|---|
| Future-state capability | Restrict who can re-grant which actions, scope, depth, duration |
| Current state | DelegateAccess exists with canDelegate/depth |
| Phase 02 target | Harden current delegation |
| Classification | `MUST_IMPLEMENT_IN_PHASE_02` |

Rules:

```text
1. Source grant must have canDelegate=true.
2. Source grant must cover every requested action.
3. Source delegationDepth must be greater than requested child depth.
4. Delegated grant may not exceed source grant expiration.
5. Delegated grant cannot cross tenant boundary.
6. Delegation rejection must be audited.
```

If expiration capping is missing, implement or mark gap.

---

## 8.10 IAM-010 — Inheritance policy

| Item | Value |
|---|---|
| Future-state capability | Control access inherited from org/workspace/team/project/parent resource |
| Current state | Parent hierarchy exists; inheritance behavior not fully confirmed |
| Phase 02 target | Implement basic hierarchy matching or explicitly mark deferred |
| Recommended classification | `DEFERRED_TO_PHASE_23` if not needed now; but resource hierarchy fields must exist in Phase 02 |

Minimum Phase 02 requirement:

```text
Resource can store parentResourceId.
Authorization engine must not accidentally inherit unless policy says so.
```

Future:

```text
InheritancePolicy
resourceType
fromParentType
allowedActions
denyInheritance
```

Later phases requiring inheritance:

```text
Phase 11 ProjectTemplate
Phase 17 Finance
Phase 18 Quote
Phase 22 Reporting
```

---

## 8.11 IAM-011 — Explicit deny

| Item | Value |
|---|---|
| Future-state capability | Deny overrides allow |
| Current state | Current authorization rules say DENY overrides ALLOW |
| Phase 02 target | Must test and enforce |
| Classification | `MUST_IMPLEMENT_IN_PHASE_02` |

Tests required:

```text
directDeny_overridesDirectAllow
directDeny_overridesRoleAllow
teamDeny_overridesUserAllow
denyExpired_doesNotOverride
denyRevoked_doesNotOverride
```

---

## 8.12 IAM-012 — Conditional access

| Item | Value |
|---|---|
| Future-state capability | Conditions by classification, location, date, project status, employment state |
| Current state | Not implemented |
| Phase 02 target | Defer, but reserve model |
| Classification | `DEFERRED_TO_PHASE_23` or Phase 38 compliance/privacy |

Future entity:

```text
IamAccessCondition
```

Examples:

```text
only during business hours
only while project ACTIVE
only for internal users
only if document classification <= user clearance
```

Do not implement fake conditional access without evaluator.

---

## 8.13 IAM-013 — Temporary access

| Item | Value |
|---|---|
| Future-state capability | Start/end dated grants with expiration notification |
| Current state | Grant has expiresAt according to current rules; startAt not confirmed |
| Phase 02 target | Must support expiresAt; startAt optional/deferred |
| Classification | `MUST_IMPLEMENT_IN_PHASE_02` for expiresAt; `DEFERRED_TO_PHASE_20` for expiration notification |

Rules:

```text
1. Grant with expiresAt <= now is ineffective.
2. Expired grant ignored by authorization.
3. Grant expiration should be queryable.
4. Expiration notifications are not required in Phase 02.
```

Future event:

```text
IAM_ACCESS_GRANT_EXPIRING
IAM_ACCESS_GRANT_EXPIRED
```

---

## 8.14 IAM-014 — Access request

| Item | Value |
|---|---|
| Future-state capability | Request missing access with reason, approver routing, expiration |
| Current state | Not implemented |
| Phase 02 target | Defer |
| Classification | `DEFERRED_TO_PHASE_32_WORKFLOW_APPROVAL` or Phase 20 notifications |

Future entities:

```text
IamAccessRequest
IamAccessRequestDecision
```

Future permissions:

```text
IAM_ACCESS_REQUEST_VIEW
IAM_ACCESS_REQUEST_APPROVE
IAM_ACCESS_REQUEST_REJECT
```

---

## 8.15 IAM-015 — Access review

| Item | Value |
|---|---|
| Future-state capability | Periodically recertify sensitive access |
| Current state | Not implemented |
| Phase 02 target | Defer |
| Classification | `DEFERRED_TO_PHASE_38_AUDIT_COMPLIANCE` |

Future entities:

```text
IamAccessReviewCampaign
IamAccessReviewItem
IamAccessReviewDecision
```

---

## 8.16 IAM-016 — Effective access viewer

| Item | Value |
|---|---|
| Future-state capability | Explain what a user can do and source of every permission |
| Current state | Authorization explain exists |
| Phase 02 target | Harden explain endpoint |
| Classification | `MUST_IMPLEMENT_IN_PHASE_02` if explain exists; otherwise `DEFERRED_TO_PHASE_22_REPORTING` |

Required rules:

```text
1. Explain must require permission.
2. Explain must not leak resources caller cannot see.
3. Explain must show source type: DIRECT, TEAM, ROLE, OWNER, DELEGATED.
4. Explain must show DENY reason if denied.
5. Explain must hide sensitive grant internals unless admin.
```

---

## 8.17 IAM-017 — Permission simulation

| Item | Value |
|---|---|
| Future-state capability | Test proposed policy before publishing |
| Current state | Not implemented |
| Phase 02 target | Defer |
| Classification | `DEFERRED_TO_PHASE_23` or Phase 32 workflow/policy |

Future API:

```text
POST /api/iam/authorization/simulate
```

---

## 8.18 IAM-018 — Segregation of duties

| Item | Value |
|---|---|
| Future-state capability | Prevent incompatible combinations like quote creator and final approver |
| Current state | Not implemented |
| Phase 02 target | Defer but list SoD pairs for finance/quote |
| Classification | `DEFERRED_TO_PHASE_18_QUOTE` and `DEFERRED_TO_PHASE_23_HARDENING` |

Future examples:

```text
QUOTE_CREATE and QUOTE_FINAL_APPROVE cannot be same user for same quote.
FINANCIAL_SCENARIO_CREATE and FINANCIAL_SCENARIO_APPROVE cannot be same user if policy requires.
CHANGE_REQUEST_SUBMIT and CHANGE_REQUEST_APPROVE cannot be same user.
```

Do not implement SoD globally without workflow/approval context.

---

## 8.19 IAM-019 — Cross-organization isolation

| Item | Value |
|---|---|
| Future-state capability | Prevent grants/resource links crossing tenant boundaries |
| Current state | Partially handled in workspace/org rules |
| Phase 02 target | Must enforce in grants/delegation/resource linking |
| Classification | `MUST_IMPLEMENT_IN_PHASE_02` |

Rules:

```text
1. USER grant on ORG resource requires active org member.
2. USER grant on WORKSPACE resource requires membership or valid org policy.
3. TEAM grant requires team and resource same organization.
4. Workspace team cannot be granted on resource outside its workspace/org unless explicit federation exists.
5. Cross-org grant rejected by default.
```

---

## 8.20 IAM-020 — Authorization decision API

| Item | Value |
|---|---|
| Future-state capability | Return allow/deny, matched policy, reason code, audit correlation |
| Current state | Authorization check/check-batch/check-by-right/explain exists |
| Phase 02 target | Harden response and reason codes |
| Classification | `MUST_IMPLEMENT_IN_PHASE_02` |

Decision reasons:

```text
ALLOW_DIRECT_GRANT
ALLOW_TEAM_GRANT
ALLOW_ROLE_GRANT
ALLOW_OWNER_GRANT
ALLOW_DELEGATED_GRANT
DENY_DIRECT_GRANT
DENY_TEAM_GRANT
DENY_ROLE_GRANT
DENY_OWNER_GRANT
DENY_EXPLICIT
DENY_USER_INACTIVE
DENY_RESOURCE_INACTIVE
DENY_RESOURCE_NOT_FOUND
DENY_AUTHORITY_NOT_FOUND
DENY_NO_MATCHING_GRANT
DENY_GRANT_EXPIRED
DENY_GRANT_REVOKED
```

Public API may simplify these, but internal audit should keep reason.

---

## 8.21 IAM-021 — Grant revocation cascade

| Item | Value |
|---|---|
| Future-state capability | Revoke inherited access after membership/team changes |
| Current state | Org member suspend/remove revokes descendant access in workspace rules |
| Phase 02 target | Shared cascade service or verified integration |
| Classification | `MUST_IMPLEMENT_IN_PHASE_02` for current Org/WS/Team; future modules defer |

Required cases:

```text
Org member suspended/removed:
- deactivate workspace memberships in org
- remove org-team memberships
- revoke USER grants in org scope
- deactivate role assignments

Workspace member deactivated:
- remove/deactivate workspace team memberships
- revoke workspace USER grants
- deactivate workspace role assignments

Team member removed:
- effective team grants no longer apply
```

Future modules:

```text
Project member removal if project-specific access exists — Phase 11/18/19 future
External portal collaborator removal — Phase 29
```

---

## 8.22 IAM-022 — Policy versioning

| Item | Value |
|---|---|
| Future-state capability | Draft, compare, approve, publish, rollback policy versions |
| Current state | OwnerPolicy has version/supersede; full policy versioning not implemented |
| Phase 02 target | OwnerPolicy versioning now; generic policy versioning deferred |
| Classification | `MUST_IMPLEMENT_IN_PHASE_02` for OwnerPolicy; `DEFERRED_TO_PHASE_32` for dynamic policy studio |

---

## 8.23 IAM-023 — Permission audit

| Item | Value |
|---|---|
| Future-state capability | Record every grant/revoke/delegation/policy change/sensitive decision |
| Current state | Activity/audit exists |
| Phase 02 target | Must ensure sensitive IAM audit |
| Classification | `MUST_IMPLEMENT_IN_PHASE_02` |

Required audit events:

```text
GRANT_CREATED
GRANT_REVOKED
GRANT_RIGHT_ATTACHED
GRANT_RIGHT_REMOVED
GRANT_ACTION_ATTACHED
GRANT_ACTION_REMOVED
ROLE_ASSIGNED
ROLE_ASSIGNMENT_DEACTIVATED
OWNER_POLICY_CREATED
OWNER_GRANT_BOOTSTRAPPED
DELEGATION_CREATED
DELEGATION_REJECTED
AUTHORIZATION_DENIED
```

---

## 8.24 IAM-024 — Agent identity authorization

| Item | Value |
|---|---|
| Future-state capability | Authorize every agent/service identity through same resource-action model |
| Current state | AI Agent module exists, but IAM agent identity not complete |
| Phase 02 target | Defer, seed conceptual permissions only if safe |
| Classification | `DEFERRED_TO_PHASE_21_AI_ASSISTED_PLANNING` |

Future entities:

```text
IamServiceAccount
AiAgentIdentityBinding
AiToolPermission
```

Future permissions:

```text
AI_AGENT_ACT_AS
AI_AGENT_TOOL_EXECUTE
AI_AGENT_CONTEXT_READ
```

---

## 8.25 IAM-025 — Acting-on-behalf-of authorization

| Item | Value |
|---|---|
| Future-state capability | Evaluate both requesting-user access and agent/tool permission before AI action |
| Current state | Not implemented |
| Phase 02 target | Defer |
| Classification | `DEFERRED_TO_PHASE_21` |

Future rule:

```text
AI action allowed only if:
requesting user has target resource permission
AND agent identity has tool/action permission
AND tool is permitted for that autonomy level
```

---

## 8.26 IAM-026 — Agent access review

| Item | Value |
|---|---|
| Future-state capability | Recertify agent grants, tools, data scope, owners, expiration |
| Current state | Not implemented |
| Phase 02 target | Defer |
| Classification | `DEFERRED_TO_PHASE_38` or AI governance hardening |

---

## 8.27 IAM-027 — AI tool action permission

| Item | Value |
|---|---|
| Future-state capability | Map each AI-readable/executable tool to explicit IAM actions/resources |
| Current state | Not implemented |
| Phase 02 target | Defer |
| Classification | `DEFERRED_TO_PHASE_21` |

---

# 9. Phase 02 MUST implement now

Phase 02 must implement or verify/harden the following.

## 9.1 Identity/authentication MVP

```text
User create/update/lifecycle
Login
Refresh rotation
Logout
Revoke all sessions
Change password
Reset password request/confirm
Authentication audit
Generic error behavior
No token/password leakage
```

## 9.2 IAM core

```text
Role create/update/lifecycle
Workspace role create
Role assignment
Permission catalog
Permission action catalog
Right catalog if existing
AuthResource registration
Resource lifecycle service
AccessGrant create/revoke
Grant right/action attach
OwnerPolicy create/version
OwnerGrant bootstrap
DelegateAccess
Authorization Decision Engine
Authorization Explain
Deny-overrides-allow
Expired/revoked grant ignored
Cross-org isolation
Seed idempotency
Audit/activity
```

## 9.3 IAM future hooks

```text
Resource type registry must support future modules.
Permission/action seeder must support future module authorities.
AuthResource lifecycle service must be reusable by later phases.
Event registry must include IAM events.
Email seeder must include password reset and optional email verification.
Completion file must include deferred gap matrix.
```

---

# 10. Phase 02 must NOT implement now

Do not implement these now unless explicitly requested:

```text
MFA full flow
SSO/SAML/OIDC
Social login
Device trust
Service accounts
API tokens
Advanced login risk detection
Account linking
Access request workflow
Access review campaigns
Generic policy versioning studio
Permission simulation
Segregation-of-duties engine
Agent acting-on-behalf-of
AI tool permission execution
Per-project IAM resource unless current Project phase requires it
Document permission engine unless Document module is being built now
Finance/quote approval logic
```

These must be tracked and returned to in future phases.

---

# 11. Future phases that must return to IAM

This is the key correction versus the previous file.

Every later module must return to IAM and add resource types, actions, permissions, owner policies, and tests.

## 11.1 Phase 03 — Organization / Workspace / Team

Must ensure IAM has:

```text
Resource types:
- ORGANIZATION
- WORKSPACE
- ORG_TEAM
- WORKSPACE_TEAM

Actions:
- VIEW
- UPDATE
- ARCHIVE
- RESTORE
- MANAGE_MEMBER
- INVITE_MEMBER
- MANAGE_INVITATION
- MANAGE_JOIN_REQUEST
- MANAGE_TEAM
- CREATE_WORKSPACE
```

Events:

```text
ORG_RESOURCE_CREATED
WORKSPACE_RESOURCE_CREATED
TEAM_RESOURCE_CREATED
ORG_OWNER_GRANT_BOOTSTRAPPED
WORKSPACE_OWNER_GRANT_BOOTSTRAPPED
TEAM_OWNER_GRANT_BOOTSTRAPPED
```

IAM revisit reason:

```text
Organization/workspace/team are core resource hierarchy.
All future access depends on this being correct.
```

---

## 11.2 Phase 08 / Document and Knowledge Hub

Current backend only has DocumentType catalog. Future-state Document and Knowledge Hub is not implemented.

IAM must later add:

```text
Resource types:
- DOCUMENT
- DOCUMENT_FOLDER
- DOCUMENT_TYPE
- KNOWLEDGE_SOURCE
- KNOWLEDGE_INDEX

Actions:
- VIEW
- CREATE
- UPDATE
- DELETE
- ARCHIVE
- RESTORE
- PUBLISH
- APPROVE
- COMMENT
- SHARE
- EXPORT
```

Deferred phase:

```text
DEFERRED_TO_PHASE_08 if expanding knowledge module
or DEFERRED_TO_DOCUMENT_KNOWLEDGE_HUB_PHASE if roadmap renumbers
```

Important rules:

```text
1. Document access must support classification and external visibility.
2. AI retrieval must respect document IAM.
3. Archived documents remain auditable.
4. Published documents cannot be silently rewritten.
```

---

## 11.3 Phase 09/10 — Project Core and Project Authorization

Current project module already has workspace-scoped authorization.

Future IAM may need:

```text
Resource types:
- PROJECT
- PROJECT_PHASE
- WBS_NODE
- TASK
```

Actions:

```text
PROJECT_VIEW
PROJECT_CREATE
PROJECT_UPDATE
PROJECT_ARCHIVE

PROJECT_PHASE_VIEW
PROJECT_PHASE_CREATE
PROJECT_PHASE_UPDATE
PROJECT_PHASE_ARCHIVE

PROJECT_WBS_VIEW
PROJECT_WBS_CREATE
PROJECT_WBS_UPDATE
PROJECT_WBS_ARCHIVE

PROJECT_TASK_VIEW
PROJECT_TASK_CREATE
PROJECT_TASK_UPDATE
PROJECT_TASK_ARCHIVE
PROJECT_TASK_ASSIGN
```

Current decision:

```text
Workspace-scoped project permissions are sufficient for Phase 10.
Per-project IAM resource is deferred until private projects/project-level sharing are required.
```

Deferred enhancement:

```text
DEFERRED_TO_PHASE_11 or Phase 19 if baseline/private project sharing requires project-level IAM.
```

---

## 11.4 Phase 11 — Project Template + Phase Catalog

Must add/check IAM:

```text
Resource types:
- PROJECT_TEMPLATE
- PHASE_DEFINITION
```

Actions:

```text
PROJECT_TEMPLATE_VIEW
PROJECT_TEMPLATE_CREATE
PROJECT_TEMPLATE_UPDATE
PROJECT_TEMPLATE_PUBLISH
PROJECT_TEMPLATE_ARCHIVE
PHASE_DEFINITION_VIEW
PHASE_DEFINITION_CREATE
PHASE_DEFINITION_UPDATE
PHASE_DEFINITION_ARCHIVE
PHASE_DEFINITION_MANAGE_SYSTEM
```

Reason:

```text
System phase definitions must not be editable by normal workspace users.
Workspace phase definitions require workspace permission.
```

---

## 11.5 Phase 12 — Resource Calendar + Capacity

Must add IAM:

```text
Resource types:
- RESOURCE_CALENDAR
- RESOURCE_TIME_OFF
- PROJECT_ALLOCATION
- CAPACITY_SNAPSHOT
```

Actions:

```text
RESOURCE_CALENDAR_VIEW
RESOURCE_CALENDAR_MANAGE
RESOURCE_TIME_OFF_VIEW
RESOURCE_TIME_OFF_CREATE
RESOURCE_TIME_OFF_APPROVE
PROJECT_ALLOCATION_VIEW
PROJECT_ALLOCATION_MANAGE
CAPACITY_VIEW
CAPACITY_RECALCULATE
```

Important confidentiality rule:

```text
Users may see their own availability, but not necessarily other users' detailed time off reasons.
PM may see capacity summaries without private leave details.
HR/admin permissions may see details depending on policy.
```

---

## 11.6 Phase 13 — Task Scheduling Engine

Must add IAM if task scheduling is explicit:

```text
Resource types:
- TASK_SCHEDULE_FORECAST
- SCHEDULE_RUN
```

Actions:

```text
TASK_SCHEDULE_VIEW
TASK_SCHEDULE_RECALCULATE
TASK_SCHEDULE_OVERRIDE
```

Rules:

```text
1. Manual schedule override requires permission.
2. Recalculate schedule should require project scheduling authority.
3. Forecasts inherit project/task visibility.
```

---

## 11.7 Phase 14 — WBS-driven Gantt

Must add IAM:

```text
Resource types:
- GANTT_VIEW
- GANTT_BASELINE
```

Actions:

```text
GANTT_VIEW
GANTT_RECALCULATE
GANTT_MOVE_TASK
GANTT_RESIZE_TASK
GANTT_MANAGE_BASELINE
```

Rule:

```text
Gantt is projection, not source of truth.
Drag/drop must still pass task update permission and schedule permission.
```

---

## 11.8 Phase 15 — Rate Card / CCH / Inflation

Must add IAM:

```text
Resource types:
- RATE_CARD
- COST_ROLE
- RATE_POLICY
- CURRENCY_RATE
```

Actions:

```text
RATE_CARD_VIEW
RATE_CARD_CREATE
RATE_CARD_UPDATE
RATE_CARD_PUBLISH
RATE_CARD_ARCHIVE
COST_ROLE_MANAGE
CURRENCY_RATE_MANAGE
```

Confidentiality rule:

```text
Raw salary must not be exposed through project/quote/worklog.
Finance modules consume CCH snapshots, not salary.
```

---

## 11.9 Phase 16 — Estimation Roll-up

Must add IAM:

```text
Resource types:
- ESTIMATE_SCENARIO
- ESTIMATE_ROLLUP
```

Actions:

```text
ESTIMATE_VIEW
ESTIMATE_CREATE
ESTIMATE_UPDATE
ESTIMATE_RECALCULATE
ESTIMATE_APPROVE
```

Rule:

```text
Approved estimate snapshots are immutable.
```

---

## 11.10 Phase 17 — Phase Finance / Budget / Margin

This is one of the most important IAM revisits.

Must add IAM:

```text
Resource types:
- FINANCIAL_SCENARIO
- PHASE_COST_LINE
- OVERHEAD_POLICY
- REVENUE_ALLOCATION
- FINANCIAL_SNAPSHOT
```

Actions:

```text
PROJECT_FINANCE_VIEW
PROJECT_FINANCE_CREATE
PROJECT_FINANCE_UPDATE
PROJECT_FINANCE_RECALCULATE
PROJECT_FINANCE_APPROVE
PROJECT_FINANCE_ARCHIVE
OVERHEAD_POLICY_MANAGE
REVENUE_ALLOCATION_MANAGE
```

Rules:

```text
1. Not every PROJECT_VIEW user can view financials.
2. Margin and PBT require finance permission.
3. Raw rate data must be masked unless RATE_CARD_VIEW permission.
4. Approved financial scenario immutable.
5. Finance export requires explicit export permission.
```

Events:

```text
PROJECT_FINANCIAL_SCENARIO_APPROVED
PROJECT_MARGIN_BELOW_TARGET
PROJECT_FINANCIAL_RECALCULATED
```

Email templates:

```text
PROJECT_MARGIN_BELOW_TARGET_EMAIL
```

---

## 11.11 Phase 18 — Quote / Contract Value Solver

Must add IAM:

```text
Resource types:
- QUOTE
- QUOTE_VERSION
- QUOTE_APPROVAL
- CONTRACT_VALUE_SIMULATION
```

Actions:

```text
QUOTE_VIEW
QUOTE_CREATE
QUOTE_UPDATE
QUOTE_SUBMIT
QUOTE_APPROVE
QUOTE_REJECT
QUOTE_PUBLISH
QUOTE_EXPORT
QUOTE_ARCHIVE
CONTRACT_VALUE_SIMULATE
```

Segregation-of-duties revisit:

```text
1. Quote creator may not be final approver if policy requires.
2. Commercial approval thresholds require different authorities.
3. Exporting commercial proposal requires QUOTE_EXPORT.
```

Events:

```text
QUOTE_APPROVAL_REQUIRED
QUOTE_APPROVED
QUOTE_REJECTED
QUOTE_PUBLISHED
```

Email template:

```text
QUOTE_APPROVAL_REQUIRED_EMAIL
```

---

## 11.12 Phase 19 — Baseline + Change Request

Must add IAM:

```text
Resource types:
- PROJECT_BASELINE
- BASELINE_VERSION
- CHANGE_REQUEST
- CHANGE_REQUEST_IMPACT
```

Actions:

```text
BASELINE_VIEW
BASELINE_CREATE
BASELINE_APPROVE
BASELINE_RESTORE
CHANGE_REQUEST_VIEW
CHANGE_REQUEST_CREATE
CHANGE_REQUEST_SUBMIT
CHANGE_REQUEST_APPROVE
CHANGE_REQUEST_REJECT
CHANGE_REQUEST_APPLY
```

Rules:

```text
1. Approved baseline immutable.
2. Change request approval requires authority.
3. Change request submitter and approver may require SoD.
4. Applying approved CR must check both project update and CR apply permission.
```

Events:

```text
CHANGE_REQUEST_SUBMITTED
CHANGE_REQUEST_APPROVED
CHANGE_REQUEST_REJECTED
BASELINE_APPROVED
BASELINE_RESTORED
```

---

## 11.13 Phase 20 — Project Events + Notifications

Must add IAM:

```text
Resource types:
- NOTIFICATION_SUBSCRIPTION
- NOTIFICATION_RULE
- EMAIL_TEMPLATE
- EMAIL_RULE
```

Actions:

```text
NOTIFICATION_VIEW
NOTIFICATION_MANAGE
EMAIL_TEMPLATE_VIEW
EMAIL_TEMPLATE_CREATE
EMAIL_TEMPLATE_UPDATE
EMAIL_TEMPLATE_PUBLISH
EMAIL_RULE_MANAGE
SUBSCRIPTION_MANAGE
```

Rule:

```text
Receiving notification does not grant access to hidden resource details.
Email payload must be access-safe.
```

---

## 11.14 Phase 21 — AI-assisted Project Planning

Must return to IAM heavily.

Resource types:

```text
AI_AGENT
AI_TOOL
AI_CONTEXT_SOURCE
AI_SUGGESTION
AI_ACTION_EXECUTION
```

Actions:

```text
AI_AGENT_VIEW
AI_AGENT_MANAGE
AI_TOOL_EXECUTE
AI_CONTEXT_READ
AI_PROJECT_PLAN_SUGGEST
AI_SUGGESTION_APPLY
AI_ACT_ON_BEHALF
```

Rules:

```text
1. AI must never grant itself or another actor permission.
2. AI suggestion remains proposal until authorized human commits.
3. Acting-on-behalf-of requires both user permission and agent/tool permission.
4. AI retrieval respects requesting user's effective access.
5. AI output must store agentId, promptVersionId, modelDeploymentId.
```

Events:

```text
AI_PROJECT_PLAN_SUGGESTED
AI_PROJECT_PLAN_APPLIED
AI_ACTION_BLOCKED_BY_IAM
```

---

## 11.15 Phase 22 — Reporting / Dashboard / Export

Must add IAM:

```text
Resource types:
- REPORT
- REPORT_EXPORT
- DASHBOARD
- METRIC_DEFINITION
```

Actions:

```text
REPORT_VIEW
REPORT_CREATE
REPORT_EXPORT
REPORT_MANAGE
DASHBOARD_VIEW
DASHBOARD_MANAGE
METRIC_DEFINITION_MANAGE
```

Rules:

```text
1. Every report applies effective access at query and drill-down time.
2. Aggregates must not leak hidden individual records.
3. Export requires explicit export permission.
4. Finance export requires finance export permission.
```

Events:

```text
REPORT_EXPORT_REQUESTED
REPORT_EXPORT_COMPLETED
REPORT_EXPORT_FAILED
```

---

## 11.16 Phase 23 — Final Security / Compliance Hardening

Must revisit IAM for:

```text
MFA
SSO
Device trust
API tokens
Service accounts
Access request
Access review
Permission simulation
Conditional access
Policy versioning
Segregation of duties
Retention/anonymization
Security audit export
```

This is the phase that closes enterprise readiness gaps.

---

# 12. Phase 02 entities to implement or verify

This section is TO-BE for Phase 02 only.

## 12.1 IamUser — `iam_user`

Required fields:

```text
id UUID PK
username VARCHAR(100) unique
email VARCHAR(255) unique
email_verified BOOLEAN default false if email verification implemented
password_hash VARCHAR(255)
full_name VARCHAR(255)
status VARCHAR(50)
registration_source VARCHAR(50) optional but recommended
password_changed_at TIMESTAMP nullable
last_login_at TIMESTAMP nullable
last_failed_login_at TIMESTAMP nullable
failed_login_count INT default 0
created_at / updated_at / created_by / updated_by
version INT
```

Required status values:

```text
ACTIVE
INACTIVE
SUSPENDED
PENDING_EMAIL_VERIFICATION optional if IDN-002 implemented
```

Do not add PENDING_EMAIL_VERIFICATION unless email verification flow exists.

---

## 12.2 IamRefreshToken / Session

Required if not already equivalent:

```text
id UUID PK
user_id UUID
token_hash VARCHAR(255)
session_id UUID or VARCHAR
issued_at TIMESTAMP
expires_at TIMESTAMP
revoked_at TIMESTAMP nullable
replaced_by_token_id UUID nullable
status VARCHAR(50)
user_agent_hash VARCHAR(255) nullable
ip_hash VARCHAR(255) nullable
created_at / updated_at
```

Rules:

```text
raw token never stored
refresh rotates
revoked/expired rejected
logout revokes
revoke-all revokes by user
```

---

## 12.3 IamPermission / IamPermissionAction

Must support:

```text
permission code
permission scope
assignable subject types
action name
authority code
status
```

Future-ready requirement:

```text
Permission/action seeders must allow later phases to add actions idempotently.
```

---

## 12.4 IamAuthResource

Must support:

```text
resourceType
refId
parentResourceId
organizationId
workspaceId
status
```

Future-ready requirement:

```text
Add support for new resource types without changing authorization engine core.
```

---

## 12.5 IamAccessGrant

Must support:

```text
subjectType USER/TEAM/ROLE/SERVICE_ACCOUNT future
subjectId
resourceId
effect ALLOW/DENY
grantKind DIRECT/TEAM/ROLE/OWNER/DELEGATED
status ACTIVE/REVOKED
expiresAt
canDelegate
delegationDepth
parentGrantId
ownerPolicyId
```

Phase 02 may not implement SERVICE_ACCOUNT subject yet, but design should not block it.

---

## 12.6 IamOwnerPolicy

Must support:

```text
resourceType
inheritanceScope
policyVersion
status
canDelegate
delegationDepth
actions
```

Required current policies:

```text
ORGANIZATION_OWNER
WORKSPACE_OWNER
TEAM_OWNER
```

Future policies:

```text
PROJECT_OWNER — Phase 11/19 if per-project IAM introduced
QUOTE_OWNER — Phase 18
AI_AGENT_OWNER — Phase 21
REPORT_OWNER — Phase 22
```

---

# 13. Required Phase 02 APIs

This is the TO-BE API list. If current API names differ, preserve existing convention but document mapping.

## 13.1 User

```text
POST  /api/iam/users
GET   /api/iam/users/{id}
GET   /api/iam/users
PUT   /api/iam/users/{id}
PATCH /api/iam/users/{id}/activate
PATCH /api/iam/users/{id}/deactivate
PATCH /api/iam/users/{id}/suspend
```

## 13.2 Auth

```text
POST /api/iam/auth/login
POST /api/iam/auth/refresh
POST /api/iam/auth/logout
POST /api/iam/auth/sessions/revoke-all
POST /api/iam/auth/password/change
POST /api/iam/auth/password/reset-request
POST /api/iam/auth/password/reset-confirm
GET  /api/iam/me
```

Optional if email verification implemented:

```text
POST /api/iam/auth/email-verification/send
POST /api/iam/auth/email-verification/confirm
```

Optional/deferred:

```text
GET  /api/iam/auth/sessions
POST /api/iam/auth/sessions/{sessionId}/revoke
```

## 13.3 Role and assignment

```text
POST  /api/iam/roles/system
POST  /api/iam/roles/workspace
GET   /api/iam/roles/{id}
GET   /api/iam/roles
PUT   /api/iam/roles/{id}
PATCH /api/iam/roles/{id}/activate
PATCH /api/iam/roles/{id}/deactivate
PATCH /api/iam/roles/{id}/delete

POST  /api/iam/role-assignments
PATCH /api/iam/role-assignments/{id}/activate
PATCH /api/iam/role-assignments/{id}/deactivate
```

## 13.4 Resource and grant

```text
POST  /api/iam/resources
GET   /api/iam/resources/{id}
GET   /api/iam/resources
PUT   /api/iam/resources/{id}
PATCH /api/iam/resources/{id}/activate
PATCH /api/iam/resources/{id}/deactivate

POST  /api/iam/grants
GET   /api/iam/grants/{id}
GET   /api/iam/grants
POST  /api/iam/grants/{id}:revoke
PATCH /api/iam/grants/{id}/revoke
POST  /api/iam/grants:delegate

POST   /api/iam/grants/{id}/rights
DELETE /api/iam/grants/{id}/rights/{rightId}
POST   /api/iam/grants/{id}/permission-actions
DELETE /api/iam/grants/{id}/permission-actions/{permissionActionId}
```

## 13.5 Authorization

```text
POST /api/iam/authorization/check
POST /api/iam/authorization/check-batch
POST /api/iam/authorization/check-by-right
GET  /api/iam/authorization/explain
```

Optional future:

```text
POST /api/iam/authorization/simulate
```

Simulation is deferred unless implemented in Phase 02.

---

# 14. Phase 02 seed data

## 14.1 Permission/action seed policy

Phase 02 must seed permissions for:

```text
Current backend modules
Near-future Phase 11–18 modules where permissions are known
```

But it must not expose functionality not implemented.

Seeded permission does not equal enabled feature.

---

## 14.2 Current module permission seeds

Must include or verify:

```text
SYSTEM_IAM_*
ORGANIZATION_*
WORKSPACE_*
TEAM_*
PROJECT_*
PROJECT_PHASE_*
PROJECT_WBS_*
PROJECT_TASK_*
AI_AGENT_*
EVENT_REGISTRY_*
NOTIFICATION_*
KNOWLEDGE_*
```

---

## 14.3 Future seed placeholders

It is acceptable to seed these authorities now if they do not expose endpoints yet:

```text
PROJECT_TEMPLATE_VIEW/CREATE/UPDATE/PUBLISH/ARCHIVE
RESOURCE_CALENDAR_VIEW/MANAGE
CAPACITY_VIEW/RECALCULATE
TASK_SCHEDULE_VIEW/RECALCULATE/OVERRIDE
GANTT_VIEW/RECALCULATE/MOVE_TASK
RATE_CARD_VIEW/MANAGE/PUBLISH
ESTIMATE_VIEW/MANAGE/APPROVE
PROJECT_FINANCE_VIEW/MANAGE/APPROVE
QUOTE_VIEW/CREATE/UPDATE/SUBMIT/APPROVE/EXPORT
BASELINE_VIEW/CREATE/APPROVE/RESTORE
CHANGE_REQUEST_VIEW/CREATE/SUBMIT/APPROVE/REJECT/APPLY
REPORT_VIEW/EXPORT/MANAGE
AI_TOOL_EXECUTE
AI_ACT_ON_BEHALF
```

If the current IAM seeder does not support seed-only permissions, mark as deferred and add to future phase.

---

# 15. Phase 02 event registry TO-BE

Phase 02 must seed or verify event definitions for IAM/Identity.

## 15.1 Required identity events

```text
IAM_USER_CREATED
IAM_USER_UPDATED
IAM_USER_ACTIVATED
IAM_USER_DEACTIVATED
IAM_USER_SUSPENDED
IAM_USER_LOGGED_IN
IAM_LOGIN_FAILED
IAM_USER_LOGGED_OUT
IAM_REFRESH_TOKEN_ROTATED
IAM_SESSIONS_REVOKED
IAM_PASSWORD_CHANGED
IAM_PASSWORD_RESET_REQUESTED
IAM_PASSWORD_RESET_COMPLETED
```

Optional if implemented:

```text
IAM_EMAIL_VERIFICATION_REQUESTED
IAM_EMAIL_VERIFIED
```

Deferred:

```text
IAM_MFA_ENABLED
IAM_MFA_CHALLENGE_FAILED
IAM_SSO_LOGIN_SUCCEEDED
IAM_DEVICE_REGISTERED
IAM_API_TOKEN_CREATED
IAM_SERVICE_ACCOUNT_CREATED
```

## 15.2 Required IAM policy events

```text
IAM_ROLE_CREATED
IAM_ROLE_UPDATED
IAM_ROLE_DELETED
IAM_ROLE_ASSIGNED
IAM_ROLE_ASSIGNMENT_DEACTIVATED
IAM_RESOURCE_REGISTERED
IAM_RESOURCE_DEACTIVATED
IAM_ACCESS_GRANTED
IAM_ACCESS_REVOKED
IAM_GRANT_ACTION_ATTACHED
IAM_GRANT_ACTION_REMOVED
IAM_OWNER_POLICY_CREATED
IAM_OWNER_GRANT_BOOTSTRAPPED
IAM_DELEGATION_CREATED
IAM_DELEGATION_REJECTED
IAM_AUTHORIZATION_DENIED
```

## 15.3 Event payload rules

Never include:

```text
raw password
password hash
raw reset token
raw access token
raw refresh token
authorization header
full IP address unless privacy approved
```

Use:

```text
userId
actorUserId
resourceType
resourceRefId
grantId
authority
decision
reasonCode
occurredAt
traceId
```

## 15.4 Event seeder tests

Required:

```text
iamEventDefinitions_seededIdempotently
iamEventVariables_seededIdempotently
iamEventDefinitions_doNotContainSecrets
iamEventDefinitions_haveStableSourceSystem
```

Suggested source system:

```text
SCOPERY_IAM
```

---

# 16. Phase 02 email template seeder TO-BE

## 16.1 Must seed or verify

```text
IAM_PASSWORD_RESET_REQUEST_EMAIL
```

Required variables:

```text
user.fullName
reset.url
reset.expiresAt
support.email
```

Rules:

```text
1. The public reset-request response must always be generic.
2. Email is only sent if user exists.
3. Raw token appears only inside reset.url.
4. Token is never logged.
```

## 16.2 Optional if email verification implemented

```text
IAM_EMAIL_VERIFICATION_EMAIL
```

Required variables:

```text
user.fullName
verification.url
verification.expiresAt
support.email
```

## 16.3 Optional security notices

```text
IAM_PASSWORD_CHANGED_NOTICE_EMAIL
IAM_ACCOUNT_SUSPENDED_NOTICE_EMAIL
IAM_NEW_LOGIN_NOTICE_EMAIL
```

Do not enable high-noise emails without product decision.

---

# 17. Phase 02 business rules

## 17.1 Identity rules

```text
1. Username unique and normalized.
2. Email unique and normalized.
3. Password hashed.
4. Password hash never returned.
5. User status gates login.
6. Password reset does not leak account existence.
7. Refresh token rotates.
8. Revoked refresh token cannot refresh.
9. Expired refresh token cannot refresh.
10. Logout clears cookies and revokes token.
11. Password change/reset revokes sessions.
12. All sensitive auth actions audited.
```

## 17.2 IAM policy rules

```text
1. Resource must be active before grant.
2. Grant with no attached right/action grants nothing.
3. Expired grant is ineffective.
4. Revoked grant is ineffective.
5. Explicit DENY overrides all ALLOW sources.
6. Direct, team, role, owner, delegated grants can contribute to decision.
7. Team grant requires active team membership.
8. Role grant requires active role assignment and active role.
9. Permission action scope must match resource type.
10. Subject type must be allowed by permission.
11. Delegation cannot exceed source grant.
12. Cross-organization grants are rejected by default.
13. Owner grant bootstrap failure must fail the owning resource creation if current business rule says so.
14. Authorization deny writes immutable audit.
```

---

# 18. Phase 02 test cases

This section is TO-BE and must not be treated as optional.

## 18.1 Current implemented flow tests

```text
createUser_valid_success
createUser_duplicateUsername_throwsConflict
createUser_duplicateEmail_throwsConflict
createUser_passwordIsHashed
login_valid_setsCookies
login_unknownUser_genericInvalidCredentials
login_wrongPassword_genericInvalidCredentials
login_inactiveUser_rejected
login_suspendedUser_rejected
refresh_valid_rotatesToken
refresh_revoked_rejected
refresh_expired_rejected
logout_valid_revokesTokenAndClearsCookie
revokeAllSessions_revokesAllForCurrentUser
changePassword_wrongCurrentPassword_rejected
changePassword_valid_revokesSessions
resetRequest_unknownEmail_outwardSuccess
resetRequest_existingEmail_createsTokenAndEvent
resetConfirm_valid_updatesPasswordAndRevokesSessions
```

## 18.2 IAM policy tests

```text
createSystemRole_duplicateCode_conflict
createWorkspaceRole_duplicateWithinWorkspace_conflict
softDeleteSystemBuiltInRole_rejected
assignRole_userNotWorkspaceMember_rejected
assignRole_duplicateActive_conflict
createResource_duplicateCodeType_conflict
manualCreateNonGlobalResource_rejected
createGrant_inactiveResource_rejected
createGrant_duplicate_conflict
attachPermissionAction_scopeMismatch_rejected
attachPermissionAction_subjectTypeNotAllowed_rejected
delegateAccess_withoutCanDelegate_forbidden
delegateAccess_depthExceeded_forbidden
delegateAccess_actionNotCovered_forbidden
delegateAccess_crossOrgRejected
authorization_directAllow_allowed
authorization_noGrant_defaultDeny
authorization_directDenyOverridesAllow_denied
authorization_teamAllow_allowed
authorization_teamDenyOverridesUserAllow_denied
authorization_roleAllow_allowed
authorization_inactiveRoleAssignment_ignored
authorization_ownerGrant_allowed
authorization_revokedGrant_ignored
authorization_expiredGrant_ignored
authorization_inactiveUser_denied
authorization_inactiveResource_denied
authorizationDenied_writesAudit
```

## 18.3 Seeder tests

```text
permissionSeeder_firstRun_createsRequiredPermissions
permissionSeeder_secondRun_noDuplicates
ownerPolicySeeder_firstRun_createsRequiredPolicies
ownerPolicySeeder_secondRun_noDuplicates
iamEventSeeder_firstRun_createsEvents
iamEventSeeder_secondRun_noDuplicates
passwordResetEmailTemplateSeeder_firstRun_createsTemplate
passwordResetEmailTemplateSeeder_secondRun_noDuplicates
```

## 18.4 Future gap tests must be marked deferred

Do not write failing tests for deferred features.

Instead, create TODO section in completion file:

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

---

# 19. Phase 02 acceptance criteria

Phase 02 is accepted only if:

```text
1. Agent classified current vs future IAM capabilities.
2. Required current IAM/auth flows are implemented or verified.
3. Missing Phase 02 items are implemented.
4. Deferred future features are explicitly documented with phase number.
5. IAM resource/action model can accept future module permissions.
6. Permission/action seeders are idempotent.
7. Owner policies are idempotent.
8. IAM/identity events are seeded or explicitly deferred.
9. Password reset email template is seeded or confirmed existing.
10. DENY overrides ALLOW.
11. Revoked/expired grants are ignored.
12. Cross-org grant attempts are blocked.
13. Sensitive data is not logged or returned.
14. mvn compile passes.
15. mvn test passes.
16. Completion file includes gap matrix.
```

---

# 20. Required Phase 02 completion file

Agent must create:

```text
docs/phase-complete/PHASE_02_IAM_CORE_TO_BE_COMPLETE.md
```

Required sections:

```text
# Phase 02 — IAM Core TO-BE Complete

## 1. Summary
## 2. Source Inputs Reviewed
## 3. Current vs TO-BE Classification Matrix
## 4. Implemented in Current BE
## 5. Implemented / Hardened in This Phase
## 6. Seed-only Items Added
## 7. Deferred Items and Target Phase
## 8. Entities Created / Modified
## 9. APIs Created / Modified
## 10. Business Rules Implemented
## 11. Business Rules Deferred
## 12. IAM Resource Types Supported Now
## 13. IAM Resource Types Deferred
## 14. Permission/Action Seeder Matrix
## 15. Owner Policy Seeder Matrix
## 16. Event Registry Seeder Matrix
## 17. Email Template Seeder Matrix
## 18. Security Notes
## 19. Tests Added
## 20. Commands Run
## 21. Test Results
## 22. Manual Verification
## 23. Assumptions
## 24. Deviations From Prompt
## 25. Risks
## 26. Future Phases That Must Return to IAM
```

---

# 21. Agent anti-bịa rules

The agent must not:

```text
1. Treat current IAM inventory as complete future-state.
2. Claim MFA/SSO/API tokens/service accounts exist unless code proves it.
3. Implement fake placeholder endpoints that do nothing.
4. Seed permissions without documenting whether feature exists or is future-only.
5. Make endpoints public to pass tests.
6. Disable CSRF.
7. Store raw tokens.
8. Log passwords/tokens.
9. Bypass workspace/org membership checks.
10. Create cross-org grants unless federation exists.
11. Claim event emission works if only event definitions are seeded.
12. Claim email templates exist without seeder/test evidence.
13. Forget to list future phases that must return to IAM.
```

---

# 22. Prompt to give coding agent

```text
You are implementing Phase 02 — TO-BE IAM & Identity Core.

This is not an as-is documentation task.

Before coding, read:
- CLAUDE.md / CLAUDE.ms
- Coding_convention.md
- Phase 00 master roadmap
- Phase 01 security baseline
- Current BE feature/entity/business-rule inventory
- Dynamic Work OS future-state feature catalog
- Existing IAM code, migrations, seeders, tests
- Existing workspace/project/aiagent/notification/knowledge code

Your task:
1. Compare current IAM against this TO-BE Phase 02 spec.
2. Classify every Identity/IAM capability as:
   CURRENTLY_IMPLEMENTED,
   MUST_IMPLEMENT_IN_PHASE_02,
   SEED_ONLY_IN_PHASE_02,
   DEFERRED_TO_PHASE_XX,
   or NOT_IN_SCOPE_FOR_BACKEND_NOW.
3. Implement only Phase 02 required items.
4. Do not implement deferred features such as MFA, SSO, API tokens, service accounts, access reviews, SoD, AI acting-on-behalf, or finance permissions beyond seed-only unless this spec says so.
5. Add or verify IAM resource/action/permission seeders for current modules and future modules listed as seed-only.
6. Add or verify IAM event definitions.
7. Add or verify password reset email template seeder.
8. Add tests for all Phase 02 required rules.
9. Run mvn compile and mvn test.
10. Create docs/phase-complete/PHASE_02_IAM_CORE_TO_BE_COMPLETE.md with the full gap matrix.

Do not write "complete" if deferred gaps are not documented.
Do not claim future-state features exist just because current IAM has generic grants.
```

---

# 23. Quick tracking matrix

| Capability group | Current backend | Phase 02 action | Later phase |
|---|---|---|---|
| User create/login/password reset | Mostly present | Harden/test | — |
| Email verification | Not confirmed | Implement or defer clearly | Phase 03/23 |
| MFA | Missing | Defer | Phase 23 |
| SSO/social login | Missing | Defer | Phase 23 |
| Session revoke/rotation | Present | Harden/test | Phase 23 for session viewer |
| Service accounts | Missing | Defer | Phase 21/37 |
| API tokens | Missing | Defer | Phase 37 |
| AuthResource | Present | Harden/future-proof | Every module returns |
| Permission/action catalog | Present | Harden/seed future | Every module returns |
| Direct/team/role grants | Present | Harden/test | — |
| Owner policies | Present | Harden/seed | Future resources return |
| Delegation | Present | Harden/test | — |
| Conditional access | Missing | Defer | Phase 23/38 |
| Temporary access | Partially present via expiresAt | Harden/test | Phase 20 notification |
| Access request | Missing | Defer | Phase 32 |
| Access review | Missing | Defer | Phase 38 |
| Effective access viewer | Partially present | Harden or defer | Phase 22 |
| Permission simulation | Missing | Defer | Phase 23/32 |
| SoD | Missing | Defer | Phase 18/19/23 |
| AI agent IAM | Missing/partial | Defer | Phase 21 |
| Finance IAM | Missing | Seed-only or defer | Phase 17 |
| Quote IAM | Missing | Seed-only or defer | Phase 18 |
| Document IAM | Missing except DocumentType | Defer | Phase 08/Document Hub |
| Reporting IAM | Missing | Defer | Phase 22 |

---

# 24. Final principle

Phase 02 is not done when current login works.

Phase 02 is done when Scopery has a clear IAM foundation that can safely support:

```text
current modules
future project planning
documents
capacity
Gantt
rate cards
finance
quotes
baselines
change requests
AI agents
reports
integrations
compliance
```

Current implemented features must be verified.

Missing future features must be clearly marked and assigned to a phase.

No silent gaps.
