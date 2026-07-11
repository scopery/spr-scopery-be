# PHASE 26 — TO-BE Quality, Test Management, Defect Control, Release Package & Deployment Readiness

> Project: Scopery Backend  
> Phase: 26  
> Document type: TO-BE implementation-grade specification  
> Status: Planning / Gap-driven implementation spec  
> Roadmap group: Post-core Dynamic Work OS expansion  
> Depends on: Phase 00 — Master Backend Roadmap, Phase 01 — API Path / Security Baseline, Phase 02 — IAM Core, Phase 03 — Workspace / Organization / Team, Phase 04 — Platform Audit / Outbox / Idempotency, Phase 05 — Event Registry, Phase 06 — Notification Engine, Phase 09 — Project Core / Phase / WBS / Task, Phase 10 — Project Authorization, Phase 13 — Task Scheduling Engine, Phase 14 — WBS-driven Gantt, Phase 19 — Baseline / Change Request, Phase 20 — Project Events / Notifications, Phase 21 — AI-assisted Project Planning, Phase 22 — Reporting / Dashboard / Export, Phase 23 — Core Hardening, Phase 24 — Scope / Deliverable / Acceptance, Phase 25 — RAID / Decision Management  
> API base: `/api`  
> Primary module: `modules/quality` or `modules/project/quality` depending on repository architecture  
> Related modules: `project`, `task`, `scope`, `deliverable`, `acceptance`, `raid`, `decision`, `baseline`, `change-request`, `notification`, `reporting`, `ai-planning`, `document`, `iam`, `eventregistry`, future `release-ops`, `incident`, `support`, `integration`, `client-portal`  
> Important rule: Phase 26 introduces project quality, test, defect, release, and deployment-readiness management. It does **not** implement a real CI/CD runner, production infrastructure automation, full incident management, external test lab integration, client portal UAT workflow, or service desk operations.

---

# 0. Purpose

Phase 26 adds a quality and release control layer around project deliverables.

Earlier phases created:

```text
Project / WBS / Task
Schedule / Gantt
Baseline / ChangeRequest
Notifications
Reporting
AI planning
Scope / Deliverable / Acceptance
RAID / Decision Management
```

Phase 26 answers:

```text
What needs to be tested?
Which acceptance criteria are covered by tests?
Which test cases exist?
Which test runs were executed?
What failed?
What defects were found?
Which defects block acceptance or release?
What release package is being prepared?
Which deliverables, tasks, defects, and change requests are included in the release?
Is the release ready?
What deployment record exists?
What was deployed, where, when, and by whom?
What rollback plan exists?
```

Phase 26 makes quality and release traceable, but does not perform production deployment automatically.

---

# 1. Source inputs

Before coding Phase 26, the agent must read:

```text
1. Current backend codebase
2. Phase 09 Project Core implementation
3. Phase 10 Project Authorization implementation
4. Phase 19 Baseline / Change Request implementation
5. Phase 20 Notification implementation
6. Phase 21 AI-assisted Planning implementation
7. Phase 22 Reporting implementation
8. Phase 23 Core Hardening completion file
9. Phase 24 Scope / Deliverable / Acceptance implementation
10. Phase 25 RAID / Decision implementation
11. Current IAM seeders
12. Current EventDefinition seeders
13. Current notification rule seeders
14. Existing test/defect/release/deployment code if any
```

The agent must inspect real code, not assume implementation.

---

# 2. Current expected backend state

After Phase 25, the backend should have:

```text
Project
Phase
WBS
Task
Deliverable
AcceptanceCriteria
AcceptanceEvidence
Baseline
ChangeRequest
RaidItem / Issue / Risk
DecisionRecord
Notification
Reporting
AIPlanningSuggestion
```

Likely missing:

```text
QualityPlan
TestPlan
TestSuite
TestCase
TestStep
TestCaseAcceptanceCriteriaMapping
TestRun
TestRunCaseResult
TestStepResult
Defect
DefectLink
DefectLifecycle
ReleasePackage
ReleaseItem
ReleaseReadinessChecklist
DeploymentRecord
DeploymentEnvironment
RollbackPlan
Release report
Quality dashboard
```

Phase 26 implements these foundations.

---

# 3. Target statement

Phase 26 must deliver:

```text
1. QualityPlan per project.
2. TestPlan and TestSuite structure.
3. TestCase and TestStep definitions.
4. Mapping from TestCase to AcceptanceCriteria, Deliverable, WBS, Task, Requirement/Scope where available.
5. TestRun execution records.
6. Test result capture at case and step level.
7. Defect management with severity, priority, status, assignment, reproduction, and resolution.
8. Defect links to test result, task, deliverable, acceptance criteria, release, RAID issue, and ChangeRequest.
9. ReleasePackage and ReleaseItem management.
10. Release readiness checks and blocking rules.
11. DeploymentEnvironment and DeploymentRecord foundation.
12. Rollback plan foundation.
13. Reporting/dashboard extensions.
14. Event/notification integration.
15. AI-assisted quality suggestions as proposal only.
16. IAM permissions, audit/outbox, idempotency, tests, and completion file.
```

---

# 4. Boundary decisions

## 4.1 Quality/Test is not CI/CD runner

Phase 26 can record:

```text
release package
deployment record
deployment status
environment
build/reference
rollback plan
```

But it does not execute real pipelines unless integration exists.

## 4.2 Defect is not RAID issue, but can create/link one

A defect is a product/project quality problem found through testing/review.

A RAID issue is broader governance issue.

Rules:

```text
Defect can create/link RaidItem type ISSUE.
RaidItem can link to defects.
```

## 4.3 TestCase is not AcceptanceCriteria

AcceptanceCriteria defines what must be true.

TestCase verifies it.

A criterion can be covered by multiple test cases.

A test case can cover multiple criteria.

## 4.4 Release is not billing or revenue

A ReleasePackage can later trigger acceptance/billing milestones, but Phase 26 does not create invoice/revenue.

## 4.5 DeploymentRecord is operational record only

DeploymentRecord records what happened or is planned.

It does not perform infrastructure deployment unless integration exists.

---

# 5. Classification labels

Use these labels in the completion file:

| Label | Meaning |
|---|---|
| `CURRENTLY_IMPLEMENTED` | Existing backend already supports it with tests. |
| `PARTIALLY_IMPLEMENTED` | Some support exists but gaps remain. |
| `MUST_IMPLEMENT_IN_PHASE_26` | Required now. |
| `SEED_ONLY_IN_PHASE_26` | Seed definitions/events/permissions only. |
| `DEFERRED_TO_PHASE_XX` | Later phase returns. |
| `DEFERRED_TO_POST_23_BACKLOG` | Dynamic Work OS expansion backlog. |
| `NOT_IN_SCOPE_FOR_PHASE_26` | Explicitly not in this phase. |

---

# 6. Required capabilities

---

## 6.1 QLT-001 — QualityPlan

Classification: `MUST_IMPLEMENT_IN_PHASE_26`

Purpose:

```text
Define project quality strategy and acceptance/testing approach.
```

Rules:

```text
1. QualityPlan belongs to one project.
2. Project must not be archived.
3. Project can have multiple versions/plans, one current plan.
4. DRAFT can be edited.
5. APPROVED/CURRENT plan immutable except archive/revision.
6. QualityPlan is not a test run.
7. QualityPlan can reference baseline/scope package.
```

Status:

```text
DRAFT
READY
APPROVED
CURRENT
ARCHIVED
```

Fields should capture:

```text
qualityObjectives
testStrategy
entryCriteria
exitCriteria
defectSeverityPolicy
releaseReadinessPolicy
approvalNotes
```

---

## 6.2 TST-001 — TestPlan

Classification: `MUST_IMPLEMENT_IN_PHASE_26`

Purpose:

```text
Group test scope and test execution plan for project/release/deliverables.
```

Test levels:

```text
UNIT
INTEGRATION
SYSTEM
UAT
REGRESSION
PERFORMANCE
SECURITY
SMOKE
ACCEPTANCE
OTHER
```

Rules:

```text
1. TestPlan belongs to project.
2. TestPlan can reference QualityPlan.
3. TestPlan can reference ReleasePackage.
4. DRAFT can be edited.
5. APPROVED plan immutable except new version/revision.
6. TestPlan defines included deliverables/scope.
```

Status:

```text
DRAFT
READY
APPROVED
IN_EXECUTION
COMPLETED
ARCHIVED
```

---

## 6.3 TST-002 — TestSuite

Classification: `MUST_IMPLEMENT_IN_PHASE_26`

Purpose:

```text
Organize test cases into logical groups.
```

Examples:

```text
Authentication Suite
Order Management Suite
Payment Integration Suite
Regression Smoke Suite
UAT Suite
```

Rules:

```text
1. TestSuite belongs to TestPlan and project.
2. Suite title required.
3. Suite can contain many TestCases.
4. Suite can map to deliverable/scope item.
5. Archived suite excluded from active runs by default.
```

---

## 6.4 TST-003 — TestCase

Classification: `MUST_IMPLEMENT_IN_PHASE_26`

Purpose:

```text
Define repeatable test scenario with expected result.
```

Test case types:

```text
FUNCTIONAL
NON_FUNCTIONAL
SECURITY
PERFORMANCE
USABILITY
REGRESSION
SMOKE
UAT
API
DATA
OTHER
```

Rules:

```text
1. TestCase belongs to project and optionally TestSuite.
2. TestCase title required.
3. Preconditions and expected result should be captured.
4. TestCase can link to AcceptanceCriteria.
5. TestCase can link to Deliverable, ScopeItem, WBS, Task.
6. TestCase can be versioned or revised.
7. Approved test case immutable except revision.
```

Status:

```text
DRAFT
READY
APPROVED
DEPRECATED
ARCHIVED
```

Priority:

```text
LOW
MEDIUM
HIGH
CRITICAL
```

---

## 6.5 TST-004 — TestStep

Classification: `MUST_IMPLEMENT_IN_PHASE_26`

Purpose:

```text
Break TestCase into ordered steps.
```

Rules:

```text
1. TestStep belongs to TestCase.
2. Step order required.
3. Action required.
4. Expected result required.
5. Reordering audited if approved test case.
6. Archived steps excluded from new runs.
```

---

## 6.6 TST-005 — Acceptance criteria coverage

Classification: `MUST_IMPLEMENT_IN_PHASE_26`

Purpose:

```text
Prove acceptance criteria are covered by tests.
```

Rules:

```text
1. TestCase can cover multiple AcceptanceCriteria.
2. AcceptanceCriteria can be covered by multiple TestCases.
3. Mapping target must belong to same project.
4. Coverage report shows uncovered mandatory criteria.
5. Deliverable acceptance may require mandatory criteria covered and passed if policy enabled.
```

---

## 6.7 TST-006 — TestRun

Classification: `MUST_IMPLEMENT_IN_PHASE_26`

Purpose:

```text
Record execution of test plan/suite/cases.
```

Run types:

```text
MANUAL
AUTOMATED_IMPORTED
MIXED
```

Rules:

```text
1. TestRun belongs to project.
2. TestRun can reference TestPlan, TestSuite, ReleasePackage, environment.
3. TestRun starts PLANNED or IN_PROGRESS.
4. ExecutedBy recorded.
5. Run summary calculated from case results.
6. Completed run immutable except correction with permission/audit.
```

Status:

```text
PLANNED
IN_PROGRESS
COMPLETED
CANCELLED
ARCHIVED
```

---

## 6.8 TST-007 — Test case result

Classification: `MUST_IMPLEMENT_IN_PHASE_26`

Result statuses:

```text
NOT_RUN
PASSED
FAILED
BLOCKED
SKIPPED
RETEST_REQUIRED
```

Rules:

```text
1. Result belongs to TestRun and TestCase.
2. TestCase must belong to same project.
3. Failed result can create Defect.
4. Blocked result can create RaidItem/Issue.
5. Result stores actual result and evidence reference.
6. Completed run results immutable except correction/audit.
```

---

## 6.9 TST-008 — Test step result

Classification: `MUST_IMPLEMENT_IN_PHASE_26`

Rules:

```text
1. StepResult belongs to TestCaseResult and TestStep.
2. StepResult captures actual result.
3. Failed step can contribute to failed case result.
4. Evidence can attach to step result.
```

---

## 6.10 DEF-001 — Defect

Classification: `MUST_IMPLEMENT_IN_PHASE_26`

Purpose:

```text
Track quality problems found in testing/review/delivery.
```

Defect categories:

```text
FUNCTIONAL
UI_UX
API
DATA
PERFORMANCE
SECURITY
INTEGRATION
REGRESSION
DOCUMENTATION
DEPLOYMENT
OTHER
```

Severity:

```text
LOW
MEDIUM
HIGH
CRITICAL
BLOCKER
```

Priority:

```text
LOW
MEDIUM
HIGH
URGENT
```

Status:

```text
OPEN
TRIAGED
ASSIGNED
IN_PROGRESS
FIXED
READY_FOR_RETEST
RETESTING
VERIFIED
REOPENED
DEFERRED
REJECTED
CLOSED
ARCHIVED
```

Rules:

```text
1. Defect belongs to project.
2. Title required.
3. Severity required.
4. Defect can link to failed TestCaseResult.
5. Defect can link to Task, Deliverable, AcceptanceCriteria, ReleasePackage, RaidItem, ChangeRequest.
6. Critical/blocker defects can block release/readiness.
7. Closing defect requires resolution type and note.
8. Reopening defect requires reason.
```

Resolution types:

```text
FIXED
WONT_FIX
DUPLICATE
NOT_A_BUG
CANNOT_REPRODUCE
DEFERRED
```

---

## 6.11 DEF-002 — Defect link

Classification: `MUST_IMPLEMENT_IN_PHASE_26`

Purpose:

```text
Link defect to project artifacts.
```

Targets:

```text
TEST_RUN
TEST_CASE_RESULT
TEST_STEP_RESULT
TASK
WBS_NODE
DELIVERABLE
ACCEPTANCE_CRITERIA
RELEASE_PACKAGE
DEPLOYMENT_RECORD
RAID_ITEM
CHANGE_REQUEST
DECISION
```

Rules:

```text
1. Target must belong to same project.
2. Link does not grant access.
3. Duplicate active links prevented.
```

---

## 6.12 REL-001 — ReleasePackage

Classification: `MUST_IMPLEMENT_IN_PHASE_26`

Purpose:

```text
Group deliverables, fixes, tasks, changes, and defects into release candidate/package.
```

Release types:

```text
INTERNAL
UAT
STAGING
PRODUCTION
HOTFIX
PATCH
MAJOR
MINOR
```

Status:

```text
DRAFT
PLANNED
IN_TESTING
READY_FOR_RELEASE
RELEASED
REJECTED
ROLLED_BACK
CANCELLED
ARCHIVED
```

Rules:

```text
1. ReleasePackage belongs to project.
2. Release code/version unique within project.
3. Release can include tasks, deliverables, defects, ChangeRequests.
4. Release readiness must be checked before READY_FOR_RELEASE/RELEASED.
5. ReleasePackage does not perform deployment by itself.
6. Release status changes audited.
```

---

## 6.13 REL-002 — ReleaseItem

Classification: `MUST_IMPLEMENT_IN_PHASE_26`

Item types:

```text
TASK
DELIVERABLE
DEFECT
CHANGE_REQUEST
SCOPE_ITEM
DOCUMENT
OTHER
```

Rules:

```text
1. ReleaseItem belongs to ReleasePackage.
2. Target belongs to same project.
3. Duplicate active release item prevented.
4. Item can be required or optional.
5. Required unresolved blocker prevents release readiness.
```

---

## 6.14 REL-003 — Release readiness checklist

Classification: `MUST_IMPLEMENT_IN_PHASE_26`

Checklist items:

```text
all required deliverables accepted or ready
mandatory acceptance criteria passed/waived
critical/blocker defects closed or waived
required test cases passed
no unresolved release-blocking RAID issue
deployment plan present
rollback plan present
release notes present
approval present
```

Rules:

```text
1. Readiness check is calculated from configured policy.
2. Failed checks block READY_FOR_RELEASE unless override permission exists.
3. Override requires reason and audit.
4. Readiness result stored.
```

---

## 6.15 DEP-001 — DeploymentEnvironment

Classification: `MUST_IMPLEMENT_IN_PHASE_26`

Purpose:

```text
Represent target environment for deployment record.
```

Environment types:

```text
DEV
TEST
UAT
STAGING
PRODUCTION
TRAINING
OTHER
```

Rules:

```text
1. Environment belongs to workspace/project or platform depending architecture.
2. Name unique within workspace/project.
3. Production environment requires stronger permissions for deployment record.
4. Environment does not store secrets.
```

---

## 6.16 DEP-002 — DeploymentRecord

Classification: `MUST_IMPLEMENT_IN_PHASE_26`

Purpose:

```text
Record deployment attempt/outcome for a release package.
```

Status:

```text
PLANNED
IN_PROGRESS
SUCCEEDED
FAILED
ROLLED_BACK
CANCELLED
```

Rules:

```text
1. DeploymentRecord belongs to project.
2. DeploymentRecord references ReleasePackage and DeploymentEnvironment.
3. Deployment record can store build/version/reference, not secret.
4. Deployment status changes audited.
5. Failed deployment can create defect/RAID issue.
6. DeploymentRecord does not execute infrastructure deployment unless integration exists.
```

---

## 6.17 DEP-003 — Rollback plan

Classification: `MUST_IMPLEMENT_IN_PHASE_26`

Purpose:

```text
Capture rollback strategy and steps for release/deployment.
```

Rules:

```text
1. RollbackPlan belongs to ReleasePackage or DeploymentRecord.
2. Production release requires rollback plan unless policy waived.
3. Rollback plan can include manual steps and owner.
4. Rollback execution can be recorded as DeploymentRecord status ROLLED_BACK.
```

---

## 6.18 RPT-001 — Quality / Test / Release reporting

Classification: `MUST_IMPLEMENT_IN_PHASE_26`

Reports:

```text
quality dashboard
test coverage report
test execution report
defect summary
defect aging report
release readiness report
release contents report
deployment history report
acceptance coverage report
```

Dashboard KPIs:

```text
testCaseCount
approvedTestCaseCount
testRunPassRate
failedCaseCount
openDefectCount
criticalDefectCount
blockerDefectCount
defectReopenRate
mandatoryCriteriaCoveragePercent
releaseReadinessStatus
deploymentSuccessRate
```

---

## 6.19 AIQ-001 — AI-assisted quality suggestions

Classification: `SEED_ONLY_IN_PHASE_26` or `MUST_IMPLEMENT_IN_PHASE_26` if AI tool registry exists.

AI can propose:

```text
test cases from acceptance criteria
missing acceptance coverage
defect root-cause summary
release readiness explanation
risk from recurring defects
test case improvement suggestions
```

Rules:

```text
1. AI output is proposal only.
2. Human approval required to create/update test cases or defects.
3. AI cannot mark tests passed.
4. AI cannot close defects.
5. AI cannot approve release/deployment.
6. AI respects project/quality/finance/quote permissions.
```

---

# 7. Entity model TO-BE

---

## 7.1 QualityPlan — `project_quality_plan`

Fields:

```text
id UUID PK
project_id UUID NOT NULL
workspace_id UUID NOT NULL
source_baseline_id UUID NULL
code VARCHAR(100) NULL
name VARCHAR(255) NOT NULL
description TEXT NULL
status VARCHAR(50) NOT NULL
current_flag BOOLEAN NOT NULL DEFAULT false
quality_objectives TEXT NULL
test_strategy TEXT NULL
entry_criteria TEXT NULL
exit_criteria TEXT NULL
defect_policy_json JSONB NULL
release_readiness_policy_json JSONB NULL
approved_at TIMESTAMP NULL
approved_by UUID NULL
created_at / created_by
updated_at / updated_by
archived_at / archived_by
trace_id VARCHAR(100) NULL
version INT
```

---

## 7.2 TestPlan — `project_test_plan`

Fields:

```text
id UUID PK
project_id UUID NOT NULL
workspace_id UUID NOT NULL
quality_plan_id UUID NULL
release_package_id UUID NULL
code VARCHAR(100) NULL
name VARCHAR(255) NOT NULL
description TEXT NULL
test_level VARCHAR(50) NOT NULL
status VARCHAR(50) NOT NULL
created_at / created_by
updated_at / updated_by
approved_at / approved_by
archived_at / archived_by
version INT
```

---

## 7.3 TestSuite — `project_test_suite`

Fields:

```text
id UUID PK
project_id UUID NOT NULL
test_plan_id UUID NOT NULL
deliverable_id UUID NULL
scope_item_id UUID NULL
name VARCHAR(255) NOT NULL
description TEXT NULL
status VARCHAR(50) NOT NULL
sort_order INT NULL
created_at / created_by
updated_at / updated_by
archived_at / archived_by
version INT
```

---

## 7.4 TestCase — `project_test_case`

Fields:

```text
id UUID PK
project_id UUID NOT NULL
test_suite_id UUID NULL
code VARCHAR(100) NULL
title VARCHAR(255) NOT NULL
description TEXT NULL
type VARCHAR(50) NOT NULL
priority VARCHAR(50) NOT NULL
status VARCHAR(50) NOT NULL
preconditions TEXT NULL
expected_result TEXT NULL
version_number INT NOT NULL DEFAULT 1
created_at / created_by
updated_at / updated_by
approved_at / approved_by
archived_at / archived_by
version INT
```

Constraint:

```text
unique project_id + code where code not null
```

---

## 7.5 TestStep — `project_test_step`

Fields:

```text
id UUID PK
project_id UUID NOT NULL
test_case_id UUID NOT NULL
step_order INT NOT NULL
action_text TEXT NOT NULL
expected_result TEXT NOT NULL
data_notes TEXT NULL
created_at / created_by
updated_at / updated_by
archived_at / archived_by
version INT
```

Constraint:

```text
unique active test_case_id + step_order
```

---

## 7.6 TestCaseCoverage — `project_test_case_coverage`

Fields:

```text
id UUID PK
project_id UUID NOT NULL
test_case_id UUID NOT NULL
target_type VARCHAR(100) NOT NULL
target_id UUID NOT NULL
coverage_type VARCHAR(50) NOT NULL
created_at / created_by
archived_at / archived_by
version INT
```

Target types:

```text
ACCEPTANCE_CRITERIA
DELIVERABLE
SCOPE_ITEM
WBS_NODE
TASK
RAID_ITEM
CHANGE_REQUEST
```

Coverage type:

```text
PRIMARY
SECONDARY
REGRESSION
VALIDATION
```

---

## 7.7 TestRun — `project_test_run`

Fields:

```text
id UUID PK
project_id UUID NOT NULL
workspace_id UUID NOT NULL
test_plan_id UUID NULL
test_suite_id UUID NULL
release_package_id UUID NULL
deployment_environment_id UUID NULL
name VARCHAR(255) NOT NULL
run_type VARCHAR(50) NOT NULL
status VARCHAR(50) NOT NULL
started_at TIMESTAMP NULL
completed_at TIMESTAMP NULL
executed_by UUID NULL
summary_json JSONB NULL
created_at / created_by
updated_at / updated_by
archived_at / archived_by
trace_id VARCHAR(100) NULL
version INT
```

---

## 7.8 TestCaseResult — `project_test_case_result`

Fields:

```text
id UUID PK
project_id UUID NOT NULL
test_run_id UUID NOT NULL
test_case_id UUID NOT NULL
result_status VARCHAR(50) NOT NULL
actual_result TEXT NULL
evidence_reference TEXT NULL
executed_at TIMESTAMP NULL
executed_by UUID NULL
defect_id UUID NULL
created_at / created_by
updated_at / updated_by
version INT
```

---

## 7.9 TestStepResult — `project_test_step_result`

Fields:

```text
id UUID PK
project_id UUID NOT NULL
test_case_result_id UUID NOT NULL
test_step_id UUID NOT NULL
result_status VARCHAR(50) NOT NULL
actual_result TEXT NULL
evidence_reference TEXT NULL
executed_at TIMESTAMP NULL
executed_by UUID NULL
created_at / created_by
updated_at / updated_by
version INT
```

---

## 7.10 Defect — `project_defect`

Fields:

```text
id UUID PK
project_id UUID NOT NULL
workspace_id UUID NOT NULL
code VARCHAR(100) NULL
title VARCHAR(255) NOT NULL
description TEXT NULL
category VARCHAR(50) NOT NULL
severity VARCHAR(50) NOT NULL
priority VARCHAR(50) NOT NULL
status VARCHAR(50) NOT NULL
assigned_to_user_id UUID NULL
reported_by UUID NULL
reported_at TIMESTAMP NULL
reproduction_steps TEXT NULL
expected_result TEXT NULL
actual_result TEXT NULL
environment_notes TEXT NULL
resolution_type VARCHAR(50) NULL
resolution_note TEXT NULL
resolved_at TIMESTAMP NULL
resolved_by UUID NULL
closed_at TIMESTAMP NULL
closed_by UUID NULL
reopened_at TIMESTAMP NULL
reopened_by UUID NULL
reopen_reason TEXT NULL
source_test_case_result_id UUID NULL
source_ai_suggestion_id UUID NULL
created_at / created_by
updated_at / updated_by
archived_at / archived_by
trace_id VARCHAR(100) NULL
version INT
```

---

## 7.11 DefectLink — `project_defect_link`

Fields:

```text
id UUID PK
project_id UUID NOT NULL
defect_id UUID NOT NULL
target_type VARCHAR(100) NOT NULL
target_id UUID NOT NULL
link_type VARCHAR(50) NOT NULL
created_at / created_by
archived_at / archived_by
version INT
```

Link types:

```text
FOUND_IN
BLOCKS
AFFECTS
FIXED_BY
RELATED_TO
CAUSED_BY
VALIDATED_BY
```

---

## 7.12 ReleasePackage — `project_release_package`

Fields:

```text
id UUID PK
project_id UUID NOT NULL
workspace_id UUID NOT NULL
code VARCHAR(100) NOT NULL
version_label VARCHAR(100) NOT NULL
name VARCHAR(255) NOT NULL
description TEXT NULL
release_type VARCHAR(50) NOT NULL
status VARCHAR(50) NOT NULL
planned_release_date DATE NULL
actual_release_date DATE NULL
readiness_status VARCHAR(50) NULL
readiness_summary_json JSONB NULL
release_notes TEXT NULL
approved_at TIMESTAMP NULL
approved_by UUID NULL
released_at TIMESTAMP NULL
released_by UUID NULL
created_at / created_by
updated_at / updated_by
archived_at / archived_by
trace_id VARCHAR(100) NULL
version INT
```

Constraint:

```text
unique project_id + code
unique project_id + version_label
```

---

## 7.13 ReleaseItem — `project_release_item`

Fields:

```text
id UUID PK
project_id UUID NOT NULL
release_package_id UUID NOT NULL
target_type VARCHAR(100) NOT NULL
target_id UUID NOT NULL
required BOOLEAN NOT NULL DEFAULT true
status VARCHAR(50) NOT NULL
notes TEXT NULL
created_at / created_by
archived_at / archived_by
version INT
```

---

## 7.14 ReleaseReadinessCheck — `project_release_readiness_check`

Fields:

```text
id UUID PK
project_id UUID NOT NULL
release_package_id UUID NOT NULL
check_code VARCHAR(150) NOT NULL
check_name VARCHAR(255) NOT NULL
status VARCHAR(50) NOT NULL
details TEXT NULL
blocking BOOLEAN NOT NULL DEFAULT true
override_reason TEXT NULL
overridden_at TIMESTAMP NULL
overridden_by UUID NULL
created_at / created_by
updated_at / updated_by
version INT
```

Status:

```text
PASSED
FAILED
WARNING
WAIVED
NOT_APPLICABLE
```

---

## 7.15 DeploymentEnvironment — `project_deployment_environment`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
project_id UUID NULL
code VARCHAR(100) NOT NULL
name VARCHAR(255) NOT NULL
environment_type VARCHAR(50) NOT NULL
description TEXT NULL
active BOOLEAN NOT NULL DEFAULT true
created_at / created_by
updated_at / updated_by
archived_at / archived_by
version INT
```

Do not store secrets.

---

## 7.16 DeploymentRecord — `project_deployment_record`

Fields:

```text
id UUID PK
project_id UUID NOT NULL
workspace_id UUID NOT NULL
release_package_id UUID NOT NULL
deployment_environment_id UUID NOT NULL
status VARCHAR(50) NOT NULL
build_reference VARCHAR(500) NULL
deployment_reference VARCHAR(500) NULL
started_at TIMESTAMP NULL
completed_at TIMESTAMP NULL
deployed_by UUID NULL
failure_reason TEXT NULL
rollback_plan_id UUID NULL
rolled_back_at TIMESTAMP NULL
rolled_back_by UUID NULL
rollback_reason TEXT NULL
created_at / created_by
updated_at / updated_by
trace_id VARCHAR(100) NULL
version INT
```

---

## 7.17 RollbackPlan — `project_rollback_plan`

Fields:

```text
id UUID PK
project_id UUID NOT NULL
release_package_id UUID NULL
deployment_record_id UUID NULL
title VARCHAR(255) NOT NULL
description TEXT NOT NULL
owner_user_id UUID NULL
status VARCHAR(50) NOT NULL
steps_json JSONB NULL
approved_at TIMESTAMP NULL
approved_by UUID NULL
created_at / created_by
updated_at / updated_by
archived_at / archived_by
version INT
```

Status:

```text
DRAFT
READY
APPROVED
ARCHIVED
```

---

# 8. API TO-BE list

All APIs use `/api`.

---

## 8.1 Quality plan APIs

```text
POST  /api/projects/{projectId}/quality-plans
GET   /api/projects/{projectId}/quality-plans
GET   /api/projects/{projectId}/quality-plans/{qualityPlanId}
PUT   /api/projects/{projectId}/quality-plans/{qualityPlanId}
POST  /api/projects/{projectId}/quality-plans/{qualityPlanId}/approve
POST  /api/projects/{projectId}/quality-plans/{qualityPlanId}/mark-current
PATCH /api/projects/{projectId}/quality-plans/{qualityPlanId}/archive
```

---

## 8.2 Test plan / suite / case APIs

```text
POST /api/projects/{projectId}/test-plans
GET  /api/projects/{projectId}/test-plans
GET  /api/projects/{projectId}/test-plans/{testPlanId}
PUT  /api/projects/{projectId}/test-plans/{testPlanId}
POST /api/projects/{projectId}/test-plans/{testPlanId}/approve
PATCH /api/projects/{projectId}/test-plans/{testPlanId}/archive

POST /api/projects/{projectId}/test-plans/{testPlanId}/suites
GET  /api/projects/{projectId}/test-plans/{testPlanId}/suites
PUT  /api/projects/{projectId}/test-suites/{testSuiteId}
PATCH /api/projects/{projectId}/test-suites/{testSuiteId}/archive

POST /api/projects/{projectId}/test-cases
GET  /api/projects/{projectId}/test-cases
GET  /api/projects/{projectId}/test-cases/{testCaseId}
PUT  /api/projects/{projectId}/test-cases/{testCaseId}
POST /api/projects/{projectId}/test-cases/{testCaseId}/approve
PATCH /api/projects/{projectId}/test-cases/{testCaseId}/archive
```

---

## 8.3 Test step and coverage APIs

```text
POST   /api/projects/{projectId}/test-cases/{testCaseId}/steps
GET    /api/projects/{projectId}/test-cases/{testCaseId}/steps
PUT    /api/projects/{projectId}/test-cases/{testCaseId}/steps/{stepId}
DELETE /api/projects/{projectId}/test-cases/{testCaseId}/steps/{stepId}
PUT    /api/projects/{projectId}/test-cases/{testCaseId}/steps/reorder

POST   /api/projects/{projectId}/test-cases/{testCaseId}/coverage
GET    /api/projects/{projectId}/test-cases/{testCaseId}/coverage
DELETE /api/projects/{projectId}/test-cases/{testCaseId}/coverage/{coverageId}
```

---

## 8.4 Test run APIs

```text
POST /api/projects/{projectId}/test-runs
GET  /api/projects/{projectId}/test-runs
GET  /api/projects/{projectId}/test-runs/{testRunId}
POST /api/projects/{projectId}/test-runs/{testRunId}/start
POST /api/projects/{projectId}/test-runs/{testRunId}/complete
POST /api/projects/{projectId}/test-runs/{testRunId}/cancel
PATCH /api/projects/{projectId}/test-runs/{testRunId}/archive

POST /api/projects/{projectId}/test-runs/{testRunId}/case-results
GET  /api/projects/{projectId}/test-runs/{testRunId}/case-results
PUT  /api/projects/{projectId}/test-runs/{testRunId}/case-results/{caseResultId}

POST /api/projects/{projectId}/test-runs/{testRunId}/case-results/{caseResultId}/create-defect
```

---

## 8.5 Defect APIs

```text
POST  /api/projects/{projectId}/defects
GET   /api/projects/{projectId}/defects
GET   /api/projects/{projectId}/defects/{defectId}
PUT   /api/projects/{projectId}/defects/{defectId}
POST  /api/projects/{projectId}/defects/{defectId}/triage
POST  /api/projects/{projectId}/defects/{defectId}/assign
POST  /api/projects/{projectId}/defects/{defectId}/mark-fixed
POST  /api/projects/{projectId}/defects/{defectId}/ready-for-retest
POST  /api/projects/{projectId}/defects/{defectId}/verify
POST  /api/projects/{projectId}/defects/{defectId}/close
POST  /api/projects/{projectId}/defects/{defectId}/reopen
PATCH /api/projects/{projectId}/defects/{defectId}/archive

POST   /api/projects/{projectId}/defects/{defectId}/links
GET    /api/projects/{projectId}/defects/{defectId}/links
DELETE /api/projects/{projectId}/defects/{defectId}/links/{linkId}
```

---

## 8.6 Release APIs

```text
POST  /api/projects/{projectId}/releases
GET   /api/projects/{projectId}/releases
GET   /api/projects/{projectId}/releases/{releasePackageId}
PUT   /api/projects/{projectId}/releases/{releasePackageId}
POST  /api/projects/{projectId}/releases/{releasePackageId}/check-readiness
POST  /api/projects/{projectId}/releases/{releasePackageId}/approve
POST  /api/projects/{projectId}/releases/{releasePackageId}/mark-ready
POST  /api/projects/{projectId}/releases/{releasePackageId}/mark-released
POST  /api/projects/{projectId}/releases/{releasePackageId}/mark-rolled-back
PATCH /api/projects/{projectId}/releases/{releasePackageId}/archive

POST   /api/projects/{projectId}/releases/{releasePackageId}/items
GET    /api/projects/{projectId}/releases/{releasePackageId}/items
DELETE /api/projects/{projectId}/releases/{releasePackageId}/items/{releaseItemId}
```

---

## 8.7 Deployment APIs

```text
POST  /api/projects/{projectId}/deployment-environments
GET   /api/projects/{projectId}/deployment-environments
PUT   /api/projects/{projectId}/deployment-environments/{environmentId}
PATCH /api/projects/{projectId}/deployment-environments/{environmentId}/archive

POST /api/projects/{projectId}/deployments
GET  /api/projects/{projectId}/deployments
GET  /api/projects/{projectId}/deployments/{deploymentRecordId}
POST /api/projects/{projectId}/deployments/{deploymentRecordId}/start
POST /api/projects/{projectId}/deployments/{deploymentRecordId}/mark-succeeded
POST /api/projects/{projectId}/deployments/{deploymentRecordId}/mark-failed
POST /api/projects/{projectId}/deployments/{deploymentRecordId}/mark-rolled-back
```

---

## 8.8 Rollback plan APIs

```text
POST  /api/projects/{projectId}/rollback-plans
GET   /api/projects/{projectId}/rollback-plans
GET   /api/projects/{projectId}/rollback-plans/{rollbackPlanId}
PUT   /api/projects/{projectId}/rollback-plans/{rollbackPlanId}
POST  /api/projects/{projectId}/rollback-plans/{rollbackPlanId}/approve
PATCH /api/projects/{projectId}/rollback-plans/{rollbackPlanId}/archive
```

---

## 8.9 Reports APIs

```text
GET /api/projects/{projectId}/reports/quality-dashboard
GET /api/projects/{projectId}/reports/test-coverage
GET /api/projects/{projectId}/reports/test-execution
GET /api/projects/{projectId}/reports/defects
GET /api/projects/{projectId}/reports/defect-aging
GET /api/projects/{projectId}/reports/release-readiness
GET /api/projects/{projectId}/reports/deployment-history
```

---

# 9. Authorization requirements

Required authorities:

```text
PROJECT_QUALITY_PLAN_VIEW
PROJECT_QUALITY_PLAN_CREATE
PROJECT_QUALITY_PLAN_UPDATE
PROJECT_QUALITY_PLAN_APPROVE
PROJECT_QUALITY_PLAN_MARK_CURRENT
PROJECT_QUALITY_PLAN_ARCHIVE

PROJECT_TEST_PLAN_VIEW
PROJECT_TEST_PLAN_CREATE
PROJECT_TEST_PLAN_UPDATE
PROJECT_TEST_PLAN_APPROVE
PROJECT_TEST_PLAN_ARCHIVE

PROJECT_TEST_SUITE_MANAGE
PROJECT_TEST_CASE_VIEW
PROJECT_TEST_CASE_CREATE
PROJECT_TEST_CASE_UPDATE
PROJECT_TEST_CASE_APPROVE
PROJECT_TEST_CASE_ARCHIVE
PROJECT_TEST_STEP_MANAGE
PROJECT_TEST_COVERAGE_MANAGE

PROJECT_TEST_RUN_VIEW
PROJECT_TEST_RUN_CREATE
PROJECT_TEST_RUN_UPDATE
PROJECT_TEST_RUN_EXECUTE
PROJECT_TEST_RUN_COMPLETE
PROJECT_TEST_RESULT_UPDATE
PROJECT_TEST_RESULT_CREATE_DEFECT

PROJECT_DEFECT_VIEW
PROJECT_DEFECT_CREATE
PROJECT_DEFECT_UPDATE
PROJECT_DEFECT_TRIAGE
PROJECT_DEFECT_ASSIGN
PROJECT_DEFECT_RESOLVE
PROJECT_DEFECT_VERIFY
PROJECT_DEFECT_CLOSE
PROJECT_DEFECT_REOPEN
PROJECT_DEFECT_ARCHIVE
PROJECT_DEFECT_LINK_MANAGE

PROJECT_RELEASE_VIEW
PROJECT_RELEASE_CREATE
PROJECT_RELEASE_UPDATE
PROJECT_RELEASE_APPROVE
PROJECT_RELEASE_MARK_READY
PROJECT_RELEASE_MARK_RELEASED
PROJECT_RELEASE_ROLLBACK
PROJECT_RELEASE_ARCHIVE
PROJECT_RELEASE_ITEM_MANAGE
PROJECT_RELEASE_OVERRIDE_READINESS

PROJECT_DEPLOYMENT_ENV_VIEW
PROJECT_DEPLOYMENT_ENV_MANAGE
PROJECT_DEPLOYMENT_VIEW
PROJECT_DEPLOYMENT_CREATE
PROJECT_DEPLOYMENT_UPDATE
PROJECT_DEPLOYMENT_PRODUCTION_RECORD
PROJECT_ROLLBACK_PLAN_VIEW
PROJECT_ROLLBACK_PLAN_MANAGE
PROJECT_ROLLBACK_PLAN_APPROVE

PROJECT_QUALITY_REPORT_VIEW
PROJECT_RELEASE_REPORT_VIEW
```

Rules:

```text
1. Project access required.
2. Production deployment record requires stronger permission.
3. Release readiness override requires stronger permission and reason.
4. Creating defect from test result requires defect create permission.
5. Linking to deliverable/acceptance requires relevant project access.
6. No secret/environment credential values are exposed.
```

---

# 10. Lifecycle rules

## 10.1 TestCase lifecycle

```text
DRAFT → READY → APPROVED
APPROVED → DEPRECATED
DRAFT/READY → ARCHIVED
```

Rules:

```text
1. DRAFT editable.
2. APPROVED immutable except revision.
3. Deprecated test case excluded from new test runs by default.
```

## 10.2 TestRun lifecycle

```text
PLANNED → IN_PROGRESS → COMPLETED
PLANNED/IN_PROGRESS → CANCELLED
```

Rules:

```text
1. Completed run immutable except correction permission.
2. Summary calculated at completion.
3. Test results must belong to run/project.
```

## 10.3 Defect lifecycle

```text
OPEN → TRIAGED → ASSIGNED → IN_PROGRESS → FIXED → READY_FOR_RETEST → RETESTING → VERIFIED → CLOSED
OPEN/TRIAGED/ASSIGNED/IN_PROGRESS → REJECTED
FIXED/VERIFIED/CLOSED → REOPENED
```

Rules:

```text
1. Closing requires resolution type and note.
2. Reopen requires reason.
3. Critical/blocker defects may block release readiness.
```

## 10.4 Release lifecycle

```text
DRAFT → PLANNED → IN_TESTING → READY_FOR_RELEASE → RELEASED
READY_FOR_RELEASE/RELEASED → ROLLED_BACK
DRAFT/PLANNED → CANCELLED
```

Rules:

```text
1. Readiness check required before READY_FOR_RELEASE.
2. Blocking failed readiness prevents release unless override.
3. Released status does not create invoice/revenue.
4. Rolled back status requires reason.
```

## 10.5 Deployment lifecycle

```text
PLANNED → IN_PROGRESS → SUCCEEDED
IN_PROGRESS → FAILED
SUCCEEDED → ROLLED_BACK
PLANNED/IN_PROGRESS → CANCELLED
```

Rules:

```text
1. Deployment record is audit record.
2. Does not execute deployment unless integration exists.
3. Failure can create defect/RAID issue.
```

---

# 11. Integration rules

## 11.1 Scope / Deliverable / Acceptance integration

Rules:

```text
1. TestCase can cover AcceptanceCriteria.
2. TestRun result can satisfy criteria only if policy allows and human confirms.
3. Failed tests can block Deliverable acceptance.
4. Critical open defects can block acceptance.
5. Acceptance evidence can reference TestRun/TestCaseResult.
```

## 11.2 RAID / Decision integration

Rules:

```text
1. Critical defects can create/link RAID issue.
2. Release blocker can create RAID risk/issue.
3. DecisionRecord can record release decision.
4. Risk/issue reports include quality blockers.
```

## 11.3 Baseline / ChangeRequest integration

Rules:

```text
1. Adding tests/defects is governance data and may not need ChangeRequest.
2. Defect fix requiring scope/schedule/finance change must create ChangeRequest.
3. Release package can reference applied ChangeRequests.
4. Post-baseline acceptance criteria changes require ChangeRequest from Phase 24 rules.
```

## 11.4 Reporting integration

Extend Phase 22 report definitions with:

```text
PROJECT_QUALITY_DASHBOARD
PROJECT_TEST_COVERAGE_REPORT
PROJECT_TEST_EXECUTION_REPORT
PROJECT_DEFECT_SUMMARY_REPORT
PROJECT_DEFECT_AGING_REPORT
PROJECT_RELEASE_READINESS_REPORT
PROJECT_DEPLOYMENT_HISTORY_REPORT
```

## 11.5 Notification integration

Notifications for:

```text
test run completed
test case failed
critical defect created
defect assigned
defect fixed
defect reopened
release readiness failed
release ready
release released
deployment failed
deployment succeeded
rollback recorded
```

## 11.6 AI integration

AI can suggest:

```text
test cases from acceptance criteria
missing coverage
defect summary/root cause
release readiness explanation
risk from defect trends
```

Rules:

```text
AI suggests only.
Human applies.
AI cannot pass tests, close defects, or approve release.
```

---

# 12. Event Registry integration

Recommended source system:

```text
SCOPERY_QUALITY_RELEASE
```

Required events:

```text
QUALITY_PLAN_CREATED
QUALITY_PLAN_APPROVED
QUALITY_PLAN_MARKED_CURRENT

TEST_PLAN_CREATED
TEST_PLAN_APPROVED
TEST_SUITE_CREATED
TEST_CASE_CREATED
TEST_CASE_APPROVED
TEST_CASE_UPDATED
TEST_CASE_ARCHIVED
TEST_COVERAGE_LINKED

TEST_RUN_CREATED
TEST_RUN_STARTED
TEST_RUN_COMPLETED
TEST_RUN_CANCELLED
TEST_CASE_RESULT_RECORDED
TEST_CASE_FAILED
TEST_CASE_PASSED

DEFECT_CREATED
DEFECT_TRIAGED
DEFECT_ASSIGNED
DEFECT_FIXED
DEFECT_READY_FOR_RETEST
DEFECT_VERIFIED
DEFECT_CLOSED
DEFECT_REOPENED
DEFECT_ARCHIVED
DEFECT_LINK_CREATED

RELEASE_PACKAGE_CREATED
RELEASE_PACKAGE_UPDATED
RELEASE_READINESS_CHECKED
RELEASE_READINESS_FAILED
RELEASE_READY
RELEASE_RELEASED
RELEASE_ROLLED_BACK
RELEASE_ITEM_ADDED
RELEASE_ITEM_REMOVED

DEPLOYMENT_ENVIRONMENT_CREATED
DEPLOYMENT_RECORD_CREATED
DEPLOYMENT_STARTED
DEPLOYMENT_SUCCEEDED
DEPLOYMENT_FAILED
DEPLOYMENT_ROLLED_BACK

ROLLBACK_PLAN_CREATED
ROLLBACK_PLAN_APPROVED
```

Standard variables:

```text
actor.userId
organization.id
workspace.id
project.id
qualityPlan.id
testPlan.id
testCase.id
testRun.id
defect.id
defect.severity
releasePackage.id
releasePackage.versionLabel
deploymentRecord.id
deploymentEnvironment.id
occurredAt
traceId
```

---

# 13. Audit / activity / outbox

Audit-sensitive actions:

```text
quality plan approved
test plan approved
test case approved/deprecated
test run completed/corrected
critical/blocker defect created
defect closed/reopened
release readiness override
release marked released
deployment marked production succeeded/failed
rollback recorded
```

Activity actions:

```text
TEST_RUN_COMPLETED
DEFECT_CREATED
DEFECT_CLOSED
RELEASE_READY
RELEASE_RELEASED
DEPLOYMENT_SUCCEEDED
DEPLOYMENT_FAILED
```

Outbox:

```text
Major quality/release/deployment events must use platform outbox.
```

Idempotency recommended for:

```text
POST /quality-plans
POST /test-runs
POST /test-runs/{id}/case-results/{id}/create-defect
POST /defects
POST /releases
POST /releases/{id}/check-readiness
POST /deployments
POST /deployments/{id}/mark-succeeded
POST /deployments/{id}/mark-failed
```

---

# 14. Business rules master

## 14.1 Quality/test rules

```text
QLT-001 Project must exist.
QLT-002 Project must not be archived.
QLT-003 QualityPlan current unique per project.
TST-001 TestPlan belongs to project.
TST-002 TestCase belongs to project.
TST-003 Approved TestCase immutable except revision.
TST-004 Coverage target must belong to same project.
TST-005 Completed TestRun immutable except correction permission.
TST-006 Failed TestCaseResult can create Defect.
TST-007 TestResult does not automatically accept deliverable unless policy explicitly allows.
```

## 14.2 Defect rules

```text
DEF-001 Defect title/severity/priority required.
DEF-002 Linked targets must belong to same project.
DEF-003 Closing requires resolution type and note.
DEF-004 Reopen requires reason.
DEF-005 Critical/blocker defect blocks release readiness unless waived.
DEF-006 Defect fix requiring scope/schedule/finance change requires ChangeRequest.
```

## 14.3 Release rules

```text
REL-001 Release code/version unique per project.
REL-002 Release item target must belong to same project.
REL-003 Readiness check required before READY_FOR_RELEASE.
REL-004 Blocking failed check blocks release unless override.
REL-005 Override requires reason and permission.
REL-006 Released does not create invoice/revenue.
REL-007 Rollback requires reason.
```

## 14.4 Deployment rules

```text
DEP-001 Environment must not store secrets.
DEP-002 DeploymentRecord references release and environment.
DEP-003 Production deployment record requires stronger permission.
DEP-004 DeploymentRecord does not execute deployment unless integration exists.
DEP-005 Failed deployment can create defect/RAID issue.
```

---

# 15. Error catalog

```text
QUALITY_PLAN_NOT_FOUND
QUALITY_PLAN_CURRENT_CONFLICT
QUALITY_PLAN_NOT_DRAFT
QUALITY_PLAN_IMMUTABLE
QUALITY_PLAN_PROJECT_ARCHIVED
QUALITY_ACCESS_DENIED

TEST_PLAN_NOT_FOUND
TEST_PLAN_NOT_DRAFT
TEST_PLAN_IMMUTABLE
TEST_SUITE_NOT_FOUND
TEST_SUITE_PROJECT_MISMATCH
TEST_CASE_NOT_FOUND
TEST_CASE_CODE_ALREADY_EXISTS
TEST_CASE_IMMUTABLE
TEST_CASE_PROJECT_MISMATCH
TEST_STEP_NOT_FOUND
TEST_STEP_ORDER_DUPLICATE
TEST_COVERAGE_TARGET_MISMATCH
TEST_COVERAGE_DUPLICATE

TEST_RUN_NOT_FOUND
TEST_RUN_INVALID_STATUS
TEST_RUN_IMMUTABLE
TEST_RESULT_NOT_FOUND
TEST_RESULT_INVALID_STATUS
TEST_RESULT_PROJECT_MISMATCH

DEFECT_NOT_FOUND
DEFECT_CODE_ALREADY_EXISTS
DEFECT_INVALID_STATUS
DEFECT_CLOSE_NOTE_REQUIRED
DEFECT_RESOLUTION_TYPE_REQUIRED
DEFECT_REOPEN_REASON_REQUIRED
DEFECT_LINK_TARGET_MISMATCH
DEFECT_LINK_DUPLICATE
DEFECT_ACCESS_DENIED

RELEASE_PACKAGE_NOT_FOUND
RELEASE_CODE_ALREADY_EXISTS
RELEASE_VERSION_ALREADY_EXISTS
RELEASE_INVALID_STATUS
RELEASE_READINESS_FAILED
RELEASE_READINESS_OVERRIDE_REASON_REQUIRED
RELEASE_ITEM_NOT_FOUND
RELEASE_ITEM_TARGET_MISMATCH
RELEASE_ITEM_DUPLICATE

DEPLOYMENT_ENVIRONMENT_NOT_FOUND
DEPLOYMENT_ENVIRONMENT_SECRET_NOT_ALLOWED
DEPLOYMENT_RECORD_NOT_FOUND
DEPLOYMENT_INVALID_STATUS
DEPLOYMENT_PRODUCTION_PERMISSION_REQUIRED
DEPLOYMENT_FAILURE_REASON_REQUIRED
DEPLOYMENT_ROLLBACK_REASON_REQUIRED

ROLLBACK_PLAN_NOT_FOUND
ROLLBACK_PLAN_NOT_READY
ROLLBACK_PLAN_APPROVAL_REQUIRED
```

---

# 16. Required tests

## 16.1 QualityPlan tests

```text
createQualityPlan_valid_success
createQualityPlan_archivedProject_rejected
approveQualityPlan_valid_success
markCurrentQualityPlan_onlyOneCurrent
updateApprovedQualityPlan_rejected
```

## 16.2 Test management tests

```text
createTestPlan_valid_success
approveTestPlan_valid_success
createTestSuite_valid_success
createTestCase_valid_success
approveTestCase_valid_success
updateApprovedTestCase_rejected
createTestStep_valid_success
reorderTestSteps_success
duplicateStepOrder_rejected
mapTestCaseToAcceptanceCriteria_success
mapTestCaseToOtherProjectCriteria_rejected
coverageReport_uncoveredMandatoryCriteria
```

## 16.3 TestRun tests

```text
createTestRun_valid_success
startTestRun_valid_success
recordPassedCaseResult_success
recordFailedCaseResult_success
failedCaseResult_createDefect_success
completeTestRun_calculatesSummary
completedTestRun_immutable
cancelTestRun_valid_success
```

## 16.4 Defect tests

```text
createDefect_valid_success
createDefect_fromFailedTestResult_success
linkDefectToDeliverable_success
linkDefectToOtherProjectTarget_rejected
assignDefect_success
markFixed_success
readyForRetest_success
verifyDefect_success
closeDefect_requiresResolution
reopenDefect_requiresReason
criticalDefect_blocksReleaseReadiness
```

## 16.5 Release tests

```text
createRelease_valid_success
createRelease_duplicateCode_rejected
addReleaseItem_task_success
addReleaseItem_otherProject_rejected
readinessCheck_pass_success
readinessCheck_blockerDefect_failed
markReady_failedReadiness_rejected
markReady_overrideRequiresPermissionAndReason
markReleased_valid_success
releasedDoesNotCreateInvoice
releasedDoesNotCreateRevenue
markRolledBack_requiresReason
```

## 16.6 Deployment tests

```text
createEnvironment_valid_success
environmentDoesNotStoreSecrets
createDeploymentRecord_valid_success
productionDeployment_withoutPermission_forbidden
markDeploymentSucceeded_success
markDeploymentFailed_requiresReason
failedDeployment_canCreateDefectOrRaidIssue
markRolledBack_requiresReason
deploymentDoesNotExecutePipeline
```

## 16.7 Integration tests

```text
failedTestBlocksDeliverableAcceptance_ifPolicyEnabled
passedTestCanSatisfyCriteria_onlyWithHumanConfirm
defectFixRequiringScopeChange_createsChangeRequestDraft
releaseIncludesAppliedChangeRequest_success
aiSuggestedTestCases_areProposalOnly
```

## 16.8 Authorization tests

```text
viewQuality_withoutPermission_forbidden
createTestCase_withoutPermission_forbidden
executeTestRun_withoutPermission_forbidden
createDefect_withoutPermission_forbidden
approveRelease_withoutPermission_forbidden
productionDeployment_withoutPermission_forbidden
crossWorkspaceDefect_forbidden
```

## 16.9 Event/audit tests

```text
qualityEventSeeder_firstRun_createsDefinitions
qualityEventSeeder_secondRun_noDuplicates
testRunCompleted_eventEmitted
criticalDefectCreated_eventEmitted
releaseReadinessFailed_eventEmitted
deploymentFailed_eventEmitted
releaseOverride_auditCreated
```

---

# 17. Manual verification checklist

Completion file must include:

```text
1. Create QualityPlan.
2. Create TestPlan and TestSuite.
3. Create TestCase with steps.
4. Map TestCase to AcceptanceCriteria.
5. Create TestRun.
6. Record pass/fail results.
7. Create Defect from failed result.
8. Link Defect to Deliverable.
9. Fix, retest, verify, close Defect.
10. Create ReleasePackage.
11. Add deliverables/tasks/defects/change requests to release.
12. Run readiness check.
13. Confirm blocker defect blocks release.
14. Override readiness with permission/reason if policy allows.
15. Create DeploymentEnvironment.
16. Create DeploymentRecord.
17. Mark deployment succeeded/failed.
18. Confirm no real CI/CD pipeline was executed.
19. Confirm release does not create invoice/revenue.
20. Confirm events, notifications, reports, audit/outbox created.
```

---

# 18. Acceptance criteria

Phase 26 is accepted only if:

```text
1. Current quality/test/release capability is classified against TO-BE.
2. QualityPlan implemented/tested.
3. TestPlan/TestSuite/TestCase/TestStep implemented/tested.
4. AcceptanceCriteria coverage implemented/tested.
5. TestRun/TestCaseResult/TestStepResult implemented/tested.
6. Defect and DefectLink implemented/tested.
7. ReleasePackage and ReleaseItem implemented/tested.
8. Release readiness checks implemented/tested.
9. DeploymentEnvironment/DeploymentRecord implemented/tested.
10. RollbackPlan implemented/tested or explicitly deferred with reason.
11. Deliverable/Acceptance integration implemented/tested.
12. RAID/ChangeRequest integration implemented/tested.
13. Reporting/notification/event integrations implemented/tested.
14. IAM permissions implemented/tested.
15. Activity/audit/outbox follows Phase 04.
16. No CI/CD runner, invoice, revenue, incident, client portal is falsely claimed.
17. `mvn compile` passes.
18. `mvn test` passes.
19. Completion file exists.
```

Do not mark complete if:

```text
approved test cases can be silently edited
completed test runs can be silently edited
critical defects do not block release readiness
release creates fake invoice/revenue
deployment claims to execute pipeline without integration
environment stores secrets
cross-project links are allowed
tests fail
```

---

# 19. Required phase completion file

Agent must create:

```text
docs/phase-complete/PHASE_26_QUALITY_TEST_RELEASE_TO_BE_COMPLETE.md
```

Required sections:

```text
# Phase 26 — Quality / Test / Release Complete

## 1. Summary
## 2. Source Inputs Reviewed
## 3. Current vs TO-BE Classification Matrix
## 4. Implemented in Current BE
## 5. Implemented / Hardened in This Phase
## 6. Seed-only Items Added
## 7. Deferred Items and Target Phase
## 8. Quality Boundary Decision
## 9. Entity Mapping
## 10. API Changes
## 11. QualityPlan Strategy
## 12. TestPlan / TestSuite Strategy
## 13. TestCase / TestStep Strategy
## 14. Acceptance Coverage Strategy
## 15. TestRun / Result Strategy
## 16. Defect Lifecycle Strategy
## 17. Release Package Strategy
## 18. Release Readiness Strategy
## 19. Deployment Record Strategy
## 20. Rollback Plan Strategy
## 21. Deliverable / Acceptance Integration
## 22. RAID / ChangeRequest Integration
## 23. Reporting Strategy
## 24. Notification / Event Strategy
## 25. Authorization Matrix
## 26. Activity / Audit / Outbox Notes
## 27. Idempotency Strategy
## 28. Tests Added
## 29. Commands Run
## 30. Test Results
## 31. Manual Verification
## 32. Assumptions
## 33. Deviations From Prompt
## 34. Known Risks
## 35. Future Phases That Must Return to Quality / Release
```

---

# 20. Future phases that must return

```text
Phase 27 — Document Hub:
- Attach versioned test evidence, release notes, test reports, sign-off documents.

Phase 28 — Application / Requirement / Screen Traceability:
- Link test cases to requirements/screens/API endpoints.

Phase 30 — Customer / External Collaboration Portal:
- Client UAT test runs and acceptance signoff.

Phase 31 — Meetings / Collaboration:
- Test review meetings, defect triage meetings, release readiness meetings.

Phase 34 — Workflow / Approval:
- Multi-step quality gate, release approval, deployment approval.

Phase 35 — Advanced Notifications:
- Test execution digest, defect SLA reminders, release readiness reminders.

Phase 36 — Contract / Billing / Revenue:
- Release/acceptance milestone can trigger billing/revenue.

Phase 38 — Audit / Compliance / Privacy:
- Compliance evidence, release approval retention, audit exports.

Phase 39 — Integrations:
- CI/CD integration, test automation import, deployment tool integration.

Phase 40 — Service / Support / Maintenance:
- Post-release support tickets, incidents, SLAs.

Phase 41 — Knowledge Graph / Semantic Index:
- Traceability graph across scope, tests, defects, releases, documents.
```

---

# 21. Agent anti-bịa rules

The agent must not:

```text
1. Claim Phase 26 executes real CI/CD pipelines unless integration exists.
2. Store deployment secrets in environment records.
3. Claim release creates invoice.
4. Claim release creates revenue recognition.
5. Claim DeploymentRecord is actual infrastructure automation.
6. Claim client portal UAT exists.
7. Claim full incident/service desk exists.
8. Let approved test cases be silently edited.
9. Let completed test runs be silently edited.
10. Let critical/blocker defects be ignored in readiness without override/audit.
11. Allow cross-project defect/release/test links.
12. Hide deferred CI/CD/document/client portal/incident gaps.
```

---

# 22. Prompt to give coding agent

```text
You are implementing Phase 26 — TO-BE Quality, Test Management, Defect Control, Release Package & Deployment Readiness.

This is not an as-is documentation task.

Before coding, read:
- CLAUDE.md / CLAUDE.ms
- Coding_convention.md
- Phase 00–25 docs and completion files
- Current backend code, migrations, tests

Your task:
1. Compare current quality/test/release capability against this Phase 26 TO-BE spec.
2. Classify each capability with required labels.
3. Implement QualityPlan.
4. Implement TestPlan, TestSuite, TestCase, TestStep, TestCaseCoverage.
5. Implement TestRun, TestCaseResult, TestStepResult.
6. Implement Defect and DefectLink lifecycle.
7. Implement ReleasePackage, ReleaseItem, ReleaseReadinessCheck.
8. Implement DeploymentEnvironment, DeploymentRecord, RollbackPlan.
9. Integrate with Deliverable/Acceptance, RAID, ChangeRequest, Notification, Reporting, and AI proposal layer where available.
10. Add IAM permissions, events, audit/outbox, idempotency, and tests.
11. Run mvn compile and mvn test.
12. Create docs/phase-complete/PHASE_26_QUALITY_TEST_RELEASE_TO_BE_COMPLETE.md.

Do not implement or claim real CI/CD runner, production deployment automation, invoice/billing, revenue recognition, incident/service desk, client portal UAT, external test lab integration, or autonomous AI release approval in this phase.
```

---

# 23. Quick tracking matrix

| Capability | Current backend | Phase 26 action | Later phase |
|---|---|---|---|
| QualityPlan | Missing/unknown | Must implement | — |
| TestPlan | Missing/unknown | Must implement | — |
| TestSuite | Missing/unknown | Must implement | — |
| TestCase | Missing/unknown | Must implement | Requirement traceability Phase 28 |
| TestStep | Missing/unknown | Must implement | — |
| Acceptance coverage | Missing/unknown | Must implement | Phase 28/41 |
| TestRun | Missing/unknown | Must implement | Automation import Phase 39 |
| TestCaseResult | Missing/unknown | Must implement | — |
| TestStepResult | Missing/unknown | Must implement | — |
| Defect | Missing/unknown | Must implement | Support/incident Phase 40 |
| DefectLink | Missing/unknown | Must implement | Semantic graph Phase 41 |
| ReleasePackage | Missing/unknown | Must implement | Billing trigger Phase 36 |
| ReleaseItem | Missing/unknown | Must implement | — |
| ReleaseReadinessCheck | Missing/unknown | Must implement | Workflow Phase 34 |
| DeploymentEnvironment | Missing/unknown | Must implement without secrets | Integration Phase 39 |
| DeploymentRecord | Missing/unknown | Must implement record only | CI/CD Phase 39 |
| RollbackPlan | Missing/unknown | Must implement | — |
| CI/CD runner | Missing | Defer | Phase 39 |
| Incident/service desk | Missing | Defer | Phase 40 |
| Client UAT portal | Missing | Defer | Phase 30 |
| Versioned test evidence files | Missing | Defer | Phase 27 |

---

# 24. Final principle

Phase 26 is not complete when "a defect row can be stored."

Phase 26 is complete when Scopery can prove release quality:

```text
acceptance criteria
+ test cases
+ test execution
+ defects
+ release package
+ readiness checks
+ deployment record
+ audit
= controlled quality and release governance
```

Test verifies acceptance.

Defect protects quality.

Release groups change.

Deployment record documents delivery.

Readiness prevents unsafe release.

Release is not billing.

Deployment record is not CI/CD automation.
