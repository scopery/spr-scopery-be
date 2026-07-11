# PHASE 27 — TO-BE Document Hub, File Versioning, Document Templates, Generated Documents, Evidence Repository & Controlled Sharing

> Project: Scopery Backend  
> Phase: 27  
> Document type: TO-BE implementation-grade specification  
> Status: Planning / Gap-driven implementation spec  
> Roadmap group: Post-core Dynamic Work OS expansion  
> Depends on: Phase 00 — Master Backend Roadmap, Phase 01 — API Path / Security Baseline, Phase 02 — IAM Core, Phase 03 — Workspace / Organization / Team, Phase 04 — Platform Audit / Outbox / Idempotency, Phase 05 — Event Registry, Phase 06 — Notification Engine, Phase 08 — Knowledge / Document Type Catalog, Phase 09 — Project Core / WBS / Task, Phase 10 — Project Authorization, Phase 18 — Quote / Commercial Proposal, Phase 19 — Baseline / Change Request, Phase 20 — Project Events / Notifications, Phase 22 — Reporting / Dashboard / Export, Phase 23 — Core Hardening, Phase 24 — Scope / Deliverable / Acceptance, Phase 25 — RAID / Decision Management, Phase 26 — Quality / Test / Release  
> API base: `/api`  
> Primary module: `modules/document` or `modules/project/document` depending on repository architecture  
> Related modules: `knowledge`, `project`, `scope`, `deliverable`, `acceptance`, `quality`, `release`, `quote`, `reporting`, `raid`, `decision`, `notification`, `iam`, `eventregistry`, future `workflow`, `external-portal`, `semantic-index`, `e-signature`, `retention`  
> Important rule: Phase 27 implements controlled document storage, versioning, metadata, links, templates, and generated document jobs. It does **not** implement full semantic RAG, knowledge graph, external client portal, legal e-signature, OCR pipeline, or unrestricted public file sharing.

---

# 0. Purpose

Phase 27 turns documents from simple references into a governed project document system.

Earlier phases introduced document needs:

```text
Phase 08:
- Document Type Catalog / Knowledge foundation

Phase 18:
- Quote / proposal data

Phase 22:
- Report / export data

Phase 24:
- Acceptance evidence

Phase 25:
- Decision/risk evidence and governance logs

Phase 26:
- Test evidence, release notes, deployment records
```

Phase 27 answers:

```text
Where are project documents stored?
How are files versioned?
Which document type does a file belong to?
Which project artifact is this document linked to?
Which file version is current?
Who can view/download/upload/approve/archive?
How are generated proposal/report/acceptance documents created?
How is evidence stored for acceptance/test/release?
How do exports become controlled documents?
How do we prevent unsafe public sharing?
How do documents appear in reporting and audit?
```

Phase 27 is a **document governance and generated-document foundation**.

---

# 1. Source inputs

Before coding Phase 27, the agent must read:

```text
1. Current backend codebase
2. Phase 08 Knowledge / Document Type Catalog implementation
3. Phase 18 Quote / Commercial Proposal implementation
4. Phase 22 Reporting / Dashboard / Export implementation
5. Phase 24 Scope / Deliverable / Acceptance implementation
6. Phase 25 RAID / Decision implementation
7. Phase 26 Quality / Test / Release implementation
8. Phase 23 hardening completion file
9. Current file storage/config if any
10. Current IAM seeders
11. Current EventDefinition seeders
12. Current Notification template/rule seeders
13. Existing attachment/document/file upload code if any
14. Existing report export storage code if any
```

The coding agent must inspect actual code and storage capability.

---

# 2. Current expected backend state

After Phase 26, the backend likely has many references that need proper documents:

```text
Quote proposal text
Report export jobs
Acceptance evidence references
Test evidence references
Defect evidence references
Release notes
Rollback plans
Decision rationale
RAID evidence
```

Likely missing:

```text
DocumentFolder
Document
DocumentVersion
DocumentBlob/FileObject reference
DocumentLink
DocumentMetadata
DocumentTemplate
DocumentTemplateVersion
GeneratedDocumentJob
DocumentShare
DocumentApproval foundation
DocumentAccessLog
DocumentRetention fields
Controlled download tokens
File scan status
Document reports
```

Phase 27 implements these foundations.

---

# 3. Target statement

Phase 27 must deliver:

```text
1. Workspace/project document hub.
2. Folder hierarchy.
3. Document metadata and status lifecycle.
4. DocumentVersion with file/object reference and checksum.
5. Current version marker and immutable historical versions.
6. Link documents to project artifacts.
7. Document type validation from Phase 08 catalog.
8. Acceptance/test/release evidence as document links.
9. DocumentTemplate and TemplateVersion foundation.
10. GeneratedDocumentJob for proposal/report/acceptance/release documents.
11. Controlled download/access logging.
12. Internal share/link foundation with IAM.
13. Events/notifications for document created/versioned/approved/archived/shared.
14. Reporting/dashboard extensions.
15. Tests and completion report.
```

---

# 4. Boundary decisions

## 4.1 Document Hub is not semantic RAG

Phase 27 can store metadata and file references.

It does not implement full semantic retrieval, embeddings, vector search, or knowledge graph.

Those belong to Phase 41.

## 4.2 Document storage is not public file sharing

Files must be accessed through authorized download APIs.

Rules:

```text
1. Storage keys must not be guessable.
2. Raw storage URLs should not be exposed unless short-lived signed URL policy exists.
3. Download must be authorized and audited.
4. Public anonymous sharing is not Phase 27.
```

## 4.3 Generated documents are controlled outputs

A generated document from quote/report/acceptance is a document version or file.

Generation must record:

```text
source data
template version
actor
timestamp
masking/access summary
output file reference
```

## 4.4 Document approval is not workflow engine

Phase 27 can implement simple review/approve status.

Full multi-step approval routing is Phase 34.

## 4.5 E-signature is deferred

Legal signature/e-signature provider integration is not Phase 27.

---

# 5. Classification labels

Use these labels in completion file:

| Label | Meaning |
|---|---|
| `CURRENTLY_IMPLEMENTED` | Existing backend already supports it with tests. |
| `PARTIALLY_IMPLEMENTED` | Some support exists but gaps remain. |
| `MUST_IMPLEMENT_IN_PHASE_27` | Required now. |
| `SEED_ONLY_IN_PHASE_27` | Seed definitions/events/permissions/templates only. |
| `DEFERRED_TO_PHASE_XX` | Later phase returns. |
| `DEFERRED_TO_POST_23_BACKLOG` | Dynamic Work OS expansion backlog. |
| `NOT_IN_SCOPE_FOR_PHASE_27` | Explicitly not in this phase. |

---

# 6. Required capabilities

---

## 6.1 DOC-001 — DocumentFolder

Classification: `MUST_IMPLEMENT_IN_PHASE_27`

Purpose:

```text
Organize project/workspace documents.
```

Rules:

```text
1. Folder belongs to workspace and optionally project.
2. Folder can be nested.
3. Folder name required.
4. Folder path unique under same parent.
5. Folder archive does not delete documents by default.
6. Moving folder must not break document links.
7. Folder access follows workspace/project permissions.
```

Folder types:

```text
WORKSPACE
PROJECT
SYSTEM
GENERATED
EVIDENCE
ARCHIVE
```

---

## 6.2 DOC-002 — Document

Classification: `MUST_IMPLEMENT_IN_PHASE_27`

Purpose:

```text
Represent a logical document with metadata and versions.
```

Rules:

```text
1. Document belongs to workspace and optionally project.
2. Document can belong to folder.
3. Document type must reference active DocumentType if Phase 08 exists.
4. Document title required.
5. Document status controls lifecycle.
6. Document has one current version.
7. Document metadata can be updated without changing file version.
8. Archived document remains auditable.
```

Document statuses:

```text
DRAFT
ACTIVE
IN_REVIEW
APPROVED
REJECTED
ARCHIVED
DELETED_SOFT
```

Do not hard-delete by default unless retention policy allows.

---

## 6.3 DOC-003 — DocumentVersion

Classification: `MUST_IMPLEMENT_IN_PHASE_27`

Purpose:

```text
Preserve immutable file/content versions.
```

Rules:

```text
1. DocumentVersion belongs to Document.
2. Version number unique per document.
3. File/object reference required unless text-only generated document policy exists.
4. Version stores filename, mime type, size, checksum.
5. Current version marker unique.
6. Approved version immutable.
7. Historical versions never overwritten.
8. Uploading new file creates new version.
9. Download returns specific version or current version.
```

Supported source types:

```text
UPLOAD
GENERATED
EXTERNAL_REFERENCE
IMPORTED
REPORT_EXPORT
ACCEPTANCE_EVIDENCE
TEST_EVIDENCE
RELEASE_DOCUMENT
```

---

## 6.4 DOC-004 — File object / storage reference

Classification: `MUST_IMPLEMENT_IN_PHASE_27`

Purpose:

```text
Store safe reference to file storage.
```

Rules:

```text
1. Storage key must be opaque.
2. Original filename stored separately.
3. Mime type validated.
4. File size limit enforced.
5. Checksum stored.
6. Upload actor recorded.
7. Scan status stored if scanner exists.
8. Do not expose raw permanent storage URLs.
```

Scan status:

```text
NOT_SCANNED
PENDING
PASSED
FAILED
UNSUPPORTED
```

If no malware scanner exists:

```text
Mark scanner integration as deferred.
Do not claim virus scanning.
```

---

## 6.5 DOC-005 — DocumentLink

Classification: `MUST_IMPLEMENT_IN_PHASE_27`

Purpose:

```text
Link documents to project artifacts.
```

Target types:

```text
PROJECT
PROJECT_PHASE
WBS_NODE
TASK
DELIVERABLE
ACCEPTANCE_CRITERIA
ACCEPTANCE_EVIDENCE
TEST_CASE
TEST_RUN
TEST_CASE_RESULT
DEFECT
RELEASE_PACKAGE
DEPLOYMENT_RECORD
ROLLBACK_PLAN
RAID_ITEM
DECISION
CHANGE_REQUEST
BASELINE
QUOTE_VERSION
REPORT_RUN
REPORT_EXPORT
AI_SUGGESTION
```

Rules:

```text
1. Target must belong to same project/workspace.
2. Link type required.
3. Link does not grant document access automatically unless policy says inherited access.
4. Duplicate active links prevented.
5. Sensitive linked document access checked at download time.
```

Link types:

```text
EVIDENCE
ATTACHMENT
SOURCE
OUTPUT
REFERENCE
APPROVAL_DOCUMENT
RELEASE_NOTE
TEST_EVIDENCE
QUOTE_PROPOSAL
REPORT_EXPORT
```

---

## 6.6 DOC-006 — Document metadata

Classification: `MUST_IMPLEMENT_IN_PHASE_27`

Purpose:

```text
Allow structured searchable metadata without full semantic search.
```

Fields/examples:

```text
documentNumber
author
owner
clientVisible
confidentialityLevel
effectiveDate
expiryDate
language
tags
customMetadataJson
```

Confidentiality levels:

```text
PUBLIC_INTERNAL
INTERNAL
CONFIDENTIAL
RESTRICTED
```

Rules:

```text
1. Confidentiality affects access/masking.
2. Metadata updates audited.
3. Tags/custom metadata validated by size/type.
4. Metadata search is keyword/filter based, not semantic RAG.
```

---

## 6.7 DOC-007 — Controlled download

Classification: `MUST_IMPLEMENT_IN_PHASE_27`

Purpose:

```text
Download document/file safely.
```

Rules:

```text
1. User must have document view/download permission.
2. Project artifact access checked.
3. Version access checked.
4. Download audited.
5. Optional signed URL must be short-lived and actor-bound if supported.
6. Archived documents require permission/policy.
7. Failed scan files blocked if scanner exists and status FAILED.
```

---

## 6.8 DOC-008 — Document access log

Classification: `MUST_IMPLEMENT_IN_PHASE_27`

Purpose:

```text
Record view/download/share events for audit.
```

Logged actions:

```text
VIEW_METADATA
DOWNLOAD
PREVIEW
SHARE_CREATED
SHARE_REVOKED
VERSION_CREATED
VERSION_APPROVED
ARCHIVED
```

Rules:

```text
1. Download always logged.
2. Sensitive/restricted document views logged.
3. Access log immutable.
4. Logs must not store raw file content.
```

---

## 6.9 DOC-009 — Document share foundation

Classification: `MUST_IMPLEMENT_IN_PHASE_27`

Purpose:

```text
Share documents internally with controlled access.
```

Share targets:

```text
USER
WORKSPACE_ROLE
PROJECT_ROLE
TEAM
EXTERNAL_EMAIL future/deferred
```

Rules:

```text
1. Internal share only in Phase 27.
2. External anonymous/public share deferred.
3. Share can be view-only or download-enabled.
4. Share can expire.
5. Share does not bypass project/workspace access unless explicit grant model exists.
6. Share revoked prevents future access.
```

External sharing:

```text
DEFERRED_TO_PHASE_30_CLIENT_PORTAL or Phase 38 privacy/compliance.
```

---

## 6.10 TPL-001 — DocumentTemplate

Classification: `MUST_IMPLEMENT_IN_PHASE_27`

Purpose:

```text
Define reusable templates for generated documents.
```

Template categories:

```text
QUOTE_PROPOSAL
PROJECT_REPORT
ACCEPTANCE_REPORT
TEST_REPORT
RELEASE_NOTES
CHANGE_REQUEST_DOCUMENT
DECISION_RECORD
RAID_REPORT
CUSTOM
```

Rules:

```text
1. Template belongs to workspace or system.
2. Template has versions.
3. Published template version immutable.
4. Template variables must be declared.
5. Template access controlled.
6. Template can be active/inactive.
```

---

## 6.11 TPL-002 — DocumentTemplateVersion

Classification: `MUST_IMPLEMENT_IN_PHASE_27`

Purpose:

```text
Preserve immutable template content/version.
```

Rules:

```text
1. Version number unique per template.
2. Draft version editable.
3. Published version immutable.
4. Variables schema stored.
5. Renderer type stored.
6. Template content should not contain secrets.
```

Renderer types:

```text
MARKDOWN
HTML
DOCX_TEMPLATE future
PDF_TEMPLATE future
TEXT
```

Recommended Phase 27:

```text
MARKDOWN/HTML foundation.
DOCX/PDF rendering only if renderer exists.
```

---

## 6.12 GEN-001 — GeneratedDocumentJob

Classification: `MUST_IMPLEMENT_IN_PHASE_27`

Purpose:

```text
Generate controlled documents from source business data and template.
```

Generation sources:

```text
QUOTE_VERSION
REPORT_RUN
REPORT_SNAPSHOT
DELIVERABLE_ACCEPTANCE
TEST_RUN
RELEASE_PACKAGE
CHANGE_REQUEST
DECISION
RAID_SUMMARY
```

Rules:

```text
1. Actor must have source data permission.
2. Source data must be masked according to actor permissions.
3. Template version must be published.
4. Job stores source refs, template version, actor, and masking summary.
5. Output creates Document and DocumentVersion.
6. Job failure captured.
7. Generation is idempotent if idempotency key provided.
```

Supported outputs:

```text
MARKDOWN
HTML
PDF optional
DOCX optional
```

Do not claim PDF/DOCX unless rendering exists.

---

## 6.13 GEN-002 — Quote proposal generation

Classification: `MUST_IMPLEMENT_IN_PHASE_27` if Quote module exists.

Purpose:

```text
Generate proposal document from QuoteVersion, QuoteLines, QuoteTerms, and project summary.
```

Rules:

```text
1. QuoteVersion must belong to project.
2. Actor needs quote view/export permission.
3. Quote margin/cost fields excluded unless template/internal export and actor authorized.
4. Generated document linked to QuoteVersion.
5. Does not send proposal to client.
6. Does not create contract.
```

---

## 6.14 GEN-003 — Report document generation

Classification: `MUST_IMPLEMENT_IN_PHASE_27` if Reporting module exists.

Purpose:

```text
Convert ReportSnapshot/ReportRun to controlled document.
```

Rules:

```text
1. Uses already-masked report snapshot.
2. Does not regenerate unmasked data.
3. Generated document linked to ReportRun/ReportSnapshot.
4. Export/download audited.
```

---

## 6.15 GEN-004 — Acceptance report generation

Classification: `MUST_IMPLEMENT_IN_PHASE_27` if Phase 24 exists.

Purpose:

```text
Generate deliverable acceptance report with criteria/evidence/outcome.
```

Rules:

```text
1. Acceptance data must belong to project.
2. Evidence documents linked.
3. External signature/e-signature deferred.
4. Does not create invoice.
```

---

## 6.16 GEN-005 — Test/release document generation

Classification: `MUST_IMPLEMENT_IN_PHASE_27` if Phase 26 exists.

Generated docs:

```text
test execution report
defect summary
release notes
release readiness report
deployment summary
rollback plan export
```

Rules:

```text
1. Uses Phase 26 data.
2. Sensitive deployment details masked.
3. Does not execute deployment.
```

---

## 6.17 DOC-010 — Document approval foundation

Classification: `MUST_IMPLEMENT_IN_PHASE_27` simple; full workflow deferred.

Rules:

```text
1. Document can be submitted for review.
2. Reviewer can approve/reject.
3. Rejection requires reason.
4. Approved document/version immutable.
5. Multi-step workflow deferred to Phase 34.
```

Status transitions:

```text
DRAFT → IN_REVIEW → APPROVED
IN_REVIEW → REJECTED
APPROVED → ARCHIVED
```

---

## 6.18 DOC-011 — Evidence repository integration

Classification: `MUST_IMPLEMENT_IN_PHASE_27`

Purpose:

```text
Replace loose evidence references from Phase 24/26 with controlled DocumentLinks.
```

Rules:

```text
1. AcceptanceEvidence can reference DocumentVersion.
2. TestCaseResult can reference DocumentVersion.
3. Defect can reference DocumentVersion.
4. ReleasePackage can reference DocumentVersion.
5. Links do not bypass access.
```

---

## 6.19 RPT-001 — Document reporting

Classification: `MUST_IMPLEMENT_IN_PHASE_27`

Reports:

```text
document inventory
document by type
document by artifact
document version history
generated documents
document access/download log
evidence coverage report
documents pending review
expired documents
```

Dashboard KPIs:

```text
documentCount
approvedDocumentCount
pendingReviewCount
generatedDocumentCount
evidenceDocumentCount
downloadsThisPeriod
expiredDocumentCount
```

---

# 7. Entity model TO-BE

---

## 7.1 DocumentFolder — `document_folder`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
project_id UUID NULL
parent_folder_id UUID NULL
folder_type VARCHAR(50) NOT NULL
name VARCHAR(255) NOT NULL
description TEXT NULL
path VARCHAR(1000) NULL
sort_order INT NULL
status VARCHAR(50) NOT NULL
created_at / created_by
updated_at / updated_by
archived_at / archived_by
version INT
```

Status:

```text
ACTIVE
ARCHIVED
```

---

## 7.2 Document — `document_document`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
project_id UUID NULL
folder_id UUID NULL
document_type_id UUID NULL
code VARCHAR(100) NULL
title VARCHAR(255) NOT NULL
description TEXT NULL
status VARCHAR(50) NOT NULL
confidentiality_level VARCHAR(50) NOT NULL
owner_user_id UUID NULL
client_visible BOOLEAN NOT NULL DEFAULT false
current_version_id UUID NULL
tags_json JSONB NULL
custom_metadata_json JSONB NULL
effective_date DATE NULL
expiry_date DATE NULL
approved_at TIMESTAMP NULL
approved_by UUID NULL
created_at / created_by
updated_at / updated_by
archived_at / archived_by
trace_id VARCHAR(100) NULL
version INT
```

---

## 7.3 DocumentVersion — `document_version`

Fields:

```text
id UUID PK
document_id UUID NOT NULL
workspace_id UUID NOT NULL
project_id UUID NULL
version_number INT NOT NULL
source_type VARCHAR(50) NOT NULL
status VARCHAR(50) NOT NULL
original_file_name VARCHAR(500) NULL
stored_file_name VARCHAR(500) NULL
mime_type VARCHAR(150) NULL
file_size_bytes BIGINT NULL
checksum_sha256 VARCHAR(128) NULL
storage_provider VARCHAR(100) NULL
storage_key VARCHAR(1000) NULL
external_url VARCHAR(1000) NULL
text_content TEXT NULL
scan_status VARCHAR(50) NULL
scan_result_json JSONB NULL
render_format VARCHAR(50) NULL
created_at / created_by
approved_at TIMESTAMP NULL
approved_by UUID NULL
archived_at TIMESTAMP NULL
archived_by UUID NULL
version INT
```

Status:

```text
DRAFT
ACTIVE
IN_REVIEW
APPROVED
REJECTED
ARCHIVED
```

Constraint:

```text
unique document_id + version_number
```

---

## 7.4 DocumentLink — `document_link`

Fields:

```text
id UUID PK
document_id UUID NOT NULL
document_version_id UUID NULL
workspace_id UUID NOT NULL
project_id UUID NULL
target_type VARCHAR(100) NOT NULL
target_id UUID NOT NULL
link_type VARCHAR(50) NOT NULL
created_at / created_by
archived_at / archived_by
version INT
```

---

## 7.5 DocumentShare — `document_share`

Fields:

```text
id UUID PK
document_id UUID NOT NULL
document_version_id UUID NULL
workspace_id UUID NOT NULL
project_id UUID NULL
share_target_type VARCHAR(50) NOT NULL
share_target_id UUID NULL
share_target_email VARCHAR(255) NULL
access_level VARCHAR(50) NOT NULL
download_allowed BOOLEAN NOT NULL DEFAULT false
expires_at TIMESTAMP NULL
status VARCHAR(50) NOT NULL
created_at / created_by
revoked_at TIMESTAMP NULL
revoked_by UUID NULL
revocation_reason TEXT NULL
version INT
```

Status:

```text
ACTIVE
REVOKED
EXPIRED
```

Access level:

```text
VIEW_METADATA
PREVIEW
DOWNLOAD
```

External email sharing is deferred unless external portal exists.

---

## 7.6 DocumentAccessLog — `document_access_log`

Fields:

```text
id UUID PK
document_id UUID NOT NULL
document_version_id UUID NULL
workspace_id UUID NOT NULL
project_id UUID NULL
actor_user_id UUID NULL
action VARCHAR(50) NOT NULL
access_result VARCHAR(50) NOT NULL
ip_address VARCHAR(100) NULL
user_agent VARCHAR(500) NULL
details_json JSONB NULL
created_at TIMESTAMP NOT NULL
trace_id VARCHAR(100) NULL
```

Action:

```text
VIEW_METADATA
PREVIEW
DOWNLOAD
CREATE_SHARE
REVOKE_SHARE
ACCESS_DENIED
```

---

## 7.7 DocumentTemplate — `document_template`

Fields:

```text
id UUID PK
workspace_id UUID NULL
code VARCHAR(150) NOT NULL
name VARCHAR(255) NOT NULL
description TEXT NULL
category VARCHAR(100) NOT NULL
status VARCHAR(50) NOT NULL
current_version_id UUID NULL
created_at / created_by
updated_at / updated_by
archived_at / archived_by
version INT
```

Constraint:

```text
unique workspace_id + code
```

---

## 7.8 DocumentTemplateVersion — `document_template_version`

Fields:

```text
id UUID PK
template_id UUID NOT NULL
version_number INT NOT NULL
status VARCHAR(50) NOT NULL
renderer_type VARCHAR(50) NOT NULL
template_content TEXT NOT NULL
variables_schema_json JSONB NOT NULL
output_format VARCHAR(50) NOT NULL
created_at / created_by
published_at TIMESTAMP NULL
published_by UUID NULL
archived_at TIMESTAMP NULL
archived_by UUID NULL
version INT
```

Status:

```text
DRAFT
PUBLISHED
ARCHIVED
```

---

## 7.9 GeneratedDocumentJob — `generated_document_job`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
project_id UUID NULL
source_type VARCHAR(100) NOT NULL
source_id UUID NOT NULL
template_id UUID NOT NULL
template_version_id UUID NOT NULL
actor_user_id UUID NOT NULL
status VARCHAR(50) NOT NULL
output_format VARCHAR(50) NOT NULL
input_context_json JSONB NULL
masking_summary_json JSONB NULL
output_document_id UUID NULL
output_document_version_id UUID NULL
error_code VARCHAR(150) NULL
error_message TEXT NULL
started_at TIMESTAMP NULL
completed_at TIMESTAMP NULL
created_at TIMESTAMP NOT NULL
trace_id VARCHAR(100) NULL
version INT
```

Status:

```text
PENDING
RUNNING
COMPLETED
FAILED
CANCELLED
```

---

## 7.10 DocumentReviewAction — optional/simple approval

Fields:

```text
id UUID PK
document_id UUID NOT NULL
document_version_id UUID NOT NULL
workspace_id UUID NOT NULL
project_id UUID NULL
action VARCHAR(50) NOT NULL
actor_user_id UUID NOT NULL
reason TEXT NULL
created_at TIMESTAMP NOT NULL
trace_id VARCHAR(100) NULL
```

Actions:

```text
SUBMIT_REVIEW
APPROVE
REJECT
REOPEN
ARCHIVE
```

---

# 8. API TO-BE list

All APIs use `/api`.

---

## 8.1 Folder APIs

```text
POST  /api/workspaces/{workspaceId}/documents/folders
GET   /api/workspaces/{workspaceId}/documents/folders
GET   /api/workspaces/{workspaceId}/documents/folders/{folderId}
PUT   /api/workspaces/{workspaceId}/documents/folders/{folderId}
PATCH /api/workspaces/{workspaceId}/documents/folders/{folderId}/archive

POST  /api/projects/{projectId}/documents/folders
GET   /api/projects/{projectId}/documents/folders
```

---

## 8.2 Document APIs

```text
POST  /api/workspaces/{workspaceId}/documents
GET   /api/workspaces/{workspaceId}/documents
GET   /api/workspaces/{workspaceId}/documents/{documentId}
PUT   /api/workspaces/{workspaceId}/documents/{documentId}
PATCH /api/workspaces/{workspaceId}/documents/{documentId}/archive

POST  /api/projects/{projectId}/documents
GET   /api/projects/{projectId}/documents
GET   /api/projects/{projectId}/documents/{documentId}
```

Filters:

```text
folderId
documentTypeId
status
confidentialityLevel
tag
linkedTargetType
linkedTargetId
createdBy
dateFrom
dateTo
```

---

## 8.3 Document version APIs

```text
POST  /api/workspaces/{workspaceId}/documents/{documentId}/versions
GET   /api/workspaces/{workspaceId}/documents/{documentId}/versions
GET   /api/workspaces/{workspaceId}/documents/{documentId}/versions/{versionId}
POST  /api/workspaces/{workspaceId}/documents/{documentId}/versions/{versionId}/submit-review
POST  /api/workspaces/{workspaceId}/documents/{documentId}/versions/{versionId}/approve
POST  /api/workspaces/{workspaceId}/documents/{documentId}/versions/{versionId}/reject
POST  /api/workspaces/{workspaceId}/documents/{documentId}/versions/{versionId}/make-current
PATCH /api/workspaces/{workspaceId}/documents/{documentId}/versions/{versionId}/archive
```

Upload mode depends on backend storage design:

```text
multipart upload
pre-signed upload
external reference
text content
```

Completion file must document.

---

## 8.4 Download / preview APIs

```text
GET /api/workspaces/{workspaceId}/documents/{documentId}/download
GET /api/workspaces/{workspaceId}/documents/{documentId}/versions/{versionId}/download
GET /api/workspaces/{workspaceId}/documents/{documentId}/preview
GET /api/workspaces/{workspaceId}/documents/{documentId}/versions/{versionId}/preview
```

Rules:

```text
1. Authorization required.
2. Download logged.
3. Raw permanent storage URL not exposed.
```

---

## 8.5 Document link APIs

```text
POST   /api/workspaces/{workspaceId}/documents/{documentId}/links
GET    /api/workspaces/{workspaceId}/documents/{documentId}/links
DELETE /api/workspaces/{workspaceId}/documents/{documentId}/links/{linkId}

GET /api/projects/{projectId}/document-links?targetType=DELIVERABLE&targetId=...
```

---

## 8.6 Document share APIs

```text
POST   /api/workspaces/{workspaceId}/documents/{documentId}/shares
GET    /api/workspaces/{workspaceId}/documents/{documentId}/shares
PATCH  /api/workspaces/{workspaceId}/documents/{documentId}/shares/{shareId}/revoke
```

Phase 27 internal share only.

---

## 8.7 Template APIs

```text
POST  /api/document-templates
GET   /api/document-templates
GET   /api/document-templates/{templateId}
PUT   /api/document-templates/{templateId}
PATCH /api/document-templates/{templateId}/archive

POST  /api/document-templates/{templateId}/versions
GET   /api/document-templates/{templateId}/versions
PUT   /api/document-templates/{templateId}/versions/{versionId}
POST  /api/document-templates/{templateId}/versions/{versionId}/publish
PATCH /api/document-templates/{templateId}/versions/{versionId}/archive
```

---

## 8.8 Generated document APIs

```text
POST /api/projects/{projectId}/generated-documents/jobs
GET  /api/projects/{projectId}/generated-documents/jobs
GET  /api/projects/{projectId}/generated-documents/jobs/{jobId}
POST /api/projects/{projectId}/generated-documents/jobs/{jobId}/cancel
```

Generate request:

```json
{
  "sourceType": "QUOTE_VERSION",
  "sourceId": "uuid",
  "templateCode": "QUOTE_PROPOSAL",
  "outputFormat": "HTML",
  "targetFolderId": "uuid",
  "documentTitle": "Proposal for Client Portal"
}
```

---

## 8.9 Evidence integration APIs

Convenience endpoints:

```text
POST /api/projects/{projectId}/deliverables/{deliverableId}/evidence/documents
POST /api/projects/{projectId}/test-runs/{testRunId}/evidence/documents
POST /api/projects/{projectId}/defects/{defectId}/evidence/documents
POST /api/projects/{projectId}/releases/{releasePackageId}/documents
POST /api/projects/{projectId}/decisions/{decisionId}/documents
POST /api/projects/{projectId}/raid-items/{raidItemId}/documents
```

These should internally create Document + Version + Link.

---

## 8.10 Document reports APIs

```text
GET /api/projects/{projectId}/reports/documents
GET /api/projects/{projectId}/reports/document-versions
GET /api/projects/{projectId}/reports/generated-documents
GET /api/projects/{projectId}/reports/evidence-coverage
GET /api/projects/{projectId}/reports/document-access-log
GET /api/projects/{projectId}/reports/documents-pending-review
```

---

# 9. Authorization requirements

Required authorities:

```text
DOCUMENT_VIEW
DOCUMENT_CREATE
DOCUMENT_UPDATE
DOCUMENT_ARCHIVE
DOCUMENT_DELETE_SOFT
DOCUMENT_DOWNLOAD
DOCUMENT_PREVIEW
DOCUMENT_VERSION_CREATE
DOCUMENT_VERSION_VIEW
DOCUMENT_VERSION_APPROVE
DOCUMENT_VERSION_REJECT
DOCUMENT_VERSION_MAKE_CURRENT
DOCUMENT_VERSION_ARCHIVE
DOCUMENT_LINK_CREATE
DOCUMENT_LINK_DELETE
DOCUMENT_SHARE_CREATE
DOCUMENT_SHARE_REVOKE
DOCUMENT_ACCESS_LOG_VIEW

DOCUMENT_TEMPLATE_VIEW
DOCUMENT_TEMPLATE_CREATE
DOCUMENT_TEMPLATE_UPDATE
DOCUMENT_TEMPLATE_ARCHIVE
DOCUMENT_TEMPLATE_VERSION_CREATE
DOCUMENT_TEMPLATE_VERSION_UPDATE
DOCUMENT_TEMPLATE_VERSION_PUBLISH
DOCUMENT_TEMPLATE_VERSION_ARCHIVE

GENERATED_DOCUMENT_CREATE
GENERATED_DOCUMENT_VIEW
GENERATED_DOCUMENT_CANCEL

DOCUMENT_CONFIDENTIAL_VIEW
DOCUMENT_RESTRICTED_VIEW
DOCUMENT_REPORT_VIEW
DOCUMENT_ACCESS_REPORT_VIEW
```

Rules:

```text
1. Workspace/project access required.
2. Download requires document download permission.
3. Restricted documents require explicit restricted view permission.
4. Document link target access does not automatically grant document access unless policy configured.
5. Generated document requires source data permission.
6. Template publish requires stronger permission.
7. External/public share not available unless explicitly implemented.
```

---

# 10. Lifecycle rules

## 10.1 Document lifecycle

```text
DRAFT → ACTIVE
ACTIVE → IN_REVIEW → APPROVED
IN_REVIEW → REJECTED
ACTIVE/APPROVED → ARCHIVED
```

Rules:

```text
1. Approved document metadata changes audited.
2. Archive does not delete versions.
3. Soft delete only if retention policy allows.
```

## 10.2 Version lifecycle

```text
DRAFT → ACTIVE
ACTIVE → IN_REVIEW → APPROVED
IN_REVIEW → REJECTED
ACTIVE/APPROVED → ARCHIVED
```

Rules:

```text
1. Approved version immutable.
2. New upload creates new version.
3. Only one current version.
4. Historical versions not overwritten.
```

## 10.3 Template lifecycle

```text
DRAFT → PUBLISHED
PUBLISHED → ARCHIVED
```

Rules:

```text
1. Published template version immutable.
2. Template variables schema validated.
3. Generated jobs require published version.
```

## 10.4 Generated job lifecycle

```text
PENDING → RUNNING → COMPLETED
PENDING/RUNNING → FAILED
PENDING/RUNNING → CANCELLED
```

Rules:

```text
1. Completed job creates DocumentVersion.
2. Failed job records error.
3. Job does not send externally.
```

---

# 11. Storage strategy

Phase 27 must document actual storage choice.

Supported patterns:

```text
1. Local/private object storage
2. S3-compatible object storage
3. Database BLOB discouraged except small text/content
4. External reference only
```

Required behavior:

```text
1. Storage key opaque.
2. File size limit.
3. Mime type allowlist/blocklist.
4. Checksum.
5. Optional scan status.
6. Download authorization.
7. Cleanup policy for orphaned failed uploads.
```

Do not store provider secrets in Document/Environment records.

---

# 12. Generated document strategy

Phase 27 should support at least one real output format.

Recommended minimum:

```text
HTML or Markdown generated document stored as text_content or file.
```

Optional if renderer exists:

```text
PDF
DOCX
```

Rules:

```text
1. If PDF/DOCX not implemented, explicitly defer.
2. Generated content must use masked source data.
3. Template variables must be validated.
4. Generated output linked to source artifact.
5. Regeneration creates new version or new document according to policy.
```

---

# 13. Integration rules

## 13.1 Quote integration

```text
QuoteVersion → Generated Proposal Document
QuoteTerms → Template variables
QuoteLines → Proposal section
```

Rules:

```text
1. Do not expose internal margin unless internal template and permission.
2. Generated proposal does not create contract.
3. Sending to client deferred.
```

## 13.2 Reporting integration

```text
ReportSnapshot → Generated Report Document
ReportExportJob → DocumentVersion
```

Rules:

```text
1. Use masked snapshot.
2. Export file becomes controlled document if stored.
3. Access and download audited.
```

## 13.3 Acceptance integration

```text
AcceptanceEvidence → DocumentLink
DeliverableAcceptance → Acceptance Report
AcceptanceCriteria → Evidence coverage report
```

Rules:

```text
1. Acceptance report does not create invoice.
2. Evidence document access checked.
```

## 13.4 Quality / Release integration

```text
TestCaseResult evidence → Document
Defect evidence → Document
Release notes → Document
Rollback plan → Document
Deployment summary → Document
```

Rules:

```text
1. No deployment secrets.
2. Release document does not execute deployment.
```

## 13.5 RAID / Decision integration

```text
DecisionRecord → Decision document
RaidItem → Risk/Issue evidence
```

Rules:

```text
1. Decision document does not implement workflow approval.
2. Risk evidence access controlled.
```

## 13.6 AI integration

AI may suggest:

```text
document summary
proposal draft content
acceptance report draft
test report summary
release notes draft
```

Rules:

```text
1. AI output is proposal only.
2. Human review required before publishing generated doc.
3. Full RAG over docs deferred to Phase 41.
4. AI context respects document access permissions.
```

---

# 14. Event Registry integration

Recommended source system:

```text
SCOPERY_DOCUMENT
```

Required events:

```text
DOCUMENT_FOLDER_CREATED
DOCUMENT_FOLDER_UPDATED
DOCUMENT_FOLDER_ARCHIVED

DOCUMENT_CREATED
DOCUMENT_UPDATED
DOCUMENT_ARCHIVED
DOCUMENT_DELETED_SOFT
DOCUMENT_VIEWED
DOCUMENT_DOWNLOADED

DOCUMENT_VERSION_CREATED
DOCUMENT_VERSION_SUBMITTED_REVIEW
DOCUMENT_VERSION_APPROVED
DOCUMENT_VERSION_REJECTED
DOCUMENT_VERSION_MARKED_CURRENT
DOCUMENT_VERSION_ARCHIVED

DOCUMENT_LINK_CREATED
DOCUMENT_LINK_REMOVED

DOCUMENT_SHARE_CREATED
DOCUMENT_SHARE_REVOKED
DOCUMENT_SHARE_EXPIRED

DOCUMENT_TEMPLATE_CREATED
DOCUMENT_TEMPLATE_UPDATED
DOCUMENT_TEMPLATE_ARCHIVED
DOCUMENT_TEMPLATE_VERSION_CREATED
DOCUMENT_TEMPLATE_VERSION_PUBLISHED
DOCUMENT_TEMPLATE_VERSION_ARCHIVED

GENERATED_DOCUMENT_JOB_CREATED
GENERATED_DOCUMENT_JOB_STARTED
GENERATED_DOCUMENT_JOB_COMPLETED
GENERATED_DOCUMENT_JOB_FAILED
GENERATED_DOCUMENT_JOB_CANCELLED

DOCUMENT_ACCESS_DENIED
DOCUMENT_SCAN_FAILED
```

Standard variables:

```text
actor.userId
organization.id
workspace.id
project.id
document.id
document.title
documentVersion.id
documentVersion.versionNumber
documentType.id
folder.id
template.id
templateVersion.id
generatedDocumentJob.id
source.type
source.id
target.type
target.id
occurredAt
traceId
```

---

# 15. Notification integration

Notification candidates:

```text
document shared internally
document version submitted for review
document version approved/rejected
generated document completed/failed
restricted document access denied
document expiring soon
evidence document added
release document approved
```

Recipients:

```text
document owner
reviewer
project watchers
deliverable owner
release owner
requester
```

No external client emails unless external portal/sharing is implemented.

---

# 16. Reporting integration

Extend Phase 22 with:

```text
PROJECT_DOCUMENT_INVENTORY_REPORT
PROJECT_DOCUMENT_VERSION_HISTORY_REPORT
PROJECT_GENERATED_DOCUMENT_REPORT
PROJECT_EVIDENCE_COVERAGE_REPORT
PROJECT_DOCUMENT_ACCESS_LOG_REPORT
PROJECT_DOCUMENT_PENDING_REVIEW_REPORT
```

Masking:

```text
restricted documents omitted or summarized for unauthorized users.
download logs visible only to authorized users.
```

---

# 17. Audit / activity / outbox

Audit-sensitive actions:

```text
restricted document created/downloaded
document version approved/rejected
document shared/revoked
generated quote proposal created
report export turned into document
evidence document added/removed
document archived/deleted soft
access denied on restricted document
```

Activity actions:

```text
DOCUMENT_CREATED
DOCUMENT_VERSION_CREATED
DOCUMENT_VERSION_APPROVED
DOCUMENT_SHARED
GENERATED_DOCUMENT_JOB_COMPLETED
DOCUMENT_ARCHIVED
```

Outbox required for major document events.

Idempotency recommended for:

```text
POST /documents
POST /documents/{id}/versions
POST /documents/{id}/versions/{versionId}/approve
POST /generated-documents/jobs
POST /documents/{id}/shares
POST evidence document convenience endpoints
```

---

# 18. Business rules master

## 18.1 Folder rules

```text
DOC-FOL-001 Folder name required.
DOC-FOL-002 Folder path unique under parent.
DOC-FOL-003 Folder belongs to workspace/project.
DOC-FOL-004 Archiving folder does not delete documents by default.
```

## 18.2 Document rules

```text
DOC-001 Document title required.
DOC-002 Document type must be active if provided.
DOC-003 Document belongs to workspace/project.
DOC-004 Current version belongs to document.
DOC-005 Archived document remains auditable.
DOC-006 Restricted document requires restricted view permission.
DOC-007 Metadata changes audited.
```

## 18.3 Version rules

```text
VER-001 Version number unique per document.
VER-002 Approved version immutable.
VER-003 New upload creates new version.
VER-004 Only one current version.
VER-005 File size limit enforced.
VER-006 Mime type validated.
VER-007 Checksum stored.
VER-008 Failed scan blocks download if scanner exists.
```

## 18.4 Link/share rules

```text
LNK-001 Link target must belong to same project/workspace.
LNK-002 Duplicate active links prevented.
LNK-003 Link does not grant access unless policy configured.
SHR-001 Internal share only in Phase 27.
SHR-002 Share expiry respected.
SHR-003 Revoke prevents future access.
SHR-004 Share access audited.
```

## 18.5 Template/generation rules

```text
TPL-001 Template code unique within workspace/system.
TPL-002 Published template version immutable.
TPL-003 Variables schema required.
GEN-001 Generation requires source permission.
GEN-002 Generation uses masked source data.
GEN-003 Generation requires published template version.
GEN-004 Completed generation creates document version.
GEN-005 PDF/DOCX not claimed unless renderer exists.
```

---

# 19. Error catalog

```text
DOCUMENT_FOLDER_NOT_FOUND
DOCUMENT_FOLDER_NAME_REQUIRED
DOCUMENT_FOLDER_DUPLICATE_PATH
DOCUMENT_FOLDER_PARENT_MISMATCH
DOCUMENT_FOLDER_ACCESS_DENIED

DOCUMENT_NOT_FOUND
DOCUMENT_TITLE_REQUIRED
DOCUMENT_TYPE_NOT_FOUND
DOCUMENT_TYPE_INACTIVE
DOCUMENT_PROJECT_MISMATCH
DOCUMENT_WORKSPACE_MISMATCH
DOCUMENT_ARCHIVED
DOCUMENT_RESTRICTED_ACCESS_DENIED
DOCUMENT_ACCESS_DENIED

DOCUMENT_VERSION_NOT_FOUND
DOCUMENT_VERSION_DUPLICATE
DOCUMENT_VERSION_IMMUTABLE
DOCUMENT_VERSION_NOT_CURRENT
DOCUMENT_VERSION_FILE_REQUIRED
DOCUMENT_VERSION_FILE_TOO_LARGE
DOCUMENT_VERSION_MIME_NOT_ALLOWED
DOCUMENT_VERSION_CHECKSUM_MISMATCH
DOCUMENT_VERSION_SCAN_FAILED
DOCUMENT_VERSION_ACCESS_DENIED

DOCUMENT_LINK_NOT_FOUND
DOCUMENT_LINK_TARGET_MISMATCH
DOCUMENT_LINK_DUPLICATE
DOCUMENT_LINK_ACCESS_DENIED

DOCUMENT_SHARE_NOT_FOUND
DOCUMENT_SHARE_EXTERNAL_NOT_SUPPORTED
DOCUMENT_SHARE_EXPIRED
DOCUMENT_SHARE_REVOKED
DOCUMENT_SHARE_ACCESS_DENIED

DOCUMENT_TEMPLATE_NOT_FOUND
DOCUMENT_TEMPLATE_CODE_ALREADY_EXISTS
DOCUMENT_TEMPLATE_VERSION_NOT_FOUND
DOCUMENT_TEMPLATE_VERSION_IMMUTABLE
DOCUMENT_TEMPLATE_VERSION_NOT_PUBLISHED
DOCUMENT_TEMPLATE_VARIABLE_INVALID
DOCUMENT_TEMPLATE_RENDERER_NOT_SUPPORTED

GENERATED_DOCUMENT_JOB_NOT_FOUND
GENERATED_DOCUMENT_SOURCE_NOT_FOUND
GENERATED_DOCUMENT_SOURCE_ACCESS_DENIED
GENERATED_DOCUMENT_FORMAT_NOT_SUPPORTED
GENERATED_DOCUMENT_RENDER_FAILED
GENERATED_DOCUMENT_JOB_FAILED

DOCUMENT_DOWNLOAD_DENIED
DOCUMENT_PREVIEW_DENIED
DOCUMENT_STORAGE_ERROR
```

---

# 20. Required tests

## 20.1 Folder tests

```text
createFolder_valid_success
createFolder_duplicatePath_rejected
createNestedFolder_valid_success
archiveFolder_doesNotDeleteDocuments
folderCrossWorkspace_forbidden
```

## 20.2 Document tests

```text
createDocument_valid_success
createDocument_inactiveType_rejected
updateDocumentMetadata_success
restrictedDocument_withoutPermission_forbidden
archiveDocument_success
archivedDocument_auditable
```

## 20.3 Version tests

```text
createDocumentVersion_upload_success
createVersion_incrementsNumber
makeCurrent_onlyOneCurrent
approveVersion_immutable
updateApprovedVersion_rejected
mimeTypeNotAllowed_rejected
fileTooLarge_rejected
checksumStored
downloadCurrentVersion_success
downloadSpecificVersion_success
downloadLogsAccess
```

## 20.4 Link/share tests

```text
linkDocumentToDeliverable_success
linkDocumentToOtherProjectTarget_rejected
duplicateDocumentLink_rejected
internalShare_valid_success
revokeShare_success
expiredShare_deniesAccess
externalShare_rejectedInPhase27
```

## 20.5 Template tests

```text
createTemplate_valid_success
createTemplateVersion_valid_success
publishTemplateVersion_success
publishedTemplateVersion_immutable
templateVariableValidation_rejectsMissingVar
unsupportedRenderer_rejected
```

## 20.6 Generated document tests

```text
generateQuoteProposal_success_ifQuoteExists
generateQuoteProposal_masksInternalMarginWithoutPermission
generateReportDocument_usesMaskedSnapshot
generateAcceptanceReport_success
generateTestReport_success
generationRequiresPublishedTemplate
generationSourceAccessDenied_forbidden
generationFailure_storesError
idempotentGeneration_noDuplicate
```

## 20.7 Evidence integration tests

```text
acceptanceEvidenceCreatesDocumentLink
testEvidenceCreatesDocumentLink
defectEvidenceCreatesDocumentLink
releaseDocumentCreatesLink
documentLinkDoesNotGrantAccess
```

## 20.8 Authorization tests

```text
viewDocument_withoutPermission_forbidden
downloadDocument_withoutPermission_forbidden
downloadRestricted_withoutRestrictedPermission_forbidden
approveVersion_withoutPermission_forbidden
publishTemplate_withoutPermission_forbidden
generateDocument_withoutSourcePermission_forbidden
crossWorkspaceDocument_forbidden
```

## 20.9 Event/audit tests

```text
documentEventSeeder_firstRun_createsDefinitions
documentEventSeeder_secondRun_noDuplicates
documentCreated_eventEmitted
documentDownloaded_accessLogCreated
versionApproved_auditCreated
documentShared_eventEmitted
generatedDocumentCompleted_eventEmitted
```

---

# 21. Manual verification checklist

Completion file must include:

```text
1. Create project document folder.
2. Create document.
3. Upload first version.
4. Download current version and confirm access log.
5. Upload second version and mark current.
6. Approve version and confirm immutability.
7. Link document to deliverable evidence.
8. Link document to test run evidence.
9. Create template and publish template version.
10. Generate quote proposal or report document.
11. Confirm generated doc linked to source artifact.
12. Confirm finance/quote masked when actor lacks permission.
13. Create internal share and revoke it.
14. Confirm external share is rejected/deferred.
15. Confirm no PDF/DOCX/e-sign/RAG is falsely claimed unless actually implemented.
```

---

# 22. Acceptance criteria

Phase 27 is accepted only if:

```text
1. Current document capability is classified against TO-BE.
2. DocumentFolder implemented/tested.
3. Document implemented/tested.
4. DocumentVersion implemented/tested.
5. Storage reference/download/access log implemented/tested.
6. DocumentLink implemented/tested.
7. Internal DocumentShare foundation implemented/tested or explicitly deferred.
8. DocumentTemplate and TemplateVersion implemented/tested.
9. GeneratedDocumentJob implemented/tested.
10. Quote/report/acceptance/test/release generation implemented/tested or explicitly deferred with reason.
11. Evidence repository integration implemented/tested.
12. IAM permissions implemented/tested.
13. Event seeders idempotent.
14. Activity/audit/outbox follows Phase 04.
15. Reports implemented/tested or explicitly extended in reporting module.
16. No RAG/semantic index/e-sign/client portal/public share/PDF-DOCX renderer falsely claimed.
17. `mvn compile` passes.
18. `mvn test` passes.
19. Completion file exists.
```

Do not mark complete if:

```text
raw permanent storage URL exposed
download not authorized
download not audited
approved version editable
historical versions overwritten
document link bypasses access
generated document uses unmasked restricted source data
external public sharing is enabled without policy
PDF/DOCX claimed without renderer
tests fail
```

---

# 23. Required phase completion file

Agent must create:

```text
docs/phase-complete/PHASE_27_DOCUMENT_HUB_GENERATION_TO_BE_COMPLETE.md
```

Required sections:

```text
# Phase 27 — Document Hub / Generation Complete

## 1. Summary
## 2. Source Inputs Reviewed
## 3. Current vs TO-BE Classification Matrix
## 4. Implemented in Current BE
## 5. Implemented / Hardened in This Phase
## 6. Seed-only Items Added
## 7. Deferred Items and Target Phase
## 8. Document Boundary Decision
## 9. Storage Strategy
## 10. Entity Mapping
## 11. API Changes
## 12. Folder Strategy
## 13. Document / Version Strategy
## 14. Link / Evidence Strategy
## 15. Share Strategy
## 16. Template Strategy
## 17. Generated Document Strategy
## 18. Quote / Report / Acceptance / Quality Integration
## 19. Access / Download / Audit Strategy
## 20. Reporting Strategy
## 21. Notification / Event Strategy
## 22. Authorization Matrix
## 23. Activity / Audit / Outbox Notes
## 24. Idempotency Strategy
## 25. Tests Added
## 26. Commands Run
## 27. Test Results
## 28. Manual Verification
## 29. Assumptions
## 30. Deviations From Prompt
## 31. Known Risks
## 32. Future Phases That Must Return to Document Hub
```

---

# 24. Future phases that must return

```text
Phase 28 — Application / Requirement / Screen Traceability:
- Link documents to requirements, screens, APIs, traceability matrix.

Phase 30 — Customer / External Collaboration Portal:
- External client document sharing and controlled client downloads.

Phase 31 — Meetings / Collaboration:
- Meeting minutes, action items, decision documents.

Phase 34 — Workflow / Approval:
- Multi-step document approval, review routing, escalation.

Phase 35 — Advanced Notifications:
- Document review reminders, expiry reminders, digest.

Phase 36 — Contract / Billing / Revenue:
- Contract documents, invoice documents, signed commercial docs.

Phase 38 — Audit / Compliance / Privacy / Retention:
- Retention policies, legal hold, privacy redaction, audit export.

Phase 39 — Integration / Import / Export:
- External storage, e-sign provider, OCR, document import/export.

Phase 41 — Data Quality / Knowledge Graph / Semantic Index:
- Full document RAG, embeddings, semantic search, knowledge graph.
```

---

# 25. Agent anti-bịa rules

The agent must not:

```text
1. Claim full RAG/semantic search exists.
2. Claim knowledge graph exists.
3. Claim OCR exists unless implemented.
4. Claim virus scanning exists unless scanner exists.
5. Claim PDF/DOCX rendering exists unless renderer exists.
6. Claim e-signature exists.
7. Claim external client portal sharing exists.
8. Expose raw permanent storage URLs.
9. Allow download without authorization.
10. Allow approved versions to be overwritten.
11. Let document links bypass access.
12. Generate documents from unmasked restricted data.
13. Hide deferred RAG/e-sign/external sharing/OCR gaps.
```

---

# 26. Prompt to give coding agent

```text
You are implementing Phase 27 — TO-BE Document Hub, File Versioning, Document Templates, Generated Documents, Evidence Repository & Controlled Sharing.

This is not an as-is documentation task.

Before coding, read:
- CLAUDE.md / CLAUDE.ms
- Coding_convention.md
- Phase 00–26 docs and completion files
- Current backend code, migrations, tests
- Current file storage/export/document type code if any

Your task:
1. Compare current document/file/generation capability against this Phase 27 TO-BE spec.
2. Classify each capability with required labels.
3. Implement DocumentFolder, Document, DocumentVersion, DocumentLink, DocumentAccessLog.
4. Implement controlled upload/download/storage reference strategy.
5. Implement internal DocumentShare foundation or explicitly defer with reason.
6. Implement DocumentTemplate and DocumentTemplateVersion.
7. Implement GeneratedDocumentJob.
8. Integrate with Quote, Reporting, Acceptance Evidence, Quality/Test/Release, RAID/Decision where available.
9. Add IAM permissions, events, notifications, reports, audit/outbox, idempotency, and tests.
10. Run mvn compile and mvn test.
11. Create docs/phase-complete/PHASE_27_DOCUMENT_HUB_GENERATION_TO_BE_COMPLETE.md.

Do not implement or claim full RAG, semantic index, knowledge graph, OCR, e-signature, external client portal sharing, public anonymous share, real PDF/DOCX rendering, or virus scanning unless those integrations actually exist and are tested.
```

---

# 27. Quick tracking matrix

| Capability | Current backend | Phase 27 action | Later phase |
|---|---|---|---|
| DocumentFolder | Missing/unknown | Must implement | — |
| Document | Missing/unknown | Must implement | — |
| DocumentVersion | Missing/unknown | Must implement | — |
| Storage reference | Missing/unknown | Must implement | Integration expansion Phase 39 |
| Controlled download | Missing/unknown | Must implement | — |
| DocumentAccessLog | Missing/unknown | Must implement | Compliance Phase 38 |
| DocumentLink | Missing/unknown | Must implement | Knowledge graph Phase 41 |
| Internal share | Missing/unknown | Must implement/defer | Client portal Phase 30 |
| DocumentTemplate | Missing/unknown | Must implement | Workflow Phase 34 |
| TemplateVersion | Missing/unknown | Must implement | — |
| GeneratedDocumentJob | Missing/unknown | Must implement | PDF/DOCX Phase 39 if absent |
| Quote proposal generation | Missing/unknown | Must implement if quote exists | Contract Phase 36 |
| Report document generation | Missing/unknown | Must implement if reporting exists | Advanced export Phase 39 |
| Acceptance report generation | Missing/unknown | Must implement if Phase 24 exists | E-sign Phase 39 |
| Test/release document generation | Missing/unknown | Must implement if Phase 26 exists | — |
| OCR | Missing | Defer | Phase 39 |
| E-signature | Missing | Defer | Phase 39 / Contract |
| Full RAG / semantic index | Missing | Defer | Phase 41 |
| External client document portal | Missing | Defer | Phase 30 |

---

# 28. Final principle

Phase 27 is not complete when "a file can be uploaded."

Phase 27 is complete when Scopery can govern documents as project evidence:

```text
document metadata
+ immutable versions
+ controlled storage reference
+ artifact links
+ templates
+ generated documents
+ access-controlled download
+ audit logs
= trustworthy document hub
```

A document link is not permission.

A generated document is not a contract.

A file upload is not evidence unless linked and auditable.

No RAG without semantic index.

No e-signature without provider and legal flow.

No public sharing without external access model.
