# Workspace Platform Module — Business & Functional Design

## 1. Document Purpose

This document defines the **Workspace Platform Module** for a Confluence + Notion-like knowledge workspace product.

The Workspace Platform Module manages the business structure of collaboration:

```text
Organization
→ Workspace
→ Workspace Member
→ Team / Group
→ Team Member
```

This module does **not** own permission logic. It creates and manages the actual business entities that IAM / Access Control will protect later.

---

## 2. Module Boundary

### 2.1 What this module owns

```text
Organization
Workspace
Workspace Member
Team / Group
Team Member
Workspace settings
Workspace default visibility
Workspace bootstrap flow
```

### 2.2 What this module does not own

```text
Login / JWT / Password
Dynamic roles / rights / access grants
Permission decision engine
Page / document / block / database content
AI Agent execution
Provider secret
Frontend UI
```

| Area | Owner Module |
|---|---|
| Login / authentication | IAM / Auth |
| Dynamic roles / rights / grants | IAM / Access Control |
| Page / document / database | Knowledge Module |
| Provider / Prompt / Execution | AI Agent Module |
| Provider API key | AI Agent Provider Secret |
| Permission checking | IAM / Access Control |

---

## 3. High-Level Concept

```text
Workspace Platform = creates the house
IAM / Access Control = controls who has keys to the house
Knowledge Module = stores rooms, pages, documents and content inside the house
```

---

## 4. Core Entities

### 4.1 Organization

An organization represents a top-level business entity.

Examples:

```text
Archetype Group
ABC Company
Personal Organization
```

Fields:

| Field | Description |
|---|---|
| id | Unique organization ID |
| code | Unique organization code |
| name | Organization name |
| description | Optional description |
| ownerUserId | User who created/owns the organization |
| status | ACTIVE, INACTIVE, ARCHIVED |
| createdAt / updatedAt | Audit timestamps |
| createdBy / updatedBy | Audit users |

Business rules:

```text
1. Organization code is required.
2. Organization code is normalized to uppercase.
3. Organization code must be globally unique.
4. Organization name is required.
5. Owner user ID is required.
6. New organization is ACTIVE by default.
```

---

### 4.2 Workspace

A workspace is a collaboration area inside an organization.

Examples:

```text
HR Knowledge Base
Marketing Hub
Project Delivery Workspace
Finance Internal Workspace
```

Fields:

| Field | Description |
|---|---|
| id | Unique workspace ID |
| organizationId | Parent organization |
| code | Unique code within organization |
| name | Workspace name |
| description | Optional description |
| ownerUserId | Creator / owner of workspace |
| defaultVisibility | Default visibility for new content |
| status | ACTIVE, INACTIVE, ARCHIVED |
| createdAt / updatedAt | Audit timestamps |
| createdBy / updatedBy | Audit users |

Business rules:

```text
1. Workspace belongs to an organization.
2. Organization must exist and be ACTIVE.
3. Workspace code is required.
4. Workspace code is normalized to uppercase.
5. Workspace code is unique within one organization.
6. Workspace name is required.
7. Owner user ID is required.
8. Default visibility is required.
9. If default visibility is blank, use PRIVATE.
10. New workspace is ACTIVE by default.
11. Workspace creator must automatically become a workspace member.
12. Workspace creation and creator membership creation must be transactional.
```

---

### 4.3 Workspace Member

A workspace member indicates that a user belongs to a workspace.

Important:

```text
Workspace Member does not mean Admin.
Workspace Member does not contain role.
Role and permission are handled later by IAM.
```

Fields:

| Field | Description |
|---|---|
| id | Unique member record ID |
| workspaceId | Workspace ID |
| userId | User ID |
| status | ACTIVE, INACTIVE |
| joinedAt | Date/time user joined workspace |
| createdAt / updatedAt | Audit timestamps |

Business rules:

```text
1. Workspace must exist and be ACTIVE.
2. User ID is required.
3. One user can only be added once to a workspace.
4. New member is ACTIVE by default.
5. Inactive member should not be considered active by permission logic later.
```

---

### 4.4 Team / Group

A team is a group of users inside one workspace.

Examples:

```text
HR Team
Finance Team
Project Managers
External Consultants
```

Fields:

| Field | Description |
|---|---|
| id | Unique team ID |
| workspaceId | Workspace ID |
| code | Unique team code within workspace |
| name | Team name |
| description | Optional description |
| status | ACTIVE, INACTIVE, ARCHIVED |
| createdAt / updatedAt | Audit timestamps |

Business rules:

```text
1. Team belongs to one workspace.
2. Workspace must exist and be ACTIVE.
3. Team code is required.
4. Team code is normalized to uppercase.
5. Team code is unique within one workspace.
6. Team name is required.
7. New team is ACTIVE by default.
```

---

### 4.5 Team Member

A team member links a user to a team.

Fields:

| Field | Description |
|---|---|
| teamId | Team ID |
| userId | User ID |
| createdAt | Created timestamp |
| createdBy | Created by |

Business rules:

```text
1. Team must exist and be ACTIVE.
2. User ID is required.
3. User must be an ACTIVE member of the same workspace before being added to the team.
4. Duplicate team member is rejected.
```

---

## 5. Workspace Visibility

Workspace default visibility controls the default visibility for new resources created under the workspace.

Recommended values:

```text
PRIVATE
WORKSPACE
RESTRICTED
```

| Visibility | Meaning |
|---|---|
| PRIVATE | Only owner can access by default unless explicitly shared |
| WORKSPACE | Workspace members can access based on IAM rights |
| RESTRICTED | Explicit access grant is required |

Recommended default:

```text
PRIVATE
```

Reason:

```text
New data should be private by default to avoid accidental information leaks.
```

---

## 6. Workspace Bootstrap Flow

### 6.1 Problem to avoid

Previous onboarding failures often happen when the frontend calls multiple APIs separately:

```text
POST create workspace
POST add creator as member
POST create permission
POST register resource
```

If one step fails, the system may end up with broken data:

```text
Workspace exists but creator is not a member.
Workspace exists but creator cannot access it.
User logs in but has no valid workspace state.
```

### 6.2 Required backend flow

Workspace creation must be handled by backend as one transaction:

```text
POST /api/v1/workspaces
→ create workspace
→ create workspace member for ownerUserId
→ commit transaction
```

If any step fails:

```text
rollback all changes
```

### 6.3 Future IAM integration

After IAM Access Control is implemented, workspace creation will also trigger:

```text
register workspace as protected resource
create owner access grant
```

But this is intentionally not part of Workspace Phase 1.

---

## 7. API Scope

### 7.1 Organization APIs

```http
POST   /api/v1/organizations
PUT    /api/v1/organizations/{id}
GET    /api/v1/organizations/{id}
GET    /api/v1/organizations
PATCH  /api/v1/organizations/{id}/activate
PATCH  /api/v1/organizations/{id}/archive
```

### 7.2 Workspace APIs

```http
POST   /api/v1/workspaces
PUT    /api/v1/workspaces/{id}
GET    /api/v1/workspaces/{id}
GET    /api/v1/workspaces
PATCH  /api/v1/workspaces/{id}/activate
PATCH  /api/v1/workspaces/{id}/archive
```

### 7.3 Workspace Member APIs

```http
POST   /api/v1/workspaces/{workspaceId}/members
GET    /api/v1/workspaces/{workspaceId}/members
PATCH  /api/v1/workspaces/{workspaceId}/members/{memberId}/activate
PATCH  /api/v1/workspaces/{workspaceId}/members/{memberId}/deactivate
```

### 7.4 Team APIs

```http
POST   /api/v1/workspaces/{workspaceId}/teams
PUT    /api/v1/workspaces/{workspaceId}/teams/{teamId}
GET    /api/v1/workspaces/{workspaceId}/teams/{teamId}
GET    /api/v1/workspaces/{workspaceId}/teams
PATCH  /api/v1/workspaces/{workspaceId}/teams/{teamId}/activate
PATCH  /api/v1/workspaces/{workspaceId}/teams/{teamId}/archive
POST   /api/v1/workspaces/{workspaceId}/teams/{teamId}/members
DELETE /api/v1/workspaces/{workspaceId}/teams/{teamId}/members/{userId}
```

---

## 8. Suggested Database Tables

```text
workspace_organization
workspace_workspace
workspace_member
workspace_team
workspace_team_member
```

Workspace Platform tables should use prefix:

```text
workspace_
```

Do not use `iam_` prefix because workspace business objects do not belong to IAM.

---

## 9. Relationship with IAM

Workspace Platform does not decide final access.

It only provides business facts:

```text
User A created Workspace X.
User B is a member of Workspace X.
User C belongs to Team HR in Workspace X.
Workspace X default visibility is PRIVATE.
```

IAM will later use these facts to answer:

```text
Can user B VIEW workspace X?
Can user C UPDATE document Y?
Can Team HR access HR confidential pages?
```

---

## 10. Out of Scope

```text
Login
JWT
Password
Dynamic role
Right catalog
Access grant
Permission decision engine
Page / document / database
Content classification
Public link sharing
AI access control
Frontend UI
```

---

## 11. Recommended Next Phases

```text
Phase 1: Workspace Platform Foundation
Phase 2: IAM Access Control Foundation
Phase 3: Authorization Decision Engine
Phase 4: Knowledge Module Foundation
Phase 5: Content Classification
Phase 6: AI-aware Permission Enforcement
```

---

## 12. Key Design Decision

```text
Business modules own resources.
IAM owns access rules for those resources.
```

Therefore:

```text
Workspace Platform owns Workspace.
IAM references Workspace as a protected resource later.
```
