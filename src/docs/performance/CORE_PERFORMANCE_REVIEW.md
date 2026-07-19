# Core Performance Review — Phase 23

> Equivalent path: `src/docs/performance/CORE_PERFORMANCE_REVIEW.md`
> Status: **NOT DONE** — deferred; Gate/target 7 not satisfied.

## Scope (HDN-011)

Heavy paths requiring baseline + optimization:

```text
Project dashboard aggregate
Convenience reports (task/schedule/capacity/finance/quote/…)
Report run + snapshot materialization
Gantt / schedule projections
Estimation / finance recalculation
Outbox processor throughput
```

## Current state

| Item | Classification | Notes |
|---|---|---|
| Profiling under load | DEFERRED_TO_POST_23_BACKLOG | No JMeter/k6/Gatling assets |
| Query explain / index review | NOT YET VERIFIED | Needs Postgres EXPLAIN on dashboard/report SQL |
| N+1 detection | NOT YET VERIFIED | Requires SQL logging / datasource-proxy run |
| Pagination on large lists | PARTIAL | PageResponse used in many modules; reporting list by project unbounded |
| Caching strategy | NOT IN SCOPE for this pass | — |

## Recommended next steps

1. Capture p95 latency for dashboard + finance recalculate with representative fixtures.
2. Add indexes called out by EXPLAIN for project-scoped FKs used in dashboard joins.
3. Bound export/list queries with pagination.
4. Add a minimal performance smoke test (fail if dashboard query count exceeds budget) once Testcontainers exists.

## Decision

Performance target **FAIL / deferred**. Documented as remaining Phase 23 blocker for GA (not for continuing feature work only if product accepts CONDITIONAL expansion risk — release gate itself remains NO-GO).
