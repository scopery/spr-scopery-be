# Phase 44 — Maven & Infrastructure Dependencies — Repository-Aligned

> Status: **Accepted / implementation guidance**

---

# 1. Required dependencies

Inspect current `pom.xml` before adding duplicates.

Required capability:

```text
spring-boot-starter-websocket
spring-messaging (normally transitive)
existing Spring MVC/web starter
existing Redis starter/client
existing PostgreSQL/JPA/Flyway
existing validation/Jackson stack
existing JSON Schema validator introduced/selected for Phase 43
existing audit/outbox/idempotency/IAM modules
```

Do not introduce WebFlux solely for Phase 44.

---

# 2. WebSocket configuration

Required Spring components:

```text
@EnableWebSocketMessageBroker configuration
STOMP endpoint /api/ws/ai-actions
application prefix /app
user destination prefix /user
ChannelInterceptor for authentication/resource authorization
SimpMessagingTemplate for local delivery
AiActionRedisRealtimeBridge for cross-instance delivery
```

Do not enable a public anonymous topic containing execution IDs.

---

# 3. Redis use

Redis is used only for live cross-instance fan-out and optional short-lived connection metadata.

```text
Channel namespace: scopery:ai-action:events:v1
Payload: execution event ID or safe event envelope <= 64 KiB
```

PostgreSQL remains authoritative for execution state, sequence, and replay. No execution correctness may depend on Redis delivery.

---

# 4. Async execution

MVP does not require Kafka/RabbitMQ.

Use:

```text
existing outbox wake-up mechanism when available;
or bounded Spring TaskExecutor + scheduled database dispatcher;
database worker lease and idempotency for durability.
```

Never run long execution work on the HTTP or WebSocket inbound thread.

---

# 5. Configuration keys

Semantic keys; adapt prefix to repository conventions:

```yaml
ai-action:
  enabled: true
  worker:
    concurrency: 4
    lease-seconds: 60
    renew-seconds: 20
    poll-interval-ms: 1000
  limits:
    max-plan-steps: 25
    max-plan-targets: 100
    max-concurrent-per-user-workspace: 2
  ttl:
    plan-seconds: 1800
    preview-seconds: 900
    confirmation-seconds: 600
    realtime-event-days: 7
  websocket:
    endpoint: /api/ws/ai-actions
    heartbeat-ms: 15000
    max-frame-bytes: 65536
  redis:
    channel: scopery:ai-action:events:v1
```

Locked policy seed values remain authoritative defaults; configuration may only lower limits unless explicitly governed.

---

# 6. No new infrastructure

Not required in Phase 44 MVP:

```text
Kafka
RabbitMQ
Temporal/Camunda
WebFlux/RSocket
new database
new vector store
new object storage
```


---

# 7. Phase 41 retrieval dependency — no new search infrastructure

Phase 44 inherits the already-implemented PostgreSQL/pgvector retrieval stack through `knowledge.search`.

Phase 44 must not add:

```text
Elasticsearch Java client
Elasticsearch service/container/volume
Elasticsearch configuration keys
another vector database
another pgvector repository/query implementation
another knowledge index writer
```

Phase 44 does not need a new pgvector Java dependency solely for action planning. `modules/aiaction` consumes the bounded AiTool response contract and remains independent of vector column mapping.
