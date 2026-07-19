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
    public static final IamPermissionAction DOCUMENT_TYPE_ARCHIVE = IamPermissionAction.of(
            IamPermissionCodes.DOCUMENT_TYPE_MANAGEMENT, IamActionCodes.ARCHIVE, "ARCHIVE_DOCUMENT_TYPE");
    public static final IamPermissionAction DOCUMENT_TYPE_MANAGE = IamPermissionAction.of(
            IamPermissionCodes.DOCUMENT_TYPE_MANAGEMENT, IamActionCodes.MANAGE, "MANAGE_DOCUMENT_TYPE");

    /** Phase 08 alias rights (KNOWLEDGE_* naming) — same permission/action as DOCUMENT_TYPE_*. */
    public static final IamPermissionAction KNOWLEDGE_DOCUMENT_TYPE_VIEW = IamPermissionAction.of(
            IamPermissionCodes.DOCUMENT_TYPE_MANAGEMENT, IamActionCodes.VIEW, "KNOWLEDGE_DOCUMENT_TYPE_VIEW");
    public static final IamPermissionAction KNOWLEDGE_DOCUMENT_TYPE_CREATE = IamPermissionAction.of(
            IamPermissionCodes.DOCUMENT_TYPE_MANAGEMENT, IamActionCodes.CREATE, "KNOWLEDGE_DOCUMENT_TYPE_CREATE");
    public static final IamPermissionAction KNOWLEDGE_DOCUMENT_TYPE_UPDATE = IamPermissionAction.of(
            IamPermissionCodes.DOCUMENT_TYPE_MANAGEMENT, IamActionCodes.UPDATE, "KNOWLEDGE_DOCUMENT_TYPE_UPDATE");
    public static final IamPermissionAction KNOWLEDGE_DOCUMENT_TYPE_ARCHIVE = IamPermissionAction.of(
            IamPermissionCodes.DOCUMENT_TYPE_MANAGEMENT, IamActionCodes.ARCHIVE, "KNOWLEDGE_DOCUMENT_TYPE_ARCHIVE");
    public static final IamPermissionAction KNOWLEDGE_DOCUMENT_TYPE_MANAGE = IamPermissionAction.of(
            IamPermissionCodes.DOCUMENT_TYPE_MANAGEMENT, IamActionCodes.MANAGE, "KNOWLEDGE_DOCUMENT_TYPE_MANAGE");

    public static final IamPermissionAction DOCUMENT_TYPE_FIELD_VIEW = IamPermissionAction.of(
            IamPermissionCodes.DOCUMENT_TYPE_FIELD_MANAGEMENT, IamActionCodes.VIEW, "KNOWLEDGE_DOCUMENT_TYPE_FIELD_VIEW");
    public static final IamPermissionAction DOCUMENT_TYPE_FIELD_CREATE = IamPermissionAction.of(
            IamPermissionCodes.DOCUMENT_TYPE_FIELD_MANAGEMENT, IamActionCodes.CREATE, "KNOWLEDGE_DOCUMENT_TYPE_FIELD_CREATE");
    public static final IamPermissionAction DOCUMENT_TYPE_FIELD_UPDATE = IamPermissionAction.of(
            IamPermissionCodes.DOCUMENT_TYPE_FIELD_MANAGEMENT, IamActionCodes.UPDATE, "KNOWLEDGE_DOCUMENT_TYPE_FIELD_UPDATE");
    public static final IamPermissionAction DOCUMENT_TYPE_FIELD_ARCHIVE = IamPermissionAction.of(
            IamPermissionCodes.DOCUMENT_TYPE_FIELD_MANAGEMENT, IamActionCodes.ARCHIVE, "KNOWLEDGE_DOCUMENT_TYPE_FIELD_ARCHIVE");
    public static final IamPermissionAction DOCUMENT_TYPE_FIELD_MANAGE = IamPermissionAction.of(
            IamPermissionCodes.DOCUMENT_TYPE_FIELD_MANAGEMENT, IamActionCodes.MANAGE, "KNOWLEDGE_DOCUMENT_TYPE_FIELD_MANAGE");

    public static final IamPermissionAction KNOWLEDGE_CLASSIFICATION_VIEW = IamPermissionAction.of(
            IamPermissionCodes.KNOWLEDGE_CLASSIFICATION_MANAGEMENT, IamActionCodes.VIEW, "KNOWLEDGE_CLASSIFICATION_VIEW");

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

    public static final IamPermissionAction PROJECT_TEMPLATE_VIEW = IamPermissionAction.of(
            IamPermissionCodes.PROJECT_TEMPLATE_MANAGEMENT, IamActionCodes.VIEW, "VIEW_PROJECT_TEMPLATE");
    public static final IamPermissionAction PROJECT_TEMPLATE_CREATE = IamPermissionAction.of(
            IamPermissionCodes.PROJECT_TEMPLATE_MANAGEMENT, IamActionCodes.CREATE, "CREATE_PROJECT_TEMPLATE");
    public static final IamPermissionAction PROJECT_TEMPLATE_UPDATE = IamPermissionAction.of(
            IamPermissionCodes.PROJECT_TEMPLATE_MANAGEMENT, IamActionCodes.UPDATE, "UPDATE_PROJECT_TEMPLATE");
    /**
     * Phase 11: publish uses UPDATE action code with PUBLISH_PROJECT_TEMPLATE right code.
     */
    public static final IamPermissionAction PROJECT_TEMPLATE_PUBLISH = IamPermissionAction.of(
            IamPermissionCodes.PROJECT_TEMPLATE_MANAGEMENT, IamActionCodes.UPDATE, "PUBLISH_PROJECT_TEMPLATE");
    public static final IamPermissionAction PROJECT_TEMPLATE_ARCHIVE = IamPermissionAction.of(
            IamPermissionCodes.PROJECT_TEMPLATE_MANAGEMENT, IamActionCodes.ARCHIVE, "ARCHIVE_PROJECT_TEMPLATE");
    public static final IamPermissionAction PROJECT_TEMPLATE_APPLY = IamPermissionAction.of(
            IamPermissionCodes.PROJECT_TEMPLATE_MANAGEMENT, IamActionCodes.APPLY, "APPLY_PROJECT_TEMPLATE");
    public static final IamPermissionAction PROJECT_TEMPLATE_MANAGE = IamPermissionAction.of(
            IamPermissionCodes.PROJECT_TEMPLATE_MANAGEMENT, IamActionCodes.MANAGE, "MANAGE_PROJECT_TEMPLATE");

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

    // ── Resource Capacity scope (Phase 12) ──────────────────────────────────

    public static final IamPermissionAction CAPACITY_CALENDAR_VIEW = IamPermissionAction.of(
            IamPermissionCodes.CAPACITY_CALENDAR_MANAGEMENT, IamActionCodes.VIEW, "CAPACITY_CALENDAR_VIEW");
    public static final IamPermissionAction CAPACITY_CALENDAR_CREATE = IamPermissionAction.of(
            IamPermissionCodes.CAPACITY_CALENDAR_MANAGEMENT, IamActionCodes.CREATE, "CAPACITY_CALENDAR_CREATE");
    public static final IamPermissionAction CAPACITY_CALENDAR_UPDATE = IamPermissionAction.of(
            IamPermissionCodes.CAPACITY_CALENDAR_MANAGEMENT, IamActionCodes.UPDATE, "CAPACITY_CALENDAR_UPDATE");
    public static final IamPermissionAction CAPACITY_CALENDAR_ARCHIVE = IamPermissionAction.of(
            IamPermissionCodes.CAPACITY_CALENDAR_MANAGEMENT, IamActionCodes.ARCHIVE, "CAPACITY_CALENDAR_ARCHIVE");
    public static final IamPermissionAction CAPACITY_CALENDAR_MANAGE = IamPermissionAction.of(
            IamPermissionCodes.CAPACITY_CALENDAR_MANAGEMENT, IamActionCodes.MANAGE, "CAPACITY_CALENDAR_MANAGE");

    public static final IamPermissionAction CAPACITY_PROFILE_VIEW = IamPermissionAction.of(
            IamPermissionCodes.CAPACITY_PROFILE_MANAGEMENT, IamActionCodes.VIEW, "CAPACITY_PROFILE_VIEW");
    public static final IamPermissionAction CAPACITY_PROFILE_CREATE = IamPermissionAction.of(
            IamPermissionCodes.CAPACITY_PROFILE_MANAGEMENT, IamActionCodes.CREATE, "CAPACITY_PROFILE_CREATE");
    public static final IamPermissionAction CAPACITY_PROFILE_UPDATE = IamPermissionAction.of(
            IamPermissionCodes.CAPACITY_PROFILE_MANAGEMENT, IamActionCodes.UPDATE, "CAPACITY_PROFILE_UPDATE");
    public static final IamPermissionAction CAPACITY_PROFILE_ARCHIVE = IamPermissionAction.of(
            IamPermissionCodes.CAPACITY_PROFILE_MANAGEMENT, IamActionCodes.ARCHIVE, "CAPACITY_PROFILE_ARCHIVE");
    public static final IamPermissionAction CAPACITY_PROFILE_MANAGE = IamPermissionAction.of(
            IamPermissionCodes.CAPACITY_PROFILE_MANAGEMENT, IamActionCodes.MANAGE, "CAPACITY_PROFILE_MANAGE");

    public static final IamPermissionAction PROJECT_ALLOCATION_VIEW = IamPermissionAction.of(
            IamPermissionCodes.PROJECT_ALLOCATION_MANAGEMENT, IamActionCodes.VIEW, "PROJECT_ALLOCATION_VIEW");
    public static final IamPermissionAction PROJECT_ALLOCATION_CREATE = IamPermissionAction.of(
            IamPermissionCodes.PROJECT_ALLOCATION_MANAGEMENT, IamActionCodes.CREATE, "PROJECT_ALLOCATION_CREATE");
    public static final IamPermissionAction PROJECT_ALLOCATION_UPDATE = IamPermissionAction.of(
            IamPermissionCodes.PROJECT_ALLOCATION_MANAGEMENT, IamActionCodes.UPDATE, "PROJECT_ALLOCATION_UPDATE");
    public static final IamPermissionAction PROJECT_ALLOCATION_ARCHIVE = IamPermissionAction.of(
            IamPermissionCodes.PROJECT_ALLOCATION_MANAGEMENT, IamActionCodes.ARCHIVE, "PROJECT_ALLOCATION_ARCHIVE");
    public static final IamPermissionAction PROJECT_ALLOCATION_MANAGE = IamPermissionAction.of(
            IamPermissionCodes.PROJECT_ALLOCATION_MANAGEMENT, IamActionCodes.MANAGE, "PROJECT_ALLOCATION_MANAGE");

    public static final IamPermissionAction CAPACITY_VIEW = IamPermissionAction.of(
            IamPermissionCodes.CAPACITY_MANAGEMENT, IamActionCodes.VIEW, "CAPACITY_VIEW");
    public static final IamPermissionAction CAPACITY_CALCULATE = IamPermissionAction.of(
            IamPermissionCodes.CAPACITY_MANAGEMENT, IamActionCodes.CALCULATE, "CAPACITY_CALCULATE");

    // RATE CARD (Phase 15)
    public static final IamPermissionAction COST_ROLE_VIEW = IamPermissionAction.of(
            IamPermissionCodes.COST_ROLE_MANAGEMENT, IamActionCodes.VIEW, "COST_ROLE_VIEW");
    public static final IamPermissionAction COST_ROLE_CREATE = IamPermissionAction.of(
            IamPermissionCodes.COST_ROLE_MANAGEMENT, IamActionCodes.CREATE, "COST_ROLE_CREATE");
    public static final IamPermissionAction COST_ROLE_UPDATE = IamPermissionAction.of(
            IamPermissionCodes.COST_ROLE_MANAGEMENT, IamActionCodes.UPDATE, "COST_ROLE_UPDATE");
    public static final IamPermissionAction COST_ROLE_ARCHIVE = IamPermissionAction.of(
            IamPermissionCodes.COST_ROLE_MANAGEMENT, IamActionCodes.ARCHIVE, "COST_ROLE_ARCHIVE");
    public static final IamPermissionAction COST_ROLE_MANAGE = IamPermissionAction.of(
            IamPermissionCodes.COST_ROLE_MANAGEMENT, IamActionCodes.MANAGE, "COST_ROLE_MANAGE");

    public static final IamPermissionAction RATE_CARD_VIEW = IamPermissionAction.of(
            IamPermissionCodes.RATE_CARD_MANAGEMENT, IamActionCodes.VIEW, "RATE_CARD_VIEW");
    public static final IamPermissionAction RATE_CARD_CREATE = IamPermissionAction.of(
            IamPermissionCodes.RATE_CARD_MANAGEMENT, IamActionCodes.CREATE, "RATE_CARD_CREATE");
    public static final IamPermissionAction RATE_CARD_UPDATE = IamPermissionAction.of(
            IamPermissionCodes.RATE_CARD_MANAGEMENT, IamActionCodes.UPDATE, "RATE_CARD_UPDATE");
    public static final IamPermissionAction RATE_CARD_PUBLISH = IamPermissionAction.of(
            IamPermissionCodes.RATE_CARD_MANAGEMENT, IamActionCodes.PUBLISH, "RATE_CARD_PUBLISH");
    public static final IamPermissionAction RATE_CARD_ARCHIVE = IamPermissionAction.of(
            IamPermissionCodes.RATE_CARD_MANAGEMENT, IamActionCodes.ARCHIVE, "RATE_CARD_ARCHIVE");
    public static final IamPermissionAction RATE_CARD_MANAGE = IamPermissionAction.of(
            IamPermissionCodes.RATE_CARD_MANAGEMENT, IamActionCodes.MANAGE, "RATE_CARD_MANAGE");

    public static final IamPermissionAction RATE_CARD_LINE_VIEW = IamPermissionAction.of(
            IamPermissionCodes.RATE_CARD_LINE_MANAGEMENT, IamActionCodes.VIEW, "RATE_CARD_LINE_VIEW");
    public static final IamPermissionAction RATE_CARD_LINE_CREATE = IamPermissionAction.of(
            IamPermissionCodes.RATE_CARD_LINE_MANAGEMENT, IamActionCodes.CREATE, "RATE_CARD_LINE_CREATE");
    public static final IamPermissionAction RATE_CARD_LINE_UPDATE = IamPermissionAction.of(
            IamPermissionCodes.RATE_CARD_LINE_MANAGEMENT, IamActionCodes.UPDATE, "RATE_CARD_LINE_UPDATE");
    public static final IamPermissionAction RATE_CARD_LINE_DELETE = IamPermissionAction.of(
            IamPermissionCodes.RATE_CARD_LINE_MANAGEMENT, IamActionCodes.DELETE, "RATE_CARD_LINE_DELETE");

    public static final IamPermissionAction INFLATION_POLICY_VIEW = IamPermissionAction.of(
            IamPermissionCodes.INFLATION_POLICY_MANAGEMENT, IamActionCodes.VIEW, "INFLATION_POLICY_VIEW");
    public static final IamPermissionAction INFLATION_POLICY_CREATE = IamPermissionAction.of(
            IamPermissionCodes.INFLATION_POLICY_MANAGEMENT, IamActionCodes.CREATE, "INFLATION_POLICY_CREATE");
    public static final IamPermissionAction INFLATION_POLICY_UPDATE = IamPermissionAction.of(
            IamPermissionCodes.INFLATION_POLICY_MANAGEMENT, IamActionCodes.UPDATE, "INFLATION_POLICY_UPDATE");
    public static final IamPermissionAction INFLATION_POLICY_ARCHIVE = IamPermissionAction.of(
            IamPermissionCodes.INFLATION_POLICY_MANAGEMENT, IamActionCodes.ARCHIVE, "INFLATION_POLICY_ARCHIVE");
    public static final IamPermissionAction INFLATION_POLICY_MANAGE = IamPermissionAction.of(
            IamPermissionCodes.INFLATION_POLICY_MANAGEMENT, IamActionCodes.MANAGE, "INFLATION_POLICY_MANAGE");

    public static final IamPermissionAction RATE_RESOLUTION_VIEW = IamPermissionAction.of(
            IamPermissionCodes.RATE_RESOLUTION_MANAGEMENT, IamActionCodes.VIEW, "RATE_RESOLUTION_VIEW");
    public static final IamPermissionAction RATE_RESOLUTION_PREVIEW = IamPermissionAction.of(
            IamPermissionCodes.RATE_RESOLUTION_MANAGEMENT, IamActionCodes.PREVIEW, "RATE_RESOLUTION_PREVIEW");

    public static final IamPermissionAction MEMBER_COST_ROLE_VIEW = IamPermissionAction.of(
            IamPermissionCodes.MEMBER_COST_ROLE_MANAGEMENT, IamActionCodes.VIEW, "MEMBER_COST_ROLE_VIEW");
    public static final IamPermissionAction MEMBER_COST_ROLE_ASSIGN = IamPermissionAction.of(
            IamPermissionCodes.MEMBER_COST_ROLE_MANAGEMENT, IamActionCodes.ASSIGN, "MEMBER_COST_ROLE_ASSIGN");
    public static final IamPermissionAction MEMBER_COST_ROLE_MANAGE = IamPermissionAction.of(
            IamPermissionCodes.MEMBER_COST_ROLE_MANAGEMENT, IamActionCodes.MANAGE, "MEMBER_COST_ROLE_MANAGE");

    // ESTIMATION (Phase 16)
    public static final IamPermissionAction ESTIMATION_VIEW = IamPermissionAction.of(
            IamPermissionCodes.ESTIMATION_MANAGEMENT, IamActionCodes.VIEW, "ESTIMATION_VIEW");
    public static final IamPermissionAction ESTIMATION_RUN_CREATE = IamPermissionAction.of(
            IamPermissionCodes.ESTIMATION_MANAGEMENT, IamActionCodes.CREATE, "ESTIMATION_RUN_CREATE");
    public static final IamPermissionAction ESTIMATION_RUN_CANCEL = IamPermissionAction.of(
            IamPermissionCodes.ESTIMATION_MANAGEMENT, IamActionCodes.CANCEL, "ESTIMATION_RUN_CANCEL");
    public static final IamPermissionAction ESTIMATION_MARK_CURRENT = IamPermissionAction.of(
            IamPermissionCodes.ESTIMATION_MANAGEMENT, IamActionCodes.MARK_CURRENT, "ESTIMATION_MARK_CURRENT");
    public static final IamPermissionAction ESTIMATION_TASK_VIEW = IamPermissionAction.of(
            IamPermissionCodes.ESTIMATION_MANAGEMENT, IamActionCodes.VIEW_TASK, "ESTIMATION_TASK_VIEW");
    public static final IamPermissionAction ESTIMATION_WBS_VIEW = IamPermissionAction.of(
            IamPermissionCodes.ESTIMATION_MANAGEMENT, IamActionCodes.VIEW_WBS, "ESTIMATION_WBS_VIEW");
    public static final IamPermissionAction ESTIMATION_PHASE_VIEW = IamPermissionAction.of(
            IamPermissionCodes.ESTIMATION_MANAGEMENT, IamActionCodes.VIEW_PHASE, "ESTIMATION_PHASE_VIEW");
    public static final IamPermissionAction ESTIMATION_SUMMARY_VIEW = IamPermissionAction.of(
            IamPermissionCodes.ESTIMATION_MANAGEMENT, IamActionCodes.VIEW_SUMMARY, "ESTIMATION_SUMMARY_VIEW");
    public static final IamPermissionAction ESTIMATION_RATE_DETAIL_VIEW = IamPermissionAction.of(
            IamPermissionCodes.ESTIMATION_MANAGEMENT, IamActionCodes.VIEW_RATE_DETAIL, "ESTIMATION_RATE_DETAIL_VIEW");

    // PROJECT FINANCE (Phase 17)
    public static final IamPermissionAction PROJECT_FINANCE_VIEW = IamPermissionAction.of(
            IamPermissionCodes.PROJECT_FINANCE_MANAGEMENT, IamActionCodes.VIEW, "PROJECT_FINANCE_VIEW");
    public static final IamPermissionAction PROJECT_FINANCE_CREATE = IamPermissionAction.of(
            IamPermissionCodes.PROJECT_FINANCE_MANAGEMENT, IamActionCodes.CREATE, "PROJECT_FINANCE_CREATE");
    public static final IamPermissionAction PROJECT_FINANCE_UPDATE = IamPermissionAction.of(
            IamPermissionCodes.PROJECT_FINANCE_MANAGEMENT, IamActionCodes.UPDATE, "PROJECT_FINANCE_UPDATE");
    public static final IamPermissionAction PROJECT_FINANCE_RECALCULATE = IamPermissionAction.of(
            IamPermissionCodes.PROJECT_FINANCE_MANAGEMENT, IamActionCodes.RECALCULATE, "PROJECT_FINANCE_RECALCULATE");
    public static final IamPermissionAction PROJECT_FINANCE_APPROVE = IamPermissionAction.of(
            IamPermissionCodes.PROJECT_FINANCE_MANAGEMENT, IamActionCodes.APPROVE, "PROJECT_FINANCE_APPROVE");
    public static final IamPermissionAction PROJECT_FINANCE_MARK_CURRENT = IamPermissionAction.of(
            IamPermissionCodes.PROJECT_FINANCE_MANAGEMENT, IamActionCodes.MARK_CURRENT, "PROJECT_FINANCE_MARK_CURRENT");
    public static final IamPermissionAction PROJECT_FINANCE_ARCHIVE = IamPermissionAction.of(
            IamPermissionCodes.PROJECT_FINANCE_MANAGEMENT, IamActionCodes.ARCHIVE, "PROJECT_FINANCE_ARCHIVE");
    public static final IamPermissionAction PROJECT_FINANCE_MANAGE = IamPermissionAction.of(
            IamPermissionCodes.PROJECT_FINANCE_MANAGEMENT, IamActionCodes.MANAGE, "PROJECT_FINANCE_MANAGE");
    public static final IamPermissionAction PROJECT_FINANCE_COST_VIEW = IamPermissionAction.of(
            IamPermissionCodes.PROJECT_FINANCE_MANAGEMENT, IamActionCodes.VIEW_COST, "PROJECT_FINANCE_COST_VIEW");
    public static final IamPermissionAction PROJECT_FINANCE_COST_CREATE = IamPermissionAction.of(
            IamPermissionCodes.PROJECT_FINANCE_MANAGEMENT, IamActionCodes.CREATE_COST, "PROJECT_FINANCE_COST_CREATE");
    public static final IamPermissionAction PROJECT_FINANCE_COST_UPDATE = IamPermissionAction.of(
            IamPermissionCodes.PROJECT_FINANCE_MANAGEMENT, IamActionCodes.UPDATE_COST, "PROJECT_FINANCE_COST_UPDATE");
    public static final IamPermissionAction PROJECT_FINANCE_COST_ARCHIVE = IamPermissionAction.of(
            IamPermissionCodes.PROJECT_FINANCE_MANAGEMENT, IamActionCodes.ARCHIVE_COST, "PROJECT_FINANCE_COST_ARCHIVE");
    public static final IamPermissionAction PROJECT_FINANCE_REVENUE_VIEW = IamPermissionAction.of(
            IamPermissionCodes.PROJECT_FINANCE_MANAGEMENT, IamActionCodes.VIEW_REVENUE, "PROJECT_FINANCE_REVENUE_VIEW");
    public static final IamPermissionAction PROJECT_FINANCE_REVENUE_UPDATE = IamPermissionAction.of(
            IamPermissionCodes.PROJECT_FINANCE_MANAGEMENT, IamActionCodes.UPDATE_REVENUE, "PROJECT_FINANCE_REVENUE_UPDATE");
    public static final IamPermissionAction PROJECT_FINANCE_MARGIN_VIEW = IamPermissionAction.of(
            IamPermissionCodes.PROJECT_FINANCE_MANAGEMENT, IamActionCodes.VIEW_MARGIN, "PROJECT_FINANCE_MARGIN_VIEW");

    // QUOTE (Phase 18) — simplified: QUOTE_VIEW covers line/summary/version view
    public static final IamPermissionAction QUOTE_VIEW = IamPermissionAction.of(
            IamPermissionCodes.QUOTE_MANAGEMENT, IamActionCodes.VIEW, "QUOTE_VIEW");
    public static final IamPermissionAction QUOTE_CREATE = IamPermissionAction.of(
            IamPermissionCodes.QUOTE_MANAGEMENT, IamActionCodes.CREATE, "QUOTE_CREATE");
    public static final IamPermissionAction QUOTE_UPDATE = IamPermissionAction.of(
            IamPermissionCodes.QUOTE_MANAGEMENT, IamActionCodes.UPDATE, "QUOTE_UPDATE");
    public static final IamPermissionAction QUOTE_ARCHIVE = IamPermissionAction.of(
            IamPermissionCodes.QUOTE_MANAGEMENT, IamActionCodes.ARCHIVE, "QUOTE_ARCHIVE");
    public static final IamPermissionAction QUOTE_MANAGE = IamPermissionAction.of(
            IamPermissionCodes.QUOTE_MANAGEMENT, IamActionCodes.MANAGE, "QUOTE_MANAGE");
    public static final IamPermissionAction QUOTE_VERSION_CREATE = IamPermissionAction.of(
            IamPermissionCodes.QUOTE_MANAGEMENT, IamActionCodes.VERSION_CREATE, "QUOTE_VERSION_CREATE");
    public static final IamPermissionAction QUOTE_VERSION_UPDATE = IamPermissionAction.of(
            IamPermissionCodes.QUOTE_MANAGEMENT, IamActionCodes.VERSION_UPDATE, "QUOTE_VERSION_UPDATE");
    public static final IamPermissionAction QUOTE_VERSION_ARCHIVE = IamPermissionAction.of(
            IamPermissionCodes.QUOTE_MANAGEMENT, IamActionCodes.VERSION_ARCHIVE, "QUOTE_VERSION_ARCHIVE");
    public static final IamPermissionAction QUOTE_VERSION_DUPLICATE = IamPermissionAction.of(
            IamPermissionCodes.QUOTE_MANAGEMENT, IamActionCodes.DUPLICATE, "QUOTE_VERSION_DUPLICATE");
    public static final IamPermissionAction QUOTE_LINE_CREATE = IamPermissionAction.of(
            IamPermissionCodes.QUOTE_MANAGEMENT, IamActionCodes.LINE_CREATE, "QUOTE_LINE_CREATE");
    public static final IamPermissionAction QUOTE_LINE_UPDATE = IamPermissionAction.of(
            IamPermissionCodes.QUOTE_MANAGEMENT, IamActionCodes.LINE_UPDATE, "QUOTE_LINE_UPDATE");
    public static final IamPermissionAction QUOTE_LINE_DELETE = IamPermissionAction.of(
            IamPermissionCodes.QUOTE_MANAGEMENT, IamActionCodes.LINE_DELETE, "QUOTE_LINE_DELETE");
    public static final IamPermissionAction QUOTE_TERM_CREATE = IamPermissionAction.of(
            IamPermissionCodes.QUOTE_MANAGEMENT, IamActionCodes.TERM_CREATE, "QUOTE_TERM_CREATE");
    public static final IamPermissionAction QUOTE_TERM_UPDATE = IamPermissionAction.of(
            IamPermissionCodes.QUOTE_MANAGEMENT, IamActionCodes.TERM_UPDATE, "QUOTE_TERM_UPDATE");
    public static final IamPermissionAction QUOTE_TERM_DELETE = IamPermissionAction.of(
            IamPermissionCodes.QUOTE_MANAGEMENT, IamActionCodes.TERM_DELETE, "QUOTE_TERM_DELETE");
    public static final IamPermissionAction QUOTE_SOLVER_USE = IamPermissionAction.of(
            IamPermissionCodes.QUOTE_MANAGEMENT, IamActionCodes.SOLVER_USE, "QUOTE_SOLVER_USE");
    public static final IamPermissionAction QUOTE_RECALCULATE = IamPermissionAction.of(
            IamPermissionCodes.QUOTE_MANAGEMENT, IamActionCodes.RECALCULATE, "QUOTE_RECALCULATE");
    public static final IamPermissionAction QUOTE_SUBMIT = IamPermissionAction.of(
            IamPermissionCodes.QUOTE_MANAGEMENT, IamActionCodes.SUBMIT, "QUOTE_SUBMIT");
    public static final IamPermissionAction QUOTE_APPROVE = IamPermissionAction.of(
            IamPermissionCodes.QUOTE_MANAGEMENT, IamActionCodes.APPROVE, "QUOTE_APPROVE");
    public static final IamPermissionAction QUOTE_REJECT = IamPermissionAction.of(
            IamPermissionCodes.QUOTE_MANAGEMENT, IamActionCodes.REJECT, "QUOTE_REJECT");
    public static final IamPermissionAction QUOTE_SEND = IamPermissionAction.of(
            IamPermissionCodes.QUOTE_MANAGEMENT, IamActionCodes.SEND, "QUOTE_SEND");
    public static final IamPermissionAction QUOTE_MARK_ACCEPTED = IamPermissionAction.of(
            IamPermissionCodes.QUOTE_MANAGEMENT, IamActionCodes.MARK_ACCEPTED, "QUOTE_MARK_ACCEPTED");
    public static final IamPermissionAction QUOTE_MARK_CURRENT = IamPermissionAction.of(
            IamPermissionCodes.QUOTE_MANAGEMENT, IamActionCodes.MARK_CURRENT, "QUOTE_MARK_CURRENT");
    public static final IamPermissionAction QUOTE_MARGIN_VIEW = IamPermissionAction.of(
            IamPermissionCodes.QUOTE_MANAGEMENT, IamActionCodes.VIEW_MARGIN, "QUOTE_MARGIN_VIEW");
    public static final IamPermissionAction QUOTE_DISCOUNT_UPDATE = IamPermissionAction.of(
            IamPermissionCodes.QUOTE_MANAGEMENT, IamActionCodes.DISCOUNT_UPDATE, "QUOTE_DISCOUNT_UPDATE");
    public static final IamPermissionAction QUOTE_DISCOUNT_APPROVE = IamPermissionAction.of(
            IamPermissionCodes.QUOTE_MANAGEMENT, IamActionCodes.DISCOUNT_APPROVE, "QUOTE_DISCOUNT_APPROVE");

    // PROJECT BASELINE / CHANGE REQUEST / CHANGE ORDER (Phase 19)
    public static final IamPermissionAction PROJECT_BASELINE_VIEW = IamPermissionAction.of(
            IamPermissionCodes.PROJECT_BASELINE_MANAGEMENT, IamActionCodes.VIEW, "PROJECT_BASELINE_VIEW");
    public static final IamPermissionAction PROJECT_BASELINE_CREATE = IamPermissionAction.of(
            IamPermissionCodes.PROJECT_BASELINE_MANAGEMENT, IamActionCodes.CREATE, "PROJECT_BASELINE_CREATE");
    public static final IamPermissionAction PROJECT_BASELINE_UPDATE = IamPermissionAction.of(
            IamPermissionCodes.PROJECT_BASELINE_MANAGEMENT, IamActionCodes.UPDATE, "PROJECT_BASELINE_UPDATE");
    public static final IamPermissionAction PROJECT_BASELINE_VALIDATE = IamPermissionAction.of(
            IamPermissionCodes.PROJECT_BASELINE_MANAGEMENT, IamActionCodes.VALIDATE, "PROJECT_BASELINE_VALIDATE");
    public static final IamPermissionAction PROJECT_BASELINE_APPROVE = IamPermissionAction.of(
            IamPermissionCodes.PROJECT_BASELINE_MANAGEMENT, IamActionCodes.APPROVE, "PROJECT_BASELINE_APPROVE");
    public static final IamPermissionAction PROJECT_BASELINE_MARK_CURRENT = IamPermissionAction.of(
            IamPermissionCodes.PROJECT_BASELINE_MANAGEMENT, IamActionCodes.MARK_CURRENT, "PROJECT_BASELINE_MARK_CURRENT");
    public static final IamPermissionAction PROJECT_BASELINE_ARCHIVE = IamPermissionAction.of(
            IamPermissionCodes.PROJECT_BASELINE_MANAGEMENT, IamActionCodes.ARCHIVE, "PROJECT_BASELINE_ARCHIVE");
    public static final IamPermissionAction PROJECT_BASELINE_COMPARE = IamPermissionAction.of(
            IamPermissionCodes.PROJECT_BASELINE_MANAGEMENT, IamActionCodes.COMPARE, "PROJECT_BASELINE_COMPARE");

    public static final IamPermissionAction CHANGE_REQUEST_VIEW = IamPermissionAction.of(
            IamPermissionCodes.CHANGE_REQUEST_MANAGEMENT, IamActionCodes.VIEW, "CHANGE_REQUEST_VIEW");
    public static final IamPermissionAction CHANGE_REQUEST_CREATE = IamPermissionAction.of(
            IamPermissionCodes.CHANGE_REQUEST_MANAGEMENT, IamActionCodes.CREATE, "CHANGE_REQUEST_CREATE");
    public static final IamPermissionAction CHANGE_REQUEST_UPDATE = IamPermissionAction.of(
            IamPermissionCodes.CHANGE_REQUEST_MANAGEMENT, IamActionCodes.UPDATE, "CHANGE_REQUEST_UPDATE");
    public static final IamPermissionAction CHANGE_REQUEST_SUBMIT = IamPermissionAction.of(
            IamPermissionCodes.CHANGE_REQUEST_MANAGEMENT, IamActionCodes.SUBMIT, "CHANGE_REQUEST_SUBMIT");
    public static final IamPermissionAction CHANGE_REQUEST_APPROVE = IamPermissionAction.of(
            IamPermissionCodes.CHANGE_REQUEST_MANAGEMENT, IamActionCodes.APPROVE, "CHANGE_REQUEST_APPROVE");
    public static final IamPermissionAction CHANGE_REQUEST_REJECT = IamPermissionAction.of(
            IamPermissionCodes.CHANGE_REQUEST_MANAGEMENT, IamActionCodes.REJECT, "CHANGE_REQUEST_REJECT");
    public static final IamPermissionAction CHANGE_REQUEST_CANCEL = IamPermissionAction.of(
            IamPermissionCodes.CHANGE_REQUEST_MANAGEMENT, IamActionCodes.CANCEL, "CHANGE_REQUEST_CANCEL");
    public static final IamPermissionAction CHANGE_REQUEST_APPLY = IamPermissionAction.of(
            IamPermissionCodes.CHANGE_REQUEST_MANAGEMENT, IamActionCodes.APPLY, "CHANGE_REQUEST_APPLY");
    public static final IamPermissionAction CHANGE_REQUEST_ARCHIVE = IamPermissionAction.of(
            IamPermissionCodes.CHANGE_REQUEST_MANAGEMENT, IamActionCodes.ARCHIVE, "CHANGE_REQUEST_ARCHIVE");
    public static final IamPermissionAction CHANGE_REQUEST_ITEM_VIEW = IamPermissionAction.of(
            IamPermissionCodes.CHANGE_REQUEST_MANAGEMENT, IamActionCodes.ITEM_VIEW, "CHANGE_REQUEST_ITEM_VIEW");
    public static final IamPermissionAction CHANGE_REQUEST_ITEM_CREATE = IamPermissionAction.of(
            IamPermissionCodes.CHANGE_REQUEST_MANAGEMENT, IamActionCodes.ITEM_CREATE, "CHANGE_REQUEST_ITEM_CREATE");
    public static final IamPermissionAction CHANGE_REQUEST_ITEM_UPDATE = IamPermissionAction.of(
            IamPermissionCodes.CHANGE_REQUEST_MANAGEMENT, IamActionCodes.ITEM_UPDATE, "CHANGE_REQUEST_ITEM_UPDATE");
    public static final IamPermissionAction CHANGE_REQUEST_ITEM_DELETE = IamPermissionAction.of(
            IamPermissionCodes.CHANGE_REQUEST_MANAGEMENT, IamActionCodes.ITEM_DELETE, "CHANGE_REQUEST_ITEM_DELETE");
    public static final IamPermissionAction CHANGE_IMPACT_VIEW = IamPermissionAction.of(
            IamPermissionCodes.CHANGE_REQUEST_MANAGEMENT, IamActionCodes.IMPACT_VIEW, "CHANGE_IMPACT_VIEW");
    public static final IamPermissionAction CHANGE_IMPACT_UPDATE = IamPermissionAction.of(
            IamPermissionCodes.CHANGE_REQUEST_MANAGEMENT, IamActionCodes.IMPACT_UPDATE, "CHANGE_IMPACT_UPDATE");
    public static final IamPermissionAction CHANGE_IMPACT_CALCULATE = IamPermissionAction.of(
            IamPermissionCodes.CHANGE_REQUEST_MANAGEMENT, IamActionCodes.IMPACT_CALCULATE, "CHANGE_IMPACT_CALCULATE");

    public static final IamPermissionAction CHANGE_ORDER_VIEW = IamPermissionAction.of(
            IamPermissionCodes.CHANGE_ORDER_MANAGEMENT, IamActionCodes.VIEW, "CHANGE_ORDER_VIEW");
    public static final IamPermissionAction CHANGE_ORDER_CREATE = IamPermissionAction.of(
            IamPermissionCodes.CHANGE_ORDER_MANAGEMENT, IamActionCodes.CREATE, "CHANGE_ORDER_CREATE");
    public static final IamPermissionAction CHANGE_ORDER_UPDATE = IamPermissionAction.of(
            IamPermissionCodes.CHANGE_ORDER_MANAGEMENT, IamActionCodes.UPDATE, "CHANGE_ORDER_UPDATE");
    public static final IamPermissionAction CHANGE_ORDER_APPROVE = IamPermissionAction.of(
            IamPermissionCodes.CHANGE_ORDER_MANAGEMENT, IamActionCodes.APPROVE, "CHANGE_ORDER_APPROVE");
    public static final IamPermissionAction CHANGE_ORDER_REJECT = IamPermissionAction.of(
            IamPermissionCodes.CHANGE_ORDER_MANAGEMENT, IamActionCodes.REJECT, "CHANGE_ORDER_REJECT");
    public static final IamPermissionAction CHANGE_ORDER_ARCHIVE = IamPermissionAction.of(
            IamPermissionCodes.CHANGE_ORDER_MANAGEMENT, IamActionCodes.ARCHIVE, "CHANGE_ORDER_ARCHIVE");

    // PROJECT NOTIFICATION (Phase 20)
    public static final IamPermissionAction PROJECT_NOTIFICATION_VIEW = IamPermissionAction.of(
            IamPermissionCodes.PROJECT_NOTIFICATION_MANAGEMENT, IamActionCodes.VIEW, "PROJECT_NOTIFICATION_VIEW");
    public static final IamPermissionAction PROJECT_NOTIFICATION_SUBSCRIBE_SELF = IamPermissionAction.of(
            IamPermissionCodes.PROJECT_NOTIFICATION_MANAGEMENT, IamActionCodes.SUBSCRIBE_SELF, "PROJECT_NOTIFICATION_SUBSCRIBE_SELF");
    public static final IamPermissionAction PROJECT_NOTIFICATION_MANAGE_SUBSCRIBERS = IamPermissionAction.of(
            IamPermissionCodes.PROJECT_NOTIFICATION_MANAGEMENT, IamActionCodes.MANAGE_SUBSCRIBERS, "PROJECT_NOTIFICATION_MANAGE_SUBSCRIBERS");
    public static final IamPermissionAction PROJECT_NOTIFICATION_PREFERENCE_VIEW = IamPermissionAction.of(
            IamPermissionCodes.PROJECT_NOTIFICATION_MANAGEMENT, IamActionCodes.PREFERENCE_VIEW, "PROJECT_NOTIFICATION_PREFERENCE_VIEW");
    public static final IamPermissionAction PROJECT_NOTIFICATION_PREFERENCE_UPDATE = IamPermissionAction.of(
            IamPermissionCodes.PROJECT_NOTIFICATION_MANAGEMENT, IamActionCodes.PREFERENCE_UPDATE, "PROJECT_NOTIFICATION_PREFERENCE_UPDATE");
    public static final IamPermissionAction TASK_NOTIFICATION_VIEW = IamPermissionAction.of(
            IamPermissionCodes.PROJECT_NOTIFICATION_MANAGEMENT, IamActionCodes.TASK_NOTIFICATION_VIEW, "TASK_NOTIFICATION_VIEW");
    public static final IamPermissionAction TASK_NOTIFICATION_SUBSCRIBE_SELF = IamPermissionAction.of(
            IamPermissionCodes.PROJECT_NOTIFICATION_MANAGEMENT, IamActionCodes.TASK_SUBSCRIBE_SELF, "TASK_NOTIFICATION_SUBSCRIBE_SELF");
    public static final IamPermissionAction TASK_NOTIFICATION_MANAGE_SUBSCRIBERS = IamPermissionAction.of(
            IamPermissionCodes.PROJECT_NOTIFICATION_MANAGEMENT, IamActionCodes.TASK_MANAGE_SUBSCRIBERS, "TASK_NOTIFICATION_MANAGE_SUBSCRIBERS");
    public static final IamPermissionAction TASK_NOTIFICATION_PREFERENCE_VIEW = IamPermissionAction.of(
            IamPermissionCodes.PROJECT_NOTIFICATION_MANAGEMENT, IamActionCodes.TASK_PREFERENCE_VIEW, "TASK_NOTIFICATION_PREFERENCE_VIEW");
    public static final IamPermissionAction TASK_NOTIFICATION_PREFERENCE_UPDATE = IamPermissionAction.of(
            IamPermissionCodes.PROJECT_NOTIFICATION_MANAGEMENT, IamActionCodes.TASK_PREFERENCE_UPDATE, "TASK_NOTIFICATION_PREFERENCE_UPDATE");
    public static final IamPermissionAction PROJECT_NOTIFICATION_RULE_VIEW = IamPermissionAction.of(
            IamPermissionCodes.PROJECT_NOTIFICATION_MANAGEMENT, IamActionCodes.RULE_VIEW, "PROJECT_NOTIFICATION_RULE_VIEW");
    public static final IamPermissionAction PROJECT_NOTIFICATION_RULE_MANAGE = IamPermissionAction.of(
            IamPermissionCodes.PROJECT_NOTIFICATION_MANAGEMENT, IamActionCodes.RULE_MANAGE, "PROJECT_NOTIFICATION_RULE_MANAGE");
    public static final IamPermissionAction PROJECT_REMINDER_RUN = IamPermissionAction.of(
            IamPermissionCodes.PROJECT_NOTIFICATION_MANAGEMENT, IamActionCodes.REMINDER_RUN, "PROJECT_REMINDER_RUN");
    public static final IamPermissionAction PROJECT_NOTIFICATION_DELIVERY_VIEW = IamPermissionAction.of(
            IamPermissionCodes.PROJECT_NOTIFICATION_MANAGEMENT, IamActionCodes.DELIVERY_VIEW, "PROJECT_NOTIFICATION_DELIVERY_VIEW");


    // AI PROJECT PLANNING (Phase 21)
    public static final IamPermissionAction AI_PROJECT_PLANNING_VIEW = IamPermissionAction.of(
            IamPermissionCodes.AI_PROJECT_PLANNING_MANAGEMENT, IamActionCodes.VIEW, "AI_PROJECT_PLANNING_VIEW");
    public static final IamPermissionAction AI_PROJECT_PLANNING_RUN = IamPermissionAction.of(
            IamPermissionCodes.AI_PROJECT_PLANNING_MANAGEMENT, IamActionCodes.RUN, "AI_PROJECT_PLANNING_RUN");
    public static final IamPermissionAction AI_PROJECT_PLANNING_REVIEW = IamPermissionAction.of(
            IamPermissionCodes.AI_PROJECT_PLANNING_MANAGEMENT, IamActionCodes.REVIEW, "AI_PROJECT_PLANNING_REVIEW");
    public static final IamPermissionAction AI_PROJECT_PLANNING_ACCEPT = IamPermissionAction.of(
            IamPermissionCodes.AI_PROJECT_PLANNING_MANAGEMENT, IamActionCodes.ACCEPT, "AI_PROJECT_PLANNING_ACCEPT");
    public static final IamPermissionAction AI_PROJECT_PLANNING_REJECT = IamPermissionAction.of(
            IamPermissionCodes.AI_PROJECT_PLANNING_MANAGEMENT, IamActionCodes.REJECT, "AI_PROJECT_PLANNING_REJECT");
    public static final IamPermissionAction AI_PROJECT_PLANNING_APPLY = IamPermissionAction.of(
            IamPermissionCodes.AI_PROJECT_PLANNING_MANAGEMENT, IamActionCodes.APPLY, "AI_PROJECT_PLANNING_APPLY");
    public static final IamPermissionAction AI_PROJECT_PLANNING_ARCHIVE = IamPermissionAction.of(
            IamPermissionCodes.AI_PROJECT_PLANNING_MANAGEMENT, IamActionCodes.ARCHIVE, "AI_PROJECT_PLANNING_ARCHIVE");
    public static final IamPermissionAction AI_PROJECT_PLAN_DRAFT = IamPermissionAction.of(
            IamPermissionCodes.AI_PROJECT_PLANNING_MANAGEMENT, IamActionCodes.PLAN_DRAFT, "AI_PROJECT_PLAN_DRAFT");
    public static final IamPermissionAction AI_TASK_ESTIMATE_SUGGEST = IamPermissionAction.of(
            IamPermissionCodes.AI_PROJECT_PLANNING_MANAGEMENT, IamActionCodes.TASK_ESTIMATE_SUGGEST, "AI_TASK_ESTIMATE_SUGGEST");
    public static final IamPermissionAction AI_COST_ROLE_SUGGEST = IamPermissionAction.of(
            IamPermissionCodes.AI_PROJECT_PLANNING_MANAGEMENT, IamActionCodes.COST_ROLE_SUGGEST, "AI_COST_ROLE_SUGGEST");
    public static final IamPermissionAction AI_SCHEDULE_RISK_EXPLAIN = IamPermissionAction.of(
            IamPermissionCodes.AI_PROJECT_PLANNING_MANAGEMENT, IamActionCodes.SCHEDULE_RISK_EXPLAIN, "AI_SCHEDULE_RISK_EXPLAIN");
    public static final IamPermissionAction AI_FINANCE_INSIGHT = IamPermissionAction.of(
            IamPermissionCodes.AI_PROJECT_PLANNING_MANAGEMENT, IamActionCodes.FINANCE_INSIGHT, "AI_FINANCE_INSIGHT");
    public static final IamPermissionAction AI_QUOTE_DRAFT = IamPermissionAction.of(
            IamPermissionCodes.AI_PROJECT_PLANNING_MANAGEMENT, IamActionCodes.QUOTE_DRAFT, "AI_QUOTE_DRAFT");
    public static final IamPermissionAction AI_CHANGE_REQUEST_DRAFT = IamPermissionAction.of(
            IamPermissionCodes.AI_PROJECT_PLANNING_MANAGEMENT, IamActionCodes.CHANGE_REQUEST_DRAFT, "AI_CHANGE_REQUEST_DRAFT");
    public static final IamPermissionAction AI_SUGGESTION_APPLY_WBS = IamPermissionAction.of(
            IamPermissionCodes.AI_PROJECT_PLANNING_MANAGEMENT, IamActionCodes.APPLY_WBS, "AI_SUGGESTION_APPLY_WBS");
    public static final IamPermissionAction AI_SUGGESTION_APPLY_TASK = IamPermissionAction.of(
            IamPermissionCodes.AI_PROJECT_PLANNING_MANAGEMENT, IamActionCodes.APPLY_TASK, "AI_SUGGESTION_APPLY_TASK");
    public static final IamPermissionAction AI_SUGGESTION_APPLY_ESTIMATE = IamPermissionAction.of(
            IamPermissionCodes.AI_PROJECT_PLANNING_MANAGEMENT, IamActionCodes.APPLY_ESTIMATE, "AI_SUGGESTION_APPLY_ESTIMATE");
    public static final IamPermissionAction AI_SUGGESTION_APPLY_DEPENDENCY = IamPermissionAction.of(
            IamPermissionCodes.AI_PROJECT_PLANNING_MANAGEMENT, IamActionCodes.APPLY_DEPENDENCY, "AI_SUGGESTION_APPLY_DEPENDENCY");
    public static final IamPermissionAction AI_SUGGESTION_APPLY_QUOTE_TERM = IamPermissionAction.of(
            IamPermissionCodes.AI_PROJECT_PLANNING_MANAGEMENT, IamActionCodes.APPLY_QUOTE_TERM, "AI_SUGGESTION_APPLY_QUOTE_TERM");
    public static final IamPermissionAction AI_SUGGESTION_APPLY_CHANGE_REQUEST = IamPermissionAction.of(
            IamPermissionCodes.AI_PROJECT_PLANNING_MANAGEMENT, IamActionCodes.APPLY_CHANGE_REQUEST, "AI_SUGGESTION_APPLY_CHANGE_REQUEST");

    // REPORTING (Phase 22)
    public static final IamPermissionAction REPORTING_DASHBOARD_VIEW = IamPermissionAction.of(
            IamPermissionCodes.REPORTING_MANAGEMENT, IamActionCodes.DASHBOARD_VIEW, "REPORTING_DASHBOARD_VIEW");
    public static final IamPermissionAction REPORTING_HEALTH_VIEW = IamPermissionAction.of(
            IamPermissionCodes.REPORTING_MANAGEMENT, IamActionCodes.HEALTH_VIEW, "REPORTING_HEALTH_VIEW");
    public static final IamPermissionAction REPORTING_KPI_VIEW = IamPermissionAction.of(
            IamPermissionCodes.REPORTING_MANAGEMENT, IamActionCodes.KPI_VIEW, "REPORTING_KPI_VIEW");
    public static final IamPermissionAction REPORTING_REPORT_VIEW = IamPermissionAction.of(
            IamPermissionCodes.REPORTING_MANAGEMENT, IamActionCodes.REPORT_VIEW, "REPORTING_REPORT_VIEW");
    public static final IamPermissionAction REPORTING_REPORT_RUN = IamPermissionAction.of(
            IamPermissionCodes.REPORTING_MANAGEMENT, IamActionCodes.REPORT_RUN, "REPORTING_REPORT_RUN");
    public static final IamPermissionAction REPORTING_REPORT_EXPORT = IamPermissionAction.of(
            IamPermissionCodes.REPORTING_MANAGEMENT, IamActionCodes.REPORT_EXPORT, "REPORTING_REPORT_EXPORT");
    public static final IamPermissionAction REPORTING_DEFINITION_VIEW = IamPermissionAction.of(
            IamPermissionCodes.REPORTING_MANAGEMENT, IamActionCodes.VIEW, "REPORTING_DEFINITION_VIEW");
    public static final IamPermissionAction REPORTING_DEFINITION_MANAGE = IamPermissionAction.of(
            IamPermissionCodes.REPORTING_MANAGEMENT, IamActionCodes.MANAGE, "REPORTING_DEFINITION_MANAGE");

    // SCOPE / DELIVERABLE (Phase 24)
    public static final IamPermissionAction SCOPE_VIEW = IamPermissionAction.of(
            IamPermissionCodes.SCOPE_MANAGEMENT, IamActionCodes.VIEW, "SCOPE_VIEW");
    public static final IamPermissionAction SCOPE_CREATE = IamPermissionAction.of(
            IamPermissionCodes.SCOPE_MANAGEMENT, IamActionCodes.CREATE, "SCOPE_CREATE");
    public static final IamPermissionAction SCOPE_UPDATE = IamPermissionAction.of(
            IamPermissionCodes.SCOPE_MANAGEMENT, IamActionCodes.UPDATE, "SCOPE_UPDATE");
    public static final IamPermissionAction SCOPE_APPROVE = IamPermissionAction.of(
            IamPermissionCodes.SCOPE_MANAGEMENT, IamActionCodes.APPROVE, "SCOPE_APPROVE");
    public static final IamPermissionAction SCOPE_ARCHIVE = IamPermissionAction.of(
            IamPermissionCodes.SCOPE_MANAGEMENT, IamActionCodes.ARCHIVE, "SCOPE_ARCHIVE");
    public static final IamPermissionAction DELIVERABLE_VIEW = IamPermissionAction.of(
            IamPermissionCodes.DELIVERABLE_MANAGEMENT, IamActionCodes.VIEW, "DELIVERABLE_VIEW");
    public static final IamPermissionAction DELIVERABLE_CREATE = IamPermissionAction.of(
            IamPermissionCodes.DELIVERABLE_MANAGEMENT, IamActionCodes.CREATE, "DELIVERABLE_CREATE");
    public static final IamPermissionAction DELIVERABLE_UPDATE = IamPermissionAction.of(
            IamPermissionCodes.DELIVERABLE_MANAGEMENT, IamActionCodes.UPDATE, "DELIVERABLE_UPDATE");
    public static final IamPermissionAction DELIVERABLE_SUBMIT_REVIEW = IamPermissionAction.of(
            IamPermissionCodes.DELIVERABLE_MANAGEMENT, IamActionCodes.SUBMIT_REVIEW, "DELIVERABLE_SUBMIT_REVIEW");
    public static final IamPermissionAction DELIVERABLE_ACCEPT = IamPermissionAction.of(
            IamPermissionCodes.DELIVERABLE_MANAGEMENT, IamActionCodes.ACCEPT, "DELIVERABLE_ACCEPT");
    public static final IamPermissionAction DELIVERABLE_REJECT = IamPermissionAction.of(
            IamPermissionCodes.DELIVERABLE_MANAGEMENT, IamActionCodes.REJECT, "DELIVERABLE_REJECT");
    public static final IamPermissionAction DELIVERABLE_REOPEN = IamPermissionAction.of(
            IamPermissionCodes.DELIVERABLE_MANAGEMENT, IamActionCodes.REOPEN, "DELIVERABLE_REOPEN");
    public static final IamPermissionAction DELIVERABLE_ARCHIVE = IamPermissionAction.of(
            IamPermissionCodes.DELIVERABLE_MANAGEMENT, IamActionCodes.ARCHIVE, "DELIVERABLE_ARCHIVE");
    public static final IamPermissionAction ACCEPTANCE_CRITERIA_WAIVE = IamPermissionAction.of(
            IamPermissionCodes.DELIVERABLE_MANAGEMENT, IamActionCodes.WAIVE, "ACCEPTANCE_CRITERIA_WAIVE");

    // RAID / DECISION (Phase 25)
    public static final IamPermissionAction RAID_VIEW = IamPermissionAction.of(
            IamPermissionCodes.RAID_MANAGEMENT, IamActionCodes.VIEW, "RAID_VIEW");
    public static final IamPermissionAction RAID_CREATE = IamPermissionAction.of(
            IamPermissionCodes.RAID_MANAGEMENT, IamActionCodes.CREATE, "RAID_CREATE");
    public static final IamPermissionAction RAID_UPDATE = IamPermissionAction.of(
            IamPermissionCodes.RAID_MANAGEMENT, IamActionCodes.UPDATE, "RAID_UPDATE");
    public static final IamPermissionAction RAID_ESCALATE = IamPermissionAction.of(
            IamPermissionCodes.RAID_MANAGEMENT, IamActionCodes.ESCALATE, "RAID_ESCALATE");
    public static final IamPermissionAction RAID_CONVERT = IamPermissionAction.of(
            IamPermissionCodes.RAID_MANAGEMENT, IamActionCodes.CONVERT, "RAID_CONVERT");
    public static final IamPermissionAction RAID_ARCHIVE = IamPermissionAction.of(
            IamPermissionCodes.RAID_MANAGEMENT, IamActionCodes.ARCHIVE, "RAID_ARCHIVE");
    public static final IamPermissionAction DECISION_VIEW = IamPermissionAction.of(
            IamPermissionCodes.DECISION_MANAGEMENT, IamActionCodes.VIEW, "DECISION_VIEW");
    public static final IamPermissionAction DECISION_CREATE = IamPermissionAction.of(
            IamPermissionCodes.DECISION_MANAGEMENT, IamActionCodes.CREATE, "DECISION_CREATE");
    public static final IamPermissionAction DECISION_UPDATE = IamPermissionAction.of(
            IamPermissionCodes.DECISION_MANAGEMENT, IamActionCodes.UPDATE, "DECISION_UPDATE");
    public static final IamPermissionAction DECISION_DECIDE = IamPermissionAction.of(
            IamPermissionCodes.DECISION_MANAGEMENT, IamActionCodes.DECIDE, "DECISION_DECIDE");
    public static final IamPermissionAction DECISION_ARCHIVE = IamPermissionAction.of(
            IamPermissionCodes.DECISION_MANAGEMENT, IamActionCodes.ARCHIVE, "DECISION_ARCHIVE");

    // QUALITY / TEST / DEFECT / RELEASE / DEPLOYMENT (Phase 26)
    public static final IamPermissionAction QUALITY_VIEW = IamPermissionAction.of(
            IamPermissionCodes.QUALITY_MANAGEMENT, IamActionCodes.VIEW, "QUALITY_VIEW");
    public static final IamPermissionAction QUALITY_CREATE = IamPermissionAction.of(
            IamPermissionCodes.QUALITY_MANAGEMENT, IamActionCodes.CREATE, "QUALITY_CREATE");
    public static final IamPermissionAction QUALITY_UPDATE = IamPermissionAction.of(
            IamPermissionCodes.QUALITY_MANAGEMENT, IamActionCodes.UPDATE, "QUALITY_UPDATE");
    public static final IamPermissionAction QUALITY_APPROVE = IamPermissionAction.of(
            IamPermissionCodes.QUALITY_MANAGEMENT, IamActionCodes.APPROVE, "QUALITY_APPROVE");
    public static final IamPermissionAction TEST_VIEW = IamPermissionAction.of(
            IamPermissionCodes.TEST_MANAGEMENT, IamActionCodes.VIEW, "TEST_VIEW");
    public static final IamPermissionAction TEST_CREATE = IamPermissionAction.of(
            IamPermissionCodes.TEST_MANAGEMENT, IamActionCodes.CREATE, "TEST_CREATE");
    public static final IamPermissionAction TEST_UPDATE = IamPermissionAction.of(
            IamPermissionCodes.TEST_MANAGEMENT, IamActionCodes.UPDATE, "TEST_UPDATE");
    public static final IamPermissionAction TEST_EXECUTE = IamPermissionAction.of(
            IamPermissionCodes.TEST_MANAGEMENT, IamActionCodes.EXECUTE, "TEST_EXECUTE");
    public static final IamPermissionAction DEFECT_VIEW = IamPermissionAction.of(
            IamPermissionCodes.DEFECT_MANAGEMENT, IamActionCodes.VIEW, "DEFECT_VIEW");
    public static final IamPermissionAction DEFECT_CREATE = IamPermissionAction.of(
            IamPermissionCodes.DEFECT_MANAGEMENT, IamActionCodes.CREATE, "DEFECT_CREATE");
    public static final IamPermissionAction DEFECT_UPDATE = IamPermissionAction.of(
            IamPermissionCodes.DEFECT_MANAGEMENT, IamActionCodes.UPDATE, "DEFECT_UPDATE");
    public static final IamPermissionAction DEFECT_RESOLVE = IamPermissionAction.of(
            IamPermissionCodes.DEFECT_MANAGEMENT, IamActionCodes.RESOLVE, "DEFECT_RESOLVE");
    public static final IamPermissionAction RELEASE_VIEW = IamPermissionAction.of(
            IamPermissionCodes.RELEASE_MANAGEMENT, IamActionCodes.VIEW, "RELEASE_VIEW");
    public static final IamPermissionAction RELEASE_CREATE = IamPermissionAction.of(
            IamPermissionCodes.RELEASE_MANAGEMENT, IamActionCodes.CREATE, "RELEASE_CREATE");
    public static final IamPermissionAction RELEASE_UPDATE = IamPermissionAction.of(
            IamPermissionCodes.RELEASE_MANAGEMENT, IamActionCodes.UPDATE, "RELEASE_UPDATE");
    public static final IamPermissionAction RELEASE_APPROVE = IamPermissionAction.of(
            IamPermissionCodes.RELEASE_MANAGEMENT, IamActionCodes.APPROVE, "RELEASE_APPROVE");
    public static final IamPermissionAction DEPLOYMENT_VIEW = IamPermissionAction.of(
            IamPermissionCodes.DEPLOYMENT_MANAGEMENT, IamActionCodes.VIEW, "DEPLOYMENT_VIEW");
    public static final IamPermissionAction DEPLOYMENT_MANAGE = IamPermissionAction.of(
            IamPermissionCodes.DEPLOYMENT_MANAGEMENT, IamActionCodes.MANAGE, "DEPLOYMENT_MANAGE");

    // DOCUMENT HUB (Phase 27)
    public static final IamPermissionAction DOCUMENT_HUB_VIEW = IamPermissionAction.of(
            IamPermissionCodes.DOCUMENT_HUB_MANAGEMENT, IamActionCodes.VIEW, "DOCUMENT_HUB_VIEW");
    public static final IamPermissionAction DOCUMENT_HUB_CREATE = IamPermissionAction.of(
            IamPermissionCodes.DOCUMENT_HUB_MANAGEMENT, IamActionCodes.CREATE, "DOCUMENT_HUB_CREATE");
    public static final IamPermissionAction DOCUMENT_HUB_UPDATE = IamPermissionAction.of(
            IamPermissionCodes.DOCUMENT_HUB_MANAGEMENT, IamActionCodes.UPDATE, "DOCUMENT_HUB_UPDATE");
    public static final IamPermissionAction DOCUMENT_HUB_APPROVE = IamPermissionAction.of(
            IamPermissionCodes.DOCUMENT_HUB_MANAGEMENT, IamActionCodes.APPROVE, "DOCUMENT_HUB_APPROVE");

    // REQUIREMENT / TRACEABILITY (Phase 28)
    public static final IamPermissionAction REQUIREMENT_VIEW = IamPermissionAction.of(
            IamPermissionCodes.REQUIREMENT_MANAGEMENT, IamActionCodes.VIEW, "REQUIREMENT_VIEW");
    public static final IamPermissionAction REQUIREMENT_CREATE = IamPermissionAction.of(
            IamPermissionCodes.REQUIREMENT_MANAGEMENT, IamActionCodes.CREATE, "REQUIREMENT_CREATE");
    public static final IamPermissionAction REQUIREMENT_UPDATE = IamPermissionAction.of(
            IamPermissionCodes.REQUIREMENT_MANAGEMENT, IamActionCodes.UPDATE, "REQUIREMENT_UPDATE");
    public static final IamPermissionAction REQUIREMENT_APPROVE = IamPermissionAction.of(
            IamPermissionCodes.REQUIREMENT_MANAGEMENT, IamActionCodes.APPROVE, "REQUIREMENT_APPROVE");

    // EXTERNAL PARTY (Phase 29)
    public static final IamPermissionAction EXTERNAL_PARTY_VIEW = IamPermissionAction.of(
            IamPermissionCodes.EXTERNAL_PARTY_MANAGEMENT, IamActionCodes.VIEW, "EXTERNAL_PARTY_VIEW");
    public static final IamPermissionAction EXTERNAL_PARTY_CREATE = IamPermissionAction.of(
            IamPermissionCodes.EXTERNAL_PARTY_MANAGEMENT, IamActionCodes.CREATE, "EXTERNAL_PARTY_CREATE");
    public static final IamPermissionAction EXTERNAL_PARTY_UPDATE = IamPermissionAction.of(
            IamPermissionCodes.EXTERNAL_PARTY_MANAGEMENT, IamActionCodes.UPDATE, "EXTERNAL_PARTY_UPDATE");

    // CLIENT PORTAL (Phase 30)
    public static final IamPermissionAction CLIENT_PORTAL_VIEW = IamPermissionAction.of(
            IamPermissionCodes.CLIENT_PORTAL_MANAGEMENT, IamActionCodes.VIEW, "CLIENT_PORTAL_VIEW");
    public static final IamPermissionAction CLIENT_PORTAL_MANAGE = IamPermissionAction.of(
            IamPermissionCodes.CLIENT_PORTAL_MANAGEMENT, IamActionCodes.MANAGE, "CLIENT_PORTAL_MANAGE");

    public static final IamPermissionAction PROJECT_MEETING_VIEW = IamPermissionAction.of(
            IamPermissionCodes.COLLABORATION_MANAGEMENT, IamActionCodes.VIEW, "PROJECT_MEETING_VIEW");
    public static final IamPermissionAction PROJECT_MEETING_CREATE = IamPermissionAction.of(
            IamPermissionCodes.COLLABORATION_MANAGEMENT, IamActionCodes.CREATE, "PROJECT_MEETING_CREATE");
    public static final IamPermissionAction PROJECT_MEETING_UPDATE = IamPermissionAction.of(
            IamPermissionCodes.COLLABORATION_MANAGEMENT, IamActionCodes.UPDATE, "PROJECT_MEETING_UPDATE");
    public static final IamPermissionAction PROJECT_MEETING_CANCEL = IamPermissionAction.of(
            IamPermissionCodes.COLLABORATION_MANAGEMENT, IamActionCodes.CANCEL, "PROJECT_MEETING_CANCEL");
    public static final IamPermissionAction PROJECT_MEETING_ARCHIVE = IamPermissionAction.of(
            IamPermissionCodes.COLLABORATION_MANAGEMENT, IamActionCodes.ARCHIVE, "PROJECT_MEETING_ARCHIVE");
    public static final IamPermissionAction PROJECT_MEETING_SERIES_MANAGE = IamPermissionAction.of(
            IamPermissionCodes.COLLABORATION_MANAGEMENT, IamActionCodes.MANAGE, "PROJECT_MEETING_SERIES_MANAGE");
    public static final IamPermissionAction PROJECT_MEETING_PARTICIPANT_MANAGE = IamPermissionAction.of(
            IamPermissionCodes.COLLABORATION_MANAGEMENT, IamActionCodes.MANAGE_MEMBER, "PROJECT_MEETING_PARTICIPANT_MANAGE");
    public static final IamPermissionAction PROJECT_MEETING_AGENDA_MANAGE = IamPermissionAction.of(
            IamPermissionCodes.COLLABORATION_MANAGEMENT, IamActionCodes.MANAGE, "PROJECT_MEETING_AGENDA_MANAGE");
    public static final IamPermissionAction PROJECT_MEETING_MINUTES_VIEW = IamPermissionAction.of(
            IamPermissionCodes.COLLABORATION_MANAGEMENT, IamActionCodes.VIEW, "PROJECT_MEETING_MINUTES_VIEW");
    public static final IamPermissionAction PROJECT_MEETING_MINUTES_UPDATE = IamPermissionAction.of(
            IamPermissionCodes.COLLABORATION_MANAGEMENT, IamActionCodes.UPDATE, "PROJECT_MEETING_MINUTES_UPDATE");
    public static final IamPermissionAction PROJECT_MEETING_MINUTES_APPROVE = IamPermissionAction.of(
            IamPermissionCodes.COLLABORATION_MANAGEMENT, IamActionCodes.APPROVE, "PROJECT_MEETING_MINUTES_APPROVE");
    public static final IamPermissionAction PROJECT_MEETING_MINUTES_GENERATE_DOCUMENT = IamPermissionAction.of(
            IamPermissionCodes.COLLABORATION_MANAGEMENT, IamActionCodes.EXPORT, "PROJECT_MEETING_MINUTES_GENERATE_DOCUMENT");
    public static final IamPermissionAction PROJECT_MEETING_NOTE_UPDATE = IamPermissionAction.of(
            IamPermissionCodes.COLLABORATION_MANAGEMENT, IamActionCodes.UPDATE, "PROJECT_MEETING_NOTE_UPDATE");
    public static final IamPermissionAction PROJECT_MEETING_ACTION_UPDATE = IamPermissionAction.of(
            IamPermissionCodes.COLLABORATION_MANAGEMENT, IamActionCodes.UPDATE, "PROJECT_MEETING_ACTION_UPDATE");
    public static final IamPermissionAction PROJECT_MEETING_ACTION_COMPLETE = IamPermissionAction.of(
            IamPermissionCodes.COLLABORATION_MANAGEMENT, IamActionCodes.COMPLETE, "PROJECT_MEETING_ACTION_COMPLETE");
    public static final IamPermissionAction PROJECT_MEETING_ACTION_CREATE_TASK = IamPermissionAction.of(
            IamPermissionCodes.COLLABORATION_MANAGEMENT, IamActionCodes.APPLY_TASK, "PROJECT_MEETING_ACTION_CREATE_TASK");
    public static final IamPermissionAction PROJECT_MEETING_LINK_MANAGE = IamPermissionAction.of(
            IamPermissionCodes.COLLABORATION_MANAGEMENT, IamActionCodes.MANAGE, "PROJECT_MEETING_LINK_MANAGE");
    public static final IamPermissionAction PROJECT_COMMENT_THREAD_VIEW = IamPermissionAction.of(
            IamPermissionCodes.COMMENT_MANAGEMENT, IamActionCodes.VIEW, "PROJECT_COMMENT_THREAD_VIEW");
    public static final IamPermissionAction PROJECT_COMMENT_THREAD_CREATE = IamPermissionAction.of(
            IamPermissionCodes.COMMENT_MANAGEMENT, IamActionCodes.CREATE, "PROJECT_COMMENT_THREAD_CREATE");
    public static final IamPermissionAction PROJECT_COMMENT_CREATE = IamPermissionAction.of(
            IamPermissionCodes.COMMENT_MANAGEMENT, IamActionCodes.CREATE, "PROJECT_COMMENT_CREATE");
    public static final IamPermissionAction PROJECT_COMMENT_UPDATE = IamPermissionAction.of(
            IamPermissionCodes.COMMENT_MANAGEMENT, IamActionCodes.UPDATE, "PROJECT_COMMENT_UPDATE");
    public static final IamPermissionAction PROJECT_MEETING_REPORT_VIEW = IamPermissionAction.of(
            IamPermissionCodes.COLLABORATION_MANAGEMENT, IamActionCodes.REPORT_VIEW, "PROJECT_MEETING_REPORT_VIEW");


    public static final IamPermissionAction GLOBAL_SEARCH_USE = IamPermissionAction.of(
            IamPermissionCodes.PRODUCTIVITY_MANAGEMENT, IamActionCodes.VIEW, "GLOBAL_SEARCH_USE");
    public static final IamPermissionAction SAVED_SEARCH_CREATE = IamPermissionAction.of(
            IamPermissionCodes.PRODUCTIVITY_MANAGEMENT, IamActionCodes.CREATE, "SAVED_SEARCH_CREATE");
    public static final IamPermissionAction SAVED_VIEW_CREATE = IamPermissionAction.of(
            IamPermissionCodes.PRODUCTIVITY_MANAGEMENT, IamActionCodes.CREATE, "SAVED_VIEW_CREATE");
    public static final IamPermissionAction FAVORITE_MANAGE = IamPermissionAction.of(
            IamPermissionCodes.PRODUCTIVITY_MANAGEMENT, IamActionCodes.MANAGE, "FAVORITE_MANAGE");
    public static final IamPermissionAction WORK_INBOX_VIEW = IamPermissionAction.of(
            IamPermissionCodes.PRODUCTIVITY_MANAGEMENT, IamActionCodes.VIEW, "WORK_INBOX_VIEW");
    public static final IamPermissionAction NAVIGATION_VIEW = IamPermissionAction.of(
            IamPermissionCodes.PRODUCTIVITY_MANAGEMENT, IamActionCodes.VIEW, "NAVIGATION_VIEW");
    public static final IamPermissionAction CUSTOM_FIELD_VIEW = IamPermissionAction.of(
            IamPermissionCodes.CONFIGURATION_MANAGEMENT, IamActionCodes.VIEW, "CUSTOM_FIELD_VIEW");
    public static final IamPermissionAction CUSTOM_FIELD_CREATE = IamPermissionAction.of(
            IamPermissionCodes.CONFIGURATION_MANAGEMENT, IamActionCodes.CREATE, "CUSTOM_FIELD_CREATE");
    public static final IamPermissionAction CUSTOM_FIELD_UPDATE = IamPermissionAction.of(
            IamPermissionCodes.CONFIGURATION_MANAGEMENT, IamActionCodes.UPDATE, "CUSTOM_FIELD_UPDATE");
    public static final IamPermissionAction CUSTOM_FORM_VIEW = IamPermissionAction.of(
            IamPermissionCodes.CONFIGURATION_MANAGEMENT, IamActionCodes.VIEW, "CUSTOM_FORM_VIEW");
    public static final IamPermissionAction GOVERNANCE_POLICY_VIEW = IamPermissionAction.of(
            IamPermissionCodes.OBJECT_GOVERNANCE_MANAGEMENT, IamActionCodes.VIEW, "GOVERNANCE_POLICY_VIEW");
    public static final IamPermissionAction GOVERNANCE_POLICY_UPDATE = IamPermissionAction.of(
            IamPermissionCodes.OBJECT_GOVERNANCE_MANAGEMENT, IamActionCodes.UPDATE, "GOVERNANCE_POLICY_UPDATE");
    public static final IamPermissionAction OBJECT_OWNERSHIP_VIEW = IamPermissionAction.of(
            IamPermissionCodes.OBJECT_GOVERNANCE_MANAGEMENT, IamActionCodes.VIEW, "OBJECT_OWNERSHIP_VIEW");
    public static final IamPermissionAction OBJECT_OWNERSHIP_ASSIGN = IamPermissionAction.of(
            IamPermissionCodes.OBJECT_GOVERNANCE_MANAGEMENT, IamActionCodes.ASSIGN, "OBJECT_OWNERSHIP_ASSIGN");
    public static final IamPermissionAction OBJECT_VERSION_VIEW = IamPermissionAction.of(
            IamPermissionCodes.OBJECT_GOVERNANCE_MANAGEMENT, IamActionCodes.VIEW, "OBJECT_VERSION_VIEW");
    public static final IamPermissionAction OBJECT_LOCK_CREATE = IamPermissionAction.of(
            IamPermissionCodes.OBJECT_GOVERNANCE_MANAGEMENT, IamActionCodes.CREATE, "OBJECT_LOCK_CREATE");
    public static final IamPermissionAction NOTIFICATION_PREFERENCE_UPDATE = IamPermissionAction.of(
            IamPermissionCodes.ADVANCED_NOTIFICATION_MANAGEMENT, IamActionCodes.PREFERENCE_UPDATE, "NOTIFICATION_PREFERENCE_UPDATE");
    public static final IamPermissionAction DIGEST_RULE_MANAGE = IamPermissionAction.of(
            IamPermissionCodes.ADVANCED_NOTIFICATION_MANAGEMENT, IamActionCodes.RULE_MANAGE, "DIGEST_RULE_MANAGE");
    public static final IamPermissionAction REMINDER_RULE_MANAGE = IamPermissionAction.of(
            IamPermissionCodes.ADVANCED_NOTIFICATION_MANAGEMENT, IamActionCodes.RULE_MANAGE, "REMINDER_RULE_MANAGE");
    public static final IamPermissionAction ALERT_RULE_MANAGE = IamPermissionAction.of(
            IamPermissionCodes.ADVANCED_NOTIFICATION_MANAGEMENT, IamActionCodes.RULE_MANAGE, "ALERT_RULE_MANAGE");
    public static final IamPermissionAction PROFITABILITY_PROFILE_VIEW = IamPermissionAction.of(
            IamPermissionCodes.PROFITABILITY_MANAGEMENT, IamActionCodes.VIEW, "PROFITABILITY_PROFILE_VIEW");
    public static final IamPermissionAction PROFITABILITY_PROFILE_UPDATE = IamPermissionAction.of(
            IamPermissionCodes.PROFITABILITY_MANAGEMENT, IamActionCodes.UPDATE, "PROFITABILITY_PROFILE_UPDATE");
    public static final IamPermissionAction PROFITABILITY_SUMMARY_VIEW = IamPermissionAction.of(
            IamPermissionCodes.PROFITABILITY_MANAGEMENT, IamActionCodes.VIEW_SUMMARY, "PROFITABILITY_SUMMARY_VIEW");

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
                DOCUMENT_TYPE_ARCHIVE,
                DOCUMENT_TYPE_MANAGE,
                DOCUMENT_TYPE_FIELD_VIEW,
                DOCUMENT_TYPE_FIELD_CREATE,
                DOCUMENT_TYPE_FIELD_UPDATE,
                DOCUMENT_TYPE_FIELD_ARCHIVE,
                DOCUMENT_TYPE_FIELD_MANAGE,
                KNOWLEDGE_CLASSIFICATION_VIEW,
                WORKSPACE_INVITE_MEMBER,
                WORKSPACE_MANAGE_INVITATION,
                WORKSPACE_MANAGE_JOIN_REQUEST,
                NOTIFICATION_VIEW_TEMPLATE,
                NOTIFICATION_MANAGE_TEMPLATE,
                NOTIFICATION_VIEW_RULE,
                NOTIFICATION_MANAGE_RULE,
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
                PHASE_DEFINITION_VIEW,
                PHASE_DEFINITION_CREATE,
                PHASE_DEFINITION_UPDATE,
                PHASE_DEFINITION_ARCHIVE,
                PHASE_DEFINITION_MANAGE,
                PROJECT_TEMPLATE_VIEW,
                PROJECT_TEMPLATE_CREATE,
                PROJECT_TEMPLATE_UPDATE,
                PROJECT_TEMPLATE_PUBLISH,
                PROJECT_TEMPLATE_ARCHIVE,
                PROJECT_TEMPLATE_APPLY,
                PROJECT_TEMPLATE_MANAGE,
                CAPACITY_CALENDAR_VIEW,
                CAPACITY_CALENDAR_CREATE,
                CAPACITY_CALENDAR_UPDATE,
                CAPACITY_CALENDAR_ARCHIVE,
                CAPACITY_CALENDAR_MANAGE,
                CAPACITY_PROFILE_VIEW,
                CAPACITY_PROFILE_CREATE,
                CAPACITY_PROFILE_UPDATE,
                CAPACITY_PROFILE_ARCHIVE,
                CAPACITY_PROFILE_MANAGE,
                PROJECT_ALLOCATION_VIEW,
                PROJECT_ALLOCATION_CREATE,
                PROJECT_ALLOCATION_UPDATE,
                PROJECT_ALLOCATION_ARCHIVE,
                PROJECT_ALLOCATION_MANAGE,
                CAPACITY_VIEW,
                CAPACITY_CALCULATE,
                COST_ROLE_VIEW,
                COST_ROLE_CREATE,
                COST_ROLE_UPDATE,
                COST_ROLE_ARCHIVE,
                COST_ROLE_MANAGE,
                RATE_CARD_VIEW,
                RATE_CARD_CREATE,
                RATE_CARD_UPDATE,
                RATE_CARD_PUBLISH,
                RATE_CARD_ARCHIVE,
                RATE_CARD_MANAGE,
                RATE_CARD_LINE_VIEW,
                RATE_CARD_LINE_CREATE,
                RATE_CARD_LINE_UPDATE,
                RATE_CARD_LINE_DELETE,
                INFLATION_POLICY_VIEW,
                INFLATION_POLICY_CREATE,
                INFLATION_POLICY_UPDATE,
                INFLATION_POLICY_ARCHIVE,
                INFLATION_POLICY_MANAGE,
                RATE_RESOLUTION_VIEW,
                RATE_RESOLUTION_PREVIEW,
                MEMBER_COST_ROLE_VIEW,
                MEMBER_COST_ROLE_ASSIGN,
                MEMBER_COST_ROLE_MANAGE,
                ESTIMATION_VIEW,
                ESTIMATION_RUN_CREATE,
                ESTIMATION_RUN_CANCEL,
                ESTIMATION_MARK_CURRENT,
                ESTIMATION_TASK_VIEW,
                ESTIMATION_WBS_VIEW,
                ESTIMATION_PHASE_VIEW,
                ESTIMATION_SUMMARY_VIEW,
                ESTIMATION_RATE_DETAIL_VIEW,
                PROJECT_FINANCE_VIEW,
                PROJECT_FINANCE_CREATE,
                PROJECT_FINANCE_UPDATE,
                PROJECT_FINANCE_RECALCULATE,
                PROJECT_FINANCE_APPROVE,
                PROJECT_FINANCE_MARK_CURRENT,
                PROJECT_FINANCE_ARCHIVE,
                PROJECT_FINANCE_MANAGE,
                PROJECT_FINANCE_COST_VIEW,
                PROJECT_FINANCE_COST_CREATE,
                PROJECT_FINANCE_COST_UPDATE,
                PROJECT_FINANCE_COST_ARCHIVE,
                PROJECT_FINANCE_REVENUE_VIEW,
                PROJECT_FINANCE_REVENUE_UPDATE,
                PROJECT_FINANCE_MARGIN_VIEW,
                QUOTE_VIEW,
                QUOTE_CREATE,
                QUOTE_UPDATE,
                QUOTE_ARCHIVE,
                QUOTE_MANAGE,
                QUOTE_VERSION_CREATE,
                QUOTE_VERSION_UPDATE,
                QUOTE_VERSION_ARCHIVE,
                QUOTE_VERSION_DUPLICATE,
                QUOTE_LINE_CREATE,
                QUOTE_LINE_UPDATE,
                QUOTE_LINE_DELETE,
                QUOTE_TERM_CREATE,
                QUOTE_TERM_UPDATE,
                QUOTE_TERM_DELETE,
                QUOTE_SOLVER_USE,
                QUOTE_RECALCULATE,
                QUOTE_SUBMIT,
                QUOTE_APPROVE,
                QUOTE_REJECT,
                QUOTE_SEND,
                QUOTE_MARK_ACCEPTED,
                QUOTE_MARK_CURRENT,
                QUOTE_MARGIN_VIEW,
                QUOTE_DISCOUNT_UPDATE,
                QUOTE_DISCOUNT_APPROVE,
                PROJECT_BASELINE_VIEW,
                PROJECT_BASELINE_CREATE,
                PROJECT_BASELINE_UPDATE,
                PROJECT_BASELINE_VALIDATE,
                PROJECT_BASELINE_APPROVE,
                PROJECT_BASELINE_MARK_CURRENT,
                PROJECT_BASELINE_ARCHIVE,
                PROJECT_BASELINE_COMPARE,
                CHANGE_REQUEST_VIEW,
                CHANGE_REQUEST_CREATE,
                CHANGE_REQUEST_UPDATE,
                CHANGE_REQUEST_SUBMIT,
                CHANGE_REQUEST_APPROVE,
                CHANGE_REQUEST_REJECT,
                CHANGE_REQUEST_CANCEL,
                CHANGE_REQUEST_APPLY,
                CHANGE_REQUEST_ARCHIVE,
                CHANGE_REQUEST_ITEM_VIEW,
                CHANGE_REQUEST_ITEM_CREATE,
                CHANGE_REQUEST_ITEM_UPDATE,
                CHANGE_REQUEST_ITEM_DELETE,
                CHANGE_IMPACT_VIEW,
                CHANGE_IMPACT_UPDATE,
                CHANGE_IMPACT_CALCULATE,
                CHANGE_ORDER_VIEW,
                CHANGE_ORDER_CREATE,
                CHANGE_ORDER_UPDATE,
                CHANGE_ORDER_APPROVE,
                CHANGE_ORDER_REJECT,
                CHANGE_ORDER_ARCHIVE,
                PROJECT_NOTIFICATION_VIEW,
                PROJECT_NOTIFICATION_SUBSCRIBE_SELF,
                PROJECT_NOTIFICATION_MANAGE_SUBSCRIBERS,
                PROJECT_NOTIFICATION_PREFERENCE_VIEW,
                PROJECT_NOTIFICATION_PREFERENCE_UPDATE,
                TASK_NOTIFICATION_VIEW,
                TASK_NOTIFICATION_SUBSCRIBE_SELF,
                TASK_NOTIFICATION_MANAGE_SUBSCRIBERS,
                TASK_NOTIFICATION_PREFERENCE_VIEW,
                TASK_NOTIFICATION_PREFERENCE_UPDATE,
                PROJECT_NOTIFICATION_RULE_VIEW,
                PROJECT_NOTIFICATION_RULE_MANAGE,
                PROJECT_REMINDER_RUN,
                PROJECT_NOTIFICATION_DELIVERY_VIEW,
                AI_PROJECT_PLANNING_VIEW,
                AI_PROJECT_PLANNING_RUN,
                AI_PROJECT_PLANNING_REVIEW,
                AI_PROJECT_PLANNING_ACCEPT,
                AI_PROJECT_PLANNING_REJECT,
                AI_PROJECT_PLANNING_APPLY,
                AI_PROJECT_PLANNING_ARCHIVE,
                AI_PROJECT_PLAN_DRAFT,
                AI_TASK_ESTIMATE_SUGGEST,
                AI_COST_ROLE_SUGGEST,
                AI_SCHEDULE_RISK_EXPLAIN,
                AI_FINANCE_INSIGHT,
                AI_QUOTE_DRAFT,
                AI_CHANGE_REQUEST_DRAFT,
                AI_SUGGESTION_APPLY_WBS,
                AI_SUGGESTION_APPLY_TASK,
                AI_SUGGESTION_APPLY_ESTIMATE,
                AI_SUGGESTION_APPLY_DEPENDENCY,
                AI_SUGGESTION_APPLY_QUOTE_TERM,
                AI_SUGGESTION_APPLY_CHANGE_REQUEST,
                REPORTING_DASHBOARD_VIEW,
                REPORTING_HEALTH_VIEW,
                REPORTING_KPI_VIEW,
                REPORTING_REPORT_VIEW,
                REPORTING_REPORT_RUN,
                REPORTING_REPORT_EXPORT,
                REPORTING_DEFINITION_VIEW,
                REPORTING_DEFINITION_MANAGE,
                SCOPE_VIEW,
                SCOPE_CREATE,
                SCOPE_UPDATE,
                SCOPE_APPROVE,
                SCOPE_ARCHIVE,
                DELIVERABLE_VIEW,
                DELIVERABLE_CREATE,
                DELIVERABLE_UPDATE,
                DELIVERABLE_SUBMIT_REVIEW,
                DELIVERABLE_ACCEPT,
                DELIVERABLE_REJECT,
                DELIVERABLE_REOPEN,
                DELIVERABLE_ARCHIVE,
                ACCEPTANCE_CRITERIA_WAIVE,
                RAID_VIEW,
                RAID_CREATE,
                RAID_UPDATE,
                RAID_ESCALATE,
                RAID_CONVERT,
                RAID_ARCHIVE,
                DECISION_VIEW,
                DECISION_CREATE,
                DECISION_UPDATE,
                DECISION_DECIDE,
                DECISION_ARCHIVE,
                QUALITY_VIEW,
                QUALITY_CREATE,
                QUALITY_UPDATE,
                QUALITY_APPROVE,
                TEST_VIEW,
                TEST_CREATE,
                TEST_UPDATE,
                TEST_EXECUTE,
                DEFECT_VIEW,
                DEFECT_CREATE,
                DEFECT_UPDATE,
                DEFECT_RESOLVE,
                RELEASE_VIEW,
                RELEASE_CREATE,
                RELEASE_UPDATE,
                RELEASE_APPROVE,
                DEPLOYMENT_VIEW,
                DEPLOYMENT_MANAGE,
                DOCUMENT_HUB_VIEW,
                DOCUMENT_HUB_CREATE,
                DOCUMENT_HUB_UPDATE,
                DOCUMENT_HUB_APPROVE,
                REQUIREMENT_VIEW,
                REQUIREMENT_CREATE,
                REQUIREMENT_UPDATE,
                REQUIREMENT_APPROVE,
                EXTERNAL_PARTY_VIEW,
                EXTERNAL_PARTY_CREATE,
                EXTERNAL_PARTY_UPDATE,
                CLIENT_PORTAL_VIEW,
                CLIENT_PORTAL_MANAGE,
                PROJECT_MEETING_VIEW,
                PROJECT_MEETING_CREATE,
                PROJECT_MEETING_UPDATE,
                PROJECT_MEETING_CANCEL,
                PROJECT_MEETING_ARCHIVE,
                PROJECT_MEETING_SERIES_MANAGE,
                PROJECT_MEETING_PARTICIPANT_MANAGE,
                PROJECT_MEETING_AGENDA_MANAGE,
                PROJECT_MEETING_MINUTES_VIEW,
                PROJECT_MEETING_MINUTES_UPDATE,
                PROJECT_MEETING_MINUTES_APPROVE,
                PROJECT_MEETING_MINUTES_GENERATE_DOCUMENT,
                PROJECT_MEETING_NOTE_UPDATE,
                PROJECT_MEETING_ACTION_UPDATE,
                PROJECT_MEETING_ACTION_COMPLETE,
                PROJECT_MEETING_ACTION_CREATE_TASK,
                PROJECT_MEETING_LINK_MANAGE,
                PROJECT_COMMENT_THREAD_VIEW,
                PROJECT_COMMENT_THREAD_CREATE,
                PROJECT_COMMENT_CREATE,
                PROJECT_COMMENT_UPDATE,
                PROJECT_MEETING_REPORT_VIEW,
                GLOBAL_SEARCH_USE,
                SAVED_SEARCH_CREATE,
                SAVED_VIEW_CREATE,
                FAVORITE_MANAGE,
                WORK_INBOX_VIEW,
                NAVIGATION_VIEW,
                CUSTOM_FIELD_VIEW,
                CUSTOM_FIELD_CREATE,
                CUSTOM_FIELD_UPDATE,
                CUSTOM_FORM_VIEW,
                GOVERNANCE_POLICY_VIEW,
                GOVERNANCE_POLICY_UPDATE,
                OBJECT_OWNERSHIP_VIEW,
                OBJECT_OWNERSHIP_ASSIGN,
                OBJECT_VERSION_VIEW,
                OBJECT_LOCK_CREATE,
                NOTIFICATION_PREFERENCE_UPDATE,
                DIGEST_RULE_MANAGE,
                REMINDER_RULE_MANAGE,
                ALERT_RULE_MANAGE,
                PROFITABILITY_PROFILE_VIEW,
                PROFITABILITY_PROFILE_UPDATE,
                PROFITABILITY_SUMMARY_VIEW);
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
                SYSTEM_GOVERNANCE_MANAGE_PHASE_DEFINITION,
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
                DOCUMENT_TYPE_ARCHIVE,
                DOCUMENT_TYPE_MANAGE,
                DOCUMENT_TYPE_FIELD_VIEW,
                DOCUMENT_TYPE_FIELD_CREATE,
                DOCUMENT_TYPE_FIELD_UPDATE,
                DOCUMENT_TYPE_FIELD_ARCHIVE,
                DOCUMENT_TYPE_FIELD_MANAGE,
                KNOWLEDGE_CLASSIFICATION_VIEW,
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
                PHASE_DEFINITION_VIEW,
                PHASE_DEFINITION_CREATE,
                PHASE_DEFINITION_UPDATE,
                PHASE_DEFINITION_ARCHIVE,
                PHASE_DEFINITION_MANAGE,
                PROJECT_TEMPLATE_VIEW,
                PROJECT_TEMPLATE_CREATE,
                PROJECT_TEMPLATE_UPDATE,
                PROJECT_TEMPLATE_PUBLISH,
                PROJECT_TEMPLATE_ARCHIVE,
                PROJECT_TEMPLATE_APPLY,
                PROJECT_TEMPLATE_MANAGE,
                NOTIFICATION_VIEW_TEMPLATE,
                NOTIFICATION_MANAGE_TEMPLATE,
                NOTIFICATION_VIEW_RULE,
                NOTIFICATION_MANAGE_RULE,
                SYSTEM_EVENT_REGISTRY_MANAGE,
                CAPACITY_CALENDAR_VIEW,
                CAPACITY_CALENDAR_CREATE,
                CAPACITY_CALENDAR_UPDATE,
                CAPACITY_CALENDAR_ARCHIVE,
                CAPACITY_CALENDAR_MANAGE,
                CAPACITY_PROFILE_VIEW,
                CAPACITY_PROFILE_CREATE,
                CAPACITY_PROFILE_UPDATE,
                CAPACITY_PROFILE_ARCHIVE,
                CAPACITY_PROFILE_MANAGE,
                PROJECT_ALLOCATION_VIEW,
                PROJECT_ALLOCATION_CREATE,
                PROJECT_ALLOCATION_UPDATE,
                PROJECT_ALLOCATION_ARCHIVE,
                PROJECT_ALLOCATION_MANAGE,
                CAPACITY_VIEW,
                CAPACITY_CALCULATE,
                COST_ROLE_VIEW,
                COST_ROLE_CREATE,
                COST_ROLE_UPDATE,
                COST_ROLE_ARCHIVE,
                COST_ROLE_MANAGE,
                RATE_CARD_VIEW,
                RATE_CARD_CREATE,
                RATE_CARD_UPDATE,
                RATE_CARD_PUBLISH,
                RATE_CARD_ARCHIVE,
                RATE_CARD_MANAGE,
                RATE_CARD_LINE_VIEW,
                RATE_CARD_LINE_CREATE,
                RATE_CARD_LINE_UPDATE,
                RATE_CARD_LINE_DELETE,
                INFLATION_POLICY_VIEW,
                INFLATION_POLICY_CREATE,
                INFLATION_POLICY_UPDATE,
                INFLATION_POLICY_ARCHIVE,
                INFLATION_POLICY_MANAGE,
                RATE_RESOLUTION_VIEW,
                RATE_RESOLUTION_PREVIEW,
                MEMBER_COST_ROLE_VIEW,
                MEMBER_COST_ROLE_ASSIGN,
                MEMBER_COST_ROLE_MANAGE,
                ESTIMATION_VIEW,
                ESTIMATION_RUN_CREATE,
                ESTIMATION_RUN_CANCEL,
                ESTIMATION_MARK_CURRENT,
                ESTIMATION_TASK_VIEW,
                ESTIMATION_WBS_VIEW,
                ESTIMATION_PHASE_VIEW,
                ESTIMATION_SUMMARY_VIEW,
                ESTIMATION_RATE_DETAIL_VIEW,
                PROJECT_FINANCE_VIEW,
                PROJECT_FINANCE_CREATE,
                PROJECT_FINANCE_UPDATE,
                PROJECT_FINANCE_RECALCULATE,
                PROJECT_FINANCE_APPROVE,
                PROJECT_FINANCE_MARK_CURRENT,
                PROJECT_FINANCE_ARCHIVE,
                PROJECT_FINANCE_MANAGE,
                PROJECT_FINANCE_COST_VIEW,
                PROJECT_FINANCE_COST_CREATE,
                PROJECT_FINANCE_COST_UPDATE,
                PROJECT_FINANCE_COST_ARCHIVE,
                PROJECT_FINANCE_REVENUE_VIEW,
                PROJECT_FINANCE_REVENUE_UPDATE,
                PROJECT_FINANCE_MARGIN_VIEW,
                QUOTE_VIEW,
                QUOTE_CREATE,
                QUOTE_UPDATE,
                QUOTE_ARCHIVE,
                QUOTE_MANAGE,
                QUOTE_VERSION_CREATE,
                QUOTE_VERSION_UPDATE,
                QUOTE_VERSION_ARCHIVE,
                QUOTE_VERSION_DUPLICATE,
                QUOTE_LINE_CREATE,
                QUOTE_LINE_UPDATE,
                QUOTE_LINE_DELETE,
                QUOTE_TERM_CREATE,
                QUOTE_TERM_UPDATE,
                QUOTE_TERM_DELETE,
                QUOTE_SOLVER_USE,
                QUOTE_RECALCULATE,
                QUOTE_SUBMIT,
                QUOTE_APPROVE,
                QUOTE_REJECT,
                QUOTE_SEND,
                QUOTE_MARK_ACCEPTED,
                QUOTE_MARK_CURRENT,
                QUOTE_MARGIN_VIEW,
                QUOTE_DISCOUNT_UPDATE,
                QUOTE_DISCOUNT_APPROVE,
                PROJECT_BASELINE_VIEW,
                PROJECT_BASELINE_CREATE,
                PROJECT_BASELINE_UPDATE,
                PROJECT_BASELINE_VALIDATE,
                PROJECT_BASELINE_APPROVE,
                PROJECT_BASELINE_MARK_CURRENT,
                PROJECT_BASELINE_ARCHIVE,
                PROJECT_BASELINE_COMPARE,
                CHANGE_REQUEST_VIEW,
                CHANGE_REQUEST_CREATE,
                CHANGE_REQUEST_UPDATE,
                CHANGE_REQUEST_SUBMIT,
                CHANGE_REQUEST_APPROVE,
                CHANGE_REQUEST_REJECT,
                CHANGE_REQUEST_CANCEL,
                CHANGE_REQUEST_APPLY,
                CHANGE_REQUEST_ARCHIVE,
                CHANGE_REQUEST_ITEM_VIEW,
                CHANGE_REQUEST_ITEM_CREATE,
                CHANGE_REQUEST_ITEM_UPDATE,
                CHANGE_REQUEST_ITEM_DELETE,
                CHANGE_IMPACT_VIEW,
                CHANGE_IMPACT_UPDATE,
                CHANGE_IMPACT_CALCULATE,
                CHANGE_ORDER_VIEW,
                CHANGE_ORDER_CREATE,
                CHANGE_ORDER_UPDATE,
                CHANGE_ORDER_APPROVE,
                CHANGE_ORDER_REJECT,
                CHANGE_ORDER_ARCHIVE,
                PROJECT_NOTIFICATION_VIEW,
                PROJECT_NOTIFICATION_SUBSCRIBE_SELF,
                PROJECT_NOTIFICATION_MANAGE_SUBSCRIBERS,
                PROJECT_NOTIFICATION_PREFERENCE_VIEW,
                PROJECT_NOTIFICATION_PREFERENCE_UPDATE,
                TASK_NOTIFICATION_VIEW,
                TASK_NOTIFICATION_SUBSCRIBE_SELF,
                TASK_NOTIFICATION_MANAGE_SUBSCRIBERS,
                TASK_NOTIFICATION_PREFERENCE_VIEW,
                TASK_NOTIFICATION_PREFERENCE_UPDATE,
                PROJECT_NOTIFICATION_RULE_VIEW,
                PROJECT_NOTIFICATION_RULE_MANAGE,
                PROJECT_REMINDER_RUN,
                PROJECT_NOTIFICATION_DELIVERY_VIEW,
                AI_PROJECT_PLANNING_VIEW,
                AI_PROJECT_PLANNING_RUN,
                AI_PROJECT_PLANNING_REVIEW,
                AI_PROJECT_PLANNING_ACCEPT,
                AI_PROJECT_PLANNING_REJECT,
                AI_PROJECT_PLANNING_APPLY,
                AI_PROJECT_PLANNING_ARCHIVE,
                AI_PROJECT_PLAN_DRAFT,
                AI_TASK_ESTIMATE_SUGGEST,
                AI_COST_ROLE_SUGGEST,
                AI_SCHEDULE_RISK_EXPLAIN,
                AI_FINANCE_INSIGHT,
                AI_QUOTE_DRAFT,
                AI_CHANGE_REQUEST_DRAFT,
                AI_SUGGESTION_APPLY_WBS,
                AI_SUGGESTION_APPLY_TASK,
                AI_SUGGESTION_APPLY_ESTIMATE,
                AI_SUGGESTION_APPLY_DEPENDENCY,
                AI_SUGGESTION_APPLY_QUOTE_TERM,
                AI_SUGGESTION_APPLY_CHANGE_REQUEST,
                REPORTING_DASHBOARD_VIEW,
                REPORTING_HEALTH_VIEW,
                REPORTING_KPI_VIEW,
                REPORTING_REPORT_VIEW,
                REPORTING_REPORT_RUN,
                REPORTING_REPORT_EXPORT,
                REPORTING_DEFINITION_VIEW,
                REPORTING_DEFINITION_MANAGE,
                SCOPE_VIEW,
                SCOPE_CREATE,
                SCOPE_UPDATE,
                SCOPE_APPROVE,
                SCOPE_ARCHIVE,
                DELIVERABLE_VIEW,
                DELIVERABLE_CREATE,
                DELIVERABLE_UPDATE,
                DELIVERABLE_SUBMIT_REVIEW,
                DELIVERABLE_ACCEPT,
                DELIVERABLE_REJECT,
                DELIVERABLE_REOPEN,
                DELIVERABLE_ARCHIVE,
                ACCEPTANCE_CRITERIA_WAIVE,
                RAID_VIEW,
                RAID_CREATE,
                RAID_UPDATE,
                RAID_ESCALATE,
                RAID_CONVERT,
                RAID_ARCHIVE,
                DECISION_VIEW,
                DECISION_CREATE,
                DECISION_UPDATE,
                DECISION_DECIDE,
                DECISION_ARCHIVE,
                QUALITY_VIEW,
                QUALITY_CREATE,
                QUALITY_UPDATE,
                QUALITY_APPROVE,
                TEST_VIEW,
                TEST_CREATE,
                TEST_UPDATE,
                TEST_EXECUTE,
                DEFECT_VIEW,
                DEFECT_CREATE,
                DEFECT_UPDATE,
                DEFECT_RESOLVE,
                RELEASE_VIEW,
                RELEASE_CREATE,
                RELEASE_UPDATE,
                RELEASE_APPROVE,
                DEPLOYMENT_VIEW,
                DEPLOYMENT_MANAGE,
                DOCUMENT_HUB_VIEW,
                DOCUMENT_HUB_CREATE,
                DOCUMENT_HUB_UPDATE,
                DOCUMENT_HUB_APPROVE,
                REQUIREMENT_VIEW,
                REQUIREMENT_CREATE,
                REQUIREMENT_UPDATE,
                REQUIREMENT_APPROVE,
                EXTERNAL_PARTY_VIEW,
                EXTERNAL_PARTY_CREATE,
                EXTERNAL_PARTY_UPDATE,
                CLIENT_PORTAL_VIEW,
                CLIENT_PORTAL_MANAGE,
                PROJECT_MEETING_VIEW,
                PROJECT_MEETING_CREATE,
                PROJECT_MEETING_UPDATE,
                PROJECT_MEETING_CANCEL,
                PROJECT_MEETING_ARCHIVE,
                PROJECT_MEETING_SERIES_MANAGE,
                PROJECT_MEETING_PARTICIPANT_MANAGE,
                PROJECT_MEETING_AGENDA_MANAGE,
                PROJECT_MEETING_MINUTES_VIEW,
                PROJECT_MEETING_MINUTES_UPDATE,
                PROJECT_MEETING_MINUTES_APPROVE,
                PROJECT_MEETING_MINUTES_GENERATE_DOCUMENT,
                PROJECT_MEETING_NOTE_UPDATE,
                PROJECT_MEETING_ACTION_UPDATE,
                PROJECT_MEETING_ACTION_COMPLETE,
                PROJECT_MEETING_ACTION_CREATE_TASK,
                PROJECT_MEETING_LINK_MANAGE,
                PROJECT_COMMENT_THREAD_VIEW,
                PROJECT_COMMENT_THREAD_CREATE,
                PROJECT_COMMENT_CREATE,
                PROJECT_COMMENT_UPDATE,
                PROJECT_MEETING_REPORT_VIEW,
                GLOBAL_SEARCH_USE,
                SAVED_SEARCH_CREATE,
                SAVED_VIEW_CREATE,
                FAVORITE_MANAGE,
                WORK_INBOX_VIEW,
                NAVIGATION_VIEW,
                CUSTOM_FIELD_VIEW,
                CUSTOM_FIELD_CREATE,
                CUSTOM_FIELD_UPDATE,
                CUSTOM_FORM_VIEW,
                GOVERNANCE_POLICY_VIEW,
                GOVERNANCE_POLICY_UPDATE,
                OBJECT_OWNERSHIP_VIEW,
                OBJECT_OWNERSHIP_ASSIGN,
                OBJECT_VERSION_VIEW,
                OBJECT_LOCK_CREATE,
                NOTIFICATION_PREFERENCE_UPDATE,
                DIGEST_RULE_MANAGE,
                REMINDER_RULE_MANAGE,
                ALERT_RULE_MANAGE,
                PROFITABILITY_PROFILE_VIEW,
                PROFITABILITY_PROFILE_UPDATE,
                PROFITABILITY_SUMMARY_VIEW);
    }
}
