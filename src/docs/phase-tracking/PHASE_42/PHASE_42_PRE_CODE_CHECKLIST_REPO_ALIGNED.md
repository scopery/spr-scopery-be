# Phase 42 — Repository-Aligned Pre-Code Checklist

```text
[ ] Read CLAUDE.md / CLAUDE.ms and Coding_convention.md.
[ ] Confirm Phase 41 V95/V96 and `knowledge.search` implementation/contract status.
[ ] Confirm V97/V98 are free; if occupied, atomically renumber both and update docs.
[ ] Inspect current `modules/aiagent` AiTool registry, definition, execute logging, and NO_OP/stub path.
[ ] Identify the existing AiTool handler interface or add one without creating a second registry.
[ ] Confirm exact location for KnowledgeSearchAiToolHandler under modules/knowledge convention.
[ ] Confirm Spring MVC is active and WebFlux is not required.
[ ] Inspect existing TaskExecutor/ScheduledExecutor conventions before creating aiAssistantExecutor.
[ ] Inspect ErrorResponse fields and AppException factories.
[ ] Inspect controller success-wrapper and pagination/cursor conventions.
[ ] Inspect current idempotency mechanism; choose reuse or migration amendment.
[ ] Inspect IAM permission initializer interface and one working module initializer.
[ ] Inspect EventDefinition initializer interface and one working module initializer.
[ ] Inspect Phase 38 retention/legal-hold APIs/events.
[ ] Inspect workspace/project membership and permission-change events.
[ ] Inspect route/page/action/QuickAction metadata services for Context Builder integration.
[ ] Inspect server-side disabled-action reason services for TASK and other supported entities.
[ ] Apply V97 on clean database and upgrade database.
[ ] Apply V98 on clean database and upgrade database.
[ ] Implement modules/aiassistant package using repository convention.
[ ] Implement conversation/message/context/citation/tool/memory/guide/feedback repositories.
[ ] Implement exact REST DTOs and validation.
[ ] Implement Spring MVC SseEmitter stream service and durable replay.
[ ] Implement message state machine and cancel race handling.
[ ] Implement registry-only knowledge.search execution.
[ ] Replace/extend stub registry execution with one real read-only handler.
[ ] Implement server-authoritative Context Builder and deterministic ctx:v1 hash.
[ ] Implement quota reservation/finalization and active-stream limits.
[ ] Implement memory summarization triggers/invalidation.
[ ] Implement citation validation before persistence/emission.
[ ] Implement AiAssistantPermissionInitializer.
[ ] Implement AiAssistantEventDefinitionInitializer.
[ ] Add event consumers for IAM/source/retention invalidation.
[ ] Add unit/integration/controller/SSE/reconnect/cancel/security tests.
[ ] Verify normal message API excludes TOOL_REQUEST/TOOL_RESULT.
[ ] Verify no prompt, raw source, ACL tokens, vectors, secrets, or chain-of-thought leak.
[ ] Verify Phase 42 cannot execute mutation tools.
[ ] Run mvn compile.
[ ] Run mvn test.
[ ] Create docs/phase-complete/PHASE_42_CONTEXTUAL_AI_CHAT_GUIDANCE_TO_BE_COMPLETE.md with evidence.
```

## Hard stop conditions

Do not begin broad implementation when any item is unresolved:

```text
- tool registry execution interface cannot dispatch a live handler;
- Phase 41 knowledge.search contract is absent or incompatible;
- actual web stack is unknown;
- V97/V98 conflict is unresolved;
- current ErrorResponse/AppException convention is unknown;
- Context Builder has no server-side page/entity/IAM source;
- retention/legal-hold integration is unknown.
```
