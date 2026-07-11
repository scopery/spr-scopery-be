package com.company.scopery.modules.iam.shared.error;

import com.company.scopery.common.exception.AppException;

import java.util.Map;
import java.util.UUID;

public final class IamExceptions {

    private IamExceptions() {}

    // ── Auth ──────────────────────────────────────────────────────────────────

    public static AppException invalidCredentials() {
        return new AppException(IamErrorCatalog.INVALID_CREDENTIALS,
                IamErrorCatalog.INVALID_CREDENTIALS.defaultMessage(), Map.of());
    }

    public static AppException userInactiveCannotLogin(String username) {
        return new AppException(IamErrorCatalog.IAM_USER_INACTIVE_CANNOT_LOGIN,
                "User account is not active: " + username, Map.of("username", username));
    }

    // ── IAM User ──────────────────────────────────────────────────────────────

    public static AppException iamUserNotFound(UUID id) {
        return new AppException(IamErrorCatalog.IAM_USER_NOT_FOUND,
                "IAM user not found: " + id, Map.of("id", id));
    }

    public static AppException usernameAlreadyExists(String username) {
        return new AppException(IamErrorCatalog.IAM_USER_USERNAME_ALREADY_EXISTS,
                "Username already exists: " + username, Map.of("username", username));
    }

    public static AppException emailAlreadyExists(String email) {
        return new AppException(IamErrorCatalog.IAM_USER_EMAIL_ALREADY_EXISTS,
                "Email already exists: " + email, Map.of("email", email));
    }

    public static AppException iamUserSuspendedCannotBeUpdated(String username) {
        return new AppException(IamErrorCatalog.IAM_USER_SUSPENDED_CANNOT_BE_UPDATED,
                "Suspended user cannot be updated: " + username, Map.of("username", username));
    }

    // ── IAM Role ──────────────────────────────────────────────────────────────

    public static AppException iamRoleNotFound(UUID id) {
        return new AppException(IamErrorCatalog.IAM_ROLE_NOT_FOUND,
                "IAM role not found: " + id, Map.of("id", id));
    }

    public static AppException roleCodeAlreadyExists(String code) {
        return new AppException(IamErrorCatalog.IAM_ROLE_CODE_ALREADY_EXISTS,
                "Role code already exists: " + code, Map.of("code", code));
    }

    public static AppException roleInactiveCannotBeUsed(String code) {
        return new AppException(IamErrorCatalog.IAM_ROLE_INACTIVE_CANNOT_BE_USED,
                "Inactive role cannot be assigned: " + code, Map.of("code", code));
    }

    public static AppException roleDeletedCannotBeModified(UUID id) {
        return new AppException(IamErrorCatalog.IAM_ROLE_DELETED_CANNOT_BE_MODIFIED,
                "Deleted role cannot be modified: " + id, Map.of("id", id));
    }

    public static AppException roleSystemBuiltInCannotBeDeleted(String code) {
        return new AppException(IamErrorCatalog.IAM_ROLE_SYSTEM_BUILTIN_CANNOT_BE_DELETED,
                "System built-in role cannot be deleted: " + code, Map.of("code", code));
    }

    public static AppException roleWorkspaceScopeRequiresWorkspaceId() {
        return new AppException(IamErrorCatalog.IAM_ROLE_WORKSPACE_SCOPE_REQUIRES_WORKSPACE_ID,
                IamErrorCatalog.IAM_ROLE_WORKSPACE_SCOPE_REQUIRES_WORKSPACE_ID.defaultMessage(), Map.of());
    }

    public static AppException roleSystemScopeMustNotHaveWorkspaceId() {
        return new AppException(IamErrorCatalog.IAM_ROLE_SYSTEM_SCOPE_MUST_NOT_HAVE_WORKSPACE_ID,
                IamErrorCatalog.IAM_ROLE_SYSTEM_SCOPE_MUST_NOT_HAVE_WORKSPACE_ID.defaultMessage(), Map.of());
    }

    public static AppException roleWorkspaceCodeAlreadyExists(String code, UUID workspaceId) {
        return new AppException(IamErrorCatalog.IAM_ROLE_WORKSPACE_CODE_ALREADY_EXISTS,
                "Role code already exists in workspace " + workspaceId + ": " + code,
                Map.of("code", code, "workspaceId", workspaceId));
    }

    public static AppException roleParentNotFound(UUID parentRoleId) {
        return new AppException(IamErrorCatalog.IAM_ROLE_PARENT_NOT_FOUND,
                "Parent role not found: " + parentRoleId, Map.of("parentRoleId", parentRoleId));
    }

    public static AppException roleParentMustBeTemplate(String parentCode) {
        return new AppException(IamErrorCatalog.IAM_ROLE_PARENT_MUST_BE_TEMPLATE,
                "Parent role must be a SYSTEM_TEMPLATE role: " + parentCode, Map.of("parentCode", parentCode));
    }

    public static AppException roleCannotManageSystemRole(String code) {
        return new AppException(IamErrorCatalog.IAM_ROLE_CANNOT_MANAGE_SYSTEM_ROLE,
                "System role cannot be managed via workspace API: " + code, Map.of("code", code));
    }

    // ── IAM Right ─────────────────────────────────────────────────────────────

    public static AppException iamRightNotFound(UUID id) {
        return new AppException(IamErrorCatalog.IAM_RIGHT_NOT_FOUND,
                "IAM right not found: " + id, Map.of("id", id));
    }

    public static AppException rightCodeAlreadyExists(String code) {
        return new AppException(IamErrorCatalog.IAM_RIGHT_CODE_ALREADY_EXISTS,
                "Right code already exists: " + code, Map.of("code", code));
    }

    public static AppException rightInactiveCannotBeUsed(String code) {
        return new AppException(IamErrorCatalog.IAM_RIGHT_INACTIVE_CANNOT_BE_USED,
                "Inactive right cannot be granted: " + code, Map.of("code", code));
    }

    // ── IAM Permission / Action ──────────────────────────────────────────────

    public static AppException iamPermissionNotFound(UUID id) {
        return new AppException(IamErrorCatalog.IAM_PERMISSION_NOT_FOUND,
                "IAM permission not found: " + id, Map.of("id", id));
    }

    public static AppException iamPermissionNotFound(String code) {
        return new AppException(IamErrorCatalog.IAM_PERMISSION_NOT_FOUND,
                "IAM permission not found: " + code, Map.of("code", code));
    }

    public static AppException iamPermissionInactiveCannotBeUsed(String code) {
        return new AppException(IamErrorCatalog.IAM_PERMISSION_INACTIVE_CANNOT_BE_USED,
                "Inactive permission cannot be granted: " + code, Map.of("code", code));
    }

    public static AppException iamPermissionActionNotFound(UUID id) {
        return new AppException(IamErrorCatalog.IAM_PERMISSION_ACTION_NOT_FOUND,
                "IAM permission action not found: " + id, Map.of("id", id));
    }

    public static AppException iamPermissionActionNotFound(String permissionCode, String actionCode) {
        return new AppException(IamErrorCatalog.IAM_PERMISSION_ACTION_NOT_FOUND,
                "IAM permission action not found: " + permissionCode + "." + actionCode,
                Map.of("permissionCode", permissionCode, "actionCode", actionCode));
    }

    public static AppException iamPermissionActionInactiveCannotBeUsed(String permissionCode, String actionCode) {
        return new AppException(IamErrorCatalog.IAM_PERMISSION_ACTION_INACTIVE_CANNOT_BE_USED,
                "Inactive permission action cannot be granted: " + permissionCode + "." + actionCode,
                Map.of("permissionCode", permissionCode, "actionCode", actionCode));
    }

    public static AppException iamPermissionActionRightNotMapped(UUID actionId) {
        return new AppException(IamErrorCatalog.IAM_PERMISSION_ACTION_RIGHT_NOT_MAPPED,
                "Permission action is not mapped to a legacy right: " + actionId,
                Map.of("actionId", actionId));
    }

    public static AppException iamPermissionActionResourceScopeMismatch(String permissionCode,
                                                                        String resourceScopeLevel,
                                                                        String resourceType,
                                                                        UUID resourceId) {
        return new AppException(IamErrorCatalog.IAM_PERMISSION_ACTION_RESOURCE_SCOPE_MISMATCH,
                "Permission " + permissionCode + " applies to " + resourceScopeLevel
                        + " scope and cannot be granted on resource " + resourceId
                        + " of type " + resourceType,
                Map.of("permissionCode", permissionCode,
                        "resourceScopeLevel", resourceScopeLevel,
                        "resourceType", resourceType,
                        "resourceId", resourceId));
    }

    public static AppException iamPermissionActionSubjectTypeNotAllowed(String permissionCode,
                                                                        String subjectType,
                                                                        UUID grantId) {
        return new AppException(IamErrorCatalog.IAM_PERMISSION_ACTION_SUBJECT_TYPE_NOT_ALLOWED,
                "Permission " + permissionCode + " cannot be granted to subject type "
                        + subjectType + " on grant " + grantId,
                Map.of("permissionCode", permissionCode,
                        "subjectType", subjectType,
                        "grantId", grantId));
    }

    // ── IAM Auth Resource ─────────────────────────────────────────────────────

    public static AppException iamAuthResourceNotFound(UUID id) {
        return new AppException(IamErrorCatalog.IAM_AUTH_RESOURCE_NOT_FOUND,
                "IAM auth resource not found: " + id, Map.of("id", id));
    }

    public static AppException iamAuthResourceNotFound(String code) {
        return new AppException(IamErrorCatalog.IAM_AUTH_RESOURCE_NOT_FOUND,
                "IAM auth resource not found: " + code, Map.of("code", code));
    }

    public static AppException iamAuthResourceCodeAlreadyExists(String code, String type) {
        return new AppException(IamErrorCatalog.IAM_AUTH_RESOURCE_CODE_ALREADY_EXISTS,
                "Resource code already exists for type " + type + ": " + code,
                Map.of("code", code, "type", type));
    }

    public static AppException iamAuthResourceInactiveCannotBeUsed(String code) {
        return new AppException(IamErrorCatalog.IAM_AUTH_RESOURCE_INACTIVE_CANNOT_BE_USED,
                "Inactive resource cannot receive access grants: " + code, Map.of("code", code));
    }

    public static AppException iamAuthResourceManualCreateRequiresGlobal(String resourceType) {
        return new AppException(IamErrorCatalog.IAM_AUTH_RESOURCE_MANUAL_CREATE_REQUIRES_GLOBAL,
                "Only GLOBAL IAM resources can be manually registered; " + resourceType
                        + " resources must be bootstrapped by their owning module",
                Map.of("resourceType", resourceType));
    }

    // ── IAM Access Grant ──────────────────────────────────────────────────────

    public static AppException iamAccessGrantNotFound(UUID id) {
        return new AppException(IamErrorCatalog.IAM_ACCESS_GRANT_NOT_FOUND,
                "IAM access grant not found: " + id, Map.of("id", id));
    }

    public static AppException iamAccessGrantAlreadyExists(UUID subjectId, UUID resourceId) {
        return new AppException(IamErrorCatalog.IAM_ACCESS_GRANT_ALREADY_EXISTS,
                "Access grant already exists for subject " + subjectId + " on resource " + resourceId,
                Map.of("subjectId", subjectId, "resourceId", resourceId));
    }

    public static AppException iamAccessGrantRevokedCannotBeModified(UUID id) {
        return new AppException(IamErrorCatalog.IAM_ACCESS_GRANT_REVOKED_CANNOT_BE_MODIFIED,
                "Revoked access grant cannot be modified: " + id, Map.of("id", id));
    }

    public static AppException iamAccessGrantRightAlreadyExists(UUID grantId, UUID rightId) {
        return new AppException(IamErrorCatalog.IAM_ACCESS_GRANT_RIGHT_ALREADY_EXISTS,
                "Right " + rightId + " is already attached to grant " + grantId,
                Map.of("grantId", grantId, "rightId", rightId));
    }

    public static AppException iamAccessGrantRightNotFound(UUID grantId, UUID rightId) {
        return new AppException(IamErrorCatalog.IAM_ACCESS_GRANT_RIGHT_NOT_FOUND,
                "Right " + rightId + " is not attached to grant " + grantId,
                Map.of("grantId", grantId, "rightId", rightId));
    }

    public static AppException iamAccessGrantPermissionActionAlreadyExists(UUID grantId,
                                                                           UUID permissionActionId) {
        return new AppException(IamErrorCatalog.IAM_ACCESS_GRANT_PERMISSION_ACTION_ALREADY_EXISTS,
                "Permission action " + permissionActionId + " is already attached to grant " + grantId,
                Map.of("grantId", grantId, "permissionActionId", permissionActionId));
    }

    public static AppException iamAccessGrantPermissionActionNotFound(UUID grantId,
                                                                      UUID permissionActionId) {
        return new AppException(IamErrorCatalog.IAM_ACCESS_GRANT_PERMISSION_ACTION_NOT_FOUND,
                "Permission action " + permissionActionId + " is not attached to grant " + grantId,
                Map.of("grantId", grantId, "permissionActionId", permissionActionId));
    }

    public static AppException iamOwnerPolicyNotFound(String resourceType) {
        return new AppException(IamErrorCatalog.IAM_OWNER_POLICY_NOT_FOUND,
                "Active owner policy not found for resource type " + resourceType,
                Map.of("resourceType", resourceType));
    }

    public static AppException iamDelegationNotPermitted(UUID actorId, UUID resourceId) {
        return new AppException(IamErrorCatalog.IAM_DELEGATION_NOT_PERMITTED,
                "Actor " + actorId + " cannot delegate access on resource " + resourceId,
                Map.of("actorId", actorId, "resourceId", resourceId));
    }

    public static AppException iamDelegationDepthExceeded(int requestedDepth, int availableDepth) {
        return new AppException(IamErrorCatalog.IAM_DELEGATION_DEPTH_EXCEEDED,
                "Requested delegation depth " + requestedDepth + " exceeds available depth " + availableDepth,
                Map.of("requestedDepth", requestedDepth, "availableDepth", availableDepth));
    }

    // ── IAM Role Assignment ───────────────────────────────────────────────────

    public static AppException iamRoleAssignmentNotFound(UUID id) {
        return new AppException(IamErrorCatalog.IAM_ROLE_ASSIGNMENT_NOT_FOUND,
                "Role assignment not found: " + id, Map.of("id", id));
    }

    public static AppException iamRoleAssignmentAlreadyExists(UUID roleId, UUID assigneeId) {
        return new AppException(IamErrorCatalog.IAM_ROLE_ASSIGNMENT_ALREADY_EXISTS,
                "Active role assignment already exists for role " + roleId + " and assignee " + assigneeId,
                Map.of("roleId", roleId, "assigneeId", assigneeId));
    }

    public static AppException iamRoleNotActiveForAssignment(String roleCode) {
        return new AppException(IamErrorCatalog.IAM_ROLE_ASSIGNMENT_ROLE_NOT_ACTIVE,
                "Role is not active and cannot be assigned: " + roleCode, Map.of("roleCode", roleCode));
    }

    public static AppException iamRoleAssignmentInvalidAssignee(UUID assigneeId, String assigneeType) {
        return new AppException(IamErrorCatalog.IAM_ROLE_ASSIGNMENT_INVALID_ASSIGNEE,
                "Assignee " + assigneeId + " of type " + assigneeType + " is not valid for this role assignment",
                Map.of("assigneeId", assigneeId, "assigneeType", assigneeType));
    }

    public static AppException iamRoleAssignmentRequiresWorkspaceMember(UUID userId, UUID workspaceId) {
        return new AppException(IamErrorCatalog.IAM_ROLE_ASSIGNMENT_REQUIRES_WORKSPACE_MEMBER,
                "User " + userId + " must be an active workspace member for workspace-scoped role assignment in workspace " + workspaceId,
                Map.of("userId", userId, "workspaceId", workspaceId));
    }

    // ── Authorization ─────────────────────────────────────────────────────────

    public static AppException accessDenied(UUID userId, String resourceType, UUID resourceId, String rightCode) {
        return new AppException(IamErrorCatalog.IAM_ACCESS_DENIED,
                "Access denied for user " + userId + " on " + resourceType + " " + resourceId + " (right: " + rightCode + ")",
                Map.of("userId", userId, "resourceType", resourceType, "resourceId", resourceId, "rightCode", rightCode));
    }

    public static AppException authenticatedUserNotFound(String username) {
        return new AppException(IamErrorCatalog.IAM_AUTHENTICATED_USER_NOT_FOUND,
                "Authenticated user not found in system: " + username, Map.of("username", username));
    }

    public static AppException authenticatedUserCannotBeResolved() {
        return new AppException(IamErrorCatalog.IAM_AUTHENTICATED_USER_CANNOT_BE_RESOLVED,
                IamErrorCatalog.IAM_AUTHENTICATED_USER_CANNOT_BE_RESOLVED.defaultMessage(), Map.of());
    }

    public static AppException authorizationDecisionFailed(String reason) {
        return new AppException(IamErrorCatalog.IAM_AUTHORIZATION_DECISION_FAILED,
                "Authorization decision failed: " + reason, Map.of("reason", reason));
    }

    public static AppException globalSystemResourceMissing() {
        return new AppException(IamErrorCatalog.IAM_GLOBAL_SYSTEM_RESOURCE_MISSING,
                IamErrorCatalog.IAM_GLOBAL_SYSTEM_RESOURCE_MISSING.defaultMessage(), Map.of());
    }

    // ── Integration ───────────────────────────────────────────────────────────

    public static AppException iamResourceBootstrapFailed(String entityType, UUID entityId, String reason) {
        return new AppException(IamErrorCatalog.IAM_RESOURCE_BOOTSTRAP_FAILED,
                "Failed to register " + entityType + " " + entityId + " in IAM: " + reason,
                Map.of("entityType", entityType, "entityId", entityId, "reason", reason));
    }

    public static AppException iamOwnerGrantBootstrapFailed(String entityType, UUID entityId, String reason) {
        return new AppException(IamErrorCatalog.IAM_OWNER_GRANT_BOOTSTRAP_FAILED,
                "Failed to create owner grant for " + entityType + " " + entityId + ": " + reason,
                Map.of("entityType", entityType, "entityId", entityId, "reason", reason));
    }

    public static AppException iamRightRequiredForBootstrapNotFound(String rightCode) {
        return new AppException(IamErrorCatalog.IAM_RIGHT_REQUIRED_FOR_BOOTSTRAP_NOT_FOUND,
                "Right required for IAM bootstrap not found in catalog: " + rightCode,
                Map.of("rightCode", rightCode));
    }

    public static AppException iamIntegrationAccessDenied(UUID userId, String action) {
        return new AppException(IamErrorCatalog.IAM_INTEGRATION_ACCESS_DENIED,
                "User " + userId + " does not have permission to: " + action,
                Map.of("userId", userId, "action", action));
    }
}
