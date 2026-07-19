# Phase 03 — Workspace / Organization / Member / Team TO-BE Complete

## 1. Summary

Phase 03 hardens the tenant collaboration foundation: org invitation tokens are hash-only (parity with workspace invites), archive/activate syncs IAM AuthResource lifecycle, workspace member deactivate cascades grants/teams/role assignments, OrgTeam is declared source of truth (legacy WorkspaceTeam deprecated), and workspace/org event + email seeders are expanded. Deferred commercial/security/people/portal items are documented with target phases.

## 2. Source Inputs Reviewed

- `PHASE_00_MASTER_ROADMAP_MODULAR_REVISED.md`
- `PHASE_01` / `PHASE_02` specs + completion files
- `PHASE_03_WORKSPACE_ORG_MEMBER_TEAM_TO_BE_DETAILED.md`
- Existing `modules/workspace/**`, IAM bootstrap/lifecycle, notification email seeders, migrations V13–V40

## 3. Current vs TO-BE Classification Matrix

| Capability | Classification | Notes |
|---|---|---|
| Organization CRUD + archive/activate | `CURRENTLY_IMPLEMENTED` + hardened | IAM lifecycle wired |
| OrgMember add/activate/suspend/remove + cascade | `CURRENTLY_IMPLEMENTED` | OrgMembershipAccessRevocationService |
| Org invitation create/accept/cancel | Hardened | **Hash-only token** (V41) |
| Workspace CRUD + members + invite/join/onboarding/context | `CURRENTLY_IMPLEMENTED` | WS deactivate cascade added |
| OrgTeam + workspace assignment | `CURRENTLY_IMPLEMENTED` | Cross-org blocked + tested |
| Legacy WorkspaceTeam APIs | Keep + deprecate | See §8 |
| Workspace/org event catalog | Seeded | `SCOPERY_WORKSPACE` |
| Email templates (org invite, join admin/reject) | Seeded | Admin join rule disabled pending recipient strategy |
| Org billing / client portal / HR / MFA org policy | Deferred | §7 |
| Formal owner transfer / restore archived | Deferred | Phase 23/32 |

## 4. Implemented in Current BE (pre–Phase 03)

Org/workspace CRUD, members, workspace invite (hashed), join requests, onboarding FSM, context switch, OrgTeam, IAM bootstrap on create, org-member cascade revoke.

## 5. Implemented / Hardened in This Phase

- Org invitation: `token_hash` + `token_hint`; raw token returned once on create; accept by hash; exception does not echo token
- Archive/activate Organization & Workspace → `IamAuthResourceLifecycleService`
- Archive OrgTeam → deactivate TEAM AuthResource
- `WorkspaceMembershipAccessRevocationService` on workspace member deactivate
- `WorkspaceEventDefinitionSeedInitializer` (§13 codes)
- Email: `ORG_INVITATION_CREATED_EMAIL`, `WORKSPACE_INVITATION_CREATED_EMAIL`, `WORKSPACE_JOIN_REQUEST_CREATED_ADMIN_EMAIL` (rule disabled), `WORKSPACE_JOIN_REQUEST_REJECTED_EMAIL`
- Legacy `TeamController` marked `@Deprecated`

## 6. Seed-only Items Added

Event definitions for all §13.1 / §13.2 codes under `SCOPERY_WORKSPACE` (idempotent find-or-create). Variable upsert for org invitation + join request created/rejected.

## 7. Deferred Items and Target Phase

| Item | Target |
|---|---|
| Org billing / subscription | Commercial / platform |
| External party / client portal | Phase 29 |
| Employee / People Directory | Phase 31 |
| Org MFA/SSO security policy | Phase 23 |
| Access review / retention | Phase 38 |
| Dynamic workspace config | Phase 39 |
| Formal owner transfer workflow | Phase 23 / 32 |
| Restore from ARCHIVED | Phase 23 |
| `WORKSPACE_USERS_WITH_RIGHT` admin email recipient | Notification hardening |
| Full outbox emit for every workspace event | Incremental with actions |

## 8. Team Model Decision

**Source of truth: OrgTeam** (`modules/workspace/orgteam`) — org-scoped teams assignable to workspaces.

**Legacy WorkspaceTeam** (`modules/workspace/team`) remains for backward compatibility and cascade cleanup on member deactivate, but `TeamController` is `@Deprecated`. New product features must use OrgTeam APIs. Full migration/removal deferred.

## 9. Entities Created / Modified

- **Migration:** `V41__alter_org_invitation_hash_token.sql`
- **Modified:** `OrgInvitation` (+ JPA/mapper/repo/response/create/accept), archive/activate org/workspace, `ArchiveOrgTeamAction`, `DeactivateWorkspaceMemberAction`
- **Created:** `WorkspaceMembershipAccessRevocationService`, `WorkspaceEventDefinitionSeedInitializer`

## 10. APIs Created / Modified

- Org invitation create response now includes one-time `token` (+ `tokenHint`); list/get omit raw token
- Accept path unchanged (`/{token}/accept`) but lookup is by hash
- No new path prefixes; legacy teams still mounted under existing paths (deprecated)

## 11. Business Rules Implemented

- Invitation tokens never stored raw (org + workspace)
- Archive syncs AuthResource INACTIVE; activate restores ACTIVE (org/workspace)
- Workspace member deactivate revokes USER grants on workspace-scoped resources, WS team memberships, workspace role assignments
- Cross-org OrgTeam→Workspace assignment rejected

## 12. Business Rules Deferred

- Only-ACTIVE-owner suspend/remove nuance (current: any OWNER blocked)
- Admin join-request email via right-based recipients
- Auto-cancel expired org invitations job event

## 13. IAM Resource Bootstrap Matrix

| Resource | Bootstrap on create | Lifecycle on archive |
|---|---|---|
| ORGANIZATION | Yes | Deactivate AuthResource |
| WORKSPACE | Yes | Deactivate AuthResource |
| TEAM (OrgTeam) | Yes | Deactivate AuthResource |
| Legacy WorkspaceTeam | Existing bootstrap | Unchanged |

## 14. Permission/Action Matrix

Existing IAM authorities (`ORGANIZATION_*`, `WORKSPACE_*`, `TEAM_*`) unchanged this phase. Future module permissions deferred with their phases.

## 15. Event Registry Seeder Matrix

| Source | Codes | Status |
|---|---|---|
| `SCOPERY_WORKSPACE` | Org/member/invite/team/workspace/join/onboarding/context (§13) | Seeded |
| Legacy `SCOPERY` invitation/join vars | Pre-existing | Kept (findByCode shared) |

## 16. Email Template Seeder Matrix

| Template | Event | Rule |
|---|---|---|
| `WORKSPACE_INVITATION_EMAIL` | `WORKSPACE_INVITATION_CREATED` | Enabled (legacy) |
| `WORKSPACE_INVITATION_CREATED_EMAIL` | same | Enabled (Phase 03 canonical) |
| `WORKSPACE_JOIN_REQUEST_APPROVED_EMAIL` | approved | Enabled |
| `WORKSPACE_JOIN_REQUEST_REJECTED_EMAIL` | rejected | Enabled |
| `WORKSPACE_JOIN_REQUEST_CREATED_ADMIN_EMAIL` | created | **Disabled** until admin recipient strategy |
| `ORG_INVITATION_CREATED_EMAIL` | `ORG_INVITATION_CREATED` | Enabled (`INVITEE_EMAIL`) |

## 17. Outbox / Audit / Activity Notes

Create org/workspace/org-team still enqueue transactional outbox. Archive/member/invite paths rely on activity log (+ IAM audit on grant revoke cascade). Broader outbox coverage deferred.

## 18. Tests Added

- `OrgInvitationActionTest` — hash store + accept-by-hash
- `ArchiveWorkspaceActionTest` — IAM deactivate on archive
- `AssignOrgTeamToWorkspaceActionTest` — cross-org rejected
- `WorkspaceEventDefinitionSeedInitializerTest` — idempotent seed + source system
- Organization activate/archive tests updated for lifecycle dependency

## 19. Commands Run

```bash
./mvnw test-compile
./mvnw test
```

## 20. Test Results

`./mvnw test` → **BUILD SUCCESS** — Tests run: **635**, Failures: 0, Errors: 0, Skipped: 1.

## 21. Manual Verification

Not yet verified live SMTP for new org-invite / join-rejected templates. Pending org invites are cancelled by V41 (must re-invite).

## 22. Assumptions

- Frontend org accept URL continues to use raw token in path; server hashes for lookup
- `token_hint` is safe to expose
- OrgTeam AuthResource type remains `TEAM`

## 23. Deviations From Prompt

1. Admin join-request email template seeded but rule **disabled** (`WORKSPACE_USERS_WITH_RIGHT` unsupported)
2. Dual team model: documented deprecate, not hard-deleted
3. Completion under `src/docs/phase-complete/` (repo convention)
4. V41 cancels pending plaintext invites rather than SQL-hashing (no pgcrypto dependency)

## 24. Risks

- Clients expecting org invite `token` column/plaintext break until they use create response once
- Disabled admin join email means ops rely on UI polling until recipient strategy lands
- Legacy WorkspaceTeam still writable — product must steer to OrgTeam

## 25. Future Phases That Must Return to Workspace/IAM

| Phase | Why |
|---|---|
| 11 / 19 | Project membership effects |
| 23 | MFA/SSO org policy, restore archived, owner transfer hardening |
| 29 | External/client collaboration |
| 31 | People directory / employee profiles |
| 32 | Access request workflows |
| 38 | Access review / retention |
| 39 | Dynamic workspace configuration |
