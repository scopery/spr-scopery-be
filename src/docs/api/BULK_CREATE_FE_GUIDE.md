# Bulk Create — Frontend Integration Guide

> **Applies to:** All `POST /bulk` endpoints across Traceability, Project, and Quality modules.  
> **Pattern:** Submit → Poll → Display result. No long-loading. No blocking.

---

## Overview

Bulk create is **asynchronous**. The flow is always the same regardless of entity:

```
FE sends payload
      │
      ▼
BE returns 202 + jobId   ← immediate, no waiting
      │
      ▼
FE polls GET /api/bulk-jobs/{jobId}   ← every 2–3 seconds
      │
      ├─ status = RUNNING  →  show progress bar, keep polling
      │
      ├─ status = SUCCEEDED  →  show success toast, stop polling
      ├─ status = PARTIAL    →  show partial result UI, stop polling
      └─ status = FAILED     →  show error UI, stop polling
```

The backend worker picks up jobs every **3 seconds** and processes items one by one. After each item, `succeededItems` and `failedItems` are updated in the database, so the progress visible to FE is **real-time per item**, not just 0% → 100%.

---

## Step 1 — Submit the job

### Request

```
POST /api/projects/{projectId}/requirements/bulk
Authorization: Bearer <token>
Content-Type: application/json

{
  "items": [
    {
      "title": "User must be able to login via email",
      "code": "REQ-001",
      "requirementType": "FUNCTIONAL",
      "priority": "HIGH"
    },
    {
      "title": "System response time < 2s under load",
      "code": "REQ-002",
      "requirementType": "TECHNICAL",
      "priority": "MEDIUM"
    }
  ]
}
```

**Max items per request:** 500. Sending more returns `400 Bad Request`.

### Response — `202 Accepted`

```json
{
  "success": true,
  "data": {
    "id": "f47ac10b-58cc-4372-a567-0e02b2c3d479",
    "jobType": "BULK_CREATE_REQUIREMENT",
    "status": "QUEUED",
    "totalItems": 2,
    "succeededItems": 0,
    "failedItems": 0,
    "resultSummary": null,
    "errorMessage": null,
    "createdAt": "2026-08-01T10:00:00Z",
    "updatedAt": "2026-08-01T10:00:00Z"
  }
}
```

**Save `data.id` immediately** — this is the `jobId` you'll poll with.

---

## Step 2 — Poll for progress

```
GET /api/bulk-jobs/{jobId}
Authorization: Bearer <token>
```

Poll at a **2–3 second interval**. Stop when `status` is one of: `SUCCEEDED`, `PARTIAL`, `FAILED`.

### While running — `status: RUNNING`

```json
{
  "success": true,
  "data": {
    "id": "f47ac10b-58cc-4372-a567-0e02b2c3d479",
    "status": "RUNNING",
    "totalItems": 100,
    "succeededItems": 37,
    "failedItems": 2,
    "resultSummary": null,
    "errorMessage": null
  }
}
```

**Progress percentage:**

```ts
const percent = Math.round(
  ((job.succeededItems + job.failedItems) / job.totalItems) * 100
);
// → 39%
```

Display a progress bar using `(succeededItems + failedItems) / totalItems`.

**Recommended polling logic (TypeScript):**

```ts
async function pollBulkJob(jobId: string, onProgress: (job: BulkJobResponse) => void) {
  const POLL_INTERVAL_MS = 2500;
  const TIMEOUT_MS = 10 * 60 * 1000; // 10 minutes max
  const start = Date.now();

  while (Date.now() - start < TIMEOUT_MS) {
    const res = await api.get(`/api/bulk-jobs/${jobId}`);
    const job = res.data.data;

    onProgress(job);

    if (['SUCCEEDED', 'PARTIAL', 'FAILED'].includes(job.status)) {
      return job;
    }

    await sleep(POLL_INTERVAL_MS);
  }

  throw new Error('Bulk job timed out after 10 minutes');
}
```

---

## Step 3 — Handle the final result

### `SUCCEEDED` — All items created

```json
{
  "status": "SUCCEEDED",
  "totalItems": 50,
  "succeededItems": 50,
  "failedItems": 0,
  "resultSummary": "Created 50 items"
}
```

**UI action:** Show a success toast. Refresh the list.

---

### `PARTIAL` — Some items created, some failed

```json
{
  "status": "PARTIAL",
  "totalItems": 50,
  "succeededItems": 47,
  "failedItems": 3,
  "resultSummary": "47 created, 3 failed"
}
```

**UI action:** Show a warning banner with the summary. Example:

> ✓ 47 items created successfully. 3 items could not be created (duplicate code or missing required fields). The successful items are already saved.

**Important:** Items that succeeded are **already persisted**. Partial result does not roll anything back. FE should let the user know which rows might have failed (if row-level IDs were tracked) or suggest they re-check the import file.

---

### `FAILED` — All items failed (or system error)

```json
{
  "status": "FAILED",
  "totalItems": 50,
  "succeededItems": 0,
  "failedItems": 50,
  "resultSummary": null,
  "errorMessage": "All 50 items failed"
}
```

Or in case of a system-level failure (e.g. payload could not be parsed):

```json
{
  "status": "FAILED",
  "totalItems": 50,
  "succeededItems": 0,
  "failedItems": 0,
  "errorMessage": "No handler registered for job type: ..."
}
```

**UI action:** Show an error state. Allow retry (see Step 4).

---

## Step 4 — Retry

The bulk job system does **not** auto-retry failed jobs. If a job ends in `FAILED` or `PARTIAL`, the user must re-submit.

**Recommended retry strategy:**

| Result | User action |
|---|---|
| `SUCCEEDED` | Nothing. Done. |
| `PARTIAL` | Re-submit only the items that failed. FE should track which rows were sent and which might have failed (use `failedItems` count as a hint). |
| `FAILED` | Fix the data (e.g. remove duplicate codes) and re-submit the full batch. |
| Network error while polling | Resume polling with the same `jobId` — the job is still running on the server. |

**Re-submitting after `PARTIAL`:**

If FE tracked which items were sent and in what order, you can infer that roughly the first `succeededItems` rows passed and the last `failedItems` rows failed. However, BE does not guarantee insertion order matches input order for concurrent cases, so the safest approach is to let the user fix and re-upload the original file, then de-duplicate on their end.

**Do not re-use a `jobId`** — each submission creates a new job. There is no "resume job" API.

---

## Status state machine

```
QUEUED
  │
  └─► RUNNING
        │
        ├─► SUCCEEDED   (failedItems = 0)
        ├─► PARTIAL     (succeededItems > 0 AND failedItems > 0)
        └─► FAILED      (succeededItems = 0, or system error before processing)
```

Once a job reaches `SUCCEEDED`, `PARTIAL`, or `FAILED`, its status **never changes**. Safe to cache the final response.

---

## Supported endpoints

| Entity | Submit endpoint | jobType |
|---|---|---|
| Requirement | `POST /api/projects/{projectId}/requirements/bulk` | `BULK_CREATE_REQUIREMENT` |
| Functional Item | `POST /api/projects/{projectId}/functional-items/bulk` | `BULK_CREATE_FUNCTIONAL_ITEM` |
| Non-Functional Item | `POST /api/projects/{projectId}/non-functional-items/bulk` | `BULK_CREATE_NON_FUNCTIONAL_ITEM` |
| Use Case | `POST /api/projects/{projectId}/use-cases/bulk` | `BULK_CREATE_USE_CASE` |
| Screen | `POST /api/workspaces/{workspaceId}/applications/{applicationId}/screens/bulk` | `BULK_CREATE_REGISTRY_SCREEN` |
| API Endpoint | `POST /api/workspaces/{workspaceId}/applications/{applicationId}/api-endpoints/bulk` | `BULK_CREATE_REGISTRY_API_ENDPOINT` |
| Data Entity | `POST /api/workspaces/{workspaceId}/applications/{applicationId}/data-entities/bulk` | `BULK_CREATE_REGISTRY_DATA_ENTITY` |
| App Module | `POST /api/workspaces/{workspaceId}/applications/{applicationId}/modules/bulk` | `BULK_CREATE_REGISTRY_APP_MODULE` |
| App Component | `POST /api/workspaces/{workspaceId}/applications/{applicationId}/components/bulk` | `BULK_CREATE_REGISTRY_APP_COMPONENT` |
| WBS Node | `POST /api/projects/{projectId}/wbs-nodes/bulk` | `BULK_CREATE_WBS_NODE` |
| Project Phase | `POST /api/projects/{projectId}/phases/bulk` | `BULK_CREATE_PROJECT_PHASE` |
| Task | `POST /api/projects/{projectId}/tasks/bulk` | `BULK_CREATE_TASK` |
| Test Case | `POST /api/projects/{projectId}/test-cases/bulk` | `BULK_CREATE_TEST_CASE` |

Poll URL (same for all): `GET /api/bulk-jobs/{jobId}`

---

## Payload field reference

### Requirement

```json
{
  "items": [{
    "title": "string (required)",
    "code": "string (optional, e.g. REQ-001)",
    "description": "string (optional)",
    "requirementType": "FUNCTIONAL | NON_FUNCTIONAL | BUSINESS | TECHNICAL | SECURITY | COMPLIANCE | OTHER",
    "priority": "CRITICAL | HIGH | MEDIUM | LOW",
    "applicationId": "uuid (optional)",
    "functionalItemId": "uuid (optional)",
    "nonFunctionalItemId": "uuid (optional)",
    "scopeItemId": "uuid (optional)",
    "scopePackageId": "uuid (optional)"
  }]
}
```

### Functional Item

```json
{
  "items": [{
    "code": "string (required)",
    "title": "string (required)",
    "description": "string (optional)",
    "priority": "LOW | MEDIUM | HIGH | CRITICAL",
    "type": "FUNCTIONAL | USER_STORY | USE_CASE",
    "acceptanceCriteria": ["string", "string"],
    "workspaceId": "uuid (optional)",
    "moduleId": "uuid (optional)"
  }]
}
```

### Non-Functional Item

```json
{
  "items": [{
    "code": "string (required)",
    "title": "string (required)",
    "description": "string (optional)",
    "category": "PERFORMANCE | SECURITY | USABILITY | RELIABILITY | MAINTAINABILITY | SCALABILITY | COMPATIBILITY | OTHER",
    "priority": "LOW | MEDIUM | HIGH | CRITICAL",
    "scopeType": "SYSTEM | MODULE | FEATURE",
    "targetMetric": "string (optional)",
    "workspaceId": "uuid (optional)",
    "scopeRefId": "uuid (optional)"
  }]
}
```

### Use Case

```json
{
  "items": [{
    "primaryFunctionId": "uuid string (optional — can be linked later)",
    "key": "string (required, e.g. UC-001)",
    "name": "string (required)",
    "goal": "string (optional)",
    "primaryActorName": "string (optional)",
    "triggerText": "string (optional)"
  }]
}
```

### Task

```json
{
  "items": [{
    "projectPhaseId": "uuid (required)",
    "wbsNodeId": "uuid (optional)",
    "code": "string (required)",
    "title": "string (required)",
    "description": "string (optional)",
    "priority": "LOW | MEDIUM | HIGH | CRITICAL",
    "estimateHours": 8.0,
    "plannedStartDate": "2026-08-15",
    "dueDate": "2026-08-20",
    "inChargeUserId": "uuid (optional)",
    "plannedRoleCode": "string (optional)",
    "plannedRoleName": "string (optional)"
  }]
}
```

### Test Case

```json
{
  "items": [{
    "title": "string (required)",
    "code": "string (optional)",
    "description": "string (optional)",
    "type": "FUNCTIONAL | NEGATIVE | REGRESSION | UAT | SMOKE | PERFORMANCE | SECURITY | OTHER",
    "priority": "LOW | MEDIUM | HIGH | CRITICAL",
    "automationStatus": "MANUAL | PLANNED | AUTOMATED",
    "testSuiteId": "uuid (optional)",
    "useCaseId": "uuid (optional)",
    "preconditions": "string (optional)",
    "expectedResult": "string (optional)",
    "assigneeId": "uuid (optional)"
  }]
}
```

### WBS Node

```json
{
  "items": [{
    "code": "string (required)",
    "title": "string (required)",
    "description": "string (optional)",
    "nodeType": "WORK_PACKAGE | DELIVERABLE | TASK_GROUP",
    "phaseId": "uuid (required — must belong to the same project)",
    "parentId": "uuid (optional)",
    "sortOrder": 0
  }]
}
```

### Project Phase

```json
{
  "items": [{
    "code": "string (required)",
    "name": "string (required)",
    "description": "string (optional)",
    "displayOrder": 0,
    "plannedStartDate": "2026-09-01",
    "plannedEndDate": "2026-12-31"
  }]
}
```

---

## Edge cases

| Scenario | Behavior |
|---|---|
| FE closes tab while job is running | Job keeps running on the server. Re-open and poll with the saved `jobId`. |
| 1 item out of 500 fails | `PARTIAL`: 499 items created, 1 failed. The 499 are saved. |
| Network error while submitting (no response) | Treat as unknown — do NOT re-submit blindly. First check if a job was created by inspecting server logs or asking BE. Otherwise you risk duplicates. |
| `status = QUEUED` for more than 30 seconds | Worker may be busy with concurrent jobs (default: 3 concurrent). Keep polling — the job will eventually be picked up. |
| `totalItems = 0` in response | Validation would reject before submission (min 1 item). This should not happen. |
| Submitting 501+ items | BE returns `400 Bad Request` synchronously. Show inline validation error, do not show a progress UI. |

---

## TestCase: `/batch` vs `/bulk`

The TestCase endpoint has **two** create operations:

| Endpoint | Behavior | When to use |
|---|---|---|
| `POST /test-cases/batch` | **Synchronous** — processes all items in one transaction, returns result immediately | Small batches (< 50), need immediate result in same request |
| `POST /test-cases/bulk` | **Asynchronous** — returns `202 + jobId`, poll for progress | Large imports (50–500 items), CSV/Excel upload flows |

Use `/bulk` for any import-style UX where the user is uploading a file with many items.
