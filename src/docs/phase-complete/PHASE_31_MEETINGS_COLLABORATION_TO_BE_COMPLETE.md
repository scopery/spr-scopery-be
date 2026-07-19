# Phase 31 — Meetings / Collaboration Complete

## 1. Summary

Phase 31 delivered `modules/collaboration` with meeting series, meetings (lifecycle), participants, agenda, minutes (immutable when approved), notes, action items (complete + explicit linked-task), artifact links, comment threads/comments/mentions, collaboration reports, Flyway `V74`, IAM collaboration/comment rights, and event seeder `@Order(37)`.

**Integration follow-up (this pass):** portal client-visible meeting APIs (Phase 30), DocHub minutes document generation (Phase 27), and note → Decision / RAID / Requirement / ChangeRequest conversion with artifact links (Phases 25/28/19).

## 2. Source Inputs Reviewed

- `PHASE_31_MEETINGS_COLLABORATION_TO_BE_DETAILED.md`
- Patterns from `modules/raid`, `modules/documenthub`, `modules/traceability`, `modules/projectbaseline`, Phase 30 portal

## 3. Current vs TO-BE Classification Matrix

| Capability | Classification | Status |
|---|---|---|
| MeetingSeries | MUST | Implemented |
| Meeting + lifecycle | MUST | Implemented |
| Participants | MUST | Implemented |
| Agenda | MUST | Implemented |
| Minutes + approve immutability | MUST | Implemented |
| Notes | MUST | Implemented |
| Action items + linked task | MUST | Implemented (explicit task id link) |
| Artifact links | MUST | Implemented |
| CommentThread/Comment/Mention | MUST | Implemented |
| Reports | MUST | Implemented (core reports) |
| Portal meeting APIs | MUST if Phase 30 | **Implemented** — client-visible list/get/minutes/comments |
| Document generate-minutes | MUST if Phase 27 | **Implemented** — `POST .../minutes/{id}/generate-document` via DocHub |
| Decision/RAID/Requirement/CR conversion | MUST if modules exist | **Implemented** — note create-* endpoints + artifact link |
| AI summary tools | SEED_ONLY | **SEEDED** — AiTool registry V89 (`AiToolSeedInitializer`); execute = stub/NO_OP |
| Video/chat/calendar/Slack | NOT_IN_SCOPE | Not claimed |

## 4. Implemented in Current BE

- Migration `V74__create_collaboration_tables_phase31.sql`
- Controllers under `/api/projects/{projectId}/meetings*` / `comments*` / `reports*`
- Portal: `GET/POST /api/portal/projects/{projectId}/meetings...` (`PortalProjectMeetingController`)
- Minutes DocHub: `GenerateMinutesDocumentAction` + `PROJECT_MEETING_MINUTES_GENERATE_DOCUMENT`
- Note conversions: `create-decision`, `create-raid-item`, `create-requirement`, `create-change-request-draft`
- Shared kernel: constants, errors, auth, activity logger, enum parser, event seeder
- Tests: domain (Meeting, Minutes, ActionItem, CommentThread, attachDocument); `CreateMeetingActionTest`; `CreateDecisionFromNoteActionTest`; `PortalMeetingQueryServiceTest`

## 5. Deferred / Blocked Items

| Item | Notes |
|---|---|
| Live AI meeting tool handlers | Registry + seeds shipped; LLM/tool-call handlers remain future work — see [`NOTE_AI_TOOL_REGISTRY_DEFERRED.md`](../phase-tracking/NOTE_AI_TOOL_REGISTRY_DEFERRED.md) |
| Full calendar recurrence engine | Phase 39 |
| Video / realtime chat / Slack | NOT_IN_SCOPE |

## 6. Release Decision

**Phase 31 MUST + MUST-if integrations: COMPLETE** against existing Phases 25/27/28/30 modules. Meeting AI tool codes are seeded in AiTool registry (V89); live handlers remain future work.
