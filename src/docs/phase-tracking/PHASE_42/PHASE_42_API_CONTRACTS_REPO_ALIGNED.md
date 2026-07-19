# Phase 42 — Repository-Aligned REST API Contracts

> Status: **Accepted / implementation-blocking**  
> API base: `/api/ai-assistant`  
> Error handling: existing repository `ErrorResponse` / `AppException`  
> Streaming: see `PHASE_42_SSE_STREAMING_CONTRACT_REPO_ALIGNED.md`

---

# 1. Common rules

```text
Controller → Request DTO → Command/QueryService → Response DTO
```

Do not return JPA/domain entities. Preserve any existing global success wrapper. Errors must use the current `ErrorResponse` fields; do not create `{code,message}` as a second envelope.

Representative error only; exact common fields must match the repository class:

```json
{
  "success": false,
  "errorCode": "AI_CONVERSATION_ACCESS_DENIED",
  "message": "You do not have access to this conversation.",
  "traceId": "existing-trace-id-field-if-present"
}
```

Common validation:

| Field | Rule |
|---|---|
| UUIDs | valid UUID |
| title | trimmed, 1–200 chars |
| user message | trimmed, 1–8,000 chars |
| locale | BCP-47-like, max 20 chars |
| timezone | valid IANA zone, max 80 chars |
| page size | default 20, min 1, max 100 |
| cursor | opaque server-generated string |

---

# 2. Common DTOs

## 2.1 Client context hint

```json
{
  "route": "/workspaces/{workspaceId}/projects/{projectId}/tasks/{taskId}",
  "pageCode": "PROJECT_TASK_DETAIL",
  "entity": {
    "type": "TASK",
    "id": "5b27ef76-2362-49c4-ac69-993c0a7392ea"
  },
  "selectedActionCode": "TASK_START",
  "tabCode": "DEPENDENCIES",
  "locale": "vi-VN",
  "timezone": "Asia/Ho_Chi_Minh",
  "visibleFieldCodes": ["TASK_STATUS", "TASK_BLOCKER"],
  "availableActionCodes": ["TASK_EDIT", "TASK_START"],
  "clientContextVersion": 1,
  "clientContextHash": "ctx:v1:sha256:0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef"
}
```

All access-sensitive fields are hints. Server resolution overrides them.

## 2.2 Conversation summary

```json
{
  "id": "uuid",
  "workspaceId": "uuid",
  "projectId": "uuid-or-null",
  "conversationType": "PROJECT_ASSISTANT",
  "capabilityLevel": "CONTEXTUAL_ANSWER",
  "status": "ACTIVE",
  "title": "API integration blocker",
  "lastMessageAt": "2026-07-17T08:30:00Z",
  "createdAt": "2026-07-17T08:00:00Z",
  "updatedAt": "2026-07-17T08:30:00Z"
}
```

## 2.3 Message response

```json
{
  "id": "uuid",
  "conversationId": "uuid",
  "turnId": "uuid",
  "parentMessageId": "uuid-or-null",
  "sequence": 4,
  "role": "ASSISTANT",
  "status": "COMPLETED",
  "contentFormat": "MARKDOWN",
  "content": "Task **API Integration** is blocked by Authentication API.",
  "responseMode": "GROUNDED_ANSWER",
  "citations": [
    {
      "id": "uuid",
      "ordinal": 1,
      "sourceType": "TASK",
      "sourceId": "uuid",
      "sourceVersionId": "uuid",
      "knowledgeChunkId": "uuid",
      "title": "API Integration",
      "headingPath": ["Blocker"],
      "fragment": "Task is waiting for Authentication API.",
      "appRoute": "/projects/{projectId}/tasks/{taskId}"
    }
  ],
  "finishReason": "STOP",
  "errorCode": null,
  "createdAt": "2026-07-17T08:30:00Z",
  "completedAt": "2026-07-17T08:30:02Z"
}
```

Provider payload, tool arguments, raw ACL tokens, vectors, and chain-of-thought are never returned.

---

# 3. Create conversation

## Endpoint

`POST /api/ai-assistant/conversations`

## Permission

```text
AI_ASSISTANT_USE
+ AI_ASSISTANT_PROJECT_USE when projectId is present
```

## Request

```json
{
  "conversationType": "PROJECT_ASSISTANT",
  "projectId": "b3fc8fb0-c0ef-4c28-b976-d76f6efcc86e",
  "capabilityLevel": "CONTEXTUAL_ANSWER",
  "title": null,
  "context": {
    "route": "/projects/b3fc8fb0-c0ef-4c28-b976-d76f6efcc86e",
    "pageCode": "PROJECT_OVERVIEW",
    "entity": {"type": "PROJECT", "id": "b3fc8fb0-c0ef-4c28-b976-d76f6efcc86e"},
    "selectedActionCode": null,
    "tabCode": "SUMMARY",
    "locale": "vi-VN",
    "timezone": "Asia/Ho_Chi_Minh",
    "visibleFieldCodes": [],
    "availableActionCodes": [],
    "clientContextVersion": 1,
    "clientContextHash": null
  }
}
```

Validation:

```text
GENERAL_GUIDE → projectId optional
PROJECT_ASSISTANT → projectId required and accessible
capabilityLevel must be GUIDE or CONTEXTUAL_ANSWER
```

## Response `201`

Conversation summary DTO.

---

# 4. List conversations

`GET /api/ai-assistant/conversations?projectId={uuid}&status=ACTIVE&pageSize=20&cursor={cursor}`

Permission: `AI_ASSISTANT_CONVERSATION_VIEW`.

Response `200`:

```json
{
  "items": [
    {
      "id": "uuid",
      "workspaceId": "uuid",
      "projectId": "uuid",
      "conversationType": "PROJECT_ASSISTANT",
      "capabilityLevel": "CONTEXTUAL_ANSWER",
      "status": "ACTIVE",
      "title": "API integration blocker",
      "lastMessageAt": "2026-07-17T08:30:00Z",
      "createdAt": "2026-07-17T08:00:00Z",
      "updatedAt": "2026-07-17T08:30:00Z"
    }
  ],
  "nextCursor": null
}
```

Only conversations owned by the actor are returned in Phase 42 unless an existing administrative right explicitly permits broader access.

---

# 5. Get conversation

`GET /api/ai-assistant/conversations/{conversationId}`

Permission: `AI_ASSISTANT_CONVERSATION_VIEW` plus current workspace/project access.

Response `200`:

```json
{
  "conversation": {
    "id": "uuid",
    "workspaceId": "uuid",
    "projectId": "uuid",
    "conversationType": "PROJECT_ASSISTANT",
    "capabilityLevel": "CONTEXTUAL_ANSWER",
    "status": "ACTIVE",
    "title": "API integration blocker",
    "lastMessageAt": "2026-07-17T08:30:00Z",
    "createdAt": "2026-07-17T08:00:00Z",
    "updatedAt": "2026-07-17T08:30:00Z"
  },
  "memory": {
    "status": "ACTIVE",
    "summaryVersion": 2,
    "coveredThroughSequence": 32,
    "summaryText": "The user is investigating API integration blockers.",
    "updatedAt": "2026-07-17T08:20:00Z"
  },
  "quota": {
    "dailyTurnLimit": 200,
    "dailyTurnsUsed": 18,
    "dailyTokenLimit": 500000,
    "dailyTokensUsed": 42100
  }
}
```

A stale/redacted memory summary is not returned as active context.

---

# 6. Update conversation

`PATCH /api/ai-assistant/conversations/{conversationId}`

Permission: `AI_ASSISTANT_CONVERSATION_MANAGE`.

Request:

```json
{
  "title": "Authentication dependency analysis"
}
```

Response `200`: conversation summary DTO.

Project scope, owner, capability level, and type cannot be changed by this endpoint.

---

# 7. Archive conversation

`POST /api/ai-assistant/conversations/{conversationId}/archive`

Permission: `AI_ASSISTANT_CONVERSATION_MANAGE`.

Request body: empty.

Response `200`:

```json
{
  "conversationId": "uuid",
  "status": "ARCHIVED",
  "archivedAt": "2026-07-17T09:00:00Z"
}
```

Archived conversations are read-only and cannot receive new messages.

---

# 8. Delete conversation

`DELETE /api/ai-assistant/conversations/{conversationId}`

Permission: `AI_ASSISTANT_CONVERSATION_MANAGE`.

Response `204`.

Semantics: immediate soft delete, user-visible content unavailable, legal-hold-aware physical purge within the locked retention window.

---

# 9. Submit a user message and start a turn

## Endpoint

`POST /api/ai-assistant/conversations/{conversationId}/messages`

## Permissions

```text
AI_ASSISTANT_USE
AI_ASSISTANT_CONVERSATION_VIEW
AI_ASSISTANT_PROJECT_USE for project conversations
```

## Request

```json
{
  "content": "Vì sao task API Integration đang bị blocked?",
  "idempotencyKey": "client-generated-uuid-or-opaque-key",
  "context": {
    "route": "/projects/{projectId}/tasks/{taskId}",
    "pageCode": "PROJECT_TASK_DETAIL",
    "entity": {"type": "TASK", "id": "5b27ef76-2362-49c4-ac69-993c0a7392ea"},
    "selectedActionCode": "TASK_START",
    "tabCode": "DEPENDENCIES",
    "locale": "vi-VN",
    "timezone": "Asia/Ho_Chi_Minh",
    "visibleFieldCodes": ["TASK_STATUS", "TASK_BLOCKER"],
    "availableActionCodes": ["TASK_EDIT"],
    "clientContextVersion": 1,
    "clientContextHash": null
  }
}
```

Validation:

```text
content: 1–8,000 chars
idempotencyKey: required, 1–200 chars
conversation must be ACTIVE
message count < 500
quota must be available
only one duplicate response per idempotency key
```

## Response `202`

```json
{
  "conversationId": "uuid",
  "turnId": "uuid",
  "userMessageId": "uuid",
  "assistantMessageId": "uuid",
  "assistantStatus": "QUEUED",
  "streamUrl": "/api/ai-assistant/messages/{assistantMessageId}/stream",
  "messageUrl": "/api/ai-assistant/messages/{assistantMessageId}",
  "cancelUrl": "/api/ai-assistant/messages/{assistantMessageId}/cancel"
}
```

The request must persist the USER message and ASSISTANT placeholder before returning `202`.

---

# 10. List conversation messages

`GET /api/ai-assistant/conversations/{conversationId}/messages?pageSize=50&cursor={cursor}`

Permission: `AI_ASSISTANT_CONVERSATION_VIEW` plus current source access revalidation.

Response `200`:

```json
{
  "items": [
    {
      "id": "uuid",
      "conversationId": "uuid",
      "turnId": "uuid",
      "sequence": 1,
      "role": "USER",
      "status": "COMPLETED",
      "contentFormat": "PLAIN_TEXT",
      "content": "Vì sao task này bị blocked?",
      "responseMode": null,
      "citations": [],
      "finishReason": null,
      "errorCode": null,
      "createdAt": "2026-07-17T08:30:00Z",
      "completedAt": "2026-07-17T08:30:00Z"
    }
  ],
  "nextCursor": null
}
```

`TOOL_REQUEST` and `TOOL_RESULT` are not included in normal end-user history. Administrative trace endpoints are deferred to governance/admin scope.

---

# 11. Get one message

`GET /api/ai-assistant/messages/{messageId}`

Permission: `AI_ASSISTANT_CONVERSATION_VIEW`.

Response `200`: Message response DTO from §2.3.

For non-final messages, `content` may be the durable accumulated partial answer and status reflects current state.

---

# 12. SSE stream

`GET /api/ai-assistant/messages/{messageId}/stream?afterSequence=17`

Headers:

```text
Accept: text/event-stream
Last-Event-ID: 17
```

Permission: `AI_ASSISTANT_CONVERSATION_VIEW` and current conversation access.

Response: `text/event-stream`; exact events are locked in the SSE contract.

`Last-Event-ID` wins over `afterSequence` when both are present.

---

# 13. Cancel assistant message

`POST /api/ai-assistant/messages/{messageId}/cancel`

Permission: conversation owner with `AI_ASSISTANT_USE`, or administrative manage permission.

Request:

```json
{
  "reasonCode": "USER_REQUESTED"
}
```

Response `202`:

```json
{
  "messageId": "uuid",
  "status": "CANCEL_REQUESTED",
  "cancelRequestedAt": "2026-07-17T08:30:01Z"
}
```

Idempotency:

```text
- final CANCELLED → return current final state
- final COMPLETED/FAILED/BLOCKED → return 409 AI_MESSAGE_ALREADY_FINAL
- duplicate cancel while CANCEL_REQUESTED → return same response
```

---

# 14. Explain page

`POST /api/ai-assistant/explain-page`

Permission: `AI_ASSISTANT_GUIDE_USE`.

Request:

```json
{
  "question": "Trang này dùng để làm gì?",
  "context": {
    "route": "/projects/{projectId}/tasks",
    "pageCode": "PROJECT_TASK_LIST",
    "entity": {"type": "PROJECT", "id": "uuid"},
    "selectedActionCode": null,
    "tabCode": null,
    "locale": "vi-VN",
    "timezone": "Asia/Ho_Chi_Minh",
    "visibleFieldCodes": [],
    "availableActionCodes": [],
    "clientContextVersion": 1,
    "clientContextHash": null
  }
}
```

Response `200`:

```json
{
  "responseMode": "CURRENT_PAGE_EXPLANATION",
  "answerMarkdown": "Trang **Tasks** dùng để quản lý công việc của dự án...",
  "guideDefinitionCodes": ["GUIDE_PROJECT_TASK_LIST_V1"],
  "citations": [],
  "suggestedQuestions": [
    "Làm sao tạo task mới?",
    "Vì sao một task bị blocked?"
  ]
}
```

No project fact may be asserted without retrieval/citations.

---

# 15. Explain field

`POST /api/ai-assistant/explain-field`

Permission: `AI_ASSISTANT_GUIDE_USE`.

Request:

```json
{
  "fieldCode": "TASK_ESTIMATED_HOURS",
  "question": "Trường này nên nhập gì?",
  "context": {
    "route": "/projects/{projectId}/tasks/{taskId}",
    "pageCode": "PROJECT_TASK_DETAIL",
    "entity": {"type": "TASK", "id": "uuid"},
    "selectedActionCode": null,
    "tabCode": "DETAILS",
    "locale": "vi-VN",
    "timezone": "Asia/Ho_Chi_Minh",
    "visibleFieldCodes": ["TASK_ESTIMATED_HOURS"],
    "availableActionCodes": [],
    "clientContextVersion": 1,
    "clientContextHash": null
  }
}
```

Response `200`:

```json
{
  "responseMode": "FIELD_EXPLANATION",
  "fieldCode": "TASK_ESTIMATED_HOURS",
  "answerMarkdown": "Nhập số giờ dự kiến cần để hoàn thành task...",
  "guideDefinitionCodes": ["GUIDE_TASK_ESTIMATED_HOURS_V1"],
  "citations": []
}
```

The field must be visible to the actor according to server-resolved metadata.

---

# 16. Explain disabled action

`POST /api/ai-assistant/explain-disabled-action`

Permission: `AI_ASSISTANT_GUIDE_USE` and underlying entity view permission.

Request:

```json
{
  "actionCode": "TASK_START",
  "question": "Vì sao tôi không thể bắt đầu task này?",
  "context": {
    "route": "/projects/{projectId}/tasks/{taskId}",
    "pageCode": "PROJECT_TASK_DETAIL",
    "entity": {"type": "TASK", "id": "uuid"},
    "selectedActionCode": "TASK_START",
    "tabCode": "DETAILS",
    "locale": "vi-VN",
    "timezone": "Asia/Ho_Chi_Minh",
    "visibleFieldCodes": [],
    "availableActionCodes": [],
    "clientContextVersion": 1,
    "clientContextHash": null
  }
}
```

Response `200`:

```json
{
  "responseMode": "DISABLED_ACTION_EXPLANATION",
  "actionCode": "TASK_START",
  "disabled": true,
  "reasonCodes": ["TASK_HAS_BLOCKING_DEPENDENCY"],
  "answerMarkdown": "Task chưa thể bắt đầu vì dependency **Authentication API** chưa hoàn thành.",
  "citations": [
    {
      "id": "uuid",
      "ordinal": 1,
      "sourceType": "TASK",
      "sourceId": "uuid",
      "sourceVersionId": "uuid",
      "knowledgeChunkId": "uuid",
      "title": "Authentication API",
      "headingPath": ["Status"],
      "fragment": "Status: IN_PROGRESS",
      "appRoute": "/projects/{projectId}/tasks/{dependencyId}"
    }
  ]
}
```

The disabled state and reason codes must come from server-side business/IAM evaluation, not the client.

---

# 17. Suggested questions

`GET /api/ai-assistant/suggested-questions?pageCode=PROJECT_TASK_DETAIL&entityType=TASK&actionCode=TASK_START&locale=vi-VN`

Permission: `AI_ASSISTANT_GUIDE_USE`.

Response `200`:

```json
{
  "items": [
    {
      "code": "SUGGEST_TASK_WHY_BLOCKED_V1",
      "question": "Vì sao task này đang bị blocked?",
      "displayOrder": 10
    },
    {
      "code": "SUGGEST_TASK_NEXT_STEP_V1",
      "question": "Tôi nên làm gì tiếp theo?",
      "displayOrder": 20
    }
  ]
}
```

Only active definitions matching the server-resolved page/entity context are returned.

---

# 18. Submit answer feedback

`POST /api/ai-assistant/messages/{messageId}/feedback`

Permission: `AI_ASSISTANT_FEEDBACK_CREATE` and conversation access.

Request:

```json
{
  "rating": "DOWN",
  "reasonCode": "INCORRECT_CITATION",
  "comment": "Nguồn trích dẫn không nói về dependency hiện tại."
}
```

Validation:

```text
rating: UP or DOWN
reasonCode: optional known code
comment: optional, max 2,000 chars
one feedback record per actor/message; subsequent request updates it
```

Response `200`:

```json
{
  "feedbackId": "uuid",
  "messageId": "uuid",
  "rating": "DOWN",
  "reasonCode": "INCORRECT_CITATION",
  "updatedAt": "2026-07-17T09:00:00Z"
}
```

---

# 19. HTTP status and error mapping

| Situation | HTTP | Error code |
|---|---:|---|
| conversation not found/invisible | 404 | `AI_CONVERSATION_NOT_FOUND` |
| conversation access denied | 403 | `AI_CONVERSATION_ACCESS_DENIED` |
| archived/deleted conversation receives message | 409 | `AI_CONVERSATION_INVALID_STATUS` |
| project mismatch | 409 | `AI_CONVERSATION_PROJECT_SCOPE_MISMATCH` |
| message not found | 404 | `AI_MESSAGE_NOT_FOUND` |
| message already final on cancel | 409 | `AI_MESSAGE_ALREADY_FINAL` |
| page metadata unknown | 422 | `AI_CONTEXT_PAGE_UNKNOWN` |
| entity missing/invisible | 404 | `AI_CONTEXT_ENTITY_NOT_FOUND` |
| context too large | 422 | `AI_MESSAGE_CONTEXT_TOO_LARGE` |
| quota exceeded | 429 | `AI_ASSISTANT_QUOTA_EXCEEDED` |
| provider unavailable | 503 | `AI_ASSISTANT_MODEL_UNAVAILABLE` |
| policy blocked | 422 | `AI_MESSAGE_BLOCKED_BY_POLICY` |
| retrieval insufficient | 200 response mode | `INSUFFICIENT_EVIDENCE` in response, not exception |
| invalid citation produced internally | 500 | `AI_CITATION_INVALID` |

---

# 20. Required API tests

```text
createConversation_projectRequiresAccess
createConversation_generalGuideAllowsNullProject
listConversations_returnsOnlyOwnedVisibleRows
patchConversation_cannotChangeProjectScope
archiveConversation_blocksNewMessages
deleteConversation_hidesContentImmediately
submitMessage_persistsUserAndAssistantBefore202
submitMessage_idempotencyDoesNotDuplicateTurn
submitMessage_enforcesMessageAndQuotaLimits
getMessages_hidesToolTranscriptFromNormalUser
getMessage_revalidatesConversationAccess
cancelMessage_isIdempotent
explainPage_usesRegisteredMetadata
explainField_requiresServerVisibleField
explainDisabledAction_usesServerReasonCodes
suggestedQuestions_filtersByPageLocaleAndStatus
feedback_upsertsPerActorAndMessage
allErrors_useExistingErrorResponse
