# PHASE 12 — TO-BE Resource Calendar, Working Capacity, Project Allocation & Capacity Foundation

> Project: Scopery Backend  
> Phase: 12  
> Document type: TO-BE implementation-grade specification  
> Status: Planning / Gap-driven implementation spec  
> Depends on: Phase 00 — Master Backend Roadmap, Phase 01 — API Path / Security Baseline, Phase 02 — IAM Core, Phase 03 — Workspace Core, Phase 04 — Platform Audit / Outbox / Idempotency, Phase 05 — Event Registry, Phase 06 — Notification Engine, Phase 07 — AI Agent Platform, Phase 08 — Knowledge / Document Type Catalog, Phase 09 — Project Core / Phase / WBS / Task, Phase 10 — Project Authorization, Phase 11 — Project Template / Phase Catalog  
> API base: `/api`  
> Primary module: `modules/resourcecapacity` or `modules/project/capacity` depending on current architecture  
> Related modules: `workspace`, `iam`, `project`, `eventregistry`, `notification`, future `scheduling`, `gantt`, `ratecard`, `finance`, `timesheet`, `reporting`, `aiagent`  
> Important rule: This file is **not an as-is document**. It defines the TO-BE capacity foundation. Phase 12 does not implement full task scheduling, Gantt, cost, margin, timesheet, or utilization analytics.

---

# 0. Purpose of this file

Phase 12 creates the resource capacity foundation for Scopery.

Previous phases created:

```text
Workspace / member / team foundation
Project / phase / WBS / task foundation
Task estimateHours
Task dueDate
Task assignee/inCharge
TaskDependency
ProjectTemplate
```

Phase 12 answers:

```text
How many working hours does a user have?
Which calendar defines working days?
What are holidays and exceptions?
How much of a user is allocated to a project?
What is the user's effective capacity after focus factor?
Is a user over-allocated by project allocation?
What data will Phase 13 scheduling use to place task effort on calendar days?
```

Phase 12 does **not** answer:

```text
When exactly will each task start?
When exactly will each task finish?
How should dependency graph schedule tasks?
What is the Gantt projection?
What is labor cost?
What is project margin?
What is actual timesheet utilization?
```

Those are later phases.

---

# 1. Source inputs

Before coding Phase 12, the agent must read:

```text
1. Current backend codebase
2. Phase 09 Project Core TO-BE spec and implementation
3. Phase 10 Project Authorization TO-BE spec and implementation
4. Phase 11 Project Template TO-BE spec and implementation
5. Phase 03 Workspace / Member / Team TO-BE spec
6. Phase 02 IAM TO-BE spec
7. Phase 04 Platform Audit / Outbox / Idempotency spec
8. Phase 05 Event Registry spec
9. Existing workspace member entities/actions
10. Existing project task entities/actions
11. Existing calendar/capacity code if any
12. Current BE feature/entity/business-rule inventory
13. Dynamic Work OS feature catalog
```

The agent must not implement Phase 12 from current code only.

---

# 2. Current expected backend state

After Phase 09–11, the backend should have:

```text
Project
ProjectPhase
PhaseDefinition
WbsNode
Task
TaskDependency
ProjectTemplate
WorkspaceMember
WorkspaceTeam
OrgMember
IAM authorities
EventRegistry
Notification foundation
```

Current likely missing/partial areas:

```text
Resource calendar
Working calendar
Holiday/exception calendar
User capacity profile
Focus factor
Project allocation %
Capacity calculation
Over-allocation detection
Capacity snapshot
Calendar assignment
Time-off handling
Capacity report APIs
```

Phase 12 must verify actual current code and classify accurately.

---

# 3. Future-state capabilities related to Phase 12

The future Work OS includes:

```text
Resource and Capacity Management
Time, Attendance, and Expense
Schedule / Gantt
Estimation and Planning
Rate Card and Cost Policy
Project Budget and Profitability
Reporting and Portfolio Intelligence
AI-assisted planning
```

Phase 12 only delivers the capacity foundation needed by these modules.

---

# 4. Classification labels

Every requirement uses one label:

| Label | Meaning |
|---|---|
| `CURRENTLY_IMPLEMENTED` | Current backend already implements it. Agent must verify and test. |
| `PARTIALLY_IMPLEMENTED` | Current backend implements part of it. |
| `MUST_IMPLEMENT_IN_PHASE_12` | Must be implemented or hardened now. |
| `SEED_ONLY_IN_PHASE_12` | Seed calendar/events/permissions/config now; full consumer later. |
| `DEFERRED_TO_PHASE_XX` | Do not implement now; later phase must return. |
| `DEFERRED_TO_POST_23_BACKLOG` | Outside core 00–23 roadmap, part of full Work OS expansion. |
| `NOT_IN_SCOPE_FOR_BACKEND_NOW` | Explicitly not backend scope now. |

---

# 5. Phase 12 target statement

Phase 12 must deliver a future-ready capacity foundation:

```text
1. Workspace has working calendar definitions.
2. Working calendar defines working days and daily working hours.
3. Calendar exceptions define holidays, non-working days, or adjusted working hours.
4. Workspace member can have capacity profile.
5. User capacity profile defines working calendar, default daily hours, focus factor, and availability status.
6. Project allocation records define how much of a user is allocated to a project over a date range.
7. Capacity APIs can calculate effective capacity per user/project/date range.
8. Over-allocation can be detected from allocation percentages.
9. Capacity foundation can be consumed by Phase 13 scheduling.
10. Capacity changes are authorized, audited, and evented.
11. Capacity does not calculate task schedule, Gantt, cost, or actual utilization in Phase 12.
```

---

# 6. Core concepts

## 6.1 Working calendar

A working calendar defines what days/hours are normally working time.

Example:

```text
Monday-Friday
8 working hours per day
Saturday/Sunday non-working
Timezone Asia/Ho_Chi_Minh
```

## 6.2 Calendar exception

A calendar exception overrides normal working calendar.

Examples:

```text
Public holiday
Company holiday
Half-day
Special working Saturday
User-specific leave
```

## 6.3 Focus factor

Focus factor converts theoretical working hours into realistic productive planning hours.

Examples:

```text
Developer: 0.70–0.80
QA: 0.70–0.85
BA/PM: 0.50–0.70
Designer: 0.70–0.80
```

Formula:

```text
Focused Daily Capacity = Working Hours Per Day × Focus Factor
```

## 6.4 Project allocation

Allocation means what portion of a user's focused capacity is reserved for a project.

Example:

```text
User A allocated 50% to Project X from Aug 1 to Aug 31.
```

Formula:

```text
Project Daily Capacity = Focused Daily Capacity × Allocation %
```

## 6.5 Already planned hours

In Phase 12, "already planned hours" can only come from project allocation/capacity reservations if implemented.

Task-level daily planned hours are Phase 13.

Therefore Phase 12 formula is limited:

```text
Effective Daily Project Capacity =
Working Hours Per Day × Focus Factor × Project Allocation %
```

Full formula with task scheduling:

```text
Effective Daily Remaining Capacity =
Working Hours Per Day × Focus Factor × Project Allocation % − Already Scheduled Task Hours
```

The second formula is Phase 13.

---

# 7. Phase 12 scope decision

## 7.1 Must implement now

```text
WorkspaceWorkingCalendar
Working day rules
Calendar exceptions
UserCapacityProfile
WorkspaceMember calendar assignment
Focus factor
Default daily hours
ProjectResourceAllocation
Capacity calculation service
Over-allocation detection by allocation %
Capacity event definitions
Capacity permissions
Capacity tests
Completion gap matrix
```

## 7.2 Optional in Phase 12

```text
Team capacity summary
Workspace capacity overview
Capacity snapshot table
User-specific time off
Capacity reservation placeholder
```

Implement only if current architecture needs them.

## 7.3 Must not implement now

```text
Task scheduling
Task start date / finish date calculation
Dependency-aware scheduling
Critical path
Gantt chart
Resource leveling
Skill matching
Role cost / CCH
Finance roll-up
Timesheet actuals
Expense
Attendance check-in
Utilization analytics dashboard
AI capacity recommendations
```

---

# 8. TO-BE capability matrix

---

## 8.1 CAP-001 — Workspace working calendar

| Item | Value |
|---|---|
| Future capability | Define workspace calendars with timezone, working days, and hours |
| Current state | Missing/unknown |
| Phase 12 target | Implement |
| Classification | `MUST_IMPLEMENT_IN_PHASE_12` |

Rules:

```text
1. Workspace must exist and be ACTIVE.
2. Calendar name required.
3. Timezone required.
4. Calendar code unique within workspace.
5. At least one working day required.
6. Daily working hours must be > 0 and <= 24.
7. Default workspace calendar can be marked.
8. Only one default calendar per workspace.
9. Archived calendar cannot be assigned to new users.
10. Calendar cannot be hard-deleted if assigned to users or allocations depend on it.
```

---

## 8.2 CAP-002 — Working day rules

| Item | Value |
|---|---|
| Future capability | Define normal working schedule per day of week |
| Current state | Missing/unknown |
| Phase 12 target | Implement |
| Classification | `MUST_IMPLEMENT_IN_PHASE_12` |

Required days:

```text
MONDAY
TUESDAY
WEDNESDAY
THURSDAY
FRIDAY
SATURDAY
SUNDAY
```

Fields:

```text
isWorkingDay
startTime optional
endTime optional
workingHours
```

Rules:

```text
1. Each calendar should have rules for seven days or use defaults.
2. Non-working day has workingHours = 0.
3. Working day must have workingHours > 0.
4. workingHours cannot exceed 24.
5. startTime/endTime optional unless product needs schedule slots.
6. Time zone belongs to calendar.
```

---

## 8.3 CAP-003 — Calendar exception

| Item | Value |
|---|---|
| Future capability | Override working calendar for holidays/special working days |
| Current state | Missing/unknown |
| Phase 12 target | Implement |
| Classification | `MUST_IMPLEMENT_IN_PHASE_12` |

Types:

```text
HOLIDAY
NON_WORKING_DAY
SPECIAL_WORKING_DAY
HALF_DAY
ADJUSTED_HOURS
COMPANY_EVENT
USER_TIME_OFF optional
```

Rules:

```text
1. Exception belongs to workspace calendar or user capacity profile depending on scope.
2. Date required.
3. Exception date unique per calendar/type or calendar/date depending on design.
4. Non-working exception sets available hours to 0.
5. Adjusted-hours exception requires workingHours >= 0 and <= 24.
6. Exception reason optional but recommended.
7. Exceptions override normal day rule.
8. User-specific time off may be deferred if HR/time-off is not in Phase 12.
```

Recommended Phase 12:

```text
Workspace calendar exceptions now.
User time-off can be optional or deferred to Phase 37 Time/Attendance/Expense.
```

---

## 8.4 CAP-004 — User capacity profile

| Item | Value |
|---|---|
| Future capability | Define planning capacity for a workspace member |
| Current state | Missing/unknown |
| Phase 12 target | Implement |
| Classification | `MUST_IMPLEMENT_IN_PHASE_12` |

Fields:

```text
workspaceMemberId
userId
workspaceId
workingCalendarId
defaultDailyHours
focusFactor
capacityStatus
effectiveFrom
effectiveTo
```

Capacity status:

```text
ACTIVE
INACTIVE
ON_LEAVE
UNAVAILABLE
```

Rules:

```text
1. WorkspaceMember must exist and be ACTIVE.
2. workingCalendarId must belong to same workspace and be ACTIVE.
3. defaultDailyHours > 0 and <= 24.
4. focusFactor > 0 and <= 1.
5. effective date range valid.
6. User cannot have overlapping active capacity profiles in same workspace unless versioned history supports it.
7. If no profile exists, use workspace default calendar and default focus factor.
```

Recommended defaults:

```text
defaultDailyHours = 8
focusFactor = 0.75
```

---

## 8.5 CAP-005 — Project resource allocation

| Item | Value |
|---|---|
| Future capability | Allocate user capacity to project over a date range |
| Current state | Missing/unknown |
| Phase 12 target | Implement |
| Classification | `MUST_IMPLEMENT_IN_PHASE_12` |

Fields:

```text
projectId
workspaceId
workspaceMemberId
userId
allocationPercent
startDate
endDate
status
allocationType
notes
```

Allocation type:

```text
PLANNED
RESERVED
CONFIRMED
```

Status:

```text
ACTIVE
INACTIVE
ARCHIVED
```

Rules:

```text
1. Project must exist and be ACTIVE or DRAFT depending on product.
2. Project workspace must match allocation workspace.
3. WorkspaceMember must be active in project workspace.
4. allocationPercent > 0 and <= 100.
5. startDate required.
6. endDate optional for open-ended allocation.
7. endDate >= startDate if provided.
8. Overlapping allocations for same user are allowed only if total allocation <= 100 unless explicit over-allocation allowed.
9. Over-allocation should be detected and returned as warning/error according to policy.
10. Allocation does not assign tasks.
11. Allocation does not calculate cost.
```

Recommended policy:

```text
Warn when > 100%.
Block when > 120% unless admin override.
```

If no warning mechanism exists:

```text
Block > 100% by default.
```

---

## 8.6 CAP-006 — Capacity calculation service

| Item | Value |
|---|---|
| Future capability | Calculate available capacity for a user/project/date range |
| Current state | Missing/unknown |
| Phase 12 target | Implement |
| Classification | `MUST_IMPLEMENT_IN_PHASE_12` |

Required calculations:

```text
Working hours by date
Focused capacity by date
Allocated capacity by project/date
Total allocation percent by user/date
Over-allocation flag
Available unallocated focused capacity
```

Formula:

```text
FocusedCapacity(date) =
CalendarWorkingHours(date) × FocusFactor
```

```text
ProjectAllocatedCapacity(date, project) =
FocusedCapacity(date) × AllocationPercent(project, date)
```

```text
TotalAllocationPercent(user, date) =
sum(all active allocationPercent covering date)
```

```text
AvailableAllocationPercent(user, date) =
max(0, 100 - TotalAllocationPercent(user, date))
```

```text
UnallocatedFocusedCapacity(date) =
FocusedCapacity(date) × AvailableAllocationPercent(date)
```

Important:

```text
Phase 12 does not subtract task scheduled hours.
```

That belongs to Phase 13.

---

## 8.7 CAP-007 — Capacity overview APIs

| Item | Value |
|---|---|
| Future capability | View workspace/project/user capacity for planning |
| Current state | Missing/unknown |
| Phase 12 target | Implement basic summaries |
| Classification | `MUST_IMPLEMENT_IN_PHASE_12` |

Required summaries:

```text
User capacity summary
Project allocation summary
Workspace allocation overview
Over-allocation list
```

Optional summaries:

```text
Team capacity summary
Phase capacity summary
Role capacity summary
```

Role capacity is deferred to Phase 15 unless roles already exist.

---

## 8.8 CAP-008 — Capacity snapshot

| Item | Value |
|---|---|
| Future capability | Persist calculated capacity for reporting/performance |
| Current state | Missing |
| Phase 12 target | Optional/defer |
| Classification | `DEFERRED_TO_PHASE_22_REPORTING` unless needed for performance |

Recommended Phase 12:

```text
Calculate on demand.
Do not persist snapshots unless performance requires it.
```

Future entity:

```text
CapacitySnapshot
```

Use later for:

```text
reporting
trend analysis
portfolio capacity
forecast history
AI analysis
```

---

## 8.9 CAP-009 — Team capacity

| Item | Value |
|---|---|
| Future capability | Aggregate member capacity by team |
| Current state | WorkspaceTeam exists |
| Phase 12 target | Optional basic aggregation |
| Classification | `DEFERRED_TO_PHASE_22_REPORTING` or optional Phase 12 |

If implemented:

```text
Team capacity = sum active member focused capacity over date range
```

Rules:

```text
1. Team must belong to workspace.
2. Only active team members counted.
3. User with multiple team membership may be double-counted unless aggregation mode handles unique users.
```

Recommended:

```text
Defer detailed team capacity to reporting.
```

---

## 8.10 CAP-010 — Time off / leave

| Item | Value |
|---|---|
| Future capability | User-specific leave, PTO, sick leave, absence |
| Current state | Missing |
| Phase 12 target | Defer or implement minimal user capacity exception |
| Classification | `DEFERRED_TO_PHASE_37_TIME_ATTENDANCE_EXPENSE` by default |

Optional Phase 12 minimal:

```text
UserCapacityException
```

Rules if implemented:

```text
1. Belongs to workspace member.
2. Date/date range required.
3. Reduces capacity.
4. Does not expose leave reason to unauthorized users.
5. Approval workflow deferred.
```

Recommended:

```text
Do not implement HR-style leave approval in Phase 12.
```

---

## 8.11 CAP-011 — Skill / role capacity

| Item | Value |
|---|---|
| Future capability | Capacity by role/skill/cost role |
| Current state | Missing/partial |
| Phase 12 target | Defer |
| Classification | `DEFERRED_TO_PHASE_15_RATE_CARD_COST_ROLE` and `PHASE_16_ESTIMATION` |

Reason:

```text
Phase 15 defines cost roles/rate cards.
Phase 16 uses role estimates.
Phase 12 only knows users/members/capacity.
```

---

## 8.12 CAP-012 — Capacity recommendation / resource leveling

| Item | Value |
|---|---|
| Future capability | Recommend alternate assignee, adjust allocation, level schedule |
| Current state | Missing |
| Phase 12 target | Defer |
| Classification | `DEFERRED_TO_PHASE_13_SCHEDULING` and `PHASE_21_AI_ASSISTED_PLANNING` |

Do not implement AI recommendation in Phase 12.

---

# 9. Entity model TO-BE

If current schema differs, the agent must map actual fields and document gaps.

---

## 9.1 WorkspaceWorkingCalendar — `capacity_working_calendar`

Required fields:

```text
id UUID PK
workspace_id UUID NOT NULL
code VARCHAR(100) NOT NULL
name VARCHAR(255) NOT NULL
description TEXT NULL
timezone VARCHAR(100) NOT NULL
is_default BOOLEAN NOT NULL DEFAULT false
status VARCHAR(50) NOT NULL
created_at / created_by
updated_at / updated_by
archived_at / archived_by
version INT
```

Status:

```text
ACTIVE
INACTIVE
ARCHIVED
```

Constraints:

```text
unique workspace_id + code
only one default active calendar per workspace
```

---

## 9.2 WorkingCalendarDayRule — `capacity_calendar_day_rule`

Required fields:

```text
id UUID PK
working_calendar_id UUID NOT NULL
day_of_week VARCHAR(20) NOT NULL
is_working_day BOOLEAN NOT NULL
start_time TIME NULL
end_time TIME NULL
working_hours DECIMAL(5,2) NOT NULL
created_at / updated_at
```

Constraints:

```text
unique working_calendar_id + day_of_week
working_hours >= 0 and <= 24
```

Rules:

```text
If isWorkingDay = false, workingHours must be 0.
If isWorkingDay = true, workingHours must be > 0.
```

---

## 9.3 CalendarException — `capacity_calendar_exception`

Required fields:

```text
id UUID PK
working_calendar_id UUID NOT NULL
exception_date DATE NOT NULL
exception_type VARCHAR(50) NOT NULL
name VARCHAR(255) NOT NULL
description TEXT NULL
is_working_day BOOLEAN NOT NULL
working_hours DECIMAL(5,2) NOT NULL
created_at / created_by
updated_at / updated_by
```

Exception types:

```text
HOLIDAY
NON_WORKING_DAY
SPECIAL_WORKING_DAY
HALF_DAY
ADJUSTED_HOURS
COMPANY_EVENT
```

Constraints:

```text
unique working_calendar_id + exception_date
working_hours >= 0 and <= 24
```

---

## 9.4 UserCapacityProfile — `capacity_user_profile`

Required fields:

```text
id UUID PK
workspace_id UUID NOT NULL
workspace_member_id UUID NOT NULL
user_id UUID NOT NULL
working_calendar_id UUID NOT NULL
default_daily_hours DECIMAL(5,2) NOT NULL
focus_factor DECIMAL(4,3) NOT NULL
capacity_status VARCHAR(50) NOT NULL
effective_from DATE NOT NULL
effective_to DATE NULL
created_at / created_by
updated_at / updated_by
archived_at / archived_by
version INT
```

Status:

```text
ACTIVE
INACTIVE
ON_LEAVE
UNAVAILABLE
ARCHIVED
```

Constraints:

```text
default_daily_hours > 0 and <= 24
focus_factor > 0 and <= 1
effective_to >= effective_from if not null
no overlapping ACTIVE profile for same workspace_member_id unless versioning supports it
```

---

## 9.5 ProjectResourceAllocation — `capacity_project_resource_allocation`

Required fields:

```text
id UUID PK
workspace_id UUID NOT NULL
project_id UUID NOT NULL
workspace_member_id UUID NOT NULL
user_id UUID NOT NULL
allocation_percent DECIMAL(5,2) NOT NULL
allocation_type VARCHAR(50) NOT NULL
status VARCHAR(50) NOT NULL
start_date DATE NOT NULL
end_date DATE NULL
notes TEXT NULL
created_at / created_by
updated_at / updated_by
archived_at / archived_by
version INT
```

Allocation type:

```text
PLANNED
RESERVED
CONFIRMED
```

Status:

```text
ACTIVE
INACTIVE
ARCHIVED
```

Constraints:

```text
allocation_percent > 0 and <= 100
end_date >= start_date if not null
project belongs to workspace_id
workspace_member belongs to workspace_id
```

---

## 9.6 UserCapacityException — optional / deferred

If implemented:

```text
capacity_user_exception
```

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
workspace_member_id UUID NOT NULL
start_date DATE NOT NULL
end_date DATE NOT NULL
exception_type VARCHAR(50) NOT NULL
working_hours_override DECIMAL(5,2) NULL
reason_private TEXT NULL
status VARCHAR(50) NOT NULL
created_at / created_by
updated_at / updated_by
```

Recommended:

```text
DEFERRED_TO_PHASE_37 unless needed by Phase 13 scheduling.
```

---

# 10. API TO-BE list

All APIs use `/api`.

---

## 10.1 Working calendar APIs

```text
POST  /api/capacity/calendars
GET   /api/capacity/calendars
GET   /api/capacity/calendars/{calendarId}
PUT   /api/capacity/calendars/{calendarId}
PATCH /api/capacity/calendars/{calendarId}/activate
PATCH /api/capacity/calendars/{calendarId}/deactivate
PATCH /api/capacity/calendars/{calendarId}/archive
PATCH /api/capacity/calendars/{calendarId}/set-default
```

Filters:

```text
workspaceId
status
isDefault
code
```

---

## 10.2 Day rule APIs

```text
PUT /api/capacity/calendars/{calendarId}/day-rules
GET /api/capacity/calendars/{calendarId}/day-rules
```

Recommended:

```text
Replace all seven day rules atomically.
```

Rules:

```text
1. Must contain valid dayOfWeek values.
2. Duplicate dayOfWeek rejected.
3. Must leave calendar with at least one working day.
```

---

## 10.3 Calendar exception APIs

```text
POST   /api/capacity/calendars/{calendarId}/exceptions
GET    /api/capacity/calendars/{calendarId}/exceptions
GET    /api/capacity/calendars/{calendarId}/exceptions/{exceptionId}
PUT    /api/capacity/calendars/{calendarId}/exceptions/{exceptionId}
DELETE /api/capacity/calendars/{calendarId}/exceptions/{exceptionId}
```

DELETE can hard delete if exception is future/config-only, but audit is recommended.

---

## 10.4 User capacity profile APIs

```text
POST  /api/capacity/user-profiles
GET   /api/capacity/user-profiles
GET   /api/capacity/user-profiles/{profileId}
PUT   /api/capacity/user-profiles/{profileId}
PATCH /api/capacity/user-profiles/{profileId}/activate
PATCH /api/capacity/user-profiles/{profileId}/deactivate
PATCH /api/capacity/user-profiles/{profileId}/archive
```

Filters:

```text
workspaceId
userId
workspaceMemberId
status
effectiveDate
```

---

## 10.5 Project allocation APIs

```text
POST  /api/capacity/project-allocations
GET   /api/capacity/project-allocations
GET   /api/capacity/project-allocations/{allocationId}
PUT   /api/capacity/project-allocations/{allocationId}
PATCH /api/capacity/project-allocations/{allocationId}/activate
PATCH /api/capacity/project-allocations/{allocationId}/deactivate
PATCH /api/capacity/project-allocations/{allocationId}/archive
```

Filters:

```text
workspaceId
projectId
userId
workspaceMemberId
dateFrom
dateTo
status
overAllocatedOnly
```

---

## 10.6 Capacity calculation APIs

```text
GET /api/capacity/users/{userId}/availability
GET /api/capacity/workspaces/{workspaceId}/overview
GET /api/capacity/projects/{projectId}/allocations/summary
GET /api/capacity/over-allocations
POST /api/capacity/calculate
```

Example calculate request:

```json
{
  "workspaceId": "uuid",
  "userIds": ["uuid"],
  "projectIds": ["uuid"],
  "dateFrom": "2026-08-01",
  "dateTo": "2026-08-31"
}
```

Response should include:

```text
workingHours
focusedCapacityHours
allocatedCapacityHours
allocationPercent
availableAllocationPercent
overAllocated
```

---

# 11. Authorization requirements

Required IAM authorities:

```text
CAPACITY_CALENDAR_VIEW
CAPACITY_CALENDAR_CREATE
CAPACITY_CALENDAR_UPDATE
CAPACITY_CALENDAR_ARCHIVE
CAPACITY_CALENDAR_MANAGE

CAPACITY_PROFILE_VIEW
CAPACITY_PROFILE_CREATE
CAPACITY_PROFILE_UPDATE
CAPACITY_PROFILE_ARCHIVE
CAPACITY_PROFILE_MANAGE

PROJECT_ALLOCATION_VIEW
PROJECT_ALLOCATION_CREATE
PROJECT_ALLOCATION_UPDATE
PROJECT_ALLOCATION_ARCHIVE
PROJECT_ALLOCATION_MANAGE

CAPACITY_VIEW
CAPACITY_CALCULATE
CAPACITY_OVERVIEW_VIEW
CAPACITY_OVERALLOCATION_VIEW
```

Rules:

```text
1. All capacity APIs require authentication.
2. Workspace-scoped calendar/profile/allocation APIs require active workspace access.
3. User must have relevant IAM authority on workspace.
4. Project allocation create/update requires project access plus allocation permission.
5. User can view own basic capacity if product supports self-view.
6. Viewing other users' capacity requires CAPACITY_VIEW or CAPACITY_PROFILE_VIEW.
7. Private time-off details, if implemented, require stronger permission.
```

Future:

```text
CAPACITY_PRIVATE_LEAVE_VIEW — Phase 37
CAPACITY_ROLE_VIEW — Phase 15/16
CAPACITY_EXPORT — Phase 22
```

---

# 12. Integration with Project Core

Phase 12 consumes:

```text
Project
Task
WorkspaceMember
Task.assignee/inCharge
Task.estimateHours
Task.dueDate
```

But Phase 12 does not schedule tasks.

Allowed:

```text
Capacity calculation can show project allocation coverage.
Capacity calculation can show total effort assigned to a user/project as simple aggregate if useful.
```

Not allowed:

```text
Daily task distribution
estimatedFinishDate
startDate calculation
dependency scheduling
critical path
```

If task effort aggregation is implemented:

```text
It must be labeled "assigned estimated effort", not scheduled hours.
```

---

# 13. Integration with Phase 13 Scheduling

Phase 13 will use Phase 12 APIs/data.

Phase 13 expected formulas:

```text
Effective Daily Remaining Capacity =
CalendarWorkingHours × FocusFactor × ProjectAllocation% − AlreadyScheduledTaskHours
```

Phase 13 will calculate:

```text
task estimated start date
task estimated finish date
due date capacity gap
schedule run
dependency-aware ordering
capacity conflicts
```

Phase 12 must expose enough data:

```text
working hours by date
focus factor by user/date
allocation percent by user/project/date
exceptions by date
over-allocation status
```

---

# 14. Integration with Rate Card / Finance

Phase 12 does not calculate cost.

Phase 15/17 will use:

```text
Task estimateHours
User/cost role
Rate card CCH
Inflation
```

Phase 12 only supplies:

```text
capacity hours
allocation %
working days
```

Important rule:

```text
Capacity hours are not labor cost.
```

---

# 15. Integration with Timesheet / Actuals

Phase 12 is planned capacity, not actual work.

Actual time comes later:

```text
Phase 37 — Time / Attendance / Expense
```

Do not implement:

```text
clock-in/out
timesheet approvals
actual utilization
expense reports
attendance policy
```

---

# 16. Event Registry integration

Source system:

```text
SCOPERY_CAPACITY
```

If no separate module source exists:

```text
SCOPERY_PROJECT
```

Recommended source:

```text
SCOPERY_CAPACITY
```

## 16.1 Required Phase 12 events

```text
CAPACITY_CALENDAR_CREATED
CAPACITY_CALENDAR_UPDATED
CAPACITY_CALENDAR_ARCHIVED
CAPACITY_CALENDAR_DEFAULT_CHANGED

CAPACITY_CALENDAR_DAY_RULES_UPDATED
CAPACITY_CALENDAR_EXCEPTION_CREATED
CAPACITY_CALENDAR_EXCEPTION_UPDATED
CAPACITY_CALENDAR_EXCEPTION_DELETED

USER_CAPACITY_PROFILE_CREATED
USER_CAPACITY_PROFILE_UPDATED
USER_CAPACITY_PROFILE_ARCHIVED
USER_CAPACITY_PROFILE_ACTIVATED
USER_CAPACITY_PROFILE_DEACTIVATED

PROJECT_RESOURCE_ALLOCATION_CREATED
PROJECT_RESOURCE_ALLOCATION_UPDATED
PROJECT_RESOURCE_ALLOCATION_ARCHIVED
PROJECT_RESOURCE_ALLOCATION_ACTIVATED
PROJECT_RESOURCE_ALLOCATION_DEACTIVATED

RESOURCE_OVER_ALLOCATED
RESOURCE_OVER_ALLOCATION_RESOLVED
CAPACITY_CALCULATED
```

## 16.2 Standard variables

```text
actor.userId
organization.id
workspace.id
project.id
calendar.id
calendar.code
profile.id
target.userId
workspaceMember.id
allocation.id
allocation.percent
date.from
date.to
overAllocation.percent
occurredAt
traceId
```

---

# 17. Notification integration

Phase 12 should not spam users.

Optional notifications:

```text
RESOURCE_OVER_ALLOCATED_ADMIN_NOTIFICATION
PROJECT_ALLOCATION_CHANGED_NOTIFICATION
```

Recommended:

```text
Seed events only in Phase 12.
User-facing notifications deferred to Phase 20/22.
```

Over-allocation notifications:

```text
DEFERRED_TO_PHASE_20_PROJECT_NOTIFICATIONS or PHASE_22_REPORTING
```

---

# 18. AI Agent integration

Phase 12 does not implement AI recommendations.

Future AI use cases:

```text
Suggest alternate assignee
Explain capacity risk
Recommend allocation changes
Detect over-allocation patterns
Generate capacity report summary
```

Deferred:

```text
Phase 21 — AI-assisted Project Planning
Phase 22 — Reporting summary
```

Phase 12 must provide structured capacity data so future AI can use it.

---

# 19. Platform audit/outbox/idempotency integration

Phase 12 must follow Phase 04.

## 19.1 Activity log actions

```text
CAPACITY_CALENDAR_CREATED
CAPACITY_CALENDAR_UPDATED
CAPACITY_CALENDAR_ARCHIVED
CAPACITY_DAY_RULES_UPDATED
CAPACITY_EXCEPTION_CREATED
CAPACITY_EXCEPTION_UPDATED
CAPACITY_PROFILE_CREATED
CAPACITY_PROFILE_UPDATED
PROJECT_ALLOCATION_CREATED
PROJECT_ALLOCATION_UPDATED
PROJECT_ALLOCATION_ARCHIVED
```

## 19.2 Audit-sensitive actions

Audit:

```text
Calendar default changed
Working hours changed
Calendar exception created/updated/deleted
User focus factor changed
User capacity status changed
Project allocation percent changed
Over-allocation override allowed
```

Reason:

```text
These changes affect schedule forecasts and project commitments.
```

## 19.3 Idempotency

Recommended for:

```text
POST /api/capacity/calendars
PUT  /api/capacity/calendars/{id}/day-rules
POST /api/capacity/calendars/{id}/exceptions
POST /api/capacity/user-profiles
POST /api/capacity/project-allocations
```

## 19.4 Outbox

All capacity events should use platform outbox if available.

If not:

```text
Use current event publisher and document gap.
```

---

# 20. Seed data requirements

## 20.1 Default calendar seeder

When a workspace is created, optionally seed:

```text
DEFAULT_BUSINESS_CALENDAR
```

Default:

```text
Timezone: workspace timezone or Asia/Ho_Chi_Minh fallback
Monday-Friday: 8h
Saturday/Sunday: non-working
isDefault: true
```

If workspace timezone is not modeled:

```text
Use configured system default timezone.
Document assumption.
```

## 20.2 Default capacity profile seeder

For active workspace members, optionally create profile:

```text
defaultDailyHours = 8
focusFactor = 0.75
workingCalendar = workspace default calendar
status = ACTIVE
```

Recommended:

```text
Do not auto-create for all historical members unless migration explicitly handles it.
```

Alternative:

```text
Lazy default profile resolution if no explicit profile exists.
```

## 20.3 Event seeder

Seed Phase 12 capacity events.

## 20.4 Permission seeder

Seed capacity authorities.

---

# 21. Business rules master

## 21.1 Calendar rules

```text
CAP-CAL-001 Workspace must exist and be ACTIVE.
CAP-CAL-002 Calendar code required.
CAP-CAL-003 Calendar code unique within workspace.
CAP-CAL-004 Calendar name required.
CAP-CAL-005 Timezone required.
CAP-CAL-006 Calendar must have at least one working day.
CAP-CAL-007 Only one default active calendar per workspace.
CAP-CAL-008 Archived calendar cannot be assigned.
CAP-CAL-009 Calendar in use cannot be hard-deleted.
```

## 21.2 Day rule rules

```text
CAP-DAY-001 dayOfWeek required and valid.
CAP-DAY-002 One rule per day per calendar.
CAP-DAY-003 Working day requires workingHours > 0.
CAP-DAY-004 Non-working day requires workingHours = 0.
CAP-DAY-005 workingHours <= 24.
CAP-DAY-006 Updating day rules is atomic.
```

## 21.3 Exception rules

```text
CAP-EXC-001 Exception date required.
CAP-EXC-002 Exception type required.
CAP-EXC-003 Calendar must exist and be active.
CAP-EXC-004 One exception per calendar/date unless multiple exception policy exists.
CAP-EXC-005 Non-working exception sets workingHours = 0.
CAP-EXC-006 Adjusted-hours exception requires 0 <= workingHours <= 24.
CAP-EXC-007 Exception overrides day rule.
```

## 21.4 Capacity profile rules

```text
CAP-PRO-001 WorkspaceMember must exist.
CAP-PRO-002 WorkspaceMember must be ACTIVE.
CAP-PRO-003 Calendar must belong to same workspace.
CAP-PRO-004 Calendar must be ACTIVE.
CAP-PRO-005 defaultDailyHours > 0 and <= 24.
CAP-PRO-006 focusFactor > 0 and <= 1.
CAP-PRO-007 effectiveTo >= effectiveFrom if provided.
CAP-PRO-008 No overlapping active profiles for same member unless versioning supports it.
CAP-PRO-009 Inactive member cannot have active profile.
```

## 21.5 Project allocation rules

```text
CAP-ALL-001 Project must exist.
CAP-ALL-002 Project workspace must match allocation workspace.
CAP-ALL-003 WorkspaceMember must be active in project workspace.
CAP-ALL-004 allocationPercent > 0 and <= 100.
CAP-ALL-005 startDate required.
CAP-ALL-006 endDate >= startDate if provided.
CAP-ALL-007 Overlapping allocations are checked.
CAP-ALL-008 Total allocation above threshold returns warning/error.
CAP-ALL-009 Allocation does not assign tasks.
CAP-ALL-010 Allocation does not calculate cost.
```

## 21.6 Capacity calculation rules

```text
CAP-CALC-001 Calendar exceptions override day rules.
CAP-CALC-002 Focus factor applied after working hours.
CAP-CALC-003 Allocation percent applied after focus factor.
CAP-CALC-004 Non-working day capacity is zero.
CAP-CALC-005 Missing profile uses workspace default profile if configured.
CAP-CALC-006 Phase 12 does not subtract task scheduled hours.
CAP-CALC-007 Phase 12 does not calculate estimatedFinishDate.
```

---

# 22. Error catalog requirements

Exact names follow project convention, but these concepts must exist.

```text
CAPACITY_CALENDAR_NOT_FOUND
CAPACITY_CALENDAR_CODE_ALREADY_EXISTS
CAPACITY_CALENDAR_INVALID_TIMEZONE
CAPACITY_CALENDAR_NO_WORKING_DAY
CAPACITY_CALENDAR_DEFAULT_CONFLICT
CAPACITY_CALENDAR_ARCHIVED
CAPACITY_CALENDAR_IN_USE

CAPACITY_DAY_RULE_INVALID_DAY
CAPACITY_DAY_RULE_DUPLICATE
CAPACITY_DAY_RULE_INVALID_HOURS
CAPACITY_DAY_RULE_ATOMIC_UPDATE_FAILED

CAPACITY_EXCEPTION_NOT_FOUND
CAPACITY_EXCEPTION_DUPLICATE_DATE
CAPACITY_EXCEPTION_INVALID_TYPE
CAPACITY_EXCEPTION_INVALID_HOURS

CAPACITY_PROFILE_NOT_FOUND
CAPACITY_PROFILE_MEMBER_NOT_FOUND
CAPACITY_PROFILE_MEMBER_INACTIVE
CAPACITY_PROFILE_CALENDAR_WORKSPACE_MISMATCH
CAPACITY_PROFILE_INVALID_DAILY_HOURS
CAPACITY_PROFILE_INVALID_FOCUS_FACTOR
CAPACITY_PROFILE_DATE_RANGE_INVALID
CAPACITY_PROFILE_OVERLAP

PROJECT_ALLOCATION_NOT_FOUND
PROJECT_ALLOCATION_PROJECT_NOT_FOUND
PROJECT_ALLOCATION_PROJECT_WORKSPACE_MISMATCH
PROJECT_ALLOCATION_MEMBER_INACTIVE
PROJECT_ALLOCATION_INVALID_PERCENT
PROJECT_ALLOCATION_DATE_RANGE_INVALID
PROJECT_ALLOCATION_OVER_ALLOCATED
PROJECT_ALLOCATION_ACCESS_DENIED

CAPACITY_CALCULATION_INVALID_RANGE
CAPACITY_ACCESS_DENIED
```

---

# 23. Required tests

Phase 12 is incomplete without tests.

---

## 23.1 Calendar tests

```text
createCalendar_valid_success
createCalendar_duplicateCodeSameWorkspace_conflict
createCalendar_sameCodeDifferentWorkspace_allowed
createCalendar_missingTimezone_rejected
createCalendar_invalidTimezone_rejected
createCalendar_noWorkingDay_rejected
setDefaultCalendar_valid_success
setDefaultCalendar_onlyOneDefault
archiveCalendar_assignedToProfile_rejectedOrDocumented
archiveCalendar_valid_success
```

## 23.2 Day rule tests

```text
updateDayRules_valid_success
updateDayRules_duplicateDay_rejected
updateDayRules_invalidDay_rejected
updateDayRules_workingDayZeroHours_rejected
updateDayRules_nonWorkingDayNonZeroHours_rejected
updateDayRules_hoursOver24_rejected
updateDayRules_atomicRollbackOnInvalidRule
```

## 23.3 Exception tests

```text
createException_holiday_success
createException_duplicateDate_rejected
createException_adjustedHours_success
createException_invalidHours_rejected
exceptionOverridesNormalWorkingDay
exceptionCanCreateSpecialWorkingDay
deleteException_success
```

## 23.4 User capacity profile tests

```text
createProfile_valid_success
createProfile_inactiveWorkspaceMember_rejected
createProfile_calendarDifferentWorkspace_rejected
createProfile_archivedCalendar_rejected
createProfile_invalidDailyHours_rejected
createProfile_invalidFocusFactorZero_rejected
createProfile_invalidFocusFactorAboveOne_rejected
createProfile_overlappingActiveProfile_rejected
updateProfile_focusFactor_audited
archiveProfile_valid_success
```

## 23.5 Project allocation tests

```text
createAllocation_valid_success
createAllocation_projectDifferentWorkspace_rejected
createAllocation_inactiveMember_rejected
createAllocation_invalidPercentZero_rejected
createAllocation_invalidPercentOver100_rejected
createAllocation_invalidDateRange_rejected
createAllocation_overAllocation_rejectedOrWarnedAccordingToPolicy
updateAllocation_valid_success
archiveAllocation_valid_success
```

## 23.6 Capacity calculation tests

```text
calculateCapacity_weekday_success
calculateCapacity_nonWorkingDay_zero
calculateCapacity_holiday_zero
calculateCapacity_specialWorkingDay_positive
calculateCapacity_focusFactorApplied
calculateCapacity_allocationPercentApplied
calculateCapacity_totalAllocationPercent
calculateCapacity_availableAllocationPercent
calculateCapacity_overAllocatedFlag
calculateCapacity_missingProfile_usesDefault
calculateCapacity_doesNotSubtractTaskScheduledHours
calculateCapacity_doesNotReturnEstimatedFinishDate
```

## 23.7 Authorization tests

```text
createCalendar_withoutPermission_forbidden
viewCalendar_withoutPermission_forbidden
createProfile_withoutPermission_forbidden
viewOtherUserProfile_withoutPermission_forbidden
createAllocation_withoutPermission_forbidden
calculateCapacity_withoutPermission_forbidden
workspaceCrossAccess_forbidden
```

## 23.8 Seeder/event tests

```text
capacityPermissionSeeder_authoritiesExist
capacityEventSeeder_firstRun_createsDefinitions
capacityEventSeeder_secondRun_noDuplicates
defaultCalendarSeeder_workspaceCreated_createsDefault
defaultCalendarSeeder_secondRun_noDuplicate
capacityActivity_logged
capacityAudit_forFocusFactorChange_created
```

---

# 24. Manual verification checklist

Completion file must include:

```text
1. Create workspace default calendar.
2. Add Monday-Friday working day rules.
3. Add holiday exception.
4. Verify holiday capacity is zero.
5. Add special working Saturday.
6. Create user capacity profile.
7. Set focusFactor 0.75.
8. Create project allocation 50%.
9. Calculate capacity for one week.
10. Verify focused capacity formula.
11. Verify allocation formula.
12. Create overlapping allocation and verify over-allocation behavior.
13. Try assigning inactive workspace member to profile/allocation and confirm rejection.
14. Confirm events/activity/audit are created.
15. Confirm no task schedule/Gantt/cost records are created.
```

---

# 25. Acceptance criteria

Phase 12 is accepted only if:

```text
1. Current capacity/calendar implementation is classified against TO-BE.
2. Working calendar implemented/tested.
3. Day rules implemented/tested.
4. Calendar exceptions implemented/tested.
5. User capacity profile implemented/tested.
6. Focus factor validation implemented/tested.
7. Project allocation implemented/tested.
8. Allocation over-capacity behavior implemented/tested.
9. Capacity calculation returns working/focused/allocated/available capacity.
10. Authorization matrix implemented/tested.
11. Events seeded idempotently.
12. Activity/audit/outbox integration follows Phase 04.
13. Phase 12 does not falsely claim scheduling/Gantt/cost/timesheet.
14. Future phase dependencies are documented.
15. mvn compile passes.
16. mvn test passes.
17. Completion file exists and includes gap matrix.
```

Agent must not mark complete if:

```text
mvn test fails
calendar allows no working days
focusFactor <= 0 or > 1 accepted
allocationPercent <= 0 or > 100 accepted
calendar exception does not override day rule
inactive workspace member can have active capacity profile
project allocation can cross workspace
capacity calculation returns fake estimatedFinishDate
Gantt/schedule/finance/timesheet features are claimed implemented
```

---

# 26. Required phase completion file

Agent must create:

```text
docs/phase-complete/PHASE_12_RESOURCE_CALENDAR_CAPACITY_TO_BE_COMPLETE.md
```

Required sections:

```text
# Phase 12 — Resource Calendar / Capacity TO-BE Complete

## 1. Summary
## 2. Source Inputs Reviewed
## 3. Current vs TO-BE Classification Matrix
## 4. Implemented in Current BE
## 5. Implemented / Hardened in This Phase
## 6. Seed-only Items Added
## 7. Deferred Items and Target Phase
## 8. Entity Mapping
## 9. API Changes
## 10. Calendar Rules
## 11. Day Rule Rules
## 12. Exception Rules
## 13. User Capacity Profile Rules
## 14. Project Allocation Rules
## 15. Capacity Calculation Formulas
## 16. Over-allocation Policy
## 17. Authorization Matrix
## 18. Event Registry Seeder Matrix
## 19. Activity / Audit / Outbox Notes
## 20. Seed Data
## 21. Tests Added
## 22. Commands Run
## 23. Test Results
## 24. Manual Verification
## 25. Assumptions
## 26. Deviations From Prompt
## 27. Known Risks
## 28. Future Phases That Must Return to Capacity
```

---

# 27. Future phases that must return to Capacity

## 27.1 Phase 13 — Task Scheduling Engine

Must consume:

```text
working calendar
exceptions
focus factor
project allocation
task estimateHours
task dueDate
task dependencies
```

Must add:

```text
TaskSchedule
ScheduleRun
ScheduledDailyWork
estimatedStartDate
estimatedFinishDate
dueDateCapacityGap
dependency-aware scheduling
alreadyScheduledTaskHours
```

## 27.2 Phase 14 — WBS-driven Gantt

Must project scheduled tasks to Gantt.

Gantt must not directly mutate capacity tables.

## 27.3 Phase 15 — Rate Card / CCH

Must add cost roles and role capacity mapping.

Capacity hours do not imply cost until rate card exists.

## 27.4 Phase 16 — Estimation Roll-up

Must roll up effort and compare with capacity availability.

## 27.5 Phase 17 — Finance

Must combine effort/capacity with role CCH to calculate planned labor cost.

## 27.6 Phase 18 — Quote

Must use capacity and estimates to support timeline/quote feasibility.

## 27.7 Phase 20 — Project Notifications

Must notify over-allocation, due date risk, or capacity conflict.

## 27.8 Phase 21 — AI-assisted Planning

May recommend alternate assignee or allocation change.

AI must not override allocation without human approval.

## 27.9 Phase 22 — Reporting

Must add:

```text
capacity dashboard
utilization forecast
over-allocation report
project allocation report
team capacity view
```

## 27.10 Phase 23 — Core Hardening

Must review:

```text
capacity calculation performance
date range limits
calendar timezone correctness
large workspace aggregation
authorization coverage
```

## 27.11 Phase 37 — Time / Attendance / Expense

Must add actual work time and attendance.

Actuals are not Phase 12.

---

# 28. Agent anti-bịa rules

The agent must not:

```text
1. Claim task scheduling exists in Phase 12.
2. Claim estimated finish date exists unless Phase 13 schedule engine exists.
3. Claim Gantt exists.
4. Claim capacity hours are labor cost.
5. Claim utilization actuals exist without timesheet/attendance.
6. Treat estimateHours as duration.
7. Treat dueDate as computed finish date.
8. Allow inactive workspace members in active capacity profiles.
9. Allow project allocation across workspaces.
10. Ignore calendar exceptions in capacity calculation.
11. Ignore focus factor.
12. Hide over-allocation behavior.
13. Hide deferred scheduling/Gantt/finance/timesheet gaps.
```

---

# 29. Prompt to give coding agent

```text
You are implementing Phase 12 — TO-BE Resource Calendar, Working Capacity, Project Allocation & Capacity Foundation.

This is not an as-is documentation task.

Before coding, read:
- CLAUDE.md / CLAUDE.ms
- Coding_convention.md
- Phase 00 master roadmap
- Phase 01 API/security baseline
- Phase 02 IAM TO-BE spec
- Phase 03 Workspace TO-BE spec
- Phase 04 Platform Audit/Outbox/Idempotency spec
- Phase 05 Event Registry TO-BE spec
- Phase 06 Notification TO-BE spec
- Phase 07 AI Agent TO-BE spec
- Phase 08 Knowledge TO-BE spec
- Phase 09 Project Core TO-BE spec
- Phase 10 Project Authorization TO-BE spec
- Phase 11 Project Template TO-BE spec
- Current backend code, migrations, tests

Your task:
1. Compare current calendar/capacity implementation against this Phase 12 TO-BE spec.
2. Classify each capability as CURRENTLY_IMPLEMENTED, PARTIALLY_IMPLEMENTED, MUST_IMPLEMENT_IN_PHASE_12, SEED_ONLY_IN_PHASE_12, DEFERRED_TO_PHASE_XX, or NOT_IN_SCOPE_FOR_BACKEND_NOW.
3. Implement only Phase 12 required items.
4. Implement working calendars, day rules, calendar exceptions.
5. Implement user capacity profiles with focusFactor.
6. Implement project resource allocations.
7. Implement capacity calculation service for working/focused/allocated/available capacity.
8. Implement over-allocation detection/policy.
9. Add capacity permissions and event definitions.
10. Add tests listed in this spec.
11. Run mvn compile and mvn test.
12. Create docs/phase-complete/PHASE_12_RESOURCE_CALENDAR_CAPACITY_TO_BE_COMPLETE.md with full gap matrix.

Do not implement or claim task scheduling, estimated finish date, Gantt, finance/cost, timesheet actuals, or AI recommendations in this phase.
```

---

# 30. Quick tracking matrix

| Capability | Current backend | Phase 12 action | Later phase |
|---|---|---|---|
| Working calendar | Missing/unknown | Must implement | — |
| Day rules | Missing/unknown | Must implement | — |
| Calendar exceptions | Missing/unknown | Must implement | — |
| User capacity profile | Missing/unknown | Must implement | — |
| Focus factor | Missing/unknown | Must implement | — |
| Project allocation % | Missing/unknown | Must implement | — |
| Capacity calculation | Missing/unknown | Must implement | Phase 13 extends |
| Over-allocation detection | Missing/unknown | Must implement | Phase 20/22 notify/report |
| Team capacity | Missing/unknown | Optional/defer | Phase 22 |
| Time off / leave | Missing | Defer | Phase 37 |
| Skill/role capacity | Missing | Defer | Phase 15/16 |
| Task scheduling | Missing | Defer | Phase 13 |
| Gantt | Missing | Defer | Phase 14 |
| Rate/cost | Missing | Defer | Phase 15/17 |
| Utilization actuals | Missing | Defer | Phase 37 |
| AI recommendation | Missing | Defer | Phase 21 |

---

# 31. Final principle

Phase 12 is not complete when "a calendar table exists."

Phase 12 is complete when Scopery can answer:

```text
Which days does this user work?
How many focused hours does this user have?
How much of that capacity is allocated to this project?
Is this user over-allocated?
What capacity data can the scheduling engine consume later?
```

Phase 12 must not fake scheduling.

Capacity is availability.

Scheduling is placement.

Gantt is visualization.

Cost is finance.

Timesheet is actual work.
