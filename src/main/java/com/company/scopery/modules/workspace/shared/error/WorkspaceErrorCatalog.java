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
            HttpStatus.FORBIDDEN);

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
