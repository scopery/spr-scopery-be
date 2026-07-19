# Phase 41 — Repository-Aligned REST and Tool Contracts

> Status: **Accepted / implementation-blocking**  
> Knowledge API base: `/api/knowledge`  
> AI tool: `knowledge.search`  
> Document binary API owner: existing Document Hub controllers  
> Error envelope: existing repository `ErrorResponse` / `AppException`

---

# 1. Common API rule

Controllers must follow repository conventions:

```text
request DTO → command/query → Action/QueryService → response DTO
```

Do not return JPA/domain entities.

Success payloads below are the endpoint body. If the repository wraps successful responses globally, preserve that existing wrapper rather than adding another wrapper.

Errors must serialize through the existing `ErrorResponse`. Do not create the previous `{code,message}` model.

Known required error semantics:

```json
{
  "success": false,
  "errorCode": "KNOWLEDGE_RETRIEVAL_ACCESS_DENIED",
  "message": "You do not have access to the requested knowledge scope.",
  "traceId": "existing-trace-id-field-if-present"
}
```

The exact common field list must match the current `ErrorResponse` class. Module-specific errors are defined in `KnowledgeErrorCatalog` and raised through existing `AppException` factories/helpers.

---

# 2. Hybrid search

## Endpoint

`POST /api/knowledge/retrieval/search`

## Request

```json
{
  "query": "Why is API Integration blocked?",
  "projectId": "b3fc8fb0-c0ef-4c28-b976-d76f6efcc86e",
  "sourceTypes": ["TASK", "DOCUMENT_VERSION", "MEETING_MINUTE"],
  "topK": 20,
  "includeGraphExpansion": false,
  "filters": {
    "language": null,
    "updatedFrom": null,
    "updatedTo": null
  }
}
```

Validation:

| Field | Rule |
|---|---|
| query | required, trimmed, 1–2,000 chars |
| projectId | optional UUID; actor must have project access |
| sourceTypes | optional subset of MVP whitelist |
| topK | default 20; min 1; max 50 |
| includeGraphExpansion | default false |
| updatedFrom/updatedTo | ISO-8601; from <= to |

Server injects workspace, actor, ACL tokens, classification clearance, and trace/correlation IDs.

## Response

```json
{
  "requestId": "ceea96ef-ff89-44d7-83ae-57b4e825cbfb",
  "retrievalMode": "HYBRID_RRF",
  "query": "Why is API Integration blocked?",
  "topK": 20,
  "truncated": false,
  "results": [
    {
      "rank": 1,
      "chunkId": "6f519969-79e0-48ad-b0f1-cef414457118",
      "sourceType": "TASK",
      "sourceId": "5b27ef76-2362-49c4-ac69-993c0a7392ea",
      "sourceVersionId": "7db4a57f-7041-4688-bd86-75502473b75c",
      "chunkOrdinal": 0,
      "title": "API Integration",
      "headingPath": ["Blocker"],
      "snippet": "Task is waiting for Authentication API.",
      "scores": {
        "lexicalRank": 2,
        "vectorRank": 1,
        "rrfScore": 0.032522
      },
      "citation": {
        "citationId": "cit-1",
        "sourceType": "TASK",
        "sourceId": "5b27ef76-2362-49c4-ac69-993c0a7392ea",
        "sourceVersionId": "7db4a57f-7041-4688-bd86-75502473b75c",
        "chunkId": "6f519969-79e0-48ad-b0f1-cef414457118",
        "appRoute": "/projects/b3fc8fb0-c0ef-4c28-b976-d76f6efcc86e/tasks/5b27ef76-2362-49c4-ac69-993c0a7392ea"
      }
    }
  ]
}
```

Never expose raw embedding vectors, internal ACL tokens, provider payloads, or unredacted trace data.

---

# 3. Source detail

`GET /api/knowledge/sources/{sourceId}`

```json
{
  "id": "uuid",
  "workspaceId": "uuid",
  "projectId": "uuid",
  "sourceType": "DOCUMENT_VERSION",
  "sourceRefId": "uuid",
  "sourceVersionRefId": "uuid",
  "title": "Requirements v3",
  "language": "vi",
  "classification": "INTERNAL",
  "status": "INDEXED",
  "contentHash": "64-lowercase-hex",
  "permissionSignature": "acl:v1:sha256:64-lowercase-hex",
  "projectionVersion": 1,
  "chunkCount": 17,
  "lastIndexedAt": "2026-07-17T02:00:00Z"
}
```

---

# 4. Chunk list

`GET /api/knowledge/sources/{sourceId}/chunks?page=0&size=50`

```json
{
  "items": [
    {
      "id": "uuid",
      "ordinal": 0,
      "strategyVersion": "chunk-v1",
      "headingPath": ["Scope"],
      "tokenCount": 734,
      "startCodePoint": 0,
      "endCodePoint": 3587,
      "contentHash": "64-lowercase-hex",
      "isCurrent": true
    }
  ],
  "page": 0,
  "size": 50,
  "totalElements": 17,
  "totalPages": 1
}
```

Raw chunk content requires `KNOWLEDGE_CHUNK_VIEW_CONTENT`.

---

# 5. Reindex APIs

## Source

`POST /api/knowledge/sources/{sourceId}/reindex`

```json
{
  "reason": "MANUAL_REPAIR",
  "forceReextract": false,
  "forceReembed": false
}
```

Response `202`:

```json
{
  "jobId": "uuid",
  "status": "QUEUED",
  "idempotencyKey": "resolved-idempotency-key"
}
```

## Project

`POST /api/knowledge/indexing/projects/{projectId}/reindex`

```json
{
  "sourceTypes": ["TASK", "DOCUMENT_VERSION", "MEETING_MINUTE"],
  "rebuildIndexGeneration": false,
  "reason": "PROJECT_REPAIR"
}
```

---

# 6. Index job/status

`GET /api/knowledge/indexing/jobs/{jobId}`

```json
{
  "jobId": "uuid",
  "jobType": "PROJECT_REINDEX",
  "status": "RUNNING",
  "processed": 81,
  "succeeded": 79,
  "failed": 2,
  "startedAt": "2026-07-17T02:00:00Z",
  "completedAt": null,
  "errorCode": null
}
```

`GET /api/knowledge/indexing/status?projectId={uuid}`

```json
{
  "elasticsearchAvailable": true,
  "readAlias": "scopery-prod-knowledge-chunks-read",
  "writeAlias": "scopery-prod-knowledge-chunks-write",
  "schemaVersion": "v001",
  "activeGeneration": "000014",
  "pendingJobs": 3,
  "failedJobs": 0,
  "oldestPendingAt": "2026-07-17T01:58:00Z"
}
```

---

# 7. Retrieval debug

`POST /api/knowledge/retrieval/debug`

Request = search request plus:

```json
{
  "includeCandidateRanks": true,
  "includeExclusionSummary": true
}
```

Requires `KNOWLEDGE_RETRIEVAL_DEBUG`. Content and exclusions remain permission-filtered/redacted.

---

# 8. Graph related response

`GET /api/knowledge/graph/nodes/{nodeId}/related?depth=1&limit=20`

Validation:

```text
depth default 1; max 2
limit default 20; max 50
fan-out max 25 per expanded node
```

Response:

```json
{
  "root": {
    "nodeId": "uuid",
    "nodeType": "TASK",
    "sourceRefId": "uuid",
    "title": "API Integration",
    "appRoute": "/projects/project-id/tasks/task-id"
  },
  "depth": 1,
  "truncated": false,
  "nodes": [
    {
      "nodeId": "uuid-2",
      "nodeType": "TASK",
      "sourceRefId": "uuid-2",
      "title": "Authentication API",
      "appRoute": "/projects/project-id/tasks/task-id-2"
    }
  ],
  "edges": [
    {
      "edgeId": "uuid-edge",
      "edgeType": "TASK_DEPENDS_ON_TASK",
      "fromNodeId": "uuid",
      "toNodeId": "uuid-2"
    }
  ]
}
```

Inaccessible nodes/edges are omitted; omission must not leak their existence.

---

# 9. Document Hub presigned upload

These endpoints belong to existing Document Hub package/controllers and reuse existing document authorities.

## Create upload session

`POST /api/workspaces/{workspaceId}/documents/{documentId}/versions/presigned-upload`

```json
{
  "originalFileName": "requirements-v3.pdf",
  "contentType": "application/pdf",
  "fileSizeBytes": 26214400,
  "checksum": "sha256-lowercase-hex-or-current-repo-format",
  "sourceType": "UPLOAD"
}
```

Validation:

```text
DOCUMENT_VERSION_CREATE
workspace/document ownership
content type allowlist
file size <= configured 100 MiB default
checksum format follows current Document Hub convention
```

Response `201`:

```json
{
  "documentVersionId": "uuid",
  "uploadMethod": "PUT",
  "uploadUrl": "short-lived-presigned-url",
  "requiredHeaders": {
    "Content-Type": "application/pdf"
  },
  "expiresAt": "2026-07-17T02:10:00Z",
  "storageStatus": "PENDING_UPLOAD"
}
```

Do not return permanent bucket URL or credentials.

## Complete upload

`POST /api/workspaces/{workspaceId}/documents/{documentId}/versions/{versionId}/complete-upload`

```json
{
  "clientEtag": "optional-etag"
}
```

Backend must call HeadObject and verify object existence, size, content type, and configured checksum metadata policy.

Response:

```json
{
  "documentVersionId": "uuid",
  "storageProvider": "CLOUDFLARE_R2",
  "storageStatus": "AVAILABLE",
  "contentType": "application/pdf",
  "fileSizeBytes": 26214400,
  "checksum": "stored-checksum",
  "uploadCompletedAt": "2026-07-17T02:05:00Z"
}
```

Completion is idempotent. A different object state for the same idempotency key returns conflict through existing error handling.

---

# 10. Document Hub presigned download

`POST /api/workspaces/{workspaceId}/documents/{documentId}/versions/{versionId}/presigned-download`

Request:

```json
{
  "disposition": "ATTACHMENT"
}
```

Requires document/version access and `DOCUMENT_DOWNLOAD`.

Response:

```json
{
  "documentVersionId": "uuid",
  "downloadUrl": "short-lived-presigned-url",
  "expiresAt": "2026-07-17T02:10:00Z",
  "fileName": "requirements-v3.pdf",
  "contentType": "application/pdf",
  "disposition": "ATTACHMENT"
}
```

Download access must be audited before URL issuance.

---

# 11. AI tool contract

Tool code:

```text
knowledge.search
```

LLM-visible arguments:

```json
{
  "query": "string",
  "sourceTypes": ["TASK", "DOCUMENT_VERSION", "MEETING_MINUTE"],
  "topK": 20,
  "includeGraphExpansion": false
}
```

Server injected:

```text
actorId
workspaceId
projectId/current-page context when authorized
actorAclTokens
classificationClearance
correlationId
```

Result uses the search response envelope and is truncated to the orchestrator context budget.

---

# 12. Error catalog

Minimum module codes:

```text
KNOWLEDGE_QUERY_INVALID
KNOWLEDGE_SOURCE_TYPE_UNSUPPORTED
KNOWLEDGE_SOURCE_NOT_FOUND
KNOWLEDGE_SOURCE_ACCESS_DENIED
KNOWLEDGE_CHUNK_CONTENT_ACCESS_DENIED
KNOWLEDGE_RETRIEVAL_ACCESS_DENIED
KNOWLEDGE_RETRIEVAL_PROVIDER_UNAVAILABLE
KNOWLEDGE_RETRIEVAL_TIMEOUT
KNOWLEDGE_INDEX_JOB_NOT_FOUND
KNOWLEDGE_INDEX_JOB_CONFLICT
KNOWLEDGE_GRAPH_NODE_NOT_FOUND
KNOWLEDGE_GRAPH_LIMIT_INVALID
KNOWLEDGE_EXTRACTION_UNSUPPORTED
KNOWLEDGE_EXTRACTION_EMPTY_OR_SCANNED
DOCUMENT_STORAGE_UPLOAD_NOT_FOUND
DOCUMENT_STORAGE_UPLOAD_NOT_COMPLETE
DOCUMENT_STORAGE_OBJECT_MISMATCH
DOCUMENT_STORAGE_PROVIDER_UNAVAILABLE
```

Use existing module-error catalog and AppException mapping conventions.
