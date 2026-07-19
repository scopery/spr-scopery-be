# Phase 21 — AI-Assisted Project Planning TO-BE Complete

## 1. Summary

Phase 21 delivered `modules/aiplanning`: planning runs, access-aware context snapshots, deterministic suggestion generation, human review lifecycle (start-review / accept / reject / archive), and safe apply that routes baselined structural changes through Change Request drafts. Live LLM provider orchestration and unconstrained direct domain mutation were **not** claimed.

Convention hardening (review follow-up): SafeApply/ContextBuilder moved under DDD `application/service` with `@Service`; lifecycle Actions take Command records; apply returns typed `ApplySuggestionResponse`; Phase 21 unit tests added.

## 2. Source Inputs Reviewed

- `PHASE_21_AI_ASSISTED_PROJECT_PLANNING_TO_BE_DETAILED.md`
- Phase 07 AI Agent platform, Phase 10 authorization, Phase 19 baseline/CR
- Existing `CreateChangeRequestAction` / `ProjectMutationGuard`
- Phase 21 review (FAIL / REQUEST CHANGES) — tests + CLAUDE.md convention fixes

## 3. Current vs TO-BE Classification Matrix

| Capability | Classification | Status |
|---|---|---|
| Planning run CRUD + cancel | MUST | Implemented |
| Context snapshot (access-aware) | MUST | Implemented (`context/application/service/AiPlanningContextBuilder`) |
| Suggestion + suggestion items | MUST | Implemented |
| Review / accept / reject / archive | MUST | Implemented (Command-based Actions) |
| Safe apply + apply results | MUST | Implemented (`apply/application/service/AiPlanningSafeApplyService` `@Service`) |
| Baselined → CR draft path | MUST | Implemented |
| EventDefinition seed | MUST | `@Order(28)` `SCOPERY_AI_PLANNING` |
| IAM rights | MUST | `AI_PROJECT_PLANNING_MANAGEMENT` |
| Unit tests (spec §22) | MUST | Domain + context + apply + seeder coverage added |
| Live LLM provider execute | DEFERRED / PARTIAL | Deterministic generator only |
| Convenience explain endpoints | DEFERRED | Not implemented |
| Direct WBS/TASK mutate apply | DEFERRED | Conservative SKIP / CR path |

## 4. Implemented in Current BE

- Flyway `V58__create_aiplanning_tables_phase21.sql`
- Module packages: `planningrun`, `contextsnapshot`, `suggestion`, `suggestionitem`, `reviewaction`, `applyresult`, `context`, `apply`, `shared`
- Controllers under `/api/projects/{projectId}/ai-planning/runs` and `.../suggestions`
- Safe apply: baselined structural items → `CreateChangeRequestCommand` with `SCOPE_CHANGE`
- Typed `ApplySuggestionResponse` (no `Map<String, Object>`)

## 5. Known deviations

| Item | Notes |
|---|---|
| Table prefix `ai_planning_*` | Baked in V58; CLAUDE.md §12 prefers `aiplanning_*`. Documented on `AiPlanningTableNames`; rename requires a new Flyway migration (not done in this pass). |

## 6. Deferred Items and Target Phase

| Item | Target |
|---|---|
| Live model deployment execution for planning | Phase 07 hardening / post-23 AI ops |
| Explain-schedule / explain-finance convenience APIs | Later AI UX |
| Full direct apply for non-baselined WBS edits | Controlled apply backlog |
| Full integration coverage for every §22 method name | Expand as LLM/domain apply adapters land |

## 7. Flyway / IAM / Events

- Migration: `V58`
- IAM: RUN / VIEW / REVIEW / ACCEPT / REJECT / APPLY / ARCHIVE authorities
- Events: run lifecycle + suggestion lifecycle codes seeded

## 8. Tests Added

| Test class | Focus |
|---|---|
| `AiPlanningSuggestionDomainTest` | Lifecycle state machine (accept/reject/apply guards) |
| `AiPlanningSuggestionItemDomainTest` | Item accept/reject/apply/skip |
| `AiPlanningRunDomainTest` | Run create/cancel/fail/complete |
| `AiPlanningContextBuilderTest` | Finance/quote masking, redaction summary, no salary/payroll |
| `AiPlanningSafeApplyServiceTest` | Baselined → CR, skip structural, reject cannot apply |
| `AiPlanningEventDefinitionSeedInitializerTest` | Idempotent event seed |

## 9. Release Decision

**Phase 21 core MUST path: COMPLETE** for assisted planning with human-gated apply, convention alignment, and unit-test coverage for critical safety paths. Live LLM generation remains an explicit gap, not a silent claim.

## 10. Follow-ups

- Wire optional AI Agent event-config execution behind feature flag
- Optional Flyway rename `ai_planning_*` → `aiplanning_*` if team wants strict prefix alignment
- Broaden §22 integration tests when direct domain apply adapters exist
