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
}
