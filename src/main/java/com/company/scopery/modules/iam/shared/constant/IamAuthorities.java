package com.company.scopery.modules.iam.shared.constant;

import java.util.List;

public final class IamAuthorities {

    private IamAuthorities() {}

    // ── System scope ─────────────────────────────────────────────────────────

    public static final IamPermissionAction SYSTEM_IAM_VIEW_USER = IamPermissionAction.of(
            IamPermissionCodes.SYSTEM_IAM_MANAGEMENT, IamActionCodes.VIEW_USER, "VIEW_IAM_USER");
    public static final IamPermissionAction SYSTEM_IAM_CREATE_USER = IamPermissionAction.of(
            IamPermissionCodes.SYSTEM_IAM_MANAGEMENT, IamActionCodes.CREATE_USER, "CREATE_IAM_USER");
    public static final IamPermissionAction SYSTEM_IAM_MANAGE_USER = IamPermissionAction.of(
            IamPermissionCodes.SYSTEM_IAM_MANAGEMENT, IamActionCodes.MANAGE_USER, "MANAGE_IAM_USER");
    public static final IamPermissionAction SYSTEM_IAM_VIEW_ROLE = IamPermissionAction.of(
            IamPermissionCodes.SYSTEM_IAM_MANAGEMENT, IamActionCodes.VIEW_ROLE, "VIEW_IAM_ROLE");
    public static final IamPermissionAction SYSTEM_IAM_MANAGE_ROLE = IamPermissionAction.of(
            IamPermissionCodes.SYSTEM_IAM_MANAGEMENT, IamActionCodes.MANAGE_ROLE, "MANAGE_IAM_ROLE");
    public static final IamPermissionAction SYSTEM_IAM_VIEW_RIGHT = IamPermissionAction.of(
            IamPermissionCodes.SYSTEM_IAM_MANAGEMENT, IamActionCodes.VIEW_RIGHT, "VIEW_IAM_RIGHT");
    public static final IamPermissionAction SYSTEM_IAM_VIEW_ACCESS_GRANT = IamPermissionAction.of(
            IamPermissionCodes.SYSTEM_IAM_MANAGEMENT, IamActionCodes.VIEW_ACCESS_GRANT, "VIEW_ACCESS_GRANT");
    public static final IamPermissionAction SYSTEM_IAM_MANAGE_ACCESS_GRANT = IamPermissionAction.of(
            IamPermissionCodes.SYSTEM_IAM_MANAGEMENT, IamActionCodes.MANAGE_ACCESS_GRANT, "MANAGE_ACCESS_GRANT");

    public static final IamPermissionAction SYSTEM_RESOURCE_VIEW = IamPermissionAction.of(
            IamPermissionCodes.SYSTEM_RESOURCE_MANAGEMENT, IamActionCodes.VIEW, "VIEW_IAM_RESOURCE");
    public static final IamPermissionAction SYSTEM_RESOURCE_MANAGE = IamPermissionAction.of(
            IamPermissionCodes.SYSTEM_RESOURCE_MANAGEMENT, IamActionCodes.MANAGE, "MANAGE_IAM_RESOURCE");

    public static final IamPermissionAction SYSTEM_GOVERNANCE_VIEW = IamPermissionAction.of(
            IamPermissionCodes.SYSTEM_GOVERNANCE_MANAGEMENT, IamActionCodes.VIEW, "SYSTEM_VIEW_GOVERNANCE");
    public static final IamPermissionAction SYSTEM_GOVERNANCE_MANAGE = IamPermissionAction.of(
            IamPermissionCodes.SYSTEM_GOVERNANCE_MANAGEMENT, IamActionCodes.MANAGE, "SYSTEM_MANAGE_GOVERNANCE");
    public static final IamPermissionAction SYSTEM_GOVERNANCE_MANAGE_ROLE = IamPermissionAction.of(
            IamPermissionCodes.SYSTEM_GOVERNANCE_MANAGEMENT, IamActionCodes.MANAGE_ROLE, "SYSTEM_MANAGE_ROLE");
    public static final IamPermissionAction SYSTEM_GOVERNANCE_MANAGE_DOCUMENT_TYPE = IamPermissionAction.of(
            IamPermissionCodes.SYSTEM_GOVERNANCE_MANAGEMENT, IamActionCodes.MANAGE_DOCUMENT_TYPE,
            "SYSTEM_MANAGE_DOCUMENT_TYPE");
    public static final IamPermissionAction SYSTEM_GOVERNANCE_MANAGE_PHASE_DEFINITION = IamPermissionAction.of(
            IamPermissionCodes.SYSTEM_GOVERNANCE_MANAGEMENT, IamActionCodes.MANAGE_PHASE_DEFINITION,
            "SYSTEM_MANAGE_PHASE_DEFINITION");

    public static final IamPermissionAction SYSTEM_NOTIFICATION_VIEW = IamPermissionAction.of(
            IamPermissionCodes.SYSTEM_NOTIFICATION_MANAGEMENT, IamActionCodes.VIEW_NOTIFICATION,
            "SYSTEM_VIEW_NOTIFICATION");
    public static final IamPermissionAction SYSTEM_NOTIFICATION_MANAGE = IamPermissionAction.of(
            IamPermissionCodes.SYSTEM_NOTIFICATION_MANAGEMENT, IamActionCodes.MANAGE_NOTIFICATION,
            "SYSTEM_MANAGE_NOTIFICATION");
    public static final IamPermissionAction SYSTEM_NOTIFICATION_MANAGE_TEMPLATE = IamPermissionAction.of(
            IamPermissionCodes.SYSTEM_NOTIFICATION_MANAGEMENT, IamActionCodes.MANAGE_TEMPLATE,
            "SYSTEM_MANAGE_NOTIFICATION_TEMPLATE");
    public static final IamPermissionAction SYSTEM_NOTIFICATION_MANAGE_RULE = IamPermissionAction.of(
            IamPermissionCodes.SYSTEM_NOTIFICATION_MANAGEMENT, IamActionCodes.MANAGE_RULE,
            "SYSTEM_MANAGE_NOTIFICATION_RULE");
    public static final IamPermissionAction SYSTEM_NOTIFICATION_VIEW_DELIVERY = IamPermissionAction.of(
            IamPermissionCodes.SYSTEM_NOTIFICATION_MANAGEMENT, IamActionCodes.VIEW_DELIVERY,
            "SYSTEM_VIEW_NOTIFICATION_DELIVERY");
    public static final IamPermissionAction SYSTEM_NOTIFICATION_RETRY_DELIVERY = IamPermissionAction.of(
            IamPermissionCodes.SYSTEM_NOTIFICATION_MANAGEMENT, IamActionCodes.RETRY_DELIVERY,
            "SYSTEM_RETRY_NOTIFICATION_DELIVERY");

    public static final IamPermissionAction SYSTEM_EVENT_REGISTRY_MANAGE = IamPermissionAction.of(
            IamPermissionCodes.SYSTEM_EVENT_REGISTRY_MANAGEMENT, IamActionCodes.MANAGE, "SYSTEM_MANAGE_EVENT_REGISTRY");

    // ── Organization scope ───────────────────────────────────────────────────

    public static final IamPermissionAction ORGANIZATION_VIEW = IamPermissionAction.of(
            IamPermissionCodes.ORGANIZATION_MANAGEMENT, IamActionCodes.VIEW, "VIEW_ORGANIZATION");
    public static final IamPermissionAction ORGANIZATION_CREATE = IamPermissionAction.of(
            IamPermissionCodes.ORGANIZATION_MANAGEMENT, IamActionCodes.CREATE, "CREATE_ORGANIZATION");
    public static final IamPermissionAction ORGANIZATION_UPDATE = IamPermissionAction.of(
            IamPermissionCodes.ORGANIZATION_MANAGEMENT, IamActionCodes.UPDATE, "UPDATE_ORGANIZATION");
    public static final IamPermissionAction ORGANIZATION_ARCHIVE = IamPermissionAction.of(
            IamPermissionCodes.ORGANIZATION_MANAGEMENT, IamActionCodes.ARCHIVE, "ARCHIVE_ORGANIZATION");
    public static final IamPermissionAction ORGANIZATION_MANAGE = IamPermissionAction.of(
            IamPermissionCodes.ORGANIZATION_MANAGEMENT, IamActionCodes.MANAGE, "MANAGE_ORGANIZATION");
    public static final IamPermissionAction ORGANIZATION_CREATE_WORKSPACE = IamPermissionAction.of(
            IamPermissionCodes.ORGANIZATION_MANAGEMENT, IamActionCodes.CREATE_WORKSPACE, "CREATE_WORKSPACE");

    // ── Workspace scope ──────────────────────────────────────────────────────

    public static final IamPermissionAction WORKSPACE_VIEW = IamPermissionAction.of(
            IamPermissionCodes.WORKSPACE_MANAGEMENT, IamActionCodes.VIEW, "VIEW_WORKSPACE");
    public static final IamPermissionAction WORKSPACE_UPDATE = IamPermissionAction.of(
            IamPermissionCodes.WORKSPACE_MANAGEMENT, IamActionCodes.UPDATE, "UPDATE_WORKSPACE");
    public static final IamPermissionAction WORKSPACE_ARCHIVE = IamPermissionAction.of(
            IamPermissionCodes.WORKSPACE_MANAGEMENT, IamActionCodes.ARCHIVE, "ARCHIVE_WORKSPACE");
    public static final IamPermissionAction WORKSPACE_MANAGE = IamPermissionAction.of(
            IamPermissionCodes.WORKSPACE_MANAGEMENT, IamActionCodes.MANAGE, "MANAGE_WORKSPACE");
    public static final IamPermissionAction WORKSPACE_MANAGE_SETTING = IamPermissionAction.of(
            IamPermissionCodes.WORKSPACE_MANAGEMENT, IamActionCodes.MANAGE_SETTING, "MANAGE_WORKSPACE_SETTING");

    public static final IamPermissionAction TEAM_VIEW = IamPermissionAction.of(
            IamPermissionCodes.TEAM_MANAGEMENT, IamActionCodes.VIEW, "VIEW_TEAM");
    public static final IamPermissionAction TEAM_CREATE = IamPermissionAction.of(
            IamPermissionCodes.TEAM_MANAGEMENT, IamActionCodes.CREATE, "CREATE_TEAM");
    public static final IamPermissionAction TEAM_UPDATE = IamPermissionAction.of(
            IamPermissionCodes.TEAM_MANAGEMENT, IamActionCodes.UPDATE, "UPDATE_TEAM");
    public static final IamPermissionAction TEAM_ARCHIVE = IamPermissionAction.of(
            IamPermissionCodes.TEAM_MANAGEMENT, IamActionCodes.ARCHIVE, "ARCHIVE_TEAM");
    public static final IamPermissionAction TEAM_MANAGE = IamPermissionAction.of(
            IamPermissionCodes.TEAM_MANAGEMENT, IamActionCodes.MANAGE, "MANAGE_TEAM");

    public static final IamPermissionAction WORKSPACE_ACCESS_MANAGE_MEMBER = IamPermissionAction.of(
            IamPermissionCodes.WORKSPACE_ACCESS_MANAGEMENT, IamActionCodes.MANAGE_MEMBER, "MANAGE_MEMBER");
    public static final IamPermissionAction WORKSPACE_ACCESS_MANAGE_ACCESS = IamPermissionAction.of(
            IamPermissionCodes.WORKSPACE_ACCESS_MANAGEMENT, IamActionCodes.MANAGE_ACCESS, "MANAGE_ACCESS");
    public static final IamPermissionAction WORKSPACE_ACCESS_MANAGE_PERMISSION = IamPermissionAction.of(
            IamPermissionCodes.WORKSPACE_ACCESS_MANAGEMENT, IamActionCodes.MANAGE_PERMISSION, "MANAGE_PERMISSION");
    public static final IamPermissionAction WORKSPACE_INVITE_MEMBER = IamPermissionAction.of(
            IamPermissionCodes.WORKSPACE_ACCESS_MANAGEMENT, IamActionCodes.INVITE_MEMBER, "WORKSPACE_INVITE_MEMBER");
    public static final IamPermissionAction WORKSPACE_MANAGE_INVITATION = IamPermissionAction.of(
            IamPermissionCodes.WORKSPACE_ACCESS_MANAGEMENT, IamActionCodes.MANAGE_INVITATION,
            "WORKSPACE_MANAGE_INVITATION");
    public static final IamPermissionAction WORKSPACE_REQUEST_JOIN = IamPermissionAction.of(
            IamPermissionCodes.WORKSPACE_ACCESS_MANAGEMENT, IamActionCodes.REQUEST_JOIN, "WORKSPACE_REQUEST_JOIN");
    public static final IamPermissionAction WORKSPACE_MANAGE_JOIN_REQUEST = IamPermissionAction.of(
            IamPermissionCodes.WORKSPACE_ACCESS_MANAGEMENT, IamActionCodes.MANAGE_JOIN_REQUEST,
            "WORKSPACE_MANAGE_JOIN_REQUEST");

    public static final IamPermissionAction WORKSPACE_ROLE_VIEW = IamPermissionAction.of(
            IamPermissionCodes.WORKSPACE_ROLE_MANAGEMENT, IamActionCodes.VIEW, "VIEW_ROLE");
    public static final IamPermissionAction WORKSPACE_ROLE_CREATE = IamPermissionAction.of(
            IamPermissionCodes.WORKSPACE_ROLE_MANAGEMENT, IamActionCodes.CREATE, "CREATE_ROLE");
    public static final IamPermissionAction WORKSPACE_ROLE_UPDATE = IamPermissionAction.of(
            IamPermissionCodes.WORKSPACE_ROLE_MANAGEMENT, IamActionCodes.UPDATE, "UPDATE_ROLE");
    public static final IamPermissionAction WORKSPACE_ROLE_DELETE = IamPermissionAction.of(
            IamPermissionCodes.WORKSPACE_ROLE_MANAGEMENT, IamActionCodes.DELETE, "DELETE_ROLE");
    public static final IamPermissionAction WORKSPACE_ROLE_ASSIGN = IamPermissionAction.of(
            IamPermissionCodes.WORKSPACE_ROLE_MANAGEMENT, IamActionCodes.ASSIGN_ROLE, "ASSIGN_ROLE");

    public static final IamPermissionAction WORKSPACE_MEMBER_VIEW = IamPermissionAction.of(
            IamPermissionCodes.WORKSPACE_MEMBER_MANAGEMENT, IamActionCodes.VIEW, "VIEW_WORKSPACE_MEMBER");
    public static final IamPermissionAction WORKSPACE_MEMBER_ADD = IamPermissionAction.of(
            IamPermissionCodes.WORKSPACE_MEMBER_MANAGEMENT, IamActionCodes.ADD, "ADD_WORKSPACE_MEMBER");
    public static final IamPermissionAction WORKSPACE_MEMBER_REMOVE = IamPermissionAction.of(
            IamPermissionCodes.WORKSPACE_MEMBER_MANAGEMENT, IamActionCodes.REMOVE, "REMOVE_WORKSPACE_MEMBER");

    public static final IamPermissionAction TEAM_MEMBER_VIEW = IamPermissionAction.of(
            IamPermissionCodes.TEAM_MEMBER_MANAGEMENT, IamActionCodes.VIEW, "VIEW_TEAM_MEMBER");
    public static final IamPermissionAction TEAM_MEMBER_ADD = IamPermissionAction.of(
            IamPermissionCodes.TEAM_MEMBER_MANAGEMENT, IamActionCodes.ADD, "ADD_TEAM_MEMBER");
    public static final IamPermissionAction TEAM_MEMBER_REMOVE = IamPermissionAction.of(
            IamPermissionCodes.TEAM_MEMBER_MANAGEMENT, IamActionCodes.REMOVE, "REMOVE_TEAM_MEMBER");

    public static final IamPermissionAction DOCUMENT_TYPE_VIEW = IamPermissionAction.of(
            IamPermissionCodes.DOCUMENT_TYPE_MANAGEMENT, IamActionCodes.VIEW, "VIEW_DOCUMENT_TYPE");
    public static final IamPermissionAction DOCUMENT_TYPE_CREATE = IamPermissionAction.of(
            IamPermissionCodes.DOCUMENT_TYPE_MANAGEMENT, IamActionCodes.CREATE, "CREATE_DOCUMENT_TYPE");
    public static final IamPermissionAction DOCUMENT_TYPE_UPDATE = IamPermissionAction.of(
            IamPermissionCodes.DOCUMENT_TYPE_MANAGEMENT, IamActionCodes.UPDATE, "UPDATE_DOCUMENT_TYPE");
    public static final IamPermissionAction DOCUMENT_TYPE_DELETE = IamPermissionAction.of(
            IamPermissionCodes.DOCUMENT_TYPE_MANAGEMENT, IamActionCodes.DELETE, "DELETE_DOCUMENT_TYPE");
    public static final IamPermissionAction DOCUMENT_TYPE_MANAGE = IamPermissionAction.of(
            IamPermissionCodes.DOCUMENT_TYPE_MANAGEMENT, IamActionCodes.MANAGE, "MANAGE_DOCUMENT_TYPE");

    public static final IamPermissionAction PHASE_DEFINITION_VIEW = IamPermissionAction.of(
            IamPermissionCodes.PHASE_DEFINITION_MANAGEMENT, IamActionCodes.VIEW, "VIEW_PHASE_DEFINITION");
    public static final IamPermissionAction PHASE_DEFINITION_CREATE = IamPermissionAction.of(
            IamPermissionCodes.PHASE_DEFINITION_MANAGEMENT, IamActionCodes.CREATE, "CREATE_PHASE_DEFINITION");
    public static final IamPermissionAction PHASE_DEFINITION_UPDATE = IamPermissionAction.of(
            IamPermissionCodes.PHASE_DEFINITION_MANAGEMENT, IamActionCodes.UPDATE, "UPDATE_PHASE_DEFINITION");
    public static final IamPermissionAction PHASE_DEFINITION_ARCHIVE = IamPermissionAction.of(
            IamPermissionCodes.PHASE_DEFINITION_MANAGEMENT, IamActionCodes.ARCHIVE, "ARCHIVE_PHASE_DEFINITION");
    public static final IamPermissionAction PHASE_DEFINITION_MANAGE = IamPermissionAction.of(
            IamPermissionCodes.PHASE_DEFINITION_MANAGEMENT, IamActionCodes.MANAGE, "MANAGE_PHASE_DEFINITION");

    public static final IamPermissionAction NOTIFICATION_VIEW_TEMPLATE = IamPermissionAction.of(
            IamPermissionCodes.NOTIFICATION_MANAGEMENT, IamActionCodes.VIEW_TEMPLATE, "VIEW_NOTIFICATION_TEMPLATE");
    public static final IamPermissionAction NOTIFICATION_MANAGE_TEMPLATE = IamPermissionAction.of(
            IamPermissionCodes.NOTIFICATION_MANAGEMENT, IamActionCodes.MANAGE_TEMPLATE, "MANAGE_NOTIFICATION_TEMPLATE");
    public static final IamPermissionAction NOTIFICATION_VIEW_RULE = IamPermissionAction.of(
            IamPermissionCodes.NOTIFICATION_MANAGEMENT, IamActionCodes.VIEW_RULE, "VIEW_NOTIFICATION_RULE");
    public static final IamPermissionAction NOTIFICATION_MANAGE_RULE = IamPermissionAction.of(
            IamPermissionCodes.NOTIFICATION_MANAGEMENT, IamActionCodes.MANAGE_RULE, "MANAGE_NOTIFICATION_RULE");

    // ── Project scope ────────────────────────────────────────────────────────

    public static final IamPermissionAction PROJECT_VIEW = IamPermissionAction.of(
            IamPermissionCodes.PROJECT_MANAGEMENT, IamActionCodes.VIEW, "VIEW_PROJECT");
    public static final IamPermissionAction PROJECT_CREATE = IamPermissionAction.of(
            IamPermissionCodes.PROJECT_MANAGEMENT, IamActionCodes.CREATE, "CREATE_PROJECT");
    public static final IamPermissionAction PROJECT_UPDATE = IamPermissionAction.of(
            IamPermissionCodes.PROJECT_MANAGEMENT, IamActionCodes.UPDATE, "UPDATE_PROJECT");
    public static final IamPermissionAction PROJECT_ARCHIVE = IamPermissionAction.of(
            IamPermissionCodes.PROJECT_MANAGEMENT, IamActionCodes.ARCHIVE, "ARCHIVE_PROJECT");
    public static final IamPermissionAction PROJECT_MANAGE = IamPermissionAction.of(
            IamPermissionCodes.PROJECT_MANAGEMENT, IamActionCodes.MANAGE, "MANAGE_PROJECT");

    public static final IamPermissionAction PROJECT_PHASE_VIEW = IamPermissionAction.of(
            IamPermissionCodes.PROJECT_PHASE_MANAGEMENT, IamActionCodes.VIEW, "VIEW_PROJECT_PHASE");
    public static final IamPermissionAction PROJECT_PHASE_CREATE = IamPermissionAction.of(
            IamPermissionCodes.PROJECT_PHASE_MANAGEMENT, IamActionCodes.CREATE, "CREATE_PROJECT_PHASE");
    public static final IamPermissionAction PROJECT_PHASE_UPDATE = IamPermissionAction.of(
            IamPermissionCodes.PROJECT_PHASE_MANAGEMENT, IamActionCodes.UPDATE, "UPDATE_PROJECT_PHASE");
    public static final IamPermissionAction PROJECT_PHASE_ARCHIVE = IamPermissionAction.of(
            IamPermissionCodes.PROJECT_PHASE_MANAGEMENT, IamActionCodes.ARCHIVE, "ARCHIVE_PROJECT_PHASE");
    public static final IamPermissionAction PROJECT_PHASE_MANAGE = IamPermissionAction.of(
            IamPermissionCodes.PROJECT_PHASE_MANAGEMENT, IamActionCodes.MANAGE, "MANAGE_PROJECT_PHASE");

    public static final IamPermissionAction PROJECT_WBS_VIEW = IamPermissionAction.of(
            IamPermissionCodes.PROJECT_WBS_MANAGEMENT, IamActionCodes.VIEW, "VIEW_WBS_NODE");
    public static final IamPermissionAction PROJECT_WBS_CREATE = IamPermissionAction.of(
            IamPermissionCodes.PROJECT_WBS_MANAGEMENT, IamActionCodes.CREATE, "CREATE_WBS_NODE");
    public static final IamPermissionAction PROJECT_WBS_UPDATE = IamPermissionAction.of(
            IamPermissionCodes.PROJECT_WBS_MANAGEMENT, IamActionCodes.UPDATE, "UPDATE_WBS_NODE");
    public static final IamPermissionAction PROJECT_WBS_ARCHIVE = IamPermissionAction.of(
            IamPermissionCodes.PROJECT_WBS_MANAGEMENT, IamActionCodes.ARCHIVE, "ARCHIVE_WBS_NODE");
    public static final IamPermissionAction PROJECT_WBS_MANAGE = IamPermissionAction.of(
            IamPermissionCodes.PROJECT_WBS_MANAGEMENT, IamActionCodes.MANAGE, "MANAGE_WBS_NODE");

    public static final IamPermissionAction PROJECT_TASK_VIEW = IamPermissionAction.of(
            IamPermissionCodes.PROJECT_TASK_MANAGEMENT, IamActionCodes.VIEW, "VIEW_TASK");
    public static final IamPermissionAction PROJECT_TASK_CREATE = IamPermissionAction.of(
            IamPermissionCodes.PROJECT_TASK_MANAGEMENT, IamActionCodes.CREATE, "CREATE_TASK");
    public static final IamPermissionAction PROJECT_TASK_UPDATE = IamPermissionAction.of(
            IamPermissionCodes.PROJECT_TASK_MANAGEMENT, IamActionCodes.UPDATE, "UPDATE_TASK");
    public static final IamPermissionAction PROJECT_TASK_ARCHIVE = IamPermissionAction.of(
            IamPermissionCodes.PROJECT_TASK_MANAGEMENT, IamActionCodes.ARCHIVE, "ARCHIVE_TASK");
    public static final IamPermissionAction PROJECT_TASK_MANAGE = IamPermissionAction.of(
            IamPermissionCodes.PROJECT_TASK_MANAGEMENT, IamActionCodes.MANAGE, "MANAGE_TASK");

    // Backward-compatible constant names used by existing services.
    public static final IamPermissionAction WORKSPACE_MANAGE_MEMBER = WORKSPACE_ACCESS_MANAGE_MEMBER;
    public static final IamPermissionAction WORKSPACE_MANAGE_TEAM = TEAM_MANAGE;
    public static final IamPermissionAction WORKSPACE_MANAGE_ROLE = WORKSPACE_ROLE_ASSIGN;
    public static final IamPermissionAction WORKSPACE_MANAGE_ACCESS = WORKSPACE_ACCESS_MANAGE_ACCESS;
    public static final IamPermissionAction WORKSPACE_MANAGE_PERMISSION = WORKSPACE_ACCESS_MANAGE_PERMISSION;
    public static final IamPermissionAction ROLE_VIEW = WORKSPACE_ROLE_VIEW;
    public static final IamPermissionAction ROLE_CREATE = WORKSPACE_ROLE_CREATE;
    public static final IamPermissionAction ROLE_UPDATE = WORKSPACE_ROLE_UPDATE;
    public static final IamPermissionAction ROLE_DELETE = WORKSPACE_ROLE_DELETE;
    public static final IamPermissionAction ROLE_ASSIGN = WORKSPACE_ROLE_ASSIGN;

    public static List<IamPermissionAction> workspaceOwnerActions() {
        return List.of(
                WORKSPACE_VIEW,
                WORKSPACE_MANAGE,
                WORKSPACE_MANAGE_SETTING,
                WORKSPACE_ACCESS_MANAGE_MEMBER,
                WORKSPACE_ACCESS_MANAGE_ACCESS,
                WORKSPACE_ACCESS_MANAGE_PERMISSION,
                TEAM_VIEW,
                TEAM_CREATE,
                TEAM_UPDATE,
                TEAM_ARCHIVE,
                TEAM_MANAGE,
                WORKSPACE_ROLE_VIEW,
                WORKSPACE_ROLE_CREATE,
                WORKSPACE_ROLE_UPDATE,
                WORKSPACE_ROLE_DELETE,
                WORKSPACE_ROLE_ASSIGN,
                DOCUMENT_TYPE_VIEW,
                DOCUMENT_TYPE_CREATE,
                DOCUMENT_TYPE_UPDATE,
                DOCUMENT_TYPE_DELETE,
                DOCUMENT_TYPE_MANAGE,
                WORKSPACE_INVITE_MEMBER,
                WORKSPACE_MANAGE_INVITATION,
                WORKSPACE_MANAGE_JOIN_REQUEST,
                NOTIFICATION_VIEW_TEMPLATE,
                NOTIFICATION_MANAGE_TEMPLATE,
                NOTIFICATION_VIEW_RULE,
                NOTIFICATION_MANAGE_RULE);
    }

    public static List<IamPermissionAction> organizationOwnerActions() {
        return List.of(
                ORGANIZATION_VIEW,
                ORGANIZATION_MANAGE,
                ORGANIZATION_CREATE_WORKSPACE,
                TEAM_VIEW,
                TEAM_CREATE,
                TEAM_UPDATE,
                TEAM_ARCHIVE,
                TEAM_MANAGE);
    }

    public static List<IamPermissionAction> teamOwnerActions() {
        return List.of(TEAM_VIEW, TEAM_MANAGE);
    }

    public static List<IamPermissionAction> all() {
        return List.of(
                SYSTEM_IAM_VIEW_USER,
                SYSTEM_IAM_CREATE_USER,
                SYSTEM_IAM_VIEW_ROLE,
                SYSTEM_IAM_MANAGE_ROLE,
                SYSTEM_IAM_VIEW_RIGHT,
                SYSTEM_IAM_VIEW_ACCESS_GRANT,
                SYSTEM_IAM_MANAGE_ACCESS_GRANT,
                SYSTEM_RESOURCE_VIEW,
                SYSTEM_RESOURCE_MANAGE,
                SYSTEM_GOVERNANCE_VIEW,
                SYSTEM_GOVERNANCE_MANAGE,
                SYSTEM_GOVERNANCE_MANAGE_ROLE,
                SYSTEM_GOVERNANCE_MANAGE_DOCUMENT_TYPE,
                SYSTEM_NOTIFICATION_VIEW,
                SYSTEM_NOTIFICATION_MANAGE,
                SYSTEM_NOTIFICATION_MANAGE_TEMPLATE,
                SYSTEM_NOTIFICATION_MANAGE_RULE,
                SYSTEM_NOTIFICATION_VIEW_DELIVERY,
                SYSTEM_NOTIFICATION_RETRY_DELIVERY,
                ORGANIZATION_VIEW,
                ORGANIZATION_CREATE,
                ORGANIZATION_UPDATE,
                ORGANIZATION_ARCHIVE,
                ORGANIZATION_MANAGE,
                ORGANIZATION_CREATE_WORKSPACE,
                WORKSPACE_VIEW,
                WORKSPACE_UPDATE,
                WORKSPACE_ARCHIVE,
                WORKSPACE_MANAGE,
                WORKSPACE_MANAGE_SETTING,
                TEAM_VIEW,
                TEAM_CREATE,
                TEAM_UPDATE,
                TEAM_ARCHIVE,
                TEAM_MANAGE,
                WORKSPACE_ACCESS_MANAGE_MEMBER,
                WORKSPACE_ACCESS_MANAGE_ACCESS,
                WORKSPACE_ACCESS_MANAGE_PERMISSION,
                WORKSPACE_INVITE_MEMBER,
                WORKSPACE_MANAGE_INVITATION,
                WORKSPACE_REQUEST_JOIN,
                WORKSPACE_MANAGE_JOIN_REQUEST,
                WORKSPACE_ROLE_VIEW,
                WORKSPACE_ROLE_CREATE,
                WORKSPACE_ROLE_UPDATE,
                WORKSPACE_ROLE_DELETE,
                WORKSPACE_ROLE_ASSIGN,
                WORKSPACE_MEMBER_VIEW,
                WORKSPACE_MEMBER_ADD,
                WORKSPACE_MEMBER_REMOVE,
                TEAM_MEMBER_VIEW,
                TEAM_MEMBER_ADD,
                TEAM_MEMBER_REMOVE,
                DOCUMENT_TYPE_VIEW,
                DOCUMENT_TYPE_CREATE,
                DOCUMENT_TYPE_UPDATE,
                DOCUMENT_TYPE_DELETE,
                DOCUMENT_TYPE_MANAGE,
                PROJECT_VIEW,
                PROJECT_CREATE,
                PROJECT_UPDATE,
                PROJECT_ARCHIVE,
                PROJECT_MANAGE,
                PROJECT_PHASE_VIEW,
                PROJECT_PHASE_CREATE,
                PROJECT_PHASE_UPDATE,
                PROJECT_PHASE_ARCHIVE,
                PROJECT_PHASE_MANAGE,
                PROJECT_WBS_VIEW,
                PROJECT_WBS_CREATE,
                PROJECT_WBS_UPDATE,
                PROJECT_WBS_ARCHIVE,
                PROJECT_WBS_MANAGE,
                PROJECT_TASK_VIEW,
                PROJECT_TASK_CREATE,
                PROJECT_TASK_UPDATE,
                PROJECT_TASK_ARCHIVE,
                PROJECT_TASK_MANAGE,
                NOTIFICATION_VIEW_TEMPLATE,
                NOTIFICATION_MANAGE_TEMPLATE,
                NOTIFICATION_VIEW_RULE,
                NOTIFICATION_MANAGE_RULE,
                SYSTEM_EVENT_REGISTRY_MANAGE);
    }
}
