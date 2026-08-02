# Traceability Coverage — Phase & API Reference

> **Mục đích:** Mô tả các phase triển khai tính năng Traceability Coverage và toàn bộ API đã implement với format input/output chi tiết.

---

## Tổng quan mô hình Coverage

Mỗi requirement có một **coverage chain** đi qua 4 lớp:

```
Requirement
  └── Function(s)            [SATISFIED_BY]
        └── Use Case(s)      [COVERED_BY]
              └── Screen / API Endpoint  [IMPLEMENTED_BY]
  └── Test Case(s)           [TESTED_BY]  ← via traceability_link
```

**Coverage status** của một requirement:
- `COMPLETE` — không có gap nào
- `PARTIAL` — có ít nhất 1 gap code

**Gap codes** (được tính theo thứ tự):

| Gap Code | Điều kiện phát sinh |
|---|---|
| `MISSING_FUNCTION` | `requiresUseCase` = true AND `fn_count = 0` |
| `MISSING_USE_CASE` | `requiresUseCase` = true AND `uc_count = 0` |
| `INCOMPLETE_USE_CASE` | `requiresUseCase` = true AND `uc_count > 0` AND không có UC nào ở `READY_FOR_REVIEW`/`COMPLETE` |
| `MISSING_IMPLEMENTATION` | Không có screen hoặc API endpoint nào link qua function |
| `MISSING_TEST` | `tc_count = 0` |
| `TEST_FAILED` | Latest test result = `FAILED` |
| `BLOCKED` | Latest test result = `BLOCKED` |

**`requiresUseCase` logic:**

| Giá trị field `requires_use_case` | `requiresUseCaseResolved` | UC chain bắt buộc? |
|---|---|---|
| `YES` | `true` | Có |
| `NO` | `false` | Không |
| `AUTO` + type = `FUNCTIONAL` | `true` | Có |
| `AUTO` + type = `NON_FUNCTIONAL` | `false` | Không |

---

## Phase Implementation

### Phase 0 — Domain model & DB migration (Done)

**Migration:** `V175__add_requires_use_case_to_requirement.sql`
```sql
ALTER TABLE requirements_requirement
    ADD COLUMN IF NOT EXISTS requires_use_case VARCHAR(10) NOT NULL DEFAULT 'AUTO';
```

**Thay đổi domain:**

- `Requirement.java` — thêm field `String requiresUseCase` (giá trị: `"AUTO"` / `"YES"` / `"NO"`)
- `Requirement.create()` — default `"AUTO"`
- `Requirement.update()` — nhận thêm param `String requiresUseCase`
- `Requirement.withRequiresUseCase(String)` — explicit setter method

**Thay đổi application layer:**

- `UpdateRequirementRequest` — thêm field `String requiresUseCase`
- `UpdateRequirementCommand` — thêm field `String requiresUseCase`
- `UpdateRequirementAction` — truyền `c.requiresUseCase()` vào `req.update()`
- `RequirementController` — truyền `r.requiresUseCase()` vào command
- `RequirementResponse` — thêm `String requiresUseCase` + `boolean requiresUseCaseResolved`
- `RequirementJpaEntity` — thêm `@Column(name="requires_use_case") private String requiresUseCase`
- `RequirementPersistenceMapper` — map 2 chiều, default `"AUTO"` khi null

---

### Phase 1 — Coverage Query Service (Done) — P0

**Sub-module mới:** `modules/traceability/coverage/`

```
coverage/
├── application/
│   ├── response/
│   │   ├── CoverageSummaryResponse.java
│   │   ├── TraceabilityMatrixResponse.java
│   │   ├── RequirementTraceDetailResponse.java
│   │   └── GapsResponse.java
│   └── service/
│       └── TraceabilityCoverageQueryService.java
└── http/
    └── controller/
        └── TraceabilityCoverageController.java
```

**Kỹ thuật:**
- `@PersistenceContext EntityManager` + native SQL với CTE
- CTE `cov` tổng hợp fn_count, uc_count, cuc_count, has_impl, tc_count, latest_result cho mỗi requirement
- Gap codes và coverage status được tính trong Java từ dữ liệu raw
- Preview objects được load bằng `ANY(:reqIds)` batched queries (tránh N+1)

---

### Phase 2 — Linkable Pickers + Mention Resolve + History (Done) — P1/P2

**P1 — Linkable pickers:** Danh sách object có thể link vào requirement (chưa được link, không ARCHIVED).
- `GET /api/projects/{projectId}/requirements/{requirementId}/linkable-functions`
- `GET /api/projects/{projectId}/requirements/{requirementId}/linkable-use-cases`

**P2 — Mention resolve:** Endpoint nhẹ để set riêng field `requiresUseCase` mà không cần gửi toàn bộ update body.
- `PATCH /api/projects/{projectId}/requirements/{requirementId}/requires-use-case`

**P2 — History on detail:** Activity log của requirement theo thời gian ngược.
- `GET /api/projects/{projectId}/traceability/requirements/{requirementId}/history`

**Files mới (P2):**
- `SetRequiresUseCaseRequest.java` — validate `YES|NO|AUTO`
- `SetRequiresUseCaseCommand.java`
- `SetRequiresUseCaseAction.java` — gọi `req.withRequiresUseCase()`, log `REQUIREMENT_REQUIRES_USE_CASE_SET`
- `RequirementTraceHistoryResponse.java`
- `TraceabilityCoverageQueryService.getRequirementHistory()` — query `app_activity_log`, newest first

Xem chi tiết ở phần **API Reference — Linkable Picker & P2 Endpoints** bên dưới.

---

### Phase 3 — Frontend Coverage UI (Pending) — P1

> Chưa implement. Các tab Coverage, Matrix, Gaps trong requirement detail panel.

---

## API Reference — Coverage Endpoints

> **Base path:** `/api/projects/{projectId}/traceability`
>
> **Auth:** Bearer JWT. Cần quyền view project.
>
> **Response envelope:** Tất cả response đều wrap trong `ApiResponse`:
> ```json
> { "success": true, "data": { ... }, "timestamp": "2026-07-30T..." }
> ```

---

### 1. GET `/api/projects/{projectId}/traceability/coverage-summary`

**Mô tả:** KPI dashboard tổng quan coverage của toàn bộ project.

#### Path params

| Param | Type | Mô tả |
|---|---|---|
| `projectId` | UUID | ID của project |

#### Query params

Không có.

#### Response `data`

```json
{
  "requirements": 42,
  "completeCount": 18,
  "completeCoveragePct": 43,
  "partialCount": 24,
  "missingFunctions": 10,
  "missingUseCases": 8,
  "missingImplementation": 14,
  "missingTests": 20,
  "failedTests": 3,
  "blocked": 1,
  "layerCoverage": {
    "functionPct": 76,
    "useCasePct": 60,
    "implementationPct": 55,
    "testPct": 52
  },
  "funnel": [
    { "stage": "REQUIREMENTS",       "count": 42 },
    { "stage": "HAS_FUNCTION",       "count": 32 },
    { "stage": "HAS_USE_CASE",       "count": 25 },
    { "stage": "HAS_IMPLEMENTATION", "count": 23 },
    { "stage": "HAS_TEST",           "count": 22 }
  ],
  "byRequirementType": [
    {
      "requirementType": "FUNCTIONAL",
      "total": 30,
      "completeCount": 14,
      "completePct": 47,
      "gapCounts": {
        "MISSING_FUNCTION": 6,
        "MISSING_USE_CASE": 5,
        "MISSING_IMPLEMENTATION": 9,
        "MISSING_TEST": 12,
        "TEST_FAILED": 2,
        "BLOCKED": 1
      }
    },
    {
      "requirementType": "NON_FUNCTIONAL",
      "total": 12,
      "completeCount": 4,
      "completePct": 33,
      "gapCounts": {
        "MISSING_FUNCTION": 4,
        "MISSING_USE_CASE": 3,
        "MISSING_IMPLEMENTATION": 5,
        "MISSING_TEST": 8,
        "TEST_FAILED": 1,
        "BLOCKED": 0
      }
    }
  ],
  "generatedAt": "2026-07-30T10:00:00Z",
  "stale": false
}
```

**Ghi chú:**
- `requirements` — số lượng requirement không ở trạng thái `ARCHIVED` hoặc `REJECTED`
- `completeCoveragePct` — `completeCount / requirements * 100` (làm tròn)
- `layerCoverage.*Pct` — % requirement có ít nhất 1 object ở lớp đó
- `funnel` — dữ liệu theo từng lớp để render funnel chart
- `byRequirementType.gapCounts` — số requirement có gap đó (có thể overlap, 1 req có nhiều gap)

---

### 2. GET `/api/projects/{projectId}/traceability/matrix`

**Mô tả:** Danh sách requirements với coverage status và gap codes từng requirement. Hỗ trợ filter, search, phân trang.

#### Path params

| Param | Type | Mô tả |
|---|---|---|
| `projectId` | UUID | ID của project |

#### Query params

| Param | Type | Default | Mô tả |
|---|---|---|---|
| `q` | string | — | Tìm theo code hoặc title (case-insensitive, LIKE) |
| `coverageStatus` | string | — | Filter: `COMPLETE` hoặc `PARTIAL` |
| `gapCode` | string | — | Filter theo gap code cụ thể (xem bảng gap codes) |
| `requirementType` | string | — | Filter: `FUNCTIONAL` hoặc `NON_FUNCTIONAL` |
| `showGapsOnly` | boolean | `false` | Chỉ trả về requirements có ít nhất 1 gap |
| `limit` | int | `50` | Số items per page |
| `offset` | int | `0` | Vị trí bắt đầu |

#### Response `data`

```json
{
  "summary": {
    "requirements": 42,
    "completeCount": 18,
    "completeCoveragePct": 43,
    "partialCount": 24,
    "missingFunctions": 10,
    "missingUseCases": 8,
    "missingImplementation": 14,
    "missingTests": 20,
    "failedTests": 3,
    "blocked": 1
  },
  "items": [
    {
      "requirementId": "uuid-...",
      "code": "REQ-001",
      "title": "User can log in with email and password",
      "requirementType": "FUNCTIONAL",
      "priority": "HIGH",
      "requiresUseCase": "AUTO",
      "requiresUseCaseResolved": true,
      "coverageStatus": "PARTIAL",
      "gapCodes": ["MISSING_TEST", "TEST_FAILED"],
      "functionCount": 2,
      "useCaseCount": 3,
      "implementationCount": 5,
      "testCaseCount": 0,
      "latestResult": null,
      "latestResultAt": null,
      "openDefectCount": 0,
      "previews": {
        "functions": [
          { "id": "uuid-...", "objectType": "FUNCTION", "code": "FN-001", "name": "Login Flow", "relationKind": "SATISFIED_BY" },
          { "id": "uuid-...", "objectType": "FUNCTION", "code": "FN-002", "name": "Auth Token", "relationKind": "SATISFIED_BY" }
        ],
        "useCases": [
          { "id": "uuid-...", "objectType": "USE_CASE", "code": "UC-001", "name": "Login with email", "relationKind": "COVERED_BY" }
        ],
        "implementation": [
          { "id": "uuid-...", "objectType": "SCREEN", "code": "SCR-001", "name": "Login Screen", "relationKind": "IMPLEMENTED_BY" }
        ],
        "testCases": []
      },
      "previewMore": {
        "functions": 0,
        "useCases": 1,
        "implementation": 3,
        "testCases": 0
      }
    }
  ],
  "page": {
    "limit": 50,
    "offset": 0,
    "total": 24
  },
  "generatedAt": "2026-07-30T10:00:00Z",
  "stale": false
}
```

**Ghi chú:**
- `summary` — tính trên toàn bộ kết quả sau filter (không phải chỉ trang hiện tại)
- `previews.*` — tối đa 2 objects per lớp
- `previewMore.*` — số objects còn lại (`count - 2`, min 0)
- `openDefectCount` — hiện tại luôn là `0` (P2 feature)
- Filter `gapCode` và `coverageStatus` được apply trong Java sau khi query, không phải SQL

---

### 3. GET `/api/projects/{projectId}/traceability/requirements/{requirementId}`

**Mô tả:** Chi tiết coverage đầy đủ cho 1 requirement, bao gồm toàn bộ objects theo từng lớp và danh sách gap items.

#### Path params

| Param | Type | Mô tả |
|---|---|---|
| `projectId` | UUID | ID của project |
| `requirementId` | UUID | ID của requirement |

#### Query params

Không có.

#### Response `data`

```json
{
  "requirement": {
    "id": "uuid-...",
    "code": "REQ-001",
    "title": "User can log in with email and password",
    "requirementType": "FUNCTIONAL",
    "priority": "HIGH",
    "requiresUseCase": "AUTO",
    "requiresUseCaseResolved": true
  },
  "coverageStatus": "PARTIAL",
  "gapCodes": ["MISSING_TEST"],
  "coverageScore": {
    "pct": 80,
    "layers": {
      "function": true,
      "useCase": true,
      "implementation": true,
      "test": false,
      "passing": false
    }
  },
  "functions": [
    {
      "id": "uuid-...",
      "objectType": "FUNCTION",
      "code": "FN-001",
      "name": "Login Flow",
      "relationKind": "SATISFIED_BY",
      "completenessStatus": null,
      "latestResult": null
    }
  ],
  "useCases": [
    {
      "id": "uuid-...",
      "objectType": "USE_CASE",
      "code": "UC-001",
      "name": "Login with email",
      "relationKind": "COVERED_BY",
      "completenessStatus": "COMPLETE",
      "latestResult": null
    }
  ],
  "implementationObjects": [
    {
      "id": "uuid-...",
      "objectType": "SCREEN",
      "code": "SCR-001",
      "name": "Login Screen",
      "relationKind": "IMPLEMENTED_BY",
      "completenessStatus": null,
      "latestResult": null
    },
    {
      "id": "uuid-...",
      "objectType": "API_ENDPOINT",
      "code": "POST /api/auth/login",
      "name": "Login endpoint",
      "relationKind": "IMPLEMENTED_BY",
      "completenessStatus": null,
      "latestResult": null
    }
  ],
  "testCases": [],
  "gaps": [
    {
      "gapCode": "MISSING_TEST",
      "priority": "MEDIUM",
      "message": "Requirement REQ-001 has no linked test cases",
      "recommendedAction": "Link at least one test case via TESTED_BY",
      "relatedObject": null
    }
  ],
  "generatedAt": "2026-07-30T10:00:00Z"
}
```

**Ghi chú:**
- `coverageScore.pct` — % số lớp đã pass (nếu requiresUseCase: 5 lớp fn+uc+impl+tc+passing; nếu không: 3 lớp impl+tc+passing)
- `implementationObjects.code` cho `API_ENDPOINT` — format là `"METHOD /path"` (e.g. `"POST /api/auth/login"`)
- `gaps.priority` — `HIGH` cho TEST_FAILED/BLOCKED, `MEDIUM` cho MISSING_TEST/MISSING_IMPLEMENTATION, `LOW` cho các gap còn lại
- 404 nếu requirement không tồn tại trong project

---

### 4. GET `/api/projects/{projectId}/traceability/gaps`

**Mô tả:** Danh sách tất cả gaps, mỗi gap là 1 item riêng (1 requirement có nhiều gap → nhiều items). Hỗ trợ filter và phân trang.

#### Path params

| Param | Type | Mô tả |
|---|---|---|
| `projectId` | UUID | ID của project |

#### Query params

| Param | Type | Default | Mô tả |
|---|---|---|---|
| `gapCode` | string | — | Filter theo gap code cụ thể |
| `priority` | string | — | Filter theo priority của requirement: `HIGH`, `MEDIUM`, `LOW` |
| `requirementId` | string (UUID) | — | Chỉ lấy gap của 1 requirement cụ thể |
| `q` | string | — | Tìm theo requirement code hoặc title |
| `limit` | int | `50` | Số items per page |
| `offset` | int | `0` | Vị trí bắt đầu |

#### Response `data`

```json
{
  "summary": {
    "total": 47,
    "byGapCode": {
      "MISSING_FUNCTION": 10,
      "MISSING_USE_CASE": 8,
      "INCOMPLETE_USE_CASE": 3,
      "MISSING_IMPLEMENTATION": 14,
      "MISSING_TEST": 20,
      "TEST_FAILED": 3,
      "BLOCKED": 1
    }
  },
  "items": [
    {
      "id": "uuid-REQ-001_MISSING_TEST",
      "gapCode": "MISSING_TEST",
      "priority": "HIGH",
      "requirement": {
        "id": "uuid-...",
        "code": "REQ-001",
        "title": "User can log in with email and password"
      },
      "relatedObject": null,
      "recommendedAction": "Link at least one test case via TESTED_BY",
      "message": "Requirement REQ-001 has no linked test cases"
    },
    {
      "id": "uuid-REQ-002_TEST_FAILED",
      "gapCode": "TEST_FAILED",
      "priority": "HIGH",
      "requirement": {
        "id": "uuid-...",
        "code": "REQ-002",
        "title": "Password reset flow"
      },
      "relatedObject": null,
      "recommendedAction": "Investigate and fix failing test cases",
      "message": "Latest test result for requirement REQ-002 is FAILED"
    }
  ],
  "page": {
    "limit": 50,
    "offset": 0,
    "total": 47
  },
  "generatedAt": "2026-07-30T10:00:00Z",
  "stale": false
}
```

**Ghi chú:**
- `summary.total` — tổng số gap items sau filter (không phải số requirements)
- `summary.byGapCode` — count theo loại gap (chỉ bao gồm gap codes xuất hiện trong kết quả)
- `items[].id` — composite key `"{requirementId}_{gapCode}"` (string, không phải UUID)
- `items[].relatedObject` — hiện tại luôn `null` (P2: sẽ gắn object liên quan)
- `items[].priority` — priority của *requirement*, không phải severity của gap

---

## API Reference — Linkable Picker & P2 Endpoints

### 5. GET `/api/projects/{projectId}/requirements/{requirementId}/linkable-functions`

**Mô tả:** Danh sách functions trong project chưa được link vào requirement này. Dùng cho dropdown picker khi thêm function link.

#### Query params

| Param | Type | Default | Mô tả |
|---|---|---|---|
| `q` | string | — | Tìm theo code hoặc title (case-insensitive) |
| `limit` | int | `20` | Số kết quả tối đa |

#### Response `data`

```json
[
  {
    "id": "uuid-...",
    "code": "FN-001",
    "title": "User Authentication Flow",
    "type": "FUNCTIONAL",
    "status": "ACTIVE"
  },
  {
    "id": "uuid-...",
    "code": "FN-002",
    "title": "Password Reset Flow",
    "type": "FUNCTIONAL",
    "status": "DRAFT"
  }
]
```

**Ghi chú:**
- Chỉ trả về functions thuộc `project_id` tương ứng
- Loại trừ functions đã có trong `app_requirement_function` cho `requirementId` này
- Loại trừ functions có `status = 'ARCHIVED'`
- Sắp xếp theo `code ASC`

---

### 6. GET `/api/projects/{projectId}/requirements/{requirementId}/linkable-use-cases`

**Mô tả:** Danh sách use cases trong project chưa được link vào requirement này. Dùng cho dropdown picker khi thêm use case link.

#### Query params

| Param | Type | Default | Mô tả |
|---|---|---|---|
| `q` | string | — | Tìm theo key hoặc name (case-insensitive) |
| `limit` | int | `20` | Số kết quả tối đa |

#### Response `data`

```json
[
  {
    "id": "uuid-...",
    "key": "UC-001",
    "name": "Login with email and password",
    "status": "ACTIVE",
    "completenessStatus": "IN_PROGRESS"
  },
  {
    "id": "uuid-...",
    "key": "UC-002",
    "name": "Reset password via email",
    "status": "ACTIVE",
    "completenessStatus": "COMPLETE"
  }
]
```

**Ghi chú:**
- Chỉ trả về use cases thuộc `project_id` tương ứng
- Loại trừ use cases đã có trong `app_requirement_use_case` cho `requirementId` này
- Loại trừ use cases có `status = 'ARCHIVED'`
- Sắp xếp theo `key ASC`

---

### 7. PATCH `/api/projects/{projectId}/requirements/{requirementId}/requires-use-case`

**Mô tả:** Set override cho field `requiresUseCase` của requirement. Endpoint nhẹ — không cần gửi toàn bộ update body.

#### Path params

| Param | Type | Mô tả |
|---|---|---|
| `projectId` | UUID | ID của project |
| `requirementId` | UUID | ID của requirement |

#### Request body

```json
{ "value": "YES" }
```

| Field | Type | Bắt buộc | Giá trị hợp lệ |
|---|---|---|---|
| `value` | string | Có | `YES` \| `NO` \| `AUTO` |

#### Response `data`

`RequirementResponse` đầy đủ (giống `PATCH /requirements/{id}`), với `requiresUseCase` và `requiresUseCaseResolved` đã được cập nhật.

**Ghi chú:**
- Validation 400 nếu `value` không phải `YES`, `NO`, hoặc `AUTO`
- Log activity `REQUIREMENT_REQUIRES_USE_CASE_SET`
- `requiresUseCaseResolved` được recompute tức thì trong response

---

### 8. GET `/api/projects/{projectId}/traceability/requirements/{requirementId}/history`

**Mô tả:** Lịch sử hoạt động của một requirement theo thứ tự thời gian ngược (newest first). Lấy từ `app_activity_log`.

#### Path params

| Param | Type | Mô tả |
|---|---|---|
| `projectId` | UUID | ID của project |
| `requirementId` | UUID | ID của requirement |

#### Query params

| Param | Type | Default | Mô tả |
|---|---|---|---|
| `limit` | int | `20` | Số items per page |
| `offset` | int | `0` | Vị trí bắt đầu |

#### Response `data`

```json
{
  "items": [
    {
      "id": "uuid-log-entry",
      "action": "REQUIREMENT_REQUIRES_USE_CASE_SET",
      "actorId": null,
      "actorName": null,
      "message": "requiresUseCase set to YES",
      "occurredAt": "2026-07-30T11:05:00Z"
    },
    {
      "id": "uuid-log-entry-2",
      "action": "REQUIREMENT_UPDATED",
      "actorId": null,
      "actorName": null,
      "message": "Requirement updated",
      "occurredAt": "2026-07-30T10:30:00Z"
    },
    {
      "id": "uuid-log-entry-3",
      "action": "REQUIREMENT_CREATED",
      "actorId": null,
      "actorName": null,
      "message": "Requirement created: REQ-001",
      "occurredAt": "2026-07-30T09:00:00Z"
    }
  ],
  "page": {
    "limit": 20,
    "offset": 0,
    "total": 3
  },
  "generatedAt": "2026-07-30T12:00:00Z"
}
```

**Action codes phổ biến trong history:**

| Action | Ý nghĩa |
|---|---|
| `REQUIREMENT_CREATED` | Requirement được tạo |
| `REQUIREMENT_UPDATED` | Cập nhật thông tin chung |
| `REQUIREMENT_REQUIRES_USE_CASE_SET` | Set giá trị `requiresUseCase` |
| `REQUIREMENT_APPROVED` | Chuyển sang APPROVED |
| `REQUIREMENT_REJECTED` | Chuyển sang REJECTED |
| `REQUIREMENT_DEFERRED` | Chuyển sang DEFERRED |
| `REQUIREMENT_IMPLEMENTED` | Chuyển sang IMPLEMENTED |
| `REQUIREMENT_ARCHIVED` | Archive requirement |
| `REQUIREMENT_FUNCTION_LINKED` | Link function vào requirement |
| `REQUIREMENT_FUNCTION_UNLINKED` | Unlink function |
| `REQUIREMENT_USE_CASE_LINKED` | Link use case vào requirement |
| `REQUIREMENT_USE_CASE_UNLINKED` | Unlink use case |

**Ghi chú:**
- `actorId` / `actorName` — hiện tại `null` (Security phase chưa populate)
- 404 nếu requirement không thuộc project
- Sắp xếp `occurredAt DESC`

---

## Requirement API — field `requiresUseCase`

Field `requiresUseCase` được thêm vào tất cả các API liên quan đến requirement.

### PATCH `/api/projects/{projectId}/requirements/{requirementId}`

**Request body (thêm field mới):**

```json
{
  "title": "string (optional)",
  "description": "string (optional)",
  "priority": "HIGH | MEDIUM | LOW (optional)",
  "requirementType": "FUNCTIONAL | NON_FUNCTIONAL (optional)",
  "applicationId": "uuid (optional)",
  "functionalItemId": "uuid (optional)",
  "nonFunctionalItemId": "uuid (optional)",
  "scopeItemId": "uuid (optional)",
  "scopePackageId": "uuid (optional)",
  "requiresUseCase": "AUTO | YES | NO (optional)"
}
```

**Response** (thêm 2 fields mới):

```json
{
  "success": true,
  "data": {
    "id": "uuid-...",
    "projectId": "uuid-...",
    "applicationId": "uuid-...",
    "code": "REQ-001",
    "title": "...",
    "description": "...",
    "requirementType": "FUNCTIONAL",
    "priority": "HIGH",
    "status": "DRAFT",
    "functionalItemId": "uuid-...",
    "nonFunctionalItemId": null,
    "scopeItemId": "uuid-...",
    "scopePackageId": "uuid-...",
    "requiresUseCase": "AUTO",
    "requiresUseCaseResolved": true,
    "createdAt": "2026-07-30T...",
    "updatedAt": "2026-07-30T..."
  }
}
```

`requiresUseCaseResolved` — computed field, không lưu DB:
```
"YES"               → true
"NO"                → false
"AUTO" + FUNCTIONAL → true
"AUTO" + NON_FUNCTIONAL → false
```

---

## Tables sử dụng trong Coverage queries

| Table | Mô tả |
|---|---|
| `requirements_requirement` | Nguồn chính — requirements của project |
| `app_requirement_function` | Link requirement → function |
| `app_requirement_use_case` | Link requirement → use case |
| `app_use_case` | Use cases với `completeness_status` |
| `app_function_screen` | Link function → screen |
| `app_function_api` | Link function → API endpoint |
| `app_registry_screen` | Screen objects |
| `app_registry_api_endpoint` | API endpoint objects |
| `traceability_link` | Links TESTED_BY (requirement → test case) |
| `quality_test_case` | Test case objects |
| `quality_test_case_result` | Kết quả chạy test case |

---

## Trạng thái implementation

| Feature | Phase | Status |
|---|---|---|
| `requires_use_case` DB column | Phase 0 | Done |
| `requiresUseCase` field trên Requirement domain + API | Phase 0 | Done |
| Coverage Summary endpoint | Phase 1 (P0) | Done |
| Traceability Matrix endpoint | Phase 1 (P0) | Done |
| Requirement Trace Detail endpoint | Phase 1 (P0) | Done |
| Gaps endpoint | Phase 1 (P0) | Done |
| Linkable pickers (function, use case) | Phase 2 (P1) | Done |
| Mention resolve endpoint (`requires-use-case`) | Phase 2 (P2) | Done |
| History endpoint (activity log per requirement) | Phase 2 (P2) | Done |
| Frontend Coverage tab UI | Phase 3 (P1) | Pending |
| Frontend Matrix tab UI | Phase 3 (P1) | Pending |
| Frontend Gaps tab UI | Phase 3 (P1) | Pending |
