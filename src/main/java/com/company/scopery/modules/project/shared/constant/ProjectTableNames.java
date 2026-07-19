package com.company.scopery.modules.project.shared.constant;

public final class ProjectTableNames {

    private ProjectTableNames() {}

    public static final String PHASE_DEFINITION = "project_phase_definition";
    public static final String PROJECT          = "project_project";
    public static final String PROJECT_PHASE    = "project_project_phase";
    public static final String WBS_NODE         = "project_wbs_node";
    public static final String TASK             = "project_task";
    public static final String TASK_DEPENDENCY  = "project_task_dependency";
    public static final String SCHEDULE_RUN = "project_schedule_run";
    public static final String TASK_SCHEDULE = "project_task_schedule";
    public static final String SCHEDULED_DAILY_WORK = "project_scheduled_daily_work";
    public static final String SCHEDULING_ISSUE = "project_scheduling_issue";

    // Phase 14 — Gantt
    public static final String PROJECT_MILESTONE = "project_milestone";
    public static final String TASK_SCHEDULE_OVERRIDE = "project_task_schedule_override";

    // Phase 11 — templates
    public static final String PROJECT_TEMPLATE                = "project_template";
    public static final String PROJECT_TEMPLATE_VERSION        = "project_template_version";
    public static final String PROJECT_TEMPLATE_PHASE          = "project_template_phase";
    public static final String PROJECT_TEMPLATE_WBS_NODE       = "project_template_wbs_node";
    public static final String PROJECT_TEMPLATE_TASK           = "project_template_task";
    public static final String PROJECT_TEMPLATE_TASK_DEPENDENCY = "project_template_task_dependency";
}
