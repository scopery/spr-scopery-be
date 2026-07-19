# Phase 10 — Project Authorization Hardening TO-BE Complete

## 1. Summary

Phase 10 hardens Project Core authorization: centralized named permission methods on `ProjectWorkspaceAuthorizationService`, workspace-scoped IAM on every Project action/query, path-ownership IDOR checks (including TaskDependency get), IAM right seeds for project legacy right codes, Project access/path error catalog entries, and an expanded authorization + security regression test matrix.

No new project business features (scheduling, Gantt, finance, quote, baseline, AI planning, reporting) were added.

## 2. Source Inputs Reviewed

- `CLAUDE.md` / project coding conventions
- `PHASE_10_PROJECT_AUTHORIZATION_TO_BE_DETAILED.md`
- `PHASE_09_PROJECT_CORE_WBS_TASK_TO_BE_COMPLETE.md`
- Phase 02 IAM / Phase 03 Workspace patterns (`WorkspaceIamIntegrationService`, grant cascade on member deactivate)
- Existing `modules/project/**` actions, query services, controllers
- `SecurityConfig` / `SecurityPathPolicy`
- Existing `ProjectAuthorizationActionTest` / `ProjectBusinessRulesActionTest`

## 3. Current vs TO-BE Classification Matrix

| Capability | Pre-Phase 10 | Phase 10 |
|---|---|---|
| Workspace-scoped project IAM on writes | CURRENTLY_IMPLEMENTED | Hardened via named methods |
| Workspace-scoped project IAM on queries | CURRENTLY_IMPLEMENTED | Hardened; dependency get path fixed |
| Path ownership (phase/WBS/task) | CURRENTLY_IMPLEMENTED | Verified + tested |
| Path ownership (dependency get) | PARTIALLY_IMPLEMENTED | MUST → fixed |
| Assignee active workspace member | CURRENTLY_IMPLEMENTED | Verified + tested |
| Archived project blocks child mutations | CURRENTLY_IMPLEMENTED | Verified + tested |
| Inactive workspace member denial | CURRENTLY_IMPLEMENTED (IAM grant revoke cascade) | Documented + tested via IAM deny |
| Named auth service API (§8) | PARTIALLY (2 methods) | Expanded to full verb surface |
| Fine-grained ACTIVATE/MOVE/ASSIGN/STATUS/DEPENDENCY authorities | Missing | **Intentional simplification** (documented) |
| Project rights in `IamRightCatalogInitializer` | Missing (warn on seed) | Seeded |
| Project access/path error codes (§25) | PARTIAL (`*_PROJECT_MISMATCH`) | Catalog expanded |
| Authorization test matrix (§26) | Thin negatives | Expanded (~45 auth cases + security regression) |
| Per-project IAM | Missing | **DEFERRED** |
| Finance/quote/baseline/AI/report auth | Missing | **DEFERRED** (later phases) |

## 4. Implemented in Current BE

- All Project / Phase / WBS / Task / Dependency write actions already called `ProjectWorkspaceAuthorizationService`
- Query services already required VIEW authorities with workspace filter on project list
- `ProjectMutationGuard` blocked child mutations on ARCHIVED / non-DRAFT-ACTIVE projects
- Assignee/in-charge validated via `WorkspaceMemberRepository.isActiveMember`
- Controllers under `/api/projects/**` and `/api/phase-definitions` (no `/api/v1`)
- Security: `.anyRequest().authenticated()` — no project `permitAll`

## 5. Implemented / Hardened in This Phase

- Expanded `ProjectWorkspaceAuthorizationService` with named methods (`requireProjectCreate`, `requireTaskAssign`, `requireWbsMove`, `requireTaskDependencyCreate`, …)
- Wired all Project Core actions/queries to named methods
- Fixed `TaskDependencyQueryService.getTaskDependency(projectId, id)` path ownership + controller pass-through
- Seeded PROJECT / PROJECT_PHASE / WBS / TASK legacy rights in `IamRightCatalogInitializer`
- Expanded `ProjectErrorCatalog` with access/path/cross-workspace concepts
- Expanded `ProjectAuthorizationActionTest` to Phase 10 §26 coverage
- Added `ProjectSecurityRegressionTest` (§26.8)

## 6. Deferred Items and Target Phase

| Item | Target |
|---|---|
| Fine-grained PROJECT_ACTIVATE / PHASE_COMPLETE / WBS_MOVE / TASK_ASSIGN / TASK_STATUS_UPDATE / DEPENDENCY_* IAM actions | Optional later; currently simplified (see §10) |
| Per-project IAM resource | Phase 19 / 29–30 if private/external sharing required |
| PhaseDefinition Mode B catalog management rights expansion | Phase 11 |
| Capacity / calendar auth | Phase 12 |
| Scheduling / Gantt auth | Phase 13–14 |
| Finance / quote / baseline auth | Phase 15–19 |
| Notification recipient access re-check | Phase 20 |
| AI dual auth | Phase 21 |
| Report export auth | Phase 22 |
| Full auth audit | Phase 23 |

## 7. Project Authorization Model Decision

**Model:** Workspace-scoped project permissions.

A project belongs to a workspace. Access is checked against the workspace IAM resource with project-related authorities (`PROJECT_*`, `PROJECT_PHASE_*`, `PROJECT_WBS_*`, `PROJECT_TASK_*`).

This is sufficient while all workspace members share the same project visibility model.

## 8. Per-project IAM Decision

```text
Per-project IAM: not implemented.
Reason: workspace-scoped permission model is the current product decision for core 00–23.
Future trigger: private projects inside workspace; client/vendor limited project access;
project-level finance/document/AI restrictions; external portal sharing (Phase 19 / 29–30).
```

## 9. Permission Matrix Implemented

| Operation | Authority enforced |
|---|---|
| Create project | `PROJECT_CREATE` |
| View / list projects | `PROJECT_VIEW` |
| Update / hold / complete project | `PROJECT_UPDATE` |
| Activate project | `PROJECT_UPDATE` (simplified) |
| Archive project | `PROJECT_ARCHIVE` |
| Phase view/list | `PROJECT_PHASE_VIEW` |
| Phase create | `PROJECT_PHASE_CREATE` |
| Phase update / activate / complete | `PROJECT_PHASE_UPDATE` (simplified for activate/complete) |
| Phase archive | `PROJECT_PHASE_ARCHIVE` |
| WBS view/tree | `PROJECT_WBS_VIEW` |
| WBS create | `PROJECT_WBS_CREATE` |
| WBS update / move | `PROJECT_WBS_UPDATE` (move simplified) |
| WBS archive | `PROJECT_WBS_ARCHIVE` |
| Task view/list | `PROJECT_TASK_VIEW` |
| Task create | `PROJECT_TASK_CREATE` |
| Task update / assign / status change | `PROJECT_TASK_UPDATE` (assign/status simplified) |
| Task archive | `PROJECT_TASK_ARCHIVE` |
| Dependency view/list | `PROJECT_TASK_VIEW` (simplified) |
| Dependency create/remove | `PROJECT_TASK_UPDATE` (simplified) |

## 10. Simplified Permission Mappings

Documented intentional simplifications (Phase 10 §8 / §20 allowed):

| TO-BE authority | Mapped to |
|---|---|
| `PROJECT_ACTIVATE` | `PROJECT_UPDATE` |
| `PROJECT_PHASE_ACTIVATE` | `PROJECT_PHASE_UPDATE` |
| `PROJECT_PHASE_COMPLETE` | `PROJECT_PHASE_UPDATE` |
| `PROJECT_WBS_MOVE` | `PROJECT_WBS_UPDATE` |
| `PROJECT_TASK_ASSIGN` | `PROJECT_TASK_UPDATE` |
| `PROJECT_TASK_STATUS_UPDATE` | `PROJECT_TASK_UPDATE` |
| `PROJECT_TASK_DEPENDENCY_VIEW` | `PROJECT_TASK_VIEW` |
| `PROJECT_TASK_DEPENDENCY_CREATE` | `PROJECT_TASK_UPDATE` |
| `PROJECT_TASK_DEPENDENCY_REMOVE` | `PROJECT_TASK_UPDATE` |

Named methods on `ProjectWorkspaceAuthorizationService` preserve intent in call sites even when the underlying authority is shared.

## 11. Query Authorization Strategy

- `ProjectQueryService.search` requires `workspaceId` + `PROJECT_VIEW` on that workspace (no blind cross-workspace list)
- `getProject` loads project then requires `PROJECT_VIEW` on owning workspace
- Child list/get require child VIEW authority on path `projectId`
- DTOs (`ProjectResponse`, `TaskResponse`, etc.) contain no finance/quote/margin fields

**403 vs 404 masking policy:**

```text
- Missing project / child → 404 (PROJECT_NOT_FOUND / *_NOT_FOUND)
- Known project, lacking permission → 403 IAM_ACCESS_DENIED
- Path mismatch (child belongs to another project) → 422 *_PROJECT_MISMATCH
- Cross-workspace enumeration via list is blocked by required workspaceId + PROJECT_VIEW
```

## 12. IDOR / Path Ownership Strategy

Every nested get/write verifies `child.projectId == path.projectId` before mutation or response.

Covered:

- ProjectPhase path
- WbsNode path + move parent same project
- Task path + phase/WBS same project
- TaskDependency path (get + remove) + predecessor/successor same project

## 13. Archived Project / Workspace Behavior

- **Archived project:** view allowed with `PROJECT_VIEW`; child mutations blocked by `ProjectMutationGuard` (`PROJECT_ALREADY_ARCHIVED`)
- **Archived/inactive workspace:** mutations blocked via IAM `RESOURCE_INACTIVE` after workspace archive deactivates IAM resource; create also requires ACTIVE workspace (`PROJECT_WORKSPACE_NOT_ACTIVE`)
- **List under archived workspace:** denied by `PROJECT_VIEW` / resource inactive (documented behavior)

## 14. Inactive Membership Behavior

- Inactive workspace member cannot read/write projects: `DeactivateWorkspaceMemberAction` revokes grants → subsequent access fails `IAM_ACCESS_DENIED`
- Inactive assignee rejected: `TASK_ASSIGNEE_NOT_WORKSPACE_MEMBER` (422)
- Suspended org member denied through workspace membership cascade

## 15. Assignment Authorization Rules

- Actor needs `PROJECT_TASK_UPDATE` (covers assign)
- Target must be active workspace member
- Assignment changes audited via activity / platform publisher after successful auth + validation
- Role/capacity fit deferred to Phase 12+

## 16. APIs Checked

| Surface | Path prefix | Auth |
|---|---|---|
| Project | `/api/projects` | PROJECT_* |
| ProjectPhase | `/api/projects/{projectId}/phases` | PROJECT_PHASE_* + path |
| WBS | `/api/projects/{projectId}/wbs-nodes` | PROJECT_WBS_* + path |
| Task | `/api/projects/{projectId}/tasks` | PROJECT_TASK_* + path |
| TaskDependency | `/api/projects/{projectId}/task-dependencies` | TASK_VIEW/UPDATE + path |
| PhaseDefinition | `/api/phase-definitions` | PHASE_DEFINITION_* / system right |

No `/api/v1/projects` routes. No project `permitAll`.

## 17. Tests Added

`ProjectAuthorizationActionTest` expanded to cover:

- Project create/view/update/archive allow/deny
- Cross-workspace get forbidden
- Inactive member deny (via IAM)
- Phase/WBS/Task/Dependency permission denies
- Path mismatch / IDOR
- `taskPhaseFromOtherProject_rejected` / `taskWbsFromOtherProject_rejected`
- `dependencyPredecessorFromOtherProject_rejected` / `dependencySuccessorFromOtherProject_rejected`
- Assignee not member / inactive assignee
- Archived project view allowed; create/update task, create phase/dependency rejected
- `archivedWorkspace_createProject_rejected` / list deny behavior documented
- §26.7 query filters: project/task/wbs/phase query auth + no finance DTO fields

`ProjectSecurityRegressionTest`:

- Controllers under `/api`, no v1
- No project paths in public/permit lists
- Controllers do not return JPA/domain types

Existing `ProjectBusinessRulesActionTest` remains the business-rule companion.

## 18. Commands Run

```text
mvn -q -Dtest=ProjectAuthorizationActionTest,ProjectSecurityRegressionTest,ProjectBusinessRulesActionTest test
mvn -q clean test
```

## 19. Test Results

```text
mvn -q clean test → BUILD SUCCESS
Tests run: 731, Failures: 0, Errors: 0, Skipped: 1
```

## 20. Manual Verification

- [ ] User with PROJECT_CREATE can create project
- [ ] User without PROJECT_CREATE cannot create project
- [ ] User with PROJECT_VIEW can view project
- [ ] User without PROJECT_VIEW cannot view project
- [ ] User cannot access project in another workspace
- [ ] User cannot list projects from inaccessible workspace
- [ ] User cannot use phaseId / wbsNodeId from another project in task create
- [ ] User cannot use predecessor/successor from another project in dependency create
- [ ] Inactive workspace member cannot view/update/create
- [ ] Archived project blocks child mutations
- [ ] Assignee must be active workspace member
- [ ] Query endpoints return only authorized records
- [ ] Authorization denied does not leak sensitive details (no finance fields; IAM deny messages)

## 21. Assumptions

- Workspace-scoped IAM remains the product model for core roadmap
- Inactive membership is enforced via IAM grant revocation (not a duplicate membership check in every project action)
- Fine-grained ACTIVATE/MOVE/ASSIGN/DEPENDENCY authorities are intentionally collapsed onto UPDATE/VIEW
- PhaseDefinition Mode B management already present; Phase 11 may expand further

## 22. Deviations From Prompt

- Did **not** seed separate IAM action codes for ACTIVATE/MOVE/ASSIGN/STATUS/DEPENDENCY — used documented simplification (allowed by Phase 10 §8 / §20)
- Permission denial continues to surface as `IAM_ACCESS_DENIED` (403); project-specific access catalog codes exist for future/optional wrapping but are not yet thrown by the auth service
- Path mismatch codes remain `*_PROJECT_MISMATCH` at throw sites; alias catalog entries (`PROJECT_*_PATH_MISMATCH`) added for taxonomy completeness

## 23. Known Risks

- Shared UPDATE authority means a user who can update tasks can also assign and change status — acceptable now, may need SoD later
- 403 on known-but-forbidden project can still confirm existence to an authenticated caller who guesses UUIDs; list enumeration is blocked
- Activity logs visible via admin tooling are not yet filtered by workspace in a dedicated viewer (future hardening)

## 24. Future Phases That Must Return to Project Authorization

- Phase 11 — Project template / phase catalog rights
- Phase 12 — Capacity / allocation / calendar
- Phase 13–14 — Schedule / Gantt
- Phase 15–17 — Rate / finance field masking
- Phase 18 — Quote SoD
- Phase 19 — Baseline / change request (+ possible per-project IAM trigger)
- Phase 20 — Notification recipient access re-check
- Phase 21 — AI dual auth
- Phase 22 — Report export
- Phase 23 — Full authorization audit
- Phase 29–30 — External collaboration / per-project IAM
