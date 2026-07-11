# PHASE 32 — TO-BE Search, Navigation, Saved Views, Command Palette, Personal Productivity & Work Inbox

> Project: Scopery Backend  
> Phase: 32  
> Document type: TO-BE implementation-grade specification  
> Status: Planning / Gap-driven implementation spec  
> Roadmap group: Post-core Dynamic Work OS expansion  
> Depends on: Phase 00 — Master Backend Roadmap, Phase 01 — API Path / Security Baseline, Phase 02 — IAM Core, Phase 03 — Workspace / Organization / Team, Phase 04 — Platform Audit / Outbox / Idempotency, Phase 05 — Event Registry, Phase 06 — Notification Engine, Phase 09 — Project Core / WBS / Task, Phase 10 — Project Authorization, Phase 20 — Project Events / Notifications, Phase 22 — Reporting / Dashboard / Export, Phase 23 — Core Hardening, Phase 24 — Scope / Deliverable / Acceptance, Phase 25 — RAID / Decision Management, Phase 26 — Quality / Test / Release, Phase 27 — Document Hub / Generation, Phase 28 — Application / Requirement / Traceability, Phase 29 — External Party / Client CRM / Stakeholder, Phase 30 — Customer / External Collaboration Portal, Phase 31 — Meetings / Collaboration  
> API base: `/api`  
> Primary module: `modules/productivity`, `modules/search`, or `modules/navigation` depending on repository architecture  
> Related modules: `iam`, `project`, `task`, `notification`, `document`, `reporting`, `collaboration`, `requirements`, `quality`, `externalparty`, `clientportal`, `audit`, `eventregistry`, future `semantic-index`, `workflow`, `integration`, `automation`  
> Important rule: Phase 32 introduces permission-aware search, navigation, saved views, personal productivity objects, command palette metadata, and work inbox. It does **not** implement full semantic/RAG search, vector embeddings, real-time collaboration, browser push notifications, workflow automation, or cross-tenant search.

---

# 0. Purpose

Phase 32 improves usability and daily work efficiency across the whole platform.

Earlier phases created many objects:

```text
Projects
Tasks
Deliverables
Requirements
RAID items
Decisions
Quality/Test/Defects/Releases
Documents
External parties
Client portal items
Meetings
Comments
Notifications
Reports
```

Without a productivity layer, users may struggle to find and act on work.

Phase 32 answers:

```text
How does a user quickly find anything they are allowed to see?
How do they resume recent work?
How do they save frequently used filters/views?
How do they favorite important objects?
How do they get a consolidated personal work inbox?
How do they run quick actions safely?
How does command palette know what commands are available?
How does navigation stay permission-aware?
How are search results masked?
How do we avoid exposing cross-workspace data?
How do we prepare for future semantic search without pretending it exists now?
```

Phase 32 is the **productivity and discoverability foundation**.

---

# 1. Source inputs

Before coding Phase 32, the agent must read:

```text
1. Current backend codebase
2. Phase 01 API/Security baseline implementation
3. Phase 02 IAM implementation
4. Phase 03 Workspace/Organization/Team implementation
5. Phase 04 Audit/Outbox/Idempotency implementation
6. Phase 06 Notification implementation
7. Phase 09 Project/Task implementation
8. Phase 10 Project Authorization implementation
9. Phase 20 Project Notification implementation
10. Phase 22 Reporting implementation
11. Phase 23 Core Hardening completion file
12. Phase 24 Scope/Deliverable/Acceptance implementation
13. Phase 25 RAID/Decision implementation
14. Phase 26 Quality/Test/Release implementation
15. Phase 27 Document Hub implementation
16. Phase 28 Requirement/Traceability implementation
17. Phase 29 External Party implementation
18. Phase 30 External Portal implementation
19. Phase 31 Collaboration implementation
20. Current search/indexing code if any
21. Current notification/work inbox code if any
22. Current saved filter/view code if any
23. Current IAM seeders and EventDefinition seeders
```

The agent must inspect actual code and not assume features exist.

---

# 2. Current expected backend state

After Phase 31, Scopery likely has many searchable artifacts:

```text
Project
Task
WBS node
Deliverable
AcceptanceCriteria
Requirement
Document
RAID item
Decision
TestCase
Defect
ReleasePackage
ExternalOrganization
ExternalContact
Meeting
MeetingActionItem
CommentThread
Notification
ReportRun
```

Likely missing:

```text
GlobalSearchIndex or query adapter
Permission-aware search result mapper
SearchScope
SearchSavedQuery
SavedView
FavoriteItem
RecentItem
PinnedItem
QuickAccessShortcut
CommandDefinition
CommandAvailabilityPolicy
UserWorkInbox
WorkInboxItem
PersonalTaskQueue
QuickActionRegistry
NavigationMenuDefinition
NavigationItemVisibilityPolicy
UserNavigationPreference
SearchAuditLog
```

Phase 32 implements these foundations.

---

# 3. Target statement

Phase 32 must deliver:

```text
1. Permission-aware global search across core objects.
2. Search result abstraction with masking and highlighting.
3. Search scopes and filters.
4. Saved searches and saved views.
5. Favorites, pinned items, and recent items.
6. Personal work inbox aggregating actionable items.
7. Command palette metadata and safe quick actions.
8. Permission-aware navigation menu metadata.
9. User navigation/productivity preferences.
10. Work item grouping by due date, priority, source, project, and type.
11. Audit for sensitive search/download/action events.
12. Reporting on user productivity/search usage if needed.
13. AI-assisted search/help suggestions as proposal only.
14. Tests, security checks, and completion file.
```

---

# 4. Boundary decisions

## 4.1 Search is permission-aware

Search results must be filtered by what the actor can view.

Rules:

```text
1. No object is returned unless actor can view it.
2. Sensitive fields are masked.
3. Search result snippets must not leak hidden content.
4. Cross-workspace search only when actor has workspace access.
5. External portal search is separate and more restricted.
```

## 4.2 Search is not semantic RAG

Phase 32 can implement:

```text
keyword search
database-backed search
PostgreSQL full-text search
basic ranking
saved searches
recent searches
```

It does not implement:

```text
embeddings
vector database
semantic RAG
knowledge graph
cross-document question answering
```

Those are Phase 41.

## 4.3 Command palette is metadata + safe dispatch

Phase 32 can expose command definitions and quick action endpoints.

It does not bypass IAM or domain actions.

## 4.4 SavedView is not report definition

SavedView stores user/team filters and display preferences for lists.

ReportDefinition from Phase 22 remains reporting engine.

## 4.5 WorkInbox is not workflow engine

WorkInbox aggregates actionable items.

It does not implement workflow approval routing, escalation, SLA, or automation.

Those are Phase 34/35.

---

# 5. Classification labels

Use these labels in completion file:

| Label | Meaning |
|---|---|
| `CURRENTLY_IMPLEMENTED` | Existing backend already supports it with tests. |
| `PARTIALLY_IMPLEMENTED` | Some support exists but gaps remain. |
| `MUST_IMPLEMENT_IN_PHASE_32` | Required now. |
| `SEED_ONLY_IN_PHASE_32` | Seed definitions/commands/navigation/events only. |
| `DEFERRED_TO_PHASE_XX` | Later phase returns. |
| `DEFERRED_TO_POST_23_BACKLOG` | Later Work OS backlog. |
| `NOT_IN_SCOPE_FOR_PHASE_32` | Explicitly not in this phase. |

---

# 6. Required capabilities

---

## 6.1 SRCH-001 — Global search

Classification: `MUST_IMPLEMENT_IN_PHASE_32`

Purpose:

```text
Allow users to search across allowed workspace/project artifacts.
```

Searchable object types:

```text
PROJECT
TASK
WBS_NODE
DELIVERABLE
REQUIREMENT
DOCUMENT
RAID_ITEM
DECISION
TEST_CASE
DEFECT
RELEASE_PACKAGE
MEETING
MEETING_ACTION_ITEM
COMMENT_THREAD
EXTERNAL_ORGANIZATION
EXTERNAL_CONTACT
REPORT_RUN
QUOTE_VERSION
CHANGE_REQUEST
BASELINE
```

Rules:

```text
1. Search requires authenticated actor.
2. Actor must have workspace/project access.
3. Object-level permissions enforced.
4. Field-level masking enforced.
5. Search snippets use only visible fields.
6. External portal search limited to portal-visible objects.
7. Pagination required.
8. Result type and target id included.
9. Ranking documented.
```

---

## 6.2 SRCH-002 — Search result model

Classification: `MUST_IMPLEMENT_IN_PHASE_32`

Each result should include:

```text
targetType
targetId
workspaceId
projectId optional
title
subtitle
snippet
icon/type
status
updatedAt
matchedFields
score
navigationPath
permissions/allowedActions summary
masked flag
```

Rules:

```text
1. Result title must not leak hidden data.
2. Result snippet must not include hidden fields.
3. Navigation path must use internal route metadata only.
4. Allowed actions are capability hints, not permission grants.
```

---

## 6.3 SRCH-003 — Search scopes

Classification: `MUST_IMPLEMENT_IN_PHASE_32`

Scopes:

```text
ALL
CURRENT_WORKSPACE
CURRENT_PROJECT
MY_ITEMS
DOCUMENTS
TASKS
DELIVERABLES
REQUIREMENTS
QUALITY
RAID_DECISIONS
MEETINGS
EXTERNAL_PARTIES
```

Rules:

```text
1. Scope restricts query targets.
2. Scope never expands access.
3. Scope availability is permission-aware.
4. External portal has separate scopes.
```

---

## 6.4 SRCH-004 — Search indexing strategy

Classification: `MUST_IMPLEMENT_IN_PHASE_32`

Accepted strategies:

```text
1. Direct database query adapters per module.
2. Consolidated search_index table.
3. PostgreSQL full-text search materialized index.
4. Hybrid direct adapters + index.
```

Recommended Phase 32:

```text
Start with permission-aware query adapters or search_index table.
Document chosen approach in completion file.
```

Rules:

```text
1. Index must not store unmasked cross-tenant data unsafely.
2. Index entries must include workspace/project scope.
3. Index updates must be idempotent.
4. Index rebuild job must be safe.
5. Sensitive fields either excluded or marked restricted.
```

---

## 6.5 SRCH-005 — SavedSearch

Classification: `MUST_IMPLEMENT_IN_PHASE_32`

Purpose:

```text
Save reusable search queries.
```

Rules:

```text
1. SavedSearch belongs to user and workspace.
2. Can be private or shared if policy supports.
3. Saved query does not store search results by default.
4. Running saved search re-applies current IAM.
5. Saved search can be archived.
```

Visibility:

```text
PRIVATE
WORKSPACE_SHARED
TEAM_SHARED
PROJECT_SHARED
```

---

## 6.6 VIEW-001 — SavedView

Classification: `MUST_IMPLEMENT_IN_PHASE_32`

Purpose:

```text
Save filters, sorting, grouping, columns, and display mode for list/table/board views.
```

View targets:

```text
PROJECT_LIST
TASK_LIST
DELIVERABLE_LIST
REQUIREMENT_LIST
DEFECT_LIST
RAID_LIST
DOCUMENT_LIST
MEETING_LIST
RELEASE_LIST
EXTERNAL_CONTACT_LIST
REPORT_LIST
```

Rules:

```text
1. SavedView belongs to user/workspace and optional project.
2. View config stored as JSON but schema-validated.
3. View does not bypass permissions.
4. Shared views require permission.
5. Default view can be set per user/target.
```

---

## 6.7 FAV-001 — FavoriteItem

Classification: `MUST_IMPLEMENT_IN_PHASE_32`

Purpose:

```text
Allow users to favorite important objects.
```

Rules:

```text
1. Favorite belongs to user.
2. Target must be visible to user when creating.
3. Favorite does not grant future access.
4. If access is lost, favorite hides or shows inaccessible placeholder by policy.
5. Duplicate active favorite prevented.
```

---

## 6.8 PIN-001 — PinnedItem

Classification: `MUST_IMPLEMENT_IN_PHASE_32`

Purpose:

```text
Pin items to personal/project/workspace navigation.
```

Pin scopes:

```text
PERSONAL
PROJECT
WORKSPACE
TEAM
```

Rules:

```text
1. Personal pin belongs to user.
2. Project/workspace pin requires manage permission.
3. Pin target must exist.
4. Pin does not grant access.
5. Ordering supported.
```

---

## 6.9 REC-001 — RecentItem

Classification: `MUST_IMPLEMENT_IN_PHASE_32`

Purpose:

```text
Track recently viewed/modified objects for quick resume.
```

Rules:

```text
1. RecentItem belongs to user.
2. Created/updated when user views supported object.
3. Stores target type/id, timestamp, workspace/project.
4. Recent list re-checks access.
5. Limit/retention configured.
6. External portal recents stored separately or marked principal type.
```

---

## 6.10 INB-001 — UserWorkInbox

Classification: `MUST_IMPLEMENT_IN_PHASE_32`

Purpose:

```text
Aggregate actionable items for a user.
```

Sources:

```text
assigned tasks
due/overdue tasks
assigned meeting actions
mentions
unread notifications
client review requests assigned internally
defects assigned to user
RAID actions assigned to user
approval/review requests
test runs assigned to user
documents pending review
requirements pending review
change requests pending review
release readiness actions
```

Rules:

```text
1. Inbox is derived/read model or persisted feed.
2. Actor only sees items they can access.
3. Inbox item includes sourceType/sourceId.
4. Dismiss/snooze optional but useful.
5. Completing source item should update/hide inbox item.
6. Inbox is not workflow engine.
```

---

## 6.11 INB-002 — WorkInboxItem

Classification: `MUST_IMPLEMENT_IN_PHASE_32`

Fields should support:

```text
sourceType
sourceId
projectId
title
summary
priority
dueAt
status
actionType
navigationPath
createdAt
readAt
dismissedAt
snoozedUntil
```

Action types:

```text
REVIEW
APPROVE
RESPOND
COMPLETE_TASK
COMPLETE_ACTION
FIX_DEFECT
RUN_TEST
READ
COMMENT
FOLLOW_UP
```

Rules:

```text
1. Inbox item action must re-check permission when opened.
2. Dismiss does not mutate source object.
3. Snooze only affects user's inbox.
4. Sensitive source data masked.
```

---

## 6.12 CMD-001 — CommandDefinition

Classification: `MUST_IMPLEMENT_IN_PHASE_32`

Purpose:

```text
Expose command palette metadata.
```

Command examples:

```text
Open project
Create task
Create requirement
Create RAID item
Create meeting
Upload document
Run search
Open inbox
Generate report
Create client review request
```

Rules:

```text
1. Command has code/title/category.
2. Command availability checks IAM/context.
3. Command execution calls existing domain actions.
4. Command cannot bypass validation.
5. Dangerous commands require confirmation/idempotency.
6. External portal has separate limited command set.
```

---

## 6.13 CMD-002 — QuickAction

Classification: `MUST_IMPLEMENT_IN_PHASE_32`

Purpose:

```text
Execute small user actions safely from inbox/search/command palette.
```

Examples:

```text
mark notification read
complete meeting action
open review
assign favorite
snooze inbox item
create task from selected object
create comment
```

Rules:

```text
1. QuickAction must map to domain action/service.
2. Permission re-checked at execution.
3. Idempotency for mutation actions.
4. Audit for sensitive actions.
5. No hidden mass mutation.
```

---

## 6.14 NAV-001 — NavigationMenuDefinition

Classification: `MUST_IMPLEMENT_IN_PHASE_32`

Purpose:

```text
Backend-provided navigation metadata for frontend.
```

Menu item types:

```text
ROUTE
SECTION
EXTERNAL_LINK
ACTION
COMMAND
REPORT
```

Rules:

```text
1. Menu item code stable.
2. Visibility permission-aware.
3. Project/workspace context supported.
4. Badges/counters can be provided.
5. Navigation metadata does not grant access.
```

---

## 6.15 NAV-002 — UserNavigationPreference

Classification: `MUST_IMPLEMENT_IN_PHASE_32`

Purpose:

```text
Allow user to personalize navigation.
```

Preferences:

```text
collapsed sections
favorite routes
default landing page
hidden optional sections
menu order
density/display mode
```

Rules:

```text
1. Preference belongs to user/workspace.
2. Preference cannot reveal hidden menu items.
3. Invalid/deprecated route preferences ignored safely.
```

---

## 6.16 NOTIF-001 — Notification center integration

Classification: `MUST_IMPLEMENT_IN_PHASE_32`

Purpose:

```text
Unify notification center with work inbox and navigation badges.
```

Rules:

```text
1. Existing notification engine remains source for notifications.
2. Work inbox may consume notification events but not replace notifications.
3. Mark read/unread supported if existing notification model supports it.
4. Badges count only visible items.
5. Notification drill-down re-checks access.
```

---

## 6.17 EXTPOR-001 — External portal productivity subset

Classification: `MUST_IMPLEMENT_IN_PHASE_32` if Phase 30 exists.

Portal features:

```text
portal search across visible documents/deliverables/requirements/reviews
portal recent items
portal work inbox for review requests/UAT/feedback
portal favorites optional
```

Rules:

```text
1. Separate external principal.
2. Portal scopes limited.
3. No internal data.
4. Portal search audited.
```

---

## 6.18 AI-001 — AI-assisted search/help

Classification: `SEED_ONLY_IN_PHASE_32` or `MUST_IMPLEMENT_IN_PHASE_32` if Phase 21 tool registry exists.

AI can help with:

```text
natural-language command suggestions
summarize search results
suggest saved view filters
explain inbox priorities
suggest related artifacts
```

Rules:

```text
1. AI only sees data actor can access.
2. AI output is explanation/proposal.
3. AI cannot execute command automatically.
4. AI cannot bypass search/IAM.
5. Full semantic search/RAG deferred to Phase 41.
```

---

# 7. Entity model TO-BE

---

## 7.1 SearchIndexEntry — optional `productivity_search_index`

If using consolidated index.

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
project_id UUID NULL
target_type VARCHAR(100) NOT NULL
target_id UUID NOT NULL
title TEXT NOT NULL
subtitle TEXT NULL
body_text TEXT NULL
status VARCHAR(100) NULL
tags_json JSONB NULL
visibility_class VARCHAR(50) NOT NULL
restricted BOOLEAN NOT NULL DEFAULT false
last_source_updated_at TIMESTAMP NULL
indexed_at TIMESTAMP NOT NULL
index_version VARCHAR(50) NULL
source_hash VARCHAR(128) NULL
version INT
```

Constraint:

```text
unique target_type + target_id
```

Important:

```text
Do not index sensitive hidden fields in plain body_text unless protected.
```

---

## 7.2 SavedSearch — `productivity_saved_search`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
project_id UUID NULL
owner_user_id UUID NOT NULL
name VARCHAR(255) NOT NULL
description TEXT NULL
scope VARCHAR(100) NOT NULL
query_text TEXT NULL
filters_json JSONB NULL
sort_json JSONB NULL
visibility VARCHAR(50) NOT NULL
status VARCHAR(50) NOT NULL
created_at / created_by
updated_at / updated_by
archived_at / archived_by
version INT
```

Status:

```text
ACTIVE
ARCHIVED
```

---

## 7.3 SavedView — `productivity_saved_view`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
project_id UUID NULL
owner_user_id UUID NOT NULL
target_type VARCHAR(100) NOT NULL
name VARCHAR(255) NOT NULL
description TEXT NULL
view_config_json JSONB NOT NULL
filters_json JSONB NULL
sort_json JSONB NULL
group_by_json JSONB NULL
columns_json JSONB NULL
display_mode VARCHAR(50) NULL
visibility VARCHAR(50) NOT NULL
default_flag BOOLEAN NOT NULL DEFAULT false
status VARCHAR(50) NOT NULL
created_at / created_by
updated_at / updated_by
archived_at / archived_by
version INT
```

Display mode:

```text
TABLE
BOARD
LIST
TIMELINE
CALENDAR
KANBAN
```

---

## 7.4 FavoriteItem — `productivity_favorite_item`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
project_id UUID NULL
user_id UUID NOT NULL
target_type VARCHAR(100) NOT NULL
target_id UUID NOT NULL
label_override VARCHAR(255) NULL
created_at TIMESTAMP NOT NULL
archived_at TIMESTAMP NULL
version INT
```

Constraint:

```text
unique active user_id + target_type + target_id
```

---

## 7.5 PinnedItem — `productivity_pinned_item`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
project_id UUID NULL
scope VARCHAR(50) NOT NULL
owner_user_id UUID NULL
target_type VARCHAR(100) NOT NULL
target_id UUID NOT NULL
label_override VARCHAR(255) NULL
sort_order INT NOT NULL DEFAULT 0
created_at / created_by
archived_at / archived_by
version INT
```

Scope:

```text
PERSONAL
PROJECT
WORKSPACE
TEAM
```

---

## 7.6 RecentItem — `productivity_recent_item`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
project_id UUID NULL
principal_type VARCHAR(50) NOT NULL
user_id UUID NULL
external_portal_account_id UUID NULL
target_type VARCHAR(100) NOT NULL
target_id UUID NOT NULL
title_snapshot VARCHAR(255) NULL
viewed_at TIMESTAMP NOT NULL
last_action VARCHAR(100) NULL
version INT
```

Constraint suggestion:

```text
unique principal_type + principal_id + target_type + target_id
```

---

## 7.7 WorkInboxItem — `productivity_work_inbox_item`

If persisted. If derived only, document as read model.

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
project_id UUID NULL
principal_type VARCHAR(50) NOT NULL
user_id UUID NULL
external_portal_account_id UUID NULL
source_type VARCHAR(100) NOT NULL
source_id UUID NOT NULL
action_type VARCHAR(100) NOT NULL
title VARCHAR(255) NOT NULL
summary TEXT NULL
priority VARCHAR(50) NULL
due_at TIMESTAMP NULL
status VARCHAR(50) NOT NULL
read_at TIMESTAMP NULL
dismissed_at TIMESTAMP NULL
snoozed_until TIMESTAMP NULL
created_at TIMESTAMP NOT NULL
updated_at TIMESTAMP NULL
source_updated_at TIMESTAMP NULL
trace_id VARCHAR(100) NULL
version INT
```

Status:

```text
ACTIVE
READ
DISMISSED
SNOOZED
DONE
ARCHIVED
```

---

## 7.8 CommandDefinition — `productivity_command_definition`

Usually seed/config table.

Fields:

```text
id UUID PK
code VARCHAR(150) NOT NULL
title VARCHAR(255) NOT NULL
description TEXT NULL
category VARCHAR(100) NOT NULL
target_context VARCHAR(100) NULL
required_permission VARCHAR(150) NULL
required_capability VARCHAR(150) NULL
dangerous BOOLEAN NOT NULL DEFAULT false
confirmation_required BOOLEAN NOT NULL DEFAULT false
enabled BOOLEAN NOT NULL DEFAULT true
sort_order INT NULL
created_at / updated_at
version INT
```

---

## 7.9 CommandExecutionLog — `productivity_command_execution_log`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
project_id UUID NULL
principal_type VARCHAR(50) NOT NULL
user_id UUID NULL
external_portal_account_id UUID NULL
command_code VARCHAR(150) NOT NULL
target_type VARCHAR(100) NULL
target_id UUID NULL
status VARCHAR(50) NOT NULL
error_code VARCHAR(150) NULL
created_at TIMESTAMP NOT NULL
trace_id VARCHAR(100) NULL
```

---

## 7.10 NavigationMenuDefinition — `productivity_navigation_menu_definition`

Usually seed/config table.

Fields:

```text
id UUID PK
code VARCHAR(150) NOT NULL
parent_code VARCHAR(150) NULL
label VARCHAR(255) NOT NULL
menu_type VARCHAR(50) NOT NULL
route_path VARCHAR(500) NULL
icon VARCHAR(100) NULL
required_permission VARCHAR(150) NULL
required_capability VARCHAR(150) NULL
context_type VARCHAR(100) NULL
enabled BOOLEAN NOT NULL DEFAULT true
sort_order INT NOT NULL DEFAULT 0
created_at / updated_at
version INT
```

---

## 7.11 UserNavigationPreference — `productivity_user_navigation_preference`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
user_id UUID NOT NULL
preference_json JSONB NOT NULL
default_landing_route VARCHAR(500) NULL
created_at / updated_at
version INT
```

Constraint:

```text
unique workspace_id + user_id
```

---

## 7.12 SearchAuditLog — `productivity_search_audit_log`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
project_id UUID NULL
principal_type VARCHAR(50) NOT NULL
user_id UUID NULL
external_portal_account_id UUID NULL
query_text TEXT NULL
scope VARCHAR(100) NULL
result_count INT NULL
sensitive_result_count INT NULL
created_at TIMESTAMP NOT NULL
trace_id VARCHAR(100) NULL
```

Use for restricted/sensitive searches if policy requires.

---

# 8. API TO-BE list

All APIs use `/api`.

---

## 8.1 Search APIs

```text
GET  /api/search
POST /api/search
GET  /api/search/scopes
GET  /api/search/suggestions
POST /api/search/reindex
```

Recommended query:

```text
GET /api/search?q=login&scope=CURRENT_PROJECT&projectId=...&types=TASK,DOCUMENT&page=0&size=20
```

Rules:

```text
1. Pagination required.
2. Reindex endpoint admin/internal only.
3. Suggestions permission-aware.
```

---

## 8.2 Saved search APIs

```text
POST  /api/workspaces/{workspaceId}/saved-searches
GET   /api/workspaces/{workspaceId}/saved-searches
GET   /api/workspaces/{workspaceId}/saved-searches/{savedSearchId}
PUT   /api/workspaces/{workspaceId}/saved-searches/{savedSearchId}
POST  /api/workspaces/{workspaceId}/saved-searches/{savedSearchId}/run
PATCH /api/workspaces/{workspaceId}/saved-searches/{savedSearchId}/archive
```

---

## 8.3 Saved view APIs

```text
POST  /api/workspaces/{workspaceId}/saved-views
GET   /api/workspaces/{workspaceId}/saved-views
GET   /api/workspaces/{workspaceId}/saved-views/{savedViewId}
PUT   /api/workspaces/{workspaceId}/saved-views/{savedViewId}
POST  /api/workspaces/{workspaceId}/saved-views/{savedViewId}/set-default
PATCH /api/workspaces/{workspaceId}/saved-views/{savedViewId}/archive
```

Filters:

```text
targetType
projectId
visibility
```

---

## 8.4 Favorites / pins / recent APIs

```text
POST   /api/workspaces/{workspaceId}/favorites
GET    /api/workspaces/{workspaceId}/favorites
DELETE /api/workspaces/{workspaceId}/favorites/{favoriteId}

POST   /api/workspaces/{workspaceId}/pins
GET    /api/workspaces/{workspaceId}/pins
PUT    /api/workspaces/{workspaceId}/pins/reorder
DELETE /api/workspaces/{workspaceId}/pins/{pinnedItemId}

POST /api/workspaces/{workspaceId}/recent-items/record
GET  /api/workspaces/{workspaceId}/recent-items
POST /api/workspaces/{workspaceId}/recent-items/clear
```

---

## 8.5 Work inbox APIs

```text
GET  /api/workspaces/{workspaceId}/work-inbox
GET  /api/workspaces/{workspaceId}/work-inbox/counts
POST /api/workspaces/{workspaceId}/work-inbox/{inboxItemId}/mark-read
POST /api/workspaces/{workspaceId}/work-inbox/{inboxItemId}/dismiss
POST /api/workspaces/{workspaceId}/work-inbox/{inboxItemId}/snooze
POST /api/workspaces/{workspaceId}/work-inbox/rebuild
```

Filters:

```text
projectId
sourceType
actionType
priority
dueBefore
status
```

---

## 8.6 Command palette APIs

```text
GET  /api/workspaces/{workspaceId}/commands
POST /api/workspaces/{workspaceId}/commands/{commandCode}/execute
GET  /api/workspaces/{workspaceId}/commands/recent
GET  /api/workspaces/{workspaceId}/commands/suggestions
```

Execution request:

```json
{
  "contextType": "PROJECT",
  "contextId": "uuid",
  "targetType": "TASK",
  "targetId": "uuid",
  "payload": {}
}
```

---

## 8.7 Navigation APIs

```text
GET /api/workspaces/{workspaceId}/navigation
GET /api/projects/{projectId}/navigation
GET /api/workspaces/{workspaceId}/navigation/preferences
PUT /api/workspaces/{workspaceId}/navigation/preferences
```

---

## 8.8 Portal productivity APIs

If Phase 30 exists:

```text
GET  /api/portal/search
GET  /api/portal/recent-items
POST /api/portal/recent-items/record
GET  /api/portal/work-inbox
GET  /api/portal/work-inbox/counts
POST /api/portal/work-inbox/{inboxItemId}/mark-read
GET  /api/portal/navigation
```

---

## 8.9 Reports APIs

```text
GET /api/workspaces/{workspaceId}/reports/search-usage
GET /api/workspaces/{workspaceId}/reports/saved-views
GET /api/workspaces/{workspaceId}/reports/work-inbox-summary
GET /api/workspaces/{workspaceId}/reports/command-usage
```

These reports are optional/admin depending product decision.

---

# 9. Authorization requirements

Required authorities:

```text
GLOBAL_SEARCH_USE
GLOBAL_SEARCH_ADMIN_REINDEX
SAVED_SEARCH_VIEW
SAVED_SEARCH_CREATE
SAVED_SEARCH_UPDATE
SAVED_SEARCH_ARCHIVE
SAVED_SEARCH_SHARE

SAVED_VIEW_VIEW
SAVED_VIEW_CREATE
SAVED_VIEW_UPDATE
SAVED_VIEW_ARCHIVE
SAVED_VIEW_SHARE
SAVED_VIEW_SET_DEFAULT

FAVORITE_MANAGE
PIN_PERSONAL_MANAGE
PIN_PROJECT_MANAGE
PIN_WORKSPACE_MANAGE
RECENT_ITEM_VIEW
RECENT_ITEM_MANAGE

WORK_INBOX_VIEW
WORK_INBOX_UPDATE
WORK_INBOX_ADMIN_REBUILD

COMMAND_VIEW
COMMAND_EXECUTE
COMMAND_ADMIN_MANAGE
NAVIGATION_VIEW
NAVIGATION_PREFERENCE_UPDATE

PRODUCTIVITY_REPORT_VIEW
SEARCH_AUDIT_VIEW
```

Portal capabilities:

```text
PORTAL_SEARCH_USE
PORTAL_RECENT_VIEW
PORTAL_WORK_INBOX_VIEW
PORTAL_WORK_INBOX_UPDATE
PORTAL_NAVIGATION_VIEW
```

Rules:

```text
1. Search checks target permissions in addition to search permission.
2. Saved views/searches do not bypass permissions.
3. Favorites/pins/recent do not grant access.
4. Commands re-check domain permissions.
5. Navigation hides unavailable items.
6. External portal uses portal capabilities, not internal IAM.
```

---

# 10. Lifecycle rules

## 10.1 SavedSearch lifecycle

```text
ACTIVE → ARCHIVED
```

Rules:

```text
1. Owner can update private saved search.
2. Shared saved search requires share/manage permission.
3. Running saved search re-checks access.
```

## 10.2 SavedView lifecycle

```text
ACTIVE → ARCHIVED
```

Rules:

```text
1. Default view unique per user/target if policy.
2. Invalid config rejected.
3. Archived view not returned by default.
```

## 10.3 Favorite/pin lifecycle

```text
ACTIVE → ARCHIVED/REMOVED
```

Rules:

```text
1. Duplicate active target prevented.
2. Access loss hides target details.
```

## 10.4 RecentItem lifecycle

```text
RECORDED → EXPIRED/CLEARED
```

Rules:

```text
1. Retention policy enforced.
2. Access re-checked at list time.
```

## 10.5 WorkInbox lifecycle

```text
ACTIVE → READ
ACTIVE/READ → SNOOZED
ACTIVE/READ/SNOOZED → DISMISSED
Source completed → DONE/ARCHIVED
```

Rules:

```text
1. User inbox status does not mutate source object.
2. Source completion can mark item done.
3. Snooze applies only to user.
```

---

# 11. Search implementation strategy

The completion file must state the chosen search approach.

Minimum acceptable approaches:

## Option A — Query adapters

```text
SearchService calls module-specific adapters:
- ProjectSearchAdapter
- TaskSearchAdapter
- DocumentSearchAdapter
- RequirementSearchAdapter
- DefectSearchAdapter
...
```

Pros:

```text
simple
permission logic close to module
less duplicated index data
```

Cons:

```text
harder ranking
more queries
```

## Option B — Search index table

```text
Modules publish index entries to productivity_search_index.
SearchService queries index and re-checks permissions.
```

Pros:

```text
faster global search
consistent result shape
```

Cons:

```text
needs synchronization/rebuild
sensitive data risk
```

## Option C — PostgreSQL full-text search

```text
Use tsvector or generated columns for indexed search.
```

Pros:

```text
better keyword search
```

Cons:

```text
needs migration/indexing work
```

Rules for all:

```text
1. Permission re-check is mandatory.
2. Sensitive fields excluded or protected.
3. Workspace/project filters mandatory.
4. Pagination mandatory.
```

---

# 12. Work inbox source mapping

Initial inbox sources:

| Source | Action type |
|---|---|
| Assigned Task due soon | COMPLETE_TASK |
| Overdue Task | COMPLETE_TASK |
| MeetingActionItem assigned | COMPLETE_ACTION |
| Mention | READ / RESPOND |
| Notification unread | READ |
| Defect assigned | FIX_DEFECT |
| Requirement pending review | REVIEW |
| Document pending review | REVIEW |
| TestRun assigned | RUN_TEST |
| ClientReviewRequest internal follow-up | RESPOND |
| ChangeRequest pending review | REVIEW |
| Release readiness failed | FOLLOW_UP |
| RAID action assigned | COMPLETE_ACTION |

Rules:

```text
1. Missing source module means source type deferred.
2. Inbox builder must handle missing/deleted sources.
3. Duplicate inbox items prevented with sourceType/sourceId/actionType/principal.
```

---

# 13. Command palette strategy

Seed initial commands:

```text
OPEN_PROJECT
OPEN_TASK
CREATE_TASK
CREATE_REQUIREMENT
CREATE_RAID_ITEM
CREATE_MEETING
UPLOAD_DOCUMENT
CREATE_DEFECT
CREATE_RELEASE
RUN_GLOBAL_SEARCH
OPEN_WORK_INBOX
CREATE_SAVED_VIEW
GENERATE_REPORT
CREATE_CLIENT_REVIEW_REQUEST
```

Rules:

```text
1. Command availability depends on context and permission.
2. Command execution logs result.
3. Commands that mutate call existing application/domain actions.
4. Dangerous commands require confirmation.
5. Commands disabled if target module absent.
```

---

# 14. Navigation strategy

Backend navigation should include:

```text
Home
Work Inbox
Projects
Tasks
Documents
Reports
Requirements
Deliverables
RAID / Decisions
Quality / Defects / Releases
Meetings
External Parties
Admin / Settings
```

Rules:

```text
1. Only show menu items actor can access.
2. Counts/badges permission-aware.
3. Navigation does not authorize endpoint access.
4. Frontend can cache but must handle changes.
```

---

# 15. Integration rules

## 15.1 IAM integration

Rules:

```text
1. Search queries call permission service.
2. Results masked by field policy.
3. Saved view/share permissions checked.
4. Command availability uses IAM.
5. Navigation visibility uses IAM.
```

## 15.2 Notification integration

Rules:

```text
1. Inbox can include unread notifications.
2. Notification drill-down uses navigation path.
3. Mention notifications link to comments/meetings.
4. Mark read uses existing notification service.
```

## 15.3 Document integration

Rules:

```text
1. Document search only indexes allowed metadata.
2. Document content search is basic only unless implemented.
3. Full OCR/semantic content search deferred.
4. Document download still uses DocumentHub authorization.
```

## 15.4 Collaboration integration

Rules:

```text
1. Mentions/comments feed inbox.
2. Meeting actions feed inbox.
3. Recent items record meeting/comment views if supported.
```

## 15.5 External portal integration

Rules:

```text
1. External search limited to client-visible portal data.
2. Portal recent/inbox separate from internal user recents/inbox.
3. Portal navigation uses portal grants.
```

## 15.6 Reporting integration

Rules:

```text
1. Search usage report does not expose query content if privacy policy forbids.
2. Inbox summary report should aggregate without leaking private items.
3. Saved view report restricted to admins/owners.
```

## 15.7 AI integration

Rules:

```text
1. AI search summaries use returned permission-filtered results only.
2. AI cannot access hidden search corpus.
3. AI command suggestions require user confirmation before execute.
4. Semantic search deferred to Phase 41.
```

---

# 16. Event Registry integration

Recommended source system:

```text
SCOPERY_PRODUCTIVITY
```

Required events:

```text
GLOBAL_SEARCH_EXECUTED
SEARCH_REINDEX_STARTED
SEARCH_REINDEX_COMPLETED
SEARCH_REINDEX_FAILED

SAVED_SEARCH_CREATED
SAVED_SEARCH_UPDATED
SAVED_SEARCH_ARCHIVED
SAVED_SEARCH_RUN

SAVED_VIEW_CREATED
SAVED_VIEW_UPDATED
SAVED_VIEW_SET_DEFAULT
SAVED_VIEW_ARCHIVED

FAVORITE_CREATED
FAVORITE_REMOVED
PIN_CREATED
PIN_REMOVED
PIN_REORDERED
RECENT_ITEM_RECORDED
RECENT_ITEMS_CLEARED

WORK_INBOX_ITEM_CREATED
WORK_INBOX_ITEM_READ
WORK_INBOX_ITEM_DISMISSED
WORK_INBOX_ITEM_SNOOZED
WORK_INBOX_REBUILT

COMMAND_EXECUTED
COMMAND_FAILED
NAVIGATION_PREFERENCE_UPDATED
```

Standard variables:

```text
actor.userId
externalPortalAccount.id
workspace.id
project.id
target.type
target.id
savedSearch.id
savedView.id
inboxItem.id
command.code
scope
occurredAt
traceId
```

---

# 17. Audit / activity / outbox

Audit-sensitive actions:

```text
restricted search executed
search returned sensitive result types
workspace shared saved view created
workspace pin created
command executed for mutation
work inbox admin rebuild
search reindex
navigation preference updated if security relevant
```

Activity actions:

```text
SAVED_VIEW_CREATED
FAVORITE_CREATED
COMMAND_EXECUTED
WORK_INBOX_ITEM_CREATED
```

Outbox required for major productivity events if notification/analytics consumers depend on them.

Idempotency recommended for:

```text
POST /saved-searches
POST /saved-views
POST /favorites
POST /pins
POST /work-inbox/{id}/dismiss
POST /commands/{commandCode}/execute
POST /search/reindex
```

---

# 18. Business rules master

## 18.1 Search rules

```text
SRCH-001 Actor must be authenticated.
SRCH-002 Workspace/project scope required.
SRCH-003 Search never bypasses permissions.
SRCH-004 Search snippets use visible fields only.
SRCH-005 Pagination required.
SRCH-006 External portal search limited to portal-visible objects.
SRCH-007 Semantic/RAG search not claimed.
```

## 18.2 Saved view/search rules

```text
SAVE-001 Owner required.
SAVE-002 JSON config schema validated.
SAVE-003 Running saved search/view re-checks permissions.
SAVE-004 Shared view/search requires permission.
SAVE-005 Archived entries hidden by default.
```

## 18.3 Favorite/pin/recent rules

```text
FAV-001 Target must be visible when favorited.
FAV-002 Favorite does not grant access.
PIN-001 Pin does not grant access.
PIN-002 Workspace/project pin requires manage permission.
REC-001 Recent list re-checks access.
REC-002 Retention/limit enforced.
```

## 18.4 Inbox rules

```text
INB-001 Inbox item source must exist or be safely hidden.
INB-002 Inbox item does not grant source access.
INB-003 Dismiss/snooze affects only current principal.
INB-004 Duplicate active inbox items prevented.
INB-005 Source completion hides/completes inbox item.
```

## 18.5 Command/navigation rules

```text
CMD-001 Command availability permission-aware.
CMD-002 Command execution re-checks permission.
CMD-003 Mutating command calls domain action.
CMD-004 Dangerous command requires confirmation.
NAV-001 Navigation visibility permission-aware.
NAV-002 Navigation does not authorize endpoint access.
```

---

# 19. Error catalog

```text
SEARCH_SCOPE_INVALID
SEARCH_ACCESS_DENIED
SEARCH_QUERY_TOO_SHORT
SEARCH_QUERY_TOO_LONG
SEARCH_TYPE_NOT_SUPPORTED
SEARCH_REINDEX_NOT_ALLOWED
SEARCH_REINDEX_FAILED

SAVED_SEARCH_NOT_FOUND
SAVED_SEARCH_ACCESS_DENIED
SAVED_SEARCH_INVALID_CONFIG
SAVED_SEARCH_SHARE_NOT_ALLOWED

SAVED_VIEW_NOT_FOUND
SAVED_VIEW_ACCESS_DENIED
SAVED_VIEW_INVALID_CONFIG
SAVED_VIEW_TARGET_NOT_SUPPORTED
SAVED_VIEW_SHARE_NOT_ALLOWED
SAVED_VIEW_DEFAULT_CONFLICT

FAVORITE_NOT_FOUND
FAVORITE_TARGET_NOT_FOUND
FAVORITE_TARGET_ACCESS_DENIED
FAVORITE_DUPLICATE

PIN_NOT_FOUND
PIN_TARGET_NOT_FOUND
PIN_TARGET_ACCESS_DENIED
PIN_SCOPE_NOT_ALLOWED
PIN_DUPLICATE

RECENT_ITEM_NOT_FOUND
RECENT_ITEM_TARGET_ACCESS_DENIED

WORK_INBOX_ITEM_NOT_FOUND
WORK_INBOX_ITEM_SOURCE_NOT_FOUND
WORK_INBOX_ITEM_ACCESS_DENIED
WORK_INBOX_ACTION_NOT_ALLOWED

COMMAND_NOT_FOUND
COMMAND_DISABLED
COMMAND_CONTEXT_INVALID
COMMAND_PERMISSION_DENIED
COMMAND_CONFIRMATION_REQUIRED
COMMAND_EXECUTION_FAILED

NAVIGATION_ITEM_NOT_FOUND
NAVIGATION_PREFERENCE_INVALID
```

---

# 20. Required tests

## 20.1 Search tests

```text
globalSearch_returnsAllowedResults
globalSearch_excludesForbiddenProject
globalSearch_masksSensitiveFields
globalSearch_snippetDoesNotLeakHiddenFields
globalSearch_paginationWorks
searchScope_currentProjectLimitsResults
searchScope_myItemsLimitsResults
searchExternalPortal_clientVisibleOnly
searchAudit_createdForSensitiveSearchIfPolicy
```

## 20.2 Search index tests

```text
indexEntry_createdOrUpdatedFromSource
indexEntry_deletedOrArchivedWithSource
indexRebuild_idempotent
indexDoesNotStoreRestrictedBodyFields
indexWorkspaceScopeRequired
```

If using adapter approach, replace with adapter tests.

## 20.3 Saved search/view tests

```text
createSavedSearch_valid_success
runSavedSearch_rechecksPermissions
shareSavedSearch_withoutPermission_forbidden
archiveSavedSearch_success
createSavedView_valid_success
invalidSavedViewConfig_rejected
setDefaultSavedView_success
savedViewDoesNotBypassPermissions
```

## 20.4 Favorite/pin/recent tests

```text
createFavorite_visibleTarget_success
createFavorite_forbiddenTarget_rejected
favoriteDoesNotGrantAccessAfterPermissionLoss
createPersonalPin_success
createProjectPin_withoutPermission_forbidden
pinDoesNotGrantAccess
recordRecentItem_success
recentList_rechecksAccess
recentRetention_applied
```

## 20.5 Work inbox tests

```text
inboxIncludesAssignedTask
inboxIncludesMeetingAction
inboxIncludesMention
inboxExcludesForbiddenSource
inboxMasksSensitiveSource
dismissInboxItem_doesNotMutateSource
snoozeInboxItem_hidesUntilDate
sourceCompleted_marksInboxDone
inboxDuplicatePrevented
```

## 20.6 Command tests

```text
listCommands_permissionAware
executeCreateTaskCommand_success
executeCommand_withoutPermission_forbidden
dangerousCommand_requiresConfirmation
commandExecutionLogged
commandDoesNotBypassDomainValidation
```

## 20.7 Navigation tests

```text
workspaceNavigation_permissionAware
projectNavigation_permissionAware
navigationDoesNotGrantEndpointAccess
updateNavigationPreference_success
invalidNavigationPreference_ignoredOrRejected
badgeCounts_permissionAware
```

## 20.8 Portal productivity tests

```text
portalSearch_clientVisibleOnly
portalInbox_reviewRequestsOnly
portalRecent_separateFromInternal
portalNavigation_usesGrants
externalSearchDoesNotReturnInternalDocs
```

## 20.9 Event/audit tests

```text
productivityEventSeeder_firstRun_createsDefinitions
productivityEventSeeder_secondRun_noDuplicates
savedViewCreated_eventEmitted
commandExecuted_auditCreated
searchReindex_auditCreated
workInboxDismissed_eventEmitted
```

---

# 21. Manual verification checklist

Completion file must include:

```text
1. Search for a task by keyword.
2. Confirm forbidden project item is not returned.
3. Confirm sensitive finance/internal fields are hidden.
4. Save a search.
5. Run saved search after permission change and confirm re-check.
6. Create saved view for task list.
7. Set saved view as default.
8. Favorite a project/task/document.
9. Remove permission and confirm favorite no longer reveals data.
10. Pin project item.
11. Record recent item and view recents.
12. Open work inbox and confirm assigned task/meeting action/mention appears.
13. Dismiss and snooze inbox item.
14. Execute a safe command from command palette.
15. Confirm dangerous command requires confirmation.
16. Load navigation and confirm hidden modules are not shown.
17. Test portal search if Phase 30 exists.
18. Confirm no semantic/RAG/vector search is falsely claimed.
```

---

# 22. Acceptance criteria

Phase 32 is accepted only if:

```text
1. Current search/productivity capability is classified against TO-BE.
2. Global search implemented/tested.
3. Search result masking implemented/tested.
4. Search scopes implemented/tested.
5. Search indexing/adapters strategy documented and tested.
6. SavedSearch implemented/tested.
7. SavedView implemented/tested.
8. FavoriteItem implemented/tested.
9. PinnedItem implemented/tested.
10. RecentItem implemented/tested.
11. WorkInbox implemented/tested.
12. CommandDefinition/QuickAction implemented/tested.
13. Navigation metadata/preferences implemented/tested.
14. Notification/inbox integration implemented/tested.
15. Portal productivity subset implemented/tested or explicitly deferred if Phase 30 unavailable.
16. IAM permissions implemented/tested.
17. Event seeders idempotent.
18. Activity/audit/outbox follows Phase 04.
19. No semantic/RAG/vector search/workflow automation/realtime chat/push notification is falsely claimed.
20. `mvn compile` passes.
21. `mvn test` passes.
22. Completion file exists.
```

Do not mark complete if:

```text
search leaks forbidden object
search snippet leaks sensitive field
saved view bypasses permissions
favorite/pin/recent grants access
external portal search returns internal data
command bypasses domain action/IAM
navigation item grants endpoint access
tests fail
```

---

# 23. Required phase completion file

Agent must create:

```text
docs/phase-complete/PHASE_32_SEARCH_NAVIGATION_PRODUCTIVITY_TO_BE_COMPLETE.md
```

Required sections:

```text
# Phase 32 — Search / Navigation / Productivity Complete

## 1. Summary
## 2. Source Inputs Reviewed
## 3. Current vs TO-BE Classification Matrix
## 4. Implemented in Current BE
## 5. Implemented / Hardened in This Phase
## 6. Seed-only Items Added
## 7. Deferred Items and Target Phase
## 8. Search Boundary Decision
## 9. Search Implementation Strategy
## 10. Entity Mapping
## 11. API Changes
## 12. Permission / Masking Strategy
## 13. Saved Search Strategy
## 14. Saved View Strategy
## 15. Favorite / Pin / Recent Strategy
## 16. Work Inbox Strategy
## 17. Command Palette / Quick Action Strategy
## 18. Navigation Strategy
## 19. Notification Integration
## 20. Portal Productivity Strategy
## 21. Reporting Strategy
## 22. AI Suggestion Strategy
## 23. Authorization Matrix
## 24. Activity / Audit / Outbox Notes
## 25. Idempotency Strategy
## 26. Tests Added
## 27. Commands Run
## 28. Test Results
## 29. Manual Verification
## 30. Assumptions
## 31. Deviations From Prompt
## 32. Known Risks
## 33. Future Phases That Must Return to Search / Productivity
```

---

# 24. Future phases that must return

```text
Phase 34 — Workflow / Approval:
- Workflow task inbox, approval queue, SLA/escalation.

Phase 35 — Advanced Notifications:
- Digest, reminders, push/email preferences, quiet hours, inbox automation.

Phase 38 — Audit / Compliance / Privacy:
- Search audit retention, privacy controls, export/delete user productivity data.

Phase 39 — Integration / Import / Export:
- Browser extension search, Slack/Teams commands, calendar integrations.

Phase 41 — Data Quality / Knowledge Graph / Semantic Index:
- Semantic search, vector search, RAG, graph-based related results, AI Q&A.
```

---

# 25. Agent anti-bịa rules

The agent must not:

```text
1. Claim semantic search exists.
2. Claim vector embeddings exist.
3. Claim RAG over documents exists.
4. Claim knowledge graph exists.
5. Claim saved view is a report definition.
6. Claim work inbox is workflow engine.
7. Let search bypass IAM.
8. Let search snippet leak hidden fields.
9. Let favorites/pins/recents grant access.
10. Let command palette bypass domain actions.
11. Let navigation grant endpoint access.
12. Hide deferred semantic/RAG/workflow/push integration gaps.
```

---

# 26. Prompt to give coding agent

```text
You are implementing Phase 32 — TO-BE Search, Navigation, Saved Views, Command Palette, Personal Productivity & Work Inbox.

This is not an as-is documentation task.

Before coding, read:
- CLAUDE.md / CLAUDE.ms
- Coding_convention.md
- Phase 00–31 docs and completion files
- Current backend code, migrations, tests

Your task:
1. Compare current search/navigation/productivity capability against this Phase 32 TO-BE spec.
2. Classify each capability with required labels.
3. Implement permission-aware global search with masking.
4. Implement search scopes and chosen indexing/adapter strategy.
5. Implement SavedSearch and SavedView.
6. Implement FavoriteItem, PinnedItem, RecentItem.
7. Implement WorkInbox / WorkInboxItem.
8. Implement CommandDefinition / command execution metadata and safe QuickActions.
9. Implement NavigationMenuDefinition and UserNavigationPreference.
10. Integrate with Notification, Document, Task, Meeting, Requirement, Quality, RAID, Client Portal where available.
11. Add IAM permissions, events, reports, audit/outbox, idempotency, and tests.
12. Run mvn compile and mvn test.
13. Create docs/phase-complete/PHASE_32_SEARCH_NAVIGATION_PRODUCTIVITY_TO_BE_COMPLETE.md.

Do not implement or claim semantic search, vector embeddings, RAG, knowledge graph, realtime collaboration, browser push notification, workflow automation, or cross-tenant search in this phase.
```

---

# 27. Quick tracking matrix

| Capability | Current backend | Phase 32 action | Later phase |
|---|---|---|---|
| GlobalSearch | Missing/unknown | Must implement | Semantic Phase 41 |
| SearchResult masking | Missing/unknown | Must implement | — |
| Search scopes | Missing/unknown | Must implement | — |
| SearchIndex/adapters | Missing/unknown | Must implement strategy | Phase 41 advanced |
| SavedSearch | Missing/unknown | Must implement | — |
| SavedView | Missing/unknown | Must implement | — |
| FavoriteItem | Missing/unknown | Must implement | — |
| PinnedItem | Missing/unknown | Must implement | — |
| RecentItem | Missing/unknown | Must implement | Privacy Phase 38 |
| WorkInbox | Missing/unknown | Must implement | Workflow Phase 34 |
| WorkInboxItem | Missing/unknown | Must implement | Notifications Phase 35 |
| CommandDefinition | Missing/unknown | Must implement | Integrations Phase 39 |
| QuickAction | Missing/unknown | Must implement safe actions | Workflow Phase 34 |
| Navigation metadata | Missing/unknown | Must implement | — |
| User navigation preference | Missing/unknown | Must implement | — |
| Portal productivity subset | Missing/partial | Must implement if Phase 30 | — |
| Semantic search | Missing | Defer | Phase 41 |
| Vector embeddings | Missing | Defer | Phase 41 |
| RAG Q&A | Missing | Defer | Phase 41 |
| Workflow inbox | Missing | Defer | Phase 34 |
| Push notifications | Missing | Defer | Phase 35/39 |
| Slack/Teams commands | Missing | Defer | Phase 39 |

---

# 28. Final principle

Phase 32 is not complete when "search returns rows."

Phase 32 is complete when Scopery makes work easy to find and act on safely:

```text
permission-aware search
+ masked results
+ saved views
+ favorites / pins / recents
+ work inbox
+ command palette
+ navigation metadata
+ audit
= personal productivity without data leakage
```

Search is not permission.

Favorite is not access.

Pin is not access.

Navigation is not authorization.

Work inbox is not workflow engine.

Command palette must still use domain actions.

No semantic search without semantic index.
