# SCOPERY AI COPILOT — END-TO-END ARCHITECTURE FLOW

> Applies to Phase 41–45. This document summarizes the locked cross-phase call flow and technology decisions. Detailed requirements remain in each phase document.

## 1. Locked stack

```text
Backend: Java 21 + Spring Boot 3.x
Database: PostgreSQL + JPA/Hibernate + Flyway
Search: Elasticsearch 8.x, BM25 + dense_vector/KNN + hybrid fusion/reranking
Local file storage: MinIO
Production file storage: Cloudflare R2
Storage protocol: S3-compatible API
Chat streaming: SSE
Agent execution streaming: WebSocket
Distributed realtime coordination: Redis Pub/Sub or Redis Streams
Reliability: Resilience4j
Observability: Micrometer + OpenTelemetry + Prometheus/Grafana-compatible dashboards
```

## 2. General read-only AI chat flow

```mermaid
flowchart TD
    U[User question] --> P[Persist USER message]
    P --> C[Build authenticated context]
    C --> I[Intent classification]
    I --> T[Registered searchKnowledge tool]
    T --> F[Mandatory IAM and metadata filters]
    F --> B[Elasticsearch BM25]
    F --> V[Elasticsearch KNN vector]
    F --> G[Bounded knowledge graph expansion]
    B --> M[Merge deduplicate and RRF]
    V --> M
    G --> M
    M --> R[Rerank and mask fields]
    R --> E[Evidence and citations]
    E --> L[LLM grounded response]
    L --> Q[Validate citations policy and output]
    Q --> A[Persist ASSISTANT/tool trace]
    A --> S[Stream through SSE]
```

## 3. File-to-RAG flow

```mermaid
flowchart LR
    UI[Frontend] -->|Create upload session| BE[Backend]
    BE -->|Presigned URL| UI
    UI -->|Upload bytes| OS[MinIO local / R2 production]
    UI -->|Complete upload| BE
    BE --> PG[(PostgreSQL metadata)]
    BE --> OUT[Outbox]
    OUT --> EX[Extract normalize redact chunk]
    EX --> EMB[Embedding provider]
    EX --> ES[(Elasticsearch)]
    EMB --> ES
```

## 4. Agentic action flow

```mermaid
flowchart TD
    U[User request] --> R[AiActionRequest]
    R --> P[Structured Action Plan]
    P --> D[Dry-run validation]
    D --> V[Preview diff risk]
    V --> C{Confirmation required?}
    C -->|Yes| H[Human confirmation bound to plan hash]
    C -->|No| X[Final revalidation]
    H --> X
    X --> G[AiToolGateway]
    G --> A[Application Action execute Command]
    A --> DB[(PostgreSQL durable state)]
    A --> O[Audit and Outbox]
    DB --> W[WebSocket progress]
```

## 5. Source-of-truth rules

```text
PostgreSQL = business, conversation and execution source of truth.
Cloudflare R2/MinIO = raw file bytes.
Elasticsearch = rebuildable search/vector index.
Redis = transient coordination/cache/rate limiting, never sole durable truth.
SSE/WebSocket = delivery channels, never source of truth.
```
