# Phase 44 — Artifact Index — Repository-Aligned

> Status: **Accepted**

| Artifact | Purpose | Blocking |
|---|---|---:|
| `ADR_044_PHASE_44_AI_ACTION_AGENTIC_OPERATIONS_REPO_ALIGNED.md` | lock module, paths, tools, hashes, limits, compatibility | yes |
| `V124__phase_44_ai_action_core.sql` | request/plan/step/preview/confirmation/execution/compensation DDL | yes |
| `V125__phase_44_ai_action_registry_realtime.sql` | schema/tool/policy/realtime/control DDL | yes |
| `PHASE_44_API_CONTRACTS_REPO_ALIGNED.md` | full REST request/response contracts | yes |
| `PHASE_44_ACTION_TOOL_CATALOG_MVP_REPO_ALIGNED.md` | active MVP tools and Phase 43 mapping | yes |
| `PHASE_44_ACTION_PLAN_SCHEMA_REGISTRY_REPO_ALIGNED.md` | JSON schemas and canonical plan hash | yes |
| `PHASE_44_PHASE41_PGVECTOR_RETRIEVAL_DEPENDENCY_REPO_ALIGNED.md` | lock V123/knowledge.search/PostgreSQL-pgvector dependency and no-direct-access boundary | yes |
| `PHASE_44_TOOL_GATEWAY_ORCHESTRATOR_INTEGRATION_REPO_ALIGNED.md` | existing AiTool registry + typed Action adapters | yes |
| `PHASE_44_CONFIRMATION_RISK_POLICY_REPO_ALIGNED.md` | dual authorization, risk, preview, confirmation | yes |
| `PHASE_44_EXECUTION_STATE_MACHINE_REPO_ALIGNED.md` | durable lifecycle and worker model | yes |
| `PHASE_44_IDEMPOTENCY_COMPENSATION_REPO_ALIGNED.md` | retry, partial failure, compensation | yes |
| `PHASE_44_WEBSOCKET_REALTIME_CONTRACT_REPO_ALIGNED.md` | STOMP/Redis/durable replay contract | yes |
| `PHASE_44_PHASE21_PHASE43_COMPATIBILITY_REPO_ALIGNED.md` | legacy and recommendation apply boundaries | yes |
| `PHASE_44_IAM_EVENT_SEED_CATALOG_REPO_ALIGNED.md` | authority/event catalogs and initializer names | yes |
| `PHASE_44_MAVEN_INFRA_DEPENDENCIES_REPO_ALIGNED.md` | WebSocket/Redis/worker dependencies | yes |
| `PHASE_44_GAP_CLOSURE_MATRIX_REPO_ALIGNED.md` | current-vs-target classification | yes |
| `PHASE_44_PRE_CODE_CHECKLIST_REPO_ALIGNED.md` | implementation entry gate | yes |
| `PHASE_44_COMPLETION_TEMPLATE_REPO_ALIGNED.md` | completion evidence | yes |
| `PHASE_44_AI_TOOL_ACTION_AGENTIC_OPERATIONS_TO_BE_DETAILED_REPO_ALIGNED.md` | consolidated detailed specification | yes |

Precedence:

```text
compile-safe repository/conventions
→ ADR-044
→ focused Phase 44 contracts
→ consolidated TO-BE document
→ older planning text
```
