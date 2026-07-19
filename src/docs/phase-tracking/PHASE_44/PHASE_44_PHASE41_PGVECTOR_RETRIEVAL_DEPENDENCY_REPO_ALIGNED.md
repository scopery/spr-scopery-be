# Phase 44 — Phase 41 PostgreSQL/pgvector Retrieval Dependency — Repository-Aligned

> Status: **Accepted / implementation-blocking**  
> Purpose: lock the only permitted retrieval integration used while Phase 44 prepares an action plan.

---

# 1. Repository truth

```text
Migration:
V123__migrate_knowledge_chunk_to_pgvector.sql

Persistence/search columns:
knowledge_chunk.embedding vector(1536)
knowledge_chunk.search_vector tsvector

Indexes:
HNSW vector cosine index
GIN full-text index

JPA entity:
KnowledgeChunkJpaEntity
retrieval/index fields are database/index-service managed
insertable=false, updatable=false where mapped as read-only

Index writing:
PostgresKnowledgeIndexService
JdbcTemplate batch update
KnowledgeChunkIndexRecord
KnowledgeSourceIndexingService

Retrieval:
HybridRetrievalService
NamedParameterJdbcTemplate
PostgreSQL full-text candidate query
pgvector cosine candidate query
Reciprocal Rank Fusion in Java

Public AI entry point:
AiTool registry tool code knowledge.search
```

---

# 2. Allowed Phase 44 use

```text
AiActionPlanningOrchestrator
→ AiTool registry
→ knowledge.search
→ bounded permission-safe result
→ action-plan grounding/evidence
```

Allowed uses:

- identify likely target entities;
- explain why a proposed action may be relevant;
- attach bounded citations/source references to a plan;
- discover missing context that must then be loaded canonically;
- improve plan wording and evidence.

---

# 3. Forbidden coupling

`modules/aiaction` must not:

```text
query knowledge_chunk directly;
inject KnowledgeChunkJpaRepository;
inject HybridRetrievalService;
inject PostgresKnowledgeIndexService;
construct vector/tsvector SQL;
write embedding or search_vector;
reuse retrieved projection as authoritative entity state;
treat a citation as permission;
reintroduce Elasticsearch or another vector database.
```

Architecture tests should fail when a class under `modules.aiaction` imports Phase 41 persistence/index implementation packages, except the shared AiTool request/response contract allowed by the repository.

---

# 4. Security and correctness

Retrieval output is untrusted evidence for mutation planning.

Before preview and execution, Phase 44 must independently perform:

```text
current domain entity load;
workspace/project/target authorization;
expected-version comparison;
baseline/change-request guard;
business invariant validation;
tool policy validation;
sensitive-field masking.
```

Client-provided citation IDs, source IDs, workspace IDs, project IDs, permissions, or action codes do not grant access.

---

# 5. Persisted references

Phase 44 may persist only bounded references needed for traceability:

```text
retrieval request/tool-call ID;
source type;
source ID;
chunk ID when permitted;
source version;
citation label;
content hash/context hash;
short masked excerpt when policy permits.
```

Do not persist:

```text
embedding arrays;
tsvector values;
raw unrestricted retrieved payloads;
hidden chain-of-thought;
presigned URLs;
provider credentials or secrets.
```

---

# 6. Failure behavior

| Condition | Behavior |
|---|---|
| `knowledge.search` unavailable | planning may continue only when all required targets/context were explicitly provided and canonically resolvable; otherwise controlled failure |
| retrieval timeout | return retryable planning error; no domain mutation |
| no relevant evidence | return no-evidence warning or required-input error; do not fabricate |
| permission-filtered result set empty | disclose no inaccessible source metadata |
| citation becomes stale | mark plan stale and rebuild/revalidate |
| V123/pgvector unavailable | Phase 44 grounding dependency gate fails |

Suggested errors:

```text
AI_ACTION_GROUNDING_UNAVAILABLE
AI_ACTION_GROUNDING_TIMEOUT
AI_ACTION_GROUNDING_INSUFFICIENT
AI_ACTION_GROUNDING_STALE
```

Reuse an existing error when the repository already has an equivalent code.

---

# 7. Tests

```text
planningGrounding_callsKnowledgeSearchThroughAiToolRegistry
planningGrounding_neverQueriesKnowledgeChunkDirectly
retrievalEvidence_doesNotGrantTargetPermission
retrievalProjection_doesNotReplaceCanonicalDomainRead
staleCitation_invalidatesPlan
knowledgeSearchUnavailable_failsWithoutMutation
phase44Package_hasNoElasticsearchReference
phase44Package_hasNoPgvectorSql
```

---

# 8. Precedence

```text
compile-safe repository implementation
→ V123 migration
→ knowledge.search live handler/API contract
→ ADR-044
→ this dependency contract
→ older Phase 44 planning text
```
