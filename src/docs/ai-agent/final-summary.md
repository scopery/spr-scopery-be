# AI Agent Module — Final Summary

## What Was Built

The AI Agent module is a fully functional, DDD-oriented bounded context within the Scopery modular monolith. It enables administrators to configure AI provider integrations, define agent behaviors, version prompts, bind agents to application events, enforce usage policies, and observe execution history — all through a REST API.

---

## Completed Phases

| Phase | Scope | Status |
|---|---|---|
| 0 | Java 21, Spring Boot 3.4, project skeleton, Flyway, application.yml | Done |
| 0.5 | ApiResponse, ErrorResponse, exceptions, pagination, audit, traceId, ActivityLog | Done |
| 1 | Provider CRUD + activate/deactivate + Swagger setup | Done |
| 2 | AI Model CRUD + activate/deactivate, linked to Provider | Done |
| 3 | Model Deployment CRUD + activate/deactivate/set-default, linked to AI Model | Done |
| 4 | AI Agent shared kernel — AiAgentActivityLogger, AiAgentEnumParser, all constant classes | Done |
| 5 | Model Parameter Capability CRUD + activate/deactivate | Done |
| 6 | Agent CRUD + activate/deactivate | Done |
| 6.1 | Full Error Catalog Refactor — ErrorCatalog, AppException, AiAgentErrorCatalog, AiAgentExceptions | Done |
| 7 | Prompt module — PromptTemplate + PromptVersion with versioning and archiving | Done |
| 7.1 | Event Registry module — EventDefinition under `modules/eventregistry/` | Done |
| 8 | AI Agent Event Configuration — links Event Registry to Agent + Prompt + Deployment | Done |
| 9 | Provider Secret — AES-256-GCM encryption for API keys | Done |
| 10 | Usage Policy — GLOBAL/AGENT/EVENT_CONFIG/MODEL_DEPLOYMENT scopes, BLOCK/WARN actions | Done |
| 11 | Usage Policy Evaluator wired into ExecutionApplicationService | Done |
| 12 | Playground module — 4 endpoints for developer testing | Done |
| 13 | Final hardening: .env.example fix, docs, seed SQL, manual test cases | Done |

---

## Architecture

```
HTTP
 │
 ▼
{Entity}Controller          (api/)
 │  Request → Command/Query
 ▼
{Entity}ApplicationService  (application/)
 │  @Transactional, orchestrates domain + AiAgentActivityLogger
 ▼
Domain objects              (domain/)
 │  Pure Java, no JPA, no Spring Web
 ▼
Jpa{Entity}Repository       (infrastructure/persistence/)
 │  Implements domain interface via Spring Data JPA
 ▼
PostgreSQL
```

**Shared kernel** (`modules/aiagent/shared/`):
- `AiAgentErrorCatalog` — single source of truth for all AI Agent error codes and HTTP statuses
- `AiAgentExceptions` — factory methods; no direct exception constructors in services
- `AiAgentActivityLogger` — wraps `ActivityLogService` for the AIAGENT module
- `AiAgentEnumParser` — safe enum parsing from String; no inline `Enum.valueOf`
- Constant classes: `AiAgentTableNames`, `AiAgentApiPaths`, `AiAgentEntityTypes`, `AiAgentActivityActions`, `AiAgentSortFields`

---

## Database Schema

12 migration files, all applied in order:

| Migration | Table |
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

All business tables use the `aiagent_` prefix. Cross-module registry tables use `app_`.

All query-heavy tables have comprehensive indexes on status, foreign key columns, and search fields.

---

## Security Design

**Provider secrets:**
- API keys are stored encrypted using AES-256-GCM
- Each stored secret has: `encrypted_value` (Base64), `iv` (Base64), `key_version`
- Master key is injected via env var `SCOPERY_SECRET_MASTER_KEY` (never stored in DB or code)
- API responses return only `maskedValue` — never the raw or encrypted key
- Only one ACTIVE secret per provider type enforced by partial unique index

**Sensitive data logging:**
- Prompt content, AI output, input variables, and API keys are never logged
- `RequestLoggingFilter` logs method, path, status, duration, IP, traceId only
- `AiAgentActivityLogger` records action type and entity ID — no content fields

**API safety:**
- No SQL string concatenation — all queries use parameterized JPQL or Criteria API
- All `@Modifying` bulk updates return `int`, use `flushAutomatically=true`, `clearAutomatically=true`, set `updatedAt` manually
- Domain objects never returned from controllers — always mapped through Response records

---

## Execution Flow

```
POST /api/v1/ai-agent/executions/event-config/{id}
  1. Load active EventConfig for AIAGENT_RUNTIME_ENVIRONMENT
  2. Load Agent (must be ACTIVE)
  3. Load PromptVersion (must be ACTIVE)
  4. Load PromptTemplate (must be ACTIVE + match agent)
  5. Load ModelDeployment (must be ACTIVE)
  6. Load AiModel (must be ACTIVE)
  7. Load Provider (must be ACTIVE)
  8. Decrypt provider API key
  9. Evaluate UsagePolicies (GLOBAL + EVENT_CONFIG + AGENT + MODEL_DEPLOYMENT)
     → BLOCKED: throw 429 USAGE_POLICY_EXCEEDED, no log written
     → WARN: continue, collect warnings
 10. Render prompt template with inputVariables
 11. Create ExecutionLog (status = RUNNING)
 12. Call OpenAI Responses API
 13. Mark ExecutionLog COMPLETED (or FAILED on error)
 14. Return ExecutionRunResponse with output, tokens, cost, policyDecision, warnings
```

---

## Usage Policy Evaluation

Policies are evaluated before the AI call to avoid log state issues (log doesn't exist yet when BLOCKED).

| Scope | Evaluates | When |
|---|---|---|
| GLOBAL | All executions | Always |
| EVENT_CONFIG | Executions for this event config | When eventConfigId is non-null |
| AGENT | Executions by this agent | Always |
| MODEL_DEPLOYMENT | Executions on this deployment | Always |

Policy metrics: `maxRequestsPerPeriod`, `maxTokensPerPeriod`, `maxCostPerPeriod`, `maxConcurrentRequests`, `dailyBudget`.
Period units: `MINUTE`, `HOUR`, `DAY`, `MONTH` (truncated to UTC boundaries).

---

## Playground Module

Four endpoints for admin/developer testing:

| Endpoint | Purpose |
|---|---|
| `POST /playground/event-config/{id}/run` | Test an existing active EventConfig binding |
| `POST /playground/direct/run` | Test any Agent + PromptVersion + Deployment combination directly |
| `POST /playground/prompt/preview` | Render prompt template without calling AI; returns missingVariables |
| `GET /playground/options` | Return active options for UI dropdowns |

Playground executions are recorded in `aiagent_execution_log` with `trigger_source = PLAYGROUND`.  
Direct playground runs have `event_config_id = NULL` in the log.

---

## API Surface

13 Swagger tag groups under `http://localhost:8080/swagger-ui.html`:

- AI Agent - Providers
- AI Agent - Provider Secrets
- AI Agent - AI Models
- AI Agent - Model Deployments
- AI Agent - Model Parameter Capabilities
- AI Agent - Agents
- AI Agent - Prompt Templates
- AI Agent - Prompt Versions
- Event Registry - Event Definitions
- AI Agent - Event Configs
- AI Agent - Usage Policies
- AI Agent - Executions
- AI Agent - Playground

---

## Known Limitations

| Limitation | Impact | Next Phase |
|---|---|---|
| No Spring Security | All endpoints are open | Phase 14 (Security + SSO) |
| No prompt approval workflow | Versions can be activated without review | Phase 14 |
| Redis rate limiting not implemented | Usage policies use DB aggregate queries; may be slow at high volume | Future |
| Only OpenAI adapter wired | Gemini/Claude clients are stub placeholders | Future |
| Master key rotation not implemented | Rotating key requires manual re-encryption of all secrets | Future |
| No integration test infrastructure | Test coverage is unit tests only (Mockito) | Future |
| `show-sql: true` in application.yml | Acceptable for dev; disable for production | Phase 14 |

---

## Files Created (Phase 13)

| File | Type |
|---|---|
| `.env.example` | Modified — `SPRING_DATASOURCE_USERNAME` corrected to `scopery_user` |
| `src/docs/ai-agent/README.md` | Developer runbook |
| `src/docs/ai-agent/api-samples.md` | curl samples for all 13 endpoint groups |
| `src/docs/ai-agent/manual-test-cases.md` | 13 manual test cases (TC-01 to TC-13) |
| `src/docs/ai-agent/demo-seed.sql` | Idempotent SQL seed for full demo setup |
| `src/docs/ai-agent/final-summary.md` | This document |

No new DB migration was created in Phase 13 — all required indexes were already present from V9–V12.

---

## Next Phase Options

**Phase 14 — Security & Governance:**
- Spring Security + JWT / SSO
- Role-based access: ADMIN, DEVELOPER, VIEWER
- Prompt version approval workflow (PENDING_APPROVAL → APPROVED → ACTIVE)
- Audit trail enhancements (actorId, actorName from security context)

**Phase 15 — Frontend:**
- Next.js admin UI for all configuration screens
- Playground UI with live prompt preview
- Execution log viewer with filter/search

**Phase 16 — Redis Rate Limiting:**
- Real-time counters in Redis for usage policies
- Replaces DB aggregate queries for hot path evaluation

**Phase 17 — Additional Providers:**
- Google Gemini adapter
- Anthropic Claude adapter
- Provider-agnostic parameter mapping
