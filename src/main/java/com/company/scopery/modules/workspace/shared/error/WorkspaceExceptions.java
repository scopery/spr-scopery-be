package com.company.scopery.modules.workspace.shared.error;

import com.company.scopery.common.exception.AppException;

import java.util.Map;
import java.util.UUID;

public final class WorkspaceExceptions {

    private WorkspaceExceptions() {}

    // ── Organization ──────────────────────────────────────────────────────────

    public static AppException organizationNotFound(UUID id) {
        return new AppException(WorkspaceErrorCatalog.ORGANIZATION_NOT_FOUND,
                "Organization not found: " + id, Map.of("id", id));
    }

    public static AppException organizationCodeAlreadyExists(String code) {
        return new AppException(WorkspaceErrorCatalog.ORGANIZATION_CODE_ALREADY_EXISTS,
                "Organization code already exists: " + code, Map.of("code", code));
    }

    public static AppException organizationNotActive(String code) {
        return new AppException(WorkspaceErrorCatalog.ORGANIZATION_NOT_ACTIVE,
                "Organization is not active: " + code, Map.of("code", code));
    }

    public static AppException organizationArchivedCannotBeUpdated(String code) {
        return new AppException(WorkspaceErrorCatalog.ORGANIZATION_ARCHIVED_CANNOT_BE_UPDATED,
                "Archived organization cannot be updated: " + code, Map.of("code", code));
    }

    // ── Workspace ─────────────────────────────────────────────────────────────

    public static AppException workspaceNotFound(UUID id) {
        return new AppException(WorkspaceErrorCatalog.WORKSPACE_NOT_FOUND,
                "Workspace not found: " + id, Map.of("id", id));
    }

    public static AppException workspaceCodeAlreadyExists(String code) {
        return new AppException(WorkspaceErrorCatalog.WORKSPACE_CODE_ALREADY_EXISTS,
                "Workspace code already exists in this organization: " + code, Map.of("code", code));
    }

    public static AppException workspaceNotActive(String code) {
        return new AppException(WorkspaceErrorCatalog.WORKSPACE_NOT_ACTIVE,
                "Workspace is not active: " + code, Map.of("code", code));
    }

    public static AppException workspaceArchivedCannotBeUpdated(String code) {
        return new AppException(WorkspaceErrorCatalog.WORKSPACE_ARCHIVED_CANNOT_BE_UPDATED,
                "Archived workspace cannot be updated: " + code, Map.of("code", code));
    }

    // ── Workspace Member ──────────────────────────────────────────────────────

    public static AppException workspaceMemberNotFound(UUID id) {
        return new AppException(WorkspaceErrorCatalog.WORKSPACE_MEMBER_NOT_FOUND,
                "Workspace member not found: " + id, Map.of("id", id));
    }

    public static AppException workspaceMemberAlreadyExists(UUID workspaceId, UUID userId) {
        return new AppException(WorkspaceErrorCatalog.WORKSPACE_MEMBER_ALREADY_EXISTS,
                "User " + userId + " is already a member of workspace " + workspaceId,
                Map.of("workspaceId", workspaceId, "userId", userId));
    }

    public static AppException workspaceOwnerMemberBootstrapFailed(UUID workspaceId) {
        return new AppException(WorkspaceErrorCatalog.WORKSPACE_OWNER_MEMBER_BOOTSTRAP_FAILED,
                "Failed to bootstrap owner membership for workspace: " + workspaceId,
                Map.of("workspaceId", workspaceId));
    }

    public static AppException workspaceMemberCannotBeActivatedForInactiveWorkspace(UUID workspaceId) {
        return new AppException(
                WorkspaceErrorCatalog.WORKSPACE_MEMBER_CANNOT_BE_ACTIVATED_FOR_INACTIVE_WORKSPACE,
                "Workspace member cannot be activated because workspace is not active: " + workspaceId,
                Map.of("workspaceId", workspaceId));
    }

    // ── Team ──────────────────────────────────────────────────────────────────

    public static AppException teamNotFound(UUID id) {
        return new AppException(WorkspaceErrorCatalog.TEAM_NOT_FOUND,
                "Team not found: " + id, Map.of("id", id));
    }

    public static AppException teamCodeAlreadyExists(String code) {
        return new AppException(WorkspaceErrorCatalog.TEAM_CODE_ALREADY_EXISTS,
                "Team code already exists in this workspace: " + code, Map.of("code", code));
    }

    public static AppException teamNotActive(String code) {
        return new AppException(WorkspaceErrorCatalog.TEAM_NOT_ACTIVE,
                "Team is not active: " + code, Map.of("code", code));
    }

    public static AppException teamArchivedCannotBeUpdated(String code) {
        return new AppException(WorkspaceErrorCatalog.TEAM_ARCHIVED_CANNOT_BE_UPDATED,
                "Archived team cannot be updated: " + code, Map.of("code", code));
    }

    // ── Team Member ───────────────────────────────────────────────────────────

    public static AppException teamMemberNotFound(UUID teamId, UUID userId) {
        return new AppException(WorkspaceErrorCatalog.TEAM_MEMBER_NOT_FOUND,
                "Team member not found: userId=" + userId + " in teamId=" + teamId,
                Map.of("teamId", teamId, "userId", userId));
    }

    public static AppException teamMemberAlreadyExists(UUID teamId, UUID userId) {
        return new AppException(WorkspaceErrorCatalog.TEAM_MEMBER_ALREADY_EXISTS,
                "User " + userId + " is already a member of team " + teamId,
                Map.of("teamId", teamId, "userId", userId));
    }

    public static AppException teamMemberRequiresWorkspaceMember(UUID userId) {
        return new AppException(WorkspaceErrorCatalog.TEAM_MEMBER_REQUIRES_WORKSPACE_MEMBER,
                "User must be an active workspace member before joining a team: userId=" + userId,
                Map.of("userId", userId));
    }

    // ── IAM Integration ───────────────────────────────────────────────────────

    public static AppException workspaceIamBootstrapFailed(String entityType, UUID entityId) {
        return new AppException(WorkspaceErrorCatalog.WORKSPACE_IAM_BOOTSTRAP_FAILED,
                "Failed to bootstrap IAM access for " + entityType + " " + entityId,
                Map.of("entityType", entityType, "entityId", entityId));
    }

    public static AppException workspaceAccessDenied(String action) {
        return new AppException(WorkspaceErrorCatalog.WORKSPACE_ACCESS_DENIED,
                "Access denied: " + action, Map.of("action", action));
    }
}
