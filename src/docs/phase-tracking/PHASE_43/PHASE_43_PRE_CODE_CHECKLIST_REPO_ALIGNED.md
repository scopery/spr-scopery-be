# Phase 43 — Pre-Code Checklist — Repository-Aligned

> Completion of every blocking item is required before implementation is claimed.

## A. Repository verification

```text
[ ] Read CLAUDE.md / Coding_convention.md.
[ ] Confirm latest Flyway migration and whether V99/V100 are free.
[ ] If occupied, renumber both migrations and all doc references atomically.
[ ] Inspect modules/aiplanning entities/actions/APIs/tests and exact ai_planning_* columns.
[ ] Inspect modules/aiassistant Phase 42 IDs/contracts after implementation.
[ ] Confirm knowledge.search is registered and has a live handler.
[ ] Inspect AiTool registry execution API and logging behavior.
[ ] Inspect ErrorResponse/AppException exact fields.
[ ] Inspect existing page envelope/sort parameter conventions.
[ ] Inspect IAM/Event initializer interfaces and role constants.
[ ] Inspect Work Inbox publisher/adapter if present.
```

## B. Package and persistence

```text
[ ] Create modules/airecommendation using repository feature/http/application/domain/infrastructure pattern.
[ ] Do not create modules/copilot, modules/assistant, or a second registry.
[ ] Apply V99/V100 on clean DB.
[ ] Apply all migrations on a DB upgraded from current baseline.
[ ] Verify every internal FK/index/check constraint.
[ ] Verify no FK is added to ai_planning/aiassistant/knowledge/source module tables.
[ ] Verify ACTIVE schema/pack/detector registry versions are immutable.
```

## C. Phase 21 compatibility

```text
[ ] Implement p43:/p21: reference parser.
[ ] Implement Phase21SuggestionCompatibilityQueryService.
[ ] Map confidence labels deterministically.
[ ] Revalidate legacy source references before exposure.
[ ] Preserve legacy API and legacy-only apply behavior.
[ ] Prove Phase 43 accept never invokes Phase 21 apply.
[ ] Do not dual-write.
```

## D. Generation and retrieval

```text
[ ] Register recommendation.generate read-only tool.
[ ] Invoke knowledge.search only through AiTool registry.
[ ] Implement five deterministic detectors.
[ ] Disable LLM enrichment by default.
[ ] Implement PARTIAL run handling.
[ ] Persist bounded evidence and no full chunks.
```

## E. Schema/confidence/impact

```text
[ ] Seed six schema definitions.
[ ] Validate every item before persistence/edit.
[ ] Canonicalize and hash payloads.
[ ] Enforce 0.4 persistence threshold and label bands.
[ ] Enforce numeric impact source/calculation constraints.
[ ] Mask sensitive fields before persistence/API/events.
```

## F. Dedup/suppression/staleness

```text
[ ] Implement atomic active dedup handling.
[ ] Test unique-constraint race.
[ ] Implement actor-scoped suppression max 90 days.
[ ] Implement cooldown defaults.
[ ] Implement hourly expiry job.
[ ] Implement target version tokens and revalidation.
[ ] Implement evidence access revalidation.
[ ] Implement supersession without loops.
```

## G. API/IAM/events

```text
[ ] Implement every endpoint/request/response in API contract.
[ ] Use exact repository ErrorResponse.
[ ] Seed rights/events idempotently.
[ ] Ensure recommendation rights never grant target access.
[ ] Emit outbox events only after durable state.
[ ] Do not emit raw payload/evidence/prompt content.
[ ] Reuse Work Inbox adapter; no duplicate table.
```

## H. Phase 44 boundary

```text
[ ] Default RecommendationApplyPreparationPort adapter returns controlled 409.
[ ] Do not create fake plans/tokens.
[ ] Keep accepted suggestion unchanged when unavailable.
[ ] Reserve `agent.action.prepare` v1 only as inactive handoff metadata.
```

## I. Build/test gates

```text
[ ] mvn compile
[ ] mvn test
[ ] migration clean-install test
[ ] API integration tests
[ ] IAM isolation tests
[ ] Phase 21 backward-compatibility tests
[ ] no-target-mutation test suite
[ ] update PHASE_43_COMPLETED.md from completion template
```
