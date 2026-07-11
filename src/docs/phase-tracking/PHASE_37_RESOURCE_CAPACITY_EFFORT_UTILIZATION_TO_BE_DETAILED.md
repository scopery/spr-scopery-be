# PHASE 37 — TO-BE Resource Capacity, Effort Forecast, Utilization, Assignment Load & Cost Input for Profitability

> Project: Scopery Backend  
> Phase: 37  
> Document type: TO-BE implementation-grade specification  
> Status: Planning / Resource intelligence and effort-cost foundation  
> Roadmap group: Post-core Project Intelligence expansion  
> Depends on: Phase 00 — Master Backend Roadmap, Phase 01 — API Path / Security Baseline, Phase 02 — IAM Core, Phase 03 — Workspace / Organization / Team, Phase 04 — Platform Audit / Outbox / Idempotency, Phase 05 — Event Registry, Phase 06 — Notification Engine, Phase 09 — Project Core / WBS / Task, Phase 10 — Project Authorization, Phase 20 — Project Events / Notifications, Phase 22 — Reporting / Dashboard / Export, Phase 23 — Core Hardening, Phase 24 — Scope / Deliverable / Acceptance, Phase 25 — RAID / Decision Management, Phase 26 — Quality / Test / Release, Phase 28 — Application / Requirement / Traceability, Phase 31 — Meetings / Collaboration, Phase 32 — Search / Productivity, Phase 34 — Governance / Versioning / Permission / Audit, Phase 35 — Advanced Notifications, Phase 36 — Revenue & Profitability Visibility  
> API base: `/api`  
> Primary module: `modules/resource`, `modules/capacity`, `modules/effort`, or `modules/project-resource` depending on repository architecture  
> Related modules: `iam`, `workspace`, `team`, `project`, `task`, `wbs`, `requirement`, `quality`, `release`, `meeting`, `raid`, `notification`, `reporting`, `governance`, `profitability`, `audit`, `eventregistry`, future `timesheet`, `calendar-integration`, `hr-integration`, `ai-planning`  
> Important rule: Phase 37 gives Scopery resource capacity, planned effort, actual/observed effort foundation, utilization, workload risk, and cost input for profitability. It is **not** payroll, HR attendance, leave management, time clock, salary management, or employee performance scoring.

---

# 0. Purpose

Phase 37 makes Scopery understand team capacity and project workload.

Phase 36 can calculate project profitability only if the system has reasonable cost input.

Cost mainly comes from:

```text
planned effort
actual/observed effort
resource capacity
resource role/rate
assignment load
overrun risk
rework effort
```

Phase 37 answers:

```text
Who is working on the project?
What role/capability do they have?
How much capacity do they have?
How much effort is planned?
How much effort is already consumed or observed?
Is anyone overloaded?
Is the project under-staffed?
Which tasks or deliverables are driving effort?
Is cost forecast increasing?
Will resource shortage delay deliverables?
How does workload affect profitability?
```

The intended experience:

```text
PM assigns people/roles to project.
System estimates workload from tasks/WBS/deliverables.
System compares planned effort vs capacity.
System detects overload/under-allocation.
System feeds cost forecast into Phase 36.
PM adjusts only when the system estimate is wrong.
```

---

# 1. Product intention

Scopery should not make PM manually manage every hour.

The system should:

```text
derive effort from task estimates
derive capacity from team/resource calendars
derive utilization from assignments
derive cost from rate card/blended cost
detect overload and risk
feed profitability forecast
```

PM should mostly see:

```text
team workload
capacity availability
overloaded resources
understaffed project
planned vs actual effort
cost impact
profitability impact
```

---

# 2. Core principle

```text
Capacity explains whether work can be done.
Effort explains how much it costs.
Utilization explains whether people are overloaded.
```

Simple formulas:

```text
Available Capacity = working hours - unavailable hours

Planned Utilization % = assigned planned hours / available capacity * 100

Actual Utilization % = observed actual hours / available capacity * 100

Remaining Effort = forecast effort - actual/observed effort

Effort Cost = effort hours * cost rate

Cost Overrun = forecast cost - baseline cost
```

Important:

```text
Resource cost is a management estimate.
It is not payroll.
It is not salary disclosure.
It is not employee performance scoring.
```

---

# 3. Source data strategy

Phase 37 should auto-derive resource and effort data from existing modules.

## 3.1 Resource sources

Resources can come from:

```text
WorkspaceMember
TeamMember
ProjectMember
ProjectRole
ExternalContact if contractor/vendor staff is modeled
Placeholder role for future staffing
```

Examples:

```text
Internal user: Alice — Developer
Team role: Backend Team
Placeholder: Senior QA needed
External contractor: Vendor Designer
```

## 3.2 Effort sources

Effort can come from:

```text
Task estimate
WBS estimate
Requirement implementation estimate
Defect rework estimate
Release support estimate
Meeting action work estimate
Manual effort adjustment
Actual/observed task effort if available
```

## 3.3 Capacity sources

Capacity can come from:

```text
workspace default calendar
team calendar
resource calendar
project allocation
manual availability exception
holiday/non-working day configuration
```

## 3.4 Cost sources

Cost can come from:

```text
RateCard from Phase 36
role blended cost rate
team blended cost rate
project default rate
resource-specific rate if allowed
manual cost override
```

---

# 4. Target statement

Phase 37 must deliver:

```text
1. Resource profile model.
2. Resource role/skill/capability metadata.
3. Capacity calendar and availability foundation.
4. Project resource allocation.
5. Task/resource assignment load.
6. Effort estimate and effort forecast.
7. Actual/observed effort foundation.
8. Utilization calculation.
9. Workload snapshot and capacity summary.
10. Resource risk flags.
11. Integration with profitability cost forecast.
12. Integration with task, WBS, requirement, defect, release, meeting actions.
13. Reporting/dashboard/export.
14. Governance, audit, notifications, permissions, tests.
```

---

# 5. Current expected backend state

Before Phase 37, backend likely has:

```text
Workspace users/members
Teams
Projects
Tasks
Task assignee maybe
Task estimates maybe
Quote/project dates
Requirement/defect/release modules
Meetings/action items
Profitability rate card foundation
Reporting/notification/governance
```

Likely missing:

```text
ResourceProfile
ResourceRole
ResourceSkill
CapacityCalendar
CapacityException
ProjectResourceAllocation
TaskResourceAssignment
EffortEstimate
EffortForecast
EffortActualRecord
WorkloadSnapshot
ResourceUtilizationSummary
ProjectCapacitySummary
ResourceRiskFlag
CapacityVariance
AssignmentConflict
ResourceCostInput
```

---

# 6. Classification labels

Use these labels in completion file:

| Label | Meaning |
|---|---|
| `CURRENTLY_IMPLEMENTED` | Existing backend already supports it with tests. |
| `PARTIALLY_IMPLEMENTED` | Some support exists but gaps remain. |
| `MUST_IMPLEMENT_IN_PHASE_37` | Required now. |
| `MUST_HARDEN_IN_PHASE_37` | Existing resource/assignment/effort behavior must be hardened now. |
| `SEED_ONLY_IN_PHASE_37` | Seed roles/skills/events/permissions/default configs only. |
| `DEFERRED_TO_PHASE_XX` | Later phase returns. |
| `DEFERRED_TO_POST_23_BACKLOG` | Later backlog item. |
| `NOT_IN_SCOPE_FOR_PHASE_37` | Explicitly not in this phase. |

---

# 7. Required capabilities

---

## 7.1 RES-001 — ResourceProfile

Classification: `MUST_IMPLEMENT_IN_PHASE_37`

Purpose:

```text
Represent a person, team capacity, or placeholder resource used for planning.
```

Resource types:

```text
INTERNAL_USER
TEAM
EXTERNAL_CONTRACTOR
VENDOR_STAFF
PLACEHOLDER_ROLE
SYSTEM_RESOURCE
```

Rules:

```text
1. ResourceProfile belongs to workspace.
2. Internal resource can link to WorkspaceMember/User.
3. External resource can link to ExternalContact if available.
4. Placeholder resource can represent needed capacity.
5. ResourceProfile is not user account.
6. ResourceProfile does not grant permission.
7. Resource status controls planning availability.
```

Status:

```text
ACTIVE
INACTIVE
ARCHIVED
```

---

## 7.2 RES-002 — ResourceRole

Classification: `MUST_IMPLEMENT_IN_PHASE_37`

Purpose:

```text
Define planning role used for staffing and cost assumptions.
```

Examples:

```text
Project Manager
Business Analyst
Backend Developer
Frontend Developer
QA Engineer
UX/UI Designer
DevOps Engineer
Solution Architect
Client Success
```

Rules:

```text
1. Role belongs to workspace.
2. Role can map to default cost rate.
3. Role can be used by placeholder resources.
4. Role is not IAM role.
5. Role does not grant permission.
```

---

## 7.3 RES-003 — ResourceSkill

Classification: `MUST_IMPLEMENT_IN_PHASE_37`

Purpose:

```text
Record skills/capabilities for planning.
```

Examples:

```text
Java
Spring Boot
React
PostgreSQL
AWS
QA Automation
Figma
Business Analysis
Project Management
```

Rules:

```text
1. Skill belongs to workspace.
2. Resource can have multiple skills.
3. Skill proficiency optional.
4. Skill does not grant permission.
5. Skill can help AI/scheduling suggestions later.
```

---

## 7.4 CAL-001 — CapacityCalendar

Classification: `MUST_IMPLEMENT_IN_PHASE_37`

Purpose:

```text
Define default working capacity.
```

Calendar scopes:

```text
WORKSPACE_DEFAULT
TEAM
RESOURCE
PROJECT
```

Fields:

```text
timezone
working days
hours per day
start/end working time optional
calendar status
```

Rules:

```text
1. Workspace default calendar required.
2. Resource calendar can override workspace/team default.
3. Calendar affects capacity calculation.
4. Calendar does not enforce attendance.
```

---

## 7.5 CAL-002 — CapacityException

Classification: `MUST_IMPLEMENT_IN_PHASE_37`

Purpose:

```text
Represent non-standard availability for planning.
```

Exception types:

```text
NON_WORKING_DAY
REDUCED_CAPACITY
EXTRA_CAPACITY
PROJECT_BLOCKED
MANUAL_UNAVAILABLE
HOLIDAY
```

Rules:

```text
1. Exception belongs to calendar/resource/team/project.
2. Exception changes capacity calculation.
3. Exception is not HR leave approval.
4. Exception changes audited if sensitive.
```

---

## 7.6 ALLOC-001 — ProjectResourceAllocation

Classification: `MUST_IMPLEMENT_IN_PHASE_37`

Purpose:

```text
Allocate resource capacity to a project over a time range.
```

Allocation types:

```text
PERCENTAGE
HOURS_PER_WEEK
HOURS_TOTAL
ROLE_PLACEHOLDER
```

Rules:

```text
1. Allocation belongs to project and resource.
2. Allocation has start/end date.
3. Allocation can be planned or active.
4. Allocation contributes to workload/utilization.
5. Allocation does not grant project access.
6. Allocation can feed profitability cost forecast.
```

Status:

```text
DRAFT
PLANNED
ACTIVE
COMPLETED
CANCELLED
ARCHIVED
```

---

## 7.7 ASSIGN-001 — TaskResourceAssignment

Classification: `MUST_IMPLEMENT_IN_PHASE_37`

Purpose:

```text
Assign planned work effort to resources.
```

Assignment types:

```text
OWNER
PRIMARY
SUPPORT
REVIEWER
QA
APPROVER_LABEL_ONLY
```

Important:

```text
APPROVER_LABEL_ONLY is just responsibility label, not workflow approval.
```

Rules:

```text
1. Assignment links task/work item to resource.
2. Assignment can include planned effort.
3. Assignment does not grant access.
4. Task assignee and ResourceAssignment can be mapped but not assumed same.
5. Assignment contributes to workload forecast.
```

---

## 7.8 EFF-001 — EffortEstimate

Classification: `MUST_IMPLEMENT_IN_PHASE_37`

Purpose:

```text
Record estimated effort for work items.
```

Supported targets:

```text
PROJECT
WBS_ITEM
TASK
REQUIREMENT
DEFECT
RELEASE_PACKAGE
MEETING_ACTION_ITEM
DELIVERABLE
```

Estimate types:

```text
INITIAL
REVISED
REMAINING
REWORK
MANUAL_ADJUSTMENT
SYSTEM_DERIVED
```

Rules:

```text
1. Estimate belongs to target object.
2. Estimate uses hours as canonical unit.
3. Estimate can reference resource role.
4. Estimate changes audited if material.
5. Revised estimate can trigger cost/profitability recalculation.
```

---

## 7.9 EFF-002 — EffortForecast

Classification: `MUST_IMPLEMENT_IN_PHASE_37`

Purpose:

```text
Forecast total effort needed to complete work/project.
```

Forecast dimensions:

```text
project
phase
deliverable
role
resource
time period
```

Rules:

```text
1. Forecast derived from estimates, assignments, actual/observed effort, and remaining work.
2. Forecast can be rebuilt.
3. Forecast should feed Phase 36 CostForecast.
4. Forecast is management estimate, not payroll.
```

---

## 7.10 EFF-003 — Actual / Observed Effort foundation

Classification: `MUST_IMPLEMENT_IN_PHASE_37`

Purpose:

```text
Record actual or observed effort when available.
```

Input modes:

```text
TASK_PROGRESS_MANUAL
WORK_LOG_LIGHT
MEETING_ACTION_UPDATE
SYSTEM_OBSERVED
IMPORT_FUTURE
```

Rules:

```text
1. Actual effort entry is lightweight.
2. This is not a timesheet/payroll module.
3. Actual effort can update remaining effort.
4. Actual effort can feed utilization and cost forecast.
5. Actor, date, source, and reason captured.
```

---

## 7.11 UTIL-001 — ResourceUtilizationSummary

Classification: `MUST_IMPLEMENT_IN_PHASE_37`

Purpose:

```text
Show how loaded a resource is.
```

Metrics:

```text
availableCapacityHours
allocatedHours
assignedEffortHours
actualObservedHours
remainingEffortHours
plannedUtilizationPercent
actualUtilizationPercent
overloadHours
underAllocationHours
```

Rules:

```text
1. Summary can be calculated per resource/date range.
2. Summary should support dashboard queries.
3. Overload threshold configurable.
4. Summary respects permissions.
```

---

## 7.12 UTIL-002 — ProjectCapacitySummary

Classification: `MUST_IMPLEMENT_IN_PHASE_37`

Purpose:

```text
Show whether project has enough capacity to complete planned work.
```

Metrics:

```text
projectRequiredEffort
projectAllocatedCapacity
capacityGap
overAllocatedResources
underAllocatedRoles
remainingEffort
forecastCompletionRisk
costForecastInput
```

Rules:

```text
1. Summary belongs to project.
2. Summary derived from allocations, estimates, forecasts.
3. Capacity gap can trigger risk flag.
4. Summary feeds profitability risk/cost.
```

---

## 7.13 SNAP-001 — WorkloadSnapshot

Classification: `MUST_IMPLEMENT_IN_PHASE_37`

Purpose:

```text
Immutable timestamped view of workload/capacity state.
```

Snapshot metrics:

```text
totalCapacity
totalAllocated
totalEstimatedEffort
totalForecastEffort
totalActualObservedEffort
capacityGap
overloadCount
understaffedRoleCount
costForecastInput
```

Rules:

```text
1. Snapshot immutable.
2. Snapshot can feed reports.
3. Snapshot can trigger notifications/risk if material change.
```

---

## 7.14 RISK-001 — ResourceRiskFlag

Classification: `MUST_IMPLEMENT_IN_PHASE_37`

Purpose:

```text
Flag capacity/workload risk.
```

Risk reasons:

```text
RESOURCE_OVERLOADED
ROLE_UNDERSTAFFED
CAPACITY_GAP
EFFORT_OVERRUN
MISSING_ESTIMATE
MISSING_RATE
KEY_RESOURCE_UNAVAILABLE
SCHEDULE_COMPRESSION
DEFECT_REWORK_LOAD
MANUAL_RISK
```

Impact types:

```text
DELIVERY_RISK
COST_RISK
PROFITABILITY_RISK
QUALITY_RISK
```

Rules:

```text
1. Risk flag can be system-generated or manual.
2. Risk flag can link to project/task/resource/deliverable/defect.
3. Risk flag can feed Phase 36 ProfitabilityRiskFlag.
4. Risk flag does not mutate source object.
```

Status:

```text
OPEN
MITIGATED
CLOSED
ARCHIVED
```

---

## 7.15 CONFLICT-001 — AssignmentConflict

Classification: `MUST_IMPLEMENT_IN_PHASE_37`

Purpose:

```text
Detect conflicting resource allocations.
```

Conflict types:

```text
OVER_ALLOCATION
DATE_OVERLAP
ROLE_MISMATCH
SKILL_MISMATCH
CAPACITY_UNAVAILABLE
MISSING_RATE
MISSING_ESTIMATE
```

Rules:

```text
1. Conflict generated by recalculation.
2. Conflict can be acknowledged/resolved.
3. Conflict does not block assignment by default unless policy says.
4. Conflict can trigger notification.
```

---

## 7.16 COST-001 — ResourceCostInput

Classification: `MUST_IMPLEMENT_IN_PHASE_37`

Purpose:

```text
Produce cost inputs for Phase 36 profitability.
```

Cost input sources:

```text
effort forecast * role rate
assignment hours * resource/team rate
actual observed effort * rate
manual cost source
rework estimate * rate
```

Rules:

```text
1. Cost input should be traceable to effort/resource/rate source.
2. Cost values sensitive.
3. Cost input update triggers Phase 36 cost forecast rebuild.
4. Cost input is management estimate.
```

---

## 7.17 AUTO-001 — Automatic recalculation

Classification: `MUST_IMPLEMENT_IN_PHASE_37`

Trigger recalculation on:

```text
task estimate changed
task assignment changed
resource allocation changed
capacity calendar changed
capacity exception changed
actual/observed effort recorded
rate card changed
defect rework estimate changed
deliverable date changed
project date changed
risk closed/opened
```

Rules:

```text
1. Recalculation job idempotent.
2. Recalculation updates summaries.
3. Recalculation can create snapshot if material change.
4. Recalculation can emit risk events.
```

---

## 7.18 THRESH-001 — Utilization threshold policy

Classification: `MUST_IMPLEMENT_IN_PHASE_37`

Default thresholds:

```text
UNDER_ALLOCATED < 50%
HEALTHY 50% - 85%
WATCH 85% - 100%
OVERLOADED > 100%
CRITICAL_OVERLOAD > 120%
```

Rules:

```text
1. Workspace default thresholds configurable.
2. Project override allowed by permission.
3. Threshold changes audited.
4. Threshold updates can recalculate utilization status.
```

---

## 7.19 NOTIF-001 — Resource/capacity notifications

Classification: `MUST_IMPLEMENT_IN_PHASE_37` if Phase 35 exists.

Notifications:

```text
resource overloaded
project capacity gap detected
missing estimate
missing rate
key resource unavailable
effort forecast increased materially
cost input increased materially
assignment conflict detected
```

Rules:

```text
1. Notify PM/resource owner/project owner based on policy.
2. Sensitive cost values masked.
3. Notification does not grant access.
```

---

## 7.20 AI-001 — AI-assisted resource planning suggestions

Classification: `SEED_ONLY_IN_PHASE_37` or `MUST_IMPLEMENT_IN_PHASE_37` if Phase 21 exists.

AI can suggest:

```text
which role is underallocated
which resource is overloaded
how to rebalance assignments
why effort forecast increased
which tasks caused cost overrun
staffing recommendation draft
```

Rules:

```text
1. AI output is suggestion only.
2. AI cannot assign resources automatically.
3. AI cannot change cost/rate values.
4. AI cannot expose sensitive cost data.
```

---

# 8. Entity model TO-BE

---

## 8.1 ResourceProfile — `resource_profile`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
linked_user_id UUID NULL
linked_workspace_member_id UUID NULL
linked_team_id UUID NULL
linked_external_contact_id UUID NULL
resource_type VARCHAR(50) NOT NULL
display_name VARCHAR(255) NOT NULL
primary_role_id UUID NULL
default_calendar_id UUID NULL
default_rate_card_id UUID NULL
timezone VARCHAR(100) NULL
status VARCHAR(50) NOT NULL
created_at / created_by
updated_at / updated_by
archived_at / archived_by
trace_id VARCHAR(100) NULL
version INT
```

Constraint:

```text
unique workspace_id + linked_user_id where linked_user_id not null
```

---

## 8.2 ResourceRole — `resource_role`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
role_code VARCHAR(150) NOT NULL
name VARCHAR(255) NOT NULL
description TEXT NULL
default_rate_card_id UUID NULL
status VARCHAR(50) NOT NULL
created_at / created_by
updated_at / updated_by
archived_at / archived_by
version INT
```

Constraint:

```text
unique workspace_id + role_code
```

---

## 8.3 ResourceSkill — `resource_skill`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
skill_code VARCHAR(150) NOT NULL
name VARCHAR(255) NOT NULL
description TEXT NULL
status VARCHAR(50) NOT NULL
created_at / created_by
updated_at / updated_by
archived_at / archived_by
version INT
```

Constraint:

```text
unique workspace_id + skill_code
```

---

## 8.4 ResourceSkillAssignment — `resource_skill_assignment`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
resource_profile_id UUID NOT NULL
resource_skill_id UUID NOT NULL
proficiency_level VARCHAR(50) NULL
created_at / created_by
updated_at / updated_by
version INT
```

Constraint:

```text
unique resource_profile_id + resource_skill_id
```

---

## 8.5 CapacityCalendar — `resource_capacity_calendar`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
project_id UUID NULL
resource_profile_id UUID NULL
team_id UUID NULL
calendar_scope VARCHAR(50) NOT NULL
name VARCHAR(255) NOT NULL
timezone VARCHAR(100) NOT NULL
working_days_json JSONB NOT NULL
hours_per_day DECIMAL(9,4) NOT NULL
start_time VARCHAR(20) NULL
end_time VARCHAR(20) NULL
status VARCHAR(50) NOT NULL
created_at / created_by
updated_at / updated_by
archived_at / archived_by
version INT
```

---

## 8.6 CapacityException — `resource_capacity_exception`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
capacity_calendar_id UUID NULL
resource_profile_id UUID NULL
team_id UUID NULL
project_id UUID NULL
exception_type VARCHAR(50) NOT NULL
start_date DATE NOT NULL
end_date DATE NOT NULL
capacity_hours_delta DECIMAL(9,4) NULL
reason TEXT NULL
status VARCHAR(50) NOT NULL
created_at / created_by
updated_at / updated_by
archived_at / archived_by
trace_id VARCHAR(100) NULL
version INT
```

---

## 8.7 ProjectResourceAllocation — `resource_project_allocation`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
project_id UUID NOT NULL
resource_profile_id UUID NOT NULL
resource_role_id UUID NULL
allocation_type VARCHAR(50) NOT NULL
allocation_percent DECIMAL(9,4) NULL
hours_per_week DECIMAL(9,4) NULL
total_hours DECIMAL(19,4) NULL
start_date DATE NOT NULL
end_date DATE NULL
status VARCHAR(50) NOT NULL
notes TEXT NULL
created_at / created_by
updated_at / updated_by
cancelled_at / cancelled_by
cancellation_reason TEXT NULL
trace_id VARCHAR(100) NULL
version INT
```

---

## 8.8 TaskResourceAssignment — `resource_task_assignment`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
project_id UUID NOT NULL
task_id UUID NOT NULL
resource_profile_id UUID NOT NULL
assignment_type VARCHAR(50) NOT NULL
planned_effort_hours DECIMAL(19,4) NULL
start_date DATE NULL
end_date DATE NULL
status VARCHAR(50) NOT NULL
notes TEXT NULL
created_at / created_by
updated_at / updated_by
removed_at / removed_by
version INT
```

Constraint:

```text
unique active task_id + resource_profile_id + assignment_type
```

---

## 8.9 EffortEstimate — `resource_effort_estimate`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
project_id UUID NULL
target_type VARCHAR(100) NOT NULL
target_id UUID NOT NULL
resource_role_id UUID NULL
resource_profile_id UUID NULL
estimate_type VARCHAR(50) NOT NULL
effort_hours DECIMAL(19,4) NOT NULL
confidence_percent DECIMAL(5,2) NULL
reason TEXT NULL
status VARCHAR(50) NOT NULL
created_at / created_by
updated_at / updated_by
archived_at / archived_by
trace_id VARCHAR(100) NULL
version INT
```

---

## 8.10 EffortForecast — `resource_effort_forecast`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
project_id UUID NOT NULL
resource_profile_id UUID NULL
resource_role_id UUID NULL
forecast_type VARCHAR(50) NOT NULL
period_start DATE NULL
period_end DATE NULL
forecast_effort_hours DECIMAL(19,4) NOT NULL
remaining_effort_hours DECIMAL(19,4) NULL
actual_observed_effort_hours DECIMAL(19,4) NULL
confidence_percent DECIMAL(5,2) NULL
generated_by VARCHAR(50) NOT NULL
status VARCHAR(50) NOT NULL
created_at / created_by
archived_at / archived_by
trace_id VARCHAR(100) NULL
version INT
```

---

## 8.11 ActualEffortRecord — `resource_actual_effort_record`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
project_id UUID NOT NULL
resource_profile_id UUID NULL
target_type VARCHAR(100) NOT NULL
target_id UUID NOT NULL
effort_date DATE NOT NULL
effort_hours DECIMAL(19,4) NOT NULL
input_mode VARCHAR(50) NOT NULL
description TEXT NULL
status VARCHAR(50) NOT NULL
created_at / created_by
updated_at / updated_by
cancelled_at / cancelled_by
cancellation_reason TEXT NULL
trace_id VARCHAR(100) NULL
version INT
```

---

## 8.12 ResourceUtilizationSummary — `resource_utilization_summary`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
resource_profile_id UUID NOT NULL
period_start DATE NOT NULL
period_end DATE NOT NULL
available_capacity_hours DECIMAL(19,4) NOT NULL DEFAULT 0
allocated_hours DECIMAL(19,4) NOT NULL DEFAULT 0
assigned_effort_hours DECIMAL(19,4) NOT NULL DEFAULT 0
actual_observed_hours DECIMAL(19,4) NOT NULL DEFAULT 0
remaining_effort_hours DECIMAL(19,4) NOT NULL DEFAULT 0
planned_utilization_percent DECIMAL(9,4) NULL
actual_utilization_percent DECIMAL(9,4) NULL
overload_hours DECIMAL(19,4) NOT NULL DEFAULT 0
under_allocation_hours DECIMAL(19,4) NOT NULL DEFAULT 0
utilization_status VARCHAR(50) NOT NULL
updated_at TIMESTAMP NOT NULL
version INT
```

---

## 8.13 ProjectCapacitySummary — `resource_project_capacity_summary`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
project_id UUID NOT NULL
period_start DATE NULL
period_end DATE NULL
project_required_effort_hours DECIMAL(19,4) NOT NULL DEFAULT 0
project_allocated_capacity_hours DECIMAL(19,4) NOT NULL DEFAULT 0
capacity_gap_hours DECIMAL(19,4) NOT NULL DEFAULT 0
over_allocated_resource_count INT NOT NULL DEFAULT 0
under_allocated_role_count INT NOT NULL DEFAULT 0
remaining_effort_hours DECIMAL(19,4) NOT NULL DEFAULT 0
forecast_completion_risk VARCHAR(50) NULL
cost_forecast_input DECIMAL(19,4) NULL
currency VARCHAR(10) NULL
updated_at TIMESTAMP NOT NULL
version INT
```

Constraint:

```text
unique workspace_id + project_id + period_start + period_end
```

---

## 8.14 WorkloadSnapshot — `resource_workload_snapshot`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
project_id UUID NULL
resource_profile_id UUID NULL
period_start DATE NULL
period_end DATE NULL
total_capacity_hours DECIMAL(19,4) NOT NULL DEFAULT 0
total_allocated_hours DECIMAL(19,4) NOT NULL DEFAULT 0
total_estimated_effort_hours DECIMAL(19,4) NOT NULL DEFAULT 0
total_forecast_effort_hours DECIMAL(19,4) NOT NULL DEFAULT 0
total_actual_observed_effort_hours DECIMAL(19,4) NOT NULL DEFAULT 0
capacity_gap_hours DECIMAL(19,4) NOT NULL DEFAULT 0
overload_count INT NOT NULL DEFAULT 0
understaffed_role_count INT NOT NULL DEFAULT 0
cost_forecast_input DECIMAL(19,4) NULL
currency VARCHAR(10) NULL
snapshot_source VARCHAR(100) NOT NULL
snapshot_at TIMESTAMP NOT NULL
created_at TIMESTAMP NOT NULL
trace_id VARCHAR(100) NULL
version INT
```

---

## 8.15 ResourceRiskFlag — `resource_risk_flag`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
project_id UUID NULL
resource_profile_id UUID NULL
risk_reason VARCHAR(100) NOT NULL
impact_type VARCHAR(50) NOT NULL
source_type VARCHAR(100) NULL
source_id UUID NULL
impact_effort_hours DECIMAL(19,4) NULL
impact_cost_amount DECIMAL(19,4) NULL
currency VARCHAR(10) NULL
description TEXT NULL
status VARCHAR(50) NOT NULL
created_at / created_by
mitigated_at / mitigated_by
closed_at / closed_by
trace_id VARCHAR(100) NULL
version INT
```

---

## 8.16 AssignmentConflict — `resource_assignment_conflict`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
project_id UUID NULL
resource_profile_id UUID NULL
conflict_type VARCHAR(100) NOT NULL
source_type VARCHAR(100) NULL
source_id UUID NULL
severity VARCHAR(50) NOT NULL
description TEXT NULL
status VARCHAR(50) NOT NULL
detected_at TIMESTAMP NOT NULL
acknowledged_at TIMESTAMP NULL
acknowledged_by UUID NULL
resolved_at TIMESTAMP NULL
resolved_by UUID NULL
trace_id VARCHAR(100) NULL
version INT
```

Status:

```text
OPEN
ACKNOWLEDGED
RESOLVED
DISMISSED
ARCHIVED
```

---

## 8.17 ResourceCostInput — `resource_cost_input`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
project_id UUID NOT NULL
resource_profile_id UUID NULL
resource_role_id UUID NULL
source_type VARCHAR(100) NOT NULL
source_id UUID NULL
effort_hours DECIMAL(19,4) NOT NULL
rate_amount DECIMAL(19,4) NOT NULL
currency VARCHAR(10) NOT NULL
cost_amount DECIMAL(19,4) NOT NULL
status VARCHAR(50) NOT NULL
created_at / created_by
updated_at / updated_by
trace_id VARCHAR(100) NULL
version INT
```

---

## 8.18 UtilizationThresholdPolicy — `resource_utilization_threshold_policy`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
project_id UUID NULL
under_allocated_percent DECIMAL(9,4) NOT NULL
healthy_min_percent DECIMAL(9,4) NOT NULL
healthy_max_percent DECIMAL(9,4) NOT NULL
watch_max_percent DECIMAL(9,4) NOT NULL
overloaded_percent DECIMAL(9,4) NOT NULL
critical_overload_percent DECIMAL(9,4) NOT NULL
enabled BOOLEAN NOT NULL DEFAULT true
created_at / created_by
updated_at / updated_by
version INT
```

---

# 9. API TO-BE list

All APIs use `/api`.

---

## 9.1 Resource profile APIs

```text
POST  /api/workspaces/{workspaceId}/resources
GET   /api/workspaces/{workspaceId}/resources
GET   /api/workspaces/{workspaceId}/resources/{resourceId}
PUT   /api/workspaces/{workspaceId}/resources/{resourceId}
PATCH /api/workspaces/{workspaceId}/resources/{resourceId}/archive
POST  /api/workspaces/{workspaceId}/resources/sync-from-members
```

---

## 9.2 Resource role / skill APIs

```text
POST  /api/workspaces/{workspaceId}/resources/roles
GET   /api/workspaces/{workspaceId}/resources/roles
PUT   /api/workspaces/{workspaceId}/resources/roles/{roleId}
PATCH /api/workspaces/{workspaceId}/resources/roles/{roleId}/archive

POST  /api/workspaces/{workspaceId}/resources/skills
GET   /api/workspaces/{workspaceId}/resources/skills
PUT   /api/workspaces/{workspaceId}/resources/skills/{skillId}
PATCH /api/workspaces/{workspaceId}/resources/skills/{skillId}/archive

POST   /api/workspaces/{workspaceId}/resources/{resourceId}/skills
DELETE /api/workspaces/{workspaceId}/resources/{resourceId}/skills/{skillId}
```

---

## 9.3 Capacity calendar APIs

```text
POST  /api/workspaces/{workspaceId}/resources/capacity-calendars
GET   /api/workspaces/{workspaceId}/resources/capacity-calendars
GET   /api/workspaces/{workspaceId}/resources/capacity-calendars/{calendarId}
PUT   /api/workspaces/{workspaceId}/resources/capacity-calendars/{calendarId}
PATCH /api/workspaces/{workspaceId}/resources/capacity-calendars/{calendarId}/archive

POST  /api/workspaces/{workspaceId}/resources/capacity-exceptions
GET   /api/workspaces/{workspaceId}/resources/capacity-exceptions
PUT   /api/workspaces/{workspaceId}/resources/capacity-exceptions/{exceptionId}
PATCH /api/workspaces/{workspaceId}/resources/capacity-exceptions/{exceptionId}/archive
```

---

## 9.4 Project allocation APIs

```text
POST  /api/projects/{projectId}/resources/allocations
GET   /api/projects/{projectId}/resources/allocations
GET   /api/projects/{projectId}/resources/allocations/{allocationId}
PUT   /api/projects/{projectId}/resources/allocations/{allocationId}
POST  /api/projects/{projectId}/resources/allocations/{allocationId}/activate
POST  /api/projects/{projectId}/resources/allocations/{allocationId}/complete
POST  /api/projects/{projectId}/resources/allocations/{allocationId}/cancel
PATCH /api/projects/{projectId}/resources/allocations/{allocationId}/archive
```

---

## 9.5 Task resource assignment APIs

```text
POST   /api/projects/{projectId}/tasks/{taskId}/resource-assignments
GET    /api/projects/{projectId}/tasks/{taskId}/resource-assignments
PUT    /api/projects/{projectId}/tasks/{taskId}/resource-assignments/{assignmentId}
DELETE /api/projects/{projectId}/tasks/{taskId}/resource-assignments/{assignmentId}
```

Project-level assignment list:

```text
GET /api/projects/{projectId}/resources/task-assignments
```

---

## 9.6 Effort estimate / forecast APIs

```text
POST  /api/projects/{projectId}/resources/effort-estimates
GET   /api/projects/{projectId}/resources/effort-estimates
GET   /api/projects/{projectId}/resources/effort-estimates/{estimateId}
PUT   /api/projects/{projectId}/resources/effort-estimates/{estimateId}
PATCH /api/projects/{projectId}/resources/effort-estimates/{estimateId}/archive

POST /api/projects/{projectId}/resources/effort-forecasts/rebuild
GET  /api/projects/{projectId}/resources/effort-forecasts
GET  /api/projects/{projectId}/resources/effort-forecasts/{forecastId}
```

---

## 9.7 Actual / observed effort APIs

```text
POST  /api/projects/{projectId}/resources/actual-effort-records
GET   /api/projects/{projectId}/resources/actual-effort-records
GET   /api/projects/{projectId}/resources/actual-effort-records/{recordId}
PUT   /api/projects/{projectId}/resources/actual-effort-records/{recordId}
POST  /api/projects/{projectId}/resources/actual-effort-records/{recordId}/cancel
```

---

## 9.8 Utilization / capacity summary APIs

```text
GET  /api/workspaces/{workspaceId}/resources/{resourceId}/utilization
POST /api/workspaces/{workspaceId}/resources/{resourceId}/utilization/rebuild

GET  /api/projects/{projectId}/resources/capacity-summary
POST /api/projects/{projectId}/resources/capacity-summary/rebuild

GET  /api/projects/{projectId}/resources/workload-snapshots
POST /api/projects/{projectId}/resources/workload-snapshots
```

---

## 9.9 Resource risk / conflict APIs

```text
POST /api/projects/{projectId}/resources/risk-flags
GET  /api/projects/{projectId}/resources/risk-flags
POST /api/projects/{projectId}/resources/risk-flags/{riskFlagId}/mitigate
POST /api/projects/{projectId}/resources/risk-flags/{riskFlagId}/close

GET  /api/projects/{projectId}/resources/assignment-conflicts
POST /api/projects/{projectId}/resources/assignment-conflicts/{conflictId}/acknowledge
POST /api/projects/{projectId}/resources/assignment-conflicts/{conflictId}/resolve
POST /api/projects/{projectId}/resources/conflicts/recalculate
```

---

## 9.10 Resource cost input APIs

```text
GET  /api/projects/{projectId}/resources/cost-inputs
POST /api/projects/{projectId}/resources/cost-inputs/rebuild
```

These cost inputs feed Phase 36 profitability.

---

## 9.11 Threshold policy APIs

```text
GET /api/workspaces/{workspaceId}/resources/utilization-threshold-policy
PUT /api/workspaces/{workspaceId}/resources/utilization-threshold-policy

GET /api/projects/{projectId}/resources/utilization-threshold-policy
PUT /api/projects/{projectId}/resources/utilization-threshold-policy
```

---

## 9.12 Reports APIs

```text
GET /api/workspaces/{workspaceId}/reports/resources/utilization
GET /api/workspaces/{workspaceId}/reports/resources/capacity
GET /api/workspaces/{workspaceId}/reports/resources/overload
GET /api/workspaces/{workspaceId}/reports/resources/underallocation
GET /api/workspaces/{workspaceId}/reports/resources/effort-forecast
GET /api/projects/{projectId}/reports/resources/project-capacity
GET /api/projects/{projectId}/reports/resources/workload
GET /api/projects/{projectId}/reports/resources/effort-vs-capacity
GET /api/projects/{projectId}/reports/resources/cost-input
```

---

# 10. Authorization requirements

Required authorities:

```text
RESOURCE_PROFILE_VIEW
RESOURCE_PROFILE_CREATE
RESOURCE_PROFILE_UPDATE
RESOURCE_PROFILE_ARCHIVE
RESOURCE_PROFILE_SYNC

RESOURCE_ROLE_VIEW
RESOURCE_ROLE_MANAGE
RESOURCE_SKILL_VIEW
RESOURCE_SKILL_MANAGE

CAPACITY_CALENDAR_VIEW
CAPACITY_CALENDAR_MANAGE
CAPACITY_EXCEPTION_VIEW
CAPACITY_EXCEPTION_MANAGE

PROJECT_RESOURCE_ALLOCATION_VIEW
PROJECT_RESOURCE_ALLOCATION_CREATE
PROJECT_RESOURCE_ALLOCATION_UPDATE
PROJECT_RESOURCE_ALLOCATION_CANCEL

TASK_RESOURCE_ASSIGNMENT_VIEW
TASK_RESOURCE_ASSIGNMENT_CREATE
TASK_RESOURCE_ASSIGNMENT_UPDATE
TASK_RESOURCE_ASSIGNMENT_DELETE

EFFORT_ESTIMATE_VIEW
EFFORT_ESTIMATE_CREATE
EFFORT_ESTIMATE_UPDATE
EFFORT_ESTIMATE_ARCHIVE
EFFORT_FORECAST_VIEW
EFFORT_FORECAST_REBUILD

ACTUAL_EFFORT_VIEW
ACTUAL_EFFORT_CREATE
ACTUAL_EFFORT_UPDATE
ACTUAL_EFFORT_CANCEL

RESOURCE_UTILIZATION_VIEW
RESOURCE_UTILIZATION_REBUILD
PROJECT_CAPACITY_VIEW
PROJECT_CAPACITY_REBUILD
WORKLOAD_SNAPSHOT_VIEW
WORKLOAD_SNAPSHOT_CREATE

RESOURCE_RISK_VIEW
RESOURCE_RISK_CREATE
RESOURCE_RISK_UPDATE
ASSIGNMENT_CONFLICT_VIEW
ASSIGNMENT_CONFLICT_UPDATE
ASSIGNMENT_CONFLICT_RECALCULATE

RESOURCE_COST_INPUT_VIEW
RESOURCE_COST_INPUT_REBUILD
RESOURCE_COST_SENSITIVE_VIEW

UTILIZATION_THRESHOLD_VIEW
UTILIZATION_THRESHOLD_MANAGE

RESOURCE_REPORT_VIEW
RESOURCE_AUDIT_VIEW
```

Rules:

```text
1. Resource profile visibility depends on workspace membership and role.
2. Cost/rate data requires sensitive permission.
3. Project allocation requires project access.
4. Assignment does not grant project access.
5. Effort/capacity reports respect project/workspace permissions.
6. External resources do not become users.
```

---

# 11. Lifecycle rules

## 11.1 ResourceProfile lifecycle

```text
ACTIVE → INACTIVE
ACTIVE/INACTIVE → ARCHIVED
```

## 11.2 Allocation lifecycle

```text
DRAFT → PLANNED → ACTIVE → COMPLETED
DRAFT/PLANNED/ACTIVE → CANCELLED
Any → ARCHIVED
```

## 11.3 Assignment lifecycle

```text
ACTIVE → REMOVED
ACTIVE → COMPLETED
```

## 11.4 Estimate lifecycle

```text
DRAFT → ACTIVE
ACTIVE → SUPERSEDED
Any → ARCHIVED
```

## 11.5 Actual effort lifecycle

```text
RECORDED → UPDATED
RECORDED/UPDATED → CANCELLED
```

## 11.6 Risk lifecycle

```text
OPEN → MITIGATED → CLOSED
OPEN → CLOSED
Any → ARCHIVED
```

## 11.7 Conflict lifecycle

```text
OPEN → ACKNOWLEDGED → RESOLVED
OPEN → DISMISSED
Any → ARCHIVED
```

---

# 12. Calculation rules

## 12.1 Capacity

```text
available_capacity_hours =
  working_days_in_period * hours_per_day
  + extra_capacity
  - reduced_capacity
  - non_working_capacity
```

Rules:

```text
1. Use calendar timezone.
2. Date range inclusive/exclusive must be documented.
3. CapacityException overrides base calendar.
4. Missing calendar falls back to workspace default.
```

## 12.2 Allocation hours

For percentage allocation:

```text
allocated_hours = available_capacity_hours * allocation_percent / 100
```

For weekly allocation:

```text
allocated_hours = hours_per_week * number_of_weeks_in_period
```

For total allocation:

```text
allocated_hours = total_hours within allocation period
```

## 12.3 Utilization

```text
planned_utilization_percent =
  assigned_effort_hours / available_capacity_hours * 100

allocation_utilization_percent =
  allocated_hours / available_capacity_hours * 100

actual_utilization_percent =
  actual_observed_hours / available_capacity_hours * 100
```

Rules:

```text
If capacity is 0, utilization is null or special status UNAVAILABLE.
```

## 12.4 Effort forecast

```text
forecast_effort =
  active estimates
+ rework estimates
+ manual adjustments
- completed/actualized effort if policy
```

## 12.5 Cost input

```text
cost_amount = effort_hours * applicable_rate
```

Rate priority:

```text
resource-specific rate
project role rate
team rate
project default rate
workspace default rate
```

Rules:

```text
1. Missing rate creates MISSING_RATE risk/conflict.
2. Cost values sensitive.
3. Use BigDecimal/decimal precision.
```

---

# 13. Integration rules

## 13.1 Project/task integration

Rules:

```text
1. Task estimate can create EffortEstimate.
2. Task assignee can sync to ResourceAssignment if mapped.
3. Task status changes can update remaining effort if policy.
4. Task date changes can trigger conflict recalculation.
```

## 13.2 WBS/deliverable integration

Rules:

```text
1. WBS item can aggregate effort estimates.
2. Deliverable can aggregate task/resource effort.
3. Deliverable delay can create resource risk.
```

## 13.3 Requirement integration

Rules:

```text
1. Requirement can have implementation effort estimate.
2. Requirement priority/complexity can influence forecast.
3. Requirement change can update effort forecast.
```

## 13.4 Quality/defect integration

Rules:

```text
1. Defect can create rework effort estimate.
2. High severity defect can increase effort forecast.
3. Rework effort feeds profitability cost.
```

## 13.5 Release integration

Rules:

```text
1. Release support effort can be estimated.
2. Release delay can create resource risk.
3. Release readiness issues can affect workload.
```

## 13.6 Meeting/collaboration integration

Rules:

```text
1. Meeting action item can have effort estimate.
2. Meeting assignment can create light workload item.
3. Meeting time itself can be tracked only if needed and policy allows.
```

## 13.7 Profitability integration

Rules:

```text
1. EffortForecast and ResourceCostInput feed Phase 36 CostForecast.
2. ResourceRiskFlag can create ProfitabilityRiskFlag.
3. Cost input changes trigger profitability summary rebuild.
4. Cost values masked by permission.
```

## 13.8 Governance integration

Rules:

```text
1. WorkloadSnapshot immutable.
2. Manual effort/cost adjustments audited.
3. Resource/rate changes audited if sensitive.
4. Historical snapshots not rewritten by rate change.
```

## 13.9 Notification integration

Rules:

```text
1. Overload/risk/conflict can notify PM.
2. Missing rate/estimate can notify project owner.
3. Material cost input increase can notify profitability owner.
```

---

# 14. Event Registry integration

Recommended source system:

```text
SCOPERY_RESOURCE
```

Required events:

```text
RESOURCE_PROFILE_CREATED
RESOURCE_PROFILE_UPDATED
RESOURCE_PROFILE_ARCHIVED
RESOURCE_PROFILE_SYNCED_FROM_MEMBER

RESOURCE_ROLE_CREATED
RESOURCE_ROLE_UPDATED
RESOURCE_ROLE_ARCHIVED
RESOURCE_SKILL_CREATED
RESOURCE_SKILL_UPDATED
RESOURCE_SKILL_ARCHIVED
RESOURCE_SKILL_ASSIGNED
RESOURCE_SKILL_REMOVED

CAPACITY_CALENDAR_CREATED
CAPACITY_CALENDAR_UPDATED
CAPACITY_CALENDAR_ARCHIVED
CAPACITY_EXCEPTION_CREATED
CAPACITY_EXCEPTION_UPDATED
CAPACITY_EXCEPTION_ARCHIVED

PROJECT_RESOURCE_ALLOCATION_CREATED
PROJECT_RESOURCE_ALLOCATION_UPDATED
PROJECT_RESOURCE_ALLOCATION_ACTIVATED
PROJECT_RESOURCE_ALLOCATION_COMPLETED
PROJECT_RESOURCE_ALLOCATION_CANCELLED

TASK_RESOURCE_ASSIGNED
TASK_RESOURCE_ASSIGNMENT_UPDATED
TASK_RESOURCE_ASSIGNMENT_REMOVED

EFFORT_ESTIMATE_CREATED
EFFORT_ESTIMATE_UPDATED
EFFORT_ESTIMATE_ARCHIVED
EFFORT_FORECAST_REBUILT
ACTUAL_EFFORT_RECORDED
ACTUAL_EFFORT_UPDATED
ACTUAL_EFFORT_CANCELLED

RESOURCE_UTILIZATION_REBUILT
PROJECT_CAPACITY_SUMMARY_REBUILT
WORKLOAD_SNAPSHOT_CREATED

RESOURCE_RISK_FLAG_CREATED
RESOURCE_RISK_FLAG_MITIGATED
RESOURCE_RISK_FLAG_CLOSED
ASSIGNMENT_CONFLICT_DETECTED
ASSIGNMENT_CONFLICT_ACKNOWLEDGED
ASSIGNMENT_CONFLICT_RESOLVED

RESOURCE_COST_INPUT_REBUILT
UTILIZATION_THRESHOLD_POLICY_UPDATED
RESOURCE_REPORT_EXPORTED
RESOURCE_COST_SENSITIVE_FIELD_VIEWED
```

Standard variables:

```text
actor.userId
workspace.id
project.id
resource.id
role.id
skill.id
calendar.id
allocation.id
assignment.id
estimate.id
forecast.id
snapshot.id
riskFlag.id
conflict.id
effort.hours
cost.amount
currency
status
occurredAt
traceId
```

---

# 15. Audit / activity / outbox

Audit-sensitive actions:

```text
resource cost/rate viewed
rate card linked to resource
manual effort estimate changed
actual effort recorded/changed
allocation cancelled
capacity exception created
cost input exported
resource report exported
```

Activity actions:

```text
PROJECT_RESOURCE_ALLOCATION_CREATED
TASK_RESOURCE_ASSIGNED
EFFORT_FORECAST_REBUILT
RESOURCE_RISK_FLAG_CREATED
ASSIGNMENT_CONFLICT_DETECTED
WORKLOAD_SNAPSHOT_CREATED
```

Outbox required for:

```text
resource overloaded
capacity gap detected
cost input rebuilt
effort forecast materially increased
assignment conflict detected
workload snapshot created
```

Idempotency recommended for:

```text
sync resources from members
rebuild effort forecast
rebuild utilization summary
rebuild project capacity summary
rebuild cost input
create workload snapshot
```

---

# 16. Business rules master

## 16.1 Resource rules

```text
RES-001 ResourceProfile is not user account.
RES-002 ResourceRole is not IAM role.
RES-003 ResourceSkill is not permission.
RES-004 External resource does not become internal user.
```

## 16.2 Capacity rules

```text
CAL-001 Workspace default calendar required.
CAL-002 CapacityException changes planning capacity only.
CAL-003 CapacityException is not HR leave approval.
CAL-004 Missing resource calendar falls back to default.
```

## 16.3 Allocation/assignment rules

```text
ALLOC-001 Allocation does not grant project access.
ALLOC-002 Allocation date range required.
ALLOC-003 Assignment target must belong to same project.
ASSIGN-001 Assignment does not grant access.
ASSIGN-002 Duplicate active assignment prevented.
```

## 16.4 Effort rules

```text
EFF-001 Effort uses hours as canonical unit.
EFF-002 Actual effort is lightweight management data.
EFF-003 Actual effort is not payroll/timesheet.
EFF-004 Revised estimate can supersede previous estimate.
```

## 16.5 Utilization rules

```text
UTIL-001 Utilization calculated against available capacity.
UTIL-002 Overload threshold configurable.
UTIL-003 Capacity zero handled safely.
```

## 16.6 Cost rules

```text
COST-001 Cost input is sensitive.
COST-002 Missing rate creates risk, not crash.
COST-003 Cost input feeds profitability.
COST-004 Cost input is not payroll.
```

---

# 17. Error catalog

```text
RESOURCE_PROFILE_NOT_FOUND
RESOURCE_PROFILE_DUPLICATE_LINKED_USER
RESOURCE_PROFILE_ACCESS_DENIED
RESOURCE_ROLE_NOT_FOUND
RESOURCE_ROLE_DUPLICATE_CODE
RESOURCE_SKILL_NOT_FOUND
RESOURCE_SKILL_DUPLICATE_CODE

CAPACITY_CALENDAR_NOT_FOUND
CAPACITY_CALENDAR_INVALID
CAPACITY_EXCEPTION_NOT_FOUND
CAPACITY_EXCEPTION_INVALID_DATE_RANGE

PROJECT_RESOURCE_ALLOCATION_NOT_FOUND
PROJECT_RESOURCE_ALLOCATION_INVALID_STATUS
PROJECT_RESOURCE_ALLOCATION_INVALID_DATE_RANGE
PROJECT_RESOURCE_ALLOCATION_INVALID_AMOUNT
PROJECT_RESOURCE_ALLOCATION_ACCESS_DENIED

TASK_RESOURCE_ASSIGNMENT_NOT_FOUND
TASK_RESOURCE_ASSIGNMENT_DUPLICATE
TASK_RESOURCE_ASSIGNMENT_TARGET_MISMATCH
TASK_RESOURCE_ASSIGNMENT_ACCESS_DENIED

EFFORT_ESTIMATE_NOT_FOUND
EFFORT_ESTIMATE_INVALID_TARGET
EFFORT_ESTIMATE_INVALID_HOURS
EFFORT_FORECAST_REBUILD_FAILED

ACTUAL_EFFORT_RECORD_NOT_FOUND
ACTUAL_EFFORT_INVALID_HOURS
ACTUAL_EFFORT_CANCEL_NOT_ALLOWED

RESOURCE_UTILIZATION_REBUILD_FAILED
PROJECT_CAPACITY_REBUILD_FAILED
WORKLOAD_SNAPSHOT_CREATE_FAILED

RESOURCE_RISK_FLAG_NOT_FOUND
RESOURCE_RISK_FLAG_INVALID_STATUS
ASSIGNMENT_CONFLICT_NOT_FOUND
ASSIGNMENT_CONFLICT_INVALID_STATUS

RESOURCE_COST_INPUT_REBUILD_FAILED
RESOURCE_COST_SENSITIVE_ACCESS_DENIED
UTILIZATION_THRESHOLD_POLICY_INVALID
RESOURCE_REPORT_ACCESS_DENIED
```

---

# 18. Required tests

## 18.1 Resource tests

```text
createResourceProfileInternalUser_success
createResourceProfileDuplicateUser_rejected
createPlaceholderResource_success
externalResourceDoesNotCreateUser
createResourceRole_success
resourceRoleDoesNotGrantIam
createResourceSkill_success
assignSkillToResource_success
```

## 18.2 Calendar/capacity tests

```text
createWorkspaceDefaultCalendar_success
createResourceCalendar_success
capacityCalculatedFromCalendar_success
capacityExceptionReducedCapacity_success
capacityExceptionExtraCapacity_success
missingCalendarFallsBackToDefault
capacityExceptionIsNotLeaveApproval
```

## 18.3 Allocation/assignment tests

```text
createProjectResourceAllocation_success
allocationDoesNotGrantProjectAccess
activateAllocation_success
cancelAllocation_success
assignResourceToTask_success
duplicateTaskAssignment_rejected
assignmentDoesNotGrantAccess
```

## 18.4 Effort tests

```text
createEffortEstimateForTask_success
createEffortEstimateForDefectRework_success
revisedEstimateSupersedesOld
invalidNegativeEffort_rejected
rebuildEffortForecast_success
recordActualEffort_success
actualEffortIsNotTimesheetPayroll
```

## 18.5 Utilization/capacity summary tests

```text
resourceUtilizationCalculated_success
overloadDetected_success
underAllocationDetected_success
capacityZeroHandledSafely
projectCapacitySummaryCalculated_success
capacityGapCreatesRiskFlag
```

## 18.6 Conflict/risk tests

```text
overAllocationConflictDetected
missingRateConflictDetected
missingEstimateConflictDetected
acknowledgeConflict_success
resolveConflict_success
createResourceRiskFlag_success
mitigateResourceRiskFlag_success
closeResourceRiskFlag_success
```

## 18.7 Cost/profitability integration tests

```text
resourceCostInputBuiltFromEffortAndRate
missingRateCreatesRiskNotCrash
costInputSensitiveMaskedWithoutPermission
costInputTriggersProfitabilityRebuild
defectReworkFeedsCostForecast
```

## 18.8 Governance/audit tests

```text
manualEffortAdjustmentAudited
capacityExceptionAudited
resourceCostSensitiveViewAudited
workloadSnapshotImmutable
rateChangeDoesNotRewriteHistoricalSnapshot
```

## 18.9 Authorization tests

```text
viewResourceWithoutPermission_forbidden
manageAllocationWithoutPermission_forbidden
viewCostInputWithoutSensitivePermission_forbidden
recordActualEffortWithoutPermission_forbidden
crossWorkspaceResource_forbidden
```

## 18.10 Event tests

```text
resourceEventSeeder_firstRun_createsDefinitions
resourceEventSeeder_secondRun_noDuplicates
resourceAllocated_eventEmitted
effortForecastRebuilt_eventEmitted
overloadRisk_eventEmitted
costInputRebuilt_eventEmitted
```

---

# 19. Manual verification checklist

Completion file must include:

```text
1. Sync ResourceProfile from workspace members.
2. Create ResourceRole and ResourceSkill.
3. Create workspace default capacity calendar.
4. Create capacity exception.
5. Allocate resource to project.
6. Assign resource to task.
7. Create effort estimate for task.
8. Rebuild effort forecast.
9. Record actual/observed effort.
10. Rebuild utilization summary.
11. Confirm overloaded resource detected.
12. Rebuild project capacity summary.
13. Confirm capacity gap detected.
14. Rebuild resource cost input.
15. Confirm Phase 36 profitability cost forecast can consume cost input.
16. Confirm assignment does not grant project access.
17. Confirm cost values hidden without sensitive permission.
18. Confirm no payroll/HR attendance/timesheet claim.
```

---

# 20. Acceptance criteria

Phase 37 is accepted only if:

```text
1. Current resource/capacity/effort capability is classified against TO-BE.
2. ResourceProfile implemented/tested.
3. ResourceRole and ResourceSkill implemented/tested.
4. CapacityCalendar and CapacityException implemented/tested.
5. ProjectResourceAllocation implemented/tested.
6. TaskResourceAssignment implemented/tested.
7. EffortEstimate and EffortForecast implemented/tested.
8. Actual/Observed Effort foundation implemented/tested.
9. ResourceUtilizationSummary implemented/tested.
10. ProjectCapacitySummary implemented/tested.
11. WorkloadSnapshot implemented/tested.
12. ResourceRiskFlag and AssignmentConflict implemented/tested.
13. ResourceCostInput implemented/tested and integrated with Phase 36.
14. Automatic recalculation implemented/tested.
15. Threshold policy implemented/tested.
16. Notifications/reports integrated/tested.
17. IAM permissions implemented/tested.
18. Event seeders idempotent.
19. Activity/audit/outbox follows Phase 04.
20. Cost/rate sensitive access enforced.
21. `mvn compile` passes.
22. `mvn test` passes.
23. Completion file exists.
```

Do not mark complete if:

```text
assignment grants project permission
resource role is confused with IAM role
capacity exception is treated as HR leave approval
actual effort is treated as payroll/timesheet
cost values are visible without sensitive permission
missing rate crashes forecast instead of creating risk
profitability cost integration is absent
tests fail
```

---

# 21. Required phase completion file

Agent must create:

```text
docs/phase-complete/PHASE_37_RESOURCE_CAPACITY_EFFORT_UTILIZATION_TO_BE_COMPLETE.md
```

Required sections:

```text
# Phase 37 — Resource / Capacity / Effort / Utilization Complete

## 1. Summary
## 2. Source Inputs Reviewed
## 3. Current vs TO-BE Classification Matrix
## 4. Implemented in Current BE
## 5. Implemented / Hardened in This Phase
## 6. Seed-only Items Added
## 7. Deferred Items and Target Phase
## 8. Product Boundary Decision
## 9. Entity Mapping
## 10. API Changes
## 11. Resource Profile Strategy
## 12. Role / Skill Strategy
## 13. Capacity Calendar Strategy
## 14. Project Allocation Strategy
## 15. Task Assignment Strategy
## 16. Effort Estimate / Forecast Strategy
## 17. Actual / Observed Effort Strategy
## 18. Utilization Strategy
## 19. Workload Snapshot Strategy
## 20. Risk / Conflict Strategy
## 21. Cost Input / Profitability Integration
## 22. Automatic Recalculation Strategy
## 23. Threshold Policy Strategy
## 24. Notification Strategy
## 25. Reporting / Export Strategy
## 26. Authorization Matrix
## 27. Activity / Audit / Outbox Notes
## 28. Idempotency Strategy
## 29. Tests Added
## 30. Commands Run
## 31. Test Results
## 32. Manual Verification
## 33. Assumptions
## 34. Deviations From Prompt
## 35. Known Risks
## 36. Future Phases That Must Return to Resource Planning
```

---

# 22. Future phases that may return

```text
Phase 38 — Audit / Compliance / Privacy:
- Sensitive cost/rate access review, export audit, privacy retention.

Phase 39 — Integration / Import / Export:
- Calendar integration, HRIS import, BI export.

Phase 40 — Service / Support / Maintenance:
- Support staffing and maintenance capacity.

Phase 41 — Knowledge Graph / Semantic Index:
- AI workload explanation, staffing suggestions, cost overrun prediction.

Future timesheet backlog:
- Detailed time entry, approval, payroll connection if product ever needs it.
```

---

# 23. Agent anti-bịa rules

The agent must not:

```text
1. Treat ResourceProfile as user account.
2. Treat ResourceRole as IAM role.
3. Let assignment grant access.
4. Treat capacity exception as HR leave approval.
5. Treat actual effort as payroll/timesheet.
6. Expose cost/rate values without permission.
7. Crash cost forecast when rate is missing.
8. Rewrite historical snapshots after rate changes.
9. Claim AI automatically assigns resources.
10. Skip profitability cost integration.
```

---

# 24. Prompt to give coding agent

```text
You are implementing Phase 37 — TO-BE Resource Capacity, Effort Forecast, Utilization, Assignment Load & Cost Input for Profitability.

Product direction:
PM should not manually calculate workload and cost.
The system should derive resource capacity, effort forecast, utilization, assignment load, risk, and cost input from projects, tasks, WBS, requirements, defects, deliverables, resource allocation, calendars, and rate cards.
Phase 37 must feed Phase 36 profitability cost forecast.

Before coding, read:
- CLAUDE.md / CLAUDE.ms
- Coding_convention.md
- Phase 00–36 docs and completion files
- Current backend code, migrations, tests

Your task:
1. Compare current resource/capacity/effort capability against this Phase 37 TO-BE spec.
2. Classify each capability with required labels.
3. Implement ResourceProfile.
4. Implement ResourceRole and ResourceSkill.
5. Implement CapacityCalendar and CapacityException.
6. Implement ProjectResourceAllocation.
7. Implement TaskResourceAssignment.
8. Implement EffortEstimate and EffortForecast.
9. Implement Actual/Observed Effort foundation.
10. Implement ResourceUtilizationSummary and ProjectCapacitySummary.
11. Implement WorkloadSnapshot.
12. Implement ResourceRiskFlag and AssignmentConflict.
13. Implement ResourceCostInput and integrate with Phase 36 CostForecast.
14. Implement automatic recalculation and utilization threshold policy.
15. Integrate with Governance, Notifications, Reporting, Audit, Outbox, IAM.
16. Run mvn compile and mvn test.
17. Create docs/phase-complete/PHASE_37_RESOURCE_CAPACITY_EFFORT_UTILIZATION_TO_BE_COMPLETE.md.

Do not implement or claim payroll, HR attendance, leave approval, salary management, detailed timesheet approval, or employee performance scoring in this phase.
```

---

# 25. Quick tracking matrix

| Capability | Current backend | Phase 37 action | Later phase |
|---|---|---|---|
| ResourceProfile | Missing/unknown | Must implement | HR import Phase 39 |
| ResourceRole | Missing/unknown | Must implement | — |
| ResourceSkill | Missing/unknown | Must implement | AI matching Phase 41 |
| CapacityCalendar | Missing/unknown | Must implement | Calendar integration Phase 39 |
| CapacityException | Missing/unknown | Must implement | HR integration Phase 39 |
| ProjectResourceAllocation | Missing/unknown | Must implement | — |
| TaskResourceAssignment | Missing/partial | Must implement/harden | — |
| EffortEstimate | Missing/partial | Must implement/harden | — |
| EffortForecast | Missing/unknown | Must implement | AI forecast Phase 41 |
| Actual/Observed Effort | Missing/unknown | Must implement light foundation | Timesheet backlog |
| ResourceUtilizationSummary | Missing/unknown | Must implement | — |
| ProjectCapacitySummary | Missing/unknown | Must implement | — |
| WorkloadSnapshot | Missing/unknown | Must implement | — |
| ResourceRiskFlag | Missing/unknown | Must implement | — |
| AssignmentConflict | Missing/unknown | Must implement | — |
| ResourceCostInput | Missing/unknown | Must implement | Phase 36 integration |
| Auto recalculation | Missing/unknown | Must implement | — |
| Utilization threshold | Missing/unknown | Must implement | — |
| Payroll | Not needed | Not in scope | Future if ever |
| HR attendance | Not needed | Not in scope | Future if ever |
| Leave approval | Not needed | Not in scope | Future if ever |
| Detailed timesheet approval | Not needed | Not in scope | Future backlog only |

---

# 26. Final principle

Phase 37 is not complete when "a task has an assignee."

Phase 37 is complete when Scopery can explain:

```text
who is doing the work
how much effort is needed
how much capacity is available
who is overloaded
which role is missing
how much cost the effort creates
how workload changes profitability
```

Resource is not permission.

Role is not IAM.

Capacity is not HR attendance.

Actual effort is not payroll.

Cost input is sensitive.

Workload intelligence should help PM act before the project becomes late or unprofitable.
