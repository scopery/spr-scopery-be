# PHASE 45 — TO-BE AI Governance, Evaluation, Safety, Observability, Cost Control & Production Hardening

> Project: Scopery Backend  
> Phase: 45  
> Document type: TO-BE implementation-grade specification  
> Status: Planning / AI trust, evaluation, operations and production-readiness gate  
> Roadmap group: Advanced AI Assistant & Knowledge Intelligence  
> Depends on: Phase 00–44, with hard runtime dependencies limited to Core Platform and explicitly declared adapters  
> API base: `/api`  
> Primary module: `modules/aigovernance`, `modules/aievaluation`, `modules/aiobservability`, or platform AI extensions depending on repository architecture  
> Important rule: Phase 45 is the release gate for Phase 41–44. It measures quality and safety, controls rollout/cost, and provides incident/kill-switch operations.

---

# 0. Purpose

Phase 45 is the production-readiness gate for the AI Copilot stack.

It does not add broad business modules. It evaluates, governs, observes, hardens and controls release of retrieval, chat, recommendation and action capabilities.

Phase 45 answers:

```text
Does retrieval find the correct accessible evidence?
Are answers grounded and citations accurate?
Are suggestions useful and supported?
Are tool selection, arguments and action plans valid?
Can policies, permissions or confirmation be bypassed?
Are cost, latency, outage and rollback controlled?
```

---

# 1. Product intention and core principle

```text
AI is not production-ready because it looks good in a demo.
It is production-ready only when quality, permission safety,
action safety, cost, reliability and recovery are measured and controlled.
```

Evaluation must combine deterministic checks, human review and clearly identified model-assisted evaluation.

Standalone and modular behavior remains mandatory:

```text
Each capability works with Core Platform and available adapters.
Missing optional modules reduce available sources/tools/packs gracefully.
No optional integration becomes a hidden hard runtime dependency.
```

---

# 2. Source inputs

Before coding Phase 45, the agent must read:

```text
1. Phase 00–44 docs/completion
2. Phase 07 AI logs/usage policies
3. Phase 23 hardening/observability
4. Phase 38 privacy/retention
5. Phase 41 retrieval traces
6. Phase 42 conversation/feedback
7. Phase 43 suggestion lifecycle
8. Phase 44 action history/policy decisions
9. Current metrics/logging/tracing/CI
10. Phase 40 incident/support
11. Current code, migrations and tests
```

The agent must inspect actual code, migrations, seeders and tests. Documentation alone is not proof of implementation.

---

# 3. Current expected gaps

Likely missing or partial:

```text
AiEvaluationDataset/DatasetVersion/Case
AiEvaluationRun/Result/MetricDefinition
AiQualityGate
AiReleaseCandidate/AiRolloutPolicy
AiExperiment
AiSafetyFinding/AiIncident
AiRedTeamScenario
AiCostBudget/UsageSnapshot
AiLatencySlo/ModelRoutingPolicy
AiKillSwitch/RegressionBaseline
```

Every item must be classified as:

```text
CURRENTLY_IMPLEMENTED
PARTIALLY_IMPLEMENTED
MUST_IMPLEMENT_IN_PHASE_45
MUST_HARDEN_IN_PHASE_45
SEED_ONLY_IN_PHASE_45
DEFERRED_TO_PHASE_XX
NOT_IN_SCOPE_FOR_PHASE_45
```

---

# 4. Target statement

Phase 45 must deliver:

```text
1. Versioned benchmark datasets/cases
2. Retrieval/chat/suggestion/action evaluation
3. Deterministic/human/model-assisted metrics
4. Blocking quality gates
5. Regression tests for model/prompt/index/tool/policy
6. Safety/red-team scenarios
7. Permission leakage and citation integrity suites
8. Cost budgets and model routing
9. Latency/reliability SLOs
10. Canary rollout/rollback/kill switches
11. AI incident and safety finding lifecycle
12. Dashboards/reports/alerts
13. Privacy/retention/redaction for telemetry
14. Operational runbook
```

---

# 5. Boundary decisions

## Does

```text
evaluate/compare/release-gate
monitor/alert
promote/rollback/disable configuration
track incidents/findings
control budget/routing
```
## Does not

```text
fine-tune by default
claim certification
automatically broaden permission
enable autonomous critical actions
store raw sensitive prompts indefinitely
replace human product acceptance
```

General prohibitions:

```text
No cross-tenant access.
No permission bypass.
No raw secret exposure.
No hidden chain-of-thought storage/exposure.
No capability may claim a side effect or quality level not implemented and tested.
```

---

# 6. Required entities and value objects

## AiEvaluationDataset/Version

```text
scope/source policy/status/version
immutable published cases
global/workspace governance
```
## AiEvaluationCase

```text
input/context fixture
expected sources/assertions
forbidden content
expected suggestions/tools/actions
risk/tags/reviewer
```
## AiEvaluationRun/Result

```text
explicit model/prompt/index/policy/tool targets
metric/pass/fail/evidence/evaluator
latency/cost/error
```
## AiMetricDefinition

```text
retrieval precision/recall/rank
groundedness/citation/refusal
suggestion precision/acceptance/staleness
tool/action validity/success/policy denial
latency/token/cost
```
## AiQualityGate

```text
scope/thresholds
blocking/non-blocking
required datasets/safety
sample/latency/cost
pass/warn/fail/insufficient
```
## AiReleaseCandidate/RolloutPolicy

```text
assistant/prompt/model/retrieval/recommendation/tool targets
internal/admin/pilot/canary/GA/rollback stages
```
## AiSafetyFinding/AiIncident

```text
permission leak/sensitive exposure/citation mismatch/policy bypass/duplicate mutation/cost/outage/index staleness
owner/severity/status/lifecycle
```
## AiCostBudget/UsageSnapshot

```text
workspace/user/agent/capability/model scope
daily/monthly token/currency
warning/block/fallback
```
## AiKillSwitch

```text
provider/model/agent/retrieval/recommendation/tool/risk/workspace target
immediate audited state
```

All mutable important entities should follow repository conventions for UUIDs, audit columns, optimistic versioning and Flyway migrations.

---


# 6A. Locked technology decisions for Phase 41–45

The following technology decisions are now explicit and must be treated as the default implementation baseline unless a later Architecture Decision Record replaces them:

```text
Backend runtime:
- Java 21.
- Spring Boot 3.x.
- Spring Web MVC for normal REST endpoints.
- Spring SSE support for Phase 42 chat streaming.
- Spring WebSocket support for Phase 44 long-running agent execution updates.

Primary transactional database:
- PostgreSQL.
- Spring Data JPA/Hibernate.
- Flyway migrations.

Search and retrieval:
- Elasticsearch 8.x.
- BM25 lexical retrieval.
- dense_vector + KNN semantic retrieval.
- Reciprocal Rank Fusion or equivalent deterministic hybrid merge.
- Optional reranker through a provider adapter.

Object/file storage:
- Local development and integration testing: MinIO.
- Staging/production: Cloudflare R2.
- Protocol: S3-compatible API.
- Java client: AWS SDK for Java v2 S3 client/presigner, behind ObjectStorageProvider.

Caching, rate limiting and distributed realtime coordination:
- Redis.
- Redis Pub/Sub or Redis Streams may coordinate multi-instance execution/status delivery.
- PostgreSQL remains the durable source of truth; Redis is never the sole durable record.

Reliability and observability:
- Resilience4j for timeout/retry/circuit-breaker/bulkhead policies.
- Micrometer metrics.
- OpenTelemetry traces.
- Prometheus-compatible metrics collection.
- Grafana-compatible dashboards.
- Structured JSON logs with correlation/trace IDs.

AI provider integration:
- LlmProvider abstraction.
- EmbeddingProvider abstraction.
- RerankerProvider abstraction.
- Provider/model/deployment selected by versioned profile; domain/application code must not depend directly on one vendor SDK.
```

Provider-specific SDKs may exist only inside infrastructure adapters. Domain and application layers depend on ports/interfaces.

---

# 6B. Locked object storage architecture

The final storage decision is:

```text
Local development: MinIO.
Production storage: Cloudflare R2.
Communication protocol: S3-compatible API.
```

These three concepts have different responsibilities:

```text
MinIO
= object storage server run locally, normally through Docker Compose.

Cloudflare R2
= managed production object storage containing real user/project file bytes.

S3-compatible API
= the common API contract used by Scopery Backend to communicate with both systems.
```

The system must use the same application port and mostly the same infrastructure implementation in both environments:

```java
public interface ObjectStorageProvider {
    StoredObject upload(StorageUploadRequest request);
    PresignedUpload createPresignedUpload(PresignedUploadRequest request);
    PresignedDownload createPresignedDownload(PresignedDownloadRequest request);
    StorageObjectMetadata head(String objectKey);
    InputStream download(String objectKey);
    void delete(String objectKey);
}
```

Default adapter direction:

```text
ObjectStorageProvider
    ↓
S3CompatibleObjectStorageProvider
    ↓ configuration only
    ├── MinIO local endpoint
    └── Cloudflare R2 production endpoint
```

Direct dependencies from domain/application services to Cloudflare, MinIO or AWS-specific classes are forbidden.

## Storage responsibility split

```text
Cloudflare R2 / MinIO:
- raw file bytes;
- original uploads;
- generated exports;
- optional derived artifacts such as preview images or extracted text blobs when explicitly modeled.

PostgreSQL:
- FileAsset/FileVersion metadata;
- ownership and workspace/project relationships;
- object key;
- content type;
- size;
- checksum;
- upload status;
- retention state;
- security classification;
- audit references.

Elasticsearch:
- extracted searchable text;
- chunks;
- embeddings;
- searchable metadata projection;
- citation/source references.
```

R2 and MinIO are not business databases. Elasticsearch is not the source of truth for file ownership or permissions.

## Required object-key convention

Object keys must be opaque, normalized and tenant-scoped. A recommended pattern is:

```text
workspaces/{workspaceId}/projects/{projectId}/documents/{documentId}/versions/{versionId}/source/{generatedObjectName}
workspaces/{workspaceId}/projects/{projectId}/meetings/{meetingId}/attachments/{attachmentId}/{generatedObjectName}
workspaces/{workspaceId}/exports/{exportJobId}/{generatedObjectName}
```

Rules:

```text
- Never use an untrusted original filename as the complete object key.
- Preserve the original filename in PostgreSQL metadata.
- Include immutable IDs in object keys.
- Prevent path traversal and control characters.
- Default bucket visibility is private.
- Public permanent URLs are forbidden for private project files.
```

## Presigned upload/download

Large file bytes should normally flow directly between frontend and object storage through short-lived presigned URLs:

```text
Frontend → Backend: create upload session.
Backend: validate permission/type/size and create PENDING_UPLOAD metadata.
Backend → storage: create presigned upload URL.
Frontend → MinIO/R2: upload bytes directly.
Frontend → Backend: complete upload.
Backend → storage: HeadObject verification.
Backend: mark AVAILABLE and publish FileUploaded/FileVersionCreated.
```

Download/preview flow:

```text
Frontend → Backend: request file preview/download.
Backend: authorize the current user against the current resource state.
Backend → storage: create short-lived presigned download URL.
Frontend → MinIO/R2: download bytes directly.
```

Presigned URLs must be short-lived, scoped to one object/operation and must not replace application authorization.

## Environment configuration

```yaml
# application-local.yml
storage:
  provider: s3-compatible
  endpoint: http://localhost:9000
  region: us-east-1
  bucket: scopery-local
  access-key: ${MINIO_ACCESS_KEY}
  secret-key: ${MINIO_SECRET_KEY}
  path-style-access: true
```

```yaml
# application-production.yml
storage:
  provider: s3-compatible
  endpoint: https://${R2_ACCOUNT_ID}.r2.cloudflarestorage.com
  region: auto
  bucket: ${R2_BUCKET_NAME}
  access-key: ${R2_ACCESS_KEY_ID}
  secret-key: ${R2_SECRET_ACCESS_KEY}
  path-style-access: true
```

Secrets must come from environment/secret management and must never be committed to source control.

## Storage test levels

```text
Unit tests:
- mock ObjectStorageProvider.

Integration tests:
- real MinIO container.

Staging smoke tests:
- dedicated private Cloudflare R2 staging bucket.
```

R2 smoke tests must cover CORS, presigned upload/download, Unicode filenames, multipart upload, metadata headers, Content-Disposition, cancellation, timeout, delete and private-bucket access.

---

# 6C. Cross-phase production governance requirements

Phase 45 governs the entire technical chain:

```text
MinIO/R2 file ingestion
→ extraction/chunking
→ Elasticsearch indexing
→ hybrid retrieval
→ chat/SSE
→ suggestions
→ action planning/WebSocket execution
→ audit/evaluation/incident response
```

The phase must define versioned operational policies for:

```text
model/provider routing
embedding/reranker profiles
storage provider configuration
file type/size limits
presigned URL TTL
retrieval topK/context budget
SSE/WebSocket timeout and heartbeat
Redis retention and failure behavior
action batch/risk/confirmation limits
conversation/tool trace retention
cost ceilings and kill switches
```

## Provider portability

The production system must remain portable through:

```java
interface LlmProvider
interface EmbeddingProvider
interface RerankerProvider
interface ObjectStorageProvider
```

Provider replacement must not require domain model changes. Configuration and infrastructure adapters are allowed to vary.

## Storage governance

Required controls:

```text
- Cloudflare R2 production buckets private by default.
- Separate local, staging and production buckets/credentials.
- Least-privilege R2 API tokens.
- Object keys and metadata must not expose secrets.
- Malware/file validation hook before AVAILABLE where implemented.
- Retention/delete jobs reconcile PostgreSQL, R2 and Elasticsearch.
- Orphan-object and missing-object reconciliation jobs.
- Checksums verified after upload and during recovery where applicable.
- Backup/export/restore runbook documented.
```

## Streaming governance

Required controls:

```text
SSE:
- maximum turn duration;
- heartbeat interval;
- reconnect/resume behavior;
- cancellation timeout;
- final-state persistence check.

WebSocket:
- authenticated connection/subscription;
- authorization per execution;
- heartbeat/idle timeout;
- reconnect through REST state recovery;
- bounded event payloads;
- Redis degradation behavior;
- no secret/tool raw payload broadcast.
```

## Resilience policies

Use Resilience4j or equivalent explicit policies for:

```text
LLM provider timeout/retry/circuit breaker
embedding provider timeout/retry
reranker fallback
Elasticsearch timeout and lexical fallback
R2/MinIO timeout and retry
Redis degradation
SSE/WebSocket publication failure
```

Retries are allowed only for operations proven idempotent or protected by idempotency keys.

## Observability baseline

Metrics and traces must cover:

```text
file upload completion/failure/size/latency
extraction/chunk/embedding/index lag
retrieval latency and result count
citation coverage
chat first-token and completion latency
SSE disconnect/reconnect/cancel/failure
suggestion generation/acceptance/staleness
plan/confirmation/execution/partial/compensation
WebSocket active sessions and delivery failures
provider token/cost/rate-limit/error
R2/Elasticsearch/Redis availability
```

Dashboards must separate tenant-safe aggregate operational data from sensitive per-conversation debugging.

---

# 6D. Required evaluation coverage for the detailed architecture

Evaluation suites must include:

```text
Storage and ingestion:
- presigned upload/download correctness;
- R2/MinIO compatibility;
- orphan/missing object reconciliation;
- extraction and reindex recovery.

Retrieval:
- BM25 exact lookup;
- vector semantic lookup;
- hybrid merge/reranking;
- authorization and field masking;
- citation source/version correctness.

Conversation:
- USER/ASSISTANT/TOOL_REQUEST/TOOL_RESULT persistence;
- no chain-of-thought persistence;
- memory invalidation;
- SSE ordering/reconnect/cancel/failure recovery.

Suggestion:
- evidence quality;
- confidence calibration;
- stale/deduplicated/suppressed behavior.

Action:
- tool schema validation;
- dual authorization;
- preview/confirmation hash;
- WebSocket progress delivery;
- disconnect recovery;
- idempotency/partial/compensation.
```

Production release gates must fail when access-control leakage, citation-source mismatch, destructive tool escape, missing durable final state or unreconciled storage deletion is detected.

---

---

# 7. Architecture and processing flow

```text
Versioned target (model/prompt/index/policy/tool)
→ evaluation datasets/cases
→ deterministic + human + model-assisted evaluators
→ metric results
→ blocking quality gate
→ release candidate
→ internal/pilot/canary rollout
→ monitoring/alerts
→ rollback or kill switch
```

Mandatory safety scenarios include:

```text
cross-workspace request
prompt injection in indexed content
secret/token request
ignore permission request
permission grant request
project/workspace delete request
unauthorized finance/rate change
malicious tool argument
citation spoofing
archived/deleted retrieval
bulk over limit
confirmation replay after plan change
external side effect without confirmation
```

Indexed/user content is untrusted data, never system instruction.

---

# 8. API contract

Required API examples:

```text
POST /api/ai-governance/datasets
POST /api/ai-governance/datasets/{id}/versions
GET /api/ai-governance/datasets
POST /api/ai-governance/evaluation-runs
GET /api/ai-governance/evaluation-runs/{runId}
GET /api/ai-governance/evaluation-runs/{runId}/results
POST /api/ai-governance/quality-gates
POST /api/ai-governance/quality-gates/{gateId}/evaluate
POST /api/ai-governance/release-candidates
POST /api/ai-governance/release-candidates/{id}/promote
POST /api/ai-governance/release-candidates/{id}/rollback
GET /api/ai-governance/dashboard
GET /api/ai-governance/cost-usage
POST /api/ai-governance/incidents
PATCH /api/ai-governance/incidents/{id}
POST /api/ai-governance/kill-switches
PATCH /api/ai-governance/kill-switches/{id}
```

Controllers must map Request → Command/QueryService and return DTOs, never JPA/domain aggregates.

---

# 9. IAM and authorization

Required permissions:

```text
AI_GOVERNANCE_VIEW
AI_GOVERNANCE_MANAGE
AI_EVALUATION_DATASET_VIEW
AI_EVALUATION_DATASET_MANAGE
AI_EVALUATION_RUN
AI_QUALITY_GATE_VIEW
AI_QUALITY_GATE_MANAGE
AI_RELEASE_CANDIDATE_VIEW
AI_RELEASE_CANDIDATE_MANAGE
AI_ROLLOUT_MANAGE
AI_EXPERIMENT_MANAGE
AI_SAFETY_FINDING_VIEW
AI_SAFETY_FINDING_MANAGE
AI_INCIDENT_MANAGE
AI_COST_VIEW
AI_COST_BUDGET_MANAGE
AI_KILL_SWITCH_MANAGE
```

Rules:

```text
AI/search capability permission never grants access to underlying source objects.
Resource authorization and field masking remain mandatory.
Administrative/debug/governance permissions are sensitive and audited.
External portal scope must be explicit; internal access is never inferred.
```

---

# 10. Event Registry integration

Recommended source system:

```text
SCOPERY_AI_PHASE_45
```

Required events:

```text
AI_EVALUATION_DATASET_PUBLISHED
AI_EVALUATION_RUN_STARTED
AI_EVALUATION_RUN_COMPLETED
AI_EVALUATION_RUN_FAILED
AI_QUALITY_GATE_PASSED
AI_QUALITY_GATE_FAILED
AI_RELEASE_CANDIDATE_CREATED
AI_RELEASE_CANDIDATE_PROMOTED
AI_RELEASE_CANDIDATE_ROLLED_BACK
AI_ROLLOUT_POLICY_CHANGED
AI_SAFETY_FINDING_CREATED
AI_SAFETY_FINDING_RESOLVED
AI_INCIDENT_CREATED
AI_INCIDENT_RESOLVED
AI_COST_BUDGET_WARNING
AI_COST_BUDGET_EXCEEDED
AI_LATENCY_SLO_BREACHED
AI_PROVIDER_HEALTH_DEGRADED
AI_KILL_SWITCH_ACTIVATED
AI_KILL_SWITCH_RELEASED
```

Event payloads must not include raw prompt/document content, vectors, secrets, tokens, unmasked sensitive fields or hidden reasoning.

---

# 11. Audit, outbox, idempotency, privacy and observability

```text
Audit all policy/configuration changes and sensitive administrative views.
Use outbox for cross-module/asynchronous effects.
Use stable idempotency keys for repeatable jobs/executions.
Redact errors and telemetry.
Apply Phase 38 retention, legal hold and sensitive-field policy.
Correlate operations with traceId and source/execution identifiers.
```

---

# 12. Business rules master

```text
EVAL-001 Published dataset version is immutable.
EVAL-002 Evaluation target versions are explicit.
EVAL-003 Evaluator type is distinguishable.
EVAL-004 Mandatory safety cases cannot be silently waived.
GATE-001 Failed blocking gate prevents promotion.
GATE-002 Insufficient data is not a pass.
ROLL-001 Promotion/rollback is audited.
ROLL-002 Kill switch is immediately effective.
SAFE-001 Permission leakage suite must pass.
SAFE-002 Retrieved content cannot override system/tool policy.
SAFE-003 Forbidden-action suite must pass before action rollout.
COST-001 Hard budget blocks before provider call.
COST-002 Fallback model must be approved/gated.
INC-001 Safety incident has owner/severity/status.
PRV-001 Production data is not automatically copied into benchmarks.
```

---

# 13. Error catalog

```text
AI_EVALUATION_DATASET_NOT_FOUND
AI_EVALUATION_DATASET_INVALID_STATUS
AI_EVALUATION_DATASET_VERSION_IMMUTABLE
AI_EVALUATION_CASE_INVALID
AI_EVALUATION_RUN_NOT_FOUND
AI_EVALUATION_RUN_FAILED
AI_EVALUATION_TARGET_INVALID
AI_METRIC_DEFINITION_NOT_FOUND
AI_QUALITY_GATE_NOT_FOUND
AI_QUALITY_GATE_FAILED
AI_QUALITY_GATE_INSUFFICIENT_DATA
AI_RELEASE_CANDIDATE_NOT_FOUND
AI_RELEASE_PROMOTION_BLOCKED
AI_ROLLOUT_INVALID_TRANSITION
AI_EXPERIMENT_INVALID
AI_SAFETY_FINDING_NOT_FOUND
AI_INCIDENT_NOT_FOUND
AI_COST_BUDGET_EXCEEDED
AI_MODEL_ROUTING_UNAVAILABLE
AI_KILL_SWITCH_NOT_FOUND
AI_KILL_SWITCH_ALREADY_ACTIVE
AI_GOVERNANCE_ACCESS_DENIED
```

Use module-specific error catalogs. Do not throw generic business exceptions or leak provider/internal stack details.

---

# 14. Required tests

```text
publishDatasetVersion_immutable
runRetrievalEvaluation_success
runChatEvaluation_success
runSuggestionEvaluation_success
runActionEvaluation_success
evaluationTargetVersions_persisted
insufficientData_notPassed
blockingMetricFailure_blocksRelease
mandatorySafetyFailure_blocksRelease
passedGate_allowsPromotion
rollbackRestoresPreviousApprovedTarget
promptInjectionFromDocument_doesNotChangeSystemPolicy
crossWorkspaceLeakageSuite_passes
permissionGrantRequest_forbidden
confirmationReplay_afterPlanChange_blocked
citationSpoofing_detected
hardBudgetBlocksProviderCall
fallbackUsesApprovedModelOnly
canaryRolloutTargetsConfiguredWorkspaces
killSwitchImmediatelyDisablesCapability
criticalSafetyFinding_createsAlert
traceLinksConversationRetrievalAction
telemetryRetention_applied
metricsDoNotStoreRawSensitiveContent
```

Mandatory build gates:

```bash
mvn compile
mvn test
```

---

# 15. Manual verification checklist

```text
1. Publish retrieval benchmark and compare two index/policy versions.
2. Run grounded Q&A evaluation and inspect citation accuracy.
3. Run prompt injection/cross-workspace leakage suites.
4. Block an action release candidate with a failing forbidden-action test.
5. Promote a passing assistant to a pilot workspace.
6. Activate kill switch and verify immediate disable/fallback.
7. Trigger cost warning and hard budget block.
8. Simulate provider outage and safe degradation.
9. Create/resolve AI safety incident.
10. Verify dashboard correlation chat→retrieval→suggestion→action.
11. Verify telemetry retention/redaction.
12. Produce production-readiness report/runbook.
```

---

# 16. Acceptance criteria

Phase 45 is accepted only if:

```text
1. Evaluation datasets/cases/runs/results implemented
2. Retrieval/chat/suggestion/action metrics implemented
3. Blocking quality gates enforced
4. Permission leakage/prompt injection red-team suites implemented
5. Release/canary/rollback/kill switch implemented
6. Cost budgets/usage/routing implemented
7. Latency/reliability metrics and alerts implemented
8. Safety finding/incident lifecycle implemented
9. Telemetry privacy/retention implemented
10. Correlated dashboards/reporting implemented
11. Failed blocking gate cannot promote
12. Compile/test pass
13. Completion file and runbook exist
```

Do not mark complete when tests fail, access can leak, or a deferred capability is merely claimed.

---

# 17. Required phase completion file

Agent must create:

```text
docs/phase-complete/PHASE_45_AI_GOVERNANCE_EVALUATION_SAFETY_HARDENING_TO_BE_COMPLETE.md
```

Required sections:

```text
# Phase 45 — Complete

## 1. Summary
## 2. Inputs Reviewed
## 3. Current vs TO-BE
## 4. Implemented/Hardened
## 5. Boundary
## 6. Entity Mapping
## 7. Evaluation Dataset
## 8. Metric Catalog
## 9. Retrieval Evaluation
## 10. Chat/Citation Evaluation
## 11. Suggestion Evaluation
## 12. Tool/Action Evaluation
## 13. Safety/Red Team
## 14. Quality Gates
## 15. Release/Rollout/Rollback
## 16. Kill Switch
## 17. Cost/Routing
## 18. Latency/Reliability
## 19. Incident/Findings
## 20. Dashboards/Alerts
## 21. Privacy/Retention
## 22. API/Auth/Events
## 23. Tests/Results
## 24. Manual Verification
## 25. Operational Runbook
## 26. Assumptions/Deviations/Risks
## 27. Storage/Streaming Governance
## 28. Provider Portability
## 29. R2/MinIO/SSE/WebSocket Release Gates
## 30. Operational Dashboards and Runbooks

```

---

# 18. Prompt to give coding agent

```text
You are implementing Phase 45 — TO-BE AI Governance, Evaluation, Safety, Observability, Cost Control & Production Hardening.

This is not an as-is documentation task.

Before coding:
- Read CLAUDE.md / CLAUDE.ms.
- Read Coding_convention.md.
- Read Phase 00–44 docs and completion files.
- Inspect current backend code, migrations, seeders and tests.

Your task:
1. Classify current AI evaluation/governance capability.
2. Implement datasets/cases/runs/results/metrics.
3. Implement retrieval/chat/suggestion/action evaluation.
4. Implement blocking quality gates and release candidates.
5. Implement canary/rollback/kill switch.
6. Implement red-team permission leakage/prompt injection/forbidden action suites.
7. Implement storage, SSE, WebSocket, Redis and provider-portability quality gates.
8. Implement cost budgets/routing/SLOs and Resilience4j policies.
9. Implement findings/incidents/dashboards/alerts with Micrometer/OpenTelemetry metrics and traces.
10. Apply privacy/retention/redaction and PostgreSQL/R2/Elasticsearch reconciliation controls.
11. Add IAM/events/audit/tests, run compile/test and create operational runbooks.

Do not implement or claim capabilities outside the explicit Phase 45 boundary.
```

---

# 19. Quick tracking matrix

| Capability | Current backend | Phase action | Later |
|---|---|---|---|
| AI logs | Phase 07 | Reuse/harden | — |
| Retrieval/chat/suggestion/action traces | Phase 41–44 | Evaluate/monitor | — |
| Benchmark datasets | Missing | Must implement | — |
| Quality gates | Missing | Must implement | — |
| Red-team suite | Missing | Must implement | Security analytics later |
| Release/canary/rollback | Missing | Must implement | — |
| Kill switch | Missing | Must implement | — |
| Cost budgets/routing | Partial Phase 07 | Harden | — |
| AI incidents | Missing | Must implement | SIEM later |
| Fine-tuning/multi-agent | Missing | Not required | Later backlog |

| MinIO/R2 storage governance | Phase 41 | Evaluate/harden | — |
| SSE/WebSocket recovery | Phase 42/44 | Evaluate/harden | — |
| Redis degradation behavior | Phase 44 | Evaluate/harden | — |
| Provider portability | Phase 41–44 ports | Quality gate | — |

---

# 20. Agent anti-bịa rules

```text
1. Do not claim implementation without code and tests.
2. Do not bypass IAM, resource authorization, masking, governance or baseline rules.
3. Do not treat Elasticsearch, LLM output, suggestion or conversation memory as source of truth.
4. Do not store/expose hidden reasoning.
5. Do not hide partial failure, insufficient evidence, stale state, provider outage or deferred gaps.
6. Do not activate adapters/tools/providers that do not exist and pass tests.
7. Do not claim external side effects without real provider delivery result.
```

---

# 21. Final principle

Phase 45 is complete when:

```text
versioned evaluation
+ measurable quality
+ mandatory safety gates
+ controlled rollout
+ cost/latency limits
+ observability
+ incident/kill switch
+ privacy retention
= production-governed AI Copilot
```
