package com.company.scopery.modules.project.shared.constant;

public final class ProjectActivityActions {

    private ProjectActivityActions() {}

    // Project
    public static final String CREATE_PROJECT   = "CREATE_PROJECT";
    public static final String UPDATE_PROJECT   = "UPDATE_PROJECT";
    public static final String ACTIVATE_PROJECT = "ACTIVATE_PROJECT";
    public static final String HOLD_PROJECT     = "HOLD_PROJECT";
    public static final String COMPLETE_PROJECT = "COMPLETE_PROJECT";
    public static final String ARCHIVE_PROJECT  = "ARCHIVE_PROJECT";

    // Phase Definition
    public static final String CREATE_SYSTEM_PHASE_DEFINITION    = "CREATE_SYSTEM_PHASE_DEFINITION";
    public static final String CREATE_WORKSPACE_PHASE_DEFINITION = "CREATE_WORKSPACE_PHASE_DEFINITION";
    public static final String UPDATE_PHASE_DEFINITION           = "UPDATE_PHASE_DEFINITION";
    public static final String ARCHIVE_PHASE_DEFINITION          = "ARCHIVE_PHASE_DEFINITION";

    // Project Phase
    public static final String CREATE_PROJECT_PHASE                  = "CREATE_PROJECT_PHASE";
    public static final String CREATE_PROJECT_PHASE_FROM_DEFINITION  = "CREATE_PROJECT_PHASE_FROM_DEFINITION";
    public static final String UPDATE_PROJECT_PHASE                  = "UPDATE_PROJECT_PHASE";
    public static final String ACTIVATE_PROJECT_PHASE                = "ACTIVATE_PROJECT_PHASE";
    public static final String COMPLETE_PROJECT_PHASE                = "COMPLETE_PROJECT_PHASE";
    public static final String ARCHIVE_PROJECT_PHASE                 = "ARCHIVE_PROJECT_PHASE";

    // WBS Node
    public static final String CREATE_WBS_NODE  = "CREATE_WBS_NODE";
    public static final String UPDATE_WBS_NODE  = "UPDATE_WBS_NODE";
    public static final String MOVE_WBS_NODE    = "MOVE_WBS_NODE";
    public static final String ARCHIVE_WBS_NODE = "ARCHIVE_WBS_NODE";

    // Task
    public static final String CREATE_TASK   = "CREATE_TASK";
    public static final String UPDATE_TASK   = "UPDATE_TASK";
    public static final String START_TASK    = "START_TASK";
    public static final String BLOCK_TASK    = "BLOCK_TASK";
    public static final String COMPLETE_TASK = "COMPLETE_TASK";
    public static final String CANCEL_TASK   = "CANCEL_TASK";
    public static final String ARCHIVE_TASK  = "ARCHIVE_TASK";

    // Task Dependency
    public static final String CREATE_TASK_DEPENDENCY = "CREATE_TASK_DEPENDENCY";
    public static final String REMOVE_TASK_DEPENDENCY = "REMOVE_TASK_DEPENDENCY";
}
