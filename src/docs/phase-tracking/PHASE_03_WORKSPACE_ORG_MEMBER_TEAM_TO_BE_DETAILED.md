# PHASE 03 — TO-BE Organization, Workspace, Membership, Team & Tenant Collaboration Core

> Project: Scopery Backend  
> Phase: 03  
> Document type: TO-BE implementation-grade specification  
> Status: Planning / Gap-driven implementation spec  
> Depends on: Phase 00 — Master Backend Roadmap, Phase 01 — API Path & Security Baseline, Phase 02 — TO-BE IAM & Identity Core  
> API base: `/api`  
> Primary modules: `modules/workspace`, `modules/iam` integration points  
> Important rule: This file is **not an as-is document**. It compares current Workspace/Organization backend against the future-state Work OS and marks what is implemented, what must be hardened now, what is seed-only, and what is deferred to later phases.

---

# 0. Purpose of this file

Phase 03 defines the tenant and collaboration foundation for Scopery.

The previous backend already has many workspace features, but Phase 03 must not assume current implementation is the final Work OS model.

This file defines:

```text
1. The TO-BE Organization / Workspace / Member / Team model.
2. The gap between current backend and future-state Work OS capabilities.
3. What must be implemented or hardened in Phase 03.
4. What must be intentionally deferred and to which phase.
5. Which later phases must return to Workspace/IAM to add resource types, rules, events, or membership effects.
6. Exact business rules, constraints, entities, APIs, events, email templates, seeders, test cases, and acceptance criteria.
```

Phase 03 is important because every later module is workspace-scoped or organization-scoped:

```text
Project
WBS
Task
Resource calendar
Gantt
Rate card
Finance
Quote
Baseline
Change request
Documents
AI context
Notification rules
Reports
External collaboration
Compliance
```

If tenant boundaries are wrong, the whole Work OS is unsafe.

---

# 1. Required source inputs

Before coding or changing anything, the agent must read:

```text
1. Current backend codebase
2. Current BE feature/entity/business-rule inventory
3. Dynamic Work OS future-state feature catalog
4. Phase 00 master roadmap
5. Phase 01 API/security contract
6. Phase 02 TO-BE IAM core
7. Existing workspace migrations
8. Existing workspace module actions/query services/controllers
9. Existing IAM integration services
10. Existing notification/event registry/outbox patterns
11. Existing project module, because Project depends on Workspace
```

The agent must not code only from current implementation.

---

# 2. Current backend snapshot

Current BE inventory says the Workspace module currently has:

```text
15 action-level functions
72 business rules
```

Current Workspace module functional groups include:

```text
Organization Create
Organization Update / Activate / Archive
Workspace Create
Workspace Update / Activate / Archive
Workspace Member Add / Activate / Deactivate
Workspace Team CRUD + Members
Workspace Invitation Create
Workspace Invitation Accept
Workspace Invitation Revoke
Join Request Create / Approve / Reject / Cancel
Onboarding FSM
Switch Workspace Context
Org Member Add / Activate / Suspend / Remove
Org Invitation Create / Accept / Cancel
Org Team + Workspace Assignment
```

Current Workspace-related entities include:

```text
Organization
Workspace
WorkspaceMember
WorkspaceTeam
WorkspaceTeamMember
WorkspaceInvitation
WorkspaceJoinRequest
WorkspaceUserContext
WorkspaceOnboardingState
OrgMember
OrgInvitation
OrgTeam
OrgTeamMember
OrgTeamWorkspaceAssignment
```

Current important entity chain:

```text
Organization
  ├─ Workspace
  │   ├─ WorkspaceMember
  │   ├─ WorkspaceTeam / WorkspaceTeamMember
  │   ├─ WorkspaceInvitation
  │   ├─ WorkspaceJoinRequest
  │   └─ Project
  ├─ OrgMember
  ├─ OrgInvitation
  └─ OrgTeam
      └─ OrgTeamWorkspaceAssignment
```

Current IAM integration:

```text
Organization / Workspace / Team resources are bootstrapped into IAM.
Owner access is bootstrapped through IAM owner grant.
Workspace/team/org membership changes can affect grants and role assignments.
```

This is strong for current MVP, but not full Work OS future-state.

---

# 3. Future-state Work OS capability areas that affect Phase 03

Phase 03 must consider these future-state modules:

```text
Organization Management
Workspace Management
Team and People Directory
External Party and Client CRM
Customer and External Collaboration Portal
Employee Check-in and Performance
Workflow, Approval, and Automation
Notification and Subscription
Audit, Compliance, Privacy, and Retention
Reporting, Analytics, and Portfolio Intelligence
AI Platform and Agent Governance
```

Important consequence:

```text
Current Org/Workspace/Team is not just membership CRUD.
It must become the stable tenant boundary for all future access, cost, people, collaboration, client, AI, reporting, and compliance modules.
```

---

# 4. Classification labels

Every requirement in this file uses one label.

| Label | Meaning |
|---|---|
| `CURRENTLY_IMPLEMENTED` | Current backend already implements it. Agent must verify and test. |
| `MUST_IMPLEMENT_IN_PHASE_03` | Must be implemented or hardened now. |
| `SEED_ONLY_IN_PHASE_03` | Create permission/action/event/email seeds now; full feature later. |
| `DEFERRED_TO_PHASE_XX` | Do not implement now; later phase must return to Workspace/IAM. |
| `NOT_IN_SCOPE_FOR_BACKEND_NOW` | Explicitly not in backend scope for this roadmap. |

---

# 5. Phase 03 target statement

Phase 03 must deliver a future-ready tenant core:

```text
1. Organization lifecycle and ownership.
2. Workspace lifecycle, settings, visibility, join policy.
3. Org membership and workspace membership with strong status transitions.
4. Team model and team membership.
5. Invitation and join request flows.
6. Onboarding flow for new users.
7. Current workspace context switching.
8. IAM resource bootstrap for org/workspace/team.
9. Access cascade behavior when org/workspace/team membership changes.
10. Event registry exports for tenant lifecycle.
11. Email template seeders for invitations and membership events.
12. Tests that prove tenant isolation and membership rules.
13. A future gap matrix for client portal, external party, people directory, HR/performance, and org-level policies.
```

---

# 6. Phase 03 must implement now vs defer

## 6.1 Must implement / harden in Phase 03

```text
Organization create/update/archive/activate if current product supports activate
Organization owner membership bootstrap
Organization IAM resource bootstrap
Organization owner IAM grant bootstrap
Workspace create/update/archive/activate
Workspace owner membership bootstrap
Workspace IAM resource bootstrap
Workspace owner IAM grant bootstrap
Workspace member add/activate/deactivate
Workspace invitation create/accept/revoke
Workspace join request create/approve/reject/cancel
Workspace onboarding FSM
Workspace context switch
Org member add/activate/suspend/remove
Org invitation create/accept/cancel
Org team create/update/archive
Org team member add/remove
Org team workspace assignment/revoke
Workspace team view if current model supports workspace-level team view
Cross-organization isolation
Membership cascade and access revoke
Idempotent seeders
Event registry exports
Email template seeders
Regression tests
```

## 6.2 Defer but explicitly track

```text
Org billing/account subscription — DEFERRED_TO_COMMERCIAL_OR_PLATFORM_ADMIN_PHASE
External party/client CRM — DEFERRED_TO_PHASE_29_CUSTOMER_EXTERNAL_COLLABORATION or CRM phase
Client portal membership — DEFERRED_TO_PHASE_29
Employee profile/HR/performance — DEFERRED_TO_PHASE_31
Org security policy/MFA/SSO — DEFERRED_TO_PHASE_23_SECURITY_HARDENING
Access review/certification — DEFERRED_TO_PHASE_38_COMPLIANCE
Data retention/anonymization — DEFERRED_TO_PHASE_38
Workspace custom schema/configuration — DEFERRED_TO_PHASE_39_DYNAMIC_CONFIGURATION
Advanced workflow approvals for access requests — DEFERRED_TO_PHASE_32_WORKFLOW_AUTOMATION
```

---

# 7. TO-BE capability matrix — Organization Management

---

## 7.1 ORG-001 — Organization creation

| Item | Value |
|---|---|
| Future-state capability | Create tenant organization with owner, IAM resource, audit, event |
| Current state | Organization Create exists |
| Phase 03 target | Verify/harden |
| Classification | `MUST_IMPLEMENT_IN_PHASE_03` |

Required TO-BE business rules:

```text
1. Actor must be authenticated.
2. ownerUserId = current user unless admin provisioning explicitly supports another owner.
3. Organization code is required.
4. Organization code is normalized uppercase.
5. Organization code matches ^[A-Z0-9_]{2,100}$ unless existing regex differs.
6. Organization code is globally unique.
7. Organization starts ACTIVE.
8. version starts 0.
9. Owner enrolled as OrgMember with membershipType OWNER.
10. Owner OrgMember source = SYSTEM_BOOTSTRAP.
11. IAM ORGANIZATION resource is created.
12. IAM owner grant is bootstrapped.
13. If IAM bootstrap fails, organization create fails with workspaceIamBootstrapFailed or equivalent.
14. Outbox event ORGANIZATION_CREATED is created transactionally.
15. Immutable audit and activity log are recorded.
```

Required event:

```text
ORGANIZATION_CREATED
```

Required tests:

```text
createOrganization_valid_success
createOrganization_duplicateCode_conflict
createOrganization_codeNormalizedUppercase
createOrganization_createsOwnerOrgMember
createOrganization_bootstrapsIamResource
createOrganization_bootstrapsOwnerGrant
createOrganization_iamBootstrapFails_rollsBackOrThrows
createOrganization_emitsOrganizationCreatedOutbox
```

---

## 7.2 ORG-002 — Organization update

| Item | Value |
|---|---|
| Future-state capability | Update organization metadata without changing immutable identity |
| Current state | Organization update exists |
| Phase 03 target | Verify/harden |
| Classification | `MUST_IMPLEMENT_IN_PHASE_03` |

Rules:

```text
1. Organization must exist.
2. ARCHIVED organization cannot be updated.
3. Code immutable.
4. ownerUserId immutable.
5. Only name/description/profile metadata updatable in Phase 03.
6. Updating organization requires ORGANIZATION_UPDATE or ORGANIZATION_MANAGE.
7. Activity log ORGANIZATION_UPDATED.
8. Event ORGANIZATION_UPDATED optional but recommended.
```

Do not implement billing/account settings here.

Deferred:

```text
Organization billing profile — deferred to commercial/admin phase.
Organization security policy — deferred to Phase 23.
Organization retention policy — deferred to Phase 38.
```

---

## 7.3 ORG-003 — Organization activation/archive

| Item | Value |
|---|---|
| Future-state capability | Control org lifecycle and preserve auditability |
| Current state | Activate/archive exists according to inventory |
| Phase 03 target | Verify business decision |
| Classification | `MUST_IMPLEMENT_IN_PHASE_03` for archive; activate depends on current status model |

Required rules:

```text
1. Organization must exist.
2. ARCHIVED organization cannot be updated.
3. Archive sets status ARCHIVED.
4. Archive should be terminal unless product explicitly supports restore.
5. Archiving organization must block new workspace creation.
6. Archiving organization must not physically delete workspaces/members/projects.
7. IAM resource should be deactivated or marked archived according to IAM resource lifecycle contract.
8. Activity log ORGANIZATION_ARCHIVED.
9. Event ORGANIZATION_ARCHIVED.
```

If current implementation has Activate:

```text
Activation is allowed only from INACTIVE, not from ARCHIVED.
```

If there is no INACTIVE org state:

```text
Remove/ignore activate for org and document.
```

Test:

```text
archiveOrganization_blocksFutureWorkspaceCreate
archiveOrganization_doesNotDeleteChildren
archiveOrganization_updatesIamResourceLifecycle
```

---

## 7.4 ORG-004 — Organization owner management

| Item | Value |
|---|---|
| Future-state capability | Preserve at least one active owner, support owner transfer |
| Current state | Owner created on org create; cannot remove only OWNER mentioned in current rules |
| Phase 03 target | Harden owner guard; owner transfer may be deferred |
| Classification | `MUST_IMPLEMENT_IN_PHASE_03` for guard; `DEFERRED_TO_PHASE_23_OR_ADMIN` for formal owner transfer if absent |

Rules:

```text
1. Organization must always have at least one ACTIVE OWNER.
2. Cannot suspend/remove the only active OWNER.
3. Adding another OWNER requires ORGANIZATION_MANAGE_MEMBER or equivalent.
4. Owner membership source is tracked.
5. Owner grant exists in IAM.
6. Removing/suspending an owner revokes or deactivates owner-specific grants if no other owner grant applies.
```

Optional future API:

```text
POST /api/organizations/{organizationId}/owners/transfer
```

Deferred if absent:

```text
Formal owner transfer workflow — DEFERRED_TO_PHASE_32_WORKFLOW_APPROVAL or Phase 23 admin hardening.
```

---

## 7.5 ORG-005 — Organization settings

| Item | Value |
|---|---|
| Future-state capability | Manage org-level defaults for workspace creation, security, collaboration |
| Current state | Basic organization entity only |
| Phase 03 target | Implement minimal settings only if already present; otherwise defer |
| Classification | `DEFERRED_TO_PHASE_23` for security policy; `DEFERRED_TO_PHASE_39` for dynamic settings |

Possible future settings:

```text
defaultWorkspaceVisibility
allowedJoinPolicies
allowedEmailDomains
requireEmailVerification
requireMfa
defaultLocale
defaultTimezone
dataRetentionPolicy
externalCollaborationAllowed
aiFeaturesAllowed
```

Phase 03 must not invent broad settings unless product needs them now.

---

## 7.6 ORG-006 — Organization member management

| Item | Value |
|---|---|
| Future-state capability | Manage membership to tenant organization |
| Current state | Org Member Add / Activate / Suspend / Remove exists |
| Phase 03 target | Verify/harden |
| Classification | `MUST_IMPLEMENT_IN_PHASE_03` |

Required entities:

```text
OrgMember
```

Required rules:

```text
1. Organization must exist and be ACTIVE for adding a member.
2. User must exist.
3. Membership must not already exist for (organizationId, userId).
4. membershipType required.
5. source = MANUAL for manual add.
6. Initial status ACTIVE.
7. REMOVED cannot be activated.
8. OWNER cannot be suspended or removed if only active owner.
9. Suspend/remove must revoke descendant access:
   - deactivate workspace memberships in organization
   - remove/deactivate org-team memberships
   - revoke USER grants
   - deactivate role assignments
10. Activity log and event required.
```

Status model:

```text
ACTIVE
INACTIVE optional
SUSPENDED
REMOVED
```

Required tests:

```text
addOrgMember_valid_success
addOrgMember_duplicate_conflict
addOrgMember_inactiveOrg_rejected
activateRemovedOrgMember_rejected
suspendOnlyOwner_rejected
removeOnlyOwner_rejected
suspendOrgMember_revokesWorkspaceMemberships
suspendOrgMember_revokesUserGrants
removeOrgMember_deactivatesRoleAssignments
```

---

## 7.7 ORG-007 — Organization invitation

| Item | Value |
|---|---|
| Future-state capability | Invite users into organization independently of workspace |
| Current state | Org Invitation Create / Accept / Cancel exists |
| Phase 03 target | Verify/harden |
| Classification | `MUST_IMPLEMENT_IN_PHASE_03` |

Required rules:

```text
1. Organization must be ACTIVE.
2. Inviter must have ORGANIZATION_INVITE_MEMBER or ORGANIZATION_MANAGE_MEMBER.
3. membershipType defaults MEMBER.
4. Expiry defaults now + 7 days if not provided.
5. Raw token/code generated securely.
6. Store token hash, not raw token.
7. Response returns raw token/code once only.
8. Accept requires PENDING and not expired.
9. Accept blocked if user already org member.
10. Accept creates OrgMember with source ORGANIZATION_INVITATION.
11. Cancel only allowed from PENDING.
12. Event/email generated if configured.
```

Required events:

```text
ORG_INVITATION_CREATED
ORG_INVITATION_ACCEPTED
ORG_INVITATION_CANCELLED
ORG_INVITATION_EXPIRED optional job event
```

Required email template:

```text
ORG_INVITATION_CREATED_EMAIL
```

---

## 7.8 ORG-008 — Organization team

| Item | Value |
|---|---|
| Future-state capability | Org-scoped team for permission and workspace assignment |
| Current state | Org Team + Workspace Assignment exists; teams org-scoped as of V33+ |
| Phase 03 target | Verify/harden |
| Classification | `MUST_IMPLEMENT_IN_PHASE_03` |

Required rules:

```text
1. Organization must exist and be ACTIVE.
2. Actor must have TEAM_CREATE or ORGANIZATION_MANAGE_TEAM.
3. Team code unique within organization.
4. Team starts ACTIVE.
5. IAM team resource bootstrapped.
6. Add member requires active OrgMember.
7. Remove member removes effective team access immediately.
8. Archived team cannot be updated or assigned.
9. Activity log and events required.
```

Required events:

```text
ORG_TEAM_CREATED
ORG_TEAM_UPDATED
ORG_TEAM_ARCHIVED
ORG_TEAM_MEMBER_ADDED
ORG_TEAM_MEMBER_REMOVED
```

Required tests:

```text
createOrgTeam_valid_success
createOrgTeam_duplicateCode_conflict
createOrgTeam_bootstrapsIamResource
addOrgTeamMember_nonOrgMember_rejected
archiveOrgTeam_blocksUpdate
removeOrgTeamMember_removesEffectiveAccess
```

---

## 7.9 ORG-009 — Org team workspace assignment

| Item | Value |
|---|---|
| Future-state capability | Grant team visibility/participation in one or more workspaces |
| Current state | OrgTeamWorkspaceAssignment exists |
| Phase 03 target | Verify/harden |
| Classification | `MUST_IMPLEMENT_IN_PHASE_03` |

Rules:

```text
1. Team must exist and be ACTIVE.
2. Workspace must exist and be ACTIVE.
3. Team and workspace must belong to same organization.
4. No duplicate ACTIVE assignment.
5. Cross-org assignment rejected.
6. Revocation changes status REVOKED, not hard delete, unless current convention says otherwise.
7. Team assignment may bootstrap workspace-level access grants if product requires.
8. Event and activity log required.
```

Events:

```text
ORG_TEAM_ASSIGNED_TO_WORKSPACE
ORG_TEAM_REVOKED_FROM_WORKSPACE
```

Tests:

```text
assignOrgTeamToWorkspace_valid_success
assignOrgTeamToWorkspace_crossOrg_rejected
assignOrgTeamToWorkspace_duplicateActive_conflict
revokeOrgTeamWorkspaceAssignment_success
revokedAssignment_removesEffectiveWorkspaceAccess
```

---

# 8. TO-BE capability matrix — Workspace Management

---

## 8.1 WS-001 — Workspace creation

| Item | Value |
|---|---|
| Future-state capability | Create working tenant area under active organization |
| Current state | Workspace Create exists |
| Phase 03 target | Verify/harden |
| Classification | `MUST_IMPLEMENT_IN_PHASE_03` |

Required rules:

```text
1. Organization must exist.
2. Organization must be ACTIVE.
3. Actor must have ORGANIZATION_CREATE_WORKSPACE on organization.
4. Workspace code required.
5. Workspace code normalized uppercase.
6. Workspace code unique within organization.
7. defaultVisibility defaults PRIVATE.
8. joinPolicy defaults INVITE_ONLY.
9. Workspace starts ACTIVE.
10. Owner becomes active WorkspaceMember.
11. Owner WorkspaceMember source = SYSTEM_BOOTSTRAP.
12. IAM workspace resource bootstrapped.
13. IAM owner grant bootstrapped.
14. Outbox event WORKSPACE_CREATED.
15. Activity log and immutable audit.
```

Required tests:

```text
createWorkspace_valid_success
createWorkspace_inactiveOrg_rejected
createWorkspace_duplicateCodeWithinOrg_conflict
createWorkspace_sameCodeDifferentOrg_allowed
createWorkspace_defaultVisibilityPrivate
createWorkspace_defaultJoinPolicyInviteOnly
createWorkspace_createsOwnerMember
createWorkspace_bootstrapsIamResource
createWorkspace_bootstrapsOwnerGrant
createWorkspace_emitsEvent
```

---

## 8.2 WS-002 — Workspace update

| Item | Value |
|---|---|
| Future-state capability | Update workspace metadata and membership policy |
| Current state | Workspace update exists |
| Phase 03 target | Verify/harden |
| Classification | `MUST_IMPLEMENT_IN_PHASE_03` |

Rules:

```text
1. Workspace must exist.
2. ARCHIVED workspace cannot be updated.
3. Code immutable.
4. ownerUserId immutable.
5. organizationId immutable.
6. Null defaultVisibility/joinPolicy keep existing.
7. joinPolicy must be valid enum.
8. defaultVisibility must be valid enum.
9. Actor must have WORKSPACE_UPDATE.
10. Event WORKSPACE_UPDATED.
```

Allowed fields:

```text
name
description
defaultVisibility
joinPolicy
```

Deferred fields:

```text
billing/project defaults — later finance/admin phase
AI feature toggles — Phase 21 / Dynamic config
custom schema — Phase 39
```

---

## 8.3 WS-003 — Workspace lifecycle

| Item | Value |
|---|---|
| Future-state capability | Activate/archive workspace safely |
| Current state | Activate/archive exists |
| Phase 03 target | Verify/harden |
| Classification | `MUST_IMPLEMENT_IN_PHASE_03` |

Rules:

```text
1. Workspace must exist.
2. ARCHIVED cannot be updated.
3. ARCHIVED cannot be activated unless product supports restore.
4. Archive sets status ARCHIVED.
5. Archive blocks new members, invites, join requests, teams, projects.
6. Archive does not delete data.
7. IAM workspace resource deactivated/archived.
8. Existing projects remain auditable; behavior defined by Project phase.
9. Event WORKSPACE_ARCHIVED.
```

Potential restore:

```text
DEFERRED_TO_PHASE_23 or explicit restore phase.
```

Tests:

```text
archiveWorkspace_blocksNewInvitation
archiveWorkspace_blocksJoinRequest
archiveWorkspace_blocksMemberAdd
archiveWorkspace_blocksProjectCreate
archiveWorkspace_updatesIamResource
```

---

## 8.4 WS-004 — Workspace membership

| Item | Value |
|---|---|
| Future-state capability | Manage who participates in workspace |
| Current state | Workspace Member Add / Activate / Deactivate exists |
| Phase 03 target | Verify/harden |
| Classification | `MUST_IMPLEMENT_IN_PHASE_03` |

Rules:

```text
1. Workspace must exist.
2. Workspace must be ACTIVE for add/activate.
3. User must be active OrgMember of workspace.organizationId.
4. Member must not already exist for workspaceId + userId.
5. Add creates ACTIVE WorkspaceMember.
6. Activate blocked if workspace inactive.
7. Deactivate should remove effective workspace team access.
8. Deactivate should revoke or inactivate workspace-scoped USER grants and role assignments if current IAM cascade requires.
9. IDOR check: memberId must belong to workspaceId.
10. Activity log and event required.
```

Status model:

```text
ACTIVE
INACTIVE
REMOVED optional if soft removal implemented
```

Tests:

```text
addWorkspaceMember_valid_success
addWorkspaceMember_userNotOrgMember_rejected
addWorkspaceMember_inactiveOrgMember_rejected
addWorkspaceMember_duplicate_conflict
activateWorkspaceMember_inactiveWorkspace_rejected
deactivateWorkspaceMember_revokesTeamMembership
deactivateWorkspaceMember_revokesWorkspaceGrants
memberIdFromOtherWorkspace_rejected
```

---

## 8.5 WS-005 — Workspace invitation create

| Item | Value |
|---|---|
| Future-state capability | Invite user to workspace via secure code/link |
| Current state | Workspace invitation create exists |
| Phase 03 target | Verify/harden |
| Classification | `MUST_IMPLEMENT_IN_PHASE_03` |

Rules:

```text
1. Actor must have WORKSPACE_INVITE_MEMBER.
2. Workspace must exist and be ACTIVE.
3. Raw invitation code generated using secure random.
4. Raw code never stored plaintext.
5. Store SHA-256 or equivalent hash.
6. Store hint only, such as first 4 chars.
7. status PENDING.
8. usedCount = 0.
9. maxUses optional; null means unlimited if product allows.
10. expiresAt required or defaults to configured duration.
11. Response returns raw code once only.
12. List/get responses must not return raw code.
13. Optional invitedEmail stored if provided.
14. If sendEmail=true, enqueue email with raw code/link.
15. Event WORKSPACE_INVITATION_CREATED.
```

Tests:

```text
createWorkspaceInvitation_valid_returnsRawCodeOnce
createWorkspaceInvitation_listDoesNotReturnRawCode
createWorkspaceInvitation_storesHashOnly
createWorkspaceInvitation_inactiveWorkspace_rejected
createWorkspaceInvitation_withoutPermission_forbidden
createWorkspaceInvitation_sendEmail_enqueuesOutbox
```

---

## 8.6 WS-006 — Workspace invitation accept

| Item | Value |
|---|---|
| Future-state capability | Accept invitation to join workspace |
| Current state | Workspace invitation accept exists |
| Phase 03 target | Verify/harden |
| Classification | `MUST_IMPLEMENT_IN_PHASE_03` |

Rules:

```text
1. Find invitation by hash(rawCode).
2. Invitation must be PENDING.
3. Invitation must not be expired.
4. Invitation must not be revoked.
5. Invitation must not exceed maxUses.
6. User must not already be active workspace member.
7. If user has no OrgMember in workspace organization, create OrgMember:
   - membershipType MEMBER
   - source WORKSPACE_INVITATION
   - status ACTIVE
8. If OrgMember exists but is not active, reject.
9. Create WorkspaceMember ACTIVE.
10. Increment usedCount.
11. If maxUses reached, status becomes ACCEPTED; otherwise remains PENDING.
12. Event WORKSPACE_INVITATION_ACCEPTED.
13. Optional email notification.
```

Tests:

```text
acceptInvitation_valid_createsWorkspaceMember
acceptInvitation_noOrgMember_createsOrgMember
acceptInvitation_inactiveOrgMember_rejected
acceptInvitation_expired_rejected
acceptInvitation_revoked_rejected
acceptInvitation_overMaxUses_rejected
acceptInvitation_alreadyMember_rejected
acceptInvitation_incrementsUsedCount
acceptInvitation_maxUsesReached_setsAccepted
```

---

## 8.7 WS-007 — Workspace invitation revoke

| Item | Value |
|---|---|
| Future-state capability | Revoke unused invitation |
| Current state | Workspace invitation revoke exists |
| Phase 03 target | Verify/harden |
| Classification | `MUST_IMPLEMENT_IN_PHASE_03` |

Rules:

```text
1. Actor must have WORKSPACE_MANAGE_INVITATION.
2. Invitation must exist.
3. Invitation must belong to workspace in path.
4. Only PENDING invitation can be revoked.
5. Status → REVOKED.
6. Event WORKSPACE_INVITATION_REVOKED.
```

---

## 8.8 WS-008 — Join request create

| Item | Value |
|---|---|
| Future-state capability | Request access to workspace when policy allows |
| Current state | Join request create exists |
| Phase 03 target | Verify/harden |
| Classification | `MUST_IMPLEMENT_IN_PHASE_03` |

Rules:

```text
1. Workspace can be found by id or code.
2. Workspace must be ACTIVE.
3. joinPolicy controls request:
   - INVITE_ONLY → reject
   - REQUEST_TO_JOIN → allow
   - INVITE_OR_REQUEST → allow
   - DISABLED → reject
4. User must not already be active workspace member.
5. Only one PENDING request per user/workspace.
6. Initial status PENDING.
7. Event WORKSPACE_JOIN_REQUEST_CREATED.
8. Optional notification/email to workspace admins.
```

Tests:

```text
createJoinRequest_valid_success
createJoinRequest_inviteOnly_rejected
createJoinRequest_disabled_rejected
createJoinRequest_alreadyMember_rejected
createJoinRequest_duplicatePending_conflict
```

---

## 8.9 WS-009 — Join request approve/reject/cancel

| Item | Value |
|---|---|
| Future-state capability | Review workspace join requests |
| Current state | Approve/reject/cancel exists |
| Phase 03 target | Verify/harden |
| Classification | `MUST_IMPLEMENT_IN_PHASE_03` |

Approve rules:

```text
1. Actor must have WORKSPACE_MANAGE_JOIN_REQUEST.
2. Request must be PENDING.
3. Workspace must be ACTIVE.
4. If user has no active OrgMember, create or reject according to product decision.
5. Create WorkspaceMember ACTIVE.
6. Status → APPROVED.
7. reviewerUserId/reviewedAt recorded.
8. Event WORKSPACE_JOIN_REQUEST_APPROVED.
```

Reject rules:

```text
1. Actor must have WORKSPACE_MANAGE_JOIN_REQUEST.
2. Request must be PENDING.
3. reviewNote stored.
4. Status → REJECTED.
5. Event WORKSPACE_JOIN_REQUEST_REJECTED.
```

Cancel rules:

```text
1. Only requester can cancel.
2. Request must be PENDING.
3. Status → CANCELLED.
4. Event WORKSPACE_JOIN_REQUEST_CANCELLED.
```

Tests:

```text
approveJoinRequest_valid_createsMember
approveJoinRequest_nonPending_rejected
approveJoinRequest_withoutPermission_forbidden
rejectJoinRequest_valid_success
cancelJoinRequest_byRequester_success
cancelJoinRequest_byOtherUser_forbidden
```

---

## 8.10 WS-010 — Workspace onboarding FSM

| Item | Value |
|---|---|
| Future-state capability | Guide new user into first workspace |
| Current state | Onboarding FSM exists |
| Phase 03 target | Verify/harden |
| Classification | `MUST_IMPLEMENT_IN_PHASE_03` |

Supported options now:

```text
CREATE_WORKSPACE
JOIN_WITH_INVITATION
```

Unsupported:

```text
REQUEST_TO_JOIN during onboarding
```

Required FSM:

```text
START
  → IN_PROGRESS / CHOOSE_WORKSPACE_OPTION
    → CREATE_WORKSPACE
      → create organization + workspace
      → set workspace context
      → COMPLETED
    → ENTER_INVITATION_CODE
      → accept invitation
      → set workspace context
      → COMPLETED
```

Rules:

```text
1. Start blocked if active onboarding already exists.
2. Choose option only from valid step.
3. REQUEST_TO_JOIN option rejected with 422 or defined error.
4. Create workspace path uses nested org+workspace create actions.
5. Invitation path uses invitation accept action.
6. Reset allowed from configured resettable states.
7. Reset cancels legacy pending join request if current implementation does so.
8. Completion sets current workspace context.
9. Failure stores failureReason.
```

Tests:

```text
onboardingStart_newUser_success
onboardingStart_existingActive_rejected
chooseCreateWorkspace_success
chooseRequestToJoin_rejected
createWorkspacePath_createsOrgWorkspaceAndContext
acceptInvitationPath_createsMembershipAndContext
resetChoice_fromValidState_success
resetChoice_cancelsLegacyPendingJoinRequest
```

---

## 8.11 WS-011 — Workspace context switcher

| Item | Value |
|---|---|
| Future-state capability | Current active workspace selection for user session |
| Current state | Switch Workspace Context exists |
| Phase 03 target | Verify/harden |
| Classification | `MUST_IMPLEMENT_IN_PHASE_03` |

Rules:

```text
1. Workspace must exist.
2. Workspace must be ACTIVE.
3. User must have effective access / active membership.
4. Create context if missing.
5. Set currentWorkspaceId.
6. Update lastSwitchedAt.
7. Returning current context must not reveal inaccessible workspace.
8. Available workspaces list only ACTIVE memberships.
```

Tests:

```text
switchWorkspace_valid_success
switchWorkspace_inactiveWorkspace_rejected
switchWorkspace_notMember_rejected
getAvailableWorkspaces_onlyActiveMemberships
```

---

## 8.12 WS-012 — Workspace team model

| Item | Value |
|---|---|
| Future-state capability | Workspace team view / local team membership |
| Current state | WorkspaceTeam CRUD + Members exists; org-scoped team model also exists |
| Phase 03 target | Clarify model and prevent duplication/confusion |
| Classification | `MUST_IMPLEMENT_IN_PHASE_03` as model clarification/hardening |

Current notes:

```text
Current code may contain WorkspaceTeam and OrgTeam.
As of V33+, teams are described as org-scoped with assignment to workspaces.
```

Phase 03 must force a clear model:

Option A — Org-scoped team is canonical:

```text
OrgTeam is source of truth.
OrgTeamWorkspaceAssignment decides workspace participation.
WorkspaceTeam endpoints are view/compatibility façade.
```

Option B — WorkspaceTeam remains separate:

```text
WorkspaceTeam is separate from OrgTeam.
OrgTeam is for cross-workspace organizational teams.
```

Preferred TO-BE:

```text
OrgTeam is canonical for reusable teams.
WorkspaceTeam should either be deprecated or kept only for local workspace-only teams with clear naming.
```

The agent must not keep ambiguous behavior.

Required completion output:

```text
Team model decision:
- canonical team entity
- compatibility endpoints
- deprecated endpoints if any
- migration risk
```

---

# 9. Team and People Directory TO-BE gap matrix

---

## 9.1 PPL-001 — Person profile / people directory

| Item | Value |
|---|---|
| Future-state capability | Central people directory with profile, department, title, timezone, skills |
| Current state | Not implemented as full People Directory; only IamUser + OrgMember/WorkspaceMember |
| Phase 03 target | Do not implement full HR profile unless required; add note |
| Classification | `DEFERRED_TO_PHASE_31_EMPLOYEE_PERFORMANCE` or People Directory phase |

Future entities:

```text
PersonProfile
PersonSkill
PersonDepartment
PersonLocation
EmploymentInfo
```

Why deferred:

```text
Current project capacity can use WorkspaceMember + IamUser first.
Skill/capability matching can come later.
```

However, Phase 12 Resource Capacity may need:

```text
timezone
working calendar
role/capacity profile
```

So Phase 12 must return to People/Workspace to link resource calendars to users.

---

## 9.2 PPL-002 — Department / reporting line

| Item | Value |
|---|---|
| Future-state capability | Org structure, manager, department, cost center |
| Current state | Not implemented |
| Phase 03 target | Defer |
| Classification | `DEFERRED_TO_PHASE_31` or `PHASE_17` if cost center required for finance |

Future entities:

```text
Department
Position
ReportingLine
CostCenter
```

Finance revisit:

```text
Phase 17 may need cost center for overhead allocation.
```

---

## 9.3 PPL-003 — Member role within org/workspace

| Item | Value |
|---|---|
| Future-state capability | Distinguish membership type from IAM role |
| Current state | OrgMember membershipType exists; workspace membership exists |
| Phase 03 target | Harden semantic separation |
| Classification | `MUST_IMPLEMENT_IN_PHASE_03` |

Rules:

```text
1. OrgMember.membershipType is not IAM permission truth.
2. WorkspaceMember status is not role assignment.
3. IAM grants/roles determine access.
4. OWNER membership bootstraps owner grants but does not replace IAM.
5. UI labels may show owner/member, but backend authorization uses IAM.
```

Tests:

```text
memberWithoutGrant_cannotManageWorkspace
ownerGetsOwnerGrant_canManage
deactivatedWorkspaceMember_losesWorkspaceAccess
```

---

## 9.4 PPL-004 — External collaborator

| Item | Value |
|---|---|
| Future-state capability | Invite client/vendor/contractor to limited portal/workspace/project |
| Current state | Not implemented |
| Phase 03 target | Defer |
| Classification | `DEFERRED_TO_PHASE_29_CUSTOMER_EXTERNAL_COLLABORATION` |

Future entities:

```text
ExternalParty
ExternalContact
ExternalWorkspaceAccess
ClientPortalUser
```

Future IAM requirements:

```text
subjectType EXTERNAL_USER or user classification
limited document/project/task access
portal-specific auth policy
```

Do not implement external collaborators in Phase 03 unless product explicitly requires.

---

# 10. Entities and table attributes TO-BE

This section defines Phase 03 entity expectations. If current schema differs, agent must map and document.

---

## 10.1 Organization — `workspace_organization`

Required fields:

```text
id UUID PK
code VARCHAR(100) NOT NULL UNIQUE
name VARCHAR(255) NOT NULL
description TEXT NULL
owner_user_id UUID NOT NULL
status VARCHAR(50) NOT NULL
version INT NOT NULL DEFAULT 0
created_at TIMESTAMP NOT NULL
created_by UUID/VARCHAR NULL
updated_at TIMESTAMP NOT NULL
updated_by UUID/VARCHAR NULL
archived_at TIMESTAMP NULL
archived_by UUID/VARCHAR NULL
```

Recommended future fields:

```text
default_locale VARCHAR(20) NULL
default_timezone VARCHAR(100) NULL
```

Deferred fields:

```text
billingAccountId — commercial/admin phase
securityPolicyId — Phase 23
retentionPolicyId — Phase 38
```

Constraints:

```text
uq_workspace_organization_code
ck_workspace_organization_status
```

---

## 10.2 Workspace — `workspace_workspace`

Required fields:

```text
id UUID PK
organization_id UUID NOT NULL
code VARCHAR(100) NOT NULL
name VARCHAR(255) NOT NULL
description TEXT NULL
owner_user_id UUID NOT NULL
default_visibility VARCHAR(50) NOT NULL DEFAULT 'PRIVATE'
join_policy VARCHAR(50) NOT NULL DEFAULT 'INVITE_ONLY'
status VARCHAR(50) NOT NULL
version INT NOT NULL DEFAULT 0
created_at / created_by
updated_at / updated_by
archived_at / archived_by
```

Constraints:

```text
unique organization_id + code
status enum ACTIVE / INACTIVE? / ARCHIVED
visibility enum PRIVATE / PUBLIC
joinPolicy enum INVITE_ONLY / REQUEST_TO_JOIN / INVITE_OR_REQUEST / DISABLED
```

Rules:

```text
organization_id immutable
code immutable
owner_user_id immutable unless owner transfer implemented
```

---

## 10.3 OrgMember — `org_member`

Required fields:

```text
id UUID PK
organization_id UUID NOT NULL
user_id UUID NOT NULL
membership_type VARCHAR(50) NOT NULL
source VARCHAR(50) NOT NULL
status VARCHAR(50) NOT NULL
joined_at TIMESTAMP NOT NULL
suspended_at TIMESTAMP NULL
removed_at TIMESTAMP NULL
created_at / created_by
updated_at / updated_by
version INT
```

Suggested enums:

```text
membership_type: OWNER, ADMIN, MEMBER, GUEST? future
source: SYSTEM_BOOTSTRAP, MANUAL, ORGANIZATION_INVITATION, WORKSPACE_INVITATION, IMPORT
status: ACTIVE, SUSPENDED, REMOVED
```

Constraints:

```text
unique organization_id + user_id
```

Rules:

```text
Only active org member can become workspace member.
Cannot remove only active OWNER.
```

---

## 10.4 WorkspaceMember — `workspace_member`

Required fields:

```text
id UUID PK
workspace_id UUID NOT NULL
user_id UUID NOT NULL
source VARCHAR(50) NOT NULL
status VARCHAR(50) NOT NULL
joined_at TIMESTAMP NOT NULL
deactivated_at TIMESTAMP NULL
created_at / created_by
updated_at / updated_by
version INT
```

Suggested source:

```text
SYSTEM_BOOTSTRAP
MANUAL
WORKSPACE_INVITATION
JOIN_REQUEST
ORG_TEAM_ASSIGNMENT future if auto-membership via team
IMPORT
```

Constraints:

```text
unique workspace_id + user_id
```

Rules:

```text
Active workspace member requires active org member.
Deactivation affects team membership and IAM access.
```

---

## 10.5 WorkspaceInvitation — `workspace_invitation`

Required fields:

```text
id UUID PK
workspace_id UUID NOT NULL
invited_email VARCHAR(255) NULL
invitation_code_hash VARCHAR(255) NOT NULL
invitation_code_hint VARCHAR(20) NOT NULL
max_uses INT NULL
used_count INT NOT NULL DEFAULT 0
expires_at TIMESTAMP NOT NULL
status VARCHAR(50) NOT NULL
created_by UUID NOT NULL
created_at TIMESTAMP NOT NULL
updated_at TIMESTAMP NOT NULL
revoked_at TIMESTAMP NULL
revoked_by UUID NULL
```

Status:

```text
PENDING
ACCEPTED
REVOKED
EXPIRED optional if job materializes expiration
```

Never store raw code.

---

## 10.6 WorkspaceJoinRequest — `workspace_join_request`

Required fields:

```text
id UUID PK
workspace_id UUID NOT NULL
requester_user_id UUID NOT NULL
message TEXT NULL
status VARCHAR(50) NOT NULL
review_note TEXT NULL
reviewer_user_id UUID NULL
reviewed_at TIMESTAMP NULL
created_at TIMESTAMP NOT NULL
updated_at TIMESTAMP NOT NULL
```

Constraints:

```text
partial unique workspace_id + requester_user_id WHERE status='PENDING'
```

Status:

```text
PENDING
APPROVED
REJECTED
CANCELLED
```

---

## 10.7 WorkspaceUserContext — `workspace_user_context`

Required fields:

```text
id UUID PK
user_id UUID NOT NULL UNIQUE
current_workspace_id UUID NULL
last_switched_at TIMESTAMP NULL
created_at TIMESTAMP NOT NULL
updated_at TIMESTAMP NOT NULL
```

Rules:

```text
current_workspace_id must be accessible by user at switch time.
If workspace later becomes inaccessible, context should be cleared or ignored.
```

---

## 10.8 WorkspaceOnboardingState — `workspace_onboarding_state`

Required fields:

```text
id UUID PK
user_id UUID NOT NULL UNIQUE
status VARCHAR(50) NOT NULL
current_step VARCHAR(50) NOT NULL
selected_option VARCHAR(50) NULL
target_workspace_id UUID NULL
created_organization_id UUID NULL
created_workspace_id UUID NULL
invitation_id UUID NULL
join_request_id UUID NULL
failure_reason TEXT NULL
created_at TIMESTAMP NOT NULL
updated_at TIMESTAMP NOT NULL
version INT
```

Status:

```text
IN_PROGRESS
COMPLETED
CANCELLED
FAILED
WAITING_FOR_APPROVAL optional legacy
```

Step:

```text
CHOOSE_WORKSPACE_OPTION
CREATE_WORKSPACE
ENTER_INVITATION_CODE
COMPLETED
```

Option:

```text
CREATE_WORKSPACE
JOIN_WITH_INVITATION
REQUEST_TO_JOIN rejected/unsupported
```

---

## 10.9 OrgTeam — `org_team`

Required fields:

```text
id UUID PK
organization_id UUID NOT NULL
code VARCHAR(100) NOT NULL
name VARCHAR(255) NOT NULL
description TEXT NULL
status VARCHAR(50) NOT NULL
created_at / created_by
updated_at / updated_by
archived_at / archived_by
version INT
```

Constraints:

```text
unique organization_id + code
```

Status:

```text
ACTIVE
ARCHIVED
```

---

## 10.10 OrgTeamMember — `org_team_member`

Required fields:

```text
id UUID PK
org_team_id UUID NOT NULL
user_id UUID NOT NULL
status VARCHAR(50) optional
created_at / created_by
```

Constraint:

```text
unique org_team_id + user_id
```

Rule:

```text
user must be active OrgMember of same organization.
```

---

## 10.11 OrgTeamWorkspaceAssignment — `org_team_workspace_assignment`

Required fields:

```text
id UUID PK
org_team_id UUID NOT NULL
workspace_id UUID NOT NULL
status VARCHAR(50) NOT NULL
assigned_at TIMESTAMP NOT NULL
assigned_by UUID NOT NULL
revoked_at TIMESTAMP NULL
revoked_by UUID NULL
created_at / updated_at
```

Constraint:

```text
unique active org_team_id + workspace_id
```

Rule:

```text
team.organizationId must equal workspace.organizationId.
```

---

## 10.12 WorkspaceTeam / WorkspaceTeamMember

If these exist, agent must decide model.

Option A preferred:

```text
WorkspaceTeam is compatibility/local view.
OrgTeam is canonical.
```

Completion file must document:

```text
Does WorkspaceTeam still exist?
Is it deprecated?
Does it map to OrgTeam?
What endpoints remain?
What migration risk exists?
```

Do not leave both as independent unclear team models.

---

# 11. API TO-BE list

All APIs use `/api`, not `/api/v1`.

## 11.1 Organization APIs

```text
POST  /api/organizations
GET   /api/organizations/{id}
GET   /api/organizations
PUT   /api/organizations/{id}
PATCH /api/organizations/{id}/activate
PATCH /api/organizations/{id}/archive
```

Optional/deferred:

```text
POST /api/organizations/{id}/owners/transfer
```

## 11.2 Org member APIs

```text
POST   /api/organizations/{organizationId}/members
GET    /api/organizations/{organizationId}/members
PATCH  /api/organizations/{organizationId}/members/{memberId}/activate
PATCH  /api/organizations/{organizationId}/members/{memberId}/suspend
DELETE /api/organizations/{organizationId}/members/{memberId}
```

## 11.3 Org invitation APIs

```text
POST   /api/organizations/{organizationId}/invitations
GET    /api/organizations/{organizationId}/invitations
POST   /api/organizations/invitations/{token}/accept
PATCH  /api/organizations/{organizationId}/invitations/{id}/cancel
```

## 11.4 Workspace APIs

```text
POST  /api/workspaces
GET   /api/workspaces/{id}
GET   /api/workspaces
PUT   /api/workspaces/{id}
PATCH /api/workspaces/{id}/activate
PATCH /api/workspaces/{id}/archive
```

## 11.5 Workspace member APIs

```text
POST  /api/workspaces/{workspaceId}/members
GET   /api/workspaces/{workspaceId}/members
PATCH /api/workspaces/{workspaceId}/members/{memberId}/activate
PATCH /api/workspaces/{workspaceId}/members/{memberId}/deactivate
```

Optional future:

```text
DELETE /api/workspaces/{workspaceId}/members/{memberId}
```

## 11.6 Workspace invitation APIs

```text
POST  /api/workspaces/{workspaceId}/invitations
GET   /api/workspaces/{workspaceId}/invitations
PATCH /api/workspaces/{workspaceId}/invitations/{id}/revoke
POST  /api/workspaces/invitations/{code}/accept
```

## 11.7 Join request APIs

```text
POST   /api/workspaces/{workspaceId}/join-requests
POST   /api/workspace-join-requests
GET    /api/workspaces/{workspaceId}/join-requests
PATCH  /api/workspaces/{workspaceId}/join-requests/{id}/approve
PATCH  /api/workspaces/{workspaceId}/join-requests/{id}/reject
DELETE /api/workspaces/{workspaceId}/join-requests/{id}
```

## 11.8 Workspace context APIs

```text
GET /api/workspace-context/current
GET /api/workspace-context/available
PUT /api/workspace-context/current
```

## 11.9 Onboarding APIs

```text
GET  /api/workspace-onboarding/status
POST /api/workspace-onboarding/start
POST /api/workspace-onboarding/choose-option
POST /api/workspace-onboarding/create-workspace
POST /api/workspace-onboarding/accept-invitation
POST /api/workspace-onboarding/reset-choice
```

## 11.10 Org team APIs

```text
POST   /api/organizations/{organizationId}/teams
GET    /api/organizations/{organizationId}/teams/{teamId}
GET    /api/organizations/{organizationId}/teams
PUT    /api/organizations/{organizationId}/teams/{teamId}
PATCH  /api/organizations/{organizationId}/teams/{teamId}/activate
PATCH  /api/organizations/{organizationId}/teams/{teamId}/archive
POST   /api/organizations/{organizationId}/teams/{teamId}/members
GET    /api/organizations/{organizationId}/teams/{teamId}/members
DELETE /api/organizations/{organizationId}/teams/{teamId}/members/{userId}
POST   /api/organizations/{organizationId}/teams/{teamId}/workspace-assignments
DELETE /api/organizations/{organizationId}/teams/{teamId}/workspace-assignments/{assignmentId}
```

If current endpoints differ, preserve current route only if product convention demands it and document mapping.

---

# 12. Authorization requirements

Phase 03 must use IAM authorities from Phase 02.

Required current authorities:

```text
ORGANIZATION_VIEW
ORGANIZATION_CREATE
ORGANIZATION_UPDATE
ORGANIZATION_ARCHIVE
ORGANIZATION_MANAGE_MEMBER
ORGANIZATION_INVITE_MEMBER
ORGANIZATION_MANAGE_TEAM
ORGANIZATION_CREATE_WORKSPACE

WORKSPACE_VIEW
WORKSPACE_CREATE
WORKSPACE_UPDATE
WORKSPACE_ARCHIVE
WORKSPACE_MANAGE_MEMBER
WORKSPACE_INVITE_MEMBER
WORKSPACE_MANAGE_INVITATION
WORKSPACE_MANAGE_JOIN_REQUEST
WORKSPACE_MANAGE_TEAM
WORKSPACE_SWITCH
```

Team authorities:

```text
TEAM_VIEW
TEAM_CREATE
TEAM_UPDATE
TEAM_ARCHIVE
TEAM_MANAGE_MEMBER
TEAM_ASSIGN_WORKSPACE
```

Rules:

```text
1. Creating organization may be allowed for authenticated users unless platform admin controls org creation.
2. Creating workspace requires ORGANIZATION_CREATE_WORKSPACE on org.
3. Managing org members requires org-scoped authority.
4. Managing workspace members requires workspace-scoped authority.
5. Creating invitations requires invite authority.
6. Approving join requests requires manage join request authority.
7. Team assignment requires team manage/assign authority.
8. Cross-org resource operations are denied even if user has authority in another org.
```

If current code allows authenticated user to create org without authority:

```text
Document product decision:
- open organization creation
or
- platform-controlled organization creation
```

---

# 13. Event registry TO-BE

Phase 03 must seed or verify event definitions.

Source system:

```text
SCOPERY_WORKSPACE
```

## 13.1 Organization events

```text
ORGANIZATION_CREATED
ORGANIZATION_UPDATED
ORGANIZATION_ACTIVATED
ORGANIZATION_ARCHIVED
ORGANIZATION_OWNER_ADDED
ORGANIZATION_OWNER_REMOVED
ORGANIZATION_MEMBER_ADDED
ORGANIZATION_MEMBER_ACTIVATED
ORGANIZATION_MEMBER_SUSPENDED
ORGANIZATION_MEMBER_REMOVED
ORG_INVITATION_CREATED
ORG_INVITATION_ACCEPTED
ORG_INVITATION_CANCELLED
ORG_TEAM_CREATED
ORG_TEAM_UPDATED
ORG_TEAM_ARCHIVED
ORG_TEAM_MEMBER_ADDED
ORG_TEAM_MEMBER_REMOVED
ORG_TEAM_ASSIGNED_TO_WORKSPACE
ORG_TEAM_REVOKED_FROM_WORKSPACE
```

## 13.2 Workspace events

```text
WORKSPACE_CREATED
WORKSPACE_UPDATED
WORKSPACE_ACTIVATED
WORKSPACE_ARCHIVED
WORKSPACE_MEMBER_ADDED
WORKSPACE_MEMBER_ACTIVATED
WORKSPACE_MEMBER_DEACTIVATED
WORKSPACE_INVITATION_CREATED
WORKSPACE_INVITATION_ACCEPTED
WORKSPACE_INVITATION_REVOKED
WORKSPACE_JOIN_REQUEST_CREATED
WORKSPACE_JOIN_REQUEST_APPROVED
WORKSPACE_JOIN_REQUEST_REJECTED
WORKSPACE_JOIN_REQUEST_CANCELLED
WORKSPACE_CONTEXT_SWITCHED
WORKSPACE_ONBOARDING_STARTED
WORKSPACE_ONBOARDING_COMPLETED
WORKSPACE_ONBOARDING_FAILED
```

## 13.3 Required event variables

Every event should include safe variables:

```text
actor.userId
organization.id
organization.code
workspace.id
workspace.code
member.userId
team.id
team.code
invitation.id
joinRequest.id
status.old
status.new
occurredAt
traceId
```

Do not include:

```text
raw invitation code in generic event payload
raw invitation token
email if not needed
private review notes in broad events
```

If email needs raw invitation code/link, pass it through secure email command context, not broad event payload unless existing notification system explicitly handles sensitive variables safely.

---

# 14. Notification / Email seeder TO-BE

Phase 03 must seed or verify email templates/rules for invitation and access workflows.

## 14.1 Required email templates

```text
ORG_INVITATION_CREATED_EMAIL
WORKSPACE_INVITATION_CREATED_EMAIL
WORKSPACE_JOIN_REQUEST_CREATED_ADMIN_EMAIL
WORKSPACE_JOIN_REQUEST_APPROVED_EMAIL
WORKSPACE_JOIN_REQUEST_REJECTED_EMAIL
WORKSPACE_MEMBER_ADDED_EMAIL optional
ORG_MEMBER_ADDED_EMAIL optional
```

## 14.2 Workspace invitation email

Code:

```text
WORKSPACE_INVITATION_CREATED_EMAIL
```

Event:

```text
WORKSPACE_INVITATION_CREATED
```

Required variables:

```text
recipient.email
inviter.fullName
workspace.name
workspace.code
organization.name
invitation.acceptUrl
invitation.expiresAt
support.email
```

Rules:

```text
1. Raw invitation code/link appears only in email to intended recipient.
2. Email must not expose other workspace members.
3. Expiration visible.
4. If invitedEmail is null, no email is sent.
```

## 14.3 Org invitation email

Code:

```text
ORG_INVITATION_CREATED_EMAIL
```

Event:

```text
ORG_INVITATION_CREATED
```

Variables:

```text
recipient.email
inviter.fullName
organization.name
invitation.acceptUrl
invitation.expiresAt
support.email
```

## 14.4 Join request admin email

Code:

```text
WORKSPACE_JOIN_REQUEST_CREATED_ADMIN_EMAIL
```

Event:

```text
WORKSPACE_JOIN_REQUEST_CREATED
```

Variables:

```text
requester.fullName
requester.email
workspace.name
workspace.code
joinRequest.message
review.url
```

Rules:

```text
Only workspace admins/managers with permission should receive admin notification.
```

## 14.5 Seeder rules

```text
1. Email templates must be idempotent.
2. Template variables must exist in EventDefinition variables.
3. Publish version only if valid.
4. Do not duplicate rule if existing.
5. Do not send email by default in tests except outbox/enqueue tests.
```

---

# 15. Outbox / audit / activity requirements

Phase 03 must use existing platform patterns.

Required activity actions:

```text
CREATE_ORGANIZATION
UPDATE_ORGANIZATION
ACTIVATE_ORGANIZATION
ARCHIVE_ORGANIZATION
ADD_ORG_MEMBER
ACTIVATE_ORG_MEMBER
SUSPEND_ORG_MEMBER
REMOVE_ORG_MEMBER
CREATE_ORG_INVITATION
ACCEPT_ORG_INVITATION
CANCEL_ORG_INVITATION
CREATE_WORKSPACE
UPDATE_WORKSPACE
ACTIVATE_WORKSPACE
ARCHIVE_WORKSPACE
ADD_WORKSPACE_MEMBER
ACTIVATE_WORKSPACE_MEMBER
DEACTIVATE_WORKSPACE_MEMBER
CREATE_WORKSPACE_INVITATION
ACCEPT_WORKSPACE_INVITATION
REVOKE_WORKSPACE_INVITATION
CREATE_JOIN_REQUEST
APPROVE_JOIN_REQUEST
REJECT_JOIN_REQUEST
CANCEL_JOIN_REQUEST
START_ONBOARDING
COMPLETE_ONBOARDING
SWITCH_WORKSPACE_CONTEXT
CREATE_ORG_TEAM
UPDATE_ORG_TEAM
ARCHIVE_ORG_TEAM
ADD_ORG_TEAM_MEMBER
REMOVE_ORG_TEAM_MEMBER
ASSIGN_ORG_TEAM_TO_WORKSPACE
REVOKE_ORG_TEAM_FROM_WORKSPACE
```

Rules:

```text
1. Business event/outbox must be created in same transaction as business change where existing convention supports it.
2. Audit logs must not store raw invitation code/token.
3. Review notes may be sensitive; log only metadata unless product allows.
4. Activity log failure should follow existing convention.
```

---

# 16. Cross-module implications and future phase return list

This is mandatory so agents do not forget dependencies.

## 16.1 Phase 09/10 Project returns to Workspace

Project must rely on:

```text
active workspace
workspace membership
workspace-scoped IAM authority
```

Future addition:

```text
Private project membership may require ProjectMember and project-specific IAM resource.
```

Deferred:

```text
Project-level membership — DEFERRED_TO_PHASE_11/19 if needed.
```

## 16.2 Phase 12 Capacity returns to Workspace/People

Capacity needs:

```text
WorkspaceMember as resource identity.
Potential user timezone.
Potential employment status.
Working calendar linked to workspace member.
```

If People profile absent:

```text
Phase 12 must create ResourceCalendar with userId/workspaceId without full HR profile.
```

## 16.3 Phase 15/17 Finance returns to Org/Workspace

Finance may need:

```text
organization default currency
workspace default currency
cost center
billing region
tax policy
```

These are deferred now:

```text
DEFERRED_TO_PHASE_15/17
```

## 16.4 Phase 18 Quote returns to Org/Workspace

Quote needs:

```text
client organization or external party
commercial approvers
workspace quote permissions
```

External party is deferred:

```text
DEFERRED_TO_PHASE_29 or Quote phase minimal client entity.
```

## 16.5 Phase 20 Notifications returns to Workspace

Notification subscriptions need:

```text
workspace members
teams
notification preferences
admin recipients
```

Phase 20 must respect:

```text
Do not notify users who no longer have workspace access.
```

## 16.6 Phase 21 AI returns to Workspace

AI context must respect:

```text
current workspace
effective access
workspace AI feature policy
workspace/team permissions
```

Workspace AI policy deferred:

```text
DEFERRED_TO_PHASE_21/39
```

## 16.7 Phase 22 Reporting returns to Workspace

Reports must filter by:

```text
organization access
workspace access
team access
finance permission
```

## 16.8 Phase 29 External Collaboration

Must add:

```text
ExternalParty
ClientContact
PortalAccess
ExternalWorkspaceInvitation
ExternalProjectAccess
```

Do not mix external users with internal OrgMember unless product explicitly defines a unified user model.

---

# 17. Business rules master for Phase 03

## 17.1 Organization rules

```text
ORG-001 Organization code globally unique.
ORG-002 Organization code immutable.
ORG-003 Organization owner immutable unless owner transfer implemented.
ORG-004 Organization starts ACTIVE.
ORG-005 Archived organization cannot be updated.
ORG-006 Archived organization cannot create workspace.
ORG-007 Organization owner becomes active OrgMember.
ORG-008 Organization IAM resource must be bootstrapped.
ORG-009 Organization owner grant must be bootstrapped.
ORG-010 Organization must always have at least one active OWNER.
```

## 17.2 Org member rules

```text
ORGM-001 Org member unique by organizationId + userId.
ORGM-002 Add member requires ACTIVE organization.
ORGM-003 Removed member cannot be activated.
ORGM-004 Cannot suspend/remove only active OWNER.
ORGM-005 Suspend/remove revokes descendant access.
ORGM-006 Manual add source = MANUAL.
ORGM-007 Invitation accept source = ORGANIZATION_INVITATION.
```

## 17.3 Workspace rules

```text
WS-001 Workspace belongs to one organization.
WS-002 Organization must be ACTIVE to create workspace.
WS-003 Workspace code unique within organization.
WS-004 Workspace code immutable.
WS-005 Workspace owner immutable unless owner transfer implemented.
WS-006 Default visibility PRIVATE.
WS-007 Default join policy INVITE_ONLY.
WS-008 Workspace owner becomes active WorkspaceMember.
WS-009 Workspace IAM resource must be bootstrapped.
WS-010 Archived workspace blocks new operational changes.
```

## 17.4 Workspace member rules

```text
WSM-001 Workspace member unique by workspaceId + userId.
WSM-002 User must be active OrgMember of workspace organization.
WSM-003 Add member requires ACTIVE workspace.
WSM-004 Activate blocked if workspace inactive/archived.
WSM-005 Deactivate removes effective workspace-team access.
WSM-006 memberId must belong to path workspaceId.
```

## 17.5 Invitation rules

```text
INV-001 Raw code generated securely.
INV-002 Raw code never stored.
INV-003 Hash used for lookup.
INV-004 Hint safe to display.
INV-005 Raw code returned once only.
INV-006 Expired invitation cannot be accepted.
INV-007 Revoked invitation cannot be accepted.
INV-008 usedCount increments on accept.
INV-009 maxUses reached transitions to ACCEPTED.
INV-010 Already member cannot accept.
```

## 17.6 Join request rules

```text
JR-001 Workspace ACTIVE required.
JR-002 joinPolicy controls create.
JR-003 Only one PENDING request per user/workspace.
JR-004 Approve only PENDING.
JR-005 Reject only PENDING.
JR-006 Cancel only by requester and only PENDING.
JR-007 Approve creates WorkspaceMember.
```

## 17.7 Team rules

```text
TEAM-001 OrgTeam code unique within organization.
TEAM-002 Team organization must match workspace organization when assigned.
TEAM-003 Team member must be active OrgMember.
TEAM-004 Archived team cannot be updated.
TEAM-005 Removed team member loses effective team grant.
TEAM-006 Duplicate active workspace assignment blocked.
```

## 17.8 Onboarding rules

```text
ONB-001 Start creates IN_PROGRESS state.
ONB-002 CREATE_WORKSPACE and JOIN_WITH_INVITATION are supported.
ONB-003 REQUEST_TO_JOIN is not supported in onboarding.
ONB-004 Create workspace path completes onboarding and sets current workspace.
ONB-005 Accept invitation path completes onboarding and sets current workspace.
ONB-006 Reset allowed only from configured states.
```

## 17.9 Context rules

```text
CTX-001 Current workspace must be active.
CTX-002 User must be active workspace member.
CTX-003 Available list includes only accessible active workspaces.
CTX-004 Context does not grant access by itself.
```

---

# 18. Error catalog requirements

Exact names follow existing code convention, but concepts must exist.

Organization:

```text
ORGANIZATION_NOT_FOUND
ORGANIZATION_CODE_ALREADY_EXISTS
ORGANIZATION_NOT_ACTIVE
ORGANIZATION_ARCHIVED
ORGANIZATION_CANNOT_REMOVE_ONLY_OWNER
ORGANIZATION_MEMBER_NOT_FOUND
ORGANIZATION_MEMBER_ALREADY_EXISTS
ORGANIZATION_MEMBER_NOT_ACTIVE
ORG_INVITATION_NOT_FOUND
ORG_INVITATION_EXPIRED
ORG_INVITATION_CANCELLED
ORG_INVITATION_ALREADY_USED
```

Workspace:

```text
WORKSPACE_NOT_FOUND
WORKSPACE_CODE_ALREADY_EXISTS
WORKSPACE_NOT_ACTIVE
WORKSPACE_ARCHIVED
WORKSPACE_CONTEXT_NOT_MEMBER
WORKSPACE_MEMBER_NOT_FOUND
WORKSPACE_MEMBER_ALREADY_EXISTS
WORKSPACE_MEMBER_NOT_ORG_MEMBER
WORKSPACE_INVITATION_NOT_FOUND
WORKSPACE_INVITATION_EXPIRED
WORKSPACE_INVITATION_REVOKED
WORKSPACE_INVITATION_OVER_MAX_USES
WORKSPACE_ALREADY_MEMBER
WORKSPACE_JOIN_POLICY_BLOCKS_REQUEST
WORKSPACE_JOIN_REQUEST_NOT_FOUND
WORKSPACE_JOIN_REQUEST_ALREADY_PENDING
WORKSPACE_JOIN_REQUEST_NOT_PENDING
WORKSPACE_ONBOARDING_NOT_FOUND
WORKSPACE_ONBOARDING_OPTION_NOT_SUPPORTED
WORKSPACE_ONBOARDING_INVALID_STEP
```

Team:

```text
ORG_TEAM_NOT_FOUND
ORG_TEAM_CODE_ALREADY_EXISTS
ORG_TEAM_ARCHIVED
ORG_TEAM_MEMBER_ALREADY_EXISTS
ORG_TEAM_MEMBER_NOT_FOUND
ORG_TEAM_CROSS_ORGANIZATION_ASSIGNMENT
ORG_TEAM_WORKSPACE_ASSIGNMENT_ALREADY_EXISTS
ORG_TEAM_WORKSPACE_ASSIGNMENT_NOT_FOUND
```

IAM integration:

```text
WORKSPACE_IAM_BOOTSTRAP_FAILED
WORKSPACE_ACCESS_DENIED
```

---

# 19. Tests required

Phase 03 is incomplete without tests.

## 19.1 Organization action tests

```text
createOrganization_valid_success
createOrganization_duplicateCode_conflict
createOrganization_invalidCode_rejected
createOrganization_createsOwnerOrgMember
createOrganization_bootstrapsIamResource
createOrganization_bootstrapsOwnerGrant
createOrganization_emitsOutboxEvent
updateOrganization_archived_rejected
archiveOrganization_valid_success
archiveOrganization_blocksWorkspaceCreate
removeOnlyOwner_rejected
suspendOnlyOwner_rejected
```

## 19.2 Workspace action tests

```text
createWorkspace_valid_success
createWorkspace_inactiveOrg_rejected
createWorkspace_duplicateCodeSameOrg_conflict
createWorkspace_duplicateCodeDifferentOrg_allowed
createWorkspace_defaultsApplied
createWorkspace_createsOwnerWorkspaceMember
createWorkspace_bootstrapsIamResource
createWorkspace_bootstrapsOwnerGrant
updateWorkspace_archived_rejected
archiveWorkspace_blocksNewMembers
archiveWorkspace_blocksInvitations
archiveWorkspace_blocksJoinRequests
```

## 19.3 Workspace member tests

```text
addWorkspaceMember_valid_success
addWorkspaceMember_userNotOrgMember_rejected
addWorkspaceMember_inactiveOrgMember_rejected
addWorkspaceMember_duplicate_conflict
activateWorkspaceMember_inactiveWorkspace_rejected
deactivateWorkspaceMember_success
deactivateWorkspaceMember_revokesTeamAccess
workspaceMemberIdor_otherWorkspaceMember_rejected
```

## 19.4 Workspace invitation tests

```text
createInvitation_valid_returnsRawCodeOnce
createInvitation_storesHashOnly
listInvitation_doesNotExposeRawCode
acceptInvitation_valid_createsMember
acceptInvitation_createsOrgMemberIfMissing
acceptInvitation_inactiveOrgMember_rejected
acceptInvitation_expired_rejected
acceptInvitation_revoked_rejected
acceptInvitation_overMaxUses_rejected
acceptInvitation_alreadyMember_rejected
revokeInvitation_pending_success
revokeInvitation_nonPending_rejected
```

## 19.5 Join request tests

```text
createJoinRequest_requestToJoinPolicy_success
createJoinRequest_inviteOrRequestPolicy_success
createJoinRequest_inviteOnly_rejected
createJoinRequest_disabled_rejected
createJoinRequest_duplicatePending_conflict
approveJoinRequest_valid_createsMember
approveJoinRequest_nonPending_rejected
rejectJoinRequest_valid_success
cancelJoinRequest_byRequester_success
cancelJoinRequest_byOtherUser_forbidden
```

## 19.6 Onboarding tests

```text
startOnboarding_newUser_success
chooseOptionCreate_success
chooseOptionJoinInvitation_success
chooseOptionRequestToJoin_rejected
createWorkspacePath_completesAndSetsContext
acceptInvitationPath_completesAndSetsContext
resetChoice_valid_success
resetChoice_invalidState_rejected
```

## 19.7 Context tests

```text
switchWorkspace_valid_success
switchWorkspace_notMember_rejected
switchWorkspace_inactiveWorkspace_rejected
availableWorkspaces_onlyActiveMemberships
currentWorkspace_inaccessible_returnsNullOrErrorAccordingToSpec
```

## 19.8 Org team tests

```text
createOrgTeam_valid_success
createOrgTeam_duplicateCode_conflict
createOrgTeam_bootstrapsIamResource
addOrgTeamMember_valid_success
addOrgTeamMember_notOrgMember_rejected
assignTeamToWorkspace_valid_success
assignTeamToWorkspace_crossOrg_rejected
assignTeamToWorkspace_duplicateActive_conflict
revokeTeamWorkspaceAssignment_success
archiveOrgTeam_blocksAssignment
removeOrgTeamMember_removesEffectiveAccess
```

## 19.9 Seeder/event/email tests

```text
workspaceEventDefinitions_seededIdempotently
workspaceEventVariables_valid
workspaceEmailTemplates_seededIdempotently
workspaceInvitationEmailTemplate_published
orgInvitationEmailTemplate_published
joinRequestEmailTemplates_published
seeders_secondRun_noDuplicates
```

---

# 20. Manual verification checklist

Completion file must include:

```text
1. Create organization.
2. Verify owner OrgMember created.
3. Verify IAM org resource created.
4. Create workspace under organization.
5. Verify owner WorkspaceMember created.
6. Verify IAM workspace resource created.
7. Add org member.
8. Add workspace member.
9. Create workspace invitation.
10. Verify raw code returned once only.
11. Accept workspace invitation.
12. Create join request under REQUEST_TO_JOIN workspace.
13. Approve join request.
14. Reject/cancel join request.
15. Start onboarding.
16. Complete onboarding by create workspace.
17. Complete onboarding by invitation.
18. Switch current workspace.
19. Create org team.
20. Add org team member.
21. Assign org team to workspace.
22. Verify cross-org assignment blocked.
23. Suspend org member and verify descendant access revoked.
24. Rerun seeders and confirm idempotency.
```

---

# 21. Acceptance criteria

Phase 03 is accepted only if:

```text
1. Current Workspace/Org features are classified against TO-BE.
2. Organization lifecycle implemented/hardened and tested.
3. Org member lifecycle implemented/hardened and tested.
4. Workspace lifecycle implemented/hardened and tested.
5. Workspace member lifecycle implemented/hardened and tested.
6. Invitation flows store hashes only and raw code is returned once.
7. Join request rules implemented and tested.
8. Onboarding FSM implemented and tested.
9. Workspace context switch implemented and tested.
10. Org team and workspace assignment rules implemented and tested.
11. Cross-org isolation tested.
12. IAM bootstrap for Org/Workspace/Team verified.
13. Owner grant bootstrap verified.
14. Membership cascade/revoke behavior verified.
15. Event registry seeds added or confirmed.
16. Email templates/rules added or confirmed.
17. Deferred future modules listed with target phase.
18. No /api/v1 or /api/v2 routes.
19. mvn compile passes.
20. mvn test passes.
21. Phase complete file exists and is honest.
```

---

# 22. Required phase completion file

Agent must create:

```text
docs/phase-complete/PHASE_03_WORKSPACE_ORG_MEMBER_TEAM_TO_BE_COMPLETE.md
```

Required sections:

```text
# Phase 03 — Workspace / Organization / Member / Team TO-BE Complete

## 1. Summary
## 2. Source Inputs Reviewed
## 3. Current vs TO-BE Classification Matrix
## 4. Implemented in Current BE
## 5. Implemented / Hardened in This Phase
## 6. Seed-only Items Added
## 7. Deferred Items and Target Phase
## 8. Team Model Decision
## 9. Entities Created / Modified
## 10. APIs Created / Modified
## 11. Business Rules Implemented
## 12. Business Rules Deferred
## 13. IAM Resource Bootstrap Matrix
## 14. Permission/Action Matrix
## 15. Event Registry Seeder Matrix
## 16. Email Template Seeder Matrix
## 17. Outbox / Audit / Activity Notes
## 18. Tests Added
## 19. Commands Run
## 20. Test Results
## 21. Manual Verification
## 22. Assumptions
## 23. Deviations From Prompt
## 24. Risks
## 25. Future Phases That Must Return to Workspace/IAM
```

Agent must not mark complete if:

```text
mvn test fails
raw invitation code is stored
raw invitation code appears in list/get response
cross-org team assignment is allowed
inactive org can create workspace
workspace member can be added without active org membership
only owner can be removed/suspended
IAM bootstrap silently fails
seeders duplicate records
future gaps are not documented
```

---

# 23. Agent anti-bịa rules

The agent must not:

```text
1. Treat current workspace inventory as full future-state.
2. Claim People Directory exists if only IamUser/Member exists.
3. Claim External Collaboration exists if no external party model exists.
4. Claim workspace team model is clear without documenting OrgTeam vs WorkspaceTeam decision.
5. Store raw invitation codes.
6. Expose raw invitation codes in list/detail APIs.
7. Allow cross-org team assignment.
8. Allow workspace member without active org membership.
9. Remove/suspend the only active org owner.
10. Create project/finance/document/client portal features in this phase.
11. Seed email templates without matching event variables.
12. Claim event emission works if only event definitions are seeded.
13. Forget to list future phases that must return to Workspace/IAM.
```

---

# 24. Prompt to give coding agent

```text
You are implementing Phase 03 — TO-BE Organization, Workspace, Membership, Team & Tenant Collaboration Core.

This is not an as-is documentation task.

Before coding, read:
- CLAUDE.md / CLAUDE.ms
- Coding_convention.md
- Phase 00 master roadmap
- Phase 01 API/security baseline
- Phase 02 TO-BE IAM core
- Current BE feature/entity/business-rule inventory
- Dynamic Work OS future-state feature catalog
- Existing workspace code, migrations, seeders, tests
- Existing IAM integration services
- Existing event registry and notification seed patterns

Your task:
1. Compare current workspace/org/team implementation against this TO-BE Phase 03 spec.
2. Classify every workspace/org/team/people capability as:
   CURRENTLY_IMPLEMENTED,
   MUST_IMPLEMENT_IN_PHASE_03,
   SEED_ONLY_IN_PHASE_03,
   DEFERRED_TO_PHASE_XX,
   or NOT_IN_SCOPE_FOR_BACKEND_NOW.
3. Implement only Phase 03 required items.
4. Do not implement deferred features such as full People Directory, External Client Portal, HR/performance, org billing, MFA/SSO, dynamic schema, or project finance.
5. Harden organization, workspace, member, invitation, join request, onboarding, context, team, and workspace assignment rules.
6. Ensure Org/Workspace/Team IAM resources and owner grants are bootstrapped and tested.
7. Add or verify event definitions for workspace/org lifecycle.
8. Add or verify email templates for org/workspace invitations and join request notifications.
9. Add all tests listed in the Phase 03 spec.
10. Run mvn compile and mvn test.
11. Create docs/phase-complete/PHASE_03_WORKSPACE_ORG_MEMBER_TEAM_TO_BE_COMPLETE.md with the full gap matrix.

Do not write "complete" if deferred gaps are not documented.
Do not claim future-state People Directory or External Collaboration exists just because OrgMember/WorkspaceMember exists.
```

---

# 25. Quick tracking matrix

| Capability group | Current backend | Phase 03 action | Later phase |
|---|---|---|---|
| Organization create/update/archive | Present | Harden/test | — |
| Organization owner guard | Partial/present | Harden/test | Owner transfer later |
| Organization settings/security | Missing | Defer | Phase 23/39 |
| Org member lifecycle | Present | Harden/test | — |
| Org invitation | Present | Harden/test/email | — |
| Workspace create/update/archive | Present | Harden/test | — |
| Workspace member lifecycle | Present | Harden/test | — |
| Workspace invitation | Present | Harden hash/raw-code tests | — |
| Join request | Present | Harden/test | — |
| Onboarding FSM | Present | Harden/test | — |
| Workspace context | Present | Harden/test | — |
| Org team | Present | Harden/test | — |
| WorkspaceTeam vs OrgTeam clarity | Ambiguous risk | Must document/decide | — |
| Team workspace assignment | Present | Harden/test | — |
| People directory | Missing | Defer | Phase 31 / People phase |
| External collaborators | Missing | Defer | Phase 29 |
| Client portal | Missing | Defer | Phase 29 |
| Org billing | Missing | Defer | Commercial/Admin phase |
| Org security policy | Missing | Defer | Phase 23 |
| Retention/privacy | Missing | Defer | Phase 38 |
| Capacity people profile | Missing | Defer minimal link | Phase 12 |
| Finance cost center | Missing | Defer | Phase 17 |
| Reporting access | Missing | Defer | Phase 22 |

---

# 26. Final principle

Phase 03 is not complete when "workspace CRUD works."

Phase 03 is complete when Scopery has a safe tenant boundary:

```text
No workspace without active organization.
No workspace member without active org membership.
No cross-org team assignment.
No owner removal that locks the org.
No raw invitation code leakage.
No ambiguous team model.
No silent IAM bootstrap failure.
No future-state gaps hidden as implemented.
```

Every future module must respect this boundary.
