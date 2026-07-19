# Phase 18 — Quote / Commercial Proposal TO-BE Complete

## 1. Summary

Phase 18 delivered `modules/quote`: Quote shell (DRAFT/ACTIVE/ARCHIVED), QuoteVersion lifecycle (DRAFT→SUBMITTED→APPROVED→SENT→ACCEPTED / REJECTED / ARCHIVED + `currentFlag`), QuoteLine, QuoteTerm, QuoteSummary, `QuoteCalculationService` + `TargetMarginSolver` (`QUOTE_V1`), project `currentQuoteId` / `currentQuoteVersionId`, IAM rights, event seeds (`@Order(24)`), Flyway `V55`, and unit tests. PDF export, contract conversion, tax calculation, CRM, notifications, baseline, and AI were **not** implemented.

## 2. Source Inputs Reviewed

- `PHASE_18_QUOTE_COMMERCIAL_PROPOSAL_TO_BE_DETAILED.md`
- Phase 17 project finance patterns (`modules/projectfinance`, `V54`, completion doc)
- Phase 16 estimation / Phase 15 rate card / Phase 09 project / Phase 10 authorization / Phase 04 outbox-audit / Phase 05 event registry
- Current BE: no prior quote module

## 3. Current vs TO-BE Classification Matrix

| Capability | Classification | Status |
|---|---|---|
| Quote shell from finance | MUST | Implemented |
| QuoteVersion + finance snapshot | MUST | Implemented |
| Phase line generation | MUST | Implemented |
| Target margin solver | MUST | Implemented |
| Discount + threshold approve | MUST | Implemented |
| Quote summary / recalculate | MUST | Implemented |
| Terms CRUD | MUST | Implemented |
| Lifecycle submit/approve/reject/send/accept/mark-current | MUST | Implemented |
| Quote IAM | MUST | Implemented |
| Event seeds | SEED_ONLY | Seeded |
| PDF / proposal export | DEFERRED | Deferred Phase 22/27 |
| Contract / invoice / tax | OUT OF SCOPE | Not claimed |
| Client CRM | DEFERRED | Snapshot fields only |

## 4. Implemented in Current BE

- Prior phases: Project, ProjectFinanceScenario + summary + phase finance, IAM, Event Registry, Outbox/Audit, Idempotency-Key filter

## 5. Implemented / Hardened in This Phase

- Flyway `V55__create_quote_tables_phase18.sql`
- Full `modules/quote` DDD module (quote / quoteversion / quoteline / quotesummary / quoteterm / calculation / shared)
- `Project.currentQuoteId` + `currentQuoteVersionId` + JPA/mapper/`withCurrentQuoteIds`
- IAM rights/permissions/authorities for `QUOTE_MANAGEMENT`
- Event seed `@Order(24)` (`SCOPERY_QUOTE` / owner `QUOTE`)
- Audit event types: created-from-finance, target-margin-solved, discount-changed, submit/approve/reject/send/accepted/marked-current/archived

## 6. Seed-only Items Added

- Event definitions for quote lifecycle / lines / terms / solver / margin warning
- No notification rules (deferred Phase 20/22)

## 7. Deferred Items and Target Phase

| Item | Target |
|---|---|
| PDF / proposal export | Phase 22 / 27 |
| Contract conversion | Phase 36 |
| Tax calculation | Tax module |
| Client CRM / ExternalParty | Post-23 |
| Notifications | Phase 20 |
| Baseline snapshot | Phase 19 |
| AI quote recommendation | Phase 21 |
| Formal workflow engine | Phase 34 |

## 8. Quote Boundary Decision

Phase 18 calculates **commercial proposal** amounts from frozen finance snapshots + quote lines. Quote is **not** a contract and **not** an invoice. Accepted marker does not create contract/billing. Tax excluded (`taxAmount` null). Recalculate never re-reads live finance.

## 9. Entity Mapping

| Domain | Table |
|---|---|
| Quote | `quote_quote` |
| QuoteVersion | `quote_version` |
| QuoteLine | `quote_line` |
| QuoteSummary | `quote_summary` |
| QuoteTerm | `quote_term` |
| Project pointers | `project_project.current_quote_id`, `current_quote_version_id` |

Children CASCADE delete from quote→version→line/summary/term. Partial unique: one `current_flag=true` per quote. Unique `(project_id, code)` on quote; unique `(quote_id, version_number)` on version; unique `quote_version_id` on summary.

## 10. API Changes

Base: `/api/projects/{projectId}/quotes` (`QuoteApiPaths`)

- Quote: `POST/GET/PUT`, `PATCH .../archive`
- Versions: `POST/GET/PUT`, `POST .../duplicate`, `PATCH .../archive`
- Lines: `POST/GET/PUT/DELETE`, `PUT .../lines/reorder`
- Terms: `POST/GET/PUT/DELETE`, `PUT .../terms/reorder`
- Summary: `GET .../summary`, `POST .../recalculate`, `POST .../solve-target-margin`
- Lifecycle: `submit`, `approve`, `reject`, `send`, `mark-accepted`, `mark-current`

Export APIs not implemented.

## 11. Create Quote Strategy

Create quote creates **shell only** (DRAFT). Requires source finance scenario APPROVED **or** `currentFlag=true`. Client snapshot fields stored on quote. Initial version via separate `POST .../versions`.

## 12. Create Version From Finance Strategy

Snapshots finance summary + phase finances into `finance_snapshot_json`. Generates PHASE lines when `generateLinesFrom=PHASE_FINANCE`. Pricing:

- `FROM_FINANCE_PLANNED_REVENUE` / `PHASE_LINE_SUM`: unit price = phase planned revenue
- `TARGET_MARGIN_SOLVER`: RequiredContractValue distributed by revenue share (fallback cost proportion)
- `MANUAL_TOTAL`: lines may be edited manually after create

`formulaVersion=QUOTE_V1`. Starts DRAFT, `currentFlag=false`. Does not mutate finance scenario.

## 13. Cost Base / Target Margin Strategy

- Default cost base = `budgetOfCosts` from frozen finance snapshot (`CostBaseMethod.BUDGET_OF_COSTS`)
- Also supports `DIRECT_COST` / `CUSTOM`
- **Target margin percent uses 0–100 scale** (same as finance). API `30.0` → 30% → ratio `0.30`
- `RequiredContractValue = CostBase / (1 - TargetMarginRatio)` with BigDecimal HALF_UP scale 4
- Example: 700,000,000 / 30% = 1,000,000,000

## 14. Discount Strategy

Methods: `NONE`, `FIXED_AMOUNT`, `PERCENT_OF_SUBTOTAL`. Reason required when discount > 0. Discount cannot make subtotal negative. **Threshold: discount percent > 10 requires `QUOTE_DISCOUNT_APPROVE`** (also enforced on approve). Discount changes audited.

## 15. Summary / Margin Formula Version

`QUOTE_V1`:

```text
Subtotal = Σ line.amount
Discount applied → SubtotalAfterDiscount
Tax excluded → TotalQuotedAmount = SubtotalAfterDiscount
QuoteGrossMargin = TotalQuotedAmount - DirectCost
QuotePBT = TotalQuotedAmount - DirectCost - Overhead
```

Percents null when total = 0. Margin fields null in API when caller lacks `QUOTE_MARGIN_VIEW`.

## 16. Currency Policy

Quote version currency copied from finance scenario. Line currency must match version. No FX conversion.

## 17. Version Status / Immutability Rules

Status: DRAFT / SUBMITTED / APPROVED / REJECTED / SENT / ACCEPTED / ARCHIVED + boolean `currentFlag`. Only DRAFT editable. SUBMITTED/APPROVED/SENT/ACCEPTED immutable except lifecycle. Rejected → new DRAFT via duplicate. One current version per quote (partial unique + clear prior + project pointers).

## 18. Lifecycle Rules

| Action | From | Requirements |
|---|---|---|
| Submit | DRAFT | ≥1 line, totalQuotedAmount > 0, discount reason if discount>0, validUntil not past |
| Approve | SUBMITTED | `QUOTE_APPROVE`; discount>10% needs `QUOTE_DISCOUNT_APPROVE` |
| Reject | SUBMITTED | reason required → REJECTED |
| Send | APPROVED | marker only |
| Mark accepted | SENT | marker only; no contract |
| Mark current | any non-archived | sets version/quote/project current pointers |

## 19. Authorization Matrix

| Authority | Use |
|---|---|
| QUOTE_VIEW | List/get quotes, versions, lines, terms, summary (**covers** line/summary/version view) |
| QUOTE_CREATE | Create quote shell |
| QUOTE_UPDATE | Update quote shell |
| QUOTE_ARCHIVE | Archive quote |
| QUOTE_MANAGE | Full manage |
| QUOTE_VERSION_* | Version create/update/archive/duplicate |
| QUOTE_LINE_* | Line create/update/delete |
| QUOTE_TERM_* | Term create/update/delete |
| QUOTE_SOLVER_USE | Solve target margin |
| QUOTE_RECALCULATE | Recalculate |
| QUOTE_SUBMIT / APPROVE / REJECT / SEND / MARK_ACCEPTED / MARK_CURRENT | Lifecycle |
| QUOTE_MARGIN_VIEW | Expose margin/PBT fields |
| QUOTE_DISCOUNT_UPDATE / DISCOUNT_APPROVE | Discount edit / threshold |

Enforced via `QuoteAuthorizationService` → `ProjectWorkspaceAuthorizationService`.

### IAM simplification note

Separate `QUOTE_VERSION_VIEW` / `QUOTE_LINE_VIEW` / `QUOTE_TERM_VIEW` / `QUOTE_SUMMARY_VIEW` rights from the TO-BE list were **collapsed into `QUOTE_VIEW`**. Distinct write/lifecycle authorities remain.

## 20. Event Registry Seeder Matrix

`SOURCE_SYSTEM=SCOPERY_QUOTE`, `OWNER=QUOTE`, `@Order(24)`:

QUOTE_CREATED/UPDATED/ARCHIVED, VERSION_CREATED/UPDATED/DUPLICATED/ARCHIVED/MARKED_CURRENT, LINE_CREATED/UPDATED/DELETED/LINES_REORDERED, TERM_CREATED/UPDATED/DELETED/TERMS_REORDERED, TARGET_MARGIN_SOLVED, RECALCULATED, DISCOUNT_UPDATED, MARGIN_THRESHOLD_WARNING, SUBMITTED/APPROVED/REJECTED/SENT/ACCEPTED.

## 21. Activity / Audit / Outbox Notes

Activity logger for create/update/archive/version/lifecycle/discount/solver/lines/terms. Outbox enqueue on quote/version events. Audit: version created from finance, target margin solved, discount changed, submit/approve/reject/send/accepted/marked-current/archived.

## 22. Idempotency Strategy

Platform `Idempotency-Key` filter for POSTs (existing; no new filter invented). Event seed find-or-create. Mark-current clears prior current for quote. Create always new UUID.

## 23. Tests Added

- `TargetMarginSolverTest` — 700M/30%=1B, zero margin, 100%/above rejected, scale 4
- `QuoteCalculationServiceTest` — margin/PBT, percent discount, negative discount rejected, 10% threshold, reason required
- `QuoteVersionLifecycleTest` — immutability + rejected→new draft
- `QuoteEventDefinitionSeedInitializerTest` — seed + no duplicate

## 24. Commands Run

```text
./mvnw -q -DskipTests compile
./mvnw -q -Dtest=TargetMarginSolverTest,QuoteCalculationServiceTest,QuoteEventDefinitionSeedInitializerTest,QuoteVersionLifecycleTest test
```

## 25. Test Results

- `./mvnw -q -DskipTests compile` — SUCCESS
- Targeted quote tests — SUCCESS (exit 0)

## 26. Manual Verification

Not yet verified — requires running the application against PostgreSQL + Flyway V55 and calling quote APIs with JWT.

## 27. Assumptions

- Create quote = shell only; version is a separate POST
- Target margin percent is 0–100 scale (not 0–1)
- Cost base default = budgetOfCosts from snapshot
- Discount approval threshold = 10%
- QUOTE_VIEW covers all read surfaces for quote children
- Send/accept are state markers only

## 28. Deviations From Prompt

- Module package `com.company.scopery.modules.quote` (not nested under `project/quote`)
- IAM view authorities simplified to `QUOTE_VIEW` (documented above)
- `QUOTE_VERSION_CREATE` etc. use distinct action codes (`VERSION_CREATE`, `LINE_CREATE`, …) so permission catalog seeding stays unique per action
- Discount method/amount stored on both version and summary for edit + calculated display
- Full suite `mvn test` not re-run end-to-end in this pass (targeted + compile only)

## 29. Known Risks

- Synchronous recalculate in request transaction for large line sets
- Concurrent mark-current last-write-wins without pessimistic lock (DB partial unique helps)
- Finance snapshot JSON schema is application-owned; corrupt JSON fails recalculate/solver

## 30. Future Phases That Must Return to Quote

- Phase 19 Baseline / change request
- Phase 20/22 Notifications and reporting export
- Phase 21 AI commercial suggestions
- Phase 27 Document Hub proposal package
- Phase 36 Contract / billing / revenue recognition
