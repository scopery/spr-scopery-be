# Scopery Java Spring Boot — Coding Standards & Code Review Checklist

> **All-in-one standard for implementation, code generation, pull-request review, refactoring, and architecture assessment.**
>
> **Rule precedence:** Project-specific rules in **Part I** are mandatory and override generic recommendations in **Part II** whenever there is a conflict. Any exception must be documented explicitly in the PR or architecture decision record.

## How this document is used

- **Before coding:** use Part I as the implementation contract.
- **During coding:** validate package placement, dependency direction, naming, API contracts, persistence mapping, transactions, security, and hard-code prevention.
- **During review:** evaluate Part I first, then complete the broader quality review in Part II.
- **When evidence is unavailable:** write **“Not yet verified — requires running the application/tool/test.”** Do not infer runtime behavior from naming alone.
- **Source of truth:** `src/docs/TECHNICAL.md`. This document must be updated when the technical source of truth changes.

---

# Part I — Mandatory Project Coding Conventions

Source of truth: `src/docs/TECHNICAL.md`

---

## 1. Project Identity

- **App name:** spr-scopery-be
- **Root package:** `com.company.scopery`
- **Java version:** 21
- **Framework:** Spring Boot 3.4.x, Maven
- **DB:** PostgreSQL, Flyway migrations, Spring Data JPA
- **Architecture:** DDD-oriented modular monolith

---

## 2. Package Structure

```
src/main/java/com/company/scopery/
├── ScoperyApplication.java
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
│   ├── response/       ← ApiResponse, ErrorResponse
│   ├── exception/      ← exception types, ErrorCode
│   ├── pagination/     ← PageResponse, PageRequestUtils
│   ├── audit/          ← AuditableJpaEntity, ActivityLogService
│   └── validation/
├── platform/
│   ├── web/            ← GlobalExceptionHandler, RequestContext, HealthController
│   ├── logging/        ← RequestLoggingFilter
│   ├── config/         ← OpenApiConfig
│   └── scheduler/      ← @EnableScheduling config + ScheduledJobRegistry (central catalog of all jobs)
├── integration/
│   ├── ai/
│   └── redis/
└── bootstrap/
    └── seed/
```

**Rules:**
- `common` and `platform` must NOT depend on `modules`.
- `platform` may depend on `common`.
- `modules` may depend on `common` and `platform`.
- `common` must NOT contain business logic specific to any module.

---

## 3. Sub-module DDD Structure

Every sub-module uses this layout (applies to `modules/aiagent/`, `modules/eventregistry/`, `modules/workspace/`, etc.):

```
{module}/
├── http/
│   ├── controller/
│   │   └── {Entity}Controller.java
│   └── request/
│       ├── Create{Entity}Request.java
│       ├── Update{Entity}Request.java
│       └── Search{Entity}Request.java
├── application/
│   ├── action/
│   │   ├── Create{Entity}Action.java
│   │   ├── Update{Entity}Action.java
│   │   └── ...
│   ├── service/
│   │   └── {Entity}QueryService.java
│   ├── listeners/
│   │   ├── {Entity}EventListener.java
│   │   └── {Entity}CatalogInitializer.java
│   ├── jobs/
│   │   └── {Entity}Job.java
│   ├── command/
│   │   └── Create{Entity}Command.java
│   ├── query/
│   │   └── Search{Entity}Query.java
│   └── response/
│       └── {Entity}Response.java
├── domain/
│   ├── model/
│   │   ├── {Entity}.java
│   │   └── {Entity}Repository.java
│   ├── enums/
│   │   └── {Entity}Status.java
│   └── valueobject/
│       └── {Entity}Code.java
└── infrastructure/
    ├── persistence/
    │   ├── entity/                         ← used by aiagent module (all other modules put JpaEntity directly in persistence/)
    │   │   └── {Entity}JpaEntity.java
    │   ├── SpringData{Entity}JpaRepository.java
    │   └── Jpa{Entity}Repository.java
    └── mapper/
        └── {Entity}PersistenceMapper.java
```

**Folder purposes:**

| Folder | Purpose | Mandatory? |
|---|---|---|
| `http/controller/` | Spring `@RestController` — routes only, no business logic | Always |
| `http/request/` | `record` DTOs with Bean Validation (`@NotBlank`, `@Email`, etc.) | Always |
| `application/action/` | One `@Component` per write use-case (`execute()` + `@Transactional`) | If module has any writes |
| `application/service/` | One `@Service` with all read methods (`@Transactional(readOnly=true)`) | If module has any reads |
| `application/listeners/` | Spring event listeners: `@EventListener`, `@TransactionalEventListener`, `ApplicationListener<ApplicationReadyEvent>` (Initializers) | If module reacts to any Spring/domain event |
| `application/jobs/` | Scheduled background tasks using `@Scheduled`. Class name: `{Entity}Job`. Must also register an entry in `platform/scheduler/ScheduledJobRegistry` | If module has any recurring background work |
| `application/command/` | Immutable `record` objects carrying write intent (no validation annotations) | If module has any writes |
| `application/query/` | Immutable `record` objects carrying read parameters | If module has any reads |
| `application/response/` | Output `record` DTOs — mapped from domain, returned by controller | Always |
| `domain/model/` | Aggregate root `record`/class + repository interface (zero framework imports) | Always |
| `domain/enums/` | Domain enums (`{Entity}Status`, `{Entity}Type`, `{Entity}Option`, etc.) | **Always when any enum exists** |
| `domain/valueobject/` | Value objects wrapping primitives with validation (`{Entity}Code`, `{Entity}Name`, etc.) | **Always when any value object exists** |
| `infrastructure/persistence/` | `{Entity}JpaEntity.java` (extends `AuditableJpaEntity`), `SpringData*JpaRepository`, `Jpa*Repository` | Always |
| `infrastructure/mapper/` | `{Entity}PersistenceMapper` — converts domain ↔ JPA entity | Always |

**Mandatory creation rules:**

1. **`domain/enums/` MUST be created** whenever the domain has any field with a fixed set of values — even a single enum (`{Entity}Status`). Never place enums in `domain/model/` or `domain/` root.
2. **`domain/valueobject/` MUST be created** whenever the domain wraps a primitive with validation logic (e.g. `{Entity}Code` that enforces format/length). Never put value object classes inside the aggregate root file or `domain/model/`.
3. **`http/controller/` MUST be used** — controllers must never be placed directly in `http/`. The `http/` folder holds only the `controller/` and `request/` subfolders.
4. **`application/action/` MUST be used** for every write — never put write logic in QueryService, never create a shared `*ApplicationService`.
5. **`application/command/` MUST exist** for every Action class — the Action receives a Command object, not raw parameters or Request objects.
6. **`application/response/` MUST exist** — controllers must never return JPA entities, domain objects, or raw primitives.
7. **`infrastructure/mapper/`** MUST exist — the persistence layer maps via a dedicated mapper; controllers and actions must never access JPA entities directly.
8. **`application/listeners/` MUST be used** for any class that implements `ApplicationListener`, uses `@EventListener`, or `@TransactionalEventListener`. Never place listeners directly in `application/` root.
9. **`application/jobs/` MUST be used** for any class with `@Scheduled` methods. Never place job classes directly in `application/` root or inside `listeners/`.
10. **Every new `*Job` class MUST register an entry in `platform/scheduler/ScheduledJobRegistry`** so the system has a single place listing all background jobs and their schedules. Failing to register means the job is invisible to the team.

**Layer dependency rule:**
```
http → application → domain
infrastructure → domain
```
Never: `domain → infrastructure`, `domain → JPA`, `domain → Spring Web`, `controller → repository`, `action → JPA entity`, `response → JPA entity`

---

## 4. API Prefix

All AI Agent endpoints use:
```
/api/ai-agent/{resource}
```

Examples:
- `POST /api/ai-agent/providers`
- `GET  /api/ai-agent/providers/{id}`
- `PATCH /api/ai-agent/providers/{id}/activate`

---

## 5. Request / Command / Query / Response Rules

| Type | Layer | Purpose |
|---|---|---|
| `*Request` | `http/request/` | HTTP input DTO. Has Bean Validation annotations. |
| `*Command` | `application/command/` | Internal write use-case object. |
| `*Query` | `application/query/` | Internal read use-case object. |
| `*Response` | `application/response/` | Output returned to the API caller. |

- Controller converts Request → Command/Query, calls `*Action` for writes or `*QueryService` for reads, returns Response.
- Controller must not contain business logic.
- JPA entities must never be returned from controllers.
- Domain objects must never be returned from controllers.

---

## 6. Repository Rules

| Type | Location | Purpose |
|---|---|---|
| Domain repository | `domain/{Entity}Repository.java` | Interface. Defines what business needs. No JPA/Spring imports. |
| JPA repository | `infrastructure/persistence/Jpa{Entity}Repository.java` | Implements domain interface using JPA. |
| Spring Data repo | `infrastructure/persistence/SpringData{Entity}JpaRepository.java` | Extends `JpaRepository`. Works only with JPA entity. |
| Mapper | `infrastructure/mapper/{Entity}PersistenceMapper.java` | Converts between domain object and JPA entity. |

**Save behavior:** `JpaProviderRepository.save()` uses `springDataRepository.saveAndFlush()` to ensure JPA auditing timestamps (`@CreatedDate`, `@LastModifiedDate`) are applied before the returned entity is mapped back to domain. The mapper always sets `createdAt` from the domain so Spring Data's `isNew()` check (via `@CreatedDate` null-check) returns false and consistently uses `merge()`.

---

## 7. Standardized API Response

**Success:**
```json
{ "success": true, "data": {}, "timestamp": "..." }
```
Use: `ApiResponse.success(data)` — class: `common/response/ApiResponse.java`

**Error:**
```json
{ "success": false, "errorCode": "...", "message": "...", "details": [], "traceId": "...", "timestamp": "..." }
```
Generated by `GlobalExceptionHandler` in `platform/web/` — class: `common/response/ErrorResponse.java`

**Paginated list:**
```json
{ "items": [], "page": 0, "size": 20, "totalElements": 0, "totalPages": 0, "first": true, "last": true }
```
Use: `PageResponse.from(springPage)` — class: `common/pagination/PageResponse.java`

Controllers return `ApiResponse<PageResponse<T>>` for list/search endpoints.

---

## 8. Exception Types

| Class | HTTP Status | ErrorCode default |
|---|---|---|
| `NotFoundException` | 404 | `RESOURCE_NOT_FOUND` |
| `ConflictException` | 409 | provided by caller |
| `BusinessException` | 422 | `BUSINESS_RULE_VIOLATION` or custom |
| `ValidationException` | 400 | `VALIDATION_ERROR` |
| `MethodArgumentNotValidException` | 400 | `VALIDATION_ERROR` |

Module-specific error codes (e.g. `PROVIDER_CODE_ALREADY_EXISTS`) are defined as String constants inside the module and passed to the exception constructor. Do NOT add module-specific codes to `ErrorCode.java`.

---

## 9. Request Logging & TraceId

- `RequestLoggingFilter` (in `platform/logging/`) runs on every request.
- Generates a `traceId` (UUID) unless `X-Trace-Id` header is provided.
- Puts `traceId` in SLF4J MDC key `"traceId"`.
- Sets `RequestContext` with traceId in `RequestContextHolder` (ThreadLocal).
- Returns `X-Trace-Id` response header.
- Logs: method, path, status, durationMs, ip, traceId.

---

## 10. Audit Fields (JPA Entities)

Business JPA entities should extend `AuditableJpaEntity` (`common/audit/`):
- `created_at`, `updated_at` — auto-managed by Spring Data JPA auditing (`@CreatedDate`, `@LastModifiedDate`)
- `created_by`, `updated_by` — currently set to `"SYSTEM"` until Spring Security is added

`AuditConfig` enables `@EnableJpaAuditing` with `auditorProvider` bean.

Mapper rule: the `toJpaEntity()` mapper method must set `entity.setCreatedAt(domain.createdAt())` from the domain object. This allows Spring Data to use `merge()` consistently (since the `@CreatedDate` field is non-null). Never duplicate `created_by`/`updated_by` manually — Spring Data handles them.

---

## 11. Activity Log

Every important state-changing business action should be recorded via `ActivityLogService`:

```java
activityLogService.logSuccess(
    "AIAGENT",            // moduleCode
    "PROVIDER",           // entityType
    provider.id().toString(), // entityId
    "CREATE_PROVIDER",    // action — define as String constant in your module
    null,                 // actorId (null until Security is implemented)
    null,                 // actorName
    "Provider created: " + provider.code().value(),
    null                  // metadata JSON string (optional)
);
```

- `ActivityLogService` uses `MDC.get("traceId")` automatically.
- It never throws. Failures are logged as WARN only.
- Module-specific action names (e.g. `"CREATE_PROVIDER"`) live in the module.
- DB table: `app_activity_log` (created by V1 migration).

---

## 12. Database Naming Convention

| Scope | Table Prefix | Example |
|---|---|---|
| Common / platform | `app_` | `app_activity_log` |
| AI Agent module | `aiagent_` | `aiagent_provider`, `aiagent_model` |
| Future modules | `{module}_` | `crm_contact`, `billing_invoice` |

**Do NOT use** generic names like `provider`, `model`, `execution` — they will conflict across modules.
**Do NOT use** the old `ai_` prefix.

AI Agent table names:
- `aiagent_provider`
- `aiagent_model`
- `aiagent_model_deployment`
- `aiagent_model_parameter_capability`
- `aiagent_agent`
- `aiagent_prompt_template`
- `aiagent_prompt_version`
- `aiagent_event_config`
- `aiagent_usage_policy`
- `aiagent_execution_log`

**Column naming:** snake_case. Primary key: `id`. Foreign keys: `{entity}_id` (e.g. `provider_id`, `model_id`).

**Constraint naming:**
- Primary key: `pk_{table_name}` (e.g. `pk_aiagent_provider`)
- Unique constraint: `uq_{table_name}_{column}` (e.g. `uq_aiagent_provider_code`)
- Foreign key: `fk_{table}_{referenced_table}` (e.g. `fk_aiagent_model_aiagent_provider`)
- Index: `idx_{table}_{column}` (e.g. `idx_aiagent_provider_status`)

---

## 13. Flyway Migration Naming

```
V{n}__{action}_{table_name}.sql
```

Examples:
- `V1__create_activity_log_table.sql` (already applied)
- `V2__create_aiagent_provider_table.sql`
- `V3__create_aiagent_model_table.sql`

Never modify an already-applied migration. Always create a new version.

---

## 14. Swagger / OpenAPI Convention

- `OpenApiConfig` is in `platform/config/`.
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`
- Business controllers must use:
  - `@Tag(name = "AI Agent - {Resource}")` at class level
  - `@Operation(summary = "...")` at each method
- Do not add authentication/security to Swagger in this phase.

---

## 15. Coding Constraints (Summary)

- Do not put business logic in controllers.
- Do not return JPA entities from APIs.
- Do not use domain objects as API responses.
- Do not let `common` or `platform` depend on `modules`.
- Do not create module-specific logic inside `common`.
- Use `ApiResponse.success(data)` for all success responses.
- Use `PageResponse.from(page)` for all paginated responses.
- Use `ActivityLogService` for important state-changing module actions.
- Domain classes must not import `jakarta.persistence` or `org.springframework.web`.
- Action classes (`*Action`) handle write use-cases: `@Component`, single `execute()` method, `@Transactional` on `execute()`.
- QueryService classes (`*QueryService`) handle reads: `@Service`, all methods `@Transactional(readOnly = true)`.
- Do not create a monolithic `*ApplicationService` — use Action + QueryService separation instead.
- Provider code (and similar unique codes) must be validated in the domain value object.
- All AI Agent tables use the `aiagent_` prefix.
- JPA entities for business modules must extend `AuditableJpaEntity`.

---

## 17. AI Agent Shared Kernel Rules

The shared kernel lives at `modules/aiagent/shared/` and is the single source of truth for constants and utilities used across all AI Agent sub-modules.

**Packages:**
- `shared/constant/` — compile-time string constants
- `shared/activity/` — `AiAgentActivityLogger` component
- `shared/util/` — `AiAgentEnumParser` utility

**Constant classes:**

| Class | Use |
|---|---|
| `AiAgentModuleCodes` | Module code string (`"AIAGENT"`) for `ActivityLogService` |
| `AiAgentEntityTypes` | Entity type strings (`PROVIDER`, `AI_MODEL`, `MODEL_DEPLOYMENT`, …) |
| `AiAgentActivityActions` | Action name strings (`CREATE_PROVIDER`, `ACTIVATE_AI_MODEL`, …) |
| `AiAgentErrorCodes` | Error code strings for exceptions (`PROVIDER_CODE_ALREADY_EXISTS`, …) |
| `AiAgentTableNames` | DB table name strings used in `@Table(name = ...)` annotations |
| `AiAgentApiPaths` | API path strings used in `@RequestMapping(...)` annotations |
| `AiAgentSortFields` | JPA field names for `Sort.by(...)` |

**Rules:**
- All error code strings for AI Agent exceptions must be defined in `AiAgentErrorCodes`, NOT in `ErrorCode.java` (which is for platform-level codes only).
- All activity log entity types and action strings must be defined in `AiAgentEntityTypes` and `AiAgentActivityActions`.
- `@Table(name = AiAgentTableNames.X)` must be used in all AI Agent JPA entities — never a raw hardcoded string.
- `@RequestMapping(AiAgentApiPaths.X)` must be used in all AI Agent controllers — never a raw hardcoded string.
- Application services must inject `AiAgentActivityLogger` (not `ActivityLogService` directly).
- Enum parsing in application services must use `AiAgentEnumParser.parseRequired` / `parseOptional` — never inline `Enum.valueOf`.
- When adding a new AI Agent sub-module, add its constants to all relevant classes in `shared/constant/` before writing any application or controller code.

---

## 18. Security, Repository and Hard-code Prevention Rules

**Hard-code prevention:**

1. Do not hard-code AI Agent module code, entity type, activity action, error code, API path, table name, or sort field in services, controllers, or JPA entities. Use constants from `modules/aiagent/shared/constant/`.
2. Use `AiAgentActivityLogger` for all AI Agent business activity log calls — do not inject `ActivityLogService` directly into AI Agent application services.
3. Use `AiAgentEnumParser.parseRequired` / `parseOptional` for all request/query enum parsing — never use `Enum.valueOf` inline.
4. If a new module repeats logic already solved in `shared/` or `common/`, stop and reuse the existing foundation.

**SQL / JPQL safety:**

5. Do not concatenate user input into SQL, JPQL, or native query strings.
6. Use parameterized queries with `@Param` for all Spring Data / JPQL queries.
7. Criteria API predicates (`cb.like`, `cb.equal`) are acceptable for dynamic search — they do not concatenate raw strings into SQL.

**`@Modifying` bulk update rules:**

8. All `@Modifying` bulk update methods must return `int` (affected row count), not `void`.
9. All `@Modifying` bulk update methods must add `flushAutomatically = true` to flush pending changes before the bulk SQL runs.
10. All `@Modifying` bulk update methods must add `clearAutomatically = true` to clear the first-level cache after the update.
11. All `@Modifying` bulk update JPQL must manually set `e.updatedAt = CURRENT_TIMESTAMP` — JPA auditing lifecycle callbacks (`@LastModifiedDate`) do NOT fire for JPQL bulk updates.
12. Do not rely on JPA auditing for bulk update audit timestamps. Set them explicitly in the JPQL.

**Data integrity:**

13. DB constraints (unique indexes, check constraints, partial unique indexes) are the final safety layer — application logic must not be the only protection for uniqueness rules.
14. Application-level uniqueness checks (e.g. `existsByCode`) must still exist for early conflict detection with a clean error message, but the DB constraint must also exist.

**API and domain safety:**

15. Controllers must not return JPA entities or domain objects directly — return `ApiResponse<ResponseType>` or `ApiResponse<PageResponse<ResponseType>>`.
16. Controllers must not contain business logic.
17. Domain objects must not import `jakarta.persistence` or `org.springframework.web`.
18. If a business activation can fail because the entity is DEPRECATED, the application service must check this explicitly and throw `BusinessException` with a human-readable message before calling the domain method. Domain-level `IllegalStateException` is a last-resort guard, not the user-facing response.

**Controlled behavior enums:**

21. Any field whose value must come from a fixed set of allowed options must be represented as a domain enum, not a freeform String. Example: `ModelParameterIfNullBehavior` (DO_NOT_SEND_PARAMETER / USE_PROVIDER_DEFAULT) — not a raw string.
22. Domain enums are stored as their `.name()` string in the DB (VARCHAR). The mapper converts: `enum → entity.setField(enum.name())` and `entity.getField() != null ? Enum.valueOf(entity.getField()) : null`.
23. Request DTOs accept `String` input from the API. Application services parse using `AiAgentEnumParser.parseOptional` / `parseRequired` before passing the enum to the domain.

**Logging safety:**

24. Do not log `Authorization` headers, cookies, API keys, full AI prompt content, AI completion output, or uploaded file content unless explicitly allowed by a configuration flag.
25. If a new state-changing business action is added, it must be recorded via `activityLogger.logSuccess(...)`.

---

## 19. Full Error Catalog Rules

**Error catalog source of truth:**

1. All new AI Agent errors must be added to `AiAgentErrorCatalog` in `modules/aiagent/shared/error/`.
2. Do NOT add new values to `AiAgentErrorCodes` — it is deprecated.
3. `AiAgentErrorCatalog` is the single source of truth for `errorCode`, `defaultMessage`, and `httpStatus`.
4. AI Agent application services must throw via `AiAgentExceptions` factory methods — not direct exception constructors.
5. Do not hard-code repeated error messages in services.
6. `GlobalExceptionHandler` must not import any `modules/aiagent` class. It handles the generic `AppException` from `common/exception/`.
7. Error messages must not expose sensitive data (API keys, full prompt content, SQL, stack traces).

**HTTP status convention:**

8. HTTP status comes from `ErrorCatalog.httpStatus()` for `AppException`.
9. Invalid user input (enum value, format, range) → **400 Bad Request**.
10. Resource not found → **404 Not Found**.
11. Duplicate / uniqueness conflict → **409 Conflict**.
12. Business rule violation (deprecated entity, wrong state, dependency not active) → **422 Unprocessable Entity**.
13. Unexpected technical errors → **500 Internal Server Error** (fallback handler only).
14. Do not use **402 Payment Required** unless a payment/subscription feature exists.

**POST create convention:**

15. POST create endpoints should return **201 Created** in future new modules. Existing modules return 200 for backward compatibility.

**`AiAgentExceptions` factory rules:**

16. Use safe business identifiers only in factory methods: UUID, code string, name string.
17. Do not include SQL, stack traces, full prompt content, or Authorization headers in exception messages.
18. When adding a new AI Agent sub-module, add entries to `AiAgentErrorCatalog` and factory methods to `AiAgentExceptions` before writing service code.

**Backward compatibility:**

19. `NotFoundException`, `ConflictException`, `BusinessException`, `ValidationException` remain in `common/exception/` for gradual migration and non-AI-Agent modules.
20. New AI Agent service code must use `AiAgentExceptions` / `AppException` — not the old exception types.

---

## 16. Implementation Phases

| Phase | Status | Scope |
|---|---|---|
| Phase 0 | Done | Java 21, Spring Boot project, package skeleton, Flyway, application.yml |
| Phase 0.5 | Done | Common/platform foundation: ApiResponse, ErrorResponse, exceptions, pagination, audit, traceId, ActivityLog |
| Phase 1 | Done | Provider module — CRUD + activate/deactivate + Swagger/OpenAPI setup |
| Phase 2 | Done | AI Model module — CRUD + activate/deactivate, linked to Provider |
| Phase 3 | Done | Model Deployment module — CRUD + activate/deactivate/set-default, linked to AI Model |
| Phase 4 | Done | AI Agent shared kernel refactoring — shared constants, AiAgentActivityLogger, AiAgentEnumParser |
| Phase 5 | Done | Model Parameter Capability module — CRUD + activate/deactivate, linked to AI Model |
| Phase 6 | Done | Agent module — CRUD + activate/deactivate |
| Phase 6.1 | Done | Full Error Catalog Refactor — ErrorCatalog, AppException, AiAgentErrorCatalog, AiAgentExceptions |
| Phase 7 | Done | Prompt module — PromptTemplate + PromptVersion with versioning and archiving |
| Phase 7.1 | Done | Event Registry module — EventDefinition under modules/eventregistry/ (separate from AI Agent) |
| Phase 8 | Done | AI Agent Event Configuration (eventconfig) — links Event Registry to Agent+Prompt+Deployment |
| Phase 9 | Done | UsagePolicy module — CRUD + activate/deactivate + UsagePolicyEvaluator |
| Phase 10 | Done | Execution + Playground — ExecuteEventConfigAction, ExecuteEventAction, ExecutePlaygroundDirectAction, PromptRenderer, ProviderSecret |
| Phase 10.1 | Done | Containment tasks CT-1 (playground env flag) + CT-2 (lifecycle write restriction) + controller structure fix (http/controller/) |
| Phase 11 | Pending | Next.js admin UI |
| Phase 12 | Done | Spring Security core — JWT auth (`platform/security/JwtAuthFilter`), CSRF (double-submit cookie), BCrypt, stateless sessions; full IAM/RBAC module (`modules/iam/`: user, role, right, permission, grant, roleassignment, authorization, ownerpolicy); `AiAgentSecurityInterceptor` enforces per-endpoint system rights (e.g. `AI_PLATFORM_MANAGE`, `AI_PROVIDER_SECRET_MANAGE`) via `IamSystemAuthorizationService` |
| Phase 12.1 | Pending | SSO, approval workflow, further hardening |

---

## 20. Event Registry Module Rules

The Event Registry module lives at `modules/eventregistry/` — NOT inside `modules/aiagent/`.

**Structure:** Mirrors the AI Agent pattern exactly — entity named sub-module + shared kernel:
```
eventregistry/
├── eventdefinition/           ← sub-module (named after entity, like aiagent/provider/)
│   ├── http/
│   │   ├── controller/
│   │   └── request/
│   ├── application/
│   │   ├── action/
│   │   └── service/
│   ├── domain/
│   │   ├── model/
│   │   ├── enums/
│   │   └── valueobject/
│   └── infrastructure/
└── shared/                    ← shared kernel for all event registry sub-modules
    ├── constant/
    ├── activity/
    ├── error/
    └── util/
```

**Key rules:**
- `eventregistry/eventdefinition/` mirrors `aiagent/provider/` in structure.
- `EventRegistryTableNames`, `EventRegistryApiPaths`, `EventRegistryEntityTypes`, `EventRegistryActivityActions`, `EventRegistryModuleCodes`, `EventRegistrySortFields` all live in `shared/constant/`.
- `@Table(name = EventRegistryTableNames.X)` — never raw string in JPA entity.
- `@RequestMapping(EventRegistryApiPaths.X)` — never raw string in controller.
- Application service uses constants from `shared/constant/` for entity type and action strings.
- `EventRegistryActivityLogger` uses `EventRegistryModuleCodes.EVENT_REGISTRY` — never hardcoded.
- Error catalog: `EventRegistryErrorCatalog` implements `ErrorCatalog` — separate from `AiAgentErrorCatalog`.
- Exceptions thrown via `EventRegistryExceptions` factory — never direct constructors.
- Table prefix: `app_` (e.g. `app_event_definition`) — it's a cross-module registry, not AI Agent specific.
- Migration V8: `V8__create_app_event_definition_table.sql`.

---

## 21. Read/Write Separation: Action + QueryService Pattern

All modules use the Action + QueryService pattern instead of a monolithic `*ApplicationService`.

**Action class rules (`application/action/*Action.java`):**
- `@Component` (not `@Service`)
- One public method only: `execute(...)`
- `@Transactional` on `execute()`
- One write use-case per class (one class per verb: Create, Update, Activate, etc.)
- Inject only what this specific action needs — no shared parent service injection

**QueryService class rules (`application/service/*QueryService.java`):**
- `@Service`
- All methods `@Transactional(readOnly = true)`
- May contain multiple read methods (getX, searchX, listX, etc.)

**Controller rules:**
- Injects `*QueryService` + all relevant `*Action` classes directly
- No `*ApplicationService` injection — that class no longer exists in migrated modules
- `@RequestMapping` uses constant from `shared/constant/` — never raw string

**Non-CRUD verb naming:**
- Use the verb directly: `ActivateProviderAction`, `ArchiveWorkspaceAction`, `SetDefaultModelDeploymentAction`

**Private helper duplication:**
- If `findOrThrow()` or similar helpers are needed by multiple action classes, duplicate them (3 lines each)
- Do NOT extract a new shared service or parent class just to share a helper

**Read-only modules:**
- If a module has no writes (e.g. `IamPermissionQueryService`, `IamRightQueryService`), create only a QueryService — no action folder

**Module with 2 old ApplicationServices → 1 action group:**
- Two old services that share a single controller are merged into one action group + one QueryService

**Documented exception — AI provider execution actions:**
- `ExecuteEventConfigAction`, `ExecuteEventAction`, `ExecutePlaygroundDirectAction` (`modules/aiagent/execution/application/action/`) deliberately omit `@Transactional` on `execute()`. These methods call an external AI provider over HTTP, which can take seconds and must not hold a DB connection/transaction open; execution status (RUNNING → SUCCEEDED/FAILED) must also be persisted in its own committed transaction even when the provider call throws midway, so a failed run is recorded instead of being rolled back with the rest of the method. This is the only sanctioned exception to the `@Transactional` rule above — do not add more without an equivalent documented reason here.

---

# Part II — Java Spring Boot Architecture & Quality Review Checklist

The following checklist supplements the mandatory project rules above. It covers codebase-wide quality, runtime behavior, security, operations, maintainability, and scalability.

> Compiled from: internal coding conventions, HLD analysis, Java/Spring best practices, and standard design principles (SOLID/DRY/KISS/Clean Architecture).
> Use this checklist to review a Java Spring Boot codebase at multiple levels: overall architecture → module → class → method → line of code.

---

## 1. Architecture & Processing Flow

- [ ] Is the number of layers and each layer's responsibility clearly defined? (e.g. Controller → Service → Repository → Entity, or Adapter → Application → Domain → Infrastructure)
- [ ] Does the Controller contain business logic or access the Repository/EntityManager directly?
- [ ] Does the Service bypass the agreed data-access boundary or contain persistence-specific code unnecessarily?
- [ ] Does the Repository contain business logic, response formatting, or mapping that belongs to another layer?
- [ ] Do JPA Entities contain inappropriate application/infrastructure logic, or only valid domain behavior?
- [ ] Is `@Transactional` placed at the correct application/service boundary—the layer that knows the full scope of the operation?
- [ ] Are transaction propagation, isolation, and read-only semantics used intentionally?
- [ ] Is there any circular dependency between modules, packages, or Spring beans?
- [ ] Does the domain layer depend on Spring MVC, JPA, database, messaging, or other infrastructure details?
- [ ] Are synchronous and asynchronous processing flows clearly separated and documented?

## 2. Module / Package Structure

- [ ] Does a new module follow the project's standard package structure?
- [ ] Is the code organized consistently by feature/module or by technical layer?
- [ ] Are module boundaries clear, or does one module access another module's internal classes directly?
- [ ] Are public APIs between modules explicit and minimal?
- [ ] Is package visibility used where appropriate instead of making every class `public`?
- [ ] Is Spring configuration split by concern/module, or accumulated in one oversized configuration class?
- [ ] Are component scanning and bean registration scopes controlled to avoid unintended beans or conflicts?
- [ ] For multi-module builds, are Maven/Gradle dependencies directional and free of cycles?

## 3. Design Patterns — used where appropriate, no more, no less

- [ ] Repository pattern: are Spring Data repositories used appropriately, without creating unnecessary wrappers for trivial CRUD?
- [ ] Service/Application Service: is orchestration separated from domain logic and persistence logic?
- [ ] Command/Query separation: are write and read flows separated when complexity justifies it, without over-engineering simple cases?
- [ ] DTO: are DTOs used at API and cross-layer boundaries instead of exposing JPA Entities directly?
- [ ] Mapper: is mapping centralized and testable, rather than duplicated across Controllers and Services?
- [ ] Enum: are enums used instead of magic strings/numbers, with safe database and JSON serialization?
- [ ] Interface: is an interface justified by multiple implementations, an external boundary, or testing needs—not created automatically for every class?
- [ ] Event/Listener: are domain/application events used for meaningful decoupling, rather than hiding a simple synchronous method call?
- [ ] Strategy/Factory: are variable business rules modeled explicitly where appropriate?
- [ ] Inheritance: is composition preferred unless a true substitutable “is-a” relationship exists?
- [ ] State machine: are state transitions explicitly validated before persistence?

## 4. Naming Convention

- [ ] Packages: lowercase, meaningful, and consistent with the project structure?
- [ ] Classes/interfaces: PascalCase and accurately named by responsibility?
- [ ] Methods/variables: camelCase, methods begin with a meaningful verb, and names describe intent rather than implementation?
- [ ] Constants: `UPPER_SNAKE_CASE` and located in an appropriate class or enum?
- [ ] Common suffixes used consistently: `Controller`, `Service`, `Repository`, `Entity`, `Dto`, `Request`, `Response`, `Mapper`, `Exception`, `Configuration`?
- [ ] Are generic names such as `Util`, `Helper`, `Manager`, `Processor`, or `CommonService` avoided unless the responsibility is genuinely clear?
- [ ] Database tables/columns: follow the team's naming convention consistently?
- [ ] REST endpoints: nouns for resources, kebab-case where applicable, and no unnecessary verbs in URLs?
- [ ] Boolean fields: named positively and clearly (`active`, `enabled`, `hasPermission`) without confusing double negatives?

## 5. Database, JPA & Hibernate

- [ ] Are JPA relationships (`@OneToMany`, `@ManyToOne`, etc.) modeled correctly with clear ownership?
- [ ] Are fetch strategies chosen intentionally rather than relying on defaults?
- [ ] Are there signs of N+1 queries? Verify with SQL logs, Hibernate statistics, datasource-proxy, or an APM tool.
- [ ] Are `JOIN FETCH`, `@EntityGraph`, projections, or dedicated queries used where appropriate?
- [ ] Are JPA Entities returned directly from REST APIs? They generally should not be.
- [ ] Are bidirectional relationships necessary, and are recursion/serialization issues prevented?
- [ ] Are cascade types and `orphanRemoval` used carefully to avoid accidental writes/deletes?
- [ ] Are equals/hashCode implementations safe for JPA Entities and proxies?
- [ ] Are lazy-loaded relationships accessed outside an active transaction, causing `LazyInitializationException` risk?
- [ ] Are bulk updates/deletes handled with persistence-context synchronization in mind?
- [ ] Are pagination and sorting used for potentially large result sets?
- [ ] Are native queries used only when justified and documented?
- [ ] Do database indexes cover columns frequently used for filters, joins, ordering, and uniqueness?
- [ ] Are database migrations managed with Flyway or Liquibase and kept in version control?
- [ ] Are optimistic/pessimistic locking strategies used where concurrent updates can cause lost data?

## 6. Dependency Injection & Spring Bean Management

- [ ] Is constructor injection used instead of field injection?
- [ ] Are dependencies immutable where possible (`final` fields)?
- [ ] Are bean scopes appropriate, with singleton beans free of unsafe mutable request-specific state?
- [ ] Are multiple bean implementations resolved explicitly using meaningful qualifiers?
- [ ] Are conditional beans and profiles used intentionally and tested?
- [ ] Are bean lifecycle hooks (`@PostConstruct`, `@PreDestroy`) lightweight and reliable?
- [ ] Are circular bean dependencies avoided rather than hidden with `@Lazy`?

## 7. Authentication & Authorization

- [ ] Is Spring Security configured centrally and consistently?
- [ ] Are endpoint permissions enforced server-side, not only hidden in the UI?
- [ ] Are URL-level and method-level authorization rules consistent?
- [ ] Is `@PreAuthorize`/`@PostAuthorize` used appropriately without scattering authorization logic everywhere?
- [ ] Are roles, authorities, and permission naming conventions clear and consistent?
- [ ] Are JWT/session/OAuth2 settings validated correctly, including expiration, issuer, audience, and signature?
- [ ] Are passwords hashed with an appropriate adaptive password encoder?
- [ ] Is CSRF protection configured appropriately for the authentication model?
- [ ] Are CORS rules restrictive and environment-aware?
- [ ] Are public endpoints intentionally excluded from authentication and authorization?
- [ ] Are security-related caches invalidated correctly when permissions change?

## 8. REST API & Web Layer

- [ ] Are Controllers thin and limited to HTTP concerns, validation, authorization context, and delegation?
- [ ] Are request and response DTOs used instead of exposing domain/JPA objects?
- [ ] Is API versioning defined, or is there a strategy for breaking changes?
- [ ] Are HTTP methods and status codes semantically correct?
- [ ] Is the response/error format consistent across the system?
- [ ] Is global exception handling implemented with `@ControllerAdvice` / `@RestControllerAdvice`?
- [ ] Are pagination metadata and sorting contracts consistent?
- [ ] Are idempotency requirements handled for retryable write endpoints?
- [ ] Are content types, file upload limits, and request size limits configured safely?
- [ ] Is OpenAPI/Swagger documentation accurate and generated/validated as part of the workflow?

## 9. Validation & Error Handling

- [ ] Is Bean Validation (`jakarta.validation`) used on request DTOs and method parameters?
- [ ] Are validation rules located at the correct boundary, with business invariants also enforced inside the domain/service layer?
- [ ] Are custom validators used only when built-in constraints are insufficient?
- [ ] Are exceptions categorized clearly: validation, business/domain, not found, conflict, integration, infrastructure?
- [ ] Are exceptions translated into appropriate HTTP status codes and stable error codes?
- [ ] Are exceptions ever swallowed, logged without rethrowing, or converted into `null`/empty results incorrectly?
- [ ] Are catch blocks specific and actionable rather than catching generic `Exception` everywhere?
- [ ] Do error responses avoid leaking stack traces, SQL, secrets, or internal implementation details?
- [ ] Is there an audit log for important create/update/delete/security operations?

## 10. Transactions & Data Consistency

- [ ] Is `@Transactional` applied to public methods called through a Spring proxy, rather than relying on self-invocation?
- [ ] Are write transactions kept as short as possible?
- [ ] Are remote API calls avoided inside long-running database transactions where possible?
- [ ] Are checked-exception rollback rules configured intentionally when needed?
- [ ] Are `readOnly = true`, propagation, and isolation levels used correctly?
- [ ] Are transaction boundaries clear for batch jobs and message consumers?
- [ ] For workflows spanning DB and external systems, is there an outbox, saga, compensation, or reconciliation mechanism?

## 11. Configuration & Environment

- [ ] Are configuration values externalized using `application.yml` / `application.properties` and environment variables?
- [ ] Is `@ConfigurationProperties` preferred over scattered `@Value` usage for grouped configuration?
- [ ] Are secrets excluded from source control and managed by an appropriate secret store?
- [ ] Are environment profiles minimal, clear, and free of duplicated configuration?
- [ ] Are production-safe defaults used for logging, actuator exposure, CORS, SQL logging, and debug settings?
- [ ] Are configuration properties validated at startup?
- [ ] Is timezone, locale, encoding, and date/time serialization configured consistently?

## 12. Coding Standards & Design Principles

- [ ] Is a formatter/linter enforced automatically (e.g. Spotless, Checkstyle, PMD, Sonar rules), rather than reviewed by eye only?
- [ ] Is the target Java version defined and used consistently in local builds and CI?
- [ ] Are modern Java features used appropriately without harming readability or compatibility?
- [ ] **DRY**: is business logic duplicated in multiple places instead of being extracted to a suitable abstraction?
- [ ] **KISS**: is complexity proportional to the business problem?
- [ ] **SOLID**:
  - SRP: does each class have one clear reason to change?
  - OCP: can new behavior be added without repeatedly modifying central conditional logic?
  - LSP: can implementations safely substitute for their abstractions?
  - ISP: are interfaces focused and free of unrelated methods?
  - DIP: does core business logic depend on abstractions rather than frameworks and concrete integrations?
- [ ] Are long methods, deep nesting, boolean flags, and large switch statements refactored where they reduce understandability?
- [ ] Are `Optional`, streams, records, sealed classes, and lambdas used appropriately rather than mechanically?
- [ ] Are nullability expectations explicit and consistently handled?

## 13. Testing & Quality Gate

- [ ] Are unit tests present for domain and service/business logic?
- [ ] Are Controller/API tests present for validation, authorization, status codes, and response contracts?
- [ ] Are repository/integration tests present for custom queries and database behavior?
- [ ] Are sensitive flows tested: authentication, authorization, payment, concurrency, file upload, messaging?
- [ ] Is Testcontainers or an equivalent realistic dependency setup used where in-memory substitutes would hide production issues?
- [ ] Are mocks limited to external boundaries rather than mocking the class under test's internal details?
- [ ] Are tests deterministic, isolated, readable, and free of ordering/timezone dependencies?
- [ ] Are minimum coverage and quality thresholds enforced in CI?
- [ ] Do static analysis and vulnerability checks run in CI?
- [ ] Are architecture rules enforced automatically where useful (e.g. ArchUnit)?

## 14. Performance & Scalability

- [ ] Are large or slow operations processed asynchronously or in background workers when appropriate?
- [ ] Are executors/thread pools explicitly sized and monitored instead of relying blindly on defaults?
- [ ] Are async methods used correctly, without self-invocation or lost security/transaction context?
- [ ] Are large datasets processed with pagination, streaming, chunking, or batching?
- [ ] Is JDBC batching configured and verified for bulk writes?
- [ ] Is caching used for read-heavy, write-light data, with a clear invalidation strategy?
- [ ] Are cache keys, TTLs, serialization, and stampede risks handled?
- [ ] Are connection pools configured and monitored appropriately?
- [ ] Are timeouts configured for database, HTTP clients, messaging, and external services?
- [ ] Are retries limited to transient failures with backoff and jitter?
- [ ] Are circuit breakers, bulkheads, and rate limits used where external dependency failure could cascade?

## 15. Messaging, Scheduling & Background Jobs

- [ ] Are message producers and consumers designed with at-least-once delivery in mind?
- [ ] Are consumers idempotent?
- [ ] Are retry, dead-letter, backoff, and poison-message handling configured?
- [ ] Are message schemas versioned and backward compatible?
- [ ] Are transaction boundaries between database changes and message publication reliable?
- [ ] Are scheduled jobs safe in a multi-instance deployment (locking or leader election where required)?
- [ ] Are jobs observable, restartable, and protected against duplicate execution?
- [ ] Do long-running jobs support partial progress and recovery?

## 16. Observability & Operational Readiness

- [ ] Are logs structured, meaningful, and free of sensitive data?
- [ ] Are correlation/trace IDs propagated across HTTP, async, and messaging flows?
- [ ] Are metrics available for request latency, errors, JVM, DB pool, caches, queues, and external dependencies?
- [ ] Is distributed tracing configured where the architecture requires it?
- [ ] Are Spring Boot Actuator endpoints exposed safely and only as needed?
- [ ] Are liveness and readiness probes correctly separated?
- [ ] Does graceful shutdown stop accepting new work and allow in-flight work to finish?
- [ ] Are deployment and database migration steps automated and repeatable?
- [ ] Is there a rollback/roll-forward plan for application and database changes?
- [ ] Are important operational tasks available through safe admin tooling or commands when the web layer is unavailable?

## 17. Security

- [ ] Is all untrusted input validated before use?
- [ ] Are SQL injection risks avoided through parameterized queries and safe ORM usage?
- [ ] Are path traversal, SSRF, deserialization, template injection, and file upload risks considered?
- [ ] Are sensitive fields excluded from logs, API responses, `toString()`, and exception messages?
- [ ] Are secrets/tokens encrypted or hashed appropriately at rest?
- [ ] Are dependencies scanned for known vulnerabilities and updated through a controlled process?
- [ ] Are security headers configured appropriately?
- [ ] Are object-level authorization checks applied to prevent IDOR/BOLA issues?
- [ ] Are rate limits applied to login, password reset, OTP, and other abuse-sensitive endpoints?
- [ ] Are Java serialization and unsafe polymorphic JSON deserialization avoided or tightly controlled?

---

## 18. Overall Quality Attributes

Sections 1–17 assess detailed technical aspects. This section synthesizes those findings into codebase-level quality attributes.

### 18.1 Stability

- [ ] No circular dependencies between modules, packages, or Spring beans.
- [ ] Transaction boundaries are explicit and appropriate.
- [ ] External calls have timeout, retry, and failure isolation where needed.
- [ ] Jobs and message consumers are idempotent, observable, and recoverable.
- [ ] Sensitive flows have regression tests.
- [ ] The system fails safely when DB, cache, queue, or external services are unavailable.
- [ ] Concurrency-sensitive updates are protected against lost updates and duplicate processing.

### 18.2 Understandability

- [ ] Naming and package structure are consistent across the codebase.
- [ ] A developer can trace a request from Controller to persistence/integration without encountering multiple inconsistent call chains.
- [ ] The number of layers and abstractions is proportionate to actual complexity.
- [ ] Architecture documentation and module READMEs match the current implementation.
- [ ] Classes and methods communicate intent clearly without requiring deep implementation reading.
- [ ] Framework-specific behavior such as transactions, proxies, lazy loading, and async execution is used explicitly rather than implicitly.

### 18.3 Maintainability

- [ ] Business rules are not duplicated across Controllers, Services, validators, and listeners.
- [ ] A small business-rule change touches a small, predictable set of files.
- [ ] Formatting, static analysis, tests, and dependency checks run automatically in CI.
- [ ] Database migrations and deployment steps are automated.
- [ ] Tests are sufficient to support safe refactoring.
- [ ] Framework upgrades and dependency updates are feasible without widespread coupling.

### 18.4 Extensibility

- [ ] New providers, strategies, states, or integrations can be added through clear extension points.
- [ ] Modules expose stable APIs and hide implementation details.
- [ ] Core business logic depends on abstractions rather than Spring, JPA, or vendor-specific clients.
- [ ] Adding a new feature does not require modifying unrelated modules or central “god” classes.
- [ ] Event and message contracts support backward-compatible evolution.

### 18.5 Usability

- [ ] API response and error formats are consistent.
- [ ] API versioning and deprecation policies are clear.
- [ ] OpenAPI documentation matches actual behavior.
- [ ] Errors are actionable for clients without exposing internal details.
- [ ] Local setup, test execution, and module integration are straightforward and documented.
- [ ] Configuration errors fail fast with clear messages.

> **Note**: section 18 does not replace sections 1–17. Complete it last, based on aggregated evidence from the detailed review.

---

## How to use this checklist

1. **Identify the actual architecture first** (sections 1–3). Do not grade a simple layered application as if it must implement Clean Architecture or DDD.
2. **Review code by module and flow** (sections 4–12). Trace representative read and write requests end to end and cite specific classes/methods/line numbers.
3. **Verify runtime behavior** (sections 5, 10, 14–17). Query count, transaction behavior, thread usage, security, and performance cannot be confirmed reliably from class names alone.
4. **Review system-level quality gates** (sections 13–17). Inspect CI/CD, dependency management, deployment configuration, dashboards, and alerts.
5. For items that cannot be verified from documentation or static reading alone, write: **“Not yet verified — requires running the application/tool/test.”** Do not guess.

---

## Suggested Java/Spring verification tools

- Formatting/lint: Spotless, Checkstyle
- Static analysis: SonarQube/SonarCloud, PMD, SpotBugs, Error Prone
- Architecture rules: ArchUnit
- Testing: JUnit 5, AssertJ, Mockito, Spring Boot Test, Testcontainers
- Database/migrations: Flyway or Liquibase
- Query inspection: Hibernate SQL logs/statistics, datasource-proxy, p6spy, APM
- API documentation: springdoc-openapi
- Security scanning: OWASP Dependency-Check, Snyk, Dependabot/Renovate
- Observability: Spring Boot Actuator, Micrometer, OpenTelemetry
- Resilience: Resilience4j

---

# Review Decision Template

Use this format at the end of a review:

```markdown
## Review Result

- Decision: APPROVE | APPROVE WITH COMMENTS | REQUEST CHANGES
- Scope reviewed: ...
- Runtime verification performed: Yes | No | Partial

## Mandatory Violations

1. [File:line] Violated Part I rule: ...
   - Impact: ...
   - Required change: ...

## Quality Findings

1. [Severity: Critical/High/Medium/Low] [File:line] ...
   - Evidence: ...
   - Recommendation: ...

## Not Yet Verified

- ... — requires running the application/tool/test.

## Positive Findings

- ...
```
