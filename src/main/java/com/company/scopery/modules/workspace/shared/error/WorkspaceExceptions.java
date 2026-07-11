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

    public static AppException workspaceIamBootstrapFailed(String entityType, UUID entityId, String reason) {
        return new AppException(WorkspaceErrorCatalog.WORKSPACE_IAM_BOOTSTRAP_FAILED,
                "Failed to initialize access for " + entityType.toLowerCase() + ". Please try again or contact support.",
                Map.of("entityType", entityType));
    }

    public static AppException workspaceAccessDenied(String action) {
        return new AppException(WorkspaceErrorCatalog.WORKSPACE_ACCESS_DENIED,
                "Access denied: " + action, Map.of("action", action));
    }

    // ── Onboarding ────────────────────────────────────────────────────────────

    public static AppException onboardingInvalidStep() {
        return new AppException(WorkspaceErrorCatalog.WORKSPACE_ONBOARDING_INVALID_STEP);
    }

    public static AppException onboardingAlreadyInProgressOrCompleted() {
        return new AppException(WorkspaceErrorCatalog.WORKSPACE_ONBOARDING_ALREADY_COMPLETED,
                "Your workspace onboarding is already in progress or completed", null);
    }

    public static AppException onboardingOptionNotSupported() {
        return new AppException(WorkspaceErrorCatalog.WORKSPACE_ONBOARDING_OPTION_NOT_SUPPORTED);
    }

    // ── Org Member ────────────────────────────────────────────────────────────

    public static AppException orgMemberNotFound(UUID id) {
        return new AppException(WorkspaceErrorCatalog.ORG_MEMBER_NOT_FOUND,
                "Organization member not found: " + id, Map.of("id", id));
    }

    public static AppException orgMemberAlreadyExists(UUID organizationId, UUID userId) {
        return new AppException(WorkspaceErrorCatalog.ORG_MEMBER_ALREADY_EXISTS,
                "User " + userId + " is already a member of organization " + organizationId,
                Map.of("organizationId", organizationId, "userId", userId));
    }

    public static AppException orgMemberCannotRemoveOwner(UUID userId) {
        return new AppException(WorkspaceErrorCatalog.ORG_MEMBER_CANNOT_REMOVE_OWNER,
                "Organization owner cannot be removed: " + userId, Map.of("userId", userId));
    }

    // ── Org Invitation ────────────────────────────────────────────────────────

    public static AppException orgInvitationNotFound(UUID id) {
        return new AppException(WorkspaceErrorCatalog.ORG_INVITATION_NOT_FOUND,
                "Organization invitation not found: " + id, Map.of("id", id));
    }

    public static AppException orgInvitationNotFound(String token) {
        return new AppException(WorkspaceErrorCatalog.ORG_INVITATION_NOT_FOUND,
                "Organization invitation not found for token", Map.of("token", token));
    }

    public static AppException orgInvitationAlreadyMember(UUID organizationId, UUID userId) {
        return new AppException(WorkspaceErrorCatalog.ORG_INVITATION_ALREADY_MEMBER,
                "User " + userId + " is already a member of organization " + organizationId,
                Map.of("organizationId", organizationId, "userId", userId));
    }

    public static AppException orgInvitationExpired(UUID invitationId) {
        return new AppException(WorkspaceErrorCatalog.ORG_INVITATION_EXPIRED,
                "Organization invitation has expired: " + invitationId, Map.of("invitationId", invitationId));
    }

    public static AppException orgInvitationNotPending(UUID invitationId) {
        return new AppException(WorkspaceErrorCatalog.ORG_INVITATION_NOT_PENDING,
                "Organization invitation is no longer pending: " + invitationId, Map.of("invitationId", invitationId));
    }

    // ── Org Team ──────────────────────────────────────────────────────────────

    public static AppException orgTeamNotFound(UUID id) {
        return new AppException(WorkspaceErrorCatalog.ORG_TEAM_NOT_FOUND,
                "Organization team not found: " + id, Map.of("id", id));
    }

    public static AppException orgTeamCodeAlreadyExists(String code, UUID organizationId) {
        return new AppException(WorkspaceErrorCatalog.ORG_TEAM_CODE_ALREADY_EXISTS,
                "Team code already exists in organization " + organizationId + ": " + code,
                Map.of("code", code, "organizationId", organizationId));
    }

    public static AppException orgTeamNotActive(String code) {
        return new AppException(WorkspaceErrorCatalog.ORG_TEAM_NOT_ACTIVE,
                "Organization team is not active: " + code, Map.of("code", code));
    }

    public static AppException orgTeamArchivedCannotBeUpdated(UUID id) {
        return new AppException(WorkspaceErrorCatalog.ORG_TEAM_ARCHIVED_CANNOT_BE_UPDATED,
                "Archived organization team cannot be updated: " + id, Map.of("id", id));
    }

    public static AppException orgTeamMemberNotFound(UUID teamId, UUID userId) {
        return new AppException(WorkspaceErrorCatalog.ORG_TEAM_MEMBER_NOT_FOUND,
                "Team member not found: userId=" + userId + " in teamId=" + teamId,
                Map.of("teamId", teamId, "userId", userId));
    }

    public static AppException orgTeamMemberAlreadyExists(UUID teamId, UUID userId) {
        return new AppException(WorkspaceErrorCatalog.ORG_TEAM_MEMBER_ALREADY_EXISTS,
                "User " + userId + " is already a member of team " + teamId,
                Map.of("teamId", teamId, "userId", userId));
    }

    public static AppException orgTeamMemberRequiresOrgMember(UUID userId, UUID organizationId) {
        return new AppException(WorkspaceErrorCatalog.ORG_TEAM_MEMBER_REQUIRES_ORG_MEMBER,
                "User " + userId + " must be an active org member of " + organizationId,
                Map.of("userId", userId, "organizationId", organizationId));
    }

    public static AppException orgTeamWorkspaceAssignmentNotFound(UUID id) {
        return new AppException(WorkspaceErrorCatalog.ORG_TEAM_WORKSPACE_ASSIGNMENT_NOT_FOUND,
                "Org team workspace assignment not found: " + id, Map.of("id", id));
    }

    public static AppException orgTeamWorkspaceAssignmentAlreadyExists(UUID teamId, UUID workspaceId) {
        return new AppException(WorkspaceErrorCatalog.ORG_TEAM_WORKSPACE_ASSIGNMENT_ALREADY_EXISTS,
                "Team " + teamId + " is already assigned to workspace " + workspaceId,
                Map.of("teamId", teamId, "workspaceId", workspaceId));
    }

    public static AppException orgTeamCrossOrganizationAssignment(UUID teamId, UUID workspaceId) {
        return new AppException(WorkspaceErrorCatalog.ORG_TEAM_CROSS_ORGANIZATION_ASSIGNMENT,
                "Team " + teamId + " and workspace " + workspaceId + " belong to different organizations",
                Map.of("teamId", teamId, "workspaceId", workspaceId));
    }
}
