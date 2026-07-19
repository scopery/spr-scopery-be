# PHASE 43 — TO-BE AI Recommendation, Suggestion Engine, Project Health Insight & Next-best-action Proposal

> Project: Scopery Backend  
> Phase: 43  
> Document type: TO-BE implementation-grade specification  
> Status: Planning / Structured AI recommendation and proposal layer  
> Roadmap group: Advanced AI Assistant & Knowledge Intelligence  
> Depends on: Phase 00–42, with hard runtime dependencies limited to Core Platform and explicitly declared adapters  
> API base: `/api`  
> Primary module: `modules/airecommendation`, `modules/assistant`, or `modules/copilot/suggestion` depending on repository architecture  
> Important rule: Phase 43 produces structured, evidence-backed suggestions. Suggestions are not business mutations and become applicable only through Phase 44.

---

# 0. Purpose

Phase 43 implements Level 3 — Suggest.

Phase 21 already defines project-planning-specific proposals. Phase 43 keeps Phase 21 history and generalizes a common recommendation model across planning, requirements, quality, meetings, resources, governance, documents and support.

Phase 43 answers:

```text
What should change?
Why and based on which evidence?
How confident is the system?
What impact/risk may occur?
Which action could accept/edit/reject/apply it?
Does it require baseline/change request or stronger permission?
```

---

# 1. Product intention and core principle

```text
AI may recommend.
Human remains accountable.
Suggestions are explainable and evidence-backed.
Accept does not equal apply.
Domain validation and authorization remain mandatory.
```

Standalone and modular behavior remains mandatory:

```text
Each capability works with Core Platform and available adapters.
Missing optional modules reduce available sources/tools/packs gracefully.
No optional integration becomes a hidden hard runtime dependency.
```

---

# 2. Source inputs

Before coding Phase 43, the agent must read:

```text
1. Phase 00–42 docs/completion
2. Phase 21 AI Planning
3. Phase 22 project health/reporting
4. Phase 24–31 scope/RAID/quality/documents/requirements/meetings
5. Phase 34–37 governance/notifications/profitability/resources
6. Phase 40 support
7. Phase 41 retrieval and Phase 42 context
8. Actual domain actions, migrations and tests
```

The agent must inspect actual code, migrations, seeders and tests. Documentation alone is not proof of implementation.

---

# 3. Current expected gaps

Likely missing or partial:

```text
AiRecommendationPolicy
AiRecommendationRun
AiSuggestion/AiSuggestionItem
AiSuggestionEvidence/AiSuggestionImpact
AiSuggestionReview/Feedback
AiSuggestionSuppression/dedup/cooldown
NextBestActionDefinition
cross-module detector registry
staleness/expiration handling
```

Every item must be classified as:

```text
CURRENTLY_IMPLEMENTED
PARTIALLY_IMPLEMENTED
MUST_IMPLEMENT_IN_PHASE_43
MUST_HARDEN_IN_PHASE_43
SEED_ONLY_IN_PHASE_43
DEFERRED_TO_PHASE_XX
NOT_IN_SCOPE_FOR_PHASE_43
```

---

# 4. Target statement

Phase 43 must deliver:

```text
1. General recommendation policy/run model
2. Structured common suggestion schema
3. Evidence/citations and confidence
4. Impact and risk classification
5. Review lifecycle generated→viewed→edited→accepted/rejected→expired/superseded
6. Deduplication/suppression/cooldown
7. Next-best-action definitions
8. Project/page/entity/user scoped APIs
9. Initial recommendation packs
10. Compatibility adapter for Phase 21 suggestions
11. Feedback/acceptance analytics
12. No mutation before Phase 44
13. IAM/privacy/events/audit/tests
```

---

# 5. Boundary decisions

## May generate

```text
insight/warning/recommendation
proposed drafts/field changes/links/dependencies
next-best action
reason/evidence/confidence/impact
```
## Does not execute

```text
create/update/delete domain object
send external message
change permission
approve/finalize
apply baseline/change request
destructive bulk update
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

## AiRecommendationPolicy

```text
scope/trigger/source types
agent/prompt refs
confidence/severity/cooldown/limits
allowed types/status/version
```
## AiRecommendationRun

```text
policy/scope/trigger
context/retrieval refs
status/count/model/cost/latency
```
## AiSuggestion

```text
type/category/severity/status
title/summary/reason
confidence/impact/risk
target/expiry/dedup/supersedes/version
```
## AiSuggestionItem

```text
operation/entity/target
schema-bound proposed payload
masked before snapshot
required permission/confirmation/baseline impact
```
## AiSuggestionEvidence

```text
citation/source
evidence type/support strength
fragment reference
```
## AiSuggestionImpact

```text
scope/schedule/cost/revenue/margin/quality/resource/risk/compliance/client visibility
direction/magnitude/source/assumptions
```
## AiSuggestionReview/Feedback

```text
decision/edited payload/reason
helpful/reason/comment/outcome
```
## AiSuggestionSuppression

```text
object/category/project mute
cooldown
non-suppressible critical policy
```
## NextBestActionDefinition

```text
action code/label
entity types/permissions
capability/risk
Phase 44 tool and UI metadata
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

# 6C. Recommendation generation and retrieval integration

Phase 43 must reuse Phase 41 retrieval and Phase 42 context/citation persistence. It must not create a parallel search implementation.

```text
Trigger/event/manual request
→ resolve authorized project scope
→ retrieve current evidence through searchKnowledge/retrieval service
→ run deterministic checks and/or approved model profile
→ normalize into structured AiSuggestion
→ validate evidence/citations/confidence/staleness
→ persist suggestion
→ optionally publish inbox/notification event
```

Each suggestion must persist:

```text
suggestion type and version
workspace/project/resource scope
reason and user-visible explanation
evidence source IDs/versions/chunks
confidence method/value
impact method/value
recommended action code
suggestion status
created/observed/expires/stale timestamps
policy/profile/model/tool versions
```

A suggestion generated from conversation context must link to the originating conversation/message/turn, but the suggestion record remains independently reviewable and permission-aware.

## Realtime delivery

Suggestion generation may reuse SSE when initiated interactively from chat or UI. Background-generated suggestions are delivered through durable inbox/notification records; WebSocket is not required for suggestion persistence.

SSE event examples for interactive generation:

```text
suggestion.started
suggestion.evidence.completed
suggestion.candidate
suggestion.completed
suggestion.failed
```

## Storage usage

Large source attachments remain in MinIO/R2. Suggestions store only source references, citations and bounded derived explanations in PostgreSQL. Elasticsearch remains a derived retrieval index.

---

---

# 7. Architecture and processing flow

```text
Trigger
→ resolve policy
→ permission-aware context
→ deterministic detectors
→ retrieve evidence
→ optional LLM analysis
→ schema/target/permission validation
→ deterministic impact where possible
→ dedup/suppress
→ persist suggestion
→ work inbox/digest
```

Initial packs:

```text
Planning/task: missing tasks, estimates, owners, dependencies, blocked mitigation.
Requirement/quality: missing test or app/screen/API trace, release blockers.
Meeting: missing action owner/date, probable decision/risk, stale task status.
Resource/schedule: overload, capacity shortage, critical path risk.
Governance/document: outdated current link, baseline variance, visibility concern.
Support: convert to defect/change request/problem/knowledge item.
```

---

# 8. API contract

Required API examples:

```text
POST /api/ai-recommendations/runs
GET /api/ai-recommendations/runs/{runId}
GET /api/ai-recommendations/projects/{projectId}
GET /api/ai-recommendations/entities/{entityType}/{entityId}
GET /api/ai-recommendations/next-best-actions
GET /api/ai-recommendations/suggestions/{suggestionId}
POST /api/ai-recommendations/suggestions/{suggestionId}/view
POST /api/ai-recommendations/suggestions/{suggestionId}/accept
POST /api/ai-recommendations/suggestions/{suggestionId}/reject
PATCH /api/ai-recommendations/suggestions/{suggestionId}/edit
POST /api/ai-recommendations/suggestions/{suggestionId}/suppress
POST /api/ai-recommendations/suggestions/{suggestionId}/feedback
POST /api/ai-recommendations/suggestions/{suggestionId}/prepare-apply
```

Controllers must map Request → Command/QueryService and return DTOs, never JPA/domain aggregates.

---

# 9. IAM and authorization

Required permissions:

```text
AI_RECOMMENDATION_VIEW
AI_RECOMMENDATION_GENERATE
AI_RECOMMENDATION_REVIEW
AI_RECOMMENDATION_EDIT
AI_RECOMMENDATION_ACCEPT
AI_RECOMMENDATION_REJECT
AI_RECOMMENDATION_SUPPRESS
AI_RECOMMENDATION_POLICY_VIEW
AI_RECOMMENDATION_POLICY_MANAGE
AI_RECOMMENDATION_ANALYTICS_VIEW
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
SCOPERY_AI_PHASE_43
```

Required events:

```text
AI_RECOMMENDATION_RUN_REQUESTED
AI_RECOMMENDATION_RUN_SUCCEEDED
AI_RECOMMENDATION_RUN_FAILED
AI_SUGGESTION_GENERATED
AI_SUGGESTION_VIEWED
AI_SUGGESTION_EDITED
AI_SUGGESTION_ACCEPTED
AI_SUGGESTION_REJECTED
AI_SUGGESTION_EXPIRED
AI_SUGGESTION_SUPERSEDED
AI_SUGGESTION_SUPPRESSED
AI_SUGGESTION_READY_TO_APPLY
AI_SUGGESTION_FEEDBACK_SUBMITTED
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
REC-001 Suggestion belongs to one workspace/project scope.
REC-002 Evidence must be accessible to viewer.
REC-003 Suggestion never grants source access.
REC-004 Suggestion is not mutation.
REC-005 Proposed payload must match registered schema.
REC-006 Confidence is not certainty.
REC-007 Numeric impact needs deterministic source or qualitative label.
REC-008 Duplicates are deduplicated.
REC-009 Material target changes expire/revalidate suggestion.
REC-010 Accept does not equal apply.
REC-011 Suppression cannot hide non-suppressible critical findings.
REC-012 Phase 21 records remain traceable.
```

---

# 13. Error catalog

```text
AI_RECOMMENDATION_POLICY_NOT_FOUND
AI_RECOMMENDATION_POLICY_INACTIVE
AI_RECOMMENDATION_RUN_NOT_FOUND
AI_RECOMMENDATION_RUN_FAILED
AI_RECOMMENDATION_CONTEXT_ACCESS_DENIED
AI_SUGGESTION_NOT_FOUND
AI_SUGGESTION_ACCESS_DENIED
AI_SUGGESTION_INVALID_STATUS
AI_SUGGESTION_STALE
AI_SUGGESTION_SCHEMA_INVALID
AI_SUGGESTION_TARGET_NOT_FOUND
AI_SUGGESTION_EVIDENCE_MISSING
AI_SUGGESTION_EVIDENCE_ACCESS_DENIED
AI_SUGGESTION_DUPLICATE
AI_SUGGESTION_SUPPRESSION_FORBIDDEN
AI_SUGGESTION_PREPARE_APPLY_UNAVAILABLE
AI_SUGGESTION_IMPACT_UNVERIFIED
```

Use module-specific error catalogs. Do not throw generic business exceptions or leak provider/internal stack details.

---

# 14. Required tests

```text
generateRequirementCoverageSuggestion_success
generateTaskDependencySuggestion_withEvidence
deterministicFinding_doesNotRequireLlm
generatedSuggestion_schemaValid
suggestionWithoutEvidence_rejectedUnlessHeuristicLabeled
suggestionViewerCannotSeeRestrictedEvidence
financeSuggestion_masksSensitiveValues
acceptSuggestion_doesNotMutateDomain
editSuggestion_preservesOriginalAndAudit
materialTargetChange_expiresSuggestion
duplicateSuggestion_deduplicated
suppressedSuggestion_notRegeneratedDuringCooldown
planningSuggestion_mapsToCommonContract
historicalPlanningSuggestion_remainsReadable
recommendationRun_idempotent
partialDetectorFailure_recordsPartialStatus
```

Mandatory build gates:

```bash
mvn compile
mvn test
```

---

# 15. Manual verification checklist

```text
1. Generate project suggestions for incomplete requirements/tasks.
2. Verify reason, evidence, confidence and impact.
3. Accept a suggestion and confirm no domain mutation.
4. Reject/suppress and verify cooldown.
5. Change the target and verify expiration/staleness.
6. Test restricted finance/resource evidence.
7. Generate meeting and support suggestions.
8. Prepare apply and verify Phase 44 handoff/controlled unavailable result.
```

---

# 16. Acceptance criteria

Phase 43 is accepted only if:

```text
1. Common policy/run/suggestion model implemented
2. Items/evidence/confidence/impact structured
3. Review/edit/accept/reject/expire/suppress lifecycle implemented
4. Dedup and stale target handling implemented
5. Initial recommendation packs classified/implemented
6. Phase 21 integrated without history loss
7. Accept never mutates domain
8. Prepare-apply safely hands off to Phase 44
9. IAM/masking/audit/events implemented
10. Compile/test pass and completion file exists
```

Do not mark complete when tests fail, access can leak, or a deferred capability is merely claimed.

---

# 17. Required phase completion file

Agent must create:

```text
docs/phase-complete/PHASE_43_AI_RECOMMENDATION_SUGGESTION_ENGINE_TO_BE_COMPLETE.md
```

Required sections:

```text
# Phase 43 — Complete

## 1. Summary
## 2. Inputs Reviewed
## 3. Current vs TO-BE
## 4. Implemented/Hardened
## 5. Phase 21 Compatibility
## 6. Boundary
## 7. Entity Mapping
## 8. Policy/Trigger
## 9. Suggestion Schema
## 10. Evidence/Citations
## 11. Confidence/Impact
## 12. Lifecycle/Staleness
## 13. Dedup/Suppression
## 14. Recommendation Packs
## 15. API Changes
## 16. Permissions/Masking
## 17. Events/Notifications/Inbox
## 18. Audit/Idempotency
## 19. Tests/Results
## 20. Manual Verification
## 21. Assumptions/Deviations/Risks
## 22. Retrieval/Chat Integration
## 23. Interactive SSE Suggestion Delivery
## 24. Storage and Evidence References

```

---

# 18. Prompt to give coding agent

```text
You are implementing Phase 43 — TO-BE AI Recommendation, Suggestion Engine, Project Health Insight & Next-best-action Proposal.

This is not an as-is documentation task.

Before coding:
- Read CLAUDE.md / CLAUDE.ms.
- Read Coding_convention.md.
- Read Phase 00–42 docs and completion files.
- Inspect current backend code, migrations, seeders and tests.

Your task:
1. Classify current recommendation capability.
2. Implement policy/run/suggestion/items/evidence/impact/review/suppression/next-best-action.
3. Adapt Phase 21 suggestions to common contract.
4. Implement initial recommendation packs where modules exist.
5. Implement confidence, impact, dedup, cooldown and staleness.
6. Reuse Phase 41 retrieval and Phase 42 context/citation persistence; do not build parallel search.
7. Support optional interactive SSE delivery while persisting durable suggestions.
8. Ensure accept/edit/reject never mutate domain.
9. Add prepare-apply handoff to Phase 44.
10. Add IAM/events/audit/inbox/tests and run compile/test.

Do not implement or claim capabilities outside the explicit Phase 43 boundary.
```

---

# 19. Quick tracking matrix

| Capability | Current backend | Phase action | Later |
|---|---|---|---|
| AI planning suggestions | Phase 21 | Reuse/adapt | Phase 44 applies |
| General suggestion model | Missing | Must implement | — |
| Evidence/citations | Phase 41/42 | Integrate | — |
| Confidence/impact | Partial/unknown | Must implement | Phase 45 evaluates |
| Review/dedup/suppression | Partial/missing | Generalize | — |
| Next-best action | Missing | Must implement | Phase 44 tool mapping |
| General apply | Limited Phase 21 | Defer | Phase 44 |
| Outcome evaluation | Missing | Defer | Phase 45 |

| Interactive suggestion SSE | Missing | Optional delivery + durable persistence | Phase 45 hardens |
| Parallel suggestion search | Not allowed | Reuse Phase 41/42 | — |

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

Phase 43 is complete when:

```text
structured suggestion
+ accessible evidence
+ confidence and impact
+ lifecycle
+ dedup/staleness
+ human review
+ no mutation
= trustworthy next-best action
```
