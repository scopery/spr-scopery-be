# Phase 12 — Resource Calendar / Capacity TO-BE Complete

## 1. Summary

Phase 12 delivers the resource capacity foundation: workspace working calendars with day rules and exceptions, user capacity profiles (focus factor), project resource allocations with **block when overlapping ACTIVE allocation % > 100**, and on-demand capacity calculation (working / focused / allocated / available). Scheduling, Gantt, cost, timesheet actuals, team capacity reports, capacity snapshots, leave/PTO, and AI recommendations are **explicitly not implemented**.

## 2. Source Inputs Reviewed

- `CLAUDE.md` / project coding conventions (Action + QueryService, DDD layout, error catalog)
- `PHASE_12_RESOURCE_CALENDAR_CAPACITY_TO_BE_DETAILED.md`
- Phase 09–11 completion docs + `modules/project/**` patterns
- Phase 03 workspace member ACTIVE checks
- Phase 02 IAM catalog / `IamAuthorities`
- Phase 04 outbox/audit (`TransactionalOutboxService`, `ImmutableAuditEventService`)
- Phase 05 event registry seed pattern (`EventDefinitionSeedSupport`)

## 3. Current vs TO-BE Classification Matrix

| Capability | Pre-Phase 12 | Phase 12 result |
|---|---|---|
| CAP-001 Working calendar | Missing | MUST → Implemented |
| CAP-002 Day rules | Missing | MUST → Implemented |
| CAP-003 Calendar exceptions | Missing | MUST → Implemented |
| CAP-004 User capacity profile | Missing | MUST → Implemented |
| CAP-005 Project resource allocation | Missing | MUST → Implemented (block > 100%) |
| CAP-006 Capacity calculation | Missing | MUST → Implemented |
| CAP-007 Capacity overview APIs | Missing | MUST → Implemented |
| CAP-008 Capacity snapshot | Missing | **DEFERRED_TO_PHASE_22** |
| CAP-009 Team capacity | Missing | **DEFERRED_TO_PHASE_22** |
| CAP-010 Time off / leave | Missing | **DEFERRED_TO_PHASE_37** |
| CAP-011 Skill / role capacity | Missing | **DEFERRED_TO_PHASE_15/16** |
| CAP-012 AI recommendation / leveling | Missing | **DEFERRED_TO_PHASE_13/21** |
| Task scheduling / estimatedFinishDate | Missing | Not in scope |
| Gantt / finance / timesheet | Missing | Not in scope |
| HTTP Idempotency-Key | Missing | Documented gap (same as Phase 11) |

## 4. Implemented in Current BE (pre-Phase 12)

- Workspace / WorkspaceMember / Project Core (Phases 03, 09–11)
- IAM workspace-scoped authorities + catalog seeders
- Outbox / immutable audit / activity log foundation
- Event registry seed support
- No calendar/capacity/allocation tables or APIs

## 5. Implemented / Hardened in This Phase

- Flyway `V49__create_capacity_tables_phase12.sql`
- Module `modules/resourcecapacity` with shared kernel + sub-packages:
  - `workingcalendar`, `dayrule`, `calendarexception`, `usercapacityprofile`, `projectallocation`, `calculation`
- Default calendar seeder on `WorkspaceCreatedEvent` (`DEFAULT_BUSINESS_CALENDAR`, timezone `Asia/Ho_Chi_Minh`, Mon–Fri 8h)
- Lazy defaults when no user profile exists (focus 0.75, daily 8h, workspace default calendar)
- IAM permissions/rights/authorities for capacity + allocation
- Event seed under `SCOPERY_CAPACITY` (20 event codes)
- Audit types for default change, day rules, exceptions, focus factor, allocation, over-allocation blocked

## 6. Seed-only Items Added

- Capacity event definitions (`SCOPERY_CAPACITY` / owner `RESOURCE_CAPACITY`)
- IAM rights + permission catalog entries (calendar, profile, allocation, capacity view/calculate)
- Default business calendar on workspace create (idempotent)
- No user-facing notification templates (deferred Phase 20/22)
- No auto-create of historical member capacity profiles (lazy resolution instead)

## 7. Deferred Items and Target Phase

| Item | Target |
|---|---|
| CapacitySnapshot (CAP-008) | Phase 22 Reporting |
| Team capacity aggregation (CAP-009) | Phase 22 |
| User time-off / leave (CAP-010) | Phase 37 |
| Skill / role capacity (CAP-011) | Phase 15/16 |
| AI recommendation / resource leveling (CAP-012) | Phase 13 / 21 |
| Task scheduling / estimatedFinishDate | Phase 13 |
| Gantt | Phase 14 |
| Rate/cost / utilization actuals | Phase 15/17 / 37 |
| Over-allocation user notifications | Phase 20 |
| HTTP Idempotency-Key on capacity POSTs | Later (Phase 04 pattern available) |

## 8. Entity Mapping

| Entity | Table | Notes |
|---|---|---|
| WorkingCalendar | `capacity_working_calendar` | Unique `(workspace_id, code)`; partial unique one default ACTIVE |
| CalendarDayRule | `capacity_calendar_day_rule` | Unique `(working_calendar_id, day_of_week)`; hours 0–24 |
| CalendarException | `capacity_calendar_exception` | Unique `(working_calendar_id, exception_date)` |
| UserCapacityProfile | `capacity_user_profile` | Soft FKs; hours/focus CHECK constraints |
| ProjectResourceAllocation | `capacity_project_resource_allocation` | Percent CHECK `> 0 AND <= 100` |

## 9. API Changes

| Surface | Path |
|---|---|
| Calendars | `/api/capacity/calendars` (+ activate/deactivate/archive/set-default) |
| Day rules | `/api/capacity/calendars/{calendarId}/day-rules` |
| Exceptions | `/api/capacity/calendars/{calendarId}/exceptions` |
| User profiles | `/api/capacity/user-profiles` (+ lifecycle) |
| Project allocations | `/api/capacity/project-allocations` (+ lifecycle) |
| User availability | `GET /api/capacity/users/{userId}/availability` |
| Workspace overview | `GET /api/capacity/workspaces/{workspaceId}/overview` |
| Project allocation summary | `GET /api/capacity/projects/{projectId}/allocations/summary` |
| Over-allocations | `GET /api/capacity/over-allocations` |
| Calculate | `POST /api/capacity/calculate` |

## 10. Calendar Rules

- Workspace must exist and be ACTIVE
- Code unique within workspace; timezone via `ZoneId.of`
- Create seeds Mon–Fri 8h / Sat–Sun 0h day rules
- Only one default ACTIVE calendar per workspace (cleared on set-default / create-as-default)
- ARCHIVED calendars cannot be assigned; archive blocked if referenced by capacity profiles
- Soft-archive only (no hard delete)

## 11. Day Rule Rules

- Atomic replace of exactly 7 days
- Working day ⇒ hours > 0; non-working ⇒ hours = 0; hours ≤ 24
- At least one working day required
- Duplicate `dayOfWeek` rejected

## 12. Exception Rules

- Types: `HOLIDAY`, `NON_WORKING_DAY`, `SPECIAL_WORKING_DAY`, `HALF_DAY`, `ADJUSTED_HOURS`, `COMPANY_EVENT`
- Unique per calendar/date
- Non-working types force hours = 0 / `isWorkingDay = false`
- Exceptions override day rules in calculation

## 13. User Capacity Profile Rules

- ACTIVE workspace member required
- Calendar same workspace + ACTIVE
- `0 < defaultDailyHours ≤ 24`; `0 < focusFactor ≤ 1`
- No overlapping ACTIVE profiles for same member
- Soft-archive lifecycle (activate/deactivate/archive)

## 14. Project Allocation Rules

- Project must exist in same workspace as allocation
- ACTIVE workspace member required
- `0 < allocationPercent ≤ 100`; valid date range
- Allocation does **not** assign tasks or calculate cost
- Overlapping ACTIVE allocations for same user: **block if total > 100%**

## 15. Capacity Calculation Formulas

```text
WorkingHours(date) = exception override OR day-rule hours (else lazy weekday default)
FocusedCapacity(date) = WorkingHours(date) × FocusFactor
ProjectAllocatedCapacity(date, project) = FocusedCapacity × AllocationPercent / 100
TotalAllocationPercent(user, date range) = sum ACTIVE overlapping allocation %
```

Phase 12 does **not** subtract task scheduled hours and does **not** return `estimatedFinishDate`.

Missing profile → lazy defaults: workspace default calendar, daily hours 8, focus factor 0.75.

## 16. Over-allocation Policy

- **Block** when creating/updating would make sum of overlapping ACTIVE allocation percentages **> 100**
- Error: `PROJECT_ALLOCATION_OVER_ALLOCATED`
- Audit: `RESOURCE_OVER_ALLOCATION_BLOCKED`
- Overview / over-allocations APIs still surface over-allocated users for detection of existing data

## 17. Authorization Matrix

| Authority | Mapping |
|---|---|
| CAPACITY_CALENDAR_VIEW/CREATE/UPDATE/ARCHIVE/MANAGE | Seeded; action-level checks |
| CAPACITY_PROFILE_VIEW/CREATE/UPDATE/ARCHIVE/MANAGE | Seeded |
| PROJECT_ALLOCATION_VIEW/CREATE/UPDATE/ARCHIVE/MANAGE | Seeded |
| CAPACITY_VIEW | Overview, availability, over-allocations, project summary |
| CAPACITY_CALCULATE | POST calculate |
| CAPACITY_OVERVIEW_VIEW / CAPACITY_OVERALLOCATION_VIEW | Map to **CAPACITY_VIEW** (Phase 10-style simplification) |

Auth via `CapacityWorkspaceAuthorizationService` (no path interceptor).

## 18. Event Registry Seeder Matrix

Source system: `SCOPERY_CAPACITY`. Seeded 20 codes including calendar lifecycle, day rules, exceptions, profiles, allocations. Outbox via `CapacityPlatformPublisher`.

| Event family | Seeded | Emitted |
|---|---|---|
| CAPACITY_CALENDAR_* | Yes | Yes |
| CAPACITY_DAY_RULES_UPDATED | Yes | Activity (+ audit type) |
| CAPACITY_EXCEPTION_* | Yes | Yes |
| CAPACITY_PROFILE_* | Yes | Yes |
| PROJECT_ALLOCATION_* | Yes | Yes |
| RESOURCE_OVER_ALLOCATED / RESOLVED / CAPACITY_CALCULATED | Optional in seed set | Detection via API/audit block; not spam-emitted on every calculate |

## 19. Activity / Audit / Outbox Notes

- Activity via `CapacityActivityLogger` / `CapacityActivityActions`
- Immutable audit for default calendar change, day rules, exceptions, focus factor, allocation change, over-allocation blocked
- Outbox enqueue on create/update/lifecycle for calendars, exceptions, profiles, allocations
- HTTP Idempotency-Key: **not** wired (documented gap)

## 20. Seed Data

- `DefaultWorkingCalendarSeeder` + `WorkspaceCreatedCapacitySeedListener` on `WorkspaceCreatedEvent`
- Code `DEFAULT_BUSINESS_CALENDAR`, timezone `Asia/Ho_Chi_Minh`, isDefault=true, Mon–Fri 8h
- Idempotent (second run no duplicate)

## 21. Tests Added

Under `src/test/java/com/company/scopery/modules/resourcecapacity/`:

| Class | Focus |
|---|---|
| `CapacityCalendarBusinessRulesActionTest` | create/duplicate/timezone/archive/set-default |
| `CapacityDayRuleBusinessRulesActionTest` | atomic replace + hours validation |
| `CapacityExceptionBusinessRulesActionTest` | holiday/duplicate/hours/delete |
| `CapacityProfileBusinessRulesActionTest` | member/calendar/focus/overlap |
| `CapacityAllocationBusinessRulesActionTest` | workspace match / percent / over-allocation block |
| `CapacityCalculationServiceTest` | weekday/weekend/holiday/focus/allocation/lazy defaults |
| `CapacityEventDefinitionSeedInitializerTest` | seed idempotency |
| `CapacityAuthorizationActionTest` | forbidden without permission |

Also fixed `WorkspaceApplicationServiceTest` constructor for new `ApplicationEventPublisher` on `CreateWorkspaceAction`.

## 22. Commands Run

```text
mvn -q compile -DskipTests
mvn -q -Dtest=CapacityCalendarBusinessRulesActionTest,CapacityDayRuleBusinessRulesActionTest,CapacityExceptionBusinessRulesActionTest,CapacityProfileBusinessRulesActionTest,CapacityAllocationBusinessRulesActionTest,CapacityCalculationServiceTest,CapacityEventDefinitionSeedInitializerTest,CapacityAuthorizationActionTest test
mvn -q test
```

## 23. Test Results

```text
mvn compile → SUCCESS
Phase 12 capacity unit tests (38) → SUCCESS
mvn test (full suite) → BUILD SUCCESS (exit code 0)
```

## 24. Manual Verification

- [ ] Flyway V49 applies cleanly after V48
- [ ] Create workspace → default calendar + Mon–Fri day rules appear
- [ ] Add holiday exception → capacity for that date is zero
- [ ] Add special working Saturday → positive capacity
- [ ] Create user profile focusFactor 0.75
- [ ] Create allocation 50%; calculate week; verify focused × 0.5
- [ ] Create overlapping allocation totaling > 100% → rejected
- [ ] Inactive member profile/allocation → rejected
- [ ] Confirm outbox/activity/audit rows; no schedule/Gantt/cost rows
- [ ] Rerun IAM/event seeders — no duplicates

## 25. Assumptions

- Workspace has no timezone field → default calendar uses `Asia/Ho_Chi_Minh`
- Lazy profile defaults used instead of bulk historical profile seeding
- Over-allocation policy is hard block at > 100% (no warning response)
- Soft FKs to workspace/member/project (UUID columns; app-level integrity)
- `CAPACITY_OVERVIEW_VIEW` / `CAPACITY_OVERALLOCATION_VIEW` intentionally map to `CAPACITY_VIEW`

## 26. Deviations From Prompt

- Module placed at `modules/resourcecapacity` (not under `modules/project/capacity`)
- Spec optional events `RESOURCE_OVER_ALLOCATED` / `CAPACITY_CALCULATED` not required for every calculate call; over-allocation blocked emits audit instead of notification spam
- HTTP Idempotency-Key not wired (same as Phase 11)

## 27. Known Risks

- Large workspace overview paginates members in 100s — may need snapshot/caching in Phase 22/23
- Over-allocation check uses date-range overlap sum of percents (conservative; same-day exact peak may differ if ranges partially overlap)
- Soft FK integrity relies on application rules + DB checks, not DB foreign keys to workspace/project tables
- Calendar timezone correctness for edge DST cases not exhaustively tested

## 28. Future Phases That Must Return to Capacity

| Phase | Must consume / extend |
|---|---|
| 13 Scheduling | Working hours, exceptions, focus, allocation, alreadyScheduledTaskHours |
| 14 Gantt | Project scheduled tasks; do not mutate capacity tables directly |
| 15 Rate Card | Role capacity mapping; hours ≠ cost until CCH |
| 16 Estimation | Effort vs capacity availability |
| 17 Finance | Capacity hours × role CCH |
| 18 Quote | Timeline feasibility |
| 20 Notifications | Over-allocation / capacity conflict alerts |
| 21 AI Planning | Recommend allocation changes with human approval |
| 22 Reporting | Dashboard, utilization forecast, team capacity |
| 23 Hardening | Performance, date-range limits, auth coverage |
| 37 Time/Attendance | Actuals; not planned capacity |

---

**Anti-bịa confirmation:** Phase 12 does not claim task scheduling, estimated finish date, Gantt, labor cost, or timesheet utilization.
