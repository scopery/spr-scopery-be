# PHASE 34 — TO-BE Governance by Permission, Versioning, Ownership, Locking, Audit & Restore

> Project: Scopery Backend  
> Phase: 34  
> Document type: TO-BE implementation-grade specification  
> Status: Planning / Direction changed from Workflow Approval Engine to Lightweight Governance  
> Roadmap group: Post-core Dynamic Work OS expansion  
> Replaces previous direction: Phase 34 Workflow / Approval Engine  
> Depends on: Phase 00 — Master Backend Roadmap, Phase 01 — API Path / Security Baseline, Phase 02 — IAM Core, Phase 03 — Workspace / Organization / Team, Phase 04 — Platform Audit / Outbox / Idempotency, Phase 05 — Event Registry, Phase 06 — Notification Engine, Phase 09 — Project Core / WBS / Task, Phase 10 — Project Authorization, Phase 18 — Quote / Commercial Proposal, Phase 19 — Baseline / Change Request, Phase 20 — Project Events / Notifications, Phase 22 — Reporting / Dashboard / Export, Phase 23 — Core Hardening, Phase 24 — Scope / Deliverable / Acceptance, Phase 25 — RAID / Decision Management, Phase 26 — Quality / Test / Release, Phase 27 — Document Hub / Generation, Phase 28 — Application / Requirement / Traceability, Phase 29 — External Party / Stakeholder, Phase 30 — External Portal, Phase 31 — Meetings / Collaboration, Phase 32 — Search / Productivity, Phase 33 — Custom Fields / Forms / Configuration  
> API base: `/api`  
> Primary module: `modules/governance`, `modules/versioning`, or `modules/platform/governance` depending on repository architecture  
> Related modules: `iam`, `workspace`, `project`, `task`, `quote`, `baseline`, `change-request`, `scope`, `deliverable`, `acceptance`, `requirement`, `quality`, `release`, `document`, `raid`, `decision`, `externalparty`, `clientportal`, `collaboration`, `configuration`, `notification`, `reporting`, `search`, `audit`, `eventregistry`  
> Important rule: Phase 34 does **not** implement approval workflow. Scopery governance is based on permission, ownership, immutable versions, locking/finalization, audit trail, restore, and controlled access grants. If a user has permission, they can act; the system records and protects the history.

---

# 0. Direction change

Previous idea:

```text
Phase 34 — Workflow / Approval Engine
```

New direction:

```text
Phase 34 — Governance by Permission, Versioning, Ownership, Locking, Audit & Restore
```

Reason:

```text
Full approval workflow is too heavy for Scopery MVP.
Most project objects do not need approval.
Versioning + permission + audit is simpler, faster, safer, and easier to use.
```

Scopery should avoid unnecessary friction:

```text
No approval queue for normal work.
No multi-step workflow engine.
No parallel approvers.
No delegation/escalation/SLA approval flow.
No workflow designer.
```

Instead, Scopery should enforce:

```text
Who can do what?
Who owns the object?
What changed?
When did it change?
What version existed before?
Can the old version be restored?
Is this object finalized/locked?
Is this object client-visible?
Did the action affect baseline/scope/cost/release?
```

---

# 1. Core principle

```text
Versioning + Permission + Audit > Approval Engine
```

Meaning:

```text
1. Owner/Admin controls permission.
2. Users with permission can act directly.
3. Important objects create immutable versions.
4. Finalized/locked objects cannot be silently changed.
5. All important actions are audited.
6. Restore/revert is supported where safe.
7. Baseline/change guard handles commitment changes.
```

---

# 2. What this phase removes from scope

Phase 34 must not implement:

```text
WorkflowDefinition
WorkflowDefinitionVersion
WorkflowRun
WorkflowStepRun
ApprovalRequest
ApprovalDecision
Delegation
Escalation
Approval SLA
Parallel approval
Multi-step approval
Workflow designer
BPMN engine
Custom workflow scripting
AI autonomous approval
```

If any previous Phase 34 approval-engine document exists, it should be treated as:

```text
DEPRECATED_DIRECTION
```

---

# 3. Purpose

Phase 34 creates a lightweight governance layer across Scopery.

It answers:

```text
Who owns this object?
Who can view/edit/manage it?
Which objects must create versions?
Which fields trigger version creation?
Which status means finalized/locked?
What can be restored?
What changes require ChangeRequest because baseline is affected?
What changed between versions?
Which client-visible data changed?
Who changed permissions?
Who accessed sensitive content?
How do reports show governance health?
```

Phase 34 is **not approval**.

Phase 34 is **controlled history and access**.

---

# 4. Source inputs

Before coding Phase 34, the agent must read:

```text
1. Current backend codebase
2. Phase 01 API/Security baseline implementation
3. Phase 02 IAM implementation
4. Phase 03 Workspace/Organization/Team implementation
5. Phase 04 Audit/Outbox/Idempotency implementation
6. Phase 05 Event Registry implementation
7. Phase 06 Notification implementation
8. Phase 09 Project/Task implementation
9. Phase 10 Project Authorization implementation
10. Phase 18 Quote implementation
11. Phase 19 Baseline / ChangeRequest implementation
12. Phase 22 Reporting implementation
13. Phase 23 Core Hardening completion file
14. Phase 24 Scope / Deliverable / Acceptance implementation
15. Phase 25 RAID / Decision implementation
16. Phase 26 Quality / Release implementation
17. Phase 27 Document Hub implementation
18. Phase 28 Requirement / Traceability implementation
19. Phase 29 External Party / Stakeholder implementation
20. Phase 30 External Portal implementation
21. Phase 31 Meetings / Collaboration implementation
22. Phase 32 Search / Productivity implementation
23. Phase 33 Custom Fields / Forms / Configuration implementation
24. Existing owner/access/version/audit code if any
25. Current IAM seeders and EventDefinition seeders
```

The agent must inspect actual code and not assume versioning exists everywhere.

---

# 5. Current expected backend state

After Phase 33, some modules may already have partial versioning:

```text
QuoteVersion
DocumentVersion
RequirementVersion
TemplateVersion
FormVersion
BaselineSnapshot
ReportSnapshot
ReleasePackage version-like state
```

But the system may still lack a unified governance policy around:

```text
which object types require versioning
which changes create a version
who owns objects
object-level grants
finalization/locking
restore/revert rules
change diff
permission change audit
sensitive access audit
client-visible change audit
governance reports
```

Phase 34 unifies these rules without building workflow approval.

---

# 6. Target statement

Phase 34 must deliver:

```text
1. GovernedObjectType registry.
2. GovernancePolicy per workspace/object type.
3. Ownership model for important objects.
4. Object-level access grants where needed.
5. Unified versioning policy and version metadata.
6. Snapshot/diff/restore foundation.
7. Lock/finalize policy.
8. Baseline/change guard integration.
9. Client-visible and sensitive change tracking.
10. Permission-change audit.
11. Restore/revert APIs for supported objects.
12. Governance events/notifications/reports.
13. Search/report/export integration for versioned history where safe.
14. Tests and completion report.
```

---

# 7. Boundary decisions

## 7.1 No approval engine

Rules:

```text
1. If actor has permission, action can proceed.
2. Action must still pass domain validation.
3. Important changes create version/audit.
4. Locked/finalized objects require unlock/new version/revision path.
5. Baseline-affecting changes use ChangeRequest guard.
```

## 7.2 Permission is not ownership

Owner can manage object-level access if policy allows.

But owner is still bounded by workspace/project IAM.

```text
Owner does not become superadmin.
Owner cannot bypass domain lifecycle.
Owner cannot bypass baseline guard.
```

## 7.3 Versioning is not backup

Versioning captures meaningful object states.

It is not database backup.

Restore must still call domain services and validate rules.

## 7.4 Audit is not permission

Audit records what happened.

Audit does not prevent actions by itself.

Permission/domain rules prevent actions.

## 7.5 Locking is not approval

Lock/finalize means:

```text
this version/state is protected from silent mutation
```

It does not mean it was approved by a workflow.

---

# 8. Classification labels

Use these labels in completion file:

| Label | Meaning |
|---|---|
| `CURRENTLY_IMPLEMENTED` | Existing backend already supports it with tests. |
| `PARTIALLY_IMPLEMENTED` | Some support exists but gaps remain. |
| `MUST_IMPLEMENT_IN_PHASE_34` | Required now. |
| `MUST_HARDEN_IN_PHASE_34` | Existing behavior must be hardened now. |
| `SEED_ONLY_IN_PHASE_34` | Seed object types/events/permissions/policies only. |
| `DEFERRED_TO_PHASE_XX` | Later phase returns. |
| `DEFERRED_TO_POST_23_BACKLOG` | Later backlog item. |
| `NOT_IN_SCOPE_FOR_PHASE_34` | Explicitly not in this phase. |
| `DEPRECATED_DIRECTION` | Old approval-engine direction not used. |

---

# 9. Required capabilities

---

## 9.1 GOV-001 — GovernedObjectType

Classification: `MUST_IMPLEMENT_IN_PHASE_34`

Purpose:

```text
Declare which objects are governed by versioning, ownership, locking, and audit policies.
```

Initial governed object types:

```text
PROJECT
TASK
QUOTE_VERSION
CHANGE_REQUEST
BASELINE
SCOPE_PACKAGE
SCOPE_ITEM
DELIVERABLE
DELIVERABLE_ACCEPTANCE
REQUIREMENT
REQUIREMENT_VERSION
DOCUMENT
DOCUMENT_VERSION
RAID_ITEM
DECISION_RECORD
TEST_PLAN
TEST_CASE
TEST_RUN
DEFECT
RELEASE_PACKAGE
DEPLOYMENT_RECORD
MEETING_MINUTES
CUSTOM_FORM_DEFINITION
CUSTOM_FORM_VERSION
CUSTOM_FIELD_DEFINITION
EXTERNAL_CONTACT
CLIENT_FEEDBACK
```

Rules:

```text
1. Object type code stable and seeded.
2. Object type maps to real domain object.
3. Each object type declares supported governance features.
4. Object type does not create new domain entity.
```

Supported feature flags:

```text
ownershipSupported
objectGrantSupported
versioningSupported
snapshotSupported
diffSupported
restoreSupported
lockSupported
clientVisibleAuditSupported
sensitiveAccessAuditSupported
baselineGuardSupported
```

---

## 9.2 GOV-002 — GovernancePolicy

Classification: `MUST_IMPLEMENT_IN_PHASE_34`

Purpose:

```text
Configure governance behavior per workspace/object type.
```

Policy fields:

```text
versioningMode
versionOnCreate
versionOnUpdate
versionOnStatusChange
versionOnClientVisibleChange
versionOnSensitiveFieldChange
lockOnFinalize
requireReasonForSensitiveChange
allowRestore
allowOwnerGrant
allowObjectLevelGrant
baselineGuardMode
auditLevel
```

Versioning modes:

```text
NONE
AUDIT_ONLY
SNAPSHOT_ON_MAJOR_CHANGE
SNAPSHOT_ON_EVERY_CHANGE
DOMAIN_VERSION_ONLY
```

Audit levels:

```text
BASIC
DETAILED
SENSITIVE
```

Rules:

```text
1. Workspace can configure policy only within allowed system limits.
2. Some object types have mandatory minimum policy.
3. Policy changes are audited.
4. Policy does not override domain rules.
```

Mandatory minimum examples:

```text
QuoteVersion -> DOMAIN_VERSION_ONLY + detailed audit
DocumentVersion -> DOMAIN_VERSION_ONLY + download/access audit
Requirement -> snapshot/version on major change
Baseline -> immutable snapshot
ChangeRequest -> detailed audit
ReleasePackage -> snapshot on finalize/release
```

---

## 9.3 OWN-001 — ObjectOwnership

Classification: `MUST_IMPLEMENT_IN_PHASE_34`

Purpose:

```text
Track owner/responsible person for governed object.
```

Owner target types:

```text
INTERNAL_USER
TEAM
PROJECT_ROLE
EXTERNAL_CONTACT limited/display only
SYSTEM
```

Rules:

```text
1. Ownership belongs to target object.
2. Owner can be used for default manage/edit permission if policy allows.
3. Owner change audited.
4. Owner cannot bypass workspace/project IAM.
5. External contact owner does not become internal user.
6. Ownership can be transferred by current owner/admin if policy allows.
```

---

## 9.4 OWN-002 — Owner-managed access

Classification: `MUST_IMPLEMENT_IN_PHASE_34`

Purpose:

```text
Allow owner/admin to grant controlled access to specific object where policy allows.
```

Object-level grant roles:

```text
VIEW
COMMENT
EDIT
MANAGE
OWNER
```

Grantee types:

```text
INTERNAL_USER
TEAM
PROJECT_ROLE
WORKSPACE_ROLE
EXTERNAL_PORTAL_ACCOUNT limited/portal only
EXTERNAL_CONTACT display-only unless portal account exists
```

Rules:

```text
1. Object-level grant does not bypass workspace/project membership.
2. Grant target must be valid.
3. Grant role must be allowed by object type.
4. External grant requires portal policy and client-visible object.
5. Grant changes audited.
6. Grant can expire/revoke.
7. Owner can grant only up to allowed level.
8. Admin can override if permission allows.
```

---

## 9.5 VER-001 — Unified version metadata

Classification: `MUST_IMPLEMENT_IN_PHASE_34`

Purpose:

```text
Provide common metadata around domain-specific versions and snapshots.
```

This does not replace module-specific versions such as:

```text
QuoteVersion
DocumentVersion
RequirementVersion
TemplateVersion
FormVersion
BaselineSnapshot
```

Instead, it records common governance metadata:

```text
object type
object id
version label/number
reason
actor
change type
created at
current/finalized flag
restore eligibility
snapshot link
```

Rules:

```text
1. Domain version remains source of truth if it exists.
2. Governance version record references domain version when present.
3. Snapshot version can store JSON only if no domain version exists.
4. Version metadata immutable after creation.
5. Current version marker unique per object where applicable.
```

---

## 9.6 VER-002 — Snapshot versioning

Classification: `MUST_IMPLEMENT_IN_PHASE_34`

Purpose:

```text
Capture object state for objects without native version tables.
```

Rules:

```text
1. Snapshot contains allowed fields only.
2. Sensitive fields masked or separately protected.
3. Snapshot includes schema version.
4. Snapshot created according to GovernancePolicy.
5. Snapshot never used to bypass domain validation.
6. Restore from snapshot calls domain service.
```

Snapshot modes:

```text
FULL_ALLOWED_STATE
DIFF_ONLY
METADATA_ONLY
```

---

## 9.7 VER-003 — Change reason

Classification: `MUST_IMPLEMENT_IN_PHASE_34`

Purpose:

```text
Require reason for important changes.
```

Reason required for:

```text
finalized object modification
client-visible content change
sensitive field change
ownership transfer
object grant change
restore/revert
baseline-affecting change
release finalization/unfinalization
document controlled version change
```

Rules:

```text
1. Reason stored in audit and version metadata.
2. Reason can be optional for normal task updates.
3. Policy decides required reason.
```

---

## 9.8 DIFF-001 — Diff summary

Classification: `MUST_IMPLEMENT_IN_PHASE_34`

Purpose:

```text
Show what changed between versions.
```

Diff types:

```text
FIELD_DIFF
STATUS_DIFF
CUSTOM_FIELD_DIFF
VISIBILITY_DIFF
PERMISSION_DIFF
DOCUMENT_VERSION_DIFF metadata-only
```

Rules:

```text
1. Sensitive old/new values masked unless permission.
2. Diff generated from snapshots/domain versions.
3. Diff is read-only.
4. Diff does not expose hidden fields.
5. Binary document content diff is not required.
```

---

## 9.9 RST-001 — Restore / Revert

Classification: `MUST_IMPLEMENT_IN_PHASE_34`

Purpose:

```text
Allow safe restore to previous version/snapshot where supported.
```

Rules:

```text
1. Restore requires permission.
2. Restore requires reason.
3. Restore creates a new version/current state.
4. Restore does not overwrite history.
5. Restore calls domain service.
6. Restore forbidden if baseline/change guard blocks it.
7. Restore forbidden if object archived/deleted unless policy allows.
```

Supported initial restore candidates:

```text
Requirement
Document metadata / current version pointer
MeetingMinutes
CustomForm draft version
CustomFieldDefinition before values maybe limited
Task snapshot optional
RAID item optional
DecisionRecord optional
```

Not recommended:

```text
Baseline restore by direct mutation
Financial/quote version overwrite
Audit log restore
Historical acceptance overwrite
```

---

## 9.10 LOCK-001 — Lock / Finalize

Classification: `MUST_IMPLEMENT_IN_PHASE_34`

Purpose:

```text
Prevent silent mutation of finalized/controlled objects.
```

Lock types:

```text
FINALIZED
BASELINED
CONTROLLED
RELEASED
CLIENT_VISIBLE_PUBLISHED
ARCHIVED
SYSTEM_LOCK
```

Rules:

```text
1. Locked object cannot be silently edited.
2. Edits require unlock permission, revision/new version, or ChangeRequest path.
3. Lock/unlock requires reason and audit.
4. Lock does not mean approval.
5. Lock can be automatic by domain lifecycle.
```

Examples:

```text
Baseline -> BASELINED lock
Document approved/current version -> CONTROLLED lock
ReleasePackage released -> RELEASED lock
MeetingMinutes approved/final -> FINALIZED lock
Client-visible proposal -> CLIENT_VISIBLE_PUBLISHED lock
```

---

## 9.11 BASE-001 — Baseline / Change guard

Classification: `MUST_IMPLEMENT_IN_PHASE_34`

Purpose:

```text
Prevent baseline-controlled commitments from changing silently.
```

Guard modes:

```text
OFF
WARN_ONLY
BLOCK_REQUIRE_CHANGE_REQUEST
BLOCK_REQUIRE_NEW_VERSION
```

Rules:

```text
1. Guard checks before mutation.
2. Guard uses Phase 19 baseline/change rules.
3. Scope/time/cost/release baseline changes should require ChangeRequest or new controlled version.
4. Guard result included in error response.
5. Guard does not approve; it blocks unsafe mutation.
```

---

## 9.12 AUD-001 — Governance audit

Classification: `MUST_IMPLEMENT_IN_PHASE_34`

Purpose:

```text
Record meaningful governance actions.
```

Audit actions:

```text
OWNER_ASSIGNED
OWNER_TRANSFERRED
OBJECT_GRANT_CREATED
OBJECT_GRANT_UPDATED
OBJECT_GRANT_REVOKED
VERSION_CREATED
SNAPSHOT_CREATED
RESTORE_REQUESTED
RESTORE_COMPLETED
LOCK_CREATED
LOCK_RELEASED
FINALIZED
UNFINALIZED
CLIENT_VISIBLE_CHANGED
SENSITIVE_FIELD_CHANGED
BASELINE_GUARD_BLOCKED
PERMISSION_DENIED
```

Rules:

```text
1. Audit entries immutable.
2. Sensitive values masked.
3. Actor/principal recorded.
4. Trace id recorded.
5. Audit report available to authorized users.
```

---

## 9.13 SENS-001 — Sensitive access tracking

Classification: `MUST_IMPLEMENT_IN_PHASE_34`

Purpose:

```text
Audit access to sensitive governed data.
```

Sensitive targets:

```text
finance fields
quote internal margin/cost
restricted documents
external contact personal data
client portal access
custom sensitive fields
AI prompts/context snapshots
audit exports
permission grants
```

Rules:

```text
1. Read/download/export of sensitive data audited where policy says.
2. Search snippets never expose sensitive data.
3. Reports/export mask without permission.
4. Sensitive access audit does not log raw sensitive values.
```

---

## 9.14 CVIS-001 — Client-visible change tracking

Classification: `MUST_IMPLEMENT_IN_PHASE_34`

Purpose:

```text
Track changes to data exposed or eligible for client portal.
```

Rules:

```text
1. clientVisible flag changes audited.
2. Client-visible content changes create version/snapshot if policy.
3. Published client-visible object can be locked.
4. Portal sees only current allowed version.
5. Internal notes remain hidden.
```

---

## 9.15 DEL-001 — Archive / soft delete governance

Classification: `MUST_IMPLEMENT_IN_PHASE_34`

Purpose:

```text
Standardize archive and soft delete behavior.
```

Rules:

```text
1. Archive preferred over hard delete.
2. Hard delete restricted to admin/compliance policy.
3. Archive requires permission and sometimes reason.
4. Archived object remains in audit/history.
5. Restore from archive follows domain policy.
6. Search excludes archived by default.
```

---

## 9.16 REP-001 — Governance reporting

Classification: `MUST_IMPLEMENT_IN_PHASE_34`

Reports:

```text
object ownership report
object access grant report
version history report
locked/finalized objects report
restore activity report
baseline guard blocked actions report
client-visible change report
sensitive access audit report
permission change report
governance policy report
```

Dashboard KPIs:

```text
versionedObjectCount
lockedObjectCount
objectsWithoutOwner
objectGrantsActive
restoresThisPeriod
baselineGuardBlocks
clientVisibleChanges
sensitiveAccessEvents
policyChanges
```

---

## 9.17 NOTIF-001 — Governance notifications

Classification: `MUST_IMPLEMENT_IN_PHASE_34`

Notification candidates:

```text
ownership assigned/transferred
object access granted/revoked
important object version created
object locked/finalized
object restored/reverted
baseline guard blocked change
client-visible content changed
sensitive object accessed/exported
```

Rules:

```text
1. Notifications do not leak sensitive content.
2. Recipients are owner, project manager, watchers, admins depending policy.
3. External notifications only if portal policy allows.
```

---

## 9.18 AI-001 — AI-assisted governance summaries

Classification: `SEED_ONLY_IN_PHASE_34` or `MUST_IMPLEMENT_IN_PHASE_34` if Phase 21 tool registry exists.

AI can help with:

```text
summarize version changes
explain diff
summarize governance risk
suggest owner for unowned object
suggest client-visible change summary
summarize audit trail
```

Rules:

```text
1. AI output is explanation/proposal only.
2. AI cannot grant permissions.
3. AI cannot restore/revert.
4. AI cannot unlock/finalize.
5. AI cannot expose hidden sensitive values.
```

---

# 10. Entity model TO-BE

---

## 10.1 GovernedObjectType — `governance_object_type`

Fields:

```text
id UUID PK
object_type_code VARCHAR(100) NOT NULL
name VARCHAR(255) NOT NULL
description TEXT NULL
domain_module VARCHAR(150) NULL
ownership_supported BOOLEAN NOT NULL DEFAULT false
object_grant_supported BOOLEAN NOT NULL DEFAULT false
versioning_supported BOOLEAN NOT NULL DEFAULT false
snapshot_supported BOOLEAN NOT NULL DEFAULT false
diff_supported BOOLEAN NOT NULL DEFAULT false
restore_supported BOOLEAN NOT NULL DEFAULT false
lock_supported BOOLEAN NOT NULL DEFAULT false
client_visible_audit_supported BOOLEAN NOT NULL DEFAULT false
sensitive_access_audit_supported BOOLEAN NOT NULL DEFAULT false
baseline_guard_supported BOOLEAN NOT NULL DEFAULT false
enabled BOOLEAN NOT NULL DEFAULT true
created_at TIMESTAMP NOT NULL
updated_at TIMESTAMP NULL
version INT
```

Constraint:

```text
unique object_type_code
```

---

## 10.2 GovernancePolicy — `governance_policy`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
object_type_code VARCHAR(100) NOT NULL
versioning_mode VARCHAR(50) NOT NULL
version_on_create BOOLEAN NOT NULL DEFAULT false
version_on_update BOOLEAN NOT NULL DEFAULT false
version_on_status_change BOOLEAN NOT NULL DEFAULT false
version_on_client_visible_change BOOLEAN NOT NULL DEFAULT true
version_on_sensitive_field_change BOOLEAN NOT NULL DEFAULT true
lock_on_finalize BOOLEAN NOT NULL DEFAULT true
require_reason_for_sensitive_change BOOLEAN NOT NULL DEFAULT true
allow_restore BOOLEAN NOT NULL DEFAULT false
allow_owner_grant BOOLEAN NOT NULL DEFAULT false
allow_object_level_grant BOOLEAN NOT NULL DEFAULT false
baseline_guard_mode VARCHAR(50) NOT NULL
audit_level VARCHAR(50) NOT NULL
enabled BOOLEAN NOT NULL DEFAULT true
created_at / created_by
updated_at / updated_by
version INT
```

Constraint:

```text
unique workspace_id + object_type_code
```

---

## 10.3 ObjectOwnership — `governance_object_ownership`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
project_id UUID NULL
object_type_code VARCHAR(100) NOT NULL
target_id UUID NOT NULL
owner_target_type VARCHAR(50) NOT NULL
owner_target_id UUID NULL
owner_display_name_snapshot VARCHAR(255) NULL
assigned_reason TEXT NULL
assigned_at TIMESTAMP NOT NULL
assigned_by UUID NULL
revoked_at TIMESTAMP NULL
revoked_by UUID NULL
version INT
```

Constraint:

```text
unique active object_type_code + target_id
```

---

## 10.4 ObjectAccessGrant — `governance_object_access_grant`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
project_id UUID NULL
object_type_code VARCHAR(100) NOT NULL
target_id UUID NOT NULL
grantee_type VARCHAR(50) NOT NULL
grantee_id UUID NULL
grant_role VARCHAR(50) NOT NULL
status VARCHAR(50) NOT NULL
expires_at TIMESTAMP NULL
reason TEXT NULL
created_at / created_by
updated_at / updated_by
revoked_at / revoked_by
revocation_reason TEXT NULL
trace_id VARCHAR(100) NULL
version INT
```

Status:

```text
ACTIVE
EXPIRED
REVOKED
ARCHIVED
```

Constraint:

```text
unique active object_type_code + target_id + grantee_type + grantee_id + grant_role
```

---

## 10.5 GovernanceVersionRecord — `governance_version_record`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
project_id UUID NULL
object_type_code VARCHAR(100) NOT NULL
target_id UUID NOT NULL
version_number INT NULL
version_label VARCHAR(100) NULL
domain_version_type VARCHAR(100) NULL
domain_version_id UUID NULL
snapshot_id UUID NULL
change_type VARCHAR(100) NOT NULL
change_reason TEXT NULL
current_flag BOOLEAN NOT NULL DEFAULT false
finalized_flag BOOLEAN NOT NULL DEFAULT false
restore_eligible BOOLEAN NOT NULL DEFAULT false
created_at TIMESTAMP NOT NULL
created_by UUID NULL
trace_id VARCHAR(100) NULL
version INT
```

Change types:

```text
CREATE
UPDATE
STATUS_CHANGE
CLIENT_VISIBLE_CHANGE
SENSITIVE_CHANGE
RESTORE
FINALIZE
UNFINALIZE
DOMAIN_VERSION_CREATED
SNAPSHOT_CREATED
```

---

## 10.6 GovernanceSnapshot — `governance_snapshot`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
project_id UUID NULL
object_type_code VARCHAR(100) NOT NULL
target_id UUID NOT NULL
snapshot_mode VARCHAR(50) NOT NULL
schema_version VARCHAR(50) NULL
snapshot_json JSONB NOT NULL
masked_fields_json JSONB NULL
sensitive_fields_present BOOLEAN NOT NULL DEFAULT false
created_at TIMESTAMP NOT NULL
created_by UUID NULL
trace_id VARCHAR(100) NULL
version INT
```

---

## 10.7 ObjectLock — `governance_object_lock`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
project_id UUID NULL
object_type_code VARCHAR(100) NOT NULL
target_id UUID NOT NULL
lock_type VARCHAR(50) NOT NULL
status VARCHAR(50) NOT NULL
reason TEXT NULL
locked_at TIMESTAMP NOT NULL
locked_by UUID NULL
released_at TIMESTAMP NULL
released_by UUID NULL
release_reason TEXT NULL
trace_id VARCHAR(100) NULL
version INT
```

Status:

```text
ACTIVE
RELEASED
EXPIRED
ARCHIVED
```

Constraint:

```text
unique active object_type_code + target_id + lock_type
```

---

## 10.8 RestoreRequest — `governance_restore_request`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
project_id UUID NULL
object_type_code VARCHAR(100) NOT NULL
target_id UUID NOT NULL
restore_from_version_record_id UUID NOT NULL
status VARCHAR(50) NOT NULL
reason TEXT NOT NULL
requested_at TIMESTAMP NOT NULL
requested_by UUID NOT NULL
completed_at TIMESTAMP NULL
completed_by UUID NULL
result_version_record_id UUID NULL
failure_code VARCHAR(150) NULL
failure_message TEXT NULL
trace_id VARCHAR(100) NULL
version INT
```

Status:

```text
REQUESTED
VALIDATED
COMPLETED
FAILED
CANCELLED
```

No approval step. Request here means execution tracking, not approval.

---

## 10.9 GovernanceDiff — optional `governance_diff_cache`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
project_id UUID NULL
object_type_code VARCHAR(100) NOT NULL
target_id UUID NOT NULL
from_version_record_id UUID NOT NULL
to_version_record_id UUID NOT NULL
diff_json JSONB NOT NULL
masked BOOLEAN NOT NULL DEFAULT false
created_at TIMESTAMP NOT NULL
version INT
```

Can be computed on demand instead of persisted.

---

## 10.10 GovernanceAuditEvent — optional if Phase 04 audit is insufficient

Prefer existing Phase 04 audit if available.

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
project_id UUID NULL
object_type_code VARCHAR(100) NULL
target_id UUID NULL
actor_principal_type VARCHAR(50) NOT NULL
actor_user_id UUID NULL
actor_external_portal_account_id UUID NULL
action VARCHAR(150) NOT NULL
details_json JSONB NULL
sensitive BOOLEAN NOT NULL DEFAULT false
created_at TIMESTAMP NOT NULL
trace_id VARCHAR(100) NULL
```

---

# 11. API TO-BE list

All APIs use `/api`.

---

## 11.1 Governed object type APIs

```text
GET /api/governance/object-types
GET /api/governance/object-types/{objectTypeCode}
```

Admin/internal seed:

```text
POST /api/admin/governance/object-types/seed
```

---

## 11.2 Governance policy APIs

```text
GET  /api/workspaces/{workspaceId}/governance/policies
GET  /api/workspaces/{workspaceId}/governance/policies/{objectTypeCode}
PUT  /api/workspaces/{workspaceId}/governance/policies/{objectTypeCode}
POST /api/workspaces/{workspaceId}/governance/policies/reset-defaults
```

---

## 11.3 Ownership APIs

```text
GET  /api/projects/{projectId}/governance/ownership
GET  /api/projects/{projectId}/governance/ownership/{objectTypeCode}/{targetId}
POST /api/projects/{projectId}/governance/ownership/{objectTypeCode}/{targetId}/assign
POST /api/projects/{projectId}/governance/ownership/{objectTypeCode}/{targetId}/transfer
POST /api/projects/{projectId}/governance/ownership/{objectTypeCode}/{targetId}/revoke
```

---

## 11.4 Object access grant APIs

```text
POST   /api/projects/{projectId}/governance/access-grants
GET    /api/projects/{projectId}/governance/access-grants
GET    /api/projects/{projectId}/governance/access-grants/{grantId}
PATCH  /api/projects/{projectId}/governance/access-grants/{grantId}
DELETE /api/projects/{projectId}/governance/access-grants/{grantId}
```

Grant request:

```json
{
  "objectTypeCode": "DOCUMENT",
  "targetId": "uuid",
  "granteeType": "INTERNAL_USER",
  "granteeId": "uuid",
  "grantRole": "VIEW",
  "expiresAt": null,
  "reason": "Need access for implementation support"
}
```

---

## 11.5 Version history APIs

```text
GET /api/projects/{projectId}/governance/versions
GET /api/projects/{projectId}/governance/versions/{objectTypeCode}/{targetId}
GET /api/projects/{projectId}/governance/versions/{objectTypeCode}/{targetId}/{versionRecordId}
```

Filters:

```text
objectTypeCode
targetId
changeType
createdBy
dateFrom
dateTo
currentOnly
finalizedOnly
```

---

## 11.6 Snapshot / diff APIs

```text
GET  /api/projects/{projectId}/governance/snapshots/{snapshotId}
POST /api/projects/{projectId}/governance/diff
GET  /api/projects/{projectId}/governance/diff/{objectTypeCode}/{targetId}?fromVersionId=...&toVersionId=...
```

Diff request:

```json
{
  "objectTypeCode": "REQUIREMENT",
  "targetId": "uuid",
  "fromVersionRecordId": "uuid",
  "toVersionRecordId": "uuid"
}
```

---

## 11.7 Restore APIs

```text
POST /api/projects/{projectId}/governance/restore
GET  /api/projects/{projectId}/governance/restore-requests
GET  /api/projects/{projectId}/governance/restore-requests/{restoreRequestId}
```

Restore request:

```json
{
  "objectTypeCode": "REQUIREMENT",
  "targetId": "uuid",
  "restoreFromVersionRecordId": "uuid",
  "reason": "Restore previous wording after accidental edit"
}
```

Important:

```text
Restore executes immediately if valid.
No approval request is created.
```

---

## 11.8 Lock / finalize APIs

```text
POST   /api/projects/{projectId}/governance/locks
GET    /api/projects/{projectId}/governance/locks
GET    /api/projects/{projectId}/governance/locks/{lockId}
DELETE /api/projects/{projectId}/governance/locks/{lockId}
```

Convenience:

```text
POST /api/projects/{projectId}/governance/{objectTypeCode}/{targetId}/finalize
POST /api/projects/{projectId}/governance/{objectTypeCode}/{targetId}/unfinalize
```

---

## 11.9 Baseline guard APIs

```text
POST /api/projects/{projectId}/governance/baseline-guard/check
GET  /api/projects/{projectId}/governance/baseline-guard/blocked-actions
```

Check request:

```json
{
  "objectTypeCode": "SCOPE_ITEM",
  "targetId": "uuid",
  "action": "UPDATE",
  "proposedChanges": {}
}
```

---

## 11.10 Audit/report APIs

```text
GET /api/projects/{projectId}/governance/audit
GET /api/projects/{projectId}/reports/governance/ownership
GET /api/projects/{projectId}/reports/governance/access-grants
GET /api/projects/{projectId}/reports/governance/version-history
GET /api/projects/{projectId}/reports/governance/locked-objects
GET /api/projects/{projectId}/reports/governance/restore-activity
GET /api/projects/{projectId}/reports/governance/baseline-guard
GET /api/projects/{projectId}/reports/governance/client-visible-changes
GET /api/projects/{projectId}/reports/governance/sensitive-access
```

---

# 12. Authorization requirements

Required authorities:

```text
GOVERNANCE_OBJECT_TYPE_VIEW

GOVERNANCE_POLICY_VIEW
GOVERNANCE_POLICY_UPDATE
GOVERNANCE_POLICY_RESET

OBJECT_OWNERSHIP_VIEW
OBJECT_OWNERSHIP_ASSIGN
OBJECT_OWNERSHIP_TRANSFER
OBJECT_OWNERSHIP_REVOKE

OBJECT_ACCESS_GRANT_VIEW
OBJECT_ACCESS_GRANT_CREATE
OBJECT_ACCESS_GRANT_UPDATE
OBJECT_ACCESS_GRANT_REVOKE

OBJECT_VERSION_VIEW
OBJECT_VERSION_DIFF
OBJECT_SNAPSHOT_VIEW
OBJECT_RESTORE

OBJECT_LOCK_VIEW
OBJECT_LOCK_CREATE
OBJECT_LOCK_RELEASE
OBJECT_FINALIZE
OBJECT_UNFINALIZE

BASELINE_GUARD_CHECK
GOVERNANCE_AUDIT_VIEW
GOVERNANCE_REPORT_VIEW
SENSITIVE_ACCESS_AUDIT_VIEW
```

Rules:

```text
1. Target object access still required.
2. Owner can manage access only if policy allows.
3. Object grant does not bypass workspace/project membership.
4. Restore requires target edit/manage permission.
5. Lock/unlock requires stronger permission.
6. Sensitive snapshots/diffs require sensitive view permission.
7. Governance policy update requires workspace admin/config permission.
```

---

# 13. Lifecycle rules

## 13.1 GovernancePolicy lifecycle

```text
ACTIVE → UPDATED
ACTIVE → DISABLED
```

Rules:

```text
1. Policy update audited.
2. System minimums cannot be weakened below safe baseline.
3. Existing version records remain unchanged.
```

## 13.2 Ownership lifecycle

```text
ASSIGNED → TRANSFERRED
ASSIGNED → REVOKED
```

Rules:

```text
1. Only one active owner record where policy says single owner.
2. Transfer creates audit trail.
3. Revoked ownership does not delete history.
```

## 13.3 Access grant lifecycle

```text
ACTIVE → EXPIRED
ACTIVE → REVOKED
ACTIVE → ARCHIVED
```

Rules:

```text
1. Expired/revoked grants no longer authorize object access.
2. Grant history retained.
```

## 13.4 Version lifecycle

```text
CREATED → CURRENT
CURRENT → SUPERSEDED
CREATED/CURRENT → FINALIZED
```

Rules:

```text
1. Version metadata immutable.
2. Current pointer changes by domain/version service.
3. Restore creates new version.
```

## 13.5 Lock lifecycle

```text
ACTIVE → RELEASED
ACTIVE → EXPIRED
ACTIVE → ARCHIVED
```

Rules:

```text
1. Active lock blocks silent mutation.
2. Release requires permission and reason.
3. Release audited.
```

## 13.6 Restore lifecycle

```text
REQUESTED → VALIDATED → COMPLETED
REQUESTED/VALIDATED → FAILED
REQUESTED → CANCELLED
```

No approval step.

---

# 14. Module integration rules

## 14.1 Quote integration

Rules:

```text
1. QuoteVersion remains source of truth.
2. GovernanceVersionRecord references QuoteVersion.
3. Internal cost/margin access audited.
4. Sending client-visible proposal can lock/finalize version.
5. No quote approval workflow required.
```

## 14.2 ChangeRequest integration

Rules:

```text
1. ChangeRequest retains status/version history.
2. Owner/admin with permission can apply if domain rules pass.
3. Baseline guard blocks direct mutation that should become ChangeRequest.
4. Applying ChangeRequest creates audit/version records.
5. No approval request required.
```

## 14.3 Baseline integration

Rules:

```text
1. Baseline snapshot immutable.
2. Baseline locks controlled scope/time/cost references.
3. Baseline-affecting changes use guard.
4. No approval engine.
```

## 14.4 Document integration

Rules:

```text
1. DocumentVersion remains source of truth for files.
2. Governance records track controlled/finalized/current changes.
3. Download/access audit remains in DocumentHub.
4. Restore may change current version pointer if policy allows.
5. Binary file content diff not required.
```

## 14.5 Requirement integration

Rules:

```text
1. RequirementVersion remains source of truth if implemented.
2. Requirement snapshot can fill gaps.
3. Owner/admin can edit if permission and lock/baseline guard allow.
4. Approved/finalized requirement should create new version instead of silent edit.
5. No requirement approval workflow.
```

## 14.6 Deliverable / Acceptance integration

Rules:

```text
1. Acceptance records remain evidence.
2. Accepted/finalized deliverables locked from silent mutation.
3. Correction creates new evidence/version record.
4. Acceptance does not need approval engine.
```

## 14.7 Release integration

Rules:

```text
1. Released ReleasePackage locked.
2. Hotfix/change creates new package/version.
3. Release action requires permission and domain readiness rules.
4. No release approval workflow required.
```

## 14.8 RAID / Decision integration

Rules:

```text
1. DecisionRecord can be finalized/locked.
2. Risk acceptance can be versioned/audited.
3. Owner controls updates if permission allows.
4. No decision approval workflow.
```

## 14.9 Meeting minutes integration

Rules:

```text
1. MeetingMinutes can be finalized.
2. Finalized minutes locked.
3. Revisions create new version/snapshot.
4. No minutes approval workflow.
```

## 14.10 Custom config integration

Rules:

```text
1. Published form/config versions immutable.
2. Config changes audited.
3. Restore draft config only where safe.
4. No config approval workflow.
```

## 14.11 External portal integration

Rules:

```text
1. Client-visible changes audited.
2. Portal sees current allowed version only.
3. External access grants remain explicit.
4. External user cannot access internal snapshots/diffs.
```

---

# 15. Search / report / export integration

## 15.1 Search

Rules:

```text
1. Search returns current object state by default.
2. Version history searchable only if permission and query includes history scope.
3. Sensitive version fields masked.
4. Archived/locked filters supported where relevant.
```

## 15.2 Reporting

Rules:

```text
1. Governance reports respect project/workspace permissions.
2. Sensitive audit reports require sensitive audit permission.
3. Client-visible change reports exclude internal details.
```

## 15.3 Export

Rules:

```text
1. Version history export requires permission.
2. Sensitive values masked unless export permission.
3. Audit export tracked.
```

---

# 16. Event Registry integration

Recommended source system:

```text
SCOPERY_GOVERNANCE
```

Required events:

```text
GOVERNANCE_OBJECT_TYPE_SEEDED

GOVERNANCE_POLICY_CREATED
GOVERNANCE_POLICY_UPDATED
GOVERNANCE_POLICY_DISABLED

OBJECT_OWNER_ASSIGNED
OBJECT_OWNER_TRANSFERRED
OBJECT_OWNER_REVOKED

OBJECT_ACCESS_GRANT_CREATED
OBJECT_ACCESS_GRANT_UPDATED
OBJECT_ACCESS_GRANT_REVOKED
OBJECT_ACCESS_GRANT_EXPIRED

GOVERNANCE_VERSION_RECORD_CREATED
GOVERNANCE_SNAPSHOT_CREATED
GOVERNANCE_DIFF_VIEWED

OBJECT_RESTORE_REQUESTED
OBJECT_RESTORE_COMPLETED
OBJECT_RESTORE_FAILED

OBJECT_LOCK_CREATED
OBJECT_LOCK_RELEASED
OBJECT_FINALIZED
OBJECT_UNFINALIZED

CLIENT_VISIBLE_FIELD_CHANGED
SENSITIVE_FIELD_CHANGED
SENSITIVE_OBJECT_ACCESSED
BASELINE_GUARD_CHECKED
BASELINE_GUARD_BLOCKED

GOVERNANCE_AUDIT_EXPORTED
```

Standard variables:

```text
actor.userId
externalPortalAccount.id
workspace.id
project.id
objectType.code
target.id
owner.type
owner.id
grant.id
versionRecord.id
snapshot.id
lock.id
restoreRequest.id
change.type
occurredAt
traceId
```

---

# 17. Audit / activity / outbox

Audit-sensitive actions:

```text
owner transfer
object access grant create/revoke
sensitive field viewed/changed
client-visible content changed
restore completed
object lock released
baseline guard override if allowed
governance policy changed
audit export
```

Activity actions:

```text
OBJECT_OWNER_ASSIGNED
GOVERNANCE_VERSION_RECORD_CREATED
OBJECT_FINALIZED
OBJECT_RESTORE_COMPLETED
OBJECT_ACCESS_GRANT_CREATED
BASELINE_GUARD_BLOCKED
```

Outbox required for:

```text
object finalized
object restored
access granted/revoked
client-visible content changed
baseline guard blocked
sensitive access alert
```

Idempotency recommended for:

```text
assign owner
create access grant
create version record
restore
lock/finalize
unfinalize
policy update
```

---

# 18. Business rules master

## 18.1 Permission/ownership rules

```text
OWN-001 Owner does not bypass IAM.
OWN-002 Owner can grant access only if GovernancePolicy allows.
OWN-003 Object grant does not bypass workspace/project access.
OWN-004 External grant requires portal account and visibility policy.
OWN-005 Ownership transfer requires audit.
```

## 18.2 Versioning rules

```text
VER-001 Important objects create version record according to policy.
VER-002 Domain version remains source of truth where present.
VER-003 Snapshot must not include unauthorized raw sensitive fields.
VER-004 Version metadata immutable.
VER-005 Restore creates new version and never overwrites history.
```

## 18.3 Lock/finalize rules

```text
LOCK-001 Locked object cannot be silently edited.
LOCK-002 Unlock/unfinalize requires permission and reason.
LOCK-003 Lock is not approval.
LOCK-004 Finalized objects use revision/new version path.
```

## 18.4 Baseline guard rules

```text
BASE-001 Baseline-controlled mutation checked before update.
BASE-002 Guard blocks or warns based on policy.
BASE-003 Blocked action returns structured reason.
BASE-004 ChangeRequest remains domain mechanism for commitment changes.
```

## 18.5 Audit rules

```text
AUD-001 Governance audit immutable.
AUD-002 Sensitive values masked.
AUD-003 Actor/principal recorded.
AUD-004 Audit export tracked.
```

---

# 19. Error catalog

```text
GOVERNANCE_OBJECT_TYPE_NOT_FOUND
GOVERNANCE_OBJECT_TYPE_FEATURE_DISABLED
GOVERNANCE_POLICY_NOT_FOUND
GOVERNANCE_POLICY_MINIMUM_VIOLATION
GOVERNANCE_POLICY_ACCESS_DENIED

OBJECT_OWNERSHIP_NOT_FOUND
OBJECT_OWNER_INVALID
OBJECT_OWNER_TRANSFER_NOT_ALLOWED
OBJECT_OWNER_ACCESS_DENIED

OBJECT_ACCESS_GRANT_NOT_FOUND
OBJECT_ACCESS_GRANT_DUPLICATE
OBJECT_ACCESS_GRANT_NOT_ALLOWED
OBJECT_ACCESS_GRANT_GRANTEE_INVALID
OBJECT_ACCESS_GRANT_REVOKED
OBJECT_ACCESS_GRANT_EXPIRED
OBJECT_ACCESS_GRANT_ACCESS_DENIED

GOVERNANCE_VERSION_RECORD_NOT_FOUND
GOVERNANCE_VERSION_NOT_RESTORE_ELIGIBLE
GOVERNANCE_SNAPSHOT_NOT_FOUND
GOVERNANCE_SNAPSHOT_ACCESS_DENIED
GOVERNANCE_DIFF_NOT_AVAILABLE
GOVERNANCE_DIFF_ACCESS_DENIED

OBJECT_RESTORE_NOT_ALLOWED
OBJECT_RESTORE_REASON_REQUIRED
OBJECT_RESTORE_DOMAIN_VALIDATION_FAILED
OBJECT_RESTORE_BASELINE_GUARD_BLOCKED

OBJECT_LOCK_NOT_FOUND
OBJECT_LOCK_ACTIVE
OBJECT_LOCK_NOT_ALLOWED
OBJECT_LOCK_RELEASE_REASON_REQUIRED
OBJECT_FINALIZE_NOT_ALLOWED
OBJECT_UNFINALIZE_NOT_ALLOWED

BASELINE_GUARD_BLOCKED
BASELINE_GUARD_CHECK_FAILED

SENSITIVE_ACCESS_DENIED
GOVERNANCE_AUDIT_ACCESS_DENIED
```

---

# 20. Required tests

## 20.1 Object type / policy tests

```text
governanceObjectTypeSeeder_firstRun_createsDefaults
governanceObjectTypeSeeder_secondRun_noDuplicates
createGovernancePolicy_valid_success
updatePolicy_belowSystemMinimum_rejected
policyChange_audited
```

## 20.2 Ownership tests

```text
assignOwner_valid_success
transferOwner_valid_success
ownerDoesNotBypassProjectAccess
externalContactOwnerDoesNotBecomeUser
ownershipTransfer_audited
```

## 20.3 Access grant tests

```text
createObjectGrant_valid_success
createObjectGrant_duplicate_rejected
objectGrantDoesNotBypassWorkspaceAccess
objectGrantRevoked_blocksAccess
objectGrantExpired_blocksAccess
ownerGrantDisabled_rejected
externalGrantRequiresPortalPolicy
grantChange_audited
```

## 20.4 Version/snapshot tests

```text
createGovernanceVersionRecord_success
versionRecordImmutable
snapshotCreatedOnMajorChange
snapshotMasksSensitiveFields
domainVersionReference_success
currentVersionUnique
```

## 20.5 Diff tests

```text
diffBetweenSnapshots_success
diffMasksSensitiveValuesWithoutPermission
diffUnavailableForUnsupportedType
documentBinaryDiff_notRequired
```

## 20.6 Restore tests

```text
restoreSupportedObject_success
restoreCreatesNewVersion
restoreDoesNotOverwriteHistory
restoreRequiresReason
restoreWithoutPermission_forbidden
restoreBaselineGuardBlocked_rejected
restoreUnsupportedObject_rejected
```

## 20.7 Lock/finalize tests

```text
finalizeObject_success
lockedObjectSilentEdit_rejected
unfinalizeRequiresReason
lockIsNotApproval
finalizedObjectCreatesRevisionPath
releaseLockPreventsMutation
```

## 20.8 Baseline guard tests

```text
baselineGuardWarnOnly_returnsWarning
baselineGuardBlock_rejectsMutation
baselineGuardChangeRequestPath_allowed
baselineGuardAuditCreated
```

## 20.9 Sensitive/client-visible tests

```text
sensitiveFieldChange_requiresReason
sensitiveFieldAccess_audited
clientVisibleFlagChange_audited
clientVisibleContentChange_createsVersion
portalSeesOnlyCurrentAllowedVersion
```

## 20.10 Authorization tests

```text
viewGovernancePolicy_withoutPermission_forbidden
assignOwner_withoutPermission_forbidden
createGrant_withoutPermission_forbidden
restore_withoutPermission_forbidden
releaseLock_withoutPermission_forbidden
viewSensitiveSnapshot_withoutPermission_forbidden
crossWorkspaceGovernance_forbidden
```

## 20.11 Event/audit tests

```text
governanceEventSeeder_firstRun_createsDefinitions
governanceEventSeeder_secondRun_noDuplicates
ownerAssigned_eventEmitted
grantCreated_eventEmitted
versionCreated_eventEmitted
restoreCompleted_eventEmitted
baselineGuardBlocked_eventEmitted
```

---

# 21. Manual verification checklist

Completion file must include:

```text
1. Seed GovernedObjectType records.
2. Configure GovernancePolicy for Requirement.
3. Assign owner to Requirement.
4. Owner grants VIEW access to another user.
5. Confirm grant does not work without project access.
6. Update Requirement and create version/snapshot.
7. View version history.
8. Compare two versions.
9. Mark Requirement finalized/locked.
10. Try silent edit and confirm blocked.
11. Restore old version and confirm new version created.
12. Confirm restore does not overwrite history.
13. Trigger baseline guard on controlled ScopeItem.
14. Confirm blocked action returns structured error.
15. Change clientVisible field and confirm audit/version.
16. Access sensitive field and confirm audit.
17. Confirm no ApprovalRequest/WorkflowRun/ApprovalDecision tables are required.
18. Confirm no workflow approval engine is falsely claimed.
```

---

# 22. Acceptance criteria

Phase 34 is accepted only if:

```text
1. Previous approval-engine direction is explicitly deprecated.
2. Current governance/versioning/ownership/audit capability is classified against TO-BE.
3. GovernedObjectType implemented/seeded/tested.
4. GovernancePolicy implemented/tested.
5. ObjectOwnership implemented/tested.
6. ObjectAccessGrant implemented/tested where policy allows.
7. GovernanceVersionRecord implemented/tested.
8. Snapshot/diff implemented/tested for supported objects.
9. Restore/revert implemented/tested for supported objects.
10. Lock/finalize implemented/tested.
11. Baseline/change guard integrated/tested.
12. Sensitive access tracking implemented/tested.
13. Client-visible change tracking implemented/tested.
14. Reporting/notification/search integration implemented/tested.
15. IAM permissions implemented/tested.
16. Event seeders idempotent.
17. Activity/audit/outbox follows Phase 04.
18. No approval workflow, BPMN, delegation, approval SLA, or arbitrary scripting is falsely claimed.
19. `mvn compile` passes.
20. `mvn test` passes.
21. Completion file exists.
```

Do not mark complete if:

```text
owner bypasses IAM
object grant bypasses workspace/project access
version restore overwrites history
locked object can be silently edited
sensitive snapshots leak
client-visible changes are not audited
baseline guard can be bypassed silently
approval workflow tables are introduced unnecessarily
tests fail
```

---

# 23. Required phase completion file

Agent must create:

```text
docs/phase-complete/PHASE_34_GOVERNANCE_VERSIONING_PERMISSION_AUDIT_TO_BE_COMPLETE.md
```

Required sections:

```text
# Phase 34 — Governance / Versioning / Permission / Audit Complete

## 1. Summary
## 2. Direction Change From Approval Engine
## 3. Source Inputs Reviewed
## 4. Current vs TO-BE Classification Matrix
## 5. Implemented in Current BE
## 6. Implemented / Hardened in This Phase
## 7. Seed-only Items Added
## 8. Deferred Items and Target Phase
## 9. Governance Boundary Decision
## 10. No-Approval Decision
## 11. Entity Mapping
## 12. API Changes
## 13. Governed Object Type Strategy
## 14. Governance Policy Strategy
## 15. Ownership Strategy
## 16. Object Access Grant Strategy
## 17. Version Metadata Strategy
## 18. Snapshot / Diff Strategy
## 19. Restore / Revert Strategy
## 20. Lock / Finalize Strategy
## 21. Baseline Guard Strategy
## 22. Sensitive Access Tracking Strategy
## 23. Client-visible Change Strategy
## 24. Module Integration Notes
## 25. Search / Report / Export Integration
## 26. Notification / Event Strategy
## 27. Authorization Matrix
## 28. Activity / Audit / Outbox Notes
## 29. Idempotency Strategy
## 30. Tests Added
## 31. Commands Run
## 32. Test Results
## 33. Manual Verification
## 34. Assumptions
## 35. Deviations From Prompt
## 36. Known Risks
## 37. Future Phases That Must Return to Governance
```

---

# 24. Future phases that may return

```text
Phase 35 — Advanced Notifications:
- Governance alerts, sensitive access digest, ownership reminders.

Phase 36 — Contract / Billing / Revenue:
- Commercial versioning, invoice/contract record governance.

Phase 38 — Audit / Compliance / Privacy:
- Legal retention, audit export, privacy deletion/export, sensitive access review.

Phase 39 — Integration / Import / Export:
- External version import/export, storage providers, enterprise audit integrations.

Phase 41 — Data Quality / Knowledge Graph / Semantic Index:
- Governance intelligence, semantic diff summary, stale owner detection.

Optional future workflow backlog:
- Only if real users later need it.
- Keep separate from MVP.
```

---

# 25. Agent anti-bịa rules

The agent must not:

```text
1. Implement ApprovalRequest.
2. Implement ApprovalDecision.
3. Implement WorkflowRun.
4. Claim approval workflow exists.
5. Claim locking means approval.
6. Let owner bypass IAM.
7. Let object grant bypass project/workspace access.
8. Let restore overwrite history.
9. Let locked/finalized object be silently edited.
10. Let sensitive data leak in diff/snapshot/search/report/export.
11. Hide that previous approval-engine direction was removed.
```

---

# 26. Prompt to give coding agent

```text
You are implementing Phase 34 — TO-BE Governance by Permission, Versioning, Ownership, Locking, Audit & Restore.

Important direction change:
The previous Phase 34 Workflow / Approval Engine direction is deprecated.
Do NOT implement WorkflowDefinition, WorkflowRun, ApprovalRequest, ApprovalDecision, delegation, escalation, approval SLA, or BPMN.

Scopery governance should be:
Versioning + Permission + Ownership + Locking + Audit + Restore.

Before coding, read:
- CLAUDE.md / CLAUDE.ms
- Coding_convention.md
- Phase 00–33 docs and completion files
- Current backend code, migrations, tests

Your task:
1. Compare current governance/versioning/ownership/audit capability against this Phase 34 TO-BE spec.
2. Classify each capability with required labels.
3. Implement GovernedObjectType and GovernancePolicy.
4. Implement ObjectOwnership.
5. Implement ObjectAccessGrant where policy allows.
6. Implement GovernanceVersionRecord and GovernanceSnapshot.
7. Implement diff and restore/revert for supported object types.
8. Implement ObjectLock / finalize / unfinalize.
9. Integrate with Baseline/ChangeRequest guard.
10. Integrate with Quote, Document, Requirement, Deliverable, Release, RAID, Decision, MeetingMinutes, Custom Config, External Portal where available.
11. Add sensitive access tracking and client-visible change tracking.
12. Add IAM permissions, events, reports, notifications, audit/outbox, idempotency, and tests.
13. Run mvn compile and mvn test.
14. Create docs/phase-complete/PHASE_34_GOVERNANCE_VERSIONING_PERMISSION_AUDIT_TO_BE_COMPLETE.md.

Do not implement or claim approval workflow, BPMN engine, arbitrary scripting, delegation, escalation, approval SLA, or autonomous AI approval.
```

---

# 27. Quick tracking matrix

| Capability | Current backend | Phase 34 action | Later phase |
|---|---|---|---|
| Approval engine | Old direction | Remove / do not implement | Optional future backlog only |
| GovernedObjectType | Missing/unknown | Must implement | — |
| GovernancePolicy | Missing/unknown | Must implement | Compliance Phase 38 |
| ObjectOwnership | Missing/unknown | Must implement | — |
| ObjectAccessGrant | Missing/unknown | Must implement where useful | External portal Phase 30/38 |
| GovernanceVersionRecord | Missing/unknown | Must implement | — |
| GovernanceSnapshot | Missing/unknown | Must implement where object lacks native version | — |
| Diff summary | Missing/unknown | Must implement supported types | AI summary Phase 41 |
| Restore/revert | Missing/unknown | Must implement supported types | — |
| ObjectLock/finalize | Missing/unknown | Must implement | — |
| Baseline guard | Missing/partial | Must harden | — |
| Sensitive access tracking | Missing/partial | Must implement | Compliance Phase 38 |
| Client-visible change tracking | Missing/partial | Must implement | Portal Phase 30 |
| Governance reports | Missing/unknown | Must implement | Advanced audit Phase 38 |
| WorkflowDefinition | Not needed | Do not implement | Optional future backlog |
| ApprovalRequest | Not needed | Do not implement | Optional future backlog |
| ApprovalDecision | Not needed | Do not implement | Optional future backlog |
| Delegation/escalation | Not needed | Do not implement | Optional future backlog |
| Approval SLA | Not needed | Do not implement | Optional future backlog |

---

# 28. Final principle

Phase 34 is not complete when "approval exists."

Phase 34 is complete when Scopery can govern changes without slowing users down:

```text
permission
+ owner control
+ version history
+ snapshots
+ diff
+ lock/finalize
+ restore
+ baseline guard
+ audit
= lightweight governance
```

No approval queue.

No workflow engine.

No unnecessary waiting.

If a user has permission, they can act.

If the action matters, Scopery keeps the version, audit, and restore path.
