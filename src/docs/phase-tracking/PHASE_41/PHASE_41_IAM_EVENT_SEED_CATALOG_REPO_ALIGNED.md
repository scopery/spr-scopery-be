# Phase 41 — IAM and Event Registry Seed Catalog — Repository-Aligned

> Status: Accepted  
> Implementation mechanism: existing idempotent initializer/seeder APIs  
> Do not invent IAM/Event table names or raw SQL without inspecting current repository schema.

# 1. Initializer classes

```text
KnowledgePermissionInitializer
KnowledgeEventDefinitionInitializer
```

Use the same interfaces, transaction behavior, ordering, and idempotency pattern as existing module initializers.

# 2. New Knowledge authorities

| Authority | Purpose | Default scope |
|---|---|---|
| KNOWLEDGE_SEARCH | execute permission-aware hybrid retrieval | workspace/project |
| KNOWLEDGE_SOURCE_VIEW | view indexed source metadata | workspace/project |
| KNOWLEDGE_CHUNK_VIEW_CONTENT | view raw normalized chunk text | restricted admin/debug |
| KNOWLEDGE_REINDEX_SOURCE | enqueue one-source reindex | project admin |
| KNOWLEDGE_REINDEX_PROJECT | enqueue project reindex | project admin |
| KNOWLEDGE_INDEX_STATUS_VIEW | view index/job health | workspace admin/ops |
| KNOWLEDGE_RETRIEVAL_DEBUG | view redacted rank/debug data | system/workspace admin |
| KNOWLEDGE_GRAPH_VIEW | view permission-filtered related nodes | workspace/project |

Reuse existing Document Hub authorities for presigned storage:

```text
DOCUMENT_VERSION_CREATE
DOCUMENT_VERSION_VIEW
DOCUMENT_DOWNLOAD
DOCUMENT_CONFIDENTIAL_VIEW / DOCUMENT_RESTRICTED_VIEW where applicable
```

Do not create duplicate storage-specific IAM rights unless the current IAM model requires one.

# 3. Knowledge-owned event definitions

Source system:

```text
SCOPERY_KNOWLEDGE
```

| Code | eventKey | Required variables |
|---|---|---|
| KNOWLEDGE_SOURCE_DISCOVERED | knowledge.source_discovered | actor.userId, workspace.id, project.id, knowledgeSource.id, source.type, source.id, source.versionId, occurredAt, traceId |
| KNOWLEDGE_SOURCE_PROJECTED | knowledge.source_projected | workspace.id, project.id, knowledgeSource.id, projection.id, projection.version, extractor.code, occurredAt, traceId |
| KNOWLEDGE_SOURCE_INDEXED | knowledge.source_indexed | workspace.id, project.id, knowledgeSource.id, chunk.count, index.name, embeddingProfile.code, occurredAt, traceId |
| KNOWLEDGE_SOURCE_INDEX_FAILED | knowledge.source_index_failed | workspace.id, project.id, knowledgeSource.id, error.code, retryable, occurredAt, traceId |
| KNOWLEDGE_SOURCE_INVALIDATED | knowledge.source_invalidated | workspace.id, project.id, knowledgeSource.id, reason.code, occurredAt, traceId |
| KNOWLEDGE_REINDEX_REQUESTED | knowledge.reindex_requested | actor.userId, workspace.id, project.id, indexJob.id, indexJob.type, occurredAt, traceId |
| KNOWLEDGE_REINDEX_COMPLETED | knowledge.reindex_completed | workspace.id, project.id, indexJob.id, processed, succeeded, failed, occurredAt, traceId |
| KNOWLEDGE_REINDEX_FAILED | knowledge.reindex_failed | workspace.id, project.id, indexJob.id, error.code, occurredAt, traceId |
| KNOWLEDGE_INDEX_ALIAS_SWITCHED | knowledge.index_alias_switched | environment, index.readAlias, index.writeAlias, index.previousGeneration, index.newGeneration, occurredAt, traceId |

Do not emit a business event for every successful retrieval. Retrieval is represented by bounded trace/audit/metrics to avoid event storms.

# 4. Existing source events consumed

## Task

```text
TASK_CREATED
TASK_UPDATED
TASK_ASSIGNED
TASK_STARTED
TASK_BLOCKED
TASK_COMPLETED
TASK_CANCELLED
TASK_ARCHIVED
TASK_DEPENDENCY_CREATED
TASK_DEPENDENCY_REMOVED
```

## Document Hub

```text
DOCUMENT_VERSION_CREATED
DOCUMENT_VERSION_APPROVED
DOCUMENT_VERSION_MARKED_CURRENT
DOCUMENT_VERSION_ARCHIVED
DOCUMENT_ARCHIVED
DOCUMENT_DELETED_SOFT
```

If repository currently uses `DOCUMENT_VERSION_UPLOADED`, map it as storage-available/create input instead of creating a duplicate code.

## Meeting Minute

Implementation must inspect Phase 31's actual event codes and map them in `MeetingMinuteIndexEventMapper`. Required semantic triggers:

```text
minute created
minute updated
minute approved/finalized
minute archived/deleted
minute-task reference changed
```

# 5. Event variable safety

Never include:

```text
raw document text
raw chunk text
embedding vector
ACL token list
provider API response
presigned URL
storage credentials
full extraction exception stack
```

# 6. Required tests

```text
knowledgePermissionInitializer_firstRun_createsAuthorities
knowledgePermissionInitializer_secondRun_noDuplicates
knowledgeEventInitializer_firstRun_createsDefinitionsAndVariables
knowledgeEventInitializer_secondRun_noDuplicates
knowledgeEvents_haveStableSourceSystemAndEventKey
knowledgeEventVariables_doNotExposeContentSecrets
sourceEventMapper_taskEvents_mapDeterministically
sourceEventMapper_documentEvents_mapDeterministically
sourceEventMapper_meetingEvents_mapToActualRegistryCodes
```
