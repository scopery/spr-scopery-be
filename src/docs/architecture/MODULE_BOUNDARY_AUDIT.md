# Module Boundary Audit — Phase 23

> Equivalent path: `src/docs/architecture/MODULE_BOUNDARY_AUDIT.md`
> Status: **PASS (sampled)** — reporting Map controller returns removed; ArchUnit still deferred

## Rules (CLAUDE.md / Part I)

```text
http → application → domain
infrastructure → domain
common/platform must not depend on modules
domain must not import Spring Web / JPA
controllers return ApiResponse DTOs, never JPA/domain
Action + QueryService (no monolithic ApplicationService)
```

## Sampled results

| Check | Result | Notes |
|---|---|---|
| Domain free of Spring Web/JPA (sampled) | PASS (sampled) | Domain records/enums |
| Controllers return ApiResponse DTOs | **PASS** | Reporting dashboard/convenience/definition use typed records |
| Reporting run/export controllers | PASS | Actions + QueryServices |
| ArchUnit automated rules | MISSING | Deferred backlog |
| `common`/`platform` → `modules` | NOT YET VERIFIED exhaustively | Needs ArchUnit |

## Residual

1. Internal Map building remains inside `ProjectDashboardQueryService` (converted at boundary) — acceptable for Gate 6.
2. Add ArchUnit before hard long-term maintainability gate.

## Decision

**Gate 4–5:** PASS sampled. Gate 6 API typing addressed.
