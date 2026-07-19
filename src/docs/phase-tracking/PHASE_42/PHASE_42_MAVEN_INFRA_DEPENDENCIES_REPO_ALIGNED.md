# Phase 42 — Maven and Runtime Infrastructure Dependencies — Repository-Aligned

> Status: Accepted  
> Goal: add only dependencies required for contextual chat; do not introduce an alternate reactive stack.

---

# 1. Required baseline

Expected existing dependencies:

```text
spring-boot-starter-web
spring-boot-starter-validation
spring-boot-starter-data-jpa
postgresql driver
Flyway
existing security/IAM stack
existing outbox/event registry
existing Phase 07 AI provider abstraction
existing Phase 41 Knowledge/AiTool contracts
```

`SseEmitter` is provided by Spring MVC through `spring-webmvc`; no dedicated SSE library is required.

---

# 2. Explicit dependency rules

```text
- Do not add spring-boot-starter-webflux solely for Phase 42.
- Do not add a second LLM SDK when Phase 07 already provides an adapter.
- Do not import Elasticsearch client in modules/aiassistant.
- Do not import AWS/Cloudflare/MinIO storage SDK in modules/aiassistant.
- Do not introduce Kafka/Redis PubSub solely for Phase 42 SSE.
- Reuse the existing JSON/Jackson stack.
- Reuse existing resilience/metrics/tracing libraries when present.
```

Optional only when absent and approved by repository convention:

```text
- a JSON Schema validator for structured LLM/tool output;
- Resilience4j integration for provider/tool timeout and circuit breaker;
- Micrometer/OpenTelemetry instrumentation already standardized by the repo.
```

---

# 3. Executor configuration

Use an existing application executor if it supports bounded async work. Otherwise add a dedicated bean:

```text
bean name: aiAssistantExecutor
core pool: 4
max pool: 16
queue capacity: 100
thread prefix: ai-assistant-
shutdown: wait for tasks using repository-standard timeout
```

All values must be configuration-backed. Production tuning is environment-specific.

Long provider calls must not execute on servlet request threads after the `202` response.

---

# 4. Scheduler requirements

Reuse the existing scheduler for:

```text
SSE heartbeat
stream-event purge
retention purge
stale running-message reconciliation
memory summarization jobs
quota reconciliation if needed
```

Do not create one scheduler/thread per conversation.

---

# 5. Configuration keys

Representative prefix:

```yaml
scopery:
  ai-assistant:
    prompt-profile-code: SCOPERY_CONTEXTUAL_ASSISTANT_V1
    emitter-timeout: 180s
    heartbeat-interval: 15s
    stream-event-retention: 24h
    conversation-retention: 180d
    deleted-purge-delay: 30d
    tool-trace-retention: 30d
    max-user-message-chars: 8000
    max-messages-per-conversation: 500
    max-input-tokens: 24000
    max-output-tokens: 2000
    max-evidence-chunks: 8
    max-active-streams-per-user: 2
    max-turns-per-user-per-day: 200
    max-tokens-per-user-per-day: 500000
    memory:
      trigger-turns: 20
      trigger-unsummarized-tokens: 12000
      keep-latest-messages: 8
      max-summary-tokens: 2000
```

Secrets and provider credentials remain under the existing Phase 07 provider configuration.

---

# 6. Required dependency/runtime tests

```text
applicationStartsWithoutWebFlux
sseEmitterAvailableFromMvcStack
aiAssistantExecutorIsBounded
aiAssistantModuleHasNoElasticsearchOrStorageSdkImports
providerUsesExistingPhase07Port
knowledgeSearchUsesExistingAiToolRegistry
configurationDefaultsBindSuccessfully
