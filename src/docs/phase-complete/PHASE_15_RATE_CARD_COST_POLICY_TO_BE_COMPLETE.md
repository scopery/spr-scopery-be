# Phase 15 — Rate Card / Cost Policy TO-BE Complete

## 1. Summary

Phase 15 delivered the `modules/ratecard` foundation: CostRole catalog, workspace member cost-role assignment, RateCard shell with versioned lines, inflation policy, deterministic rate resolution + task labor cost preview, IAM rights, event seeds, Flyway `V52`, and unit tests. No project budget, margin, quote, billing, or salary/payroll features were implemented.

## 2. Source Inputs Reviewed

- `PHASE_15_RATE_CARD_COST_POLICY_TO_BE_DETAILED.md`
- `modules/resourcecapacity` (shared kernel, Action+QueryService, IAM, publisher, seeds)
- Phase 09/12/13/14 complete docs and existing Project/Task/Workspace/IAM modules
- `AuditEventType`, outbox, Event Registry seed support

## 3. Current vs TO-BE Classification Matrix

| Capability | Classification | Status |
|---|---|---|
| CostRole catalog | MUST | Implemented |
| Member cost role mapping | MUST | Implemented |
| RateCard SYSTEM/ORG/WORKSPACE | MUST | Implemented |
| RateCard CLIENT/PROJECT | DEFERRED | Deferred |
| RateCardVersion + publish immutability | MUST | Implemented |
| RateCardLine CCH + optional billing | MUST | Implemented |
| Currency validation enum | MUST | Implemented |
| Currency exchange rates | DEFERRED | Deferred |
| Inflation annual compound | MUST | Implemented |
| RateResolutionService + snapshot | MUST | Implemented |
| Project default rate card assignment | DEFERRED | Deferred (workspace `isDefault` only) |
| Approval workflow | DEFERRED | Deferred |
| Budget / margin / quote | OUT OF SCOPE | Not claimed |

## 4. Implemented in Current BE

- Prior phases: Workspace, IAM, Project/Task (`estimateHours`, `plannedRoleCode`), Capacity, Event Registry, Outbox/Audit.

## 5. Implemented / Hardened in This Phase

- Flyway `V52__create_ratecard_tables_phase15.sql`
- Full `modules/ratecard` DDD module (shared + costrole, membercostrole, ratecard, ratecardversion, ratecardline, inflationpolicy, resolution)
- IAM rights/permissions/authorities for RATE_CARD
- Event seed `@Order(19)`, CostRole seed, Inflation 0% seed
- Audit event types for publish / rate / inflation / member assignment / archive

## 6. Seed-only Items Added

- System built-in CostRoles (§20.1) via `CostRoleSeedInitializer`
- `DEFAULT_ANNUAL_INFLATION_0_PERCENT` via `InflationPolicySeedInitializer`
- Event definitions (`SCOPERY_RATE_CARD` / owner `RATE_CARD`)
- No commercial rate card line seeds (no fake production rates)

## 7. Deferred Items and Target Phase

| Item | Target |
|---|---|
| ProjectRateCardAssignment | Phase 17 Finance |
| CLIENT / PROJECT rate card scopes | Phase 17/18 |
| Currency exchange / FX snapshot | Phase 17/18 |
| Approval workflow for publish | Later governance |
| Budget / margin / quote / billing | Phase 17/18 |
| Persisted estimation roll-ups | Phase 16 |
| Notification rules for rate changes | Phase 20/22 |

## 8. CostRole Decision

Independent planning roles (not IAM roles, not salary). Scope SYSTEM/ORGANIZATION/WORKSPACE. Code UPPERCASE. Unique within scope+org+workspace. Built-in soft-archive only (no hard delete API).

## 9. Member Cost Role Mapping Decision

Implemented `rate_workspace_member_cost_role` with ACTIVE member + ACTIVE role + date range + no overlapping default assignment.

## 10. RateCard Entity Mapping

Table `rate_card`: code, name, scope, org/workspace, `default_currency_code`, `is_default`, status DRAFT/ACTIVE/INACTIVE/ARCHIVED, `current_version_id`, auditable + version. CLIENT/PROJECT columns reserved nullable for future.

## 11. RateCardVersion Entity Mapping

Table `rate_card_version`: unique `(rate_card_id, version_number)`, DRAFT/PUBLISHED/ARCHIVED, effective range, publish metadata. PUBLISHED immutable; publish validates lines/roles/rates/currency/overlap.

## 12. RateCardLine Entity Mapping

Table `rate_card_line`: cost/billing rates as DECIMAL(18,4), currency, optional seniority/location. No salary fields. Mutations only on DRAFT versions.

## 13. InflationPolicy Decision

Annual compound foundation: `Adjusted = Base × (1 + percent/100) ^ YearsForward` with BigDecimal HALF_UP scale 4. MONTHLY accepted in enum but treated like annual compound in Phase 15 calculation path; NONE skips adjustment.

## 14. Currency Decision

`SupportedCurrency` enum: VND, USD, EUR, SGD, JPY, KRW, CNY. Validation only; no FX table.

## 15. Rate Resolution Strategy

Precedence WORKSPACE > ORGANIZATION > SYSTEM. Prefer `isDefault` ACTIVE card, else first ACTIVE with published version covering `targetDate`. Match line by costRoleId + optional currency.

**YearsForward** = `ChronoUnit.DAYS.between(version.effectiveFrom, targetDate) / 365.25` (decimal years). Documented Phase 15 choice.

## 16. Rate Snapshot Contract

`RateSnapshot` / `RateSnapshotResponse` fields per §7.9. Preview API returns estimateHours × adjustedCostRate labeled **estimated labor cost preview** (not official finance).

## 17. API Changes

Base `/api/rate-card`:

- `/cost-roles`, `/member-cost-roles`, `/cards`, `/cards/{id}/versions`, `/versions/{id}/lines`, `/inflation-policies`
- `POST /resolve`, `POST /preview-task-rate`
- Cost role / card creates return **201**

## 18. Authorization Matrix

Rights under module `RATE_CARD`: COST_ROLE_*, RATE_CARD_*, RATE_CARD_LINE_*, INFLATION_POLICY_*, RATE_RESOLUTION_*, MEMBER_COST_ROLE_*. Workspace access via `WorkspaceIamIntegrationService`; SYSTEM/ORG without workspaceId requires authenticated user (catalog ops).

## 19. Event Registry Seeder Matrix

All §16 events seeded with `SOURCE_SYSTEM=SCOPERY_RATE_CARD`, `OWNER=RATE_CARD`, `@Order(19)`.

## 20. Activity / Audit / Outbox Notes

- Activity via `RateCardActivityLogger`
- Outbox via `RateCardPlatformPublisher`
- Sensitive audits: version publish, member assignment, rate card/cost role archive (new `AuditEventType` values)

## 21. Idempotency Strategy

HTTP idempotency filter applies to POST paths under `/api` when clients send idempotency keys (platform existing behavior). Seeds are idempotent existence checks.

## 22. Tests Added

- `CostRoleBusinessRulesActionTest`
- `RateCardVersionPublishActionTest`
- `RateCardLineBusinessRulesActionTest`
- `RateResolutionServiceTest` (incl. inflation exact 5%×2y = 551250)
- `CostRoleSeedInitializerTest`
- `RateCardEventDefinitionSeedInitializerTest`

## 23. Commands Run

```bash
mvn -q -DskipTests compile
mvn -q test -Dtest='com.company.scopery.modules.ratecard.**'
mvn -q test
```

## 24. Test Results

- `mvn -q -DskipTests compile` — success
- `mvn -q test -Dtest='com.company.scopery.modules.ratecard.**'` — **18 tests, all green**
- `mvn -q test` (full suite) — **success** (exit 0); IAM right catalog now seeds 162 rights including RATE_CARD

## 25. Manual Verification

Not yet verified — requires running the application against PostgreSQL / Swagger smoke for CRUD + resolve.

## 26. Assumptions

- Workspace default rate card uses `is_default` + partial unique index for ACTIVE WORKSPACE cards
- SYSTEM-scoped ops without workspaceId only require authentication (rights granted via workspace owner catalogs for workspace-scoped usage)
- `findByCode` for resolution picks first matching CostRole by code (SYSTEM seeds unique)
- Inflation uses pure BigDecimal: whole-year repeated multiply + linear fractional accrual (no float)

## 27. Deviations From Prompt

- Table names use `rate_*` prefix per TO-BE entity section (not `ratecard_*`)
- Module code / owner `RATE_CARD` as specified
- MONTHLY compound frequency stored but not separately formula-modeled beyond annual path
- Idempotency not custom-wired beyond platform filter

## 28. Known Risks

- Fractional-year inflation uses linear accrual of the annual increment (not continuous compound); refine if finance requires true fractional exponent
- CostRole `findByCode` is global — ambiguous if org/workspace duplicate codes exist
- SYSTEM catalog mutation auth is lighter than capacity workspace checks when `workspaceId` is null

## 29. Future Phases That Must Return to Rate Card

- **Phase 16** Estimation roll-up consuming `RateResolutionService` / snapshots
- **Phase 17** Budget/margin, project rate assignment, FX
- **Phase 18** Quote / billing rate commercial use
- Later AI/reporting must not expose salary and must respect rate permissions
