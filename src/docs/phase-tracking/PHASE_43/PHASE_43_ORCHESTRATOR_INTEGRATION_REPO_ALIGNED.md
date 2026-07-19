# Phase 43 — Integration with Phase 41 Retrieval and Phase 42 Assistant — Repository-Aligned

> Status: **Accepted / implementation-blocking**

---

# 1. Orchestrator

```text
RecommendationRunOrchestrator
```

Input:

```text
workspace/project/actor
policy + pack codes
trigger type
idempotency key
optional Phase 42 conversation/message/turn references
```

Flow:

```text
Create/return idempotent run
→ resolve project authorization
→ resolve ACTIVE policy/packs/detectors
→ execute deterministic source detectors
→ when evidence enrichment is needed invoke AiTool registry tool knowledge.search
→ normalize candidates through schema registry
→ resolve target version token and authorization
→ validate/attach evidence
→ compute confidence/impact
→ dedup/suppression/cooldown/staleness decision
→ persist suggestion graph + outbox atomically
→ complete run
```

---

# 2. AiTool contracts

## Phase 43 generation tool

```text
toolCode: recommendation.generate
version: 1
mode: READ_ONLY
handler: RecommendationGenerateAiToolHandler
owner: modules/airecommendation
```

Input:

```json
{
  "projectId": "uuid",
  "policyCode": "PROJECT_RECOMMENDATION_MVP_V1",
  "packCodes": ["TASK_PLANNING_HYGIENE_V1"],
  "idempotencyKey": "string"
}
```

Output:

```json
{
  "runId": "uuid",
  "status": "PENDING",
  "suggestionRefs": []
}
```

The handler takes workspace/actor/project access from authenticated execution context; tool arguments cannot grant scope.

## Required retrieval tool

```text
toolCode: knowledge.search
version: active Phase 41 version
handler: KnowledgeSearchAiToolHandler
```

Phase 43 must invoke it through the existing registry/executor. Direct service/repository/Elasticsearch calls are forbidden.

---

# 3. Evidence mapping

`knowledge.search` result → `ai_recommendation_evidence`:

| Tool field | Evidence field |
|---|---|
| retrieval trace ID | `retrieval_trace_id` |
| chunk ID | `knowledge_chunk_id` |
| source type/ref/version | same named fields |
| title/heading/route | bounded display fields |
| permission signature | same opaque signature |
| access validation | must be ALLOWED/REDACTED |

Rules:

```text
- Attach only evidence validated for the run actor.
- Revalidate on later read/review.
- Do not copy full chunks.
- quoted_fragment maximum 2,000 characters and only when permitted.
- direct detector facts may use evidence_type=DOMAIN_FACT with a bounded fact label.
```

---

# 4. Phase 42 linkage

When triggered from chat, all three IDs are required:

```text
originConversationId
originMessageId
originTurnId
```

Validation:

```text
- conversation/message belongs to actor/workspace/project;
- message is a user/assistant turn allowed to request suggestions;
- current source access is rebuilt; old chat context cannot grant access;
- suggestion stores IDs only, not chat content;
- Phase 42 answer may display resulting suggestionRefs.
```

When manually triggered outside chat, all three fields are null.

---

# 5. Citation reuse

Phase 43 may reference an `aiassistant_message_citation.id` only when the originating chat turn exists and current access remains valid. It still stores source/chunk identifiers needed for independent revalidation.

No foreign key is created because:

```text
cross-module retention may delete/redact chat records;
suggestions remain independently reviewable;
current source access is authoritative.
```

---

# 6. Context and access rules

```text
- Client-provided project/entity context is a hint only.
- Server resolves workspace/project membership and source fields.
- AI_ASSISTANT_USE or AI_RECOMMENDATION_GENERATE never grants source access.
- A suggestion can be listed only when the viewer can access its project and target summary.
- Evidence is filtered independently.
- Finance/sensitive evidence uses actual domain field masking.
```

---

# 7. LLM boundary

MVP deterministic detectors do not require an LLM. Optional enrichment:

```text
feature flag: scopery.ai.recommendation.llm-enrichment-enabled=false
```

When enabled later:

```text
- use existing LlmProvider abstraction;
- only bounded authorized evidence enters prompt;
- output must validate against registered schema;
- LLM cannot choose target scope or permissions;
- LLM explanation/confidence is never stored as chain-of-thought;
- failures fall back to deterministic explanation where possible.
```

---

# 8. Required tests

```text
orchestrator_callsKnowledgeSearchThroughAiToolRegistry
orchestrator_doesNotCallKnowledgeRepositoryDirectly
clientProjectId_cannotOverrideAuthenticatedScope
chatOrigin_allIdsRequiredTogether
chatOrigin_revalidatedAgainstCurrentAccess
citationReference_missingAfterRetention_doesNotBreakSuggestion
restrictedChunk_notPersistedAsEvidence
llmDisabled_deterministicRunStillSucceeds
partialKnowledgeSearchFailure_marksRunPartialOrUsesDirectFact
```
