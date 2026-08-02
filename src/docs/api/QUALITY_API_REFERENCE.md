# Quality Module — API Reference

> **Swagger UI (interactive):** http://localhost:8080/swagger-ui.html
> **OpenAPI JSON:** http://localhost:8080/v3/api-docs
>
> All endpoints require authentication. Base URL: `http://localhost:8080`

---

## Table of Contents

1. [Quality Plans](#1-quality-plans)
2. [Test Plans](#2-test-plans)
3. [Test Suites](#3-test-suites)
4. [Test Cases ⭐ updated](#4-test-cases)
5. [Test Case Steps](#5-test-case-steps)
6. [Test Runs ⭐ updated](#6-test-runs)
7. [Verification Results 🆕](#7-verification-results-new)
8. [Defects](#8-defects)
9. [Verification Cases 🆕](#9-verification-cases-new)
10. [NFR Specification 🆕](#10-nfr-specification-new)
11. [NFR Targets 🆕](#11-nfr-targets-new)
12. [Releases](#12-releases)
13. [Deployments](#13-deployments)
14. [Reports](#14-reports)

---

## 1. Quality Plans

Base: `/api/projects/{projectId}/quality-plans`

| Method | Path | Description |
|--------|------|-------------|
| `GET` | `/` | List quality plans |
| `POST` | `/` | Create quality plan |
| `GET` | `/{qualityPlanId}` | Get quality plan detail |
| `PUT` | `/{qualityPlanId}` | Update quality plan |
| `POST` | `/{qualityPlanId}/approve` | Approve quality plan |
| `POST` | `/{qualityPlanId}/mark-current` | Mark as current quality plan |
| `PATCH` | `/{qualityPlanId}/archive` | Archive quality plan |

### Create quality plan — `POST /api/projects/{projectId}/quality-plans`

```json
{
  "name": "string (required)",
  "code": "string | null",
  "description": "string | null",
  "qualityObjectives": "string | null",
  "testStrategy": "string | null",
  "entryCriteria": "string | null",
  "exitCriteria": "string | null"
}
```

---

## 2. Test Plans

Base: `/api/projects/{projectId}/test-plans`

| Method | Path | Description |
|--------|------|-------------|
| `GET` | `/` | List test plans |
| `POST` | `/` | Create test plan |
| `GET` | `/{testPlanId}` | Get test plan detail |
| `POST` | `/{testPlanId}/approve` | Approve test plan |
| `PATCH` | `/{testPlanId}/archive` | Archive test plan |

### Create test plan — `POST /api/projects/{projectId}/test-plans`

```json
{
  "name": "string (required)",
  "testLevel": "UNIT | INTEGRATION | SYSTEM | ACCEPTANCE",
  "code": "string | null",
  "description": "string | null",
  "qualityPlanId": "uuid | null",
  "releasePackageId": "uuid | null"
}
```

---

## 3. Test Suites

Base: `/api/projects/{projectId}/test-plans/{testPlanId}/suites`

| Method | Path | Description |
|--------|------|-------------|
| `GET` | `/` | List suites for a test plan |
| `POST` | `/` | Create test suite |
| `GET` | `/{suiteId}` | Get test suite detail |
| `PATCH` | `/{suiteId}/archive` | Archive test suite |

### Create test suite

```json
{
  "name": "string (required)",
  "description": "string | null",
  "deliverableId": "uuid | null",
  "scopeItemId": "uuid | null",
  "sortOrder": "number | null"
}
```

---

## 4. Test Cases

> ⭐ **Updated in Phase 1**: `useCaseId` field added to create, update, filter, and response.

Base: `/api/projects/{projectId}/test-cases`

| Method | Path | Description |
|--------|------|-------------|
| `GET` | `/` | List test cases (paginated, filterable) |
| `POST` | `/` | Create test case |
| `GET` | `/{testCaseId}` | Get test case detail |
| `PATCH` | `/{testCaseId}` | Update test case |
| `POST` | `/{testCaseId}/approve` | Approve test case |
| `PATCH` | `/{testCaseId}/archive` | Archive test case |
| `POST` | `/batch` | Batch create test cases |
| `PATCH` | `/batch` | Batch update test cases |
| `GET` | `/{testCaseId}/traceability` | Get traceability links |
| `PUT` | `/{testCaseId}/requirement-links` | Replace requirement links |
| `PUT` | `/{testCaseId}/use-case-links` | Replace use case links |

### List test cases — `GET /api/projects/{projectId}/test-cases`

Query params:

| Param | Type | Description |
|-------|------|-------------|
| `q` | string | Search by title/code |
| `type` | string | `FUNCTIONAL \| NON_FUNCTIONAL \| INTEGRATION \| REGRESSION \| SMOKE \| PERFORMANCE \| SECURITY \| USABILITY \| EXPLORATORY` |
| `priority` | string | `LOW \| MEDIUM \| HIGH \| CRITICAL` |
| `status` | string | `DRAFT \| READY \| APPROVED \| ARCHIVED` |
| `assigneeId` | uuid | Filter by assignee |
| `automationStatus` | string | `MANUAL \| PLANNED \| AUTOMATED` |
| `useCaseId` | uuid | **NEW** — filter by linked use case |
| `requirementId` | uuid | Filter by linked requirement |
| `latestResult` | string | `PASSED \| FAILED \| BLOCKED \| SKIPPED \| NOT_RUN` |
| `hasOpenDefect` | boolean | Filter by open defect |
| `sort` | string | e.g. `createdAt,desc` |
| `page` | number | 0-based (default 0) |
| `size` | number | default 20 |

### Create test case — `POST /api/projects/{projectId}/test-cases`

```json
{
  "title": "string (required)",
  "type": "FUNCTIONAL | NON_FUNCTIONAL | INTEGRATION | ...",
  "priority": "LOW | MEDIUM | HIGH | CRITICAL",
  "code": "string | null",
  "description": "string | null",
  "testSuiteId": "uuid | null",
  "useCaseId": "uuid | null",
  "preconditions": "string | null",
  "expectedResult": "string | null",
  "assigneeId": "uuid | null",
  "automationStatus": "MANUAL | PLANNED | AUTOMATED"
}
```

### Update test case — `PATCH /api/projects/{projectId}/test-cases/{testCaseId}`

```json
{
  "title": "string",
  "type": "string",
  "priority": "string",
  "status": "DRAFT | READY | APPROVED | ARCHIVED",
  "description": "string | null",
  "preconditions": "string | null",
  "expectedResult": "string | null",
  "useCaseId": "uuid | null",
  "assigneeId": "uuid | null",
  "automationStatus": "string",
  "version": "number (required, optimistic lock)"
}
```

### Test case response shape

```json
{
  "id": "uuid",
  "projectId": "uuid",
  "code": "string",
  "title": "string",
  "type": "string",
  "priority": "string",
  "status": "string",
  "testSuiteId": "uuid | null",
  "useCaseId": "uuid | null",
  "description": "string | null",
  "preconditions": "string | null",
  "expectedResult": "string | null",
  "assigneeId": "uuid | null",
  "automationStatus": "string",
  "stepCount": "number",
  "latestResult": "string | null",
  "latestResultAt": "string | null",
  "openDefectCount": "number",
  "version": "number",
  "createdAt": "string",
  "updatedAt": "string"
}
```

---

## 5. Test Case Steps

Base: `/api/projects/{projectId}/test-cases/{testCaseId}/steps`

| Method | Path | Description |
|--------|------|-------------|
| `GET` | `/` | List steps |
| `POST` | `/` | Create step |
| `POST` | `/batch` | Batch create steps |
| `POST` | `/reorder` | Reorder steps |
| `GET` | `/{stepId}` | Get step |
| `PATCH` | `/{stepId}` | Update step |
| `PATCH` | `/{stepId}/archive` | Archive step |
| `POST` | `/{stepId}/duplicate` | Duplicate step |

### Create step

```json
{
  "action": "string (required)",
  "expectedResult": "string | null",
  "screenId": "uuid | null",
  "componentId": "uuid | null"
}
```

---

## 6. Test Runs

> ⭐ **Updated in Phase 5**: `runScope` added to create payload and all responses to differentiate functional vs NFR test runs.

Base: `/api/projects/{projectId}/test-runs`

| Method | Path | Description |
|--------|------|-------------|
| `GET` | `/` | List test runs |
| `POST` | `/` | Create test run |
| `GET` | `/{testRunId}` | Get test run detail |
| `POST` | `/{testRunId}/start` | Start test run |
| `POST` | `/{testRunId}/complete` | Complete test run |
| `POST` | `/{testRunId}/cancel` | Cancel test run |
| `GET` | `/{testRunId}/results` | List test case results (functional) |
| `PATCH` | `/{testRunId}/results/{resultId}` | Update test case result |
| `PATCH` | `/{testRunId}/results/batch` | Batch update results |
| `POST` | `/{testRunId}/verification-results` | Record/upsert verification result 🆕 |
| `GET` | `/{testRunId}/verification-results` | List verification results 🆕 |
| `GET` | `/{testRunId}/verification-results/{resultId}` | Get verification result 🆕 |
| `PATCH` | `/{testRunId}/verification-results/{resultId}` | Update verification result 🆕 |

### Create test run

```json
{
  "name": "string (required)",
  "runType": "MANUAL | AUTOMATED | REGRESSION | SMOKE",
  "runScope": "FUNCTIONAL | NON_FUNCTIONAL | MIXED",
  "testPlanId": "uuid | null",
  "testSuiteId": "uuid | null",
  "releasePackageId": "uuid | null"
}
```

- `runScope` defaults to `FUNCTIONAL` if omitted
- Functional runs use `/results` endpoints; NFR runs use `/verification-results` endpoints
- MIXED runs can use both

### Update test case result

```json
{
  "result": "PASSED | FAILED | BLOCKED | SKIPPED | NOT_RUN",
  "comment": "string | null",
  "version": "number"
}
```

### List results — query params

| Param | Type | Description |
|-------|------|-------------|
| `q` | string | Search |
| `result` | string | Filter by result |
| `assigneeId` | uuid | Filter by assignee |
| `hasDefect` | boolean | |
| `page` | number | |
| `size` | number | |

---

## 7. Verification Results 🆕

> **New in Phase 5.** Records the actual execution outcome for a `VerificationCase` within a test run. POST is idempotent per `(testRunId, verificationCaseId)` — calling it again updates the existing record.

Base: `/api/projects/{projectId}/test-runs/{testRunId}/verification-results`

| Method | Path | Description |
|--------|------|-------------|
| `POST` | `/` | Record or update verification result (upsert by verificationCaseId) |
| `GET` | `/` | List all verification results for a test run |
| `GET` | `/{resultId}` | Get a specific result |
| `PATCH` | `/{resultId}` | Update a specific result |

### Record verification result — `POST /.../{testRunId}/verification-results`

```json
{
  "verificationCaseId": "uuid (required)",
  "resultStatus": "PASSED | FAILED | BLOCKED | SKIPPED | NOT_RUN",
  "actualValue": "number | null",
  "actualValueUnit": "string | null",
  "actualResultJson": "string | null",
  "evidenceReference": "string | null",
  "executedById": "uuid | null",
  "defectId": "uuid | null",
  "comment": "string | null"
}
```

**Typical NFR flow:**
1. Create test run with `runScope: "NON_FUNCTIONAL"`
2. POST verification-results once per verification case (upsert — if already exists it updates)
3. PATCH to refine the result (actual measured value, evidence, defect link)
4. Complete the test run

### Update verification result — `PATCH /.../{testRunId}/verification-results/{resultId}`

```json
{
  "resultStatus": "PASSED | FAILED | BLOCKED | SKIPPED | NOT_RUN",
  "actualValue": "number | null",
  "actualValueUnit": "string | null",
  "actualResultJson": "string | null",
  "evidenceReference": "string | null",
  "executedById": "uuid | null",
  "defectId": "uuid | null",
  "comment": "string | null",
  "version": "number (required, optimistic lock)"
}
```

### Response shape

```json
{
  "id": "uuid",
  "projectId": "uuid",
  "testRunId": "uuid",
  "verificationCaseId": "uuid",
  "resultStatus": "PASSED | FAILED | BLOCKED | SKIPPED | NOT_RUN",
  "actualValue": "number | null",
  "actualValueUnit": "string | null",
  "actualResultJson": "string | null",
  "evidenceReference": "string | null",
  "executedAt": "string | null",
  "executedById": "uuid | null",
  "defectId": "uuid | null",
  "comment": "string | null",
  "version": "number",
  "createdAt": "string",
  "updatedAt": "string"
}
```

---

## 8. Defects

Base: `/api/projects/{projectId}/defects`

| Method | Path | Description |
|--------|------|-------------|
| `GET` | `/` | List defects |
| `POST` | `/` | Create defect |
| `GET` | `/{defectId}` | Get defect detail |
| `PUT` | `/{defectId}` | Update defect |
| `POST` | `/{defectId}/triage` | Triage defect |
| `POST` | `/{defectId}/assign` | Assign defect |
| `POST` | `/{defectId}/mark-fixed` | Mark fixed |
| `POST` | `/{defectId}/ready-for-retest` | Mark ready for retest |
| `POST` | `/{defectId}/verify` | Verify defect |
| `POST` | `/{defectId}/close` | Close defect |
| `POST` | `/{defectId}/reopen` | Reopen defect |
| `PATCH` | `/{defectId}/archive` | Archive defect |

**Defect links** — Base: `/api/projects/{projectId}/defects/{defectId}/links`

| Method | Path | Description |
|--------|------|-------------|
| `GET` | `/` | List links |
| `POST` | `/` | Add link |
| `PATCH` | `/{linkId}/archive` | Remove link |

### Create defect

```json
{
  "title": "string (required)",
  "category": "FUNCTIONAL | PERFORMANCE | SECURITY | USABILITY | COMPATIBILITY | DATA | UI | INTEGRATION | CONFIGURATION | DOCUMENTATION",
  "severity": "CRITICAL | MAJOR | MINOR | TRIVIAL",
  "priority": "LOW | MEDIUM | HIGH | CRITICAL",
  "code": "string | null",
  "description": "string | null",
  "reproductionSteps": "string | null",
  "expectedResult": "string | null",
  "actualResult": "string | null",
  "sourceTestCaseResultId": "uuid | null"
}
```

---

## 9. Verification Cases 🆕

> **New in Phase 2.** Used for NFR/non-functional testing — separate from TestCase which is for functional testing.

Base: `/api/projects/{projectId}/verification-cases`

| Method | Path | Description |
|--------|------|-------------|
| `GET` | `/` | List verification cases (paginated) |
| `POST` | `/` | Create verification case |
| `GET` | `/{verificationCaseId}` | Get detail |
| `PATCH` | `/{verificationCaseId}` | Update |
| `POST` | `/{verificationCaseId}/archive` | Archive |

### List — query params

| Param | Type | Description |
|-------|------|-------------|
| `requirementId` | uuid | Filter by requirement (typical use) |
| `status` | string | `DRAFT \| READY \| DEPRECATED \| ARCHIVED` |
| `assigneeId` | uuid | Filter by assignee |
| `sort` | string | e.g. `createdAt,desc` |
| `page` | number | 0-based |
| `size` | number | default 20 |

### Create verification case — `POST /api/projects/{projectId}/verification-cases`

```json
{
  "requirementId": "uuid (required)",
  "title": "string (required)",
  "verificationMethod": "LOAD_TEST | PERFORMANCE_TEST | SECURITY_SCAN | PENETRATION_TEST | AVAILABILITY_CHECK | ACCESSIBILITY_AUDIT | COMPLIANCE_REVIEW | MANUAL_REVIEW | MONITORING_CHECK",
  "code": "string | null",
  "description": "string | null",
  "procedure": "string | null",
  "expectedResultJson": "string | null",
  "environment": "string | null",
  "automationStatus": "MANUAL | PLANNED | AUTOMATED",
  "ownerId": "uuid | null",
  "assigneeId": "uuid | null"
}
```

### Update verification case — `PATCH /api/projects/{projectId}/verification-cases/{id}`

```json
{
  "title": "string",
  "description": "string | null",
  "verificationMethod": "string",
  "procedure": "string | null",
  "expectedResultJson": "string | null",
  "environment": "string | null",
  "lifecycleStatus": "DRAFT | READY | DEPRECATED | ARCHIVED",
  "automationStatus": "string",
  "ownerId": "uuid | null",
  "assigneeId": "uuid | null",
  "version": "number (required)"
}
```

### Response shape

```json
{
  "id": "uuid",
  "projectId": "uuid",
  "requirementId": "uuid",
  "code": "string",
  "title": "string",
  "description": "string | null",
  "verificationMethod": "string",
  "procedure": "string | null",
  "expectedResultJson": "string | null",
  "environment": "string | null",
  "lifecycleStatus": "DRAFT | READY | DEPRECATED | ARCHIVED",
  "automationStatus": "string",
  "ownerId": "uuid | null",
  "assigneeId": "uuid | null",
  "assignee": { "id": "uuid", "displayName": "string" },
  "archivedAt": "string | null",
  "version": "number",
  "createdAt": "string",
  "updatedAt": "string"
}
```

---

## 10. NFR Specification 🆕

> **New in Phase 3.** 1-1 with a Requirement. Stores the quality attribute, metric, and threshold for a non-functional requirement.

### Get NFR specification

```
GET /api/projects/{projectId}/requirements/{requirementId}/nfr-specification
```

Returns `404` if no spec has been saved yet.

### Save NFR specification (upsert)

```
PUT /api/projects/{projectId}/requirements/{requirementId}/nfr-specification
```

```json
{
  "qualityAttribute": "PERFORMANCE | SECURITY | AVAILABILITY | RELIABILITY | SCALABILITY | USABILITY | ACCESSIBILITY | COMPATIBILITY | MAINTAINABILITY | OBSERVABILITY | DATA_INTEGRITY | COMPLIANCE",
  "metricName": "string | null",
  "comparisonOperator": "LT | LTE | GT | GTE | EQ | BETWEEN | null",
  "targetValue": "number | null",
  "secondaryTargetValue": "number | null (used when operator is BETWEEN)",
  "unit": "string | null",
  "measurementWindow": "string | null",
  "environment": "string | null",
  "verificationFrequency": "string | null",
  "configurationJson": "string | null"
}
```

**Example** (API P95 latency ≤ 200ms in production):
```json
{
  "qualityAttribute": "PERFORMANCE",
  "metricName": "P95 Response Time",
  "comparisonOperator": "LTE",
  "targetValue": 200,
  "unit": "ms",
  "measurementWindow": "1 hour",
  "environment": "production"
}
```

### Response shape

```json
{
  "requirementId": "uuid",
  "qualityAttribute": "string",
  "metricName": "string | null",
  "comparisonOperator": "string | null",
  "targetValue": "number | null",
  "secondaryTargetValue": "number | null",
  "unit": "string | null",
  "measurementWindow": "string | null",
  "environment": "string | null",
  "verificationFrequency": "string | null",
  "configurationJson": "string | null",
  "createdAt": "string",
  "updatedAt": "string"
}
```

---

## 11. NFR Targets 🆕

> **New in Phase 3.** Defines what system scopes (modules, APIs, components…) the NFR applies to. PUT replaces the entire list atomically.

### Get NFR targets

```
GET /api/projects/{projectId}/requirements/{requirementId}/nfr-targets
```

### Replace NFR targets

```
PUT /api/projects/{projectId}/requirements/{requirementId}/nfr-targets
```

```json
{
  "targets": [
    {
      "targetType": "SYSTEM | MODULE | FUNCTION | API | COMPONENT | ENTITY | INFRASTRUCTURE",
      "targetId": "uuid | null",
      "targetLabel": "string | null",
      "displayOrder": 0
    }
  ]
}
```

- `targetId` — optional reference to a specific entity (function, component, API…)
- `targetLabel` — free-text label when no entity reference is available
- Send `{ "targets": [] }` to clear all targets

### Response shape

```json
{
  "requirementId": "uuid",
  "targets": [
    {
      "id": "uuid",
      "requirementId": "uuid",
      "targetType": "string",
      "targetId": "uuid | null",
      "targetLabel": "string | null",
      "displayOrder": 0,
      "createdAt": "string"
    }
  ]
}
```

---

## 12. Releases

Base: `/api/projects/{projectId}/releases`

| Method | Path | Description |
|--------|------|-------------|
| `GET` | `/` | List releases |
| `POST` | `/` | Create release package |
| `GET` | `/{releasePackageId}` | Get release detail |
| `POST` | `/{releasePackageId}/check-readiness` | Check readiness |
| `POST` | `/{releasePackageId}/mark-ready` | Mark ready |
| `POST` | `/{releasePackageId}/mark-released` | Mark released |
| `POST` | `/{releasePackageId}/mark-rolled-back` | Mark rolled back |
| `PATCH` | `/{releasePackageId}/archive` | Archive |

**Release items** — Base: `/api/projects/{projectId}/releases/{releasePackageId}/items`

| Method | Path | Description |
|--------|------|-------------|
| `GET` | `/` | List items |
| `POST` | `/` | Add item |
| `PATCH` | `/{itemId}/archive` | Remove item |

### Create release

```json
{
  "code": "string (required)",
  "versionLabel": "string (required)",
  "name": "string (required)",
  "releaseType": "MAJOR | MINOR | PATCH | HOTFIX | EMERGENCY",
  "description": "string | null",
  "plannedReleaseDate": "string (ISO 8601) | null"
}
```

---

## 13. Deployments

**Deployment environments** — Base: `/api/projects/{projectId}/deployment-environments`

| Method | Path | Description |
|--------|------|-------------|
| `GET` | `/` | List environments |
| `POST` | `/` | Create environment |
| `GET` | `/{envId}` | Get environment |
| `PATCH` | `/{envId}/archive` | Archive |

**Deployment records** — Base: `/api/projects/{projectId}/deployments`

| Method | Path | Description |
|--------|------|-------------|
| `GET` | `/` | List deployments |
| `POST` | `/` | Create deployment |
| `GET` | `/{deploymentId}` | Get deployment |
| `POST` | `/{deploymentId}/start` | Start |
| `POST` | `/{deploymentId}/succeed` | Mark succeeded |
| `POST` | `/{deploymentId}/fail` | Mark failed |
| `POST` | `/{deploymentId}/rollback` | Rollback |

**Rollback plans** — Base: `/api/projects/{projectId}/rollback-plans`

| Method | Path | Description |
|--------|------|-------------|
| `GET` | `/` | List |
| `POST` | `/` | Create |
| `GET` | `/{planId}` | Get |
| `POST` | `/{planId}/approve` | Approve |

---

## 14. Reports

Base: `/api/projects/{projectId}/reports`

| Method | Path | Description |
|--------|------|-------------|
| `GET` | `/quality-dashboard` | Overall quality dashboard |
| `GET` | `/defects` | Defect analysis report |
| `GET` | `/release-readiness` | Release readiness |
| `GET` | `/test-execution` | Test execution summary |
| `GET` | `/test-coverage` | Test coverage matrix |
| `GET` | `/defect-aging` | Defect aging analysis |
| `GET` | `/deployment-history` | Deployment history |

---

## Standard Response Envelope

**Success:**
```json
{
  "success": true,
  "data": { ... },
  "timestamp": "2026-07-30T..."
}
```

**Paginated list:**
```json
{
  "success": true,
  "data": {
    "items": [ ... ],
    "page": 0,
    "size": 20,
    "totalElements": 100,
    "totalPages": 5,
    "first": true,
    "last": false
  }
}
```

**Error:**
```json
{
  "success": false,
  "errorCode": "VERIFICATION_CASE_NOT_FOUND",
  "message": "...",
  "traceId": "...",
  "timestamp": "..."
}
```

---

## Domain Enums Reference

### Run Scope
`FUNCTIONAL` | `NON_FUNCTIONAL` | `MIXED`

### Verification Result Status
`PASSED` | `FAILED` | `BLOCKED` | `SKIPPED` | `NOT_RUN`

### Verification Method
`LOAD_TEST` | `PERFORMANCE_TEST` | `SECURITY_SCAN` | `PENETRATION_TEST` | `AVAILABILITY_CHECK` | `ACCESSIBILITY_AUDIT` | `COMPLIANCE_REVIEW` | `MANUAL_REVIEW` | `MONITORING_CHECK`

### NFR Quality Attribute
`PERFORMANCE` | `SECURITY` | `AVAILABILITY` | `RELIABILITY` | `SCALABILITY` | `USABILITY` | `ACCESSIBILITY` | `COMPATIBILITY` | `MAINTAINABILITY` | `OBSERVABILITY` | `DATA_INTEGRITY` | `COMPLIANCE`

### NFR Comparison Operator
`LT` (<) | `LTE` (≤) | `GT` (>) | `GTE` (≥) | `EQ` (=) | `BETWEEN`

### NFR Target Type
`SYSTEM` | `MODULE` | `FUNCTION` | `API` | `COMPONENT` | `ENTITY` | `INFRASTRUCTURE`

### Verification Case Status
`DRAFT` | `READY` | `DEPRECATED` | `ARCHIVED`

### Test Case Status
`DRAFT` | `READY` | `APPROVED` | `ARCHIVED`

### Test Execution Result
`PASSED` | `FAILED` | `BLOCKED` | `SKIPPED` | `NOT_RUN`

### Automation Status
`MANUAL` | `PLANNED` | `AUTOMATED`
