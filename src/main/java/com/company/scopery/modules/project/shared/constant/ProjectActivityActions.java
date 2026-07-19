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
    public static final String CREATE_SYSTEM_PHASE_DEFINITION       = "CREATE_SYSTEM_PHASE_DEFINITION";
    public static final String CREATE_WORKSPACE_PHASE_DEFINITION    = "CREATE_WORKSPACE_PHASE_DEFINITION";
    public static final String CREATE_ORGANIZATION_PHASE_DEFINITION = "CREATE_ORGANIZATION_PHASE_DEFINITION";
    public static final String UPDATE_PHASE_DEFINITION              = "UPDATE_PHASE_DEFINITION";
    public static final String ACTIVATE_PHASE_DEFINITION            = "ACTIVATE_PHASE_DEFINITION";
    public static final String DEACTIVATE_PHASE_DEFINITION          = "DEACTIVATE_PHASE_DEFINITION";
    public static final String ARCHIVE_PHASE_DEFINITION             = "ARCHIVE_PHASE_DEFINITION";
    /** Spec §16.1 activity name alias for phase definition create. */
    public static final String PHASE_DEFINITION_CREATED             = "PHASE_DEFINITION_CREATED";
    public static final String PHASE_DEFINITION_UPDATED             = "PHASE_DEFINITION_UPDATED";
    public static final String PHASE_DEFINITION_ARCHIVED            = "PHASE_DEFINITION_ARCHIVED";

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

    public static final String SCHEDULE_RUN_CREATED = "SCHEDULE_RUN_CREATED";
    public static final String SCHEDULE_RUN_COMPLETED = "SCHEDULE_RUN_COMPLETED";
    public static final String SCHEDULE_RUN_FAILED = "SCHEDULE_RUN_FAILED";
    public static final String TASK_SCHEDULE_AT_RISK = "TASK_SCHEDULE_AT_RISK";
    public static final String TASK_SCHEDULE_UNSCHEDULED = "TASK_SCHEDULE_UNSCHEDULED";

    // Phase 14 — Gantt (spec §20.1)
    public static final String GANTT_RECALCULATED = "GANTT_RECALCULATED";
    public static final String GANTT_TASK_MOVED = "GANTT_TASK_MOVED";
    public static final String GANTT_TASK_RESIZED = "GANTT_TASK_RESIZED";
    public static final String GANTT_TASK_OVERRIDE_CLEARED = "GANTT_TASK_OVERRIDE_CLEARED";
    public static final String GANTT_DEPENDENCY_CREATED = "GANTT_DEPENDENCY_CREATED";
    public static final String GANTT_DEPENDENCY_REMOVED = "GANTT_DEPENDENCY_REMOVED";
    public static final String PROJECT_MILESTONE_CREATED = "PROJECT_MILESTONE_CREATED";
    public static final String PROJECT_MILESTONE_UPDATED = "PROJECT_MILESTONE_UPDATED";
    public static final String PROJECT_MILESTONE_ACHIEVED = "PROJECT_MILESTONE_ACHIEVED";
    public static final String PROJECT_MILESTONE_ARCHIVED = "PROJECT_MILESTONE_ARCHIVED";

    // Phase 11 — templates (spec §16.1)
    public static final String PROJECT_TEMPLATE_CREATED                 = "PROJECT_TEMPLATE_CREATED";
    public static final String PROJECT_TEMPLATE_UPDATED                 = "PROJECT_TEMPLATE_UPDATED";
    public static final String PROJECT_TEMPLATE_ARCHIVED                = "PROJECT_TEMPLATE_ARCHIVED";
    public static final String PROJECT_TEMPLATE_VERSION_CREATED         = "PROJECT_TEMPLATE_VERSION_CREATED";
    public static final String PROJECT_TEMPLATE_VERSION_PUBLISHED       = "PROJECT_TEMPLATE_VERSION_PUBLISHED";
    public static final String PROJECT_TEMPLATE_PHASE_CREATED           = "PROJECT_TEMPLATE_PHASE_CREATED";
    public static final String PROJECT_TEMPLATE_WBS_NODE_CREATED        = "PROJECT_TEMPLATE_WBS_NODE_CREATED";
    public static final String PROJECT_TEMPLATE_TASK_CREATED            = "PROJECT_TEMPLATE_TASK_CREATED";
    public static final String PROJECT_TEMPLATE_TASK_DEPENDENCY_CREATED = "PROJECT_TEMPLATE_TASK_DEPENDENCY_CREATED";
    public static final String PROJECT_TEMPLATE_APPLIED                 = "PROJECT_TEMPLATE_APPLIED";

    // Additional lifecycle activity actions used by Phase 11 actions
    public static final String ACTIVATE_PROJECT_TEMPLATE               = "ACTIVATE_PROJECT_TEMPLATE";
    public static final String DEACTIVATE_PROJECT_TEMPLATE             = "DEACTIVATE_PROJECT_TEMPLATE";
    public static final String UPDATE_PROJECT_TEMPLATE_VERSION         = "UPDATE_PROJECT_TEMPLATE_VERSION";
    public static final String ARCHIVE_PROJECT_TEMPLATE_VERSION        = "ARCHIVE_PROJECT_TEMPLATE_VERSION";
    public static final String DUPLICATE_PROJECT_TEMPLATE_VERSION      = "DUPLICATE_PROJECT_TEMPLATE_VERSION";
    public static final String UPDATE_PROJECT_TEMPLATE_PHASE           = "UPDATE_PROJECT_TEMPLATE_PHASE";
    public static final String DELETE_PROJECT_TEMPLATE_PHASE           = "DELETE_PROJECT_TEMPLATE_PHASE";
    public static final String REORDER_PROJECT_TEMPLATE_PHASES         = "REORDER_PROJECT_TEMPLATE_PHASES";
    public static final String UPDATE_PROJECT_TEMPLATE_WBS_NODE        = "UPDATE_PROJECT_TEMPLATE_WBS_NODE";
    public static final String MOVE_PROJECT_TEMPLATE_WBS_NODE          = "MOVE_PROJECT_TEMPLATE_WBS_NODE";
    public static final String DELETE_PROJECT_TEMPLATE_WBS_NODE        = "DELETE_PROJECT_TEMPLATE_WBS_NODE";
    public static final String UPDATE_PROJECT_TEMPLATE_TASK            = "UPDATE_PROJECT_TEMPLATE_TASK";
    public static final String DELETE_PROJECT_TEMPLATE_TASK            = "DELETE_PROJECT_TEMPLATE_TASK";
    public static final String REMOVE_PROJECT_TEMPLATE_TASK_DEPENDENCY = "REMOVE_PROJECT_TEMPLATE_TASK_DEPENDENCY";
}
