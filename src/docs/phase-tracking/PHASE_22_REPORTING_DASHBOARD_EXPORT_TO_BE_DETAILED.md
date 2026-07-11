# PHASE 22 — TO-BE Reporting, Project Dashboard, KPI Snapshot, Export & Access-controlled Analytics Foundation

> Project: Scopery Backend  
> Phase: 22  
> Document type: TO-BE implementation-grade specification  
> Status: Planning / Gap-driven implementation spec  
> Depends on: Phase 00–21  
> API base: `/api`  
> Primary module: `modules/reporting` or `modules/project/reporting`  
> Important rule: Phase 22 is reporting/export foundation only. It does not implement data warehouse, external BI, client portal dashboard, real-time streaming analytics, PDF document generation, scheduled digest delivery, or autonomous AI reporting.

---

# 0. Purpose

Phase 22 creates Scopery's project reporting foundation.

It consumes source data from:

```text
Project / Phase / WBS / Task
Resource capacity
Task scheduling
Gantt projection
Rate card
Estimation roll-up
Finance scenario
Quote
Baseline / Change Request
Project Notifications
AI planning suggestions
```

Phase 22 answers:

```text
What is current project health?
Which tasks are late, blocked, at risk, or unscheduled?
What are the current effort/cost/revenue/margin KPIs?
How does live project differ from baseline?
Which ChangeRequests affected scope, schedule, cost, revenue, or quote?
Which reports can be exported safely?
How are finance/quote/rate/AI fields masked?
How are report runs and exports audited?
```

Phase 22 does **not** mutate project data. Reports are read models.

---

# 1. Required source inputs

Before coding, the agent must read:

```text
1. Current backend codebase
2. Phase 09 Project Core implementation
3. Phase 12 Capacity implementation
4. Phase 13 Scheduling implementation
5. Phase 14 Gantt implementation
6. Phase 15 Rate Card implementation
7. Phase 16 Estimation implementation
8. Phase 17 Finance implementation
9. Phase 18 Quote implementation
10. Phase 19 Baseline / Change Request implementation
11. Phase 20 Notifications implementation
12. Phase 21 AI Planning implementation
13. Phase 02 IAM implementation
14. Phase 04 Audit / Outbox / Idempotency implementation
15. Phase 05 Event Registry implementation
```

The agent must not assume the implementation state.

---

# 2. Target outcome

Phase 22 must deliver:

```text
1. Project dashboard summary API.
2. Project health/KPI calculation.
3. ReportDefinition registry.
4. ReportRun execution record.
5. ReportSnapshot immutable output.
6. ReportExportJob for controlled exports.
7. Required project reports:
   - task risk
   - schedule risk
   - capacity
   - estimation
   - finance
   - quote
   - baseline vs current
   - change impact
   - notification attention
   - AI planning
8. Access-aware report query services.
9. Field-level masking for finance/quote/rate/AI fields.
10. CSV export foundation.
11. Event/audit/outbox integration.
12. Completion report with current vs TO-BE matrix.
```

---

# 3. Classification labels

Use these labels in the completion file:

| Label | Meaning |
|---|---|
| `CURRENTLY_IMPLEMENTED` | Existing backend already supports this and tests prove it. |
| `PARTIALLY_IMPLEMENTED` | Some parts exist, but gap remains. |
| `MUST_IMPLEMENT_IN_PHASE_22` | Must be implemented now. |
| `SEED_ONLY_IN_PHASE_22` | Seed definitions/events/permissions now; full consumer later. |
| `DEFERRED_TO_PHASE_XX` | Later phase must return. |
| `DEFERRED_TO_POST_23_BACKLOG` | Outside core 00–23. |
| `NOT_IN_SCOPE_FOR_BACKEND_NOW` | Explicitly not backend scope now. |

---

# 4. Reporting boundaries

## 4.1 Reports are not source of truth

Source data remains in business modules. Reports must not mutate:

```text
Project
Task
ScheduleRun
EstimationRun
FinanceScenario
QuoteVersion
Baseline
ChangeRequest
Notification
AIPlanningSuggestion
```

## 4.2 Snapshots preserve output

`ReportSnapshot` preserves what was generated for an actor at a point in time. It is immutable and must not store unmasked data that the actor was not allowed to see.

## 4.3 Export is controlled disclosure

Export must apply the same permissions and masking as the API.

Rules:

```text
1. Export job stores actor, filters, selected fields, report code, format, and masking summary.
2. Export download is authorized and audited.
3. Export file access should expire where storage supports it.
4. Finance/quote/rate/AI export requires stronger permissions.
```

---

# 5. Required capabilities

## 5.1 RPT-001 — Project dashboard summary

Classification: `MUST_IMPLEMENT_IN_PHASE_22`

API:

```text
GET /api/projects/{projectId}/dashboard
GET /api/projects/{projectId}/dashboard/health
GET /api/projects/{projectId}/dashboard/kpis
GET /api/projects/{projectId}/dashboard/attention
```

Sections:

```text
project overview
task status
schedule health
capacity health
estimation summary
finance summary
quote summary
baseline/change status
notification attention
AI suggestion attention
```

Rules:

```text
1. Project access required.
2. Section-level permissions apply.
3. Unauthorized sections are omitted or masked.
4. Missing source data returns UNKNOWN, not fake success.
5. Dashboard must not recalculate finance/schedule/quote.
```

## 5.2 RPT-002 — Project health score

Classification: `MUST_IMPLEMENT_IN_PHASE_22`

Status:

```text
GREEN
YELLOW
RED
UNKNOWN
```

Recommended rules:

```text
RED when schedule run failed, blocker scheduling issue exists, project is overdue, or critical approved change is unapplied.
YELLOW when due-date gaps exist, tasks are overdue, estimates/rates unresolved, CRs await approval, or quote is near expiry.
GREEN only when required source data exists and no major issues exist.
UNKNOWN when required source data is missing.
```

The formula version must be returned.

## 5.3 RPT-003 — Task risk report

Classification: `MUST_IMPLEMENT_IN_PHASE_22`

Metrics:

```text
totalTasks
todoTasks
inProgressTasks
blockedTasks
completedTasks
cancelledTasks
overdueTasks
dueSoonTasks
unscheduledTasks
atRiskTasks
tasksWithoutEstimate
tasksWithoutAssignee
tasksChangedAfterBaseline
```

Archived tasks excluded by default.

## 5.4 RPT-004 — Schedule risk report

Classification: `MUST_IMPLEMENT_IN_PHASE_22`

Metrics:

```text
currentScheduleRunId
forecastStartDate
forecastFinishDate
scheduledTasks
unscheduledTasks
atRiskTasks
overdueTasks
dueDateCapacityGapTotal
issueCountBySeverity
dependencyCycleCount
noCapacityTaskCount
```

Use existing ScheduleRun and SchedulingIssue. Do not recalculate schedule in report.

## 5.5 RPT-005 — Capacity report

Classification: `MUST_IMPLEMENT_IN_PHASE_22`

Metrics:

```text
allocatedUsers
averageAllocationPercent
overAllocatedUsers
allocatedCapacityHours
scheduledHours
capacityGapHours
userCapacityRows
```

Rules:

```text
1. Requires capacity report permission.
2. Private leave/time-off details must not be exposed.
3. User-level detail can be masked if actor lacks detail permission.
```

## 5.6 RPT-006 — Estimation report

Classification: `MUST_IMPLEMENT_IN_PHASE_22`

Metrics:

```text
currentEstimationRunId
totalEstimateHours
totalLaborCost
totalBillingPreview
unestimatedTaskCount
unresolvedRoleTaskCount
unresolvedRateTaskCount
estimateByPhase
estimateByWbs
estimateByCostRole
```

Rules:

```text
1. Use stored EstimationRun snapshots.
2. Do not recalculate rates.
3. Salary/payroll must never appear.
4. Rate fields require rate permission.
```

## 5.7 RPT-007 — Finance report

Classification: `MUST_IMPLEMENT_IN_PHASE_22`

Metrics:

```text
currentFinanceScenarioId
plannedRevenue
directCost
laborCost
customCost
vendorCost
contingency
overhead
budgetOfCosts
grossMargin
grossMarginPercent
profitBeforeTax
pbtPercent
phaseFinanceBreakdown
```

Rules:

```text
1. Requires PROJECT_FINANCE_REPORT_VIEW or equivalent.
2. Margin fields require margin permission if separated.
3. Actual cost/tax are excluded unless later modules exist.
```

## 5.8 RPT-008 — Quote report

Classification: `MUST_IMPLEMENT_IN_PHASE_22`

Metrics:

```text
currentQuoteVersionId
quoteStatus
totalQuotedAmount
targetMarginPercent
discountAmount
grossMarginPercent
pbtPercent
validUntil
sentAt
acceptedAt
```

Rules:

```text
1. Requires QUOTE_REPORT_VIEW or equivalent.
2. Amount/margin/discount fields can be masked.
3. Accepted quote is not revenue recognition.
```

## 5.9 RPT-009 — Baseline vs current report

Classification: `MUST_IMPLEMENT_IN_PHASE_22`

Comparison:

```text
scope changes
task count delta
estimate hours delta
schedule finish delta
finance delta
quote delta
change request count
approved change impact
```

Rules:

```text
1. Use current baseline by default.
2. Finance and quote deltas masked without permission.
3. Detailed diff can be foundation-level in Phase 22.
4. No mutation occurs.
```

## 5.10 RPT-010 — Change impact report

Classification: `MUST_IMPLEMENT_IN_PHASE_22`

Metrics:

```text
changeRequestCount
submittedCount
approvedCount
rejectedCount
appliedCount
pendingApprovalCount
totalScheduleImpactDays
totalEstimateHoursImpact
totalCostImpact
totalRevenueImpact
totalMarginImpact
changeOrderCount
```

Finance/quote impacts masked without permission.

## 5.11 RPT-011 — Notification attention report

Classification: `MUST_IMPLEMENT_IN_PHASE_22`

Metrics:

```text
unreadNotifications
criticalAlerts
failedDeliveries
dueSoonAlerts
overdueAlerts
scheduleRiskAlerts
changeRequestAlerts
```

User sees own notifications by default. Admin delivery analytics are deferred.

## 5.12 RPT-012 — AI planning report

Classification: `MUST_IMPLEMENT_IN_PHASE_22`

Metrics:

```text
aiPlanningRuns
generatedSuggestions
acceptedSuggestions
rejectedSuggestions
appliedSuggestions
failedApplyCount
pendingReviewCount
```

AI prompt/raw output requires AI permission. Finance/quote AI outputs inherit source permissions.

---

# 6. Entity model TO-BE

## 6.1 ReportDefinition — `report_definition`

Fields:

```text
id UUID PK
code VARCHAR(150) NOT NULL UNIQUE
name VARCHAR(255) NOT NULL
description TEXT NULL
scope VARCHAR(50) NOT NULL
report_type VARCHAR(100) NOT NULL
required_permissions_json JSONB NOT NULL
supported_formats_json JSONB NOT NULL
default_filters_json JSONB NULL
sensitive_fields_json JSONB NULL
status VARCHAR(50) NOT NULL
created_at / created_by
updated_at / updated_by
version INT
```

Status:

```text
ACTIVE
INACTIVE
DEPRECATED
ARCHIVED
```

## 6.2 ReportRun — `report_run`

Fields:

```text
id UUID PK
report_definition_id UUID NOT NULL
workspace_id UUID NULL
project_id UUID NULL
actor_user_id UUID NOT NULL
status VARCHAR(50) NOT NULL
filters_json JSONB NULL
selected_fields_json JSONB NULL
access_summary_json JSONB NULL
masking_summary_json JSONB NULL
result_summary_json JSONB NULL
error_code VARCHAR(150) NULL
error_message TEXT NULL
started_at TIMESTAMP NULL
completed_at TIMESTAMP NULL
created_at TIMESTAMP NOT NULL
trace_id VARCHAR(100) NULL
version INT
```

Status:

```text
PENDING
RUNNING
COMPLETED
FAILED
CANCELLED
```

## 6.3 ReportSnapshot — `report_snapshot`

Fields:

```text
id UUID PK
report_run_id UUID NOT NULL
report_definition_id UUID NOT NULL
workspace_id UUID NULL
project_id UUID NULL
actor_user_id UUID NOT NULL
snapshot_type VARCHAR(100) NOT NULL
data_json JSONB NOT NULL
summary_json JSONB NULL
masking_summary_json JSONB NULL
generated_at TIMESTAMP NOT NULL
created_at TIMESTAMP NOT NULL
version INT
```

Rules:

```text
1. Immutable after creation.
2. Stores only what actor was allowed to see.
```

## 6.4 ReportExportJob — `report_export_job`

Fields:

```text
id UUID PK
report_run_id UUID NULL
report_snapshot_id UUID NULL
report_definition_id UUID NOT NULL
workspace_id UUID NULL
project_id UUID NULL
actor_user_id UUID NOT NULL
format VARCHAR(50) NOT NULL
status VARCHAR(50) NOT NULL
file_name VARCHAR(255) NULL
file_mime_type VARCHAR(100) NULL
file_size_bytes BIGINT NULL
storage_key VARCHAR(500) NULL
download_expires_at TIMESTAMP NULL
filters_json JSONB NULL
selected_fields_json JSONB NULL
masking_summary_json JSONB NULL
error_code VARCHAR(150) NULL
error_message TEXT NULL
created_at TIMESTAMP NOT NULL
completed_at TIMESTAMP NULL
trace_id VARCHAR(100) NULL
version INT
```

Formats:

```text
CSV
XLSX optional
JSON optional
PDF deferred
```

## 6.5 ProjectKpiSnapshot — `project_kpi_snapshot`

Recommended for dashboard performance.

Fields:

```text
id UUID PK
project_id UUID NOT NULL
workspace_id UUID NOT NULL
snapshot_date DATE NOT NULL
health_status VARCHAR(50) NOT NULL
health_score DECIMAL(8,4) NULL
kpi_json JSONB NOT NULL
source_refs_json JSONB NULL
formula_version VARCHAR(50) NOT NULL
created_at TIMESTAMP NOT NULL
trace_id VARCHAR(100) NULL
```

This is derived data, not source of truth.

---

# 7. API TO-BE list

## 7.1 Dashboard APIs

```text
GET /api/projects/{projectId}/dashboard
GET /api/projects/{projectId}/dashboard/health
GET /api/projects/{projectId}/dashboard/kpis
GET /api/projects/{projectId}/dashboard/attention
```

## 7.2 Report definition APIs

```text
GET /api/reports/definitions
GET /api/reports/definitions/{code}
```

Admin optional:

```text
POST  /api/reports/definitions
PUT   /api/reports/definitions/{definitionId}
PATCH /api/reports/definitions/{definitionId}/deprecate
```

## 7.3 Report run APIs

```text
POST /api/reports/runs
GET  /api/reports/runs
GET  /api/reports/runs/{reportRunId}
POST /api/reports/runs/{reportRunId}/cancel
GET  /api/reports/runs/{reportRunId}/snapshot
```

Create request:

```json
{
  "reportCode": "PROJECT_TASK_RISK_REPORT",
  "projectId": "uuid",
  "filters": {
    "dateFrom": "2026-08-01",
    "dateTo": "2026-08-31",
    "includeArchived": false
  },
  "selectedFields": ["taskTitle", "assignee", "dueDate", "riskStatus"]
}
```

## 7.4 Project report convenience APIs

```text
GET /api/projects/{projectId}/reports/task-risk
GET /api/projects/{projectId}/reports/schedule-risk
GET /api/projects/{projectId}/reports/capacity
GET /api/projects/{projectId}/reports/estimation
GET /api/projects/{projectId}/reports/finance
GET /api/projects/{projectId}/reports/quote
GET /api/projects/{projectId}/reports/baseline-vs-current
GET /api/projects/{projectId}/reports/change-impact
GET /api/projects/{projectId}/reports/notifications
GET /api/projects/{projectId}/reports/ai-planning
```

## 7.5 Export APIs

```text
POST /api/reports/runs/{reportRunId}/exports
GET  /api/reports/exports
GET  /api/reports/exports/{exportJobId}
GET  /api/reports/exports/{exportJobId}/download
POST /api/reports/exports/{exportJobId}/cancel
```

Export request:

```json
{
  "format": "CSV",
  "fileName": "project-task-risk-report.csv"
}
```

---

# 8. Authorization requirements

Required authorities:

```text
REPORT_DEFINITION_VIEW
REPORT_RUN_CREATE
REPORT_RUN_VIEW
REPORT_RUN_CANCEL
REPORT_SNAPSHOT_VIEW

REPORT_EXPORT_CREATE
REPORT_EXPORT_VIEW
REPORT_EXPORT_DOWNLOAD
REPORT_EXPORT_CANCEL

PROJECT_DASHBOARD_VIEW
PROJECT_HEALTH_VIEW
PROJECT_TASK_REPORT_VIEW
PROJECT_SCHEDULE_REPORT_VIEW
PROJECT_CAPACITY_REPORT_VIEW
PROJECT_ESTIMATION_REPORT_VIEW
PROJECT_FINANCE_REPORT_VIEW
PROJECT_QUOTE_REPORT_VIEW
PROJECT_BASELINE_REPORT_VIEW
PROJECT_CHANGE_REPORT_VIEW
PROJECT_NOTIFICATION_REPORT_VIEW
PROJECT_AI_REPORT_VIEW

REPORT_FINANCE_FIELD_VIEW
REPORT_QUOTE_FIELD_VIEW
REPORT_RATE_FIELD_VIEW
REPORT_AI_FIELD_VIEW
REPORT_ADMIN_MANAGE_DEFINITIONS optional
```

Rules:

```text
1. Project report requires project access.
2. Report section requires module permission.
3. Finance/quote/rate/AI fields require explicit permission.
4. Export requires export permission plus report view permission.
5. Drill-down re-checks permissions.
6. Cross-workspace access is forbidden.
```

---

# 9. Access and masking policy

Access levels:

```text
NO_ACCESS
SUMMARY_ONLY
MASKED
FULL
```

Sensitive fields:

```text
Finance:
plannedRevenue, directCost, laborCost, overhead, grossMargin, pbtPercent

Quote:
totalQuotedAmount, discountAmount, targetMarginPercent, clientEmail

Rate:
costRate, billingRate, rateCardVersion, inflationPolicy

AI:
prompt, rawOutput, contextSnapshot, financeInsight, quoteDraft

Capacity:
private leave/time-off details
```

Rules:

```text
1. If a field is masked in API, it must be masked in export.
2. Snapshot must not store unauthorized unmasked values.
3. Masking summary stored in ReportRun, ReportSnapshot, and ExportJob.
4. Report download must re-check access.
```

---

# 10. Export strategy

## 10.1 CSV export

Required minimum.

Rules:

```text
1. UTF-8.
2. Header row.
3. Already-masked values only.
4. Date/currency formatting consistent.
5. Download authorized and audited.
```

## 10.2 XLSX export

Optional.

Implement only if supported library exists.

If not:

```text
DEFERRED_TO_PHASE_22_FOLLOWUP_OR_PHASE_27_DOCUMENT_HUB
```

## 10.3 PDF export

Deferred unless a real renderer/storage pipeline exists.

Target:

```text
Phase 27 Document Hub
```

Do not fake PDF export.

---

# 11. Report definitions to seed

Seed these definitions idempotently:

```text
PROJECT_DASHBOARD_SUMMARY
PROJECT_HEALTH_REPORT
PROJECT_TASK_RISK_REPORT
PROJECT_SCHEDULE_RISK_REPORT
PROJECT_CAPACITY_REPORT
PROJECT_ESTIMATION_REPORT
PROJECT_FINANCE_REPORT
PROJECT_QUOTE_REPORT
PROJECT_BASELINE_VS_CURRENT_REPORT
PROJECT_CHANGE_IMPACT_REPORT
PROJECT_NOTIFICATION_ATTENTION_REPORT
PROJECT_AI_PLANNING_REPORT
```

Each definition must contain:

```text
required permissions
supported formats
sensitive fields
default filters
status ACTIVE
```

---

# 12. Event Registry integration

Recommended source system:

```text
SCOPERY_REPORTING
```

Events:

```text
REPORT_DEFINITION_SEEDED
REPORT_RUN_CREATED
REPORT_RUN_STARTED
REPORT_RUN_COMPLETED
REPORT_RUN_FAILED
REPORT_RUN_CANCELLED

REPORT_SNAPSHOT_CREATED

REPORT_EXPORT_REQUESTED
REPORT_EXPORT_STARTED
REPORT_EXPORT_COMPLETED
REPORT_EXPORT_FAILED
REPORT_EXPORT_CANCELLED
REPORT_EXPORT_DOWNLOADED
REPORT_EXPORT_EXPIRED

PROJECT_DASHBOARD_VIEWED
PROJECT_HEALTH_CALCULATED
PROJECT_KPI_SNAPSHOT_CREATED

REPORT_ACCESS_DENIED
REPORT_FIELD_MASKED
```

Standard variables:

```text
actor.userId
organization.id
workspace.id
project.id
reportDefinition.code
reportRun.id
reportSnapshot.id
exportJob.id
export.format
health.status
maskedField.count
occurredAt
traceId
```

---

# 13. Audit / activity / outbox

Audit-sensitive actions:

```text
finance report viewed/exported
quote report viewed/exported
rate fields exported
AI prompt/output exported
baseline comparison exported
change impact exported
export downloaded
```

Activity log actions:

```text
REPORT_RUN_CREATED
REPORT_RUN_COMPLETED
REPORT_EXPORT_REQUESTED
REPORT_EXPORT_COMPLETED
REPORT_EXPORT_DOWNLOADED
PROJECT_KPI_SNAPSHOT_CREATED
```

Idempotency recommended for:

```text
POST /api/reports/runs
POST /api/reports/runs/{reportRunId}/exports
```

---

# 14. Business rules master

## 14.1 Report definition rules

```text
RPT-DEF-001 Report code required.
RPT-DEF-002 Report code unique.
RPT-DEF-003 Required permissions configured.
RPT-DEF-004 Sensitive fields configured.
RPT-DEF-005 Deprecated report cannot run.
RPT-DEF-006 Seeder idempotent.
```

## 14.2 Report run rules

```text
RPT-RUN-001 Actor must have required permission.
RPT-RUN-002 Project report requires project access.
RPT-RUN-003 Filters validated.
RPT-RUN-004 Report does not mutate source data.
RPT-RUN-005 Access/masking summary stored.
RPT-RUN-006 Failed report stores error.
```

## 14.3 Snapshot rules

```text
RPT-SNP-001 Snapshot belongs to report run.
RPT-SNP-002 Snapshot immutable.
RPT-SNP-003 Snapshot stores only authorized/masked data.
RPT-SNP-004 Snapshot is not source of truth.
```

## 14.4 Export rules

```text
RPT-EXP-001 Export requires export permission.
RPT-EXP-002 Export uses masked snapshot.
RPT-EXP-003 Export format must be supported.
RPT-EXP-004 Export file access controlled.
RPT-EXP-005 Export download audited.
RPT-EXP-006 Export expires if expiration configured.
```

## 14.5 Dashboard rules

```text
RPT-DSH-001 Dashboard requires project access.
RPT-DSH-002 Unauthorized sections omitted/masked.
RPT-DSH-003 Missing source data produces UNKNOWN.
RPT-DSH-004 Health formula version returned.
RPT-DSH-005 Dashboard does not recalculate finance/schedule.
```

---

# 15. Error catalog

```text
REPORT_DEFINITION_NOT_FOUND
REPORT_DEFINITION_CODE_ALREADY_EXISTS
REPORT_DEFINITION_DEPRECATED
REPORT_DEFINITION_INVALID_SCOPE
REPORT_DEFINITION_INVALID_FORMAT

REPORT_RUN_NOT_FOUND
REPORT_RUN_INVALID_FILTER
REPORT_RUN_ACCESS_DENIED
REPORT_RUN_FAILED
REPORT_RUN_NOT_CANCELLABLE
REPORT_RUN_PROJECT_MISMATCH

REPORT_SNAPSHOT_NOT_FOUND
REPORT_SNAPSHOT_IMMUTABLE
REPORT_SNAPSHOT_ACCESS_DENIED

REPORT_EXPORT_NOT_FOUND
REPORT_EXPORT_FORMAT_NOT_SUPPORTED
REPORT_EXPORT_ACCESS_DENIED
REPORT_EXPORT_FAILED
REPORT_EXPORT_NOT_READY
REPORT_EXPORT_EXPIRED
REPORT_EXPORT_DOWNLOAD_DENIED

PROJECT_DASHBOARD_ACCESS_DENIED
PROJECT_DASHBOARD_SECTION_MASKED
PROJECT_HEALTH_SOURCE_MISSING

REPORT_FINANCE_ACCESS_DENIED
REPORT_QUOTE_ACCESS_DENIED
REPORT_RATE_ACCESS_DENIED
REPORT_AI_ACCESS_DENIED
REPORT_CROSS_WORKSPACE_DENIED
```

---

# 16. Required tests

## 16.1 Report definition tests

```text
reportDefinitionSeeder_firstRun_createsDefinitions
reportDefinitionSeeder_secondRun_noDuplicates
reportDefinition_requiredPermissionsConfigured
reportDefinition_sensitiveFieldsConfigured
deprecatedReport_cannotRun
```

## 16.2 Dashboard tests

```text
projectDashboard_valid_success
projectDashboard_withoutProjectAccess_forbidden
projectDashboard_masksFinanceWithoutPermission
projectDashboard_masksQuoteWithoutPermission
projectDashboard_includesTaskKpis
projectDashboard_includesScheduleKpis
projectDashboard_missingSchedule_healthUnknown
projectHealth_redWhenScheduleFailed
projectHealth_yellowWhenDueDateRisk
projectHealth_greenWhenNoIssues
```

## 16.3 Report run tests

```text
createReportRun_valid_success
createReportRun_invalidReportCode_rejected
createReportRun_withoutPermission_forbidden
createReportRun_crossWorkspace_forbidden
reportRun_storesFilters
reportRun_storesAccessSummary
reportRun_storesMaskingSummary
reportRun_failure_storesError
```

## 16.4 Snapshot tests

```text
reportSnapshot_createdAfterRun
reportSnapshot_immutable
reportSnapshot_doesNotStoreUnauthorizedFinanceFields
reportSnapshot_doesNotStoreUnauthorizedQuoteFields
reportSnapshot_notSourceOfTruth
```

## 16.5 Export tests

```text
exportCsv_valid_success
exportUnsupportedFormat_rejected
exportWithoutPermission_forbidden
exportFinanceWithoutFinancePermission_forbidden
exportUsesMaskedSnapshot
exportDownload_audited
exportDownloadExpired_rejected
exportIdempotency_noDuplicateJob
```

## 16.6 Project reports tests

```text
taskRiskReport_countsOverdueAndAtRisk
scheduleRiskReport_usesCurrentScheduleRun
capacityReport_masksPrivateLeaveDetails
estimationReport_usesCurrentEstimationRun
financeReport_requiresFinancePermission
quoteReport_requiresQuotePermission
baselineVsCurrentReport_masksFinanceDeltaWithoutPermission
changeImpactReport_countsAppliedChanges
notificationAttentionReport_userOwnNotifications
aiPlanningReport_requiresAiPermission
```

## 16.7 Authorization/masking tests

```text
normalProjectViewer_cannotViewFinanceReport
financeViewer_canViewFinanceReport
quoteViewer_canViewQuoteReport
rateFieldsMaskedWithoutRatePermission
aiPromptMaskedWithoutAiPermission
drillDownRechecksPermission
```

## 16.8 Event/audit tests

```text
reportRunCompleted_eventEmitted
reportExportRequested_eventEmitted
reportExportDownloaded_auditCreated
financeReportExport_auditCreated
quoteReportExport_auditCreated
fieldMasked_eventOrLogCreated
```

---

# 17. Manual verification checklist

Completion file must include:

```text
1. Seed report definitions.
2. Open project dashboard as project viewer.
3. Confirm task/schedule sections visible.
4. Confirm finance/quote sections masked without permission.
5. Run task risk report.
6. Run schedule risk report.
7. Run estimation report.
8. Run finance report with finance permission.
9. Run quote report with quote permission.
10. Run baseline vs current report.
11. Run change impact report.
12. Export task report to CSV.
13. Confirm CSV values are masked according to actor permissions.
14. Download export and confirm audit.
15. Confirm no PDF/document export is falsely claimed unless implemented.
16. Confirm no data warehouse/client portal/digest is created.
```

---

# 18. Acceptance criteria

Phase 22 is accepted only if:

```text
1. Current reporting/export capability is classified against TO-BE.
2. Project dashboard implemented/tested.
3. Project health/KPI calculation implemented/tested.
4. ReportDefinition implemented/tested.
5. ReportRun implemented/tested.
6. ReportSnapshot implemented/tested.
7. ReportExportJob implemented/tested.
8. Required project reports implemented/tested.
9. Access/masking policy implemented/tested.
10. CSV export implemented/tested.
11. XLSX/PDF either implemented/tested or explicitly deferred.
12. Finance/quote/rate/AI fields protected.
13. Events seeded idempotently.
14. Activity/audit/outbox follows Phase 04.
15. No data warehouse/BI/client portal/digest/full document export falsely claimed.
16. mvn compile passes.
17. mvn test passes.
18. Completion file exists and includes gap matrix.
```

Do not mark complete if:

```text
mvn test fails
report exposes finance/quote/rate fields without permission
export contains unmasked sensitive values
report snapshot stores unauthorized unmasked values
dashboard marks GREEN when source data missing
drill-down bypasses IAM
PDF export claimed without renderer/storage
data warehouse/client portal/digest is claimed implemented
```

---

# 19. Required phase completion file

Agent must create:

```text
docs/phase-complete/PHASE_22_REPORTING_DASHBOARD_EXPORT_TO_BE_COMPLETE.md
```

Required sections:

```text
# Phase 22 — Reporting / Dashboard / Export TO-BE Complete

## 1. Summary
## 2. Source Inputs Reviewed
## 3. Current vs TO-BE Classification Matrix
## 4. Implemented in Current BE
## 5. Implemented / Hardened in This Phase
## 6. Seed-only Items Added
## 7. Deferred Items and Target Phase
## 8. Reporting Boundary Decision
## 9. Entity Mapping
## 10. API Changes
## 11. Project Dashboard Strategy
## 12. Project Health Formula Version
## 13. Report Definition Seeder Matrix
## 14. Report Run Strategy
## 15. Report Snapshot Strategy
## 16. Export Strategy
## 17. Access / Masking Strategy
## 18. Finance / Quote / Rate / AI Field Protection
## 19. Baseline / Change Report Strategy
## 20. Notification / AI Report Strategy
## 21. Authorization Matrix
## 22. Event Registry Seeder Matrix
## 23. Activity / Audit / Outbox Notes
## 24. Idempotency Strategy
## 25. Tests Added
## 26. Commands Run
## 27. Test Results
## 28. Manual Verification
## 29. Assumptions
## 30. Deviations From Prompt
## 31. Known Risks
## 32. Future Phases That Must Return to Reporting
```

---

# 20. Future phases that must return

```text
Phase 23 — performance, masking, export security, KPI edge cases
Phase 27 — PDF/DOCX report and proposal documents
Phase 35 — scheduled reports, digest, delivery subscriptions
Phase 36 — contract/billing/revenue reports
Phase 37 — actual hours, expenses, planned-vs-actual variance
Phase 38 — audit/compliance/privacy/retention reports
Phase 41 — semantic analytics, knowledge graph, AI-grounded report explanations
Portfolio intelligence backlog — multi-project dashboards and executive scorecards
```

---

# 21. Agent anti-bịa rules

The agent must not:

```text
1. Claim report data is source of truth.
2. Mutate business data from report run.
3. Expose finance/quote/rate/AI fields without permission.
4. Store unauthorized unmasked values in snapshots.
5. Export unmasked values when API masked them.
6. Mark project health GREEN when source data is missing.
7. Claim PDF export without renderer/storage.
8. Claim data warehouse exists.
9. Claim external BI connector exists.
10. Claim client portal dashboard exists.
11. Claim scheduled digest delivery exists.
12. Hide deferred warehouse/document/digest/client portal gaps.
```

---

# 22. Prompt to give coding agent

```text
You are implementing Phase 22 — TO-BE Reporting, Project Dashboard, KPI Snapshot, Export & Access-controlled Analytics Foundation.

This is not an as-is documentation task.

Before coding, read all Phase 00–21 docs and current backend code.

Your task:
1. Compare current reporting/export capability against this Phase 22 TO-BE spec.
2. Classify each capability with the required labels.
3. Implement ProjectDashboardSummary and ProjectHealth/KPI calculation.
4. Implement ReportDefinition, ReportRun, ReportSnapshot, ReportExportJob.
5. Implement required project reports.
6. Implement access/masking policy for finance/quote/rate/AI fields.
7. Implement CSV export and optionally XLSX only if supported.
8. Add report permissions and event definitions.
9. Add tests listed in this spec.
10. Run mvn compile and mvn test.
11. Create docs/phase-complete/PHASE_22_REPORTING_DASHBOARD_EXPORT_TO_BE_COMPLETE.md.

Do not implement or claim data warehouse, external BI connector, client portal dashboard, real-time streaming analytics, PDF document generation, scheduled report digest, Slack/Teams delivery, or AI autonomous reporting in this phase.
```

---

# 23. Final principle

Phase 22 is not complete when "some totals are returned."

Phase 22 is complete when Scopery can safely report project reality:

```text
source modules
+ report definitions
+ access checks
+ field masking
+ report run
+ immutable snapshot
+ controlled export
= trustworthy reporting
```

Reports read.

Reports do not mutate.

Exports disclose only what the actor is allowed to see.

Missing source data means UNKNOWN, not fake success.

Finance, quote, rate, and AI fields are sensitive by default.
