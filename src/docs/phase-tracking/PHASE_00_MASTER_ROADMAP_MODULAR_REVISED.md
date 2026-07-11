# 00 — Scopery Backend Master Implementation Roadmap, Modular Architecture & Agent Control Spec

> **Purpose:** This is the single master tracking, architecture, and implementation-control file for the Scopery Backend.
>
> It is designed to be read by both humans and AI coding agents before implementing any backend phase.
>
> This file must be treated as the **source of planning truth** for backend implementation phases.
>
> Individual phase specs may be created later, but they must not contradict this file.
>
> **Important update:** Scopery is a loosely-coupled modular platform. Each business module must be usable independently with Core Platform only unless explicitly declared otherwise. Cross-module relationships are optional integrations, not hidden hard requirements.

---

## 0. How to use this file

This file has 6 purposes:

1. **Track what has already been implemented.**
2. **Define the total backend roadmap from Phase 0 onward.**
3. **Give AI coding agents enough structure to implement future phases without inventing logic.**
4. **Define quality gates, tests, events, seeders, completion files, and acceptance criteria.**
5. **Define Scopery's modular architecture principle.**
6. **Prevent future phases from becoming tightly coupled and unnecessarily heavy.**

Before any coding agent starts a phase, it must:

```text
1. Read CLAUDE.md / CLAUDE.ms if present.
2. Read Coding_convention.md if present.
3. Read this file fully.
4. Read the current BE feature/entity/business-rule inventory.
5. Read the target phase spec.
6. Inspect the actual codebase before coding.
7. Identify the next Flyway migration number.
8. Confirm the phase scope and out-of-scope items.
9. Confirm hard required core vs optional integrations.
10. Confirm standalone mode behavior.
11. Confirm graceful degradation behavior when optional modules are disabled.
12. Create or update the phase completion file after coding.
```

The agent must not code based only on assumptions.

If repo behavior differs from this document, the agent must stop and write the mismatch before continuing.

---

## 1. Current backend state snapshot

### 1.1 Current modules

The backend currently contains these main business modules:

| Module | Status | Role |
|---|---:|---|
| `iam` | Implemented | Users, roles, permission actions, grants, authorization engine |
| `workspace` | Implemented | Organization, workspace, members, teams, invitations, onboarding, context |
| `project` | Implemented foundation | Project, phase, WBS, task, dependency |
| `aiagent` | Implemented platform | AI provider, model, deployment, agent, prompt, execution, usage policy |
| `eventregistry` | Implemented | Event definitions and variables |
| `notification` | Implemented | Email templates, rules, delivery, outbox |
| `knowledge` | Minimal implemented | Document type catalog |

### 1.2 Current inventory

Current backend inventory:

| Metric | Current value |
|---|---:|
| Action-level features | 63 |
| Atomic business rules | 307 |
| Entities | 50 |
| Business modules | 7 |
| Project module features | 7 |
| Project module business rules | 42 |

### 1.3 Current project planning chain

The implemented project foundation currently supports:

```text
Organization
  → Workspace
    → Project
      → ProjectPhase ← PhaseDefinition
      → WbsNode tree
      → Task
        ↔ TaskDependency
```

This is enough for the **planning source of truth**, but not yet enough for capacity scheduling, Gantt, finance, quote, baseline, service support, integrations, trust/compliance, or AI-assisted planning.

### 1.4 Current coverage estimate

| Product area | Current coverage | Notes |
|---|---:|---|
| Platform architecture | 85–90% | DDD modular monolith, API conventions, Flyway, audit, outbox |
| IAM/Auth/Permission | 85–90% | Strong foundation; per-project resource not yet fully implemented |
| Workspace/Org/Team | 85–90% | Strong tenant model |
| Project core | 90–95% | Project, phase, WBS, task, dependency exist |
| WBS-driven planning | 65–70% | WBS and tasks exist, but no roll-up/capacity/Gantt yet |
| Capacity scheduling | 5–10% | Only assignee/member exists; no calendars/allocation/focus factor |
| Gantt | 10–15% | Dependency data exists; no Gantt projection yet |
| Rate card/CCH | 0–5% | `plannedRoleCode` only placeholder |
| Finance/margin/quote | 0–5% | No financial engine yet |
| Project events/notifications | 10–20% | Event/notification platform exists, not wired to project |
| AI-assisted project planning | 10–20% | AI platform exists, not wired to project planning |
| Document/knowledge | 5–15% | Minimal catalog only |
| Client portal | 0–5% | Not implemented or not confirmed |
| Support/service | 0–5% | Not implemented or not confirmed |
| Integrations | 0–5% | Foundation only if outbox/export exists |
| Trust/privacy/retention | 5–10% | Basic audit exists; advanced privacy not yet implemented |
| Overall BE product coverage | 45–50% | Good foundation, major product intelligence still missing |

---

## 2. Non-negotiable architecture rules

All future implementation must follow these rules.

---

### 2.1 Module architecture

The backend follows a DDD-oriented modular monolith.

Standard dependency direction:

```text
http → application → domain
infrastructure → domain
common/platform may be used by modules according to existing repo conventions
```

Prohibited:

```text
domain must not import jakarta.persistence
domain must not import org.springframework.web
controllers must not return JPA entities
controllers must not return domain aggregate objects directly
common/platform must not depend on modules unless existing convention explicitly allows it
```

---

### 2.2 Action and QueryService pattern

All write use cases must use:

```text
*Action.execute(Command)
```

Rules:

```text
- Command is a record.
- Command lives in application/command.
- Action lives in application/action.
- Action is transactional.
- Controller maps Request → Command.
- Controller must not pass Request directly to Action.
```

All read use cases must use:

```text
*QueryService
```

Rules:

```text
- Query services are read-only transactional.
- Search endpoints should support pagination when result can grow.
- Responses must be DTOs/records in application/response.
```

---

### 2.3 Error handling

Each module must have:

```text
shared/error/{Module}ErrorCatalog.java
shared/error/{Module}Exceptions.java
```

Rules:

```text
- Do not throw raw AppException constructors in actions.
- Do not throw generic IllegalStateException for business rules.
- Use module-specific error code.
- Use 404 for not found.
- Use 409 for uniqueness/conflict.
- Use 422 for business state violations.
- Use 400 for invalid input format/value.
- Use 403 for authorization denied.
```

---

### 2.4 Persistence rules

All database changes must use Flyway.

Rules:

```text
- No Hibernate auto-DDL reliance.
- Use next migration number.
- Use UUID primary keys.
- Use created_at / updated_at / created_by / updated_by according to repo convention.
- Use version INT optimistic locking for important mutable business entities.
- Use indexes for every FK/search/filter field.
- Use partial unique indexes where scoped uniqueness is required.
```

Constraint naming:

```text
pk_{table}
uq_{table}_{columns}
fk_{table}_{referenced_table}
idx_{table}_{columns}
ck_{table}_{rule}
```

---

### 2.5 API path rules

The product decision is:

```text
All backend API paths use /api as base.
No /api/v1 or /api/v2.
No backward-compatible alias unless explicitly requested later.
```

Security requirements:

```text
- Never use /api/** permitAll.
- Public endpoints must be listed explicitly.
- /api/iam/auth/** is CSRF-exempt.
- refresh_token cookie path is /api/iam/auth.
- protected POST/PUT/PATCH/DELETE require CSRF.
```

---

### 2.6 Tests are mandatory

For every phase, agent must run:

```bash
mvn compile
mvn test
```

A phase is not complete if tests fail, unless the completion file explicitly documents:

```text
- exact failing tests
- why they fail
- whether failure is pre-existing
- why it is safe or not safe
- follow-up task
```

---

## 3. Scopery modular architecture principle

This section is mandatory for all phases.

Earlier roadmap documents may list many phases as `Depends on`.

That does **not** mean every mentioned module is a hard runtime requirement.

The correct principle is:

```text
Scopery is a loosely-coupled modular platform.
```

Meaning:

```text
Each business module should be usable independently with Core Platform only.

Related modules may enrich the experience.

If a related module is disabled, unavailable, or not implemented, the current module must degrade gracefully.
```

Vietnamese product principle:

```text
Tách bạch để dùng riêng được.
Liên kết để khi cần thì mạnh hơn.
Không ràng buộc để sản phẩm không bị nặng.
```

English product principle:

```text
Standalone by default.
Integrated when available.
Graceful when missing.
```

---

## 3.1 Hard required core vs optional integrations

Every phase must separate dependencies into these groups.

### Hard required core

These are platform services a module can reasonably require:

```text
Workspace
User / Principal
Authentication
IAM / Permission
Basic authorization checks
Audit basics
Outbox / Idempotency basics
Event registry basics
Notification basics if the module sends notifications
File / Attachment basics if the module stores attachments
```

### Optional integrations

These are business modules that enrich the current module:

```text
Project
WBS
Task
Requirement
Scope / Deliverable
Change Request
Quality / Defect
Release
Document Hub
Client Portal
External Party / CRM
Resource / Capacity
Revenue / Profitability
Support / Maintenance
Integration Hub
Trust / Privacy
AI Assistant
Search
Custom Fields
```

Optional integration means:

```text
If module exists, current module can link to it.
If module does not exist, current module still works.
```

---

## 3.2 Required dependency wording for future phase specs

Avoid this wording:

```text
Depends on Phase 09, Phase 24, Phase 36, Phase 37...
```

Use this wording instead:

```text
Hard required core:
- Workspace
- IAM / Permission
- Audit / Outbox
- Event Registry

Standalone mode:
- What this module can do by itself

Optional integrations:
- Project: what becomes possible
- Task: what becomes possible
- Document: what becomes possible
- Portal: what becomes possible
- Resource: what becomes possible
- Profitability: what becomes possible
- Integration: what becomes possible
- AI: what becomes possible

Graceful degradation:
- What happens when optional modules are missing
```

If a phase still includes `Depends on`, interpret it as:

```text
Can integrate with / should inspect if available.
```

Not:

```text
Hard runtime requirement.
```

unless explicitly listed under **Hard required core**.

---

## 3.3 Module independence test

Every business module must pass this test:

```text
Can this module be used by a workspace that has only Core Platform plus this module?
```

If the answer is no, the module is too tightly coupled unless there is a strong product reason.

Examples:

```text
Support should work without Project.
Document Hub should work without Task.
Resource Capacity should work without Profitability.
Profitability should work without Resource, using manual cost inputs.
Meetings should work without Project, as standalone meetings.
Custom Fields should work without every object type, by enabling only supported targets.
Integration Hub should work without Support.
```

---

## 3.4 Graceful degradation rule

When an optional module is unavailable, disabled, or not configured, the current module must degrade gracefully.

Example — Support without Task:

```text
Available:
- Create support case
- Assign owner
- Add comments
- Track SLA
- Attach files
- Resolve/close case
- View support dashboard

Unavailable:
- Create task from support case
- Link task to support case
- Show task progress
```

System behavior:

```text
Hide task-related actions or return MODULE_CAPABILITY_NOT_ENABLED.
Do not break support case workflow.
```

Example — Profitability without Resource:

```text
Available:
- Revenue source
- Manual cost source
- Manual adjustment
- Profitability summary
- Margin forecast

Unavailable:
- Auto cost from effort/capacity
- Utilization-based cost forecast
- Resource cost input
```

System behavior:

```text
Profitability uses manual/blended/default cost assumptions.
No failure.
```

---

## 3.5 Module Registry and capability checking

Scopery should introduce or formalize:

```text
ModuleDefinition
WorkspaceModuleSetting
ModuleCapability
ModuleDependencyDeclaration
FeatureFlag
CapabilityCheckService
```

Example modules:

```text
CORE_WORKSPACE
CORE_IAM
CORE_AUDIT
PROJECT
TASK
DOCUMENT_HUB
CLIENT_PORTAL
CUSTOM_FIELDS
RESOURCE_CAPACITY
PROFITABILITY
SUPPORT_SERVICE
INTEGRATION_HUB
TRUST_PRIVACY
AI_ASSISTANT
```

Example capabilities:

```text
TASK.CREATE
TASK.LINK
TASK.SEARCH
SUPPORT.CREATE_CASE
SUPPORT.PORTAL_INTAKE
SUPPORT.CREATE_TASK_FROM_CASE
PROFITABILITY.COST_INPUT
RESOURCE.UTILIZATION
DOCUMENT.ATTACHMENT
PORTAL.EXTERNAL_VIEW
```

Every optional integration should check capability before use.

Example:

```text
if capability TASK.CREATE is enabled:
    allow create task from support case
else:
    return MODULE_CAPABILITY_NOT_ENABLED
```

---

## 3.6 Cross-module reference strategy

Use three levels of reference.

### Hard reference

Use hard FK when the object is part of the module's own core or platform core.

Example:

```text
support_case.workspace_id NOT NULL
support_case.request_type_id NOT NULL
```

### Nullable optional reference

Use nullable FK when integration is common but not required.

Example:

```text
support_case.project_id NULL
service_profile.external_organization_id NULL
```

### Generic link reference

Use object type + object id for flexible cross-module links.

Example:

```text
support_work_link:
- source_object_type
- source_object_id
- target_object_type
- target_object_id
- link_type
```

Rules:

```text
1. Generic links must be permission-checked.
2. Generic links must validate module capability if target module exists.
3. Broken links should be detectable by integrity check jobs.
4. Generic links must not bypass domain service rules.
```

Bad migration example:

```text
ALTER TABLE support_case ADD COLUMN task_id UUID NOT NULL REFERENCES task(id);
```

Good migration example:

```text
ALTER TABLE support_case ADD COLUMN project_id UUID NULL;

CREATE TABLE support_work_link (
  id UUID PRIMARY KEY,
  workspace_id UUID NOT NULL,
  support_case_id UUID NOT NULL,
  target_object_type VARCHAR(100) NOT NULL,
  target_object_id UUID NOT NULL,
  link_type VARCHAR(100) NOT NULL
);
```

---

## 3.7 Optional integration through ports/adapters

For optional integrations, prefer adapter interfaces.

Examples:

```text
TaskIntegrationPort
DocumentIntegrationPort
ResourceIntegrationPort
ProfitabilityIntegrationPort
PortalIntegrationPort
```

Implementations:

```text
RealTaskIntegrationAdapter
NoopTaskIntegrationAdapter
```

Noop adapter behavior:

```text
return capability disabled
return empty result
skip optional sync
do not crash
```

Domain/service code must not directly call optional module service without capability check.

---

## 3.8 Optional links do not grant access

Rules:

```text
1. A user assigned to SupportCase does not automatically get access to linked Project.
2. A user assigned to Task does not automatically get access to linked SupportCase.
3. A portal user with case access does not automatically get document access unless document is client-visible and granted.
4. Every linked object read must check permission for that target object.
```

---

## 3.9 Error codes for disabled optional features

Recommended generic errors:

```text
MODULE_NOT_ENABLED
MODULE_CAPABILITY_NOT_ENABLED
OPTIONAL_INTEGRATION_NOT_AVAILABLE
OPTIONAL_TARGET_MODULE_DISABLED
OPTIONAL_TARGET_OBJECT_NOT_FOUND
OPTIONAL_TARGET_PERMISSION_DENIED
OPTIONAL_LINK_NOT_SUPPORTED
```

Examples:

```text
SUPPORT_TASK_INTEGRATION_NOT_ENABLED
SUPPORT_PROJECT_INTEGRATION_NOT_ENABLED
SUPPORT_PORTAL_INTEGRATION_NOT_ENABLED
PROFITABILITY_RESOURCE_COST_INPUT_NOT_ENABLED
DOCUMENT_PORTAL_VISIBILITY_NOT_ENABLED
INTEGRATION_PROVIDER_ADAPTER_NOT_IMPLEMENTED
```

API response example:

```json
{
  "code": "MODULE_CAPABILITY_NOT_ENABLED",
  "message": "Task integration is not enabled for this workspace.",
  "details": {
    "module": "TASK",
    "capability": "TASK.CREATE",
    "sourceModule": "SUPPORT_SERVICE"
  }
}
```

Do not return 500 for expected disabled capabilities.

---

## 3.10 Module independence completion requirement

Every phase completion file must include:

```text
## Module Independence Verification
```

Required questions:

```text
1. Can this module run with Core Platform only?
2. Which APIs are standalone?
3. Which features require optional integrations?
4. What happens when optional modules are disabled?
5. Which optional integrations were tested?
6. Which optional integrations are deferred?
7. Does any DB field accidentally require optional module?
8. Does any service call optional module without capability check?
9. Does any assignment/link accidentally grant access?
10. Does dashboard/report degrade gracefully?
```

---

## 4. Global anti-fabrication rules for coding agents

The AI agent must obey these rules:

```text
1. Do not invent entities not listed in the phase scope.
2. Do not invent endpoints not listed in the phase scope.
3. Do not silently skip business rules.
4. Do not claim tests pass unless tests were actually run.
5. Do not fake completion files.
6. Do not change unrelated modules unless required by the phase.
7. Do not refactor global API/security paths unless phase explicitly includes that work.
8. Do not implement future phases early.
9. Do not use placeholder classes that look complete but do not enforce rules.
10. If a business rule cannot be implemented, document it under Business Rules Not Implemented.
11. Do not add NOT NULL foreign keys to optional module tables.
12. Do not call optional module services without capability checks.
13. Do not make module startup fail when optional module is absent.
14. Do not require Project for modules that can be standalone.
15. Do not require Task/WBS for Support.
16. Do not require Resource for Profitability.
17. Do not require Profitability for Resource.
18. Do not require Portal for internal workflows.
19. Do not let optional links grant access automatically.
20. Do not hide optional module assumptions in code.
```

---

## 5. Master phase roadmap

The backend roadmap has two layers:

```text
Phase 00–23 = Original core implementation roadmap.
Phase 24–40 = Expanded modular product roadmap.
```

Important:

```text
Phase 24–40 modules are modular expansions.
They must follow the standalone/optional integration principle.
```

---

## 5.1 Original core roadmap — Phase 00 to Phase 23

| Phase | Name | Status | Main purpose |
|---:|---|---:|---|
| 00 | Architecture Baseline / Master Roadmap | Implemented / tracking | Lock backend conventions and architecture rules |
| 01 | API Path & Security Baseline | Implemented | `/api`, auth, CSRF, cookies |
| 02 | IAM Core | Implemented | User, role, permission, grants, authorization |
| 03 | Workspace / Org / Member / Team | Implemented | Tenant and membership model |
| 04 | Platform Audit / Outbox / Idempotency | Implemented foundation | Audit, trace, outbox, idempotency |
| 05 | Event Registry | Implemented | Event definitions and variables |
| 06 | Notification Engine | Implemented foundation | Email template/rule/outbox |
| 07 | AI Agent Platform | Implemented foundation | Provider/model/agent/prompt/execution |
| 08 | Knowledge Catalog | Minimal implemented | Document type catalog |
| 09 | Project Core / Phase / WBS / Task | Implemented | Planning source of truth |
| 10 | Project Authorization Hardening | Implemented | Workspace-scoped project permissions |
| 11 | Project Template & Phase Catalog Hardening | To do | Reusable project/WBS templates |
| 12 | Resource Calendar & Capacity | To do | Work calendar, allocation, capacity |
| 13 | Task Scheduling Engine | To do | Estimate hours → forecast dates |
| 14 | WBS-driven Gantt Projection | To do | Gantt from WBS/task/schedule |
| 15 | Rate Card / CCH / Inflation | To do | Cost role, CCH, billing rate |
| 16 | Estimation Roll-up | To do | Task/WBS/phase/project estimate aggregation |
| 17 | Phase Finance / Budget / Margin | To do | Cost, revenue split, overhead, margin, PBT |
| 18 | Quote / Contract Value Solver | To do | Contract value from target margin |
| 19 | Baseline & Change Request | To do | Freeze plan, manage scope/cost changes |
| 20 | Project Events & Notifications | To do | Project event exports and email rules |
| 21 | AI-assisted Project Planning | To do | AI WBS/task/estimate/risk suggestions |
| 22 | Reporting / Dashboard / Export | To do | Portfolio/project reports and exports |
| 23 | Final Hardening / QA / Performance | To do | Release-grade hardening |

---

## 5.2 Expanded modular product roadmap — Phase 24 to Phase 40

| Phase | Name | Status | Main purpose |
|---:|---|---:|---|
| 24 | Scope / Deliverable / Acceptance | Spec ready | Manage scope packages, deliverables, acceptance |
| 25 | RAID / Decision Management | Spec ready | Risks, assumptions, issues, dependencies, decisions |
| 26 | Quality / Test / Release | Spec ready | QA cases, defects, releases, release readiness |
| 27 | Document Hub / Generation | Spec ready | Documents, versions, templates, generated docs |
| 28 | Application / Requirement / Traceability | Spec ready | App/system requirements and traceability |
| 29 | External Party / Client Stakeholder | Spec ready | Client/vendor/stakeholder CRM-like foundation |
| 30 | Customer / External Portal | Spec ready | External client portal and controlled visibility |
| 31 | Meetings / Collaboration | Spec ready | Meetings, agenda, minutes, actions |
| 32 | Search / Navigation / Productivity | Spec ready | Global search, recent items, saved views |
| 33 | Custom Fields / Forms / Configuration | Spec ready | Custom fields, dynamic forms, tags, statuses |
| 34 | Governance / Versioning / Permission / Audit | Spec ready | Owner control, versioning, lock, restore, audit |
| 35 | Advanced Notifications / Digest / Reminder | Spec ready | Preferences, mute, digest, reminders, alerts |
| 36 | Revenue & Profitability Visibility | Spec ready | Revenue, cost, profit, margin, forecast |
| 37 | Resource / Capacity / Effort / Utilization | Spec ready | Resource profile, capacity, effort, utilization |
| 38 | Audit / Compliance Readiness / Privacy / Retention | Spec ready | Sensitive access, privacy request, retention |
| 39 | Integration / Import / Export / Connectors | Spec ready | Provider registry, import/export, webhook, sync |
| 40 | Service / Support / Maintenance | Spec ready | Support cases, SLA, incident, maintenance |

---

## 5.3 Future backlog — Phase 41+

Phase 41+ is optional future expansion.

Examples:

```text
Knowledge graph
Advanced AI assistant
Semantic index
Mobile app backend
Enterprise admin
Marketplace / connector SDK
Advanced security analytics
Advanced billing if ever needed
Advanced customer success
Product telemetry
```

Phase 41+ must not be implemented inside Phase 00–40 unless explicitly requested.

---

## 6. Recommended implementation order

The current backend has completed:

```text
Phase 00–10 foundation and project core/auth hardening.
```

Recommended next implementation path if continuing original project core:

```text
Phase 11 — Project Template & Phase Catalog Hardening
Phase 12 — Resource Calendar & Capacity
Phase 13 — Task Scheduling Engine
Phase 14 — WBS-driven Gantt Projection
Phase 15 — Rate Card / CCH / Inflation
Phase 16 — Estimation Roll-up
Phase 17 — Phase Finance / Budget / Margin
Phase 18 — Quote / Contract Value Solver
Phase 19 — Baseline & Change Request
Phase 20 — Project Events & Notifications
Phase 21 — AI-assisted Project Planning
Phase 22 — Reporting / Dashboard / Export
Phase 23 — Final Hardening
```

If the product wants to reach Gantt fastest, Phase 11 can be delayed, but Phase 12 and Phase 13 must come before Phase 14.

Correct dependency:

```text
Resource Calendar → Task Scheduling → Gantt
Rate Card → Estimation Roll-up → Finance → Quote
Baseline → Change Request
Events → Notification / AI automation
```

---

## 6.1 Alternative modular implementation paths

Because Scopery is modular, the product does not always need to implement phases strictly in numeric order.

### Path A — Project management MVP

```text
01–10 Core foundation
11 Template
12 Capacity
13 Scheduling
14 Gantt
16 Estimation
22 Reporting
23 Hardening
```

### Path B — Support/service MVP

```text
01–06 Core foundation
29 External Party
27 Document/Attachment basics
30 Portal optional
35 Advanced Notifications optional
40 Support / Maintenance
38 Trust basics optional
```

This path can work without WBS/Task/Resource/Profitability.

### Path C — Profitability MVP

```text
01–06 Core foundation
36 Profitability
22 Reporting
38 Trust basics for sensitive cost/profit
```

Optional later:

```text
18 Quote
19 Change Request
37 Resource cost input
40 Support cost input
```

### Path D — Resource planning MVP

```text
01–06 Core foundation
37 Resource / Capacity
22 Reporting
```

Optional later:

```text
09 Project / Task linking
36 Profitability cost input
39 HR/calendar imports
```

### Path E — Client portal/document MVP

```text
01–06 Core foundation
27 Document Hub
29 External Party
30 Client Portal
35 Notifications
38 Trust basics
```

---

## 7. Phase inventory summary

### Phase 00 — Architecture Baseline / Master Roadmap

Defines roadmap, architecture principles, modularity, implementation order, scope control, and completion rules.

### Phase 01 — API Path / Security Baseline

Defines API base path, auth patterns, security defaults, error structure, request tracing, CORS/security basics.

### Phase 02 — IAM Core

Defines users, roles, permissions, authorities, role assignment, workspace/project permission foundations.

### Phase 03 — Workspace / Organization / Team

Defines workspace, organization, team, membership, workspace-level settings, and ownership.

### Phase 04 — Platform Audit / Outbox / Idempotency

Defines audit log, activity log, outbox events, idempotency keys, and traceability.

### Phase 05 — Event Registry

Defines standard event definitions, event variables, event sources, and event seeding.

### Phase 06 — Notification Engine

Defines basic in-app/email notification foundation, templates, delivery state, and notification events.

### Phase 07 — AI Agent Platform

Defines provider/model/agent/prompt/execution/usage policy foundation.

### Phase 08 — Knowledge Catalog

Defines document type catalog and future knowledge document foundation.

### Phase 09 — Project Core / WBS / Task

Defines WBS, task, task lifecycle, task hierarchy, task assignment basics, status, priority, and task tracking.

### Phase 10 — Project Authorization

Defines project-specific access control, permission checks, object access, and cross-project safety.

### Phase 11 — Project Template & Phase Catalog Hardening

Defines project templates, template phases, template WBS nodes, template task blueprints, and clone workflow.

### Phase 12 — Resource Calendar & Capacity

Defines working calendar, time off, allocation, capacity calculation.

### Phase 13 — Task Scheduling Engine

Defines scheduling forecast from estimate, assignee, capacity, and dependency.

### Phase 14 — WBS-driven Gantt Projection

Defines Gantt projection from WBS, tasks, dependencies, and schedule forecast.

### Phase 15 — Rate Card / CCH / Inflation

Defines cost roles, rate card, role rates, and inflation/escalation policy.

### Phase 16 — Estimation Roll-up

Defines task/WBS/phase/project estimate aggregation.

### Phase 17 — Phase Finance / Budget / Margin

Defines financial scenario, task cost snapshot, custom cost, overhead, revenue allocation, margin/PBT.

### Phase 18 — Quote / Contract Value Solver

Defines quote, quote version, quote lines, required contract value solver, target margin.

### Phase 19 — Baseline & Change Request

Defines baseline, baseline version, change request, change impact.

### Phase 20 — Project Events & Notifications

Defines project event seeding, outbox event emission, and notification templates/rules.

### Phase 21 — AI-assisted Project Planning

Defines AI-generated planning suggestions, user approval, and traceability.

### Phase 22 — Reporting / Dashboard / Export

Defines project/portfolio reports, dashboards, exports.

### Phase 23 — Final Hardening / QA / Performance

Defines release-grade hardening and final QA gates.

### Phase 24 — Scope / Deliverable / Acceptance

Defines scope package, deliverable, acceptance criteria, acceptance record, and controlled delivery visibility.

### Phase 25 — RAID / Decision Management

Defines risks, assumptions, issues, dependencies, decisions, mitigation, and decision history.

### Phase 26 — Quality / Test / Release

Defines test cases, test runs, defects, release package, release readiness.

### Phase 27 — Document Hub / Generation

Defines documents, versions, templates, generated documents, and document links.

### Phase 28 — Application / Requirement / Traceability

Defines application/system requirements, requirement versions, traceability links, and impact analysis.

### Phase 29 — External Party / Client Stakeholder

Defines external organizations, contacts, client stakeholders, vendor contacts, and relationship mapping.

### Phase 30 — Customer / External Portal

Defines external portal account, grants, portal-visible objects, external comments, portal activity.

### Phase 31 — Meetings / Collaboration

Defines meetings, agenda items, minutes, decisions, action items, and collaboration artifacts.

### Phase 32 — Search / Navigation / Productivity

Defines global search, saved views, recent items, favorites, quick navigation.

### Phase 33 — Custom Fields / Forms / Configuration

Defines custom fields, form definitions, submissions, tags, taxonomies, custom statuses.

### Phase 34 — Governance / Versioning / Permission / Audit

Defines owner control, object grants, version records, snapshots, locks, restore, diff, audit.

### Phase 35 — Advanced Notifications / Digest / Reminder

Defines notification preferences, mute, digest, reminders, subscriptions, alert rules.

### Phase 36 — Revenue & Profitability Visibility

Defines revenue sources, cost sources, forecasts, profit, margin, snapshots, profitability dashboard.

### Phase 37 — Resource / Capacity / Effort / Utilization

Defines resources, roles, skills, capacity calendars, allocation, effort, utilization, cost input.

### Phase 38 — Audit / Compliance Readiness / Privacy / Retention

Defines data classification, sensitive access, export audit, privacy requests, retention, legal hold, evidence.

### Phase 39 — Integration / Import / Export / Connectors

Defines provider registry, connection, credential reference, mapping, import/export jobs, webhooks, sync.

### Phase 40 — Service / Support / Maintenance

Defines support cases, SLA, incident, problem, maintenance, handover, portal support, support cost input.

---

## 8. Original phase detail references

The detailed original specs for Phase 11–23 remain valid as implementation detail, with one correction:

```text
If any original phase implies hard coupling to another business module,
the modular architecture principle in Section 3 overrides that interpretation.
```

For example:

```text
Phase 12 Resource Calendar & Capacity can exist independently as Resource module.
Phase 17 Finance can be interpreted as early version of Phase 36 Profitability if product chooses.
Phase 18 Quote can feed Phase 36 Revenue but is not required for manual profitability.
Phase 19 Baseline & Change Request can integrate with Project but should not force Support to depend on it.
```

---

## 9. Event seeding master rules

Event-producing phases must follow these rules:

```text
1. Seed must be idempotent by code or sourceSystem + eventKey.
2. Seed variables must match payload actually emitted.
3. Event input schema must not describe fields the event does not emit.
4. Existing event definitions must not be overwritten destructively.
5. If schema changes incompatibly, create new event version.
6. Optional module consumers must not be required for producer events.
7. If a consumer module is disabled, event should still be emitted unless event itself is optional.
```

---

## 10. Email / notification seeding master rules

Notification phases must follow these rules:

```text
1. Seeder must be idempotent by template code + scope.
2. Template must create a DRAFT version, then publish if no active version exists.
3. If active version exists, do not overwrite unless explicitly requested.
4. Rule must be seeded idempotently by rule code.
5. Rule recipient strategy must match actual payload variables.
6. Template variable placeholders must be validated against EventVariable.
7. Notification content must respect permissions and sensitive masking where applicable.
8. Disabled optional module notification rules must not fire.
```

---

## 11. Master test strategy

### 11.1 Unit/action tests

Every Action must have tests for:

```text
- happy path
- not found dependency
- duplicate unique constraint business check
- invalid state transition
- authorization denied when applicable
- activity/event side effect when applicable
- optional integration disabled where applicable
```

### 11.2 Query tests

Every QueryService must test:

```text
- authorization filtering/checking
- pagination
- filter fields
- sorting if supported
- not returning archived/deleted unless requested
- hiding unavailable optional integration sections
```

### 11.3 Migration tests

Every migration must be validated by:

```text
- fresh DB startup
- mvn test with Flyway
- FK creation order
- unique constraints
- indexes
- no NOT NULL FK to optional module tables unless intentionally hard-required
```

### 11.4 Security tests

Security-sensitive phases must test:

```text
- unauthenticated denied
- authenticated without permission denied
- authorized allowed
- CSRF required for protected write
- public endpoints remain public only where expected
- optional links do not grant access
```

### 11.5 Event tests

Event-producing phases must test:

```text
- event definition seeded
- event variables seeded
- outbox event written on action
- payload contains documented fields
- no duplicate seed on repeated startup
- disabled optional consumer does not break producer event
```

### 11.6 Email tests

Notification phases must test:

```text
- template seeded idempotently
- version published only if appropriate
- variables validate against event variables
- rule seeded idempotently
- disabled rule does not fire
- retry behavior works for failed outbox
```

### 11.7 Module independence tests

Each business module must test:

```text
- standalone mode works
- optional integration enabled works
- optional integration disabled degrades gracefully
- disabled optional action returns MODULE_CAPABILITY_NOT_ENABLED
- optional link does not grant target access
- dashboard/report hides disabled optional sections
```

---

## 12. Phase completion file requirement

Every phase must create:

```text
docs/phase-complete/PHASE_XX_{NAME}_COMPLETE.md
```

Required content:

```markdown
# Phase XX Complete

## 1. Summary
## 2. Files Created
## 3. Files Modified
## 4. Database Migrations
## 5. Entities Implemented
## 6. Relationships Implemented
## 7. APIs Implemented
## 8. Commands / Actions Implemented
## 9. Query Services Implemented
## 10. Business Rules Implemented
## 11. Business Rules Not Implemented
## 12. Events Exported / Seeded
## 13. Email Templates / Rules Seeded
## 14. Authorization Rules Implemented
## 15. Module Independence Verification
## 16. Optional Integrations Implemented
## 17. Optional Integrations Deferred
## 18. Graceful Degradation Behavior
## 19. Tests Added
## 20. Commands Run
## 21. Test Results
## 22. Manual Verification Steps
## 23. Assumptions Made
## 24. Deviations From Spec
## 25. Known Risks
## 26. Next Recommended Phase
```

The agent must not fake this file.

---

## 13. Master acceptance gate for moving to next phase

A phase can be accepted only when:

```text
1. Scope implemented.
2. Out-of-scope not accidentally implemented.
3. mvn compile PASS.
4. mvn test PASS.
5. Flyway migration applies cleanly.
6. Business rule tests exist and pass.
7. Authorization tests exist where applicable.
8. Event/email seeders tested where applicable.
9. Completion file created and honest.
10. Known risks are documented.
11. Module independence section completed.
12. Optional integrations are clearly listed.
13. Disabled optional modules degrade gracefully.
14. No optional link grants access automatically.
15. No hard FK accidentally forces optional module.
```

---

## 14. Specific modular contracts for key modules

### 14.1 Support / Maintenance module

Standalone capabilities:

```text
service profile
support case
queue
assignment
comments
attachments if file basics enabled
SLA
incident
problem
maintenance window
support dashboard
reports
notifications
```

Optional integrations:

```text
Project: link support to project
Task: create/link task
Quality/Defect: create/link defect
Release: link fix release
Document: handover/knowledge/attachments
Portal: client support portal
Resource: support effort affects utilization
Profitability: support cost affects margin
Integration: email/chat/helpdesk intake
AI: support summary and duplicate detection
```

Disabled behavior:

```text
Without Project, support case is standalone or client-scoped.
Without Task, create-task action hidden.
Without Resource, support effort remains local.
Without Profitability, support cost impact hidden.
Without Portal, internal support still works.
```

---

### 14.2 Profitability module

Standalone capabilities:

```text
manual revenue source
manual cost source
profitability adjustment
profitability snapshot
margin dashboard
threshold status
reports
```

Optional integrations:

```text
Quote: auto revenue from quote
ChangeRequest: revenue/cost impact
Task/WBS: cost from estimated effort
Resource: cost from utilization/effort/rate
Support: support cost impact
Portal: client-safe commercial value
AI: variance explanation
```

Disabled behavior:

```text
Without Resource, use manual/blended cost.
Without Quote, use manual revenue.
Without Project, allow workspace/client profitability profile if product needs.
Without AI, no explanation suggestions.
```

---

### 14.3 Resource / Capacity module

Standalone capabilities:

```text
resource profile
role/skill
capacity calendar
capacity exception
allocation
effort estimate
utilization
workload snapshot
resource dashboard
```

Optional integrations:

```text
Project: project allocation
Task: assignment workload
Quality/Defect: rework effort
Release: release support effort
Profitability: cost input
Support: support effort load
AI: staffing suggestions
```

Disabled behavior:

```text
Without Project, resource module can still track workspace/team capacity.
Without Task, effort can be manual or allocation-based.
Without Profitability, cost input is disabled but utilization works.
```

---

### 14.4 Document Hub module

Standalone capabilities:

```text
upload document
version document
categorize document
search document
permission document
download document
generate document if template exists
```

Optional integrations:

```text
Project: project document folders
Task: task attachments
Support: support attachments and knowledge
Deliverable: deliverable documents
Portal: client-visible documents
Trust: sensitive document access
AI: document summary
Integration: external storage sync
```

Disabled behavior:

```text
Without Project, documents remain workspace/client/general documents.
Without Portal, documents are internal only.
Without Integration, storage sync disabled.
```

---

### 14.5 Integration Hub module

Standalone capabilities:

```text
provider registry
connection
credential reference
mapping
import/export framework
generic webhook
sync foundation
dashboard
```

Optional integrations:

```text
Project/Task: import/export projects/tasks
Document: storage sync
Meeting: calendar sync
Support: helpdesk/email/chat intake
Profitability: finance/revenue data import/export
Resource: HR/resource data import
Trust: export audit and masking
AI: mapping suggestions
```

Disabled behavior:

```text
Without Project, project import templates disabled.
Without Support, helpdesk integrations disabled.
Without Trust, baseline export audit still required; advanced masking only if available.
```

---

## 15. Final instruction to agents

When implementing any phase:

```text
Do exactly the phase.
Do not invent adjacent modules.
Do not skip tests.
Do not hide incomplete rules.
Do not claim success without running commands.
Create the completion file.
Respect modular architecture.
Keep modules standalone-capable.
Treat cross-module relationships as optional integrations unless explicitly hard-required.
```

The final product principle remains:

```text
Tách bạch để dùng riêng được.
Liên kết để khi cần thì mạnh hơn.
Không ràng buộc để sản phẩm không bị nặng.
```
