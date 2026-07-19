# Phase 24 — Scope / Deliverable / Acceptance TO-BE Complete

## 1. Summary

Phase 24 delivered `modules/scope`: scope packages (create/list/get/approve), scope items (create/list/get/update/archive), deliverables (create/list/get/accept/reopen), acceptance criteria (create/list/get/satisfy/waive), event seeder, and IAM. Evidence/review workflow APIs, quote→scope import, WBS/task mapping APIs, and full scope reports remain partially deferred.

## 2. Source Inputs Reviewed

- `PHASE_24_SCOPE_DELIVERABLE_ACCEPTANCE_TO_BE_DETAILED.md`
- Phase 18 quote, Phase 19 baseline/CR, Phase 09 WBS/Task

## 3. Current vs TO-BE Classification Matrix

| Capability | Classification | Status |
|---|---|---|
| Scope package create/list/get/approve | MUST | Implemented |
| Scope package mark-current / archive / import-from-quote | PARTIAL / DEFERRED | Approve sets current; dedicated mark/archive/import deferred |
| Scope item CRUD + archive | MUST | Implemented |
| Scope item ↔ WBS mappings | DEFERRED | Table exists (`V60`); HTTP not yet |
| Deliverable create/list/get/accept/reopen | MUST | Implemented |
| Deliverable update/status/archive + task mappings | DEFERRED | Persistence ready; HTTP partial |
| Acceptance criteria create/list/satisfy/waive | MUST | Implemented |
| Evidence + review workflow | DEFERRED | Tables in `V60`; APIs not yet |
| Scope reports | DEFERRED | Convenience reporting later |
| Events + IAM | MUST | `@Order(30)` + `SCOPE_MANAGEMENT` / `DELIVERABLE_MANAGEMENT` |

## 4. Implemented in Current BE

- Flyway `V60__create_scope_deliverable_tables_phase24.sql`
- Controllers: packages, items, deliverables, acceptance criteria
- Accept path enforces mandatory criteria via domain `isMet()`
- Unit tests: acceptance criteria lifecycle, in/out-of-scope flag guard

## 5. Deferred Items

| Item | Target |
|---|---|
| Evidence / review approve-reject-rework | Immediate backlog under Phase 24 follow-up |
| WBS + task mapping HTTP | Same |
| Quote import | Quote↔scope integration backlog |
| Scope coverage reports | Phase 22 report engines |

## 6. Release Decision

**Phase 24 core MUST path: MOSTLY COMPLETE** for package/item/deliverable/criteria accept-reopen loop. Evidence/review and mappings are explicit gaps.

## 7. Follow-ups

- Add evidence + review actions/controllers on existing `V60` tables
- Add WBS/task mapping CRUD
- Import-from-quote action
