# Phase 41 — Source Adapter and Extraction Contracts — Repository-Aligned

> Status: Accepted / implementation-blocking

# 1. Common snapshot

```java
public record KnowledgeSourceSnapshot(
    UUID workspaceId,
    UUID projectId,
    KnowledgeSourceType sourceType,
    UUID sourceRefId,
    UUID sourceVersionRefId,
    String title,
    String language,
    String classification,
    String normalizedText,
    Map<String, Object> structuredMetadata,
    List<String> aclTokens,
    String sourceAccessVersion,
    String appRoute,
    Instant sourceUpdatedAt
) {}
```

If a source does not have a UUID version row, create a stable UUID source-version identity using the repository-approved UUIDv5/name-based utility from `(sourceType, sourceId, optimisticVersion/contentHash)`. Do not use a random UUID on each reindex.

# 2. Task adapter

Canonical sections in order:

```text
Title
Status
Description
Acceptance Criteria
Blocker
Dependencies
Assignment
Dates
```

Omit empty sections. Do not include fields the indexing service is not authorized to read.

Metadata minimum:

```json
{
  "status": "BLOCKED",
  "assigneeUserId": "uuid-or-null",
  "wbsNodeId": "uuid-or-null",
  "dueDate": "date-or-null",
  "dependencyIds": ["uuid"]
}
```

# 3. DocumentVersion adapter

Load metadata from the real `documenthub_version` aggregate/repository, not raw cross-module table access when a module service/port exists.

Storage availability requirement:

```text
storage_status = AVAILABLE
storage_key not null
```

Extraction:

```text
PDF → PDFBox text layer
DOCX → POI XWPF paragraphs/tables in document order
TXT → UTF-8
Markdown → UTF-8 + heading path
```

No OCR. No XLSX/PPTX in MVP.

# 4. MeetingMinute adapter

Canonical sections:

```text
Meeting title/date
Agenda
Discussion summary
Decisions
Action items with owner/due date
Referenced tasks
```

Exclude internal/private notes not permitted by the minute visibility policy.

# 5. Permission mapping

Every adapter returns sorted, unique ACL tokens. The indexing service calculates the signature centrally; adapters must not implement divergent hash logic.

# 6. Outbox/index idempotency

Idempotency key:

```text
knowledge-index:<sourceType>:<sourceRefId>:<sourceVersionRefId>:<chunkStrategy>:<embeddingProfile>
```

A repeated event with identical source version and content hash must not duplicate projection, chunk, embedding, graph, or ES documents.

# 7. Tests

```text
taskAdapter_sameVersion_reproducesSnapshot
taskAdapter_unauthorizedField_omitted
documentAdapter_pdfText_extracts
documentAdapter_docx_extracts
documentAdapter_txtMarkdown_extracts
documentAdapter_scannedPdf_returnsExplicitUnsupportedState
documentAdapter_storageNotAvailable_doesNotIndex
meetingMinuteAdapter_privateNote_omitted
allAdapters_aclTokens_sortedUnique
allAdapters_sourceVersionIdentity_stable
```
