# NOTE — AiTool Registry Foundation

> **Status:** `IMPLEMENTED` (foundation + seed + stub execute) — live LLM tool handlers remain out of scope  
> **Kind:** Cross-phase tracking note (không phải phase mới; không rewrite Phase 21)  
> **Capability origin:** Phase 07 `AIG-012` — Tool execution  
> **Last updated:** 2026-07-15

---

## 1. Purpose / Tại sao cần

Cần một **governed AI tool catalog** để agent gọi tool an toàn, có kiểm soát IAM và audit — thay vì đăng ký ad-hoc theo từng domain phase.

Catalog (AIG-012):

```text
AiTool
AiToolPermission
AiToolExecution
AiAgentToolBinding
```

Rules (Phase 07 §7.12):

```text
1. Tool must be registered.
2. Agent must be allowed to use tool.
3. Requesting user must have target business permission.
4. Tool call must be logged.
5. Human approval required for mutation unless policy allows.
```

---

## 2. What shipped (2026-07-15)

| Layer | Detail |
|---|---|
| Flyway | **V89** `V89__create_aiagent_tool_tables.sql` — `aiagent_tool`, `aiagent_tool_permission`, `aiagent_agent_tool_binding`, `aiagent_tool_execution` |
| Module | `modules/aiagent/tool/` — Action + QueryService + controllers |
| API | `/api/ai-agent/tools` CRUD/activate/deactivate/permissions/bindings/execute; `/api/ai-agent/tool-executions` |
| IAM | `AI_TOOL_MANAGE`, `AI_TOOL_EXECUTE` (+ SUPER_ADMIN grant); `AiAgentSecurityInterceptor` paths |
| Seed | `AiToolSeedInitializer` — Phase 31 meeting tools + Phase 37–40 AI-001 camelCase codes (idempotent) |
| Execute | **Stub/NO_OP** (or DENIED if mutation requires human approval) — logs `AiToolExecution`; no live provider tool-call |

### Seeded tool codes

**Phase 31:** `summarizeMeetingNotes`, `extractMeetingActionItems`, `extractMeetingDecisions`, `extractMeetingRisksIssues`, `draftMeetingMinutes`, `draftFollowUpMessage`, `suggestMeetingAgenda`

**Phase 37:** `suggestUnderallocatedRoles`, `suggestOverloadedResources`, `suggestAssignmentRebalance`, `explainEffortForecastIncrease`, `explainTaskCostOverrun`, `draftStaffingRecommendation`

**Phase 38:** `summarizeAccessReviewFindings`, `summarizeSensitiveAccessActivity`, `draftPrivacyRequestResponse`, `explainRetentionCandidates`, `summarizeExportAudit`, `detectSuspiciousAccessPatterns`

**Phase 39:** `suggestCsvFieldMapping`, `summarizeImportErrors`, `suggestSyncConflictResolution`, `explainProviderFailure`, `suggestExportColumns`, `summarizeIntegrationHealth`

**Phase 40:** `summarizeSupportCase`, `suggestCaseTypePriority`, `detectDuplicateCases`, `suggestKnowledgeArticle`, `summarizeIncidentTimeline`, `draftClientUpdate`, `explainSlaBreach`, `suggestSupportNextAction`

---

## 3. Still out of scope / honest limits

- Live LLM tool-calling / provider adapters that invoke handlers for each code
- Human approval workflow UI/API for mutation tools (currently DENIED when `requiresHumanApproval`)
- Phase 25/26/29 optional domain tool seed lists (can reuse same seeder pattern later)
- Do **not** claim full “AI acts on behalf of user” (AIG-013) — registry foundation only

---

## 4. Origin & Phase 21 clarification (historical)

AIG-012 was deferred toward Phase 21; Phase 21 shipped `aiplanning` only. This pass implements the missing registry without rewriting Phase 21 scope.

---

## 5. Cross-links

| Doc | Role |
|---|---|
| `PHASE_07_*_TO_BE_DETAILED.md` §7.12 | AIG-012 definition |
| `PHASE_31_*` … `PHASE_40_*` complete docs | Domain AI-001 seeds now unblocked |
| `modules/aiagent/tool/` | Implementation |

---

## 6. One-line summary

```text
AiTool registry (AIG-012) SHIPPED: V89 tables + CRUD/bindings/permissions +
stub execute logging + IAM + Phase 31/37–40 seed registrations.
Live tool handlers remain future work.

Phases 37–40 deferred operational gaps closed 2026-07-15:
overload fan-out, DocumentHub/Search masking, Jira/Slack/Drive stubs,
dead-letter JPA, warranty/effort→profitability pragmatic wiring.
```


Cross-cutting (nhiều phase chung)
Chủ đề	Trạng thái theo doc
Live LLM / AiTool handlers
Registry V89 + seed xong; execute stub/NO_OP; handlers thật còn deferred (31, 37–40, NOTE)
Live Slack/Drive/Jira OAuth/API
Stub only; không claim sync thật (39)
SMTP proven delivery
Outbox/LOG_ONLY; không claim mail thật
RAG / semantic search
Phase 41 (32)
MFA / SSO
Deferred (02 → 23 / 12.1)
Destructive retention execute
Dry-run only (38)
Phase 2–15 — còn note rõ
Phase	Còn ghi chưa làm (điểm chính)
02 IAM
Refresh-token chain / device bind; MFA/SSO; API tokens; service accounts; SoD/conditional access
03–06
Có section deferred (email verify, full outbox fan-out, …) — phần lớn nhắm phase sau
07 AI Agent
RAG; content safety; cost dashboard; acting-on-behalf; minute-window rate; secret manager
08 Knowledge
Nhiều DOC-* vẫn “not implemented” ở complete doc cũ (hub đầy đủ → 27); phần type catalog là MUST đã xong
10 Auth
Per-project IAM sâu vẫn deferred trong doc
11 Template
PTC-002 workspace phase setting; clone-from-project; marketplace
12 Capacity
Snapshot/team capacity (→22); một số CAP-* lịch sử
14 Gantt
Critical path; Gantt view prefs; Gantt export
15 Rate card
CLIENT/PROJECT rate card; FX rates; project default assignment; approval workflow
Phase 16–23
Phase	Còn ghi
16
Preview-task / preview-rate-impact
17
Scenario comparison / preview
18
PDF/proposal export; Client CRM (snapshot only); contract/invoice OUT OF SCOPE
19
Compare/diff APIs; multi-step SoD; override-with-reason
20
Activity feed; external client notifications; push/chat OUT OF SCOPE; auto-subscribe assignee hook
21
Live LLM; explain endpoints; direct WBS/TASK mutate apply; table rename ai_planning_*
22
Activity feed; XLSX; PDF OUT OF SCOPE; snapshot retention job; capacity hour rollup mỏng
23
CONDITIONAL GO — còn: ArchUnit, concurrency IT, performance baseline, Swagger lockdown, idempotency-key bắt buộc, IDOR chưa cover hết nested
Phase 24–30 (gap domain rõ)
Phase	Còn ghi
24 Scope
Evidence/review workflow HTTP; WBS/task mapping HTTP; quote import; scope reports
25 RAID
RAID/Decision link APIs; create linked-task; decision supersede/update PUT
26 Quality
AI quality suggestions; follow-ups trong doc
27 DocHub
Full template render engine (job queue có, engine tùy chọn)
28 Trace
Rich impact / graph traversal (optional)
29 Stakeholder
Có deferred section (chi tiết nhẹ hơn MUST)
30 Portal
Portal refresh-token store; rate-limit/audit query; deep deliverable/doc catalog
Phase 31–40 (mới nhất — còn mở)
Phase	Còn ghi
31
Live AI meeting tool handlers
32
Semantic/RAG (41); portal nav variants
33
Search/report index adapters
34
Global mutation interceptors; object-type admin HTTP (seed only)
35
Rich domain-event condition payloads; delivery out of scope
36
Revenue/cost source CRUD; forecast/plan aggregates; portal commercial; quote/finance → auto-rebuild events
37
Live AI handlers (seeded); vài surface phụ nếu còn trong doc
38
Destructive retention; auto-revoke; live AI; full search rewrite
39
Live OAuth/APIs; import-row/rate-limit schema-only; live AI mapping
40
Live AI service desk; rate-card-grade support cost (đang default $75/h)
Tóm lại
MUST core hầu hết phase 2–40 doc ghi COMPLETE / MOSTLY COMPLETE.
Còn “chưa làm” thật sự đáng để ý (sản phẩm / platform):
Live LLM + tool handlers
MFA/SSO, ArchUnit, perf, Swagger lockdown (23)
24 evidence/review + mappings
25 RAID/decision links
Export PDF/XLSX, activity feed
Live connectors OAuth
RAG (41)
Profitability source CRUD + portal commercial (36)
Nếu bạn muốn, sang Agent mode mình có thể làm checklist ưu tiên (P0/P1) từ danh sách này để đóng dần.