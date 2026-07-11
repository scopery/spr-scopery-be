# PHASE 29 — TO-BE External Party, Client CRM, Stakeholder, Contact, Vendor & Project Relationship Management

> Project: Scopery Backend  
> Phase: 29  
> Document type: TO-BE implementation-grade specification  
> Status: Planning / Gap-driven implementation spec  
> Roadmap group: Post-core Dynamic Work OS expansion  
> Depends on: Phase 00 — Master Backend Roadmap, Phase 01 — API Path / Security Baseline, Phase 02 — IAM Core, Phase 03 — Workspace / Organization / Team, Phase 04 — Platform Audit / Outbox / Idempotency, Phase 05 — Event Registry, Phase 06 — Notification Engine, Phase 08 — Knowledge / Document Type Catalog, Phase 09 — Project Core / WBS / Task, Phase 10 — Project Authorization, Phase 18 — Quote / Commercial Proposal, Phase 19 — Baseline / Change Request, Phase 20 — Project Events / Notifications, Phase 22 — Reporting / Dashboard / Export, Phase 23 — Core Hardening, Phase 24 — Scope / Deliverable / Acceptance, Phase 25 — RAID / Decision Management, Phase 26 — Quality / Test / Release, Phase 27 — Document Hub / Generation, Phase 28 — Application / Requirement / Traceability  
> API base: `/api`  
> Primary module: `modules/externalparty`, `modules/clientcrm`, or `modules/project/stakeholder` depending on repository architecture  
> Related modules: `workspace`, `project`, `quote`, `scope`, `deliverable`, `acceptance`, `raid`, `decision`, `quality`, `release`, `document`, `notification`, `reporting`, `iam`, `eventregistry`, future `client-portal`, `contract`, `billing`, `workflow`, `integration`, `marketing`  
> Important rule: Phase 29 introduces external party and stakeholder relationship management. It does **not** implement full sales CRM pipeline, marketing automation, invoice/billing, legal contract execution, external client portal login, vendor procurement, or public external collaboration.

---

# 0. Purpose

Phase 29 adds structured management of external organizations and people connected to projects.

Earlier phases created internal project delivery control:

```text
Project / Task / Schedule
Quote / Finance
Baseline / ChangeRequest
Scope / Deliverable / Acceptance
RAID / Decisions
Quality / Release
Document Hub
Requirements / Traceability
```

Phase 29 answers:

```text
Who is the client organization?
Who are the client contacts?
Who approves scope, quote, deliverables, requirements, releases, or decisions?
Who are vendors/partners/subcontractors?
Which stakeholders are internal vs external?
What role does each stakeholder play on each project?
What communication preferences are known?
What documents/quotes/acceptance records are associated with an external party?
Which contact can be visible in future client portal?
Which external party data is sensitive/private?
```

Phase 29 is the **external relationship and stakeholder foundation**.

It is not a full sales, marketing, billing, procurement, or portal system.

---

# 1. Source inputs

Before coding Phase 29, the agent must read:

```text
1. Current backend codebase
2. Phase 03 Workspace / Organization / Team implementation
3. Phase 09 Project Core implementation
4. Phase 10 Project Authorization implementation
5. Phase 18 Quote implementation
6. Phase 20 Notification implementation
7. Phase 22 Reporting implementation
8. Phase 23 Core Hardening completion file
9. Phase 24 Scope / Deliverable / Acceptance implementation
10. Phase 25 RAID / Decision implementation
11. Phase 26 Quality / Test / Release implementation
12. Phase 27 Document Hub implementation
13. Phase 28 Application / Requirement / Traceability implementation
14. Current IAM seeders
15. Current EventDefinition seeders
16. Existing customer/contact/client/vendor/stakeholder code if any
```

The coding agent must inspect actual code and not assume implementation.

---

# 2. Current expected backend state

After Phase 28, Scopery likely has internal project entities, but external relationships may still be weak or free-text:

```text
Project clientName maybe string
Quote recipient maybe text
Deliverable acceptedByNameSnapshot / email snapshot
Document clientVisible flag
Requirement source CLIENT_REQUEST
RAID client/vendor issue categories
Decision client-facing notes
```

Likely missing:

```text
ExternalOrganization
ExternalContact
ExternalPartyAddress
ExternalPartyRelationship
ProjectStakeholder
ProjectExternalPartyRole
ContactCommunicationPreference
ExternalPartyDocumentLink
ExternalPartyQuoteLink
ExternalPartyAcceptanceLink
ClientApprovalAuthority foundation
Vendor/partner role foundation
Stakeholder report
Client contact report
```

Phase 29 implements a structured foundation.

---

# 3. Target statement

Phase 29 must deliver:

```text
1. ExternalOrganization entity for clients, vendors, partners, subcontractors, regulators, and other external parties.
2. ExternalContact entity for people linked to external organizations.
3. Address/contact channel/preference foundation.
4. ProjectExternalPartyRelationship connecting external parties to projects.
5. ProjectStakeholder connecting internal users and external contacts to project stakeholder roles.
6. Approval authority metadata foundation for quotes, scope, deliverables, releases, decisions, and requirements.
7. Links to Quote, DeliverableAcceptance, Documents, RAID, Decisions, Requirements, Releases.
8. Privacy/sensitivity handling for external personal/contact data.
9. IAM permissions for viewing/managing external party data.
10. Events/notifications/reporting integration.
11. AI-assisted contact/stakeholder suggestions as proposal only.
12. Tests and completion report.
```

---

# 4. Boundary decisions

## 4.1 External party is not workspace organization

Scopery workspace organization is the company/team using Scopery.

ExternalOrganization is outside party:

```text
client
vendor
partner
subcontractor
regulator
consultant
```

They must not be treated as internal workspace members.

## 4.2 External contact is not user account

ExternalContact is a contact record.

It does not automatically grant login.

External client login/portal identity is Phase 30.

## 4.3 Stakeholder is a project role

ProjectStakeholder can point to:

```text
internal user
workspace member
external contact
external organization
```

It defines role/interest/influence/responsibility, not authentication.

## 4.4 CRM foundation is not sales CRM pipeline

Phase 29 can record clients/contacts/project relationships.

It does not implement:

```text
lead pipeline
campaigns
marketing automation
sales forecasting
commission
email outreach automation
```

## 4.5 Vendor role is not procurement

Phase 29 can record vendor/partner/subcontractor contacts.

It does not implement purchase orders, vendor contracts, procurement approvals, or vendor invoices.

---

# 5. Classification labels

Use these labels in completion file:

| Label | Meaning |
|---|---|
| `CURRENTLY_IMPLEMENTED` | Existing backend already supports it with tests. |
| `PARTIALLY_IMPLEMENTED` | Some support exists but gaps remain. |
| `MUST_IMPLEMENT_IN_PHASE_29` | Required now. |
| `SEED_ONLY_IN_PHASE_29` | Seed definitions/events/permissions only. |
| `DEFERRED_TO_PHASE_XX` | Later phase returns. |
| `DEFERRED_TO_POST_23_BACKLOG` | Dynamic Work OS expansion backlog. |
| `NOT_IN_SCOPE_FOR_PHASE_29` | Explicitly not in this phase. |

---

# 6. Required capabilities

---

## 6.1 EXT-001 — ExternalOrganization

Classification: `MUST_IMPLEMENT_IN_PHASE_29`

Purpose:

```text
Represent an external company/entity connected to workspace/projects.
```

Types:

```text
CLIENT
PROSPECT
VENDOR
PARTNER
SUBCONTRACTOR
CONSULTANT
REGULATOR
GOVERNMENT
PAYMENT_PROVIDER
TECHNOLOGY_PROVIDER
OTHER
```

Rules:

```text
1. ExternalOrganization belongs to workspace.
2. Name required.
3. Code unique within workspace if provided.
4. Type required.
5. Status controls active/archived relationship.
6. Sensitive/business data protected by IAM.
7. ExternalOrganization is not a Scopery workspace/internal organization.
8. Creating ExternalOrganization does not create user accounts.
```

Status:

```text
ACTIVE
INACTIVE
ON_HOLD
BLACKLISTED
ARCHIVED
```

---

## 6.2 EXT-002 — ExternalContact

Classification: `MUST_IMPLEMENT_IN_PHASE_29`

Purpose:

```text
Represent a person at an external organization.
```

Rules:

```text
1. ExternalContact belongs to workspace.
2. Can link to ExternalOrganization.
3. Name or email/phone required by policy.
4. Email normalized/lowercased where applicable.
5. Contact can be active/inactive/archived.
6. Contact is not authenticated user.
7. Personal data is sensitive and must be protected.
8. Duplicate detection recommended but not hard-block unless policy.
```

Contact roles:

```text
DECISION_MAKER
APPROVER
SPONSOR
PROJECT_CONTACT
TECHNICAL_CONTACT
FINANCE_CONTACT
LEGAL_CONTACT
UAT_CONTACT
SUPPORT_CONTACT
VENDOR_CONTACT
OTHER
```

---

## 6.3 EXT-003 — ExternalPartyAddress

Classification: `MUST_IMPLEMENT_IN_PHASE_29`

Purpose:

```text
Store organization/contact address details.
```

Address types:

```text
BILLING
SHIPPING
OFFICE
REGISTERED
PROJECT_SITE
OTHER
```

Rules:

```text
1. Address belongs to ExternalOrganization or ExternalContact.
2. Country/city/line fields optional by policy.
3. Address is not used for billing in Phase 29.
4. Precise addresses are sensitive.
```

---

## 6.4 EXT-004 — ExternalContactChannel

Classification: `MUST_IMPLEMENT_IN_PHASE_29`

Purpose:

```text
Store controlled communication channels.
```

Channel types:

```text
EMAIL
PHONE
MOBILE
WHATSAPP
TELEGRAM
WECHAT
LINE
SLACK_CONNECT
TEAMS_CONNECT
LINKEDIN
OTHER
```

Rules:

```text
1. Channel belongs to ExternalContact.
2. Channel value required.
3. Primary flag unique per contact/channel type.
4. Phase 29 records channel only; does not send messages via these channels.
5. Sensitive contact values protected by IAM.
```

---

## 6.5 EXT-005 — CommunicationPreference

Classification: `MUST_IMPLEMENT_IN_PHASE_29`

Purpose:

```text
Record preferences and restrictions for communication.
```

Preferences:

```text
preferredLanguage
preferredChannel
timezone
doNotContact
doNotContactReason
communicationNotes
allowedForProjectNotifications future
```

Rules:

```text
1. Preferences do not send notifications by themselves.
2. DoNotContact must be respected by future outreach/client portal modules.
3. External notification is deferred unless explicit module exists.
4. Preferences audited when changed.
```

---

## 6.6 REL-001 — ProjectExternalPartyRelationship

Classification: `MUST_IMPLEMENT_IN_PHASE_29`

Purpose:

```text
Link external organization/contact to project with relationship type.
```

Relationship types:

```text
CLIENT
END_CLIENT
IMPLEMENTATION_PARTNER
VENDOR
SUBCONTRACTOR
CONSULTANT
REGULATOR
PAYMENT_PROVIDER
TECH_PROVIDER
OTHER
```

Rules:

```text
1. Relationship belongs to project and workspace.
2. External party must belong to same workspace.
3. Relationship can have primary flag.
4. Only one primary client per project by policy.
5. Relationship does not grant project access.
6. Relationship can be used by quote/scope/acceptance reports.
```

---

## 6.7 STK-001 — ProjectStakeholder

Classification: `MUST_IMPLEMENT_IN_PHASE_29`

Purpose:

```text
Represent a person or party with stakeholder role in project.
```

Stakeholder can target:

```text
INTERNAL_USER
WORKSPACE_MEMBER
EXTERNAL_CONTACT
EXTERNAL_ORGANIZATION
TEAM
ROLE_PLACEHOLDER
```

Stakeholder roles:

```text
PROJECT_SPONSOR
CLIENT_SPONSOR
CLIENT_APPROVER
CLIENT_PROJECT_MANAGER
PRODUCT_OWNER
BUSINESS_OWNER
TECHNICAL_OWNER
UAT_REVIEWER
FINANCE_APPROVER
LEGAL_REVIEWER
VENDOR_OWNER
IMPLEMENTATION_PARTNER
SUPPORT_OWNER
INFORMED_PARTY
OTHER
```

Rules:

```text
1. Stakeholder belongs to project.
2. Target must be valid and in same workspace/project context.
3. Stakeholder role required.
4. Influence/interest can be recorded.
5. Stakeholder does not grant authentication/authorization.
6. Stakeholder can be used for reports and future notifications.
```

Influence/interest:

```text
LOW
MEDIUM
HIGH
CRITICAL
```

Engagement status:

```text
UNKNOWN
SUPPORTIVE
NEUTRAL
RESISTANT
BLOCKING
```

---

## 6.8 STK-002 — ApprovalAuthority foundation

Classification: `MUST_IMPLEMENT_IN_PHASE_29`

Purpose:

```text
Record who is authorized/expected to approve project artifacts externally/internally.
```

Approval areas:

```text
QUOTE
SCOPE_PACKAGE
DELIVERABLE_ACCEPTANCE
REQUIREMENT
CHANGE_REQUEST
RELEASE
DECISION
DOCUMENT
OTHER
```

Rules:

```text
1. ApprovalAuthority belongs to project.
2. Authority can target internal user or external contact.
3. Authority records area and limits.
4. It does not perform workflow approval.
5. It can be used by Phase 34 workflow later.
6. External contact authority does not create login.
```

---

## 6.9 EXT-006 — ExternalPartyDocumentLink

Classification: `MUST_IMPLEMENT_IN_PHASE_29`

Purpose:

```text
Link external party/contact to documents.
```

Examples:

```text
NDA reference
client requirements document
approval email document
company registration document
vendor security document
```

Rules:

```text
1. Document must belong to same workspace/project where applicable.
2. Link does not grant document access.
3. Restricted documents remain restricted.
4. Link type required.
```

---

## 6.10 EXT-007 — Quote / acceptance relationship integration

Classification: `MUST_IMPLEMENT_IN_PHASE_29`

Rules:

```text
1. Quote can reference client ExternalOrganization and recipient ExternalContact.
2. DeliverableAcceptance can reference ExternalContact/ApprovalAuthority instead of only snapshots.
3. Acceptance still stores name/email snapshots for historical proof.
4. Changing contact after acceptance does not rewrite historical acceptance.
5. Client relationship does not create invoice/contract.
```

---

## 6.11 EXT-008 — Vendor / partner foundation

Classification: `MUST_IMPLEMENT_IN_PHASE_29`

Purpose:

```text
Record vendors/partners/subcontractors connected to project.
```

Rules:

```text
1. Vendor/partner is ExternalOrganization type.
2. Vendor contact can be stakeholder.
3. Vendor can be linked to RAID dependency/issue.
4. Vendor can be linked to deliverable/task/release/document.
5. No procurement/PO/vendor invoice in Phase 29.
```

---

## 6.12 EXT-009 — Client-visible flag governance

Classification: `MUST_IMPLEMENT_IN_PHASE_29`

Purpose:

```text
Prepare safe data for future client portal.
```

Rules:

```text
1. Entities can mark clientVisible where relevant.
2. clientVisible does not expose externally until portal exists.
3. Future Phase 30 must re-check access.
4. Client-visible data should avoid internal margin/rates/AI prompts.
```

Applies to:

```text
documents
requirements
deliverables
release summaries
stakeholder records
project external party records
```

---

## 6.13 RPT-001 — External party / stakeholder reporting

Classification: `MUST_IMPLEMENT_IN_PHASE_29`

Reports:

```text
client profile report
project stakeholder register
external contact list
approval authority matrix
vendor/partner register
client-visible data readiness report
project relationship report
external documents report
```

Dashboard KPIs:

```text
clientCount
activeExternalContacts
projectStakeholderCount
missingPrimaryClient
missingClientApprover
pendingExternalApprovals
vendorDependencyCount
contactsWithDoNotContact
```

---

## 6.14 AI-001 — AI-assisted stakeholder suggestions

Classification: `SEED_ONLY_IN_PHASE_29` or `MUST_IMPLEMENT_IN_PHASE_29` if Phase 21 tool registry exists.

AI can suggest:

```text
stakeholder roles from project/quote/scope
missing approver roles
contact dedup candidates
client-facing summary
communication risks
approval authority gaps
```

Rules:

```text
1. AI output is proposal only.
2. Human must approve creation/update.
3. AI cannot create user accounts.
4. AI cannot send external messages.
5. AI cannot decide approval authority automatically.
6. AI must respect contact privacy and permissions.
```

---

# 7. Entity model TO-BE

---

## 7.1 ExternalOrganization — `external_organization`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
code VARCHAR(100) NULL
legal_name VARCHAR(255) NULL
display_name VARCHAR(255) NOT NULL
organization_type VARCHAR(50) NOT NULL
status VARCHAR(50) NOT NULL
website_url VARCHAR(1000) NULL
industry VARCHAR(150) NULL
tax_identifier VARCHAR(100) NULL
registration_number VARCHAR(100) NULL
notes TEXT NULL
client_visible BOOLEAN NOT NULL DEFAULT false
created_at / created_by
updated_at / updated_by
archived_at / archived_by
trace_id VARCHAR(100) NULL
version INT
```

Constraint:

```text
unique workspace_id + code where code not null
```

Sensitive:

```text
tax_identifier
registration_number
notes
```

---

## 7.2 ExternalContact — `external_contact`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
external_organization_id UUID NULL
display_name VARCHAR(255) NOT NULL
first_name VARCHAR(150) NULL
last_name VARCHAR(150) NULL
job_title VARCHAR(255) NULL
department VARCHAR(255) NULL
email VARCHAR(320) NULL
phone VARCHAR(100) NULL
status VARCHAR(50) NOT NULL
primary_role VARCHAR(100) NULL
timezone VARCHAR(100) NULL
preferred_language VARCHAR(20) NULL
notes TEXT NULL
client_visible BOOLEAN NOT NULL DEFAULT false
created_at / created_by
updated_at / updated_by
archived_at / archived_by
trace_id VARCHAR(100) NULL
version INT
```

Status:

```text
ACTIVE
INACTIVE
LEFT_ORGANIZATION
DO_NOT_CONTACT
ARCHIVED
```

---

## 7.3 ExternalPartyAddress — `external_party_address`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
external_organization_id UUID NULL
external_contact_id UUID NULL
address_type VARCHAR(50) NOT NULL
line1 VARCHAR(255) NULL
line2 VARCHAR(255) NULL
city VARCHAR(150) NULL
state_region VARCHAR(150) NULL
postal_code VARCHAR(50) NULL
country_code VARCHAR(10) NULL
primary_flag BOOLEAN NOT NULL DEFAULT false
created_at / created_by
updated_at / updated_by
archived_at / archived_by
version INT
```

Rules:

```text
external_organization_id or external_contact_id required
```

---

## 7.4 ExternalContactChannel — `external_contact_channel`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
external_contact_id UUID NOT NULL
channel_type VARCHAR(50) NOT NULL
channel_value VARCHAR(500) NOT NULL
primary_flag BOOLEAN NOT NULL DEFAULT false
verified_flag BOOLEAN NOT NULL DEFAULT false
notes TEXT NULL
created_at / created_by
updated_at / updated_by
archived_at / archived_by
version INT
```

---

## 7.5 CommunicationPreference — `external_communication_preference`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
external_organization_id UUID NULL
external_contact_id UUID NULL
preferred_channel VARCHAR(50) NULL
preferred_language VARCHAR(20) NULL
timezone VARCHAR(100) NULL
do_not_contact BOOLEAN NOT NULL DEFAULT false
do_not_contact_reason TEXT NULL
communication_notes TEXT NULL
allowed_for_project_notifications BOOLEAN NOT NULL DEFAULT false
created_at / created_by
updated_at / updated_by
version INT
```

---

## 7.6 ProjectExternalPartyRelationship — `project_external_party_relationship`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
project_id UUID NOT NULL
external_organization_id UUID NULL
external_contact_id UUID NULL
relationship_type VARCHAR(50) NOT NULL
primary_flag BOOLEAN NOT NULL DEFAULT false
status VARCHAR(50) NOT NULL
start_date DATE NULL
end_date DATE NULL
notes TEXT NULL
created_at / created_by
updated_at / updated_by
archived_at / archived_by
version INT
```

Status:

```text
ACTIVE
INACTIVE
ENDED
ARCHIVED
```

Rules:

```text
external_organization_id or external_contact_id required
```

---

## 7.7 ProjectStakeholder — `project_stakeholder`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
project_id UUID NOT NULL
target_type VARCHAR(50) NOT NULL
target_id UUID NULL
stakeholder_role VARCHAR(100) NOT NULL
influence_level VARCHAR(50) NULL
interest_level VARCHAR(50) NULL
engagement_status VARCHAR(50) NULL
responsibility_summary TEXT NULL
approval_relevant BOOLEAN NOT NULL DEFAULT false
client_visible BOOLEAN NOT NULL DEFAULT false
status VARCHAR(50) NOT NULL
created_at / created_by
updated_at / updated_by
archived_at / archived_by
trace_id VARCHAR(100) NULL
version INT
```

Target type:

```text
INTERNAL_USER
WORKSPACE_MEMBER
EXTERNAL_CONTACT
EXTERNAL_ORGANIZATION
TEAM
ROLE_PLACEHOLDER
```

Status:

```text
ACTIVE
INACTIVE
ARCHIVED
```

---

## 7.8 ApprovalAuthority — `project_approval_authority`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
project_id UUID NOT NULL
authority_target_type VARCHAR(50) NOT NULL
authority_target_id UUID NULL
approval_area VARCHAR(100) NOT NULL
approval_limit_json JSONB NULL
required BOOLEAN NOT NULL DEFAULT true
primary_flag BOOLEAN NOT NULL DEFAULT false
status VARCHAR(50) NOT NULL
notes TEXT NULL
created_at / created_by
updated_at / updated_by
archived_at / archived_by
version INT
```

Status:

```text
ACTIVE
INACTIVE
ARCHIVED
```

---

## 7.9 ExternalPartyDocumentLink — `external_party_document_link`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
project_id UUID NULL
external_organization_id UUID NULL
external_contact_id UUID NULL
document_id UUID NOT NULL
document_version_id UUID NULL
link_type VARCHAR(50) NOT NULL
created_at / created_by
archived_at / archived_by
version INT
```

Link types:

```text
PROFILE_DOCUMENT
REQUIREMENT_SOURCE
APPROVAL_EVIDENCE
LEGAL_REFERENCE
SECURITY_DOCUMENT
VENDOR_DOCUMENT
CLIENT_DOCUMENT
OTHER
```

---

## 7.10 ExternalPartyArtifactLink — optional generalized link

If project already has generic link infrastructure, use it.

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
project_id UUID NULL
external_organization_id UUID NULL
external_contact_id UUID NULL
target_type VARCHAR(100) NOT NULL
target_id UUID NOT NULL
link_type VARCHAR(50) NOT NULL
created_at / created_by
archived_at / archived_by
version INT
```

Targets:

```text
QUOTE_VERSION
DELIVERABLE_ACCEPTANCE
SCOPE_PACKAGE
REQUIREMENT
CHANGE_REQUEST
RAID_ITEM
DECISION
RELEASE_PACKAGE
DOCUMENT
```

---

# 8. API TO-BE list

All APIs use `/api`.

---

## 8.1 External organization APIs

```text
POST  /api/workspaces/{workspaceId}/external-organizations
GET   /api/workspaces/{workspaceId}/external-organizations
GET   /api/workspaces/{workspaceId}/external-organizations/{externalOrganizationId}
PUT   /api/workspaces/{workspaceId}/external-organizations/{externalOrganizationId}
PATCH /api/workspaces/{workspaceId}/external-organizations/{externalOrganizationId}/archive
```

Filters:

```text
type
status
clientVisible
search
```

---

## 8.2 External contact APIs

```text
POST  /api/workspaces/{workspaceId}/external-contacts
GET   /api/workspaces/{workspaceId}/external-contacts
GET   /api/workspaces/{workspaceId}/external-contacts/{externalContactId}
PUT   /api/workspaces/{workspaceId}/external-contacts/{externalContactId}
PATCH /api/workspaces/{workspaceId}/external-contacts/{externalContactId}/archive
```

Filters:

```text
externalOrganizationId
role
status
search
```

---

## 8.3 Address/channel/preference APIs

```text
POST   /api/workspaces/{workspaceId}/external-organizations/{externalOrganizationId}/addresses
GET    /api/workspaces/{workspaceId}/external-organizations/{externalOrganizationId}/addresses
PUT    /api/workspaces/{workspaceId}/external-addresses/{addressId}
DELETE /api/workspaces/{workspaceId}/external-addresses/{addressId}

POST   /api/workspaces/{workspaceId}/external-contacts/{externalContactId}/channels
GET    /api/workspaces/{workspaceId}/external-contacts/{externalContactId}/channels
PUT    /api/workspaces/{workspaceId}/external-contact-channels/{channelId}
DELETE /api/workspaces/{workspaceId}/external-contact-channels/{channelId}

GET /api/workspaces/{workspaceId}/external-contacts/{externalContactId}/communication-preferences
PUT /api/workspaces/{workspaceId}/external-contacts/{externalContactId}/communication-preferences
```

---

## 8.4 Project external relationship APIs

```text
POST   /api/projects/{projectId}/external-party-relationships
GET    /api/projects/{projectId}/external-party-relationships
GET    /api/projects/{projectId}/external-party-relationships/{relationshipId}
PUT    /api/projects/{projectId}/external-party-relationships/{relationshipId}
DELETE /api/projects/{projectId}/external-party-relationships/{relationshipId}
POST   /api/projects/{projectId}/external-party-relationships/{relationshipId}/mark-primary
```

---

## 8.5 Project stakeholder APIs

```text
POST   /api/projects/{projectId}/stakeholders
GET    /api/projects/{projectId}/stakeholders
GET    /api/projects/{projectId}/stakeholders/{stakeholderId}
PUT    /api/projects/{projectId}/stakeholders/{stakeholderId}
PATCH  /api/projects/{projectId}/stakeholders/{stakeholderId}/archive
```

Filters:

```text
targetType
stakeholderRole
approvalRelevant
clientVisible
status
```

---

## 8.6 Approval authority APIs

```text
POST   /api/projects/{projectId}/approval-authorities
GET    /api/projects/{projectId}/approval-authorities
GET    /api/projects/{projectId}/approval-authorities/{authorityId}
PUT    /api/projects/{projectId}/approval-authorities/{authorityId}
PATCH  /api/projects/{projectId}/approval-authorities/{authorityId}/archive
```

Filters:

```text
approvalArea
targetType
required
status
```

---

## 8.7 External party document/artifact link APIs

```text
POST   /api/workspaces/{workspaceId}/external-organizations/{externalOrganizationId}/document-links
GET    /api/workspaces/{workspaceId}/external-organizations/{externalOrganizationId}/document-links
DELETE /api/workspaces/{workspaceId}/external-organizations/{externalOrganizationId}/document-links/{linkId}

POST   /api/projects/{projectId}/external-party-artifact-links
GET    /api/projects/{projectId}/external-party-artifact-links
DELETE /api/projects/{projectId}/external-party-artifact-links/{linkId}
```

---

## 8.8 Integration convenience APIs

```text
POST /api/projects/{projectId}/quotes/{quoteId}/assign-client
POST /api/projects/{projectId}/deliverable-acceptances/{acceptanceId}/assign-external-approver
POST /api/projects/{projectId}/requirements/{requirementId}/assign-client-source
POST /api/projects/{projectId}/raid-items/{raidItemId}/link-external-party
POST /api/projects/{projectId}/decisions/{decisionId}/link-stakeholder
```

Only implement if corresponding modules exist.

---

## 8.9 Reports APIs

```text
GET /api/workspaces/{workspaceId}/reports/external-organizations
GET /api/workspaces/{workspaceId}/reports/external-contacts
GET /api/projects/{projectId}/reports/stakeholders
GET /api/projects/{projectId}/reports/approval-authority-matrix
GET /api/projects/{projectId}/reports/client-profile
GET /api/projects/{projectId}/reports/vendor-partner-register
GET /api/projects/{projectId}/reports/client-visible-readiness
```

---

# 9. Authorization requirements

Required authorities:

```text
EXTERNAL_ORGANIZATION_VIEW
EXTERNAL_ORGANIZATION_CREATE
EXTERNAL_ORGANIZATION_UPDATE
EXTERNAL_ORGANIZATION_ARCHIVE

EXTERNAL_CONTACT_VIEW
EXTERNAL_CONTACT_CREATE
EXTERNAL_CONTACT_UPDATE
EXTERNAL_CONTACT_ARCHIVE
EXTERNAL_CONTACT_SENSITIVE_VIEW

EXTERNAL_ADDRESS_VIEW
EXTERNAL_ADDRESS_MANAGE
EXTERNAL_CHANNEL_VIEW
EXTERNAL_CHANNEL_MANAGE
EXTERNAL_COMMUNICATION_PREFERENCE_VIEW
EXTERNAL_COMMUNICATION_PREFERENCE_UPDATE

PROJECT_EXTERNAL_PARTY_VIEW
PROJECT_EXTERNAL_PARTY_MANAGE
PROJECT_STAKEHOLDER_VIEW
PROJECT_STAKEHOLDER_CREATE
PROJECT_STAKEHOLDER_UPDATE
PROJECT_STAKEHOLDER_ARCHIVE

PROJECT_APPROVAL_AUTHORITY_VIEW
PROJECT_APPROVAL_AUTHORITY_MANAGE

EXTERNAL_PARTY_DOCUMENT_LINK_MANAGE
EXTERNAL_PARTY_ARTIFACT_LINK_MANAGE

EXTERNAL_PARTY_REPORT_VIEW
PROJECT_STAKEHOLDER_REPORT_VIEW
CLIENT_PROFILE_REPORT_VIEW
```

Rules:

```text
1. Workspace access required for external party master data.
2. Project access required for project relationship/stakeholder APIs.
3. Sensitive contact fields require sensitive view permission.
4. Document links require document access.
5. Assignment to quote/acceptance/requirement requires access to target artifact.
6. External contact records do not grant portal/user access.
```

---

# 10. Lifecycle rules

## 10.1 ExternalOrganization lifecycle

```text
ACTIVE → INACTIVE
ACTIVE/INACTIVE → ON_HOLD
ACTIVE/INACTIVE/ON_HOLD → BLACKLISTED
Any non-deleted → ARCHIVED
```

Rules:

```text
1. Archived organization cannot be added to new projects by default.
2. Existing project history remains.
3. Blacklisted organization cannot be assigned without override permission.
```

## 10.2 ExternalContact lifecycle

```text
ACTIVE → INACTIVE
ACTIVE/INACTIVE → LEFT_ORGANIZATION
ACTIVE/INACTIVE → DO_NOT_CONTACT
Any → ARCHIVED
```

Rules:

```text
1. DO_NOT_CONTACT blocks future outreach modules.
2. Historical approvals/acceptance snapshots remain unchanged.
3. Archived contact remains in historical references.
```

## 10.3 ProjectStakeholder lifecycle

```text
ACTIVE → INACTIVE → ARCHIVED
```

Rules:

```text
1. Stakeholder role can be changed with audit.
2. Approval-relevant stakeholders require stronger update permission.
3. Client-visible flag does not publish externally until portal exists.
```

## 10.4 ApprovalAuthority lifecycle

```text
ACTIVE → INACTIVE → ARCHIVED
```

Rules:

```text
1. Authority change audited.
2. Authority does not automatically approve anything.
3. Future workflow can consume authority records.
```

---

# 11. Integration rules

## 11.1 Project integration

Rules:

```text
1. Project can have primary client relationship.
2. Project stakeholder register pulls internal/external stakeholders.
3. Project dashboard can show primary client and missing approver warnings.
4. Client relationship does not grant project access.
```

## 11.2 Quote integration

Rules:

```text
1. Quote can reference ExternalOrganization as client.
2. Quote can reference ExternalContact as recipient/approver.
3. Quote sent/accepted does not create contract.
4. Changing client contact does not rewrite historical quote snapshots.
```

## 11.3 Scope/deliverable/acceptance integration

Rules:

```text
1. DeliverableAcceptance can reference ExternalContact.
2. Acceptance stores snapshot fields for legal/audit history.
3. ApprovalAuthority can define expected acceptor.
4. External contact cannot approve through portal until Phase 30.
```

## 11.4 RAID/decision integration

Rules:

```text
1. RAID client/vendor dependencies can link external parties.
2. Decision stakeholders can include external contacts.
3. Client/vendor issue reports can be generated.
```

## 11.5 Document integration

Rules:

```text
1. ExternalPartyDocumentLink links to DocumentHub.
2. Document access remains controlled by DocumentHub IAM.
3. External party link does not grant document access.
```

## 11.6 Requirement / traceability integration

Rules:

```text
1. RequirementSource can reference ExternalContact/Organization.
2. Client-visible requirements can be flagged.
3. Future portal review uses these records but is not Phase 29.
```

## 11.7 Notification integration

Phase 29 can notify internal users about stakeholder changes.

It must not send external notifications unless external notification model exists.

---

# 12. Reporting integration

Extend Phase 22 with:

```text
EXTERNAL_ORGANIZATION_REPORT
EXTERNAL_CONTACT_REPORT
PROJECT_STAKEHOLDER_REGISTER_REPORT
APPROVAL_AUTHORITY_MATRIX_REPORT
CLIENT_PROFILE_REPORT
VENDOR_PARTNER_REGISTER_REPORT
CLIENT_VISIBLE_READINESS_REPORT
```

Masked fields:

```text
email
phone
address
tax identifiers
private notes
communication notes
```

Rules:

```text
1. Sensitive fields masked without permission.
2. Reports apply workspace/project access.
3. Client-visible readiness does not expose externally.
```

---

# 13. AI integration

If Phase 21 tool registry exists, seed AI tools/prompts:

```text
suggestStakeholdersFromProject
suggestApprovalAuthorityGaps
summarizeClientProfile
detectPotentialDuplicateContacts
suggestClientVisibleReadinessGaps
summarizeVendorRisks
```

Rules:

```text
1. AI suggestions are proposal only.
2. Human approval required for create/update/merge.
3. AI cannot create external user accounts.
4. AI cannot send external messages.
5. AI cannot mark contact as authority automatically.
6. AI respects contact privacy permissions.
```

---

# 14. Event Registry integration

Recommended source system:

```text
SCOPERY_EXTERNAL_PARTY
```

Required events:

```text
EXTERNAL_ORGANIZATION_CREATED
EXTERNAL_ORGANIZATION_UPDATED
EXTERNAL_ORGANIZATION_ARCHIVED
EXTERNAL_ORGANIZATION_STATUS_CHANGED

EXTERNAL_CONTACT_CREATED
EXTERNAL_CONTACT_UPDATED
EXTERNAL_CONTACT_ARCHIVED
EXTERNAL_CONTACT_STATUS_CHANGED
EXTERNAL_CONTACT_MARKED_DO_NOT_CONTACT

EXTERNAL_ADDRESS_CREATED
EXTERNAL_ADDRESS_UPDATED
EXTERNAL_ADDRESS_REMOVED
EXTERNAL_CONTACT_CHANNEL_CREATED
EXTERNAL_CONTACT_CHANNEL_UPDATED
EXTERNAL_CONTACT_CHANNEL_REMOVED
COMMUNICATION_PREFERENCE_UPDATED

PROJECT_EXTERNAL_PARTY_LINKED
PROJECT_EXTERNAL_PARTY_UPDATED
PROJECT_EXTERNAL_PARTY_UNLINKED
PROJECT_PRIMARY_CLIENT_CHANGED

PROJECT_STAKEHOLDER_CREATED
PROJECT_STAKEHOLDER_UPDATED
PROJECT_STAKEHOLDER_ARCHIVED

APPROVAL_AUTHORITY_CREATED
APPROVAL_AUTHORITY_UPDATED
APPROVAL_AUTHORITY_ARCHIVED

EXTERNAL_PARTY_DOCUMENT_LINK_CREATED
EXTERNAL_PARTY_DOCUMENT_LINK_REMOVED
EXTERNAL_PARTY_ARTIFACT_LINK_CREATED
EXTERNAL_PARTY_ARTIFACT_LINK_REMOVED
```

Standard variables:

```text
actor.userId
organization.id
workspace.id
project.id
externalOrganization.id
externalContact.id
relationship.id
stakeholder.id
approvalAuthority.id
document.id
target.type
target.id
occurredAt
traceId
```

---

# 15. Audit / activity / outbox

Audit-sensitive actions:

```text
external contact created/updated/archived
contact marked do-not-contact
communication preference changed
primary client changed
approval authority changed
sensitive contact data viewed/exported
external document link created
blacklist/on-hold status changed
```

Activity actions:

```text
PROJECT_PRIMARY_CLIENT_CHANGED
PROJECT_STAKEHOLDER_CREATED
APPROVAL_AUTHORITY_CREATED
EXTERNAL_CONTACT_MARKED_DO_NOT_CONTACT
```

Outbox required for major events.

Idempotency recommended for:

```text
POST /external-organizations
POST /external-contacts
POST /projects/{id}/external-party-relationships
POST /projects/{id}/stakeholders
POST /projects/{id}/approval-authorities
POST document/artifact links
```

---

# 16. Business rules master

## 16.1 External organization rules

```text
EXT-ORG-001 Workspace required.
EXT-ORG-002 Display name required.
EXT-ORG-003 Type required.
EXT-ORG-004 Code unique within workspace if provided.
EXT-ORG-005 Archived organization cannot be added to new project by default.
EXT-ORG-006 Blacklisted organization requires override to link.
EXT-ORG-007 External organization is not internal workspace organization.
```

## 16.2 External contact rules

```text
EXT-CON-001 Display name required.
EXT-CON-002 Contact belongs to workspace.
EXT-CON-003 Contact organization must belong to same workspace.
EXT-CON-004 Contact is not user account.
EXT-CON-005 DoNotContact respected by future outreach.
EXT-CON-006 Sensitive contact fields require permission.
EXT-CON-007 Historical snapshots not rewritten by contact update.
```

## 16.3 Project relationship rules

```text
REL-001 Relationship project and external party must be same workspace.
REL-002 Only one primary client per project unless policy allows.
REL-003 Relationship does not grant project access.
REL-004 Ended relationship remains historical.
```

## 16.4 Stakeholder rules

```text
STK-001 Stakeholder role required.
STK-002 Stakeholder target must exist.
STK-003 Stakeholder does not grant authentication.
STK-004 Approval-relevant stakeholder update audited.
STK-005 Client-visible does not publish externally until portal exists.
```

## 16.5 Approval authority rules

```text
AUTH-001 Approval area required.
AUTH-002 Authority target must be valid.
AUTH-003 Authority does not execute approval.
AUTH-004 Authority change audited.
AUTH-005 Future workflow can consume authority.
```

---

# 17. Error catalog

```text
EXTERNAL_ORGANIZATION_NOT_FOUND
EXTERNAL_ORGANIZATION_CODE_ALREADY_EXISTS
EXTERNAL_ORGANIZATION_ARCHIVED
EXTERNAL_ORGANIZATION_BLACKLISTED
EXTERNAL_ORGANIZATION_ACCESS_DENIED

EXTERNAL_CONTACT_NOT_FOUND
EXTERNAL_CONTACT_ORGANIZATION_MISMATCH
EXTERNAL_CONTACT_ARCHIVED
EXTERNAL_CONTACT_DO_NOT_CONTACT
EXTERNAL_CONTACT_SENSITIVE_ACCESS_DENIED
EXTERNAL_CONTACT_ACCESS_DENIED

EXTERNAL_ADDRESS_NOT_FOUND
EXTERNAL_ADDRESS_OWNER_REQUIRED
EXTERNAL_ADDRESS_OWNER_MISMATCH

EXTERNAL_CHANNEL_NOT_FOUND
EXTERNAL_CHANNEL_DUPLICATE_PRIMARY
EXTERNAL_CHANNEL_INVALID_TYPE

COMMUNICATION_PREFERENCE_NOT_FOUND
COMMUNICATION_PREFERENCE_ACCESS_DENIED

PROJECT_EXTERNAL_PARTY_RELATIONSHIP_NOT_FOUND
PROJECT_EXTERNAL_PARTY_WORKSPACE_MISMATCH
PROJECT_EXTERNAL_PARTY_PRIMARY_CLIENT_CONFLICT
PROJECT_EXTERNAL_PARTY_ACCESS_DENIED

PROJECT_STAKEHOLDER_NOT_FOUND
PROJECT_STAKEHOLDER_TARGET_NOT_FOUND
PROJECT_STAKEHOLDER_TARGET_MISMATCH
PROJECT_STAKEHOLDER_ACCESS_DENIED

APPROVAL_AUTHORITY_NOT_FOUND
APPROVAL_AUTHORITY_TARGET_NOT_FOUND
APPROVAL_AUTHORITY_TARGET_MISMATCH
APPROVAL_AUTHORITY_ACCESS_DENIED

EXTERNAL_DOCUMENT_LINK_NOT_FOUND
EXTERNAL_DOCUMENT_LINK_TARGET_MISMATCH
EXTERNAL_ARTIFACT_LINK_NOT_FOUND
EXTERNAL_ARTIFACT_LINK_TARGET_MISMATCH

EXTERNAL_PARTY_REPORT_ACCESS_DENIED
```

---

# 18. Required tests

## 18.1 External organization tests

```text
createExternalOrganization_valid_success
createExternalOrganization_duplicateCode_rejected
updateExternalOrganization_success
archiveExternalOrganization_success
blacklistedOrganization_linkRequiresOverride
```

## 18.2 External contact tests

```text
createExternalContact_valid_success
createExternalContact_orgOtherWorkspace_rejected
updateExternalContact_success
markContactDoNotContact_success
archiveExternalContact_success
sensitiveContactFields_maskedWithoutPermission
```

## 18.3 Address/channel/preference tests

```text
createOrganizationAddress_success
createContactAddress_success
addressOwnerRequired_rejected
createContactChannel_success
primaryChannel_uniquePerType
updateCommunicationPreference_success
doNotContactChange_audited
```

## 18.4 Project relationship tests

```text
linkPrimaryClient_success
linkSecondPrimaryClient_rejected
linkVendor_success
relationshipOtherWorkspace_rejected
relationshipDoesNotGrantProjectAccess
markPrimaryClient_updatesPrevious
```

## 18.5 Stakeholder tests

```text
createInternalStakeholder_success
createExternalContactStakeholder_success
createExternalOrganizationStakeholder_success
stakeholderTargetMissing_rejected
stakeholderOtherWorkspace_rejected
approvalRelevantStakeholderUpdate_audited
stakeholderDoesNotCreateUserAccount
```

## 18.6 Approval authority tests

```text
createApprovalAuthority_externalContact_success
createApprovalAuthority_internalUser_success
authorityTargetOtherWorkspace_rejected
authorityDoesNotApproveArtifact
authorityChange_audited
```

## 18.7 Integration tests

```text
assignClientToQuote_success_ifQuoteExists
deliverableAcceptanceReferencesExternalContact_success
acceptanceSnapshotUnaffectedByContactUpdate
requirementSourceExternalContact_success
raidItemLinkVendor_success
documentLinkExternalParty_success
documentLinkDoesNotGrantDocumentAccess
```

## 18.8 Authorization tests

```text
viewExternalOrg_withoutPermission_forbidden
createExternalContact_withoutPermission_forbidden
viewSensitiveContact_withoutPermission_maskedOrForbidden
manageProjectStakeholder_withoutPermission_forbidden
manageApprovalAuthority_withoutPermission_forbidden
crossWorkspaceExternalParty_forbidden
```

## 18.9 Event/audit tests

```text
externalPartyEventSeeder_firstRun_createsDefinitions
externalPartyEventSeeder_secondRun_noDuplicates
primaryClientChanged_eventEmitted
approvalAuthorityUpdated_auditCreated
doNotContact_eventEmitted
sensitiveContactExport_auditCreated
```

---

# 19. Manual verification checklist

Completion file must include:

```text
1. Create client ExternalOrganization.
2. Create client ExternalContact.
3. Add contact channels and preferences.
4. Link client as primary client to project.
5. Add contact as ProjectStakeholder.
6. Add contact as ApprovalAuthority for Deliverable Acceptance.
7. Link client/contact to quote and deliverable acceptance if modules exist.
8. Link document to external party.
9. Confirm contact is not a user account and cannot log in.
10. Confirm relationship/stakeholder does not grant project access.
11. Confirm sensitive fields masked without permission.
12. Generate stakeholder register and approval authority report.
13. Confirm no client portal/billing/contract/marketing CRM is falsely claimed.
```

---

# 20. Acceptance criteria

Phase 29 is accepted only if:

```text
1. Current external party/stakeholder capability is classified against TO-BE.
2. ExternalOrganization implemented/tested.
3. ExternalContact implemented/tested.
4. Address/channel/preference foundation implemented/tested.
5. ProjectExternalPartyRelationship implemented/tested.
6. ProjectStakeholder implemented/tested.
7. ApprovalAuthority foundation implemented/tested.
8. Document/artifact link integration implemented/tested.
9. Quote/acceptance/requirement/RAID integration implemented/tested or explicitly deferred if modules unavailable.
10. Sensitive contact field protection implemented/tested.
11. IAM permissions implemented/tested.
12. Event seeders idempotent.
13. Activity/audit/outbox follows Phase 04.
14. Reports implemented/tested.
15. No client portal, user account creation, billing, contract execution, marketing CRM, procurement is falsely claimed.
16. `mvn compile` passes.
17. `mvn test` passes.
18. Completion file exists.
```

Do not mark complete if:

```text
external contact automatically creates user account
stakeholder grants project permission
relationship grants project access
sensitive contact fields leak
primary client conflict allowed when policy forbids
document link grants document access
client portal is claimed
billing/contract/procurement is claimed
tests fail
```

---

# 21. Required phase completion file

Agent must create:

```text
docs/phase-complete/PHASE_29_EXTERNAL_PARTY_CLIENT_STAKEHOLDER_TO_BE_COMPLETE.md
```

Required sections:

```text
# Phase 29 — External Party / Client / Stakeholder Complete

## 1. Summary
## 2. Source Inputs Reviewed
## 3. Current vs TO-BE Classification Matrix
## 4. Implemented in Current BE
## 5. Implemented / Hardened in This Phase
## 6. Seed-only Items Added
## 7. Deferred Items and Target Phase
## 8. External Party Boundary Decision
## 9. Entity Mapping
## 10. API Changes
## 11. ExternalOrganization Strategy
## 12. ExternalContact Strategy
## 13. Address / Channel / Preference Strategy
## 14. Project Relationship Strategy
## 15. Stakeholder Strategy
## 16. ApprovalAuthority Strategy
## 17. Quote / Acceptance / Requirement Integration
## 18. Document / RAID / Decision Integration
## 19. Privacy / Sensitive Field Strategy
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
## 32. Future Phases That Must Return to External Party / Stakeholder
```

---

# 22. Future phases that must return

```text
Phase 30 — Customer / External Collaboration Portal:
- External contact login, client portal access, client review/approval.

Phase 31 — Meetings / Collaboration:
- Meeting participants, stakeholder attendance, action items.

Phase 34 — Workflow / Approval:
- Approval routing using ApprovalAuthority.

Phase 35 — Advanced Notifications:
- External notification preferences, reminders, digest.

Phase 36 — Contract / Billing / Revenue:
- Contract party, billing contact, invoice recipient, legal entity.

Phase 38 — Audit / Compliance / Privacy:
- Data retention, consent, privacy export/delete, audit around personal data.

Phase 39 — Integration / Import / Export:
- CRM import/sync, contact import, email/contact provider integration.

Phase 40 — Service / Support / Maintenance:
- Support contacts, SLA contacts, escalation contacts.

Phase 41 — Knowledge Graph / Semantic Index:
- Relationship graph across stakeholders, decisions, risks, contracts, documents.
```

---

# 23. Agent anti-bịa rules

The agent must not:

```text
1. Treat external contact as authenticated user.
2. Grant project access from stakeholder or relationship record.
3. Claim client portal exists.
4. Claim external contact can approve through portal.
5. Claim billing/invoice/contract exists.
6. Claim marketing CRM pipeline exists.
7. Claim procurement/vendor PO exists.
8. Expose sensitive contact fields without permission.
9. Rewrite historical acceptance/quote snapshots when contact changes.
10. Let document links bypass document access.
11. Hide deferred client portal/contract/billing/workflow/privacy gaps.
```

---

# 24. Prompt to give coding agent

```text
You are implementing Phase 29 — TO-BE External Party, Client CRM, Stakeholder, Contact, Vendor & Project Relationship Management.

This is not an as-is documentation task.

Before coding, read:
- CLAUDE.md / CLAUDE.ms
- Coding_convention.md
- Phase 00–28 docs and completion files
- Current backend code, migrations, tests

Your task:
1. Compare current external party/client/stakeholder capability against this Phase 29 TO-BE spec.
2. Classify each capability with required labels.
3. Implement ExternalOrganization, ExternalContact, ExternalPartyAddress, ExternalContactChannel, CommunicationPreference.
4. Implement ProjectExternalPartyRelationship and ProjectStakeholder.
5. Implement ApprovalAuthority foundation.
6. Implement document/artifact links and integrate with Quote, Acceptance, Requirement, RAID, Decision where available.
7. Add sensitive field masking/privacy protection.
8. Add IAM permissions, events, reports, notifications, audit/outbox, idempotency, and tests.
9. Run mvn compile and mvn test.
10. Create docs/phase-complete/PHASE_29_EXTERNAL_PARTY_CLIENT_STAKEHOLDER_TO_BE_COMPLETE.md.

Do not implement or claim external client portal login, user account creation for contacts, marketing CRM pipeline, billing/invoicing, legal contract execution, procurement/vendor PO, external email automation, or workflow approval in this phase.
```

---

# 25. Quick tracking matrix

| Capability | Current backend | Phase 29 action | Later phase |
|---|---|---|---|
| ExternalOrganization | Missing/unknown | Must implement | Contract/Billing Phase 36 |
| ExternalContact | Missing/unknown | Must implement | Portal identity Phase 30 |
| Address | Missing/unknown | Must implement basic | Privacy Phase 38 |
| ContactChannel | Missing/unknown | Must implement basic | Notifications/Integration Phase 35/39 |
| CommunicationPreference | Missing/unknown | Must implement basic | External notifications Phase 35 |
| ProjectExternalPartyRelationship | Missing/unknown | Must implement | — |
| ProjectStakeholder | Missing/unknown | Must implement | Meetings Phase 31 |
| ApprovalAuthority | Missing/unknown | Must implement foundation | Workflow Phase 34 |
| Document/artifact links | Missing/unknown | Must implement | Knowledge graph Phase 41 |
| Quote client link | Missing/partial | Must implement if Quote exists | Contract Phase 36 |
| Acceptance external approver | Missing/partial | Must implement if Phase 24 exists | Portal Phase 30 |
| Client portal login | Missing | Defer | Phase 30 |
| External approval workflow | Missing | Defer | Phase 34/30 |
| Billing contact/invoice recipient | Missing | Defer | Phase 36 |
| Marketing CRM pipeline | Missing | Defer | Post-23 CRM backlog |
| Vendor procurement | Missing | Defer | Procurement backlog / Phase 36+ |
| Privacy retention/consent | Missing | Defer | Phase 38 |

---

# 26. Final principle

Phase 29 is not complete when "a client name can be stored."

Phase 29 is complete when Scopery can govern external relationships:

```text
external organization
+ external contact
+ project relationship
+ stakeholder role
+ approval authority
+ linked artifacts
+ privacy controls
+ audit
= trusted project stakeholder management
```

External contact is not user account.

Stakeholder is not permission.

Relationship is not access.

ApprovalAuthority is not workflow.

ClientVisible is not client portal.

Client data is sensitive by default.
