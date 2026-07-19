# Phase 27 — Document Hub / Generation Complete

## 1. Summary

Phase 27 delivered `modules/documenthub`: Document create/list/get/approve, DocumentFolder CRUD/archive, DocumentVersion upload/list/get/download (storage_key reference), DocumentShare create/list/revoke, DocumentTemplate workspace CRUD, GeneratedDocumentJob queue/list/get/complete, shared kernel, Flyway `V64` (+ `V68` audit alignment), event seeder `@Order(33)`, IAM `DOCUMENT_HUB_*`.

## 2. Source Inputs Reviewed

- `PHASE_27_DOCUMENT_HUB_GENERATION_TO_BE_DETAILED.md`
- Phase 08 knowledge DocumentType, Phase 26 quality

## 3. Current vs TO-BE Classification Matrix

| Capability | Classification | Status |
|---|---|---|
| Document create/list/get/approve | MUST | Implemented |
| DocumentFolder HTTP | MUST | Implemented |
| DocumentVersion upload/download | MUST | Implemented (storage_key reference mode) |
| DocumentShare | MUST | Implemented |
| DocumentTemplate + GeneratedJob | MUST | Implemented |
| Events + IAM | MUST | Implemented |

## 4. Implemented in Current BE

- `V64__create_document_hub_tables_phase27.sql`
- Controllers: documents, folders, versions, shares, templates, generated-documents
- Unit test: DocumentDomainTest

## 5. Deferred Items

None for Phase 27 required MUST scope.

| Item | Notes |
|---|---|
| Binary blob storage / object-store integration | Storage_key reference is the contracted mode; real CI/CD object store is environment-specific |
| DocumentLink / AccessLog dedicated HTTP | Optional observability; tables exist |
| Full template rendering engine | Job queue + complete hooks implemented; render engine optional follow-up |

## 6. Release Decision

**Phase 27 MUST path: COMPLETE** — document register/approve, folders, version upload/download reference, shares, templates, generation jobs.
