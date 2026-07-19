# Phase 05 — Event Registry TO-BE Complete

## 1. Summary

Phase 05 hardens Event Registry into a stable event contract hub: governance fields (`dataClassification`, `ownerModule`, `isSystemEvent`, deprecation metadata), variable `sensitive` + path validation + `CURRENCY`/`PERCENT`, deprecate API with audit, consumer-safety guards against active EmailRule/EventConfig, IAM-enforced read/write via `SYSTEM_MANAGE_EVENT_REGISTRY`, idempotent seeder support, and seed-only catalogs for AI/Notification/Knowledge/Project.

## 2. Source Inputs Reviewed

- `PHASE_00_MASTER_ROADMAP_MODULAR_REVISED.md`
- `PHASE_01`–`PHASE_04` specs + completion files
- `PHASE_05_EVENT_REGISTRY_TO_BE_DETAILED.md`
- Existing `modules/eventregistry`, IAM/Workspace/Platform seeders
- Notification EmailRule/EmailTemplate + AI EventConfig ACTIVE binding code

## 3. Current vs TO-BE Classification Matrix

| Capability | Classification | Notes |
|---|---|---|
| EventDefinition CRUD | `CURRENTLY_IMPLEMENTED` + hardened | Create/Update/Activate/Deactivate + Deprecate |
| EventVariable upsert | Hardened | Path validation, duplicate check, sensitive, consumer-safe |
| Schema JSON validation | `CURRENTLY_IMPLEMENTED` | Parse-only; advanced JSON Schema/diff → Phase 23 |
| eventVersion | `CURRENTLY_IMPLEMENTED` | Starts at 1; multi-version entity deferred |
| dataClassification | `MUST_IMPLEMENT_IN_PHASE_05` → done | Enum + DB check constraint |
| Lifecycle ACTIVE/INACTIVE/DEPRECATED | Hardened | Deprecate API + metadata |
| Consumer safety | Implemented (minimum) | Block deactivate + required-var removal / type change when consumers active |
| Module seeders | Hardened + seed-only expansions | Shared `EventDefinitionSeedSupport` |
| Notification variable validation | `CURRENTLY_IMPLEMENTED` | Publish template validates paths; ACTIVE required |
| AI EventConfig ACTIVE check | `CURRENTLY_IMPLEMENTED` | Create/Update/Activate reject non-ACTIVE |
| PlatformOutbox code check | Soft WARN (Phase 04) | Hard ACTIVE-only emit deferred |
| Granular EVENT_REGISTRY_* rights | Mapped to manage right | Split deferred |
| Full EventDefinitionVersion entity | `DEFERRED_TO_PHASE_23/38` | |
| Workflow/webhook/analytics | Deferred | Phases 22/32/integration |

## 4. Implemented in Current BE (pre–Phase 05)

- EventDefinition + EventVariable tables/API
- Activate/deactivate; DEPRECATED cannot activate
- code / sourceSystem / eventKey uniqueness + immutability
- IAM/Workspace/Platform event seeders
- EmailRule/EmailTemplate/EventConfig ACTIVE binding
- Template variable path validation on publish

## 5. Implemented / Hardened in This Phase

- **V43** migration: governance + deprecation + sensitive columns/constraints
- Domain: `EventDataClassification`, `EventVariablePath`, deprecate(), sensitive vars, CURRENCY/PERCENT
- API: `PATCH /api/event-definitions/{id}/deprecate`
- Consumer safety: `EventDefinitionConsumerSafetyService` (EmailRule + EventConfig)
- Upsert: duplicate path rejection; block required removal / type change when consumers active
- Activity action names aligned to TO-BE (`EVENT_DEFINITION_*`)
- Audit: `EVENT_DEFINITION_DEPRECATED`, `EVENT_SCHEMA_CHANGED`
- Seeders: AI / Notification / Knowledge / Project + shared seed support (add-missing-only vars)
- IAM password-reset variables marked sensitive; invitation/email vars marked sensitive in legacy seeder
- Query reads require `SYSTEM_MANAGE_EVENT_REGISTRY` (VIEW mapped to manage)

## 6. Seed-only Items Added

| Module | Source system | Codes (examples) | Emitter status |
|---|---|---|---|
| AI Agent | `SCOPERY_AI_AGENT` | `AI_PROVIDER_CREATED` … `AI_USAGE_POLICY_BLOCKED` | Seed only — Phase 07 |
| Notification | `SCOPERY_NOTIFICATION` | `EMAIL_TEMPLATE_CREATED` … `EMAIL_DELIVERY_RETRIED` | Seed only — Phase 06 |
| Knowledge | `SCOPERY_KNOWLEDGE` | `DOCUMENT_TYPE_*` | Seed only (Document Hub → Phase 08) |
| Project | `SCOPERY_PROJECT` | `PROJECT_*`, `WBS_*`, `TASK_*` + TASK_ASSIGNED vars | Seed only — Phase 09/10 |

## 7. Deferred Items and Target Phase

| Item | Target |
|---|---|
| Advanced schema diff / breaking-change auto-version | Phase 23 |
| Full EventDefinitionVersion entity | Phase 23 / 38 |
| Hard PlatformOutbox reject for unknown/INACTIVE/DEPRECATED | Phase 04 follow-up / 20 |
| Granular EVENT_REGISTRY_VIEW/CREATE/… rights | Phase 23 IAM hardening |
| Sensitive variable gating in email/AI rendering | Phase 06 / 07 |
| Finance/Quote/Baseline/Reporting/Capacity events | Phases 12–22 |
| Document Hub events beyond DocumentType | Phase 08 |
| Workflow trigger catalog consumers | Phase 32 |
| Event analytics dashboard | Phase 22 |

## 8. EventDefinition Entity Mapping

| TO-BE field | Actual |
|---|---|
| code / sourceSystem / eventKey / name / description | existing |
| eventVersion | `event_version` |
| inputSchemaJson / outputSchemaJson | `input_schema` / `output_schema` (TEXT) |
| status | ACTIVE / INACTIVE / DEPRECATED |
| dataClassification | `data_classification` |
| ownerModule | `owner_module` |
| isSystemEvent | `is_system_event` |
| deprecatedAt / deprecatedBy / replacementEventDefinitionId | added |
| samplePayloadJson | retained (extra vs TO-BE) |

## 9. EventVariable Entity Mapping

| TO-BE field | Actual |
|---|---|
| path | `variable_path` |
| name | `variable_label` |
| dataType | `variable_type` (+ CURRENCY, PERCENT) |
| required / sensitive | columns |
| exampleValue / description | existing |

## 10. API Changes

| Method | Path | Change |
|---|---|---|
| POST/PUT | `/api/event-definitions` | + `dataClassification`, `ownerModule` |
| PATCH | `/api/event-definitions/{id}/deprecate` | **new** |
| PUT | `/api/event-definitions/{id}/variables` | + `sensitive`; safety checks |
| GET | search/detail/variables | require EVENT_REGISTRY manage right |

## 11. Business Rules Implemented

- EVDEF-001..015 (core uniqueness, immutability, lifecycle)
- EVDEF-016 deprecate metadata
- EVDEF-017 consumers require ACTIVE (Notification + AI already; deactivate blocked)
- EVVAR-001..006, 009 + consumer-safe 007/008 when active consumers exist
- EVSEED-001..003, 005 (drift log), add-missing variables

## 12. Business Rules Deferred

- EVDEF-018 full breaking-change detection beyond consumer checks
- Force-deactivate admin override path (audit type reserved)
- Strict outbox payload↔variable validation

## 13. Event Lifecycle Rules

- Create → ACTIVE, eventVersion=1
- Deactivate blocked if active EmailRule or EventConfig
- Deprecate → DEPRECATED + deprecatedAt/By + optional replacement; cannot activate again
- Update blocked when DEPRECATED

## 14. Consumer Safety Rules

| Consumer | Check |
|---|---|
| EmailRule ACTIVE+enabled | Exists probe; blocks deactivate / required var removal / type change |
| EventConfig ACTIVE | Same |
| EmailTemplate publish | Existing path validation (Phase pre-05) |
| New EmailRule/EventConfig | Must bind ACTIVE definition (existing) |

## 15. Event Seeder Matrix by Module

| Seeder | Order | Owner |
|---|---|---|
| PlatformEventDefinitionSeedInitializer | 5 | SCOPERY_PLATFORM |
| IamEventDefinitionSeedInitializer | 10 | SCOPERY_IAM |
| WorkspaceEventDefinitionSeedInitializer | 11 | SCOPERY_WORKSPACE |
| AiAgentEventDefinitionSeedInitializer | 12 | SCOPERY_AI_AGENT |
| NotificationEventDefinitionSeedInitializer | 13 | SCOPERY_NOTIFICATION |
| KnowledgeEventDefinitionSeedInitializer | 14 | SCOPERY_KNOWLEDGE |
| ProjectEventDefinitionSeedInitializer | 15 | SCOPERY_PROJECT |
| EventRegistryVariableSeedInitializer | 20 | Legacy SCOPERY enrichment |

## 16. Event Variable Matrix

| Event | Notable variables | Sensitive |
|---|---|---|
| IAM_PASSWORD_RESET_REQUESTED | reset.url, targetUser.email | yes |
| TASK_ASSIGNED | task.* , occurredAt, traceId | no |
| WORKSPACE_INVITATION_* / JOIN_* | invitee/requester emails, links | yes |

## 17. IAM Authorization Matrix

| TO-BE authority | Phase 05 mapping |
|---|---|
| EVENT_REGISTRY_VIEW | `SYSTEM_MANAGE_EVENT_REGISTRY` |
| EVENT_REGISTRY_CREATE/UPDATE/ACTIVATE/DEACTIVATE/DEPRECATE/MANAGE_VARIABLES/MANAGE | `SYSTEM_MANAGE_EVENT_REGISTRY` |
| System seeders | Internal ApplicationReady listeners (no user auth) |

Unauthorized → existing IAM 403 path via `IamSystemAuthorizationService`.

## 18. Activity / Audit Notes

- Activity: `EVENT_DEFINITION_CREATED|UPDATED|ACTIVATED|DEACTIVATED|DEPRECATED`, `EVENT_VARIABLES_UPSERTED`
- Audit: deprecate + schema change
- Seed drift: WARN log (activity `EVENT_SEEDER_DRIFT_DETECTED` reserved for future)

## 19. Tests Added / Updated

- Domain lifecycle + value object (code regex, eventKey dual style, variable path)
- Action: create conflicts, deprecate, deactivate with consumers, upsert duplicate/required-removal
- IAM seeder idempotency (no delete on second run)
- Project seeder creates known codes
- Error catalog coverage retained

## 20. Commands Run

```bash
./mvnw test
```

## 21. Test Results

`./mvnw test` → **BUILD SUCCESS** — Tests run: **658**, Failures: 0, Errors: 0, Skipped: 1.

## 22. Manual Verification

| Check | Status |
|---|---|
| Create EventDefinition | Unit |
| Duplicate code / source+key | Unit |
| Invalid schema JSON | Unit |
| Upsert variables + duplicate path | Unit |
| Deactivate with consumers blocked | Unit |
| Deprecate + cannot activate | Unit |
| Seeder second run no duplicates | Unit (IAM/Platform/Workspace/Project) |
| Activity on create/deprecate | Unit (logger mocked) |
| Audit on deprecate | Unit |
| Live HTTP against running app | **Not yet verified — requires running the application** |

## 23. Assumptions

- Table remains `app_event_definition` / `app_event_variable`
- eventKey continues to support uppercase snake (existing seeds); lowercase-dot accepted when containing `.`
- Soft WARN for unknown outbox codes remains acceptable until hard enforce
- Seed-only events do **not** imply emitters exist

## 24. Deviations From Prompt

- Granular EVENT_REGISTRY_* rights not split; single manage right maps all
- Legacy `EventRegistryVariableSeedInitializer` still uses sourceSystem `SCOPERY` for invitation templates (Workspace seeder owns `SCOPERY_WORKSPACE` definitions separately)
- Advanced schema validation remains parse-only

## 25. Known Risks

- Workspace seeders that still delete+recreate variables may conflict with consumer-safe policy until migrated to `ensureVariables`
- Soft outbox unknown-code path can still enqueue unregistered codes
- Sensitive flag not yet enforced at email/AI render time

## 26. Future Phases That Must Return to Event Registry

- Phase 06 — Notification engine consumer safety + sensitive var gating
- Phase 07 — AI Agent emitters + prompt/variable alignment
- Phase 08 — Document Hub events
- Phase 09/10 — Project emitters for seeded contracts
- Phase 12–22 — Capacity / Finance / Quote / Baseline / Reporting events
- Phase 23/38 — Versioning, classification hardening, retention
- Phase 32 — Workflow triggers
