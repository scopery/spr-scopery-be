# PHASE 23 — TO-BE Core Hardening, QA, Performance, Security, Observability & Production Readiness

> Project: Scopery Backend  
> Phase: 23  
> Document type: TO-BE implementation-grade specification  
> Status: Planning / Gap-driven implementation spec  
> Depends on: Phase 00 — Master Backend Roadmap, Phase 01 — API Path / Security Baseline, Phase 02 — IAM Core, Phase 03 — Workspace / Organization / Team, Phase 04 — Platform Audit / Outbox / Idempotency, Phase 05 — Event Registry, Phase 06 — Notification Engine, Phase 07 — AI Agent Platform, Phase 08 — Knowledge / Document Type Catalog, Phase 09 — Project Core / WBS / Task, Phase 10 — Project Authorization, Phase 11 — Project Template / Phase Catalog, Phase 12 — Resource Calendar / Capacity, Phase 13 — Task Scheduling Engine, Phase 14 — WBS-driven Gantt, Phase 15 — Rate Card / Cost Policy, Phase 16 — Estimation Roll-up, Phase 17 — Project Budget / Margin, Phase 18 — Quote / Commercial Proposal, Phase 19 — Baseline / Change Request, Phase 20 — Project Events / Notifications, Phase 21 — AI-assisted Project Planning, Phase 22 — Reporting / Dashboard / Export  
> API base: `/api`  
> Primary module: platform-wide / all modules  
> Important rule: Phase 23 is **core hardening and release readiness**. It must not add large new business modules. It validates, strengthens, documents, tests, optimizes, and prepares Phase 00–22 for production-grade operation.

---

# 0. Purpose

Phase 23 is the final hardening phase for the core backend roadmap.

By the end of Phase 22, Scopery should have foundations for:

```text
Identity and IAM
Organization / Workspace / Team
Platform audit / outbox / idempotency
Event registry
Notification engine
AI agent platform
Knowledge/document type catalog
Project / Phase / WBS / Task
Project authorization
Project templates
Resource calendar / capacity
Task scheduling
Gantt projection
Rate card / CCH / inflation
Estimation roll-up
Project finance / margin scenario
Quote / commercial proposal
Baseline / Change Request
Project notifications
AI-assisted project planning
Reporting / dashboard / export
```

Phase 23 answers:

```text
Is the core backend safe?
Is authorization consistently enforced?
Are APIs stable and documented?
Are migrations safe?
Are transactions correct?
Are events/outbox/idempotency reliable?
Are tests broad enough?
Are performance hotspots handled?
Are sensitive fields masked?
Are reports/exports safe?
Are AI actions governed?
Are baseline/change guards enforced?
Can this core be deployed and operated with confidence?
```

Phase 23 is not a feature expansion phase.

It is a **production-readiness gate**.

---

# 1. Source inputs

Before coding Phase 23, the agent must read:

```text
1. Current backend codebase
2. All phase docs Phase 00–22
3. All phase completion files if present
4. Current migrations
5. Current API controllers
6. Current Spring Security configuration
7. Current IAM permission seeders
8. Current EventDefinition seeders
9. Current Notification template/rule seeders
10. Current AI agent/prompt seeders
11. Current tests
12. Current OpenAPI output
13. Current application configuration profiles
14. Current logging/observability configuration
15. Current CI/build scripts
16. Current BE feature/entity/business-rule inventory
```

The agent must not rely only on documentation. It must inspect actual code.

---

# 2. Phase 23 target statement

Phase 23 must deliver:

```text
1. Complete current-vs-TO-BE gap audit for Phase 00–22.
2. Security and IAM hardening across all `/api` endpoints.
3. IDOR/path ownership checks across organization/workspace/project resources.
4. Transaction, outbox, audit, and idempotency verification.
5. Migration and schema integrity review.
6. API contract and OpenAPI cleanup.
7. Performance and query optimization for heavy project/reporting paths.
8. Test suite expansion and regression stabilization.
9. Sensitive data masking and export safety review.
10. AI safety and safe-apply governance review.
11. Baseline/change guard review.
12. Notification spam/dedup/masking review.
13. Observability and operational readiness.
14. Release readiness checklist and final completion report.
```

---

# 3. Phase 23 classification labels

Use the same labels in the completion report:

| Label | Meaning |
|---|---|
| `CURRENTLY_IMPLEMENTED` | Code already satisfies requirement and tests prove it. |
| `PARTIALLY_IMPLEMENTED` | Some coverage exists but gaps remain. |
| `MUST_HARDEN_IN_PHASE_23` | Must be hardened now. |
| `MUST_FIX_IN_PHASE_23` | Bug or critical gap that must be fixed now. |
| `SEED_ONLY_IN_PHASE_23` | Seeder/definition only; consumer later. |
| `DEFERRED_TO_PHASE_XX` | Later phase must return. |
| `DEFERRED_TO_POST_23_BACKLOG` | Full Work OS expansion backlog. |
| `NOT_IN_SCOPE_FOR_CORE_RELEASE` | Explicitly not required for core release. |

---

# 4. Non-negotiable release gates

The core release must not pass if any of these are true:

```text
1. `/api/**` has unintended permitAll.
2. Any protected business endpoint lacks authentication.
3. IDOR exists for project/workspace scoped resources.
4. Controller returns JPA/domain objects directly.
5. Domain imports Spring Web/JPA infrastructure in violation of architecture.
6. Reports/exports leak finance/quote/rate/AI fields.
7. AI can mutate business data without human-approved apply.
8. AI can bypass IAM or baseline/change guard.
9. Approved baseline can be mutated.
10. Published/approved quote/finance/rate versions can be edited.
11. Outbox events are written outside required transaction boundaries.
12. Idempotency endpoints can duplicate critical writes.
13. Flyway migrations fail from clean database.
14. `mvn test` fails.
15. OpenAPI exposes wrong `/api/v1` or deprecated version path.
16. Sensitive secrets/tokens appear in logs, responses, reports, exports, or AI context.
```

---

# 5. Hardening domains

---

## 5.1 HDN-001 — API security baseline review

Classification: `MUST_HARDEN_IN_PHASE_23`

Review:

```text
Spring Security configuration
CSRF rules
CORS rules
public endpoints
auth endpoints
refresh token cookie path
error handler
JWT/session handling
```

Required checks:

```text
1. No `/api/**.permitAll()` except explicitly public auth/health endpoints.
2. CSRF ignore only for intended auth endpoints if stateless API design requires it.
3. Refresh token cookie path remains `/api/iam/auth`.
4. Auth endpoints do not expose user secrets.
5. Logout invalidates refresh cookie/session as designed.
6. Error responses do not leak stack traces.
7. CORS allows only configured origins.
8. Swagger/OpenAPI access follows environment policy.
```

Tests:

```text
unauthenticatedProtectedEndpoint_returns401
publicAuthEndpoint_available
apiPermitAllNotBroad
refreshCookiePath_correct
csrfIgnoreScope_correct
corsOrigin_restricted
```

---

## 5.2 HDN-002 — IAM permission coverage audit

Classification: `MUST_HARDEN_IN_PHASE_23`

Review all modules:

```text
organization
workspace
team
iam resources/grants
project
templates
capacity
schedule
gantt
rate card
estimation
finance
quote
baseline/change
notification
AI planning
report/export
```

Rules:

```text
1. Every protected action maps to permission.
2. Every query applies view permission.
3. Field-sensitive sections apply extra permission.
4. Seeder contains required permissions.
5. Permission codes are stable and documented.
6. No controller directly trusts frontend role flags.
7. Admin/staff/superadmin compatibility rules documented.
```

Tests:

```text
permissionSeeder_idempotent
protectedAction_withoutPermission_forbidden
protectedQuery_withoutPermission_forbidden
financeFields_withoutPermission_masked
quoteFields_withoutPermission_masked
aiFields_withoutPermission_masked
```

---

## 5.3 HDN-003 — IDOR and path ownership hardening

Classification: `MUST_FIX_IN_PHASE_23` if any gap exists.

Review nested endpoints:

```text
/api/workspaces/{workspaceId}/...
/api/projects/{projectId}/...
/api/projects/{projectId}/tasks/{taskId}
/api/projects/{projectId}/finance-scenarios/{scenarioId}
/api/projects/{projectId}/quotes/{quoteId}
/api/projects/{projectId}/baselines/{baselineId}
/api/reports/runs/{runId}
/api/reports/exports/{exportJobId}
```

Rules:

```text
1. Child resource must belong to parent path resource.
2. Workspace-scoped resource must belong to actor's workspace.
3. Project-scoped resource must belong to project path.
4. Cross-workspace access forbidden.
5. Archived resources handled according to policy.
6. Query filters must not allow access outside actor scope.
```

Tests:

```text
taskFromOtherProject_rejected
wbsFromOtherProject_rejected
financeScenarioFromOtherProject_rejected
quoteFromOtherProject_rejected
baselineFromOtherProject_rejected
changeRequestFromOtherProject_rejected
reportRunFromOtherUserOrWorkspace_rejected
exportDownloadCrossWorkspace_rejected
```

---

## 5.4 HDN-004 — DDD architecture boundary review

Classification: `MUST_HARDEN_IN_PHASE_23`

Architecture rules:

```text
http → application → domain
infrastructure → domain
domain must not import Spring Web / JPA / controller DTO
controller must not return JPA/domain entity directly
application actions own transactions
read services return DTO/read models
```

Checks:

```text
1. Domain packages do not import `jakarta.persistence`.
2. Domain packages do not import Spring MVC/Web.
3. Controllers return response DTOs.
4. JPA entities isolated in infrastructure/persistence if architecture requires.
5. No circular module dependencies.
6. Common/platform code does not depend on business modules.
```

Tests/tooling:

```text
ArchUnit tests or package dependency tests
controllerResponseDto tests
moduleDependency tests
```

---

## 5.5 HDN-005 — Transaction boundary review

Classification: `MUST_HARDEN_IN_PHASE_23`

Review write actions:

```text
CreateProjectAction
CreateTaskAction
UpdateTaskAction
ScheduleRunAction
CreateFinanceScenarioAction
CreateQuoteAction
ApproveBaselineAction
ApplyChangeRequestAction
AIApplySuggestionAction
ReportExportAction
```

Rules:

```text
1. Writes use application action transaction boundary.
2. Partial business writes do not commit on failure.
3. Outbox/audit/event records participate in correct transaction.
4. Apply ChangeRequest uses transactional all-or-nothing by default.
5. Report export job status transitions are consistent.
6. Notification reminder jobs handle partial failures safely.
```

Tests:

```text
businessWriteFailure_rollsBack
changeRequestApplyFailure_rollsBack
outboxCreatedWithBusinessWrite
auditCreatedWithSensitiveAction
reportExportFailure_marksFailed
```

---

## 5.6 HDN-006 — Outbox and event reliability

Classification: `MUST_HARDEN_IN_PHASE_23`

Review:

```text
outbox table
event publication
event definitions
notification rule consumers
retry/dead-letter behavior
idempotency of consumers
```

Rules:

```text
1. Events reference ACTIVE EventDefinition.
2. Event variables match registry.
3. Outbox rows are written transactionally.
4. Failed dispatch retried or dead-lettered.
5. Consumers are idempotent.
6. Event payloads do not include secrets.
7. Sensitive values are masked where needed.
```

Tests:

```text
eventDefinitionSeeder_idempotent
eventPayloadVariables_valid
outboxRetry_success
outboxConsumer_idempotent
notificationConsumer_noDuplicateDelivery
eventPayload_noSecrets
```

---

## 5.7 HDN-007 — Idempotency review

Classification: `MUST_HARDEN_IN_PHASE_23`

Critical endpoints:

```text
create project/template apply
create task/dependency
schedule recalculation
create estimation run
create finance scenario
approve/mark current finance
create quote/version
submit/approve quote
create/approve/apply baseline/change request
create AI planning run
apply AI suggestion
create report run
create export job
```

Rules:

```text
1. Critical POST actions accept idempotency key if designed.
2. Same key returns same result or safe conflict.
3. Same key cannot duplicate financial/commercial/governance records.
4. Idempotency scope includes actor + endpoint + tenant/workspace.
5. Idempotency records have expiration/retention policy.
```

Tests:

```text
sameKeyCreateFinanceScenario_noDuplicate
sameKeyApproveQuote_noDuplicate
sameKeyApplyChangeRequest_noDuplicate
sameKeyCreateReportExport_noDuplicate
sameKeyDifferentActor_rejectedOrSeparateByPolicy
```

---

## 5.8 HDN-008 — Migration and schema integrity review

Classification: `MUST_HARDEN_IN_PHASE_23`

Checks:

```text
1. Flyway clean migration from empty DB succeeds.
2. All tables have expected constraints.
3. Foreign keys match ownership.
4. Unique constraints prevent duplicates.
5. Indexes exist for common filters.
6. JSONB columns documented.
7. Soft archive fields consistent.
8. Version/optimistic locking fields exist where needed.
9. Decimal fields use BigDecimal-compatible precision.
10. No destructive migration without explicit approval.
```

Commands:

```text
mvn test
mvn -DskipTests package
Flyway clean/migrate in test environment if available
```

Tests:

```text
flywayMigration_cleanDb_success
schemaConstraints_preventDuplicateCurrentScenario
schemaConstraints_preventDuplicateCurrentBaseline
schemaConstraints_preventDuplicateRateLine
schemaConstraints_preventDuplicateReportDefinition
```

---

## 5.9 HDN-009 — API contract and OpenAPI review

Classification: `MUST_HARDEN_IN_PHASE_23`

Checks:

```text
1. API base is `/api`, not `/api/v1` or `/api/v2`.
2. Deprecated endpoints documented or removed.
3. Request/response DTOs documented.
4. Error model consistent.
5. Pagination/sorting/filtering consistent.
6. Date/time formats consistent.
7. Currency/decimal formats consistent.
8. Sensitive fields not documented as generally visible.
9. OpenAPI generation succeeds.
```

Tests:

```text
openApi_containsApiBase
openApi_noApiV1Paths
openApi_financeFields_permissionDocumented
openApi_errorModel_consistent
```

---

## 5.10 HDN-010 — Error handling consistency

Classification: `MUST_HARDEN_IN_PHASE_23`

Rules:

```text
1. Business errors use stable error codes.
2. Validation errors are clear.
3. Forbidden vs not found policy documented.
4. No stack trace in API response.
5. Error response includes traceId/correlationId.
6. Sensitive resource existence not leaked when policy says hide.
```

Tests:

```text
validationError_standardShape
forbiddenError_standardShape
notFoundError_standardShape
serverError_noStackTrace
errorResponse_includesTraceId
```

---

## 5.11 HDN-011 — Performance and query optimization

Classification: `MUST_HARDEN_IN_PHASE_23`

High-risk paths:

```text
project dashboard
task list with WBS/phase/status filters
Gantt projection
schedule run
capacity forecast
estimation roll-up
finance summary
baseline snapshot/compare
change impact report
report run/export
notification reminder job
AI context builder
```

Required checks:

```text
1. Avoid N+1 queries.
2. Paginate large lists.
3. Index common filters.
4. Batch insert snapshots/rollups.
5. Stream exports where possible.
6. Limit AI context size.
7. Avoid loading full project graph unnecessarily.
8. Use read models/projections for dashboards.
```

Suggested indexes:

```text
project_id
workspace_id
status
created_at
updated_at
due_date
assignee_user_id
project_phase_id
wbs_node_id
schedule_run_id
estimation_run_id
finance_scenario_id
quote_id
baseline_id
change_request_id
report_run_id
export_job_id
```

Tests:

```text
dashboardLargeProject_performanceBudget
ganttLargeProject_performanceBudget
estimationLargeWbs_noNPlusOne
reportExportLargeCsv_streamsOrBatches
notificationReminderLargeSet_batched
```

---

## 5.12 HDN-012 — Concurrency and optimistic locking

Classification: `MUST_HARDEN_IN_PHASE_23`

Review:

```text
task update
WBS move
dependency create
schedule recalculation
finance scenario approve/current
quote version approve/current
baseline current marker
change request apply
AI suggestion apply
report/export status
```

Rules:

```text
1. Version field or locking strategy applied to mutable aggregate roots.
2. Only one current finance scenario per project.
3. Only one current quote version per quote.
4. Only one current baseline per project.
5. Duplicate dependencies prevented under concurrency.
6. ChangeRequest apply cannot run twice concurrently.
7. Export job status transition safe.
```

Tests:

```text
concurrentMarkCurrentFinance_onlyOneCurrent
concurrentMarkCurrentBaseline_onlyOneCurrent
concurrentApplyChangeRequest_oneSucceeds
concurrentCreateDependency_duplicatePrevented
optimisticLockConflict_returns409
```

---

## 5.13 HDN-013 — Sensitive data and masking review

Classification: `MUST_HARDEN_IN_PHASE_23`

Sensitive data groups:

```text
finance values
quote values
rate card values
AI prompt/context/raw output
client email/contact
notification payloads
export files
tokens/secrets
private capacity/leave details
```

Rules:

```text
1. Field-level permissions enforced.
2. Reports and exports share same masking.
3. AI context builder uses same access policy.
4. Notification payloads masked by recipient permission.
5. Logs do not contain secrets/tokens.
6. Export storage keys not guessable.
7. Download access audited.
```

Tests:

```text
reportMasksFinance_withoutPermission
exportMasksQuote_withoutPermission
aiContextMasksFinance_withoutPermission
notificationMasksMargin_withoutPermission
logsDoNotContainRefreshToken
```

---

## 5.14 HDN-014 — AI safety hardening

Classification: `MUST_HARDEN_IN_PHASE_23`

Review:

```text
AIPlanningRun
AIPlanningContextSnapshot
AIPlanningSuggestion
Safe apply service
AI agent/tool permissions
Prompt templates
Execution log
```

Rules:

```text
1. AI suggestions do not mutate business data.
2. Human review required before apply.
3. Apply uses domain actions.
4. Underlying user permission required.
5. Agent/tool permission required.
6. Baseline guard enforced.
7. Finance/quote/rate masking enforced.
8. Prompt injection mitigation documented.
9. AI output schema validation enforced.
10. AI cannot approve/submit/send/permission grant.
```

Tests:

```text
aiSuggestion_noMutation
aiApply_withoutUserPermission_forbidden
aiApply_withoutAgentPermission_forbidden
aiApply_baselinedProject_createsChangeRequest
aiContext_noUnauthorizedFinance
aiOutputInvalidSchema_rejected
```

---

## 5.15 HDN-015 — Baseline and ChangeRequest guard hardening

Classification: `MUST_HARDEN_IN_PHASE_23`

Rules:

```text
1. Approved baseline immutable.
2. Current baseline enables controlled edit guard.
3. Controlled post-baseline edits route through ChangeRequest.
4. ChangeRequest apply uses domain actions.
5. Apply all-or-nothing unless explicitly configured.
6. Change impact stored.
7. ChangeOrder does not create invoice/contract.
```

Tests:

```text
approvedBaseline_immutable
postBaselineTaskEdit_blocked
postBaselineDependencyCreate_blocked
changeApply_usesDomainActions
changeApplyFailure_rollsBack
changeOrder_noInvoiceNoContract
```

---

## 5.16 HDN-016 — Finance / quote / rate immutability hardening

Classification: `MUST_HARDEN_IN_PHASE_23`

Rules:

```text
1. Published RateCardVersion immutable.
2. Estimation snapshots do not change after rate/task change.
3. Approved/current FinanceScenario immutable.
4. Approved/sent QuoteVersion immutable.
5. Historical snapshots remain stable.
6. BigDecimal used for money/rates.
7. No floating point in finance/rate/quote calculations.
```

Tests:

```text
publishedRateVersion_immutable
estimationSnapshot_stableAfterRateChange
approvedFinanceScenario_immutable
sentQuoteVersion_immutable
moneyCalculations_useBigDecimal
```

---

## 5.17 HDN-017 — Notification reliability hardening

Classification: `MUST_HARDEN_IN_PHASE_23`

Rules:

```text
1. Reminder jobs idempotent.
2. Dedup keys stable.
3. Inactive users excluded.
4. Removed workspace members excluded.
5. Optional preferences/mute respected.
6. Mandatory notifications delivered according to policy.
7. Sensitive payloads masked.
8. Delivery retries safe.
```

Tests:

```text
dueSoonReminder_noDuplicate
overdueReminder_noDuplicate
notificationExcludesInactiveMember
notificationRespectsMute
mandatoryNotificationBypassesMute
notificationPayloadMasked
```

---

## 5.18 HDN-018 — Reporting/export hardening

Classification: `MUST_HARDEN_IN_PHASE_23`

Rules:

```text
1. Reports apply access at query time.
2. Drill-down re-checks access.
3. Snapshots store only authorized/masked data.
4. Export uses masked snapshot.
5. Export download audited.
6. Export file expires or is access-controlled.
7. Project health UNKNOWN when source data missing.
```

Tests:

```text
reportQuery_enforcesAccess
reportDrilldown_enforcesAccess
snapshot_noUnauthorizedUnmaskedData
export_usesMaskedSnapshot
exportDownload_audited
healthMissingSource_unknown
```

---

## 5.19 HDN-019 — Observability and operations

Classification: `MUST_HARDEN_IN_PHASE_23`

Required:

```text
structured logs
traceId/correlationId in requests/errors
metrics for critical jobs
health endpoints
outbox lag metric
notification delivery failure metric
report export failure metric
AI execution failure metric
schedule run failure metric
DB migration status visibility
```

Rules:

```text
1. Logs avoid secrets.
2. Errors include traceId.
3. Critical scheduled jobs log run summary.
4. Slow query paths have instrumentation or logs.
5. Health endpoint does not leak secrets.
```

Tests/checks:

```text
errorResponse_hasTraceId
healthEndpoint_safe
scheduledJob_logsSummary
secretNotLogged
```

---

## 5.20 HDN-020 — CI / build / quality gate

Classification: `MUST_HARDEN_IN_PHASE_23`

Required commands:

```text
mvn compile
mvn test
mvn -DskipTests package
```

Recommended if configured:

```text
mvn verify
dependency vulnerability scan
static analysis
format check
OpenAPI generation check
Flyway migration check
```

Quality gates:

```text
1. Compile passes.
2. Tests pass.
3. Migrations pass.
4. No critical security vulnerability.
5. No ignored failing tests without explanation.
6. Completion file includes exact command output summary.
```

---

# 6. Phase 23 test expansion plan

Minimum test categories:

```text
Security tests
IAM permission tests
IDOR tests
Architecture boundary tests
Transaction rollback tests
Outbox/idempotency tests
Migration/schema tests
OpenAPI contract tests
Performance smoke tests
Concurrency tests
Sensitive masking tests
AI safety tests
Baseline/change guard tests
Finance/quote/rate immutability tests
Notification dedup tests
Reporting/export tests
```

The coding agent must add tests where gaps exist, not only document them.

---

# 7. Required API/endpoint audit matrix

The completion file must include a matrix:

```text
Endpoint
Method
Module
Authenticated?
Permission checked?
Workspace/project ownership checked?
Sensitive fields?
Pagination?
Idempotency needed?
Tests present?
Status
```

At minimum, audit:

```text
/auth
/iam
/workspaces
/projects
/project-phases
/wbs
/tasks
/task-dependencies
/templates
/capacity
/scheduling
/gantt
/rate-card
/estimation
/finance
/quotes
/baselines
/change-requests
/notifications
/ai-planning
/reports
/exports
```

---

# 8. Required seeders audit matrix

The completion file must include:

```text
Seeder
Module
Idempotent?
Definitions created?
Duplicate safe?
Deprecated handling?
Tests present?
Status
```

Review:

```text
IAM permission seeder
EventDefinition seeder
Notification template/rule seeder
AI prompt/agent seeder
Rate cost role seeder
Currency seeder
Report definition seeder
```

---

# 9. Required performance checklist

The completion file must include performance notes for:

```text
large project dashboard
large WBS tree
large task list
schedule recalculation
gantt projection
estimation roll-up
finance recalculation
baseline snapshot
baseline compare
change apply
notification reminder job
AI context builder
report export
```

For each:

```text
data size assumption
query strategy
pagination/batching
indexes
known risks
follow-up if any
```

---

# 10. Required operational checklist

The completion file must confirm:

```text
1. Environment profiles documented.
2. Secrets not committed.
3. Database connection config externalized.
4. CORS config externalized.
5. JWT/session secret externalized.
6. Email provider secrets externalized.
7. AI provider secrets externalized.
8. File/export storage config externalized.
9. Logs structured enough for debugging.
10. Health endpoint safe.
11. Error traceId available.
12. Scheduled jobs can be disabled/configured.
13. Outbox worker can be monitored.
14. Report export storage can be cleaned up.
```

---

# 11. Error catalog hardening

Phase 23 must verify consistent error codes for:

```text
AUTH_*
IAM_*
WORKSPACE_*
PROJECT_*
TASK_*
SCHEDULE_*
GANTT_*
RATE_*
ESTIMATION_*
FINANCE_*
QUOTE_*
BASELINE_*
CHANGE_REQUEST_*
NOTIFICATION_*
AI_PLANNING_*
REPORT_*
EXPORT_*
```

Rules:

```text
1. Error codes stable.
2. No duplicate unrelated meanings.
3. Client-facing messages clear.
4. Sensitive existence hidden where policy requires.
5. Internal details logged with traceId only.
```

---

# 12. Documentation outputs required

Phase 23 must produce or update:

```text
docs/phase-complete/PHASE_23_CORE_HARDENING_QA_PERFORMANCE_TO_BE_COMPLETE.md
docs/security/API_SECURITY_AUDIT.md
docs/security/IAM_PERMISSION_COVERAGE.md
docs/security/IDOR_PATH_OWNERSHIP_AUDIT.md
docs/architecture/MODULE_BOUNDARY_AUDIT.md
docs/testing/CORE_REGRESSION_TEST_MATRIX.md
docs/performance/CORE_PERFORMANCE_REVIEW.md
docs/operations/PRODUCTION_READINESS_CHECKLIST.md
docs/api/OPENAPI_REVIEW_NOTES.md
```

If repository structure differs, equivalent paths are acceptable, but completion file must document.

---

# 13. Completion file

The agent must create:

```text
docs/phase-complete/PHASE_23_CORE_HARDENING_QA_PERFORMANCE_TO_BE_COMPLETE.md
```

Required sections:

```text
# Phase 23 — Core Hardening / QA / Performance Complete

## 1. Summary
## 2. Source Inputs Reviewed
## 3. Phase 00–22 Completion Status Matrix
## 4. Endpoint Security Audit Matrix
## 5. IAM Permission Coverage Matrix
## 6. IDOR / Path Ownership Audit
## 7. Architecture Boundary Audit
## 8. Transaction / Outbox / Idempotency Audit
## 9. Migration / Schema Audit
## 10. API / OpenAPI Audit
## 11. Error Handling Audit
## 12. Performance Review
## 13. Concurrency Review
## 14. Sensitive Data / Masking Review
## 15. AI Safety Review
## 16. Baseline / Change Guard Review
## 17. Finance / Quote / Rate Immutability Review
## 18. Notification Reliability Review
## 19. Reporting / Export Safety Review
## 20. Observability / Operations Review
## 21. Tests Added
## 22. Commands Run
## 23. Test Results
## 24. Known Remaining Risks
## 25. Deferred Items and Target Phase
## 26. Release Readiness Decision
## 27. Evidence / File References
```

---

# 14. Acceptance criteria

Phase 23 is accepted only if:

```text
1. Phase 00–22 gaps are audited.
2. Critical security gaps fixed.
3. IAM coverage audited and hardened.
4. IDOR/path ownership tests added.
5. Architecture boundary checks added or documented.
6. Transaction/outbox/idempotency reviewed and critical gaps fixed.
7. Flyway migrations pass from clean DB in test environment or equivalent check documented.
8. OpenAPI reviewed and `/api` base verified.
9. Error handling standardized.
10. Performance hotspots reviewed and critical N+1/pagination/index issues fixed.
11. Concurrency/current-marker rules tested.
12. Sensitive masking verified for notifications, AI, reports, exports.
13. Baseline/change guard verified.
14. Finance/quote/rate immutability verified.
15. Notification reminder/dedup verified.
16. Reporting/export safety verified.
17. Observability/readiness checklist completed.
18. `mvn compile` passes.
19. `mvn test` passes.
20. Completion file exists with evidence.
```

Do not mark complete if:

```text
1. Any critical release gate fails.
2. Tests fail.
3. Security endpoints are unaudited.
4. Known IDOR remains.
5. Finance/quote/rate/AI leakage remains.
6. Approved/published immutable records can be edited.
7. AI can bypass safe apply.
8. Baseline guard can be bypassed.
9. Report/export can leak masked data.
10. Migration state is unknown.
```

---

# 15. Future phases after core release

Phase 23 closes the core roadmap. The full Dynamic Work OS continues after core.

Likely future phases:

```text
Phase 24 — Scope / Deliverable / Formal Acceptance
Phase 25 — RAID / Decision Management
Phase 26 — Quality / Test / Release Management
Phase 27 — Full Document Hub / Proposal & Report Document Generation
Phase 28 — Application Registry / Requirement / Screen Traceability
Phase 29 — External Party / Client CRM
Phase 30 — Customer / External Collaboration Portal
Phase 31 — Meetings / Collaboration
Phase 32 — Search / Navigation / Personal Productivity
Phase 33 — Dynamic Configuration / Schema Studio Expansion
Phase 34 — Workflow / Approval / Automation
Phase 35 — Advanced Notifications / Reminders / Digest
Phase 36 — Contract / Billing / Revenue
Phase 37 — Time / Attendance / Expense / Actual Cost
Phase 38 — Audit / Compliance / Privacy / Retention
Phase 39 — Integration / Import / Export / API
Phase 40 — Service / Support / Maintenance Operations
Phase 41 — Data Quality / Knowledge Graph / Semantic Index
```

---

# 16. Agent anti-bịa rules

The agent must not:

```text
1. Claim production readiness without command/test evidence.
2. Claim all Phase 00–22 complete without audit matrix.
3. Hide failing tests.
4. Ignore security/IDOR gaps.
5. Claim performance is fine without reviewing heavy paths.
6. Claim migrations are safe without running or documenting equivalent check.
7. Claim masking is safe without tests.
8. Claim AI safety without safe apply tests.
9. Claim baseline guard works without direct-edit tests.
10. Claim export is safe without masking/export tests.
11. Add new major business modules in Phase 23.
12. Mark deferred feature as implemented.
```

---

# 17. Prompt to give coding agent

```text
You are implementing Phase 23 — TO-BE Core Hardening, QA, Performance, Security, Observability & Production Readiness.

This is not a feature expansion task.

Before coding, read:
- CLAUDE.md / CLAUDE.ms
- Coding_convention.md
- Phase 00–22 docs
- Phase completion files if present
- Current backend code, migrations, tests, security config, seeders, OpenAPI config

Your task:
1. Audit Phase 00–22 implementation against TO-BE requirements.
2. Classify gaps with the Phase 23 labels.
3. Fix critical security, IAM, IDOR, transaction, migration, masking, AI safety, baseline guard, report/export, and immutability gaps.
4. Add regression tests for all critical gates.
5. Review performance hotspots and add indexes/pagination/batching where needed.
6. Verify seeders are idempotent.
7. Verify `/api` path standard and OpenAPI.
8. Verify observability and production readiness checklist.
9. Run mvn compile, mvn test, and package/migration checks if available.
10. Create docs/phase-complete/PHASE_23_CORE_HARDENING_QA_PERFORMANCE_TO_BE_COMPLETE.md with evidence and release readiness decision.

Do not implement or claim new business modules in Phase 23.
Do not claim production readiness without evidence.
```

---

# 18. Quick tracking matrix

| Area | Phase 23 action |
|---|---|
| API security | Must audit/harden |
| IAM | Must audit/harden |
| IDOR/path ownership | Must test/fix |
| DDD boundaries | Must audit/test |
| Transactions | Must audit/test |
| Outbox/events | Must audit/test |
| Idempotency | Must audit/test |
| Migrations | Must verify |
| OpenAPI | Must review |
| Error model | Must standardize |
| Performance | Must review/harden |
| Concurrency | Must test |
| Sensitive masking | Must test |
| AI safety | Must test |
| Baseline/change guard | Must test |
| Finance/quote/rate immutability | Must test |
| Notification reliability | Must test |
| Reporting/export safety | Must test |
| Observability | Must check |
| Production readiness | Must document |
| New business modules | Not in scope |

---

# 19. Final principle

Phase 23 is not complete when "the app still runs."

Phase 23 is complete when Scopery can prove the core backend is safe enough to operate:

```text
secure APIs
+ enforced IAM
+ IDOR protection
+ reliable transactions
+ idempotent critical writes
+ valid migrations
+ stable events/outbox
+ safe AI
+ immutable approvals
+ masked sensitive data
+ controlled exports
+ passing tests
+ operational visibility
= core release readiness
```

No evidence means not ready.

No tests means not proven.

No masking means not safe.

No audit trail means not trustworthy.
