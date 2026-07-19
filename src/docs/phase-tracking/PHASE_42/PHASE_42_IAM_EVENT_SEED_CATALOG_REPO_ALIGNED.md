# Phase 42 — IAM and Event Registry Seed Catalog — Repository-Aligned

> Status: **Accepted / implementation-blocking**  
> Mechanism: existing idempotent initializer/seeder APIs  
> Do not invent IAM/Event table names or raw SQL without inspecting repository schema.

---

# 1. Initializer classes

```text
AiAssistantPermissionInitializer
AiAssistantEventDefinitionInitializer
```

Use the same interfaces, ordering, transaction behavior, and idempotency pattern as existing module initializers.

Phase 42 does not duplicate the Phase 41 `knowledge.search` tool definition. The tool remains Knowledge-owned and is only verified/consumed here.

---

# 2. Authority codes

| Authority | Purpose | Resource scope |
|---|---|---|
| `AI_ASSISTANT_USE` | create conversations and submit read-only questions | workspace |
| `AI_ASSISTANT_PROJECT_USE` | use assistant in an accessible project | project |
| `AI_ASSISTANT_CONVERSATION_VIEW` | list/read owned conversations and messages | workspace/project |
| `AI_ASSISTANT_CONVERSATION_MANAGE` | rename/archive/delete owned conversations | workspace/project |
| `AI_ASSISTANT_GUIDE_USE` | explain page/field/action and suggested questions | workspace |
| `AI_ASSISTANT_TRACEABILITY_USE` | ask grounded project traceability questions | project |
| `AI_ASSISTANT_FEEDBACK_CREATE` | submit answer feedback | workspace/project |
| `AI_ASSISTANT_ADMIN_VIEW` | view redacted operational/debug metadata | workspace/system admin |
| `AI_ASSISTANT_PROMPT_MANAGE` | manage prompt/guide policy in later governance scope | system admin |

Authorization rules:

```text
- AI_ASSISTANT_* never grants access to a Task, Document, Meeting, Project, or field.
- Underlying source permissions and masking are evaluated on every turn.
- Conversation owner scope does not preserve access after project/IAM revocation.
- Admin view does not expose raw prompts, secrets, chain-of-thought, embeddings, or unrestricted source text.
- AI_ASSISTANT_PROMPT_MANAGE is seeded for stable catalog compatibility but Phase 42 does not implement prompt-management endpoints.
```

Default role grants must use actual repository role constants and central role-mapping conventions. Do not invent literal role codes. Minimum semantic mapping:

```text
ordinary authenticated workspace member → USE, CONVERSATION_VIEW, CONVERSATION_MANAGE, GUIDE_USE, FEEDBACK_CREATE
project participant with source access → PROJECT_USE, TRACEABILITY_USE
workspace/system administrative role → ADMIN_VIEW as allowed by current role model
system administrative role only → PROMPT_MANAGE
```

---

# 3. Source system

```text
SCOPERY_AI_ASSISTANT
```

---

# 4. Event definitions

| Code | eventKey | Required variables |
|---|---|---|
| `AI_CONVERSATION_CREATED` | `ai_assistant.conversation_created` | actor.userId, workspace.id, project.id, conversation.id, conversation.type, capability.level, occurredAt, traceId |
| `AI_CONVERSATION_ARCHIVED` | `ai_assistant.conversation_archived` | actor.userId, workspace.id, project.id, conversation.id, occurredAt, traceId |
| `AI_CONVERSATION_DELETED` | `ai_assistant.conversation_deleted` | actor.userId, workspace.id, project.id, conversation.id, retention.policyCode, occurredAt, traceId |
| `AI_MESSAGE_REQUESTED` | `ai_assistant.message_requested` | actor.userId, workspace.id, project.id, conversation.id, message.id, turn.id, response.modeHint, occurredAt, traceId |
| `AI_MESSAGE_STARTED` | `ai_assistant.message_started` | workspace.id, project.id, conversation.id, message.id, turn.id, occurredAt, traceId |
| `AI_MESSAGE_COMPLETED` | `ai_assistant.message_completed` | workspace.id, project.id, conversation.id, message.id, turn.id, response.mode, citation.count, input.tokens, output.tokens, latency.ms, occurredAt, traceId |
| `AI_MESSAGE_FAILED` | `ai_assistant.message_failed` | workspace.id, project.id, conversation.id, message.id, turn.id, error.code, retryable, occurredAt, traceId |
| `AI_MESSAGE_CANCELLED` | `ai_assistant.message_cancelled` | actor.userId, workspace.id, project.id, conversation.id, message.id, turn.id, reason.code, occurredAt, traceId |
| `AI_MESSAGE_BLOCKED` | `ai_assistant.message_blocked` | workspace.id, project.id, conversation.id, message.id, turn.id, policy.code, occurredAt, traceId |
| `AI_TOOL_CALL_STARTED` | `ai_assistant.tool_call_started` | workspace.id, project.id, conversation.id, message.id, turn.id, toolCall.id, tool.code, occurredAt, traceId |
| `AI_TOOL_CALL_COMPLETED` | `ai_assistant.tool_call_completed` | workspace.id, project.id, conversation.id, message.id, turn.id, toolCall.id, tool.code, result.count, truncated, latency.ms, occurredAt, traceId |
| `AI_TOOL_CALL_FAILED` | `ai_assistant.tool_call_failed` | workspace.id, project.id, conversation.id, message.id, turn.id, toolCall.id, tool.code, error.code, occurredAt, traceId |
| `AI_TOOL_CALL_BLOCKED` | `ai_assistant.tool_call_blocked` | workspace.id, project.id, conversation.id, message.id, turn.id, toolCall.id, tool.code, policy.code, occurredAt, traceId |
| `AI_ANSWER_CITATIONS_ATTACHED` | `ai_assistant.answer_citations_attached` | workspace.id, project.id, conversation.id, message.id, turn.id, citation.count, occurredAt, traceId |
| `AI_ANSWER_FEEDBACK_SUBMITTED` | `ai_assistant.answer_feedback_submitted` | actor.userId, workspace.id, project.id, conversation.id, message.id, feedback.id, rating, reason.code, occurredAt, traceId |
| `AI_CONTEXT_ACCESS_REVALIDATED` | `ai_assistant.context_access_revalidated` | actor.userId, workspace.id, project.id, conversation.id, message.id, context.id, context.status, occurredAt, traceId |
| `AI_CONTEXT_REDACTED` | `ai_assistant.context_redacted` | workspace.id, project.id, conversation.id, message.id, context.id, reason.code, occurredAt, traceId |
| `AI_MEMORY_SUMMARY_CREATED` | `ai_assistant.memory_summary_created` | workspace.id, project.id, conversation.id, summary.id, summary.version, coveredThrough.sequence, sourceMessage.count, occurredAt, traceId |
| `AI_MEMORY_SUMMARY_INVALIDATED` | `ai_assistant.memory_summary_invalidated` | workspace.id, project.id, conversation.id, summary.id, summary.version, reason.code, occurredAt, traceId |
| `AI_GUIDE_RESPONSE_GENERATED` | `ai_assistant.guide_response_generated` | actor.userId, workspace.id, project.id, page.code, field.code, action.code, guideDefinition.count, occurredAt, traceId |

Event emission rules:

```text
- Conversation lifecycle events use the outbox when consumed cross-module.
- Do not emit one business event for every SSE delta or heartbeat.
- Tool start/complete/fail events are operationally bounded and may be emitted only once per tool call.
- Message completion event is emitted only after final message/citations are committed.
```

---

# 5. Forbidden event variables

Never include:

```text
raw user prompt
raw assistant answer
raw document/chunk text
quoted citation fragment
provider request/response
provider API key/secret
presigned URL
ACL token list
embedding vector
private chain-of-thought
full stack trace
SSE delta text
```

Safe identifiers, counts, status codes, hashes, and redacted error codes are allowed.

---

# 6. Existing events consumed for invalidation

The implementation must map actual repository codes rather than inventing duplicates. Required semantic inputs:

## IAM/workspace/project access

```text
workspace member removed/suspended
workspace role/authority changed
project member removed
project authorization changed
field/classification access changed
```

Effect:

```text
invalidate context snapshots and active memory summaries for affected actor/scope;
old conversation can remain listed only if current access still permits it;
new turns always rebuild context.
```

## Knowledge/source lifecycle

Consume Phase 41 semantics:

```text
KNOWLEDGE_SOURCE_INVALIDATED
KNOWLEDGE_SOURCE_INDEXED
```

Effect:

```text
citations are revalidated when read;
memory summaries referencing inaccessible sources become STALE;
no automatic rewrite of historical user-visible answer content.
```

## Retention/privacy

Consume actual Phase 38 deletion/redaction/legal-hold events.

Effect:

```text
redact/purge covered message/context/tool data as required;
invalidate affected memory summaries;
preserve only permitted audit identifiers.
```

---

# 7. Required initializer tests

```text
aiAssistantPermissionInitializer_firstRun_createsAuthorities
aiAssistantPermissionInitializer_secondRun_noDuplicates
aiAssistantEventInitializer_firstRun_createsDefinitionsAndVariables
aiAssistantEventInitializer_secondRun_noDuplicates
aiAssistantEvents_haveStableSourceSystemAndEventKey
aiAssistantEvents_doNotExposePromptContentOrSecrets
aiAssistantPromptManage_notGrantedToOrdinaryRole
aiAssistantPermissions_doNotBypassUnderlyingSourceAccess
