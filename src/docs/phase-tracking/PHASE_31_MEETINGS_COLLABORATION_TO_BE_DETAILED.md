# PHASE 31 — TO-BE Meetings, Collaboration, Agenda, Minutes, Action Items, Decisions & Discussion Records

> Project: Scopery Backend  
> Phase: 31  
> Document type: TO-BE implementation-grade specification  
> Status: Planning / Gap-driven implementation spec  
> Roadmap group: Post-core Dynamic Work OS expansion  
> Depends on: Phase 00 — Master Backend Roadmap, Phase 01 — API Path / Security Baseline, Phase 02 — IAM Core, Phase 03 — Workspace / Organization / Team, Phase 04 — Platform Audit / Outbox / Idempotency, Phase 05 — Event Registry, Phase 06 — Notification Engine, Phase 09 — Project Core / WBS / Task, Phase 10 — Project Authorization, Phase 20 — Project Events / Notifications, Phase 22 — Reporting / Dashboard / Export, Phase 23 — Core Hardening, Phase 24 — Scope / Deliverable / Acceptance, Phase 25 — RAID / Decision Management, Phase 26 — Quality / Test / Release, Phase 27 — Document Hub / Generation, Phase 28 — Application / Requirement / Traceability, Phase 29 — External Party / Client CRM / Stakeholder, Phase 30 — Customer / External Collaboration Portal  
> API base: `/api`  
> Primary module: `modules/collaboration`, `modules/meeting`, or `modules/project/collaboration` depending on repository architecture  
> Related modules: `project`, `task`, `stakeholder`, `externalparty`, `clientportal`, `document`, `raid`, `decision`, `requirement`, `scope`, `deliverable`, `quality`, `notification`, `reporting`, `iam`, `eventregistry`, future `workflow`, `realtime-chat`, `calendar-integration`, `video-conference`, `semantic-index`  
> Important rule: Phase 31 introduces meeting and collaboration records. It does **not** implement a real-time chat system, video-call engine, full calendar sync, Slack/Teams sync, workflow automation, or legal e-signature.

---

# 0. Purpose

Phase 31 adds formal collaboration memory to Scopery.

Earlier phases created the work objects:

```text
Project / Task / Schedule
Scope / Deliverable / Acceptance
RAID / Decisions
Quality / Release
Document Hub
Requirements / Traceability
External Stakeholders
Client Portal
```

Phase 31 answers:

```text
What meetings happened on a project?
Who attended?
What was the agenda?
What was discussed?
What decisions were made?
What action items were created?
Which risks/issues/requirements/tasks/deliverables were discussed?
Which meeting minutes are approved?
Which external stakeholders participated?
Which client-visible meeting notes can be shown in portal?
What follow-ups are overdue?
How are meeting outcomes turned into tasks, RAID items, decisions, requirements, or change requests?
```

Phase 31 is the **meeting-to-action collaboration layer**.

---

# 1. Source inputs

Before coding Phase 31, the agent must read:

```text
1. Current backend codebase
2. Phase 09 Project Core implementation
3. Phase 10 Project Authorization implementation
4. Phase 20 Notification implementation
5. Phase 22 Reporting implementation
6. Phase 23 Core Hardening completion file
7. Phase 24 Scope / Deliverable / Acceptance implementation
8. Phase 25 RAID / Decision implementation
9. Phase 26 Quality / Test / Release implementation
10. Phase 27 Document Hub implementation
11. Phase 28 Requirement / Traceability implementation
12. Phase 29 External Party / Stakeholder implementation
13. Phase 30 External Portal implementation
14. Current IAM seeders
15. Current EventDefinition seeders
16. Existing meeting/comment/action/collaboration code if any
```

The coding agent must inspect the real code and not assume implementation.

---

# 2. Current expected backend state

After Phase 30, Scopery should have many artifacts that need discussion records:

```text
tasks
requirements
deliverables
RAID items
decisions
quality defects
release packages
documents
external stakeholders
client feedback
```

Likely missing:

```text
Meeting
MeetingSeries
MeetingParticipant
MeetingAgendaItem
MeetingMinute
MeetingNote
MeetingDecisionLink
MeetingActionItem
MeetingArtifactLink
MeetingDocumentLink
CommentThread
Comment
Mention
DiscussionRecord
Client-visible minutes policy
Meeting reports
```

Phase 31 implements those foundations.

---

# 3. Target statement

Phase 31 must deliver:

```text
1. Meeting records for project/workspace collaboration.
2. Optional recurring MeetingSeries foundation.
3. Participants including internal users and external contacts.
4. Agenda items linked to project artifacts.
5. Meeting notes/minutes with review/approval lifecycle.
6. Meeting action items with owner/due date/status and optional task creation.
7. Links from meetings to tasks, requirements, deliverables, RAID items, decisions, defects, releases, documents, and client feedback.
8. Comment threads on major project artifacts.
9. Mentions and notification integration.
10. Client-visible meeting note policy for future/existing portal.
11. Reporting for meetings, action items, decisions, and follow-ups.
12. AI-assisted meeting summary/action extraction as proposal only.
13. IAM, audit/outbox, idempotency, tests, and completion file.
```

---

# 4. Boundary decisions

## 4.1 Meeting record is not calendar integration

Phase 31 can store meeting time, participants, agenda, and minutes.

It does not implement full Google/Outlook calendar sync.

Calendar integration is deferred to Phase 39 unless simple export/import exists.

## 4.2 Meeting record is not video call

Phase 31 can store meeting URL/reference.

It does not host video/audio calls.

## 4.3 Meeting action is not always task

MeetingActionItem can become a Project Task, RAID action, or remain a lightweight follow-up.

Creating a linked task must be explicit.

## 4.4 Meeting decision is not approval workflow

Meeting minutes can record a decision.

DecisionRecord from Phase 25 remains the formal decision object.

Phase 31 can create/link DecisionRecord but does not implement workflow approval.

## 4.5 Comments are asynchronous collaboration, not realtime chat

Phase 31 can implement comments/threads.

It does not implement websocket-based realtime chat, presence, typing indicators, or channels.

---

# 5. Classification labels

Use these labels in completion file:

| Label | Meaning |
|---|---|
| `CURRENTLY_IMPLEMENTED` | Existing backend already supports it with tests. |
| `PARTIALLY_IMPLEMENTED` | Some support exists but gaps remain. |
| `MUST_IMPLEMENT_IN_PHASE_31` | Required now. |
| `SEED_ONLY_IN_PHASE_31` | Seed definitions/events/permissions only. |
| `DEFERRED_TO_PHASE_XX` | Later phase returns. |
| `DEFERRED_TO_POST_23_BACKLOG` | Dynamic Work OS expansion backlog. |
| `NOT_IN_SCOPE_FOR_PHASE_31` | Explicitly not in this phase. |

---

# 6. Required capabilities

---

## 6.1 MTG-001 — Meeting

Classification: `MUST_IMPLEMENT_IN_PHASE_31`

Purpose:

```text
Record a project/workspace meeting.
```

Meeting types:

```text
PROJECT_STATUS
CLIENT_STATUS
SPRINT_PLANNING
SPRINT_REVIEW
RETROSPECTIVE
REQUIREMENT_REVIEW
DESIGN_REVIEW
TECHNICAL_REVIEW
RAID_REVIEW
CHANGE_CONTROL
QUALITY_REVIEW
RELEASE_READINESS
UAT_REVIEW
DELIVERABLE_ACCEPTANCE
DECISION_MEETING
GENERAL
OTHER
```

Rules:

```text
1. Meeting belongs to workspace and optionally project.
2. Title required.
3. Start/end time required.
4. Organizer required.
5. Meeting can be internal-only or client-visible.
6. Meeting can link to many artifacts.
7. Meeting does not grant artifact access.
8. External participants do not become internal users.
```

Status:

```text
DRAFT
SCHEDULED
IN_PROGRESS
COMPLETED
CANCELLED
ARCHIVED
```

---

## 6.2 MTG-002 — MeetingSeries

Classification: `MUST_IMPLEMENT_IN_PHASE_31` basic.

Purpose:

```text
Group recurring meetings without implementing full calendar recurrence engine.
```

Rules:

```text
1. Series belongs to workspace/project.
2. Series has name, cadence, owner.
3. Individual Meeting records still store actual occurrence times.
4. Full recurrence/calendar sync deferred to Phase 39.
```

Cadence examples:

```text
DAILY
WEEKLY
BIWEEKLY
MONTHLY
CUSTOM
```

---

## 6.3 MTG-003 — MeetingParticipant

Classification: `MUST_IMPLEMENT_IN_PHASE_31`

Participant target types:

```text
INTERNAL_USER
WORKSPACE_MEMBER
TEAM
EXTERNAL_CONTACT
EXTERNAL_ORGANIZATION
ROLE_PLACEHOLDER
```

Participant roles:

```text
ORGANIZER
FACILITATOR
ATTENDEE
OPTIONAL
PRESENTER
REVIEWER
APPROVER
CLIENT_REPRESENTATIVE
OBSERVER
```

Attendance status:

```text
INVITED
ACCEPTED
DECLINED
TENTATIVE
ATTENDED
NO_SHOW
EXCUSED
```

Rules:

```text
1. Participant target must exist.
2. External participant does not grant portal access.
3. Attendance updates audited.
4. Client-visible minutes must not expose internal-only participants if policy says mask.
```

---

## 6.4 MTG-004 — MeetingAgendaItem

Classification: `MUST_IMPLEMENT_IN_PHASE_31`

Purpose:

```text
Define structured agenda topics for meeting.
```

Rules:

```text
1. Agenda item belongs to Meeting.
2. Title required.
3. Sort order required.
4. Owner optional.
5. Agenda item can link to artifact.
6. Agenda status can be tracked.
```

Status:

```text
OPEN
DISCUSSED
DEFERRED
CANCELLED
```

---

## 6.5 MTG-005 — MeetingMinutes

Classification: `MUST_IMPLEMENT_IN_PHASE_31`

Purpose:

```text
Record official minutes / summary for meeting.
```

Rules:

```text
1. Minutes belong to Meeting.
2. Draft minutes can be edited.
3. Approved minutes immutable except revision.
4. Approval/rejection requires permission.
5. Client-visible minutes must be sanitized/masked.
6. Minutes can be generated document via Phase 27.
```

Status:

```text
DRAFT
IN_REVIEW
APPROVED
REJECTED
ARCHIVED
```

---

## 6.6 MTG-006 — MeetingNote

Classification: `MUST_IMPLEMENT_IN_PHASE_31`

Purpose:

```text
Capture discussion notes by agenda/artifact/topic.
```

Note types:

```text
SUMMARY
DISCUSSION
DECISION_NOTE
RISK_NOTE
ISSUE_NOTE
ACTION_NOTE
CLIENT_NOTE
INTERNAL_NOTE
OTHER
```

Rules:

```text
1. Note belongs to Meeting and optionally AgendaItem.
2. Internal-only note must not be shown to external portal.
3. Notes can link to artifacts.
4. Notes can be promoted to DecisionRecord, RaidItem, Requirement, Defect, ChangeRequest, or Task by explicit action.
```

---

## 6.7 ACT-001 — MeetingActionItem

Classification: `MUST_IMPLEMENT_IN_PHASE_31`

Purpose:

```text
Track follow-ups from meetings.
```

Action status:

```text
OPEN
IN_PROGRESS
DONE
BLOCKED
CANCELLED
OVERDUE
ARCHIVED
```

Rules:

```text
1. Action belongs to Meeting.
2. Owner can be internal user or external contact.
3. Due date optional but recommended.
4. Action can link to Project Task.
5. Creating linked task requires task create permission.
6. External owner does not become task assignee unless external collaboration policy supports it.
7. Action completion audited.
```

---

## 6.8 ACT-002 — Create linked task from meeting action

Classification: `MUST_IMPLEMENT_IN_PHASE_31`

Rules:

```text
1. User must have TASK_CREATE permission.
2. MeetingActionItem must belong to same project.
3. Created task links back to action item.
4. Idempotency prevents duplicate task from same action.
5. If project is baselined and task changes controlled scope/schedule, ChangeRequest may be required by policy.
```

---

## 6.9 LNK-001 — MeetingArtifactLink

Classification: `MUST_IMPLEMENT_IN_PHASE_31`

Targets:

```text
PROJECT_PHASE
WBS_NODE
TASK
DELIVERABLE
ACCEPTANCE_CRITERIA
REQUIREMENT
TEST_CASE
TEST_RUN
DEFECT
RELEASE_PACKAGE
RAID_ITEM
DECISION
CHANGE_REQUEST
BASELINE
DOCUMENT
QUOTE_VERSION
CLIENT_FEEDBACK
EXTERNAL_CONTACT
```

Link types:

```text
DISCUSSED
REVIEWED
DECIDED
ACTION_FOR
BLOCKED_BY
FOLLOW_UP_FOR
EVIDENCE
REFERENCE
```

Rules:

```text
1. Target must belong to same workspace/project when applicable.
2. Link does not grant access.
3. Duplicate active links prevented.
4. Sensitive targets masked in external/client-visible views.
```

---

## 6.10 CMT-001 — CommentThread

Classification: `MUST_IMPLEMENT_IN_PHASE_31`

Purpose:

```text
Support asynchronous comments on project artifacts.
```

Thread target types:

```text
PROJECT
TASK
DELIVERABLE
REQUIREMENT
TEST_CASE
DEFECT
RELEASE_PACKAGE
RAID_ITEM
DECISION
CHANGE_REQUEST
DOCUMENT
QUOTE_VERSION
CLIENT_FEEDBACK
```

Rules:

```text
1. Thread belongs to workspace/project.
2. Target must exist and be accessible.
3. Thread can be internal-only or client-visible.
4. Thread does not grant artifact access.
5. Thread can be resolved/archived.
```

---

## 6.11 CMT-002 — Comment

Classification: `MUST_IMPLEMENT_IN_PHASE_31`

Purpose:

```text
Record comments/replies on a thread.
```

Rules:

```text
1. Comment belongs to Thread.
2. Author can be internal user or external portal account if portal-enabled.
3. Body required.
4. Internal-only comment hidden from external users.
5. Edits/deletes follow policy and are audited.
6. Mentions can trigger notifications.
```

Status:

```text
ACTIVE
EDITED
DELETED_SOFT
ARCHIVED
```

---

## 6.12 MEN-001 — Mention

Classification: `MUST_IMPLEMENT_IN_PHASE_31`

Purpose:

```text
Mention users, teams, external contacts, or roles in comments/notes/action items.
```

Mention target types:

```text
INTERNAL_USER
TEAM
EXTERNAL_CONTACT
PROJECT_ROLE
STAKEHOLDER
```

Rules:

```text
1. Mention target must exist.
2. Mention does not grant access.
3. Notification only sent if target has access/policy allows.
4. External mention notification only if portal/external notification enabled.
```

---

## 6.13 DEC-001 — Meeting to DecisionRecord

Classification: `MUST_IMPLEMENT_IN_PHASE_31`

Purpose:

```text
Create or link formal DecisionRecord from meeting discussion.
```

Rules:

```text
1. Creating DecisionRecord requires Phase 25 decision permission.
2. Meeting note can become DecisionRecord source.
3. DecisionRecord links back to Meeting/AgendaItem/Note.
4. Meeting minutes alone do not equal formal decision unless linked/created.
```

---

## 6.14 RAID-001 — Meeting to RAID

Classification: `MUST_IMPLEMENT_IN_PHASE_31`

Purpose:

```text
Create Risk/Issue/Assumption/Dependency from meeting notes/actions.
```

Rules:

```text
1. Creating RAID item requires Phase 25 permission.
2. Meeting note/action can be source.
3. RAID item links back to meeting.
4. AI can suggest RAID extraction, but human must apply.
```

---

## 6.15 REQ-001 — Meeting to Requirement / ChangeRequest

Classification: `MUST_IMPLEMENT_IN_PHASE_31` if Phase 28/19 exists.

Rules:

```text
1. Meeting note/client discussion can create Requirement draft.
2. Meeting decision can create ChangeRequest draft.
3. Applying change still uses ChangeRequest workflow.
4. Meeting does not mutate approved requirements directly.
```

---

## 6.16 DOC-001 — Meeting documents

Classification: `MUST_IMPLEMENT_IN_PHASE_31` if Phase 27 exists.

Documents:

```text
meeting agenda document
meeting minutes document
attachments
recording link/reference
presentation
supporting evidence
```

Rules:

```text
1. Meeting documents use Document Hub.
2. File access controlled by DocumentHub.
3. Recording URL/reference does not grant public access.
4. Generated minutes document can be created from MeetingMinutes.
```

---

## 6.17 EXT-001 — External/client-visible meeting view

Classification: `MUST_IMPLEMENT_IN_PHASE_31` if Phase 30 exists.

Rules:

```text
1. External portal can only see meetings explicitly client-visible or shared.
2. Internal-only notes/comments/participants hidden.
3. External comments become ClientComment or regular comment with external author according to architecture.
4. External participant does not see meeting unless project grant and visibility policy allow.
```

---

## 6.18 RPT-001 — Meeting / collaboration reporting

Classification: `MUST_IMPLEMENT_IN_PHASE_31`

Reports:

```text
meeting register
meeting minutes status report
open meeting action items
overdue meeting actions
decision from meetings report
RAID from meetings report
meeting artifact coverage
comment activity report
client meeting report
```

Dashboard KPIs:

```text
meetingsThisPeriod
openMeetingActions
overdueMeetingActions
minutesPendingApproval
decisionsCreatedFromMeetings
risksCreatedFromMeetings
clientMeetingsThisPeriod
unresolvedCommentThreads
```

---

## 6.19 AI-001 — AI-assisted meeting summary/action extraction

Classification: `SEED_ONLY_IN_PHASE_31` or `MUST_IMPLEMENT_IN_PHASE_31` if Phase 21 tool registry exists.

AI can suggest:

```text
meeting summary
action items
decisions
risks/issues
requirements
change request drafts
minutes document draft
follow-up messages
```

Rules:

```text
1. AI suggestions are proposal only.
2. Human approval required to create tasks, RAID, decisions, requirements, or ChangeRequests.
3. AI cannot approve minutes.
4. AI cannot notify external users automatically.
5. AI must respect document/meeting/project access.
6. AI-generated minutes should be clearly marked as draft.
```

---

# 7. Entity model TO-BE

---

## 7.1 MeetingSeries — `collab_meeting_series`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
project_id UUID NULL
code VARCHAR(100) NULL
title VARCHAR(255) NOT NULL
description TEXT NULL
cadence VARCHAR(50) NULL
owner_user_id UUID NULL
status VARCHAR(50) NOT NULL
client_visible BOOLEAN NOT NULL DEFAULT false
created_at / created_by
updated_at / updated_by
archived_at / archived_by
version INT
```

Status:

```text
ACTIVE
PAUSED
ARCHIVED
```

---

## 7.2 Meeting — `collab_meeting`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
project_id UUID NULL
meeting_series_id UUID NULL
title VARCHAR(255) NOT NULL
description TEXT NULL
meeting_type VARCHAR(100) NOT NULL
status VARCHAR(50) NOT NULL
start_at TIMESTAMP NOT NULL
end_at TIMESTAMP NULL
timezone VARCHAR(100) NULL
location TEXT NULL
meeting_url VARCHAR(1000) NULL
organizer_user_id UUID NULL
client_visible BOOLEAN NOT NULL DEFAULT false
internal_only BOOLEAN NOT NULL DEFAULT true
created_at / created_by
updated_at / updated_by
cancelled_at / cancelled_by
archived_at / archived_by
trace_id VARCHAR(100) NULL
version INT
```

---

## 7.3 MeetingParticipant — `collab_meeting_participant`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
project_id UUID NULL
meeting_id UUID NOT NULL
target_type VARCHAR(50) NOT NULL
target_id UUID NULL
display_name_snapshot VARCHAR(255) NULL
email_snapshot VARCHAR(320) NULL
participant_role VARCHAR(50) NOT NULL
attendance_status VARCHAR(50) NOT NULL
client_visible BOOLEAN NOT NULL DEFAULT false
created_at / created_by
updated_at / updated_by
version INT
```

---

## 7.4 MeetingAgendaItem — `collab_meeting_agenda_item`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
project_id UUID NULL
meeting_id UUID NOT NULL
title VARCHAR(255) NOT NULL
description TEXT NULL
owner_user_id UUID NULL
status VARCHAR(50) NOT NULL
sort_order INT NOT NULL DEFAULT 0
timebox_minutes INT NULL
client_visible BOOLEAN NOT NULL DEFAULT false
created_at / created_by
updated_at / updated_by
archived_at / archived_by
version INT
```

---

## 7.5 MeetingMinutes — `collab_meeting_minutes`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
project_id UUID NULL
meeting_id UUID NOT NULL
status VARCHAR(50) NOT NULL
summary TEXT NULL
decisions_summary TEXT NULL
actions_summary TEXT NULL
client_visible_summary TEXT NULL
document_id UUID NULL
document_version_id UUID NULL
submitted_at TIMESTAMP NULL
submitted_by UUID NULL
approved_at TIMESTAMP NULL
approved_by UUID NULL
rejected_at TIMESTAMP NULL
rejected_by UUID NULL
rejection_reason TEXT NULL
created_at / created_by
updated_at / updated_by
trace_id VARCHAR(100) NULL
version INT
```

---

## 7.6 MeetingNote — `collab_meeting_note`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
project_id UUID NULL
meeting_id UUID NOT NULL
agenda_item_id UUID NULL
note_type VARCHAR(50) NOT NULL
body TEXT NOT NULL
internal_only BOOLEAN NOT NULL DEFAULT true
client_visible BOOLEAN NOT NULL DEFAULT false
source_ai_suggestion_id UUID NULL
created_at / created_by
updated_at / updated_by
archived_at / archived_by
version INT
```

---

## 7.7 MeetingActionItem — `collab_meeting_action_item`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
project_id UUID NULL
meeting_id UUID NOT NULL
agenda_item_id UUID NULL
title VARCHAR(255) NOT NULL
description TEXT NULL
owner_target_type VARCHAR(50) NULL
owner_target_id UUID NULL
due_date DATE NULL
status VARCHAR(50) NOT NULL
linked_task_id UUID NULL
linked_raid_action_id UUID NULL
completed_at TIMESTAMP NULL
completed_by UUID NULL
completion_note TEXT NULL
client_visible BOOLEAN NOT NULL DEFAULT false
created_at / created_by
updated_at / updated_by
archived_at / archived_by
trace_id VARCHAR(100) NULL
version INT
```

---

## 7.8 MeetingArtifactLink — `collab_meeting_artifact_link`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
project_id UUID NULL
meeting_id UUID NOT NULL
agenda_item_id UUID NULL
note_id UUID NULL
action_item_id UUID NULL
target_type VARCHAR(100) NOT NULL
target_id UUID NOT NULL
link_type VARCHAR(50) NOT NULL
created_at / created_by
archived_at / archived_by
version INT
```

---

## 7.9 CommentThread — `collab_comment_thread`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
project_id UUID NULL
target_type VARCHAR(100) NOT NULL
target_id UUID NOT NULL
title VARCHAR(255) NULL
status VARCHAR(50) NOT NULL
internal_only BOOLEAN NOT NULL DEFAULT true
client_visible BOOLEAN NOT NULL DEFAULT false
resolved_at TIMESTAMP NULL
resolved_by UUID NULL
created_at / created_by
updated_at / updated_by
archived_at / archived_by
trace_id VARCHAR(100) NULL
version INT
```

Status:

```text
OPEN
RESOLVED
ARCHIVED
```

---

## 7.10 Comment — `collab_comment`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
project_id UUID NULL
thread_id UUID NOT NULL
parent_comment_id UUID NULL
author_type VARCHAR(50) NOT NULL
author_id UUID NULL
author_display_name_snapshot VARCHAR(255) NULL
body TEXT NOT NULL
status VARCHAR(50) NOT NULL
internal_only BOOLEAN NOT NULL DEFAULT true
client_visible BOOLEAN NOT NULL DEFAULT false
created_at TIMESTAMP NOT NULL
edited_at TIMESTAMP NULL
edited_by UUID NULL
deleted_at TIMESTAMP NULL
deleted_by UUID NULL
trace_id VARCHAR(100) NULL
version INT
```

Author types:

```text
INTERNAL_USER
EXTERNAL_PORTAL_ACCOUNT
SYSTEM
AI_ASSISTANT
```

---

## 7.11 Mention — `collab_mention`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
project_id UUID NULL
source_type VARCHAR(50) NOT NULL
source_id UUID NOT NULL
target_type VARCHAR(50) NOT NULL
target_id UUID NULL
created_at / created_by
notification_sent BOOLEAN NOT NULL DEFAULT false
version INT
```

Source types:

```text
COMMENT
MEETING_NOTE
MEETING_ACTION_ITEM
MEETING_MINUTES
```

---

# 8. API TO-BE list

All APIs use `/api`.

---

## 8.1 Meeting series APIs

```text
POST  /api/projects/{projectId}/meeting-series
GET   /api/projects/{projectId}/meeting-series
GET   /api/projects/{projectId}/meeting-series/{seriesId}
PUT   /api/projects/{projectId}/meeting-series/{seriesId}
PATCH /api/projects/{projectId}/meeting-series/{seriesId}/pause
PATCH /api/projects/{projectId}/meeting-series/{seriesId}/archive
```

Workspace-level variant optional:

```text
POST /api/workspaces/{workspaceId}/meeting-series
GET  /api/workspaces/{workspaceId}/meeting-series
```

---

## 8.2 Meeting APIs

```text
POST  /api/projects/{projectId}/meetings
GET   /api/projects/{projectId}/meetings
GET   /api/projects/{projectId}/meetings/{meetingId}
PUT   /api/projects/{projectId}/meetings/{meetingId}
POST  /api/projects/{projectId}/meetings/{meetingId}/start
POST  /api/projects/{projectId}/meetings/{meetingId}/complete
POST  /api/projects/{projectId}/meetings/{meetingId}/cancel
PATCH /api/projects/{projectId}/meetings/{meetingId}/archive
```

Filters:

```text
meetingType
status
dateFrom
dateTo
seriesId
clientVisible
participantId
```

---

## 8.3 Participant APIs

```text
POST   /api/projects/{projectId}/meetings/{meetingId}/participants
GET    /api/projects/{projectId}/meetings/{meetingId}/participants
PUT    /api/projects/{projectId}/meetings/{meetingId}/participants/{participantId}
DELETE /api/projects/{projectId}/meetings/{meetingId}/participants/{participantId}
POST   /api/projects/{projectId}/meetings/{meetingId}/participants/{participantId}/mark-attended
```

---

## 8.4 Agenda APIs

```text
POST   /api/projects/{projectId}/meetings/{meetingId}/agenda-items
GET    /api/projects/{projectId}/meetings/{meetingId}/agenda-items
PUT    /api/projects/{projectId}/meetings/{meetingId}/agenda-items/{agendaItemId}
DELETE /api/projects/{projectId}/meetings/{meetingId}/agenda-items/{agendaItemId}
PUT    /api/projects/{projectId}/meetings/{meetingId}/agenda-items/reorder
```

---

## 8.5 Minutes / notes APIs

```text
POST /api/projects/{projectId}/meetings/{meetingId}/minutes
GET  /api/projects/{projectId}/meetings/{meetingId}/minutes
PUT  /api/projects/{projectId}/meetings/{meetingId}/minutes/{minutesId}
POST /api/projects/{projectId}/meetings/{meetingId}/minutes/{minutesId}/submit-review
POST /api/projects/{projectId}/meetings/{meetingId}/minutes/{minutesId}/approve
POST /api/projects/{projectId}/meetings/{meetingId}/minutes/{minutesId}/reject
POST /api/projects/{projectId}/meetings/{meetingId}/minutes/{minutesId}/generate-document

POST /api/projects/{projectId}/meetings/{meetingId}/notes
GET  /api/projects/{projectId}/meetings/{meetingId}/notes
PUT  /api/projects/{projectId}/meetings/{meetingId}/notes/{noteId}
PATCH /api/projects/{projectId}/meetings/{meetingId}/notes/{noteId}/archive
```

---

## 8.6 Meeting action APIs

```text
POST  /api/projects/{projectId}/meetings/{meetingId}/action-items
GET   /api/projects/{projectId}/meetings/{meetingId}/action-items
GET   /api/projects/{projectId}/meeting-action-items/{actionItemId}
PUT   /api/projects/{projectId}/meeting-action-items/{actionItemId}
POST  /api/projects/{projectId}/meeting-action-items/{actionItemId}/complete
POST  /api/projects/{projectId}/meeting-action-items/{actionItemId}/create-linked-task
PATCH /api/projects/{projectId}/meeting-action-items/{actionItemId}/archive
```

---

## 8.7 Artifact link APIs

```text
POST   /api/projects/{projectId}/meetings/{meetingId}/artifact-links
GET    /api/projects/{projectId}/meetings/{meetingId}/artifact-links
DELETE /api/projects/{projectId}/meetings/{meetingId}/artifact-links/{linkId}

POST /api/projects/{projectId}/meetings/{meetingId}/notes/{noteId}/create-decision
POST /api/projects/{projectId}/meetings/{meetingId}/notes/{noteId}/create-raid-item
POST /api/projects/{projectId}/meetings/{meetingId}/notes/{noteId}/create-requirement
POST /api/projects/{projectId}/meetings/{meetingId}/notes/{noteId}/create-change-request-draft
```

---

## 8.8 Comment thread APIs

```text
POST /api/projects/{projectId}/comments/threads
GET  /api/projects/{projectId}/comments/threads
GET  /api/projects/{projectId}/comments/threads/{threadId}
POST /api/projects/{projectId}/comments/threads/{threadId}/resolve
PATCH /api/projects/{projectId}/comments/threads/{threadId}/archive

POST /api/projects/{projectId}/comments/threads/{threadId}/comments
GET  /api/projects/{projectId}/comments/threads/{threadId}/comments
PUT  /api/projects/{projectId}/comments/{commentId}
POST /api/projects/{projectId}/comments/{commentId}/delete
```

Target convenience:

```text
GET /api/projects/{projectId}/comments/by-target?targetType=TASK&targetId=...
```

---

## 8.9 Portal/client visible APIs

If Phase 30 exists:

```text
GET /api/portal/projects/{projectId}/meetings
GET /api/portal/projects/{projectId}/meetings/{meetingId}
GET /api/portal/projects/{projectId}/meetings/{meetingId}/minutes
GET /api/portal/projects/{projectId}/meetings/{meetingId}/comments
POST /api/portal/projects/{projectId}/meetings/{meetingId}/comments
```

Rules:

```text
Only client-visible and policy-allowed content.
```

---

## 8.10 Reports APIs

```text
GET /api/projects/{projectId}/reports/meetings
GET /api/projects/{projectId}/reports/meeting-actions
GET /api/projects/{projectId}/reports/overdue-meeting-actions
GET /api/projects/{projectId}/reports/meeting-minutes-status
GET /api/projects/{projectId}/reports/decisions-from-meetings
GET /api/projects/{projectId}/reports/raid-from-meetings
GET /api/projects/{projectId}/reports/comment-activity
```

---

# 9. Authorization requirements

Required authorities:

```text
PROJECT_MEETING_VIEW
PROJECT_MEETING_CREATE
PROJECT_MEETING_UPDATE
PROJECT_MEETING_CANCEL
PROJECT_MEETING_ARCHIVE
PROJECT_MEETING_SERIES_MANAGE

PROJECT_MEETING_PARTICIPANT_VIEW
PROJECT_MEETING_PARTICIPANT_MANAGE
PROJECT_MEETING_AGENDA_VIEW
PROJECT_MEETING_AGENDA_MANAGE

PROJECT_MEETING_MINUTES_VIEW
PROJECT_MEETING_MINUTES_CREATE
PROJECT_MEETING_MINUTES_UPDATE
PROJECT_MEETING_MINUTES_SUBMIT
PROJECT_MEETING_MINUTES_APPROVE
PROJECT_MEETING_MINUTES_REJECT
PROJECT_MEETING_MINUTES_GENERATE_DOCUMENT

PROJECT_MEETING_NOTE_VIEW
PROJECT_MEETING_NOTE_CREATE
PROJECT_MEETING_NOTE_UPDATE
PROJECT_MEETING_NOTE_ARCHIVE

PROJECT_MEETING_ACTION_VIEW
PROJECT_MEETING_ACTION_CREATE
PROJECT_MEETING_ACTION_UPDATE
PROJECT_MEETING_ACTION_COMPLETE
PROJECT_MEETING_ACTION_CREATE_TASK
PROJECT_MEETING_ACTION_ARCHIVE

PROJECT_MEETING_LINK_MANAGE

PROJECT_COMMENT_THREAD_VIEW
PROJECT_COMMENT_THREAD_CREATE
PROJECT_COMMENT_THREAD_RESOLVE
PROJECT_COMMENT_THREAD_ARCHIVE
PROJECT_COMMENT_CREATE
PROJECT_COMMENT_UPDATE
PROJECT_COMMENT_DELETE

PROJECT_CLIENT_VISIBLE_COLLABORATION_MANAGE
PROJECT_MEETING_REPORT_VIEW
PROJECT_COMMENT_REPORT_VIEW
```

Portal capabilities if Phase 30:

```text
PORTAL_VIEW_MEETINGS
PORTAL_VIEW_MEETING_MINUTES
PORTAL_CREATE_MEETING_COMMENT
```

Rules:

```text
1. Project access required.
2. Artifact links require access to target.
3. External participants do not get access from participation alone.
4. Client-visible content requires clientVisible=true and portal grant.
5. Internal-only notes/comments hidden from external users.
```

---

# 10. Lifecycle rules

## 10.1 Meeting lifecycle

```text
DRAFT → SCHEDULED → IN_PROGRESS → COMPLETED
DRAFT/SCHEDULED/IN_PROGRESS → CANCELLED
Any non-final → ARCHIVED
```

Rules:

```text
1. Completed meeting can still have minutes/actions added if permission allows.
2. Cancelled meeting cannot create action items unless policy allows.
3. Meeting archive does not delete minutes/actions.
```

## 10.2 Minutes lifecycle

```text
DRAFT → IN_REVIEW → APPROVED
IN_REVIEW → REJECTED
APPROVED → ARCHIVED
```

Rules:

```text
1. Approved minutes immutable except revision.
2. Rejection requires reason.
3. Client-visible summary is separate from internal summary.
```

## 10.3 Action item lifecycle

```text
OPEN → IN_PROGRESS → DONE
OPEN/IN_PROGRESS → BLOCKED
Any non-final → CANCELLED
Any → ARCHIVED
```

Rules:

```text
1. Complete requires permission.
2. Overdue can be derived from dueDate/status.
3. Linked task creation idempotent.
```

## 10.4 Comment lifecycle

```text
ACTIVE → EDITED
ACTIVE/EDITED → DELETED_SOFT
ACTIVE/EDITED → ARCHIVED
```

Rules:

```text
1. Delete is soft delete.
2. Edit/delete audited.
3. External comment policy separate from internal.
```

---

# 11. Integration rules

## 11.1 Task integration

Rules:

```text
1. MeetingActionItem can create linked Task.
2. Task can link back to MeetingActionItem.
3. Task comments can use CommentThread.
4. Creating task after baseline may require ChangeRequest by policy.
```

## 11.2 Decision integration

Rules:

```text
1. Meeting note can create DecisionRecord.
2. DecisionRecord links to Meeting/Agenda/Note.
3. Decision approval workflow deferred to Phase 34.
```

## 11.3 RAID integration

Rules:

```text
1. Meeting note/action can create RaidItem or RaidAction.
2. RAID item links back to meeting.
3. Critical meeting-raised risk/issue can notify stakeholders.
```

## 11.4 Requirement / ChangeRequest integration

Rules:

```text
1. Meeting note can create Requirement draft.
2. Meeting note can create ChangeRequest draft.
3. Approved requirement/change flow still uses existing domain actions.
```

## 11.5 Document integration

Rules:

```text
1. Meeting attachments use Document Hub.
2. Generated minutes document uses GeneratedDocumentJob if available.
3. Document access controlled by DocumentHub.
```

## 11.6 External portal integration

Rules:

```text
1. External users see only client-visible meetings/minutes/comments.
2. External participation does not grant portal access.
3. External comments are audited.
4. Internal-only content masked/omitted.
```

## 11.7 Notification integration

Notifications for:

```text
meeting scheduled/updated/cancelled
participant added
minutes submitted/approved/rejected
action item assigned/overdue/completed
mention created
comment added
decision created from meeting
RAID item created from meeting
```

---

# 12. Reporting integration

Extend Phase 22 with:

```text
PROJECT_MEETING_REGISTER_REPORT
PROJECT_MEETING_MINUTES_STATUS_REPORT
PROJECT_MEETING_ACTION_REPORT
PROJECT_OVERDUE_MEETING_ACTION_REPORT
PROJECT_MEETING_DECISION_REPORT
PROJECT_MEETING_RAID_REPORT
PROJECT_COMMENT_ACTIVITY_REPORT
CLIENT_MEETING_REPORT
```

Masking:

```text
internal-only notes/comments omitted from client reports
external participants masked if privacy policy requires
```

---

# 13. AI integration

If Phase 21 tool registry exists, seed tools/prompts:

```text
summarizeMeetingNotes
extractMeetingActionItems
extractMeetingDecisions
extractMeetingRisksIssues
draftMeetingMinutes
draftFollowUpMessage
suggestMeetingAgenda
```

Rules:

```text
1. AI output is proposal only.
2. AI-generated minutes are DRAFT.
3. AI cannot approve minutes.
4. AI cannot create tasks/RAID/decisions without human apply.
5. AI cannot notify external users automatically.
6. AI must respect meeting/document/project access.
```

---

# 14. Event Registry integration

Recommended source system:

```text
SCOPERY_COLLABORATION
```

Required events:

```text
MEETING_SERIES_CREATED
MEETING_SERIES_UPDATED
MEETING_SERIES_ARCHIVED

MEETING_CREATED
MEETING_UPDATED
MEETING_STARTED
MEETING_COMPLETED
MEETING_CANCELLED
MEETING_ARCHIVED

MEETING_PARTICIPANT_ADDED
MEETING_PARTICIPANT_UPDATED
MEETING_PARTICIPANT_REMOVED
MEETING_PARTICIPANT_MARKED_ATTENDED

MEETING_AGENDA_ITEM_CREATED
MEETING_AGENDA_ITEM_UPDATED
MEETING_AGENDA_ITEM_ARCHIVED

MEETING_MINUTES_CREATED
MEETING_MINUTES_UPDATED
MEETING_MINUTES_SUBMITTED
MEETING_MINUTES_APPROVED
MEETING_MINUTES_REJECTED
MEETING_MINUTES_DOCUMENT_GENERATED

MEETING_NOTE_CREATED
MEETING_NOTE_UPDATED
MEETING_NOTE_ARCHIVED

MEETING_ACTION_ITEM_CREATED
MEETING_ACTION_ITEM_UPDATED
MEETING_ACTION_ITEM_COMPLETED
MEETING_ACTION_ITEM_OVERDUE
MEETING_ACTION_ITEM_LINKED_TASK_CREATED
MEETING_ACTION_ITEM_ARCHIVED

MEETING_ARTIFACT_LINK_CREATED
MEETING_ARTIFACT_LINK_REMOVED

COMMENT_THREAD_CREATED
COMMENT_THREAD_RESOLVED
COMMENT_CREATED
COMMENT_UPDATED
COMMENT_DELETED

MENTION_CREATED
MEETING_DECISION_CREATED
MEETING_RAID_ITEM_CREATED
MEETING_REQUIREMENT_CREATED
MEETING_CHANGE_REQUEST_DRAFT_CREATED
```

Standard variables:

```text
actor.userId
workspace.id
project.id
meeting.id
meeting.title
meetingSeries.id
participant.id
agendaItem.id
minutes.id
note.id
actionItem.id
commentThread.id
comment.id
target.type
target.id
occurredAt
traceId
```

---

# 15. Audit / activity / outbox

Audit-sensitive actions:

```text
client-visible meeting published
minutes approved/rejected
internal-only note changed
external participant added/removed
meeting action linked to task
meeting note converted to decision/RAID/requirement/change request
comment deleted
external comment created
```

Activity actions:

```text
MEETING_CREATED
MEETING_COMPLETED
MEETING_MINUTES_APPROVED
MEETING_ACTION_ITEM_CREATED
MEETING_ACTION_ITEM_COMPLETED
MEETING_DECISION_CREATED
COMMENT_CREATED
```

Outbox required for major collaboration events.

Idempotency recommended for:

```text
POST /meetings
POST /meeting-action-items/{id}/create-linked-task
POST /notes/{id}/create-decision
POST /notes/{id}/create-raid-item
POST /notes/{id}/create-requirement
POST /notes/{id}/create-change-request-draft
POST /comments
```

---

# 16. Business rules master

## 16.1 Meeting rules

```text
MTG-001 Meeting title required.
MTG-002 Start time required.
MTG-003 Meeting belongs to workspace/project.
MTG-004 External participant does not grant portal access.
MTG-005 Client-visible meeting must hide internal-only notes.
MTG-006 Cancelled meeting cannot be started/completed.
MTG-007 Archived meeting remains auditable.
```

## 16.2 Minutes/note rules

```text
MIN-001 Minutes belong to meeting.
MIN-002 Approved minutes immutable except revision.
MIN-003 Rejection requires reason.
MIN-004 Client-visible summary separate from internal summary.
NOTE-001 Internal-only notes hidden from external portal.
NOTE-002 Note conversion requires target module permission.
```

## 16.3 Action rules

```text
ACT-001 Action item title required.
ACT-002 Action owner target must exist if provided.
ACT-003 Completing action requires permission.
ACT-004 Linked task creation requires task permission.
ACT-005 Linked task creation idempotent.
ACT-006 External owner does not become task assignee unless policy supports.
```

## 16.4 Comment/mention rules

```text
CMT-001 Comment body required.
CMT-002 Thread target must exist.
CMT-003 Thread/comment does not grant access.
CMT-004 Soft delete only by default.
MEN-001 Mention does not grant access.
MEN-002 Mention notification only if target can access.
```

## 16.5 Artifact link rules

```text
LNK-001 Target must exist.
LNK-002 Target must belong to same workspace/project.
LNK-003 Link does not grant access.
LNK-004 Duplicate active links prevented.
```

---

# 17. Error catalog

```text
MEETING_NOT_FOUND
MEETING_INVALID_STATUS
MEETING_PROJECT_ARCHIVED
MEETING_START_TIME_REQUIRED
MEETING_CANCELLED
MEETING_ACCESS_DENIED

MEETING_SERIES_NOT_FOUND
MEETING_SERIES_INVALID_STATUS

MEETING_PARTICIPANT_NOT_FOUND
MEETING_PARTICIPANT_TARGET_NOT_FOUND
MEETING_PARTICIPANT_TARGET_MISMATCH
MEETING_PARTICIPANT_ACCESS_DENIED

MEETING_AGENDA_ITEM_NOT_FOUND
MEETING_AGENDA_ITEM_ORDER_DUPLICATE

MEETING_MINUTES_NOT_FOUND
MEETING_MINUTES_IMMUTABLE
MEETING_MINUTES_REJECTION_REASON_REQUIRED
MEETING_MINUTES_ACCESS_DENIED

MEETING_NOTE_NOT_FOUND
MEETING_NOTE_INTERNAL_ONLY
MEETING_NOTE_CONVERSION_NOT_ALLOWED

MEETING_ACTION_ITEM_NOT_FOUND
MEETING_ACTION_ITEM_INVALID_STATUS
MEETING_ACTION_OWNER_MISMATCH
MEETING_ACTION_COMPLETE_NOTE_REQUIRED
MEETING_ACTION_LINKED_TASK_ALREADY_EXISTS
MEETING_ACTION_TASK_PERMISSION_REQUIRED

MEETING_ARTIFACT_LINK_NOT_FOUND
MEETING_ARTIFACT_LINK_TARGET_NOT_FOUND
MEETING_ARTIFACT_LINK_TARGET_MISMATCH
MEETING_ARTIFACT_LINK_DUPLICATE

COMMENT_THREAD_NOT_FOUND
COMMENT_THREAD_TARGET_NOT_FOUND
COMMENT_THREAD_TARGET_MISMATCH
COMMENT_NOT_FOUND
COMMENT_BODY_REQUIRED
COMMENT_EDIT_NOT_ALLOWED
COMMENT_DELETE_NOT_ALLOWED
COMMENT_ACCESS_DENIED

MENTION_TARGET_NOT_FOUND
MENTION_ACCESS_DENIED
```

---

# 18. Required tests

## 18.1 Meeting tests

```text
createMeeting_valid_success
createMeeting_archivedProject_rejected
updateMeeting_success
startMeeting_valid_success
completeMeeting_valid_success
cancelMeeting_valid_success
cancelledMeeting_startRejected
archiveMeeting_preservesMinutesActions
```

## 18.2 Participant tests

```text
addInternalParticipant_success
addExternalContactParticipant_success
participantExternalDoesNotGrantPortalAccess
markParticipantAttended_success
participantOtherWorkspace_rejected
```

## 18.3 Agenda/minutes/note tests

```text
createAgendaItem_success
reorderAgendaItems_success
createMinutes_success
submitMinutesReview_success
approveMinutes_success
rejectMinutes_requiresReason
approvedMinutes_immutable
createInternalNote_hiddenFromPortal
createClientVisibleNote_visibleInPortalIfGrant
```

## 18.4 Action item tests

```text
createMeetingAction_success
assignActionToInternalUser_success
assignActionToExternalContact_success
completeAction_success
overdueAction_reported
createLinkedTask_success
createLinkedTask_idempotentNoDuplicate
createLinkedTask_withoutPermission_forbidden
```

## 18.5 Artifact conversion tests

```text
linkMeetingToTask_success
linkMeetingToOtherProjectTask_rejected
createDecisionFromNote_success
createRaidItemFromNote_success
createRequirementFromNote_success
createChangeRequestDraftFromNote_success
conversionWithoutPermission_forbidden
```

## 18.6 Comment tests

```text
createCommentThreadOnTask_success
createComment_success
replyComment_success
editComment_success
deleteComment_softDelete
threadDoesNotGrantTargetAccess
internalCommentHiddenFromExternal
externalCommentAudited
mentionCreatesNotificationIfTargetHasAccess
mentionDoesNotGrantAccess
```

## 18.7 Portal tests

```text
portalMeetingList_clientVisibleOnly
portalMeetingMinutes_internalNotesHidden
portalComment_requiresGrant
externalParticipantWithoutGrant_forbidden
```

## 18.8 Authorization tests

```text
viewMeeting_withoutPermission_forbidden
createMeeting_withoutPermission_forbidden
approveMinutes_withoutPermission_forbidden
createComment_withoutTargetAccess_forbidden
createArtifactLink_withoutTargetAccess_forbidden
crossWorkspaceMeeting_forbidden
```

## 18.9 Event/audit tests

```text
collaborationEventSeeder_firstRun_createsDefinitions
collaborationEventSeeder_secondRun_noDuplicates
meetingCreated_eventEmitted
minutesApproved_auditCreated
commentDeleted_auditCreated
noteConvertedToDecision_eventEmitted
```

---

# 19. Manual verification checklist

Completion file must include:

```text
1. Create meeting series.
2. Create meeting.
3. Add internal participant.
4. Add external contact participant.
5. Add agenda items.
6. Add notes.
7. Create minutes.
8. Submit and approve minutes.
9. Generate minutes document if Document Hub exists.
10. Create meeting action item.
11. Convert action item to task.
12. Link meeting to deliverable/requirement/RAID item.
13. Create decision from meeting note.
14. Create comment thread on task/deliverable.
15. Mention user and confirm notification.
16. Mark meeting client-visible and confirm portal sees only allowed content.
17. Confirm no video call/realtime chat/calendar sync/workflow automation is falsely claimed.
```

---

# 20. Acceptance criteria

Phase 31 is accepted only if:

```text
1. Current meeting/collaboration capability is classified against TO-BE.
2. MeetingSeries implemented/tested.
3. Meeting implemented/tested.
4. MeetingParticipant implemented/tested.
5. MeetingAgendaItem implemented/tested.
6. MeetingMinutes and MeetingNote implemented/tested.
7. MeetingActionItem implemented/tested.
8. Linked task creation implemented/tested.
9. MeetingArtifactLink implemented/tested.
10. CommentThread/Comment/Mention implemented/tested.
11. Decision/RAID/Requirement/ChangeRequest conversion implemented/tested where modules exist.
12. Document/portal/reporting/notification integrations implemented/tested or explicitly deferred.
13. IAM permissions implemented/tested.
14. Event seeders idempotent.
15. Activity/audit/outbox follows Phase 04.
16. No video call engine, realtime chat, full calendar sync, Slack/Teams sync, workflow automation is falsely claimed.
17. `mvn compile` passes.
18. `mvn test` passes.
19. Completion file exists.
```

Do not mark complete if:

```text
external participant gets portal access automatically
comment or mention grants target access
internal notes visible in portal
approved minutes editable silently
linked task duplicated from same action
cross-project artifact links allowed
tests fail
```

---

# 21. Required phase completion file

Agent must create:

```text
docs/phase-complete/PHASE_31_MEETINGS_COLLABORATION_TO_BE_COMPLETE.md
```

Required sections:

```text
# Phase 31 — Meetings / Collaboration Complete

## 1. Summary
## 2. Source Inputs Reviewed
## 3. Current vs TO-BE Classification Matrix
## 4. Implemented in Current BE
## 5. Implemented / Hardened in This Phase
## 6. Seed-only Items Added
## 7. Deferred Items and Target Phase
## 8. Collaboration Boundary Decision
## 9. Entity Mapping
## 10. API Changes
## 11. Meeting / Series Strategy
## 12. Participant Strategy
## 13. Agenda / Minutes / Notes Strategy
## 14. Action Item Strategy
## 15. Linked Task Strategy
## 16. Artifact Link Strategy
## 17. Comment / Mention Strategy
## 18. Decision / RAID / Requirement Conversion Strategy
## 19. Document / Portal Integration
## 20. Reporting Strategy
## 21. Notification / Event Strategy
## 22. Authorization Matrix
## 23. Activity / Audit / Outbox Notes
## 24. Idempotency Strategy
## 25. Tests Added
## 26. Commands Run
## 27. Test Results
## 28. Manual Verification
## 29. Assumptions
## 30. Deviations From Prompt
## 31. Known Risks
## 32. Future Phases That Must Return to Collaboration
```

---

# 22. Future phases that must return

```text
Phase 34 — Workflow / Approval:
- Meeting minutes approval routing, decision approval, action escalation.

Phase 35 — Advanced Notifications:
- Meeting reminders, action digest, mention digest, overdue follow-up reminders.

Phase 38 — Audit / Compliance / Privacy:
- Retention, legal hold, external participant privacy, transcript retention.

Phase 39 — Integration / Import / Export:
- Google/Outlook calendar sync, Slack/Teams integration, video recording import.

Phase 40 — Service / Support / Maintenance:
- Support meetings, incident review meetings, SLA follow-up actions.

Phase 41 — Knowledge Graph / Semantic Index:
- Semantic meeting search, transcript summarization, cross-meeting decision graph.

Realtime collaboration backlog:
- websocket chat, live cursors, presence, typing indicators, channels.
```

---

# 23. Agent anti-bịa rules

The agent must not:

```text
1. Claim video call engine exists.
2. Claim full calendar sync exists.
3. Claim realtime chat exists.
4. Claim Slack/Teams sync exists.
5. Claim workflow automation exists.
6. Treat external participant as portal access grant.
7. Let comments/mentions grant access.
8. Expose internal-only meeting notes to external portal.
9. Silently edit approved minutes.
10. Hide deferred integration/realtime/workflow gaps.
```

---

# 24. Prompt to give coding agent

```text
You are implementing Phase 31 — TO-BE Meetings, Collaboration, Agenda, Minutes, Action Items, Decisions & Discussion Records.

This is not an as-is documentation task.

Before coding, read:
- CLAUDE.md / CLAUDE.ms
- Coding_convention.md
- Phase 00–30 docs and completion files
- Current backend code, migrations, tests

Your task:
1. Compare current meeting/collaboration capability against this Phase 31 TO-BE spec.
2. Classify each capability with required labels.
3. Implement MeetingSeries, Meeting, MeetingParticipant, MeetingAgendaItem.
4. Implement MeetingMinutes and MeetingNote.
5. Implement MeetingActionItem and linked task creation.
6. Implement MeetingArtifactLink.
7. Implement CommentThread, Comment, Mention.
8. Integrate with Task, RAID, Decision, Requirement, ChangeRequest, Document, Portal, Notification, Reporting where available.
9. Add IAM permissions, events, audit/outbox, idempotency, and tests.
10. Run mvn compile and mvn test.
11. Create docs/phase-complete/PHASE_31_MEETINGS_COLLABORATION_TO_BE_COMPLETE.md.

Do not implement or claim video-call engine, realtime chat, full calendar sync, Slack/Teams sync, workflow automation, public external collaboration, or autonomous AI meeting decisions in this phase.
```

---

# 25. Quick tracking matrix

| Capability | Current backend | Phase 31 action | Later phase |
|---|---|---|---|
| MeetingSeries | Missing/unknown | Must implement basic | Calendar sync Phase 39 |
| Meeting | Missing/unknown | Must implement | — |
| MeetingParticipant | Missing/unknown | Must implement | Portal visibility Phase 30 |
| MeetingAgendaItem | Missing/unknown | Must implement | — |
| MeetingMinutes | Missing/unknown | Must implement | Workflow Phase 34 |
| MeetingNote | Missing/unknown | Must implement | Semantic search Phase 41 |
| MeetingActionItem | Missing/unknown | Must implement | Notification digest Phase 35 |
| Linked task creation | Missing/unknown | Must implement | — |
| MeetingArtifactLink | Missing/unknown | Must implement | Semantic graph Phase 41 |
| CommentThread | Missing/unknown | Must implement | Realtime chat backlog |
| Comment | Missing/unknown | Must implement | Realtime chat backlog |
| Mention | Missing/unknown | Must implement | Advanced notifications Phase 35 |
| Meeting to Decision | Missing/unknown | Must implement if Phase 25 | Workflow Phase 34 |
| Meeting to RAID | Missing/unknown | Must implement if Phase 25 | — |
| Meeting to Requirement | Missing/unknown | Must implement if Phase 28 | — |
| Meeting to ChangeRequest | Missing/unknown | Must implement if Phase 19 | — |
| Minutes document generation | Missing/partial | Must implement if Phase 27 | Advanced docs Phase 39 |
| Client-visible meeting view | Missing/partial | Must implement if Phase 30 | — |
| Calendar sync | Missing | Defer | Phase 39 |
| Video call engine | Missing | Not in scope | External integration only |
| Realtime chat | Missing | Defer | Collaboration backlog |
| Slack/Teams sync | Missing | Defer | Phase 39 |
| Workflow approval | Missing | Defer | Phase 34 |

---

# 26. Final principle

Phase 31 is not complete when "a meeting row can be stored."

Phase 31 is complete when Scopery can turn collaboration into traceable work:

```text
meeting
+ participants
+ agenda
+ notes
+ minutes
+ action items
+ artifact links
+ comments
+ mentions
+ decisions / RAID / requirements
+ audit
= accountable collaboration
```

Meeting is not calendar sync.

Comment is not realtime chat.

Mention is not permission.

External participant is not portal grant.

Approved minutes are project memory and must not change silently.
