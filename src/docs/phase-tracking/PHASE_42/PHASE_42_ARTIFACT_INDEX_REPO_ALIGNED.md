# Phase 42 — Repository-Aligned Artifact Index

> Target repository folder: `src/docs/phase-tracking/PHASE_42/`

| Artifact | Purpose | Implementation precedence |
|---|---|---:|
| `ADR_042_PHASE_42_CONTEXTUAL_AI_CHAT_REPO_ALIGNED.md` | locks module, tables, API, SSE, tool path, quotas, retention, memory | 1 |
| `PHASE_42_CONTEXTUAL_AI_CHAT_GUIDANCE_TO_BE_DETAILED_REPO_ALIGNED.md` | complete TO-BE implementation spec | 2 |
| `V97__phase_42_aiassistant_conversation_core.sql` | conversation/message/context/citation/tool/memory/guide/feedback DDL | exact DDL |
| `V98__phase_42_aiassistant_streaming_quota.sql` | durable SSE replay, active streams, quota usage DDL | exact DDL |
| `PHASE_42_API_CONTRACTS_REPO_ALIGNED.md` | full REST request/response schemas | exact HTTP contract |
| `PHASE_42_SSE_STREAMING_CONTRACT_REPO_ALIGNED.md` | event payloads, replay, state machine, cancellation | exact streaming contract |
| `PHASE_42_CONTEXT_BUILDER_CONTRACT_REPO_ALIGNED.md` | server-authoritative UI/entity/action context | exact context contract |
| `PHASE_42_ORCHESTRATOR_INTEGRATION_REPO_ALIGNED.md` | AiTool registry + knowledge.search + LLM + persistence flow | exact integration contract |
| `PHASE_42_IAM_EVENT_SEED_CATALOG_REPO_ALIGNED.md` | rights, initializer names, events and safe variables | exact catalog |
| `PHASE_42_MAVEN_INFRA_DEPENDENCIES_REPO_ALIGNED.md` | Spring MVC/SseEmitter and runtime dependency decisions | dependency lock |
| `PHASE_42_GAP_CLOSURE_MATRIX_REPO_ALIGNED.md` | current-vs-TO-BE gap classification | review evidence |
| `PHASE_42_PRE_CODE_CHECKLIST_REPO_ALIGNED.md` | hard gates before implementation | Definition of Ready |
| `PHASE_42_COMPLETION_TEMPLATE_REPO_ALIGNED.md` | required implementation completion evidence | completion template |

## Target code/migration placement

```text
src/docs/phase-tracking/PHASE_42_CONTEXTUAL_AI_CHAT_GUIDANCE_TO_BE_DETAILED.md
src/docs/phase-tracking/PHASE_42/*.md
src/main/resources/db/migration/V97__phase_42_aiassistant_conversation_core.sql
src/main/resources/db/migration/V98__phase_42_aiassistant_streaming_quota.sql
```

The SQL files are included in the docs package as locked source artifacts and must be copied unchanged into the real Flyway migration folder, unless V97/V98 are occupied. When renumbering is necessary, rename both migrations atomically and update all references.
