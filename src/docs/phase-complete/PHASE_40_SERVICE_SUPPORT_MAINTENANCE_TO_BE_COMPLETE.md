# Phase 40 — Service / Support / Maintenance Complete

## 1. Summary

Phase 40 delivered `modules/servicesupport`: SupportCase lifecycle (create/triage/resolve/close), portal visibility filtering, SLA policy/clock/breach persistence + tracking helpers, maintenance plans, dashboard. Migration: **V85**. SLA is **operational tracking only**, not legal enforcement. Support effort is not payroll. AI service-desk tools **seeded** in AiTool registry (V89); execute = stub/NO_OP.

## 2. Already Done (prior pass)

- V85 tables
- SupportCase domain + JPA + SupportController core
- SlaClockService breach/at-risk
- SupportComment portal visibility rules
- Domain tests for case/SLA/comment

## 3. Newly Added / Completed in This Pass

- SLA policy/clock/breach repositories + HTTP (create/list policies, list clocks/breaches)
- Maintenance plan create/list HTTP + persistence
- Triage starts resolve clock when policy present; resolve completes clocks / records breaches
- Domain models: ServiceProfile, MaintenanceWindow (helpers; plan is the persisted surface)

## 3.1 Gap closure (this pass)

- `SlaBreachEvaluationJob` + `SlaBreachEvaluationService` (open clock scan)
- ServiceProfile, SupportQueue, SupportAssignment, SupportComment HTTP + JPA
- Warranty create/list/**expire** HTTP; Effort record/list/**cancel** HTTP
- Effort → profitability: rebuild adds project support effort hours × default hourly cost (`75.00`) for non-cancelled records (not payroll; pragmatic cost contribution)
- Portal intake: `PortalSupportCaseController`
- Tests: `SlaBreachEvaluationServiceTest`, `SupportAssignmentDomainTest`, `SupportEffortCostContributionTest`, warranty/effort domain tests

## 4. Deferred / Partial

| Item | Notes |
|---|---|
| Portal intake wired into `clientportal` module | **Verified** — `PortalSupportCaseController` creates/lists portal-visible cases |
| Queue / warranty / effort → profitability | Queue/assignment/comment/warranty expire/effort cancel HTTP done; effort cost on profitability rebuild uses **default hourly cost** (not rate-card payroll wiring) |
| Scheduled SLA breach job in ScheduledJobRegistry | **Implemented** — `SlaBreachEvaluationJob` |
| AI service desk tools | **SEEDED** in AiTool registry (V89); live handlers deferred |

## 5. Product Boundaries

SLA ≠ contractual enforcement. Portal hides non-visible cases. Effort records ≠ payroll. Warranty expire is operational status only.

## 6. Commands / Tests

Focused suite includes servicesupport + profitability wiring tests — green.

## 7. Release Decision

**Phase 40 MUST core COMPLETE** for support cases + SLA measurement + maintenance plans + pragmatic effort→profitability contribution at documented depth.
