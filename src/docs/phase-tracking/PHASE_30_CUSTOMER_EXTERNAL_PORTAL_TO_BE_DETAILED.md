# PHASE 30 — TO-BE Customer / External Collaboration Portal, Client Access, Review, Approval & UAT Gateway

> Project: Scopery Backend  
> Phase: 30  
> Document type: TO-BE implementation-grade specification  
> Status: Planning / Gap-driven implementation spec  
> Roadmap group: Post-core Dynamic Work OS expansion  
> Depends on: Phase 00 — Master Backend Roadmap, Phase 01 — API Path / Security Baseline, Phase 02 — IAM Core, Phase 03 — Workspace / Organization / Team, Phase 04 — Platform Audit / Outbox / Idempotency, Phase 05 — Event Registry, Phase 06 — Notification Engine, Phase 08 — Knowledge / Document Type Catalog, Phase 09 — Project Core / WBS / Task, Phase 10 — Project Authorization, Phase 18 — Quote / Commercial Proposal, Phase 19 — Baseline / Change Request, Phase 20 — Project Events / Notifications, Phase 22 — Reporting / Dashboard / Export, Phase 23 — Core Hardening, Phase 24 — Scope / Deliverable / Acceptance, Phase 26 — Quality / Test / Release, Phase 27 — Document Hub / Generation, Phase 28 — Application / Requirement / Traceability, Phase 29 — External Party / Client CRM / Stakeholder  
> API base: `/api`  
> Primary module: `modules/clientportal`, `modules/externalportal`, or `modules/project/clientportal` depending on repository architecture  
> Related modules: `iam`, `externalparty`, `project`, `scope`, `deliverable`, `acceptance`, `requirement`, `quality`, `document`, `quote`, `notification`, `reporting`, `audit`, `eventregistry`, future `workflow`, `contract`, `billing`, `support`, `integration`  
> Important rule: Phase 30 introduces controlled external/client portal access. It does **not** implement billing portal, payment, legal e-signature, contract execution, public anonymous sharing, marketing CRM, full support desk, or full workflow automation.

---

# 0. Purpose

Phase 30 turns external contacts from passive records into controlled portal participants.

Earlier phases introduced:

```text
ExternalOrganization
ExternalContact
ProjectStakeholder
ApprovalAuthority
clientVisible flags
Deliverables / Acceptance
Requirements / Traceability
Quality / UAT Test Runs
Documents / Generated Documents
Quotes
Notifications
Reports
```

Phase 30 answers:

```text
Can an external client contact log into a restricted portal?
Which projects can they see?
Which documents can they view/download?
Which deliverables can they review or accept?
Which requirements can they review/comment on?
Can they participate in UAT?
Can they view quote/proposal documents?
Can they give feedback or request changes?
Can they approve anything without becoming an internal user?
How do we audit every external view/download/action?
How do we prevent external users from seeing internal cost, margin, rate, AI prompt, internal risk, or private notes?
```

Phase 30 is the **external collaboration and client-facing access gateway**.

---

# 1. Source inputs

Before coding Phase 30, the agent must read:

```text
1. Current backend codebase
2. Phase 01 API/Security baseline implementation
3. Phase 02 IAM implementation
4. Phase 03 Workspace/Organization/Team implementation
5. Phase 04 Audit/Outbox/Idempotency implementation
6. Phase 06 Notification implementation
7. Phase 09 Project Core implementation
8. Phase 10 Project Authorization implementation
9. Phase 18 Quote implementation
10. Phase 20 Project Notifications implementation
11. Phase 22 Reporting implementation
12. Phase 23 Core Hardening completion file
13. Phase 24 Scope / Deliverable / Acceptance implementation
14. Phase 26 Quality / Test / Release implementation
15. Phase 27 Document Hub implementation
16. Phase 28 Requirement / Traceability implementation
17. Phase 29 External Party / Client CRM / Stakeholder implementation
18. Current IAM seeders
19. Current EventDefinition seeders
20. Current notification rule seeders
21. Existing external portal/client access code if any
```

The coding agent must inspect actual code and not assume implementation.

---

# 2. Current expected backend state

After Phase 29, the backend should have:

```text
ExternalOrganization
ExternalContact
ProjectExternalPartyRelationship
ProjectStakeholder
ApprovalAuthority
clientVisible flags
Documents and links
Deliverables and acceptance records
Requirement and traceability records
Test/UAT foundation
Quote/proposal documents
Project reporting
```

Likely missing:

```text
ExternalIdentity
ExternalPortalAccount
ExternalPortalInvite
ExternalProjectAccessGrant
ExternalPortalSession policy
ExternalPortalPermission model
ClientPortalViewPolicy
ClientPortalDocumentAccess
ClientReviewRequest
ClientReviewDecision
ClientComment
ClientFeedback
ClientUatAssignment
ExternalNotificationPreference
ExternalAuditLog extensions
Client portal API surface
```

Phase 30 implements the secure external portal foundation.

---

# 3. Target statement

Phase 30 must deliver:

```text
1. External portal identity/account foundation linked to ExternalContact.
2. Invite and activation flow for external portal users.
3. Project-level external access grants.
4. External portal role/permission model.
5. Client-visible data policy and field masking.
6. Portal APIs for external project overview.
7. Portal APIs for documents, deliverables, acceptance, requirements, UAT, quote/proposal, and feedback.
8. Client review request / decision model.
9. Client comments and feedback foundation.
10. External audit logging for all portal actions.
11. Notifications for portal invite, review request, decision, feedback, and access changes.
12. Reporting around external access and client actions.
13. Tests for external access isolation, masking, and audit.
14. Completion report with explicit deferrals.
```

---

# 4. Boundary decisions

## 4.1 External portal user is not internal workspace user

An external portal account is not an internal workspace member.

It must not receive internal permissions by default.

Relationship:

```text
ExternalContact → ExternalPortalAccount → ExternalProjectAccessGrant
```

Not:

```text
ExternalContact → WorkspaceMember
```

## 4.2 Portal access is explicit

External users only see projects/artifacts with explicit grants and client-visible flags.

Rules:

```text
1. Project relationship alone does not grant portal access.
2. Stakeholder role alone does not grant portal access.
3. ApprovalAuthority alone does not grant portal access.
4. Portal access grant is required.
```

## 4.3 Client-visible does not equal public

`clientVisible=true` means eligible for portal exposure.

It is not public sharing.

Actual portal visibility requires:

```text
external account
+ active project access grant
+ artifact client-visible
+ artifact permission/scope
+ status policy
```

## 4.4 External review is not full workflow engine

Phase 30 can support simple review/decision.

Multi-step approval workflow, escalation, delegation, and SLA engine are Phase 34.

## 4.5 No billing/payment/contract portal

Phase 30 can show quote/proposal documents if allowed.

It does not implement:

```text
invoice payment
payment status
billing portal
contract execution
e-signature
legal amendment signing
```

---

# 5. Classification labels

Use these labels in completion file:

| Label | Meaning |
|---|---|
| `CURRENTLY_IMPLEMENTED` | Existing backend already supports it with tests. |
| `PARTIALLY_IMPLEMENTED` | Some support exists but gaps remain. |
| `MUST_IMPLEMENT_IN_PHASE_30` | Required now. |
| `SEED_ONLY_IN_PHASE_30` | Seed definitions/events/permissions only. |
| `DEFERRED_TO_PHASE_XX` | Later phase returns. |
| `DEFERRED_TO_POST_23_BACKLOG` | Dynamic Work OS expansion backlog. |
| `NOT_IN_SCOPE_FOR_PHASE_30` | Explicitly not in this phase. |

---

# 6. Required capabilities

---

## 6.1 POR-001 — ExternalPortalAccount

Classification: `MUST_IMPLEMENT_IN_PHASE_30`

Purpose:

```text
Represent an external user's portal login identity.
```

Rules:

```text
1. ExternalPortalAccount belongs to workspace.
2. Must link to ExternalContact.
3. Does not create WorkspaceMember.
4. Email required and unique per workspace unless policy allows multi-account.
5. Status controls login.
6. Password/MFA/auth strategy must follow security baseline.
7. Internal user tokens and external portal tokens must be distinguishable.
8. External account cannot call internal APIs unless explicitly allowed by portal API adapter.
```

Status:

```text
INVITED
ACTIVE
SUSPENDED
LOCKED
DEACTIVATED
ARCHIVED
```

---

## 6.2 POR-002 — ExternalPortalInvite

Classification: `MUST_IMPLEMENT_IN_PHASE_30`

Purpose:

```text
Invite external contact to activate portal access.
```

Rules:

```text
1. Invite belongs to ExternalContact and workspace.
2. Invite token must be random, hashed at rest, and expiring.
3. Invite can be accepted once.
4. Invite can be revoked.
5. Invite acceptance creates/activates portal account.
6. Invite email sending uses Notification Engine if email is configured.
7. Invite does not grant project access unless access grants exist.
```

Invite status:

```text
PENDING
ACCEPTED
EXPIRED
REVOKED
FAILED
```

Security:

```text
Token hash stored.
Raw token only sent once.
Rate limit acceptance.
Audit all invite operations.
```

---

## 6.3 POR-003 — ExternalProjectAccessGrant

Classification: `MUST_IMPLEMENT_IN_PHASE_30`

Purpose:

```text
Explicitly grant external account access to a project.
```

Grant roles:

```text
CLIENT_VIEWER
CLIENT_REVIEWER
CLIENT_APPROVER
CLIENT_UAT_TESTER
CLIENT_DOCUMENT_VIEWER
CLIENT_PROJECT_SPONSOR
VENDOR_COLLABORATOR
EXTERNAL_AUDITOR
```

Rules:

```text
1. Grant belongs to project, workspace, and external portal account/contact.
2. Grant must have role.
3. Grant can have expiration.
4. Grant can be suspended/revoked.
5. Grant does not expose non-client-visible artifacts.
6. Grant does not allow internal cost/margin/rate/AI prompt/private note access.
7. Grant changes audited.
```

Status:

```text
ACTIVE
SUSPENDED
REVOKED
EXPIRED
ARCHIVED
```

---

## 6.4 POR-004 — ExternalPortalPermissionPolicy

Classification: `MUST_IMPLEMENT_IN_PHASE_30`

Purpose:

```text
Map portal roles to allowed portal capabilities.
```

Capabilities:

```text
VIEW_PROJECT_OVERVIEW
VIEW_DELIVERABLES
REVIEW_DELIVERABLE
ACCEPT_DELIVERABLE
VIEW_REQUIREMENTS
COMMENT_REQUIREMENT
REVIEW_REQUIREMENT
VIEW_DOCUMENTS
DOWNLOAD_DOCUMENTS
VIEW_QUOTE_PROPOSAL
VIEW_UAT_TESTS
EXECUTE_UAT_TEST
SUBMIT_FEEDBACK
VIEW_RELEASE_SUMMARY
VIEW_CLIENT_REPORTS
```

Rules:

```text
1. Portal permission model separate from internal IAM.
2. Portal policy cannot grant internal permissions.
3. Internal admin can manage external grants if authorized.
4. Capability checks happen on every portal API.
```

---

## 6.5 POR-005 — ClientPortalVisibilityPolicy

Classification: `MUST_IMPLEMENT_IN_PHASE_30`

Purpose:

```text
Determine what external user can see.
```

Visibility conditions:

```text
1. External account active.
2. Project grant active.
3. Artifact belongs to granted project/workspace.
4. Artifact clientVisible=true or explicitly shared to portal.
5. Artifact status is allowed for portal.
6. Portal role has capability.
7. Sensitive fields masked.
```

Fields always hidden from external portal:

```text
internal cost
labor cost
billing/cost rate
gross margin
profit before tax
internal quote margin
internal finance scenario
AI prompt/raw output
internal notes
private capacity/leave details
security secrets
internal audit logs
internal staff-only RAID notes
```

---

## 6.6 POR-006 — Portal project overview API

Classification: `MUST_IMPLEMENT_IN_PHASE_30`

Purpose:

```text
External-facing project summary.
```

Allowed sections:

```text
project name
project status
client-visible milestones
client-visible deliverables
client-visible requirements
client-visible release summaries
open review requests
accepted deliverables
client-visible documents
UAT assignments
```

Rules:

```text
1. No internal task/cost/margin details by default.
2. Only client-visible task summaries if explicitly allowed.
3. Health score external variant must hide internal cause details.
4. External overview read is audited.
```

---

## 6.7 REV-001 — ClientReviewRequest

Classification: `MUST_IMPLEMENT_IN_PHASE_30`

Purpose:

```text
Request external review/approval/feedback on an artifact.
```

Review target types:

```text
DELIVERABLE
ACCEPTANCE_REPORT
REQUIREMENT
DOCUMENT
QUOTE_PROPOSAL
RELEASE_SUMMARY
UAT_TEST_RUN
CHANGE_REQUEST_SUMMARY
DECISION_SUMMARY
OTHER
```

Review types:

```text
REVIEW
APPROVAL
ACCEPTANCE
UAT_CONFIRMATION
FEEDBACK
ACKNOWLEDGEMENT
```

Rules:

```text
1. Target must be client-visible or explicitly portal-shared.
2. Review request assigned to external portal account/contact.
3. Due date optional.
4. Request can be cancelled.
5. Decision captured in ClientReviewDecision.
6. Review request does not bypass internal approval workflow.
```

Status:

```text
DRAFT
SENT
VIEWED
RESPONDED
OVERDUE
CANCELLED
EXPIRED
```

---

## 6.8 REV-002 — ClientReviewDecision

Classification: `MUST_IMPLEMENT_IN_PHASE_30`

Purpose:

```text
Capture external user's response.
```

Decision outcomes:

```text
APPROVED
ACCEPTED
REJECTED
CHANGES_REQUESTED
ACKNOWLEDGED
COMMENT_ONLY
```

Rules:

```text
1. Decision belongs to ReviewRequest.
2. External actor must be assigned or authorized.
3. Rejection/changes requested requires reason.
4. Decision creates audit/event.
5. Decision can update linked deliverable acceptance if target and authority policy allow.
6. Decision does not create invoice, contract, or legal e-signature.
```

---

## 6.9 COM-001 — ClientComment

Classification: `MUST_IMPLEMENT_IN_PHASE_30`

Purpose:

```text
Allow external comments/feedback on client-visible artifacts.
```

Targets:

```text
DELIVERABLE
REQUIREMENT
DOCUMENT
UAT_TEST_RUN
RELEASE_SUMMARY
QUOTE_PROPOSAL
CHANGE_REQUEST_SUMMARY
```

Rules:

```text
1. External user must have project grant and capability.
2. Comment target must be visible.
3. Comments are visible to internal authorized users.
4. Internal-only comments remain separate and hidden.
5. Comment edit/delete policy must be explicit.
6. Comments audited.
```

---

## 6.10 FBK-001 — ClientFeedback

Classification: `MUST_IMPLEMENT_IN_PHASE_30`

Purpose:

```text
Capture structured client feedback that may become issue, defect, requirement, or change request.
```

Feedback types:

```text
GENERAL
BUG_REPORT
CHANGE_REQUEST
QUESTION
UAT_FEEDBACK
ACCEPTANCE_FEEDBACK
DOCUMENT_FEEDBACK
RELEASE_FEEDBACK
```

Status:

```text
NEW
TRIAGED
ACCEPTED
REJECTED
CONVERTED_TO_REQUIREMENT
CONVERTED_TO_DEFECT
CONVERTED_TO_CHANGE_REQUEST
CLOSED
ARCHIVED
```

Rules:

```text
1. Feedback belongs to project and external account/contact.
2. Internal triage required before conversion.
3. Conversion creates linked artifact using domain action.
4. Feedback does not automatically mutate scope.
5. Feedback can create notification to project team.
```

---

## 6.11 UAT-001 — Client UAT assignment

Classification: `MUST_IMPLEMENT_IN_PHASE_30` if Phase 26 exists.

Purpose:

```text
Allow external UAT testers to view assigned UAT test runs/cases and submit results.
```

Rules:

```text
1. External tester must have CLIENT_UAT_TESTER role.
2. Only UAT-designated TestRuns/TestCases are visible.
3. External result creates TestCaseResult or ClientUatResult according to architecture.
4. Failed UAT can create feedback/defect after internal triage.
5. External user cannot edit internal test plan/case definitions.
6. External user cannot close defects.
```

---

## 6.12 DOC-001 — Portal document access

Classification: `MUST_IMPLEMENT_IN_PHASE_30` if Phase 27 exists.

Rules:

```text
1. Only client-visible or explicitly portal-shared documents visible.
2. Document download requires portal document capability.
3. Restricted/internal documents hidden.
4. Download audited as external access.
5. Document link does not grant access without portal policy.
6. Public anonymous document sharing is not Phase 30.
```

---

## 6.13 REQ-001 — Portal requirement review

Classification: `MUST_IMPLEMENT_IN_PHASE_30` if Phase 28 exists.

Rules:

```text
1. Only client-visible requirements are visible.
2. External reviewers can comment/acknowledge/approve if assigned.
3. Approval updates review decision, not internal requirement approval unless configured with authority and internal action.
4. Requirement changes requested become feedback/change request draft.
```

---

## 6.14 DLV-001 — Portal deliverable review/acceptance

Classification: `MUST_IMPLEMENT_IN_PHASE_30` if Phase 24 exists.

Rules:

```text
1. Only client-visible deliverables visible.
2. Assigned approver can accept/reject through ReviewDecision.
3. Acceptance can update DeliverableAcceptance only when:
   - Review target is deliverable/acceptance
   - External contact has ApprovalAuthority
   - Grant role allows CLIENT_APPROVER
   - Internal policy permits portal acceptance
4. Acceptance stores snapshot of external actor name/email/time.
5. Acceptance does not create invoice/revenue.
```

---

## 6.15 QTE-001 — Portal quote/proposal view

Classification: `MUST_IMPLEMENT_IN_PHASE_30` if Phase 18/27 exists.

Rules:

```text
1. Only generated proposal document or client-safe quote summary visible.
2. Internal margin/cost/rate hidden.
3. External quote approval/acceptance can be captured as ReviewDecision only.
4. Contract execution/e-sign deferred.
5. Quote acceptance does not create contract/invoice.
```

---

## 6.16 RPT-001 — Portal reporting

Classification: `MUST_IMPLEMENT_IN_PHASE_30`

Reports:

```text
external access grant report
portal invite report
client review status report
client feedback report
portal document download report
client UAT participation report
external audit report
client-visible readiness report
```

---

## 6.17 AI-001 — AI-assisted portal preparation

Classification: `SEED_ONLY_IN_PHASE_30` or `MUST_IMPLEMENT_IN_PHASE_30` if Phase 21 tool registry exists.

AI can suggest:

```text
client-visible summary
portal readiness gaps
missing approvers
review request draft
feedback triage summary
UAT feedback categorization
```

Rules:

```text
1. AI uses only data actor can access.
2. AI output is proposal only.
3. AI cannot invite external users automatically.
4. AI cannot grant portal access.
5. AI cannot approve/accept on behalf of client.
```

---

# 7. Entity model TO-BE

---

## 7.1 ExternalPortalAccount — `external_portal_account`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
external_contact_id UUID NOT NULL
email VARCHAR(320) NOT NULL
display_name VARCHAR(255) NOT NULL
status VARCHAR(50) NOT NULL
auth_provider VARCHAR(50) NULL
password_hash VARCHAR(500) NULL
mfa_enabled BOOLEAN NOT NULL DEFAULT false
last_login_at TIMESTAMP NULL
failed_login_count INT NOT NULL DEFAULT 0
locked_until TIMESTAMP NULL
accepted_terms_at TIMESTAMP NULL
created_at / created_by
updated_at / updated_by
deactivated_at / deactivated_by
trace_id VARCHAR(100) NULL
version INT
```

Constraint:

```text
unique workspace_id + email
unique workspace_id + external_contact_id
```

---

## 7.2 ExternalPortalInvite — `external_portal_invite`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
external_contact_id UUID NOT NULL
external_portal_account_id UUID NULL
email VARCHAR(320) NOT NULL
token_hash VARCHAR(500) NOT NULL
status VARCHAR(50) NOT NULL
expires_at TIMESTAMP NOT NULL
accepted_at TIMESTAMP NULL
accepted_by_account_id UUID NULL
revoked_at TIMESTAMP NULL
revoked_by UUID NULL
revocation_reason TEXT NULL
created_at / created_by
trace_id VARCHAR(100) NULL
version INT
```

---

## 7.3 ExternalProjectAccessGrant — `external_project_access_grant`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
project_id UUID NOT NULL
external_portal_account_id UUID NOT NULL
external_contact_id UUID NOT NULL
grant_role VARCHAR(100) NOT NULL
status VARCHAR(50) NOT NULL
expires_at TIMESTAMP NULL
granted_at TIMESTAMP NOT NULL
granted_by UUID NOT NULL
suspended_at TIMESTAMP NULL
suspended_by UUID NULL
revoked_at TIMESTAMP NULL
revoked_by UUID NULL
revocation_reason TEXT NULL
created_at / updated_at
trace_id VARCHAR(100) NULL
version INT
```

Constraint:

```text
unique active project_id + external_portal_account_id + grant_role
```

---

## 7.4 ExternalPortalPermissionPolicy — `external_portal_permission_policy`

Fields:

```text
id UUID PK
workspace_id UUID NULL
grant_role VARCHAR(100) NOT NULL
capability VARCHAR(150) NOT NULL
enabled BOOLEAN NOT NULL DEFAULT true
created_at / created_by
updated_at / updated_by
version INT
```

Constraint:

```text
unique workspace_id + grant_role + capability
```

---

## 7.5 ClientReviewRequest — `client_review_request`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
project_id UUID NOT NULL
review_type VARCHAR(50) NOT NULL
target_type VARCHAR(100) NOT NULL
target_id UUID NOT NULL
title VARCHAR(255) NOT NULL
message TEXT NULL
assigned_external_contact_id UUID NOT NULL
assigned_external_portal_account_id UUID NULL
status VARCHAR(50) NOT NULL
due_date DATE NULL
sent_at TIMESTAMP NULL
viewed_at TIMESTAMP NULL
responded_at TIMESTAMP NULL
cancelled_at TIMESTAMP NULL
cancelled_by UUID NULL
created_at / created_by
updated_at / updated_by
trace_id VARCHAR(100) NULL
version INT
```

---

## 7.6 ClientReviewDecision — `client_review_decision`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
project_id UUID NOT NULL
review_request_id UUID NOT NULL
external_portal_account_id UUID NOT NULL
external_contact_id UUID NOT NULL
outcome VARCHAR(50) NOT NULL
reason TEXT NULL
notes TEXT NULL
accepted_name_snapshot VARCHAR(255) NULL
accepted_email_snapshot VARCHAR(320) NULL
decided_at TIMESTAMP NOT NULL
created_at TIMESTAMP NOT NULL
trace_id VARCHAR(100) NULL
version INT
```

---

## 7.7 ClientComment — `client_comment`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
project_id UUID NOT NULL
external_portal_account_id UUID NOT NULL
external_contact_id UUID NOT NULL
target_type VARCHAR(100) NOT NULL
target_id UUID NOT NULL
body TEXT NOT NULL
status VARCHAR(50) NOT NULL
created_at TIMESTAMP NOT NULL
edited_at TIMESTAMP NULL
archived_at TIMESTAMP NULL
archived_by UUID NULL
trace_id VARCHAR(100) NULL
version INT
```

Status:

```text
ACTIVE
EDITED
ARCHIVED
```

---

## 7.8 ClientFeedback — `client_feedback`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
project_id UUID NOT NULL
external_portal_account_id UUID NOT NULL
external_contact_id UUID NOT NULL
feedback_type VARCHAR(50) NOT NULL
target_type VARCHAR(100) NULL
target_id UUID NULL
title VARCHAR(255) NOT NULL
description TEXT NOT NULL
status VARCHAR(50) NOT NULL
priority VARCHAR(50) NULL
triaged_at TIMESTAMP NULL
triaged_by UUID NULL
triage_notes TEXT NULL
converted_target_type VARCHAR(100) NULL
converted_target_id UUID NULL
created_at TIMESTAMP NOT NULL
updated_at TIMESTAMP NULL
closed_at TIMESTAMP NULL
closed_by UUID NULL
trace_id VARCHAR(100) NULL
version INT
```

---

## 7.9 ClientUatAssignment — `client_uat_assignment`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
project_id UUID NOT NULL
test_run_id UUID NOT NULL
test_case_id UUID NULL
external_portal_account_id UUID NOT NULL
external_contact_id UUID NOT NULL
status VARCHAR(50) NOT NULL
assigned_at TIMESTAMP NOT NULL
assigned_by UUID NOT NULL
due_date DATE NULL
completed_at TIMESTAMP NULL
created_at / updated_at
version INT
```

Status:

```text
ASSIGNED
IN_PROGRESS
SUBMITTED
ACCEPTED_INTERNAL
REJECTED_INTERNAL
CANCELLED
```

---

## 7.10 ExternalPortalAuditLog — `external_portal_audit_log`

Fields:

```text
id UUID PK
workspace_id UUID NOT NULL
project_id UUID NULL
external_portal_account_id UUID NULL
external_contact_id UUID NULL
action VARCHAR(150) NOT NULL
target_type VARCHAR(100) NULL
target_id UUID NULL
access_result VARCHAR(50) NOT NULL
ip_address VARCHAR(100) NULL
user_agent VARCHAR(500) NULL
details_json JSONB NULL
created_at TIMESTAMP NOT NULL
trace_id VARCHAR(100) NULL
```

Actions:

```text
LOGIN_SUCCESS
LOGIN_FAILED
VIEW_PROJECT
VIEW_DOCUMENT
DOWNLOAD_DOCUMENT
VIEW_DELIVERABLE
SUBMIT_REVIEW_DECISION
CREATE_COMMENT
CREATE_FEEDBACK
VIEW_REQUIREMENT
SUBMIT_UAT_RESULT
ACCESS_DENIED
```

---

# 8. API TO-BE list

All APIs use `/api`.

Important:

```text
Internal admin APIs and external portal APIs should be separated by path and security policy.
Recommended external path: /api/portal/...
```

---

## 8.1 Internal portal management APIs

```text
POST  /api/workspaces/{workspaceId}/external-portal/accounts
GET   /api/workspaces/{workspaceId}/external-portal/accounts
GET   /api/workspaces/{workspaceId}/external-portal/accounts/{accountId}
PATCH /api/workspaces/{workspaceId}/external-portal/accounts/{accountId}/suspend
PATCH /api/workspaces/{workspaceId}/external-portal/accounts/{accountId}/activate
PATCH /api/workspaces/{workspaceId}/external-portal/accounts/{accountId}/deactivate

POST  /api/workspaces/{workspaceId}/external-portal/invites
GET   /api/workspaces/{workspaceId}/external-portal/invites
POST  /api/workspaces/{workspaceId}/external-portal/invites/{inviteId}/revoke
POST  /api/workspaces/{workspaceId}/external-portal/invites/{inviteId}/resend
```

---

## 8.2 Internal project grant APIs

```text
POST   /api/projects/{projectId}/external-access-grants
GET    /api/projects/{projectId}/external-access-grants
GET    /api/projects/{projectId}/external-access-grants/{grantId}
PATCH  /api/projects/{projectId}/external-access-grants/{grantId}/suspend
PATCH  /api/projects/{projectId}/external-access-grants/{grantId}/reactivate
PATCH  /api/projects/{projectId}/external-access-grants/{grantId}/revoke
```

---

## 8.3 External portal auth APIs

```text
POST /api/portal/auth/accept-invite
POST /api/portal/auth/login
POST /api/portal/auth/logout
POST /api/portal/auth/refresh
GET  /api/portal/auth/me
POST /api/portal/auth/change-password
POST /api/portal/auth/request-password-reset
POST /api/portal/auth/reset-password
```

Security rules:

```text
1. Portal auth routes must not authenticate as internal users.
2. Portal tokens/scopes distinguish external principal.
3. Portal refresh cookie path should be separate, e.g. /api/portal/auth.
4. Rate limits required for invite/login/reset.
```

---

## 8.4 External portal project APIs

```text
GET /api/portal/projects
GET /api/portal/projects/{projectId}
GET /api/portal/projects/{projectId}/overview
GET /api/portal/projects/{projectId}/timeline
GET /api/portal/projects/{projectId}/reviews
GET /api/portal/projects/{projectId}/feedback
```

---

## 8.5 Portal deliverable/acceptance APIs

```text
GET  /api/portal/projects/{projectId}/deliverables
GET  /api/portal/projects/{projectId}/deliverables/{deliverableId}
POST /api/portal/projects/{projectId}/deliverables/{deliverableId}/comments
POST /api/portal/projects/{projectId}/deliverables/{deliverableId}/review-decision
```

---

## 8.6 Portal requirement APIs

```text
GET  /api/portal/projects/{projectId}/requirements
GET  /api/portal/projects/{projectId}/requirements/{requirementId}
POST /api/portal/projects/{projectId}/requirements/{requirementId}/comments
POST /api/portal/projects/{projectId}/requirements/{requirementId}/review-decision
```

---

## 8.7 Portal document APIs

```text
GET /api/portal/projects/{projectId}/documents
GET /api/portal/projects/{projectId}/documents/{documentId}
GET /api/portal/projects/{projectId}/documents/{documentId}/download
```

---

## 8.8 Portal quote/proposal APIs

```text
GET  /api/portal/projects/{projectId}/quotes
GET  /api/portal/projects/{projectId}/quotes/{quoteId}
GET  /api/portal/projects/{projectId}/proposal-documents
POST /api/portal/projects/{projectId}/quotes/{quoteId}/review-decision
```

---

## 8.9 Portal UAT APIs

```text
GET  /api/portal/projects/{projectId}/uat/assignments
GET  /api/portal/projects/{projectId}/uat/assignments/{assignmentId}
POST /api/portal/projects/{projectId}/uat/assignments/{assignmentId}/start
POST /api/portal/projects/{projectId}/uat/assignments/{assignmentId}/submit-result
```

---

## 8.10 Portal feedback APIs

```text
POST /api/portal/projects/{projectId}/feedback
GET  /api/portal/projects/{projectId}/feedback
GET  /api/portal/projects/{projectId}/feedback/{feedbackId}
```

---

## 8.11 Internal review/feedback management APIs

```text
POST /api/projects/{projectId}/client-review-requests
GET  /api/projects/{projectId}/client-review-requests
GET  /api/projects/{projectId}/client-review-requests/{reviewRequestId}
POST /api/projects/{projectId}/client-review-requests/{reviewRequestId}/send
POST /api/projects/{projectId}/client-review-requests/{reviewRequestId}/cancel

GET  /api/projects/{projectId}/client-feedback
POST /api/projects/{projectId}/client-feedback/{feedbackId}/triage
POST /api/projects/{projectId}/client-feedback/{feedbackId}/convert-to-requirement
POST /api/projects/{projectId}/client-feedback/{feedbackId}/convert-to-defect
POST /api/projects/{projectId}/client-feedback/{feedbackId}/convert-to-change-request
POST /api/projects/{projectId}/client-feedback/{feedbackId}/close
```

---

## 8.12 Portal reports APIs

```text
GET /api/projects/{projectId}/reports/external-access-grants
GET /api/projects/{projectId}/reports/client-review-status
GET /api/projects/{projectId}/reports/client-feedback
GET /api/projects/{projectId}/reports/client-uat
GET /api/projects/{projectId}/reports/external-portal-audit
GET /api/projects/{projectId}/reports/client-visible-readiness
```

---

# 9. Authorization requirements

## 9.1 Internal authorities

```text
EXTERNAL_PORTAL_ACCOUNT_VIEW
EXTERNAL_PORTAL_ACCOUNT_CREATE
EXTERNAL_PORTAL_ACCOUNT_UPDATE
EXTERNAL_PORTAL_ACCOUNT_SUSPEND
EXTERNAL_PORTAL_ACCOUNT_DEACTIVATE

EXTERNAL_PORTAL_INVITE_CREATE
EXTERNAL_PORTAL_INVITE_VIEW
EXTERNAL_PORTAL_INVITE_REVOKE
EXTERNAL_PORTAL_INVITE_RESEND

EXTERNAL_PROJECT_ACCESS_GRANT_VIEW
EXTERNAL_PROJECT_ACCESS_GRANT_CREATE
EXTERNAL_PROJECT_ACCESS_GRANT_UPDATE
EXTERNAL_PROJECT_ACCESS_GRANT_SUSPEND
EXTERNAL_PROJECT_ACCESS_GRANT_REVOKE

CLIENT_REVIEW_REQUEST_VIEW
CLIENT_REVIEW_REQUEST_CREATE
CLIENT_REVIEW_REQUEST_SEND
CLIENT_REVIEW_REQUEST_CANCEL
CLIENT_REVIEW_DECISION_VIEW

CLIENT_FEEDBACK_VIEW
CLIENT_FEEDBACK_TRIAGE
CLIENT_FEEDBACK_CONVERT
CLIENT_FEEDBACK_CLOSE

CLIENT_UAT_ASSIGNMENT_MANAGE
EXTERNAL_PORTAL_AUDIT_VIEW
EXTERNAL_PORTAL_REPORT_VIEW
```

## 9.2 Portal capabilities

```text
PORTAL_VIEW_PROJECT_OVERVIEW
PORTAL_VIEW_DELIVERABLES
PORTAL_REVIEW_DELIVERABLE
PORTAL_ACCEPT_DELIVERABLE
PORTAL_VIEW_REQUIREMENTS
PORTAL_COMMENT_REQUIREMENT
PORTAL_REVIEW_REQUIREMENT
PORTAL_VIEW_DOCUMENTS
PORTAL_DOWNLOAD_DOCUMENTS
PORTAL_VIEW_QUOTE_PROPOSAL
PORTAL_REVIEW_QUOTE
PORTAL_VIEW_UAT
PORTAL_EXECUTE_UAT
PORTAL_SUBMIT_FEEDBACK
PORTAL_VIEW_RELEASE_SUMMARY
PORTAL_CREATE_COMMENT
```

Rules:

```text
1. Internal authorities apply to internal `/api/projects/...` management APIs.
2. Portal capabilities apply to `/api/portal/...` APIs.
3. External principal must never be accepted as internal WorkspaceMember.
4. Portal grants must be checked on every portal request.
5. Sensitive internal fields must never appear in portal responses.
```

---

# 10. Lifecycle rules

## 10.1 External account lifecycle

```text
INVITED → ACTIVE
ACTIVE → SUSPENDED
SUSPENDED → ACTIVE
ACTIVE/SUSPENDED → DEACTIVATED
Any → ARCHIVED
```

Rules:

```text
1. DEACTIVATED cannot log in.
2. SUSPENDED cannot access project data.
3. Account changes audited.
```

## 10.2 Invite lifecycle

```text
PENDING → ACCEPTED
PENDING → EXPIRED
PENDING → REVOKED
PENDING → FAILED
```

Rules:

```text
1. Invite can be accepted once.
2. Expired/revoked invite rejected.
3. Token hash stored, raw token never stored.
```

## 10.3 Access grant lifecycle

```text
ACTIVE → SUSPENDED
SUSPENDED → ACTIVE
ACTIVE/SUSPENDED → REVOKED
ACTIVE → EXPIRED
Any → ARCHIVED
```

Rules:

```text
1. Revoked grant blocks future project access.
2. Expired grant treated as inactive.
3. Grant changes audited.
```

## 10.4 Review request lifecycle

```text
DRAFT → SENT → VIEWED → RESPONDED
SENT/VIEWED → OVERDUE
DRAFT/SENT/VIEWED → CANCELLED
SENT/VIEWED → EXPIRED
```

Rules:

```text
1. Decision only allowed for SENT/VIEWED.
2. Assigned external user required.
3. Decision target must remain visible.
```

## 10.5 Feedback lifecycle

```text
NEW → TRIAGED → ACCEPTED → CONVERTED_TO_REQUIREMENT / CONVERTED_TO_DEFECT / CONVERTED_TO_CHANGE_REQUEST
NEW/TRIAGED → REJECTED
Any non-final → CLOSED
```

Rules:

```text
1. Conversion requires internal permission.
2. Conversion creates linked artifact via domain action.
3. Feedback does not mutate scope directly.
```

---

# 11. Security and privacy rules

## 11.1 Principal separation

External portal principal must be separate from internal user principal.

Rules:

```text
1. External principal type stored in security context.
2. Internal endpoints reject external principal unless explicitly allowed.
3. Portal endpoints reject internal principal unless impersonation/support mode explicitly implemented and audited.
4. Token claims distinguish `principalType=EXTERNAL_PORTAL`.
```

## 11.2 Field masking

Always hide from external portal:

```text
internal cost
rate card
labor rate
billing rate
margin
PBT
finance scenario internals
internal comments
internal-only documents
internal RAID notes
AI prompt/raw output/context snapshot
security audit logs
workspace member private data
capacity/leave details
```

## 11.3 Access audit

Audit all:

```text
login attempts
project views
document downloads
review decisions
acceptance decisions
feedback submitted
UAT results submitted
access denied events
grant changes
```

## 11.4 Rate limiting

Recommended for:

```text
login
accept invite
password reset
download
feedback submit
comments submit
```

---

# 12. Integration rules

## 12.1 External Party integration

Rules:

```text
1. ExternalPortalAccount requires ExternalContact.
2. Contact status DO_NOT_CONTACT should block invite by default unless override.
3. Contact update does not rewrite historical portal actions.
4. Portal account deactivation does not delete contact.
```

## 12.2 Deliverable / Acceptance integration

Rules:

```text
1. Portal acceptance writes ClientReviewDecision.
2. If policy allows, it creates/updates DeliverableAcceptance.
3. Acceptance stores external actor snapshot.
4. Acceptance does not create invoice/revenue.
```

## 12.3 Requirement integration

Rules:

```text
1. Portal review can add comments and decisions.
2. Changes requested become ClientFeedback.
3. Internal requirement approval remains internal unless workflow configured.
```

## 12.4 Quality / UAT integration

Rules:

```text
1. External user can submit UAT result only for assigned UAT assignment.
2. Failed UAT can create ClientFeedback.
3. Internal team triages feedback into defect/change/request.
4. External user cannot close defects.
```

## 12.5 Document integration

Rules:

```text
1. Portal document view/download uses DocumentHub.
2. Document must be clientVisible or explicitly portal-shared.
3. Download audited in both DocumentAccessLog and ExternalPortalAuditLog if possible.
```

## 12.6 Quote integration

Rules:

```text
1. Portal can show client-safe proposal document.
2. Internal margin/cost hidden.
3. Client quote decision recorded as ReviewDecision.
4. Contract/e-sign deferred.
```

## 12.7 Notification integration

Notifications:

```text
portal invite sent
portal account activated
project access granted/revoked
client review requested
client review decision submitted
client feedback submitted
UAT result submitted
document downloaded by client
access denied suspicious event
```

External emails require Notification Engine and communication preference policy.

---

# 13. Reporting integration

Extend Phase 22 with:

```text
EXTERNAL_ACCESS_GRANT_REPORT
PORTAL_INVITE_REPORT
CLIENT_REVIEW_STATUS_REPORT
CLIENT_FEEDBACK_REPORT
CLIENT_UAT_REPORT
PORTAL_DOCUMENT_DOWNLOAD_REPORT
EXTERNAL_PORTAL_AUDIT_REPORT
CLIENT_VISIBLE_READINESS_REPORT
```

Dashboard KPIs:

```text
activePortalAccounts
pendingInvites
activeProjectGrants
pendingClientReviews
overdueClientReviews
clientFeedbackNew
uatAssignmentsPending
externalDownloadsThisPeriod
accessDeniedEvents
```

---

# 14. AI integration

AI can assist internal users with:

```text
portal readiness checklist
client-visible summary draft
review request draft
client feedback triage suggestion
UAT feedback classification
missing external approver detection
```

Rules:

```text
1. AI suggestions are proposal only.
2. AI cannot send invite.
3. AI cannot grant access.
4. AI cannot accept/reject as client.
5. AI cannot expose internal-only data in client-visible summary.
```

---

# 15. Event Registry integration

Recommended source system:

```text
SCOPERY_CLIENT_PORTAL
```

Required events:

```text
EXTERNAL_PORTAL_ACCOUNT_CREATED
EXTERNAL_PORTAL_ACCOUNT_ACTIVATED
EXTERNAL_PORTAL_ACCOUNT_SUSPENDED
EXTERNAL_PORTAL_ACCOUNT_DEACTIVATED

EXTERNAL_PORTAL_INVITE_CREATED
EXTERNAL_PORTAL_INVITE_SENT
EXTERNAL_PORTAL_INVITE_ACCEPTED
EXTERNAL_PORTAL_INVITE_EXPIRED
EXTERNAL_PORTAL_INVITE_REVOKED

EXTERNAL_PROJECT_ACCESS_GRANTED
EXTERNAL_PROJECT_ACCESS_SUSPENDED
EXTERNAL_PROJECT_ACCESS_REVOKED
EXTERNAL_PROJECT_ACCESS_EXPIRED

PORTAL_LOGIN_SUCCESS
PORTAL_LOGIN_FAILED
PORTAL_PROJECT_VIEWED
PORTAL_DOCUMENT_VIEWED
PORTAL_DOCUMENT_DOWNLOADED
PORTAL_ACCESS_DENIED

CLIENT_REVIEW_REQUEST_CREATED
CLIENT_REVIEW_REQUEST_SENT
CLIENT_REVIEW_REQUEST_VIEWED
CLIENT_REVIEW_DECISION_SUBMITTED
CLIENT_REVIEW_REQUEST_OVERDUE
CLIENT_REVIEW_REQUEST_CANCELLED

CLIENT_COMMENT_CREATED
CLIENT_FEEDBACK_SUBMITTED
CLIENT_FEEDBACK_TRIAGED
CLIENT_FEEDBACK_CONVERTED_TO_REQUIREMENT
CLIENT_FEEDBACK_CONVERTED_TO_DEFECT
CLIENT_FEEDBACK_CONVERTED_TO_CHANGE_REQUEST
CLIENT_FEEDBACK_CLOSED

CLIENT_UAT_ASSIGNED
CLIENT_UAT_STARTED
CLIENT_UAT_RESULT_SUBMITTED
```

Standard variables:

```text
actor.userId
externalPortalAccount.id
externalContact.id
workspace.id
project.id
reviewRequest.id
reviewDecision.id
feedback.id
document.id
target.type
target.id
grant.id
occurredAt
traceId
```

---

# 16. Audit / activity / outbox

Audit-sensitive actions:

```text
external account activated/deactivated
portal invite sent/revoked
project access grant created/revoked
portal login failed repeatedly
document downloaded by external user
review decision submitted
deliverable accepted via portal
feedback converted to requirement/defect/change request
access denied event
```

Activity actions:

```text
CLIENT_REVIEW_DECISION_SUBMITTED
CLIENT_FEEDBACK_SUBMITTED
CLIENT_UAT_RESULT_SUBMITTED
EXTERNAL_PROJECT_ACCESS_GRANTED
```

Outbox required for major portal events.

Idempotency recommended for:

```text
POST /external-portal/invites
POST /external-access-grants
POST /client-review-requests
POST /portal/.../review-decision
POST /portal/.../feedback
POST /portal/.../uat/.../submit-result
feedback conversion endpoints
```

---

# 17. Business rules master

## 17.1 Account/invite rules

```text
POR-ACC-001 External account must link to ExternalContact.
POR-ACC-002 External account is not WorkspaceMember.
POR-ACC-003 Email unique per workspace.
POR-ACC-004 Suspended/deactivated accounts cannot access portal.
POR-INV-001 Invite token hashed at rest.
POR-INV-002 Invite expires.
POR-INV-003 Invite accepted once.
POR-INV-004 DoNotContact contact cannot be invited unless override.
```

## 17.2 Access grant rules

```text
POR-GRT-001 Project grant required for portal project access.
POR-GRT-002 Grant must be active and unexpired.
POR-GRT-003 Grant role maps to portal capabilities.
POR-GRT-004 Grant does not expose non-client-visible data.
POR-GRT-005 Grant does not grant internal permissions.
```

## 17.3 Visibility rules

```text
VIS-001 clientVisible is eligibility, not public access.
VIS-002 Artifact must belong to granted project.
VIS-003 Artifact status must allow portal exposure.
VIS-004 Internal-sensitive fields always hidden.
VIS-005 Document downloads audited.
```

## 17.4 Review/feedback rules

```text
REV-001 Review target must be visible.
REV-002 Review decision requires assigned/authorized external actor.
REV-003 Rejection/changes requested requires reason.
REV-004 Portal acceptance can update DeliverableAcceptance only if policy allows.
FBK-001 Feedback conversion requires internal permission.
FBK-002 Feedback conversion uses domain actions.
```

## 17.5 UAT rules

```text
UAT-001 External UAT requires assignment.
UAT-002 External user cannot edit test case definitions.
UAT-003 External failed UAT creates feedback, not direct defect closure.
```

---

# 18. Error catalog

```text
EXTERNAL_PORTAL_ACCOUNT_NOT_FOUND
EXTERNAL_PORTAL_ACCOUNT_EMAIL_ALREADY_EXISTS
EXTERNAL_PORTAL_ACCOUNT_CONTACT_REQUIRED
EXTERNAL_PORTAL_ACCOUNT_NOT_ACTIVE
EXTERNAL_PORTAL_ACCOUNT_SUSPENDED
EXTERNAL_PORTAL_ACCOUNT_DEACTIVATED
EXTERNAL_PORTAL_ACCESS_DENIED

EXTERNAL_PORTAL_INVITE_NOT_FOUND
EXTERNAL_PORTAL_INVITE_EXPIRED
EXTERNAL_PORTAL_INVITE_REVOKED
EXTERNAL_PORTAL_INVITE_ALREADY_ACCEPTED
EXTERNAL_PORTAL_INVITE_INVALID_TOKEN
EXTERNAL_PORTAL_INVITE_CONTACT_DO_NOT_CONTACT

EXTERNAL_PROJECT_ACCESS_GRANT_NOT_FOUND
EXTERNAL_PROJECT_ACCESS_GRANT_INACTIVE
EXTERNAL_PROJECT_ACCESS_GRANT_EXPIRED
EXTERNAL_PROJECT_ACCESS_GRANT_DUPLICATE
EXTERNAL_PROJECT_ACCESS_GRANT_ROLE_INVALID

PORTAL_CAPABILITY_DENIED
PORTAL_PROJECT_ACCESS_DENIED
PORTAL_ARTIFACT_NOT_VISIBLE
PORTAL_DOCUMENT_ACCESS_DENIED
PORTAL_DOCUMENT_DOWNLOAD_DENIED

CLIENT_REVIEW_REQUEST_NOT_FOUND
CLIENT_REVIEW_REQUEST_INVALID_STATUS
CLIENT_REVIEW_REQUEST_TARGET_NOT_VISIBLE
CLIENT_REVIEW_DECISION_NOT_ALLOWED
CLIENT_REVIEW_DECISION_REASON_REQUIRED

CLIENT_COMMENT_NOT_FOUND
CLIENT_COMMENT_TARGET_NOT_VISIBLE
CLIENT_FEEDBACK_NOT_FOUND
CLIENT_FEEDBACK_INVALID_STATUS
CLIENT_FEEDBACK_CONVERSION_NOT_ALLOWED

CLIENT_UAT_ASSIGNMENT_NOT_FOUND
CLIENT_UAT_ASSIGNMENT_NOT_ALLOWED
CLIENT_UAT_RESULT_INVALID

PORTAL_INTERNAL_DATA_LEAK_BLOCKED
```

---

# 19. Required tests

## 19.1 External account/invite tests

```text
createPortalAccount_valid_success
createPortalAccount_doesNotCreateWorkspaceMember
createInvite_valid_success
inviteStoresTokenHash_notRawToken
acceptInvite_valid_activatesAccount
acceptInvite_expired_rejected
acceptInvite_revoked_rejected
inviteDoNotContact_rejectedUnlessOverride
portalLogin_active_success
portalLogin_suspended_rejected
```

## 19.2 Access grant tests

```text
createProjectGrant_valid_success
createProjectGrant_duplicate_rejected
portalProjectAccess_withoutGrant_forbidden
portalProjectAccess_revokedGrant_forbidden
portalProjectAccess_expiredGrant_forbidden
grantDoesNotExposeInternalData
grantDoesNotGrantInternalApiAccess
```

## 19.3 Visibility/masking tests

```text
portalOverview_clientVisibleOnly
portalOverview_hidesInternalCostMarginRate
portalDocuments_clientVisibleOnly
portalDownloadDocument_requiresCapability
portalRequirement_hidesInternalOnlyRequirement
portalQuote_hidesMarginAndCost
portalAccessDenied_audited
```

## 19.4 Review decision tests

```text
createClientReviewRequest_valid_success
sendClientReviewRequest_success
portalViewReviewRequest_success
submitReviewDecision_approved_success
submitReviewDecision_rejected_requiresReason
reviewDecision_wrongExternalUser_forbidden
portalAcceptance_updatesDeliverableAcceptance_ifPolicyAllows
portalAcceptance_doesNotCreateInvoice
portalAcceptance_doesNotCreateContract
```

## 19.5 Comment/feedback tests

```text
portalCreateComment_visibleTarget_success
portalCreateComment_hiddenTarget_forbidden
portalSubmitFeedback_success
triageFeedback_success
convertFeedbackToRequirement_success
convertFeedbackToDefect_success
convertFeedbackToChangeRequest_success
feedbackConversion_requiresInternalPermission
feedbackDoesNotMutateScopeDirectly
```

## 19.6 UAT tests

```text
createClientUatAssignment_success
portalViewAssignedUat_success
portalViewUnassignedUat_forbidden
submitUatResult_success
failedUatCreatesFeedback_ifConfigured
externalUserCannotCloseDefect
```

## 19.7 Authorization/security tests

```text
externalTokenCannotCallInternalProjectApi
internalTokenCannotCallPortalAsExternal_unlessSupportMode
portalTokenPrincipalTypeExternal
portalRefreshCookiePathCorrect
portalPasswordResetRateLimited
portalDownloadRateLimited
```

## 19.8 Event/audit tests

```text
clientPortalEventSeeder_firstRun_createsDefinitions
clientPortalEventSeeder_secondRun_noDuplicates
portalLoginSuccess_eventEmitted
projectAccessGranted_eventEmitted
clientReviewDecision_eventEmitted
clientFeedbackSubmitted_eventEmitted
externalDocumentDownload_auditCreated
```

---

# 20. Manual verification checklist

Completion file must include:

```text
1. Create ExternalContact.
2. Create portal invite.
3. Accept invite and activate external account.
4. Grant project access as CLIENT_REVIEWER.
5. Login as external portal user.
6. View portal project overview.
7. Confirm internal cost/margin/rate/AI/internal notes hidden.
8. View client-visible document.
9. Download document and confirm external audit log.
10. Create client review request for deliverable.
11. Submit approval/rejection as external user.
12. Confirm decision recorded and internal users notified.
13. Submit client feedback.
14. Convert feedback to requirement/defect/change request internally.
15. Assign UAT test and submit result if Phase 26 exists.
16. Revoke project grant and confirm portal access denied.
17. Confirm external account is not WorkspaceMember.
18. Confirm no billing/contract/e-sign/public sharing is falsely claimed.
```

---

# 21. Acceptance criteria

Phase 30 is accepted only if:

```text
1. Current external portal capability is classified against TO-BE.
2. ExternalPortalAccount implemented/tested.
3. ExternalPortalInvite implemented/tested.
4. ExternalProjectAccessGrant implemented/tested.
5. Portal capability policy implemented/tested.
6. Client visibility/masking policy implemented/tested.
7. Portal project/document/deliverable/requirement/quote/feedback APIs implemented/tested as applicable.
8. ClientReviewRequest and ClientReviewDecision implemented/tested.
9. ClientComment and ClientFeedback implemented/tested.
10. UAT assignment integration implemented/tested or explicitly deferred if Phase 26 unavailable.
11. External portal audit implemented/tested.
12. Portal token/principal separation implemented/tested.
13. IAM/internal management permissions implemented/tested.
14. Event seeders idempotent.
15. Notifications/reporting integrated/tested.
16. No billing/payment/contract/e-sign/public sharing/full workflow/support desk is falsely claimed.
17. `mvn compile` passes.
18. `mvn test` passes.
19. Completion file exists.
```

Do not mark complete if:

```text
external account becomes WorkspaceMember
relationship/stakeholder grants portal access without grant
external token can call internal APIs
portal response leaks internal cost/margin/rate/AI/private notes
document download not audited
clientVisible treated as public
portal acceptance creates invoice/contract/revenue
tests fail
```

---

# 22. Required phase completion file

Agent must create:

```text
docs/phase-complete/PHASE_30_CUSTOMER_EXTERNAL_PORTAL_TO_BE_COMPLETE.md
```

Required sections:

```text
# Phase 30 — Customer / External Collaboration Portal Complete

## 1. Summary
## 2. Source Inputs Reviewed
## 3. Current vs TO-BE Classification Matrix
## 4. Implemented in Current BE
## 5. Implemented / Hardened in This Phase
## 6. Seed-only Items Added
## 7. Deferred Items and Target Phase
## 8. External Portal Boundary Decision
## 9. Security / Principal Separation Strategy
## 10. Entity Mapping
## 11. API Changes
## 12. ExternalPortalAccount Strategy
## 13. Invite / Activation Strategy
## 14. ExternalProjectAccessGrant Strategy
## 15. Portal Permission Policy
## 16. Client Visibility / Masking Strategy
## 17. Portal Project / Document / Deliverable Strategy
## 18. Review / Decision Strategy
## 19. Comment / Feedback Strategy
## 20. UAT Integration Strategy
## 21. Quote / Requirement Integration Strategy
## 22. Notification / Event Strategy
## 23. Reporting Strategy
## 24. Authorization Matrix
## 25. External Audit Strategy
## 26. Activity / Audit / Outbox Notes
## 27. Idempotency Strategy
## 28. Tests Added
## 29. Commands Run
## 30. Test Results
## 31. Manual Verification
## 32. Assumptions
## 33. Deviations From Prompt
## 34. Known Risks
## 35. Future Phases That Must Return to External Portal
```

---

# 23. Future phases that must return

```text
Phase 31 — Meetings / Collaboration:
- Client meeting participants, external meeting minutes visibility, client action items.

Phase 34 — Workflow / Approval:
- Multi-step external approval workflow, delegation, escalation, SLA.

Phase 35 — Advanced Notifications:
- External notification preferences, review reminders, overdue digest, quiet hours.

Phase 36 — Contract / Billing / Revenue:
- Billing portal, invoice view/payment, contract acceptance, commercial signoff.

Phase 38 — Audit / Compliance / Privacy:
- External user consent, privacy export/delete, retention, legal hold.

Phase 39 — Integration / Import / Export:
- SSO/SAML/OIDC for external users, e-signature provider, external document sharing provider.

Phase 40 — Service / Support / Maintenance:
- External support portal, ticketing, SLA, incident communication.

Phase 41 — Knowledge Graph / Semantic Index:
- Client-visible knowledge search, controlled Q&A over documents.
```

---

# 24. Agent anti-bịa rules

The agent must not:

```text
1. Treat external account as WorkspaceMember.
2. Let external token call internal APIs.
3. Treat clientVisible as public access.
4. Grant portal access from stakeholder/relationship alone.
5. Expose internal cost/margin/rate/AI/private notes.
6. Claim e-signature exists.
7. Claim contract execution exists.
8. Claim billing/payment portal exists.
9. Claim public anonymous sharing exists.
10. Claim full workflow approval exists.
11. Claim external support desk exists.
12. Hide deferred billing/contract/e-sign/workflow/support/privacy gaps.
```

---

# 25. Prompt to give coding agent

```text
You are implementing Phase 30 — TO-BE Customer / External Collaboration Portal, Client Access, Review, Approval & UAT Gateway.

This is not an as-is documentation task.

Before coding, read:
- CLAUDE.md / CLAUDE.ms
- Coding_convention.md
- Phase 00–29 docs and completion files
- Current backend code, migrations, tests

Your task:
1. Compare current external/client portal capability against this Phase 30 TO-BE spec.
2. Classify each capability with required labels.
3. Implement ExternalPortalAccount, ExternalPortalInvite, ExternalProjectAccessGrant, ExternalPortalPermissionPolicy.
4. Implement client visibility and masking policy.
5. Implement external portal principal/token separation.
6. Implement portal project, document, deliverable, requirement, quote, feedback, and UAT APIs where source modules exist.
7. Implement ClientReviewRequest, ClientReviewDecision, ClientComment, ClientFeedback, ClientUatAssignment.
8. Implement external portal audit logging.
9. Add IAM/internal management permissions, portal capabilities, events, notifications, reports, audit/outbox, idempotency, and tests.
10. Run mvn compile and mvn test.
11. Create docs/phase-complete/PHASE_30_CUSTOMER_EXTERNAL_PORTAL_TO_BE_COMPLETE.md.

Do not implement or claim billing/payment portal, legal contract execution, e-signature, public anonymous sharing, full workflow automation, support desk/incident portal, marketing CRM, or autonomous AI client actions in this phase.
```

---

# 26. Quick tracking matrix

| Capability | Current backend | Phase 30 action | Later phase |
|---|---|---|---|
| ExternalPortalAccount | Missing/unknown | Must implement | SSO Phase 39 |
| ExternalPortalInvite | Missing/unknown | Must implement | Advanced notification Phase 35 |
| ExternalProjectAccessGrant | Missing/unknown | Must implement | — |
| Portal permission policy | Missing/unknown | Must implement | Workflow Phase 34 |
| Client visibility policy | Missing/unknown | Must implement | — |
| Portal project overview | Missing/unknown | Must implement | Advanced portal UX later |
| Portal document access | Missing/unknown | Must implement if Phase 27 | External sharing Phase 39 |
| Portal deliverable review | Missing/unknown | Must implement if Phase 24 | Workflow Phase 34 |
| Portal requirement review | Missing/unknown | Must implement if Phase 28 | Workflow Phase 34 |
| Portal UAT | Missing/unknown | Must implement if Phase 26 | — |
| Portal quote/proposal view | Missing/unknown | Must implement if Phase 18/27 | Contract Phase 36 |
| ClientReviewRequest | Missing/unknown | Must implement | Workflow Phase 34 |
| ClientReviewDecision | Missing/unknown | Must implement | E-sign/contract Phase 36/39 |
| ClientComment | Missing/unknown | Must implement | Collaboration Phase 31 |
| ClientFeedback | Missing/unknown | Must implement | Support Phase 40 |
| Portal audit log | Missing/unknown | Must implement | Compliance Phase 38 |
| Billing/payment portal | Missing | Defer | Phase 36 |
| E-signature | Missing | Defer | Phase 39/36 |
| Support desk | Missing | Defer | Phase 40 |
| Public sharing | Missing | Defer | Not now / privacy review |
| Full approval workflow | Missing | Defer | Phase 34 |

---

# 27. Final principle

Phase 30 is not complete when "a client can log in."

Phase 30 is complete when Scopery can safely expose project-facing work to external clients:

```text
external account
+ explicit project grant
+ portal role/capability
+ client-visible artifact
+ field masking
+ review/feedback/UAT action
+ audit log
= controlled external collaboration
```

External contact is not internal user.

Stakeholder is not access.

clientVisible is not public.

Portal approval is not e-signature.

Portal acceptance is not billing.

External access must be explicit, minimal, and audited.
