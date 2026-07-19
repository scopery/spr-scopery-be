# ADR-041 — Phase 41 MVP Semantic Retrieval Foundation — Repository-Aligned Lock

> Status: **Accepted / implementation-blocking**  
> Date: 2026-07-17  
> Applies to: Phase 41 implementation and Phase 42–45 consumers  
> Repository baseline used by this revision: latest known Flyway migration `V94`; Document Hub version table `documenthub_version`; current Knowledge package convention from `CLAUDE.md` / `Coding_convention.md`  
> Conflict rule: **this ADR wins over Phase 41 planning text. `CLAUDE.md`, `Coding_convention.md`, and compile-safe existing package conventions win over any package example in this ADR.**

---

# 1. Decision summary

```text
Module owner: modules/knowledge
Table prefix: knowledge_
REST API base: /api/knowledge
Registered AI tool code: knowledge.search
MVP source types: TASK, DOCUMENT_VERSION, MEETING_MINUTE
Default embedding profile: OPENAI_TEXT_EMBEDDING_3_SMALL_1536_V1
Embedding dimensions: 1536
Vector similarity: cosine
Chunking algorithm: chunk-v1
Hybrid retrieval: BM25 + KNN + application-layer RRF(k=60)
External reranker: optional; disabled by default
Elasticsearch: 8.19.16
Elasticsearch schema: v001
Local object storage: MinIO
Production object storage: Cloudflare R2
Storage protocol: S3-compatible API
Document Hub table: documenthub_version
Document Hub existing columns preserved: storage_key, content_type, checksum
Flyway versions for this package: V95 and V96
```

No coding agent may change the module, table prefix, API base, source whitelist, embedding dimensions, chunking defaults, index aliases, graph whitelist, ACL format, or reranking requirement without a replacement ADR.

---

# 2. Repository precedence and implementation hygiene

The implementation must read the real repository before creating files.

Precedence:

```text
1. Current compile-safe code and table names
2. CLAUDE.md / CLAUDE.ms
3. Coding_convention.md
4. ADR-041 repository-aligned lock
5. Phase 41 detailed planning text
```

Rules:

```text
- Do not create interfaces/rest.
- Do not create a new semanticindex/retrieval/rag top-level module.
- Do not rename documenthub_version.
- Do not introduce document_version as a second table.
- Do not rename content_type to mime_type.
- Do not rename checksum to checksum_sha256.
- Do not create a parallel error envelope.
- Use the existing ErrorResponse/AppException pipeline.
- If V95 or V96 is occupied on the target branch at merge time, renumber both migrations atomically to the next free consecutive versions and update every documentation reference in the same change.
```

---

# 3. Package layout lock

Logical owner:

```text
modules/knowledge
```

Use the current repository pattern:

```text
modules/knowledge/
├── source/
│   └── http/
│       ├── controller/
│       ├── request/
│       └── response/
├── retrieval/
│   └── http/
│       ├── controller/
│       ├── request/
│       └── response/
├── indexing/
│   └── http/
│       ├── controller/
│       ├── request/
│       └── response/
├── graph/
│   └── http/
│       ├── controller/
│       └── response/
├── application/
│   ├── command/
│   ├── action/
│   ├── service/
│   ├── response/
│   └── port/
├── domain/
│   ├── model/
│   ├── repository/
│   ├── policy/
│   └── value/
└── infrastructure/
    ├── persistence/
    ├── elasticsearch/
    ├── embedding/
    ├── extraction/
    ├── storage/
    └── eventconsumer/
```

Required flow:

```text
HTTP Controller
→ Request
→ Command or Query parameters
→ *Action.execute(Command) for writes
→ *QueryService for reads
→ Response DTO
```

Shared object-storage port must use the repository's existing common/platform/application-port convention. Do not invent a new shared-kernel convention. Minimum logical types:

```text
ObjectStorageProvider
CreatePresignedUploadCommand
CreatePresignedDownloadQuery
HeadObjectQuery
DeleteObjectCommand
StoredObjectMetadata
```

Provider SDK imports are infrastructure-only.

---

# 4. Database and migration lock

## 4.1 Migration filenames

```text
V95__phase_41_knowledge_semantic_retrieval.sql
V96__phase_41_documenthub_version_object_storage_upgrade.sql
```

## 4.2 Knowledge-owned tables

```text
knowledge_source
knowledge_projection
knowledge_chunk
knowledge_embedding_profile
knowledge_index_definition
knowledge_index_job
knowledge_graph_node
knowledge_graph_edge
knowledge_retrieval_trace
```

Cross-module IDs are stored without database foreign keys to source-module tables. Foreign keys inside `knowledge_*` are required.

## 4.3 Document Hub alignment

Real table:

```text
documenthub_version
```

Existing columns used by Phase 41:

```text
storage_key
content_type
checksum
file_size_bytes, when present in the current migration/entity
```

Phase 41 adds:

```text
storage_provider
storage_status
upload_completed_at
storage_verified_at
storage_etag
```

`storage_key` becomes the canonical opaque object key. Existing legacy values remain `LEGACY_REFERENCE` until verified or migrated.

---

# 5. MVP source whitelist

Exactly:

```text
TASK
DOCUMENT_VERSION
MEETING_MINUTE
```

Production adapters:

```text
TaskKnowledgeSourceAdapter
DocumentVersionKnowledgeSourceAdapter
MeetingMinuteKnowledgeSourceAdapter
```

Each adapter returns a canonical `KnowledgeSourceSnapshot`; it must not expose a JPA entity outside its module boundary.

Deferred sources include Requirement, Risk, Decision, WBS, Application, Screen, API, Comment, Support Case, custom fields, reports, and external connectors.

---

# 6. Embedding profile lock

```text
code: OPENAI_TEXT_EMBEDDING_3_SMALL_1536_V1
provider: OPENAI
model: text-embedding-3-small
dimensions: 1536
maxInputTokens: 8191
distanceMetric: COSINE
normalization: PROVIDER_NORMALIZED
status: ACTIVE
```

Rules:

```text
- ES v001 dense_vector.dims = 1536.
- A different dimension requires mapping v002 and full reindex.
- Secrets are configuration, never profile rows.
- Unit tests use a deterministic fake provider with exactly 1536 dimensions.
- Domain/application code depends on EmbeddingProvider only.
```

---

# 7. Chunking lock

Algorithm:

```text
chunk-v1
```

Normalization:

```text
Unicode NFKC
line endings → \n
trim trailing whitespace
collapse >2 blank lines to 2
preserve heading order/path
Unicode code-point offsets
cl100k_base token estimation
```

| Source | Split strategy | Target | Overlap | Hard max |
|---|---|---:|---:|---:|
| TASK | structured sections | 600 | 0 | 800 |
| DOCUMENT_VERSION | heading-aware paragraphs | 800 | 120 | 1,000 |
| MEETING_MINUTE | agenda/decision/action blocks | 600 | 80 | 800 |

Chunk identity:

```text
SHA-256(sourceVersionIdentity + chunk-v1 + ordinal + normalizedText)
```

Identical normalized input must reproduce identical chunks, hashes, headings, and code-point offsets.

---

# 8. DOCUMENT_VERSION extraction MVP

Supported:

| Format | Detection | Extractor |
|---|---|---|
| PDF text layer | `application/pdf` | Apache PDFBox |
| DOCX | Office Open XML MIME | Apache POI XWPF |
| TXT | `text/plain` | UTF-8 decoder |
| Markdown | `text/markdown` or `.md` | UTF-8 decoder + heading parser |

Explicitly deferred:

```text
OCR/scanned PDF
XLSX
PPTX
legacy DOC
images
video/audio transcription
password-protected files
```

Rules:

```text
- No OCR claim.
- Scanned/image-only PDF returns DOCUMENT_EXTRACTION_EMPTY_OR_SCANNED.
- Password-protected/unsupported file returns DOCUMENT_EXTRACTION_UNSUPPORTED.
- Extractor output is normalized before hashing/chunking.
- Extraction errors are redacted and retry-classified.
```

---

# 9. ACL and permissionSignature lock

ES filtering uses explicit `aclTokens`; `permissionSignature` only detects staleness.

Canonical tokens:

```text
workspace:<workspaceId>
project:<projectId>
user:<userId>
role:<roleCode>
team:<teamId>
classification:<level>
```

Canonical signature:

```text
acl:v1:sha256:<64 lowercase hex>
```

Signature input:

```json
{
  "workspaceId": "uuid",
  "projectId": "uuid-or-null",
  "classification": "INTERNAL",
  "aclTokens": ["sorted", "unique", "tokens"],
  "sourceAccessVersion": "stable-version-string"
}
```

Rules:

```text
- JSON keys sorted.
- aclTokens sorted and deduplicated.
- UTF-8, no insignificant whitespace.
- SHA-256 lowercase hex.
- Retrieval requires workspace token plus project/source-specific access.
- A matching signature never grants access by itself.
- Source permission is revalidated before opening a citation.
```

Source ACL mapping:

```text
TASK:
- workspace/project scope from Task/Project.
- actor must have PROJECT_VIEW + PROJECT_TASK_VIEW and project access.

DOCUMENT_VERSION:
- inherit document/workspace/project scope.
- use current Document Hub view/download/restricted rules.
- link to a project artifact does not grant document access.

MEETING_MINUTE:
- inherit meeting workspace/project scope and minutes visibility.
- internal/private notes are excluded from the projection unless the actor/source policy allows them.
```

---

# 10. Graph MVP lock

Node types:

```text
PROJECT
TASK
DOCUMENT_VERSION
MEETING_MINUTE
```

Edge types:

```text
PROJECT_HAS_TASK
PROJECT_HAS_DOCUMENT_VERSION
PROJECT_HAS_MEETING_MINUTE
TASK_DEPENDS_ON_TASK
MEETING_MINUTE_REFERENCES_TASK
DOCUMENT_VERSION_REFERENCES_TASK
```

Limits:

```text
default depth = 1
maximum depth = 2
default result limit = 20
maximum result limit = 50
maximum fan-out per node = 25
```

Graph expansion must apply ACL filtering to every target node.

---

# 11. Retrieval lock

Mandatory:

```text
BM25 candidate retrieval
KNN candidate retrieval
application-layer Reciprocal Rank Fusion with k=60
deduplication by chunkId
ACL/classification filters before candidate exposure
```

Defaults:

```text
lexical candidates = 50
vector candidates = 50
final topK default = 20
final topK max = 50
```

External reranker:

```text
optional
disabled by default
not an acceptance requirement for Phase 41
fallback = deterministic RRF order
```

---

# 12. Elasticsearch lock

```text
Elasticsearch version: 8.19.16
Template schema: v001
Index pattern: scopery-<env>-knowledge-chunks-v001-<generation>
Read alias: scopery-<env>-knowledge-chunks-read
Write alias: scopery-<env>-knowledge-chunks-write
Generation format: six digits, e.g. 000001
```

Mapping source:

```text
PHASE_41_ELASTICSEARCH_KNOWLEDGE_CHUNKS_MAPPING_V001.json
```

Alias switch is atomic. Reindex writes to a new generation, validates counts/smoke queries, then swaps read/write aliases. Old generation is retained until rollback window expires.

---

# 13. Object storage lock

```text
Local: MinIO
Production: Cloudflare R2
Protocol: S3-compatible API
Java SDK: AWS SDK for Java v2 S3 client and presigner
Bucket: private by default
Upload/download: short-lived presigned URL
```

No permanent storage URL is returned.

MVP upload rules:

```text
maximum file size default: 100 MiB, configuration-bound
presigned upload expiry: 10 minutes
presigned download expiry: 5 minutes
single PUT upload for <=100 MiB
multipart upload deferred unless existing repo already supports it
HeadObject verification required before AVAILABLE
```

Object key format:

```text
workspaces/{workspaceId}/projects/{projectId-or-none}/documents/{documentId}/versions/{versionId}/{uuid}
```

Original filename is metadata, never part of the security boundary.

---

# 14. API and error lock

Knowledge API schemas are defined in:

```text
PHASE_41_API_CONTRACTS_REPO_ALIGNED.md
```

Document upload/download APIs remain in the existing Document Hub controller boundary, not `/api/knowledge`.

Error rule:

```text
Use existing ErrorResponse/AppException and module KnowledgeErrorCatalog.
Do not create {code,message} as a second error model.
Known serialized fields include success and errorCode; exact common fields must match the current ErrorResponse class.
```

HTTP semantics:

```text
400 invalid input
401 unauthenticated
403 unauthorized
404 hidden/not found
409 idempotency or state conflict
422 business-state violation
503 provider unavailable
504 provider timeout
```

---

# 15. Source event → indexing action lock

Consumer must map canonical existing events:

| Source | Events | Index action |
|---|---|---|
| TASK | TASK_CREATED, TASK_UPDATED, TASK_ASSIGNED, TASK_STARTED, TASK_BLOCKED, TASK_COMPLETED, TASK_CANCELLED | UPSERT_SOURCE |
| TASK | TASK_ARCHIVED | INVALIDATE_SOURCE |
| TASK graph | TASK_DEPENDENCY_CREATED, TASK_DEPENDENCY_REMOVED | REFRESH_GRAPH |
| DOCUMENT_VERSION | DOCUMENT_VERSION_CREATED, DOCUMENT_VERSION_APPROVED, DOCUMENT_VERSION_MARKED_CURRENT | UPSERT_SOURCE when bytes/text are available |
| DOCUMENT_VERSION | DOCUMENT_VERSION_ARCHIVED | INVALIDATE_SOURCE |
| DOCUMENT | DOCUMENT_ARCHIVED, DOCUMENT_DELETED_SOFT | INVALIDATE_DOCUMENT_SOURCES |
| MEETING_MINUTE | actual Phase 31 minute create/update/approve/finalize event codes | UPSERT_SOURCE |
| MEETING_MINUTE | actual archive/delete event code | INVALIDATE_SOURCE |

For Meeting events, implementation must inspect the existing Phase 31 EventDefinition codes and place their mapping in one explicit `MeetingMinuteIndexEventMapper`. It must not create duplicate semantic events merely to match this document.

Every consumer is idempotent using event/outbox identity plus source version identity.

---

# 16. IAM and Event Registry seed lock

Exact catalogs live in:

```text
PHASE_41_IAM_EVENT_SEED_CATALOG_REPO_ALIGNED.md
```

Implementation mechanism:

```text
KnowledgePermissionInitializer
KnowledgeEventDefinitionInitializer
```

Use the repository's existing initializer/seeder API. Do not invent IAM/Event tables or raw seed SQL without inspecting the current schema.

---

# 17. Dependency lock

Required Maven artifacts, versions managed by existing BOM where possible:

```text
co.elastic.clients:elasticsearch-java
software.amazon.awssdk:s3
software.amazon.awssdk:auth
software.amazon.awssdk:regions
org.apache.pdfbox:pdfbox
org.apache.poi:poi-ooxml
```

Use existing Jackson, HTTP transport, resilience, testcontainers, and Spring dependencies when already present. Do not add a second JSON or HTTP stack unnecessarily.

---

# 18. Implementation order

```text
1. Verify V94 is still latest and verify documenthub_version columns.
2. Merge compose services and Maven dependencies.
3. Apply V95 and V96.
4. Implement repository-aligned package scaffold.
5. Implement S3-compatible storage port/adapter and Document Hub presigned flow.
6. Implement extraction adapters.
7. Implement source snapshots and ACL signatures.
8. Implement projections and deterministic chunks.
9. Implement embedding profile/provider.
10. Create ES template/index/aliases.
11. Implement indexing jobs and source event consumers.
12. Implement BM25+KNN+RRF retrieval.
13. Implement graph MVP.
14. Implement REST and knowledge.search.
15. Add IAM/Event initializers, audit, metrics, tests.
16. Run local MinIO/ES smoke tests and full Maven tests.
```

---

# 19. Definition of ready for coding

Phase 41 may begin only when:

```text
- Target branch confirms V95/V96 are free or documentation is atomically renumbered.
- documenthub_version and its real columns are verified.
- current ErrorResponse JSON is inspected.
- existing Knowledge package layout is inspected.
- existing IAM and Event initializer patterns are identified.
```

---

# 20. Definition of done

```text
- V95/V96 apply successfully on a clean database and upgrade database.
- MinIO local upload/download works.
- R2 staging presigned smoke test works.
- PDF/DOCX/TXT/MD extraction tests pass.
- Chunk reproduction tests pass.
- ACL leakage tests pass.
- ES mapping/aliases/reindex tests pass.
- TASK, DOCUMENT_VERSION, MEETING_MINUTE adapters work.
- BM25+KNN+RRF returns citation-bearing results.
- Graph response obeys depth/fan-out and permissions.
- IAM/Event seeders are idempotent.
- Existing ErrorResponse is used.
- mvn compile and mvn test pass.
- Completion file maps every artifact to code and test evidence.
```
