package com.company.scopery.modules.iam.shared.error;

import com.company.scopery.common.exception.ErrorCatalog;
import org.springframework.http.HttpStatus;

public enum IamErrorCatalog implements ErrorCatalog {

    // Auth
    INVALID_CREDENTIALS(
            "INVALID_CREDENTIALS", "Invalid username or password", HttpStatus.UNAUTHORIZED),
    IAM_USER_INACTIVE_CANNOT_LOGIN(
            "IAM_USER_INACTIVE_CANNOT_LOGIN", "User account is not active", HttpStatus.UNAUTHORIZED),

    // User
    IAM_USER_NOT_FOUND(
            "IAM_USER_NOT_FOUND", "User not found", HttpStatus.NOT_FOUND),
    IAM_USER_USERNAME_ALREADY_EXISTS(
            "IAM_USER_USERNAME_ALREADY_EXISTS", "Username already taken", HttpStatus.CONFLICT),
    IAM_USER_EMAIL_ALREADY_EXISTS(
            "IAM_USER_EMAIL_ALREADY_EXISTS", "Email already registered", HttpStatus.CONFLICT),
    IAM_USER_SUSPENDED_CANNOT_BE_UPDATED(
            "IAM_USER_SUSPENDED_CANNOT_BE_UPDATED", "Suspended user cannot be updated", HttpStatus.UNPROCESSABLE_ENTITY),
    INVALID_IAM_USER_STATUS(
            "INVALID_IAM_USER_STATUS", "Invalid user status value", HttpStatus.BAD_REQUEST),

    // Role
    IAM_ROLE_NOT_FOUND(
            "IAM_ROLE_NOT_FOUND", "Role not found", HttpStatus.NOT_FOUND),
    IAM_ROLE_CODE_ALREADY_EXISTS(
            "IAM_ROLE_CODE_ALREADY_EXISTS", "Role code already exists", HttpStatus.CONFLICT),
    IAM_ROLE_INACTIVE_CANNOT_BE_USED(
            "IAM_ROLE_INACTIVE_CANNOT_BE_USED", "Role is inactive and cannot be assigned", HttpStatus.UNPROCESSABLE_ENTITY),
    INVALID_IAM_ROLE_STATUS(
            "INVALID_IAM_ROLE_STATUS", "Invalid role status value", HttpStatus.BAD_REQUEST),
    INVALID_IAM_ROLE_SCOPE(
            "INVALID_IAM_ROLE_SCOPE", "Invalid role scope value", HttpStatus.BAD_REQUEST),
    INVALID_IAM_ROLE_SOURCE(
            "INVALID_IAM_ROLE_SOURCE", "Invalid role source value", HttpStatus.BAD_REQUEST),
    IAM_ROLE_DELETED_CANNOT_BE_MODIFIED(
            "IAM_ROLE_DELETED_CANNOT_BE_MODIFIED", "Deleted role cannot be modified", HttpStatus.UNPROCESSABLE_ENTITY),
    IAM_ROLE_SYSTEM_BUILTIN_CANNOT_BE_DELETED(
            "IAM_ROLE_SYSTEM_BUILTIN_CANNOT_BE_DELETED", "System built-in roles cannot be deleted", HttpStatus.UNPROCESSABLE_ENTITY),
    IAM_ROLE_WORKSPACE_SCOPE_REQUIRES_WORKSPACE_ID(
            "IAM_ROLE_WORKSPACE_SCOPE_REQUIRES_WORKSPACE_ID", "Workspace-scoped role requires a workspaceId", HttpStatus.BAD_REQUEST),
    IAM_ROLE_SYSTEM_SCOPE_MUST_NOT_HAVE_WORKSPACE_ID(
            "IAM_ROLE_SYSTEM_SCOPE_MUST_NOT_HAVE_WORKSPACE_ID", "System-scoped role must not have a workspaceId", HttpStatus.BAD_REQUEST),
    IAM_ROLE_WORKSPACE_CODE_ALREADY_EXISTS(
            "IAM_ROLE_WORKSPACE_CODE_ALREADY_EXISTS", "Role code already exists in this workspace", HttpStatus.CONFLICT),
    IAM_ROLE_PARENT_NOT_FOUND(
            "IAM_ROLE_PARENT_NOT_FOUND", "Parent role not found", HttpStatus.NOT_FOUND),
    IAM_ROLE_PARENT_MUST_BE_TEMPLATE(
            "IAM_ROLE_PARENT_MUST_BE_TEMPLATE", "Parent role must be a system template role", HttpStatus.UNPROCESSABLE_ENTITY),
    IAM_ROLE_CANNOT_MANAGE_SYSTEM_ROLE(
            "IAM_ROLE_CANNOT_MANAGE_SYSTEM_ROLE", "System roles cannot be managed via workspace API", HttpStatus.UNPROCESSABLE_ENTITY),

    // Right
    IAM_RIGHT_NOT_FOUND(
            "IAM_RIGHT_NOT_FOUND", "Right not found", HttpStatus.NOT_FOUND),
    IAM_RIGHT_CODE_ALREADY_EXISTS(
            "IAM_RIGHT_CODE_ALREADY_EXISTS", "Right code already exists", HttpStatus.CONFLICT),
    IAM_RIGHT_INACTIVE_CANNOT_BE_USED(
            "IAM_RIGHT_INACTIVE_CANNOT_BE_USED", "Right is inactive and cannot be granted", HttpStatus.UNPROCESSABLE_ENTITY),
    INVALID_IAM_RIGHT_STATUS(
            "INVALID_IAM_RIGHT_STATUS", "Invalid right status value", HttpStatus.BAD_REQUEST),

    // Auth Resource
    IAM_AUTH_RESOURCE_NOT_FOUND(
            "IAM_AUTH_RESOURCE_NOT_FOUND", "Resource not found", HttpStatus.NOT_FOUND),
    IAM_AUTH_RESOURCE_CODE_ALREADY_EXISTS(
            "IAM_AUTH_RESOURCE_CODE_ALREADY_EXISTS", "Resource code already exists for this type", HttpStatus.CONFLICT),
    IAM_AUTH_RESOURCE_INACTIVE_CANNOT_BE_USED(
            "IAM_AUTH_RESOURCE_INACTIVE_CANNOT_BE_USED", "Resource is inactive and cannot receive access grants", HttpStatus.UNPROCESSABLE_ENTITY),
    INVALID_IAM_RESOURCE_TYPE(
            "INVALID_IAM_RESOURCE_TYPE", "Invalid resource type value", HttpStatus.BAD_REQUEST),
    INVALID_IAM_RESOURCE_STATUS(
            "INVALID_IAM_RESOURCE_STATUS", "Invalid resource status value", HttpStatus.BAD_REQUEST),

    // Access Grant
    IAM_ACCESS_GRANT_NOT_FOUND(
            "IAM_ACCESS_GRANT_NOT_FOUND", "Access grant not found", HttpStatus.NOT_FOUND),
    IAM_ACCESS_GRANT_ALREADY_EXISTS(
            "IAM_ACCESS_GRANT_ALREADY_EXISTS", "Access grant already exists for this subject and resource", HttpStatus.CONFLICT),
    IAM_ACCESS_GRANT_REVOKED_CANNOT_BE_MODIFIED(
            "IAM_ACCESS_GRANT_REVOKED_CANNOT_BE_MODIFIED", "Revoked grant cannot be modified", HttpStatus.UNPROCESSABLE_ENTITY),
    IAM_ACCESS_GRANT_RIGHT_ALREADY_EXISTS(
            "IAM_ACCESS_GRANT_RIGHT_ALREADY_EXISTS", "Right is already attached to this grant", HttpStatus.CONFLICT),
    IAM_ACCESS_GRANT_RIGHT_NOT_FOUND(
            "IAM_ACCESS_GRANT_RIGHT_NOT_FOUND", "Right is not attached to this grant", HttpStatus.NOT_FOUND),
    INVALID_IAM_SUBJECT_TYPE(
            "INVALID_IAM_SUBJECT_TYPE", "Invalid subject type value", HttpStatus.BAD_REQUEST),
    INVALID_IAM_ACCESS_GRANT_STATUS(
            "INVALID_IAM_ACCESS_GRANT_STATUS", "Invalid access grant status value", HttpStatus.BAD_REQUEST),
    INVALID_IAM_GRANT_EFFECT(
            "INVALID_IAM_GRANT_EFFECT", "Invalid grant effect value", HttpStatus.BAD_REQUEST),
    INVALID_IAM_GRANT_SCOPE_TYPE(
            "INVALID_IAM_GRANT_SCOPE_TYPE", "Invalid grant scope type value", HttpStatus.BAD_REQUEST),

    // Role Assignment
    IAM_ROLE_ASSIGNMENT_NOT_FOUND(
            "IAM_ROLE_ASSIGNMENT_NOT_FOUND", "Role assignment not found", HttpStatus.NOT_FOUND),
    IAM_ROLE_ASSIGNMENT_ALREADY_EXISTS(
            "IAM_ROLE_ASSIGNMENT_ALREADY_EXISTS", "Duplicate active role assignment", HttpStatus.CONFLICT),
    IAM_ROLE_ASSIGNMENT_ROLE_NOT_ACTIVE(
            "IAM_ROLE_ASSIGNMENT_ROLE_NOT_ACTIVE", "Role is not active and cannot be assigned", HttpStatus.UNPROCESSABLE_ENTITY),
    IAM_ROLE_ASSIGNMENT_INVALID_ASSIGNEE(
            "IAM_ROLE_ASSIGNMENT_INVALID_ASSIGNEE", "Assignee is not valid for this role assignment", HttpStatus.BAD_REQUEST),
    IAM_ROLE_ASSIGNMENT_REQUIRES_WORKSPACE_MEMBER(
            "IAM_ROLE_ASSIGNMENT_REQUIRES_WORKSPACE_MEMBER", "User must be an active workspace member for workspace-scoped role assignment", HttpStatus.UNPROCESSABLE_ENTITY),
    INVALID_ROLE_ASSIGNEE_TYPE(
            "INVALID_ROLE_ASSIGNEE_TYPE", "Invalid role assignee type", HttpStatus.BAD_REQUEST),
    INVALID_IAM_ROLE_ASSIGNMENT_STATUS(
            "INVALID_IAM_ROLE_ASSIGNMENT_STATUS", "Invalid role assignment status", HttpStatus.BAD_REQUEST),

    // Authorization
    IAM_ACCESS_DENIED(
            "IAM_ACCESS_DENIED", "Access denied", HttpStatus.FORBIDDEN),
    IAM_AUTHENTICATED_USER_NOT_FOUND(
            "IAM_AUTHENTICATED_USER_NOT_FOUND", "Authenticated user not found in the system", HttpStatus.UNAUTHORIZED),
    IAM_AUTHENTICATED_USER_CANNOT_BE_RESOLVED(
            "IAM_AUTHENTICATED_USER_CANNOT_BE_RESOLVED", "Authenticated user identity cannot be resolved", HttpStatus.UNAUTHORIZED),
    IAM_AUTHORIZATION_DECISION_FAILED(
            "IAM_AUTHORIZATION_DECISION_FAILED", "Authorization decision evaluation failed unexpectedly", HttpStatus.INTERNAL_SERVER_ERROR),

    // Integration
    IAM_RESOURCE_BOOTSTRAP_FAILED(
            "IAM_RESOURCE_BOOTSTRAP_FAILED", "Failed to register resource in IAM system", HttpStatus.INTERNAL_SERVER_ERROR),
    IAM_OWNER_GRANT_BOOTSTRAP_FAILED(
            "IAM_OWNER_GRANT_BOOTSTRAP_FAILED", "Failed to create owner access grant in IAM system", HttpStatus.INTERNAL_SERVER_ERROR),
    IAM_RIGHT_REQUIRED_FOR_BOOTSTRAP_NOT_FOUND(
            "IAM_RIGHT_REQUIRED_FOR_BOOTSTRAP_NOT_FOUND", "Required right not found in catalog during IAM bootstrap", HttpStatus.INTERNAL_SERVER_ERROR),
    IAM_INTEGRATION_ACCESS_DENIED(
            "IAM_INTEGRATION_ACCESS_DENIED", "Access denied by IAM integration", HttpStatus.FORBIDDEN);

    private final String code;
    private final String defaultMessage;
    private final HttpStatus httpStatus;

    IamErrorCatalog(String code, String defaultMessage, HttpStatus httpStatus) {
        this.code = code;
        this.defaultMessage = defaultMessage;
        this.httpStatus = httpStatus;
    }

    @Override public String code() { return code; }
    @Override public String defaultMessage() { return defaultMessage; }
    @Override public HttpStatus httpStatus() { return httpStatus; }
}
