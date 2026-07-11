# Scopery Backend — Technical Specification

> **As-is reference** — reflects the current codebase state (Phases 0–10.1 complete).  
> Covers: architecture, folder structure, database schema, business rules, and API reference.

> **API path versioning:** All endpoints use the unversioned base path `/api/...`. Legacy prefixes `/api/v1` and `/api/v2` are intentionally removed — no backward-compatibility aliases are provided.

---

## Table of Contents

1. [Architecture Overview](#1-architecture-overview)
2. [Folder / Package Structure](#2-folder--package-structure)
3. [Infrastructure & Runtime](#3-infrastructure--runtime)
4. [Authentication](#4-authentication)
5. [Standard API Conventions](#5-standard-api-conventions)
6. [Database Schema](#6-database-schema)
7. [Business Rules](#7-business-rules)
8. [API Reference — IAM](#8-api-reference--iam)
9. [API Reference — Workspace](#9-api-reference--workspace)
10. [API Reference — Event Registry](#10-api-reference--event-registry)
11. [API Reference — Notification](#11-api-reference--notification)
12. [API Reference — AI Agent](#12-api-reference--ai-agent)
13. [API Reference — Knowledge](#13-api-reference--knowledge)
14. [Appendix A — Enums](#appendix-a--enums)
15. [Appendix B — Seeded Admin Account](#appendix-b--seeded-admin-account)
16. [Appendix C — Quick Start (Next.js)](#appendix-c--quick-start-nextjs)

---

## 1. Architecture Overview

**Scopery Backend** is a **DDD-oriented modular monolith** built on:

| Technology | Version |
|---|---|
| Java | 21 |
| Spring Boot | 3.4.x |
| Build tool | Maven |
| Database | PostgreSQL + Flyway migrations |
| ORM | Spring Data JPA (Hibernate) |
| Cache / Queue | Redis |
| API docs | SpringDoc OpenAPI 3 (Swagger UI) |

### 1.1 Module Boundaries

Six top-level modules, each a self-contained vertical slice:

| Module | Package | Responsibility |
|---|---|---|
| `iam` | `modules/iam` | Users, roles, rights, permissions, access grants, authorization |
| `workspace` | `modules/workspace` | Organizations, workspaces, members, teams, invitations, join requests, context, onboarding |
| `eventregistry` | `modules/eventregistry` | Cross-module event catalog (event definitions + variables) |
| `notification` | `modules/notification` | Email templates, rules, deliveries, outbox |
| `aiagent` | `modules/aiagent` | AI providers, models, deployments, agents, prompts, event configs, usage policies, executions, playground |
| `knowledge` | `modules/knowledge` | Document types |

### 1.2 Layer Dependency Rules

```
http → application → domain
infrastructure → domain

common    ← may be used by any layer
platform  ← may be used by http / application layers
```

Strict prohibitions:
- `domain` must **never** import `jakarta.persistence` or `org.springframework.web`
- Controllers must **never** return JPA entities or domain objects
- `common` / `platform` must **never** depend on `modules`

### 1.3 Shared Kernel per Module

Every module that has more than one sub-module owns a `shared/` kernel:

```
shared/
├── constant/     ← error codes, entity types, action names, table names, API paths, sort fields
├── activity/     ← {Module}ActivityLogger (wraps ActivityLogService)
├── error/        ← {Module}ErrorCatalog (enum implementing ErrorCatalog)
└── util/         ← enum parsers, value-object helpers
```

### 1.4 Action + QueryService Pattern

All modules use **write-read separation** — no monolithic `*ApplicationService` allowed:

| Class type | Annotation | Method | Transactional |
|---|---|---|---|
| `*Action` | `@Component` | `execute(Command)` | `@Transactional` |
| `*QueryService` | `@Service` | `getX()`, `searchX()`, `listX()` | `@Transactional(readOnly=true)` |

Every `*Action.execute()` receives a **Command record** — never raw primitives or Request objects.

### 1.5 Error Catalog Pattern

Module-specific errors are grouped in an `ErrorCatalog` enum per module:

```java
// Every entry carries: errorCode (String), defaultMessage (String), httpStatus (HttpStatus)
enum AiAgentErrorCatalog implements ErrorCatalog { ... }
enum WorkspaceErrorCatalog implements ErrorCatalog { ... }
```

Exceptions are thrown via factory classes (`AiAgentExceptions`, etc.) — never direct constructors.  
`GlobalExceptionHandler` handles the generic `AppException` from `common/exception/` and maps `httpStatus` from the catalog entry.

---

## 2. Folder / Package Structure

```
src/main/java/com/company/scopery/
├── ScoperyApplication.java
├── modules/
│   ├── aiagent/
│   │   ├── provider/
│   │   ├── aimodel/
│   │   ├── deployment/
│   │   ├── capability/
│   │   ├── agent/
│   │   ├── prompt/
│   │   ├── eventconfig/
│   │   ├── usagepolicy/
│   │   ├── execution/
│   │   ├── playground/
│   │   ├── providersecret/
│   │   └── shared/
│   │       ├── constant/
│   │       ├── activity/
│   │       ├── error/
│   │       └── util/
│   ├── eventregistry/
│   │   ├── eventdefinition/
│   │   └── shared/
│   ├── iam/
│   │   ├── auth/
│   │   ├── user/
│   │   ├── role/
│   │   ├── right/
│   │   ├── resource/
│   │   ├── grant/
│   │   ├── roleassignment/
│   │   ├── permission/
│   │   ├── ownerpolicy/
│   │   ├── authorization/
│   │   └── shared/
│   ├── workspace/
│   │   ├── organization/
│   │   ├── workspace/
│   │   ├── member/
│   │   ├── team/
│   │   ├── invitation/
│   │   ├── joinrequest/
│   │   ├── context/
│   │   ├── onboarding/
│   │   ├── orgmember/
│   │   ├── orginvitation/
│   │   ├── orgteam/
│   │   └── shared/
│   ├── notification/
│   │   ├── emailtemplate/
│   │   ├── emailrule/
│   │   ├── emaildelivery/
│   │   ├── emailoutbox/
│   │   └── shared/
│   └── knowledge/
│       ├── documenttype/
│       └── shared/
├── common/
│   ├── response/        ← ApiResponse, ErrorResponse
│   ├── exception/       ← AppException, ErrorCatalog interface, legacy exception types
│   ├── pagination/      ← PageResponse, PageRequestUtils
│   ├── audit/           ← AuditableJpaEntity, ActivityLogService
│   └── validation/
├── platform/
│   ├── web/             ← GlobalExceptionHandler, RequestContext, HealthController
│   ├── logging/         ← RequestLoggingFilter
│   ├── config/          ← OpenApiConfig
│   └── scheduler/       ← ScheduledJobRegistry
├── integration/
│   ├── ai/              ← AiProviderAdapterRegistry, adapters per provider type
│   └── redis/
└── bootstrap/
    └── seed/            ← AdminUserSeedInitializer, permission/role seeders
```

### 2.1 Sub-module Structure

Every sub-module follows this layout:

```
{sub-module}/
├── http/
│   ├── controller/
│   │   └── {Entity}Controller.java          ← @RestController, routes only
│   └── request/
│       ├── Create{Entity}Request.java        ← record + Bean Validation
│       └── Update{Entity}Request.java
├── application/
│   ├── action/
│   │   ├── Create{Entity}Action.java         ← @Component, execute(Command), @Transactional
│   │   └── Update{Entity}Action.java
│   ├── service/
│   │   └── {Entity}QueryService.java         ← @Service, @Transactional(readOnly=true)
│   ├── listeners/                            ← @EventListener, @TransactionalEventListener
│   ├── jobs/                                 ← @Scheduled, registered in ScheduledJobRegistry
│   ├── command/
│   │   └── Create{Entity}Command.java        ← record, no validation annotations
│   ├── query/
│   │   └── Search{Entity}Query.java          ← record
│   └── response/
│       └── {Entity}Response.java             ← record, returned from controller
├── domain/
│   ├── model/
│   │   ├── {Entity}.java                     ← aggregate root (record or class)
│   │   └── {Entity}Repository.java           ← interface, zero framework imports
│   ├── enums/
│   │   └── {Entity}Status.java
│   └── valueobject/
│       └── {Entity}Code.java
└── infrastructure/
    ├── persistence/
    │   ├── entity/                           ← aiagent module only
    │   │   └── {Entity}JpaEntity.java
    │   ├── {Entity}JpaEntity.java            ← other modules (directly in persistence/)
    │   ├── SpringData{Entity}JpaRepository.java
    │   └── Jpa{Entity}Repository.java
    └── mapper/
        └── {Entity}PersistenceMapper.java
```

---

## 3. Infrastructure & Runtime

| Service | Internal (Docker) | External (localhost) |
|---|---|---|
| Backend | `scopery-backend:8080` | `http://localhost:8080` |
| PostgreSQL | `scopery-postgres:5432` | `localhost:5433` |
| Redis | `scopery-redis:6379` | `localhost:6379` |

```bash
docker compose up -d
```

**Swagger UI:** `http://localhost:8080/swagger-ui.html`  
**OpenAPI JSON:** `http://localhost:8080/v3/api-docs`

### 3.1 Request Tracing

`RequestLoggingFilter` (in `platform/logging/`) runs on every request:
- Generates a `traceId` (UUID) unless `X-Trace-Id` request header is provided
- Puts `traceId` in SLF4J MDC key `"traceId"` and in `RequestContextHolder`
- Returns `X-Trace-Id` response header
- Logs: method, path, status, durationMs, ip, traceId

### 3.2 Audit Fields

All business JPA entities extend `AuditableJpaEntity`:

| Column | Type | Managed by |
|---|---|---|
| `created_at` | TIMESTAMP | Spring Data JPA `@CreatedDate` |
| `updated_at` | TIMESTAMP | Spring Data JPA `@LastModifiedDate` |
| `created_by` | VARCHAR(100) | Auditor bean (currently `"SYSTEM"`) |
| `updated_by` | VARCHAR(100) | Auditor bean (currently `"SYSTEM"`) |

**Mapper rule:** `toJpaEntity()` must set `entity.setCreatedAt(domain.createdAt())` from the domain object so Spring Data uses `merge()` consistently.

### 3.3 Activity Log

Every state-changing action is recorded via `ActivityLogService`:

```java
activityLogger.logSuccess(
    entityType,   // e.g. WorkspaceEntityTypes.WORKSPACE
    entityId,     // UUID of the affected entity
    action,       // e.g. WorkspaceActivityActions.CREATE_WORKSPACE
    message       // human-readable summary
);
```

Table: `app_activity_log`. Failures are logged as WARN only — never throw.

---

## 4. Authentication

### 4.1 Token Flow

```
POST /api/iam/auth/login
  → server sets 2 HTTP-only cookies:
      access_token   (~15 min)
      refresh_token  (~7 days, path=/api/iam/auth only)
  → browser sends access_token cookie automatically on each request
  → when expired → POST /api/iam/auth/refresh
  → logout       → POST /api/iam/auth/logout
```

### 4.2 Sending the Token

| Client | Method |
|---|---|
| Browser | `access_token` HTTP-only cookie (automatic) |
| Postman / mobile | `Authorization: Bearer <token>` header |

### 4.3 CSRF (Double Submit Cookie)

All **POST / PUT / PATCH / DELETE** except `/api/iam/auth/**` require CSRF:

1. Server sets `XSRF-TOKEN` cookie (JS-readable, not HttpOnly)
2. Frontend reads `XSRF-TOKEN` and sends it as `X-XSRF-TOKEN` header

```js
axios.defaults.withCredentials = true;
axios.defaults.xsrfCookieName = 'XSRF-TOKEN';
axios.defaults.xsrfHeaderName = 'X-XSRF-TOKEN';
```

---

## 5. Standard API Conventions

### 5.1 Success Response

```json
{ "success": true, "data": { ... }, "timestamp": "2026-06-22T07:00:00Z" }
```

Use: `ApiResponse.success(data)`

### 5.2 Paginated Response (`data` field)

```json
{
  "items": [],
  "page": 0,
  "size": 20,
  "totalElements": 100,
  "totalPages": 5,
  "first": true,
  "last": false
}
```

Use: `ApiResponse.success(PageResponse.from(springPage))`. Controllers return `ApiResponse<PageResponse<T>>`.

### 5.3 Error Response

```json
{
  "success": false,
  "status": 422,
  "errorCode": "WORKSPACE_CODE_ALREADY_EXISTS",
  "message": "Workspace code already exists: ACME",
  "details": [],
  "traceId": "uuid",
  "timestamp": "..."
}
```

### 5.4 HTTP Status Convention

| Code | When |
|---|---|
| 200 | OK (default) |
| 201 | Created (POST create endpoints) |
| 400 | Invalid input / validation error |
| 401 | Not authenticated |
| 403 | Authenticated but no permission |
| 404 | Resource not found |
| 409 | Conflict / duplicate |
| 422 | Business rule violation (wrong state, dependency inactive, etc.) |
| 500 | Unexpected server error |

### 5.5 Request / Command / Query / Response Types

| Type | Layer | Purpose |
|---|---|---|
| `*Request` | `http/request/` | HTTP input DTO. Has Bean Validation annotations (`@NotBlank`, `@Email`, etc.) |
| `*Command` | `application/command/` | Internal write use-case object. No validation annotations. |
| `*Query` | `application/query/` | Internal read parameters. |
| `*Response` | `application/response/` | Output DTO returned to API callers. Mapped from domain, never from JPA entity. |

### 5.6 Repository Types

| Type | Location | Purpose |
|---|---|---|
| Domain repository | `domain/{Entity}Repository.java` | Interface. No JPA/Spring imports. |
| JPA repository | `infrastructure/persistence/Jpa{Entity}Repository.java` | Implements domain interface using JPA. Uses `saveAndFlush()`. |
| Spring Data repo | `infrastructure/persistence/SpringData{Entity}JpaRepository.java` | Extends `JpaRepository`. Works only with JPA entity. |
| Mapper | `infrastructure/mapper/{Entity}PersistenceMapper.java` | Converts domain ↔ JPA entity. |

---

## 6. Database Schema

### 6.1 Naming Convention

| Scope | Table Prefix | Example |
|---|---|---|
| Platform / cross-module | `app_` | `app_activity_log`, `app_event_definition` |
| AI Agent module | `aiagent_` | `aiagent_provider`, `aiagent_model` |
| IAM module | `iam_` | `iam_user`, `iam_role` |
| Workspace module | `workspace_` | `workspace_organization`, `workspace_workspace` |
| Organization layer | `org_` | `org_member`, `org_team` |
| Notification module | `notification_` | `notification_email_template` |
| Knowledge module | `knowledge_` | `knowledge_document_type` |

**Constraint naming:**
- PK: `pk_{table_name}`
- Unique: `uq_{table_name}_{column(s)}`
- FK: `fk_{table}_{referenced_table}`
- Index: `idx_{table}_{column}`
- Check: `ck_{table}_{rule}`

---

### 6.2 Platform Tables (`app_*`)

#### `app_activity_log` (V1)

Append-only log for every state-changing business action.

| Column | Type | Constraints |
|---|---|---|
| `id` | UUID | PK |
| `module_code` | VARCHAR(100) | NOT NULL — e.g. `AIAGENT`, `WORKSPACE` |
| `entity_type` | VARCHAR(100) | e.g. `PROVIDER`, `WORKSPACE` |
| `entity_id` | VARCHAR(100) | UUID of the affected entity |
| `action` | VARCHAR(100) | NOT NULL — e.g. `CREATE_PROVIDER` |
| `status` | VARCHAR(50) | NOT NULL — `SUCCESS` \| `FAILURE` |
| `actor_id` | VARCHAR(100) | null until Spring Security is added |
| `actor_name` | VARCHAR(255) | |
| `trace_id` | VARCHAR(100) | from MDC |
| `message` | TEXT | |
| `metadata` | JSONB | |
| `created_at` | TIMESTAMP | NOT NULL |

Indexes: `module_code`, `entity_type`, `entity_id`, `action`, `created_at`, `trace_id`

---

#### `app_audit_event` (V37)

Immutable, structured audit trail (richer than `app_activity_log`).

| Column | Type | Notes |
|---|---|---|
| `id` | UUID | PK |
| `event_type` | VARCHAR(200) | NOT NULL |
| `actor_id` | UUID | |
| `actor_type` | VARCHAR(50) | `USER` \| `SERVICE` \| `SYSTEM` |
| `resource_type` | VARCHAR(200) | |
| `resource_ref_id` | UUID | |
| `organization_id` | UUID | |
| `workspace_id` | UUID | |
| `before_state` | JSONB | |
| `after_state` | JSONB | |
| `reason` | TEXT | |
| `trace_id` | VARCHAR(100) | |
| `occurred_at` | TIMESTAMP | NOT NULL — no `updated_at` (immutable) |

---

#### `app_transactional_outbox` (V37)

Transactional outbox for reliable event publishing.

| Column | Type | Notes |
|---|---|---|
| `id` | UUID | PK |
| `aggregate_type` | VARCHAR(200) | NOT NULL |
| `aggregate_id` | UUID | NOT NULL |
| `event_type` | VARCHAR(200) | NOT NULL |
| `payload` | JSONB | NOT NULL |
| `trace_id` | VARCHAR(100) | |
| `status` | VARCHAR(50) | `PENDING` \| `PUBLISHED` \| `FAILED` |
| `occurred_at` | TIMESTAMP | NOT NULL |
| `published_at` | TIMESTAMP | |
| `retry_count` | INT | DEFAULT 0 |

Partial index on `(status, occurred_at)` WHERE `status = 'PENDING'` for polling.

---

#### `app_idempotency_key` (V38)

Request deduplication cache.

| Column | Type | Notes |
|---|---|---|
| `key_hash` | VARCHAR(64) | PK (SHA-256 of idempotency key) |
| `request_method` | VARCHAR(20) | NOT NULL |
| `request_path` | VARCHAR(500) | NOT NULL |
| `response_status` | INT | NOT NULL |
| `response_body` | JSONB | NOT NULL |
| `content_type` | VARCHAR(200) | |
| `expires_at` | TIMESTAMP | NOT NULL |
| `created_at` | TIMESTAMP | NOT NULL |

Index on `expires_at` for TTL cleanup job.

---

### 6.3 IAM Tables (`iam_*`)

#### `iam_user` (V14 + V15)

| Column | Type | Constraints |
|---|---|---|
| `id` | UUID | PK |
| `username` | VARCHAR(100) | NOT NULL, UNIQUE |
| `email` | VARCHAR(255) | NOT NULL, UNIQUE |
| `full_name` | VARCHAR(255) | |
| `password_hash` | VARCHAR(255) | added V15 |
| `status` | VARCHAR(50) | NOT NULL — `ACTIVE` \| `INACTIVE` \| `SUSPENDED` |
| `created_at` | TIMESTAMP | NOT NULL |
| `updated_at` | TIMESTAMP | NOT NULL |
| `created_by` | VARCHAR(100) | |
| `updated_by` | VARCHAR(100) | |

---

#### `iam_role` (V14 + V20 + V24)

| Column | Type | Notes |
|---|---|---|
| `id` | UUID | PK |
| `code` | VARCHAR(100) | NOT NULL, UNIQUE |
| `name` | VARCHAR(255) | NOT NULL |
| `description` | TEXT | |
| `role_scope` | VARCHAR(50) | `SYSTEM` \| `WORKSPACE` (V20) |
| `role_source` | VARCHAR(50) | `BUILT_IN` \| `CUSTOM` (V20) |
| `parent_role_id` | UUID | FK self-reference (V20) |
| `workspace_id` | UUID | workspace-scoped roles only (V24) |
| `deleted_at` | TIMESTAMP | soft delete (V20) |
| `deleted_by` | VARCHAR(100) | |
| `status` | VARCHAR(50) | NOT NULL |
| `created_at` / `updated_at` / `created_by` / `updated_by` | | standard audit |

---

#### `iam_right` (V14)

Legacy backing codes. New code should use `iam_permission_action` instead.

| Column | Type | Notes |
|---|---|---|
| `id` | UUID | PK |
| `code` | VARCHAR(100) | NOT NULL, UNIQUE — e.g. `UPDATE_WORKSPACE` |
| `name` | VARCHAR(255) | NOT NULL |
| `module` | VARCHAR(100) | |
| `description` | TEXT | |
| `status` | VARCHAR(50) | NOT NULL |
| `created_at` / `updated_at` | | |

---

#### `iam_auth_resource` (V14 + V18 + V36)

Represents a resource that can be protected by access grants.

| Column | Type | Notes |
|---|---|---|
| `id` | UUID | PK |
| `code` | VARCHAR(100) | NOT NULL |
| `resource_type` | VARCHAR(100) | NOT NULL — `GLOBAL` \| `ORGANIZATION` \| `WORKSPACE` \| `TEAM` |
| `name` | VARCHAR(255) | NOT NULL |
| `description` | TEXT | |
| `ref_id` | UUID | FK to the business entity (V18) |
| `owner_user_id` | UUID | (V18) |
| `workspace_id` | UUID | (V18) |
| `organization_id` | UUID | (V36) |
| `parent_resource_id` | UUID | FK self-reference (V18, backfilled V36) |
| `visibility` | VARCHAR(100) | `PRIVATE` \| `WORKSPACE` \| `SPACE` \| `PUBLIC_LINK` \| `RESTRICTED` |
| `version` | INT | optimistic locking (V36) |
| `status` | VARCHAR(50) | NOT NULL |
| `created_at` / `updated_at` | | |

Unique constraint: `(code, resource_type)`

---

#### `iam_access_grant` (V14 + V17 + V36)

Grants a subject authority over a resource.

| Column | Type | Notes |
|---|---|---|
| `id` | UUID | PK |
| `subject_type` | VARCHAR(50) | `USER` \| `TEAM` \| `ROLE` |
| `subject_id` | UUID | NOT NULL |
| `resource_id` | UUID | NOT NULL, FK → `iam_auth_resource` |
| `role_id` | UUID | optional FK → `iam_role` |
| `effect` | VARCHAR(50) | `ALLOW` \| `DENY` (V17) |
| `scope_type` | VARCHAR(100) | (V17) |
| `scope_ref_id` | UUID | (V17) |
| `workspace_id` | UUID | for workspace-scoped grants (V17) |
| `kind` | VARCHAR(50) | `OWNER` \| `DIRECT` \| `TEAM` \| `ROLE` \| `DELEGATED` (V36) |
| `source_policy_id` | UUID | FK → `iam_owner_policy` (V36) |
| `can_delegate` | BOOLEAN | (V36) |
| `delegation_depth` | INT | (V36) |
| `expires_at` | TIMESTAMP | (V36) |
| `condition_json` | JSONB | (V36) |
| `reason` | TEXT | (V36) |
| `version` | INT | optimistic locking (V36) |
| `status` | VARCHAR(50) | NOT NULL |
| `granted_by` | UUID | |
| `created_at` / `updated_at` | | |

---

#### `iam_access_grant_right` (V14)

Legacy bridge between grants and rights.

| Column | Type |
|---|---|
| `grant_id` | UUID (PK, FK → `iam_access_grant`) |
| `right_id` | UUID (PK, FK → `iam_right`) |

---

#### `iam_access_grant_permission_action` (V28)

Current authority model — permission action attached to a grant.

| Column | Type |
|---|---|
| `grant_id` | UUID (PK, FK → `iam_access_grant`) |
| `permission_action_id` | UUID (PK, FK → `iam_permission_action`) |

Backfilled from `iam_access_grant_right` via `iam_permission_action.right_id` bridge.

---

#### `iam_role_assignment` (V16 + V19)

| Column | Type | Notes |
|---|---|---|
| `id` | UUID | PK |
| `assignee_type` | VARCHAR(50) | `USER` \| `TEAM` (V19 — migrated from `user_id`) |
| `assignee_id` | UUID | NOT NULL |
| `role_id` | UUID | NOT NULL, FK → `iam_role` |
| `workspace_id` | UUID | null = system/global scope |
| `assigned_by` | UUID | |
| `status` | VARCHAR(50) | NOT NULL |
| `created_at` / `updated_at` | | |

Two partial unique indexes: one for workspace-scoped `(assignee_type, assignee_id, role_id, workspace_id)` WHERE `workspace_id IS NOT NULL`; one for global WHERE `workspace_id IS NULL`.

---

#### `iam_permission` (V26 + V27)

| Column | Type | Notes |
|---|---|---|
| `id` | UUID | PK |
| `code` | VARCHAR(100) | NOT NULL, UNIQUE |
| `module_code` | VARCHAR(100) | NOT NULL |
| `name` | VARCHAR(255) | NOT NULL |
| `description` | TEXT | |
| `resource_scope_level` | VARCHAR(50) | `SYSTEM` \| `ORGANIZATION` \| `WORKSPACE` |
| `data_access_policy` | VARCHAR(50) | `OWNER_ONLY` \| `ANCESTOR_INHERITED` \| `SCOPE_WIDE` |
| `permission_category` | VARCHAR(100) | `SECURITY` \| `RESOURCE_ADMIN` \| `GOVERNANCE` \| `NOTIFICATION_ADMIN` \| `ORGANIZATION_ADMIN` \| `WORKSPACE_ADMIN` \| `ACCESS_CONTROL` \| `TEAM_ADMIN` \| `MEMBER_ADMIN` \| `CONTENT_ADMIN` |
| `assignable_subject_types` | VARCHAR(200) | comma-separated: `USER`, `TEAM`, `ROLE` |
| `risk_level` | VARCHAR(50) | `LOW` \| `MEDIUM` \| `HIGH` \| `CRITICAL` |
| `status` | VARCHAR(50) | NOT NULL |
| `created_at` / `updated_at` | | |

---

#### `iam_permission_action` (V26)

| Column | Type | Notes |
|---|---|---|
| `id` | UUID | PK |
| `permission_id` | UUID | NOT NULL, FK → `iam_permission` |
| `action_code` | VARCHAR(100) | NOT NULL |
| `name` | VARCHAR(255) | |
| `description` | TEXT | |
| `right_id` | UUID | optional FK → `iam_right` (legacy bridge) |
| `status` | VARCHAR(50) | NOT NULL |
| `created_at` / `updated_at` | | |

Unique: `(permission_id, action_code)`

---

#### `iam_permission_action_dependency` (V26)

| Column | Type |
|---|---|
| `action_id` | UUID (PK, FK → `iam_permission_action`) |
| `required_action_id` | UUID (PK, FK → `iam_permission_action`) |

CHECK: `action_id <> required_action_id` (no self-dependency)

---

#### `iam_owner_policy` (V36)

Resource-type-specific bundles of actions granted automatically to an owner.

| Column | Type | Notes |
|---|---|---|
| `id` | UUID | PK |
| `resource_type` | VARCHAR(100) | NOT NULL |
| `action_bundle` | TEXT | comma-separated action codes |
| `inheritance_scope` | VARCHAR(50) | `SELF_ONLY` \| `DESCENDANTS` \| `SPECIFIC_CHILDREN` |
| `can_delegate` | BOOLEAN | NOT NULL |
| `delegation_depth` | INT | |
| `description` | TEXT | |
| `status` | VARCHAR(50) | NOT NULL |
| `created_at` / `updated_at` | | |

Partial unique index: one ACTIVE policy per `resource_type`. Seeded for `ORGANIZATION`, `WORKSPACE`, `TEAM`.

---

### 6.4 Workspace Tables

#### `workspace_organization` (V13 + V36)

| Column | Type | Notes |
|---|---|---|
| `id` | UUID | PK |
| `name` | VARCHAR(255) | NOT NULL |
| `code` | VARCHAR(100) | NOT NULL, UNIQUE |
| `description` | TEXT | |
| `owner_user_id` | UUID | UUID reference, no FK to `iam_user` at this stage |
| `status` | VARCHAR(50) | NOT NULL — `ACTIVE` \| `ARCHIVED` |
| `version` | INT | optimistic locking (V36) |
| `created_at` / `updated_at` / `created_by` / `updated_by` | | |

---

#### `workspace_workspace` (V13 + V23 + V36)

| Column | Type | Notes |
|---|---|---|
| `id` | UUID | PK |
| `organization_id` | UUID | NOT NULL, FK → `workspace_organization` |
| `name` | VARCHAR(255) | NOT NULL |
| `code` | VARCHAR(100) | NOT NULL |
| `description` | TEXT | |
| `default_visibility` | VARCHAR(50) | `PRIVATE` \| `PUBLIC` |
| `join_policy` | VARCHAR(50) | NOT NULL DEFAULT `INVITE_ONLY` — `INVITE_ONLY` \| `REQUEST_TO_JOIN` \| `INVITE_OR_REQUEST` \| `DISABLED` (V23) |
| `owner_user_id` | UUID | |
| `status` | VARCHAR(50) | NOT NULL — `ACTIVE` \| `ARCHIVED` |
| `version` | INT | optimistic locking (V36) |
| `created_at` / `updated_at` / `created_by` / `updated_by` | | |

Unique: `(organization_id, code)`

---

#### `workspace_member` (V13)

| Column | Type | Notes |
|---|---|---|
| `id` | UUID | PK |
| `workspace_id` | UUID | NOT NULL, FK → `workspace_workspace` |
| `user_id` | UUID | NOT NULL |
| `status` | VARCHAR(50) | NOT NULL — `ACTIVE` \| `INACTIVE` |
| `joined_at` | TIMESTAMP | |
| `created_at` / `updated_at` / `created_by` / `updated_by` | | |

Unique: `(workspace_id, user_id)`

---

#### `workspace_team` (V13)

> **Note:** As of V33+, teams are now owned by Organization (`org_team`). `workspace_team` is legacy and is backfilled to `org_team`.

| Column | Type | Notes |
|---|---|---|
| `id` | UUID | PK |
| `workspace_id` | UUID | NOT NULL, FK → `workspace_workspace` |
| `name` | VARCHAR(255) | NOT NULL |
| `code` | VARCHAR(100) | NOT NULL |
| `description` | TEXT | |
| `status` | VARCHAR(50) | NOT NULL |
| `created_at` / `updated_at` / `created_by` / `updated_by` | | |

Unique: `(workspace_id, code)`

---

#### `workspace_team_member` (V13)

| Column | Type |
|---|---|
| `team_id` | UUID (PK, FK → `workspace_team`) |
| `user_id` | UUID (PK) |

---

#### `workspace_invitation` (V23)

| Column | Type | Notes |
|---|---|---|
| `id` | UUID | PK |
| `workspace_id` | UUID | NOT NULL, FK → `workspace_workspace` |
| `invitation_code_hash` | VARCHAR(64) | NOT NULL, UNIQUE — SHA-256 of the raw code, **never stored in plaintext** |
| `invitation_code_hint` | VARCHAR(20) | First 4 characters of the raw code, for display |
| `invited_email` | VARCHAR(255) | optional — null for open (multi-use) invitations |
| `invited_by_user_id` | UUID | |
| `max_uses` | INT | null = unlimited |
| `used_count` | INT | NOT NULL DEFAULT 0 |
| `expires_at` | TIMESTAMP | null = no expiry |
| `status` | VARCHAR(50) | NOT NULL — `PENDING` \| `ACCEPTED` \| `EXPIRED` \| `REVOKED` |
| `created_at` / `updated_at` / `created_by` / `updated_by` | | |

---

#### `workspace_join_request` (V23)

| Column | Type | Notes |
|---|---|---|
| `id` | UUID | PK |
| `workspace_id` | UUID | NOT NULL, FK → `workspace_workspace` |
| `requested_by_user_id` | UUID | NOT NULL |
| `message` | TEXT | optional user message |
| `reviewed_by_user_id` | UUID | null until reviewed |
| `review_note` | TEXT | rejection reason |
| `status` | VARCHAR(50) | NOT NULL DEFAULT `PENDING` — `PENDING` \| `APPROVED` \| `REJECTED` \| `CANCELLED` |
| `created_at` / `updated_at` / `created_by` / `updated_by` | | |

Partial unique index: one `PENDING` request per `(workspace_id, requested_by_user_id)`.

---

#### `workspace_user_context` (V23)

Per-user current workspace selection. PK = `user_id` (one row per user).

| Column | Type | Notes |
|---|---|---|
| `user_id` | UUID | PK |
| `current_workspace_id` | UUID | FK → `workspace_workspace` |
| `created_at` / `updated_at` | | |

---

#### `workspace_onboarding_state` (V23)

Per-user onboarding state machine. One row per user.

| Column | Type | Notes |
|---|---|---|
| `id` | UUID | PK |
| `user_id` | UUID | NOT NULL, UNIQUE |
| `status` | VARCHAR(50) | `IN_PROGRESS` \| `WAITING_FOR_APPROVAL` \| `COMPLETED` \| `CANCELLED` \| `FAILED` |
| `current_step` | VARCHAR(100) | `CHOOSE_WORKSPACE_OPTION` \| `CREATE_WORKSPACE` \| `ENTER_INVITATION_CODE` \| `WAITING_JOIN_APPROVAL` \| `COMPLETED` |
| `selected_option` | VARCHAR(50) | `CREATE_WORKSPACE` \| `JOIN_WITH_INVITATION` |
| `target_workspace_id` | UUID | set after a workspace is identified |
| `created_organization_id` | UUID | set when a new org is created |
| `created_workspace_id` | UUID | set when a new workspace is created |
| `invitation_id` | UUID | FK to an accepted invitation |
| `join_request_id` | UUID | FK to an active join request (legacy path) |
| `failure_reason` | TEXT | |
| `created_at` / `updated_at` | | |

---

#### `org_member` (V29 + V35)

Organization-level membership (owns workspace memberships).

| Column | Type | Notes |
|---|---|---|
| `id` | UUID | PK |
| `organization_id` | UUID | NOT NULL, FK → `workspace_organization` |
| `user_id` | UUID | NOT NULL, FK → `iam_user` (V35) |
| `membership_type` | VARCHAR(50) | `OWNER` \| `ADMIN` \| `MEMBER` |
| `status` | VARCHAR(50) | NOT NULL — `ACTIVE` \| `SUSPENDED` \| `REMOVED` |
| `source` | VARCHAR(100) | how the member joined (V35) |
| `joined_at` | TIMESTAMP | |
| `suspended_at` | TIMESTAMP | (V35) |
| `removed_at` | TIMESTAMP | (V35) |
| `version` | INT | optimistic locking (V35) |
| `created_at` / `updated_at` / `created_by` / `updated_by` | | |

Unique: `(organization_id, user_id)`

---

#### `org_invitation` (V30)

Organization-level invitation.

| Column | Type | Notes |
|---|---|---|
| `id` | UUID | PK |
| `organization_id` | UUID | NOT NULL, FK → `workspace_organization` |
| `invitee_email` | VARCHAR(255) | NOT NULL |
| `invitee_user_id` | UUID | resolved after acceptance |
| `membership_type` | VARCHAR(50) | |
| `status` | VARCHAR(50) | `PENDING` \| `ACCEPTED` \| `DECLINED` \| `EXPIRED` \| `CANCELLED` |
| `invited_by` | UUID | |
| `token` | VARCHAR(255) | UNIQUE — secure acceptance token |
| `expires_at` | TIMESTAMP | |
| `responded_at` | TIMESTAMP | |
| `created_at` / `updated_at` / `created_by` / `updated_by` | | |

---

#### `org_team` (V33 + V35)

Teams owned by Organization (not Workspace).

| Column | Type | Notes |
|---|---|---|
| `id` | UUID | PK |
| `organization_id` | UUID | NOT NULL, FK → `workspace_organization` |
| `name` | VARCHAR(255) | NOT NULL |
| `code` | VARCHAR(100) | NOT NULL |
| `description` | TEXT | |
| `status` | VARCHAR(50) | NOT NULL |
| `version` | INT | optimistic locking (V35) |
| `created_at` / `updated_at` / `created_by` / `updated_by` | | |

Unique: `(organization_id, code)`

---

#### `org_team_member` (V33)

| Column | Type |
|---|---|
| `team_id` | UUID (PK, FK → `org_team`) |
| `user_id` | UUID (PK, FK → `iam_user`) |

---

#### `org_team_workspace_assignment` (V33)

Assigns an org-level team to a specific workspace.

| Column | Type | Notes |
|---|---|---|
| `id` | UUID | PK |
| `team_id` | UUID | FK → `org_team` |
| `workspace_id` | UUID | FK → `workspace_workspace` |
| `status` | VARCHAR(50) | NOT NULL |
| `created_at` / `updated_at` | | |

Unique: `(team_id, workspace_id)`. Teams can only be assigned to workspaces within the same organization (enforced by application logic).

---

### 6.5 AI Agent Tables (`aiagent_*`)

#### `aiagent_provider` (V2)

| Column | Type | Notes |
|---|---|---|
| `id` | UUID | PK |
| `name` | VARCHAR(255) | NOT NULL |
| `code` | VARCHAR(100) | NOT NULL, UNIQUE |
| `type` | VARCHAR(100) | NOT NULL — e.g. `OPENAI`, `ANTHROPIC`, `AZURE_OPENAI` |
| `api_base_url` | VARCHAR(500) | |
| `description` | TEXT | |
| `status` | VARCHAR(50) | NOT NULL — `ACTIVE` \| `INACTIVE` \| `DEPRECATED` |
| `created_at` / `updated_at` / `created_by` / `updated_by` | | |

Indexes: `status`, `type`, `code`

---

#### `aiagent_provider_secret` (V12)

| Column | Type | Notes |
|---|---|---|
| `id` | UUID | PK |
| `provider_id` | UUID | NOT NULL, FK → `aiagent_provider` |
| `secret_type` | VARCHAR(50) | NOT NULL — e.g. `API_KEY` |
| `encrypted_value` | TEXT | NOT NULL — AES-256 encrypted |
| `iv` | VARCHAR(255) | NOT NULL — AES IV |
| `key_version` | VARCHAR(50) | NOT NULL — for key rotation tracking |
| `masked_value` | VARCHAR(100) | NOT NULL — `sk-...****abcd` |
| `description` | TEXT | |
| `status` | VARCHAR(50) | NOT NULL — `ACTIVE` \| `INACTIVE` |
| `last_rotated_at` | TIMESTAMP | |
| `created_at` / `updated_at` / `created_by` / `updated_by` | | |

Partial unique index: one `ACTIVE` secret per `(provider_id, secret_type)`.

---

#### `aiagent_model` (V3)

| Column | Type | Notes |
|---|---|---|
| `id` | UUID | PK |
| `provider_id` | UUID | NOT NULL, FK → `aiagent_provider` |
| `name` | VARCHAR(255) | NOT NULL |
| `code` | VARCHAR(100) | NOT NULL |
| `provider_model_id` | VARCHAR(255) | NOT NULL — e.g. `gpt-4o`, `claude-3-5-sonnet-20241022` |
| `type` | VARCHAR(50) | NOT NULL — `TEXT_GENERATION` \| `EMBEDDING` \| `IMAGE_GENERATION` |
| `description` | TEXT | |
| `status` | VARCHAR(50) | NOT NULL — `ACTIVE` \| `INACTIVE` \| `DEPRECATED` |
| `created_at` / `updated_at` / `created_by` / `updated_by` | | |

Unique: `(provider_id, code)`. FK cascade: model cannot exist without provider.

---

#### `aiagent_model_deployment` (V4)

| Column | Type | Notes |
|---|---|---|
| `id` | UUID | PK |
| `model_id` | UUID | NOT NULL, FK → `aiagent_model` |
| `name` | VARCHAR(255) | NOT NULL |
| `code` | VARCHAR(100) | NOT NULL |
| `environment` | VARCHAR(50) | NOT NULL — `DEV` \| `UAT` \| `PROD` |
| `provider_deployment_id` | VARCHAR(255) | NOT NULL |
| `endpoint_url` | VARCHAR(500) | |
| `default_temperature` | NUMERIC(4,2) | CHECK: 0–2 |
| `default_max_output_tokens` | INT | CHECK: > 0 |
| `is_default` | BOOLEAN | NOT NULL DEFAULT FALSE |
| `description` | TEXT | |
| `status` | VARCHAR(50) | NOT NULL — `ACTIVE` \| `INACTIVE` \| `DEPRECATED` |
| `created_at` / `updated_at` / `created_by` / `updated_by` | | |

Unique: `(model_id, code)`. Partial unique index: only one `is_default = TRUE` per `(model_id, environment)`.

---

#### `aiagent_model_parameter_capability` (V5)

| Column | Type | Notes |
|---|---|---|
| `id` | UUID | PK |
| `model_id` | UUID | NOT NULL, FK → `aiagent_model` |
| `parameter_name` | VARCHAR(100) | NOT NULL — e.g. `temperature`, `max_tokens` |
| `api_parameter_key` | VARCHAR(255) | actual key to send to provider API |
| `support_status` | VARCHAR(50) | `SUPPORTED` \| `UNSUPPORTED` \| `UNKNOWN` |
| `value_type` | VARCHAR(50) | `INTEGER` \| `FLOAT` \| `BOOLEAN` \| `STRING` |
| `min_value` | NUMERIC(12,4) | CHECK: `min_value <= max_value` |
| `max_value` | NUMERIC(12,4) | |
| `default_value` | VARCHAR(500) | |
| `nullable` | BOOLEAN | NOT NULL DEFAULT TRUE |
| `if_null_behavior` | VARCHAR(100) | `DO_NOT_SEND_PARAMETER` \| `USE_PROVIDER_DEFAULT` — required when `nullable = FALSE` |
| `description` | TEXT | |
| `status` | VARCHAR(50) | NOT NULL |
| `created_at` / `updated_at` / `created_by` / `updated_by` | | |

Unique: `(model_id, parameter_name)`. CHECK: `nullable = FALSE` requires `if_null_behavior IS NOT NULL`.

---

#### `aiagent_agent` (V6)

| Column | Type | Notes |
|---|---|---|
| `id` | UUID | PK |
| `name` | VARCHAR(255) | NOT NULL |
| `code` | VARCHAR(100) | NOT NULL, UNIQUE |
| `type` | VARCHAR(50) | NOT NULL — e.g. `CONVERSATIONAL`, `TASK_EXECUTOR` |
| `description` | TEXT | |
| `default_model_deployment_id` | UUID | FK → `aiagent_model_deployment` (optional) |
| `output_format` | VARCHAR(50) | `TEXT` \| `JSON` \| `MARKDOWN` |
| `status` | VARCHAR(50) | NOT NULL — `ACTIVE` \| `INACTIVE` \| `DEPRECATED` |
| `created_at` / `updated_at` / `created_by` / `updated_by` | | |

---

#### `aiagent_prompt_template` (V7)

| Column | Type | Notes |
|---|---|---|
| `id` | UUID | PK |
| `agent_id` | UUID | NOT NULL, FK → `aiagent_agent` |
| `name` | VARCHAR(255) | NOT NULL |
| `code` | VARCHAR(100) | NOT NULL |
| `description` | TEXT | |
| `status` | VARCHAR(50) | NOT NULL — `ACTIVE` \| `INACTIVE` |
| `created_at` / `updated_at` / `created_by` / `updated_by` | | |

Unique: `(agent_id, code)`

---

#### `aiagent_prompt_version` (V7)

| Column | Type | Notes |
|---|---|---|
| `id` | UUID | PK |
| `template_id` | UUID | NOT NULL, FK → `aiagent_prompt_template` |
| `version_number` | INT | NOT NULL — auto-incremented per template |
| `title` | VARCHAR(255) | |
| `content` | TEXT | NOT NULL — prompt body |
| `content_format` | VARCHAR(50) | `PLAIN_TEXT` \| `MARKDOWN` \| `JINJA2` |
| `variable_schema` | JSONB | JSON Schema describing expected input variables |
| `change_note` | TEXT | |
| `status` | VARCHAR(50) | NOT NULL — `DRAFT` \| `ACTIVE` \| `ARCHIVED` |
| `created_at` / `updated_at` / `created_by` / `updated_by` | | |

Unique: `(template_id, version_number)`. Partial unique index: only one `ACTIVE` version per `template_id`.

---

#### `aiagent_event_config` (V9)

| Column | Type | Notes |
|---|---|---|
| `id` | UUID | PK |
| `code` | VARCHAR(100) | NOT NULL, UNIQUE |
| `name` | VARCHAR(255) | NOT NULL |
| `event_definition_id` | UUID | NOT NULL, FK → `app_event_definition` |
| `environment` | VARCHAR(50) | NOT NULL — `DEV` \| `UAT` \| `PROD` |
| `trigger_type` | VARCHAR(50) | NOT NULL — `AUTOMATIC` \| `MANUAL` |
| `agent_id` | UUID | NOT NULL, FK → `aiagent_agent` |
| `prompt_version_id` | UUID | NOT NULL, FK → `aiagent_prompt_version` |
| `model_deployment_id` | UUID | NOT NULL, FK → `aiagent_model_deployment` |
| `condition_expression` | TEXT | |
| `description` | TEXT | |
| `status` | VARCHAR(50) | NOT NULL — `ACTIVE` \| `INACTIVE` |
| `created_at` / `updated_at` / `created_by` / `updated_by` | | |

Partial unique index: one `ACTIVE` config per `(event_definition_id, environment)`.

---

#### `aiagent_usage_policy` (V10)

| Column | Type | Notes |
|---|---|---|
| `id` | UUID | PK |
| `code` | VARCHAR(100) | NOT NULL, UNIQUE |
| `name` | VARCHAR(255) | NOT NULL |
| `target_type` | VARCHAR(50) | NOT NULL — `GLOBAL` \| `EVENT_CONFIG` \| `AGENT` \| `MODEL_DEPLOYMENT` |
| `target_id` | UUID | null iff `target_type = GLOBAL` (enforced by CHECK) |
| `max_requests_per_period` | INT | |
| `max_tokens_per_period` | BIGINT | |
| `max_cost_per_period` | NUMERIC(19,6) | |
| `max_concurrent_requests` | INT | |
| `daily_budget` | NUMERIC(19,6) | |
| `period` | VARCHAR(50) | `HOURLY` \| `DAILY` \| `WEEKLY` \| `MONTHLY` — required when any rate-based limit is set |
| `action` | VARCHAR(50) | NOT NULL — `BLOCK` \| `WARN` \| `THROTTLE` |
| `priority` | INT | NOT NULL DEFAULT 100 |
| `description` | TEXT | |
| `status` | VARCHAR(50) | NOT NULL — `ACTIVE` \| `INACTIVE` |
| `created_at` / `updated_at` / `created_by` / `updated_by` | | |

CHECK: `target_id IS NULL` iff `target_type = 'GLOBAL'`. CHECK: at least one limit field must be non-null. CHECK: `period` required when any `max_requests_per_period` / `max_tokens_per_period` / `max_cost_per_period` is set.

Four partial unique indexes: one ACTIVE policy per target type.

---

#### `aiagent_execution_log` (V11)

| Column | Type | Notes |
|---|---|---|
| `id` | UUID | PK |
| `request_id` | VARCHAR(150) | NOT NULL, UNIQUE — caller-provided idempotency key |
| `event_config_id` | UUID | optional FK → `aiagent_event_config` |
| `event_definition_id` | UUID | optional FK → `app_event_definition` |
| `agent_id` | UUID | NOT NULL, FK → `aiagent_agent` |
| `prompt_version_id` | UUID | NOT NULL, FK → `aiagent_prompt_version` |
| `model_deployment_id` | UUID | NOT NULL, FK → `aiagent_model_deployment` |
| `trigger_source` | VARCHAR(50) | NOT NULL — `API` \| `SCHEDULER` \| `EVENT` \| `PLAYGROUND` |
| `status` | VARCHAR(50) | NOT NULL — `PENDING` \| `RUNNING` \| `SUCCEEDED` \| `FAILED` \| `CANCELLED` |
| `started_at` | TIMESTAMP | |
| `completed_at` | TIMESTAMP | |
| `latency_ms` | BIGINT | |
| `input_token_count` | INT | CHECK: >= 0 |
| `output_token_count` | INT | CHECK: >= 0 |
| `total_token_count` | INT | CHECK: >= 0 |
| `estimated_cost` | NUMERIC(14,4) | CHECK: >= 0 |
| `provider_request_id` | VARCHAR(255) | provider's own request ID |
| `error_code` | VARCHAR(150) | |
| `error_message` | TEXT | |
| `metadata` | TEXT | free-form JSON |
| `created_at` / `updated_at` | | |

---

### 6.6 Event Registry Tables (`app_event_*`)

#### `app_event_definition` (V8 + V21)

| Column | Type | Notes |
|---|---|---|
| `id` | UUID | PK |
| `code` | VARCHAR(100) | NOT NULL, UNIQUE |
| `name` | VARCHAR(255) | NOT NULL |
| `source_system` | VARCHAR(100) | NOT NULL |
| `event_key` | VARCHAR(150) | NOT NULL — uppercase letters, digits, underscores only |
| `description` | TEXT | |
| `input_schema` | TEXT | JSON Schema string |
| `output_schema` | TEXT | JSON Schema string |
| `event_version` | INT | NOT NULL DEFAULT 1 (V21) |
| `sample_payload_json` | JSONB | (V21) |
| `status` | VARCHAR(50) | NOT NULL — `ACTIVE` \| `INACTIVE` |
| `created_at` / `updated_at` / `created_by` / `updated_by` | | |

Unique: `code`. Unique: `(source_system, event_key)`.

---

#### `app_event_variable` (V21)

Structured, typed variables for an event definition.

| Column | Type | Notes |
|---|---|---|
| `id` | UUID | PK |
| `event_definition_id` | UUID | NOT NULL, FK → `app_event_definition` |
| `variable_path` | VARCHAR(255) | NOT NULL — dot-notation path, e.g. `user.email` |
| `variable_label` | VARCHAR(255) | human-readable label |
| `variable_type` | VARCHAR(50) | NOT NULL — `STRING` \| `NUMBER` \| `BOOLEAN` \| `OBJECT` \| `ARRAY` |
| `required` | BOOLEAN | NOT NULL DEFAULT FALSE |
| `description` | TEXT | |
| `example_value` | TEXT | |
| `created_at` / `updated_at` | | |

Unique: `(event_definition_id, variable_path)`.

---

### 6.7 Notification Tables (`notification_*`)

#### `notification_email_template` (V22)

| Column | Type | Notes |
|---|---|---|
| `id` | UUID | PK |
| `code` | VARCHAR(100) | NOT NULL |
| `name` | VARCHAR(255) | NOT NULL |
| `description` | TEXT | |
| `scope` | VARCHAR(50) | NOT NULL — `SYSTEM` \| `WORKSPACE` |
| `workspace_id` | UUID | null for SYSTEM scope |
| `event_definition_id` | UUID | NOT NULL, FK → `app_event_definition` |
| `status` | VARCHAR(50) | NOT NULL — `ACTIVE` \| `INACTIVE` |
| `created_at` / `updated_at` / `created_by` / `updated_by` | | |

Two partial unique indexes: system-scoped `(code)` WHERE `workspace_id IS NULL`; workspace-scoped `(code, workspace_id)` WHERE `workspace_id IS NOT NULL`.

---

#### `notification_email_template_version` (V22)

| Column | Type | Notes |
|---|---|---|
| `id` | UUID | PK |
| `template_id` | UUID | NOT NULL, FK → `notification_email_template` |
| `version_number` | INT | NOT NULL — auto-incremented per template |
| `subject_template` | VARCHAR(500) | NOT NULL — Mustache/Jinja2 string |
| `html_body_template` | TEXT | NOT NULL |
| `text_body_template` | TEXT | |
| `status` | VARCHAR(50) | `DRAFT` \| `ACTIVE` \| `ARCHIVED` |
| `published_at` | TIMESTAMP | |
| `created_at` / `updated_at` / `created_by` / `updated_by` | | |

Unique: `(template_id, version_number)`. Partial unique index: one ACTIVE version per `template_id`.

---

#### `notification_email_rule` (V22 + V25)

| Column | Type | Notes |
|---|---|---|
| `id` | UUID | PK |
| `code` | VARCHAR(100) | NOT NULL |
| `name` | VARCHAR(255) | (V25) |
| `description` | TEXT | (V25) |
| `scope` | VARCHAR(50) | NOT NULL — `SYSTEM` \| `WORKSPACE` |
| `workspace_id` | UUID | null for SYSTEM scope |
| `event_definition_id` | UUID | NOT NULL, FK → `app_event_definition` |
| `template_id` | UUID | NOT NULL, FK → `notification_email_template` |
| `recipient_strategy` | VARCHAR(50) | NOT NULL — `FIXED_ADDRESS` \| `EVENT_PAYLOAD_FIELD` \| `WORKSPACE_USERS_WITH_RIGHT` |
| `recipient_config_json` | JSONB | strategy-specific config |
| `priority` | INT | NOT NULL DEFAULT 100 |
| `enabled` | BOOLEAN | NOT NULL DEFAULT TRUE — rule is fired when `enabled = TRUE` |
| `status` | VARCHAR(50) | NOT NULL — `ACTIVE` \| `INACTIVE` |
| `created_at` / `updated_at` / `created_by` / `updated_by` | | |

---

#### `notification_email_delivery` (V22)

| Column | Type | Notes |
|---|---|---|
| `id` | UUID | PK |
| `rule_id` | UUID | FK → `notification_email_rule` |
| `template_version_id` | UUID | FK → `notification_email_template_version` |
| `event_definition_id` | UUID | FK → `app_event_definition` |
| `workspace_id` | UUID | |
| `recipient_email` | VARCHAR(255) | NOT NULL |
| `rendered_subject` | VARCHAR(500) | |
| `rendered_html_body` | TEXT | |
| `rendered_text_body` | TEXT | |
| `trigger_payload` | JSONB | original event payload |
| `status` | VARCHAR(50) | NOT NULL — `PENDING` \| `SENT` \| `FAILED` \| `SKIPPED` |
| `created_at` / `updated_at` | | |

---

#### `notification_email_outbox` (V22)

Physical send queue with retry.

| Column | Type | Notes |
|---|---|---|
| `id` | UUID | PK |
| `delivery_id` | UUID | FK → `notification_email_delivery` |
| `recipient_email` | VARCHAR(255) | NOT NULL |
| `subject` | VARCHAR(500) | |
| `html_body` | TEXT | |
| `text_body` | TEXT | |
| `provider_type` | VARCHAR(50) | e.g. `SENDGRID`, `SES`, `SMTP` |
| `provider_message_id` | VARCHAR(255) | returned by provider |
| `status` | VARCHAR(50) | NOT NULL — `PENDING` \| `SENT` \| `FAILED` |
| `retry_count` | INT | NOT NULL DEFAULT 0 |
| `max_retry` | INT | NOT NULL DEFAULT 3 |
| `next_retry_at` | TIMESTAMP | |
| `sent_at` | TIMESTAMP | |
| `error_message` | TEXT | |
| `created_at` / `updated_at` | | |

---

### 6.8 Knowledge Tables (`knowledge_*`)

#### `knowledge_document_type` (V20)

| Column | Type | Notes |
|---|---|---|
| `id` | UUID | PK |
| `code` | VARCHAR(100) | NOT NULL |
| `name` | VARCHAR(255) | NOT NULL |
| `description` | TEXT | |
| `document_scope` | VARCHAR(50) | `SYSTEM` \| `WORKSPACE` |
| `workspace_id` | UUID | null for SYSTEM scope |
| `deleted_at` | TIMESTAMP | soft delete |
| `deleted_by` | VARCHAR(100) | |
| `status` | VARCHAR(50) | NOT NULL |
| `created_at` / `updated_at` / `created_by` / `updated_by` | | |

Two partial unique indexes: system-scoped `(code)` WHERE `workspace_id IS NULL`; workspace-scoped `(code, workspace_id)` WHERE `workspace_id IS NOT NULL`.

---

## 7. Business Rules

### 7.1 IAM Business Rules

**User management:**
- `username` must be 3–100 characters; `email` must be valid format
- A SUSPENDED user cannot log in
- Deactivated users remain visible but cannot perform operations

**Role management:**
- `role_scope` distinguishes SYSTEM roles from WORKSPACE roles
- Workspace roles carry a `workspace_id`; system roles have `workspace_id = NULL`
- `parent_role_id` supports role hierarchy (read-only in this phase)
- Soft-deleted roles retain their grants for audit; `deleted_at` is set

**Access grants:**
- A grant without attached permission actions grants **nothing**
- System-scoped permissions can only be granted on the `GLOBAL` IAM resource
- Workspace-scoped permissions can only be granted on a concrete `WORKSPACE` IAM resource and must carry `workspaceId`
- Organization-scoped permissions can only be granted on a concrete `ORGANIZATION` IAM resource
- Permission actions can only be attached when `subjectType` is listed in the permission's `assignableSubjectTypes`
- `DENY` effect overrides `ALLOW` grants from all other sources
- `OWNER` kind grants are created automatically when an entity owner is established

**Authorization check:**
- Authorization evaluates permission actions first; legacy right codes are resolved via the `right_id` bridge in `iam_permission_action`
- `SCOPE_WIDE` data access policy does not mean public — it means the subject can access all resources within their already-granted scope

### 7.2 Workspace Business Rules

**Organization:**
- Organization code must be globally unique
- Only `ACTIVE` organizations can have new workspaces created under them
- Archive is terminal — no reactivation path

**Workspace:**
- Workspace code must be unique within the organization
- `joinPolicy` controls how new users can access the workspace:
  - `INVITE_ONLY` — only via invitation code; join requests are blocked at application layer
  - `REQUEST_TO_JOIN` — only via join request; invitations still work
  - `INVITE_OR_REQUEST` — both paths allowed
  - `DISABLED` — no new member entry allowed

**Invitations:**
- Raw invitation code is generated once and returned only at create time — **it is never stored in plaintext** (stored as SHA-256 hash)
- Only `invitation_code_hint` (first 4 chars) is shown in list responses
- `accept()` increments `used_count`; invitation transitions to `ACCEPTED` only when `used_count >= maxUses` (null maxUses = unlimited)
- Accepting an expired invitation throws `WORKSPACE_INVITATION_EXPIRED`
- Accepting a revoked invitation throws `WORKSPACE_INVITATION_REVOKED`
- Accepting when already a member throws `WORKSPACE_ALREADY_MEMBER`

**Join requests:**
- Only one `PENDING` request allowed per user per workspace (partial unique index)
- `REQUEST_TO_JOIN` or `INVITE_OR_REQUEST` join policy required; `INVITE_ONLY` or `DISABLED` blocks creation
- Approve transitions `PENDING → APPROVED` and automatically adds the user as a workspace member
- Reject transitions `PENDING → REJECTED`; `reviewNote` is stored
- Cancel transitions `PENDING → CANCELLED`; user can only cancel their own request
- All state transitions from non-PENDING status throw `WORKSPACE_JOIN_REQUEST_NOT_PENDING`

**Onboarding state machine:**

```
create(userId)           → IN_PROGRESS / CHOOSE_WORKSPACE_OPTION
chooseOption(CREATE)     → IN_PROGRESS / CREATE_WORKSPACE
chooseOption(JOIN)       → IN_PROGRESS / ENTER_INVITATION_CODE
chooseOption(REQUEST)    → throws WORKSPACE_ONBOARDING_OPTION_NOT_SUPPORTED (422)
onWorkspaceCreated(...)  → COMPLETED
onInvitationAccepted(…)  → COMPLETED
resetChoice()            → IN_PROGRESS / CHOOSE_WORKSPACE_OPTION
                           (allowed from IN_PROGRESS[resettable], CANCELLED, FAILED, WAITING_FOR_APPROVAL)
```

`resetChoice()` also cancels any legacy pending join request via `CancelJoinRequestAction`.

**Teams (org-scoped as of V33+):**
- Teams are owned by an Organization, not a Workspace
- Team code must be unique within the organization
- A team can be assigned to workspaces via `org_team_workspace_assignment`
- A team cannot be assigned to a workspace in a different organization (enforced by application logic)
- Cannot remove the only OWNER from an org_member

### 7.3 AI Agent Business Rules

**Provider lifecycle:**
- Status transitions: `INACTIVE → ACTIVE`, `ACTIVE → INACTIVE`, `ACTIVE → DEPRECATED`
- A DEPRECATED provider cannot be reactivated (throws 422)
- Provider code must be globally unique and validated in the domain value object

**Model lifecycle:**
- Same status machine as provider
- Model code must be unique within a provider
- Cannot activate a model whose provider is DEPRECATED or INACTIVE

**Model Deployment lifecycle:**
- Status: `ACTIVE`, `INACTIVE`, `DEPRECATED`
- One deployment can be the default per `(model_id, environment)` — enforced by partial unique index
- `set-default` clears the current default for that `(model, environment)` and sets the new one atomically
- Cannot activate a deployment whose model is DEPRECATED or INACTIVE

**Agent lifecycle:**
- Status: `ACTIVE`, `INACTIVE`, `DEPRECATED`
- `activate()` on a DEPRECATED agent throws `IllegalStateException` (domain-level guard) — application service checks and throws 422 with human-readable message before calling domain

**Prompt versioning:**
- Only one ACTIVE version per template at any time (partial unique index)
- Activating a new version archives the current ACTIVE version automatically
- Only DRAFT versions can be updated
- Archived versions cannot be re-activated

**Event Config:**
- Only one ACTIVE config per `(event_definition_id, environment)` at any time
- Activation validates: agent is ACTIVE, prompt version is ACTIVE, model deployment is ACTIVE, event definition is ACTIVE
- Deactivating an event config does not affect any linked entities

**Usage Policy:**
- `target_type = GLOBAL` must have `target_id = NULL`
- At least one limit (requests, tokens, cost, concurrent, daily budget) must be set
- `period` is required when any rate-based limit is set
- Action determines what happens when limit is hit: `BLOCK` (reject), `WARN` (log), `THROTTLE` (delay)

**Execution lifecycle:**
- `request_id` is the caller's idempotency key — unique across all executions
- Status machine: `PENDING → RUNNING → SUCCEEDED | FAILED | CANCELLED`
- `trigger_source` options: `API`, `SCHEDULER`, `EVENT`, `PLAYGROUND`
- Playground execution does NOT create an execution log record — it is direct and ephemeral
- Playground is gated by `scopery.playground.enabled` flag in config

**Provider Secrets:**
- API keys are stored AES-256 encrypted with IV; never stored in plaintext
- Only masked value is exposed in API responses
- Storing a new secret deactivates the previous ACTIVE secret for the same `(provider_id, secret_type)`
- Rotation creates a new encrypted value on the same record and updates `last_rotated_at`

### 7.4 Notification Business Rules

- A rule fires only when `status = ACTIVE` AND `enabled = TRUE`
- Template `scope` and rule `scope` must match; a workspace rule can only use a workspace or system template
- Publishing a version archives the previous ACTIVE version
- Outbox records retry up to `max_retry` times; `next_retry_at` is set with exponential backoff
- `recipient_config_json` semantics depend on `recipient_strategy`:
  - `FIXED_ADDRESS`: `{ "email": "user@example.com" }`
  - `EVENT_PAYLOAD_FIELD`: `{ "field": "user.email" }`
  - `WORKSPACE_USERS_WITH_RIGHT`: `{ "rightCode": "VIEW_IAM_USER" }`

### 7.5 Event Registry Business Rules

- `eventKey` must match pattern `[A-Z][A-Z0-9_]*` (uppercase letters, digits, underscores only)
- The combination `(source_system, event_key)` must be globally unique
- Event definition `code` must be globally unique
- `PUT /{id}/variables` performs a full replace — all existing variables are deleted and re-created atomically
- An `INACTIVE` event definition cannot be used in new event configs or notification rules

---

## 8. API Reference — IAM

### Public Endpoints (no auth)

| Method | Path | Description |
|---|---|---|
| GET | `/api/health` | Health check |
| POST | `/api/iam/auth/login` | Login |
| POST | `/api/iam/auth/refresh` | Refresh token |
| POST | `/api/iam/auth/logout` | Logout |
| POST | `/api/iam/users` | Register user |

---

### IAM — Auth

**Base:** `/api/iam/auth`

| Method | Path | Request Body | Response |
|---|---|---|---|
| POST | `/login` | `{ username*, password* }` | Sets `access_token` + `refresh_token` cookies |
| POST | `/refresh` | _(refresh_token cookie)_ | Rotates both tokens |
| POST | `/logout` | _(refresh_token cookie)_ | Clears cookies, revokes refresh token |

---

### IAM — Users

**Base:** `/api/iam/users`

| Method | Path | Input | Description |
|---|---|---|---|
| POST | `/` | `{ username*(3-100), email*, fullName(max 255) }` | Create user — 201 |
| GET | `/{id}` | — | Get user by ID |
| GET | `/` | `?keyword&status&page=0&size=20` | Search users |
| PUT | `/{id}` | `{ fullName }` | Update full name |
| PATCH | `/{id}/activate` | — | Activate user |
| PATCH | `/{id}/deactivate` | — | Deactivate user |
| PATCH | `/{id}/suspend` | — | Suspend user |

**User response fields:** `id`, `username`, `email`, `fullName`, `status`, `createdAt`, `updatedAt`

---

### IAM — Roles

**Base:** `/api/iam/roles`

| Method | Path | Input | Description |
|---|---|---|---|
| POST | `/system` | `{ code*(2-100), name*, description, roleScope, roleSource, parentRoleId }` | Create system role — 201 |
| POST | `/workspace` | `{ code*(2-100), name*, description, roleScope, roleSource, workspaceId, parentRoleId }` | Create workspace role — 201 |
| GET | `/{id}` | — | Get role |
| GET | `/` | `?keyword&workspaceId&roleScope&roleSource&status&includeDeleted=false&page=0&size=20` | Search roles |
| PUT | `/{id}` | `{ name*, description }` | Update role |
| PATCH | `/{id}/activate` | — | Activate |
| PATCH | `/{id}/deactivate` | — | Deactivate |
| PATCH | `/{id}/soft-delete` | — | Soft delete (sets `deletedAt`) |

---

### IAM — Rights (Legacy)

**Base:** `/api/iam/rights`

> Legacy compatibility catalog. New code should model authority via `/api/iam/permissions`. Rights remain for debug and migration use.

| Method | Path | Input | Description |
|---|---|---|---|
| GET | `/{id}` | — | Get right |
| GET | `/` | `?keyword&module&status&page=0&size=50` | Search rights |

---

### IAM — Permissions

**Base:** `/api/iam/permissions`

| Method | Path | Input | Description |
|---|---|---|---|
| GET | `/matrix` | — | List all ACTIVE permissions with nested actions — used by FE permission matrix |
| GET | `/` | `?keyword&moduleCode&resourceScopeLevel&dataAccessPolicy&permissionCategory&riskLevel&assignableSubjectType&status&page=0&size=20` | Search permissions with nested actions |
| GET | `/{id}` | — | Get permission with nested actions |
| GET | `/{id}/actions` | — | List actions under one permission |

**Permission response:**
```json
{
  "id": "uuid",
  "code": "WORKSPACE_ACCESS_MANAGEMENT",
  "name": "Workspace Access Management",
  "moduleCode": "WORKSPACE",
  "resourceScopeLevel": "WORKSPACE",
  "dataAccessPolicy": "SCOPE_WIDE",
  "permissionCategory": "ACCESS_CONTROL",
  "assignableSubjectTypes": ["ROLE", "TEAM", "USER"],
  "riskLevel": "HIGH",
  "status": "ACTIVE",
  "actions": [
    { "id": "uuid", "actionCode": "INVITE_MEMBER", "name": "Invite Member", "legacyRightCode": "WORKSPACE_INVITE_MEMBER" }
  ]
}
```

---

### IAM — Access Grants

**Base:** `/api/iam/grants`

| Method | Path | Input | Description |
|---|---|---|---|
| POST | `/` | `{ subjectType*, subjectId*, resourceId*, roleId, effect, scopeType, scopeRefId, workspaceId, grantedBy }` | Create grant |
| GET | `/{id}` | — | Get grant |
| GET | `/` | `?subjectId&resourceId&workspaceId&status&page=0&size=20` | Search grants |
| PATCH | `/{id}/revoke` | — | Revoke grant |
| GET | `/{id}/rights` | — | List legacy rights on grant |
| POST | `/{id}/rights` | `{ rightId* }` | Add legacy right (also dual-writes mapped permission action) |
| DELETE | `/{id}/rights/{rightId}` | — | Remove legacy right |
| GET | `/{id}/actions` | — | List permission actions attached to grant |
| POST | `/{id}/actions` | `{ permissionActionId }` or `{ permissionCode, actionCode }` | Add permission action |
| DELETE | `/{id}/actions/{permissionActionId}` | — | Remove permission action |

**FE permission assignment flow:**
1. `GET /api/iam/permissions/matrix` — load permission tree
2. Resolve IAM resource: `GET /api/iam/resources/by-ref?resourceType=WORKSPACE&refId={workspaceId}`
3. Find or create grant: `GET /api/iam/grants?subjectId=...&resourceId=...`
4. Load current actions: `GET /api/iam/grants/{grantId}/actions`
5. Save deltas: `POST` / `DELETE /api/iam/grants/{grantId}/actions/...`

---

### IAM — Role Assignments

**Base:** `/api/iam/role-assignments`

| Method | Path | Input | Description |
|---|---|---|---|
| POST | `/` | `{ assigneeType*, assigneeId*, roleId*, workspaceId, assignedBy }` | Assign role |
| GET | `/{id}` | — | Get assignment |
| GET | `/` | `?roleId&assigneeId&assigneeType&status&workspaceId&page=0&size=20` | Search |
| PATCH | `/{id}/activate` | — | Activate |
| PATCH | `/{id}/deactivate` | — | Deactivate |

---

### IAM — Auth Resources

**Base:** `/api/iam/resources`

| Method | Path | Input | Description |
|---|---|---|---|
| POST | `/` | `{ code*(2-100), resourceType*, name*(max 255), description }` | Register resource (GLOBAL type only via API) |
| GET | `/{id}` | — | Get resource |
| GET | `/` | `?keyword&resourceType&status&page=0&size=20` | Search |
| GET | `/by-ref` | `?resourceType&refId` | Get IAM resource by business entity ref ID |
| GET | `/by-code` | `?resourceType&code` | Get IAM resource by code and type |
| PUT | `/{id}` | `{ name*(max 255), description }` | Update |
| PATCH | `/{id}/activate` | — | Activate |
| PATCH | `/{id}/deactivate` | — | Deactivate |

> `ORGANIZATION`, `WORKSPACE`, and `TEAM` resources are auto-created by their owning modules and always carry a `refId`.

---

### IAM — Authorization Check

**Base:** `/api/iam/authorization`

| Method | Path | Input | Description |
|---|---|---|---|
| POST | `/check` | `{ userId*, resourceId*, rightCode* }` | Debug: check if user has legacy right on resource |

**Response:**
```json
{
  "userId": "uuid",
  "resourceId": "uuid",
  "rightCode": "UPDATE_WORKSPACE",
  "allowed": true,
  "reason": "USER_GRANT_ALLOW"
}
```

`reason` values: `USER_GRANT_ALLOW`, `USER_GRANT_DENY`, `ROLE_GRANT_ALLOW`, `NO_MATCHING_GRANT`, etc.

---

## 9. API Reference — Workspace

### Organizations

**Base:** `/api/organizations`

| Method | Path | Input | Description |
|---|---|---|---|
| POST | `/` | `{ name*, code*, description }` | Create organization — 201 |
| GET | `/{id}` | — | Get organization |
| GET | `/` | `?keyword&ownerUserId&status&page=0&size=20` | Search |
| PUT | `/{id}` | `{ name*, description }` | Update |
| PATCH | `/{id}/activate` | — | Activate |
| PATCH | `/{id}/archive` | — | Archive (terminal) |

**Organization response fields:** `id`, `name`, `code`, `description`, `ownerUserId`, `status`, `createdAt`, `updatedAt`

---

### Workspaces

**Base:** `/api/workspaces`

| Method | Path | Input | Description |
|---|---|---|---|
| POST | `/` | `{ organizationId*(UUID), name*, code*, description, defaultVisibility, joinPolicy }` | Create workspace — 201 |
| GET | `/{id}` | — | Get workspace detail |
| GET | `/` | `?organizationId&ownerUserId&keyword&status&page=0&size=20` | Search |
| PUT | `/{id}` | `{ name*, description, defaultVisibility, joinPolicy }` | Update |
| PATCH | `/{id}/activate` | — | Activate |
| PATCH | `/{id}/archive` | — | Archive |

**`defaultVisibility`:** `PRIVATE` | `PUBLIC`  
**`joinPolicy`:** `INVITE_ONLY` | `REQUEST_TO_JOIN` | `INVITE_OR_REQUEST` | `DISABLED`

---

### Members

**Base:** `/api/workspaces/{workspaceId}/members`

| Method | Path | Input | Description |
|---|---|---|---|
| POST | `/` | `{ userId*(UUID) }` | Add member — 201 |
| GET | `/` | `?userId&status&page=0&size=20` | List members |
| PATCH | `/{memberId}/activate` | — | Activate member |
| PATCH | `/{memberId}/deactivate` | — | Deactivate member |

---

### Teams

**Base:** `/api/workspaces/{workspaceId}/teams`

> Note: as of V33, teams are org-scoped. These endpoints manage the workspace-level team view.

| Method | Path | Input | Description |
|---|---|---|---|
| POST | `/` | `{ name*, code*, description }` | Create team — 201 |
| GET | `/{teamId}` | — | Get team |
| GET | `/` | `?status&page=0&size=20` | Search teams |
| PUT | `/{teamId}` | `{ name*, description }` | Update team |
| PATCH | `/{teamId}/activate` | — | Activate |
| PATCH | `/{teamId}/archive` | — | Archive |
| POST | `/{teamId}/members` | `{ userId*(UUID) }` | Add team member |
| GET | `/{teamId}/members` | `?page=0&size=20` | List team members |
| DELETE | `/{teamId}/members/{userId}` | — | Remove team member |

---

### Invitations

**Base:** `/api/workspaces`

| Method | Path | Input | Description |
|---|---|---|---|
| POST | `/{workspaceId}/invitations` | `{ invitedEmail, maxUses(Integer), expiresAt(Instant), sendEmail(boolean) }` | Create invitation — 201. `invitationCode` (raw) returned **once only** |
| GET | `/{workspaceId}/invitations` | — | List invitations — `invitationCode` = null, only `invitationCodeHint` shown |
| PATCH | `/{workspaceId}/invitations/{id}/revoke` | — | Revoke invitation |
| POST | `/invitations/{code}/accept` | — | Accept invitation by raw code |

**Invitation response fields:** `id`, `workspaceId`, `invitedEmail`, `invitationCode` (null on list), `invitationCodeHint`, `maxUses`, `usedCount`, `expiresAt`, `status`, `createdAt`

---

### Join Requests

| Method | Path | Input | Description |
|---|---|---|---|
| POST | `/api/workspaces/{workspaceId}/join-requests` | `{ message }` | Submit join request (workspace ID in path) |
| POST | `/api/workspace-join-requests` | `{ workspaceId, workspaceCode, message }` | Submit join request (ID or code in body) |
| GET | `/api/workspaces/{workspaceId}/join-requests` | `?status` | List join requests (admin) |
| PATCH | `/api/workspaces/{workspaceId}/join-requests/{id}/approve` | — | Approve — adds user as member |
| PATCH | `/api/workspaces/{workspaceId}/join-requests/{id}/reject` | `{ reviewNote }` | Reject |
| DELETE | `/api/workspaces/{workspaceId}/join-requests/{id}` | — | Cancel own pending request |

**Statuses:** `PENDING` | `APPROVED` | `REJECTED` | `CANCELLED`

---

### Workspace Context (Switcher)

**Base:** `/api/workspace-context`

| Method | Path | Input | Description |
|---|---|---|---|
| GET | `/current` | — | Get current active workspace for logged-in user |
| GET | `/available` | — | List all workspaces user is an active member of |
| PUT | `/current` | `{ workspaceId*(UUID) }` | Switch current workspace |

---

### Onboarding

**Base:** `/api/workspace-onboarding`

New users must complete onboarding before accessing workspace features. Two paths only: create workspace or join via invitation.

**State machine:**
```
START
  └─► CHOOSE_WORKSPACE_OPTION
        ├─► CREATE_WORKSPACE      → auto-creates Org + Workspace → COMPLETED
        └─► ENTER_INVITATION_CODE → accept invitation            → COMPLETED
```

| Method | Path | Input | Description |
|---|---|---|---|
| GET | `/status` | — | Get current onboarding state |
| POST | `/start` | — | Initialize onboarding state — 201 |
| POST | `/choose-option` | `{ option* }` | Choose a path (`CREATE_WORKSPACE` or `JOIN_WITH_INVITATION`) |
| POST | `/create-workspace` | `{ organizationName*, organizationCode*, workspaceName*, workspaceCode*, workspaceDescription }` | Auto-create org + workspace → COMPLETED |
| POST | `/accept-invitation` | `{ code* }` | Accept invitation code → COMPLETED |
| POST | `/reset-choice` | — | Reset to option selection; cancels legacy pending join request if any |

> `REQUEST_TO_JOIN` option is no longer supported during onboarding — returns 422.

**Create workspace example request:**
```json
{
  "organizationName": "Acme Corporation",
  "organizationCode": "ACME",
  "workspaceName": "Product Team",
  "workspaceCode": "PRODUCT_TEAM",
  "workspaceDescription": "Main product engineering workspace"
}
```

**Onboarding status response fields:** `id`, `userId`, `status`, `currentStep`, `selectedOption`, `targetWorkspaceId`, `createdOrganizationId`, `createdWorkspaceId`, `invitationId`, `joinRequestId`, `failureReason`, `createdAt`, `updatedAt`

---

## 10. API Reference — Event Registry

### Event Definitions

**Base:** `/api/event-definitions`

| Method | Path | Input | Description |
|---|---|---|---|
| POST | `/` | `{ code*(max 100), name*(max 255), sourceSystem*(max 100), eventKey*(max 150), description, inputSchema, outputSchema }` | Create event definition — 201 |
| GET | `/{id}` | — | Get detail (includes variables) |
| GET | `/` | `?keyword&sourceSystem&eventKey&status&page=0&size=20` | Search |
| PUT | `/{id}` | `{ name*(max 255), description, inputSchema, outputSchema }` | Update |
| PATCH | `/{id}/activate` | — | Activate |
| PATCH | `/{id}/deactivate` | — | Deactivate |
| PUT | `/{id}/variables` | `[ { variablePath*, variableLabel, variableType*, required, description, exampleValue } ]` | Upsert variables (full replace — all existing variables deleted and re-created) |
| GET | `/{id}/variables` | — | List variables |

> `eventKey` must match `[A-Z][A-Z0-9_]*` — e.g. `USER_SIGNED_UP`, `INVOICE_PAID`.

**Event definition response fields:** `id`, `code`, `name`, `sourceSystem`, `eventKey`, `description`, `inputSchema`, `outputSchema`, `eventVersion`, `status`, `variables[]`, `createdAt`, `updatedAt`

**Variable fields:** `id`, `variablePath`, `variableLabel`, `variableType`, `required`, `description`, `exampleValue`

---

## 11. API Reference — Notification

### Email Templates

**Base:** `/api/notification/email-templates`

| Method | Path | Input | Description |
|---|---|---|---|
| POST | `/` | `{ code*, name*, description, scope*, workspaceId, eventDefinitionId* }` | Create template — 201 |
| GET | `/{id}` | — | Get template |
| GET | `/` | `?keyword&scope&status&workspaceId&eventDefinitionId&page=0&size=20` | Search |
| PUT | `/{id}` | `{ name*, description }` | Update |
| PATCH | `/{id}/activate` | — | Activate |
| PATCH | `/{id}/deactivate` | — | Deactivate |
| DELETE | `/{id}` | — | Delete |
| POST | `/{id}/versions` | `{ subjectTemplate*, htmlBodyTemplate*, textBodyTemplate }` | Create draft version — 201 |
| GET | `/{id}/versions` | — | List versions |
| PATCH | `/{id}/versions/{versionId}/publish` | — | Publish version (archives current ACTIVE, activates this one) |

---

### Email Template Preview

**Base:** `/api/notification/email-templates`

| Method | Path | Input | Description |
|---|---|---|---|
| POST | `/preview` | `{ versionId*(UUID), samplePayload: { key: value } }` | Render template with sample data |

**Preview response:** `{ subject, htmlBody, textBody }`

---

### Email Rules

**Base:** `/api/notification/email-rules`

| Method | Path | Input | Description |
|---|---|---|---|
| POST | `/` | `{ code*, name*, description, scope*, workspaceId, eventDefinitionId*, templateId*, recipientStrategy*, recipientConfigJson, priority }` | Create rule — 201 |
| GET | `/{id}` | — | Get rule |
| GET | `/` | `?keyword&scope&status&workspaceId&eventDefinitionId&templateId&page=0&size=20` | Search |
| PUT | `/{id}` | `{ name*, description, recipientStrategy*, recipientConfigJson, priority }` | Update |
| PATCH | `/{id}/activate` | — | Activate (makes rule eligible to fire) |
| PATCH | `/{id}/deactivate` | — | Deactivate |
| PATCH | `/{id}/enable` | — | Enable (rule fires on trigger events) |
| PATCH | `/{id}/disable` | — | Disable (rule is skipped on trigger) |
| DELETE | `/{id}` | — | Delete |

**`recipientStrategy`:** `FIXED_ADDRESS` | `EVENT_PAYLOAD_FIELD` | `WORKSPACE_USERS_WITH_RIGHT`

---

### Email Deliveries

**Base:** `/api/notification/email-deliveries`

| Method | Path | Input | Description |
|---|---|---|---|
| GET | `/{id}` | — | Get delivery record |
| GET | `/` | `?ruleId&templateId&eventDefinitionId&workspaceId&status&page=0&size=20` | Search deliveries |

---

### Email Outbox

**Base:** `/api/notification/email-outbox`

| Method | Path | Input | Description |
|---|---|---|---|
| GET | `/{id}` | — | Get outbox record |
| GET | `/` | `?deliveryId&status&providerType&page=0&size=20` | Search outbox |
| POST | `/{id}/retry` | — | Manually retry a failed record |

---

## 12. API Reference — AI Agent

### Providers

**Base:** `/api/ai-agent/providers`

| Method | Path | Input | Description |
|---|---|---|---|
| POST | `/` | `{ name*, code*, type*, apiBaseUrl, description }` | Create provider — 201 |
| GET | `/{id}` | — | Get provider detail |
| GET | `/` | `?keyword&type&status&page=0&size=20` | Search |
| PUT | `/{id}` | `{ name*, type*, apiBaseUrl, description }` | Update |
| PATCH | `/{id}/activate` | — | Activate (`INACTIVE → ACTIVE`) |
| PATCH | `/{id}/deactivate` | — | Deactivate (`ACTIVE → INACTIVE`) |

**Provider `type`:** `OPENAI` | `ANTHROPIC` | `AZURE_OPENAI` | `GOOGLE` | `COHERE` | `MISTRAL` | _(open, stored as string)_

---

### Provider Secrets

**Base:** `/api/ai-agent/provider-secrets`

| Method | Path | Input | Description |
|---|---|---|---|
| POST | `/` | `{ providerId*, secretType*(max 50), secretValue*(max 5000), description }` | Store encrypted API key — deactivates previous active secret for same `(provider, type)` |
| GET | `/{id}` | — | Get secret (only masked value, never raw) |
| GET | `/` | `?providerId&secretType&status&page=0&size=20` | Search |
| PUT | `/{id}/rotate` | `{ secretValue*(max 5000), description }` | Rotate secret — re-encrypts, updates `last_rotated_at` |
| PATCH | `/{id}/deactivate` | — | Deactivate |

---

### AI Models

**Base:** `/api/ai-agent/models`

| Method | Path | Input | Description |
|---|---|---|---|
| POST | `/` | `{ providerId*, name*, code*, providerModelId*, type*, description }` | Create model — 201 |
| GET | `/{id}` | — | Get model detail |
| GET | `/` | `?providerId&keyword&status&type&page=0&size=20` | Search |
| PUT | `/{id}` | `{ name*, providerModelId*, type*, description }` | Update |
| PATCH | `/{id}/activate` | — | Activate |
| PATCH | `/{id}/deactivate` | — | Deactivate |

**`type`:** `TEXT_GENERATION` | `EMBEDDING` | `IMAGE_GENERATION`

---

### Model Deployments

**Base:** `/api/ai-agent/model-deployments`

| Method | Path | Input | Description |
|---|---|---|---|
| POST | `/` | `{ modelId*, name*, code*, environment*, providerDeploymentId*, endpointUrl, defaultTemperature(0-2), defaultMaxOutputTokens(>0), isDefault, description }` | Create deployment — 201 |
| GET | `/{id}` | — | Get deployment detail |
| GET | `/` | `?modelId&environment&keyword&status&isDefault&page=0&size=20` | Search |
| PUT | `/{id}` | `{ name*, providerDeploymentId*, endpointUrl, defaultTemperature, defaultMaxOutputTokens, isDefault, description }` | Update |
| PATCH | `/{id}/activate` | — | Activate |
| PATCH | `/{id}/deactivate` | — | Deactivate |
| PATCH | `/{id}/set-default` | — | Set as default for `(model, environment)` — clears previous default atomically |

**`environment`:** `DEV` | `UAT` | `PROD`

---

### Model Parameter Capabilities

**Base:** `/api/ai-agent/model-parameter-capabilities`

| Method | Path | Input | Description |
|---|---|---|---|
| POST | `/` | `{ modelId*, parameterName*, apiParameterKey, supportStatus*, valueType*, minValue, maxValue, defaultValue, nullable*, ifNullBehavior, description }` | Create capability — 201 |
| GET | `/{id}` | — | Get capability detail |
| GET | `/` | `?modelId&parameterName&supportStatus&valueType&status&page=0&size=20` | Search |
| PUT | `/{id}` | `{ apiParameterKey, supportStatus*, valueType*, minValue, maxValue, defaultValue, nullable*, ifNullBehavior, description }` | Update |
| PATCH | `/{id}/activate` | — | Activate |
| PATCH | `/{id}/deactivate` | — | Deactivate |

**`supportStatus`:** `SUPPORTED` | `UNSUPPORTED` | `UNKNOWN`  
**`valueType`:** `INTEGER` | `FLOAT` | `BOOLEAN` | `STRING`  
**`ifNullBehavior`:** `DO_NOT_SEND_PARAMETER` | `USE_PROVIDER_DEFAULT`

---

### Agents

**Base:** `/api/ai-agent/agents`

| Method | Path | Input | Description |
|---|---|---|---|
| POST | `/` | `{ name*(max 255), code*(max 100), type*, description, defaultModelDeploymentId, outputFormat }` | Create agent — 201 |
| GET | `/{id}` | — | Get agent detail |
| GET | `/` | `?keyword&type&status&outputFormat&page=0&size=20` | Search |
| PUT | `/{id}` | `{ name*(max 255), type*, description, defaultModelDeploymentId, outputFormat }` | Update |
| PATCH | `/{id}/activate` | — | Activate (blocked if DEPRECATED — 422) |
| PATCH | `/{id}/deactivate` | — | Deactivate |

**`outputFormat`:** `TEXT` | `JSON` | `MARKDOWN`

---

### Prompt Templates

**Base:** `/api/ai-agent/prompt-templates`

| Method | Path | Input | Description |
|---|---|---|---|
| POST | `/` | `{ agentId*, name*(max 255), code*(max 100), description }` | Create template — 201 |
| GET | `/{id}` | — | Get template detail |
| GET | `/` | `?agentId&keyword&status&page=0&size=20` | Search |
| PUT | `/{id}` | `{ name*(max 255), description }` | Update |
| PATCH | `/{id}/activate` | — | Activate |
| PATCH | `/{id}/deactivate` | — | Deactivate |

---

### Prompt Versions

**Base:** `/api/ai-agent/prompt-versions`

| Method | Path | Input | Description |
|---|---|---|---|
| POST | `/` | `{ templateId*, title, content*, contentFormat*, variableSchema, changeNote }` | Create draft version — 201 |
| GET | `/{id}` | — | Get version detail |
| GET | `/` | `?templateId&status&contentFormat&page=0&size=20` | Search |
| PUT | `/{id}` | `{ title, content*, contentFormat*, variableSchema, changeNote }` | Update (DRAFT only) |
| PATCH | `/{id}/activate` | — | Publish — archives current ACTIVE version; this one becomes ACTIVE |
| PATCH | `/{id}/archive` | — | Archive version |

**`contentFormat`:** `PLAIN_TEXT` | `MARKDOWN` | `JINJA2`  
**`status`:** `DRAFT` | `ACTIVE` | `ARCHIVED`

---

### Event Configurations

**Base:** `/api/ai-agent/event-configs`

| Method | Path | Input | Description |
|---|---|---|---|
| POST | `/` | `{ code*(max 100), name*(max 255), eventDefinitionId*, environment, triggerType*, agentId*, promptVersionId*, modelDeploymentId*, conditionExpression, description }` | Create event config — 201 |
| GET | `/{id}` | — | Get config detail |
| GET | `/resolve` | `?eventDefinitionId&sourceSystem&eventKey&environment` | Resolve the ACTIVE config for a given event+environment |
| GET | `/` | `?keyword&eventDefinitionId&environment&triggerType&status&agentId&page=0&size=20` | Search |
| PUT | `/{id}` | `{ name*, triggerType*, agentId*, promptVersionId*, modelDeploymentId*, conditionExpression, description }` | Update |
| PATCH | `/{id}/activate` | — | Activate (validates all linked entities are ACTIVE) |
| PATCH | `/{id}/deactivate` | — | Deactivate |

**`triggerType`:** `AUTOMATIC` | `MANUAL`  
**`environment`:** `DEV` | `UAT` | `PROD`

---

### Usage Policies

**Base:** `/api/ai-agent/usage-policies`

| Method | Path | Input | Description |
|---|---|---|---|
| POST | `/` | `{ code*(max 100), name*(max 255), targetType*, targetId, maxRequestsPerPeriod, maxTokensPerPeriod, maxCostPerPeriod, maxConcurrentRequests, dailyBudget, period, action*, priority, description }` | Create policy — 201 |
| GET | `/{id}` | — | Get policy detail |
| GET | `/` | `?keyword&targetType&status&page=0&size=20` | Search |
| PUT | `/{id}` | `{ name*, maxRequestsPerPeriod, maxTokensPerPeriod, maxCostPerPeriod, maxConcurrentRequests, dailyBudget, period, action*, priority, description }` | Update |
| PATCH | `/{id}/activate` | — | Activate |
| PATCH | `/{id}/deactivate` | — | Deactivate |

**`targetType`:** `GLOBAL` | `EVENT_CONFIG` | `AGENT` | `MODEL_DEPLOYMENT`  
**`period`:** `HOURLY` | `DAILY` | `WEEKLY` | `MONTHLY`  
**`action`:** `BLOCK` | `WARN` | `THROTTLE`

---

### Executions (Trigger)

**Base:** `/api/ai-agent/executions`

| Method | Path | Input | Description |
|---|---|---|---|
| POST | `/event` | `{ requestId*, eventDefinitionId, eventCode, sourceSystem, eventKey, environment, triggerSource, inputVariables: {}, metadata }` | Trigger execution by event — resolves ACTIVE event config |
| POST | `/event-config/{eventConfigId}` | `{ requestId*, triggerSource, inputVariables: {}, metadata }` | Trigger execution by EventConfig ID directly |

> Provide at least one of: `eventDefinitionId`, `eventCode`, or `sourceSystem`+`eventKey` to resolve the event config.

---

### Execution Logs

**Base:** `/api/ai-agent/execution-logs`

| Method | Path | Input | Description |
|---|---|---|---|
| POST | `/` | `{ requestId*(max 150), eventConfigId, eventDefinitionId, agentId*, promptVersionId*, modelDeploymentId*, triggerSource*, metadata }` | Create execution log — 201 |
| GET | `/{id}` | — | Get log detail |
| GET | `/` | `?requestId&eventConfigId&eventDefinitionId&agentId&promptVersionId&modelDeploymentId&triggerSource&status&createdFrom&createdTo&page=0&size=20` | Search |
| PATCH | `/{id}/running` | — | Mark as running |
| PATCH | `/{id}/succeeded` | `{ inputTokenCount, outputTokenCount, totalTokenCount, estimatedCost, providerRequestId, metadata }` | Mark as succeeded |
| PATCH | `/{id}/failed` | `{ errorCode, errorMessage, inputTokenCount, outputTokenCount, totalTokenCount, estimatedCost, providerRequestId, metadata }` | Mark as failed |
| PATCH | `/{id}/cancel` | — | Cancel execution |

**`status`:** `PENDING` | `RUNNING` | `SUCCEEDED` | `FAILED` | `CANCELLED`  
**`triggerSource`:** `API` | `SCHEDULER` | `EVENT` | `PLAYGROUND`

---

### Playground

**Base:** `/api/ai-agent/playground`

> Playground is gated by `scopery.playground.enabled` property. Disabled by default in non-dev environments.

| Method | Path | Input | Description |
|---|---|---|---|
| POST | `/event-config/{eventConfigId}/run` | `{ requestId, inputVariables: {} }` | Run via EventConfig — validates and executes against AI provider |
| POST | `/direct/run` | `{ requestId, agentId*, promptVersionId*, modelDeploymentId*, inputVariables: {} }` | Run directly without EventConfig |
| POST | `/prompt/preview` | `{ promptVersionId*, inputVariables: {} }` | Preview rendered prompt only — no AI call |
| GET | `/options` | `?includeEventConfigs=true&includeAgents=true&includePromptVersions=true&includeModelDeployments=true` | Get dropdown options for playground UI |

---

## 13. API Reference — Knowledge

### Document Types

**Base:** `/api/knowledge/document-types`

| Method | Path | Input | Description |
|---|---|---|---|
| POST | `/system` | `{ code*(2-100), name*(max 255), description }` | Create system-scoped document type — 201 |
| POST | `/workspace` | `{ code*(2-100), name*(max 255), description, workspaceId }` | Create workspace-scoped document type — 201 |
| GET | `/{id}` | — | Get document type |
| GET | `/` | `?keyword&workspaceId&documentScope&status&includeDeleted=false&page=0&size=20` | Search |
| PUT | `/{id}` | `{ name*(max 255), description }` | Update |
| PATCH | `/{id}/activate` | — | Activate |
| PATCH | `/{id}/deactivate` | — | Deactivate |
| PATCH | `/{id}/soft-delete` | — | Soft delete (sets `deletedAt`) |

**`documentScope`:** `SYSTEM` | `WORKSPACE`

---

## Appendix A — Enums

| Enum | Values |
|---|---|
| User status | `ACTIVE` \| `INACTIVE` \| `SUSPENDED` |
| IAM permission status | `ACTIVE` \| `INACTIVE` |
| IAM resource scope level | `SYSTEM` \| `ORGANIZATION` \| `WORKSPACE` |
| IAM data access policy | `OWNER_ONLY` \| `ANCESTOR_INHERITED` \| `SCOPE_WIDE` |
| IAM permission category | `SECURITY` \| `RESOURCE_ADMIN` \| `GOVERNANCE` \| `NOTIFICATION_ADMIN` \| `ORGANIZATION_ADMIN` \| `WORKSPACE_ADMIN` \| `ACCESS_CONTROL` \| `TEAM_ADMIN` \| `MEMBER_ADMIN` \| `CONTENT_ADMIN` |
| IAM permission risk level | `LOW` \| `MEDIUM` \| `HIGH` \| `CRITICAL` |
| IAM grant effect | `ALLOW` \| `DENY` |
| IAM grant kind | `OWNER` \| `DIRECT` \| `TEAM` \| `ROLE` \| `DELEGATED` |
| IAM subject type | `USER` \| `TEAM` \| `ROLE` |
| IAM inheritance scope | `SELF_ONLY` \| `DESCENDANTS` \| `SPECIFIC_CHILDREN` |
| Org member membership type | `OWNER` \| `ADMIN` \| `MEMBER` |
| Org member status | `ACTIVE` \| `SUSPENDED` \| `REMOVED` |
| Org invitation status | `PENDING` \| `ACCEPTED` \| `DECLINED` \| `EXPIRED` \| `CANCELLED` |
| Organization status | `ACTIVE` \| `ARCHIVED` |
| Workspace visibility | `PRIVATE` \| `PUBLIC` |
| Workspace join policy | `INVITE_ONLY` \| `REQUEST_TO_JOIN` \| `INVITE_OR_REQUEST` \| `DISABLED` |
| Workspace status | `ACTIVE` \| `ARCHIVED` |
| Invitation status | `PENDING` \| `ACCEPTED` \| `EXPIRED` \| `REVOKED` |
| Join request status | `PENDING` \| `APPROVED` \| `REJECTED` \| `CANCELLED` |
| Onboarding option | `CREATE_WORKSPACE` \| `JOIN_WITH_INVITATION` |
| Onboarding status | `IN_PROGRESS` \| `WAITING_FOR_APPROVAL` \| `COMPLETED` \| `CANCELLED` \| `FAILED` |
| Onboarding step | `CHOOSE_WORKSPACE_OPTION` \| `CREATE_WORKSPACE` \| `ENTER_INVITATION_CODE` \| `WAITING_JOIN_APPROVAL` \| `COMPLETED` |
| AI Agent provider/model/deployment/agent status | `ACTIVE` \| `INACTIVE` \| `DEPRECATED` |
| AI Agent environment | `DEV` \| `UAT` \| `PROD` |
| AI model type | `TEXT_GENERATION` \| `EMBEDDING` \| `IMAGE_GENERATION` |
| AI agent type | `CONVERSATIONAL` \| `TASK_EXECUTOR` _(stored as string — open set)_ |
| Agent output format | `TEXT` \| `JSON` \| `MARKDOWN` |
| Prompt content format | `PLAIN_TEXT` \| `MARKDOWN` \| `JINJA2` |
| Prompt version status | `DRAFT` \| `ACTIVE` \| `ARCHIVED` |
| Event trigger type | `AUTOMATIC` \| `MANUAL` |
| Event config status | `ACTIVE` \| `INACTIVE` |
| Usage policy target type | `GLOBAL` \| `EVENT_CONFIG` \| `AGENT` \| `MODEL_DEPLOYMENT` |
| Usage policy action | `BLOCK` \| `WARN` \| `THROTTLE` |
| Usage policy period | `HOURLY` \| `DAILY` \| `WEEKLY` \| `MONTHLY` |
| Execution status | `PENDING` \| `RUNNING` \| `SUCCEEDED` \| `FAILED` \| `CANCELLED` |
| Execution trigger source | `API` \| `SCHEDULER` \| `EVENT` \| `PLAYGROUND` |
| Provider secret status | `ACTIVE` \| `INACTIVE` |
| Event variable type | `STRING` \| `NUMBER` \| `BOOLEAN` \| `OBJECT` \| `ARRAY` |
| Email scope | `SYSTEM` \| `WORKSPACE` |
| Email recipient strategy | `FIXED_ADDRESS` \| `EVENT_PAYLOAD_FIELD` \| `WORKSPACE_USERS_WITH_RIGHT` |
| Model parameter support status | `SUPPORTED` \| `UNSUPPORTED` \| `UNKNOWN` |
| Model parameter value type | `INTEGER` \| `FLOAT` \| `BOOLEAN` \| `STRING` |
| Model parameter if-null behavior | `DO_NOT_SEND_PARAMETER` \| `USE_PROVIDER_DEFAULT` |
| Document scope | `SYSTEM` \| `WORKSPACE` |
| Audit actor type | `USER` \| `SERVICE` \| `SYSTEM` |
| Outbox status | `PENDING` \| `PUBLISHED` \| `FAILED` |
| Knowledge document type status | `ACTIVE` \| `INACTIVE` |
| IAM action code (seeded) | `VIEW` \| `CREATE` \| `UPDATE` \| `ARCHIVE` \| `DEACTIVATE` \| `DELETE` \| `EXPORT` \| `INVITE` \| `ASSIGN_ROLE` \| `ADD` \| `REMOVE` \| `MANAGE` \| `CREATE_WORKSPACE` \| `MANAGE_MEMBER` \| `MANAGE_TEAM` \| `MANAGE_ROLE` \| `MANAGE_ACCESS` \| `MANAGE_SETTING` \| `MANAGE_PERMISSION` \| `INVITE_MEMBER` \| `MANAGE_INVITATION` \| `REQUEST_JOIN` \| `MANAGE_JOIN_REQUEST` \| `RETRY` \| `VIEW_USER` \| `CREATE_USER` \| `VIEW_ROLE` \| `VIEW_RIGHT` \| `VIEW_ACCESS_GRANT` \| `MANAGE_ACCESS_GRANT` \| `MANAGE_DOCUMENT_TYPE` \| `VIEW_NOTIFICATION` \| `MANAGE_NOTIFICATION` \| `MANAGE_TEMPLATE` \| `MANAGE_RULE` \| `VIEW_DELIVERY` \| `RETRY_DELIVERY` |

---

## Appendix B — Seeded Admin Account

After first `docker compose up`, an admin account is seeded automatically (configured via `.env`):

```
Username : admin
Password : Admin@123456
Role     : SUPER_ADMIN
```

Login:
```bash
curl -c cookies.txt -X POST http://localhost:8080/api/iam/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"Admin@123456"}'
```

---

## Appendix C — Quick Start (Next.js)

```ts
// lib/api.ts
import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8080',
  withCredentials: true,           // send cookies
  xsrfCookieName: 'XSRF-TOKEN',
  xsrfHeaderName: 'X-XSRF-TOKEN',
});

// Auto-refresh on 401
api.interceptors.response.use(
  res => res,
  async err => {
    if (err.response?.status === 401 && !err.config._retry) {
      err.config._retry = true;
      await api.post('/api/iam/auth/refresh');
      return api(err.config);
    }
    return Promise.reject(err);
  }
);

export default api;
```

```ts
// Usage examples
await api.post('/api/iam/auth/login', { username: 'admin', password: 'Admin@123456' });
const context = await api.get('/api/workspace-context/current');
const permissions = await api.get('/api/iam/permissions/matrix');
```

### Seeded Permissions Reference

| Permission code | Scope | Category | Actions |
|---|---|---|---|
| `SYSTEM_IAM_MANAGEMENT` | SYSTEM | SECURITY | `VIEW_USER`, `CREATE_USER`, `VIEW_ROLE`, `MANAGE_ROLE`, `VIEW_RIGHT`, `VIEW_ACCESS_GRANT`, `MANAGE_ACCESS_GRANT`, `MANAGE_PERMISSION` |
| `SYSTEM_RESOURCE_MANAGEMENT` | SYSTEM | RESOURCE_ADMIN | `MANAGE` |
| `SYSTEM_GOVERNANCE_MANAGEMENT` | SYSTEM | GOVERNANCE | `MANAGE` |
| `SYSTEM_NOTIFICATION_MANAGEMENT` | SYSTEM | NOTIFICATION_ADMIN | `MANAGE_TEMPLATE`, `MANAGE_RULE`, `VIEW_DELIVERY`, `RETRY_DELIVERY` |
| `ORGANIZATION_MANAGEMENT` | ORGANIZATION | ORGANIZATION_ADMIN | `CREATE_WORKSPACE`, `UPDATE`, `ARCHIVE` |
| `ORGANIZATION_MEMBER_MANAGEMENT` | ORGANIZATION | MEMBER_ADMIN | `MANAGE_MEMBER`, `INVITE_MEMBER` |
| `ORGANIZATION_INVITATION_MANAGEMENT` | ORGANIZATION | ACCESS_CONTROL | `MANAGE_INVITATION` |
| `WORKSPACE_MANAGEMENT` | WORKSPACE | WORKSPACE_ADMIN | `UPDATE`, `ARCHIVE` |
| `WORKSPACE_ACCESS_MANAGEMENT` | WORKSPACE | ACCESS_CONTROL | `INVITE_MEMBER`, `MANAGE_JOIN_REQUEST`, `MANAGE_INVITATION` |
| `WORKSPACE_ROLE_MANAGEMENT` | WORKSPACE | ACCESS_CONTROL | `ASSIGN_ROLE` |
| `TEAM_MANAGEMENT` | WORKSPACE | TEAM_ADMIN | `MANAGE` |
| `KNOWLEDGE_MANAGEMENT` | WORKSPACE | CONTENT_ADMIN | `MANAGE_DOCUMENT_TYPE` |
