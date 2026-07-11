package com.company.scopery.modules.workspace.shared.constant;

public final class WorkspaceActivityActions {

    private WorkspaceActivityActions() {}

    // Organization actions
    public static final String CREATE_ORGANIZATION   = "CREATE_ORGANIZATION";
    public static final String UPDATE_ORGANIZATION   = "UPDATE_ORGANIZATION";
    public static final String ACTIVATE_ORGANIZATION = "ACTIVATE_ORGANIZATION";
    public static final String ARCHIVE_ORGANIZATION  = "ARCHIVE_ORGANIZATION";

    // Workspace actions
    public static final String CREATE_WORKSPACE   = "CREATE_WORKSPACE";
    public static final String UPDATE_WORKSPACE   = "UPDATE_WORKSPACE";
    public static final String ACTIVATE_WORKSPACE = "ACTIVATE_WORKSPACE";
    public static final String ARCHIVE_WORKSPACE  = "ARCHIVE_WORKSPACE";

    // Workspace member actions
    public static final String ADD_WORKSPACE_MEMBER        = "ADD_WORKSPACE_MEMBER";
    public static final String ACTIVATE_WORKSPACE_MEMBER   = "ACTIVATE_WORKSPACE_MEMBER";
    public static final String DEACTIVATE_WORKSPACE_MEMBER = "DEACTIVATE_WORKSPACE_MEMBER";

    // Team actions
    public static final String CREATE_TEAM   = "CREATE_TEAM";
    public static final String UPDATE_TEAM   = "UPDATE_TEAM";
    public static final String ACTIVATE_TEAM = "ACTIVATE_TEAM";
    public static final String ARCHIVE_TEAM  = "ARCHIVE_TEAM";

    // Team member actions
    public static final String ADD_TEAM_MEMBER    = "ADD_TEAM_MEMBER";
    public static final String REMOVE_TEAM_MEMBER = "REMOVE_TEAM_MEMBER";

    // Invitation actions
    public static final String CREATE_INVITATION = "CREATE_INVITATION";
    public static final String ACCEPT_INVITATION = "ACCEPT_INVITATION";
    public static final String REVOKE_INVITATION = "REVOKE_INVITATION";

    // Join request actions
    public static final String CREATE_JOIN_REQUEST  = "CREATE_JOIN_REQUEST";
    public static final String APPROVE_JOIN_REQUEST = "APPROVE_JOIN_REQUEST";
    public static final String REJECT_JOIN_REQUEST  = "REJECT_JOIN_REQUEST";
    public static final String CANCEL_JOIN_REQUEST  = "CANCEL_JOIN_REQUEST";

    // Context / onboarding actions
    public static final String SWITCH_WORKSPACE            = "SWITCH_WORKSPACE";
    public static final String COMPLETE_ONBOARDING         = "COMPLETE_ONBOARDING";
    public static final String RESET_ONBOARDING_CHOICE     = "RESET_ONBOARDING_CHOICE";

    // Org member actions
    public static final String ADD_ORG_MEMBER        = "ADD_ORG_MEMBER";
    public static final String ACTIVATE_ORG_MEMBER   = "ACTIVATE_ORG_MEMBER";
    public static final String SUSPEND_ORG_MEMBER    = "SUSPEND_ORG_MEMBER";
    public static final String REMOVE_ORG_MEMBER     = "REMOVE_ORG_MEMBER";

    // Org invitation actions
    public static final String CREATE_ORG_INVITATION = "CREATE_ORG_INVITATION";
    public static final String ACCEPT_ORG_INVITATION = "ACCEPT_ORG_INVITATION";
    public static final String CANCEL_ORG_INVITATION = "CANCEL_ORG_INVITATION";

    // Org team actions
    public static final String CREATE_ORG_TEAM   = "CREATE_ORG_TEAM";
    public static final String UPDATE_ORG_TEAM   = "UPDATE_ORG_TEAM";
    public static final String ACTIVATE_ORG_TEAM = "ACTIVATE_ORG_TEAM";
    public static final String ARCHIVE_ORG_TEAM  = "ARCHIVE_ORG_TEAM";

    // Org team member actions
    public static final String ADD_ORG_TEAM_MEMBER    = "ADD_ORG_TEAM_MEMBER";
    public static final String REMOVE_ORG_TEAM_MEMBER = "REMOVE_ORG_TEAM_MEMBER";

    // Org team workspace assignment actions
    public static final String ASSIGN_ORG_TEAM_TO_WORKSPACE  = "ASSIGN_ORG_TEAM_TO_WORKSPACE";
    public static final String REVOKE_ORG_TEAM_WS_ASSIGNMENT = "REVOKE_ORG_TEAM_WS_ASSIGNMENT";
}
