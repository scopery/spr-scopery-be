# PHASE 08 — TO-BE Knowledge / Document Type Catalog Foundation & Full Document Hub Gap Contract

> Project: Scopery Backend  
> Phase: 08  
> Document type: TO-BE implementation-grade specification  
> Status: Planning / Gap-driven implementation spec  
> Depends on: Phase 00 — Master Backend Roadmap, Phase 01 — API Path & Security Baseline, Phase 02 — IAM Core, Phase 03 — Workspace Core, Phase 04 — Platform Audit / Outbox / Idempotency, Phase 05 — Event Registry, Phase 06 — Notification Engine, Phase 07 — AI Agent Platform  
> API base: `/api`  
> Primary module: `modules/knowledge`  
> Related modules: `iam`, `workspace`, `eventregistry`, `notification`, future `document`, `project`, `requirements`, `aiagent`, `reporting`, `compliance`, `dynamic-config`  
> Important rule: This file is **not an as-is document**. It defines the TO-BE target for the Knowledge / Document foundation, compares it to the current minimal Knowledge module, and marks the full Document Hub capabilities that must be deferred.

---

# 0. Purpose of this file

Phase 08 is the first Knowledge / Document foundation phase.

The current backend appears to have only a small Knowledge module, most likely around `DocumentType` catalog.

The full Work OS catalog has a much larger module:

```text
Document and Knowledge Hub
DOC-001 → DOC-020
```

This includes:

```text
document creation
dynamic document types
rich authoring
file management
version control
metadata schema
document linking
review and approval
publication
document lifecycle
classification
sharing
template library
document generation
knowledge collections
full-text search
acknowledgement
expiry/review cycles
offline/export package
AI draft and summary
```

Phase 08 must not pretend all of this exists.

Instead, Phase 08 must create/harden the **Document Type Catalog foundation** and define the full future gap contract.

---

# 1. Source inputs

Before coding Phase 08, the agent must read:

```text
1. Current backend codebase
2. Current BE feature/entity/business-rule inventory
3. Dynamic Work OS Full Module Feature Catalog
4. Phase 00 master roadmap
5. Phase 01 API/security baseline
6. Phase 02 IAM TO-BE spec
7. Phase 03 Workspace TO-BE spec
8. Phase 04 Platform Audit/Outbox/Idempotency spec
9. Phase 05 Event Registry TO-BE spec
10. Phase 06 Notification TO-BE spec
11. Phase 07 AI Agent Platform TO-BE spec
12. Existing knowledge module code, migrations, tests
13. Existing EventRegistry module
14. Existing IAM resource/action seeders
15. Existing AI Agent module if any document AI hooks exist
```

The agent must not implement based only on current code.

---

# 2. Current backend snapshot

Current BE inventory says Knowledge currently has:

```text
1 action-level function
4 business rules
```

Current Knowledge entity likely includes:

```text
DocumentType
```

Current Knowledge scope likely covers:

```text
Create/manage a document type catalog
```

This is only a small fraction of the future Document and Knowledge Hub.

Therefore Phase 08 must classify:

```text
CURRENTLY_IMPLEMENTED:
- basic DocumentType if code confirms it

MUST_IMPLEMENT_IN_PHASE_08:
- DocumentType catalog hardening
- optional DocumentTypeField / metadata schema foundation if missing
- IAM permissions for document type catalog
- EventRegistry events for document type changes
- activity/audit consistency
- tests

DEFERRED:
- full document repository
- file upload/download
- rich authoring
- versioned document content
- review/approval
- publication
- sharing
- search
- RAG/AI summaries
- offline export package
```

---

# 3. Future-state catalog: DOC-001 → DOC-020

The Dynamic Work OS feature catalog defines the Document and Knowledge Hub capabilities:

```text
DOC-001 Document creation
DOC-002 Dynamic document type
DOC-003 Rich authoring
DOC-004 File management
DOC-005 Version control
DOC-006 Metadata schema
DOC-007 Document linking
DOC-008 Review and approval
DOC-009 Publication
DOC-010 Document lifecycle
DOC-011 Classification
DOC-012 Sharing
DOC-013 Template library
DOC-014 Document generation
DOC-015 Knowledge collections
DOC-016 Full-text search
DOC-017 Document acknowledgement
DOC-018 Expiry and periodic review
DOC-019 Offline/export package
DOC-020 AI draft and summary
```

Phase 08 is responsible for the **foundation**, not full delivery of all DOC features.

---

# 4. Classification labels

Every requirement uses one label:

| Label | Meaning |
|---|---|
| `CURRENTLY_IMPLEMENTED` | Current backend already implements it. Agent must verify and test. |
| `MUST_IMPLEMENT_IN_PHASE_08` | Must be implemented or hardened now. |
| `SEED_ONLY_IN_PHASE_08` | Create seed definitions/permissions/events now, but full feature later. |
| `DEFERRED_TO_PHASE_XX` | Do not implement now; later phase must return. |
| `DEFERRED_TO_POST_23_BACKLOG` | Outside core 00–23 roadmap, part of full Work OS expansion. |
| `NOT_IN_SCOPE_FOR_BACKEND_NOW` | Explicitly not backend scope now. |

---

# 5. Phase 08 target statement

Phase 08 must deliver a future-ready Knowledge / Document foundation:

```text
1. Dynamic Document Type catalog.
2. Optional metadata field schema per document type.
3. Document classification vocabulary foundation.
4. Document type lifecycle.
5. Workspace/organization/system scope for document types if supported.
6. IAM permission model for knowledge/document type management.
7. Event definitions for document type changes.
8. Activity/audit integration.
9. Tests proving document type rules.
10. Clear deferral of full Document Hub capabilities to later phases.
```

Phase 08 must **not** implement a full document storage system unless product explicitly decides to move full Document Hub into core roadmap now.

---

# 6. Recommended Phase 08 scope decision

Recommended implementation split:

```text
Phase 08:
- Knowledge / Document Type Catalog
- DocumentType
- DocumentTypeField / metadata schema foundation
- Classification enum/vocabulary foundation
- Event and IAM seeders

Phase 27 or Post-23 Full Document Hub:
- Document repository
- Document content
- File upload/download
- Rich editor blocks
- Version comparison
- Review/approval
- Publication
- Knowledge collections
- Search/indexing
- Export package
- AI draft/summary
```

If the project chooses full 42-phase roadmap, map:

```text
Full Document Hub → Phase 27
Data Quality / Knowledge Graph / Semantic Index → Phase 41
```

If staying with core 00–23 only:

```text
Full Document Hub remains POST_23_DOCUMENT_HUB_BACKLOG
```

---

# 7. TO-BE capability matrix

---

## 7.1 DOC-001 — Document creation

| Item | Value |
|---|---|
| Future capability | Create blank, upload, import, duplicate, or generate from approved template |
| Current state | Not implemented in current Knowledge inventory |
| Phase 08 target | Defer full document creation; only prepare DocumentType |
| Classification | `DEFERRED_TO_PHASE_27_FULL_DOCUMENT_HUB` or `POST_23_DOCUMENT_HUB_BACKLOG` |

Future entities:

```text
Document
DocumentContent
DocumentFile
DocumentTemplate
DocumentGenerationJob
```

Future APIs:

```text
POST /api/documents
POST /api/documents/upload
POST /api/documents/import
POST /api/documents/{id}/duplicate
POST /api/documents/generate-from-template
```

Phase 08 must not create fake document CRUD with no storage/content/version model.

---

## 7.2 DOC-002 — Dynamic document type

| Item | Value |
|---|---|
| Future capability | Configure BRD, SRS, proposal, user guide, meeting minutes, policy, contract, and custom types |
| Current state | Basic DocumentType likely exists |
| Phase 08 target | Implement/harden DocumentType catalog |
| Classification | `MUST_IMPLEMENT_IN_PHASE_08` |

Required document type examples:

```text
BRD
SRS
TECHNICAL_SPEC
PROJECT_PROPOSAL
USER_GUIDE
MEETING_MINUTES
POLICY
CONTRACT
CHANGE_REQUEST
TEST_PLAN
RELEASE_NOTE
CUSTOM
```

Required rules:

```text
1. DocumentType code unique within scope.
2. Code normalized uppercase.
3. Name required.
4. Scope required.
5. Built-in/system type cannot be hard-deleted.
6. In-use document type cannot be deleted; archive/deactivate only.
7. DocumentType can define metadata fields if metadata schema foundation is implemented.
8. DocumentType has default classification.
9. DocumentType lifecycle is audited.
```

---

## 7.3 DOC-003 — Rich authoring

| Item | Value |
|---|---|
| Future capability | Structured headings, tables, embeds, references, comments, mentions |
| Current state | Not implemented |
| Phase 08 target | Defer |
| Classification | `DEFERRED_TO_PHASE_27_FULL_DOCUMENT_HUB` |

Future entities:

```text
DocumentBlock
DocumentComment
DocumentMention
DocumentReference
```

Future integration:

```text
Notification mention events → Phase 30/35
AI draft/edit assistance → Phase 21/27
```

Do not implement rich content blocks in Phase 08.

---

## 7.4 DOC-004 — File management

| Item | Value |
|---|---|
| Future capability | Upload, preview, download, replace, scan, and validate supported files |
| Current state | Not implemented |
| Phase 08 target | Defer |
| Classification | `DEFERRED_TO_PHASE_27_FULL_DOCUMENT_HUB` |

Future entities:

```text
DocumentFile
DocumentFileVersion
FileScanResult
FilePreview
```

Future concerns:

```text
virus scanning
mime validation
storage provider
signed URLs
download audit
watermark
```

Do not implement file upload/download in Phase 08 unless storage architecture is ready.

---

## 7.5 DOC-005 — Version control

| Item | Value |
|---|---|
| Future capability | Create versions, compare changes, restore, branch draft, preserve immutable published versions |
| Current state | Not implemented |
| Phase 08 target | Defer |
| Classification | `DEFERRED_TO_PHASE_27_FULL_DOCUMENT_HUB` |

Future entities:

```text
DocumentVersion
DocumentVersionDiff
DocumentDraftBranch
```

Rules future:

```text
1. Published version immutable.
2. Restore creates new version, does not modify old version.
3. Version comparison stores no sensitive diff in broad logs.
```

---

## 7.6 DOC-006 — Metadata schema

| Item | Value |
|---|---|
| Future capability | Configure fields, tags, owner, classification, application, project, and document-specific metadata |
| Current state | Not confirmed |
| Phase 08 target | Implement lightweight DocumentTypeField foundation if feasible |
| Classification | `MUST_IMPLEMENT_IN_PHASE_08` for type metadata schema foundation; full document metadata deferred |

Phase 08 entity:

```text
DocumentTypeField
```

Required field data types:

```text
TEXT
LONG_TEXT
NUMBER
BOOLEAN
DATE
DATETIME
SELECT
MULTI_SELECT
USER
TEAM
PROJECT
APPLICATION
URL
EMAIL
CURRENCY
PERCENT
```

Required rules:

```text
1. Field key unique within DocumentType.
2. Field key normalized camelCase or snake_case.
3. Required flag supported.
4. Field order supported.
5. Options JSON required for SELECT/MULTI_SELECT.
6. Validation JSON valid if provided.
7. Removing field used by future documents must be blocked; for now no documents exist, so removal allowed with audit.
8. System fields cannot be removed.
```

If DocumentTypeField does not exist and too much work:

```text
MUST_IMPLEMENT_IN_PHASE_08 unless product intentionally keeps only static DocumentType.
```

---

## 7.7 DOC-007 — Document linking

| Item | Value |
|---|---|
| Future capability | Link documents to organization, workspace, application node, requirement, project, work item, test, release, or client |
| Current state | Not implemented |
| Phase 08 target | Defer |
| Classification | `DEFERRED_TO_PHASE_27_FULL_DOCUMENT_HUB` |

Future entities:

```text
DocumentLink
DocumentRelationship
```

Future phases that must return:

```text
Phase 24 Application Registry
Phase 25 Application Structure / Screen Registry
Phase 26 Requirements
Phase 09/10 Project
Phase 29 Client CRM
```

---

## 7.8 DOC-008 — Review and approval

| Item | Value |
|---|---|
| Future capability | Reviewer sequence, parallel review, comments, decisions, due dates |
| Current state | Not implemented |
| Phase 08 target | Defer |
| Classification | `DEFERRED_TO_PHASE_27_FULL_DOCUMENT_HUB` and `DEFERRED_TO_PHASE_34_WORKFLOW_APPROVAL` |

Future entities:

```text
DocumentReview
DocumentReviewStep
DocumentReviewDecision
```

Future events:

```text
DOCUMENT_REVIEW_REQUESTED
DOCUMENT_REVIEW_APPROVED
DOCUMENT_REVIEW_REJECTED
```

---

## 7.9 DOC-009 — Publication

| Item | Value |
|---|---|
| Future capability | Publish approved version to audience or knowledge space |
| Current state | Not implemented |
| Phase 08 target | Defer |
| Classification | `DEFERRED_TO_PHASE_27_FULL_DOCUMENT_HUB` |

Future rules:

```text
1. Only approved version can publish.
2. Published version immutable.
3. Audience must be explicit.
4. Publication emits notification.
5. Publication access respects IAM.
```

---

## 7.10 DOC-010 — Document lifecycle

| Item | Value |
|---|---|
| Future capability | Draft, review, approved, published, superseded, archived, disposed |
| Current state | Document lifecycle not implemented; DocumentType lifecycle likely implemented |
| Phase 08 target | Implement DocumentType lifecycle only; defer Document lifecycle |
| Classification | `MUST_IMPLEMENT_IN_PHASE_08` for DocumentType lifecycle; `DEFERRED_TO_PHASE_27` for Document lifecycle |

DocumentType status:

```text
ACTIVE
INACTIVE
ARCHIVED
```

Future Document status:

```text
DRAFT
IN_REVIEW
APPROVED
PUBLISHED
SUPERSEDED
ARCHIVED
DISPOSED
```

---

## 7.11 DOC-011 — Classification

| Item | Value |
|---|---|
| Future capability | Public, internal, confidential, restricted, org-defined classification |
| Current state | Not confirmed |
| Phase 08 target | Seed classification foundation |
| Classification | `MUST_IMPLEMENT_IN_PHASE_08` as foundation; enforcement deferred |

Recommended classifications:

```text
PUBLIC
INTERNAL
CONFIDENTIAL
RESTRICTED
CUSTOM
```

Phase 08 rules:

```text
1. DocumentType can specify defaultClassification.
2. Classification values are controlled.
3. Full access enforcement by classification is deferred to full document hub/compliance.
```

Deferred:

```text
Classification-based access control — Phase 27/39
Data retention based on classification — Phase 39
AI context restrictions by classification — Phase 21/27/39
```

---

## 7.12 DOC-012 — Sharing

| Item | Value |
|---|---|
| Future capability | Share internally/externally with expiration, watermark, download, forwarding controls |
| Current state | Not implemented |
| Phase 08 target | Defer |
| Classification | `DEFERRED_TO_PHASE_27_FULL_DOCUMENT_HUB` and `DEFERRED_TO_PHASE_30_EXTERNAL_PORTAL` |

Future entities:

```text
DocumentShare
DocumentShareAccess
DocumentWatermarkPolicy
```

Do not implement external share in Phase 08.

---

## 7.13 DOC-013 — Template library

| Item | Value |
|---|---|
| Future capability | Maintain approved content structure, variables, guidance, ownership |
| Current state | Not implemented as document template library |
| Phase 08 target | Defer full template content; optional type-level template pointer only |
| Classification | `DEFERRED_TO_PHASE_27_FULL_DOCUMENT_HUB` |

Phase 08 may include:

```text
DocumentType.defaultTemplateCode nullable
```

But full template versioning belongs to Document Hub.

---

## 7.14 DOC-014 — Document generation

| Item | Value |
|---|---|
| Future capability | Merge structured project/application data into templates |
| Current state | Not implemented |
| Phase 08 target | Defer |
| Classification | `DEFERRED_TO_PHASE_21_AI_ASSISTED_PLANNING` and `DEFERRED_TO_PHASE_27_FULL_DOCUMENT_HUB` |

Future examples:

```text
Generate BRD from requirements
Generate proposal from quote
Generate release note from change log
Generate project handover document
```

---

## 7.15 DOC-015 — Knowledge collections

| Item | Value |
|---|---|
| Future capability | Group documents into handbooks, project spaces, application knowledge bases |
| Current state | Not implemented |
| Phase 08 target | Defer |
| Classification | `DEFERRED_TO_PHASE_27_FULL_DOCUMENT_HUB` |

Future entities:

```text
KnowledgeCollection
KnowledgeCollectionItem
KnowledgeSpace
```

---

## 7.16 DOC-016 — Full-text search

| Item | Value |
|---|---|
| Future capability | Index text, metadata, attachments, OCR content, permitted AI summaries |
| Current state | Not implemented |
| Phase 08 target | Defer |
| Classification | `DEFERRED_TO_PHASE_22_REPORTING_SEARCH` and `DEFERRED_TO_PHASE_41_SEMANTIC_INDEX` |

Future search:

```text
metadata search
full-text search
permission-filtered search
semantic search
OCR indexing
AI summary index
```

Phase 08 can only make DocumentType searchable by basic fields.

---

## 7.17 DOC-017 — Document acknowledgement

| Item | Value |
|---|---|
| Future capability | Require users to read and acknowledge a published version |
| Current state | Not implemented |
| Phase 08 target | Defer |
| Classification | `DEFERRED_TO_PHASE_27_FULL_DOCUMENT_HUB` and `DEFERRED_TO_PHASE_35_ADVANCED_NOTIFICATION` |

Future entities:

```text
DocumentAcknowledgementRequest
DocumentAcknowledgementReceipt
```

---

## 7.18 DOC-018 — Expiry and periodic review

| Item | Value |
|---|---|
| Future capability | Notify owners and enforce review cycles |
| Current state | Not implemented |
| Phase 08 target | Defer; optionally add default review interval to DocumentType |
| Classification | `DEFERRED_TO_PHASE_27_FULL_DOCUMENT_HUB` |

Phase 08 optional field:

```text
defaultReviewCycleDays
```

Full jobs/notifications deferred.

---

## 7.19 DOC-019 — Offline/export package

| Item | Value |
|---|---|
| Future capability | Export controlled set with manifest, versions, traceability references |
| Current state | Not implemented |
| Phase 08 target | Defer |
| Classification | `DEFERRED_TO_PHASE_22_REPORTING_EXPORT` and `DEFERRED_TO_PHASE_38_INTEGRATION_EXPORT` |

Future rules:

```text
1. Export requires explicit permission.
2. Export manifest records versions.
3. Export audit mandatory.
4. Restricted documents may be excluded/watermarked.
```

---

## 7.20 DOC-020 — AI draft and summary

| Item | Value |
|---|---|
| Future capability | Generate proposed content or summaries subject to access and review |
| Current state | AI platform exists but Document Hub/RAG not implemented |
| Phase 08 target | Defer |
| Classification | `DEFERRED_TO_PHASE_21_AI_ASSISTED_PLANNING` and `DEFERRED_TO_PHASE_27_FULL_DOCUMENT_HUB` |

Future rules:

```text
1. AI draft is suggestion, not published document.
2. User must accept/edit draft.
3. AI retrieval respects document IAM.
4. AI output logs promptVersionId/modelDeploymentId.
5. AI-generated summary may need review before publication.
```

---

# 8. Phase 08 implementation scope

## 8.1 Must implement / harden now

```text
DocumentType catalog
DocumentType lifecycle
DocumentType scope
DocumentType default classification
DocumentType field metadata schema if not present
Document type seed data for common types
IAM permissions for knowledge/document type management
Event definitions for document type changes
Activity/audit integration
Tests
Completion gap matrix
```

## 8.2 Do not implement now

```text
Document content repository
File storage
Rich authoring blocks
Document versioning
Document review workflow
Publication workflow
Document sharing
External portal access
Full-text search
Semantic index
RAG/context retrieval
AI document draft/summary
Offline export packages
Acknowledgement cycles
Expiry/review jobs
```

---

# 9. Entity model TO-BE

If current schema differs, agent must map actual fields and document gaps.

---

## 9.1 DocumentType — `knowledge_document_type`

Required fields:

```text
id UUID PK
code VARCHAR(100) NOT NULL
name VARCHAR(255) NOT NULL
description TEXT NULL
scope VARCHAR(50) NOT NULL
organization_id UUID NULL
workspace_id UUID NULL
category VARCHAR(100) NULL
default_classification VARCHAR(50) NULL
default_review_cycle_days INT NULL
default_template_code VARCHAR(150) NULL
metadata_schema_json JSONB NULL optional
built_in BOOLEAN NOT NULL DEFAULT false
status VARCHAR(50) NOT NULL
created_at / created_by
updated_at / updated_by
archived_at / archived_by
version INT
```

Scope:

```text
SYSTEM
ORGANIZATION
WORKSPACE
```

Status:

```text
ACTIVE
INACTIVE
ARCHIVED
```

Constraints:

```text
unique scope + organization_id + workspace_id + code
code uppercase snake case
```

Rules:

```text
1. SYSTEM type has organization_id/workspace_id null.
2. ORGANIZATION type requires organization_id and workspace_id null.
3. WORKSPACE type requires workspace_id and organization_id.
4. Built-in SYSTEM type cannot be deleted.
5. Archived type cannot be updated except restore if supported.
```

---

## 9.2 DocumentTypeField — `knowledge_document_type_field`

Recommended Phase 08 entity.

Required fields:

```text
id UUID PK
document_type_id UUID NOT NULL
field_key VARCHAR(100) NOT NULL
label VARCHAR(255) NOT NULL
description TEXT NULL
data_type VARCHAR(50) NOT NULL
required BOOLEAN NOT NULL DEFAULT false
system_field BOOLEAN NOT NULL DEFAULT false
options_json JSONB NULL
validation_json JSONB NULL
default_value_json JSONB NULL
display_order INT NOT NULL DEFAULT 0
status VARCHAR(50) NOT NULL
created_at / created_by
updated_at / updated_by
version INT
```

Status:

```text
ACTIVE
INACTIVE
ARCHIVED
```

Constraints:

```text
unique document_type_id + field_key
display_order >= 0
```

Data types:

```text
TEXT
LONG_TEXT
NUMBER
BOOLEAN
DATE
DATETIME
SELECT
MULTI_SELECT
USER
TEAM
PROJECT
APPLICATION
URL
EMAIL
CURRENCY
PERCENT
```

Rules:

```text
1. SELECT/MULTI_SELECT requires options_json.
2. USER/TEAM/PROJECT/APPLICATION fields are reference-like and future document values must validate.
3. System field cannot be archived/removed.
4. Required field cannot have invalid default.
5. Field key immutable after creation unless no document instances exist.
```

Since document instances do not exist in Phase 08:

```text
Field delete/archive can be allowed but audited.
```

Future once documents exist:

```text
Removing used fields must be blocked or migrated.
```

---

## 9.3 DocumentClassification — optional foundation

If implemented as table:

```text
knowledge_document_classification
```

Fields:

```text
id UUID PK
code VARCHAR(100) UNIQUE
name VARCHAR(255)
description TEXT
level_rank INT
scope VARCHAR(50)
organization_id UUID NULL
status VARCHAR(50)
created_at / updated_at
```

If not implemented as table:

```text
Use enum values:
PUBLIC
INTERNAL
CONFIDENTIAL
RESTRICTED
```

Recommended Phase 08:

```text
Enum first, table later if org-defined classifications are needed.
```

Org-defined classifications:

```text
DEFERRED_TO_PHASE_39_COMPLIANCE_DYNAMIC_CONFIG
```

---

# 10. API TO-BE list

All APIs use `/api`.

---

## 10.1 DocumentType APIs

```text
POST  /api/knowledge/document-types
GET   /api/knowledge/document-types
GET   /api/knowledge/document-types/{id}
PUT   /api/knowledge/document-types/{id}
PATCH /api/knowledge/document-types/{id}/activate
PATCH /api/knowledge/document-types/{id}/deactivate
PATCH /api/knowledge/document-types/{id}/archive
```

Optional restore:

```text
PATCH /api/knowledge/document-types/{id}/restore
```

Restore can be deferred.

Filters:

```text
scope
organizationId
workspaceId
status
code
category
defaultClassification
builtIn
```

Rules:

```text
1. Create requires KNOWLEDGE_DOCUMENT_TYPE_CREATE.
2. Read requires KNOWLEDGE_DOCUMENT_TYPE_VIEW.
3. Update requires KNOWLEDGE_DOCUMENT_TYPE_UPDATE.
4. Archive/deactivate requires KNOWLEDGE_DOCUMENT_TYPE_MANAGE.
5. Workspace-scoped type requires active workspace and permission.
6. Organization-scoped type requires active org and permission.
```

---

## 10.2 DocumentTypeField APIs

If implemented:

```text
POST   /api/knowledge/document-types/{documentTypeId}/fields
GET    /api/knowledge/document-types/{documentTypeId}/fields
GET    /api/knowledge/document-types/{documentTypeId}/fields/{fieldId}
PUT    /api/knowledge/document-types/{documentTypeId}/fields/{fieldId}
PATCH  /api/knowledge/document-types/{documentTypeId}/fields/{fieldId}/activate
PATCH  /api/knowledge/document-types/{documentTypeId}/fields/{fieldId}/deactivate
PATCH  /api/knowledge/document-types/{documentTypeId}/fields/{fieldId}/archive
PUT    /api/knowledge/document-types/{documentTypeId}/fields/reorder
```

Rules:

```text
1. fieldId must belong to documentTypeId.
2. Field key unique within document type.
3. System fields cannot be archived.
4. Reorder validates all field IDs belong to document type.
```

---

## 10.3 Classification APIs

Usually deferred.

If enum only:

```text
GET /api/knowledge/document-classifications
```

If dynamic:

```text
POST /api/knowledge/document-classifications
GET /api/knowledge/document-classifications
PUT /api/knowledge/document-classifications/{id}
PATCH /api/knowledge/document-classifications/{id}/archive
```

Recommended:

```text
Defer dynamic classification to Phase 39/compliance backlog.
```

---

## 10.4 Full Document APIs — explicitly deferred

Do not implement now unless product chooses full Document Hub.

Deferred APIs:

```text
POST /api/documents
GET /api/documents
GET /api/documents/{id}
PUT /api/documents/{id}
POST /api/documents/{id}/versions
POST /api/documents/{id}/publish
POST /api/documents/{id}/review
POST /api/documents/{id}/share
POST /api/documents/{id}/acknowledge
GET /api/documents/search
POST /api/documents/export-package
POST /api/documents/{id}/ai-summary
```

Completion file must state these are **not implemented in Phase 08**.

---

# 11. Authorization requirements

Phase 08 must add/verify IAM authorities.

## 11.1 Required Phase 08 authorities

```text
KNOWLEDGE_DOCUMENT_TYPE_VIEW
KNOWLEDGE_DOCUMENT_TYPE_CREATE
KNOWLEDGE_DOCUMENT_TYPE_UPDATE
KNOWLEDGE_DOCUMENT_TYPE_ARCHIVE
KNOWLEDGE_DOCUMENT_TYPE_MANAGE

KNOWLEDGE_DOCUMENT_TYPE_FIELD_VIEW
KNOWLEDGE_DOCUMENT_TYPE_FIELD_CREATE
KNOWLEDGE_DOCUMENT_TYPE_FIELD_UPDATE
KNOWLEDGE_DOCUMENT_TYPE_FIELD_ARCHIVE
KNOWLEDGE_DOCUMENT_TYPE_FIELD_MANAGE

KNOWLEDGE_CLASSIFICATION_VIEW
KNOWLEDGE_CLASSIFICATION_MANAGE optional
```

## 11.2 Future document authorities

Seed-only or defer:

```text
DOCUMENT_VIEW
DOCUMENT_CREATE
DOCUMENT_UPDATE
DOCUMENT_ARCHIVE
DOCUMENT_DELETE
DOCUMENT_RESTORE
DOCUMENT_PUBLISH
DOCUMENT_REVIEW
DOCUMENT_APPROVE
DOCUMENT_REJECT
DOCUMENT_SHARE
DOCUMENT_DOWNLOAD
DOCUMENT_EXPORT
DOCUMENT_ACKNOWLEDGE
DOCUMENT_AI_SUMMARIZE
DOCUMENT_AI_DRAFT
DOCUMENT_SEARCH
```

Recommended:

```text
Do not seed active document authorities as if document module exists.
Either seed as future-only with documentation or defer to Phase 27.
```

## 11.3 Resource types

Phase 08 resource type:

```text
DOCUMENT_TYPE
```

Future resource types:

```text
DOCUMENT
DOCUMENT_VERSION
DOCUMENT_FOLDER
DOCUMENT_COLLECTION
DOCUMENT_SHARE
KNOWLEDGE_COLLECTION
KNOWLEDGE_SOURCE
SEARCH_INDEX
SEMANTIC_INDEX
```

---

# 12. Event Registry integration

Source system:

```text
SCOPERY_KNOWLEDGE
```

## 12.1 Required Phase 08 events

```text
DOCUMENT_TYPE_CREATED
DOCUMENT_TYPE_UPDATED
DOCUMENT_TYPE_ACTIVATED
DOCUMENT_TYPE_DEACTIVATED
DOCUMENT_TYPE_ARCHIVED
DOCUMENT_TYPE_FIELD_CREATED
DOCUMENT_TYPE_FIELD_UPDATED
DOCUMENT_TYPE_FIELD_ACTIVATED
DOCUMENT_TYPE_FIELD_DEACTIVATED
DOCUMENT_TYPE_FIELD_ARCHIVED
DOCUMENT_TYPE_FIELDS_REORDERED
DOCUMENT_CLASSIFICATION_LIST_UPDATED optional
```

## 12.2 Future document events

Deferred to Phase 27:

```text
DOCUMENT_CREATED
DOCUMENT_UPDATED
DOCUMENT_ARCHIVED
DOCUMENT_RESTORED
DOCUMENT_VERSION_CREATED
DOCUMENT_VERSION_PUBLISHED
DOCUMENT_REVIEW_REQUESTED
DOCUMENT_REVIEW_APPROVED
DOCUMENT_REVIEW_REJECTED
DOCUMENT_PUBLISHED
DOCUMENT_SUPERSEDED
DOCUMENT_SHARED
DOCUMENT_SHARE_REVOKED
DOCUMENT_DOWNLOADED
DOCUMENT_EXPORTED
DOCUMENT_ACKNOWLEDGED
DOCUMENT_REVIEW_DUE
DOCUMENT_EXPIRED
```

AI/document events deferred:

```text
DOCUMENT_AI_DRAFT_CREATED
DOCUMENT_AI_SUMMARY_CREATED
DOCUMENT_INDEXED_FOR_AI
DOCUMENT_AI_CONTEXT_RETRIEVED
DOCUMENT_AI_CONTEXT_ACCESS_DENIED
```

---

# 13. Notification/email integration

Phase 08 normally does not need user-facing email except admin/config changes.

No required email templates now.

Future templates:

```text
DOCUMENT_REVIEW_REQUESTED_EMAIL
DOCUMENT_APPROVED_EMAIL
DOCUMENT_PUBLISHED_EMAIL
DOCUMENT_ACKNOWLEDGEMENT_REQUIRED_EMAIL
DOCUMENT_REVIEW_DUE_EMAIL
DOCUMENT_SHARE_INVITATION_EMAIL
DOCUMENT_EXPORT_COMPLETED_EMAIL
DOCUMENT_AI_SUMMARY_READY_EMAIL
```

Deferred to:

```text
Phase 27 Full Document Hub
Phase 35 Advanced Notification
Phase 21 AI
Phase 22 Reporting/Export
```

---

# 14. AI Agent integration

Phase 08 must not implement RAG.

But it must define the future AI contract.

## 14.1 Current Phase 08 AI stance

```text
DocumentType metadata can be used later by AI.
No document content exists in Phase 08.
No vector index.
No RAG.
No AI summarization.
No document generation.
```

## 14.2 Future AI integration

Future phases must add:

```text
Document content extraction
Chunking
Embeddings
Vector index
Semantic index
AI context source
Access-filtered retrieval
Prompt context citation
AI summary suggestion
AI draft generation
```

Target phases:

```text
Phase 21 — AI-assisted planning/document generation use cases
Phase 27 — Full Document Hub
Phase 41 — Data Quality, Knowledge Graph, Semantic Index
```

Rules future:

```text
1. AI retrieval respects document IAM.
2. AI must not read restricted documents without permission.
3. Retrieved context IDs logged.
4. Summaries are suggestions until reviewed.
5. Published AI-generated content requires human approval.
```

---

# 15. Platform audit/outbox/idempotency integration

Phase 08 must use Phase 04 patterns.

## 15.1 Activity log actions

```text
DOCUMENT_TYPE_CREATED
DOCUMENT_TYPE_UPDATED
DOCUMENT_TYPE_ACTIVATED
DOCUMENT_TYPE_DEACTIVATED
DOCUMENT_TYPE_ARCHIVED
DOCUMENT_TYPE_FIELD_CREATED
DOCUMENT_TYPE_FIELD_UPDATED
DOCUMENT_TYPE_FIELD_ARCHIVED
DOCUMENT_TYPE_FIELDS_REORDERED
```

## 15.2 Audit-sensitive actions

Audit if:

```text
classification default changes
system/built-in type updated
field required flag changes
field data type changes
field archived
document type archived
```

Audit events:

```text
DOCUMENT_TYPE_CLASSIFICATION_CHANGED
DOCUMENT_TYPE_SCHEMA_CHANGED
DOCUMENT_TYPE_ARCHIVED_AUDIT
```

## 15.3 Idempotency

Recommended for:

```text
POST /api/knowledge/document-types
POST /api/knowledge/document-types/{id}/fields
PUT  /api/knowledge/document-types/{id}/fields/reorder
```

## 15.4 Outbox

Business changes should emit registered events.

If PlatformEventOutbox is not implemented:

```text
Use current application event pattern and document gap.
```

---

# 16. Seed data requirements

## 16.1 DocumentType seeds

Seed common built-in/system document types:

```text
BRD
SRS
TECHNICAL_SPEC
PROJECT_PROPOSAL
USER_GUIDE
MEETING_MINUTES
POLICY
CONTRACT
CHANGE_REQUEST
TEST_PLAN
RELEASE_NOTE
RISK_REGISTER
DECISION_LOG
RETROSPECTIVE_NOTE
```

Seed rules:

```text
1. Idempotent.
2. Built-in = true.
3. Scope = SYSTEM.
4. Status = ACTIVE.
5. Do not overwrite custom user/workspace types.
6. Add missing types.
7. Do not delete old types.
```

## 16.2 Default fields by type

Optional seed:

```text
owner
summary
status
classification
effectiveDate
reviewDueDate
relatedProject
relatedApplication
```

If field schema is implemented, seed basic system fields.

## 16.3 Classification seed

Enum or seed values:

```text
PUBLIC
INTERNAL
CONFIDENTIAL
RESTRICTED
```

Do not add org-defined custom classifications in Phase 08 unless dynamic classification table is implemented.

---

# 17. Business rules master

## 17.1 DocumentType rules

```text
KNO-DT-001 DocumentType code required.
KNO-DT-002 DocumentType code normalized uppercase.
KNO-DT-003 DocumentType code unique within scope.
KNO-DT-004 Name required.
KNO-DT-005 Scope required.
KNO-DT-006 SYSTEM scope cannot have org/workspace id.
KNO-DT-007 ORGANIZATION scope requires organizationId.
KNO-DT-008 WORKSPACE scope requires workspaceId and organizationId.
KNO-DT-009 Organization/workspace must be active.
KNO-DT-010 Built-in system type cannot be deleted.
KNO-DT-011 In-use type cannot be deleted; archive only.
KNO-DT-012 Archived type cannot be updated.
KNO-DT-013 Default classification must be valid.
KNO-DT-014 metadataSchemaJson must be valid JSON if used.
KNO-DT-015 Update emits event and activity log.
```

## 17.2 DocumentTypeField rules

```text
KNO-FLD-001 Field key required.
KNO-FLD-002 Field key unique within document type.
KNO-FLD-003 Label required.
KNO-FLD-004 Data type required and valid.
KNO-FLD-005 SELECT/MULTI_SELECT requires options.
KNO-FLD-006 validationJson valid JSON if provided.
KNO-FLD-007 system field cannot be archived.
KNO-FLD-008 fieldId must belong to documentTypeId path.
KNO-FLD-009 Reorder requires all IDs belong to same type.
KNO-FLD-010 Data type change audited.
KNO-FLD-011 Required flag change audited.
```

## 17.3 Classification rules

```text
KNO-CLS-001 Classification value must be valid.
KNO-CLS-002 Default classification inherited by future documents.
KNO-CLS-003 Restricted/confidential classification must not be ignored by future document/AI phases.
```

## 17.4 Future document warning rules

```text
KNO-FUT-001 DocumentType is not a Document.
KNO-FUT-002 DocumentType catalog does not imply document storage exists.
KNO-FUT-003 Document classification foundation does not imply full enforcement exists.
KNO-FUT-004 AI/RAG cannot be claimed until Document content and access-filtered retrieval exist.
```

---

# 18. Error catalog requirements

Exact names follow project convention, but these concepts must exist.

```text
KNOWLEDGE_DOCUMENT_TYPE_NOT_FOUND
KNOWLEDGE_DOCUMENT_TYPE_CODE_ALREADY_EXISTS
KNOWLEDGE_DOCUMENT_TYPE_INVALID_CODE
KNOWLEDGE_DOCUMENT_TYPE_INVALID_SCOPE
KNOWLEDGE_DOCUMENT_TYPE_ARCHIVED
KNOWLEDGE_DOCUMENT_TYPE_BUILT_IN_CANNOT_DELETE
KNOWLEDGE_DOCUMENT_TYPE_IN_USE_CANNOT_DELETE
KNOWLEDGE_DOCUMENT_TYPE_INVALID_CLASSIFICATION
KNOWLEDGE_DOCUMENT_TYPE_INVALID_METADATA_SCHEMA

KNOWLEDGE_DOCUMENT_TYPE_FIELD_NOT_FOUND
KNOWLEDGE_DOCUMENT_TYPE_FIELD_CODE_ALREADY_EXISTS
KNOWLEDGE_DOCUMENT_TYPE_FIELD_INVALID_KEY
KNOWLEDGE_DOCUMENT_TYPE_FIELD_INVALID_DATA_TYPE
KNOWLEDGE_DOCUMENT_TYPE_FIELD_OPTIONS_REQUIRED
KNOWLEDGE_DOCUMENT_TYPE_FIELD_SYSTEM_CANNOT_ARCHIVE
KNOWLEDGE_DOCUMENT_TYPE_FIELD_PATH_MISMATCH
KNOWLEDGE_DOCUMENT_TYPE_FIELD_REORDER_INVALID

KNOWLEDGE_ACCESS_DENIED
```

Future errors deferred:

```text
DOCUMENT_NOT_FOUND
DOCUMENT_VERSION_NOT_FOUND
DOCUMENT_FILE_SCAN_FAILED
DOCUMENT_REVIEW_NOT_PENDING
DOCUMENT_SHARE_EXPIRED
DOCUMENT_AI_CONTEXT_ACCESS_DENIED
```

---

# 19. Required tests

Phase 08 is incomplete without tests.

---

## 19.1 DocumentType tests

```text
createDocumentType_validSystem_success
createDocumentType_duplicateCodeSameScope_conflict
createDocumentType_sameCodeDifferentWorkspace_allowed
createDocumentType_invalidCode_rejected
createDocumentType_missingName_rejected
createDocumentType_invalidScope_rejected
createDocumentType_workspaceScopeWithoutWorkspace_rejected
createDocumentType_inactiveWorkspace_rejected
createDocumentType_defaultClassificationInvalid_rejected
createDocumentType_builtInSystem_success
updateDocumentType_valid_success
updateDocumentType_archived_rejected
archiveDocumentType_builtin_rejectedOrSoftArchiveAccordingToPolicy
archiveDocumentType_inUse_rejected
archiveDocumentType_valid_success
activateDocumentType_valid_success
deactivateDocumentType_valid_success
documentTypeEvents_emitted
documentTypeActivity_logged
```

## 19.2 DocumentTypeField tests

```text
createField_valid_success
createField_duplicateKey_conflict
createField_invalidKey_rejected
createField_missingLabel_rejected
createField_invalidDataType_rejected
createField_selectWithoutOptions_rejected
createField_invalidValidationJson_rejected
updateField_valid_success
updateField_dataTypeChange_audited
archiveSystemField_rejected
archiveField_valid_success
fieldPathMismatch_rejected
reorderFields_valid_success
reorderFields_foreignField_rejected
```

If DocumentTypeField deferred:

```text
Mark these tests deferred and explain why.
```

## 19.3 Classification tests

```text
classificationList_returnsDefaults
createDocumentType_withRestrictedClassification_success
createDocumentType_withUnknownClassification_rejected
```

## 19.4 Authorization tests

```text
createDocumentType_withoutPermission_forbidden
updateDocumentType_withoutPermission_forbidden
archiveDocumentType_withoutPermission_forbidden
createWorkspaceDocumentType_withoutWorkspaceAccess_forbidden
listDocumentTypes_onlyAccessibleScope
```

## 19.5 Seeder tests

```text
documentTypeSeeder_firstRun_createsBuiltIns
documentTypeSeeder_secondRun_noDuplicates
documentTypeSeeder_doesNotOverwriteCustomTypes
classificationSeeder_firstRun_createsDefaults
classificationSeeder_secondRun_noDuplicates
knowledgeEventSeeder_firstRun_createsDefinitions
knowledgeEventSeeder_secondRun_noDuplicates
```

## 19.6 Anti-future claim tests / checks

Completion file must explicitly state:

```text
documentsCrud_notImplemented
fileUpload_notImplemented
documentVersioning_notImplemented
documentReview_notImplemented
documentSearch_notImplemented
documentRag_notImplemented
aiSummary_notImplemented
```

This is not a unit test but a completion requirement.

---

# 20. Manual verification checklist

Completion file must include:

```text
1. Create SYSTEM document type.
2. Create WORKSPACE document type.
3. Verify duplicate code in same scope rejected.
4. Verify same code in different workspace allowed if intended.
5. Add metadata field.
6. Try duplicate field key and confirm conflict.
7. Try SELECT field without options and confirm validation error.
8. Update required flag and confirm audit.
9. Archive document type.
10. Confirm archived type cannot update.
11. Rerun built-in type seeder and confirm no duplicates.
12. Confirm EventRegistry has DOCUMENT_TYPE_* events.
13. Confirm permissions block unauthorized user.
14. Confirm there is no fake document CRUD implemented unless explicitly intended.
```

---

# 21. Acceptance criteria

Phase 08 is accepted only if:

```text
1. Current Knowledge module is classified against TO-BE.
2. DocumentType catalog is implemented or verified.
3. DocumentType lifecycle rules are implemented/tested.
4. DocumentType scope rules are implemented/tested.
5. DocumentType default classification implemented/tested.
6. DocumentTypeField metadata schema implemented/tested or explicitly deferred with reason.
7. Built-in document type seeder is idempotent.
8. Knowledge event definitions are seeded idempotently.
9. IAM authorities protect document type APIs.
10. Activity/audit/outbox integration follows Phase 04.
11. Full Document Hub capabilities DOC-001/DOC-003–DOC-020 are not falsely claimed.
12. Deferred items are mapped to Phase 27 / Phase 21 / Phase 22 / Phase 39 / Phase 41 as appropriate.
13. mvn compile passes.
14. mvn test passes.
15. Completion file exists and includes gap matrix.
```

Agent must not mark complete if:

```text
mvn test fails
duplicate document type codes are allowed in same scope
archived document type can be updated
built-in/system type is hard-deleted
unknown classification is accepted
field duplicate keys are allowed
future document/RAG/search features are claimed implemented without real code
deferred gap matrix is missing
```

---

# 22. Required phase completion file

Agent must create:

```text
docs/phase-complete/PHASE_08_KNOWLEDGE_DOCUMENT_TYPE_TO_BE_COMPLETE.md
```

Required sections:

```text
# Phase 08 — Knowledge / Document Type Catalog TO-BE Complete

## 1. Summary
## 2. Source Inputs Reviewed
## 3. Current vs TO-BE Classification Matrix
## 4. Implemented in Current BE
## 5. Implemented / Hardened in This Phase
## 6. Seed-only Items Added
## 7. Deferred Items and Target Phase
## 8. DOC-001 to DOC-020 Gap Matrix
## 9. Entity Mapping
## 10. API Changes
## 11. DocumentType Rules
## 12. DocumentTypeField Rules
## 13. Classification Foundation
## 14. IAM Authorization Matrix
## 15. Event Registry Seeder Matrix
## 16. Notification/AI Integration Notes
## 17. Activity / Audit / Outbox Notes
## 18. Built-in Seed Data
## 19. Tests Added
## 20. Commands Run
## 21. Test Results
## 22. Manual Verification
## 23. Assumptions
## 24. Deviations From Prompt
## 25. Known Risks
## 26. Future Phases That Must Return to Knowledge/Document Hub
```

---

# 23. Future phases that must return to Knowledge / Document Hub

## 23.1 Phase 09/10 — Project Core

Must link future documents to:

```text
Project
Phase
WBS node
Task
Task deliverable
```

If no Document entity exists yet:

```text
Only DocumentType reference/placeholder allowed.
```

## 23.2 Phase 11 — Project Templates

May use DocumentType for template deliverables.

Future:

```text
ProjectTemplate default documents
Phase deliverable document type
```

## 23.3 Phase 17 — Finance

Finance documents:

```text
budget justification
approved financial scenario attachment
margin approval note
```

Full file support deferred to Phase 27.

## 23.4 Phase 18 — Quote

Quote/proposal documents require:

```text
proposal document type
quote export package
commercial approval document
```

Full generation deferred to Phase 27/21.

## 23.5 Phase 19 — Baseline / Change Request

Baseline/change packages may include documents.

Requires:

```text
immutable references to published document versions
```

Deferred until DocumentVersion exists.

## 23.6 Phase 20 — Project Events / Notifications

Must add notification templates for:

```text
DOCUMENT_REVIEW_REQUESTED
DOCUMENT_PUBLISHED
DOCUMENT_ACKNOWLEDGEMENT_REQUIRED
```

Only when full Document Hub exists.

## 23.7 Phase 21 — AI-assisted Project Planning

Must add:

```text
AI document draft
AI summary
AI proposal generation
AI retrieval with permission checks
```

Requires Phase 27 document content or Phase 41 semantic index.

## 23.8 Phase 22 — Reporting / Export

Must add document/report export packages if full document versioning exists.

## 23.9 Phase 27 — Full Document Hub

Must implement:

```text
Document creation
File management
Rich authoring
Version control
Review/approval
Publication
Lifecycle
Sharing
Template library
Knowledge collections
Document acknowledgement
Expiry/review cycle
```

## 23.10 Phase 39 — Audit / Compliance / Privacy / Retention

Must implement:

```text
classification enforcement
retention policy
legal hold
disposal
download/export audit
privacy masking
```

## 23.11 Phase 41 — Data Quality / Knowledge Graph / Semantic Index

Must implement:

```text
full-text search
semantic search
knowledge graph links
embedding index
AI-safe retrieval
OCR/attachment indexing
```

---

# 24. Agent anti-bịa rules

The agent must not:

```text
1. Treat current Knowledge module 1 function as full Document Hub.
2. Claim document creation exists unless Document entity/API/storage exists.
3. Claim file upload exists unless storage, scan, preview/download rules exist.
4. Claim version control exists unless DocumentVersion and immutability rules exist.
5. Claim RAG/search exists unless indexed content and permission-filtered retrieval exist.
6. Claim AI summary/draft exists unless AI execution + document content + review flow exist.
7. Implement fake document CRUD with no version/content/storage.
8. Hard-delete built-in/system document types.
9. Allow duplicate document type code in same scope.
10. Ignore workspace/org access for scoped document types.
11. Forget DOC-001 to DOC-020 gap matrix.
12. Hide deferred Document Hub features.
```

---

# 25. Prompt to give coding agent

```text
You are implementing Phase 08 — TO-BE Knowledge / Document Type Catalog Foundation & Full Document Hub Gap Contract.

This is not an as-is documentation task.

Before coding, read:
- CLAUDE.md / CLAUDE.ms
- Coding_convention.md
- Phase 00 master roadmap
- Phase 01 API/security baseline
- Phase 02 IAM TO-BE spec
- Phase 03 Workspace TO-BE spec
- Phase 04 Platform Audit/Outbox/Idempotency spec
- Phase 05 Event Registry TO-BE spec
- Phase 06 Notification TO-BE spec
- Phase 07 AI Agent TO-BE spec
- Current BE feature/entity/business-rule inventory
- Dynamic Work OS Full Module Feature Catalog
- Existing knowledge module code, migrations, seeders, tests

Your task:
1. Compare current Knowledge module against this TO-BE Phase 08 spec.
2. Classify every DOC-001 to DOC-020 capability as CURRENTLY_IMPLEMENTED, MUST_IMPLEMENT_IN_PHASE_08, SEED_ONLY_IN_PHASE_08, DEFERRED_TO_PHASE_XX, DEFERRED_TO_POST_23_BACKLOG, or NOT_IN_SCOPE_FOR_BACKEND_NOW.
3. Implement only Phase 08 required items.
4. Harden DocumentType catalog.
5. Implement or explicitly defer DocumentTypeField metadata schema.
6. Add default classification foundation.
7. Add/verify IAM permissions for document type management.
8. Add/verify EventRegistry definitions for document type changes.
9. Add/verify built-in document type seeders.
10. Add tests listed in the Phase 08 spec.
11. Run mvn compile and mvn test.
12. Create docs/phase-complete/PHASE_08_KNOWLEDGE_DOCUMENT_TYPE_TO_BE_COMPLETE.md with full DOC-001 to DOC-020 gap matrix.

Do not claim full Document Hub, file management, versioning, search, RAG, AI summary, or sharing exists unless real entities/APIs/tests exist.
```

---

# 26. Quick tracking matrix

| Capability | Current backend | Phase 08 action | Later phase |
|---|---|---|---|
| Basic DocumentType | Present/likely | Harden/test | — |
| Dynamic DocumentType | Partial | Must implement/harden | — |
| DocumentTypeField metadata | Missing/unknown | Implement or explicit defer | Phase 27/39 if deferred |
| Classification foundation | Missing/unknown | Must implement enum/foundation | Phase 39 for enforcement |
| Document creation | Missing | Defer | Phase 27 |
| Rich authoring | Missing | Defer | Phase 27 |
| File management | Missing | Defer | Phase 27 |
| Version control | Missing | Defer | Phase 27 |
| Metadata values | Missing | Defer | Phase 27 |
| Document linking | Missing | Defer | Phase 27 + related modules |
| Review/approval | Missing | Defer | Phase 27/34 |
| Publication | Missing | Defer | Phase 27 |
| Document lifecycle | Missing | Defer | Phase 27 |
| Sharing | Missing | Defer | Phase 27/30 |
| Template library | Missing | Defer | Phase 27 |
| Document generation | Missing | Defer | Phase 21/27 |
| Knowledge collections | Missing | Defer | Phase 27 |
| Full-text search | Missing | Defer | Phase 22/41 |
| Acknowledgement | Missing | Defer | Phase 27/35 |
| Expiry/review | Missing | Defer | Phase 27/35 |
| Offline/export package | Missing | Defer | Phase 22/38 |
| AI draft/summary | Missing | Defer | Phase 21/27/41 |
| RAG/semantic index | Missing | Defer | Phase 41 |

---

# 27. Final principle

Phase 08 is not complete when "DocumentType can be created."

Phase 08 is complete when Scopery has a clean document foundation:

```text
document types are governed
metadata schema is clear
classification is seeded
IAM is ready
events are ready
tests are clear
full Document Hub gaps are explicit
AI/RAG/search are not falsely claimed
```

A document type is not a document.

A document catalog is not a document repository.

A future AI summary requires actual document content, permission-filtered retrieval, and human review.
