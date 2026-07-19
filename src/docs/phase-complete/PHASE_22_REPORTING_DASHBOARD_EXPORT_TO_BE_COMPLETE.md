# Phase 22 — Reporting / Dashboard / Export TO-BE Complete

## 1. Summary

Phase 22 delivered `modules/reporting`: seeded report definitions, report runs + immutable snapshots, CSV export (flattened key/value), project dashboard/health/KPIs, and all 10 project convenience report GET endpoints as **read-only** queries.

## 2. Source Inputs Reviewed

- `PHASE_22_REPORTING_DASHBOARD_EXPORT_TO_BE_DETAILED.md`
- Phase 09–19 project/finance/quote/baseline/schedule/estimation/capacity data surfaces
- CLAUDE.md Action + QueryService + Request/Command/Response conventions

## 3. Current vs TO-BE Classification Matrix

| Capability | Classification | Status |
|---|---|---|
| Report definition catalog | MUST | Seeded + query APIs |
| Report run + snapshot | MUST | Implemented with QueryServices |
| Export job create (CSV) | MUST | Flattened CSV serializer |
| Project dashboard / health | MUST | Implemented (`healthFormulaVersion=v1`) |
| Convenience project reports (10) | MUST | All 10 GET endpoints wired |
| Finance/quote field masking | MUST | Authz-gated + dedicated error codes |
| Activity feed | DEFERRED | Remains deferred from Phase 20 |
| PDF export | OUT OF SCOPE | Not claimed |
| XLSX | DEFERRED | Rejected with `REPORT_EXPORT_FORMAT_NOT_SUPPORTED` |

## 4. Implemented in Current BE

- Flyway `V59__create_reporting_tables_phase22.sql`
- Controllers: definition, run, dashboard, convenience reports
- `ReportRunQueryService`, `ReportSnapshotQueryService` (no controller → repository)
- Typed `CreateReportRunRequest` / `CreateReportExportRequest` + Commands + Responses
- `CsvReportSerializer` flattens nested JSON to CSV rows
- Seeders `@Order(29)` definitions + events
- Unit tests under `src/test/java/.../reporting/`

## 5. Convenience report endpoints (§7.4)

| Endpoint | Status |
|---|---|
| GET `/reports/task-risk` | Implemented (read) |
| GET `/reports/schedule-risk` | Implemented (read) |
| GET `/reports/capacity` | Implemented (read; leave details masked) |
| GET `/reports/estimation` | Implemented (read) |
| GET `/reports/finance` | Implemented (read; finance permission required) |
| GET `/reports/quote` | Implemented (read; quote permission required) |
| GET `/reports/baseline-vs-current` | Implemented (read; finance/quote deltas masked) |
| GET `/reports/change-impact` | Implemented (read; finance impacts masked) |
| GET `/reports/notifications` | Implemented (read) |
| GET `/reports/ai-planning` | Implemented (read; no write side-effect) |

## 6. Known deviations

| Item | Note |
|---|---|
| Table prefix `report_*` vs `reporting_*` | Locked in V59; documented deviation from `{module}_` pattern |
| Notification unread/failed delivery counts | Foundation zeros until project-scoped notification queries exist |
| Capacity scheduled/gap hours | Allocation rows present; hour rollup left at zero without daily-work join depth |

## 7. Deferred Items

| Item | Target |
|---|---|
| XLSX / PDF | Export backlog / Phase 27 Document Hub |
| Cross-workspace executive rollups | Later analytics |
| Snapshot retention job + storage offload | Follow-up |

## 8. Release Decision

**Phase 22 core MUST path: COMPLETE** after remediation of controller layering, convenience endpoint coverage, CSV export, Commands/DTOs, error catalog, and unit tests.
