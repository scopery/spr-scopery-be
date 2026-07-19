# Phase 25 — RAID / Decision Management TO-BE Complete

## 1. Summary

Phase 25 delivered `modules/raid`: RAID items (CRUD/status/resolve/close/reopen/escalate/archive), risk→issue conversion, CR draft from RAID, RAID actions (create/list/complete/update/cancel/create-linked-task), RAID links, decisions (create/update/supersede/decide/reject/archive), decision options (create/update/delete) + impact (with finance masking), decision links, RAID/decision report endpoints, event seeder, and IAM.

## 2. Source Inputs Reviewed

- `PHASE_25_RAID_DECISION_MANAGEMENT_TO_BE_DETAILED.md`
- Phase 19 change request, Phase 22 reports, Phase 23 hardening gates

## 3. Current vs TO-BE Classification Matrix

| Capability | Classification | Status |
|---|---|---|
| RAID item APIs | MUST | Implemented |
| Risk convert + CR draft | MUST | Implemented |
| RAID actions create/list/complete/update/cancel | MUST | Implemented |
| RAID action create-linked-task | MUST | Implemented |
| RAID links | MUST | Implemented (`raid_link` + HTTP) |
| Decision CRUD lifecycle | MUST | Implemented (update + supersede) |
| Decision options + impact | MUST | Implemented (option update/delete added) |
| Decision links | MUST | Implemented (`V90` + HTTP) |
| Reports (summary/registers/logs) | MUST | Implemented |
| Risk score formula v1 | MUST | Implemented + unit tested |
| Events + IAM | MUST | `@Order(31)` + `RAID_MANAGEMENT` / `DECISION_MANAGEMENT` |

## 4. Implemented in Current BE

- Flyway `V61__create_raid_decision_tables_phase25.sql`, `V69__rename_raid_tables_to_raid_prefix.sql`, `V90__create_raid_decision_link_table.sql`
- Controllers: raid-items (incl. links), raid-actions, decisions (incl. links), reports
- Sub-modules: `raidlink`, `decisionlink`
- `RiskScoreCalculator` (probability × impact weights)
- Finance fields on decision impact masked without `PROJECT_FINANCE_VIEW`
- Unit tests: risk score, convert risk→issue, domain score attachment, link validation, supersede domain, linked-task domain

## 5. Deferred Items

| Item | Target |
|---|---|
| AI tool registry hooks | Phase 21 optional |

## 6. Release Decision

**Phase 25 core MUST path: COMPLETE** for RAID register + decision log operational loop including links and action→task bridge.

## 7. Follow-ups

- Notification rules for escalation (Phase 20 bridge extension)
