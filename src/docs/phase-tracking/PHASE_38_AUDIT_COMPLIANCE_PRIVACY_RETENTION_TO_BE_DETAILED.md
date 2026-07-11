# PHASE 38 — TO-BE Audit, Compliance Readiness, Privacy Controls, Retention, Sensitive Access Review & Data Governance

> Project: Scopery Backend  
> Phase: 38  
> Document type: TO-BE implementation-grade specification  
> Status: Planning / Audit, privacy, retention and compliance-readiness layer  
> Roadmap group: Platform Trust, Governance & Compliance Readiness  
> Depends on: Phase 00 — Master Backend Roadmap, Phase 01 — API Path / Security Baseline, Phase 02 — IAM Core, Phase 03 — Workspace / Organization / Team, Phase 04 — Platform Audit / Outbox / Idempotency, Phase 05 — Event Registry, Phase 06 — Notification Engine, Phase 10 — Project Authorization, Phase 20 — Project Events / Notifications, Phase 22 — Reporting / Dashboard / Export, Phase 23 — Core Hardening, Phase 27 — Document Hub / Generation, Phase 29 — External Party / Client CRM / Stakeholder, Phase 30 — Customer / External Portal, Phase 32 — Search / Productivity, Phase 33 — Custom Fields / Forms / Configuration, Phase 34 — Governance / Versioning / Permission / Audit, Phase 35 — Advanced Notifications, Phase 36 — Revenue & Profitability Visibility, Phase 37 — Resource / Capacity / Effort / Utilization  
> API base: `/api`  
> Primary module: `modules/compliance`, `modules/privacy`, `modules/audit`, or `modules/platform/trust` depending on repository architecture  
> Related modules: `iam`, `workspace`, `project`, `document`, `clientportal`, `externalparty`, `governance`, `notification`, `reporting`, `search`, `configuration`, `profitability`, `resource`, `audit`, `eventregistry`, future `integration`, `security-monitoring`, `enterprise-admin`  
> Important rule: Phase 38 makes Scopery audit-ready and privacy-aware. It provides controls, evidence, retention, sensitive access review, privacy requests, and data governance foundations. It should avoid claiming formal legal compliance certification unless Scopery has actually passed the relevant external audit/certification process.

---

# 0. Purpose

Phase 38 creates Scopery's trust and compliance-readiness layer.

By now, Scopery has many important objects:

```text
projects
tasks
requirements
documents
quotes
change requests
deliverables
acceptance records
client portal data
governance versions
notifications
revenue/profitability data
resource/capacity data
custom fields
external contacts
audit events
```

The system needs to answer:

```text
Who accessed sensitive data?
Who changed permissions?
Who exported reports?
Who downloaded restricted documents?
Which data is client-visible?
Which data should be retained?
Which data should be deleted/anonymized?
Which objects are under legal hold?
Which personal data belongs to a contact/user?
Can we export a user's data?
Can we record a privacy request?
Can admins review suspicious access?
Can reports avoid leaking sensitive data?
```

Phase 38 provides the platform foundation for these controls.

---

# 1. Product intention

Scopery should be trusted by teams working with client projects.

Project systems often contain:

```text
client contact data
commercial/profitability values
resource/cost assumptions
documents
requirements
meeting notes
comments
external portal activity
sensitive custom fields
audit records
```

Phase 38 helps Scopery become safer by default:

```text
sensitive fields are classified
access to sensitive records is logged
exports are tracked
retention rules are explicit
privacy requests are managed
deletion/anonymization is controlled
legal holds stop unsafe deletion
admins can review access risk
```

This phase is not about adding heavy bureaucracy.

It is about making the platform explainable, auditable, and safer.

---

# 2. Core principle

```text
If data is sensitive, access must be intentional, traceable, and controlled.
```

Phase 38 should enforce:

```text
classify
protect
log
review
retain
export safely
delete/anonymize safely
prove what happened
```

Important distinction:

```text
Audit tells what happened.
Privacy controls manage personal data.
Retention controls how long data stays.
Legal hold prevents deletion when needed.
Compliance evidence proves system behavior.
```

---

# 3. Source inputs

Before coding Phase 38, the agent must read:

```text
1. Current backend codebase
2. Phase 01 Security baseline implementation
3. Phase 02 IAM implementation
4. Phase 03 Workspace/Organization/Team implementation
5. Phase 04 Platform Audit / Outbox / Idempotency implementation
6. Phase 05 Event Registry implementation
7. Phase 06 Notification implementation
8. Phase 10 Project Authorization implementation
9. Phase 22 Reporting / Dashboard / Export implementation
10. Phase 23 Core Hardening completion file
11. Phase 27 Document Hub implementation
12. Phase 29 External Party / Contact implementation
13. Phase 30 External Portal implementation
14. Phase 32 Search / Productivity implementation
15. Phase 33 Custom Fields / Configuration implementation
16. Phase 34 Governance / Versioning / Permission / Audit implementation
17. Phase 35 Advanced Notifications implementation
18. Phase 36 Revenue & Profitability implementation
19. Phase 37 Resource / Capacity implementation
20. Existing audit event tables and APIs
21. Existing document access logs
22. Existing report/export logs
23. Existing personal data models
24. Existing permission and role seeders
25. Existing EventDefinition seeders
```

The agent must inspect actual implementation and classify gaps.

---

# 4. Current expected backend state

Before Phase 38, Scopery likely has:

```text
basic audit events
activity log
outbox events
notifications
permission checks
governance audit
document access maybe
report/export maybe
external contact data
portal users
custom fields with possible sensitive markers
profitability/resource sensitive values
```

Likely missing or partial:

```text
DataClassificationPolicy
SensitiveFieldRegistry
SensitiveAccessLog
ExportAuditLog
PrivacyRequest
DataSubjectIndex
RetentionPolicy
RetentionJob
LegalHold
AnonymizationPlan
DeletionReview
ConsentRecord
ComplianceEvidence
AccessReviewCampaign
PermissionReviewFinding
SecurityAuditDashboard
PrivacySafeSearchIndex policy
```

Phase 38 creates the missing trust layer.

---

# 5. Target statement

Phase 38 must deliver:

```text
1. Data classification model.
2. Sensitive field registry and sensitive object registry.
3. Sensitive access logging.
4. Export/download audit hardening.
5. Privacy request management.
6. Data subject index for users/external contacts.
7. Retention policy and retention job foundation.
8. Legal hold / deletion hold foundation.
9. Anonymization and safe deletion planning.
10. Consent/communication preference foundation where applicable.
11. Access review campaign and permission review findings.
12. Compliance evidence records.
13. Privacy-safe search/report/export rules.
14. Governance, notification, reporting, and test integration.
```

---

# 6. Boundary decisions

## 6.1 Compliance readiness, not certification

Phase 38 creates evidence and controls.

It should not claim:

```text
SOC 2 certified
ISO 27001 certified
GDPR fully compliant
HIPAA compliant
legal compliance guaranteed
```

Actual compliance depends on company process, legal review, security operations, and external audits.

## 6.2 Privacy request is workflow-light

A privacy request is a controlled record.

It does not require heavy approval workflow.

Actions are governed by:

```text
permission
review status
legal hold check
retention policy
audit
```

## 6.3 Deletion should be safe

Deletion/anonymization must consider:

```text
audit integrity
project history
legal hold
document history
client portal records
financial/profitability snapshots
cross-reference integrity
```

## 6.4 Audit logs must not leak secrets

Audit details should record what happened without storing raw secrets unnecessarily.

## 6.5 Retention policy is not automatic destruction by default

Retention jobs should support preview/dry-run before destructive action.

---

# 7. Classification labels

Use these labels in completion file:

| Label | Meaning |
|---|---|
| `CURRENTLY_IMPLEMENTED` | Existing backend already supports it with tests. |
| `PARTIALLY_IMPLEMENTED` | Some support exists but gaps remain. |
| `MUST_IMPLEMENT_IN_PHASE_38` | Required now. |
| `MUST_HARDEN_IN_PHASE_38` | Existing behavior must be hardened now. |
| `SEED_ONLY_IN_PHASE_38` | Seed definitions/events/permissions/default configs only. |
| `DEFERRED_TO_PHASE_XX` | Later phase returns. |
| `DEFERRED_TO_POST_23_BACKLOG` | Later backlog item. |
| `NOT_IN_SCOPE_FOR_PHASE_38` | Explicitly not in this phase. |

---

# 8. Required capabilities

---

## 8.1 CLASS-001 — DataClassificationPolicy

Classification: `MUST_IMPLEMENT_IN_PHASE_38`

Purpose:

```text
Define how data is classified inside a workspace.
```

Classification levels:

```text
PUBLIC
INTERNAL
CONFIDENTIAL
RESTRICTED
SENSITIVE_PERSONAL
SENSITIVE_FINANCIAL
SENSITIVE_SECURITY
```

Rules:

```text
1. Workspace has default classification policy.
2. Object types can have default classification.
3. Fields can have classification override.
4. Custom fields can be marked sensitive.
5. Classification affects access, masking, audit, export, and search indexing.
```

---

## 8.2 CLASS-002 — SensitiveObjectRegistry

Classification: `MUST_IMPLEMENT_IN_PHASE_38`

Purpose:

```text
Register object types that contain sensitive data.
```

Examples:

```text
DOCUMENT
DOCUMENT_VERSION
EXTERNAL_CONTACT
PORTAL_ACCOUNT
CUSTOM_FIELD_VALUE
REVENUE_SOURCE
PROFITABILITY_SUMMARY
RESOURCE_COST_INPUT
RATE_CARD
AUDIT_EVENT
PERMISSION_GRANT
AI_CONTEXT_SNAPSHOT
```

Rules:

```text
1. Registry is seeded and workspace-configurable where safe.
2. Registry drives logging and masking behavior.
3. Registry does not grant access.
4. Sensitive object read/export/download should be auditable.
```

---

## 8.3 CLASS-003 — SensitiveFieldRegistry

Classification: `MUST_IMPLEMENT_IN_PHASE_38`

Purpose:

```text
Identify sensitive fields on domain objects.
```

Examples:

```text
external contact email/phone
tax identifier if present
address
document restricted metadata
internal cost
profit margin
resource rate
custom sensitive field value
token/secret fields
AI prompt/context fields
```

Rules:

```text
1. Sensitive field registry supports static and custom fields.
2. Sensitive fields can be masked in DTOs.
3. Sensitive fields can be excluded from search index.
4. Sensitive field access can be logged.
5. Sensitive field changes can require reason.
```

---

## 8.4 ACCESS-001 — SensitiveAccessLog

Classification: `MUST_IMPLEMENT_IN_PHASE_38`

Purpose:

```text
Record access to sensitive objects/fields.
```

Access actions:

```text
VIEW
LIST
SEARCH_RESULT
DOWNLOAD
EXPORT
PRINT
COPY_LINK
API_READ
PORTAL_READ
AI_CONTEXT_USED
```

Rules:

```text
1. Log actor, target, action, timestamp, trace id.
2. Log classification level.
3. Do not store raw sensitive values.
4. Bulk list/search should log at safe granularity.
5. Access logs are themselves sensitive.
6. Access logs can feed review dashboard and alerts.
```

---

## 8.5 EXPORT-001 — ExportAuditLog

Classification: `MUST_IMPLEMENT_IN_PHASE_38`

Purpose:

```text
Track report/data/document exports.
```

Export types:

```text
REPORT_EXPORT
DOCUMENT_DOWNLOAD
BULK_DATA_EXPORT
AUDIT_EXPORT
PORTAL_DOWNLOAD
SEARCH_EXPORT
CSV_EXPORT
PDF_EXPORT
JSON_EXPORT
```

Rules:

```text
1. Export actor, filters, object type, row count, classification recorded.
2. Export file path/token should be protected.
3. Sensitive export requires stronger permission.
4. Export may require reason if policy says.
5. Export audit retained longer than normal activity logs.
```

---

## 8.6 PRIV-001 — DataSubjectIndex

Classification: `MUST_IMPLEMENT_IN_PHASE_38`

Purpose:

```text
Map personal data across Scopery to a person-like subject.
```

Subject types:

```text
INTERNAL_USER
EXTERNAL_CONTACT
PORTAL_ACCOUNT
CLIENT_CONTACT
VENDOR_CONTACT
UNKNOWN_PERSON_REFERENCE
```

Possible linked records:

```text
user account
workspace member
external contact
portal account
comment author
meeting participant
document metadata
custom field value
audit actor reference
notification recipient
```

Rules:

```text
1. Index should be rebuildable.
2. Index does not expose data without permission.
3. Index helps privacy export/delete/anonymization planning.
4. Index must not break project history.
```

---

## 8.7 PRIV-002 — PrivacyRequest

Classification: `MUST_IMPLEMENT_IN_PHASE_38`

Purpose:

```text
Track data subject requests.
```

Request types:

```text
ACCESS_EXPORT
CORRECTION
DELETION
ANONYMIZATION
RESTRICTION
PORTABILITY
CONSENT_WITHDRAWAL
CONTACT_SUPPRESSION
```

Status:

```text
SUBMITTED
TRIAGED
IN_REVIEW
ACTION_PLANNED
COMPLETED
REJECTED
CANCELLED
```

Rules:

```text
1. Request belongs to workspace.
2. Subject identity can be internal or external.
3. Request requires assigned owner.
4. Action planning checks retention and legal hold.
5. Completion creates audit/evidence.
6. Request does not automatically delete data without controlled action.
```

---

## 8.8 PRIV-003 — PrivacyExportPackage

Classification: `MUST_IMPLEMENT_IN_PHASE_38`

Purpose:

```text
Generate safe export package for a data subject.
```

Rules:

```text
1. Export package created from DataSubjectIndex.
2. Export includes allowed personal data only.
3. Internal confidential data can be excluded or redacted.
4. Package generation audited.
5. Package access expires.
```

---

## 8.9 PRIV-004 — AnonymizationPlan

Classification: `MUST_IMPLEMENT_IN_PHASE_38`

Purpose:

```text
Plan how personal data should be anonymized safely.
```

Anonymization actions:

```text
MASK_NAME
MASK_EMAIL
MASK_PHONE
REMOVE_ADDRESS
REPLACE_WITH_PSEUDONYM
REMOVE_FREE_TEXT_REFERENCE
DISCONNECT_PORTAL_ACCOUNT
SUPPRESS_CONTACT
```

Rules:

```text
1. Plan generated before execution.
2. Plan supports dry-run.
3. Execution checks legal hold.
4. Execution preserves required project/audit history.
5. Execution audited.
```

---

## 8.10 RET-001 — RetentionPolicy

Classification: `MUST_IMPLEMENT_IN_PHASE_38`

Purpose:

```text
Define how long data should be kept.
```

Policy dimensions:

```text
object type
classification
workspace
project status
client status
document category
audit category
retention period
action after period
```

Retention actions:

```text
KEEP
ARCHIVE
ANONYMIZE
DELETE_SOFT
DELETE_HARD_RESTRICTED
REVIEW_REQUIRED
```

Rules:

```text
1. Default retention policy seeded.
2. Destructive actions require explicit policy and dry-run.
3. Legal hold overrides retention action.
4. Audit/security logs may have stricter retention.
5. Retention job must be idempotent.
```

---

## 8.11 RET-002 — RetentionJob

Classification: `MUST_IMPLEMENT_IN_PHASE_38`

Purpose:

```text
Evaluate retention policies and create actionable retention results.
```

Job modes:

```text
DRY_RUN
EXECUTE_ARCHIVE
EXECUTE_ANONYMIZE
EXECUTE_DELETE
```

Rules:

```text
1. Dry-run available before execution.
2. Job records candidates and actions.
3. Job skips legal hold records.
4. Job failures are recorded safely.
5. Job output is audit-sensitive.
```

---

## 8.12 HOLD-001 — LegalHold / DeletionHold

Classification: `MUST_IMPLEMENT_IN_PHASE_38`

Purpose:

```text
Prevent deletion/anonymization of records under hold.
```

Hold types:

```text
LEGAL
SECURITY_INVESTIGATION
CLIENT_DISPUTE
PROJECT_DISPUTE
MANUAL_ADMIN_HOLD
```

Rules:

```text
1. Hold can target workspace/project/object/data subject.
2. Active hold blocks destructive retention actions.
3. Hold creation/release audited.
4. Hold reason required.
5. Hold does not grant access.
```

---

## 8.13 CONSENT-001 — ConsentRecord / ContactSuppression

Classification: `MUST_IMPLEMENT_IN_PHASE_38`

Purpose:

```text
Track basic communication consent and contact suppression.
```

Consent types:

```text
PORTAL_NOTIFICATION
EMAIL_NOTIFICATION
PRODUCT_UPDATE
PROJECT_COMMUNICATION
DATA_PROCESSING_ACK
```

Rules:

```text
1. Consent belongs to data subject/contact/portal account.
2. Consent source and timestamp recorded.
3. Withdrawn consent suppresses optional communication.
4. Project-critical communication may follow workspace policy.
5. Consent records audited.
```

---

## 8.14 REVIEW-001 — AccessReviewCampaign

Classification: `MUST_IMPLEMENT_IN_PHASE_38`

Purpose:

```text
Allow admins to review access grants and sensitive permissions.
```

Review targets:

```text
workspace roles
project roles
object grants
portal grants
sensitive permissions
external access
document access
custom field access
```

Rules:

```text
1. Campaign has scope and due date.
2. Findings created for risky/expired/unused grants.
3. Review result can recommend revoke/keep.
4. Actual revoke uses IAM/governance APIs with permission.
5. Review action audited.
```

---

## 8.15 REVIEW-002 — PermissionReviewFinding

Classification: `MUST_IMPLEMENT_IN_PHASE_38`

Purpose:

```text
Record access risks found during review.
```

Finding types:

```text
EXPIRED_ACCESS
UNUSED_ACCESS
EXTERNAL_ACCESS_RISK
SENSITIVE_PERMISSION_RISK
ORPHANED_OWNER
PORTAL_GRANT_RISK
BROAD_ADMIN_ROLE
OBJECT_GRANT_STALE
```

Status:

```text
OPEN
ACKNOWLEDGED
RESOLVED
DISMISSED
ARCHIVED
```

Rules:

```text
1. Finding does not automatically revoke access.
2. Finding links to grant/role/object where possible.
3. Resolution audited.
4. Finding can trigger notification.
```

---

## 8.16 EVID-001 — ComplianceEvidenceRecord

Classification: `MUST_IMPLEMENT_IN_PHASE_38`

Purpose:

```text
Store evidence that a compliance/security/privacy control exists or ran.
```

Evidence types:

```text
ACCESS_REVIEW_COMPLETED
RETENTION_JOB_RUN
PRIVACY_REQUEST_COMPLETED
EXPORT_AUDIT_REVIEWED
SENSITIVE_ACCESS_REVIEWED
POLICY_UPDATED
LEGAL_HOLD_CREATED
LEGAL_HOLD_RELEASED
SECURITY_REVIEW_COMPLETED
```

Rules:

```text
1. Evidence record immutable after finalization.
2. Evidence can link to reports/files/documents.
3. Evidence has owner and date.
4. Evidence does not claim certification by itself.
```

---

## 8.17 SAFE-001 — Privacy-safe search/report/export

Classification: `MUST_IMPLEMENT_IN_PHASE_38`

Purpose:

```text
Prevent sensitive data leakage through search/report/export.
```

Rules:

```text
1. Sensitive fields excluded/masked in search snippets.
2. Report columns enforce field-level permission.
3. Export requires export permission.
4. Export logs classification and count.
5. Portal search never returns internal sensitive data.
6. Custom sensitive fields follow same rules.
```

---

## 8.18 DASH-001 — Trust dashboard

Classification: `MUST_IMPLEMENT_IN_PHASE_38`

Purpose:

```text
Provide operational dashboard for admins.
```

Dashboard metrics:

```text
sensitiveAccessCount
sensitiveExportCount
openPrivacyRequests
retentionCandidates
activeLegalHolds
openAccessReviewFindings
externalAccessCount
portalGrantCount
staleObjectGrantCount
sensitiveCustomFieldCount
auditExportCount
```

Rules:

```text
1. Dashboard requires trust/compliance permission.
2. Values respect workspace boundaries.
3. Dashboard does not expose raw sensitive values.
```

---

## 8.19 NOTIF-001 — Trust notifications

Classification: `MUST_IMPLEMENT_IN_PHASE_38` if Phase 35 exists.

Notification candidates:

```text
privacy request submitted
privacy request due soon
sensitive export performed
unusual sensitive access detected
retention job completed
retention job failed
legal hold created/released
access review campaign due
permission review finding created
consent withdrawn
```

Rules:

```text
1. Sensitive details masked.
2. Recipients are privacy owner, workspace admin, security/admin roles.
3. External notifications are carefully limited.
```

---

## 8.20 AI-001 — AI-assisted compliance summaries

Classification: `SEED_ONLY_IN_PHASE_38` or `MUST_IMPLEMENT_IN_PHASE_38` if Phase 21 exists.

AI can help with:

```text
summarize access review findings
summarize sensitive access activity
draft privacy request response summary
explain retention candidates
summarize export audit
detect suspicious access patterns
```

Rules:

```text
1. AI sees only data actor can access.
2. AI cannot execute deletion/anonymization automatically.
3. AI cannot release legal hold.
4. AI cannot claim legal compliance.
5. AI output is draft/explanation only.
```

---

# 9. Entity model TO-BE

---

## 9.1 DataClassificationPolicy — `trust_data_classification_policy`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
policy_code VARCHAR(150) NOT NULL
name VARCHAR(255) NOT NULL
description TEXT NULL
default_classification VARCHAR(50) NOT NULL
classification_rules_json JSONB NULL
enabled BOOLEAN NOT NULL DEFAULT true
created_at / created_by
updated_at / updated_by
version INT
```

Constraint:

```text
unique workspace_id + policy_code
```

---

## 9.2 SensitiveObjectRegistry — `trust_sensitive_object_registry`

Fields:

```text
id UUID PK
workspace_id UUID NULL
object_type_code VARCHAR(100) NOT NULL
classification VARCHAR(50) NOT NULL
access_logging_required BOOLEAN NOT NULL DEFAULT true
export_reason_required BOOLEAN NOT NULL DEFAULT false
search_index_allowed BOOLEAN NOT NULL DEFAULT false
enabled BOOLEAN NOT NULL DEFAULT true
created_at / created_by
updated_at / updated_by
version INT
```

Constraint:

```text
unique workspace_id + object_type_code
```

Workspace null can mean system default.

---

## 9.3 SensitiveFieldRegistry — `trust_sensitive_field_registry`

Fields:

```text
id UUID PK
workspace_id UUID NULL
object_type_code VARCHAR(100) NOT NULL
field_path VARCHAR(500) NOT NULL
classification VARCHAR(50) NOT NULL
masking_strategy VARCHAR(100) NOT NULL
access_logging_required BOOLEAN NOT NULL DEFAULT true
search_index_allowed BOOLEAN NOT NULL DEFAULT false
export_allowed BOOLEAN NOT NULL DEFAULT false
enabled BOOLEAN NOT NULL DEFAULT true
created_at / created_by
updated_at / updated_by
version INT
```

Masking strategies:

```text
FULL_MASK
PARTIAL_EMAIL
PARTIAL_PHONE
HASH_ONLY
REDACT
ROLE_BASED
```

---

## 9.4 SensitiveAccessLog — `trust_sensitive_access_log`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
project_id UUID NULL
actor_principal_type VARCHAR(50) NOT NULL
actor_user_id UUID NULL
actor_external_portal_account_id UUID NULL
target_object_type VARCHAR(100) NOT NULL
target_object_id UUID NULL
field_path VARCHAR(500) NULL
classification VARCHAR(50) NOT NULL
access_action VARCHAR(50) NOT NULL
access_channel VARCHAR(50) NULL
reason TEXT NULL
request_path VARCHAR(500) NULL
ip_address_hash VARCHAR(255) NULL
user_agent_hash VARCHAR(255) NULL
occurred_at TIMESTAMP NOT NULL
trace_id VARCHAR(100) NULL
version INT
```

---

## 9.5 ExportAuditLog — `trust_export_audit_log`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
project_id UUID NULL
actor_user_id UUID NULL
actor_external_portal_account_id UUID NULL
export_type VARCHAR(100) NOT NULL
target_object_type VARCHAR(100) NULL
classification VARCHAR(50) NOT NULL
filter_summary_json JSONB NULL
row_count BIGINT NULL
file_reference VARCHAR(500) NULL
reason TEXT NULL
status VARCHAR(50) NOT NULL
created_at TIMESTAMP NOT NULL
completed_at TIMESTAMP NULL
failure_code VARCHAR(150) NULL
failure_message TEXT NULL
trace_id VARCHAR(100) NULL
version INT
```

Status:

```text
REQUESTED
COMPLETED
FAILED
CANCELLED
EXPIRED
```

---

## 9.6 DataSubjectIndex — `trust_data_subject_index`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
subject_type VARCHAR(50) NOT NULL
subject_id UUID NULL
subject_key_hash VARCHAR(255) NULL
display_name_snapshot VARCHAR(255) NULL
linked_user_id UUID NULL
linked_external_contact_id UUID NULL
linked_portal_account_id UUID NULL
record_count BIGINT NOT NULL DEFAULT 0
last_rebuilt_at TIMESTAMP NULL
status VARCHAR(50) NOT NULL
created_at / created_by
updated_at / updated_by
version INT
```

Status:

```text
ACTIVE
MERGED
ARCHIVED
```

---

## 9.7 DataSubjectRecordRef — `trust_data_subject_record_ref`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
data_subject_index_id UUID NOT NULL
object_type_code VARCHAR(100) NOT NULL
object_id UUID NOT NULL
field_path VARCHAR(500) NULL
classification VARCHAR(50) NOT NULL
relationship_type VARCHAR(100) NULL
created_at TIMESTAMP NOT NULL
version INT
```

Constraint:

```text
unique data_subject_index_id + object_type_code + object_id + field_path
```

---

## 9.8 PrivacyRequest — `trust_privacy_request`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
request_code VARCHAR(150) NULL
request_type VARCHAR(50) NOT NULL
data_subject_index_id UUID NULL
subject_type VARCHAR(50) NULL
subject_reference VARCHAR(255) NULL
submitted_by_user_id UUID NULL
submitted_by_external_portal_account_id UUID NULL
submitted_channel VARCHAR(50) NOT NULL
status VARCHAR(50) NOT NULL
request_description TEXT NULL
assigned_owner_user_id UUID NULL
due_date DATE NULL
resolution_summary TEXT NULL
rejection_reason TEXT NULL
created_at / created_by
updated_at / updated_by
completed_at / completed_by
trace_id VARCHAR(100) NULL
version INT
```

Constraint:

```text
unique workspace_id + request_code where request_code not null
```

---

## 9.9 PrivacyExportPackage — `trust_privacy_export_package`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
privacy_request_id UUID NOT NULL
data_subject_index_id UUID NOT NULL
status VARCHAR(50) NOT NULL
package_manifest_json JSONB NULL
file_reference VARCHAR(500) NULL
expires_at TIMESTAMP NULL
created_at / created_by
completed_at / completed_by
failure_code VARCHAR(150) NULL
failure_message TEXT NULL
trace_id VARCHAR(100) NULL
version INT
```

Status:

```text
REQUESTED
GENERATING
COMPLETED
FAILED
EXPIRED
CANCELLED
```

---

## 9.10 AnonymizationPlan — `trust_anonymization_plan`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
privacy_request_id UUID NULL
data_subject_index_id UUID NOT NULL
status VARCHAR(50) NOT NULL
plan_json JSONB NOT NULL
dry_run_result_json JSONB NULL
execution_result_json JSONB NULL
legal_hold_blocked BOOLEAN NOT NULL DEFAULT false
reason TEXT NOT NULL
created_at / created_by
executed_at / executed_by
cancelled_at / cancelled_by
trace_id VARCHAR(100) NULL
version INT
```

Status:

```text
DRAFT
DRY_RUN_COMPLETED
READY
EXECUTED
BLOCKED
CANCELLED
FAILED
```

---

## 9.11 RetentionPolicy — `trust_retention_policy`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
policy_code VARCHAR(150) NOT NULL
name VARCHAR(255) NOT NULL
object_type_code VARCHAR(100) NOT NULL
classification VARCHAR(50) NULL
retention_period_days INT NOT NULL
retention_action VARCHAR(50) NOT NULL
review_required BOOLEAN NOT NULL DEFAULT true
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

## 9.12 RetentionJob — `trust_retention_job`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
retention_policy_id UUID NULL
job_mode VARCHAR(50) NOT NULL
status VARCHAR(50) NOT NULL
candidate_count BIGINT NOT NULL DEFAULT 0
actioned_count BIGINT NOT NULL DEFAULT 0
skipped_legal_hold_count BIGINT NOT NULL DEFAULT 0
failed_count BIGINT NOT NULL DEFAULT 0
result_summary_json JSONB NULL
started_at TIMESTAMP NULL
completed_at TIMESTAMP NULL
created_at / created_by
failure_code VARCHAR(150) NULL
failure_message TEXT NULL
trace_id VARCHAR(100) NULL
version INT
```

Status:

```text
CREATED
RUNNING
COMPLETED
COMPLETED_WITH_ERRORS
FAILED
CANCELLED
```

---

## 9.13 RetentionCandidate — `trust_retention_candidate`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
retention_job_id UUID NOT NULL
object_type_code VARCHAR(100) NOT NULL
object_id UUID NOT NULL
classification VARCHAR(50) NULL
candidate_reason TEXT NULL
proposed_action VARCHAR(50) NOT NULL
status VARCHAR(50) NOT NULL
legal_hold_blocked BOOLEAN NOT NULL DEFAULT false
result_message TEXT NULL
created_at TIMESTAMP NOT NULL
version INT
```

---

## 9.14 LegalHold — `trust_legal_hold`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
hold_code VARCHAR(150) NULL
hold_type VARCHAR(50) NOT NULL
scope_type VARCHAR(50) NOT NULL
scope_id UUID NULL
reason TEXT NOT NULL
status VARCHAR(50) NOT NULL
created_at / created_by
released_at / released_by
release_reason TEXT NULL
trace_id VARCHAR(100) NULL
version INT
```

Status:

```text
ACTIVE
RELEASED
ARCHIVED
```

---

## 9.15 ConsentRecord — `trust_consent_record`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
data_subject_index_id UUID NULL
external_contact_id UUID NULL
portal_account_id UUID NULL
consent_type VARCHAR(100) NOT NULL
status VARCHAR(50) NOT NULL
source VARCHAR(100) NULL
source_reference VARCHAR(255) NULL
given_at TIMESTAMP NULL
withdrawn_at TIMESTAMP NULL
created_at / created_by
updated_at / updated_by
trace_id VARCHAR(100) NULL
version INT
```

Status:

```text
GIVEN
WITHDRAWN
UNKNOWN
NOT_REQUIRED
```

---

## 9.16 ContactSuppression — `trust_contact_suppression`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
data_subject_index_id UUID NULL
external_contact_id UUID NULL
portal_account_id UUID NULL
suppression_type VARCHAR(100) NOT NULL
reason TEXT NULL
status VARCHAR(50) NOT NULL
created_at / created_by
released_at / released_by
release_reason TEXT NULL
trace_id VARCHAR(100) NULL
version INT
```

Status:

```text
ACTIVE
RELEASED
ARCHIVED
```

---

## 9.17 AccessReviewCampaign — `trust_access_review_campaign`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
campaign_code VARCHAR(150) NULL
name VARCHAR(255) NOT NULL
scope_json JSONB NOT NULL
status VARCHAR(50) NOT NULL
owner_user_id UUID NULL
due_date DATE NULL
started_at TIMESTAMP NULL
completed_at TIMESTAMP NULL
created_at / created_by
updated_at / updated_by
trace_id VARCHAR(100) NULL
version INT
```

Status:

```text
DRAFT
ACTIVE
COMPLETED
CANCELLED
ARCHIVED
```

---

## 9.18 PermissionReviewFinding — `trust_permission_review_finding`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
campaign_id UUID NULL
finding_type VARCHAR(100) NOT NULL
severity VARCHAR(50) NOT NULL
target_type VARCHAR(100) NULL
target_id UUID NULL
principal_type VARCHAR(50) NULL
principal_id UUID NULL
description TEXT NULL
recommendation TEXT NULL
status VARCHAR(50) NOT NULL
created_at / created_by
acknowledged_at / acknowledged_by
resolved_at / resolved_by
dismissed_at / dismissed_by
trace_id VARCHAR(100) NULL
version INT
```

---

## 9.19 ComplianceEvidenceRecord — `trust_compliance_evidence_record`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
evidence_code VARCHAR(150) NULL
evidence_type VARCHAR(100) NOT NULL
title VARCHAR(255) NOT NULL
description TEXT NULL
source_type VARCHAR(100) NULL
source_id UUID NULL
document_id UUID NULL
document_version_id UUID NULL
status VARCHAR(50) NOT NULL
owner_user_id UUID NULL
evidence_date DATE NOT NULL
finalized_at TIMESTAMP NULL
finalized_by UUID NULL
created_at / created_by
updated_at / updated_by
trace_id VARCHAR(100) NULL
version INT
```

Status:

```text
DRAFT
FINALIZED
SUPERSEDED
ARCHIVED
```

---

# 10. API TO-BE list

All APIs use `/api`.

---

## 10.1 Classification APIs

```text
GET  /api/workspaces/{workspaceId}/trust/classification-policy
PUT  /api/workspaces/{workspaceId}/trust/classification-policy

GET  /api/workspaces/{workspaceId}/trust/sensitive-objects
POST /api/workspaces/{workspaceId}/trust/sensitive-objects
PUT  /api/workspaces/{workspaceId}/trust/sensitive-objects/{registryId}

GET  /api/workspaces/{workspaceId}/trust/sensitive-fields
POST /api/workspaces/{workspaceId}/trust/sensitive-fields
PUT  /api/workspaces/{workspaceId}/trust/sensitive-fields/{registryId}
```

---

## 10.2 Sensitive access / export audit APIs

```text
GET /api/workspaces/{workspaceId}/trust/sensitive-access-logs
GET /api/workspaces/{workspaceId}/trust/sensitive-access-logs/{logId}

GET /api/workspaces/{workspaceId}/trust/export-audit-logs
GET /api/workspaces/{workspaceId}/trust/export-audit-logs/{logId}
POST /api/workspaces/{workspaceId}/trust/export-audit-logs/{logId}/mark-reviewed
```

---

## 10.3 Data subject APIs

```text
POST /api/workspaces/{workspaceId}/trust/data-subjects/rebuild-index
GET  /api/workspaces/{workspaceId}/trust/data-subjects
GET  /api/workspaces/{workspaceId}/trust/data-subjects/{subjectIndexId}
GET  /api/workspaces/{workspaceId}/trust/data-subjects/{subjectIndexId}/records
```

---

## 10.4 Privacy request APIs

```text
POST  /api/workspaces/{workspaceId}/trust/privacy-requests
GET   /api/workspaces/{workspaceId}/trust/privacy-requests
GET   /api/workspaces/{workspaceId}/trust/privacy-requests/{requestId}
PUT   /api/workspaces/{workspaceId}/trust/privacy-requests/{requestId}
POST  /api/workspaces/{workspaceId}/trust/privacy-requests/{requestId}/triage
POST  /api/workspaces/{workspaceId}/trust/privacy-requests/{requestId}/mark-in-review
POST  /api/workspaces/{workspaceId}/trust/privacy-requests/{requestId}/complete
POST  /api/workspaces/{workspaceId}/trust/privacy-requests/{requestId}/reject
POST  /api/workspaces/{workspaceId}/trust/privacy-requests/{requestId}/cancel
```

---

## 10.5 Privacy export APIs

```text
POST /api/workspaces/{workspaceId}/trust/privacy-requests/{requestId}/export-packages
GET  /api/workspaces/{workspaceId}/trust/privacy-export-packages
GET  /api/workspaces/{workspaceId}/trust/privacy-export-packages/{packageId}
POST /api/workspaces/{workspaceId}/trust/privacy-export-packages/{packageId}/expire
```

---

## 10.6 Anonymization APIs

```text
POST /api/workspaces/{workspaceId}/trust/anonymization-plans
GET  /api/workspaces/{workspaceId}/trust/anonymization-plans
GET  /api/workspaces/{workspaceId}/trust/anonymization-plans/{planId}
POST /api/workspaces/{workspaceId}/trust/anonymization-plans/{planId}/dry-run
POST /api/workspaces/{workspaceId}/trust/anonymization-plans/{planId}/execute
POST /api/workspaces/{workspaceId}/trust/anonymization-plans/{planId}/cancel
```

---

## 10.7 Retention APIs

```text
POST  /api/workspaces/{workspaceId}/trust/retention-policies
GET   /api/workspaces/{workspaceId}/trust/retention-policies
GET   /api/workspaces/{workspaceId}/trust/retention-policies/{policyId}
PUT   /api/workspaces/{workspaceId}/trust/retention-policies/{policyId}
PATCH /api/workspaces/{workspaceId}/trust/retention-policies/{policyId}/archive

POST /api/workspaces/{workspaceId}/trust/retention-jobs
GET  /api/workspaces/{workspaceId}/trust/retention-jobs
GET  /api/workspaces/{workspaceId}/trust/retention-jobs/{jobId}
GET  /api/workspaces/{workspaceId}/trust/retention-jobs/{jobId}/candidates
POST /api/workspaces/{workspaceId}/trust/retention-jobs/{jobId}/cancel
```

---

## 10.8 Legal hold APIs

```text
POST  /api/workspaces/{workspaceId}/trust/legal-holds
GET   /api/workspaces/{workspaceId}/trust/legal-holds
GET   /api/workspaces/{workspaceId}/trust/legal-holds/{holdId}
POST  /api/workspaces/{workspaceId}/trust/legal-holds/{holdId}/release
PATCH /api/workspaces/{workspaceId}/trust/legal-holds/{holdId}/archive
```

---

## 10.9 Consent / suppression APIs

```text
POST /api/workspaces/{workspaceId}/trust/consent-records
GET  /api/workspaces/{workspaceId}/trust/consent-records
POST /api/workspaces/{workspaceId}/trust/consent-records/{consentId}/withdraw
POST /api/workspaces/{workspaceId}/trust/consent-records/{consentId}/restore

POST /api/workspaces/{workspaceId}/trust/contact-suppressions
GET  /api/workspaces/{workspaceId}/trust/contact-suppressions
POST /api/workspaces/{workspaceId}/trust/contact-suppressions/{suppressionId}/release
```

---

## 10.10 Access review APIs

```text
POST  /api/workspaces/{workspaceId}/trust/access-review-campaigns
GET   /api/workspaces/{workspaceId}/trust/access-review-campaigns
GET   /api/workspaces/{workspaceId}/trust/access-review-campaigns/{campaignId}
PUT   /api/workspaces/{workspaceId}/trust/access-review-campaigns/{campaignId}
POST  /api/workspaces/{workspaceId}/trust/access-review-campaigns/{campaignId}/start
POST  /api/workspaces/{workspaceId}/trust/access-review-campaigns/{campaignId}/complete
POST  /api/workspaces/{workspaceId}/trust/access-review-campaigns/{campaignId}/cancel

GET  /api/workspaces/{workspaceId}/trust/permission-review-findings
POST /api/workspaces/{workspaceId}/trust/permission-review-findings/{findingId}/acknowledge
POST /api/workspaces/{workspaceId}/trust/permission-review-findings/{findingId}/resolve
POST /api/workspaces/{workspaceId}/trust/permission-review-findings/{findingId}/dismiss
```

---

## 10.11 Compliance evidence APIs

```text
POST /api/workspaces/{workspaceId}/trust/evidence-records
GET  /api/workspaces/{workspaceId}/trust/evidence-records
GET  /api/workspaces/{workspaceId}/trust/evidence-records/{evidenceId}
PUT  /api/workspaces/{workspaceId}/trust/evidence-records/{evidenceId}
POST /api/workspaces/{workspaceId}/trust/evidence-records/{evidenceId}/finalize
POST /api/workspaces/{workspaceId}/trust/evidence-records/{evidenceId}/archive
```

---

## 10.12 Trust dashboard / reports APIs

```text
GET /api/workspaces/{workspaceId}/trust/dashboard
GET /api/workspaces/{workspaceId}/reports/trust/sensitive-access
GET /api/workspaces/{workspaceId}/reports/trust/exports
GET /api/workspaces/{workspaceId}/reports/trust/privacy-requests
GET /api/workspaces/{workspaceId}/reports/trust/retention
GET /api/workspaces/{workspaceId}/reports/trust/legal-holds
GET /api/workspaces/{workspaceId}/reports/trust/access-review
GET /api/workspaces/{workspaceId}/reports/trust/evidence
```

---

# 11. Authorization requirements

Required authorities:

```text
TRUST_DASHBOARD_VIEW

DATA_CLASSIFICATION_VIEW
DATA_CLASSIFICATION_MANAGE
SENSITIVE_OBJECT_VIEW
SENSITIVE_OBJECT_MANAGE
SENSITIVE_FIELD_VIEW
SENSITIVE_FIELD_MANAGE

SENSITIVE_ACCESS_LOG_VIEW
EXPORT_AUDIT_LOG_VIEW
EXPORT_AUDIT_REVIEW

DATA_SUBJECT_VIEW
DATA_SUBJECT_INDEX_REBUILD

PRIVACY_REQUEST_VIEW
PRIVACY_REQUEST_CREATE
PRIVACY_REQUEST_UPDATE
PRIVACY_REQUEST_TRIAGE
PRIVACY_REQUEST_COMPLETE
PRIVACY_REQUEST_REJECT
PRIVACY_EXPORT_CREATE
PRIVACY_EXPORT_VIEW
PRIVACY_EXPORT_EXPIRE

ANONYMIZATION_PLAN_VIEW
ANONYMIZATION_PLAN_CREATE
ANONYMIZATION_PLAN_DRY_RUN
ANONYMIZATION_PLAN_EXECUTE
ANONYMIZATION_PLAN_CANCEL

RETENTION_POLICY_VIEW
RETENTION_POLICY_MANAGE
RETENTION_JOB_VIEW
RETENTION_JOB_CREATE
RETENTION_JOB_CANCEL

LEGAL_HOLD_VIEW
LEGAL_HOLD_CREATE
LEGAL_HOLD_RELEASE
LEGAL_HOLD_ARCHIVE

CONSENT_RECORD_VIEW
CONSENT_RECORD_MANAGE
CONTACT_SUPPRESSION_VIEW
CONTACT_SUPPRESSION_MANAGE

ACCESS_REVIEW_CAMPAIGN_VIEW
ACCESS_REVIEW_CAMPAIGN_MANAGE
PERMISSION_REVIEW_FINDING_VIEW
PERMISSION_REVIEW_FINDING_UPDATE

COMPLIANCE_EVIDENCE_VIEW
COMPLIANCE_EVIDENCE_CREATE
COMPLIANCE_EVIDENCE_UPDATE
COMPLIANCE_EVIDENCE_FINALIZE
COMPLIANCE_EVIDENCE_ARCHIVE

TRUST_REPORT_VIEW
TRUST_REPORT_EXPORT
```

Rules:

```text
1. Trust APIs are workspace-scoped.
2. Access logs and export logs are sensitive.
3. Privacy request actions require trust/privacy permission.
4. Retention execution requires high-trust admin permission.
5. Legal hold release requires strong permission and reason.
6. Sensitive field/object configuration requires admin permission.
7. Portal users cannot access internal trust APIs.
```

---

# 12. Lifecycle rules

## 12.1 PrivacyRequest lifecycle

```text
SUBMITTED → TRIAGED → IN_REVIEW → ACTION_PLANNED → COMPLETED
SUBMITTED/TRIAGED/IN_REVIEW/ACTION_PLANNED → REJECTED
SUBMITTED/TRIAGED/IN_REVIEW → CANCELLED
```

Rules:

```text
1. Completion requires resolution summary.
2. Rejection requires reason.
3. Destructive actions check legal hold.
4. All transitions audited.
```

## 12.2 RetentionJob lifecycle

```text
CREATED → RUNNING → COMPLETED
RUNNING → COMPLETED_WITH_ERRORS
CREATED/RUNNING → CANCELLED
RUNNING → FAILED
```

## 12.3 LegalHold lifecycle

```text
ACTIVE → RELEASED
ACTIVE/RELEASED → ARCHIVED
```

## 12.4 AccessReviewCampaign lifecycle

```text
DRAFT → ACTIVE → COMPLETED
DRAFT/ACTIVE → CANCELLED
Any → ARCHIVED
```

## 12.5 PermissionReviewFinding lifecycle

```text
OPEN → ACKNOWLEDGED → RESOLVED
OPEN → DISMISSED
Any → ARCHIVED
```

## 12.6 ComplianceEvidenceRecord lifecycle

```text
DRAFT → FINALIZED
FINALIZED → SUPERSEDED
Any → ARCHIVED
```

Rules:

```text
Finalized evidence immutable except supersede/archive metadata.
```

---

# 13. Sensitive masking rules

Default masking examples:

```text
email: j***@domain.com
phone: ***1234
tax id: ******
address: REDACTED
cost/rate: HIDDEN
profit/margin: HIDDEN
security token: REDACTED
AI context: REDACTED
```

Rules:

```text
1. Masking happens before DTO leaves service boundary.
2. Search snippets use masked values.
3. Export applies same or stronger masking.
4. Sensitive access log records access to raw value if raw was returned.
5. Masked access may be logged optionally based on policy.
```

---

# 14. Retention rules

Retention evaluation should consider:

```text
object type
classification
created date
updated date
archived date
project status
client status
legal hold
privacy request
workspace policy
```

Example default policy ideas:

```text
audit/security logs: long retention
activity logs: medium retention
notifications: shorter retention
temporary exports: short expiration
privacy export packages: short expiration
archived project data: review after configured years
```

Rules:

```text
1. Retention policy must be explicit.
2. Dry-run should show candidates before execution.
3. Legal hold blocks destructive action.
4. Destructive job requires audit and permission.
5. Retention job must be idempotent.
```

---

# 15. Privacy request rules

Privacy request processing should support:

```text
identify data subject
rebuild subject index
find linked records
check legal hold
prepare export package
plan correction/anonymization/deletion
execute safe action
record evidence
complete/reject request
```

Rules:

```text
1. Subject identity verification is recorded as metadata if needed.
2. Data export excludes internal confidential data unless policy permits.
3. Anonymization avoids breaking project history.
4. Audit logs preserve actor/history but can pseudonymize subject where appropriate.
5. Completion produces evidence record.
```

---

# 16. Access review rules

Access review should inspect:

```text
workspace admin roles
project role assignments
object permission grants
external portal grants
document shares
sensitive field permissions
stale owner grants
expired grants
unused external access
```

Rules:

```text
1. Campaign creates findings.
2. Finding recommends action.
3. Actual permission change uses existing IAM/governance APIs.
4. Findings and resolutions audited.
```

---

# 17. Integration rules

## 17.1 IAM integration

Rules:

```text
1. Trust permissions seeded.
2. Sensitive permissions separated from normal view permissions.
3. Admin role does not automatically mean unrestricted export if policy says separate export permission.
4. Access review can inspect IAM grants.
```

## 17.2 Governance integration

Rules:

```text
1. Object grants reviewed by AccessReviewCampaign.
2. Governance audit feeds trust dashboard.
3. Sensitive object access uses Phase 34 classification.
4. Finalized evidence uses governance version/lock if available.
```

## 17.3 Document integration

Rules:

```text
1. Restricted document download creates ExportAuditLog/SensitiveAccessLog.
2. Document retention policy can archive/delete/anonymize metadata where safe.
3. Legal hold blocks document deletion.
4. Privacy export can include allowed document metadata.
```

## 17.4 Portal integration

Rules:

```text
1. Portal access/download logged.
2. Portal data subject indexed.
3. Portal consent/contact suppression respected.
4. Portal search/export client-safe only.
```

## 17.5 Custom fields integration

Rules:

```text
1. CustomFieldDefinition can mark field sensitive.
2. Sensitive custom field values masked/logged/export-controlled.
3. Search index excludes sensitive custom fields by default.
```

## 17.6 Search integration

Rules:

```text
1. Sensitive fields not included in snippets unless policy and permission.
2. Search results respect classification.
3. Search export audited.
4. Data subject search requires privacy/trust permission.
```

## 17.7 Reporting/export integration

Rules:

```text
1. Report export creates ExportAuditLog.
2. Sensitive report columns require sensitive permission.
3. Trust reports require trust permission.
4. Export files expire according to policy.
```

## 17.8 Profitability/resource integration

Rules:

```text
1. Cost/profit/rate access creates sensitive access log.
2. Profitability/resource exports audited.
3. Rate/cost fields masked unless permission.
4. Access review can include sensitive profitability/resource permissions.
```

## 17.9 Notification integration

Rules:

```text
1. Privacy request due soon notification.
2. Sensitive export alert.
3. Legal hold created/released notification.
4. Access review campaign due notification.
5. Retention job completed/failed notification.
```

---

# 18. Event Registry integration

Recommended source system:

```text
SCOPERY_TRUST
```

Required events:

```text
DATA_CLASSIFICATION_POLICY_UPDATED
SENSITIVE_OBJECT_REGISTERED
SENSITIVE_OBJECT_UPDATED
SENSITIVE_FIELD_REGISTERED
SENSITIVE_FIELD_UPDATED

SENSITIVE_ACCESS_RECORDED
EXPORT_AUDIT_RECORDED
EXPORT_AUDIT_REVIEWED

DATA_SUBJECT_INDEX_REBUILT
DATA_SUBJECT_RECORD_LINKED

PRIVACY_REQUEST_CREATED
PRIVACY_REQUEST_TRIAGED
PRIVACY_REQUEST_IN_REVIEW
PRIVACY_REQUEST_ACTION_PLANNED
PRIVACY_REQUEST_COMPLETED
PRIVACY_REQUEST_REJECTED
PRIVACY_REQUEST_CANCELLED

PRIVACY_EXPORT_PACKAGE_CREATED
PRIVACY_EXPORT_PACKAGE_COMPLETED
PRIVACY_EXPORT_PACKAGE_FAILED
PRIVACY_EXPORT_PACKAGE_EXPIRED

ANONYMIZATION_PLAN_CREATED
ANONYMIZATION_PLAN_DRY_RUN_COMPLETED
ANONYMIZATION_PLAN_EXECUTED
ANONYMIZATION_PLAN_BLOCKED
ANONYMIZATION_PLAN_CANCELLED

RETENTION_POLICY_CREATED
RETENTION_POLICY_UPDATED
RETENTION_POLICY_ARCHIVED
RETENTION_JOB_CREATED
RETENTION_JOB_STARTED
RETENTION_JOB_COMPLETED
RETENTION_JOB_FAILED
RETENTION_CANDIDATE_IDENTIFIED

LEGAL_HOLD_CREATED
LEGAL_HOLD_RELEASED
LEGAL_HOLD_ARCHIVED

CONSENT_RECORD_CREATED
CONSENT_WITHDRAWN
CONTACT_SUPPRESSION_CREATED
CONTACT_SUPPRESSION_RELEASED

ACCESS_REVIEW_CAMPAIGN_CREATED
ACCESS_REVIEW_CAMPAIGN_STARTED
ACCESS_REVIEW_CAMPAIGN_COMPLETED
PERMISSION_REVIEW_FINDING_CREATED
PERMISSION_REVIEW_FINDING_RESOLVED

COMPLIANCE_EVIDENCE_CREATED
COMPLIANCE_EVIDENCE_FINALIZED
COMPLIANCE_EVIDENCE_ARCHIVED

TRUST_REPORT_EXPORTED
```

Standard variables:

```text
actor.userId
workspace.id
project.id
objectType.code
target.id
classification
privacyRequest.id
dataSubject.id
retentionPolicy.id
retentionJob.id
legalHold.id
accessReviewCampaign.id
permissionFinding.id
evidence.id
occurredAt
traceId
```

---

# 19. Audit / activity / outbox

Audit-sensitive actions:

```text
sensitive data viewed
sensitive data exported
classification policy changed
sensitive field registry changed
privacy request status changed
privacy export generated
anonymization executed
retention job executed
legal hold created/released
access review finding resolved
evidence finalized
```

Activity actions:

```text
PRIVACY_REQUEST_CREATED
RETENTION_JOB_COMPLETED
LEGAL_HOLD_CREATED
ACCESS_REVIEW_CAMPAIGN_STARTED
COMPLIANCE_EVIDENCE_FINALIZED
```

Outbox required for:

```text
privacy request created/completed
sensitive export recorded
retention job completed/failed
legal hold created/released
access review due
finding created
```

Idempotency recommended for:

```text
data subject index rebuild
privacy export package generation
anonymization dry-run
anonymization execution
retention job run
access review campaign generation
sensitive access log write if repeated by same request
```

---

# 20. Business rules master

## 20.1 Classification rules

```text
CLASS-001 Classification drives masking/audit/export/search behavior.
CLASS-002 Sensitive field registry supports custom fields.
CLASS-003 Classification does not grant access.
```

## 20.2 Access/export rules

```text
ACCESS-001 Raw sensitive field access logged.
ACCESS-002 Export of sensitive data requires permission.
ACCESS-003 Export audit stores filters/count/classification but not raw secrets.
ACCESS-004 Access logs are sensitive.
```

## 20.3 Privacy rules

```text
PRIV-001 Privacy request has owner and status.
PRIV-002 Data subject index is rebuildable.
PRIV-003 Privacy export package expires.
PRIV-004 Anonymization plan supports dry-run.
PRIV-005 Destructive privacy action checks legal hold.
```

## 20.4 Retention rules

```text
RET-001 Retention policy explicit.
RET-002 Retention job supports dry-run.
RET-003 Legal hold blocks destructive retention.
RET-004 Retention job idempotent.
```

## 20.5 Review/evidence rules

```text
REV-001 Access review creates findings, not direct revokes by default.
REV-002 Evidence record does not claim certification.
REV-003 Finalized evidence immutable.
```

---

# 21. Error catalog

```text
DATA_CLASSIFICATION_POLICY_NOT_FOUND
DATA_CLASSIFICATION_POLICY_INVALID
SENSITIVE_OBJECT_REGISTRY_NOT_FOUND
SENSITIVE_FIELD_REGISTRY_NOT_FOUND
SENSITIVE_FIELD_MASKING_FAILED

SENSITIVE_ACCESS_LOG_NOT_FOUND
SENSITIVE_ACCESS_LOG_ACCESS_DENIED
EXPORT_AUDIT_LOG_NOT_FOUND
EXPORT_AUDIT_ACCESS_DENIED
EXPORT_REASON_REQUIRED

DATA_SUBJECT_INDEX_NOT_FOUND
DATA_SUBJECT_INDEX_REBUILD_FAILED
DATA_SUBJECT_RECORD_NOT_FOUND

PRIVACY_REQUEST_NOT_FOUND
PRIVACY_REQUEST_INVALID_STATUS
PRIVACY_REQUEST_ACCESS_DENIED
PRIVACY_REQUEST_OWNER_REQUIRED
PRIVACY_REQUEST_RESOLUTION_REQUIRED
PRIVACY_REQUEST_REJECTION_REASON_REQUIRED

PRIVACY_EXPORT_PACKAGE_NOT_FOUND
PRIVACY_EXPORT_PACKAGE_GENERATION_FAILED
PRIVACY_EXPORT_PACKAGE_EXPIRED
PRIVACY_EXPORT_ACCESS_DENIED

ANONYMIZATION_PLAN_NOT_FOUND
ANONYMIZATION_PLAN_INVALID_STATUS
ANONYMIZATION_PLAN_DRY_RUN_REQUIRED
ANONYMIZATION_LEGAL_HOLD_BLOCKED
ANONYMIZATION_EXECUTION_FAILED

RETENTION_POLICY_NOT_FOUND
RETENTION_POLICY_INVALID
RETENTION_JOB_NOT_FOUND
RETENTION_JOB_INVALID_MODE
RETENTION_JOB_EXECUTION_FAILED
RETENTION_LEGAL_HOLD_BLOCKED

LEGAL_HOLD_NOT_FOUND
LEGAL_HOLD_INVALID_SCOPE
LEGAL_HOLD_REASON_REQUIRED
LEGAL_HOLD_RELEASE_REASON_REQUIRED

CONSENT_RECORD_NOT_FOUND
CONTACT_SUPPRESSION_NOT_FOUND

ACCESS_REVIEW_CAMPAIGN_NOT_FOUND
ACCESS_REVIEW_CAMPAIGN_INVALID_STATUS
PERMISSION_REVIEW_FINDING_NOT_FOUND
PERMISSION_REVIEW_FINDING_INVALID_STATUS

COMPLIANCE_EVIDENCE_NOT_FOUND
COMPLIANCE_EVIDENCE_INVALID_STATUS
COMPLIANCE_EVIDENCE_FINALIZED_IMMUTABLE

TRUST_REPORT_ACCESS_DENIED
TRUST_EXPORT_ACCESS_DENIED
```

---

# 22. Required tests

## 22.1 Classification tests

```text
createDataClassificationPolicy_success
updateClassificationPolicy_audited
registerSensitiveObject_success
registerSensitiveField_success
sensitiveCustomField_masked
sensitiveFieldExcludedFromSearchIndex
```

## 22.2 Sensitive access/export tests

```text
viewSensitiveField_logsSensitiveAccess
maskedSensitiveField_doesNotLeakRawValue
downloadRestrictedDocument_createsExportAudit
exportSensitiveReport_requiresPermission
exportAuditLog_doesNotStoreRawSecrets
sensitiveAccessLog_requiresTrustPermission
```

## 22.3 Data subject/privacy tests

```text
rebuildDataSubjectIndex_success
dataSubjectIndexLinksExternalContactRecords
createPrivacyRequest_success
privacyRequestLifecycle_success
privacyRequestReject_requiresReason
privacyExportPackage_success
privacyExportPackage_expires
```

## 22.4 Anonymization tests

```text
createAnonymizationPlan_success
anonymizationDryRun_success
executeAnonymization_requiresDryRun
legalHoldBlocksAnonymization
anonymizationPreservesProjectHistory
anonymizationAudited
```

## 22.5 Retention/legal hold tests

```text
createRetentionPolicy_success
retentionDryRun_identifiesCandidates
retentionExecute_archive_success
retentionJobSkipsLegalHold
createLegalHold_success
releaseLegalHold_requiresReason
legalHoldDoesNotGrantAccess
```

## 22.6 Consent/suppression tests

```text
createConsentRecord_success
withdrawConsent_suppressesOptionalNotification
createContactSuppression_success
releaseContactSuppression_success
projectCriticalNotificationPolicyHandled
```

## 22.7 Access review/evidence tests

```text
createAccessReviewCampaign_success
startAccessReviewCampaign_generatesFindings
resolvePermissionReviewFinding_success
findingDoesNotAutoRevokeAccess
createComplianceEvidence_success
finalizeEvidence_immutable
```

## 22.8 Search/report/export tests

```text
searchSnippetMasksSensitiveField
reportColumnHiddenWithoutSensitivePermission
trustDashboardRequiresPermission
trustReportExportAudited
portalSearchNeverReturnsInternalSensitiveData
```

## 22.9 Integration tests

```text
profitabilityCostAccess_loggedSensitive
resourceRateAccess_loggedSensitive
portalDocumentDownload_exportAudited
customSensitiveFieldExport_requiresPermission
objectGrantReviewFinding_created
```

## 22.10 Authorization tests

```text
trustApiWithoutPermission_forbidden
privacyRequestWithoutPermission_forbidden
retentionExecuteWithoutPermission_forbidden
legalHoldReleaseWithoutPermission_forbidden
sensitiveAccessLogCrossWorkspace_forbidden
```

## 22.11 Event tests

```text
trustEventSeeder_firstRun_createsDefinitions
trustEventSeeder_secondRun_noDuplicates
privacyRequestCreated_eventEmitted
retentionJobCompleted_eventEmitted
legalHoldCreated_eventEmitted
sensitiveExportRecorded_eventEmitted
```

---

# 23. Manual verification checklist

Completion file must include:

```text
1. Configure data classification policy.
2. Register sensitive object and field.
3. View sensitive field and confirm access log.
4. Export report with sensitive fields and confirm export audit.
5. Rebuild data subject index.
6. Create privacy request.
7. Generate privacy export package.
8. Create anonymization plan.
9. Run anonymization dry-run.
10. Create legal hold and confirm anonymization/retention blocked.
11. Release legal hold with reason.
12. Create retention policy.
13. Run retention dry-run.
14. Create access review campaign.
15. Generate and resolve permission review finding.
16. Create and finalize compliance evidence.
17. Confirm search/report/export masking.
18. Confirm trust dashboard metrics.
```

---

# 24. Acceptance criteria

Phase 38 is accepted only if:

```text
1. Current audit/privacy/retention capability is classified against TO-BE.
2. DataClassificationPolicy implemented/tested.
3. SensitiveObjectRegistry and SensitiveFieldRegistry implemented/tested.
4. SensitiveAccessLog implemented/tested.
5. ExportAuditLog implemented/tested.
6. DataSubjectIndex implemented/tested.
7. PrivacyRequest implemented/tested.
8. PrivacyExportPackage implemented/tested.
9. AnonymizationPlan implemented/tested.
10. RetentionPolicy and RetentionJob implemented/tested.
11. LegalHold implemented/tested.
12. ConsentRecord and ContactSuppression foundation implemented/tested.
13. AccessReviewCampaign and PermissionReviewFinding implemented/tested.
14. ComplianceEvidenceRecord implemented/tested.
15. Privacy-safe search/report/export behavior implemented/tested.
16. Trust dashboard/reports implemented/tested.
17. Notifications integrated/tested.
18. IAM permissions implemented/tested.
19. Event seeders idempotent.
20. Activity/audit/outbox follows Phase 04.
21. Sensitive values are not leaked in logs/search/report/export.
22. `mvn compile` passes.
23. `mvn test` passes.
24. Completion file exists.
```

Do not mark complete if:

```text
sensitive values are stored raw in audit logs unnecessarily
search snippets leak sensitive fields
exports bypass permission
privacy deletion ignores legal hold
retention job performs destructive action without dry-run option
access review automatically revokes without explicit action
evidence record claims certification
tests fail
```

---

# 25. Required phase completion file

Agent must create:

```text
docs/phase-complete/PHASE_38_AUDIT_COMPLIANCE_PRIVACY_RETENTION_TO_BE_COMPLETE.md
```

Required sections:

```text
# Phase 38 — Audit / Compliance Readiness / Privacy / Retention Complete

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
## 11. Data Classification Strategy
## 12. Sensitive Object / Field Strategy
## 13. Sensitive Access Logging Strategy
## 14. Export Audit Strategy
## 15. Data Subject Index Strategy
## 16. Privacy Request Strategy
## 17. Privacy Export Strategy
## 18. Anonymization Strategy
## 19. Retention Strategy
## 20. Legal Hold Strategy
## 21. Consent / Contact Suppression Strategy
## 22. Access Review Strategy
## 23. Compliance Evidence Strategy
## 24. Search / Report / Export Safety Strategy
## 25. Trust Dashboard Strategy
## 26. Notification Strategy
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
## 37. Future Phases That Must Return to Trust / Compliance
```

---

# 26. Future phases that may return

```text
Phase 39 — Integration / Import / Export:
- External DLP/security tools, SIEM export, enterprise archive, storage provider lifecycle.

Phase 40 — Service / Support / Maintenance:
- Support ticket privacy, incident evidence, operational service audit.

Phase 41 — Knowledge Graph / Semantic Index:
- Sensitive semantic index controls, AI privacy filtering, compliance Q&A.

Enterprise backlog:
- SSO/SAML advanced admin audit
- SOC2 evidence package automation
- ISO control mapping
- DPA/vendor management
- Advanced anomaly detection
```

---

# 27. Agent anti-bịa rules

The agent must not:

```text
1. Claim Scopery is SOC2/ISO/GDPR certified.
2. Store raw secrets in audit details.
3. Expose sensitive logs without permission.
4. Delete/anonymize data under legal hold.
5. Run destructive retention without safe controls.
6. Let search snippets leak sensitive fields.
7. Let report/export bypass masking.
8. Treat access review finding as automatic revoke.
9. Claim privacy request guarantees legal compliance.
10. Hide deferred legal/security/compliance review needs.
```

---

# 28. Prompt to give coding agent

```text
You are implementing Phase 38 — TO-BE Audit, Compliance Readiness, Privacy Controls, Retention, Sensitive Access Review & Data Governance.

Product direction:
Scopery should be audit-ready and privacy-aware.
Implement controls that classify data, protect sensitive fields, log sensitive access, track exports, handle privacy requests, support retention/legal hold, review access, and produce compliance evidence.
Do not claim external certification or legal compliance guarantee.

Before coding, read:
- CLAUDE.md / CLAUDE.ms
- Coding_convention.md
- Phase 00–37 docs and completion files
- Current backend code, migrations, tests

Your task:
1. Compare current audit/privacy/retention/compliance capability against this Phase 38 TO-BE spec.
2. Classify each capability with required labels.
3. Implement DataClassificationPolicy.
4. Implement SensitiveObjectRegistry and SensitiveFieldRegistry.
5. Implement SensitiveAccessLog and ExportAuditLog.
6. Implement DataSubjectIndex.
7. Implement PrivacyRequest and PrivacyExportPackage.
8. Implement AnonymizationPlan.
9. Implement RetentionPolicy, RetentionJob, and RetentionCandidate.
10. Implement LegalHold.
11. Implement ConsentRecord and ContactSuppression foundation.
12. Implement AccessReviewCampaign and PermissionReviewFinding.
13. Implement ComplianceEvidenceRecord.
14. Harden search/report/export masking and audit behavior.
15. Implement Trust dashboard/reports.
16. Integrate with IAM, Governance, DocumentHub, Portal, Custom Fields, Profitability, Resource, Notifications, Audit, Outbox.
17. Run mvn compile and mvn test.
18. Create docs/phase-complete/PHASE_38_AUDIT_COMPLIANCE_PRIVACY_RETENTION_TO_BE_COMPLETE.md.

Do not claim SOC2/ISO/GDPR certification, legal compliance guarantee, automatic legal judgment, or destructive deletion without legal hold/retention controls and tests.
```

---

# 29. Quick tracking matrix

| Capability | Current backend | Phase 38 action | Later phase |
|---|---|---|---|
| Basic audit | Existing/partial | Harden | — |
| DataClassificationPolicy | Missing/unknown | Must implement | — |
| SensitiveObjectRegistry | Missing/unknown | Must implement | — |
| SensitiveFieldRegistry | Missing/unknown | Must implement | — |
| SensitiveAccessLog | Missing/partial | Must implement | Security analytics later |
| ExportAuditLog | Missing/partial | Must implement | SIEM Phase 39 |
| DataSubjectIndex | Missing/unknown | Must implement | — |
| PrivacyRequest | Missing/unknown | Must implement | — |
| PrivacyExportPackage | Missing/unknown | Must implement | — |
| AnonymizationPlan | Missing/unknown | Must implement | — |
| RetentionPolicy | Missing/unknown | Must implement | Storage lifecycle Phase 39 |
| RetentionJob | Missing/unknown | Must implement | — |
| LegalHold | Missing/unknown | Must implement | — |
| ConsentRecord | Missing/unknown | Must implement foundation | CRM/Marketing future |
| ContactSuppression | Missing/unknown | Must implement foundation | — |
| AccessReviewCampaign | Missing/unknown | Must implement | Enterprise admin later |
| PermissionReviewFinding | Missing/unknown | Must implement | — |
| ComplianceEvidenceRecord | Missing/unknown | Must implement | SOC2/ISO mapping future |
| Privacy-safe search/report/export | Missing/partial | Must harden | — |
| Trust dashboard | Missing/unknown | Must implement | — |
| Certification automation | Missing | Not in core scope | Enterprise backlog |

---

# 30. Final principle

Phase 38 is not complete when "audit rows exist."

Phase 38 is complete when Scopery can answer:

```text
What data is sensitive?
Who accessed it?
Who exported it?
What personal data do we hold?
Can we safely export a subject's data?
Can we anonymize/delete safely?
What must be retained?
What is under legal hold?
Who has risky access?
What evidence proves the control ran?
```

Trust is not one feature.

Trust is classification, permission, masking, audit, retention, review, and evidence working together.
