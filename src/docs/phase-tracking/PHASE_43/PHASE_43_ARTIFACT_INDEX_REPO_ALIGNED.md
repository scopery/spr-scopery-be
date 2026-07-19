# Phase 43 — Artifact Index — Repository-Aligned

> Destination in repository: `src/docs/phase-tracking/PHASE_43/`

| Artifact | Purpose | Blocking |
|---|---|---:|
| `ADR_043_PHASE_43_AI_RECOMMENDATION_REPO_ALIGNED.md` | module, prefix, API, Phase 21 coexistence, boundaries | yes |
| `V99__phase_43_ai_recommendation_core.sql` | core recommendation persistence | yes |
| `V100__phase_43_ai_recommendation_registry.sql` | schema/pack/detector/NBA registry tables | yes |
| `PHASE_43_API_CONTRACTS_REPO_ALIGNED.md` | endpoint JSON schemas/errors | yes |
| `PHASE_43_PHASE21_COMPATIBILITY_REPO_ALIGNED.md` | historical/read/apply coexistence | yes |
| `PHASE_43_SUGGESTION_SCHEMA_REGISTRY_REPO_ALIGNED.md` | six MVP schemas and masking | yes |
| `PHASE_43_RECOMMENDATION_PACKS_MVP_REPO_ALIGNED.md` | pack/detector whitelist | yes |
| `PHASE_43_DEDUP_SUPPRESSION_COOLDOWN_REPO_ALIGNED.md` | deterministic lifecycle algorithms | yes |
| `PHASE_43_PREPARE_APPLY_PHASE44_HANDOFF_REPO_ALIGNED.md` | no-mutation/handoff behavior | yes |
| `PHASE_43_ORCHESTRATOR_INTEGRATION_REPO_ALIGNED.md` | Phase 41/42/AiTool integration | yes |
| `PHASE_43_IAM_EVENT_SEED_CATALOG_REPO_ALIGNED.md` | rights/events/inbox rules | yes |
| `PHASE_43_SSE_SUGGESTION_STREAMING_REPO_ALIGNED.md` | locked decision to defer new SSE | yes |
| `PHASE_43_GAP_CLOSURE_MATRIX_REPO_ALIGNED.md` | current vs TO-BE classification | yes |
| `PHASE_43_PRE_CODE_CHECKLIST_REPO_ALIGNED.md` | implementation gate | yes |
| `PHASE_43_COMPLETION_TEMPLATE_REPO_ALIGNED.md` | proof/evidence template | yes |
| `PHASE_43_AI_RECOMMENDATION_SUGGESTION_ENGINE_TO_BE_DETAILED_REPO_ALIGNED.md` | consolidated Phase 43 spec | yes |

Repository placement:

```text
src/docs/phase-tracking/PHASE_43/<all markdown artifacts>
src/main/resources/db/migration/V99__phase_43_ai_recommendation_core.sql
src/main/resources/db/migration/V100__phase_43_ai_recommendation_registry.sql
```

The SQL files may also be copied into the docs folder for traceability, but the runtime source of truth is the Flyway migration directory.
