# Scopery AI Agent Module

**Technical Design Document**

| **Item** | **Value** |
|---|---|
| Document Type | Technical Design Document |
| Application | Scopery Backend |
| Module / Bounded Context | AI Agent & Event Configuration Management |
| Architecture Style | DDD-oriented Modular Monolith |
| Backend Stack | Java 21, Spring Boot, PostgreSQL, Redis, Flyway |
| Frontend Stack | Next.js, TypeScript, Tailwind CSS, shadcn/ui |
| Status | Draft for technical alignment |

---

# Table of Contents

- [1. Purpose and Scope](#1-purpose-and-scope)
- [2. Final Technical Stack](#2-final-technical-stack)
- [3. Architecture Overview](#3-architecture-overview)
- [4. Backend Package Structure](#4-backend-package-structure)
- [5. AI Agent Module Structure](#5-ai-agent-module-structure)
- [6. DDD Layer Responsibilities](#6-ddd-layer-responsibilities)
- [7. Request, Command, Query and Response Rules](#7-request-command-query-and-response-rules)
- [8. Repository Pattern: Domain vs Infrastructure](#8-repository-pattern-domain-vs-infrastructure)
- [9. Provider Sub-module Example](#9-provider-sub-module-example)
- [10. Database and Migration Strategy](#10-database-and-migration-strategy)
- [11. API Naming Conventions](#11-api-naming-conventions)
- [12. AI Provider Integration Strategy](#12-ai-provider-integration-strategy)
- [13. Usage Limit and Rate Limit Strategy](#13-usage-limit-and-rate-limit-strategy)
- [14. Security, Audit and Error Handling](#14-security-audit-and-error-handling)
- [15. Frontend Structure](#15-frontend-structure)
- [16. Local Development Setup](#16-local-development-setup)
- [17. Implementation Roadmap](#17-implementation-roadmap)
- [Appendix A - Recommended First Coding Phase](#appendix-a---recommended-first-coding-phase)
- [Appendix B - Agent Implementation Prompt Rules](#appendix-b---agent-implementation-prompt-rules)

---

# 1. Purpose and Scope

This technical design document defines the recommended implementation structure for the **AI Agent & Event Configuration Management** module inside the larger **Scopery** application.

The AI Agent module allows administrators to configure AI providers, models, deployments, agents, prompt versions, event bindings, usage limits and execution logs.

The design follows a **DDD-oriented modular monolith** approach. The Scopery backend may contain multiple business modules, and AI Agent is only one bounded context inside the application. Therefore, AI Agent-specific features must be placed under:

```text
modules/aiagent/
```

Application-wide reusable concerns such as common response wrappers, security, logging, external integrations and bootstrap data should stay outside the AI Agent module.

## In Scope

| **Scope** | **Description** |
|---|---|
| Provider Management | Manage AI providers such as OpenAI, Google Gemini, Anthropic Claude or internal providers. |
| AI Model Management | Manage models under each provider and their capabilities. |
| Model Deployment | Manage model versions or deployments per environment. |
| Agent Management | Manage business agents and their default behavior. |
| Prompt Versioning | Manage prompt templates and prompt versions. |
| Event Configuration | Bind application events to agents, prompts and model deployments. |
| Usage Policy | Configure max token, max request, rate limit and nullable limit behavior. |
| Execution Logging | Track model, prompt, token, status, error and response time for each AI execution. |
| Playground | Allow authorized users to test an agent/prompt/model setup before activation. |

## Out of Scope for Initial Backend Foundation

| **Out of Scope** | **Reason** |
|---|---|
| Full frontend implementation | Implement after backend APIs are stable. |
| Spring Security / SSO | Add after core module structure and initial CRUD APIs are working. |
| Production AI provider calls | Add after Provider, Model, Deployment, Prompt and Event Configuration are stable. |
| Advanced approval workflow | Add after prompt/event activation rules are finalized. |

---

# 2. Final Technical Stack

| **Layer** | **Selected Technology** | **Reason** |
|---|---|---|
| Frontend | Next.js + TypeScript | Suitable for admin UI, routing, form-heavy pages and maintainable frontend structure. |
| UI | Tailwind CSS + shadcn/ui | Fast component development with consistent styling. |
| Backend | Java 21 + Spring Boot | Enterprise-friendly backend for configuration, audit, security and transaction handling. |
| Database | PostgreSQL | Relational data is suitable for provider, model, prompt, event and execution relationships. |
| Cache / Rate Limit | Redis | Fast counters for request and token limits by user, event and time window. |
| Migration | Flyway | Versioned database migration scripts. |
| Local Runtime | Docker Compose | Consistent local setup for PostgreSQL, Redis, API and web app. |
| Build Tool | Maven | Standard Spring Boot build and dependency management. |

---

# 3. Architecture Overview

The system should be implemented as a modular monolith first. This keeps development simple while preserving clear module boundaries.

AI Agent is a bounded context inside the larger Scopery application.

```text
Next.js Admin UI
        |
        v
Spring Boot REST API
        |
        v
modules/aiagent Application Services / Use Cases
        |
        v
AI Agent Domain Models and Repository Contracts
        |
        v
Infrastructure Adapters
        |
        +-- PostgreSQL
        +-- Redis
        +-- OpenAI / Gemini / Claude APIs
```

## Key Architecture Rules

- The AI Agent feature set must live under `modules/aiagent/`.
- Common reusable code must not contain AI Agent-specific business logic.
- Application services orchestrate use cases but should not contain persistence-specific code.
- Domain models contain business rules and should not depend on Spring Web or JPA APIs.
- Infrastructure implements technical details such as JPA, Redis and external API clients.
- Controllers convert API requests into application commands or queries.
- JPA entities must not be returned directly to frontend.
- API request objects must not be used as domain objects.
- Command objects represent state-changing use cases.
- Query objects represent read/search use cases.
- The backend package name should represent the application, not the AI Agent module only.

---

# 4. Backend Package Structure

Because AI Agent is only one module inside the larger application, the root package should represent the application name.

Recommended root package:

```text
com.company.scopery
```

> Replace `company` with the real organization package if needed.

## Final Backend Package Structure

```text
apps/api/src/main/java/com/company/scopery/
├── ScoperyApplication.java
│
├── modules/
│   └── aiagent/
│       ├── provider/
│       ├── aimodel/
│       ├── deployment/
│       ├── capability/
│       ├── agent/
│       ├── prompt/
│       ├── eventconfig/
│       ├── usagepolicy/
│       ├── execution/
│       └── playground/
│
├── common/
│   ├── response/
│   ├── exception/
│   ├── pagination/
│   ├── validation/
│   └── audit/
│
├── integration/
│   ├── ai/
│   └── redis/
│
├── platform/
│   ├── config/
│   ├── security/
│   ├── web/
│   └── logging/
│
└── bootstrap/
    └── seed/
```

## Package Purpose

| **Package** | **Purpose** | **Allowed Content** |
|---|---|---|
| `modules` | Business modules of the full application | AI Agent and other future Scopery modules. |
| `modules/aiagent` | AI Agent bounded context | Provider, model, deployment, agent, prompt, event config, usage policy, execution. |
| `common` | Generic reusable cross-cutting code | Generic response wrappers, base exceptions, pagination, audit base fields, generic validators. |
| `integration` | External systems | AI clients, Redis adapter, storage/email clients if needed. |
| `platform` | Technical foundation | Security, CORS, OpenAPI, request context, logging filters. |
| `bootstrap` | Initial data | Seed providers, seed models, seed demo configuration. |

## Important Naming Decision

Do not use this package structure if AI Agent is only one module inside the app:

```text
com.company.aiagent
```

Use this instead:

```text
com.company.scopery.modules.aiagent
```

Reason:

```text
provider
model
deployment
execution
```

are generic names. Without the `aiagent` wrapper, these names may conflict with other modules in the larger application.

---

# 5. AI Agent Module Structure

Inside `modules/aiagent/`, each AI Agent sub-module should follow the same DDD internal structure.

```text
modules/aiagent/provider/
├── api/
├── application/
│   ├── command/
│   ├── query/
│   └── response/
├── domain/
└── infrastructure/
    ├── persistence/
    └── mapper/
```

## AI Agent Sub-modules

| **Sub-module** | **Purpose** |
|---|---|
| `provider` | Manage AI providers such as OpenAI, Google, Anthropic. |
| `aimodel` | Manage AI model catalog under each provider. |
| `deployment` | Manage concrete model deployment/version per environment. |
| `capability` | Manage provider/model parameter capability such as temperature and max tokens. |
| `agent` | Manage AI agent profiles and business purpose. |
| `prompt` | Manage prompt templates and prompt versions. |
| `eventconfig` | Map application events to agent, prompt, model deployment and usage policy. |
| `usagepolicy` | Manage max token, max request, nullable limit and rate limit policy. |
| `execution` | Store AI execution history, status, error and token/cost summary. |
| `playground` | Test agent/prompt/model configuration before production activation. |

---

# 6. DDD Layer Responsibilities

Each business sub-module should repeat the same internal structure. This helps developers find files quickly and keeps each sub-module self-contained.

```text
modules/aiagent/provider/
├── api/
├── application/
│   ├── command/
│   ├── query/
│   └── response/
├── domain/
└── infrastructure/
    ├── persistence/
    └── mapper/
```

| **Layer** | **Responsibility** | **Example** |
|---|---|---|
| `api` | Expose REST endpoints and map HTTP request to command/query. | `ProviderController`, `CreateProviderRequest` |
| `application` | Orchestrate use cases and transaction boundaries. | `CreateProviderCommand`, `ProviderApplicationService` |
| `domain` | Represent business concepts, rules and repository contracts. | `Provider`, `ProviderCode`, `ProviderRepository` |
| `infrastructure` | Implement persistence and technical adapters. | `JpaProviderRepository`, `ProviderJpaEntity`, `ProviderPersistenceMapper` |

## Layer Dependency Rule

Allowed direction:

```text
api -> application -> domain
infrastructure -> domain
application -> domain repository interface
```

Not allowed:

```text
domain -> infrastructure
domain -> JPA
domain -> Spring Web
controller -> repository directly
controller -> business logic
```

---

# 7. Request, Command, Query and Response Rules

The project should distinguish external API objects from internal application use-case objects.

| **Object Type** | **Layer** | **Purpose** | **Example** |
|---|---|---|---|
| Request | `api/request` | Data received from HTTP request. | `CreateProviderRequest`, `SearchProviderRequest` |
| Command | `application/command` | Internal object representing an action that changes system state. | `CreateProviderCommand`, `ActivateEventConfigCommand` |
| Query | `application/query` | Internal object representing a read/search action. | `SearchProviderQuery`, `GetProviderDetailQuery` |
| Response | `application/response` | Data returned to frontend. | `ProviderResponse`, `EventConfigDetailResponse` |

## Command Use Case Flow

```text
POST /api/v1/ai-agent/providers
        |
        v
CreateProviderRequest
        |
        v
CreateProviderCommand
        |
        v
ProviderApplicationService.createProvider(command)
        |
        v
Provider domain object
        |
        v
ProviderRepository.save(provider)
        |
        v
ProviderResponse
```

## Naming Rules

```text
api/request/
├── CreateProviderRequest.java
├── UpdateProviderRequest.java
└── SearchProviderRequest.java

application/command/
├── CreateProviderCommand.java
├── UpdateProviderCommand.java
└── ActivateProviderCommand.java

application/query/
├── SearchProviderQuery.java
└── GetProviderDetailQuery.java

application/response/
├── ProviderResponse.java
└── ProviderDetailResponse.java
```

## Practical Rule

Use `Command` for:

```text
create
update
delete
activate
deactivate
approve
submit
run
execute
```

Use `Query` for:

```text
get detail
search
list
filter
export preview
```

---

# 8. Repository Pattern: Domain vs Infrastructure

The repository is split into a domain contract and an infrastructure implementation. This is the key design choice that keeps business logic independent from database technology.

| **Repository Type** | **Location** | **Purpose** | **Knows About** |
|---|---|---|---|
| Domain Repository | `domain/ProviderRepository.java` | Defines what the business needs: save, find, check existence. | Domain objects only. No JPA, no SQL, no PostgreSQL. |
| Infrastructure Repository | `infrastructure/persistence/JpaProviderRepository.java` | Implements the domain contract using JPA/PostgreSQL. | JPA entity, Spring Data repository, mapper, persistence details. |
| Spring Data Repository | `infrastructure/persistence/SpringDataProviderJpaRepository.java` | Technical tool that extends `JpaRepository`. | JPA entity and database query methods. |

## Example

```java
// Domain contract
public interface ProviderRepository {
    Provider save(Provider provider);
    Optional<Provider> findById(UUID id);
    boolean existsByCode(ProviderCode code);
}
```

```java
// Infrastructure implementation
@Repository
public class JpaProviderRepository implements ProviderRepository {

    private final SpringDataProviderJpaRepository springDataRepository;
    private final ProviderPersistenceMapper mapper;

    public Provider save(Provider provider) {
        ProviderJpaEntity entity = mapper.toJpaEntity(provider);
        ProviderJpaEntity saved = springDataRepository.save(entity);
        return mapper.toDomain(saved);
    }
}
```

## Main Benefit

```text
Domain repository = what the business needs.
Infrastructure repository = how the system does it technically.
```

This allows application services to use a repository without knowing whether the data is stored through JPA, native SQL, cache, external API or test in-memory implementation.

---

# 9. Provider Sub-module Example

The Provider sub-module is the first recommended sub-module to implement because other AI Agent sub-modules depend on it.

Providers include OpenAI, Google, Anthropic or internal AI services.

## 9.1 Module Structure

```text
modules/aiagent/provider/
├── api/
│   ├── ProviderController.java
│   └── request/
│       ├── CreateProviderRequest.java
│       ├── UpdateProviderRequest.java
│       └── SearchProviderRequest.java
│
├── application/
│   ├── ProviderApplicationService.java
│   ├── command/
│   │   ├── CreateProviderCommand.java
│   │   ├── UpdateProviderCommand.java
│   │   ├── ActivateProviderCommand.java
│   │   └── DeactivateProviderCommand.java
│   ├── query/
│   │   ├── SearchProviderQuery.java
│   │   └── GetProviderDetailQuery.java
│   └── response/
│       ├── ProviderResponse.java
│       └── ProviderDetailResponse.java
│
├── domain/
│   ├── Provider.java
│   ├── ProviderCode.java
│   ├── ProviderStatus.java
│   └── ProviderRepository.java
│
└── infrastructure/
    ├── persistence/
    │   ├── ProviderJpaEntity.java
    │   ├── SpringDataProviderJpaRepository.java
    │   └── JpaProviderRepository.java
    └── mapper/
        └── ProviderPersistenceMapper.java
```

## 9.2 Provider Business Fields

| **Field** | **Description** |
|---|---|
| `id` | Unique provider ID. |
| `name` | Display name, e.g. OpenAI. |
| `code` | Unique provider code, e.g. OPENAI. |
| `type` | Provider type, e.g. LLM, OCR, EMBEDDING. |
| `apiBaseUrl` | Base API URL. |
| `description` | Optional provider description. |
| `status` | ACTIVE, INACTIVE, DEPRECATED. |
| `createdAt` | Created timestamp. |
| `updatedAt` | Updated timestamp. |

## 9.3 Business Rules

- Provider code must be unique.
- Provider code should be normalized to uppercase.
- Provider code only allows uppercase letters, numbers and underscore.
- Active provider must have API base URL.
- Provider is created with ACTIVE status by default.
- Deprecated provider cannot be activated again.
- Deactivated provider can be activated again if it has a valid API base URL.

## 9.4 Example Application Service

```java
@Service
public class ProviderApplicationService {

    private final ProviderRepository providerRepository;

    @Transactional
    public ProviderResponse createProvider(CreateProviderCommand command) {
        ProviderCode providerCode = ProviderCode.of(command.code());

        if (providerRepository.existsByCode(providerCode)) {
            throw new BusinessException("Provider code already exists: " + providerCode.value());
        }

        Provider provider = Provider.create(
            command.name(),
            providerCode,
            command.type(),
            command.apiBaseUrl(),
            command.description()
        );

        Provider saved = providerRepository.save(provider);

        return ProviderResponse.from(saved);
    }
}
```

---

# 10. Database and Migration Strategy

Flyway should be used for database schema changes. Each schema change must be committed as a versioned SQL migration file.

```text
resources/db/migration/
├── V1__create_ai_provider_table.sql
├── V2__create_ai_model_table.sql
├── V3__create_ai_model_deployment_table.sql
├── V4__create_ai_model_parameter_capability_table.sql
├── V5__create_ai_agent_table.sql
├── V6__create_prompt_tables.sql
├── V7__create_event_config_tables.sql
├── V8__create_usage_policy_table.sql
└── V9__create_execution_log_table.sql
```

## Main Tables

| **Table** | **Purpose** |
|---|---|
| `ai_provider` | Stores AI providers such as OpenAI, Google, Anthropic. |
| `ai_model` | Stores model catalog under each provider. |
| `ai_model_deployment` | Stores model version/deployment per environment. |
| `ai_model_parameter_capability` | Stores supported parameters such as temperature and max output tokens. |
| `ai_agent` | Stores AI agent profile and business purpose. |
| `ai_prompt_template` | Stores prompt template metadata. |
| `ai_prompt_version` | Stores versioned prompt content and status. |
| `ai_event_config` | Stores event to agent/model/prompt binding. |
| `ai_usage_policy` | Stores max token, max request and nullable limit rules. |
| `ai_execution_log` | Stores every AI execution summary, status and token usage. |

## Nullable Limit Rule

Maximum usage fields can be null. If a max field is null, the system must treat it as no limit for that specific rule and skip validation.

```text
max_token_per_run = null       -> no token limit per execution
max_request_per_user = null    -> no request limit per user
max_request_per_event = null   -> no request limit for this event
max_monthly_cost = null        -> no monthly cost limit
```

---

# 11. API Naming Conventions

Since AI Agent is a module in a larger application, API routes should include the module prefix.

Recommended pattern:

```text
/api/v1/ai-agent/{resource}
```

| **AI Agent Resource** | **Endpoint Pattern** |
|---|---|
| Provider | `/api/v1/ai-agent/providers` |
| AI Model | `/api/v1/ai-agent/models` |
| Model Deployment | `/api/v1/ai-agent/model-deployments` |
| Model Parameter Capability | `/api/v1/ai-agent/model-parameter-capabilities` |
| Agent | `/api/v1/ai-agent/agents` |
| Prompt Template | `/api/v1/ai-agent/prompt-templates` |
| Prompt Version | `/api/v1/ai-agent/prompt-versions` |
| Event Configuration | `/api/v1/ai-agent/event-configs` |
| Usage Policy | `/api/v1/ai-agent/usage-policies` |
| Execution Log | `/api/v1/ai-agent/execution-logs` |
| Playground | `/api/v1/ai-agent/playground/run` |

## Provider Endpoints

```text
POST   /api/v1/ai-agent/providers
GET    /api/v1/ai-agent/providers
GET    /api/v1/ai-agent/providers/{id}
PUT    /api/v1/ai-agent/providers/{id}
PATCH  /api/v1/ai-agent/providers/{id}/activate
PATCH  /api/v1/ai-agent/providers/{id}/deactivate
```

CRUD endpoints should remain predictable. Business actions should be expressed with explicit action endpoints only when the action cannot be represented as simple CRUD.

---

# 12. AI Provider Integration Strategy

External AI provider calls should not be placed directly inside event configuration services.

The system should use a provider client interface and provider-specific implementations.

```text
integration/ai/
├── AiProviderClient.java
├── AiRequest.java
├── AiResponse.java
├── openai/
│   └── OpenAiClient.java
├── gemini/
│   └── GeminiClient.java
└── claude/
    └── ClaudeClient.java
```

## Common AI Client Interface

```java
public interface AiProviderClient {
    String providerCode();
    AiResponse execute(AiRequest request);
}
```

## Common AI Request

```java
public record AiRequest(
    String model,
    String prompt,
    BigDecimal temperature,
    Integer maxOutputTokens,
    Map<String, Object> variables
) {}
```

## Common AI Response

```java
public record AiResponse(
    String outputText,
    Integer inputTokens,
    Integer outputTokens,
    String rawResponse
) {}
```

## Provider Parameter Capability

Each provider/model can define supported generation parameters. For example, temperature may be supported by some providers or model versions but restricted by others.

| **Field** | **Description** |
|---|---|
| `provider_code` | Provider such as OPENAI, GOOGLE, ANTHROPIC. |
| `model_code` | Model code under the provider. |
| `parameter_name` | Business name such as temperature or maxOutputTokens. |
| `api_parameter_key` | Actual provider API key such as temperature or generationConfig.temperature. |
| `supported` | YES, NO or CONDITIONAL. |
| `min_value` | Minimum supported value. |
| `max_value` | Maximum supported value. |
| `if_null_behavior` | Usually do not send parameter and use provider/model default. |

---

# 13. Usage Limit and Rate Limit Strategy

PostgreSQL stores official execution logs. Redis should be used for fast real-time counters before AI execution.

## Redis Counter Keys

```text
rate:user:{userId}:event:{eventCode}:hour
rate:user:{userId}:event:{eventCode}:day
rate:event:{eventCode}:day
token:user:{userId}:month
cost:office:{officeCode}:month
```

## Validation Sequence Before Execution

1. Load active event configuration.
2. Load usage policy and nullable max limits.
3. Skip validation for any max field that is null.
4. Check Redis counters for applicable limits.
5. Execute AI provider call.
6. Update Redis counters.
7. Save execution log to PostgreSQL.

---

# 14. Security, Audit and Error Handling

| **Concern** | **Implementation Direction** |
|---|---|
| Authentication | Use JWT or enterprise SSO integration. For MVP, JWT-based authentication is acceptable. |
| Authorization | Admin-only access for configuration pages; separate permissions for create, update, approve and activate. |
| Audit | Track `created_by`, `created_at`, `updated_by`, `updated_at` for configuration tables. |
| Prompt Approval | Production prompt activation should require approval if governance is needed. |
| Sensitive Data | Do not store full input/output by default if it may contain confidential data. Provide configurable retention and masking. |
| Error Handling | Use a global exception handler for validation, business and infrastructure errors. |

## Error Response Format

```json
{
  "success": false,
  "errorCode": "PROVIDER_CODE_ALREADY_EXISTS",
  "message": "Provider code already exists: OPENAI",
  "timestamp": "2026-06-13T09:00:00+07:00"
}
```

## Common Package Rule

`common/exception` may contain generic exception types such as:

```text
BusinessException
ResourceNotFoundException
ValidationException
```

However, AI Agent-specific error codes or messages should stay inside `modules/aiagent` when they are strongly tied to AI Agent business rules.

---

# 15. Frontend Structure

The frontend should be organized by feature to mirror backend business modules.

Because AI Agent is a module inside the larger application, the frontend route should also be grouped under an AI Agent section.

```text
apps/web/src/
├── app/
│   └── ai-agent/
│       ├── providers/
│       ├── models/
│       ├── deployments/
│       ├── agents/
│       ├── prompts/
│       ├── events/
│       ├── usage-policies/
│       ├── execution-logs/
│       └── playground/
│
├── components/
│   ├── layout/
│   ├── ui/
│   └── common/
│
├── features/
│   └── ai-agent/
│       ├── providers/
│       ├── models/
│       ├── deployments/
│       ├── agents/
│       ├── prompts/
│       ├── events/
│       ├── usage-policies/
│       ├── execution-logs/
│       └── playground/
│
└── lib/
    ├── api-client.ts
    ├── query-client.ts
    └── constants.ts
```

---

# 16. Local Development Setup

Recommended root structure:

```text
scopery-platform/
├── apps/
│   ├── api/
│   └── web/
├── infra/
│   └── docker-compose.yml
├── docs/
│   └── TECHNICAL.md
└── scripts/
```

## Environment Files

| **File** | **Purpose** |
|---|---|
| `.env.example` | Root environment example for ports and common variables. |
| `apps/api/.env.example` | Backend database, Redis and AI provider keys. |
| `apps/web/.env.example` | Frontend API base URL. |
| `infra/docker-compose.yml` | Local PostgreSQL, Redis and optional app services. |

## Suggested First Local Flow

```bash
cd infra
docker compose up -d postgres redis

cd ../apps/api
./mvnw spring-boot:run

cd ../web
npm install
npm run dev
```

---

# 17. Database Naming Convention

All database tables must follow a consistent naming convention based on which layer owns the table.

## Table Prefix Rules

| **Scope** | **Table Prefix** | **Example** |
|---|---|---|
| Common / platform | `app_` | `app_activity_log` |
| AI Agent module | `aiagent_` | `aiagent_provider`, `aiagent_model` |

Do not use generic names such as `provider`, `model`, `deployment`, or `execution`. These will conflict with other modules in the larger application.

## AI Agent Table Names

```text
aiagent_provider
aiagent_model
aiagent_model_deployment
aiagent_model_parameter_capability
aiagent_agent
aiagent_prompt_template
aiagent_prompt_version
aiagent_event_config
aiagent_usage_policy
aiagent_execution_log
```

## Column Naming

- Use snake_case.
- Primary key: `id`
- Foreign keys: `{entity}_id` (e.g. `provider_id`, `model_id`, `deployment_id`)

## Constraint Naming

| **Type** | **Pattern** | **Example** |
|---|---|---|
| Primary key | `pk_{table_name}` | `pk_aiagent_provider` |
| Unique | `uq_{table_name}_{column}` | `uq_aiagent_provider_code` |
| Foreign key | `fk_{table}_{referenced_table}` | `fk_aiagent_model_aiagent_provider` |
| Index | `idx_{table}_{column}` | `idx_aiagent_provider_status` |

## Flyway Migration Naming

```text
V{n}__{action}_{table_name}.sql
```

Examples:
- `V1__create_activity_log_table.sql`
- `V2__create_aiagent_provider_table.sql`
- `V3__create_aiagent_model_table.sql`

---

# 18. Swagger / OpenAPI Convention

OpenAPI configuration is placed under `platform/config/OpenApiConfig.java`.

## Local Access

| **Resource** | **URL** |
|---|---|
| Swagger UI | `http://localhost:8080/swagger-ui.html` |
| OpenAPI JSON | `http://localhost:8080/v3/api-docs` |

## Controller Annotations

Business controllers should use the following OpenAPI annotations:

```java
@Tag(name = "AI Agent - Providers", description = "Manage AI provider configurations")
@RestController
public class ProviderController {

    @Operation(summary = "Create a new AI provider")
    @PostMapping
    public ResponseEntity<ApiResponse<ProviderResponse>> createProvider(...) {}
}
```

## Rules

- Do not add authentication/security configuration to Swagger in early phases.
- Each AI Agent sub-module should use `@Tag(name = "AI Agent - {SubModule}")`.
- Swagger UI should only be active in local and development environments.

---

# 19. Implementation Roadmap

The implementation should be split into small phases so each diff is easy to review.

| **Phase** | **Scope** | **Main Output** |
|---|---|---|
| Phase 0 | Foundation setup. | Java 21, Spring Boot project, package structure, config, DB/Flyway readiness. |
| Phase 1 | AI Agent Provider sub-module. | Provider CRUD and status actions. |
| Phase 2 | AI Model sub-module. | Provider -> model hierarchy. |
| Phase 3 | Model Deployment sub-module. | Model deployment/version per environment. |
| Phase 4 | Model Parameter Capability sub-module. | Supported parameters such as temperature and max output tokens. |
| Phase 5 | Agent sub-module. | AI agent profile and default behavior. |
| Phase 6 | Prompt sub-module. | Prompt template and prompt versioning. |
| Phase 7 | Usage Policy sub-module. | Nullable max limits and rate limit configuration. |
| Phase 8 | Event Configuration sub-module. | Event -> agent -> prompt -> deployment binding. |
| Phase 9 | Execution Log sub-module. | Execution history, token, status, error and response time. |
| Phase 10 | Playground sub-module. | Test run endpoint for agent/prompt/model configuration. |
| Phase 11 | Next.js admin UI. | AI Agent configuration screens and playground. |
| Phase 12 | Security, audit, approval and hardening. | Production-ready governance controls. |

---

# Appendix A - Recommended First Coding Phase

Start with **Phase 0 - Foundation Setup**.

Do not implement Provider or any other business sub-module during Phase 0.

## Phase 0 Checklist

1. Verify project uses Java 21.
2. Verify project is a valid Spring Boot Maven project.
3. Verify backend starts on port 8080.
4. Create package structure under `com.company.scopery`.
5. Create `modules/aiagent` wrapper.
6. Create empty AI Agent sub-module folders.
7. Create `common`, `integration`, `platform`, `bootstrap` packages.
8. Create `application.yml`.
9. Add PostgreSQL, JPA and Flyway dependencies if the database setup is part of Phase 0.
10. Create `src/main/resources/db/migration/`.
11. Do not create business tables yet unless Phase 1 starts.
12. Do not implement Provider yet.
13. Do not add Spring Security yet.

## Expected Phase 0 Structure

```text
src/main/java/com/company/scopery/
├── ScoperyApplication.java
├── modules/
│   └── aiagent/
│       ├── provider/
│       │   ├── api/
│       │   ├── application/
│       │   ├── domain/
│       │   └── infrastructure/
│       ├── aimodel/
│       ├── deployment/
│       ├── capability/
│       ├── agent/
│       ├── prompt/
│       ├── eventconfig/
│       ├── usagepolicy/
│       ├── execution/
│       └── playground/
├── common/
├── integration/
├── platform/
└── bootstrap/
```

---

# Appendix B - Agent Implementation Prompt Rules

Use these rules when asking a coding agent to implement a phase.

```text
Before coding, read docs/TECHNICAL.md first.

Follow the architecture and folder structure defined in that document.

Do not redesign the architecture unless there is a clear issue. If a change is needed, explain it before applying it.

Implement only the requested phase.

Do not implement future phases early.

After implementation, provide:
1. Files created.
2. Files modified.
3. How to run the backend.
4. How to test the implemented API or setup.
5. Any assumptions made.
```

## Phase 0 Agent Prompt

```text
Before coding, please read the file docs/TECHNICAL.md first.

Current goal:
Implement Phase 0 - Foundation Setup only.

Important:
AI Agent is only one module inside the larger Scopery application.
Therefore, do not use com.company.aiagent as the root package.
Use com.company.scopery as the root package and place AI Agent under modules/aiagent.

Do not implement any business module yet.

Required package structure:

src/main/java/com/company/scopery/
├── modules/
│   └── aiagent/
│       ├── provider/
│       ├── aimodel/
│       ├── deployment/
│       ├── capability/
│       ├── agent/
│       ├── prompt/
│       ├── eventconfig/
│       ├── usagepolicy/
│       ├── execution/
│       └── playground/
├── common/
├── integration/
├── platform/
└── bootstrap/

For each AI Agent sub-module, create the base DDD folders only:
- api
- application
- domain
- infrastructure

Do not add business classes yet.

Create or update application.yml with:
- server port 8080
- application name
- PostgreSQL datasource config if DB dependencies already exist
- JPA ddl-auto=validate if JPA is already included
- Flyway enabled if Flyway is already included

Create:
src/main/resources/db/migration/

After implementation, list all files created/modified and explain how to run the backend.
```

---

# 19. AI Agent Shared Kernel

## Purpose

The shared kernel (`modules/aiagent/shared/`) centralises all cross-cutting constants, utilities, and helper components used by every AI Agent sub-module. It eliminates duplicated string literals and local helper methods that previously lived inside each application service.

## Why a Shared Kernel, Not `common/`

`common/` is platform-level infrastructure. It must not contain business-domain knowledge. Table names like `aiagent_provider`, action strings like `CREATE_AI_MODEL`, and error codes like `PROVIDER_CODE_ALREADY_EXISTS` are domain knowledge specific to the AI Agent bounded context. They belong in `modules/aiagent/shared/`, not in `common/`.

## Package Layout

```
modules/aiagent/shared/
├── constant/
│   ├── AiAgentModuleCodes.java       ← "AIAGENT" module code
│   ├── AiAgentEntityTypes.java       ← "PROVIDER", "AI_MODEL", "MODEL_DEPLOYMENT", …
│   ├── AiAgentActivityActions.java   ← "CREATE_PROVIDER", "ACTIVATE_AI_MODEL", …
│   ├── AiAgentErrorCodes.java        ← "PROVIDER_CODE_ALREADY_EXISTS", …
│   ├── AiAgentTableNames.java        ← "aiagent_provider", "aiagent_model", …
│   ├── AiAgentApiPaths.java          ← "/api/v1/ai-agent/providers", …
│   └── AiAgentSortFields.java        ← "createdAt", "updatedAt", …
├── activity/
│   └── AiAgentActivityLogger.java    ← wraps ActivityLogService with moduleCode = "AIAGENT"
└── util/
    └── AiAgentEnumParser.java        ← generic enum parsing with parseRequired / parseOptional
```

## Constants Catalog

### AiAgentTableNames

| Constant | Value |
|---|---|
| `PROVIDER` | `"aiagent_provider"` |
| `AI_MODEL` | `"aiagent_model"` |
| `MODEL_DEPLOYMENT` | `"aiagent_model_deployment"` |

Used in: `@Table(name = AiAgentTableNames.PROVIDER)` on JPA entities.

### AiAgentApiPaths

| Constant | Value |
|---|---|
| `API_V1_AI_AGENT` | `"/api/v1/ai-agent"` |
| `PROVIDERS` | `"/api/v1/ai-agent/providers"` |
| `MODELS` | `"/api/v1/ai-agent/models"` |
| `MODEL_DEPLOYMENTS` | `"/api/v1/ai-agent/model-deployments"` |

Used in: `@RequestMapping(AiAgentApiPaths.PROVIDERS)` on controllers.

### AiAgentSortFields

| Constant | Value |
|---|---|
| `CREATED_AT` | `"createdAt"` |
| `UPDATED_AT` | `"updatedAt"` |
| `NAME` | `"name"` |
| `CODE` | `"code"` |

Used in: `Sort.by(Sort.Direction.DESC, AiAgentSortFields.CREATED_AT)`.

## AiAgentActivityLogger

Wraps `ActivityLogService` with a hardcoded `moduleCode = "AIAGENT"`. Application services inject `AiAgentActivityLogger` rather than `ActivityLogService` directly, reducing per-call boilerplate.

```java
activityLogger.logSuccess(AiAgentEntityTypes.PROVIDER, saved.id(),
        AiAgentActivityActions.CREATE_PROVIDER,
        "Provider created: " + saved.code().value());
```

## AiAgentEnumParser

Generic utility for parsing enum values from strings received at the API boundary.

- `parseRequired(enumType, value, errorCode, fieldName)` — throws `ValidationException` if null/blank or unrecognised.
- `parseOptional(enumType, value, errorCode, fieldName)` — returns `null` if null/blank; throws `ValidationException` for non-blank unrecognised values (never silently ignores bad input).

The `errorCode` parameter is embedded in the exception message as `[ERRORCODE]` since `ValidationException` does not carry a separate code field.

## Bulk Update Audit Rule

JPA lifecycle callbacks (`@CreatedDate`, `@LastModifiedDate`) are **not triggered** by JPQL bulk `UPDATE` statements. Any `@Modifying` bulk update that changes row state must explicitly set `e.updatedAt = CURRENT_TIMESTAMP` in the JPQL. Example (the `clearDefaultFlags` pattern):

```java
@Modifying(clearAutomatically = true, flushAutomatically = true)
@Query("""
       UPDATE ModelDeploymentJpaEntity e
       SET e.isDefault = false,
           e.updatedAt = CURRENT_TIMESTAMP
       WHERE e.modelId = :modelId
         AND e.environment = :environment
       """)
int clearAllDefaultFlags(@Param("modelId") UUID modelId, @Param("environment") String environment);
```

Rules:
- Return `int` (affected row count), not `void`.
- `flushAutomatically = true` — flush pending entity changes before bulk SQL runs.
- `clearAutomatically = true` — evict first-level cache after bulk update.
- `updatedBy` is intentionally omitted until Spring Security is implemented (no authenticated user context available).

## DB Constraint as Final Safety Layer

Application-level uniqueness checks (e.g. `existsByCode(...)`) provide early conflict detection with human-readable error messages. DB unique constraints (and partial unique indexes, e.g. `WHERE is_default = TRUE`) provide the final guarantee. Both layers must exist — neither alone is sufficient.

## Activation DEPRECATED Guard Rule

When an activation operation can fail because the entity is already `DEPRECATED`, the application service must explicitly check the status and throw `BusinessException` with a readable message **before** calling the domain method. The domain still guards as a last resort, but `IllegalStateException` propagates to the client as a generic 422 with the exception message as-is — unacceptable for user experience. Example:

```java
if (model.status() == AiModelStatus.DEPRECATED) {
    throw new BusinessException(AiAgentErrorCodes.DEPRECATED_AI_MODEL_CANNOT_BE_ACTIVATED,
            "Deprecated AI model cannot be activated again");
}
model.activate(); // domain guard remains as defensive backstop
```

## Controlled Behavior Enums

Any field whose value must come from a fixed set of allowed options must be a domain enum, not a freeform String.

### Example: `ModelParameterIfNullBehavior`

Location: `modules/aiagent/capability/domain/ModelParameterIfNullBehavior.java`

| Value | Meaning |
|---|---|
| `DO_NOT_SEND_PARAMETER` | Omit the parameter entirely from the AI provider request when the value is null |
| `USE_PROVIDER_DEFAULT` | Let the provider apply its own default when the parameter is absent |

### Enum storage pattern

```java
// Mapper toJpaEntity — enum → String
entity.setIfNullBehavior(
    capability.ifNullBehavior() != null ? capability.ifNullBehavior().name() : null);

// Mapper toDomain — String → enum
entity.getIfNullBehavior() != null
    ? ModelParameterIfNullBehavior.valueOf(entity.getIfNullBehavior()) : null
```

The DB column stays `VARCHAR(100)`. The `.name()` value is stored. No new migration is needed when adding enum values — only when the column size needs expanding.

### Application service parsing

Request DTOs carry the raw String from the API. The service parses before calling the domain:

```java
ModelParameterIfNullBehavior ifNullBehavior = AiAgentEnumParser.parseOptional(
    ModelParameterIfNullBehavior.class, command.ifNullBehavior(),
    AiAgentErrorCodes.INVALID_MODEL_PARAMETER_IF_NULL_BEHAVIOR, "ifNullBehavior");
```

An invalid value produces a `ValidationException` with error code `INVALID_MODEL_PARAMETER_IF_NULL_BEHAVIOR`.

---

## Refactoring Rule for New Sub-modules

When adding a new AI Agent sub-module (e.g. `capability`, `agent`):

1. Add table name constant to `AiAgentTableNames`.
2. Add API path constant to `AiAgentApiPaths`.
3. Add entity type constant to `AiAgentEntityTypes`.
4. Add action constants to `AiAgentActivityActions`.
5. Add error catalog entries to `AiAgentErrorCatalog` (not to the deprecated `AiAgentErrorCodes`).
6. Add factory methods to `AiAgentExceptions`.
7. Use these constants in all JPA entities, controllers, and application services — never raw strings.

---

## Full Error Catalog Pattern (Phase 6.1)

### Architecture

```
common/exception/
  ErrorCatalog.java          ← interface: code(), defaultMessage(), httpStatus()
  AppException.java          ← generic exception carrying ErrorCatalog

modules/aiagent/shared/error/
  AiAgentErrorCatalog.java   ← enum implementing ErrorCatalog, all AI Agent errors
  AiAgentExceptions.java     ← factory methods for throwing typed AppException

platform/web/
  GlobalExceptionHandler.java ← handles AppException; must NOT import AiAgentErrorCatalog
```

### ErrorCatalog Interface

```java
public interface ErrorCatalog {
    String code();
    String defaultMessage();
    HttpStatus httpStatus();
}
```

### AppException

```java
public class AppException extends RuntimeException {
    public AppException(ErrorCatalog catalog)
    public AppException(ErrorCatalog catalog, String message)
    public AppException(ErrorCatalog catalog, String message, Map<String, Object> details)
    public AppException(ErrorCatalog catalog, Throwable cause)

    String getErrorCode()        // = catalog.code()
    HttpStatus getHttpStatus()   // = catalog.httpStatus()
    Map<String, Object> getDetails()
}
```

### AiAgentErrorCatalog (excerpt)

```java
public enum AiAgentErrorCatalog implements ErrorCatalog {
    PROVIDER_NOT_FOUND("PROVIDER_NOT_FOUND", "Provider not found", HttpStatus.NOT_FOUND),
    PROVIDER_CODE_ALREADY_EXISTS("PROVIDER_CODE_ALREADY_EXISTS", "Provider code already exists", HttpStatus.CONFLICT),
    DEPRECATED_PROVIDER_CANNOT_BE_ACTIVATED("...", "...", HttpStatus.UNPROCESSABLE_ENTITY),
    INVALID_AGENT_TYPE("...", "...", HttpStatus.BAD_REQUEST),
    // ... all AI Agent error entries
}
```

### AiAgentExceptions (factory pattern)

```java
// In application service — before:
throw new NotFoundException("Provider not found: " + id);
throw new ConflictException(AiAgentErrorCodes.PROVIDER_CODE_ALREADY_EXISTS, "Provider code ...");
throw new BusinessException(AiAgentErrorCodes.DEPRECATED_PROVIDER_CANNOT_BE_ACTIVATED, "...");

// After:
throw AiAgentExceptions.providerNotFound(id);
throw AiAgentExceptions.providerCodeAlreadyExists(code.value());
throw AiAgentExceptions.deprecatedProviderCannotBeActivated(provider.code().value());
```

### HTTP Status Convention

| Scenario | Status |
|---|---|
| Resource not found | 404 Not Found |
| Duplicate / uniqueness conflict | 409 Conflict |
| Invalid input (enum, format, range) | 400 Bad Request |
| Business rule violation (wrong state, dependency not active, deprecated) | 422 Unprocessable Entity |
| Unexpected technical error | 500 Internal Server Error |

### ErrorResponse Format

```json
{
  "success": false,
  "status": 404,
  "error": "Not Found",
  "errorCode": "PROVIDER_NOT_FOUND",
  "message": "Provider not found: 9f2c...",
  "details": ["id=9f2c..."],
  "path": "/api/v1/ai-agent/providers/9f2c...",
  "traceId": "abc-123",
  "timestamp": "2026-06-13T15:00:00+07:00"
}
```

### Deprecated: AiAgentErrorCodes

`AiAgentErrorCodes` is `@Deprecated`. Do not add new values. Use `AiAgentErrorCatalog` instead.

`AiAgentEnumParser.parseRequired/parseOptional` still accepts `String errorCode` — pass `AiAgentErrorCatalog.XXX.code()` at call sites.
