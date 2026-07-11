# PHASE 39 — TO-BE Integration Hub, Import / Export Framework, External Connectors, Webhooks, Sync Jobs & Data Exchange

> Project: Scopery Backend  
> Phase: 39  
> Document type: TO-BE implementation-grade specification  
> Status: Planning / Integration foundation and data exchange layer  
> Roadmap group: Platform Integration & Enterprise Interoperability  
> Depends on: Phase 00 — Master Backend Roadmap, Phase 01 — API Path / Security Baseline, Phase 02 — IAM Core, Phase 03 — Workspace / Organization / Team, Phase 04 — Platform Audit / Outbox / Idempotency, Phase 05 — Event Registry, Phase 06 — Notification Engine, Phase 09 — Project Core / WBS / Task, Phase 10 — Project Authorization, Phase 20 — Project Events / Notifications, Phase 22 — Reporting / Dashboard / Export, Phase 23 — Core Hardening, Phase 27 — Document Hub / Generation, Phase 29 — External Party / Client CRM / Stakeholder, Phase 30 — Customer / External Portal, Phase 31 — Meetings / Collaboration, Phase 32 — Search / Productivity, Phase 33 — Custom Fields / Forms / Configuration, Phase 34 — Governance / Versioning / Permission / Audit, Phase 35 — Advanced Notifications, Phase 36 — Revenue & Profitability Visibility, Phase 37 — Resource / Capacity / Effort / Utilization, Phase 38 — Audit / Compliance / Privacy / Retention  
> API base: `/api`  
> Primary module: `modules/integration`, `modules/importexport`, `modules/connector`, or `modules/platform/integration` depending on repository architecture  
> Related modules: `iam`, `workspace`, `project`, `document`, `notification`, `reporting`, `search`, `collaboration`, `clientportal`, `externalparty`, `customfields`, `governance`, `trust`, `resource`, `profitability`, `audit`, `eventregistry`, future `mobile`, `ai-insight`, `service-support`  
> Important rule: Phase 39 builds the connector and data exchange foundation. It must not claim any external provider works unless the provider adapter, credentials, sync flow, error handling, tests, and manual verification exist.

---

# 0. Purpose

Phase 39 makes Scopery ready to exchange data with external systems.

By now, Scopery contains many useful project records:

```text
projects
tasks
documents
requirements
meetings
comments
notifications
client portal records
governance versions
audit records
revenue/profitability summaries
resource capacity data
custom fields
```

Teams often need to:

```text
import legacy project data
export dashboard data
send webhook events
sync documents to external storage
sync meetings with calendar
send notifications to Slack/Teams
import contacts from CRM
export reports to BI tools
import resource/cost/revenue actuals from external systems
connect to enterprise identity or admin tools later
```

Phase 39 provides the foundation for this.

---

# 1. Product intention

Scopery should not become every external product.

It should integrate safely.

The product goal:

```text
Users can bring data in.
Users can take data out.
Systems can subscribe to Scopery events.
Scopery can sync selected data with trusted providers.
Admin can see sync status, failures, and audit.
Sensitive data remains protected.
```

Phase 39 should be designed as a connector framework:

```text
provider config
credential reference
connection health
mapping
sync job
import job
export job
webhook endpoint
delivery attempt
error handling
audit
```

This lets future providers be added without rewriting core modules.

---

# 2. Core principle

```text
Integrations must be explicit, permissioned, auditable, and reversible where possible.
```

Meaning:

```text
No hidden sync.
No untracked export.
No provider claim without tests.
No raw secrets in database logs.
No sensitive data sent externally unless policy allows.
No external import overwriting controlled records silently.
```

---

# 3. Source inputs

Before coding Phase 39, the agent must read:

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
10. Phase 22 Reporting / Dashboard / Export implementation
11. Phase 23 Core Hardening completion file
12. Phase 27 Document Hub implementation
13. Phase 29 External Party / Contact implementation
14. Phase 30 External Portal implementation
15. Phase 31 Meeting/Collaboration implementation
16. Phase 32 Search/Productivity implementation
17. Phase 33 Custom Fields/Configuration implementation
18. Phase 34 Governance/Versioning/Audit implementation
19. Phase 35 Advanced Notifications implementation
20. Phase 36 Revenue/Profitability implementation
21. Phase 37 Resource/Capacity implementation
22. Phase 38 Trust/Privacy/Retention implementation
23. Existing import/export code if any
24. Existing webhook/outbox/event code
25. Existing OAuth/secret handling if any
26. Existing file storage/document provider code if any
27. Existing scheduled job infrastructure
28. Existing IAM seeders and EventDefinition seeders
```

The agent must inspect actual code and classify current state.

---

# 4. Current expected backend state

Before Phase 39, Scopery likely has:

```text
outbox events
event definitions
report export foundation
document generation/download
notifications
audit logs
workspace/project permissions
some import/export utilities maybe
```

Likely missing or partial:

```text
IntegrationProvider registry
IntegrationConnection
CredentialReference / SecretReference
ConnectorCapability
ConnectionHealthCheck
DataMappingProfile
ExternalIdMapping
ImportTemplate
ImportJob
ImportRowResult
ExportProfile
ExportJob
WebhookSubscription
WebhookDeliveryAttempt
InboundWebhookEndpoint
SyncJob
SyncCursor
SyncConflict
SyncRun
ProviderRateLimitState
DeadLetterIntegrationEvent
IntegrationAuditLog
```

---

# 5. Target statement

Phase 39 must deliver:

```text
1. Integration provider registry.
2. Integration connection model.
3. Secure credential/secret reference strategy.
4. Connector capability registry.
5. Connection health check.
6. Data mapping and external ID mapping.
7. Import framework with validation, preview, dry-run, execution, row results.
8. Export framework with profiles, jobs, permission masking, audit.
9. Webhook subscription and delivery framework.
10. Inbound webhook endpoint foundation.
11. Sync job/run/cursor/conflict model.
12. Provider rate limit and retry handling.
13. Integration observability dashboard.
14. Integration audit and trust integration.
15. Tests and completion file.
```

---

# 6. Boundary decisions

## 6.1 Framework first, providers second

Phase 39 must provide a strong framework.

Provider adapters can be implemented as:

```text
MUST_IMPLEMENT_IN_PHASE_39
SEED_ONLY_IN_PHASE_39
DEFERRED_TO_PHASE_XX
```

depending on actual codebase and product priority.

Do not claim a provider works unless implemented and tested.

## 6.2 No raw secrets

Secrets must be stored through existing secret manager or encrypted secret reference.

Never store raw OAuth tokens/API keys in normal tables or logs.

## 6.3 Import does not silently overwrite controlled data

Imports must support:

```text
preview
validation
dry-run
mapping
conflict detection
execution
rollback/undo strategy where possible
audit
```

## 6.4 Export must follow trust rules

Exports must respect Phase 38:

```text
field classification
masking
export permission
export audit
retention/expiration
```

## 6.5 Webhook delivery is best-effort with retry

Webhook does not guarantee target system accepted business semantics.

It records delivery attempts and response status.

## 6.6 Sync is not magic

Sync must be explicit:

```text
which provider
which direction
which objects
which fields
which conflict strategy
which schedule
which permissions
```

---

# 7. Classification labels

Use these labels in completion file:

| Label | Meaning |
|---|---|
| `CURRENTLY_IMPLEMENTED` | Existing backend already supports it with tests. |
| `PARTIALLY_IMPLEMENTED` | Some support exists but gaps remain. |
| `MUST_IMPLEMENT_IN_PHASE_39` | Required now. |
| `MUST_HARDEN_IN_PHASE_39` | Existing import/export/integration behavior must be hardened now. |
| `SEED_ONLY_IN_PHASE_39` | Seed providers/capabilities/events/permissions/templates only. |
| `DEFERRED_TO_PHASE_XX` | Later phase returns. |
| `DEFERRED_TO_POST_23_BACKLOG` | Later backlog item. |
| `NOT_IN_SCOPE_FOR_PHASE_39` | Explicitly not in this phase. |

---

# 8. Required capabilities

---

## 8.1 PROV-001 — IntegrationProvider

Classification: `MUST_IMPLEMENT_IN_PHASE_39`

Purpose:

```text
Register external provider types known by Scopery.
```

Provider categories:

```text
STORAGE
CALENDAR
EMAIL
CHAT
CRM
BI_EXPORT
ACCOUNTING_IMPORT
HR_IMPORT
ISSUE_TRACKER
CODE_REPOSITORY
WEBHOOK
GENERIC_API
CSV_IMPORT
CSV_EXPORT
```

Example providers:

```text
GOOGLE_DRIVE
GOOGLE_CALENDAR
MICROSOFT_ONEDRIVE
MICROSOFT_OUTLOOK_CALENDAR
SLACK
MICROSOFT_TEAMS
HUBSPOT
SALESFORCE
POWER_BI
LOOKER_STUDIO
JIRA
GITHUB
GITLAB
GENERIC_WEBHOOK
CSV
JSON
```

Rules:

```text
1. Provider registry is seeded.
2. Provider does not mean connection exists.
3. Provider does not mean provider adapter is implemented.
4. Provider declares supported capabilities.
5. Unsupported providers can be seeded as disabled/future.
```

---

## 8.2 CONN-001 — IntegrationConnection

Classification: `MUST_IMPLEMENT_IN_PHASE_39`

Purpose:

```text
Represent a workspace/project connection to an external provider.
```

Connection scopes:

```text
WORKSPACE
PROJECT
USER
PORTAL
SYSTEM
```

Connection statuses:

```text
DRAFT
ACTIVE
DEGRADED
EXPIRED
DISABLED
ERROR
ARCHIVED
```

Rules:

```text
1. Connection belongs to workspace.
2. Project-scoped connection also references project.
3. User-scoped connection references user.
4. Connection stores config but not raw secrets.
5. Connection health tracked.
6. Connection can be disabled without deleting history.
```

---

## 8.3 SEC-001 — CredentialReference / SecretReference

Classification: `MUST_IMPLEMENT_IN_PHASE_39`

Purpose:

```text
Store reference to provider credentials securely.
```

Rules:

```text
1. Raw secrets must not be stored in normal entity fields.
2. Secret values must not appear in logs/audit/details.
3. Secret reference can point to encrypted storage or secret manager.
4. Token refresh failures update connection status.
5. Credential rotation audited.
```

---

## 8.4 CAP-001 — ConnectorCapability

Classification: `MUST_IMPLEMENT_IN_PHASE_39`

Purpose:

```text
Declare what a provider/connection can do.
```

Capabilities:

```text
IMPORT_PROJECTS
IMPORT_TASKS
IMPORT_CONTACTS
IMPORT_DOCUMENTS
IMPORT_RESOURCE_DATA
IMPORT_REVENUE_ACTUALS
EXPORT_REPORTS
EXPORT_DOCUMENTS
EXPORT_DASHBOARD_DATA
SYNC_CALENDAR_EVENTS
SYNC_DOCUMENTS
SEND_CHAT_NOTIFICATION
SEND_EMAIL_NOTIFICATION
WEBHOOK_OUTBOUND
WEBHOOK_INBOUND
BI_DATA_FEED
```

Rules:

```text
1. Capability is checked before job execution.
2. Capability can be disabled per connection.
3. Capability does not grant user permission.
```

---

## 8.5 HEALTH-001 — ConnectionHealthCheck

Classification: `MUST_IMPLEMENT_IN_PHASE_39`

Purpose:

```text
Check whether integration connection is usable.
```

Health statuses:

```text
UNKNOWN
HEALTHY
DEGRADED
AUTH_EXPIRED
RATE_LIMITED
ERROR
DISABLED
```

Rules:

```text
1. Health check stores last checked time and result.
2. Health failure does not delete connection.
3. Health issues can notify admins.
4. Health check should avoid exposing provider secrets.
```

---

## 8.6 MAP-001 — DataMappingProfile

Classification: `MUST_IMPLEMENT_IN_PHASE_39`

Purpose:

```text
Map external fields to Scopery fields.
```

Mapping targets:

```text
PROJECT
TASK
WBS_ITEM
DOCUMENT
EXTERNAL_CONTACT
MEETING
REQUIREMENT
RESOURCE_PROFILE
EFFORT_RECORD
REVENUE_SOURCE
COST_SOURCE
CUSTOM_FIELD_VALUE
```

Rules:

```text
1. Mapping profile belongs to workspace/connection.
2. Mapping supports required field validation.
3. Mapping can map external values to custom fields.
4. Sensitive fields follow classification policy.
5. Mapping profile versioned/audited.
```

---

## 8.7 MAP-002 — ExternalIdMapping

Classification: `MUST_IMPLEMENT_IN_PHASE_39`

Purpose:

```text
Link external records to Scopery records.
```

Rules:

```text
1. External ID mapping is unique per provider/connection/object.
2. Mapping prevents duplicate imports.
3. Mapping stores last synced metadata.
4. Mapping does not grant access.
5. Mapping archived if connection archived.
```

---

## 8.8 IMPORT-001 — ImportTemplate

Classification: `MUST_IMPLEMENT_IN_PHASE_39`

Purpose:

```text
Define reusable import format for CSV/JSON/provider import.
```

Templates:

```text
Project import
Task import
WBS import
Contact import
Document metadata import
Resource import
Effort import
Revenue source import
Cost source import
Custom field values import
```

Rules:

```text
1. Template has required columns/fields.
2. Template can include sample file.
3. Template can validate field types.
4. Template can map to custom fields.
5. Template versioned/audited.
```

---

## 8.9 IMPORT-002 — ImportJob

Classification: `MUST_IMPLEMENT_IN_PHASE_39`

Purpose:

```text
Run import safely.
```

Import modes:

```text
VALIDATE_ONLY
DRY_RUN
EXECUTE
```

Import statuses:

```text
CREATED
VALIDATING
VALIDATED
DRY_RUN_COMPLETED
RUNNING
COMPLETED
COMPLETED_WITH_ERRORS
FAILED
CANCELLED
```

Rules:

```text
1. Import requires permission.
2. Import validates before execution.
3. Import supports dry-run preview.
4. Import records row-level results.
5. Import detects conflicts.
6. Import creates audit and events.
7. Import should be idempotent where possible.
```

---

## 8.10 IMPORT-003 — ImportRowResult

Classification: `MUST_IMPLEMENT_IN_PHASE_39`

Purpose:

```text
Track row-level import outcome.
```

Row statuses:

```text
VALID
INVALID
SKIPPED
CREATED
UPDATED
CONFLICT
FAILED
```

Rules:

```text
1. Store row number/reference.
2. Store validation messages.
3. Avoid storing raw sensitive data if not needed.
4. Link created/updated Scopery object where possible.
```

---

## 8.11 EXPORT-001 — ExportProfile

Classification: `MUST_IMPLEMENT_IN_PHASE_39`

Purpose:

```text
Define reusable export configuration.
```

Export targets:

```text
CSV
JSON
XLSX
PDF
BI_FEED
WEBHOOK_PAYLOAD
EXTERNAL_STORAGE
```

Export object scopes:

```text
PROJECTS
TASKS
DOCUMENTS
REQUIREMENTS
REPORTS
PROFITABILITY
RESOURCE_CAPACITY
AUDIT
TRUST
CUSTOM_OBJECTS
```

Rules:

```text
1. Export profile stores columns/filters/format.
2. Export profile respects field permissions.
3. Export profile can be scheduled if scheduler exists.
4. Export profile changes audited.
```

---

## 8.12 EXPORT-002 — ExportJob

Classification: `MUST_IMPLEMENT_IN_PHASE_39`

Purpose:

```text
Run export safely.
```

Rules:

```text
1. Export requires permission.
2. Export uses Phase 38 masking/export audit.
3. Export output has retention/expiration.
4. Export does not include hidden fields.
5. Export can send to external storage only if connection active.
6. Export failure recorded.
```

---

## 8.13 WEBHOOK-001 — WebhookSubscription

Classification: `MUST_IMPLEMENT_IN_PHASE_39`

Purpose:

```text
Allow external systems to receive Scopery events.
```

Webhook event sources:

```text
project
task
document
requirement
change request
governance
notification
profitability
resource
trust
custom field
```

Rules:

```text
1. Subscription belongs to workspace/connection.
2. Event types explicitly selected.
3. Payload template/version explicit.
4. Signing secret stored securely.
5. Webhook can be disabled.
6. Sensitive fields excluded unless policy and permission allow.
```

---

## 8.14 WEBHOOK-002 — WebhookDeliveryAttempt

Classification: `MUST_IMPLEMENT_IN_PHASE_39`

Purpose:

```text
Track outbound webhook delivery.
```

Rules:

```text
1. Delivery attempts store status, response code, duration.
2. Payload body should be stored carefully or redacted.
3. Retry with backoff.
4. Dead-letter after max attempts.
5. Delivery audit available.
```

Statuses:

```text
PENDING
SENT
FAILED
RETRYING
DEAD_LETTERED
CANCELLED
```

---

## 8.15 WEBHOOK-003 — InboundWebhookEndpoint

Classification: `MUST_IMPLEMENT_IN_PHASE_39`

Purpose:

```text
Receive provider events.
```

Use cases:

```text
document changed in storage
calendar event updated
external task updated
chat command future
provider auth event
```

Rules:

```text
1. Endpoint validates signature/token.
2. Endpoint records received event.
3. Endpoint maps event to SyncJob or ImportJob.
4. Unknown events stored safely.
5. Inbound payloads should be retained according to policy.
```

---

## 8.16 SYNC-001 — SyncJob

Classification: `MUST_IMPLEMENT_IN_PHASE_39`

Purpose:

```text
Synchronize selected data between Scopery and external system.
```

Sync directions:

```text
IMPORT_ONLY
EXPORT_ONLY
BIDIRECTIONAL
```

Sync modes:

```text
MANUAL
SCHEDULED
EVENT_DRIVEN
```

Rules:

```text
1. Sync scope explicit.
2. Sync direction explicit.
3. Sync mapping required.
4. Sync conflict strategy required.
5. Sync run records results.
6. Bidirectional sync requires stricter conflict logic.
```

---

## 8.17 SYNC-002 — SyncRun / SyncCursor

Classification: `MUST_IMPLEMENT_IN_PHASE_39`

Purpose:

```text
Track each sync execution and cursor state.
```

Rules:

```text
1. SyncRun records started/completed/status/counts/errors.
2. SyncCursor tracks last successful position.
3. Cursor updates only after successful processing.
4. Failed run does not corrupt cursor.
5. SyncRun can be retried.
```

---

## 8.18 SYNC-003 — SyncConflict

Classification: `MUST_IMPLEMENT_IN_PHASE_39`

Purpose:

```text
Record conflicts between Scopery and external system.
```

Conflict types:

```text
BOTH_CHANGED
DELETED_EXTERNALLY
DELETED_IN_SCOPERY
FIELD_MAPPING_CONFLICT
PERMISSION_BLOCKED
VALIDATION_FAILED
VERSION_LOCKED
```

Conflict strategies:

```text
SCOPERY_WINS
EXTERNAL_WINS
MOST_RECENT_WINS
MANUAL_RESOLUTION
SKIP
```

Rules:

```text
1. Controlled/finalized records should not be overwritten silently.
2. Conflict can require manual resolution.
3. Conflict resolution audited.
4. Conflict does not expose sensitive values.
```

---

## 8.19 RATE-001 — ProviderRateLimitState

Classification: `MUST_IMPLEMENT_IN_PHASE_39`

Purpose:

```text
Track provider rate limit and backoff state.
```

Rules:

```text
1. Rate limit status per connection/provider.
2. Backoff respected.
3. Rate limited jobs move to delayed/retry state.
4. Notify admin if persistent.
```

---

## 8.20 OBS-001 — Integration observability dashboard

Classification: `MUST_IMPLEMENT_IN_PHASE_39`

Dashboard metrics:

```text
active connections
degraded connections
failed imports
failed exports
failed webhooks
sync conflicts
rate limited providers
last successful sync
dead-letter count
sensitive exports by connection
```

Rules:

```text
1. Dashboard requires integration admin/report permission.
2. Sensitive data masked.
3. Links to job logs and errors.
```

---

## 8.21 NOTIF-001 — Integration notifications

Classification: `MUST_IMPLEMENT_IN_PHASE_39` if Phase 35 exists.

Notifications:

```text
connection expired
connection degraded
import completed with errors
export failed
webhook dead-lettered
sync conflict created
sync job failed
provider rate limited
credential rotation needed
```

Rules:

```text
1. Notify connection owner/admin.
2. Sensitive provider details masked.
3. Repeated failures deduplicated.
```

---

## 8.22 AI-001 — AI-assisted import mapping and integration troubleshooting

Classification: `SEED_ONLY_IN_PHASE_39` or `MUST_IMPLEMENT_IN_PHASE_39` if Phase 21 exists.

AI can help with:

```text
suggest CSV field mapping
summarize import errors
suggest sync conflict resolution
explain provider failure
suggest export columns
summarize integration health
```

Rules:

```text
1. AI sees only data actor can access.
2. AI cannot create connection with secret.
3. AI cannot execute import/export automatically unless user confirms and permission allows.
4. AI cannot expose secrets or sensitive data.
```

---

# 9. Provider implementation priorities

Phase 39 framework should seed many provider types, but implementation priority should be practical.

## 9.1 Strong MVP provider candidates

```text
CSV import/export
JSON import/export
Generic outbound webhook
Generic inbound webhook foundation
External storage export foundation
```

## 9.2 Useful next provider candidates

```text
Google Drive / Microsoft OneDrive document sync
Google Calendar / Outlook Calendar meeting sync
Slack / Teams notification delivery
PowerBI / BI export feed
Jira import/sync
GitHub/GitLab issue/reference sync
```

## 9.3 Provider implementation rule

For each provider, completion file must state:

```text
SEEDED_ONLY
CONFIG_UI_READY_ONLY
ADAPTER_IMPLEMENTED
AUTH_IMPLEMENTED
SYNC_IMPLEMENTED
TESTED
DEFERRED
```

Do not mark provider as working unless adapter and tests exist.

---

# 10. Entity model TO-BE

---

## 10.1 IntegrationProvider — `integration_provider`

Fields:

```text
id UUID PK
provider_code VARCHAR(150) NOT NULL
name VARCHAR(255) NOT NULL
category VARCHAR(100) NOT NULL
description TEXT NULL
adapter_key VARCHAR(150) NULL
enabled BOOLEAN NOT NULL DEFAULT true
seed_only BOOLEAN NOT NULL DEFAULT false
capabilities_json JSONB NULL
created_at TIMESTAMP NOT NULL
updated_at TIMESTAMP NULL
version INT
```

Constraint:

```text
unique provider_code
```

---

## 10.2 IntegrationConnection — `integration_connection`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
project_id UUID NULL
provider_code VARCHAR(150) NOT NULL
connection_scope VARCHAR(50) NOT NULL
name VARCHAR(255) NOT NULL
description TEXT NULL
owner_user_id UUID NULL
credential_reference_id UUID NULL
config_json JSONB NULL
status VARCHAR(50) NOT NULL
last_health_status VARCHAR(50) NULL
last_health_checked_at TIMESTAMP NULL
last_error_code VARCHAR(150) NULL
last_error_message TEXT NULL
created_at / created_by
updated_at / updated_by
disabled_at / disabled_by
archived_at / archived_by
trace_id VARCHAR(100) NULL
version INT
```

---

## 10.3 CredentialReference — `integration_credential_reference`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
provider_code VARCHAR(150) NOT NULL
credential_type VARCHAR(50) NOT NULL
secret_reference VARCHAR(500) NOT NULL
status VARCHAR(50) NOT NULL
expires_at TIMESTAMP NULL
last_rotated_at TIMESTAMP NULL
created_at / created_by
updated_at / updated_by
revoked_at / revoked_by
trace_id VARCHAR(100) NULL
version INT
```

Credential types:

```text
OAUTH2
API_KEY
BASIC_AUTH
WEBHOOK_SIGNING_SECRET
SERVICE_ACCOUNT
PERSONAL_TOKEN
```

---

## 10.4 ConnectorCapability — `integration_connector_capability`

Fields:

```text
id UUID PK
provider_code VARCHAR(150) NOT NULL
capability_code VARCHAR(150) NOT NULL
direction VARCHAR(50) NOT NULL
enabled BOOLEAN NOT NULL DEFAULT true
description TEXT NULL
created_at TIMESTAMP NOT NULL
updated_at TIMESTAMP NULL
version INT
```

Constraint:

```text
unique provider_code + capability_code
```

---

## 10.5 ConnectionHealthCheck — `integration_connection_health_check`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
connection_id UUID NOT NULL
health_status VARCHAR(50) NOT NULL
checked_at TIMESTAMP NOT NULL
duration_ms BIGINT NULL
message TEXT NULL
error_code VARCHAR(150) NULL
details_json JSONB NULL
trace_id VARCHAR(100) NULL
version INT
```

---

## 10.6 DataMappingProfile — `integration_data_mapping_profile`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
connection_id UUID NULL
mapping_code VARCHAR(150) NOT NULL
name VARCHAR(255) NOT NULL
target_object_type VARCHAR(100) NOT NULL
source_format VARCHAR(100) NOT NULL
mapping_json JSONB NOT NULL
validation_rules_json JSONB NULL
status VARCHAR(50) NOT NULL
created_at / created_by
updated_at / updated_by
archived_at / archived_by
trace_id VARCHAR(100) NULL
version INT
```

Constraint:

```text
unique workspace_id + mapping_code
```

---

## 10.7 ExternalIdMapping — `integration_external_id_mapping`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
connection_id UUID NOT NULL
provider_code VARCHAR(150) NOT NULL
external_object_type VARCHAR(150) NOT NULL
external_id VARCHAR(500) NOT NULL
scopery_object_type VARCHAR(100) NOT NULL
scopery_object_id UUID NOT NULL
last_synced_at TIMESTAMP NULL
external_updated_at TIMESTAMP NULL
scopery_updated_at TIMESTAMP NULL
sync_state VARCHAR(50) NULL
metadata_json JSONB NULL
created_at TIMESTAMP NOT NULL
updated_at TIMESTAMP NULL
version INT
```

Constraint:

```text
unique connection_id + external_object_type + external_id
```

---

## 10.8 ImportTemplate — `integration_import_template`

Fields:

```text
id UUID PK
workspace_id UUID NULL
template_code VARCHAR(150) NOT NULL
name VARCHAR(255) NOT NULL
target_object_type VARCHAR(100) NOT NULL
source_format VARCHAR(100) NOT NULL
schema_json JSONB NOT NULL
sample_file_reference VARCHAR(500) NULL
enabled BOOLEAN NOT NULL DEFAULT true
created_at / created_by
updated_at / updated_by
version INT
```

Workspace null can mean system template.

---

## 10.9 ImportJob — `integration_import_job`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
project_id UUID NULL
connection_id UUID NULL
import_template_id UUID NULL
mapping_profile_id UUID NULL
job_mode VARCHAR(50) NOT NULL
source_format VARCHAR(100) NOT NULL
source_file_reference VARCHAR(500) NULL
target_object_type VARCHAR(100) NOT NULL
status VARCHAR(50) NOT NULL
total_rows BIGINT NOT NULL DEFAULT 0
valid_rows BIGINT NOT NULL DEFAULT 0
invalid_rows BIGINT NOT NULL DEFAULT 0
created_rows BIGINT NOT NULL DEFAULT 0
updated_rows BIGINT NOT NULL DEFAULT 0
skipped_rows BIGINT NOT NULL DEFAULT 0
conflict_rows BIGINT NOT NULL DEFAULT 0
failed_rows BIGINT NOT NULL DEFAULT 0
started_at TIMESTAMP NULL
completed_at TIMESTAMP NULL
created_at / created_by
cancelled_at / cancelled_by
failure_code VARCHAR(150) NULL
failure_message TEXT NULL
trace_id VARCHAR(100) NULL
version INT
```

---

## 10.10 ImportRowResult — `integration_import_row_result`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
import_job_id UUID NOT NULL
row_number BIGINT NOT NULL
row_reference VARCHAR(255) NULL
status VARCHAR(50) NOT NULL
message TEXT NULL
validation_errors_json JSONB NULL
target_object_type VARCHAR(100) NULL
target_object_id UUID NULL
external_id VARCHAR(500) NULL
created_at TIMESTAMP NOT NULL
version INT
```

---

## 10.11 ExportProfile — `integration_export_profile`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
connection_id UUID NULL
profile_code VARCHAR(150) NOT NULL
name VARCHAR(255) NOT NULL
export_format VARCHAR(50) NOT NULL
target_destination VARCHAR(100) NOT NULL
object_scope VARCHAR(100) NOT NULL
columns_json JSONB NULL
filters_json JSONB NULL
masking_policy VARCHAR(100) NULL
status VARCHAR(50) NOT NULL
created_at / created_by
updated_at / updated_by
archived_at / archived_by
trace_id VARCHAR(100) NULL
version INT
```

Constraint:

```text
unique workspace_id + profile_code
```

---

## 10.12 ExportJob — `integration_export_job`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
project_id UUID NULL
connection_id UUID NULL
export_profile_id UUID NULL
export_format VARCHAR(50) NOT NULL
object_scope VARCHAR(100) NOT NULL
status VARCHAR(50) NOT NULL
row_count BIGINT NULL
file_reference VARCHAR(500) NULL
expires_at TIMESTAMP NULL
started_at TIMESTAMP NULL
completed_at TIMESTAMP NULL
created_at / created_by
cancelled_at / cancelled_by
failure_code VARCHAR(150) NULL
failure_message TEXT NULL
export_audit_log_id UUID NULL
trace_id VARCHAR(100) NULL
version INT
```

---

## 10.13 WebhookSubscription — `integration_webhook_subscription`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
connection_id UUID NULL
name VARCHAR(255) NOT NULL
endpoint_url VARCHAR(1000) NOT NULL
event_types_json JSONB NOT NULL
payload_version VARCHAR(50) NOT NULL
signing_secret_reference_id UUID NULL
status VARCHAR(50) NOT NULL
max_attempts INT NOT NULL DEFAULT 5
timeout_seconds INT NOT NULL DEFAULT 10
created_at / created_by
updated_at / updated_by
disabled_at / disabled_by
archived_at / archived_by
trace_id VARCHAR(100) NULL
version INT
```

---

## 10.14 WebhookDeliveryAttempt — `integration_webhook_delivery_attempt`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
webhook_subscription_id UUID NOT NULL
event_type VARCHAR(150) NOT NULL
event_id UUID NULL
target_object_type VARCHAR(100) NULL
target_object_id UUID NULL
status VARCHAR(50) NOT NULL
attempt_number INT NOT NULL
request_id VARCHAR(150) NULL
response_status_code INT NULL
response_body_redacted TEXT NULL
duration_ms BIGINT NULL
next_retry_at TIMESTAMP NULL
created_at TIMESTAMP NOT NULL
sent_at TIMESTAMP NULL
failure_code VARCHAR(150) NULL
failure_message TEXT NULL
trace_id VARCHAR(100) NULL
version INT
```

---

## 10.15 InboundWebhookEndpoint — `integration_inbound_webhook_endpoint`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
connection_id UUID NOT NULL
endpoint_code VARCHAR(150) NOT NULL
provider_code VARCHAR(150) NOT NULL
signing_secret_reference_id UUID NULL
status VARCHAR(50) NOT NULL
created_at / created_by
updated_at / updated_by
disabled_at / disabled_by
version INT
```

---

## 10.16 InboundWebhookEvent — `integration_inbound_webhook_event`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
inbound_endpoint_id UUID NOT NULL
provider_code VARCHAR(150) NOT NULL
external_event_id VARCHAR(500) NULL
event_type VARCHAR(150) NOT NULL
status VARCHAR(50) NOT NULL
payload_reference VARCHAR(500) NULL
payload_redacted_json JSONB NULL
received_at TIMESTAMP NOT NULL
processed_at TIMESTAMP NULL
failure_code VARCHAR(150) NULL
failure_message TEXT NULL
trace_id VARCHAR(100) NULL
version INT
```

---

## 10.17 SyncJob — `integration_sync_job`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
project_id UUID NULL
connection_id UUID NOT NULL
mapping_profile_id UUID NULL
sync_code VARCHAR(150) NULL
name VARCHAR(255) NOT NULL
sync_direction VARCHAR(50) NOT NULL
sync_mode VARCHAR(50) NOT NULL
object_scope VARCHAR(100) NOT NULL
conflict_strategy VARCHAR(50) NOT NULL
schedule_json JSONB NULL
status VARCHAR(50) NOT NULL
created_at / created_by
updated_at / updated_by
disabled_at / disabled_by
archived_at / archived_by
trace_id VARCHAR(100) NULL
version INT
```

---

## 10.18 SyncRun — `integration_sync_run`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
sync_job_id UUID NOT NULL
status VARCHAR(50) NOT NULL
started_at TIMESTAMP NULL
completed_at TIMESTAMP NULL
processed_count BIGINT NOT NULL DEFAULT 0
created_count BIGINT NOT NULL DEFAULT 0
updated_count BIGINT NOT NULL DEFAULT 0
skipped_count BIGINT NOT NULL DEFAULT 0
conflict_count BIGINT NOT NULL DEFAULT 0
failed_count BIGINT NOT NULL DEFAULT 0
failure_code VARCHAR(150) NULL
failure_message TEXT NULL
trace_id VARCHAR(100) NULL
version INT
```

---

## 10.19 SyncCursor — `integration_sync_cursor`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
sync_job_id UUID NOT NULL
cursor_key VARCHAR(150) NOT NULL
cursor_value TEXT NULL
last_successful_sync_at TIMESTAMP NULL
updated_at TIMESTAMP NOT NULL
version INT
```

Constraint:

```text
unique sync_job_id + cursor_key
```

---

## 10.20 SyncConflict — `integration_sync_conflict`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
sync_job_id UUID NOT NULL
sync_run_id UUID NULL
connection_id UUID NOT NULL
conflict_type VARCHAR(100) NOT NULL
scopery_object_type VARCHAR(100) NULL
scopery_object_id UUID NULL
external_object_type VARCHAR(100) NULL
external_id VARCHAR(500) NULL
severity VARCHAR(50) NOT NULL
status VARCHAR(50) NOT NULL
description TEXT NULL
resolution_strategy VARCHAR(50) NULL
resolved_at TIMESTAMP NULL
resolved_by UUID NULL
resolution_notes TEXT NULL
created_at TIMESTAMP NOT NULL
trace_id VARCHAR(100) NULL
version INT
```

---

## 10.21 ProviderRateLimitState — `integration_provider_rate_limit_state`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
connection_id UUID NOT NULL
provider_code VARCHAR(150) NOT NULL
status VARCHAR(50) NOT NULL
limit_name VARCHAR(150) NULL
remaining_count BIGINT NULL
reset_at TIMESTAMP NULL
backoff_until TIMESTAMP NULL
last_updated_at TIMESTAMP NOT NULL
version INT
```

---

## 10.22 DeadLetterIntegrationEvent — `integration_dead_letter_event`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
source_type VARCHAR(100) NOT NULL
source_id UUID NULL
event_type VARCHAR(150) NULL
payload_reference VARCHAR(500) NULL
failure_code VARCHAR(150) NOT NULL
failure_message TEXT NULL
attempt_count INT NOT NULL DEFAULT 0
status VARCHAR(50) NOT NULL
created_at TIMESTAMP NOT NULL
last_attempt_at TIMESTAMP NULL
resolved_at TIMESTAMP NULL
resolved_by UUID NULL
version INT
```

---

# 11. API TO-BE list

All APIs use `/api`.

---

## 11.1 Provider APIs

```text
GET /api/integrations/providers
GET /api/integrations/providers/{providerCode}
GET /api/integrations/providers/{providerCode}/capabilities
```

Admin seed:

```text
POST /api/admin/integrations/providers/seed
```

---

## 11.2 Connection APIs

```text
POST  /api/workspaces/{workspaceId}/integrations/connections
GET   /api/workspaces/{workspaceId}/integrations/connections
GET   /api/workspaces/{workspaceId}/integrations/connections/{connectionId}
PUT   /api/workspaces/{workspaceId}/integrations/connections/{connectionId}
POST  /api/workspaces/{workspaceId}/integrations/connections/{connectionId}/enable
POST  /api/workspaces/{workspaceId}/integrations/connections/{connectionId}/disable
PATCH /api/workspaces/{workspaceId}/integrations/connections/{connectionId}/archive
POST  /api/workspaces/{workspaceId}/integrations/connections/{connectionId}/health-check
GET   /api/workspaces/{workspaceId}/integrations/connections/{connectionId}/health-checks
```

---

## 11.3 Credential reference APIs

```text
POST /api/workspaces/{workspaceId}/integrations/credential-references
GET  /api/workspaces/{workspaceId}/integrations/credential-references
POST /api/workspaces/{workspaceId}/integrations/credential-references/{credentialId}/rotate
POST /api/workspaces/{workspaceId}/integrations/credential-references/{credentialId}/revoke
```

Responses must never return raw secret value.

---

## 11.4 Mapping APIs

```text
POST  /api/workspaces/{workspaceId}/integrations/mapping-profiles
GET   /api/workspaces/{workspaceId}/integrations/mapping-profiles
GET   /api/workspaces/{workspaceId}/integrations/mapping-profiles/{mappingProfileId}
PUT   /api/workspaces/{workspaceId}/integrations/mapping-profiles/{mappingProfileId}
PATCH /api/workspaces/{workspaceId}/integrations/mapping-profiles/{mappingProfileId}/archive

GET /api/workspaces/{workspaceId}/integrations/external-id-mappings
GET /api/workspaces/{workspaceId}/integrations/external-id-mappings/by-object?objectType=...&objectId=...
```

---

## 11.5 Import APIs

```text
GET  /api/workspaces/{workspaceId}/integrations/import-templates
GET  /api/workspaces/{workspaceId}/integrations/import-templates/{templateId}

POST /api/workspaces/{workspaceId}/integrations/import-jobs
GET  /api/workspaces/{workspaceId}/integrations/import-jobs
GET  /api/workspaces/{workspaceId}/integrations/import-jobs/{importJobId}
POST /api/workspaces/{workspaceId}/integrations/import-jobs/{importJobId}/validate
POST /api/workspaces/{workspaceId}/integrations/import-jobs/{importJobId}/dry-run
POST /api/workspaces/{workspaceId}/integrations/import-jobs/{importJobId}/execute
POST /api/workspaces/{workspaceId}/integrations/import-jobs/{importJobId}/cancel
GET  /api/workspaces/{workspaceId}/integrations/import-jobs/{importJobId}/rows
```

---

## 11.6 Export APIs

```text
POST  /api/workspaces/{workspaceId}/integrations/export-profiles
GET   /api/workspaces/{workspaceId}/integrations/export-profiles
GET   /api/workspaces/{workspaceId}/integrations/export-profiles/{exportProfileId}
PUT   /api/workspaces/{workspaceId}/integrations/export-profiles/{exportProfileId}
PATCH /api/workspaces/{workspaceId}/integrations/export-profiles/{exportProfileId}/archive

POST /api/workspaces/{workspaceId}/integrations/export-jobs
GET  /api/workspaces/{workspaceId}/integrations/export-jobs
GET  /api/workspaces/{workspaceId}/integrations/export-jobs/{exportJobId}
POST /api/workspaces/{workspaceId}/integrations/export-jobs/{exportJobId}/cancel
GET  /api/workspaces/{workspaceId}/integrations/export-jobs/{exportJobId}/download
```

Download requires permission and expiration check.

---

## 11.7 Webhook APIs

```text
POST  /api/workspaces/{workspaceId}/integrations/webhooks/subscriptions
GET   /api/workspaces/{workspaceId}/integrations/webhooks/subscriptions
GET   /api/workspaces/{workspaceId}/integrations/webhooks/subscriptions/{subscriptionId}
PUT   /api/workspaces/{workspaceId}/integrations/webhooks/subscriptions/{subscriptionId}
POST  /api/workspaces/{workspaceId}/integrations/webhooks/subscriptions/{subscriptionId}/enable
POST  /api/workspaces/{workspaceId}/integrations/webhooks/subscriptions/{subscriptionId}/disable
PATCH /api/workspaces/{workspaceId}/integrations/webhooks/subscriptions/{subscriptionId}/archive

GET  /api/workspaces/{workspaceId}/integrations/webhooks/delivery-attempts
POST /api/workspaces/{workspaceId}/integrations/webhooks/delivery-attempts/{attemptId}/retry
```

Inbound provider endpoint pattern:

```text
POST /api/integrations/inbound/{endpointCode}
```

Inbound management:

```text
POST /api/workspaces/{workspaceId}/integrations/inbound-endpoints
GET  /api/workspaces/{workspaceId}/integrations/inbound-endpoints
GET  /api/workspaces/{workspaceId}/integrations/inbound-events
```

---

## 11.8 Sync APIs

```text
POST  /api/workspaces/{workspaceId}/integrations/sync-jobs
GET   /api/workspaces/{workspaceId}/integrations/sync-jobs
GET   /api/workspaces/{workspaceId}/integrations/sync-jobs/{syncJobId}
PUT   /api/workspaces/{workspaceId}/integrations/sync-jobs/{syncJobId}
POST  /api/workspaces/{workspaceId}/integrations/sync-jobs/{syncJobId}/run-now
POST  /api/workspaces/{workspaceId}/integrations/sync-jobs/{syncJobId}/enable
POST  /api/workspaces/{workspaceId}/integrations/sync-jobs/{syncJobId}/disable
PATCH /api/workspaces/{workspaceId}/integrations/sync-jobs/{syncJobId}/archive

GET /api/workspaces/{workspaceId}/integrations/sync-runs
GET /api/workspaces/{workspaceId}/integrations/sync-runs/{syncRunId}

GET  /api/workspaces/{workspaceId}/integrations/sync-conflicts
POST /api/workspaces/{workspaceId}/integrations/sync-conflicts/{conflictId}/resolve
POST /api/workspaces/{workspaceId}/integrations/sync-conflicts/{conflictId}/dismiss
```

---

## 11.9 Observability APIs

```text
GET /api/workspaces/{workspaceId}/integrations/dashboard
GET /api/workspaces/{workspaceId}/integrations/rate-limits
GET /api/workspaces/{workspaceId}/integrations/dead-letter-events
POST /api/workspaces/{workspaceId}/integrations/dead-letter-events/{deadLetterId}/retry
POST /api/workspaces/{workspaceId}/integrations/dead-letter-events/{deadLetterId}/resolve
```

---

## 11.10 Reports APIs

```text
GET /api/workspaces/{workspaceId}/reports/integrations/summary
GET /api/workspaces/{workspaceId}/reports/integrations/connections
GET /api/workspaces/{workspaceId}/reports/integrations/imports
GET /api/workspaces/{workspaceId}/reports/integrations/exports
GET /api/workspaces/{workspaceId}/reports/integrations/webhooks
GET /api/workspaces/{workspaceId}/reports/integrations/sync-conflicts
GET /api/workspaces/{workspaceId}/reports/integrations/failures
```

---

# 12. Authorization requirements

Required authorities:

```text
INTEGRATION_PROVIDER_VIEW

INTEGRATION_CONNECTION_VIEW
INTEGRATION_CONNECTION_CREATE
INTEGRATION_CONNECTION_UPDATE
INTEGRATION_CONNECTION_ENABLE
INTEGRATION_CONNECTION_DISABLE
INTEGRATION_CONNECTION_ARCHIVE
INTEGRATION_CONNECTION_HEALTH_CHECK

INTEGRATION_CREDENTIAL_VIEW
INTEGRATION_CREDENTIAL_CREATE
INTEGRATION_CREDENTIAL_ROTATE
INTEGRATION_CREDENTIAL_REVOKE

INTEGRATION_MAPPING_VIEW
INTEGRATION_MAPPING_CREATE
INTEGRATION_MAPPING_UPDATE
INTEGRATION_MAPPING_ARCHIVE

INTEGRATION_IMPORT_TEMPLATE_VIEW
INTEGRATION_IMPORT_JOB_VIEW
INTEGRATION_IMPORT_JOB_CREATE
INTEGRATION_IMPORT_JOB_VALIDATE
INTEGRATION_IMPORT_JOB_DRY_RUN
INTEGRATION_IMPORT_JOB_EXECUTE
INTEGRATION_IMPORT_JOB_CANCEL

INTEGRATION_EXPORT_PROFILE_VIEW
INTEGRATION_EXPORT_PROFILE_CREATE
INTEGRATION_EXPORT_PROFILE_UPDATE
INTEGRATION_EXPORT_PROFILE_ARCHIVE
INTEGRATION_EXPORT_JOB_VIEW
INTEGRATION_EXPORT_JOB_CREATE
INTEGRATION_EXPORT_JOB_CANCEL
INTEGRATION_EXPORT_JOB_DOWNLOAD
INTEGRATION_EXPORT_SENSITIVE

WEBHOOK_SUBSCRIPTION_VIEW
WEBHOOK_SUBSCRIPTION_CREATE
WEBHOOK_SUBSCRIPTION_UPDATE
WEBHOOK_SUBSCRIPTION_ENABLE
WEBHOOK_SUBSCRIPTION_DISABLE
WEBHOOK_SUBSCRIPTION_ARCHIVE
WEBHOOK_DELIVERY_VIEW
WEBHOOK_DELIVERY_RETRY

INBOUND_WEBHOOK_ENDPOINT_VIEW
INBOUND_WEBHOOK_ENDPOINT_CREATE
INBOUND_WEBHOOK_ENDPOINT_UPDATE
INBOUND_WEBHOOK_EVENT_VIEW

SYNC_JOB_VIEW
SYNC_JOB_CREATE
SYNC_JOB_UPDATE
SYNC_JOB_RUN
SYNC_JOB_ENABLE
SYNC_JOB_DISABLE
SYNC_JOB_ARCHIVE
SYNC_RUN_VIEW
SYNC_CONFLICT_VIEW
SYNC_CONFLICT_RESOLVE

INTEGRATION_DASHBOARD_VIEW
INTEGRATION_DEAD_LETTER_VIEW
INTEGRATION_DEAD_LETTER_RETRY
INTEGRATION_DEAD_LETTER_RESOLVE

INTEGRATION_REPORT_VIEW
```

Rules:

```text
1. Connection management is workspace-admin level by default.
2. Project-scoped sync/export still requires project permission.
3. Sensitive export requires export-sensitive permission and Phase 38 audit.
4. Credentials never returned raw.
5. Webhook payload configuration requires admin permission.
6. Import execution requires target object create/update permission.
```

---

# 13. Lifecycle rules

## 13.1 Connection lifecycle

```text
DRAFT → ACTIVE
ACTIVE → DEGRADED
ACTIVE/DEGRADED → EXPIRED
ACTIVE/DEGRADED/EXPIRED → DISABLED
DISABLED → ACTIVE
Any → ARCHIVED
```

## 13.2 ImportJob lifecycle

```text
CREATED → VALIDATING → VALIDATED
VALIDATED → DRY_RUN_COMPLETED
DRY_RUN_COMPLETED/VALIDATED → RUNNING
RUNNING → COMPLETED
RUNNING → COMPLETED_WITH_ERRORS
CREATED/VALIDATING/RUNNING → CANCELLED
Any → FAILED
```

## 13.3 ExportJob lifecycle

```text
CREATED → RUNNING → COMPLETED
RUNNING → FAILED
CREATED/RUNNING → CANCELLED
COMPLETED → EXPIRED
```

## 13.4 WebhookDeliveryAttempt lifecycle

```text
PENDING → SENT
PENDING/SENT? no
PENDING → FAILED → RETRYING → SENT
FAILED/RETRYING → DEAD_LETTERED
PENDING/RETRYING → CANCELLED
```

## 13.5 SyncJob lifecycle

```text
DRAFT → ACTIVE
ACTIVE → DISABLED
ACTIVE/DISABLED → ARCHIVED
```

## 13.6 SyncRun lifecycle

```text
CREATED → RUNNING → COMPLETED
RUNNING → COMPLETED_WITH_ERRORS
RUNNING → FAILED
RUNNING → CANCELLED
```

## 13.7 SyncConflict lifecycle

```text
OPEN → RESOLVED
OPEN → DISMISSED
Any → ARCHIVED
```

---

# 14. Import rules

Import pipeline:

```text
1. Create ImportJob.
2. Upload/attach source file or select provider source.
3. Select ImportTemplate and DataMappingProfile.
4. Validate schema.
5. Validate permissions.
6. Validate row data.
7. Dry-run preview.
8. Execute import.
9. Store row results.
10. Create/update ExternalIdMapping.
11. Emit events/audit.
12. Update search/report read models if needed.
```

Rules:

```text
1. Import must not bypass domain services.
2. Import must not write directly to tables unless safe/internal.
3. Controlled/finalized records require conflict handling.
4. Sensitive imported fields follow classification.
5. Partial failure must be reported.
6. Idempotency based on external ID, row key, or job id.
```

---

# 15. Export rules

Export pipeline:

```text
1. Create ExportJob.
2. Check permission and profile.
3. Build query through authorized services.
4. Apply masking/classification.
5. Generate file/feed.
6. Create ExportAuditLog.
7. Store file reference with expiration.
8. Deliver to external destination if configured.
9. Record result/failure.
```

Rules:

```text
1. Export must not bypass permissions.
2. Sensitive fields require sensitive export permission.
3. Export files expire by policy.
4. Export to external destination requires active connection.
5. Export audit is mandatory for sensitive or report exports.
```

---

# 16. Webhook rules

Outbound webhook pipeline:

```text
1. Domain event emitted.
2. EventRegistry/outbox records event.
3. Matching WebhookSubscription selected.
4. Payload built and masked.
5. Request signed.
6. Delivery attempted.
7. Retry/backoff on failure.
8. Dead-letter after max attempts.
```

Rules:

```text
1. Payload version stable.
2. Sensitive fields excluded by default.
3. Signing secret not logged.
4. Delivery attempts auditable.
5. Webhook failure does not roll back original domain transaction.
```

Inbound webhook pipeline:

```text
1. Receive request.
2. Validate endpoint and signature.
3. Store redacted payload reference.
4. Deduplicate external event id.
5. Map event to provider handler.
6. Create SyncJob/ImportJob/action event if supported.
7. Record processing result.
```

Rules:

```text
1. Invalid signatures rejected.
2. Duplicate inbound events ignored/idempotent.
3. Unknown events stored or skipped safely.
4. Provider payloads may contain sensitive data and must follow retention.
```

---

# 17. Sync rules

Sync pipeline:

```text
1. Check connection health.
2. Load SyncJob config.
3. Load cursor.
4. Fetch external changes or Scopery changes.
5. Map records.
6. Validate permissions/domain rules.
7. Detect conflicts.
8. Apply changes through domain services.
9. Update ExternalIdMapping.
10. Update cursor after successful processing.
11. Record SyncRun.
```

Rules:

```text
1. Sync must not silently overwrite locked/finalized records.
2. Cursor updates only after success.
3. Conflict strategy required.
4. Bidirectional sync must be extra conservative.
5. Failed sync should be retryable.
6. Provider rate limits respected.
```

---

# 18. Provider adapter contract

Every provider adapter should implement:

```text
providerCode()
supportedCapabilities()
validateConnection()
healthCheck()
refreshCredentialIfSupported()
executeImport()
executeExport()
executeSync()
sendWebhookOrMessageIfSupported()
handleInboundWebhookIfSupported()
mapProviderError()
```

Rules:

```text
1. Adapter must return typed errors.
2. Adapter must not leak secrets in errors.
3. Adapter must support dry-run where applicable.
4. Adapter must be testable with mocks.
```

---

# 19. Data safety rules

## 19.1 Sensitive data

Rules:

```text
1. Use Phase 38 classification for import/export/webhook/sync.
2. Mask or exclude sensitive fields by default.
3. Sensitive external export requires permission.
4. Sensitive provider payloads stored redacted or encrypted.
5. Export/download audit required.
```

## 19.2 Secrets

Rules:

```text
1. Never log raw secrets.
2. Never return raw secrets from API.
3. Never store OAuth token in config_json.
4. Credential rotation audited.
5. Revoked credentials disable dependent jobs.
```

## 19.3 External data quality

Rules:

```text
1. Imports validate required fields.
2. Unknown fields can map to custom fields if allowed.
3. Bad rows produce ImportRowResult.
4. Import should not fail entire job unless critical.
```

---

# 20. Integration with Scopery modules

## 20.1 Project/task integration

Use cases:

```text
import projects/tasks from CSV/Jira
export project/task status
sync task references
webhook task status changed
```

Rules:

```text
Import must use project/task domain services.
```

## 20.2 Document integration

Use cases:

```text
export documents to external storage
import document metadata
sync external storage file references
webhook document downloaded/updated
```

Rules:

```text
Document access and downloads must follow Document Hub and Trust rules.
```

## 20.3 Meeting/calendar integration

Use cases:

```text
sync meeting schedule to Google/Outlook calendar
import calendar event changes
notify participants
```

Rules:

```text
Calendar sync must not expose internal notes externally unless mapped.
```

## 20.4 Notification/chat integration

Use cases:

```text
send selected notifications to Slack/Teams
post digest to channel
send integration failure alerts
```

Rules:

```text
Chat notification content must be masked and permission-safe.
```

## 20.5 Reporting/BI integration

Use cases:

```text
export dashboard data to CSV/JSON
BI feed for reports
scheduled data export
```

Rules:

```text
BI export follows Phase 38 export audit and masking.
```

## 20.6 External party/CRM integration

Use cases:

```text
import contacts
sync client organization metadata
export client project summary
```

Rules:

```text
External contact personal data follows privacy controls.
```

## 20.7 Profitability/resource integration

Use cases:

```text
import revenue actuals if external system exists
import resource actual effort if external system exists
export profitability dashboard
export resource utilization report
```

Rules:

```text
Cost/profit/rate values sensitive.
```

## 20.8 Governance/trust integration

Rules:

```text
1. Integration connection changes audited.
2. Export uses ExportAuditLog.
3. Sensitive access logged.
4. Retention applies to integration payloads.
5. Legal hold can block integration deletion.
```

---

# 21. Event Registry integration

Recommended source system:

```text
SCOPERY_INTEGRATION
```

Required events:

```text
INTEGRATION_PROVIDER_SEEDED
INTEGRATION_CONNECTION_CREATED
INTEGRATION_CONNECTION_UPDATED
INTEGRATION_CONNECTION_ENABLED
INTEGRATION_CONNECTION_DISABLED
INTEGRATION_CONNECTION_ARCHIVED
INTEGRATION_CONNECTION_HEALTH_CHECKED
INTEGRATION_CONNECTION_DEGRADED
INTEGRATION_CONNECTION_AUTH_EXPIRED

INTEGRATION_CREDENTIAL_CREATED
INTEGRATION_CREDENTIAL_ROTATED
INTEGRATION_CREDENTIAL_REVOKED

DATA_MAPPING_PROFILE_CREATED
DATA_MAPPING_PROFILE_UPDATED
DATA_MAPPING_PROFILE_ARCHIVED
EXTERNAL_ID_MAPPING_CREATED
EXTERNAL_ID_MAPPING_UPDATED

IMPORT_TEMPLATE_SEEDED
IMPORT_JOB_CREATED
IMPORT_JOB_VALIDATED
IMPORT_JOB_DRY_RUN_COMPLETED
IMPORT_JOB_STARTED
IMPORT_JOB_COMPLETED
IMPORT_JOB_COMPLETED_WITH_ERRORS
IMPORT_JOB_FAILED
IMPORT_ROW_FAILED
IMPORT_CONFLICT_DETECTED

EXPORT_PROFILE_CREATED
EXPORT_PROFILE_UPDATED
EXPORT_PROFILE_ARCHIVED
EXPORT_JOB_CREATED
EXPORT_JOB_STARTED
EXPORT_JOB_COMPLETED
EXPORT_JOB_FAILED
EXPORT_JOB_EXPIRED

WEBHOOK_SUBSCRIPTION_CREATED
WEBHOOK_SUBSCRIPTION_UPDATED
WEBHOOK_SUBSCRIPTION_ENABLED
WEBHOOK_SUBSCRIPTION_DISABLED
WEBHOOK_DELIVERY_SENT
WEBHOOK_DELIVERY_FAILED
WEBHOOK_DELIVERY_DEAD_LETTERED
WEBHOOK_DELIVERY_RETRIED

INBOUND_WEBHOOK_ENDPOINT_CREATED
INBOUND_WEBHOOK_RECEIVED
INBOUND_WEBHOOK_PROCESSED
INBOUND_WEBHOOK_REJECTED

SYNC_JOB_CREATED
SYNC_JOB_UPDATED
SYNC_JOB_ENABLED
SYNC_JOB_DISABLED
SYNC_RUN_STARTED
SYNC_RUN_COMPLETED
SYNC_RUN_FAILED
SYNC_CONFLICT_CREATED
SYNC_CONFLICT_RESOLVED

PROVIDER_RATE_LIMITED
DEAD_LETTER_EVENT_CREATED
DEAD_LETTER_EVENT_RETRIED
DEAD_LETTER_EVENT_RESOLVED
INTEGRATION_REPORT_EXPORTED
```

Standard variables:

```text
actor.userId
workspace.id
project.id
provider.code
connection.id
credential.id
mappingProfile.id
importJob.id
exportJob.id
webhookSubscription.id
webhookDelivery.id
inboundEvent.id
syncJob.id
syncRun.id
syncConflict.id
deadLetter.id
objectType.code
target.id
occurredAt
traceId
```

---

# 22. Audit / activity / outbox

Audit-sensitive actions:

```text
connection created/disabled
credential created/rotated/revoked
export job created/downloaded
sensitive data exported
webhook subscription created/changed
inbound webhook rejected
sync conflict resolved
import executed
mapping changed
provider payload retained/deleted
```

Activity actions:

```text
IMPORT_JOB_COMPLETED
EXPORT_JOB_COMPLETED
SYNC_RUN_COMPLETED
SYNC_CONFLICT_CREATED
WEBHOOK_DELIVERY_DEAD_LETTERED
INTEGRATION_CONNECTION_DEGRADED
```

Outbox required for:

```text
connection degraded
import completed with errors
export failed
sync conflict created
webhook dead-lettered
credential expired
provider rate limited
```

Idempotency recommended for:

```text
import execute
export job create
webhook delivery retry
inbound webhook processing
sync run
external id mapping create
dead-letter retry
```

---

# 23. Business rules master

## 23.1 Provider/connection rules

```text
PROV-001 Provider seeded does not mean implemented.
CONN-001 Connection stores config, not raw secrets.
CONN-002 Disabled connection cannot run jobs.
CONN-003 Health check failure updates status.
```

## 23.2 Credential rules

```text
SEC-001 Raw secrets never returned.
SEC-002 Secrets not logged.
SEC-003 Revoked credential disables dependent jobs.
SEC-004 Credential rotation audited.
```

## 23.3 Mapping/import rules

```text
MAP-001 Mapping profile required for custom import.
IMPORT-001 Validate before execute.
IMPORT-002 Dry-run available.
IMPORT-003 Row result recorded.
IMPORT-004 Import uses domain services.
IMPORT-005 Controlled records not overwritten silently.
```

## 23.4 Export rules

```text
EXPORT-001 Export requires permission.
EXPORT-002 Sensitive export uses Phase 38 audit/masking.
EXPORT-003 Export file expires by policy.
EXPORT-004 External export requires active connection.
```

## 23.5 Webhook rules

```text
WEBHOOK-001 Payload signed.
WEBHOOK-002 Payload versioned.
WEBHOOK-003 Delivery retried with backoff.
WEBHOOK-004 Dead-letter after max attempts.
WEBHOOK-005 Failure does not rollback source transaction.
```

## 23.6 Sync rules

```text
SYNC-001 Sync scope/direction explicit.
SYNC-002 Cursor updates only after success.
SYNC-003 Conflict strategy required.
SYNC-004 Locked/finalized records not overwritten silently.
```

---

# 24. Error catalog

```text
INTEGRATION_PROVIDER_NOT_FOUND
INTEGRATION_PROVIDER_DISABLED
INTEGRATION_CAPABILITY_NOT_SUPPORTED
INTEGRATION_ADAPTER_NOT_IMPLEMENTED

INTEGRATION_CONNECTION_NOT_FOUND
INTEGRATION_CONNECTION_INVALID_STATUS
INTEGRATION_CONNECTION_ACCESS_DENIED
INTEGRATION_CONNECTION_HEALTH_CHECK_FAILED
INTEGRATION_CONNECTION_AUTH_EXPIRED

INTEGRATION_CREDENTIAL_NOT_FOUND
INTEGRATION_CREDENTIAL_REVOKED
INTEGRATION_CREDENTIAL_EXPIRED
INTEGRATION_SECRET_ACCESS_FAILED
INTEGRATION_SECRET_VALUE_NOT_RETURNABLE

DATA_MAPPING_PROFILE_NOT_FOUND
DATA_MAPPING_PROFILE_INVALID
EXTERNAL_ID_MAPPING_NOT_FOUND
EXTERNAL_ID_MAPPING_DUPLICATE

IMPORT_TEMPLATE_NOT_FOUND
IMPORT_JOB_NOT_FOUND
IMPORT_JOB_INVALID_STATUS
IMPORT_JOB_VALIDATION_FAILED
IMPORT_JOB_DRY_RUN_REQUIRED
IMPORT_JOB_EXECUTION_FAILED
IMPORT_ROW_VALIDATION_FAILED
IMPORT_CONFLICT_DETECTED
IMPORT_PERMISSION_DENIED

EXPORT_PROFILE_NOT_FOUND
EXPORT_JOB_NOT_FOUND
EXPORT_JOB_INVALID_STATUS
EXPORT_JOB_EXECUTION_FAILED
EXPORT_FILE_EXPIRED
EXPORT_PERMISSION_DENIED
EXPORT_SENSITIVE_PERMISSION_REQUIRED

WEBHOOK_SUBSCRIPTION_NOT_FOUND
WEBHOOK_SUBSCRIPTION_INVALID
WEBHOOK_SIGNING_SECRET_MISSING
WEBHOOK_DELIVERY_NOT_FOUND
WEBHOOK_DELIVERY_RETRY_NOT_ALLOWED
WEBHOOK_DELIVERY_DEAD_LETTERED

INBOUND_WEBHOOK_ENDPOINT_NOT_FOUND
INBOUND_WEBHOOK_SIGNATURE_INVALID
INBOUND_WEBHOOK_DUPLICATE_EVENT
INBOUND_WEBHOOK_PROCESSING_FAILED

SYNC_JOB_NOT_FOUND
SYNC_JOB_INVALID_STATUS
SYNC_JOB_MAPPING_REQUIRED
SYNC_RUN_NOT_FOUND
SYNC_RUN_FAILED
SYNC_CURSOR_UPDATE_FAILED
SYNC_CONFLICT_NOT_FOUND
SYNC_CONFLICT_RESOLUTION_REQUIRED
SYNC_LOCKED_RECORD_CONFLICT

PROVIDER_RATE_LIMITED
DEAD_LETTER_EVENT_NOT_FOUND
DEAD_LETTER_RETRY_FAILED
INTEGRATION_REPORT_ACCESS_DENIED
```

---

# 25. Required tests

## 25.1 Provider/connection tests

```text
providerSeeder_firstRun_createsDefaults
providerSeeder_secondRun_noDuplicates
createConnection_success
connectionDoesNotStoreRawSecret
disableConnection_blocksJobs
healthCheckSuccess_updatesStatus
healthCheckFailure_marksDegraded
```

## 25.2 Credential tests

```text
createCredentialReference_success
credentialApiDoesNotReturnRawSecret
rotateCredential_audited
revokeCredential_disablesDependentJobs
secretNotLoggedInError
```

## 25.3 Mapping/import tests

```text
createMappingProfile_success
validateImportJob_success
importDryRun_success
importExecuteCreatesObjectsViaDomainService
importInvalidRowsRecorded
importConflictDetectedForLockedRecord
importDoesNotBypassPermission
importSensitiveFieldClassified
```

## 25.4 Export tests

```text
createExportProfile_success
exportJobSuccess_createsFileReference
exportJobCreatesExportAudit
exportSensitiveFieldRequiresPermission
exportMasksHiddenFields
exportFileExpires
externalStorageExportRequiresActiveConnection
```

## 25.5 Webhook tests

```text
createWebhookSubscription_success
webhookPayloadSigned
webhookDeliverySuccess_recorded
webhookDeliveryFailure_retried
webhookDeadLetterAfterMaxAttempts
webhookPayloadMasksSensitiveFields
sourceTransactionNotRolledBackOnWebhookFailure
```

## 25.6 Inbound webhook tests

```text
createInboundEndpoint_success
inboundWebhookInvalidSignature_rejected
inboundWebhookDuplicateEvent_idempotent
inboundWebhookProcessed_success
unknownInboundEvent_storedSafely
```

## 25.7 Sync tests

```text
createSyncJob_success
syncRunSuccess_updatesCursor
syncRunFailure_doesNotUpdateCursor
syncConflictCreatedForBothChanged
lockedRecordSyncConflict_notOverwritten
resolveSyncConflict_success
rateLimitDelaysSyncRun
```

## 25.8 Observability tests

```text
integrationDashboardShowsConnectionHealth
dashboardShowsFailedImportsExportsWebhooks
deadLetterRetry_success
providerRateLimitState_updated
integrationReportRequiresPermission
```

## 25.9 Trust/security tests

```text
sensitiveExportUsesPhase38Audit
integrationPayloadRetentionPolicyApplied
legalHoldBlocksIntegrationPayloadDeletion
webhookSecretNotLogged
externalDataImportAudited
```

## 25.10 Authorization tests

```text
createConnectionWithoutPermission_forbidden
executeImportWithoutPermission_forbidden
downloadExportWithoutPermission_forbidden
resolveConflictWithoutPermission_forbidden
crossWorkspaceConnection_forbidden
```

## 25.11 Event tests

```text
integrationEventSeeder_firstRun_createsDefinitions
integrationEventSeeder_secondRun_noDuplicates
importCompleted_eventEmitted
exportCompleted_eventEmitted
syncConflict_eventEmitted
webhookDeadLetter_eventEmitted
```

---

# 26. Manual verification checklist

Completion file must include:

```text
1. Seed providers and capabilities.
2. Create workspace connection.
3. Attach credential reference and confirm raw secret not returned.
4. Run connection health check.
5. Create data mapping profile.
6. Create CSV import job.
7. Validate import job.
8. Run dry-run and inspect row results.
9. Execute import and confirm domain objects created.
10. Create export profile.
11. Run export job and confirm export audit.
12. Create webhook subscription.
13. Trigger event and confirm delivery attempt.
14. Force webhook failure and confirm retry/dead-letter.
15. Create inbound webhook endpoint.
16. Send valid/invalid inbound webhook.
17. Create sync job.
18. Run sync and confirm cursor behavior.
19. Create sync conflict and resolve it.
20. Confirm integration dashboard shows health/failures/conflicts.
21. Confirm sensitive data masking/export audit.
22. Confirm no provider is claimed working unless tested.
```

---

# 27. Acceptance criteria

Phase 39 is accepted only if:

```text
1. Current integration/import/export capability is classified against TO-BE.
2. IntegrationProvider implemented/seeded/tested.
3. IntegrationConnection implemented/tested.
4. CredentialReference/secret strategy implemented/tested.
5. ConnectorCapability implemented/tested.
6. ConnectionHealthCheck implemented/tested.
7. DataMappingProfile implemented/tested.
8. ExternalIdMapping implemented/tested.
9. ImportTemplate/ImportJob/ImportRowResult implemented/tested.
10. ExportProfile/ExportJob implemented/tested.
11. WebhookSubscription/WebhookDeliveryAttempt implemented/tested.
12. InboundWebhookEndpoint/Event foundation implemented/tested.
13. SyncJob/SyncRun/SyncCursor/SyncConflict implemented/tested.
14. ProviderRateLimitState and DeadLetterIntegrationEvent implemented/tested.
15. Integration dashboard/reports implemented/tested.
16. Phase 38 export/masking/audit integrated/tested.
17. IAM permissions implemented/tested.
18. Event seeders idempotent.
19. Activity/audit/outbox follows Phase 04.
20. No external provider is falsely claimed as working.
21. Raw secrets never returned/logged.
22. `mvn compile` passes.
23. `mvn test` passes.
24. Completion file exists.
```

Do not mark complete if:

```text
raw secrets are stored/logged
import bypasses domain services
export bypasses permission/masking
locked records overwritten by sync
webhook payload leaks sensitive data
cursor updates after failed sync
provider claimed working without adapter/tests
tests fail
```

---

# 28. Required phase completion file

Agent must create:

```text
docs/phase-complete/PHASE_39_INTEGRATION_IMPORT_EXPORT_CONNECTORS_TO_BE_COMPLETE.md
```

Required sections:

```text
# Phase 39 — Integration / Import / Export / Connectors Complete

## 1. Summary
## 2. Source Inputs Reviewed
## 3. Current vs TO-BE Classification Matrix
## 4. Implemented in Current BE
## 5. Implemented / Hardened in This Phase
## 6. Provider Implementation Matrix
## 7. Seed-only Providers / Deferred Providers
## 8. Product Boundary Decision
## 9. Entity Mapping
## 10. API Changes
## 11. Provider Registry Strategy
## 12. Connection / Credential Strategy
## 13. Capability Strategy
## 14. Mapping / External ID Strategy
## 15. Import Strategy
## 16. Export Strategy
## 17. Webhook Strategy
## 18. Inbound Webhook Strategy
## 19. Sync Strategy
## 20. Conflict Strategy
## 21. Rate Limit / Retry / Dead Letter Strategy
## 22. Trust / Privacy / Export Audit Integration
## 23. Notification Strategy
## 24. Reporting / Dashboard Strategy
## 25. Authorization Matrix
## 26. Activity / Audit / Outbox Notes
## 27. Idempotency Strategy
## 28. Tests Added
## 29. Commands Run
## 30. Test Results
## 31. Manual Verification
## 32. Assumptions
## 33. Deviations From Prompt
## 34. Known Risks
## 35. Future Phases That Must Return to Integrations
```

---

# 29. Future phases that may return

```text
Phase 40 — Service / Support / Maintenance:
- Support ticket integrations, incident tools, status page, helpdesk connectors.

Phase 41 — Knowledge Graph / Semantic Index:
- Semantic import, entity matching, AI mapping suggestions, integration data graph.

Mobile app backlog:
- Mobile push provider integrations.

Enterprise backlog:
- SAML/SCIM provisioning.
- SIEM export.
- DLP/security provider integration.
- Advanced data warehouse sync.
- Marketplace/public connector SDK.
```

---

# 30. Agent anti-bịa rules

The agent must not:

```text
1. Claim a provider works without adapter, auth, sync/delivery logic, tests, and manual verification.
2. Store raw secrets in config JSON.
3. Return raw credential values from API.
4. Log secrets in errors/audit.
5. Let import write directly to domain tables unsafely.
6. Let export bypass Phase 38 masking/audit.
7. Let webhook payload leak sensitive fields.
8. Update sync cursor after failed run.
9. Overwrite locked/finalized records silently.
10. Hide provider implementation status in completion file.
```

---

# 31. Prompt to give coding agent

```text
You are implementing Phase 39 — TO-BE Integration Hub, Import / Export Framework, External Connectors, Webhooks, Sync Jobs & Data Exchange.

Product direction:
Build the integration foundation first.
Provider registry, connection model, credential reference, mapping, import/export jobs, webhooks, sync jobs, conflict handling, retries, rate limits, dead letters, dashboard, audit, and tests are required.
Do not claim a provider works unless its adapter, auth, behavior, and tests exist.

Before coding, read:
- CLAUDE.md / CLAUDE.ms
- Coding_convention.md
- Phase 00–38 docs and completion files
- Current backend code, migrations, tests

Your task:
1. Compare current integration/import/export capability against this Phase 39 TO-BE spec.
2. Classify each capability with required labels.
3. Implement IntegrationProvider and ConnectorCapability seeders.
4. Implement IntegrationConnection and CredentialReference.
5. Implement ConnectionHealthCheck.
6. Implement DataMappingProfile and ExternalIdMapping.
7. Implement ImportTemplate, ImportJob, and ImportRowResult.
8. Implement ExportProfile and ExportJob with Phase 38 audit/masking.
9. Implement WebhookSubscription and WebhookDeliveryAttempt.
10. Implement InboundWebhookEndpoint and InboundWebhookEvent foundation.
11. Implement SyncJob, SyncRun, SyncCursor, and SyncConflict.
12. Implement ProviderRateLimitState and DeadLetterIntegrationEvent.
13. Implement integration dashboard/reports.
14. Integrate with IAM, Audit, Outbox, EventRegistry, Trust, Notification, Reporting, Document, Project, Task, Custom Fields where available.
15. Run mvn compile and mvn test.
16. Create docs/phase-complete/PHASE_39_INTEGRATION_IMPORT_EXPORT_CONNECTORS_TO_BE_COMPLETE.md.

Do not expose secrets, bypass domain services, bypass permissions, leak sensitive exports, silently overwrite locked records, or claim external providers are working without tests.
```

---

# 32. Quick tracking matrix

| Capability | Current backend | Phase 39 action | Later phase |
|---|---|---|---|
| Provider registry | Missing/unknown | Must implement | Marketplace later |
| Connection model | Missing/unknown | Must implement | — |
| Credential reference | Missing/unknown | Must implement | Secret manager hardening later |
| Connector capabilities | Missing/unknown | Must implement | — |
| Health checks | Missing/unknown | Must implement | — |
| Data mapping | Missing/unknown | Must implement | AI mapping Phase 41 |
| External ID mapping | Missing/unknown | Must implement | — |
| Import template | Missing/unknown | Must implement | — |
| Import job | Missing/partial | Must implement/harden | — |
| Import row result | Missing/unknown | Must implement | — |
| Export profile | Missing/partial | Must implement/harden | — |
| Export job | Missing/partial | Must implement/harden | — |
| Webhook subscription | Missing/unknown | Must implement | — |
| Webhook delivery attempt | Missing/unknown | Must implement | — |
| Inbound webhook | Missing/unknown | Must implement foundation | Provider-specific later |
| Sync job/run/cursor | Missing/unknown | Must implement | Provider-specific later |
| Sync conflict | Missing/unknown | Must implement | — |
| Rate limit state | Missing/unknown | Must implement | — |
| Dead-letter event | Missing/unknown | Must implement | — |
| Integration dashboard | Missing/unknown | Must implement | — |
| Google/Microsoft provider | Missing/unknown | Seed/adapter depending code | Later if not tested |
| Slack/Teams | Missing/unknown | Seed/adapter depending code | Later if not tested |
| BI export | Missing/unknown | Seed/framework | Later provider |
| SIEM/DLP | Missing | Defer | Enterprise backlog |
| Connector marketplace | Missing | Defer | Enterprise backlog |

---

# 33. Final principle

Phase 39 is not complete when "there is an export button."

Phase 39 is complete when Scopery can safely exchange data:

```text
provider registry
+ connection
+ credential reference
+ mapping
+ import validation
+ export masking
+ webhook delivery
+ sync cursor
+ conflict handling
+ retry/dead-letter
+ audit
= integration foundation
```

Integration is power.

Power needs permission, audit, masking, retries, and clear provider status.
