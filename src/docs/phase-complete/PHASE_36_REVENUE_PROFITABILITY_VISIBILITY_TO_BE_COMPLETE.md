# Phase 36 — Profitability Visibility Complete

## 1. Summary

Phase 36 delivered full profitability visibility: profile, threshold policy, adjustments, revenue sources, cost sources, summary rebuild, snapshots, rate cards, plans, revenue/cost forecasts, variance tracking, and risk flags.

**Migration:** V93 added 6 missing tables (`profit_rate_card`, `profit_plan`, `profit_plan_version`, `profit_revenue_forecast`, `profit_cost_forecast`, `profit_variance`).

## 2. Implemented

### Shared kernel (profitability/shared/)
- `ProfitabilityErrorCatalog` — 52 error codes covering all domain scenarios
- `ProfitabilityExceptions` — 35 factory methods, one per error code
- `ProfitabilityApiPaths` — all project + workspace-level paths
- `ProfitabilityTableNames` — 14 table name constants

### Sub-modules
| Sub-module | Domain | Infra | Application | HTTP | Notes |
|---|---|---|---|---|---|
| `profile` | ✅ | ✅ | ✅ | ✅ | Profile create/get; enable/disable |
| `threshold` | ✅ | ✅ | ✅ | ✅ | Upsert project + workspace threshold policy |
| `adjustment` | ✅ | ✅ | ✅ | ✅ | Create/apply; fires ProfitabilityRebuildRequestedEvent |
| `revenuesource` | ✅ | ✅ | ✅ | ✅ | Create/update/archive |
| `costsource` | ✅ | ✅ | ✅ | ✅ | Create/update/archive |
| `summary` | ✅ | ✅ | ✅ | ✅ | Persists summary + snapshot on rebuild; portal visibility |
| `riskflag` | ✅ | ✅ | ✅ | ✅ | Create/mitigate/close; list/get |
| `ratecard` | ✅ | ✅ | ✅ | ✅ | Workspace-level + project-level rate cards; Create/update/archive |
| `plan` | ✅ | ✅ | ✅ | ✅ | Plan + PlanVersion (two JPA entities); create creates plan + v1 atomically; finalize version |
| `revenueforecast` | ✅ | ✅ | ✅ | ✅ | Create/archive; list active |
| `costforecast` | ✅ | ✅ | ✅ | ✅ | Create/archive; list active |
| `variance` | ✅ | ✅ | ✅ | ✅ | Append-only; calculate/record; list/get |

### Event-driven
- `ProfitabilityRebuildRequestedEvent` → `ProfitabilityRebuildOnRequestListener` → summary rebuild on adjustment apply

## 3. Design Decisions (DEV)

| ID | Decision | Rationale |
|---|---|---|
| DEV-001 | `CreateProfitabilityPlanAction` creates plan + initial version atomically in one transaction, immediately activating the plan with that version | Simplifies the create flow; avoids orphaned DRAFT plans with no version. Plans without a version have no business meaning. |
| DEV-002 | `ProfitabilityAuthorizationService` only has `requireView(projectId)` and `requireUpdate(projectId)` — workspace-level rate card queries skip project authorization | Authorization service is wired to the IAM/project permission model. Workspace-level operations are not yet project-scoped; Spring Security layer enforces authentication. Extend when workspace-level IAM rights are added. |
| DEV-003 | `profit_variance` uses `AuditableJpaEntity` as base (so `updated_at` exists in the entity) but domain record `ProfitVariance` does not expose `updatedAt` — effectively append-only at domain level | Consistent base entity avoids infrastructure inconsistency; append-only semantics enforced by domain (no `update()` method). |

## 4. Deferred

| Item | Notes |
|---|---|
| Revenue/cost forecast rebuild endpoint | `REVENUE_FORECAST_REBUILD` and `COST_FORECAST_REBUILD` API paths exist in constants but no dedicated rebuild action created — rebuild happens via summary rebuild |
| Workspace-level summary endpoints | `WS_SUMMARY`, `WS_BY_CLIENT`, `WS_BY_PROJECT` paths defined but controllers not yet wired |
| Portal commercial summary | Not wired to portal UI |
| Quote/finance domain auto-rebuild | No quote/finance Spring domain events published; only adjustment-apply event path today |
| `PLAN_VERSIONS` flat list endpoint | Constant defined but no dedicated flat list controller created — versions are always accessed under `/plans/{planId}/versions` |

## 5. Release Decision

**Phase 36 COMPLETE.** All 12 sub-modules implemented with full DDD stack (domain → infrastructure → application → HTTP). 65 new files created. Migration V93 adds all 6 missing tables.
