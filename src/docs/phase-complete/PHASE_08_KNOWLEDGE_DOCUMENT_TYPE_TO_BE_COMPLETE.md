# Phase 08 — Knowledge / Document Type Catalog TO-BE Complete

## 1. Summary

Phase 08 hardens the Knowledge **Document Type Catalog** foundation: ORGANIZATION scope, ARCHIVED lifecycle, default classification, DocumentTypeField metadata schema, IAM/event/outbox/audit wiring, built-in seeds, and classification vocabulary API. Full Document Hub (DOC content, files, versions, search, RAG, etc.) is explicitly **not** implemented.

## 2. Source Inputs Reviewed

- `CLAUDE.md` / project coding conventions
- `PHASE_00_MASTER_ROADMAP_MODULAR_REVISED.md`
- `PHASE_08_KNOWLEDGE_DOCUMENT_TYPE_TO_BE_DETAILED.md`
- Existing `modules/knowledge/documenttype/**`
- Phase 04 outbox/audit patterns (`CreateOrganizationAction`, `TransactionalOutboxService`, `ImmutableAuditEventService`)
- IAM catalogs (`IamAuthorities`, `IamRightCatalogInitializer`, `IamPermissionCatalogInitializer`)
- V20 knowledge migration + V45 harden style
- Phase 07 completion file structure

## 3. Current vs TO-BE Classification Matrix

| Area | Pre-Phase 08 | Phase 08 |
|---|---|---|
| DocumentType CRUD | CURRENTLY_IMPLEMENTED | Hardened |
| SYSTEM/WORKSPACE scope | CURRENTLY_IMPLEMENTED | + ORGANIZATION |
| DELETED status | CURRENTLY_IMPLEMENTED | Migrated to ARCHIVED |
| Classification | Missing | MUST → implemented (enum + default) |
| DocumentTypeField | Missing | MUST → implemented |
| IAM DOCUMENT_TYPE_* | CURRENTLY_IMPLEMENTED | + ARCHIVE + KNOWLEDGE_* aliases + FIELD + CLASSIFICATION |
| Events | Partial (3) | Expanded to §12.1 |
| Outbox/immutable audit | Missing | Wired |
| Built-in seeds | Partial (8) | Expanded (§16.1) + system fields |
| Full Document Hub | Missing | Deferred |

## 4. Implemented in Current BE

- Basic DocumentType entity + V20 table
- SYSTEM/WORKSPACE create endpoints
- Activate/deactivate/soft-delete
- IAM VIEW/CREATE/UPDATE/DELETE/MANAGE_DOCUMENT_TYPE
- 3 event seeds (CREATED/UPDATED/DELETED)
- Activity logging
- Unit tests for core actions

## 5. Implemented / Hardened in This Phase

- Flyway `V46__harden_knowledge_document_type_phase08.sql`
- Domain: ORGANIZATION scope, ARCHIVED status, classification, built-in, archive semantics
- Unified `POST /api/knowledge/document-types` + archive endpoint
- DocumentTypeField submodule (CRUD lifecycle + reorder)
- `GET /api/knowledge/document-classifications`
- Error catalog expansion
- Outbox enqueue + immutable audit for sensitive changes
- Expanded event + document-type seeders
- Tests for DocumentType + DocumentTypeField

## 6. Seed-only Items Added

- Knowledge event definitions for field lifecycle / reorder
- IAM rights/permissions for field + classification (no full document authorities seeded as active document module)
- Built-in SYSTEM document types + optional system fields

## 7. Deferred Items and Target Phase

- Full Document Hub → Phase 27 / POST_23_DOCUMENT_HUB_BACKLOG
- Classification access enforcement → Phase 27/39
- Dynamic org classifications → Phase 39
- Document review/publish/share/search/RAG/AI → Phase 21/22/27/35/41
- Idempotency keys on create/reorder → recommended later (Phase 04 pattern available)

## 8. DOC-001 to DOC-020 Gap Matrix

| Code | Capability | Classification |
|---|---|---|
| DOC-001 | Document creation | DEFERRED_TO_PHASE_27_FULL_DOCUMENT_HUB — **not implemented** |
| DOC-002 | Dynamic document type | MUST_IMPLEMENT_IN_PHASE_08 — **implemented/hardened** |
| DOC-003 | Rich authoring | DEFERRED_TO_PHASE_27 — **not implemented** |
| DOC-004 | File management | DEFERRED_TO_PHASE_27 — **not implemented** |
| DOC-005 | Version control | DEFERRED_TO_PHASE_27 — **not implemented** |
| DOC-006 | Metadata schema | MUST_IMPLEMENT_IN_PHASE_08 (type fields) — **DocumentTypeField implemented**; document instance metadata deferred |
| DOC-007 | Document linking | DEFERRED_TO_PHASE_27 — **not implemented** |
| DOC-008 | Review and approval | DEFERRED_TO_PHASE_27/34 — **not implemented** |
| DOC-009 | Publication | DEFERRED_TO_PHASE_27 — **not implemented** |
| DOC-010 | Document lifecycle | DocumentType lifecycle MUST — **implemented**; Document lifecycle deferred |
| DOC-011 | Classification | Foundation MUST — **enum + defaultClassification + list API**; enforcement deferred |
| DOC-012 | Sharing | DEFERRED_TO_PHASE_27/30 — **not implemented** |
| DOC-013 | Template library | Deferred; `defaultTemplateCode` pointer only |
| DOC-014 | Document generation | DEFERRED_TO_PHASE_21/27 — **not implemented** |
| DOC-015 | Knowledge collections | DEFERRED_TO_PHASE_27 — **not implemented** |
| DOC-016 | Full-text search | DEFERRED_TO_PHASE_22/41 — **not implemented** |
| DOC-017 | Acknowledgement | DEFERRED_TO_PHASE_27/35 — **not implemented** |
| DOC-018 | Expiry/review | Deferred; `defaultReviewCycleDays` on type only |
| DOC-019 | Offline/export package | DEFERRED_TO_PHASE_22/38 — **not implemented** |
| DOC-020 | AI draft/summary | DEFERRED_TO_PHASE_21/27 — **not implemented**; no RAG |

## 9. Entity Mapping

| Entity | Table | Notes |
|---|---|---|
| DocumentType | `knowledge_document_type` | Hardened columns in V46 |
| DocumentTypeField | `knowledge_document_type_field` | New in V46 |
| Document | — | **Not created** |
| DocumentClassification | Enum only | No dynamic table |

## 10. API Changes

| Method | Path | Notes |
|---|---|---|
| POST | `/api/knowledge/document-types` | Unified create (201) |
| POST | `/api/knowledge/document-types/system` | Legacy kept |
| POST | `/api/knowledge/document-types/workspace` | Legacy kept |
| PUT | `/api/knowledge/document-types/{id}` | Extended fields |
| PATCH | `/api/knowledge/document-types/{id}/archive` | New |
| PATCH | `/api/knowledge/document-types/{id}/soft-delete` | Deprecated alias → archive |
| GET | `/api/knowledge/document-classifications` | New |
| * | `/api/knowledge/document-types/{id}/fields/**` | Field CRUD/lifecycle/reorder |
| * | `/api/documents/**` | **Not implemented** |

## 11. DocumentType Rules

- Code unique within SYSTEM / ORGANIZATION / WORKSPACE scope (partial unique indexes)
- SYSTEM: no org/workspace ids
- ORGANIZATION: requires organizationId, no workspaceId
- WORKSPACE: requires workspaceId; organizationId resolved from workspace
- Built-in cannot be archived/deleted
- Archived cannot be updated/activated/deactivated
- Soft-delete maps to archive

## 12. DocumentTypeField Rules

- Field key unique per document type; normalized camelCase/snake_case
- SELECT/MULTI_SELECT require optionsJson
- System fields cannot be archived
- Path mismatch rejected
- Reorder requires full ID set for the type

## 13. Classification Foundation

- Enum: PUBLIC, INTERNAL, CONFIDENTIAL, RESTRICTED (+ CUSTOM optional in domain)
- List API returns the four primary values
- DocumentType.defaultClassification defaults to INTERNAL
- Access enforcement by classification: **not implemented**

## 14. IAM Authorization Matrix

| Right / Authority | Status |
|---|---|
| VIEW/CREATE/UPDATE/DELETE/MANAGE_DOCUMENT_TYPE | Kept (compat) |
| ARCHIVE_DOCUMENT_TYPE | Added |
| KNOWLEDGE_DOCUMENT_TYPE_* aliases | Added |
| KNOWLEDGE_DOCUMENT_TYPE_FIELD_* | Added |
| KNOWLEDGE_CLASSIFICATION_VIEW | Added |
| Full DOCUMENT_* hub rights | Not seeded as live document module |

Auth remains action-level (no KnowledgeSecurityInterceptor); same pattern as workspace modules.

## 15. Event Registry Seeder Matrix

| Event | Seeded |
|---|---|
| DOCUMENT_TYPE_CREATED/UPDATED/ACTIVATED/DEACTIVATED/ARCHIVED | Yes |
| DOCUMENT_TYPE_DELETED | Kept (legacy) |
| DOCUMENT_TYPE_FIELD_* + FIELDS_REORDERED | Yes |
| Full DOCUMENT_* hub events | Deferred |

Source system: `SCOPERY_KNOWLEDGE`.

## 16. Notification/AI Integration Notes

- No Phase 08 email templates required
- No RAG / AI draft / document summary
- Future AI must respect document IAM + classification (deferred)

## 17. Activity / Audit / Outbox Notes

- Activity via `KnowledgeActivityLogger` (create/update/activate/deactivate/archive + field ops)
- Outbox via `TransactionalOutboxService` with source `SCOPERY_KNOWLEDGE`
- Immutable audit: classification change, schema change, archive, field schema change
- HTTP idempotency keys: not wired in this phase (documented gap)

## 18. Built-in Seed Data

- Legacy types retained (ARTICLE, GUIDE, …)
- Added §16.1 types (BRD, SRS, TECHNICAL_SPEC, …) with `built_in=true` when newly inserted
- Existing codes are not overwritten
- Basic system fields seeded idempotently per type (owner, summary, status, classification, …)

## 19. Tests Added

- Expanded `DocumentTypeActionTest` (organization scope, archive/built-in reject, classification, outbox verify)
- New `DocumentTypeFieldActionTest` (create, options required, system archive reject, path mismatch, reorder)

## 20. Commands Run

```text
mvn -q compile
mvn -q test -Dtest=DocumentTypeActionTest,DocumentTypeFieldActionTest
mvn -q test
```

## 21. Test Results

```text
mvn -q compile → SUCCESS
DocumentTypeActionTest → Tests run: 15, Failures: 0, Errors: 0
DocumentTypeFieldActionTest → Tests run: 7, Failures: 0, Errors: 0
Knowledge suite total → Tests run: 22, Failures: 0, Errors: 0
mvn -q test (full suite) → SUCCESS (exit 0)
```

## 22. Manual Verification

- [ ] Flyway V46 applies cleanly on a DB with V20 knowledge table
- [ ] Swagger shows document-types, fields, classifications
- [ ] Soft-delete endpoint archives (status ARCHIVED)
- [ ] Built-in types reject archive
- [ ] System seeder is idempotent across restarts

## 23. Assumptions

- ORGANIZATION-scoped type management uses system governance right in this phase
- Legacy soft-delete/deleted columns retained for compatibility; archive_* is canonical
- Pre-existing SYSTEM types keep prior `built_in=false` if already present (not overwritten)

## 24. Deviations From Prompt

- Kept legacy right codes (`VIEW_DOCUMENT_TYPE`, …) and added `KNOWLEDGE_*` aliases rather than renaming in place
- Soft-delete endpoint retained as deprecated alias
- No KnowledgeSecurityInterceptor (auth already enforced in actions)
- CUSTOM classification exists in domain enum but list API returns primary four only
- HTTP idempotency not implemented for create/reorder

## 25. Known Risks

- Existing SYSTEM types may not flip to `built_in=true` without a one-time data fix
- Workspace search includes SYSTEM types when workspaceId is provided (intentional catalog UX)
- Field SELECT seeded with static options JSON; org-defined options later

## 26. Future Phases That Must Return to Knowledge/Document Hub

- Phase 09/10 Project — document links to project/WBS/task
- Phase 21 AI-assisted planning — draft/generation against real documents
- Phase 22 Reporting/Search/Export
- Phase 27 Full Document Hub
- Phase 34 Workflow approval
- Phase 35 Advanced notification (ack/review due)
- Phase 39 Compliance / dynamic classification
- Phase 41 Semantic index / knowledge graph
