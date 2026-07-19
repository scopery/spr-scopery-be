# Phase 42 — Context Builder Contract — Repository-Aligned

> Status: **Accepted / implementation-blocking**  
> Owner: `modules/aiassistant`  
> Service: `AiAssistantContextBuilder`  
> Rule: client context is a hint; server context is authoritative.

---

# 1. Purpose

The Context Builder converts a user request and UI hint into a validated, permission-aware, reproducible context snapshot for one assistant turn.

It must answer:

```text
Who is the actor?
Which workspace/project is active?
Which registered page is open?
Which entity is selected and visible?
Which fields are visible?
Which actions are actually available?
Why is a selected action disabled?
Which locale/timezone applies?
Which memory summary is safe to reuse?
```

It must not grant access, mutate business data, or trust client-reported action/field visibility.

---

# 2. Input contract

```json
{
  "conversationId": "uuid",
  "assistantMessageId": "uuid",
  "turnId": "uuid",
  "userQuestion": "Vì sao task này không thể bắt đầu?",
  "clientContext": {
    "route": "/workspaces/{workspaceId}/projects/{projectId}/tasks/{taskId}",
    "pageCode": "PROJECT_TASK_DETAIL",
    "entity": {
      "type": "TASK",
      "id": "uuid"
    },
    "selectedActionCode": "TASK_START",
    "tabCode": "DEPENDENCIES",
    "locale": "vi-VN",
    "timezone": "Asia/Ho_Chi_Minh",
    "visibleFieldCodes": ["TASK_STATUS", "TASK_BLOCKER"],
    "availableActionCodes": ["TASK_EDIT", "TASK_START"],
    "clientContextVersion": 1,
    "clientContextHash": "ctx:v1:sha256:..."
  }
}
```

The authenticated actor, current workspace, trace ID, and correlation ID must be taken from server security/request context, never from request JSON.

---

# 3. Server-resolved output

```json
{
  "actorId": "uuid",
  "workspaceId": "uuid",
  "projectId": "uuid",
  "conversationId": "uuid",
  "assistantMessageId": "uuid",
  "turnId": "uuid",
  "route": "/workspaces/{workspaceId}/projects/{projectId}/tasks/{taskId}",
  "page": {
    "code": "PROJECT_TASK_DETAIL",
    "metadataVersion": 12,
    "registered": true
  },
  "entity": {
    "type": "TASK",
    "id": "uuid",
    "version": 7,
    "visible": true
  },
  "selectedActionCode": "TASK_START",
  "tabCode": "DEPENDENCIES",
  "locale": "vi-VN",
  "timezone": "Asia/Ho_Chi_Minh",
  "serverVisibleFieldCodes": ["TASK_STATUS", "TASK_BLOCKER", "TASK_DEPENDENCIES"],
  "availableActionCodes": ["TASK_EDIT"],
  "disabledActionReasons": {
    "TASK_START": ["TASK_HAS_BLOCKING_DEPENDENCY"]
  },
  "permissionSignature": "acl:v1:sha256:...",
  "contextHash": "ctx:v1:sha256:...",
  "memory": {
    "summaryId": "uuid-or-null",
    "summaryVersion": 2,
    "status": "ACTIVE",
    "coveredThroughMessageSequence": 32,
    "summaryText": "bounded user-visible summary"
  },
  "contextStatus": "VALID",
  "redactions": []
}
```

Only server-resolved output may enter the prompt or be persisted as authoritative context.

---

# 4. Resolution order

```text
1. Load conversation by ID.
2. Verify ownership/current administrative access.
3. Resolve authenticated actor and workspace.
4. Verify conversation workspace and project scope.
5. Reject project mismatch; do not silently switch scope.
6. Resolve pageCode against registered page/navigation metadata.
7. Resolve entity type/id through a read-only context adapter.
8. Revalidate entity view permission and field masking.
9. Resolve actual available action codes through existing IAM/business action metadata.
10. Resolve disabled-action reason codes from business policy services.
11. Validate locale/timezone.
12. Load active memory summary only when permission signature and scope remain valid.
13. Calculate permissionSignature and contextHash.
14. Persist `aiassistant_context_snapshot`.
```

---

# 5. Context adapter contract

Phase 42 must not import source JPA entities across module boundaries.

Logical port:

```java
public interface AiAssistantEntityContextProvider {
    boolean supports(String entityType);
    AiAssistantEntityContext resolve(AiAssistantEntityContextQuery query);
}
```

Minimum Phase 42 entity types:

```text
WORKSPACE
PROJECT
TASK
DOCUMENT_VERSION
MEETING_MINUTE
```

Providers return bounded, permission-filtered metadata only:

```text
entity type/id/version
display title
status/reason codes needed for explanation
visible field codes
registered app route
actual available action codes
disabled action reason codes
```

They must not return unrestricted raw entity data.

---

# 6. Server-side override rules

| Client field | Server behavior |
|---|---|
| workspace/actor | ignored if supplied; server security context wins |
| projectId | must equal conversation scope and be accessible |
| pageCode | resolved against registered metadata; unknown page is explicit fallback/error |
| entity type/id | reloaded and authorization-checked |
| visibleFieldCodes | intersect/replace with server-visible fields |
| availableActionCodes | fully replaced by server result |
| selectedActionCode | kept only as requested action; availability/reason resolved server-side |
| locale | accepted only when supported; otherwise repository default |
| timezone | validated IANA value; otherwise actor/workspace default |
| clientContextHash | cache hint only; never used for authorization |

A client claiming an action is available cannot make it available.

---

# 7. Context hash

Format:

```text
ctx:v1:sha256:<64 lowercase hex>
```

Canonical hash input:

```json
{
  "actorId": "uuid",
  "workspaceId": "uuid",
  "projectId": "uuid-or-null",
  "pageCode": "string-or-null",
  "pageMetadataVersion": 12,
  "entityType": "string-or-null",
  "entityId": "uuid-or-null",
  "entityVersion": 7,
  "selectedActionCode": "string-or-null",
  "tabCode": "string-or-null",
  "locale": "vi-VN",
  "timezone": "Asia/Ho_Chi_Minh",
  "serverVisibleFieldCodes": ["sorted", "unique"],
  "availableActionCodes": ["sorted", "unique"],
  "disabledActionReasons": {"sortedKey": ["sorted", "unique"]},
  "permissionSignature": "acl:v1:sha256:...",
  "memorySummaryVersion": 2
}
```

Canonicalization:

```text
UTF-8
object keys sorted lexicographically
arrays that represent sets sorted and deduplicated
null fields retained consistently
no timestamps, trace IDs, secrets, raw content, or route query tokens
SHA-256 lowercase hex
```

---

# 8. Permission signature

The context snapshot reuses the canonical ACL signature format locked in Phase 41:

```text
acl:v1:sha256:<64 lowercase hex>
```

The signature is derived from effective access facts required by current IAM conventions. It is stored for invalidation/comparison and never returned to the client.

A matching old signature is not sufficient by itself; each new message still revalidates authorization.

---

# 9. Page and guide metadata

Page/field/action explanations should use existing route/page/action metadata where available.

Fallback order:

```text
1. registered page/field/action metadata
2. active AiGuideDefinition
3. grounded Phase 41 retrieval when the question asks project facts
4. honest unknown-page/insufficient-evidence response
```

Do not fabricate a route, button, field, or permission reason.

---

# 10. Disabled-action explanation

Required server result:

```json
{
  "actionCode": "TASK_START",
  "available": false,
  "reasonCodes": ["TASK_HAS_BLOCKING_DEPENDENCY"],
  "relatedEntityRefs": [
    {"type": "TASK", "id": "dependency-uuid"}
  ]
}
```

Reason codes must come from existing action authorization/business policy logic. The LLM only converts validated codes/evidence into user-facing wording.

---

# 11. Memory validation

An active summary may be injected only if:

```text
conversation scope matches
summary status = ACTIVE
permissionSignature matches current effective signature
coveredThroughMessageSequence is <= current last sequence
summary is not expired/redacted
no invalidation event is pending for the actor/scope
```

Otherwise mark it STALE/REDACTED as appropriate and omit it.

---

# 12. Failure modes

| Condition | Result |
|---|---|
| conversation hidden | `AI_CONVERSATION_NOT_FOUND` or access error per repo convention |
| project scope mismatch | `AI_CONVERSATION_PROJECT_SCOPE_MISMATCH` |
| unknown page | context status valid with unknown-page fallback, or `AI_CONTEXT_PAGE_UNKNOWN` for explain endpoint |
| entity invisible | `AI_CONTEXT_ENTITY_NOT_FOUND` without leaking existence |
| selected field invisible | `AI_CONTEXT_ACCESS_DENIED` |
| action code unknown | `AI_CONTEXT_ACTION_UNKNOWN` |
| permission changed | rebuild snapshot; emit revalidated/redacted event |
| context exceeds bounded metadata size | `AI_MESSAGE_CONTEXT_TOO_LARGE` |

---

# 13. Required tests

```text
contextBuilder_usesAuthenticatedActorNotClientActor
contextBuilder_rejectsConversationProjectMismatch
contextBuilder_unknownPageDoesNotFabricateMetadata
contextBuilder_reloadsEntityAndChecksViewPermission
contextBuilder_clientVisibleFieldsCannotExpandServerFields
contextBuilder_clientAvailableActionsCannotGrantAction
contextBuilder_disabledReasonComesFromBusinessPolicy
contextBuilder_contextHashIsDeterministic
contextBuilder_contextHashChangesWhenPermissionChanges
contextBuilder_neverHashesSecretOrRawDocumentContent
contextBuilder_staleMemoryIsNotInjected
contextBuilder_persistsOneSnapshotPerTurn
contextBuilder_entityProviderDoesNotExposeJpaEntity
