package com.company.scopery.modules.workspace.shared.error;

import com.company.scopery.common.exception.ErrorCatalog;
import org.springframework.http.HttpStatus;

public enum WorkspaceErrorCatalog implements ErrorCatalog {

    ORGANIZATION_NOT_FOUND(
            "ORGANIZATION_NOT_FOUND",
            "Organization not found",
            HttpStatus.NOT_FOUND),

    ORGANIZATION_CODE_ALREADY_EXISTS(
            "ORGANIZATION_CODE_ALREADY_EXISTS",
            "Organization code already exists",
            HttpStatus.CONFLICT),

    ORGANIZATION_NOT_ACTIVE(
            "ORGANIZATION_NOT_ACTIVE",
            "Organization is not active",
            HttpStatus.UNPROCESSABLE_ENTITY),

    ORGANIZATION_ARCHIVED_CANNOT_BE_UPDATED(
            "ORGANIZATION_ARCHIVED_CANNOT_BE_UPDATED",
            "Archived organization cannot be updated",
            HttpStatus.UNPROCESSABLE_ENTITY),

    WORKSPACE_NOT_FOUND(
            "WORKSPACE_NOT_FOUND",
            "Workspace not found",
            HttpStatus.NOT_FOUND),

    WORKSPACE_CODE_ALREADY_EXISTS(
            "WORKSPACE_CODE_ALREADY_EXISTS",
            "Workspace code already exists in this organization",
            HttpStatus.CONFLICT),

    WORKSPACE_NOT_ACTIVE(
            "WORKSPACE_NOT_ACTIVE",
            "Workspace is not active",
            HttpStatus.UNPROCESSABLE_ENTITY),

    WORKSPACE_ARCHIVED_CANNOT_BE_UPDATED(
            "WORKSPACE_ARCHIVED_CANNOT_BE_UPDATED",
            "Archived workspace cannot be updated",
            HttpStatus.UNPROCESSABLE_ENTITY),

    WORKSPACE_MEMBER_NOT_FOUND(
            "WORKSPACE_MEMBER_NOT_FOUND",
            "Workspace member not found",
            HttpStatus.NOT_FOUND),

    WORKSPACE_MEMBER_ALREADY_EXISTS(
            "WORKSPACE_MEMBER_ALREADY_EXISTS",
            "User is already a member of this workspace",
            HttpStatus.CONFLICT),

    WORKSPACE_OWNER_MEMBER_BOOTSTRAP_FAILED(
            "WORKSPACE_OWNER_MEMBER_BOOTSTRAP_FAILED",
            "Failed to bootstrap owner membership for workspace",
            HttpStatus.INTERNAL_SERVER_ERROR),

    WORKSPACE_MEMBER_CANNOT_BE_ACTIVATED_FOR_INACTIVE_WORKSPACE(
            "WORKSPACE_MEMBER_CANNOT_BE_ACTIVATED_FOR_INACTIVE_WORKSPACE",
            "Workspace member cannot be activated because the workspace is not active",
            HttpStatus.UNPROCESSABLE_ENTITY),

    TEAM_NOT_FOUND(
            "TEAM_NOT_FOUND",
            "Team not found",
            HttpStatus.NOT_FOUND),

    TEAM_CODE_ALREADY_EXISTS(
            "TEAM_CODE_ALREADY_EXISTS",
            "Team code already exists in this workspace",
            HttpStatus.CONFLICT),

    TEAM_NOT_ACTIVE(
            "TEAM_NOT_ACTIVE",
            "Team is not active",
            HttpStatus.UNPROCESSABLE_ENTITY),

    TEAM_ARCHIVED_CANNOT_BE_UPDATED(
            "TEAM_ARCHIVED_CANNOT_BE_UPDATED",
            "Archived team cannot be updated",
            HttpStatus.UNPROCESSABLE_ENTITY),

    TEAM_MEMBER_NOT_FOUND(
            "TEAM_MEMBER_NOT_FOUND",
            "Team member not found",
            HttpStatus.NOT_FOUND),

    TEAM_MEMBER_ALREADY_EXISTS(
            "TEAM_MEMBER_ALREADY_EXISTS",
            "User is already a member of this team",
            HttpStatus.CONFLICT),

    TEAM_MEMBER_REQUIRES_WORKSPACE_MEMBER(
            "TEAM_MEMBER_REQUIRES_WORKSPACE_MEMBER",
            "User must be an active workspace member before joining a team",
            HttpStatus.UNPROCESSABLE_ENTITY),

    INVALID_ORGANIZATION_STATUS(
            "INVALID_ORGANIZATION_STATUS",
            "Invalid organization status value",
            HttpStatus.BAD_REQUEST),

    INVALID_WORKSPACE_STATUS(
            "INVALID_WORKSPACE_STATUS",
            "Invalid workspace status value",
            HttpStatus.BAD_REQUEST),

    INVALID_WORKSPACE_VISIBILITY(
            "INVALID_WORKSPACE_VISIBILITY",
            "Invalid workspace visibility value",
            HttpStatus.BAD_REQUEST),

    INVALID_WORKSPACE_JOIN_POLICY(
            "INVALID_WORKSPACE_JOIN_POLICY",
            "Invalid workspace join policy value",
            HttpStatus.BAD_REQUEST),

    WORKSPACE_INVITATION_NOT_FOUND(
            "WORKSPACE_INVITATION_NOT_FOUND",
            "Workspace invitation not found",
            HttpStatus.NOT_FOUND),

    WORKSPACE_INVITATION_EXPIRED(
            "WORKSPACE_INVITATION_EXPIRED",
            "This invitation has expired",
            HttpStatus.UNPROCESSABLE_ENTITY),

    WORKSPACE_INVITATION_REVOKED(
            "WORKSPACE_INVITATION_REVOKED",
            "This invitation has been revoked",
            HttpStatus.UNPROCESSABLE_ENTITY),

    WORKSPACE_INVITATION_MAX_USES_REACHED(
            "WORKSPACE_INVITATION_MAX_USES_REACHED",
            "This invitation has reached its maximum usage limit",
            HttpStatus.UNPROCESSABLE_ENTITY),

    WORKSPACE_INVITATION_ALREADY_MEMBER(
            "WORKSPACE_INVITATION_ALREADY_MEMBER",
            "You are already a member of this workspace",
            HttpStatus.CONFLICT),

    WORKSPACE_JOIN_REQUEST_NOT_FOUND(
            "WORKSPACE_JOIN_REQUEST_NOT_FOUND",
            "Workspace join request not found",
            HttpStatus.NOT_FOUND),

    WORKSPACE_JOIN_REQUEST_ALREADY_PENDING(
            "WORKSPACE_JOIN_REQUEST_ALREADY_PENDING",
            "You already have a pending join request for this workspace",
            HttpStatus.CONFLICT),

    WORKSPACE_JOIN_REQUEST_ALREADY_MEMBER(
            "WORKSPACE_JOIN_REQUEST_ALREADY_MEMBER",
            "You are already a member of this workspace",
            HttpStatus.CONFLICT),

    WORKSPACE_JOIN_REQUIRES_INVITATION(
            "WORKSPACE_JOIN_REQUIRES_INVITATION",
            "This workspace only accepts members via invitation",
            HttpStatus.UNPROCESSABLE_ENTITY),

    WORKSPACE_JOIN_DISABLED(
            "WORKSPACE_JOIN_DISABLED",
            "This workspace is not accepting new members",
            HttpStatus.UNPROCESSABLE_ENTITY),

    WORKSPACE_JOIN_REQUEST_NOT_PENDING(
            "WORKSPACE_JOIN_REQUEST_NOT_PENDING",
            "This join request is no longer pending",
            HttpStatus.UNPROCESSABLE_ENTITY),

    WORKSPACE_JOIN_REQUEST_FORBIDDEN(
            "WORKSPACE_JOIN_REQUEST_FORBIDDEN",
            "You can only cancel your own join request",
            HttpStatus.FORBIDDEN),

    WORKSPACE_CONTEXT_NOT_MEMBER(
            "WORKSPACE_CONTEXT_NOT_MEMBER",
            "You are not an active member of the selected workspace",
            HttpStatus.UNPROCESSABLE_ENTITY),

    WORKSPACE_ONBOARDING_ALREADY_COMPLETED(
            "WORKSPACE_ONBOARDING_ALREADY_COMPLETED",
            "Your workspace onboarding is already completed",
            HttpStatus.CONFLICT),

    WORKSPACE_ONBOARDING_INVALID_STEP(
            "WORKSPACE_ONBOARDING_INVALID_STEP",
            "Cannot perform this action at the current onboarding step",
            HttpStatus.UNPROCESSABLE_ENTITY),

    WORKSPACE_ONBOARDING_OPTION_NOT_SUPPORTED(
            "WORKSPACE_ONBOARDING_OPTION_NOT_SUPPORTED",
            "This onboarding option is not supported. Use create workspace or join with invitation code.",
            HttpStatus.UNPROCESSABLE_ENTITY),

    INVALID_WORKSPACE_MEMBER_STATUS(
            "INVALID_WORKSPACE_MEMBER_STATUS",
            "Invalid workspace member status value",
            HttpStatus.BAD_REQUEST),

    INVALID_TEAM_STATUS(
            "INVALID_TEAM_STATUS",
            "Invalid team status value",
            HttpStatus.BAD_REQUEST),

    WORKSPACE_IAM_BOOTSTRAP_FAILED(
            "WORKSPACE_IAM_BOOTSTRAP_FAILED",
            "Failed to bootstrap IAM access for workspace resource",
            HttpStatus.INTERNAL_SERVER_ERROR),

    WORKSPACE_ACCESS_DENIED(
            "WORKSPACE_ACCESS_DENIED",
            "You do not have permission to perform this action",
            HttpStatus.FORBIDDEN),

    ORG_MEMBER_NOT_FOUND(
            "ORG_MEMBER_NOT_FOUND",
            "Organization member not found",
            HttpStatus.NOT_FOUND),

    ORG_MEMBER_ALREADY_EXISTS(
            "ORG_MEMBER_ALREADY_EXISTS",
            "User is already a member of this organization",
            HttpStatus.CONFLICT),

    ORG_MEMBER_CANNOT_REMOVE_OWNER(
            "ORG_MEMBER_CANNOT_REMOVE_OWNER",
            "Organization owner cannot be removed from the organization",
            HttpStatus.UNPROCESSABLE_ENTITY),

    INVALID_ORG_MEMBERSHIP_TYPE(
            "INVALID_ORG_MEMBERSHIP_TYPE",
            "Invalid organization membership type value",
            HttpStatus.BAD_REQUEST),

    INVALID_ORG_MEMBER_STATUS(
            "INVALID_ORG_MEMBER_STATUS",
            "Invalid organization member status value",
            HttpStatus.BAD_REQUEST),

    ORG_INVITATION_NOT_FOUND(
            "ORG_INVITATION_NOT_FOUND",
            "Organization invitation not found",
            HttpStatus.NOT_FOUND),

    ORG_INVITATION_ALREADY_MEMBER(
            "ORG_INVITATION_ALREADY_MEMBER",
            "User is already a member of this organization",
            HttpStatus.CONFLICT),

    ORG_INVITATION_EXPIRED(
            "ORG_INVITATION_EXPIRED",
            "This organization invitation has expired",
            HttpStatus.UNPROCESSABLE_ENTITY),

    ORG_INVITATION_NOT_PENDING(
            "ORG_INVITATION_NOT_PENDING",
            "This invitation is no longer pending",
            HttpStatus.UNPROCESSABLE_ENTITY),

    ORG_TEAM_NOT_FOUND(
            "ORG_TEAM_NOT_FOUND",
            "Organization team not found",
            HttpStatus.NOT_FOUND),

    ORG_TEAM_CODE_ALREADY_EXISTS(
            "ORG_TEAM_CODE_ALREADY_EXISTS",
            "Team code already exists in this organization",
            HttpStatus.CONFLICT),

    ORG_TEAM_NOT_ACTIVE(
            "ORG_TEAM_NOT_ACTIVE",
            "Organization team is not active",
            HttpStatus.UNPROCESSABLE_ENTITY),

    ORG_TEAM_ARCHIVED_CANNOT_BE_UPDATED(
            "ORG_TEAM_ARCHIVED_CANNOT_BE_UPDATED",
            "Archived organization team cannot be updated",
            HttpStatus.UNPROCESSABLE_ENTITY),

    ORG_TEAM_MEMBER_NOT_FOUND(
            "ORG_TEAM_MEMBER_NOT_FOUND",
            "Organization team member not found",
            HttpStatus.NOT_FOUND),

    ORG_TEAM_MEMBER_ALREADY_EXISTS(
            "ORG_TEAM_MEMBER_ALREADY_EXISTS",
            "User is already a member of this organization team",
            HttpStatus.CONFLICT),

    ORG_TEAM_MEMBER_REQUIRES_ORG_MEMBER(
            "ORG_TEAM_MEMBER_REQUIRES_ORG_MEMBER",
            "User must be an active organization member before joining an org team",
            HttpStatus.UNPROCESSABLE_ENTITY),

    ORG_TEAM_WORKSPACE_ASSIGNMENT_NOT_FOUND(
            "ORG_TEAM_WORKSPACE_ASSIGNMENT_NOT_FOUND",
            "Organization team workspace assignment not found",
            HttpStatus.NOT_FOUND),

    ORG_TEAM_WORKSPACE_ASSIGNMENT_ALREADY_EXISTS(
            "ORG_TEAM_WORKSPACE_ASSIGNMENT_ALREADY_EXISTS",
            "This team is already assigned to this workspace",
            HttpStatus.CONFLICT),

    ORG_TEAM_CROSS_ORGANIZATION_ASSIGNMENT(
            "ORG_TEAM_CROSS_ORGANIZATION_ASSIGNMENT",
            "An organization team cannot be assigned outside its organization",
            HttpStatus.UNPROCESSABLE_ENTITY),

    INVALID_ORG_TEAM_STATUS(
            "INVALID_ORG_TEAM_STATUS",
            "Invalid organization team status value",
            HttpStatus.BAD_REQUEST),

    INVALID_ORG_TEAM_WORKSPACE_ASSIGNMENT_STATUS(
            "INVALID_ORG_TEAM_WORKSPACE_ASSIGNMENT_STATUS",
            "Invalid org team workspace assignment status value",
            HttpStatus.BAD_REQUEST);

    private final String code;
    private final String defaultMessage;
    private final HttpStatus httpStatus;

    WorkspaceErrorCatalog(String code, String defaultMessage, HttpStatus httpStatus) {
        this.code = code;
        this.defaultMessage = defaultMessage;
        this.httpStatus = httpStatus;
    }

    @Override
    public String code() {
        return code;
    }

    @Override
    public String defaultMessage() {
        return defaultMessage;
    }

    @Override
    public HttpStatus httpStatus() {
        return httpStatus;
    }
}
