# IDOR / Path Ownership Audit — Phase 23

> Equivalent path: `src/docs/security/IDOR_PATH_OWNERSHIP_AUDIT.md`
> Status: **PASS (suite implemented)** — Gate 3 remediating evidence below.

## Spec requirement (HDN-003 / Gate 3)

Project/workspace-scoped resources must reject cross-tenant or cross-project access (wrong `projectId` / workspace membership).

## Controls

| Control | Status | Evidence |
|---|---|---|
| Auth required on `/api/**` business routes | PASS | Security path + ProtectedEndpointSecurityIT |
| Project permission checks in actions | PASS | `ProjectWorkspaceAuthorizationService` + module wrappers |
| Path mismatch (`findById` vs path `projectId`) | PASS | `ProjectAuthorizationActionTest` (phase/task/…) |
| Cross-workspace project GET | PASS | `projectGet_crossWorkspace_forbidden` + `ProjectIdorSecurityIT` |
| Report run/snapshot ownership | PASS | `ReportRunQueryService` / `ReportSnapshotQueryService` call `requireReportView` |
| Export download ownership | PASS | `ReportExportQueryService.findAuthorized` + IDOR tests |

## Test suite (implemented)

| Test | Asserts |
|---|---|
| `ReportingIdorAccessTest` | Foreign project run/export/snapshot → `REPORT_ACCESS_DENIED` |
| `ProjectIdorSecurityIT` | Cross-workspace project GET → 403 `IAM_ACCESS_DENIED`; export download → 403 `REPORT_ACCESS_DENIED` |
| `ProjectAuthorizationActionTest` | Cross-workspace + path mismatch (existing) |

## High-risk surfaces (covered pattern)

```text
/api/projects/{id}
/api/reports/runs/{id}          (+ requireReportView)
/api/reports/exports/{id}/download
/api/projects/{projectId}/…     (module findByIdAndProjectId / path mismatch)
```

## Residual risk

- Not every module has a dedicated HTTP IDOR IT; coverage is pattern-based (authz service + representative HTTP slices + reporting ownership).
- Prefer continuing to add module-specific path-mismatch tests when new nested resources ship.

## Decision

**Gate 3: PASS** with the suite above. Classification: `CURRENTLY_IMPLEMENTED` for core project/reporting ownership checks.
