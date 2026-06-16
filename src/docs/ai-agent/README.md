# AI Agent Module — Developer Runbook

## Overview

The AI Agent module is a bounded context inside the Scopery backend (`spr-scopery-be`). It provides:

- AI provider and model configuration
- Prompt template versioning
- Event-driven agent execution with configurable usage policies
- Execution logging and playground testing

All AI Agent APIs are prefixed with `/api/v1/ai-agent/`.

---

## Prerequisites

| Tool | Version |
|---|---|
| Java | 21 |
| Maven | 3.9+ |
| PostgreSQL | 15+ |
| Docker (optional) | Latest |

---

## Environment Setup

Copy the example env file and fill in values:

```bash
cp .env.example .env
```

Key variables:

| Variable | Description | Example |
|---|---|---|
| `SPRING_PROFILES_ACTIVE` | Active profile | `local` |
| `SPRING_DATASOURCE_URL` | PostgreSQL JDBC URL | `jdbc:postgresql://localhost:5432/scopery` |
| `SPRING_DATASOURCE_USERNAME` | DB username | `scopery_user` |
| `SPRING_DATASOURCE_PASSWORD` | DB password | (set locally) |
| `SCOPERY_SECRET_MASTER_KEY` | Base64 AES-256 master key for provider secret encryption | (generate below) |
| `SCOPERY_SECRET_KEY_VERSION` | Key version label stored with each encrypted secret | `v1` |
| `AIAGENT_RUNTIME_ENVIRONMENT` | Controls which EventConfig records are resolved at runtime | `DEV` / `UAT` / `PROD` |

### Generate the AES Master Key

```bash
openssl rand -base64 32
```

Paste the output as `SCOPERY_SECRET_MASTER_KEY` in your `.env`. Never commit this key.

---

## Database Setup

### Create the database and user (first time only)

```sql
CREATE USER scopery_user WITH PASSWORD 'your_password';
CREATE DATABASE scopery OWNER scopery_user;
GRANT ALL PRIVILEGES ON DATABASE scopery TO scopery_user;
```

### Run migrations

Flyway runs automatically on startup. Migrations are in:

```
src/main/resources/db/migration/
```

Migrations applied in order:

| File | Table(s) Created |
|---|---|
| V1 | `app_activity_log` |
| V2 | `aiagent_provider` |
| V3 | `aiagent_model` |
| V4 | `aiagent_model_deployment` |
| V5 | `aiagent_model_parameter_capability` |
| V6 | `aiagent_agent` |
| V7 | `aiagent_prompt_template`, `aiagent_prompt_version` |
| V8 | `app_event_definition` |
| V9 | `aiagent_event_config` |
| V10 | `aiagent_usage_policy` |
| V11 | `aiagent_execution_log` |
| V12 | `aiagent_provider_secret` |

---

## Running the Application

```bash
./mvnw spring-boot:run
```

The app starts on port `8080`.

- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI JSON: http://localhost:8080/v3/api-docs
- Health: http://localhost:8080/api/health

---

## Architecture

```
HTTP Request
    │
    ▼
{Entity}Controller           (api/)
    │  converts Request → Command/Query
    ▼
{Entity}ApplicationService   (application/)
    │  orchestrates use case, @Transactional
    ▼
Domain objects + Repository interfaces  (domain/)
    │
    ▼
Jpa{Entity}Repository        (infrastructure/persistence/)
    │  implements domain interface via Spring Data JPA
    ▼
PostgreSQL
```

**Layer dependency rule:**
- `api` → `application` → `domain`
- `infrastructure` → `domain`
- `domain` must NOT import `jakarta.persistence` or `org.springframework.web`

**Shared kernel** (`modules/aiagent/shared/`):
- `AiAgentErrorCatalog` — all AI Agent error codes and HTTP status
- `AiAgentExceptions` — factory methods for throwing typed errors
- `AiAgentActivityLogger` — wraps `ActivityLogService` for the AIAGENT module
- `AiAgentEnumParser` — safe enum parsing from String input

---

## End-to-End Execution Flow

When `POST /api/v1/ai-agent/executions/event-config/{id}` is called:

1. Load active EventConfig → resolved by `AIAGENT_RUNTIME_ENVIRONMENT`
2. Load Agent, PromptVersion, ModelDeployment, AiModel, Provider — each must be ACTIVE
3. Decrypt provider API key from `aiagent_provider_secret`
4. Evaluate active UsagePolicies — BLOCKED → 429, WARN → continue with warnings
5. Render prompt template with input variables
6. Call OpenAI Responses API with rendered prompt + model parameters
7. Create ExecutionLog (RUNNING → COMPLETED / FAILED)
8. Return `ExecutionRunResponse` with outputText, tokens, cost, warnings

**Playground direct flow** (`POST /api/v1/ai-agent/playground/direct/run`):

Same flow but Agent + PromptVersion + ModelDeployment are passed explicitly in the request. EventConfig policies are skipped. ExecutionLog is created with `eventConfigId = null`.

---

## Provider Secret Configuration

The API key for each AI provider is stored encrypted in `aiagent_provider_secret`.

To configure an OpenAI key:

1. Create a Provider via `POST /api/v1/ai-agent/providers`
2. Set the secret via `POST /api/v1/ai-agent/provider-secrets`  
   — field `secretValue` is the raw OpenAI API key (e.g. `sk-proj-...`)  
   — the application encrypts it with AES-256-GCM using `SCOPERY_SECRET_MASTER_KEY`  
   — only the masked value is ever returned in API responses

The master key must be stable for the lifetime of stored secrets. Rotating the key requires re-encrypting all stored secrets (out of scope for this phase).

---

## Adding a New AI Agent Sub-module

Before writing any service or controller code:

1. Add table name to `AiAgentTableNames`
2. Add API path to `AiAgentApiPaths`
3. Add entity type to `AiAgentEntityTypes`
4. Add action constants to `AiAgentActivityActions`
5. Add error entries to `AiAgentErrorCatalog`
6. Add factory methods to `AiAgentExceptions`

Then implement layers in order: `domain` → `infrastructure` → `application` → `api`.

---

## Running Tests

```bash
./mvnw test
```

Test structure: unit tests only (no H2 / Testcontainers). Application services are tested with Mockito.

Key test classes:

| Test | What it covers |
|---|---|
| `ProviderApplicationServiceTest` | Provider CRUD + status transitions |
| `ExecutionApplicationServiceTest` | Full orchestration, policy block/warn/allow |
| `UsagePolicyEvaluatorTest` | Policy evaluation — BLOCKED / WARN / ALLOWED |
| `UsageWindowCalculatorTest` | Time window truncation for MINUTE/HOUR/DAY/MONTH |
| `PlaygroundApplicationServiceTest` | Playground run (event-config, direct, preview, options) |

---

## Logging

- All requests are logged by `RequestLoggingFilter` with a `traceId` (UUID).
- `traceId` is returned as `X-Trace-Id` response header and included in error responses.
- Sensitive data (API keys, prompt content, AI output) is NOT logged.
- Activity log entries are written to `app_activity_log` for all state-changing actions.

---

## Known Limitations (This Phase)

- No Spring Security — all endpoints are open.
- Prompt approval workflow not implemented.
- Redis-based rate limiting not implemented (usage policies use DB aggregate queries only).
- Only OpenAI provider type is wired; Gemini/Claude adapters are placeholder stubs.
- Master key rotation requires manual re-encryption of stored secrets.
