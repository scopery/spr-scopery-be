# SCOPERY BACKEND — PHASE 41–45 AI COPILOT ROADMAP ADDENDUM

> Purpose: extend Phase 00 with a detailed Advanced AI Assistant & Knowledge Intelligence track.
>
> This addendum does not replace Phase 07 or Phase 21. It continues capabilities explicitly deferred by Phase 21 and Phase 32.

---

# 1. Roadmap extension

| Phase | Name | Main outcome | User level |
|---:|---|---|---|
| 41 | Knowledge Graph / Semantic Index / Elasticsearch Hybrid Search / RAG | Permission-aware retrieval, citations, graph and indexing | Foundation |
| 42 | Contextual AI Chat / In-App Guide / Project Q&A | Replace guide and answer using page/project context | Level 1–2 |
| 43 | AI Recommendation / Suggestion Engine | Evidence-backed next-best actions and structured proposals | Level 3 |
| 44 | AI Tool Gateway / Action Planning / Agentic Operations | Preview, confirm and execute allowlisted domain actions | Level 4–5 |
| 45 | AI Governance / Evaluation / Safety / Hardening | Quality gates, rollout, cost, safety, incidents and kill switches | Production gate |

---

# 2. Capability progression

```text
Keyword search
→ semantic/hybrid retrieval
→ grounded answer
→ contextual guide
→ structured suggestion
→ action preview/diff
→ confirmed execution
→ governed limited auto-execution
```

---

# 3. Relationship with existing phases

```text
Phase 07 = provider/model/deployment/agent/prompt/execution platform.
Phase 21 = project-planning-specific proposal and safe-apply foundation.
Phase 32 = permission-aware keyword search/navigation/productivity.
Phase 41 = full semantic/RAG retrieval deferred by Phase 21/32.
Phase 42 = general read-only assistant and guide.
Phase 43 = common recommendation/suggestion layer; adapts Phase 21 history.
Phase 44 = common safe action gateway; absorbs compatible safe-apply behavior.
Phase 45 = evaluation/governance/release gate for the AI stack.
```

Phase 21 must not be deleted or silently rewritten.

---

# 4. AI authority model

| Level | Capability | Default authority |
|---:|---|---|
| 1 | Explain product/page/field | Read-only |
| 2 | Answer from project context | Read-only + citations |
| 3 | Recommend change | Suggestion only |
| 4 | Build plan and diff | Preview + confirmation |
| 5 | Execute allowed action | Policy-controlled; low risk may auto-execute, high risk confirms, critical may be forbidden |

Execution modes:

```text
READ_ONLY
SUGGEST_ONLY
CONFIRM_BEFORE_EXECUTE
AUTO_EXECUTE
FORBIDDEN
```

---

# 5. Non-negotiable architecture

```text
PostgreSQL/domain modules = source of truth
Elasticsearch = rebuildable search/semantic index
LLM = reasoning/generation
AiToolGateway = allowlisted action gateway
Action.execute(Command) = business mutation path
IAM + resource authorization = mandatory
Audit + outbox + idempotency = mandatory
```

Prohibited:

```text
LLM direct repository/JPA/SQL
arbitrary shell/code/HTTP tool
AI permission grant
cross-tenant retrieval
silent high-risk mutation
uncited project facts presented as grounded
hidden reasoning storage/exposure
```

---


# 5A. Locked implementation decisions

```text
Local object storage:
- MinIO through Docker Compose.

Production object storage:
- Cloudflare R2, private buckets by default.

Storage protocol:
- S3-compatible API through ObjectStorageProvider.
- AWS SDK for Java v2 S3 client/presigner inside infrastructure only.

Durable metadata/state:
- PostgreSQL.

Search/retrieval:
- Elasticsearch 8.x BM25 + KNN vector + hybrid fusion/reranking.

Chat realtime:
- SSE in Phase 42.

Agent action realtime:
- WebSocket in Phase 44, with Redis Pub/Sub or Streams for multi-instance coordination.
- REST/PostgreSQL remain authoritative.

Conversation persistence:
- USER, ASSISTANT, TOOL_REQUEST and TOOL_RESULT traces are persisted safely.
- Private chain-of-thought is never stored or exposed.

AI/provider portability:
- LlmProvider, EmbeddingProvider, RerankerProvider and ObjectStorageProvider ports.
```

Overall AI call direction:

```text
User question
→ persist USER message
→ build authorized context
→ classify intent
→ call registered searchKnowledge tool
→ Elasticsearch hybrid retrieval + graph expansion + reranking
→ return accessible evidence/citations
→ LLM grounded response
→ validate policy/citations
→ persist ASSISTANT/tool trace
→ stream answer with SSE
```

Higher-level action direction:

```text
User action request
→ proposed allowlisted tool calls
→ action plan and dry-run
→ preview/diff
→ confirmation where required
→ revalidate permission/state/version
→ execute Application Actions
→ persist execution/audit/outbox
→ stream progress with WebSocket
```

---

# 6. Product outcome

```text
User: “Tài liệu requirement này đang thiếu gì?”
AI: retrieves current requirement/document/task/test evidence and answers with citations.

User: “Đề xuất task còn thiếu.”
AI: creates structured suggestions with reason, confidence and impact.

User: “Tạo các task đó giúp tôi.”
AI: creates an action plan, shows the diff and requests confirmation.

User confirms.
AI: executes registered task actions, reports exact success/failure and preserves audit.
```
