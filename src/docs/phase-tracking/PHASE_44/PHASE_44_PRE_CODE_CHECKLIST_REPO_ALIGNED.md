# Phase 44 — Pre-Code Checklist — Repository-Aligned

> Every item must be checked against the actual repository before writing production code.

## Repository and migration

- [ ] Confirm repository migration history ends at `V123` and `V124`/`V125` are the next free Flyway versions; renumber atomically if later migrations already exist.
- [ ] Confirm no existing `ai_action_*` tables/classes conflict.
- [ ] Inspect UUID/audit/version column conventions and update SQL only where required by real repository standards.
- [ ] Run Flyway against a clean database and an upgraded representative database.

## Phase 41 retrieval baseline

- [ ] Confirm `V123__migrate_knowledge_chunk_to_pgvector.sql` is applied successfully.
- [ ] Confirm `knowledge_chunk.embedding` is `vector(1536)` and `search_vector` is `tsvector`.
- [ ] Confirm HNSW and GIN indexes exist and retrieval tests pass.
- [ ] Confirm the live `knowledge.search` handler resolves to the PostgreSQL/pgvector implementation.
- [ ] Confirm `HybridRetrievalService` uses `NamedParameterJdbcTemplate` and Java RRF as repository truth.
- [ ] Confirm `PostgresKnowledgeIndexService` and `KnowledgeSourceIndexingService` own index updates.
- [ ] Verify Phase 44 has no direct dependency on repositories, index services, pgvector SQL, or `knowledge_chunk`.
- [ ] Verify no Elasticsearch dependency, config, container, volume, mapping, or documentation remains in the Phase 44 change.

## Existing AI modules

- [ ] Inspect real `modules/aiagent` AiTool registry entities, handler interfaces, execution logging, and initializer pattern.
- [ ] Confirm no second registry is introduced.
- [ ] Inspect Phase 42 `aiassistant_tool_call` integration for `agent.action.prepare/status` transcript links.
- [ ] Inspect Phase 43 `RecommendationApplyPreparationPort` and p43/p21 reference parsing.

## Domain action adapters

- [ ] Identify exact compile-safe Application Action/Command for each ACTIVE MVP tool.
- [ ] Confirm adapter never injects repository/EntityManager.
- [ ] Confirm real domain IAM and business validation execute.
- [ ] Confirm expected-version mapping for each target.
- [ ] Leave tool INACTIVE when no safe real action exists.

## Security and policy

- [ ] Map Phase 44 authority codes using real initializer/role constants.
- [ ] Verify every endpoint/STOMP destination revalidates workspace/project/target access.
- [ ] Confirm direct LLM mutation tool execution is blocked.
- [ ] Confirm forbidden action list is enforced before planning and execution.
- [ ] Confirm plan/preview/confirmation hashes and TTLs.
- [ ] Confirm sensitive-field masking uses existing Phase 38 mechanism or fails closed.

## Execution durability

- [ ] Implement worker claim/lease with PostgreSQL authoritative state.
- [ ] Implement request, execution, and step idempotency conflicts.
- [ ] Verify transaction boundary per domain step.
- [ ] Implement crash recovery and unpublished event retry.
- [ ] Verify concurrent execution limit transactionally.

## WebSocket/Redis

- [ ] Add/reuse `spring-boot-starter-websocket`; do not migrate to WebFlux.
- [ ] Configure STOMP endpoint/prefixes and handshake authentication.
- [ ] Add subscription and SEND authorization interceptor.
- [ ] Persist event before Redis/WebSocket publication.
- [ ] Implement REST replay and sequence de-duplication.
- [ ] Test Redis outage does not lose execution state.

## Compatibility

- [ ] p43 accepted/edited non-stale suggestion creates a real plan.
- [ ] missing manual Phase 43 payload fields return controlled 422.
- [ ] p21 payload requires explicit mapper; generic reflection is absent.
- [ ] legacy Phase 21 apply endpoint remains unchanged.

## Required test suites

- [ ] Unit tests for hash canonicalization, risk aggregation, state machines, schema validation.
- [ ] Integration tests for each active adapter and idempotency.
- [ ] Security tests for cross-tenant, target permissions, STOMP subscription/commands.
- [ ] Recovery tests for worker crash, Redis loss, duplicate execute, event replay.
- [ ] Compensation tests proving best-effort and no fake global rollback.
- [ ] `mvn test` and repository architecture/convention tests pass.
