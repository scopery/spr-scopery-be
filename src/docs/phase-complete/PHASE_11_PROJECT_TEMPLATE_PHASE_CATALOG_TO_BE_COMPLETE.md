# Phase 11 — Project Template / Phase Catalog TO-BE Complete

## 1. Summary

Phase 11 delivers reusable project structure: hardened PhaseDefinition catalog (ORGANIZATION scope, INACTIVE status, activate/deactivate), full ProjectTemplate versioning (immutable published versions), template phase/WBS/task/dependency structure, and transactional apply-to-project that creates live Project/Phase/WBS/Task/Dependency records with source-template traceability. Scheduling, Gantt, capacity, finance, quote, baseline, marketplace, clone-from-project, AI templates, WorkspacePhaseDefinitionSetting, and DocumentType validation are **explicitly not implemented**.

## 2. Source Inputs Reviewed

- `CLAUDE.md` / project coding conventions (Action + QueryService, DDD layout, error catalog)
- `PHASE_11_PROJECT_TEMPLATE_PHASE_CATALOG_TO_BE_DETAILED.md`
- Phase 09 completion + existing `modules/project/**` patterns
- Phase 10 authorization patterns (`ProjectWorkspaceAuthorizationService`)
- IAM catalog initializers / `IamAuthorities`
- Phase 04 outbox/audit (`ProjectPlatformPublisher`, `AuditEventType`)
- Phase 05 event registry seed pattern

## 3. Current vs TO-BE Classification Matrix

| Capability | Pre-Phase 11 | Phase 11 result |
|---|---|---|
| PTC-001 PhaseDefinition hardening | PARTIALLY_IMPLEMENTED | MUST → Implemented |
| PTC-002 WorkspacePhaseDefinitionSetting | Missing | **DEFERRED_TO_PHASE_11_FOLLOWUP** (status+scope only) |
| PTC-003 ProjectTemplate create | Missing | MUST → Implemented |
| PTC-004 Template lifecycle | Missing | MUST → Implemented |
| PTC-005 ProjectTemplateVersion | Missing | MUST → Implemented |
| PTC-006 TemplatePhase | Missing | MUST → Implemented |
| PTC-007 TemplateWbsNode | Missing | MUST → Implemented |
| PTC-008 TemplateTask | Missing | MUST → Implemented |
| PTC-009 TemplateTaskDependency | Missing | MUST → Implemented |
| PTC-010 Apply template | Missing | MUST → Implemented (new project only) |
| PTC-011 Clone from project | Missing | **DEFERRED** |
| PTC-012 Marketplace | Missing | **DEFERRED_TO_POST_23_BACKLOG** |
| Schedule/Gantt/Finance/AI | Missing | Not in scope |
| DocumentType deliverable refs | — | Nullable UUID columns; **no validation** |
| HTTP Idempotency-Key | Missing | Documented gap (same as Phase 09) |

## 4. Implemented in Current BE (pre-Phase 11)

- PhaseDefinition (SYSTEM/WORKSPACE, ACTIVE/ARCHIVED) + CRUD/archive
- Project Core Phase 09 (Project/Phase/WBS/Task/Dependency)
- Project IAM authorities and Phase 10 workspace auth service
- Outbox/activity foundation via `ProjectPlatformPublisher`

## 5. Implemented / Hardened in This Phase

- Flyway `V48__create_project_template_tables_phase11.sql`
- PhaseDefinition: `organizationId`, `ORGANIZATION` scope, `INACTIVE` status, activate/deactivate, org create API
- Archive PhaseDefinition blocked when used by ProjectPhase **or** ProjectTemplatePhase
- Built-in semantics kept as `is_system_default` / `isSystemDefault` (no separate `built_in` column on phase_definition; soft-archive allowed; hard-delete not exposed)
- Template packages: `template`, `templateversion`, `templatephase`, `templatewbs`, `templatetask`, `templatedependency`
- Apply template: one `@Transactional` action; default estimateHours `1.0` when template estimate null or `copyEstimateHours=false`
- Project source template fields + `withSourceTemplate(...)`
- IAM: `PROJECT_TEMPLATE_MANAGEMENT` + authorities; `PHASE_DEFINITION_MANAGEMENT` permission seed
- Events seeded (Phase 11 set) + publisher enqueue/audit helpers
- AuditEventType: `PROJECT_TEMPLATE_ARCHIVED`, `PROJECT_TEMPLATE_VERSION_PUBLISHED`, `PROJECT_TEMPLATE_APPLIED`, `PHASE_DEFINITION_ARCHIVED`

## 6. Seed-only Items Added

- Phase 11 event definitions under `SCOPERY_PROJECT` (template + phase-definition lifecycle)
- IAM rights/permissions for project templates (+ phase-definition permission catalog entry)
- No marketplace / AI / notification template seeds

## 7. Deferred Items and Target Phase

| Item | Target |
|---|---|
| WorkspacePhaseDefinitionSetting (PTC-002) | Phase 11 follow-up / Phase 23 |
| Clone template from existing project (PTC-011) | Phase 16/21 / Post-23 |
| Template marketplace (PTC-012) | Post-23 backlog |
| DocumentType ACTIVE validation on deliverable refs | Phase 27 |
| Apply to existing empty project | Later product decision |
| Template admin notifications | Phase 20 |
| Scheduling offsets execution | Phase 13 |
| Gantt / finance / quote / baseline / AI | Phases 14–21 |
| HTTP Idempotency-Key on template/apply POSTs | Phase 04 pattern available; not wired |

## 8. PhaseDefinition Catalog Decision

- Path kept: `/api/phase-definitions` (not moved to `/api/project/phase-definitions`) for client compatibility
- Scopes: SYSTEM / ORGANIZATION / WORKSPACE
- Statuses: ACTIVE / INACTIVE / ARCHIVED
- `is_system_default` remains the built-in flag (documented alias of TO-BE `built_in`)
- New ProjectPhase / TemplatePhase reject non-ACTIVE definitions
- Workspace phase set table **not** introduced — availability controlled by status + scope only

## 9. ProjectTemplate Entity Mapping

| Field | Column / notes |
|---|---|
| Table | `project_template` |
| Scope | SYSTEM / ORGANIZATION / WORKSPACE (partial unique indexes on code) |
| Status | DRAFT / ACTIVE / INACTIVE / ARCHIVED |
| Category | SOFTWARE_IMPLEMENTATION, WEB_APP, MOBILE_APP, ERP_ROLLOUT, MAINTENANCE, SUPPORT, CUSTOM |
| Visibility | PRIVATE / WORKSPACE / ORGANIZATION / SYSTEM |
| currentVersionId | Soft FK to published version |
| built_in | Boolean column on template |

## 10. ProjectTemplateVersion Entity Mapping

| Field | Notes |
|---|---|
| Table | `project_template_version` |
| Status | DRAFT / PUBLISHED / ARCHIVED |
| Unique | `(project_template_id, version_number)` |
| Publish | Validates ≥1 phase; WBS parent integrity; task phase/WBS refs; dependency integrity + cycle |
| Immutability | Non-DRAFT versions reject structural mutations via `TemplateVersionMutationGuard` |

## 11. TemplatePhase/WBS/Task/Dependency Mapping

| Entity | Table | Notes |
|---|---|---|
| ProjectTemplatePhase | `project_template_phase` | Optional `phase_definition_id`; unique display_order; nullable deliverable_document_type_id |
| ProjectTemplateWbsNode | `project_template_wbs_node` | Self-parent; depth/order_index; cascade delete opt-in |
| ProjectTemplateTask | `project_template_task` | estimateHours null or > 0; phase required |
| ProjectTemplateTaskDependency | `project_template_task_dependency` | Default FINISH_TO_START; BFS cycle detection |

## 12. Apply Template Algorithm

1. Auth: `PROJECT_CREATE` + `PROJECT_TEMPLATE_APPLY` on target workspace
2. Template ACTIVE + version PUBLISHED + workspace ACTIVE
3. Create Project (same uniqueness/owner/date rules as CreateProjectAction)
4. Map TemplatePhases → ProjectPhases (order preserved)
5. Map TemplateWbsNodes by depth → WbsNodes (hierarchy preserved)
6. Map TemplateTasks → Tasks (estimate default **1.0** when not copied)
7. Map TemplateDependencies → TaskDependencies
8. Persist `sourceTemplateId` / `sourceTemplateVersionId` / `sourceTemplateAppliedAt`
9. Emit `PROJECT_TEMPLATE_APPLIED` (+ project created outbox)
10. **Does not** create schedule/Gantt/finance/quote/baseline/documents

## 13. API Changes

| Surface | Path |
|---|---|
| Phase definitions (legacy kept) | `/api/phase-definitions` (+ org create, activate, deactivate) |
| Templates | `/api/project/templates` |
| Versions | `/api/project/templates/{templateId}/versions` |
| Structure | `.../phases`, `.../wbs-nodes`, `.../wbs-tree`, `.../tasks`, `.../task-dependencies` |
| Apply | `POST .../versions/{versionId}/apply` |

## 14. Authorization Matrix

| Authority | Mapping |
|---|---|
| PROJECT_TEMPLATE_VIEW/CREATE/UPDATE/ARCHIVE/APPLY/MANAGE | Seeded |
| PROJECT_TEMPLATE_PUBLISH | Uses **UPDATE** action code (`PUBLISH_PROJECT_TEMPLATE` right) — documented |
| SYSTEM/ORGANIZATION template writes | System right `SYSTEM_GOVERNANCE_MANAGE_PHASE_DEFINITION` gate via `TemplateAccessSupport` |
| WORKSPACE templates | Workspace membership + template authorities |
| Apply | `PROJECT_CREATE` + `PROJECT_TEMPLATE_APPLY` (child creates covered by apply privilege) |

## 15. Event Registry Seeder Matrix

All Phase 11 codes from spec §12.1 seeded under `SCOPERY_PROJECT` (52 total project events after Phase 11). Key emissions:

| Event | Seeded | Emitted |
|---|---|---|
| PHASE_DEFINITION_* | Yes | Yes (create/update/activate/deactivate/archive) |
| PROJECT_TEMPLATE_* lifecycle | Yes | Yes |
| PROJECT_TEMPLATE_VERSION_* | Yes | Yes |
| Structure create/update/delete events | Yes | Partial (activity always; outbox where publisher wired) |
| PROJECT_TEMPLATE_APPLIED | Yes | Yes |

## 16. Activity / Audit / Outbox Notes

- Activity actions from §16.1 plus lifecycle helpers in `ProjectActivityActions`
- Immutable audit for template archive, version publish, template applied, phase definition archived
- Outbox via `ProjectPlatformPublisher`
- HTTP Idempotency-Key: **not** wired (documented gap)

## 17. Idempotency Strategy

- Same as Phase 09: rely on DB uniqueness (project code, template code) + transactional apply
- Dedicated Idempotency-Key middleware for apply: **gap**

## 18. Tests Added

`ProjectTemplateBusinessRulesActionTest`:

- create template success / duplicate
- update archived rejected
- activate without published rejected
- publish without phases rejected
- update published version rejected
- apply creates project structure + source template fields
- apply inactive rejected
- archive blocks apply
- template dependency cycle rejected

`ProjectEventDefinitionSeedInitializerTest` updated for Phase 11 event codes.

## 19. Commands Run

```text
mvn -q -DskipTests compile
mvn -q -Dtest=ProjectTemplateBusinessRulesActionTest,ProjectEventDefinitionSeedInitializerTest,ProjectBusinessRulesActionTest test
mvn -q test
```

## 20. Test Results

```text
mvn -q -DskipTests compile → SUCCESS
ProjectTemplateBusinessRulesActionTest + related → SUCCESS
mvn test (full suite) → BUILD SUCCESS
```

## 21. Manual Verification

- [ ] Flyway V48 applies cleanly after V47
- [ ] Create org/workspace/system phase definitions; activate/deactivate
- [ ] Archive phase definition blocked when used by project phase or template phase
- [ ] Create workspace template → version → phases → WBS → tasks → deps
- [ ] Reject dependency cycle; reject edit of published version
- [ ] Publish version; activate template; apply to ACTIVE workspace
- [ ] Confirm project phases/WBS/tasks/deps + source_template_* set
- [ ] Confirm no Gantt/finance/quote/baseline rows
- [ ] Rerun event/IAM seeders — no duplicates

## 22. Assumptions

- Apply creates a **new** project only (not apply-to-existing-empty)
- Publish sets template `currentVersionId` and archives previous published current when present
- ORGANIZATION template auth uses system governance right in Phase 11 (stricter than org-membership-only)
- Default task estimate `1.0` when template estimate omitted satisfies Phase 09 DB CHECK
- Nullable `deliverable_document_type_id` stored without DocumentType existence checks

## 23. Deviations From Prompt

- Phase definitions path remains `/api/phase-definitions` (prompt allowed keeping it)
- Phase definition `built_in` not added as separate column; `is_system_default` retained
- Built-in phase definitions can be soft-archived when unused; hard-delete API not provided
- Template PUBLISH authority maps to UPDATE action code (no new IamActionCodes.PUBLISH)
- SYSTEM/ORGANIZATION template management gated by existing system phase-definition governance right
- Full §20 test matrix not exhaustively automated — critical rules covered in unit tests
- Completion path: `src/docs/phase-complete/` (repo convention)

## 24. Known Risks

- Large template apply is a single transaction — deep WBS/task graphs may need Phase 23 performance review
- SYSTEM/ORGANIZATION template access model may be too coarse for multi-tenant org admins
- Missing Idempotency-Key allows duplicate projects on client retries with different codes only uniqueness protects same code
- DocumentType UUIDs may dangle until Phase 27 validation

## 25. Future Phases That Must Return to Project Template

- Phase 11 follow-up — WorkspacePhaseDefinitionSetting
- Phase 12 — Capacity / default role effort
- Phase 13 — Offset/lag scheduling interpretation
- Phase 14 — Gantt presets
- Phase 15–18 — Cost/finance/quote from template roles
- Phase 19 — Baseline seed from apply
- Phase 20 — Template admin notifications
- Phase 21 — AI template suggestions (human-approved)
- Phase 22–23 — Usage reporting / apply performance
- Phase 27 — Document deliverable generation
- Post-23 — Marketplace / clone-from-project

---

## PTC Capability Gap Matrix (honest)

| Code | Classification | Result |
|---|---|---|
| PTC-001 PhaseDefinition harden | MUST_IMPLEMENT_IN_PHASE_11 | Implemented |
| PTC-002 Workspace phase setting | DEFERRED_TO_PHASE_11_FOLLOWUP | Deferred (status+scope only) |
| PTC-003 Template create | MUST | Implemented |
| PTC-004 Template lifecycle | MUST | Implemented |
| PTC-005 Template version | MUST | Implemented |
| PTC-006 Template phase | MUST | Implemented |
| PTC-007 Template WBS | MUST | Implemented |
| PTC-008 Template task | MUST | Implemented |
| PTC-009 Template dependency | MUST | Implemented |
| PTC-010 Apply template | MUST | Implemented (new project) |
| PTC-011 Clone from project | DEFERRED | Deferred |
| PTC-012 Marketplace | DEFERRED_TO_POST_23 | Deferred (list/search only) |

**Not claimed:** scheduling, Gantt, capacity, finance, quote, baseline, AI planning, DocumentType validation, WorkspacePhaseDefinitionSetting, HTTP idempotency keys.
