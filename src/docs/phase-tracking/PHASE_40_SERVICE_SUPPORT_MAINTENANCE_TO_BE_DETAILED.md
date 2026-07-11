# PHASE 40 — TO-BE Service, Support, Maintenance, Incident, SLA Tracking & Post-Delivery Operations

> Project: Scopery Backend  
> Phase: 40  
> Document type: TO-BE implementation-grade specification  
> Status: Planning / Post-delivery service and support operations  
> Roadmap group: Service Operations & Customer Success  
> Depends on: Phase 00 — Master Backend Roadmap, Phase 01 — API Path / Security Baseline, Phase 02 — IAM Core, Phase 03 — Workspace / Organization / Team, Phase 04 — Platform Audit / Outbox / Idempotency, Phase 05 — Event Registry, Phase 06 — Notification Engine, Phase 09 — Project Core / WBS / Task, Phase 10 — Project Authorization, Phase 20 — Project Events / Notifications, Phase 22 — Reporting / Dashboard / Export, Phase 23 — Core Hardening, Phase 24 — Scope / Deliverable / Acceptance, Phase 25 — RAID / Decision Management, Phase 26 — Quality / Test / Release, Phase 27 — Document Hub / Generation, Phase 29 — External Party / Client CRM / Stakeholder, Phase 30 — Customer / External Portal, Phase 31 — Meetings / Collaboration, Phase 32 — Search / Productivity, Phase 33 — Custom Fields / Forms / Configuration, Phase 34 — Governance / Versioning / Permission / Audit, Phase 35 — Advanced Notifications, Phase 36 — Revenue & Profitability Visibility, Phase 37 — Resource / Capacity / Effort / Utilization, Phase 38 — Audit / Compliance / Privacy / Retention, Phase 39 — Integration / Import / Export / Connectors  
> API base: `/api`  
> Primary module: `modules/service`, `modules/support`, `modules/maintenance`, or `modules/postdelivery` depending on repository architecture  
> Related modules: `iam`, `workspace`, `project`, `task`, `quality`, `release`, `document`, `externalparty`, `clientportal`, `notification`, `reporting`, `governance`, `resource`, `profitability`, `integration`, `trust`, `audit`, `eventregistry`, future `knowledge-graph`, `ai-service-desk`, `billing`  
> Important rule: Phase 40 covers operational service/support/maintenance after delivery. It is not a full ITSM suite, not billing, not payment, not payroll, not external helpdesk replacement, and not automated legal SLA enforcement. It provides service visibility, support workflow, incident tracking, SLA measurement, maintenance planning, and escalation-by-notification.

---

# 0. Purpose

Phase 40 turns Scopery from a project delivery tool into a post-delivery operations tool.

A project often does not end at acceptance.

After delivery, teams may need to handle:

```text
client support requests
bugs reported after go-live
incidents
maintenance work
warranty period tracking
SLA response/resolution tracking
small service requests
handover documents
release patches
support effort
support cost
client portal ticket intake
customer success follow-up
```

Phase 40 answers:

```text
What support cases exist after delivery?
Which client reported the issue?
Which project/product/release is affected?
Is this a bug, incident, service request, or maintenance task?
Who owns it?
What is the priority?
What is the SLA target?
Is it breached or at risk?
What maintenance activities are planned?
What support effort/cost is generated?
Which support items should become tasks, defects, change requests, or releases?
What can the client see in portal?
```

---

# 1. Product intention

Scopery should support teams that deliver client projects and then maintain them.

Examples:

```text
software agency supporting delivered app
consulting company handling post-go-live requests
outsourcing team maintaining client system
implementation partner supporting rollout
design/dev studio handling warranty fixes
```

The intended experience:

```text
Client submits support request in portal.
Support team triages it.
System links it to project, deliverable, release, defect, or document.
SLA clock starts if policy applies.
Owner gets notified.
PM/support owner tracks status.
If work is needed, system can create task/defect/change request.
Maintenance windows are planned.
Support effort feeds resource/profitability visibility.
Dashboard shows open support load, SLA risk, incidents, and maintenance status.
```

---

# 2. Core principle

```text
Support is not just a ticket.
Support is the operational life of delivered work.
```

Phase 40 should connect:

```text
client request
+ project context
+ service classification
+ ownership
+ SLA target
+ support work
+ incident/defect/release link
+ maintenance plan
+ client visibility
+ audit/notification/reporting
```

A support case should never be an isolated note with no owner, priority, or traceability.

---

# 3. Source inputs

Before coding Phase 40, the agent must read:

```text
1. Current backend codebase
2. Phase 01 Security baseline implementation
3. Phase 02 IAM implementation
4. Phase 03 Workspace/Organization/Team implementation
5. Phase 04 Audit/Outbox/Idempotency implementation
6. Phase 05 Event Registry implementation
7. Phase 06 Notification implementation
8. Phase 09 Project/Task implementation
9. Phase 10 Project Authorization implementation
10. Phase 20 Notification/Event implementation
11. Phase 22 Reporting implementation
12. Phase 23 Core Hardening completion file
13. Phase 24 Scope/Deliverable/Acceptance implementation
14. Phase 25 RAID implementation
15. Phase 26 Quality/Test/Release implementation
16. Phase 27 Document Hub implementation
17. Phase 29 External Party/Client Contact implementation
18. Phase 30 Customer/External Portal implementation
19. Phase 31 Meetings/Collaboration implementation
20. Phase 32 Search/Productivity implementation
21. Phase 33 Custom Fields/Forms implementation
22. Phase 34 Governance/Versioning/Permission/Audit implementation
23. Phase 35 Advanced Notifications implementation
24. Phase 36 Revenue/Profitability implementation
25. Phase 37 Resource/Capacity implementation
26. Phase 38 Trust/Privacy implementation
27. Phase 39 Integration/Import/Export implementation
28. Existing support/ticket/incident code if any
29. Existing defect/release/task linking code
30. Existing portal submission code
31. Current IAM seeders and EventDefinition seeders
```

The agent must inspect actual code and classify current state.

---

# 4. Current expected backend state

Before Phase 40, Scopery likely has:

```text
projects
tasks
deliverables
acceptance
defects/test/release
documents
client portal
external contacts
notifications
comments/meetings
resource/profitability
audit/trust
integration foundation
```

Likely missing or partial:

```text
ServiceWorkspace / ServiceProfile
SupportCase
SupportRequest
SupportTicket
SupportQueue
SupportAssignment
SupportComment
SupportStatusHistory
SupportSlaPolicy
SlaTarget
SlaClock
SlaBreach
IncidentRecord
ProblemRecord
MaintenancePlan
MaintenanceWindow
MaintenanceActivity
WarrantyCoverage
ServiceHandoverPackage
ServiceRequestType
SupportKnowledgeLink
SupportEffortRecord
ServiceCostInput
SupportMetricSnapshot
ClientSupportPortalView
SupportEscalationRule
```

---

# 5. Target statement

Phase 40 must deliver:

```text
1. Service profile for project/client post-delivery support.
2. Support case/ticket model.
3. Support request intake from internal users and client portal.
4. Support queue and assignment.
5. Support classification: request, bug, incident, question, maintenance, change.
6. SLA policy/target/clock/breach measurement.
7. Incident and problem tracking foundation.
8. Maintenance plan/window/activity.
9. Warranty/coverage tracking.
10. Handover package linking delivered project to support.
11. Links to task/defect/release/change request/document.
12. Support effort and cost input for resource/profitability.
13. Client portal support view.
14. Notifications and escalation-by-notification.
15. Reporting/dashboard/export.
16. Governance, audit, trust, integration, tests.
```

---

# 6. Boundary decisions

## 6.1 Support workflow is lightweight

Phase 40 uses status transitions, owner assignment, SLA clock, notifications, and audit.

It does not introduce heavy approval workflow.

## 6.2 SLA is tracking, not legal enforcement

SLA measurements help teams operate.

They do not automatically guarantee legal or contractual compliance.

## 6.3 Support effort is not payroll

Support effort can feed resource/profitability.

It is not timesheet approval, attendance, payroll, or salary tracking.

## 6.4 Maintenance plan is operational

MaintenancePlan helps plan windows and activities.

It is not infrastructure monitoring, APM, or automated deployment unless integrations exist.

## 6.5 Client portal view is controlled

Clients only see support items and fields explicitly marked portal-visible.

Internal cost, profit, resource rate, internal RCA notes, and sensitive audit remain hidden.

---

# 7. Classification labels

Use these labels in completion file:

| Label | Meaning |
|---|---|
| `CURRENTLY_IMPLEMENTED` | Existing backend already supports it with tests. |
| `PARTIALLY_IMPLEMENTED` | Some support exists but gaps remain. |
| `MUST_IMPLEMENT_IN_PHASE_40` | Required now. |
| `MUST_HARDEN_IN_PHASE_40` | Existing support/service behavior must be hardened now. |
| `SEED_ONLY_IN_PHASE_40` | Seed request types/events/permissions/default configs only. |
| `DEFERRED_TO_PHASE_XX` | Later phase returns. |
| `DEFERRED_TO_POST_23_BACKLOG` | Later backlog item. |
| `NOT_IN_SCOPE_FOR_PHASE_40` | Explicitly not in this phase. |

---

# 8. Required capabilities

---

## 8.1 SERV-001 — ServiceProfile

Classification: `MUST_IMPLEMENT_IN_PHASE_40`

Purpose:

```text
Enable post-delivery service/support for a project or client.
```

Profile scope:

```text
PROJECT
CLIENT
WORKSPACE
PRODUCT
```

Fields:

```text
support owner
client organization
linked project
default SLA policy
default support queue
portal intake enabled
service start/end dates
warranty start/end dates
status
```

Rules:

```text
1. ServiceProfile belongs to workspace.
2. Project-scoped profile links to project.
3. Client-scoped profile links to ExternalOrganization.
4. Profile can be created from accepted deliverable/project handover.
5. Profile controls support defaults.
```

Status:

```text
DRAFT
ACTIVE
SUSPENDED
ENDED
ARCHIVED
```

---

## 8.2 TYPE-001 — ServiceRequestType

Classification: `MUST_IMPLEMENT_IN_PHASE_40`

Purpose:

```text
Classify support cases consistently.
```

Default request types:

```text
QUESTION
BUG_REPORT
INCIDENT
SERVICE_REQUEST
CHANGE_REQUEST
MAINTENANCE_REQUEST
ACCESS_REQUEST
DATA_REQUEST
TRAINING_REQUEST
WARRANTY_FIX
OTHER
```

Rules:

```text
1. Type belongs to workspace.
2. Type can define default priority/SLA/queue.
3. Type can be portal-visible or internal-only.
4. Type can map to downstream object creation.
```

---

## 8.3 CASE-001 — SupportCase

Classification: `MUST_IMPLEMENT_IN_PHASE_40`

Purpose:

```text
Main support/ticket record.
```

Case sources:

```text
PORTAL_SUBMISSION
INTERNAL_CREATE
EMAIL_IMPORT_FUTURE
CHAT_IMPORT_FUTURE
INTEGRATION_IMPORT
INCIDENT_TRIGGER
MAINTENANCE_PLAN
DEFECT_ESCALATION
```

Statuses:

```text
NEW
TRIAGED
IN_PROGRESS
WAITING_FOR_CLIENT
WAITING_INTERNAL
WAITING_VENDOR
RESOLVED
CLOSED
CANCELLED
ARCHIVED
```

Rules:

```text
1. SupportCase belongs to workspace.
2. Case may link to project/service profile/client/contact.
3. Case must have type, priority, status, owner/queue.
4. Portal-submitted case must be client-safe.
5. Internal-only fields hidden from portal.
6. Case status changes audited.
```

---

## 8.4 CASE-002 — SupportCasePriority

Classification: `MUST_IMPLEMENT_IN_PHASE_40`

Priority levels:

```text
LOW
NORMAL
HIGH
URGENT
CRITICAL
```

Rules:

```text
1. Priority can affect SLA target.
2. Priority can affect notification routing.
3. Priority changes audited.
4. Critical cases can create incident record if policy.
```

---

## 8.5 CASE-003 — SupportStatusHistory

Classification: `MUST_IMPLEMENT_IN_PHASE_40`

Purpose:

```text
Track case lifecycle history.
```

Rules:

```text
1. Every status transition recorded.
2. Actor, timestamp, from/to status, reason captured.
3. History visible internally.
4. Portal status history can be limited.
```

---

## 8.6 QUEUE-001 — SupportQueue

Classification: `MUST_IMPLEMENT_IN_PHASE_40`

Purpose:

```text
Group support cases for routing and ownership.
```

Examples:

```text
General Support
Technical Support
QA / Bug Triage
Customer Success
Maintenance Team
Critical Incident Team
```

Rules:

```text
1. Queue belongs to workspace.
2. Queue has owner/team.
3. Queue can have default SLA.
4. Queue membership does not automatically grant project access unless policy explicitly maps it.
```

---

## 8.7 ASSIGN-001 — SupportAssignment

Classification: `MUST_IMPLEMENT_IN_PHASE_40`

Purpose:

```text
Assign support case to user/resource/team.
```

Assignment types:

```text
OWNER
ASSIGNEE
WATCHER
TECHNICAL_LEAD
CUSTOMER_SUCCESS
QA_OWNER
```

Rules:

```text
1. Assignment belongs to case.
2. Assignment does not grant access by itself.
3. Assignment changes audited.
4. Watchers can receive notifications.
```

---

## 8.8 COMM-001 — SupportComment / ClientReply

Classification: `MUST_IMPLEMENT_IN_PHASE_40`

Purpose:

```text
Capture conversation on support case.
```

Comment visibility:

```text
INTERNAL
CLIENT_VISIBLE
SYSTEM
```

Rules:

```text
1. Client-visible comments can appear in portal.
2. Internal comments hidden from portal.
3. Comments can attach documents.
4. Sensitive content follows Phase 38.
5. Comment creation triggers notifications.
```

---

## 8.9 SLA-001 — SupportSlaPolicy

Classification: `MUST_IMPLEMENT_IN_PHASE_40`

Purpose:

```text
Define response/resolution targets.
```

SLA dimensions:

```text
request type
priority
queue
service profile
client
project
business hours calendar
```

Targets:

```text
first response time
next response time
resolution time
triage time
```

Rules:

```text
1. SLA policy belongs to workspace.
2. SLA can use business hours calendar.
3. SLA target starts based on case status/source.
4. SLA can pause in waiting statuses if policy.
5. SLA is tracking, not legal guarantee.
```

---

## 8.10 SLA-002 — SlaClock / SlaTarget

Classification: `MUST_IMPLEMENT_IN_PHASE_40`

Purpose:

```text
Track active SLA clocks for a case.
```

Clock statuses:

```text
NOT_STARTED
RUNNING
PAUSED
MET
BREACHED
CANCELLED
```

Rules:

```text
1. Case can have multiple SLA targets.
2. Clock records start/pause/resume/end.
3. Breach calculated by job or status event.
4. SLA status feeds dashboard and notifications.
```

---

## 8.11 SLA-003 — SlaBreach

Classification: `MUST_IMPLEMENT_IN_PHASE_40`

Purpose:

```text
Record SLA breach event.
```

Rules:

```text
1. Breach links to case and target.
2. Breach captures expected deadline and actual completion.
3. Breach creates notification.
4. Breach can trigger escalation-by-notification.
5. Breach does not automatically create legal liability.
```

---

## 8.12 INC-001 — IncidentRecord

Classification: `MUST_IMPLEMENT_IN_PHASE_40`

Purpose:

```text
Track operational incident affecting service.
```

Incident severities:

```text
SEV_1_CRITICAL
SEV_2_HIGH
SEV_3_MEDIUM
SEV_4_LOW
```

Incident statuses:

```text
OPEN
INVESTIGATING
MITIGATING
MONITORING
RESOLVED
CLOSED
CANCELLED
```

Rules:

```text
1. Incident can link to one or more support cases.
2. Incident has severity, owner, impact, timeline.
3. Incident can link to release/defect/task.
4. Incident can have client-visible summary.
5. Internal RCA can be hidden.
```

---

## 8.13 INC-002 — IncidentTimelineEntry

Classification: `MUST_IMPLEMENT_IN_PHASE_40`

Purpose:

```text
Record incident timeline.
```

Entry types:

```text
DETECTED
ACKNOWLEDGED
INVESTIGATION_STARTED
MITIGATION_STARTED
CLIENT_UPDATE_SENT
RESOLVED
CLOSED
SYSTEM_NOTE
```

Rules:

```text
1. Timeline entry belongs to incident.
2. Visibility can be internal or client-visible.
3. Timeline supports incident review/reporting.
```

---

## 8.14 PROB-001 — ProblemRecord

Classification: `MUST_IMPLEMENT_IN_PHASE_40`

Purpose:

```text
Track recurring root cause behind cases/incidents.
```

Statuses:

```text
OPEN
UNDER_ANALYSIS
WORKAROUND_DEFINED
FIX_PLANNED
RESOLVED
CLOSED
ARCHIVED
```

Rules:

```text
1. Problem can link multiple incidents/cases/defects.
2. Problem can create task/change request/defect.
3. Root cause notes can be internal.
4. Problem resolution audited.
```

---

## 8.15 MAINT-001 — MaintenancePlan

Classification: `MUST_IMPLEMENT_IN_PHASE_40`

Purpose:

```text
Plan recurring or one-time maintenance for delivered project/service.
```

Plan types:

```text
ONE_TIME
RECURRING
VERSION_UPGRADE
SECURITY_PATCH
DATA_CLEANUP
HEALTH_CHECK
CONTENT_UPDATE
CONFIG_UPDATE
```

Rules:

```text
1. MaintenancePlan belongs to service profile/project/client.
2. Plan can create MaintenanceWindow.
3. Plan can create support cases/tasks.
4. Plan has owner and schedule.
5. Plan visible to client only if enabled.
```

---

## 8.16 MAINT-002 — MaintenanceWindow

Classification: `MUST_IMPLEMENT_IN_PHASE_40`

Purpose:

```text
Schedule when maintenance occurs.
```

Statuses:

```text
PLANNED
CONFIRMED
IN_PROGRESS
COMPLETED
CANCELLED
MISSED
ARCHIVED
```

Rules:

```text
1. Window has start/end.
2. Window can have expected impact.
3. Client-visible notice optional.
4. Window can link to tasks/releases/documents.
5. Window completion can create MaintenanceActivity.
```

---

## 8.17 MAINT-003 — MaintenanceActivity

Classification: `MUST_IMPLEMENT_IN_PHASE_40`

Purpose:

```text
Record actual maintenance work performed.
```

Activity types:

```text
PATCH_APPLIED
CONFIG_UPDATED
DATA_MAINTENANCE
HEALTH_CHECK_DONE
BACKUP_CHECKED
SECURITY_REVIEWED
DEPENDENCY_UPDATED
OTHER
```

Rules:

```text
1. Activity links to plan/window.
2. Activity can record outcome, effort, documents.
3. Activity can create support effort/cost input.
4. Activity can be client-visible if policy.
```

---

## 8.18 COV-001 — WarrantyCoverage / ServiceCoverage

Classification: `MUST_IMPLEMENT_IN_PHASE_40`

Purpose:

```text
Track support coverage period and included support type.
```

Coverage types:

```text
WARRANTY
MAINTENANCE
RETAINER_SUPPORT
AD_HOC_SUPPORT
INTERNAL_SUPPORT
```

Rules:

```text
1. Coverage belongs to service profile.
2. Coverage has start/end date.
3. Coverage can define included request types.
4. Coverage can influence SLA and priority.
5. Coverage does not perform billing.
```

---

## 8.19 HAND-001 — ServiceHandoverPackage

Classification: `MUST_IMPLEMENT_IN_PHASE_40`

Purpose:

```text
Bundle information needed to support delivered project.
```

Can include:

```text
accepted deliverables
release notes
deployment notes
user guides
known issues
open risks
maintenance checklist
client contacts
support queue
SLA policy
handover meeting notes
```

Rules:

```text
1. Handover package belongs to project/service profile.
2. Package can link documents.
3. Package can be finalized/locked.
4. Package can be partially client-visible.
5. Handover package creates service readiness signal.
```

---

## 8.20 LINK-001 — SupportWorkLink

Classification: `MUST_IMPLEMENT_IN_PHASE_40`

Purpose:

```text
Link support case to actionable work objects.
```

Supported links:

```text
TASK
DEFECT
RELEASE
CHANGE_REQUEST
REQUIREMENT
DOCUMENT
DELIVERABLE
RISK
ISSUE
MEETING
PROBLEM_RECORD
INCIDENT_RECORD
```

Rules:

```text
1. Link belongs to support case.
2. Link target must be permission-valid.
3. Link does not copy data blindly.
4. Link changes audited.
```

---

## 8.21 EFF-001 — SupportEffortRecord

Classification: `MUST_IMPLEMENT_IN_PHASE_40`

Purpose:

```text
Capture support effort for resource/cost/profitability visibility.
```

Rules:

```text
1. Effort belongs to support case/activity/incident.
2. Effort can link to resource profile.
3. Effort can feed Phase 37 actual/observed effort.
4. Effort can feed Phase 36 profitability cost input.
5. Effort is not payroll.
```

---

## 8.22 COST-001 — ServiceCostInput

Classification: `MUST_IMPLEMENT_IN_PHASE_40`

Purpose:

```text
Provide support/maintenance cost input to profitability.
```

Sources:

```text
support effort * rate
maintenance effort * rate
incident response effort * rate
manual support cost adjustment
vendor support cost future
```

Rules:

```text
1. Cost values sensitive.
2. Cost input traceable to support object.
3. Cost input can update Phase 36 project profitability.
4. Cost input does not represent payroll.
```

---

## 8.23 SNAP-001 — SupportMetricSnapshot

Classification: `MUST_IMPLEMENT_IN_PHASE_40`

Purpose:

```text
Immutable support operations snapshot.
```

Metrics:

```text
openCases
newCases
resolvedCases
closedCases
slaAtRisk
slaBreached
criticalIncidents
averageFirstResponseTime
averageResolutionTime
maintenanceWindowsPlanned
supportEffortHours
supportCostInput
```

Rules:

```text
1. Snapshot belongs to workspace/project/service profile.
2. Snapshot immutable.
3. Snapshot feeds dashboard/report.
```

---

## 8.24 DASH-001 — ServiceDashboard

Classification: `MUST_IMPLEMENT_IN_PHASE_40`

Dashboard views:

```text
workspace support dashboard
project support dashboard
client support dashboard
SLA dashboard
incident dashboard
maintenance dashboard
support workload dashboard
```

Metrics:

```text
open support cases
cases by priority/status/type
SLA at risk/breached
incident severity
maintenance schedule
support effort
support cost input
client satisfaction future
```

Rules:

```text
1. Dashboard respects permissions.
2. Client portal dashboard is client-safe.
3. Sensitive cost hidden unless permission.
```

---

## 8.25 PORTAL-001 — Client support portal

Classification: `MUST_IMPLEMENT_IN_PHASE_40` if Phase 30 exists.

Portal capabilities:

```text
submit support request
view own/client cases
add client-visible reply
upload attachment
see case status
see SLA target if enabled
see maintenance notice if enabled
see incident client-visible updates
```

Rules:

```text
1. Portal user can only see granted client/project scope.
2. Internal comments hidden.
3. Internal cost/resource/profit hidden.
4. Case type must be portal-visible.
5. Portal uploads follow Document Hub/trust rules.
```

---

## 8.26 ESC-001 — SupportEscalationRule

Classification: `MUST_IMPLEMENT_IN_PHASE_40`

Purpose:

```text
Notify the right people when case/SLA/incident needs attention.
```

Escalation triggers:

```text
SLA_DUE_SOON
SLA_BREACHED
CRITICAL_CASE_CREATED
INCIDENT_SEV1_CREATED
CASE_UNASSIGNED_TOO_LONG
CLIENT_WAITING_TOO_LONG
MAINTENANCE_WINDOW_MISSED
```

Rules:

```text
1. Escalation is notification/routing, not approval.
2. Rule belongs to workspace/service profile/queue.
3. Rule creates notification/task if configured.
4. Escalation actions audited.
```

---

## 8.27 KB-001 — SupportKnowledgeLink

Classification: `MUST_IMPLEMENT_IN_PHASE_40`

Purpose:

```text
Link support cases/problems to knowledge documents.
```

Rules:

```text
1. Knowledge article can be Document Hub document.
2. Link can be internal or client-visible.
3. Resolved cases can suggest knowledge creation.
4. Search can use linked knowledge.
```

---

## 8.28 AUTO-001 — Automatic routing and classification

Classification: `MUST_IMPLEMENT_IN_PHASE_40`

System can auto-suggest:

```text
case type
priority
queue
SLA policy
related project/deliverable/release
duplicate case
related knowledge article
```

Rules:

```text
1. Auto-classification can be suggestion or default.
2. User can override with permission.
3. Changes audited.
4. AI suggestions are not final without user confirmation if risk.
```

---

## 8.29 NOTIF-001 — Support notifications

Classification: `MUST_IMPLEMENT_IN_PHASE_40` if Phase 35 exists.

Notifications:

```text
case created
case assigned
client replied
internal comment added
SLA due soon
SLA breached
incident created
incident update
maintenance window scheduled
maintenance completed
case resolved
case reopened
```

Rules:

```text
1. Sensitive content masked.
2. Portal notifications client-safe.
3. Duplicate notifications deduplicated.
4. Quiet hours respected unless critical policy.
```

---

## 8.30 AI-001 — AI-assisted support summary

Classification: `SEED_ONLY_IN_PHASE_40` or `MUST_IMPLEMENT_IN_PHASE_40` if Phase 21 exists.

AI can help with:

```text
summarize support case
suggest case type/priority
detect duplicate cases
suggest knowledge article
summarize incident timeline
draft client update
explain SLA breach
suggest next action
```

Rules:

```text
1. AI output is suggestion/draft.
2. AI cannot close case automatically.
3. AI cannot expose internal notes to client.
4. AI cannot change priority/SLA without confirmation.
```

---

# 9. Entity model TO-BE

---

## 9.1 ServiceProfile — `service_profile`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
project_id UUID NULL
external_organization_id UUID NULL
profile_scope VARCHAR(50) NOT NULL
name VARCHAR(255) NOT NULL
support_owner_user_id UUID NULL
default_queue_id UUID NULL
default_sla_policy_id UUID NULL
portal_intake_enabled BOOLEAN NOT NULL DEFAULT false
service_start_date DATE NULL
service_end_date DATE NULL
warranty_start_date DATE NULL
warranty_end_date DATE NULL
status VARCHAR(50) NOT NULL
notes TEXT NULL
created_at / created_by
updated_at / updated_by
archived_at / archived_by
trace_id VARCHAR(100) NULL
version INT
```

---

## 9.2 ServiceRequestType — `service_request_type`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
type_code VARCHAR(150) NOT NULL
name VARCHAR(255) NOT NULL
description TEXT NULL
default_priority VARCHAR(50) NULL
default_queue_id UUID NULL
default_sla_policy_id UUID NULL
portal_visible BOOLEAN NOT NULL DEFAULT false
enabled BOOLEAN NOT NULL DEFAULT true
created_at / created_by
updated_at / updated_by
archived_at / archived_by
version INT
```

Constraint:

```text
unique workspace_id + type_code
```

---

## 9.3 SupportCase — `support_case`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
case_number VARCHAR(150) NULL
service_profile_id UUID NULL
project_id UUID NULL
external_organization_id UUID NULL
external_contact_id UUID NULL
portal_account_id UUID NULL
request_type_id UUID NOT NULL
source VARCHAR(100) NOT NULL
title VARCHAR(500) NOT NULL
description TEXT NULL
priority VARCHAR(50) NOT NULL
status VARCHAR(50) NOT NULL
queue_id UUID NULL
owner_user_id UUID NULL
client_visible BOOLEAN NOT NULL DEFAULT false
internal_only BOOLEAN NOT NULL DEFAULT false
sla_policy_id UUID NULL
first_response_at TIMESTAMP NULL
resolved_at TIMESTAMP NULL
closed_at TIMESTAMP NULL
cancelled_at TIMESTAMP NULL
cancellation_reason TEXT NULL
created_at / created_by
updated_at / updated_by
archived_at / archived_by
trace_id VARCHAR(100) NULL
version INT
```

Constraint:

```text
unique workspace_id + case_number where case_number not null
```

---

## 9.4 SupportStatusHistory — `support_status_history`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
support_case_id UUID NOT NULL
from_status VARCHAR(50) NULL
to_status VARCHAR(50) NOT NULL
reason TEXT NULL
changed_at TIMESTAMP NOT NULL
changed_by UUID NULL
visibility VARCHAR(50) NOT NULL
trace_id VARCHAR(100) NULL
version INT
```

---

## 9.5 SupportQueue — `support_queue`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
queue_code VARCHAR(150) NOT NULL
name VARCHAR(255) NOT NULL
description TEXT NULL
owner_user_id UUID NULL
owner_team_id UUID NULL
default_sla_policy_id UUID NULL
status VARCHAR(50) NOT NULL
created_at / created_by
updated_at / updated_by
archived_at / archived_by
version INT
```

Constraint:

```text
unique workspace_id + queue_code
```

---

## 9.6 SupportAssignment — `support_assignment`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
support_case_id UUID NOT NULL
assignment_type VARCHAR(50) NOT NULL
assigned_user_id UUID NULL
assigned_team_id UUID NULL
resource_profile_id UUID NULL
status VARCHAR(50) NOT NULL
assigned_at TIMESTAMP NOT NULL
assigned_by UUID NULL
removed_at TIMESTAMP NULL
removed_by UUID NULL
version INT
```

---

## 9.7 SupportComment — `support_comment`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
support_case_id UUID NOT NULL
author_user_id UUID NULL
author_portal_account_id UUID NULL
visibility VARCHAR(50) NOT NULL
body TEXT NOT NULL
system_generated BOOLEAN NOT NULL DEFAULT false
created_at TIMESTAMP NOT NULL
updated_at TIMESTAMP NULL
deleted_at TIMESTAMP NULL
trace_id VARCHAR(100) NULL
version INT
```

Visibility:

```text
INTERNAL
CLIENT_VISIBLE
SYSTEM
```

---

## 9.8 SupportAttachment — `support_attachment`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
support_case_id UUID NOT NULL
comment_id UUID NULL
document_id UUID NULL
document_version_id UUID NULL
visibility VARCHAR(50) NOT NULL
uploaded_by_user_id UUID NULL
uploaded_by_portal_account_id UUID NULL
created_at TIMESTAMP NOT NULL
version INT
```

---

## 9.9 SupportSlaPolicy — `support_sla_policy`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
policy_code VARCHAR(150) NOT NULL
name VARCHAR(255) NOT NULL
description TEXT NULL
business_calendar_id UUID NULL
pause_statuses_json JSONB NULL
enabled BOOLEAN NOT NULL DEFAULT true
created_at / created_by
updated_at / updated_by
archived_at / archived_by
version INT
```

Constraint:

```text
unique workspace_id + policy_code
```

---

## 9.10 SlaTarget — `support_sla_target`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
sla_policy_id UUID NOT NULL
request_type_id UUID NULL
priority VARCHAR(50) NULL
target_type VARCHAR(50) NOT NULL
duration_minutes INT NOT NULL
enabled BOOLEAN NOT NULL DEFAULT true
created_at / created_by
updated_at / updated_by
version INT
```

Target types:

```text
TRIAGE
FIRST_RESPONSE
NEXT_RESPONSE
RESOLUTION
```

---

## 9.11 SlaClock — `support_sla_clock`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
support_case_id UUID NOT NULL
sla_target_id UUID NOT NULL
target_type VARCHAR(50) NOT NULL
status VARCHAR(50) NOT NULL
started_at TIMESTAMP NULL
paused_at TIMESTAMP NULL
resumed_at TIMESTAMP NULL
due_at TIMESTAMP NULL
completed_at TIMESTAMP NULL
breached_at TIMESTAMP NULL
elapsed_minutes INT NOT NULL DEFAULT 0
remaining_minutes INT NULL
trace_id VARCHAR(100) NULL
version INT
```

---

## 9.12 SlaBreach — `support_sla_breach`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
support_case_id UUID NOT NULL
sla_clock_id UUID NOT NULL
target_type VARCHAR(50) NOT NULL
due_at TIMESTAMP NOT NULL
breached_at TIMESTAMP NOT NULL
resolved_at TIMESTAMP NULL
severity VARCHAR(50) NOT NULL
notified BOOLEAN NOT NULL DEFAULT false
created_at TIMESTAMP NOT NULL
trace_id VARCHAR(100) NULL
version INT
```

---

## 9.13 IncidentRecord — `support_incident`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
incident_number VARCHAR(150) NULL
service_profile_id UUID NULL
project_id UUID NULL
external_organization_id UUID NULL
title VARCHAR(500) NOT NULL
description TEXT NULL
severity VARCHAR(50) NOT NULL
status VARCHAR(50) NOT NULL
impact_summary TEXT NULL
client_visible_summary TEXT NULL
owner_user_id UUID NULL
detected_at TIMESTAMP NULL
acknowledged_at TIMESTAMP NULL
resolved_at TIMESTAMP NULL
closed_at TIMESTAMP NULL
created_at / created_by
updated_at / updated_by
trace_id VARCHAR(100) NULL
version INT
```

---

## 9.14 IncidentTimelineEntry — `support_incident_timeline_entry`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
incident_id UUID NOT NULL
entry_type VARCHAR(100) NOT NULL
visibility VARCHAR(50) NOT NULL
message TEXT NOT NULL
occurred_at TIMESTAMP NOT NULL
created_at / created_by
trace_id VARCHAR(100) NULL
version INT
```

---

## 9.15 ProblemRecord — `support_problem`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
problem_number VARCHAR(150) NULL
project_id UUID NULL
service_profile_id UUID NULL
title VARCHAR(500) NOT NULL
description TEXT NULL
status VARCHAR(50) NOT NULL
root_cause_summary TEXT NULL
workaround TEXT NULL
owner_user_id UUID NULL
created_at / created_by
updated_at / updated_by
resolved_at / resolved_by
closed_at / closed_by
trace_id VARCHAR(100) NULL
version INT
```

---

## 9.16 MaintenancePlan — `maintenance_plan`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
service_profile_id UUID NULL
project_id UUID NULL
external_organization_id UUID NULL
plan_code VARCHAR(150) NULL
name VARCHAR(255) NOT NULL
plan_type VARCHAR(50) NOT NULL
description TEXT NULL
owner_user_id UUID NULL
schedule_json JSONB NULL
client_visible BOOLEAN NOT NULL DEFAULT false
status VARCHAR(50) NOT NULL
created_at / created_by
updated_at / updated_by
archived_at / archived_by
trace_id VARCHAR(100) NULL
version INT
```

---

## 9.17 MaintenanceWindow — `maintenance_window`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
maintenance_plan_id UUID NULL
service_profile_id UUID NULL
project_id UUID NULL
title VARCHAR(255) NOT NULL
description TEXT NULL
planned_start_at TIMESTAMP NOT NULL
planned_end_at TIMESTAMP NOT NULL
actual_start_at TIMESTAMP NULL
actual_end_at TIMESTAMP NULL
expected_impact TEXT NULL
client_visible_notice TEXT NULL
status VARCHAR(50) NOT NULL
created_at / created_by
updated_at / updated_by
cancelled_at / cancelled_by
cancellation_reason TEXT NULL
trace_id VARCHAR(100) NULL
version INT
```

---

## 9.18 MaintenanceActivity — `maintenance_activity`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
maintenance_window_id UUID NULL
maintenance_plan_id UUID NULL
service_profile_id UUID NULL
project_id UUID NULL
activity_type VARCHAR(100) NOT NULL
title VARCHAR(255) NOT NULL
description TEXT NULL
outcome_summary TEXT NULL
effort_hours DECIMAL(19,4) NULL
client_visible BOOLEAN NOT NULL DEFAULT false
performed_at TIMESTAMP NULL
performed_by UUID NULL
created_at / created_by
updated_at / updated_by
trace_id VARCHAR(100) NULL
version INT
```

---

## 9.19 WarrantyCoverage — `service_coverage`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
service_profile_id UUID NOT NULL
coverage_type VARCHAR(50) NOT NULL
name VARCHAR(255) NOT NULL
start_date DATE NOT NULL
end_date DATE NULL
included_request_types_json JSONB NULL
default_sla_policy_id UUID NULL
status VARCHAR(50) NOT NULL
created_at / created_by
updated_at / updated_by
archived_at / archived_by
version INT
```

---

## 9.20 ServiceHandoverPackage — `service_handover_package`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
project_id UUID NOT NULL
service_profile_id UUID NULL
package_code VARCHAR(150) NULL
title VARCHAR(255) NOT NULL
summary TEXT NULL
status VARCHAR(50) NOT NULL
client_visible BOOLEAN NOT NULL DEFAULT false
finalized_at TIMESTAMP NULL
finalized_by UUID NULL
created_at / created_by
updated_at / updated_by
archived_at / archived_by
trace_id VARCHAR(100) NULL
version INT
```

Status:

```text
DRAFT
READY
FINALIZED
SUPERSEDED
ARCHIVED
```

---

## 9.21 HandoverPackageItem — `service_handover_package_item`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
handover_package_id UUID NOT NULL
item_type VARCHAR(100) NOT NULL
target_object_type VARCHAR(100) NULL
target_object_id UUID NULL
document_id UUID NULL
title VARCHAR(255) NOT NULL
description TEXT NULL
client_visible BOOLEAN NOT NULL DEFAULT false
sort_order INT NOT NULL DEFAULT 0
created_at / created_by
version INT
```

---

## 9.22 SupportWorkLink — `support_work_link`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
support_case_id UUID NOT NULL
target_object_type VARCHAR(100) NOT NULL
target_object_id UUID NOT NULL
link_type VARCHAR(100) NOT NULL
created_at / created_by
trace_id VARCHAR(100) NULL
version INT
```

---

## 9.23 SupportEffortRecord — `support_effort_record`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
support_case_id UUID NULL
incident_id UUID NULL
maintenance_activity_id UUID NULL
resource_profile_id UUID NULL
effort_date DATE NOT NULL
effort_hours DECIMAL(19,4) NOT NULL
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

## 9.24 ServiceCostInput — `support_service_cost_input`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
project_id UUID NULL
service_profile_id UUID NULL
support_case_id UUID NULL
incident_id UUID NULL
maintenance_activity_id UUID NULL
resource_profile_id UUID NULL
source_type VARCHAR(100) NOT NULL
source_id UUID NULL
effort_hours DECIMAL(19,4) NULL
rate_amount DECIMAL(19,4) NULL
currency VARCHAR(10) NULL
cost_amount DECIMAL(19,4) NOT NULL
status VARCHAR(50) NOT NULL
created_at / created_by
updated_at / updated_by
trace_id VARCHAR(100) NULL
version INT
```

---

## 9.25 SupportMetricSnapshot — `support_metric_snapshot`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
project_id UUID NULL
service_profile_id UUID NULL
external_organization_id UUID NULL
period_start DATE NULL
period_end DATE NULL
open_cases INT NOT NULL DEFAULT 0
new_cases INT NOT NULL DEFAULT 0
resolved_cases INT NOT NULL DEFAULT 0
closed_cases INT NOT NULL DEFAULT 0
sla_at_risk INT NOT NULL DEFAULT 0
sla_breached INT NOT NULL DEFAULT 0
critical_incidents INT NOT NULL DEFAULT 0
avg_first_response_minutes DECIMAL(19,4) NULL
avg_resolution_minutes DECIMAL(19,4) NULL
maintenance_windows_planned INT NOT NULL DEFAULT 0
support_effort_hours DECIMAL(19,4) NOT NULL DEFAULT 0
support_cost_input DECIMAL(19,4) NULL
currency VARCHAR(10) NULL
snapshot_source VARCHAR(100) NOT NULL
snapshot_at TIMESTAMP NOT NULL
created_at TIMESTAMP NOT NULL
trace_id VARCHAR(100) NULL
version INT
```

---

## 9.26 SupportEscalationRule — `support_escalation_rule`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
service_profile_id UUID NULL
queue_id UUID NULL
rule_code VARCHAR(150) NOT NULL
name VARCHAR(255) NOT NULL
trigger_type VARCHAR(100) NOT NULL
condition_json JSONB NULL
target_user_ids_json JSONB NULL
target_team_ids_json JSONB NULL
notification_template_code VARCHAR(150) NULL
enabled BOOLEAN NOT NULL DEFAULT true
created_at / created_by
updated_at / updated_by
archived_at / archived_by
version INT
```

---

## 9.27 SupportKnowledgeLink — `support_knowledge_link`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
support_case_id UUID NULL
problem_id UUID NULL
incident_id UUID NULL
document_id UUID NULL
document_version_id UUID NULL
link_type VARCHAR(100) NOT NULL
client_visible BOOLEAN NOT NULL DEFAULT false
created_at / created_by
version INT
```

---

# 10. API TO-BE list

All APIs use `/api`.

---

## 10.1 Service profile APIs

```text
POST  /api/workspaces/{workspaceId}/service/profiles
GET   /api/workspaces/{workspaceId}/service/profiles
GET   /api/workspaces/{workspaceId}/service/profiles/{profileId}
PUT   /api/workspaces/{workspaceId}/service/profiles/{profileId}
POST  /api/workspaces/{workspaceId}/service/profiles/{profileId}/activate
POST  /api/workspaces/{workspaceId}/service/profiles/{profileId}/suspend
POST  /api/workspaces/{workspaceId}/service/profiles/{profileId}/end
PATCH /api/workspaces/{workspaceId}/service/profiles/{profileId}/archive
POST  /api/projects/{projectId}/service/profile/from-handover
```

---

## 10.2 Request type APIs

```text
POST  /api/workspaces/{workspaceId}/service/request-types
GET   /api/workspaces/{workspaceId}/service/request-types
PUT   /api/workspaces/{workspaceId}/service/request-types/{typeId}
PATCH /api/workspaces/{workspaceId}/service/request-types/{typeId}/archive
```

---

## 10.3 Support case APIs

```text
POST  /api/workspaces/{workspaceId}/support/cases
GET   /api/workspaces/{workspaceId}/support/cases
GET   /api/workspaces/{workspaceId}/support/cases/{caseId}
PUT   /api/workspaces/{workspaceId}/support/cases/{caseId}
POST  /api/workspaces/{workspaceId}/support/cases/{caseId}/triage
POST  /api/workspaces/{workspaceId}/support/cases/{caseId}/start
POST  /api/workspaces/{workspaceId}/support/cases/{caseId}/wait-client
POST  /api/workspaces/{workspaceId}/support/cases/{caseId}/wait-internal
POST  /api/workspaces/{workspaceId}/support/cases/{caseId}/resolve
POST  /api/workspaces/{workspaceId}/support/cases/{caseId}/close
POST  /api/workspaces/{workspaceId}/support/cases/{caseId}/cancel
PATCH /api/workspaces/{workspaceId}/support/cases/{caseId}/archive
GET   /api/workspaces/{workspaceId}/support/cases/{caseId}/status-history
```

Project-scoped list:

```text
GET /api/projects/{projectId}/support/cases
```

Client-scoped list:

```text
GET /api/workspaces/{workspaceId}/clients/{externalOrganizationId}/support/cases
```

---

## 10.4 Support queue / assignment APIs

```text
POST  /api/workspaces/{workspaceId}/support/queues
GET   /api/workspaces/{workspaceId}/support/queues
PUT   /api/workspaces/{workspaceId}/support/queues/{queueId}
PATCH /api/workspaces/{workspaceId}/support/queues/{queueId}/archive

POST   /api/workspaces/{workspaceId}/support/cases/{caseId}/assignments
GET    /api/workspaces/{workspaceId}/support/cases/{caseId}/assignments
DELETE /api/workspaces/{workspaceId}/support/cases/{caseId}/assignments/{assignmentId}
```

---

## 10.5 Support comment / attachment APIs

```text
POST /api/workspaces/{workspaceId}/support/cases/{caseId}/comments
GET  /api/workspaces/{workspaceId}/support/cases/{caseId}/comments
PUT  /api/workspaces/{workspaceId}/support/cases/{caseId}/comments/{commentId}
POST /api/workspaces/{workspaceId}/support/cases/{caseId}/comments/{commentId}/delete

POST /api/workspaces/{workspaceId}/support/cases/{caseId}/attachments
GET  /api/workspaces/{workspaceId}/support/cases/{caseId}/attachments
```

---

## 10.6 SLA APIs

```text
POST  /api/workspaces/{workspaceId}/support/sla-policies
GET   /api/workspaces/{workspaceId}/support/sla-policies
GET   /api/workspaces/{workspaceId}/support/sla-policies/{policyId}
PUT   /api/workspaces/{workspaceId}/support/sla-policies/{policyId}
PATCH /api/workspaces/{workspaceId}/support/sla-policies/{policyId}/archive

POST /api/workspaces/{workspaceId}/support/sla-policies/{policyId}/targets
GET  /api/workspaces/{workspaceId}/support/sla-policies/{policyId}/targets
PUT  /api/workspaces/{workspaceId}/support/sla-targets/{targetId}

GET  /api/workspaces/{workspaceId}/support/cases/{caseId}/sla-clocks
POST /api/workspaces/{workspaceId}/support/sla/recalculate
GET  /api/workspaces/{workspaceId}/support/sla/breaches
```

---

## 10.7 Incident APIs

```text
POST /api/workspaces/{workspaceId}/support/incidents
GET  /api/workspaces/{workspaceId}/support/incidents
GET  /api/workspaces/{workspaceId}/support/incidents/{incidentId}
PUT  /api/workspaces/{workspaceId}/support/incidents/{incidentId}
POST /api/workspaces/{workspaceId}/support/incidents/{incidentId}/acknowledge
POST /api/workspaces/{workspaceId}/support/incidents/{incidentId}/start-investigation
POST /api/workspaces/{workspaceId}/support/incidents/{incidentId}/start-mitigation
POST /api/workspaces/{workspaceId}/support/incidents/{incidentId}/monitor
POST /api/workspaces/{workspaceId}/support/incidents/{incidentId}/resolve
POST /api/workspaces/{workspaceId}/support/incidents/{incidentId}/close
POST /api/workspaces/{workspaceId}/support/incidents/{incidentId}/cancel

POST /api/workspaces/{workspaceId}/support/incidents/{incidentId}/timeline
GET  /api/workspaces/{workspaceId}/support/incidents/{incidentId}/timeline

POST /api/workspaces/{workspaceId}/support/incidents/{incidentId}/link-case/{caseId}
```

---

## 10.8 Problem APIs

```text
POST /api/workspaces/{workspaceId}/support/problems
GET  /api/workspaces/{workspaceId}/support/problems
GET  /api/workspaces/{workspaceId}/support/problems/{problemId}
PUT  /api/workspaces/{workspaceId}/support/problems/{problemId}
POST /api/workspaces/{workspaceId}/support/problems/{problemId}/define-workaround
POST /api/workspaces/{workspaceId}/support/problems/{problemId}/plan-fix
POST /api/workspaces/{workspaceId}/support/problems/{problemId}/resolve
POST /api/workspaces/{workspaceId}/support/problems/{problemId}/close
```

---

## 10.9 Maintenance APIs

```text
POST  /api/workspaces/{workspaceId}/maintenance/plans
GET   /api/workspaces/{workspaceId}/maintenance/plans
GET   /api/workspaces/{workspaceId}/maintenance/plans/{planId}
PUT   /api/workspaces/{workspaceId}/maintenance/plans/{planId}
PATCH /api/workspaces/{workspaceId}/maintenance/plans/{planId}/archive

POST /api/workspaces/{workspaceId}/maintenance/windows
GET  /api/workspaces/{workspaceId}/maintenance/windows
GET  /api/workspaces/{workspaceId}/maintenance/windows/{windowId}
PUT  /api/workspaces/{workspaceId}/maintenance/windows/{windowId}
POST /api/workspaces/{workspaceId}/maintenance/windows/{windowId}/confirm
POST /api/workspaces/{workspaceId}/maintenance/windows/{windowId}/start
POST /api/workspaces/{workspaceId}/maintenance/windows/{windowId}/complete
POST /api/workspaces/{workspaceId}/maintenance/windows/{windowId}/cancel

POST /api/workspaces/{workspaceId}/maintenance/activities
GET  /api/workspaces/{workspaceId}/maintenance/activities
GET  /api/workspaces/{workspaceId}/maintenance/activities/{activityId}
```

---

## 10.10 Coverage / handover APIs

```text
POST  /api/workspaces/{workspaceId}/service/profiles/{profileId}/coverages
GET   /api/workspaces/{workspaceId}/service/profiles/{profileId}/coverages
PUT   /api/workspaces/{workspaceId}/service/coverages/{coverageId}
PATCH /api/workspaces/{workspaceId}/service/coverages/{coverageId}/archive

POST /api/projects/{projectId}/service/handover-packages
GET  /api/projects/{projectId}/service/handover-packages
GET  /api/projects/{projectId}/service/handover-packages/{packageId}
PUT  /api/projects/{projectId}/service/handover-packages/{packageId}
POST /api/projects/{projectId}/service/handover-packages/{packageId}/finalize
POST /api/projects/{projectId}/service/handover-packages/{packageId}/supersede

POST /api/projects/{projectId}/service/handover-packages/{packageId}/items
GET  /api/projects/{projectId}/service/handover-packages/{packageId}/items
DELETE /api/projects/{projectId}/service/handover-packages/{packageId}/items/{itemId}
```

---

## 10.11 Work link APIs

```text
POST   /api/workspaces/{workspaceId}/support/cases/{caseId}/work-links
GET    /api/workspaces/{workspaceId}/support/cases/{caseId}/work-links
DELETE /api/workspaces/{workspaceId}/support/cases/{caseId}/work-links/{linkId}

POST /api/workspaces/{workspaceId}/support/cases/{caseId}/create-task
POST /api/workspaces/{workspaceId}/support/cases/{caseId}/create-defect
POST /api/workspaces/{workspaceId}/support/cases/{caseId}/create-change-request
POST /api/workspaces/{workspaceId}/support/cases/{caseId}/link-release/{releaseId}
```

---

## 10.12 Effort / cost APIs

```text
POST /api/workspaces/{workspaceId}/support/effort-records
GET  /api/workspaces/{workspaceId}/support/effort-records
PUT  /api/workspaces/{workspaceId}/support/effort-records/{effortRecordId}
POST /api/workspaces/{workspaceId}/support/effort-records/{effortRecordId}/cancel

GET  /api/workspaces/{workspaceId}/support/service-cost-inputs
POST /api/workspaces/{workspaceId}/support/service-cost-inputs/rebuild
```

---

## 10.13 Snapshot / dashboard APIs

```text
POST /api/workspaces/{workspaceId}/support/metric-snapshots
GET  /api/workspaces/{workspaceId}/support/metric-snapshots

GET /api/workspaces/{workspaceId}/support/dashboard
GET /api/projects/{projectId}/support/dashboard
GET /api/workspaces/{workspaceId}/clients/{externalOrganizationId}/support/dashboard
GET /api/workspaces/{workspaceId}/support/sla-dashboard
GET /api/workspaces/{workspaceId}/support/incident-dashboard
GET /api/workspaces/{workspaceId}/maintenance/dashboard
```

---

## 10.14 Escalation / knowledge APIs

```text
POST  /api/workspaces/{workspaceId}/support/escalation-rules
GET   /api/workspaces/{workspaceId}/support/escalation-rules
PUT   /api/workspaces/{workspaceId}/support/escalation-rules/{ruleId}
PATCH /api/workspaces/{workspaceId}/support/escalation-rules/{ruleId}/archive

POST   /api/workspaces/{workspaceId}/support/knowledge-links
GET    /api/workspaces/{workspaceId}/support/knowledge-links
DELETE /api/workspaces/{workspaceId}/support/knowledge-links/{linkId}
```

---

## 10.15 Portal support APIs

If Phase 30 exists:

```text
POST /api/portal/support/cases
GET  /api/portal/support/cases
GET  /api/portal/support/cases/{caseId}
POST /api/portal/support/cases/{caseId}/comments
POST /api/portal/support/cases/{caseId}/attachments

GET /api/portal/support/request-types
GET /api/portal/support/maintenance-windows
GET /api/portal/support/incidents
GET /api/portal/support/dashboard
```

---

## 10.16 Reports APIs

```text
GET /api/workspaces/{workspaceId}/reports/support/summary
GET /api/workspaces/{workspaceId}/reports/support/cases
GET /api/workspaces/{workspaceId}/reports/support/sla
GET /api/workspaces/{workspaceId}/reports/support/incidents
GET /api/workspaces/{workspaceId}/reports/support/problems
GET /api/workspaces/{workspaceId}/reports/support/maintenance
GET /api/workspaces/{workspaceId}/reports/support/effort
GET /api/workspaces/{workspaceId}/reports/support/cost-input
GET /api/projects/{projectId}/reports/support/project-support
GET /api/workspaces/{workspaceId}/clients/{externalOrganizationId}/reports/support/client-support
```

---

# 11. Authorization requirements

Required authorities:

```text
SERVICE_PROFILE_VIEW
SERVICE_PROFILE_CREATE
SERVICE_PROFILE_UPDATE
SERVICE_PROFILE_ACTIVATE
SERVICE_PROFILE_SUSPEND
SERVICE_PROFILE_END
SERVICE_PROFILE_ARCHIVE

SERVICE_REQUEST_TYPE_VIEW
SERVICE_REQUEST_TYPE_MANAGE

SUPPORT_CASE_VIEW
SUPPORT_CASE_CREATE
SUPPORT_CASE_UPDATE
SUPPORT_CASE_TRIAGE
SUPPORT_CASE_ASSIGN
SUPPORT_CASE_STATUS_UPDATE
SUPPORT_CASE_RESOLVE
SUPPORT_CASE_CLOSE
SUPPORT_CASE_CANCEL
SUPPORT_CASE_ARCHIVE
SUPPORT_CASE_INTERNAL_VIEW
SUPPORT_CASE_CLIENT_VISIBLE_MANAGE

SUPPORT_QUEUE_VIEW
SUPPORT_QUEUE_MANAGE
SUPPORT_ASSIGNMENT_VIEW
SUPPORT_ASSIGNMENT_MANAGE

SUPPORT_COMMENT_VIEW
SUPPORT_COMMENT_CREATE
SUPPORT_COMMENT_UPDATE
SUPPORT_COMMENT_DELETE
SUPPORT_INTERNAL_COMMENT_VIEW
SUPPORT_CLIENT_COMMENT_CREATE

SUPPORT_SLA_VIEW
SUPPORT_SLA_MANAGE
SUPPORT_SLA_RECALCULATE
SUPPORT_SLA_BREACH_VIEW

INCIDENT_VIEW
INCIDENT_CREATE
INCIDENT_UPDATE
INCIDENT_STATUS_UPDATE
INCIDENT_INTERNAL_VIEW
INCIDENT_CLIENT_VISIBLE_MANAGE

PROBLEM_VIEW
PROBLEM_CREATE
PROBLEM_UPDATE
PROBLEM_STATUS_UPDATE

MAINTENANCE_PLAN_VIEW
MAINTENANCE_PLAN_MANAGE
MAINTENANCE_WINDOW_VIEW
MAINTENANCE_WINDOW_MANAGE
MAINTENANCE_ACTIVITY_VIEW
MAINTENANCE_ACTIVITY_CREATE

SERVICE_COVERAGE_VIEW
SERVICE_COVERAGE_MANAGE
SERVICE_HANDOVER_VIEW
SERVICE_HANDOVER_CREATE
SERVICE_HANDOVER_UPDATE
SERVICE_HANDOVER_FINALIZE

SUPPORT_WORK_LINK_VIEW
SUPPORT_WORK_LINK_MANAGE
SUPPORT_CREATE_TASK
SUPPORT_CREATE_DEFECT
SUPPORT_CREATE_CHANGE_REQUEST

SUPPORT_EFFORT_VIEW
SUPPORT_EFFORT_CREATE
SUPPORT_EFFORT_UPDATE
SUPPORT_EFFORT_CANCEL
SUPPORT_COST_INPUT_VIEW
SUPPORT_COST_INPUT_REBUILD
SUPPORT_COST_SENSITIVE_VIEW

SUPPORT_DASHBOARD_VIEW
SUPPORT_REPORT_VIEW
SUPPORT_REPORT_EXPORT

SUPPORT_ESCALATION_RULE_VIEW
SUPPORT_ESCALATION_RULE_MANAGE
SUPPORT_KNOWLEDGE_LINK_VIEW
SUPPORT_KNOWLEDGE_LINK_MANAGE
```

Portal authorities:

```text
PORTAL_SUPPORT_CASE_CREATE
PORTAL_SUPPORT_CASE_VIEW
PORTAL_SUPPORT_CASE_COMMENT
PORTAL_SUPPORT_ATTACHMENT_UPLOAD
PORTAL_MAINTENANCE_VIEW
PORTAL_INCIDENT_VIEW
PORTAL_SUPPORT_DASHBOARD_VIEW
```

Rules:

```text
1. Support access requires workspace/project/client scope.
2. Portal users only access client-visible cases in granted scope.
3. Internal notes hidden without internal permission.
4. Support assignment does not grant object access by itself.
5. Cost/effort/rate data requires sensitive permission.
6. SLA policy management requires admin/service owner permission.
```

---

# 12. Lifecycle rules

## 12.1 SupportCase lifecycle

```text
NEW → TRIAGED → IN_PROGRESS → RESOLVED → CLOSED
NEW/TRIAGED/IN_PROGRESS → WAITING_FOR_CLIENT
NEW/TRIAGED/IN_PROGRESS → WAITING_INTERNAL
NEW/TRIAGED/IN_PROGRESS → WAITING_VENDOR
WAITING_* → IN_PROGRESS
NEW/TRIAGED/IN_PROGRESS/WAITING_* → CANCELLED
Any → ARCHIVED
```

Rules:

```text
1. Resolve requires resolution summary.
2. Close can be manual or auto-close after policy.
3. Reopen can be supported by moving CLOSED/RESOLVED to IN_PROGRESS if policy.
4. Status changes audited.
```

## 12.2 SLA lifecycle

```text
NOT_STARTED → RUNNING
RUNNING → PAUSED
PAUSED → RUNNING
RUNNING → MET
RUNNING → BREACHED
RUNNING/PAUSED → CANCELLED
```

## 12.3 Incident lifecycle

```text
OPEN → INVESTIGATING → MITIGATING → MONITORING → RESOLVED → CLOSED
OPEN/INVESTIGATING/MITIGATING → CANCELLED
```

## 12.4 Problem lifecycle

```text
OPEN → UNDER_ANALYSIS → WORKAROUND_DEFINED → FIX_PLANNED → RESOLVED → CLOSED
Any → ARCHIVED
```

## 12.5 MaintenanceWindow lifecycle

```text
PLANNED → CONFIRMED → IN_PROGRESS → COMPLETED
PLANNED/CONFIRMED → CANCELLED
PLANNED/CONFIRMED → MISSED
Any → ARCHIVED
```

## 12.6 Handover lifecycle

```text
DRAFT → READY → FINALIZED
FINALIZED → SUPERSEDED
Any → ARCHIVED
```

Finalized handover package is locked.

---

# 13. SLA calculation rules

SLA target should calculate due time using:

```text
case created/triaged/start time
target duration
business calendar
pause statuses
case priority
request type
queue/service profile policy
```

Rules:

```text
1. FIRST_RESPONSE completed when first internal/client-visible response is sent.
2. RESOLUTION completed when case status becomes RESOLVED.
3. SLA can pause in WAITING_FOR_CLIENT if policy.
4. SLA breach job should be idempotent.
5. SLA due soon notification threshold configurable.
```

Example:

```text
Priority CRITICAL first response: 30 minutes
Priority HIGH first response: 2 hours
Priority NORMAL first response: 8 business hours
Resolution target depends on request type and policy
```

---

# 14. Support routing rules

Routing may use:

```text
request type
priority
client
project
service profile
queue defaults
component/module
availability/capacity future
```

Rules:

```text
1. Routing can suggest queue/owner.
2. System can auto-assign if policy allows.
3. User can override with permission.
4. Assignment does not grant access automatically.
5. Routing decision logged.
```

---

# 15. Integration rules

## 15.1 Project/task integration

Rules:

```text
1. Support case can create task.
2. Created task links back to support case.
3. Task progress can update support case if policy.
4. Closing support case should not auto-close task unless configured.
```

## 15.2 Quality/defect integration

Rules:

```text
1. Bug report can create defect.
2. Defect status can update support case.
3. Defect rework effort can feed support effort/cost.
4. Critical defect can create incident.
```

## 15.3 Release integration

Rules:

```text
1. Support case can link to release.
2. Incident fix can be linked to patch release.
3. Maintenance window can link to release deployment.
4. Release note can be linked to support case.
```

## 15.4 Change request integration

Rules:

```text
1. Support case can create ChangeRequest if it is out-of-scope work.
2. ChangeRequest outcome can update support case.
3. Support case should not silently become billable/commercial item.
```

## 15.5 Document integration

Rules:

```text
1. Attachments stored via Document Hub.
2. Knowledge links point to documents.
3. Handover package links documents.
4. Restricted documents remain protected.
```

## 15.6 Portal integration

Rules:

```text
1. Portal users can submit cases.
2. Portal users see client-visible comments/status only.
3. Portal attachments follow Document Hub/trust.
4. Portal incident/maintenance views are optional and controlled.
```

## 15.7 Resource integration

Rules:

```text
1. SupportEffortRecord can create/update Phase 37 ActualEffortRecord.
2. Support assignment can reference ResourceProfile.
3. Support load can affect utilization.
4. Incident/maintenance effort can affect workload.
```

## 15.8 Profitability integration

Rules:

```text
1. ServiceCostInput can feed Phase 36 CostForecast.
2. Support cost can affect project profitability.
3. Cost values are sensitive.
4. Warranty support cost can be tracked as cost impact.
```

## 15.9 Trust integration

Rules:

```text
1. Support cases can contain sensitive client data.
2. Client-visible/internal visibility must be respected.
3. Portal access logged where sensitive.
4. Export of support data audited.
5. Retention/legal hold can apply to support records.
```

## 15.10 Integration hub integration

Possible provider use cases:

```text
email-to-case future
Slack/Teams support notifications
external helpdesk import/export future
status page integration future
calendar maintenance windows
```

Rules:

```text
Do not claim provider-specific support integrations unless adapter and tests exist.
```

---

# 16. Event Registry integration

Recommended source system:

```text
SCOPERY_SERVICE
```

Required events:

```text
SERVICE_PROFILE_CREATED
SERVICE_PROFILE_ACTIVATED
SERVICE_PROFILE_SUSPENDED
SERVICE_PROFILE_ENDED
SERVICE_PROFILE_ARCHIVED

SERVICE_REQUEST_TYPE_CREATED
SERVICE_REQUEST_TYPE_UPDATED
SERVICE_REQUEST_TYPE_ARCHIVED

SUPPORT_CASE_CREATED
SUPPORT_CASE_TRIAGED
SUPPORT_CASE_STARTED
SUPPORT_CASE_WAITING_FOR_CLIENT
SUPPORT_CASE_WAITING_INTERNAL
SUPPORT_CASE_WAITING_VENDOR
SUPPORT_CASE_RESOLVED
SUPPORT_CASE_CLOSED
SUPPORT_CASE_CANCELLED
SUPPORT_CASE_REOPENED
SUPPORT_CASE_ARCHIVED
SUPPORT_CASE_PRIORITY_CHANGED
SUPPORT_CASE_ASSIGNED
SUPPORT_CASE_UNASSIGNED
SUPPORT_CASE_COMMENT_ADDED
SUPPORT_CASE_ATTACHMENT_ADDED
SUPPORT_CASE_WORK_LINKED

SUPPORT_QUEUE_CREATED
SUPPORT_QUEUE_UPDATED
SUPPORT_QUEUE_ARCHIVED

SLA_POLICY_CREATED
SLA_POLICY_UPDATED
SLA_CLOCK_STARTED
SLA_CLOCK_PAUSED
SLA_CLOCK_RESUMED
SLA_TARGET_MET
SLA_BREACHED
SLA_DUE_SOON

INCIDENT_CREATED
INCIDENT_ACKNOWLEDGED
INCIDENT_INVESTIGATING
INCIDENT_MITIGATING
INCIDENT_MONITORING
INCIDENT_RESOLVED
INCIDENT_CLOSED
INCIDENT_TIMELINE_ENTRY_ADDED

PROBLEM_CREATED
PROBLEM_WORKAROUND_DEFINED
PROBLEM_FIX_PLANNED
PROBLEM_RESOLVED
PROBLEM_CLOSED

MAINTENANCE_PLAN_CREATED
MAINTENANCE_PLAN_UPDATED
MAINTENANCE_WINDOW_SCHEDULED
MAINTENANCE_WINDOW_CONFIRMED
MAINTENANCE_WINDOW_STARTED
MAINTENANCE_WINDOW_COMPLETED
MAINTENANCE_WINDOW_CANCELLED
MAINTENANCE_ACTIVITY_RECORDED

SERVICE_COVERAGE_CREATED
SERVICE_COVERAGE_UPDATED
SERVICE_HANDOVER_PACKAGE_CREATED
SERVICE_HANDOVER_PACKAGE_FINALIZED
SERVICE_HANDOVER_PACKAGE_SUPERSEDED

SUPPORT_EFFORT_RECORDED
SERVICE_COST_INPUT_REBUILT
SUPPORT_METRIC_SNAPSHOT_CREATED

SUPPORT_ESCALATION_TRIGGERED
SUPPORT_KNOWLEDGE_LINK_CREATED
PORTAL_SUPPORT_CASE_CREATED
PORTAL_SUPPORT_COMMENT_ADDED
SUPPORT_REPORT_EXPORTED
```

Standard variables:

```text
actor.userId
workspace.id
project.id
client.id
serviceProfile.id
supportCase.id
case.number
queue.id
priority
status
slaPolicy.id
slaClock.id
incident.id
problem.id
maintenancePlan.id
maintenanceWindow.id
handoverPackage.id
effort.hours
cost.amount
currency
occurredAt
traceId
```

---

# 17. Audit / activity / outbox

Audit-sensitive actions:

```text
portal support case submitted
internal comment added
client-visible comment added
case priority changed
case status changed
case assigned/unassigned
SLA policy changed
SLA breach recorded
incident severity changed
maintenance window changed
handover package finalized
support effort/cost recorded
support report exported
```

Activity actions:

```text
SUPPORT_CASE_CREATED
SUPPORT_CASE_ASSIGNED
SUPPORT_CASE_RESOLVED
SLA_BREACHED
INCIDENT_CREATED
MAINTENANCE_WINDOW_SCHEDULED
SERVICE_HANDOVER_PACKAGE_FINALIZED
```

Outbox required for:

```text
case created
case assigned
client replied
SLA due soon
SLA breached
incident created
incident update
maintenance window scheduled
maintenance completed
case resolved
```

Idempotency recommended for:

```text
portal case submit
create task/defect/change request from support case
SLA recalculation
SLA breach recording
support metric snapshot creation
service cost input rebuild
maintenance recurrence generation
```

---

# 18. Business rules master

## 18.1 Service/profile rules

```text
SERV-001 ServiceProfile controls support defaults.
SERV-002 ServiceProfile can be created from handover.
SERV-003 Suspended profile can block portal intake if policy.
```

## 18.2 Case rules

```text
CASE-001 Case requires type/priority/status.
CASE-002 Portal case must be client-safe.
CASE-003 Internal comments hidden from portal.
CASE-004 Status changes recorded.
CASE-005 Resolve requires resolution summary.
```

## 18.3 SLA rules

```text
SLA-001 SLA policy belongs to workspace.
SLA-002 SLA target can vary by request type/priority.
SLA-003 SLA clock can pause by status.
SLA-004 SLA breach creates event/notification.
SLA-005 SLA tracking is operational measurement.
```

## 18.4 Incident/problem rules

```text
INC-001 Incident can link multiple support cases.
INC-002 Incident timeline records major events.
PROB-001 Problem can link repeated incidents/cases.
PROB-002 Root cause notes internal by default.
```

## 18.5 Maintenance rules

```text
MAINT-001 Maintenance window has planned start/end.
MAINT-002 Maintenance completion can create activity.
MAINT-003 Client-visible notices explicit.
MAINT-004 Maintenance effort can feed cost.
```

## 18.6 Handover rules

```text
HAND-001 Handover package can link delivered artifacts.
HAND-002 Finalized handover package locked.
HAND-003 Client-visible items explicit.
```

## 18.7 Effort/cost rules

```text
EFF-001 Support effort is not payroll.
COST-001 Support cost is sensitive.
COST-002 Cost input can feed profitability.
COST-003 Missing rate creates warning, not crash.
```

---

# 19. Error catalog

```text
SERVICE_PROFILE_NOT_FOUND
SERVICE_PROFILE_INVALID_STATUS
SERVICE_PROFILE_ACCESS_DENIED
SERVICE_REQUEST_TYPE_NOT_FOUND
SERVICE_REQUEST_TYPE_NOT_PORTAL_VISIBLE

SUPPORT_CASE_NOT_FOUND
SUPPORT_CASE_INVALID_STATUS
SUPPORT_CASE_ACCESS_DENIED
SUPPORT_CASE_PORTAL_ACCESS_DENIED
SUPPORT_CASE_RESOLUTION_REQUIRED
SUPPORT_CASE_CANCELLATION_REASON_REQUIRED
SUPPORT_CASE_INTERNAL_FIELD_FORBIDDEN

SUPPORT_QUEUE_NOT_FOUND
SUPPORT_ASSIGNMENT_NOT_FOUND
SUPPORT_ASSIGNMENT_ACCESS_DENIED
SUPPORT_COMMENT_NOT_FOUND
SUPPORT_COMMENT_VISIBILITY_INVALID
SUPPORT_ATTACHMENT_NOT_FOUND

SLA_POLICY_NOT_FOUND
SLA_TARGET_NOT_FOUND
SLA_CLOCK_NOT_FOUND
SLA_RECALCULATION_FAILED
SLA_BREACH_NOT_FOUND

INCIDENT_NOT_FOUND
INCIDENT_INVALID_STATUS
INCIDENT_TIMELINE_ENTRY_NOT_FOUND
PROBLEM_NOT_FOUND
PROBLEM_INVALID_STATUS

MAINTENANCE_PLAN_NOT_FOUND
MAINTENANCE_WINDOW_NOT_FOUND
MAINTENANCE_WINDOW_INVALID_STATUS
MAINTENANCE_WINDOW_INVALID_DATE_RANGE
MAINTENANCE_ACTIVITY_NOT_FOUND

SERVICE_COVERAGE_NOT_FOUND
SERVICE_HANDOVER_PACKAGE_NOT_FOUND
SERVICE_HANDOVER_PACKAGE_IMMUTABLE
HANDOVER_ITEM_NOT_FOUND

SUPPORT_WORK_LINK_NOT_FOUND
SUPPORT_WORK_LINK_TARGET_INVALID
SUPPORT_CREATE_TASK_FAILED
SUPPORT_CREATE_DEFECT_FAILED
SUPPORT_CREATE_CHANGE_REQUEST_FAILED

SUPPORT_EFFORT_RECORD_NOT_FOUND
SUPPORT_EFFORT_INVALID_HOURS
SERVICE_COST_INPUT_REBUILD_FAILED
SUPPORT_COST_SENSITIVE_ACCESS_DENIED

SUPPORT_ESCALATION_RULE_NOT_FOUND
SUPPORT_KNOWLEDGE_LINK_NOT_FOUND
SUPPORT_REPORT_ACCESS_DENIED
```

---

# 20. Required tests

## 20.1 Service profile/type tests

```text
createServiceProfile_success
createServiceProfileFromHandover_success
activateServiceProfile_success
suspendServiceProfile_blocksPortalIntake_ifPolicy
createServiceRequestType_success
portalHiddenRequestType_notVisible
```

## 20.2 Support case tests

```text
createInternalSupportCase_success
createPortalSupportCase_success
portalCaseRequiresVisibleType
triageSupportCase_success
startSupportCase_success
resolveSupportCase_requiresResolution
closeSupportCase_success
cancelSupportCase_requiresReason
statusHistoryCreatedOnTransition
priorityChangeAudited
```

## 20.3 Queue/assignment/comment tests

```text
createSupportQueue_success
assignSupportCase_success
assignmentDoesNotGrantAccess
addInternalComment_hiddenFromPortal
addClientVisibleComment_visibleInPortal
attachmentUsesDocumentHub
clientReplyTriggersNotification
```

## 20.4 SLA tests

```text
createSlaPolicy_success
createSlaTarget_success
caseCreationStartsSlaClock
firstResponseMeetsSla
waitingForClientPausesClock_ifPolicy
slaBreachDetected
slaBreachNotificationCreated
slaRecalculationIdempotent
```

## 20.5 Incident/problem tests

```text
createIncident_success
linkSupportCaseToIncident_success
incidentTimelineEntry_success
incidentClientVisibleSummary_onlyVisibleWhenAllowed
createProblem_success
linkProblemToCases_success
problemRootCauseInternalByDefault
resolveProblem_success
```

## 20.6 Maintenance tests

```text
createMaintenancePlan_success
createMaintenanceWindow_success
confirmMaintenanceWindow_success
completeMaintenanceWindow_createsActivity
cancelMaintenanceWindow_requiresReason
maintenanceClientNotice_visibilityControlled
```

## 20.7 Coverage/handover tests

```text
createServiceCoverage_success
coverageAffectsDefaultSla_success
createHandoverPackage_success
addHandoverItem_success
finalizeHandoverPackage_locksPackage
clientVisibleHandoverItemsOnly
```

## 20.8 Work link tests

```text
linkCaseToTask_success
createTaskFromSupportCase_success
createDefectFromBugReport_success
createChangeRequestFromSupportCase_success
linkedWorkRespectsPermission
```

## 20.9 Effort/cost/profitability tests

```text
recordSupportEffort_success
supportEffortFeedsResourceActualEffort
rebuildServiceCostInput_success
serviceCostInputSensitiveMaskedWithoutPermission
serviceCostInputFeedsProfitabilityCost
missingRateCreatesWarningNotCrash
```

## 20.10 Portal tests

```text
portalSubmitCase_success
portalListOnlyClientVisibleCases
portalCannotSeeInternalComment
portalCanReplyClientVisible
portalCanViewMaintenanceNoticeWhenEnabled
portalCannotSeeCostOrInternalRca
```

## 20.11 Dashboard/report tests

```text
supportDashboardShowsOpenCases
slaDashboardShowsAtRiskAndBreached
incidentDashboardShowsSeverity
maintenanceDashboardShowsWindows
supportMetricSnapshotImmutable
supportReportExportAudited
```

## 20.12 Authorization tests

```text
viewSupportCaseWithoutPermission_forbidden
portalCrossClientCase_forbidden
manageSlaWithoutPermission_forbidden
viewInternalCommentWithoutPermission_forbidden
viewSupportCostWithoutSensitivePermission_forbidden
crossWorkspaceSupport_forbidden
```

## 20.13 Event tests

```text
serviceEventSeeder_firstRun_createsDefinitions
serviceEventSeeder_secondRun_noDuplicates
supportCaseCreated_eventEmitted
slaBreached_eventEmitted
incidentCreated_eventEmitted
maintenanceScheduled_eventEmitted
```

---

# 21. Manual verification checklist

Completion file must include:

```text
1. Create ServiceProfile for a completed project.
2. Create ServiceRequestType.
3. Submit support case internally.
4. Submit support case from portal.
5. Triage and assign support case.
6. Add internal comment and confirm portal cannot see it.
7. Add client-visible comment and confirm portal can see it.
8. Configure SLA policy and target.
9. Start SLA clock and trigger first response.
10. Force SLA breach and confirm notification.
11. Create incident from critical case.
12. Add incident timeline entry.
13. Create problem from recurring cases.
14. Create maintenance plan and window.
15. Complete maintenance window and record activity.
16. Create handover package and finalize it.
17. Create task/defect/change request from support case.
18. Record support effort.
19. Rebuild service cost input.
20. Confirm Phase 36 profitability can consume support cost.
21. Confirm portal sees only client-safe data.
22. Confirm support dashboard/report works.
```

---

# 22. Acceptance criteria

Phase 40 is accepted only if:

```text
1. Current support/service capability is classified against TO-BE.
2. ServiceProfile implemented/tested.
3. ServiceRequestType implemented/tested.
4. SupportCase implemented/tested.
5. SupportStatusHistory implemented/tested.
6. SupportQueue and SupportAssignment implemented/tested.
7. SupportComment/Attachment implemented/tested.
8. SupportSlaPolicy/SlaTarget/SlaClock/SlaBreach implemented/tested.
9. IncidentRecord and IncidentTimelineEntry implemented/tested.
10. ProblemRecord implemented/tested.
11. MaintenancePlan/Window/Activity implemented/tested.
12. WarrantyCoverage/ServiceCoverage implemented/tested.
13. ServiceHandoverPackage implemented/tested.
14. SupportWorkLink implemented/tested.
15. SupportEffortRecord and ServiceCostInput implemented/tested.
16. SupportMetricSnapshot/dashboard/report implemented/tested.
17. Portal support view implemented/tested if Phase 30 exists.
18. Escalation/notification integration implemented/tested.
19. IAM permissions implemented/tested.
20. Event seeders idempotent.
21. Activity/audit/outbox follows Phase 04.
22. Internal/client-visible separation enforced.
23. Sensitive cost/internal RCA hidden without permission.
24. `mvn compile` passes.
25. `mvn test` passes.
26. Completion file exists.
```

Do not mark complete if:

```text
portal can see internal comments
assignment grants access silently
SLA breach not tracked
support case cannot link to task/defect/change request
support effort/cost cannot feed resource/profitability
handover package finalization is editable silently
sensitive support cost visible without permission
tests fail
```

---

# 23. Required phase completion file

Agent must create:

```text
docs/phase-complete/PHASE_40_SERVICE_SUPPORT_MAINTENANCE_TO_BE_COMPLETE.md
```

Required sections:

```text
# Phase 40 — Service / Support / Maintenance Complete

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
## 11. Service Profile Strategy
## 12. Request Type Strategy
## 13. Support Case Strategy
## 14. Queue / Assignment Strategy
## 15. Comment / Attachment Strategy
## 16. SLA Strategy
## 17. Incident Strategy
## 18. Problem Strategy
## 19. Maintenance Strategy
## 20. Coverage / Handover Strategy
## 21. Work Link Strategy
## 22. Effort / Cost / Profitability Integration
## 23. Portal Support Strategy
## 24. Escalation / Notification Strategy
## 25. Reporting / Dashboard Strategy
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
## 36. Future Phases That Must Return to Service
```

---

# 24. Future phases that may return

```text
Phase 41 — Knowledge Graph / Semantic Index:
- Duplicate case detection, knowledge suggestions, AI support summary.

Future integration backlog:
- Email-to-case.
- Slack/Teams case intake.
- External helpdesk sync.
- Status page integration.
- Monitoring/APM incident import.

Future billing backlog:
- Support retainer billing.
- Maintenance package billing.
- Case-level billable/non-billable tracking.

Future mobile backlog:
- Mobile push for critical incidents.
- Field support mobile workflow.
```

---

# 25. Agent anti-bịa rules

The agent must not:

```text
1. Treat support as billing.
2. Treat support effort as payroll.
3. Treat SLA tracking as legal guarantee.
4. Expose internal comments to portal.
5. Expose internal RCA/cost/profit to client.
6. Let support assignment grant access silently.
7. Create task/defect/change request without permission.
8. Claim provider-specific helpdesk/email/chat integration without adapter/tests.
9. Let finalized handover package be silently edited.
10. Skip SLA breach event/notification tests.
```

---

# 26. Prompt to give coding agent

```text
You are implementing Phase 40 — TO-BE Service, Support, Maintenance, Incident, SLA Tracking & Post-Delivery Operations.

Product direction:
Scopery should support the operational life after project delivery.
Implement service profiles, support cases, queues, assignments, comments, SLA clocks, incidents, problems, maintenance windows, handover packages, portal support, support effort/cost input, dashboards, and notifications.
Support effort/cost should feed Phase 37 Resource and Phase 36 Profitability.
Do not turn this into billing, payroll, heavy ITSM, or external helpdesk replacement.

Before coding, read:
- CLAUDE.md / CLAUDE.ms
- Coding_convention.md
- Phase 00–39 docs and completion files
- Current backend code, migrations, tests

Your task:
1. Compare current service/support/maintenance capability against this Phase 40 TO-BE spec.
2. Classify each capability with required labels.
3. Implement ServiceProfile.
4. Implement ServiceRequestType.
5. Implement SupportCase and SupportStatusHistory.
6. Implement SupportQueue and SupportAssignment.
7. Implement SupportComment and SupportAttachment.
8. Implement SupportSlaPolicy, SlaTarget, SlaClock, and SlaBreach.
9. Implement IncidentRecord and IncidentTimelineEntry.
10. Implement ProblemRecord.
11. Implement MaintenancePlan, MaintenanceWindow, and MaintenanceActivity.
12. Implement WarrantyCoverage/ServiceCoverage.
13. Implement ServiceHandoverPackage and items.
14. Implement SupportWorkLink and creation of task/defect/change request from case.
15. Implement SupportEffortRecord and ServiceCostInput.
16. Implement SupportMetricSnapshot, dashboards, reports.
17. Implement portal support APIs if Phase 30 exists.
18. Implement escalation rules and notification integration.
19. Integrate with Project, Task, Quality, Release, ChangeRequest, DocumentHub, Resource, Profitability, Trust, Integration, Audit, Outbox, IAM.
20. Run mvn compile and mvn test.
21. Create docs/phase-complete/PHASE_40_SERVICE_SUPPORT_MAINTENANCE_TO_BE_COMPLETE.md.

Do not expose internal comments/cost/RCA to portal, treat SLA as legal guarantee, or claim provider-specific helpdesk integrations unless adapter and tests exist.
```

---

# 27. Quick tracking matrix

| Capability | Current backend | Phase 40 action | Later phase |
|---|---|---|---|
| ServiceProfile | Missing/unknown | Must implement | — |
| ServiceRequestType | Missing/unknown | Must implement | — |
| SupportCase | Missing/unknown | Must implement | — |
| SupportStatusHistory | Missing/unknown | Must implement | — |
| SupportQueue | Missing/unknown | Must implement | — |
| SupportAssignment | Missing/unknown | Must implement | — |
| SupportComment | Missing/unknown | Must implement | — |
| SupportAttachment | Missing/unknown | Must implement with DocumentHub | — |
| SLA Policy/Clock/Breach | Missing/unknown | Must implement | Advanced SLA later |
| IncidentRecord | Missing/unknown | Must implement | Monitoring integration later |
| IncidentTimelineEntry | Missing/unknown | Must implement | — |
| ProblemRecord | Missing/unknown | Must implement | Knowledge graph Phase 41 |
| MaintenancePlan | Missing/unknown | Must implement | Calendar integration later |
| MaintenanceWindow | Missing/unknown | Must implement | — |
| MaintenanceActivity | Missing/unknown | Must implement | — |
| ServiceCoverage | Missing/unknown | Must implement | Billing backlog later |
| HandoverPackage | Missing/partial | Must implement | — |
| SupportWorkLink | Missing/unknown | Must implement | — |
| SupportEffortRecord | Missing/unknown | Must implement | Phase 37 integration |
| ServiceCostInput | Missing/unknown | Must implement | Phase 36 integration |
| Portal support | Missing/partial | Must implement if portal exists | — |
| Support dashboard | Missing/unknown | Must implement | — |
| Escalation rule | Missing/unknown | Must implement notification-based | — |
| Email-to-case | Missing | Defer | Integration backlog |
| External helpdesk sync | Missing | Defer | Integration backlog |
| Status page/APM | Missing | Defer | Integration backlog |

---

# 28. Final principle

Phase 40 is not complete when "there is a ticket table."

Phase 40 is complete when Scopery can manage post-delivery work:

```text
client request
+ support case
+ queue/owner
+ SLA clock
+ incident/problem/maintenance link
+ task/defect/change link
+ portal-safe communication
+ support effort
+ service cost input
+ dashboard
+ audit
= service operations
```

Project delivery creates value.

Support protects that value.

Maintenance keeps it alive.
