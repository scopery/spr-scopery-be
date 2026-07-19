# Phase 07 — AI Agent Platform TO-BE Complete

## 1. Summary

Phase 07 hardens the existing AI Agent module into a governed platform foundation: provider/model deactivation guards, model capability flags, agent autonomy/scope (mutation autonomy rejected), prompt version activation metadata + immutability, EventRegistry-bound event configs, usage-policy BLOCKED execution logs with outbox `AI_USAGE_POLICY_BLOCKED`, enriched execution logs (`traceId`, provider/model/environment), expanded AI event seeds, default DEV/PROD usage policies, seed-only AI failure notification templates, and IAM interceptor coverage for models/agents/usage-policies. RAG, tools, acting-on-behalf, and AI project planning remain deferred.

## 2. Source Inputs Reviewed

- `PHASE_00_MASTER_ROADMAP_MODULAR_REVISED.md`
- `PHASE_01`–`PHASE_06` specs + completion files
- `PHASE_07_AI_AGENT_PLATFORM_TO_BE_DETAILED.md`
- Existing `modules/aiagent` (provider → execution/playground/usagepolicy)
- Event Registry, Notification, IAM, platform outbox/audit patterns
- `CLAUDE.md` Action+QueryService / AiAgentExceptions conventions

## 3. Current vs TO-BE Classification Matrix

| Capability | Classification | Notes |
|---|---|---|
| AiProvider lifecycle | `CURRENTLY_IMPLEMENTED` + hardened | Deactivate blocked if ACTIVE deployments |
| Provider secret AES-GCM + write-only API | `CURRENTLY_IMPLEMENTED` | Separate `providersecret` submodule |
| AiModel lifecycle + capability flags | Hardened | V45 flags + token limits |
| AiModelDeployment | `CURRENTLY_IMPLEMENTED` | Env remains DEV/UAT/PROD |
| AiAgent + autonomy/scope | Hardened | Default `SUGGEST_ONLY` / `SYSTEM`; `AUTO_EXECUTE_RESTRICTED` rejected |
| AiPromptTemplate / Version | Hardened | ACTIVE immutable; `activatedAt`/`activatedBy`; dual prompt fields |
| AiEventConfig ↔ EventRegistry ACTIVE | `CURRENTLY_IMPLEMENTED` + hardened | Mapping/activation audit columns |
| ExecuteEvent / ExecuteEventConfig | Hardened | Policy block → BLOCKED log + outbox; schema validation |
| AiExecutionLog enrichment | Hardened | `traceId`, provider/model/env, previews, `BLOCKED` |
| AiUsagePolicy limits + event allow/block | Hardened | Evaluator extended; default seeders |
| AI EventDefinition seeds (§18) | `SEED_ONLY_IN_PHASE_07` | Idempotent seeder expanded |
| AI failure notification templates | `SEED_ONLY_IN_PHASE_07` | Templates + disabled rules |
| Tool execution / acting-on-behalf / suggestions | `DEFERRED_TO_PHASE_21` | Not implemented |
| RAG / document context | `DEFERRED_TO_PHASE_08` / `21` | Not implemented |
| Cost dashboards | `DEFERRED_TO_PHASE_22` | Fields stored; no reporting UI |
| Content safety / evaluation | `DEFERRED_TO_PHASE_23` | Usage policy foundation only |
| Manual agent execute API | `DEFERRED_TO_PHASE_21` | Playground exists; dedicated agent execute deferred |

## 4. Implemented in Current BE (pre–Phase 07)

- Full CRUD lifecycle for Provider, Model, Deployment, Agent, PromptTemplate/Version, EventConfig, UsagePolicy
- AES-GCM provider secrets; masked API responses; runtime secret resolution
- EventConfig validates ACTIVE EventDefinition + agent/prompt/deployment + env match
- PromptVersion DRAFT editable / ACTIVE immutable; single ACTIVE per template
- UsagePolicyEvaluator rate/token/cost with WARN/BLOCK
- ExecuteEvent / ExecuteEventConfig / Playground with provider adapters
- Coarse IAM rights + `AiAgentSecurityInterceptor` for subset of paths
- Partial AI event seed (10 codes)

## 5. Implemented / Hardened in This Phase

- **V45** migration: model capabilities, agent autonomy/scope, prompt version metadata, execution log enrichment, usage policy finer limits, event config mapping/activation columns
- Deactivate Provider/Model blocked when ACTIVE deployments exist (`AI_PROVIDER_HAS_ACTIVE_DEPLOYMENTS` / `AI_MODEL_HAS_ACTIVE_DEPLOYMENTS`)
- `ExecutionStatus.BLOCKED` + log before throw; outbox enqueue `AI_USAGE_POLICY_BLOCKED`
- Error codes: `AI_AGENT_AUTONOMY_NOT_ALLOWED`, `AI_EXECUTION_POLICY_BLOCKED`, autonomy/scope invalid enums
- Agent create/update accepts autonomy/scope/org/workspace; rejects `AUTO_EXECUTE_RESTRICTED`
- `UsagePolicyEvaluator`: environment match, allow/block event definition IDs, max tokens/request, daily limits
- `AiAgentSecurityInterceptor`: WRITE on models/agents/prompt-templates/usage-policies/capabilities → `AI_PLATFORM_MANAGE`
- Expanded `AiAgentEventDefinitionSeedInitializer` (§18 list)
- `AiUsagePolicySeedInitializer`: `DEFAULT_AI_USAGE_POLICY_DEV` / `PROD`
- System email templates for AI execution failed / usage policy blocked (rules seeded inactive)
- Focused tests for deactivate guard, policy BLOCKED path, autonomy rejection

## 6. Seed-only Items Added

| Code / Artifact | Purpose | Emitter / Consumer |
|---|---|---|
| Full AI EventDefinition catalog (§18) | Platform event contracts | Lifecycle activity mostly; policy block via outbox |
| `DEFAULT_AI_USAGE_POLICY_DEV` / `PROD` | Conservative GLOBAL limits | Evaluator at execution |
| `AI_EXECUTION_FAILED_EMAIL` + rule (inactive) | Admin alert foundation | Phase 20 wiring |
| `AI_USAGE_POLICY_BLOCKED_EMAIL` + rule (inactive) | Admin alert foundation | Phase 20 wiring |

## 7. Deferred Items and Target Phase

| Item | Target |
|---|---|
| AI project planning / WBS / suggestions | Phase 21 |
| Tool execution + tool IAM | Phase 21 |
| Acting-on-behalf dual authorization | Phase 21 |
| RAG / embeddings / document context | Phase 08 / 21 |
| Prompt evaluation / human feedback | Phase 23 |
| Content safety / injection detection | Phase 23 |
| AI cost/usage dashboards | Phase 22 |
| Full AI notification fan-out / preferences | Phase 20 |
| Granular per-resource IAM rights (~30 codes) | Keep coarse rights; expand later if product needs |
| Minute-window rate evaluation for `max_requests_per_minute` | Phase 23 / follow-up |
| Manual `POST /agents/{id}/execute` | Phase 21 (playground covers DEV runs) |
| External secret manager `credential_ref` | Post-23 / ops |

## 8. Entity Mapping

### AiModel (`aiagent_model`)

| TO-BE | Actual |
|---|---|
| supports_chat / embedding / tool_calling / json_mode | BOOLEAN columns (V45) |
| context_window_tokens / max_output_tokens | INT nullable |
| model_family / capabilities_json | VARCHAR / JSONB |

### AiAgent (`aiagent_agent`)

| TO-BE | Actual |
|---|---|
| autonomy_level | VARCHAR + CHECK; default `SUGGEST_ONLY` |
| scope | `SYSTEM`/`ORGANIZATION`/`WORKSPACE`; default `SYSTEM` |
| organization_id / workspace_id | UUID nullable |

### AiPromptVersion (`aiagent_prompt_version`)

| TO-BE | Actual |
|---|---|
| system_prompt / user_prompt_template | Added; legacy `content` retained; resolve prefers user template |
| temperature / top_p / max_tokens / response_format / response_schema_json | Added |
| activated_at / activated_by | Set on activate |

### AiExecutionLog (`aiagent_execution_log`)

| TO-BE | Actual |
|---|---|
| trace_id / provider_id / model_id / environment | Added |
| prompt_template_id / input_hash / previews / currency | Added |
| block_reason_code | Added |
| BLOCKED status | App enum (+ migration comment; no prior DB CHECK) |
| trigger_type | Existing `trigger_source` |

### AiUsagePolicy (`aiagent_usage_policy`)

| TO-BE | Actual |
|---|---|
| environment + finer max_* limits | Added alongside existing period limits |
| allowed/blocked_event_definition_ids | TEXT (JSON list of UUIDs) |

### AiEventConfig (`aiagent_event_config`)

| TO-BE | Actual |
|---|---|
| input/output_mapping_json | TEXT nullable |
| activated/deactivated at/by | Added |
| Uniqueness | Existing partial unique `(event_definition_id, environment)` WHERE ACTIVE (stricter than agent+env) |

## 9. API Changes

| Method | Path | Change |
|---|---|---|
| POST/PUT | `/api/ai-agent/agents` | + `autonomyLevel`, `scope`, `organizationId`, `workspaceId` |
| GET | agents/models/prompt-versions/usage-policies/event-configs/execution-logs | Responses expose new fields where mapped |
| WRITE | `/models`, `/agents`, `/prompt-templates`, `/usage-policies`, `/model-parameter-capabilities` | Now require `AI_PLATFORM_MANAGE` |
| Behavior | Deactivate provider/model | 422 if active deployments |
| Behavior | Execute event(s) | Policy block → BLOCKED log + `AI_EXECUTION_POLICY_BLOCKED` |

No new public paths required for Phase 07 core.

## 10. Provider Secret Strategy

- Secrets live in `aiagent_provider_secret` (AES-GCM), not on provider row
- API returns masked values only (`hasCredential` / `maskedValue`)
- Resolved only at execution via `ProviderSecretResolver`
- Never returned in provider/deployment responses
- External `credential_ref` secret manager deferred

## 11. Model / Deployment Strategy

- Capability flags stored on model for governance/reporting; execution still uses deployment defaults for temperature/max tokens
- Environments: `DEV` / `UAT` / `PROD` (UAT maps to staging concept; no separate STAGING/LOCAL enum)
- Default deployment uniqueness remains partial unique per model+env (existing V4)

## 12. Agent / Autonomy Strategy

- Default: `SUGGEST_ONLY` + `SYSTEM` scope
- Allowed Phase 07 levels: `SUGGEST_ONLY`, `DRAFT_ONLY`, `REQUIRES_APPROVAL`, `AUTO_EXECUTE_READ_ONLY`
- Rejected: `AUTO_EXECUTE_RESTRICTED` (`AI_AGENT_AUTONOMY_NOT_ALLOWED`) — no business mutation path exists
- Execution only renders prompt, calls provider, logs result

## 13. Prompt Versioning Strategy

- DRAFT editable; ACTIVE/ARCHIVED immutable (`update` domain guard)
- Activate archives previous ACTIVE; sets `activatedAt`/`activatedBy`
- Legacy `content` kept for backward compatibility; `resolvedPromptContent()` prefers `user_prompt_template`

## 14. EventConfig / EventRegistry Integration

- Create/activate/update still require ACTIVE EventDefinition
- Agent, ACTIVE PromptVersion, ACTIVE Deployment, environment match enforced
- Input/output mapping columns reserved (pass-through execution; advanced mapping → Phase 32)

## 15. ExecuteEvent Flow

```text
Resolve EventDefinition (ACTIVE)
→ Resolve ACTIVE EventConfig for env
→ Validate agent / prompt / deployment / provider ACTIVE
→ Schema-validate input variables
→ UsagePolicyEvaluator
   → if BLOCKED: save ExecutionLog(BLOCKED), enqueue AI_USAGE_POLICY_BLOCKED, throw AI_EXECUTION_POLICY_BLOCKED (no provider call)
→ Render prompt → call provider → SUCCEEDED/FAILED log
→ traceId from MDC stored on log
```

## 16. UsagePolicy Rules

Evaluation includes: GLOBAL/AGENT/DEPLOYMENT/EVENT_CONFIG targets, period limits, concurrent, daily budget, environment, allow/block event IDs, max tokens/request, daily request/token/cost fields when set. Default seeded DEV (looser) and PROD (stricter) GLOBAL policies.

## 17. Execution Log Contract

Every attempt should yield a log: SUCCEEDED / FAILED / BLOCKED. Blocked attempts never call the provider. Secrets must not appear in previews/errors (sanitized truncation retained).

## 18. IAM Authorization Matrix

| Surface | Right |
|---|---|
| Provider / deployment writes | `AI_PLATFORM_MANAGE` |
| Model / agent / prompt-template / usage-policy / capability writes | `AI_PLATFORM_MANAGE` (closed Phase 07 gap) |
| Prompt version writes | `AI_PROMPT_PUBLISH` |
| Event config writes | `AI_EVENT_CONFIG_MANAGE` |
| Provider secrets | `AI_PROVIDER_SECRET_MANAGE` |
| Execution / logs | `AI_EXECUTION_VIEW_OR_RUN` |
| Playground writes | `AI_PLAYGROUND_RUN` |

Coarse rights retained (not full Phase 07 ~30 granular codes).

## 19. Event Seeder Matrix

| Seeder | Order | Source system |
|---|---|---|
| `AiAgentEventDefinitionSeedInitializer` | 12 | `SCOPERY_AI_AGENT` |

Codes: provider/model/deployment/agent/prompt/event-config/execution/usage-policy lifecycle including `AI_USAGE_POLICY_BLOCKED`.

## 20. Notification Integration

| Item | Status |
|---|---|
| Event defs for failure/block | Seeded |
| Email templates | Seeded (`AI_EXECUTION_FAILED_EMAIL`, `AI_USAGE_POLICY_BLOCKED_EMAIL`) |
| Email rules | Seeded **inactive** (SEED_ONLY) |
| Full emit → inbox fan-out | Deferred Phase 20; policy block uses platform outbox |

## 21. Activity / Audit / Outbox Notes

- Activity via `AiAgentActivityLogger` for lifecycle + blocked execution
- Policy block enqueues transactional outbox event `AI_USAGE_POLICY_BLOCKED` (`aggregateType=AI_EXECUTION_LOG`)
- Full immutable audit for every AI lifecycle event remains optional/partial; activity + outbox cover Phase 07 acceptance for block path

## 22. Tests Added / Updated

- `DeactivateProviderActionTest` — active deployment reject
- `ExecuteEventConfigActionTest` — policy BLOCKED log + `AI_EXECUTION_POLICY_BLOCKED`
- `AgentApplicationServiceTest` — autonomy mutation reject; default SUGGEST_ONLY
- `AgentTest` — default autonomy/scope
- Existing PromptVersion immutability, UsagePolicyEvaluator, ProviderSecret, EventConfig tests retained/updated

## 23. Commands Run

```text
mvn -q -DskipTests compile
mvn -q test -Dtest='com.company.scopery.modules.aiagent.**'
mvn -q test
```

## 24. Test Results

- `mvn compile` — **PASS**
- AI agent module tests — **PASS**
- Full `mvn test` — **PASS** (exit code 0)

Runtime verification of live provider calls / secret redaction in production logs: **Not yet verified — requires running the application against a real provider.**

## 25. Manual Verification

Checklist from Phase 07 §23 — perform after Flyway V45 apply:

1. Create provider + secret; GET masks secret
2. Create model/deployment/agent/prompt/version; activate version
3. Create EventConfig for ACTIVE EventDefinition; reject inactive/deprecated
4. Create/activate usage policy; force block → ExecutionLog `BLOCKED` + outbox event
5. Confirm no secret in API/logs
6. Rerun AI event seeder — no duplicates
7. Confirm WRITE `/api/ai-agent/models|agents|usage-policies` without `AI_PLATFORM_MANAGE` → forbidden

## 26. Assumptions

- Existing AES-GCM secret module satisfies “encrypted or referenced” requirement
- Coarse IAM rights sufficient vs full granular catalog for Phase 07
- UAT environment covers staging semantics
- Dual prompt fields can coexist with legacy `content`
- Disabled email rules are acceptable SEED_ONLY for Phase 07

## 27. Deviations From Prompt

- Spec path `docs/phase-complete/...` → project uses `src/docs/phase-complete/`
- Deployment uniqueness remains `(model_id, code)` not global `(environment, code)`
- EventConfig uniqueness is `(event_definition_id, environment)` ACTIVE — not `(event, agent, environment)`
- `max_requests_per_minute` column stored; minute-window evaluator not fully separate from period limits
- Create/Update HTTP DTOs for model capability flags not fully expanded (domain defaults apply; responses expose fields)
- Provider type remains LLM/OCR/EMBEDDING taxonomy (not OPENAI/AZURE vendor enum)

## 28. Known Risks

- Minute-level rate limiting incomplete if only `max_requests_per_minute` set without period metrics
- Outbox AI events need notification consumer enabled before admin emails fire
- Org/workspace-scoped agents stored but not fully authorized by workspace IAM yet
- Prompt preview redaction depth depends on sensitivity policy maturity

## 29. Future Phases That Must Return to AI Platform

| Phase | Must add |
|---|---|
| 08 | RAG / document context / embeddings |
| 20 | AI notification fan-out, preferences |
| 21 | Planning suggestions, tools, acting-on-behalf, human approval mutations |
| 22 | Cost/usage reporting dashboards |
| 23 | Content safety, evaluation, cost budgets, security testing |
| Post-23 | Tool marketplace, provider webhooks, external secret manager |
