# IAM & Access Control Module — Business & Functional Design

## 1. Document Purpose

This document defines the **IAM & Access Control Module** for a Confluence + Notion-like knowledge workspace platform.

This module is responsible for answering:

```text
Who can do what, on which resource, under which scope?
```

The IAM module does not own workspace, page, document, or database business data. It owns the access rules for those resources.

---

## 2. Module Boundary

### 2.1 What IAM owns

```text
User identity reference
Dynamic roles
Right catalog
Access grants
Grant rights
Protected resource registry
Permission decision engine
Ownership-aware access
Visibility-aware access
Deny / allow logic
```

### 2.2 What IAM does not own

```text
Organization business data
Workspace business data
Team business data
Page / document / block / database content
AI execution
Provider secret execution flow
Workspace onboarding UI
Frontend UI
```

| Area | Owner Module |
|---|---|
| Organization / Workspace / Team | Workspace Platform |
| Page / Document / Database | Knowledge Module |
| Provider / Prompt / Execution | AI Agent Module |
| API key encryption | Provider Secret Module |
| Access decision | IAM / Access Control |

---

## 3. Core Principle

The system must not authorize based on hard-coded role names.

Bad example:

```java
if (role == "ADMIN") {
    allow();
}
```

Correct approach:

```java
permissionService.canAccess(userId, resource, "MANAGE_MEMBER")
```

Or:

```java
permissionService.requireRight(userId, resource, "MANAGE_WORKSPACE")
```

User is not powerful because their role name is "Admin".

User is powerful because they have the required **right** on the required **resource/scope**.

---

## 4. Key Concepts

### 4.1 Principal

A principal is the subject receiving access.

Supported principal types:

```text
USER
GROUP
ROLE
```

Examples:

```text
User Nhi
HR Team
Finance Reviewer Role
```

---

### 4.2 Resource

A resource is the object being protected.

Examples:

```text
WORKSPACE
SPACE
PAGE
DOCUMENT
DATABASE
DATABASE_RECORD
BLOCK
ATTACHMENT
AI_AGENT
PROVIDER_SECRET
```

Important:

```text
IAM does not own the actual resource.
IAM only stores a reference to the resource.
```

Example:

```text
resourceType = WORKSPACE
resourceId = workspace_123
```

The actual workspace is owned by the Workspace Platform Module.

---

### 4.3 Right

A right is an action that the backend understands.

Examples:

```text
VIEW
COMMENT
CREATE
UPDATE
DELETE
SHARE
EXPORT
MANAGE_PERMISSION
MANAGE_WORKSPACE
MANAGE_MEMBER
MANAGE_ROLE
MANAGE_ACCESS
USE_AI
```

Rights should be stored in a system catalog so UI can display them, but they are still system-recognized actions.

---

### 4.4 Role

A role is a dynamic container for access grants.

Examples:

```text
HR Reviewer
Finance Editor
Contract Approver
External Consultant
Knowledge Manager
```

Rules:

```text
1. Role is dynamic.
2. Role is created by workspace/system admin.
3. Role name should not control authorization.
4. Role becomes useful only when it has access grants/rights.
```

---

### 4.5 Scope

Scope defines where a grant applies.

Examples:

```text
RESOURCE
WORKSPACE
SPACE
CLASSIFICATION
OWN_CREATED
CUSTOM
```

Example:

```text
Role HR Reviewer
→ Scope: Classification = HR_CONFIDENTIAL
→ Rights: VIEW, COMMENT
```

---

### 4.6 Access Grant

Access Grant is the core permission record.

It answers:

```text
Who can do what, where?
```

A grant contains:

```text
principalType
principalId
resourceType
resourceId
scopeType
scopeRefId
effect
rights
```

Example:

```text
Principal: HR Team
Scope: Classification = HR_CONFIDENTIAL
Rights: VIEW, COMMENT, UPDATE
Effect: ALLOW
```

---

### 4.7 Effect

Supported effects:

```text
ALLOW
DENY
```

Rule:

```text
DENY wins over ALLOW.
```

Example:

```text
HR Team can view HR_CONFIDENTIAL documents.
But HR Team is denied access to Salary Policy page.
→ HR Team cannot view Salary Policy page.
```

---

## 5. Ownership and Visibility

### 5.1 Owner

Every protected resource should have an owner.

Example:

```text
ownerUserId = user_A
```

Owner should have implicit rights on their own resource.

Recommended owner rights:

```text
VIEW
UPDATE
DELETE
SHARE
MANAGE_PERMISSION
```

### 5.2 Visibility

Visibility controls default access behavior.

Recommended values:

```text
PRIVATE
WORKSPACE
SPACE
PUBLIC_LINK
RESTRICTED
```

| Visibility | Meaning |
|---|---|
| PRIVATE | Owner only unless explicit grant exists |
| WORKSPACE | Workspace members may access if they have required right |
| SPACE | Users with space-level access may access |
| PUBLIC_LINK | Anyone with link may access, if enabled |
| RESTRICTED | Explicit grant required |

Recommended default:

```text
PRIVATE
```

---

## 6. System Admin vs Workspace Manager

### 6.1 System Admin

System Admin manages platform rules:

```text
Right catalog
Default policies
Classification catalog
Workspace creation policy
Security settings
Audit settings
AI/provider settings
Emergency access policy
```

System Admin should not manually assign access to every document.

System Admin sets the rules of the platform.

---

### 6.2 Workspace Manager

A Workspace Manager is not necessarily a hard-coded role.

A user becomes workspace manager because they have rights such as:

```text
MANAGE_WORKSPACE
MANAGE_MEMBER
MANAGE_GROUP
MANAGE_ROLE
MANAGE_ACCESS
MANAGE_CLASSIFICATION
```

The role name can be anything:

```text
Workspace Manager
Office Controller
Knowledge Lead
HR Workspace Owner
```

Authorization depends on rights, not role name.

---

## 7. Dynamic Role Model

Role is data, not hard-coded logic.

Example role:

```text
Role: HR Document Controller
```

Grants:

```text
Grant 1:
Scope: Classification = HR_CONFIDENTIAL
Rights: VIEW, COMMENT, UPDATE

Grant 2:
Scope: Space = HR_SPACE
Rights: CREATE, SHARE
```

If user An has this role:

```text
An can view/comment/update HR confidential content.
An can create/share content in HR space.
```

But An cannot delete unless a grant gives DELETE right.

---

## 8. Content Classification Integration

Content classification is not owned directly by IAM, but IAM should support grants based on classification.

Examples:

```text
PUBLIC
INTERNAL
CONFIDENTIAL
HR_CONFIDENTIAL
FINANCE_CONFIDENTIAL
LEGAL_CONFIDENTIAL
EXECUTIVE_ONLY
```

Example access policy:

```text
Principal: HR Team
Classification: HR_CONFIDENTIAL
Rights: VIEW, COMMENT
Effect: ALLOW
```

Any resource tagged as `HR_CONFIDENTIAL` can then be accessible to HR Team, depending on the authorization engine.

---

## 9. Authorization Decision Engine

The core function should be:

```text
canAccess(userId, resourceType, resourceId, rightCode)
```

Or:

```text
requireAccess(userId, resourceType, resourceId, rightCode)
```

### 9.1 Decision steps

Recommended check sequence:

```text
1. Validate user is active.
2. Load resource reference.
3. Check resource owner.
4. Check resource visibility.
5. Check workspace membership.
6. Load user's groups.
7. Load user's roles.
8. Load direct user grants.
9. Load group grants.
10. Load role grants.
11. Load inherited grants from parent resource/scope.
12. Load classification-based grants.
13. Apply DENY before ALLOW.
14. If no allow found, deny by default.
```

### 9.2 Default rule

```text
Default access = DENY
```

Unless user is owner or has a matching ALLOW grant.

---

## 10. Relationship with Workspace Platform

Workspace Platform owns:

```text
Organization
Workspace
Workspace Member
Team
Team Member
```

IAM uses these as inputs:

```text
Is user a workspace member?
Which teams does user belong to?
Which workspace does resource belong to?
```

IAM owns:

```text
AuthResource
AccessGrant
AccessGrantRight
RightCatalog
DynamicRole
Permission decision
```

---

## 11. Suggested Database Tables

```text
iam_user
iam_role
iam_right
iam_access_grant
iam_access_grant_right
iam_auth_resource
```

Depending on final boundary, user and group may be split between IAM and Workspace Platform.

Recommended clean boundary:

```text
Workspace Platform owns team/group membership.
IAM owns roles, rights, grants, resource registry and permission checks.
```

---

## 12. Access Grant Data Model

### 12.1 iam_access_grant

Suggested fields:

```text
id
workspace_id
principal_type
principal_id
resource_type
resource_id
scope_type
scope_ref_id
classification_id
effect
status
created_at
updated_at
created_by
updated_by
```

### 12.2 iam_access_grant_right

Suggested fields:

```text
grant_id
right_id
```

One access grant can contain multiple rights.

---

## 13. Auth Resource Registry

IAM needs a registry of protected resources.

Suggested table:

```text
iam_auth_resource
```

Fields:

```text
id
workspace_id
resource_type
resource_id
parent_resource_id
owner_user_id
visibility
status
created_at
updated_at
```

Example:

```text
resourceType = WORKSPACE
resourceId = workspace_123
ownerUserId = user_A
visibility = PRIVATE
```

Important:

```text
The actual workspace is still stored in Workspace Platform.
IAM only stores a protected-resource reference.
```

---

## 14. Workspace Creation Integration

When a workspace is created by Workspace Platform:

```text
Workspace Platform creates workspace
Workspace Platform creates owner as workspace member
IAM registers workspace as protected resource
IAM creates owner grant
```

This integration can be implemented after both Workspace Platform and IAM foundation exist.

Owner grant example:

```text
Principal: User A
Resource: Workspace X
Rights:
- VIEW
- CREATE
- UPDATE
- DELETE
- SHARE
- MANAGE_PERMISSION
- MANAGE_WORKSPACE
- MANAGE_MEMBER
- MANAGE_GROUP
- MANAGE_ROLE
- MANAGE_ACCESS
- MANAGE_CLASSIFICATION
Effect: ALLOW
```

---

## 15. AI-Aware Authorization

AI must respect user permissions.

If user cannot access a page, AI must not read or summarize that page for them.

Example:

```text
User asks AI to summarize Salary Policy page.
System checks:
canAccess(userId, PAGE, salary_policy_page_id, VIEW)
```

If denied:

```text
AI cannot read the page.
AI must not include that page in search/summarization.
```

Recommended AI rights:

```text
AI_AGENT_USE
AI_AGENT_MANAGE
AI_AGENT_PROVIDER_SECRET_MANAGE
AI_AGENT_EXECUTION_RUN
AI_AGENT_EXECUTION_LOG_READ
AI_AGENT_PLAYGROUND_RUN
AI_CAN_READ_RESOURCE
AI_CAN_SUMMARIZE_RESOURCE
```

---

## 16. Public vs Private Data

### Private data

```text
visibility = PRIVATE
```

Default access:

```text
owner only
```

Others need explicit grant.

### Workspace data

```text
visibility = WORKSPACE
```

Default access:

```text
workspace members can access only if they have required rights
```

### Restricted data

```text
visibility = RESTRICTED
```

Default access:

```text
explicit grant required
```

---

## 17. Break-Glass / Emergency Access

System Admin should not automatically read all private content by default.

If needed, implement a special emergency access right later:

```text
VIEW_ALL_WORKSPACE_CONTENT
BREAK_GLASS_ACCESS
```

Rules should include:

```text
reason required
audit log required
temporary access
highly restricted
```

This is out of scope for initial IAM foundation.

---

## 18. Out of Scope for Initial IAM Foundation

```text
SSO
MFA
SCIM
OAuth2 login
Public link sharing
External guest invitation
Full inheritance engine if not ready
Break-glass access
AI semantic search permission filtering
Frontend UI
```

---

## 19. Recommended Phases

```text
IAM Phase 1: Access Control Foundation
- Right catalog
- Dynamic role
- Access grant
- Auth resource registry

IAM Phase 2: Authorization Decision Engine
- canAccess()
- owner check
- visibility check
- user/group/role grant check
- deny wins allow

IAM Phase 3: Workspace Integration
- register workspace resource
- create owner grant on workspace creation

IAM Phase 4: Content/Knowledge Integration
- page/document/database resource registration
- inherited access
- restricted pages

IAM Phase 5: Classification-Based Access
- classification grants
- sensitive document rules

IAM Phase 6: AI-Aware Authorization
- AI reads only what user can access
- AI search respects permission filtering
```

---

## 20. Key Design Decision

```text
Role is dynamic.
Right is system-recognized.
Grant connects principal + scope/resource + rights.
Permission engine makes the final decision.
```

And:

```text
Business modules own data.
IAM owns access rules.
```
